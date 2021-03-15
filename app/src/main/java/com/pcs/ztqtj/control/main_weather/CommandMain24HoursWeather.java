package com.pcs.ztqtj.control.main_weather;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pcs.lib.lib_pcs_v3.control.tool.Util;
import com.pcs.lib.lib_pcs_v3.model.image.ImageFetcher;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCity;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackHourForecastDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackHourForecastDown.HourForecast;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackHourForecastUp;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.hour_forecast.AdapterXMForecast;
import com.pcs.ztqtj.control.tool.utils.TextUtil;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.util.OkHttpUtil;
import com.pcs.ztqtj.view.activity.ActivityMain;
import com.pcs.ztqtj.view.myview.MyGridView;
import com.pcs.ztqtj.view.myview.MyHScrollView;
import com.pcs.ztqtj.view.myview.OceanHour24View;

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
 * Created by tyaathome on 2019/03/21.
 */
public class CommandMain24HoursWeather extends CommandMainBase {

    private Activity activity;
    private ViewGroup rootLayout;
    private View rowView;
    private ImageFetcher imageFetcher;
    private MyGridView gridview24hour;
    private AdapterXMForecast adapter;
    private OceanHour24View hour24View;
    private MyHScrollView scrollView;
    private TextView tvNodata;
    private List<HourForecast> forecastList = new ArrayList<>();
    private View layoutWeather;

    public CommandMain24HoursWeather(Activity activity , ViewGroup rootLayout) {
        this.activity = activity;
        this.rootLayout = rootLayout;
        if(activity instanceof ActivityMain) {
            imageFetcher = ((ActivityMain) activity).getImageFetcher();
        }
    }

    @Override
    protected void init() {
        rowView = LayoutInflater.from(activity).inflate(R.layout.layout_main_24hours_weather, rootLayout, false);
        rootLayout.addView(rowView);
        gridview24hour = (MyGridView) rowView.findViewById(R.id.gridview24hour);
        adapter = new AdapterXMForecast(forecastList);
        gridview24hour.setAdapter(adapter);
        scrollView = (MyHScrollView) rowView.findViewById(R.id.layout_24house);
        hour24View = (OceanHour24View) rowView.findViewById(R.id.main24hour);
        hour24View.setParentScrollView(scrollView);
        tvNodata = (TextView) rowView.findViewById(R.id.tv_nodata);
        layoutWeather = rowView.findViewById(R.id.weather_view);
    }

    @Override
    protected void refresh() {
        requestForecast();
    }

    private void requestForecast() {
        PackLocalCity city = ZtqCityDB.getInstance().getCityMain();
        if(city == null) return;
        //24小时
        final PackHourForecastUp packHourUp = new PackHourForecastUp();
        packHourUp.county_id = city.ID;
//        PackHourForecastDown down = (PackHourForecastDown) PcsDataManager.getInstance().getNetPack(packHourUp.getName());
//        if(down != null && down.list != null && down.list.size() > 0) {
//            List<Float> mTopTemp = new ArrayList<>();
//            List<Float> mLowRain = new ArrayList<>();
//            List<HourForecast> list = down.list;
//            int size = list.size();
//            for (int i = 0; i < list.size(); i++) {
//                if (i > 36) {
//                    break;
//                }
//                String rain = list.get(i).rainfall;
//                String temp = list.get(i).temperature;
//                if (!TextUtils.isEmpty(temp)) {
//                    mTopTemp.add(Float.parseFloat(temp));
//                } else {
//                    continue;
//                }
//                if (!TextUtils.isEmpty(rain)) {
//                    mLowRain.add(Float.parseFloat(rain));
//                } else {
//                    mLowRain.add(0f);
//                }
//            }
//            int width = Util.dp2px(60) * size;
//
//            gridview24hour.setNumColumns(size);
//            gridview24hour.setVisibility(View.VISIBLE);
//            ViewGroup.LayoutParams gradviewParams = gridview24hour.getLayoutParams();
//            gradviewParams.width = width;
//            gridview24hour.setLayoutParams(gradviewParams);
//            forecastList.clear();
//            forecastList.addAll(list);
//            adapter.notifyDataSetChanged();
//            hour24View.setData(forecastList, imageFetcher);
//            //tvNodata.setVisibility(View.INVISIBLE);
//            //layoutWeather.setVisibility(View.VISIBLE);
//            rowView.setVisibility(View.VISIBLE);
//        } else {
//            rowView.setVisibility(View.GONE);
//            //tvNodata.setVisibility(View.VISIBLE);
//            //layoutWeather.setVisibility(View.INVISIBLE);
//        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                final String url = CONST.BASE_URL+packHourUp.getName();
                Log.e("forecast", url);
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
                                            if (!bobj.isNull("forecast")) {
                                                JSONObject forecastobj = bobj.getJSONObject("forecast");
                                                if (!forecastobj.isNull("today")) {
                                                    JSONArray todayArray = forecastobj.getJSONArray("today");
                                                    List<PackHourForecastDown.HourForecast> list = new ArrayList<>();
                                                    for (int i = 0; i < todayArray.length(); i++) {
                                                        PackHourForecastDown down = new PackHourForecastDown();
                                                        PackHourForecastDown.HourForecast dto = down.new HourForecast();
                                                        JSONObject itemObj = todayArray.getJSONObject(i);
                                                        if (!itemObj.isNull("windspeed")) {
                                                            dto.windspeed = itemObj.getString("windspeed");
                                                        }
                                                        if (!itemObj.isNull("time")) {
                                                            dto.time = itemObj.getString("time");
                                                        }
                                                        if (!itemObj.isNull("airpressure")) {
                                                            dto.airpressure = itemObj.getString("airpressure");
                                                        }
                                                        if (!itemObj.isNull("visibility")) {
                                                            dto.visibility = itemObj.getString("visibility");
                                                        }
                                                        if (!itemObj.isNull("rainfall")) {
                                                            dto.rainfall = itemObj.getString("rainfall");
                                                        }
                                                        if (!itemObj.isNull("rh")) {
                                                            dto.rh = itemObj.getString("rh");
                                                        }
                                                        if (!itemObj.isNull("ico")) {
                                                            dto.ico = itemObj.getString("ico");
                                                        }
                                                        if (!itemObj.isNull("w_datetime")) {
                                                            dto.w_datetime = itemObj.getString("w_datetime");
                                                        }
                                                        if (!itemObj.isNull("winddir")) {
                                                            dto.winddir = itemObj.getString("winddir");
                                                        }
                                                        if (!itemObj.isNull("temperature")) {
                                                            dto.temperature = itemObj.getString("temperature");
                                                        }
                                                        list.add(dto);
                                                    }

                                                    if(list.size() > 0) {
                                                        List<Float> mTopTemp = new ArrayList<>();
                                                        List<Float> mLowRain = new ArrayList<>();
                                                        int size = list.size();
                                                        for (int i = 0; i < list.size(); i++) {
                                                            if (i > 36) {
                                                                break;
                                                            }
                                                            String rain = list.get(i).rainfall;
                                                            String temp = list.get(i).temperature;
                                                            if (!TextUtils.isEmpty(temp)) {
                                                                mTopTemp.add(Float.parseFloat(temp));
                                                            } else {
                                                                continue;
                                                            }
                                                            if (!TextUtils.isEmpty(rain)) {
                                                                mLowRain.add(Float.parseFloat(rain));
                                                            } else {
                                                                mLowRain.add(0f);
                                                            }
                                                        }
                                                        int width = Util.dp2px(60) * size;

                                                        gridview24hour.setNumColumns(size);
                                                        gridview24hour.setVisibility(View.VISIBLE);
                                                        ViewGroup.LayoutParams gradviewParams = gridview24hour.getLayoutParams();
                                                        gradviewParams.width = width;
                                                        gridview24hour.setLayoutParams(gradviewParams);
                                                        forecastList.clear();
                                                        forecastList.addAll(list);
                                                        adapter.notifyDataSetChanged();
                                                        hour24View.setData(forecastList, imageFetcher);
                                                        //tvNodata.setVisibility(View.INVISIBLE);
                                                        //layoutWeather.setVisibility(View.VISIBLE);
                                                        rowView.setVisibility(View.VISIBLE);
                                                    } else {
                                                        rowView.setVisibility(View.GONE);
                                                        //tvNodata.setVisibility(View.VISIBLE);
                                                        //layoutWeather.setVisibility(View.INVISIBLE);
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
            }
        }).start();

    }
}
