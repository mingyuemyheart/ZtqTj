package com.pcs.ztqtj.control.tool;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.view.activity.ActivityMain;

import java.io.InputStream;
import java.util.Map;

/**
 * 通知栏管理
 */
public class MNotification {
    // 通知管理器
    private NotificationManager notificationManager;
    private Notification baseNF;
    private Builder mBuilder;
    private Context context;
    private int Notification_ID_BASE = 110;
    private static final String CHANNEL_ID = "ZTQ_NOTIFICATION_CHANNEL_ID";
    private static final String CHANNEL_NAME = "天津气象推送通道";
    /**
     * NotificationCompat 构造器
     */

    private Map<String, String> dataMap;

    private String TYPE = "";

    public int getNotification_ID_BASE() {
        return Notification_ID_BASE;
    }

    private Class nextclass;

    public MNotification(Context context, Map<String, String> dataMap,
                         Class nextclass) {
        this.context = context;
        notificationManager = (NotificationManager) context
                .getSystemService(context.NOTIFICATION_SERVICE);

        this.dataMap = dataMap;
        this.nextclass = nextclass;
    }

    public MNotification(Context context, Map<String, String> dataMap) {
        this.context = context;
        notificationManager = (NotificationManager) context
                .getSystemService(context.NOTIFICATION_SERVICE);

        this.dataMap = dataMap;
    }

    /**
     * @获取默认的pendingIntent,为了防止2.3及以下版本报错
     * @flags属性: 在顶部常驻:Notification.FLAG_ONGOING_EVENT 点击去除：
     * Notification.FLAG_AUTO_CANCEL
     */
    public PendingIntent getDefalutIntent(int flags) {
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1,
                new Intent(), flags);
        return pendingIntent;
    }

    public void setNotification_ID_BASE(int notification_ID_BASE) {
        Notification_ID_BASE = notification_ID_BASE;
    }

    /**
     * 默认自定义通知栏
     */
    public void showDefaultNotify() {
        TYPE = dataMap.get("TYPE").toString();
        Log.d("data", dataMap.toString());
        // 先设定RemoteViews
        RemoteViews view_warn = new RemoteViews(context.getPackageName(),
                R.layout.notification_warn_layout);

        view_warn.setImageViewResource(R.id.warn_icon, R.drawable.ic_launcher);

        view_warn.setTextViewText(R.id.tv_warn_title, dataMap.get("TITLE")
                .toString());
        view_warn.setTextViewText(R.id.tv_warn_and, "@天津气象");
        view_warn.setTextViewText(R.id.tv_warn_content, dataMap.get("CONTENT")
                .toString());
        view_warn.setTextColor(R.id.tv_warn_title, notification_text_color);
        view_warn.setTextColor(R.id.tv_warn_and, notification_text_color);
        view_warn.setTextColor(R.id.tv_warn_content, notification_text_color);

        mBuilder = new Builder(context);
        mBuilder.setContent(view_warn)
                .setContentIntent(
                        getDefalutIntent(Notification.FLAG_AUTO_CANCEL))
                .setWhen(System.currentTimeMillis())
                // 通知产生的时间，会在通知信息里显示
                .setTicker(dataMap.get("TITLE").toString())
                .setPriority(Notification.PRIORITY_DEFAULT)// 设置该通知优先级
                .setOngoing(false)// 不是正在进行的 true为正在进行 效果和.flag一样
                .setSmallIcon(R.drawable.ic_launcher);
        Notification notify = mBuilder.build();
        notify.contentView = view_warn;
        notify.contentIntent = getPendIntent();
        notify.contentIntent = getDefalutIntent(Notification.FLAG_AUTO_CANCEL);
        notificationManager.notify(Notification_ID_BASE, notify);

    }

    private Integer notification_text_color = null;
    private float notification_text_size = 11;
    private final String COLOR_SEARCH_RECURSE_TIP = "SOME_SAMPLE_TEXT";

    private boolean recurseGroup(ViewGroup gp) {
        final int count = gp.getChildCount();
        for (int i = 0; i < count; ++i) {
            if (gp.getChildAt(i) instanceof TextView) {
                final TextView text = (TextView) gp.getChildAt(i);
                final String szText = text.getText().toString();
                {
                    notification_text_color = text.getTextColors().getDefaultColor();
                    notification_text_size = text.getTextSize();
                    DisplayMetrics metrics = new DisplayMetrics();
                    WindowManager systemWM = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                    systemWM.getDefaultDisplay().getMetrics(metrics);
                    notification_text_size /= metrics.scaledDensity;
                    return true;
                }
            } else if (gp.getChildAt(i) instanceof ViewGroup)
                return recurseGroup((ViewGroup) gp.getChildAt(i));
        }
        return false;
    }

    /**
     * 预警推送自定义通知栏
     */
    public void showWarnNotify() {
        TYPE = dataMap.get("TYPE").toString();
        String ico = dataMap.get("ICO").toString();

        // 先设定RemoteViews
        RemoteViews view_warn = new RemoteViews(context.getPackageName(),
                R.layout.notification_warn_layout);
        try {
            InputStream is = null;
            is = context.getResources().getAssets()
                    .open("img_warn/" + ico + ".png");
            Bitmap bm = BitmapFactory.decodeStream(is);
            // 设置对应IMAGEVIEW的ID的资源图片
            view_warn.setImageViewBitmap(R.id.warn_icon, bm);

        } catch (Exception e) {
            view_warn.setImageViewResource(R.id.warn_icon,
                    R.drawable.ic_launcher);
        }
        view_warn.setTextViewText(R.id.tv_warn_title, dataMap.get("TITLE")
                .toString());
        view_warn.setTextViewText(R.id.tv_warn_and, "@天津气象");
        view_warn.setTextViewText(R.id.tv_warn_content, dataMap.get("CONTENT")
                .toString());
        view_warn.setTextColor(R.id.tv_warn_title, notification_text_color);
        view_warn.setTextColor(R.id.tv_warn_and, notification_text_color);
        view_warn.setTextColor(R.id.tv_warn_content, notification_text_color);
        mBuilder = new Builder(context);
        mBuilder.setContent(view_warn)
                .setContentIntent(
                        getDefalutIntent(Notification.FLAG_AUTO_CANCEL))
                .setWhen(System.currentTimeMillis())
                // 通知产生的时间，会在通知信息里显示
                .setTicker(dataMap.get("TITLE").toString())
                .setPriority(Notification.PRIORITY_DEFAULT)// 设置该通知优先级
                .setOngoing(false)// 不是正在进行的 true为正在进行 效果和.flag一样
                .setSmallIcon(R.drawable.ic_launcher);
        Notification notify = mBuilder.build();
        notify.contentView = view_warn;
        notify.contentIntent = getPendIntent();
        // 通知被点击后，自动消失
        notify.defaults = Notification.DEFAULT_SOUND;
        notify.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(Notification_ID_BASE, notify);
    }

    /**
     * 温馨提示(节日、节气)自定义通知栏
     */
    public void showHolidayNotify() {
        TYPE = dataMap.get("TYPE").toString();
        String title = dataMap.get("TITLE").toString();
        String content = dataMap.get("CONTENT").toString();
        String holiday_name = dataMap.get("HOLIDAY_NAME").toString();
        String ico = dataMap.get("ICO").toString();
        String days = dataMap.get("DAYS").toString();

        String str = "";
        if (TYPE.equals("节日")) {
            str = "jr_";
        } else if (TYPE.equals("节气")) {
            str = "jq_";
        }

        // 先设定RemoteViews
        RemoteViews view_holiday = new RemoteViews(context.getPackageName(),
                R.layout.notification_holiday_layout);
        try {
            InputStream is = null;
            is = context.getResources().getAssets()
                    .open("img_holiday/" + str + ico + ".png");
            Bitmap bm = BitmapFactory.decodeStream(is);
            // 设置对应IMAGEVIEW的ID的资源图片
            view_holiday.setImageViewBitmap(R.id.holiday_icon, bm);

        } catch (Exception e) {
            e.printStackTrace();
            // view_holiday.setImageViewResource(R.id.holiday_icon,R.drawable.ic_launcher);
        }

        String tipTitle = "";
        if ("0".equals(days)) {
            tipTitle = "<font color=\"#FFFFFF\">亲，</font><font color=\"#FCFF00\">今天</font><font color=\"#FFFFFF\">是"
                    + holiday_name + "啦！</font>";
        } else {
            tipTitle = "<font color=\"#FFFFFF\">亲，还有</font><font color=\"#FCFF00\">"
                    + days
                    + "天</font><font color=\"#FFFFFF\">就到"
                    + holiday_name + "啦！</font>";
        }

        if (!TextUtils.isEmpty(days)) {
            view_holiday.setTextViewText(R.id.tv_holiday_title,
                    Html.fromHtml(tipTitle));
        }

        view_holiday.setTextViewText(R.id.tv_holiday_and, "@天津气象");
        view_holiday.setTextViewText(R.id.tv_holiday_content, content);

        view_holiday.setTextColor(R.id.tv_holiday_title, notification_text_color);
        view_holiday.setTextColor(R.id.tv_holiday_and, notification_text_color);
        view_holiday.setTextColor(R.id.tv_holiday_content, notification_text_color);

        mBuilder = new Builder(context);
        mBuilder.setContent(view_holiday)
                .setContentIntent(getDefalutIntent(Notification.FLAG_AUTO_CANCEL))
                .setWhen(System.currentTimeMillis())
                // 通知产生的时间，会在通知信息里显示
                .setTicker(holiday_name)
                .setPriority(Notification.PRIORITY_DEFAULT)// 设置该通知优先级
                .setOngoing(false)// 不是正在进行的 true为正在进行 效果和.flag一样
                .setSmallIcon(R.drawable.ic_launcher);
        Notification notify = mBuilder.build();
        notify.contentView = view_holiday;
        notify.contentIntent = getPendIntent();
        // 通知被点击后，自动消失
        notify.defaults |= Notification.DEFAULT_SOUND;
        notify.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(Notification_ID_BASE, notify);
    }

    /**
     * 天气预报自定义通知栏
     */
    public void showWeatherNotify() {

        TYPE = dataMap.get("TYPE").toString();

        String CITY = dataMap.get("CITY").toString(); // 福州
        String DAY = dataMap.get("DAY").toString(); // 11月10日周四
        String D1_W1 = dataMap.get("D1_W1").toString(); // 多云(白天天气)
        String D1_ICO1 = dataMap.get("D1_ICO1").toString(); // (白天天气图标)
        String D1_W2 = dataMap.get("D1_W2").toString(); // 多云(夜间天气)
        String D1_ICO2 = dataMap.get("D1_ICO2").toString(); // (夜间天气图标)
        String D1_TL = dataMap.get("D1_TL").toString(); // 10(低温)
        String D1_TH = dataMap.get("D1_TH").toString(); // 20(高温)
        String D2_W1 = dataMap.get("D2_W1").toString(); // 多云(第二天白天天气)
        String D2_ICO1 = dataMap.get("D2_ICO1").toString(); // (白天天气图标)
        String D2_W2 = dataMap.get("D2_W2").toString(); // 多云(第二天夜间天气)
        String D2_ICO2 = dataMap.get("D2_ICO2").toString(); // (夜间天气图标)
        String D2_TL = dataMap.get("D2_TL").toString(); // 10
        String D2_TH = dataMap.get("D2_TH").toString(); // 20

        String TITIE = ""; // 早间天气预报or晚间天气预报
        String TODAY_WEATHER = ""; // 今天的天气
        String TOMORROW_WEATHER = ""; // 明天的天气
        String today_ico = ""; // 今天的天气图标
        String tomorrow_ico = ""; // 明天的天气图标
        String TODAY_TEMP = ""; // 今天的气温
        String TOMORROW_TEMP = ""; // 明天的气温

        boolean isDay = true; // 是否白天

        // ------今天
        // 白天：高温在上、低温在下
        // 夜晚：低温在上，高温--
        // 凌晨：低温在上、高温在下
        // ------6天
        // 白天：高温在上、低温在下
        // 夜晚：低温在上、高温在下
        // 凌晨：低温在上、高温在下

        if (TYPE.equals("早预报")) {
            TITIE = "早间天气预报";
            TODAY_WEATHER = D1_W1;
            TOMORROW_WEATHER = D2_W1;
            isDay = true;
            today_ico = D1_ICO1;
            tomorrow_ico = D2_ICO1;
            TODAY_TEMP = D1_TH + " ~ " + D1_TL;
            TOMORROW_TEMP = D2_TH + " ~ " + D2_TL;
        } else if (TYPE.equals("晚预报")) {
            TITIE = "晚间天气预报";
            TODAY_WEATHER = D1_W2;
            TOMORROW_WEATHER = D2_W2;
            isDay = false;
            today_ico = D1_ICO2;
            tomorrow_ico = D2_ICO2;
            TODAY_TEMP = D1_TL + " -- ";// D1_TL +" ~ "+ D1_TH;
            TOMORROW_TEMP = D2_TL + " -- ";// D2_TL +" ~ "+ D2_TH;
        }

        // 先设定RemoteViews
        RemoteViews view_weather = new RemoteViews(context.getPackageName(),
                R.layout.notification_weatherpush_layout);

        try {
            Bitmap today_bm;
            Bitmap tomorrow_bm;

            today_bm = ZtqImageTool.getInstance().getWeatherIcon(context,
                    isDay, today_ico);// 今天的天气图标
            tomorrow_bm = ZtqImageTool.getInstance().getWeatherIcon(context,
                    isDay, tomorrow_ico);// 明天的天气图标
            view_weather.setImageViewBitmap(R.id.iv_today_weather, today_bm);
            view_weather.setImageViewBitmap(R.id.iv_tomorrow_weather,
                    tomorrow_bm);
        } catch (Exception e) {

            e.printStackTrace();
        }

        view_weather.setTextViewText(R.id.tv_city, CITY);// 城市
        view_weather.setTextViewText(R.id.tv_time, DAY);// 时间 12月24日周三
        view_weather.setTextViewText(R.id.tv_today_weather, TODAY_WEATHER);// 今天的天气
        view_weather.setTextViewText(R.id.tv_today_temp, TODAY_TEMP + "°C");// 今天的气温
        // 9-15°C
        view_weather
                .setTextViewText(R.id.tv_tomorrow_weather, TOMORROW_WEATHER);// 明天的天气
        view_weather.setTextViewText(R.id.tv_tomorrow_temp, TOMORROW_TEMP
                + "°C");// 明天的气温

        view_weather.setTextColor(R.id.tv_city, notification_text_color);
        view_weather.setTextColor(R.id.tv_time, notification_text_color);
        view_weather.setTextColor(R.id.tv_today_weather, notification_text_color);
        view_weather.setTextColor(R.id.tv_today_temp, notification_text_color);
        view_weather.setTextColor(R.id.tv_tomorrow_weather, notification_text_color);
        view_weather.setTextColor(R.id.tv_tomorrow_temp, notification_text_color);
        // 9-15°C

        mBuilder = new NotificationCompat.Builder(context,CHANNEL_ID);
        mBuilder.setContent(view_weather)
                .setContentIntent(
                        getDefalutIntent(Notification.FLAG_AUTO_CANCEL))
                .setWhen(System.currentTimeMillis())// 通知产生的时间，会在通知信息里显示
                .setTicker(TITIE).setPriority(Notification.PRIORITY_DEFAULT)// 设置该通知优先级
                .setOngoing(false)// 不是正在进行的 true为正在进行 效果和.flag一样
                .setSmallIcon(R.drawable.ic_launcher);
        // mNotificationManager.notify(notifyId, mBuilder.build());
        Notification notify = mBuilder.build();
        notify.contentView = view_weather;
        // 通知被点击后，自动消失
        notify.flags |= Notification.FLAG_AUTO_CANCEL;
        notify.defaults = Notification.DEFAULT_SOUND;
        // // 不跳转
        // notify.setLatestEventInfo(context, TITIE, null, null);
        notify.contentIntent = getPendIntent();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(Notification_ID_BASE, notify);
    }

    public PendingIntent getPendIntent() {
        Intent intent = new Intent(context, ActivityMain.class);
        Bundle bundle = new Bundle();
        intent.putExtras(bundle);
        PendingIntent pendingInt = PendingIntent.getActivity(context, 0,
                intent, 0);
        return pendingInt;
    }

    /**
     * 实况预警推送自定义通知栏
     */
    public void showWarnLiveNotify() {
        TYPE = dataMap.get("TYPE").toString();

        String CITY = dataMap.get("CITY").toString();// 城市
        String TIME = dataMap.get("TIME").toString();// 发布时间
        String MSG = dataMap.get("MSG").toString(); // 消息

        String tipTitle = "";

        if (TYPE.equals("temp_h")) {// 实况告警通知 温度高于(temp_h)
            String TEMP = dataMap.get("TEMP").toString();
            tipTitle = "<font color=\"#FFFFFF\">气温</font><font color=\"red\">"
                    + TEMP + "°C</font>";
        } else if (TYPE.equals("temp_l")) {// 实况告警通知 温度低于(temp_l)
            String TEMP = dataMap.get("TEMP").toString();
            tipTitle = "<font color=\"#FFFFFF\">气温</font><font color=\"red\">"
                    + TEMP + "°C</font>";
        } else if (TYPE.equals("vis_l")) {// 实况告警通知 能见度低于(vis_l)
            String VIS = dataMap.get("VIS").toString();
            tipTitle = "<font color=\"#FFFFFF\">能见度</font><font color=\"red\">"
                    + VIS + "m</font>";
        } else if (TYPE.equals("hum_h")) {// 实况告警通知 湿度高于(hum_h)
            String HUM = dataMap.get("HUM").toString();
            tipTitle = "<font color=\"#FFFFFF\">湿度</font><font color=\"red\">"
                    + HUM + "%</font>";
        } else if (TYPE.equals("hum_l")) {// 实况告警通知 湿度低于(hum_l)
            String HUM = dataMap.get("HUM").toString();
            tipTitle = "<font color=\"#FFFFFF\">湿度</font><font color=\"red\">"
                    + HUM + "%</font>";
        } else if (TYPE.equals("rain_h")) {// 实况告警通知 小时雨量高于(rain_h)
            String RAIN = dataMap.get("RAIN").toString();
            tipTitle = "<font color=\"#FFFFFF\">小时雨量</font><font color=\"red\">"
                    + RAIN + "mm</font>";
        } else if (TYPE.equals("wspeed_h")) {// 实况告警通知 风速高于(wspeed_h)
            String WSPEED = dataMap.get("WSPEED").toString();
            tipTitle = "<font color=\"#FFFFFF\">风速</font><font color=\"red\">"
                    + WSPEED + "m/s</font>";
        }

        // 先设定RemoteViews
        RemoteViews view_warn = new RemoteViews(context.getPackageName(),
                R.layout.notification_warnlive_layout);

        view_warn.setImageViewResource(R.id.warn_icon, R.drawable.ic_launcher);

        if (!TextUtils.isEmpty(tipTitle)) {
            view_warn.setTextViewText(R.id.tv_value_time,
                    Html.fromHtml(tipTitle));
        }

        view_warn.setTextViewText(R.id.tv_warn_city, CITY);
        view_warn.setTextViewText(R.id.tv_warn_time, TIME + "发布");
        view_warn.setTextViewText(R.id.tv_warn_content, MSG);

        view_warn.setTextColor(R.id.tv_warn_city, notification_text_color);
        view_warn.setTextColor(R.id.tv_warn_time, notification_text_color);
        view_warn.setTextColor(R.id.tv_warn_content, notification_text_color);

        mBuilder = new Builder(context);
        mBuilder.setContent(view_warn)
                .setContentIntent(
                        getDefalutIntent(Notification.FLAG_AUTO_CANCEL))
                .setWhen(System.currentTimeMillis())// 通知产生的时间，会在通知信息里显示
                .setTicker(MSG).setPriority(Notification.PRIORITY_DEFAULT)// 设置该通知优先级
                .setOngoing(false)// 不是正在进行的 true为正在进行 效果和.flag一样
                .setSmallIcon(R.drawable.ic_launcher);
        // mNotificationManager.notify(notifyId, mBuilder.build());
        Notification notify = mBuilder.build();
        notify.contentView = view_warn;
        // 通知的默认参数 DEFAULT_SOUND, DEFAULT_VIBRATE, DEFAULT_LIGHTS.
        // 如果要全部采用默认值, 用 DEFAULT_ALL.
        // 此处采用默认声音
        // baseNF.defaults |= Notification.DEFAULT_ALL;
        // baseNF.defaults |= Notification.DEFAULT_VIBRATE;
        // baseNF.defaults |= Notification.DEFAULT_LIGHTS;
        notify.defaults = Notification.DEFAULT_SOUND;
        // 通知被点击后，自动消失
        notify.flags |= Notification.FLAG_AUTO_CANCEL;
        // Notification notify = new Notification();
        // notify.icon = R.drawable.icon;
        // notify.contentView = view_warn;
        // notify.contentIntent =
        // getDefalutIntent(Notification.FLAG_AUTO_CANCEL);

        notify.contentIntent = getPendIntent();

        notificationManager.notify(Notification_ID_BASE, notify);
    }

}
