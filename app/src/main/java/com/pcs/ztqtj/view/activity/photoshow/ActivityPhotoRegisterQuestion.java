package com.pcs.ztqtj.view.activity.photoshow;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.inter.ItemClick;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.net.photowall.PackPhotoSetUserQuestionDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.photowall.PackPhotoSetUserQuestionUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.photowall.PackPhotoUserQuestionDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.photowall.PackPhotoUserQuestionUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.photowall.UserQuestion;

import java.util.ArrayList;
import java.util.List;

/**
 * 注册问题
 * Created by tyaathome on 2016/9/7.
 */
public class ActivityPhotoRegisterQuestion extends FragmentActivityZtqBase implements View.OnClickListener{

    private Button btnSubmit;
    private MyReceiver receiver = new MyReceiver();

    private PackPhotoUserQuestionUp packQuestionUp = new PackPhotoUserQuestionUp();
    private PackPhotoUserQuestionDown packQuestionDown = new PackPhotoUserQuestionDown();

    private TextView tvQuestion1;
    private TextView tvQuestion2;
    private EditText etAnswer1;
    private EditText etAnswer2;
    private RelativeLayout layoutQuestion1;
    private RelativeLayout layoutQuestion2;

    private String questionId1 = "";
    private String questionId2 = "";

    // 用户id
    private String user_id = "";
    // 平台id
    private String platform_user_id = "";
    private String password = "";

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
        PcsDataBrocastReceiver.registerReceiver(this, receiver);
        reqQuestion();
        user_id = getIntent().getStringExtra("user_id");
        platform_user_id = getIntent().getStringExtra("platform_user_id");
        password = getIntent().getStringExtra("password");
    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra("username", platform_user_id);
        intent.putExtra("password", password);
        ActivityPhotoRegisterQuestion.this.setResult(RESULT_OK, intent);
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(receiver != null) {
            PcsDataBrocastReceiver.unregisterReceiver(this, receiver);
            receiver = null;
        }
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
        if(!isOpenNet()){
            showToast(getString(R.string.net_err));
            return ;
        }
        packQuestionUp = new PackPhotoUserQuestionUp();
        PcsDataDownload.addDownload(packQuestionUp);
    }

    /**
     * 注册设置问题列表
     */
    private void reqRegister() {
        PackPhotoSetUserQuestionUp packUp = new PackPhotoSetUserQuestionUp();
        packUp.user_id = user_id;
        packUp.platform_user_id = platform_user_id;

        List<UserQuestion> list = new ArrayList<>();
        UserQuestion q1 = new UserQuestion();
        q1.que_id = questionId1;
        q1.ans_info = etAnswer1.getText().toString();

        UserQuestion q2 = new UserQuestion();
        q2.que_id = questionId2;
        q2.ans_info = etAnswer2.getText().toString();

        list.add(q1);
        list.add(q2);
        packUp.req_list = list;
        if(!isOpenNet()){
            showToast(getString(R.string.net_err));
            return ;
        }
        PcsDataDownload.addDownload(packUp);
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
                        reqRegister();
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

    private class MyReceiver extends PcsDataBrocastReceiver {

        @Override
        public void onReceive(String nameStr, String errorStr) {
            if(packQuestionUp.getName().equals(nameStr)) {
                packQuestionDown = (PackPhotoUserQuestionDown) PcsDataManager.getInstance().getNetPack(nameStr);
                if(packQuestionDown == null) {
                    return ;
                }
                if(packQuestionDown.info_list_1 != null && packQuestionDown.info_list_1.size() > 0
                        && packQuestionDown.info_list_2 != null && packQuestionDown.info_list_2.size() > 0) {
                    questionList1 = packQuestionDown.info_list_1;
                    questionList2 = packQuestionDown.info_list_2;
                    tvQuestion1.setText(questionList1.get(0).que_title);
                    questionId1 = questionList1.get(0).que_id;
                    tvQuestion2.setText(questionList2.get(0).que_title);
                    questionId2 = questionList2.get(0).que_id;
                }
            }

            if(PackPhotoSetUserQuestionUp.NAME.equals(nameStr)) {
                dismissProgressDialog();

                PackPhotoSetUserQuestionDown packDown = (PackPhotoSetUserQuestionDown) PcsDataManager.getInstance().getNetPack(nameStr);
                if (packDown == null) {
                    Toast.makeText(ActivityPhotoRegisterQuestion.this, "注册失败！", Toast.LENGTH_SHORT).show();
                    return;
                }
                showToast(packDown.result_msg);
                if(packDown.result.equals("1")) {
                    gotoLogin();
                }

            }
        }
    }
}
