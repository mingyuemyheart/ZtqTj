package com.pcs.ztqtj.view.activity.web.webview;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.amap.api.maps.model.LatLng;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityLocation;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalUser;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackInitDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackInitUp;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.MyConfigure;
import com.pcs.ztqtj.control.tool.PermissionsTools;
import com.pcs.ztqtj.control.tool.ZtqLocationTool;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
import com.pcs.ztqtj.view.activity.photoshow.ActivityPhotoLogin;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 普通网页
 */
public class ActivityWebView extends FragmentActivityZtqBase {

    public WebView webview;
    private ControlWebView control;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            WebView.enableSlowWholeDocumentDraw();
        }
        setContentView(R.layout.activity_web);
        initView();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        control.destory();
    }

    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {
        super.onActivityResult(arg0, arg1, arg2);
        control.onActivityResult(arg0, arg1, arg2);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == MyConfigure.REQUEST_CAMERA) {
            PermissionsTools.onRequestPermissionsResult(this, permissions, grantResults, new PermissionsTools.RequestPermissionResultCallback() {
                @Override
                public void onSuccess() {
                    control.clickCamera();
                }

                @Override
                public void onDeny() {

                }

                @Override
                public void onDenyNeverAsk() {

                }
            });
        }
    }

    protected void loadUrl(String url) {
        // 设置下载
        if (!TextUtils.isEmpty(url)) {
            webview.loadUrl(url);
        }
    }

    public String url;
    public String shareContent;
    public String pagetitle;
    private void initData() {
        Intent intent = getIntent();
        pagetitle = intent.getStringExtra("title");
        setTitleText(pagetitle);
        url = intent.getStringExtra("url");
        shareContent = intent.getStringExtra("shareContent");
        control = new ControlWebView(this, null);
        String pid = "?PID=";
        PackInitDown packDown = (PackInitDown) PcsDataManager.getInstance().getNetPack(PackInitUp.NAME);
        if (packDown != null) {
            pid += packDown.pid;
        }
        String userId = "&USER_ID=" + ZtqCityDB.getInstance().getMyInfo().sys_user_id;
        loadUrl(url + pid + userId);
    }

    public void toLoginActivity() {
        startActivityForResult(new Intent(this, ActivityPhotoLogin.class), control.resultCode);
    }

    public void commitShareResult(SHARE_MEDIA share_media) {
        control.commitShareResult(share_media);
    }

    public void openShare(String act_id, String type) {
        control.shareClick(act_id, type);
    }

    public String getShareContent() {
        return shareContent;
    }

    //提交视频
    public void showFileChooser(String user_id, String act_id) {
        control.showFileChooser(user_id, act_id);
    }

    //提交图片
    public void showPowChose(String id) {
        control.showPowChose(id);
    }

    private void initView() {
        webview = (WebView) findViewById(R.id.webview);
        webview.getSettings().setUseWideViewPort(true);// 设置此属性，可任意比例缩放。
        webview.getSettings().setTextZoom(100);
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("http:") || url.startsWith("https:")) {
                    view.removeAllViews();
                    view.loadUrl(url);
                    return false;
                }
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {

            }

            @Override
            public void onPageFinished(WebView view, String url) {

            }
        });

        webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int progress) {
                super.onProgressChanged(view, progress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }
        });
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);//允许js
        webSettings.setBlockNetworkImage(false);//后台处理加载图片
        JsInterfaceWebView interBanner = new JsInterfaceWebView(this);
        webview.addJavascriptInterface(interBanner, "js");
        webview.setDrawingCacheEnabled(true);
    }

    /**
     * 获取app信息
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
        RegeocodeAddress regeocodeAddress = ZtqLocationTool.getInstance().getSearchAddress();
        if (regeocodeAddress != null) {
            address = regeocodeAddress.getFormatAddress();
        }
        PackLocalUser localUser=ZtqCityDB.getInstance().getMyInfo();
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
//            obj.put("appType", "知天气决策版");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj.toString();
    }

}
