package com.pcs.ztqtj.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;

import com.pcs.lib.lib_pcs_v3.control.log.Log;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCity;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityInfo;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityMain;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalFamilyCityInfo;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalStation;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalTravelViewInfo;
import com.pcs.lib_ztqfj_v2.model.pack.net.citylist.AroundCityBean;
import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.control.ControlAppInit;
import com.pcs.ztqtj.control.inter.Callback;
import com.pcs.ztqtj.control.observables.CityListObservable;
import com.pcs.ztqtj.control.observables.CityListObservable.CityListCallback;
import com.pcs.ztqtj.control.observables.CityListObservable.FileEmitterValue;
import com.pcs.ztqtj.control.observables.CityListObservable.FileType;
import com.pcs.ztqtj.control.tool.ZtqAppWidget;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 城市数据
 */
@SuppressLint("DefaultLocale")
public class ZtqCityDB {

    private static ZtqCityDB instance = null;
    private Context mContext;

    // 全国旅游景点
    private List<PackLocalCity> Tj_landscapeList = new ArrayList<>();
    // 全国省份
    private List<PackLocalCity> Tj_Province = new ArrayList<>();
    // 全国城市，到区县级
    private List<PackLocalCity> Tj_Qgdisty = new ArrayList<>();
    // 天津室内区域
    private List<PackLocalCity> Tj_citys = new ArrayList<>();
    // 天津及周边城市
    private List<AroundCityBean> around_area = new ArrayList<>();
    // 天津自动站，所有
    private List<PackLocalStation> Tj_auto = new ArrayList<>();
    // 天津自动站，is_base为1的站点
    private List<PackLocalStation> baseTj_auto = new ArrayList<>();
    // 全国所有地市、县级市、区域
    private List<PackLocalStation> Tj_Qgstations = new ArrayList<>();


    
    // 当前城市
    private PackLocalCityInfo mCityInfo = null;
    // 首页显示的城市
    private PackLocalCityMain mCityMain = null;
    // 当前亲情城市
    private PackLocalFamilyCityInfo mFamilyCityInfo = null;
    // 当前旅游景点
    private PackLocalTravelViewInfo mTravelViewInfo = null;

    private ZtqCityDB() {

    }

    public static ZtqCityDB getInstance() {
        if (instance == null) {
            instance = new ZtqCityDB();
        }
        return instance;
    }

    /**
     * 初始化
     * @param context
     */
    public void init(Context context) {
        mContext = context;
    }

    long start;

    public void initnew(final Context context, final Callback callback) {
        mContext = context;
        start = System.currentTimeMillis();
        CityListObservable observable = new CityListObservable(context);
        observable.setCityListCallback(new CityListCallback() {
            @Override
            public void onNext(List<FileEmitterValue> list) {
                for(FileEmitterValue value : list) {
                    parseCityData(value);
                }
                Log.e("ZtqCityDB", String.valueOf(System.currentTimeMillis() - start));
            }

            @Override
            public void onComplete() {
                ControlAppInit.getInstance().reqInit(context, callback);
            }
        });
        observable.execute();
    }

    public void parseCityData(FileEmitterValue value) {
        FileType type = value.getType();
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(value.getString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        switch (type) {
            case Tj_landscapeList:// 旅游景点，景点名称
                if (jsonObject == null) {
                    try {
                        jsonObject = new JSONObject(CityDBParseTool.getStringFromAssets(mContext, "Tj_landscapeList.json"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Tj_landscapeList = CityDBParseTool.parseCityList(jsonObject);
                break;
            case tj_City://全国地级市，地市名称
//                if (jsonObject == null) {
//                    try {
//                        jsonObject = new JSONObject(CityDBParseTool.getStringFromAssets(mContext, "tj_City.json"));
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }

                break;
            case Tj_Province:// 所有省份，省份名称
                if (jsonObject == null) {
                    try {
                        jsonObject = new JSONObject(CityDBParseTool.getStringFromAssets(mContext, "Tj_Province.json"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Tj_Province = CityDBParseTool.getProvinceList(jsonObject);
                break;
            case Tj_Qgdisty:// 全国城市，到区县级
                if (jsonObject == null) {
                    try {
                        jsonObject = new JSONObject(CityDBParseTool.getStringFromAssets(mContext, "Tj_Qgdisty.json"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Tj_Qgdisty = CityDBParseTool.parseCityList(jsonObject);
                Tj_citys = CityDBParseTool.parseTJCity(Tj_Qgdisty);
                break;
            case around_area:// 天津周边城市及天津内区域
                if (jsonObject == null) {
                    try {
                        jsonObject = new JSONObject(CityDBParseTool.getStringFromAssets(mContext, "around_area.json"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                around_area = CityDBParseTool.parseAroundCity(jsonObject);
                break;
            case Tj_auto:// 天津自动站
                if (jsonObject == null) {
                    try {
                        jsonObject = new JSONObject(CityDBParseTool.getStringFromAssets(mContext, "Tj_auto.json"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Tj_auto = CityDBParseTool.parseStationList(jsonObject);
                baseTj_auto = CityDBParseTool.parseBaseStationList(Tj_auto);
                break;
            case Tj_Qgstations:// 全国所有地市、县级市、区域
                if (jsonObject == null) {
                    try {
                        jsonObject = new JSONObject(CityDBParseTool.getStringFromAssets(mContext, "Tj_Qgstations.json"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Tj_Qgstations = CityDBParseTool.parseStationList(jsonObject);
                break;
            default:
        }
    }

    /**
     * 省内基本站
     * @return
     */
    public List<PackLocalStation> getBaseStationList() {
        if(baseTj_auto == null) return null;
        return new ArrayList<>(baseTj_auto);
    }

    /**
     * 全国站
     * @return
     */
    public List<PackLocalStation> getNationStationList() {
        if(Tj_Qgstations == null) return null;
        return new ArrayList<>(Tj_Qgstations);
    }

    /**
     * 天津及周边城市
     * @return
     */
    public List<AroundCityBean> getAroundAreas() {
        if(around_area == null) return null;
        return new ArrayList<>(around_area);
    }

    /*获取全国城市parent_id相同的城市*/
    public List<PackLocalCity> getCountryCityList(String cityId) {
        List<PackLocalCity> listChilCityInfo = new ArrayList<>();
        if (Tj_Qgdisty == null) {
            return listChilCityInfo;
        }
        if (TextUtils.isEmpty(cityId)) {
            return listChilCityInfo;
        }
        for (int i = 0; i < Tj_Qgdisty.size(); i++) {
            PackLocalCity cityInfo = Tj_Qgdisty.get(i);
            if (cityInfo.PARENT_ID.equals(cityId)) {
                PackLocalCity cTemp = new PackLocalCity();
                cTemp.copyCity(cityInfo);
                listChilCityInfo.add(cTemp);
            }
        }
        return listChilCityInfo;
    }

    /**
     * 外省第三级城市通过省ID和二级城市名获取二级城市
     * @param parendId
     * @param name
     * @return
     */
    public PackLocalCity getCityByNameAndParentId(String parendId, String name) {
        if (Tj_Qgdisty == null) {
            return null;
        }
        if (TextUtils.isEmpty(parendId) || TextUtils.isEmpty(name)) {
            return null;
        }
        for(int i = 0; i < Tj_Qgdisty.size(); i++) {
            PackLocalCity cityInfo = Tj_Qgdisty.get(i);
            if (cityInfo.PARENT_ID.equals(parendId) && cityInfo.NAME.equals(name)) {
                PackLocalCity cityInfoTemp = new PackLocalCity();
                cityInfoTemp.copyCity(cityInfo);
                return cityInfoTemp;
            }
        }
        return null;
    }


    /*根据id从全国城市获取城市*/
    public PackLocalCity getCountryCity(String cityId) {
        if (Tj_Qgdisty == null) {
            return null;
        }
        if (TextUtils.isEmpty(cityId)) {
            return null;
        }
        for (int i = 0; i < Tj_Qgdisty.size(); i++) {
            PackLocalCity cityInfo = Tj_Qgdisty.get(i);
            if (cityInfo.ID.equals(cityId)) {
                PackLocalCity cityInfoTemp = new PackLocalCity();
                cityInfoTemp.copyCity(cityInfo);
                return cityInfoTemp;
            }
        }
        return null;
    }

    /**
     * 从全国城市获取城市信息
     */
    public PackLocalCity getCityInfoInAllCity(String cityName) {
        if (Tj_Qgdisty == null) {
            return null;
        }
        if (TextUtils.isEmpty(cityName)) {
            return null;
        }
        String matchingStr = cityName;
        // 县市区
        if (matchingStr.endsWith("县") || matchingStr.endsWith("市") || matchingStr.endsWith("区")) {
            matchingStr = cityName.substring(0, cityName.length() - 1);
        }
        PackLocalCity cityInfoTemp = null;
        for (int i = 0; i < Tj_Qgdisty.size(); i++) {
            PackLocalCity cityInfo = Tj_Qgdisty.get(i);
            if (cityInfo.NAME.startsWith(matchingStr)) {
                cityInfoTemp = new PackLocalCity();
                cityInfoTemp.copyCity(cityInfo);
                break;
            }
        }
        return cityInfoTemp;
    }

    /**
     * 初始化景点列表
     * @param context
     */
    public void initTravels(Context context) {
        this.mContext = context;
        // 所有景点
        if (Tj_landscapeList.size() > 0) {
            return;
        }
        mTravelViewInfo = (PackLocalTravelViewInfo) PcsDataManager.getInstance().getLocalPack(PackLocalTravelViewInfo.KEY);
    }

    /**
     * 获取等级1城市列表
     * @return
     */
    public final List<PackLocalCity> getCityLv1() {
        List<PackLocalCity> Tj_citysTemp = new ArrayList<>();

        for (int i = 0; i < Tj_Province.size(); i++) {
            PackLocalCity tjInfo = Tj_Province.get(i);
            tjInfo.PARENT_ID = tjInfo.ID;
            if (tjInfo.ID.equals("10103")) {
                Tj_citysTemp.add(tjInfo);
                break;
            }
        }

        Tj_citysTemp.addAll(Tj_citys);
        return Tj_citysTemp;
    }

    public final List<PackLocalCity> getCityLv1WithoutTJ() {
        List<PackLocalCity> result = new ArrayList<>(Tj_citys);
        int index = -1;
        for(PackLocalCity city : result) {
            if(city.ID.equals("25183")) {
                index = result.indexOf(city);
                break;
            }
        }
        if(index != -1) {
            result.remove(index);
        }
        return result;
    }

    public final List<PackLocalStation> getAllStationList() {
        return new ArrayList<>(Tj_auto);
    }

    /**
     * 通过自动站名称判断该自动站是否为天津的自动站
     * @param name
     * @return
     */
    public boolean getStationIsTjByName(String name) {
        if(TextUtils.isEmpty(name)) return false;
        PackLocalStation station = getStation(name);
        return station != null && !TextUtils.isEmpty(station.AREA);
    }

    public final List<PackLocalStation> getStationListByArea(String area) {
        ArrayList<PackLocalStation> result = new ArrayList<>();
        for(PackLocalStation station : Tj_auto) {
            if(station.AREA.equals(area)) {
                result.add(station);
            }
        }
        Collections.sort(result, new Comparator<PackLocalStation>() {
            @Override
            public int compare(PackLocalStation o1, PackLocalStation o2) {
                if(o1.IS_BASE.equals("1")) {
                    return -1;
                } else if (o2.IS_BASE.equals("1")) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
        return result;
    }

    /**
     * 输入城市ID称筛选城市信息
     * @param cityId
     * @return PackLocalCity；不存在则返回null
     */
    public final PackLocalCity getCityInfo1_ID(String cityId) {
        // 三级
        // if (mListCity_3 == null) {
        // mListCity_3 = pareseCityXml(context, R.raw.city_lv3);
        // }
        for (int i = 0; i < Tj_citys.size(); i++) {
            PackLocalCity pack = Tj_citys.get(i);
            if (cityId.contains(pack.ID)) {
                PackLocalCity packTemp = new PackLocalCity();
                packTemp.copyCity(pack);
                return packTemp;
            }
        }
        return null;
    }

    /**
     * 输入城市名称筛选城市信息
     * @param cityname
     * @return PackLocalCity；不存在则返回null
     */
    public final PackLocalCity getCityInfo1_Name(String cityname) {
        if (Tj_citys == null) {
            return null;
        }
        for (PackLocalCity bean : Tj_citys) {
            if (TextUtils.isEmpty(cityname) || bean == null) {
                return null;
            }
            if (cityname.equals(bean.NAME)) {
                PackLocalCity cTemp = new PackLocalCity();
                cTemp.copyCity(bean);

                return cTemp;
            }
        }
        return null;
    }

    public PackLocalCity getSHCity() {
        for(PackLocalCity city : Tj_citys) {
            if(city.ID.equals("72892")) {
                return city;
            }
        }
        return null;
    }

    /**
     * 是否纯英文
     * @param s
     * @return
     */
    public boolean isEn(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (!(s.charAt(i) >= 'A' && s.charAt(i) <= 'Z') && !(s.charAt(i) >= 'a' && s.charAt(i) <= 'z')) {
                return false;
            }
        }
        return true;
    }

    /**
     * 设置首页城市信息
     * <p>
     * PackLocalCity cityMain
     */
    public void setCityMain(PackLocalCity cityMain, boolean isLocationCity) {
//        PackLocalCity city = getCity3ById(cityId);
//        if (city == null) {
//            return;
//        }
        if (mCityMain == null) {
            mCityMain = new PackLocalCityMain();
        }
        mCityMain.copyCity(cityMain);
        mCityMain.isLocationCity = isLocationCity;
        PcsDataManager.getInstance().saveLocalData(PackLocalCityMain.KEY, mCityMain);
        // 更新小部件信息
        if (mContext != null) {
            ZtqAppWidget.getInstance().updateAllWidget(mContext);
        }
    }

    public void removeCityMain() {
        mCityMain = null;
        PcsDataManager.getInstance().removeLocalData(PackLocalCityMain.KEY);
    }

    /**
     * 获取首页城市信息
     * @return
     */
    public PackLocalCityMain getCityMain() {
        if (mCityMain != null) {
            return mCityMain;
        }
        mCityMain = (PackLocalCityMain) PcsDataManager.getInstance().getLocalPack(PackLocalCityMain.KEY);
        return mCityMain;
    }

    /**
     * 设置当前城市信息
     * @param pack
     */
    public void setCurrentCityInfo(PackLocalCityInfo pack) {
        mCityInfo = pack;
        PcsDataManager.getInstance().saveLocalData(PackLocalCityInfo.KEY, pack);
    }

    /**
     * 获取当前城市信息
     * @return
     */
    public PackLocalCityInfo getCurrentCityInfo() {
        if (mCityInfo == null) {
            mCityInfo = (PackLocalCityInfo) PcsDataManager.getInstance().getLocalPack(PackLocalCityInfo.KEY);
        }
        if (mCityInfo == null) {
            mCityInfo = new PackLocalCityInfo();
        }
        return mCityInfo;
    }

    /**
     * 设置当前亲情城市信息
     * @param pack
     */
    public void setCurrentFamilyCityInfo(PackLocalFamilyCityInfo pack) {
        mFamilyCityInfo = pack;
        PcsDataManager.getInstance().saveLocalData(PackLocalFamilyCityInfo.KEY, pack);
    }

    /**
     * 获取当前亲情城市信息
     * @return
     */
    public PackLocalFamilyCityInfo getCurrentFamilyCityInfo() {
        if (mFamilyCityInfo == null) {
            mFamilyCityInfo = (PackLocalFamilyCityInfo) PcsDataManager.getInstance().getLocalPack(PackLocalFamilyCityInfo.KEY);
        }
        if (mFamilyCityInfo == null) {
            mFamilyCityInfo = new PackLocalFamilyCityInfo();
        }
        return mFamilyCityInfo;
    }

    /**
     * 设置当前景点信息
     * @param pack
     */
    public void setCurrentTravelViewInfo(PackLocalTravelViewInfo pack) {
        mTravelViewInfo = pack;
        PcsDataManager.getInstance().saveLocalData(PackLocalTravelViewInfo.KEY, pack);
    }

    /**
     * 获取当前景点信息
     * @return
     */
    public PackLocalTravelViewInfo getCurrentTravelViewInfo() {
        if (mTravelViewInfo == null) {
            mTravelViewInfo = (PackLocalTravelViewInfo) PcsDataManager.getInstance().getLocalPack(PackLocalTravelViewInfo.KEY);
            if (mTravelViewInfo != null) {
                if (mTravelViewInfo.localViewList.size() > 0) {
                    mTravelViewInfo.currentTravelView = mTravelViewInfo.localViewList.get(mTravelViewInfo.defaultPosition);
                }
            } else {
                mTravelViewInfo = new PackLocalTravelViewInfo();
            }
        }
        return mTravelViewInfo;
    }

    /**
     * 是否登录气象服务
     * @return
     */
    public boolean isLoginService() {
        if (!TextUtils.isEmpty(MyApplication.UID) && !TextUtils.equals(MyApplication.UID, MyApplication.offline)) {
            return true;
        }
        return false;
    }

    /**
     * 获取所有的景点
     * @return
     */
    public List<PackLocalCity> getAllViewsInfo() {
        List<PackLocalCity> cityList = new ArrayList<>();
        for (PackLocalCity pack : Tj_landscapeList) {
            PackLocalCity cTemp = new PackLocalCity();
            cTemp.copyCity(pack);
            cityList.add(cTemp);
        }
        return cityList;
    }

    /**
     * 获取省份列表
     * @return
     */
    public List<PackLocalCity> getProvincesList() {
        return new ArrayList<>(Tj_Province);
    }

    /**
     * 获取风雨查询省份列表
     * @return
     */
    public List<PackLocalCity> getProvincesLiveQueryList() {
        return new ArrayList<>(Tj_Province);
    }

    public PackLocalCity getProvinceByName(String name) {
        for (PackLocalCity pack : Tj_Province) {
            if (pack.NAME.toLowerCase().contains(name)) {
                PackLocalCity cTemp = new PackLocalCity();
                cTemp.copyCity(pack);
                return cTemp;
            }
        }
        return null;
    }

    /**
     * 搜索省份
     * @param key
     * @return
     */
    public List<PackLocalCity> searchProvince(String key) {
        String low = key.toLowerCase();
        List<PackLocalCity> resultList = new ArrayList<>();
        for (PackLocalCity pack : Tj_Province) {
            if (pack.NAME.toLowerCase().contains(low)) {
                resultList.add(pack);
            } else if (pack.PINGYIN.toLowerCase().startsWith(low)) {
                resultList.add(pack);
            } else if (pack.PY.toLowerCase().startsWith(low)) {
                resultList.add(pack);
            }
        }
        return resultList;
    }

    /**
     * 搜索城市
     * @param key
     * @return
     */
    public List<PackLocalCity> searchCity(String key) {
        String low = key.toLowerCase();
        List<PackLocalCity> resultList = new ArrayList<>();
        for (PackLocalCity pack : Tj_Qgdisty) {
            if (pack.NAME.toLowerCase().contains(low)) {
                resultList.add(pack);
            } else if (pack.PINGYIN.toLowerCase().contains(low)) {
                resultList.add(pack);
            } else if (pack.PY.toLowerCase().contains(low)) {
                resultList.add(pack);
            }
        }
        return resultList;
    }

    public List<PackLocalCity> searchCountry(String key) {
        String low = key.toLowerCase();
        List<PackLocalCity> resultList = new ArrayList<>();
        for (PackLocalCity pack : Tj_Qgdisty) {
            if (pack.NAME.toLowerCase().contains(low)) {
                resultList.add(pack);
            } else if (pack.PINGYIN.toLowerCase().contains(low)) {
                resultList.add(pack);
            } else if (pack.PY.toLowerCase().contains(low)) {
                resultList.add(pack);
            }
        }
        return resultList;
    }

    //精确搜索城市
    public List<PackLocalCity> searchCityConfirm(String key){
        String low = key.toLowerCase();
        List<PackLocalCity> resultList = new ArrayList<>();
        for (PackLocalCity pack : Tj_Qgdisty) {
            if (pack.NAME.toLowerCase().equals(low)) {
                resultList.add(pack);
            } else if (pack.PINGYIN.toLowerCase().equals(low)) {
                resultList.add(pack);
            } else if (pack.PY.toLowerCase().equals(low)) {
                resultList.add(pack);
            }
        }
        return resultList;
    }

    /**
     * 搜索景点
     * @param key
     * @return
     */
    public List<PackLocalCity> searchTrave(String key) {
        String low = key.toLowerCase();
        List<PackLocalCity> resultList = new ArrayList<>();
        for (PackLocalCity pack : Tj_landscapeList) {
            if (pack.NAME.toLowerCase().contains(low)) {
                resultList.add(pack);
            } else if (pack.PINGYIN.toLowerCase().contains(low)) {
                resultList.add(pack);
            } else if (pack.PY.toLowerCase().contains(low)) {
                resultList.add(pack);
            }
        }
        return resultList;
    }

    /**
     * 获取单个省份
     * @param id
     * @return
     */
    public PackLocalCity getProvinceById(String id) {
        for (PackLocalCity pack : Tj_Province) {
            if (pack.ID.equals(id)) {
                PackLocalCity cTemp = new PackLocalCity();
                cTemp.copyCity(pack);
                return cTemp;
            }
        }
        return null;
    }

    /**
     * 获取所有城市列表
     * @return
     */
    public List<PackLocalCity> getAllCityInfos() {
        return Tj_Qgdisty;
    }

    public List<PackLocalCity> getViewsByProcinceID(String provinceID) {
        List<PackLocalCity> citys = new ArrayList<>();
        for (PackLocalCity c : Tj_landscapeList) {
            if (c.PARENT_ID.equals(provinceID)) {
                PackLocalCity cTemp = new PackLocalCity();
                cTemp.copyCity(c);
                citys.add(cTemp);
            }
        }
        return citys;
    }

    public List<PackLocalCity> getCityByProcinceID(String provinceID) {
        List<PackLocalCity> citys = new ArrayList<>();
        for (PackLocalCity c : Tj_Qgdisty) {
            if (c.PARENT_ID.equals(provinceID)) {
                PackLocalCity cTemp = new PackLocalCity();
                cTemp.copyCity(c);
                citys.add(cTemp);
            }
        }
        return citys;
    }

    /**
     * 获取全国城市
     * @param provinceName
     * @param cityName
     * @return
     */
    public PackLocalCity getAllCityByName(String provinceName, String cityName) {
        if (TextUtils.isEmpty(cityName)) {
            return null;
        }
        for (PackLocalCity c : Tj_Qgdisty) {
            if (cityName.contains(c.NAME) || c.NAME.contains(cityName)) {
                // 查询省份
                for (PackLocalCity p : Tj_Province) {
                    if ((provinceName.contains(p.NAME) || p.NAME.contains(provinceName)) && c.PARENT_ID.equals(p.ID)) {
                        PackLocalCity cTemp = new PackLocalCity();
                        cTemp.copyCity(c);
                        return cTemp;
                    }
                }
            }
        }
        return null;
    }

    public PackLocalCity getAllCityByID(String cityID) {
        for (PackLocalCity c : Tj_Qgdisty) {
            if (c.ID.equals(cityID)) {
                PackLocalCity cTemp = new PackLocalCity();
                cTemp.copyCity(c);
                return cTemp;
            }
        }
        return null;
    }

    /**
     * 列表中是否有某个城市
     * @param list
     * @param pack
     * @return
     */
    public boolean isCityExists(List<PackLocalCity> list, PackLocalCity pack) {
        if (list == null || list.size() == 0 || pack == null) {
            return false;
        }

        for (int i = 0; i < list.size(); i++) {
            PackLocalCity row = list.get(i);
            if (row.ID.equals(pack.ID)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 从列表中删除城市
     * @param list
     * @param pack
     */
    public void delCityFromList(List<PackLocalCity> list, PackLocalCity pack) {
        if (list == null || list.size() == 0 || pack == null) {
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            PackLocalCity row = list.get(i);
            if (row.ID.equals(pack.ID)) {
                list.remove(i);
                return;
            }
        }
    }

    public String getStationId(String stationName) {
        if (TextUtils.isEmpty(stationName)) {
            return "";
        }
        for(PackLocalStation station : Tj_auto) {
            if(station.STATIONNAME.equals(stationName)) {
                return station.STATIONID;
            }
        }
        for(PackLocalStation station : Tj_Qgstations) {
            if(station.STATIONNAME.equals(stationName)) {
                return station.STATIONID;
            }
        }
        return "";
    }

    public PackLocalStation getStation(String stationName) {
        if (TextUtils.isEmpty(stationName)) {
            return null;
        }
        for(PackLocalStation station : Tj_auto) {
            if(station.STATIONNAME.equals(stationName)) {
                return station;
            }
        }
        for(PackLocalStation station : Tj_Qgstations) {
            if(station.STATIONNAME.equals(stationName)) {
                return station;
            }
        }
        return null;
    }

    public PackLocalStation getStationById(String stationId) {
        if (TextUtils.isEmpty(stationId)) {
            return null;
        }
        PackLocalStation station = new PackLocalStation();
        for (int i = 0; i < Tj_auto.size(); i++) {
            if (Tj_auto.get(i).STATIONID.equals(stationId)) {
                station = Tj_auto.get(i);
                break;
            }
        }
        return station;
    }


    public void searchStation(List<PackLocalStation> stationList, String stationName) {
        if (TextUtils.isEmpty(stationName)) {
            return;
        }
        for (int i = 0; i < Tj_auto.size(); i++) {
            if (Tj_auto.get(i).STATIONNAME.contains(stationName)) {
                stationList.add(Tj_auto.get(i));
            }
        }

//        if (isEn(stationName)) {
//            // 搜索拼音/简拼
//            String tempStr = stationName.toLowerCase();
//            for(PackLocalStation bean : Tj_auto) {
//                bean.
//            }
//        } else {
//            // 搜索名字
//            for (int i = 0; i < mListCity_3.size(); i++) {
//                PackLocalCity pack = mListCity_3.get(i);
//                if (pack.NAME.indexOf(str) > -1) {
//                    list.add(pack);
//                }
//            }
//        }
    }

    /**
     * 搜索省外自动站
     * @param stationList
     * @param stationName
     */
    public void searchCountryStation(List<PackLocalStation> stationList, String stationName) {
        if (TextUtils.isEmpty(stationName)) {
            return;
        }
        for (int i = 0; i < Tj_Qgstations.size(); i++) {
            if (Tj_Qgstations.get(i).STATIONNAME.contains(stationName)) {
                stationList.add(Tj_Qgstations.get(i));
            }
        }
    }

    public void searchStation(List<PackLocalStation> stationList) {
        stationList.addAll(Tj_auto);
    }
}


