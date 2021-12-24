package com.pcs.ztqtj.view.activity.product;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackShareAboutDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.oceanweather.OceanAreaInfo;
import com.pcs.lib_ztqfj_v2.model.pack.net.oceanweather.OceanWeatherInfo;
import com.pcs.lib_ztqfj_v2.model.pack.net.oceanweather.PackAreaPositionDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.oceanweather.PackOceanWeatherDown;
import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.ocean.AdapterMapOcean;
import com.pcs.ztqtj.control.tool.ShareTools;
import com.pcs.ztqtj.control.tool.ZtqImageTool;
import com.pcs.ztqtj.control.tool.ZtqLocationTool;
import com.pcs.ztqtj.control.tool.utils.TextUtil;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.util.OkHttpUtil;
import com.pcs.ztqtj.view.activity.livequery.DataControl;
import com.pcs.ztqtj.view.activity.product.typhoon.AMapUtil;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 监测预报-海洋气象
 */
public class ActivityOceanMap extends AMapUtil {

    private TextureMapView mapView;
    private AMap aMap;
    private AdapterMapOcean adapterMapOcean;
    private ListView list_map;
    private TextView tvAddress;
    private LinearLayout layout_data;
    // 当前经纬度
    private LatLng mLatLng = null;
    // 标记
    private MarkerOptions mMarker = null;
    // 地理编码搜索

    private GeocodeSearch mGeocodeSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocean_map);
        mapView = findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);
        setTitleText("海洋气象");
        // 初始化地理编码搜索
        initGeocodeSearch();
        initMap();
        initView();
        initEvent();
        createImageFetcher();
        initData();
        // 初始化定位
        initLocation();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        if (mLatLng == null) {
            ZtqLocationTool.getInstance().addListener(mLocationListener);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // 去除定位监听
        ZtqLocationTool.getInstance().removeListener(mLocationListener);
        mapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    private ArrayList<OceanWeatherInfo> list_ocean = new ArrayList<>();

    private void initView() {
        list_map = findViewById(R.id.listView);
        adapterMapOcean = new AdapterMapOcean(this, list_ocean);
        list_map.setAdapter(adapterMapOcean);
        tvAddress = findViewById(R.id.text_list_title);
        layout_data = findViewById(R.id.layout_data);
    }

    private void initMap() {
        aMap = mapView.getMap();
        if (aMap != null) {
            UiSettings settings = aMap.getUiSettings();
            settings.setZoomControlsEnabled(false);
            aMap.setOnMapClickListener(onMapClickListener);
            aMap.setOnMarkerClickListener(mOnMarkerClick);
            aMap.moveCamera(CameraUpdateFactory.zoomTo(14));
        }
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

    /**
     * 初始化地理编码搜索
     */
    private void initGeocodeSearch() {
        mGeocodeSearch = new GeocodeSearch(ActivityOceanMap.this);
        mGeocodeSearch.setOnGeocodeSearchListener(mGeocodeSearchListener);
    }

    private void initEvent() {
        setShareListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_right:
                        aMap.getMapScreenShot(mScreenShotListener);
                        break;
                }
            }
        });
    }

    /**
     * 高德地图截屏回调
     */
    private AMap.OnMapScreenShotListener mScreenShotListener = new AMap.OnMapScreenShotListener() {
        @Override
        public void onMapScreenShot(Bitmap bitmap) {
            View layout = findViewById(R.id.ocean_weather_view).getRootView();
            mAmapBitmap = bitmap;
            Bitmap bm = ZtqImageTool.getInstance().getScreenBitmapNew(ActivityOceanMap.this);

            int[] location = new int[2];
            mapView.getLocationOnScreen(location);

            mShareBitmap = procImage(mAmapBitmap, bm, location[1]);
            mShareBitmap = ZtqImageTool.getInstance().stitchQR(ActivityOceanMap.this, mShareBitmap);
            ShareTools.getInstance(ActivityOceanMap.this).setShareContent(getTitleText(), getTitleText(), mShareBitmap, "0").showWindow(layout);
        }

        @Override
        public void onMapScreenShot(Bitmap bitmap, int i) {

        }
    };

    private void initData() {
        okHttpHyArea();
    }

    private boolean isFirst = true;

    /**
     * 设置定位点，显示标识
     * @param latLng
     */
    private void setLocation(LatLng latLng) {
        mLatLng = latLng;
        // aMap.clear();
        // 标记
        if (mMarker == null) {
            mMarker = new MarkerOptions();
        }
        if (isFirst) {
            mMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_location));
            mMarker.position(latLng);
            aMap.addMarker(mMarker);
            isFirst = false;
//		mAMap.moveCamera(CameraUpdateFactory.changeLatLng(tempLatLng));
        }

        aMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));
        callGeocodeSearch(latLng);
        // 去除定位监听
        ZtqLocationTool.getInstance().removeListener(mLocationListener);
    }

    /**
     * 显示数据列表
     *
     * @param
     */
    private void showDataList(String area) {
        showProgressDialog();
        okHttpHyList(area);
    }

    /**
     * 调用地理编码搜索（获取地名）
     *
     * @param latLng
     */
    private void callGeocodeSearch(final LatLng latLng) {
        LatLonPoint point = new LatLonPoint(latLng.latitude, latLng.longitude);
        // 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        RegeocodeQuery query = new RegeocodeQuery(point, 200,
                GeocodeSearch.AMAP);
        // 设置同步逆地理编码请求
        mGeocodeSearch.getFromLocationAsyn(query);
    }


    private AMap.OnMapClickListener onMapClickListener = new AMap.OnMapClickListener() {
        @Override
        public void onMapClick(LatLng latLng) {
            layout_data.setVisibility(View.GONE);

        }
    };

    public String area_name;
    /**
     * 标记点击事件
     */
    private AMap.OnMarkerClickListener mOnMarkerClick = new AMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            OceanAreaInfo bean = (OceanAreaInfo) marker.getObject();
            // 显示地名
            area_name = bean.area_name;
            // 显示数据
            showDataList(bean.area_id);
            return false;
        }

    };

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
                    String tempAddr = result.getRegeocodeAddress().getFormatAddress();
                    // 显示地名
                }
            }
        }

    };

    private void locationSuccess(LatLng latLng) {
        // 切换定位点
//        setLocation(latLng);
        // 调用地理编码搜索
        callGeocodeSearch(latLng);
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

    private DataControl.ListSelect listenermax = new DataControl.ListSelect() {
        @Override
        public void itemClick(int position, String id) {
//            area_id = id;
            requestMapClick();
        }

    };

    private void requestMapClick() {
    }

    // 点数据列表
    private List<Marker> markerOptionsList = new ArrayList<Marker>();

    /**
     * 添加站点图标
     *
     * @param
     */
    private void addMarkersToMap(final List<OceanAreaInfo> areaList) {
        List<OceanAreaInfo> listdata = areaList;
        LatLngBounds.Builder builder = LatLngBounds.builder();
        for (OceanAreaInfo bean : listdata) {
            if (TextUtils.isEmpty(bean.lat) || TextUtils.isEmpty(bean.lon) || TextUtils.isEmpty(bean.area_name)) {
                continue;
            }
            LatLng latLng = new LatLng(Double.parseDouble(bean.lat), Double.parseDouble(bean.lon));
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng)
                    .icon(BitmapDescriptorFactory.fromBitmap(getWeatherIcon()))
                    //.zIndex(MapElementZIndex.markerZIndex)
                    .anchor(0.5f, 0.35f);
            // 添加点数据至缓存列表
            Marker marker = aMap.addMarker(markerOptions);
//            markerOptions.icon(getWeatherIcon());
            marker.setObject(bean);
            markerOptionsList.add(marker);
            builder.include(latLng);
        }
        LatLng latLng;
        if (markerOptionsList.size() > 2) {
            latLng = new LatLng(Double.parseDouble(listdata.get(2).lat), Double.parseDouble(listdata.get(2).lon));
            aMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));
            aMap.moveCamera(CameraUpdateFactory.zoomTo(6));
        }else {
            latLng = new LatLng(Double.parseDouble(listdata.get(0).lat), Double.parseDouble(listdata.get(0).lon));
            aMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));
            aMap.moveCamera(CameraUpdateFactory.zoomTo(6));
        }
    }
    private Bitmap getWeatherIcon() {
        View view = getLayoutInflater().inflate(R.layout.view_ocean_weather_map, null);
        return BitmapDescriptorFactory.fromView(view).getBitmap();
    }

    /**
     * 清除自动站数据
     */
    public void clearAutoSite() {
        for (Marker marker : markerOptionsList) {
            marker.remove();
        }
        markerOptionsList.clear();
    }

    /**
     * 获取海洋气象
     */
    private void okHttpHyArea() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param  = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    String json = param.toString();
                    final String url = CONST.BASE_URL+"hy_area";
                    Log.e("hy_area", url);
                    RequestBody body = FormBody.create(MediaType.parse("application/json; charset=utf-8"), json);
                    OkHttpUtil.enqueue(new Request.Builder().post(body).url(url).build(), new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        }
                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            if (!response.isSuccessful()) {
                                return;
                            }
                            final String result = response.body().string();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (!TextUtil.isEmpty(result)) {
                                        Log.e("hy_area", result);
                                        try {
                                            JSONObject obj = new JSONObject(result);
                                            if (!obj.isNull("b")) {
                                                JSONObject bobj = obj.getJSONObject("b");
                                                if (!bobj.isNull("hy_area")) {
                                                    JSONObject listobj = bobj.getJSONObject("hy_area");
                                                    if (!TextUtil.isEmpty(listobj.toString())) {
                                                        dismissProgressDialog();
                                                        PackAreaPositionDown down = new PackAreaPositionDown();
                                                        down.fillData(listobj.toString());
                                                        addMarkersToMap(down.list_info);
                                                        area_name=down.list_info.get(0).area_name;
                                                        showDataList(down.list_info.get(0).area_id);
                                                    }
                                                }
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            });
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 获取海洋气象
     */
    private void okHttpHyList(final String area) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param  = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    JSONObject info = new JSONObject();
                    info.put("stationId", area);
                    param.put("paramInfo", info);
                    String json = param.toString();
                    Log.e("hy_list", json);
                    final String url = CONST.BASE_URL+"hy_list";
                    Log.e("hy_list", url);
                    RequestBody body = FormBody.create(MediaType.parse("application/json; charset=utf-8"), json);
                    OkHttpUtil.enqueue(new Request.Builder().post(body).url(url).build(), new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        }
                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            if (!response.isSuccessful()) {
                                return;
                            }
                            final String result = response.body().string();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (!TextUtil.isEmpty(result)) {
                                        Log.e("hy_list", result);
                                        try {
                                            JSONObject obj = new JSONObject(result);
                                            if (!obj.isNull("b")) {
                                                JSONObject bobj = obj.getJSONObject("b");
                                                if (!bobj.isNull("hy_list")) {
                                                    JSONObject listobj = bobj.getJSONObject("hy_list");
                                                    if (!TextUtil.isEmpty(listobj.toString())) {
                                                        dismissProgressDialog();
                                                        PackOceanWeatherDown down = new PackOceanWeatherDown();
                                                        down.fillData(listobj.toString());
                                                        layout_data.setVisibility(View.VISIBLE);
                                                        tvAddress.setText(area_name + "( " + down.pub_time + " )");
                                                        list_ocean.clear();
                                                        list_ocean.addAll(down.oceanWeatherList);
                                                        adapterMapOcean.notifyDataSetChanged();
                                                    }
                                                }
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            });
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
