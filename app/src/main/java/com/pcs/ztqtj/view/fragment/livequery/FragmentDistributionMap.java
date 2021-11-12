package com.pcs.ztqtj.view.fragment.livequery;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.FycxFbtBean;
import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.livequery.ControlDistribution;
import com.pcs.ztqtj.control.livequery.ControlDistributionBase;
import com.pcs.ztqtj.control.livequery.ControlDistributionHandler;
import com.pcs.ztqtj.control.livequery.ControlDistributionLegend;
import com.pcs.ztqtj.control.livequery.ControlMapBound;
import com.pcs.ztqtj.control.livequery.ControlTyphoonHandler;
import com.pcs.ztqtj.control.livequery.MapElementZIndex;
import com.pcs.ztqtj.control.tool.ZtqLocationTool;
import com.pcs.ztqtj.util.CommonUtil;
import com.pcs.ztqtj.view.activity.livequery.ActivityLiveQuery;
import com.pcs.ztqtj.view.activity.livequery.ActivityLiveQueryDetail;

import java.util.ArrayList;
import java.util.List;

/**
 * 监测预报-实况查询-要素分布图
 */
public class FragmentDistributionMap extends FragmentLiveQueryCommon {

    private TextureMapView mapView;
    protected AMap aMap;

    private ViewGroup layout;
    // 下拉控件
    private View layoutDropDown;
    // 雷达与台风菜单
    private View layoutRadarTyphoon;
    // 下拉菜单和搜索栏父控件
    private View layoutDropSearch;
    private TextView tvTime;
    // 当前栏目
    private ControlDistribution.ColumnCategory currentColumn = ControlDistribution.ColumnCategory.RAIN;

    private ControlDistributionLegend mLegendControl;
    // 地图缩放层级
    private static final float MAP_ZOOM = 9.4f;
    private static final LatLng INIT_LATLNG = new LatLng(39.085100,117.199370);

    // 是否是全国类型
    private boolean isProvince = false;

    private String mType = "";

    private List<ControlDistributionBase> controlList = new ArrayList<>();
    // 当前点击的marker
    private Marker currentClickMarker;
    private ActivityLiveQuery mActivity;

    private ControlMapBound controlMapBound;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_distribution2, null);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (ActivityLiveQuery) activity;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mapView = getView().findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        initView();
        initEvent();
        initData();
    }

    private CheckBox cb_show;
    private LinearLayout layout_menu;

    private void initView() {
        initMap();
        layout = (ViewGroup) getView().findViewById(R.id.layout);
        layoutRadarTyphoon = getView().findViewById(R.id.layout_radar_typhoon);
        layoutDropSearch = getView().findViewById(R.id.layout_drop_search);
        layoutDropDown = getView().findViewById(R.id.layout_drop_down);
        tvTime = (TextView) getView().findViewById(R.id.tv_drop_down);
        cb_show = (CheckBox) getView().findViewById(R.id.cb_show);
        layout_menu = (LinearLayout) getView().findViewById(R.id.layout_menu_drow);
    }

    private void initEvent() {
        // 设置栏目回调
        aMap.setOnMarkerClickListener(onMarkerClickListener);
        aMap.setOnMapClickListener(onMapClickListener);
        aMap.setMapTextZIndex((int) MapElementZIndex.markerZIndex);
        cb_show.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    layout_menu.setVisibility(View.VISIBLE);
                } else {
                    layout_menu.setVisibility(View.GONE);
                }
            }
        });
    }

    private void initData() {
        controlMapBound = new ControlMapBound(getActivity(), aMap, R.color.gray);
        initArguments();
        addLocationMarkerToMap();
        initControls();
        refreshView(mType);
    }

    /**
     * 初始化参数
     */
    private void initArguments() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            isProvince = bundle.getBoolean("isProvince", false);
            mType = bundle.getString("type", "rain");
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        // 回收控制器
        destroyControls();
    }

    private void initMap() {
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.getUiSettings().setRotateGesturesEnabled(false);
        aMap.getUiSettings().setTiltGesturesEnabled(false);
        //aMap.setMapTextZIndex(5);
    }

    public void refreshView(String type) {
//        RadioButton rbRain = (RadioButton) getView().findViewById(rb_rain);
        CheckBox cbSb = getView().findViewById(R.id.rb_sb);
        CheckBox cbGj = getView().findViewById(R.id.rb_gj);
        CheckBox cbDb = getView().findViewById(R.id.rb_db);
        CheckBox cbZd = getView().findViewById(R.id.rb_zd);
        View radar = getView().findViewById(R.id.layout_radar);
        CheckBox cbTyphoon = getView().findViewById(R.id.cb_typhoon);
        if (isProvince) { // 天津内
            cbSb.setVisibility(View.VISIBLE);
            cbGj.setVisibility(View.VISIBLE);
            cbDb.setVisibility(View.VISIBLE);
            cbZd.setVisibility(View.VISIBLE);
            radar.setVisibility(View.VISIBLE);
            cbTyphoon.setVisibility(View.VISIBLE);
            if (controlMapBound != null) {
                controlMapBound.start();
            }
        } else { // 全国
            cbSb.setVisibility(View.VISIBLE);
            cbGj.setVisibility(View.VISIBLE);
            cbDb.setVisibility(View.GONE);
            cbZd.setVisibility(View.GONE);
            radar.setVisibility(View.GONE);
            cbTyphoon.setVisibility(View.GONE);
        }
        if(!CommonUtil.isHaveAuth("2010401")) {
            cbSb.setVisibility(View.GONE);
        }
        if(!CommonUtil.isHaveAuth("2010402")) {
            cbGj.setVisibility(View.GONE);
        }
        if(!CommonUtil.isHaveAuth("2010403")) {
            cbDb.setVisibility(View.GONE);
        }
        if(!CommonUtil.isHaveAuth("2010404")) {
            cbZd.setVisibility(View.GONE);
        }
        if(!CommonUtil.isHaveAuth("2010405")) {
            radar.setVisibility(View.GONE);
        }
        if(!CommonUtil.isHaveAuth("2010406")) {
            cbTyphoon.setVisibility(View.GONE);
        }
        update(ControlDistribution.getColumnType(type));
    }

    private void createControls() {
        mLegendControl = new ControlDistributionLegend(this, mActivity, layout);
        // 图例控制器
        controlList.add(mLegendControl);
        controlList.add(new ControlTyphoonHandler(this, mActivity, layout));
        controlList.add(new ControlDistributionHandler(this, mActivity, layout, aMap, controlMapBound));
    }

    /**
     * 初始化适配器
     */
    private void initControls() {
        createControls();
        for (ControlDistributionBase bean : controlList) {
            bean.init();
        }
    }

    private void updateControls(ControlDistribution.ColumnCategory column) {
        for (ControlDistributionBase bean : controlList) {
            bean.updateView(column);
        }
    }

    /**
     * 执行控制器中清除
     */
    private void clearControls() {
        for (ControlDistributionBase bean : controlList) {
            bean.clear();
        }
    }

    /**
     * 回收控制器
     */
    private void destroyControls() {
        for (ControlDistributionBase bean : controlList) {
            bean.destroy();
        }
    }

    /**
     * 设置栏目的控件状态
     *
     * @param column
     */
    private void setColumnViewStatus(ControlDistribution.ColumnCategory column) {
        switch (column) {
            case RAIN: // 雨量
                layoutDropSearch.setVisibility(View.VISIBLE);
                layoutRadarTyphoon.setVisibility(View.VISIBLE);
                layoutDropDown.setVisibility(View.VISIBLE);
                break;
            case TEMPERATURE: // 气温
                layoutDropSearch.setVisibility(View.VISIBLE);
                layoutRadarTyphoon.setVisibility(View.GONE);
                layoutDropDown.setVisibility(View.VISIBLE);
                break;
            case WIND: // 风速
                layoutDropSearch.setVisibility(View.VISIBLE);
                layoutRadarTyphoon.setVisibility(View.GONE);
                layoutDropDown.setVisibility(View.VISIBLE);
                break;
            case VISIBILITY: // 能见度
                layoutDropSearch.setVisibility(View.VISIBLE);
                layoutRadarTyphoon.setVisibility(View.GONE);
                layoutDropDown.setVisibility(View.VISIBLE);
                break;
            case PRESSURE: // 气压
                layoutDropSearch.setVisibility(View.VISIBLE);
                layoutRadarTyphoon.setVisibility(View.GONE);
                layoutDropDown.setVisibility(View.VISIBLE);
                break;
            case HUMIDITY: // 相对湿度
                layoutDropSearch.setVisibility(View.VISIBLE);
                layoutRadarTyphoon.setVisibility(View.GONE);
                layoutDropDown.setVisibility(View.VISIBLE);
                break;
        }
        tvTime.setText("");
    }

    /**
     * 清除所有
     */
    private void clearAll() {
        // 控制器清除
        clearControls();
    }

    /**
     * 跳转站点详情页
     */
    private void gotoQueryDetail(ControlDistribution.ColumnCategory column, String stationName) {
        if (!TextUtils.isEmpty(stationName)) {
            Intent intent = new Intent(getActivity(), ActivityLiveQueryDetail.class);
            intent.putExtra("stationName", stationName);
            intent.putExtra("item", ControlDistribution.getColumnName(column));
            startActivity(intent);
        }
    }

    /**
     * 添加定位点
     */
    private void addLocationMarkerToMap() {
        LatLng latLng = ZtqLocationTool.getInstance().getLatLng();
        if (latLng != null) {
            Bitmap icon = resizeIcon(R.drawable.icon_location, 0.5f);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions
                    .icon(BitmapDescriptorFactory.fromBitmap(icon))
                    .anchor(0.5f, 0.5f)
                    //.zIndex(MapElementZIndex.markerZIndex)
                    .position(latLng);
            Marker locationMarker = aMap.addMarker(markerOptions);
            aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(INIT_LATLNG, MAP_ZOOM));
        }
    }

    /**
     * 放大图标
     *
     * @param resid
     * @return
     */
    private Bitmap resizeIcon(int resid, float scale) {
        Bitmap icon = BitmapFactory.decodeResource(getResources(), resid);
        int width = (int) (icon.getWidth() * scale);
        int height = (int) (icon.getHeight() * scale);
        Bitmap resizeBitmap = Bitmap.createScaledBitmap(icon, width, height, false);
        return resizeBitmap;
    }

    /**
     * @return
     */
    public AMap getMap() {
        return aMap;
    }

    /**
     * 获取当前栏目
     *
     * @return
     */
    public ControlDistribution.ColumnCategory getCurrentColumn() {
        return currentColumn;
    }

    /**
     * 隐藏图例
     */
    public void hideLegend() {
        mLegendControl.clear();
    }

    /**
     * 显示图例
     */
    public void showLegend() {
        mLegendControl.show();
    }

    /**
     * 获取是否是全国类型
     *
     * @return
     */
    public boolean getIsProvince() {
        return isProvince;
    }

    private void update(ControlDistribution.ColumnCategory column) {
        currentColumn = column;
        // 清除地图上所有数据
        clearAll();
        // 设置当前点击的栏目的控件状态
        setColumnViewStatus(currentColumn);
        // 刷新控制器
        updateControls(currentColumn);
    }

    AMap.OnMarkerClickListener onMarkerClickListener = new AMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            FycxFbtBean bean = (FycxFbtBean) marker.getObject();
            if (bean != null) {
                gotoQueryDetail(currentColumn, bean.sta_name);
            }
            if (!TextUtils.isEmpty(marker.getSnippet())) {
                marker.showInfoWindow();
                currentClickMarker = marker;
            }

            return true;
        }
    };

    AMap.OnMapClickListener onMapClickListener = new AMap.OnMapClickListener() {
        @Override
        public void onMapClick(LatLng latLng) {
            if (currentClickMarker != null && currentClickMarker.isInfoWindowShown()) {
                currentClickMarker.hideInfoWindow();
            }
        }
    };

    @Override
    public void refleshData() {
        updateControls(currentColumn);
    }
}
