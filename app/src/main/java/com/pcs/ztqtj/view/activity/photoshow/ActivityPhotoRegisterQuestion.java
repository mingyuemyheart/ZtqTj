package com.pcs.ztqtj.view.activity.photoshow;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pcs.lib_ztqfj_v2.model.pack.net.photowall.UserQuestion;
import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.inter.ItemClick;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.util.OkHttpUtil;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;

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
 * 注册问题
 * Created by tyaathome on 2016/9/7.
 */
public class ActivityPhotoRegisterQuestion extends FragmentActivityZtqBase implements View.OnClickListener{

    private Button btnSubmit;

    private TextView tvQuestion1;
    private TextView tvQuestion2;
    private EditText etAnswer1;
    private EditText etAnswer2;
    private RelativeLayout layoutQuestion1;
    private RelativeLayout layoutQuestion2;

    private String questionId1 = "";
    private String questionId2 = "";

    /**
     * 问题信息列表
     */
    //private List<UserQuestion> questionList = new ArrayList<>();
    private List<UserQuestion> questionList1 = new ArrayList<>();
    private List<UserQuestion> questionList2 = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_register_question);
        setTitleText("注册");
        initView();
        initEvent();
        initData();
    }

    private void initView() {
        btnSubmit = (Button) findViewById(R.id.btn_submit);
        tvQuestion1 = (TextView) findViewById(R.id.popup_question_1);
        tvQuestion2 = (TextView) findViewById(R.id.popup_question_2);
        etAnswer1 = (EditText) findViewById(R.id.et_answer_1);
        etAnswer2 = (EditText) findViewById(R.id.et_answer_2);
        layoutQuestion1 = (RelativeLayout) findViewById(R.id.layout_question_1);
        layoutQuestion2 = (RelativeLayout) findViewById(R.id.layout_question_2);
    }

    private void initEvent() {
        btnSubmit.setOnClickListener(this);
//        tvQuestion1.setOnClickListener(this);
//        tvQuestion2.setOnClickListener(this);
        layoutQuestion1.setOnClickListener(this);
        layoutQuestion2.setOnClickListener(this);

//        setBackListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                checkLogin();
//            }
//        });
    }

    private void initData() {
        reqQuestion();
    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra("username", MyApplication.USERNAME);
        intent.putExtra("password", MyApplication.PASSWORD);
        ActivityPhotoRegisterQuestion.this.setResult(RESULT_OK, intent);
        super.finish();
    }

    private boolean check() {
        String str1 = etAnswer1.getText().toString();
        String str2 = etAnswer2.getText().toString();
        if(TextUtils.isEmpty(str1)) {
            showToast(getString(R.string.hint_question_1_tip));
            return false;
        }
        if(TextUtils.isEmpty(str2)) {
            showToast(getString(R.string.hint_question_2_tip));
            return false;
        }
        return true;
    }

    /**
     * 获取问题字符串
     * @param list
     */
    private List<String> getQuestionList(List<UserQuestion> list) {
        List<String> stringList = new ArrayList<>();
        if(list == null) {
            return stringList;
        }
        for(UserQuestion bean : list) {
            stringList.add(bean.que_title);
        }
        return stringList;
    }

    /**
     * 获取问题信息
     */
    private void reqQuestion() {
        questionList1.clear();
        UserQuestion dto = new UserQuestion();
        dto.que_title = "你最喜欢的颜色是什么？";
        dto.que_id = "1";
        questionList1.add(dto);

        questionList2.clear();
        dto = new UserQuestion();
        dto.que_title = "你最喜欢的水果是什么？";
        dto.que_id = "2";
        questionList2.add(dto);
    }

    /**
     * 完成注册
     */
    private void gotoLogin() {
//        Intent intent = new Intent();
//        intent.putExtra("username", platform_user_id);
//        intent.putExtra("password", password);
//        ActivityPhotoRegisterQuestion.this.setResult(RESULT_OK, intent);
        ActivityPhotoRegisterQuestion.this.finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:
                String str1 = etAnswer1.getText().toString();
                String str2 = etAnswer2.getText().toString();
                if(TextUtils.isEmpty(str1) && TextUtils.isEmpty(str2)) {
                    gotoLogin();
                } else {
                    if(check()) {
                        okHttpRegister();
                    }
                }

                break;
            case R.id.layout_question_1:
                showPopupWindow(ActivityPhotoRegisterQuestion.this, tvQuestion1, getQuestionList(questionList1), 5, new ItemClick() {
                    @Override
                    public void itemClick(int position, String... str) {
                        if(str.length > 0) {
                            tvQuestion1.setText(str[0]);
                        }
                        questionId1 = questionList1.get(position).que_id;
                    }
                });
                break;
            case R.id.layout_question_2:
                showPopupWindow(ActivityPhotoRegisterQuestion.this, tvQuestion2, getQuestionList(questionList2), 5, new ItemClick() {
                    @Override
                    public void itemClick(int position, String... str) {
                        if(str.length > 0) {
                            tvQuestion2.setText(str[0]);
                        }
                        questionId2 = questionList2.get(position).que_id;
                    }
                });
                break;
        }
    }

    /**
     * 用户注册
     */
    private void okHttpRegister() {
        showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = CONST.BASE_URL+"user/register";
                JSONObject param = new JSONObject();
                try {
                    param.put("loginName", MyApplication.USERNAME);
                    param.put("pwd", MyApplication.PASSWORD);
                    param.put("userName", MyApplication.NAME);
                    param.put("qesTypeOne", questionId1);
                    param.put("qesTypeOneAns", etAnswer1.getText().toString());
                    param.put("qesTypeTwo", questionId2);
                    param.put("qesTypeTwoAns", etAnswer2.getText().toString());
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
                                    if (!TextUtils.isEmpty(result)) {
                                        try {
                                            JSONObject obj = new JSONObject(result);
                                            if (!obj.isNull("errorMessage")) {
                                                String errorMessage = obj.getString("errorMessage");
                                                showToast(errorMessage);
                                            }
                                            if (!obj.isNull("token")) {
                                                MyApplication.TOKEN = obj.getString("token");
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
                                                MyApplication.saveUserInfo(ActivityPhotoRegisterQuestion.this);

                                                gotoLogin();
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
