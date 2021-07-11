package com.pcs.ztqtj.control.tool;

import android.text.TextUtils;

import com.amap.api.maps.model.LatLng;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCity;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityLocation;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityMain;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackForecastWeatherTipDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackForecastWeatherTipUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackHourForecastUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackLifeNumberUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackSstqUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.airinfopack.PackAirInfoSimpleUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.PackWarnWeatherUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.PackWarningCenterYJXXGridIndexUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.PackYjxxIndexFbUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.week.PackMainWeekWeatherUp;

/**
 * 自动下载天气
 * @author JiangZy
 */
public class AutoDownloadWeather {
    // 默认城市ID
    private PackLocalCity mDefaultCity = null;

    // 首页城市ID
    private PackLocalCityMain mMainCity = new PackLocalCityMain();
    //暂停首页数据？
    private boolean mMainDataPaused = true;
    //温馨提示上传包
    private PackForecastWeatherTipUp mPackForecastWeatherTipUp = new PackForecastWeatherTipUp();

    private static AutoDownloadWeather instance = null;

    public static AutoDownloadWeather getInstance() {
        if (instance == null) {
            instance = new AutoDownloadWeather();
        }
        return instance;
    }

    /**
     * 添加一周天气城市
     */
    public void addWeekCity(PackLocalCity city) {
        // 一周天气-主界面显示（）
        PackMainWeekWeatherUp packMainWeekWeather = new PackMainWeekWeatherUp();
        packMainWeekWeather.setCity(city);
        PcsDataDownload.addDownload(packMainWeekWeather);
    }

    public void removeWeekCity(PackLocalCity city) {
        // 一周天气-主界面
        PackMainWeekWeatherUp packMainWeekWeather = new PackMainWeekWeatherUp();
        packMainWeekWeather.setCity(city);
        PcsDataDownload.removeDownload(packMainWeekWeather);
    }

    /**
     * 移除默认城市数据下载
     */
    private void removeDefaultCity(PackLocalCity cityInfo) {
        // 首页预警
       // PackYjxxIndexUp packYjxxIndex = new PackYjxxIndexUp();
        PackYjxxIndexFbUp packYjxxIndex=new PackYjxxIndexFbUp();
        packYjxxIndex.setCity(cityInfo);
        PcsDataDownload.removeDownload(packYjxxIndex);
        // 实时天气
        PackSstqUp packSstq = new PackSstqUp();
        packSstq.area = cityInfo.ID;
        PcsDataDownload.removeDownload(packSstq);
    }

    /**
     * 设置默认城市
     */
    public void setDefaultCity(PackLocalCity cityInfo) {
        if (cityInfo == null) {
            return;
        }
        if (mDefaultCity != null && !TextUtils.isEmpty(mDefaultCity.ID) && !mDefaultCity.ID.equals(cityInfo.ID)) {
            // 移除前一个城市
            removeDefaultCity(cityInfo);
        }
        mDefaultCity = cityInfo;
        // 首页预警
        PackYjxxIndexFbUp packYjxxIndex=new PackYjxxIndexFbUp();
        packYjxxIndex.setCity(cityInfo);
        PcsDataDownload.addDownload(packYjxxIndex);

        // 实时天气
        PackSstqUp packSstq = new PackSstqUp();
        packSstq.area = cityInfo.ID;
        packSstq.is_jc = ZtqCityDB.getInstance().isLoginService();
        PcsDataDownload.addDownload(packSstq);
    }

    /**
     * 停止首页数据
     */
    public void stopMainData() {
        if (TextUtils.isEmpty(mMainCity.ID)) {
            return;
        }
        // 逐时预报
        PackHourForecastUp mainHour = new PackHourForecastUp();
        mainHour.county_id = mMainCity.ID;
        PcsDataDownload.removeDownload(mainHour);
        // 首页空气质量
        PackAirInfoSimpleUp packAirInfoSimple = new PackAirInfoSimpleUp();
        packAirInfoSimple.type = "1";
        packAirInfoSimple.setCity(mMainCity);
        PcsDataDownload.removeDownload(packAirInfoSimple);
        //未读预警
        PackWarnWeatherUp packWarnWeatherUp = new PackWarnWeatherUp();
        packWarnWeatherUp.areaid = mMainCity.ID;
        PcsDataDownload.removeDownload(packWarnWeatherUp);
        // 生活指数
        PackLifeNumberUp packLifeNumber = new PackLifeNumberUp();
        packLifeNumber.area = mMainCity.ID;

        PcsDataDownload.removeDownload(packLifeNumber);
        //格点预警
        PcsDataDownload.removeDownload(PackWarningCenterYJXXGridIndexUp.NAME);
        //温馨提示
        PcsDataDownload.removeDownload(mPackForecastWeatherTipUp.getName());
        mMainCity.ID = "";
    }

    /**
     * 设置首页数据暂停
     * @param isPause
     */
    public void setMainDataPause(boolean isPause) {
        mMainDataPaused = isPause;
    }

    /**
     * 开始下载首页数据
     */
    public void beginMainData() {
        if (mMainDataPaused) {
            return;
        }
        PackLocalCityMain cityMain = ZtqCityDB.getInstance().getCityMain();
        if (cityMain == null||TextUtils.isEmpty(cityMain.ID)) {
            return;
        }
        stopMainData();
        mMainCity.copyCity(cityMain);
        // 一周天气
        addWeekCity(mMainCity);
        // 逐时预报
        PackHourForecastUp mainHour = new PackHourForecastUp();
        mainHour.county_id = cityMain.ID;
        PcsDataDownload.addDownload(mainHour);
        // 首页空气质量
        PackAirInfoSimpleUp packAirInfoSimple = new PackAirInfoSimpleUp();
        packAirInfoSimple.type = "1";
        packAirInfoSimple.setCity(cityMain);
        PcsDataDownload.addDownload(packAirInfoSimple);
        //未读预警
        PackWarnWeatherUp packWarnWeatherUp = new PackWarnWeatherUp();
        packWarnWeatherUp.areaid = mMainCity.ID;
        PcsDataDownload.addDownload(packWarnWeatherUp);
        // 生活指数
        PackLifeNumberUp packLifeNumber = new PackLifeNumberUp();
        packLifeNumber.area = cityMain.ID;
        PcsDataDownload.addDownload(packLifeNumber);
        //请求城市预警
        reqCityWarning();
    }

    private void reqCityWarning() {
        String province = "";
        //------预警
        PackLocalCityLocation packCityLocation = ZtqLocationTool.getInstance().getLocationCity();
        if (packCityLocation == null) {
            return;
        }
        //省份
        PackLocalCity packProvince = ZtqCityDB.getInstance().getProvinceById(packCityLocation.PARENT_ID);
        if (packProvince == null) {
            return;
        }
        province = packProvince.NAME;
        //请求预警
        PackWarningCenterYJXXGridIndexUp packWarningUp = new PackWarningCenterYJXXGridIndexUp();

        packWarningUp.area = packCityLocation.ID;

        packWarningUp.province = province;
        PcsDataDownload.addDownload(packWarningUp);
        //------温馨提示
        LatLng lantLng = ZtqLocationTool.getInstance().getLatLng();
        if (lantLng == null) {
            return;
        }
        //请求温馨提示
        mPackForecastWeatherTipUp.province = province;
        mPackForecastWeatherTipUp.latitude = String.valueOf(lantLng.latitude);
        mPackForecastWeatherTipUp.longitude = String.valueOf(lantLng.longitude);
        PcsDataDownload.addDownload(mPackForecastWeatherTipUp);
    }

    public PackForecastWeatherTipDown getPackForecastWeatherTipDown() {
        PackForecastWeatherTipDown packDown = (PackForecastWeatherTipDown) PcsDataManager.getInstance().getNetPack(mPackForecastWeatherTipUp.getName());
        return packDown;
    }
}
