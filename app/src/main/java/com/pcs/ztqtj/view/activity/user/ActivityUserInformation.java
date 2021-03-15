package com.pcs.ztqtj.view.activity.user;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pcs.lib.lib_pcs_v3.control.file.PcsGetPathValue;
import com.pcs.lib.lib_pcs_v3.control.tool.BitmapUtil;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityLocation;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityMain;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalUrl;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalUser;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalUserInfo;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackInitDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackInitUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.photowall.PackPhotoChangeUserInfoDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.photowall.PackPhotoChangeUserInfoUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.photowall.PackPhotoUserInfoDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.photowall.PackPhotoUserInfoUp;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.CommUtils;
import com.pcs.ztqtj.control.tool.KWHttpRequest;
import com.pcs.ztqtj.control.tool.MyConfigure;
import com.pcs.ztqtj.control.tool.PermissionsTools;
import com.pcs.ztqtj.control.tool.ZtqLocationTool;
import com.pcs.ztqtj.control.tool.image.GetImageView;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
import com.pcs.ztqtj.view.activity.photoshow.ActivityPhotoPasswordManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 个人中心个人信息页面
 * Created by tyaathome on 2016/9/8.
 */
public class ActivityUserInformation extends FragmentActivityZtqBase implements View.OnClickListener {

    private GetImageView getImageView = new GetImageView();
    private ImageView ivHead;
    private EditText etName;
    private Button btnSubmit;
    private TextView tvManager;
    private EditText etPhone;
    private RadioGroup radioGroup;
    private EditText etAddress;
    private ImageView ivEditHead;
    private ImageView ivEditUserName;
    private RelativeLayout rlHead;
    // 弹出框
    private PopupWindow mPopupWindow;
    // 图片文件
    private File mFilePhoto = null;

    private MyReceiver receiver = new MyReceiver();

    /**
     * 上传数据+文件
     */
    private KWHttpRequest mKWHttpRequest;

    /**
     * 性别
     */
    private String mGender = "";

    /**
     * 地址
     */
    private String mAddress = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_information);
        setTitleText(R.string.user_information);
        initView();
        initEvent();
        initData();
        etName.setSelection(etName.getText().length());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case MyConfigure.REQUEST_ALBUM:
                // 相册
                resultAlbum(intent);
                break;
            case MyConfigure.REQUEST_CAMERA:
                // 相机
                resultCamera(intent);
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        CommUtils.closeKeyboard(this);
    }

    private void initView() {
        ivHead = (ImageView) findViewById(R.id.iv_head);
        etName = (EditText) findViewById(R.id.et_name);
        btnSubmit = (Button) findViewById(R.id.btn_submit);
        tvManager = (TextView) findViewById(R.id.tv_password_manager);
        etPhone = (EditText) findViewById(R.id.et_phone);
        radioGroup = (RadioGroup) findViewById(R.id.radio_group);
        etAddress = (EditText) findViewById(R.id.et_address);
        ivEditHead = (ImageView) findViewById(R.id.iv_edit_head);
        ivEditUserName = (ImageView) findViewById(R.id.iv_edit_username);
        PackLocalUser localUser = ZtqCityDB.getInstance().getMyInfo();
        getImageView.setImageView(this, localUser.sys_head_url, ivHead);
        initPopupWindow();
    }

    private void initEvent() {
        ivHead.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        tvManager.setOnClickListener(this);
        ivEditHead.setOnClickListener(this);
        ivEditUserName.setOnClickListener(this);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_male:
                        mGender = "1";
                        break;
                    case R.id.rb_female:
                        mGender = "2";
                        break;
                }
            }
        });
    }

    private void initData() {
        PcsDataBrocastReceiver.registerReceiver(this, receiver);
        initKWHttpRequest();
        //getImageView.setImageView(this, LoginInformation.getInstance().getUserIconUrl(), ivHead);
        etName.setText(ZtqCityDB.getInstance().getMyInfo().sys_nick_name);

        reqInformation();
        // 刷新界面
        updateUI();
    }

    /**
     * 初始化弹出框
     */
    private void initPopupWindow() {
        View view = LayoutInflater.from(this).inflate(R.layout.pop_photograph,
                null);
        mPopupWindow = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                // 设置背景颜色变暗
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1.0f;
                getWindow().setAttributes(lp);
            }
        });
        // 设置可以获得焦点
        mPopupWindow.setFocusable(true);
        // 设置弹窗内可点击
        mPopupWindow.setTouchable(true);
        // 设置弹窗外可点击
        mPopupWindow.setOutsideTouchable(true);

        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());

        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dismissPopupWindow();
                    return true;
                }
                return false;
            }
        });
        View btn;
        // 相机按钮
        btn = view.findViewById(R.id.btnCamera);
        btn.setOnClickListener(mOnClick);
        // 相册按钮
        btn = view.findViewById(R.id.btnAlbum);
        btn.setOnClickListener(mOnClick);
        // 取消按钮
        btn = view.findViewById(R.id.btnCancel);
        btn.setOnClickListener(mOnClick);
    }

    /**
     * 关闭弹出框
     */
    private void dismissPopupWindow() {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
    }

    /**
     * 显示键盘
     */
    private void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    /**
     * 显示弹出框
     */
    private void showPopupWindow() {
        if (mPopupWindow != null && !mPopupWindow.isShowing()) {
            View layout = findViewById(R.id.layout);
            mPopupWindow.showAtLocation(layout, Gravity.BOTTOM, 0, 0);
        }
    }

    private void updateUI() {
        // 本地登录
        if (ZtqCityDB.getInstance().getMyInfo().type.equals("4")) {
            etName.setEnabled(true);
            ivHead.setEnabled(true);
            findViewById(R.id.iv_edit_head).setVisibility(View.VISIBLE);
            findViewById(R.id.iv_edit_username).setVisibility(View.VISIBLE);
            tvManager.setVisibility(View.VISIBLE);
        } else { // 第三方登录
            etName.setEnabled(false);
            ivHead.setEnabled(false);
            findViewById(R.id.iv_edit_head).setVisibility(View.GONE);
            findViewById(R.id.iv_edit_username).setVisibility(View.GONE);
            tvManager.setVisibility(View.GONE);
        }
    }

    private View.OnClickListener mOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnCamera:
                    // 点击相机
                    clickCamera();
                    break;
                case R.id.btnAlbum:
                    // 点击相册
                    clickAlbum();
                    break;
                case R.id.btnCancel:
                    // 点击取消
                    clickCancel();
                    break;
            }
        }
    };

    /**
     * 点击照相
     */
    private void clickCamera() {
        dismissPopupWindow();

        String tempStr = String.valueOf(System.currentTimeMillis());
        mFilePhoto = new File(PcsGetPathValue.getInstance().getMyPhotoPath()
                + tempStr + ".jpg");
        mFilePhoto.getParentFile().mkdirs();
        CommUtils.openCamera(this, mFilePhoto, MyConfigure.REQUEST_CAMERA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MyConfigure.REQUEST_CAMERA) {
            PermissionsTools.onRequestPermissionsResult(this, permissions, grantResults,
                    new PermissionsTools.RequestPermissionResultCallback() {

                @Override
                public void onSuccess() {
                    clickCamera();
                }

                @Override
                public void onDeny() {

                }

                @Override
                public void onDenyNeverAsk() {

                }
            });
        }
    }

    /**
     * 点击相册
     */
    private void clickAlbum() {
        dismissPopupWindow();
        Intent it = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        this.startActivityForResult(it, MyConfigure.REQUEST_ALBUM);
    }

    /**
     * 点击取消
     */
    private void clickCancel() {
        dismissPopupWindow();
    }

    /**
     * 从相册返回
     *
     * @param fromIntent
     */
    private void resultAlbum(Intent fromIntent) {
        Uri uri = fromIntent.getData();
        String[] filePathColumns = {MediaStore.Images.Media.DATA};
        Cursor c = this.getContentResolver().query(uri, filePathColumns, null,
                null, null);
        if (c == null) {
            return;
        }
        c.moveToFirst();
        int columnIndex = c.getColumnIndex(filePathColumns[0]);
        String picturePath = c.getString(columnIndex);
        c.close();
        // ---------拷贝文件
        // 旧文件
        File oldFile = new File(picturePath);
        // 新文件
        mFilePhoto = new File(PcsGetPathValue.getInstance().getMyPhotoPath()
                + oldFile.getName());
        if (mFilePhoto.exists()) {
            mFilePhoto.delete();
        }
        mFilePhoto.getParentFile().mkdirs();
        if (!copyFile(oldFile, mFilePhoto)) {
            Toast.makeText(this, R.string.photo_error, Toast.LENGTH_SHORT)
                    .show();
            dismissProgressDialog();
            return;
        }

        Bitmap bitmap = BitmapFactory.decodeFile(mFilePhoto.getPath());
        Bitmap result = BitmapUtil.toRoundBitmap(bitmap);
        ivHead.setImageBitmap(result);
    }

    /**
     * 从相机返回
     */
    private void resultCamera(Intent fromIntent) {
        if (mFilePhoto == null || !mFilePhoto.exists()) {
            Toast.makeText(this, R.string.photo_error, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        Bitmap bitmap = BitmapFactory.decodeFile(mFilePhoto.getPath());
        Bitmap result = BitmapUtil.toRoundBitmap(bitmap);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        result.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] bitmapdata = bos.toByteArray();
        try {
            FileOutputStream fos = new FileOutputStream(mFilePhoto);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ivHead.setImageBitmap(result);
    }

    /**
     * 拷贝文件
     *
     * @param oldFile
     * @param newFile
     */
    private boolean copyFile(File oldFile, File newFile) {
        if (!oldFile.exists()) {
            return false;
        }
        newFile.getParentFile().mkdirs();
        InputStream inStream = null;
        FileOutputStream fs = null;
        int byteread = 0;
        try {
            inStream = new FileInputStream(oldFile); // 读入原文件
            fs = new FileOutputStream(newFile);
            byte[] buffer = new byte[1444];
            while ((byteread = inStream.read(buffer)) != -1) {
                fs.write(buffer, 0, byteread);
            }

            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inStream.close();
                fs.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 初始化HTTP请求
     */
    private void initKWHttpRequest() {
        PackLocalUrl packUrl = (PackLocalUrl) PcsDataManager.getInstance().getLocalPack(PackLocalUrl.KEY);
        PackInitUp initUp = new PackInitUp();
        PackInitDown packInit = (PackInitDown) PcsDataManager.getInstance().getNetPack(initUp.getName());
        mKWHttpRequest = new KWHttpRequest(this);
        mKWHttpRequest.setURL(packUrl.url);
        mKWHttpRequest.setmP(packInit.pid);
        mKWHttpRequest.setListener(0, new KWHttpRequest.KwHttpRequestListener() {
            @Override
            public void loadFinished(int nThreadID, byte[] b) {

            }

            @Override
            public void loadFailed(int nThreadID, int nErrorCode) {

            }
        });
    }

    private void reqInformation() {

        if (!isOpenNet()) {
            showToast(getString(R.string.net_err));
            return;
        }
        showProgressDialog();
        PackPhotoUserInfoUp packUp = new PackPhotoUserInfoUp();
        packUp.user_id = ZtqCityDB.getInstance().getMyInfo().sys_user_id;
        PcsDataDownload.addDownload(packUp);
    }

    private void reqChangeInfo() {

        if (!isOpenNet()) {
            showToast(getString(R.string.net_err));
            return;
        }
        showProgressDialog();
        String type = ZtqCityDB.getInstance().getMyInfo().type;
        PackPhotoChangeUserInfoUp packUp = new PackPhotoChangeUserInfoUp();
        packUp.user_id = ZtqCityDB.getInstance().getMyInfo().sys_user_id;
        if (type.equals("4")) {
            packUp.nick_name = etName.getText().toString();
        }
        packUp.sex = mGender;
        packUp.address = etAddress.getText().toString();
        packUp.mobile = etPhone.getText().toString();
        // 请求网络
        if (mFilePhoto != null) {
            mKWHttpRequest.setFilePath(mFilePhoto.getPath(), KWHttpRequest.FILETYPE.IMG);
            mKWHttpRequest.addDownload(packUp);
            mKWHttpRequest.startAsynchronous();
        } else {
            PcsDataDownload.addDownload(packUp);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            PcsDataBrocastReceiver.unregisterReceiver(this, receiver);
            receiver = null;
        }
        if (mKWHttpRequest != null) {
            mKWHttpRequest.setListener(0, null);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:
                if (check()) {
                    reqChangeInfo();
                }

                break;
            case R.id.tv_password_manager:
                Intent intent = new Intent(ActivityUserInformation.this, ActivityPhotoPasswordManager.class);
                intent.putExtra("type", 3);
                startActivity(intent);
                break;
            case R.id.iv_edit_head:
            case R.id.iv_head:
                //CommUtils.closeKeyboard(ActivityUserInformation.this);
                CommUtils.closeKeyboard(this);
                showPopupWindow();
                break;
            case R.id.iv_edit_username:
                etName.setSelection(etName.getText().length());
                etName.requestFocus();
                showKeyboard();
                break;
        }
    }

    /**
     * 监测提交参数正确性
     *
     * @return
     */
    private boolean check() {
        String name = etName.getText().toString();
        String address = etAddress.getText().toString();
        String mobile = etPhone.getText().toString();
        int id = radioGroup.getCheckedRadioButtonId();
        if (TextUtils.isEmpty(name)) {
            showToast("昵称不能为空");
            return false;
        }
        if (TextUtils.isEmpty(mobile)) {
            showToast("绑定手机号不能为空");
            return false;
        }
        if (mobile.length() > 11) {
            showToast("请正确填写手机号码");
            return false;
        }
        if (TextUtils.isEmpty(address)) {
            showToast("地址不能为空");
            return false;
        }
        if (id != R.id.rb_male && id != R.id.rb_female) {
            showToast("请选择性别");
            return false;
        }
        return true;
    }

    private class MyReceiver extends PcsDataBrocastReceiver {

        @Override
        public void onReceive(String nameStr, String errorStr) {
            if (PackPhotoUserInfoUp.NAME.equals(nameStr)) {
                dismissProgressDialog();
                PackPhotoUserInfoDown down = (PackPhotoUserInfoDown) PcsDataManager.getInstance().getNetPack(nameStr);
                if (down == null) {
                    return;
                }

                PackLocalUser myUserInfo = ZtqCityDB.getInstance().getMyInfo();
                myUserInfo.user_id = ZtqCityDB.getInstance().getMyInfo().user_id;
                myUserInfo.sys_user_id = down.user_id;
                myUserInfo.sys_nick_name = down.nick_name;
                myUserInfo.sys_head_url = down.head_url;
                myUserInfo.mobile = down.mobile;
                myUserInfo.type = down.platform_type;

                PackLocalUserInfo packLocalUserInfo = new PackLocalUserInfo();
                packLocalUserInfo.currUserInfo = myUserInfo;
                ZtqCityDB.getInstance().setMyInfo(packLocalUserInfo);

                // 设置头像
                getImageView.setImageView(ActivityUserInformation.this, down.head_url, ivHead);
                // 设置电话号码
                etPhone.setText(down.mobile);
                //设置性别
                if (down.sex.equals("2")) {
                    radioGroup.check(R.id.rb_female);
                } else {
                    radioGroup.check(R.id.rb_male);
                }
                // 设置地址
                if (TextUtils.isEmpty(down.address)) {

                    PackLocalCityLocation packLocation = ZtqLocationTool.getInstance().getLocationCity();
                    // 先判断定位城市
                    if (!packLocation.NAME.equals(PackLocalCityLocation.LOCATING) && ZtqLocationTool.getInstance().getIsAutoLocation()) {
                        mAddress = packLocation.NAME;
                    } else { // 无定位城市时，使用当前城市
                        PackLocalCityMain cityMain = ZtqCityDB.getInstance().getCityMain();
                        if (cityMain != null) {
                            mAddress = cityMain.NAME;
                        } else {
                            mAddress = "";
                        }
                    }
                } else {
                    mAddress = down.address;
                }
                etAddress.setText(mAddress);
            }

            // 更新用户信息
            if (PackPhotoChangeUserInfoUp.NAME.equals(nameStr)) {
                dismissProgressDialog();
                PackPhotoChangeUserInfoDown down =
                        (PackPhotoChangeUserInfoDown) PcsDataManager.getInstance().getNetPack(nameStr);
                if (down == null) {
                    return;
                }

//                if(down.result.equals("1")) {
//                    ActivityUserInformation.this.finish();
//                }
                if (down.result.equals("1")) {
                    reqInformation();
                }
                showToast(down.result_msg);
            }
        }
    }
}
