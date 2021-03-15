package com.pcs.ztqtj;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.evernote.android.job.JobManager;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.pcs.lib.lib_pcs_v3.control.tool.ProcessTool;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.ztqtj.control.ControlAppInit;
import com.pcs.ztqtj.control.tool.SharedPreferencesUtil;
import com.pcs.ztqtj.control.tool.ZtqLocationTool;
import com.pcs.ztqtj.control.tool.youmeng.ShareUtil;
import com.pcs.ztqtj.view.appwidget.job.UpdateWidgetJob;
import com.pcs.ztqtj.view.appwidget.job.WidgetJobCreator;
import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;

public class MyApplication extends MultiDexApplication {
    public static Application application;

    public static final long START_INTERVAL = 300;

    private final int WHAT_CHECK_INIT = 101;

    {
        //初始化分享
        //Config.DEBUG = true;
        ShareUtil.initShare();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            if (msg.what == WHAT_CHECK_INIT) {
//                //初始化
//                ControlAppInit.getInstance().init(MyApplication.this);
//                //注册锁屏广播接收
//                registLockReceiver();
            }
        }
    };

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
        //StubAppUtils.attachBaseContext(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        UMConfigure.init(this,"5ca458313fc19546520016c3","umeng",UMConfigure.DEVICE_TYPE_PHONE,"");
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);
        if (!isMainProcess()) {
            return;
        }
        JobManager.create(this).addJobCreator(new WidgetJobCreator(this));
        UpdateWidgetJob.scheduleJob();
        ControlAppInit.getInstance().setIs_Main(true);
        application = this;
        mHandler.sendEmptyMessageDelayed(WHAT_CHECK_INIT, START_INTERVAL);
        // bugly
        CrashReport.initCrashReport(this, "959e7bdb27", false);
        //崩溃重启
//        PcsUncaughtException.getInstance().init(this);
        SharedPreferencesUtil.getInstance(this, "com.pcs.ztqtj");
        //初始化讯飞ID
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=59f0382d");
        //初始化
        ControlAppInit.getInstance().init(MyApplication.this);
        //注册锁屏广播接收
        registLockReceiver();
        //CrashReport.testJavaCrash();
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
     *
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
}