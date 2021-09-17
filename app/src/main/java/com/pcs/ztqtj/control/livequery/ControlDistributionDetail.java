package com.pcs.ztqtj.control.livequery;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.GroundOverlay;
import com.amap.api.maps.model.GroundOverlayOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.net.column.ColumnInfo;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.FycxFbtBean;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.FycxFbtLdBean;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackFycxFbtDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackFycxFbtLdDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackFycxFbtLdUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackFycxFbtUp;
import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.image.ImageLoadFromUrl;
import com.pcs.ztqtj.control.tool.image.ImageMultipleLoadFromUrl;
import com.pcs.ztqtj.control.tool.utils.TextUtil;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.util.OkHttpUtil;
import com.pcs.ztqtj.view.activity.livequery.ActivityLiveQuery;
import com.pcs.ztqtj.view.activity.livequery.LegendInterval;
import com.pcs.ztqtj.view.fragment.livequery.FragmentDistributionMap;

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

import static com.pcs.ztqtj.control.livequery.ControlDistribution.ColumnCategory.RAIN;
import static com.pcs.ztqtj.control.livequery.ControlDistribution.ColumnCategory.WIND;
import static com.pcs.ztqtj.control.livequery.ControlDistribution.DistributionStatus.CLOUD;
import static com.pcs.ztqtj.control.livequery.ControlDistribution.DistributionStatus.DB;
import static com.pcs.ztqtj.control.livequery.ControlDistribution.DistributionStatus.GJ;
import static com.pcs.ztqtj.control.livequery.ControlDistribution.DistributionStatus.NONE;
import static com.pcs.ztqtj.control.livequery.ControlDistribution.DistributionStatus.RADAR;
import static com.pcs.ztqtj.control.livequery.ControlDistribution.DistributionStatus.SB;
import static com.pcs.ztqtj.control.livequery.ControlDistribution.DistributionStatus.ZD;

/**
 * 监测预报-实况查询-要素分布图-色斑图操作控制器-下一级
 */
public class ControlDistributionDetail {

    private FragmentDistributionMap mFragment;
    private Context mContext;
    private ActivityLiveQuery mActivity;
    private ViewGroup mRootLayout;
    private AMap aMap;
    private TextView tvPublicTime;
    // 轮播图片信息
    private TextView tvPlayInfo;
    // 是否是全国类型
    private boolean isProvince = false;
    private CheckBox cbSb, cbGj, cbZd, cbDb, cbRadar, cbCloud;

    // 当前色斑图flag
    private ColumnInfo currentFlagInfo = new ColumnInfo();
    // 当前站点信息
    private ColumnInfo currentSiteInfo = new ColumnInfo();
    // 当前栏目类型
    ControlDistribution.ColumnCategory currentColumn = RAIN;
    // 当前分布图状态
    private ControlDistribution.DistributionStatus currentStatus = SB;

    private static final float MAP_ZOOM = 7.2f;

    // 分布图数据
    private GroundOverlay mDistributionGroundoverlay;
    private GroundOverlay mRadarGroundoverlay;
    private GroundOverlay mCloudGroundoverlay;

    private List<GroundOverlayOptions> mRadarGroundoverlayList = new ArrayList<>();
    private List<GroundOverlayOptions> mCloudGroundoverlayList = new ArrayList<>();

    //private PackFycxFbtUp fycxFbtUp = new PackFycxFbtUp();
    private PackFycxFbtLdUp fycxFbtLdUp = new PackFycxFbtLdUp();

    private List<FycxFbtLdBean> mListData = new ArrayList<>();

    // 点数据列表
    private List<Marker> markerOptionsList = new ArrayList<Marker>();
    // 风向站点
    private List<Marker> windMarkerOptionsList = new ArrayList<>();

    public ControlDistributionDetail(FragmentDistributionMap fragment, ActivityLiveQuery activity, ViewGroup rootLayout) {
        mFragment = fragment;
        mActivity = activity;
        mContext = activity;
        mRootLayout = rootLayout;
        aMap = mFragment.getMap();
        isProvince = mFragment.getIsProvince();
        tvPublicTime = (TextView) rootLayout.findViewById(R.id.tv_public_time);
        tvPlayInfo = (TextView) rootLayout.findViewById(R.id.tv_play_info);
        cbSb = (CheckBox) rootLayout.findViewById(R.id.rb_sb);
        cbGj = rootLayout.findViewById(R.id.rb_gj);
        cbZd = (CheckBox) rootLayout.findViewById(R.id.rb_zd);
        cbDb = rootLayout.findViewById(R.id.rb_db);
        cbRadar = (CheckBox) rootLayout.findViewById(R.id.rb_radar);
        cbCloud = (CheckBox) rootLayout.findViewById(R.id.rb_cloud);
//        PcsDataBrocastReceiver.registerReceiver(activity, receiver);
    }

    /**
     * 隐藏轮播信息
     */
    public void hidePlayInfo() {
        tvPlayInfo.setVisibility(View.GONE);
    }

    /**
     * 添加分布图到地图
     * @param status
     * @param down
     */
    private void addPolygonToMap(final ControlDistribution.DistributionStatus status, final PackFycxFbtDown down) {
        if (TextUtils.isEmpty(down.img_url)) {
            mActivity.dismissProgressDialog();
            mActivity.showToast("无数据！");
            return;
        }

        String url = mContext.getString(R.string.file_download_url)+down.img_url;
        Log.e("sebantu", url);
        boolean isP = false;
        if(currentSiteInfo.name.equals("全国")) {
            isP = true;
        }
        //mImageFetcher.loadImage(url, null, ImageConstant.ImageShowType.NONE);
        ImageLoadFromUrl
                .getInstance()
                .setParams(url, onCompleteListener)
                .setObject(status, down, isP)
                .start();

        tvPublicTime.setText(down.pub_time + " 更新");
    }

    private void addMultiplePolygonToMap(final ControlDistribution.DistributionStatus status, final PackFycxFbtLdDown down) {
        List<String> urlList = new ArrayList<>();
        for(int i = 0; i < down.info_list.size(); i++) {
            String url = mContext.getString(R.string.file_download_url)+down.info_list.get(i).img_url;
            urlList.add(url);
        }
        boolean isP = false;
        if(currentSiteInfo.name.equals("全国")) {
            isP = true;
        }
        ImageMultipleLoadFromUrl
                .getInstance()
                .setParams(urlList, onMultipleCompleteListener)
                .setObject(status, down, isP)
                .startMultiple();
    }

    /**
     * 添加站点图标
     * @param down
     */
    private void addMarkersToMap(final PackFycxFbtDown down) {
        if(down.list.size() == 0) {
            mActivity.showToast("无数据！");
            return;
        }
        List<FycxFbtBean> listdata = down.list;
//        LatLngBounds.Builder builder = LatLngBounds.builder();
        for (FycxFbtBean bean : listdata) {
            if (TextUtils.isEmpty(bean.lat) || TextUtils.isEmpty(bean.lon) || TextUtils.isEmpty(bean.val)) {
                continue;
            }
            LatLng latLng = new LatLng(Double.parseDouble(bean.lat), Double.parseDouble(bean.lon));
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng)
                    .icon(BitmapDescriptorFactory.fromBitmap(getIcon(currentColumn, bean.val)))
                    //.zIndex(MapElementZIndex.markerZIndex)
                    .anchor(0.5f, 0.35f);
            // 添加点数据至缓存列表
            Marker marker = aMap.addMarker(markerOptions);
            //marker.setObject(ZtqCityDB.getInstance().getStation(bean.sta_name));
            marker.setObject(bean);
            markerOptionsList.add(marker);
//            builder.include(latLng);
        }

        if(!cbRadar.isChecked() && !cbCloud.isChecked()) {
            if (markerOptionsList.size() != 0) {
                if (markerOptionsList.size() == 1) {
                    aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerOptionsList.get(0).getPosition(), MAP_ZOOM));
                } else {
//                    aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 10));
                }
            }
        }
        mActivity.dismissProgressDialog();
        tvPublicTime.setText(down.pub_time + " 更新");
    }

    /**
     * 添加风向图标
     * @param down
     */
    private void addWindMarkersToMap(final PackFycxFbtDown down) {
        if(down.list.size() == 0) {
            mActivity.showToast("无数据！");
            return;
        }
        List<FycxFbtBean> listdata = down.list;
//        LatLngBounds.Builder builder = LatLngBounds.builder();
        for (FycxFbtBean bean : listdata) {
            if (TextUtils.isEmpty(bean.lat) || TextUtils.isEmpty(bean.lon) ||
                    TextUtils.isEmpty(bean.val) || TextUtils.isEmpty(bean.fx)) {
                continue;
            }
            LatLng latLng = new LatLng(Double.parseDouble(bean.lat), Double.parseDouble(bean.lon));
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng)
                    .icon(BitmapDescriptorFactory.fromBitmap(getIcon(currentColumn, bean.val)))
                    //.zIndex(MapElementZIndex.markerZIndex)
                    .rotateAngle(-Float.parseFloat(bean.fx))
                    .anchor(0.5f, 0.35f);
            // 添加点数据至缓存列表
            Marker marker = aMap.addMarker(markerOptions);
            //marker.setObject(ZtqCityDB.getInstance().getStation(bean.sta_name));
            marker.setObject(bean);
            windMarkerOptionsList.add(marker);
//            builder.include(latLng);
        }

        if(windMarkerOptionsList.size() != 0) {
            if(windMarkerOptionsList.size() == 1) {
                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(windMarkerOptionsList.get(0).getPosition(), MAP_ZOOM));
            } else {
//                aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 10));
            }
        }
        mActivity.dismissProgressDialog();
        tvPublicTime.setText(down.pub_time + " 更新");
    }

    /**
     * 添加图片至地图
     * @param status
     * @param position
     */
    public void addPolyToMap(ControlDistribution.DistributionStatus status, int position, boolean isMoveCamera) {
        if(status == RADAR) {
            if(mRadarGroundoverlayList.size() > position) {
                if(mRadarGroundoverlay != null) {
                    mRadarGroundoverlay.remove();
                }
                GroundOverlayOptions options = mRadarGroundoverlayList.get(position);
                mRadarGroundoverlay = aMap.addGroundOverlay(options);
                if(isMoveCamera) {
                    aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(options.getBounds(), 100));
                }
                if(mListData.size() > position) {
                    tvPlayInfo.setVisibility(View.VISIBLE);
                    tvPlayInfo.setText("雷达：" + mListData.get(position).pub_time);
                }
            }
        } else {
            if(mCloudGroundoverlayList.size() > position) {
                if(mCloudGroundoverlay != null) {
                    mCloudGroundoverlay.remove();
                }
                GroundOverlayOptions options = mCloudGroundoverlayList.get(position);
                mCloudGroundoverlay = aMap.addGroundOverlay(options);
                if(isMoveCamera) {
                    aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(options.getBounds(), 100));
                }
                if(mListData.size() > position) {
                    tvPlayInfo.setVisibility(View.VISIBLE);
                    tvPlayInfo.setText("云图：" + mListData.get(position).pub_time);
                }
            }
        }
    }

    /**
     * 通过aqi获取图标
     *
     * @param value
     * @return
     */
    private Bitmap getIcon(ControlDistribution.ColumnCategory column, String value) {
        boolean isTotal = false;
        if(currentFlagInfo != null &&
                !currentFlagInfo.type.equals("1")) {
            isTotal = true;
        }

        float fValue = Float.parseFloat(value);
        int resid;
        if(column == RAIN && (currentStatus == ZD || currentStatus == DB || currentStatus == GJ)) {
            resid = LegendInterval.getInstance().getDrawableId(column, fValue, isTotal);
        } else if(column == WIND) {
            if(currentStatus == SB) {
                resid = LegendInterval.getInstance().getWindDrawableId(fValue, true);
            } else {
                resid = LegendInterval.getInstance().getWindDrawableId(fValue, false);
            }
        } else {
            resid = LegendInterval.getInstance().getDrawableId(column, fValue);
        }
        //视图
        View view = mActivity.getLayoutInflater().inflate(R.layout.mymarker, null);
        view.setBackgroundResource(resid);
        if((column == WIND && currentStatus != SB) || (column != WIND)) {
            TextView textView = (TextView) view.findViewById(R.id.marker_text);
            textView.setTextColor(mActivity.getResources().getColor(LegendInterval.getInstance().getTextColorId(column, fValue)));
            textView.setText(value);
        }
        return BitmapDescriptorFactory.fromView(view).getBitmap();
    }

    private ImageLoadFromUrl.OnCompleteListener onCompleteListener = new ImageLoadFromUrl.OnCompleteListener() {
        @Override
        public void onComplete(Bitmap bitmap, Object... object) {
            if (bitmap != null && object.length == 3) {
                LatLng leftTop = null;
                LatLng rightBottom = null;
                PackFycxFbtDown down = (PackFycxFbtDown) object[1];
                for (FycxFbtBean bean : down.list) {
                    if (bean.l_type.equals("1")) { // 左上角
                        leftTop = new LatLng(Double.parseDouble(converLagLon(bean.lat)), Double.parseDouble
                                (converLagLon(bean.lon)));
                    } else if (bean.l_type.equals("2")) {
                        rightBottom = new LatLng(Double.parseDouble(converLagLon(bean.lat)), Double.parseDouble
                                (converLagLon(bean.lon)));
                    }
                }
                if (leftTop == null || rightBottom == null) {
                    return;
                }
                LatLng northeast = new LatLng(rightBottom.latitude, leftTop.longitude);
                LatLng southwest = new LatLng(leftTop.latitude, rightBottom.longitude);
//                boolean isP = (boolean) object[2];
//                if (isP) {
//                    northeast = new LatLng(rightBottom.latitude - 3.8f, leftTop.longitude + 0.2f);
//                    southwest = new LatLng(leftTop.latitude - 1.5f, rightBottom.longitude + 0.5f);
////                    northeast = new LatLng(rightBottom.latitude - 3.8f, leftTop.longitude + 0.2f);
////                    southwest = new LatLng(leftTop.latitude - 5f, rightBottom.longitude + 0.5f);
//                } else {
//                    northeast = new LatLng(rightBottom.latitude-0.02f, leftTop.longitude);
//                    southwest = new LatLng(leftTop.latitude-0.02f, rightBottom.longitude);
//                }
                BitmapDescriptor descriptor = BitmapDescriptorFactory.fromBitmap(bitmap);
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(northeast);
                builder.include(southwest);
                ControlDistribution.DistributionStatus status = (ControlDistribution.DistributionStatus) object[0];

                switch (status) {
                    case SB: {
                        if(mDistributionGroundoverlay != null) {
                            mDistributionGroundoverlay.remove();
                        }
                        mDistributionGroundoverlay = aMap.addGroundOverlay(new GroundOverlayOptions()
                                .image(descriptor)
                                .transparency(0.1f)
                                .positionFromBounds(builder.build())
                                .zIndex(MapElementZIndex.polygonZIndex)
                        );
                        if(!cbRadar.isChecked() && !cbCloud.isChecked()) {
                            aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100));
                        }
                        break;
                    }
                    case RADAR: {
                        if(mRadarGroundoverlay != null) {
                            mRadarGroundoverlay.remove();
                        }
                        mRadarGroundoverlay = aMap.addGroundOverlay(new GroundOverlayOptions()
                                .image(descriptor)
                                .positionFromBounds(builder.build())
                                .zIndex(MapElementZIndex.polygonZIndex)
                        );
                        aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100));
                        break;
                    }
                    case CLOUD: {
                        if(mCloudGroundoverlay != null) {
                            mCloudGroundoverlay.remove();
                        }
                        mCloudGroundoverlay = aMap.addGroundOverlay(new GroundOverlayOptions()
                                .image(descriptor)
                                .positionFromBounds(builder.build())
                                .zIndex(MapElementZIndex.polygonZIndex)
                        );
                        aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100));
                        break;
                    }
                }

                mActivity.dismissProgressDialog();
            }
        }
    };

    ImageMultipleLoadFromUrl.OnMultipleCompleteListener onMultipleCompleteListener = new ImageMultipleLoadFromUrl.OnMultipleCompleteListener() {
        @Override
        public void onComplete(List<Bitmap> bitmapList, Object... object) {
            if(bitmapList == null || bitmapList.size() == 0 || object.length != 3) {
                return;
            }
            ControlDistribution.DistributionStatus status = (ControlDistribution.DistributionStatus) object[0];
            PackFycxFbtLdDown down = (PackFycxFbtLdDown) object[1];
            mRadarGroundoverlayList.clear();
            mCloudGroundoverlayList.clear();
            for(Bitmap bitmap : bitmapList) {
                int index = bitmapList.indexOf(bitmap);
                LatLng leftTop = null;
                LatLng rightBottom = null;
                for (FycxFbtBean bean : down.info_list.get(index).s_info_list) {
                    if (bean.l_type.equals("1")) { // 左上角
                        leftTop = new LatLng(Double.parseDouble(converLagLon(bean.lat)), Double.parseDouble
                                (converLagLon(bean.lon)));
                    } else if (bean.l_type.equals("2")) {
                        rightBottom = new LatLng(Double.parseDouble(converLagLon(bean.lat)), Double.parseDouble
                                (converLagLon(bean.lon)));
                    }
                }
                if (leftTop == null || rightBottom == null) {
                    return;
                }
                LatLng northeast = new LatLng(rightBottom.latitude, leftTop.longitude);
                LatLng southwest = new LatLng(leftTop.latitude, rightBottom.longitude);
//                boolean isP = (boolean) object[2];
//                if (isP) {
//                    northeast = new LatLng(rightBottom.latitude - 3.8f, leftTop.longitude + 0.2f);
//                    southwest = new LatLng(leftTop.latitude - 1.5f, rightBottom.longitude + 0.5f);
////                    northeast = new LatLng(rightBottom.latitude - 3.8f, leftTop.longitude + 0.2f);
////                    southwest = new LatLng(leftTop.latitude - 5f, rightBottom.longitude + 0.5f);
//                } else {
//                    northeast = new LatLng(rightBottom.latitude-0.02f, leftTop.longitude);
//                    southwest = new LatLng(leftTop.latitude-0.02f, rightBottom.longitude);
//                }
                BitmapDescriptor descriptor = BitmapDescriptorFactory.fromBitmap(bitmap);
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(northeast);
                builder.include(southwest);
                switch (status) {
                    case RADAR: {
                        mRadarGroundoverlayList.add(new GroundOverlayOptions()
                                .image(descriptor)
                                .positionFromBounds(builder.build())
                                .zIndex(MapElementZIndex.polygonZIndex));
                        break;
                    }
                    case CLOUD: {
                        mCloudGroundoverlayList.add(new GroundOverlayOptions()
                                .image(descriptor)
                                .positionFromBounds(builder.build())
                                .zIndex(MapElementZIndex.polygonZIndex));
                        break;
                    }
                }
            }
            multipleLoadFinish(status);
            mActivity.dismissProgressDialog();
        }

        @Override
        public void onProgress(int progress, int max) {

        }
    };

    /**
     * 轮播雷达/云图
     */
    private void multipleLoadFinish(ControlDistribution.DistributionStatus status) {
        callBack.onFinish(status, status == RADAR ? mRadarGroundoverlayList : mCloudGroundoverlayList);
    }

    private MultiplePictureCallBack callBack;

    /**
     * 设置雷达/云图加载完成的回调回调
     * @param callBack
     */
    public void setCallBack(MultiplePictureCallBack callBack) {
        this.callBack = callBack;
    }

    /**
     * 转换坐标值
     * @param value
     */
    private String converLagLon(String value) {
        if(value.charAt(value.length() -1) == ';') {
            return value.substring(0, value.length()-1);
        }
        return value;
    }

    public void clear(ControlDistribution.DistributionStatus status) {
        switch (status) {
            case SB:
                if(currentColumn == WIND) {
                    clearWindSite();
                } else {
                    clearDistribution();
                }
                break;
            case ZD:
            case DB:
            case GJ:
                clearAutoSite();
                break;
            case RADAR:
                clearRadar();
                break;
            case CLOUD:
                clearCloud();
                break;
        }
    }

    /**
     * 清除所有
     */
    public void clearAll() {
        clearDistribution();
        clearAutoSite();
        clearRadar();
        clearCloud();
    }

    /**
     * 清除分布图
     */
    public void clearDistribution() {
        if (mDistributionGroundoverlay != null) {
            mDistributionGroundoverlay.setVisible(false);
            mDistributionGroundoverlay.remove();
            mDistributionGroundoverlay = null;
        }
        tvPublicTime.setText("");
    }

    /**
     * 清除自动站数据
     */
    public void clearAutoSite() {
        for (Marker marker : markerOptionsList) {
            marker.remove();
        }
        markerOptionsList.clear();
        tvPublicTime.setText("");
    }

    /**
     * 清除风向站点
     */
    public void clearWindSite() {
        for (Marker marker : windMarkerOptionsList) {
            marker.remove();
        }
        windMarkerOptionsList.clear();
        tvPublicTime.setText("");
    }

    /**
     * 清除雷达图
     */
    public void clearRadar() {
        if (mRadarGroundoverlay != null) {
            mRadarGroundoverlay.setVisible(false);
            mRadarGroundoverlay.remove();
            mRadarGroundoverlay = null;
        }
    }

    /**
     * 清除云图
     */
    public void clearCloud() {
        if (mCloudGroundoverlay != null) {
            mCloudGroundoverlay.setVisible(false);
            mCloudGroundoverlay.remove();
            mCloudGroundoverlay = null;
        }
    }

    public ControlDistribution.DistributionStatus getMultipleImageState() {
        ControlDistribution.DistributionStatus status = NONE;
        if(cbRadar.isChecked()) {
            status = RADAR;
        } else if(cbCloud.isChecked()) {
            status = CLOUD;
        }
        return status;
    }

    /**
     * 请求分布图数据
     *
     * @param column
     * @param status
     */
    public void reqDistribution(ControlDistribution.ColumnCategory column, final ControlDistribution.DistributionStatus status,
                                ColumnInfo siteInfo, ColumnInfo flagInfo) {
        currentColumn = column;
        currentStatus = status;
        currentSiteInfo = siteInfo;
        currentFlagInfo = flagInfo;
        if (TextUtils.isEmpty(flagInfo.type) || TextUtils.isEmpty(siteInfo.type)) {
            return;
        }
        String type = "";
        String img_type = "";
        String flag = "";
        String area_id = "";
        String name = "";
        switch (column) {
            case RAIN: // 雨量
                type = PackFycxFbtUp.RAIN;
                break;
            case TEMPERATURE: // 气温
                type = PackFycxFbtUp.TEMPERATURE;
                break;
            case WIND: // 风速
                type = PackFycxFbtUp.WIND;
                break;
            case VISIBILITY: // 能见度
                type = PackFycxFbtUp.VISIBILITY;
                break;
            case PRESSURE: // 气压
                type = PackFycxFbtUp.PRESSURE;
                break;
            case HUMIDITY:
                type = PackFycxFbtUp.HUMIDITY;
                break;
        }

        if(status == SB || status == ZD || status == DB || status == GJ) {
            switch (status) {
                case SB: // 色斑
                    if (column == WIND) {
                        name = "fycx_fbt_station";
                        img_type = PackFycxFbtUp.ZD;
                        img_type = "2010403";
                    } else {
                        name = "fycx_fbt_img";
                        img_type = PackFycxFbtUp.SB;
                        img_type = "2010401";
                    }
                    flag = flagInfo.type;
                    area_id = siteInfo.type;
                    break;
                case ZD: // 自动站
                    name = "fycx_fbt_station";
                    img_type = PackFycxFbtUp.ZD;
                    img_type = "2010404";
                    flag = flagInfo.type;
                    area_id = siteInfo.type;
                    break;
                case DB: // 代表站
                    name = "fycx_fbt_station";
                    img_type = PackFycxFbtUp.DB;
                    img_type = "2010403";
                    flag = flagInfo.type;
                    area_id = siteInfo.type;
                    break;
                case GJ: // 国家站
                    name = "fycx_fbt_station";
                    img_type = PackFycxFbtUp.GJ;
                    img_type = "2010402";
                    flag = flagInfo.type;
                    area_id = siteInfo.type;
                    break;
                case NONE:
                    return;
            }
            PackFycxFbtUp fycxFbtUp = new PackFycxFbtUp();
            fycxFbtUp.type = type;
            fycxFbtUp.img_type = img_type;
            fycxFbtUp.falg = flag;
            fycxFbtUp.area_id = area_id;
            okHttpFycxFbt(name,fycxFbtUp, status);
        } else {
            switch (status) {
                case RADAR: // 雷达
                    img_type = PackFycxFbtUp.RADAR;
                    img_type = "2010405";
                    area_id = siteInfo.type;
                    break;
                case CLOUD: // 云图
                    img_type = PackFycxFbtUp.CLOUD;
                    img_type = "2010406";
                    area_id = "25169";
                    break;
                case NONE:
                    return;
            }
//            fycxFbtLdUp = new PackFycxFbtLdUp();
//            fycxFbtLdUp.area_id = area_id;
//            fycxFbtLdUp.img_type = img_type;
//            fycxFbtLdUp.type = type;
//            PcsDataDownload.addDownload(fycxFbtLdUp);

            PackFycxFbtUp fycxFbtUp = new PackFycxFbtUp();
            fycxFbtUp.type = type;
            fycxFbtUp.img_type = img_type;
            fycxFbtUp.falg = flag;
            fycxFbtUp.area_id = area_id;
            okHttpFycxFbt(name,fycxFbtUp, status);
        }
    }

    public interface MultiplePictureCallBack {
        void onFinish(ControlDistribution.DistributionStatus status, List<GroundOverlayOptions> list);
    }

    private PcsDataBrocastReceiver receiver = new PcsDataBrocastReceiver() {

        @Override
        public void onReceive(String nameStr, String errorStr) {
            if (nameStr.equals(fycxFbtLdUp.getName())) {
                PackFycxFbtLdDown down = (PackFycxFbtLdDown) PcsDataManager.getInstance().getNetPack(nameStr);
                if(down == null) {
                    return ;
                }
                mListData.clear();
                mListData.addAll(down.info_list);
                addMultiplePolygonToMap(getMultipleImageState(), down);
            }
        }
    };

    /**
     * 获取站点数据，包括色斑图和打点
     */
    private void okHttpFycxFbt(final String name, final PackFycxFbtUp fycxFbtUp, final ControlDistribution.DistributionStatus status) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param  = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    JSONObject info = new JSONObject();
                    info.put("stationId", fycxFbtUp.type);//10(雨量),12(风况),11(气温),13(能见度),14(气压),17(相对湿度)
                    info.put("area", fycxFbtUp.area_id);//例如:china/101030100，右上下拉框
                    info.put("type", fycxFbtUp.falg);//数据范围
                    info.put("limit", fycxFbtUp.img_type);//色斑图10103020401，国家站10103020402，代表站10103020403，自动站10103020404，雷达10103020405，台风10103020406
                    param.put("paramInfo", info);
                    String json = param.toString();
                    Log.e(name, json);
                    final String url = CONST.BASE_URL+name;
                    Log.e(name, url);
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
                                    Log.e(name, result);
                                    if (!TextUtil.isEmpty(result)) {
                                        try {
                                            JSONObject obj = new JSONObject(result);
                                            if (!obj.isNull("b")) {
                                                JSONObject bobj = obj.getJSONObject("b");
                                                if (!bobj.isNull("fycx_fbt")) {
                                                    JSONObject fycx_fbt = bobj.getJSONObject("fycx_fbt");
                                                    if (!TextUtil.isEmpty(fycx_fbt.toString())) {
                                                        PackFycxFbtDown fbtDown = new PackFycxFbtDown();
                                                        fbtDown.fillData(fycx_fbt.toString());
                                                        switch (status) {
                                                            case SB: {
                                                                if (cbSb.isChecked()) {
                                                                    if (currentColumn == WIND) {
                                                                        addWindMarkersToMap(fbtDown);
                                                                    } else {
                                                                        addPolygonToMap(SB, fbtDown);
                                                                    }
                                                                }
                                                            }
                                                            break;
                                                            case ZD: {
                                                                if(cbZd.isChecked()) {
                                                                    addMarkersToMap(fbtDown);
                                                                }
                                                            }
                                                            break;
                                                            case DB: {
                                                                if(cbDb.isChecked()) {
                                                                    addMarkersToMap(fbtDown);
                                                                }
                                                            }
                                                            case GJ: {
                                                                if(cbGj.isChecked()) {
                                                                    addMarkersToMap(fbtDown);
                                                                }
                                                            }
                                                            break;
                                                        }
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
