package com.pcs.ztqtj.view.activity.newairquality;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.TextureSupportMapFragment;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polygon;
import com.amap.api.maps.model.PolygonOptions;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.district.DistrictItem;
import com.amap.api.services.district.DistrictResult;
import com.amap.api.services.district.DistrictSearch;
import com.amap.api.services.district.DistrictSearchQuery;
import com.pcs.lib.lib_pcs_v3.control.log.Log;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib.lib_pcs_v3.model.image.AsyncTask;
import com.pcs.lib.lib_pcs_v3.model.image.ImageFetcher;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCity;
import com.pcs.lib_ztqfj_v2.model.pack.net.airinfopack.AirRankNew;
import com.pcs.lib_ztqfj_v2.model.pack.net.airinfopack.PackAirCityInfoDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.airinfopack.PackAirCityInfoUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.airinfopack.PackAirRankNewDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.airinfopack.PackAirRankNewUp;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.air_quality.AdapterAirCityStation;
import com.pcs.ztqtj.control.adapter.air_quality.AdapterAirRankCount;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
import com.pcs.ztqtj.view.fragment.air.FragmentAirForecast;
import com.pcs.ztqtj.view.myview.MyGridView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Z on 2017/1/12.
 * <p>
 * 空气质量地图模式。
 */

public class ActivityAir extends FragmentActivityZtqBase implements DistrictSearch.OnDistrictSearchListener, AMap
        .OnMapLoadedListener, AMap.OnCameraChangeListener {
    private RadioGroup radgroup;
    private AMap mAMap;
    // 城市空气质量窗口
    private View layoutRank;
    private MyReceiver receiver = new MyReceiver();

    private LatLng centerLatlng = new LatLng(29.310379437777698, 114.66025114059451);

    // 空气质量上传包
    private PackAirRankNewUp rankUp = new PackAirRankNewUp();
    private List<AirRankNew> airRankList = new ArrayList<>();

    // 站点列表序号
    private int listPosition = 0;

    // 开始
    private static final int WHAT_START = 0;
    // 搜索边界
    private static final int WHAT_SEARCH = 1;
    // 画面
    private static final int WHAT_DRAW = 2;
    // 隐藏进度条对话框
    private static final int WHAT_DISMISS = 3;

    private MyGridView gridView;
    private AdapterAirRankCount adapter;
    //适配器：城市站点详情
    private AdapterAirCityStation mAdapterAirCityStation;
    // 空气分级统计数据
    private String[] airRankCountList = new String[6];

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    // 线程池
    private ExecutorService fixedThreadPool = Executors.newFixedThreadPool(MAXIMUM_POOL_SIZE);

    //画图任务列表
    private List<DrawTask> drawTaskList = new ArrayList<>();
    // 面数据列表
    private List<Polygon> polygonOptionsList = new ArrayList<>();
    // 点数据列表
    private List<Marker> markerOptionsList = new ArrayList<Marker>();

    // 是否显示面
    private boolean isShowPoly = true;
    // 是否显示点
    private boolean isShowMarker = true;
    //地图初始缩放级别
    private float MAP_INIT_ZOOM = 5f;

    private RadioButton air_pollution_distribution;
    private List<List<AirRankNew>> airRankColumnList = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_air);
        //获取地图控件引用
        setTitleText("空气质量");
        createImageFetcher();
        initView();
        intData();
        initEvent();
        initCityStations();
        radgroup.check(R.id.air_pollution_forecast);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            PcsDataBrocastReceiver.unregisterReceiver(this, receiver);
            receiver = null;
        }
    }

    public ImageFetcher getMImageFetcher() {
        return getImageFetcher();
    }


    private void intData() {
        PcsDataBrocastReceiver.registerReceiver(this, receiver);
        adapter = new AdapterAirRankCount(this, airRankCountList);
        gridView.setAdapter(adapter);


//        fragmentMap = SupportMapFragment.newInstance(aOptions);
//        mAMap = fragmentMap.getMap();
//        mAMap.setOnMapLoadedListener(this);
//        mAMap.setOnCameraChangeListener(this);
//        mAMap.setOnMarkerClickListener(mOnMarker);
//        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout, fragmentMap).commit();
//        reqAirRank();


        //showForecastView();
        shwoMap();
    }

    private LinearLayout fragment_forecast;
    private LinearLayout fragment_layout;

    private void initView() {
        air_pollution_distribution = (RadioButton) findViewById(R.id.air_pollution_distribution);
        radgroup = (RadioGroup) findViewById(R.id.radgroup);
        gridView = (MyGridView) findViewById(R.id.gridview);
        layoutRank = findViewById(R.id.layout_rank);
        fragment_forecast = (LinearLayout) findViewById(R.id.fragment_forecast);

        fragment_layout = (LinearLayout) findViewById(R.id.fragment_layout);

        TextView textCity = (TextView) findViewById(R.id.tv_info);
        textCity.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
    }

    private FragmentAirForecast fragmentForecast;
    private TextureSupportMapFragment fragmentMap;

    private void initEvent() {
        radgroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.air_pollution_forecast) {
                    showForecastView();
                    restoreMap();
                } else {
                    shwoMap();
                }
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(airRankColumnList.size() > position) {
                    int index = 0;
                    switch (position) {
                        case 0:
                            index = 0;
                            break;
                        case 1:
                            index = 3;
                            break;
                        case 2:
                            index = 1;
                            break;
                        case 3:
                            index = 4;
                            break;
                        case 4:
                            index = 2;
                            break;
                        case 5:
                            index = 5;
                            break;
                    }
                    Intent intent = new Intent(ActivityAir.this, ActivityAirRankLevel.class);
                    intent.putParcelableArrayListExtra("listdata", (ArrayList<? extends Parcelable>) airRankColumnList.get(index));
                    startActivity(intent);
                }


            }
        });
    }

    /*展示扩散预览图*/
    private void showForecastView() {
        // 隐藏下部窗口
        layoutRank.setVisibility(View.GONE);
        fragment_layout.setVisibility(View.GONE);
        fragment_forecast.setVisibility(View.VISIBLE);

        if (fragmentForecast == null) {
            fragmentForecast = new FragmentAirForecast();
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_forecast, fragmentForecast).commit();
        hiddCityStations();
    }

    /*展示污染分布图*/
    private void shwoMap() {

        fragment_layout.setVisibility(View.VISIBLE);
        fragment_forecast.setVisibility(View.GONE);
        AMapOptions o = new AMapOptions();
        o.tiltGesturesEnabled(false);
        o.rotateGesturesEnabled(false);
        fragmentMap = TextureSupportMapFragment.newInstance(o);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout, fragmentMap).commit();
        mAMap = fragmentMap.getMap();
        if(mAMap == null) {
            return;
        }
        mAMap.setOnMapLoadedListener(ActivityAir.this);
        mAMap.setOnCameraChangeListener(ActivityAir.this);
        mAMap.setOnMarkerClickListener(mOnMarker);
        moveCameraToCenter();
        reqAirRank();
    }

    /**
     * 初始化城市详情
     */
    private void initCityStations() {
        ListView listView = (ListView) findViewById(R.id.list_city_aqi);
        mAdapterAirCityStation = new AdapterAirCityStation(this);
        listView.setAdapter(mAdapterAirCityStation);
    }

    /**
     * 回收地图
     */
    private void restoreMap() {
        if (mAMap != null) {
            mAMap.clear();
        }
        mHandler.removeMessages(WHAT_DRAW);
        restoreDrawTask();
        restoreCache();
    }

    /**
     * 回收画图任务
     */
    private void restoreDrawTask() {
        for (DrawTask task : drawTaskList) {
            task.cancel(true);
        }
        drawTaskList.clear();
    }

    /**
     * 回收缓存
     */
    private void restoreCache() {
        polygonOptionsList.clear();
        markerOptionsList.clear();
    }

    /**
     * 搜索行政区划边界数据
     */
    private void search(AirRankNew bean) {
        DistrictSearch search = new DistrictSearch(this);
        DistrictSearchQuery query = new DistrictSearchQuery();
        query.setKeywords(bean.city);
        query.setShowBoundary(true);
        query.setShowChild(false);
        query.setShowBusinessArea(false);
        search.setQuery(query);
        search.setOnDistrictSearchListener(this);
        search.searchDistrictAsyn();
    }

    /**
     * 通过城市名搜索城市空气信息
     *
     * @param city
     * @return
     */
    private AirRankNew searchAirRankByCityId(String city) {
        for (AirRankNew bean : airRankList) {
            if (bean.map_name.equals(city)) {
                return bean;
            }
        }
        return null;
    }

    /**
     * 绘制面数据
     */
    private void draw(String[] polyStr, AirRankNew bean, LatLng centerLatlng) {
        int aqi = Integer.parseInt(bean.num);
        if (polyStr == null || polyStr.length == 0) {
            return;
        }
        int a = 50;
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
                }
                if (a-- == 0) {
                    polygonOptions.add(new LatLng(Double
                            .parseDouble(lats[1]), Double
                            .parseDouble(lats[0])));
                    a = 50;
                }
//                polygonOptions.add(new LatLng(Double
//                        .parseDouble(lats[1]), Double
//                        .parseDouble(lats[0])));
            }
            if (firstLatLng != null) {
                polygonOptions.add(firstLatLng);
            }

            int color = getMyColor(aqi);

            polygonOptions.strokeWidth(0).fillColor(color);
            polygonOptions.visible(isShowPoly);
            // 添加面数据至缓存列表
            polygonOptionsList.add(mAMap.addPolygon(polygonOptions));
        }

        if (centerLatlng == null) {
            return;
        }
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(centerLatlng)
                .icon(BitmapDescriptorFactory.fromBitmap(getIcon(aqi)))
                .anchor(0.5f, 0.35f)
                .visible(isShowMarker);
        // 添加点数据至缓存列表
        Marker marker = mAMap.addMarker(markerOptions);
        marker.setObject(bean);
        markerOptionsList.add(marker);
    }

    /**
     * 画面
     */
    private void drawPoly(String[] polyStr, int aqi) {
        if (polyStr == null || polyStr.length == 0) {
            return;
        }
        int a = 50;
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
                }
                if (a-- == 0) {
                    polygonOptions.add(new LatLng(Double
                            .parseDouble(lats[1]), Double
                            .parseDouble(lats[0])));
                    a = 50;
                }
//                polygonOptions.add(new LatLng(Double
//                        .parseDouble(lats[1]), Double
//                        .parseDouble(lats[0])));
            }
            if (firstLatLng != null) {
                polygonOptions.add(firstLatLng);
            }

            int color = getMyColor(aqi);

            polygonOptions.strokeWidth(0).fillColor(color);

            // 添加面数据至缓存列表
            polygonOptionsList.add(mAMap.addPolygon(polygonOptions));
        }
    }

    /**
     * 是否显示面
     *
     * @param b
     */
    private void showAllPoly(boolean b) {
        for (Polygon polygon : polygonOptionsList) {
            polygon.setVisible(b);
        }
    }

    /**
     * 是否显示点
     *
     * @param b
     */
    private void showAllMarker(boolean b) {
        for (Marker marker : markerOptionsList) {
            marker.setVisible(b);
        }
    }

    /**
     * 通过aqi获取颜色值
     *
     * @param aqi
     * @return
     */
    private int getMyColor(int aqi) {
        if (aqi >= 0 && aqi <= 50) {
            return Color.argb(204, 3, 228, 6);
        } else if (aqi > 50 && aqi <= 100) {
            return Color.argb(204, 255, 255, 3);
        } else if (aqi > 100 && aqi <= 150) {
            return Color.argb(204, 253, 167, 3);
        } else if (aqi > 150 && aqi <= 200) {
            return Color.argb(204, 254, 3, 3);
        } else if (aqi > 200 && aqi <= 300) {
            return Color.argb(204, 131, 3, 134);
        } else {
            return Color.argb(204, 127, 6, 29);
        }
    }

    /**
     * 通过aqi值获取空气质量等级()
     *
     * @param aqi
     * @return
     */
    private AirRank getAirRank(int aqi) {
        if (aqi >= 0 && aqi <= 50) {
            return AirRank.AQI_0;
        } else if (aqi > 50 && aqi <= 100) {
            return AirRank.AQI_1;
        } else if (aqi > 100 && aqi <= 150) {
            return AirRank.AQI_2;
        } else if (aqi > 150 && aqi <= 200) {
            return AirRank.AQI_3;
        } else if (aqi > 200 && aqi <= 300) {
            return AirRank.AQI_4;
        } else {
            return AirRank.AQI_5;
        }
    }

    /**
     * 通过aqi获取图标
     *
     * @param aqi
     * @return
     */
    private Bitmap getIcon(int aqi) {
        AirRank airRank = getAirRank(aqi);
        Bitmap icon = null;
        switch (airRank) {
            case AQI_0:
                icon = resizeIcon(R.drawable.icon_arrow_air_0);
                break;
            case AQI_1:
                icon = resizeIcon(R.drawable.icon_arrow_air_1);
                break;
            case AQI_2:
                icon = resizeIcon(R.drawable.icon_arrow_air_2);
                break;
            case AQI_3:
                icon = resizeIcon(R.drawable.icon_arrow_air_3);
                break;
            case AQI_4:
                icon = resizeIcon(R.drawable.icon_arrow_air_4);
                break;
            default:
                icon = resizeIcon(R.drawable.icon_arrow_air_5);
                break;
        }

        //视图
        View view = getLayoutInflater().inflate(R.layout.mymarker, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.marker_image);
        imageView.setImageBitmap(icon);
        TextView textView = (TextView) view.findViewById(R.id.marker_text);
        textView.setText(aqi + "");

        return BitmapDescriptorFactory.fromView(view).getBitmap();
    }

    /**
     * 放大图标
     *
     * @param resid
     * @return
     */
    private Bitmap resizeIcon(int resid) {
        Bitmap icon = BitmapFactory.decodeResource(getResources(), resid);
        int width = (int) (icon.getWidth() * 2f);
        int height = (int) (icon.getHeight() * 2f);
        Bitmap resizeBitmap = Bitmap.createScaledBitmap(icon, width, height, false);
        return resizeBitmap;
    }

    /**
     * 高德地图镜头移动回调
     *
     * @param cameraPosition
     */
    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        float zoom = cameraPosition.zoom;
        if (zoom > MAP_INIT_ZOOM && isShowPoly) {
            isShowPoly = false;
            isShowMarker = true;
            showAllPoly(false);
            showAllMarker(true);
            PackLocalCity packLocalCity = ZtqCityDB.getInstance().getCityMain();
            if(packLocalCity.isFjCity) {
                showCityStations(packLocalCity.PARENT_ID);
            } else {
                showCityStations(packLocalCity.ID);
            }
        } else if (zoom <= MAP_INIT_ZOOM && isShowMarker) {
            isShowPoly = true;
            isShowMarker = false;
            showAllPoly(true);
            showAllMarker(false);
            hiddCityStations();
        }
    }

    /**
     * 高德地图镜头移动完成回调
     *
     * @param cameraPosition
     */
    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
    }

    /**
     * 空气质量枚举
     */
    enum AirRank {
        AQI_0(0),// 优
        AQI_1(1),// 良
        AQI_2(2),// 轻度污染
        AQI_3(3),// 中度污染
        AQI_4(4),// 重度污染
        AQI_5(5);// 严重污染

        private int _value;

        AirRank(int value) {
            _value = value;
        }

        public int value() {
            return _value;
        }
    }

    /**
     * 请求污染分布数据
     */
    private void reqAirRank() {
        moveCameraToCenter();
        mAMap.clear();
        rankUp = new PackAirRankNewUp();
        rankUp.rank_type = "aqi";
        PcsDataDownload.addDownload(rankUp);
    }

    /**
     * 将镜头移动至中心点
     */
    private void moveCameraToCenter() {
        if (mAMap != null) {
            mAMap.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(centerLatlng, 4.7f));
        }
    }

    /**
     * 处理空气质量分级
     *
     * @param airRankList
     */
    private void getAirRankCount(List<AirRankNew> airRankList) {
        int[] quality = new int[6];
        airRankColumnList = new ArrayList<>();
        for(int i = 0; i < 6; i++) {
            airRankColumnList.add(new ArrayList<AirRankNew>());
        }
        String[] rank = {"优", "良", "轻度污染", "中度污染", "重度污染", "严重污染"};
        for (AirRankNew airRankNew : airRankList) {
            int aqi = Integer.parseInt(airRankNew.num);
            AirRank airRank = getAirRank(aqi);
            int index = airRank.value();
            quality[index] += 1;
            airRankColumnList.get(index).add(airRankNew);
        }
        for (int i = 0; i < quality.length; i++) {
            airRankCountList[i] = rank[i] + "：" + quality[i] + "个";
        }
    }

    int count = 0;

    /**
     * 显示城市站点详情
     *
     * @param areaId
     */
    private void showCityStations(String areaId) {
        View view = findViewById(R.id.layout_station);
        view.setVisibility(View.VISIBLE);
        //上传包
        PackAirCityInfoUp packUp = new PackAirCityInfoUp();
        packUp.county_id = areaId;
        //刷新
        refreshCityStations(packUp.getName(), true);
        //请求
        PcsDataDownload.addDownload(packUp);
    }

    /**
     * 刷新城市站点详情
     *
     * @param key
     * @param firstClick
     */
    private void refreshCityStations(String key, boolean firstClick) {
        PackAirCityInfoDown packDown = (PackAirCityInfoDown) PcsDataManager.getInstance().getNetPack(key);
        TextView textTime = (TextView) findViewById(R.id.text_up_time);
        //城市名
        TextView textCity = (TextView) findViewById(R.id.text_city_aqi);
        textCity.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

        textCity.setText("");
        //更新时间
        textTime.setText("");
        TextView textTip = (TextView) findViewById(R.id.text_list_tip);
        if (packDown == null || packDown.list.size() == 0) {
            if (firstClick) {
                //请等待
                textTip.setText(R.string.please_wait);
            } else {
                //无数据
                textTip.setText(R.string.no_data);
            }
        } else {
            if (!TextUtils.isEmpty(packDown.update_time)) {
                textTime.setText(packDown.update_time);
            }

            textTip.setText(" ");
            textCity.setText(packDown.city_name + "空气质量指数（AQI）");
        }

        //列表
        mAdapterAirCityStation.setPack(packDown);
        mAdapterAirCityStation.notifyDataSetChanged();
    }

    /**
     * 隐藏城市站点详情
     */
    private void hiddCityStations() {
        View view = findViewById(R.id.layout_station);
        view.setVisibility(View.GONE);
    }

    @Override
    public void onDistrictSearched(DistrictResult districtResult) {

        if (++count >= airRankList.size()) {
            dismissProgressDialog();
        }
        Log.e("Tag", count++ + "");
        String keywords = districtResult.getQuery().getKeywords();
        if (districtResult == null || districtResult.getDistrict() == null) {
            return;
        }
        //通过ErrorCode判断是否成功
        if (districtResult.getAMapException() != null && districtResult.getAMapException().getErrorCode() == AMapException.CODE_AMAP_SUCCESS) {
            DistrictItem districtItem = null;
            try {
                districtItem = districtResult.getDistrict().get(0);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (districtItem == null) {
                return;
            }

            AirRankNew bean = searchAirRankByCityId(keywords);
            if (bean == null) {
                return;
            }
            String[] polyStr = districtItem.districtBoundary();
            LatLng center = new LatLng(Double.parseDouble(bean.lat), Double.parseDouble(bean.lon));
            DrawTask drawTask = new DrawTask();
            drawTask.executeOnExecutor(fixedThreadPool, polyStr, bean, center, bean.areaId, bean.city);
            drawTaskList.add(drawTask);
        } else {
//            if (districtResult.getAMapException() != null)
//                Toast.makeText(this, districtResult.getAMapException().getErrorCode(), Toast.LENGTH_LONG)
//                        .show();
        }
    }

    /**
     * 高德地图加载回调
     */
    @Override
    public void onMapLoaded() {
        MAP_INIT_ZOOM = mAMap.getCameraPosition().zoom;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what) {
                case WHAT_DISMISS:
                    dismissProgressDialog();
                    break;
            }
        }
    };

    private class MyReceiver extends PcsDataBrocastReceiver {

        @Override
        public void onReceive(String nameStr, String errorStr) {
            if (nameStr.equals(rankUp.getName())) {
                if (!air_pollution_distribution.isChecked()) {
                    return;
                }
                PackAirRankNewDown down = (PackAirRankNewDown) PcsDataManager.getInstance().getNetPack(nameStr);
                if (down == null) {
                    return;
                }
                // 初始化count值
                count = 0;
                showProgressDialog();
                airRankList.clear();
                airRankList.addAll(down.rank_list);
                mHandler.sendEmptyMessageDelayed(WHAT_DISMISS, 45 * 1000);
                SearchTask searchTask = new SearchTask();
                searchTask.executeOnExecutor(fixedThreadPool, airRankList);

                getAirRankCount(airRankList);
                adapter.notifyDataSetChanged();
                if(down.rank_list.size() > 0) {
                    // 显示时间
                    TextView textTime = (TextView) findViewById(R.id.tv_time);
                    textTime.setText(down.rank_list.get(0).pub_time);
                }
                // 显示下部窗口
                layoutRank.setVisibility(View.VISIBLE);
            } else if (nameStr.startsWith(PackAirCityInfoUp.KEY)) {
                //城市站点详情
                refreshCityStations(nameStr, false);
            }
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
            AirRankNew bean = (AirRankNew) params[1];
            LatLng centerLatlng = (LatLng) params[2];
            draw(polyStr, bean, centerLatlng);
            return null;
        }
    }

    public class SearchTask extends AsyncTask<List<AirRankNew>, Integer, Void> {

        @Override
        protected Void doInBackground(List<AirRankNew>... params) {

            for (AirRankNew bean : params[0]) {
                search(bean);
            }
            return null;
        }
    }

    private AMap.OnMarkerClickListener mOnMarker = new AMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            AirRankNew bean = (AirRankNew) marker.getObject();
            showCityStations(bean.areaId);
            return false;
        }
    };
}
