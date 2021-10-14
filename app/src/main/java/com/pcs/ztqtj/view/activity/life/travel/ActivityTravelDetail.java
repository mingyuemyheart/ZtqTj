package com.pcs.ztqtj.view.activity.life.travel;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;

import com.pcs.lib.lib_pcs_v3.control.tool.BitmapUtil;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.ShareTools;
import com.pcs.ztqtj.control.tool.ZtqImageTool;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;

/**
 * 生活气象-旅游气象-详情
 */
public class ActivityTravelDetail extends FragmentActivityZtqBase implements OnClickListener {

    private ViewPager pager;
    public TravelFragmentOne travelFragmentOne;
    private TravelFragmentTwo travelFragmentTwo;
    private LinearLayout pagePoints;
    private TravelFragmentPagerAdapter adapter;
    private String cityId = "";
    private String cityName = "";
    private View layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.travel_detail);
        setTitleText("旅游气象");
        createImageFetcher();
        initView();
        initEvent();
        initData();
    }

    private void initView() {
        pager = (ViewPager) findViewById(R.id.pager);
        pagePoints = (LinearLayout) findViewById(R.id.page_points);
        layout = findViewById(R.id.layout);
    }

    private void initEvent() {
        setBtnRight(R.drawable.icon_share_new, this);
        findViewById(R.id.btn_back).setOnClickListener(myOnClickListener);
        pager.setOnPageChangeListener(myOnPageChangeListener);
    }

    /**
     * 加载缓存
     */
    private void initData() {
        cityId = getIntent().getStringExtra("cityId");
        cityName = getIntent().getStringExtra("cityName");
        changPoint(0);
        adapter = new TravelFragmentPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        pager.setCurrentItem(0);
    }

    /**
     * 切换点
     *
     * @param arg0
     */
    private void changPoint(int arg0) {
        for (int i = 0; i < 2; i++) {
            pagePoints.getChildAt(i).setBackgroundResource(
                    R.drawable.point_normal);
        }
        pagePoints.getChildAt(arg0).setBackgroundResource(
                R.drawable.point_checked);
    }

    /**
     * 返回监听
     **/
    private OnClickListener myOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View arg0) {
            setResult(RESULT_OK);
            finish();
        }
    };

    private ViewPager.OnPageChangeListener myOnPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageSelected(int arg0) {
            changPoint(arg0);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    class TravelFragmentPagerAdapter extends FragmentPagerAdapter {
        public TravelFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    travelFragmentOne = new TravelFragmentOne(cityId, cityName);
                    return travelFragmentOne;
                case 1:
                    travelFragmentTwo = new TravelFragmentTwo(cityId);
                    return travelFragmentTwo;
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_right:
                try {
                    Bitmap bitmap = BitmapUtil.takeScreenShot(ActivityTravelDetail.this);
                    bitmap = ZtqImageTool.getInstance().stitchQR(ActivityTravelDetail.this, bitmap);
                    ShareTools.getInstance(ActivityTravelDetail.this).setShareContent(getTitleText(),
                            travelFragmentOne.shareC, bitmap, "0").showWindow(layout);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            default:
                break;
        }

    }

}
