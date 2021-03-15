package com.pcs.ztqtj.view.myview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.PathEffect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import com.pcs.ztqtj.view.myview.observation.ObData;
import com.pcs.lib.lib_pcs_v3.control.tool.Util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * @author Z 整点实况
 */
public class ObservationView extends View {
    /**
     * 趋势图的高度
     */
    private float viewHeight;

    /**
     * 趋势图的宽度
     */
    private float viewWidth;

    /**
     * 画笔
     */
    private Paint paintGreenNew;
    private Paint paintBlueNew;
    private Paint paintOrangeNew;
    private Paint paintYellowNew;
    private Paint paintRedNew;
    private Paint paintVoiletNew;
    /**
     * 连线
     */
    private Paint lineGreenNew;
    private Paint lineVoiletNew;
    private Paint lineRedNew;
    private Paint lineYellowNew;
    private Paint lineOrangeNew;
    private Paint lineBlueNew;
    /**
     * 公共线
     */
    private Paint lineCom;
    /**
     * 公共线偏灰
     */
    private Paint lineCom2;
    /**
     * 文字
     */
    private Paint mTextPaint;
    private Paint mTextPaintGrary;
    /**
     * 柱形线
     */
    private Paint line1;
    private float hight_value;

    /**
     * 当前值
     */
    private List<ObData> dataList = new ArrayList<ObData>();


    /**
     * Y轴的值
     */
    private List<Float> yValue = new ArrayList<Float>();

    /**
     * 区间值
     */
    private float section = 5;
    /**
     * 圆点半径
     */
    private float CircleR = 3;
    /**
     * 顶部留高
     */
    private float margTopH = 40;
    /**
     * 顶部留高
     */
    private float margBottonH = 40;
    /**
     * 右边距离边
     */
    private float margRightW = 20;

    /**
     * 左边距离边：实时变化，在draw中改变值
     */
    private float margLeftValue = 10;

    /**
     * Y周区间个数,(修改此值必须同时修改h_val默认个数)
     */
    private int countSectionY = 5;
    /**
     * 横轴区间个数
     */
    private int countSecitonX = 24;

    /**
     * 文字大小
     */
    private float textSzie = 15;
    /**
     * 线宽度
     */
    private float lineWidth = 2;
    private float rectangleWidth = 10;

    public ObservationView(Context context) {
        super(context);

    }

    /**
     * 单位
     */
    public String company_v = "";
    private Context context;

    public ObservationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        minValue = 0f;
        maxValue = 10f;
    }

    private void chagnValue(Context context) {
        CircleR = Util.dip2px(context, 4);
        /** 顶部留高 */
        margTopH = Util.dip2px(context, 30);
        /** 顶部留高 */
        margBottonH = Util.dip2px(context, 40);
        /** 右边距离边 */
        margRightW = Util.dip2px(context, 5);
        /** 左边距离边：实时变化，在draw中改变值 */
        margLeftValue = Util.dip2px(context, 5);

        textSzie = Util.dip2px(context, 13);
        lineWidth = Util.dip2px(context, 2f);
        rectangleWidth = Util.dip2px(context, 10);
    }

    private void init(Context context) {
        chagnValue(context);

        paintGreenNew = new Paint();
        paintGreenNew.setAntiAlias(true);
        paintGreenNew.setColor(Color.argb(255, 106, 172, 53));

        paintOrangeNew = new Paint();
        paintOrangeNew.setAntiAlias(true);
        paintOrangeNew.setColor(Color.argb(255, 251, 153, 1));

        paintYellowNew = new Paint();
        paintYellowNew.setAntiAlias(true);
        paintYellowNew.setColor(Color.argb(255, 247, 204, 19));

        paintRedNew = new Paint();
        paintRedNew.setAntiAlias(true);
        paintRedNew.setColor(Color.argb(255, 254, 39, 18));

        paintVoiletNew = new Paint();
        paintVoiletNew.setAntiAlias(true);
        paintVoiletNew.setColor(Color.argb(255, 134, 1, 176));

        paintBlueNew = new Paint();
        paintBlueNew.setAntiAlias(true);
        paintBlueNew.setColor(Color.argb(255, 54, 108, 255));


        // 最高点线
        lineGreenNew = new Paint();
        lineGreenNew.setAntiAlias(true);
        lineGreenNew.setStrokeWidth(lineWidth);
        lineGreenNew.setColor(Color.argb(255, 106, 172, 53));

        lineBlueNew = new Paint();
        lineBlueNew.setAntiAlias(true);
        lineBlueNew.setStrokeWidth(lineWidth);
        lineBlueNew.setColor(Color.argb(255, 54, 108, 255));

        lineOrangeNew = new Paint();
        lineOrangeNew.setAntiAlias(true);
        lineOrangeNew.setStrokeWidth(lineWidth);
        lineOrangeNew.setColor(Color.argb(255, 251, 153, 1));

        lineYellowNew = new Paint();
        lineYellowNew.setAntiAlias(true);
        lineYellowNew.setStrokeWidth(lineWidth);
        lineYellowNew.setColor(Color.argb(255, 247, 204, 19));

        lineRedNew = new Paint();
        lineRedNew.setAntiAlias(true);
        lineRedNew.setStrokeWidth(lineWidth);
        lineRedNew.setColor(Color.argb(255, 254, 39, 18));

        lineVoiletNew = new Paint();
        lineVoiletNew.setAntiAlias(true);
        lineVoiletNew.setStrokeWidth(lineWidth);
        lineVoiletNew.setColor(Color.argb(255, 134, 1, 176));


        // 公共线
        lineCom = new Paint();
        lineCom.setAntiAlias(true);
        lineCom.setStrokeWidth(1);
        lineCom.setColor(Color.argb(255, 170, 170, 170));
        // 公共线
        lineCom2 = new Paint();
        lineCom2.setAntiAlias(true);
        lineCom2.setStrokeWidth(1);
        lineCom2.setStyle(Paint.Style.STROKE);
        lineCom2.setColor(Color.argb(255, 95, 95, 95));
        PathEffect effects = new DashPathEffect(new float[]{1, 2, 4, 8}, 1);
        lineCom2.setPathEffect(effects);


        // 文字颜色
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(Color.argb(255, 255, 255, 255));
        // mTextPaint.setTypeface(Typeface.create("宋体", Typeface.BOLD));
        mTextPaint.setTypeface(Typeface.create("宋体", Typeface.NORMAL));
        mTextPaint.setTextAlign(Align.CENTER);
        mTextPaint.setTextSize(textSzie);
        // 文字颜色
        mTextPaintGrary = new Paint();
        mTextPaintGrary.setAntiAlias(true);
        mTextPaintGrary.setColor(Color.argb(255, 157, 157, 157));
        // mTextPaint.setTypeface(Typeface.create("宋体", Typeface.BOLD));
        mTextPaintGrary.setTypeface(Typeface.create("宋体", Typeface.NORMAL));
        mTextPaintGrary.setTextAlign(Align.CENTER);
        mTextPaintGrary.setTextSize(textSzie);

        line1 = new Paint();
        line1.setAntiAlias(true);
        line1.setStrokeWidth(rectangleWidth);
        line1.setColor(Color.argb(255, 72, 117, 165));

    }

    private float maxValue, minValue;


    /**
     * 值
     *
     * @param timeValue 横轴文字
     * @param company_v 竖轴单位
     */
    public void setValue(List<ObData> timeValue, String company_v, float maxValue, float minValue) {
        this.dataList.clear();
        this.maxValue = maxValue;
        this.minValue = minValue;
        dataList.addAll(timeValue);
        this.company_v = company_v;
        this.yValue.clear();
        getSectionValue();
        this.invalidate();
    }

    /**
     * 换算高度值
     *
     * @param value 总的高度值
     */
    private void getHight(float value, float xSection) {

        float valueCount = maxValue - minValue;
        if (Float.compare(valueCount, 0) == 0) {
            for (int i = 0; i < dataList.size(); i++) {
                dataList.get(i).pGreenNew.x = xSection * i;
                dataList.get(i).pGreenNew.y = value;

                dataList.get(i).pOrangeNew.x = xSection * i;
                dataList.get(i).pOrangeNew.y = value;

                dataList.get(i).pBlueNew.x = xSection * i;
                dataList.get(i).pBlueNew.y = value;

                dataList.get(i).pVoiletNew.x = xSection * i;
                dataList.get(i).pVoiletNew.y = value;

                dataList.get(i).pYellowNew.x = xSection * i;
                dataList.get(i).pYellowNew.y = value;

                dataList.get(i).pGreenNew.x = xSection * i;
                dataList.get(i).pGreenNew.y = value;

                dataList.get(i).pXValue.x = xSection * i;
                dataList.get(i).pXValue.y = value;

//				// 当前值减少最低值
//				viewTempValueHight.add(0f);
            }
        } else {
            for (int i = 0; i < dataList.size(); i++) {
                ObData bean = dataList.get(i);

                float greenNew = bean.greenNew - minValue;
                dataList.get(i).pGreenNew.x = xSection * i;
                dataList.get(i).pGreenNew.y = value * (greenNew / valueCount);


                float orangeNew = bean.orangeNew - minValue;
                dataList.get(i).pOrangeNew.x = xSection * i;
                dataList.get(i).pOrangeNew.y = value * (orangeNew / valueCount);
                ;

                float blue = bean.blueNew - minValue;
                dataList.get(i).pBlueNew.x = xSection * i;
                dataList.get(i).pBlueNew.y = value * (blue / valueCount);
                ;


                float voilet = bean.voiletNew - minValue;
                dataList.get(i).pVoiletNew.x = xSection * i;
                dataList.get(i).pVoiletNew.y = value * (voilet / valueCount);
                ;

                float yellow = bean.yellowNew - minValue;
                dataList.get(i).pYellowNew.x = xSection * i;
                dataList.get(i).pYellowNew.y = value * (yellow / valueCount);
                ;

                float red = bean.redNew - minValue;
                dataList.get(i).pRedNew.x = xSection * i;
                dataList.get(i).pRedNew.y = value * (red / valueCount);
                ;
                dataList.get(i).pXValue.x = xSection * i;
                dataList.get(i).pXValue.y = value;
                ;

            }
        }
    }


    /**
     * 计算区间值
     *
     * @return
     */
    private void getSectionValue() {
        if (this.dataList.size() != 0) {
            float value = maxValue - minValue;
            if (Float.compare(maxValue, minValue) == 0) {
                section = 1;
            } else {
                section = value / countSectionY;
            }
        } else {
            section = 1;
        }

        for (int i = 0; i < countSectionY + 1; i++) {
            yValue.add(minValue + section * i);
        }

    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        // 控件高度-距头，和距尾的值即为实际使用高度
        viewHeight = getHeight() - margTopH - margBottonH;
        viewWidth = getWidth();
        // 计算文字高度
        FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        float fontHeight = fontMetrics.bottom - fontMetrics.top;
        canvas.drawText(company_v, margLeftValue / 2, fontHeight, mTextPaint);
        canvas.drawText("时", viewWidth - fontHeight / 3, getHeight() - margBottonH + fontHeight, mTextPaint);
        canvas.drawText("日", viewWidth - fontHeight / 3, getHeight() - margBottonH + 2 * fontHeight, mTextPaint);
        // 区间高度
        float sectionHight = viewHeight / countSectionY;
        // 根据Y轴值的长度缩进值
        margLeftValue = fontHeight * 3;
        viewWidth = viewWidth - margLeftValue - margRightW;

        // 宽度区间
        float sectionWidth = viewWidth / countSecitonX;
        for (int i = 0; i < countSectionY + 1; i++) {
            // 横线，
            canvas.drawLine(margLeftValue, margTopH + sectionHight * i, viewWidth + margLeftValue - sectionWidth, margTopH + sectionHight * i, lineCom);
        }

        // 画y轴坐标值
        for (int i = 0; i < yValue.size(); i++) {
            DecimalFormat decimalFormat = new DecimalFormat(".0");//构造方法的字符格式这里如果小数不足2位,会以0补足.
            String p = decimalFormat.format(yValue.get(yValue.size() - i - 1));//format 返
            canvas.drawText(p, margLeftValue / 2, margBottonH + sectionHight * i + fontHeight / 3, mTextPaint);
        }

        for (int i = 0; i < countSecitonX; i++) {
            // 竖线
            if (i % 2 == 0) {
                canvas.drawLine(margLeftValue + sectionWidth * i, margTopH, margLeftValue + sectionWidth * i, margTopH + viewHeight, lineCom);
            } else {
                canvas.drawLine(margLeftValue + sectionWidth * i, margTopH, margLeftValue + sectionWidth * i, margTopH + viewHeight, lineCom2);
            }
        }
        if (dataList.size() == 0) {
            return;
        }
        getHight(viewHeight, sectionWidth);
        float startH = getHeight() - margBottonH;
        for (int i = 0; i < dataList.size(); i++) {
            ObData bean = dataList.get(i);
            if (i % 2 == 0) {
                // 底部文字
                canvas.drawText(bean.xValue, margLeftValue + bean.pXValue.x, margTopH + viewHeight + fontHeight, mTextPaint);
                canvas.drawText(bean.xDay, margLeftValue + bean.pXValue.x, margTopH + viewHeight + 2 * fontHeight, mTextPaint);
            } else {
                // 底部文字
//                canvas.drawText(bean.xValue, margLeftValue + bean.pXValue.x, margTopH + viewHeight + fontHeight, mTextPaintGrary);
//                canvas.drawText(bean.xDay, margLeftValue + bean.pXValue.x, margTopH + viewHeight + 2 * fontHeight, mTextPaintGrary);
            }
            if (i == 0) {

            } else {
                ObData beanPro = dataList.get(i - 1);
                canvas.drawLine(margLeftValue + bean.pRedNew.x, startH - bean.pRedNew.y, margLeftValue + beanPro.pRedNew.x, startH - beanPro.pRedNew.y, lineRedNew);
                canvas.drawLine(margLeftValue + bean.pOrangeNew.x, startH - bean.pOrangeNew.y, margLeftValue + beanPro.pOrangeNew.x, startH - beanPro.pOrangeNew.y, lineOrangeNew);
                canvas.drawLine(margLeftValue + bean.pGreenNew.x, startH - bean.pGreenNew.y, margLeftValue + beanPro.pGreenNew.x, startH - beanPro.pGreenNew.y, lineGreenNew);
                canvas.drawLine(margLeftValue + bean.pVoiletNew.x, startH - bean.pVoiletNew.y, margLeftValue + beanPro.pVoiletNew.x, startH - beanPro.pVoiletNew.y, lineVoiletNew);
                canvas.drawLine(margLeftValue + bean.pYellowNew.x, startH - bean.pYellowNew.y, margLeftValue + beanPro.pYellowNew.x, startH - beanPro.pYellowNew.y, lineYellowNew);
                canvas.drawLine(margLeftValue + bean.pBlueNew.x, startH - bean.pBlueNew.y, margLeftValue + beanPro.pBlueNew.x, startH - beanPro.pBlueNew.y, lineBlueNew);
            }
//            ，为了美观(为了看起来点不会被线压住)
//            canvas.drawCircle(margLeftValue + bean.pGreenNew.x, startH - bean.pGreenNew.y, CircleR, paintGreenNew);
//            canvas.drawCircle(margLeftValue + bean.pRedNew.x, startH - bean.pRedNew.y, CircleR, paintRedNew);
//            canvas.drawCircle(margLeftValue + bean.pYellowNew.x, startH - bean.pYellowNew.y, CircleR, paintYellowNew);
//            canvas.drawCircle(margLeftValue + bean.pOrangeNew.x, startH - bean.pOrangeNew.y, CircleR, paintOrangeNew);
//            canvas.drawCircle(margLeftValue + bean.pBlueNew.x, startH - bean.pBlueNew.y, CircleR, paintBlueNew);
//            canvas.drawCircle(margLeftValue + bean.pVoiletNew.x, startH - bean.pVoiletNew.y, CircleR, paintVoiletNew);
        }

        for (int i = 0; i < dataList.size(); i++) {
            ObData bean = dataList.get(i);
            canvas.drawCircle(margLeftValue + bean.pRedNew.x, startH - bean.pRedNew.y, CircleR, paintRedNew);
            canvas.drawCircle(margLeftValue + bean.pOrangeNew.x, startH - bean.pOrangeNew.y, CircleR, paintOrangeNew);
            canvas.drawCircle(margLeftValue + bean.pGreenNew.x, startH - bean.pGreenNew.y, CircleR, paintGreenNew);
            canvas.drawCircle(margLeftValue + bean.pVoiletNew.x, startH - bean.pVoiletNew.y, CircleR, paintVoiletNew);
            canvas.drawCircle(margLeftValue + bean.pYellowNew.x, startH - bean.pYellowNew.y, CircleR, paintYellowNew);
            canvas.drawCircle(margLeftValue + bean.pBlueNew.x, startH - bean.pBlueNew.y, CircleR, paintBlueNew);
        }

    }


}