package com.pcs.ztqtj.control.main_en_weather;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.pcs.lib.lib_pcs_v3.control.tool.Util;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib.lib_pcs_v3.model.image.ImageFetcher;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityMain;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackMapForecastDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackMapForecastUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.week.PackMainWeekWeatherDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.week.PackMainWeekWeatherUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.week.WeekWeatherInfo;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.AdapterMapForecastEn;
import com.pcs.ztqtj.control.adapter.AdapterWeekListView;
import com.pcs.ztqtj.control.main_weather.CommandMainBase;
import com.pcs.ztqtj.control.tool.ZtqLocationTool;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.view.myview.Hour24View_En;
import com.pcs.ztqtj.view.myview.MapContainer;
import com.pcs.ztqtj.view.myview.MyGridView;

import java.util.ArrayList;
import java.util.List;

/**
 * JiangZy on 2016/6/3.
 */
public class CommandMainEnRow3 extends CommandMainBase implements AdapterView.OnItemClickListener, AMap
        .OnMarkerClickListener {

    private Activity mActivity;
    private ViewGroup mRootLayout;
    private ImageFetcher imageFetcher;
    // 地图默认每像素密度
    private final float DEFAULKT_SCALE_PER_PIXEL = 2.14253568649292f;
    private View mRowView;
    // 当前经纬度
    private LatLng mLatLng = null;

    private MapView mMapView;
    // 定位标记
    private MarkerOptions mMarker = null;

    private AMap mAMap = null;

    private Bundle mSavedInstanceState;

    private RadioGroup radio_group_week_24;

    private TextView not_time_data;

    private LinearLayout layout_24house;

    private FrameLayout lay_fra_location;

    private ListView lv_for_7day;

    private AdapterWeekListView adapterWeekListView;

    private List<WeekWeatherInfo> list_week = new ArrayList<>();

    private ScrollView scrollView;

    private LinearLayout lay_image_up;

    private LinearLayout lay_main_address;

    private TextView tv_main_address;

    private RelativeLayout rel_grid_content;

    public AMapLocationClient mLocationClient = null;
    private int height;

    public CommandMainEnRow3(Activity activity, ViewGroup rootLayout, ImageFetcher imageFetcher, Bundle
            savedInstanceState, ScrollView mscrollView, int mheight) {
        mActivity = activity;
        mRootLayout = rootLayout;
        this.imageFetcher = imageFetcher;
        mSavedInstanceState = savedInstanceState;
        this.scrollView = mscrollView;
        height = mheight;
    }

    @Override
    protected void init() {
        mRowView = LayoutInflater.from(mActivity).inflate(
                R.layout.item_home_weather_en_3, null);
        DisplayMetrics dm = mActivity.getResources().getDisplayMetrics();
        int heights = dm.heightPixels;
        mRowView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                heights - height - 80));
//        mRootLayout.addView(mRowView);
        mRootLayout.addView(mRowView);
        PcsDataBrocastReceiver.registerReceiver(mActivity, mReceiver);
        initView();
        // 初始化地图
        initMap();
        initGeocodeSearch();
        // 初始化定位
        initLocation();
        initReq();
    }

    private void initReq() {
        reFreshWeek();
        layout_24house.setVisibility(View.VISIBLE);
        lay_fra_location.setVisibility(View.GONE);
        radio_group_week_24.check(R.id.main_24hour);
    }

    private void initView() {
        mMapView = (MapView) mRowView.findViewById(R.id.map);
        mMapView.onCreate(mSavedInstanceState);// 此方法必须重写
        lay_main_address = (LinearLayout) mRootLayout.findViewById(R.id.lay_main_address);
        tv_main_address = (TextView) mRootLayout.findViewById(R.id.tv_main_address);
        rel_grid_content = (RelativeLayout) mRowView.findViewById(R.id.rel_grid_content);
        rel_grid_content.setVisibility(View.GONE);
        gridview24hour = (MyGridView) mRowView.findViewById(R.id.gridview24hour);
        adapterMain = new AdapterMapForecastEn(mActivity, list);
        gridview24hour.setAdapter(adapterMain);
        lay_image_up = (LinearLayout) mRowView.findViewById(R.id.lay_image_up);
        lay_image_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scrollView.scrollTo(0, 0);
            }
        });
        layout_24house = (LinearLayout) mRowView.findViewById(R.id.layout_24house);
        lv_for_7day = (ListView) mRowView.findViewById(R.id.lv_for_7day);
        adapterWeekListView = new AdapterWeekListView(mActivity, imageFetcher, list_week);
        lv_for_7day.setAdapter(adapterWeekListView);
        lay_fra_location = (FrameLayout) mRowView.findViewById(R.id.lay_fra_location);
        main24hour = (Hour24View_En) mRowView.findViewById(R.id.main24hour);
        not_time_data = (TextView) mRowView.findViewById(R.id.not_time_data);
        WindowManager wm = mActivity.getWindowManager();
        int width = wm.getDefaultDisplay().getWidth();
        not_time_data.setWidth(width);
        radio_group_week_24 = (RadioGroup) mRowView.findViewById(R.id.radio_group_week_24);
        radio_group_week_24.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.main_location:
                        lay_fra_location.setVisibility(View.VISIBLE);
                        layout_24house.setVisibility(View.GONE);
                        break;
                    case R.id.main_24hour:
                        layout_24house.setVisibility(View.VISIBLE);
                        lay_fra_location.setVisibility(View.GONE);
                        break;
                }
            }
        });
    }


    /**
     * 初始化定位
     */
    private void initLocation() {
        // 定位4
        LatLng latLng = ZtqLocationTool.getInstance().getLatLng();
        if (latLng != null) {
            locationSuccess(latLng);
        } else {
            // 添加定位监听
            ZtqLocationTool.getInstance().addListener(mLocationListener);
        }
    }

    @Override
    protected void refresh() {
        reFreshWeek();
        reFlush24House(mPackDown.list);
    }

    //适配器：小时
    private AdapterMapForecastEn adapterMain;
    private Hour24View_En main24hour;
    private MyGridView gridview24hour;

    private void reFlush24House(List<PackMapForecastDown.MapForecast> list) {
        //逐时预报获取数据
        if (main24hour == null) {
            View view = LayoutInflater.from(mActivity).inflate(R.layout.item_home_weather_2, null);
            if (view != null) {
                main24hour = (Hour24View_En) view.findViewById(R.id.main24hour);
            }
        }
        if (main24hour != null) {
//            PackLocalCityMain packCity = ZtqCityDB.getInstance().getCityMain();
//            if (packCity == null) {
//                return;
//            }
//            //24小时
//            PackHourForecastUp packHourUp = new PackHourForecastUp();
//            packHourUp.county_id = packCity.ID;
//            PackHourForecastDown down = (PackHourForecastDown) PcsDataManager.getInstance().getNetPack(packHourUp
//                    .getName());
            List<Float> mTopTemp = new ArrayList<Float>();
            if (list.size() > 0) {
                int size = list.size();
                for (int i = 0; i < list.size(); i++) {
                    String temp = list.get(i).temperature;
                    if (!TextUtils.isEmpty(temp)) {
                        if (CommandMainEnRow1.is_hs){
                            mTopTemp.add(Float.parseFloat(changeStr(temp)));
                        }else {
                            mTopTemp.add(Float.parseFloat(temp));
                        }
                    } else {
                        continue;
                    }
                }
                not_time_data.setVisibility(View.GONE);
                int width = Util.dip2px(mActivity, 50) * size;

                gridview24hour.setNumColumns(size);
                gridview24hour.setVisibility(View.VISIBLE);
                ViewGroup.LayoutParams gradviewParams = gridview24hour.getLayoutParams();
                gradviewParams.width = width;
                gridview24hour.setLayoutParams(gradviewParams);

                main24hour.setVisibility(View.VISIBLE);
                main24hour.setCount(size);
                ViewGroup.LayoutParams mainParams = main24hour.getLayoutParams();
                mainParams.width = width;
                main24hour.setLayoutParams(mainParams);
            } else {
                not_time_data.setVisibility(View.VISIBLE);
                gridview24hour.setVisibility(View.GONE);
                main24hour.setVisibility(View.GONE);
            }
            main24hour.setTemperture(mTopTemp, null);
        }
    }


    private String changeStr(String value) {
        String temHs_little = "";
        String tempHs = String.valueOf(Float.parseFloat(value) * 1.8 + 32);
        if (!TextUtils.isEmpty(tempHs) && tempHs.indexOf(".") > 1) {
            tempHs = tempHs.substring(0, tempHs.indexOf(".") + 1).replace(".", "");
        }
        if (!TextUtils.isEmpty(String.valueOf(Float.parseFloat(value) * 1.8 + 32)) && String.valueOf(Float
                .parseFloat(value) * 1.8 + 32).indexOf(".") > 1) {
            temHs_little = value.substring(value.indexOf(".") + 1, value.length());
        }
        if (!temHs_little.equals("0")) {
            tempHs = tempHs + "." + temHs_little.substring(0, 1);
        }
        return tempHs;
    }
    //一周天气-7天
    private PackMainWeekWeatherUp packWeekUp = new PackMainWeekWeatherUp();

    private void reFreshWeek() {
        // 温度

        PackLocalCityMain packCity = ZtqCityDB.getInstance().getCityMain();
        if (packCity == null) {
            return;
        }
        packWeekUp.setCity(packCity);
        PackMainWeekWeatherDown packWeekDown = (PackMainWeekWeatherDown) PcsDataManager.getInstance().getNetPack
                (packWeekUp.getName());
        if (packWeekDown == null || packWeekDown.getWeek() == null
                || packWeekDown.getWeek().size() == 0) {
//            // 清空显示
            return;
        } else {
            list_week.clear();
            for (int i = 1; i < packWeekDown.getWeek().size(); i++) {
                WeekWeatherInfo info = packWeekDown.getWeek().get(i);
                    list_week.add(info);
            }
//            list_week.addAll(packWeekDown.getWeek());
            adapterWeekListView.notifyDataSetChanged();
        }
    }


    /**
     * 初始化地图
     */
    private void initMap() {
        MapContainer mapContainer = (MapContainer) mRowView.findViewById(R.id.map_container);
        mapContainer.setScrollView(scrollView);
        if (mAMap == null) {
            mAMap = mMapView.getMap();
            mAMap.setMapLanguage(AMap.ENGLISH);
            mAMap.setOnMapClickListener(mMapClick);
            // 标记点击事件
            mAMap.setOnMarkerClickListener(mOnMarkerClick);
            // 缩放
            mAMap.moveCamera(CameraUpdateFactory.zoomTo(14));
            // 显示比例尺
            mAMap.getUiSettings().setScaleControlsEnabled(true);
            mAMap.getUiSettings().setRotateGesturesEnabled(false);
            mAMap.getUiSettings().setTiltGesturesEnabled(false);
            mAMap.getUiSettings().setZoomControlsEnabled(false);
        }

    }

    /**
     * 地图点击监听
     */
    private AMap.OnMapClickListener mMapClick = new AMap.OnMapClickListener() {
        @Override
        public void onMapClick(LatLng latLng) {
            // 切换定位点
            setLocation(latLng);
            // 显示数据
            showDataList(latLng);
            // 调用地理编码搜索
            LatLonPoint point = new LatLonPoint(latLng.latitude,
                    latLng.longitude);
            callGeocodeSearch(point);
        }
    };

    /**
     * 显示数据列表
     *
     * @param latLng
     */
    private void showDataList(LatLng latLng) {
//        View layout = findViewById(R.id.layout_data);
//        layout.setVisibility(View.VISIBLE);
        mPackUp.hour = "24";
        String latitude = String.format("%.1f", latLng.latitude);
        String longitude = String.format("%.1f", latLng.longitude);
        mPackUp.latitude = latitude;
        mPackUp.longitude = longitude;
        PcsDataDownload.addDownload(mPackUp);
        adapterMain.notifyDataSetChanged();
//        adapterMain.refresh( latLng);
    }



    /**
     * 标记点击事件
     */
    private AMap.OnMarkerClickListener mOnMarkerClick = new AMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            LatLng latLng = marker.getPosition();
            // 显示地名
            String name = (String) marker.getObject();

            lay_main_address.setVisibility(View.VISIBLE);
            tv_main_address.setText(name);


//            showAddrName(name);
            // 显示数据
            showDataList(latLng);
            return false;
        }

    };

    /**
     * 调用地理编码搜索（获取地名）
     *
     * @param latLonPoint
     */
    private void callGeocodeSearch(final LatLonPoint latLonPoint) {
        // 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,
                GeocodeSearch.AMAP);
        // 设置同步逆地理编码请求
        mGeocodeSearch.getFromLocationAsyn(query);
    }

    private GeocodeSearch mGeocodeSearch;

    /**
     * 初始化地理编码搜索
     */
    private void initGeocodeSearch() {
        mGeocodeSearch = new GeocodeSearch(mActivity);
        mGeocodeSearch.setOnGeocodeSearchListener(mGeocodeSearchListener);
    }


    /**
     * 地理编码搜索监听
     */
    private GeocodeSearch.OnGeocodeSearchListener mGeocodeSearchListener = new GeocodeSearch.OnGeocodeSearchListener() {

        @Override
        public void onGeocodeSearched(GeocodeResult result, int rCode) {

        }

        @Override
        public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
            if (rCode == ZtqLocationTool.RCODE) {
                if (result != null
                        && result.getRegeocodeAddress() != null
                        && result.getRegeocodeAddress().getFormatAddress() != null) {
                    // 格式化地址
                    String tempAddr =  result.getRegeocodeAddress().getFormatAddress();
                    // 显示地名
                    lay_main_address.setVisibility(View.VISIBLE);
                    tv_main_address.setText(tempAddr);
                }
            }
        }

    };

    /**
     * 设置定位点，显示标识
     *
     * @param latLng
     */
    private void setLocation(LatLng latLng) {
        mLatLng = latLng;
        mAMap.clear();
        // 地图位置
        LatLng tempLatLng = new LatLng(latLng.latitude + getLatitudeAdd(), latLng.longitude);
        // 标记
        if (mMarker == null) {
            mMarker = new MarkerOptions();
        }
        mMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_location));
        mMarker.position(latLng);
        mAMap.addMarker(mMarker);
//		mAMap.moveCamera(CameraUpdateFactory.changeLatLng(tempLatLng));
        mAMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));
        // 去除定位监听
        ZtqLocationTool.getInstance().removeListener(mLocationListener);
    }


    /**
     * 位置改变监听
     */
    private ZtqLocationTool.PcsLocationListener mLocationListener = new ZtqLocationTool.PcsLocationListener() {
        @Override
        public void onLocationChanged() {
            LatLng latLng = ZtqLocationTool.getInstance().getLatLng();
            locationSuccess(latLng);
        }
    };

    private void locationSuccess(LatLng latLng) {
        // 切换定位点
        setLocation(latLng);
        // 显示数据
        showDataList(latLng);
        // 调用地理编码搜索
        LatLonPoint point = new LatLonPoint(latLng.latitude, latLng.longitude);
        callGeocodeSearch(point);
    }

    /**
     * 获取纬度显示偏移
     *
     * @return
     */
    private double getLatitudeAdd() {
        double latitudeAdd = DEFAULKT_SCALE_PER_PIXEL;
        if (mAMap.getScalePerPixel() > 0) {
            latitudeAdd = (double) mAMap.getScalePerPixel();
        }
        latitudeAdd = -latitudeAdd / 800.0d;
        return latitudeAdd;
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    private PackMapForecastUp mPackUp = new PackMapForecastUp();
    private PackMapForecastDown mPackDown = new PackMapForecastDown();
    public List<PackMapForecastDown.MapForecast> list = new ArrayList<PackMapForecastDown.MapForecast>();

    private PcsDataBrocastReceiver mReceiver = new PcsDataBrocastReceiver() {
        @Override
        public void onReceive(String nameStr, String errorStr) {

            if (!TextUtils.isEmpty(errorStr)) {
//                Toast.makeText(mContext, errorStr, Toast.LENGTH_SHORT)
//                        .show();
                return;
            }
            if (mPackUp.hour == null) {
                return;
            }
            if (nameStr.equals(mPackUp.getName())) {
                mPackDown = (PackMapForecastDown) PcsDataManager.getInstance().getNetPack(mPackUp.getName());
                if (mPackDown == null || mPackDown.list.size() == 0) {
                    // 提示无数据
//                    Toast.makeText(mContext, R.string.no_data, Toast.LENGTH_SHORT)
//                            .show();
//                    mContext.setView(true);
                    rel_grid_content.setVisibility(View.GONE);
                    //return ;
                } else {
                    rel_grid_content.setVisibility(View.VISIBLE);
                    list.clear();
                    list.addAll(mPackDown.list);
//                    mContext.setView(false);
                    adapterMain.notifyDataSetChanged();
                    reFlush24House(mPackDown.list);
                }
            }
        }
    };
}
