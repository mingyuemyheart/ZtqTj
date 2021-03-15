package com.pcs.ztqtj.view.activity.web;//package com.pcs.knowing_weather.view.activity.web;
//
//import android.webkit.JavascriptInterface;
//
//
//public class JsCommitFileInterface {
//    private ActivityCommitPicMV activity;
//    /**
//     * Instantiate the interface and set the context
//     */
//    public JsCommitFileInterface(ActivityCommitPicMV a) {
//        activity = a;
//    }
//
//
//    @JavascriptInterface
//    public void commitPicture(String act_id) {
//        activity.showPowChose(act_id);
//    }
//
//    @JavascriptInterface
//    public void commitMovice(String id,String act_id) {
//        activity.showFileChooser(id,act_id);
//    }
//    /**
//     * Show a toast from the web page
//     */
//    @JavascriptInterface
//    public void shareUrlAndContent() {
//        activity.openShare();
////        Log.i("z", "---------------share");
////        <script type="text/javascript">
////             function showShareInterface(shareContent) {
////            javascript:js.showToast(shareContent);
////        }
////        </script>
//    }
//
//    @JavascriptInterface
//    public void login() {
//        activity.toLoginActivity();
//    }
//
//}
