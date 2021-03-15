package com.pcs.ztqtj.view.activity.product.waterflood;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.ShareTools;
import com.pcs.ztqtj.control.tool.ZtqImageTool;
import com.pcs.ztqtj.view.activity.FragmentActivityWithShare;

/**
 * 汛情摘要
 * @author tya
 *
 */
public class ActivityFloodSummary extends FragmentActivityWithShare {

	private WebView mWebView = null;
    private String url="http://218.85.78.125:8099/fjfx/xqzy_XqzyIndex.do";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_flood_summary);
		setTitleText(R.string.title_flood_summary);
		initView();
		initEvent();
		initData();
	}


	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	/**
	 * 初始化UI
	 */
	private void initView() {
		mWebView = (WebView) findViewById(R.id.webview);
	}

	/**
	 * 初始化事件
	 */
	private void initEvent() {
        setBtnRightListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View layout = findViewById(R.id.layout).getRootView();
                Bitmap shareBitmap = ZtqImageTool.getInstance().getScreenBitmap(layout);
				shareBitmap = ZtqImageTool.getInstance().stitchQR(ActivityFloodSummary.this, shareBitmap);
                ShareTools.getInstance(ActivityFloodSummary.this).setShareContent(getTitleText(), mShare, shareBitmap,"0").showWindow(layout);
            }
        });
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		initWebView();
	}

	/**
	 * 回退键
	 */
	@Override
	public void onBackPressed() {
		if(mWebView.canGoBack()) {
			mWebView.goBack();
			return ;
		}
		super.onBackPressed();

	}

	/**
	 * 初始化webview
	 */
	private void initWebView() {
		WebSettings settings = mWebView.getSettings();
        settings.setTextZoom(100);
		settings.setUseWideViewPort(true);
		settings.setBuiltInZoomControls(true);
		settings.setSupportZoom(true);
		settings.setJavaScriptEnabled(true);
		settings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		settings.setJavaScriptCanOpenWindowsAutomatically(true);
		settings.setLoadWithOverviewMode(true);

        // 启动缓存
        settings.setAppCacheEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        //settings.setTextSize(TextSize.SMALLEST);



		mWebView.setWebViewClient(new WebViewClient() {
		    @Override
			public boolean shouldOverrideUrlLoading( WebView view,
					 String url) {
				view.loadUrl(url);// 载入网页

				return true;
			}// 重写点击动作,用WebView载入

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
			}
		});

		mWebView.setWebChromeClient(new WebChromeClient() {

			@Override
			public boolean onJsAlert(final WebView view, final String url,
					String message, final JsResult result) {
				// return super.onJsAlert(view, url, message, result);
				new AlertDialog.Builder(ActivityFloodSummary.this)
						.setMessage(message)
						.setPositiveButton(android.R.string.ok,
								new AlertDialog.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										result.confirm();
										view.loadUrl(url);
									}
								}).setCancelable(false).create().show();

				return true;
			}

			@Override
			public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
				return super.onConsoleMessage(consoleMessage);
			}

		});

		mWebView.loadUrl(url);
	}

}
