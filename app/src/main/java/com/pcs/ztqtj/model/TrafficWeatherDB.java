package com.pcs.ztqtj.model;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.amap.api.services.poisearch.PoiSearch.Query;
import com.pcs.lib.lib_pcs_v3.PcsInit;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalTrafficPoints;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackRoadDescDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackRoadDescUp;
import com.pcs.ztqtj.control.inter.InterfaceInit;
import com.pcs.ztqtj.model.pack.PackLocalTrafficWeather;
import com.pcs.ztqtj.model.pack.TrafficHighWay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 交通气象数据库
 *
 * @author JiangZy
 */
public class TrafficWeatherDB {
    // 上下文
    private Context mContext;
    // 初始化接口
    private InterfaceInit mInterfaceInit;
    // 查询列表
    private List<Query> mQueryList = new ArrayList<Query>();
    // 公路Poi项列表
    private List<PoiItem> mPoiItemList = null;
    // 公路Poi详细项列表
    private Map<String, List<PoiItem>> mPoiItemDetailMap = null;
    // 公路标记列表
    private List<MarkerOptions> mMarkerList = null;
    // 公路详细标记Map
    private Map<String, List<MarkerOptions>> mMarkerDetailMap = null;
    // 公路/高速列表
    private PackLocalTrafficWeather mPackTrafficWeather = new PackLocalTrafficWeather();
    // 下载包：气象概况
    private Map<String, PackRoadDescDown> mPackRoadDescMap = new HashMap<String, PackRoadDescDown>();

    public void init(Context context, InterfaceInit interfaceInit) {
        mContext = context;
        mInterfaceInit = interfaceInit;

        for (TrafficHighWay pack : ZtqCityDB.getInstance().getHighWay()) {
            // 请求气象概况
            PackRoadDescUp packUp = new PackRoadDescUp();
            packUp.road_id = pack.ID;
            mPackRoadDescMap.put(packUp.getName(), new PackRoadDescDown());
            PcsDataDownload.addDownload(packUp);
            // 读取坐标列表
            pack.points = (PackLocalTrafficPoints) PcsDataManager.getInstance().getLocalPack(pack.points.getName(pack.ID));

            if (pack.points == null) {
                pack.points = new PackLocalTrafficPoints();
                // 查询POI
                queryPOI(pack.SEARCH_NAME);
            }
        }
        // }
        // 检查查询完成？
        checkQueryDone();
    }

    /**
     * 查询POI
     *
     * @param search_name
     */
    private void queryPOI(String search_name) {
        Query query = new Query(search_name, "", "福建");// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(100);// 设置每页最多返回多少条poiitem
        query.setPageNum(0);// 设置查第一页
        mQueryList.add(query);

        PoiSearch poiSearch = new PoiSearch(mContext, query);
        poiSearch.setOnPoiSearchListener(mPoiListener);
        poiSearch.searchPOIAsyn();
    }

    /**
     * 初始化失败
     */
    private void initError() {
        if (mInterfaceInit == null) {
            return;
        }
        mInterfaceInit.initDone(false);
    }

    /**
     * 检查查询完成？
     */
    private void checkQueryDone() {
        if (mQueryList.size() == 0) {
            // 启动初始化线程
            PcsInit.getInstance().getExecutorService()
                    .execute(new InitRunnable());
        }
    }

    /**
     * 注册广播接收
     *
     * @param context
     */
    public void registerReceiver(Context context) {
        PcsDataBrocastReceiver.registerReceiver(context, mReceiver);
    }

    /**
     * 注销广播接收
     *
     * @param context
     */
    public void unregisterReceiver(Context context) {
        PcsDataBrocastReceiver.unregisterReceiver(context, mReceiver);
    }

    /**
     * 获取数据包
     *
     * @return
     */
    public final PackLocalTrafficWeather getPack() {
        return mPackTrafficWeather;
    }

    /**
     * 获取公路POI列表
     *
     * @return
     */
    public final List<PoiItem> getPoiItemList() {
        return mPoiItemList;
    }

    /**
     * 获取公路POI详细列表
     *
     * @param id
     * @return
     */
    public final List<PoiItem> getPoiItemDetailList(String id) {
        if (!mPoiItemDetailMap.containsKey(id)) {
            return null;
        }

        return mPoiItemDetailMap.get(id);
    }

    /**
     * 获取公路标记
     *
     * @return
     */
    public final List<MarkerOptions> getMarkerList() {
        return mMarkerList;
    }

    /**
     * 获取公路详细标记
     *
     * @return
     */
    public final List<MarkerOptions> getMarkerDetail(String id) {
        if (!mMarkerDetailMap.containsKey(id)) {
            return null;
        }
        return mMarkerDetailMap.get(id);
    }

    /**
     * 获取交通气象概况
     *
     * @param road_id
     * @return
     */
    public final PackRoadDescDown getRoadDesc(String road_id) {
        PackRoadDescUp packUp = new PackRoadDescUp();
        packUp.road_id = road_id;
        return mPackRoadDescMap.get(packUp.getName());
    }

    private OnPoiSearchListener mPoiListener = new OnPoiSearchListener() {

        @Override
        public void onPoiSearched(PoiResult result, int rCode) {
            if (rCode != AMapException.CODE_AMAP_SUCCESS) {
                // 初始化失败
                initError();
                return;
            }
            if (result == null || result.getQuery() == null) {// 搜索poi的结果
                // 初始化失败
                initError();
                return;
            }
            if (!mQueryList.contains(result.getQuery())) {
                return;
            }
            List<PoiItem> poiItems = result.getPois();// 取得第一页的poiitem数据，页数从数字0开始
            if (poiItems == null || poiItems.size() == 0) {
                // 初始化失败
                initError();
                return;
            }
            TrafficHighWay pack = ZtqCityDB.getInstance()
                    .getHeiWayBySearchName(result.getQuery().getQueryString());
            if (pack != null) {
                for (int i = 0; i < poiItems.size(); i++) {
                    // 坐标列表
                    LatLonPoint lalLonPoint = poiItems.get(i).getLatLonPoint();
                    LatLng latLnn = new LatLng(lalLonPoint.getLatitude(),
                            lalLonPoint.getLongitude());
                    pack.points.list.add(latLnn);
                }
                // 保存坐标列表
                PcsDataManager.getInstance().saveLocalData(
                        pack.points.getName(pack.ID), pack.points);
            }
            mQueryList.remove(result.getQuery());
            // 检查查询完成？
            checkQueryDone();
        }

        @Override
        public void onPoiItemSearched(PoiItem poiItem, int i) {

        }
    };

    /**
     * 初始化线程处理
     */
    private Handler mInitHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            mInterfaceInit.initDone(true);
        }
    };

    /**
     * 初始化线程
     *
     * @author JiangZy
     */
    private class InitRunnable implements Runnable {
        @Override
        public void run() {
            // 公路Poi项列表
            mPoiItemList = new ArrayList<PoiItem>();
            // 公路Poi详细项列表
            mPoiItemDetailMap = new HashMap<String, List<PoiItem>>();
            // 公路标记Map
            mMarkerList = new ArrayList<MarkerOptions>();
            // 公路详细标记Map
            mMarkerDetailMap = new HashMap<String, List<MarkerOptions>>();

            // 遍历公路列表
            for (TrafficHighWay pack : ZtqCityDB.getInstance().getHighWay()) {
                // 添加公路Poi项
                addPoiItemList(pack);
                // 添加公路Poi详细项
                addPoiItemDetailList(pack);
                // 添加公路标记
                addMarker(pack);
                // 添加公路详细标记
                addMarkerDetail(pack);
            }
            // 线程完成
            mInitHandler.sendEmptyMessage(0);
        }

        /**
         * 添加公路Poi项
         *
         * @param pack
         */
        private void addPoiItemList(TrafficHighWay pack) {
            LatLng latLng = new LatLng(pack.SHOW_LATITUDE, pack.SHOW_LONGITUDE);
            LatLonPoint point = new LatLonPoint(latLng.latitude,
                    latLng.longitude);
            PoiItem poiItem = new PoiItem("", point, "", "");
            mPoiItemList.add(poiItem);
        }

        /**
         * 添加公路Poi详细项
         *
         * @param pack
         */
        private void addPoiItemDetailList(TrafficHighWay pack) {
            List<PoiItem> listDetail = new ArrayList<PoiItem>();
            for (int i = 0; i < pack.points.list.size(); i++) {
                LatLng latLng = pack.points.list.get(i);
                LatLonPoint point = new LatLonPoint(latLng.latitude,
                        latLng.longitude);
                PoiItem poiItem = new PoiItem("", point, "", "");
                listDetail.add(poiItem);
            }
            mPoiItemDetailMap.put(pack.ID, listDetail);
        }

        /**
         * 添加公路标记
         *
         * @param pack
         */
        private void addMarker(TrafficHighWay pack) {
            MarkerOptions options = new MarkerOptions();
            LatLng latLng = new LatLng(pack.SHOW_LATITUDE, pack.SHOW_LONGITUDE);
            options.position(latLng);
            options.title(pack.ID);
            mMarkerList.add(options);
        }

        /**
         * 添加公路详细标记
         *
         * @param pack
         */
        private void addMarkerDetail(TrafficHighWay pack) {
            List<MarkerOptions> listDetail = new ArrayList<MarkerOptions>();
            for (int i = 0; i < pack.points.list.size(); i++) {
                MarkerOptions tempOptions = new MarkerOptions();
                tempOptions.position(pack.points.list.get(i));
                tempOptions.title(pack.ID + "_" + i);
                listDetail.add(tempOptions);
            }
            mMarkerDetailMap.put(pack.ID, listDetail);
        }
    }

    /**
     * 数据广播接收器
     */
    private PcsDataBrocastReceiver mReceiver = new PcsDataBrocastReceiver() {
        @Override
        public void onReceive(String nameStr, String errorStr) {
            if (!mPackRoadDescMap.containsKey(nameStr)) {
                return;
            }
            PackRoadDescDown pack = (PackRoadDescDown) PcsDataManager.getInstance().getLocalPack(nameStr);
            if (pack != null) {
                mPackRoadDescMap.put(nameStr, pack);
            }
        }
    };
}
