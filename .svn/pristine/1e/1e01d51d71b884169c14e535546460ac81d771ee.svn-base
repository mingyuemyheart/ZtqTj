package com.pcs.ztqtj.view.activity.web;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.view.activity.FragmentActivityWithShare;

/**
 * Created by tyaathome on 2017/8/18.
 */

public class WebViewWithShare extends FragmentActivityWithShare {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        Intent intent = getIntent();
        String pagetitle = intent.getStringExtra("title");
        String url = intent.getStringExtra("url");
        setTitleText(pagetitle);
        WebView webview = (WebView) findViewById(R.id.webview);
        webview.getSettings().setTextZoom(100);
        webview.getSettings().setUseWideViewPort(true);// 设置此属性，可任意比例缩放。
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if( url.startsWith("http:") || url.startsWith("https:") ) {
                    view.removeAllViews();
                    view.loadUrl(url);
                    return false;
                }

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

        });
        WebSettings webSettings = webview.getSettings();
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setSupportZoom(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setLoadWithOverviewMode(true);
        // 设置下载
        if (!TextUtils.isEmpty(url)) {
            webview.loadUrl(url);
        }

    }

}
