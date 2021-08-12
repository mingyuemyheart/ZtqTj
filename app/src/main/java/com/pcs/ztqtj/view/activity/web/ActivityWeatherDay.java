package com.pcs.ztqtj.view.activity.web;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.amap.api.maps.model.LatLng;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.pcs.lib.lib_pcs_v3.control.file.PcsGetPathValue;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib.lib_pcs_v3.model.image.ImageResizer;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityLocation;
import com.pcs.lib_ztqfj_v2.model.pack.net.BannerInfo;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackInitDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackInitUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackUserPictureDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackUserPictureUp;
import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.AdapterShareGraiView;
import com.pcs.ztqtj.control.tool.CommUtils;
import com.pcs.ztqtj.control.tool.FileUtils;
import com.pcs.ztqtj.control.tool.MyConfigure;
import com.pcs.ztqtj.control.tool.PermissionsTools;
import com.pcs.ztqtj.control.tool.ShareTools;
import com.pcs.ztqtj.control.tool.ZtqImageTool;
import com.pcs.ztqtj.control.tool.ZtqLocationTool;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * 世界气象日活动页面
 * Created by tyaathome on 2017/2/23.
 */

public class ActivityWeatherDay extends FragmentActivityZtqBase implements View.OnClickListener {

    private ControlCommit controlCommit;

    // 弹出框
    private PopupWindow mPopupWindowPhoto;
    // 图片文件
    private File mFilePhoto = null;
    // 图片最大长度
    private final int PHOTO_MAX_PENGTH = 1920;
    // 提交图片上传包
    private PackUserPictureUp packUserPictureUp = new PackUserPictureUp();
    private static final int WHAT_SHARE = 0;
    private Bitmap shareBitmap;
    private AlertDialog dialog;
    private static final int SHARE_WX = 1;
    private static final int SHARE_MOMENT = 2;
    private static final int SHARE_WEIBO = 3;
    private static final int SHARE_QZONE = 4;

    private String mShareContent = "";
    private String mShareUrl = "";

    // 活动地址
//    private static final String mUrl = "http://plasticheart.zicp.io/ztq_qxr/perInfo.html?USER_ID=&PID=10001-8888888899999";
    private String mUrl = "";
    private MyReceiver receiver = new MyReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_day);
        String title = getIntent().getStringExtra("title");
        mUrl = getIntent().getStringExtra("url");
        if (!TextUtils.isEmpty(title)) {
            setTitleText(title);
        }
        initView();
        initEvent();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (controlCommit != null) {
            controlCommit.destory();
            controlCommit = null;
        }
        if (receiver != null) {
            PcsDataBrocastReceiver.unregisterReceiver(this, receiver);
            receiver = null;
        }
    }

    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {
        super.onActivityResult(arg0, arg1, arg2);
        UMShareAPI.get(this).onActivityResult(arg0, arg1, arg2);
        if (arg1 != Activity.RESULT_OK) {
            return;
        }
        switch (arg0) {
            case MyConfigure.REQUEST_ALBUM:
                // 相册
                resultAlbum(arg2);
                break;
            case MyConfigure.REQUEST_CAMERA:
                // 相机
                resultCamera(arg2);
                break;
            case MyConfigure.RESULT_LOGIN:
                //         判断是否登录成功。成功则
                if (!ZtqCityDB.getInstance().isLoginService()) {
                    String userid = MyApplication.UID;
                    controlCommit.loadUrl("javascript:loginCallback ('" + userid + "')");
                }

                break;
        }
    }

    private void initView() {
        initWebView();
        initPopupWindow();
    }

    private void initEvent() {

    }

    private void initData() {
//        mUrl=getIntent().getStringExtra("url");
//        intent.putExtra("title", packDown.arrBannerInfo.get(0).title);
//        intent.putExtra("url", packDown.arrBannerInfo.get(0).url);
//        intent.putExtra("shareContent", packDown.arrBannerInfo.get(0).fx_content);
        PcsDataBrocastReceiver.registerReceiver(this, receiver);
    }

    /**
     * 初始化浏览器
     */
    private void initWebView() {
        JsWeatherDayCommitInterface jsInterface = new JsWeatherDayCommitInterface(this);
        WebView webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setTextZoom(100);
        controlCommit = new ControlCommit(this, webView, jsInterface);
        String url = "";
        if (ZtqCityDB.getInstance().isLoginService()) {
            String userid = MyApplication.UID;
            PackInitDown down = (PackInitDown) PcsDataManager.getInstance().getNetPack(PackInitUp.NAME);
            String pid = down.pid;
            url = mUrl + "?USER_ID=" + userid + "&PID=" + pid;
        } else {
            PackInitDown down = (PackInitDown) PcsDataManager.getInstance().getNetPack(PackInitUp.NAME);
            String pid = down.pid;
            url = mUrl + "?USER_ID=&PID=" + pid;
        }
        controlCommit.loadUrl(url);
    }

    /**
     * 初始化弹出框
     */
    private void initPopupWindow() {
        View view = LayoutInflater.from(this).inflate(R.layout.pop_photograph, null);
        mPopupWindowPhoto = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);
        mPopupWindowPhoto.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                // 设置背景颜色变暗
                try {
                    WindowManager.LayoutParams lp = getWindow().getAttributes();
                    lp.alpha = 1.0f;
                    getWindow().setAttributes(lp);
                } catch (Exception e) {
                }
            }
        });
        // 设置可以获得焦点
        mPopupWindowPhoto.setFocusable(true);
        // 设置弹窗内可点击
        mPopupWindowPhoto.setTouchable(true);
        // 设置弹窗外可点击
        mPopupWindowPhoto.setOutsideTouchable(true);

        mPopupWindowPhoto.setBackgroundDrawable(new BitmapDrawable());

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
        btn.setOnClickListener(this);
        // 相册按钮
        btn = view.findViewById(R.id.btnAlbum);
        btn.setOnClickListener(this);
        // 取消按钮
        btn = view.findViewById(R.id.btnCancel);
        btn.setOnClickListener(this);
    }

    /**
     * 点击拍照
     */
    public void showPowChose(String act_id) {
        packUserPictureUp.act_id = act_id;
        String status = Environment.getExternalStorageState();
        if (!status.equals(Environment.MEDIA_MOUNTED)) {
            showToast(getString(R.string.no_sdcard));
            return;
        }

        View layout = findViewById(R.id.layout);
        mPopupWindowPhoto.showAtLocation(layout, Gravity.BOTTOM, 0, 0);
    }

    /**
     * 关闭弹出框
     */
    private void dismissPopupWindow() {
        if (mPopupWindowPhoto != null && mPopupWindowPhoto.isShowing()) {
            mPopupWindowPhoto.dismiss();
        }
    }

    /**
     * 点击照相
     */
    private void clickCamera() {
        dismissPopupWindow();
        String tempStr = String.valueOf(System.currentTimeMillis());
        mFilePhoto = new File(PcsGetPathValue.getInstance().getMyPhotoPath() + tempStr + ".jpg");
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
        Intent it = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        this.startActivityForResult(it, MyConfigure.REQUEST_ALBUM);
    }

    /**
     * 从相册返回
     *
     * @param fromIntent
     */
    private void resultAlbum(Intent fromIntent) {
        showProgressDialog();
        Uri uri = fromIntent.getData();
        String[] filePathColumns = {MediaStore.Images.Media.DATA};
        Cursor c = this.getContentResolver().query(uri, filePathColumns, null, null, null);
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
        if (!FileUtils.copyFile(oldFile, mFilePhoto)) {
            Toast.makeText(this, R.string.photo_error, Toast.LENGTH_SHORT).show();
            dismissProgressDialog();
            return;
        }
        // 调整图片大小
        resizeImage();
    }

    /**
     * 从相机返回
     */
    private void resultCamera(Intent fromIntent) {
        if (mFilePhoto == null || !mFilePhoto.exists()) {
            Toast.makeText(this, R.string.photo_error, Toast.LENGTH_SHORT).show();
            return;
        }
        // 调整图片大小
        resizeImage();
    }

    /**
     * 调整图片大小
     */
    private void resizeImage() {
        Point point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);
        int maxSize = point.y;
        if (point.x > maxSize) {
            maxSize = point.x;
        }
        if (maxSize > PHOTO_MAX_PENGTH) {
            maxSize = PHOTO_MAX_PENGTH;
        }
        ImageResizer resizer = new ImageResizer();
        resizer.resizeSD(mFilePhoto.getPath(), maxSize, mImageResizerListener);
    }

    private ImageResizer.ImageResizerListener mImageResizerListener = new ImageResizer.ImageResizerListener() {
        @Override
        public void doneSD(String path, boolean isSucc) {
            commit();
        }
    };

    /**
     * 提交图片
     */
    private void commit() {
        showProgressDialog();
        controlCommit.commitFile(mFilePhoto.getPath(), packUserPictureUp);
    }

    /**
     * 获取app信息
     *
     * @return
     */
    public String getDatasFromApp() {
        // 获取imei
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String imei = tm.getDeviceId();
        // 定位城市
        PackLocalCityLocation packLocation = ZtqLocationTool.getInstance().getLocationCity();
        // 当前城市ID
        String currentCityID = packLocation.ID;
        // 当前城市显示ID
        String xianshiid = packLocation.PARENT_ID;

        PackageManager pm = getPackageManager();//context为当前Activity上下文
        PackageInfo pi = null;
        try {
            pi = pm.getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        // 版本名
        String appVersion = pi.versionName;

        // 经纬度
        LatLng latlng = ZtqLocationTool.getInstance().getLatLng();
        String lat = String.valueOf(latlng.latitude);
        String lon = String.valueOf(latlng.longitude);
        String address = "";
        RegeocodeAddress regeocodeAddress = ZtqLocationTool.getInstance()
                .getSearchAddress();
        if (regeocodeAddress != null) {
            address = regeocodeAddress.getFormatAddress();
        }

        JSONObject obj = new JSONObject();
        try {
            JSONObject locationObj = new JSONObject();
            locationObj.put("lat", lat);
            locationObj.put("lon", lon);
            locationObj.put("address", address);

            obj.put("imei", imei);
            obj.put("currentCityID", currentCityID);
            obj.put("xianshiid", xianshiid);
            obj.put("appVersion", appVersion);
            obj.put("locationaddress", locationObj);
            obj.put("appType", "天津气象决策版");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj.toString();
    }

    /**
     * 分享
     *
     * @param url
     * @param content
     */
    public void shareUrlAndContent(String url, String content) {
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        bundle.putString("content", content);
        Message message = Message.obtain();
        message.setData(bundle);
        message.what = WHAT_SHARE;
        mHandler.sendMessage(message);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            String title = "";
            BannerInfo info = (BannerInfo) getIntent().getSerializableExtra("BannerInfo");
            // 初始化sharetools
            ShareTools.getInstance(ActivityWeatherDay.this);
            switch (msg.what) {
                case WHAT_SHARE:
                    String url = msg.getData().getString("url");
                    if(TextUtils.isEmpty(url)) {
                        url = "";
                    }
                    String content = msg.getData().getString("content");
                    if(TextUtils.isEmpty(content)) {
                        content = "";
                    }
                    mShareContent = content + " " + url;
                    mShareUrl = url;
                    WebView webView = (WebView) findViewById(R.id.webview);
                    webView.getSettings().setTextZoom(100);
                    shareBitmap = ZtqImageTool.getInstance().getWebViewBitmap(getBaseContext(), webView);
                    showPopShare();
                    break;
                case SHARE_WX:
                    // 微信
                    title = "【天津气象决策版分享】 " + info.title;
                    ShareTools.shareWeb(mShareUrl, title, mShareContent, shareBitmap, SHARE_MEDIA.WEIXIN);
                    break;
                case SHARE_MOMENT:
                    // 朋友圈
                    title = "【天津气象决策版分享】 " + info.title;
                    ShareTools.shareWeb(mShareUrl, title, mShareContent, shareBitmap, SHARE_MEDIA.WEIXIN_CIRCLE);
                    break;
                case SHARE_WEIBO:
                    // 新浪微博
                    ShareTools.shareWithImageAndText(mShareContent, shareBitmap, SHARE_MEDIA.SINA);
                    break;
                case SHARE_QZONE:
                    // qq空间
                    title = "【天津气象决策版分享】 " + info.title + mShareContent;
                    ShareTools.shareWeb(mShareUrl, title, mShareContent, shareBitmap, SHARE_MEDIA.QZONE);
                    break;
            }
        }
    };

    private void showPopShare() {
        View view = LayoutInflater.from(ActivityWeatherDay.this).inflate(R.layout.pop_share_windown, null);
        GridView share_face = (GridView) view.findViewById(R.id.share_face);
        AdapterShareGraiView adapter = new AdapterShareGraiView();
        share_face.setAdapter(adapter);
        share_face.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    shareClick(SHARE_MEDIA.WEIXIN);
                } else if (position == 1) {
                    shareClick(SHARE_MEDIA.WEIXIN_CIRCLE);
                } else if (position == 2) {
                    shareClick(SHARE_MEDIA.SINA);
                } else if (position == 3) {
                    shareClick(SHARE_MEDIA.QZONE);
                }
                dialog.dismiss();
            }
        });
        dialog = new AlertDialog.Builder(ActivityWeatherDay.this).create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setView(view, 0, 0, 0, 0);
        dialog.show();
        dialog.show();
    }

    public void shareClick(SHARE_MEDIA p) {
        int what = -1;
        if(p == SHARE_MEDIA.WEIXIN) {
            what = 1;
        } else if (p == SHARE_MEDIA.WEIXIN_CIRCLE) {
            what = 2;
        } else if (p == SHARE_MEDIA.SINA) {
            what = 3;
        } else if (p == SHARE_MEDIA.QZONE) {
            what = 4;
        }
        mHandler.sendEmptyMessage(what);
    }

    private ShareTools.ShareOnItemClickListener shareListener = new ShareTools.ShareOnItemClickListener() {
        @Override
        public void onItemClick(int position) {
            String title = "";
            String content = "";
            BannerInfo info = (BannerInfo) getIntent().getSerializableExtra("BannerInfo");
            switch (position) {
                case 0:
                    //电话
                    ShareTools.gotoDial();
                    break;
                case 1:
                    // 朋友圈
                    title = "【天津气象决策版分享】 " + info.title;
                    ShareTools.shareWeb(info.url, title, info.fx_content, shareBitmap, SHARE_MEDIA.WEIXIN_CIRCLE);
                    break;
                case 2:
                    // 微信
                    title = "【天津气象决策版分享】 " + info.title;
                    ShareTools.shareWeb(info.url, title, info.fx_content, shareBitmap, SHARE_MEDIA.WEIXIN);
                    break;
                case 3:
                    // 彩信
                    ShareTools.shareWithImageAndText(info.fx_content, shareBitmap, SHARE_MEDIA.SMS);
                    break;
                case 4:
                    // qq空间
                    title = "【天津气象决策版分享】 " + info.title;
                    ShareTools.shareWeb(info.url, title, info.fx_content, shareBitmap, SHARE_MEDIA.QZONE);
                    break;
                case 5:
                    // 新浪微博
                    content = info.fx_content + " " + info.url;
                    ShareTools.shareWithImageAndText(content, shareBitmap, SHARE_MEDIA.SINA);
                    break;
            }
        }
    };

    private ShareTools.ShareCallBackListener shareCallBackListener = new ShareTools.ShareCallBackListener() {
        @Override
        public void onSuccess() {
            controlCommit.loadUrl("javascript:shareUrlAndContentCallback ('1')");
        }

        @Override
        public void onError() {

        }

        @Override
        public void onCancle() {

        }
    };

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
                //取消选项
                dismissPopupWindow();
                break;
        }
    }

    private class MyReceiver extends PcsDataBrocastReceiver {

        @Override
        public void onReceive(String nameStr, String errorStr) {
            if (nameStr.equals(packUserPictureUp.getName())) {
                dismissProgressDialog();
                PackUserPictureDown down = (PackUserPictureDown) PcsDataManager.getInstance().getNetPack(nameStr);
                if (down == null || !down.result.equals("1")) {
                    //Toast.makeText(ActivityWeatherDay.this, "提交失败", Toast.LENGTH_SHORT).show();
                } else {
                    controlCommit.loadUrl("javascript:uploadImgCallback('" + down.url + "')");
                    //showToast("图片提交完成");
                }
            }
        }
    }
}
