package com.pcs.ztqtj.view.activity.service;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalUser;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalUserInfo;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackQxfuMyproV2Down.DesServer;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackQxfwAuthenticationProductDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackQxfwMyproMoreDown;
import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.AdapterMyserverMore;
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
 * 专项服务-决策服务-我的服务-更多
 */
public class ActivityMyServerMore extends FragmentActivityZtqWithHelp {

    private ListView listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myserver_more);
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
    private AdapterMyserverMore adapter;
    private List<DesServer> pro_list;

    private void initView() {
        listview = findViewById(R.id.listview);
        null_data = findViewById(R.id.null_data);
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
    }

    public boolean show_warn = true;


    private void IntentNextActivity() {
        Intent intent = new Intent(ActivityMyServerMore.this, ActivityServeDetails.class);
        //存储用来判断已读跟未读的id
        SharedPreferencesUtil.putData(getString(R.string.file_download_url)+url, getString(R.string.file_download_url)+url);
        intent.putExtra("url", getString(R.string.file_download_url)+url);
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

    private void initData() {
        show_warn = getIntent().getBooleanExtra("show_warn", true);
        pro_list = new ArrayList<DesServer>();
        adapter = new AdapterMyserverMore(this, pro_list);
        listview.setAdapter(adapter);
        Intent intnet = getIntent();
        String channelId = intnet.getStringExtra("channel_id");
        String channel_name = intnet.getStringExtra("channel_name");
        String org_id = intnet.getStringExtra("org_id");
        org_name = intnet.getStringExtra("org_name");
        title = channel_name;
        setTitleText(title);
        TextView tv = (TextView) findViewById(R.id.myserver_subtitle);
        tv.setText(org_name);

        okHttpQxfwMyproMore(channelId, org_id);
    }

    private PackQxfwAuthenticationProductDown checkDown;

    private void checkPass(String type) {
        if (type.equals("1")) {
            IntentNextActivity();
        } else {
            showCheckTipsDialog(getResources().getString(R.string.empty_promess_service));
        }
    }

    /**
     * 登出当前账号
     */
    private void logout() {
        PackLocalUserInfo info = new PackLocalUserInfo();
        ZtqCityDB.getInstance().setMyInfo(info);
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

    private void dealWithData(PackQxfwMyproMoreDown down) {
        pro_list.addAll(down.pro_list);
        adapter.notifyDataSetChanged();
        if (down.pro_list == null || down.pro_list.size() == 0) {
            null_data.setVisibility(View.VISIBLE);
        } else {
            null_data.setVisibility(View.GONE);
        }
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

    /**
     * 获取更多
     */
    private void okHttpQxfwMyproMore(final String channelId, final String orgId) {
        showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param  = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    JSONObject info = new JSONObject();
                    info.put("stationId", channelId);
                    info.put("orgId", orgId);
                    param.put("paramInfo", info);
                    String json = param.toString();
                    final String url = CONST.BASE_URL+"qxfw_mypro_more";
                    Log.e("qxfw_mypro_more", url);
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
                                    Log.e("qxfw_mypro_more", result);
                                    if (!TextUtil.isEmpty(result)) {
                                        try {
                                            JSONObject obj = new JSONObject(result);
                                            if (!obj.isNull("b")) {
                                                JSONObject bobj = obj.getJSONObject("b");
                                                if (!bobj.isNull("qxfw_mypro_more")) {
                                                    JSONObject qxfw_mypro_more = bobj.getJSONObject("qxfw_mypro_more");
                                                    if (!TextUtil.isEmpty(qxfw_mypro_more.toString())) {
                                                        dismissProgressDialog();
                                                        PackQxfwMyproMoreDown down = new PackQxfwMyproMoreDown();
                                                        down.fillData(qxfw_mypro_more.toString());
                                                        if (down != null) {
                                                            dealWithData(down);
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

}