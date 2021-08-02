package com.pcs.ztqtj.view.activity.service;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib.lib_pcs_v3.model.image.AsyncTask;
import com.pcs.lib.lib_pcs_v3.model.image.ImageConstant;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalUser;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalUserInfo;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackQxfuMyproV2Down;
import com.pcs.lib_ztqfj_v2.model.pack.net.service.PackSHTwoChannelDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.service.PackSHTwoChannelUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.service.ServiceChannelInfo;
import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.ServiceLoginTool;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.view.activity.web.FragmentActivityZtqWithHelp;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Z 气象服务-我的服务 getintent()中 subtitle--是否显示副标题：-决策报告- 0为不显示，其他只为显示
 */
public class ActivityMyServerMain extends FragmentActivityZtqWithHelp implements View.OnClickListener {
    private PackLocalUser localUserinfo;
    private MyReceiver receiver = new MyReceiver();
    private boolean isClickMore = false;
    private TextView tv_myserver_main, tv_myserver_label;
    private GridView gridview;

    // 气象类别
    private static int DECISION = 1;// 决策
    private static int INDESTRY = 2;// 行业
    private static int NEAR = 3;// 临近
    private PackSHTwoChannelUp pack = new PackSHTwoChannelUp();
    private MyGridAdapter gridAdapter;
    private TextView text_myservice_introduce, text_myservice_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myserver_second);
        setTitleText("我的服务");
        initView();
        initEvent();
        initData();
        initRadioButton();
        setDefaultDisplay();
    }

    @Override
    protected void onResume() {
        super.onResume();
        PcsDataBrocastReceiver.registerReceiver(this, receiver);
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(receiver);
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case ServiceLoginTool.SERVICE_RESULT:
                    PackLocalUser info = ZtqCityDB.getInstance().getMyInfo();
                    if (info != null && !TextUtils.isEmpty(info.user_id)) {
                        if (isClickMore) {
                            gotoServiceMore();
                        }
                    }
                    break;
            }
        }
    }

    private boolean isFlag = true;

    private void initView() {
        tv_myserver_main = (TextView) findViewById(R.id.tv_myserver_main);
        tv_myserver_label = (TextView) findViewById(R.id.tv_myserver_label);
        text_myservice_introduce = (TextView) findViewById(R.id.text_myservice_introduce);
        text_myservice_content = (TextView) findViewById(R.id.text_myservice_content);
        gridview = (GridView) findViewById(R.id.gridview);
        gridAdapter = new MyGridAdapter(ActivityMyServerMain.this);
        gridview.setAdapter(gridAdapter);
//        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                isFlag=true;
//                showProgressDialog();
//                ServiceLoginTool.getInstance().reqLoginQuery();
//                info = serviceChannelList.get(i);
//            }
//        });
    }

    private void initEvent() {
        tv_myserver_main.setOnClickListener(this);
        tv_myserver_label.setOnClickListener(this);
    }

    private boolean show_warn = true;

    private PackQxfuMyproV2Down.SubClassList moreBean;

    private void initData() {
        // 0为不显示，其他只为显示
        show_warn = getIntent().getBooleanExtra("show_warn", true);
        localUserinfo = ZtqCityDB.getInstance().getMyInfo();
        req();
    }

    private void req() {


        if (!isOpenNet()) {
            showToast(getString(R.string.net_err));
            return;
        }
        // showProgressDialog();
    }

    private void gotoServiceMore() {
        if (isClickMore && moreBean != null) {
            Intent intent = new Intent(ActivityMyServerMain.this, ActivityMyServerMore.class);
            intent.putExtra("channel_id", moreBean.channel_id);
            intent.putExtra("channel_name", moreBean.channel_name);
            intent.putExtra("show_warn", show_warn);
            intent.putExtra("org_id", moreBean.org_id);
            intent.putExtra("org_name", moreBean.org_name);
            ActivityMyServerMain.this.startActivity(intent);
        }
    }


    private void setDefaultDisplay() {
        RadioButton radio = (RadioButton) findViewById(R.id.radio_decision);
        radio.setChecked(true);
        // 刷新箭头图标
        refreshArrowIcon(DECISION, true);

        getServiceChannelList(DECISION);
    }

    /**
     * 单选按钮点击事件
     */
    private CompoundButton.OnCheckedChangeListener mRadioListener = new CompoundButton.OnCheckedChangeListener() {

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
                        text_myservice_introduce.setText("决策报告简介");
                    }
                    break;
                case R.id.radio_industry:
                    show_warn = false;
                    // --------行业气象
                    // 刷新箭头图标
                    refreshArrowIcon(INDESTRY, isChecked);
                    if (isChecked) {
                        getServiceChannelList(INDESTRY);
                        text_myservice_introduce.setText("行业气象简介");
                    }
                    break;
                case R.id.radio_near:
                    show_warn = false;
                    // --------临近气象
                    // 刷新箭头图标
                    refreshArrowIcon(NEAR, isChecked);
//                    refreshArrowIcon(NEAR, isChecked);
                    if (isChecked) {
                        getServiceChannelList(NEAR);
                        text_myservice_introduce.setText("临近预报简介");
                    }
                    break;
            }
        }
    };

    /**
     * 刷新箭头图标
     */
    private void refreshArrowIcon(int type, boolean isShow) {
        View view;
        if (type == INDESTRY) {
            // 行业
            view = findViewById(R.id.iv_industry);
        }  else {
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
     * 获取二级栏目
     *
     * @param channel_id
     */
    private void getServiceChannelList(int channel_id) {
        if (!isOpenNet()) {
            showToast(getString(R.string.net_err));
            return;
        }
        pack.user_id = localUserinfo.user_id;
        pack.channel_id = channel_id + "";
        PcsDataDownload.addDownload(pack);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_myserver_main:
                onBackPressed();
                break;
            case R.id.tv_myserver_label:
                isFirst=false;
                isFlag=false;
                showProgressDialog();
                ServiceLoginTool.getInstance().reqLoginQuery();

                break;
            default:
                break;
        }
    }

    private List<ServiceChannelInfo> serviceChannelList = new ArrayList<>();

    private String cn_name ,id;
    private boolean isFirst=true;
    /**
     * 数据更新广播接收
     */
    private class MyReceiver extends PcsDataBrocastReceiver {
        @Override
        public void onReceive(final String name, String error) {
            if (name.equals(pack.getName())) {
                PackSHTwoChannelDown down = (PackSHTwoChannelDown) PcsDataManager.getInstance().getNetPack(name);
                if (down == null) {
                    return;
                }
                serviceChannelList.clear();
                serviceChannelList.addAll(down.serviceChannelList);
                text_myservice_content.setText(down.remark);
                gridAdapter.notifyDataSetChanged();
            }

            ServiceLoginTool.getInstance().callback(name, new ServiceLoginTool.CheckListener() {
                @Override
                public void onSuccess() {
                    dismissProgressDialog();
                    if (isFirst){

                    }else{
                        if (isFlag){
                            Intent it = new Intent();
                            it.setClass(ActivityMyServerMain.this, ActivityServeThird.class);
                            it.putExtra("channel_id", id);
                            it.putExtra("area_id", "00");
                            show_warn = true;
                            it.putExtra("area_name", cn_name);
                            it.putExtra("title", cn_name);
                            it.putExtra("show_warn", show_warn);
                            startActivity(it);
                        }else {
                            Intent intent = new Intent(ActivityMyServerMain.this, ActivityMyServer.class);
                            intent.putExtra("channel", "");
                            intent.putExtra("title", "我的服务");
                            intent.putExtra("subtitle", "1");
                            startActivity(intent);
                        }
                    }
                }

                @Override
                public void onFail() {
                    // 退出登录
                    logout();
                    dismissProgressDialog();
                    ServiceLoginTool.getInstance().createAlreadyLogined(ActivityMyServerMain.this);
                }
            });
        }
    }

    /**
     * 登出当前账号
     */
    private void logout() {
        PackLocalUserInfo info = new PackLocalUserInfo();
        ZtqCityDB.getInstance().setMyInfo(info);

        MyApplication.clearUserInfo(this);

        //刷新栏目数据
        Intent bdIntent = new Intent();
        bdIntent.setAction(CONST.BROADCAST_REFRESH_COLUMNN);
        sendBroadcast(bdIntent);
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * GridView适配器
     *
     * @author JiangZy
     */
    private class MyGridAdapter extends BaseAdapter {
        private Context mContext;

        public MyGridAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getCount() {
            return serviceChannelList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
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

            final ServiceChannelInfo   info = serviceChannelList.get(position);
            if (!TextUtils.isEmpty(info.icon)) {
                String url = ActivityMyServerMain.this
                        .getString(R.string.file_download_url)
                        + "/"
                        + info.icon;
                AsyncTask asyncTask = getImageFetcher().loadImage(url, holder.itemImageView,
                        ImageConstant.ImageShowType.SRC);
            }

            holder.itemimageview_top.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    showProgressDialog();
                    isFlag=true;
                    isFirst=false;
                    id=info.id;
                    cn_name=info.ch_name;
                    ServiceLoginTool.getInstance().reqLoginQuery();
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


}