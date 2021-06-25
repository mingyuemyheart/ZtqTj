package com.pcs.ztqtj.view.activity.service;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.pcs.lib.lib_pcs_v3.model.image.ImageConstant;
import com.pcs.lib_ztqfj_v2.model.pack.net.service.PackServiceTwoChannelDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.service.ServiceChannelInfo;
import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.utils.TextUtil;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.util.OkHttpUtil;
import com.pcs.ztqtj.view.activity.web.FragmentActivityZtqWithHelp;

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
 * 专项服务-地区点击
 */
public class ActivityServerSecond extends FragmentActivityZtqWithHelp {

    // 气象类别
    private static String DECISION = "jc";// 决策
    private static String INDESTRY = "hy";// 行业
    private static String NEAR = "lj";// 临近
    private String currTab = DECISION;
    private MyGridViewAdapter mGridViewAdapter;
    private List<ServiceChannelInfo> serviceChannelList = new ArrayList<>();
    private String area_id = "";// 地区ID 测试
    private boolean show_warn = false;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_server_second);
        setTitleText(getResources().getString(R.string.weather_service));
        createImageFetcher();

        // 初始化单选按钮
        initRadioButton();
        // 初始化GridView
        initGridView();
        // 设置默认显示
        setDefaultDisplay();
    }

    /**
     * 初始化单选按钮
     */
    private void initRadioButton() {
        RadioButton radio;
        // 决策
        radio = (RadioButton) findViewById(R.id.radio_decision);
        radio.setOnCheckedChangeListener(mRadioListener);
        // 行业
        radio = (RadioButton) findViewById(R.id.radio_industry);
        radio.setOnCheckedChangeListener(mRadioListener);
        // 临近
        radio = (RadioButton) findViewById(R.id.radio_near);
        radio.setOnCheckedChangeListener(mRadioListener);
    }

    /**
     * 初始化GridView
     */
    private void initGridView() {
        GridView mGridView = findViewById(R.id.gridview);
        mGridViewAdapter = new MyGridViewAdapter(this);
        mGridView.setAdapter(mGridViewAdapter);
    }

    private void setDefaultDisplay() {
        RadioButton radio = findViewById(R.id.radio_decision);
        radio.setChecked(true);
        // 刷新箭头图标
        refreshArrowIcon(DECISION, true);
        getServiceChannelList(DECISION);
    }

    /**
     * 刷新箭头图标
     */
    private void refreshArrowIcon(String type, boolean isShow) {
        View view;
        if (TextUtils.equals(INDESTRY, type)) {
            // 行业
            view = findViewById(R.id.iv_industry);
        } else {
            // 决策
            view = findViewById(R.id.iv_decision);
        }

        if (isShow) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 显示介绍
     */
    private void refreshIntroduce(String type, String remark) {
        TextView introduce = (TextView) findViewById(R.id.text_decision_introduce);
        TextView content = (TextView) findViewById(R.id.text_decision_content);
        if (TextUtils.equals(INDESTRY, type)) {
            // 行业
            introduce.setText(R.string.industry_introduce);
            content.setText(Html.fromHtml(remark));
        } else if (TextUtils.equals(NEAR, type)) {
            // 临近
            introduce.setText(R.string.near_introduce);
            content.setText(Html.fromHtml(remark));
        } else {
            // 决策
            introduce.setText(R.string.decision_introduce);
            content.setText(Html.fromHtml(remark));
        }
    }

    /**
     * 单选按钮点击事件
     */
    private OnCheckedChangeListener mRadioListener = new OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            switch (buttonView.getId()) {
                case R.id.radio_decision:
                    show_warn = false;
                    // --------决策气象
                    // 刷新箭头图标
                    refreshArrowIcon(DECISION, isChecked);
                    if (isChecked) {
                        getServiceChannelList(DECISION);
                    }
                    break;
                case R.id.radio_industry:
                    show_warn = false;
                    // --------行业气象
                    // 刷新箭头图标
                    refreshArrowIcon(INDESTRY, isChecked);
                    if (isChecked) {
                        getServiceChannelList(INDESTRY);
                    }
                    break;
                case R.id.radio_near:
                    show_warn = false;
                    // --------临近气象
                    // 刷新箭头图标
                    refreshArrowIcon(NEAR, isChecked);
                    if (isChecked) {
                        getServiceChannelList(NEAR);
                    }
                    break;
            }
        }
    };

    /**
     * GridView适配器
     */
    private class MyGridViewAdapter extends BaseAdapter {

        private Context mContext;

        public MyGridViewAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getCount() {
            if (serviceChannelList == null) {
                return 0;
            }
            return serviceChannelList.size();
        }

        @Override
        public Object getItem(int position) {
            if (serviceChannelList == null) {
                return null;
            }
            return position;
        }

        @Override
        public long getItemId(int position) {
            if (serviceChannelList == null) {
                return 0;
            }
            return position;
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            Holder holder;
            if (view == null) {
                holder = new Holder();
                view = LayoutInflater.from(mContext).inflate(R.layout.item_server_second, null);
                holder.itemImageView = (ImageView) view.findViewById(R.id.itemImageView);
                holder.itemimageview_top = (ImageView) view.findViewById(R.id.itemimageview_top);
                holder.itemName = (TextView) view.findViewById(R.id.itemName);
                view.setTag(holder);
            } else {
                holder = (Holder) view.getTag();
            }

            final ServiceChannelInfo info = serviceChannelList.get(position);
            if (!TextUtils.isEmpty(info.icon)) {
                String url = mContext.getString(R.string.file_download_url)+ info.icon;
                getImageFetcher().loadImage(url, holder.itemImageView, ImageConstant.ImageShowType.SRC);
            }

            holder.itemimageview_top.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    String title = "决策气象服务";
                    if (TextUtils.equals(currTab, DECISION)) {
                        title = "决策气象服务";
                    } else if (TextUtils.equals(currTab, INDESTRY)) {
                        title = "行业气象服务";
                    } else if (TextUtils.equals(currTab, NEAR)) {
                        title = "临近气象服务";
                    } else {
                        title = "决策气象服务";
                    }

                    Intent it = new Intent();
                    it.setClass(mContext, ActivityServeThird.class);
                    it.putExtra("channel_id", info.id);
                    it.putExtra("area_id", area_id);
                    show_warn = true;
                    it.putExtra("area_name", area_name);
                    it.putExtra("title", info.ch_name);
                    it.putExtra("show_warn", show_warn);
//					 it.putExtra("title", title);
                    mContext.startActivity(it);
                }
            });
            holder.itemName.setText(info.ch_name);

            return view;
        }

        private class Holder {
            public ImageView itemImageView;
            public ImageView itemimageview_top;
            public TextView itemName;
        }
    }

    private String area_name = "";

    /**
     * 获取二级栏目
     * @param type "jc/hy"//各区模块中决策和行业，必填
     */
    private void getServiceChannelList(String type) {
        if (!isOpenNet()) {
            showToast(getString(R.string.net_err));
            return;
        }

        serviceChannelList.clear();
        mGridViewAdapter.notifyDataSetChanged();

        currTab = type;
        area_id = getIntent().getStringExtra("area_id");
        area_name = getIntent().getStringExtra("area_name");

        okHttpQxfwTwoChannel();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }

    /**
     * 获取专项服务-专项服务数据
     */
    private void okHttpQxfwTwoChannel() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String area_id = getIntent().getStringExtra("area_id");
                    JSONObject param  = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    JSONObject info = new JSONObject();
                    info.put("stationId", area_id);
                    info.put("extra", currTab);
                    param.put("paramInfo", info);
                    String json = param.toString();
                    final String url = CONST.BASE_URL+"qxfw_two_channel";
                    Log.e("qxfw_two_channel", url);
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
                                    Log.e("qxfw_two_channel", result);
                                    if (!TextUtil.isEmpty(result)) {
                                        try {
                                            JSONObject obj = new JSONObject(result);
                                            if (!obj.isNull("b")) {
                                                JSONObject bobj = obj.getJSONObject("b");
                                                if (!bobj.isNull("qxfw_two_channel")) {
                                                    JSONObject qxfw_two_channel = bobj.getJSONObject("qxfw_two_channel");
                                                    if (!TextUtil.isEmpty(qxfw_two_channel.toString())) {
                                                        PackServiceTwoChannelDown packServiceTwoChannelDown = new PackServiceTwoChannelDown();
                                                        packServiceTwoChannelDown.fillData(qxfw_two_channel.toString());
                                                        serviceChannelList = packServiceTwoChannelDown.serviceChannelList;

                                                        mGridViewAdapter.notifyDataSetChanged();
                                                        // 刷新介绍
                                                        refreshIntroduce(currTab, packServiceTwoChannelDown.remark);
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
