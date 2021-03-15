//package com.pcs.ztqtj.view.activity.product;
//
//import android.annotation.SuppressLint;
//import android.graphics.Bitmap;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.WindowManager;
//import android.widget.GridView;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.ListView;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.amap.api.maps.AMap;
//import com.amap.api.maps.AMap.OnCameraChangeListener;
//import com.amap.api.maps.AMap.OnMapLoadedListener;
//import com.amap.api.maps.AMap.OnMapTouchListener;
//import com.amap.api.maps.AMap.OnMarkerClickListener;
//import com.amap.api.maps.CameraUpdateFactory;
//import com.amap.api.maps.MapView;
//import com.amap.api.maps.UiSettings;
//import com.amap.api.maps.model.BitmapDescriptor;
//import com.amap.api.maps.model.BitmapDescriptorFactory;
//import com.amap.api.maps.model.CameraPosition;
//import com.amap.api.maps.model.LatLng;
//import com.amap.api.maps.model.Marker;
//import com.amap.api.maps.model.MarkerOptions;
//import com.amap.api.maps.model.PolylineOptions;
//import com.amap.api.services.core.LatLonPoint;
//import com.amap.api.services.core.PoiItem;
//import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
//import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
//import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
//import com.pcs.lib_ztqfj_v2.model.pack.net.PackShareAboutDown;
//import com.pcs.lib_ztqfj_v2.model.pack.net.locationwarning.MyPosition;
//import com.pcs.lib_ztqfj_v2.model.pack.net.oceanweather.AreaNameInfo;
//import com.pcs.lib_ztqfj_v2.model.pack.net.oceanweather.OceanAreaInfo;
//import com.pcs.lib_ztqfj_v2.model.pack.net.oceanweather.OceanWeatherInfo;
//import com.pcs.lib_ztqfj_v2.model.pack.net.oceanweather.PackAreaPositionDown;
//import com.pcs.lib_ztqfj_v2.model.pack.net.oceanweather.PackAreaPositionUp;
//import com.pcs.lib_ztqfj_v2.model.pack.net.oceanweather.PackOceanWeatherDown;
//import com.pcs.lib_ztqfj_v2.model.pack.net.oceanweather.PackOceanWeatherUp;
//import com.pcs.ztqtj.R;
//import com.pcs.ztqtj.control.adapter.ocean.AdapterMapOcean;
//import com.pcs.ztqtj.control.tool.LatLngFactory;
//import com.pcs.ztqtj.control.tool.PoiOverlay;
//import com.pcs.ztqtj.control.tool.ShareTools;
//import com.pcs.ztqtj.control.tool.ZtqImageTool;
//import com.pcs.ztqtj.view.activity.product.typhoon.AMapUtil;
//import com.pcs.ztqtj.view.myview.OceanAreaView;
//import com.pcs.ztqtj.view.myview.OceanWeatherView;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * 海洋天气
// *
// * @author chenjh
// * @author E.Sun
// *         2015年11月3日
// */
//@SuppressLint("InflateParams")
//public class ActivityOceanWeather extends AMapUtil implements OnClickListener {
//
//    private final static String TAG = "ActivityOceanWeather";
//
//    /**
//     * 地图控件
//     **/
//    private AMap mAMap;
//
//    /**
//     * 地图
//     **/
//    private MapView mMapView;
//
//    /**
//     * 标签
//     */
//    private RelativeLayout switchLayout1, switchLayout2;
//
//    /**
//     * 下划线
//     */
//    private View bottomLine1, bottomLine2;
//
//    /**
//     * 详情弹窗
//     */
//    private LinearLayout layoutDes;
//
//    /**
//     * 详情天气图标
//     */
//    private ImageView imageWeather;
//    /**
//     * 详情内容文本
//     */
//    private TextView tvArea, tvWeather, tvDate, tvWind, tvWave, tvWater;
//
//    private PcsDataBrocastReceiver receiver = new MyReceiver();
//    private PackOceanWeatherUp weatherUpPack = new PackOceanWeatherUp();
//    private PackAreaPositionUp areaUpPack = new PackAreaPositionUp();
//    private GridView gridView;
//    private AdapterMapOcean adapterMapOcean;
//
//    /**
//     * 渔场天气视图
//     */
//    private OceanWeatherView fisheryWeatherView = new OceanWeatherView();
//    /**
//     * 海域天气视图
//     */
//    private OceanWeatherView seaWeatherView = new OceanWeatherView();
//    /**
//     * 天气视图集合
//     */
//    private Map<Integer, OceanWeatherView> weatherViewMap = new HashMap<Integer, OceanWeatherView>();
//
//    /**
//     * 渔场区域视图
//     */
//    private OceanAreaView fisheryAreaView;
//    /**
//     * 沿海区域视图
//     */
//    private OceanAreaView seaAreaView;
//
//    /**
//     * 当前标签标识
//     */
//    private int mCurTab = OceanWeatherInfo.TYPE_SEA;
//
//    /**
//     * 是否进行自动缩放效果优化
//     */
//    private boolean isOptimize = false;
//
//    /**
//     * 消息：天气图标加载完毕
//     */
//    private final int MSG_ADD_WEATHER_VIEW_OVER = 101;
//    /**
//     * 消息：区域视图加载完毕
//     */
//    private final int MSG_ADD_AREA_VIEW_OVER = 102;
//
//    /**
//     * 上一次点击的标记点
//     */
//    private Marker lastMarker;
//
//    private ListView lv_ocean;
//
//    /**
//     * 默认缩放
//     **/
//    public static final float DEFAULT_ZOOM = 5.5f;
//
//    private Handler mHandler = new Handler() {
//        @Override
//        public void dispatchMessage(Message msg) {
//            super.dispatchMessage(msg);
//            switch (msg.what) {
//                case MSG_ADD_WEATHER_VIEW_OVER:
//                    showWeatherView();
//                    break;
//                case MSG_ADD_AREA_VIEW_OVER:
//                    showAreaView();
//                    break;
//            }
//        }
//    };
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.ocean_weather);
//
//        initParam();
//        initView();
//        resetView();
//        initEvent();
//        initMapView(savedInstanceState);
//
//        PcsDataBrocastReceiver.registerReceiver(this, receiver);
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        mMapView.onResume();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        mMapView.onPause();
//    }
//
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        mMapView.onSaveInstanceState(outState);
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        PcsDataBrocastReceiver.unregisterReceiver(this, receiver);
//        mMapView.onDestroy();
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.switch_layout1:
//                onClickTab(OceanWeatherInfo.TYPE_SEA);
//                break;
//            case R.id.switch_layout2:
//                onClickTab(OceanWeatherInfo.TYPE_FISHERY);
//                break;
//            case R.id.layout_des:
//                onClickDes();
//                break;
//        }
//    }
//
//    /**
//     * 初始化页面参数
//     */
//    private void initParam() {
//        //weatherViewMap.put(OceanWeatherInfo.TYPE_FISHERY, fisheryWeatherView);
//        weatherViewMap.put(OceanWeatherInfo.TYPE_SEA, seaWeatherView);
//    }
//
//    /**
//     * 初始化地图
//     *
//     * @param savedInstanceState
//     */
//    private void initMapView(Bundle savedInstanceState) {
//        mMapView = (MapView) findViewById(R.id.map);
//        mMapView.onCreate(savedInstanceState);
//        mAMap = mMapView.getMap();
//        UiSettings set = mAMap.getUiSettings();
//        set.setZoomControlsEnabled(false);// 禁用缩放按钮
//        set.setRotateGesturesEnabled(false);// 禁止旋转
//
//        mAMap.setOnMarkerClickListener(myOnMarkerClickListener);
//        mAMap.setOnCameraChangeListener(myOnCameraChangeListener);
//        mAMap.setOnMapLoadedListener(myOnMapLoadedListener);
//        mAMap.setOnMapTouchListener(mOnMapTouchListener);// 地图触摸事件
//        mAMap.getUiSettings().setRotateGesturesEnabled(false);
//        mAMap.getUiSettings().setTiltGesturesEnabled(false);
//    }
//
//    /**
//     * 初始化UI
//     */
//    private void initView() {
//        View view = findViewById(R.id.layout_content);
//        view.setBackgroundColor(getResources().getColor(R.color.alpha100));
//        setTitleText(getString(R.string.title_ocean_weather));
//
//        switchLayout1 = (RelativeLayout) findViewById(R.id.switch_layout1);
//        switchLayout2 = (RelativeLayout) findViewById(R.id.switch_layout2);
//        bottomLine1 = findViewById(R.id.bottom_line1);
//        bottomLine2 = findViewById(R.id.bottom_line2);
//        gridView = (GridView) findViewById(R.id.grid_view);
//        lv_ocean=findViewById(R.id.lv_ocean);
//        adapterMapOcean = new AdapterMapOcean(ActivityOceanWeather.this, list);
//        lv_ocean.setAdapter(adapterMapOcean);
////        gridView.setAdapter(adapterMapOcean);
//        initDesLayout();
//    }
//
//    /**
//     * 初始化详情布局
//     */
//    private void initDesLayout() {
//        View convertView = LayoutInflater.from(ActivityOceanWeather.this).inflate(
//                R.layout.item_ocean_v, null);
//        layoutDes = (LinearLayout) convertView.findViewById(R.id.layout_des);
//        layoutDes.setOnClickListener(this);
//        imageWeather = (ImageView) layoutDes.findViewById(R.id.image_weather);
//        tvArea = (TextView) layoutDes.findViewById(R.id.tv_area);
//        tvWeather = (TextView) layoutDes.findViewById(R.id.tv_weather);
//        tvDate = (TextView) layoutDes.findViewById(R.id.tv_date);
//        tvWind = (TextView) layoutDes.findViewById(R.id.tv_wind);
//        tvWave = (TextView) layoutDes.findViewById(R.id.tv_wave);
//        tvWater = (TextView) layoutDes.findViewById(R.id.tv_water);
//    }
//
//    /**
//     * 重置页面视图
//     */
//    private void resetView() {
//        resetDesLayout();
//    }
//
//    /**
//     * 重置详情布局
//     */
//    private void resetDesLayout() {
//        imageWeather.setImageBitmap(null);
//        tvArea.setText("");
//        tvWeather.setText("");
//        tvWind.setText("");
//        tvWave.setText("");
//        tvWater.setText("");
//        hideDesLayout();
//    }
//
//    /**
//     * 初始化用户操作响应事件
//     */
//    private void initEvent() {
//        switchLayout1.setOnClickListener(this);
//        switchLayout2.setOnClickListener(this);
//        setShareListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                switch (v.getId()) {
//                    case R.id.btn_right:
//                        mAMap.getMapScreenShot(mScreenShotListener);
//                        break;
//                }
//            }
//        });
//    }
//
//    /**
//     * 加载数据
//     */
//    private void loadData(String area) {
//        showProgressDialog();
//        requestWeatherData(area);
//    }
//
//    /**
//     * 请求海洋天气数据
//     */
//    private void requestWeatherData(String area) {
//        if (!isOpenNet()) {
//            showToast(getString(R.string.net_err));
//            return;
//        }
//        showProgressDialog();
//        weatherUpPack.area = area;
//        PcsDataDownload.addDownload(weatherUpPack);
//    }
//
//    private ArrayList<OceanWeatherInfo> list=new ArrayList<>();
//    /**
//     * 接收海洋天气数据
//     *
//     * @param name
//     */
//    private void receiveWeatherData(String name) {
//        PackOceanWeatherDown downPack = (PackOceanWeatherDown) PcsDataManager.getInstance().getNetPack(name);
//        if (downPack == null) {
//            showToast(getString(R.string.error_ocean_weather_data));
//            return;
//        }
//
//        if (downPack.oceanWeatherList == null || downPack.oceanWeatherList.size() <= 0) {
//            showToast(getString(R.string.hint_no_ocean_weather_data));
//            return;
//        }
//        gridView.setVisibility(View.VISIBLE);
//        list.clear();
//        list.addAll(downPack.oceanWeatherList);
//        WindowManager wm = this.getWindowManager();
//        int width = wm.getDefaultDisplay().getWidth();
//        LinearLayout.LayoutParams linearParams =new LinearLayout.LayoutParams(width*list.size(), LinearLayout.LayoutParams.WRAP_CONTENT);
//        gridView.setLayoutParams(linearParams);
//        adapterMapOcean.notifyDataSetChanged();
//
//        	//addWeatherView(downPack.oceanWeatherList);
//    }
//
//    /**
//     * 请求区域数据
//     */
//    private void requestAreaData() {
//        if (!isOpenNet()) {
//            showToast(getString(R.string.net_err));
//            return;
//        }
//        showProgressDialog();
//        PcsDataDownload.addDownload(areaUpPack);
//    }
//
//    /**
//     * 接收区域数据
//     *
//     * @param name
//     */
//    private void receiveAreaData(String name) {
//        dismissProgressDialog();
//
//        PackAreaPositionDown downPack = (PackAreaPositionDown) PcsDataManager.getInstance().getNetPack(name);
//        if (downPack == null) {
//            showToast(getString(R.string.error_ocean_area_data));
//            return;
//        }
//
////        if (downPack.areaMap == null || downPack.areaMap.size() <= 0) {
////            showToast(getString(R.string.hint_no_ocean_area_data));
////            return;
////        }
////        addWeatherViews(downPack.list_info.get(0));
////        addAreaView(downPack.areaMap);
//    }
//
////	/**
//
//    /**
//     * 添加天气视图
//     *
//     * @param weatherList
//     */
//    private void addWeatherViews(List<AreaNameInfo> weatherList) {
//        AreaNameInfo info;
//        MarkerOptions options;
//        for (int i = 0; i < weatherList.size(); i++) {
//            info = weatherList.get(i);
//            if (info == null) {
//                continue;
//            }
//            options = getWeatherOptions(info);
//
//            // if(OceanWeatherInfo.TYPE_FISHERY == info.name) {
////            } else if(OceanWeatherInfo.TYPE_SEA == info.type) {
//            seaWeatherView.addWeatherMarker(options);
////                seaWeatherView.addWeatherMarker(options);
////            }
//        }
//
//        // 休眠时，等待唤醒屏幕后更新UI
//        mHandler.sendEmptyMessage(MSG_ADD_WEATHER_VIEW_OVER);
//    }
////	 * 添加天气视图
////	 * @param weatherList
////	 */
////	private void addWeatherView(List<OceanWeatherInfo> weatherList) {
////		OceanWeatherInfo info;
////		MarkerOptions options;
////		for (int i = 0; i < weatherList.size(); i++) {
////			info = weatherList.get(i);
////			if(info == null) {
////				continue;
////			}
////			options = getWeatherOptions(info);
////			if(OceanWeatherInfo.TYPE_FISHERY == info.type) {
////				fisheryWeatherView.addWeatherMarker(options);
////			} else if(OceanWeatherInfo.TYPE_SEA == info.type) {
////				seaWeatherView.addWeatherMarker(options);
////			}
////		}
////
////		// 休眠时，等待唤醒屏幕后更新UI
////		mHandler.sendEmptyMessage(MSG_ADD_WEATHER_VIEW_OVER);
////	}
//
//    /**
//     * 添加区域视图
//     *
//     * @param areaMap
//     */
//    private void addAreaView(Map<Integer, OceanAreaInfo> areaMap) {
////        OceanAreaInfo info = areaMap.get(OceanAreaInfo.TYPE_FISHERY);
////        seaAreaView = getAreaView(info);
////        // 休眠时，等待唤醒屏幕后更新UI
////        mHandler.sendEmptyMessage(MSG_ADD_AREA_VIEW_OVER);
//    }
//
//    /**
//     * 构建区域视图
//     *
//     * @param info
//     * @return
//     */
//    private OceanAreaView getAreaView(OceanAreaInfo info) {
//        if (info == null) {
//            return null;
//        }
//
//        OceanAreaView areaView = new OceanAreaView();
////        for (AreaNameInfo nameInfo : info.namePositions) {
////            areaView.addNameOptions(getAreaNameOptions(nameInfo));
////        }
////        for (AreaPositionInfo positionInfo : info.areaPositions) {
////            areaView.addRangeOptions(getAreaRange(positionInfo.positionList));
////        }
//        return areaView;
//    }
//
//    /**
//     * 获取区域名称标识
//     *
//     * @param info
//     */
//    private MarkerOptions getAreaNameOptions(AreaNameInfo info) {
//        if (info == null) {
//            return null;
//        }
//        View view = getLayoutInflater().inflate(R.layout.view_fishery_area_name, null);
//        TextView tv = (TextView) view.findViewById(R.id.tv_name);
//        tv.setText(info.name);
//        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromView(view);
//        LatLng latLng = new LatLng(info.latitude, info.longitude);
//        MarkerOptions options = new MarkerOptions().position(latLng)
//                .icon(bitmap).setFlat(true).anchor(0.5f, 0.5f);
//        return options;
//    }
//
//    /**
//     * 添加区域范围
//     *
//     * @param list
//     */
//    private PolylineOptions getAreaRange(List<MyPosition> list) {
//        if (list == null || list.size() <= 0) {
//            return null;
//        }
//
//        PolylineOptions options = new PolylineOptions();
//        options.width(10);
////        if (type == OceanAreaInfo.TYPE_SEA) {
//            options.color(getResources().getColor(R.color.line_yellow_soil));
////        } else {
////            options.color(Color.WHITE);
////        }
//        LatLng latLng;
//        for (MyPosition info : list) {
//            if (info != null) {
//                latLng = new LatLng(info.latitude, info.longitude);
//                options.add(latLng);
//            }
//        }
//
//        return options;
//    }
//
//    /**
//     * 获取天气图标
//     *
//     * @param info 海洋天气信息
//     * @return
//     */
//    private BitmapDescriptor getWeatherIcon(AreaNameInfo info) {
////		View view = getLayoutInflater().inflate(R.layout.orean_popview_small, null);
////		TextView tvArea = (TextView) view.findViewById(R.id.seaplace);
////		TextView tvWeather = (TextView) view.findViewById(R.id.seaplace_weather);
////		ImageView icon = (ImageView) view.findViewById(R.id.sea_icon);
////		tvArea.setText(info.area);
////		tvWeather.setText(info.weather);
////		if (!TextUtils.isEmpty(info.icon)) {
////			try {
////				Bitmap bitmap = ZtqImageTool.getInstance().getWeatherIcon(
////						getApplicationContext(), true, info.icon);
////				icon.setImageBitmap(bitmap);
////			} catch (IOException e) {
////				e.printStackTrace();
////			}
////		}
////
////		return BitmapDescriptorFactory.fromView(view);
//
//
//        View view = getLayoutInflater().inflate(R.layout.view_ocean_weather_point, null);
//        ImageView icon = (ImageView) view.findViewById(R.id.image);
//		if (!TextUtils.isEmpty(info.ioc)) {
//        try {
//            Bitmap bitmap = ZtqImageTool.getInstance().getWeatherIcon(
//                    getApplicationContext(), true, info.ioc);
//            icon.setImageBitmap(bitmap);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//		}
//
//        return BitmapDescriptorFactory.fromView(view);
//    }
//
//    /**
//     * 获取天气标记
//     *
//     * @param info
//     * @return
//     */
//    private MarkerOptions getWeatherOptions(AreaNameInfo info) {
//        BitmapDescriptor icon = getWeatherIcon(info);
//        LatLng latLng = LatLngFactory.getFormatLatLng(info.ioclatitude, info.ioclongitude);
//
//        // 初始化marker内容
//        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.position(latLng);
//        if (icon != null) {
//            markerOptions.icon(icon);
//        }
//        markerOptions.snippet(info.name);
//        markerOptions.anchor(0.5f, 0.5f);
//        markerOptions.zIndex(1);
//
//        return markerOptions;
//    }
//
//    /**
//     * 显示当前海洋天气及区域
//     */
//    private void showView() {
//        showWeatherView();
//        showAreaView();
//    }
//
//    /**
//     * 显示当前栏目天气
//     */
//    private void showWeatherView() {
//        OceanWeatherView view = weatherViewMap.get(mCurTab);
//        view.show(mAMap);
//        zoomToSpan(view.getLatLngs());
//    }
//
//    /**
//     * 显示当前栏目区域
//     */
//    private void showAreaView() {
////		if(mCurTab == OceanAreaInfo.TYPE_SEA) {
//        if (seaAreaView != null) {
//            seaAreaView.show(mAMap);
//        }
////		}
////		else if(mCurTab == OceanAreaInfo.TYPE_FISHERY) {
////			if(fisheryAreaView != null) {
////				fisheryAreaView.show(mAMap);
////			}
////		}
//    }
//
//    /**
//     * 显示详情布局
//     *
//     * @param info
//     */
//    private void showDesLayout(OceanWeatherInfo info) {
//        try {
//            if (!TextUtils.isEmpty(info.icon)) {
//                Bitmap bitmap = ZtqImageTool.getInstance().getWeatherIcon(
//                        getApplicationContext(), true, info.icon);
//                imageWeather.setImageBitmap(bitmap);
//            } else {
//                imageWeather.setImageBitmap(null);
//            }
//        } catch (IOException e) {
//            imageWeather.setImageBitmap(null);
//        }
//        tvArea.setText(info.area);
//        tvDate.setText(info.date);
//        tvWeather.setText(info.weather);
//        tvWind.setText(info.wind);
//        tvWave.setText(info.waveHeight);
//        tvWater.setText(info.waterTemperature);
//        layoutDes.setVisibility(View.VISIBLE);
//    }
//
//    /**
//     * 隐藏详情布局
//     */
//    private void hideDesLayout() {
//        gridView.setVisibility(View.GONE);
//        layoutDes.setVisibility(View.GONE);
//    }
//
//    /**
//     * 详情弹窗是否显示
//     *
//     * @return
//     */
//    private boolean isDesLayoutShow() {
//        return layoutDes.getVisibility() == View.VISIBLE;
//    }
//
//    /**
//     * 点击标签，切换栏目
//     *
//     * @param position
//     */
//    private void onClickTab(int position) {
//        if (mCurTab == position) {
//            return;
//        }
//
//        hideDesLayout();
//
//        switch (position) {
//            case 0:// 渔场
//                bottomLine1.setVisibility(View.GONE);
//                bottomLine2.setVisibility(View.VISIBLE);
//                mCurTab = 0;
//                break;
//            case 1:// 海域
//                bottomLine1.setVisibility(View.VISIBLE);
//                bottomLine2.setVisibility(View.GONE);
//                mCurTab = 1;
//                break;
//        }
//        mAMap.clear();
//        showView();
//    }
//
//    /**
//     * 点击详情布局
//     */
//    private void onClickDes() {
//        hideDesLayout();
//    }
//
//    /**
//     * 自动缩放
//     *
//     * @param list 经纬度集合
//     */
//    private void zoomToSpan(List<LatLng> list) {
//        if (list == null || list.size() <= 1) {
//            return;
//        }
//        isOptimize = true;
//
//        LatLonPoint point;
//        PoiItem poiItem;
//        List<PoiItem> poiItemList = new ArrayList<PoiItem>();
//        for (LatLng latLng : list) {
//            point = new LatLonPoint(latLng.latitude, latLng.longitude);
//            poiItem = new PoiItem("", point, "", "");
//            poiItemList.add(poiItem);
//        }
//        PoiOverlay poiOverlay = new PoiOverlay(mAMap, poiItemList);
//        poiOverlay.zoomToSpan();
//    }
//
//    /**
//     * 数据更新广播接收
//     */
//    private class MyReceiver extends PcsDataBrocastReceiver {
//
//        @Override
//        public void onReceive(String name, String error) {
//            if (weatherUpPack.getName().equals(name)) {
//                dismissProgressDialog();
//                // 海洋天气
//                if (!TextUtils.isEmpty(error)) {
//                    Log.e(TAG, "获取海洋天气列表失败");
//                    dismissProgressDialog();
//                    showToast(getString(R.string.error_net));
//                    return;
//                }
//                receiveWeatherData(name);
//                //requestAreaData();
//            } else if (name.equals(areaUpPack.getName())) {
//                // 区域
//                if (!TextUtils.isEmpty(error)) {
//                    Log.e(TAG, "获取区域列表失败");
//                    dismissProgressDialog();
//                    showToast(getString(R.string.error_net));
//                    return;
//                }
//                receiveAreaData(name);
//            }
//        }
//    }
//
//    private OnMapLoadedListener myOnMapLoadedListener = new OnMapLoadedListener() {
//
//        @Override
//        public void onMapLoaded() {
//            requestAreaData();
//            //loadData();
//        }
//
//    };
//
//    private OnCameraChangeListener myOnCameraChangeListener = new OnCameraChangeListener() {
//
//        @Override
//        public void onCameraChange(CameraPosition arg0) {
//
//        }
//
//        @Override
//        public void onCameraChangeFinish(CameraPosition arg0) {
//            if (isOptimize) {
//                isOptimize = false;
//                mAMap.animateCamera(CameraUpdateFactory.zoomTo(arg0.zoom - 0.8f), 2000, null);
//            }
//        }
//    };
//
//    /**
//     * 地图触摸事件监听器
//     *
//     * @author E.Sun
//     * 2015年9月7日
//     */
//    private OnMapTouchListener mOnMapTouchListener = new OnMapTouchListener() {
//
//        @Override
//        public void onTouch(MotionEvent arg0) {
//            hideDesLayout();
//        }
//    };
//
//    private OnMarkerClickListener myOnMarkerClickListener = new OnMarkerClickListener() {
//
//        @Override
//        public boolean onMarkerClick(Marker arg0) {
//            if (!TextUtils.isEmpty(arg0.getSnippet().toString())){
//                loadData(arg0.getSnippet().toString());
//            }
//            if (lastMarker == null) {
//                // 第一次点击显示弹窗
//                OceanWeatherInfo info = new OceanWeatherInfo();
//                info.fillData(arg0.getSnippet());
//                showDesLayout(info);
//                mAMap.animateCamera(CameraUpdateFactory.changeLatLng(arg0.getPosition()));
//            } else if (lastMarker.getId() != arg0.getId() || !isDesLayoutShow()) {
//                // 点击其他标记或弹窗未隐藏状态时显示弹窗
//                OceanWeatherInfo info = new OceanWeatherInfo();
//                info.fillData(arg0.getSnippet());
//                showDesLayout(info);
//                mAMap.animateCamera(CameraUpdateFactory.changeLatLng(arg0.getPosition()));
//            } else {
//                hideDesLayout();
//            }
//            lastMarker = arg0;
//            return true;
//        }
//    };
//
//    /**
//     * 高德地图截屏回调
//     */
//    private AMap.OnMapScreenShotListener mScreenShotListener = new AMap.OnMapScreenShotListener() {
//        @Override
//        public void onMapScreenShot(Bitmap bitmap) {
//            View layout = findViewById(R.id.ocean_weather_view).getRootView();
//            mAmapBitmap = bitmap;
//            Bitmap bm = ZtqImageTool.getInstance().getScreenBitmapNew(ActivityOceanWeather.this);
//
//            int[] location = new int[2];
//            mMapView.getLocationOnScreen(location);
//
//            mShareBitmap = procImage(mAmapBitmap, bm, location[1]);
//            mShareBitmap = ZtqImageTool.getInstance().stitchQR(ActivityOceanWeather.this, mShareBitmap);
//            PackShareAboutDown down = (PackShareAboutDown) PcsDataManager.getInstance().getNetPack("wt_share#ABOUT_QXCP_DXFW");
//            if (down != null) {
//                ShareTools.getInstance(ActivityOceanWeather.this).setShareContent(getTitleText(), down.share_content, mShareBitmap, "0").showWindow(layout);
//            }
//        }
//
//        @Override
//        public void onMapScreenShot(Bitmap bitmap, int i) {
//
//        }
//    };
//
////	/**
////	 * 自定义地图标记弹窗
////	 * @author E.Sun
////	 * 2015年11月3日
////	 */
////	private class MyInfoWindowAdapter implements InfoWindowAdapter {
////
////		@Override
////		public View getInfoContents(Marker arg0) {
////			View view = getLayoutInflater().inflate(R.layout.orean_popview,
////					null);
////			OceanWeatherInfo info = new OceanWeatherInfo();
////			info.fillData(arg0.getSnippet());
////
////			TextView tvArea = (TextView) view.findViewById(R.id.seaplace);
////			TextView tvDate = (TextView) view.findViewById(R.id.seaplace_date);
////			TextView tvWeather = (TextView) view.findViewById(R.id.seaplace_weather);
////			TextView tvWaterTemperature = (TextView) view.findViewById(R.id.watertemp);
////			TextView tvWaveHeight = (TextView) view.findViewById(R.id.wave_height);
////			TextView tvWind = (TextView) view.findViewById(R.id.windforce);
////
////			tvArea.setText(info.area);
////			tvDate.setText(info.date);
////			tvWeather.setText(info.weather);
////			tvWaterTemperature.setText("水温：" + info.waterTemperature);
////			tvWaveHeight.setText("浪高：" + info.waveHeight);
////			tvWind.setText("风力风向：" + info.wind);
////
////			ImageView image = (ImageView) view.findViewById(R.id.sea_icon);
////			if (!TextUtils.isEmpty(info.icon)) {
////				try {
////					Bitmap bitmap = ZtqImageTool.getInstance().getWeatherIcon(
////							getApplicationContext(), true, info.icon);
////					image.setImageBitmap(bitmap);
////				} catch (IOException e) {
////					e.printStackTrace();
////				}
////			}
////
////			return view;
////		}
////
////		@Override
////		public View getInfoWindow(Marker arg0) {
////			return null;
////		}
////	}
//
//}
