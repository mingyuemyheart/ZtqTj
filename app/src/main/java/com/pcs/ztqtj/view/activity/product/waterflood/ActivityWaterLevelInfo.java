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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
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
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.LatLngBounds.Builder;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.waterflood.AdapterPopWindow;
import com.pcs.ztqtj.control.adapter.waterflood.AdapterWaterOverInfo;
import com.pcs.ztqtj.control.tool.ShareTools;
import com.pcs.ztqtj.control.tool.ZtqImageTool;
import com.pcs.ztqtj.control.tool.ZtqLocationTool;
import com.pcs.ztqtj.control.tool.youmeng.ShareUtil;
import com.pcs.ztqtj.view.activity.FragmentActivityWithShare;
import com.pcs.ztqtj.view.fragment.weatherflood.FragmentWeatherList;
import com.pcs.ztqtj.view.fragment.weatherflood.FragmentWeatherView;
import com.pcs.lib.lib_pcs_v3.control.tool.BitmapUtil;
import com.pcs.lib.lib_pcs_v3.control.tool.Util;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackShareAboutDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.waterflood.PackReservoirWaterInfoUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.waterflood.PackRiverWaterInfoUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.waterflood.PackWaterInfoAllDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.waterflood.PackWaterInfoAllUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.waterflood.PackWaterInfoOverAllDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.waterflood.PackWaterInfoOverAllUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.waterflood.WaterInfo;
import com.pcs.lib_ztqfj_v2.model.pack.net.waterflood.WaterOverInfo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 水位信息
 *
 * @author tya
 *
 */
public class ActivityWaterLevelInfo extends FragmentActivityWithShare implements
        OnClickListener, OnMarkerClickListener {

    // UI
    /**
     * 高德地图控件
     */
    private MapView mMapView = null;

    /**
     *
     */
    private CheckBox change_view;

    /**
     *
     */
    private RelativeLayout view_list_layout;

    /**
     *
     */
    private ImageView gone_view;

    /**
     * 水库站按钮
     */
    private RadioButton rbReservoir = null;

    /**
     * 河道站按钮
     */
    private RadioButton rbRiver = null;

    /**
     * 图例按钮
     */
    private Button btnLegend = null;

    /**
     * 超汛按钮
     */
    private Button btnOver = null;

    /**
     * 选择时间
     */
    private Button btnChooseTime = null;

    /**
     * 显示时间
     */
    private TextView tvTimeSlot = null;
    /**
     * 选择站点
     */
    private TextView station_name = null;

    /**
     * 选择时间的布局
     */
    private LinearLayout mChooseTimeLayout = null;

    /**
     * 主布局
     */
    private RelativeLayout mMainLayout = null;

    /**
     * 图例布局
     */
    private LinearLayout mLegendLayout = null;

    /**
     * 超汛限/超警戒布局
     */
    private LinearLayout mOverLayout = null;

    /**
     * 选择时间的日期控件
     */
    private TextView tvDate = null;

    /**
     * 选择时间的时间控件
     */
    private TextView tvTime = null;

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
     * 设置的时间
     */
    private Date mDate = new Date();

    /**
     * 是否是河道（河道或水库） ,默认是
     */
    public boolean isWeather = true;

    /**
     * 站点ID
     */
    public String station_id = "";

    /**
     * 地图上所有的marker
     */
    private List<Marker> markerList = new ArrayList<Marker>();

    /**
     * 上一个被点击的marker
     */
    private Marker lastClickedMarker = null;

    /**
     * 上一个被点击的marker的图标
     */
    private BitmapDescriptor lastClickedMarkerIcon = null;

    /**
     * 超汛限marker
     */
    private Marker overFloodMarker = null;

    /**
     * 是否可以点击地图
     */
    private boolean isClickable = true;

    /**
     * 站点河道站信息上传包
     */
    private PackRiverWaterInfoUp riverUp = new PackRiverWaterInfoUp();

    /**
     * 站点水库站信息上传包
     */
    private PackReservoirWaterInfoUp reservoirUp = new PackReservoirWaterInfoUp();

    /**
     * 水位信息上传包
     */
    private PackWaterInfoAllUp allWaterUp = new PackWaterInfoAllUp();

    /**
     * 水位信息下载包
     */
    private PackWaterInfoAllDown allWaterDown = new PackWaterInfoAllDown();

    /**
     * 超汛限上传包
     */
    private PackWaterInfoOverAllUp overUp = new PackWaterInfoOverAllUp();

    /**
     * 超汛限下载包
     */
    private PackWaterInfoOverAllDown overDown = new PackWaterInfoOverAllDown();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_level_info);
        setTitleText(R.string.title_water_level_info);
        initView();
        mMapView.onCreate(savedInstanceState);
        initEvent();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
        setOverButtonText();
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

    /**
     * 初始化UI
     */
    private void initView() {
        mMapView = (MapView) findViewById(R.id.map);
        station_name = (TextView) findViewById(R.id.station_name);
        change_view = (CheckBox) findViewById(R.id.change_view);
        gone_view = (ImageView) findViewById(R.id.gone_view);
        view_list_layout = (RelativeLayout) findViewById(R.id.view_list_layout);
        rbReservoir = (RadioButton) findViewById(R.id.rb_reservoir_info);
        rbRiver = (RadioButton) findViewById(R.id.rb_river_info);
        btnLegend = (Button) findViewById(R.id.btn_legend);
        btnChooseTime = (Button) findViewById(R.id.btn_choose_time);
        btnOver = (Button) findViewById(R.id.btn_over);
        tvTimeSlot = (TextView) findViewById(R.id.tv_time_slot);
        mMainLayout = (RelativeLayout) findViewById(R.id.layout_main);
        mChooseTimeLayout = (LinearLayout) LayoutInflater.from(this).inflate(
                R.layout.layout_water_level_timeslot, null);
        mLegendLayout = (LinearLayout) LayoutInflater.from(this).inflate(
                R.layout.layout_water_info_legend, null);
        mOverLayout = (LinearLayout) LayoutInflater.from(this).inflate(
                R.layout.layout_water_info_over, null);
        initChooseTimeView();
    }

    /**
     * 初始化事件
     */
    private void initEvent() {
        gone_view.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                view_list_layout.setVisibility(View.GONE);
                // 所有按钮可以点击
                setAllButtonClickable(true);
            }
        });
        change_view.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    // 列表
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fra_content,
                                    new FragmentWeatherList()).commit();
                } else {
                    // 趋势图
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fra_content,
                                    new FragmentWeatherView()).commit();
                }
            }
        });

        rbReservoir.setOnClickListener(this);
        rbRiver.setOnClickListener(this);
        btnLegend.setOnClickListener(this);
        btnChooseTime.setOnClickListener(this);
        btnOver.setOnClickListener(this);
        setBackListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (view_list_layout.getVisibility() == View.VISIBLE) {
                    view_list_layout.setVisibility(View.GONE);
                    setAllButtonClickable(true);
                } else {
                    finish();
                }
            }
        });
        setShareListener(this);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        // 注册广播接收
        PcsDataBrocastReceiver.registerReceiver(this, mReceiver);
        initMap();
        mDate = new Date(System.currentTimeMillis());
        setTimeSlotString(mDate);

        // 请求初始数据
        reqWaterInfo();
    }

    /**
     * 设置超汛限按钮显示名
     */
    private void setOverButtonText() {
        switch (getButtonSTATUS()) {
            case 0: // 水库站信息
                btnOver.setText(getString(R.string.over_flood));
                break;
            case 1: // 河道站信息
                btnOver.setText(getString(R.string.over_alert));
                break;
        }
    }

    public void setStationName(String str) {
        if (station_name != null) {
            station_name.setText(str);
        }
    }

    private void initChooseTimeView() {
        tvDate = (TextView) mChooseTimeLayout.findViewById(R.id.tv_total_date);
        tvTime = (TextView) mChooseTimeLayout.findViewById(R.id.tv_total_time);
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

    /**
     * 地图事件
     */
    private void initMapEvent() {
        aMap.setOnMarkerClickListener(this);
    }

    /**
     * 定位
     */
    private void location() {
        LatLng latLng = ZtqLocationTool.getInstance().getLatLng();
        if (latLng != null) {
            aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,
                    MAP_LEVEL));
        }
    }

    /**
     * 通过Date设置时间字符串
     *
     * @param date
     */
    private void setTimeSlotString(Date date) {
        if (date == null) {
            return;
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd H时");
        String time = format.format(date);
        tvTimeSlot.setText(time);
    }

    /**
     * 获取当前雨量查询状态
     *
     * @return 返回0则状态为水库，返回1则状态为河道,返回-1则错误
     */
    public int getButtonSTATUS() {
        if (rbReservoir == null || rbRiver == null) {
            return -1;
        }
        if (rbReservoir.isChecked()) {
            return 0;
        } else {
            return 1;
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
     * 关闭所有布局
     */
    private void closeAllLayout() {
        if (mChooseTimeLayout != null) {
            closeView(mChooseTimeLayout);
        }
        if (mLegendLayout != null) {
            closeView(mLegendLayout);
        }
        if (mOverLayout != null) {
            closeView(mOverLayout);
        }
    }

    /**
     * 关闭其他页面
     */
    private void closeOtherLayout(View layout) {
        if (mChooseTimeLayout != null && !mChooseTimeLayout.equals(layout)) {
            closeView(mChooseTimeLayout);
        }
        if (mLegendLayout != null && !mLegendLayout.equals(layout)) {
            closeView(mLegendLayout);
        }
        if (mOverLayout != null && !mOverLayout.equals(layout)) {
            closeView(mOverLayout);
        }
    }

    /**
     * 显示时间选择布局
     */
    private void showChooseTimeLayout() {

        // 如果没显示布局就显示，反之则关闭
        if (!mChooseTimeLayout.isShown()) {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.BELOW, R.id.ll_time_slot);
            params.setMargins(0, 10, 0, 0);
            mMainLayout.addView(mChooseTimeLayout, params);
        } else {
            closeView(mChooseTimeLayout);
            return;
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat hour = new SimpleDateFormat("H");
        String strDate = format.format(mDate);
        String strTime = hour.format(mDate);

        tvDate.setOnClickListener(new MyClickListener(getFiveDayDateList()));
        tvTime.setOnClickListener(new MyClickListener(getHoursList(strDate)));

        tvDate.setText(strDate);
        tvTime.setText(strTime);

        setTimeSlotString(mDate);

        // 确定键
        Button btn = (Button) mChooseTimeLayout.findViewById(R.id.btn_sure);
        btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                procOKButton();
            }
        });
    }

    /**
     * 显示图例布局
     */
    private void showLegendLayout() {
        // 如果没显示布局就显示，反之则关闭
        if (!mLegendLayout.isShown()) {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ABOVE, R.id.ll_bottom_button);
            params.setMargins(0, 10, 0, 0);
            mMainLayout.addView(mLegendLayout, params);
        } else {
            closeView(mLegendLayout);
        }
    }

    /**
     * 显示超汛限/超警戒布局
     */
    private void showOverLayout() {
        // 如果没显示布局就显示，反之则关闭
        if (!mOverLayout.isShown()) {
            LinearLayout ll = (LinearLayout) findViewById(R.id.ll_bottom_button);
            RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) ll
                    .getLayoutParams();
            int bottomHeight = p.height + p.bottomMargin * 2;
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.BELOW, R.id.ll_time_slot);
            params.setMargins(0, 0, 0, bottomHeight);
            mMainLayout.addView(mOverLayout, params);
        } else {
            closeView(mOverLayout);
            return;
        }
        ListView listView = (ListView) mOverLayout.findViewById(R.id.lv_over);
        TextView tvName = (TextView) mOverLayout.findViewById(R.id.tv_name);
        TextView tvOver = (TextView) mOverLayout.findViewById(R.id.tv_over);
        AdapterWaterOverInfo adapter = new AdapterWaterOverInfo(this,
                overDown.waterinfo_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                closeAllLayout();
                if (lastClickedMarker != null) {
                    lastClickedMarker.setIcon(lastClickedMarkerIcon);
                }
                WaterOverInfo woi = overDown.waterinfo_list.get(position);
                String station_id = woi.station_id;
                if (!TextUtils.isEmpty(woi.latitude)
                        && !TextUtils.isEmpty(woi.longitude)) {
                    double lat = Double.parseDouble(woi.latitude);
                    double lng = Double.parseDouble(woi.longitude);
                    LatLng latLng = new LatLng(lat, lng);
                    // 移动镜头
                    moveCamera(latLng);
                    // 移除防汛站点标记
                    moveMarkerOnMap(overFloodMarker);
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(latLng)
                            .anchor(0.5f, 0.5f)
                            .icon(BitmapDescriptorFactory
                                    .fromResource(R.drawable.icon_typhoon_location));
                    overFloodMarker = aMap.addMarker(markerOptions);
                }
                switch (getButtonSTATUS()) {
                    case 0: { // 水库站
                        reqReservoir(station_id);
                        break;
                    }
                    case 1: { // 河道站
                        requestRiver(station_id);
                        break;
                    }
                }
            }
        });
        switch (getButtonSTATUS()) {
            case 0: { // 水库站信息
                tvName.setText("水库站名称");
                tvOver.setText("超汛限m");
                break;
            }
            case 1: { // 河道站信息
                tvName.setText("河道站名称");
                tvOver.setText("超警戒m");
                break;
            }
        }

    }

    /**
     * 获取今天加过去五天的日期列表
     *
     * @return 今天加过去五天的日期列表
     */
    private List<String> getFiveDayDateList() {
        List<String> result = new ArrayList<String>();
        Calendar endCal = Calendar.getInstance();
        for (int i = 0; i < 6; i++) {
            Calendar cal = Calendar.getInstance();
            int day = cal.get(Calendar.DATE);
            cal.set(Calendar.DATE, day - i);
            Date date = new Date(cal.getTimeInMillis());
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String str = format.format(date);
            result.add(str);
        }
        return result;
    }

    /**
     * 获取24小时时间列表
     *
     * @return 24小时时间列表
     */
    private List<String> get24HoursTimeList() {
        List<String> result = new ArrayList<String>();
        for (int i = 1; i <= 24; i++) {
            result.add(String.valueOf(i));
        }
        return result;
    }

    /**
     * 获取时间列表
     *
     * @return
     */
    private List<String> getHoursList(String time) {
        int endHour = 0;
        List<String> hourList = new ArrayList<String>();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        // 获取今天时间
        Date todayDate = new Date(System.currentTimeMillis());
        String todayTime = format.format(todayDate);
		/*
		 * 如果日期的textview时间是今天就返回当前时间的时间列表 比如现在是13点，则返回1-13的列表
		 */
        if (time.equals(todayTime)) {
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH");
            endHour = Integer.parseInt(timeFormat.format(todayDate));
        } else {
            endHour = 23;
        }
        for (int i = 0; i <= endHour; i++) {
            hourList.add(String.valueOf(i));
        }
        return hourList;
    }

    /**
     * 处理确定键
     */
    private void procOKButton() {
        closeAllLayout();
        clearMarker();
        String strDate = tvDate.getText().toString();
        String strTime = tvTime.getText().toString();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-ddH");
        Date date = null;
        try {
            date = format.parse(strDate + strTime);
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(this, "错误！", Toast.LENGTH_LONG).show();
            return;
        }
        mDate = date;
        closeView(mChooseTimeLayout);
        setTimeSlotString(mDate);
        reqWaterInfo();
    }

    /**
     * 弹出日期选择框
     */
    private void createPopupWindow(final TextView dropDownView,
                                   final List<String> dataeaum) {
        AdapterPopWindow dataAdapter = new AdapterPopWindow(this, dataeaum);
        View popcontent = LayoutInflater.from(this).inflate(
                R.layout.pop_list_layout, null);
        ListView lv = (ListView) popcontent.findViewById(R.id.mylistviw);
        lv.setAdapter(dataAdapter);
        final PopupWindow pop = new PopupWindow(this);
        pop.setContentView(popcontent);
        pop.setOutsideTouchable(false);
        pop.setWidth(dropDownView.getWidth());
        // 调整下拉框长度
        int screenHight = Util.getScreenHeight(this);
        if (dataeaum.size() < 9) {
            pop.setHeight(LayoutParams.WRAP_CONTENT);
        } else {
            pop.setHeight((int) (screenHight * 0.6));
        }
        pop.setFocusable(true);
        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                pop.dismiss();
                dropDownView.setText(dataeaum.get(position));

                tvTime.setOnClickListener(new MyClickListener(
                        getHoursList(tvDate.getText().toString())));
            }
        });
        pop.showAsDropDown(dropDownView);
    }

    /**
     * 在地图上加点
     *
     * @param list
     */
    private void addMarkerOnMap(List<WaterInfo> list) {
        if (list == null) {
            return;
        }
        Builder builder = new Builder();
        clearMarker();
        float allLat = 0f;
        float allLog = 0f;
        for (int i = 0; i < list.size(); i++) {
            WaterInfo info = list.get(i);
            String color = info.color;
            if (TextUtils.isEmpty(info.lat) || TextUtils.isEmpty(info.log)) {
                continue;
            }
            double lat = Double.parseDouble(info.lat);
            double log = Double.parseDouble(info.log);
            LatLng latLng = new LatLng(lat, log);
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .anchor(0.5f, 0.5f)
                    .icon(BitmapDescriptorFactory
                            .fromResource(getIconID(color)));
            addMarker(markerOptions, info);
            builder.include(latLng);
            allLat += lat;
            allLog += log;
        }
        LatLng latLng = new LatLng(allLat / list.size(), allLog / list.size());
        // int maxZoom = (int) aMap.getMaxZoomLevel();
        // aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(),
        // maxZoom));
        aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 7));
    }

    /**
     * 添加marker
     *
     * @param markerOptions
     */
    private void addMarker(MarkerOptions markerOptions, WaterInfo info) {
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
     * 通过颜色名获取颜色ID
     *
     * @param color
     * @return
     */
    private int getIconID(String color) {
        int result = R.drawable.icon_water_info_green;
        if (color.equals("G")) {
            result = R.drawable.icon_water_info_green;
        } else if (color.equals("O")) {
            result = R.drawable.icon_water_info_orange;
        } else if (color.equals("P")) {
            result = R.drawable.icon_water_info_purple;
        }
        return result;
    }

    /**
     * 移动摄像机
     *
     * @param latLng
     *            坐标
     */
    private void moveCamera(LatLng latLng) {
        float currentLevel = aMap.getCameraPosition().zoom;
        aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,
                currentLevel));
    }

    /**
     * 通过marker获取气象站ID
     *
     * @param marker
     * @return
     */
    private String getStationIdOnMap(Marker marker) {
        WaterInfo info = (WaterInfo) marker.getObject();
        selectMarker(marker);
        return info.station_id;
    }

    /**
     * 选中移动并标记marker
     */
    private void selectMarker(Marker marker) {
        // 移除防汛站点标记
        moveMarkerOnMap(overFloodMarker);
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
     * 从地图上移除makrer
     *
     * @param marker
     */
    private void moveMarkerOnMap(Marker marker) {
        if (marker != null) {
            marker.remove();
        }
    }

    /**
     * 设置所有的按钮是否可以点击
     *
     * @param value
     */
    private void setAllButtonClickable(boolean value) {
        rbReservoir.setClickable(value);
        rbRiver.setClickable(value);
        btnLegend.setClickable(value);
        btnChooseTime.setClickable(value);
        btnOver.setClickable(value);
        isClickable = value;
    }

    /**
     * 水库站水位信息
     *
     * @param station_id
     */
    private void reqReservoir(String station_id) {

        if(!isOpenNet()){
            showToast(getString(R.string.net_err));
            return ;
        }
        showProgressDialog();
        reservoirUp = new PackReservoirWaterInfoUp();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHH");
        String time = format.format(mDate);
        this.station_id = station_id;
        reservoirUp.time = time;
        reservoirUp.station_id = station_id;
        PcsDataDownload.addDownload(reservoirUp);
    }

    /**
     * 站点河道站水位信息
     */
    private void requestRiver(String station_id) {

        if(!isOpenNet()){
            showToast(getString(R.string.net_err));
            return ;
        }
        showProgressDialog();
        riverUp = new PackRiverWaterInfoUp();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHH");
        String time = format.format(mDate);
        this.station_id = station_id;
        riverUp.time = time;
        riverUp.station_id = station_id;
        PcsDataDownload.addDownload(riverUp);
    }

    /**
     * 请求所有水库站数据(type=3为水库)
     */
    private void reqAllReservoir() {

        if(!isOpenNet()){
            showToast(getString(R.string.net_err));
            return ;
        }
        showProgressDialog();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHH");
        String time = format.format(mDate);
        allWaterUp = new PackWaterInfoAllUp();
        allWaterUp.time = time;
        allWaterUp.type = "3";
        PcsDataDownload.addDownload(allWaterUp);
    }

    /**
     * 请求水库或河道数据
     */
    private void reqWaterInfo() {
        switch (getButtonSTATUS()) {
            case 0: // 水库站信息
                btnOver.setText(getString(R.string.over_flood));
                reqAllReservoir();
                break;
            case 1: // 河道站信息
                btnOver.setText(getString(R.string.over_alert));
                reqAllRiver();
                break;
        }
    }

    /**
     * 请求所有河道站数据(type=1为河道)
     */
    private void reqAllRiver() {

        if(!isOpenNet()){
            showToast(getString(R.string.net_err));
            return ;
        }
        showProgressDialog();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHH");
        String time = format.format(mDate);
        allWaterUp = new PackWaterInfoAllUp();
        allWaterUp.time = time;
        allWaterUp.type = "1";
        PcsDataDownload.addDownload(allWaterUp);
    }

    /**
     * 请求水库超汛限数据
     */
    private void reqOverReservoir() {

        if(!isOpenNet()){
            showToast(getString(R.string.net_err));
            return ;
        }
        showProgressDialog();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHH");
        String time = format.format(mDate);
        overUp = new PackWaterInfoOverAllUp();
        overUp.time = time;
        overUp.type = "3";
        PcsDataDownload.addDownload(overUp);
    }

    /**
     * 请求河道超汛限数据
     */
    private void reqOverRiver() {

        if(!isOpenNet()){
            showToast(getString(R.string.net_err));
            return ;
        }
        showProgressDialog();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHH");
        String time = format.format(mDate);
        overUp = new PackWaterInfoOverAllUp();
        overUp.time = time;
        overUp.type = "1";
        PcsDataDownload.addDownload(overUp);
    }

    /**
     * 请求河道或水库超汛限数据
     */
    private void reqOverDate() {
        switch (getButtonSTATUS()) {
            case 0: // 水库站信息
                reqOverReservoir();
                break;
            case 1: // 河道站信息
                reqOverRiver();
                break;
        }
    }

    /**
     * 高德地图marker点击回调
     */
    @Override
    public boolean onMarkerClick(Marker marker) {
        if (isClickable) {
            closeAllLayout();
            String station_id = getStationIdOnMap(marker);
            switch (getButtonSTATUS()) {
                case 0: { // 水库站
                    reqReservoir(station_id);
                    break;
                }
                case 1: { // 河道站
                    requestRiver(station_id);
                    break;
                }
            }
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_right:
                aMap.getMapScreenShot(mScreenShotListener);
                break;
            case R.id.rb_reservoir_info:
            case R.id.rb_river_info:
                // 河道信息
                clearMarker();
                closeAllLayout();
                reqWaterInfo();
                break;
            case R.id.btn_over:
                closeOtherLayout(mOverLayout);
                if (!mOverLayout.isShown()) {
                    reqOverDate();
                } else {
                    closeView(mOverLayout);
                }
                break;
            case R.id.btn_choose_time:
                closeOtherLayout(mChooseTimeLayout);
                showChooseTimeLayout();
                break;
            case R.id.btn_legend:
                closeOtherLayout(mLegendLayout);
                showLegendLayout();
                break;
        }
    }

    /**
     * 点击下拉菜单回调类
     *
     * @author tya
     *
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
            createPopupWindow((TextView) v, dataList);
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
     * 数据更新广播接收
     *
     */
    private class MyReceiver extends PcsDataBrocastReceiver {
        @Override
        public void onReceive(String name, String error) {
            if (riverUp != null && TextUtils.isEmpty(error)) {
                if (name.equals(riverUp.getName())
                        || name.equals(reservoirUp.getName())) {
                    dismissProgressDialog();
                    if (view_list_layout.getVisibility() == View.GONE
                            || view_list_layout.getVisibility() == View.INVISIBLE) {
                        view_list_layout.setVisibility(View.VISIBLE);
                    }
                    // 数据接受完成
                    change_view.setChecked(false);
                    // 设置所有按钮不能点击
                    setAllButtonClickable(false);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fra_content,
                                    new FragmentWeatherView()).commit();
                }

                // 所有站点的水位信息
                if (name.equals(allWaterUp.getName())) {
                    dismissProgressDialog();
                    allWaterDown = (PackWaterInfoAllDown) PcsDataManager.getInstance().getNetPack(name);
                    if (allWaterDown == null) {
                        return;
                    }
                    if (allWaterDown.waterinfo_list.size() == 0) {
                        Toast.makeText(ActivityWaterLevelInfo.this, "无数据！",
                                Toast.LENGTH_LONG).show();
                        return;
                    }
                    addMarkerOnMap(allWaterDown.waterinfo_list);
                }

                // 超汛限数据
                if (name.equals(overUp.getName())) {
                    dismissProgressDialog();
                    overDown = (PackWaterInfoOverAllDown) PcsDataManager.getInstance().getNetPack(name);
                    if (overDown == null) {
                        return;
                    }
                    if (overDown.waterinfo_list.size() == 0) {
                        switch (getButtonSTATUS()) {
                            case 0: // 水库
                                Toast.makeText(ActivityWaterLevelInfo.this,
                                        "超汛限水库站0个", Toast.LENGTH_LONG).show();
                                break;
                            case 1: // 河道
                                Toast.makeText(ActivityWaterLevelInfo.this,
                                        "超警戒河道站0个", Toast.LENGTH_LONG).show();
                                break;
                        }

                        return;
                    }
                    showOverLayout();
                }
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
                    shareMap(ActivityWaterLevelInfo.this);
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
        if (view_list_layout != null
                && view_list_layout.getVisibility() == View.INVISIBLE) {
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
            Bitmap bm = ZtqImageTool.getInstance().getScreenBitmapNew(ActivityWaterLevelInfo.this);

            int[] location = new int[2];
            mMapView.getLocationOnScreen(location);

            mShareBitmap = procImage(mAmapBitmap, bm, location[1]);
            mShareBitmap = ZtqImageTool.getInstance().stitchQR(ActivityWaterLevelInfo.this, mShareBitmap);
            ShareTools.getInstance(ActivityWaterLevelInfo.this).setShareContent(getTitleText(), getTitleText(), mShareBitmap,"0").showWindow(layout);
        }

        @Override
        public void onMapScreenShot(Bitmap bitmap, int i) {

        }
    };

}
