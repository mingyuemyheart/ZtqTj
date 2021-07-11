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
import android.os.Handler;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import com.pcs.lib.lib_pcs_v3.control.tool.Util;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCity;
import com.pcs.lib_ztqfj_v2.model.pack.net.week.PackMainWeekWeatherDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.week.WeekWeatherInfo;
import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.utils.TextUtil;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.util.OkHttpUtil;
import com.pcs.ztqtj.view.activity.loading.ActivityLoading;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WeatherWidget_5x1 extends AppWidgetProvider {

    private Handler mUIHandler = new Handler();

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String action = AppWidgetManager.ACTION_APPWIDGET_UPDATE + getClass().getName();
        if (action.equals(intent.getAction())) {
            update(context);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
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
    }

    private void update(Context context) {
        okHttpWeekData(context);
    }

    private void bundleIntent(Context context, RemoteViews views) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setClass(context, ActivityLoading.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.layout_widget_5x1, pendingIntent);
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
                                                        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_provider_4x2);
                                                        PackMainWeekWeatherDown pcsDownPack = new PackMainWeekWeatherDown();
                                                        pcsDownPack.fillData(p_new_weekobj.toString());
                                                        if (pcsDownPack == null) {
                                                            views.setTextViewText(R.id.widget_weather, "");
                                                        } else {
                                                            WeekWeatherInfo info = pcsDownPack.getToday();
                                                            // 高低温度
                                                            try {
                                                                final String maxTemp = info.higt;// 最高气温
                                                                final String minTemp = info.lowt;// 最低气温
                                                                String temperHight = maxTemp + "℃~" + minTemp + "℃";
                                                                views.setImageViewBitmap(R.id.widget_5x1_current_h_l_temp, createTextBitmap(context, temperHight,
                                                                                Util.dip2px(context, 20), context.getResources().getColor(R.color.text_white)));
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                            // 天气与天气图标
                                                            try {
                                                                views.setTextViewText(R.id.widget_weather, info.weather);
                                                                String path = pcsDownPack.getIconPath(pcsDownPack.getTodayIndex());
                                                                if (TextUtils.isEmpty(path)) {
                                                                    views.setImageViewBitmap(R.id.widget_5x1_current_icon, null);
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
                                                                        m.setRectToRect(new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight()),
                                                                                new RectF(0, 0, width, height),
                                                                                Matrix.ScaleToFit.CENTER);
                                                                        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
                                                                        views.setImageViewBitmap(R.id.widget_5x1_current_icon, bitmap);
                                                                    }
                                                                }
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                        // 设置时间、日期、区域
                                                        try {
                                                            if (pcsDownPack != null && !TextUtils.isEmpty(pcsDownPack.sys_time)) {
                                                                views.setTextViewText(R.id.widget_5x1_time_info, city.NAME);
                                                            }
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }

                                                        bundleIntent(context, views);
                                                        AppWidgetManager mrg = AppWidgetManager.getInstance(context);
                                                        ComponentName name = new ComponentName(context, WeatherWidget_4x2.class);
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
