package com.pcs.ztqtj.view.activity.service;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pcs.lib.lib_pcs_v3.control.file.PcsMD5;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalUser;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalUserInfo;
import com.pcs.lib_ztqfj_v2.model.pack.net.service.PackServiceUserLoginDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.service.PackServiceUserLoginUp;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.CommUtils;
import com.pcs.ztqtj.control.tool.MyConfigure;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
import com.pcs.ztqtj.view.activity.help.ActivityHelp;
import com.pcs.ztqtj.view.dialog.DialogFactory.DialogListener;
import com.pcs.ztqtj.view.dialog.DialogTwoButton;

/**
 * 气象服务 登录
 *
 * @author chenjh
 */
public class AcitvityServeLogin extends FragmentActivityZtqBase implements
        View.OnClickListener {

    private EditText mobileEt, loginPwdEt;
    private ImageView mobileDel;
    private ImageView pwdDel;
    private Button commit;
    private TextView promit;// 提示信息
    private TextView tvHelp;
    private String mobile = "";
    private String pwd = "";
    private ProgressDialog dialog;
    private MyReceiver receiver = new MyReceiver();
    private PackServiceUserLoginUp serviceUserLoginUp = new PackServiceUserLoginUp();
    // private boolean isServiceLogin = false;
    /**
     * 机身编码
     **/
    public String imei = "";
    /**
     * 手机型号
     **/
    public String mobile_type = "";
    private DialogTwoButton myDialog2;
    private TextView messageTextView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitleText("登录");
        setContentView(R.layout.serveloginacitvity);
        initview();
        initEvent();
        PcsDataBrocastReceiver.registerReceiver(this, receiver);
        // isServiceLogin = getIntent().getBooleanExtra("isServiceLogin",
        // false);
    }

    private void initview() {
        mobileEt = (EditText) findViewById(R.id.mobile_et);
        loginPwdEt = (EditText) findViewById(R.id.pwd_et);
        mobileDel = (ImageView) findViewById(R.id.del_mobile_iv);
        pwdDel = (ImageView) findViewById(R.id.del_pwd_iv);
        promit = (TextView) findViewById(R.id.toastinfomation);
        commit = (Button) findViewById(R.id.login_btn);
        tvHelp = (TextView) findViewById(R.id.help);
    }

    private void initEvent() {
        findViewById(R.id.btn_back).setOnClickListener(this);
        mobileDel.setOnClickListener(this);
        pwdDel.setOnClickListener(this);
        commit.setOnClickListener(this);
        tvHelp.setOnClickListener(this);
    }

    private void checkLogin() {
        mobile = mobileEt.getText().toString().trim();
        pwd = loginPwdEt.getText().toString().trim();

        if (TextUtils.isEmpty(mobile)) {
            // Toast.makeText(getApplicationContext(), "请输入手机号码！",
            // Toast.LENGTH_SHORT).show();
            showToast("请输入手机号码！");

            return;
        } else if (TextUtils.isEmpty(pwd)) {
            // Toast.makeText(getApplicationContext(), "请输入密码！ ",
            // Toast.LENGTH_SHORT).show();
            showToast("请输入密码！");
            return;
        } else {
            if (!isOpenNet()) {
                showToast(getString(R.string.net_err));
                return;
            }
            if (serviceUserLoginUp == null) {
                serviceUserLoginUp = new PackServiceUserLoginUp();
            }
            showProgressDialog();
            TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            imei = tm.getDeviceId();
            mobile_type = android.os.Build.MODEL;
            serviceUserLoginUp.mobile = mobile;
            serviceUserLoginUp.pwd = PcsMD5.Md5(pwd);
            serviceUserLoginUp.imei = imei;
            serviceUserLoginUp.mobile_type = mobile_type;
            PcsDataDownload.addDownload(serviceUserLoginUp);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                //finishView();
                finish();
                break;
            case R.id.del_mobile_iv:
                mobileEt.setText("");
                break;
            case R.id.del_pwd_iv:
                loginPwdEt.setText("");
                break;
            case R.id.login_btn:
                checkLogin();
                break;
            case R.id.help:
                Intent intent = new Intent(AcitvityServeLogin.this, ActivityHelp.class);
                startActivity(intent);
                break;
        }
    }

    /**
     * 数据更新广播接收
     *
     * @author JiangZy
     */
    private class MyReceiver extends PcsDataBrocastReceiver {

        @Override
        public void onReceive(String name, String error) {
            if (!TextUtils.isEmpty(error)) {
                return;
            }
            if (serviceUserLoginUp != null && serviceUserLoginUp.getName().equals(name)) {
                dismissProgressDialog();
                PackServiceUserLoginDown packServiceUserLoginDown =
                        (PackServiceUserLoginDown) PcsDataManager.getInstance().getNetPack(name);
                if (packServiceUserLoginDown == null) {
                    showToast("提交失败，请稍后再试。");
                    return;
                }
                int key = -1;
                try {
                    key = Integer.valueOf(packServiceUserLoginDown.type);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                switch (key) {
                    case 0:
                        Toast.makeText(AcitvityServeLogin.this,
                                packServiceUserLoginDown.msg, Toast.LENGTH_SHORT)
                                .show();

                        // 缓存用户信息
                        PackLocalUser myUserInfo = new PackLocalUser();

                        myUserInfo.user_id = packServiceUserLoginDown.user_id;
                        myUserInfo.mobile = mobile;
                        myUserInfo.pwd = pwd;
                        myUserInfo.nick_name = packServiceUserLoginDown.nick_name;
                        myUserInfo.imei = imei;
                        myUserInfo.mobile_type = mobile_type;
                        myUserInfo.type = "4";
                        myUserInfo.sys_head_url = packServiceUserLoginDown.sys_head_url;
                        myUserInfo.sys_nick_name = packServiceUserLoginDown.sys_nick_name;
                        myUserInfo.sys_user_id = packServiceUserLoginDown.sys_user_id;
                        myUserInfo.is_jc = true;

                        PackLocalUserInfo packLocalUserInfo = new PackLocalUserInfo();
                        packLocalUserInfo.currUserInfo = myUserInfo;

                        ZtqCityDB.getInstance().setMyInfo(packLocalUserInfo);

                        // ZtqCityDB.getInstance().setLogin(true);
                        // if(!isServiceLogin){
                        // Intent intent = new Intent(AcitvityServeLogin.this,
                        // UserCenterActivity.class);
                        // startActivity(intent);
                        // }
                        finishView();
                        break;
                    case -1:
                        break;
                    default:
                        showCheckTipsDialog(packServiceUserLoginDown.msg);
                        break;
                }
            }
        }
    }

    private void showCheckTipsDialog(String msg) {
        View view = LayoutInflater.from(AcitvityServeLogin.this).inflate(
                R.layout.dialog_message, null);

        if (myDialog2 == null) {
            messageTextView2 = (TextView) view.findViewById(R.id.dialogmessage);
            messageTextView2.setText(msg);
            messageTextView2.setTextColor(getResources().getColor(
                    R.color.text_color));
            myDialog2 = new DialogTwoButton(AcitvityServeLogin.this, view,
                    "帮助", "返回", new DialogListener() {
                @Override
                public void click(String str) {
                    myDialog2.dismiss();
                    if (str.equals("帮助")) {
                        Intent intent = null;
                        intent = new Intent(AcitvityServeLogin.this,
                                ActivityHelp.class);
                        startActivityForResult(intent,
                                MyConfigure.RESULT_SERVICE_THREE);
                    }
                }
            });
            myDialog2.setTitle("天津气象提示");
            myDialog2.showCloseBtn();
        }

        messageTextView2.setText(msg);
        myDialog2.show();
    }

    private void finishView() {
        CommUtils.closeKeyboard(this);
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onBackPressed() {
        //finishView();
        super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(receiver);
    }
}
