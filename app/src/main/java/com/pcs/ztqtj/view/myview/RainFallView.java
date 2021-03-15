package com.pcs.ztqtj.view.myview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import com.pcs.lib.lib_pcs_v3.control.tool.Util;

import java.util.Random;

public class RainFallView extends View {
    /**
     * 趋势图的高度
     */
    private int viewHeight;

    /**
     * 趋势图的宽度
     */
    private int viewWidth;
    private Paint h_point;
    private Paint l_point;
    private Paint lineMax;
    private Paint lineAvg;
    private Paint lineCom;
    private Paint mTextPaint;
    private Paint mTextPaintUnit;

    private Paint line1;
    private Paint line2;
    private Paint line3;
    private Paint line4;

    private float hight_value;
    private float[] h_rain = {0, 0, 0, 0, 0, 0};
    private float[] l_rain = {0, 0, 0, 0, 0, 0};
    private float[][] rect;
    private String[] h_val;
    private float section = 0;
    private final int COUNTPOINT = 6;
    private float CircleR = 10;

    private String unit = "雨量";

    public void setUnit(String unit) {
        this.unit = unit;
    }


    public RainFallView(Context context) {
        super(context);
    }

    public RainFallView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        h_val = new String[COUNTPOINT];
        rect = new float[COUNTPOINT][4];
        Random r = new Random();
        for (int i = 0; i < rect.length; i++) {
            for (int j = 0; j < rect[i].length; j++) {
                rect[i][j] = 1;
            }
        }
    }

    private int avgLineWidth = 0;

    private void init(Context context) {
        lineWidth = Util.dip2px(context, 7);
        avgLineWidth = Util.dip2px(context, 2);
        CircleR = Util.dip2px(context, 5);
        // 最高点
        h_point = new Paint();
        h_point.setAntiAlias(true);
        h_point.setColor(Color.argb(255, 255, 0, 0));
        // 平均点
        l_point = new Paint();
        l_point.setAntiAlias(true);
        l_point.setColor(Color.argb(255, 237, 136, 16));
        // 公共线
        lineCom = new Paint();
        lineCom.setAntiAlias(true);
        lineCom.setStrokeWidth(1);
        lineCom.setColor(Color.argb(255, 170, 170, 170));

        // 最高点线
        lineMax = new Paint();
        lineMax.setAntiAlias(true);
        lineMax.setStrokeWidth(avgLineWidth);
        lineMax.setColor(Color.argb(255, 255, 0, 0));
        // 平均点线
        lineAvg = new Paint();
        lineAvg.setAntiAlias(true);
        lineAvg.setStrokeWidth(avgLineWidth);
        lineAvg.setColor(Color.argb(255, 237, 136, 16));
        // 文字颜色
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(Color.argb(255, 170, 170, 170));
        mTextPaint.setTextSize(25F);
        mTextPaint.setTypeface(Typeface.create("宋体", Typeface.BOLD));
        mTextPaint.setTextAlign(Align.CENTER);


        mTextPaintUnit = new Paint();
        mTextPaintUnit.setAntiAlias(true);
        mTextPaintUnit.setColor(Color.argb(255, 170, 170, 170));
        mTextPaintUnit.setTextSize(Util.dip2px(context, 14));
        mTextPaintUnit.setTypeface(Typeface.create("宋体", Typeface.BOLD));
        mTextPaintUnit.setTextAlign(Align.CENTER);

        line1 = new Paint();
        line1.setAntiAlias(true);
        line1.setStrokeWidth(lineWidth);
        line1.setColor(Color.argb(255, 7, 73, 143));
        line2 = new Paint();
        line2.setAntiAlias(true);
        line2.setStrokeWidth(lineWidth);
        line2.setColor(Color.argb(255, 24, 202, 214));
        line3 = new Paint();
        line3.setAntiAlias(true);
        line3.setStrokeWidth(lineWidth);
        line3.setColor(Color.argb(255, 54, 234, 143));
        line4 = new Paint();
        line4.setAntiAlias(true);
        line4.setStrokeWidth(lineWidth);
        line4.setColor(Color.argb(255, 7, 165, 255));
    }

    private int lineWidth = 15;// 柱状图宽

    /**
     * 获取最大值
     *
     * @return
     */
    private float getMax() {
        float MaxR = 0;
        for (int i = 0; i < h_rain.length; i++) {
            if (h_rain[i] > MaxR) {
                MaxR = h_rain[i];
            }
        }
        for (int i = 0; i < l_rain.length; i++) {
            if (l_rain[i] > MaxR) {
                MaxR = l_rain[i];
            }
        }
        for (int i = 0; i < rect.length; i++) {
            for (int j = 0; j < rect[i].length; j++) {
                if (rect[i][j] > MaxR) {
                    MaxR = rect[i][j];
                }
            }
        }
        return MaxR;
    }


    /**
     * 获取最大值
     *
     * @return
     */
    private float getMin() {
        float MaxR = 0;
        for (int i = 0; i <h_rain.length; i++) {
            if (h_rain[i] < MaxR) {
                MaxR = h_rain[i];
            }
        }
        for (int i = 0; i < l_rain.length; i++) {
            if (l_rain[i] < MaxR) {
                MaxR = l_rain[i];
            }
        }
        for (int i = 0; i < rect.length; i++) {
            for (int j = 0; j < rect[i].length; j++) {
                if (rect[i][j] < MaxR) {
                    MaxR = rect[i][j];
                }
            }
        }
        return MaxR;
    }

    /**
     * 计算竖直区间值
     *
     * @return
     */
    private void getValue() {
        float max_value = getMax();//最大值
        float min_value = getMin();//最小值
        section = (float) ((max_value - min_value) / 5);
        for (int i = 0; i < h_val.length; i++) {
            h_val[i] = (int) (section * (h_val.length - (i + 1))) + "";
        }
    }

    /**
     * 获取高度值
     *
     * @param all_rain
     * @return
     */
    private float[] geth(float[] all_rain) {
        float[] hihgt = new float[COUNTPOINT];
        float hight = section * COUNTPOINT;
        for (int j = 0; j < all_rain.length; j++) {
            float value = (all_rain[j] - getMin()) / hight;
            float value2 = viewHeight * value;
            hihgt[j] = value2;
        }
        return hihgt;
    }

    /**
     * 获取柱状图的高度
     *
     * @param all_rain
     * @return
     */
    private float[][] gethrect(float[][] all_rain) {
        float[][] rect = new float[COUNTPOINT][4];
        float hight = section * COUNTPOINT;
        for (int j = 0; j < all_rain.length; j++) {
            for (int i = 0; i < all_rain[j].length; i++) {
                float value = (all_rain[j][i] - getMin()) / hight;
                float value2 = viewHeight * value;
                rect[j][i] = value2;
            }
        }
        return rect;
    }

    public void setViewData(float[] h_rain, float[] l_rain, float[][] rect) {
        this.h_rain = h_rain;
        this.l_rain = l_rain;
        this.rect = rect;
        this.invalidate();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        int magrLeft = Util.dip2px(getContext(), 5);
        viewHeight = getHeight();
        viewWidth = getWidth() - magrLeft;
        getValue();
        float[] dh_rain = geth(h_rain);
        float[] dl_rain = geth(l_rain);
        float[][] rectaaa = gethrect(rect);
        FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        // 计算文字高度
        float fontHeight = fontMetrics.bottom - fontMetrics.top;
        for (int i = 0; i < rectaaa.length; i++) {
            for (int j = 0; j < rectaaa.length; j++) {
                if (j == 0) {

                    canvas.drawLine((viewWidth / dl_rain.length * (i + 1)) - 2 * CircleR - 2 * lineWidth, viewHeight,
                            (viewWidth / dl_rain.length * (i + 1)) - 2 * CircleR - 2 * lineWidth,
                            viewHeight - rectaaa[i][j], line1);
                } else if (j == 1) {

                    canvas.drawLine((viewWidth / dl_rain.length * (i + 1)) - 2 * CircleR - lineWidth, viewHeight,
                            (viewWidth / dl_rain.length * (i + 1)) - 2 * CircleR - lineWidth,
                            viewHeight - rectaaa[i][j], line2);
                } else if (j == 2) {
                    canvas.drawLine((viewWidth / dl_rain.length * (i + 1)) - 2 * CircleR + 0, viewHeight,
                            (viewWidth / dl_rain.length * (i + 1)) - 2 * CircleR + 0, viewHeight - rectaaa[i][j],
                            line3);

                } else if (j == 3) {
                    canvas.drawLine((viewWidth / dl_rain.length * (i + 1)) - 2 * CircleR + lineWidth, viewHeight,
                            (viewWidth / dl_rain.length * (i + 1)) - 2 * CircleR + lineWidth,
                            viewHeight - rectaaa[i][j], line4);

                }
            }
        }
        for (int i = 0; i < dh_rain.length; i++) {
            // 趋势图线条
            if (i == 0) {
            } else {
                canvas.drawLine((viewWidth / dh_rain.length * i) - 2 * CircleR, viewHeight - dh_rain[i - 1],
                        (viewWidth / dh_rain.length * (i + 1)) - 2 * CircleR, viewHeight - dh_rain[i], lineMax);
                canvas.drawLine((viewWidth / dl_rain.length * i) - 2 * CircleR, viewHeight - dl_rain[i - 1],
                        (viewWidth / dl_rain.length * (i + 1)) - 2 * CircleR, viewHeight - dl_rain[i], lineAvg);
            }
        }

        for (int i = 0; i < dh_rain.length; i++) {
            // 趋势图点
            canvas.drawCircle((viewWidth / dh_rain.length * (i + 1)) - 2 * CircleR, viewHeight - dh_rain[i], CircleR,
                    h_point);
            canvas.drawCircle((viewWidth / dl_rain.length * (i + 1)) - 2 * CircleR, viewHeight - dl_rain[i], CircleR,
                    l_point);
        }

        for (int i = 0; i < 6; i++) {
            // 横线
            if (i == 5) {
                canvas.drawText(h_val[i], Util.dip2px(getContext(), 10), (viewHeight / 6) * (i + 1), mTextPaint);
                canvas.drawLine((viewHeight / 23) + magrLeft * 2, (viewHeight / 6) * (i + 1) - 1,
                        viewWidth + magrLeft * 2, (viewHeight / 6) * (i + 1) - 1, lineCom);
            } else {
                canvas.drawText(h_val[i], Util.dip2px(getContext(), 10), (viewHeight / 6) * (i + 1), mTextPaint);
                canvas.drawLine((viewHeight / 23) + magrLeft * 2, (viewHeight / 6) * (i + 1), viewWidth + magrLeft * 2,
                        (viewHeight / 6) * (i + 1), lineCom);
            }
        }
        for (int i = 0; i <= 24; i++) {
            // 竖线
            canvas.drawLine((viewWidth / 23) * (i + 1) + magrLeft, viewHeight / 6,
                    (viewWidth / 23) * (i + 1) + magrLeft, viewHeight, lineCom);
        }
        canvas.drawText(unit, Util.dip2px(getContext(), 15), Util.dip2px(getContext(), 20), mTextPaintUnit);
    }


}
