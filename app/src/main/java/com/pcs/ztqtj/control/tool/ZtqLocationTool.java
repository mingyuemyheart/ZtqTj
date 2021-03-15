package com.pcs.ztqtj.control.tool;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.geocoder.StreetNumber;
import com.pcs.ztqtj.control.listener.PcsOnGeocodeSearchListener;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.lib.lib_pcs_v3.control.log.Log;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCity;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityLocation;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityMain;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalLocationSet;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalTestLocation;

import java.util.HashMap;
import java.util.Map;

/**
 * 定位工具类
 *
 * @author JiangZy
 */
public class ZtqLocationTool {
    private static ZtqLocationTool instance = null;
    // 定位间隔
    private final long LOCATION_TIME = 60 * 1000 * 3;
    // 定位信息
    private AMapLocation mAMapLocation = null;
    //是否暂停
    private boolean mIsPaused = false;
    //定位选项
    private AMapLocationClientOption mOption = new AMapLocationClientOption();
    // 定位客户端
    private AMapLocationClient mAMapLocationClient = null;
    // 监听列表
    private Map<Integer, PcsLocationListener> mListenerMap = new HashMap<Integer, PcsLocationListener>();
    // 地理编码搜索
    private GeocodeSearch mGeocodeSearch;
    // 地理编码搜索地址
    private RegeocodeAddress mRegeocodeAddress;
    // 搜索的坐标
    private LatLng mSearchLatLng = null;
    // 结果的坐标
    private LatLng mResultLatLng = null;
    // 逆地理编码返回的响应码(V3.2.1之后搜索成功响应码为1000，之前版本为0)
    public static final int RCODE = 1000;
    private final String[] SHList = {"黄浦区","徐汇区","长宁区","静安区","普陀区","虹口区","杨浦区"};

    public static ZtqLocationTool getInstance() {
        if (instance == null) {
            instance = new ZtqLocationTool();
        }

        return instance;
    }

    private ZtqLocationTool() {

    }

    /**
     * 设置暂停
     *
     * @param isPaused
     */
    public void setPause(boolean isPaused) {
        mIsPaused = isPaused;
        if (!isPaused) {
            startLocation();
            refreshSearch();
        }
    }

    /**
     * 开始定位
     */
    public void beginLocation(Context appContext) {
        if (mAMapLocationClient == null) {
            mAMapLocationClient = new AMapLocationClient(appContext);
        }

        // 高精度
        mOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
        // 单次定位
        mOption.setOnceLocation(true);
        // 间隔
        mOption.setInterval(LOCATION_TIME);

        // 监听
        mAMapLocationClient.setLocationListener(mListener);
        // 选项
        mAMapLocationClient.setLocationOption(mOption);
        //清空定位
        mAMapLocation = null;
        // 地理信息搜索
        mGeocodeSearch = new GeocodeSearch(appContext);
        mGeocodeSearch.setOnGeocodeSearchListener(mGeocodeSearchListener);
        startLocation();
        // 刷新定位搜索
        refreshSearch();
        mHandlerRefresh.sendEmptyMessageDelayed(0, LOCATION_TIME);
    }

    private Handler mHandlerRefresh = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            startLocation();
            refreshSearch();
            mHandlerRefresh.sendEmptyMessageDelayed(0, LOCATION_TIME);
        }
    };

    private void startLocation() {
        if(mAMapLocationClient != null) {
            mAMapLocationClient.startLocation();
        }
    }

    /**
     * 获取搜索的坐标
     *
     * @return
     */
    public LatLng getLatLng() {
        return mResultLatLng;
    }

    /**
     * 获取搜索地址
     *
     * @return
     */
    public RegeocodeAddress getSearchAddress() {
        return mRegeocodeAddress;
    }

    /**
     * 获取定位到的城市
     *
     * @return
     */
    public PackLocalCityLocation getLocationCity() {
        PackLocalCityLocation packLocation = (PackLocalCityLocation) PcsDataManager.getInstance().getLocalPack(PackLocalCityLocation.KEY);
        if (packLocation != null) {
            return packLocation;
        }
        packLocation = new PackLocalCityLocation();
        packLocation.NAME = PackLocalCityLocation.LOCATING;
        PcsDataManager.getInstance().saveLocalData(PackLocalCityLocation.KEY, packLocation);
        return packLocation;
    }

    /**
     * 设置是否自动定位
     *
     * @param b
     */
    public void setIsAutoLocation(boolean b) {
        PackLocalLocationSet packSet = new PackLocalLocationSet();
        packSet.isAutoLocation = b;

        PcsDataManager.getInstance().saveLocalData(PackLocalLocationSet.KEY, packSet);
    }

    /**
     * 获取是否自动定位
     *
     * @return
     */
    public boolean getIsAutoLocation() {
        PackLocalLocationSet packSet = (PackLocalLocationSet) PcsDataManager.getInstance().getLocalPack
                (PackLocalLocationSet.KEY);
        if (packSet == null) {
            packSet = new PackLocalLocationSet();
            PcsDataManager.getInstance().saveLocalData(PackLocalLocationSet.KEY, packSet);
        }

        return packSet.isAutoLocation;
    }

    /**
     * 虚拟定位（测试用）
     */
    private void virtualLocation() {
        PackLocalTestLocation pack = (PackLocalTestLocation) PcsDataManager.getInstance().getLocalPack
                (PackLocalTestLocation.KEY);
        if (pack == null) {
            return;
        }

        mAMapLocation = new AMapLocation("test");
        mAMapLocation.setLatitude(pack.latitude);
        mAMapLocation.setLongitude(pack.longitude);
        mAMapLocation.setProvince(pack.province);
        mAMapLocation.setCity(pack.city);
        mAMapLocation.setDistrict(pack.district);



    }

    private AMapLocation getTestLocation() {
        AMapLocation aMapLocation = new AMapLocation("test");
        aMapLocation.setLatitude(39.085100);
        aMapLocation.setLongitude(117.199370);
        aMapLocation.setProvince("天津市");
        aMapLocation.setCity("天津市");
        aMapLocation.setDistrict("天津市");
        return aMapLocation;
    }

    /**
     * 刷新定位搜索
     */
    public void refreshSearch() {
        //虚拟定位（测试用）
        //virtualLocation();
        if (mAMapLocation == null) {
            return;
        }

        //mAMapLocation = getTestLocation();

        mSearchLatLng = new LatLng(mAMapLocation.getLatitude(), mAMapLocation.getLongitude());
        if (mResultLatLng == null) {
            mResultLatLng = mSearchLatLng;
        }
        // 搜索地理信息
        LatLonPoint point = new LatLonPoint(mAMapLocation.getLatitude(), mAMapLocation.getLongitude());
        // 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        RegeocodeQuery query = new RegeocodeQuery(point, 200, GeocodeSearch.AMAP);
        // 设置同步逆地理编码请求
        mGeocodeSearch.getFromLocationAsyn(query);
    }

    /**
     * 刷新定位城市信息
     *
     * @param refreshMain 强制刷新首页？
     */
    public void refreshLocationCity(boolean refreshMain) {

        Log.e("jzy", "refreshLocationCity");
        String province = "";
        String city = "";
        String district = "";
        PackLocalCityLocation packLocation = getLocationCity();
        PackLocalCityMain packMain = ZtqCityDB.getInstance().getCityMain();
        if (mRegeocodeAddress != null &&
                !TextUtils.isEmpty(mRegeocodeAddress.getProvince()) &&
                !TextUtils.isEmpty(mRegeocodeAddress.getCity()) &&
                !TextUtils.isEmpty(mRegeocodeAddress.getDistrict())) {
            //使用RegeocodeAddress的城市信息
            province = mRegeocodeAddress.getProvince();
            city = mRegeocodeAddress.getCity();
            district = mRegeocodeAddress.getDistrict();
        } else if (mAMapLocation != null && !TextUtils.isEmpty(mAMapLocation.getProvince())) {
            //使用AMapLocation的城市信息
            province = mAMapLocation.getProvince();
            city = mAMapLocation.getCity();
            district = mAMapLocation.getDistrict();
        } else if (getIsAutoLocation()) {
            if (!TextUtils.isEmpty(packLocation.ID))
                //使用之前保存的定位城市
                saveCity(packLocation);
            AutoDownloadWeather.getInstance().addWeekCity(packLocation);
            return;
        } else if (!getIsAutoLocation()) {
            //使用之前保存的城市
            saveCity(packMain);
            AutoDownloadWeather.getInstance().addWeekCity(packMain);
        }

        PackLocalCity packCity;
        if(province.contains("天津")) {
            packCity = getCitySh(province, city, district);
        } else {
            packCity = getCityFamily(province, city, district);
        }
        if (packCity != null) {
            AutoDownloadWeather.getInstance().addWeekCity(packCity);
            //保存定位城市
            packLocation.copyCity(packCity);
            PcsDataManager.getInstance().saveLocalData(PackLocalCityLocation.KEY, packLocation);
            //保存首页城市(顺序放最后)
            if (getIsAutoLocation() && (refreshMain || packMain == null || packMain.isLocationCity)) {
                // 首页城市
                ZtqCityDB.getInstance().setCityMain(packCity, true);
            }
            // 下载数据
            AutoDownloadWeather.getInstance().setDefaultCity(packCity);
            AutoDownloadWeather.getInstance().beginMainData();
        }
    }

    /**
     * 取亲情城市
     *
     * @return
     */
    private PackLocalCity getCityFamily(String province, String city, String district) {
        // 区县
        PackLocalCity packCity = ZtqCityDB.getInstance().getAllCityByName(province,
                district);
        if (packCity == null) {
            // 城市
            packCity = ZtqCityDB.getInstance().getAllCityByName(province, city);
        }
        return packCity;
    }

    private PackLocalCity getCitySh(String province, String city, String district) {
        for (String name : SHList) {
            if (district.contains(name)) {
                return ZtqCityDB.getInstance().getSHCity();
            }
        }
        return getCityFamily(province, city, district);
    }

    /**
     * 保存城市
     *
     * @param packCity
     */
    private void saveCity(PackLocalCity packCity) {
        // 首页城市
        ZtqCityDB.getInstance().setCityMain(packCity, true);
        // 下载数据
        AutoDownloadWeather.getInstance().setDefaultCity(packCity);
        AutoDownloadWeather.getInstance().beginMainData();
    }

    private AMapLocationListener mListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation location) {
            if (location == null || location.getLatitude() < 1 && location.getLongitude() < 1) {
                return;
            }
            mAMapLocation = location;
            //mAMapLocation = getTestLocation();
            if (mIsPaused) {
                return;
            }
            //刷新搜索
            refreshSearch();
//            if (mAMapLocation == null) {
//                //刷新城市
//                refreshLocationCity(true);
//            } else {
//                refreshLocationCity(false);
//            }
            // 监听
            for (Integer key : mListenerMap.keySet()) {
                PcsLocationListener listener = mListenerMap.get(key);
                if (listener != null) {
                    listener.onLocationChanged();
                }
            }
        }
    };

    /**
     * 添加监听
     *
     * @param listener
     */
    public void addListener(PcsLocationListener listener) {
        mListenerMap.put(listener.hashCode(), listener);
    }

    /**
     * 删除监听
     *
     * @param listener
     */
    public void removeListener(PcsLocationListener listener) {
        mListenerMap.remove(listener.hashCode());
    }

    /**
     * 定位改变监听
     *
     * @author JiangZy
     */
    public interface PcsLocationListener {
        void onLocationChanged();
    }

    /**
     * 地理编码搜索监听
     */
    private final PcsOnGeocodeSearchListener mGeocodeSearchListener = new PcsOnGeocodeSearchListener() {

        @Override
        public void onGeocodeSearched(GeocodeResult result, int rCode) {
            super.onGeocodeSearched(result, rCode);
        }

        @Override
        public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
            super.onRegeocodeSearched(result, rCode);
            if (rCode != ZtqLocationTool.RCODE || result == null
                    || result.getRegeocodeAddress() == null
                    || result.getRegeocodeAddress().getFormatAddress() == null) {
                return;
            }

            mRegeocodeAddress = result.getRegeocodeAddress();
            mResultLatLng = mSearchLatLng;
            // 格式化地址
            String tempAddr = mRegeocodeAddress.getFormatAddress();
            String district = mRegeocodeAddress.getDistrict();
            String city = mRegeocodeAddress.getCity();
            if (!TextUtils.isEmpty(district) && tempAddr.indexOf(district) > -1) {
                tempAddr = tempAddr.substring(tempAddr.indexOf(district)
                        + district.length());
            } else if (!TextUtils.isEmpty(city) && tempAddr.indexOf(city) > -1) {
                tempAddr = tempAddr.substring(tempAddr.indexOf(city)
                        + district.length());
            }
            mRegeocodeAddress.setFormatAddress(tempAddr);
            // 刷新定位到的城市
            refreshLocationCity(false);
            // 监听
            for (Integer key : mListenerMap.keySet()) {
                PcsLocationListener listener = mListenerMap.get(key);
                if (listener != null) {
                    listener.onLocationChanged();
                }
            }
        }
    };


    public String getLocationName() {
        String locationName = "";
        if (mAMapLocation != null && !TextUtils.isEmpty(mAMapLocation.getProvince())) {
            //使用AMapLocation的城市信息
            mAMapLocation.getCountry();//国家名称
            locationName += "province =" + mAMapLocation.getProvince();//获取省的名称
            locationName += "  city = " + mAMapLocation.getCity();//获取城市名称
            locationName += "  district =" + mAMapLocation.getDistrict();//获取区的名称
            locationName += "  street =" + mAMapLocation.getStreet();//获取街道名称
        }
        if (mRegeocodeAddress != null) {
            locationName += "mRegeocodeAddress-->";

            locationName += "province =" + mRegeocodeAddress.getProvince();

            locationName += "  city = " + mRegeocodeAddress.getCity();
            locationName += "  district =" + mRegeocodeAddress.getDistrict();
            StreetNumber streetNumber = mRegeocodeAddress.getStreetNumber();
            locationName += "  street =" + streetNumber.getStreet();
            locationName += "  Township =" + mRegeocodeAddress.getTownship();
        }
        return locationName;
    }
}
