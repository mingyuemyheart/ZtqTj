package com.pcs.ztqtj.view.activity.photoshow;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.services.geocoder.RegeocodeAddress;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.KWHttpRequest;
import com.pcs.ztqtj.control.tool.ZtqLocationTool;
import com.pcs.ztqtj.model.PhotoShowDB;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
import com.pcs.ztqtj.view.dialog.DialogFactory.DialogListener;
import com.pcs.ztqtj.view.dialog.DialogTwoButton;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib.lib_pcs_v3.model.image.ImageResizer;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCity;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityLocation;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityMain;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalPhotoUser;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalUrl;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackInitDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackInitUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.photowall.PackPhotoSubmitUp;

import java.io.File;

/**
 * 实景提交页面
 *
 * @author JiangZy
 */
public class ActivityPhotoSubmit extends FragmentActivityZtqBase {
    private int REQUEST_CODE_PHOTO = 101;
    /**
     * 图片文件
     */
    private File mFilePhoto = null;
    /**
     * 复选框选中事件
     */
    private OnMyCheckedChange mOnChecked;
    /**
     * 上传包
     */
    private PackPhotoSubmitUp mPackUp = new PackPhotoSubmitUp();
    /**
     * 上传数据+文件
     */
    private KWHttpRequest mKWHttpRequest;
    /**
     * 对话框：回退
     */
    private Dialog mDialogBack;
    /**
     * 对话框：日期
     */
    private DatePickerDialog mDialogDate;
    /**
     * 照片的Bitmap
     */
    private Bitmap mBitmapPhoto;
    private ImageResizer mResizer = new ImageResizer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_submit);
        setTitleText(R.string.photo_title_submit);
        // 等待框
        showProgressDialog();
        // 初始化位置
        initAddress();
        // 初始化图片
        initPicture();
        // 刷新图片
        refreshPicture();
        // 初始化单选按钮组
        initRadioGroup();
        // 初始化按钮
        initButton();
        // 初始化描述
        initDesc();
        // 初始化HTTP请求
        initKWHttpRequest();
        // 初始化回退对话框
        initDialogBack();
        // 初始化时间
        initTime();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 初始化用户信息
        initUserInfo();
        // 取消等待框
        dismissProgressDialog();
    }

    @Override
    public void onBackPressed() {
        mDialogBack.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        // 刷新图片
        refreshPicture();
    }

    @Override
    protected void createImageFetcher() {

    }

    @Override
    protected void onDestroy() {
        if (mBitmapPhoto != null && !mBitmapPhoto.isRecycled()) {
            mBitmapPhoto.recycle();
        }
        super.onDestroy();
        if (mKWHttpRequest != null) {
            mKWHttpRequest.setListener(0, null);
        }
    }

    /**
     * 初始化位置
     */
    private void initAddress() {
        EditText editText = (EditText) findViewById(R.id.edit_address);
        RegeocodeAddress regeocodeAddress = ZtqLocationTool.getInstance()
                .getSearchAddress();
        if (regeocodeAddress == null) {
            // 没有定位信息，显示首页城市
            PackLocalCityMain cityMain = ZtqCityDB.getInstance().getCityMain();
            editText.setText(cityMain.NAME);
            return;
        } else {
            // 显示地名
            editText.setText(regeocodeAddress.getFormatAddress());
        }
    }

    /**
     * 初始化图片
     */
    private void initPicture() {
        // 图片
        String photoPath = getIntent().getStringExtra("photo_path");
        mFilePhoto = new File(photoPath);
        if (!checkPhotoExists()) {
            return;
        }

        ImageView imageView = (ImageView) findViewById(R.id.imagePhoto);
        imageView.setOnClickListener(mOnClick);
    }

    /**
     * 刷新图片
     */
    private void refreshPicture() {
        if (mBitmapPhoto != null && !mBitmapPhoto.isRecycled()) {
            mBitmapPhoto.recycle();
        }

        Options options = new Options();
        options.inSampleSize = 6;

        mBitmapPhoto = BitmapFactory.decodeFile(mFilePhoto.getPath(), options);
        ImageView imageView = (ImageView) findViewById(R.id.imagePhoto);
        imageView.setImageBitmap(mBitmapPhoto);
    }

    /**
     * 初始化单选按钮组
     */
    private void initRadioGroup() {
        mOnChecked = new OnMyCheckedChange();

        RadioGroup radioGroup = null;
        radioGroup = (RadioGroup) findViewById(R.id.radio_group_1);
        radioGroup.setOnCheckedChangeListener(mOnChecked);
        if(radioGroup.getChildCount() > 0) {
            View view = radioGroup.getChildAt(0);
            if(view != null) {
                radioGroup.check(view.getId());
            }
        }
        radioGroup = (RadioGroup) findViewById(R.id.radio_group_2);
        radioGroup.setOnCheckedChangeListener(mOnChecked);
        radioGroup = (RadioGroup) findViewById(R.id.radio_group_3);
        radioGroup.setOnCheckedChangeListener(mOnChecked);
        radioGroup = (RadioGroup) findViewById(R.id.radio_group_4);
        radioGroup.setOnCheckedChangeListener(mOnChecked);

    }

    /**
     * 初始化按钮
     */
    private void initButton() {
        View btn = null;
        // 地址
//        btn = findViewById(R.id.image_address);
//        btn.setOnClickListener(mOnClick);
        // 时间
//        btn = findViewById(R.id.image_time);
//        btn.setOnClickListener(mOnClick);
        // 登陆
        btn = findViewById(R.id.btn_login);
        btn.setOnClickListener(mOnClick);
        // 发布
        btn = findViewById(R.id.btn_submit);
        btn.setOnClickListener(mOnClick);
        // 回退
        btn = findViewById(R.id.btn_back);
        btn.setOnClickListener(mOnClick);
        // 左旋转
        btn = findViewById(R.id.btn_rotate_left);
        btn.setOnClickListener(mOnClick);
        // 右旋转
        btn = findViewById(R.id.btn_rotate_right);
        btn.setOnClickListener(mOnClick);
    }

    /**
     * 初始化描述
     */
    private void initDesc() {
        EditText editText = (EditText) findViewById(R.id.edit_desc);
        editText.addTextChangedListener(mTextWatcher);
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
    }

    /**
     * 初始化用户信息
     */
    private void initUserInfo() {
        PackLocalPhotoUser packUser = PhotoShowDB.getInstance().getUserPack();
        // 用户名
        TextView textView = (TextView) findViewById(R.id.text_name);
        textView.setText(packUser.nickName);
        // 登陆按钮
        View btn = findViewById(R.id.btn_login);
        if (TextUtils.isEmpty(packUser.userId)) {
            btn.setVisibility(View.VISIBLE);
        } else {
            btn.setVisibility(View.GONE);
        }
    }

    /**
     * 初始化回退对话框
     */
    private void initDialogBack() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_message,
                null);
        TextView tv = (TextView) view.findViewById(R.id.dialogmessage);
        tv.setText("是否放弃发布?");
        mDialogBack = new DialogTwoButton(this, view, "确定", "返回",
                new DialogListener() {
                    @Override
                    public void click(String str) {
                        mDialogBack.dismiss();
                        if (str.equals("确定")) {
                            finish();
                        }
                    }
                });
    }

    /**
     * 初始化时间
     */
    private void initTime() {
        Time time = new Time();
        time.setToNow();
        // 显示时间
        TextView textView = (TextView) findViewById(R.id.text_time);
        textView.setText(time.format("%Y-%m-%d %H:%M:%S"));
        // 对话框
        mDialogDate = new DatePickerDialog(this, mOnDate, time.year,
                time.month, time.monthDay);
    }

    /**
     * 点击照片
     */
    private void clickPhoto() {
        showProgressDialog();
        Intent intent = new Intent();
        intent.setClass(this, ActivityPhotoFullSubmit.class);
        intent.putExtra("path", mFilePhoto.getPath());
        startActivityForResult(intent, REQUEST_CODE_PHOTO);
    }

    /**
     * 点击地址
     */
    private void clickBtnAddr() {
        EditText editText = (EditText) findViewById(R.id.edit_address);
        editText.selectAll();

        // 显示键盘
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, 0);
    }

    /**
     * 点击时间
     */
    private void clickBtnTime() {
        mDialogDate.show();
    }

    /**
     * 点击登陆
     */
    private void clickBtnLogin() {
        Intent intent = new Intent();
        intent.setClass(this, ActivityPhotoLogin.class);
        startActivity(intent);
    }

    /**
     * 点击发布
     */
    private void clickBtnSubmit() {
        if (!checkPhotoExists()) {
            return;
        }
        Button btn = (Button) findViewById(R.id.btn_submit);
        btn.setClickable(false);
        // // 等待框
        // showProgressDialog();

        EditText editText;
        TextView textView;

        // 天气
        mPackUp.weather = mOnChecked.getCurrValue();
        // 位置
        editText = (EditText) findViewById(R.id.edit_address);
        mPackUp.address = editText.getText().toString();
        // 时间
        textView = (TextView) findViewById(R.id.text_time);
        mPackUp.dateTime = textView.getText().toString();
        // 用户ID
        mPackUp.userId = PhotoShowDB.getInstance().getUserPack().userId;
        // 描述
        editText = (EditText) findViewById(R.id.edit_desc);
        mPackUp.des = editText.getText().toString();
        RegeocodeAddress regeocodeAddress = ZtqLocationTool.getInstance().getSearchAddress();
        if (regeocodeAddress != null) {
            String name = regeocodeAddress.getCity();
            if (!TextUtils.isEmpty(name)) {
                PackLocalCity currentCityInfo = ZtqCityDB.getInstance().getCityInfoInAllCity(name);
                if (currentCityInfo != null) {
                    mPackUp.areaId = currentCityInfo.ID;
                }
            }
        }
        if (TextUtils.isEmpty(mPackUp.areaId)) {
            // 地区ID
            PackLocalCityLocation location = ZtqLocationTool.getInstance().getLocationCity();
            if(location != null) {
                mPackUp.areaId = location.ID;
            }
            if(TextUtils.isEmpty(mPackUp.areaId)) {
                mPackUp.areaId = PhotoShowDB.getInstance().getCityId();
            }
        }

        // 请求网络
        mKWHttpRequest.setFilePath(mFilePhoto.getPath(), KWHttpRequest.FILETYPE.IMG);
        mKWHttpRequest.addDownload(mPackUp);
        mKWHttpRequest.startAsynchronous();
        // 提示成功
        tipRequestSucc();
    }

    /**
     * 检查有没照片并提示
     *
     * @return
     */
    private boolean checkPhotoExists() {
        if (mFilePhoto == null || !mFilePhoto.exists()) {
            Toast.makeText(this, R.string.picture_not_exists,
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    /**
     * 提示请求成功
     */
    private void tipRequestSucc() {
        dismissProgressDialog();
        // 提示
        Toast.makeText(getApplicationContext(), R.string.photo_up_succ,
                Toast.LENGTH_SHORT).show();
        this.finish();
    }

    /**
     * 单选按钮选中事件
     */
    private class OnMyCheckedChange implements OnCheckedChangeListener {
        // 当前值
        private String mCurrValue = "";
        // 正在运行
        private boolean isRunning = false;

        /**
         * 获取单选按钮的值
         *
         * @return
         */
        private String getRadioValue(RadioGroup radioGroup) {
            int rID = radioGroup.getCheckedRadioButtonId();
            RadioButton radio = (RadioButton) findViewById(rID);
            if(radio == null) {
                return "晴好";
            } else {
                return radio.getText().toString();
            }
        }

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (isRunning) {
                return;
            }
            isRunning = true;

            RadioGroup radioGroup1 = (RadioGroup) findViewById(R.id.radio_group_1);
            RadioGroup radioGroup2 = (RadioGroup) findViewById(R.id.radio_group_2);
            RadioGroup radioGroup3 = (RadioGroup) findViewById(R.id.radio_group_3);
            RadioGroup radioGroup4 = (RadioGroup) findViewById(R.id.radio_group_4);
            switch (group.getId()) {
                case R.id.radio_group_1:
                    mCurrValue = getRadioValue(radioGroup1);
                    radioGroup2.clearCheck();
                    radioGroup3.clearCheck();
                    radioGroup4.clearCheck();
                    break;
                case R.id.radio_group_2:
                    mCurrValue = getRadioValue(radioGroup2);
                    radioGroup1.clearCheck();
                    radioGroup3.clearCheck();
                    radioGroup4.clearCheck();
                    break;
                case R.id.radio_group_3:
                    mCurrValue = getRadioValue(radioGroup3);
                    radioGroup1.clearCheck();
                    radioGroup2.clearCheck();
                    radioGroup4.clearCheck();
                    break;
                case R.id.radio_group_4:
                    mCurrValue = getRadioValue(radioGroup4);
                    radioGroup1.clearCheck();
                    radioGroup2.clearCheck();
                    radioGroup3.clearCheck();
                    break;
            }

            isRunning = false;
        }

        /**
         * 获取当前值
         *
         * @return
         */
        public String getCurrValue() {
            if (TextUtils.isEmpty(mCurrValue)) {
                RadioGroup radioGroup1 = (RadioGroup) findViewById(R.id.radio_group_1);
                mCurrValue = getRadioValue(radioGroup1);
            }

            return mCurrValue;
        }
    }

    /**
     * 点击事件
     */
    private OnClickListener mOnClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.imagePhoto:
                    // 点击照片
                    clickPhoto();
                    break;
//                case R.id.image_address:
//                    // 地址
//                    clickBtnAddr();
//                    break;
//                case R.id.image_time:
//                    // 时间
//                    clickBtnTime();
//                    break;
                case R.id.btn_login:
                    clickBtnLogin();
                    // 登陆
                    break;
                case R.id.btn_submit:
                    // 发布
                    Button btn = (Button) findViewById(R.id.btn_submit);
                    if (btn.isClickable()) {
                        clickBtnSubmit();
                    } else {
                        showToast("正在发布");
                    }
                    break;
                case R.id.btn_back:
                    mDialogBack.show();
                    break;
                case R.id.btn_rotate_left:
                    // 左旋转
                    showProgressDialog();
                    mResizer.rotateSD(mFilePhoto.getPath(), -90, mImageListener);
                    break;
                case R.id.btn_rotate_right:
                    // 右旋转
                    showProgressDialog();
                    mResizer.rotateSD(mFilePhoto.getPath(), 90, mImageListener);
                    break;
            }
        }
    };

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            // 显示字数
            TextView text = (TextView) findViewById(R.id.text_tips);
            text.setText(String.valueOf(s.length()));
        }
    };

    /**
     * 日期选择回调
     */
    private OnDateSetListener mOnDate = new OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            String str = String.valueOf(year) + "-"
                    + String.valueOf(monthOfYear + 1) + "-"
                    + String.valueOf(dayOfMonth);
            TextView textView = (TextView) findViewById(R.id.text_time);
            textView.setText(str);
        }
    };

    /**
     * 图片修改监听
     */
    private ImageResizer.ImageResizerListener mImageListener = new ImageResizer.ImageResizerListener() {

        @Override
        public void doneSD(String path, boolean isSucc) {
            if (isSucc) {
                refreshPicture();
            }

            dismissProgressDialog();
        }
    };
}
