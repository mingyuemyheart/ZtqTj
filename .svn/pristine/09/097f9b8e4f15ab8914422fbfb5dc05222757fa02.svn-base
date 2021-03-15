package com.pcs.ztqtj.view.myview.typhoon;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 台风View
 *
 * @author E.Sun
 *         2015年9月6日
 */
public class TyphoonView {

    /**
     * 台风编号
     */
    private String code;

    /**
     * 台风名称
     */
    private String name;

    /**
     * 台风简称
     */
    private String simpleName;

    /**
     * 实况路径视图集合
     */
    private List<TyphoonTrueView> trueList = new ArrayList<TyphoonTrueView>();

    /**
     * 预测路径视图
     */
    private TyphoonForecastView BeijingForecast, TokyoForecast, ShangHaiForecast, TaiWanForecast;

    /**
     * 台风标记ID集合
     **/
    private Map<String, String> markerIdMap = new HashMap<String, String>();

    public void addTyphoonTrueView(TyphoonTrueView view) {
        trueList.add(view);
    }

    public void setBeijingForecast(TyphoonForecastView beijingForecast) {
        BeijingForecast = beijingForecast;
    }

    public void setTokyoForecast(TyphoonForecastView tokyoForecast) {
        TokyoForecast = tokyoForecast;
    }

    public void setShangHaiForecast(TyphoonForecastView shangHaiForecast) {
        ShangHaiForecast = shangHaiForecast;
    }

    public void setTaiWanForecast(TyphoonForecastView taiWanForecast) {
        TaiWanForecast = taiWanForecast;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSimpleName() {
        return simpleName;
    }

    public void setSimpleName(String simpleName) {
        this.simpleName = simpleName;
    }

    public void show(AMap aMap) {
        if (aMap == null) {
            return;
        }

        showTrue(aMap);
        showForecast(aMap);
    }

    /**
     * 隐藏台风
     */
    public void hide() {
        hideTrue();
        hideForecast();
        markerIdMap.clear();
    }

    /**
     * 显示实况路径（包含全部实况路径点、实况路径线及最近点风圈）
     *
     * @param aMap
     */
    private void showTrue(AMap aMap) {
        if (trueList.size() <= 0) {
            return;
        }

        for (int i = 0; i < trueList.size(); i++) {
            showTruePoint(aMap, i);
        }

        TyphoonTrueView pointView = trueList.get(trueList.size() - 1);
        if (pointView != null) {
            pointView.showWind(aMap);
        }
    }

    /**
     * 显示指定实况路径点
     *
     * @param aMap
     * @param position
     */
    private void showTruePoint(AMap aMap, int position) {
        TyphoonTrueView view = trueList.get(position);
        showTruePoint(aMap, view);
    }

    /**
     * 显示实况路径点
     *
     * @param aMap
     * @param view
     */
    private void showTruePoint(AMap aMap, TyphoonTrueView view) {
        if (view != null) {
            String markerID = view.showPoint(aMap);
            markerIdMap.put(markerID, code);
        }
    }

    /**
     * 显示风圈
     *
     * @param aMap
     * @param position
     */
    public void showWind(AMap aMap, int position) {
        if (aMap == null || position < 0 || position >= trueList.size()) {
            return;
        }

        TyphoonTrueView pointView = trueList.get(position);
        if (pointView != null) {
            pointView.showWind(aMap);
        }
    }

    /**
     * 播放台风
     *
     * @param aMap
     * @param position
     */
    public void play(AMap aMap, int position) {
        if (aMap == null || position < 0 || position >= trueList.size()) {
            return;
        }

        TyphoonTrueView trueView;
        if (position > 0) {
            trueView = trueList.get(position - 1);
            trueView.hideWind();
        }

        trueView = trueList.get(position);
        if (trueView != null) {
            LatLng latLng = trueView.getLatLng();
            if (latLng != null) {
                if (position == 0) {
                    aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 4.5f));
                } else {
                    aMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));
                }
            }
            showTruePoint(aMap, trueView);
            trueView.showWind(aMap);
        }

        // 当播放到最后一个，显示预测路径
        if (position == trueList.size() - 1) {
            showForecast(aMap);
        }
    }

    /**
     * 隐藏实况路径
     */
    public void hideTrue() {
        for (TyphoonTrueView pointView : trueList) {
            if (pointView != null) {
                pointView.hidePoint();
                pointView.hideWind();
            }
        }
    }

    /**
     * 显示预测路径
     */
    public void showForecast(AMap aMap) {
        if (aMap == null) {
            return;
        }

        if (BeijingForecast != null) {
            BeijingForecast.show(aMap);
        }
        if (TokyoForecast != null) {
            TokyoForecast.show(aMap);
        }
        if (ShangHaiForecast != null) {
            ShangHaiForecast.show(aMap);
        }
        if (TaiWanForecast != null) {
            TaiWanForecast.show(aMap);
        }
    }

    /**
     * 隐藏预测路径
     */
    public void hideForecast() {
        if (BeijingForecast != null) {
            BeijingForecast.hide();
        }
        if (TokyoForecast != null) {
            TokyoForecast.hide();
        }
        if (ShangHaiForecast != null) {
            ShangHaiForecast.hide();
        }
        if (TaiWanForecast != null) {
            TaiWanForecast.hide();
        }
    }

    /**
     * 获取实况路径经纬度集合
     *
     * @return
     */
    public List<LatLng> getTrueLatLng() {
        List<LatLng> list = new ArrayList<LatLng>();
        for (TyphoonTrueView view : trueList) {
            if (view.getLatLng() != null) {
                list.add(view.getLatLng());
            }
        }
        return list;
    }

    /**
     * 获取预测台风路径点
     * @return
     */
    public List<LatLng> getForecastLatLng() {
        List<LatLng> latLngList = new ArrayList<>();
        if(BeijingForecast != null) {
            latLngList.addAll(BeijingForecast.getForecastLatlngList());
        }
        if(TokyoForecast != null) {
            latLngList.addAll(TokyoForecast.getForecastLatlngList());
        }
        if(ShangHaiForecast != null) {
            latLngList.addAll(ShangHaiForecast.getForecastLatlngList());
        }
        if(TaiWanForecast != null) {
            latLngList.addAll(TaiWanForecast.getForecastLatlngList());
        }
        return latLngList;
    }

    /**
     * 获取所有台风路径点(实况路径和预测路径)
     * @return
     */
    public List<LatLng> getAllLatLng() {
        List<LatLng> latLngList = new ArrayList<>();
        latLngList.addAll(getTrueLatLng());
        latLngList.addAll(getForecastLatLng());
        return latLngList;
    }

    /**
     * 获取最近点实况路径视图
     *
     * @return
     */
    public TyphoonTrueView getLastTrueView() {
        if (trueList.size() >= 1) {
            return trueList.get(trueList.size() - 1);
        }
        return null;
    }

    /**
     * 获取指定实况路径视图
     *
     * @param position
     * @return
     */
    public TyphoonTrueView getTrueView(int position) {
        if (position < 0 || position >= trueList.size()) {
            return null;
        }
        return trueList.get(position);
    }

    /**
     * 获取路径点数量
     *
     * @return
     */
    public int getSize() {
        return trueList.size();
    }

    /**
     * 获取标记点所属台风编号
     *
     * @param markerID
     * @return
     */
    public String getCode(String markerID) {
        return markerIdMap.get(markerID);
    }

}
