package com.pcs.ztqtj.view.myview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Z on 2016/8/4.
 */
public class CompleView extends View {

    public static final int DATANull = -100000;
    private int widthSeaction = 24;//宽度区间个数；
    private int hightSeaction = 6;//高度区间个数
    private float pingLeft = 0, pindRight = 0, pindTop = 0, pindButton = 0;
    /*矩形背景图的宽度*/

    private Paint lineCom;
    private Paint mTextYBrush, mTextXBrush;

    //    绘制圆点
    private Paint gardenRed, gardenOrange;
    private float radius = 0;//圆半径
    private float lineRedOrangeWidth = 0;//连线的宽度
    //圆点连接线
    private Paint lineRed, lineOrange;

    //柱状图
    private Paint deepBlue, blue, green;
    private float columnWidth = 0;// 柱状图宽


    public CompleView(Context context) {
        super(context);
        initPointValue();
    }

    public CompleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPointValue();
        initTextValue();
    }


    public void setUnit(String unit) {

    }


    private boolean isStartZero = false;

    public void isStartZero(boolean isTrue) {
        this.isStartZero = isTrue;
    }

    private void initTextValue() {
//        h_rain = new float[]{0, 0, 0, 0, 0, 0};
//        l_rain = new float[]{0, 0, 0, 0, 0, 0};
//        rect = new float[][]{{0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}};
//        h_rain = new float[]{5, 7, 12, 11, 15, 19};
//        l_rain = new float[]{3, 4, 6, 5, 7, 9};
//        rect = new float[][]{{2, 3, 4}, {4, 3, 8}, {2, 5, 4}, {2, 7, 4}, {6, 3, 4}, {2, 3, 9}};
//        xVlue = new String[]{"1月", "2月", "3月", "4月", "5月", "6月"};
    }


    public float dip2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (dpValue * scale + 0.5f);
    }

    private void initPointValue() {

        pingLeft = dip2px(45);
        pindRight = dip2px(15);
        pindTop = dip2px(30);
        pindButton = dip2px(30);
        radius = dip2px(3);
        columnWidth = dip2px(6);
        lineRedOrangeWidth = dip2px(1);


        // 公共线
        lineCom = new Paint();
        lineCom.setAntiAlias(true);
        lineCom.setStrokeWidth(1);
        lineCom.setColor(Color.argb(255, 170, 170, 170));


        // 文字颜色
        mTextYBrush = new Paint();
        mTextYBrush.setAntiAlias(true);
        mTextYBrush.setColor(Color.argb(255, 170, 170, 170));
        mTextYBrush.setTextSize(dip2px(12));
        mTextYBrush.setTypeface(Typeface.create("宋体", Typeface.BOLD));
        mTextYBrush.setTextAlign(Paint.Align.CENTER);

        mTextXBrush = new Paint();
        mTextXBrush.setAntiAlias(true);
        mTextXBrush.setColor(Color.argb(255, 170, 170, 170));
        mTextXBrush.setTypeface(Typeface.create("宋体", Typeface.BOLD));
        mTextXBrush.setTextAlign(Paint.Align.CENTER);
        mTextXBrush.setTextSize(dip2px(14));


        gardenRed = new Paint();
        gardenRed.setAntiAlias(true);
        gardenRed.setColor(Color.argb(255, 255, 0, 0));
        lineRed = gardenRed;
        lineRed.setStrokeWidth(lineRedOrangeWidth);
        gardenOrange = new Paint();
        gardenRed.setAntiAlias(true);
        gardenOrange.setColor(Color.argb(255, 237, 136, 16));
        lineOrange = gardenOrange;
        lineOrange.setStrokeWidth(lineRedOrangeWidth);


        deepBlue = new Paint();
        deepBlue.setAntiAlias(true);
        deepBlue.setStrokeWidth(columnWidth);
        deepBlue.setColor(Color.argb(255, 7, 73, 143));
        blue = new Paint();
        blue.setAntiAlias(true);
        blue.setStrokeWidth(columnWidth);
        blue.setColor(Color.argb(255, 24, 202, 214));
        green = new Paint();
        green.setAntiAlias(true);
        green.setStrokeWidth(columnWidth);
        green.setColor(Color.argb(255, 54, 234, 143));
    }


    private float[] h_rain;
    private float[] l_rain;
    private float[][] rect;
    private String[] xVlue;

    /**
     * 重设值
     */
    public void setViewData(float[] h_rain, float[] l_rain, float[][] rect, String[] xVlue) {
        this.h_rain = h_rain;
        this.l_rain = l_rain;
        this.rect = rect;
        this.xVlue = xVlue;
        this.invalidate();
    }

    /**
     * 计算所有点的位置值
     * drawViewHight 图像绘制的区域高度
     * xSection x轴一个区间的宽度
     * ySection y轴一个区间的高度
     */
    private void countPoint(float drawViewHight, float xSection, float ySection) {
        if (xVlue == null) {
            return;
        }
        float max_value = getMax();//最大值
        float min_value = getMin();//最小值

        valueList.clear();
        if(DATANull==max_value){
            return;
        }
        if (Float.compare(min_value, max_value) == 0) {
//            所有的值都是同一个时
            if (isStartZero && Float.compare(min_value, 0) == 0) {
                //从0值开始又全部都是0则不用绘制图了
                return;
            }
            yValue.clear();
            yValue.add("");
            DecimalFormat fnum = new DecimalFormat("##0.0");
            String yvalue = fnum.format(min_value);
            yValue.add(yvalue);
            for (int i = 0; i < h_rain.length; i++) {
                Paint.FontMetrics fontMetrics = mTextXBrush.getFontMetrics();
                // 计算文字高度
                float fontHeight = fontMetrics.bottom - fontMetrics.top;
                float yPointStart = drawViewHight + pindTop;
                ItemCompleView item = new ItemCompleView();
                float xValue = xSection * (i * 4) + xSection * 2 + pingLeft;
                item.pointHight.set(xValue, yPointStart - ySection);
                item.pointLow.set(xValue, yPointStart - ySection);

                item.rectangleLeft.set(xValue - columnWidth, yPointStart - ySection);
                item.rectangleCenter.set(xValue, yPointStart - ySection);
                item.rectangleRight.set(xValue + columnWidth, yPointStart - ySection);

                item.pointXValue.set(xValue - fontHeight / 4, getHeight() - pindButton + fontHeight);
                item.xValue = xVlue[i];
                valueList.add(item);
            }
        } else {
            if (isStartZero) {
//                从0开始绘制
                isZeroDraw(drawViewHight, xSection, ySection, max_value, 0);
//                notZeroDraw(drawViewHight, xSection, ySection, max_value, 0);
            } else {
//                不确定0开始绘制
                notZeroDraw(drawViewHight, xSection, ySection, max_value, min_value);
            }
        }
    }


    private void compValue(float drawViewHight, float xSection, float ySection, float max_value, float min_value, float yValueSeaction, float ySeactionHight) {
        yValue.clear();
        for (int i = 0; i < hightSeaction + 1; i++) {
            DecimalFormat fnum = new DecimalFormat("##0.0");
            String yvalue = fnum.format(min_value + yValueSeaction * (i - 1));
            yValue.add(yvalue);
        }
        for (int i = 0; i < h_rain.length; i++) {
            Paint.FontMetrics fontMetrics = mTextXBrush.getFontMetrics();
            // 计算文字高度
            float fontHeight = fontMetrics.bottom - fontMetrics.top;
            float yPointStart = drawViewHight + pindTop;
            ItemCompleView item = new ItemCompleView();
            float xValue = xSection * (i * 4) + xSection * 2 + pingLeft;
            float allValue = max_value - min_value;//使用的总体高度对应的值
            float scaleHight = (ySeactionHight / allValue);

            float cuttHightValue = h_rain[i] - min_value;//当前值
            float cuttHight = cuttHightValue * scaleHight + ySection;//当前值对应的高度值
            item.pointHight.set(xValue, yPointStart - cuttHight);

            float cuttLowValue = l_rain[i] - min_value;//当前值
            float cuttLow = cuttLowValue * scaleHight + ySection;//当前值对应的高度值
            item.pointLow.set(xValue, yPointStart - cuttLow);

            float cuttLeftValue = rect[i][0] - min_value;//当前值
            if (rect[i][0] != DATANull) {
                float cuttLeft = cuttLeftValue * scaleHight + ySection;//当前值对应的高度值
                item.rectangleLeft.set(xValue - columnWidth, yPointStart - cuttLeft);
            } else {
                item.rectangleLeft.set(xValue - columnWidth, yPointStart);
            }
            float cuttCenterValue = rect[i][1] - min_value;//当前值
            if (rect[i][1] != DATANull) {
                float cuttCenter = cuttCenterValue * scaleHight + ySection;//当前值对应的高度值
                item.rectangleCenter.set(xValue, yPointStart - cuttCenter);
            } else {
                item.rectangleCenter.set(xValue, yPointStart);
            }

            float cuttRightValue = rect[i][2] - min_value;//当前值
            if (rect[i][2] != DATANull) {
                float cuttRight = cuttRightValue * scaleHight + ySection;//当前值对应的高度值
                item.rectangleRight.set(xValue + columnWidth, yPointStart - cuttRight);
            } else {
                item.rectangleRight.set(xValue, yPointStart);
            }
            item.pointXValue.set(xValue - fontHeight / 4, getHeight() - pindButton + fontHeight);
            item.xValue = xVlue[i];
            valueList.add(item);
        }
    }

    /**
     * 非从0值绘制
     */
    private void notZeroDraw(float drawViewHight, float xSection, float ySection, float max_value, float min_value) {
        // 留下两个区间，最顶和最底下两个空白区域
        float yValueSeaction = (float) ((max_value - min_value) / (hightSeaction - 1));
        float ySeactionHight = ySection * (hightSeaction - 1);//Y轴使用的总高度
        compValue(drawViewHight, xSection, ySection, max_value, min_value, yValueSeaction, ySeactionHight);
    }

    /**
     * 从0值绘制
     */
    private void isZeroDraw(float drawViewHight, float xSection, float ySection, float max_value, float min_value) {
        // 留下两个区间，最顶和最底下两个空白区域
        float yValueSeaction = (float) (max_value / hightSeaction);
        float ySeactionHight = ySection * (hightSeaction - 1);//Y轴使用的总高度

        yValue.clear();
        for (int i = 0; i < hightSeaction + 1; i++) {
            DecimalFormat fnum = new DecimalFormat("##0.0");
            String yvalue = fnum.format(yValueSeaction * i);
            yValue.add(yvalue);
        }

        for (int i = 0; i < h_rain.length; i++) {
            Paint.FontMetrics fontMetrics = mTextXBrush.getFontMetrics();
            // 计算文字高度
            float fontHeight = fontMetrics.bottom - fontMetrics.top;
            float yPointStart = drawViewHight + pindTop;
            ItemCompleView item = new ItemCompleView();
            float xValue = xSection * (i * 4) + xSection * 2 + pingLeft;
            float allValue = max_value;//使用的总体高度对应的值
            float scaleHight = (drawViewHight / allValue);

            float cuttHightValue = h_rain[i];//当前值
            float cuttHight = cuttHightValue * scaleHight;//当前值对应的高度值
            item.pointHight.set(xValue, yPointStart - cuttHight);

            float cuttLowValue = l_rain[i];//当前值
            float cuttLow = cuttLowValue * scaleHight;//当前值对应的高度值
            item.pointLow.set(xValue, yPointStart - cuttLow);

            float cuttLeftValue = rect[i][0];//当前值
            if (rect[i][0] != DATANull) {
                float cuttLeft = cuttLeftValue * scaleHight ;//当前值对应的高度值
                item.rectangleLeft.set(xValue - columnWidth, yPointStart - cuttLeft);
            } else {
                item.rectangleLeft.set(xValue - columnWidth, yPointStart);
            }
            float cuttCenterValue = rect[i][1];//当前值
            if (rect[i][1] != DATANull) {
                float cuttCenter = cuttCenterValue * scaleHight ;//当前值对应的高度值
                item.rectangleCenter.set(xValue, yPointStart - cuttCenter);
            } else {
                item.rectangleCenter.set(xValue, yPointStart);
            }

            float cuttRightValue = rect[i][2];//当前值
            if (rect[i][2] != DATANull) {
                float cuttRight = cuttRightValue * scaleHight ;//当前值对应的高度值
                item.rectangleRight.set(xValue + columnWidth, yPointStart - cuttRight);
            } else {
                item.rectangleRight.set(xValue, yPointStart);
            }
            item.pointXValue.set(xValue - fontHeight / 4, getHeight() - pindButton + fontHeight);
            item.xValue = xVlue[i];
            valueList.add(item);
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float drawViewWidth = getWidth() - pingLeft - pindRight;
        float drawViewHight = getHeight() - pindButton - pindTop;
        float xSection = (drawViewWidth / widthSeaction);
        float ySection = (drawViewHight / hightSeaction);
        //绘制网格
        for (int i = 0; i < widthSeaction + 1; i++) {
            float xWidth = xSection * i + pingLeft;
            canvas.drawLine(xWidth, pindTop, xWidth, getHeight() - pindButton, lineCom);
        }
        //绘制网格
        for (int i = 0; i < hightSeaction + 1; i++) {
            float xHight = ySection * i + pindButton;
            canvas.drawLine(pingLeft, xHight, getWidth() - pindRight, xHight, lineCom);
        }


        countPoint(drawViewHight, xSection, ySection);
        //y轴坐标值
        for (int i = 0; i < yValue.size(); i++) {
            float y = drawViewHight + pindTop - ySection * i;
            Paint.FontMetrics yFontMetrics = mTextYBrush.getFontMetrics();
            // 计算文字高度
            float fontHeight = yFontMetrics.bottom - yFontMetrics.top;
            canvas.drawText(yValue.get(i), pingLeft - (fontHeight * yValue.get(i).length()) / 4, y, mTextYBrush);
        }

        for (int i = 0; i < valueList.size(); i++) {
            ItemCompleView item = valueList.get(i);
            canvas.drawText(item.xValue, item.pointXValue.x, item.pointXValue.y, mTextXBrush);
//            柱状图
            canvas.drawLine(item.rectangleLeft.x, item.rectangleLeft.y, item.rectangleLeft.x, drawViewHight + pindTop, deepBlue);
            canvas.drawLine(item.rectangleCenter.x, item.rectangleCenter.y, item.rectangleCenter.x, drawViewHight + pindTop, blue);
            canvas.drawLine(item.rectangleRight.x, item.rectangleRight.y, item.rectangleRight.x, drawViewHight + pindTop, green);
            if (i == 0) {
            } else {
                ItemCompleView proItem = valueList.get(i - 1);
                canvas.drawLine(proItem.pointHight.x, proItem.pointHight.y, item.pointHight.x, item.pointHight.y, lineRed);
                canvas.drawLine(proItem.pointLow.x, proItem.pointLow.y, item.pointLow.x, item.pointLow.y, lineOrange);
            }
            canvas.drawCircle(item.pointHight.x, item.pointHight.y, radius, gardenRed);
            canvas.drawCircle(item.pointLow.x, item.pointLow.y, radius, gardenOrange);
        }
    }


    /**
     * 获取最大值
     *
     * @return
     */
    private float getMax() {
        List<Float> compList = new ArrayList<>();
        for (int i = 0; i < h_rain.length; i++) {
            if (h_rain[i] != DATANull) {
                compList.add(h_rain[i]);
            }
        }
        for (int i = 0; i < l_rain.length; i++) {
            if (l_rain[i] != DATANull) {
                compList.add(l_rain[i]);
            }
        }
        for (int i = 0; i < rect.length; i++) {
            for (int j = 0; j < rect[i].length; j++) {
                if (rect[i][j] != DATANull) {
                    compList.add(rect[i][j]);
                }
            }
        }
        if(compList.size()==0){
            return DATANull;
        }

        float MaxR = Collections.max(compList);
        return MaxR;
    }


    /**
     * 获取最大值
     *
     * @return
     */
    private float getMin() {
        List<Float> compList = new ArrayList<>();
        for (int i = 0; i < h_rain.length; i++) {
            if (h_rain[i] != DATANull) {
                compList.add(h_rain[i]);
            }
        }
        for (int i = 0; i < l_rain.length; i++) {
            if (l_rain[i] != DATANull) {
                compList.add(l_rain[i]);
            }
        }
        for (int i = 0; i < rect.length; i++) {
            for (int j = 0; j < rect[i].length; j++) {
                if (rect[i][j] != DATANull) {
                    compList.add(rect[i][j]);
                }
            }
        }
        if(compList.size()==0){
            return DATANull;
        }
        float MaxR = Collections.min(compList);
        return MaxR;
    }


    /**
     * y轴值
     */
    private List<String> yValue = new ArrayList<>();
    /**
     * 所有值列表
     */
    private List<ItemCompleView> valueList = new ArrayList<ItemCompleView>();

    public class ItemCompleView {
        /**
         * 矩形图，左边
         */
        public MyPoint rectangleLeft = new MyPoint();
        /**
         * 矩形图中间
         */
        public MyPoint rectangleCenter = new MyPoint();
        /**
         * 矩形图右边
         */
        public MyPoint rectangleRight = new MyPoint();

        /**
         * 高圆点点值
         */
        public MyPoint pointHight = new MyPoint();

        /**
         * 低圆点点值
         */
        public MyPoint pointLow = new MyPoint();
        public MyPoint pointXValue = new MyPoint();
        public String xValue = "";
    }

    public class MyPoint {
        public float x;
        public float y;

        public void set(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }
}
