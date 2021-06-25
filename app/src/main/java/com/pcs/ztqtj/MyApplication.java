package com.pcs.ztqtj;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.evernote.android.job.JobManager;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.pcs.lib.lib_pcs_v3.control.tool.ProcessTool;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalUser;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalUserInfo;
import com.pcs.ztqtj.control.ControlAppInit;
import com.pcs.ztqtj.control.tool.SharedPreferencesUtil;
import com.pcs.ztqtj.control.tool.ZtqLocationTool;
import com.pcs.ztqtj.control.tool.utils.TextUtil;
import com.pcs.ztqtj.control.tool.youmeng.ShareUtil;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.util.OkHttpUtil;
import com.pcs.ztqtj.view.activity.photoshow.ActivityPhotoLogin;
import com.pcs.ztqtj.view.appwidget.job.UpdateWidgetJob;
import com.pcs.ztqtj.view.appwidget.job.WidgetJobCreator;
import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyApplication extends MultiDexApplication {

    public static Application application;
    public static final long START_INTERVAL = 300;

    {
        //初始化分享
        //Config.DEBUG = true;
        ShareUtil.initShare();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        getUserInfo(this);
        UMConfigure.init(this,"606c002b18b72d2d244655a7","umeng",UMConfigure.DEVICE_TYPE_PHONE,"");
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);
        if (!isMainProcess()) {
            return;
        }
        JobManager.create(this).addJobCreator(new WidgetJobCreator(this));
        UpdateWidgetJob.scheduleJob();
        ControlAppInit.getInstance().setIs_Main(true);
        application = this;
        // bugly
        CrashReport.initCrashReport(this, "959e7bdb27", false);
        //崩溃重启
//        PcsUncaughtException.getInstance().init(this);
        SharedPreferencesUtil.getInstance(this, getPackageName());
        //初始化讯飞ID
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=59f0382d");
        //初始化
        ControlAppInit.getInstance().init(MyApplication.this);
        //注册锁屏广播接收
        registLockReceiver();
    }

    /**
     * 注册锁屏广播接收
     */
    private void registLockReceiver() {
        final IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mReceiverLock, filter);
    }

    /**
     * 是否主进程
     * @return
     */
    private boolean isMainProcess() {
        String process = ProcessTool.getProcessName(this, android.os.Process.myPid());
        if (getPackageName().equals(process)) {
            return true;
        }
        return false;
    }

    /**
     * 锁屏广播接收
     */
    private BroadcastReceiver mReceiverLock = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == Intent.ACTION_SCREEN_ON) {
                PcsDataDownload.setPause(false);
                ZtqLocationTool.getInstance().setPause(false);
            } else if (intent.getAction() == Intent.ACTION_SCREEN_OFF) {
                PcsDataDownload.setPause(true);
                ZtqLocationTool.getInstance().setPause(true);
            }
        }
    };

    //本地保存用户信息参数
    public static String USERNAME = "";
    public static String PASSWORD = "";
    public static String TOKEN = "";
    public static String UID = "";
    public static String NAME = "";//用户名字
    public static String PARTMENT = "";//部门
    public static String DUTY = "";//职务
    public static String MOBILE = "";//电话号码
    public static String PORTRAIT = "";//头像
    public static String LIMITINFO = "";//标签权限

    public static String USERINFO = "userInfo";//userInfo sharedPreferance名称
    public static class UserInfo {
        private static final String uId = "uId";
        private static final String userName = "uName";
        private static final String passWord = "pwd";
        private static final String token = "token";
        private static final String name = "name";//用户名字
        private static final String partment = "partment";//部门
        private static final String duty = "duty";//职务
        private static final String mobile = "mobile";//电话号码
        private static final String portrait = "portrait";//头像
        private static final String limitInfo = "limitInfo";//头像
    }

    /**
     * 清除用户信息
     */
    public static void clearUserInfo(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(USERINFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        UID = "";
        USERNAME = "";
        PASSWORD = "";
        TOKEN = "";
        NAME = "";
        PARTMENT = "";
        DUTY = "";
        MOBILE = "";
        PORTRAIT = "";
        LIMITINFO = "";
    }

    /**
     * 保存用户信息
     */
    public static void saveUserInfo(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(USERINFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(UserInfo.uId, UID);
        editor.putString(UserInfo.userName, USERNAME);
        editor.putString(UserInfo.passWord, PASSWORD);
        editor.putString(UserInfo.token, TOKEN);
        editor.putString(UserInfo.name, NAME);
        editor.putString(UserInfo.partment, PARTMENT);
        editor.putString(UserInfo.duty, DUTY);
        editor.putString(UserInfo.mobile, MOBILE);
        editor.putString(UserInfo.portrait, PORTRAIT);
        editor.putString(UserInfo.limitInfo, LIMITINFO);
        editor.apply();
    }

    /**
     * 获取用户信息
     */
    public static void getUserInfo(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(USERINFO, Context.MODE_PRIVATE);
        UID = sharedPreferences.getString(UserInfo.uId, "");
        USERNAME = sharedPreferences.getString(UserInfo.userName, "");
        PASSWORD = sharedPreferences.getString(UserInfo.passWord, "");
        TOKEN = sharedPreferences.getString(UserInfo.token, "");
        NAME = sharedPreferences.getString(UserInfo.name, "");
        PARTMENT = sharedPreferences.getString(UserInfo.partment, "");
        DUTY = sharedPreferences.getString(UserInfo.duty, "");
        MOBILE = sharedPreferences.getString(UserInfo.mobile, "");
        PORTRAIT = sharedPreferences.getString(UserInfo.portrait, "");
        LIMITINFO = sharedPreferences.getString(UserInfo.limitInfo, "");

        okHttpLogin(context);
    }

    /**
     * 游客用户登录
     */
    private static void okHttpLogin(final Context context) {
        if (!TextUtil.isEmpty(TOKEN)) {//已登录状态
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = CONST.BASE_URL+"user/offline";
                JSONObject param = new JSONObject();
                try {
                    param.put("token", "offline");
                    String json = param.toString();
                    final RequestBody body = FormBody.create(MediaType.parse("application/json; charset=utf-8"), json);
                    OkHttpUtil.enqueue(new Request.Builder().post(body).url(url).build(), new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                        }
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (!response.isSuccessful()) {
                                return;
                            }
                            final String result = response.body().string();
                            if (!TextUtils.isEmpty(result)) {
                                try {
                                    JSONObject obj = new JSONObject(result);
                                    if (!obj.isNull("token")) {
                                        MyApplication.TOKEN = obj.getString("token");
                                        Log.e("token", MyApplication.TOKEN);
                                    }
                                    if (!obj.isNull("limitInfo")) {
                                        MyApplication.LIMITINFO = obj.getString("limitInfo");
                                    }
                                    MyApplication.saveUserInfo(context);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

//    /**
//     * 获取站点信息
//     */
//    public static void okHttpGeo(final double lat, final double lng) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                String url = CONST.GEO_URL;
//                FormBody.Builder builder = new FormBody.Builder();
//                builder.add("lat", lat+"");
//                builder.add("lon", lng+"");
//                RequestBody body = builder.build();
//                OkHttpUtil.enqueue(new Request.Builder().post(body).url(url).build(), new Callback() {
//                    @Override
//                    public void onFailure(Call call, IOException e) {
//                    }
//                    @Override
//                    public void onResponse(Call call, Response response) throws IOException {
//                        if (!response.isSuccessful()) {
//                            return;
//                        }
//                        final String result = response.body().string();
//                        if (!TextUtils.isEmpty(result)) {
//                            try {
//                                JSONObject obj = new JSONObject(result);
//                                if (!obj.isNull("station")) {
////                                    MyApplication.STATIONID = obj.getString("station");
////                                    if (!obj.isNull("city")) {
////                                        JSONObject city = obj.getJSONObject("city");
////                                        if (!city.isNull("station")) {
////                                            MyApplication.STATIONID = city.getString("station");
////                                            if (!city.isNull("city")) {
////                                                JSONObject area = city.getJSONObject("city");
////                                                if (!area.isNull("station")) {
////                                                    MyApplication.STATIONID = area.getString("station");
////                                                }
////                                            }
////                                        }
////                                    }
//
////                                    MyApplication.STATIONID = "101010100";//测试用
////                                    MyApplication.STATIONID = "101190101";//测试用
//                                }
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                });
//            }
//        }).start();
//    }

}