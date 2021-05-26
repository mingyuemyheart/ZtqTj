package com.pcs.ztqtj.control.main_weather;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.pcs.lib.lib_pcs_v3.control.tool.BitmapUtil;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib.lib_pcs_v3.model.image.ImageFetcher;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCity;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityMain;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalUser;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackShareAboutDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackShareAboutUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackSstqDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackSstqUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.week.PackMainWeekWeatherDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.week.PackMainWeekWeatherUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.week.WeekWeatherInfo;
import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.inter.InterfaceShowBg;
import com.pcs.ztqtj.control.tool.PermissionsTools;
import com.pcs.ztqtj.control.tool.ServiceLoginTool;
import com.pcs.ztqtj.control.tool.ShareTools;
import com.pcs.ztqtj.control.tool.SharedPreferencesUtil;
import com.pcs.ztqtj.control.tool.VoiceTool;
import com.pcs.ztqtj.control.tool.ZtqImageTool;
import com.pcs.ztqtj.control.tool.utils.TextUtil;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.util.OkHttpUtil;
import com.pcs.ztqtj.view.activity.ActivityMain;
import com.pcs.ztqtj.view.activity.livequery.ActivityLiveQuery;
import com.pcs.ztqtj.view.activity.livequery.ActivityLiveQueryDetail;
import com.pcs.ztqtj.view.activity.newairquality.ActivityAirQualityQuery;
import com.pcs.ztqtj.view.activity.photoshow.ActivityPhotoShow;
import com.pcs.ztqtj.view.activity.warn.ActivityWarningCenterNotFjCity;
import com.pcs.ztqtj.view.fragment.airquality.ActivityAirQualitySH;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * JiangZy on 2016/6/3.
 */
public class CommandMainRow1 extends CommandMainBase {
    private ActivityMain mActivity;
    private ViewGroup mRootLayout;
    private ImageFetcher mImageFetcher;
    private InterfaceShowBg mShowBg;
    //行视图
    private View mRowView;
    //一周天气
    private PackMainWeekWeatherUp mPackWeekUp = new PackMainWeekWeatherUp();
    //预警未读消息
    private Fragment mFragment;

    private boolean is_readed = true;

    //讯飞语音

    // 语音听写对象
    private static String TAG;
    // 用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<>();
    // 引擎类型TYPE_CLOUD为云端
    // 语音合成对象
    // 默认发音人
    private String voicer = "xiaoyan";
    // 保存听写参数的对象 参数是jar 里写好的
    private int clickNum = 0;
    private boolean isPlay = false;
    public PopupWindow popVoice;
    private boolean isPopVoice = false;
    private ImageView main_voice;
    //讯飞语音
    private VoiceTool voiceTool;

    public CommandMainRow1(Activity activity, ViewGroup rootLayout, ImageFetcher imageFetcher,
                           InterfaceShowBg showBg, Fragment fragment) {
        mActivity = (ActivityMain) activity;
        mRootLayout = rootLayout;
        mImageFetcher = imageFetcher;
        mShowBg = showBg;
        mFragment = fragment;
//        voiceTool = VoiceTool.getInstance(mActivity, CommandMainRow1.this);
    }

    @Override
    protected void init() {
        mRowView = LayoutInflater.from(mActivity).inflate(
                R.layout.item_home_weather_1, null);
        mRowView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        mRootLayout.addView(mRowView);

        // 预警中心
        View btnWarn = mRowView.findViewById(R.id.layout_warn);
        btnWarn.setOnClickListener(onClickRow1);
        // 风雨查询
        View btnWind = mRowView.findViewById(R.id.layout_wind);
        btnWind.setOnClickListener(onClickRow1);
        // 空气质量
        View layoutAir = mRowView.findViewById(R.id.layout_air);
        layoutAir.setOnClickListener(onClickRow1);
        // 决策气象
        View layoutServer = mRowView.findViewById(R.id.layout_server);
        layoutServer.setOnClickListener(onClickRow1);
        //分享
//        View btn_share = mRowView.findViewById(R.id.lay_bt_share);
//        btn_share.setOnClickListener(onClickRow1);
//        //实景
//        View btn_real = mRowView.findViewById(R.id.lay_bt_real);
//        btn_real.setOnClickListener(onClickRow1);
        // 整点实况区域
        View clickinteger = mRowView.findViewById(R.id.layout_temperature);
        clickinteger.setOnClickListener(onClickRow1);

//        //语音
//        main_voice = mRowView.findViewById(R.id.main_voice);
//        main_voice.setOnTouchListener(touchListener);
        //btn.setOnClickListener(onClickRow1);
        // 初始化听写Dialog，如果只使用有UI听写功能，无需创建SpeechRecognizer
        // 使用UI听写功能，请根据sdk文件目录下的notice.txt,放置布局文件和图片资源

//        View mContentView = LayoutInflater.from(mActivity).inflate(R.layout.dialog_voice, null);
//        iv_voice = (ImageView) mContentView.findViewById(R.id.iv_voice);
//        popVoice = new PopupWindow(mContentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup
//        .LayoutParams
//                .WRAP_CONTENT, true);
//        popVoice.setBackgroundDrawable(new BitmapDrawable());
//        // 设置可以获得焦点
//        popVoice.setFocusable(true);
//        popVoice.setOutsideTouchable(true);

    }

    private String stationName = "";

    @Override
    protected void refresh() {
        stationName = "";
        // 气温
        final TextView text_temperature = (TextView) mRootLayout.findViewById(R.id.text_temperature);
        text_temperature.setText("");
        final TextView text_temperature_decimals =
                (TextView) mRootLayout.findViewById(R.id.text_temperature_decimals);
        text_temperature_decimals.setText("");
        // 中文天气
        final TextView text_weather_cn = (TextView) mRootLayout.findViewById(R.id.text_weather_cn);
        text_weather_cn.setText("");
        // 雨量
        final TextView text_rain = (TextView) mRootLayout.findViewById(R.id.text_rain);
        text_rain.setText("");
        // 风速
        final TextView text_wind = (TextView) mRootLayout.findViewById(R.id.text_wind);
        text_wind.setText("");
        // 能见度
        final TextView text_visibility = (TextView) mRootLayout.findViewById(R.id.text_visibility);
        text_visibility.setText("");
        // 空气质量
        final TextView text_station = (TextView) mRootLayout.findViewById(R.id.text_station_name);
        text_station.setText("");
        final TextView text_reflush_time = (TextView) mRootLayout.findViewById(R.id.text_reflush_time);
        text_reflush_time.setText("");

        // 当前城市
        final PackLocalCityMain packCity = ZtqCityDB.getInstance().getCityMain();
        if (packCity == null || TextUtils.isEmpty(packCity.ID)) {
            return;
        }

        /**
         * 获取实况信息
         */
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String url = CONST.BASE_URL+"sstq";
                Log.e("sstq", url);
                try {
                    JSONObject param  = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    JSONObject info = new JSONObject();
                    info.put("stationId", packCity.ID);
                    param.put("paramInfo", info);
                    String json = param.toString();
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
                                    if (!TextUtil.isEmpty(result)) {
                                        Log.e("sstq", result);
                                        try {
                                            JSONObject obj = new JSONObject(result);
                                            if (!obj.isNull("b")) {
                                                JSONObject bobj = obj.getJSONObject("b");
                                                if (!bobj.isNull("sstq")) {
                                                    JSONObject sstqobj = bobj.getJSONObject("sstq");
                                                    if (!sstqobj.isNull("sstq")) {
                                                        PackSstqDown packSstq = new PackSstqDown();
                                                        JSONObject itemObj = sstqobj.getJSONObject("sstq");
                                                        if (!itemObj.isNull("ct")) {
                                                            packSstq.ct = itemObj.getString("ct");
                                                        }
                                                        if (!itemObj.isNull("humidity")) {
                                                            packSstq.humidity = itemObj.getString("humidity");
                                                        }
                                                        if (!itemObj.isNull("rainfall")) {
                                                            packSstq.rainfall_day = itemObj.getString("rainfall");
                                                        }
                                                        if (!itemObj.isNull("wind")) {
                                                            packSstq.wind = itemObj.getString("wind");
                                                        }
                                                        if (!itemObj.isNull("visibility")) {
                                                            packSstq.visibility = itemObj.getString("visibility");
                                                        }
                                                        if (!itemObj.isNull("upt")) {
                                                            packSstq.upt = itemObj.getString("upt");
                                                        }

                                                        // 气温
                                                        String tempStr = packSstq.ct;
                                                        if (!TextUtils.isEmpty(packSstq.ct) && packSstq.ct.indexOf(".") > -1) {
                                                            tempStr = packSstq.ct.substring(0, packSstq.ct.indexOf(".") + 1);
                                                        }
                                                        text_temperature.setText(tempStr);
                                                        tempStr = "";
                                                        //气温小数
                                                        if (!TextUtils.isEmpty(packSstq.ct) && packSstq.ct.indexOf(".") > -1) {
                                                            tempStr = packSstq.ct.substring(packSstq.ct.indexOf(".") + 1, packSstq.ct.length());
                                                        }
                                                        text_temperature_decimals.setText(tempStr);

                                                        mPackWeekUp.setCity(packCity);
                                                        PackMainWeekWeatherDown packMainWeekDown = (PackMainWeekWeatherDown) PcsDataManager.getInstance().getNetPack(mPackWeekUp.getName());
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
                                                            text_humidity.setText("暂无");
                                                        }

                                                        // 雨量
                                                        if (!TextUtils.isEmpty(packSstq.rainfall_day)) {
                                                            text_rain.setText(packSstq.rainfall_day + "mm");
                                                        } else {
                                                            text_rain.setText("暂无");
                                                        }

                                                        // 风速
                                                        if (!TextUtils.isEmpty(packSstq.wind)) {
                                                            text_wind.setText(packSstq.wind + "m/s");
                                                        } else {
                                                            text_wind.setText("暂无");
                                                        }

                                                        // 能见度
                                                        if (!TextUtils.isEmpty(packSstq.visibility)) {
                                                            text_visibility.setText(packSstq.visibility + "m");
                                                        } else {
                                                            text_visibility.setText("暂无");
                                                        }
                                                        Time time = new Time();
                                                        String time_text = "";
                                                        if (!TextUtils.isEmpty(packSstq.upt)) {
                                                            try {
                                                                if (!TextUtils.isEmpty(packSstq.upt)) {
                                                                    String[] str = packSstq.upt.split(" ");
                                                                    time_text = str[1];
                                                                }
//                if (packCity.isFjCity) {
                                                                stationName = packSstq.stationname;
//                } else {
//                    stationName = packCity.NAME;
//                }
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

        /**
         * 获取预警列表信息
         */
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param  = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    JSONObject info = new JSONObject();
                    info.put("stationId", packCity.ID);
                    param.put("paramInfo", info);
                    String json = param.toString();
                    final String url = CONST.BASE_URL+"warningcenterqx_fb";
                    Log.e("warningcenterqx_fb", url);
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
                                    if (!TextUtil.isEmpty(result)) {
                                        Log.e("warningcenterqx_fb", result);
                                        try {
                                            JSONObject obj = new JSONObject(result);
                                            if (!obj.isNull("b")) {
                                                JSONObject bobj = obj.getJSONObject("b");
                                                if (!bobj.isNull("warningcenterqx_fb")) {
                                                    JSONObject fbObj = bobj.getJSONObject("warningcenterqx_fb");
                                                    JSONArray cityArray = null;
                                                    JSONArray proArray = null;
                                                    JSONArray countryArray = null;
                                                    if (!fbObj.isNull("city")) {
                                                        cityArray = fbObj.getJSONArray("city");
                                                    }
                                                    if (!fbObj.isNull("province")) {
                                                        proArray = fbObj.getJSONArray("province");
                                                    }
                                                    if (!fbObj.isNull("county")) {
                                                        countryArray = fbObj.getJSONArray("county");
                                                    }

                                                    if (cityArray == null && proArray == null && countryArray == null) {
                                                        ImageView widget_title_icon = (ImageView) mRootLayout.findViewById(R.id.widget_title_icon);
                                                        widget_title_icon.setVisibility(View.INVISIBLE);
                                                    }

                                                    if (cityArray.length() == 0 && proArray.length() == 0 && countryArray.length() == 0) {
                                                        ImageView widget_title_icon = (ImageView) mRootLayout.findViewById(R.id.widget_title_icon);
                                                        widget_title_icon.setVisibility(View.INVISIBLE);
                                                    } else {
                                                        setRead(cityArray, proArray, countryArray);
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

    private void setRead(JSONArray cityArray, JSONArray proArray, JSONArray countryArray) {
        is_readed = true;
        String id = "";
        ImageView widget_title_icon = (ImageView) mRootLayout.findViewById(R.id.widget_title_icon);
        if (cityArray.length() != 0) {
            for (int i = 0; i < cityArray.length(); i++) {
                try {
                    JSONObject itemObj = cityArray.getJSONObject(i);
                    String itemId = "";
                    if (!itemObj.isNull("id")) {
                        itemId = itemObj.getString("id");
                    }
                    id = SharedPreferencesUtil.getData(itemId, "");
                    String date2 = itemObj.getString("yj_time") + ":00";
                    date2 = date2.replace("年", "-");
                    date2 = date2.replace("月", "-");
                    date2 = date2.replace("日", " ");
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    int days = 0;
                    try {
                        Date date3 = format.parse(date2);
                        String date = format.format(new Date());
                        Date date4 = format.parse(date);
                        days = differentDaysByMillisecond(date3, date4);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (days < 1) {
                        if (!TextUtils.isEmpty(id)) {
                            is_readed = true;
                            widget_title_icon.setVisibility(View.INVISIBLE);
                        } else {
                            widget_title_icon.setVisibility(View.VISIBLE);
                            is_readed = false;
                            break;
                        }
                    } else {
                        is_readed = true;
                        widget_title_icon.setVisibility(View.INVISIBLE);
                        SharedPreferencesUtil.putData(itemId, itemId);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        if (proArray.length() != 0) {
            for (int i = 0; i < proArray.length(); i++) {
                try {
                    JSONObject itemObj = proArray.getJSONObject(i);
                    String itemId = "";
                    if (!itemObj.isNull("id")) {
                        itemId = itemObj.getString("id");
                    }
                    id = SharedPreferencesUtil.getData(itemId, "");
                    if (is_readed) {
                        String date2 = itemObj.getString("yj_time") + ":00";
                        date2 = date2.replace("年", "-");
                        date2 = date2.replace("月", "-");
                        date2 = date2.replace("日", " ");
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        int days = 0;
                        try {
                            Date date3 = format.parse(date2);
                            String date = format.format(new Date());
                            Date date4 = format.parse(date);
                            days = differentDaysByMillisecond(date3, date4);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (days < 1) {
                            if (!TextUtils.isEmpty(id)) {
                                is_readed = true;
                                widget_title_icon.setVisibility(View.INVISIBLE);
                            } else {
                                widget_title_icon.setVisibility(View.VISIBLE);
                                is_readed = false;
                                break;
                            }
                        } else {
                            is_readed = true;
                            widget_title_icon.setVisibility(View.INVISIBLE);
                            SharedPreferencesUtil.putData(itemId, itemId);
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        if (countryArray.length() != 0) {
            for (int i = 0; i < countryArray.length(); i++) {
                try {
                    JSONObject itemObj = countryArray.getJSONObject(i);
                    String itemId = "";
                    if (!itemObj.isNull("id")) {
                        itemId = itemObj.getString("id");
                    }
                    id = SharedPreferencesUtil.getData(itemId, "");
                    if (is_readed) {
                        String date2 = itemObj.getString("yj_time") + ":00";
                        date2 = date2.replace("年", "-");
                        date2 = date2.replace("月", "-");
                        date2 = date2.replace("日", " ");
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        int days = 0;
                        try {
                            Date date3 = format.parse(date2);
                            String date = format.format(new Date());
                            Date date4 = format.parse(date);
                            days = differentDaysByMillisecond(date3, date4);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (days < 1) {
                            if (!TextUtils.isEmpty(id)) {
                                is_readed = true;
                                widget_title_icon.setVisibility(View.INVISIBLE);
                            } else {
                                widget_title_icon.setVisibility(View.VISIBLE);
                                is_readed = false;
                                break;
                            }
                        } else {
                            is_readed = true;
                            widget_title_icon.setVisibility(View.INVISIBLE);
                            SharedPreferencesUtil.putData(itemId, itemId);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
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


    /**
     * 按钮监听第1行
     */
    private View.OnClickListener onClickRow1 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent it = new Intent();
            PackLocalCityMain cityMain = ZtqCityDB.getInstance().getCityMain();
            switch (v.getId()) {
                case R.id.layout_warn:
                    // 预警中心
                    gotoWarn(cityMain.isFjCity);
                    break;
                case R.id.layout_wind:
                    //风雨查询
                    it.setClass(mActivity, ActivityLiveQuery.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("city", cityMain);
                    it.putExtras(bundle);
                    mActivity.startActivity(it);
                    break;
                case R.id.layout_air:
                    // 空气质量
                    // 当前城市

                    PackLocalCityMain packCity = ZtqCityDB.getInstance().getCityMain();
                    if (packCity == null || packCity.ID == null) {
                        return;
                    }
                    if (packCity.isFjCity) {
                        it.setClass(mActivity, ActivityAirQualitySH.class);
                        it.putExtra("id", cityMain.ID);
                        it.putExtra("name", cityMain.NAME);
                    } else {
                        ActivityAirQualityQuery.setCity(packCity.ID, packCity.CITY);
                        it.putExtra("id", packCity.ID);
                        String str[] = packCity.SHOW_NAME.split("-");
                        it.putExtra("name", str[0]);
                        it.setClass(mActivity, ActivityAirQualityQuery.class);
                    }

                    mActivity.startActivity(it);

                    break;
                case R.id.layout_server:
                    if (cityMain.isFjCity) {
                        // 决策气象
                        PackLocalUser info = ZtqCityDB.getInstance().getMyInfo();
                        if (TextUtils.isEmpty(info.user_id)) {
                            ServiceLoginTool.getInstance().createAlreadyLoginedWithFragment(mActivity, mFragment);
                        } else {
                            ServiceLoginTool.getInstance().reqLoginQuery();
                        }
                    } else {
                        Toast.makeText(mActivity, "暂无决策报告", Toast.LENGTH_LONG).show();
                    }
                    break;
                case R.id.layout_temperature:
                    String stationName = "";
                    // 当前城市
                    PackLocalCityMain city = ZtqCityDB.getInstance().getCityMain();
                    if (city == null || city.ID == null) {
                        return;
                    }
                    if (TextUtils.isEmpty(city.ID)) {
                        return;
                    }
                    //实时天气
                    PackSstqUp mPackSstqUp = new PackSstqUp();
                    mPackSstqUp.area = city.ID;
                    PackSstqDown packSstq =
                            (PackSstqDown) PcsDataManager.getInstance().getNetPack(mPackSstqUp
                                    .getName());
                    if (packSstq != null && !TextUtils.isEmpty(packSstq.stationname)) {
                        stationName = packSstq.stationname;
                    } else {
                        stationName = city.NAME;
                    }
                    Intent intent = new Intent(mActivity, ActivityLiveQueryDetail.class);
                    intent.putExtra("stationName", stationName);
                    intent.putExtra("item", "temp");
                    mActivity.startActivity(intent);
                    break;
//                case R.id.layout_near:
//                    Intent intents = new Intent(mActivity, ActivityNearReport.class);
//                    mActivity.startActivity(intents);
//                    break;
//                //分享
//                case R.id.lay_bt_share:
//                    mActivity.showProgressDialog();
//                    clickShare();
//                    break;
//                //实景
//                case R.id.lay_bt_real:
//                    clickUserGuide();
//                    break;
            }
        }
    };

    //点击预警中心
    private void gotoWarn(boolean isFjCity) {
        Intent intent = new Intent(mActivity, ActivityWarningCenterNotFjCity.class);
        mActivity.startActivity(intent);
    }

    private View view;
    private PcsDataBrocastReceiver mReceiver = new PcsDataBrocastReceiver() {
        @Override
        public void onReceive(String nameStr, String errorStr) {
            if (TextUtils.isEmpty(nameStr)) {
                return;
            }
//            if (nameStr.startsWith(PackVoiceUp.NAME)) {
//                PcsDataBrocastReceiver.unregisterReceiver(mActivity, mReceiver);
//                PackVoiceDown down = (PackVoiceDown) PcsDataManager.getInstance().getNetPack
//                (PackVoiceUp.NAME);
//                mActivity.dismissProgressDialog();
//                if (down == null) {
//                    return;
//                }
//                lists.clear();
//                String str = down.desc.replace("-", "零下");
//                //格式化语音报读数字
//                if (str.contains("12") || str.contains("22") || str.contains("32") || str
//                .contains("42") || str
//                        .contains("12.2") || str
//                        .contains("22.2") || str.contains("32.2") || str.contains("42.2")) {
//
//                } else {
//                    if (str.contains("2.2")) {
//                        str = str.replace("2.2", "二点二");
//                    } else {
//                        if (str.contains("2.")) {
//                            str = str.replace("2.", "二点");
//                        }
//                    }
//                }
//                str = str.replace(".2", "点二");
//                str = str.replace(".", "点");
//                if (dialogVoiceButton == null) {
//                    view = LayoutInflater.from(mActivity).inflate(
//                            R.layout.dialog_message, null);
//                    ((TextView) view.findViewById(R.id.dialogmessage))
//                            .setText(down.desc);
//                    dialogVoiceButton = new DialogVoiceButton(mActivity,
//                            view, "关闭", new DialogFactory.DialogListener() {
//                        @Override
//                        public void click(String str) {
//                            if (str.equals("关闭")) {
//                                dialogVoiceButton.dismiss();
//                            }
//                        }
//                    });
//                } else {
//                    ((TextView) view.findViewById(R.id.dialogmessage))
//                            .setText(down.desc);
//                }
//
//                if (!dialogVoiceButton.isShowing()) {
//                    dialogVoiceButton.show();
//                }
//                voiceTool.readResult(str);
//            }

        }
    };


    /* 判断是否是处于wifi状态下*/
    protected boolean isWiFiNewWord() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) mActivity.getSystemService(Context
                        .CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI;
    }


    //讯飞语音开发

//    private DialogVoiceButton dialogVoiceButton;

//    public void setImageChange(int volume) {
//        if (isPopVoice) {
//            if (volume == 0) {
//                iv_voice.setBackgroundResource(R.drawable
//                        .mic_0);
//            } else if (volume < 5) {
//                iv_voice.setBackgroundResource(R.drawable
//                        .mic_1);
//            } else if (volume < 10) {
//                iv_voice.setBackgroundResource(R.drawable
//                        .mic_2);
//            } else if (volume < 15) {
//                iv_voice.setBackgroundResource(R.drawable
//                        .mic_3);
//            } else if (volume < 20) {
//                iv_voice.setBackgroundResource(R.drawable
//                        .mic_4);
//            } else if (volume >= 20) {
//                iv_voice.setBackgroundResource(R.drawable
//                        .mic_5);
//            }
//        }
//
//    }

//    private PackVoiceUp voiceUp = new PackVoiceUp();
//    private List<PackLocalCity> lists = new ArrayList<>();

    /**
     * 打印出语音转换后的汉字
     *
     * @param
     */
//    private String errorString = "没查到该城市天气信息";
//
//    public void printResult(RecognizerResult results) {
//        String text = JsonParser.parseIatResult(results.getResultString());
//
//        String sn = null;
//        // 读取json结果中的sn字段
//        try {
//            JSONObject resultJson = new JSONObject(results.getResultString());
//            sn = resultJson.optString("sn");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        mIatResults.put(sn, text);
//
//        StringBuffer resultBuffer = new StringBuffer();
//        for (String key : mIatResults.keySet()) {
//            resultBuffer.append(mIatResults.get(key));
//        }
//        lists.clear();
//        lists.addAll(ZtqCityDB.getInstance().searchCityConfirm(resultBuffer.toString()));
//        if (TextUtils.isEmpty(resultBuffer.toString())) {
//            voiceTool.readResult(errorString);
//        } else if (lists.size() == 0 || lists == null) {
//            mActivity.showProgressDialog();
//            voiceTool.readResult(errorString);
//            mActivity.dismissProgressDialog();
//        } else {
//            isPopVoice = false;
//            mActivity.showProgressDialog();
//            PcsDataBrocastReceiver.registerReceiver(mActivity, mReceiver);
//            voiceUp.county_id = lists.get(0).ID;
//            PcsDataDownload.addDownload(voiceUp);
//        }
//    }

    //点击分享
    private void clickShare() {
        PackLocalCity packCity = ZtqCityDB.getInstance().getCityMain();
        List<WeekWeatherInfo> weatherList = getWeatherList();
        if (packCity == null || TextUtils.isEmpty(packCity.ID) || weatherList == null) {
            return;
        }
        StringBuffer shareStr = new StringBuffer(packCity.NAME + ":");
        if (weatherList.size() > 1) {
            shareStr.append(weatherList.get(0).gdt + ",");
            shareStr.append(weatherList.get(0).weather + ",");
            shareStr.append(weatherList.get(0).higt + "~");
            shareStr.append(weatherList.get(0).lowt + "°C,");
        }
        if (weatherList.size() > 2) {
            shareStr.append(weatherList.get(1).gdt + ",");
            shareStr.append(weatherList.get(1).weather + ",");
            shareStr.append(weatherList.get(1).higt + "~");
            shareStr.append(weatherList.get(1).lowt + "°C,");
        }
        if (weatherList.size() > 3) {
            shareStr.append(weatherList.get(2).gdt + ",");
            shareStr.append(weatherList.get(2).weather + ",");
            shareStr.append(weatherList.get(2).higt + "~");
            shareStr.append(weatherList.get(2).lowt + "°C。");
        }
        if (weatherList.size() > 4) {
            shareStr.append(weatherList.get(3).gdt + ",");
            shareStr.append(weatherList.get(3).weather + ",");
            shareStr.append(weatherList.get(3).higt + "~");
            shareStr.append(weatherList.get(3).lowt + "°C,");
        }
        if (weatherList.size() > 5) {
            shareStr.append(weatherList.get(4).gdt + ",");
            shareStr.append(weatherList.get(4).weather + ",");
            shareStr.append(weatherList.get(4).higt + "~");
            shareStr.append(weatherList.get(4).lowt + "°C,");
        }
        if (weatherList.size() > 6) {
            shareStr.append(weatherList.get(5).gdt + ",");
            shareStr.append(weatherList.get(5).weather + ",");
            shareStr.append(weatherList.get(5).higt + "~");
            shareStr.append(weatherList.get(5).lowt + "°C。");
        }
        if (weatherList.size() >= 7) {
            shareStr.append(weatherList.get(6).gdt + ",");
            shareStr.append(weatherList.get(6).weather + ",");
            shareStr.append(weatherList.get(6).higt + "~");
            shareStr.append(weatherList.get(6).lowt + "°C。");
        }

        Bitmap bitmap = BitmapUtil.takeScreenShot(mActivity);
        //ShareUtil.share(mActivity, shareStr.toString() + mActivity.getResources().getString(R
        // .string.share_add),
        // bitmap);
        bitmap = ZtqImageTool.getInstance().stitchQR(mActivity, bitmap);
        PackShareAboutDown shareDown =
                (PackShareAboutDown) PcsDataManager.getInstance().getNetPack(PackShareAboutUp
                        .getNameCom());

        String shareContnet = "";
        if (shareDown != null) {
            shareContnet = shareStr + shareDown.share_content;
        }
        mActivity.dismissProgressDialog();
        ShareTools.getInstance(mActivity).setShareContent("分享天气", shareContnet, bitmap, "0").showWindow(mRootLayout);
    }

    /**
     * 取一周天气列表
     *
     * @return
     */
    private List<WeekWeatherInfo> getWeatherList() {
        // 当前城市
        PackLocalCity packCity = ZtqCityDB.getInstance().getCityMain();
        if (packCity == null || packCity.ID == null) {
            return new ArrayList<WeekWeatherInfo>();
        }
        mPackWeekUp.setCity(packCity);
        PackMainWeekWeatherDown packMainWeekDown =
                (PackMainWeekWeatherDown) PcsDataManager.getInstance().getNetPack
                        (mPackWeekUp.getName());

        return packMainWeekDown.getWeek();
    }

    //实景
    private void clickUserGuide() {

        Intent it = new Intent();
        it.setClass(mActivity, ActivityPhotoShow.class);
        it.putExtra(ActivityPhotoShow.CITY_ID, getPhotoCityId());
        mActivity.startActivity(it);

    }


    /**
     * 获取实景地区ID
     *
     * @return
     */
    public String getPhotoCityId() {
        PackLocalCityMain cityMain = ZtqCityDB.getInstance().getCityMain();
        return cityMain.ID;
    }

//    public void speakBegin() {
//        main_voice.setBackgroundResource(R.drawable.btn_mainvoice_sel);
//        isPlay = true;
//    }
//
//    public void completeRead() {
//        main_voice.setBackgroundResource(R.drawable.btn_mainvoice_nor);
//        isPlay = false;
//        clickNum = 1;
//        if (dialogVoiceButton != null) {
//            dialogVoiceButton.dismiss();
//        }
//    }

//    private ImageView iv_voice;

    /**
     * 关闭弹出框
     */
//    public void dismissPopupWindow() {
//        if (popVoice != null && popVoice.isShowing()) {
//            popVoice.dismiss();
//        }
//    }

//    boolean flag = false;
//    private View.OnTouchListener touchListener = new View.OnTouchListener() {
//        @Override
//        public boolean onTouch(View view, MotionEvent event) {
//            switch (view.getId()) {
//                case R.id.main_voice:
//                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                        flag = checkAudioPermissions();
//                        if(!flag) {
//                            break;
//                        }
//                        voiceTool.setRecParam();
//                        if (clickNum == 0) {
//                            voiceTool.readResult("您好，欢迎使用气象语音服务。请问，您要查询哪个城市的天气？");
//                            clickNum = 1;
//                        } else {
//                            if (isPlay) {
//                                voiceTool.mTts.stopSpeaking();
//                                main_voice.setBackgroundResource(R.drawable.btn_mainvoice_nor);
//                                clickNum = 2;
//                                isPlay = false;
//                            } else {
//                                clickNum = 1;
//                                isPopVoice = true;
//                                voiceTool.mIat.startListening
//                                        (voiceTool.mRecognizerListener);
//                                if (!popVoice.isShowing()) {
//                                    popVoice.showAtLocation(mActivity.getWindow().getDecorView
//                                    (), Gravity.CENTER, 0,
//                                            -250);
//                                }
////                            }
//                            }
//                        }
//                    }
//                    if (event.getAction() == MotionEvent.ACTION_UP) {
//                        voiceTool.mRecognizerListener.onEndOfSpeech();
//                    }
//                    break;
//            }
//            return true;
//        }
//    };

    String[] nessaryPermissions = {
            Manifest.permission.RECORD_AUDIO
    };
//
//    private boolean checkAudioPermissions() {
//        return PermissionsTools.checkPermissions(mFragment, nessaryPermissions, MyConfigure
//        .REQUEST_PERMISSION_AUDIO);
//    }

    @Override
    public void checkPermission(String[] permissions, int[] grantResults) {
        PermissionsTools.verifyNessaryPermissions(nessaryPermissions, permissions, grantResults);
    }
}
