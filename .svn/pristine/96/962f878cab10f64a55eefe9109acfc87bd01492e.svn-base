package com.pcs.ztqtj.control.tool;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityMain;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalInit;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackQueryPushTagDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackQueryPushTagUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.push.PushTag;
import com.pcs.lib_ztqfj_v2.model.pack.net.push.SetPushTagDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.push.SetPushTagUp;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;
import com.tencent.android.tpush.common.Constants;

/**
 * 知天气推送设置工具
 *
 * @author JiangZY
 */
public class ZtqPushTool {
    private static ZtqPushTool instance = null;
    // 是否成功
    private boolean mIsSucc = false;
    // 当前推送城市ID
    private String mCurrCityId = "";
    private String mCityId = ""; // 城市ID
    private String mCityName = ""; // 城市名称
    private String mThreeCityId = ""; // 第三级城市ID
    private String mThreeCityName = ""; // 第三级城市名称
    // 上传包
    private SetPushTagUp mSetPushTagUp = new SetPushTagUp();
    // 下载包
    private SetPushTagDown mSetPushTagDown = new SetPushTagDown();
    // 广播接收
    private PushReceiver mPushReceiver = new PushReceiver();

    private ZtqPushTool() {

    }

    public static ZtqPushTool getInstance() {
        if (instance == null) {
            instance = new ZtqPushTool();
        }

        return instance;
    }

    /**
     * 刷新推送
     */
    public void refreshPush() {
        if (mIsSucc) {
            setPushTag();
            return;
        }

        Context context = MyApplication.application;
        // 当前推送城市ID
        mCurrCityId = (String) LocalDataHelper.getPushTag(context,
                PushTag.getInstance().PUSHTAG_QXYJ_CITY, String.class);
        // 广播接收
        PcsDataBrocastReceiver.registerReceiver(context, mPushReceiver);
        XGPushConfig.enableOtherPush(context, true);
        XGPushConfig.setHuaweiDebug(true);
        XGPushConfig.enableDebug(context, true);// 信鸽debug日志开关
        // 注册接口
        XGPushManager.registerPush(context, new XGIOperateCallback() {
            @Override
            public void onSuccess(Object data, int flag) {
                Log.w(Constants.LogTag, "+++ register push sucess. token:"
                        + data);
                mIsSucc = true;
                setPushTag();

            }

            @Override
            public void onFail(Object data, int errCode, String msg) {
                Log.w(Constants.LogTag, "+++ register push fail. token:" + data
                        + ", errCode:" + errCode + ",msg:" + msg);
            }
        });
    }

    private String token;

    private void setPushTag() {
        Context context = MyApplication.application;
        token=XGPushConfig.getToken(context);;
        PackLocalCityMain cityMain = ZtqCityDB.getInstance().getCityMain();
        if (cityMain == null || TextUtils.isEmpty(cityMain.PARENT_ID)) {
            return;
        }
        // 与当前推送一致
        if (mCurrCityId.equals(cityMain.PARENT_ID)) {
            return;
        }
        mThreeCityId = cityMain.ID;
        mThreeCityName = cityMain.NAME;
        mCityId = cityMain.ID;
        mCityName = cityMain.NAME;

//        Map<String, String> params = new HashMap<String, String>();
//        params.put(PushTag.getInstance().PUSHTAG_WARNING_CITY, mThreeCityId);
//        params.put(PushTag.getInstance().PUSHTAG_QXYJ_CITY, mCityId);
//        params.put(PushTag.getInstance().PUSHTAG_WEATHER_FORECAST_CITY, mCityId);
//        // 气象服务
//        params.put(PushTag.getInstance().PUSHTAG_QXFW_CITY, mCityId);
//        mSetPushTagUp.token = XGPushConfig.getToken(context);
//        mSetPushTagUp.params = params;
//        mSetPushTagUp.key = "";
////        Toast.makeText(context,XGPushConfig.getToken(context),Toast.LENGTH_SHORT).show();
//        if (!TextUtils.isEmpty(mSetPushTagUp.token)) {
//            //PcsDataDownload.addDownload(mSetPushTagUp);
//        }
        PcsDataBrocastReceiver.registerReceiver(context, mPushReceiver);
        PackLocalInit packLocalInit = (PackLocalInit) PcsDataManager.getInstance().getLocalPack(PackLocalInit.KEY);

//        if(!packLocalInit.isRegestPush) {
        PackQueryPushTagUp packQueryPushTagUp = new PackQueryPushTagUp();
        packQueryPushTagUp.token = token;
        PcsDataDownload.addDownload(packQueryPushTagUp);

        packLocalInit.isRegestPush = true;
        PcsDataManager.getInstance().saveLocalData(PackLocalInit.KEY, packLocalInit);
//        }

//        String token = XGPushConfig.getToken(context);
//
//        LocalDataHelper.initPushTag(context, mCityId, mCityName,
//                token, mThreeCityId, mThreeCityName,
//                packLocalInit.isRegestPush);
//        // 当前推送城市ID
//        mCurrCityId = mCityId;
    }

    /**
     * 推送广播接收
     *
     * @author JiangZY
     */
    private class PushReceiver extends PcsDataBrocastReceiver {
        @Override
        public void onReceive(String name, String errorStr) {
            if (TextUtils.isEmpty(name)) {
                // 名字NULL
                return;
            }
            if (!TextUtils.isEmpty(errorStr)) {
                // 出错
                return;
            }
            if (PackQueryPushTagUp.NAME.equals(name)) {
                PackQueryPushTagDown serverDataDown = (PackQueryPushTagDown) PcsDataManager.getInstance().getNetPack
                        (name);
                if (serverDataDown == null) {
                    return;
                }
                PackLocalCityMain cityMain = ZtqCityDB.getInstance().getCityMain();
                SetPushTagUp up = new SetPushTagUp();
                up.token = token;
                serverDataDown.hashMap.put("warning_city",cityMain.ID);
                serverDataDown.hashMap.put("yjxx_city",cityMain.ID);
                serverDataDown.hashMap.put("weatherForecast_city",cityMain.ID);
                up.params = serverDataDown.hashMap;
                up.isarea = "0";
                PcsDataDownload.addDownload(up);

            } else {
                if (mSetPushTagUp.getName().equals(name)) {
                    if ("1".equals(getPushResult(name))) {
//                    Context context = MyApplication.application;
//                    PackLocalInit packLocalInit = (PackLocalInit) PcsDataManager.getInstance().getLocalPack
// (PackLocalInit.KEY);
//                    if (packLocalInit == null) {
//                        return;
//                    }
//                    String token = XGPushConfig.getToken(context);
//                    LocalDataHelper.initPushTag(context, mCityId, mCityName,
//                            token, mThreeCityId, mThreeCityName,
//                            packLocalInit.isRegestPush);
//                    // 当前推送城市ID
//                    mCurrCityId = mCityId;
                    }
                    PcsDataBrocastReceiver.unregisterReceiver(MyApplication.application, mPushReceiver);
                }
            }
        }

        private String getPushResult(String name) {
            mSetPushTagDown = (SetPushTagDown) PcsDataManager.getInstance().getNetPack(name);
            if (mSetPushTagDown == null) {
                return "-1";
            }

            return mSetPushTagDown.result;
        }
    }
}
