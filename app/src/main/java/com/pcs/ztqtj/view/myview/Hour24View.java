package com.pcs.ztqtj.view.myview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Point;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import com.pcs.ztqtj.control.tool.DrawViewTool;
import com.pcs.lib.lib_pcs_v3.control.tool.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Z 首页24小时逐时预报
 */
public class Hour24View extends View {
    /**
     * 趋势图的高度
     */
    private int viewHeight;
    /**
     * 趋势图的宽度
     */
//    private int viewWidth;

    private List<Float> mTopTemp;
    private List<Float> mLowRain;
    private Paint mPointPaint;
    private Paint mPoint2Paint;
    private Paint mPoint3Paint;
    private Paint mLine1Paint;
    private Paint mline2Paint;
    private Paint mTextPaint;
    private Paint mTextPaintS;
    private Paint p;
    private Paint mline3Paint;
    private Context context;
    private int marginTop = 20;
    private int marginButton = 20;
    private Paint paintRain;
    /**
     * 10
     */
    private float CircleR = 10f;
    /**
     * 15
     */
//    private float CircleR2 = 15f;
    private float widtha = 0;
//    private float widthc = 0;
    /**
     * 1
     */

    /***/
    private float textsize = 0;
    private float textsizeS = 0;

    private int countWidth = 23;

    public Hour24View(Context context) {
        super(context);
        this.context = context;
    }

    public Hour24View(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        mTopTemp = new ArrayList<Float>();
        mLowRain = new ArrayList<Float>();
//        for (int i = 0; i < 24; i++) {
//            mTopTemp.add(10f + i);
//            mLowRain.add(10f);
//        }
        init();
    }

    public void setTemperture(List<Float> temper, List<Float> rain) {
        mTopTemp.clear();
        mLowRain.clear();
        if (temper == null || rain == null) {
            return;
        }
        if (temper.size() == 0) {
            return;
        }
        mTopTemp.addAll(temper);
        mLowRain.addAll(rain);
        invalidate();
    }

    private void init() {
        widtha = Util.dip2px(context, 1);
//        widthc = Util.dip2px(context, 1);
        textsize = Util.dip2px(context, 15);
        textsizeS = Util.dip2px(context, 11);
        CircleR = Util.dip2px(context, 4);
//        CircleR2 = Util.dip2px(context, 6);
        marginTop = Util.dip2px(context, 25);
        marginButton = Util.dip2px(context, 5);

        // 虚线
        p = new Paint();
        p.setStyle(Style.STROKE);
        p.setColor(Color.WHITE);
        p.setStrokeWidth(widtha);
        PathEffect effects = new DashPathEffect(new float[]{8, 8, 8, 8}, 1);
        p.setPathEffect(effects);
        p.setAntiAlias(true);

        // 边线
        mline3Paint = new Paint();
        mline3Paint.setColor(0x19FFFFFF);
        mline3Paint.setAntiAlias(true);
        mline3Paint.setStrokeWidth(2);

        // 高温度点
        mPointPaint = new Paint();
        mPointPaint.setAntiAlias(true);
        mPointPaint.setColor(Color.argb(255, 255, 254, 3));

        mPoint3Paint = new Paint();
        mPoint3Paint.setAntiAlias(true);
        mPoint3Paint.setColor(Color.WHITE);
        // 高低温度点
        mPoint2Paint = new Paint();
        mPoint2Paint.setAntiAlias(true);
        mPoint2Paint.setColor(Color.argb(255, 255, 255, 255));
        // 高温线
//        mLine1Paint = new Paint();
//        mLine1Paint.setAntiAlias(true);
//        mLine1Paint.setStrokeWidth(widtha);
//        mLine1Paint.setStyle(Paint.Style.STROKE);
//        mLine1Paint.setColor(Color.argb(200, 255, 255, 255));

        mLine1Paint = DrawViewTool.getInstance().getMyLine(Color.WHITE, widtha);
        mLine1Paint.setStyle(Style.STROKE);


        // 低温线
        mline2Paint = new Paint();
        mline2Paint.setColor(Color.argb(200, 255, 255, 255));
        mline2Paint.setAntiAlias(true);
        mline2Paint.setStrokeWidth(widtha);

        // °文字
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextSize(textsize);
        mTextPaint.setTypeface(Typeface.create("宋体", Typeface.NORMAL));
        mTextPaint.setTextAlign(Align.CENTER);
        // °文字小
        mTextPaintS = new Paint();
        mTextPaintS.setAntiAlias(true);
        mTextPaintS.setColor(Color.argb(255, 255, 255, 255));
        mTextPaintS.setTextSize(textsizeS);
        mTextPaintS.setTypeface(Typeface.create("宋体", Typeface.NORMAL));
        mTextPaintS.setTextAlign(Align.CENTER);


        paintRain = new Paint();
        paintRain.setColor(Color.parseColor("#40FFFFFF"));
        paintRain.setStrokeJoin(Paint.Join.ROUND);
        paintRain.setStrokeCap(Paint.Cap.ROUND);
        paintRain.setStrokeWidth(1);

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
        viewHeight = getHeight();
//        viewWidth = getWidth();
        FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        // 计算文字高度

        float tempHeight = (getHeight() / 3) * 2;//温度图
        float rainHeight = (getHeight() / 3) * 2;//降雨量图高度为1/3
        float comWidth = getWidth() / countWidth;//横左边一个格子宽度；
        float fontHeight = fontMetrics.bottom - fontMetrics.top;
        canvas.drawLine(0, getHeight() - 2, getWidth(), getHeight() - 2, mline3Paint);

//        boolean goneRain = true;// 是否需要绘制雨量图
//        if (mLowRain != null) {
//            for (int i = 0; i < mLowRain.size(); i++) {
//                if (Float.compare(mLowRain.get(i), 0) == 0) {
//                    goneRain = false;
//                    tempHeight = getHeight() - fontHeight;
//                    break;
//                }
//            }
//        }

        if (mLowRain != null && mLowRain.size() > 0) {
//            降雨
            float minRain = 0.0f;
            float maxTemp = 0;
            try {
                //minRain = Collections.min(mLowRain);
                maxTemp = Collections.max(mLowRain);
            } catch (Exception e) {
                e.printStackTrace();
//                不存在最小值和最大值
                maxTemp = mLowRain.get(0);
                //minRain = mLowRain.get(0);
            }
            float h = maxTemp - minRain;
            Path rainPath3 = new Path();
            Path rainViewPath = new Path();//降雨量绘制区域第二段
            int sizeFirst = mLowRain.size() / 2;
            if (h == 0) {
//              降雨量都是一起的则画满（非0）
                if (maxTemp == 0) {
                } else {
                    float yPoint = getHeight() / 3;
                    rainPath3.moveTo(0, getHeight());
                    for (int i = 0; i < sizeFirst; i++) {
                        float xWidth = comWidth / 2 + comWidth * i;
                        if (i < sizeFirst - 1) {
                            rainPath3.lineTo(xWidth, yPoint);
                        } else if (i == sizeFirst - 1) {
                            rainPath3.lineTo(xWidth, yPoint);
                            rainPath3.lineTo(xWidth, viewHeight);
                            rainPath3.close();
                        }
                    }
                    rainViewPath.moveTo(comWidth / 2 + comWidth * sizeFirst, getHeight());
                    rainViewPath.lineTo(comWidth / 2 + comWidth * (sizeFirst - 1), yPoint);
                    for (int i = sizeFirst; i < mLowRain.size(); i++) {
                        float xWidth = comWidth / 2 + comWidth * i;
                        if (i < mLowRain.size() - 1) {
                            rainViewPath.lineTo(xWidth, yPoint);
                        } else if (i == mLowRain.size() - 1) {
                            rainViewPath.lineTo(xWidth, yPoint);
                            rainViewPath.lineTo(xWidth, viewHeight);
                            rainViewPath.close();
                        }
                    }
                    canvas.drawPath(rainPath3, paintRain);
                    canvas.drawPath(rainViewPath, paintRain);
                }
            } else {
                //根据比例话图
                List<Point> points = new ArrayList<>();
                float spaceRain = rainHeight / h;
                points.add(new Point(0, getHeight()));
                for (int i = 0; i < sizeFirst; i++) {
                    float xWidth = comWidth / 2 + comWidth * i;
                    float yPoint = viewHeight - (mLowRain.get(i) - minRain) * spaceRain;
                    points.add(new Point((int) xWidth, (int) yPoint));
                }
                drawMoreThreeRain(canvas, points);


                List<Point> points2 = new ArrayList<>();
                points2.add(new Point((int) (comWidth / 2 + comWidth * (sizeFirst - 1)), getHeight()));
                float yPointConnection = viewHeight - (mLowRain.get(sizeFirst - 1) - minRain) * spaceRain;
                points2.add(new Point((int) (comWidth / 2 + comWidth * (sizeFirst - 1)), (int) yPointConnection));
                for (int i = sizeFirst; i < mLowRain.size(); i++) {
                    float xWidth = comWidth / 2 + comWidth * i;
                    float yPoint = viewHeight - (mLowRain.get(i) - minRain) * spaceRain;
                    points2.add(new Point((int) xWidth, (int) yPoint));
                }
                drawMoreThreeRain(canvas, points2);
                for (int i = 0; i < mLowRain.size(); i++) {
                    float xWidth = comWidth / 2 + comWidth * i;
                    if (i > 0) {
//                        canvas.drawText("雨量" , xWidth, getHeight() - fontHeight / 2-fontHeight, mTextPaintS);
//                        canvas.drawText(mLowRain.get(i) + "mm", xWidth, getHeight() - fontHeight / 2, mTextPaintS);
//                    } else {
                        if (mLowRain.get(i) != 0) {
                            canvas.drawText(mLowRain.get(i) + "", xWidth, getHeight() - fontHeight / 2, mTextPaintS);
                        }
                    }
                }
            }

//          业务规则：当第一个城市雨量为0时，雨量位置一直显示“雨量 0.0mm”。不管后面的其他小时有无降雨。
            float xWidth = comWidth / 2;
            canvas.drawText("雨量", xWidth, getHeight()  - fontHeight, mTextPaintS);
            canvas.drawText(mLowRain.get(0) + "mm", xWidth, getHeight() - fontHeight / 5, mTextPaintS);
        }

        if (mTopTemp != null && mTopTemp.size() > 0) {
//            温度
            float minTemp = Collections.min(mTopTemp);
            float maxTemp = Collections.max(mTopTemp);
            float h = maxTemp - minTemp;
            if (h == 0) {
                float yPoint = tempHeight;//高的位置
                for (int i = 0; i < mTopTemp.size(); i++) {
                    float wPoint = comWidth / 2 + comWidth * i;//宽的位置
                    canvas.drawText(mTopTemp.get(i) + "°", wPoint, yPoint - fontHeight, mTextPaint);
                    canvas.drawCircle(wPoint, yPoint, CircleR, mPoint3Paint);
//                    canvas.drawCircle(wPoint, yPoint, CircleR, mPoint2Paint);
                }
                canvas.drawLine(comWidth / 2, yPoint, getWidth() - comWidth / 2, yPoint, mLine1Paint);
            } else {
                float spaceTemp = (tempHeight - marginTop) / h;
                List<Point> curvePoint = new ArrayList<>();
                List<Point> curvePoint2 = new ArrayList<>();
                for (int i = 0; i < mTopTemp.size(); i++) {
                    float yPoint = tempHeight - (mTopTemp.get(i) - minTemp) * spaceTemp;//高的位置
                    float xPoint = comWidth / 2 + comWidth * i;//宽的位置
                    canvas.drawText(mTopTemp.get(i) + "°", xPoint, yPoint - fontHeight / 2, mTextPaint);
                    canvas.drawCircle(xPoint, yPoint, CircleR, mPoint3Paint);
//                  canvas.drawCircle(xPoint, yPoint, CircleR, mPoint2Paint);

                    if (i <= mTopTemp.size() / 2) {
                        curvePoint.add(new Point((int) xPoint, (int) yPoint));
                        if (i == mTopTemp.size() / 2) {
                            curvePoint2.add(new Point((int) xPoint, (int) yPoint));
                        }
                    } else {
                        curvePoint2.add(new Point((int) xPoint, (int) yPoint));
                    }
//                绘制连接线
//                    if (i < mTopTemp.size()) {
//                        if (i >= 1) {
//                            float hProTop = tempHeight - (mTopTemp.get(i - 1) - minTemp) * spaceTemp;//高的位置
//                            float wProTop = comWidth / 2 + comWidth * (i - 1);//宽的位置
//                            drawScrollLine(canvas,mLine1Paint,xPoint, yPoint, wProTop, hProTop );
//
////                            canvas.drawLine(xPoint, yPoint, wProTop, hProTop, mLine1Paint);
//                        }
//                    }
                }
                drawBezier(canvas, curvePoint, mLine1Paint);
                drawBezier(canvas, curvePoint2, mLine1Paint);
            }
        }


//        if (mLowRain.size() < 2) {
//        } else {
//            Path path = new Path();
//            Path path2 = new Path();
//            path.moveTo(dx[0], (maxTemp - mTopTemp.get(0) + 2) * mSpace + marginTop);
//            path.lineTo(dx[1], (maxTemp - mTopTemp.get(1) + 2) * mSpace + marginTop);
//            path2.moveTo(dx[0], (maxTemp - mLowRain.get(0) + 2) * mSpace + marginTop);
//            path2.lineTo(dx[1], (maxTemp - mLowRain.get(1) + 2) * mSpace + marginTop);
//            canvas.drawPath(path, p);
//            canvas.drawPath(path2, p);
//        }
    }

//
//    private void drawScrollLine(Canvas canvas,Paint mPaint,float statrX,float statrY,float endX,float endY){
//        float centerX=(statrX+endX)/2;
//        float centerY=(statrY+endY)/2;
//        Path path=new Path();
//        path.moveTo(statrX,statrY);
////        path.quadTo(centerX,centerY,endX,endY);
//        path.cubicTo(centerX,statrY,centerX,endY,endX,endY);
//        canvas.drawPath(path,mPaint);
//    }


    /**
     * 画贝塞尔曲线
     * mPoints传入所有的点位置
     */
    private void drawBezier(Canvas canvas, List<Point> points, Paint mPaint) {
        if (points.size() < 3) {
            drawTwoPoint(canvas, points, mPaint);
        } else {
            drawMoreThree(canvas, points, mPaint);
        }
    }

    private void drawTwoPoint(Canvas canvas, List<Point> points, Paint mPaint) {
        if (points.size() == 2) {
            // 绘制路径
            Path mPath = new Path();
            mPath.moveTo(points.get(0).x, points.get(0).y);// 起点
            mPath.quadTo((points.get(0).x + points.get(1).x) / 2, points.get(1).y, points.get(1).x, points.get(1).y);// 控制点
            canvas.drawPath(mPath, mPaint);
        }
    }

    private void drawMoreThree(Canvas canvas, List<Point> points, Paint mPaint) {
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

        // 绘制路径
        Path mPath = new Path();
        for (int i = 0; i < points.size(); i++) {
            if (i == 0) {// 第一条为二阶贝塞尔
                mPath.moveTo(points.get(i).x, points.get(i).y);// 起点
                mPath.quadTo(mControlPoints.get(i).x, mControlPoints.get(i).y, points.get(i + 1).x, points.get(i + 1).y);// 控制点
            } else if (i < points.size() - 2) {// 三阶贝塞尔
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


    /*绘制降雨量趋势图*/
    private void drawMoreThreeRain(Canvas canvas, List<Point> points) {
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
        // 绘制路径
        Path mPath = new Path();
        for (int i = 0; i < points.size(); i++) {
            if (i == 0) {// 第一条为二阶贝塞尔
                mPath.moveTo(points.get(i).x, points.get(i).y);
                // 起点
            } else if (i == points.size() - 1) {
                if (points.get(i - 1).y > points.get(i).y) {
                    mPath.quadTo((points.get(i).x + points.get(i - 1).x) / 2, points.get(i).y, points.get(i).x, points.get(i).y);// 控制点
                } else {
                    mPath.quadTo((points.get(i).x + points.get(i - 1).x) / 2, points.get(i - 1).y, points.get(i).x, points.get(i).y);// 控制点
                }
                mPath.lineTo(points.get(i).x, viewHeight);
                mPath.close();
            } else if (i == 1) {
                mPath.quadTo(mControlPoints.get(i - 1).x, mControlPoints.get(i - 1).y, points.get(i).x, points.get(i).y);// 控制点
            } else if (i < points.size() - 1) {// 三阶贝塞尔
                // 三阶贝塞尔
                if (points.get(i).y == points.get(i - 1).y) {
                    mPath.lineTo(points.get(i).x, points.get(i).y);
                } else {
                    Point p = points.get(i);// 终点
                    Point conPa = mControlPoints.get(2 * (i - 2) + 1);// 控制点a
                    Point conPb = mControlPoints.get(2 * (i - 1));// 控制点b
                    mPath.cubicTo(conPa.x, conPa.y, conPb.x, conPb.y, p.x, p.y);
                }
            }
        }
        canvas.drawPath(mPath, paintRain);
    }


    public void setCount(int count) {
        countWidth = count;
    }
}
