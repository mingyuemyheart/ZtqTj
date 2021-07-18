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
import android.util.Log;
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
import com.pcs.lib.lib_pcs_v3.model.image.ImageResizer;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCity;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityMain;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalPhotoUser;
import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.ZtqLocationTool;
import com.pcs.ztqtj.model.PhotoShowDB;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.util.OkHttpUtil;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
import com.pcs.ztqtj.view.dialog.DialogFactory.DialogListener;
import com.pcs.ztqtj.view.dialog.DialogTwoButton;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 实景提交页面
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

    private ImageView imagePhoto;
    private EditText edit_address,edit_desc;
    private TextView text_time;
    private Button btn_submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_submit);
        initWidget();
        // 等待框
        showProgressDialog();
        // 初始化位置
        initAddress();
        // 初始化单选按钮组
        initRadioGroup();
        // 初始化回退对话框
        initDialogBack();
    }

    private void initWidget() {
        setTitleText(R.string.photo_title_submit);

        // 图片
        String photoPath = getIntent().getStringExtra("photo_path");
        mFilePhoto = new File(photoPath);
        if (!checkPhotoExists()) {
            return;
        }

        imagePhoto = findViewById(R.id.imagePhoto);
        imagePhoto.setOnClickListener(mOnClick);
        refreshPicture();

        View btn = null;
        // 登陆
        btn = findViewById(R.id.btn_login);
        btn.setOnClickListener(mOnClick);
        // 发布
        btn_submit = findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(mOnClick);
        // 回退
        btn = findViewById(R.id.btn_back);
        btn.setOnClickListener(mOnClick);
        // 左旋转
        btn = findViewById(R.id.btn_rotate_left);
        btn.setOnClickListener(mOnClick);
        // 右旋转
        btn = findViewById(R.id.btn_rotate_right);
        btn.setOnClickListener(mOnClick);

        edit_address = findViewById(R.id.edit_address);
        edit_desc = findViewById(R.id.edit_desc);
        edit_desc.addTextChangedListener(mTextWatcher);

        Time time = new Time();
        time.setToNow();
        // 显示时间
        text_time = findViewById(R.id.text_time);
        text_time.setOnClickListener(mOnClick);
        text_time.setText(time.format("%Y-%m-%d %H:%M:%S"));
        // 对话框
        mDialogDate = new DatePickerDialog(this, mOnDate, time.year, time.month, time.monthDay);
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
        imagePhoto.setImageBitmap(mBitmapPhoto);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
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
    }

    /**
     * 初始化位置
     */
    private void initAddress() {
        RegeocodeAddress regeocodeAddress = ZtqLocationTool.getInstance().getSearchAddress();
        if (regeocodeAddress == null) {
            // 没有定位信息，显示首页城市
            PackLocalCityMain cityMain = ZtqCityDB.getInstance().getCityMain();
            edit_address.setText(cityMain.NAME);
            return;
        } else {
            // 显示地名
            edit_address.setText(regeocodeAddress.getFormatAddress());
        }
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
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_message, null);
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
        edit_address.selectAll();

        // 显示键盘
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInput(edit_address, 0);
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
     * 检查有没照片并提示
     * @return
     */
    private boolean checkPhotoExists() {
        if (mFilePhoto == null || !mFilePhoto.exists()) {
            Toast.makeText(this, R.string.picture_not_exists, Toast.LENGTH_SHORT).show();
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
        Toast.makeText(getApplicationContext(), R.string.photo_up_succ, Toast.LENGTH_SHORT).show();
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
                case R.id.text_time:
                    // 时间
//                    clickBtnTime();
                    break;
                case R.id.btn_login:
                    clickBtnLogin();
                    // 登陆
                    break;
                case R.id.btn_submit:
                    // 发布
                    if (btn_submit.isClickable()) {
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
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
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
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            String str = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
            text_time.setText(str);
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

    /**
     * 点击发布
     */
    private void clickBtnSubmit() {
        if (!checkPhotoExists()) {
            return;
        }
        btn_submit.setClickable(false);
        okHttpPostFiles();
    }

    /**
     * 上传图片
     */
    private void okHttpPostFiles() {
        showProgressDialog();
        final String url = CONST.BASE_URL+"live_photo/upload";
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.addFormDataPart("token", MyApplication.TOKEN);
        if (mFilePhoto.exists()) {
            builder.addFormDataPart("files", mFilePhoto.getName(), RequestBody.create(MediaType.parse("image/*"), mFilePhoto));
        }
        final RequestBody body = builder.build();
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpUtil.enqueue(new Request.Builder().url(url).post(body).build(), new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dismissProgressDialog();
                                Toast.makeText(ActivityPhotoSubmit.this, "上传失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            return;
                        }
                        final String result = response.body().string();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dismissProgressDialog();
                                Log.e("submit-file", result);
                                if (!TextUtils.isEmpty(result)) {
                                    try {
                                        JSONObject obj = new JSONObject(result);
                                        if (!obj.isNull("result")) {
                                            JSONArray array = obj.getJSONArray("result");
                                            if (array.length() > 0) {
                                                String fileUrl = array.get(0).toString();
                                                okHttpPostContent(fileUrl);
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
            }
        }).start();
    }

    /**
     * 报送内容
     */
    private void okHttpPostContent(final String fileUrl) {
        showProgressDialog();
        final PackLocalCity city = ZtqCityDB.getInstance().getCityMain();
        if(city == null) {
            dismissProgressDialog();
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = CONST.BASE_URL+"live_photo/save";
                    JSONObject param  = new JSONObject();
                    param.put("token",MyApplication.TOKEN);
                    JSONObject info = new JSONObject();
                    info.put("areaid", city.ID);
                    info.put("imgType", "1");
                    info.put("userId", MyApplication.UID);
                    info.put("nickName", MyApplication.NAME);
                    info.put("address", edit_address.getText().toString());
                    info.put("weather", mOnChecked.getCurrValue());
                    info.put("des", edit_desc.getText().toString());
                    info.put("imageUrl", fileUrl);
                    param.put("paramInfo", info);
                    String json = param.toString();
                    Log.e("submit-content", json);
                    RequestBody body = FormBody.create(MediaType.parse("application/json; charset=utf-8"), json);
                    OkHttpUtil.enqueue(new Request.Builder().post(body).url(url).build(), new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            Toast.makeText(ActivityPhotoSubmit.this, "上传失败", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            if (!response.isSuccessful()) {
                                return;
                            }
                            final String result = response.body().string();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dismissProgressDialog();
                                    Log.e("submit-content", result);
                                    if (!TextUtils.isEmpty(result)) {
                                        try {
                                            JSONObject obj = new JSONObject(result);
                                            if (!obj.isNull("status")) {
                                                String status = obj.getString("status");
                                                if (TextUtils.equals(status, "success")) {
                                                    tipRequestSucc();
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
