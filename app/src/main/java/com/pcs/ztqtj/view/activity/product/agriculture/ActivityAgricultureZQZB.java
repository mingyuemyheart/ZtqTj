package com.pcs.ztqtj.view.activity.product.agriculture;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.services.geocoder.RegeocodeAddress;
import com.pcs.lib.lib_pcs_v3.control.file.PcsGetPathValue;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCity;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityLocation;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalUrl;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackInitDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackInitUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.agriculture.AgricultureInfo;
import com.pcs.lib_ztqfj_v2.model.pack.net.agriculture.PackAgricultureDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.agriculture.PackAgricultureSubmitDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.agriculture.PackAgricultureSubmitUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.agriculture.PackAgricultureUp;
import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.AdapterDisasterCategory;
import com.pcs.ztqtj.control.tool.CommUtils;
import com.pcs.ztqtj.control.tool.KWHttpRequest;
import com.pcs.ztqtj.control.tool.LoginInformation;
import com.pcs.ztqtj.control.tool.MyConfigure;
import com.pcs.ztqtj.control.tool.PermissionsTools;
import com.pcs.ztqtj.control.tool.ZtqLocationTool;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
import com.pcs.ztqtj.view.activity.photoshow.ActivityLogin;
import com.pcs.ztqtj.view.myview.MyGridView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 农业灾情直报
 * Created by tyaathome on 2016/11/11.
 */
public class ActivityAgricultureZQZB extends FragmentActivityZtqBase implements View.OnClickListener {

    private MyGridView gridView;
    private AdapterDisasterCategory adapter;
    private List<AgricultureInfo> disasterList = new ArrayList<>();
    private TextView tvHello;
    private Button btnLogin;
    private Button btnAddress;
    private TextView tvTime;
    private Button btnAddImage;
    private ImageView ivPreview;
    private EditText etDescription;
    private Button btnSubmit;

    private MyReceiver receiver = new MyReceiver();
    // 弹出框
    private PopupWindow mPopupWindow;
    private File mFilePhoto;
    private String channel_id = "";
    private String area_id = "";
    private String zq_id = "";

    PackAgricultureUp packAgricultureUp = new PackAgricultureUp();

    /**
     * 上传数据+文件
     */
    private KWHttpRequest mKWHttpRequest;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                // 登录
                case MyConfigure.RESULT_USER_REGISTER:
                    updateLogin();
                    break;
                case MyConfigure.RESULT_CITY_LIST:
                    // 选择发布城市
                    PackLocalCity cityInfo = (PackLocalCity) intent.getSerializableExtra("cityinfo");
                    PackLocalCity parent = (PackLocalCity) intent.getSerializableExtra("parent_city");
                    if(cityInfo != null && parent != null) {
                        if (cityInfo.isFjCity) {
                            btnAddress.setText(parent.CITY + "," + parent.NAME + "," + cityInfo.NAME);
                        } else {
                            btnAddress.setText(cityInfo.CITY + "," + cityInfo.NAME);
                        }
                        area_id = cityInfo.ID;
                    }
                    break;
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
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitleText("农业灾情直报");
        setContentView(R.layout.activity_agriculture_zqzb);
        initView();
        initEvent();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(receiver != null) {
            PcsDataBrocastReceiver.unregisterReceiver(this, receiver);
            receiver = null;
        }
    }

    private void initView() {
        // 隐藏键盘
        //this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        gridView = (MyGridView) findViewById(R.id.gridview);
        tvHello = (TextView) findViewById(R.id.tv_hello);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnAddress = (Button) findViewById(R.id.btn_address);
        tvTime = (TextView) findViewById(R.id.tv_time);
        btnAddImage = (Button) findViewById(R.id.btn_add_image);
        ivPreview = (ImageView) findViewById(R.id.iv_image_preview);
        etDescription = (EditText) findViewById(R.id.et_description);
        btnSubmit = (Button) findViewById(R.id.btn_submit);
    }

    private void initEvent() {
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.setCurrentClickedPosition(position);
                AgricultureInfo info = disasterList.get(position);
                zq_id = info.type;
            }
        });
        btnLogin.setOnClickListener(this);
        btnAddress.setOnClickListener(this);
        btnAddImage.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        ivPreview.setOnClickListener(this);
        etDescription.addTextChangedListener(mTextWatcher);
    }

    private void initData() {
        channel_id = getIntent().getStringExtra("channel_id");
        PcsDataBrocastReceiver.registerReceiver(this, receiver);
        initKWHttpRequest();
        adapter = new AdapterDisasterCategory(this, disasterList);
        gridView.setAdapter(adapter);
        initPopupWindow();
        update();
        // 请求灾情类型
        reqDisaster();
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

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            int length = etDescription.getText().toString().length();
            if(length <= 100) {
                TextView tv = (TextView) findViewById(R.id.tv_length);
                tv.setText(length + "/100");
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

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
        if(requestCode == MyConfigure.REQUEST_CAMERA) {
            PermissionsTools.onRequestPermissionsResult(this, permissions, grantResults, new PermissionsTools.RequestPermissionResultCallback() {

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

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 6;
        Bitmap bm = BitmapFactory.decodeFile(mFilePhoto.getPath(), options);
        ivPreview.setImageBitmap(bm);
        ivPreview.setVisibility(View.VISIBLE);
        btnAddImage.setVisibility(View.GONE);
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
     * 从相机返回
     */
    private void resultCamera(Intent fromIntent) {
        if (mFilePhoto == null || !mFilePhoto.exists()) {
            Toast.makeText(this, R.string.photo_error, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        Bitmap bitmap = BitmapFactory.decodeFile(mFilePhoto.getPath());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] bitmapdata = bos.toByteArray();
        try {
            FileOutputStream fos = new FileOutputStream(mFilePhoto);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 6;
        Bitmap bm = BitmapFactory.decodeFile(mFilePhoto.getPath(), options);
        ivPreview.setImageBitmap(bm);
        ivPreview.setVisibility(View.VISIBLE);
        btnAddImage.setVisibility(View.GONE);
    }

    /**
     * 刷新控件
     */
    private void update() {
        updateLogin();
        updateAddress();
        updateTime();
    }

    /**
     * 刷新登录按钮
     */
    private void updateLogin() {
        if(LoginInformation.getInstance().hasLogin()) {
            String username = MyApplication.NAME;
            tvHello.setText("你好，" + username + "，请发布灾情报告。");
            tvHello.setVisibility(View.VISIBLE);
            btnLogin.setVisibility(View.GONE);
        } else {
            tvHello.setVisibility(View.GONE);
            btnLogin.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 刷新灾情地点
     */
    private void updateAddress() {
        // 是否定位成功
        boolean isClickable;
        PackLocalCityLocation packLocation = ZtqLocationTool.getInstance().getLocationCity();
        if(packLocation != null) { // 定位成功
//            // 福建省内判断
//            if (packLocation.isFjCity) {
//                PackLocalCity parentCity = ZtqCityDB.getInstance().getCityInfo2_ID(packLocation.PARENT_ID);
//                if (!packLocation.NAME.equals(PackLocalCityLocation.LOCATING)) {
//                    btnAddress.setText(parentCity.CITY + "," + parentCity.NAME + "," + packLocation.NAME);
//                    area_id = parentCity.ID;
//                    isLocationSuccess = true;
//                } else {
//                    btnAddress.setText("定位失败");
//                    isLocationSuccess = false;
//                }
//            } else { // 附件省外判断
//                if(!packLocation.NAME.equals(PackLocalCityLocation.LOCATING)) {
//                    btnAddress.setText(packLocation.CITY + "," + packLocation.NAME);
//                    area_id = packLocation.ID;
//                    isLocationSuccess = true;
//                } else {
//                    isLocationSuccess = false;
//                }
//            }
            RegeocodeAddress regeocodeAddress = ZtqLocationTool.getInstance()
                    .getSearchAddress();
            if (regeocodeAddress != null) {
                btnAddress.setText((regeocodeAddress.getFormatAddress()));
                area_id = packLocation.ID;
                isClickable = false;
            } else {
                btnAddress.setText(getResources().getString(R.string.targeting_failed));
                isClickable = true;
            }
        } else { // 定位失败
            btnAddress.setText(getResources().getString(R.string.targeting_failed));
            isClickable = true;
        }
        btnAddress.setTag(isClickable);
    }

    /**
     * 更新时间
     */
    private void updateTime() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("yyyy年M月d日 HH:m:s");
        String result ="上报时间： " + format.format(date);
        tvTime.setText(result);
    }

    /**
     * 跳转登录
     */
    private void gotoLogin() {
        Intent intent = new Intent(this, ActivityLogin.class);
        startActivityForResult(intent, MyConfigure.RESULT_USER_REGISTER);
    }

    /**
     * 跳转选择城市
     */
    private void gotoSelectAddress() {
//        Intent intent = new Intent(this, ActivityAntherCityList.class);
//        intent.putExtra("onlyFjCity", true);
//        startActivityForResult(intent, MyConfigure.RESULT_CITY_LIST);
    }

    /**
     * 显示弹出框
     */
    private void showPopupWindow() {
        if(mPopupWindow != null && !mPopupWindow.isShowing()) {
            View layout = findViewById(R.id.layout);
            mPopupWindow.showAtLocation(layout, Gravity.BOTTOM, 0, 0);
        }
    }

    /**
     * 检查提交参数
     */
    private boolean check() {
        // 是否登录成功
        if(!LoginInformation.getInstance().hasLogin()) {
            showToast(getResources().getString(R.string.not_logged_in));
            return false;
        }
        if(mFilePhoto == null) {
            showToast(getResources().getString(R.string.update_disaster_picture));
            return false;
        }
        if(TextUtils.isEmpty(area_id)) {
            showToast("请选择定位城市");
            return false;
        }
        return true;
    }

    /**
     * 请求灾害类型
     */
    private void reqDisaster() {
        packAgricultureUp = new PackAgricultureUp();
        packAgricultureUp.type = "7";
        PcsDataDownload.addDownload(packAgricultureUp);
    }

    /**
     * 提交请求
     */
    private void reqSubmit() {
        showProgressDialog();
        PackAgricultureSubmitUp packUp = new PackAgricultureSubmitUp();
        packUp.channel_id = channel_id;
        packUp.user_id = MyApplication.UID;
        packUp.area_id = area_id;
        packUp.zq_id = zq_id;
        packUp.zq_desc = etDescription.getText().toString();
        // 请求网络
        mKWHttpRequest.setFilePath(mFilePhoto.getPath(), KWHttpRequest.FILETYPE.IMG);
        mKWHttpRequest.addDownload(packUp);
        mKWHttpRequest.startAsynchronous();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                gotoLogin();
                break;
            case R.id.btn_address:
                boolean b = (boolean) v.getTag();
                if(b) {
                    gotoSelectAddress();
                }
                break;
            case R.id.btn_add_image:
            case R.id.iv_image_preview:
                showPopupWindow();
                break;
            case R.id.btn_submit:
                if(check()) {
                    reqSubmit();
                }
                break;
        }
    }

    private class MyReceiver extends PcsDataBrocastReceiver {

        @Override
        public void onReceive(String nameStr, String errorStr) {
            if(!TextUtils.isEmpty(errorStr)) {
                return ;
            }

            if(nameStr.equals(packAgricultureUp.getName())) {
                PackAgricultureDown down = (PackAgricultureDown) PcsDataManager.getInstance().getNetPack(nameStr);
                if(down == null) {
                    return ;
                }

                disasterList.clear();
                disasterList.addAll(down.info_list);
                adapter.setCurrentClickedPosition(0);
                AgricultureInfo info = disasterList.get(0);
                zq_id = info.type;
            }

            if(nameStr.equals(PackAgricultureSubmitUp.NAME)) {
                dismissProgressDialog();
                PackAgricultureSubmitDown down = (PackAgricultureSubmitDown) PcsDataManager.getInstance().getNetPack(nameStr);
                if(down == null) {
                    return ;
                }

                if(down.result.equals("1")) {
                    // 显示添加图片按钮
                    btnAddImage.setVisibility(View.VISIBLE);
                    // 隐藏图片
                    ivPreview.setImageBitmap(null);
                    ivPreview.setVisibility(View.GONE);
                    // 清空描述内容
                    etDescription.setText("");
                    // 还原灾害类型选择
                    if(disasterList.size() > 0) {
                        adapter.setCurrentClickedPosition(0);
                        AgricultureInfo info = disasterList.get(0);
                        zq_id = info.type;
                    }
                    showToast("上传成功");
                } else {
                    showToast(down.result_msg);
                }
            }
        }
    }
}
