package com.pcs.ztqtj.view.activity.product.situation;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.pcs.lib_ztqfj_v2.model.pack.net.PackForecastWeatherTipUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.PackWarningCenterYJXXGridIndexDown;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.AdapterWarningCenterGrid;
import com.pcs.ztqtj.control.adapter.livequery.AdapterData;
import com.pcs.ztqtj.control.inter.DrowListClick;
import com.pcs.ztqtj.control.tool.utils.TextUtil;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.util.OkHttpUtil;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
import com.pcs.ztqtj.view.activity.air_quality.AcitvityAirWhatAQI;
import com.pcs.ztqtj.view.myview.ImageTouchView;
import com.pcs.lib.lib_pcs_v3.control.tool.Util;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib.lib_pcs_v3.model.image.ImageConstant;
import com.pcs.lib.lib_pcs_v3.model.image.ListenerImageLoad;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackNumericalForecastColumnDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackNumericalForecastColumnUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackNumericalForecastDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackNumericalForecastUp;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 天气形势
 */
public class ActivitySituation extends FragmentActivityZtqBase implements View.OnClickListener {

    private ImageButton image_left;
    private ImageButton image_right;
    private Button bt_clear;
    private TextView spinner_text;
    private TextView spinner_title;
    private TextView n_content, text_title;
    private ImageTouchView image_show;
    private RadioGroup number_radio_group;
    private LinearLayout spinner_situation;
    private ScrollView content_scrollview;
    private MyReceiver receiver = new MyReceiver();
    private PackNumericalForecastUp packNumericalForecastUp;
    private List<String> spinner_data;
    private String imageKey = "";
    /**
     * 左右按钮布局
     */
    private LinearLayout left_right_btn_layout;
    /**
     * 头部下拉选项源数据
     */
    private List<PackNumericalForecastColumnDown.ForList> forList2 = new ArrayList<>();// 二级目录
    private PackNumericalForecastColumnUp packNumericalForecastColumnUp = new PackNumericalForecastColumnUp();
    private PackNumericalForecastColumnDown packNumericalForecastColumnDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitleText("天气形势");
        setContentView(R.layout.activity_situation);
        PcsDataBrocastReceiver.registerReceiver(ActivitySituation.this, receiver);
        initView();
        initDate();
        initEvent();
    }

    private void initEvent() {
//        showProgressDialog();
//        req();
        number_radio_group
                .setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        raidoItemSelect = checkedId - 101;
                        tiemposition = 0;
                        changeValue(0);
//                        getserverData(raidoItemSelect);
                    }
                });
    }

    /**
     * 请求获取对应的二级列表
     **/
    private void requestForecastColumn() {
        if (!isOpenNet()) {
            showToast(getString(R.string.net_err));
            return;
        }
        packNumericalForecastColumnUp.type = "3";
        PcsDataDownload.addDownload(packNumericalForecastColumnUp);

    }

    /**
     * 头部下拉选项数据
     */
    private List<String> subTitleList;

    private void initDate() {
        spinner_data = new ArrayList<>();
        lmBeanListData = new ArrayList<>();
        TitleBeanListData = new ArrayList<>();
        screenwidth = Util.getScreenWidth(ActivitySituation.this);

        try {
            subTitleList = new ArrayList<>();
            // 获取一级code对应的二级列表
            showProgressDialog();
            requestForecastColumn();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initView() {
        image_left = (ImageButton) findViewById(R.id.situation_image_left);
        image_left.setOnClickListener(this);
        image_right = (ImageButton) findViewById(R.id.situation_image_right);
        image_right.setOnClickListener(this);
        image_show = (ImageTouchView) findViewById(R.id.situation_image_show);
        bt_clear = (Button) findViewById(R.id.situation_clear);
        bt_clear.setOnClickListener(this);
        content_scrollview = (ScrollView) findViewById(R.id.content_scrollview);
        n_content = (TextView) findViewById(R.id.n_content);
        number_radio_group = (RadioGroup) findViewById(R.id.situation_radio_group);
        spinner_text = (TextView) findViewById(R.id.situation_text);
        spinner_text.setOnClickListener(this);
        spinner_title = (TextView) findViewById(R.id.situation_tv);
        spinner_situation = (LinearLayout) findViewById(R.id.spinner_situation);
        left_right_btn_layout = (LinearLayout) findViewById(R.id.left_right_btn_layout);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    /**
     * 数据解析完成取得源数据
     */
    public List<PackNumericalForecastDown.LmListBean> lmBeanListData;
    public List<List<PackNumericalForecastDown.TitleListBean>> TitleBeanListData;
    /**
     * 单选按钮第几个被选中
     */
    private int raidoItemSelect = 0;

    private void dealWidth(PackNumericalForecastDown packDown) {
        lmBeanListData.clear();
        TitleBeanListData.clear();
        for (int i = 0; i < packDown.lmBeanList.size(); i++) {
            lmBeanListData.add(packDown.lmBeanList.get(i));
            List<PackNumericalForecastDown.TitleListBean> TitleBeanList = new ArrayList<>();
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

        number_radio_group.removeAllViews();
        int width = Util.getScreenWidth(ActivitySituation.this);
        int radioWidth = width / lmBeanListData.size();
        int pad = Util.dip2px(ActivitySituation.this, 10);
        number_radio_group.setVisibility(View.VISIBLE);
        if (lmBeanListData.size() > 0) {
            for (int i = 0; i < lmBeanListData.size(); i++) {
                RadioButton radioButton = new RadioButton(ActivitySituation.this);
                radioButton.setId(101 + i);
                radioButton.setGravity(Gravity.CENTER);
                radioButton.setTextColor(getResources().getColor(R.color.text_black));
                radioButton.setBackgroundResource(R.drawable.radio_number);
                radioButton.setPadding(0, pad, 0, pad);
                radioButton.setButtonDrawable(R.drawable.bgalph100);
                // radioButton.setButtonDrawable(getResources().getDrawable(android.R.color.transparent));
                if (i == 0) {
                    radioButton.setChecked(true);
                }
                radioButton.setText(lmBeanListData.get(i).name);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        radioWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
                number_radio_group.addView(radioButton, lp);
            }
        } else {
            number_radio_group.setVisibility(View.GONE);
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
        spinner_data.clear();
        for (int i = 0; i < TitleBeanListData.get(raidoItemSelect).size(); i++) {
            spinner_data.add(TitleBeanListData.get(raidoItemSelect).get(i).title );
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
            n_content.setTextColor(getResources().getColor(R.color.text_black));
            n_content.setText(TitleBeanListData.get(raidoItemSelect).get(
                    position).str);
        } else if (TitleBeanListData.size() > raidoItemSelect && TitleBeanListData.get(raidoItemSelect).size() >
                position && TitleBeanListData.get(raidoItemSelect).get(position).type
                .equals("2")) {
            content_scrollview.setVisibility(View.GONE);
            image_show.setVisibility(View.VISIBLE);
            showProgressDialog();

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
//            showProgressDialog();
//            if (!TextUtils.isEmpty(TitleBeanListData.get(0).get(position).html)) {
//                String path = getString(R.string.file_download_url) + TitleBeanListData.get(0).get(position).html;
//                webview.loadUrl(path);
//            }
        } else {
            content_scrollview.setVisibility(View.VISIBLE);
            image_show.setVisibility(View.GONE);
            // 暂无数据
            n_content.setGravity(Gravity.CENTER);
            n_content.setText("暂无数据");
            n_content.setTextColor(getResources().getColor(R.color.bg_black_alpha20));
        }
    }

    private PackNumericalForecastDown packDown;
    private int screenwidth = 0;

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.situation_text:

                // 具体标题时间选择
                if (spinner_data.size() > 1) {
                    createPopupWindow(spinner_text, spinner_data, SPINNERSUBTIME,
                            listener, screenwidth / 2).showAsDropDown(spinner_text);
                }

                break;
            case R.id.situation_image_right:
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
            case R.id.situation_image_left:

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
            case R.id.situation_clear:
                changeValueKey();
                break;
        }


    }

    private void changeValueKey() {
        Intent intent = new Intent(ActivitySituation.this, AcitvityAirWhatAQI.class);
        intent.putExtra("w", packDown.lmBeanList.get(raidoItemSelect).remark+" ");
        intent.putExtra("t","要素说明");
        startActivity(intent);
    }

    private class MyReceiver extends PcsDataBrocastReceiver {
        @Override
        public void onReceive(String name, String errorStr) {
            if (name.contains(packNumericalForecastUp.NAME)) {
                okHttpList(packNumericalForecastUp.getName());
            } else if (name.equals(packNumericalForecastColumnUp.getName())) {
                packNumericalForecastColumnDown = (PackNumericalForecastColumnDown) PcsDataManager.getInstance()
                        .getNetPack
                        (packNumericalForecastColumnUp.getName());
                dismissProgressDialog();
                if (packNumericalForecastColumnDown == null) {
                    return;
                }
                for (int i = 0; i < packNumericalForecastColumnDown.forlist.size(); i++) {
                    if (packNumericalForecastColumnDown.forlist.get(i).parent_id.equals("109")) {
                        forList2.add(packNumericalForecastColumnDown.forlist.get(i));
                    }
                }

//                number_radio_group.removeAllViews();
//                int width = Util.getScreenWidth(ActivitySituation.this);
//                int radioWidth = width / forList2.size();
//                int pad = Util.dip2px(ActivitySituation.this, 10);
//                number_radio_group.setVisibility(View.VISIBLE);
//                if (forList2.size() > 0) {
//                    for (int i = 0; i < forList2.size(); i++) {
//                        RadioButton radioButton = new RadioButton(ActivitySituation.this);
//                        radioButton.setId(101 + i);
//                        radioButton.setGravity(Gravity.CENTER);
//                        radioButton.setTextColor(getResources().getColor(R.color.text_black));
//                        radioButton.setBackgroundResource(R.drawable.radio_number);
//                        radioButton.setPadding(0, pad, 0, pad);
//                        radioButton.setButtonDrawable(R.drawable.bgalph100);
//                        // radioButton.setButtonDrawable(getResources().getDrawable(android.R.color.transparent));
//                        if (i == 0) {
//                            radioButton.setChecked(true);
//                        }
//                        radioButton.setText(forList2.get(i).name);
//                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
//                                radioWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
//                        number_radio_group.addView(radioButton, lp);
//                    }
//                } else {
//                    number_radio_group.setVisibility(View.GONE);
//                }

                for (int i = 0; i < forList2.size(); i++) {
                    subTitleList.add(forList2.get(i).name);
                }
                //textButton.setVisibility(View.VISIBLE);
                //int width = (int) (Util.getScreenWidth(this) / 2.0f) - Util.dip2px(this, 20);
                //setRightButtonText(R.drawable.bg_drowdown, forList2.get(0).name, width);

                screenwidth = Util.getScreenWidth(ActivitySituation.this);
                getserverData(0);
            }
        }
    }

    /**
     * 获取天气形势数据
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
                                                    if (packDown == null || packDown.lmBeanList.size() == 0) {
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
    private DrowListClick listener = new DrowListClick() {
        @Override
        public void itemClick(int floag, int item) {
            try {
                if (floag == SPINNERSUBTIME) {
                    tiemposition = item;
                    changeValue(item);
                } else if (floag == SPINNERTIME) {
                    spinner_text.setText(spinner_data.get(item));
                    raidoItemSelect = item;
//                    spinner_title.setText(spinner_title_data.get(item));
                    changeValue(0);
                } else if (floag == SUBTITLE) {
                    // 栏目切换
                    getserverData(item);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * 栏目切换、重新取数据
     *
     * @param itemPostion
     */
    private void getserverData(int itemPostion) {
        if (forList2.size() == 0) {
            return;
        }
//        int width = (int) (Util.getScreenWidth(this) / 2.0f) - Util.dip2px(this, 20);
//        setRightButtonText(R.drawable.bg_drowdown,
//                forList2.get(itemPostion).name, width);
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
        packNumericalForecastUp = new PackNumericalForecastUp();
        packNumericalForecastUp.lm = reqCode;
        PcsDataDownload.addDownload(packNumericalForecastUp);

    }

    private int tiemposition = 0;

    /**
     * 创建下拉选择列表
     */
    public PopupWindow createPopupWindow(View dropDownView,
                                         final List<String> dataeaum, final int floag,
                                         final DrowListClick listener, int popwidth) {
        AdapterData dataAdapter = new AdapterData(
                ActivitySituation.this, dataeaum);
        View popcontent = LayoutInflater.from(
                ActivitySituation.this).inflate(
                R.layout.pop_list_layout, null);

        ListView lv = (ListView) popcontent.findViewById(R.id.mylistviw);
        lv.setAdapter(dataAdapter);
        final PopupWindow pop = new PopupWindow(
                ActivitySituation.this);
        pop.setContentView(popcontent);
        pop.setOutsideTouchable(false);
        pop.setWidth(popwidth);
        pop.setFocusable(true);
        pop.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 判断哪里的下拉框
        if (dropDownView.equals(textButton)) {
            pop.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        } else if (dropDownView.equals(spinner_text)) {
            if (lv.getCount() < 10) {
                pop.setHeight(android.view.WindowManager.LayoutParams.WRAP_CONTENT);
            } else {
                int screenHight = Util.getScreenHeight(ActivitySituation.this);
                pop.setHeight((int) (screenHight * 0.49));
            }

        }
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                tiemposition = 0;
                pop.dismiss();
                listener.itemClick(floag, position);

            }
        });
        return pop;
    }


    private ListenerImageLoad mImageListener = new ListenerImageLoad() {
        @Override
        public void done(String key, boolean isSucc) {
            if (imageKey.equals(key)) {
                dismissProgressDialog();
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
