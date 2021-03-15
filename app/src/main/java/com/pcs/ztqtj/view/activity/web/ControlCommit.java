package com.pcs.ztqtj.view.activity.web;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.pcs.ztqtj.control.tool.KWHttpRequest;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib.lib_pcs_v3.model.pack.PcsPackUp;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalUrl;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackInitDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackInitUp;

/**
 * Created by tyaathome on 2017/2/23.
 */

public class ControlCommit {
    private Context context;
    private JSInterface jsInterface;
    private KWHttpRequest.KwHttpRequestListener listener;
    private WebView mWebView;

    public ControlCommit(Context context, WebView webView, JSInterface jsInterface) {
        this.context = context;
        this.jsInterface = jsInterface;
        init(webView);
    }

    /**
     * 上传数据+文件
     */
    private KWHttpRequest mKWHttpRequest;

    public void init(WebView webView) {
        PackLocalUrl packUrl = (PackLocalUrl) PcsDataManager.getInstance().getLocalPack(PackLocalUrl.KEY);
        PackInitUp initUp = new PackInitUp();
        PackInitDown packInit = (PackInitDown) PcsDataManager.getInstance().getNetPack(initUp.getName());
        mKWHttpRequest = new KWHttpRequest(context);
        mKWHttpRequest.setURL(packUrl.url);
        mKWHttpRequest.setmP(packInit.pid);
        mKWHttpRequest.setListener(0, listener);
        initWebView(webView);
    }

    public void destory() {
        if (mKWHttpRequest != null) {
            mKWHttpRequest.setListener(0, null);
        }
    }

    public void commitFile(String filePath, PcsPackUp packUp) {
        // 请求网络
        mKWHttpRequest.setFilePath(filePath, KWHttpRequest.FILETYPE.VIDEO);
        mKWHttpRequest.addDownload(packUp);
        mKWHttpRequest.startAsynchronous();
    }

    @SuppressLint("JavascriptInterface")
    public void initWebView(WebView webview) {
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {

            }

            @Override
            public void onPageFinished(WebView view, String url) {

            }
        });

        webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int progress) {
                super.onProgressChanged(view, progress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {

//                AlertDialog.Builder b2 = new AlertDialog.Builder(context)
//                        .setTitle("Alert").setMessage(message)
//                        .setPositiveButton("ok",
//                                new AlertDialog.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog,
//                                                        int which) {
//                                        result.confirm();
//                                        // MyWebView.this.finish();
//                                    }
//                                });
//
//                b2.setCancelable(false);
//                b2.create();
//                b2.show();

                return super.onJsAlert(view, url, message, result);
            }
        });
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);//允许js
        webSettings.setBlockNetworkImage(false);//后台处理加载图片
        webview.addJavascriptInterface(jsInterface, "js");
        webview.setDrawingCacheEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView = webview;
    }

    /**
     * 封装载入地址方法
     *
     * @param url
     */
    public void loadUrl(String url) {
        if (mWebView != null) {
            mWebView.loadUrl(url);
        }
    }

    /**
     * 获取webview
     *
     * @return
     */
    public WebView getWebView() {
        return mWebView;
    }
}
