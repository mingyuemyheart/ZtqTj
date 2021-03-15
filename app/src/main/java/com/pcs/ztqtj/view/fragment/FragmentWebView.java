package com.pcs.ztqtj.view.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.ShareTools;
import com.pcs.ztqtj.control.tool.ZtqImageTool;

/**
 * 网页fragment
 * Created by tyaathome on 2016/6/14.
 */
public class FragmentWebView extends Fragment {

    private WebView webView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState) {
        return LayoutInflater.from(getActivity()).inflate(R.layout.fragment_webview, null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initEvent();
        initData();
    }

    private void initView() {
        webView = (WebView) getView().findViewById(R.id.webview);
    }

    private void initEvent() {
        webView.getSettings().setUseWideViewPort(true);// 设置此属性，可任意比例缩放。
        webView.getSettings().setTextZoom(100);
        webView.setWebViewClient(new WebViewClient() {
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
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setSupportZoom(true);
        webSettings.setBlockNetworkImage(false);

        getView().findViewById(R.id.tv_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String shareContent = getArguments().getString("title", "");
                RelativeLayout content = (RelativeLayout) getView().findViewById(R.id.content);
                Bitmap bitmap = ZtqImageTool.getInstance().getScreenBitmap(content);
                bitmap = ZtqImageTool.getInstance().stitchQR(getActivity(), bitmap);
                ShareTools.getInstance(getActivity()).setShareContent("",shareContent, bitmap,"0").showWindow(content);
            }
        });
    }

    private void initData() {
        // 获取url
        String url = getArguments().getString("url");
        if(TextUtils.isEmpty(url)) {
            return ;
        }
        webView.loadUrl(url);
    }
}
