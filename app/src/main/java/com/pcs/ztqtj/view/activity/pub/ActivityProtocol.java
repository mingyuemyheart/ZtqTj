package com.pcs.ztqtj.view.activity.pub;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.pcs.lib.lib_pcs_v3.model.pack.PcsPackDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.pub.PackPropertyDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.pub.PackPropertyUp;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.NetTask;
import com.pcs.ztqtj.control.tool.utils.TextUtil;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
/**
 * 知天气决策版协议
 * @author E.Sun
 * 2015年9月24日
 */
public class ActivityProtocol extends FragmentActivityZtqBase{
	private WebView mWebView;
	private PackPropertyUp upPack;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protocol);
		initView();
		loadData();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	/**
	 * 初始化视图
	 */
	private void initView() {
		initWebView();// 初始化WebView
	}
	
	@SuppressLint("SetJavaScriptEnabled")
	private void initWebView() {
		mWebView = (WebView) findViewById(R.id.webview);
        mWebView.getSettings().setTextZoom(100);
		mWebView.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});
		mWebView.getSettings().setJavaScriptEnabled(true);
	}
	
	/**
	 * 加载数据
	 */
	private void loadData() {
        String url = getIntent().getStringExtra("url");
        if(TextUtil.isEmpty(url)) {
            setTitleText("天津气象软件许可及服务协议");
            if (!isOpenNet()) {
                showToast(getString(R.string.net_err));
                return;
            }
            showProgressDialog();

            upPack = new PackPropertyUp();
            upPack.key = PackPropertyUp.KEY_AGREEMENT;
            NetTask task = new NetTask(this, new NetTask.NetListener() {
                @Override
                public void onComplete(PcsPackDown down) {
                    if(down instanceof PackPropertyDown) {
                        dismissProgressDialog();
                        PackPropertyDown packDown  = (PackPropertyDown) down;
                        // 加载网页
                        mWebView.loadUrl(packDown.value);
                    }
                }
            });
            task.execute(upPack);
        } else {
            setTitleText("用户隐私协议");
            mWebView.loadUrl(url);
        }
	}
}
