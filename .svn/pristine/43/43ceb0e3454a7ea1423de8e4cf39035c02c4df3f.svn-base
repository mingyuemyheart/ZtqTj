package com.pcs.ztqtj.view.activity.life.travel;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.AdapterTravelTourSpots;
import com.pcs.ztqtj.control.tool.PoiOverlay;
import com.pcs.ztqtj.control.tool.ZtqLocationTool;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
import com.pcs.lib.lib_pcs_v3.control.tool.Util;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib.lib_pcs_v3.model.image.ImageConstant;
import com.pcs.lib.lib_pcs_v3.model.image.ListenerImageLoad;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackTravelSubjectDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackTravelSubjectUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.TravelSubjectInfo;
import com.pcs.lib_ztqfj_v2.model.pack.net.TravelWeatherSubject;
import com.pcs.lib_ztqfj_v2.model.pack.net.week.PackTravelWeekDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.week.PackTravelWeekUp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityTravelSubjectMap extends FragmentActivityZtqBase implements
        AMap.OnCameraChangeListener, AMap.OnMarkerClickListener {

    // UI
    /**
     * 高德地图控件
     */
    private MapView mMapView = null;

    /**
     * 副标题
     */
    private TextView tvTitle = null;

    /**
     * 景点列表
     */
    private ListView lvSpots = null;

    // 数据
    /**
     * 高德地图对象
     */
    private AMap aMap = null;

    /**
     * 传过来的subject
     */
    private TravelWeatherSubject mSubject = new TravelWeatherSubject();

    /**
     * 景点专题推荐类别
     */
    private boolean isLine = true;

    /**
     * 专题ID
     */
    private String subject_id = "";

    /**
     * 第一点经纬度
     */
    private LatLng firstPoint = null;

    /**
     * 起点图标路径
     */
    private String traffic_ico = "";

    /**
     * 广播对象
     */
    private MyReceiver mReceiver = new MyReceiver();

    /**
     * 专题信息上传包
     */
    private PackTravelSubjectUp packSubjectUp = new PackTravelSubjectUp();

    /**
     * 专题信息下载包
     */
    private PackTravelSubjectDown packSubjectDown = new PackTravelSubjectDown();

    private PackTravelWeekUp packWeekUp = new PackTravelWeekUp();

    /**
     * 图片URL对应标记
     */
    private Map<String, TravelSubjectInfo> mapUrlMarker = new HashMap<String, TravelSubjectInfo>();

    /**
     * 景点列表
     */
    private List<TravelSubjectInfo> tourSpotsList = new ArrayList<TravelSubjectInfo>();

    /**
     * 地图上的景点列表
     */
    private List<Marker> spotsList = new ArrayList<Marker>();

    /**
     * 景点适配器
     */
    private AdapterTravelTourSpots adapterSpots = null;

    /**
     * 透明度动画
     */
    private ValueAnimator alphaShowAnimation = null;

    /**
     * 处理异常收到一周天气广播
     */
    private boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_subject_map);
        setTitleText("旅游气象");
        createImageFetcher();
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

    @Override
    public void onCameraChange(CameraPosition arg0) {
        Log.e("TAG", String.valueOf(arg0.zoom));
        refreshSpots(arg0.zoom);
    }

    @Override
    public void onCameraChangeFinish(CameraPosition arg0) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        TravelSubjectInfo tsi = (TravelSubjectInfo) marker.getObject();
        if (tsi != null) {
            reqNet(tsi);
        }

        return true;
    }

    /**
     * 初始化view
     */
    private void initView() {
        mMapView = (MapView) findViewById(R.id.map);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        lvSpots = (ListView) findViewById(R.id.lv_spots);
        initMap();
    }

    /**
     * 初始化事件
     */
    private void initEvent() {
        aMap.setOnCameraChangeListener(this);
        aMap.setOnMarkerClickListener(this);
        lvSpots.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                TravelSubjectInfo tsi = tourSpotsList.get(position);
                reqNet(tsi);
            }
        });
    }

    /**
     * 初始化数据
     */
    private void initData() {
        PcsDataBrocastReceiver.registerReceiver(this, mReceiver);
        initAnimator();
        mSubject = (TravelWeatherSubject) getIntent().getSerializableExtra(
                "subject");
        mSubject = mSubject == null ? new TravelWeatherSubject() : mSubject;
        tvTitle.setText(mSubject.title);
        isLine = mSubject.is_line.equals("1") ? true : false;
        subject_id = mSubject.subject_id;
        traffic_ico = getResources().getString(R.string.file_download_url)
                + mSubject.traffic_ico;
        double lat = Double.parseDouble(mSubject.lat);
        double log = Double.parseDouble(mSubject.log);
        firstPoint = new LatLng(lat, log);
        adapterSpots = new AdapterTravelTourSpots(this, tourSpotsList,
                getImageFetcher());
        lvSpots.setAdapter(adapterSpots);
    }

    /**
     * 初始化高德地图
     */
    private void initMap() {
        if (aMap == null) {
            aMap = mMapView.getMap();
            aMap.getUiSettings().setZoomControlsEnabled(false);
            mMapView.post(new Runnable() {
                @Override
                public void run() {
                    reqSubjectInfo();
                }
            });
        }
        // initMapEvent();
        // 定位
        location();
    }

    /**
     * 定位
     */
    private void location() {
        LatLng latLng = ZtqLocationTool.getInstance().getLatLng();
        if (latLng != null) {
            aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
        }
    }

    /**
     * 初始化动画
     */
    private void initAnimator() {
        alphaShowAnimation = ValueAnimator.ofInt(0, 255);
        alphaShowAnimation.setDuration(500);
        alphaShowAnimation.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (spotsList.size() > 0) {
                    for (Marker marker : spotsList) {
                        if (marker.getIcons().size() == 1) {
                            BitmapDescriptor bd = marker.getIcons().get(0);
                            Bitmap bm = bd.getBitmap();
                            Bitmap newBm = Bitmap.createBitmap(bm.getWidth(),
                                    bm.getHeight(), Config.ARGB_8888);
                            Canvas canvas = new Canvas(newBm);
                            canvas.drawARGB(0, 0, 0, 0);
                            // config paint
                            final Paint paint = new Paint();
                            paint.setAlpha((Integer) animation
                                    .getAnimatedValue());
                            canvas.drawBitmap(bm, 0, 0, paint);
                            marker.setIcon(BitmapDescriptorFactory
                                    .fromBitmap(bm));
                        }
                    }
                }
            }
        });
        alphaShowAnimation.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                for (Marker marker : spotsList) {
                    marker.setVisible(true);
                    if (marker.getIcons().size() == 1) {
                        BitmapDescriptor bd = marker.getIcons().get(0);
                        Bitmap bm = bd.getBitmap();
                        Bitmap newBm = Bitmap.createBitmap(bm.getWidth(),
                                bm.getHeight(), Config.ARGB_8888);
                        Canvas canvas = new Canvas(newBm);
                        canvas.drawARGB(0, 0, 0, 0);
                        // config paint
                        final Paint paint = new Paint();
                        paint.setAlpha(0);
                        canvas.drawBitmap(bm, 0, 0, paint);
                        marker.setIcon(BitmapDescriptorFactory.fromBitmap(bm));
                    }
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

            }

        });
    }

    private void intentNextActivity(String cityID, String CityName) {
        Intent intent = new Intent(this, ActivityTravelDetail.class);
        intent.putExtra("cityId", cityID);
        intent.putExtra("cityName", CityName);
        startActivity(intent);
    }

    /**
     * 刷新景点
     *
     * @param zoom
     */
    private void refreshSpots(float zoom) {
        if (zoom <= 7.5f) {
            lvSpots.setVisibility(View.VISIBLE);
            for (Marker marker : spotsList) {
                marker.setVisible(false);
            }
        } else if (zoom > 7.5f) {
            lvSpots.setVisibility(View.INVISIBLE);
            for (Marker marker : spotsList) {
                marker.setVisible(true);
            }
        }
    }

    /**
     * 网络请求数据
     *
     * @param cityInfo
     */
    private void reqNet(TravelSubjectInfo cityInfo) {

        if(!isOpenNet()){
            showToast(getString(R.string.net_err));
            return ;
        }
        showProgressDialog();
        packWeekUp = new PackTravelWeekUp();
        packWeekUp.setCity(cityInfo.tour_id, cityInfo.tour_name);
        PackTravelWeekDown down = (PackTravelWeekDown) PcsDataManager.getInstance().getNetPack(packWeekUp.getName());
        if (down == null) {
            flag = true;
            // 判断是缓存数据中是否已经存在，不存在则网络请求取数据
            PcsDataDownload.addDownload(packWeekUp);
        } else {
            // 取城市信息后解析数据
            dismissProgressDialog();
            intentNextActivity(cityInfo.tour_id, cityInfo.tour_name);
        }
    }

    /**
     * 添加图标至地图
     */
    private void addMarksToMap(List<TravelSubjectInfo> dataList) {
        if (dataList != null && dataList.size() > 0) {
            // addMarkToMap(firstPoint);
            for (TravelSubjectInfo tsi : dataList) {
                try {
                    double lat = Double.parseDouble(tsi.lat);
                    double log = Double.parseDouble(tsi.log);
                    LatLng latlng = new LatLng(lat, log);
                    Marker marker = addMarkToMap(latlng);
                    marker.setObject(tsi);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 添加起点图片至地图
     *
     * @param subject
     */
    private void addStartPointToMap(TravelWeatherSubject subject) {
        String preURL = getResources().getString(R.string.file_download_url);
        getImageFetcher().addListener(mListenerImageLoad);
        getImageFetcher().loadImage(preURL + subject.traffic_ico, null,
                ImageConstant.ImageShowType.NONE);
    }

    /**
     * 添加景点图片至地图
     */
    private void addImageToMap(List<TravelSubjectInfo> dataList) {
        if (dataList != null && dataList.size() > 0) {
            if (mapUrlMarker == null) {
                mapUrlMarker = new HashMap<String, TravelSubjectInfo>();
            }
            String preURL = getResources()
                    .getString(R.string.file_download_url);
            getImageFetcher().addListener(mListenerImageLoad);
            for (TravelSubjectInfo tsi : dataList) {
                getImageFetcher().loadImage(preURL + tsi.img_url, null,
                        ImageConstant.ImageShowType.NONE);
                mapUrlMarker.put(preURL + tsi.img_url, tsi);
            }
        }
    }

    /**
     * 图片下载监听
     */
    private ListenerImageLoad mListenerImageLoad = new ListenerImageLoad() {

        @Override
        public void done(String key, boolean isSucc) {
            if (!isSucc) {
                return;
            }

            if (getImageFetcher().getImageCache() == null) {
                return;
            }

            BitmapDrawable drawable = getImageFetcher().getImageCache()
                    .getBitmapFromAllCache(key);
            if (drawable == null) {
                return;
            }
            int width = Util.dip2px(ActivityTravelSubjectMap.this, 70.f);
            int height = Util.dip2px(ActivityTravelSubjectMap.this, 70.f);

            // 计算缩放率，新尺寸除原始尺寸
            float scaleWidth = ((float) width)
                    / drawable.getBitmap().getWidth();
            float scaleHeight = ((float) height)
                    / drawable.getBitmap().getHeight();

            // 创建操作图片用的matrix对象
            Matrix matrix = new Matrix();

            // 缩放图片动作
            if (!key.equals(traffic_ico)) {
                matrix.postScale(scaleWidth, scaleHeight);
            }

            Bitmap resizedBitmap = Bitmap.createBitmap(drawable.getBitmap(), 0,
                    0, drawable.getBitmap().getWidth(), drawable.getBitmap()
                            .getHeight(), matrix, true);
            // 处理异步显示起点图标
            if (key.equals(traffic_ico)) {
                Marker marker = addMarkToMap(firstPoint, resizedBitmap);
            } else { // 处理异步显示景点图标
                Bitmap copyBitmap = resizedBitmap.copy(
                        resizedBitmap.getConfig(), true);
                TravelSubjectInfo info = mapUrlMarker.get(key);
                info.bm = copyBitmap;
                tourSpotsList.add(info);
                // adapterSpots.setData(tourSpotsList);
                adapterSpots.notifyDataSetChanged();
                try {
                    double lat = Double.parseDouble(info.lat);
                    double log = Double.parseDouble(info.log);
                    LatLng latlng = new LatLng(lat, log);
                    Marker marker = addMarkToMap(latlng, resizedBitmap, 1.0f,
                            1.0f);
                    marker.setObject(info);
                    if (!spotsList.contains(marker)) {
                        spotsList.add(marker);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            refreshSpots(aMap.getCameraPosition().zoom);
        }
    };

    /**
     * 将照相机移至合适位置
     *
     * @param dataList
     */
    private void moveCameraToFit(List<TravelSubjectInfo> dataList) {
        if (dataList != null && dataList.size() > 0) {
            List<PoiItem> listPoi = new ArrayList<PoiItem>();
            LatLonPoint firstLLP = new LatLonPoint(firstPoint.latitude,
                    firstPoint.longitude);
            PoiItem firstP = new PoiItem("", firstLLP, "", "");
            listPoi.add(firstP);
            for (TravelSubjectInfo tsi : dataList) {
                try {
                    double lat = Double.parseDouble(tsi.lat);
                    double log = Double.parseDouble(tsi.log);
                    LatLonPoint llp = new LatLonPoint(lat, log);
                    PoiItem pi = new PoiItem("", llp, "", "");
                    listPoi.add(pi);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // int maxZoom = (int) aMap.getMaxZoomLevel();
            // aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(
            // builder.build(), maxZoom));
            // 自动缩放地图
            PoiOverlay poiOverlay = new PoiOverlay(aMap, listPoi);
            poiOverlay.zoomToSpan();
        }
    }

    /**
     * 添加线至地图
     *
     * @param dataList
     */
    private void addLineToMap(List<TravelSubjectInfo> dataList, boolean isLine) {
        if (dataList != null && dataList.size() > 0) {
            float width = Util.dip2px(this, 6);
            // 当为线性连线时
            if (isLine) {
                PolylineOptions lineOptions = new PolylineOptions();
                lineOptions.add(firstPoint);
                for (TravelSubjectInfo tsi : dataList) {
                    try {
                        double lat = Double.parseDouble(tsi.lat);
                        double log = Double.parseDouble(tsi.log);
                        LatLng latlng = new LatLng(lat, log);
                        lineOptions.add(latlng);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                aMap.addPolyline(lineOptions.width(width).color(
                        ActivityTravelSubjectMap.this.getResources().getColor(
                                R.color.map_line)));
            } else { // 当为放射性连线时
                for (TravelSubjectInfo tsi : dataList) {
                    try {
                        double lat = Double.parseDouble(tsi.lat);
                        double log = Double.parseDouble(tsi.log);
                        LatLng latlng = new LatLng(lat, log);
                        PolylineOptions lineOptions = new PolylineOptions();
                        lineOptions.add(firstPoint);
                        lineOptions.add(latlng);
                        aMap.addPolyline(lineOptions.width(width).color(
                                ActivityTravelSubjectMap.this.getResources()
                                        .getColor(R.color.map_line)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 请求专题信息
     */
    private void reqSubjectInfo() {

        if(!isOpenNet()){
            showToast(getString(R.string.net_err));
            return ;
        }
        packSubjectUp = new PackTravelSubjectUp();
        if (mSubject == null) {
            TravelWeatherSubject subject = (TravelWeatherSubject) getIntent()
                    .getSerializableExtra("subject");
            subject = subject == null ? new TravelWeatherSubject() : subject;
            packSubjectUp.subject_id = subject.subject_id;
        } else {
            packSubjectUp.subject_id = mSubject.subject_id;
        }
        PcsDataDownload.addDownload(packSubjectUp);
        // String str = PcsDataManager.getInstance().loadData(
        // packSubjectUp.getName());
        // if (str == null) {
        // PcsDataDownload.addDownload(packSubjectUp);
        // } else {
        // if (packSubjectDown == null) {
        // packSubjectDown = new PackTravelSubjectDown();
        // }
        // packSubjectDown.fillData(str);
        // // addMarkToMap(packSubjectDown.tour_list);
        // // 移动摄像头
        // moveCameraToFit(packSubjectDown.tour_list);
        // // 画点
        // addMarksToMap(packSubjectDown.tour_list);
        // // 画起点
        // addStartPointToMap(mSubject);
        // // 画线
        // addLineToMap(packSubjectDown.tour_list, isLine);
        // // 画景点
        // addImageToMap(packSubjectDown.tour_list);
        // }
    }

    /**
     * 添加单个坐标至地图，自定义图标，自定义锚点
     *
     * @param latlng
     * @param bm
     * @param x
     * @param y
     * @return
     */
    private Marker addMarkToMap(LatLng latlng, Bitmap bm, float x, float y) {
        MarkerOptions markerOption = new MarkerOptions();
        markerOption.position(latlng).draggable(true).setFlat(false)
                .anchor(x, y);
        if (bm != null) {
            markerOption.icon(BitmapDescriptorFactory.fromBitmap(bm));
        } else {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                    R.drawable.icon_travel_map_point_default);

            int dstWidth = Util.dip2px(this, 15);
            int dstHeight = Util.dip2px(this, 15);

            Bitmap newBm = Bitmap.createScaledBitmap(bitmap, dstWidth,
                    dstHeight, false);
            markerOption.icon(BitmapDescriptorFactory.fromBitmap(newBm));
            // markerOption.icon(BitmapDescriptorFactory
            // .fromResource(R.drawable.icon_travel_map_point_default));
        }
        return aMap.addMarker(markerOption);
    }

    /**
     * 添加单个坐标至地图，自定义图标, 默认锚点坐标(0.5, 0.5)
     *
     * @param latlng
     * @param bm
     * @return
     */
    private Marker addMarkToMap(LatLng latlng, Bitmap bm) {
        return addMarkToMap(latlng, bm, 0.5f, 0.5f);
    }

    /**
     * 添加单个坐标至地图，默认图标
     *
     * @param latlng
     * @return
     */
    private Marker addMarkToMap(LatLng latlng) {
        return addMarkToMap(latlng, null);
    }

    /**
     * 广播
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

            // 套餐信息
            if (packSubjectUp.getName().equals(nameStr)) {
                dismissProgressDialog();
                packSubjectDown = (PackTravelSubjectDown) PcsDataManager.getInstance().getNetPack(nameStr);
                if (packSubjectDown == null) {
                    return;
                }
                // addMarkToMap(packSubjectDown.tour_list);
                // 移动摄像头
                moveCameraToFit(packSubjectDown.tour_list);
                // 画点
                addMarksToMap(packSubjectDown.tour_list);
                // 画起点
                addStartPointToMap(mSubject);
                // 画线
                addLineToMap(packSubjectDown.tour_list, isLine);
                // 画景点
                addImageToMap(packSubjectDown.tour_list);
            }
            if (nameStr.contains("weeklytq")) {
                dismissProgressDialog();
                if (flag) {
                    PackTravelWeekDown down = (PackTravelWeekDown) PcsDataManager.getInstance().getNetPack(nameStr);
                    if (down == null) {
                        return;
                    }
                    String cityId = nameStr.substring(nameStr.indexOf("#") + 1,
                            nameStr.indexOf("_"));
                    String cityName = nameStr
                            .substring(nameStr.indexOf("_") + 1);
                    intentNextActivity(cityId, cityName);
                    flag = false;
                }
            }
        }
    }

}
