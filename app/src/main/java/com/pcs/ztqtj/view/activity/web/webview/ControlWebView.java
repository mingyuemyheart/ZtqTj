package com.pcs.ztqtj.view.activity.web.webview;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.pcs.lib.lib_pcs_v3.control.file.PcsGetPathValue;
import com.pcs.lib.lib_pcs_v3.control.file.PcsMD5;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib.lib_pcs_v3.model.image.ImageResizer;
import com.pcs.lib.lib_pcs_v3.model.pack.PcsPackUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackCommitMoviceDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackCommitMoviceUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackInitDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackInitUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackUserPictureDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackUserPictureUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.banner_share.PackShareToServerDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.banner_share.PackShareToServerUp;
import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.AdapterShareGraiView;
import com.pcs.ztqtj.control.tool.CommUtils;
import com.pcs.ztqtj.control.tool.FileUtils;
import com.pcs.ztqtj.control.tool.KWHttpRequest;
import com.pcs.ztqtj.control.tool.MyConfigure;
import com.pcs.ztqtj.control.tool.ZtqImageTool;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.view.activity.cammer.ActivityVideo;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import java.io.File;
import java.util.Random;

public class ControlWebView {
    public final int resultCode = 11121;
    private ActivityWebView activity;

    public ControlWebView(ActivityWebView activity, KWHttpRequest.KwHttpRequestListener listener) {
        this.activity = activity;
        init();
    }

    public void init() {
        PcsDataBrocastReceiver.registerReceiver(activity, mReceiver);

        initPopupWindow();
        initPopupWindow2();
    }

    public void destory() {
        PcsDataBrocastReceiver.unregisterReceiver(activity, mReceiver);
    }

    public void commitFile(String filePath, PcsPackUp packUp) {

    }

    private PcsDataBrocastReceiver mReceiver = new PcsDataBrocastReceiver() {
        @Override
        public void onReceive(String nameStr, String errorStr) {
            if (packShareToServerUp != null && packShareToServerUp.getName().equals(nameStr)) {
                activity.dismissProgressDialog();
                PackShareToServerDown down = (PackShareToServerDown) PcsDataManager.getInstance().getNetPack(packShareToServerUp.getName());
                if (down != null && down.result.equals("1")) {
//                    Toast.makeText(ActivityMainBannerWebView.this, "分享记录提交成功。", Toast.LENGTH_SHORT).show();
                    activity.loadUrl("javascript:shareUrlAndContentCallback('1')");
                } else {
                    showToast("分享提交失败。");
                    activity.loadUrl("javascript:shareUrlAndContentCallback('0')");
                }
            } else if (mPackPicUp.getName().equals(nameStr)) {
                activity.dismissProgressDialog();
                if (!TextUtils.isEmpty(errorStr)) {
                    showToast(errorStr);
                    return;
                }
                PackUserPictureDown down = (PackUserPictureDown) PcsDataManager.getInstance().getNetPack(nameStr);
                if (down != null) {
                    if (down.result.equals("1")) {
                        activity.loadUrl("javascript:uploadImgCallback('" + down.url + "')");
                        showToast("图片提交完成");
                    } else {
                        showToast(down.result_msg);
                    }
                } else {
                    showToast("图片提交失败");
                }
            } else if (mPackMusicUp.getName().equals(nameStr)) {
                activity.dismissProgressDialog();
                if (!TextUtils.isEmpty(errorStr)) {
                    showToast(errorStr);
                    return;
                }
                PackCommitMoviceDown down = (PackCommitMoviceDown) PcsDataManager.getInstance().getNetPack(nameStr);
                if (down != null) {
                    if (down.result.equals("1")) {
                        activity.loadUrl("javascript:uploadVideoCallback(" + down.vid_id + ")");
                        showToast("视频提交完成");
                    } else {
                        showToast(down.result_msg);
                    }
                } else {
                    showToast("视频提交失败");
                }
            }
        }
    };

    private Toast toast;

    public void showToast(String str) {
        if (toast == null) {
            toast = Toast.makeText(activity, str,
                    Toast.LENGTH_SHORT);
        } else {
            toast.setText(str);
        }
        toast.show();
    }

    // 图片最大长度
    private final int PHOTO_MAX_PENGTH = 1920;
    // 相册
    private final int REQUEST_ALBUM = 101;
    // 拍照
    private final int REQUEST_CAMERA = 102;
    // 拍照
    private final int REQUEST_MOVICE = 103;

    public void onActivityResult(int arg0, int arg1, Intent arg2) {
        UMShareAPI.get(activity).onActivityResult(arg0, arg1, arg2);
        if (arg1 != Activity.RESULT_OK) {
            return;
        }
        switch (arg0) {
            case REQUEST_ALBUM:
                // 相册
                resultAlbum(arg2);
                break;
            case REQUEST_CAMERA:
                // 相机
                resultCamera(arg2);
                break;
            case REQUEST_MOVICE:
                // 视频
                Uri uri = arg2.getData();
                String path = FileUtils.getPath(activity, uri);
                activity.showProgressDialog();
                commitFile(path, mPackMusicUp);
                break;
            case 1111:
                activity.showProgressDialog();
                String file = arg2.getStringExtra("file");
                commitFile(file, mPackMusicUp);
                break;
            case resultCode:
                //         判断是否登录成功。成功则
                if (ZtqCityDB.getInstance().isLoginService()) {
                    String userId = "?USER_ID=" + MyApplication.UID;
                    String pid = "&PID=";
                    PackInitDown packDown = (PackInitDown) PcsDataManager.getInstance().getNetPack(PackInitUp.NAME);
                    if (packDown != null) {
                        pid += packDown.pid;
                    }
                    activity.loadUrl(activity.url + userId + pid);
                }
                break;
        }
    }

    /**
     * 从相机返回
     */
    private void resultCamera(Intent fromIntent) {
        if (mFilePhoto == null || !mFilePhoto.exists()) {
            Toast.makeText(activity, R.string.photo_error, Toast.LENGTH_SHORT).show();
            return;
        }
        // 调整图片大小
        resizeImage();
    }

    /**
     * 从相册返回
     *
     * @param fromIntent
     */
    private void resultAlbum(Intent fromIntent) {
        activity.showProgressDialog();
        Uri uri = fromIntent.getData();
        String[] filePathColumns = {MediaStore.Images.Media.DATA};
        Cursor c = activity.getContentResolver().query(uri, filePathColumns, null, null, null);
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
            Toast.makeText(activity, R.string.photo_error, Toast.LENGTH_SHORT).show();
            activity.dismissProgressDialog();
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
        activity.getWindowManager().getDefaultDisplay().getSize(point);
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
            commitFile(mFilePhoto.getPath(), mPackPicUp);
        }
    };
    // 图片文件
    private File mFilePhoto = null;


    //___________________________分享_________________________
    private PackShareToServerUp packShareToServerUp;
    private SHARE_MEDIA cutMedia;

    public void shareClick(String act_id, String type) {
        packShareToServerUp = new PackShareToServerUp();
        packShareToServerUp.act_id = act_id;
        packShareToServerUp.type = type;
        handler.sendEmptyMessage(0);
    }

    /**
     * 分享网页链接
     *
     * @param url     分享链接
     * @param title   分享标题
     * @param content 分享内容
     * @param bitmap  缩略图
     * @param media   分享类型
     */
    public void shareWeb(String url, String title, String content, Bitmap bitmap, SHARE_MEDIA media) {
        UMWeb umWeb = new UMWeb(url);
        umWeb.setTitle(title);
        umWeb.setDescription(content);
        umWeb.setThumb(new UMImage(activity, bitmap));
        new ShareAction(activity)
                .setPlatform(media)
                .setCallback(umShareListenr)
                .withMedia(umWeb)
                .withText(content)
                .share();
    }


    /**
     * 分享图片
     *
     * @param bitmap
     * @param media
     */
    public void shareWithImage(Bitmap bitmap, SHARE_MEDIA media) {
        UMImage image = new UMImage(activity, bitmap);
        new ShareAction(activity)
                .setPlatform(media)
                .setCallback(umShareListenr)
                //.withTitle("天津气象决策版分享")
                //.withTargetUrl(mShareUrl)
                .withMedia(image)
                .share();
    }


    /**
     * 分享图片加文字
     *
     * @param content
     * @param bitmap
     * @param media
     */
    public void shareWithImageAndText(String content, Bitmap bitmap, SHARE_MEDIA media) {
        UMImage image = new UMImage(activity, bitmap);
        image.setThumb(new UMImage(activity, R.drawable.ic_launcher));
        image.setDescription(content);
        image.setTitle(content);
        new ShareAction(activity)
                .setPlatform(media)
                .setCallback(umShareListenr)
                .withText(content)
                //.withTitle("天津气象决策版分享")
                //.withTargetUrl(mShareUrl)
                .withMedia(image)
                .share();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            activity.dismissProgressDialog();
            if (what == 0) {
                showPopShare();
            } else if (what == 1) {
//                String shareStr = activity.getShareContent();
                Bitmap bitmap = ZtqImageTool.getInstance().screenshotWebView(activity.webview);
                if (bitmap == null) {
                    bitmap = BitmapFactory.decodeResource(activity.getResources(), R.drawable.ic_launcher);
                }

//                String shareClickUrl = "";
//                if (!TextUtils.isEmpty(activity.shareContent)) {
//                    shareClickUrl = activity.shareContent.substring(activity.shareContent.indexOf("http"), activity.shareContent.length());
//                }
                if (TextUtils.isEmpty(activity.url)) {
                    activity.url = CONST.SHARE_URL;
                }

//                activity.url
                if (cutMedia == SHARE_MEDIA.SINA) {
                    // 新浪微博
                    shareWithImageAndText(activity.shareContent, bitmap, SHARE_MEDIA.SINA);
//                    shareWithImageAndText(activity.shareContent + activity.url, bitmap, SHARE_MEDIA.SINA);
                } else if (cutMedia == SHARE_MEDIA.WEIXIN) {
                    // 微信
                    shareWeb(activity.url, "【天津气象决策版分享】 ", activity.shareContent, bitmap, SHARE_MEDIA.WEIXIN);
                } else if (cutMedia == SHARE_MEDIA.WEIXIN_CIRCLE) {
                    // 朋友圈
//                    shareWithImage(bitmap, SHARE_MEDIA.WEIXIN_CIRCLE);
                    shareWeb(activity.url, "【天津气象决策版分享】 "+activity.pagetitle , activity.shareContent+activity.url, bitmap, SHARE_MEDIA.WEIXIN_CIRCLE);
                } else if (cutMedia == SHARE_MEDIA.QZONE) {
                    // qq空间
                    shareWeb(activity.url, "【天津气象决策版分享】 " + activity.pagetitle, activity.shareContent+activity.url, bitmap, SHARE_MEDIA.QZONE);
                }

//                UMImage mMedia = new UMImage(activity, bitmap);
//                UMWeb web = new UMWeb(activity.url);
//                web.setTitle("天津气象分享");//标题
//                web.setThumb(mMedia);  //缩略图
//                web.setDescription(shareStr);//描述
//                new ShareAction(activity)
//                        .setPlatform(cutMedia)
//                        .withMedia(web)
//                        .setCallback(umShareListenr)
//                        .share();
            }
        }
    };

    //提交分享结果到服务器
    public void commitShareResult(SHARE_MEDIA share_media) {
        packShareToServerUp.user_id = MyApplication.UID;
        Random rand = new Random();
        packShareToServerUp.req_mid = System.currentTimeMillis() + "" + (1000 + rand.nextInt(8999));
        if (share_media == SHARE_MEDIA.QZONE) {
            packShareToServerUp.fx_qd = "2";
        } else if (share_media == SHARE_MEDIA.QQ) {
            packShareToServerUp.fx_qd = "2";
        } else if (share_media == SHARE_MEDIA.WEIXIN_CIRCLE) {
            packShareToServerUp.fx_qd = "3";
        } else if (share_media == SHARE_MEDIA.WEIXIN) {
            packShareToServerUp.fx_qd = "3";
        } else if (share_media == SHARE_MEDIA.SINA) {
            packShareToServerUp.fx_qd = "1";
        }
        packShareToServerUp.md5 = PcsMD5.Md5(packShareToServerUp.user_id + packShareToServerUp.req_mid + "pcs_ztq");
        PcsDataDownload.addDownload(packShareToServerUp);
    }

    private UMShareListener umShareListenr = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA share_media) {
        }

        @Override
        public void onResult(SHARE_MEDIA share_media) {
            Toast.makeText(activity, "分享成功。", Toast.LENGTH_SHORT).show();
//            activity.dismissProgressDialog();
            //分享完成提交数据
            activity.commitShareResult(share_media);
        }

        @Override
        public void onError(SHARE_MEDIA share_media, Throwable throwable) {
            activity.dismissProgressDialog();
            Toast.makeText(activity, "分享失败", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA share_media) {
            activity.dismissProgressDialog();
        }
    };

    private void showPopShare() {
        View view = LayoutInflater.from(activity).inflate(R.layout.pop_share_windown, null);
        GridView share_face = (GridView) view.findViewById(R.id.share_face);
        AdapterShareGraiView adapter = new AdapterShareGraiView();
        share_face.setAdapter(adapter);
        share_face.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                if (position == 0) {
//                    shareClick(SHARE_MEDIA.WEIXIN);
//                } else if (position == 1) {
//                    shareClick(SHARE_MEDIA.WEIXIN_CIRCLE);
//                } else if (position == 2) {
//                    shareClick(SHARE_MEDIA.SINA);
//                } else if (position == 3) {
//                    shareClick(SHARE_MEDIA.QZONE);
//                }
                if (position == 0) {
                    shareClick(SHARE_MEDIA.WEIXIN_CIRCLE);
                } else if (position == 1) {
                    shareClick(SHARE_MEDIA.SINA);
                } else if (position == 2) {
                    shareClick(SHARE_MEDIA.QZONE);
                }
                dialog.dismiss();
            }
        });
        dialog = new AlertDialog.Builder(activity).create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setView(view, 0, 0, 0, 0);
        dialog.show();
        dialog.show();
    }

    private AlertDialog dialog;

    public void shareClick(SHARE_MEDIA p) {
        handler.sendEmptyMessage(1);
        cutMedia = p;
    }
    //____________________________________________________


//  ————————————拍照————————————————————
// 弹出框
    /**
     * 上传包
     */
    private PackUserPictureUp mPackPicUp = new PackUserPictureUp();
    private PackCommitMoviceUp mPackMusicUp = new PackCommitMoviceUp();

    //文件选择器
    public void showFileChooser(String user_id, String act_id) {
        mPackMusicUp.act_id = act_id;
        mPackMusicUp.user_id = user_id;

        String status = Environment.getExternalStorageState();
        if (!status.equals(Environment.MEDIA_MOUNTED)) {
            showToast(activity.getString(R.string.no_sdcard));
            return;
        }
        mPopupWindowMovice.showAtLocation(activity.webview, Gravity.BOTTOM, 0, 0);
    }

    /**
     * 点击拍照
     */
    public void showPowChose(String act_id) {
        mPackPicUp.act_id = act_id;
        String status = Environment.getExternalStorageState();
        if (!status.equals(Environment.MEDIA_MOUNTED)) {
            showToast(activity.getString(R.string.no_sdcard));
            return;
        }
        mPopupWindowPhoto.showAtLocation(activity.webview, Gravity.BOTTOM, 0, 0);
    }

    private PopupWindow mPopupWindowMovice;
    // 弹出框
    private PopupWindow mPopupWindowPhoto;

    /**
     * 初始化弹出框
     */
    private void initPopupWindow() {
        View view = LayoutInflater.from(activity).inflate(R.layout.pop_photograph, null);
        mPopupWindowPhoto = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);
        mPopupWindowPhoto.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                // 设置背景颜色变暗
                try {
                    WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
                    lp.alpha = 1.0f;
                    activity.getWindow().setAttributes(lp);
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
        btn.setOnClickListener(clickListener);
        // 相册按钮
        btn = view.findViewById(R.id.btnAlbum);
        btn.setOnClickListener(clickListener);
        // 取消按钮
        btn = view.findViewById(R.id.btnCancel);
        btn.setOnClickListener(clickListener);
    }

    /**
     * 初始化弹出框
     */
    private void initPopupWindow2() {
        View view = LayoutInflater.from(activity).inflate(R.layout.pop_photograph2, null);
        mPopupWindowMovice = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);
        mPopupWindowMovice.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                // 设置背景颜色变暗
                try {
                    WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
                    lp.alpha = 1.0f;
                    activity.getWindow().setAttributes(lp);
                } catch (Exception e) {
                }
            }
        });
        // 设置可以获得焦点
        mPopupWindowMovice.setFocusable(true);
        // 设置弹窗内可点击
        mPopupWindowMovice.setTouchable(true);
        // 设置弹窗外可点击
        mPopupWindowMovice.setOutsideTouchable(true);

        mPopupWindowMovice.setBackgroundDrawable(new BitmapDrawable());

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
        btn = view.findViewById(R.id.btnCamera2);
        btn.setOnClickListener(clickListener);
        // 相册按钮
        btn = view.findViewById(R.id.btnAlbum2);
        btn.setOnClickListener(clickListener);
        // 取消按钮
        btn = view.findViewById(R.id.btnCancel2);
        btn.setOnClickListener(clickListener);
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
     * 关闭弹出框
     */
    private void dismissPopupWindow2() {
        if (mPopupWindowMovice != null && mPopupWindowMovice.isShowing()) {
            mPopupWindowMovice.dismiss();
        }
    }

    /**
     * 点击照相
     */
    public void clickCamera() {
        dismissPopupWindow();
        String tempStr = String.valueOf(System.currentTimeMillis());
        mFilePhoto = new File(PcsGetPathValue.getInstance().getMyPhotoPath() + tempStr + ".jpg");
        mFilePhoto.getParentFile().mkdirs();
        CommUtils.openCamera(activity, mFilePhoto, MyConfigure.REQUEST_CAMERA);
    }

    /**
     * 点击相册
     */
    private void clickAlbum() {
        dismissPopupWindow();
        Intent it = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(it, REQUEST_ALBUM);
    }


    private View.OnClickListener clickListener = new View.OnClickListener() {
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
                case R.id.btnCamera2:
                    // 录像
                    showFileChooserSelf();
                    break;
                case R.id.btnAlbum2:
                    // 本地
                    showFileChooserDir();
                    break;
                case R.id.btnCancel2:
                    //取消
                    dismissPopupWindow2();

            }
        }
    };

    //文件选择器
    public void showFileChooserDir() {
        dismissPopupWindow2();
        Intent it = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(it, REQUEST_MOVICE);
    }

    //文件选择器
    public void showFileChooserSelf() {
        dismissPopupWindow2();
        Intent intent = new Intent(activity, ActivityVideo.class);
        activity.startActivityForResult(intent, 1111);
    }

//————————————————————————————————————-
//-------------------分享提交---------------------------


}
