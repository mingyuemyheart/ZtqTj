package com.pcs.ztqtj.view.activity.web;

import android.content.Intent;
import android.webkit.JavascriptInterface;

import com.pcs.ztqtj.control.tool.MyConfigure;
import com.pcs.ztqtj.view.activity.photoshow.ActivityLogin;

/**
 * Created by tyaathome on 2017/2/23.
 */

public class JsWeatherDayCommitInterface implements JSInterface {

    private ActivityWeatherDay activity;

    public JsWeatherDayCommitInterface(ActivityWeatherDay activity) {
        this.activity = activity;
    }

    @JavascriptInterface
    public void commitPicture(String act_id) {
        activity.showPowChose(act_id);
    }

    @JavascriptInterface
    public void login() {
        Intent intent = new Intent(activity, ActivityLogin.class);
        activity.startActivityForResult(intent, MyConfigure.RESULT_LOGIN);
    }

    @JavascriptInterface
    public String JsGetDatasFromApp() {
        String json = activity.getDatasFromApp();
        return activity.getDatasFromApp();
    }

    @JavascriptInterface
    public void shareUrlAndContent(String url, String content) {
        activity.shareUrlAndContent(url, content);
    }

}
