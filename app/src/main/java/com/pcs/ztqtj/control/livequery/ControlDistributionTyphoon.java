package com.pcs.ztqtj.control.livequery;

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
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.GroundOverlayOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.maps.model.Text;
import com.amap.api.maps.model.TextOptions;
import com.pcs.lib_ztqfj_v2.model.pack.net.typhoon.ForecastPoint;
import com.pcs.lib_ztqfj_v2.model.pack.net.typhoon.FulPoint;
import com.pcs.lib_ztqfj_v2.model.pack.net.typhoon.PackTyphoonPathDown;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.view.myview.typhoon.TyphoonForecastView;
import com.pcs.ztqtj.view.myview.typhoon.TyphoonTrueView;
import com.pcs.ztqtj.view.myview.typhoon.TyphoonView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 风雨查询分布图台风控制类
 * Created by tyaathome on 2017/4/26.
 */

public class ControlDistributionTyphoon {

    private Context mContext;
    // 高德地图
    private AMap aMap;

    /**
     * 台风路径View
     */
    //private TyphoonView typhoonView;

    // 风眼图片列表
    private ArrayList<BitmapDescriptor> mListCentreBitmap = new ArrayList<BitmapDescriptor>();
    private static final int PAINT_WIDTH = 5;
    private Map<String, TyphoonView> typhoonViewMap = new HashMap<>();

    // 完成回调
    private OnFinishListener mListener;

    public ControlDistributionTyphoon(Context context, AMap aMap) {
        this.mContext = context;
        this.aMap = aMap;
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

    public void showTyphoonPath(PackTyphoonPathDown down) {
        TyphoonPathTask task = new TyphoonPathTask();
        task.execute(down);
    }

    /**
     * 隐藏所有台风路径
     */
    public void hideAllTyphoonPath() {
        for(Map.Entry<String, TyphoonView> entry : typhoonViewMap.entrySet()) {
            entry.getValue().hide();
        }
        typhoonViewMap.clear();
    }

    /**
     * 隐藏单一台风路径
     * @param code
     */
    public void hideTyphoonPath(String code) {
        if(typhoonViewMap.containsKey(code)) {
            typhoonViewMap.get(code).hide();
        }
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
        sb.append("\n").append("七级风圈半径：").append(point.fl_7)
                .append(fl_7Unit);
        sb.append("\n").append("十级风圈半径：").append(point.fl_10)
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
        if(!TextUtils.isEmpty(point.time)) {
            result += point.time;
        }

        // 风速
        if(!TextUtils.isEmpty(point.fs)) {
            result += ",风速" + point.fs;
        }
        // 风力
        if(!TextUtils.isEmpty(point.fl)) {
            result += "\n风力" + point.fl + "级";
        }
        // 气旋
        if(!TextUtils.isEmpty(point.qx)) {
            result += "(" + point.qx + ")";
        }

        return result;
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
    private MarkerOptions addPathMarkerLast(LatLng latLng, Context context, String code, FulPoint point, String title, String content) {
        if (latLng == null) {
            return null;
        }

        MarkerOptions options = new MarkerOptions().position(latLng)
                .icon(getMarkerIconLast(context, code, point))
                .snippet(content)
                //.zIndex(MapElementZIndex.markerZIndex)
                .anchor(0.5f, 0.5f)
                .title(title).zIndex(2);
        return options;
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
        return BitmapDescriptorFactory.fromView(view);
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
                //.zIndex(MapElementZIndex.markerSmallZIndex)
                .snippet(content);
        return options;
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
                .zIndex(1)
                //.zIndex(MapElementZIndex.markerSmallZIndex)
                .snippet(content);
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

        PolylineOptions options = (new PolylineOptions())
                .add(latLng1).add(latLng2)
                .width(4).color(Color.RED)
                .setDottedLine(false);
        return options;
    }

    /**
     * 添加预测路径
     *
     * @param list
     * @param color
     * @return
     */
    private TyphoonForecastView addTyphoonForecastView(List<ForecastPoint> list, int color, String address) {
        if (list == null) {
            return null;
        }

        TyphoonForecastView forecast = new TyphoonForecastView();
        double wd, jd;
        LatLng latLng;
        List<LatLng> latLngList = new ArrayList<LatLng>();
        for (ForecastPoint item : list) {
            forecast.addTimeOptions(addForecastMarker(item, color));
            forecast.addTipsOptions(addForecastTipsMarker(item, address));
            wd = Double.parseDouble(item.wd);
            jd = Double.parseDouble(item.jd);
            latLng = new LatLng(wd, jd);
            latLngList.add(latLng);
        }
        forecast.setLineOptions(addForecastLine(latLngList, color));
        //forecast.setProbabilisticChart(addProbabilisticChart(latLngList), addProbabilisticLastPoint(latLngList));

        return forecast;
    }

    /**
     * 添加预测路径标记
     *
     * @param item
     * @param color
     * @return
     */
    private TextOptions addForecastMarker(ForecastPoint item, int color) {
        if (item == null) {
            return null;
        }

        String strTV = item.time;
        double wd = Double.parseDouble(item.wd);
        double jd = Double.parseDouble(item.jd);
        LatLng point = new LatLng(wd, jd);

        boolean isVisible = false;
        if(!TextUtils.isEmpty(item.time)) {
            isVisible = true;
        }

        TextOptions marker = new TextOptions().position(point)
                .text(strTV).fontColor(color)
                .backgroundColor(mContext.getResources().getColor(android.R.color.transparent))
                .fontSize(30)
                .typeface(Typeface.DEFAULT_BOLD)
                .align(Text.ALIGN_RIGHT, Text.ALIGN_CENTER_VERTICAL)
                //.zIndex(MapElementZIndex.markerSmallZIndex)
                .visible(isVisible).zIndex(1);

        return marker;
    }

    /**
     * 添加预测路径标记
     *
     * @param item
     * @return
     */
    private MarkerOptions addForecastTipsMarker(ForecastPoint item, String address) {
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
        if(!TextUtils.isEmpty(item.time)
                && !TextUtils.isEmpty(item.fl)
                && !TextUtils.isEmpty(item.fs)) {
            //typhoonTime = getTyphoonTime(item);
            isVisible = true;
            typhoonContent = getTyphoonContent(item);
        }
        MarkerOptions marker = new MarkerOptions().position(latLng)
                .icon(point)
                .title(address)
                .anchor(0.5f, 0.5f)
                .snippet(typhoonContent)
                //.zIndex(MapElementZIndex.markerSmallZIndex)
                .visible(isVisible);

        return marker;
    }

    /**
     * 添加预测路径线
     *
     * @param list
     * @param color
     * @return
     */
    private PolylineOptions addForecastLine(List<LatLng> list, int color) {
        if (list == null || list.size() <= 1) {
            return null;
        }

        PolylineOptions options = new PolylineOptions().width(4)
                .color(color)
                .setDottedLine(true);

        for (LatLng latLng : list) {
            if (latLng != null) {
                options.add(latLng);
            }
        }

        return options;
    }

    public void setFinishListener(OnFinishListener listener) {
        mListener = listener;
    }

    private class TyphoonPathTask extends AsyncTask<Object, Void, TyphoonView> {

        @Override
        protected TyphoonView doInBackground(Object... params) {
            PackTyphoonPathDown down = (PackTyphoonPathDown) params[0];
            TyphoonView typhoonView = new TyphoonView();
            typhoonView.setCode(down.typhoonPathInfo.code);
            typhoonView.setName(down.typhoonPathInfo.name);
            typhoonView.setSimpleName(down.typhoonPathInfo.simplename);

            List<FulPoint> list = down.typhoonPathInfo.fulPointList;
            FulPoint point;
            LatLng latLng;
            String typhoonTime;
            String typhoonContent;
            MarkerOptions pathMarker;
            TyphoonTrueView pointView;

            for (int i = 0; i < list.size(); i++) {
                point = list.get(i);
                latLng = getLatLng(point);
                typhoonTime = getTyphoonTime(point);
                typhoonContent = getTyphoonContent(point);

                if (i == list.size() - 1) {
                    // 最后一个点
                    pathMarker = addPathMarkerLast(latLng, mContext, down.typhoonPathInfo.code, point, typhoonTime, typhoonContent);
                } else {
                    pathMarker = addPathMarkerSmart(latLng, point, typhoonTime, typhoonContent);
                }

                // 一个路径点匹配一个台风时间、台风详情、风眼、风圈、实况路径线
                if (pathMarker != null) {
                    pointView = new TyphoonTrueView();
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
                            pointView.setWindSevenGroudOverlayOptions(addWindPowerSevenNew(aMap, latLng, point));
                        }
                        //10级风圈
                        if(ne10+se10+sw10+nw10 <= 0) {
                            pointView.setWindTenOptions(addPower10(latLng, point));
                        } else {
                            pointView.setWindTenGroudOverlayOptions(addWindPowerTenNew(aMap, latLng, point));
                        }

                        if(ne12+se12+sw12+nw12 > 0) {
                            pointView.setWindTwelveGroudOverlayOptions(addWindPowerTwelveNew(aMap, latLng, point));
                        }
                    }

                    if (i > 0) {
                        pointView.setTrueLineOptions(addTrueLine(latLng, getLatLng(list.get(i - 1))));
                    }
                    typhoonView.addTyphoonTrueView(pointView);
                }
            }

            if (list.size() > 0 && !TextUtils.isEmpty(list.get(0).jd) && !TextUtils.isEmpty(list.get(0).wd)) {
                int shColor = Color.parseColor("#0602EB");
                // 初始化预测路径
                typhoonView.setBeijingForecast(addTyphoonForecastView(down.typhoonPathInfo.BeijingDottedPointList, Color.RED, "北京预报"));
                typhoonView.setTokyoForecast(addTyphoonForecastView(down.typhoonPathInfo.TokyoDottedPointList, Color.BLACK, "东京预报"));
                typhoonView.setShangHaiForecast(addTyphoonForecastView(down.typhoonPathInfo.ShangHaiDottedPointList, shColor, "天津预报"));
                typhoonView.setTaiWanForecast(addTyphoonForecastView(down.typhoonPathInfo.TaiWanDottedPointList,
                        mContext.getResources().getColor(R.color.typhoon_tw), "美国预报"));
            }
            if(typhoonView != null) {
                typhoonViewMap.put(down.typhoonPathInfo.code, typhoonView);
            }
            return typhoonView;
        }

        @Override
        protected void onPostExecute(TyphoonView typhoonView) {
            super.onPostExecute(typhoonView);
            if(typhoonView != null) {
                typhoonView.show(aMap);
            }
            if(mListener != null) {
                mListener.onComplete(typhoonView);
            }
        }
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

        Point leftPoint = aMap.getProjection().toScreenLocation(leftLatlng);
        Point topPoint = aMap.getProjection().toScreenLocation(topLatlng);
        Point rightPoint = aMap.getProjection().toScreenLocation(rightLatlng);
        Point bottomPoint = aMap.getProjection().toScreenLocation(bottomLatlng);
//        int width = rightPoint.x-leftPoint.x;
//        int height = bottomPoint.y-topPoint.y;
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

//            canvas.drawArc(oval1, 270, 90, true, strokePaint);
//            canvas.drawArc(oval2, 0, 90, true, strokePaint);
//            canvas.drawArc(oval3, 90, 90, true, strokePaint);
//            canvas.drawArc(oval4, 180, 90, true, strokePaint);

//            canvas.drawArc(oval1, 270, 90, true, fillPaint);
//            canvas.drawArc(oval2, 0, 90, true, fillPaint);
//            canvas.drawArc(oval3, 90, 90, true, fillPaint);
//            canvas.drawArc(oval4, 180, 90, true, fillPaint);

        Path path = new Path();
        int x = rect.width()/2;
        int y = (int) (rect.height()/2 - oval1.height()/2);
        path.moveTo(x, y);
//            path.addArc(oval1, 270, 90);
//            x = (int) (rect.width()/2 + oval2.width()/2);
//            y = rect.height()/2;
//            path.lineTo(x, y);
//            path.addArc(oval2, 0, 90);
//            x = rect.width()/2;
//            y = (int) (rect.height()/2 + oval3.height()/2);
//            path.lineTo(x, y);
//            path.addArc(oval3, 90, 90);
//            x = (int) (rect.width()/2 - oval4.width()/2);
//            y = rect.height()/2;
//            path.lineTo(x, y);
//            path.addArc(oval4, 180, 45);
//            x = rect.width()/2;
//            y = (int) (rect.height()/2 - oval1.height()/2);
//            path.lineTo(x, y);
        path.arcTo(oval1, 270, 90);
        path.arcTo(oval2, 0, 90);
        path.arcTo(oval3, 90, 90);
        path.arcTo(oval4, 180, 90);
        path.close();
        canvas.drawPath(path, fillPaint);
        canvas.drawPath(path, strokePaint);

        return new GroundOverlayOptions()
                .anchor(0.5f, 0.5f)
                //.transparency(0.7f)
                .image(BitmapDescriptorFactory.fromBitmap(bitmap))
                .position(new LatLng(leftLatlng.latitude, topLatlng.longitude), max*2*1000, max*2*1000)
                .zIndex(2);
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


    public interface OnFinishListener {
        void onComplete(TyphoonView typhoonView);
    }
}
