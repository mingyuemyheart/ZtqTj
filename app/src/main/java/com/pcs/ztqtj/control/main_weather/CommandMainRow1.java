package com.pcs.ztqtj.control.main_weather;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pcs.lib.lib_pcs_v3.model.image.ImageFetcher;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityMain;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalUser;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackSstqDown;
import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.inter.InterfaceShowBg;
import com.pcs.ztqtj.control.tool.PermissionsTools;
import com.pcs.ztqtj.control.tool.ServiceLoginTool;
import com.pcs.ztqtj.control.tool.SharedPreferencesUtil;
import com.pcs.ztqtj.control.tool.utils.TextUtil;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.util.OkHttpUtil;
import com.pcs.ztqtj.view.activity.ActivityMain;
import com.pcs.ztqtj.view.activity.livequery.ActivityLiveQuery;
import com.pcs.ztqtj.view.activity.livequery.ActivityLiveQueryDetail;
import com.pcs.ztqtj.view.activity.newairquality.ActivityAirQualityQuery;
import com.pcs.ztqtj.view.activity.warn.ActivityWarningCenterNotFjCity;
import com.pcs.ztqtj.view.fragment.airquality.ActivityAirQualitySH;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 首页-实况
 */
public class CommandMainRow1 extends CommandMainBase {

    private ActivityMain mActivity;
    private ViewGroup mRootLayout;
    private Fragment mFragment;
    private boolean is_readed = true;

    private TextView text_temperature,text_temperature_decimals,text_humidity,text_rain,text_wind,text_visibility,text_station;
    private ImageView widget_title_icon;
    private String stationName = "";
    private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

    public CommandMainRow1(Activity activity, ViewGroup rootLayout, ImageFetcher imageFetcher, InterfaceShowBg showBg, Fragment fragment) {
        mActivity = (ActivityMain) activity;
        mRootLayout = rootLayout;
        mFragment = fragment;
    }

    @Override
    protected void init() {
        View mRowView = LayoutInflater.from(mActivity).inflate(R.layout.item_home_weather_1, null);
        mRowView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mRootLayout.addView(mRowView);

        widget_title_icon = mRootLayout.findViewById(R.id.widget_title_icon);

        // 预警中心
        View btnWarn = mRowView.findViewById(R.id.layout_warn);
        btnWarn.setOnClickListener(onClickRow1);
        // 实况查询
        View btnWind = mRowView.findViewById(R.id.layout_wind);
        btnWind.setOnClickListener(onClickRow1);
        // 空气质量
        View layoutAir = mRowView.findViewById(R.id.layout_air);
        layoutAir.setOnClickListener(onClickRow1);
        // 决策气象
        View layoutServer = mRowView.findViewById(R.id.layout_server);
        layoutServer.setOnClickListener(onClickRow1);
        // 整点实况区域
        View clickinteger = mRowView.findViewById(R.id.layout_temperature);
        clickinteger.setOnClickListener(onClickRow1);

        text_temperature = mRootLayout.findViewById(R.id.text_temperature);
        text_temperature_decimals = mRootLayout.findViewById(R.id.text_temperature_decimals);
        text_humidity = mRootLayout.findViewById(R.id.text_humidity);
        text_rain = mRootLayout.findViewById(R.id.text_rain);
        text_wind = mRootLayout.findViewById(R.id.text_wind);
        text_visibility = mRootLayout.findViewById(R.id.text_visibility);
        text_station = mRootLayout.findViewById(R.id.text_station_name);
    }

    @Override
    protected void refresh() {
        stationName = "";
        text_temperature.setText("");
        text_temperature_decimals.setText("");
        text_humidity.setText("");
        text_rain.setText("");
        text_wind.setText("");
        text_visibility.setText("");
        text_station.setText("");

        // 当前城市
        final PackLocalCityMain packCity = ZtqCityDB.getInstance().getCityMain();
        if (packCity == null || TextUtils.isEmpty(packCity.ID)) {
            return;
        }
        okHttpSstq(packCity.ID, packCity.NAME);
        okHttpWarningList(packCity.ID);
    }

    /**
     * 通过时间秒毫秒数判断两个时间的间隔
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
            PackLocalCityMain cityMain = ZtqCityDB.getInstance().getCityMain();
            Intent intent;
            switch (v.getId()) {
                case R.id.layout_warn:
                    // 预警中心
                    intent = new Intent(mActivity, ActivityWarningCenterNotFjCity.class);
                    intent.putExtra("isDisWaring", true);
                    mActivity.startActivity(intent);
                    break;
                case R.id.layout_wind:
                    //实况查询
                    if (cityMain == null || cityMain.ID == null) {
                        return;
                    }
                    intent = new Intent(mActivity, ActivityLiveQuery.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("city", cityMain);
                    intent.putExtras(bundle);
                    mActivity.startActivity(intent);
                    break;
                case R.id.layout_air:
                    // 空气质量
                    if (cityMain == null || cityMain.ID == null) {
                        return;
                    }
                    if (cityMain.isFjCity) {
                        intent = new Intent(mActivity, ActivityAirQualitySH.class);
                        intent.putExtra("id", cityMain.ID);
                        intent.putExtra("name", cityMain.NAME);
                    } else {
                        ActivityAirQualityQuery.setCity(cityMain.ID, cityMain.CITY);
                        intent = new Intent(mActivity, ActivityAirQualityQuery.class);
                        intent.putExtra("id", cityMain.ID);
                        String str[] = cityMain.SHOW_NAME.split("-");
                        intent.putExtra("name", str[0]);
                    }
                    mActivity.startActivity(intent);
                    break;
                case R.id.layout_server:
                    if (cityMain == null || cityMain.ID == null) {
                        return;
                    }
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
                    intent = new Intent(mActivity, ActivityLiveQueryDetail.class);
                    intent.putExtra("stationName", stationName);
                    intent.putExtra("item", "temp");
                    mActivity.startActivity(intent);
                    break;
            }
        }
    };

    /* 判断是否是处于wifi状态下*/
    protected boolean isWiFiNewWord() {
        ConnectivityManager connectivityManager = (ConnectivityManager) mActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI;
    }

    /**
     * 获取实景地区ID
     * @return
     */
    public String getPhotoCityId() {
        PackLocalCityMain cityMain = ZtqCityDB.getInstance().getCityMain();
        return cityMain.ID;
    }

    private String[] nessaryPermissions = {
            Manifest.permission.RECORD_AUDIO
    };

    @Override
    public void checkPermission(String[] permissions, int[] grantResults) {
        PermissionsTools.verifyNessaryPermissions(nessaryPermissions, permissions, grantResults);
    }

    /**
     * 获取实况信息
     */
    private void okHttpSstq(final String stationId, final String sName) {
        mActivity.showProgressDialog();
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
                    Log.e("sstq", json);
                    final String url = CONST.BASE_URL+"sstq";
                    Log.e("sstq", url);
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
                                    Log.e("sstq", result);
                                    mActivity.dismissProgressDialog();
                                    if (!TextUtil.isEmpty(result)) {
                                        try {
                                            JSONObject obj = new JSONObject(result);
                                            if (!obj.isNull("b")) {
                                                JSONObject bobj = obj.getJSONObject("b");
                                                if (!bobj.isNull("sstq")) {
                                                    JSONObject sstqobj = bobj.getJSONObject("sstq");
                                                    if (!sstqobj.isNull("sstq")) {
                                                        PackSstqDown packSstq = new PackSstqDown();
                                                        packSstq.fillData(sstqobj.toString());

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

                                                        // 湿度
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
                                                        String time_text = "";
                                                        if (!TextUtils.isEmpty(packSstq.upt)) {
                                                            try {
                                                                if (!TextUtils.isEmpty(packSstq.upt)) {
                                                                    String[] str = packSstq.upt.split(" ");
                                                                    time_text = str[1];
                                                                }
                                                                stationName = packSstq.stationname;
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        } else {
                                                            stationName = sName;
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
    }

    /**
     * 获取预警列表信息
     */
    private void okHttpWarningList(final String stationId) {
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
                                                    JSONArray cityArray = new JSONArray();
                                                    JSONArray proArray = new JSONArray();
                                                    JSONArray countryArray = new JSONArray();
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
                                                        widget_title_icon.setVisibility(View.INVISIBLE);
                                                    }
                                                    if (cityArray.length() == 0 && proArray.length() == 0 && countryArray.length() == 0) {
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

}
