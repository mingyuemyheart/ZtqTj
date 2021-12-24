package com.pcs.ztqtj.view.activity.set;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationManagerCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCity;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityMain;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackQueryPushTagDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackQueryPushTagUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.push.PushTag;
import com.pcs.lib_ztqfj_v2.model.pack.net.push.PushTagContant;
import com.pcs.lib_ztqfj_v2.model.pack.net.push.SetPushTagDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.push.SetPushTagUp;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.CommUtils;
import com.pcs.ztqtj.control.tool.LocalDataHelper;
import com.pcs.ztqtj.control.tool.MyConfigure;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
import com.pcs.ztqtj.view.dialog.DialogFactory;
import com.pcs.ztqtj.view.dialog.DialogTwoButton;

import java.util.HashMap;
import java.util.Map;

/**
 * 实况告警推送
 *
 * @author chenjh
 */
public class AcitvityWarnLivePush extends FragmentActivityZtqBase implements OnClickListener {

    private Button higtBtn;
    private Button lowtBtn;
    private Button visibilityBtn;
    private Button highHumidityBtn;
    private Button lowHumidityBtn;
    private Button rainfallBtn;
    private Button windBtn;

    private CheckBox higtCheckBox;
    private CheckBox lowtCheckBox;
    private CheckBox visibilityCheckBox;
    private CheckBox highHumidityCheckBox;
    private CheckBox lowHumidityCheckBox;
    private CheckBox rainfallCheckBox;
    private CheckBox windCheckBox;

    private TextView setcity;

    private Button setmodel;
    private TextView current_model;

    private MyReceiver receiver = new MyReceiver();
    private Map<String, String> params = new HashMap<String, String>();

    private String cityName = "";
    private String token = "";

    private String[] itemValues = new String[7];
    private boolean[] itemSwitchs = new boolean[7];

    private Button bt_push_confirm;
    private DialogTwoButton modifieDialog;
    private Boolean isBackCommit=true;
    private PackLocalCity ssta;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        setTitleText(bundle.getString("title"));
        ssta = (PackLocalCity) bundle.getSerializable("city");
        setContentView(R.layout.activity_warnlivepush);
        initView();
        initData();
        initEvent();
        initModifieDialog();
    }

    private void initView() {

        higtBtn = (Button) findViewById(R.id.higt_btn);
        lowtBtn = (Button) findViewById(R.id.lowt_btn);
        visibilityBtn = (Button) findViewById(R.id.visibility_btn);
        highHumidityBtn = (Button) findViewById(R.id.high_humidity_btn);
        lowHumidityBtn = (Button) findViewById(R.id.low_humidity_btn);
        rainfallBtn = (Button) findViewById(R.id.rainfall_btn);
        windBtn = (Button) findViewById(R.id.wind_btn);

        higtCheckBox = (CheckBox) findViewById(R.id.checkbox_higt);
        lowtCheckBox = (CheckBox) findViewById(R.id.checkbox_lowt);
        visibilityCheckBox = (CheckBox) findViewById(R.id.checkbox_visibility);
        highHumidityCheckBox = (CheckBox) findViewById(R.id.checkbox_high_humidity);
        lowHumidityCheckBox = (CheckBox) findViewById(R.id.checkbox_low_humidity);
        rainfallCheckBox = (CheckBox) findViewById(R.id.checkbox_rainfall);
        windCheckBox = (CheckBox) findViewById(R.id.checkbox_wind);
        bt_push_confirm = (Button) findViewById(R.id.bt_push_confirm);

        setcity = (TextView) findViewById(R.id.setcity);
        current_model = (TextView) findViewById(R.id.current_model);
        setmodel = (Button) findViewById(R.id.setmodel);
        current_model.setText("消息栏模式");
    }

    private void initEvent() {
        // 实况告警阈值区间：
        // 温度：-50~50
        // 能见度：0~5000；
        // 湿度：0~100；
        // 小时雨量：0~200
        // 风速：0~100
        // setRegion(higtEditText, 25, 999);
        // setRegion(lowtEditText, -273, 24);
        // setRegion(visibilityEditText, 0, 5000);
        // setRegion(highHumidityEditText, 70, 999);
        // setRegion(lowHumidityEditText, 0, 100);
        // setRegion(rainfallEditText, 0, 200);
        // setRegion(windEditText, 0, 100);

        higtCheckBox.setOnClickListener(clickListener);
        lowtCheckBox.setOnClickListener(clickListener);
        visibilityCheckBox.setOnClickListener(clickListener);
        highHumidityCheckBox.setOnClickListener(clickListener);
        lowHumidityCheckBox.setOnClickListener(clickListener);
        rainfallCheckBox.setOnClickListener(clickListener);
        windCheckBox.setOnClickListener(clickListener);
        bt_push_confirm.setOnClickListener(clickListener);

        higtBtn.setOnClickListener(this);
        lowtBtn.setOnClickListener(this);
        visibilityBtn.setOnClickListener(this);
        // highHumidityBtn.setOnClickListener(this);
        lowHumidityBtn.setOnClickListener(this);
        rainfallBtn.setOnClickListener(this);
        windBtn.setOnClickListener(this);
        setBackListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                    if (isModify()){
                        modifieDialog.show();
                    }else{
                        AcitvityWarnLivePush.this.finish();
                    }

            }
        });

    }

    @Override
    public void onBackPressed() {

            if (isModify()){
                modifieDialog.show();
            }else{
                AcitvityWarnLivePush.this.finish();
            }

    }

    private void initData() {
        // 设备token
//		token = (String) LocalDataHelper.getPushTag(getApplicationContext(), "token", String.class);
//        token = XGPushConfig.getToken(this);
        cityName = (String) LocalDataHelper.getPushTag(AcitvityWarnLivePush.this, PushTag.getInstance()
                .PUSHTAG_WARNING_CITY_NAME, String.class);
//        if (!TextUtils.isEmpty(cityName)) {
            PushTagContant.setCityName(cityName);
            setcity.setText(ssta.NAME);
//        }else {
//            setcity.setText(PushTagContant.getCityName());
//        }
        initCheckBox();
        // 注册广播接收
        PcsDataBrocastReceiver.registerReceiver(AcitvityWarnLivePush.this, receiver);
        checkDataServer(token);
    }

    private void initModifieDialog() {
        View view = LayoutInflater.from(this).inflate(
                R.layout.dialog_message, null);
        ((TextView) view.findViewById(R.id.dialogmessage))
                .setText("设置信息还未提交，要提交吗？");
        modifieDialog = new DialogTwoButton(this,
                view, "提交", "放弃", new DialogFactory.DialogListener() {
            @Override
            public void click(String str) {
                modifieDialog.dismiss();
                if (str.equals("放弃")) {
                    finish();
                } else if(str.equals("提交")) {
                    if (!NotificationManagerCompat.from(AcitvityWarnLivePush.this).areNotificationsEnabled()){
                        checkNotificationPermission(AcitvityWarnLivePush.this);
                    }else{
                        isBackCommit=true;
                        showProgressDialog();
                        params.clear();
                        setLocalPushTag(PushTag.getInstance().VALUE_HIGH_TEMPERATURE, higtCheckBox.isChecked(), PushTag
                                .getInstance().LOCAL_WARNING_INFO_HIGH_TEMPERATURE, higtBtn);
                        setLocalPushTag(PushTag.getInstance().VALUE_LOW_TEMPERATURE, lowtCheckBox.isChecked(), PushTag
                                .getInstance().LOCAL_WARNING_INFO_LOW_TEMPERATURE, lowtBtn);
                        setLocalPushTag(PushTag.getInstance().VALUE_VISIBILITY, visibilityCheckBox.isChecked(), PushTag
                                .getInstance().LOCAL_WARNING_INFO_VISIBILITY, visibilityBtn);
                        setLocalPushTag(PushTag.getInstance().VALUE_HIGH_HUMIDITY, highHumidityCheckBox.isChecked(),
                                PushTag.getInstance().LOCAL_WARNING_INFO_HIGH_HUMIDITY, highHumidityBtn);
                        setLocalPushTag(PushTag.getInstance().VALUE_LOW_HUMIDITY, lowHumidityCheckBox.isChecked(),
                                PushTag.getInstance().LOCAL_WARNING_INFO_LOW_HUMIDITY, lowHumidityBtn);
                        setLocalPushTag(PushTag.getInstance().VALUE_HOURLY_RAINFALL, rainfallCheckBox.isChecked(),
                                PushTag.getInstance().LOCAL_WARNING_INFO_HOURLY_RAINFALL, rainfallBtn);
                        setLocalPushTag(PushTag.getInstance().VALUE_WIND_SPEED, windCheckBox.isChecked(), PushTag
                                .getInstance().LOCAL_WARNING_INFO_WIND_SPEED, windBtn);
                        setPushTag();
                    }
                }
            }
        });
        modifieDialog.setTitle("天津气象");
    }

    private PackQueryPushTagDown serverDataDown;

    /**
     * 获取服务器的值
     *
     * @param token
     */
    private void checkDataServer(String token) {
        if (!isOpenNet()) {
            showToast(getString(R.string.net_err));
            return;
        }
        serverDataDown = new PackQueryPushTagDown();
        PackQueryPushTagUp up = new PackQueryPushTagUp();
        up.token = token;
        up.pushType = "2";
        showProgressDialog();
        PcsDataDownload.addDownload(up);
    }

    private void initCheckBox() {

        itemValues[0] = (String) LocalDataHelper.getPushTag(AcitvityWarnLivePush.this, PushTag.getInstance()
                .VALUE_HIGH_TEMPERATURE, String.class);
        itemValues[1] = (String) LocalDataHelper.getPushTag(AcitvityWarnLivePush.this, PushTag.getInstance()
                .VALUE_LOW_TEMPERATURE, String.class);
        itemValues[2] = (String) LocalDataHelper.getPushTag(AcitvityWarnLivePush.this, PushTag.getInstance()
                .VALUE_VISIBILITY, String.class);
        itemValues[3] = (String) LocalDataHelper.getPushTag(AcitvityWarnLivePush.this, PushTag.getInstance()
                .VALUE_HIGH_HUMIDITY, String.class);
        itemValues[4] = (String) LocalDataHelper.getPushTag(AcitvityWarnLivePush.this, PushTag.getInstance()
                .VALUE_LOW_HUMIDITY, String.class);
        itemValues[5] = (String) LocalDataHelper.getPushTag(AcitvityWarnLivePush.this, PushTag.getInstance()
                .VALUE_HOURLY_RAINFALL, String.class);
        itemValues[6] = (String) LocalDataHelper.getPushTag(AcitvityWarnLivePush.this, PushTag.getInstance()
                .VALUE_WIND_SPEED, String.class);

        itemSwitchs[0] = (Boolean) LocalDataHelper.getPushTag(AcitvityWarnLivePush.this, PushTag.getInstance()
                .LOCAL_WARNING_INFO_HIGH_TEMPERATURE, Boolean.class);
        itemSwitchs[1] = (Boolean) LocalDataHelper.getPushTag(AcitvityWarnLivePush.this, PushTag.getInstance()
                .LOCAL_WARNING_INFO_LOW_TEMPERATURE, Boolean.class);
        itemSwitchs[2] = (Boolean) LocalDataHelper.getPushTag(AcitvityWarnLivePush.this, PushTag.getInstance()
                .LOCAL_WARNING_INFO_VISIBILITY, Boolean.class);
        itemSwitchs[3] = (Boolean) LocalDataHelper.getPushTag(AcitvityWarnLivePush.this, PushTag.getInstance()
                .LOCAL_WARNING_INFO_HIGH_HUMIDITY, Boolean.class);
        itemSwitchs[4] = (Boolean) LocalDataHelper.getPushTag(AcitvityWarnLivePush.this, PushTag.getInstance()
                .LOCAL_WARNING_INFO_LOW_HUMIDITY, Boolean.class);
        itemSwitchs[5] = (Boolean) LocalDataHelper.getPushTag(AcitvityWarnLivePush.this, PushTag.getInstance()
                .LOCAL_WARNING_INFO_HOURLY_RAINFALL, Boolean.class);
        itemSwitchs[6] = (Boolean) LocalDataHelper.getPushTag(AcitvityWarnLivePush.this, PushTag.getInstance()
                .LOCAL_WARNING_INFO_WIND_SPEED, Boolean.class);

        higtBtn.setText(itemValues[0] + "");
        lowtBtn.setText(itemValues[1] + "");
        visibilityBtn.setText(itemValues[2] + "");
        highHumidityBtn.setText(itemValues[3] + "");
        lowHumidityBtn.setText(itemValues[4] + "");
        rainfallBtn.setText(itemValues[5] + "");
        windBtn.setText(itemValues[6] + "");

        higtCheckBox.setChecked(itemSwitchs[0]);
        lowtCheckBox.setChecked(itemSwitchs[1]);
        visibilityCheckBox.setChecked(itemSwitchs[2]);
        highHumidityCheckBox.setChecked(itemSwitchs[3]);
        lowHumidityCheckBox.setChecked(itemSwitchs[4]);
        rainfallCheckBox.setChecked(itemSwitchs[5]);
        windCheckBox.setChecked(itemSwitchs[6]);

        setButtonEnabled(higtCheckBox.isChecked(), higtBtn);
        setButtonEnabled(lowtCheckBox.isChecked(), lowtBtn);
        setButtonEnabled(visibilityCheckBox.isChecked(), visibilityBtn);
        setButtonEnabled(highHumidityCheckBox.isChecked(), highHumidityBtn);
        setButtonEnabled(lowHumidityCheckBox.isChecked(), lowHumidityBtn);
        setButtonEnabled(rainfallCheckBox.isChecked(), rainfallBtn);
        setButtonEnabled(windCheckBox.isChecked(), windBtn);
    }

    // /** 7个实况预警推送类型中有至少一个的为true时，返回true。即，实况预警标签设置为1 **/
    // private static boolean getFlag(boolean[] flags) {
    // for (int i = 0; i < flags.length; i++) {
    // if (flags[i]) {
    // return true;
    // }
    // }
    // return false;
    // }

    private void getData(){
        itemValues[0] = (String) LocalDataHelper.getPushTag(AcitvityWarnLivePush.this, PushTag.getInstance()
                .VALUE_HIGH_TEMPERATURE, String.class);
        itemValues[1] = (String) LocalDataHelper.getPushTag(AcitvityWarnLivePush.this, PushTag.getInstance()
                .VALUE_LOW_TEMPERATURE, String.class);
        itemValues[2] = (String) LocalDataHelper.getPushTag(AcitvityWarnLivePush.this, PushTag.getInstance()
                .VALUE_VISIBILITY, String.class);
        itemValues[3] = (String) LocalDataHelper.getPushTag(AcitvityWarnLivePush.this, PushTag.getInstance()
                .VALUE_HIGH_HUMIDITY, String.class);
        itemValues[4] = (String) LocalDataHelper.getPushTag(AcitvityWarnLivePush.this, PushTag.getInstance()
                .VALUE_LOW_HUMIDITY, String.class);
        itemValues[5] = (String) LocalDataHelper.getPushTag(AcitvityWarnLivePush.this, PushTag.getInstance()
                .VALUE_HOURLY_RAINFALL, String.class);
        itemValues[6] = (String) LocalDataHelper.getPushTag(AcitvityWarnLivePush.this, PushTag.getInstance()
                .VALUE_WIND_SPEED, String.class);

        itemSwitchs[0] = (Boolean) LocalDataHelper.getPushTag(AcitvityWarnLivePush.this, PushTag.getInstance()
                .LOCAL_WARNING_INFO_HIGH_TEMPERATURE, Boolean.class);
        itemSwitchs[1] = (Boolean) LocalDataHelper.getPushTag(AcitvityWarnLivePush.this, PushTag.getInstance()
                .LOCAL_WARNING_INFO_LOW_TEMPERATURE, Boolean.class);
        itemSwitchs[2] = (Boolean) LocalDataHelper.getPushTag(AcitvityWarnLivePush.this, PushTag.getInstance()
                .LOCAL_WARNING_INFO_VISIBILITY, Boolean.class);
        itemSwitchs[3] = (Boolean) LocalDataHelper.getPushTag(AcitvityWarnLivePush.this, PushTag.getInstance()
                .LOCAL_WARNING_INFO_HIGH_HUMIDITY, Boolean.class);
        itemSwitchs[4] = (Boolean) LocalDataHelper.getPushTag(AcitvityWarnLivePush.this, PushTag.getInstance()
                .LOCAL_WARNING_INFO_LOW_HUMIDITY, Boolean.class);
        itemSwitchs[5] = (Boolean) LocalDataHelper.getPushTag(AcitvityWarnLivePush.this, PushTag.getInstance()
                .LOCAL_WARNING_INFO_HOURLY_RAINFALL, Boolean.class);
        itemSwitchs[6] = (Boolean) LocalDataHelper.getPushTag(AcitvityWarnLivePush.this, PushTag.getInstance()
                .LOCAL_WARNING_INFO_WIND_SPEED, Boolean.class);
    }

    private boolean isModify() {
        if (!higtBtn.getText().toString().equals(itemValues[0])) {
            return true;
        }
        if (!lowtBtn.getText().toString().equals(itemValues[1])) {
            return true;
        }
        if (!visibilityBtn.getText().toString().equals(itemValues[2])) {
            return true;
        }
        if (!highHumidityBtn.getText().toString().equals(itemValues[3])) {
            return true;
        }
        if (!lowHumidityBtn.getText().toString().equals(itemValues[4])) {
            return true;
        }
        if (!rainfallBtn.getText().toString().equals(itemValues[5])) {
            return true;
        }
        if (!windBtn.getText().toString().equals(itemValues[6])) {
            return true;
        }
        if (higtCheckBox.isChecked()!=itemSwitchs[0]){
            return true;
        }
        if (lowtCheckBox.isChecked()!=itemSwitchs[1]){
            return true;
        }
        if (visibilityCheckBox.isChecked()!=itemSwitchs[2]){
            return true;
        }
        if (highHumidityCheckBox.isChecked()!=itemSwitchs[3]){
            return true;
        }
        if (lowHumidityCheckBox.isChecked()!=itemSwitchs[4]){
            return true;
        }
        if (rainfallCheckBox.isChecked()!=itemSwitchs[5]){
            return true;
        }
        if (windCheckBox.isChecked()!=itemSwitchs[6]){
            return true;
        }

        return false;
    }

    /**
     * 设置EditText是否可编辑
     *
     * @param mValue
     * @param editText
     */
    private void setEditTextEnabled(final boolean mValue, final EditText editText) {
        if (mValue) {
            editText.setEnabled(false); // 开关打开时，设置编辑框不可编辑,保存数值
        } else {
            editText.setEnabled(true); // 开关关闭时，设置编辑框可编辑
        }
    }

    /**
     * 设置Button是否可点击
     *
     * @param mValue
     */
    private void setButtonEnabled(final boolean mValue, final Button btn) {
        if (mValue) {
            btn.setEnabled(true); // 开关打开时，设置不可编辑,保存数值
            btn.setBackgroundResource(R.drawable.btn_dialog_normal);
        } else {
            btn.setEnabled(false); // 开关打开时，设置不可编辑,保存数值
            btn.setBackgroundResource(R.drawable.btn_dialog_pressed);
        }
    }

    /**
     * item推送类型
     *
     * @param mValue  item推送开关
     * @param itmeKey item数值类型
     *                item数值
     */
    private void setLocalPushTag(final String mkey, final boolean mValue, final String itmeKey, final Button btn) {
        String numValue = btn.getText().toString();
        if (!mValue) {// 当开关关闭时，传空值给接口
            numValue = "";
        }
        setButtonEnabled(mValue, btn);
        params.put(mkey, numValue);
    }

    /**
     * item推送类型
     *
     * @param mValue  item推送开关
     * @param itmeKey item数值类型
     *                item数值
     */
    private void saveLocalPushTag(final String mkey, final boolean mValue, final String itmeKey, final Button
            editText) {
        String numValue = editText.getText().toString();
        if (mValue) {
            editText.setEnabled(false); // 开关打开时，设置编辑框不可编辑,保存数值
            // 开关打开时，设置编辑框不可编辑,保存数值
            LocalDataHelper.savePushTag(AcitvityWarnLivePush.this, mkey, numValue);
        } else {
            editText.setEnabled(true); // 开关关闭时，设置编辑框可编辑
        }
        // 保存开关状态
        LocalDataHelper.savePushTag(AcitvityWarnLivePush.this, itmeKey, mValue);
        // System.out.println("mkey: "+mkey +"> "+ mValue);
    }

    /**
     * 设置信鸽推送标签
     *
     * @param
     * @param
     */
    private void setPushTag() {
        if (!isOpenNet()) {
            showToast(getString(R.string.net_err));
            return;
        }
        PackLocalCityMain cityMain = ZtqCityDB.getInstance().getCityMain();
        SetPushTagUp setPushTagUp = new SetPushTagUp();
        setPushTagUp.token = token;
        params.put("warning_city",cityMain.ID);
        params.put("yjxx_city",cityMain.ID);
        params.put("weatherForecast_city",cityMain.ID);
        setPushTagUp.params = params;
//        setPushTagUp.key = key;
        setPushTagUp.pushType = "2";
        PcsDataDownload.addDownload(setPushTagUp);
    }

    private OnClickListener clickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.checkbox_higt:
                    setButtonEnabled(higtCheckBox.isChecked(), higtBtn);
//                    if (!NotificationManagerCompat.from(AcitvityWarnLivePush.this).areNotificationsEnabled()){
//                        checkNotificationPermission(AcitvityWarnLivePush.this);
//                    }else{
//                        setLocalPushTag(PushTag.getInstance().VALUE_HIGH_TEMPERATURE, higtCheckBox.isChecked(),
// PushTag
//                                .getInstance().LOCAL_WARNING_INFO_HIGH_TEMPERATURE, higtBtn);
//                    }

                    break;
                case R.id.checkbox_lowt:
                    setButtonEnabled(lowtCheckBox.isChecked(), lowtBtn);
//                    if (!NotificationManagerCompat.from(AcitvityWarnLivePush.this).areNotificationsEnabled()){
//                        checkNotificationPermission(AcitvityWarnLivePush.this);
//                    }else{
//                        setLocalPushTag(PushTag.getInstance().VALUE_LOW_TEMPERATURE, lowtCheckBox.isChecked(), PushTag
//                                .getInstance().LOCAL_WARNING_INFO_LOW_TEMPERATURE, lowtBtn);
//                    }
                    break;
                case R.id.checkbox_visibility:
                    setButtonEnabled(visibilityCheckBox.isChecked(), visibilityBtn);
//                    if (!NotificationManagerCompat.from(AcitvityWarnLivePush.this).areNotificationsEnabled()){
//                        checkNotificationPermission(AcitvityWarnLivePush.this);
//                    }else{
//                        setLocalPushTag(PushTag.getInstance().VALUE_VISIBILITY, visibilityCheckBox.isChecked(),
// PushTag
//                                .getInstance().LOCAL_WARNING_INFO_VISIBILITY, visibilityBtn);
//                    }
                    break;
                case R.id.checkbox_high_humidity:
                    setButtonEnabled(highHumidityCheckBox.isChecked(), highHumidityBtn);
//                    if (!NotificationManagerCompat.from(AcitvityWarnLivePush.this).areNotificationsEnabled()){
//                        checkNotificationPermission(AcitvityWarnLivePush.this);
//                    }else{
//                        setLocalPushTag(PushTag.getInstance().VALUE_HIGH_HUMIDITY, highHumidityCheckBox.isChecked(),
//                                PushTag.getInstance().LOCAL_WARNING_INFO_HIGH_HUMIDITY, highHumidityBtn);
//                    }
                    break;
                case R.id.checkbox_low_humidity:
                    setButtonEnabled(lowHumidityCheckBox.isChecked(), lowHumidityBtn);
//                    if (!NotificationManagerCompat.from(AcitvityWarnLivePush.this).areNotificationsEnabled()){
//                        checkNotificationPermission(AcitvityWarnLivePush.this);
//                    }else{
//                        setLocalPushTag(PushTag.getInstance().VALUE_LOW_HUMIDITY, lowHumidityCheckBox.isChecked(),
//                                PushTag.getInstance().LOCAL_WARNING_INFO_LOW_HUMIDITY, lowHumidityBtn);
//                    }
                    break;
                case R.id.checkbox_rainfall:
                    setButtonEnabled(rainfallCheckBox.isChecked(), rainfallBtn);
//                    if (!NotificationManagerCompat.from(AcitvityWarnLivePush.this).areNotificationsEnabled()){
//                        checkNotificationPermission(AcitvityWarnLivePush.this);
//                    }else{
//                        setLocalPushTag(PushTag.getInstance().VALUE_HOURLY_RAINFALL, rainfallCheckBox.isChecked(),
//                                PushTag.getInstance().LOCAL_WARNING_INFO_HOURLY_RAINFALL, rainfallBtn);
//                    }
                    break;
                case R.id.checkbox_wind:
                    setButtonEnabled(windCheckBox.isChecked(), windBtn);
//                    if (!NotificationManagerCompat.from(AcitvityWarnLivePush.this).areNotificationsEnabled()){
//                        checkNotificationPermission(AcitvityWarnLivePush.this);
//                    }else{
//                        setLocalPushTag(PushTag.getInstance().VALUE_WIND_SPEED, windCheckBox.isChecked(), PushTag
//                                .getInstance().LOCAL_WARNING_INFO_WIND_SPEED, windBtn);
//                    }
                    break;
                case R.id.bt_push_confirm:
                    if (!NotificationManagerCompat.from(AcitvityWarnLivePush.this).areNotificationsEnabled()) {
                        checkNotificationPermission(AcitvityWarnLivePush.this);
                    } else {
                        isBackCommit=false;
                        showProgressDialog();
                        params.clear();
                        setLocalPushTag(PushTag.getInstance().VALUE_HIGH_TEMPERATURE, higtCheckBox.isChecked(), PushTag
                                .getInstance().LOCAL_WARNING_INFO_HIGH_TEMPERATURE, higtBtn);
                        setLocalPushTag(PushTag.getInstance().VALUE_LOW_TEMPERATURE, lowtCheckBox.isChecked(), PushTag
                                .getInstance().LOCAL_WARNING_INFO_LOW_TEMPERATURE, lowtBtn);
                        setLocalPushTag(PushTag.getInstance().VALUE_VISIBILITY, visibilityCheckBox.isChecked(), PushTag
                                .getInstance().LOCAL_WARNING_INFO_VISIBILITY, visibilityBtn);
                        setLocalPushTag(PushTag.getInstance().VALUE_HIGH_HUMIDITY, highHumidityCheckBox.isChecked(),
                                PushTag.getInstance().LOCAL_WARNING_INFO_HIGH_HUMIDITY, highHumidityBtn);
                        setLocalPushTag(PushTag.getInstance().VALUE_LOW_HUMIDITY, lowHumidityCheckBox.isChecked(),
                                PushTag.getInstance().LOCAL_WARNING_INFO_LOW_HUMIDITY, lowHumidityBtn);
                        setLocalPushTag(PushTag.getInstance().VALUE_HOURLY_RAINFALL, rainfallCheckBox.isChecked(),
                                PushTag.getInstance().LOCAL_WARNING_INFO_HOURLY_RAINFALL, rainfallBtn);
                        setLocalPushTag(PushTag.getInstance().VALUE_WIND_SPEED, windCheckBox.isChecked(), PushTag
                                .getInstance().LOCAL_WARNING_INFO_WIND_SPEED, windBtn);
                        setPushTag();

                    }
                    break;
            }

        }
    };


    /**
     * 对文本输入进行数值限制
     *
     * @param et
     * @param MIN_MARK 最小值
     * @param MAX_MARK 最大值
     */
    private void setRegion(final EditText et, final int MIN_MARK, final int MAX_MARK) {
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (start > 1) {
                    Log.d("onTextChanged", s.toString());
                    if (MIN_MARK != -1 && MAX_MARK != -1) {
                        int num = 0;
                        try {
                            num = Integer.parseInt(s.toString());
                        } catch (NumberFormatException e) {
                            num = MIN_MARK;
                        }

                        if (num > MAX_MARK) {
                            s = String.valueOf(MAX_MARK);
                            et.setText(s);
                        } else if (num < MIN_MARK)
                            s = String.valueOf(MIN_MARK);
                        return;
                    }
                }
                if (TextUtils.isEmpty(s)) {
                    s = String.valueOf(MIN_MARK);
                    et.setText(s);
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d("beforeTextChanged", s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null && !s.equals("")) {
                    String text = s.toString();

                    int len = s.toString().length();
                    if (len > 1) {
                        if (text.substring(0, 1).equals("0")) {// 去除数值前面输入的0
                            s.delete(0, 1);
                        }
                    }
                    if (!CommUtils.checkNum(s.toString())) {
                        et.setText(String.valueOf(MIN_MARK));
                        Log.d("afterTextChanged", et.getText().toString());
                    }
                    if (MIN_MARK != -1 && MAX_MARK != -1) {
                        int markVal = 0;
                        try {
                            markVal = Integer.parseInt(s.toString());
                        } catch (NumberFormatException e) {
                            markVal = MIN_MARK;
                        }
                        if (markVal > MAX_MARK) {
                            Toast.makeText(getBaseContext(), "数值不能高于" + MAX_MARK, Toast.LENGTH_SHORT).show();
                            et.setText(String.valueOf(MAX_MARK));
                        }
                        if (markVal < MIN_MARK) {
                            Toast.makeText(getBaseContext(), "数值不能低于" + MIN_MARK, Toast.LENGTH_SHORT).show();
                            et.setText(String.valueOf(MIN_MARK));
                        }
                        return;
                    }
                } else {
                    et.setText(String.valueOf(MIN_MARK));
                }
            }
        });
    }

    /**
     * 数据更新广播接收
     */
    private class MyReceiver extends PcsDataBrocastReceiver {

        @Override
        public void onReceive(String name, String error) {
            SetPushTagDown setPushTagDown = new SetPushTagDown();
            if (PackQueryPushTagUp.NAME.equals(name)) {
                dismissProgressDialog();
                checkNotificationPermission(AcitvityWarnLivePush.this);
                serverDataDown = (PackQueryPushTagDown) PcsDataManager.getInstance().getNetPack(name);
                if (serverDataDown == null) {
                    return;
                } else {

                    String hightTemper = serverDataDown.hashMap.get(PushTag.getInstance().VALUE_HIGH_TEMPERATURE);
                    if (TextUtils.isEmpty(hightTemper)) {
                        LocalDataHelper.savePushTag(AcitvityWarnLivePush.this, PushTag.getInstance()
                                .VALUE_HIGH_TEMPERATURE, "38");
                        LocalDataHelper.savePushTag(AcitvityWarnLivePush.this, PushTag.getInstance()
                                .LOCAL_WARNING_INFO_HIGH_TEMPERATURE, false);
                    } else {
                        LocalDataHelper.savePushTag(AcitvityWarnLivePush.this, PushTag.getInstance()
                                .VALUE_HIGH_TEMPERATURE, hightTemper);
                        LocalDataHelper.savePushTag(AcitvityWarnLivePush.this, PushTag.getInstance()
                                .LOCAL_WARNING_INFO_HIGH_TEMPERATURE, true);
                    }
                    String lowTemper = serverDataDown.hashMap.get(PushTag.getInstance().VALUE_LOW_TEMPERATURE);
                    if (TextUtils.isEmpty(lowTemper)) {
                        LocalDataHelper.savePushTag(AcitvityWarnLivePush.this, PushTag.getInstance()
                                .VALUE_LOW_TEMPERATURE, "5");
                        LocalDataHelper.savePushTag(AcitvityWarnLivePush.this, PushTag.getInstance()
                                .LOCAL_WARNING_INFO_LOW_TEMPERATURE, false);
                    } else {
                        LocalDataHelper.savePushTag(AcitvityWarnLivePush.this, PushTag.getInstance()
                                .VALUE_LOW_TEMPERATURE, lowTemper);
                        LocalDataHelper.savePushTag(AcitvityWarnLivePush.this, PushTag.getInstance()
                                .LOCAL_WARNING_INFO_LOW_TEMPERATURE, true);
                    }
                    String visibility = serverDataDown.hashMap.get(PushTag.getInstance().VALUE_VISIBILITY);
                    if (TextUtils.isEmpty(visibility)) {
                        LocalDataHelper.savePushTag(AcitvityWarnLivePush.this, PushTag.getInstance()
                                .VALUE_VISIBILITY, "200");
                        LocalDataHelper.savePushTag(AcitvityWarnLivePush.this, PushTag.getInstance()
                                .LOCAL_WARNING_INFO_VISIBILITY, false);
                    } else {
                        LocalDataHelper.savePushTag(AcitvityWarnLivePush.this, PushTag.getInstance()
                                .VALUE_VISIBILITY, visibility);
                        LocalDataHelper.savePushTag(AcitvityWarnLivePush.this, PushTag.getInstance()
                                .LOCAL_WARNING_INFO_VISIBILITY, true);
                    }
                    String highhumidity = serverDataDown.hashMap.get(PushTag.getInstance().VALUE_HIGH_HUMIDITY);
                    if (TextUtils.isEmpty(highhumidity)) {
                        LocalDataHelper.savePushTag(AcitvityWarnLivePush.this, PushTag.getInstance()
                                .VALUE_HIGH_HUMIDITY, "20");
                        LocalDataHelper.savePushTag(AcitvityWarnLivePush.this, PushTag.getInstance()
                                .LOCAL_WARNING_INFO_HIGH_HUMIDITY, false);
                    } else {
                        LocalDataHelper.savePushTag(AcitvityWarnLivePush.this, PushTag.getInstance()
                                .VALUE_HIGH_HUMIDITY, highhumidity);
                        LocalDataHelper.savePushTag(AcitvityWarnLivePush.this, PushTag.getInstance()
                                .LOCAL_WARNING_INFO_HIGH_HUMIDITY, true);
                    }
                    String lowHumidity = serverDataDown.hashMap.get(PushTag.getInstance().VALUE_LOW_HUMIDITY);
                    if (TextUtils.isEmpty(lowHumidity)) {
                        LocalDataHelper.savePushTag(AcitvityWarnLivePush.this, PushTag.getInstance()
                                .VALUE_LOW_HUMIDITY, "30");
                        LocalDataHelper.savePushTag(AcitvityWarnLivePush.this, PushTag.getInstance()
                                .LOCAL_WARNING_INFO_LOW_HUMIDITY, false);
                    } else {
                        LocalDataHelper.savePushTag(AcitvityWarnLivePush.this, PushTag.getInstance()
                                .VALUE_LOW_HUMIDITY, lowHumidity);
                        LocalDataHelper.savePushTag(AcitvityWarnLivePush.this, PushTag.getInstance()
                                .LOCAL_WARNING_INFO_LOW_HUMIDITY, true);
                    }
                    String hourlyRainfall = serverDataDown.hashMap.get(PushTag.getInstance().VALUE_HOURLY_RAINFALL);
                    if (TextUtils.isEmpty(hourlyRainfall)) {
                        LocalDataHelper.savePushTag(AcitvityWarnLivePush.this, PushTag.getInstance()
                                .VALUE_HOURLY_RAINFALL, "20");
                        LocalDataHelper.savePushTag(AcitvityWarnLivePush.this, PushTag.getInstance()
                                .LOCAL_WARNING_INFO_HOURLY_RAINFALL, false);
                    } else {
                        LocalDataHelper.savePushTag(AcitvityWarnLivePush.this, PushTag.getInstance()
                                .VALUE_HOURLY_RAINFALL, hourlyRainfall);
                        LocalDataHelper.savePushTag(AcitvityWarnLivePush.this, PushTag.getInstance()
                                .LOCAL_WARNING_INFO_HOURLY_RAINFALL, true);
                    }
                    String windSpeed = serverDataDown.hashMap.get(PushTag.getInstance().VALUE_WIND_SPEED);
                    if (TextUtils.isEmpty(windSpeed)) {
                        LocalDataHelper.savePushTag(AcitvityWarnLivePush.this, PushTag.getInstance()
                                .VALUE_WIND_SPEED, "15");
                        LocalDataHelper.savePushTag(AcitvityWarnLivePush.this, PushTag.getInstance()
                                .LOCAL_WARNING_INFO_WIND_SPEED, false);
                    } else {
                        LocalDataHelper.savePushTag(AcitvityWarnLivePush.this, PushTag.getInstance()
                                .VALUE_WIND_SPEED, windSpeed);
                        LocalDataHelper.savePushTag(AcitvityWarnLivePush.this, PushTag.getInstance()
                                .LOCAL_WARNING_INFO_WIND_SPEED, true);
                    }
                    initCheckBox();
                }
            } else if (name.contains(SetPushTagUp.NAME)) {

                setPushTagDown = (SetPushTagDown) PcsDataManager.getInstance().getNetPack(name);
                if (setPushTagDown == null) {
                    return;
                }
                Toast.makeText(AcitvityWarnLivePush.this, "提交成功", Toast.LENGTH_SHORT).show();
                if ("1".equals(setPushTagDown.result)) {
                    // 高温
                    if (name.indexOf(PushTag.getInstance().VALUE_HIGH_TEMPERATURE) != -1) {
                        saveLocalPushTag(PushTag.getInstance().VALUE_HIGH_TEMPERATURE, higtCheckBox.isChecked(),
                                PushTag.getInstance().LOCAL_WARNING_INFO_HIGH_TEMPERATURE, higtBtn);
                    }
                    // 低温
                    if (name.indexOf(PushTag.getInstance().VALUE_LOW_TEMPERATURE) != -1) {
                        saveLocalPushTag(PushTag.getInstance().VALUE_LOW_TEMPERATURE, lowtCheckBox.isChecked(),
                                PushTag.getInstance().LOCAL_WARNING_INFO_LOW_TEMPERATURE, lowtBtn);
                    }
                    // 能见度
                    if (name.indexOf(PushTag.getInstance().VALUE_VISIBILITY) != -1) {
                        saveLocalPushTag(PushTag.getInstance().VALUE_VISIBILITY, visibilityCheckBox.isChecked(),
                                PushTag.getInstance().LOCAL_WARNING_INFO_VISIBILITY, visibilityBtn);
                    }
                    // 湿度高于
                    if (name.indexOf(PushTag.getInstance().VALUE_HIGH_HUMIDITY) != -1) {
                        saveLocalPushTag(PushTag.getInstance().VALUE_HIGH_HUMIDITY, highHumidityCheckBox.isChecked(),
                                PushTag.getInstance().LOCAL_WARNING_INFO_HIGH_HUMIDITY,
                                highHumidityBtn);
                    }
                    // 湿度低于
                    if (name.indexOf(PushTag.getInstance().VALUE_LOW_HUMIDITY) != -1) {
                        saveLocalPushTag(PushTag.getInstance().VALUE_LOW_HUMIDITY, lowHumidityCheckBox.isChecked(),
                                PushTag.getInstance().LOCAL_WARNING_INFO_LOW_HUMIDITY,
                                lowHumidityBtn);
                    }
                    // 小时雨量
                    if (name.indexOf(PushTag.getInstance().VALUE_HOURLY_RAINFALL) != -1) {
                        saveLocalPushTag(PushTag.getInstance().VALUE_HOURLY_RAINFALL, rainfallCheckBox.isChecked(),
                                PushTag.getInstance().LOCAL_WARNING_INFO_HOURLY_RAINFALL,
                                rainfallBtn);
                    }
                    // 风速
                    if (name.indexOf(PushTag.getInstance().VALUE_WIND_SPEED) != -1) {
                        saveLocalPushTag(PushTag.getInstance().VALUE_WIND_SPEED, windCheckBox.isChecked(), PushTag
                                .getInstance().LOCAL_WARNING_INFO_WIND_SPEED, windBtn);
                    }
                } else {
                    // 高温
                    if (name.indexOf(PushTag.getInstance().VALUE_HIGH_TEMPERATURE) != -1) {
                        higtCheckBox.setChecked(!higtCheckBox.isChecked());
                        saveLocalPushTag(PushTag.getInstance().VALUE_HIGH_TEMPERATURE, higtCheckBox.isChecked(),
                                PushTag.getInstance().LOCAL_WARNING_INFO_HIGH_TEMPERATURE, higtBtn);
                    }
                    // 低温
                    if (name.indexOf(PushTag.getInstance().VALUE_LOW_TEMPERATURE) != -1) {
                        lowtCheckBox.setChecked(!lowtCheckBox.isChecked());
                        saveLocalPushTag(PushTag.getInstance().VALUE_LOW_TEMPERATURE, lowtCheckBox.isChecked(),
                                PushTag.getInstance().LOCAL_WARNING_INFO_LOW_TEMPERATURE, lowtBtn);
                    }
                    // 能见度
                    if (name.indexOf(PushTag.getInstance().VALUE_VISIBILITY) != -1) {
                        visibilityCheckBox.setChecked(!visibilityCheckBox.isChecked());
                        saveLocalPushTag(PushTag.getInstance().VALUE_VISIBILITY, visibilityCheckBox.isChecked(),
                                PushTag.getInstance().LOCAL_WARNING_INFO_VISIBILITY, visibilityBtn);
                    }
                    // 湿度高于
                    if (name.indexOf(PushTag.getInstance().VALUE_HIGH_HUMIDITY) != -1) {
                        highHumidityCheckBox.setChecked(!highHumidityCheckBox.isChecked());
                        saveLocalPushTag(PushTag.getInstance().VALUE_HIGH_HUMIDITY, highHumidityCheckBox.isChecked(),
                                PushTag.getInstance().LOCAL_WARNING_INFO_HIGH_HUMIDITY,
                                highHumidityBtn);

                    }
                    // 湿度低于
                    if (name.indexOf(PushTag.getInstance().VALUE_LOW_HUMIDITY) != -1) {
                        lowHumidityCheckBox.setChecked(!lowHumidityCheckBox.isChecked());
                        saveLocalPushTag(PushTag.getInstance().VALUE_LOW_HUMIDITY, lowHumidityCheckBox.isChecked(),
                                PushTag.getInstance().LOCAL_WARNING_INFO_LOW_HUMIDITY,
                                lowHumidityBtn);

                    }
                    // 小时雨量
                    if (name.indexOf(PushTag.getInstance().VALUE_HOURLY_RAINFALL) != -1) {
                        rainfallCheckBox.setChecked(!rainfallCheckBox.isChecked());
                        saveLocalPushTag(PushTag.getInstance().VALUE_HOURLY_RAINFALL, rainfallCheckBox.isChecked(),
                                PushTag.getInstance().LOCAL_WARNING_INFO_HOURLY_RAINFALL,
                                rainfallBtn);

                    }
                    // 风速
                    if (name.indexOf(PushTag.getInstance().VALUE_WIND_SPEED) != -1) {
                        windCheckBox.setChecked(!windCheckBox.isChecked());
                        saveLocalPushTag(PushTag.getInstance().VALUE_WIND_SPEED, windCheckBox.isChecked(), PushTag
                                .getInstance().LOCAL_WARNING_INFO_WIND_SPEED, windBtn);

                    }
                }
                if (isBackCommit){
                    dismissProgressDialog();
                    AcitvityWarnLivePush.this.finish();
                }else{
                    checkDataServer(token);
                }
            }
        }
    }

    /**
     * @param title      标题
     * @param unit       单位
     * @param mValue     默认值
     * @param minValue   最小值
     * @param maxValue   最大值
     * @param isNegative 是否负数(SeekBar只支持非负数)
     */
    private void showSelectDialog(final String title, final String unit, final String mValue, final int minValue,
                                  final int maxValue, final String type, final boolean isNegative) {

        Intent intent = new Intent(AcitvityWarnLivePush.this, ActivitySeekBarDialog.class);
        intent.putExtra("type", type); // 类型
        intent.putExtra("title", title); // 标题
        intent.putExtra("unit", unit); // 单位
        intent.putExtra("mValue", mValue); // 默认值
        intent.putExtra("minValue", minValue); // 最小值
        intent.putExtra("maxValue", maxValue); // 最大值
        intent.putExtra("isNegative", isNegative); // 是否负数
        startActivityForResult(intent, MyConfigure.RESULT_WARN_LIVE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 实况告警阈值区间：
            // 温度：-50~50
            // 能见度：0~100000；
            // 湿度：0~100；
            // 小时雨量：0~200
            // 风速：0~100
            case R.id.higt_btn:// 高温
                showSelectDialog("气温高于: ", " °C", higtBtn.getText().toString(), -50, 50, "higt", true);
                break;
            case R.id.lowt_btn:// 低温
                showSelectDialog("气温低于: ", " °C", lowtBtn.getText().toString(), -50, 50, "lowt", true);
                break;
            case R.id.visibility_btn:// 能见度
                showSelectDialog("能见度低于: ", " m", visibilityBtn.getText().toString(), 0, 5000, "visibility", false);
                break;
            case R.id.low_humidity_btn:// 湿度
                showSelectDialog("湿度低于: ", " %", lowHumidityBtn.getText().toString(), 0, 100, "humidity", false);
                break;
            case R.id.rainfall_btn:// 小时雨量
                showSelectDialog("小时雨量高于: ", " mm", rainfallBtn.getText().toString(), 0, 100, "rainfall", false);

                break;
            case R.id.wind_btn:// 风速
                showSelectDialog("风速高于: ", " m/s", windBtn.getText().toString(), 0, 100, "wind", false);
                break;
            default:
                break;
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent mData) {
        switch (requestCode) {
            case MyConfigure.RESULT_WARN_LIVE://
                if (resultCode == Activity.RESULT_OK) {
                    System.out.println("返回");
                    boolean flag = mData.getBooleanExtra("flag", false);

                    if (flag) {
                        String type = mData.getStringExtra("type");
                        String value = mData.getStringExtra("value");
                        if ("higt".equals(type)) {
                            higtBtn.setText(value);
                        } else if ("lowt".equals(type)) {
                            lowtBtn.setText(value);
                        } else if ("visibility".equals(type)) {
                            visibilityBtn.setText(value);
                        } else if ("humidity".equals(type)) {
                            lowHumidityBtn.setText(value);
                        } else if ("rainfall".equals(type)) {
                            rainfallBtn.setText(value);
                        } else if ("wind".equals(type)) {
                            windBtn.setText(value);
                        }
                        System.out.println("类型" + type + ":" + value);
                    }
                }
                break;
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(receiver);
    }

}
