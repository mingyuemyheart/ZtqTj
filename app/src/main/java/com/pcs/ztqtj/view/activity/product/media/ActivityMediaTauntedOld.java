package com.pcs.ztqtj.view.activity.product.media;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pcs.lib.lib_pcs_v3.control.tool.Util;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackMediaTauntedDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackMediaTauntedListDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackMediaTauntedListUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackMediaTauntedUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.SuggestListInfo;
import com.pcs.lib_ztqfj_v2.model.pack.net.media.MediaInfo;
import com.pcs.lib_ztqfj_v2.model.pack.net.photowall.PackPhotoLoginDown;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.AdatperFeedBackList;
import com.pcs.ztqtj.control.tool.CommUtils;
import com.pcs.ztqtj.control.tool.LoginInformation;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
import com.pcs.ztqtj.view.activity.photoshow.ActivityPhotoLogin;
import com.pcs.ztqtj.view.myview.MyDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Z on 2016/6/3.
 * 影视吐槽
 */
public class ActivityMediaTauntedOld extends FragmentActivityZtqBase implements View.OnClickListener {
    /**
     * 输入法驱动
     */
    private InputMethodManager mIMM;
    private EditText emailContent;
    private EditText connectionway;
    private LinearLayout ll_feedback_main;
    private Button btn_tweet_login;
    private PackMediaTauntedUp uppack;
    private MyReceiver myReceiver = new MyReceiver();
    private PackMediaTauntedDown down = new PackMediaTauntedDown();
    private PackMediaTauntedListUp packTauntedListUp;
    /**
     * 退出登录提示框
     */
    private MyDialog exitDialog;
    private ListView lv_feedback_list;
    private AdatperFeedBackList adatperFeedBackList;
    private TextView tv;// Dialog中的提示信息
    private EditText edit_name_text;
    private String channel_id = "";//栏目id
    private Button commit_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        PcsDataBrocastReceiver.registerReceiver(ActivityMediaTauntedOld.this, myReceiver);
        MediaInfo mediaInfo = (MediaInfo) getIntent().getSerializableExtra("mediaInfo");
        String title = mediaInfo.title_two;
        channel_id = mediaInfo.channel_id;
        if (!TextUtils.isEmpty(title)) {
            setTitleText(title);
        } else {
            if (!TextUtils.isEmpty(mediaInfo.title)) {
                setTitleText(mediaInfo.title);
            } else {
                setTitleText("气象影视");
            }
        }
        initView();
        initData();
        initListener();
    }

    private void initView() {
        ll_feedback_main = (LinearLayout) findViewById(R.id.ll_feedback_main);
        ll_feedback_main.setOnClickListener(this);
        emailContent = (EditText) findViewById(R.id.feedbackinformation);
        connectionway = (EditText) findViewById(R.id.connectionway);
        btn_tweet_login = (Button) findViewById(R.id.btn_tweet_login);
        edit_name_text = (EditText) findViewById(R.id.edit_name_text);
        lv_feedback_list = (ListView) findViewById(R.id.lv_feedback_list);
        commit_content = (Button) findViewById(R.id.commit_content);
    }

    public List<SuggestListInfo> arrsuggestListInfo = new ArrayList<SuggestListInfo>();

    private void initData() {
        Intent intent = getIntent();
        mIMM = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (!LoginInformation.getInstance().hasLogin()) {
            setNoLoginStatus();
        } else {
            setLoginStatus();
        }
        initTextInfo();
        arrsuggestListInfo.clear();
        adatperFeedBackList = new AdatperFeedBackList(ActivityMediaTauntedOld.this, arrsuggestListInfo);
        lv_feedback_list.setAdapter(adatperFeedBackList);
        showProgressDialog();

        request();
    }

    // 设置没有登录的UI状态
    private void setNoLoginStatus() {
        // 显示登录按钮
        btn_tweet_login.setText("登录");
        connectionway.setEnabled(true);
        edit_name_text.setText("");
        edit_name_text.setTextColor(getResources().getColor(R.color.text_black));
    }

    // 设置已登录UI状态
    private void setLoginStatus() {
        // 显示退出按钮
        btn_tweet_login.setText("退出");
        edit_name_text.setEnabled(false);
        edit_name_text.setText(LoginInformation.getInstance().getUsername());
        edit_name_text.setTextColor(getResources().getColor(R.color.gray));
        String strPhone = LoginInformation.getInstance().getUserPhone();
        if (TextUtils.isEmpty(strPhone)) {
            connectionway.setHint(getString(R.string.feedback_eidtemail));
        } else {
            connectionway.setText(strPhone.substring(0, 3) + "****" + strPhone.substring(7, strPhone.length()));
        }
    }

    /**
     * 设置内容提示信息
     */
    private void initTextInfo() {
        if (!LoginInformation.getInstance().hasLogin()) {
            emailContent.setHint(getString(R.string.login_un_login));
        } else {
            if (TextUtils.isEmpty(LoginInformation.getInstance().getUserPhone())) {
                emailContent.setHint(getString(R.string.login_not_bind));
            } else {
                emailContent.setHint(getString(R.string.login_has_bind));
            }
        }
    }

    private void initListener() {
        commit_content.setOnClickListener(this);
        btn_tweet_login.setOnClickListener(new MOnclick());
        emailContent.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (!LoginInformation.getInstance().hasLogin()) {
                        //没有登录处理
                        showDescDialog("尊敬的用户，先请登录客户端");
                    } else {
                        if (TextUtils.isEmpty(connectionway.getText().toString().trim())) {
//                            手机号码为空
                            showDescDialog("尊敬的用户，请输入手机号码再发布建议");
                        }
                    }
                }
            }
        });
    }

    private MyDialog dialogDesc;

    /**
     * 退出登录初始化对话框
     *
     * @return
     */
    private void showDescDialog(String desc) {
        final TextView tvDesc = new TextView(this);
        tvDesc.setTextSize(17);
        tvDesc.setGravity(Gravity.CENTER);
        int pd = Util.dip2px(this, 10);
        tvDesc.setPadding(pd, pd, pd, pd);
        tvDesc.setTextColor(getResources().getColor(R.color.text_black));
        tvDesc.setText(desc);
        final String yes = "确定";
        dialogDesc = new MyDialog(this, tvDesc, yes, new MyDialog.DialogListener() {
            @Override
            public void click(String str) {
                dialogDesc.dismiss();
                emailContent.clearFocus();
            }
        });
        dialogDesc.setCancelable(false);
        dialogDesc.setCanceledOnTouchOutside(false);
        dialogDesc.show();
    }

    /**
     * 退出登录初始化对话框
     *
     * @return
     */
    private MyDialog initDialog() {
        initTextDialog();
        tv.setText(getString(R.string.exit_hint));
        final String yes = getString(R.string.exit_yes);
        final String no = getString(R.string.exit_no);
        MyDialog dialog = new MyDialog(this, tv, yes, no, new MyDialog.DialogListener() {
            @Override
            public void click(String str) {
                if (str.equals(yes)) {
                    exitDialog.dismiss();
                    exitLogin();
                    edit_name_text.setText("陌生人");
                    connectionway.setText("");
                    connectionway.setEnabled(true);
                    initTextInfo();
                } else if (str.equals(no)) {
                    exitDialog.dismiss();
                }
            }
        });
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    /**
     * 初始化对话框提示字段
     */
    private void initTextDialog() {
        tv = (TextView) LayoutInflater.from(this).inflate(R.layout.dialog_message, null);
        tv.setGravity(Gravity.CENTER);
    }

    /**
     * 退出登录
     */
    private void exitLogin() {
        btn_tweet_login.setText("登录");
        LoginInformation.getInstance().clearLoginInfo();
    }

    /**
     * 显示退出提示框
     */
    private void showExitDialog() {
        if (exitDialog == null) {
            exitDialog = initDialog();
        }
        exitDialog.show();
    }

    /**
     * 点击登录\退出按钮
     */
    private void clickLogin() {
        if (!LoginInformation.getInstance().hasLogin()) {
            // 未登录状态跳转到登录界面
            gotoLogin();
        } else {
            // 已登录状态弹窗提示退出
            showExitDialog();
        }
    }

    /**
     * 跳转到登录界面
     */
    private void gotoLogin() {
        Intent intent = new Intent(this, ActivityPhotoLogin.class);
        startActivityForResult(intent, 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
//                Bundle b = data.getExtras(); //data为B中回传的Intent
//                String str = b.getString("nick_name");//str即为回传的值
//                String phone = b.getString("phone");
//                if (str == "") {
//                    return;
//                }
//                edit_name_text.setText(str);
//                connectionway.setText(phone);
//                btn_tweet_login.setText("退出");
                setLoginStatus();
                initTextInfo();
                break;
        }
    }

    private class MOnclick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_tweet_login:
                    clickLogin();
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myReceiver);
    }

    private Toast toast;

    private void reqNet(String msg, String contact) {
        // 上传包
        if (msg == null || "".equals(msg)) {
            if (toast == null) {
                toast = Toast.makeText(this, "还没填写内容，请填写内容。", Toast.LENGTH_SHORT);
            } else {
                toast.setText("还没填写内容，请填写内容。");
            }
            toast.show();
            return;
        }
        if(!isOpenNet()){
            showToast(getString(R.string.net_err));
            return ;
        }
        showProgressDialog();
        uppack = new PackMediaTauntedUp();
        uppack.call_way = contact;
        uppack.user_id = LoginInformation.getInstance().getUserId();
        uppack.msg = msg;
        uppack.nick_name = LoginInformation.getInstance().getUsername();

        if (TextUtils.isEmpty(LoginInformation.getInstance().getUserPhone())) {
            uppack.is_bd = "1";
            uppack.mobile = contact;
        }

        uppack.channel_id = channel_id;
        PcsDataDownload.addDownload(uppack);
    }

    /**
     * 请求反馈列表
     **/
    private void request() {
        if(!isOpenNet()){
            showToast(getString(R.string.net_err));
            return ;
        }
        packTauntedListUp = new PackMediaTauntedListUp();
        packTauntedListUp.channel_id = channel_id;
        PcsDataDownload.addDownload(packTauntedListUp);
    }

    private class MyReceiver extends PcsDataBrocastReceiver {
        @Override
        public void onReceive(String nameStr, String errorStr) {
            dismissProgressDialog();
            if (uppack != null && uppack.getName().equals(nameStr)) {
                down = (PackMediaTauntedDown) PcsDataManager.getInstance().getNetPack(nameStr);


                if (down != null && down.result.equals("1")) {
                    Toast.makeText(ActivityMediaTauntedOld.this, "您反馈的意见已收录！感谢您的建议！", Toast.LENGTH_SHORT).show();
                    finish();
                    String strPhone = LoginInformation.getInstance().getUserPhone();
                    if (TextUtils.isEmpty(strPhone)) {
                        //保存手机号码
                        PackPhotoLoginDown login = LoginInformation.getInstance().getLoginInfo();
                        login.mobile = connectionway.getText().toString().trim();
                        LoginInformation.getInstance().savePhotoLoginDown(login);
                    }

                } else {
                    Toast.makeText(ActivityMediaTauntedOld.this, "提交失败咯。麻烦您重新提交。", Toast.LENGTH_SHORT).show();
                }
            }
            if (packTauntedListUp != null && packTauntedListUp.getName().equals(nameStr)) {
                arrsuggestListInfo.clear();
                PackMediaTauntedListDown packSuggestListDown = (PackMediaTauntedListDown) PcsDataManager.getInstance().getNetPack(nameStr);
                if (packSuggestListDown == null || packSuggestListDown.arrsuggestListInfo.size() == 0) {
                    return;
                }
                arrsuggestListInfo.addAll(packSuggestListDown.arrsuggestListInfo);
                adatperFeedBackList.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_feedback_main:
                CommUtils.closeKeyboard(this, v);
                break;
            case R.id.commit_content:
                reqNet(emailContent.getText().toString().trim(), connectionway
                        .getText().toString().trim());
                break;
            default:
                break;
        }
    }
}
