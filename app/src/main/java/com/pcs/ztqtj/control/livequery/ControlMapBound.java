package com.pcs.ztqtj.control.livequery;

import android.content.Context;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Polygon;
import com.amap.api.maps.model.PolygonOptions;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.district.DistrictItem;
import com.amap.api.services.district.DistrictResult;
import com.amap.api.services.district.DistrictSearch;
import com.amap.api.services.district.DistrictSearchQuery;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.lib.lib_pcs_v3.model.image.AsyncTask;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 地图边界控制器
 */
public class ControlMapBound {

    private AMap aMap;
    private Context context;

    private List<PackLocalCity> allCityList = new ArrayList<>();

    // 一二级城市边界列表
    private List<String[]> lv1BoundList = new ArrayList<>();
    private List<String[]> lv2BoundList = new ArrayList<>();

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    // 线程池
    private ExecutorService fixedThreadPool = Executors.newFixedThreadPool(MAXIMUM_POOL_SIZE);

    //画图任务列表
    private List<DrawTask> drawTaskList = new ArrayList<>();
    // 面数据列表
    private List<Polygon> polygonLv1List = new ArrayList<>();
    private List<Polygon> polygonLv2List = new ArrayList<>();

    private static final boolean isSingleBound = true;

    private int lineWidth = 3;

    // 是否添加边界
    private boolean isAddBound = false;
    private int color;

    public ControlMapBound(Context context, AMap aMap, int color) {
        this.aMap = aMap;
        this.context = context;
        this.color = color;
        init();
    }

    private void init() {
        aMap.setOnCameraChangeListener(onCameraChangeListener);
        allCityList.addAll(ZtqCityDB.getInstance().getCityLv1());
//        if(!isSingleBound) {
//            allCityList.addAll(ZtqCityDB.getInstance().getCityLv2());
//        }
    }

    public void start() {
        isAddBound = true;
        startSearch(allCityList);
    }

    public void setLineWidth(int lineWidth) {
        this.lineWidth = lineWidth;
    }

    private void startSearch(List<PackLocalCity> list) {
        SearchTask task = new SearchTask();
        task.executeOnExecutor(fixedThreadPool, list);
    }

    /**
     * 搜索行政区划边界数据
     */
    private void search(PackLocalCity bean) {
        DistrictSearch search = new DistrictSearch(context);
        DistrictSearchQuery query = new DistrictSearchQuery();
        query.setKeywords(bean.NAME);
        query.setShowBoundary(true);
        query.setShowChild(false);
        query.setShowBusinessArea(false);
        search.setQuery(query);
        search.setOnDistrictSearchListener(onDistrictSearchListener);
        search.searchDistrictAsyn();
    }

    private void draw(String[] polyStr, BoundType boundType) {
        if (polyStr == null || polyStr.length == 0) {
            return;
        }
        int a = 10;
        for (String str : polyStr) {
            String[] lat = str.split(";");
            PolygonOptions polygonOptions = new PolygonOptions();
            boolean isFirst = true;
            LatLng firstLatLng = null;
            for (String latstr : lat) {
                String[] lats = latstr.split(",");
                if (isFirst) {
                    isFirst = false;
                    firstLatLng = new LatLng(Double
                            .parseDouble(lats[1]), Double
                            .parseDouble(lats[0]));
                } else {
                    polygonOptions.add(new LatLng(Double
                            .parseDouble(lats[1]), Double
                            .parseDouble(lats[0])));
                }
//                if (a-- == 0) {
//                    polygonOptions.add(new LatLng(Double
//                            .parseDouble(lats[1]), Double
//                            .parseDouble(lats[0])));
//                    a = 10;
//                }
            }
            if (firstLatLng != null) {
                polygonOptions.add(firstLatLng);
            }
            int transparent = context.getResources().getColor(android.R.color.transparent);
            int transparent2 = context.getResources().getColor(R.color.bg_black_alpha70);

            polygonOptions
                    .strokeWidth(lineWidth)
                    .strokeColor(color)
                    .fillColor(transparent)
                    .zIndex(MapElementZIndex.mapBoundZIndex)
                    .visible(false);
            // 添加面数据至缓存列表
            switch (boundType) {
                case LV1:
                    polygonLv1List.add(aMap.addPolygon(polygonOptions));
                    break;
                case LV2:
                    polygonLv2List.add(aMap.addPolygon(polygonOptions));
                    break;
            }
        }
    }

    private void drawAsyn(String[] polyStr, BoundType boundType) {
        DrawTask task = new DrawTask();
        task.executeOnExecutor(fixedThreadPool, polyStr, boundType);
        drawTaskList.add(task);
    }

    private void setBoundVisibility(VisibilityType visibility) {
        switch (visibility) {
            case VISIBILITY_LV1:
                showBounds(true, polygonLv1List);
                showBounds(false, polygonLv2List);
                break;
            case VISIBILITY_LV2:
                showBounds(false, polygonLv1List);
                showBounds(true, polygonLv2List);
                break;
            case GONE:
                showBounds(false, polygonLv1List);
                showBounds(false, polygonLv2List);
                break;
        }
    }

    private void showBounds(boolean b, List<Polygon> polygonList) {
        for (Polygon polygon : polygonList) {
            polygon.setVisible(b);
        }
    }

    private DistrictSearch.OnDistrictSearchListener onDistrictSearchListener = new DistrictSearch
            .OnDistrictSearchListener() {

        @Override
        public void onDistrictSearched(DistrictResult districtResult) {
            if (districtResult == null || districtResult.getDistrict() == null) {
                return;
            }
            String keywords = districtResult.getQuery().getKeywords();

            //通过ErrorCode判断是否成功
            if (districtResult.getAMapException() != null && districtResult.getAMapException().getErrorCode() ==
                    AMapException.CODE_AMAP_SUCCESS) {
                DistrictItem districtItem = null;
                try {
                    districtItem = districtResult.getDistrict().get(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (districtItem == null) {
                    return;
                }

                String[] polyStr = districtItem.districtBoundary();
                PackLocalCity bean = ZtqCityDB.getInstance().getCityInfo1_Name(keywords);
                if (bean != null) {
                    lv1BoundList.add(polyStr);
                    drawAsyn(polyStr, BoundType.LV1);
                } else if (!isSingleBound) {
//                    bean = ZtqCityDB.getInstance().getCityInfo2_Name(keywords);
//                    if(bean != null) {
//                        lv2BoundList.add(polyStr);
//                        drawAsyn(polyStr, BoundType.LV2);
//                    }
                }
            }
        }
    };

    private AMap.OnCameraChangeListener onCameraChangeListener = new AMap.OnCameraChangeListener() {
        @Override
        public void onCameraChange(CameraPosition cameraPosition) {
            float zoom = cameraPosition.zoom;
            if (isSingleBound) {
                if (!isAddBound) {
                    if (zoom >= 8f) {
                        start();
                    }
                } else {
                    if (zoom >= 7f && polygonLv1List.size() > 0) {
                        setBoundVisibility(VisibilityType.VISIBILITY_LV1);
                    } else {
                        setBoundVisibility(VisibilityType.GONE);
                    }
                }
            } else {
                if (!isAddBound) {
                    if (zoom >= 8f) {
                        start();
                    }
                } else {
                    if (zoom >= 7f && zoom <= 8f && polygonLv1List.size() > 0) {
                        setBoundVisibility(VisibilityType.VISIBILITY_LV1);
                    } else if (zoom > 8f && polygonLv2List.size() > 0) {
                        setBoundVisibility(VisibilityType.VISIBILITY_LV2);
                    } else {
                        setBoundVisibility(VisibilityType.GONE);
                    }
                }
            }
        }

        @Override
        public void onCameraChangeFinish(CameraPosition cameraPosition) {

        }
    };

    private class SearchTask extends AsyncTask<List<PackLocalCity>, Integer, Void> {

        @Override
        protected Void doInBackground(List<PackLocalCity>... params) {

            for (PackLocalCity bean : params[0]) {
                search(bean);
            }
            return null;
        }
    }

    /**
     * 画图异步类
     */
    public class DrawTask extends AsyncTask<Object, Object, Void> {

        @Override
        protected Void doInBackground(Object... params) {
            if (isCancelled()) {
                return null;
            }
            String[] polyStr = (String[]) params[0];
            BoundType boundType = (BoundType) params[1];
            draw(polyStr, boundType);
            return null;
        }
    }

    private enum VisibilityType {
        VISIBILITY_LV1,
        VISIBILITY_LV2,
        GONE
    }

    private enum BoundType {
        LV1,
        LV2
    }

}
