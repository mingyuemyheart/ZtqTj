package com.pcs.ztqtj.view.activity.push;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.pack.PcsPackDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackPullServiceDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackPullServiceUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackShareAboutDown;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.MyConfigure;
import com.pcs.ztqtj.control.tool.NetTask;
import com.pcs.ztqtj.control.tool.ServiceLoginTool;
import com.pcs.ztqtj.control.tool.ShareTools;
import com.pcs.ztqtj.control.tool.SharedPreferencesUtil;
import com.pcs.ztqtj.control.tool.ZtqImageTool;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqWithPhone;

public class ActivityPushServiceNotificationDetails extends FragmentActivityZtqWithPhone {
    private WebView webview;
    private TextView tvShare;
    private TextView show_warnTv;
    private TextView show_warn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_service_detail);
        PcsDataBrocastReceiver.registerReceiver(ActivityPushServiceNotificationDetails.this, receiver);
        initView();
        initEvent();
        initData();
        showProgressDialog();
        ServiceLoginTool.getInstance().reqLoginQuery();
    }

    private String id,title;
    private void initData() {

        Bundle bundle = getIntent().getBundleExtra(MyConfigure.EXTRA_BUNDLE);
//        Bundle bundle=getIntent().getExtras();
        if(bundle != null) {
            setTitleText(bundle.getString("TITLE"));
            title = bundle.getString("TITLE");
            id = bundle.getString("id");
        }


//        Intent intent = getIntent();
//        String title = intent.getStringExtra("title");
//        String id = intent.getStringExtra("id");
//        show_warn = intent.getBooleanExtra("show_warn", false);
//        if (show_warn) {
//            show_warnTv.setVisibility(View.VISIBLE);
//            tvShare.setVisibility(View.GONE);
//        }
        setTitleText(title);
        if (title != null && title.startsWith("决策报告")) {
            show_warnTv.setVisibility(View.VISIBLE);
        } else {
            show_warnTv.setVisibility(View.GONE);
        }

    }

    @SuppressWarnings("deprecation")
    private void initView() {
        show_warn= (TextView) findViewById(R.id.show_warn);
        tvShare = (TextView) findViewById(R.id.tv_share);
        show_warnTv = (TextView) findViewById(R.id.tv_share);
        webview = (WebView) findViewById(R.id.webview);
        webview.setWebViewClient(new WebViewClient() {
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
//        webSettings.setJavaScriptEnabled(true);
//        webSettings.setBuiltInZoomControls(true);
//        webSettings.setSupportZoom(true);
        // webSettings.setBlockNetworkImage(false);
        webSettings.setTextZoom(100);
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setSupportZoom(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setTextSize(WebSettings.TextSize.LARGEST);

    }

    private void initEvent() {
        show_warn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show_warn.setVisibility(View.GONE);
            }
        });
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
    }

    private void reqNet() {
        String title = getIntent().getStringExtra("title");
        title = TextUtils.isEmpty(title) ? "" : title;
        String content = title + CONST.SHARE_URL;
        Bitmap bitmap = ZtqImageTool.getInstance().getScreenBitmap(webview);
//        ShareUtil.share(this, content, bitmap);

        View layout = findViewById(R.id.layout_main);
        View rootView = layout.getRootView();
        ShareTools.getInstance(ActivityPushServiceNotificationDetails.this).setShareContent(content, bitmap,"0").showWindow(rootView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private MyReceiver receiver = new MyReceiver();
    private PackPullServiceUp packup;

    public void req(String id) {

        if(!isOpenNet()){
            showToast(getString(R.string.net_err));
            return ;
        }
        showProgressDialog();
        packup = new PackPullServiceUp();
        packup.id = id;
        SharedPreferencesUtil.putData(id,id);
        NetTask task = new NetTask(this, new NetTask.NetListener() {
            @Override
            public void onComplete(PcsPackDown down) {
                dismissProgressDialog();
                if (down == null) {
                    return;
                }
                PackPullServiceDown serviceDown = (PackPullServiceDown) down;
                SharedPreferencesUtil.putData(serviceDown.html_url,serviceDown.html_url);
                webview.loadUrl(serviceDown.html_url);
            }
        });
        task.execute(packup);



    }

    /**
     * 分享
     *
     * @param down
     */
    private void share(PackShareAboutDown down) {
        String title = getIntent().getStringExtra("title");
        title = TextUtils.isEmpty(title) ? "" : title;
        String content = title + down.share_content;
        Bitmap bitmap = ZtqImageTool.getInstance().getScreenBitmap(webview);
//        ShareUtil.share(this, content, bitmap);

        View layout = findViewById(R.id.layout_main);
        View rootView = layout.getRootView();
        ShareTools.getInstance(ActivityPushServiceNotificationDetails.this).setShareContent(content, bitmap,"0").showWindow(rootView);

        //ShareUtil.share(ActivityServeDetails.this, content, bitmap);
        //Intent intent = new Intent(this, WeiboShareActivity.class);
        //startActivity(intent);
        //ShareUtil.weiboShare(this, content, bitmap, "");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 103:
                showProgressDialog();
                req(id);
                break;
        }
    }

    private class MyReceiver extends PcsDataBrocastReceiver {
        @Override
        public void onReceive(String name, String errorStr) {
            // 取出内容替换

            ServiceLoginTool.getInstance().callback(name, new ServiceLoginTool.CheckListener() {
                @Override
                public void onSuccess() {
                    if(!TextUtils.isEmpty(id)){
                        req(id);
                    }else{
                        dismissProgressDialog();
                        Toast.makeText(ActivityPushServiceNotificationDetails.this, "文章不存在", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFail() {
                    dismissProgressDialog();
                    ServiceLoginTool.getInstance().createAlreadyLogined(ActivityPushServiceNotificationDetails.this);
                }
            });

        }
    }

}
