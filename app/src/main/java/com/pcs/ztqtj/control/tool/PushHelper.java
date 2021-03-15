package com.pcs.ztqtj.control.tool;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.pcs.lib_ztqfj_v2.model.pack.net.push.PushTag;
import com.pcs.ztqtj.view.activity.push.ActivityDefaultDialog;
import com.pcs.ztqtj.view.activity.push.ActivityHolidayDialog;
import com.pcs.ztqtj.view.activity.push.ActivityPushDialog;
import com.pcs.ztqtj.view.activity.push.ActivityPushEmergencyDialog;
import com.pcs.ztqtj.view.activity.push.ActivityPushLocationDialog;
import com.pcs.ztqtj.view.activity.push.ActivityPushServiceDialog;
import com.pcs.ztqtj.view.activity.push.ActivityPushWeatherDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class PushHelper {
    public static void send(Context context, Map<String, String> dataMap) {
        String TYPE = dataMap.get("TYPE");
        String title = dataMap.get("TITLE");
        String content = dataMap.get("CONTENT");
        if(!TextUtils.isEmpty(TYPE)) {
            if (TYPE.equals("WP")) {
                Intent intent = new Intent(context, ActivityPushLocationDialog.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("TITLE", dataMap.get("TITLE"));// 预警标题
                intent.putExtra("AUTHOR", dataMap.get("AUTHOR"));// 预警来源
                intent.putExtra("PTIME", dataMap.get("PTIME"));// 预警发布时间
                intent.putExtra("ICO", dataMap.get("ICO"));// 预警图片名称
                intent.putExtra("CONTENT", dataMap.get("CONTENT"));// 预警内容
                String PTIME = dataMap.get("PTIME");
                if (checkTime(PTIME)) {
                    context.startActivity(intent);
                }
            } else if (TYPE.equals("预警") || TYPE.equals(PushTag.getInstance().PUSHTAG_REMOVE)) {// 预警信息推送:
                int displayModel = (Integer) LocalDataHelper.getPushTag(context, PushTag.getInstance()
                        .PUSHTAG_QXYJ_MODEL, Integer.class);
                dataMap.put("type","0");
                switch (displayModel) {
                    case 0:// 消息栏
                        Log.d("预警展示模式", "消息栏");
                        ZtqNotification notification = new ZtqNotification(context, dataMap, getData());
                        notification.showWarn();
                        break;
                    case 1:// 弹窗
                        Log.d("预警展示模式", "弹窗");
                        Intent intent = new Intent(context, ActivityPushDialog.class);
                        intent.putExtra("TITLE", dataMap.get("TITLE"));// 预警标题
                        intent.putExtra("AUTHOR", dataMap.get("AUTHOR"));// 预警来源
                        intent.putExtra("PTIME", dataMap.get("PTIME"));// 预警发布时间
                        intent.putExtra("ICO", dataMap.get("ICO"));// 预警图片名称
                        intent.putExtra("CONTENT", dataMap.get("CONTENT"));// 预警内容
                        intent.putExtra("ID", dataMap.get("ID"));//预警ID
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        String PTIME = dataMap.get("PTIME");
                        if (checkTime(PTIME)) {
                            context.startActivity(intent);
                        }
                        break;
                }
            } else if (TYPE.equals("weather_cstq") || TYPE.equals("weather_bw") || TYPE.equals
                    ("weather_tsqw") || TYPE.equals("weather_tstq") || TYPE
                    .equals("weather_kqwr")) {
                int displayModel = (Integer) LocalDataHelper.getPushTag(context, PushTag.getInstance()
                        .PUSHTAG_WEATHER_FORECAST_MODEL, Integer.class);
                switch (displayModel) {
                    case 0:// 消息栏
                        Log.d("天气预报模式", "消息栏");
                        ZtqNotification notification = new ZtqNotification(context, dataMap, getData());
                        notification.showWeatherPre();
                        break;
                    case 1:// 弹窗
                        Log.d("天气预报模式", "弹窗");
                        Intent intent = new Intent(context, ActivityPushWeatherDialog.class);
                        intent.putExtra("TITLE", dataMap.get("TITLE"));// 预警标题
                        intent.putExtra("PTIME", dataMap.get("PTIME"));// 预警发布时间
                        intent.putExtra("CONTENT", dataMap.get("CONTENT"));// 预警内容
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                        break;
                }
            } else if (TYPE.equals("节日") || TYPE.equals("节气")) {// 节日节气信息推送:
                int displayModel = (Integer) LocalDataHelper.getPushTag(context, PushTag.getInstance()
                        .PUSHTAG_TIPS_MODEL, Integer.class);
                switch (displayModel) {
                    case 0:// 消息栏
                        ZtqNotification notification = new ZtqNotification(context, dataMap, getData());
                        notification.showHoliday();
                        break;

                    case 1:// 弹窗
                        Intent intent = new Intent(context, ActivityHolidayDialog.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("TITLE", title);// 标题
                        intent.putExtra("TYPE", TYPE);// 推送类型
                        intent.putExtra("HOLIDAY_NAME", dataMap.get("HOLIDAY_NAME"));// 节日或者节气的名称
                        intent.putExtra("CONTENT", content);// 推送内容
                        intent.putExtra("ICO", dataMap.get("ICO"));// 节日、节气的图片名称
                        context.startActivity(intent);
                        break;
                }
            }  else if (TYPE.equals("公告")) {
                int displayModel = (Integer) LocalDataHelper.getPushTag(context, PushTag.getInstance()
                        .PUSHTAG_TIPS_MODEL, Integer.class);
                switch (displayModel) {
                    case 0:// 消息栏
                        ZtqNotification notification = new ZtqNotification(context, dataMap, getData());
                        notification.showDefault();
                        break;
                    case 1:// 弹窗
                        Intent intent = new Intent(context, ActivityDefaultDialog.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("TITLE", title);// 标题
                        intent.putExtra("TYPE", TYPE);// 推送类型
                        intent.putExtra("CONTENT", content);// 推送内容
                        context.startActivity(intent);
                        break;
                }
            } else if (TYPE.equals("早预报") || TYPE.equals("晚预报")) {
                MNotification mn = new MNotification(context, dataMap);
                mn.setNotification_ID_BASE(getData());
                mn.showWeatherNotify();
            } else if (TYPE.equals("temp_h") || TYPE.equals("temp_l")) {// 实况告警通知
                // 温度高于(temp_h)、温度低于(temp_l)
                ZtqNotification notification = new ZtqNotification(context, dataMap, getData());
                notification.showWarnLive();
            } else if (TYPE.equals("vis_l")) {// 实况告警通知 能见度低于(vis_l)
                ZtqNotification notification = new ZtqNotification(context, dataMap, getData());
                notification.showWarnLive();
            } else if (TYPE.equals("hum_h") || TYPE.equals("hum_l")) {// 实况告警通知
                // 湿度高于(hum_h)、湿度低于(hum_l)
                ZtqNotification notification = new ZtqNotification(context, dataMap, getData());
                notification.showWarnLive();
            } else if (TYPE.equals("rain_h")) {// 实况告警通知 小时雨量高于(rain_h)
                ZtqNotification notification = new ZtqNotification(context, dataMap, getData());
                notification.showWarnLive();
            } else if (TYPE.equals("wspeed_h")) {// 实况告警通知
                ZtqNotification notification = new ZtqNotification(context, dataMap, getData());
                notification.showWarnLive();
            } else if (TYPE.equals("qxfw_jc")) {// TYPE=qxfw_jc （决策报告） qxfw_hy （行业气象）  qxfw_lj （临近预报）
                int displayModel = (Integer) LocalDataHelper.getPushTag(context, PushTag.getInstance()
                        .PUSHTAG_TIPS_MODEL_SERVICE, Integer.class);
                switch (displayModel) {
                    case 0:// 消息栏
                        ZtqNotification notification = new ZtqNotification(context, dataMap, getData());
                        notification.showService();
                        break;
                    case 1:// 弹窗
                        Intent intent = new Intent(context, ActivityPushServiceDialog.class);
                        intent.putExtra("TITLE", dataMap.get("TITLE"));// 标题
                        intent.putExtra("CONTENT", dataMap.get("CONTENT"));// 标题
                        if (TYPE.equals("qxfw_jc")) {
                            intent.putExtra("show", true);// 标题
                        }
                        intent.putExtra("id", dataMap.get("ID"));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                        break;
                }
            } else if (TYPE.equals(PushTag.getInstance().PUSHTAG_QXYJ_NATURAL) ||
                    TYPE.equals(PushTag.getInstance().PUSHTAG_QXYJ_ACCIDENT) ||
                    TYPE.equals(PushTag.getInstance().PUSHTAG_QXYJ_PUBLIC) ||
                    TYPE.equals(PushTag.getInstance().PUSHTAG_QXYJ_EMERGENCY)) {
                int displayModel = (Integer) LocalDataHelper.getPushTag(context, PushTag.getInstance()
                        .PUSHTAG_QXYJ_MODEL, Integer.class);
                dataMap.put("type","1");
                switch (displayModel) {
                    case 0:// 消息栏
                        Log.d("预警展示模式", "消息栏");
                        ZtqNotification notification = new ZtqNotification(context, dataMap, getData());
                        notification.showWarn();
                        break;
                    case 1:// 弹窗
                        Log.d("预警展示模式", "弹窗");
                        Intent intent = new Intent(context, ActivityPushEmergencyDialog.class);
                        intent.putExtra("TITLE", dataMap.get("TITLE"));
                        intent.putExtra("AUTHOR", dataMap.get("AUTHOR"));
                        intent.putExtra("PTIME", dataMap.get("PTIME"));
                        intent.putExtra("CONTENT", dataMap.get("CONTENT"));
                        intent.putExtra("ID", dataMap.get("ID"));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        if (checkTime(dataMap.get("PTIME"))) {
                            context.startActivity(intent);
                        }
                        break;
                }
            } else if (TYPE.equals(PushTag.getInstance().PUSHTAG_TKL)) {
                int displayModel = (Integer) LocalDataHelper.getPushTag(context, PushTag.getInstance()
                        .PUSHTAG_QXYJ_MODEL, Integer.class);
                dataMap.put("type","2");
                switch (displayModel) {
                    case 0:// 消息栏
                        Log.d("预警展示模式", "消息栏");
                        ZtqNotification notification = new ZtqNotification(context, dataMap, getData());
                        notification.showWarn();
                        break;
                    case 1:// 弹窗
                        Log.d("预警展示模式", "弹窗");
                        Intent intent = new Intent(context, ActivityPushEmergencyDialog.class);
                        intent.putExtra("TITLE", dataMap.get("TITLE"));
                        intent.putExtra("AUTHOR", dataMap.get("AUTHOR"));
                        intent.putExtra("PTIME", dataMap.get("PTIME"));
                        intent.putExtra("CONTENT", dataMap.get("CONTENT"));
                        intent.putExtra("ID", dataMap.get("ID"));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        if (checkTime(dataMap.get("PTIME"))) {
                            context.startActivity(intent);
                        }
                        break;
                }
            }
        }
    }

    private static int getData() {
        int result = 1;
        SimpleDateFormat sf = new SimpleDateFormat("HHMMssSSS");
        String time = sf.format(new Date());
        time = time.substring(1, time.length());
        result = Integer.parseInt(time);
        return result;
    }

    private static boolean checkTime(String time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmmss");
        try {
            Date pushDate = format.parse(time);
            Calendar pushCal = Calendar.getInstance();
            pushCal.setTime(pushDate);
            Calendar currentCal = Calendar.getInstance();
            // 添加3小时，如果推送时间的3小时后在当前时间之后，则返回true
            pushCal.add(Calendar.HOUR_OF_DAY, 3);
            return pushCal.after(currentCal);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return true;
    }
}
