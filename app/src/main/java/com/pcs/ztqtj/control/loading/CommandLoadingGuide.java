package com.pcs.ztqtj.control.loading;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.command.AbstractCommand;
import com.pcs.ztqtj.control.tool.MyConfigure;
import com.pcs.ztqtj.view.activity.ActivityMain;
import com.pcs.ztqtj.view.myview.ViewPageControl;

import java.util.ArrayList;

/**
 * 引导页
 */
public class CommandLoadingGuide extends AbstractCommand {

    private FragmentActivity mActivity;
    private ArrayList<View> views;
    private ViewPagerAdapter mAdapter;
    private ImageView l1;
    private ImageView l2;
    private ImageView l3;
    private ImageView l4;
    private ViewPageControl pagepoint;
    private TextView btn_gotomain;

    public CommandLoadingGuide(FragmentActivity activity) {
        mActivity = activity;
    }

    @Override
    public void execute() {
        super.execute();
        l1 = new ImageView(mActivity);
        l2 = new ImageView(mActivity);
        l3 = new ImageView(mActivity);
        l4 = new ImageView(mActivity);

        l1.setScaleType(ScaleType.FIT_XY);
        l2.setScaleType(ScaleType.FIT_XY);
        l3.setScaleType(ScaleType.FIT_XY);
        l4.setScaleType(ScaleType.FIT_XY);

        btn_gotomain = (TextView) mActivity.findViewById(R.id.btn_gotomain);
        l1.setImageResource(R.drawable.bg_guide_01);
        l2.setImageResource(R.drawable.bg_guide_02);
        l3.setImageResource(R.drawable.bg_guide_03);
        l4.setImageResource(R.drawable.bg_guide_04);

        initPager();
    }

    private void initPager() {
        pagepoint = (ViewPageControl) mActivity.findViewById(R.id.pageControl);
        ViewPager viewGroup = (ViewPager) mActivity.findViewById(R.id.scrollViewGroup);
        views = new ArrayList<View>();
        views.add(l1);
        views.add(l2);
        views.add(l3);
        views.add(l4);

        mAdapter = new ViewPagerAdapter();

        viewGroup.setAdapter(mAdapter);
        pagepoint.init(views.size());
        pagepoint.generatePageControl(0);
        viewGroup.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
                pagepoint.generatePageControl(arg0);
                if (arg0 == views.size() - 1) {
                    btn_gotomain.setVisibility(View.VISIBLE);
                } else {
                    btn_gotomain.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
        btn_gotomain.setOnClickListener(mOnClick);

        // 关闭按钮
        ImageView btnClose = (ImageView) mActivity.findViewById(R.id.btn_close);
        btnClose.setOnClickListener(mOnClick);
    }

    /**
     * 适配器
     */
    public class ViewPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public Object instantiateItem(ViewGroup collection, int position) {
            collection.addView(views.get(position), 0);
            return views.get(position);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        public void destroyItem(ViewGroup view, int position, Object object) {
            view.removeView(views.get(position));
        }
    }

    private OnClickListener mOnClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            setStatus(Status.SUCC);
            Intent it = new Intent();
            Bundle bundle = mActivity.getIntent().getBundleExtra(MyConfigure.EXTRA_BUNDLE);
            if(bundle != null) {
                it.putExtra(MyConfigure.EXTRA_BUNDLE, bundle);
            }
            it.setClass(mActivity, ActivityMain.class);
            //it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            mActivity.startActivity(it);
            mActivity.finish();
        }
    };
}
