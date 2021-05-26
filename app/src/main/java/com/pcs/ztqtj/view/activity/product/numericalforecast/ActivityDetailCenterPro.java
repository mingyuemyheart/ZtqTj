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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.ScrollView;
import android.widget.TextView;

import com.pcs.lib.lib_pcs_v3.control.tool.BitmapUtil;
import com.pcs.lib.lib_pcs_v3.control.tool.Util;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib.lib_pcs_v3.model.image.ImageConstant;
import com.pcs.lib.lib_pcs_v3.model.image.ListenerImageLoad;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackNumericalForecast.NumberBean;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackNumericalForecastColumnDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackNumericalForecastColumnDown.ForList;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackNumericalForecastColumnUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackNumericalForecastDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackNumericalForecastDown.TitleListBean;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackNumericalForecastUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackShareAboutDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackShareAboutUp;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.livequery.AdapterData;
import com.pcs.ztqtj.control.inter.DrowListClick;
import com.pcs.ztqtj.control.tool.ShareTools;
import com.pcs.ztqtj.control.tool.utils.TextUtil;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.util.OkHttpUtil;
import com.pcs.ztqtj.view.activity.FragmentActivitySZYBBase;
import com.pcs.ztqtj.view.myview.ImageTouchView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 指导预报
 */
public class ActivityDetailCenterPro extends FragmentActivitySZYBBase implements OnClickListener {
    private TextView subtitle_tv;
    private TextView spinner_text;
    private TextView n_content;
    private ImageTouchView image_show; // 展示图片
    private RadioGroup number_radio_group;
    private PackNumericalForecastUp packup;

    private PackNumericalForecastDown packDown;
    private MyReceiver receiver = new MyReceiver();
    private ScrollView content_scrollview;
    private LinearLayout layoutTime;// 选择小时下拉数据布局--是否要隐藏
    /**
     * 头部下拉选项源数据
     */
    private List<NumberBean> listLeve2;

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
    private PackNumericalForecastColumnUp packNumericalForecastColumnUp = new PackNumericalForecastColumnUp();
    private PackNumericalForecastColumnDown packNumericalForecastColumnDown;
    private List<ForList> forList2 = new ArrayList<ForList>();// 二级目录
    final String TIMEUNIT = "小时";


    private Button image_share;
    private LinearLayout layout_detail_center;

    private WebView webview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_numerical_center_pro);
        createImageFetcher();
        PcsDataBrocastReceiver.registerReceiver(ActivityDetailCenterPro.this,
                receiver);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
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
        number_radio_group = (RadioGroup) findViewById(R.id.number_radio_group);
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
        requestForecastColumn(code);
        showData = new ArrayList<TitleListBean>();
        dataeaum = new ArrayList<String>();
        getserverData(0);

        getShareContext();
    }

    private PackShareAboutUp aboutShare = new PackShareAboutUp();
    private PackShareAboutDown shareDwon;

    private void getShareContext() {
        // 气象产品分享--短信分享
        aboutShare.keyword = "ABOUT_QXCP_DXFW";
        PcsDataDownload.addDownload(aboutShare);
    }

    /**
     * 请求获取对应的二级列表
     **/
    private void requestForecastColumn(String code) {
        if (packNumericalForecastColumnUp == null) {
            return;
        }
        packNumericalForecastColumnDown = (PackNumericalForecastColumnDown) PcsDataManager.getInstance().getNetPack
                (packNumericalForecastColumnUp.getName());
        if (packNumericalForecastColumnDown == null) {
            return;
        }
        for (int i = 0; i < packNumericalForecastColumnDown.forlist.size(); i++) {
            if (packNumericalForecastColumnDown.forlist.get(i).parent_id
                    .equals(code)) {
                forList2.add(packNumericalForecastColumnDown.forlist.get(i));
            }
        }
    }

    private void initEvent() {
        spinner_text.setOnClickListener(this);
        image_share.setOnClickListener(this);
        findViewById(R.id.image_left).setOnClickListener(this);
        findViewById(R.id.image_right).setOnClickListener(this);
        number_radio_group
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        switch (checkedId) {
                            case R.id.raido_six:
                                // getserverData(0);
                                radioButtonSelect(0);
                                break;
                            case R.id.raido_tf:
                                // getserverData(1);
                                radioButtonSelect(1);
                                break;
                        }
                    }
                });
    }

    private String imageKey = "";

    /**
     * 单选按钮点击选中事件
     *
     * @param checkedId
     */
    private void radioButtonSelect(int checkedId) {
        tiemposition=0;
        if (packDown.lmBeanList != null && packDown.lmBeanList.size() >= 2) {
            raidolmListId = packDown.lmBeanList.get(checkedId).id;
        } else {
            raidolmListId = "";
        }
        radioSelectChangeSpinner();
        changeValue(0);
    }

    /**
     * 栏目切换、重新取数据
     *
     * @param itemPostion
     */
    private void getserverData(int itemPostion) {
        if (forList2.size() == 0) {
            return;
        }
        subtitle_tv.setText(forList2.get(itemPostion).name);
        request(forList2.get(itemPostion).id);
    }

    /**
     * 请求数据
     *
     * @param reqCode
     */
    private void request(String reqCode) {
        if (!isOpenNet()) {
            showToast(getString(R.string.net_err));
            return;
        }
        showProgressDialog();
        packup = new PackNumericalForecastUp();
        packup.lm = reqCode;
        PcsDataDownload.addDownload(packup);
    }

    private class MyReceiver extends PcsDataBrocastReceiver {
        @Override
        public void onReceive(String name, String errorStr) {
            if (packup != null && name.equals(packup.getName())) {
                okHttpList(name);
            } else if (aboutShare != null && aboutShare.getName().equals(name)) {
                shareDwon = (PackShareAboutDown) PcsDataManager.getInstance().getNetPack(name);
            }
        }
    }

    /**
     * 获取指导预报数据
     */
    private void okHttpList(final String name) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String url = CONST.BASE_URL+name;
                Log.e("szyb_new", url);
                OkHttpUtil.enqueue(new Request.Builder().url(url).build(), new Callback() {
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
                                    try {
                                        JSONObject obj = new JSONObject(result);
                                        if (!obj.isNull("b")) {
                                            JSONObject bobj = obj.getJSONObject("b");
                                            if (!bobj.isNull("szyb_new")) {
                                                JSONObject listobj = bobj.getJSONObject("szyb_new");
                                                if (!TextUtil.isEmpty(listobj.toString())) {
                                                    dismissProgressDialog();
                                                    packDown = new PackNumericalForecastDown();
                                                    packDown.fillData(listobj.toString());
                                                    if (packDown == null) {
                                                        return;
                                                    }
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
            }
        }).start();
    }

    /**
     * 解析完数据处理
     */
    private void dealWidth(PackNumericalForecastDown packDown) {
        if ("雨量预报".endsWith(subtitle_tv.getText().toString().trim())) {
            layoutTime.setVisibility(View.VISIBLE);
            left_right_btn_layout.setVisibility(View.VISIBLE);
            number_radio_group.setVisibility(View.VISIBLE);
            number_radio_group.check(R.id.raido_six);
            if (packDown.lmBeanList != null && packDown.lmBeanList.size() >= 2) {
                RadioButton rl = (RadioButton) findViewById(R.id.raido_six);
                RadioButton rr = (RadioButton) findViewById(R.id.raido_tf);
                rl.setText(packDown.lmBeanList.get(0).name);
                rr.setText(packDown.lmBeanList.get(1).name);
                raidolmListId = packDown.lmBeanList.get(0).id;
            } else {
                raidolmListId = "";
            }
        } else {
            raidolmListId = "";
            number_radio_group.setVisibility(View.GONE);
            layoutTime.setVisibility(View.GONE);
            left_right_btn_layout.setVisibility(View.GONE);
        }
        radioSelectChangeSpinner();
        changeValue(0);
    }

    /**
     * 雨量预报---小时筛选
     */
    private String raidolmListId = "";

    /**
     * 下拉框显示数据
     */
    private void radioSelectChangeSpinner() {
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

    /**
     * 子标题改变
     *
     * @param position
     */
    private void changeValue(int position) {
        n_content.setText("");
        if (packDown == null) {
            return;
        }
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
            showProgressDialog();
            if (!TextUtils.isEmpty(showData.get(position).img)) {
                SimpleDateFormat sf = new SimpleDateFormat("hh:MM:SS");
                imageKey = showData.get(position).img;
                getImageFetcher().addListener(getImageViewlistener);
                getImageFetcher().loadImage(imageKey, null, ImageConstant.ImageShowType.NONE);
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
            n_content.setTextColor(getResources().getColor(
                    R.color.bg_black_alpha20));
        }
    }

    ListenerImageLoad getImageViewlistener = new ListenerImageLoad() {

        @Override
        public void done(String key, boolean isSucc) {
            if (imageKey.equals(key)) {
                dismissProgressDialog();
                if (isSucc && getImageFetcher().getImageCache() != null) {
                    Bitmap bm = getImageFetcher().getImageCache()
                            .getBitmapFromAllCache(key).getBitmap();
                    image_show.setMyImageBitmap(bm);
                    SimpleDateFormat sf = new SimpleDateFormat("hh:MM:SS");
                    Log.i("z", "finish-time:" + sf.format(new Date()));
                } else {
                    image_show.setMyImageBitmap(null);
                    showToast("图片为空");
                }
            }

        }
    };

    private DrowListClick listener = new DrowListClick() {
        @Override
        public void itemClick(int floag, int item) {
            tiemposition=item;
            if (floag == SPINNERTIME) {
                changeValue(item);
            } else if (floag == SUBTITLE) {
                // 栏目切换
                getserverData(item);
            }
        }
    };

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
                if (shareDwon == null) {
                    shareDwon = (PackShareAboutDown) PcsDataManager.getInstance().getNetPack(aboutShare.getName());
                }
                if (shareDwon == null) {
                    return;
                }
                String chareContent = shareDwon.share_content;
                Bitmap bitmap = BitmapUtil.takeScreenShot(ActivityDetailCenterPro.this);
                ShareTools.getInstance(ActivityDetailCenterPro.this).setShareContent(getTitleText(),chareContent, bitmap, "0")
                        .showWindow(layout_detail_center);
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

    private int screenwidth;
    private int screenHight ;
    /**
     * 创建下拉选择列表
     */
    public PopupWindow createPopupWindow(final List<String> dataeaum,
                                         final int floag) {
        AdapterData dataAdapter = new AdapterData(ActivityDetailCenterPro.this,
                dataeaum);
        View popcontent = LayoutInflater.from(ActivityDetailCenterPro.this)
                .inflate(R.layout.pop_list_layout, null);
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
}