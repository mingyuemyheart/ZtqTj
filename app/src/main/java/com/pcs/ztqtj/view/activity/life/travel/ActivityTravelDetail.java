package com.pcs.ztqtj.view.activity.life.travel;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;

import com.pcs.lib.lib_pcs_v3.control.tool.BitmapUtil;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackTravelWeatherDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackTravelWeatherUp;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.ShareTools;
import com.pcs.ztqtj.control.tool.ZtqImageTool;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;

/**
 * 旅游气象详情
 */
public class ActivityTravelDetail extends FragmentActivityZtqBase implements
        OnClickListener {
    private PackTravelWeatherDown pcsDownPack = new PackTravelWeatherDown();
    private PackTravelWeatherUp packTravelWeatherUp;
    private PcsDataBrocastReceiver receiver = new MyReceiver();
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
        // travalPointTwo = (ImageView) findViewById(R.id.traval_point_two);
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
        PcsDataBrocastReceiver.registerReceiver(this, receiver);
        cityId = getIntent().getStringExtra("cityId");
        cityName = getIntent().getStringExtra("cityName");
        changPoint(0);
        adapter = new TravelFragmentPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        RequestData();
        pager.setCurrentItem(0);
    }

    /**
     * 获取本地的旅游气象数据
     */
//	private void getTravelWeather() {
//        pcsDownPack = (PackTravelWeatherDown) PcsDataManager.getInstance().getNetPack(packTravelWeatherUp.getName());
//		if (pcsDownPack == null)
//			return;
//	}

    /**
     * 请求数据
     */
    private void RequestData() {
        showProgressDialog();
        packTravelWeatherUp = new PackTravelWeatherUp();
        packTravelWeatherUp.area = cityId;
        PcsDataDownload.addDownload(packTravelWeatherUp);


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
     * 数据更新广播接收
     *
     * @author JiangZy
     */
    private class MyReceiver extends PcsDataBrocastReceiver {

        @Override
        public void onReceive(String name, String error) {
            if (packTravelWeatherUp != null && name.equals(packTravelWeatherUp.getName())) {
                dismissProgressDialog();
                pcsDownPack = (PackTravelWeatherDown) PcsDataManager.getInstance().getNetPack(packTravelWeatherUp
                        .getName());
                if (pcsDownPack == null)
                    return;
                adapter.notifyDataSetChanged();
                travelFragmentOne.reflashUI(cityId, cityName);
            }
        }

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

    private OnPageChangeListener myOnPageChangeListener = new OnPageChangeListener() {
        @Override
        public void onPageSelected(int arg0) {
            changPoint(arg0);
            switch (arg0) {
                case 0:
                    travelFragmentOne.reflashUI(cityId, cityName);
                    break;
                case 1:
                    travelFragmentTwo.reflashUI(pcsDownPack);
                    break;
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    class TravelFragmentPagerAdapter extends FragmentPagerAdapter {
        public TravelFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    travelFragmentOne = new TravelFragmentOne(cityId, cityName, getImageFetcher());
                    return travelFragmentOne;
                case 1:
                    travelFragmentTwo = new TravelFragmentTwo(getImageFetcher());
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

    // /**
    // * 分享
    // */
    // public void share(final Activity activity) {
    // ShareUtil.share(activity);
    // }
}
