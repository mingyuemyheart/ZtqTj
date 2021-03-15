package com.pcs.ztqtj.view.myview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.DrawViewTool;
import com.pcs.ztqtj.model.pack.ItemLiveQuery;
import com.pcs.lib.lib_pcs_v3.control.tool.ScreenUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Z on 2017/4/4.
 */
public class LiveQueryCompleView extends View {
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
    private float disX;
    private float disY;
    private int whatX;
    private int whatY;
    protected Rect srcRect;
    protected RectF dstRect;
    private boolean reinvalidate = true;

    public LiveQueryCompleView(Context context) {
        super(context);
        this.setPadding(0, 0, 0, 0);
    }

    public LiveQueryCompleView(Context context, AttributeSet a) {
        super(context, a);
        this.setPadding(0, 0, 0, 0);
        initPointValue();
    }


    /**
     * 处理触碰事件
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                startX = (int) event.getX();
                startY = (int) event.getY();
                mode = DRAG;
                break;
            case MotionEvent.ACTION_UP:
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
                // 若为ZOOM模式，则多点触摸缩放
                else if (mode == ZOOM) {
                    float newDist = spacing(event);
                    float scale = newDist / oldDist;
                    if (newDist > 50f && Math.abs(scale - this.mScale) > 0.02) {
                        oldDist = newDist;
                        postScale(scale);
                        invalidate();
                    }
                }
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

    /**
     * 处理图片溢出屏幕
     */
    public void checkOutScreen() {
        float left = mLeft + whatX;
        float right = mRight + whatX;
        float top = mTop + whatY;
        float bottom = mBottom + whatY;
        float wight = right - left;
        float height = bottom - top;
        float screenW = getWidth();
        float screenH = getHeight();
        float disX = this.disX;
        float disY = this.disY;
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


    private void setBitmap(Bitmap bm) {
        float mScale;
        mScale = (float) getWidth() / (float) bm.getWidth();
        this.mScale = mScale;
        imageStartCenter(bm, mScale);
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

    public void postScale(float scale) {
        this.mScale = scale;
        float disX = (scale - 1) * (mRight - mLeft) / 2;
        float disY = (scale - 1) * (mBottom - mTop) / 2;
        mRight += disX;
        mLeft -= disX;
        mTop -= disY;
        mBottom += disY;
    }

    private List<Point> actualValue = new ArrayList<>();//实际数据
    private List<Point> predictionValue = new ArrayList<>();//预报数据
    private List<String> yValue = new ArrayList<>();
    private List<String> xValue = new ArrayList<>();

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (reinvalidate) {
            reDefaultValue();
            mBitmap = drawImage();
            setBitmap(mBitmap);
            reinvalidate = false;
        }
        //绘制图片。放大缩小等
        if (null == mBitmap || mBitmap.isRecycled()) {
            return;
        }
        float left = mLeft + whatX;
        float right = mRight + whatX;
        float top = mTop + whatY;
        float bottom = mBottom + whatY;
        if (disY != 0 || disX != 0) {
            whatX += disX;
            left += disX;
            right += disX;
            whatY += disY;
            top += disY;
            bottom += disY;
        }
        srcRect = new Rect(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
        dstRect = new RectF(left, top, right, bottom);
        canvas.drawBitmap(mBitmap, srcRect, dstRect, null);
        this.disY = 0;
        this.disX = 0;
    }

    private Bitmap drawImage() {
        //创建一个画布，在这个画笔上绘制图，然后用这张图放大缩小；
        Bitmap bitmapAltered = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvasBm = new Canvas(bitmapAltered);
        float viewXWidth, viewYHight, acturalYhight;// 绘图区域宽高（扣除写值区域）

        // 计算文字高度
        Paint.FontMetrics fontMetrics = ptText.getFontMetrics();
        float fontHeight = fontMetrics.bottom - fontMetrics.top;
        margButton = 4 * fontHeight;
        viewXWidth = getWidth() - margLeft - margRight;
        viewYHight = getHeight() - margButton;
        acturalYhight = getHeight() - margButton - margTop;//实际绘制图片的高度区域
        float xSaction = viewXWidth / stationX;//x轴一个格子宽度
//            绘制横竖坐标
        canvasBm.drawLine(margLeft, viewYHight, getWidth() - margRight + xSaction, viewYHight, ptLine);
        canvasBm.drawLine(margLeft, margTop, margLeft, viewYHight, ptLine);
        canvasBm.drawLine(margLeft + viewXWidth + xSaction, margTop, margLeft + viewXWidth + xSaction, viewYHight, ptLine);

//     设置数据  ————————————————————————
        countValue((int) acturalYhight, xSaction);

//————————————————————————————


        //&&&&&&&&&&&&&&&&&&&&&&&&&&&&  Y轴的值，和格子&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
        float actionYalue = acturalYhight / 8;
        for (int i = 0; i < 8; i++) {
            float yValueHight = actionYalue * i + margTop;
            canvasBm.drawLine(margLeft, yValueHight, getWidth() - margRight + xSaction, yValueHight, ptLine);//横坐标
        }
        for (int i = 0; i < yValue.size(); i++) {
            float yValueHight = actionYalue * i + margTop + fontHeight / 4;
            canvasBm.drawText(yValue.get(i), margLeft / 2, yValueHight, ptText);
        }
        //&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
        ptActual24.setStrokeWidth(xSaction);//设置画笔宽度
        ptPrediction24.setStrokeWidth(xSaction);//设置画笔宽度
        //     #######绘制x轴坐标值#################
        for (int i = 0; i < xValue.size(); i++) {
            Bitmap timeBmTemp = Bitmap.createBitmap((int) margButton, (int) fontHeight + 2, Bitmap.Config.ARGB_8888);
            Canvas canvasBmTemp = new Canvas(timeBmTemp);
            if (xValue.get(i).length() < 4) {
                canvasBmTemp.drawText(xValue.get(i), timeBmTemp.getWidth() / 4, fontHeight, ptText);
            } else {
                canvasBmTemp.drawText(xValue.get(i), timeBmTemp.getWidth() / 2, fontHeight, ptText);
            }
            float withXPosition = xSaction * (i * 3 + 2) + margLeft;//每一个值横坐标的位置
            canvasBm.drawBitmap(adjustPhotoRotation(timeBmTemp, 90), withXPosition, getHeight() - margButton, new Paint());
        }
//#################

//绘制图像区域
        for (int i = 0; i < actualValue.size(); i++) {
            Point p = actualValue.get(i);
            canvasBm.drawLine(p.x, viewYHight - p.y, p.x, viewYHight, ptActual24);
        }
        for (int i = 0; i < predictionValue.size(); i++) {
            Point p = predictionValue.get(i);
            canvasBm.drawLine(p.x, viewYHight - p.y, p.x, viewYHight, ptPrediction24);
        }
        return bitmapAltered;
    }

    private List<ItemLiveQuery> mItems = new ArrayList<>();

    public void setNewData(List<ItemLiveQuery> mItems) {
        this.mItems.clear();
        this.mItems.addAll(mItems);
        reinvalidate = true;
        invalidate();
    }

    private boolean startZero = true;

    public void setStartPosition(boolean startZero) {
        this.startZero = startZero;
    }

    /*计算设置的值，转换成高度*/
    private void countValue(int acturalYhight, float xSaction) {
        List<Float> listData = new ArrayList<>();
        xValue.clear();
        for (int i = 0; i < mItems.size(); i++) {
            xValue.add(mItems.get(i).value_over24_hour);
            if (!TextUtils.isEmpty(mItems.get(i).value_over24_value)) {
                listData.add(Float.parseFloat(mItems.get(i).value_over24_value));
            }
            if (!TextUtils.isEmpty(mItems.get(i).value_future24_value)) {
                listData.add(Float.parseFloat(mItems.get(i).value_future24_value));
            }
        }
        if (listData.size() == 0) {
            return;
        }
        float max = Collections.max(listData);
        float min = Collections.min(listData);
        if (max == min) {
            return;
        }
        float ySection = 0;
        if (startZero) {
            min = 0;
        } else {

        }
        ySection = (max - min) / 8;
        actualValue.clear();

        for (int i = 0; i < mItems.size(); i++) {
            Point point = new Point();
            point.y = (int) (acturalYhight / ((max - min)) * Float.parseFloat(mItems.get(i).value_over24_value));
            point.x = (int) (xSaction * (i * 3 + 2) + margLeft);
            actualValue.add(point);

            Point pointPre = new Point();
            pointPre.y = (int) (acturalYhight / ((max - min)) * Float.parseFloat(mItems.get(i).value_future24_value));
            pointPre.x = (int) (xSaction * (i * 3 + 3) + margLeft);
            predictionValue.add(pointPre);
        }

        //设置x，y轴的值
        yValue.clear();
        for (int i = 0; i < 9; i++) {
            DecimalFormat fnum = new DecimalFormat("##0.00");
            String dd = fnum.format(ySection * i);
            yValue.add(dd);
        }
        Collections.reverse(yValue);
    }

    private Paint ptLine;
    private Paint ptActual24;
    private Paint ptPrediction24;
    private Paint ptText;
    private float margLeft, margButton, margRight, margTop;
    private int stationX = 72;

    private void initPointValue() {
        margLeft = ScreenUtil.dip2px(getContext(), 25);
        margTop = ScreenUtil.dip2px(getContext(), 5);
        margRight = ScreenUtil.dip2px(getContext(), 10);
        int sizeText = ScreenUtil.dip2px(getContext(), 7);
        ptText = new Paint();
        ptText.setAntiAlias(true);
        ptText.setColor(Color.argb(255, 0, 0, 0));
        ptText.setTextSize(sizeText);
        ptText.setTypeface(Typeface.create("宋体", Typeface.NORMAL));
        ptText.setTextAlign(Paint.Align.CENTER);

        ptLine = DrawViewTool.getInstance().getMyLine(Color.argb(255, 0, 0, 0));
        ptLine.setStrokeWidth(1);
        ptPrediction24 = DrawViewTool.getInstance().getMyLine(getContext().getResources().getColor(R.color.livequery_prediction));
        ptActual24 = DrawViewTool.getInstance().getMyLine(getContext().getResources().getColor(R.color.livequery_actual));
    }


    /**
     * @param bitmap
     * @param orientationDegree 0 - 360 范围
     * @return
     */
    private Bitmap adjustPhotoRotation(Bitmap bitmap, int orientationDegree) {
        Matrix matrix = new Matrix();
        matrix.setRotate(orientationDegree, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);
        Bitmap BmTemp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return BmTemp;
    }
}
