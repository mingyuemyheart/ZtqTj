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
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityMain;
import com.pcs.lib_ztqfj_v2.model.pack.net.week.PackMainWeekWeatherUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.week.WeekWeatherInfo;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
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
        requestWeek();
        if (mChangeCity) {
            if (adapter != null) {
                adapter.setClickPositon(0);
            }
            mChangeCity = false;
        }
    }

    private void requestWeek() {
        PackLocalCityMain packCity = ZtqCityDB.getInstance().getCityMain();
        if (packCity == null) {
            return;
        }
        final PackMainWeekWeatherUp packWeekUp = new PackMainWeekWeatherUp();
        packWeekUp.setCity(packCity);
//        PackMainWeekWeatherDown packWeekDown = (PackMainWeekWeatherDown) PcsDataManager.getInstance().getNetPack(packWeekUp.getName());
//        if (packWeekDown != null && packWeekDown.getWeek() != null
//                && packWeekDown.getWeek().size() != 0) {
//            weekList = new ArrayList<>(packWeekDown.getWeek());
//            if(weekList.size() > 0) {
//                weekList.remove(0);
//            }
//            int size = weekList.size();
//            int width = getWeekItemWidth()*size;
//            ViewGroup.LayoutParams params = gridViewWeek.getLayoutParams();
//            params.width = width;
//            adapter.setView(rowView);
//            gridViewWeek.setNumColumns(size);
//            gridViewWeek.setLayoutParams(params);
//            gridViewWeek.setColumnWidth(getWeekItemWidth());
//            adapter.setUpdate(weekList);
//            mHighList.clear();
//            mLowList.clear();
//            for (int i = 0; i < weekList.size(); i++) {
//                WeekWeatherInfo info = weekList.get(i);
//                //最后一个高温或低温为空这可以单一添加，否者直接丢弃整个高低温数据
//                if (i == weekList.size() - 1) {
//                    if (!TextUtils.isEmpty(info.higt)) {
//                        mHighList.add(Float.parseFloat(info.higt));
//                    }
//                    if (!TextUtils.isEmpty(info.lowt)) {
//                        mLowList.add(Float.parseFloat(info.lowt));
//                    }
//                } else {
//                    if (!TextUtils.isEmpty(info.higt) && !TextUtils.isEmpty(info.lowt)) {
//                        mHighList.add(Float.parseFloat(info.higt));
//                        mLowList.add(Float.parseFloat(info.lowt));
//                    }
//                }
//            }
//            tempertureview.setTemperture(mHighList, mLowList, size);
//            params = tempertureview.getLayoutParams();
//            params.width = width;
//            tempertureview.setLayoutParams(params);
//        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                final String url = CONST.BASE_URL+packWeekUp.getName();
                Log.e(packWeekUp.getName(), url);
                OkHttpUtil.enqueue(new Request.Builder().url(url).build(), new Callback() {
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
                                    try {
                                        JSONObject obj = new JSONObject(result);
                                        if (!obj.isNull("b")) {
                                            JSONObject bobj = obj.getJSONObject("b");
                                            if (!bobj.isNull("p_new_week")) {
                                                JSONObject p_new_weekobj = bobj.getJSONObject("p_new_week");
                                                if (!p_new_weekobj.isNull("week")) {
                                                    JSONArray weekArray = p_new_weekobj.getJSONArray("week");
                                                    weekList.clear();
                                                    for (int i = 0; i < weekArray.length(); i++) {
                                                        WeekWeatherInfo dto = new WeekWeatherInfo();
                                                        JSONObject itemObj = weekArray.getJSONObject(i);
                                                        if (!itemObj.isNull("gdt")) {
                                                            dto.gdt = itemObj.getString("gdt");
                                                        }
                                                        if (!itemObj.isNull("wd_night")) {
                                                            dto.wd_night = itemObj.getString("wd_night");
                                                        }
                                                        if (!itemObj.isNull("wind_dir_night")) {
                                                            dto.wind_dir_night = itemObj.getString("wind_dir_night");
                                                        }
                                                        if (!itemObj.isNull("wd_night_ico")) {
                                                            dto.wd_night_ico = itemObj.getString("wd_night_ico");
                                                        }
                                                        if (!itemObj.isNull("wind_dir_day")) {
                                                            dto.wind_dir_day = itemObj.getString("wind_dir_day");
                                                        }
                                                        if (!itemObj.isNull("lowt")) {
                                                            dto.lowt = itemObj.getString("lowt");
                                                        }
                                                        if (!itemObj.isNull("wind_speed_day")) {
                                                            dto.wind_speed_day = itemObj.getString("wind_speed_day");
                                                        }
                                                        if (!itemObj.isNull("wd_day_ico")) {
                                                            dto.wd_day_ico = itemObj.getString("wd_day_ico");
                                                        }
                                                        if (!itemObj.isNull("higt")) {
                                                            dto.higt = itemObj.getString("higt");
                                                        }
                                                        if (!itemObj.isNull("weather")) {
                                                            dto.weather = itemObj.getString("weather");
                                                        }
                                                        if (!itemObj.isNull("wind_speed_night")) {
                                                            dto.wind_speed_night = itemObj.getString("wind_speed_night");
                                                        }
                                                        if (!itemObj.isNull("week")) {
                                                            dto.week = itemObj.getString("week");
                                                        }
                                                        if (!itemObj.isNull("wd_day")) {
                                                            dto.wd_day = itemObj.getString("wd_day");
                                                        }
                                                        if (!itemObj.isNull("gdt")) {
                                                            dto.gdt = itemObj.getString("gdt");
                                                        }
                                                        weekList.add(dto);
                                                    }

                                                    if(weekList.size() > 0) {
                                                        weekList.remove(0);
                                                    }
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
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                    }
                });
            }
        }).start();
    }

    private int getWeekItemWidth() {
        int width = (int) (Util.getScreenWidth(activity)/7.0f);
        return width;
    }
}
