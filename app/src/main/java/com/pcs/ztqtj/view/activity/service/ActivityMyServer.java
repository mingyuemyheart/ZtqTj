package com.pcs.ztqtj.view.activity.service;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.AdapterServerMyServer;
import com.pcs.ztqtj.control.tool.MyConfigure;
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
import com.pcs.lib_ztqfj_v2.model.pack.net.PackQxfuMyproV2Down;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackQxfwAuthenticationProductDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackQxfwAuthenticationProductUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackQxfwMyproV2Up;

/**
 * @author Z 气象服务-我的服务 getintent()中 subtitle--是否显示副标题：-决策报告- 0为不显示，其他只为显示
 */
public class ActivityMyServer extends FragmentActivityZtqWithHelp {
    private PackLocalUser localUserinfo;
    private MyReceiver receiver = new MyReceiver();
    private TextView null_data;
    private ExpandableListView explistviw;
    private AdapterServerMyServer listAdatper;
    private PackQxfwMyproV2Up up;
    private PackQxfuMyproV2Down down;
    private String channel = "";
    private String showSubtitle = "1";

    private String url = "";
    private String channel_title = "";
    private String style = "";
    private String article_title = "";

    private PackQxfuMyproV2Down.DesServer currentDes;
    private PackQxfuMyproV2Down.SubClassList currentSubClass;

    private boolean isClickMore = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myserve);
        proDeal();
        initView();
        initEvent();
        initData();
        //chackIsJcbg();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (listAdatper!=null){
            listAdatper.notifyDataSetChanged();
        }
        PcsDataBrocastReceiver.registerReceiver(this, receiver);
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(receiver);
    }



    private void chackIsJcbg() {
        if ("1".equals(channel)) {
            if (!TextUtils.isEmpty(ZtqCityDB.getInstance().getMyInfo().user_id)) {
                initData();
            } else {
                showCheckUserIdDialog(getResources().getString(R.string.text_islogin_tips));
            }
        } else {
            initData();
        }
    }

    private DialogTwoButton myDialogLogin;
    private TextView messageTextViewLogin;

    private void showCheckUserIdDialog(String msg) {
        View view = LayoutInflater.from(ActivityMyServer.this).inflate(R.layout.dialog_message, null);
        if (myDialogLogin == null) {
            messageTextViewLogin = (TextView) view.findViewById(R.id.dialogmessage);
            messageTextViewLogin.setText(msg);
            messageTextViewLogin.setTextColor(getResources().getColor(R.color.text_color));
            myDialogLogin = new DialogTwoButton(ActivityMyServer.this, view, "登录", "帮助", new DialogListener() {
                @Override
                public void click(String str) {
                    myDialogLogin.dismiss();
                    if (str.equals("登录")) {
                        Intent intent = null;
                        intent = new Intent(ActivityMyServer.this, AcitvityServeLogin.class);
                        startActivityForResult(intent, MyConfigure.RESULT_SERVICE_THREE);
                    } else if (str.equals("帮助")) {
                        Intent intentHelp = new Intent(ActivityMyServer.this, ActivityHelp.class);
                        startActivityForResult(intentHelp, MyConfigure.RESULT_HELP_VIEW);
                    } else {
                        finish();
                    }
                }
            });
            myDialogLogin.setTitle("天津气象提示");
            myDialogLogin.showCloseBtn();
        }

        messageTextViewLogin.setText(msg);
        myDialogLogin.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (MyConfigure.RESULT_SERVICE_THREE == requestCode && resultCode == Activity.RESULT_OK) {
            //chackIsJcbg();
        } else if (MyConfigure.RESULT_HELP_VIEW == requestCode) {
            //chackIsJcbg();
        }
        if(resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case ServiceLoginTool.SERVICE_RESULT:
                    PackLocalUser info = ZtqCityDB.getInstance().getMyInfo();
                    if(info != null && !TextUtils.isEmpty(info.user_id)) {
                        if (isClickMore) {
                            gotoServiceMore();
                        } else {
                            gotoServiceDetail();
                        }
                    }
                    break;
            }
        }
    }

    /**
     * 加载广播，设置标题
     */
    private void proDeal() {
        String title = getIntent().getStringExtra("title");
        setTitleText(title);

        showSubtitle = getIntent().getStringExtra("subtitle");// 是否显示副标题：-决策报告-
        // 0为不显示，其他只为显示
        channel = getIntent().getStringExtra("channel");

    }

    private void initView() {
        explistviw = (ExpandableListView) findViewById(R.id.myexlistviw);
        null_data = (TextView) findViewById(R.id.null_data);
    }

    private void initEvent() {
        explistviw.setOnGroupClickListener(new OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return true;
            }
        });
        explistviw.setOnChildClickListener(new OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition,
                                        long id) {
                isClickMore = false;
                currentDes = (PackQxfuMyproV2Down.DesServer) listAdatper.getChild(groupPosition, childPosition);
                currentSubClass = (PackQxfuMyproV2Down.SubClassList) listAdatper.getGroup(groupPosition);
                ServiceLoginTool.getInstance().reqLoginQuery();
                showProgressDialog();
                return true;
            }
        });


    }

    private boolean show_warn = true;

    private void IntentNextActivity() {
        Intent intent = new Intent(ActivityMyServer.this, ActivityServeDetails.class);
        SharedPreferencesUtil.putData(url,url);
        intent.putExtra("url", url);
        intent.putExtra("title", channel_title);
        intent.putExtra("channelid", channel);
        intent.putExtra("show_warn", show_warn);
        intent.putExtra("style", style);
        intent.putExtra("article_title", article_title);
        //intent.putExtra("is_show_share","1");// 是否显示分享按钮(1显示,0不显示)
        startActivity(intent);
    }

    private PackQxfwAuthenticationProductDown checkDown;
    private PackQxfwAuthenticationProductUp checkUp;

    private void checkPass(String proId) {
        if (TextUtils.isEmpty(localUserinfo.user_id)) {

        } else {


            if(!isOpenNet()){
                showToast(getString(R.string.net_err));
                return ;
            }

            checkDown = new PackQxfwAuthenticationProductDown();
            checkUp = new PackQxfwAuthenticationProductUp();
            checkUp.product_id = proId;
            checkUp.user_id = localUserinfo.user_id;
            showProgressDialog();
            PcsDataDownload.addDownload(checkUp);
        }
    }

    private PackQxfuMyproV2Down.SubClassList moreBean;

    private void initData() {
        show_warn = getIntent().getBooleanExtra("show_warn", true);
        localUserinfo = ZtqCityDB.getInstance().getMyInfo();
        listAdatper = new AdapterServerMyServer(ActivityMyServer.this, showSubtitle);
        listAdatper.setMoreListener(new AdapterServerMyServer.MoreClickListener() {
            @Override
            public void onClick(PackQxfuMyproV2Down.SubClassList bean) {
                showProgressDialog();
                ServiceLoginTool.getInstance().reqLoginQuery();
                isClickMore = true;
                currentDes = null;
                currentSubClass = null;
                moreBean = bean;
            }
        });
        explistviw.setAdapter(listAdatper);
        req();
    }

    private void req() {


        if(!isOpenNet()){
            showToast(getString(R.string.net_err));
            return ;
        }

        showProgressDialog();
        up = new PackQxfwMyproV2Up();
        if (TextUtils.isEmpty(localUserinfo.user_id)) {
            up.user_id = "";
        } else {
            up.user_id = localUserinfo.user_id;
        }
        // up.user_id = "201503241118444";
        up.channel_id = channel;
        PcsDataDownload.addDownload(up);
        down = new PackQxfuMyproV2Down();
    }

    private void gotoServiceMore() {
        if (isClickMore && moreBean != null) {
            Intent intent = new Intent(ActivityMyServer.this, ActivityMyServerMore.class);
            intent.putExtra("channel_id", moreBean.channel_id);
            intent.putExtra("channel_name", moreBean.channel_name);
            intent.putExtra("show_warn", show_warn);
            intent.putExtra("org_id", moreBean.org_id);
            intent.putExtra("org_name", moreBean.org_name);
            ActivityMyServer.this.startActivity(intent);
        }
    }

    private void gotoServiceDetail() {
        if (currentDes != null && currentSubClass != null) {
            url = currentDes.html_url;
            style = currentDes.style;
            //checkPass(currentDes.id);
            channel_title = currentSubClass.channel_name;
            article_title = currentSubClass.org_name + "发布了《" + currentDes.title + "》，请查阅。";
            if(currentDes.type.equals("1")) {
                IntentNextActivity();
            } else {
                View view = LayoutInflater.from(ActivityMyServer.this).inflate(R.layout.dialog_message, null);
                TextView messageText = (TextView) view.findViewById(R.id.dialogmessage);
                messageText.setText(getResources().getString(R.string.empty_promess_service));
                messageText.setTextColor(getResources().getColor(R.color.text_color));
                myDialogToNext = new DialogTwoButton(ActivityMyServer.this, view, "帮助", "返回",
                        new DialogListener() {
                            @Override
                            public void click(String str) {
                                myDialogToNext.dismiss();
                                if (str.equals("帮助")) {
                                    Intent intent = null;
                                    intent = new Intent(ActivityMyServer.this, ActivityHelp.class);
                                    startActivity(intent);
                                }
                            }
                        });
                myDialogToNext.setTitle("天津气象提示");
                myDialogToNext.showCloseBtn();
                myDialogToNext.show();
            }
        }
    }

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
            if (up != null && name.equals(up.getName())) {
                down = (PackQxfuMyproV2Down) PcsDataManager.getInstance().getNetPack(name);
                if (down != null) {

                    dealWithData();
                }
            } else if (checkUp != null && name.equals(checkUp.getName())) {
                dismissProgressDialog();
                checkDown = (PackQxfwAuthenticationProductDown) PcsDataManager.getInstance().getNetPack(name);
                if (checkDown != null) {
                    if (checkDown.auth_pass) {
                        IntentNextActivity();
                    } else {
                        View view = LayoutInflater.from(ActivityMyServer.this).inflate(R.layout.dialog_message, null);
                        TextView messageText = (TextView) view.findViewById(R.id.dialogmessage);
                        messageText.setText(getResources().getString(R.string.empty_promess_service));
                        messageText.setTextColor(getResources().getColor(R.color.text_color));
                        myDialogToNext = new DialogTwoButton(ActivityMyServer.this, view, "帮助", "返回",
                                new DialogListener() {
                                    @Override
                                    public void click(String str) {
                                        myDialogToNext.dismiss();
                                        if (str.equals("帮助")) {
                                            Intent intent = null;
                                            intent = new Intent(ActivityMyServer.this, ActivityHelp.class);
                                            startActivity(intent);
                                        }
                                    }
                                });
                        myDialogToNext.setTitle("天津气象提示");
                        myDialogToNext.showCloseBtn();
                        myDialogToNext.show();
                    }
                    checkDown = null;
                }

            }
            ServiceLoginTool.getInstance().callback(name, new ServiceLoginTool.CheckListener() {
                @Override
                public void onSuccess() {
                    dismissProgressDialog();
                    if (currentDes != null && currentSubClass != null) {
                        gotoServiceDetail();
                    }
                    if (isClickMore && moreBean != null) {
                        gotoServiceMore();
                    }

                }

                @Override
                public void onFail() {
                    // 退出登录
                    logout();
                    dismissProgressDialog();
                    ServiceLoginTool.getInstance().createAlreadyLogined(ActivityMyServer.this);
                }
            });
        }
    }

    DialogTwoButton myDialogToNext;

    /**
     * 处理数据
     */
    private void dealWithData() {
        dismissProgressDialog();
        try {
            if (down.auth_pass) {
                // 有权限的时候
                listAdatper.setData(down.classList);
                for (int j = 0; j < listAdatper.getGroupCount(); j++) {
                    explistviw.expandGroup(j);
                }
                if (down.classList == null || down.classList.size() == 0) {
                    null_data.setVisibility(View.VISIBLE);
                    explistviw.setVisibility(View.GONE);
                } else {
                    null_data.setVisibility(View.GONE);
                    explistviw.setVisibility(View.VISIBLE);
                }
            } else {
                // 没权限时
                if (channel.equals("1")) {
                    showCheckTipsDialog("当前账号无“决策报告”使用权限，如需开通，请查阅帮助信息！");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private DialogTwoButton myDialogCom;
    private TextView messageTextViewCom;

    private void showCheckTipsDialog(String msg) {
        View view = LayoutInflater.from(ActivityMyServer.this).inflate(R.layout.dialog_message, null);
        if (myDialogCom == null) {
            messageTextViewCom = (TextView) view.findViewById(R.id.dialogmessage);
            messageTextViewCom.setText(msg);
            messageTextViewCom.setTextColor(getResources().getColor(R.color.text_color));
            myDialogCom = new DialogTwoButton(ActivityMyServer.this, view, "帮助", "我的服务", new DialogListener() {
                @Override
                public void click(String str) {
                    myDialogCom.dismiss();
                    if (str.equals("帮助")) {
                        Intent intent = null;
                        intent = new Intent(ActivityMyServer.this, ActivityHelp.class);
                        startActivity(intent);
                    } else if (str.equals("我的服务")) {
                        channel = "";// 我的服务上传的为空
                        showSubtitle = "1";
                        setTitleText("我的服务");
                        initData();
                    } else if (str.equals("close")) {
                        finish();
                    } else {
                        finish();
                    }
                }
            });
            myDialogCom.setTitle("天津气象提示");
            myDialogCom.showCloseBtn();
        }
        messageTextViewCom.setText(msg);
        myDialogCom.show();
    }
}