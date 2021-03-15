package com.pcs.ztqtj.view.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.amap.api.maps.MapView;
import com.amap.api.maps.SupportMapFragment;
import com.amap.api.services.core.ServiceSettings;
import com.pcs.lib.lib_pcs_v3.control.tool.BitmapUtil;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib.lib_pcs_v3.model.image.ImageFetcher;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCity;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityMain;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalUser;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackBannerDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackBannerUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackShareAboutDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackShareAboutUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackSstqDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackSstqUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.week.PackMainWeekWeatherDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.week.PackMainWeekWeatherUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.week.WeekWeatherInfo;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.command.CommandBatching;
import com.pcs.ztqtj.control.inter.InterfaceRefresh;
import com.pcs.ztqtj.control.inter.InterfaceShowBg;
import com.pcs.ztqtj.control.listener.ListenerRefreshTouch;
import com.pcs.ztqtj.control.listener.ListenerRefreshTouch.InterfacePulldownView;
import com.pcs.ztqtj.control.listener.ListenerRefreshTouch.InterfaceScrollView;
import com.pcs.ztqtj.control.listener.MainOnScrollListener;
import com.pcs.ztqtj.control.main_en_weather.CommandMainEnRow1;
import com.pcs.ztqtj.control.main_en_weather.CommandMainEnRow2;
import com.pcs.ztqtj.control.main_en_weather.CommandMainEnRow3;
import com.pcs.ztqtj.control.tool.AutoDownloadWeather;
import com.pcs.ztqtj.control.tool.ServiceLoginTool;
import com.pcs.ztqtj.control.tool.ShareToolsEn;
import com.pcs.ztqtj.control.tool.ZtqImageTool;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.view.activity.activityEn.ActivityMainEn;
import com.pcs.ztqtj.view.myview.MyScrollView;
import com.pcs.ztqtj.view.myview.ViewPulldownRefresh;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 首页天气
 *
 * @author JiangZy
 */
@SuppressLint({"InflateParams", "UseSparseArrays"})
public class FragmentHomeEnWeather extends SupportMapFragment {
    private View mView;
    //缩略图
    private ImageView mThumbView;
    //变暗背景
    private ImageView mDarkView;
    // 滚动列表
    private MyScrollView mScrollView;
    // 滚动监听
    private MyInterfaceScrollView mMyInterfaceScrollView;
    // 触摸监听
    private MyListenerRefreshTouch mListenerRefreshTouch;
    // 下拉视图
    private InterfacePulldownView mPulldownView;
    // 其他页面的刷新动画
    private InterfaceRefresh mOtherRefreshAnim;
    // 天气广播列表
    private static Map<Integer, WeatherReceiver> mListReceiver = new HashMap<Integer, WeatherReceiver>();
    private ImageFetcher mImageFetcher;
    // 上传包：实时天气
    private PackSstqUp mPackSstqUp = new PackSstqUp();
    // 数据命令
    private CommandBatching mDataCommand = null;

    private MapView mMapView;
    private String mPrevBg = "";

    private CommandMainEnRow2 mCommandMainRow2;

    private MyRecevier recevier = new MyRecevier();
    // 世界气象日活动上传包
    private PackBannerUp weatherDayUp = new PackBannerUp();

    //是否已改变城市
    private boolean isChangedCity = false;
    private LinearLayout lay_bt_setting;

    public FragmentHomeEnWeather() {

    }

    private LinearLayout lay_en_back;
    private ImageView popbg;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_home_en, null);
        mThumbView = (ImageView) mView.findViewById(R.id.image_blur);
        mDarkView = (ImageView) mView.findViewById(R.id.image_dark);
        popbg = (ImageView) mView.findViewById(R.id.popbg);
        lay_en_back = (LinearLayout) mView.findViewById(R.id.lay_en_back);
        ServiceSettings.getInstance().setLanguage(ServiceSettings.ENGLISH);
        return mView;
    }

    private int height = 80;
    private LinearLayout btnCityList;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ActivityMainEn activity = (ActivityMainEn) getActivity();
        mImageFetcher = activity.getImageFetcher();
        mOtherRefreshAnim = null;

        mScrollView = (MyScrollView) mView.findViewById(R.id.scroll_view);
        MainOnScrollListener onSrcollListener = new MainOnScrollListener(getActivity(), mThumbView, mDarkView,
                savedInstanceState);
        mScrollView.setListener(onSrcollListener);

        // 初始化行数据
        initRowData(savedInstanceState);
        refreshData(false);

        mMapView = (MapView) mScrollView.findViewById(R.id.map);

        // 下拉视图
        View pullView = mView.findViewById(R.id.layout_pulldown);
        mPulldownView = new ViewPulldownRefresh(getActivity(), pullView);
        // 滚动监听
        mMyInterfaceScrollView = new MyInterfaceScrollView(mScrollView);

        // 触摸监听
        mListenerRefreshTouch = new MyListenerRefreshTouch(getActivity()
                .getWindowManager(), mPulldownView, myRefreshView,
                mOtherRefreshAnim, mMyInterfaceScrollView);
        mScrollView.setOnTouchListener(mListenerRefreshTouch);
        btnCityList = (LinearLayout) mView.findViewById(R.id.layout_top_left);
        View btn_citylist = mView.findViewById(R.id.btn_citylist);
        lay_bt_setting = (LinearLayout) mView.findViewById(R.id.lay_bt_share);
        lay_bt_setting.setOnClickListener(mOnClick);
        btnCityList.setOnClickListener(mOnClick);
        btn_citylist.setOnClickListener(mOnClick);
        lay_en_back.setOnClickListener(mOnClick);
        text_cityname = (TextView) mView.findViewById(R.id.text_cityname);

//        View btnPhoto = mView.findViewById(R.id.btn_photo);
//        btnPhoto.setOnClickListener(mOnClick);
        // 注册广播
        //registerReceiver(getActivity(), mReceiver);
    }


    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha 屏幕透明度0.0-1.0 1表示完全不透明
     */
    public void setBackgroundAlpha(float bgAlpha) {
        popbg.setVisibility(View.GONE);
//        try {
//            WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
//            lp.alpha = bgAlpha;
//            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//            getActivity().getWindow().setAttributes(lp);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // 重新注册所有广播
        registerReceiverAgain();

        if (mMapView != null) {
            mMapView.onResume();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        // 注销所有广播
        ungisterReceiverAll();


    }

    @Override
    public void onPause() {
        super.onPause();
        if (mMapView != null) {
            mMapView.onPause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMapView != null) {
            mMapView.onDestroy();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case ServiceLoginTool.SERVICE_RESULT:
                    PackLocalUser info = ZtqCityDB.getInstance().getMyInfo();
                    if (!TextUtils.isEmpty(info.user_id)) { // 如果成功登陆，则进入气象服务首页
                    }
                    break;
            }
        }
    }

    /**
     * resume时自动刷新视图
     */
    private void refreshData(boolean isChangeCity) {
        //刷新城市名
        if (mDataCommand == null) {
            return;
        }

        if (isChangeCity) {
            mCommandMainRow2.setChangeCity();
        }
        mDataCommand.execute();

        mPrevBg = "";
    }

    private TextView text_cityname;

    private ViewGroup rootLayout;

    /**
     * 初始化行数据
     */
    private void initRowData(Bundle savedInstanceState) {
        rootLayout = (ViewGroup) mScrollView
                .findViewById(R.id.scroll_view_layout);

        mDataCommand = new CommandBatching();
        mCommandMainRow2 = new CommandMainEnRow2(getActivity(), rootLayout, mImageFetcher, mShowBg, height);
        CommandMainEnRow3 commandMainEnRow3 = new CommandMainEnRow3(getActivity(), rootLayout, mImageFetcher,
                savedInstanceState,
                mScrollView, height);
        CommandMainEnRow1 commandMainEnRow1 = new CommandMainEnRow1(mCommandMainRow2, commandMainEnRow3, getActivity
                (), rootLayout, mImageFetcher,
                mShowBg, height);
        mDataCommand.addCommand(commandMainEnRow1);

        mDataCommand.addCommand(mCommandMainRow2);
        mDataCommand.addCommand(commandMainEnRow3);
        mDataCommand.execute();
    }

    private class MyListenerRefreshTouch extends ListenerRefreshTouch {

        private boolean mScrollable = true;

        public MyListenerRefreshTouch(WindowManager windowManager,
                                      InterfacePulldownView pulldownView,
                                      InterfaceRefresh refreshView, InterfaceRefresh refreshAnim,
                                      InterfaceScrollView scrollView) {
            super(windowManager, pulldownView, refreshView, refreshAnim,
                    scrollView);
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            boolean b = super.onTouch(v, event);
            if (mScrollable) {
                return b;
            }

            return true;
        }

        public void setScrollable(boolean b) {
            mScrollable = b;
        }
    }

    private class MyInterfaceScrollView implements InterfaceScrollView {

        private ScrollView mScrollView;

        public MyInterfaceScrollView(ScrollView scrollView) {
            mScrollView = scrollView;
        }

        @Override
        public boolean isScrollTop() {
            return mScrollView.getScrollY() == 0;

        }

        @Override
        public void setScrollable(boolean b) {
            mListenerRefreshTouch.setScrollable(b);
        }

    }

    /**
     * 注册广播
     *
     * @param context
     * @param receiver
     */
    public static void registerReceiver(Context context,
                                        PcsDataBrocastReceiver receiver) {
        if (mListReceiver.containsKey(receiver.hashCode())) {
            return;
        }
        PcsDataBrocastReceiver.registerReceiver(context, receiver);
        // 加入列表
        WeatherReceiver wReceiver = new WeatherReceiver();
        wReceiver.isRegistered = true;
        wReceiver.context = context;
        wReceiver.receiver = receiver;
        mListReceiver.put(receiver.hashCode(), wReceiver);
    }

    /**
     * 重新注册所有广播
     */
    public void registerReceiverAgain() {
        for (Integer key : mListReceiver.keySet()) {
            WeatherReceiver wReceiver = mListReceiver.get(key);
            if (wReceiver.isRegistered) {
                continue;
            }

            PcsDataBrocastReceiver.registerReceiver(wReceiver.context,
                    wReceiver.receiver);
            wReceiver.isRegistered = true;
        }

        if (recevier == null) {
            recevier = new MyRecevier();
        }
        PcsDataBrocastReceiver.registerReceiver(getActivity(), recevier);
    }

    /**
     * 注销所有广播
     */
    public void ungisterReceiverAll() {
        for (Integer key : mListReceiver.keySet()) {
            WeatherReceiver wReceiver = mListReceiver.get(key);
            if (!wReceiver.isRegistered) {
                continue;
            }

            PcsDataBrocastReceiver.unregisterReceiver(wReceiver.context, wReceiver.receiver);
            wReceiver.isRegistered = false;
        }
        if (recevier != null) {
            PcsDataBrocastReceiver.unregisterReceiver(getActivity(), recevier);
            recevier = null;
        }
    }

    /**
     * 天气广播
     *
     * @author JiangZy
     */
    public static class WeatherReceiver {
        public boolean isRegistered = false;
        public Context context;
        public PcsDataBrocastReceiver receiver = null;
    }

    public static class HomeRefreshParam implements InterfaceRefresh.RefreshParam {
        public boolean isChangedCity = false;
    }

    /**
     * 刷新视图
     */
    // TODO: 2017/2/15
    public InterfaceRefresh myRefreshView = new InterfaceRefresh() {
        @Override
        public void refresh(RefreshParam param) {
            if (param != null) {
                HomeRefreshParam homeParam = (HomeRefreshParam) param;
                isChangedCity = homeParam.isChangedCity;
            }

            PackLocalCityMain cityMain = ZtqCityDB.getInstance().getCityMain();
            AutoDownloadWeather.getInstance().setDefaultCity(cityMain);
            AutoDownloadWeather.getInstance().beginMainData();

            // 刷新世界气象日活动广告
            weatherDayUp = new PackBannerUp();
            weatherDayUp.position_id = "27";
            PcsDataDownload.addDownload(weatherDayUp);
        }
    };


    private void refresh() {
//        openPop();
        //刷新数据
        refreshData(isChangedCity);
        if (isChangedCity) {
            isChangedCity = false;
        }
        // 触摸重置
        if (mListenerRefreshTouch != null) {
            mListenerRefreshTouch.reset();
        }
        PackLocalCityMain cityMain = ZtqCityDB.getInstance().getCityMain();
        if (cityMain != null) {
            mPackSstqUp.area = cityMain.ID;
            PackSstqDown pack = (PackSstqDown) PcsDataManager.getInstance().getNetPack(mPackSstqUp.getName());
            if (pack == null || pack.upt_time == null || "".equals(pack.upt_time)) {
            } else {
                text_cityname.setText(pack.us_area);
                if (mListenerRefreshTouch != null) {
                    mListenerRefreshTouch.setRefreshTime(Long.valueOf(pack.upt_time));
                }
            }
        }
    }

    private InterfaceShowBg mShowBg = new InterfaceShowBg() {
        @Override
        public void showBg(String bgPath, String thumbPath) {
            if (mPrevBg.equals(bgPath)) {
                return;
            }
            BitmapDrawable bitmapBg = mImageFetcher.getImageCache().getBitmapFromAssets(bgPath);
            if (bitmapBg == null) {
                return;
            }
            BitmapDrawable bitmapThumb = mImageFetcher.getImageCache().getBitmapFromAssets(thumbPath);
            mView.setBackgroundDrawable(bitmapBg);
            mThumbView.setBackgroundDrawable(bitmapThumb);

            mPrevBg = bgPath;
        }
    };

    private View.OnClickListener mOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.lay_bt_share:
                    //点击设置
                    reqShare();
                    break;
                case R.id.lay_en_back:
                    ServiceSettings.getInstance().setLanguage(ServiceSettings.CHINESE);
                    getActivity().finish();
                    break;
                default:
                    break;
            }
        }
    };
    private PackShareAboutUp packShareAboutUp = new PackShareAboutUp();

    private void reqShare() {
        packShareAboutUp.keyword = "ABOUT_QXCP_DXFW_US";
        PcsDataDownload.addDownload(packShareAboutUp);
    }

    //上传包：一周天气
    private PackMainWeekWeatherUp mPackWeekUp = new PackMainWeekWeatherUp();

    /**
     * 取一周天气列表
     *
     * @return
     */
    private List<WeekWeatherInfo> getWeatherList() {
        // 当前城市
        PackLocalCity packCity = ZtqCityDB.getInstance().getCityMain();
        if (packCity == null || packCity.ID == null) {
            return new ArrayList<WeekWeatherInfo>();
        }
        mPackWeekUp.setCity(packCity);
        PackMainWeekWeatherDown packMainWeekDown = (PackMainWeekWeatherDown) PcsDataManager.getInstance().getNetPack
                (mPackWeekUp.getName());

        return packMainWeekDown.getWeek();
    }

    //点击分享
    private void clickShare(String content) {
//        PackLocalCity packCity = ZtqCityDB.getInstance().getCityMain();
        List<WeekWeatherInfo> weatherList = getWeatherList();
//        if (packCity == null || TextUtils.isEmpty(packCity.ID) || weatherList == null) {
//            return;
//        }
        StringBuffer shareStr = new StringBuffer(text_cityname.getText().toString() + ":");
        if (weatherList.size() > 1) {
            shareStr.append(weatherList.get(1).us_gdt + ",");
            shareStr.append(weatherList.get(1).us_weather + ",");
            shareStr.append(weatherList.get(1).higt + "~");
            shareStr.append(weatherList.get(1).lowt + "°C,");
        }
        if (weatherList.size() > 2) {
            shareStr.append(weatherList.get(2).us_gdt + ",");
            shareStr.append(weatherList.get(2).us_weather + ",");
            shareStr.append(weatherList.get(2).higt + "~");
            shareStr.append(weatherList.get(2).lowt + "°C,");
        }
        if (weatherList.size() > 3) {
            shareStr.append(weatherList.get(3).us_gdt + ",");
            shareStr.append(weatherList.get(3).us_weather + ",");
            shareStr.append(weatherList.get(3).higt + "~");
            shareStr.append(weatherList.get(3).lowt + "°C。");
        }
        if (weatherList.size() > 4) {
            shareStr.append(weatherList.get(4).us_gdt + ",");
            shareStr.append(weatherList.get(4).us_weather + ",");
            shareStr.append(weatherList.get(4).higt + "~");
            shareStr.append(weatherList.get(4).lowt + "°C,");
        }
        if (weatherList.size() > 5) {
            shareStr.append(weatherList.get(5).us_gdt + ",");
            shareStr.append(weatherList.get(5).us_weather + ",");
            shareStr.append(weatherList.get(5).higt + "~");
            shareStr.append(weatherList.get(5).lowt + "°C,");
        }
        if (weatherList.size() > 6) {
            shareStr.append(weatherList.get(6).us_gdt + ",");
            shareStr.append(weatherList.get(6).us_weather + ",");
            shareStr.append(weatherList.get(6).higt + "~");
            shareStr.append(weatherList.get(6).lowt + "°C。");
        }
        if (weatherList.size() >= 7) {
            shareStr.append(weatherList.get(7).us_gdt + ",");
            shareStr.append(weatherList.get(7).us_weather + ",");
            shareStr.append(weatherList.get(7).higt + "~");
            shareStr.append(weatherList.get(7).lowt + "°C。");
        }

        Bitmap bitmap = BitmapUtil.takeScreenShot(getActivity());
        //ShareUtil.share(mActivity, shareStr.toString() + mActivity.getResources().getString(R.string.share_add),
        // bitmap);
        bitmap = ZtqImageTool.getInstance().stitchQR(getActivity(), bitmap);
//        PackShareAboutDown shareDown = (PackShareAboutDown) PcsDataManager.getInstance().getNetPack(PackShareAboutUp
//                .getNameCom());

        String shareContnet = "";
        if (content != null) {
            shareContnet = shareStr + content;
        }
        ShareToolsEn.getInstance(getActivity()).setShareContent("Share", shareContnet, bitmap, "0").showWindow
                (rootLayout);
    }


    private class MyRecevier extends PcsDataBrocastReceiver {
        @Override
        public void onReceive(String nameStr, String errorStr) {
            if (!TextUtils.isEmpty(errorStr)) {
                return;
            }

            refresh();

            if (nameStr.equals(weatherDayUp.getName())) {
                PackBannerDown down = (PackBannerDown) PcsDataManager.getInstance().getNetPack(nameStr);
                if (down == null) {
                    return;
                }

            } else if (nameStr.equals(packShareAboutUp.getName())) {
                PackShareAboutDown packShareAboutDown = (PackShareAboutDown) PcsDataManager.getInstance().getNetPack
                        (nameStr);
                clickShare(packShareAboutDown.share_content);
            }
        }
    }
}
