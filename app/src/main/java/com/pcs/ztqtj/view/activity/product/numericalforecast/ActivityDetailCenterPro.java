package com.pcs.ztqtj.view.activity.product.numericalforecast;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

import com.pcs.lib.lib_pcs_v3.control.tool.BitmapUtil;
import com.pcs.lib.lib_pcs_v3.control.tool.Util;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackNumericalForecastColumnDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackNumericalForecastColumnDown.ForList;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackNumericalForecastDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackNumericalForecastDown.TitleListBean;
import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.livequery.AdapterData;
import com.pcs.ztqtj.control.inter.DrowListClick;
import com.pcs.ztqtj.control.tool.ShareTools;
import com.pcs.ztqtj.control.tool.utils.TextUtil;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.util.OkHttpUtil;
import com.pcs.ztqtj.view.activity.FragmentActivitySZYBBase;
import com.pcs.ztqtj.view.myview.ImageTouchView;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 监测预报-指导预报
 */
public class ActivityDetailCenterPro extends FragmentActivitySZYBBase implements OnClickListener {

    private TextView subtitle_tv;
    private TextView spinner_text;
    private TextView n_content;
    private ImageTouchView image_show; // 展示图片
    private LinearLayout number_radio_group;
    private ScrollView content_scrollview;
    private LinearLayout layoutTime;// 选择小时下拉数据布局--是否要隐藏

    /**
     * 显示的数据数据
     */
    private List<TitleListBean> showData;
    /**
     * 头部下下拉标记
     */
    private final int SUBTITLE = 3;
    /**
     * 雨量下拉-几时间段标记
     */
    private final int SPINNERTIME = 0;
    /**
     * 底部左右按钮布局---控制是否显示
     */
    private LinearLayout left_right_btn_layout;
    /**
     * 记录下拉时间选择当前选中的位置
     */
    private int tiemposition = 0;
    private PackNumericalForecastColumnDown packNumericalForecastColumnDown;
    private List<ForList> forList2 = new ArrayList<>();// 二级目录
    final String TIMEUNIT = "小时";
    private Button image_share;
    private LinearLayout layout_detail_center;
    private WebView webview;

    private int screenwidth, screenHight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_numerical_center_pro);
        setBtnRight(R.drawable.btn_num_more, new OnClickListener() {
            @Override
            public void onClick(View v) {
                topRightButtonClick();
            }
        });
        initView();
        initEvent();
        initData();
    }

    /**
     * 右边按钮--标题切换
     */
    private void topRightButtonClick() {
        // 切换标题时单选按钮复原
        raidolmListId = "";
        showPopWindow(SUBTITLE, btnRight);
    }

    private void initView() {
        content_scrollview = (ScrollView) findViewById(R.id.content_scrollview);
        subtitle_tv = (TextView) findViewById(R.id.subtitle_tv);
        spinner_text = (TextView) findViewById(R.id.spinner_text);
        n_content = (TextView) findViewById(R.id.n_content);
        image_show = (ImageTouchView) findViewById(R.id.image_show);
        image_show.setImagePositon(ImageTouchView.StartPostion.ImageCenter);
        number_radio_group = findViewById(R.id.number_radio_group);
        layoutTime = (LinearLayout) findViewById(R.id.spinner_layout);
        left_right_btn_layout = (LinearLayout) findViewById(R.id.left_right_btn_layout);
        image_share = (Button) findViewById(R.id.image_share);
        layout_detail_center = (LinearLayout) findViewById(R.id.layout_detail_center);
        initWebView();
    }

    private void initWebView() {
        webview = (WebView) findViewById(R.id.webview);
        webview.getSettings().setTextZoom(100);
        webview.getSettings().setUseWideViewPort(true);// 设置此属性，可任意比例缩放。
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("http:") || url.startsWith("https:")) {
                    view.removeAllViews();
                    view.loadUrl(url);
                    return false;
                }
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
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setSupportZoom(true);
        webSettings.setBlockNetworkImage(false);
        webSettings.setLoadWithOverviewMode(true);
    }

    private void initData() {
        screenwidth = Util.getScreenWidth(ActivityDetailCenterPro.this);
        screenHight= Util.getScreenHeight(ActivityDetailCenterPro.this);
        Intent intent = getIntent();
        String title = intent.getStringExtra("t");
        String code = intent.getStringExtra("c");
        setTitleText(title);
        // 获取一级code对应的二级列表
        okHttpList();

        showData = new ArrayList<>();
        dataeaum = new ArrayList<>();
    }

    private void initEvent() {
        spinner_text.setOnClickListener(this);
        image_share.setOnClickListener(this);
        findViewById(R.id.image_left).setOnClickListener(this);
        findViewById(R.id.image_right).setOnClickListener(this);
    }

    /**
     * 子标题改变
     * @param position
     */
    private void changeValue(int position) {
        n_content.setText("");
        if (showData.size() > 1) {
            left_right_btn_layout.setVisibility(View.VISIBLE);
            spinner_text.setText(showData.get(position).title + TIMEUNIT);
        } else {
            spinner_text.setText("");
            left_right_btn_layout.setVisibility(View.GONE);
        }
        if (showData.size() > position && showData.get(position).type.equals("1")) {
            // 文字
            content_scrollview.setVisibility(View.VISIBLE);
            image_show.setVisibility(View.GONE);
            webview.setVisibility(View.GONE);
            n_content.setGravity(Gravity.LEFT | Gravity.TOP);
            n_content.setTextColor(getResources().getColor(R.color.text_black));
            n_content.setText(showData.get(position).str);
        } else if (showData.size() > position && showData.get(position).type.equals("2")) {
            // 圖片
            webview.setVisibility(View.GONE);
            content_scrollview.setVisibility(View.GONE);
            image_show.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(showData.get(position).img)) {
                String imgUrl = getString(R.string.file_download_url)+showData.get(position).img;
                Picasso.get().load(imgUrl).into(image_show);
            } else {
                showToast("服务器不存在这张图标");
            }
        } else if (showData.size() > position && showData.get(position).type.equals("3")) {
            content_scrollview.setVisibility(View.GONE);
            image_show.setVisibility(View.GONE);
            webview.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(showData.get(position).html)) {
                String path = getString(R.string.file_download_url) + showData.get(position).html;
                webview.loadUrl(path);
            }
        } else {
            // 空数据
            content_scrollview.setVisibility(View.VISIBLE);
            webview.setVisibility(View.GONE);
            image_show.setVisibility(View.GONE);
            n_content.setGravity(Gravity.CENTER);
            n_content.setText("暂无数据");
            n_content.setTextColor(getResources().getColor(R.color.bg_black_alpha20));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.spinner_text:
                // 具体标题时间选择
                if (showData.size() > 1) {
                    showPopWindow(SPINNERTIME, spinner_text);
                }
                break;
            case R.id.image_left:
                if (showData.size() > 1) {
                    if (tiemposition == 0) {
                        tiemposition = showData.size() - 1;
                    } else {
                        tiemposition--;
                    }
                    changeValue(tiemposition);
                }
                break;
            case R.id.image_right:
                if (showData.size() > 1) {
                    if (tiemposition >= showData.size() - 1) {
                        tiemposition = 0;
                    } else {
                        tiemposition++;
                    }
                    changeValue(tiemposition);
                }
                break;
            case R.id.image_share:
                Bitmap bitmap = BitmapUtil.takeScreenShot(ActivityDetailCenterPro.this);
                ShareTools.getInstance(ActivityDetailCenterPro.this).setShareContent(getTitleText(),CONST.SHARE_URL, bitmap, "0").showWindow(layout_detail_center);
                break;
            default:
                break;
        }
    }

    /**
     * 下拉显示列表
     */
    private List<String> dataeaum;

    public void showPopWindow(int floag, View drowView) {
        dataeaum.clear();
        if (floag == SPINNERTIME) {
            // 下拉时间数据显示
            for (int i = 0; i < showData.size(); i++) {
                dataeaum.add(showData.get(i).title + TIMEUNIT);
            }
            createPopupWindow(dataeaum, floag).showAsDropDown(drowView);
        } else if (floag == SUBTITLE) {
            // 子标题下拉显示
            for (int i = 0; i < forList2.size(); i++) {
                dataeaum.add(forList2.get(i).name);
            }
            createPopupWindow(dataeaum, floag).showAsDropDown(drowView);
        }
    }

    /**
     * 创建下拉选择列表
     */
    public PopupWindow createPopupWindow(final List<String> dataeaum, final int floag) {
        AdapterData dataAdapter = new AdapterData(ActivityDetailCenterPro.this,dataeaum);
        View popcontent = LayoutInflater.from(ActivityDetailCenterPro.this).inflate(R.layout.pop_list_layout, null);
        ListView lv = (ListView) popcontent.findViewById(R.id.mylistviw);
        lv.setAdapter(dataAdapter);
        final PopupWindow pop = new PopupWindow(ActivityDetailCenterPro.this);
        pop.setContentView(popcontent);
        pop.setOutsideTouchable(false);
        pop.setWidth(screenwidth / 2);
        if (dataeaum.size()<10){
            pop.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        }else{
            pop.setHeight(screenHight/2);
        }

        pop.setFocusable(true);
        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                pop.dismiss();
                listener.itemClick(floag, position);
            }
        });
        return pop;
    }

    private DrowListClick listener = new DrowListClick() {
        @Override
        public void itemClick(int floag, int item) {
            tiemposition = item;
            if (floag == SPINNERTIME) {
                changeValue(item);
            } else if (floag == SUBTITLE) {
                try {
                    if (forList2.size() == 0) {
                        return;
                    }
                    subtitle_tv.setText(forList2.get(item).name);
                    okHttpItemColumn(forList2.get(item).id);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    /**
     * 获取模式预报数据
     */
    private void okHttpList() {
        showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param  = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    String json = param.toString();
                    final String url = CONST.BASE_URL+"zdyb_lm_list";
                    Log.e("zdyb_lm_list", url);
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
                            Log.e("zdyb_lm_list", result);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dismissProgressDialog();
                                    try {
                                        JSONObject obj = new JSONObject(result);
                                        if (!obj.isNull("b")) {
                                            JSONObject bobj = obj.getJSONObject("b");
                                            if (!bobj.isNull("forecast_column_n2")) {
                                                JSONObject forecast_column_n2 = bobj.getJSONObject("forecast_column_n2");
                                                if (!TextUtils.isEmpty(forecast_column_n2.toString())) {
                                                    dismissProgressDialog();
                                                    packNumericalForecastColumnDown = new PackNumericalForecastColumnDown();
                                                    packNumericalForecastColumnDown.fillData(forecast_column_n2.toString());
                                                    for (int i = 0; i < packNumericalForecastColumnDown.forlist.size(); i++) {
                                                        if (packNumericalForecastColumnDown.forlist.get(i).parent_id.equals("106")) {
                                                            forList2.add(packNumericalForecastColumnDown.forlist.get(i));
                                                        }
                                                    }
                                                    try {
                                                        if (forList2.size() == 0) {
                                                            return;
                                                        }
                                                        subtitle_tv.setText(forList2.get(0).name);
                                                        okHttpItemColumn(forList2.get(0).id);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
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

    /**
     * 栏目切换、重新取数据
     * 获取分类数据
     */
    private void okHttpItemColumn(final String columnId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final JSONObject param  = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    JSONObject info = new JSONObject();
                    info.put("stationId", columnId);
                    param.put("paramInfo", info);
                    String json = param.toString();
                    Log.e("zdyb_init", json);
                    final String url = CONST.BASE_URL+"zdyb_init";
                    Log.e("zdyb_init", url);
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
                                    Log.e("zdyb_init", result);
                                    if (!TextUtil.isEmpty(result)) {
                                        try {
                                            JSONObject obj = new JSONObject(result);
                                            if (!obj.isNull("b")) {
                                                JSONObject bobj = obj.getJSONObject("b");
                                                if (!bobj.isNull("szyb_new")) {
                                                    JSONObject listobj = bobj.getJSONObject("szyb_new");
                                                    if (!TextUtil.isEmpty(listobj.toString())) {
                                                        dismissProgressDialog();
                                                        PackNumericalForecastDown columnData = new PackNumericalForecastDown();
                                                        columnData.fillData(listobj.toString());
                                                        number_radio_group.removeAllViews();
                                                        int size = columnData.lmBeanList.size();
                                                        if (size > 1) {
                                                            number_radio_group.setVisibility(View.VISIBLE);
                                                            layoutTime.setVisibility(View.VISIBLE);
                                                            left_right_btn_layout.setVisibility(View.VISIBLE);
                                                        } else {
                                                            number_radio_group.setVisibility(View.GONE);
                                                            layoutTime.setVisibility(View.GONE);
                                                            left_right_btn_layout.setVisibility(View.GONE);
                                                        }
                                                        for (int i = 0; i < size; i++) {
                                                            PackNumericalForecastDown.LmListBean bean = columnData.lmBeanList.get(i);
                                                            TextView textView = new TextView(ActivityDetailCenterPro.this);
                                                            textView.setGravity(Gravity.CENTER);
                                                            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                                                            textView.setPadding(0, 30, 0, 30);
                                                            textView.setTextColor(Color.BLACK);
                                                            textView.setText(bean.name);
                                                            textView.setTag(bean.id);
                                                            number_radio_group.addView(textView);
                                                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                                            params.weight = 1;
                                                            textView.setLayoutParams(params);

                                                            textView.setOnClickListener(new OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    for (int j = 0; j < number_radio_group.getChildCount(); j++) {
                                                                        TextView tv = (TextView) number_radio_group.getChildAt(j);
                                                                        if (TextUtils.equals(v.getTag()+"", tv.getTag()+"")) {
                                                                            tv.setBackgroundResource(R.drawable.btn_number_check);
                                                                        } else {
                                                                            tv.setBackgroundColor(getResources().getColor(R.color.bg_rainfall_subtitle));
                                                                        }
                                                                    }
                                                                    raidolmListId = v.getTag()+"";
                                                                    okHttpDetail();
                                                                }
                                                            });

                                                            if (i == 0) {
                                                                textView.setBackgroundResource(R.drawable.btn_number_check);
                                                                raidolmListId = bean.id;
                                                                okHttpDetail();
                                                            } else {
                                                                textView.setBackgroundColor(getResources().getColor(R.color.bg_rainfall_subtitle));
                                                            }
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

    /**
     * 栏目切换、重新取数据
     * 获取分类数据
     */
    private void okHttpDetail() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param  = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    JSONObject info = new JSONObject();
                    info.put("stationId", raidolmListId);
                    param.put("paramInfo", info);
                    String json = param.toString();
                    Log.e("zdyb_init", json);
                    final String url = CONST.BASE_URL+"zdyb_init";
                    Log.e("zdyb_init", url);
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
                                    Log.e("zdyb_init", result);
                                    if (!TextUtil.isEmpty(result)) {
                                        try {
                                            JSONObject obj = new JSONObject(result);
                                            if (!obj.isNull("b")) {
                                                JSONObject bobj = obj.getJSONObject("b");
                                                if (!bobj.isNull("szyb_new")) {
                                                    JSONObject listobj = bobj.getJSONObject("szyb_new");
                                                    if (!TextUtil.isEmpty(listobj.toString())) {
                                                        dismissProgressDialog();
                                                        PackNumericalForecastDown packDown = new PackNumericalForecastDown();
                                                        packDown.fillData(listobj.toString());
                                                        radioSelectChangeSpinner(packDown);
                                                        changeValue(0);
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

    private String raidolmListId = "";

    /**
     * 下拉框显示数据
     */
    private void radioSelectChangeSpinner(PackNumericalForecastDown packDown) {
        showData.clear();
        if (!TextUtils.isEmpty(raidolmListId)) {
            for (int j = 0; j < packDown.TitleBeanList.size(); j++) {
                if (packDown.TitleBeanList.get(j).lm.equals(raidolmListId)) {
                    showData.add(packDown.TitleBeanList.get(j));
                }
            }
        } else {
            for (int j = 0; j < packDown.TitleBeanList.size(); j++) {
                showData.add(packDown.TitleBeanList.get(j));
            }
        }
    }

}