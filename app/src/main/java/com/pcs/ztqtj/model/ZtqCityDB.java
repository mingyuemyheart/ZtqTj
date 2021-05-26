package com.pcs.ztqtj.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;

import com.pcs.lib.lib_pcs_v3.control.file.PcsGetPathValue;
import com.pcs.lib.lib_pcs_v3.control.log.Log;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCity;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityInfo;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityMain;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityUnit;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalFamilyCityInfo;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalStation;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalTravelViewInfo;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalUser;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalUserInfo;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalWarn;
import com.pcs.lib_ztqfj_v2.model.pack.net.citylist.PackCityListDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.citylist.PackCityListUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.service.PackServiceUserLoginUp;
import com.pcs.ztqtj.control.ControlAppInit;
import com.pcs.ztqtj.control.inter.Callback;
import com.pcs.ztqtj.control.observables.CityListObservable;
import com.pcs.ztqtj.control.observables.CityListObservable.CityListCallback;
import com.pcs.ztqtj.control.observables.CityListObservable.FileEmitterValue;
import com.pcs.ztqtj.control.observables.CityListObservable.FileType;
import com.pcs.ztqtj.control.tool.FileUtils;
import com.pcs.ztqtj.control.tool.ZtqAppWidget;
import com.pcs.ztqtj.model.pack.TrafficHighWay;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 城市数据
 *
 * @author JiangZy
 */
@SuppressLint("DefaultLocale")
public class ZtqCityDB {
    private static ZtqCityDB instance = null;
    private Context mContext;
    // 城市列表：1级
    private List<PackLocalCity> mListCity_1 = new ArrayList<PackLocalCity>();
    // 当前城市
    private PackLocalCityInfo mCityInfo = null;
    // 首页显示的城市
    private PackLocalCityMain mCityMain = null;
    // 省份
    private List<PackLocalCity> provincesList = new ArrayList<PackLocalCity>();
    // 风雨查询省份
    private List<PackLocalCity> provincesLiveQueryList = new ArrayList<>();
    // 旅游景点
    private List<PackLocalCity> allViewsList = new ArrayList<PackLocalCity>();
    // 全国城市
    private List<PackLocalCity> allCityInfos = new ArrayList<PackLocalCity>();
    // 当前亲情城市
    private PackLocalFamilyCityInfo mFamilyCityInfo = null;
    // 当前旅游景点
    private PackLocalTravelViewInfo mTravelViewInfo = null;
    // 当前气象服务用户信息
    private PackLocalUserInfo myUserInfo = null;
    // 城市列表：1级
    private List<PackLocalWarn> mWarnList = new ArrayList<PackLocalWarn>();
    // 单位列表
    private List<PackLocalCityUnit> mUnitList = new ArrayList<PackLocalCityUnit>();
    // 高速信息
    private List<TrafficHighWay> mHighWay = new ArrayList<TrafficHighWay>();

    // 全国城市 风雨查询使用-没有那么详细。只有类似福建九地市地名，没有更详细的信息。
    private List<PackLocalCity> liveCityInfos = new ArrayList<PackLocalCity>();

    // 全国城市
    private List<PackLocalCity> allCountryCityInfos = new ArrayList<PackLocalCity>();

    // 福建自动站
    private List<PackLocalStation> allStationList = new ArrayList<>();
    private List<PackLocalStation> baseStationList = new ArrayList<>();
    // 省外自动站
    private List<PackLocalStation> countryStationList = new ArrayList<>();

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
     *
     * @param context
     */
    public void init(Context context) {
//        mContext = context;
//        // DB
//        allCityInfos.clear();
//        allViewsList.clear();
//        // 排序
//        File file = initDB(context);
//        if (file.exists()) {
//            // 从数据库中获取城市列表
//            SqliteTool tool = SqliteTool.getInstance();
//            tool.openDB(context, file.getPath());
//            tool.getInfo(PackLocalDB.FJCITYNAME, mListCity_1);
//            tool.getInfo(PackLocalDB.PROCITYNAME, provincesList);
//            tool.getInfo(CITYNAME, allCityInfos);
//            tool.getInfo(PackLocalDB.LIVECITY, liveCityInfos);
//            tool.getInfo(PackLocalDB.TRAVELCITYNAME, allViewsList);
//
//            tool.getWarnInfo(PackLocalDB.WARN, mWarnList);
//            tool.getUnitInfo(PackLocalDB.CITYUNIT, mUnitList);
//
//            tool.getStaitonInfo(PackLocalDB.STATION, allStationList);
//            tool.getStaitonInfo(PackLocalDB.STATION_COUNTRY, countryStationList);
//
//            tool.getTrafficHighWayInfo(PackLocalDB.TRAFFICHIGHWAY, mHighWay);
//            tool.closeDB();
//
//            allCountryCityInfos.clear();
//            //allCountryCityInfos.addAll(provincesList);
//
//            allCountryCityInfos.addAll(liveCityInfos);
//        }
    }

    long start;

    public void initnew(final Context context, final Callback callback) {
        start = System.currentTimeMillis();
        CityListObservable observable = new CityListObservable(context);
        observable.setCityListCallback(new CityListCallback() {
            @Override
            public void onNext(List<FileEmitterValue> list) {
                for(FileEmitterValue value : list) {
                    parseCityData(value);
                }
                mWarnList = CityDBParseTool.getWarnList(context);
                mUnitList = CityDBParseTool.getUnitList(context);
                mHighWay = CityDBParseTool.getHighWayList(context);
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
        if(jsonObject == null) return;
        switch (type) {
            case Tj_Qgdisty:
                allCityInfos = CityDBParseTool.parseCityList(jsonObject);
                mListCity_1 = CityDBParseTool.parseTJCity(allCityInfos);

                liveCityInfos = CityDBParseTool.parseCityList(jsonObject);
                allCountryCityInfos.clear();
                allCountryCityInfos.addAll(liveCityInfos);

                countryStationList = CityDBParseTool.parseStationList(jsonObject);
                break;
            case Tj_landscapeList:
                allViewsList = CityDBParseTool.parseCityList(jsonObject);
                break;
            case Tj_auto:
                allStationList = CityDBParseTool.parseStationList(jsonObject);
                baseStationList = CityDBParseTool.parseBaseStationList(allStationList);
                countryStationList = CityDBParseTool.parseStationList(jsonObject);
                break;
            case Tj_Province:
                provincesList = CityDBParseTool.getProvinceList(jsonObject);
                provincesLiveQueryList = CityDBParseTool.getProvinceList(jsonObject);
                break;
            default:
        }
    }

    /**
     * 省内基本站
     * @return
     */
    public List<PackLocalStation> getBaseStationList() {
        if(baseStationList == null) return null;
        return new ArrayList<>(baseStationList);
    }

    /**
     * 全国站
     * @return
     */
    public List<PackLocalStation> getNationStationList() {
        if(countryStationList == null) return null;
        return new ArrayList<>(countryStationList);
    }

    private List<PackLocalCity> getLv1List(List<PackLocalCity> list, String parentid) {
        List<PackLocalCity> result = new ArrayList<>();
        for(PackLocalCity city : list) {
            if(city.PARENT_ID.equals(parentid)) {
                result.add(city);
            }
        }
        return result;
    }

    /**
     * 初始化数据库
     *
     * @param context
     * @return 返回数据库文件
     */
    private File initDB(Context context) {
        String dbPath = PcsGetPathValue.getInstance().getCityDBPath();
        String filePath = dbPath + "/pcs.db";
        File file = new File(filePath);
        File dir = new File(dbPath);
        // 城市列表父文件夹不存在
        if (!dir.exists()) {
            dir.mkdirs();
            file = new File(filePath);
            getCityDBAsset(file, context);
        } else { // 城市列表父文件夹存在
            PackCityListDown down = (PackCityListDown) PcsDataManager.getInstance().getNetPack(PackCityListUp.NAME);
            String time = "";
            if (down != null) {
                for (PackCityListDown.CityDBInfo info : down.info_list) {
                    if (info.channel_id == 0) { // 决策版
                        time = info.pub_time;
                    }
                }
                if (!TextUtils.isEmpty(time)) {
                    String fileName = "pcs" + time + ".db";
                    for (File f : dir.listFiles()) {
                        String name = f.getName();
                        if (name.equals(fileName)) {
                            return f;
                        }
                    }
                }
            }
            FileUtils.deleteAllFile(dir);
            file = new File(filePath);
            getCityDBAsset(file, context);
        }
        return file;
    }

    public void getCityDBAsset(File file, Context context) {
        // 不存在这冲assets中拷贝到指定目录
        try {
            file.createNewFile();
            InputStream in = context.getResources().getAssets().open("city_info/pcs.db");
            OutputStream out = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            in.close();
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*获取全国城市parent_id相同的城市*/
    public List<PackLocalCity> getCountryCityList(String cityId) {
        List<PackLocalCity> listChilCityInfo = new ArrayList<>();
        if (allCountryCityInfos == null) {
            return listChilCityInfo;
        }
        if (TextUtils.isEmpty(cityId)) {
            return listChilCityInfo;
        }
        for (int i = 0; i < allCountryCityInfos.size(); i++) {
            PackLocalCity cityInfo = allCountryCityInfos.get(i);
            if (cityInfo.PARENT_ID.equals(cityId)) {
                PackLocalCity cTemp = new PackLocalCity();
                cTemp.copyCity(cityInfo);
                listChilCityInfo.add(cTemp);
            }
        }
        return listChilCityInfo;
    }

    /*根据id从全国城市获取城市*/
    public PackLocalCity getCountryParentIdCity(String cityId) {
        if (allCountryCityInfos == null) {
            return null;
        }
        if (TextUtils.isEmpty(cityId)) {
            return null;
        }
        for (int i = 0; i < allCountryCityInfos.size(); i++) {
            PackLocalCity cityInfo = allCountryCityInfos.get(i);
            if (cityInfo.PARENT_ID.equals(cityId)) {
                PackLocalCity cityInfoTemp = new PackLocalCity();
                cityInfoTemp.copyCity(cityInfo);
                return cityInfoTemp;
            }
        }
        return null;
    }

    /**
     * 外省第三级城市通过省ID和二级城市名获取二级城市
     * @param parendId
     * @param name
     * @return
     */
    public PackLocalCity getCityByNameAndParentId(String parendId, String name) {
        if (allCountryCityInfos == null) {
            return null;
        }
        if (TextUtils.isEmpty(parendId) || TextUtils.isEmpty(name)) {
            return null;
        }
        for(int i = 0; i < allCountryCityInfos.size(); i++) {
            PackLocalCity cityInfo = allCountryCityInfos.get(i);
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
        if (allCountryCityInfos == null) {
            return null;
        }
        if (TextUtils.isEmpty(cityId)) {
            return null;
        }
        for (int i = 0; i < allCountryCityInfos.size(); i++) {
            PackLocalCity cityInfo = allCountryCityInfos.get(i);
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
        if (allCityInfos == null) {
            return null;
        }
        if (TextUtils.isEmpty(cityName)) {
            return null;
        }
        String matchingStr = cityName;
        // 县市区
        String lastStr = cityName.substring(cityName.length() - 1);
        if (lastStr.equals("县") || lastStr.equals("市") || lastStr.equals("区")) {
            matchingStr = cityName.substring(0, cityName.length() - 1);
        }
        for (int i = 0; i < allCityInfos.size(); i++) {
            PackLocalCity cityInfo = allCityInfos.get(i);
            if (cityInfo.NAME.equals(matchingStr)) {
                PackLocalCity cityInfoTemp = new PackLocalCity();
                cityInfoTemp.copyCity(cityInfo);
                return cityInfoTemp;
            }
        }
        return null;
    }

    /**
     * 初始化景点列表
     *
     * @param context
     */
    public void initTravels(Context context) {
        this.mContext = context;
        // 所有景点
        if (allViewsList.size() > 0) {
            return;
        }
        mTravelViewInfo = (PackLocalTravelViewInfo) PcsDataManager.getInstance().getLocalPack(PackLocalTravelViewInfo.KEY);
    }

    /**
     * 获取等级1城市列表
     *
     * @return
     */
    public final List<PackLocalCity> getCityLv1() {
        List<PackLocalCity> mListCity_1Temp = new ArrayList<PackLocalCity>();
        mListCity_1Temp.addAll(mListCity_1);
        return mListCity_1Temp;
    }

    public final List<PackLocalCity> getCityLv1WithoutTJ() {
        List<PackLocalCity> result = new ArrayList<>(mListCity_1);
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
        return new ArrayList<>(allStationList);
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
        for(PackLocalStation station : allStationList) {
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
     *
     * @param cityId
     * @return PackLocalCity；不存在则返回null
     */
    public final PackLocalCity getCityInfo1_ID(String cityId) {
        // 三级
        // if (mListCity_3 == null) {
        // mListCity_3 = pareseCityXml(context, R.raw.city_lv3);
        // }
        for (int i = 0; i < mListCity_1.size(); i++) {
            PackLocalCity pack = mListCity_1.get(i);
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
     *
     * @param cityname
     * @return PackLocalCity；不存在则返回null
     */
    public final PackLocalCity getCityInfo1_Name(String cityname) {
        if (mListCity_1 == null) {
            return null;
        }
        for (PackLocalCity bean : mListCity_1) {
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
        for(PackLocalCity city : mListCity_1) {
            if(city.ID.equals("72892")) {
                return city;
            }
        }
        return null;
    }

    /**
     * 是否纯英文
     *
     * @param s
     * @return
     */
    public boolean isEn(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (!(s.charAt(i) >= 'A' && s.charAt(i) <= 'Z')
                    && !(s.charAt(i) >= 'a' && s.charAt(i) <= 'z')) {
                return false;
            }
        }
        return true;
    }

    /**
     * 输入预警类型、预警级别
     *
     * @param type
     * @return PackLocalCity；不存在则返回null
     */
    public final PackLocalWarn getWarnInfo(String type) {
        // 预警信息列表
        if (mWarnList.size() == 0) {
            return null;
        }
        for (int i = 0; i < mWarnList.size(); i++) {
            PackLocalWarn pack = mWarnList.get(i);
            if (type.contains(pack.TYPE)) {
                return pack;
            }
        }
        return null;
    }

    /**
     * 通过cityname获取单位名
     *
     * @param cityName
     * @return
     */
    public final String getUnitByCity(String cityName) {
        for (PackLocalCityUnit unit : mUnitList) {
            if (unit.CITY.contains(cityName) || cityName.contains(unit.CITY)) {
                return unit.UNIT;
            }
        }
        return "";
    }

    /**
     * 通过searchname查询heiway
     *
     * @param search_name
     * @return
     */
    public final TrafficHighWay getHeiWayBySearchName(String search_name) {
        for (TrafficHighWay pack : mHighWay) {
            if (pack.SEARCH_NAME.equals(search_name)) {
                return pack;
            }
        }
        return null;
    }

    /**
     * 获取高速列表
     *
     * @return
     */
    public final List<TrafficHighWay> getHighWay() {
        return mHighWay;
    }

    public TrafficHighWay getHighWayByID(String id) {
        for (TrafficHighWay pack : mHighWay) {
            if (pack.ID.equals(id)) {
                return pack;
            }
        }
        return null;
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
     *
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
     *
     * @param pack
     */
    public void setCurrentCityInfo(PackLocalCityInfo pack) {
        mCityInfo = pack;
        PcsDataManager.getInstance().saveLocalData(PackLocalCityInfo.KEY, pack);
    }

    /**
     * 获取当前城市信息
     *
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
     *
     * @param pack
     */
    public void setCurrentFamilyCityInfo(PackLocalFamilyCityInfo pack) {
        mFamilyCityInfo = pack;
        PcsDataManager.getInstance().saveLocalData(PackLocalFamilyCityInfo.KEY, pack);
    }

    /**
     * 获取当前亲情城市信息
     *
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
     *
     * @param pack
     */
    public void setCurrentTravelViewInfo(PackLocalTravelViewInfo pack) {
        mTravelViewInfo = pack;
        PcsDataManager.getInstance().saveLocalData(PackLocalTravelViewInfo.KEY, pack);
    }

    /**
     * 获取当前景点信息
     *
     * @return
     */
    public PackLocalTravelViewInfo getCurrentTravelViewInfo() {
        if (mTravelViewInfo == null) {
            mTravelViewInfo = (PackLocalTravelViewInfo) PcsDataManager.getInstance().getLocalPack
                    (PackLocalTravelViewInfo.KEY);
            if (mTravelViewInfo != null) {
                if (mTravelViewInfo.localViewList.size() > 0) {
                    mTravelViewInfo.currentTravelView = mTravelViewInfo.localViewList
                            .get(mTravelViewInfo.defaultPosition);
                }
            } else {
                mTravelViewInfo = new PackLocalTravelViewInfo();
            }

        }
        return mTravelViewInfo;
    }

    /**
     * 设置当前气象服务用户信息
     *
     * @param pack
     */
    public void setMyInfo(PackLocalUserInfo pack) {
        myUserInfo = pack;
        PcsDataManager.getInstance().saveLocalData(PackLocalUserInfo.KEY, pack);
    }

    /**
     * 删除当前气象服务用户信息
     */
    public void removeMyInfo() {
        PcsDataManager.getInstance().removeLocalData(PackServiceUserLoginUp.NAME);
        PcsDataManager.getInstance().removeLocalData(PackLocalUserInfo.KEY);
        myUserInfo = null;
    }

    /**
     * 获取当前气象服务用户信息
     *
     * @return
     */
    public PackLocalUser getMyInfo() {
        if (myUserInfo == null) {
            myUserInfo = (PackLocalUserInfo) PcsDataManager.getInstance().getLocalPack(PackLocalUserInfo.KEY);
//            myUserInfo = (PackLocalUserInfo) PcsDataManager.getInstance().getLocalPack(PackLocalUser.KEY);
        }
        if (myUserInfo == null) {
            myUserInfo = new PackLocalUserInfo();
        }
        return myUserInfo.currUserInfo;
    }

    /**
     * 是否登录气象服务
     * @return
     */
    public boolean isLoginService() {
        PackLocalUser userinfo = getMyInfo();
        if(userinfo != null && !TextUtils.isEmpty(userinfo.user_id)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 决策服务是否有权限
     * @return
     */
    public boolean isServiceAccessible() {
        PackLocalUser info = ZtqCityDB.getInstance().getMyInfo();
        if(info == null) {
            return false;
        }
        return info.is_jc;
    }

    /**
     * 获取所有的景点
     *
     * @return
     */
    public List<PackLocalCity> getAllViewsInfo() {
        List<PackLocalCity> cityList = new ArrayList<>();
        for (PackLocalCity pack : allViewsList) {
            PackLocalCity cTemp = new PackLocalCity();
            cTemp.copyCity(pack);
            cityList.add(cTemp);
        }
//        dataList.addAll(allViewsList);
        return cityList;
    }

    /**
     * 获取省份列表
     *
     * @return
     */
    public List<PackLocalCity> getProvincesList() {
        return new ArrayList<>(provincesList);
    }

    /**
     * 获取风雨查询省份列表
     * @return
     */
    public List<PackLocalCity> getProvincesLiveQueryList() {
        return new ArrayList<>(provincesLiveQueryList);
    }

    public PackLocalCity getProvinceByName(String name) {
        for (PackLocalCity pack : provincesList) {
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
     *
     * @param key
     * @return
     */
    public List<PackLocalCity> searchProvince(String key) {
        String low = key.toLowerCase();
        List<PackLocalCity> resultList = new ArrayList<PackLocalCity>();
        for (PackLocalCity pack : provincesList) {
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
     *
     * @param key
     * @return
     */
    public List<PackLocalCity> searchCity(String key) {
        String low = key.toLowerCase();
        List<PackLocalCity> resultList = new ArrayList<PackLocalCity>();
        for (PackLocalCity pack : allCityInfos) {
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
        List<PackLocalCity> resultList = new ArrayList<PackLocalCity>();
        for (PackLocalCity pack : allCountryCityInfos) {
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
        List<PackLocalCity> resultList = new ArrayList<PackLocalCity>();
        for (PackLocalCity pack : allCityInfos) {
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
     *
     * @param key
     * @return
     */
    public List<PackLocalCity> searchTrave(String key) {
        String low = key.toLowerCase();
        List<PackLocalCity> resultList = new ArrayList<PackLocalCity>();
        for (PackLocalCity pack : allViewsList) {
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
     *
     * @param id
     * @return
     */
    public PackLocalCity getProvinceById(String id) {
        for (PackLocalCity pack : provincesList) {
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
     *
     * @return
     */
    public List<PackLocalCity> getAllCityInfos() {
        return allCityInfos;
    }

    public List<PackLocalCity> getViewsByProcinceID(String provinceID) {
        List<PackLocalCity> citys = new ArrayList<PackLocalCity>();
        // // 所有景点
        // if (allViewsList == null) {
        // allViewsList = pareseCityXml(context, R.raw.travel_city);
        // }
        for (PackLocalCity c : allViewsList) {
            if (c.PARENT_ID.equals(provinceID)) {
                PackLocalCity cTemp = new PackLocalCity();
                cTemp.copyCity(c);
                citys.add(cTemp);
            }
        }
        return citys;
    }

    public List<PackLocalCity> getCityByProcinceID(String provinceID) {
        List<PackLocalCity> citys = new ArrayList<PackLocalCity>();
        for (PackLocalCity c : allCityInfos) {
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
     *
     * @param provinceName
     * @param cityName
     * @return
     */
    public PackLocalCity getAllCityByName(String provinceName, String cityName) {
        if (TextUtils.isEmpty(cityName)) {
            return null;
        }
        for (PackLocalCity c : allCityInfos) {
            if (cityName.contains(c.NAME) || c.NAME.contains(cityName)) {
                // 查询省份
                for (PackLocalCity p : provincesList) {
                    if ((provinceName.contains(p.NAME) || p.NAME
                            .contains(provinceName))
                            && c.PARENT_ID.equals(p.ID)) {

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
        for (PackLocalCity c : allCityInfos) {
            if (c.ID.equals(cityID)) {
                PackLocalCity cTemp = new PackLocalCity();
                cTemp.copyCity(c);
                return cTemp;
            }
        }

        return null;
    }

    public PackLocalCity getAllCityByActName(String cityName) {
        for (PackLocalCity c : allCityInfos) {
            if (c.NAME.equals(cityName)) {
                PackLocalCity cTemp = new PackLocalCity();
                cTemp.copyCity(c);
                return cTemp;
            }
        }

        return null;
    }

    /**
     * 列表中是否有某个城市
     *
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
     *
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
        for(PackLocalStation station : allStationList) {
            if(station.STATIONNAME.equals(stationName)) {
                return station.STATIONID;
            }
        }
        for(PackLocalStation station : countryStationList) {
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
        for(PackLocalStation station : allStationList) {
            if(station.STATIONNAME.equals(stationName)) {
                return station;
            }
        }
        for(PackLocalStation station : countryStationList) {
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
        for (int i = 0; i < allStationList.size(); i++) {
            if (allStationList.get(i).STATIONID.equals(stationId)) {
                station = allStationList.get(i);
                break;
            }
        }
        return station;
    }


    public void searchStation(List<PackLocalStation> stationList, String stationName) {
        if (TextUtils.isEmpty(stationName)) {
            return;
        }
        for (int i = 0; i < allStationList.size(); i++) {
            if (allStationList.get(i).STATIONNAME.contains(stationName)) {
                stationList.add(allStationList.get(i));
            }
        }

//        if (isEn(stationName)) {
//            // 搜索拼音/简拼
//            String tempStr = stationName.toLowerCase();
//            for(PackLocalStation bean : allStationList) {
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
        for (int i = 0; i < countryStationList.size(); i++) {
            if (countryStationList.get(i).STATIONNAME.contains(stationName)) {
                stationList.add(countryStationList.get(i));
            }
        }
    }

    public void searchStation(List<PackLocalStation> stationList) {
        stationList.addAll(allStationList);
    }
}


