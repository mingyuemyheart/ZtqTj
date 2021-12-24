package com.pcs.ztqtj.view.activity.product.typhoon;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Contacts;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.CancelableCallback;
import com.amap.api.maps.AMap.OnMapScreenShotListener;
import com.amap.api.maps.AMap.OnMapTouchListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.pcs.lib.lib_pcs_v3.control.tool.BitmapUtil;
import com.pcs.lib.lib_pcs_v3.control.tool.Util;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.net.column.ColumnInfo;
import com.pcs.lib_ztqfj_v2.model.pack.net.column.PackColumnDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.column.PackColumnUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.typhoon.TyphoonInfo;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.AdapterTyphoonList;
import com.pcs.ztqtj.control.adapter.AdapterTyphoonList.Holder;
import com.pcs.ztqtj.control.adapter.typhoon.AdapterTyphoonDate;
import com.pcs.ztqtj.control.tool.ControlTyphoon;
import com.pcs.ztqtj.control.tool.ShareTools;
import com.pcs.ztqtj.control.tool.ZtqImageTool;
import com.pcs.ztqtj.control.tool.ZtqLocationTool;
import com.pcs.ztqtj.control.tool.youmeng.ShareUtil;
import com.pcs.ztqtj.view.activity.FragmentActivityWithShare;
import com.pcs.ztqtj.view.myview.MyListView;
import com.pcs.ztqtj.view.myview.typhoon.DistanceView;
import com.pcs.ztqtj.view.myview.typhoon.TyphoonTrueView;
import com.pcs.ztqtj.view.myview.typhoon.TyphoonView;

import java.util.ArrayList;
import java.util.List;

/**
 * 监测预报-台风路径
 */
@SuppressWarnings("deprecation")
@SuppressLint({"HandlerLeak", "InflateParams"})
public class ActivityTyphoon extends FragmentActivityWithShare implements OnClickListener, CompoundButton.OnCheckedChangeListener {

    /**
     * 台风控制器
     **/
    private ControlTyphoon mControl;

    /**
     * 地图控件
     **/
    private AMap mAMap;

    /**
     * 地图
     **/
    private MapView mMapView;

    /**
     * 下拉框按钮
     **/
    private Button btnSpinner;

    /**
     * 台风列表弹出窗
     **/
    private PopupWindow mTyphoonListWindow;

    /**
     * 详情展开/收起按钮
     **/
    private View arrowUp, arrowDown;
    private View btn_road_more_up, btn_road_more_down;
    /**
     * 详情布局
     **/
    private View layoutDetail;

    /**
     * 台风详情文本
     **/
    private TextView tvDetailTime, tvDetailContent;

    /**
     * 播放/暂停按钮
     */
    private CheckBox cbPlayPause;

    /**
     * 台风列表数据适配器
     **/
    public AdapterTyphoonList mAdapter;

    /**
     * 定位符号
     **/
    private Marker locationMarker;

    /**
     * 测距视图
     **/
    private DistanceView distanceView;

    /**
     * 当前台风编号
     **/
    public String localCode = "";

    /**
     * 默认缩放
     **/
    public static final float DEFAULT_ZOOM = 5.5f;

    /**
     * 播放路径：下一个
     **/
    private final int WHAT_PLAY_NEXT = 102;

    /**
     * 最大值：台风显示数量
     */
    private final int MAX_COUNT_TYPHOON = 3;

    /**
     * 播放间隔
     **/
    private static final int PLAY_INTERVAL = 500;

    /**
     * 台风云图时间列表
     */
    private List<String> dateList = new ArrayList<>();
    private MyListView lvDate;
    private AdapterTyphoonDate dateAdapter;

    private LinearLayout lay_typhoon_left;
    private boolean isTyphoonSel = false;
    private LinearLayout btn_road_more;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_typhoon);

        initParam();
        initMap(savedInstanceState);
        initView();
        resetView();
        resetLocation();

        mControl = new ControlTyphoon(this, mAMap);
        mControl.registerReceiver();
        mControl.init();
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        mControl.unregisterReceiver();
        PcsDataBrocastReceiver.unregisterReceiver(this, mReceiver);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.btn_right:
//                if (isPlay()) {
//                    clickPause();
//                }
//                hideDistance();
//                hideInfoWindow();
//                clickMailList();// 通讯录
//                break;
            case R.id.btn_choice_typhoon:
            case R.id.btn_typhoon_list:
                if (isPlay()) {
                    clickPause();
                }
                hideDistance();
                is_Distance=true;
                hideInfoWindow();
                clickSpinner();// 选择台风
                break;
            case R.id.layout_detail:
                clickTyphoonDetail();// 详情
                break;
            case R.id.btn_map_switch:
                clickMapSwitch();// 切换
                break;
            case R.id.btn_alarm_list:
                // 警报单
                clickTyphoonBillList();
                break;
            case R.id.btn_road_more:
                //台风路径
                //clickTyphoonRoad();
                if (isTyphoonSel) {
                    lay_typhoon_left.setVisibility(View.VISIBLE);
                    isTyphoonSel = false;
                    btn_road_more_up.setVisibility(View.GONE);
                    btn_road_more_down.setVisibility(View.VISIBLE);
                } else {
                    lay_typhoon_left.setVisibility(View.GONE);
                    isTyphoonSel = true;
                    btn_road_more_up.setVisibility(View.VISIBLE);
                    btn_road_more_down.setVisibility(View.GONE);

                }
                //popTyphoon.showAtLocation(btn_road_more,  Gravity.BOTTOM|Gravity.LEFT, 20,btn_road_more.getHeight()
                // +20);
                break;

        }
    }


    /**
     * 点击台风警报单
     */
    private void clickTyphoonBillList() {
        Intent intent = new Intent(this, ActivityTyphoonBillList.class);
        startActivity(intent);
    }

    private PackColumnUp packYjZqColumnUp = new PackColumnUp();

    //台风路径请求
    public void req() {
        showProgressDialog();
        packYjZqColumnUp.column_type = "25";
        PcsDataBrocastReceiver.registerReceiver(this, mReceiver);
        PcsDataDownload.addDownload(packYjZqColumnUp);
    }

    public List<ColumnInfo> arrcolumnInfo = new ArrayList<>();
    private PcsDataBrocastReceiver mReceiver = new PcsDataBrocastReceiver() {
        @Override
        public void onReceive(String nameStr, String errorStr) {
            if (TextUtils.isEmpty(nameStr)) {
                return;
            }
            if (nameStr.equals(packYjZqColumnUp.getName())) {
                dismissProgressDialog();
                PackColumnDown packDowns = (PackColumnDown) PcsDataManager.getInstance().getNetPack(nameStr);
                if (packDowns == null) {
                    return;
                }
                arrcolumnInfo.clear();
                arrcolumnInfo.addAll(packDowns.arrcolumnInfo);
                if (arrcolumnInfo.size() == 0) {
//                    cb_haiwen.setVisibility(View.GONE);
                    cb_history.setVisibility(View.GONE);
                    cb_typhoon_more.setVisibility(View.GONE);
                    cb_more_road.setVisibility(View.GONE);
                } else {
                    lay_typhoon_left.setVisibility(View.VISIBLE);
                    if (arrcolumnInfo.size() > 1) {
//                        cb_haiwen.setVisibility(View.VISIBLE);
                        cb_haiwen.setText(arrcolumnInfo.get(1).name);
                    }
                    if (arrcolumnInfo.size() > 2) {
                        cb_history.setVisibility(View.VISIBLE);
                        cb_history.setText(arrcolumnInfo.get(2).name);
                    }
                    if (arrcolumnInfo.size() > 3) {
                        cb_typhoon_more.setText(arrcolumnInfo.get(3).name);
                        cb_typhoon_more.setVisibility(View.VISIBLE);
                    }
                    if (arrcolumnInfo.size() > 4) {
                        cb_more_road.setText(arrcolumnInfo.get(4).name);
                        cb_more_road.setVisibility(View.VISIBLE);
                    }
                }

            }
        }
    };

    /**
     * 点击台风分析
     */
    private void clickTyphoonRoad(String name, String url) {
        Intent intent = new Intent(this, ActivityTyphoonRoad.class);
        intent.putExtra("title", name);
        intent.putExtra("url", url);
        startActivity(intent);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what) {
                case WHAT_PLAY_NEXT:
                    playTyphoon((TyphoonView) msg.obj, msg.arg1);
                    break;
            }
        }
    };

    /**
     * 初始化参数
     */
    private void initParam() {
        mAdapter = new AdapterTyphoonList(this, null);
        dateAdapter = new AdapterTyphoonDate(dateList);
    }

    /**
     * 初始化地图
     *
     * @param savedInstanceState
     */
    private void initMap(Bundle savedInstanceState) {
        mMapView = (MapView) findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        mAMap = mMapView.getMap();
        UiSettings set = mAMap.getUiSettings();
        set.setZoomControlsEnabled(false);// 禁用缩放按钮
        set.setRotateGesturesEnabled(false);// 禁止旋转
        mAMap.setOnMarkerClickListener(mOnMarkerClick);// 标记点点击事件
        mAMap.setOnMapTouchListener(mOnMapTouchListener);// 地图触摸事件
//		mAMap.setOnCameraChangeListener(mOnCameraChangeListener);// 地图自动缩放事件

        mAMap.setMapType(AMap.MAP_TYPE_NORMAL);
        mAMap.getUiSettings().setRotateGesturesEnabled(false);
        mAMap.getUiSettings().setTiltGesturesEnabled(false);
    }

    /**
     * 初始化页面
     */
    private void initView() {
        // 背景设透明
        View view = findViewById(R.id.layout_content);
        view.setBackgroundColor(getResources().getColor(R.color.alpha100));

        initTopView();
        initSpinner();
        initPopupWindow();
        initTyphoonDetail();
        initButton();
        initEvent();

    }

    /**
     * 设置事件
     */
    private void initEvent() {
        setShareListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_right:
                        mAMap.getMapScreenShot(mScreenShotListener);
                        break;
                }
            }
        });
        req();
    }

    /**
     * 初始化顶部布局
     */
    private void initTopView() {
        setTitleText(R.string.typhoon_path);
        //setBtnRight(R.drawable.maillist_button, this);
    }

    /**
     * 初始化下拉按钮
     */
    private void initSpinner() {
        btnSpinner = (Button) findViewById(R.id.btn_choice_typhoon);
        btnSpinner.setOnClickListener(this);
    }

    /**
     * 初始化下拉弹窗
     */
    private void initPopupWindow() {
        View popView = LayoutInflater.from(this).inflate(R.layout.pop_typhoon_list, null);
        ListView listView = (ListView) popView.findViewById(R.id.mylistviw);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(mTyphoonListClick);
        mTyphoonListWindow = new PopupWindow(this);
        mTyphoonListWindow.setContentView(popView);
        mTyphoonListWindow.setWidth((int) (Util.getScreenWidth(this) * 0.7));
        mTyphoonListWindow.setHeight(LayoutParams.WRAP_CONTENT);
        mTyphoonListWindow.setFocusable(true);
    }

    /**
     * 初始化台风详情
     */
    private void initTyphoonDetail() {
        layoutDetail = findViewById(R.id.layout_detail);
        layoutDetail.setOnClickListener(this);
        arrowUp = findViewById(R.id.arrow_up);
        arrowDown = findViewById(R.id.arrow_down);
        btn_road_more_up = findViewById(R.id.btn_road_more_up);
        btn_road_more_down = findViewById(R.id.btn_road_more_down);
        tvDetailTime = (TextView) findViewById(R.id.text_detail_time);
        tvDetailContent = (TextView) findViewById(R.id.text_detail_content);
        lvDate = (MyListView) findViewById(R.id.lv_date);
        lvDate.setAdapter(dateAdapter);
    }

    private CheckBox cb_haiwen, cb_history, cb_typhoon_more, cb_more_road;

    /**
     * 初始化底部按钮
     */
    private void initButton() {
        Button btn;
        CheckBox cb;
        // 播放
        cbPlayPause = (CheckBox) findViewById(R.id.btn_play_pause);
        cbPlayPause.setOnCheckedChangeListener(this);
        // 雷达
        cb = (CheckBox) findViewById(R.id.btn_radar);
        cb.setOnCheckedChangeListener(this);
        // 云图
        cb = (CheckBox) findViewById(R.id.btn_cloud);
        cb.setOnCheckedChangeListener(this);
        //海温检测
        cb_haiwen = (CheckBox) findViewById(R.id.btn_haiwen);
        cb_haiwen.setOnCheckedChangeListener(this);
        //历史查询
        cb_history = (CheckBox) findViewById(R.id.btn_history);
        cb_history.setOnCheckedChangeListener(this);
        //台风之最
        cb_typhoon_more = (CheckBox) findViewById(R.id.btn_typhoon_more);
        cb_typhoon_more.setOnCheckedChangeListener(this);
        //集合路径查询
        cb_more_road = (CheckBox) findViewById(R.id.btn_more_road);
        cb_more_road.setOnCheckedChangeListener(this);
        // 测距
        cb = (CheckBox) findViewById(R.id.btn_distance);
        cb.setOnCheckedChangeListener(this);
        // 图例
        cb = (CheckBox) findViewById(R.id.btn_example);
        cb.setOnCheckedChangeListener(this);
        // 地图切换
        btn = (Button) findViewById(R.id.btn_map_switch);
        btn.setOnClickListener(this);
        // 台风列表
        btn = (Button) findViewById(R.id.btn_typhoon_list);
        btn.setOnClickListener(this);
        // 警报单
        btn = (Button) findViewById(R.id.btn_alarm_list);
        btn.setOnClickListener(this);
        //更多
        btn_road_more = (LinearLayout) findViewById(R.id.btn_road_more);
        btn_road_more.setOnClickListener(this);
        lay_typhoon_left = (LinearLayout) findViewById(R.id.lay_typhoon_left);
    }

    /**
     * 重置页面
     */
    private void resetView() {
        resetSpinner();
        resetInfoLayout();
        cbPlayPause.setChecked(false);
    }

    /**
     * 重置下拉框文本
     */
    private void resetSpinner() {
        btnSpinner.setText(getString(R.string.choice_typhoon));
    }

    /**
     * 重置台风详情布局
     */
    private void resetInfoLayout() {
        arrowUp.setVisibility(View.GONE);
        arrowDown.setVisibility(View.VISIBLE);
        tvDetailTime.setText("");
        tvDetailContent.setText("");
        tvDetailContent.setVisibility(View.GONE);
        layoutDetail.setVisibility(View.GONE);
    }

    /**
     * 重置定位
     */
    private boolean resetLocation() {
        if (locationMarker != null) {
            locationMarker.remove();
            locationMarker = null;
        }

        LatLng latLng = ZtqLocationTool.getInstance().getLatLng();
        if (latLng == null) {
            showToast(getString(R.string.hint_no_location));
            return false;
        }
        mAMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));

        locationMarker = addLocationMarker(this, mAMap, latLng);
        return true;
    }

    /**
     * 更新下拉框文本
     *
     * @param name
     */
    private void updateSpinner(String name) {
//        btnSpinner.setText(name);
        setTitleText(name);
    }

    /**
     * 更新台风详情布局
     *
     * @param view
     */
    private void updateInfoLayout(TyphoonTrueView view) {
        layoutDetail.setVisibility(View.VISIBLE);
        arrowDown.setVisibility(View.GONE);
        arrowUp.setVisibility(View.VISIBLE);
        tvDetailContent.setVisibility(View.VISIBLE);
        tvDetailTime.setText(view.getTime());
        tvDetailContent.setText(view.getContent());
    }

    /**
     * 更新下拉框弹窗
     */
    private void updatePopupWindow() {
        // 调整下拉框长度
        if (mAdapter.getCount() < 6) {
            mTyphoonListWindow.setHeight(LayoutParams.WRAP_CONTENT);
        } else {
            mTyphoonListWindow.setHeight((int) (Util.getScreenHeight(ActivityTyphoon.this) * 0.7));
        }
    }

    public void updateDate(String date) {
        if (date.startsWith("雷达")) {
            for (String str : dateList) {
                if (str.startsWith("雷达")) {
                    dateList.remove(str);
                    break;
                }
            }
        } else if (date.startsWith("云图")) {
            for (String str : dateList) {
                if (str.startsWith("云图")) {
                    dateList.remove(str);
                    break;
                }
            }
        }
        dateList.add(0, date);
        dateAdapter.notifyDataSetChanged();
    }

    /**
     * 清除时间项
     *
     * @param type
     */
    public void clearDate(String type) {
        String filter = "";
        if (type.equals("1")) { // 雷达
            filter = "雷达";
        } else {
            filter = "云图";
        }
        for (String str : dateList) {
            if (str.startsWith(filter)) {
                dateList.remove(str);
                break;
            }
        }
        dateAdapter.notifyDataSetChanged();
    }

    /**
     * 获取中心点坐标
     *
     * @param latLng1
     * @param latLng2
     * @return
     */
    private LatLng getMiddlePoint(LatLng latLng1, LatLng latLng2) {
        // 偏移量
        double latTemp = Math.abs(latLng1.latitude - latLng2.latitude) / 2;
        double lngTemp = Math.abs(latLng1.longitude - latLng2.longitude) / 2;
        // 中间点
        if (latLng1.latitude < latLng2.latitude) {
            latTemp += latLng1.latitude;
        } else {
            latTemp += latLng2.latitude;
        }

        if (latLng1.longitude < latLng2.longitude) {
            lngTemp += latLng1.longitude;
        } else {
            lngTemp += latLng2.longitude;
        }

        return new LatLng(latTemp, lngTemp);
    }

    /**
     * 添加定位标记
     *
     * @param activity
     * @param aMap
     * @param latLng
     * @return
     */
    private Marker addLocationMarker(FragmentActivity activity, AMap aMap, LatLng latLng) {
        if (activity == null || aMap == null || latLng == null) {
            return null;
        }

        MarkerOptions markerOptions = new MarkerOptions().position(latLng).anchor(0.5f, 0.5f).icon
                (BitmapDescriptorFactory.fromResource(R.drawable.icon_typhoon_location));

        return aMap.addMarker(markerOptions);
    }

    /**
     * 添加测距中心点
     *
     * @param latLng
     * @param distance
     * @param name
     * @return
     */
    private MarkerOptions addDistanceCenter(LatLng latLng, float distance, String name) {
        if (latLng == null) {
            return null;
        }

        MarkerOptions options = new MarkerOptions().position(latLng).anchor(0.5f, 0.5f).title("您距离" + name + "台风中心")
                .snippet((int) (distance / 1000) + "公里")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_alpha_point));
        return options;
    }

    /**
     * 添加测距线
     *
     * @param list
     * @return
     */
    private PolylineOptions addDistanceLine(List<LatLng> list) {
        if (list == null || list.size() < 2) {
            return null;
        }

        PolylineOptions options = (new PolylineOptions()).width(3).color(Color.GRAY);
        for (LatLng latLng : list) {
            options.add(latLng);
        }
        return options;
    }

    /**
     * 填充台风列表数据
     *
     * @param list
     */
    public void fillTyphoonListData(List<TyphoonInfo> list) {
        mAdapter.setData(list);
        updatePopupWindow();
    }

    /**
     * 选择指定台风列表项。 未缓存时请求指定台风数据，已缓存时显示指定台风。
     *
     * @param position
     */
    public void selectTyphoon(int position) {
        if (mAdapter.getCount() <= 0) {
            showToast(getString(R.string.hint_no_typhoon));
            return;
        }

        TyphoonInfo info = (TyphoonInfo) mAdapter.getItem(position);
        if (info == null || TextUtils.isEmpty(info.code)) {
            showToast(getString(R.string.error_data));
            finish();
            return;
        }

        TyphoonView view = mControl.getTyphoonView(info.code);
        if (view == null) {
            mControl.requestTyphoonPath(info.code);
        } else {
            showTyphoonPath(view);
        }
    }

    /**
     * 取消选择指定台风列表项
     *
     * @param position
     */
    private void unselectTyphoon(int position) {
        mAdapter.setItemState(position, false);
        TyphoonInfo info = (TyphoonInfo) mAdapter.getItem(position);
        TyphoonView view = mControl.getTyphoonView(info.code);
        hideTyphoonPath(view);
    }

    /**
     * 显示台风路径
     *
     * @param view
     */
    public void showTyphoonPath(TyphoonView view) {
        if (view == null) {
            return;
        }

        mAdapter.setItemState(view.getCode(), true);
        switchLocal(view);
        view.show(mAMap);
        showAllInScreen();
    }

    /**
     * 显示测距视图
     */
    private void showDistance() {
        // 缩放
        LatLng locationLatLng = locationMarker.getPosition();
        LatLng lastPointLatLng = mControl.getTyphoonView(localCode).getLastTrueView().getLatLng();
        List<LatLng> list = new ArrayList<LatLng>();
        if (locationLatLng != null) {
            list.add(locationLatLng);
        }
        if (lastPointLatLng != null) {
            list.add(lastPointLatLng);
        }
        zoomToSpan(list);

        // 显示测距视图
        LatLng centerLatLng = getMiddlePoint(locationLatLng, lastPointLatLng);// 中心点坐标
        float distance = AMapUtils.calculateLineDistance(locationLatLng, lastPointLatLng);// 两点距离
        String name = mControl.getTyphoonView(localCode).getSimpleName();
        distanceView = new DistanceView();
        distanceView.setCenter(addDistanceCenter(centerLatLng, distance, name));
        distanceView.setLine(addDistanceLine(list));
        distanceView.show(mAMap);
    }

    /**
     * 隐藏台风
     *
     * @param view
     */
    private void hideTyphoonPath(TyphoonView view) {
        if (view == null) {
            return;
        }

        view.hide();

        view = mControl.getTyphoonView(mAdapter.getFirstCheckedItem().toString());
        switchLocal(view);
        showAllInScreen();
    }

    /**
     * 隐藏地图标记弹窗
     */
    private void hideInfoWindow() {
        List<Marker> list = mAMap.getMapScreenMarkers();
        for (Marker marker : list) {
            marker.hideInfoWindow();
        }
    }

    /**
     * 切换当前台风
     *
     * @param view
     */
    private void switchLocal(TyphoonView view) {
        if (view == null) {
            localCode = "";
            resetSpinner();
            resetInfoLayout();
        } else {
            localCode = view.getCode();
            if (mAdapter.getCheckedCount() == MAX_COUNT_TYPHOON) {
                mTyphoonListWindow.dismiss();
            }
            updateSpinner(view.getName());
            updateInfoLayout(view.getLastTrueView());
        }
    }

    /**
     * 隐藏测距视图
     */
    private void hideDistance() {
        if (distanceView != null) {
            distanceView.hide();
            distanceView = null;
        }
    }

    /**
     * 点击通讯录
     */
    private void clickMailList() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Contacts.People.CONTENT_URI);
        startActivity(intent);
    }

    /**
     * 点击下拉框
     */
    private void clickSpinner() {
        if (mAdapter.getCount() <= 0) {
            showToast(getString(R.string.hint_no_typhoon));
            return;
        }

//        mTyphoonListWindow.showAsDropDown(btnSpinner, 0, 0);
        mTyphoonListWindow.showAtLocation(btnSpinner, Gravity.CENTER, 0, 0);
    }

    /**
     * 点击台风详情
     */
    private void clickTyphoonDetail() {
        if (arrowUp.getVisibility() == View.VISIBLE) {
            // 改为不可见
            arrowUp.setVisibility(View.GONE);
            arrowDown.setVisibility(View.VISIBLE);
            tvDetailContent.setVisibility(View.GONE);
        } else {
            // 改为可见
            arrowUp.setVisibility(View.VISIBLE);
            arrowDown.setVisibility(View.GONE);
            tvDetailContent.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 同屏显示所有台风及定位点
     */
    private void showAllInScreen() {
        List<LatLng> latLngList = new ArrayList<LatLng>();

        if (resetLocation()) {
            latLngList.add(locationMarker.getPosition());
        }

        List<String> codeList = mAdapter.getAllCheckedItem();
        TyphoonView view;
        for (String code : codeList) {
            view = mControl.getTyphoonView(code);
            if (view != null) {
                latLngList.addAll(view.getTrueLatLng());
            }
        }

        zoomToSpan(latLngList);
    }

    /**
     * 自动缩放地图
     *
     * @param latLngList
     */
    private void zoomToSpan(List<LatLng> latLngList) {
        if (latLngList == null || latLngList.size() <= 0) {
            return;
        }
        if (latLngList.size() == 1) {
            mAMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngList.get(0), DEFAULT_ZOOM));
        } else {
            mAMap.animateCamera(CameraUpdateFactory.newLatLngBounds(getLatLngBounds(latLngList), 0), 500,
                    mCancelableCallback);
        }
    }

    /**
     * 获取台风路径范围区域
     *
     * @param list
     * @return
     */
    private LatLngBounds getLatLngBounds(List<LatLng> list) {
        if (list == null || list.size() <= 1) {
            return null;
        }

        double swLatitude = 90;
        double swLongitude = 180;

        double neLatitude = -90;
        double neLongitude = -180;

        for (LatLng latlng : list) {
            if (latlng.latitude < swLatitude) {
                swLatitude = latlng.latitude;
            }
            if (latlng.longitude < swLongitude) {
                swLongitude = latlng.longitude;
            }

            if (latlng.latitude > neLatitude) {
                neLatitude = latlng.latitude;
            }
            if (latlng.longitude > neLongitude) {
                neLongitude = latlng.longitude;
            }
        }

        LatLng swLatLng = new LatLng(swLatitude, swLongitude);
        LatLng neLatLng = new LatLng(neLatitude, neLongitude);
        return new LatLngBounds(swLatLng, neLatLng);
    }

    /**
     * 点击播放按钮。 流程：隐藏当前台风，显示暂停按钮，播放台风。
     */
    private void clickPlay() {
        if (TextUtils.isEmpty(localCode)) {
            clickSpinner();
            return;
        }

        TyphoonView view = mControl.getTyphoonView(localCode);
        if (view == null) {
            return;
        }
        view.hide();

        showPauseButton();
        playTyphoon(view, 0);
    }

    /**
     * 点击暂停按钮。 流程：暂停播放，移除已显示的路径点，显示完整台风路径，显示播放按钮。
     */
    private void clickPause() {
        mHandler.removeMessages(WHAT_PLAY_NEXT);

        TyphoonView view = mControl.getTyphoonView(localCode);
        if (view == null) {
            return;
        }
        view.hide();
        view.show(mAMap);
        mAMap.moveCamera(CameraUpdateFactory.changeLatLng(view.getLastTrueView().getLatLng()));
        updateInfoLayout(view.getLastTrueView());
        showPlayButton();
    }

    /**
     * 点击测距按钮
     */
    private void clickDistance() {
        if (TextUtils.isEmpty(localCode)) {
            clickSpinner();
            return;
        }

        boolean result = resetLocation();
        if (!result) {
            return;
        }

        showDistance();
    }

    /**
     * 点击图例按钮
     */
    private void clickExample() {
        // 暂停
        clickPause();
        // 打开图例页面
        Intent intent = new Intent(this, ActivityTyphoonExample.class);
        this.startActivity(intent);
    }

    /**
     * 点击地图切换按钮
     */
    private void clickMapSwitch() {
        if (mAMap.getMapType() == AMap.MAP_TYPE_SATELLITE) {
            mAMap.setMapType(AMap.MAP_TYPE_NORMAL);
        } else {
            mAMap.setMapType(AMap.MAP_TYPE_SATELLITE);
        }
    }

    /**
     * 显示播放按钮
     */
    private void showPlayButton() {
        isPlayEnd=true;
        cbPlayPause.setText(getString(R.string.play));
    }

    /**
     * 显示暂停按钮
     */
    private void showPauseButton() {
        isPlayEnd=false;
        cbPlayPause.setText(getString(R.string.stop));
    }

    /**
     * 提示失败
     */
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 显示台风名称
     *
     * @param name
     */
    public void showTyphoonName(String name) {
        btnSpinner.setText(name);
    }

    /**
     * 显示台风详情
     *
     * @param time
     * @param content
     */
    public void showTyphoonDetail(String time, String content) {
        tvDetailTime.setText(time);
        tvDetailContent.setText(content);
    }

    /**
     * 是否正在播放台风
     *
     * @return
     */
    private boolean isPlay() {
        return cbPlayPause.isChecked();
    }

    /**
     * 播放显示台风指定位置
     *
     * @param view
     * @param position
     */
    private void playTyphoon(TyphoonView view, int position) {
        if (view == null || position < 0) {
            playEnd();
        }

        view.play(mAMap, position);
        TyphoonTrueView trueView = view.getTrueView(position);
        if (trueView != null) {
            updateInfoLayout(trueView);
        }

        if (position == view.getSize() - 1) {
            playEnd();
        } else {
            position++;
            Message msg = new Message();
            msg.what = WHAT_PLAY_NEXT;
            msg.arg1 = position;
            msg.obj = view;
            mHandler.sendMessageDelayed(msg, PLAY_INTERVAL);
        }
    }

    private boolean isPlayEnd=true;
    /**
     * 播放结束
     */
    private void playEnd() {
        isPlayEnd=true;
        showPlayButton();
    }

    /**
     * 地图触摸事件监听器
     *
     * @author E.Sun 2015年9月7日
     */
    private OnMapTouchListener mOnMapTouchListener = new OnMapTouchListener() {

        @Override
        public void onTouch(MotionEvent arg0) {
            is_Distance=true;
            hideInfoWindow();
            hideDistance();
        }
    };

    /**
     * 路径点点击事件
     */
    private OnMarkerClickListener mOnMarkerClick = new OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker arg0) {
            if (!isPlayEnd) {
                return true;
            }

            // 获取标记点所属台风，若标记点为台风标记点，则切换当前台风
            String markerID = arg0.getId();
            List<String> codeList = mAdapter.getAllCheckedItem();
            TyphoonView view;
            String tempCode;
            for (String code : codeList) {
                view = mControl.getTyphoonView(code);
                if (view == null) {
                    continue;
                }

                tempCode = view.getCode(markerID);
                if (TextUtils.isEmpty(tempCode)) {
                    continue;
                }

                if (!localCode.equals(tempCode)) {
                    switchLocal(view);
                }
                return false;
            }

            return false;
        }
    };

    private OnMapScreenShotListener mScreenShotListener = new OnMapScreenShotListener() {
        @Override
        public void onMapScreenShot(Bitmap bitmap) {
            View layout = findViewById(R.id.layout_main).getRootView();
            mAmapBitmap = bitmap;
            Bitmap bm = ZtqImageTool.getInstance().getScreenBitmapNew(ActivityTyphoon.this);

            int[] location = new int[2];
            mMapView.getLocationOnScreen(location);
            int top = location[1] - getStatusBarHeight();

            mShareBitmap = procImage(mAmapBitmap, bm, top);
            mShareBitmap = ZtqImageTool.getInstance().stitchQR(ActivityTyphoon.this, mShareBitmap);
            ShareTools.getInstance(ActivityTyphoon.this).setShareContent(getTitleText(), getTitleText(), mShareBitmap, "0").showWindow(layout);
        }

        @Override
        public void onMapScreenShot(Bitmap bitmap, int i) {

        }
    };

    /**
     * 台风列表点击事件
     */
    private OnItemClickListener mTyphoonListClick = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Holder holder = (Holder) view.getTag();
            if (holder.cb.isChecked()) {
                unselectTyphoon(position);
            } else {
                // 限制显示数
                if (mAdapter.getCheckedCount() >= MAX_COUNT_TYPHOON && !holder.cb.isChecked()) {
                    Toast.makeText(ActivityTyphoon.this, getString(R.string.error_select_typhoon), Toast.LENGTH_SHORT).show();
                    return;
                }
                selectTyphoon(position);
            }
        }
    };

    /**
     * 用于实现缩放动画
     */
    private CancelableCallback mCancelableCallback = new CancelableCallback() {

        @Override
        public void onCancel() {

        }

        @Override
        public void onFinish() {
            mAMap.animateCamera(CameraUpdateFactory.zoomTo(mAMap.getCameraPosition().zoom - 0.4f), 2000, null);
        }
    };

    private Bitmap bitmapMap;

    private void getBitmap() {
        if (mMapView != null) {
            mMapView.getMap().getMapScreenShot(new OnMapScreenShotListener() {
                @Override
                public void onMapScreenShot(Bitmap arg0) {
                    bitmapMap = arg0;
                    shareMap(ActivityTyphoon.this);
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
        getBitmap();
    }

    private void shareMap(Activity activity) {
        Bitmap bitmap = BitmapUtil.takeScreenShot(activity);
        Canvas canvas = new Canvas(bitmap);
        if (bitmapMap != null) {
            int hight = bitmap.getHeight() - bitmapMap.getHeight();
            canvas.drawBitmap(bitmapMap, 0, hight, null);
        }
        ShareUtil.share(activity, "", bitmap);
    }
    private boolean is_Distance = true;
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.btn_radar:
                // 雷达
                if (isChecked) {
                    mControl.reqRadar();
                } else {
                    mControl.clearRadar();
                }
                break;
            case R.id.btn_cloud:
                // 云图
                if (isChecked) {
                    mControl.reqCloud();
                } else {
                    mControl.clearCloud();
                }
                break;
            case R.id.btn_distance:
                if (isPlay()) {
                    clickPause();
                }
                if(is_Distance){
                    hideDistance();
                    hideInfoWindow();
                    clickDistance();// 测距
                    is_Distance=false;
                }else {
                    hideDistance();
                    hideInfoWindow();
                    is_Distance=true;
                }

                break;
            case R.id.btn_more_road:
                //集合路径预报
                if (arrcolumnInfo.size() > 4) {
                    clickTyphoonRoad(arrcolumnInfo.get(4).name, arrcolumnInfo.get(4).req_url);
                }
                break;
            case R.id.btn_history:
                //历史查询
                if (arrcolumnInfo.size() > 2) {
                    clickTyphoonRoad(arrcolumnInfo.get(2).name, arrcolumnInfo.get(2).req_url);
                }
                break;
            case R.id.btn_haiwen:
                //海温检测
                if (arrcolumnInfo.size() > 1) {
                    clickTyphoonRoad(arrcolumnInfo.get(1).name, arrcolumnInfo.get(1).req_url);
                }
                break;
            case R.id.btn_typhoon_more:
                //台风之最
                if (arrcolumnInfo.size() > 3) {
                    clickTyphoonRoad(arrcolumnInfo.get(3).name, arrcolumnInfo.get(3).req_url);
                }
                break;
            case R.id.btn_example:
                hideDistance();
                is_Distance=true;
                hideInfoWindow();
                clickExample();// 图例
                break;
            case R.id.btn_play_pause:
                if (isPlayEnd){
                    hideDistance();
                    is_Distance=true;
                    hideInfoWindow();
                    clickPlay();// 播放
                }else {
                    clickPause();// 暂停
                }
                break;
        }
    }
}