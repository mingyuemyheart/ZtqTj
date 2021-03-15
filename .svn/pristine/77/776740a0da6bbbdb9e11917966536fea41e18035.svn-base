package com.pcs.ztqtj.view.activity.service;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.view.activity.web.FragmentActivityZtqWithHelp;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib.lib_pcs_v3.model.image.AsyncTask;
import com.pcs.lib.lib_pcs_v3.model.image.ImageConstant;
import com.pcs.lib_ztqfj_v2.model.pack.net.service.PackServiceTwoChannelDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.service.PackServiceTwoChannelUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.service.ServiceChannelInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 气象生活第二页
 *
 * @author JiangZy
 */
public class ActivityServerSecond extends FragmentActivityZtqWithHelp {

    // 气象类别
    private static int DECISION = 1;// 决策
    private static int INDESTRY = 2;// 行业
    private static int NEAR = 3;// 临近

    private int currTab = 1;

    private GridView mGridView;
    private MyGridViewAdapter mGridViewAdapter;

    private MyReceiver receiver = new MyReceiver();
    private PackServiceTwoChannelUp packServiceTwoChannelUp = new PackServiceTwoChannelUp();
    private List<ServiceChannelInfo> serviceChannelList = new ArrayList<ServiceChannelInfo>();

    private String area_id = "";// 地区ID 测试

    private boolean show_warn = false;
    private LinearLayout ll_bottom_button;

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
        PcsDataBrocastReceiver.registerReceiver(ActivityServerSecond.this,
                receiver);
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
        mGridView = (GridView) findViewById(R.id.gridview);
        mGridViewAdapter = new MyGridViewAdapter(this);
        mGridView.setAdapter(mGridViewAdapter);
        ll_bottom_button= (LinearLayout) findViewById(R.id.ll_bottom_button);
    }

    private void setDefaultDisplay() {
        RadioButton radio = (RadioButton) findViewById(R.id.radio_decision);
        radio.setChecked(true);
        // 刷新箭头图标
        refreshArrowIcon(DECISION, true);
        getServiceChannelList(DECISION);
    }

    /**
     * 刷新箭头图标
     */
    private void refreshArrowIcon(int type, boolean isShow) {
        View view;
        if (type == INDESTRY) {
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
     * 刷新GridView
     */
    private void refreshGridView() {
        mGridViewAdapter.notifyDataSetChanged();
    }

    /**
     * 显示介绍
     */
    private void refreshIntroduce(int type, String remark) {
        TextView introduce = (TextView) findViewById(R.id.text_decision_introduce);
        TextView content = (TextView) findViewById(R.id.text_decision_content);
        if (type == INDESTRY) {
            // 行业
            introduce.setText(R.string.industry_introduce);
            content.setText(Html.fromHtml(remark));
        } else if (type == NEAR) {
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
     *
     * @author JiangZy
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
                view = LayoutInflater.from(mContext).inflate(
                        R.layout.item_server_second, null);
                holder.itemImageView = (ImageView) view
                        .findViewById(R.id.itemImageView);
                holder.itemimageview_top = (ImageView) view
                        .findViewById(R.id.itemimageview_top);

                holder.itemName = (TextView) view.findViewById(R.id.itemName);

                view.setTag(holder);
            } else {
                holder = (Holder) view.getTag();
            }

            final ServiceChannelInfo info = serviceChannelList.get(position);
            if (!TextUtils.isEmpty(info.icon)) {
                String url = ActivityServerSecond.this
                        .getString(R.string.file_download_url)
                        + "/"
                        + info.icon;
                AsyncTask asyncTask = getImageFetcher().loadImage(url, holder.itemImageView,
                        ImageConstant.ImageShowType.SRC);
            }

            holder.itemimageview_top.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    String title = "决策气象服务";
                    switch (currTab) {
                        case 1:
                            title = "决策气象服务";
                            break;
                        case 2:
                            title = "行业气象服务";
                            break;
                        case 3:
                            title = "临近气象服务";
                            break;
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
     *
     * @param channel_id
     */
    private void getServiceChannelList(int channel_id) {

        if (!isOpenNet()) {
            showToast(getString(R.string.net_err));
            return;
        }

        serviceChannelList.clear();
        mGridViewAdapter.notifyDataSetChanged();

        currTab = channel_id;
        area_id = getIntent().getStringExtra("area_id");
        area_name = getIntent().getStringExtra("area_name");
        if (packServiceTwoChannelUp == null) {
            packServiceTwoChannelUp = new PackServiceTwoChannelUp();
        }
        packServiceTwoChannelUp.area_id = area_id;
        packServiceTwoChannelUp.channel_id = channel_id + "";
        PcsDataDownload.addDownload(packServiceTwoChannelUp);
    }

    /**
     * 数据更新广播接收
     */
    private class MyReceiver extends PcsDataBrocastReceiver {

        @Override
        public void onReceive(String name, String error) {
            if (packServiceTwoChannelUp != null && packServiceTwoChannelUp.getName().equals(name)) {//
                PackServiceTwoChannelDown packServiceTwoChannelDown =
                        (PackServiceTwoChannelDown) PcsDataManager.getInstance().getNetPack(name);
                if (packServiceTwoChannelDown == null) {
                    return;
                }
                serviceChannelList = packServiceTwoChannelDown.serviceChannelList;


                refreshGridView();
                // 刷新介绍
                refreshIntroduce(currTab, packServiceTwoChannelDown.remark);
            }

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);
        finish();
        this.unregisterReceiver(receiver);
    }
}
