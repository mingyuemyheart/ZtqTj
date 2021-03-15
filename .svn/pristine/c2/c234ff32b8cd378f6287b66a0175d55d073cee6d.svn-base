package com.pcs.ztqtj.control.main_weather;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib.lib_pcs_v3.model.image.ImageFetcher;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackForecastWeatherTipDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackForecastWeatherTipUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.PackWarningCenterYJXXGridIndexDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.PackWarningCenterYJXXGridIndexUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.WarnCenterYJXXGridBean;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.AdapterWarningCenterGrid;
import com.pcs.ztqtj.control.tool.MyConfigure;
import com.pcs.ztqtj.control.tool.SharedPreferencesUtil;
import com.pcs.ztqtj.control.tool.ZtqLocationTool;
import com.pcs.ztqtj.view.activity.product.ActivityMapForecast;
import com.pcs.ztqtj.view.activity.warn.ActivityWarnDetails;

import java.util.ArrayList;
import java.util.List;

/**
 * JiangZy on 2016/6/3.
 */
public class CommandMainRow3 extends CommandMainBase implements AdapterView.OnItemClickListener, AMap.OnMarkerClickListener {
    // 地图默认每像素密度
    private final float DEFAULKT_SCALE_PER_PIXEL = -0.002145532131195068f;

    private Activity mActivity;
    private ViewGroup mRootLayout;
    private ImageFetcher imageFetcher;
    private View mRowView;

    private TextureMapView mMapView;
    // 定位标记
    private Marker mMarker = null;

    private AMap mAMap = null;

    private boolean isChangeCamera = false;

    private GridView gridView = null;

    private AdapterWarningCenterGrid adapter = null;

    private List<WarnCenterYJXXGridBean> dataList = new ArrayList<WarnCenterYJXXGridBean>();
    //格点预警
    private PackWarningCenterYJXXGridIndexDown packDown = null;
    //温馨提示
    private PackForecastWeatherTipUp mPackForecastWeatherTipUp = new PackForecastWeatherTipUp();

    // Bundle
    private Bundle mSavedInstanceState;

    public CommandMainRow3(Activity activity, ViewGroup rootLayout, ImageFetcher imageFetcher, Bundle savedInstanceState) {
        mActivity = activity;
        mRootLayout = rootLayout;
        this.imageFetcher = imageFetcher;
        mSavedInstanceState = savedInstanceState;
    }

    @Override
    protected void init() {
        mRowView = LayoutInflater.from(mActivity).inflate(
                R.layout.item_home_weather_3_new, null);

        mRootLayout.addView(mRowView);

        initView(mRootLayout);
        initMap(mRootLayout);
        initData();
        setStatus(Status.SUCC);
    }

    @Override
    protected void refresh() {
        refreshLocation();
        updateWarningGrid();
    }

    private void initView(View view) {
        Button btn_maps = (Button) view.findViewById(R.id.btn_maps);
        btn_maps.setOnClickListener(mOnBtnMapClick);
    }

    /**
     * 初始化地图
     */
    private void initMap(View view) {
        if (mAMap == null) {
            mMapView = view.findViewById(R.id.map);
            mMapView.onCreate(mSavedInstanceState);
            //mMapView.onResume();

            mAMap = mMapView.getMap();
//            mAMap.setMapLanguage(AMap.ENGLISH);
            mAMap.setMapLanguage(AMap.CHINESE);
            // 点击事件
            mAMap.setOnMapClickListener(mOnMapClick);
            mAMap.setOnMarkerClickListener(this);
            // 缩放
            mAMap.moveCamera(CameraUpdateFactory.zoomTo(16));
            // 显示比例尺
            mAMap.getUiSettings().setScaleControlsEnabled(true);

            // 禁止缩放
            mAMap.getUiSettings().setScrollGesturesEnabled(false);
            mAMap.getUiSettings().setZoomGesturesEnabled(false);
            // 禁止显示缩放按钮
            mAMap.getUiSettings().setZoomControlsEnabled(false);
        }

        gridView = (GridView) mRootLayout.findViewById(R.id.gridview_warning);
        gridView.setOnItemClickListener(this);
    }

    private void initData() {

    }

    private void gotoMap() {
        // 跳转
        Intent it = new Intent();
        it.setClass(mActivity, ActivityMapForecast.class);
        mActivity.startActivity(it);
    }

    /**
     * 刷新定位
     */
    private void refreshLocation() {
        // 地名
        TextView textView = (TextView) mRootLayout.findViewById(R.id.text_im_here);
        textView.setText(mActivity.getResources().getString(R.string.locating) + "定位中...");
        LatLng latLng = ZtqLocationTool.getInstance().getLatLng();
        if (latLng == null) {
            return;
        }
        mMapView = mRootLayout.findViewById(R.id.map);
        mAMap = mMapView.getMap();
        // 标记
        if (mMarker != null) {
            mMarker.remove();
        }
        showPosition(latLng);
        RegeocodeAddress regeocodeAddress = ZtqLocationTool.getInstance().getSearchAddress();
        if (regeocodeAddress != null) {
            textView.setText((mActivity.getResources().getString(R.string.locating)) + regeocodeAddress.getFormatAddress());
        }
    }

    /**
     * 显示位置
     */
    private void showPosition(LatLng latLng) {
        // 定位标识
        MarkerOptions options = new MarkerOptions();
        BitmapDescriptor descriptor = BitmapDescriptorFactory
                .fromResource(R.drawable.icon_location);
        Bitmap resizedBitmap = Bitmap.createBitmap(descriptor.getBitmap(),
                0, 0, descriptor.getWidth(), descriptor.getHeight(),
                getMatrix(mActivity), true);
        // 拷贝图标，防止高德地图回收异常
        Bitmap copyBitmap = descriptor.getBitmap().copy(Bitmap.Config.ARGB_8888, true);
        //Bitmap copyBitmap = resizedBitmap.copy(resizedBitmap.getConfig(), true);
        descriptor = BitmapDescriptorFactory.fromBitmap(copyBitmap);
        options.icon(descriptor);
        mMarker = mAMap.addMarker(options);
//        LatLng cameraLatLng = new LatLng(latLng.latitude - getLatitudeAdd(),
//                latLng.longitude);
        LatLng cameraLatLng = new LatLng(latLng.latitude - DEFAULKT_SCALE_PER_PIXEL,
                latLng.longitude);

        mMarker.setPosition(latLng);
        mAMap.moveCamera(CameraUpdateFactory.changeLatLng(cameraLatLng));
    }

    /**
     * 获取缩放用的Matrix
     *
     * @param activity 设计屏幕宽度
     * @return
     */
    private Matrix getMatrix(Activity activity) {
        final int designWidth = 1280;
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        float scale = ((float) dm.widthPixels) / ((float) designWidth);
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        return matrix;
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

        latitudeAdd = -latitudeAdd / 1000.0d;
        return latitudeAdd;
    }


    private void updateWarningGrid() {
        packDown = (PackWarningCenterYJXXGridIndexDown)PcsDataManager.getInstance().getNetPack(PackWarningCenterYJXXGridIndexUp.NAME);
        if (packDown != null) {
            dataList.clear();
            dataList.addAll(packDown.dataList);
            if (adapter == null) {
                adapter = new AdapterWarningCenterGrid(mActivity, dataList, imageFetcher);
                gridView.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
            }
        }

        String strTip = mActivity.getResources().getString(R.string.map_forecast_tip_init);
        //PackForecastWeatherTipDown packForecastWeatherTipDown = AutoDownloadWeather.getInstance().getPackForecastWeatherTipDown();
        PackForecastWeatherTipDown packForecastWeatherTipDown = (PackForecastWeatherTipDown) PcsDataManager.getInstance().getNetPack(PackForecastWeatherTipUp.NAME);
        if (packForecastWeatherTipDown != null) {
            strTip = packForecastWeatherTipDown.tip;
        }
        TextView tvTip = (TextView) mRootLayout.findViewById(R.id.text_tip);
        if (tvTip != null) {
            tvTip.setText(mActivity.getResources().getString(R.string.map_forecast_tip) + strTip);
        }
    }

    /**
     * 地图点击事件
     */
    private final AMap.OnMapClickListener mOnMapClick = new AMap.OnMapClickListener() {
        @Override
        public void onMapClick(LatLng arg0) {
            // 跳转
            gotoMap();
        }
    };

    View.OnClickListener mOnBtnMapClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            gotoMap();
        }
    };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (packDown != null) {
            WarnCenterYJXXGridBean bean = packDown.dataList.get(position);
            Intent intent = new Intent(mActivity, ActivityWarnDetails.class);
            Bundle bundle=new Bundle();
            bundle.putString("t", "气象预警");
            bundle.putString("i", bean.ico);
            bundle.putString("id", bean.id);
            intent.putExtra(MyConfigure.EXTRA_BUNDLE, bundle);
//            intent.putExtra("t", "气象预警");
//            intent.putExtra("i", bean.ico);
//            intent.putExtra("id", bean.id);

            SharedPreferencesUtil.putData(bean.id,bean.id);
            mActivity.startActivity(intent);
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        gotoMap();
        return false;
    }
}
