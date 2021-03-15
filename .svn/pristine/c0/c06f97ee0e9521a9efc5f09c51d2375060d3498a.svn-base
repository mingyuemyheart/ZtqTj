package com.pcs.ztqtj.view.myview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;

import com.pcs.ztqtj.R;
import com.pcs.lib.lib_pcs_v3.control.tool.Util;
import com.pcs.lib_ztqfj_v2.model.pack.net.dataquery.ElementQueryYear;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tyaathome on 2017/11/6.
 */

public class ElementWeatherView extends View {

    private Context mContext;
    private GestureDetector mDetector;
    private boolean isInited = false;
    private List<ElementQueryYear> mElementDataList = new ArrayList<>();
    // 最大值最小值
    private int mMaxValue = 0, mMinValue = 0;
    // 总数
    private int mCount = 0;
    private final int mLineCount = 6;
    // 柱状图间隔
    private int mBarMargin;
    // 柱状图单条宽度
    private int mBarWidth;
    private float mLeftMargin;
    private int mHistogramWidth;
    private final int mGap = 15;
    private Paint mTextPaint, mBarPaint, mDottedLinePaint, mFullLinePaint, mSelectedBarPaint, mNoDataTextPaint;
    private int mMargin;
    // 年份xy起始坐标
    private float mYearTextStartY;
    // 所有柱状图范围
    private List<RectF> mBarBoundList = new ArrayList<>();
    private List<PointF> mYearBoundList = new ArrayList<>();
    private List<PointF> mDottedLineBoundList = new ArrayList<>();
    private List<Integer> mValueList = new ArrayList<>();

    protected Rect srcRect;
    protected RectF dstRect;
    private RectF clipRect;
    private Bitmap mBitmap;
    // 已选中的柱状图位置
    private int mCurrentSelectedBarPosition = 0;
    private OnBarItemClickListener listener;
    private OnBarScrollListener scrollListener;
    private float lastX, lastY;
    private float noDataTextX, noDataTextY;
    private final String strNoData = "数据=0";

    public ElementWeatherView(Context context) {
        super(context);
        init(context);
    }

    public ElementWeatherView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ElementWeatherView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /**
     * 初始化
     *
     * @param context
     */
    private void init(Context context) {
        mContext = context;
        mDetector = new GestureDetector(context, new MyGestureListener());
        mMargin = Util.dip2px(context, 10);
        mBarMargin = Util.dip2px(context, 10);
        mBarWidth = Util.dip2px(context, 20);
        initPaint();
    }

    private void initPaint() {
        // 初始化文字画笔
        mTextPaint = new Paint();
        mTextPaint.setTextSize(Util.dip2px(mContext, 10));
        mTextPaint.setColor(mContext.getResources().getColor(R.color.text_black_common));
        mTextPaint.setAntiAlias(true);

        // 柱状图画笔
        mBarPaint = new Paint();
        mBarPaint.setColor(mContext.getResources().getColor(R.color.grey2));
        mBarPaint.setStyle(Paint.Style.FILL);
        mBarPaint.setStrokeWidth(0);
        mBarPaint.setAntiAlias(true);

        // 初始化虚线画笔
        mDottedLinePaint = new Paint();
        mDottedLinePaint.setStyle(Paint.Style.STROKE);
        mDottedLinePaint.setColor(mContext.getResources().getColor(R.color.text_black));
        Path path = new Path();
        path.addCircle(0, 0, mGap, Path.Direction.CW);
        mDottedLinePaint.setPathEffect(new DashPathEffect(new float[]{mGap, mGap}, 0));
        mDottedLinePaint.setAntiAlias(true);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        // 初始化实线画笔
        mFullLinePaint = new Paint();
        mFullLinePaint.setStyle(Paint.Style.STROKE);
        mFullLinePaint.setColor(mContext.getResources().getColor(R.color.text_black));
        mFullLinePaint.setAntiAlias(true);

        mSelectedBarPaint = new Paint(mBarPaint);
        mSelectedBarPaint.setColor(mContext.getResources().getColor(R.color.mblue));

        mNoDataTextPaint = new Paint(mTextPaint);
        int _15dp = Util.dip2px(mContext, 15);
        mNoDataTextPaint.setTextSize(_15dp);
        mNoDataTextPaint.setFakeBoldText(true);
    }

    public void setOnItemClickListener(OnBarItemClickListener listener) {
        this.listener = listener;
    }

    public void setOnScrollListener(OnBarScrollListener listener) {
        this.scrollListener = listener;
    }

    public void setData(List<ElementQueryYear> list) {
        if (list == null) {
            return;
        }
        mCurrentSelectedBarPosition = 0;
        mElementDataList = list;
        mCount = mElementDataList.size();
        float max = 0;
        for (int i = 0, count = list.size(); i < count; i++) {
            ElementQueryYear bean = list.get(i);
            float value = Float.valueOf(bean.num);
            max = max < value ? value : max;
        }
        mMaxValue = (int) (Math.ceil(max / 5.0f) * 5);
        mLeftMargin = Util.getTextWidth(mTextPaint, String.valueOf(mMaxValue)) + mMargin;
        mValueList = new ArrayList<>();
        if(mMaxValue == 0) {
            for(int i = (mLineCount - 1)*5; i >= 0; i-=5) {
                mValueList.add(i);
            }
        } else {
            int value = (mMaxValue - mMinValue) / 5;
            for (int i = mLineCount - 1; i >= 0; i--) {
                mValueList.add(mMinValue + value * i);
            }
        }
        clipRect = new RectF(mLeftMargin, 0, getMeasuredWidth(), getMeasuredHeight());
        // 柱状图宽度 = (柱状图间隔 + 柱状图单条宽度) * 数量 + 最后一个间隔
        mHistogramWidth = (mBarMargin + mBarWidth) * mCount + mBarMargin;
        if(mHistogramWidth < getMeasuredWidth()-mLeftMargin) {
            mHistogramWidth = (int) (getMeasuredWidth()-mLeftMargin);
        }
        srcRect = new Rect(0, 0, mHistogramWidth, getHeight());
        dstRect = new RectF(mLeftMargin, 0, mHistogramWidth + mLeftMargin, getHeight());
        updateData();
        invalidate();
    }

    private void updateData() {
        // 初始化文字坐标数据
        initHistogramData();
        mBitmap = getHistogram();
        if (!isInited) {
            isInited = true;
        }
    }

    private void initHistogramData() {
        if (mCount == 0) {
            mCount = mElementDataList.size();
        }
//        if (mCount == 0) {
//            return;
//        }
        mYearBoundList = new ArrayList<>();
        mYearTextStartY = getMeasuredHeight() - Util.getTextHeight(mTextPaint);
        // 初始化年份文字位置
        for (int i = 0; i < mCount; i++) {
            // 柱状图中心点
            float barCenterX = mBarMargin + (mBarWidth / 2.0f) + (mBarMargin + mBarWidth) * i;
            String str = mElementDataList.get(i).year;
            float textWidth = Util.getTextWidth(mTextPaint, str);
            float textStartX = barCenterX - textWidth / 2.0f;
            mYearBoundList.add(new PointF(textStartX, getMeasuredHeight()));

        }
        //起始高度结束高度
        float dottedLineStartY = mMargin;
        float dottedLineEndY = mYearTextStartY - mMargin;
        float dottedLineHeight = Math.abs(dottedLineEndY - dottedLineStartY) / 5.0f;
        mDottedLineBoundList = new ArrayList<>();
        for (int i = 0; i < mLineCount; i++) {
            float dottedLineY = dottedLineStartY + dottedLineHeight * i;
            mDottedLineBoundList.add(new PointF(mHistogramWidth, dottedLineY));
        }

        float chartWidth = getMeasuredWidth()-mLeftMargin;
        float chartHeight = mYearTextStartY - mMargin;
        noDataTextX = chartWidth/2.0f - Util.getTextWidth(mNoDataTextPaint, strNoData)/2.0f;
        noDataTextY = chartHeight/2.0f + Util.getTextHeight(mNoDataTextPaint)/2.0f;
        if (mMaxValue == 0) {
            return;
        }
        mBarBoundList = new ArrayList<>();
        for (int i = 0; i < mCount; i++) {
            float precent = Float.parseFloat(mElementDataList.get(i).num) / mMaxValue;
            float barCenterX = mBarMargin + (mBarWidth / 2.0f) + (mBarMargin + mBarWidth) * i;
            float left = barCenterX - (mBarWidth / 2.0f);
            float right = barCenterX + (mBarWidth / 2.0f);
            float top = Math.abs(dottedLineEndY - dottedLineStartY) * (1 - precent) + dottedLineStartY;
            float bottom = dottedLineEndY;
            mBarBoundList.add(new RectF(left, top, right, bottom));
        }
    }

    /**
     * 获取柱状图
     *
     * @return
     */
    private Bitmap getHistogram() {
        Bitmap histogramBitmap = Bitmap.createBitmap(mHistogramWidth, getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(histogramBitmap);

        for (int i = 0; i < mLineCount; i++) {
            PointF line = mDottedLineBoundList.get(i);
            if (i == mLineCount - 1) {
                canvas.drawLine(0, line.y, line.x, line.y, mFullLinePaint);
            } else {
                canvas.drawLine(0, line.y, line.x, line.y, mDottedLinePaint);
            }
        }

        // 最右边的边线
        canvas.drawLine(mHistogramWidth - 1, 0, mHistogramWidth - 1, mYearTextStartY - mMargin, mTextPaint);

        // 画年份和柱状图
        for (int i = 0; i < mCount; i++) {
            ElementQueryYear bean = mElementDataList.get(i);
            PointF textBound = mYearBoundList.get(i);
            RectF barBound = mBarBoundList.get(i);
            canvas.drawText(bean.year, textBound.x, textBound.y, mTextPaint);
            if (i == mCurrentSelectedBarPosition) {
                canvas.drawRect(barBound, mSelectedBarPaint);
            } else {
                canvas.drawRect(barBound, mBarPaint);
            }
        }
        // 画暂无数据
        if(mElementDataList.size() == 0) {
            canvas.drawText(strNoData, noDataTextX, noDataTextY, mNoDataTextPaint);
        }
        return histogramBitmap;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isInited) {
            return;
        }

        int textHeight = Util.getTextHeight(mTextPaint);
        int maxValue;
        if(mMaxValue == 0) {
            maxValue = (mLineCount-1) * 5;
        } else {
            maxValue = mMaxValue;
        }
        float maxTextWidth = Util.getTextWidth(mTextPaint, String.valueOf(maxValue));
        for (int i = 0, count = mDottedLineBoundList.size(); i < count; i++) {
            int value = mValueList.get(i);
            float y = mDottedLineBoundList.get(i).y + textHeight / 2.0f;
            float textWidth = maxTextWidth - Util.getTextWidth(mTextPaint, String.valueOf(value));
            canvas.drawText(String.valueOf(value), textWidth, y, mTextPaint);
        }

        canvas.drawLine(mLeftMargin, 0, mLeftMargin, mYearTextStartY - mMargin, mTextPaint);

        if (mBitmap == null) {
            mBitmap = getHistogram();
        }
        if (mBitmap != null) {
            canvas.save();
            canvas.clipRect(clipRect);
            canvas.drawBitmap(mBitmap, srcRect, dstRect, new Paint());
            canvas.restore();
        }

        if(dstRect.left == getMeasuredWidth() - mHistogramWidth) {
            if(scrollListener != null) {
                scrollListener.onScrollToEnd(true);
            }
        } else {
            if(scrollListener != null) {
                scrollListener.onScrollToEnd(false);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(mElementDataList == null || mElementDataList.size() == 0) {
            return super.onTouchEvent(event);
        } else {
            ViewParent parent = getParent();
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                    if (parent != null) {
                        parent.requestDisallowInterceptTouchEvent(false);
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    // 只要横向大于竖向，就拦截掉事件。
                    float slopX = Math.abs(event.getX() - lastX);
                    float slopY = Math.abs(event.getY() - lastY);
                    if( slopX >= slopY){
                        if (parent != null) {
                            parent.requestDisallowInterceptTouchEvent(true);
                        }
                    }
                    break;
                case MotionEvent.ACTION_DOWN:
                    lastX = event.getX();
                    lastY = event.getY();
                    break;
            }
            return mDetector.onTouchEvent(event);
        }
    }

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if(dstRect == null) {
                return false;
            }
            Log.e("MyGestureListener", "onSingleTapUp");
            float value = srcRect.left - dstRect.left + e.getX();
            for (int i = 0, count = mBarBoundList.size(); i < count; i++) {
                RectF rectF = mBarBoundList.get(i);
                if (value >= rectF.left && value < rectF.right) {
                    Log.e("contains", "got it : " + i);
                    mCurrentSelectedBarPosition = i;
                    if (listener != null) {
                        listener.onItemClick(i);
                    }
                    mBitmap = getHistogram();
                    invalidate();
                    break;
                }
            }
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if(dstRect == null) {
                return false;
            }
            if (dstRect.left <= mLeftMargin && dstRect.right >= getMeasuredWidth()) {
                if ((dstRect.left - distanceX) > mLeftMargin) {
                    dstRect.left = mLeftMargin;
                    dstRect.right = mHistogramWidth + mLeftMargin;
                    invalidate();
                    return true;
                } else if (dstRect.right - distanceX < getMeasuredWidth()) {
                    dstRect.left = getMeasuredWidth() - mHistogramWidth;
                    dstRect.right = getMeasuredWidth();
                    invalidate();
                    return true;
                }
                dstRect.left -= distanceX;
                dstRect.right -= distanceX;
                Log.e("Tag", String.valueOf(dstRect.left));
                invalidate();
            } else {
                if (dstRect.left > mLeftMargin) {
                    dstRect.left = mLeftMargin;
                    dstRect.right = mHistogramWidth + mLeftMargin;
                    invalidate();
                } else if (dstRect.right < getMeasuredWidth()) {
                    dstRect.left = getMeasuredWidth() - mHistogramWidth;
                    dstRect.right = getMeasuredWidth();
                    invalidate();
                }
            }
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            Log.e("MyGestureListener", "onDown");
            return true;
        }
    }

    public interface OnBarItemClickListener {
        void onItemClick(int position);
    }

    public interface OnBarScrollListener {
        void onScrollToEnd(boolean b);
    }
}
