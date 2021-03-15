package com.pcs.ztqtj.view.myview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 继承ImageView 实现了多点触碰的拖动和缩放
 *
 * @author Administrator
 */

public class ImageTouchView extends View {
    public static enum StartPostion {
        ImageLeft, ImageCenter, ImageRight, ImageTJ
    }

    private StartPostion deFaultSP = StartPostion.ImageCenter;
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
    private boolean reinvalidate = false;

    public ImageTouchView(Context context) {
        super(context);
        this.setPadding(0, 0, 0, 0);
    }

    public ImageTouchView(Context context, AttributeSet a) {
        super(context, a);
        this.setPadding(0, 0, 0, 0);
    }

    public ImageTouchView(Context c, AttributeSet a, int d) {
        super(c, a, d);
        this.setPadding(0, 0, 0, 0);
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
        if (touchEvent != null) {
            touchEvent.touchEvent(event);
        }
        return true;
    }

    /**
     * 触摸事件
     */
    private TouchViewLisetner touchEvent;

    /**
     * 触摸事件
     */
    public interface TouchViewLisetner {
        /**
         * 触摸事件的event
         */
        public void touchEvent(MotionEvent event);
    }

    ;

    /**
     * 设置触摸事件
     */
    public void setTouchListener(TouchViewLisetner l) {
        this.touchEvent = l;
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private PointF midPoint(MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        return new PointF(x / 2, y / 2);
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
            if (top < getTop()) {
                disY = getTop() - top;
            } else if (bottom > getBottom()) {
                disY = getBottom() - bottom;
            }
        } else {
            if (top > getTop()) {
                disY = getTop() - top;
            } else if (bottom < getBottom()) {
                disY = getBottom() - bottom;
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

    /**
     * 设置对齐方向
     *
     * @param sp
     */
    public void setImagePositon(StartPostion sp) {
        if (sp == null) {
        } else {
            deFaultSP = sp;
        }
    }

    /**
     * 设置显示图片
     *
     * @param bm    图片
     */
    public void setMyImageBitmap(Bitmap bm) {
        reDefaultValue();
        if (bm == null) {
        } else {
            reinvalidate = true;
            this.mBitmap = bm;
        }
        invalidate();
    }

    private boolean hightFillScale = false;

    public void setHightFillScale(boolean fillScale) {
        hightFillScale = fillScale;
    }

    private void setBitmap(Bitmap bm) {
        float mScale;
        if (hightFillScale) {
            mScale = (float) getHeight() / (float) bm.getHeight();
        } else {
            mScale = (float) getWidth() / (float) bm.getWidth();
        }
        this.mScale = mScale;
        if (deFaultSP == StartPostion.ImageLeft) {
            imageStartLeft(bm, mScale);
        } else if (deFaultSP == StartPostion.ImageCenter) {
            imageStartCenter(bm, mScale);
        } else if (deFaultSP == StartPostion.ImageRight) {
            imageStartRight(bm, mScale);
        } else if (deFaultSP == StartPostion.ImageTJ) {
            imageStartTJ(bm, mScale);
        }
    }

    /**
     * 改变图片，按放大后的比例显示图片
     *
     * @param bm
     */
    public void changeImageBitmap(Bitmap bm) {
        if (bm == null) {
            return;
        }

        float mScale = (float) getHeight() / (float) bm.getHeight();
        this.mBitmap = bm;
        this.mScale = mScale;
        checkOutScreen();
        invalidate();
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
     * 左边对齐
     *
     * @param bm
     * @param mScale
     */
    private void imageStartLeft(Bitmap bm, float mScale) {
        mLeft = 0;
        mTop = 0;
        mRight = mLeft + bm.getWidth() * mScale;
        mBottom = mTop + bm.getHeight() * mScale;
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
     * 右对齐
     *
     * @param bm
     * @param mScale
     */
    private void imageStartRight(Bitmap bm, float mScale) {
        mLeft = (getWidth() - bm.getWidth() * mScale);
        mTop = 0;
        mRight = mLeft + bm.getWidth() * mScale;
        mBottom = mTop + bm.getHeight() * mScale;
    }

    private void imageStartTJ(Bitmap bm, float mScale) {
        mLeft = (getWidth() - bm.getWidth() * mScale) + 200;
        mTop = 0;
        mRight = mLeft + bm.getWidth() * mScale;
        mBottom = mTop + bm.getHeight() * mScale;
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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (reinvalidate) {
            setBitmap(mBitmap);
            reinvalidate = false;
        }

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

}