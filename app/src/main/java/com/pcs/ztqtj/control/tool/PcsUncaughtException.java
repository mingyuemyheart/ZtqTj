package com.pcs.ztqtj.control.tool;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.pcs.ztqtj.view.activity.loading.ActivityLoading;

/**
 * JiangZy on 2016/8/26.
 */
public class PcsUncaughtException implements Thread.UncaughtExceptionHandler {
    private Context mContext;
    private final String FILE_NAME = "PcsUncaughtException";
    private boolean mIsProc = true;

    private static PcsUncaughtException instance = null;

    private PcsUncaughtException() {
    }

    public static PcsUncaughtException getInstance() {
        if (instance == null) {
            instance = new PcsUncaughtException();
        }

        return instance;
    }

    /**
     * 初始化
     */
    public void init(Context context) {
        mContext = context;
        Thread.setDefaultUncaughtExceptionHandler(this);
        mIsProc = true;
    }

    /**
     * 取消捕获
     */
    public void cancel() {
        mIsProc = false;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!mIsProc) {
            return;
        }
        mIsProc = false;
        ex.printStackTrace();
        // 定时重启
        Intent intent = new Intent(mContext, ActivityLoading.class);
        PendingIntent restartIntent = PendingIntent.getActivity(mContext, 0,
                intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager mgr = (AlarmManager) mContext
                .getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000,
                restartIntent);
        // 退出
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }
}