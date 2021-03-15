package com.pcs.ztqtj.view.activity.web;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.webkit.WebView;

import com.amap.api.maps.model.LatLng;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityLocation;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalUser;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackInitDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackInitUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.photowall.PackPhotoLoginDown;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.main_en_weather.CommandMainEnRow1;
import com.pcs.ztqtj.control.tool.LoginInformation;
import com.pcs.ztqtj.control.tool.ZtqLocationTool;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 世界气象日活动页面
 * Created by tyaathome on 2017/2/23.
 */

public class ActivityWebSh extends FragmentActivityZtqBase {

    private ControlCommit controlCommit;

    // 活动地址
    private String mUrl = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_day);
        String title = getIntent().getStringExtra("title");
        mUrl = getIntent().getStringExtra("url");
        if (!TextUtils.isEmpty(title)) {
            setTitleText(title);
        }
        initWebView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (controlCommit != null) {
            controlCommit.destory();
            controlCommit = null;
        }
    }

    /**
     * 初始化浏览器
     */
    private void initWebView() {
        JsTravelCommitInterface jsInterface = new JsTravelCommitInterface(this);
        WebView webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setTextZoom(100);
        webView.getSettings().setUseWideViewPort(true);// 设置此属性，可任意比例缩放。
        controlCommit = new ControlCommit(this, webView, jsInterface);
        if (!TextUtils.isEmpty(ZtqCityDB.getInstance().getMyInfo().sys_user_id)) {
            String userid = ZtqCityDB.getInstance().getMyInfo().sys_user_id;
            PackInitDown down = (PackInitDown) PcsDataManager.getInstance().getNetPack(PackInitUp.NAME);
            String pid = down.pid;
            if (mUrl.contains("?")){
                mUrl = mUrl + "&PID=" + pid+ "&USER_ID=" + userid ;
            }else {
                mUrl = mUrl + "?PID=" + pid+ "&USER_ID=" + userid ;
            }
        } else {
            PackInitDown down = (PackInitDown) PcsDataManager.getInstance().getNetPack(PackInitUp.NAME);
            String pid = down.pid;
            if (mUrl.contains("?")){
                mUrl = mUrl + "&USER_ID=&PID=" + pid;
            }else {
                mUrl = mUrl + "?USER_ID=&PID=" + pid;
            }

        }
        controlCommit.loadUrl(mUrl);
    }

    /**
     * 获取app信息
     *
     * @return
     */
    public String getDatasFromApp() {
        // 获取imei
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String imei = tm.getDeviceId();
        // 定位城市
        PackLocalCityLocation packLocation = ZtqLocationTool.getInstance().getLocationCity();
        // 当前城市ID
        String currentCityID = packLocation.ID;
        // 当前城市显示ID
        String xianshiid = packLocation.PARENT_ID;

        PackageManager pm = getPackageManager();//context为当前Activity上下文
        PackageInfo pi = null;
        try {
            pi = pm.getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        // 版本名
        String appVersion = pi.versionName;

        // 经纬度
        LatLng latlng = ZtqLocationTool.getInstance().getLatLng();
        String lat = String.valueOf(latlng.latitude);
        String lon = String.valueOf(latlng.longitude);
        String address = "";
        RegeocodeAddress regeocodeAddress = ZtqLocationTool.getInstance()
                .getSearchAddress();
        if (regeocodeAddress != null) {
            address = regeocodeAddress.getFormatAddress();
        }

        PackLocalUser localUser =ZtqCityDB.getInstance().getMyInfo();
        JSONObject obj = new JSONObject();
        try {
            JSONObject locationObj = new JSONObject();
            locationObj.put("lat", lat);
            locationObj.put("lon", lon);
            locationObj.put("address", address);

            if (!TextUtils.isEmpty(localUser.sys_user_id)){
                obj.put("userid", localUser.sys_user_id);
            }else{
                obj.put("userid", "");
            }
            obj.put("imei", imei);
            obj.put("currentCityID", currentCityID);
            obj.put("xianshiid", xianshiid);
            obj.put("appVersion", appVersion);
            obj.put("img_head",localUser.sys_head_url);
            obj.put("username",localUser.sys_nick_name);
            obj.put("locationaddress", locationObj);
            if (CommandMainEnRow1.is_hs){
                obj.put("ishs","1");
            }else{
                obj.put("ishs","0");
            }
//            obj.put("appType", "知天气决策版");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj.toString();
    }


}
