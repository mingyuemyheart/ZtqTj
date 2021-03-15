package com.pcs.ztqtj.view.activity.web;

import android.webkit.JavascriptInterface;

/**
 * Created by tyaathome on 2017/2/23.
 */

public class JsTravelCommitInterface implements JSInterface {

    private ActivityWebSh activity;

    public JsTravelCommitInterface(ActivityWebSh activity) {
        this.activity = activity;
    }


//    @JavascriptInterface
//    public void login() {
//        Intent intent = new Intent(activity, ActivityPhotoLogin.class);
//        activity.startActivityForResult(intent, MyConfigure.RESULT_LOGIN);
//    }

    @JavascriptInterface
    public String JsGetDatasFromApp() {
        String json = activity.getDatasFromApp();
        return activity.getDatasFromApp();
    }

}
