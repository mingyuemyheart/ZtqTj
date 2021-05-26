package com.pcs.ztqtj.view.activity.photoshow;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.inter.UserFragmentCallBack;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
import com.pcs.ztqtj.view.dialog.DialogFactory;
import com.pcs.ztqtj.view.dialog.DialogOneButton;
import com.pcs.ztqtj.view.fragment.user.FragmentChangePassword;
import com.pcs.ztqtj.view.fragment.user.FragmentFindPassword;
import com.pcs.ztqtj.view.fragment.user.FragmentUserChangePassword;
import com.pcs.ztqtj.view.fragment.user.FragmentUserSetQuestion;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.net.photowall.PackPhotoFindPasswordDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.photowall.PackPhotoFindPasswordUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.photowall.PackPhotoSetUserQuestionDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.photowall.PackPhotoSetUserQuestionUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.photowall.PackPhotoUserChangePasswordDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.photowall.PackPhotoUserChangePasswordUp;

/**
 * 管理密码
 * Created by tyaathome on 2016/9/8.
 */
public class ActivityPhotoPasswordManager extends FragmentActivityZtqBase implements View.OnClickListener{

    private int mType = 0;

    private RadioGroup radioGroup;
    private Button btnSubmit;
    private FragmentChangePassword fragmentChangePassword = new FragmentChangePassword();
    private FragmentFindPassword fragmentFindPassword = new FragmentFindPassword();
    private FragmentUserChangePassword fragmentUserChangePassword = new FragmentUserChangePassword();
    private FragmentUserSetQuestion fragmentUserSetQuestion = new FragmentUserSetQuestion();

    private UserFragmentCallBack mFragment = null;

    private MyReceiver receiver = new MyReceiver();

    private DialogOneButton dialog;

    private String currentNewPassword = "";
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_password_manager);
        setTitleText(R.string.password_manager);
        initView();
        initEvent();
        initData();
    }

    private void initView() {
        radioGroup = (RadioGroup) findViewById(R.id.radio_group);
        btnSubmit = (Button) findViewById(R.id.btn_submit);
    }

    private void initEvent() {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton btn = (RadioButton) findViewById(checkedId);
                btn.setChecked(true);
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                switch (checkedId) {
                    // 修改密码
                    case R.id.rb_change_password:
                        if(mType == 1 || mType == 2) {
                            mFragment = fragmentChangePassword;
                            findViewById(R.id.tv_change_password_tip).setVisibility(View.VISIBLE);
                            findViewById(R.id.tv_find_password_tip).setVisibility(View.GONE);
                        } else if (mType == 3) {
                            mFragment = fragmentUserChangePassword;
                            findViewById(R.id.tv_change_password_tip).setVisibility(View.GONE);
                            findViewById(R.id.tv_find_password_tip).setVisibility(View.GONE);
                        }
                        break;
                    // 找回密码
                    case R.id.rb_find_password:
                        if(mType == 1 || mType == 2) {
                            mFragment = fragmentFindPassword;
                            findViewById(R.id.tv_change_password_tip).setVisibility(View.GONE);
                            findViewById(R.id.tv_find_password_tip).setVisibility(View.VISIBLE);
                        } else if (mType == 3) {
                            mFragment = fragmentUserSetQuestion;
                            findViewById(R.id.tv_change_password_tip).setVisibility(View.GONE);
                            findViewById(R.id.tv_find_password_tip).setVisibility(View.GONE);
                        }
                        break;
                }
                if(mFragment != null) {
                    fragmentTransaction.replace(R.id.fragment, mFragment);
                    fragmentTransaction.commitAllowingStateLoss();
                }
            }
        });
        btnSubmit.setOnClickListener(this);
    }

    private void initData() {
        receiver = new MyReceiver();
        PcsDataBrocastReceiver.registerReceiver(this, receiver);

        // 1:修改密码 2:找回密码 3:个人信息跳转至密码管理页面
        mType = getIntent().getIntExtra("type", 0);
        if(mType == 0) {
            return ;
        }
        RadioButton btn = (RadioButton) findViewById(R.id.rb_find_password);
        switch(mType) {
            case 1:
                radioGroup.check(R.id.rb_change_password);
                btn.setText("找回密码");
                break;
            case 2:
                radioGroup.check(R.id.rb_find_password);
                btn.setText("找回密码");
                break;
            case 3:
                radioGroup.check(R.id.rb_change_password);
                btn.setText("设置密码提示");
                break;
        }



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(receiver != null) {
            PcsDataBrocastReceiver.unregisterReceiver(this, receiver);
            receiver = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btn_submit:
                if(mFragment != null && mFragment.check()) {
                    if(!isNetworkAvailable(ActivityPhotoPasswordManager.this)) {
                        Toast.makeText(ActivityPhotoPasswordManager.this, "网络不可用", Toast.LENGTH_LONG).show();
                        return ;
                    }
                    mFragment.onClickSubmitButton();
                }
                break;
        }
    }

    /**
     * 检测当的网络（WLAN、3G/2G）状态
     * @param context Context
     * @return true 表示网络可用
     */
    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected())
            {
                // 当前网络是连接的
                if (info.getState() == NetworkInfo.State.CONNECTED)
                {
                    // 当前所连接的网络可用
                    return true;
                }
            }
        }
        return false;
    }

    private class MyReceiver extends PcsDataBrocastReceiver {

        @Override
        public void onReceive(String nameStr, String errorStr) {
            if(!TextUtils.isEmpty(errorStr)) {
                return;
            }

            // 找回密码
            if(PackPhotoFindPasswordUp.NAME.equals(nameStr)) {
                dismissProgressDialog();
                final PackPhotoFindPasswordDown down = (PackPhotoFindPasswordDown) PcsDataManager.getInstance().getNetPack(nameStr);
                PcsDataDownload.removeDownload(nameStr);
                if(down == null) {
                    return;
                }
                if(!down.result.equals("1")) {
                    showToast(down.result_msg);
                } else {
                    String tip = "随机密码：" + down.pwd + "。请尽快修改。";
                    View view = LayoutInflater.from(ActivityPhotoPasswordManager.this).inflate(R.layout.dialog_setanther_layout, null);
                    TextView tv = (TextView) view.findViewById(R.id.dialog_info);
                    tv.setText(tip);
                    dialog = new DialogOneButton(ActivityPhotoPasswordManager.this, view, "知道了", new DialogFactory.DialogListener() {

                        @Override
                        public void click(String str) {
                            dialog.dismiss();
                            radioGroup.check(R.id.rb_change_password);
                            Bundle bundle = new Bundle();
                            bundle.putString("username", down.platform_user_id);
                            bundle.putString("password", down.pwd);
                            if(mType == 2 || mType == 1) {
                                fragmentChangePassword.setArguments(bundle);
                            } else if(mType == 3) {
                                //fragmentUserChangePassword.setArguments(bundle);
                            }
                        }
                    });
                    dialog.show();
                }
            }

            //修改密码
            if(PackPhotoUserChangePasswordUp.NAME.equals(nameStr)) {
                dismissProgressDialog();
                PackPhotoUserChangePasswordDown down = (PackPhotoUserChangePasswordDown) PcsDataManager.getInstance().getNetPack(nameStr);
                if(down == null) {
                    return ;
                }

                if(down.result.equals("1")) {
//                    if(mType == 1 || mType == 2) {
//                        ActivityPhotoPasswordManager.this.finish();
//                    }
                    ActivityPhotoPasswordManager.this.finish();
                }
                showToast(down.result_msg);

            }

            // 设置密码提示问题
            if(PackPhotoSetUserQuestionUp.NAME.equals(nameStr)) {
                dismissProgressDialog();
                PackPhotoSetUserQuestionDown down = (PackPhotoSetUserQuestionDown) PcsDataManager.getInstance().getNetPack(nameStr);
                if(down == null) {
                    return ;
                }

                if(down.result.equals("1")) {
                    showToast("设置成功");
                    ActivityPhotoPasswordManager.this.finish();
                } else {
                    showToast(down.result_msg);
                }
            }
        }
    }
}
