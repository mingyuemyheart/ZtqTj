package com.pcs.ztqtj.view.activity.product.agriculture;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.ShareTools;
import com.pcs.ztqtj.control.tool.ZtqImageTool;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackShareAboutDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackShareAboutUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.expert.PackExpertDetailDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.expert.PackExpertDetailUp;

/**
 * 农业气象详情页
 * Created by tyaathome on 2016/11/17.
 */
public class ActivityAgricultureWeatherDetail extends FragmentActivityZtqBase {

    private WebView webView;

    private MyReceiver receiver = new MyReceiver();
    private PackExpertDetailUp packExpertDetailUp = new PackExpertDetailUp();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        setTitleText(getIntent().getStringExtra("title"));
        initView();
        initEvent();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            PcsDataBrocastReceiver.unregisterReceiver(this, receiver);
            receiver = null;
        }
    }

    private void initView() {
        webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setTextZoom(100);
        initWebView();

        setBtnRight(R.drawable.icon_share_new, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View layout = findViewById(R.id.layout_content_web).getRootView();
                Bitmap shareBitmap = ZtqImageTool.getInstance().getWebViewBitmap(ActivityAgricultureWeatherDetail.this, webView);
                shareBitmap = ZtqImageTool.getInstance().stitchQR(ActivityAgricultureWeatherDetail.this, shareBitmap);
                PackShareAboutDown shareDown = (PackShareAboutDown) PcsDataManager.getInstance().getNetPack(PackShareAboutUp.getNameCom());
                String shareContent = "";
                if (shareDown != null) {
                    shareContent = shareDown.share_content;
                }
                ShareTools.getInstance(ActivityAgricultureWeatherDetail.this).setShareContent(getTitleText(), shareContent, shareBitmap,"0").showWindow(layout);
            }
        });
    }

    private void initEvent() {

    }

    private void initData() {
        PcsDataBrocastReceiver.registerReceiver(this, receiver);

        req();
    }

    private void initWebView() {
        webView.setWebViewClient(new WebViewClient() {
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

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int progress) {

                super.onProgressChanged(view, progress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {

                super.onReceivedTitle(view, title);
            }

        });
        WebSettings webSettings = webView.getSettings();
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setSupportZoom(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setLoadWithOverviewMode(true);
    }

    /**
     * 请求数据
     */
    private void req() {
        packExpertDetailUp = new PackExpertDetailUp();
        packExpertDetailUp.id = getIntent().getStringExtra("id");
        PcsDataDownload.addDownload(packExpertDetailUp);
    }

    private class MyReceiver extends PcsDataBrocastReceiver {

        @Override
        public void onReceive(String nameStr, String errorStr) {
            if (!TextUtils.isEmpty(errorStr)) {
                return;
            }

            if (nameStr.equals(packExpertDetailUp.getName())) {
                PackExpertDetailDown down = (PackExpertDetailDown) PcsDataManager.getInstance().getNetPack(nameStr);
                if (down == null) {
                    return;
                }

                String url = getResources().getString(R.string.file_download_url) + down.link;
                webView.loadUrl(url);
            }
        }
    }
}
