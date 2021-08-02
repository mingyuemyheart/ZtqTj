package com.pcs.ztqtj.view.activity.service;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalUser;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalUserInfo;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackQxfuMyproV2Down;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackQxfwAuthenticationProductDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackQxfwAuthenticationProductUp;
import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.AdapterServerMyServer;
import com.pcs.ztqtj.control.tool.ServiceLoginTool;
import com.pcs.ztqtj.control.tool.SharedPreferencesUtil;
import com.pcs.ztqtj.control.tool.utils.TextUtil;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.util.OkHttpUtil;
import com.pcs.ztqtj.view.activity.help.ActivityHelp;
import com.pcs.ztqtj.view.activity.web.FragmentActivityZtqWithHelp;
import com.pcs.ztqtj.view.dialog.DialogFactory.DialogListener;
import com.pcs.ztqtj.view.dialog.DialogTwoButton;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 专项服务-决策服务-我的服务 getintent()中 subtitle--是否显示副标题：-决策报告- 0为不显示，其他只为显示
 */
public class ActivityMyServer extends FragmentActivityZtqWithHelp {

    private PackLocalUser localUserinfo;
    private MyReceiver receiver = new MyReceiver();
    private TextView null_data;
    private ExpandableListView explistviw;
    private AdapterServerMyServer listAdatper;
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
        initData();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
        null_data = findViewById(R.id.null_data);
        explistviw = findViewById(R.id.myexlistviw);
        explistviw.setOnGroupClickListener(new OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return true;
            }
        });
        explistviw.setOnChildClickListener(new OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
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
        SharedPreferencesUtil.putData(getString(R.string.file_download_url)+url,getString(R.string.file_download_url)+url);
        intent.putExtra("url", getString(R.string.file_download_url)+url);
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

        okHttpInfoList();
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

        MyApplication.clearUserInfo(this);

        //刷新栏目数据
        Intent bdIntent = new Intent();
        bdIntent.setAction(CONST.BROADCAST_REFRESH_COLUMNN);
        sendBroadcast(bdIntent);
    }

    /**
     * 数据更新广播接收
     */
    private class MyReceiver extends PcsDataBrocastReceiver {
        @Override
        public void onReceive(String name, String error) {
            if (checkUp != null && name.equals(checkUp.getName())) {
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
    private void dealWithData(PackQxfuMyproV2Down down) {
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
                        startActivity(new Intent(ActivityMyServer.this, ActivityHelp.class));
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

    /**
     * 获取我的服务
     */
    private void okHttpInfoList() {
        showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param  = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    JSONObject info = new JSONObject();
                    info.put("stationId", "101030109");//101030301决策服务，101030109决策报告都是一样的数据
                    info.put("extra", "");
                    param.put("paramInfo", info);
                    String json = param.toString();
                    Log.e("info_list", json);
                    final String url = CONST.BASE_URL+"info_list";
                    Log.e("info_list", url);
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
                                    if (!TextUtil.isEmpty(result)) {
                                        Log.e("info_list", result);
                                        try {
                                            JSONObject obj = new JSONObject(result);
                                            if (!obj.isNull("b")) {
                                                JSONObject bobj = obj.getJSONObject("b");
                                                if (!bobj.isNull("qxfw_mypro_v2")) {
                                                    JSONObject qxfw_mypro_v2 = bobj.getJSONObject("qxfw_mypro_v2");
                                                    if (!TextUtil.isEmpty(qxfw_mypro_v2.toString())) {
                                                        PackQxfuMyproV2Down down = new PackQxfuMyproV2Down();
                                                        down.fillData(qxfw_mypro_v2.toString());
                                                        if (down != null) {
                                                            dealWithData(down);
                                                        }
                                                    }
                                                }
                                            } else {
                                                if (!obj.isNull("errorMessage")) {
                                                    String errorMessage = obj.getString("errorMessage");
                                                    if (errorMessage != null) {
                                                        Toast.makeText(ActivityMyServer.this, errorMessage, Toast.LENGTH_SHORT).show();
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