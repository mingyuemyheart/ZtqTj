package com.pcs.ztqtj.view.activity.service;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.CommUtils;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.util.OkHttpUtil;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
import com.pcs.ztqtj.view.activity.help.ActivityHelp;

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
 * 气象服务-登录
 */
public class AcitvityServeLogin extends FragmentActivityZtqBase implements View.OnClickListener {

    private EditText mobileEt, loginPwdEt;
    private ImageView mobileDel;
    private ImageView pwdDel;
    private Button commit;
    private TextView tvHelp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitleText("登录");
        setContentView(R.layout.serveloginacitvity);
        initview();
        initEvent();
    }

    private void initview() {
        mobileEt = (EditText) findViewById(R.id.mobile_et);
        loginPwdEt = (EditText) findViewById(R.id.pwd_et);
        mobileDel = (ImageView) findViewById(R.id.del_mobile_iv);
        pwdDel = (ImageView) findViewById(R.id.del_pwd_iv);
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
        String mobile = mobileEt.getText().toString().trim();
        String pwd = loginPwdEt.getText().toString().trim();

        if (TextUtils.isEmpty(mobile)) {
            showToast("请输入手机号码！");
            return;
        }
        if (TextUtils.isEmpty(pwd)) {
            showToast("请输入密码！");
            return;
        }
        okHttpLogin(mobile, pwd);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
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

    private void finishView() {
        CommUtils.closeKeyboard(this);
        setResult(RESULT_OK);
        finish();
    }

    /**
     * 用户登录
     */
    private void okHttpLogin(final String uName, final String pwd) {
        showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param = new JSONObject();
                    param.put("loginName", uName);
                    param.put("pwd", pwd);
                    String json = param.toString();
                    String url = CONST.BASE_URL+"user/login";
                    Log.e("login", url);
                    final RequestBody body = FormBody.create(MediaType.parse("application/json; charset=utf-8"), json);
                    OkHttpUtil.enqueue(new Request.Builder().post(body).url(url).build(), new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                        }
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (!response.isSuccessful()) {
                                return;
                            }
                            final String result = response.body().string();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dismissProgressDialog();
                                    Log.e("login", result);
                                    if (!TextUtils.isEmpty(result)) {
                                        try {
                                            JSONObject obj = new JSONObject(result);
                                            if (!obj.isNull("token")) {
                                                MyApplication.TOKEN = obj.getString("token");
                                                Log.e("token", MyApplication.TOKEN);
                                            }
                                            if (!obj.isNull("userInfo")) {
                                                JSONObject userInfo = obj.getJSONObject("userInfo");
                                                if (!userInfo.isNull("userId")) {
                                                    MyApplication.UID = userInfo.getString("userId");
                                                }
                                                if (!userInfo.isNull("loginName")) {
                                                    MyApplication.USERNAME = userInfo.getString("loginName");
                                                }
                                                if (!userInfo.isNull("password")) {
                                                    MyApplication.PASSWORD = userInfo.getString("password");
                                                }
                                                if (!userInfo.isNull("userName")) {
                                                    MyApplication.NAME= userInfo.getString("userName");
                                                }
                                                if (!userInfo.isNull("phonenumber")) {
                                                    MyApplication.MOBILE= userInfo.getString("phonenumber");
                                                }
                                                if (!userInfo.isNull("avatar")) {
                                                    MyApplication.PORTRAIT= userInfo.getString("avatar");
                                                }
                                                MyApplication.saveUserInfo(AcitvityServeLogin.this);

                                                //刷新栏目数据
                                                Intent bdIntent = new Intent();
                                                bdIntent.setAction(CONST.BROADCAST_REFRESH_COLUMNN);
                                                sendBroadcast(bdIntent);

                                                finishView();
                                            }
                                            if (!obj.isNull("errorMessage")) {
                                                String errorMessage = obj.getString("errorMessage");
                                                if (errorMessage != null) {
                                                    Toast.makeText(AcitvityServeLogin.this, errorMessage, Toast.LENGTH_SHORT).show();
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
