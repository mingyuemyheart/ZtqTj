package com.pcs.ztqtj.view.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.pcs.ztqtj.R;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackBannerDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackBannerUp;

public class ActivityBanner extends FragmentActivityZtqBase {
	private PackBannerDown packBannerDown;
	private PackBannerUp packBannerUp;
	private WebView show_webview;
	// private MyReceiver receiver = new MyReceiver();
	private Button qingyun_back;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_banner);
		// 注册广播接收
		// PcsDataBrocastReceiver.registerReceiver(this, receiver);
		initView();
		initData();
		initEvent();
		// request(getIntent().getStringExtra("url"));
	}

	private void initEvent() {
		qingyun_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				show_webview.goBack();
			}
		});
	}

	private void initData() {
		setTitleText(getIntent().getStringExtra("title"));
		String url = getIntent().getStringExtra("url");
		if (!TextUtils.isEmpty(url)) {
			show_webview.loadUrl(url);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// if (receiver != null) {
		// PcsDataBrocastReceiver.unregisterReceiver(this, receiver);
		// }
		// qingyun_back.setVisibility(View.GONE);
	}

	private void initView() {
		qingyun_back = (Button) findViewById(R.id.qingyun_back);
		qingyun_back.setVisibility(View.VISIBLE);
		show_webview = (WebView) findViewById(R.id.show_webview);
		// phonelist = (ImageButton) findViewById(R.id.phonelist);
		// phonelist.setVisibility(View.VISIBLE);
		show_webview.getSettings().setUseWideViewPort(true);// 设置此属性，可任意比例缩放。
		WebSettings ws = show_webview.getSettings();
		//ws.setBuiltInZoomControls(true);
		//ws.setSupportZoom(true);// 支持缩放
		ws.setJavaScriptEnabled(true);// 支持js
		ws.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);// 适应屏幕，内容将自动缩放
		show_webview.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});
	}

	// private void request(String id) {
	// showProgressDialog();
	// packBannerUp=new PackBannerUp();
	// packBannerUp.position_id=id;
	// PcsDataDownload.addDownload(packBannerUp);
	// }
	// /**解析数据**/
	// private void analysis(){
	// String str =
	// PcsDataManager.getInstance().loadData(packBannerUp.getName());
	// if (str == null) {
	// return;
	// }
	// packBannerDown = new PackBannerDown();
	// packBannerDown.fillData(str);
	// }
	// private class MyReceiver extends PcsDataBrocastReceiver {
	// @Override
	// public void onReceive(String nameStr, String errorStr) {
	// dismissProgressDialog();
	// if (packBannerUp.getName().equals(nameStr)) {
	// analysis();
	// if(packBannerDown.arrBannerInfo.size()>0){
	// show_webview.loadUrl(packBannerDown.arrBannerInfo.get(0).url);
	// }
	// }
	// }
	//
	// }
}
