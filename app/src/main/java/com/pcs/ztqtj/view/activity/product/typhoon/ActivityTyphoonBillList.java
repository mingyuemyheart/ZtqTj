package com.pcs.ztqtj.view.activity.product.typhoon;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.pcs.lib.lib_pcs_v3.control.tool.Util;
import com.pcs.lib_ztqfj_v2.model.pack.net.column.ColumnInfo;
import com.pcs.lib_ztqfj_v2.model.pack.net.column.PackColumnDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.PackWarningCenterTfggsjDown;
import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.typhoon.AdapterTyphoonBill;
import com.pcs.ztqtj.control.tool.utils.TextUtil;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.util.OkHttpUtil;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
import com.pcs.ztqtj.view.activity.web.WebViewWithShare;

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
 * 台风路径-警报单
 */
public class ActivityTyphoonBillList extends FragmentActivityZtqBase {

    private RadioGroup radioGroup;
    private ListView listView = null;
    // 数据
    private List<PackWarningCenterTfggsjDown.WarnTFGGSJ> datalist = new ArrayList<>();

    // 适配器
    private AdapterTyphoonBill adapter = null;
    private List<ColumnInfo> columnInfoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_typhoon_bill_list);
        setTitleText(R.string.typhoon_bill_list);
        initView();
        initEvent();
        initData();
    }

    private void initView() {
        radioGroup = (RadioGroup) findViewById(R.id.radiogroup);
        listView = (ListView) findViewById(R.id.warnlist);
    }

    private void initEvent() {
        radioGroup.setOnCheckedChangeListener(onCheckedChangeListener);
        listView.setOnItemClickListener(onItemClickListener);
    }

    private void initData() {
        adapter = new AdapterTyphoonBill(this, datalist);
        listView.setAdapter(adapter);
        okHttpList();
    }

    private void initTable(List<ColumnInfo> list) {
        columnInfoList = list;
        int width = Util.getScreenWidth(this);
        int radioWidth = width / list.size();
        int pad = Util.dip2px(this, 6);
        for(int i = 0; i < list.size(); i++) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setId(101 + i);
            radioButton.setTextColor(getResources().getColor(R.color.text_black));
            radioButton.setBackgroundResource(R.drawable.btn_warn_radiobutton_select);
            radioButton.setPadding(0, pad, 0, pad);
            radioButton.setMaxLines(1);
            radioButton.setGravity(Gravity.CENTER);
            radioButton.setTextSize(18);
            radioButton.setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));
            radioButton.setText(list.get(i).name);

            //radioButton.setOnClickListener(onTimeTableListener);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(radioWidth, ViewGroup.LayoutParams.MATCH_PARENT);
            radioGroup.addView(radioButton, lp);
        }
        if(list.size() > 0 && radioGroup.getChildCount() > 0) {
            radioGroup.check(radioGroup.getChildAt(0).getId());
        }
    }

    // 处理数据
    private void dealwithData(PackWarningCenterTfggsjDown down) {
        datalist.clear();
        datalist.addAll(down.list);
        adapter.notifyDataSetChanged();
    }

    /**
     * 清空警报单列表
     */
    private void clearList() {
        datalist.clear();
        adapter.notifyDataSetChanged();
    }

    private void clickButton(int index) {
        if(columnInfoList.size() > index) {
            okHttpDetail(columnInfoList.get(index).type);
        }
    }

    private void gotoWebview(String url) {
        Intent intent = new Intent(this, WebViewWithShare.class);
        intent.putExtra("title", "台风警报单");
        intent.putExtra("url", url);
        startActivity(intent);
    }

    private RadioGroup.OnCheckedChangeListener onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
            clickButton(checkedId-101);
        }
    };

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            gotoWebview(getString(R.string.file_download_url) + datalist.get(position).html_path);
        }
    };

    /**
     * 获取警报单列表数据
     */
    private void okHttpList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param  = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    JSONObject info = new JSONObject();
                    param.put("paramInfo", info);
                    String json = param.toString();
                    Log.e("typhoon_megs", json);
                    final String url = CONST.BASE_URL+"typhoon_megs";
                    Log.e("typhoon_megs", url);
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
                                        try {
                                            JSONObject obj = new JSONObject(result);
                                            if (!obj.isNull("b")) {
                                                JSONObject bobj = obj.getJSONObject("b");
                                                if (!bobj.isNull("column")) {
                                                    JSONObject column = bobj.getJSONObject("column");
                                                    PackColumnDown down = new PackColumnDown();
                                                    down.fillData(column.toString());
                                                    initTable(down.arrcolumnInfo);
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
     * 获取警报单具体数据
     */
    private void okHttpDetail(final String type) {
        clearList();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param  = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    JSONObject info = new JSONObject();
                    info.put("extra", type);
                    param.put("paramInfo", info);
                    String json = param.toString();
                    Log.e("pub_warn_acci_health", json);
                    final String url = CONST.BASE_URL+"pub_warn_acci_health";
                    Log.e("pub_warn_acci_health", url);
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
                                        try {
                                            JSONObject obj = new JSONObject(result);
                                            if (!obj.isNull("b")) {
                                                JSONObject bobj = obj.getJSONObject("b");
                                                if (!bobj.isNull("pub_warn_acci_health")) {
                                                    JSONObject pub_warn_acci_health = bobj.getJSONObject("pub_warn_acci_health");
                                                    PackWarningCenterTfggsjDown packDown = new PackWarningCenterTfggsjDown();
                                                    packDown.fillData(pub_warn_acci_health.toString());
                                                    dealwithData(packDown);
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
