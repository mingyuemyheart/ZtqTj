package com.pcs.ztqtj.control.tool.youmeng;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONObject;

/**
 * Created by Z on 2016/6/21.
 * 1.创建一个工程，并把open-sdk.jar文件和mta_sdk_x.x.x.jar文件拷贝到libs（或lib）目录下
 * <p/>
 * <p/>
 * <p/>
 * xml：
 * <activity
 * android:name="com.tencent.tauth.AuthActivity"
 * android:noHistory="true"
 * android:launchMode="singleTask" >
 * <intent-filter>
 * <action android:name="android.intent.action.VIEW" />
 * <category android:name="android.intent.category.DEFAULT" />
 * <category android:name="android.intent.category.BROWSABLE" />
 * <data android:scheme="tencent你的AppId" />
 * </intent-filter>
 * </activity>
 * <activity android:name="com.tencent.connect.common.AssistActivity"
 * android:theme="@android:style/Theme.Translucent.NoTitleBar"
 * android:configChanges="orientation|keyboardHidden|screenSize"
 * />
 */
public class ToolQQPlatform {
    private static Tencent mTencent;
    private String appID = "100880589";
    private Activity activity;

    public ToolQQPlatform(Activity activity, IUiListener listener) {
        this.activity = activity;
        this.uiListener = listener;
        mTencent = Tencent.createInstance(appID, activity);
    }


    public void setActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_LOGIN || requestCode == Constants.REQUEST_APPBAR) {
            Tencent.onActivityResultData(requestCode, resultCode, data, loginListener);
        }
    }

    /*qq登录*/
    public void login() {
        if (!mTencent.isSessionValid()) {
            mTencent.login(activity, "all", loginListener);
        } else {
            mTencent.logout(activity);
            mTencent.login(activity, "all", loginListener);
        }
    }

    /*退出登录*/
    public void logout(Activity activity) {
        mTencent.logout(activity);
    }


    private IUiListener loginListener = new BaseUiListener() {
        @Override
        protected void doComplete(JSONObject values) {
            initOpenidAndToken(values);
            updateUserInfo();
        }
    };


    public String getOpenId(){
        return mTencent.getOpenId();
    }

    private IUiListener uiListener;
    private UserInfo mInfo;

    private void updateUserInfo() {
        if (mTencent != null && mTencent.isSessionValid()) {
            mInfo = new UserInfo(activity, mTencent.getQQToken());
            mInfo.getUserInfo(uiListener);
        } else {
            uiListener.onError(new UiError(10000, "错误", "客户端调用错误"));
        }
    }


    public static void initOpenidAndToken(JSONObject jsonObject) {
        try {
            String token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN);
            String expires = jsonObject.getString(Constants.PARAM_EXPIRES_IN);
            String openId = jsonObject.getString(Constants.PARAM_OPEN_ID);
            if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires) && !TextUtils.isEmpty(openId)) {
                mTencent.setAccessToken(token, expires);
                mTencent.setOpenId(openId);
            }
        } catch (Exception e) {
        }
    }


    private class BaseUiListener implements IUiListener {

        @Override
        public void onComplete(Object response) {
            if (null == response) {
                Util.showResultDialog(activity, "返回为空", "登录失败");
                return;
            }
            JSONObject jsonResponse = (JSONObject) response;
            if (null != jsonResponse && jsonResponse.length() == 0) {
                Util.showResultDialog(activity, "返回为空", "登录失败");
                return;
            }
//            Util.showResultDialog(ActivityPhotoLogin.this, response.toString(), "登录成功");
            doComplete((JSONObject) response);
        }

        protected void doComplete(JSONObject values) {

        }

        @Override
        public void onError(UiError e) {
            uiListener.onError(e);
//            Util.toastMessage(activity, "onError: " + e.errorDetail);
//            Util.dismissDialog();
        }

        @Override
        public void onCancel() {
//            Util.toastMessage(activity, "onCancel: ");
//            Util.dismissDialog();
            uiListener.onCancel();
        }
    }

}
