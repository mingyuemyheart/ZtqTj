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
import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.MyPackHourForecastDown;
import com.pcs.ztqtj.control.adapter.hour_forecast.AdapterMainHourForecast;
import com.pcs.ztqtj.control.tool.utils.TextUtil;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.util.OkHttpUtil;
import com.pcs.ztqtj.view.activity.ActivityMain;
import com.pcs.ztqtj.view.myview.Hour24View;
import com.pcs.ztqtj.view.myview.MyGridView;
import com.pcs.ztqtj.view.myview.MyHScrollView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
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
 * Created by tyaathome on 2019/06/22.
 */
public class CommandMain24Hours extends CommandMainBase {
    private Activity activity;
    private ViewGroup rootLayout;
    private View rowView;
    private ImageFetcher imageFetcher;
    private MyGridView gridview24hour;
    //适配器：小时
    private AdapterMainHourForecast adapterMain;
    private Hour24View main24hour;
    private List<MyPackHourForecastDown.HourForecast> list = new ArrayList<>();
    private MyHScrollView scrollView;
    private TextView tvNodata;
    private View layoutWeather;
    private TextView tvDesc;

    public CommandMain24Hours(Activity activity , ViewGroup rootLayout) {
        this.activity = activity;
        this.rootLayout = rootLayout;
        if(activity instanceof ActivityMain) {
            imageFetcher = ((ActivityMain) activity).getImageFetcher();
        }
    }

    @Override
    protected void init() {
        rowView = LayoutInflater.from(activity).inflate(R.layout.layout_main_24hours, rootLayout, false);
        rootLayout.addView(rowView);
        gridview24hour = (MyGridView) rowView.findViewById(R.id.gridview24hour);
        adapterMain = new AdapterMainHourForecast(activity, list);
        gridview24hour.setAdapter(adapterMain);
        scrollView = (MyHScrollView) rowView.findViewById(R.id.layout_24house);
        main24hour = rowView.findViewById(R.id.main24hour);
        tvNodata = (TextView) rowView.findViewById(R.id.tv_nodata);
        layoutWeather = rowView.findViewById(R.id.weather_view);
        tvDesc = rowView.findViewById(R.id.text_content_desc);
    }

    @Override
    protected void refresh() {
        requestForecast();
    }

    private void requestForecast() {
        final PackLocalCity city = ZtqCityDB.getInstance().getCityMain();
        if (city == null) return;
        //24小时
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
                    final String url = CONST.BASE_URL+"forecast";
                    Log.e("forecast", url);
                    RequestBody body = FormBody.create(MediaType.parse("application/json; charset=utf-8"), json);
                    OkHttpUtil.enqueue(new Request.Builder().post(body).url(url).build(), new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            Log.e("onFailure", e.getMessage());
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
                                        Log.e("forecast", result);
                                        try {
                                            JSONObject obj = new JSONObject(result);
                                            if (!obj.isNull("b")) {
                                                JSONObject bobj = obj.getJSONObject("b");
                                                if (!bobj.isNull("forecast")) {
                                                    JSONObject forecastobj = bobj.getJSONObject("forecast");
                                                    if (!forecastobj.isNull("today")) {
                                                        JSONArray todayArray = forecastobj.getJSONArray("today");
                                                        list.clear();
                                                        for (int i = 0; i < todayArray.length(); i++) {
                                                            MyPackHourForecastDown down = new MyPackHourForecastDown();
                                                            MyPackHourForecastDown.HourForecast dto = down.new HourForecast();
                                                            JSONObject itemObj = todayArray.getJSONObject(i);
                                                            if (!itemObj.isNull("windspeed")) {
                                                                dto.windspeed = itemObj.getString("windspeed");
                                                            }
                                                            if (!itemObj.isNull("windlevel")) {
                                                                dto.windlevel = itemObj.getString("windlevel");
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
//                                                            dto.temperature = new Random().nextInt(20)+"";
                                                            }
                                                            list.add(dto);
                                                        }

                                                        if (list.size() > 0) {
                                                            List<Float> mTopTemp = new ArrayList<>();
                                                            List<Float> mLowRain = new ArrayList<>();
                                                            if (list.size() > 0) {
                                                                int size = list.size();
                                                                for (int i = 0; i < size; i++) {
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
                                                                int width = getWeekItemWidth() * size;

                                                                gridview24hour.setNumColumns(size);
                                                                gridview24hour.setVisibility(View.VISIBLE);
                                                                ViewGroup.LayoutParams gradviewParams = gridview24hour.getLayoutParams();
                                                                gradviewParams.width = width;
                                                                gridview24hour.setLayoutParams(gradviewParams);
                                                                adapterMain.notifyDataSetChanged();

                                                                main24hour.setVisibility(View.VISIBLE);
                                                                main24hour.setCount(size);
                                                                ViewGroup.LayoutParams mainParams = main24hour.getLayoutParams();
                                                                mainParams.width = width;
                                                                main24hour.setLayoutParams(mainParams);

                                                                rowView.setVisibility(View.VISIBLE);
                                                            } else {
                                                                rowView.setVisibility(View.GONE);
                                                                //tvNodata.setVisibility(View.VISIBLE);
                                                                //layoutWeather.setVisibility(View.INVISIBLE);
                                                            }
                                                            main24hour.setTemperture(mTopTemp, mLowRain);
                                                        } else {
                                                            rowView.setVisibility(View.GONE);
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
