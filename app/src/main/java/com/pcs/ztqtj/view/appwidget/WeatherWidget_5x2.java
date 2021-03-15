package com.pcs.ztqtj.view.appwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.RemoteViews;

import com.pcs.lib.lib_pcs_v3.PcsInit;
import com.pcs.lib.lib_pcs_v3.control.tool.Util;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCity;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackSstqDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackSstqUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.PackYjxxIndexFbDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.PackYjxxIndexFbUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.YjxxInfo;
import com.pcs.lib_ztqfj_v2.model.pack.net.week.PackMainWeekWeatherDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.week.PackMainWeekWeatherUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.week.WeekWeatherInfo;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.MyConfigure;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.view.activity.loading.ActivityLoading;
import com.pcs.ztqtj.view.activity.warn.ActivityWarnDetails;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Z
 */
public class WeatherWidget_5x2 extends AppWidgetProvider {
    private static final String CLICK_ACTION = "com.pcs.ztq.CLICK_ACTION_5x2";
    private int heightPixels = 0;
    //实时天气
    private PackSstqUp mPackSstqUp = new PackSstqUp();
    //一周天气
    private PackMainWeekWeatherUp mPackWeekUp = new PackMainWeekWeatherUp();
    //首页预警
    //private PackYjxxIndexUp mPackYjxxUp = new PackYjxxIndexUp();
    private PackYjxxIndexFbUp mPackYjxxUp = new PackYjxxIndexFbUp();

    private int[] warnImageResourseId = {R.id.iv1, R.id.iv2, R.id.iv3, R.id.iv4};

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                + getClass().getName();
        if (intent != null) {
            if (action.equals(intent.getAction())) {
                //update(context);
                requestUpdate(context);
            } else if (CLICK_ACTION.equals(intent.getAction())) {
                startWarnActivity(context, intent.getExtras());
            }
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        //updateWarnGrid(context, appWidgetManager, appWidgetIds);
        update(context);
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    private void updateWarnGrid(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int id : appWidgetIds) {
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_provider_5x2);
            Intent intent = new Intent(context, WarnGridService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            rv.setRemoteAdapter(id, R.id.list_warn, intent);
            //rv.setViewPadding(R.id.list_warn, 0,0,300,0);
            Intent clickIntent = new Intent(context, WeatherWidget_5x2.class);
            clickIntent.setAction(WeatherWidget_5x2.CLICK_ACTION);
            clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            PendingIntent clickPendingIntent = PendingIntent.getBroadcast(context, 0, clickIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            rv.setPendingIntentTemplate(R.id.list_warn, clickPendingIntent);
            appWidgetManager.notifyAppWidgetViewDataChanged(id, R.id.list_warn);
            appWidgetManager.updateAppWidget(id, rv);
        }
    }

    private void startWarnActivity(Context context, Bundle bundle) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.putExtra(MyConfigure.EXTRA_BUNDLE, bundle);
        intent.setClass(context, ActivityWarnDetails.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    private void requestUpdate(Context context) {
        Intent intent = new Intent(context, WeatherWidget_5x2.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
// Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
// since it seems the onUpdate() is only fired on that:
        int[] ids = AppWidgetManager.getInstance(context)
                .getAppWidgetIds(new ComponentName(context, WeatherWidget_5x2.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        context.sendBroadcast(intent);
    }

    private void update(Context context) {
        RemoteViews views = new RemoteViews(context.getPackageName(),
                R.layout.widget_provider_5x2);
        PackLocalCity cityinfo = ZtqCityDB.getInstance().getCityMain();

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();

        // 屏幕高度
        heightPixels = metrics.heightPixels;

        int defaultTextColor = context.getResources().getColor(
                R.color.text_white);

        if (cityinfo != null) {
            // LogFile.writeFile(context, "小部件更新", null);
//            warncityinfo = ZtqCityDB.getInstance().getCityInfo2_ID(
//                    cityinfo.PARENT_ID);
            mPackSstqUp.area = cityinfo.ID;
            PackSstqDown pack = (PackSstqDown) PcsDataManager.getInstance().getNetPack(mPackSstqUp.getName());
            if (pack == null) {
            } else {
                if (pack.sys_time == null || "".equals(pack.sys_time)) {
                    return;
                }
                try {
                    // 一周天气
                    mPackWeekUp.setCity(cityinfo);
                    PackMainWeekWeatherDown pcsDownPack = (PackMainWeekWeatherDown) PcsDataManager.getInstance()
                            .getNetPack(mPackWeekUp.getName());
                    if (pcsDownPack != null) {
                        WeekWeatherInfo info = pcsDownPack.getToday();
                        if (info != null
                                && !(TextUtils
                                .isEmpty(info.higt) || TextUtils
                                .isEmpty(info.lowt))) {
                            views.setTextViewText(R.id.widget_weather,
                                    info.weather);
                            final String maxTemp = info.higt;// 最高气温
                            final String minTemp = info.lowt;// 最低气温
                            String temperHight = maxTemp + "℃/" + minTemp + "℃";
                            if (!TextUtils.isEmpty(temperHight)) {
                                views.setImageViewBitmap(
                                        R.id.widget_5x2_current_h_l_temp,
                                        createTextBitmap(
                                                context,
                                                // pack.wt_daytime +"\n\r"
                                                temperHight,
                                                Util.dip2px(context, 20),
                                                defaultTextColor));
                            }
                        }
                        // 天气图标
                        try {
                            String path = pcsDownPack.getIconPath(pcsDownPack.getTodayIndex());
                            if (TextUtils.isEmpty(path)) {
                                views.setImageViewBitmap(
                                        R.id.widget_5x2_current_icon, null);
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
                                    views.setImageViewBitmap(
                                            R.id.widget_5x2_current_icon,
                                            bitmap);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    // 日期
                    views.setTextViewText(R.id.widget_provider_5x2_cityname,
                            cityinfo.NAME);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // 当前气温，风描述
                if (pack.ct == null || "".equals(pack.ct)) {
                    int textColor = context.getResources().getColor(
                            R.color.widget_text);
                    views.setImageViewBitmap(
                            R.id.widget_5x2_current_temp,
                            createTextBitmap(context, pack.ct + "℃",
                                    Util.dip2px(context, 33), textColor));
                } else {
                    // setTextViewText(views, R.id.widget_4x2_current_temp,
                    // pack.ct + "°C", 45);
                    // views.setTextViewText(R.id.widget_4x2_current_temp,
                    // pack.ct + "°C");
                    int textColor = context.getResources().getColor(
                            R.color.widget_text);
                    views.setImageViewBitmap(
                            R.id.widget_5x2_current_temp,
                            createTextBitmap(context, pack.ct + "℃",
                                    Util.dip2px(context, 33), textColor));
                }
                if (pack.fl == null || "".equals(pack.fl)) {
                    views.setTextViewText(R.id.widget_wind, "暂无");
                } else {
                    views.setTextViewText(R.id.widget_wind, pack.winddir_current + pack.fl);
                }

                // 首页预警
                mPackYjxxUp.setCity(cityinfo);
                //PackYjxxIndexDown warnbean = (PackYjxxIndexDown) PcsDataManager.getInstance().getNetPack
                // (mPackYjxxUp.getName());
                PackYjxxIndexFbDown warnbean = (PackYjxxIndexFbDown) PcsDataManager.getInstance().getNetPack
                        (mPackYjxxUp.getName());
                if (warnbean == null) {
                    views.setTextViewText(R.id.widger_5x2_warn, "暂无预警");
                    views.setViewVisibility(R.id.widger_5x2_warn, View.VISIBLE);
                    views.setViewVisibility(R.id.layout_warn_grid, View.GONE);
                } else {
                    String unit = "";
                    List<YjxxInfo> warnList = new ArrayList<>();
                    if (warnbean.list.size() == 2) {
                        String first = warnbean.list.get(0);
                        if (first.equals("省")) {
                            unit = "市";
                            warnList = warnbean.list_3;
                        } else if (first.equals("市")) {
                            unit = "县";
                            warnList = warnbean.list_3;
                        }
                    } else if (warnbean.list.size() == 1) {
                        unit = warnbean.list.get(0);
                        warnList = warnbean.list_2;
                    } else {
                        unit = "";
                    }
                    if (TextUtils.isEmpty(unit)) {
                        views.setTextViewText(R.id.widger_5x2_warn, "暂无预警");
                        views.setViewVisibility(R.id.widger_5x2_warn, View.VISIBLE);
                        views.setViewVisibility(R.id.layout_warn_grid, View.GONE);
                    } else {
                        views.setTextViewText(R.id.tv_unit, unit);
                        views.setViewVisibility(R.id.widger_5x2_warn, View.GONE);
                        views.setViewVisibility(R.id.layout_warn_grid, View.VISIBLE);
                    }

                    for(int i = 0; i < warnImageResourseId.length; i++) {
                        if(warnList.size() > i) {
                            Bitmap bitmap = getWarnPicture(warnList.get(i));
                            if(bitmap != null) {
                                views.setViewVisibility(warnImageResourseId[i], View.VISIBLE);
                                views.setImageViewBitmap(warnImageResourseId[i], bitmap);
                                bundleIntentWarn(context, views, warnList.get(i), warnImageResourseId[i], i);
                                continue;
                            }
                        }
                        views.setViewVisibility(warnImageResourseId[i], View.GONE);
                        views.setImageViewBitmap(warnImageResourseId[i], null);
                    }

                }
            }

        } else {
            // 没数据,清空内容
            views.setTextViewText(R.id.widget_provider_5x2_cityname, "");
            // views.setTextViewText(R.id.widget_4x2_current_temp, "");
            views.setTextViewText(R.id.widget_weather, "");
            views.setTextViewText(R.id.widget_wind, "");
            views.setTextViewText(R.id.widger_5x2_warn, "");
            // views.setTextViewText(R.id.widget_4x2_current_h_l_temp, "");
            views.setImageViewBitmap(R.id.widget_5x2_current_icon, null);
        }
        bundleIntent(context, views);
        AppWidgetManager mrg = AppWidgetManager.getInstance(context);
        ComponentName name = new ComponentName(context, WeatherWidget_5x2.class);
        mrg.updateAppWidget(name, views);
    }

    private Bitmap getWarnPicture(YjxxInfo info) {
        String path = "img_warn/" + info.ico + ".png";
        return getBitmap(path);
    }

    private Bitmap getBitmap(String path) {
        Bitmap bitmap = null;
        InputStream input = null;
        AssetManager asset = PcsInit.getInstance().getContext().getAssets();
        try {
            input = asset.open(path);

            if (input != null) {
                bitmap = BitmapFactory.decodeStream(input);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                }

            }
        }
        return bitmap;
    }

    private void bundleIntent(Context context, RemoteViews views) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setClass(context, ActivityLoading.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.layout_widget_5x2, pendingIntent);
    }

    private void bundleIntentWarn(Context context, RemoteViews views,
                                  YjxxInfo info, int viewid, int index) {
        Bundle bundle = new Bundle();
        bundle.putString("t", "气象预警");
        bundle.putString("i", info.ico);
        bundle.putString("id", info.id);
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.putExtra(MyConfigure.EXTRA_BUNDLE, bundle);
        intent.setClass(context, ActivityWarnDetails.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, index,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(viewid, pendingIntent);
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
}
