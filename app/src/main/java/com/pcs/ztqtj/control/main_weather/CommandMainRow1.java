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
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.PackWarnWeatherDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.PackWarnWeatherUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.WarnCenterYJXXGridBean;
import com.pcs.lib_ztqfj_v2.model.pack.net.week.PackMainWeekWeatherDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.week.PackMainWeekWeatherUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.week.WeekWeatherInfo;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.inter.InterfaceShowBg;
import com.pcs.ztqtj.control.tool.PermissionsTools;
import com.pcs.ztqtj.control.tool.ServiceLoginTool;
import com.pcs.ztqtj.control.tool.ShareTools;
import com.pcs.ztqtj.control.tool.SharedPreferencesUtil;
import com.pcs.ztqtj.control.tool.VoiceTool;
import com.pcs.ztqtj.control.tool.ZtqImageTool;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.view.activity.ActivityMain;
import com.pcs.ztqtj.view.activity.livequery.ActivityLiveQuery;
import com.pcs.ztqtj.view.activity.livequery.ActivityLiveQueryDetail;
import com.pcs.ztqtj.view.activity.newairquality.ActivityAirQualityQuery;
import com.pcs.ztqtj.view.activity.photoshow.ActivityPhotoShow;
import com.pcs.ztqtj.view.activity.warn.ActivityWarningCenterNotFjCity;
import com.pcs.ztqtj.view.fragment.airquality.ActivityAirQualitySH;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

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
    //实时天气
    private PackSstqUp mPackSstqUp = new PackSstqUp();
    //一周天气
    private PackMainWeekWeatherUp mPackWeekUp = new PackMainWeekWeatherUp();
    //预警未读消息
    private PackWarnWeatherUp packWarnWeatherUp = new PackWarnWeatherUp();
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
        TextView text_temperature = (TextView) mRootLayout.findViewById(R.id.text_temperature);
        text_temperature.setText("");
        TextView text_temperature_decimals =
                (TextView) mRootLayout.findViewById(R.id.text_temperature_decimals);
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
        // 能见度
        TextView text_visibility = (TextView) mRootLayout.findViewById(R.id.text_visibility);
        text_visibility.setText("");
        // 空气质量
        TextView text_station = (TextView) mRootLayout.findViewById(R.id.text_station_name);
        text_station.setText("");
        TextView text_reflush_time = (TextView) mRootLayout.findViewById(R.id.text_reflush_time);
        text_reflush_time.setText("");
        // 当前城市
        PackLocalCityMain packCity = ZtqCityDB.getInstance().getCityMain();
        if (packCity == null || TextUtils.isEmpty(packCity.ID)) {
            return;
        }
        String tempStr;
        // -------当前天气
        mPackSstqUp.area = packCity.ID;
        PackSstqDown packSstq =
                (PackSstqDown) PcsDataManager.getInstance().getNetPack(mPackSstqUp.getName());
        if (packSstq == null) {
            packSstq = new PackSstqDown();


        }
        // 气温
        tempStr = packSstq.ct;
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
        PackMainWeekWeatherDown packMainWeekDown =
                (PackMainWeekWeatherDown) PcsDataManager.getInstance().getNetPack
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
//        mPackAirUp.setCity(packCity);
//        mPackAirUp.type = "1";
//        PackAirInfoSimpleDown packAir = (PackAirInfoSimpleDown) PcsDataManager.getInstance()
//        .getNetPack(mPackAirUp
//                .getName());
//        if (packAir != null) {
//            if (!TextUtils.isEmpty(packAir.airInfoSimple.aqi)) {
//                int color = AirQualityTool.getInstance().getAqiColor(
//                        Integer.valueOf(packAir.airInfoSimple.aqi));
//                text_air_num.setText(packAir.airInfoSimple.aqi);
//                text_air_num.setTextColor(color);
//            } else {
//                text_air_num.setText("暂无");
//                text_air_num.setTextColor(mActivity.getResources().getColor(android.R.color
//                .white));
//            }
//        }

        String cityid = "";

        PackLocalCityMain cityMain = ZtqCityDB.getInstance().getCityMain();
//        if (cityMain != null) {
//            if (cityMain.isFjCity) {
//                cityid = cityMain.PARENT_ID;
//            } else {
                cityid = cityMain.ID;
//            }
//        }
        packWarnWeatherUp.areaid = cityid;

        PackWarnWeatherDown packWarnWeatherDown =
                (PackWarnWeatherDown) PcsDataManager.getInstance().getNetPack
                        (packWarnWeatherUp.getName());
        if (packWarnWeatherDown == null) {
            ImageView widget_title_icon =
                    (ImageView) mRootLayout.findViewById(R.id.widget_title_icon);
            widget_title_icon.setVisibility(View.INVISIBLE);
            return;
        }

        if (packWarnWeatherDown.city.size() == 0 && packWarnWeatherDown.county.size() == 0 && packWarnWeatherDown.province.size() == 0) {
            ImageView widget_title_icon =
                    (ImageView) mRootLayout.findViewById(R.id.widget_title_icon);
            widget_title_icon.setVisibility(View.INVISIBLE);
        } else {
            setRead(packWarnWeatherDown);

        }
    }


    public void setRead(PackWarnWeatherDown packWarnWeatherDown) {
        is_readed = true;
        String id = "";
        ImageView widget_title_icon = (ImageView) mRootLayout.findViewById(R.id.widget_title_icon);
        if (packWarnWeatherDown.city.size() != 0) {
            for (int i = 0; i < packWarnWeatherDown.city.size(); i++) {
                id = SharedPreferencesUtil.getData(packWarnWeatherDown.city.get(i).id, "");
                String date2 = packWarnWeatherDown.city.get(i).put_time + ":00";
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
                    SharedPreferencesUtil.putData(packWarnWeatherDown.city.get(i).id,
                            packWarnWeatherDown.city.get(i)
                                    .id);
                }
            }
        }
        if (packWarnWeatherDown.province.size() != 0) {
            for (WarnCenterYJXXGridBean bean : packWarnWeatherDown.province) {
                id = SharedPreferencesUtil.getData(bean.id, "");
                if (is_readed) {
                    String date2 = bean.put_time + ":00";
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
                        SharedPreferencesUtil.putData(bean.id, bean.id);
                    }

                }
            }
        }
        if (packWarnWeatherDown.county.size() != 0) {
            for (int i = 0; i < packWarnWeatherDown.county.size(); i++) {
                id = SharedPreferencesUtil.getData(packWarnWeatherDown.county.get(i).id, "");
                if (is_readed) {
                    String date2 = packWarnWeatherDown.county.get(i).put_time + ":00";
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
                        SharedPreferencesUtil.putData(packWarnWeatherDown.county.get(i).id,
                                packWarnWeatherDown
                                        .county.get(i).id);
                    }
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

//                    mPackAirUp.setCity(packCity);
//                    mPackAirUp.type = "1";
//                    PackAirInfoSimpleDown packAirDown = (PackAirInfoSimpleDown) PcsDataManager
//                    .getInstance()
//                            .getNetPack(mPackAirUp.getName());
//                    if (packAirDown == null || TextUtils.isEmpty(packAirDown.airInfoSimple
//                    .quality)) {
//                        break;
//                    }
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
