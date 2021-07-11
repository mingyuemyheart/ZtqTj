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
import android.os.Bundle;
import android.os.Handler;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.pcs.lib.lib_pcs_v3.PcsInit;
import com.pcs.lib.lib_pcs_v3.control.tool.Util;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCity;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackSstqDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.PackYjxxIndexFbDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.YjxxInfo;
import com.pcs.lib_ztqfj_v2.model.pack.net.week.PackMainWeekWeatherDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.week.WeekWeatherInfo;
import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.MyConfigure;
import com.pcs.ztqtj.control.tool.utils.TextUtil;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.util.OkHttpUtil;
import com.pcs.ztqtj.view.activity.loading.ActivityLoading;
import com.pcs.ztqtj.view.activity.warn.ActivityWarnDetails;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WeatherWidget_5x2 extends AppWidgetProvider {

    private static final String CLICK_ACTION = "com.pcs.ztq.CLICK_ACTION_5x2";
    private Handler mUIHandler = new Handler();
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
        String action = AppWidgetManager.ACTION_APPWIDGET_UPDATE + getClass().getName();
        if (intent != null) {
            if (action.equals(intent.getAction())) {
                requestUpdate(context);
            } else if (CLICK_ACTION.equals(intent.getAction())) {
                startWarnActivity(context, intent.getExtras());
            }
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        update(context);
        super.onUpdate(context, appWidgetManager, appWidgetIds);
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
        int[] ids = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, WeatherWidget_5x2.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        context.sendBroadcast(intent);
    }

    private void update(Context context) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_provider_5x2);
        PackLocalCity cityinfo = ZtqCityDB.getInstance().getCityMain();
        if (cityinfo != null) {
            try {
                // 日期
                views.setTextViewText(R.id.widget_provider_5x2_cityname, cityinfo.NAME);
            } catch (Exception e) {
                e.printStackTrace();
            }
            okHttpSstq(context, cityinfo.ID);
            okHttpWeekData(context);
            okHttpWarningImages(context);
        } else {
            // 没数据,清空内容
            views.setTextViewText(R.id.widget_provider_5x2_cityname, "");
            // views.setTextViewText(R.id.widget_5x2_current_temp, "");
            views.setTextViewText(R.id.widget_weather, "");
            views.setTextViewText(R.id.widget_wind, "");
            views.setTextViewText(R.id.widget_date, "");
            views.setTextViewText(R.id.widger_5x2_warn, "");
            // views.setTextViewText(R.id.widget_5x2_current_h_l_temp, "");
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
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.layout_widget_5x2, pendingIntent);
    }

    private void bundleIntentWarn(Context context, RemoteViews views, YjxxInfo info, int viewid, int index) {
        Bundle bundle = new Bundle();
        bundle.putString("t", "气象预警");
        bundle.putString("i", info.ico);
        bundle.putString("id", info.id);
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.putExtra(MyConfigure.EXTRA_BUNDLE, bundle);
        intent.setClass(context, ActivityWarnDetails.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, index, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(viewid, pendingIntent);
    }

    private Bitmap createTextBitmap(Context context, final String text, final float textSizePixels, final int textColor) {
        Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/Arial.ttf");
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

    /**
     * 获取实况信息
     */
    private void okHttpSstq(final Context context, final String stationId) {
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
                            mUIHandler.post(new Runnable() {
                                @Override
                                public void run() {
//                                    Log.e("sstq", result);
                                    if (!TextUtil.isEmpty(result)) {
                                        try {
                                            JSONObject obj = new JSONObject(result);
                                            if (!obj.isNull("b")) {
                                                JSONObject bobj = obj.getJSONObject("b");
                                                if (!bobj.isNull("sstq")) {
                                                    JSONObject sstqobj = bobj.getJSONObject("sstq");
                                                    if (!sstqobj.isNull("sstq")) {
                                                        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_provider_5x2);
                                                        PackSstqDown pack = new PackSstqDown();
                                                        pack.fillData(sstqobj.toString());
                                                        if (pack.sys_time == null || "".equals(pack.sys_time)) {
                                                            return;
                                                        }
                                                        // 当前气温，风描述
                                                        int textColor = context.getResources().getColor(R.color.widget_text);
                                                        views.setImageViewBitmap(R.id.widget_5x2_current_temp, createTextBitmap(context, pack.ct + "℃", Util.dip2px(context, 33), textColor));
                                                        if (pack.fl == null || "".equals(pack.fl)) {
                                                            views.setTextViewText(R.id.widget_wind, "暂无");
                                                        } else {
                                                            views.setTextViewText(R.id.widget_wind, pack.winddir_current + pack.fl);
                                                        }

                                                        bundleIntent(context, views);
                                                        AppWidgetManager mrg = AppWidgetManager.getInstance(context);
                                                        ComponentName name = new ComponentName(context, WeatherWidget_5x2.class);
                                                        mrg.updateAppWidget(name, views);
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
     * 获取一周天气
     */
    private void okHttpWeekData(final Context context) {
        final PackLocalCity city = ZtqCityDB.getInstance().getCityMain();
        if(city == null) return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param  = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    JSONObject info = new JSONObject();
                    info.put("stationId", city.ID);
                    param.put("paramInfo", info);
                    String json = param.toString();
                    Log.e("week_data", json);
                    final String url = CONST.BASE_URL+"week_data";
                    Log.e("week_data", url);
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
                            mUIHandler.post(new Runnable() {
                                @Override
                                public void run() {
//                                    Log.e("week_data", result);
                                    if (!TextUtil.isEmpty(result)) {
                                        try {
                                            JSONObject obj = new JSONObject(result);
                                            if (!obj.isNull("b")) {
                                                JSONObject bobj = obj.getJSONObject("b");
                                                if (!bobj.isNull("p_new_week")) {
                                                    JSONObject p_new_weekobj = bobj.getJSONObject("p_new_week");
                                                    if (!TextUtil.isEmpty(p_new_weekobj.toString())) {
                                                        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_provider_5x2);
                                                        PackMainWeekWeatherDown pcsDownPack = new PackMainWeekWeatherDown();
                                                        pcsDownPack.fillData(p_new_weekobj.toString());
                                                        try {
                                                            if (pcsDownPack != null) {
                                                                WeekWeatherInfo info = pcsDownPack.getToday();
                                                                if (info != null && !(TextUtils.isEmpty(info.higt) || TextUtils.isEmpty(info.lowt))) {
                                                                    views.setTextViewText(R.id.widget_weather, info.weather);
                                                                    final String maxTemp = info.higt;// 最高气温
                                                                    final String minTemp = info.lowt;// 最低气温
                                                                    String temperHight = maxTemp + "℃/" + minTemp + "℃";
                                                                    if (!TextUtils.isEmpty(temperHight)) {
                                                                        views.setImageViewBitmap(R.id.widget_5x2_current_h_l_temp, createTextBitmap(
                                                                                context, temperHight, Util.dip2px(context, 20), context.getResources().getColor(R.color.text_white)));
                                                                    }
                                                                }
                                                                // 天气图标
                                                                try {
                                                                    String path = pcsDownPack.getIconPath(pcsDownPack.getTodayIndex());
                                                                    if (TextUtils.isEmpty(path)) {
                                                                        views.setImageViewBitmap(R.id.widget_5x2_current_icon, null);
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
                                                                            views.setImageViewBitmap(R.id.widget_5x2_current_icon, bitmap);
                                                                        }
                                                                    }
                                                                } catch (Exception e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }

                                                        bundleIntent(context, views);
                                                        AppWidgetManager mrg = AppWidgetManager.getInstance(context);
                                                        ComponentName name = new ComponentName(context, WeatherWidget_5x2.class);
                                                        mrg.updateAppWidget(name, views);
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
     * 获取预警，首页预警图标
     */
    private void okHttpWarningImages(final Context context) {
        final PackLocalCity city = ZtqCityDB.getInstance().getCityMain();
        if(city == null) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param  = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    JSONObject info = new JSONObject();
                    info.put("stationId", city.ID);
                    param.put("paramInfo", info);
                    String json = param.toString();
                    Log.e("yjxx_index_fb_list", json);
                    final String url = CONST.BASE_URL+"yjxx_index_fb_list";
                    Log.e("yjxx_index_fb_list", url);
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
                            mUIHandler.post(new Runnable() {
                                @Override
                                public void run() {
//                                    Log.e("yjxx_index_fb_list", result);
                                    if (!TextUtil.isEmpty(result)) {
                                        try {
                                            JSONObject obj = new JSONObject(result);
                                            if (!obj.isNull("b")) {
                                                JSONObject bobj = obj.getJSONObject("b");
                                                if (!bobj.isNull("yjxx_index_fb_list")) {
                                                    JSONObject listobj = bobj.getJSONObject("yjxx_index_fb_list");
                                                    if (!TextUtil.isEmpty(listobj.toString())) {
                                                        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_provider_5x2);
                                                        PackYjxxIndexFbDown warnbean = new PackYjxxIndexFbDown();
                                                        warnbean.fillData(listobj.toString());
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

                                                            for (int i = 0; i < warnImageResourseId.length; i++) {
                                                                if (warnList.size() > i) {
                                                                    Bitmap bitmap = getWarnPicture(warnList.get(i));
                                                                    if (bitmap != null) {
                                                                        views.setViewVisibility(warnImageResourseId[i], View.VISIBLE);
                                                                        views.setImageViewBitmap(warnImageResourseId[i], bitmap);
                                                                        bundleIntentWarn(context, views, warnList.get(i), warnImageResourseId[i], i);
                                                                        continue;
                                                                    }
                                                                }
                                                                views.setViewVisibility(warnImageResourseId[i], View.GONE);
                                                                views.setImageViewBitmap(warnImageResourseId[i], null);
                                                                views.setOnClickPendingIntent(warnImageResourseId[i], null);
                                                            }
                                                        }

                                                        bundleIntent(context, views);
                                                        AppWidgetManager mrg = AppWidgetManager.getInstance(context);
                                                        ComponentName name = new ComponentName(context, WeatherWidget_5x2.class);
                                                        mrg.updateAppWidget(name, views);
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

}
