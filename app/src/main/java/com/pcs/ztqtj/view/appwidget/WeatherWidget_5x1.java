package com.pcs.ztqtj.view.appwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.widget.RemoteViews;

import com.pcs.lib.lib_pcs_v3.control.tool.Util;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCity;
import com.pcs.lib_ztqfj_v2.model.pack.net.week.PackMainWeekWeatherDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.week.PackMainWeekWeatherUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.week.WeekWeatherInfo;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.view.activity.loading.ActivityLoading;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;

public class WeatherWidget_5x1 extends AppWidgetProvider {

    //一周天气
    private PackMainWeekWeatherUp mPackWeekUp = new PackMainWeekWeatherUp();

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                + getClass().getName();
        if (action.equals(intent.getAction())) {
            update(context);
            // Toast.makeText(context, "已接收时间广播！", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        update(context);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        // if(mTimeReceiver != null) {
        // context.getApplicationContext().unregisterReceiver(mTimeReceiver);
        // }
    }


    private void update(Context context) {
        RemoteViews views = new RemoteViews(context.getPackageName(),
                R.layout.widget_provider_5x1);

        // 字体颜色
        int textColor = context.getResources().getColor(R.color.text_white);

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();

        PackLocalCity cityinfo = ZtqCityDB.getInstance().getCityMain();


        if (cityinfo != null) {
            mPackWeekUp.setCity(cityinfo);
            PackMainWeekWeatherDown pcsDownPack = (PackMainWeekWeatherDown) PcsDataManager.getInstance().getNetPack(mPackWeekUp.getName());
            if (pcsDownPack == null) {
                views.setTextViewText(R.id.widget_weather, "");
            } else {
                WeekWeatherInfo info = pcsDownPack.getToday();
                // 高低温度
                try {
                    final String maxTemp = info.higt;// 最高气温
                    final String minTemp = info.lowt;// 最低气温
                    String temperHight = maxTemp + "℃~" + minTemp + "℃";
                    views.setImageViewBitmap(
                            R.id.widget_5x1_current_h_l_temp,
                            createTextBitmap(context, temperHight,
                                    Util.dip2px(context, 20), textColor));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // 天气与天气图标
                try {
                    views.setTextViewText(R.id.widget_weather,
                            info.weather);
                    String path = pcsDownPack.getIconPath(pcsDownPack.getTodayIndex());
                    if (TextUtils.isEmpty(path)) {
                        views.setImageViewBitmap(
                                R.id.widget_5x1_current_icon, null);
                    } else {
                        InputStream iput;
                        Bitmap bitmap = null;
                        try {
                            iput = context.getAssets().open(path);
                            bitmap = BitmapFactory.decodeStream(iput);
                            iput.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (bitmap != null) {
                            int width = Util.dip2px(context, 40);
                            int height = Util.dip2px(context, 40);
                            Matrix m = new Matrix();
                            m.setRectToRect(
                                    new RectF(0, 0, bitmap.getWidth(),
                                            bitmap.getHeight()),
                                    new RectF(0, 0, width, height),
                                    Matrix.ScaleToFit.CENTER);
                            bitmap = Bitmap.createBitmap(bitmap, 0, 0,
                                    bitmap.getWidth(),
                                    bitmap.getHeight(), m, true);
                            views.setImageViewBitmap(
                                    R.id.widget_5x1_current_icon,
                                    bitmap);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // 设置时间、日期、区域
            try {
                if (pcsDownPack != null && !TextUtils.isEmpty(pcsDownPack.sys_time)) {
                    views.setTextViewText(R.id.widget_5x1_time_info, cityinfo.NAME);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        bundleIntent(context, views);
        AppWidgetManager mrg = AppWidgetManager.getInstance(context);
        ComponentName name = new ComponentName(context, WeatherWidget_5x1.class);
        mrg.updateAppWidget(name, views);
    }

    private String getWeek(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        switch (cal.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.SUNDAY:
                return "星期日";
            case Calendar.MONDAY:
                return "星期一";
            case Calendar.TUESDAY:
                return "星期二";
            case Calendar.WEDNESDAY:
                return "星期三";
            case Calendar.THURSDAY:
                return "星期四";
            case Calendar.FRIDAY:
                return "星期五";
            case Calendar.SATURDAY:
                return "星期六";
            default:
                return "";
        }
    }

    private void bundleIntent(Context context, RemoteViews views) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setClass(context, ActivityLoading.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.layout_widget_5x1, pendingIntent);
    }

    private Bitmap createTextBitmap(Context context, final String text,
                                    final float textSizePixels, final int textColor) {
        Typeface tf = Typeface.createFromAsset(context.getAssets(),
                "fonts/Arial.ttf");
        final TextPaint textPaint = new TextPaint();
        textPaint.setTypeface(tf);
        textPaint.setTextSize(textSizePixels);
        textPaint.setAntiAlias(true);
        textPaint.setSubpixelText(true);
        textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        textPaint.setColor(textColor);
        textPaint.setTextAlign(Paint.Align.LEFT);
        int height = (int) textPaint.getTextSize();
        Bitmap myBitmap = Bitmap.createBitmap(
                (int) textPaint.measureText(text), height + 10,
                Bitmap.Config.ARGB_8888);
        Canvas myCanvas = new Canvas(myBitmap);
        myCanvas.drawText(text, 0, height, textPaint);
        return myBitmap;
    }

    // /**
    // * 注册时间广播
    // * @param context
    // */
    // private void registerTimeReceiver(Context context) {
    // if(mTimeReceiver == null) {
    // mTimeReceiver = new TimeReceiver();
    // IntentFilter intentFilter = new IntentFilter();
    // intentFilter.addAction(Intent.ACTION_TIME_TICK);
    // context.getApplicationContext().registerReceiver(mTimeReceiver,
    // intentFilter);
    // }
    // }
    //
    // /**
    // * 时间广播
    // *
    // * @author Administrator
    // *
    // */
    // public class TimeReceiver extends BroadcastReceiver {
    //
    // @Override
    // public void onReceive(Context context, Intent intent) {
    // // if (Intent.ACTION_TIME_TICK.equals(intent.getAction())) {
    // // update(context);
    // // }
    // }
    //
    // }

}
