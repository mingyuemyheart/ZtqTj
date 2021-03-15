package com.pcs.ztqtj.view.appwidget;

import android.annotation.TargetApi;
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
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.widget.RemoteViews;

import com.pcs.lib.lib_pcs_v3.PcsInit;
import com.pcs.lib.lib_pcs_v3.control.tool.Util;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCity;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityMain;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackForecastWeatherTipDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackForecastWeatherTipUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackSstqDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackSstqUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.airinfopack.PackAirInfoSimpleDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.airinfopack.PackAirInfoSimpleUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.PackYjxxIndexFbDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.PackYjxxIndexFbUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.YjxxInfo;
import com.pcs.lib_ztqfj_v2.model.pack.net.week.PackMainWeekWeatherDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.week.PackMainWeekWeatherUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.week.WeekWeatherInfo;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.LunarCalendar;
import com.pcs.ztqtj.control.tool.MyConfigure;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.view.activity.loading.ActivityLoading;
import com.pcs.ztqtj.view.activity.newairquality.ActivityAirQualityQuery;
import com.pcs.ztqtj.view.activity.warn.ActivityWarnDetails;
import com.pcs.ztqtj.view.fragment.airquality.ActivityAirQualitySH;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by tyaathome on 2016/6/23.
 */
public class WeatherWidget_5x3 extends AppWidgetProvider {

    private static final String CLICK_ACTION = "com.pcs.ztq.CLICK_ACTION_5x3";
    private int[] warnImageResourseId = {R.id.iv1, R.id.iv2, R.id.iv3, R.id.iv4};

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        //updateWarnGrid(context, appWidgetManager, appWidgetIds);
        update(context, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                + getClass().getName();
        if(intent != null) {
            if (action.equals(intent.getAction())) {
                //update(context);
                requestUpdate(context);
            } else if (CLICK_ACTION.equals(intent.getAction())) {
                startWarnActivity(context, intent.getExtras());
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void updateWarnGrid(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for(int id : appWidgetIds) {
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_provider_5x3);
            Intent intent = new Intent(context, WarnGridService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            rv.setRemoteAdapter(id, R.id.list_warn, intent);
            //rv.setViewPadding(R.id.layout_warn_grid, 150,0,150,0);
            Intent clickIntent = new Intent(context, WeatherWidget_5x3.class);
            clickIntent.setAction(WeatherWidget_5x3.CLICK_ACTION);
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
        Intent intent = new Intent(context, WeatherWidget_5x3.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
// Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
// since it seems the onUpdate() is only fired on that:
        int[] ids = AppWidgetManager.getInstance(context)
                .getAppWidgetIds(new ComponentName(context, WeatherWidget_5x3.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        context.sendBroadcast(intent);
    }

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
        //update(context);
    }

    private void update(Context context, int[] appWidgetIds) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_provider_5x3);

        // 字体颜色
        int textColor = context.getResources().getColor(R.color.text_white);

        PackLocalCity cityinfo = ZtqCityDB.getInstance().getCityMain();
        if (cityinfo != null) {
            // 设置当前城市
            views.setTextViewText(R.id.tv_current_city, cityinfo.NAME);

            // 设置预警信息
            //PackYjxxIndexUp mPackYjxxUp = new PackYjxxIndexUp();
            PackYjxxIndexFbUp mPackYjxxUp = new PackYjxxIndexFbUp();
            mPackYjxxUp.setCity(cityinfo);
            PackYjxxIndexFbDown packYjxxDown = (PackYjxxIndexFbDown) PcsDataManager.getInstance().getNetPack(mPackYjxxUp
                    .getName());
            if (packYjxxDown == null) {
                views.setTextViewText(R.id.tv_warning, "暂无预警");
                views.setViewVisibility(R.id.tv_warning, View.VISIBLE);
                views.setViewVisibility(R.id.layout_warn_grid, View.GONE);
            } else {
                String unit = "";
                List<YjxxInfo> warnList = new ArrayList<>();
                if(packYjxxDown.list.size() == 2) {
                    String first = packYjxxDown.list.get(0);
                    if(first.equals("省")) {
                        unit = "市";
                        warnList = packYjxxDown.list_3;
                    } else if(first.equals("市")) {
                        unit = "县";
                        warnList = packYjxxDown.list_3;
                    }
                } else if(packYjxxDown.list.size() == 1) {
                    unit = packYjxxDown.list.get(0);
                    warnList = packYjxxDown.list_2;
                } else {
                    unit = "";
                }
                if(TextUtils.isEmpty(unit)) {
                    views.setTextViewText(R.id.tv_warning, "当前暂无预警");
                    views.setViewVisibility(R.id.tv_warning, View.VISIBLE);
                    views.setViewVisibility(R.id.layout_warn_grid, View.GONE);
                } else {
                    views.setTextViewText(R.id.tv_unit, unit);
                    views.setViewVisibility(R.id.tv_warning, View.GONE);
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

            // 设置实时天气
            PackSstqUp mPackSstqUp = new PackSstqUp();
            mPackSstqUp.area = cityinfo.ID;
            PackSstqDown packSstq = (PackSstqDown) PcsDataManager.getInstance().getNetPack(mPackSstqUp.getName());
            if (packSstq != null && !TextUtils.isEmpty(packSstq.ct)) {
                views.setImageViewBitmap(R.id.iv_temperature, createTextBitmap(context, packSstq.ct + "℃", 48,
                        textColor));
            } else {
                //views.setTextViewText(R.id.iv_temperature, "");
            }

            // 空气质量
            PackAirInfoSimpleUp mPackAirUp = new PackAirInfoSimpleUp();
            mPackAirUp.setCity(cityinfo);
            mPackAirUp.type = "1";
            PackAirInfoSimpleDown packAir = (PackAirInfoSimpleDown) PcsDataManager.getInstance().getNetPack
                    (mPackAirUp.getName());
            if (packAir != null && !TextUtils.isEmpty(packAir.airInfoSimple.quality)) {
                views.setViewVisibility(R.id.tv_aqi, View.VISIBLE);
                views.setTextViewText(R.id.tv_aqi, context.getResources().getString(R.string.air) + packAir
                        .airInfoSimple.quality);
                bundleIntentAqi(context, views);
                //views.setInt(R.id.tv_aqi, "setBackgroundResource", getAqiDrawable(packAir.airInfoSimple.aqi));
            } else {
                views.setViewVisibility(R.id.tv_aqi, View.GONE);
                views.setTextViewText(R.id.tv_aqi, "暂无");
            }

            // 温馨提示
            //PackLocalCityLocation location = ZtqLocationTool.getInstance().getLocationCity();
            String tips = getTips();
            if (!TextUtils.isEmpty(tips)) {
                views.setTextViewText(R.id.tv_tip, context.getResources().getString(R.string.near_the_weather) +
                        tips);
            } else {
                views.setTextViewText(R.id.tv_tip, context.getResources().getString(R.string.near_the_weather_getting));
            }

            // 日期
            PackMainWeekWeatherUp mPackWeekUp = new PackMainWeekWeatherUp();
            mPackWeekUp.setCity(cityinfo);
            PackMainWeekWeatherDown packWeekDown = (PackMainWeekWeatherDown) PcsDataManager.getInstance().getNetPack
                    (mPackWeekUp.getName());
            //---公历日期
            Date date = new Date(System.currentTimeMillis());
            //---农历日期
            LunarCalendar lunar = new LunarCalendar(date);
            String sLunar = "农历" + lunar.toString().substring(2);
            //views.setTextViewText(R.id.tv_lunar_calendar, sLunar);
            views.setImageViewBitmap(R.id.iv_lunar_calendar, createTextBitmap(context, sLunar, 14, textColor));

            // 三天天气
            if (packWeekDown != null && packWeekDown.getThreeDay().size() >= 3) {
                views.setViewVisibility(R.id.layout_three_day, View.VISIBLE);
                WeekWeatherInfo info0 = packWeekDown.getThreeDay().get(0);
                views.setTextViewText(R.id.tv_date_0, context.getResources().getString(R.string.today));
                views.setImageViewBitmap(R.id.iv_icon_day_0, getIcon(context, info0, false));
                views.setImageViewBitmap(R.id.iv_day_temp_0, createTextBitmap(context, info0.higt + "℃", 15,
                        textColor));
                views.setImageViewBitmap(R.id.iv_icon_night_0, getIcon(context, info0, true));
                views.setImageViewBitmap(R.id.iv_night_temp_0, createTextBitmap(context, info0.lowt + "℃", 15,
                        textColor));

                WeekWeatherInfo info1 = packWeekDown.getThreeDay().get(1);
                views.setTextViewText(R.id.tv_date_1, info1.week);
                views.setImageViewBitmap(R.id.iv_icon_day_1, getIcon(context, info1, false));
                views.setImageViewBitmap(R.id.iv_day_temp_1, createTextBitmap(context, info1.higt + "℃", 15,
                        textColor));
                views.setImageViewBitmap(R.id.iv_icon_night_1, getIcon(context, info1, true));
                views.setImageViewBitmap(R.id.iv_night_temp_1, createTextBitmap(context, info1.lowt + "℃", 15,
                        textColor));

                WeekWeatherInfo info2 = packWeekDown.getThreeDay().get(2);
                views.setTextViewText(R.id.tv_date_2, info2.week);
                views.setImageViewBitmap(R.id.iv_icon_day_2, getIcon(context, info2, false));
                views.setImageViewBitmap(R.id.iv_day_temp_2, createTextBitmap(context, info2.higt + "℃", 15,
                        textColor));
                views.setImageViewBitmap(R.id.iv_icon_night_2, getIcon(context, info2, true));
                views.setImageViewBitmap(R.id.iv_night_temp_2, createTextBitmap(context, info2.lowt + "℃", 15,
                        textColor));
            } else {
                views.setViewVisibility(R.id.layout_three_day, View.INVISIBLE);
            }

        } else {
            views.setTextViewText(R.id.tv_current_city, "");
            views.setTextViewText(R.id.tv_warning, "当前暂无预警");
            views.setTextViewText(R.id.tv_tip, context.getResources().getString(R.string.near_the_weather_getting));
            //views.setTextViewText(R.id.tv_lunar_calendar, "");
        }
        bundleIntent(context, views);
        AppWidgetManager mrg = AppWidgetManager.getInstance(context);
        for (int id : appWidgetIds) {
            mrg.updateAppWidget(id, views);
        }

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

    /**
     * 点击打开应用
     *
     * @param context
     * @param views
     */
    private void bundleIntent(Context context, RemoteViews views) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setClass(context, ActivityLoading.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.layout_widget_5x3, pendingIntent);
    }

    /**
     * 点击预警打开预警详情
     *
     * @param context
     * @param views
     */
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

    private void bundleIntentAqi(Context context, RemoteViews views) {
        PackLocalCityMain cityMain = ZtqCityDB.getInstance().getCityMain();
//        if (cityMain.isFjCity) {
//            ActivityAirQualityQuery.setCity(cityMain.PARENT_ID, cityMain.CITY);
//        } else {
//            ActivityAirQualityQuery.setCity(cityMain.ID, cityMain.CITY);
//        }
        String id = "", name = "";
        Class<?> cls;
        if (cityMain.isFjCity) {
            id = cityMain.ID;
            name = cityMain.CITY;
            cls = ActivityAirQualitySH.class;
        } else {
            id = cityMain.ID;
            name = cityMain.CITY;
            cls = ActivityAirQualityQuery.class;
        }
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        bundle.putString("name", name);
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.putExtra("id",id);
        intent.putExtra("name",name);
        intent.setClass(context, cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtras(bundle);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.tv_aqi, pendingIntent);
    }

    private
    @DrawableRes
    int getAqiDrawable(String value) {
        int[] res = {R.drawable.bg_widget_5x3_aqi_0,
                R.drawable.bg_widget_5x3_aqi_1,
                R.drawable.bg_widget_5x3_aqi_2,
                R.drawable.bg_widget_5x3_aqi_3,
                R.drawable.bg_widget_5x3_aqi_4,
                R.drawable.bg_widget_5x3_aqi_5};
        int aqi = Integer.valueOf(value);
        if (aqi <= 50) {
            // 优
            return res[0];
        } else if (aqi > 50 && aqi <= 100) {
            // 良
            return res[1];
        } else if (aqi > 100 && aqi <= 150) {
            // 轻度污染
            return res[2];
        } else if (aqi > 150 && aqi <= 200) {
            // 中度污染
            return res[3];
        } else if (aqi > 200 && aqi <= 300) {
            // 重度污染
            return res[4];
        } else if (aqi > 300) {
            // 严重污染
            return res[5];
        }

        return res[0];
    }

//    /**
//     * 获取温馨提示
//     *
//     * @param packCity
//     * @return
//     */
//    private String getTips(PackLocalCity packCity) {
//        // 二级城市信息
//        PackLocalCity city_2 = ZtqCityDB.getInstance().getCityInfo2_ID(packCity.PARENT_ID);
//        if (city_2 != null) {
//            // 一级城市信息
//            PackLocalCity city_1 = ZtqCityDB.getInstance().getCityInfo1_ID(city_2.PARENT_ID);
//            if (city_1 != null) {
//                // 省级信息
//                PackLocalCity province = ZtqCityDB.getInstance().getProvinceById(city_1.PARENT_ID);
//                if (province != null) {
//                    PackForecastWeatherTipUp mPackTipUp = new PackForecastWeatherTipUp();
//                    mPackTipUp.province = province.NAME;
//                    PackForecastWeatherTipDown packForecastWeatherTipDown = (PackForecastWeatherTipDown)
//                            PcsDataManager.getInstance().getNetPack(mPackTipUp.getName());
//                    if (packForecastWeatherTipDown != null) {
//                        return packForecastWeatherTipDown.tip;
//                    }
//                }
//            }
//        }
//        return "";
//    }

    /**
     * 获取温馨提示
     *
     * @return
     */
    private String getTips() {
        //PackForecastWeatherTipDown packForecastWeatherTipDown = AutoDownloadWeather.getInstance()
        // .getPackForecastWeatherTipDown();
        PackForecastWeatherTipDown packForecastWeatherTipDown = (PackForecastWeatherTipDown) PcsDataManager
                .getInstance().getNetPack(PackForecastWeatherTipUp.NAME);
        if (packForecastWeatherTipDown != null) {
            return packForecastWeatherTipDown.tip;
        }
        return "";
    }

    /**
     * 获取图标
     *
     * @param context
     * @param info
     * @return
     */
    private Bitmap getIcon(Context context, WeekWeatherInfo info, boolean isNight) {
        String path = "";
        if (!isNight) {
            path = "weather_icon/daytime/w" + info.wd_day_ico + ".png";
        } else {
            path = "weather_icon/night/n" + info.wd_night_ico + ".png";
        }
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
            int width = Util.dip2px(context, 30);
            int height = Util.dip2px(context, 30);
            Matrix m = new Matrix();
            m.setRectToRect(
                    new RectF(0, 0, bitmap.getWidth(),
                            bitmap.getHeight()),
                    new RectF(0, 0, width, height),
                    Matrix.ScaleToFit.CENTER);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0,
                    bitmap.getWidth(),
                    bitmap.getHeight(), m, true);
        }
        return bitmap;
    }

    /**
     * 设置字体
     *
     * @param context
     * @param text
     * @param textSizePixels
     * @param textColor
     * @return
     */
    private Bitmap createTextBitmap(Context context, final String text,
                                    float textSizePixels, final int textColor) {
        textSizePixels = Util.dip2px(context, textSizePixels);
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
