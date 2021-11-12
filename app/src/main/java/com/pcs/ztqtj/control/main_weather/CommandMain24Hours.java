package com.pcs.ztqtj.control.main_weather;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amap.api.maps.model.LatLng;
import com.pcs.lib.lib_pcs_v3.control.tool.Util;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCity;
import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.MyPackHourForecastDown;
import com.pcs.ztqtj.control.adapter.hour_forecast.AdapterMainHourForecast;
import com.pcs.ztqtj.control.tool.ZtqLocationTool;
import com.pcs.ztqtj.control.tool.utils.TextUtil;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.util.OkHttpUtil;
import com.pcs.ztqtj.view.myview.Hour24View;
import com.pcs.ztqtj.view.myview.MyGridView;

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
 * 首页-逐时预报
 */
public class CommandMain24Hours extends CommandMainBase {

    private Activity activity;
    private ViewGroup rootLayout;
    private View rowView;
    private MyGridView gridview24hour;
    private AdapterMainHourForecast adapterMain;
    private Hour24View main24hour;
    private MyPackHourForecastDown down = new MyPackHourForecastDown();

    public CommandMain24Hours(Activity activity , ViewGroup rootLayout) {
        this.activity = activity;
        this.rootLayout = rootLayout;
    }

    @Override
    protected void init() {
        rowView = LayoutInflater.from(activity).inflate(R.layout.layout_main_24hours, rootLayout, false);
        rootLayout.addView(rowView);
        gridview24hour = rowView.findViewById(R.id.gridview24hour);
        adapterMain = new AdapterMainHourForecast(activity, down.list);
        gridview24hour.setAdapter(adapterMain);
        main24hour = rowView.findViewById(R.id.main24hour);
    }

    @Override
    protected void refresh() {
        okHttpHourForecast();
    }

    /**
     * 获取逐时预报
     */
    private void okHttpHourForecast() {
        final LatLng latLng = ZtqLocationTool.getInstance().getLatLng();
        final PackLocalCity city = ZtqCityDB.getInstance().getCityMain();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param  = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    JSONObject info = new JSONObject();
                    if (latLng != null) {
                        info.put("lat", latLng.latitude+"");
                        info.put("lon", latLng.longitude+"");
                        info.put("typeModel", "forecast");
                    } else {
                        if (city != null) {
                            info.put("stationId", city.ID);
                        }
                    }
                    param.put("paramInfo", info);
                    String json = param.toString();
                    Log.e("forecast", json);
                    String dataUrl = "";
                    if (latLng != null) {
                        dataUrl = CONST.BASE_URL+"grid";
                    } else {
                        dataUrl = CONST.BASE_URL+"forecast";
                    }
                    final String url = dataUrl;
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
//                                    Log.e("forecast", result);
                                    if (!TextUtil.isEmpty(result)) {
                                        try {
                                            JSONObject obj = new JSONObject(result);
                                            if (!obj.isNull("b")) {
                                                JSONObject bobj = obj.getJSONObject("b");
                                                if (!bobj.isNull("forecast")) {
                                                    JSONObject forecastobj = bobj.getJSONObject("forecast");
                                                    if (!TextUtils.isEmpty(forecastobj.toString())) {
                                                        down.fillData(forecastobj.toString());
                                                        if (down.list.size() > 0) {
                                                            if (down.list.size() > 0) {
                                                                List<Float> mTopTemp = new ArrayList<>();
                                                                List<Float> mLowRain = new ArrayList<>();
                                                                int size = down.list.size();
                                                                for (int i = 0; i < size; i++) {
                                                                    String rain = down.list.get(i).rainfall;
                                                                    String temp = down.list.get(i).temperature;
                                                                    if (!TextUtils.isEmpty(temp) && !TextUtils.equals("�", temp)) {
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
                                                                int width = (int)((Util.getScreenWidth(activity)/7.0f) * size);

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
                                                                main24hour.setTemperture(mTopTemp, mLowRain);
                                                            } else {
                                                                rowView.setVisibility(View.GONE);
                                                            }
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

}
