package com.pcs.ztqtj.control.tool.youmeng;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.pcs.lib.lib_pcs_v3.control.log.Log;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.Map;

/**
 * @author Z 友盟第三方登录
 */
public class LoginAnther {

    private UMShareAPI mShareAPI;
    public LoginAnther(Activity activity) {
        mShareAPI = UMShareAPI.get(activity);
    }

    /**
     * 登录平台
     *
     * @param acitvity
     * @param platform 平台：SHARE_MEDIA.SINA
     */
    public void loginPermission(final Activity acitvity, SHARE_MEDIA platform, UMAuthListener listener) {
        Log.i("z", "--------------" + platform);
        mShareAPI.doOauthVerify(acitvity, platform, listener);
    }

    /**
     * 获取用户信息
     * @param acitvity
     * @param platform
     * @param umListener
     */
    public void getInfo(final Activity acitvity, SHARE_MEDIA platform, UMAuthListener umListener) {
        mShareAPI.getPlatformInfo(acitvity, platform, umListener);
    }
    public void onResult(int requestCode, int resultCode, Intent data) {
        mShareAPI.onActivityResult(requestCode, resultCode, data);
    }
    public boolean isInstance(Activity activity, SHARE_MEDIA platform) {
        return mShareAPI.isInstall(activity, platform);
    }

    /**
     * 注销
     *
     * @param activity
     * @param platform
     */
    public void deletePlath(final Activity activity, SHARE_MEDIA platform) {
        mShareAPI.deleteOauth(activity, platform, new UMAuthListener() {
            @Override
            public void onStart(SHARE_MEDIA share_media) {

            }

            @Override
            public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
                Toast.makeText(activity, "删除成功.", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
                Toast.makeText(activity, "删除失败", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onCancel(SHARE_MEDIA share_media, int i) {

            }
        });
    }

}
