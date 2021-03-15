package com.pcs.ztqtj.view.myview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.pcs.ztqtj.R;
import com.pcs.lib.lib_pcs_v3.control.tool.Util;
import com.pcs.lib_ztqfj_v2.model.pack.net.dataquery.CompareInfo;
import com.pcs.lib_ztqfj_v2.model.pack.net.dataquery.CompareMonthInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 资料查询元素对比图
 * Created by tyaathome on 2018/1/11.
 */

public class ElementCompareView extends View {

    private Context mContext;
    private boolean isInited = false;
    private final String[] monthStrArray = {
            "1月", "2月", "3月", "4月",
            "5月", "6月", "7月", "8月",
            "9月", "10月", "11月", "12月"};
    // 柱状图间隔
    private int mBarMargin, mMargin;
    private float mLeftMargin;
    private final int mGap = 15;
    private final float mRadius = 10;
    private final float mCurveWidth = 3;
    private Paint mTextPaint, mBarPaint, mDottedLinePaint, mFullLinePaint, mNoDataTextPaint, mCurvePointPaint, mCurvePoint;
    private int mCount = 12;
    private final int mLineCount = 6;
    // 最大值最小值
    private int mMaxValue = 0, mMinValue = 0;
    private List<Integer> mValueList = new ArrayList<>();
    private RectF clipRect;
    // 柱状图单条宽度
    private float mBarWidth;
    // 记录月份位置列表
    private List<PointF> mMonthPositionList = new ArrayList<>();
    private List<PointF> mDottedLineBoundList = new ArrayList<>();
    private List<CompareMonthInfo> mFirstYearList = new ArrayList<>();
    private List<CompareMonthInfo> mSecondYearList = new ArrayList<>();
    // 所有柱状图范围
    private List<RectF> mBarBoundList = new ArrayList<>();
    private List<PointF> mCurvePositionList = new ArrayList<>();
    // 月份字符起始坐标
    private float mMonthStartPositionY;
    private float noDataTextX, noDataTextY;
    private String firstYear = "";
    private String secondYear = "";
    private String noDataString = "";

    public ElementCompareView(Context context) {
        super(context);
        init(context);
    }

    public ElementCompareView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ElementCompareView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
        mMargin = Util.dip2px(context, 10);
        mBarMargin = Util.dip2px(context, 10);
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

        mNoDataTextPaint = new Paint(mTextPaint);
        int _15dp = Util.dip2px(mContext, 15);
        mNoDataTextPaint.setTextSize(_15dp);
        mNoDataTextPaint.setFakeBoldText(true);

        mCurvePointPaint = new Paint();
        mBarPaint.setStyle(Paint.Style.FILL);
        mCurvePointPaint.setColor(mContext.getResources().getColor(R.color.curve_orange));
        mCurvePointPaint.setStrokeWidth(0);
        mCurvePointPaint.setAntiAlias(true);


        mCurvePoint = new Paint(mCurvePointPaint);
        mCurvePoint.setStrokeWidth(mCurveWidth);
    }

    private void reset() {

    }

    public void setData(CompareInfo firstYearData, CompareInfo secondYearData) {
//        if(firstYearData == null || firstYearData.size() == 0
//                || secondYearData == null || secondYearData.size() == 0) {
//            return;
//        }
        mFirstYearList = new ArrayList<>();
        mSecondYearList = new ArrayList<>();
        if(firstYearData != null) {
            if (firstYearData.sub_list != null && firstYearData.sub_list.size() != 0) {
                mFirstYearList.addAll(firstYearData.sub_list);
            }
            firstYear = firstYearData.year;
        }
        if(secondYearData != null) {
            if (secondYearData.sub_list != null && secondYearData.sub_list.size() != 0) {
                mSecondYearList.addAll(secondYearData.sub_list);
            }
            secondYear = secondYearData.year;
        }
        float max = 0, min = 0;
        List<CompareMonthInfo> allList = new ArrayList<>();
        if (firstYearData != null) {
            allList.addAll(firstYearData.sub_list);
        }
        if (secondYearData != null) {
            allList.addAll(secondYearData.sub_list);
        }
        for (int i = 0, count = allList.size(); i < count; i++) {
            CompareMonthInfo bean = allList.get(i);
            if(!TextUtils.isEmpty(bean.val)) {
                float value = Float.valueOf(bean.val);
                max = max < value ? value : max;
                min = min > value ? value : min;
            }
        }
        mMaxValue = (int) (Math.ceil(max / 5.0f) * 5);
        if(min < 0) {
            mMinValue = (int) (Math.floor(min / 5.0f) * 5);
        } else {
            mMinValue = 0;
        }
        int maxWidth = (int) Util.getTextWidth(mTextPaint, String.valueOf(mMaxValue));
        int minWidth = (int) Util.getTextWidth(mTextPaint, String.valueOf(mMinValue));
        int widthValue = maxWidth > minWidth ? maxWidth : minWidth;
        mLeftMargin = Util.getTextWidth(mTextPaint, String.valueOf(widthValue)) + mMargin;
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
        // 柱状图单条宽度 = 控件总宽度 - 左侧刻度值距离 - 单条柱状图间隔 * 总数 - 最后一个间隔
        mBarWidth = (getMeasuredWidth() - mLeftMargin - (mCount + 1) * mBarMargin) / mCount;
        updateData();
        invalidate();
    }

    private void updateData() {
        // 初始化文字坐标数据
        initHistogramData();
        if (!isInited) {
            isInited = true;
        }
    }

    /**
     * 初始化柱状图数据
     */
    private void initHistogramData() {
        mMonthPositionList = new ArrayList<>();
        mMonthStartPositionY = getMeasuredHeight() - Util.getTextHeight(mTextPaint);
        // 初始化月份文字位置
        for(int i = 0; i < mCount; i++) {
            // 柱状图中心点
            float barCenterX = mBarMargin + (mBarWidth / 2.0f) + (mBarMargin + mBarWidth) * i;
            float textWidth = Util.getTextWidth(mTextPaint, monthStrArray[i]);
            float textStartX = barCenterX - textWidth / 2.0f;
            mMonthPositionList.add(new PointF(textStartX + mLeftMargin, getMeasuredHeight()-3));
        }

        //起始高度结束高度
        float dottedLineStartY = mMargin;
        float dottedLineEndY = mMonthStartPositionY - mMargin;
        float dottedLineHeight = Math.abs(dottedLineEndY - dottedLineStartY) / 5.0f;
        mDottedLineBoundList = new ArrayList<>();
        for (int i = 0; i < mLineCount; i++) {
            float dottedLineY = dottedLineStartY + dottedLineHeight * i;
            mDottedLineBoundList.add(new PointF(getMeasuredWidth() - mLeftMargin, dottedLineY));
        }

        float chartWidth = getMeasuredWidth()-mLeftMargin;
        float chartHeight = mMonthStartPositionY - mMargin;

        if(dataListIsNull(mFirstYearList) && dataListIsNull(mSecondYearList)) {
            if(firstYear.equals(secondYear)) {
                noDataString = "无" + firstYear + "年数据";
            } else {
                noDataString = "无" + firstYear + "," + secondYear + "年数据";
            }
        } else if(dataListIsNull(mFirstYearList)) {
            noDataString = "无" + firstYear + "年数据";
        } else if(dataListIsNull(mSecondYearList)) {
            noDataString = "无" + secondYear + "年数据";
        } else {
            noDataString = "";
        }

        // 无数据字符位置
        noDataTextX = chartWidth/2.0f - Util.getTextWidth(mNoDataTextPaint, noDataString)/2.0f;
        noDataTextY = chartHeight/2.0f + Util.getTextHeight(mNoDataTextPaint)/2.0f;
        mBarBoundList = new ArrayList<>();
        mCurvePositionList = new ArrayList<>();
        if (mMaxValue == 0) {
            return;
        }
        for (int i = 0; i < mFirstYearList.size(); i++) {
            CompareMonthInfo info = mFirstYearList.get(i);
            if(TextUtils.isEmpty(info.val)) {
                mBarBoundList.add(null);
            } else {
                float precent = (Float.parseFloat(mFirstYearList.get(i).val) - mMinValue) / (mMaxValue - mMinValue);
                float barCenterX = mBarMargin + (mBarWidth / 2.0f) + (mBarMargin + mBarWidth) * i;
                float left = barCenterX - (mBarWidth / 2.0f) + mLeftMargin;
                float right = barCenterX + (mBarWidth / 2.0f) + mLeftMargin;
                float top = Math.abs(dottedLineEndY - dottedLineStartY) * (1 - precent) + dottedLineStartY;
                float bottom = dottedLineEndY;
                mBarBoundList.add(new RectF(left, top, right, bottom));
            }
        }

        for(int i = 0; i < mSecondYearList.size(); i++) {
            CompareMonthInfo info = mSecondYearList.get(i);
            if(TextUtils.isEmpty(info.val)) {
                mCurvePositionList.add(null);
            } else {
                float precent = (Float.parseFloat(mSecondYearList.get(i).val) - mMinValue) / (mMaxValue - mMinValue);
                float barCenterX = mBarMargin + (mBarWidth / 2.0f) + (mBarMargin + mBarWidth) * i;
                float top = Math.abs(dottedLineEndY - dottedLineStartY) * (1 - precent) + dottedLineStartY;
                mCurvePositionList.add(new PointF(barCenterX + mLeftMargin, top));
            }
        }
    }

    /**
     * 检查列表数据是否为空
     * @param list
     * @return
     */
    private boolean dataListIsNull(List<CompareMonthInfo> list) {
        for(CompareMonthInfo info : list) {
            if(!TextUtils.isEmpty(info.val)) {
                return false;
            }
        }
        return true;
    }

    private void drawHistogram(Canvas canvas) {
        for (int i = 0; i < mLineCount; i++) {
            PointF line = mDottedLineBoundList.get(i);
            if (i == mLineCount - 1) {
                canvas.drawLine(mLeftMargin, line.y, line.x + mLeftMargin, line.y, mFullLinePaint);
            } else {
                canvas.drawLine(mLeftMargin, line.y, line.x + mLeftMargin, line.y, mDottedLinePaint);
            }
        }

        // 画年份和柱状图
        for(int i = 0; i < mMonthPositionList.size(); i++) {
            PointF textBound = mMonthPositionList.get(i);
            canvas.drawText(monthStrArray[i], textBound.x, textBound.y, mTextPaint);
        }
        for (int i = 0; i < mBarBoundList.size(); i++) {
            RectF barBound = mBarBoundList.get(i);
            if(barBound != null) {
                canvas.drawRect(barBound, mBarPaint);
            }
        }
        PointF preValuePoint = null;
        for(int i = 0; i < mCurvePositionList.size(); i++) {
            PointF valuePoint = mCurvePositionList.get(i);
            if(valuePoint != null) {
                canvas.drawCircle(valuePoint.x, valuePoint.y, mRadius, mCurvePointPaint);
                if (preValuePoint != null) {
                    canvas.drawLine(preValuePoint.x, preValuePoint.y, valuePoint.x, valuePoint.y, mCurvePoint);
                }
                preValuePoint = valuePoint;
            }
        }
        // 画暂无数据
//        if(mFirstYearList.size() == 0 && mSecondYearList.size() == 0) {
//            canvas.drawText("无" + firstYear + "," + secondYear + "年数据", noDataTextX + mLeftMargin, noDataTextY, mNoDataTextPaint);
//        } else if(mFirstYearList.size() == 0) {
//            canvas.drawText("无" + firstYear + "年数据", noDataTextX + mLeftMargin, noDataTextY, mNoDataTextPaint);
//        } else if(mSecondYearList.size() == 0) {
//            canvas.drawText("无" + secondYear + "年数据", noDataTextX + mLeftMargin, noDataTextY, mNoDataTextPaint);
//        }
        if(!TextUtils.isEmpty(noDataString)) {
            canvas.drawText(noDataString, noDataTextX + mLeftMargin, noDataTextY, mNoDataTextPaint);
        }
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
        //画左侧y轴刻度
        float maxTextWidth = Util.getTextWidth(mTextPaint, String.valueOf(maxValue));
        for (int i = 0, count = mDottedLineBoundList.size(); i < count; i++) {
            int value = mValueList.get(i);
            float y = mDottedLineBoundList.get(i).y + textHeight / 2.0f;
            float textWidth = maxTextWidth - Util.getTextWidth(mTextPaint, String.valueOf(value));
            canvas.drawText(String.valueOf(value), textWidth, y, mTextPaint);
        }
        // 画左侧y轴实线
        canvas.drawLine(mLeftMargin, 0, mLeftMargin, mMonthStartPositionY - mMargin, mTextPaint);
        drawHistogram(canvas);
    }
}
