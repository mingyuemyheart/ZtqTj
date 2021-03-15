package com.pcs.ztqtj.view.myview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.pcs.lib.lib_pcs_v3.control.log.Log;
import com.pcs.lib.lib_pcs_v3.control.tool.Util;
import com.pcs.lib.lib_pcs_v3.model.image.ImageFetcher;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackHourForecastDown.HourForecast;
import com.pcs.ztqtj.control.inter.OnMyScrollChanged;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Created by tyaathome on 2019/01/15.
 */
public class OceanHour24View extends View {

    //private Context context;
    private MyHScrollView scrollView;
    private boolean isInited = false; // 是否已经初始化
    private Bitmap cursorBitmap;
    private ImageFetcher imageFetcher;
    // 画笔
    private Paint bitmapPaint; // 位图画笔
    private Paint cursorTextPaint; // 游标数值画笔
    private Paint cursorBgPaint; // 游标背景画笔
    private Paint tempLinePaint; // 温度折线画笔
    private Paint rainPaint; // 雨量图画笔
    private Paint rainTextPaint; // 雨量数值画笔
    private Paint linePaint; // 底部分隔线画笔
    // constant
    private float tempTextSize; // 温度字体大小
    private float rainTextSize; // 雨量字体大小
    private float lineWidth; // 线宽
    private int cursorWidth; // 游标长度
    private int cursorHeight; // 游标高度
    private float templineHeight;
    private float rainHeight;
    private int columnCount;
    private float columnWidth; // 每列长度
    private int weatherIconWidth, weatherIconHeight;
    private int imageWidth, imageHeight;
    private float offsetX = 0f;
    private int currentLineIndex = -1; // 当前选中的线段位置
    private int roundCorner;
    private float unitMargin;

    // data
    private List<HourForecast> dataList = new ArrayList<>(); // 总数据
    private List<Float> tempList = new ArrayList<>(); // 温度列表
    private List<Float> rainFallList = new ArrayList<>(); // 雨量列表
    private List<WeatherDescInfo> weatherDescInfoList = new ArrayList<>(); // 天气描述列表
    private List<PointF> tempPositionList = new ArrayList<>(); // 温度坐标列表
    private List<PointF> rainfallPositionList = new ArrayList<>(); // 雨量坐标列表
    private List<PointF> rainfallTextPositionList = new ArrayList<>(); // 雨量数值坐标列表
    private PointF unitPosition; // 雨量单位位置

    {
        tempTextSize = Util.dp2px(12);
        rainTextSize = Util.dp2px(14);
        lineWidth = Util.dp2px(2);
        cursorWidth = (int) Util.dp2px(60);
        cursorHeight = Util.dp2px(25);
        columnWidth = Util.dp2px(60);
        weatherIconWidth = (int) Util.dp2px(20);
        weatherIconHeight = (int) Util.dp2px(20);
        roundCorner = (int) Util.dp2px(5);
        unitMargin = Util.dp2px(10);
    }

    public OceanHour24View(Context context) {
        super(context);
        init(context);
    }

    public OceanHour24View(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        //this.context = context;
        initPaint();
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    private void initPaint() {
        // 位图画笔
        bitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // 游标数值画笔
        cursorTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        cursorTextPaint.setTextSize(tempTextSize);
        cursorTextPaint.setColor(Color.WHITE);
        // 游标背景画笔
        cursorBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        cursorBgPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        cursorBgPaint.setColor(Color.parseColor("#4DFFFFFF"));
        // 温度折线画笔
        tempLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        tempLinePaint.setColor(Color.parseColor("#FFA500"));
        tempLinePaint.setStrokeWidth(lineWidth);
        tempLinePaint.setStrokeJoin(Paint.Join.ROUND);
        tempLinePaint.setStrokeCap(Paint.Cap.ROUND);
        tempLinePaint.setStyle(Paint.Style.STROKE);
        // 雨量图画笔
        rainPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        rainPaint.setColor(Color.parseColor("#784875A5"));
        rainPaint.setStyle(Paint.Style.FILL);
        // 雨量数值画笔
        rainTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        rainTextPaint.setTextSize(rainTextSize);
        rainTextPaint.setColor(Color.WHITE);
        rainTextPaint.setTextAlign(Paint.Align.CENTER);
        // 底部分隔线画笔
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(Color.WHITE);
        linePaint.setStrokeWidth(lineWidth);
        linePaint.setStyle(Paint.Style.STROKE);
    }

    private final Object lock = new Object();
    public void setData(List<HourForecast> list, ImageFetcher imageFetcher) {
        synchronized (lock) {
            if (list == null || list.size() == 0) {
                return;
            }
            dataList = new ArrayList<>(list);
            columnCount = dataList.size();
        }
        this.imageFetcher = imageFetcher;
        updateData();
        redraw();
    }

    private void redraw() {
        if(getWidth() != 0 && getHeight() != 0) {
            preperData();
            isInited = true;
            invalidate();
        }
    }

    private void updateData() {
        tempList = new ArrayList<>();
        rainFallList = new ArrayList<>();
        for (HourForecast forecast : dataList) {
            String rain = forecast.rainfall;
            String temp = forecast.temperature;
            if (!TextUtils.isEmpty(temp)) {
                BigDecimal bd = new BigDecimal(Float.parseFloat(temp));
                float ftemp = bd.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
                tempList.add(ftemp);
            } else {
                continue;
            }
            if (!TextUtils.isEmpty(rain)) {
                rainFallList.add(Float.parseFloat(rain));
            } else {
                rainFallList.add(0f);
            }
        }
        weatherDescInfoList.clear();
        for (int i = 0; i < dataList.size() - 1; i++) {
            HourForecast forecast = dataList.get(i);
            HourForecast nextForecast = dataList.get(i + 1);
            if (!forecast.ico.equals(nextForecast.ico)) {
                WeatherDescInfo info = new WeatherDescInfo();
                info.forecast = nextForecast;
                info.index = i + 1;
                weatherDescInfoList.add(info);
            }
        }
    }

    private void preperData() {
        // 温度折线高度 = (总高度 - 游标高度) / 2
        templineHeight = (getHeight() - cursorHeight) / 2f;
        // 温度折线高度等于雨量图高度
        rainHeight = templineHeight;

        // 计算温度坐标位置(相对坐标，需要加游标高度)
        float dTempValue = 0;
        float minTemp = 0;
        float maxTemp = 0;
        if (tempList.size() != 0) {
            minTemp = Collections.min(tempList);
            maxTemp = Collections.max(tempList);
            dTempValue = maxTemp - minTemp;
        }
        tempPositionList.clear();
        if (dTempValue != 0) {
            for (int i = 0; i < tempList.size(); i++) {
                float x = columnWidth * i + columnWidth / 2f;
                float y = cursorHeight + (maxTemp - tempList.get(i)) / dTempValue * templineHeight;
                tempPositionList.add(new PointF(x, y));
            }
        } else {
            for (int i = 0; i < tempList.size(); i++) {
                float x = columnWidth * i + columnWidth / 2f;
                float y = cursorHeight + templineHeight / 2;
                tempPositionList.add(new PointF(x, y));
            }
        }

        unitPosition = new PointF(unitMargin, getHeight() - unitMargin);

        // 计算雨量坐标位置
        if (rainFallList.size() != 0) {
            float minRain = Collections.min(rainFallList);
            float maxRain = Collections.max(rainFallList);
            float dRainValue = maxRain - minRain;
            rainfallPositionList.clear();
            //rainfallPositionList.add(new PointF(0, getHeight()));
            rainfallTextPositionList.clear();
            if (dRainValue != 0) {
                for (int i = 0; i < rainFallList.size(); i++) {
                    float x = columnWidth * i + columnWidth / 2f;
                    float y = getHeight() - (rainFallList.get(i) - minRain) / dRainValue * rainHeight;
                    rainfallPositionList.add(new PointF(x, y));
                    rainfallTextPositionList.add(new PointF(x, getHeight() - unitMargin));
                }
            }
        }

        for (WeatherDescInfo info : weatherDescInfoList) {
            if (info.index < tempPositionList.size() && info.forecast != null) {
                PointF rainPoint = tempPositionList.get(info.index);
                Bitmap icon = getIconByName(info.forecast);
                if (icon != null) {
                    icon = resizeBitmap(icon, weatherIconWidth, weatherIconHeight);
                    float x = rainPoint.x - icon.getWidth() / 2f;
                    float y = rainPoint.y - icon.getHeight();
                    info.position = new PointF(x, y);
                    info.bitmap = icon;
                }
            }
        }

        imageWidth = (int) (columnWidth * columnCount);
        imageHeight = getHeight();
    }

    private Bitmap getIconByName(HourForecast forecast) {

        String strDay = "";
        if (forecast.isDayTime) {
            strDay = "weather_icon/daytime/w" + forecast.ico + ".png";
        } else {
            strDay = "weather_icon/night/n" + forecast.ico + ".png";
        }
        if (imageFetcher != null) {
            BitmapDrawable bitmapDrawable = imageFetcher.getImageCache().getBitmapFromAssets(strDay);
            if (bitmapDrawable != null) {
                return bitmapDrawable.getBitmap();
            }
        }
        return null;
    }

    private Bitmap resizeBitmap(Bitmap bitmap, int width, int height) {
        return Bitmap.createScaledBitmap(bitmap, width, height, false);
    }

    private void drawChart(Canvas canvas) {
        // 画逐时温度线
        Path tempPath = new Path();
        getBezierPath(tempPath, tempPositionList);
        canvas.drawPath(tempPath, tempLinePaint);
//        for (int i = 0; i < tempPositionList.size() - 1; i++) {
//            PointF p1 = tempPositionList.get(i);
//            PointF p2 = tempPositionList.get(i + 1);
//            canvas.drawLine(p1.x, p1.y, p2.x, p2.y, tempLinePaint);
//        }

        // 画雨量
        Path rainPath = new Path();
        getRainBezierPath(rainPath, rainfallPositionList);
        canvas.drawPath(rainPath, rainPaint);

        if(rainfallTextPositionList != null && rainfallTextPositionList.size() == rainFallList.size()) {
            for(int i = 0; i < rainfallTextPositionList.size(); i++) {
                PointF pointF = rainfallTextPositionList.get(i);
                float value = rainFallList.get(i);
                if(value > 0f) {
                    rainTextPaint.setTextAlign(Paint.Align.CENTER);
                    canvas.drawText(String.valueOf(value), pointF.x, pointF.y, rainTextPaint);
                }
            }
        }
    }

    /**
     * 绘制天气图标
     * @param canvas
     */
    private void drawWeatherIcon(Canvas canvas) {
        // 线长
        float lineWidth = columnWidth * (columnCount-1);
        // 线可移动为距离
        float lineSufLength = lineWidth - getWidth() + cursorWidth;
        float percent = Math.abs(offsetX) / lineSufLength;
        if(percent > 1f) {
            percent = 1f;
        }
        float currentLineWidth = lineWidth * percent;
        for (final WeatherDescInfo info : weatherDescInfoList) {
            if (info.position != null && info.bitmap != null) {
                float left = currentLineWidth;
                float right = currentLineWidth + cursorWidth;
                if (right <= info.position.x || left >= info.position.x + info.bitmap.getWidth()) {
                    android.util.Log.e("crachcrashcrash", String.valueOf(info.index));
                    if(info.alpha != 255 && !info.isDrawing) {
                        //info.setAlpha(255);
                        if(info.animator != null) {
                            info.animator.cancel();
                        }
                        info.animator = ObjectAnimator.ofInt(info, "alpha", 255).setDuration(100);
                        info.animator.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                info.isDrawing = false;
                            }
                        });
                        info.animator.start();
                    }

                } else {
                    if(info.alpha != 0 && !info.isDrawing) {
                        //info.setAlpha(0);
                        if(info.animator != null) {
                            info.animator.cancel();
                        }
                        info.animator = ObjectAnimator.ofInt(info, "alpha", 0).setDuration(100);
                        info.animator.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                info.isDrawing = false;
                            }
                        });
                        info.animator.start();
                    }
                }
                canvas.save();
                // 画天气图标
                canvas.drawBitmap(info.bitmap, info.position.x, info.position.y, info.bitmapPaint);
                canvas.restore();
            }
        }
    }

    /**
     * 获取贝塞尔路径
     *
     * @return
     */
    private void getBezierPath(Path path, List<PointF> pointList) {
        /** 中点集合 */
        List<PointF> mMidPoints = new ArrayList<>();
        /** 中点的中点集合 */
        List<PointF> mMidMidPoints = new ArrayList<>();
        /** 移动后的点集合(控制点) */
        List<PointF> mControlPoints = new ArrayList<>();
        for (int i = 0; i < pointList.size(); i++) {
            PointF midPoint = null;
            if (i == pointList.size() - 1) {
                break;
            } else {
                midPoint = new PointF((pointList.get(i).x + pointList.get(i + 1).x) / 2,
                        (pointList.get(i).y + pointList.get(i + 1).y) / 2);
            }
            mMidPoints.add(midPoint);
        }
        for (int i = 0; i < mMidPoints.size(); i++) {
            PointF midMidPoint = null;
            if (i == mMidPoints.size() - 1) {
                break;
            } else {
                midMidPoint = new PointF((mMidPoints.get(i).x + mMidPoints.get(i + 1).x) / 2,
                        (mMidPoints.get(i).y + mMidPoints.get(i + 1).y) / 2);
            }
            mMidMidPoints.add(midMidPoint);
        }

        for (int i = 0; i < pointList.size(); i++) {
            if (i != 0 && i != pointList.size() - 1) {
                PointF before = new PointF();
                PointF after = new PointF();
                before.x = pointList.get(i).x - mMidMidPoints.get(i - 1).x + mMidPoints.get(i - 1).x;
                before.y = pointList.get(i).y - mMidMidPoints.get(i - 1).y + mMidPoints.get(i - 1).y;
                after.x = pointList.get(i).x - mMidMidPoints.get(i - 1).x + mMidPoints.get(i).x;
                after.y = pointList.get(i).y - mMidMidPoints.get(i - 1).y + mMidPoints.get(i).y;
                mControlPoints.add(before);
                mControlPoints.add(after);
            }
        }
        // 重置路径
        for (int i = 0; i < pointList.size(); i++) {
            if (i == 0) {// 第一条为二阶贝塞尔
                path.moveTo(pointList.get(i).x, pointList.get(i).y);// 起点
                path.quadTo(mControlPoints.get(i).x, mControlPoints.get(i).y,// 控制点
                        pointList.get(i + 1).x, pointList.get(i + 1).y);
            } else if (i < pointList.size() - 2) {// 三阶贝塞尔
                path.cubicTo(mControlPoints.get(2 * i - 1).x, mControlPoints.get(2 * i - 1).y,// 控制点
                        mControlPoints.get(2 * i).x, mControlPoints.get(2 * i).y,// 控制点
                        pointList.get(i + 1).x, pointList.get(i + 1).y);// 终点
            } else if (i == pointList.size() - 2) {// 最后一条为二阶贝塞尔
                path.moveTo(pointList.get(i).x, pointList.get(i).y);// 起点
                path.quadTo(mControlPoints.get(mControlPoints.size() - 1).x,
                        mControlPoints.get(mControlPoints.size() - 1).y,
                        pointList.get(i + 1).x, pointList.get(i + 1).y);// 终点
                //path.lineTo(viewLeftPadding, getHeight()-bottomTextHeight);
            }
        }

    }

    /*绘制降雨量趋势图*/
    private void getRainBezierPath(Path mPath, List<PointF> points) {
        /** 中点集合 */
        List<PointF> mMidPoints = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) {
            PointF midPoint = null;
            if (i == points.size() - 1) {

            } else {
                midPoint = new PointF((points.get(i).x + points.get(i + 1).x) / 2,
                        (points.get(i).y + points.get(i + 1).y) / 2);
                mMidPoints.add(midPoint);
            }
        }

        /** 中点的中点集合 */
        List<PointF> mMidMidPoints = new ArrayList<>();
        for (int i = 0; i < mMidPoints.size(); i++) {
            PointF midMidPoint = null;
            if (i == mMidPoints.size() - 1) {
            } else {
                midMidPoint = new PointF((mMidPoints.get(i).x + mMidPoints.get(i + 1).x) / 2,
                        (mMidPoints.get(i).y + mMidPoints.get(i + 1).y) / 2);
                mMidMidPoints.add(midMidPoint);
            }
        }
        /** 移动后的点集合(控制点) */
        List<PointF> mControlPoints = new ArrayList<>();
//        计算控制点个数
        for (int i = 0; i < points.size(); i++) {
            if (i == 0 || i == points.size() - 1) {
            } else {
                PointF before = new PointF();
                PointF after = new PointF();
                before.x = points.get(i).x - mMidMidPoints.get(i - 1).x + mMidPoints.get(i - 1).x;
                before.y = points.get(i).y - mMidMidPoints.get(i - 1).y + mMidPoints.get(i - 1).y;
                after.x = points.get(i).x - mMidMidPoints.get(i - 1).x + mMidPoints.get(i).x;
                after.y = points.get(i).y - mMidMidPoints.get(i - 1).y + mMidPoints.get(i).y;
                mControlPoints.add(before);
                mControlPoints.add(after);
            }
        }
        // 绘制路径
        for (int i = 0; i < points.size(); i++) {
            if (i == 0) {// 第一条为二阶贝塞尔
                mPath.moveTo(points.get(i).x, points.get(i).y);
                // 起点
            } else if (i == points.size() - 1) {
                if (points.get(i - 1).y > points.get(i).y) {
                    mPath.quadTo((points.get(i).x + points.get(i - 1).x) / 2, points.get(i).y, points.get(i).x,
                            points.get(i).y);// 控制点
                } else {
                    mPath.quadTo((points.get(i).x + points.get(i - 1).x) / 2, points.get(i - 1).y, points.get(i).x,
                            points.get(i).y);// 控制点
                }
                mPath.lineTo(points.get(i).x, getHeight());
                mPath.close();
            } else if (i == 1) {
                mPath.quadTo(mControlPoints.get(i - 1).x, mControlPoints.get(i - 1).y, points.get(i).x,
                        points.get(i).y);// 控制点
            } else if (i < points.size() - 1) {// 三阶贝塞尔
                // 三阶贝塞尔
                if (points.get(i).y == points.get(i - 1).y) {
                    mPath.lineTo(points.get(i).x, points.get(i).y);
                } else {
                    PointF p = points.get(i);// 终点
                    PointF conPa = mControlPoints.get(2 * (i - 2) + 1);// 控制点a
                    PointF conPb = mControlPoints.get(2 * (i - 1));// 控制点b
                    mPath.cubicTo(conPa.x, conPa.y, conPb.x, conPb.y, p.x, p.y);
                }
            }
        }
    }

    float percent;
    private PointF getCursorPosition() {
        // 线长
        float lineWidth = columnWidth * (columnCount - 1);
        // 总长
        float totalWidth = columnWidth * columnCount;
        // 游标可移动的长度
        float cursorSufLength = getWidth() - cursorWidth;
        // 线可移动为距离
        float lineSufLength = lineWidth - getWidth() + cursorWidth;
        percent = Math.abs(offsetX) / lineSufLength;
        if (percent <= 1f) {
            float currentLineWidth = lineWidth * percent;
            // 当前区域线段位置
            int currentIndex = (int) (currentLineWidth / columnWidth);
            if (currentLineIndex != currentIndex) {
                currentLineIndex = currentIndex;
                cursorBitmap = getCursorBitmap();
            }
            // 当前区域线段
            float currentLine = currentLineWidth - columnWidth * currentIndex;
            float linePercent = currentLine / columnWidth;
            android.util.Log.e("currentIndex", String.valueOf(currentIndex) + " linePercent: " + linePercent);
            if (currentIndex < columnCount - 1) {
                PointF p1 = tempPositionList.get(currentIndex);
                PointF p2 = tempPositionList.get(currentIndex + 1);
                float dY = p2.y - p1.y;
                float x = cursorSufLength * percent;
                float y = p1.y + dY * linePercent - cursorHeight;
                return new PointF(x, y);
            } else {
                float x = cursorSufLength * percent;
                float y = tempPositionList.get(columnCount - 1).y - cursorHeight;
                return new PointF(x, y);
            }
        }
        android.util.Log.e("my_percent", String.valueOf(percent));
        float x = cursorSufLength - cursorWidth - (Math.abs(offsetX) - lineSufLength);
        float y = tempPositionList.get(columnCount - 1).y - cursorHeight;
        return new PointF(x, y);
    }

    private Bitmap getCursorBitmap() {
        if (currentLineIndex >= 0 && currentLineIndex < dataList.size()) {
            Bitmap bitmap = Bitmap.createBitmap(cursorWidth, cursorHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            HourForecast forecast = dataList.get(currentLineIndex);
            String strDay = "";
            if (forecast.isDayTime) {
                strDay = "weather_icon/daytime/w" + forecast.ico + ".png";
            } else {
                strDay = "weather_icon/night/n" + forecast.ico + ".png";
            }
            if (imageFetcher != null) {
                BitmapDrawable bitmapDrawable = imageFetcher.getImageCache().getBitmapFromAssets(strDay);
                if (bitmapDrawable != null) {
                    Bitmap iconBm = bitmapDrawable.getBitmap();
                    String value = "";
                    if (!TextUtils.isEmpty(forecast.temperature)) {
                        float temp = Float.parseFloat(forecast.temperature);
                        value = String.format(Locale.getDefault(), "%.1f", temp) + "°";
                    }
                    Rect bound = new Rect();
                    cursorTextPaint.getTextBounds(value, 0, value.length(), bound);
                    float textWidth = bound.width();
                    float textHeight = bound.height();
                    // icon长宽
                    float iconWidth = cursorHeight;
                    // 游标原始宽度
                    float width = cursorWidth;
                    // 游标底图长宽
                    float bmWidth = iconWidth + 10 + bound.width();
                    float bmHeight = cursorHeight;
                    float left = 0;
                    float top = 0;
                    float right = left + cursorWidth;
                    float bottom = top + cursorHeight;
                    canvas.drawRoundRect(new RectF(left, top, right, bottom),
                            roundCorner, roundCorner, cursorBgPaint);
                    Rect src = new Rect(0, 0, iconBm.getWidth(), iconBm.getHeight());
                    // 图标位置
                    // 图标left = 原图left + 内距
                    float bmLeft = left;
                    // 图标top = 原图top + 内距
                    float bmTop = top;
                    // 图标right = 图标left + 图标宽度
                    float bmRight = bmLeft + iconWidth;
                    // 图标bottom = 图标top + 图标宽度
                    float bmBottom = bmTop + iconWidth;
                    RectF dst = new RectF(bmLeft, bmTop, bmRight, bmBottom);
                    canvas.drawBitmap(iconBm, src, dst, bitmapPaint);
                    float textX = bmRight + 10;
                    float textY = (bottom + textHeight)/2f;
                    canvas.drawText(value, textX, textY, cursorTextPaint);
                }
            }
            return bitmap;
        }
        return null;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isInited) {
            canvas.save();
            canvas.translate(offsetX, 0);
            drawChart(canvas);
            drawWeatherIcon(canvas);
            canvas.restore();
            // 画游标
            PointF point = getCursorPosition();
            if (cursorBitmap != null) {
                canvas.drawBitmap(cursorBitmap, point.x, point.y, bitmapPaint);
            }
            // 画底线
            canvas.drawLine(0, getHeight(), imageWidth, getHeight(), linePaint);
            // 画单位
            if(unitPosition != null) {
                rainTextPaint.setTextAlign(Paint.Align.LEFT);
                canvas.drawText("mm", unitPosition.x, unitPosition.y, rainTextPaint);
            }
        }
    }

    public void setParentScrollView(MyHScrollView scrollView) {
        this.scrollView = scrollView;
        this.scrollView.addScrollChangedListener(this, onMyScrollChanged);
    }

    private class WeatherDescInfo {
        HourForecast forecast;
        int index;
        PointF position;
        Bitmap bitmap;
        Paint bitmapPaint;
        int alpha = 255;
        boolean isDrawing = false;
        ObjectAnimator animator;

        WeatherDescInfo() {
            bitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            bitmapPaint.setAlpha(alpha);
        }

        public int getAlpha() {
            return alpha;
        }

        public void setAlpha(int alpha) {
            this.alpha = alpha;
            isDrawing = true;
            android.util.Log.e("alphaalphaalphaalpha", String.valueOf(alpha));
            bitmapPaint.setAlpha(alpha);
            invalidate();
        }
    }

    private OnMyScrollChanged onMyScrollChanged = new OnMyScrollChanged() {
        @Override
        public void onScrollChanged(int l, int t, int oldl, int oldt) {
            Log.e("OceanHour24View", "l:" + l + " t:" + t + " oldl:" + oldl + " oldt" + oldt);
            if(isInited) {
                if (l < 0) {
                    l = 0;
                }
                if (l > imageWidth-getWidth()) {
                    l = imageWidth-getWidth();
                }
            }
            offsetX = -l;
            invalidate();
        }
    };

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        redraw();
    }
}
