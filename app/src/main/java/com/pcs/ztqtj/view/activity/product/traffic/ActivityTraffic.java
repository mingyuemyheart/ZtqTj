package com.pcs.ztqtj.view.activity.product.traffic;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.net.traffic.PackTrafficDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.traffic.PackTrafficUp;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.CommUtils;
import com.pcs.ztqtj.view.activity.FragmentActivityWithShare;
import com.pcs.ztqtj.view.activity.air_quality.AcitvityAirWhatAQI;

/**
 * Created by Administrator on 2017/10/19 0019.
 * chen_jx
 */

public class ActivityTraffic extends FragmentActivityWithShare implements View.OnClickListener{

    private WebView web_weather;
    private LinearLayout lay_traffic_citiao;
    private PackTrafficUp packTrafficUp;
    private PackTrafficDown packTrafficDown;
    private TextView tv_traffic_citiao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            WebView.enableSlowWholeDocumentDraw();
        }
        setContentView(R.layout.activity_imweatherdown);
        initView();
        setTitleText("交通气象");
        initEvent();
    }

    private void initView() {
        web_weather= (WebView) findViewById(R.id.web_weather);
        lay_traffic_citiao= (LinearLayout) findViewById(R.id.lay_traffic_citiao);
        lay_traffic_citiao.setVisibility(View.VISIBLE);
        tv_traffic_citiao= (TextView) findViewById(R.id.tv_traffic_citiao);
    }
    private void initEvent() {
        req();
//        lay_traffic_citiao.setOnClickListener(this);
    }

    private void req() {
        showProgressDialog();
        packTrafficUp=new PackTrafficUp();
        PcsDataDownload.addDownload(packTrafficUp);
    }

    @Override
    protected void onResume() {
        super.onResume();
        CommUtils.closeKeyboard(this);
        PcsDataBrocastReceiver.registerReceiver(ActivityTraffic.this, mReceiver);
    }

    @Override
    protected void onPause() {
        super.onPause();
        PcsDataBrocastReceiver.unregisterReceiver(ActivityTraffic.this, mReceiver);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.lay_traffic_citiao:
                Intent intent = new Intent(ActivityTraffic.this, AcitvityAirWhatAQI.class);
                intent.putExtra("w", packTrafficDown.des);
                intent.putExtra("t", "小词条");
                startActivity(intent);
                break;
        }
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


    private PcsDataBrocastReceiver mReceiver = new PcsDataBrocastReceiver() {
        @Override
        public void onReceive(String nameStr, String errorStr) {
            if (TextUtils.isEmpty(nameStr)) {
                return;
            }
            if (nameStr.equals(packTrafficUp.getName())) {
                dismissProgressDialog();
                packTrafficDown = (PackTrafficDown) PcsDataManager.getInstance().getNetPack(nameStr);
                if (packTrafficDown == null) {
                    return;
                }

                if (TextUtils.isEmpty(packTrafficDown.html_path)){
                    tv_traffic_citiao.setVisibility(View.VISIBLE);
                    tv_traffic_citiao.setText("暂无数据");
                    web_weather.setVisibility(View.GONE);
                }else{
                    tv_traffic_citiao.setVisibility(View.GONE);
                    web_weather.setVisibility(View.VISIBLE);
                    setWebView(packTrafficDown.html_path);
                }

            }
        }
    };

}
