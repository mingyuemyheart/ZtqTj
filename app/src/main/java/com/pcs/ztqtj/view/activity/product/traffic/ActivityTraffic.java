package com.pcs.ztqtj.view.activity.product.traffic;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pcs.lib_ztqfj_v2.model.pack.net.traffic.PackTrafficDown;
import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.CommUtils;
import com.pcs.ztqtj.control.tool.utils.TextUtil;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.util.OkHttpUtil;
import com.pcs.ztqtj.view.activity.FragmentActivityWithShare;
import com.pcs.ztqtj.view.activity.air_quality.AcitvityAirWhatAQI;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 专项服务-行业气象-交通气象
 */
public class ActivityTraffic extends FragmentActivityWithShare implements View.OnClickListener{

    private WebView web_weather;
    private LinearLayout lay_traffic_citiao;
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
        okHttpInfoList();
//        lay_traffic_citiao.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        CommUtils.closeKeyboard(this);
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

    /**
     * 获取交通气象
     */
    private void okHttpInfoList() {
        showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String type = getIntent().getStringExtra("type");
                    JSONObject param  = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    JSONObject info = new JSONObject();
                    info.put("stationId", type);
                    info.put("extra", "");
                    param.put("paramInfo", info);
                    String json = param.toString();
                    final String url = CONST.BASE_URL+"info_list";
                    Log.e("info_list", url);
                    RequestBody body = FormBody.create(MediaType.parse("application/json; charset=utf-8"), json);
                    OkHttpUtil.enqueue(new Request.Builder().post(body).url(url).build(), new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        }
                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            if (!response.isSuccessful()) {
                                return;
                            }
                            final String result = response.body().string();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (!TextUtil.isEmpty(result)) {
                                        Log.e("info_list", result);
                                        try {
                                            JSONObject obj = new JSONObject(result);
                                            if (!obj.isNull("b")) {
                                                JSONObject bobj = obj.getJSONObject("b");
                                                if (!bobj.isNull("jtqx_desc")) {
                                                    JSONObject jtqx_desc = bobj.getJSONObject("jtqx_desc");
                                                    if (!TextUtil.isEmpty(jtqx_desc.toString())) {
                                                        dismissProgressDialog();
                                                        packTrafficDown = new PackTrafficDown();
                                                        packTrafficDown.fillData(jtqx_desc.toString());
                                                        if (TextUtils.isEmpty(packTrafficDown.html_path)){
                                                            tv_traffic_citiao.setVisibility(View.VISIBLE);
                                                            tv_traffic_citiao.setText("暂无数据");
                                                            web_weather.setVisibility(View.GONE);
                                                        }else{
                                                            tv_traffic_citiao.setVisibility(View.GONE);
                                                            web_weather.setVisibility(View.VISIBLE);
                                                            setWebView(getString(R.string.file_download_url)+packTrafficDown.html_path);
                                                        }
                                                    }
                                                }
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            });
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
