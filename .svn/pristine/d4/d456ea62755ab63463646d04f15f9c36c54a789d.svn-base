package com.pcs.ztqtj.control.livequery;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.DrawableRes;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.view.activity.livequery.ActivityLiveQuery;
import com.pcs.ztqtj.view.fragment.livequery.FragmentDistributionMap;

/**
 * 图例控制器
 * Created by tyaathome on 2017/6/3.
 */

public class ControlDistributionLegend extends ControlDistributionBase {

    FragmentDistributionMap mFragment;
    private Context mContext;
    private ActivityLiveQuery mActivity;
    private ViewGroup mRootLayout;
    private View mTyphoonLegendLayout;

    private CheckBox cbLegend;
    // 图例控件
    private View rainLegend;
    // 图例图片
    private ImageView ivRainLegend;
    //  图例是否显示
    private boolean isShowingLegend = false;
    public ControlDistributionLegend(FragmentDistributionMap fragment, ActivityLiveQuery activity, ViewGroup rootLayout) {
        mFragment = fragment;
        mActivity = activity;
        mContext = activity;
        mRootLayout = rootLayout;
    }

    @Override
    public void init() {
        mTyphoonLegendLayout = mRootLayout.findViewById(R.id.layout_typhoon_legend);
        cbLegend = (CheckBox) mRootLayout.findViewById(R.id.cb_tl);
        cbLegend.setOnCheckedChangeListener(cbLegendListener);
        rainLegend = mRootLayout.findViewById(R.id.layout_legend);
        ivRainLegend = (ImageView) mRootLayout.findViewById(R.id.iv_legend);
        ivRainLegend.setImageResource(getLegendImage());
        View btnClose = rainLegend.findViewById(R.id.btn_legend_close);
        btnClose.setOnClickListener(closeLegendListener);
        mHandler.sendEmptyMessageDelayed(0, 1000);
    }

    @Override
    public void updateView(ControlDistribution.ColumnCategory column) {
        show();
    }

    @Override
    public void clear() {
        if(cbLegend.isChecked()) {
            cbLegend.setChecked(false);
        } else {
            ivRainLegend.setImageResource(getLegendImage());
        }
    }

    @Override
    public void destroy() {

    }

    // 初始化图例控件
    private void initLegend() {
        //rainLegend = new RainLegend(this);
        //rainLegend = LayoutInflater.from(mContext).inflate(R.layout.layout_legend, null);

        //rainLegend.setBackgroundColor(getResources().getColor(android.R.color.white));
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT);
        //mRootLayout.addView(rainLegend, params);

//        float start = mRootLayout.getHeight();
//        ObjectAnimator animator = ObjectAnimator.ofFloat(rainLegend, "translationY", start, start);
//        animator.start();
    }

    /**
     * 显示图例
     */
    private void showLegend() {
        // 图例位置
        int[] locations = new int[2];
        rainLegend.getLocationOnScreen(locations);
        // 主界面位置
        int[] layoutLocations = new int[2];
        mRootLayout.getLocationOnScreen(layoutLocations);
//        float start = locations[1] - layoutLocations[1];
//        float end = mRootLayout.getHeight() - rainLegend.getHeight();
        float start = rainLegend.getHeight() - (mRootLayout.getHeight() - (locations[1] - layoutLocations[1]));
        if(rainLegend.getVisibility() == View.INVISIBLE) {
            start = rainLegend.getHeight();
        }
        float end = 0;
        rainLegend.setVisibility(View.VISIBLE);
        ObjectAnimator showAnimator = ObjectAnimator.ofFloat(mTyphoonLegendLayout, "translationY", start, end);
        showAnimator.setDuration(300);
        showAnimator.setInterpolator(new LinearInterpolator());
        showAnimator.start();
    }

    /**
     * 隐藏图例
     */
    private void hideLegend() {
        // 图例位置
        int[] locations = new int[2];
        rainLegend.getLocationOnScreen(locations);
        // 主界面位置
        int[] layoutLocations = new int[2];
        mRootLayout.getLocationOnScreen(layoutLocations);
//        float start = locations[1] - layoutLocations[1];
//        float end = mRootLayout.getHeight();
        float start = rainLegend.getHeight() - (mRootLayout.getHeight() - (locations[1] - layoutLocations[1]));
        float end = rainLegend.getHeight();
        ObjectAnimator hideAnimator = ObjectAnimator.ofFloat(mTyphoonLegendLayout, "translationY", start, end);
        hideAnimator.setDuration(300);
        hideAnimator.setInterpolator(new LinearInterpolator());
        hideAnimator.start();
    }

    private void hideLegendAndUpdateImage(@DrawableRes final int resid) {
        // 图例位置
        int[] locations = new int[2];
        rainLegend.getLocationOnScreen(locations);
        // 主界面位置
        int[] layoutLocations = new int[2];
        mRootLayout.getLocationOnScreen(layoutLocations);
//        float start = locations[1] - layoutLocations[1];
//        float end = mRootLayout.getHeight();
        float start = rainLegend.getHeight() - (mRootLayout.getHeight() - (locations[1] - layoutLocations[1]));
        float end = rainLegend.getHeight();
        ObjectAnimator hideAnimator = ObjectAnimator.ofFloat(mTyphoonLegendLayout, "translationY", start, end);
        hideAnimator.setDuration(300);
        hideAnimator.setInterpolator(new LinearInterpolator());
        hideAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                ivRainLegend.setImageResource(resid);
            }
        });
        hideAnimator.start();
    }

    /**
     * 设置图例图片
     */
    private @DrawableRes int getLegendImage() {
        ControlDistribution.ColumnCategory column = mFragment.getCurrentColumn();
        int resid = 0;
        if (rainLegend != null) {
            switch (column) {
                case RAIN: // 雨量
                    resid = R.drawable.legend_rain_new;
                    break;
                case TEMPERATURE: // 气温
                    resid = R.drawable.legend_temp;
                    break;
                case WIND: // 风速
                    resid = R.drawable.legend_wind;
                    break;
                case VISIBILITY: // 能见度
                    resid = R.drawable.legend_visibility;
                    break;
                case PRESSURE: // 气压
                    resid = R.drawable.legend_pressure;
                    break;
                case HUMIDITY:
                    resid = R.drawable.legend_humidity;
                    break;
            }
        }
        return resid;
    }

    public void show() {
        if(cbLegend != null && !cbLegend.isChecked()) {
            cbLegend.setChecked(true);
        }
    }

    /**
     * 图例回调
     */
    private CompoundButton.OnCheckedChangeListener cbLegendListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                showLegend();
            } else {
                if(isShowingLegend) {
                    // 设置图例图片
                    hideLegendAndUpdateImage(getLegendImage());
                } else {
                    hideLegend();
                }
            }
            isShowingLegend = isChecked;
        }
    };

    private Handler mHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            show();
        }
    };

    View.OnClickListener closeLegendListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            cbLegend.setChecked(false);
        }
    };
}
