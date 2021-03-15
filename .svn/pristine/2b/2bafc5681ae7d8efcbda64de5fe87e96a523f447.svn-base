package com.pcs.ztqtj.view.myview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

import com.pcs.ztqtj.control.tool.DrawViewTool;
import com.pcs.lib.lib_pcs_v3.control.tool.ScreenUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Z 首页温度趋势图
 */
public class TemperatureView extends View {
    /**
     * 趋势图的高度
     */
    private float viewHeight;
    /**
     * 趋势图的宽度
     */
    private float viewWidth;
    /**
     * 最低温度
     */
    private float minTemp;
    /**
     * 最高温度
     */
    private float maxTemp;
    /**
     * 每度的单位长度
     */
    private ArrayList<Float> mTopTemp;
    private ArrayList<Float> mLowTemp;
    float mSpace;
    private Paint mPointHight;
    private Paint mPointLow;
    private Paint mLineHight;
    private Paint mlineLow;
    private Paint mTextPaintH;
    private Paint mTextPaintL;
    private Paint pHight;
    private Paint pLow;
    private Context context;
    private float marginTop = 0;
    private float marginButton = 0;
    /**
     * 10
     */
    private float CircleR = 0;

    /**
     * 文字大小
     */
    private float textsize = 0;

    private int itemSize;

    public TemperatureView(Context context) {
        super(context);
        this.context = context;
    }

    public TemperatureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        mTopTemp = new ArrayList<>();
        mLowTemp = new ArrayList<>();
        init();
    }

    public void setTemperture(List<Float> hightlist, List<Float> lowtlist, int itemSize) {
        this.itemSize = itemSize;
        mTopTemp.clear();
        mLowTemp.clear();
        if (hightlist == null || lowtlist == null) {
            return;
        }
        mTopTemp.addAll(hightlist);
        mLowTemp.addAll(lowtlist);
        invalidate();
    }

    private void init() {
        float lineWidth = ScreenUtil.dip2px(context, DrawViewTool.lineWidth);
        textsize = ScreenUtil.dip2px(context, DrawViewTool.textSize);
        CircleR = ScreenUtil.dip2px(context, DrawViewTool.circleR);
        marginTop = ScreenUtil.dip2px(context, 25);
        marginButton = ScreenUtil.dip2px(context, 25);

        // 虚线
        pHight = DrawViewTool.getInstance().getDottedLine(Color.YELLOW, lineWidth);
        // 虚线
        pLow = DrawViewTool.getInstance().getDottedLine(Color.WHITE, lineWidth);

        // 高低温度点
        mPointHight = DrawViewTool.getInstance().getMyLine(Color.YELLOW);
        // 高温线
        mLineHight = DrawViewTool.getInstance().getMyLine(Color.YELLOW, lineWidth);
        mLineHight.setStyle(Paint.Style.STROKE);

        // 低温点
        mPointLow = DrawViewTool.getInstance().getMyLine(Color.WHITE);
        // 低温线
        mlineLow = DrawViewTool.getInstance().getMyLine(Color.WHITE, lineWidth);
        mlineLow.setStyle(Paint.Style.STROKE);

        // °文字
        mTextPaintH = DrawViewTool.getInstance().getTextPaintWhite(textsize);
        mTextPaintH.setColor(Color.YELLOW);


        mTextPaintL = DrawViewTool.getInstance().getTextPaintWhite(textsize);
        mTextPaintL.setColor(Color.WHITE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    private int measureHeight(int heightMeasureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(heightMeasureSpec);
        int specSize = MeasureSpec.getSize(heightMeasureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = getPaddingBottom() + getPaddingTop();
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    private int measureWidth(int widthMeasureSpec) {
        int result = 0;
        int size = MeasureSpec.getSize(widthMeasureSpec); // 每次调用此方法，测量用到的size会发生变化
        int mode = MeasureSpec.getMode(widthMeasureSpec); // 根据定义的Layout_width,Layout_height，会对此值产生影响
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else if (mode == MeasureSpec.UNSPECIFIED) {
            result = getPaddingLeft() + getPaddingRight();
        } else {
            result = Math.min(result, size);
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mLowTemp == null || mLowTemp.size() == 0)
            return;
        viewHeight = getHeight();
        viewWidth = getWidth();
        spaceHeightWidth();
        // 计算x轴的位置，分成12分，去单数，即中间
        // 获取温度个数
        int size = mTopTemp.size() >= mLowTemp.size() ? mTopTemp.size() : mLowTemp.size();
        float itemWidth = viewWidth/itemSize;
        List<Float> dx = new ArrayList<>();
        for(int i = 0; i < size; i++) {
            //dx.add(viewWidth * (i*2+1) / (size*2));
            dx.add(itemWidth*i + itemWidth/2.0f);
        }
        FontMetrics fontMetrics = mTextPaintH.getFontMetrics();
        // 计算文字高度
        float fontHeight = fontMetrics.bottom - fontMetrics.top;

        List<Point> mHightTempPoint = new ArrayList<>();

        for (int i = 0; i < mTopTemp.size(); i++) {
            float hTop = (maxTemp - mTopTemp.get(i)) * mSpace + marginTop;
//            if (i >= 1 && i < mTopTemp.size() - 1) {
////                canvas.drawLine(dx[i], hTop, dx[i + 1], (maxTemp - mTopTemp.get(i + 1)) * mSpace + marginTop, mLineHight);
//                drawScrollLine(canvas, mLineHight,dx[i], hTop, dx[i + 1], (maxTemp - mTopTemp.get(i + 1)) * mSpace + marginTop);
//            }
            Point pointHight = new Point();
            pointHight.x = dx.get(i).intValue();
            pointHight.y = (int) hTop;
            mHightTempPoint.add(pointHight);

            canvas.drawText(changeValue(mTopTemp.get(i)), dx.get(i), hTop - fontHeight / 3, mTextPaintH);
            canvas.drawCircle(dx.get(i), hTop, CircleR, mPointHight);
        }


        drawBezier(canvas, mHightTempPoint, mLineHight, mLineHight);

        List<Point> mLowTempPoint = new ArrayList<>();
        for (int i = 0; i < mLowTemp.size(); i++) {
            float hLow = (maxTemp - mLowTemp.get(i)) * mSpace + marginTop;
//            if (i > 1) {
////                canvas.drawLine(dx[i], hLow, dx[i - 1], (maxTemp - mLowTemp.get(i - 1)) * mSpace + marginTop, mlineLow);
//                drawScrollLine(canvas, mlineLow, dx[i], hLow, dx[i - 1], (maxTemp - mLowTemp.get(i - 1)) * mSpace + marginTop);
//            }
            Point pointHight = new Point();
            pointHight.x = dx.get(i).intValue();
            pointHight.y = (int) hLow;
            mLowTempPoint.add(pointHight);
            canvas.drawText(changeValue(mLowTemp.get(i)), dx.get(i), hLow + fontHeight, mTextPaintL);
            canvas.drawCircle(dx.get(i), hLow, CircleR, mPointLow);// 连线
        }
        drawBezier(canvas, mLowTempPoint, mlineLow, mlineLow);
//        if (mLowTemp.size() < 2) {
//        } else {
//            float hightXP1 = dx[0];
//            float hightYP1 = (maxTemp - mTopTemp.get(0)) * mSpace + marginTop;
//            float hightXP2 = dx[1];
//            float hightYP2 = (maxTemp - mTopTemp.get(1)) * mSpace + marginTop;
//
//            float lowXP1 = dx[0];
//            float lowYP1 = (maxTemp - mLowTemp.get(0)) * mSpace + marginTop;
//            float lowXP2 = dx[1];
//            float lowYP2 = (maxTemp - mLowTemp.get(1)) * mSpace + marginTop;

//            Path path = new Path();
//            Path path2 = new Path();
//            path.moveTo(dx[0], (maxTemp - mTopTemp.get(0)) * mSpace + marginTop);
//            path.lineTo(dx[1], (maxTemp - mTopTemp.get(1)) * mSpace + marginTop);
//            path2.moveTo(dx[0], (maxTemp - mLowTemp.get(0)) * mSpace + marginTop);
//            path2.lineTo(dx[1], (maxTemp - mLowTemp.get(1)) * mSpace + marginTop);
//            canvas.drawPath(path, pHight);
//            canvas.drawPath(path2, pLow);

//            drawScrollLine(canvas, pHight, hightXP1, hightYP1, hightXP2, hightYP2);
//            drawScrollLine(canvas, pLow, lowXP1, lowYP1, lowXP2, lowYP2);
//        }
    }


    private void drawScrollLine(Canvas canvas, Paint mPaint, float statrX, float statrY, float endX, float endY) {
        float centerX = (statrX + endX) / 2;
        Path path = new Path();
        path.moveTo(statrX, statrY);
        path.cubicTo(centerX, statrY, centerX, endY, endX, endY);
        canvas.drawPath(path, mPaint);
    }

    /**
     * 画贝塞尔曲线
     * mPoints传入所有的点位置
     */
    private void drawBezier(Canvas canvas, List<Point> points, Paint mPaint, Paint mPaint0) {
        if (points.size() < 2) {
            return;
        }
        /** 中点集合 */
        List<Point> mMidPoints = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) {
            Point midPoint = null;
            if (i == points.size() - 1) {
            } else {
                midPoint = new Point((points.get(i).x + points.get(i + 1).x) / 2, (points.get(i).y + points.get(i + 1).y) / 2);
                mMidPoints.add(midPoint);
            }
        }

        /** 中点的中点集合 */
        List<Point> mMidMidPoints = new ArrayList<>();
        for (int i = 0; i < mMidPoints.size(); i++) {
            Point midMidPoint = null;
            if (i == mMidPoints.size() - 1) {
            } else {
                midMidPoint = new Point((mMidPoints.get(i).x + mMidPoints.get(i + 1).x) / 2, (mMidPoints.get(i).y + mMidPoints.get(i + 1).y) / 2);
                mMidMidPoints.add(midMidPoint);
            }
        }
        /** 移动后的点集合(控制点) */
        List<Point> mControlPoints = new ArrayList<>();
//        计算控制点个数
        for (int i = 0; i < points.size(); i++) {
            if (i == 0 || i == points.size() - 1) {
            } else {
                Point before = new Point();
                Point after = new Point();
                before.x = points.get(i).x - mMidMidPoints.get(i - 1).x + mMidPoints.get(i - 1).x;
                before.y = points.get(i).y - mMidMidPoints.get(i - 1).y + mMidPoints.get(i - 1).y;
                after.x = points.get(i).x - mMidMidPoints.get(i - 1).x + mMidPoints.get(i).x;
                after.y = points.get(i).y - mMidMidPoints.get(i - 1).y + mMidPoints.get(i).y;
                mControlPoints.add(before);
                mControlPoints.add(after);
            }
        }

        if (mControlPoints.size() == 0) {
            return;
        }


        // 绘制路径
        Path mPath = new Path();
        for (int i = 0; i < points.size(); i++) {
            if (i == 0) {// 第一条为二阶贝塞尔
                Path mPath0 = new Path();
                mPath0.moveTo(points.get(i).x, points.get(i).y);// 起点
                mPath0.quadTo(mControlPoints.get(i).x, mControlPoints.get(i).y,// 控制点
                        points.get(i + 1).x, points.get(i + 1).y);

                canvas.drawPath(mPath0, mPaint0);
            } else if (i < points.size() - 2) {// 三阶贝塞尔
                if (i == 1) {
                    mPath.moveTo(points.get(i).x, points.get(i).y);// 起点
                }
                mPath.cubicTo(mControlPoints.get(2 * i - 1).x, mControlPoints.get(2 * i - 1).y,// 控制点
                        mControlPoints.get(2 * i).x, mControlPoints.get(2 * i).y,// 控制点
                        points.get(i + 1).x, points.get(i + 1).y);// 终点
            } else if (i == points.size() - 2) {// 最后一条为二阶贝塞尔
                mPath.moveTo(points.get(i).x, points.get(i).y);// 起点
                mPath.quadTo(mControlPoints.get(mControlPoints.size() - 1).x, mControlPoints.get(mControlPoints.size() - 1).y, points.get(i + 1).x, points.get(i + 1).y);// 终点
            }
        }
        canvas.drawPath(mPath, mPaint);
    }


    private String changeValue(float a) {
        String value = (int) a + "";
        return value + "℃";
    }

    /**
     * 计算区间高度,横轴位置，以及区间比例
     */
    private void spaceHeightWidth() {
        minTemp = Collections.min(mLowTemp);// 取低温的最小值
        maxTemp = Collections.max(mTopTemp);// 取高温的最大值
        float h = maxTemp - minTemp;
        float userHeight = viewHeight - marginTop - marginButton;
        mSpace = userHeight / h;
    }

}
