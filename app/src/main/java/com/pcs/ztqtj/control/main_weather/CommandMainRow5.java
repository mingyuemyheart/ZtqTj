package com.pcs.ztqtj.control.main_weather;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.AdapterControlMainRow8;
import com.pcs.ztqtj.control.inter.ImageClick;
import com.pcs.ztqtj.view.activity.web.MyWebView;
import com.pcs.ztqtj.view.myview.LeadPoint;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib.lib_pcs_v3.model.image.ImageFetcher;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackBannerDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackBannerUp;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页-广告
 */
public class CommandMainRow5 extends CommandMainBase {

    private Activity mActivity;
    private ViewGroup mRootLayout;
    private ImageFetcher mImageFetcher;
    private View mRowView;
    private AdapterControlMainRow8 adapter;
    private ViewPager vp = null;
    private int pagerCurrentPosition = 0;
    private LeadPoint pointlayout;
    private List<String> list = new ArrayList<>();
    private PackBannerUp mPackBannerUp = new PackBannerUp();
    private PackBannerDown mPackBannerDown = null;

    private Handler brannerHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    brannerHandler.removeMessages(0);
                    if (vp.getVisibility() == View.VISIBLE) {
                        vp.setCurrentItem(pagerCurrentPosition + 1);
                        moveToNextPager();
                    }
                    break;
            }
            return false;
        }
    });

    public CommandMainRow5(Activity activity, ViewGroup rootLayout, ImageFetcher mImageFetcher) {
        mActivity = activity;
        mRootLayout = rootLayout;
        this.mImageFetcher = mImageFetcher;
    }

    @Override
    protected void init() {
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mRowView = LayoutInflater.from(mActivity).inflate(R.layout.item_home_weather_5, null);
        mRowView.setLayoutParams(params);

        // 设置高度
        View rootLayout = mRowView.findViewById(R.id.root_layout);
        float screenWidth = mActivity.getWindowManager().getDefaultDisplay().getWidth();
        ViewGroup.LayoutParams rootParams = rootLayout.getLayoutParams();
        float scale = 170f / 320f;
        rootParams.height = (int) (screenWidth * scale);
        rootLayout.setLayoutParams(rootParams);

        mRootLayout.addView(mRowView);
        vp = mRowView.findViewById(R.id.viewpager);
        pointlayout = mRowView.findViewById(R.id.pointlayout);

        setStatus(Status.SUCC);
        initData();
        initEvent();
    }

    @Override
    protected void refresh() {
        mPackBannerDown = (PackBannerDown) PcsDataManager.getInstance().getNetPack(mPackBannerUp.getName());
        if (mPackBannerDown == null) {
//            PcsDataBrocastReceiver.registerReceiver(mActivity, mReceiver);
            return;
        }
        list.clear();
        // 当数据不为空时
        for (int i = 0; i < mPackBannerDown.arrBannerInfo.size(); i++) {
            list.add(mActivity.getString(R.string.file_download_url) + mPackBannerDown.arrBannerInfo.get(i).img_path);
        }
        adapter.notifyDataSetChanged();

        pointlayout.initPoint(list.size());
        if (list.size() == 0) {
            // 如果大小为0的话则不需要计算当前位置
            pointlayout.setVisibility(View.GONE);
            vp.setVisibility(View.GONE);
            mRowView.findViewById(R.id.root_layout).setVisibility(View.GONE);
        } else {
            // 不为0则计算当前位置
            pointlayout.setVisibility(View.VISIBLE);
            vp.setVisibility(View.VISIBLE);
            mRowView.findViewById(R.id.root_layout).setVisibility(View.VISIBLE);
            pagerCurrentPosition = ((adapter.getCount() / list.size()) / 2)* list.size();
            vp.setCurrentItem(pagerCurrentPosition);
        }
        if (list.size() > 1) {
            // 如果广告小于等于一个的话这不跳转播放
            moveToNextPager();
        }
    }

    private void initEvent() {
        vp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
                pagerCurrentPosition = arg0;
                if (list.size() > 1) {
                    pointlayout.setPointSelect(pagerCurrentPosition% list.size());
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });

        vp.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (brannerHandler != null) {
                            brannerHandler.removeMessages(0);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (list != null && list.size() > 1) {
                            // 如果广告小于等于一个的话这不跳转播放
                            moveToNextPager();
                        }
                        break;
                }
                return false;
            }
        });
    }

    private void moveToNextPager() {
        brannerHandler.removeMessages(0);
        brannerHandler.sendEmptyMessageDelayed(0, 3000);
    }

    private void initData() {
        mPackBannerUp.position_id = "12";
        adapter = new AdapterControlMainRow8(list, imageClick, mImageFetcher);
        vp.setAdapter(adapter);
    }

    private ImageClick imageClick = new ImageClick() {

        @Override
        public void itemClick(Object path) {
            // 图片的点击事件 path为点击的图片地址
            int position = 0;
            for (int i = 0; i < list.size(); i++) {
                if (path.toString().equals(list.get(i))) {
                    position = i;
                    break;
                }
            }

            PackBannerDown packDown = (PackBannerDown) PcsDataManager.getInstance().getNetPack(mPackBannerUp.getName());
            if (packDown == null) {
                return;
            }
            String url = packDown.arrBannerInfo.get(position).url;
            if (TextUtils.isEmpty(url)) {
                return;
            }
            Intent intent = new Intent(mActivity, MyWebView.class);
            intent.putExtra("title", packDown.arrBannerInfo.get(position).title);
            intent.putExtra("url", url);
            mActivity.startActivity(intent);
        }
    };

}
