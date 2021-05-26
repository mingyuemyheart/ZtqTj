package com.pcs.ztqtj.view.activity.photoshow;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pcs.lib.lib_pcs_v3.control.file.PcsMD5;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalUser;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalUserInfo;
import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.CommUtils;
import com.pcs.ztqtj.control.tool.MyConfigure;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.util.OkHttpUtil;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
import com.pcs.ztqtj.view.activity.pub.ActivityProtocol;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 实景注册
 *
 * @author JiangZy
 */
public class ActivityPhotoRegister extends FragmentActivityZtqBase implements OnClickListener {
    /**
     * 输入法驱动
     */
    private InputMethodManager mIMM;

    /**
     * 编辑框
     */
    private EditText etName, etPhone, etPassword, etRepassword;

    /**
     * 输入文本
     */
    private String nickname, mobile, password, repassword;

    private String type;

    private TextView tvExplain;

    private TextView tvProtocol;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_register);
        bundle = getIntent().getExtras();
        setTitleText(bundle.getString("title"));
        type = bundle.getString("register_type");//获取新老用户标识
        instantiateObject();
        // 初始化按钮
        initView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case MyConfigure.RESULT_USER_REGISTER:
                    setResult(RESULT_OK, data);
                    finish();
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_main:
                // 点击屏幕隐藏输入法
                CommUtils.closeKeyboard(this, v);
                break;
            case R.id.btn_clear_name:
                // 清除昵称
                clearName();
                break;
            case R.id.btn_old_password:
                // 清除手机号
                clearPhone();
                break;
            case R.id.btn_clear_password:
                // 清除密码
                clearPassword();
                break;
            case R.id.btn_clear_repassword:
                // 清除重复密码
                clearPasswordAgain();
                break;
            case R.id.btn_login:
                // 点击提交按钮
                clickSubmit();
                break;
        }
    }

    /**
     * 实例化对象
     */
    private void instantiateObject() {
        mIMM = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    /**
     * 初始化页面
     */
    private void initView() {
        LinearLayout layout = (LinearLayout) findViewById(R.id.layout_main);
        layout.setOnClickListener(this);
        etName = (EditText) findViewById(R.id.et_name);
        etPhone = (EditText) findViewById(R.id.et_phone);
        etPassword = (EditText) findViewById(R.id.et_password);
        etRepassword = (EditText) findViewById(R.id.et_repassword);
        TextView tv = (TextView) findViewById(R.id.tv_protocol);
        tv.setText(getClickableSpan());
        tv.setMovementMethod(LinkMovementMethod.getInstance());// 设置该句使文本的超连接起作用
        ImageButton btn = (ImageButton) findViewById(R.id.btn_clear_name);
        btn.setOnClickListener(this);
        btn = (ImageButton) findViewById(R.id.btn_old_password);
        btn.setOnClickListener(this);
        btn = (ImageButton) findViewById(R.id.btn_clear_password);
        btn.setOnClickListener(this);
        btn = (ImageButton) findViewById(R.id.btn_clear_repassword);
        btn.setOnClickListener(this);
        Button button = (Button) findViewById(R.id.btn_login);
        button.setOnClickListener(this);
        tvExplain = (TextView) findViewById(R.id.tv_explain);
        tvProtocol = (TextView) findViewById(R.id.tv_protocol);
        if (type.equals("1")) {
            tvExplain.setVisibility(View.GONE);
            tvProtocol.setVisibility(View.GONE);
            etName.setHint("请输入已注册的昵称");
        } else {
            tvExplain.setVisibility(View.VISIBLE);
            tvProtocol.setVisibility(View.VISIBLE);
            etName.setHint(R.string.name_hint);
            etName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
        }
    }

    /**
     * 清除昵称
     */
    private void clearName() {
        etName.setText("");
    }

    /**
     * 清除手机号
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
     * 清除重复密码
     */
    private void clearPasswordAgain() {
        etRepassword.setText("");
    }

    /**
     * 跳转问题页面
     */
    private void gotoNext() {
        Intent intent = new Intent(this, ActivityPhotoRegisterQuestion.class);
        startActivityForResult(intent, MyConfigure.RESULT_USER_REGISTER);
        //startActivity(intent);
    }

    /**
     * 点击提交按钮
     */
    private void clickSubmit() {
        nickname = etName.getText().toString();
        // 校验昵称长度
        if (!checkNameLength(nickname)) {
            Toast.makeText(this, getString(R.string.error_name_length), Toast.LENGTH_SHORT).show();
            return;
        }
        // 校验昵称有效性
//		if(!checkNameValidity(nickname)) {
//			Toast.makeText(this, getString(R.string.error_name_validity), Toast.LENGTH_SHORT).show();
//			return;
//		}
        mobile = etPhone.getText().toString();
        // 验证是否输入手机号
        if (!checkPhoneInput(mobile)) {
            Toast.makeText(this, getString(R.string.error_phone_input), Toast.LENGTH_SHORT).show();
            return;
        }
        // 验证手机号长度
//        if (!checkPhoneLength(platform_user_id)) {
//            Toast.makeText(this, getString(R.string.error_phone_length), Toast.LENGTH_SHORT).show();
//            return;
//        }
        password = etPassword.getText().toString();
        // 校验密码长度
        if (!checkPasswordLength(password)) {
            Toast.makeText(this, getString(R.string.error_password_length), Toast.LENGTH_SHORT).show();
            return;
        }
        // 校验密码有效性
        if (!checkPasswordValidity(password)) {
            Toast.makeText(this, getString(R.string.error_password_validity), Toast.LENGTH_SHORT).show();
            return;
        }
        repassword = etRepassword.getText().toString();
        if (!checkRepasswordLength(repassword)) {
            Toast.makeText(this, getString(R.string.error_repassword_length), Toast.LENGTH_SHORT).show();
            return;
        }
        // 校验重复密码有效性
        if (!checkRepasswordValidity(password, repassword)) {
            Toast.makeText(this, getString(R.string.error_repassword_validity), Toast.LENGTH_SHORT).show();
            return;
        }

        // 提交注册信息
        submit();
    }

    /**
     * 校验昵称长度
     *
     * @param name
     * @return
     */
    private boolean checkNameLength(String name) {
        if (TextUtils.isEmpty(name)) {
            return false;
        }
        return true;
    }

    /**
     * 校验昵称有效性
     *
     * @param name
     * @return
     */
    private boolean checkNameValidity(String name) {
        Pattern pattern = Pattern.compile("[0-9a-zA-Z\u4e00-\u9fa5]*");
        return pattern.matcher(name).matches();
    }

    /**
     * 验证是否输入手机号
     *
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
     *
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
     * 校验密码长度
     *
     * @param password
     * @return
     */
    private boolean checkPasswordLength(String password) {
        if (TextUtils.isEmpty(password) || password.length() < 6 ||
                password.length() > 16) {
            return false;
        }
        return true;
    }

    /**
     * 校验密码有效性
     *
     * @param password
     * @return
     */
    private boolean checkPasswordValidity(String password) {
        Pattern pattern = Pattern.compile("[0-9a-zA-Z]*");
        return pattern.matcher(password).matches();
    }

    /**
     * 校验重复密码长度
     *
     * @param repassword
     * @return
     */
    private boolean checkRepasswordLength(String repassword) {
        if (TextUtils.isEmpty(repassword)) {
            return false;
        }
        return true;
    }

    /**
     * 校验重复密码有效性
     *
     * @param password
     * @param repassword
     * @return
     */
    private boolean checkRepasswordValidity(String password, String repassword) {
        if (!password.equals(repassword)) {
            return false;
        }
        return true;
    }

    /**
     * 提交注册
     */
    private void submit() {
        CommUtils.closeKeyboard(this);
        if (!isOpenNet()) {
            showToast(getString(R.string.net_err));
            return;
        }

        MyApplication.USERNAME = mobile;
        MyApplication.PASSWORD = password;
        MyApplication.NAME= nickname;
        MyApplication.MOBILE= mobile;
        MyApplication.saveUserInfo(ActivityPhotoRegister.this);

        Intent intent = new Intent(ActivityPhotoRegister.this, ActivityPhotoRegisterQuestion.class);
        startActivityForResult(intent, MyConfigure.RESULT_USER_REGISTER);
    }

    /**
     * 获取超链接文本
     *
     * @return
     */
    private SpannableString getClickableSpan() {
        String str = getString(R.string.ztp_protocol);
        SpannableString spanStr = new SpannableString(str);
        //设置下划线文字
        spanStr.setSpan(new UnderlineSpan(), 0, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //设置文字的单击事件
        spanStr.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                startActivity(new Intent(ActivityPhotoRegister.this, ActivityProtocol.class));
            }
        }, 0, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //设置文字的前景色
        spanStr.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.text_orange)), 0, str.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanStr;
    }

}