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

import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackShareAboutDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackShareAboutUp;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.ShareTools;
import com.pcs.ztqtj.control.tool.ZtqImageTool;
import com.pcs.ztqtj.control.tool.youmeng.ShareUtil;
import com.pcs.ztqtj.view.activity.FragmentActivitySZYBBase;

/**
 * 专项服务-决策服务-我的服务-详情
 */
public class ActivityServeDetails extends FragmentActivitySZYBBase {
    private ViewGroup mParent;
    private WebView mWebView;
    private ProgressBar mViewProgress;
    private TextView tvShare;

    private String url = "";
    private String title = "";
    private String style = "";
    private String article_title = "";

    /*显示是否显示提示信息*/
    private boolean showWarn;

    // 分享包
    private PackShareAboutUp packUp = new PackShareAboutUp();
    private PackShareAboutDown packDown = new PackShareAboutDown();

    private MyRecevice recevice = new MyRecevice();

    private TextView show_warn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service_info_layout);
        url = getIntent().getStringExtra("url");
        title = getIntent().getStringExtra("title");
        style = getIntent().getStringExtra("style");
        article_title = getIntent().getStringExtra("article_title");
        showWarn = getIntent().getBooleanExtra("show_warn", true);

        if (article_title == null) {
            article_title = "";
        }
        setTitleText(title);
        initView();
        initEvent();
        initData();
    }


    private void initView() {
        mParent = (ViewGroup) findViewById(R.id.web_frame_layout);
        mViewProgress = (ProgressBar) findViewById(R.id.webview_progressbar);
        mWebView = (WebView) findViewById(R.id.webview_browser);
        tvShare = (TextView) findViewById(R.id.tv_share);
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
        // 显示SD卡上的测试网页
        // mWebView.loadUrl("file://"
        // + Environment.getExternalStorageDirectory().getPath()
        // + "/test.html");
    }

    private void initEvent() {
        tvShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.tv_share:
                        reqNet();
                        break;
                }
            }
        });
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
                shareBitmap = ZtqImageTool.getInstance().stitchQR(ActivityServeDetails.this, shareBitmap);
                PackShareAboutDown shareDown = (PackShareAboutDown) PcsDataManager.getInstance().getNetPack(PackShareAboutUp.getNameCom());
                String shareContent = "";
                if (shareDown != null) {
                    shareContent = shareDown.share_content;
                }
                ShareTools.getInstance(ActivityServeDetails.this).setShareContent(getTitleText(), shareContent, shareBitmap,"1").showWindow(layout);

            }
        });
    }


    private void initData() {
        if (title != null && title.contains("决策报告")) {
//		if(showWarn){
//			决策报告
//			显示提示信息
            show_warn.setVisibility(View.VISIBLE);
        } else {
            show_warn.setVisibility(View.GONE);
        }

//        show_warn.setVisibility(View.VISIBLE);
        PcsDataBrocastReceiver.registerReceiver(this, recevice);
//        String isShowShare = getIntent().getStringExtra("is_show_share");
//        if(TextUtils.isEmpty(isShowShare)) {
//            tvShare.setVisibility(View.VISIBLE);
//        } else if (isShowShare.equals("0")) {
//            tvShare.setVisibility(View.GONE);
//        } else if(isShowShare.equals("1")) {
//            tvShare.setVisibility(View.VISIBLE);
//        } else {
//            tvShare.setVisibility(View.VISIBLE);
//        }
    }

    /**
     * 分享
     *
     * @param down
     */
    private void share(PackShareAboutDown down) {
        String content = article_title + down.share_content;
        View v = findViewById(R.id.webview_browser);
        Bitmap bitmap = ZtqImageTool.getInstance().getScreenBitmap(v);
        ShareUtil.share(this, content, bitmap);
        //ShareUtil.share(ActivityServeDetails.this, content, bitmap);
        //Intent intent = new Intent(this, WeiboShareActivity.class);
        //startActivity(intent);
        //ShareUtil.weiboShare(this, content, bitmap, "");
    }

    private void reqNet() {
        if (!isOpenNet()) {
            showToast(getString(R.string.net_err));
            return;
        }
        packUp = new PackShareAboutUp();
        packUp.keyword = "ABOUT_QXFW";
        packDown = (PackShareAboutDown) PcsDataManager.getInstance().getNetPack(packUp.getName());
        if (packDown != null) {
            share(packDown);
        } else {
            PcsDataDownload.addDownload(packUp);
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
        if (recevice != null) {
            PcsDataBrocastReceiver.unregisterReceiver(this, recevice);
            recevice = null;
        }
    }

    private DownloadListener mDownloadListener = new DownloadListener() {
        @Override
        public void onDownloadStart(String url, String userAgent,
                                    String contentDisposition, String mimetype, long contentLength) {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    };

    private class MyRecevice extends PcsDataBrocastReceiver {
        @Override
        public void onReceive(String nameStr, String errorStr) {
            if (nameStr.equals(packUp.getName())) {
                packDown = (PackShareAboutDown) PcsDataManager.getInstance().getNetPack(nameStr);
                if (packDown == null) {
                    showToast("分享失败！");
                    return;
                }

                share(packDown);
            }
        }
    }
}