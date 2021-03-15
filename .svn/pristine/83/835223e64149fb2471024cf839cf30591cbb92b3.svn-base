package com.pcs.ztqtj.view.myview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.CommUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tyaathome on 2017/4/17.
 */

public class ColorBlocks extends View {

    private static final int MARGIN = 10;
    private List<ColorBlocksData> colorBlocksList = new ArrayList<>();
    private static final int VIEW_HEIGHT = 10;
    private Paint mTextPaint = new Paint();
    private Paint mBlockPaint = new Paint();
    private Paint mBackgroundPaint = new Paint();
    private int textColor;

    public ColorBlocks(Context context) {
        super(context);
        init(context);
    }

    public ColorBlocks(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ColorBlocks(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        initPaint(context);
        ColorBlocksData bean0 = new ColorBlocksData();
        bean0.color = Color.RED;
        bean0.value = "500";

        ColorBlocksData bean1 = new ColorBlocksData();
        bean1.color = Color.DKGRAY;
        bean1.value = "5000";
        colorBlocksList.add(bean0);
        colorBlocksList.add(bean1);
        colorBlocksList.add(bean0);
        colorBlocksList.add(bean1);
        colorBlocksList.add(bean0);
        colorBlocksList.add(bean1);
        colorBlocksList.add(bean0);
        colorBlocksList.add(bean1);
    }

    /**
     * 初始化画笔
     */
    private void initPaint(Context context) {
        textColor = context.getResources().getColor(R.color.text_black_common);
        mTextPaint = new Paint();
        mTextPaint.setColor(textColor);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(CommUtils.Dip2Px(context, 10));

        mBackgroundPaint = new Paint();
        mBackgroundPaint.setAntiAlias(true);
        mBackgroundPaint.setStrokeWidth(2);
        mBackgroundPaint.setColor(textColor);

        mBlockPaint = new Paint(mBackgroundPaint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(colorBlocksList.size() >= 2) {
            // 计算色块初始和结束位置
            float firstValueWidth = mTextPaint.measureText(colorBlocksList.get(0).value);
            float blockStartX = firstValueWidth / 2f + MARGIN;
            float blockEndX = getWidth() - blockStartX;
            float blockWidth = (blockEndX - blockStartX) / colorBlocksList.size();

            Rect bounds = new Rect();
            mTextPaint.getTextBounds("a", 0, 1, bounds);
            float blockStartY = getHeight() / 2f - (VIEW_HEIGHT + MARGIN + bounds.height())/2f;
            float blockEndY = blockStartY + VIEW_HEIGHT;

            float valueStartY = blockEndY + MARGIN + bounds.height();
            float valueStartX = MARGIN;
            float valueEndX = getWidth() - MARGIN;


            for (int i = 0; i < colorBlocksList.size(); i++) {
                ColorBlocksData bean = colorBlocksList.get(i);
                // 当前色块位置
                float currentBlockStartX = blockStartX + blockWidth * i;
                float currentBlockEndX = currentBlockStartX + blockWidth;
                // 当前区间值位置
                float currentValueStartX = currentBlockStartX - mTextPaint.measureText(bean.value)/2f;
                // 画背景
                //canvas.drawRect(blockStartX, blockStartY, blockEndX, blockEndY, mBackgroundPaint);
                mBlockPaint.setColor(bean.color);
                // 画色块
                canvas.drawRect(currentBlockStartX, blockStartY, currentBlockEndX, blockEndY, mBlockPaint);
                // 画区间值
                canvas.drawText(bean.value, currentValueStartX, valueStartY, mTextPaint);

            }
        }
    }

    /**
     * 设置数据
     * @param listdata
     */
    public void setData(List<ColorBlocksData> listdata) {
        colorBlocksList = listdata;
    }

    /**
     * 色块数据
     */
    public class ColorBlocksData {
        // 颜色
        public int color;
        // 值
        public String value = "";
    }
}
