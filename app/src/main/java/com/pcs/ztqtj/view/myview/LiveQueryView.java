package com.pcs.ztqtj.view.myview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.inter.ClickPositionListener;
import com.pcs.ztqtj.control.livequery.ControlDistribution.ColumnCategory;
import com.pcs.ztqtj.control.tool.DrawViewTool;
import com.pcs.lib.lib_pcs_v3.control.tool.ScreenUtil;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackFycxTrendDown;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.pcs.ztqtj.control.livequery.ControlDistribution.ColumnCategory.RAIN;

/**
 * Created by Z on 2016/8/4.
 */
public class LiveQueryView extends View {
    protected Bitmap mBitmap;
    static final int NONE = 0;
    static final int DRAG = 1; // 拖动中
    static final int ZOOM = 2; // 缩放中
    private int mode = NONE; // 当前的事件
    /**
     * 就算两点间的距离
     */
    int startX;

    /**
     * 就算两点间的距离
     */
    int startY;

    int stopX;

    int stopY;

    float oldDist;

    private float mScale = 1.0f;

    private float mTop;

    private float mBottom;

    private float mLeft;

    private float mRight;

    private float disX;//移动的x距离

    private float disY;//移动的Y距离

    private int whatX;//总共移动距离

    private int whatY;//总共移动的Y距离

    protected Rect srcRect;

    protected RectF dstRect;
    private boolean reinvalidate = true;

    private ClickBitmap cutClickBitmap = new ClickBitmap();

    public LiveQueryView(Context context) {
        super(context);
        this.setPadding(0, 0, 0, 0);
    }


    public LiveQueryView(Context context, AttributeSet a) {
        super(context, a);
        this.setPadding(0, 0, 0, 0);
        initPointValue();
    }

    private float moviceStart;
    private Paint ptLineWidth;
    private Paint ptLine;
    private Paint ptLineNull;
    private Paint ptPre24;
    private Paint ptNext24;
    private Paint ptTextButtom;
    private Paint ptTextButtomFloag;
    private Paint ptTextTop;
    private Paint ptTextYValue;
    private float margLeft, margButton, margRight, margTop;
    private int stationX = 96;
    private int ringR = 0;
    private Bitmap bmAct;
    private Bitmap bmPro;

    private void initPointValue() {
        bmAct = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_livequery_wind_act);
        bmPro = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_livequery_wind_pro);
        margTop = ScreenUtil.dip2px(getContext(), 15);
        margRight = ScreenUtil.dip2px(getContext(), 15);

        sizeText = ScreenUtil.dip2px(getContext(), 9);
        ringR = ScreenUtil.dip2px(getContext(), 4);
        ptTextButtom = DrawViewTool.getInstance().getTextPaint(getContext().getResources().getColor(R.color
                .livequery_actual));
        ptTextTop = DrawViewTool.getInstance().getTextPaint(getContext().getResources().getColor(R.color
                .livequery_prediction));
        ptTextButtomFloag = DrawViewTool.getInstance().getTextPaint(getContext().getResources().getColor(R.color
                .livequery_prediction));
        ptTextYValue = DrawViewTool.getInstance().getTextPaint(getContext().getResources().getColor(R.color
                .text_black));
        ptTextButtom.setTextSize(sizeText);
        ptTextTop.setTextSize(sizeText);
        ptTextYValue.setTextSize(sizeText);
        ptTextButtomFloag.setTextSize(sizeText);

        ptLineWidth = DrawViewTool.getInstance().getMyLine(Color.argb(255, 255, 255, 255));
        ptLine = DrawViewTool.getInstance().getMyLine(Color.argb(255, 0, 0, 0));
        ptLine.setStrokeWidth(1);

        ptLineNull = DrawViewTool.getInstance().getMyLine(Color.argb(255, 0, 0, 0));
        ptLineNull.setStrokeWidth(1);
        ptLineNull.setStyle(Paint.Style.STROKE);
        PathEffect effects = new DashPathEffect(new float[]{8, 8, 8, 8}, 1);
        ptLineNull.setPathEffect(effects);

        ptNext24 = DrawViewTool.getInstance().getMyLine(getContext().getResources().getColor(R.color
                .livequery_prediction));
        ptPre24 = DrawViewTool.getInstance().getMyLine(getContext().getResources().getColor(R.color.livequery_actual));
        ptNext24.setStrokeWidth(ringR / 3);
        ptPre24.setStrokeWidth(ringR / 3);
    }

    /**
     * 处理触碰事件
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                moviceStart = (int) event.getX();
                startX = (int) event.getX();
                startY = (int) event.getY();
                mode = DRAG;
                compleValue(startX, startY);
//                compleValuePopupWindow(startX,startY);
                break;
            case MotionEvent.ACTION_UP:
                if (Math.abs(moviceStart - event.getX()) > 4) {
                    if (clicklistener != null) {
                        cutClickBitmap.bm = null;
                        invalidate();
//                        clicklistener.moveListener();
                    }
                }
                mode = NONE;
                checkOutScreen();
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                    stopX = (int) event.getX();
                    stopY = (int) event.getY();
                    this.disX = stopX - startX;
                    this.disY = stopY - startY;
                    startX = stopX;
                    startY = stopY;
                    invalidate();
                }
//                // 若为ZOOM模式，则多点触摸缩放
//                else if (mode == ZOOM) {
//                    float newDist = spacing(event);
//                    float scale = newDist / oldDist;
//                    if (newDist > 50f && Math.abs(scale - this.mScale) > 0.02) {
//                        oldDist = newDist;
//                        postScale(scale);
//                        invalidate();
//                    }
//                }

                break;
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event);
                if (oldDist > 50f) {
                    mode = ZOOM;
                }
                break;
        }
        return true;
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private String strValue = "";
    private String strUnit = "mm";
    private ColumnCategory cutType = RAIN;

    public String getUnit() {
        return strUnit;
    }

    public void setItemName(String strValue, ColumnCategory cutType, String strUnit, IsDrawRectangele cutDraw) {
        this.cutType = cutType;
//      this.strValue = strValue;
        this.strUnit = strUnit;
        this.cutDraw = cutDraw;
    }

    private boolean isDoubleWidth = false;

    private void compleValue(int touchX, int touchY) {
//        绘图高度的起始点
//        if (isDoubleWidth) {
        float viewYHight = getHeight() - margButton;
        for (int i = 0; i < rectanglePreHight.size(); i++) {
            Point pointCut = rectanglePreHight.get(i);
            float pY = viewYHight - pointCut.y;
            float pX = pointCut.x + whatX;
            if (touchX > pX - ringR && touchX < pX + ringR) {
                if (cutType == RAIN) {//降雨量时
                    if (pointCut.y == 0) {
                        if (touchY >= viewYHight - ringR && touchY < viewYHight + ringR) {
                            setPreCompView(i, pointCut);
                            return;
                        }
                    } else {
                        if (touchY >= pY && touchY < viewYHight) {
                            setPreCompView(i, pointCut);
                            return;
                        }
                    }
                } else {
                    //非降雨量时
                    if (touchY >= (pY - ringR) && touchY <= (pY + ringR)) {
                        //点击同一个点或者柱状图两次。第二次为关闭
                        setPreCompView(i, pointCut);
                    }
                }
            }
        }
        for (int i = 0; i < rectangleAcHight.size(); i++) {
            Point pointCut = rectangleAcHight.get(i);
            float pY = viewYHight - pointCut.y;
            float pX = pointCut.x + whatX;
            if (touchX > pX - xSaction && touchX < pX + xSaction) {
                if (cutType == RAIN) {
                    if (pointCut.y == 0) {
                        if (touchY >= viewYHight - ringR && touchY < viewYHight + ringR) {
                            setActView(i, pointCut);
                            return;
                        }
                    } else {
                        if (touchY >= pY && touchY < viewYHight) {
                            setActView(i, pointCut);
                            return;
                        }
                    }
                } else {
                    if (touchY >= (pY - ringR) && touchY <= (pY + ringR)) {
                        //点击同一个点或者柱状图两次。第二次为关闭
                        setActView(i, pointCut);
                        return;
                    }
                }
            }
        }
//        }
    }

    /***
     * 升成实况的图片
     * @param i 图片的位置为第几
     * @param p 生成图片的位置参数
     */
    private void setActView(int i, Point p) {
        if (cutClickBitmap.bm != null && cutClickBitmap.bitmapPosition == i && !cutClickBitmap.isYbBitmap) {
            cutClickBitmap.bm = null;
        } else {
            cutClickBitmap.bitmapPosition = i;
            cutClickBitmap.isYbBitmap = false;

            String value = "--";
            if (!TextUtils.isEmpty(skList.get(i).val)) {
                value = skList.get(i).val;
            }
            drawClickImageView(p, skList.get(i).fulldt + "\n" + strValue + value + strUnit, false);
        }
    }

    /***
     * 生成预报的图片
     *  @param i 图片的位置为第几
     * @param p 生成图片的位置参数
     */
    private void setPreCompView(int i, Point p) {
        //点击同一个点或者柱状图两次。第二次为关闭
        if (cutClickBitmap.bm != null && cutClickBitmap.bitmapPosition == i && cutClickBitmap.isYbBitmap) {
            cutClickBitmap.bm = null;
        } else {
            cutClickBitmap.isYbBitmap = true;
            cutClickBitmap.bitmapPosition = i;
            String value = "--";
            if (!TextUtils.isEmpty(ybList.get(i).val)) {
                value = ybList.get(i).val;
            }
            drawClickImageView(p, ybList.get(i).fulldt + "\n" + strValue + value + strUnit, true);
        }
        return;
    }

    private void drawClickImageView(Point p, String value, boolean isYb) {
//        重新绘制图片
        reinvalidate = true;
        cutClickBitmap.p.x = p.x;
//        cutClickBitmap.p.x = p.x + whatX;
        cutClickBitmap.p.y = (int) (p.y + margButton);
        if (p.y + getResources().getDimensionPixelSize(R.dimen.tvPopH) + margTop + margButton < getHeight()) {
            cutClickBitmap.bm = getClickBitmap(value, isYb, true);
            cutClickBitmap.isDrawTop = true;
        } else {
            cutClickBitmap.bm = getClickBitmap(value, isYb, false);
            cutClickBitmap.isDrawTop = false;
        }
        invalidate();
    }

    private ClickPositionListener clicklistener;

    public void setClickPositionListener(ClickPositionListener clicklistener) {
        this.clicklistener = clicklistener;
    }

    /**
     * 处理图片溢出屏幕
     */
    private void checkOutScreen() {
        float left = mLeft + whatX;
        float right = mRight + whatX;
        float top = mTop + whatY;
        float bottom = mBottom + whatY;

        float wight = right - left;
        float height = bottom - top;

        float screenW = getWidth();
        float screenH = getHeight();

        float disX = 0;
        float disY = 0;

        if (height < screenH) {
            if (top < 0) {
                disY = 0 - top;
            } else if (bottom > (getBottom() - getTop())) {
                disY = (getBottom() - getTop()) - bottom;
            }
        } else {
            if (top > 0) {
                disY = 0 - top;
            } else if (bottom < (getBottom() - getTop())) {
                disY = (getBottom() - getTop()) - bottom;
            }
        }

        if (wight < screenW) {
            if (left < getLeft()) {
                disX = getLeft() - left;
            } else if (right > getRight()) {
                disX = getRight() - right;
            }
        } else {
            if (left > getLeft()) {
                disX = getLeft() - left;
            } else if (right < getRight()) {
                disX = getRight() - right;
            }
        }
        this.disX = disX;
        this.disY = disY;
    }


    private void postScale(float scale) {
        this.mScale = scale;
        float disX = (scale - 1) * (mRight - mLeft) / 2;
        float disY = (scale - 1) * (mBottom - mTop) / 2;
        mRight += disX;
        mLeft -= disX;
        mTop -= disY;
        mBottom += disY;
    }


    private void setBitmap(Bitmap bm) {
        float mScale = (float) getHeight() / (float) bm.getHeight();
        this.mScale = mScale;
        imageStartLeft(bm, mScale);
    }


    /**
     * 初始化状态
     */
    private void reDefaultValue() {
        this.mLeft = 0;
        this.whatX = 0;
        this.mRight = 0;
        this.mTop = 0;
        this.whatY = 0;
        this.mBottom = 0;
        this.srcRect = null;
        this.dstRect = null;
        this.mBitmap = null;
    }


    /**
     * 居中 所有的都居中
     *
     * @param bm
     * @param mScale
     */
    private void imageStartCenter(Bitmap bm, float mScale) {
        float imageWidth = bm.getWidth() * mScale;
        float imageHight = bm.getHeight() * mScale;
        mLeft = (getWidth() - imageWidth) / 2;
        mTop = (getHeight() - imageHight) / 2;
        mRight = mLeft + imageWidth;
        mBottom = mTop + imageHight;
    }

    /**
     * 居中 所有的都居中
     *
     * @param bm
     * @param mScale
     */
    private void imageStartLeft(Bitmap bm, float mScale) {
        float imageWidth = bm.getWidth() * mScale;
        float imageHight = bm.getHeight() * mScale;
        mLeft = 0;
        mTop = 0;
        mRight = mLeft + imageWidth;
        mBottom = mTop + imageHight;
    }


    public void moveToPro(boolean isTrue) {
        mLeft = 0;
        whatX = 0;
        cutClickBitmap.bm = null;
        if (isTrue) {
            disX = -(getWidth() * widthScale) / 2;
        } else {
            disX = 0;
        }
        invalidate();
    }


    private List<Point> rectangleAcHight = new ArrayList<>();//实况
    private List<Point> rectanglePreHight = new ArrayList<>();//预报
    private List<String> yValue = new ArrayList<>();
    private List<String> xValueButtom = new ArrayList<>();
    private List<String> xValueTop = new ArrayList<>();

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mBitmap = drawImage();
        setBitmap(mBitmap);
        checkOutScreen();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (reinvalidate) {
            mBitmap = drawImage();
            setBitmap(mBitmap);
            reinvalidate = false;
            checkOutScreen();
        }
        if (null == mBitmap || mBitmap.isRecycled()) {
            canvas.drawARGB(0, 255, 255, 255);
            return;
        }
        float left = mLeft + whatX;
        float right = mRight + whatX;
        float top = mTop + whatY;
        float bottom = mBottom + whatY;

        if (disY != 0 || disX != 0) {
            whatX += disX;
            whatY += disY;
            left += disX;
            right += disX;
            top += disY;
            bottom += disY;
        }
        srcRect = new Rect(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
        dstRect = new RectF(left, mTop, right, mBottom);
//        dstRect = new RectF(left, top, right, bottom);
        canvas.drawBitmap(mBitmap, srcRect, dstRect, null);
        this.disY = 0;
        this.disX = 0;

//        &&&&&&&&&&&&&&&绘制左边固定坐标轴&&&&&&&&&&&
//        背景覆盖
        Path mPath = new Path();
        mPath.moveTo(0, 0);
        mPath.lineTo(margLeft, 0);
        mPath.lineTo(margLeft, getHeight());
        mPath.lineTo(0, getHeight());
        mPath.close();
//        绘制线以及坐标值
        canvas.drawPath(mPath, ptLineWidth);
        canvas.drawLine(margLeft, margTop, margLeft, getHeight() - margButton, ptLine);
//        canvas.drawLine(margLeft, 0, margLeft - getResources().getDimensionPixelSize(R.dimen.dimen4), getResources
// ().getDimensionPixelSize(R.dimen.dimen4), ptLine);//纵坐标
        float acturalYhight = getHeight() - margButton - margTop;//实际绘制图片的高度区域
        float actionYalue = acturalYhight / ySize;
        Paint.FontMetrics fontMetrics = ptTextButtom.getFontMetrics();
        float fontHeight = fontMetrics.bottom - fontMetrics.top;
        for (int i = 0; i < yValue.size(); i++) {
            float yValueHight = actionYalue * i + margTop + fontHeight / 3;
            canvas.drawText(yValue.get(i), margLeft / 2, yValueHight, ptTextYValue);
        }
    }

    private int widthScale = 2;
    private int sizeText;

    public void setDoubleWidth(boolean isDouble) {

        isDoubleWidth = isDouble;
        if (isDouble) {
            widthScale = 4;
            sizeText = ScreenUtil.dip2px(getContext(), 12);
            ringR = ScreenUtil.dip2px(getContext(), 6);
            this.mLeft = mLeft * 2;
            this.disX = disX * 2;
            this.whatX = whatX * widthScale;
            this.mRight = mRight * widthScale;
        } else {
            widthScale = 2;
            sizeText = ScreenUtil.dip2px(getContext(), 9);
            ringR = ScreenUtil.dip2px(getContext(), 4);
            this.mLeft = mLeft / 2;
            this.disX = disX / 2;
            this.whatX = whatX / widthScale;
            this.mRight = mRight / widthScale;
        }
        ptTextButtom.setTextSize(sizeText);
        ptTextTop.setTextSize(sizeText);
        ptTextYValue.setTextSize(sizeText);
        cutClickBitmap.bm = null;
        reinvalidate = true;
        invalidate();
    }

    private float xSaction;

    /*  创建一个画布，在这个画笔上绘制图，然后用这张图放大缩小；*/
    private Bitmap drawImage() {
        int AllViewWidth = getWidth() * widthScale;
        Bitmap bitmapAltered = Bitmap.createBitmap(AllViewWidth, getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvasBm = new Canvas(bitmapAltered);
        float viewXWidth, viewYHight, acturalYhight;// 绘图区域宽高（扣除写值区域）
        // 计算文字高度

        Paint.FontMetrics fontMetrics = ptTextButtomFloag.getFontMetrics();
        float fontHeight = fontMetrics.bottom - fontMetrics.top;
        margButton = (int) (4 * fontHeight);
//      margTop = (int) (2 * fontHeight);
        margLeft = 4 * fontHeight;
        if (isDoubleWidth) {
            margRight = 2 * fontHeight;
        } else {
            margRight = fontHeight;
        }
        viewXWidth = AllViewWidth - margLeft - margRight;
        viewYHight = getHeight() - margButton;
        acturalYhight = getHeight() - margButton - margTop;//实际绘制图片的高度区域

        canvasBm.drawLine(margLeft, viewYHight, AllViewWidth - margRight, viewYHight, ptLine);//横坐标

//        canvasBm.drawLine(margLeft, 0, margLeft, viewYHight-margTop, ptLine);//纵坐标
//        canvasBm.drawLine(margLeft, 0,margLeft - getResources().getDimensionPixelSize(R.dimen.dimen4),getResources
// ().getDimensionPixelSize(R.dimen.dimen4),ptLine);//纵坐标

        canvasBm.drawLine(margLeft + viewXWidth, margTop, margLeft + viewXWidth, viewYHight, ptLine);//纵坐标
        xSaction = viewXWidth / stationX;
        countValue((int) acturalYhight, xSaction);
        //&&&&&&&&&&&&&&&&&&&&&&&&&&&&  Y轴的值，和格子&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
        float actionYalue = acturalYhight / ySize;
        for (int i = 0; i < ySize; i++) {
            float yValueHight = actionYalue * i + margTop;
            Path mPath = new Path();
//            canvasBm.drawLine(margLeft, yValueHight, AllViewWidth - margRight, yValueHight, ptLine);//横坐标
            mPath.moveTo(margLeft, yValueHight);
            mPath.lineTo(AllViewWidth - margRight, yValueHight);
            canvasBm.drawPath(mPath, ptLineNull);
        }

//        for (int i = 0; i < yValue.size(); i++) {
//            float yValueHight = actionYalue * i + margTop + fontHeight / 3;
//            canvasBm.drawText(yValue.get(i), margLeft / 2, yValueHight, ptTextYValue);
//        }

        //&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
        for (int i = 0; i < xValueTop.size(); i++) {
            Bitmap timeBmTemp = Bitmap.createBitmap((int) xSaction / 2 * 3, (int) margButton, Bitmap.Config.ARGB_8888);
            Canvas canvasBmTemp = new Canvas(timeBmTemp);
            if (xValueTop.get(i).length() < 4) {
                canvasBmTemp.drawText(xValueTop.get(i).replace("时", ""), timeBmTemp.getWidth() / 2, timeBmTemp
                        .getHeight() / 2 - fontHeight / 2, ptTextTop);
                canvasBmTemp.drawText("时", timeBmTemp.getWidth() / 2, timeBmTemp.getHeight() / 2 + fontHeight / 2,
                        ptTextTop);
            } else {
                String tiem = xValueTop.get(i);
                canvasBmTemp.drawText(tiem.substring(0, tiem.indexOf("日")), timeBmTemp.getWidth() / 2, timeBmTemp
                        .getHeight() / 2 - fontHeight / 2, ptTextTop);
                canvasBmTemp.drawText("日", timeBmTemp.getWidth() / 2, timeBmTemp.getHeight() / 2 + fontHeight / 2,
                        ptTextTop);
            }
            float withXPosition = (int) (xSaction * ((i + 24) * 2 + 1) + margLeft - xSaction / 2);//每一个值横坐标的位置
            canvasBm.drawBitmap(timeBmTemp, withXPosition, getHeight() - margButton, new Paint());
        }
        for (int i = 0; i < xValueButtom.size(); i++) {
            Bitmap timeBmTemp = Bitmap.createBitmap((int) xSaction / 2 * 3, (int) margButton, Bitmap.Config.ARGB_8888);
            Canvas canvasBmTemp = new Canvas(timeBmTemp);
            if (xValueButtom.get(i).length() < 4) {
                canvasBmTemp.drawText(xValueButtom.get(i).replace("时", ""), timeBmTemp.getWidth() / 2, timeBmTemp
                        .getHeight() / 2 - fontHeight / 2, ptTextButtom);
                canvasBmTemp.drawText("时", timeBmTemp.getWidth() / 2, timeBmTemp.getHeight() / 2 + fontHeight / 2,
                        ptTextButtom);
            } else {
                String tiem = xValueButtom.get(i);
                canvasBmTemp.drawText(tiem.substring(0, tiem.indexOf("日")), timeBmTemp.getWidth() / 2, timeBmTemp
                        .getHeight() / 2 - fontHeight / 2, ptTextButtom);
                canvasBmTemp.drawText("日", timeBmTemp.getWidth() / 2, timeBmTemp.getHeight() / 2 + fontHeight / 2,
                        ptTextButtom);
            }
            float withXPosition = (int) (xSaction * (i * 2 + 1) + margLeft - xSaction / 2);//每一个值横坐标的位置
            canvasBm.drawBitmap(timeBmTemp, withXPosition, getHeight() - margButton, new Paint());
        }

        if (cutDraw == IsDrawRectangele.RECTANGLE) {
            drawRectangle(canvasBm, viewYHight);
        } else if (cutDraw == IsDrawRectangele.BROKENLINE) {
            drawBrokenLine(canvasBm, viewYHight);
        } else {
            drawBrokenLineDire(canvasBm, viewYHight);
        }


        //绘制顶层点击弹窗
        if (cutClickBitmap.bm != null) {
            int bmx, bmy;
            bmx = cutClickBitmap.bm.getWidth();
            bmy = cutClickBitmap.bm.getHeight();
            int x = cutClickBitmap.p.x - bmx / 2;
            int tempY = (getHeight() - cutClickBitmap.p.y);
            int y;
            if (cutClickBitmap.isDrawTop) {
                y = tempY - bmy;
            } else {
                y = tempY;
            }
            canvasBm.drawBitmap(cutClickBitmap.bm, x, y, new Paint());
        }


        return bitmapAltered;
    }


    private IsDrawRectangele cutDraw = IsDrawRectangele.RECTANGLE;

    public enum IsDrawRectangele {
        RECTANGLE, BROKENLINE, DIRECTIONLINE;
    }

    /***
     * 绘制矩形图
     */
    private void drawRectangle(Canvas canvasBm, float viewYHight) {
        ptPre24.setStrokeWidth(xSaction);
        ptNext24.setStrokeWidth(xSaction);
        for (int i = 0; i < rectangleAcHight.size(); i++) {
            Point p = rectangleAcHight.get(i);
            if (p.y == DataNull) {
//                canvasBm.drawCircle(p.x, viewYHight, ringR, ptPre24);
            } else {
                if (p.y == 0) {
                    canvasBm.drawCircle(p.x, viewYHight, ringR, ptPre24);
                } else {
                    canvasBm.drawLine(p.x, viewYHight - p.y, p.x, viewYHight, ptPre24);
                }
            }
        }
        for (int i = 0; i < rectanglePreHight.size(); i++) {
            Point p = rectanglePreHight.get(i);
            if (p.y != DataNull) {
                if (p.y == 0) {
                    canvasBm.drawCircle(p.x, viewYHight, ringR, ptNext24);
                } else {
                    canvasBm.drawLine(p.x, viewYHight - p.y, p.x, viewYHight, ptNext24);
                }
            } else {
//                canvasBm.drawCircle(p.x, viewYHight, ringR, ptNext24);
            }
        }
    }


    /***
     * 绘制折线图
     */
    private void drawBrokenLine(Canvas canvasBm, float viewYHight) {
        ptNext24.setStrokeWidth(ringR / 3);
        ptPre24.setStrokeWidth(ringR / 3);
        //绘制连接线-------------实况--预报-----------------
        if (rectangleAcHight.size() > 0 && rectanglePreHight.size() > 0) {
            Point pPro = rectangleAcHight.get(rectangleAcHight.size() - 1);
            if (pPro.y == DataNull) {
                for (int i = rectangleAcHight.size() - 1; i >= 0; i--) {
                    if (rectangleAcHight.get(i).y != DataNull) {
                        pPro = rectangleAcHight.get(i);
                        break;
                    }
                }
            }
            Point pNext = rectanglePreHight.get(0);
            if (pNext.y == DataNull) {
                for (int i = rectanglePreHight.size() - 1; i >= 0; i--) {
                    if (rectanglePreHight.get(i).y != DataNull) {
                        pNext = rectanglePreHight.get(i);
                        break;
                    }
                }
            }
            if (pNext.y != DataNull && pPro.y != DataNull) {
                canvasBm.drawLine(pPro.x, viewYHight - pPro.y, pNext.x, viewYHight - pNext.y, ptNext24);
            }
        }
//        --------------------------------

        for (int i = 0; i < rectangleAcHight.size(); i++) {
            Point p = rectangleAcHight.get(i);
            if (p.y != DataNull) {
                canvasBm.drawCircle(p.x, viewYHight - p.y, ringR, ptPre24);
                if (i > 0) {
                    Point pp = rectangleAcHight.get(i - 1);
//                    为空是取上一个不会空的值连线
                    if (pp.y == DataNull) {
                        for (int j = i - 1; j >= 0; j--) {
                            if (rectangleAcHight.get(j).y != DataNull) {
                                pp = rectangleAcHight.get(j);
                                break;
                            }
                        }
                    }
//
                    if (pp.y != DataNull) {
                        canvasBm.drawLine(pp.x, viewYHight - pp.y, p.x, viewYHight - p.y, ptPre24);
                    }
                }
            }
        }
        for (int i = 0; i < rectanglePreHight.size(); i++) {
            Point p = rectanglePreHight.get(i);
            if (p.y != DataNull) {
                canvasBm.drawCircle(p.x, viewYHight - p.y, ringR, ptNext24);
                if (i != 0) {
                    Point pp = rectanglePreHight.get(i - 1);
//                    --------为空向上取值-------
                    if (pp.y == DataNull) {
                        for (int j = i - 1; j >= 0; j--) {
                            if (rectanglePreHight.get(j).y != DataNull) {
                                pp = rectanglePreHight.get(j);
                                break;
                            }
                        }
                    }
//                    ------------------
                    if (p.y != DataNull && pp.y != DataNull) {
                        canvasBm.drawLine(pp.x, viewYHight - pp.y, p.x, viewYHight - p.y, ptNext24);
                    }
                }
            }
        }
    }


    /***
     * 绘制折线图风向
     */
    private void drawBrokenLineDire(Canvas canvasBm, float viewYHight) {
        ptNext24.setStrokeWidth(ringR / 3);
        ptPre24.setStrokeWidth(ringR / 3);
        if (rectangleAcHight.size() > 0 && rectanglePreHight.size() > 0) {
            Point pPro = rectangleAcHight.get(rectangleAcHight.size() - 1);
            if (pPro.y == DataNull) {
                for (int i = rectangleAcHight.size() - 1; i >= 0; i--) {
                    if (rectangleAcHight.get(i).y != DataNull) {
                        pPro = rectangleAcHight.get(i);
                        break;
                    }
                }
            }
            Point pNext = rectanglePreHight.get(0);
            if (pNext.y == DataNull) {
                for (int i = rectanglePreHight.size() - 1; i >= 0; i--) {
                    if (rectanglePreHight.get(i).y != DataNull) {
                        pNext = rectanglePreHight.get(i);
                        break;
                    }
                }
            }
            if (pNext.y != DataNull && pPro.y != DataNull) {
                canvasBm.drawLine(pPro.x, viewYHight - pPro.y, pNext.x, viewYHight - pNext.y, ptNext24);
            }
        }

//        --------------------------

        for (int i = 0; i < rectangleAcHight.size(); i++) {
            Point p = rectangleAcHight.get(i);
            if (p.y != DataNull) {
//                canvasBm.drawCircle(p.x, viewYHight - p.y, ringR, ptPre24);
                if (i > 0) {
                    Point pp = rectangleAcHight.get(i - 1);

                    //                    --------为空向上取值-------
                    if (pp.y == DataNull) {
                        for (int j = i - 1; j >= 0; j--) {
                            if (rectangleAcHight.get(j).y != DataNull) {
                                pp = rectangleAcHight.get(j);
                                break;
                            }
                        }
                    }
//                    ------------------

                    if (pp.y != DataNull) {
                        canvasBm.drawLine(pp.x, viewYHight - pp.y, p.x, viewYHight - p.y, ptPre24);
                    }
                }
                int direction = 0;
                String dirString = this.skList.get(i).wind_dir;
                try {
                    direction = Integer.parseInt(dirString);
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                String dirString=this.ybList.get(i).wind_dir;
                drawRotateBitmap(canvasBm, new Paint(), bmAct, direction, p.x, viewYHight - p.y);
            }
        }
        for (int i = 0; i < rectanglePreHight.size(); i++) {
            Point p = rectanglePreHight.get(i);
            if (p.y != DataNull) {
//                canvasBm.drawCircle(p.x, viewYHight - p.y, ringR, ptNext24);
                if (i > 0) {
                    Point pp = rectanglePreHight.get(i - 1);
                    //  --------为空向上取值-------
                    if (pp.y == DataNull) {
                        for (int j = i - 1; j >= 0; j--) {
                            if (rectangleAcHight.get(j).y != DataNull) {
                                pp = rectangleAcHight.get(j);
                                break;
                            }
                        }
                    }
//                    ------------------
                    if (pp.y != DataNull) {
                        canvasBm.drawLine(pp.x, viewYHight - pp.y, p.x, viewYHight - p.y, ptNext24);
                    }
                }

                int direction = 0;
                String dirString = this.ybList.get(i).wind_dir;
                try {
                    direction = Integer.parseInt(dirString);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                drawRotateBitmap(canvasBm, new Paint(), bmPro, direction, p.x, viewYHight - p.y);
            }
        }
    }


    /**
     * 绘制自旋转位图
     *
     * @param canvas
     * @param paint
     * @param bitmap   位图对象
     * @param rotation 旋转度数
     * @param posX     在canvas的位置坐标
     * @param posY
     */
    private void drawRotateBitmap(Canvas canvas, Paint paint, Bitmap bitmap, float rotation, float posX, float posY) {
//###############转成固定大小########################
        int imgW = 30;
        if (isDoubleWidth) {
            imgW = 60;
        } else {
            imgW = 30;
        }
        Bitmap timeBmTemp = Bitmap.createBitmap(imgW, imgW, Bitmap.Config.ARGB_8888);
        Rect src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        RectF dst = new RectF(0, 0, imgW, imgW);
        Canvas canvasBmTemp = new Canvas(timeBmTemp);
        canvasBmTemp.drawBitmap(bitmap, src, dst, new Paint());
// ###########################
//&&&&&&&&&&&&&&&&&旋转角度&&&&&&&&&&&&&&&&&&&&&&
        Matrix matrix = new Matrix();
        int offsetX = timeBmTemp.getWidth() / 2;
        int offsetY = timeBmTemp.getHeight() / 2;
        matrix.postTranslate(-offsetX, -offsetY);
        matrix.postRotate(rotation);
        matrix.postTranslate(posX, posY);
        canvas.drawBitmap(timeBmTemp, matrix, paint);
    }


    public String reverse(String s) {
        return new StringBuffer(s).reverse().toString();
    }

    private int ySize = 8;//y轴分成几格
    private float DataNull = -100000;

    private List<PackFycxTrendDown.FycxMapBean> skList = new ArrayList<>();
    private List<PackFycxTrendDown.FycxMapBean> ybList = new ArrayList<>();

    /**
     * 设置数据
     */
    public void setNewData(List<PackFycxTrendDown.FycxMapBean> skList, List<PackFycxTrendDown.FycxMapBean> ybList) {
        this.skList.clear();
        this.ybList.clear();
        this.skList.addAll(skList);
        this.ybList.addAll(ybList);
        cutClickBitmap.bm = null;
        reinvalidate = true;
        reDefaultValue();
        invalidate();
    }


    /**
     * 旋转方向
     *
     * @param bitmap
     * @param orientationDegree 0 - 360 范围
     * @return
     */
    private Bitmap adjustPhotoRotation(Bitmap bitmap, int orientationDegree) {
        Matrix matrix = new Matrix();
        matrix.setRotate(orientationDegree, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);
        float targetX, targetY;
        if (orientationDegree == 90) {
            targetX = bitmap.getHeight();
            targetY = 0;
        } else {
            targetX = bitmap.getHeight();
            targetY = bitmap.getWidth();
        }
        final float[] values = new float[9];
        matrix.getValues(values);
        float x1 = values[Matrix.MTRANS_X];
        float y1 = values[Matrix.MTRANS_Y];
        matrix.postTranslate(targetX - x1, targetY - y1);
        Bitmap canvasBitmap = Bitmap.createBitmap(bitmap.getHeight(), bitmap.getWidth(),
                Bitmap.Config.ARGB_8888);
        Paint paint = new Paint();
        Canvas canvas = new Canvas(canvasBitmap);
        canvas.drawBitmap(bitmap, matrix, paint);
        return canvasBitmap;
    }

    /**
     * 选择变换
     *
     * @param origin 原图
     * @param rotate 旋转角度，可正可负
     * @return 旋转后的图片
     */
    private Bitmap rotateBitmap(Bitmap origin, float rotate) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.setRotate(rotate);
        // 围绕原地进行旋转
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBM.equals(origin)) {
            return newBM;
        }
        origin.recycle();
        return newBM;
    }

    /**
     * 计算柱状图位置，高度值
     *
     * @param acturalYhight
     * @param xSaction
     */
    private void countValue(int acturalYhight, float xSaction) {
        xValueButtom.clear();
        xValueTop.clear();
        rectangleAcHight.clear();
        rectanglePreHight.clear();
        yValue.clear();
//        数据归类
        List<Float> listData = new ArrayList<>();
        List<Float> listDataAc = new ArrayList<>();
        List<Float> listDataPre = new ArrayList<>();
        for (int i = 0; i < skList.size(); i++) {
            xValueButtom.add(skList.get(i).dt);
            if (!TextUtils.isEmpty(skList.get(i).val) && !TextUtils.equals("--", skList.get(i).val)) {
                listData.add(Float.parseFloat(skList.get(i).val));
                listDataAc.add(Float.parseFloat(skList.get(i).val));
            } else {
                listDataAc.add(DataNull);
            }
        }
        for (int i = 0; i < ybList.size(); i++) {
            xValueTop.add(ybList.get(i).dt);
            if (!TextUtils.isEmpty(ybList.get(i).val) && !TextUtils.equals("--", ybList.get(i).val)) {
                listData.add(Float.parseFloat(ybList.get(i).val));
                listDataPre.add(Float.parseFloat(ybList.get(i).val));
            } else {
                listDataPre.add(DataNull);
            }
        }
        if (listData.size() == 0) {
//            数据为空的时候
            return;
        }

        float max = Collections.max(listData);
        float min = Collections.min(listData);
        float ySeactionTemp = 4;
        //----------从新计算最大值最小值--是否是4的倍数----
        float tempMin = min % ySeactionTemp;
        if (min > 0) {
            min = min - tempMin;
        } else if (tempMin == 0) {
            min -= ySeactionTemp;
        } else {
            min = min - tempMin - ySeactionTemp;
        }

        if (min < 0) {
            switch (cutType) {
                case RAIN:
                    min = 0;//是否要大于0温度的值是有可能小于0的。则这步不需要
                    break;
                case TEMPERATURE:
                    break;
                case VISIBILITY:
                    min = 0;//是否要大于0温度的值是有可能小于0的。则这步不需要
                    break;
                case PRESSURE:
                    min = 0;//是否要大于0温度的值是有可能小于0的。则这步不需要
                    break;
                case WIND:
                    min = 0;//是否要大于0温度的值是有可能小于0的。则这步不需要
                    break;
                case HUMIDITY:
                    min = 0;
                    break;
            }
        }

        float tempMax = max % ySeactionTemp;
        max += ySeactionTemp - tempMax;

        //----------------------------------
        float ySection = (max - min) / ySize;
        float yProportion;
        if (max == 0 && max == min) {
            yProportion = 0;//y轴总高度，与区间数据值的比例
        } else {
            yProportion = acturalYhight / (max - min);
        }
        setAllXYValue(yProportion, xSaction, listDataAc, listDataPre, min);
        setYValue(min, ySection);
    }

    /**
     * 计算柱状图的高度值，折线图的位置
     *
     * @param yProportion（实际绘图高度）
     * @param xSaction            x（区间大小）
     * @param listDataAc          实况（柱状图）列表
     * @param listDataPre         预报（折线）列表
     */
    private void setAllXYValue(float yProportion, float xSaction, List<Float> listDataAc, List<Float> listDataPre,
                               float min) {
        for (int i = 0; i < listDataAc.size(); i++) {
            Point point = new Point();
            if (DataNull == listDataAc.get(i)) {
                point.y = (int) DataNull;
                point.x = (int) (xSaction * (i * 2 + 1) + margLeft);
            } else {
                point.y = (int) (yProportion * (listDataAc.get(i) - min));
                point.x = (int) (xSaction * (i * 2 + 1) + margLeft);
            }
            rectangleAcHight.add(point);
        }
        for (int i = 0; i < listDataPre.size(); i++) {
            Point point = new Point();
            if (DataNull == listDataPre.get(i)) {
                point.y = (int) DataNull;
                point.x = (int) (xSaction * ((i + 24) * 2 + 1) + margLeft);
            } else {
                point.y = (int) (yProportion * (listDataPre.get(i) - min));
                point.x = (int) (xSaction * ((i + 24) * 2 + 1) + margLeft);
            }
            rectanglePreHight.add(point);
        }
    }


    /**
     * #############y轴值##########
     *
     * @param min
     * @param ySection
     */
    private void setYValue(float min, float ySection) {
        for (int i = 0; i <= ySize; i++) {
            DecimalFormat fnum = new DecimalFormat("##0.0");
            String dd = fnum.format(ySection * i + min);
            yValue.add(dd);
        }
        Collections.reverse(yValue);//倒序处理 （数据绘制是从上到下摆）
    }

    /**
     * 通过aqi获取图标
     *
     * @param value
     * @return
     */
    public Bitmap getClickBitmap(String value, boolean isYb, boolean isOnTop) {
        //视图
        View view = LayoutInflater.from(this.getContext()).inflate(R.layout.pop_livequery, null);
        TextView textView = (TextView) view.findViewById(R.id.tv_value);
        textView.setText(value);
        if (isYb) {
            if (isOnTop) {
                view.setBackgroundResource(R.drawable.icon_livequery_yb);
            } else {
                view.setBackgroundResource(R.drawable.icon_livequery_yb_bottom);
            }
        } else {
            if (isOnTop) {
                view.setBackgroundResource(R.drawable.icon_livequery_sk);
            } else {
                view.setBackgroundResource(R.drawable.icon_livequery_sk_bottom);
            }
        }
        if (isOnTop) {
            textView.setPadding(getResources().getDimensionPixelSize(R.dimen.dimen4)
                    , 0
                    , getResources().getDimensionPixelSize(R.dimen.dimen4)
                    , getResources().getDimensionPixelSize(R.dimen.dimen10));
        } else {
            textView.setPadding(getResources().getDimensionPixelSize(R.dimen.dimen4)
                    , getResources().getDimensionPixelSize(R.dimen.dimen10)
                    , getResources().getDimensionPixelSize(R.dimen.dimen4)
                    , 0);
        }
        view.setDrawingCacheEnabled(true);
        view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0,
                MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        return bitmap;
    }

    public class ClickBitmap {
        Point p = new Point();
        Bitmap bm = null;
        boolean isDrawTop = true;
        boolean isYbBitmap = true;
        int bitmapPosition = 0;
    }
}
