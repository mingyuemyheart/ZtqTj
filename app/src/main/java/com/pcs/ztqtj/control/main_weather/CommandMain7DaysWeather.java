package com.pcs.ztqtj.control.main_weather;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.pcs.lib.lib_pcs_v3.control.tool.Util;
import com.pcs.lib.lib_pcs_v3.model.image.ImageFetcher;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCity;
import com.pcs.lib_ztqfj_v2.model.pack.net.week.PackMainWeekWeatherDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.week.WeekWeatherInfo;
import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.Adapter7DaysGridView;
import com.pcs.ztqtj.control.inter.InterfaceShowBg;
import com.pcs.ztqtj.control.tool.utils.TextUtil;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.util.OkHttpUtil;
import com.pcs.ztqtj.view.activity.ActivityMain;
import com.pcs.ztqtj.view.myview.TemperatureView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by tyaathome on 2019/03/22.
 * 逐日预报
 */
public class CommandMain7DaysWeather extends CommandMainBase {

    private Activity activity;
    private ViewGroup rootLayout;
    private View rowView;
    private ImageFetcher imageFetcher;
    private Adapter7DaysGridView adapter;
    private List<WeekWeatherInfo> weekList = new ArrayList<>();
    //一周高温列表
    private List<Float> mHighList = new ArrayList<Float>();
    //一周低温列表
    private List<Float> mLowList = new ArrayList<Float>();
    //改变城市
    private boolean mChangeCity = true;
    // 天气内容
    private GridView gridViewWeek;
    private TemperatureView tempertureview;
    private InterfaceShowBg mShowBg;

    public CommandMain7DaysWeather(Activity activity , ViewGroup rootLayout, InterfaceShowBg mShowBg) {
        this.activity = activity;
        this.rootLayout = rootLayout;
        if(activity instanceof ActivityMain) {
            imageFetcher = ((ActivityMain) activity).getImageFetcher();
        }
        this.mShowBg = mShowBg;
    }

    @Override
    protected void init() {
        rowView = LayoutInflater.from(activity).inflate(R.layout.layout_main_7days_weather, rootLayout, false);
        rootLayout.addView(rowView);
        tempertureview = (TemperatureView) rowView.findViewById(R.id.tempertureview);
        gridViewWeek = (GridView) rowView.findViewById(R.id.maingridview);
        adapter = new Adapter7DaysGridView(activity,imageFetcher, weekList, mShowBg);
        gridViewWeek.setAdapter(adapter);
    }

    public void setChangeCity() {
        mChangeCity = true;
    }


    @Override
    protected void refresh() {
        okHttpWeek();

        if (mChangeCity) {
            if (adapter != null) {
                adapter.setClickPositon(0);
            }
            mChangeCity = false;
        }
    }

    /**
     * 获取一周天气
     */
    private void okHttpWeek() {
        final PackLocalCity city = ZtqCityDB.getInstance().getCityMain();
        if(city == null) return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param  = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    JSONObject info = new JSONObject();
                    info.put("stationId", city.ID);
                    param.put("paramInfo", info);
                    String json = param.toString();
                    final String url = CONST.BASE_URL+"pnweek";
                    Log.e("pnweek", url);
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
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (!TextUtil.isEmpty(result)) {
                                        Log.e("pnweek", result);
                                        try {
                                            JSONObject obj = new JSONObject(result);
                                            if (!obj.isNull("b")) {
                                                JSONObject bobj = obj.getJSONObject("b");
                                                if (!bobj.isNull("p_new_week")) {
                                                    JSONObject p_new_weekobj = bobj.getJSONObject("p_new_week");
                                                    if (!TextUtil.isEmpty(p_new_weekobj.toString())) {
                                                        PackMainWeekWeatherDown packWeekDown = new PackMainWeekWeatherDown();
                                                        packWeekDown.fillData(p_new_weekobj.toString());
                                                        if (packWeekDown != null && packWeekDown.getWeek() != null && packWeekDown.getWeek().size() != 0) {
                                                            weekList = new ArrayList<>(packWeekDown.getWeek());
                                                            int size = weekList.size();
                                                            int width = getWeekItemWidth()*size;
                                                            ViewGroup.LayoutParams params = gridViewWeek.getLayoutParams();
                                                            params.width = width;
                                                            adapter.setView(rowView);
                                                            gridViewWeek.setNumColumns(size);
                                                            gridViewWeek.setLayoutParams(params);
                                                            gridViewWeek.setColumnWidth(getWeekItemWidth());
                                                            adapter.setUpdate(weekList);
                                                            mHighList.clear();
                                                            mLowList.clear();
                                                            for (int i = 0; i < weekList.size(); i++) {
                                                                WeekWeatherInfo info = weekList.get(i);
                                                                //最后一个高温或低温为空这可以单一添加，否者直接丢弃整个高低温数据
                                                                if (i == weekList.size() - 1) {
                                                                    if (!TextUtils.isEmpty(info.higt)) {
                                                                        mHighList.add(Float.parseFloat(info.higt));
                                                                    }
                                                                    if (!TextUtils.isEmpty(info.lowt)) {
                                                                        mLowList.add(Float.parseFloat(info.lowt));
                                                                    }
                                                                } else {
                                                                    if (!TextUtils.isEmpty(info.higt) && !TextUtils.isEmpty(info.lowt)) {
                                                                        mHighList.add(Float.parseFloat(info.higt));
                                                                        mLowList.add(Float.parseFloat(info.lowt));
                                                                    }
                                                                }
                                                            }
                                                            tempertureview.setTemperture(mHighList, mLowList, size);
                                                            params = tempertureview.getLayoutParams();
                                                            params.width = width;
                                                            tempertureview.setLayoutParams(params);
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

    private int getWeekItemWidth() {
        int width = (int) (Util.getScreenWidth(activity)/7.0f);
        return width;
    }
}
