package com.pcs.ztqtj.view.fragment;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.amap.api.maps.SupportMapFragment;
import com.amap.api.maps.TextureMapView;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.image.ImageFetcher;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCity;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityLocation;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityMain;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.command.CommandBatching;
import com.pcs.ztqtj.control.inter.InterfaceRefresh;
import com.pcs.ztqtj.control.inter.InterfaceShowBg;
import com.pcs.ztqtj.control.listener.ListenerRefreshTouch;
import com.pcs.ztqtj.control.listener.ListenerRefreshTouch.InterfacePulldownView;
import com.pcs.ztqtj.control.listener.ListenerRefreshTouch.InterfaceScrollView;
import com.pcs.ztqtj.control.main_weather.CommandMain24Hours;
import com.pcs.ztqtj.control.main_weather.CommandMain7DaysWeather;
import com.pcs.ztqtj.control.main_weather.CommandMainRow0;
import com.pcs.ztqtj.control.main_weather.CommandMainRow1;
import com.pcs.ztqtj.control.main_weather.CommandMainRow3;
import com.pcs.ztqtj.control.main_weather.CommandMainRow4;
import com.pcs.ztqtj.control.tool.AutoDownloadWeather;
import com.pcs.ztqtj.control.tool.MyConfigure;
import com.pcs.ztqtj.control.tool.PermissionsTools;
import com.pcs.ztqtj.control.tool.ZtqLocationTool;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.view.activity.ActivityMain;
import com.pcs.ztqtj.view.activity.newairquality.ActivityAirQualityQuery;
import com.pcs.ztqtj.view.myview.MyScrollView;
import com.pcs.ztqtj.view.myview.ViewPulldownRefresh;

import java.util.HashMap;
import java.util.Map;

/**
 * 首页
 */
public class FragmentHomeWeather extends SupportMapFragment {

    private View mView;
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
    private static Map<Integer, WeatherReceiver> mListReceiver = new HashMap<>();
    private ImageFetcher mImageFetcher;
    // 数据命令
    private CommandBatching mDataCommand = null;

    private TextureMapView mMapView;
    private String mPrevBg = "";

    private CommandMain7DaysWeather commandMain7DaysWeather;

    private MyRecevier recevier = new MyRecevier();

    //是否已改变城市
    private boolean isChangedCity = false;

    public FragmentHomeWeather() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_home, container, false);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ActivityMain activity = (ActivityMain) getActivity();
        mImageFetcher = activity.getImageFetcher();
        mOtherRefreshAnim = null;

        mScrollView = mView.findViewById(R.id.scroll_view);

        // 初始化行数据
        initRowData(savedInstanceState);
        refreshData(false);

        mMapView = mScrollView.findViewById(R.id.map);

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
        ConstraintLayout clTop = mView.findViewById(R.id.clTop);
        clTop.setOnClickListener(mOnClick);
        ImageView ivSetting = mView.findViewById(R.id.ivSetting);
        ivSetting.setOnClickListener(mOnClick);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == MyConfigure.REQUEST_PERMISSION_AUDIO) {
            PermissionsTools.onRequestPermissionsResult(getActivity(), permissions, grantResults, new PermissionsTools.RequestPermissionResultCallback() {
                @Override
                public void onSuccess() {
                    if(mDataCommand != null) {
                        mDataCommand.checkPermission(permissions, grantResults);
                    }
                }

                @Override
                public void onDeny() {

                }

                @Override
                public void onDenyNeverAsk() {

                }
            });
        }
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

    /**
     * resume时自动刷新视图
     */
    private void refreshData(boolean isChangeCity) {
        //刷新城市名
        refreshCityName();
        if (mDataCommand == null) {
            return;
        }
        if (isChangeCity) {
            commandMain7DaysWeather.setChangeCity();
        }
        mDataCommand.execute();
        mPrevBg = "";
    }

    /**
     * 刷新城市名
     */
    private void refreshCityName() {
        PackLocalCityMain cityMain = ZtqCityDB.getInstance().getCityMain();
        if (cityMain == null) {
            return;
        }
        TextView tvCity = mView.findViewById(R.id.tvCity);
        TextView tvStreet = mView.findViewById(R.id.tvStreet);
        PackLocalCityLocation packLocalCityLocation = ZtqLocationTool.getInstance().getLocationCity();

        //获取定位信息
        RegeocodeAddress mRegeocode = ZtqLocationTool.getInstance().getSearchAddress();

        String cityName = "";
        String streen = "";
        PackLocalCity provinceInfo = ZtqCityDB.getInstance().getProvinceById(cityMain.PARENT_ID);
        // 上海市
        if (mRegeocode != null && cityMain.ID.equals(packLocalCityLocation.ID)) {
            if (cityMain.ID.equals("10102")) {
                cityName = cityMain.NAME;
            } else {
                if (cityMain.NAME.contains(cityMain.CITY)) {
                    if (provinceInfo != null) {
                        cityName = provinceInfo.NAME + "." + cityMain.NAME;
                    } else {
                        cityName = cityMain.NAME;
                    }
                } else {
                    cityName = provinceInfo.NAME + "." + cityMain.CITY + "." + cityMain.NAME;
                }
            }
            streen = mRegeocode.getFormatAddress() + "附近";
            tvStreet.setText(streen);
            tvStreet.setVisibility(View.VISIBLE);
        } else {
            String province = "", city = "", dis = "";
            if (provinceInfo != null) {
                province = provinceInfo.NAME;
            }
            city = cityMain.CITY;
            dis = cityMain.NAME;
            if (cityMain.NAME.contains(cityMain.CITY) && provinceInfo != null) {
                if (cityMain.CITY.contains(provinceInfo.NAME)) {
                    cityName = dis;
                } else {
                    cityName = province + "." + dis;
                }
            } else {
                if (province.equals(city)) {
                    cityName += province + "." + dis;
                } else {
                    cityName += province + "." + city + "." + dis;
                }
            }
            tvStreet.setVisibility(View.GONE);
        }
        tvCity.setText(cityName);
        ActivityAirQualityQuery.setTitel(tvCity.getText().toString());
    }

    /**
     * 初始化行数据
     */
    private void initRowData(Bundle savedInstanceState) {
        ViewGroup rootLayout = mScrollView.findViewById(R.id.scroll_view_layout);

        mDataCommand = new CommandBatching();
        mDataCommand.addCommand(new CommandMainRow0((ActivityMain) getActivity(), rootLayout, mImageFetcher));
        mDataCommand.addCommand(new CommandMainRow1(getActivity(), rootLayout, mImageFetcher, mShowBg, this));
        mDataCommand.addCommand(new CommandMain24Hours(getActivity(), rootLayout));
        commandMain7DaysWeather = new CommandMain7DaysWeather(getActivity(), rootLayout, mShowBg);
        mDataCommand.addCommand(commandMain7DaysWeather);
        mDataCommand.addCommand(new CommandMainRow3(getActivity(), rootLayout, mImageFetcher, savedInstanceState));
        mDataCommand.addCommand(new CommandMainRow4(getActivity(), rootLayout, mImageFetcher));
        mDataCommand.execute();
    }

    private class MyListenerRefreshTouch extends ListenerRefreshTouch {
        private boolean mScrollable = true;

        public MyListenerRefreshTouch(WindowManager windowManager,
                                      InterfacePulldownView pulldownView,
                                      InterfaceRefresh refreshView, InterfaceRefresh refreshAnim,
                                      InterfaceScrollView scrollView) {
            super(windowManager, pulldownView, refreshView, refreshAnim, scrollView);
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
     * @param context
     * @param receiver
     */
    public static void registerReceiver(Context context, PcsDataBrocastReceiver receiver) {
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

            PcsDataBrocastReceiver.registerReceiver(wReceiver.context, wReceiver.receiver);
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
//        if (recevier != null) {
//            PcsDataBrocastReceiver.unregisterReceiver(getActivity(), recevier);
//            recevier = null;
//        }
    }

    /**
     * 天气广播
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
        }
    };


    private void refresh() {
        //刷新数据
        refreshData(isChangedCity);
        if (isChangedCity) {
            isChangedCity = false;
        }
        // 触摸重置
        if (mListenerRefreshTouch != null) {
            mListenerRefreshTouch.reset();
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
            mView.setBackgroundDrawable(bitmapBg);
            mPrevBg = bgPath;
        }
    };

    private View.OnClickListener mOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.clTop:
                    showCityManager();
                    break;
                case R.id.ivSetting:
                    showSetting();
                    break;
                default:
                    break;
            }
        }
    };

    private void showCityManager() {
        if(getActivity() instanceof ActivityMain) {
            ((ActivityMain) getActivity()).showCityManager(true);
        }
    }

    private void showSetting() {
        if(getActivity() instanceof ActivityMain) {
            ((ActivityMain) getActivity()).showSetting(true);
        }
    }

    private class MyRecevier extends PcsDataBrocastReceiver {
        @Override
        public void onReceive(String nameStr, String errorStr) {
            if (!TextUtils.isEmpty(errorStr)) {
                return;
            }
            refresh();
        }
    }

}
