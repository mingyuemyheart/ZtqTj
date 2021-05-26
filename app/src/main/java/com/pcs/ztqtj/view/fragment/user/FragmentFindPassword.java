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

import com.pcs.lib_ztqfj_v2.model.pack.net.photowall.UserQuestion;
import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.inter.ItemClick;
import com.pcs.ztqtj.control.inter.UserFragmentCallBack;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.util.OkHttpUtil;
import com.pcs.ztqtj.view.dialog.DialogFactory;
import com.pcs.ztqtj.view.dialog.DialogOneButton;

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

    /**
     * 问题信息列表
     */
    private List<UserQuestion> questionList1 = new ArrayList<>();
    private List<UserQuestion> questionList2 = new ArrayList<>();

    private DialogOneButton dialog;

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
        reqQuestion();
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
        okHttpFindPwd();
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

    /**
     * 找回密码
     */
    private void okHttpFindPwd() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = CONST.BASE_URL+"user/pwdback";
                JSONObject param = new JSONObject();
                try {
                    param.put("loginName", MyApplication.USERNAME);
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
                                            if (!obj.isNull("password")) {
                                                String pwd = obj.getString("password");
                                                String tip = "随机密码：" + pwd + "。请尽快修改。";
                                                View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_setanther_layout, null);
                                                TextView tv = (TextView) view.findViewById(R.id.dialog_info);
                                                tv.setText(tip);
                                                dialog = new DialogOneButton(getActivity(), view, "知道了", new DialogFactory.DialogListener() {
                                                    @Override
                                                    public void click(String str) {
                                                        dialog.dismiss();
                                                        getActivity().finish();
                                                    }
                                                });
                                                dialog.show();
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
