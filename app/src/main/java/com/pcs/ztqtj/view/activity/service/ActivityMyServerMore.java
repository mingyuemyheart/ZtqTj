package com.pcs.ztqtj.view.activity.service;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.AdapterMyserverMore;
import com.pcs.ztqtj.control.tool.ServiceLoginTool;
import com.pcs.ztqtj.control.tool.SharedPreferencesUtil;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.view.activity.help.ActivityHelp;
import com.pcs.ztqtj.view.activity.web.FragmentActivityZtqWithHelp;
import com.pcs.ztqtj.view.dialog.DialogFactory.DialogListener;
import com.pcs.ztqtj.view.dialog.DialogTwoButton;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalUser;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalUserInfo;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackQxfuMyproV2Down.DesServer;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackQxfwAuthenticationProductDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackQxfwAuthenticationProductUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackQxfwMyproMoreDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackQxfwMyproMoreUp;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Z
 *         气象服务。更多栏目
 */
public class ActivityMyServerMore extends FragmentActivityZtqWithHelp {
    private ListView listview;
    private PackQxfwMyproMoreDown down;
    private PackQxfwMyproMoreUp listDataUp;
    private MyReceiver receiver = new MyReceiver();

    private final int serverResult = 103;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myserver_more);
        PcsDataBrocastReceiver.registerReceiver(this, receiver);
        localUserinfo = ZtqCityDB.getInstance().getMyInfo();
        initView();
        initEvent();
        initData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case ServiceLoginTool.SERVICE_RESULT:
                    PackLocalUser info = ZtqCityDB.getInstance().getMyInfo();
                    if (info != null && !TextUtils.isEmpty(info.user_id)) {
                        if (currentDes != null) {
                            url = currentDes.html_url;
                            id=currentDes.id;
                            style = currentDes.style;
                            checkPass(currentDes.type);
                        }
                    }
                    break;
            }
        }
    }

    private TextView null_data;
    private PackLocalUser localUserinfo;
    private AdapterMyserverMore adapter;
    private List<DesServer> pro_list;

    private void initView() {
        listview = (ListView) findViewById(R.id.listview);
        null_data = (TextView) findViewById(R.id.null_data);
    }

    private String url = "";
    private String title = "";
    private String style = "";
    private String org_name = "";
    private DesServer currentDes;
    private String id="";
    private void initEvent() {
        listview.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentDes = (DesServer) adapter.getItem(position);
                ServiceLoginTool.getInstance().reqLoginQuery();
                showProgressDialog();
            }
        });
        listview.setOnScrollListener(myOnScrollListener);
    }

    public boolean show_warn = true;


    private void IntentNextActivity() {
        Intent intent = new Intent(ActivityMyServerMore.this, ActivityServeDetails.class);
        //存储用来判断已读跟未读的id
        SharedPreferencesUtil.putData(url,url);
        intent.putExtra("url", url);
        intent.putExtra("title", title);
        intent.putExtra("show_warn", show_warn);
        intent.putExtra("style", style);
        intent.putExtra("article_title", org_name + "发布了《" + title + "》，请查阅。");
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter!=null){
            adapter.notifyDataSetChanged();
        }
    }

    private String channel_id;

    private void initData() {
        show_warn = getIntent().getBooleanExtra("show_warn", true);
        pro_list = new ArrayList<DesServer>();
        adapter = new AdapterMyserverMore(this, pro_list);
        listview.setAdapter(adapter);
        Intent intnet = getIntent();
        channel_id = intnet.getStringExtra("channel_id");
        String channel_name = intnet.getStringExtra("channel_name");
        String org_id = intnet.getStringExtra("org_id");
        org_name = intnet.getStringExtra("org_name");
        title = channel_name;
        setTitleText(title);
        TextView tv = (TextView) findViewById(R.id.myserver_subtitle);
        tv.setText(org_name);
        initReqData(channel_id, org_id);
        showProgressDialog();
        getNextPage();
    }

    private int pageSize = 1;

    private void initReqData(String channel_id, String org_id) {
        listDataUp = new PackQxfwMyproMoreUp();
        listDataUp.channel_id = channel_id;
        listDataUp.org_id = org_id;
        if (TextUtils.isEmpty(localUserinfo.user_id)) {
            listDataUp.user_id = "";
        } else {
            listDataUp.user_id = localUserinfo.user_id;
        }
    }

    private void getNextPage() {
        if (!isOpenNet()) {
            showToast(getString(R.string.net_err));
            return;
        }
        listDataUp.page_num = pageSize + "";
        PcsDataDownload.addDownload(listDataUp);
    }


    private PackQxfwAuthenticationProductDown checkDown;
    private PackQxfwAuthenticationProductUp cup;

    private void checkPass(String type) {
        if (type.equals("1")) {
            IntentNextActivity();
        } else {
            showCheckTipsDialog(getResources().getString(R.string.empty_promess_service));
        }
    }

//    private void checkPass(String proId) {
//        if (TextUtils.isEmpty(localUserinfo.user_id)) {
//
//        } else {
//            checkDown = new PackQxfwAuthenticationProductDown();
//            cup = new PackQxfwAuthenticationProductUp();
//            cup.product_id = proId;
//            cup.user_id = localUserinfo.user_id;
//            if (!isOpenNet()) {
//                showToast(getString(R.string.net_err));
//                return;
//            }
//            showProgressDialog();
//            PcsDataDownload.addDownload(cup);
//        }
//    }

    /**
     * 登出当前账号
     */
    private void logout() {
        PackLocalUserInfo info = new PackLocalUserInfo();
        ZtqCityDB.getInstance().setMyInfo(info);
    }

    /**
     * 数据更新广播接收
     */
    private class MyReceiver extends PcsDataBrocastReceiver {
        @Override
        public void onReceive(String name, String error) {

            if (listDataUp != null && name.equals(listDataUp.getName())) {
                down = (PackQxfwMyproMoreDown) PcsDataManager.getInstance().getNetPack(name);
                if (down != null) {
                    dealWithData();
                }
            } else if (checkDown != null && name.equals(cup.getName())) {
                dismissProgressDialog();
                checkDown = (PackQxfwAuthenticationProductDown) PcsDataManager.getInstance().getNetPack(name);
                if (checkDown != null) {
                    if (checkDown.auth_pass) {
                        IntentNextActivity();
                    } else {
                        showCheckTipsDialog(getResources().getString(R.string.empty_promess_service));
                    }
                    checkDown = null;
                }
            } else {
                ServiceLoginTool.getInstance().callback(name, listenerCheck);
            }
        }
    }


    private ServiceLoginTool.CheckListener listenerCheck = new ServiceLoginTool.CheckListener() {
        @Override
        public void onSuccess() {
            dismissProgressDialog();
            if (currentDes != null) {
                url = currentDes.html_url;
                id=currentDes.id;
                style = currentDes.style;
                checkPass(currentDes.type);
            }
        }

        @Override
        public void onFail() {
            logout();
            dismissProgressDialog();
            ServiceLoginTool.getInstance().createAlreadyLogined(ActivityMyServerMore.this);
        }
    };
    private boolean getMoreData = true;

    private void dealWithData() {
        dismissProgressDialog();
        if (down.pro_list.size() == 15) {
            pageSize++;
        } else {
            getMoreData = false;
        }
        pro_list.addAll(down.pro_list);
        adapter.notifyDataSetChanged();
        if (down.pro_list == null || down.pro_list.size() == 0) {
            null_data.setVisibility(View.VISIBLE);
        } else {
            null_data.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(receiver);
    }

    private DialogTwoButton myDialog;
    private TextView messageTextView;

    private void showCheckTipsDialog(String msg) {
        View view = LayoutInflater.from(ActivityMyServerMore.this).inflate(R.layout.dialog_message, null);
        if (myDialog == null) {
            messageTextView = (TextView) view.findViewById(R.id.dialogmessage);
            messageTextView.setText(msg);
            messageTextView.setTextColor(getResources().getColor(R.color.text_color));
            myDialog = new DialogTwoButton(ActivityMyServerMore.this, view, "帮助", "返回", new DialogListener() {
                @Override
                public void click(String str) {
                    myDialog.dismiss();
                    if (str.equals("帮助")) {
                        Intent intent = null;
                        intent = new Intent(ActivityMyServerMore.this, ActivityHelp.class);
                        startActivity(intent);
                    }
                }
            });
            myDialog.setTitle("天津气象提示");
            myDialog.showCloseBtn();
        }

        messageTextView.setText(msg);
        myDialog.show();
    }


    private AbsListView.OnScrollListener myOnScrollListener = new AbsListView.OnScrollListener() {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            // 当不滚动时
            if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                // 判断是否滚动到底部
                if (view.getLastVisiblePosition() == view.getCount() - 1) {
                    // 加载更多功能的代码
                    System.out.println("到了底部，加载更多");
//                    判断是否要加载更多
                    if (getMoreData) {
                        getNextPage();
                    }
                }
            }
        }

        @Override
        public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {

        }
    };


}