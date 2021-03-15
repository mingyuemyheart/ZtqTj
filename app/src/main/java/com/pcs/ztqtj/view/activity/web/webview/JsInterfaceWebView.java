package com.pcs.ztqtj.view.activity.web.webview;

import android.webkit.JavascriptInterface;


public class JsInterfaceWebView {
    private ActivityWebView activity;

    /**
     * Instantiate the interface and set the context
     */
    public JsInterfaceWebView(ActivityWebView a) {
        activity = a;
    }


    @JavascriptInterface
    public void commitPicture(String act_id) {
        activity.showPowChose(act_id);
    }

    @JavascriptInterface
    public void commitMovice(String id, String act_id) {
        activity.showFileChooser(id, act_id);
    }

    /**
     * Show a toast from the web page
     */
    @JavascriptInterface
    public void shareUrlAndContent(String act_id, String type) {
        activity.openShare(act_id, type);
    }

    @JavascriptInterface
    public void shareUrlAndContent() {
        activity.openShare("", "");
    }
    @JavascriptInterface
    public void myShare(String act_id, String type) {
        activity.openShare(act_id, type);
//        Log.i("z", "---------------share");
//        <script type="text/javascript">
//             function showShareInterface(shareContent) {
//            javascript:js.showToast(shareContent);
//        }
//        </script>
    }
    @JavascriptInterface
    public void share() {
        activity.openShare("", "");
    }
    @JavascriptInterface
    public void share(String act_id, String type) {
        activity.openShare(act_id, type);
//        Log.i("z", "---------------share");
//        <script type="text/javascript">
//             function showShareInterface(shareContent) {
//            javascript:js.showToast(shareContent);
//        }
//        </script>
    }


    @JavascriptInterface
    public void login() {
        activity.toLoginActivity();
    }

    @JavascriptInterface
    public String JsGetDatasFromApp() {
        String json = activity.getDatasFromApp();
        return activity.getDatasFromApp();
    }
}
