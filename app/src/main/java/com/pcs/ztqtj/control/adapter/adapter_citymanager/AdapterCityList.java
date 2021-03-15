package com.pcs.ztqtj.control.adapter.adapter_citymanager;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib.lib_pcs_v3.model.image.ImageFetcher;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCity;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityMain;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalLocationSet;
import com.pcs.lib_ztqfj_v2.model.pack.net.week.PackMainWeekWeatherDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.week.PackMainWeekWeatherUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.week.WeekWeatherInfo;

import java.util.List;

/**
 * @author Z 城市列表适配器
 */
public class AdapterCityList extends BaseAdapter {
    private Context context;
    private List<PackLocalCity> data;
    public Boolean showDeleteBtn = false;
    private CityListDeleteBtnClick btnClickListener;
    private ImageFetcher mImageFetcher;
//    //亲情城市
//    private PackWeekWeatherFamilyUp mPackFamilyUp = new PackWeekWeatherFamilyUp();
//

    public AdapterCityList(Context context, List<PackLocalCity> data,
                           CityListDeleteBtnClick btnClickListener, ImageFetcher imageFetcher) {
        this.context = context;
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
            view = LayoutInflater.from(context).inflate( R.layout.item_fra_citylist_list, null);
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

        ///显示图标-------------------------------------
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
//        -----------------------------------
        holder.cityName.setText(cityRow.NAME);
        //省内城市
        PackMainWeekWeatherUp mPackWeekUp = new PackMainWeekWeatherUp();
        mPackWeekUp.setCity(cityRow);
        PackMainWeekWeatherDown pcsDownPack = (PackMainWeekWeatherDown) PcsDataManager.getInstance().getNetPack(mPackWeekUp.getName());
        if (pcsDownPack == null) {
            if (position == 0 && packSet.isAutoLocation) {
                // 定位城市无数据
                holder.weatherTemperature.setText("");
//                holder.progressbar.setVisibility(View.GONE);
                holder.weatherCityIcon.setVisibility(View.GONE);
                holder.weatherTemperature.setVisibility(View.GONE);
            } else {
                // 手选城市无数据
                holder.weatherTemperature.setText("");
//                holder.progressbar.setVisibility(View.VISIBLE);
                holder.weatherCityIcon.setVisibility(View.GONE);
                holder.weatherTemperature.setVisibility(View.GONE);
            }
        } else {
//            if (TextUtils.isEmpty(pcsDownPack.sys_time)) {
//                    try {
//                        holder.weatherCityIcon.setImageBitmap(null);
//                        holder.weatherTemperature.setText("");
//                        holder.progressbar.setVisibility(View.VISIBLE);
//                        holder.weatherCityIcon.setVisibility(View.GONE);
//                        holder.weatherTemperature.setVisibility(View.GONE);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//            } else {
//                SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//                String str = sf.format(new Date(pcsDownPack.sys_time_l));
                WeekWeatherInfo info = pcsDownPack.getToday();
                if (info != null) {
                    String lowt_hight = "";
                    lowt_hight = info.higt + "/"+ info.lowt + "°C";
                    if ("".equals(lowt_hight)) {
//                        holder.progressbar.setVisibility(View.VISIBLE);
                        holder.weatherCityIcon.setVisibility(View.GONE);
                        holder.weatherTemperature.setVisibility(View.GONE);
                    } else {
//                        holder.progressbar.setVisibility(View.GONE);
                        holder.weatherCityIcon.setVisibility(View.VISIBLE);
                        holder.weatherTemperature.setVisibility(View.VISIBLE);
                        holder.weatherTemperature.setText(lowt_hight);
                    }
                    BitmapDrawable bitmap = mImageFetcher.getImageCache().getBitmapFromAssets(pcsDownPack.getIconPath(pcsDownPack.getTodayIndex()));
                    holder.weatherCityIcon.setImageDrawable(bitmap);
                } else {
                    holder.weatherTemperature.setText("");
//                    holder.progressbar.setVisibility(View.VISIBLE);
                    holder.weatherCityIcon.setVisibility(View.GONE);
                    holder.weatherTemperature.setVisibility(View.GONE);
                }
            }
//        }
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
}
