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

import com.pcs.lib.lib_pcs_v3.control.tool.Util;
import com.pcs.lib.lib_pcs_v3.model.image.ImageConstant;
import com.pcs.lib.lib_pcs_v3.model.image.ListenerImageLoad;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackNumericalForecastDown;
import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.livequery.AdapterData;
import com.pcs.ztqtj.control.inter.DrowListClick;
import com.pcs.ztqtj.control.tool.utils.TextUtil;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.util.OkHttpUtil;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
import com.pcs.ztqtj.view.activity.air_quality.AcitvityAirWhatAQI;
import com.pcs.ztqtj.view.myview.ImageTouchView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
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
 * 监测预报-天气形势
 */
public class ActivitySituation extends FragmentActivityZtqBase implements View.OnClickListener {

    private TextView spinner_text;
    private TextView n_content;
    private ImageTouchView image_show;
    private RadioGroup number_radio_group;
    private ScrollView content_scrollview;
    private List<String> spinner_data = new ArrayList<>();
    private ImageButton image_left,image_right;
    private List<PackNumericalForecastDown.LmListBean> columnList = new ArrayList<>();// 二级目录
    private int raidoItemSelect = 0;//当前选中栏目下标
    private int tiemposition = 0;//当前图片下标
    private PackNumericalForecastDown packNumericalForecastDown;
    private int screenwidth = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitleText("天气形势");
        setContentView(R.layout.activity_situation);
        initWidget();
    }

    private void initWidget() {
        image_left = findViewById(R.id.situation_image_left);
        image_left.setOnClickListener(this);
        image_right = findViewById(R.id.situation_image_right);
        image_right.setOnClickListener(this);
        image_show = (ImageTouchView) findViewById(R.id.situation_image_show);
        Button bt_clear = findViewById(R.id.situation_clear);
        bt_clear.setOnClickListener(this);
        content_scrollview = (ScrollView) findViewById(R.id.content_scrollview);
        n_content = (TextView) findViewById(R.id.n_content);
        number_radio_group = (RadioGroup) findViewById(R.id.situation_radio_group);
        spinner_text = (TextView) findViewById(R.id.situation_text);
        spinner_text.setOnClickListener(this);

        number_radio_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                raidoItemSelect = checkedId - 101;
                tiemposition = 0;
                getserverData(raidoItemSelect);
            }
        });

        screenwidth = Util.getScreenWidth(ActivitySituation.this);
        okHttpQxxsColumnList();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.situation_text:
                // 具体标题时间选择
                if (spinner_data.size() > 1) {
                    createPopupWindow(spinner_text, spinner_data, SPINNERSUBTIME, listener , screenwidth / 2).showAsDropDown(spinner_text);
                }
                break;
            case R.id.situation_image_right:
                if (spinner_data.size() > 1) {
                    if (tiemposition == spinner_data.size() - 1) {
                        tiemposition = 0;
                    } else {
                        tiemposition++;
                    }
                    changeValue();
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
                    changeValue();
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
        intent.putExtra("w", packNumericalForecastDown.lmBeanList.get(0).remark);
        intent.putExtra("t","要素说明");
        startActivity(intent);
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
                    changeValue();
                } else if (floag == SPINNERTIME) {
                    spinner_text.setText(spinner_data.get(item));
                    raidoItemSelect = item;
                    changeValue();
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
     * @param itemPostion
     */
    private void getserverData(int itemPostion) {
        if (columnList.size() == 0) {
            return;
        }
        String columnId = columnList.get(itemPostion).id;
        if (!isOpenNet()) {
            showToast(getString(R.string.net_err));
            return;
        }
        okHttpQxxsInit(columnId);
    }

    /**
     * 创建下拉选择列表
     */
    public PopupWindow createPopupWindow(View dropDownView, final List<String> dataeaum, final int floag, final DrowListClick listener, int popwidth) {
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

    /**
     * 获取天气形势数据
     */
    private void okHttpQxxsColumnList() {
        showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param  = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    String json = param.toString();
                    final String url = CONST.BASE_URL+"qxxs_column_list";
                    Log.e("qxxs_column_list", url);
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
                                    Log.e("qxxs_column_list", result);
                                    dismissProgressDialog();
                                    if (!TextUtil.isEmpty(result)) {
                                        try {
                                            JSONObject obj = new JSONObject(result);
                                            if (!obj.isNull("b")) {
                                                JSONObject bobj = obj.getJSONObject("b");
                                                if (!bobj.isNull("forecast_column_n2")) {
                                                    JSONObject listobj = bobj.getJSONObject("forecast_column_n2");
                                                    if (!TextUtil.isEmpty(listobj.toString())) {
                                                        if (!listobj.isNull("for_list")) {
                                                            JSONArray array = listobj.getJSONArray("for_list");
                                                            for (int i = 0; i < array.length(); i++) {
                                                                JSONObject itemObj = array.getJSONObject(i);
                                                                String parent_id = "",id = "";
                                                                if (!itemObj.isNull("id")) {
                                                                    id = itemObj.getString("id");
                                                                }
                                                                if (!itemObj.isNull("parent_id")) {
                                                                    parent_id = itemObj.getString("parent_id");
                                                                }
                                                                if (TextUtils.equals(parent_id, "109")) {
                                                                    okHttpColumnList(id);
                                                                }
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
     * 获取栏目数据
     */
    private void okHttpColumnList(final String columnId) {
        showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param  = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    JSONObject info = new JSONObject();
                    info.put("stationId", columnId);
                    param.put("paramInfo", info);
                    String json = param.toString();
                    Log.e("qxxs_init", json);
                    final String url = CONST.BASE_URL+"qxxs_init";
                    Log.e("qxxs_init", url);
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
                                    Log.e("qxxs_init", result);
                                    dismissProgressDialog();
                                    if (!TextUtil.isEmpty(result)) {
                                        try {
                                            JSONObject obj = new JSONObject(result);
                                            if (!obj.isNull("b")) {
                                                JSONObject bobj = obj.getJSONObject("b");
                                                if (!bobj.isNull("szyb_new")) {
                                                    JSONObject listobj = bobj.getJSONObject("szyb_new");
                                                    if (!TextUtil.isEmpty(listobj.toString())) {
                                                        dismissProgressDialog();
                                                        packNumericalForecastDown = new PackNumericalForecastDown();
                                                        packNumericalForecastDown.fillData(listobj.toString());
                                                        if (packNumericalForecastDown == null || packNumericalForecastDown.lmBeanList.size() == 0) {
                                                            return;
                                                        }
                                                        columnList.clear();
                                                        columnList.addAll(packNumericalForecastDown.lmBeanList);
                                                        raidoItemSelect = 0;
                                                        dealData();

                                                        number_radio_group.removeAllViews();
                                                        int width = Util.getScreenWidth(ActivitySituation.this);
                                                        int radioWidth = width / packNumericalForecastDown.lmBeanList.size();
                                                        int pad = Util.dip2px(ActivitySituation.this, 10);
                                                        number_radio_group.setVisibility(View.VISIBLE);
                                                        if (packNumericalForecastDown.lmBeanList.size() > 0) {
                                                            for (int i = 0; i < packNumericalForecastDown.lmBeanList.size(); i++) {
                                                                RadioButton radioButton = new RadioButton(ActivitySituation.this);
                                                                radioButton.setId(101 + i);
                                                                radioButton.setGravity(Gravity.CENTER);
                                                                radioButton.setTextColor(getResources().getColor(R.color.text_black));
                                                                radioButton.setBackgroundResource(R.drawable.radio_number);
                                                                radioButton.setPadding(0, pad, 0, pad);
                                                                radioButton.setButtonDrawable(R.drawable.bgalph100);
                                                                if (i == 0) {
                                                                    radioButton.setChecked(true);
                                                                }
                                                                radioButton.setText(packNumericalForecastDown.lmBeanList.get(i).name);
                                                                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(radioWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
                                                                number_radio_group.addView(radioButton, lp);
                                                            }
                                                        } else {
                                                            number_radio_group.setVisibility(View.GONE);
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
     * 获取天气形势数据
     */
    private void okHttpQxxsInit(final String stationId) {
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
                    Log.e("qxxs_init", json);
                    final String url = CONST.BASE_URL+"qxxs_init";
                    Log.e("qxxs_init", url);
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
                                    dismissProgressDialog();
                                    Log.e("qxxs_init", result);
                                    if (!TextUtil.isEmpty(result)) {
                                        try {
                                            JSONObject obj = new JSONObject(result);
                                            if (!obj.isNull("b")) {
                                                JSONObject bobj = obj.getJSONObject("b");
                                                if (!bobj.isNull("szyb_new")) {
                                                    JSONObject listobj = bobj.getJSONObject("szyb_new");
                                                    if (!TextUtil.isEmpty(listobj.toString())) {
                                                        packNumericalForecastDown = new PackNumericalForecastDown();
                                                        packNumericalForecastDown.fillData(listobj.toString());
                                                        if (packNumericalForecastDown == null || packNumericalForecastDown.lmBeanList.size() == 0) {
                                                            return;
                                                        }
                                                        dealData();
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

    private void dealData() {
        for (int i = 0; i < packNumericalForecastDown.lmBeanList.size(); i++) {
            List<PackNumericalForecastDown.TitleListBean> TitleBeanList = new ArrayList<>();
            for (int j = 0; j < packNumericalForecastDown.TitleBeanList.size(); j++) {
                if (packNumericalForecastDown.TitleBeanList.get(j).lm.equals(packNumericalForecastDown.lmBeanList.get(i).id)) {
                    TitleBeanList.add(packNumericalForecastDown.TitleBeanList.get(j));
                }
            }
        }
        changeValue();
    }

    /**
     * 子标题改变
     */
    private void changeValue() {
        n_content.setText("");
        if (packNumericalForecastDown == null) {
            return;
        }
        spinner_data.clear();
        for (int i = 0; i < packNumericalForecastDown.TitleBeanList.size(); i++) {
            spinner_data.add(packNumericalForecastDown.TitleBeanList.get(i).title );
        }
        if (spinner_data.size() > 1) {
            image_left.setVisibility(View.VISIBLE);
            image_right.setVisibility(View.VISIBLE);
        } else {
            image_left.setVisibility(View.GONE);
            image_right.setVisibility(View.GONE);
        }
        if (spinner_data.size() > 0 && spinner_data.size() > tiemposition) {
            spinner_text.setText(spinner_data.get(tiemposition));
        } else {
            spinner_text.setText("");
        }

        if (packNumericalForecastDown.TitleBeanList.get(tiemposition).type.equals("1")) {
            content_scrollview.setVisibility(View.VISIBLE);
            image_show.setVisibility(View.GONE);
            n_content.setTextColor(getResources().getColor(R.color.text_black));
            n_content.setText(packNumericalForecastDown.TitleBeanList.get(tiemposition).str);
        } else if (packNumericalForecastDown.TitleBeanList.get(tiemposition).type.equals("2")) {
            content_scrollview.setVisibility(View.GONE);
            image_show.setVisibility(View.VISIBLE);
            showProgressDialog();
            if (!TextUtils.isEmpty(packNumericalForecastDown.TitleBeanList.get(tiemposition).img)) {
                final String imageKey = getString(R.string.file_download_url)+packNumericalForecastDown.TitleBeanList.get(tiemposition).img;
                getImageFetcher().addListener(new ListenerImageLoad() {
                    @Override
                    public void done(String key, boolean isSucc) {
                        if (imageKey.equals(key)) {
                            dismissProgressDialog();
                            if (isSucc && getImageFetcher().getImageCache() != null) {
                                Bitmap bm = getImageFetcher().getImageCache().getBitmapFromAllCache(key).getBitmap();
                                image_show.setMyImageBitmap(bm);
                            } else {
                                showToast("图片为空");
                            }
                        }
                    }
                });
                getImageFetcher().loadImage(imageKey, null, ImageConstant.ImageShowType.NONE);
            } else {
                showToast("服务器不存在这张图标");
            }
        } else if (packNumericalForecastDown.TitleBeanList.get(tiemposition).type.equals("3")) {
            content_scrollview.setVisibility(View.GONE);
            image_show.setVisibility(View.GONE);
        } else {
            content_scrollview.setVisibility(View.VISIBLE);
            image_show.setVisibility(View.GONE);
            // 暂无数据
            n_content.setGravity(Gravity.CENTER);
            n_content.setText("暂无数据");
            n_content.setTextColor(getResources().getColor(R.color.bg_black_alpha20));
        }
    }

}
