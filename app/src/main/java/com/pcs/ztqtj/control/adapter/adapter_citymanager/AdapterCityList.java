package com.pcs.ztqtj.control.adapter.adapter_citymanager;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib.lib_pcs_v3.model.image.ImageFetcher;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCity;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityMain;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalLocationSet;
import com.pcs.lib_ztqfj_v2.model.pack.net.week.PackMainWeekWeatherDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.week.WeekWeatherInfo;
import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.utils.TextUtil;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.util.OkHttpUtil;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 管理城市列表
 */
public class AdapterCityList extends BaseAdapter {

    private Activity mActivity;
    private List<PackLocalCity> data;
    public Boolean showDeleteBtn = false;
    private CityListDeleteBtnClick btnClickListener;
    private ImageFetcher mImageFetcher;
//    //亲情城市
//    private PackWeekWeatherFamilyUp mPackFamilyUp = new PackWeekWeatherFamilyUp();
//

    public AdapterCityList(Activity mActivity, List<PackLocalCity> data, CityListDeleteBtnClick btnClickListener, ImageFetcher imageFetcher) {
        this.mActivity = mActivity;
        this.data = data;
        this.btnClickListener = btnClickListener;
        this.mImageFetcher = imageFetcher;
    }

//    public void setData(List<PackLocalCity> data) {
//        this.data = data;
//    }

    public void showDeleteButton(boolean showDeleteBtn) {
        this.showDeleteBtn = showDeleteBtn;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        Holder holder;
        if (view == null) {
            holder = new Holder();
            view = LayoutInflater.from(mActivity).inflate( R.layout.item_fra_citylist_list, null);
            holder.defountCityIcon = (ImageView) view.findViewById(R.id.defaulticon);
            holder.locationIcon = (ImageView) view .findViewById(R.id.img_location);
            holder.weatherCityIcon = (ImageView) view.findViewById(R.id.weathericon);
            holder.cityName = (TextView) view.findViewById(R.id.cityname);
            holder.weatherTemperature = (TextView) view.findViewById(R.id.weathertemperaturenum);
            holder.deletecity = (ImageView) view .findViewById(R.id.deletecity);
            holder.progressbar = (ProgressBar) view.findViewById(R.id.progressbar);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }

        // 定位设置
        PackLocalLocationSet packSet = (PackLocalLocationSet) PcsDataManager.getInstance().getLocalPack(PackLocalLocationSet.KEY);
        // 首页城市
        PackLocalCityMain cityMain = ZtqCityDB.getInstance().getCityMain();
        // 行城市
        PackLocalCity cityRow = data.get(position);
        // 定位城市
//        PackLocalCityLocation packLocation = ZtqLocationTool.getInstance().getLocationCity();

        if (position == 0 && packSet.isAutoLocation) {
            // 定位城市
            holder.locationIcon.setVisibility(View.VISIBLE);
            holder.defountCityIcon.setVisibility(View.GONE);
        } else if (cityRow.ID.equals(cityMain.ID)) {
            // 手选城市
            holder.locationIcon.setVisibility(View.GONE);
            holder.defountCityIcon.setVisibility(View.VISIBLE);
        } else {
            holder.locationIcon.setVisibility(View.GONE);
            holder.defountCityIcon.setVisibility(View.GONE);
        }
        holder.cityName.setText(cityRow.NAME);

        // 是否显示删除按钮
        if (showDeleteBtn) {
            if (cityRow.ID.equals(cityMain.ID)) {
                // 默认城市不允许删除
                holder.deletecity.setVisibility(View.GONE);
            } else if (position == 0 && packSet.isAutoLocation) {
                // 定位城市不删除
                holder.deletecity.setVisibility(View.GONE);
            } else {
                holder.deletecity.setVisibility(View.VISIBLE);
            }
        } else {
            holder.deletecity.setVisibility(View.GONE);
        }
        holder.deletecity.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                btnClickListener.itemOnclick(position);
            }
        });

        okHttpWeekData(cityRow.ID, holder.weatherCityIcon, holder.weatherTemperature);
        return view;
    }

    private class Holder {
        public ImageView defountCityIcon;
        public ImageView locationIcon;
        public ImageView weatherCityIcon;
        public TextView cityName;
        public TextView weatherTemperature;
        public ImageView deletecity;
        public ProgressBar progressbar;
    }

    public interface CityListDeleteBtnClick {
        void itemOnclick(int item);
    }

    /**
     * 获取一周天气
     */
    private void okHttpWeekData(final String stationId, final ImageView weatherCityIcon, final TextView weatherTemperature) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param  = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    JSONObject info = new JSONObject();
                    info.put("stationId", stationId);
                    param.put("paramInfo", info);
                    String json = param.toString();
                    Log.e("week_data", json);
                    final String url = CONST.BASE_URL+"week_data";
                    Log.e("week_data", url);
                    RequestBody body = FormBody.create(MediaType.parse("application/json; charset=utf-8"), json);
                    OkHttpUtil.enqueue(new Request.Builder().post(body).url(url).build(), new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        }
                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            if (!response.isSuccessful()) {
                                return;
                            }
                            final String result = response.body().string();
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.e("week_data", result);
                                    if (!TextUtil.isEmpty(result)) {
                                        try {
                                            JSONObject obj = new JSONObject(result);
                                            if (!obj.isNull("b")) {
                                                JSONObject bobj = obj.getJSONObject("b");
                                                if (!bobj.isNull("p_new_week")) {
                                                    JSONObject p_new_weekobj = bobj.getJSONObject("p_new_week");
                                                    if (!TextUtil.isEmpty(p_new_weekobj.toString())) {
                                                        PackMainWeekWeatherDown pcsDownPack = new PackMainWeekWeatherDown();
                                                        pcsDownPack.fillData(p_new_weekobj.toString());
                                                        WeekWeatherInfo info = pcsDownPack.getToday();
                                                        if (info != null) {
                                                            String lowt_hight = "";
                                                            lowt_hight = info.higt + "/"+ info.lowt + "°C";
                                                            if ("".equals(lowt_hight)) {
                                                                weatherCityIcon.setVisibility(View.GONE);
                                                                weatherTemperature.setVisibility(View.GONE);
                                                            } else {
                                                                weatherCityIcon.setVisibility(View.VISIBLE);
                                                                weatherTemperature.setVisibility(View.VISIBLE);
                                                                weatherTemperature.setText(lowt_hight);
                                                            }
                                                            BitmapDrawable bitmap = mImageFetcher.getImageCache().getBitmapFromAssets(pcsDownPack.getIconPath(pcsDownPack.getTodayIndex()));
                                                            weatherCityIcon.setImageDrawable(bitmap);
                                                        } else {
                                                            weatherTemperature.setText("");
                                                            weatherCityIcon.setVisibility(View.GONE);
                                                            weatherTemperature.setVisibility(View.GONE);
                                                        }
                                                    }
                                                }
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            });
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
