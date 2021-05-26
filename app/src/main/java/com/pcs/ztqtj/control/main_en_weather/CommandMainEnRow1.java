package com.pcs.ztqtj.control.main_en_weather;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib.lib_pcs_v3.model.image.ImageConstant;
import com.pcs.lib.lib_pcs_v3.model.image.ImageFetcher;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityMain;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackBannerDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackBannerUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackSstqDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackSstqUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.airinfopack.PackAirInfoDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.airinfopack.PackAirInfoSimpleDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.airinfopack.PackAirInfoSimpleUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.voice.PackVoiceUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.week.PackMainWeekWeatherDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.week.PackMainWeekWeatherUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.week.WeekWeatherInfo;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.inter.InterfaceShowBg;
import com.pcs.ztqtj.control.main_weather.CommandMainBase;
import com.pcs.ztqtj.control.tool.AirQualityTool;
import com.pcs.ztqtj.control.tool.utils.TextUtil;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.util.OkHttpUtil;
import com.pcs.ztqtj.view.activity.activityEn.ActivityMainEn;
import com.pcs.ztqtj.view.activity.web.ActivityWebSh;
import com.pcs.ztqtj.view.dialog.DialogFactory;
import com.pcs.ztqtj.view.dialog.DialogVoiceButton;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * JiangZy on 2016/6/3.
 */
public class CommandMainEnRow1 extends CommandMainBase {
    private ActivityMainEn mActivity;
    private ViewGroup mRootLayout;
    private ImageFetcher mImageFetcher;
    private InterfaceShowBg mShowBg;
    //行视图
    private View mRowView;
    //实时天气
    private PackSstqUp mPackSstqUp = new PackSstqUp();
    //一周天气
    private PackMainWeekWeatherUp mPackWeekUp = new PackMainWeekWeatherUp();

    private Map<String, String> m_week = new HashMap<>();

    private TextView tv_date_en;

    private LinearLayout lay_comman01;
    private int height;
    private PackAirInfoSimpleUp packAirInfoSimpleUp = new PackAirInfoSimpleUp();
    private ImageView img_banner_right;
    private LinearLayout lay_tem;
    private ImageView img_tem, img_hs;
    public static boolean is_hs = false;
    private CommandMainEnRow2 commandMainEnRow2;
    private CommandMainEnRow3 commandMainEnRow3;

    private TextView text_aqi_num,text_aqi_type;
    private ImageView iv_aqi_type;

    public CommandMainEnRow1(CommandMainEnRow2 EnRow2, CommandMainEnRow3 EnRow3, Activity activity, ViewGroup
            rootLayout, ImageFetcher imageFetcher,
                             InterfaceShowBg showBg, int mheight) {
        initDate();
        commandMainEnRow2 = EnRow2;
        commandMainEnRow3 = EnRow3;
        mActivity = (ActivityMainEn) activity;
        mRootLayout = rootLayout;
        mImageFetcher = imageFetcher;
        mShowBg = showBg;
        height = mheight;
    }

    private void initDate() {
        m_week.put("Mon", "Monday");
        m_week.put("Tue", "Tuesday");
        m_week.put("Wed", "Wednesday");
        m_week.put("Thu", "Thursday");
        m_week.put("Fri", "Friday");
        m_week.put("Sat", "Saturday");
        m_week.put("Sun", "Sunday");
    }

    @Override
    protected void init() {
        mRowView = LayoutInflater.from(mActivity).inflate(
                R.layout.item_home_weather_en_1, null);
        lay_tem = (LinearLayout) mRowView.findViewById(R.id.lay_tem);
        img_tem = (ImageView) mRowView.findViewById(R.id.img_tem);
        img_hs = (ImageView) mRowView.findViewById(R.id.img_hs);
        if (is_hs) {
            img_tem.setVisibility(View.GONE);
            img_hs.setVisibility(View.VISIBLE);
        } else {
            img_tem.setVisibility(View.VISIBLE);
            img_hs.setVisibility(View.GONE);
        }
        img_banner_right = (ImageView) mRowView.findViewById(R.id.img_banner_right);
        img_banner_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickAD("32");
            }
        });
        DisplayMetrics dm = mActivity.getResources().getDisplayMetrics();
        int heights = dm.heightPixels - height;
//        LinearLayout.LayoutParams params= (LinearLayout.LayoutParams) lay_comman01.getLayoutParams();
////获取当前控件的布局对象
//        params.height=height/2;//设置当前控件布局的高度
        mRowView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                heights / 2));
        mRootLayout.addView(mRowView);
        tv_date_en = (TextView) mRowView.findViewById(R.id.tv_date_en);
        Date s = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy",
                Locale.ENGLISH);
        SimpleDateFormat sdf_w = new SimpleDateFormat("E",
                Locale.ENGLISH);
        String date = m_week.get(sdf_w.format(s));
        tv_date_en.setText(date + "   " + sdf.format(s));
        lay_tem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (is_hs) {
                    img_tem.setVisibility(View.VISIBLE);
                    img_hs.setVisibility(View.GONE);
                    is_hs = false;
                } else {
                    img_tem.setVisibility(View.GONE);
                    img_hs.setVisibility(View.VISIBLE);
                    is_hs = true;
                }
                refresh();
                commandMainEnRow2.refresh();
                commandMainEnRow3.refresh();
            }
        });

    }

    private String stationName = "";

    @Override
    protected void refresh() {
        refreshAD("32", (ImageView) mRowView.findViewById(R.id.img_banner_right));
        stationName = "";
        // 气温
        TextView text_temperature = (TextView) mRootLayout.findViewById(R.id.text_temperature);
        text_temperature.setText("");
        TextView text_temperature_decimals = (TextView) mRootLayout.findViewById(R.id.text_temperature_decimals);
        text_temperature_decimals.setText("");
        // 中文天气
        TextView text_weather_cn = (TextView) mRootLayout.findViewById(R.id.text_weather_cn);
        text_weather_cn.setText("");
        // 雨量
        TextView text_rain = (TextView) mRootLayout.findViewById(R.id.text_rain);
        text_rain.setText("");
        // 风速
        TextView text_wind = (TextView) mRootLayout.findViewById(R.id.text_wind);
        text_wind.setText("");

        TextView text_ludian = (TextView) mRootLayout.findViewById(R.id.text_ludian);
        text_ludian.setText("");
        // 能见度
        TextView text_visibility = (TextView) mRootLayout.findViewById(R.id.text_visibility);
        text_visibility.setText("");
        TextView text_station = (TextView) mRootLayout.findViewById(R.id.text_station_name);
        text_station.setText("");
        TextView text_reflush_time = (TextView) mRootLayout.findViewById(R.id.text_reflush_time);
        text_reflush_time.setText("");
        text_aqi_num = (TextView) mRootLayout.findViewById(R.id.tv_aqi_num);
        text_aqi_num.setText("");
        text_aqi_type = (TextView) mRootLayout.findViewById(R.id.tv_aqi_type);
        text_aqi_type.setText("");
        iv_aqi_type = (ImageView) mRootLayout.findViewById(R.id.iv_main_aqi);
        TextView text_degree = (TextView) mRootLayout.findViewById(R.id.text_degree);

        // 当前城市
        PackLocalCityMain packCity = ZtqCityDB.getInstance().getCityMain();
        if (packCity == null || TextUtils.isEmpty(packCity.ID)) {
            return;
        }

        TextView rain_search = (TextView) mRootLayout.findViewById(R.id.rain_search);
        TextView report = (TextView) mRootLayout.findViewById(R.id.report);

        String tempStr, tempHs, temHs_little = "", temHs_big = "";
        // -------当前天气
        mPackSstqUp.area = packCity.ID;
        PackSstqDown packSstq = (PackSstqDown) PcsDataManager.getInstance().getNetPack(mPackSstqUp.getName());
        if (packSstq == null) {
            packSstq = new PackSstqDown();
        }
        // 气温
        tempStr = packSstq.ct;
        tempHs = String.valueOf(Float.parseFloat(tempStr) * 1.8 + 32);
        temHs_big = String.valueOf(Float.parseFloat(tempStr) * 1.8 + 32);
        if (!TextUtils.isEmpty(packSstq.ct) && packSstq.ct.indexOf(".") > -1) {
            tempStr = packSstq.ct.substring(0, packSstq.ct.indexOf(".") + 1);
        }
        if (!TextUtils.isEmpty(tempHs) && tempHs.indexOf(".") > 1) {
            tempHs = tempHs.substring(0, tempHs.indexOf(".") + 1);
        }
        if (is_hs) {
            text_temperature.setText(tempHs.replace(".", ""));
            text_degree.setText("°F");
        } else {
            text_temperature.setText(tempStr);
            text_degree.setText("°C");
        }
        tempStr = "";
        //气温小数
        if (!TextUtils.isEmpty(packSstq.ct) && packSstq.ct.indexOf(".") > -1) {
            tempStr = packSstq.ct.substring(packSstq.ct.indexOf(".") + 1, packSstq.ct.length());
        }
        if (!TextUtils.isEmpty(String.valueOf(Float.parseFloat(packSstq.ct) * 1.8 + 32)) && String.valueOf(Float
                .parseFloat(packSstq.ct) * 1.8 + 32).indexOf(".") > 1) {
            temHs_little = temHs_big.substring(tempHs.indexOf(".") + 1, temHs_big.length());
        }
        if (is_hs) {
            if (!temHs_little.equals("0")) {
                text_temperature_decimals.setText("." + temHs_little.substring(0, 1));
            }
        } else {
            text_temperature_decimals.setText(tempStr);
        }

        packAirInfoSimpleUp.setCity(packCity);
        packAirInfoSimpleUp.type = "1";
        okHttpAirCityStation(packAirInfoSimpleUp.getName());

        mPackWeekUp.setCity(packCity);
        PackMainWeekWeatherDown packMainWeekDown = (PackMainWeekWeatherDown) PcsDataManager.getInstance().getNetPack
                (mPackWeekUp.getName());
        if (packMainWeekDown != null && packMainWeekDown.getToday() != null) {
            WeekWeatherInfo todayInfo = packMainWeekDown.getToday();
            // 中文天气
            text_weather_cn.setText(todayInfo.weather);
            // 刷新背景
            String path = todayInfo.getWeatherBg();
            String pathThumb = todayInfo.getWeatherThumb();
            if (!TextUtils.isEmpty(path)) {
                mShowBg.showBg(path, pathThumb);
            }
        }
        // 湿度
        TextView text_humidity = (TextView) mRootLayout
                .findViewById(R.id.text_humidity);
        if (!TextUtils.isEmpty(packSstq.humidity)) {
            text_humidity.setText(packSstq.humidity + "%");
        } else {
            text_humidity.setText("%");
        }

        // 雨量
        if (!TextUtils.isEmpty(packSstq.rainfall)) {
            text_rain.setText(packSstq.rainfall + "mm");
        } else {
            text_rain.setText("mm");
        }

        // 风速
        if (!TextUtils.isEmpty(packSstq.wind)) {
            text_wind.setText(packSstq.us_fl);
        } else {
            text_wind.setText("");
        }
        // 能见度
        if (!TextUtils.isEmpty(packSstq.visibility)) {
            text_visibility.setText(packSstq.visibility + "m");
        } else {
            text_visibility.setText("m");
        }
        if (!TextUtils.isEmpty(packSstq.dewPiont)) {
            text_ludian.setText(packSstq.dewPiont + "℃");
        } else {
            text_ludian.setText("℃");
        }
        String ludian = "";
        if (!TextUtils.isEmpty(packSstq.dewPiont)) {
            ludian = changeStr(packSstq.dewPiont);
        }
        if (is_hs) {
            if (!TextUtils.isEmpty(packSstq.dewPiont)) {
                text_ludian.setText(ludian + "℉");
            } else {
                text_ludian.setText("℉");
            }
        } else {
            if (!TextUtils.isEmpty(packSstq.dewPiont)) {
                text_ludian.setText(packSstq.dewPiont + "℃");
            } else {
                text_ludian.setText("℃");
            }
        }

        Time time = new Time();
        String time_text = "";
        if (!TextUtils.isEmpty(packSstq.upt_time)) {
            try {
                if (!TextUtils.isEmpty(packSstq.upt_time)) {
                    Long longTime = Long.parseLong(packSstq.upt_time);
                    time.set(longTime);
                    time_text = time.format("%H:%M");
                }
                if (packCity.isFjCity) {
                    stationName = packSstq.stationname;
                } else {
                    stationName = packCity.NAME;
                }
                //text_reflush_time.setText(time.format("实况%H:%M更新"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            stationName = packCity.NAME;
        }
        if (TextUtils.isEmpty(stationName)) {
            text_station.setText(time_text + "采集");
        } else {
            text_station.setText(stationName + "气象站" + time_text + "采集");
        }

    }

    /**
     * 获取空气质量实况
     * @param name
     */
    private void okHttpAirCityStation(final String name) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = CONST.BASE_URL+name;
                url = url.replace("airinfosimple", "air_city_station");
                Log.e("air_city_station", url);
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
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    JSONObject obj = new JSONObject(result);
                                    if (!obj.isNull("b")) {
                                        JSONObject bobj = obj.getJSONObject("b");
                                        if (!bobj.isNull("airinfosimple")) {
                                            JSONObject airinfosimple = bobj.getJSONObject("airinfosimple");
                                            if (!TextUtil.isEmpty(airinfosimple.toString())) {
                                                PackAirInfoSimpleDown packAir = new PackAirInfoSimpleDown();
                                                packAir.fillData(airinfosimple.toString());
                                                if (packAir != null) {
//            if (!TextUtils.isEmpty(packAir.airInfoSimple.quality)) {
//                text_pollute.setText(packAir.airInfoSimple.quality);
//            }
                                                    if (!TextUtils.isEmpty(packAir.airInfoSimple.aqi)) {
                                                        int color = AirQualityTool.getInstance().getAqiColor(
                                                                Integer.valueOf(packAir.airInfoSimple.aqi));
                                                        text_aqi_num.setText(packAir.airInfoSimple.aqi);
                                                        text_aqi_num.setTextColor(color);
                                                        getAqiColorImage(iv_aqi_type, Integer.valueOf(packAir.airInfoSimple.aqi));
                                                        text_aqi_type.setText(packAir.airInfoSimple.us_quality);
                                                        text_aqi_type.setTextColor(color);
                                                    } else {
                                                        text_aqi_num.setText("null");
                                                        text_aqi_num.setTextColor(mActivity.getResources().getColor(android.R.color.white));
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                });
            }
        }).start();
    }

    private String changeStr(String value) {
        String temHs_little = "";
        String tempHs = String.valueOf(Float.parseFloat(value) * 1.8 + 32);
        if (!TextUtils.isEmpty(tempHs) && tempHs.indexOf(".") > 1) {
            tempHs = tempHs.substring(0, tempHs.indexOf(".") + 1).replace(".", "");
        }
        if (!TextUtils.isEmpty(String.valueOf(Float.parseFloat(value) * 1.8 + 32)) && String.valueOf(Float
                .parseFloat(value) * 1.8 + 32).indexOf(".") > 1) {
            temHs_little = value.substring(value.indexOf(".") + 1, value.length());
        }
        if (!temHs_little.equals("0")) {
            tempHs = tempHs + "." + temHs_little.substring(0, 1);
        }
        return tempHs;
    }

    /**
     * 获取AQI颜色值
     *
     * @return
     */
    public void getAqiColorImage(ImageView imageView, int aqi) {
        if (aqi <= 50) {
            // 优
            imageView.setImageResource(R.drawable.icon_aqi_en_green);
        } else if (aqi > 50 && aqi <= 100) {
            // 良
            imageView.setImageResource(R.drawable.icon_aqi_en_yellow);
        } else if (aqi > 100 && aqi <= 150) {
            // 轻度污染
            imageView.setImageResource(R.drawable.icon_aqi_en_orange);
        } else if (aqi > 150 && aqi <= 200) {
            // 中度污染
            imageView.setImageResource(R.drawable.icon_aqi_en_red);
        } else if (aqi > 200 && aqi <= 300) {
            // 重度污染
            imageView.setImageResource(R.drawable.icon_aqi_en_purple);
        } else if (aqi > 300) {
            // 严重污染
            imageView.setImageResource(R.drawable.icon_aqi_en_dull_red);
        }
    }


    /**
     * 通过时间秒毫秒数判断两个时间的间隔
     *
     * @param date1
     * @param date2
     * @return
     */
    public static int differentDaysByMillisecond(Date date1, Date date2) {
        int days = (int) ((date2.getTime() - date1.getTime()) / (1000 * 3600 * 24));
        return days;
    }


    //上传包：广告
    private PackBannerUp mPackBannerUp = new PackBannerUp();

    /**
     * 刷新广告
     *
     * @param position_id 广告ID
     * @param imageView
     */
    private void refreshAD(String position_id, ImageView imageView) {

//        imageView.setBackgroundResource(R.color.text_color);
//        imageView.setVisibility(View.VISIBLE);

        mPackBannerUp.position_id = position_id;
        PackBannerDown packDown = (PackBannerDown) PcsDataManager.getInstance().getNetPack(mPackBannerUp.getName());
        if (packDown == null || packDown.arrBannerInfo.size() == 0) {
            imageView.setVisibility(View.GONE);
            return;
        } else {
            imageView.setVisibility(View.VISIBLE);
        }

        String url = mActivity.getResources().getString(R.string.file_download_url) + packDown.arrBannerInfo.get(0)
                .img_path;
        mImageFetcher.loadImage(url, imageView, ImageConstant.ImageShowType.SRC);

    }

    //点击广告
    private void clickAD(String position_id) {
        mPackBannerUp.position_id = position_id;
        PackBannerDown packDown = (PackBannerDown) PcsDataManager.getInstance().getNetPack(mPackBannerUp.getName());
        if (packDown == null || packDown.arrBannerInfo.size() == 0) {
            return;
        }

        Intent it = new Intent(mActivity, ActivityWebSh.class);
        it.putExtra("title", packDown.arrBannerInfo.get(0).title);
        it.putExtra("url", packDown.arrBannerInfo.get(0).url);
        it.putExtra("shareContent", packDown.arrBannerInfo.get(0).fx_content);
        mActivity.startActivity(it);
    }


    /* 判断是否是处于wifi状态下*/
    protected boolean isWiFiNewWord() {
        ConnectivityManager connectivityManager = (ConnectivityManager) mActivity.getSystemService(Context
                .CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI;
    }

}
