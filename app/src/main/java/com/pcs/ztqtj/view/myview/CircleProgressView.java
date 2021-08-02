package com.pcs.ztqtj.view.myview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.pcs.ztqtj.R;

/**
 * Created by Administrator on 2017/11/13 0013.
 * chen_jx
 */
public class CircleProgressView extends View {

    private static final String TAG = "CircleProgressView";

    private int mMaxProgress = 100;

    private int mProgress = 0;
    private int aqi=0;

    private final int mCircleLineStrokeWidthBottom = 5;
    private final int mCircleLineStrokeWidthAbove = 10;

    // 画圆所在的距形区域
    private final RectF mRectFBottom;
    private final RectF mRectFAbove;

    private final Paint mPaint;

    private final Context mContext;
    private int width;
    private int height;


    public CircleProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;
        mRectFBottom = new RectF();
        mRectFAbove = new RectF();
        mPaint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        width = this.getWidth();
        height = this.getHeight();

        if (width != height) {
            int min = Math.min(width, height);
            width = min;
            height = min;
        }

        mPaint.setAntiAlias(true);
//        mPaint.setColor(Color.rgb(0x3a, 0x58, 0x5f));
        if (aqi <= 50) {
            // 优
            mPaint.setColor(getResources().getColor(R.color.air_quality_1));
        } else if (aqi > 50 && aqi <= 100) {
            // 良
              mPaint.setColor(getResources().getColor(R.color.air_quality_2));
        } else if (aqi > 100 && aqi <= 150) {
            // 轻度污染
              mPaint.setColor(getResources().getColor(R.color.air_quality_3));
        } else if (aqi > 150 && aqi <= 200) {
            // 中度污染
              mPaint.setColor(getResources().getColor(R.color.air_quality_4));
        } else if (aqi > 200 && aqi <= 300) {
            // 重度污染
              mPaint.setColor(getResources().getColor(R.color.air_quality_5));
        } else if (aqi > 300) {
            // 严重污染
              mPaint.setColor(getResources().getColor(R.color.air_quality_6));
        }
        canvas.drawColor(Color.TRANSPARENT); //设置背景透明
        mPaint.setStrokeWidth(3);
        mPaint.setStyle(Paint.Style.STROKE);
        setRectFPosition(mRectFBottom, mCircleLineStrokeWidthBottom);
        setRectFPosition(mRectFAbove, mCircleLineStrokeWidthAbove);

        mPaint.setStrokeWidth(mCircleLineStrokeWidthAbove);

            canvas.drawArc(mRectFAbove, 90, ((float) mProgress / mMaxProgress) * 360, false, mPaint);

    }

    private void setRectFPosition(RectF mRectF, int mCircleLineStrokeWidth) {
        mRectF.left = mCircleLineStrokeWidth / 2;
        mRectF.top = mCircleLineStrokeWidth / 2;
        mRectF.right = width - mCircleLineStrokeWidth / 2;
        mRectF.bottom = height - mCircleLineStrokeWidth / 2;
    }

    public int getMaxProgress() {
        return mMaxProgress;
    }

    public void setMaxProgress(int maxProgress) {
        this.mMaxProgress = maxProgress;
    }

    public void setProgress(int progress,int aqi) {
        this.mProgress = progress;
        this.aqi=aqi;
        this.invalidate();
    }

    public void setProgressNotInUiThread(int progress) {
        this.mProgress = progress;
        this.postInvalidate();
    }

}