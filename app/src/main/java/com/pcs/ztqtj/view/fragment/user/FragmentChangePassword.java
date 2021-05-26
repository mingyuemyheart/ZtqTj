package com.pcs.ztqtj.view.fragment.user;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.inter.UserFragmentCallBack;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.util.OkHttpUtil;

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
 * 设置密码
 * Created by tyaathome on 2016/9/8.
 */
public class FragmentChangePassword extends UserFragmentCallBack implements View.OnClickListener{

    private EditText etUserName;
    private EditText etOldPassword;
    private EditText etNewPassword;
    private EditText etConfirmPassword;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState) {
        return inflater.inflate(R.layout.fragment_set_new_password, null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initEvent();
        initData();
    }

    @Override
    public void onResume() {
        super.onResume();
        etUserName.setText("");
        etOldPassword.setText("");
        etNewPassword.setText("");
        etConfirmPassword.setText("");
        initData();
    }

    private void initView() {
        etUserName = (EditText) getView().findViewById(R.id.et_name);
        etOldPassword = (EditText) getView().findViewById(R.id.et_oldpassword);
        etNewPassword = (EditText) getView().findViewById(R.id.et_password);
        etConfirmPassword = (EditText) getView().findViewById(R.id.et_repassword);
    }

    private void initEvent() {
        etUserName.addTextChangedListener(mTextWatcher);
        etOldPassword.addTextChangedListener(mTextWatcher);
        etNewPassword.addTextChangedListener(mTextWatcher);
        etConfirmPassword.addTextChangedListener(mTextWatcher);
        getView().findViewById(R.id.btn_clear_name).setOnClickListener(this);
        getView().findViewById(R.id.btn_old_password).setOnClickListener(this);
        getView().findViewById(R.id.btn_clear_password).setOnClickListener(this);
        getView().findViewById(R.id.btn_clear_repassword).setOnClickListener(this);
    }

    private void initData() {
        Bundle bundle = getArguments();
        if(bundle != null) {
            String user = bundle.getString("username");
            String password = bundle.getString("password");
            etUserName.setText(user);
            etOldPassword.setText(password);
        } else {
            etUserName.setText("");
            etOldPassword.setText("");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClickSubmitButton() {
        okHttpModifyPwd(userName, oldPassword, newPassword);
    }

    @Override
    public boolean check() {
        if(TextUtils.isEmpty(userName)) {
            Toast.makeText(getActivity(), getString(R.string.phone_hint), Toast.LENGTH_LONG).show();
            return false;
        }
        if(oldPassword.length() < 6 || oldPassword.length() > 16) {
            Toast.makeText(getActivity(), getString(R.string.now_password_hint), Toast.LENGTH_LONG).show();
            return false;
        }
        if (TextUtils.isEmpty(newPassword) || newPassword.length() < 6 || newPassword.length() > 16) {
            Toast.makeText(getActivity(), getString(R.string.error_new_password_length), Toast.LENGTH_LONG).show();
            return false;
        }
        if (TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(getActivity(), getString(R.string.error_repassword_length), Toast.LENGTH_LONG).show();
            return false;
        }
        if(!newPassword.equals(confirmPassword)) {
            Toast.makeText(getActivity(), "新密码与确认密码输入不一致，请重新输入", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            onUpdate();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    String userName, oldPassword, newPassword, confirmPassword;
    private void onUpdate() {
        userName = etUserName.getText().toString();
        oldPassword = etOldPassword.getText().toString();
        newPassword = etNewPassword.getText().toString();
        confirmPassword = etConfirmPassword.getText().toString();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_clear_name:
                etUserName.setText("");
                break;
            case R.id.btn_old_password:
                etOldPassword.setText("");
                break;
            case R.id.btn_clear_password:
                etNewPassword.setText("");
                break;
            case R.id.btn_clear_repassword:
                etConfirmPassword.setText("");
                break;
        }
    }

    /**
     * 修改密码
     */
    private void okHttpModifyPwd(final String uName, final String pwd, final String newPassword) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = CONST.BASE_URL+"user/changepwd";
                JSONObject param = new JSONObject();
                try {
                    param.put("loginName", uName);
                    param.put("pwd", pwd);
                    param.put("newpwd", newPassword);
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
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (!TextUtils.isEmpty(result)) {
                                        try {
                                            JSONObject obj = new JSONObject(result);
                                            if (!obj.isNull("errorMessage")) {
                                                String errorMessage = obj.getString("errorMessage");
                                                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
                                            }
                                            if (!obj.isNull("result")) {
                                                boolean result = obj.getBoolean("result");
                                                if (result) {
                                                    getActivity().finish();
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
