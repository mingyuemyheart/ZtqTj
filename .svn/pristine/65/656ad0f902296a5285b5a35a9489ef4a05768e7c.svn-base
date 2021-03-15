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

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.inter.ItemClick;
import com.pcs.ztqtj.control.inter.UserFragmentCallBack;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.net.photowall.PackPhotoFindPasswordUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.photowall.PackPhotoUserQuestionDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.photowall.PackPhotoUserQuestionUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.photowall.UserQuestion;

import java.util.ArrayList;
import java.util.List;

/**
 * 找回密码
 * Created by tyaathome on 2016/9/8.
 */
public class FragmentFindPassword extends UserFragmentCallBack implements View.OnClickListener {

    private EditText etUserName;
    private TextView tvQuestion1;
    private TextView tvQuestion2;
    private EditText etAnswer1;
    private EditText etAnswer2;
    private RelativeLayout layoutQuestion1;
    private RelativeLayout layoutQuestion2;


    private String questionId1 = "";
    private String questionId2 = "";

    private MyReceiver receiver = new MyReceiver();

    private PackPhotoUserQuestionUp packQuestionUp = new PackPhotoUserQuestionUp();

    /**
     * 问题信息列表
     */
    //private List<UserQuestion> questionList = new ArrayList<>();
    private List<UserQuestion> questionList1 = new ArrayList<>();
    private List<UserQuestion> questionList2 = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState) {
        return inflater.inflate(R.layout.fragment_find_password, null);
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
        etAnswer1.setText("");
        etAnswer2.setText("");
        etUserName.setText("");
    }

    private void initView() {
        tvQuestion1 = (TextView) getView().findViewById(R.id.popup_question_1);
        tvQuestion2 = (TextView) getView().findViewById(R.id.popup_question_2);
        etAnswer1 = (EditText) getView().findViewById(R.id.et_answer_1);
        etAnswer2 = (EditText) getView().findViewById(R.id.et_answer_2);
        etUserName = (EditText) getView().findViewById(R.id.et_username);
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
        etUserName.addTextChangedListener(mTextWatcher);

    }

    private void initData() {
        receiver = new MyReceiver();
        PcsDataBrocastReceiver.registerReceiver(getActivity(), receiver);

        reqQuestion();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(receiver != null) {
            PcsDataBrocastReceiver.unregisterReceiver(getActivity(), receiver);
            receiver = null;
        }
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
        packQuestionUp = new PackPhotoUserQuestionUp();
        PcsDataDownload.addDownload(packQuestionUp);
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

    @Override
    public void onClickSubmitButton() {
        PackPhotoFindPasswordUp packUp = new PackPhotoFindPasswordUp();
        packUp.mobile = mobile;
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
        if(TextUtils.isEmpty(mobile)) {
            Toast.makeText(getActivity(), getString(R.string.phone_hint), Toast.LENGTH_LONG).show();
            return false;
        }
        if(TextUtils.isEmpty(ans1)) {
            Toast.makeText(getActivity(), getString(R.string.hint_question_1_tip), Toast.LENGTH_LONG).show();
            return false;
        }
        if(TextUtils.isEmpty(ans2)) {
            Toast.makeText(getActivity(), getString(R.string.hint_question_2_tip), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    String mobile,ans1,ans2;

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
        mobile = etUserName.getText().toString();
        ans1 = etAnswer1.getText().toString();
        ans2 = etAnswer2.getText().toString();
    }

    private class MyReceiver extends PcsDataBrocastReceiver {

        @Override
        public void onReceive(String nameStr, String errorStr) {
            if(packQuestionUp.getName().equals(nameStr)) {
                PackPhotoUserQuestionDown down = (PackPhotoUserQuestionDown) PcsDataManager.getInstance().getNetPack(nameStr);
                if(down == null) {
                    return ;
                }
                if(down.info_list_1 != null && down.info_list_1.size() > 0
                        && down.info_list_2 != null && down.info_list_2.size() > 0) {
                    questionList1 = down.info_list_1;
                    questionList2 = down.info_list_2;
                    tvQuestion1.setText(questionList1.get(0).que_title);
                    questionId1 = questionList1.get(0).que_id;
                    tvQuestion2.setText(questionList2.get(0).que_title);
                    questionId2 = questionList2.get(0).que_id;
                }
            }
        }
    }
}
