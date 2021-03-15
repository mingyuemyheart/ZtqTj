package com.pcs.ztqtj.view.activity.web;//package com.pcs.knowing_weather.view.activity.web;
//
//import android.graphics.Bitmap;
//import android.webkit.WebChromeClient;
//import android.webkit.WebSettings;
//import android.webkit.WebView;
//import android.webkit.WebViewClient;
//
//import com.pcs.knowing_weather.control.tool.KWHttpRequest;
//import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
//import com.pcs.lib.lib_pcs_v3.model.pack.PcsPackUp;
//import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalUrl;
//import com.pcs.lib_ztqfj_v2.model.pack.net.PackInitDown;
//import com.pcs.lib_ztqfj_v2.model.pack.net.PackInitUp;
//
///**
// * Created by Z on 2017/1/19.
// */
//
//public class ControlCommitPicMv {
//    private ActivityCommitPicMV activity;
//
//    private KWHttpRequest.KwHttpRequestListener listener;
//
//    public ControlCommitPicMv(ActivityCommitPicMV activity, KWHttpRequest.KwHttpRequestListener listener) {
//        this.activity = activity;
//        init();
//    }
//
//    /**
//     * 上传数据+文件
//     */
//    private KWHttpRequest mKWHttpRequest;
//
//    public void init() {
//        PackLocalUrl packUrl = (PackLocalUrl) PcsDataManager.getInstance().getLocalPack(PackLocalUrl.KEY);
//        PackInitUp initUp = new PackInitUp();
//        PackInitDown packInit = (PackInitDown) PcsDataManager.getInstance().getNetPack(initUp.getName());
//        mKWHttpRequest = new KWHttpRequest(activity);
//        mKWHttpRequest.setURL(packUrl.url);
//        mKWHttpRequest.setmP(packInit.pid);
//        mKWHttpRequest.setListener(0, listener);
//    }
//
//    public void destory() {
//        if (mKWHttpRequest != null) {
//            mKWHttpRequest.setListener(0, null);
//        }
//    }
//
//    public void commitFile(String filePath, PcsPackUp packUp) {
//        // 请求网络
//        mKWHttpRequest.setFilePath(filePath, KWHttpRequest.FILETYPE.VIDEO);
//        mKWHttpRequest.addDownload(packUp);
//        mKWHttpRequest.startAsynchronous();
//    }
//
//    public void initWebView(WebView webview) {
//        webview.setWebViewClient(new WebViewClient() {
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                view.loadUrl(url);
//                return true;
//            }
//
//            @Override
//            public void onPageStarted(WebView view, String url, Bitmap favicon) {
//
//            }
//
//            @Override
//            public void onPageFinished(WebView view, String url) {
//
//            }
//        });
//
//        webview.setWebChromeClient(new WebChromeClient() {
//            @Override
//            public void onProgressChanged(WebView view, int progress) {
//                super.onProgressChanged(view, progress);
//            }
//
//            @Override
//            public void onReceivedTitle(WebView view, String title) {
//                super.onReceivedTitle(view, title);
//            }
//        });
//        WebSettings webSettings = webview.getSettings();
//        webSettings.setJavaScriptEnabled(true);//允许js
//        webSettings.setBlockNetworkImage(false);//后台处理加载图片
//        JsCommitFileInterface interBanner = new JsCommitFileInterface(activity);
//        webview.addJavascriptInterface(interBanner, "js");
//        webview.setDrawingCacheEnabled(true);
//    }
//
//}
