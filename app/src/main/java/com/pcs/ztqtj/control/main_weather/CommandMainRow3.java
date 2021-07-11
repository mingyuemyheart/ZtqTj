package com.pcs.ztqtj.control.main_weather;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
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
import com.pcs.lib.lib_pcs_v3.model.image.ImageFetcher;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCity;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackForecastWeatherTipDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.PackWarningCenterYJXXGridIndexDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.WarnCenterYJXXGridBean;
import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.AdapterWarningCenterGrid;
import com.pcs.ztqtj.control.tool.MyConfigure;
import com.pcs.ztqtj.control.tool.SharedPreferencesUtil;
import com.pcs.ztqtj.control.tool.ZtqLocationTool;
import com.pcs.ztqtj.control.tool.utils.TextUtil;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.util.OkHttpUtil;
import com.pcs.ztqtj.view.activity.ActivityMain;
import com.pcs.ztqtj.view.activity.product.ActivityMapForecast;
import com.pcs.ztqtj.view.activity.warn.ActivityWarnDetails;

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
 * 首页-指点天气
 */
public class CommandMainRow3 extends CommandMainBase implements AdapterView.OnItemClickListener, AMap.OnMarkerClickListener {

    // 地图默认每像素密度
    private final float DEFAULKT_SCALE_PER_PIXEL = -0.002145532131195068f;

    private ActivityMain mActivity;
    private ViewGroup mRootLayout;
    private ImageFetcher imageFetcher;
    private View mRowView;
    private TextureMapView mMapView;
    // 定位标记
    private Marker mMarker = null;
    private AMap mAMap = null;
    private GridView gridView = null;
    private AdapterWarningCenterGrid adapter = null;
    private List<WarnCenterYJXXGridBean> dataList = new ArrayList<>();
    //格点预警
    private PackWarningCenterYJXXGridIndexDown packDown = null;
    // Bundle
    private Bundle mSavedInstanceState;
    private TextView tvTip;

    public CommandMainRow3(Activity activity, ViewGroup rootLayout, ImageFetcher imageFetcher, Bundle savedInstanceState) {
        mActivity = (ActivityMain) activity;
        mRootLayout = rootLayout;
        this.imageFetcher = imageFetcher;
        mSavedInstanceState = savedInstanceState;
    }

    @Override
    protected void init() {
        mRowView = LayoutInflater.from(mActivity).inflate(R.layout.item_home_weather_3_new, null);
        mRootLayout.addView(mRowView);
        initMap(mRootLayout);
        initView(mRootLayout);
        setStatus(Status.SUCC);
    }

    @Override
    protected void refresh() {
        refreshLocation();
        okHttpGridWarning();
    }

    private void initView(View view) {
        Button btn_maps = (Button) view.findViewById(R.id.btn_maps);
        btn_maps.setOnClickListener(mOnBtnMapClick);
        tvTip = mRootLayout.findViewById(R.id.text_tip);

        String strTip = mActivity.getResources().getString(R.string.map_forecast_tip_init);
        tvTip.setText(mActivity.getResources().getString(R.string.map_forecast_tip) + strTip);

//        refreshLocation();
//        okHttpGridWarning();
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

    private void gotoMap() {
        mActivity.startActivity(new Intent(mActivity, ActivityMapForecast.class));
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
        BitmapDescriptor descriptor = BitmapDescriptorFactory.fromResource(R.drawable.icon_location);
        // 拷贝图标，防止高德地图回收异常
        Bitmap copyBitmap = descriptor.getBitmap().copy(Bitmap.Config.ARGB_8888, true);
        descriptor = BitmapDescriptorFactory.fromBitmap(copyBitmap);
        options.icon(descriptor);
        mMarker = mAMap.addMarker(options);
        LatLng cameraLatLng = new LatLng(latLng.latitude - DEFAULKT_SCALE_PER_PIXEL, latLng.longitude);
        mMarker.setPosition(latLng);
        mAMap.moveCamera(CameraUpdateFactory.changeLatLng(cameraLatLng));
    }

    /**
     * 地图点击事件
     */
    private final AMap.OnMapClickListener mOnMapClick = new AMap.OnMapClickListener() {
        @Override
        public void onMapClick(LatLng arg0) {
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
            SharedPreferencesUtil.putData(bean.id,bean.id);
            mActivity.startActivity(intent);
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        gotoMap();
        return false;
    }

    /**
     * 获取网格预警
     */
    private void okHttpGridWarning() {
        mActivity.showProgressDialog();
        final PackLocalCity city = ZtqCityDB.getInstance().getCityMain();
        if (city == null) {
            mActivity.dismissProgressDialog();
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param  = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    JSONObject info = new JSONObject();
                    info.put("stationId", city.ID);
                    param.put("paramInfo", info);
                    String json = param.toString();
                    Log.e("yjxx_grad_index_fb", json);
                    final String url = CONST.BASE_URL+"yjxx_grad_index_fb";
                    Log.e("yjxx_grad_index_fb", url);
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
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mActivity.dismissProgressDialog();
//                                    Log.e("yjxx_grad_index_fb", result);
                                    if (!TextUtil.isEmpty(result)) {
                                        try {
                                            JSONObject obj = new JSONObject(result);
                                            if (!obj.isNull("b")) {
                                                JSONObject bobj = obj.getJSONObject("b");
                                                if (!bobj.isNull("yjxx_grad_index_fb")) {
                                                    JSONObject listobj = bobj.getJSONObject("yjxx_grad_index_fb");
                                                    if (!TextUtil.isEmpty(listobj.toString())) {
                                                        packDown = new PackWarningCenterYJXXGridIndexDown();
                                                        packDown.fillData(listobj.toString());
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

//                                                        okHttpTip(PackForecastWeatherTipUp.NAME);
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
     * 获取提示
     */
    private void okHttpTip(final String name) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String url = CONST.BASE_URL+name;
                Log.e("jc_forecast_weather_tip", url);
                OkHttpUtil.enqueue(new Request.Builder().url(url).build(), new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    }
                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            return;
                        }
                        final String result = response.body().string();
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!TextUtil.isEmpty(result)) {
                                    try {
                                        JSONObject obj = new JSONObject(result);
                                        if (!obj.isNull("b")) {
                                            JSONObject bobj = obj.getJSONObject("b");
                                            if (!bobj.isNull("jc_forecast_weather_tip")) {
                                                JSONObject listobj = bobj.getJSONObject("jc_forecast_weather_tip");
                                                if (!TextUtil.isEmpty(listobj.toString())) {
                                                    String strTip = mActivity.getResources().getString(R.string.map_forecast_tip_init);
                                                    PackForecastWeatherTipDown packForecastWeatherTipDown = new PackForecastWeatherTipDown();
                                                    packForecastWeatherTipDown.fillData(listobj.toString());
                                                    if (packForecastWeatherTipDown != null) {
                                                        strTip = packForecastWeatherTipDown.tip;
                                                    }
                                                    tvTip.setText(mActivity.getResources().getString(R.string.map_forecast_tip) + strTip);
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
            }
        }).start();
    }

}
