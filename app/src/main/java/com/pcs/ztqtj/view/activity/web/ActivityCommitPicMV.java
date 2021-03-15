package com.pcs.ztqtj.view.activity.web;//package com.pcs.knowing_weather.view.activity.web;
//
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.content.Intent;
//import android.database.Cursor;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Point;
//import android.graphics.drawable.BitmapDrawable;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Environment;
//import android.os.Handler;
//import android.os.Message;
//import android.provider.MediaStore;
//import android.text.TextUtils;
//import android.view.Gravity;
//import android.view.KeyEvent;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.Window;
//import android.view.WindowManager;
//import android.webkit.WebView;
//import android.widget.AdapterView;
//import android.widget.Button;
//import android.widget.GridView;
//import android.widget.LinearLayout;
//import android.widget.PopupWindow;
//import android.widget.Toast;
//
//import com.pcs.knowing_weather.R;
//import com.pcs.knowing_weather.control.adapter.AdapterShareGraiView;
//import com.pcs.knowing_weather.control.tool.VideoFileUtils;
//import com.pcs.knowing_weather.control.tool.KWHttpRequest;
//import com.pcs.knowing_weather.control.tool.LoginInformation;
//import com.pcs.knowing_weather.control.tool.ZtqImageTool;
//import com.pcs.knowing_weather.view.activity.FragmentActivityZtqBase;
//import com.pcs.knowing_weather.view.activity.cammer.ActivityVideo;
//import com.pcs.knowing_weather.view.activity.photoshow.ActivityPhotoLogin;
//import com.pcs.lib.lib_pcs_v3.control.file.PcsGetPathValue;
//import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
//import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
//import com.pcs.lib.lib_pcs_v3.model.image.ImageResizer;
//import com.pcs.lib.lib_pcs_v3.model.pack.PcsPackUp;
//import com.pcs.lib_ztqfj_v2.model.pack.net.PackCommitMoviceDown;
//import com.pcs.lib_ztqfj_v2.model.pack.net.PackCommitMoviceUp;
//import com.pcs.lib_ztqfj_v2.model.pack.net.PackInitDown;
//import com.pcs.lib_ztqfj_v2.model.pack.net.PackInitUp;
//import com.pcs.lib_ztqfj_v2.model.pack.net.PackUserPictureDown;
//import com.pcs.lib_ztqfj_v2.model.pack.net.PackUserPictureUp;
//import com.umeng.socialize.ShareAction;
//import com.umeng.socialize.UMShareAPI;
//import com.umeng.socialize.UMShareListener;
//import com.umeng.socialize.bean.SHARE_MEDIA;
//import com.umeng.socialize.media.UMImage;
//import com.umeng.socialize.media.UMWeb;
//
//import java.io.File;
//
//import static com.pcs.knowing_weather.view.activity.web.JavaScriptinterface.resultCode;
//
//
///**
// * @author Z
// *         <p>
// *         <p>
// *         网页跳转
// */
//public class ActivityCommitPicMV extends FragmentActivityZtqBase implements View.OnClickListener {
//
//    // 图片最大长度
//    private final int PHOTO_MAX_PENGTH = 1920;
//    // 相册
//    private final int REQUEST_ALBUM = 101;
//    // 拍照
//    private final int REQUEST_CAMERA = 102;
//    // 拍照
//    private final int REQUEST_MOVICE = 103;
//    // 图片文件
//    private File mFilePhoto = null;
//    private Button commit_pic;
//    private Button commit_movice;
//    /**
//     * 上传包
//     */
//    private PackUserPictureUp mPackPicUp = new PackUserPictureUp();
//    private PackCommitMoviceUp mPackMusicUp = new PackCommitMoviceUp();
//    private ControlCommitPicMv controlPicMV;
//
//    private WebView webview;
//
//    @Override
//    protected void onCreate(Bundle arg0) {
//        super.onCreate(arg0);
//        setContentView(R.layout.activity_commit_pic_mv);
//        PcsDataBrocastReceiver.registerReceiver(this, mReceiver);
//        controlPicMV = new ControlCommitPicMv(this, listener);
//        initView();
//        initData();
//        initEvent();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        PcsDataBrocastReceiver.unregisterReceiver(this, mReceiver);
//        controlPicMV.destory();
//    }
//
//    private void initView() {
//        commit_pic = (Button) findViewById(R.id.commit_pic);
//        commit_movice = (Button) findViewById(R.id.commit_movice);
//        webview = (WebView) findViewById(R.id.webview);
//    }
//
//    private String url;
//
//    private void initData() {
//        controlPicMV.initWebView(webview);
//        Intent intent = getIntent();
//        String pagetitle = intent.getStringExtra("title");
//        setTitleText(pagetitle);
//        url = intent.getStringExtra("url");
//        shareContent = intent.getStringExtra("shareContent");
//        initPopupWindow();
//        initPopupWindow2();
//        String pid = "?PID=";
//        PackInitDown packDown = (PackInitDown) PcsDataManager.getInstance().getNetPack(PackInitUp.NAME);
//        if (packDown != null) {
//            pid += packDown.pid;
//        }
//
//        webview.loadUrl(url + pid);
////        webview.loadUrl("http://www.fjqxfw.cn:8099/ztq_qxxzb/");
//    }
//
//    private void commti(String filePath, PcsPackUp packUp) {
//        showProgressDialog();
//        controlPicMV.commitFile(filePath, packUp);
//    }
//
//    private void initEvent() {
//        commit_pic.setOnClickListener(this);
//        commit_movice.setOnClickListener(this);
//    }
//
//
//    @Override
//    protected void onActivityResult(int arg0, int arg1, Intent arg2) {
//        super.onActivityResult(arg0, arg1, arg2);
//        UMShareAPI.get(this).onActivityResult(arg0, arg1, arg2);
//        if (arg1 != Activity.RESULT_OK) {
//            return;
//        }
//        switch (arg0) {
//            case REQUEST_ALBUM:
//                // 相册
//                resultAlbum(arg2);
//                break;
//            case REQUEST_CAMERA:
//                // 相机
//                resultCamera(arg2);
//                break;
//            case REQUEST_MOVICE:
//                // 视频
//                Uri uri = arg2.getData();
//                String path = VideoFileUtils.getPath(this, uri);
//                commti(path, mPackMusicUp);
//                break;
//            case 1111:
//                String file=arg2.getStringExtra("file");
//                commti(file,mPackMusicUp);
//                break;
//            case resultCode:
//                //         判断是否登录成功。成功则
//                if (LoginInformation.getInstance().hasLogin()) {
//                    String userId = "?USER_ID=" + LoginInformation.getInstance().getUserId();
//                    String pid = "?PID=";
//                    PackInitDown packDown = (PackInitDown) PcsDataManager.getInstance().getNetPack(PackInitUp.NAME);
//                    if (packDown != null) {
//                        pid += packDown.pid;
//                    }
//                    webview.loadUrl(url + userId + pid);
//                }
//                break;
//        }
//    }
//
//    // 弹出框
//    private PopupWindow mPopupWindowMovice;
//    // 弹出框
//    private PopupWindow mPopupWindowPhoto;
//
//    /**
//     * 初始化弹出框
//     */
//    private void initPopupWindow() {
//        View view = LayoutInflater.from(this).inflate(R.layout.pop_photograph, null);
//        mPopupWindowPhoto = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT, true);
//        mPopupWindowPhoto.setOnDismissListener(new PopupWindow.OnDismissListener() {
//            @Override
//            public void onDismiss() {
//                // 设置背景颜色变暗
//                try {
//                    WindowManager.LayoutParams lp = getWindow().getAttributes();
//                    lp.alpha = 1.0f;
//                    getWindow().setAttributes(lp);
//                } catch (Exception e) {
//                }
//            }
//        });
//        // 设置可以获得焦点
//        mPopupWindowPhoto.setFocusable(true);
//        // 设置弹窗内可点击
//        mPopupWindowPhoto.setTouchable(true);
//        // 设置弹窗外可点击
//        mPopupWindowPhoto.setOutsideTouchable(true);
//
//        mPopupWindowPhoto.setBackgroundDrawable(new BitmapDrawable());
//
//        view.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if (keyCode == KeyEvent.KEYCODE_BACK) {
//                    dismissPopupWindow();
//                    return true;
//                }
//                return false;
//            }
//        });
//        View btn;
//        // 相机按钮
//        btn = view.findViewById(R.id.btnCamera);
//        btn.setOnClickListener(this);
//        // 相册按钮
//        btn = view.findViewById(R.id.btnAlbum);
//        btn.setOnClickListener(this);
//        // 取消按钮
//        btn = view.findViewById(R.id.btnCancel);
//        btn.setOnClickListener(this);
//    }
//    /**
//     * 初始化弹出框
//     */
//    private void initPopupWindow2() {
//        View view = LayoutInflater.from(this).inflate(R.layout.pop_photograph2, null);
//        mPopupWindowMovice = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT, true);
//        mPopupWindowMovice.setOnDismissListener(new PopupWindow.OnDismissListener() {
//            @Override
//            public void onDismiss() {
//                // 设置背景颜色变暗
//                try {
//                    WindowManager.LayoutParams lp = getWindow().getAttributes();
//                    lp.alpha = 1.0f;
//                    getWindow().setAttributes(lp);
//                } catch (Exception e) {
//                }
//            }
//        });
//        // 设置可以获得焦点
//        mPopupWindowMovice.setFocusable(true);
//        // 设置弹窗内可点击
//        mPopupWindowMovice.setTouchable(true);
//        // 设置弹窗外可点击
//        mPopupWindowMovice.setOutsideTouchable(true);
//
//        mPopupWindowMovice.setBackgroundDrawable(new BitmapDrawable());
//
//        view.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if (keyCode == KeyEvent.KEYCODE_BACK) {
//                    dismissPopupWindow();
//                    return true;
//                }
//                return false;
//            }
//        });
//        View btn;
//        // 相机按钮
//        btn = view.findViewById(R.id.btnCamera2);
//        btn.setOnClickListener(this);
//        // 相册按钮
//        btn = view.findViewById(R.id.btnAlbum2);
//        btn.setOnClickListener(this);
//        // 取消按钮
//        btn = view.findViewById(R.id.btnCancel2);
//        btn.setOnClickListener(this);
//    }
//
//    /**
//     * 关闭弹出框
//     */
//    private void dismissPopupWindow() {
//        if (mPopupWindowPhoto != null && mPopupWindowPhoto.isShowing()) {
//            mPopupWindowPhoto.dismiss();
//        }
//    }    /**
//     * 关闭弹出框
//     */
//    private void dismissPopupWindow2() {
//        if (mPopupWindowMovice != null && mPopupWindowMovice.isShowing()) {
//            mPopupWindowMovice.dismiss();
//        }
//    }
//
//    /**
//     * 点击拍照
//     */
//    public void showPowChose(String act_id) {
//        mPackPicUp.act_id = act_id;
//        String status = Environment.getExternalStorageState();
//        if (!status.equals(Environment.MEDIA_MOUNTED)) {
//            showToast(getString(R.string.no_sdcard));
//            return;
//        }
//        GridView gridView = (GridView) findViewById(R.id.gridView);
//        mPopupWindowPhoto.showAtLocation(gridView, Gravity.BOTTOM, 0, 0);
//    }
//
//
//    /**
//     * 点击照相
//     */
//    private void clickCamera() {
//        dismissPopupWindow();
//        String tempStr = String.valueOf(System.currentTimeMillis());
//        mFilePhoto = new File(PcsGetPathValue.getInstance().getMyPhotoPath() + tempStr + ".jpg");
//        mFilePhoto.getParentFile().mkdirs();
//        // 启动相机
//        Uri uri = Uri.fromFile(mFilePhoto);
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
//        startActivityForResult(intent, REQUEST_CAMERA);
//    }
//
//    /**
//     * 点击相册
//     */
//    private void clickAlbum() {
//        dismissPopupWindow();
//        Intent it = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        this.startActivityForResult(it, REQUEST_ALBUM);
//    }
//
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.btnCamera:
//                // 点击相机
//                clickCamera();
//                break;
//            case R.id.btnAlbum:
//                // 点击相册
//                clickAlbum();
//                break;
//            case R.id.btnCancel:
//                //取消选项
//                dismissPopupWindow();
//                break;
//            case R.id.commit_pic:
////                showPowChose("");
//                break;
//            case R.id.commit_movice:
////                showFileChooser("");
//                break;
//
//            case R.id.btnCamera2:
//                // 录像
//                showFileChooserSelf();
//                break;
//            case R.id.btnAlbum2:
//                // 本地
//                showFileChooserDir();
//                break;
//            case R.id.btnCancel2:
//                //取消
//                dismissPopupWindow2();
//
//        }
//    }
//
//
//    //文件选择器
//    public void showFileChooser(String user_id, String act_id) {
//        mPackMusicUp.act_id = act_id;
//        mPackMusicUp.user_id = user_id;
//
//        String status = Environment.getExternalStorageState();
//        if (!status.equals(Environment.MEDIA_MOUNTED)) {
//            Toast.makeText(this, R.string.no_sdcard, Toast.LENGTH_SHORT).show();
//            return;
//        }
//        GridView gridView = (GridView) findViewById(R.id.gridView);
//        mPopupWindowMovice.showAtLocation(gridView, Gravity.BOTTOM, 0, 0);
//
////        Intent it = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
////        this.startActivityForResult(it, REQUEST_MOVICE);
//    }
//
//    //文件选择器
//    public void showFileChooserDir() {
//        Intent it = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
//        this.startActivityForResult(it, REQUEST_MOVICE);
//    }
//
//    //文件选择器
//    public void showFileChooserSelf() {
//       Intent intent=new Intent(this,ActivityVideo.class);
//        startActivityForResult(intent,1111);
//    }
//
//
//    /**
//     * 从相机返回
//     */
//    private void resultCamera(Intent fromIntent) {
//        if (mFilePhoto == null || !mFilePhoto.exists()) {
//            Toast.makeText(this, R.string.photo_error, Toast.LENGTH_SHORT).show();
//            return;
//        }
//        // 调整图片大小
//        resizeImage();
//    }
//
//    /**
//     * 从相册返回
//     *
//     * @param fromIntent
//     */
//    private void resultAlbum(Intent fromIntent) {
//        showProgressDialog();
//        Uri uri = fromIntent.getData();
//        String[] filePathColumns = {MediaStore.Images.Media.DATA};
//        Cursor c = this.getContentResolver().query(uri, filePathColumns, null, null, null);
//        if (c == null) {
//            return;
//        }
//        c.moveToFirst();
//        int columnIndex = c.getColumnIndex(filePathColumns[0]);
//        String picturePath = c.getString(columnIndex);
//        c.close();
//        // ---------拷贝文件
//        // 旧文件
//        File oldFile = new File(picturePath);
//        // 新文件
//        mFilePhoto = new File(PcsGetPathValue.getInstance().getMyPhotoPath()
//                + oldFile.getName());
//        if (mFilePhoto.exists()) {
//            mFilePhoto.delete();
//        }
//        mFilePhoto.getParentFile().mkdirs();
//        if (!VideoFileUtils.copyFile(oldFile, mFilePhoto)) {
//            Toast.makeText(this, R.string.photo_error, Toast.LENGTH_SHORT).show();
//            dismissProgressDialog();
//            return;
//        }
//        // 调整图片大小
//        resizeImage();
//    }
//
//    /**
//     * 调整图片大小
//     */
//    private void resizeImage() {
//        Point point = new Point();
//        getWindowManager().getDefaultDisplay().getSize(point);
//        int maxSize = point.y;
//        if (point.x > maxSize) {
//            maxSize = point.x;
//        }
//        if (maxSize > PHOTO_MAX_PENGTH) {
//            maxSize = PHOTO_MAX_PENGTH;
//        }
//        ImageResizer resizer = new ImageResizer();
//        resizer.resizeSD(mFilePhoto.getPath(), maxSize, mImageResizerListener);
//    }
//
//    private ImageResizer.ImageResizerListener mImageResizerListener = new ImageResizer.ImageResizerListener() {
//        @Override
//        public void doneSD(String path, boolean isSucc) {
//            commti(mFilePhoto.getPath(), mPackPicUp);
//        }
//    };
//    //提交文件监听
//    private KWHttpRequest.KwHttpRequestListener listener = new KWHttpRequest.KwHttpRequestListener() {
//        @Override
//        public void loadFinished(int nThreadID, byte[] b) {
////            String result = new String(b);
////            JSONObject jsonObject = null;
////            try {
////                jsonObject = new JSONObject(result);
////                // Head
////                JSONObject h = jsonObject.getJSONObject("h");
////                int is = h.getInt("is");
////                if (is < 0) {
////                    String error = h.getString("error");
////                    return;
////                }
////                // Body
////                JSONObject body = jsonObject.getJSONObject("b");
////            } catch (JSONException e) {
////                e.printStackTrace();
////            }
//        }
//
//        @Override
//        public void loadFailed(int nThreadID, int nErrorCode) {
//        }
//    };
//
//    public void toLoginActivity() {
//        Intent intent = new Intent(this, ActivityPhotoLogin.class);
//        startActivityForResult(intent, resultCode);
//    }
//
//    public void openShare() {
//        handler.sendEmptyMessage(0);
//    }
//
//    private String shareContent;
//    private SHARE_MEDIA cutMedia;
//    private Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            int what = msg.what;
//            dismissProgressDialog();
//            if (what == 0) {
//                showPopShare();
//            } else if (what == 1) {
//                String shareStr = shareContent;
//                Bitmap bitmap = ZtqImageTool.getInstance().getWebViewBitmap(ActivityCommitPicMV.this, webview);
//                if(bitmap==null){
//                    bitmap= BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher);
//                }
//
//                String clickurl = "";
//                if (!TextUtils.isEmpty(shareContent)) {
//                    int index = shareContent.indexOf("http:");
//                    if (index > 0) {
//                        clickurl = shareContent.substring(index, shareContent.length());
//                    }
//                }
//                if (TextUtils.isEmpty(clickurl)) {
//                    clickurl = "http://www.fjqxfw.com:8099/gz_wap/";
//                }
//
//                UMImage  mMedia = new UMImage(ActivityCommitPicMV.this, bitmap);
//                UMWeb web = new UMWeb(clickurl);
//                web.setTitle("知天气分享");//标题
//                web.setThumb(mMedia);  //缩略图
//                web.setDescription(shareStr);//描述
////                ShareContent shareContent = new ShareContent();
////                shareContent.mTitle = "知天气分享";
////                shareContent.mTargetUrl = url;
////                if (cutMedia == SHARE_MEDIA.QZONE) {
////                    shareContent.mText = shareStr;
////                } else if (cutMedia == SHARE_MEDIA.QQ) {
////                    shareContent.mText = shareStr;
////                } else if (cutMedia == SHARE_MEDIA.WEIXIN_CIRCLE) {
////                    shareContent.mText = shareStr;
////                } else if (cutMedia == SHARE_MEDIA.WEIXIN) {
////                    shareContent.mText = shareStr;
////                } else if (cutMedia == SHARE_MEDIA.SINA) {
////                    shareContent.mText = shareStr;
////                }
//                new ShareAction(ActivityCommitPicMV.this)
//                        .setPlatform(cutMedia)
//                        .withMedia(web)
//                        .setCallback(umShareListenr)
//                        .share();
//            }
//        }
//    };
//    private UMShareListener umShareListenr = new UMShareListener() {
//        @Override
//        public void onStart(SHARE_MEDIA share_media) {
//
//        }
//
//        @Override
//        public void onResult(SHARE_MEDIA share_media) {
//            Toast.makeText(ActivityCommitPicMV.this, "分享成功。", Toast.LENGTH_SHORT).show();
//            dismissProgressDialog();
////            showProgressDialog();
////            commitShareResult(share_media);
//        }
//
//        @Override
//        public void onError(SHARE_MEDIA share_media, Throwable throwable) {
//            dismissProgressDialog();
//            Toast.makeText(ActivityCommitPicMV.this, "分享失败", Toast.LENGTH_SHORT).show();
//        }
//
//        @Override
//        public void onCancel(SHARE_MEDIA share_media) {
//            dismissProgressDialog();
//        }
//    };
//
//    private void showPopShare() {
//        View view = LayoutInflater.from(ActivityCommitPicMV.this).inflate(R.layout.pop_share_windown, null);
//        GridView share_face = (GridView) view.findViewById(R.id.share_face);
//        AdapterShareGraiView adapter = new AdapterShareGraiView();
//        share_face.setAdapter(adapter);
//        share_face.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                if (position == 0) {
//                    shareClick(SHARE_MEDIA.WEIXIN);
//                } else if (position == 1) {
//                    shareClick(SHARE_MEDIA.WEIXIN_CIRCLE);
//                } else if (position == 2) {
//                    shareClick(SHARE_MEDIA.SINA);
//                } else if (position == 3) {
//                    shareClick(SHARE_MEDIA.QZONE);
//                }
//                dialog.dismiss();
//            }
//        });
//
////        AlertDialog.Builder build= new AlertDialog.Builder(ActivityMainBannerWebView.this);
////        build.setView(view, 0, 0, 0, 0);
//        dialog = new AlertDialog.Builder(ActivityCommitPicMV.this).create();
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setView(view, 0, 0, 0, 0);
//        dialog.show();
//        dialog.show();
//    }
//
//    public void shareClick(SHARE_MEDIA p) {
//        handler.sendEmptyMessage(1);
//        cutMedia = p;
//    }
//
//    private AlertDialog dialog;
//    private PcsDataBrocastReceiver mReceiver = new PcsDataBrocastReceiver() {
//        @Override
//        public void onReceive(String nameStr, String errorStr) {
//            if (mPackPicUp.getName().equals(nameStr)) {
//                dismissProgressDialog();
//                if (!TextUtils.isEmpty(errorStr)) {
//                    return;
//                }
//                PackUserPictureDown down = (PackUserPictureDown) PcsDataManager.getInstance().getNetPack(nameStr);
//                if (down != null) {
//                    if (down.result.equals("1")) {
//                        webview.loadUrl("javascript:uploadImgCallback('" + down.url + "')");
//                        showToast("图片提交完成");
//                    } else {
//                        showToast(down.result_msg);
//                    }
////                    Toast.makeText(ActivityMainBannerWebView.this, "分享记录提交成功。", Toast.LENGTH_SHORT).show();
//                } else {
//                    showToast("图片提交失败");
//                }
//            } else if (mPackMusicUp.getName().equals(nameStr)) {
//                dismissProgressDialog();
//                if (!TextUtils.isEmpty(errorStr)) {
//                    return;
//                }
//                PackCommitMoviceDown down = (PackCommitMoviceDown) PcsDataManager.getInstance().getNetPack(nameStr);
//                if (down != null) {
//                    if (down.result.equals("1")) {
//                        webview.loadUrl("javascript:uploadVideoCallback(" + down.vid_id + ")");
//                        showToast("视频提交完成");
//                    } else {
//                        showToast(down.result_msg);
//                    }
//                } else {
//                    showToast("视频提交失败");
//                }
//            }
//        }
//    };
//
//
////    uploadImgCallback("img_path")头像上传回调
////    uploadVideoCallback();视频上传回调
//}
