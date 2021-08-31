package com.pcs.ztqtj;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.evernote.android.job.JobManager;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.pcs.lib.lib_pcs_v3.control.tool.ProcessTool;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.ztqtj.control.ControlAppInit;
import com.pcs.ztqtj.control.tool.SharedPreferencesUtil;
import com.pcs.ztqtj.control.tool.ZtqLocationTool;
import com.pcs.ztqtj.view.appwidget.job.UpdateWidgetJob;
import com.pcs.ztqtj.view.appwidget.job.WidgetJobCreator;
import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.IUmengCallback;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.common.inter.ITagManager;
import com.umeng.message.entity.UMessage;
import com.umeng.message.tag.TagManager;
import com.umeng.socialize.PlatformConfig;

import org.json.JSONObject;

import java.util.List;

public class MyApplication extends MultiDexApplication {

    public static Application application;
    public static String appKey = "606c002b18b72d2d244655a7", msgSecret = "00c225802b55b333e3b62d21370b281f";
    private static PushAgent mPushAgent;
    public static String DEVICETOKEN = "";

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        getUserInfo(this);
        initUmeng();
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);
        if (!isMainProcess()) {
            return;
        }
        JobManager.create(this).addJobCreator(new WidgetJobCreator(this));
        UpdateWidgetJob.scheduleJob();
        ControlAppInit.getInstance().setIs_Main(true);
        application = this;
        // bugly
        CrashReport.initCrashReport(this, "73c5452282", false);
        //崩溃重启
//        PcsUncaughtException.getInstance().init(this);
        SharedPreferencesUtil.getInstance(this, getPackageName());
        //初始化讯飞ID
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=60515ebe");
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

    public static String offline = "offline";
    //本地保存用户信息参数
    public static String USERNAME = "";
    public static String PASSWORD = "";
    public static String TOKEN = offline;
    public static String UID = offline;
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
        UID = offline;
        USERNAME = "";
        PASSWORD = "";
        TOKEN = offline;
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
        UID = sharedPreferences.getString(UserInfo.uId, offline);
        USERNAME = sharedPreferences.getString(UserInfo.userName, "");
        PASSWORD = sharedPreferences.getString(UserInfo.passWord, "");
        TOKEN = sharedPreferences.getString(UserInfo.token, offline);
        NAME = sharedPreferences.getString(UserInfo.name, "");
        PARTMENT = sharedPreferences.getString(UserInfo.partment, "");
        DUTY = sharedPreferences.getString(UserInfo.duty, "");
        MOBILE = sharedPreferences.getString(UserInfo.mobile, "");
        PORTRAIT = sharedPreferences.getString(UserInfo.portrait, "");
        LIMITINFO = sharedPreferences.getString(UserInfo.limitInfo, "");
    }

    private void initUmeng() {
        UMConfigure.init(this,appKey,"umeng",UMConfigure.DEVICE_TYPE_PHONE, msgSecret);
        PlatformConfig.setQQZone("1111680665", "AWvIDxEHLwH5M3xG");// QQ和Qzone appid appkey
        PlatformConfig.setWeixin("wxa3d2ebc5508eb19f", "c84c96f71d36bd19fc7d5d5bbe30e48c");    //微信 appid appsecret
        PlatformConfig.setSinaWeibo("838194335", "a3cec82194cbc4df3c81f852bd0e5a0d","http://sns.whalecloud.com/sina/");//新浪微博 appkey appsecret
        UMConfigure.setLogEnabled(true);
        registerUmengPush();
        //华为推送
//        HuaWeiRegister.register(this);
//
//        //小米推送
//        MiPushRegistar.register(this, "2882303761517530819", "5981753028819");
//
//        //魅族推送
//        MeizuRegister.register(this, "112611", "4bdce467a15e4ba4a34dc0e6db7ce817");
    }

    /**
     * 注册umeng推送
     */
    private void registerUmengPush() {
        mPushAgent = PushAgent.getInstance(this);
        //manifest里面的package最好与build.gradle中的applicationId保持一 致，如不一致，需调用setResourcePackageName设置资源文件包名
        mPushAgent.setResourcePackageName("com.pcs.ztqtj");

        //参数number可以设置为0~10之间任意整数。当参数为0时，表示不合并通知
        mPushAgent.setDisplayNotificationNumber(0);

//        //sdk开启通知声音
//        mPushAgent.setNotificationPlaySound(MsgConstant.NOTIFICATION_PLAY_SDK_ENABLE);
//        // sdk关闭通知声音
//		mPushAgent.setNotificationPlaySound(MsgConstant.NOTIFICATION_PLAY_SDK_DISABLE);
//        // 通知声音由服务端控制
//		mPushAgent.setNotificationPlaySound(MsgConstant.NOTIFICATION_PLAY_SERVER);
//		mPushAgent.setNotificationPlayLights(MsgConstant.NOTIFICATION_PLAY_SDK_DISABLE);
//		mPushAgent.setNotificationPlayVibrate(MsgConstant.NOTIFICATION_PLAY_SDK_DISABLE);
//
//        //此处是完全自定义处理设置
//        mPushAgent.setPushIntentServiceClass(MyPushIntentService.class);

        //注册推送服务 每次调用register都会回调该接口
        mPushAgent.register(new IUmengRegisterCallback() {
            @Override
            public void onSuccess(String deviceToken) {
                Log.e("deviceToken", deviceToken);
                DEVICETOKEN = deviceToken;
            }

            @Override
            public void onFailure(String s, String s1) {
            }
        });

        /**
         * 自定义行为的回调处理
         * UmengNotificationClickHandler是在BroadcastReceiver中被调用，故
         * 如果需启动Activity，需添加Intent.FLAG_ACTIVITY_NEW_TASK
         * */
        mPushAgent.setNotificationClickHandler(new UmengNotificationClickHandler() {
            @Override
            public void dealWithCustomAction(Context context, UMessage msg) {
                super.dealWithCustomAction(context, msg);
                Log.e("deviceToken", msg.extra.toString());
                if (msg.extra != null) {
                    JSONObject obj = new JSONObject(msg.extra);
//                    try {
//                        Intent intent;
//                        if (!obj.isNull("otherInfo")) {
//                            intent = new Intent(getApplicationContext(), CheckWorksActivity.class);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(intent);
//                        }else if (!obj.isNull("url")) {
//                            String url = obj.getString("url");
//                            if (!TextUtils.isEmpty(url)) {
//                                intent = new Intent(getApplicationContext(), WarningDetailActivity.class);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                intent.putExtra("url", url);
//                                startActivity(intent);
//                            }
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
                }
            }
        });

    }

    /**
     * 打开推送
     */
    public static void enablePush() {
        if (mPushAgent != null) {
            mPushAgent.enable(new IUmengCallback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onFailure(String s, String s1) {

                }
            });
        }
    }

    /**
     * 关闭推送
     */
    public static void disablePush() {
        if (mPushAgent != null) {
            mPushAgent.disable(new IUmengCallback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onFailure(String s, String s1) {

                }
            });
        }
    }

    /**
     * 设置推送静默
     */
    public static void setNoDisturbMode() {
        if (mPushAgent != null) {
            mPushAgent.setNoDisturbMode(22, 0, 8, 0);
        }
    }

    /**
     * 设置推送标签
     */
    public static void addPushTags(String tags) {
        if (mPushAgent != null) {
            mPushAgent.getTagManager().addTags(new TagManager.TCallBack() {
                @Override
                public void onMessage(boolean b, ITagManager.Result result) {
                    mPushAgent.getTagManager().getTags(new TagManager.TagListCallBack() {
                        @Override
                        public void onMessage(boolean b, List<String> list) {
                            String tags = "";
                            for (int i = 0; i < list.size(); i++) {
                                tags += list.get(i)+",";
                            }
                            Log.e("tgas", tags);
                        }
                    });
                }
            }, tags);
        }
    }

}