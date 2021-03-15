package com.pcs.ztqtj.control.tool;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.view.activity.loading.ActivityLoading;
import com.pcs.ztqtj.view.activity.push.ActivityPushServiceNotificationDetails;
import com.pcs.ztqtj.view.activity.warn.ActivityWarnDetails;

import java.io.InputStream;
import java.util.Map;

/**
 * Created by tyaathome on 2018/1/2.
 */

public class ZtqNotification {

    private Context context;

    private Map<String, String> dataMap;
    private int mId = 0;
    private static final String CHANNEL_ID = "ZTQ_NOTIFICATION_CHANNEL_ID";
    private static final String CHANNEL_NAME = "天津气象推送通道";

    public ZtqNotification(Context context) {
        this.context = context;
    }

    public ZtqNotification(Context context, Map<String, String> dataMap) {
        this.context = context;
        this.dataMap = dataMap;
    }

    public ZtqNotification(Context context, Map<String, String> dataMap, int id) {
        this.context = context;
        this.dataMap = dataMap;
        mId = id;
    }

    public void setId(int id) {
        mId = id;
    }

    /**
     * 预警推送自定义通知栏
     */
    public void showDefault() {
        String title = dataMap.get("TITLE");
        String content = dataMap.get("CONTENT");
        Notification notification = getNotification(title, content, ActivityLoading.class);
        doNotify(notification);
    }

    /**
     * 温馨提示(节日、节气)自定义通知栏
     */
    public void showHoliday() {
        String type = dataMap.get("TYPE");
        String title = dataMap.get("TITLE");
        String content = dataMap.get("CONTENT");
        String ico = dataMap.get("ICO");

        String str = "";
        if (type.equals("节日")) {
            str = "jr_";
        } else if (type.equals("节气")) {
            str = "jq_";
        }
        Bitmap icon = null;
        try {
            InputStream is = null;
            is = context.getResources().getAssets()
                    .open("img_holiday/" + str + ico + ".png");
            icon = BitmapFactory.decodeStream(is);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Notification notification = getNotification(title, content, icon, ActivityLoading.class);
        doNotify(notification);
    }

    /**
     * 实况预警推送自定义通知栏
     */
    public void showWarnLive() {
        String title = dataMap.get("TITLE");
        String content = dataMap.get("CONTENT");
        Notification notification = getNotification(title, content, ActivityLoading.class);
        doNotify(notification);
    }

    public void showWarn() {
        String title = dataMap.get("TITLE");
        String content = dataMap.get("CONTENT");
        String ico = dataMap.get("ICO");
        String id = dataMap.get("ID");
        String type=dataMap.get("type");
        Bitmap icon = null;
        try {
            InputStream is = null;
            is = context.getResources().getAssets()
                    .open("img_warn/" + ico + ".png");
            icon = BitmapFactory.decodeStream(is);

        } catch (Exception e) {
            e.printStackTrace();
        }

        Bundle bundle = new Bundle();
        bundle.putString("type", "warn");
        bundle.putString("t", title);
        bundle.putString("i", ico);
        bundle.putString("id", id);
        bundle.putString("type",type);
        Notification notification = getNotification(title, content, icon, ActivityWarnDetails.class, bundle);
        doNotify(notification);
    }

    public void showService(){
        String TITLE = dataMap.get("TITLE");
        String CONTENT = dataMap.get("CONTENT");
        String TYPE = dataMap.get("TYPE");
        String id = dataMap.get("ID");

//        if(!TextUtils.isEmpty(customContent)){
//            JSONObject json = null;
//            try {
//                json = new JSONObject(customContent);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//             id = json.optString("ID");
//        }

        Bundle bundle = new Bundle();
        bundle.putString("TITLE", TITLE);
        bundle.putString("CONTENT", CONTENT);
        bundle.putString("TYPE", TYPE);
        bundle.putString("id",id);
        Notification notification = getNotification(TITLE, CONTENT, null, ActivityPushServiceNotificationDetails.class, bundle);
        doNotify(notification);

    }

    /**
     * 天气预报自定义通知栏
     */
    public void showWeather() {

    }

    /**
     * 天气预报推送自定义通知栏
     */
    public void showWeatherPre() {
        String title = dataMap.get("TITLE");
        String content = dataMap.get("CONTENT");
        Notification notification = getNotification(title, content,null, ActivityLoading.class,null);
        doNotify(notification);
    }

    private void doNotify(Notification notification) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context
                .NOTIFICATION_SERVICE);
        if(manager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
                manager.createNotificationChannel(channel);
            }
            manager.notify(mId, notification);
        }
    }

    private Notification getNotification(String title, String content, Class<?> cls) {
        return getNotification(title, content, null, cls, null);
    }

    private Notification getNotification(String title, String content, Class<?> cls, Bundle bundle) {
        return getNotification(title, content, null, cls, bundle);
    }

    private Notification getNotification(String title, String content, Bitmap icon, Class<?> cls) {
        return getNotification(title, content, icon, cls, null);
    }

    private Notification getNotification(String title, String content, Bitmap icon, Class<?> cls, Bundle bundle) {
        if(icon == null) {
            Drawable drawable = ContextCompat.getDrawable(context, R.drawable.ic_launcher);
            icon = ((BitmapDrawable) drawable).getBitmap();
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,CHANNEL_ID);
        //设置小图标
        builder.setSmallIcon(R.drawable.ic_ztq_notification);
        //设置大图标
        builder.setLargeIcon(icon);
        //设置标题
        builder.setContentTitle(title);
        //设置通知正文
        builder.setContentText(content);
        //设置摘要
        //mBuilder.setSubText(subtext);
        //设置是否点击消息后自动clean
        builder.setAutoCancel(true);
        builder.setTicker("天津气象");
        //设置优先级
        builder.setPriority(NotificationCompat.PRIORITY_MAX);
        builder.setDefaults(Notification.DEFAULT_ALL);

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setClass(context, cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(MyConfigure.EXTRA_BUNDLE, bundle);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle();
        style.bigText(content);
        style.setBigContentTitle(title);
        //style.setSummaryText(summary);
        builder.setStyle(style);

        return builder.build();
    }

    public PendingIntent getMainIntent() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setClass(context, ActivityLoading.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

}
