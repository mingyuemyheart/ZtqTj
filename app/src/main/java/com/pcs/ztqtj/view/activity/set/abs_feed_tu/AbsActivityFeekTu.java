package com.pcs.ztqtj.view.activity.set.abs_feed_tu;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.pcs.lib.lib_pcs_v3.control.file.PcsMD5;
import com.pcs.lib.lib_pcs_v3.control.tool.Util;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalUser;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalUserInfo;
import com.pcs.lib_ztqfj_v2.model.pack.net.SuggestListInfo;
import com.pcs.lib_ztqfj_v2.model.pack.net.photowall.PackPhotoLoginDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.photowall.PackPhotoLoginUp;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.AdatperFeedBackList;
import com.pcs.ztqtj.control.tool.AppTool;
import com.pcs.ztqtj.control.tool.CommUtils;
import com.pcs.ztqtj.control.tool.youmeng.LoginAnther;
import com.pcs.ztqtj.control.tool.youmeng.ToolQQPlatform;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
import com.pcs.ztqtj.view.activity.photoshow.ActivityPhotoRegister;
import com.pcs.ztqtj.view.myview.MyDialog;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Z on 2016/8/1.
 * <p/>
 * 吐槽---意见反馈------父类
 */
public abstract class AbsActivityFeekTu extends FragmentActivityZtqBase implements View.OnClickListener {
    private ListView lv_feedback_list;
    private AdatperFeedBackList adatperFeedBackList;
    private List<SuggestListInfo> arrsuggestListInfo = new ArrayList<SuggestListInfo>();
    private EditText feedback_information;
    private EditText connection_way;

    private LinearLayout no_login_layout;
    private LinearLayout has_login_layout;
    private TextView name_desc;
    private TextView edit_phone_text;
    private TextView edit_pwd_text;

    private Button commit_content;

    public String phoneNum="";

    private PackLocalUser localUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abs_feedback);
        PcsDataBrocastReceiver.registerReceiver(AbsActivityFeekTu.this, myReceiver);
        localUser=ZtqCityDB.getInstance().getMyInfo();
        initView();
        initData();
        initEvent();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myReceiver);
    }

    private void initView() {

        lv_feedback_list = (ListView) findViewById(R.id.lv_feedback_list);

        feedback_information = (EditText) findViewById(R.id.feedback_information);

        no_login_layout = (LinearLayout) findViewById(R.id.no_login_layout);
        has_login_layout = (LinearLayout) findViewById(R.id.has_login_layout);

        connection_way = (EditText) findViewById(R.id.connection_way);//连接方式
        name_desc = (TextView) findViewById(R.id.name_desc);//连接方式
        edit_phone_text = (TextView) findViewById(R.id.edit_phone_text);
        edit_pwd_text = (TextView) findViewById(R.id.edit_pwd_text);

        commit_content = (Button) findViewById(R.id.commit_content);
    }
    private ToolQQPlatform toolQQLogin;
    private LoginAnther login;
    private void initData() {
        toolQQLogin = new ToolQQPlatform(this, qqLoginListener);
        login = new LoginAnther(this);
        adatperFeedBackList = new AdatperFeedBackList(AbsActivityFeekTu.this, arrsuggestListInfo);
        lv_feedback_list.setAdapter(adatperFeedBackList);
        proInitData();
        reqComment();
        judgeHasLogin();
    }


    public abstract void proInitData();

    /*判断是否有登录*/
    private void judgeHasLogin() {
        if (TextUtils.isEmpty(localUser.user_id)) {
            no_login_layout.setVisibility(View.VISIBLE);
            has_login_layout.setVisibility(View.GONE);
            commit_content.setVisibility(View.GONE);
            feedback_information.setVisibility(View.GONE);
        } else {
            has_login_layout.setVisibility(View.VISIBLE);
            no_login_layout.setVisibility(View.GONE);
            commit_content.setVisibility(View.VISIBLE);
            feedback_information.setVisibility(View.VISIBLE);
            hasLogin();
        }

    }

    private void hasLogin() {
        name_desc.setText("尊敬的" + localUser.sys_nick_name + "，欢迎发布建议!");
        String strPhone = localUser.mobile;
        if (TextUtils.isEmpty(strPhone)) {
            connection_way.setHint(getString(R.string.feedback_eidtemail));
        } else {
            connection_way.setText(strPhone.substring(0, 3) + "****" + strPhone.substring(7, strPhone.length()));
        }
        initTextInfo();
    }


    /**
     * 设置内容提示信息
     */
    private void initTextInfo() {
        if (TextUtils.isEmpty(localUser.user_id)) {
            feedback_information.setHint(getString(R.string.login_un_login));
        } else {
            if (TextUtils.isEmpty(localUser.mobile)) {
                feedback_information.setHint(getString(R.string.login_not_bind));
            } else {
                feedback_information.setHint(getString(R.string.login_has_bind));
            }
        }
    }

    private void initEvent() {
        findViewById(R.id.btn_weixin).setOnClickListener(this);
        findViewById(R.id.btn_qq).setOnClickListener(this);
        findViewById(R.id.btn_sina).setOnClickListener(this);
        findViewById(R.id.btn_regeist).setOnClickListener(this);
        findViewById(R.id.btn_login).setOnClickListener(this);
        commit_content.setOnClickListener(this);


        feedback_information.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (TextUtils.isEmpty(localUser.user_id)) {
                        //没有登录处理
                        showDescDialog("尊敬的用户，先请登录客户端");
                    } else {
                        if (TextUtils.isEmpty(connection_way.getText().toString().trim())) {
//                            手机号码为空
                            showDescDialog("尊敬的用户，请输入手机号码");
                        }
                    }
                }
            }
        });
    }


    /**
     * 刷新列表
     */
    protected void reflushListView(List<SuggestListInfo> arrsuggestListInfo) {
        this.arrsuggestListInfo.clear();
        this.arrsuggestListInfo.addAll(arrsuggestListInfo);
        adatperFeedBackList.notifyDataSetChanged();
    }

    /**
     * 获取评论列表
     */
    public abstract void reqComment();

    /**
     * 广播监听返回
     */
    public abstract void receiverBack(String nameStr, String errorStr);

    private PcsDataBrocastReceiver myReceiver = new PcsDataBrocastReceiver() {
        @Override
        public void onReceive(String nameStr, String errorStr) {
            if(mLoginUp!=null&&nameStr.equals(mLoginUp.getName())){
                dismissProgressDialog();
                if (!TextUtils.isEmpty(errorStr)) {
                    showToast(getString(R.string.error_net));
                    return;
                }
                PackPhotoLoginDown packDown = (PackPhotoLoginDown) PcsDataManager.getInstance().getNetPack(PackPhotoLoginUp.NAME);
                if (packDown == null) {
                    showToast( "登录失败！");

                    return;
                }
                if (!"1".equals(packDown.result)) {
                    showToast( packDown.result_msg);

                    return;
                } else if ("1".equals(packDown.result)) {
                    showToast(getString(R.string.login_succ));
//                    LoginInformation.getInstance().reSetValue(packDown);
                    PackLocalUser myUserInfo = new PackLocalUser();
                    myUserInfo.user_id = packDown.fw_user_id;
                    myUserInfo.sys_user_id=packDown.user_id;
                    myUserInfo.sys_nick_name=packDown.nick_name;
                    myUserInfo.sys_head_url=packDown.head_url;
                    myUserInfo.mobile=packDown.mobile;
                    myUserInfo.is_jc = packDown.is_jc;
                    localUser=myUserInfo;
                    PackLocalUserInfo packLocalUserInfo = new PackLocalUserInfo();
                    packLocalUserInfo.currUserInfo = myUserInfo;
                    ZtqCityDB.getInstance().setMyInfo(packLocalUserInfo);

                    judgeHasLogin();
                }
            }else{
                receiverBack(nameStr, errorStr);
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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

//              clickLoginOtherWay(SHARE_MEDIA.SINA);

                break;
            case R.id.btn_regeist:
//                注册
                clickRegister();
                break;
            case R.id.btn_login:
//                登录
                loginZTQ();
                break;
            case R.id.commit_content:
                String phoneNumber = localUser.mobile;
                if (TextUtils.isEmpty(phoneNumber)) {
                    phoneNumber = connection_way.getText().toString().trim();
                }

                reqNet(feedback_information.getText().toString().trim(), phoneNumber);//提交反馈数据
                break;
        }
    }

    public void cleanUpInfo(){
        feedback_information.setText("");
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
            if (AppTool.isInstalled(AbsActivityFeekTu.this, "com.tencent.mm")) {
                login.loginPermission(AbsActivityFeekTu.this, currentPathFrom, permission);
            } else {
                showToast("请先安装微信客户端！");
            }
        } else if (share == SHARE_MEDIA.QQ) {
            toolQQLogin.login();
        } else {
            login.loginPermission(AbsActivityFeekTu.this, currentPathFrom, permission);
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
        startActivity(intent);
    }

  private   PackPhotoLoginUp mLoginUp;
    /**
     * 使用知天气账号登录
     */
    private void loginZTQ() {
        CommUtils.closeKeyboard(this);
        String phone=edit_phone_text.getText().toString().trim();
        String pwd=edit_pwd_text.getText().toString().trim();
        phoneNum=phone;
        if(TextUtils.isEmpty(phone)){
            showToast(getString(R.string.error_password_length));
            return;
        }
        if(TextUtils.isEmpty(pwd)){
            showToast("请输入密码");
            return;
        }
        if(!isOpenNet()){
            showToast(getString(R.string.net_err));
            return ;
        }


        showProgressDialog();
        mLoginUp = new PackPhotoLoginUp();
        mLoginUp.platform_user_id =phone;
        mLoginUp.pwd = PcsMD5.Md5(pwd);
        mLoginUp.platform_type = PackPhotoLoginUp.PLAFORM_TYPE_ZTQ;
        PcsDataDownload.addDownload(mLoginUp);
    }


    /**
     * 向我们的服务器提交数据 platForm: 1为新浪，2为qq，3为微信
     */
    private void loginWeServer(String userId, String userName, String headUrl,
                               String platForm) {
        if(!isOpenNet()){
            showToast(getString(R.string.net_err));
            return ;
        }
        showProgressDialog();
        mLoginUp = new PackPhotoLoginUp();
        mLoginUp.head_url = headUrl;
        mLoginUp.nick_name = userName;
        mLoginUp.platform_user_id = userId;
        mLoginUp.platform_type = platForm;
        PcsDataDownload.addDownload(mLoginUp);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        toolQQLogin.setActivityResult(requestCode, resultCode, data);
        login.onResult(requestCode, requestCode, data);
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
            JSONObject json = (JSONObject) response;
            loginWeServer(toolQQLogin.getOpenId(), json.optString("nickname"), json.optString("figureurl_qq_2"), "2");
        }

        @Override
        public void onCancel() {
            showToast("取消获取信息");
            dismissProgressDialog();
        }
    };

    private SHARE_MEDIA currentPathFrom;
    private UMAuthListener permission = new UMAuthListener() {
        @Override
        public void onStart(SHARE_MEDIA share_media) {

        }

        @Override
        public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
            showToast("获取授权完成");
            login.getInfo(AbsActivityFeekTu.this, share_media, umListener);
        }

        @Override
        public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
            showToast( "授权错误");
        }

        @Override
        public void onCancel(SHARE_MEDIA share_media, int i) {

            showToast("授权错误取消授权");
        }
    };

    private UMAuthListener umListener = new UMAuthListener() {
        @Override
        public void onStart(SHARE_MEDIA share_media) {

        }

        @Override
        public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
            showToast("获取数据完成");
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
     * 新浪获取数据成功
     *
     * @param info
     */
    private void getSINAInfoSuccess(Map<String, String> info) {
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
     *
     * @param info
     */
    private void getQQInfoSuccess(Map<String, String> info) {
        // qq平台获取到的数据：

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
     * 提交评论信息
     */
    public abstract void commitInformation(String upContent, String phoneNumber);

    private void reqNet(String upContent, String phoneNumber) {
        // 上传包
        if (upContent == null || "".equals(upContent)) {
            showToast("还没填写内容，请填写内容。");
            return;
        }
        phoneNum=phoneNumber;
        commitInformation( upContent,phoneNumber);
    }


    private MyDialog dialogDesc;

    /**
     * 退出登录初始化对话框
     *
     * @return
     */
    private void showDescDialog(String desc) {
        final TextView tvDesc = new TextView(this);
        tvDesc.setTextSize(17);
        tvDesc.setGravity(Gravity.CENTER);
        int pd = Util.dip2px(this, 10);
        tvDesc.setPadding(pd, pd, pd, pd);
        tvDesc.setTextColor(getResources().getColor(R.color.text_black));
        tvDesc.setText(desc);
        final String yes = "确定";
        dialogDesc = new MyDialog(this, tvDesc, yes, new MyDialog.DialogListener() {
            @Override
            public void click(String str) {
                dialogDesc.dismiss();
                feedback_information.clearFocus();
            }
        });
        dialogDesc.setCancelable(false);
        dialogDesc.setCanceledOnTouchOutside(false);
        dialogDesc.show();
    }
}