package com.pcs.ztqtj.view.activity.service;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings.TextSize;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.ShareTools;
import com.pcs.ztqtj.control.tool.ZtqImageTool;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.view.activity.FragmentActivitySZYBBase;

/**
 * 专项服务-决策服务-我的服务-详情
 */
public class ActivityServeDetails extends FragmentActivitySZYBBase {

    private ViewGroup mParent;
    private WebView mWebView;
    private ProgressBar mViewProgress;

    private String url = "";
    private String title = "";
    private String style = "";
    private TextView show_warn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service_info_layout);
        url = getIntent().getStringExtra("url");
        title = getIntent().getStringExtra("title");
        style = getIntent().getStringExtra("style");
        setTitleText(title);
        initView();
        initEvent();
        initData();
    }

    private void initView() {
        mParent = (ViewGroup) findViewById(R.id.web_frame_layout);
        mViewProgress = (ProgressBar) findViewById(R.id.webview_progressbar);
        mWebView = (WebView) findViewById(R.id.webview_browser);
        show_warn = (TextView) findViewById(R.id.show_warn);

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.removeAllViews();
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                mViewProgress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                mViewProgress.setVisibility(View.GONE);
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int progress) {
                mViewProgress.setProgress(progress);
                super.onProgressChanged(view, progress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }
        });

        WebSettings webSettings = mWebView.getSettings();
//		webSettings.setJavaScriptEnabled(true);
//		webSettings.setBuiltInZoomControls(true);
//		webSettings.setBlockNetworkImage(false);
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setSupportZoom(true);
        webSettings.setTextZoom(100);
        webSettings.setDisplayZoomControls(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setLoadWithOverviewMode(true);
        if ("1".equals(style)) {
            webSettings.setTextSize(TextSize.LARGEST);
        }
        // 设置下载
        mWebView.setDownloadListener(mDownloadListener);
        mWebView.loadUrl(url);
    }

    private void initEvent() {
        show_warn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show_warn.setVisibility(View.GONE);
            }
        });

        setBtnRight(R.drawable.icon_share_new, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //网页分享
                View layout = findViewById(R.id.web_frame_layout).getRootView();
                Bitmap shareBitmap = ZtqImageTool.getInstance().getWebViewBitmap(ActivityServeDetails.this, mWebView);
                if (shareBitmap != null) {
                    shareBitmap = ZtqImageTool.getInstance().stitchQR(ActivityServeDetails.this, shareBitmap);
                }
                if (shareBitmap != null) {
                    ShareTools.getInstance(ActivityServeDetails.this).setShareContent(getTitleText(), CONST.SHARE_URL, shareBitmap,"1").showWindow(layout);
                }
            }
        });
    }

    private void initData() {
        if (title != null && title.contains("决策报告")) {
//			显示提示信息
            show_warn.setVisibility(View.VISIBLE);
        } else {
            show_warn.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWebView != null) {
            mParent.removeView(mWebView);
            WebView w = mWebView;
            w.removeAllViews();
            w.destroy();
            w = null;
            mWebView = null;
        }
    }

    private DownloadListener mDownloadListener = new DownloadListener() {
        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    };

}