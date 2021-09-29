package com.pcs.ztqtj.view.activity.product.typhoon;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.net.column.ColumnInfo;
import com.pcs.lib_ztqfj_v2.model.pack.net.column.PackColumnDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.column.PackColumnUp;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.CommUtils;
import com.pcs.ztqtj.control.tool.ShareTools;
import com.pcs.ztqtj.control.tool.ZtqImageTool;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/11/13 0013.
 * chen_jx
 */

public class ActivityTyphoonRoad extends FragmentActivityZtqBase {

    private WebView web_weather;
    private String name;
    private String url;
    private PackColumnUp packYjZqColumnUp = new PackColumnUp();
    private RelativeLayout head_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBtnRight(R.drawable.icon_share_new, listener);
        setContentView(R.layout.activity_imweatherdown);
        Intent intent = getIntent();
        name = intent.getStringExtra("title");
        url = intent.getStringExtra("url");
        initView();
        //req();
        setTitleText(name);
        if (!TextUtils.isEmpty(url)) {
            setWebView(url);
        }


    }

    public void req() {
        showProgressDialog();
        packYjZqColumnUp.column_type = "25";

        PcsDataDownload.addDownload(packYjZqColumnUp);
    }

    /**
     * 请求分享接口
     */
    private void reqShare() {
        View layout = findViewById(android.R.id.content).getRootView();
        if (layout != null) {
            Bitmap headBm = ZtqImageTool.getInstance().getScreenBitmap(head_layout);
            Bitmap contentBm = ZtqImageTool.getInstance().screenshotWebView(web_weather);
            Bitmap bitmap = ZtqImageTool.getInstance().stitch(headBm, contentBm);
            bitmap = ZtqImageTool.getInstance().stitchQR(ActivityTyphoonRoad.this, bitmap);
            ShareTools.getInstance(ActivityTyphoonRoad.this).setShareContent(getTitleText(), mShare, bitmap, "0").showWindow(layout);
        }
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_right:
                    reqShare();
                    break;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        CommUtils.closeKeyboard(this);
    }

    public List<ColumnInfo> arrcolumnInfo = new ArrayList<>();
    private String mShare = CONST.SHARE_URL;
    private PcsDataBrocastReceiver mReceiver = new PcsDataBrocastReceiver() {
        @Override
        public void onReceive(String nameStr, String errorStr) {
            if (TextUtils.isEmpty(nameStr)) {
                return;
            }
            if (nameStr.equals(packYjZqColumnUp.getName())) {
                dismissProgressDialog();
                PackColumnDown packDowns = (PackColumnDown) PcsDataManager.getInstance().getNetPack(nameStr);
                if (packDowns == null) {
                    return;
                }
                arrcolumnInfo.clear();
                arrcolumnInfo.addAll(packDowns.arrcolumnInfo);
                setWebView(arrcolumnInfo.get(0).req_url);
            }
        }
    };

    private void initView() {
        web_weather = (WebView) findViewById(R.id.web_weather);
        head_layout = (RelativeLayout) findViewById(R.id.head_layout);
    }

    private void setWebView(String url) {
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
