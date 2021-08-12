package com.pcs.ztqtj.view.fragment.warning.disaster_reporting;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.pcs.lib.lib_pcs_v3.control.file.PcsMD5;
import com.pcs.lib.lib_pcs_v3.control.tool.Util;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.net.column.PackColumnDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.photowall.PackPhotoLoginDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.photowall.PackPhotoLoginUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.sh_warn.PackYjMyReportDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.sh_warn.YjMyReport;
import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.disaster.AdatperReporting;
import com.pcs.ztqtj.control.adapter.livequery.AdapterData;
import com.pcs.ztqtj.control.tool.AppTool;
import com.pcs.ztqtj.control.tool.CommUtils;
import com.pcs.ztqtj.control.tool.MyConfigure;
import com.pcs.ztqtj.control.tool.utils.TextUtil;
import com.pcs.ztqtj.control.tool.youmeng.LoginAnther;
import com.pcs.ztqtj.control.tool.youmeng.ToolQQPlatform;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.util.OkHttpUtil;
import com.pcs.ztqtj.view.activity.photoshow.ActivityRegister;
import com.pcs.ztqtj.view.fragment.warning.FragmentDisasterReporting;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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
 * 首页-预警中心-灾害直报-我的预报
 */
public class FragmentDisasterMyreport extends FragmentReportBase implements View.OnClickListener {

    private ListView lv_disaster_report;
    private AdatperReporting adapter;
    private List<YjMyReport> list;
    private List<String> mlist, mlist_stutus, mlist_id;
    private TextView disaster_reprot_type, disaster_reprot_status, disaster_reprot_date, tv_register, tv_search_retult;
    private EditText etPhone, etPassword;
    private String phone, password, user_id;
    // 等待对话框
    private ProgressDialog mProgress;
    private LoginAnther login;
    private ToolQQPlatform toolQQLogin;
    private LinearLayout lay_report_title, lay_resptor_login;
    private String ids = "", status = "", time = "", type = "0";
    private int year, month;
    private Button disaster_search_btn;
    private static FragmentDisasterMyreport instance;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState) {
        return LayoutInflater.from(getActivity()).inflate(R.layout.fragment_disater_myreport, container, false);
    }

    public static FragmentDisasterMyreport getInstance() {
        if(instance == null) {
            instance = new FragmentDisasterMyreport();
        }
        return instance;
    }
    private Fragment mCurrentFragment;
    public void setCurrentFragment(Fragment fragment) {
        mCurrentFragment = fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
        initView();
    }

    private void initData() {
        showProgressDialog();
        okHttpAlarmType();
        Calendar c = Calendar.getInstance();
        String t_time = c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1);
        year = c.get(Calendar.YEAR);
        month = (c.get(Calendar.MONTH) + 1);
        String[] str = t_time.split("-");
        if (Integer.valueOf(str[1]) < 10) {
            str[1] = "0" + str[1];
        }
        time = str[0] + "-" + str[1];
        user_id = MyApplication.UID;
        login = new LoginAnther(getActivity());
        toolQQLogin = new ToolQQPlatform(getActivity(), qqLoginListener);
        list = new ArrayList<>();
        mlist_stutus = new ArrayList<>();
        mlist_stutus.add("所有状态");
        mlist_stutus.add("已通过");
        mlist_stutus.add("待审核");
        mlist_stutus.add("被驳回");
        mlist = new ArrayList<>();
        mlist_id = new ArrayList<>();
        if (!TextUtils.isEmpty(user_id)) {
            okHttpReportList();
        }
    }
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden){
            if (!TextUtils.isEmpty(user_id)) {
                okHttpReportList();
            }
        }
    }

    public void refreshDate() {
        user_id = MyApplication.UID;
        if (!TextUtils.isEmpty(user_id)) {
            Calendar c = Calendar.getInstance();
            String t_time = c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1);
            String[] str = t_time.split("-");
            if (Integer.valueOf(str[1]) < 10) {
                str[1] = "0" + str[1];
            }
            showProgressDialog();
            time = str[0] + "-" + str[1];
            okHttpReportList();
        }
    }

    private List<YjMyReport> lists = new ArrayList<>();

    private void initView() {
        lv_disaster_report = (ListView) getView().findViewById(R.id.lv_disaster_report);
        adapter = new AdatperReporting(getActivity(), lists);
        lv_disaster_report.setAdapter(adapter);
        lv_disaster_report.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                commitFragment(lists.get(i).id);
            }
        });
        tv_search_retult = (TextView) getView().findViewById(R.id.tv_search_retult);
        disaster_reprot_type = (TextView) getView().findViewById(R.id.disaster_reprot_type);
        disaster_reprot_type.setOnClickListener(this);
        disaster_reprot_status = (TextView) getView().findViewById(R.id.disaster_reprot_status);
        disaster_reprot_status.setOnClickListener(this);
        disaster_search_btn = (Button) getView().findViewById(R.id.disaster_search_btn);
        disaster_search_btn.setOnClickListener(this);
        disaster_reprot_date = (TextView) getView().findViewById(R.id.disaster_reprot_date);
        disaster_reprot_date.setOnClickListener(this);
        disaster_reprot_date.setText(time);
        lay_report_title = (LinearLayout) getView().findViewById(R.id.lay_report_title);
        lay_resptor_login = (LinearLayout) getView().findViewById(R.id.lay_resptor_login);

        etPhone = (EditText) getView().findViewById(R.id.et_disaster_phone);
        etPassword = (EditText) getView().findViewById(R.id.et_disaster_password);
        ImageButton btn = (ImageButton) getView().findViewById(R.id.btn_disaster_password);
        btn.setOnClickListener(this);
        btn = (ImageButton) getView().findViewById(R.id.btn_clear_disaster);
        btn.setOnClickListener(this);
        Button button = (Button) getView().findViewById(R.id.btn_disaster_login);
        button.setOnClickListener(this);
        btn = (ImageButton) getView().findViewById(R.id.btn_disaster_weixin);
        btn.setOnClickListener(this);
        btn = (ImageButton) getView().findViewById(R.id.btn_disaster_qq);
        btn.setOnClickListener(this);
        btn = (ImageButton) getView().findViewById(R.id.btn_disaster_sina);
        btn.setOnClickListener(this);
        tv_register = (TextView) getView().findViewById(R.id.tv_disaster_register);
        tv_register.setOnClickListener(this);
        setvisibility();
    }

    public void setvisibility() {
        if (TextUtils.isEmpty(user_id)) {
            lay_resptor_login.setVisibility(View.VISIBLE);
            lay_report_title.setVisibility(View.GONE);
            lv_disaster_report.setVisibility(View.GONE);
        } else {
            lay_resptor_login.setVisibility(View.GONE);
            lay_report_title.setVisibility(View.VISIBLE);
            lv_disaster_report.setVisibility(View.VISIBLE);
        }
    }

    public Date getPastHalfYear() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.MONTH, -6);
        Date m3 = c.getTime();
        return m3;
    }

    @Override
    public void onResume() {
        if (!TextUtils.isEmpty(user_id)) {
            lay_resptor_login.setVisibility(View.GONE);
            //lay_report_title.setVisibility(View.VISIBLE);
            lv_disaster_report.setVisibility(View.VISIBLE);
        }
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.disaster_reprot_type:
                createTimePopupWindow(disaster_reprot_type, mlist, "1")
                        .showAsDropDown(disaster_reprot_type);
                break;
            case R.id.disaster_reprot_status:
                createTimePopupWindow(disaster_reprot_status, mlist_stutus, "2")
                        .showAsDropDown(disaster_reprot_status);
                break;
            case R.id.disaster_reprot_date:
                createTimePopupWindow(disaster_reprot_date, getTimes(year, month), "3")
                        .showAsDropDown(disaster_reprot_date);
                break;
            case R.id.disaster_search_btn:
                //time = disaster_reprot_date.getText().toString();
                if (TextUtils.isEmpty(user_id)) {
                    Toast.makeText(getActivity(), "您还未登录", Toast.LENGTH_SHORT).show();
                } else if (time.equals("发布时间")) {
                    Toast.makeText(getActivity(), "您还未选择发布时间", Toast.LENGTH_SHORT).show();
                } else {
                    okHttpReportList();
                }
                break;
            case R.id.btn_disaster_password:
                // 点击一键清除账号
                clearPhone();
                break;
            case R.id.btn_clear_disaster:
                // 点击一键清除密码
                clearPassword();
                break;
            case R.id.btn_disaster_login:
                // 点击登录
                clickLoginZTQ();
                break;
            case R.id.btn_disaster_weixin:
                // 点击微信登录
                clickLoginOtherWay(SHARE_MEDIA.WEIXIN);
                break;
            case R.id.btn_disaster_qq:
                // 点击QQ登陆
                clickLoginOtherWay(SHARE_MEDIA.QQ);
                break;
            case R.id.btn_disaster_sina:
                // 点击新浪微博登陆
                showToast("登录失败，请使用手机号码登录！");
//              clickLoginOtherWay(SHARE_MEDIA.SINA);
                break;
            case R.id.tv_disaster_register:
                // 点击注册按钮
                clickRegister();
                break;
        }
    }


    /**
     * 点击注册按钮
     */
    private void clickRegister() {
        Intent intent = new Intent(getActivity(), ActivityRegister.class);
        Bundle bundle = new Bundle();
        bundle.putString("register_type", "0");
        bundle.putString("title", "注册");
        intent.putExtras(bundle);
        startActivityForResult(intent, MyConfigure.RESULT_USER_REGISTER);
    }

    /**
     * 点击第三方登录按钮
     *
     * @param share
     */
    private void clickLoginOtherWay(SHARE_MEDIA share) {
        //mHandler.sendEmptyMessageDelayed(0, 1000);
        currentPathFrom = share;
        if (share == SHARE_MEDIA.WEIXIN) {
            if (AppTool.isInstalled(getActivity(), "com.tencent.mm")) {
                login.loginPermission(getActivity(), currentPathFrom, permission);
            } else {
                showToast("请先安装微信客户端！");
            }
        } else if (share == SHARE_MEDIA.QQ) {
            toolQQLogin.login();
        } else {
            login.loginPermission(getActivity(), currentPathFrom, permission);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        toolQQLogin.setActivityResult(requestCode, resultCode, data);
        login.onResult(requestCode, requestCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1) {
            switch (requestCode) {
                case MyConfigure.RESULT_USER_REGISTER:
                    String userName = data.getStringExtra("username");
                    String password = data.getStringExtra("password");
                    loginZTQ(userName, password);
                    break;
            }
        }
    }

    private void loginZTQ(String username, String password) {
        if (!isOpenNet()) {
            showToast(getString(R.string.net_err));
            return;
        }
        CommUtils.closeKeyboard(getActivity());
        showProgressDialog();
        mPack = new PackPhotoLoginUp();
        mPack.platform_user_id = username;
        mPack.pwd = PcsMD5.Md5(password);
        mPack.platform_type = PackPhotoLoginUp.PLAFORM_TYPE_ZTQ;
        PcsDataDownload.addDownload(mPack);
    }

    private SHARE_MEDIA currentPathFrom;

    private UMAuthListener permission = new UMAuthListener() {
        @Override
        public void onStart(SHARE_MEDIA share_media) {

        }

        @Override
        public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
            Toast.makeText(getActivity(), "获取授权完成", Toast.LENGTH_SHORT).show();
            login.getInfo(getActivity(), share_media, umListener);
        }

        @Override
        public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
            Toast.makeText(getActivity(), "授权错误", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA share_media, int i) {

            Toast.makeText(getActivity(), "授权错误取消授权", Toast.LENGTH_SHORT).show();
        }
    };

    private UMAuthListener umListener = new UMAuthListener() {
        @Override
        public void onStart(SHARE_MEDIA share_media) {

        }

        @Override
        public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
            Toast.makeText(getActivity(), "获取数据完成", Toast.LENGTH_SHORT).show();
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
         *
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
     * 腾讯获取数据成功
     *
     * @param info
     */
    private void getQQInfoSuccess(Map<String, String> info) {
        // qq平台获取到的数据：
        try {
            loginWeServer(info.get("openid"), info.get("screen_name"), info.get("profile_image_url"), "2");
        } catch (Exception e) {
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
     * 新浪获取数据成功
     *
     * @param info
     */
    private void getSINAInfoSuccess(Map<String, String> info) {
        // 新浪微博返回数据：
        try {
            String str = info.get("result");
            JSONObject jsonObject = new JSONObject(str);
            loginWeServer(jsonObject.optString("idstr"), jsonObject.optString("screen_name"), jsonObject.optString
                    ("profile_image_url"), "1");
        } catch (JSONException e) {
            e.printStackTrace();
            showToast("数据错误，请用手机号登录");
        }
    }

//    @Override
//    public void onHiddenChanged(boolean hidden) {
//        super.onHiddenChanged(hidden);
//        if (!hidden){
//            updateFragment("");
//        }
//    }

    private Toast toast;

    public void showToast(String str) {
        if (toast == null) {
            toast = Toast.makeText(getActivity(), str,
                    Toast.LENGTH_SHORT);
        } else {
            toast.setText(str);
        }
        toast.show();
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
            Toast.makeText(getActivity(), getString(R.string.error_phone_input),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        // 验证手机号长度
//        if (!checkPhoneLength(phone)) {
//            Toast.makeText(getActivity(), getString(R.string.error_phone_length),
//                    Toast.LENGTH_SHORT).show();
//            return;
//        }

        password = etPassword.getText().toString();
        // 验证密码长度
        if (!checkPasswordLength(password)) {
            Toast.makeText(getActivity(), getString(R.string.now_password_hint),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        // 验证密码有效性
        if (!checkPasswordValidity(password)) {
            Toast.makeText(getActivity(), getString(R.string.error_password_validity),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // 登录
        loginZTQ();
    }

    /**
     * 使用知天气账号登录
     */
    private void loginZTQ() {
        if (!isOpenNet()) {
            Toast.makeText(getActivity(), getString(R.string.net_err),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        CommUtils.closeKeyboard(getActivity());
        showProgressDialog();
        mPack = new PackPhotoLoginUp();
        mPack.platform_user_id = phone;
        mPack.pwd = PcsMD5.Md5(password);
        mPack.platform_type = PackPhotoLoginUp.PLAFORM_TYPE_ZTQ;
        PcsDataBrocastReceiver.registerReceiver(getActivity(), mReceiver);
        PcsDataDownload.addDownload(mPack);
    }

    // 上传包
    private PackPhotoLoginUp mPack;
    private PcsDataBrocastReceiver mReceiver = new PcsDataBrocastReceiver() {
        @Override
        public void onReceive(String nameStr, String errorStr) {
            if (!PackPhotoLoginUp.NAME.equals(nameStr)) {
                return;
            }
            dismissProgressDialog();
            if (!TextUtils.isEmpty(errorStr)) {
                Toast.makeText(getActivity(), getString(R.string.error_net), Toast.LENGTH_SHORT).show();
            } else {
                PackPhotoLoginDown packDown = (PackPhotoLoginDown) PcsDataManager.getInstance().getNetPack
                        (PackPhotoLoginUp.NAME);
                if (packDown == null) {
                    Toast.makeText(getActivity(), "登录失败！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!"1".equals(packDown.result)) {
                    Toast.makeText(getActivity(), packDown.result_msg, Toast.LENGTH_SHORT).show();
                    return;
                } else if ("1".equals(packDown.result)) {
                    user_id = packDown.user_id;
                    setvisibility();
                    showProgressDialog();
                    okHttpReportList();
                    Toast.makeText(getActivity(),
                            getString(R.string.login_succ), Toast.LENGTH_SHORT)
                            .show();

                    MyApplication.UID = packDown.fw_user_id;
                    MyApplication.UID = packDown.user_id;
                    MyApplication.NAME = packDown.nick_name;
                    MyApplication.PORTRAIT = packDown.head_url;
                    MyApplication.MOBILE = packDown.mobile;
                    MyApplication.saveUserInfo(getContext());

//                    LoginInformation.getInstance().savePhotoLoginDown(packDown);
                    FragmentDisasterReporting fragmenta = (FragmentDisasterReporting) getParentFragment();
                    fragmenta.updateFragment(3, user_id);
                }
            }

        }
    };

    public void showProgressDialog() {
        showProgressDialog("请等待...");
    }

    /**
     * 取消等待对话框
     */
    public void dismissProgressDialog() {
        if (mProgress != null && mProgress.isShowing()) {
            mProgress.dismiss();
        }
    }

    /**
     * 显示等待对话框
     */
    public void showProgressDialog(String keyWord) {
        if (mProgress == null) {
            mProgress = new ProgressDialog(getActivity());
            mProgress.setCancelable(true);
            mProgress.setCanceledOnTouchOutside(false);
            mProgress.setOnCancelListener(mProgressOnCancel);
        }
        if (mProgress.isShowing()) {
            mProgress.setMessage(keyWord);
        } else {
            mProgress.show();
            mProgress.setMessage(keyWord);
        }
    }

    /**
     * 进度框OnCancel
     */
    private DialogInterface.OnCancelListener mProgressOnCancel = new DialogInterface.OnCancelListener() {
        @Override
        public void onCancel(DialogInterface dialog) {
            getActivity().finish();
        }
    };

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
     * 验证密码长度
     *
     * @param password
     * @return
     */
    private boolean checkPasswordLength(String password) {
        if (TextUtils.isEmpty(password) || password.length() < 6
                || password.length() > 16) {
            return false;
        }
        return true;
    }

    /**
     * 验证密码有效性
     *
     * @param password
     * @return
     */
    private boolean checkPasswordValidity(String password) {
        Pattern pattern = Pattern.compile("[0-9a-zA-Z]*");
        return pattern.matcher(password).matches();
    }


    private int screenHight = 0;

    /**
     * 创建下拉选择列表
     */
    public PopupWindow createTimePopupWindow(final TextView dropDownView, final List<String> dataeaum, final String
            flag) {
        AdapterData dataAdapter = new AdapterData(getActivity(), dataeaum);
        View popcontent = LayoutInflater.from(getActivity()).inflate(R.layout.pop_list_layout, null);
        ListView lv = (ListView) popcontent.findViewById(R.id.mylistviw);
        lv.setAdapter(dataAdapter);
        final PopupWindow pop = new PopupWindow(getActivity());
        pop.setContentView(popcontent);
        pop.setOutsideTouchable(false);
        pop.setWidth((int) (dropDownView.getWidth() * 1.5));
        // 调整下拉框长度
        screenHight = Util.getScreenHeight(getActivity());
        if (dataeaum.size() < 9) {
            pop.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        } else {
            pop.setHeight((int) (screenHight * 0.6));
        }
        pop.setFocusable(true);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pop.dismiss();
                String showTimeStr = dataeaum.get(position);
                dropDownView.setText(showTimeStr);
                if (flag == "1") {
                    ids = mlist_id.get(position);
                } else if (flag == "2") {
                    if (position == 0) {
                        status = "";
                    } else if (position==2){
                        status=(position-2)+"";
                    }else if(position==1){
                        status = position+"";
                    }else{
                        status=(position-1)+"";
                    }

                } else {
                    time = dataeaum.get(position);
                }
            }
        });
        return pop;
    }

    /* 判断是否是有网络*/
    public boolean isOpenNet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context
                .CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 向我们的服务器提交数据 platForm: 1为新浪，2为qq，3为微信
     */
    private void loginWeServer(String userId, String userName, String headUrl,
                               String platForm) {
        Log.e("z", userId + "---" + userName + "---" + headUrl + "---" + platForm);
        if (!isOpenNet()) {
            showToast(getString(R.string.net_err));
            return;
        }
        showProgressDialog();
        mPack = new PackPhotoLoginUp();
        mPack.head_url = headUrl;
        mPack.nick_name = userName;
        mPack.platform_user_id = userId;
        mPack.platform_type = platForm;
        PcsDataDownload.addDownload(mPack);
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
//                    loginWeServer(json.optString("openid"), json.optString("nickname"), json.optString
// ("figureurl_qq_2"), "2");
        }

        @Override
        public void onCancel() {
            showToast("取消获取信息");
            dismissProgressDialog();
        }
    };

    public void updateFragment(String id) {
        Log.e(null, "updateFragment: "+id );
        user_id = MyApplication.UID;
        if (!TextUtils.isEmpty(user_id)) {
            showProgressDialog();
            status=id;
            refreshDate();
            if (id.equals("1")){
                disaster_reprot_status.setText("已通过");
            }else if(id.equals("2")){
                disaster_reprot_status.setText("被驳回");
            }else if(id.equals("0")){
                disaster_reprot_status.setText("待审核");
            }else{
                disaster_reprot_status.setText("所有状态");
            }
        }
    }

    public ArrayList<String> getTimes(int year, int month) {
        ArrayList<String> str = new ArrayList<>();
        if (month == 12) {
            str.add(year + "-" + month);
            str.add(year + "-" + (month - 1));
            str.add(year + "-" + (month - 2));
            str.add(year + "-0" + (month - 3));
            str.add(year + "-0" + (month - 4));
            str.add(year + "-0" + (month - 5));
        } else if (month == 11) {
            str.add(year + "-" + month);
            str.add(year + "-" + (month - 1));
            str.add(year + "-0" + (month - 2));
            str.add(year + "-0" + (month - 3));
            str.add(year + "-0" + (month - 4));
            str.add(year + "-0" + (month - 5));
        } else if (month == 10) {
            str.add(year + "-" + month);
            str.add(year + "-0" + (month - 1));
            str.add(year + "-0" + (month - 2));
            str.add(year + "-0" + (month - 3));
            str.add(year + "-0" + (month - 4));
            str.add(year + "-0" + (month - 5));
        } else if (month >= 6) {
            str.add(year + "-0" + month);
            str.add(year + "-0" + (month - 1));
            str.add(year + "-0" + (month - 2));
            str.add(year + "-0" + (month - 3));
            str.add(year + "-0" + (month - 4));
            str.add(year + "-0" + (month - 5));
        } else if (month == 5) {
            str.add(year + "-0" + month);
            str.add(year + "-0" + (month - 1));
            str.add(year + "-0" + (month - 2));
            str.add(year + "-0" + (month - 3));
            str.add(year + "-0" + (month - 4));
            str.add((year - 1) + "-12");
        } else if (month == 4) {
            str.add(year + "-0" + month);
            str.add(year + "-0" + (month - 1));
            str.add(year + "-0" + (month - 2));
            str.add(year + "-0" + (month - 3));
            str.add((year - 1) + "-12");
            str.add((year - 1) + "-11");
        } else if (month == 3) {
            str.add(year + "-0" + month);
            str.add(year + "-0" + (month - 1));
            str.add(year + "-0" + (month - 2));
            str.add((year - 1) + "-12");
            str.add((year - 1) + "-11");
            str.add((year - 1) + "-10");
        } else if (month == 2) {
            str.add(year + "-0" + month);
            str.add(year + "-0" + (month - 1));
            str.add((year - 1) + "-12");
            str.add((year - 1) + "-11");
            str.add((year - 1) + "-10");
            str.add((year - 1) + "-09");
        } else if (month == 1) {
            str.add(year + "-0" + month);
            str.add((year - 1) + "-12");
            str.add((year - 1) + "-11");
            str.add((year - 1) + "-10");
            str.add((year - 1) + "-09");
            str.add((year - 1) + "-08");
        }
        return str;
    }

    /**
     * 获取预警分类
     */
    private void okHttpAlarmType() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param  = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    String json = param.toString();
                    final String url = CONST.BASE_URL+"alarm/getAlarmTypeList";
                    Log.e("getAlarmTypeList", url);
                    RequestBody body = FormBody.create(MediaType.parse("application/json; charset=utf-8"), json);
                    OkHttpUtil.enqueue(new Request.Builder().post(body).url(url).build(), new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        }
                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            if (!response.isSuccessful()) {
                                return;
                            }
                            final String result = response.body().string();
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.e("getAlarmTypeList", result);
                                    if (!TextUtil.isEmpty(result)) {
                                        try {
                                            JSONObject obj = new JSONObject(result);
                                            if (!obj.isNull("b")) {
                                                JSONObject bobj = obj.getJSONObject("b");
                                                if (!bobj.isNull("column")) {
                                                    JSONObject columnObj = bobj.getJSONObject("column");
                                                    if (!TextUtil.isEmpty(columnObj.toString())) {
                                                        dismissProgressDialog();
                                                        PackColumnDown packDowns = new PackColumnDown();
                                                        packDowns.fillData(columnObj.toString());
                                                        if (packDowns.arrcolumnInfo != null) {
                                                            mlist.clear();
                                                            mlist_id.clear();
                                                            mlist.add("所有灾情");
                                                            mlist_id.add("");
                                                            for (int i = 0; i < packDowns.arrcolumnInfo.size(); i++) {
                                                                mlist.add(packDowns.arrcolumnInfo.get(i).name);
                                                                mlist_id.add(packDowns.arrcolumnInfo.get(i).type);
                                                            }
                                                        }
                                                    }
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

    /**
     * 获取我的报告
     */
    private void okHttpReportList() {
        showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param  = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    JSONObject info = new JSONObject();
                    info.put("user_id", user_id);
                    info.put("pub_time", time);
                    info.put("status", status);
                    info.put("zq_id", ids);
                    param.put("paramInfo", info);
                    String json = param.toString();
                    Log.e("disasterReportList", json);
                    final String url = CONST.BASE_URL+"alarm/disasterReportList";
                    Log.e("disasterReportList", url);
                    RequestBody body = FormBody.create(MediaType.parse("application/json; charset=utf-8"), json);
                    OkHttpUtil.enqueue(new Request.Builder().post(body).url(url).build(), new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        }
                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            if (!response.isSuccessful()) {
                                return;
                            }
                            final String result = response.body().string();
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dismissProgressDialog();
                                    Log.e("addDisasterReport", result);
                                    if (!TextUtil.isEmpty(result)) {
                                        try {
                                            JSONObject obj = new JSONObject(result);
                                            if (!obj.isNull("b")) {
                                                JSONObject bobj = obj.getJSONObject("b");
                                                if (!bobj.isNull("yj_my_zq")) {
                                                    JSONObject itemObj = bobj.getJSONObject("yj_my_zq");
                                                    if (!TextUtil.isEmpty(itemObj.toString())) {
                                                        PackYjMyReportDown packDown = new PackYjMyReportDown();
                                                        packDown.fillData(itemObj.toString());
                                                        if (packDown.list_2.size() > 0) {
                                                            lists.clear();
                                                            lists.addAll(packDown.list_2);
                                                            adapter.notifyDataSetChanged();
                                                            lay_report_title.setVisibility(View.VISIBLE);
                                                            tv_search_retult.setVisibility(View.GONE);
                                                        } else {
                                                            lists.clear();
                                                            lay_report_title.setVisibility(View.GONE);
                                                            tv_search_retult.setVisibility(View.VISIBLE);
                                                            tv_search_retult.setText(time + ",您"+disaster_reprot_status.getText().toString()+"的灾情报告为 0 条");
                                                        }
                                                    }
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
