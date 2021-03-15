package com.pcs.ztqtj.control.listener;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.view.myview.MyScrollView;


/**
 * 首页滚动监听（动态模糊、变暗）
 *
 * @author JiangZY
 */
public class MainOnScrollListener implements MyScrollView.MyOnScrollListener {
    private int mChangeHeight = 0;
    private View mBlurView;
    private View mDarkView;
    private Context mContext;

    //
    private int mScrollY = 0;
    // 开始的透明度：模糊
    private float mBeginAlphaBlur = 0f;
    // 开始的透明度：变暗
    private float mBeginAlphaDark = 0f;

    /**
     * 模糊的图层
     *
     * @param blurView
     */
    public MainOnScrollListener(Context context, View blurView, View darkView, Bundle savedInstanceState) {
        mContext = context;
        mBlurView = blurView;
        mDarkView = darkView;
        mBeginAlphaBlur = blurView.getAlpha();
        mBeginAlphaDark = darkView.getAlpha();

        if (savedInstanceState == null) {
            return;
        }
        mScrollY = savedInstanceState.getInt("mScrollY", 0);
        if (mScrollY > 0) {
            // 滚动模糊
            scrollBlur(mScrollY);
        }
    }

    /**
     * 滚动模糊
     *
     * @param scrollY
     */
    private void scrollBlur(int scrollY) {
        if (mChangeHeight == 0) {
            mChangeHeight = mContext.getResources().getDimensionPixelSize(
                    R.dimen.main_change_height);
        }

        float alphaBlur = ((float) scrollY) / ((float) mChangeHeight);
        if (alphaBlur > 1f) {
            alphaBlur = 1f;
        }
        float alphaDark = alphaBlur;
        alphaBlur += mBeginAlphaBlur;
        alphaDark += mBeginAlphaDark;
        if (alphaBlur > 1f) {
            alphaBlur = 1f;
        }
        if (alphaDark > 1f) {
            alphaDark = 1f;
        }
        mBlurView.setAlpha(alphaBlur);
        mDarkView.setAlpha(alphaDark);
    }

    @Override
    public void onScrollChanged(int l, int t, int oldl, int oldt) {
        mScrollY = t;
        scrollBlur(t);
    }
}
