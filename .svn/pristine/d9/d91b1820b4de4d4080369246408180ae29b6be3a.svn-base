package com.pcs.ztqtj.view.activity.product.numericalforecast;//package com.pcs.knowing_weather.view.activity.product.numericalforecast;
//
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.webkit.WebChromeClient;
//import android.webkit.WebSettings;
//import android.webkit.WebView;
//import android.webkit.WebViewClient;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemClickListener;
//import android.widget.Button;
//import android.widget.ImageButton;
//import android.widget.LinearLayout;
//import android.widget.LinearLayout.LayoutParams;
//import android.widget.ListView;
//import android.widget.PopupWindow;
//import android.widget.ScrollView;
//import android.widget.TextView;
//
//import com.pcs.knowing_weather.R;
//import com.pcs.knowing_weather.control.adapter.livequery.AdapterData;
//import com.pcs.knowing_weather.control.inter.DrowListClick;
//import com.pcs.knowing_weather.control.tool.ShareTools;
//import com.pcs.knowing_weather.view.activity.FragmentActivityZtqBase;
//import com.pcs.knowing_weather.view.myview.ImageTouchView;
//import com.pcs.lib.lib_pcs_v3.control.tool.BitmapUtil;
//import com.pcs.lib.lib_pcs_v3.control.tool.Util;
//import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
//import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
//import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
//import com.pcs.lib.lib_pcs_v3.model.image.ImageConstant;
//import com.pcs.lib.lib_pcs_v3.model.image.ListenerImageLoad;
//import com.pcs.lib_ztqfj_v2.model.pack.net.PackNumericalForecastColumnDown;
//import com.pcs.lib_ztqfj_v2.model.pack.net.PackNumericalForecastColumnDown.ForList;
//import com.pcs.lib_ztqfj_v2.model.pack.net.PackNumericalForecastColumnUp;
//import com.pcs.lib_ztqfj_v2.model.pack.net.PackNumericalForecastDown;
//import com.pcs.lib_ztqfj_v2.model.pack.net.PackNumericalForecastDown.LmListBean;
//import com.pcs.lib_ztqfj_v2.model.pack.net.PackNumericalForecastDown.TitleListBean;
//import com.pcs.lib_ztqfj_v2.model.pack.net.PackNumericalForecastUp;
//import com.pcs.lib_ztqfj_v2.model.pack.net.PackShareAboutDown;
//import com.pcs.lib_ztqfj_v2.model.pack.net.PackShareAboutUp;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * @author Z 数值预报详情 福建省气象台指导预报
// */
//public class ActivityDetailNumericalPro extends FragmentActivityZtqBase
//        implements OnClickListener {
//
//    private TextView subtitle_tv;
//    private TextView spinner_text;
//    private TextView n_content;
//    /**
//     * 内容图片
//     */
//    private ImageTouchView image_show;
//    private int screenHight = 0;
//    private int screenwidth = 0;
//    private PackNumericalForecastUp packup;
//    /**
//     * 雨量预报数据
//     */
//    private List<String> spinner_data;
//    private PackNumericalForecastDown packDown;
//    private MyReceiver receiver = new MyReceiver();
//    private ScrollView content_scrollview;
//    private LinearLayout layout;
//    /**
//     * 头部下拉选项数据
//     */
//    private List<String> subTitleList;
//    public List<LmListBean> lmBeanListData;
//    public List<List<TitleListBean>> TitleBeanListData;
//    private PackNumericalForecastColumnUp packNumericalForecastColumnUp = new PackNumericalForecastColumnUp();
//    private PackNumericalForecastColumnDown packNumericalForecastColumnDown;
//    /**
//     * 头部下拉选项源数据
//     */
//    private List<ForList> forList2 = new ArrayList<ForList>();// 二级目录
//    /**
//     * 左右按钮布局
//     */
//    private LinearLayout left_right_btn_layout;
//
//    private ImageButton image_left;
//    private ImageButton image_right;
//    private String title;
//
//    private LinearLayout layout_root;
//
//    private Button image_share;
//    private WebView webview;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        createImageFetcher();
//        PcsDataBrocastReceiver.registerReceiver(
//                ActivityDetailNumericalPro.this, receiver);
//        Intent intent = getIntent();
//        title = intent.getStringExtra("t");
//        String code = intent.getStringExtra("c");
//        subTitleList = new ArrayList<String>();
//        setTitleText(title);
//        setContentView(R.layout.activity_numerical_forecast_detail);
//        setBtnRight(R.drawable.btn_num_more, new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                createPopupWindow(btnRight, subTitleList, SUBTITLE, listener)
//                        .showAsDropDown(btnRight);
//            }
//        });
//
//        initView();
//        initEvent();
//        initData(code);
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        unregisterReceiver(receiver);
//
//    }
//
//    private String imageKey = "";
//
//    private void initEvent() {
//        spinner_text.setOnClickListener(this);
//        image_share.setOnClickListener(this);
//        image_right.setOnClickListener(this);
//        image_left.setOnClickListener(this);
//    }
//
//    private void initView() {
//        content_scrollview = (ScrollView) findViewById(R.id.content_scrollview);
//        subtitle_tv = (TextView) findViewById(R.id.subtitle_tv);
//        spinner_text = (TextView) findViewById(R.id.spinner_text);
//        n_content = (TextView) findViewById(R.id.n_content);
//        image_show = (ImageTouchView) findViewById(R.id.image_show);
//        layout = (LinearLayout) findViewById(R.id.spinner_layout);
//        left_right_btn_layout = (LinearLayout) findViewById(R.id.left_right_btn_layout);
//        image_left = (ImageButton) findViewById(R.id.image_left);
//        image_right = (ImageButton) findViewById(R.id.image_right);
//        layout_root = (LinearLayout) findViewById(R.id.layout_root);
//        image_share = (Button) findViewById(R.id.image_share);
//        initWebView();
//    }
//
//    private void initWebView() {
//        webview = (WebView) findViewById(R.id.webview);
//        webview.getSettings().setUseWideViewPort(true);// 设置此属性，可任意比例缩放。
//        webview.setWebViewClient(new WebViewClient() {
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                if (url.startsWith("http:") || url.startsWith("https:")) {
//                    view.removeAllViews();
//                    view.loadUrl(url);
//                    return false;
//                }
//                return true;
//            }
//
//            @Override
//            public void onPageStarted(WebView view, String url, Bitmap favicon) {
//            }
//
//            @Override
//            public void onPageFinished(WebView view, String url) {
//            }
//        });
//
//        webview.setWebChromeClient(new WebChromeClient() {
//            @Override
//            public void onProgressChanged(WebView view, int progress) {
//
//                super.onProgressChanged(view, progress);
//            }
//
//            @Override
//            public void onReceivedTitle(WebView view, String title) {
//
//                super.onReceivedTitle(view, title);
//            }
//
//        });
//        WebSettings webSettings = webview.getSettings();
//        webSettings.setJavaScriptEnabled(true);
//        webSettings.setBuiltInZoomControls(true);
//        webSettings.setDisplayZoomControls(false);
//        webSettings.setSupportZoom(true);
//        webSettings.setBlockNetworkImage(false);
//    }
//
//    private void initData(String code) {
//        requestForecastColumn(code);
//        // 获取一级code对应的二级列表
//        subTitleList.clear();
//        for (int i = 0; i < forList2.size(); i++) {
//            subTitleList.add(forList2.get(i).name);
//        }
//        spinner_data = new ArrayList<String>();
//        lmBeanListData = new ArrayList<LmListBean>();
//        TitleBeanListData = new ArrayList<List<TitleListBean>>();
//        screenHight = Util.getScreenHeight(ActivityDetailNumericalPro.this);
//        screenwidth = Util.getScreenWidth(ActivityDetailNumericalPro.this);
//        // 默认取第一个数据
//        dealWidthData(0);
//        getShareContext();
//    }
//
//    private PackShareAboutUp aboutShare = new PackShareAboutUp();
//    private PackShareAboutDown shareDwon;
//
//    private void getShareContext() {
//        // 气象产品分享--短信分享
//        aboutShare.keyword = "ABOUT_QXCP_DXFW";
//        PcsDataDownload.addDownload(aboutShare);
//    }
//
//    /**
//     * 请求获取对应的二级列表
//     **/
//    private void requestForecastColumn(String code) {
//        if (!isOpenNet()) {
//            showToast(getString(R.string.net_err));
//            return;
//        }
//        PcsDataDownload.addDownload(packNumericalForecastColumnUp);
//        packNumericalForecastColumnDown = (PackNumericalForecastColumnDown) PcsDataManager.getInstance().getNetPack(packNumericalForecastColumnUp.getName());
//        if (packNumericalForecastColumnDown == null) {
//            return;
//        }
//        for (int i = 0; i < packNumericalForecastColumnDown.forlist.size(); i++) {
//            if (packNumericalForecastColumnDown.forlist.get(i).parent_id
//                    .equals(code)) {
//                forList2.add(packNumericalForecastColumnDown.forlist.get(i));
//            }
//        }
//    }
//
//    /**
//     * 顶部子标题数据 处理数据
//     */
//    private void dealWidthData(int position) {
//        showProgressDialog();
//        if (forList2.size() == 0) {
//            return;
//        }
//        subtitle_tv.setText(forList2.get(position).name);
//        if (forList2.get(position).name.trim().equals("雨量预报")) {
//            layout.setVisibility(View.VISIBLE);
//        } else {
//            layout.setVisibility(View.GONE);
//        }
//        // 设置背景
//        layout_anther_web = findViewById(R.id.layout_anther_web);
//        View layout_root = findViewById(R.id.layout_root);
//        if (forList2.get(position).name.trim().equals("福建省临近预报")) {
//            layout_root.setBackgroundResource(android.R.color.white);
//        } else {
//            layout_root.setBackgroundResource(R.drawable.bg_satellite);
//        }
//        req(forList2.get(position).id);
//    }
//
//    private View layout_anther_web;
//
//    private void req(String reqCode) {
////        if(!isOpenNet()){
////            showToast(getString(R.string.net_err));
////            return ;
////        }
//        packDown = new PackNumericalForecastDown();
//        packup = new PackNumericalForecastUp();
//        packup.lm = reqCode;
//        this.reqCode = reqCode;
//        PcsDataDownload.addDownload(packup);
//        packDown = (PackNumericalForecastDown) PcsDataManager.getInstance().getNetPack(packup.getName());
//        if (packDown == null) {
//        } else {
//            dismissProgressDialog();
//            dealWidth(packDown);
//        }
//    }
//
//
//    /**
//     * 解析完数据处理
//     */
//    private void dealWidth(PackNumericalForecastDown packDown) {
//        if (packDown == null) {
//            return;
//        }
//        try {
//            spinner_data.clear();
//            lmBeanListData.clear();
//            TitleBeanListData.clear();
//            for (int i = 0; i < packDown.lmBeanList.size(); i++) {
//                lmBeanListData.add(packDown.lmBeanList.get(i));
//                List<TitleListBean> TitleBeanList = new ArrayList<TitleListBean>();
//                for (int j = 0; j < packDown.TitleBeanList.size(); j++) {
//                    if (title.equals("中央气象台指导预报")) {
//                        TitleBeanList.add(packDown.TitleBeanList.get(j));
//                    } else if (packDown.TitleBeanList.get(j).lm
//                            .equals(packDown.lmBeanList.get(i).id)) {
//                        TitleBeanList.add(packDown.TitleBeanList.get(j));
//                    }
//                }
//                TitleBeanListData.add(TitleBeanList);
//            }
//            if (title.equals("中央气象台指导预报")) {
//                for (int i = 0; i < packDown.TitleBeanList.size(); i++) {
//                    spinner_data.add(packDown.TitleBeanList.get(i).title);
//                }
//            } else {
//                for (int i = 0; i < TitleBeanListData.get(0).size(); i++) {
//                    spinner_data.add(TitleBeanListData.get(0).get(i).title + "小时");
//                }
//            }
//
//            if (TitleBeanListData.get(0).size() > 1) {
//                left_right_btn_layout.setVisibility(View.VISIBLE);
//            } else {
//                left_right_btn_layout.setVisibility(View.GONE);
//            }
//            if (spinner_data.size() > 1) {
//                left_right_btn_layout.setVisibility(View.VISIBLE);
//            } else {
//                left_right_btn_layout.setVisibility(View.GONE);
//            }
//            changeValue(0);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private boolean isNullData = false;
//
//    private void changeValue(int position) {
//        n_content.setText("");
//        if (packDown == null) {
//            return;
//        }
//        spinner_text.setText(spinner_data.get(position));
//
//        if (TitleBeanListData.size() > 0 && TitleBeanListData.get(0).get(position).type.equals("1")) {
//            content_scrollview.setVisibility(View.VISIBLE);
//            image_show.setVisibility(View.GONE);
//            layout_anther_web.setVisibility(View.VISIBLE);
//            webview.setVisibility(View.GONE);
//            n_content.setTextColor(getResources().getColor(R.color.text_black));
//            n_content.setText(TitleBeanListData.get(0).get(position).str);
//
//        } else if (TitleBeanListData.size() > 0 && TitleBeanListData.get(0).get(position).type.equals("2")) {
//            content_scrollview.setVisibility(View.GONE);
//            layout_anther_web.setVisibility(View.VISIBLE);
//            webview.setVisibility(View.GONE);
//            image_show.setVisibility(View.VISIBLE);
//            // showProgressDialog();
//
//            if (!TextUtils.isEmpty(TitleBeanListData.get(0).get(position).img)) {
//                imageKey = "";
//                imageKey = TitleBeanListData.get(0).get(position).img;
//                getImageFetcher().addListener(mImageListener);
//                getImageFetcher().loadImage(imageKey, null, ImageConstant.ImageShowType.NONE);
//            } else {
//                showToast("服务器不存在这张图标");
//            }
//        } else if (TitleBeanListData.size() > 0 && TitleBeanListData.get(0).get(position).type.equals("3")) {
//            // 设置下载
//
//            layout_anther_web.setVisibility(View.GONE);
//            webview.setVisibility(View.VISIBLE);
//            if (!TextUtils.isEmpty(TitleBeanListData.get(0).get(position).html)) {
//                String path = getString(R.string.file_download_url) + TitleBeanListData.get(0).get(position).html;
//                webview.loadUrl(path);
//            }
//        } else {
//            content_scrollview.setVisibility(View.VISIBLE);
//            layout_anther_web.setVisibility(View.VISIBLE);
//            image_show.setVisibility(View.GONE);
//            webview.setVisibility(View.GONE);
//            // 暂无数据
//            n_content.setGravity(Gravity.CENTER);
//            n_content.setText("暂无数据");
//            n_content.setTextColor(getResources().getColor(
//                    R.color.bg_black_alpha20));
//        }
//    }
//
//    private final int SPINNER = 0;
//    private final int SUBTITLE = 1;
//    private DrowListClick listener = new DrowListClick() {
//        @Override
//        public void itemClick(int floag, int item) {
//            try {
//                if (floag == SPINNER) {
//                    spinner_text.setText(spinner_data.get(item));
//                    tiemposition = item;
//                    changeValue(item);
//                } else if (floag == SUBTITLE) {
//                    dealWidthData(item);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    };
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.spinner_text:
//                /** 雨量预报下拉框列表 */
//                if (spinner_data.size() > 1) {
//                    createPopupWindow(spinner_text, spinner_data, SPINNER, listener)
//                            .showAsDropDown(spinner_text);
//                }
//                break;
//
//            case R.id.image_left:
//                if (spinner_data.size() > 1) {
//                    if (tiemposition == 0) {
//                        tiemposition = spinner_data.size() - 1;
//                    } else {
//                        tiemposition--;
//                    }
//                    changeValue(tiemposition);
//                    spinner_text.setText(spinner_data.get(tiemposition));
//                }
//
//                break;
//            case R.id.image_right:
//                if (spinner_data.size() > 1) {
//                    if (tiemposition == spinner_data.size() - 1) {
//                        tiemposition = 0;
//                    } else {
//                        tiemposition++;
//                    }
//                    changeValue(tiemposition);
//                    spinner_text.setText(spinner_data.get(tiemposition));
//                }
//                break;
//            case R.id.image_share:
//                if (shareDwon == null) {
//                    shareDwon = (PackShareAboutDown) PcsDataManager.getInstance().getNetPack(aboutShare.getName());
//                }
//                if (shareDwon == null) {
//                    return;
//                }
//                String chareContent = shareDwon.share_content;
//                Bitmap bitmap = BitmapUtil.takeScreenShot(ActivityDetailNumericalPro.this);
//                ShareTools.getInstance(ActivityDetailNumericalPro.this).setShareContent(chareContent, bitmap).showWindow(layout_root);
//                break;
//        }
//    }
//
//    /**
//     * 请求数据的key
//     */
//    private String reqCode;
//    /**
//     * 记录下拉时间选择当前选中的位置
//     */
//    private int tiemposition = 0;
//
//    private class MyReceiver extends PcsDataBrocastReceiver {
//        @Override
//        public void onReceive(String name, String errorStr) {
//
//
//            if (name.contains(PackNumericalForecastUp.NAME)) {
//                dismissProgressDialog();
//                packDown = (PackNumericalForecastDown) PcsDataManager.getInstance().getNetPack(name);
//                if (packDown == null) {
//                    return;
//                }
//                dealWidth(packDown);
//            } else if (aboutShare != null && aboutShare.getName().equals(name)) {
//                shareDwon = (PackShareAboutDown) PcsDataManager.getInstance().getNetPack(name);
//            }
//        }
//    }
//
//    /**
//     * 创建下拉选择列表
//     */
//    public PopupWindow createPopupWindow(View dropDownView,
//                                         final List<String> dataeaum, final int floag,
//                                         final DrowListClick listener) {
//        AdapterData dataAdapter = new AdapterData(
//                ActivityDetailNumericalPro.this, dataeaum);
//        View popcontent = LayoutInflater.from(ActivityDetailNumericalPro.this)
//                .inflate(R.layout.pop_list_layout, null);
//        ListView lv = (ListView) popcontent.findViewById(R.id.mylistviw);
//        lv.setAdapter(dataAdapter);
//        final PopupWindow pop = new PopupWindow(ActivityDetailNumericalPro.this);
//        pop.setContentView(popcontent);
//        pop.setOutsideTouchable(false);
//        pop.setWidth(screenwidth / 2);
//        pop.setHeight(LayoutParams.WRAP_CONTENT);
//        pop.setFocusable(true);
//        lv.setOnItemClickListener(new OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view,
//                                    int position, long id) {
//                pop.dismiss();
//                listener.itemClick(floag, position);
//            }
//        });
//        return pop;
//    }
//
//    private ListenerImageLoad mImageListener = new ListenerImageLoad() {
//        @Override
//        public void done(String key, boolean isSucc) {
//            if (imageKey.equals(key)) {
//                dismissProgressDialog();
//                if (isSucc && getImageFetcher().getImageCache() != null) {
//                    Bitmap bm = getImageFetcher().getImageCache()
//                            .getBitmapFromAllCache(key).getBitmap();
//                    image_show.setMyImageBitmap(bm);
//                } else {
//                    showToast("图片为空");
//                }
//            }
//        }
//    };
//}