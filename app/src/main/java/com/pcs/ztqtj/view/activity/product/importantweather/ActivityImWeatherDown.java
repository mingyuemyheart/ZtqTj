package com.pcs.ztqtj.view.activity.product.importantweather;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.CommUtils;
import com.pcs.ztqtj.view.activity.FragmentActivityWithShare;

/**
 * Created by Administrator on 2017/10/19 0019.
 * chen_jx
 */

public class ActivityImWeatherDown extends FragmentActivityWithShare {

    private WebView web_weather;
    private String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imweatherdown);
        initView();
        setTitleText("重要天气");
        Intent intent=getIntent();
        url=intent.getStringExtra("url");
        if (!TextUtils.isEmpty(url)){
            setWebView(url);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        CommUtils.closeKeyboard(this);
    }

    private void initView() {
        web_weather= (WebView) findViewById(R.id.web_weather);
    }

    private void setWebView(String url){
        web_weather.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.removeAllViews();
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

        web_weather.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int progress) {
                super.onProgressChanged(view, progress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {

                super.onReceivedTitle(view, title);
            }

        });

        WebSettings webSettings = web_weather.getSettings();
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setSupportZoom(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setLoadWithOverviewMode(true);

        web_weather.loadUrl(url);
    }
}
