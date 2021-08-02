package com.pcs.ztqtj.view.activity.product.waterflood;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.OnMapScreenShotListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds.Builder;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.waterflood.AdapterWaterMonitorCount;
import com.pcs.ztqtj.control.adapter.waterflood.AdapterWaterMonitorStation;
import com.pcs.ztqtj.control.tool.ShareTools;
import com.pcs.ztqtj.control.tool.ZtqImageTool;
import com.pcs.ztqtj.control.tool.youmeng.ShareUtil;
import com.pcs.ztqtj.view.activity.FragmentActivityWithShare;
import com.tianyi.shawn.tjdecision.wxapi.wxtools.MD5;
import com.pcs.lib.lib_pcs_v3.control.tool.BitmapUtil;
import com.pcs.lib.lib_pcs_v3.control.tool.Util;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackShareAboutDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.waterflood.PackWaterMonitorDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.waterflood.PackWaterMonitorInfoDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.waterflood.PackWaterMonitorInfoUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.waterflood.PackWaterMonitorStationDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.waterflood.PackWaterMonitorStationUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.waterflood.PackWaterMonitorUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.waterflood.WaterMonitorInfo;
import com.pcs.lib_ztqfj_v2.model.pack.net.waterflood.WaterMonitorStationInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 雨量信息
 *
 * @author tya
 */
public class ActivityWaterMonitor extends FragmentActivityWithShare implements
        OnClickListener, OnCheckedChangeListener {

    // UI
    /**
     * 高德地图控件
     */
    private MapView mMapView = null;

    /**
     * 主布局
     */
    private RelativeLayout mMainLayout = null;

    /**
     * 图例布局
     */
    private LinearLayout mLegendLayout = null;

    /**
     * 单个气象站的雨量信息布局
     */
    private LinearLayout mStationRainInfoLayout = null;

    /**
     * 雨量统计布局
     */
    private LinearLayout mCountRainLayout = null;

    /**
     * 图例按钮
     */
    private Button btnLegend = null;

    /**
     * 积水统计按钮
     */
    private Button btnCountRain = null;

    private TextView tv_time_pub;

    // 数据

    /**
     * 地图层级
     */
    private final static int MAP_LEVEL = 14;

    /**
     * 高德地图对象
     */
    private AMap aMap = null;


    /**
     * 广播对象
     */
    private MyReceiver mReceiver = new MyReceiver();

    /**
     * 所有站点的积水信息上传包
     */
    private PackWaterMonitorUp packWaterMonitorUp = new PackWaterMonitorUp();

    /**
     * 所有站点的积水信息下载包
     */
    private PackWaterMonitorDown packWaterMonitorDown = new PackWaterMonitorDown();

    /**
     * 该站点的积水信息上传包
     */
    private PackWaterMonitorStationUp packWaterMonitorStationUp = new PackWaterMonitorStationUp();

    /**
     * 该站点的积水信息下载包
     */
    private PackWaterMonitorStationDown packWaterMonitorStationDown = new PackWaterMonitorStationDown();

    //所有站点的统计数据上传包
    private PackWaterMonitorInfoUp packWaterMonitorInfoUp = new PackWaterMonitorInfoUp();

    //所有站点的统计数据下载包
    private PackWaterMonitorInfoDown packWaterMonitorInfoDown = new PackWaterMonitorInfoDown();

    /**
     * 上一个被点击的marker
     */
    private Marker lastClickedMarker = null;

    /**
     * 地图上所有的marker
     */
    private List<Marker> markerList = new ArrayList<Marker>();

    /**
     * 上一个被点击的marker的图标
     */
    private BitmapDescriptor lastClickedMarkerIcon = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_monitor);
        setTitleText(R.string.title_water_monitor);
        initView();
        mMapView.onCreate(savedInstanceState);
        initEvent();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            PcsDataBrocastReceiver.unregisterReceiver(this, mReceiver);
        }
        mMapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    private Button btn_all_close;

    /**
     * 初始化UI
     */
    private void initView() {
        mMapView = (MapView) findViewById(R.id.map);
        mMainLayout = (RelativeLayout) findViewById(R.id.layout_main);
        mLegendLayout = (LinearLayout) LayoutInflater.from(this).inflate(
                R.layout.layout_water_monitor_legend, null);
        mStationRainInfoLayout = (LinearLayout) LayoutInflater.from(this)
                .inflate(R.layout.layout_water_monitor_station, null);
        btnLegend = (Button) findViewById(R.id.btn_legend);
        btnCountRain = (Button) findViewById(R.id.btn_count_rain);
        tv_time_pub= (TextView) findViewById(R.id.tv_time_pub);
        mCountRainLayout = (LinearLayout) LayoutInflater.from(this).inflate(
                R.layout.layout_water_monitor_count, null);
    }

    /**
     * 初始化事件
     */
    private void initEvent() {
        btnLegend.setOnClickListener(this);
        btnCountRain.setOnClickListener(this);
        setShareListener(this);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        PcsDataBrocastReceiver.registerReceiver(this, mReceiver);
        initMap();
        reqUnitTimeStationInfo();
    }

    /**
     * 初始化高德地图
     */
    private void initMap() {
        if (aMap == null) {
            aMap = mMapView.getMap();
            aMap.getUiSettings().setZoomControlsEnabled(false);
        }
        initMapEvent();
        // 定位
        location();
    }


    private String station_name;

    /**
     * 地图监听事件
     */
    private void initMapEvent() {
        aMap.setOnMarkerClickListener(new OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker) {
                closeOtherLayout(mStationRainInfoLayout);
                String station_id = getStationIdOnMap(marker);
                station_name = getStationName(marker);
                if (TextUtils.isEmpty(station_id)) {
                    return true;
                }
//                Date startdate = new Date(startCalendar.getTimeInMillis());
//                Date enddate = new Date(endCalendar.getTimeInMillis());
//                // 请求气象站雨量数据
//                reqTotalTimeRainInfo(startdate, enddate, station_id);
                reqUnitTimeRainInfo(station_id);
                return true;
            }
        });
    }

    /**
     * 通过marker获取气象站ID
     *
     * @param marker
     * @return
     */
    private String getStationIdOnMap(Marker marker) {
        WaterMonitorStationInfo info = (WaterMonitorStationInfo) marker.getObject();
        selectMarker(marker);
        return info.num;
    }

    /**
     * 通过marker获取气象站ID
     *
     * @param marker
     * @return
     */
    private String getStationName(Marker marker) {
        WaterMonitorStationInfo info = (WaterMonitorStationInfo) marker.getObject();
        selectMarker(marker);
        return info.station;
    }

    /**
     * 选中移动并标记marker
     */
    private void selectMarker(Marker marker) {
        LatLng latLng = marker.getPosition();
        moveCamera(latLng);
        // 如果上一个被点击的Marker不是是当前的Marker
        if (lastClickedMarker != null && lastClickedMarkerIcon != null
                && !lastClickedMarker.getId().equals(marker.getId())) {
            lastClickedMarker.setIcon(lastClickedMarkerIcon);
        }
        lastClickedMarker = marker;
        List<BitmapDescriptor> list = marker.getIcons();
        if (list.size() > 0) {
            lastClickedMarkerIcon = marker.getIcons().get(0);
        }
        marker.setIcon(BitmapDescriptorFactory
                .fromResource(R.drawable.icon_typhoon_location));
    }

    /**
     * 打开图例布局
     */
    private void showLegendLayout() {


        // 如果没显示布局就显示，反之则关闭
        if (!mLegendLayout.isShown()) {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ABOVE, R.id.ll_bottom_button);
            params.setMargins(60, 10, 0, 0);
            mMainLayout.addView(mLegendLayout, params);
        } else {
            closeView(mLegendLayout);
        }
    }

    /**
     * 显示站点雨量信息
     */
    private void showStationRainInfo(List<WaterMonitorInfo> list) {
        closeAllLayout();
        Button btnClose = (Button) mStationRainInfoLayout
                .findViewById(R.id.btn_monitor_station_close);
        btnClose.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                closeView(mStationRainInfoLayout);
            }
        });

        TextView tvTitle = (TextView) mStationRainInfoLayout
                .findViewById(R.id.tv_monitor_station_title);
        tvTitle.setText(station_name);
        ListView listView = (ListView) mStationRainInfoLayout
                .findViewById(R.id.lv_monitor_station);
        AdapterWaterMonitorStation adapter = new AdapterWaterMonitorStation(this, list);
        listView.setAdapter(adapter);

        // 如果没显示布局就显示，反之则关闭
        if (!mStationRainInfoLayout.isShown()) {
            LinearLayout ll = (LinearLayout) findViewById(R.id.ll_bottom_button);
            RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) ll
                    .getLayoutParams();
            int bottomHeight = p.height + p.bottomMargin * 2;
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.BELOW, R.id.ll_time_slot);
            params.setMargins(0, 0, 0, bottomHeight);
            mMainLayout.addView(mStationRainInfoLayout, params);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 关闭指定布局
     *
     * @param view
     */
    private void closeView(View view) {
        if (view.isShown()) {
            mMainLayout.removeView(view);
        }
    }


    /**
     * 处理积水统计按钮
     */
    private void procCountRain() {
        if (!mCountRainLayout.isShown()) {
            showProgressDialog();
            PcsDataDownload.addDownload(packWaterMonitorInfoUp);
        } else {
            closeView(mCountRainLayout);
        }
    }

    private LinearLayout lay_monitor_station;

    /**
     * 显示积水统计布局
     */
    private void showCountRainLayout(final List<WaterMonitorInfo> list) {
        ListView listView = (ListView) mCountRainLayout
                .findViewById(R.id.lv_water_monitor);
        lay_monitor_station = (LinearLayout) mCountRainLayout.findViewById(R.id.lay_monitor_station);
        btn_all_close = (Button) mCountRainLayout.findViewById(R.id.btn_all_close);
        btn_all_close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                closeView(mCountRainLayout);
            }
        });
        if (list.size()==0){
            lay_monitor_station.setVisibility(View.GONE);
            listView.setVisibility(View.GONE);
        }else{
            lay_monitor_station.setVisibility(View.VISIBLE);
            listView.setVisibility(View.VISIBLE);
        }
        AdapterWaterMonitorCount adapter = new AdapterWaterMonitorCount(this, list);
        listView.setAdapter(adapter);
//        listView.setOnItemClickListener(new OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view,
//                                    int position, long id) {
//                closeOtherLayout(mStationRainInfoLayout);
//                WaterMonitorInfo rsi = list.get(position);
//                String station_id = rsi.station_name;
//                reqUnitTimeRainInfo(station_id);
//            }
//        });

        // 如果没显示布局就显示，反之则关闭
        if (!mCountRainLayout.isShown()) {
            LinearLayout ll = (LinearLayout) findViewById(R.id.ll_bottom_button);
            RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) ll
                    .getLayoutParams();
            int bottomHeight = p.height + p.bottomMargin * 2;
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.BELOW, R.id.ll_time_slot);
            params.setMargins(0, 0, 0, bottomHeight);
            mMainLayout.addView(mCountRainLayout, params);
        } else {
            closeView(mCountRainLayout);
        }
    }

    /**
     * 获取站点积水信息
     */
    private void reqUnitTimeRainInfo(String station_id) {
        if (!isOpenNet()) {
            showToast(getString(R.string.net_err));
            return;
        }
        showProgressDialog();
        packWaterMonitorStationUp.station_no = station_id;
        PcsDataDownload.addDownload(packWaterMonitorStationUp);
    }

    /**
     * 所有站点信息
     */
    private void reqUnitTimeStationInfo() {

        if (!isOpenNet()) {
            showToast(getString(R.string.net_err));
            return;
        }
        showProgressDialog();
        if (packWaterMonitorUp == null) {
            packWaterMonitorUp = new PackWaterMonitorUp();
        }
        PcsDataDownload.addDownload(packWaterMonitorUp);
    }


    /**
     * 定位
     */
    private void location() {
//        LatLng latLng = ZtqLocationTool.getInstance().getLatLng();

        LatLng latLng=new LatLng(39.0851000000,117.1993700000);
        if (latLng != null) {
            aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,
                    MAP_LEVEL));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_right:
                aMap.getMapScreenShot(mScreenShotListener);
                break;
            case R.id.btn_close: {
                // 先关闭其他页面
                // closeOtherLayout(mChooseTimeLayout);
                break;
            }
            case R.id.btn_legend: {
                // 先关闭其他页面
                closeOtherLayout(mLegendLayout);
                showLegendLayout();
                break;
            }

            case R.id.btn_count_rain: {
                closeOtherLayout(mCountRainLayout);
                procCountRain();
                break;
            }
        }
    }

    private String genNonceStr() {
        Random random = new Random();
        return MD5.getMessageDigest(String.valueOf(random.nextInt(10000))
                .getBytes());
    }

    /**
     * 在地图上加点
     *
     * @param list
     */
    private void addMarkerOnMap(List<WaterMonitorStationInfo> list) {
        if (list == null) {
            return;
        }
        Builder builder = new Builder();
        clearMarker();
        float allLat = 0f;
        float allLog = 0f;
        for (int i = 0; i < list.size(); i++) {
            WaterMonitorStationInfo info = list.get(i);
            if (TextUtils.isEmpty(info.lat) || TextUtils.isEmpty(info.log)) {
                continue;
            }
            double lat = Double.parseDouble(info.lat);
            double log = Double.parseDouble(info.log);
            LatLng latLng = new LatLng(lat, log);
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .anchor(1.5f, 1.5f)
                    .icon(BitmapDescriptorFactory
                            .fromResource(getIconID(info.lev)));
            addMarker(markerOptions, info);
            builder.include(latLng);
            allLat += lat;
            allLog += log;
        }
        LatLng latLng = new LatLng(allLat / list.size(), allLog / list.size());
        // int maxZoom = (int) aMap.getMaxZoomLevel();
        // aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(),
        // MAP_LEVEL));
        aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
    }

    /**
     * 添加marker
     *
     * @param markerOptions
     */
    private void addMarker(MarkerOptions markerOptions, WaterMonitorStationInfo info) {
        Marker marker = aMap.addMarker(markerOptions);
        marker.setObject(info);
        markerList.add(marker);
    }

    /**
     * 清除所有的marker
     */
    private void clearMarker() {
        aMap.clear();
        markerList.clear();
    }

    /**
     * 通过雨量获得图片ID
     *
     * @param rainfall
     * @return
     */
    private int getIconID(String rainfall) {
        int[] resIDs = {R.drawable.icon_water_h, R.drawable.icon_water_m,
                R.drawable.icon_water_l};
        int result = R.drawable.icon_water_h;
        result = resIDs[getRainLevel(rainfall)];
        return result;
    }

    /**
     * 获取雨量等级
     *
     * @param rainfall 雨量
     * @return
     */
    private int getRainLevel(String rainfall) {
        if (rainfall.equals("重度")) {
            return 0;
        } else if (rainfall.equals("中度")) {
            return 1;
        } else {
            return 2;
        }
    }

    /**
     * 移动摄像机
     *
     * @param latLng 坐标
     */
    private void moveCamera(LatLng latLng) {
        float currentLevel = aMap.getCameraPosition().zoom;
        aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,
                currentLevel));
    }

    /**
     * 关闭所有布局
     */
    private void closeAllLayout() {
        if (mLegendLayout != null) {
            closeView(mLegendLayout);
        }
        if (mStationRainInfoLayout != null) {
            closeView(mStationRainInfoLayout);
        }
        if (mCountRainLayout != null) {
            closeView(mCountRainLayout);
        }
    }

    /**
     * 关闭其他页面
     */
    private void closeOtherLayout(View layout) {
        if (mLegendLayout != null && !mLegendLayout.equals(layout)) {
            closeView(mLegendLayout);
        }
        if (mStationRainInfoLayout != null
                && !mStationRainInfoLayout.equals(layout)) {
            closeView(mStationRainInfoLayout);
        }
        if (mCountRainLayout != null && !mCountRainLayout.equals(layout)) {
            closeView(mCountRainLayout);
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        // switch (checkedId) {
        // case R.id.rb_unit_time_rain:
        // procUnitTimeRain();
        // break;
        // case R.id.rb_total_time_rain:
        // procTotalTimeRain();
        // break;
        // }
    }

    /**
     * 点击下拉菜单回调类
     *
     * @author tya
     */
    private class MyClickListener implements OnClickListener {

        private List<String> dataList = new ArrayList<String>();

        public MyClickListener() {
        }

        public MyClickListener(List<String> dataList) {
            this.dataList = dataList;
        }

        @Override
        public void onClick(View v) {
            if (dataList == null || dataList.size() == 0) {
                return;
            }
        }

        /**
         * 设置popwindow显示数据列表
         *
         * @param dataList
         */
        public void setDateList(List<String> dataList) {
            this.dataList.clear();
            this.dataList.addAll(dataList);
        }

    }

    /**
     * 广播类
     *
     * @author tya
     */
    private class MyReceiver extends PcsDataBrocastReceiver {

        @Override
        public void onReceive(String nameStr, String errorStr) {
            if (TextUtils.isEmpty(nameStr)) {
                return;
            }

            if (!TextUtils.isEmpty(errorStr)) {
                return;
            }


            // 站点信息
            if (packWaterMonitorUp.getName().equals(nameStr)) {
                dismissProgressDialog();
                packWaterMonitorDown = (PackWaterMonitorDown) PcsDataManager.getInstance().getNetPack(nameStr);
                if (packWaterMonitorDown == null) {
                    return;
                }
                tv_time_pub.setText("更新 "+packWaterMonitorDown.time_pub);
                if (packWaterMonitorDown.listdata.size() == 0) {
                    Toast.makeText(ActivityWaterMonitor.this, "无积水!",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                addMarkerOnMap(packWaterMonitorDown.listdata);
            } else

                // 某站点的积水信息
                if (packWaterMonitorStationUp.getName().equals(nameStr)) {
                    dismissProgressDialog();
                    packWaterMonitorStationDown = (PackWaterMonitorStationDown) PcsDataManager.getInstance()
                            .getNetPack(nameStr);
                    if (packWaterMonitorStationDown == null) {
                        return;
                    }
                    if (packWaterMonitorStationDown.raininfo_list.size() == 0) {
                        Toast.makeText(ActivityWaterMonitor.this, "无数据！",
                                Toast.LENGTH_LONG).show();
                        return;
                    }
                    showStationRainInfo(packWaterMonitorStationDown.raininfo_list);
                } else if (packWaterMonitorInfoUp.getName().equals(nameStr)) {
                    dismissProgressDialog();
                    packWaterMonitorInfoDown = (PackWaterMonitorInfoDown) PcsDataManager.getInstance().getNetPack
                            (nameStr);
                    if (packWaterMonitorInfoDown == null) {
                        Toast.makeText(ActivityWaterMonitor.this, "暂无数据！", Toast.LENGTH_LONG).show();
                        return;
                    }
                    TextView tv_tj_des = (TextView) mCountRainLayout.findViewById(R.id.tv_tj_des);
                    tv_tj_des.setText(packWaterMonitorInfoDown.tj_desc);
                    showCountRainLayout(packWaterMonitorInfoDown.raininfo_list);
                }


        }

    }

    private Bitmap bitmapMap;

    private void getBitmap() {
        if (mMapView != null) {
            mMapView.getMap().getMapScreenShot(new OnMapScreenShotListener() {
                @Override
                public void onMapScreenShot(Bitmap arg0) {
                    bitmapMap = arg0;
                    shareMap(ActivityWaterMonitor.this);
                }

                @Override
                public void onMapScreenShot(Bitmap bitmap, int i) {

                }
            });
        }
    }

    /**
     * 分享
     */
    @Override
    public void share(final Activity activity) {
        if (mCountRainLayout != null
                && mCountRainLayout.getVisibility() == View.INVISIBLE) {
            ShareUtil.share(activity);
        } else {
            getBitmap();
        }
    }

    private void shareMap(Activity activity) {
        Bitmap bitmap = BitmapUtil.takeScreenShot(activity);
        Canvas canvas = new Canvas(bitmap);
        if (bitmapMap != null) {
            int hight = bitmap.getHeight() - bitmapMap.getHeight()
                    + Util.dip2px(this, 40);
            canvas.drawBitmap(bitmapMap, 0, hight, null);
        }
        ShareUtil.share(activity, "", bitmap);
    }

    /**
     * 高德地图截屏回调
     */
    private OnMapScreenShotListener mScreenShotListener = new OnMapScreenShotListener() {
        @Override
        public void onMapScreenShot(Bitmap bitmap) {
            View layout = findViewById(R.id.layout_main).getRootView();
            mAmapBitmap = bitmap;
            Bitmap bm = ZtqImageTool.getInstance().getScreenBitmapNew(ActivityWaterMonitor.this);

            int[] location = new int[2];
            mMapView.getLocationOnScreen(location);

            mShareBitmap = procImage(mAmapBitmap, bm, location[1]);
            mShareBitmap = ZtqImageTool.getInstance().stitchQR(ActivityWaterMonitor.this, mShareBitmap);
            PackShareAboutDown down = (PackShareAboutDown) PcsDataManager.getInstance().getNetPack
                    ("wt_share#ABOUT_QXCP_DXFW");
            if (down != null) {
                ShareTools.getInstance(ActivityWaterMonitor.this).setShareContent(getTitleText(), down
                        .share_content, mShareBitmap, "0").showWindow(layout);
            }
        }

        @Override
        public void onMapScreenShot(Bitmap bitmap, int i) {

        }
    };
}
