package com.pcs.ztqtj.view.activity.life.travel;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.pcs.lib.lib_pcs_v3.model.image.ImageFetcher;
import com.pcs.lib_ztqfj_v2.model.pack.net.week.PackTravelWeekDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.week.WeekWeatherInfo;
import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.AdapterTravelWeekWeather;
import com.pcs.ztqtj.control.tool.utils.TextUtil;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.util.OkHttpUtil;

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
 * 生活气象-旅游气象-第一页
 */
@SuppressLint("ValidFragment")
public class TravelFragmentOne extends Fragment {
    private TextView weatherTextView;
    private TextView tempTextView;
    private TextView dateTextView;
    private ListView weekweather;
    private ImageView iconImageView;
    private TextView areaName;
    // private ImageView travelOneBanner;
    private AdapterTravelWeekWeather weekAdapter;
    private String cityId = "";
    private String cityName = "";
    private List<WeekWeatherInfo> weekWeatherList = new ArrayList<>();
    private ImageFetcher mImageFetcher;

    public TravelFragmentOne(String cityId, String cityName, ImageFetcher imageFetcher) {
        this.cityId = cityId;
        this.cityName = cityName;
        mImageFetcher = imageFetcher;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.travel_pageone_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        areaName = (TextView) getView().findViewById(R.id.area_tv);
        weatherTextView = (TextView) getView().findViewById(R.id.weather_tv);
        tempTextView = (TextView) getView().findViewById(R.id.temp_tv);
        dateTextView = (TextView) getView().findViewById(R.id.date_tv);
        iconImageView = (ImageView) getView().findViewById(R.id.weather_icon);
        weekweather = (ListView) getView().findViewById(R.id.weekweather);
        okHttpWeeklytq(cityId);
    }

    String shareC = "";

    /**
     * 更新实时天气数据
     */
    private void updateWeather(WeekWeatherInfo info) {
        weatherTextView.setText(info.wind_dir_day);
        if (!TextUtils.isEmpty(info.lowt)) {
            tempTextView.setText(info.lowt + "/" + info.higt + "°C");
        }
        dateTextView.setText(info.gdt + info.week);
    }

    /**
     * 获取一周预报
     */
    private void okHttpWeeklytq(final String stationId) {
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
                    final String url = CONST.BASE_URL+"weeklytq";
                    Log.e("weeklytq", url);
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
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.e("weeklytq", result);
                                    if (!TextUtil.isEmpty(result)) {
                                        try {
                                            JSONObject obj = new JSONObject(result);
                                            if (!obj.isNull("b")) {
                                                JSONObject bobj = obj.getJSONObject("b");
                                                if (!bobj.isNull("weeklytq#"+stationId)) {
                                                    JSONObject weeklytq = bobj.getJSONObject("weeklytq#"+stationId);
                                                    if (!TextUtil.isEmpty(weeklytq.toString())) {
                                                        PackTravelWeekDown packTravelWeekDown = new PackTravelWeekDown();
                                                        packTravelWeekDown.fillData(weeklytq.toString());
                                                        weekWeatherList = packTravelWeekDown.getWeek();
                                                        if (weekWeatherList.size() > 0) {
                                                            WeekWeatherInfo info = weekWeatherList.get(0);
                                                            updateWeather(info);
                                                            weekAdapter = new AdapterTravelWeekWeather(getActivity(), weekWeatherList.subList(1, weekWeatherList.size()), mImageFetcher);
                                                            weekweather.setAdapter(weekAdapter);
                                                            weekAdapter.notifyDataSetChanged();
                                                            shareC = packTravelWeekDown.getShareStr(cityName);
                                                        } else {
                                                            return;
                                                        }
                                                        // -----------顶部显示
                                                        WeekWeatherInfo info = weekWeatherList.get(0);
                                                        areaName.setText(cityName);

                                                        String h_l_str = "";
                                                        // ------今天
                                                        weatherTextView.setText(info.weather);
                                                        h_l_str += info.higt + "~" + info.lowt + "°C";

                                                        tempTextView.setText(h_l_str);
                                                        dateTextView.setText(info.gdt + "" + info.week);

                                                        Drawable drawable = mImageFetcher.getImageCache().getBitmapFromAssets(packTravelWeekDown.getIconPath(packTravelWeekDown.getTodayIndex()));
                                                        iconImageView.setImageDrawable(drawable);
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
