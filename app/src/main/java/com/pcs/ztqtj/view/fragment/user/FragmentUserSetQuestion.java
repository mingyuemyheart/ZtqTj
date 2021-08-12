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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.net.photowall.PackPhotoQueryQuestionDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.photowall.PackPhotoQueryQuestionUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.photowall.PackPhotoSetUserQuestionUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.photowall.PackPhotoUserQuestionDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.photowall.PackPhotoUserQuestionUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.photowall.UserQuestion;
import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.inter.ItemClick;
import com.pcs.ztqtj.control.inter.UserFragmentCallBack;

import java.util.ArrayList;
import java.util.List;

/**
 * 个人信息中设置密码问题
 * Created by tyaathome on 2016/9/9.
 */
public class FragmentUserSetQuestion extends UserFragmentCallBack implements View.OnClickListener {

    private TextView tvQuestion1;
    private TextView tvQuestion2;
    private EditText etAnswer1;
    private EditText etAnswer2;
    private RelativeLayout layoutQuestion1;
    private RelativeLayout layoutQuestion2;

    private String questionId1 = "";
    private String questionId2 = "";

    private MyReceiver receiver = new MyReceiver();

    /**
     * 已设置的密码提示
     */
    private List<UserQuestion> passwordTipsList = new ArrayList<>();

    /**
     * 问题信息列表
     */
    //private List<UserQuestion> questionList = new ArrayList<>();
    private List<UserQuestion> questionList1 = new ArrayList<>();
    private List<UserQuestion> questionList2 = new ArrayList<>();

    private PackPhotoUserQuestionUp packQuestionUp = new PackPhotoUserQuestionUp();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_set_question, null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initEvent();
        initData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(receiver != null) {
            PcsDataBrocastReceiver.unregisterReceiver(getActivity(), receiver);
            receiver = null;
        }
    }

    private void initView() {
        tvQuestion1 = (TextView) getView().findViewById(R.id.popup_question_1);
        tvQuestion2 = (TextView) getView().findViewById(R.id.popup_question_2);
        etAnswer1 = (EditText) getView().findViewById(R.id.et_answer_1);
        etAnswer2 = (EditText) getView().findViewById(R.id.et_answer_2);
        layoutQuestion1 = (RelativeLayout) getView().findViewById(R.id.layout_question_1);
        layoutQuestion2 = (RelativeLayout) getView().findViewById(R.id.layout_question_2);
    }

    private void initEvent() {
//        tvQuestion1.setOnClickListener(this);
//        tvQuestion2.setOnClickListener(this);
        layoutQuestion1.setOnClickListener(this);
        layoutQuestion2.setOnClickListener(this);

        etAnswer1.addTextChangedListener(mTextWatcher);
        etAnswer2.addTextChangedListener(mTextWatcher);
    }

    private void initData() {
        receiver = new MyReceiver();
        PcsDataBrocastReceiver.registerReceiver(getActivity(), receiver);
        reqQuestion();
        reqQueryQuestion();
    }

    @Override
    public void onClickSubmitButton() {
        PackPhotoSetUserQuestionUp packUp = new PackPhotoSetUserQuestionUp();
        packUp.platform_user_id = MyApplication.MOBILE;
        packUp.user_id = MyApplication.UID;
        List<UserQuestion> list = new ArrayList<>();
        UserQuestion question1 = new UserQuestion();
        question1.que_id = questionId1;
        question1.ans_info = ans1;
        list.add(question1);

        UserQuestion question2 = new UserQuestion();
        question2.que_id = questionId2;
        question2.ans_info = ans2;
        list.add(question2);

        packUp.req_list = list;
        PcsDataDownload.addDownload(packUp);
    }

    @Override
    public boolean check() {
        if(TextUtils.isEmpty(ans1) || TextUtils.isEmpty(ans2)) {
            Toast.makeText(getActivity(), "请输入答案", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    /**
     * 获取问题信息
     */
    private void reqQuestion() {
        packQuestionUp = new PackPhotoUserQuestionUp();
        PcsDataDownload.addDownload(packQuestionUp);
    }

    /**
     * 查询密保问题
     */
    private void reqQueryQuestion() {
        PackPhotoQueryQuestionUp packUp = new PackPhotoQueryQuestionUp();
        packUp.platform_user_id = MyApplication.MOBILE;
        PcsDataDownload.addDownload(packUp);
    }

    String ans1,ans2;

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

    private void onUpdate() {
        ans1 = etAnswer1.getText().toString();
        ans2 = etAnswer2.getText().toString();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_question_1:
                showPopupWindow(getActivity(), tvQuestion1, getQuestionList(questionList1), new ItemClick() {
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
                showPopupWindow(getActivity(), tvQuestion2, getQuestionList(questionList2), new ItemClick() {
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
            if(PackPhotoUserQuestionUp.NAME.equals(nameStr)) {
                PackPhotoUserQuestionDown down = (PackPhotoUserQuestionDown) PcsDataManager.getInstance().getNetPack(nameStr);
                if(down == null) {
                    return ;
                }

                if(down.info_list_1 != null && down.info_list_1.size() > 0
                        && down.info_list_2 != null && down.info_list_2.size() > 0) {
                    questionList1 = down.info_list_1;
                    questionList2 = down.info_list_2;
                    // 当设置了当前密码提示时，不做任何操作
                    if(passwordTipsList != null && passwordTipsList.size() >= 2) {
                        return ;
                    }
                    tvQuestion1.setText(questionList1.get(0).que_title);
                    questionId1 = questionList1.get(0).que_id;
                    tvQuestion2.setText(questionList2.get(0).que_title);
                    questionId2 = questionList2.get(0).que_id;
                }
            }

            if(PackPhotoQueryQuestionUp.NAME.equals(nameStr)) {
                PackPhotoQueryQuestionDown down = (PackPhotoQueryQuestionDown) PcsDataManager.getInstance().getNetPack(nameStr);
                if(down == null) {
                    return ;
                }

                if(down.info_list.size() >= 2) {
                    passwordTipsList = down.info_list;

                    UserQuestion bean1 = passwordTipsList.get(0);
                    for(UserQuestion bean : questionList1) {
                        if(bean1.que_id.equals(bean.que_id)) {
                            questionId1 = bean.que_id;
                            tvQuestion1.setText(bean.que_title);
                            etAnswer1.setText(bean1.ans_info);
                            break;
                        }
                    }

                    UserQuestion bean2 = passwordTipsList.get(1);
                    for(UserQuestion bean : questionList2) {
                        if(bean2.que_id.equals(bean.que_id)) {
                            questionId2 = bean.que_id;
                            tvQuestion2.setText(bean.que_title);
                            etAnswer2.setText(bean2.ans_info);
                            break;
                        }
                    }
                }
            }

        }
    }
}
