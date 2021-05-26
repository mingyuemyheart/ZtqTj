package com.pcs.ztqtj.control.tool;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.GroundOverlay;
import com.amap.api.maps.model.GroundOverlayOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolygonOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.maps.model.Text;
import com.amap.api.maps.model.TextOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.FycxFbtBean;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackFycxFbtDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackFycxFbtUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.typhoon.ForecastPoint;
import com.pcs.lib_ztqfj_v2.model.pack.net.typhoon.FulPoint;
import com.pcs.lib_ztqfj_v2.model.pack.net.typhoon.PackMultiTyphoonPathDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.typhoon.PackMultiTyphoonPathUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.typhoon.PackTyphoonListCurrentActivityDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.typhoon.PackTyphoonListCurrentActivityUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.typhoon.PackTyphoonListDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.typhoon.PackTyphoonListUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.typhoon.PackTyphoonPathDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.typhoon.PackTyphoonPathUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.typhoon.PackTyphoonWarnLineDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.typhoon.PackTyphoonWarnLineDown.PackTyphoonWarnLineRow;
import com.pcs.lib_ztqfj_v2.model.pack.net.typhoon.PackTyphoonWarnLineUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.typhoon.TyphoonInfo;
import com.pcs.lib_ztqfj_v2.model.pack.net.typhoon.TyphoonPathInfo;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.image.ImageLoadFromUrl;
import com.pcs.ztqtj.control.tool.utils.TextUtil;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.util.OkHttpUtil;
import com.pcs.ztqtj.view.activity.product.typhoon.ActivityTyphoon;
import com.pcs.ztqtj.view.myview.typhoon.TyphoonForecastView;
import com.pcs.ztqtj.view.myview.typhoon.TyphoonTrueView;
import com.pcs.ztqtj.view.myview.typhoon.TyphoonView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 台风路径
 *
 * @author JiangZy
 */
@SuppressLint({"HandlerLeak", "SimpleDateFormat", "InflateParams"})
public class ControlTyphoon {

    private ActivityTyphoon mActivity;

    /**
     * 地图控件
     */
    private AMap mAMap;

    /**
     * 警戒线
     */
    private PackTyphoonWarnLineUp warnLine24Up, warnLine48Up;

    /**
     * 台风列表
     */
    private PackTyphoonListUp mPackTyphoonListUp = new PackTyphoonListUp();

    private PackTyphoonListCurrentActivityUp typhoonListUp = new PackTyphoonListCurrentActivityUp();

    /**
     * 台风路径
     */
    private PackTyphoonPathUp mPackTyphoonPathUp = new PackTyphoonPathUp();

    /**
     * 云图
     */
    private PackFycxFbtUp mFycxFbtUp = new PackFycxFbtUp();

    /**
     * 台风路径View
     */
    private Map<String, TyphoonView> typhoonViewMap = new HashMap<String, TyphoonView>();

    /**
     * 风眼图片列表
     */
    private ArrayList<BitmapDescriptor> mListCentreBitmap = new ArrayList<BitmapDescriptor>();

    /**
     * 雷达
     */
    private GroundOverlay mRadarGroundoverlay;

    /**
     * 云图
     */
    private GroundOverlay mCloudGroundoverlay;

    /**
     * 类型：24小时警戒线
     */
    public static final int TYPE_WARN_LINE_24 = 1;
    /**
     * 类型：48小时警戒线
     */
    public static final int TYPE_WARN_LINE_48 = 2;

    /**
     * 初始化台风路径
     */
    private final int WHAT_PATH_INIT = 101;

    private static final int LINE_WIDTH = 10;

    public ControlTyphoon(ActivityTyphoon activity, AMap aMap) {
        mActivity = activity;
        mAMap = aMap;
        initCentreList();
    }

    /**
     * 初始化风眼列表
     */
    private void initCentreList() {
        mListCentreBitmap.add(BitmapDescriptorFactory
                .fromResource(R.drawable.ic_typhoon_centre_1));
        mListCentreBitmap.add(BitmapDescriptorFactory
                .fromResource(R.drawable.ic_typhoon_centre_2));
        mListCentreBitmap.add(BitmapDescriptorFactory
                .fromResource(R.drawable.ic_typhoon_centre_3));
        mListCentreBitmap.add(BitmapDescriptorFactory
                .fromResource(R.drawable.ic_typhoon_centre_4));
        mListCentreBitmap.add(BitmapDescriptorFactory
                .fromResource(R.drawable.ic_typhoon_centre_5));
        mListCentreBitmap.add(BitmapDescriptorFactory
                .fromResource(R.drawable.ic_typhoon_centre_6));
        mListCentreBitmap.add(BitmapDescriptorFactory
                .fromResource(R.drawable.ic_typhoon_centre_7));
        mListCentreBitmap.add(BitmapDescriptorFactory
                .fromResource(R.drawable.ic_typhoon_centre_8));
        mListCentreBitmap.add(BitmapDescriptorFactory
                .fromResource(R.drawable.ic_typhoon_centre_9));
    }

    private Handler mHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what) {
                case WHAT_PATH_INIT:
                    // 初始化台风路径完成
                    initTyphoonPathDone(msg.obj.toString());
                    break;
            }
        }
    };

    /**
     * 注册广播
     */
    public void registerReceiver() {
        PcsDataBrocastReceiver.registerReceiver(mActivity, mReceiver);
    }

    /**
     * 注销广播
     */
    public void unregisterReceiver() {
        PcsDataBrocastReceiver.unregisterReceiver(mActivity, mReceiver);
    }

    public void init() {
        requestWarnLine();// 请求警戒线
        requestTyphoonList();// 请求台风列表
    }

    /**
     * 请求警戒线
     */
    private void requestWarnLine() {
        warnLine24Up = new PackTyphoonWarnLineUp();
        warnLine24Up.warn_line_type = 0;
        PcsDataDownload.addDownload(warnLine24Up);

        warnLine48Up = new PackTyphoonWarnLineUp();
        warnLine48Up.warn_line_type = 1;
        PcsDataDownload.addDownload(warnLine48Up);
    }

    /**
     * 请求台风列表
     */
    public void requestTyphoonList() {
        mActivity.showProgressDialog();
        int year = Calendar.getInstance().get(Calendar.YEAR);
        mPackTyphoonListUp.year = String.valueOf(year);
        PcsDataDownload.addDownload(mPackTyphoonListUp);
    }

    /**
     * 获取当前活动的台风
     */
    public void requestCurrentTyphoonList() {
        typhoonListUp = new PackTyphoonListCurrentActivityUp();
        PcsDataDownload.addDownload(typhoonListUp);
    }

    /**
     * 请求台风路径
     *
     * @param code
     */
    public void requestTyphoonPath(String code) {
        mActivity.showProgressDialog();

        // 提示出错
        if (TextUtils.isEmpty(code)) {
            mActivity.showError("获取台风路径失败");
            // 取消等待框
            mActivity.dismissProgressDialog();
            return;
        }

        mPackTyphoonPathUp.code = code;
        PcsDataDownload.addDownload(mPackTyphoonPathUp);
    }

    /**
     * 请求多个台风路径
     * @param code
     */
    public void requestMultiTyphoon(String code) {
        PackMultiTyphoonPathUp up = new PackMultiTyphoonPathUp();
        up.code = code;
        PcsDataDownload.addDownload(up);
    }

    /**
     * 请求云图
     */
    public void reqCloud() {
        mActivity.showProgressDialog();
        mFycxFbtUp.img_type = "4";
        mFycxFbtUp.type = "10";
        mFycxFbtUp.falg = "";
        mFycxFbtUp.area_id = "25169";
        PcsDataDownload.addDownload(mFycxFbtUp);
    }

    /**
     * 请求雷达
     */
    public void reqRadar() {
        mActivity.showProgressDialog();
        mFycxFbtUp.img_type = "3";
        mFycxFbtUp.type = "10";
        mFycxFbtUp.falg = "";
        mFycxFbtUp.area_id = "25169";
        PcsDataDownload.addDownload(mFycxFbtUp);
    }

    /**
     * 清除雷达
     */
    public void clearRadar() {
        if(mRadarGroundoverlay != null) {
            mRadarGroundoverlay.remove();
        }
        mActivity.clearDate("1");
    }

    /**
     * 清除云图
     */
    public void clearCloud() {
        if(mCloudGroundoverlay != null) {
            mCloudGroundoverlay.remove();
        }
        mActivity.clearDate("2");
    }

    /**
     * 接收警戒线
     *
     * @param name
     */
    private void receiveWarnLine(String name) {
        PackTyphoonWarnLineDown packDown = (PackTyphoonWarnLineDown) PcsDataManager.getInstance().getNetPack(name);
        if (packDown == null) {
            return;
        }

        // 颜色
        int color = Color.parseColor(packDown.color);

        // 警戒线标记
        addWarnMarker(mActivity, mAMap, new LatLng(packDown.txt_latitude, packDown.txt_longitude), packDown.name, color);
        // 警戒线
        if (packDown.list == null || packDown.list.size() < 2) {
            return;
        }
        for (int i = 1; i < packDown.list.size(); i++) {
            PackTyphoonWarnLineRow rowCurr = packDown.list.get(i);
            PackTyphoonWarnLineRow rowPrev = packDown.list.get(i - 1);
            LatLng latLngCurr = new LatLng(rowCurr.latitude, rowCurr.longitude);
            LatLng latLngPrev = new LatLng(rowPrev.latitude, rowPrev.longitude);
            addWarnLine(mAMap, latLngPrev, latLngCurr, color);
        }
    }

    /**
     * 接收台风列表
     */
    private void receiveTyphoonList() {
        mActivity.dismissProgressDialog();
        okHttpTyphoonList(mPackTyphoonListUp.getName());
    }

    /**
     * 获取台风列表
     */
    private void okHttpTyphoonList(final String name) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String url = CONST.BASE_URL+name;
                Log.e("tflist", url);
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
                                            if (!bobj.isNull("tflist")) {
                                                JSONObject tflist = bobj.getJSONObject("tflist");
                                                if (!TextUtil.isEmpty(tflist.toString())) {
                                                    // 填充数据
                                                    PackTyphoonListDown downPack = new PackTyphoonListDown();
                                                    downPack.fillData(tflist.toString());
                                                    if (downPack == null || downPack.typhoonList.size() == 0) {
                                                        mActivity.showError("获取台风列表失败");
                                                        return;
                                                    }

                                                    mActivity.fillTyphoonListData(downPack.typhoonList);

                                                    //判断当前是否有台风
                                                    if (TextUtils.isEmpty(downPack.is_ty) || downPack.is_ty.equals("0")) {
                                                        if (TextUtils.isEmpty(downPack.desc)) {
                                                            mActivity.showError("当前无台风");
                                                        } else {
                                                            mActivity.showError(downPack.desc);
                                                        }
                                                    } else {
                                                        //mActivity.selectTyphoon(0);
                                                        // 当前有台风则请求当前台风列表
                                                        requestCurrentTyphoonList();
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
            }
        }).start();
    }

    /**
     * 接收当前台风列表
     */
    private void receiverCurrentTyphoon() {
        PackTyphoonListCurrentActivityDown down = (PackTyphoonListCurrentActivityDown) PcsDataManager.getInstance().getNetPack(PackTyphoonListCurrentActivityUp.NAME);
        if(down == null) {
            return;
        }
        String code = "";
        for(TyphoonInfo info : down.typhoonList) {
            int index = down.typhoonList.indexOf(info);
            if(index != 0) {
                code += "," + info.code;
            } else {
                code += info.code;
            }
            //mActivity.selectTyphoon(info.code);
        }
        if(!TextUtils.isEmpty(code)) {
            requestMultiTyphoon(code);
        }

    }


    /**
     * 接收台风路径
     */
    private void receiveTyphoonPath(String nameStr) {
        okHttpTyphoonDetail(nameStr);
    }

    /**
     * 获取台风详情数据
     */
    private void okHttpTyphoonDetail(final String name) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String url = CONST.BASE_URL+name;
                Log.e("tfpathnew", url);
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
                                        if (!TextUtil.isEmpty(obj.toString())) {
                                            // 填充数据
                                            PackTyphoonPathDown mPackTyphoonPathDown = new PackTyphoonPathDown();
                                            mPackTyphoonPathDown.fillData(obj.toString());
                                            // 提示出错
                                            if (mPackTyphoonPathDown == null || mPackTyphoonPathDown.typhoonPathInfo.fulPointList.size() == 0) {
                                                mActivity.showError("获取台风路径失败");
                                                // 取消等待框
                                                mActivity.dismissProgressDialog();
                                                return;
                                            }

                                            // 解析台风路径
                                            //PcsInit.getInstance().getExecutorService().execute(new MyRunnable(mPackTyphoonPathDown.typhoonPathInfo));
                                            new MyRunnable(mPackTyphoonPathDown.typhoonPathInfo).run();
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

    private void receiverMultiTyphoon(String nameStr) {
        PackMultiTyphoonPathDown down = (PackMultiTyphoonPathDown) PcsDataManager.getInstance().getNetPack(nameStr);
        if(down == null) {
            mActivity.dismissProgressDialog();
            return;
        }
        List<TyphoonPathInfo> typhoonPathInfoList = down.typhoonList;
        Collections.reverse(typhoonPathInfoList);
        for(TyphoonPathInfo info : typhoonPathInfoList) {
            // 解析台风路径
            //PcsInit.getInstance().getExecutorService().execute(new MyRunnable(info));
            new MyRunnable(info).run();
        }

    }

    /**
     * 接收雷达
     */
    private void receiverRadar() {
        PackFycxFbtDown down = (PackFycxFbtDown) PcsDataManager.getInstance().getNetPack(mFycxFbtUp.getName());
        if (down == null) {
            return;
        }
        addPolygonToMap(down, "1");
        mActivity.updateDate("雷达" + down.pub_time);
    }

    /**
     * 接收云图
     */
    private void receiverClound() {
        PackFycxFbtDown down = (PackFycxFbtDown) PcsDataManager.getInstance().getNetPack(mFycxFbtUp.getName());
        if (down == null) {
            return;
        }
        addPolygonToMap(down, "2");
        mActivity.updateDate("云图" + down.pub_time);
    }

    /**
     * 添加分布图到地图
     *
     * @param down
     */
    private void addPolygonToMap(final PackFycxFbtDown down, String type) {
        if (TextUtils.isEmpty(down.img_url)) {
            mActivity.dismissProgressDialog();
            mActivity.showToast("无数据！");
            return;
        }

        String url = mActivity.getResources().getString(R.string.file_download_url) + down.img_url;
        ImageLoadFromUrl
                .getInstance()
                .setParams(url, imageListener)
                .setObject(down, type)
                .start();
    }

    private ImageLoadFromUrl.OnCompleteListener imageListener = new ImageLoadFromUrl.OnCompleteListener() {
        @Override
        public void onComplete(Bitmap bitmap, Object... object) {
            mActivity.dismissProgressDialog();
            if (bitmap != null && object.length == 2) {
                PackFycxFbtDown down = (PackFycxFbtDown) object[0];
                String type = (String) object[1];
                LatLng leftTop = null;
                LatLng rightBottom = null;
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
                BitmapDescriptor descriptor = BitmapDescriptorFactory.fromBitmap(bitmap);
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(northeast);
                builder.include(southwest);
                LatLng centerLatLng = new LatLng((northeast.latitude+southwest.latitude)/2.0f, (northeast.longitude+southwest.longitude)/2.0f);
                if(type.equals("1")) { // 雷达
                    if(mRadarGroundoverlay != null) {
                        mRadarGroundoverlay.remove();
                    }
                    mRadarGroundoverlay = mAMap.addGroundOverlay(new GroundOverlayOptions()
                                    .image(descriptor)
                                    .positionFromBounds(builder.build())
                            //.zIndex(1)
                    );
                } else if(type.equals("2")) { // 云图
                    if(mCloudGroundoverlay != null) {
                        mCloudGroundoverlay.remove();
                    }
                    mCloudGroundoverlay = mAMap.addGroundOverlay(new GroundOverlayOptions()
                                    .image(descriptor)
                                    .positionFromBounds(builder.build())
                            //.zIndex(1)
                    );
                }

                mAMap.animateCamera(CameraUpdateFactory.newLatLngZoom(centerLatLng, 6.5f));
            }
        }
    };

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

    /**
     * 初始化台风路径完成
     *
     * @param code
     */
    private void initTyphoonPathDone(String code) {
        mActivity.dismissProgressDialog();
        mActivity.showTyphoonPath(typhoonViewMap.get(code));
    }

    /**
     * 添加预警线
     *
     * @param aMap
     * @param latLng1
     * @param latLng2
     * @param color
     * @return
     */
    private Polyline addWarnLine(AMap aMap, LatLng latLng1, LatLng latLng2, int color) {
        if (aMap == null || latLng1 == null || latLng2 == null) {
            return null;
        }
        PolylineOptions options = (new PolylineOptions()).add(latLng1)
                .add(latLng2).width(4).geodesic(false).color(color)
                .setDottedLine(false);
        return aMap.addPolyline(options);
    }

    /**
     * 添加预警线标记
     *
     * @param context
     * @param aMap
     * @param latLng
     * @param content
     * @param color
     * @return
     */
    private Marker addWarnMarker(Context context, AMap aMap, LatLng latLng, String content, int color) {
        if (context == null || aMap == null || latLng == null) {
            return null;
        }

        // 警戒线文字
        View view = LayoutInflater.from(context).inflate(
                R.layout.layout_typhoon_warn_text, null);
        TextView tv = (TextView) view.findViewById(R.id.text_view);
        tv.setText(content);
        tv.setTextColor(color);
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromView(view);
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .icon(bitmap).setFlat(true)
                .anchor(0.5f, 0.5f);
        return aMap.addMarker(options);
    }

    public enum TyphoonForecastAddress {
        BEIJING("北京预报"),
        TOKYO("东京预报"),
        SHANGHAI("天津预报"),
        AMERICA("美国预报");

        private String value = "";
        private TyphoonForecastAddress(String value) {
            this.value = value;
        }
        public String value() {
            return value;
        }
    }

    /**
     * 解析台风路径数据获取台风View
     *
     * @author E.Sun
     *         2015年9月8日
     */
    private class MyRunnable {

        //private PackTyphoonPathDown down;
        private TyphoonPathInfo typhoonPathInfo;
        private TyphoonView typhoonView = new TyphoonView();

        public MyRunnable(TyphoonPathInfo info) {
            this.typhoonPathInfo = info;
        }

        public void run() {
            typhoonView.setCode(typhoonPathInfo.code);
            typhoonView.setName(typhoonPathInfo.name);
            typhoonView.setSimpleName(typhoonPathInfo.simplename);

            List<FulPoint> list = typhoonPathInfo.fulPointList;
            FulPoint point;
            LatLng latLng;
            String typhoonTime;
            String typhoonContent;
            MarkerOptions pathMarker;


            for (int i = 0; i < list.size(); i++) {
                point = list.get(i);
                latLng = getLatLng(point);
                typhoonTime = getTyphoonTime(point);
                typhoonContent = getTyphoonContent(point);

                if (i == list.size() - 1) {
                    // 最后一个点
                    pathMarker = addPathMarkerLast(latLng, mActivity, typhoonPathInfo.code, point, typhoonTime,
                            typhoonContent);
                } else {
                    pathMarker = addPathMarkerSmart(latLng, point, typhoonTime, typhoonContent);
                }

                // 一个路径点匹配一个台风时间、台风详情、风眼、风圈、实况路径线
                if (pathMarker != null) {
                    TyphoonTrueView pointView = new TyphoonTrueView();
                    pointView.setTime(typhoonTime);
                    pointView.setContent(typhoonContent);
                    if (!TextUtils.isEmpty(list.get(0).jd) && !TextUtils.isEmpty(list.get(0).wd)) {
                        //路径点
                        pointView.setTruePointOptions(pathMarker);
                        //风眼
                        pointView.setWindCenterOptions(addCentre(latLng, typhoonTime, typhoonContent));
//                        //7级风圈
//                        pointView.setWindSevenOptions(addPower7(latLng, point));
//                        //10级风圈
//                        pointView.setWindTenOptions(addPower10(latLng, point));

                        int ne7 = (int) point.ne_7;
                        int se7 = (int) point.se_7;
                        int sw7 = (int) point.sw_7;
                        int nw7 = (int) point.nw_7;

                        int ne10 = (int) point.ne_10;
                        int se10 = (int) point.se_10;
                        int sw10 = (int) point.sw_10;
                        int nw10 = (int) point.nw_10;

                        int ne12 = (int) point.ne_12;
                        int se12 = (int) point.se_12;
                        int sw12 = (int) point.sw_12;
                        int nw12 = (int) point.nw_12;

                        //7级风圈
                        if(ne7+se7+sw7+nw7 <= 0) {
                            pointView.setWindSevenOptions(addPower7(latLng, point));
                        } else {
                            pointView.setWindSevenGroudOverlayOptions(addWindPowerSevenNew(mAMap, latLng, point));
                        }
                        //10级风圈
                        if(ne10+se10+sw10+nw10 <= 0) {
                            pointView.setWindTenOptions(addPower10(latLng, point));
                        } else {
                            pointView.setWindTenGroudOverlayOptions(addWindPowerTenNew(mAMap, latLng, point));
                        }

                        if(ne12+se12+sw12+nw12 > 0) {
                            pointView.setWindTwelveGroudOverlayOptions(addWindPowerTwelveNew(mAMap, latLng, point));
                        }
                    }

                    if (i > 0) {
                        pointView.setTrueLineOptions(addTrueLine(latLng, getLatLng(list.get(i - 1))));
                    }
                    typhoonView.addTyphoonTrueView(pointView);
                }
            }

            if (!TextUtils.isEmpty(list.get(0).jd) && !TextUtils.isEmpty(list.get(0).wd)) {
                int shColor = Color.parseColor("#0602EB");
                // 初始化预测路径
                typhoonView.setBeijingForecast(addTyphoonForecastView(mAMap, typhoonPathInfo
                        .BeijingDottedPointList, Color.RED, TyphoonForecastAddress.BEIJING));
                typhoonView.setTokyoForecast(addTyphoonForecastView(mAMap, typhoonPathInfo.TokyoDottedPointList,
                        Color.BLACK, TyphoonForecastAddress.TOKYO));
                typhoonView.setShangHaiForecast(addTyphoonForecastView(mAMap, typhoonPathInfo
                        .ShangHaiDottedPointList, shColor, TyphoonForecastAddress.SHANGHAI));
                typhoonView.setTaiWanForecast(addTyphoonForecastView(mAMap, typhoonPathInfo
                        .TaiWanDottedPointList, mActivity.getResources().getColor(R.color.typhoon_tw), TyphoonForecastAddress.AMERICA));
            }
            typhoonViewMap.put(typhoonPathInfo.code, typhoonView);

            Message msg = new Message();
            msg.what = WHAT_PATH_INIT;
            msg.obj = typhoonPathInfo.code;
            mHandler.sendMessage(msg);
        }

        /**
         * 获取经纬度对象
         *
         * @param point
         * @return
         */
        private LatLng getLatLng(FulPoint point) {
            if (point == null) {
                return null;
            }
            if (TextUtils.isEmpty(point.jd) || TextUtils.isEmpty(point.wd)) {
                return new LatLng(26.079804d, 119.29972d);
            }

            double latitude = Double.parseDouble(point.wd);
            if (latitude < -90) {
                latitude = -90;
            } else if (latitude > 90) {
                latitude = 90;
            }
            double longitude = Double.parseDouble(point.jd);
            if (longitude <= -180) {
                longitude = -179.999999;
            } else if (longitude >= 180) {
                longitude = 179.999999;
            }
            return new LatLng(latitude, longitude);
        }

        /**
         * 添加实况路径点（最近点）
         *
         * @param latLng
         * @param context
         * @param code
         * @param point
         * @param title
         * @param content
         * @return
         */
        private MarkerOptions addPathMarkerLast(LatLng latLng, Context context, String code, FulPoint point, String
                title, String content) {
            if (latLng == null) {
                return null;
            }

            MarkerOptions options = new MarkerOptions().position(latLng)
                    .icon(getMarkerIconLast(context, code, point))
                    .anchor(0.5f, 0.5f).title(title)
                    .snippet(content).zIndex(2);
            return options;
        }

        /**
         * 添加实况路径点（小）
         *
         * @param latLng
         * @param point
         * @param title
         * @param content
         * @return
         */
        private MarkerOptions addPathMarkerSmart(LatLng latLng, FulPoint point, String title, String content) {
            if (latLng == null) {
                return null;
            }

            if (latLng.longitude >= 180) {
                latLng = new LatLng(latLng.latitude, 179.99);
            }

            MarkerOptions options = new MarkerOptions().position(latLng)
                    .icon(getMarkerIconSmart(point))
                    .anchor(0.5f, 0.5f).title(title)
                    .snippet(content).zIndex(1);
            return options;
        }

        /**
         * 获取台风时间
         *
         * @param point
         * @return
         */
        private String getTyphoonTime(FulPoint point) {
            String timeStr = "时间：";

            if (point == null) {
                return timeStr;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("MMddHH");
            SimpleDateFormat sdf1 = new SimpleDateFormat("MM月dd日 HH时");
            try {
                timeStr += sdf1.format(sdf.parse(point.time.substring(2)));
            } catch (Exception e) {
                e.printStackTrace();
            }

            return timeStr;
        }

        /**
         * 获取台风时间
         *
         * @param point
         * @return
         */
        private String getTyphoonTime(ForecastPoint point) {
            String timeStr = "时间：";

            if (point == null) {
                return timeStr;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("MMddHH");
            SimpleDateFormat sdf1 = new SimpleDateFormat("MM月dd日 HH时");
            try {
                timeStr += sdf1.format(sdf.parse(point.time.substring(2)));
            } catch (Exception e) {
                e.printStackTrace();
            }

            return timeStr;
        }

        /**
         * 获取台风详情
         *
         * @param point
         * @return
         */
        private String getTyphoonContent(FulPoint point) {
            if (point == null) {
                return "";
            }

            String fsMaxUnit = "m/s";
            String qyUnit = "hPa";
            String flUnit = "级";
            String fl_7Unit = "km";
            String fl_10Unit = "km";

            StringBuffer sb = new StringBuffer();
            sb.append("经度：").append(point.jd).append(" ").append("纬度：")
                    .append(point.wd);// 经纬度
            sb.append("\n").append("移动时速：").append(point.fs);
            sb.append("\n").append("最大风速：").append(point.fs_max)
                    .append(fsMaxUnit);
            sb.append("\n").append("中心气压：").append(point.qy).append(qyUnit);
            sb.append("\n").append("中心风力：").append(point.fl).append(flUnit);
            sb.append("\n").append("七级风圈半径：").append(point.ne_7)
                    .append(fl_7Unit);
            sb.append("\n").append("十级风圈半径：").append(point.ne_10)
                    .append(fl_10Unit);
            sb.append("\n").append("十二级风圈半径：").append((int)point.ne_12)
                    .append(fl_10Unit);

            return sb.toString();
        }

        /**
         * 获取台风详情
         *
         * @param point
         * @return
         */
        private String getTyphoonContent(ForecastPoint point) {
            if (point == null) {
                return "";
            }

            String result = "";
            // 时间
            if (!TextUtils.isEmpty(point.time)) {
                result += point.time;
            }

            // 风速
            if (!TextUtils.isEmpty(point.fs)) {
                result += ",风速" + point.fs;
            }
            // 风力
            if (!TextUtils.isEmpty(point.fl)) {
                result += "\n风力" + point.fl + "级";
            }
            // 气旋
            if (!TextUtils.isEmpty(point.qx)) {
                result += "(" + point.qx + ")";
            }

            return result;
        }

        /**
         * 添加风眼
         *
         * @param latLng
         * @param title
         * @param content
         * @return
         */
        private MarkerOptions addCentre(LatLng latLng, String title, String content) {
            if (latLng == null) {
                return null;
            }

            MarkerOptions options = new MarkerOptions().position(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_typhoon_centre))
                    .anchor(0.5f, 0.5f).period(5)
                    .title(title)
                    .snippet(content)
                    .zIndex(1);
            return options;
        }

        /**
         * 添加7级风圈
         *
         * @param latLng
         * @param point
         * @return
         */
        private CircleOptions addPower7(LatLng latLng, FulPoint point) {
            if (latLng == null || point == null || TextUtils.isEmpty(point.fl_7)) {
                return null;
            }

            CircleOptions options = new CircleOptions().center(latLng)
                    .radius(Float.parseFloat(point.fl_7) * 1000)
                    .strokeColor(Color.TRANSPARENT)
                    .fillColor(Color.parseColor("#A0ADD66C")).strokeWidth(0);

            return options;
        }

        /**
         * 添加10级风圈
         *
         * @param latLng
         * @param point
         * @return
         */
        private CircleOptions addPower10(LatLng latLng, FulPoint point) {
            if (latLng == null || point == null || TextUtils.isEmpty(point.fl_10)) {
                return null;
            }

            CircleOptions options = new CircleOptions().center(latLng)
                    .radius(Float.parseFloat(point.fl_10) * 1000)
                    .strokeColor(Color.TRANSPARENT)
                    .fillColor(Color.parseColor("#A037D14D")).strokeWidth(0);
            return options;
        }

        private static final int PAINT_WIDTH = 5;
        private RectF getArcFormDistance(int width, int height, int distance, int max, float ratio) {
            width = (int) ((width + PAINT_WIDTH*2)*ratio);
            height = (int) ((height + PAINT_WIDTH*2)*ratio);
            distance *= ratio;
            max *= ratio;
            float finalWidth = (width-PAINT_WIDTH*2*ratio)*distance/max;
            float finalHeight = (height-PAINT_WIDTH*2*ratio)*distance/max;

            float left = (width-finalWidth)/2f;
            float top = (height-finalHeight)/2f;
            float right = left + finalWidth;
            float bottom = top + finalHeight;
            return new RectF(left, top, right, bottom);
        }

        private GroundOverlayOptions addPowerNew(AMap aMap, LatLng latLng, int ne, int se, int sw, int nw, int fillColor, int strokeColor) {
            float ratio = 1;
            int max = ne;
            max = Math.max(max, se);
            max = Math.max(max, sw);
            max = Math.max(max, nw);

            LatLng leftLatlng = getLatlng(max, latLng, 270);
            LatLng topLatlng = getLatlng(max, latLng, 0);
            LatLng rightLatlng = getLatlng(max, latLng, 90);
            LatLng bottomLatlng = getLatlng(max, latLng, 180);

            int width = 512;
            int height = 512;
            Rect rect = new Rect(0, 0, (int)((width+PAINT_WIDTH*2)*ratio), (int)((height+PAINT_WIDTH*2)*ratio));

            Paint strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            strokePaint.setStrokeWidth(PAINT_WIDTH);
            strokePaint.setStyle(Paint.Style.STROKE);
            strokePaint.setColor(strokeColor);
            DashPathEffect effects = new DashPathEffect(new float[] { 4, 4, 4, 4 }, 0);
            //strokePaint.setPathEffect(effects);

            Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            fillPaint.setStrokeWidth(0);
            fillPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            fillPaint.setColor(fillColor);
            fillPaint.setAlpha((int) (0.5*255));

            Bitmap bitmap = Bitmap.createBitmap(rect.width(), rect.height(), Bitmap.Config.ARGB_4444);
            Canvas canvas = new Canvas(bitmap);

            RectF oval1 = getArcFormDistance(width, height, ne, max, ratio);
            RectF oval2 = getArcFormDistance(width, height, se, max, ratio);
            RectF oval3 = getArcFormDistance(width, height, sw, max, ratio);
            RectF oval4 = getArcFormDistance(width, height, nw, max, ratio);

            Path path = new Path();
            int x = rect.width()/2;
            int y = (int) (rect.height()/2 - oval1.height()/2);
            path.moveTo(x, y);
            path.arcTo(oval1, 270, 90);
            path.arcTo(oval2, 0, 90);
            path.arcTo(oval3, 90, 90);
            path.arcTo(oval4, 180, 90);
            path.close();
            canvas.drawPath(path, fillPaint);
            canvas.drawPath(path, strokePaint);

            GroundOverlayOptions options =  new GroundOverlayOptions()
                    .anchor(0.5f, 0.5f)
                    //.transparency(0.7f)
                    .image(BitmapDescriptorFactory.fromBitmap(bitmap))
                    .position(new LatLng(leftLatlng.latitude, topLatlng.longitude), max*2*1000, max*2*1000).zIndex(2);
            bitmap.recycle();
            return options;
        }

        private GroundOverlayOptions addWindPowerSevenNew(AMap aMap, LatLng latLng, FulPoint point) {
            int ne7 = (int) point.ne_7;
            int se7 = (int) point.se_7;
            int sw7 = (int) point.sw_7;
            int nw7 = (int) point.nw_7;
            return addPowerNew(aMap, latLng, ne7, se7, sw7, nw7, Color.parseColor("#33ADD66C"), Color.parseColor("#56A616"));
        }

        private GroundOverlayOptions addWindPowerTenNew(AMap aMap, LatLng latLng, FulPoint point) {
            int ne10 = (int) point.ne_10;
            int se10 = (int) point.se_10;
            int sw10 = (int) point.sw_10;
            int nw10 = (int) point.nw_10;
            return addPowerNew(aMap, latLng, ne10, se10, sw10, nw10, Color.parseColor("#3337D14D"), Color.parseColor("#56A616"));
        }

        private GroundOverlayOptions addWindPowerTwelveNew(AMap aMap, LatLng latLng, FulPoint point) {
            int ne12 = (int) point.ne_12;
            int se12 = (int) point.se_12;
            int sw12 = (int) point.sw_12;
            int nw12 = (int) point.nw_12;
            return addPowerNew(aMap, latLng, ne12, se12, sw12, nw12, Color.parseColor("#3337D14D"), Color.parseColor("#56A616"));
        }

        /**
         * 添加实况路径线
         *
         * @param latLng1
         * @param latLng2
         * @return
         */
        private PolylineOptions addTrueLine(LatLng latLng1, LatLng latLng2) {
            if (latLng1 == null || latLng2 == null) {
                return null;
            }

            return new PolylineOptions()
                    .add(latLng1).add(latLng2)
                    .width(LINE_WIDTH).color(Color.RED);
        }

        /**
         * 添加预测路径
         *
         * @param list
         * @param color
         * @return
         */
        private TyphoonForecastView addTyphoonForecastView(AMap aMap, List<ForecastPoint> list, int
                color, TyphoonForecastAddress type) {
            if (list == null || list.size() == 0) {
                return null;
            }

//            // 实线路径的最后一个点
//            FulPoint fullLastPoint = down.typhoonPathInfo.fulPointList.get(down.typhoonPathInfo.fulPointList.size() - 1);
//            // 预测路径的第一个点
//            ForecastPoint forecastFirstPoint = list.get(0);
//            // 当实际路线的最后一个点与预测路径的第一个点不相等时将第一点预测路径经纬度改为实况路径最后一点
//            if (!fullLastPoint.jd.equals(forecastFirstPoint.jd) || !fullLastPoint.wd.equals(forecastFirstPoint.wd)) {
//                list.remove(0);
//                forecastFirstPoint.jd = fullLastPoint.jd;
//                forecastFirstPoint.wd = fullLastPoint.wd;
//                list.add(0, forecastFirstPoint);
//            }

            TyphoonForecastView forecast = new TyphoonForecastView();
            double wd, jd;
            LatLng latLng;
            List<LatLng> latLngList = new ArrayList<LatLng>();
            for (ForecastPoint item : list) {
                forecast.addTimeOptions(addForecastMarker(item, color, type));
                forecast.addTipsOptions(addForecastTipsMarker(item, type));
                wd = Double.parseDouble(item.wd);
                jd = Double.parseDouble(item.jd);
                latLng = new LatLng(wd, jd);
                latLngList.add(latLng);

//                // TODO: 2017/8/7 测试数据
//                if(list.indexOf(item) == 0) {
//                    latLngList.add(latLng);
//                }

            }
            int z = 1;
            if(type.equals(TyphoonForecastAddress.SHANGHAI)) {
                z = 2;
            }
            forecast.setLineOptions(addForecastLine(latLngList, color, z));


//            latLngList.add(new LatLng(45.4, 155.4));
//            latLngList.add(new LatLng(53.4, 161.0));
//            //latLngList.add(new LatLng(52.9, 173.7));
//            latLngList.add(new LatLng(56.3, 147.1));
//            forecast.setLineOptions(addForecastLine(latLngList, color));
//            if ((down.typhoonPathInfo.ShangHaiDottedPointList.size() > 0 && type == TyphoonForecastAddress.FUZHOU) ||
//                    (down.typhoonPathInfo.ShangHaiDottedPointList.size() == 0 &&
//                            down.typhoonPathInfo.BeijingDottedPointList.size() > 0 &&
//                            type == TyphoonForecastAddress.BEIJING)) {
//                forecast.setProbabilisticChart(addProbabilisticChart(latLngList), addProbabilisticLastPoint2(aMap, latLngList));
//            }


            return forecast;
        }

        /**
         * 添加预测路径线
         *
         * @param list
         * @param color
         * @return
         */
        private PolylineOptions addForecastLine(List<LatLng> list, int color, int z) {
            if (list == null || list.size() <= 1) {
                return null;
            }

            PolylineOptions options = new PolylineOptions().width(LINE_WIDTH)
                    .color(color)
                    .zIndex(z)
                    .setDottedLineType(PolylineOptions.DOTTEDLINE_TYPE_SQUARE)
                    .setDottedLine(true);

            for (LatLng latLng : list) {
                if (latLng != null) {
                    options.add(latLng);
                }
            }

            return options;
        }

        private PolygonOptions addProbabilisticChart(List<LatLng> list) {
            // TODO: 2017/8/3 添加概率图其他dian
            return null;
        }

        private CircleOptions addProbabilisticLastPoint(List<LatLng> list) {
            LatLng latLng;
            double radius;
            int size = list.size();
            if(size == 2) {
                latLng = list.get(size-1);
                radius = 76.d * 1000;
            } else if(size == 3) {
                latLng = list.get(size-1);
                radius = 120.d * 1000;
            } else if(size >= 4) {
                latLng = list.get(3);
                radius = 250.d * 1000;
            } else {
                return null;
            }
            return new CircleOptions().center(latLng).radius(radius).fillColor(Color.argb(125, 1, 1, 1)).strokeWidth(0f);
        }

        private List<CircleOptions> addProbabilisticLastPoint2(AMap aMap, List<LatLng> list) {
            LatLng latLng;
            double radius;
            List<LatLng> centerLatlng = new ArrayList<>();
            List<Point> pointList = new ArrayList<>();
            List<CircleOptions> circleList = new ArrayList<>();
            float[] distanceList = {76.f, 120.f, 250.f};
            int size = list.size();
            if(size >= 2) {
                latLng = list.get(1);
                radius = 76.d * 1000;
                //circleList.add(new CircleOptions().center(latLng).radius(radius).fillColor(Color.argb(125, 1, 1, 1)).strokeWidth(0f));
                Point screenPosition = aMap.getProjection().toScreenLocation(latLng);
                pointList.add(screenPosition);
                centerLatlng.add(latLng);
                Log.e("TAG", "x:" + screenPosition.x + " y:" + screenPosition.y);
            }
            if(size >= 3) {
                latLng = list.get(2);
                radius = 120.d * 1000;
                //circleList.add(new CircleOptions().center(latLng).radius(radius).fillColor(Color.argb(125, 1, 1, 1)).strokeWidth(0f));
                Point screenPosition = aMap.getProjection().toScreenLocation(latLng);
                pointList.add(screenPosition);
                centerLatlng.add(latLng);
                Log.e("TAG", "x:" + screenPosition.x + " y:" + screenPosition.y);
            }
            if(size >= 4) {
                latLng = list.get(3);
                radius = 250.d * 1000;
                //circleList.add(new CircleOptions().center(latLng).radius(radius).fillColor(Color.argb(125, 1, 1, 1)).strokeWidth(0f));
                Point screenPosition = aMap.getProjection().toScreenLocation(latLng);
                pointList.add(screenPosition);
                centerLatlng.add(latLng);
                Log.e("TAG", "x:" + screenPosition.x + " y:" + screenPosition.y);
            }
            if(pointList.size() >= 2) {
                List<Point> boundList = new ArrayList<>();
                for(int j = 0; j < pointList.size()-1; j++) {
                    List<Float> radiusList = new ArrayList<>();
                    for (int i = 0; i < 2; i++) {
                        int index = j+i;
                        Point centerPoint = mAMap.getProjection().toScreenLocation(centerLatlng.get(index));
                        float distance = distanceList[index];
                        LatLng endLatLng = getLatlng(distance, centerLatlng.get(index), 0);
                        Point point = mAMap.getProjection().toScreenLocation(endLatLng);
                        radiusList.add((float) (float) Math.sqrt(Math.pow(point.y - centerPoint.y, 2) + Math.pow(point.x - centerPoint.x, 2)));
                    }

                    Point[] tangentPoint = getTangentPointList(pointList.get(j), pointList.get(j+1), radiusList.get(0),
                            radiusList.get(1));
                    boundList.addAll(new ArrayList<>(Arrays.asList(tangentPoint)));

//                    for (int i = 0; i < tangentPoint.length; i++) {
//                        Point point = tangentPoint[i];
//                        LatLng latLng1 = aMap.getProjection().fromScreenLocation(point);
//                        MarkerOptions options = new MarkerOptions().position(latLng1)
//                                .icon(BitmapDescriptorFactory
//                                        .fromResource(R.drawable.icon_typhoon_s_6_7))
//                                .anchor(0.5f, 0.5f).zIndex(1);
//                        aMap.addMarker(options);
//                    }
                }
//                LatLng[] boundLatlngList = new LatLng[boundList.size()];
//                for(int i = 0; i < boundList.size(); i++) {
//                    LatLng latLng1 = aMap.getProjection().fromScreenLocation(boundList.get(i));
//                    int half = boundList.size()/2;
//                    if(i < half) {
//                        boundLatlngList[i*2] = latLng1;
//                    } else {
//                        boundLatlngList[(i-half)+1] = latLng1;
//                    }
//                }

                List<LatLng> boundLatlngList = new ArrayList<>();
//                for(int i = 0; i < boundList.size(); i+=2) {
//                    LatLng latLng1 = aMap.getProjection().fromScreenLocation(boundList.get(i));
//                    boundLatlngList.add(latLng1);
//                }
//                for(int i = 1; i < boundList.size(); i+=2) {
//                    LatLng latLng1 = aMap.getProjection().fromScreenLocation(boundList.get(i));
//                    boundLatlngList.add(latLng1);
//                }
                for(int i = 0; i < boundList.size(); i++) {
                    LatLng latLng1 = aMap.getProjection().fromScreenLocation(boundList.get(i));
                    boundLatlngList.add(latLng1);
                }
//                if(boundList.size() == 8) {
//                    boundLatlngList.add(pointToLatlng(aMap, boundList.get(0)));
//                    boundLatlngList.add(pointToLatlng(aMap, boundList.get(3)));
//                    boundLatlngList.add(pointToLatlng(aMap, boundList.get(7)));
//                    boundLatlngList.add(pointToLatlng(aMap, boundList.get(2)));
//                    boundLatlngList.add(pointToLatlng(aMap, boundList.get(6)));
//                    boundLatlngList.add(pointToLatlng(aMap, boundList.get(5)));
//                    boundLatlngList.add(pointToLatlng(aMap, boundList.get(4)));
//                    boundLatlngList.add(pointToLatlng(aMap, boundList.get(1)));
//                } else {
//                    for(Point p : boundList) {
//                        boundLatlngList.add(pointToLatlng(aMap, p));
//                    }
//                }




//                for(int i = 0; i < boundList.size()/4; i++) {
//                    List<LatLng> lista = new ArrayList<>();
//                    for(int j = 0; j < 4; j++) {
//                        if(i == 0) {
//                            lista.add(pointToLatlng(aMap, boundList.get(i * 4 + j)));
//                        }
//                    }
//                    if(i != 0 && i != boundList.size()/4-1) {
//                        int index = (i-1)*4;
//                        lista.add(pointToLatlng(aMap, boundList.get(index+1)));
//                        lista.add(pointToLatlng(aMap, boundList.get(i*4+1)));
//                        lista.add(pointToLatlng(aMap, boundList.get(i*4+2)));
//                        lista.add(pointToLatlng(aMap, boundList.get(index+2)));
//                    }
//                    if(i == boundList.size()/4-1) {
//                        int index = (i-1)*4;
//                        lista.add(pointToLatlng(aMap, boundList.get(index+1)));
//                        lista.add(pointToLatlng(aMap, boundList.get(i*4+1)));
//                        lista.addAll(drawCircle(aMap,
//                                centerLatlng.get(centerLatlng.size()-1),
//                                pointToLatlng(aMap, boundList.get(i*4+1)),
//                                pointToLatlng(aMap, boundList.get(i*4+2)),
//                                250f));
//                        lista.add(pointToLatlng(aMap, boundList.get(i*4+2)));
//                        lista.add(pointToLatlng(aMap, boundList.get(index+2)));
//                    }
//                    addPolygon(aMap, lista);
//                }

                List<LatLng> lista = new ArrayList<>();
//                for(int i = 0; i < boundLatlngList.size(); i += 2) {
//                    int index = i/2;
//                    if(index != 0 && index%2 != 0) {
//                        continue;
//                    }
//                    lista.add(boundLatlngList.get(i));
//                }
//                for(int i = boundLatlngList.size()-1; i >= 0; i -= 2) {
//                    int index = i/2;
//                    if(index != 1 && index%2 == 0) {
//                        continue;
//                    }
//                    lista.add(boundLatlngList.get(i));
//                }

                if(boundLatlngList.size() == 8) {
                    lista.add(boundLatlngList.get(0));
                    lista.add(boundLatlngList.get(2));
                    lista.add(boundLatlngList.get(6));
                    lista.add(boundLatlngList.get(7));
                    lista.add(boundLatlngList.get(3));
                    lista.add(boundLatlngList.get(1));
                } else if(boundLatlngList.size() == 6) {
                    lista.add(boundLatlngList.get(0));
                    lista.add(boundLatlngList.get(2));
                    lista.add(boundLatlngList.get(3));
                    lista.add(boundLatlngList.get(1));
                }
                lista.add(boundLatlngList.get(0));

                addPolygon(aMap, lista);


                for(int i = 0; i < boundLatlngList.size(); i++) {
                    aMap.addText(new TextOptions().position(boundLatlngList.get(i))
                            .text(String.valueOf(i)).fontColor(Color.RED)
                            .fontSize(30)
                            .align(Text.ALIGN_RIGHT, Text.ALIGN_CENTER_VERTICAL));
                }
            }
            return circleList;
        }

        private void addPolygon(AMap aMap, List<LatLng> list) {
            // 声明 多边形参数对象
            PolygonOptions polygonOptions = new PolygonOptions();
            // 添加 多边形的每个顶点（顺序添加）
            polygonOptions.addAll(list);
            polygonOptions.strokeWidth(0) // 多边形的边框
                    .fillColor(Color.argb(125, 1, 1, 1));   // 多边形的填充色
            aMap.addPolygon(polygonOptions);
        }

        /**
         * 屏幕位置返回一个地图位置
         * @param point
         * @return
         */
        private LatLng pointToLatlng(AMap aMap, Point point) {
            return aMap.getProjection().fromScreenLocation(point);
        }

        private Point[] getTangentPointList(Point start, Point end, float radiusStart, float radiusEnd) {
            float startX = start.x;
            float startY = start.y;
            float endX = end.x;
            float endY = end.y;
            // 根据角度算出四边形的四个点
            float offsetXStart = (float) (radiusStart*Math.sin(Math.atan((endY - startY) / (endX - startX))));
            float offsetYStart = (float) (radiusStart*Math.cos(Math.atan((endY - startY) / (endX - startX))));

            float offsetXEnd = (float) (radiusEnd*Math.sin(Math.atan((endY - startY) / (endX - startX))));
            float offsetYEnd = (float) (radiusEnd*Math.cos(Math.atan((endY - startY) / (endX - startX))));

            float x1, x2, x3, x4, y1, y2, y3, y4;
            if(offsetXStart >= 0) {
                x1 = startX - offsetXStart;
                x2 = startX + offsetXStart;
            } else {
                x1 = startX + offsetXStart;
                x2 = startX - offsetXStart;
            }

            if(offsetXEnd >= 0) {
                x3 = endX - offsetXEnd;
                x4 = endX + offsetXEnd;
            } else {
                x3 = endX + offsetXEnd;
                x4 = endX - offsetXEnd;
            }
            y1 = startY + offsetYStart;
            y2 = startY - offsetYStart;
            y3 = endY + offsetYEnd;
            y4 = endY - offsetYEnd;

            Point[] result = {new Point((int)x1, (int)y1), new Point((int)x2, (int)y2), new Point((int)x3, (int)y3), new Point((int)x4, (int)y4)};
            return result;
        }

        /**
         * 根据一个点的经纬度和距离得到另外一个点的经纬度
         * @param distance
         * @param latlngA
         * @param angle：角度
         * @return
         */
        public LatLng getLatlng(float distance, LatLng latlngA, double angle){
            return new LatLng(latlngA.latitude+(distance*Math.cos(angle*Math.PI/180))/111,
                    latlngA.longitude+(distance*Math.sin(angle*Math.PI/180))/(111*Math.cos(latlngA.latitude*Math.PI/180))
            );
        }

        /**
         *
         * @param aMap
         * @param center 中心点
         * @param tanPoint 切点
         * @param radius 台风概率半径
         * @return
         */
        private List<LatLng> drawCircle(AMap aMap, LatLng center, LatLng tanPoint, LatLng tanPoint2, float radius) {
            Point p1 = aMap.getProjection().toScreenLocation(center);
            Point t1 = aMap.getProjection().toScreenLocation(tanPoint);
            Point t2 = aMap.getProjection().toScreenLocation(tanPoint2);

            double distance = (float) Math.sqrt(Math.pow(t1.y - t2.y, 2) + Math.pow(t1.x - t2.x, 2));
            double r = (float) Math.sqrt(Math.pow(t1.y - p1.y, 2) + Math.pow(t1.x - p1.x, 2));
            double x = (distance/2/r)>1 ? 1 : distance/2/r;
            double angle = 2*Math.asin(x)*180/Math.PI;
            double value1 = t1.y-p1.y;
            double value2 = t1.x-p1.x;
            double atan = value1/value2;
            double startAngle = Math.atan(atan)*180/Math.PI-90;
            List<LatLng> result = new ArrayList<>();
            if(Math.abs(startAngle) > 90) {
                for (double i = angle; i > 0; i -= 0.1) {
                    result.add(getLatlng(radius, center, startAngle + i));
                }
            } else {
                for (double i = 0; i < angle; i += 0.1) {
                    result.add(getLatlng(radius, center, startAngle + i));
                }
            }

//            List<LatLng> result = new ArrayList<>();
//            if(angle2>angle) {
//                for (float i = 0; i < angle2+angle; i += 0.1) {
//                    result.add(getLatlng(radius, center, angle + i));
//                }
//            } else {
//                for (double i = angle2+angle; i < 0 ; i += 0.1) {
//                    result.add(getLatlng(radius, center, angle + i));
//                }
//            }
//            for (float i = 180; i > 0; i -= 0.1) {
//                result.add(getLatlng(radius, center, angle + i));
//            }
            return result;
        }

        /**
         *
         * @param aMap
         * @param center 中心点
         * @param tanPoint 切点
         * @param radius 台风概率半径
         * @return
         */
        private List<LatLng> drawCircle(AMap aMap, LatLng center, LatLng tanPoint, float radius) {
            Point p1 = aMap.getProjection().toScreenLocation(center);
            Point t1 = aMap.getProjection().toScreenLocation(tanPoint);
            double value1 = t1.y-p1.y;
            double value2 = t1.x-p1.x;
            double atan = value1/value2;
            double angle = Math.atan(atan)*180/Math.PI-90;
            List<LatLng> result = new ArrayList<>();
            for (float i = 0; i < 180; i += 0.1) {
                result.add(getLatlng(radius, center, angle + i));
            }
            return result;
        }

        /**
         * 添加预测路径标记
         *
         * @param item
         * @param color
         * @return
         */
        private TextOptions addForecastMarker(ForecastPoint item, int color, TyphoonForecastAddress type) {
            if (item == null) {
                return null;
            }

            String strTV = item.time;
//            TextView view = new TextView(mActivity);
//            if (!TextUtils.isEmpty(item.fl)) {
//                strTV += item.qx + "\n\r" +" 风力" + item.fl + "级  风速" + item.fs ;
//            }
//            view.setText(strTV);
//            view.setTextColor(color);
////            android:lineSpacingExtra=""
//            view.setLineSpacing(3f, 1.2f);
//            view.setTextSize(10);
//            view.setPadding(3, 3, 3, 3);
////            view.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
//            BitmapDescriptor db = BitmapDescriptorFactory.fromView(view);

            double wd = Double.parseDouble(item.wd);
            double jd = Double.parseDouble(item.jd);
            LatLng point = new LatLng(wd, jd);

            boolean isVisible = false;
            if (!TextUtils.isEmpty(item.time)) {
                isVisible = true;
            }

            int z = 1;
            if(type.equals(TyphoonForecastAddress.SHANGHAI)) {
                z = 2;
            }

            TextOptions marker = new TextOptions().position(point)
                    .text(strTV).fontColor(color)
                    .backgroundColor(mActivity.getResources().getColor(android.R.color.transparent))
                    .fontSize(35)
                    .typeface(Typeface.DEFAULT_BOLD)
                    .align(Text.ALIGN_RIGHT, Text.ALIGN_CENTER_VERTICAL)
                    .zIndex(z).visible(isVisible);

            return marker;
        }

        /**
         * 添加预测路径标记
         *
         * @param item
         * @return
         */
        private MarkerOptions addForecastTipsMarker(ForecastPoint item, TyphoonForecastAddress type) {
            if (item == null) {
                return null;
            }

            double wd = Double.parseDouble(item.wd);
            double jd = Double.parseDouble(item.jd);
            LatLng latLng = new LatLng(wd, jd);
            BitmapDescriptor point = getMarkerIconSmart(item);

            //String typhoonTime = "";
            String typhoonContent = "";
            boolean isVisible = false;
            if (!TextUtils.isEmpty(item.time)
                    && !TextUtils.isEmpty(item.fl)
                    && !TextUtils.isEmpty(item.fs)) {
                //typhoonTime = getTyphoonTime(item);
                isVisible = true;
                typhoonContent = getTyphoonContent(item);
            }
            int z = 1;
            if(type.equals(TyphoonForecastAddress.SHANGHAI)) {
                z = 2;
            }
            MarkerOptions marker = new MarkerOptions().position(latLng)
                    .icon(point)
                    .title(type.value())
                    .anchor(0.5f, 0.5f)
                    .snippet(typhoonContent)
                    .zIndex(z).visible(isVisible);

            return marker;
        }

        /**
         * 获取路径点图标（最近点）
         *
         * @param context
         * @param code
         * @param point
         * @return
         */
        private BitmapDescriptor getMarkerIconLast(Context context, String code, FulPoint point) {
            if (context == null || point == null) {
                return null;
            }

            View view = LayoutInflater.from(context).inflate(R.layout.view_typhoon_point, null);
            TextView tv = (TextView) view.findViewById(R.id.tv);
            if (code.length() >= 2) {
                code = code.substring(code.length() - 2);
            }
            tv.setText(code);
            ImageView image = (ImageView) view.findViewById(R.id.image);
            image.setImageResource(R.drawable.icon_typhoon_last);
//			int fl = Integer.valueOf(point.fl);
//			switch (fl) {
//			case 6:
//			case 7:
//				image.setImageResource(R.drawable.icon_typhoon_s_6_7);// 热带低压
//				break;
//			case 8:
//			case 9:
//				image.setImageResource(R.drawable.icon_typhoon_s_8_9);// 热带风暴
//				break;
//			case 10:
//			case 11:
//				image.setImageResource(R.drawable.icon_typhoon_s_10_11);// 强热带风暴
//				break;
//			case 12:
//			case 13:
//				image.setImageResource(R.drawable.icon_typhoon_s_12_13);// 台风
//				break;
//			case 14:
//			case 15:
//				image.setImageResource(R.drawable.icon_typhoon_s_14_15);// 强台风
//				break;
//			default:
//				image.setImageResource(R.drawable.icon_typhoon_s_16_);// 超强台风
//				break;
//			}

            return BitmapDescriptorFactory.fromView(view);
        }

        /**
         * 获取路径点图标（小）
         *
         * @param point
         * @return
         */
        private BitmapDescriptor getMarkerIconSmart(FulPoint point) {
            if (point == null) {
                return null;
            }

            String centerPower = point.fl;
            if (TextUtils.isEmpty(centerPower)) {
                return null;
            }

            int fl = 0;
            if (centerPower.contains("大于")) {
                int index = centerPower.indexOf("大于");
                if (index != -1) {
                    String result = centerPower.substring(index + 2);
                    fl = Integer.valueOf(result) + 1;
                }
            } else {
                fl = Integer.valueOf(centerPower);
            }

            switch (fl) {
                case 6:
                case 7:
                    return BitmapDescriptorFactory
                            .fromResource(R.drawable.icon_typhoon_s_6_7);// 热带低压
                case 8:
                case 9:
                    return BitmapDescriptorFactory
                            .fromResource(R.drawable.icon_typhoon_s_8_9);// 热带风暴
                case 10:
                case 11:
                    return BitmapDescriptorFactory
                            .fromResource(R.drawable.icon_typhoon_s_10_11);// 强热带风暴
                case 12:
                case 13:
                    return BitmapDescriptorFactory
                            .fromResource(R.drawable.icon_typhoon_s_12_13);// 台风
                case 14:
                case 15:
                    return BitmapDescriptorFactory
                            .fromResource(R.drawable.icon_typhoon_s_14_15);// 强台风
                default:
                    return BitmapDescriptorFactory
                            .fromResource(R.drawable.icon_typhoon_s_16_);// 超强台风
            }
        }

        /**
         * 获取路径点图标（小）(预测点)
         *
         * @param point
         * @return
         */
        private BitmapDescriptor getMarkerIconSmart(ForecastPoint point) {
            if (point == null) {
                return null;
            }

            String centerPower = point.fl;
            if (TextUtils.isEmpty(centerPower)) {
                return null;
            }

            int fl = 0;
            if (centerPower.contains("大于")) {
                int index = centerPower.indexOf("大于");
                if (index != -1) {
                    String result = centerPower.substring(index + 2);
                    fl = Integer.valueOf(result) + 1;
                }
            } else {
                fl = Integer.valueOf(centerPower);
            }

            switch (fl) {
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                    return BitmapDescriptorFactory
                            .fromResource(R.drawable.icon_typhoon_s_6_7);// 热带低压
                case 8:
                case 9:
                    return BitmapDescriptorFactory
                            .fromResource(R.drawable.icon_typhoon_s_8_9);// 热带风暴
                case 10:
                case 11:
                    return BitmapDescriptorFactory
                            .fromResource(R.drawable.icon_typhoon_s_10_11);// 强热带风暴
                case 12:
                case 13:
                    return BitmapDescriptorFactory
                            .fromResource(R.drawable.icon_typhoon_s_12_13);// 台风
                case 14:
                case 15:
                    return BitmapDescriptorFactory
                            .fromResource(R.drawable.icon_typhoon_s_14_15);// 强台风
                default:
                    return BitmapDescriptorFactory
                            .fromResource(R.drawable.icon_typhoon_s_16_);// 超强台风
            }
        }
    }

    /**
     * 获取指定台风视图
     *
     * @param code
     * @return
     */
    public TyphoonView getTyphoonView(String code) {
        return typhoonViewMap.get(code);
    }

    /**
     * 移除指定台风视图
     *
     * @param code
     */
    public void removeTyphoonView(String code) {
        typhoonViewMap.remove(code);
    }

    /**
     * @param aMap
     * @param latLng
     * @param marker
     * @return
     */
    public final Marker updateLocationMarker(AMap aMap, LatLng latLng, Marker marker) {
        if (aMap == null || latLng == null) {
            return null;
        }

        if (marker != null) {
            marker.remove();
        }

        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_typhoon_location))
                .draggable(true).anchor(0.5f, 0.5f).title(null).snippet(null);

        marker = aMap.addMarker(markerOptions);
        return marker;
    }

    /**
     * 自动缩放
     *
     * @param aMap
     * @param latLng1
     * @param latLng2
     */
    public void autoZoonToSpan(AMap aMap, LatLng latLng1, LatLng latLng2) {
        if (aMap == null || latLng1 == null || latLng2 == null) {
            return;
        }

        List<PoiItem> poiItemList = new ArrayList<PoiItem>();
        LatLonPoint point = new LatLonPoint(latLng1.latitude, latLng1.longitude);
        PoiItem poiItem = new PoiItem("", point, "", "");
        poiItemList.add(poiItem);
        point = new LatLonPoint(latLng2.latitude, latLng2.longitude);
        poiItem = new PoiItem("", point, "", "");
        poiItemList.add(poiItem);
        PoiOverlay poiOverlay = new PoiOverlay(aMap, poiItemList);
        poiOverlay.zoomToSpan();
    }

    /**
     * 验证数据请求结果
     *
     * @param errorStr
     * @return
     */
    private boolean isError(String errorStr) {
        return !TextUtils.isEmpty(errorStr);
    }

    /**
     * 数据广播接收器
     */
    private PcsDataBrocastReceiver mReceiver = new PcsDataBrocastReceiver() {
        @Override
        public void onReceive(String nameStr, String errorStr) {
            if (TextUtils.isEmpty(nameStr)) {
                return;
            }
            if (isError(errorStr)) {
                mActivity.dismissProgressDialog();
                return;
            }

            if (warnLine24Up.getName().equals(nameStr)) {
                // 接收24小时警戒线
                receiveWarnLine(nameStr);
            } else if (warnLine48Up.getName().equals(nameStr)) {
                // 接收48小时警戒线
                receiveWarnLine(nameStr);
            } else if (mPackTyphoonListUp.getName().equals(nameStr)) {
                // 接收台风列表
                receiveTyphoonList();
            } else if (nameStr.equals(PackMultiTyphoonPathUp.NAME)) {
                receiverMultiTyphoon(nameStr);
            } else if (nameStr.startsWith(PackTyphoonPathUp.NAME)) {
                // 接收台风路径
                receiveTyphoonPath(nameStr);
            } else if (nameStr.startsWith(PackFycxFbtUp.NAME + "#10_3")) {
                // 接收雷达
                receiverRadar();
            } else if (nameStr.startsWith(PackFycxFbtUp.NAME + "#10_4")) {
                // 接收云图
                receiverClound();
            } else if (nameStr.equals(PackTyphoonListCurrentActivityUp.NAME)) {
                receiverCurrentTyphoon();
            }

        }
    };

}