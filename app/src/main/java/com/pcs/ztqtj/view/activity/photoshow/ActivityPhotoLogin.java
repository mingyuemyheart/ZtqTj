package com.pcs.ztqtj.view.activity.photoshow;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalUser;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalUserInfo;
import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.AppTool;
import com.pcs.ztqtj.control.tool.CommUtils;
import com.pcs.ztqtj.control.tool.MyConfigure;
import com.pcs.ztqtj.control.tool.youmeng.LoginAnther;
import com.pcs.ztqtj.control.tool.youmeng.ToolQQPlatform;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.util.OkHttpUtil;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
import com.pcs.ztqtj.view.activity.pub.ActivityProtocol;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 登录
 */
public class ActivityPhotoLogin extends FragmentActivityZtqBase implements OnClickListener {

    private LoginAnther login;
    private EditText etPhone, etPassword;
    private String phone, password;
    private boolean changePressword = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_login);
        setTitleText(R.string.login);
        initView();
        initData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_main:
                // 点击屏幕隐藏输入法
                CommUtils.closeKeyboard(this, v);
                break;
            case R.id.btn_old_password:
                // 点击一键清除账号
                clearPhone();
                break;
            case R.id.btn_clear_password:
                // 点击一键清除密码
                clearPassword();
                break;
            case R.id.btn_login:
                // 点击登录
                clickLoginZTQ();
                break;
            case R.id.btn_weixin:
                // 点击微信登录
                clickLoginOtherWay(SHARE_MEDIA.WEIXIN);
                break;
            case R.id.btn_qq:
                // 点击QQ登陆
                clickLoginOtherWay(SHARE_MEDIA.QQ);
                break;
            case R.id.btn_sina:
                // 点击新浪微博登陆
                showToast("登录失败，请使用手机号码登录！");
                break;
            case R.id.tv_change_password:
                // 点击修改密码
                clickChangePassword();
                break;
            case R.id.tv_register:
                // 点击注册按钮
                clickRegister();
                break;
            case R.id.tv_find_password:
                clickFindPassword();
                break;
        }
    }

    private void initView() {
        setTitleText(getString(R.string.login));
        LinearLayout layout = (LinearLayout) findViewById(R.id.layout_main);
        layout.setOnClickListener(this);
        etPhone = (EditText) findViewById(R.id.et_phone);
        etPassword = (EditText) findViewById(R.id.et_password);
        ImageButton btn = (ImageButton) findViewById(R.id.btn_old_password);
        btn.setOnClickListener(this);
        btn = (ImageButton) findViewById(R.id.btn_clear_password);
        btn.setOnClickListener(this);
        TextView tv = (TextView) findViewById(R.id.tv_protocol);
        tv.setText(getClickableSpan());
        tv.setMovementMethod(LinkMovementMethod.getInstance());// 设置该句使文本的超连接起作用
        TextView permission = (TextView) findViewById(R.id.tv_permission);
        permission.setText(getClickableSpan2());
        permission.setMovementMethod(LinkMovementMethod.getInstance());
        Button button = (Button) findViewById(R.id.btn_login);
        button.setOnClickListener(this);
        btn = (ImageButton) findViewById(R.id.btn_weixin);
        btn.setOnClickListener(this);
        btn = (ImageButton) findViewById(R.id.btn_qq);
        btn.setOnClickListener(this);
        btn = (ImageButton) findViewById(R.id.btn_sina);
        btn.setOnClickListener(this);
        tv = (TextView) findViewById(R.id.tv_change_password);
        tv.setOnClickListener(this);
        tv = (TextView) findViewById(R.id.tv_register);
        tv.setOnClickListener(this);
        TextView tvFindPassword = (TextView) findViewById(R.id.tv_find_password);
        tvFindPassword.setOnClickListener(this);
    }

    private ToolQQPlatform toolQQLogin;

    /**
     * 初始化数据
     */
    private void initData() {
        toolQQLogin = new ToolQQPlatform(this, qqLoginListener);
        login = new LoginAnther(ActivityPhotoLogin.this);
    }

    /**
     * 获取超链接文本
     * @return
     */
    private SpannableString getClickableSpan() {
        String str = getString(R.string.ztp_protocol);
        SpannableString spanStr = new SpannableString(str);
        // 设置下划线文字
        spanStr.setSpan(new UnderlineSpan(), 0, str.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 设置文字的单击事件
        spanStr.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                startActivity(new Intent(ActivityPhotoLogin.this,
                        ActivityProtocol.class));
            }
        }, 0, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 设置文字的前景色
        spanStr.setSpan(
                new ForegroundColorSpan(getResources().getColor(
                        R.color.text_orange)), 0, str.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanStr;
    }

    private SpannableString getClickableSpan2() {
        String str = "《天津气象软件用户隐私协议》";
        SpannableString spanStr = new SpannableString(str);
        // 设置下划线文字
        spanStr.setSpan(new UnderlineSpan(), 0, str.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 设置文字的单击事件
        spanStr.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(ActivityPhotoLogin.this, ActivityProtocol.class);
                intent.putExtra("url", "http://60.29.105.41:8099/ftp/wap/protocol2.html");
                startActivity(intent);
            }
        }, 0, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 设置文字的前景色
        spanStr.setSpan(
                new ForegroundColorSpan(getResources().getColor(
                        R.color.text_orange)), 0, str.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanStr;
    }

    /**
     * 清除账号
     */
    private void clearPhone() {
        etPhone.setText("");
    }

    /**
     * 清除密码
     */
    private void clearPassword() {
        etPassword.setText("");
    }

    /**
     * 点击登录按钮
     */
    private void clickLoginZTQ() {
        phone = etPhone.getText().toString();
        // 验证是否输入手机号
        if (!checkPhoneInput(phone)) {
            Toast.makeText(this, getString(R.string.error_phone_input),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        // 验证手机号长度
//        if (!checkPhoneLength(phone)) {
//            Toast.makeText(this, getString(R.string.error_phone_length),
//                    Toast.LENGTH_SHORT).show();
//            return;
//        }

        password = etPassword.getText().toString();
        // 验证密码长度
        if (!checkPasswordLength(password)) {
            Toast.makeText(this, getString(R.string.now_password_hint),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        // 验证密码有效性
        if (!checkPasswordValidity(password)) {
            Toast.makeText(this, getString(R.string.error_password_validity),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // 登录
        loginZTQ();
    }

    /**
     * 点击第三方登录按钮
     * @param share
     */
    private void clickLoginOtherWay(SHARE_MEDIA share) {
        currentPathFrom = share;
        if (share == SHARE_MEDIA.WEIXIN) {
            if (AppTool.isInstalled(ActivityPhotoLogin.this, "com.tencent.mm")) {
                login.loginPermission(ActivityPhotoLogin.this, currentPathFrom, permission);
            } else {
                showToast("请先安装微信客户端！");
            }
        } else if (share == SHARE_MEDIA.QQ) {
            toolQQLogin.login();
        } else {
            login.loginPermission(ActivityPhotoLogin.this, currentPathFrom, permission);
        }
    }


    /**
     * 点击修改密码按钮
     */
    private void clickChangePassword() {
        Intent intent = new Intent(this, ActivityPhotoPasswordManager.class);
        // 1:修改密码 2:找回密码
        intent.putExtra("type", 1);
        startActivity(intent);
    }

    private void finishAcitvity() {
        if (changePressword) {
            setResult(RESULT_OK);
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        finishAcitvity();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        toolQQLogin.setActivityResult(requestCode, resultCode, data);
        login.onResult(requestCode, requestCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                case MyConfigure.RESULT_USER_REGISTER:
                    String userName = data.getStringExtra("username");
                    String password = data.getStringExtra("password");
                    loginZTQ(userName, password);
                    break;
            }
        }
    }

    /**
     * 点击注册按钮
     */
    private void clickRegister() {
        Intent intent = new Intent(this, ActivityPhotoRegister.class);
        Bundle bundle = new Bundle();
        bundle.putString("register_type", "0");
        bundle.putString("title", "注册");
        intent.putExtras(bundle);
        startActivityForResult(intent, MyConfigure.RESULT_USER_REGISTER);
    }

    /**
     * 点击找回密码
     */
    private void clickFindPassword() {
        Intent intent = new Intent(this, ActivityPhotoPasswordManager.class);
        // 1:修改密码 2:找回密码
        intent.putExtra("type", 2);
        startActivity(intent);
    }

    /**
     * 验证是否输入手机号
     * @param phone
     * @return
     */
    private boolean checkPhoneInput(String phone) {
        if (TextUtils.isEmpty(phone)) {
            return false;
        }
        return true;
    }

    /**
     * 验证手机号长度
     * @param phone
     * @return
     */
    private boolean checkPhoneLength(String phone) {
        if (TextUtils.isEmpty(phone) || phone.length() != 11) {
            return false;
        }
        return true;
    }

    /**
     * 验证密码长度
     * @param password
     * @return
     */
    private boolean checkPasswordLength(String password) {
        if (TextUtils.isEmpty(password) || password.length() < 6 || password.length() > 16) {
            return false;
        }
        return true;
    }

    /**
     * 验证密码有效性
     * @param password
     * @return
     */
    private boolean checkPasswordValidity(String password) {
        Pattern pattern = Pattern.compile("[0-9a-zA-Z]*");
        return pattern.matcher(password).matches();
    }

    /**
     * 使用知天气账号登录
     */
    private void loginZTQ() {
        if(!isOpenNet()){
            showToast(getString(R.string.net_err));
            return ;
        }
        CommUtils.closeKeyboard(this);
        okHttpLogin(phone, password);
    }

    private void loginZTQ(String username, String password) {
        if(!isOpenNet()){
            showToast(getString(R.string.net_err));
            return ;
        }
        CommUtils.closeKeyboard(this);
        okHttpLogin(username, password);
    }

    private SHARE_MEDIA currentPathFrom;

    private UMAuthListener permission = new UMAuthListener() {
        @Override
        public void onStart(SHARE_MEDIA share_media) {

        }

        @Override
        public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
            Toast.makeText(ActivityPhotoLogin.this, "获取授权完成", Toast.LENGTH_SHORT).show();
            login.getInfo(ActivityPhotoLogin.this, share_media, umListener);
        }

        @Override
        public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
            Toast.makeText(ActivityPhotoLogin.this, "授权错误", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA share_media, int i) {

            Toast.makeText(ActivityPhotoLogin.this, "授权错误取消授权", Toast.LENGTH_SHORT).show();
        }
    };

    private UMAuthListener umListener = new UMAuthListener() {
        @Override
        public void onStart(SHARE_MEDIA share_media) {

        }

        @Override
        public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
            Toast.makeText(ActivityPhotoLogin.this, "获取数据完成", Toast.LENGTH_SHORT).show();
            logInfo(map);
            if (currentPathFrom == SHARE_MEDIA.QQ) {
                getQQInfoSuccess(map);
            } else if (currentPathFrom == SHARE_MEDIA.SINA) {
                getSINAInfoSuccess(map);
            } else if (currentPathFrom == SHARE_MEDIA.WEIXIN) {
                getWeiXinInfoSuccess(map);
            }
        }

        @Override
        public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
            showToast("获取平台数据出错");
        }

        @Override
        public void onCancel(SHARE_MEDIA share_media, int i) {
            showToast("取消获取数据");
        }

        /**
         * 打印返回值
         * @param info
         */
        private void logInfo(Map<String, String> info) {
            StringBuilder sb = new StringBuilder();
            Set<String> keys = info.keySet();
            for (String key : keys) {
                sb.append(key + "=" + info.get(key) + "  ");
            }
            Log.i("z", "获取信息完成：" + sb.toString());
        }
    };


    /**
     * 新浪获取数据成功
     * @param info
     */
    private void getSINAInfoSuccess(Map<String, String> info) {
        // 新浪微博返回数据：
// {"id":2032673205,
// "idstr":"2032673205",
// "class":1,
// "screen_name":"北北博冰",
// "name":"北北博冰",
// "province":"35",
// "city":"1","location":"福建 福州","description":"做个闪闪发光的神经病！",
// "url":"","profile_image_url":"http://tva4.sinaimg.cn/crop.52.82.269.269.50/792821b5gw1edboq3cgoij20c809s0td.jpg",
// "cover_image_phone":"http://ww2.sinaimg.cn/crop.0.0.640.640/7c85468fjw1e8yqb21wg3j20hs0hsmzk.jpg",
        try {
            String str = info.get("result");
            JSONObject jsonObject = new JSONObject(str);
            loginWeServer(jsonObject.optString("idstr"), jsonObject.optString("screen_name"), jsonObject.optString("profile_image_url"), "1");
        } catch (JSONException e) {
            e.printStackTrace();
            showToast("数据错误，请用手机号登录");
        }
    }

    /**
     * 腾讯获取数据成功
     * @param info
     */
    private void getQQInfoSuccess(Map<String, String> info) {
        // qq平台获取到的数据：
//        is_yellow_vip=0
//        yellow_vip_level=0
//        profile_image_url=http://q.qlogo.cn/qqapp/100424468/A68741CB37224F654D921F7FBA5B1D12/100
//        screen_name=记着
//        msg=
//                vip=0
//        city=
//                gender=男
//        province=
//                level=0
//        is_yellow_year_vip=0
//        openid=A68741CB37224F654D921F7FBA5B1D12
//        StringBuilder sb = new StringBuilder();
//        Set<String> keys = info.keySet();
//        for (String key : keys) {
//            sb.append(key + "=" + info.get(key) + "\r\n");
//        }
        try{
            loginWeServer(info.get("openid"), info.get("screen_name"), info.get("profile_image_url"), "2");
        }catch (Exception e){
            showToast("数据错误，请用手机号登录");
        }

    }

    /**
     * 获取微信信息成功
     */
    private void getWeiXinInfoSuccess(Map<String, String> info) {
        try{
            loginWeServer(info.get("unionid"), info.get("name"), info.get("iconurl"), "3");
//            loginWeServer(info.get("unionid"), info.get("nickname"), info.get("headimgurl"), "3");
        }catch (Exception e){
            showToast("数据错误，请用手机号登录");
        }
    }

    /**
     * 向我们的服务器提交数据 platForm: 1为新浪，2为qq，3为微信
     */
    private void loginWeServer(String userId, String userName, String headUrl, String platForm) {
        Log.e("z", userId + "---" + userName + "---" + headUrl + "---" + platForm);
        if(!isOpenNet()){
            showToast(getString(R.string.net_err));
            return ;
        }
        showProgressDialog();
        okHttpLogin(userName, userId);
    }

    private IUiListener qqLoginListener = new IUiListener() {
        @Override
        public void onError(UiError e) {
            showToast("获取信息失败");
            dismissProgressDialog();
        }
        @Override
        public void onComplete(final Object response) {
            dismissProgressDialog();
//                    showToast("获取消息完成");
            JSONObject json = (JSONObject) response;
            loginWeServer(toolQQLogin.getOpenId(), json.optString("nickname"), json.optString("figureurl_qq_2"), "2");
//                    loginWeServer(json.optString("openid"), json.optString("nickname"), json.optString("figureurl_qq_2"), "2");
        }

        @Override
        public void onCancel() {
            showToast("取消获取信息");
            dismissProgressDialog();
        }
    };

    /**
     * 用户登录
     */
    private void okHttpLogin(final String uName, final String pwd) {
        showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = CONST.BASE_URL+"user/login";
                Log.e("login", url);
                JSONObject param = new JSONObject();
                try {
                    param.put("loginName", uName);
                    param.put("pwd", pwd);
                    String json = param.toString();
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
                                            if (!obj.isNull("errorMessage")) {
                                                String errorMessage = obj.getString("errorMessage");
                                                showToast(errorMessage);
                                            }
                                            if (!obj.isNull("token")) {
                                                MyApplication.TOKEN = obj.getString("token");
                                                Log.e("token", MyApplication.TOKEN);
                                            }
                                            if (!obj.isNull("limitInfo")) {
                                                MyApplication.LIMITINFO = obj.getString("limitInfo");
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
                                                MyApplication.saveUserInfo(ActivityPhotoLogin.this);

                                                //存储用户数据
                                                PackLocalUser myUserInfo = new PackLocalUser();
                                                myUserInfo.user_id = MyApplication.UID;
                                                myUserInfo.sys_user_id = MyApplication.UID;
                                                myUserInfo.sys_nick_name = MyApplication.NAME;
                                                myUserInfo.sys_head_url = MyApplication.PORTRAIT;
                                                myUserInfo.mobile = MyApplication.MOBILE;
                                                PackLocalUserInfo packLocalUserInfo = new PackLocalUserInfo();
                                                packLocalUserInfo.currUserInfo = myUserInfo;
                                                ZtqCityDB.getInstance().setMyInfo(packLocalUserInfo);

                                                Toast.makeText(ActivityPhotoLogin.this, getString(R.string.login_succ), Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent();
                                                Bundle bundle = new Bundle();
                                                bundle.putString("nick_name", MyApplication.NAME);
                                                bundle.putString("head_url", MyApplication.PORTRAIT);
                                                bundle.putString("phone", MyApplication.MOBILE);
                                                bundle.putString("user_id", MyApplication.UID);
                                                intent.putExtras(bundle);
                                                setResult(RESULT_OK, intent);
                                                finish();
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
