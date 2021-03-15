package com.pcs.ztqtj.control.loading;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.pcs.ztqtj.control.command.AbstractCommand;
import com.pcs.ztqtj.control.tool.LocalDataHelper;
import com.pcs.lib.lib_pcs_v3.control.log.Log;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalInit;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackInitDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackInitUp;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 请求初始化
 *
 * @author JiangZY
 */
public class CommandReqInit extends AbstractCommand {
    /**
     * 上下文
     */
    private Context mContext;
    /**
     * 初始化上传包
     */
    private PackInitUp packInitUp = new PackInitUp();

//    /**
//     * 初始化下载包
//     */
//    private PackInitDown packInitDown = new PackInitDown();

    /**
     * 当前版本号
     */
    private String versionCode = "";

    public CommandReqInit(Context context) {
        mContext = context;
    }

    @Override
    public void execute() {
        super.execute();
        Log.e("jzy", "执行CommandLoadingInit");
        // 当前版本号
        versionCode = getVersionCode();
        // 检查本地版本
        if (checkLocalVersion() && checkInitDown()) {
            // 成功
            setStatus(Status.SUCC);
            return;
        }

        // 注册广播接收
        PcsDataBrocastReceiver.registerReceiver(mContext,
                mPcsDataBrocastReceiver);
        // 初始化包
        packInitUp.app = "10001";
        TelephonyManager tm = (TelephonyManager) mContext
                .getSystemService(Context.TELEPHONY_SERVICE);
        packInitUp.imei = tm.getDeviceId();
        packInitUp.meid = getDeviceId(2);
        packInitUp.sv = versionCode;
        packInitUp.xh = android.os.Build.MODEL;
        packInitUp.sys = android.os.Build.VERSION.RELEASE;
        packInitUp.sim = tm.getSimSerialNumber();
        packInitUp.androidCode = Settings.Secure.getString(mContext.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        LocalDataHelper.saveMyImei(mContext, "imei", tm.getDeviceId());
        // 请求初始化数据
        PcsDataDownload.addDownload(packInitUp);
    }

    private String getVersionCode() {
        String str = "";
        PackageManager pm = mContext.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(), 0);
            str = String.valueOf(pi.versionCode);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * 检查本地版本
     *
     * @return
     */
    private boolean checkLocalVersion() {
        PackLocalInit packLocalInit = (PackLocalInit) PcsDataManager.getInstance().getLocalPack(PackLocalInit.KEY);
        if (packLocalInit == null || !versionCode.equals(packLocalInit.versionCode)) {
            return false;
        }

        return true;
    }

    /**
     * 检查初始化下载
     *
     * @return
     */
    private boolean checkInitDown() {

        PackInitDown packInitDown = (PackInitDown) PcsDataManager.getInstance().getLocalPack(PackInitUp.NAME);
        if (packInitDown == null || TextUtils.isEmpty(packInitDown.pid)) {
            return false;
        }
        // 设置pid
        PcsDataDownload.setP(packInitDown.pid);
        Log.i("z","get pid  success,pid is"+packInitDown.pid);


        return true;
    }

    private String getDeviceId(int flag) {
        TelephonyManager manager= (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        Method method = null;
        try {
            method = manager.getClass().getMethod("getDeviceId", int.class);
            switch (flag) {
                case 0:
                    return manager.getDeviceId();
                case 1:
                    return (String) method.invoke(manager, 1);
                case 2:
                    return (String) method.invoke(manager, 2);
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 数据广播接收
     */
    private PcsDataBrocastReceiver mPcsDataBrocastReceiver = new PcsDataBrocastReceiver() {
        @Override
        public void onReceive(String name, String error) {
            if (packInitUp.getName().equals(name)) {

                PcsDataBrocastReceiver.unregisterReceiver(mContext,
                        mPcsDataBrocastReceiver);

                if (!TextUtils.isEmpty(error)) {
                    // 失败
                    setStatus(Status.FAIL);
                    return;
                }
                Log.i("z","get pid  success-----------------");
                if (checkInitDown()) {
                    setStatus(Status.SUCC);
                    return;
                }
                // 失败
                setStatus(Status.FAIL);
                return;
            }
        }
    };
}
