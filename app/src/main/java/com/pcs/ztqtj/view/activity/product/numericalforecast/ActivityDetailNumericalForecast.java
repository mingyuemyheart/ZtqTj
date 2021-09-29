package com.pcs.ztqtj.view.activity.product.numericalforecast;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.ScrollView;
import android.widget.TextView;

import com.pcs.lib.lib_pcs_v3.control.tool.BitmapUtil;
import com.pcs.lib.lib_pcs_v3.control.tool.Util;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackNumericalForecastColumnDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackNumericalForecastColumnDown.ForList;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackNumericalForecastDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackNumericalForecastDown.LmListBean;
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
 * 监测预报-模式预报-详情界面——除了中央和福建气象台指导预报
 */
public class ActivityDetailNumericalForecast extends FragmentActivitySZYBBase implements OnClickListener {

    private TextView subtitle_tv;
    private TextView spinner_text;
    private TextView spinner_title;
    private TextView n_content;
    private ImageTouchView image_show;
    private RadioGroup number_radio_group;
    private List<String> spinner_data = new ArrayList<>();
    private List<String> spinner_title_data = new ArrayList<>();
    private ScrollView content_scrollview;

    /**
     * 数据解析完成取得源数据
     */
    public List<LmListBean> lmBeanListData = new ArrayList<>();
    public List<List<TitleListBean>> TitleBeanListData = new ArrayList<>();
    /**
     * 头部下拉选项数据
     */
    private List<String> subTitleList = new ArrayList<>();
    /**
     * 时间段标记
     */
    private final int SPINNERTIME = 0;
    /**
     * 详情时间选择标记
     */
    private final int SPINNERSUBTIME = 1;
    /**
     * 头部下下拉标记
     */
    private final int SUBTITLE = 3;
    /**
     * 头部下拉选项源数据
     */
    private List<ForList> forList2 = new ArrayList<>();// 二级目录
    private ImageButton image_left;
    private ImageButton image_right;
    private Button image_share;
    private LinearLayout layout_root;
    private int screenwidth = 0;
    private WebView webview;
    /**
     * 单选按钮第几个被选中
     */
    private int raidoItemSelect = 0;
    /**
     * 记录下拉时间选择当前选中的位置
     */
    private int tiemposition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_numerical_forecast_detail);
        initView();
        initEvent();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initEvent() {
        image_share.setOnClickListener(this);
        spinner_text.setOnClickListener(this);
        spinner_title.setOnClickListener(this);
        image_right.setOnClickListener(this);
        image_left.setOnClickListener(this);
        textButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                createPopupWindow(textButton, subTitleList, SUBTITLE, listener, screenwidth / 2).showAsDropDown(textButton);
            }
        });
        number_radio_group.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        raidoItemSelect = checkedId - 101;
                        tiemposition = 0;
                        changeValue(0);
                    }
                });
    }

    private void initView() {
        content_scrollview = (ScrollView) findViewById(R.id.content_scrollview);
        subtitle_tv = (TextView) findViewById(R.id.subtitle_tv);
        spinner_text = (TextView) findViewById(R.id.spinner_text);
        spinner_title = (TextView) findViewById(R.id.spinner_title);
        n_content = (TextView) findViewById(R.id.n_content);
        image_show = (ImageTouchView) findViewById(R.id.image_show);
        number_radio_group = (RadioGroup) findViewById(R.id.number_radio_group);
        textButton.setTextColor(getResources().getColor(R.color.text_black));
        left_right_btn_layout = (LinearLayout) findViewById(R.id.left_right_btn_layout);
        image_left = (ImageButton) findViewById(R.id.image_left);
        image_right = (ImageButton) findViewById(R.id.image_right);
        layout_root = (LinearLayout) findViewById(R.id.layout_root);
        image_share = (Button) findViewById(R.id.image_share);

        screenwidth = Util.getScreenWidth(ActivityDetailNumericalForecast.this);

        initWebView();

        Intent intent = getIntent();
        String title = intent.getStringExtra("t");
        String parentId = intent.getStringExtra("c");
        String type = intent.getStringExtra("type");
        setTitleText(title);

        okHttpModelList(parentId);
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
        webSettings.setTextZoom(100);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setSupportZoom(true);
        webSettings.setBlockNetworkImage(false);
        webSettings.setLoadWithOverviewMode(true);
    }

    /**
     * 左右按钮布局
     */
    private LinearLayout left_right_btn_layout;

    private DrowListClick listener = new DrowListClick() {
        @Override
        public void itemClick(int floag, int item) {
            try {
                if (floag == SPINNERSUBTIME) {
                    dismissProgressDialog();
                    tiemposition = item;
                    changeValue(item);
                } else if (floag == SPINNERTIME) {
                    dismissProgressDialog();
                    raidoItemSelect = item;
                    spinner_title.setText(spinner_title_data.get(item));
                    changeValue(0);
                } else if (floag == SUBTITLE) {
                    // 栏目切换
                    showProgressDialog();
                    getserverData(item);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.spinner_text:
                // 具体标题时间选择
                if (spinner_data.size() > 1) {
                    createPopupWindow(spinner_text, spinner_data, SPINNERSUBTIME,
                            listener, screenwidth / 2).showAsDropDown(spinner_text);
                }
                break;
            case R.id.spinner_title:
                // 有的有两个下拉框的，时间段选择
                if (spinner_title_data.size() > 1) {
                    createPopupWindow(spinner_title, spinner_title_data,
                            SPINNERTIME, listener, screenwidth).showAsDropDown(
                            spinner_title);
                }
                break;
            case R.id.image_left:
                if (spinner_data.size() > 1) {
                    if (tiemposition == 0) {
                        tiemposition = spinner_data.size() - 1;
                    } else {
                        tiemposition--;
                    }
                    changeValue(tiemposition);
                    spinner_text.setText(spinner_data.get(tiemposition));
                }
                break;
            case R.id.image_right:
                if (spinner_data.size() > 1) {
                    if (tiemposition == spinner_data.size() - 1) {
                        tiemposition = 0;
                    } else {
                        tiemposition++;
                    }
                    changeValue(tiemposition);
                    spinner_text.setText(spinner_data.get(tiemposition));
                }
                break;
            case R.id.image_share:
                Bitmap bitmap = BitmapUtil.takeScreenShot(ActivityDetailNumericalForecast.this);
                ShareTools.getInstance(ActivityDetailNumericalForecast.this).setShareContent(getTitleText(), CONST.SHARE_URL, bitmap, "0").showWindow(layout_root);
                break;
            default:
                break;
        }
    }

    /**
     * 创建下拉选择列表
     */
    public PopupWindow createPopupWindow(TextView dropDownView, final List<String> dataeaum, final int floag, final DrowListClick listener, int popwidth) {
        AdapterData dataAdapter = new AdapterData(ActivityDetailNumericalForecast.this, dataeaum);
        View popcontent = LayoutInflater.from(ActivityDetailNumericalForecast.this).inflate(R.layout.pop_list_layout, null);
        ListView lv = (ListView) popcontent.findViewById(R.id.mylistviw);
        lv.setAdapter(dataAdapter);
        final PopupWindow pop = new PopupWindow(ActivityDetailNumericalForecast.this);
        pop.setContentView(popcontent);
        pop.setOutsideTouchable(false);
        pop.setWidth(popwidth);
        pop.setFocusable(true);
        pop.setHeight(LayoutParams.WRAP_CONTENT);
        // 判断哪里的下拉框
        if (dropDownView.equals(textButton)) {
            pop.setHeight(LayoutParams.WRAP_CONTENT);
        } else if (dropDownView.equals(spinner_text)) {
            if (lv.getCount() < 10) {
                pop.setHeight(android.view.WindowManager.LayoutParams.WRAP_CONTENT);
            } else {
                int screenHight = Util.getScreenHeight(ActivityDetailNumericalForecast.this);
                pop.setHeight((int) (screenHight * 0.55));
            }
        } else if (dropDownView.equals(spinner_title)) {
            if (lv.getCount() < 7) {
                pop.setHeight(android.view.WindowManager.LayoutParams.WRAP_CONTENT);
            } else {
                int screenHight = Util.getScreenHeight(ActivityDetailNumericalForecast.this);
                pop.setHeight((int) (screenHight * 0.45));
            }
        }
        String selName = dropDownView.getText().toString();
        int selNum = 0;
        for (int i = 0; i < dataeaum.size(); i++) {
            if (selName.equals(dataeaum.get(i))) {
                selNum = i;
            }
        }
        lv.setSelection(selNum);
        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                tiemposition = 0;
                pop.dismiss();
                showProgressDialog();
                listener.itemClick(floag, position);

            }
        });
        return pop;
    }

    /**
     * 获取模式预报数据
     */
    private void okHttpModelList(final String parentId) {
        showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param  = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    String json = param.toString();
                    final String url = CONST.BASE_URL+"modelprediction_list";
                    Log.e("modelprediction_list", url);
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
                            Log.e("modelprediction_list", result);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dismissProgressDialog();
                                    try {
                                        JSONObject obj = new JSONObject(result);
                                        if (!obj.isNull("b")) {
                                            JSONObject bobj = obj.getJSONObject("b");
                                            if (!bobj.isNull("forecast_column_n2")) {
                                                JSONObject itemObj = bobj.getJSONObject("forecast_column_n2");
                                                if (!TextUtils.isEmpty(itemObj.toString())) {
                                                    PackNumericalForecastColumnDown down = new PackNumericalForecastColumnDown();
                                                    down.fillData(itemObj.toString());
                                                    for (int i = 0; i < down.forlist.size(); i++) {
                                                        if (down.forlist.get(i).parent_id.equals(parentId)) {
                                                            forList2.add(down.forlist.get(i));
                                                            subTitleList.add(down.forlist.get(i).name);
                                                        }
                                                    }
                                                    textButton.setVisibility(View.VISIBLE);
                                                    getserverData(0);
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
     * @param itemPostion
     */
    private void getserverData(int itemPostion) {
        if (forList2.size() == 0) {
            return;
        }
        subtitle_tv.setText("预报时效");
        int width = (int) (Util.getScreenWidth(this) / 2.0f) - Util.dip2px(this, 20);
        setRightButtonText(R.drawable.bg_drowdown, forList2.get(itemPostion).name, width);
        okHttpModelPic(forList2.get(itemPostion).id);
    }

    /**
     * 获取指导预报数据
     */
    private void okHttpModelPic(final String stationId) {
        showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param  = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    JSONObject info = new JSONObject();
                    info.put("stationId", stationId);
                    param.put("paramInfo", info);
                    String json = param.toString();
                    Log.e("model_pic", json);
                    final String url = CONST.BASE_URL+"model_pic";
                    Log.e("model_pic", url);
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
                                    Log.e("model_pic", result);
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
                                                        dealWidth(packDown);
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
     * 解析完数据处理
     */
    private void dealWidth(PackNumericalForecastDown packDown) {
        lmBeanListData.clear();
        TitleBeanListData.clear();
        for (int i = 0; i < packDown.lmBeanList.size(); i++) {
            lmBeanListData.add(packDown.lmBeanList.get(i));
            List<TitleListBean> TitleBeanList = new ArrayList<>();
            for (int j = 0; j < packDown.TitleBeanList.size(); j++) {
                if (packDown.TitleBeanList.get(j).lm.equals(packDown.lmBeanList.get(i).id)) {
                    TitleBeanList.add(packDown.TitleBeanList.get(j));
                }
            }
            TitleBeanListData.add(TitleBeanList);
        }
        raidoItemSelect = 0;
        changeValue(0);

        tiemposition = 0;
        // 如果为高度场与风场这数据比较场用下拉形式而不用单选形式
        if (lmBeanListData.size() > 0) {
            if (lmBeanListData.size()==1){
                spinner_title.setVisibility(View.GONE);
            }else{
                spinner_title.setVisibility(View.VISIBLE);
            }
            spinner_title_data.clear();
            number_radio_group.setVisibility(View.GONE);
            spinner_title.setText(lmBeanListData.get(0).name);
            for (int i = 0; i < lmBeanListData.size(); i++) {
                spinner_title_data.add(lmBeanListData.get(i).name);
            }
        } else {
            number_radio_group.setVisibility(View.GONE);
            spinner_title.setVisibility(View.GONE);
        }
    }

    /**
     * 子标题改变
     * @param position
     */
    private void changeValue(int position) {
        n_content.setText("");
        spinner_data.clear();
        if (TitleBeanListData.size() == 0) {
            return;
        }
        for (int i = 0; i < TitleBeanListData.get(raidoItemSelect).size(); i++) {
            spinner_data.add(TitleBeanListData.get(raidoItemSelect).get(i).title + "小时");
        }
        if (spinner_data.size() > 1) {
            left_right_btn_layout.setVisibility(View.VISIBLE);
        } else {
            left_right_btn_layout.setVisibility(View.GONE);
        }
        if (spinner_data.size() > 0 && spinner_data.size() > position) {
            spinner_text.setText(spinner_data.get(position));
        } else {
            spinner_text.setText("");
        }

        if (TitleBeanListData.size() > raidoItemSelect && TitleBeanListData.get(raidoItemSelect).size() > position &&
                TitleBeanListData.get(raidoItemSelect).get(position).type.equals("1")) {
            content_scrollview.setVisibility(View.VISIBLE);
            image_show.setVisibility(View.GONE);
            webview.setVisibility(View.GONE);
            n_content.setTextColor(getResources().getColor(R.color.text_black));
            n_content.setText(TitleBeanListData.get(raidoItemSelect).get(position).str);
        } else if (TitleBeanListData.size() > raidoItemSelect && TitleBeanListData.get(raidoItemSelect).size() >
                position && TitleBeanListData.get(raidoItemSelect).get(position).type.equals("2")) {
            content_scrollview.setVisibility(View.GONE);
            image_show.setVisibility(View.VISIBLE);
            webview.setVisibility(View.GONE);
            //showProgressDialog();

            if (!TextUtils.isEmpty(TitleBeanListData.get(raidoItemSelect).get(position).img)) {
                String imgUrl = getString(R.string.file_download_url)+TitleBeanListData.get(raidoItemSelect).get(position).img;
                if (!TextUtils.isEmpty(imgUrl)) {
                    Picasso.get().load(imgUrl).into(image_show);
                }
            } else {
                showToast("服务器不存在这张图标");
            }
        } else if (TitleBeanListData.size() > raidoItemSelect && TitleBeanListData.get(raidoItemSelect).size() >
                position && TitleBeanListData.get(raidoItemSelect).get(position).type.equals("3")) {
            content_scrollview.setVisibility(View.GONE);
            image_show.setVisibility(View.GONE);
            webview.setVisibility(View.VISIBLE);
            //showProgressDialog();
            if (!TextUtils.isEmpty(TitleBeanListData.get(0).get(position).html)) {
                String path = getString(R.string.file_download_url) + TitleBeanListData.get(0).get(position).html;
                webview.loadUrl(path);
            }
        } else {
            content_scrollview.setVisibility(View.VISIBLE);
            image_show.setVisibility(View.GONE);
            webview.setVisibility(View.GONE);
            // 暂无数据
            n_content.setGravity(Gravity.CENTER);
            n_content.setText("暂无数据");
            n_content.setTextColor(getResources().getColor(R.color.bg_black_alpha20));
        }
    }

}