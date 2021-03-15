package com.pcs.ztqtj.view.activity.product.numericalforecast;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib.lib_pcs_v3.model.image.ImageConstant;
import com.pcs.lib.lib_pcs_v3.model.image.ListenerImageLoad;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackNumericalForecastColumnDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackNumericalForecastColumnDown.ForList;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackNumericalForecastColumnUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackNumericalForecastDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackNumericalForecastDown.LmListBean;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackNumericalForecastDown.TitleListBean;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackNumericalForecastUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackShareAboutDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackShareAboutUp;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.livequery.AdapterData;
import com.pcs.ztqtj.control.inter.DrowListClick;
import com.pcs.ztqtj.control.tool.ShareTools;
import com.pcs.ztqtj.view.activity.FragmentActivitySZYBBase;
import com.pcs.ztqtj.view.myview.ImageTouchView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Z 数值预报详情界面——除了中央和福建气象台指导预报
 */
public class ActivityDetailNumericalForecast extends FragmentActivitySZYBBase
        implements OnClickListener {
    private TextView subtitle_tv;
    private TextView spinner_text;
    private TextView spinner_title;
    private TextView n_content, text_title;
    private ImageTouchView image_show;
    private RadioGroup number_radio_group;
    //    private int screenHight = 0;
    private PackNumericalForecastUp packup;
    private List<String> spinner_data;
    private List<String> spinner_title_data;
    private PackNumericalForecastDown packDown;
    private MyReceiver receiver = new MyReceiver();
    private ScrollView content_scrollview;

    /**
     * 数据解析完成取得源数据
     */
    public List<LmListBean> lmBeanListData;
    public List<List<TitleListBean>> TitleBeanListData;
    /**
     * 头部下拉选项数据
     */
    private List<String> subTitleList;
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
    private PackNumericalForecastColumnUp packNumericalForecastColumnUp = new PackNumericalForecastColumnUp();
    private PackNumericalForecastColumnDown packNumericalForecastColumnDown;
    /**
     * 头部下拉选项源数据
     */
    private List<ForList> forList2 = new ArrayList<ForList>();// 二级目录
    private ImageButton image_left;
    private ImageButton image_right;
    private Button image_share;
    private LinearLayout layout_root;
    private int screenwidth = 0;
    private String imageKey = "";

    private WebView webview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createImageFetcher();
        PcsDataBrocastReceiver.registerReceiver(ActivityDetailNumericalForecast.this, receiver);
        setContentView(R.layout.activity_numerical_forecast_detail);
        initView();
        initEvent();
        initData();
    }


    private PackShareAboutUp aboutShare = new PackShareAboutUp();
    private PackShareAboutDown shareDwon;

    private void getShareContext() {
        // 气象产品分享--短信分享
        aboutShare.keyword = "ABOUT_QXCP_DXFW";
        PcsDataDownload.addDownload(aboutShare);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
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
                createPopupWindow(textButton, subTitleList, SUBTITLE, listener,
                        screenwidth / 2).showAsDropDown(textButton);
            }
        });
        number_radio_group
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {
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
        text_title = (TextView) findViewById(R.id.text_title);
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
        webSettings.setTextZoom(100);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setSupportZoom(true);
        webSettings.setBlockNetworkImage(false);
        webSettings.setLoadWithOverviewMode(true);
    }

    private String type = "";

    private void initData() {
        Intent intent = getIntent();
        String title = intent.getStringExtra("t");
        String code = intent.getStringExtra("c");
        type = intent.getStringExtra("type");
        setTitleText(title);
        try {
            subTitleList = new ArrayList<String>();
            // 获取一级code对应的二级列表
            requestForecastColumn(code);
            for (int i = 0; i < forList2.size(); i++) {
                subTitleList.add(forList2.get(i).name);
            }
            textButton.setVisibility(View.VISIBLE);
            int width = (int) (Util.getScreenWidth(this) / 2.0f) - Util.dip2px(this, 20);
            setRightButtonText(R.drawable.bg_drowdown, forList2.get(0).name, width);

            spinner_data = new ArrayList<String>();
            spinner_title_data = new ArrayList<String>();
            lmBeanListData = new ArrayList<LmListBean>();
            TitleBeanListData = new ArrayList<List<TitleListBean>>();
            screenwidth = Util.getScreenWidth(ActivityDetailNumericalForecast.this);
            getserverData(0);
            getShareContext();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 请求获取对应的二级列表
     **/
    private void requestForecastColumn(String code) {
        if (!isOpenNet()) {
            showToast(getString(R.string.net_err));
            return;
        }
        packNumericalForecastColumnUp.type = "1";
        PcsDataDownload.addDownload(packNumericalForecastColumnUp);
        packNumericalForecastColumnDown = (PackNumericalForecastColumnDown) PcsDataManager.getInstance().getNetPack
                (packNumericalForecastColumnUp.getName());
        if (packNumericalForecastColumnDown == null) {
            return;
        }
        for (int i = 0; i < packNumericalForecastColumnDown.forlist.size(); i++) {
            if (packNumericalForecastColumnDown.forlist.get(i).parent_id.equals(code)) {
                forList2.add(packNumericalForecastColumnDown.forlist.get(i));
            }
        }
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
        subtitle_tv.setText("预报时效");
        int width = (int) (Util.getScreenWidth(this) / 2.0f) - Util.dip2px(this, 20);
        setRightButtonText(R.drawable.bg_drowdown,
                forList2.get(itemPostion).name, width);
        showProgressDialog();
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
        packDown = new PackNumericalForecastDown();
        packup = new PackNumericalForecastUp();
        packup.lm = reqCode;
        PcsDataDownload.addDownload(packup);
        packDown = (PackNumericalForecastDown) PcsDataManager.getInstance().getNetPack(packup.getName());
        if (packDown == null) {
        } else {
            showProgressDialog();
            dealWidth(packDown);
        }
    }

    /**
     * 解析完数据处理
     */
    private void dealWidth(PackNumericalForecastDown packDown) {
        if (packup == null) {
            return;
        }
        lmBeanListData.clear();
        TitleBeanListData.clear();
        for (int i = 0; i < packDown.lmBeanList.size(); i++) {
            lmBeanListData.add(packDown.lmBeanList.get(i));
            List<TitleListBean> TitleBeanList = new ArrayList<TitleListBean>();
            for (int j = 0; j < packDown.TitleBeanList.size(); j++) {
                if (packDown.TitleBeanList.get(j).lm
                        .equals(packDown.lmBeanList.get(i).id)) {
                    TitleBeanList.add(packDown.TitleBeanList.get(j));
                }
            }
            TitleBeanListData.add(TitleBeanList);
        }
        raidoItemSelect = 0;
        changeValue(0);

//        if ("高度场和风场".endsWith(textButton.getText().toString().trim()) || "降水".endsWith(textButton.getText().toString
//                ().trim()) || "相对湿度+风场".endsWith(textButton.getText().toString().trim())) {
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
//        }
//        else {
//            number_radio_group.removeAllViews();
//            int width = Util.getScreenWidth(ActivityDetailNumericalForecast.this);
//            int radioWidth = width / lmBeanListData.size();
//            int pad = Util.dip2px(ActivityDetailNumericalForecast.this, 10);
//            number_radio_group.setVisibility(View.VISIBLE);
//            spinner_title.setVisibility(View.GONE);
//            if (lmBeanListData.size() > 0) {
//                for (int i = 0; i < lmBeanListData.size(); i++) {
//                    RadioButton radioButton = new RadioButton(ActivityDetailNumericalForecast.this);
//                    radioButton.setId(101 + i);
//                    radioButton.setGravity(Gravity.CENTER);
//                    radioButton.setTextColor(getResources().getColor(R.color.text_black));
//                    radioButton.setBackgroundResource(R.drawable.radio_number);
//                    radioButton.setPadding(0, pad, 0, pad);
//                    radioButton.setButtonDrawable(R.drawable.bgalph100);
//                    // radioButton.setButtonDrawable(getResources().getDrawable(android.R.color.transparent));
//                    if (i == 0) {
//                        radioButton.setChecked(true);
//                    }
//                    radioButton.setText(lmBeanListData.get(i).name);
//                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
//                            radioWidth, LayoutParams.WRAP_CONTENT);
//                    number_radio_group.addView(radioButton, lp);
//                }
//            } else {
//                number_radio_group.setVisibility(View.GONE);
//                spinner_title.setVisibility(View.GONE);
//            }
//        }
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
        spinner_data.clear();
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
                TitleBeanListData.get(raidoItemSelect).get(position).type
                        .equals("1")) {
            content_scrollview.setVisibility(View.VISIBLE);
            image_show.setVisibility(View.GONE);
            webview.setVisibility(View.GONE);
            n_content.setTextColor(getResources().getColor(R.color.text_black));
            n_content.setText(TitleBeanListData.get(raidoItemSelect).get(
                    position).str);
        } else if (TitleBeanListData.size() > raidoItemSelect && TitleBeanListData.get(raidoItemSelect).size() >
                position && TitleBeanListData.get(raidoItemSelect).get(position).type
                .equals("2")) {
            content_scrollview.setVisibility(View.GONE);
            image_show.setVisibility(View.VISIBLE);
            webview.setVisibility(View.GONE);
            //showProgressDialog();

            if (!TextUtils.isEmpty(TitleBeanListData.get(raidoItemSelect).get(
                    position).img)) {
                imageKey = TitleBeanListData.get(raidoItemSelect).get(position).img;
                getImageFetcher().addListener(mImageListener);
                getImageFetcher().loadImage(imageKey, null, ImageConstant.ImageShowType.NONE);
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

    /**
     * 单选按钮第几个被选中
     */
    private int raidoItemSelect = 0;
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
//                    if (spinner_data.size()>0){
//                        spinner_text.setText(spinner_data.get(0));
//                    }
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
                if (shareDwon == null) {
                    shareDwon = (PackShareAboutDown) PcsDataManager.getInstance().getNetPack(aboutShare.getName());
                }
                if (shareDwon == null) {
                    return;
                }
                String chareContent = shareDwon.share_content;
                Bitmap bitmap = BitmapUtil.takeScreenShot(ActivityDetailNumericalForecast.this);
                ShareTools.getInstance(ActivityDetailNumericalForecast.this).setShareContent(getTitleText(),
                        chareContent, bitmap,
                        "0").showWindow(layout_root);
                break;
            default:
                break;
        }
    }

    /**
     * 记录下拉时间选择当前选中的位置
     */
    private int tiemposition = 0;

    private class MyReceiver extends PcsDataBrocastReceiver {
        @Override
        public void onReceive(String name, String errorStr) {
            if (name.contains(PackNumericalForecastUp.NAME)) {
                //     dismissProgressDialog();
                if (TextUtils.isEmpty(errorStr)) {
                    packDown = (PackNumericalForecastDown) PcsDataManager.getInstance().getNetPack(name);
                    if (packDown == null) {
                        return;
                    }
                    showProgressDialog();
                    dealWidth(packDown);
                    dismissProgressDialog();
                }
            } else if (aboutShare != null && aboutShare.getName().equals(name)) {
                dismissProgressDialog();
                shareDwon = (PackShareAboutDown) PcsDataManager.getInstance().getNetPack(name);
            }
        }
    }


    /**
     * 创建下拉选择列表
     */
    public PopupWindow createPopupWindow(TextView dropDownView,
                                         final List<String> dataeaum, final int floag,
                                         final DrowListClick listener, int popwidth) {
        AdapterData dataAdapter = new AdapterData(
                ActivityDetailNumericalForecast.this, dataeaum);
        View popcontent = LayoutInflater.from(
                ActivityDetailNumericalForecast.this).inflate(
                R.layout.pop_list_layout, null);

        ListView lv = (ListView) popcontent.findViewById(R.id.mylistviw);
        lv.setAdapter(dataAdapter);
        final PopupWindow pop = new PopupWindow(
                ActivityDetailNumericalForecast.this);
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

    private ListenerImageLoad mImageListener = new ListenerImageLoad() {
        @Override
        public void done(String key, boolean isSucc) {
            if (imageKey.equals(key)) {
                if (isSucc && getImageFetcher().getImageCache() != null) {
                    Bitmap bm = getImageFetcher().getImageCache()
                            .getBitmapFromAllCache(key).getBitmap();
                    image_show.setMyImageBitmap(bm);
                } else {
                    showToast("图片为空");
                }
            }
        }
    };
}