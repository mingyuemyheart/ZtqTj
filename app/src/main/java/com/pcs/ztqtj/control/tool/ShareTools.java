package com.pcs.ztqtj.control.tool;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.AdapterShareTool;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 分享工具类
 * Created by tyaathome on 2016/9/21.
 */
public class ShareTools {

    private PopupWindow mPopupWindow;
    private AdapterShareTool adapter;
    private Toast mToast;
    private ShareOnItemClickListener listener;
    // 分享类型列表
    private int[] iconList = {R.drawable.icon_phone, R.drawable.icon_moments,
            R.drawable.icon_wechat, R.drawable.icon_mms,
            R.drawable.icon_qzone, R.drawable.icon_weibo};
    private int[] nameList = {R.string.share_tool_phone, R.string.share_tool_moments,
            R.string.share_tool_wechat, R.string.share_tool_mms,
            R.string.share_tool_qzone, R.string.share_tool_weibo};

    private static ShareTools instance;
    private static Context mContext;
    private String mShare = "";
    private Bitmap mBitmap;
    private String mShareUrl = "https://tjhm-app.weather.com.cn:8081/web/build/index.html";
    private String mTitle = "";
    private static ShareCallBackListener shareCallBackListener;

    public ShareTools(Context context) {
        mContext = context;
        initWindow(context);
    }

    public static ShareTools getInstance(Context context) {
//        if(instance == null) {
//            instance = new ShareTools(context);
//        }
        if (instance == null || (context != null && mContext != context)) {
            instance = new ShareTools(context);
        }
        return instance;
    }

    /**
     * 显示弹窗
     */
    public ShareTools showWindow(View layout) {
//        Activity activity = (Activity) mContext;
//        View layout = activity.findViewById(android.R.id.content).getRootView();
        if (mPopupWindow != null && layout != null) {
            mPopupWindow.showAtLocation(layout, Gravity.BOTTOM, 0, 0);
        }
        return instance;
    }

    private String flags = "";

    /**
     * 设置分享内容
     *
     * @param share
     * @param shareBitmap flag==1 QQ分享图片 其他值为发送链接  仅QQ
     * @return
     */
    public ShareTools setShareContent(String title, String share, Bitmap shareBitmap, String flag) {
        mTitle = title;
        mShare = share;
        mBitmap = shareBitmap;
        flags = flag;
        return instance;
    }

    /**
     * 设置分享内容
     *
     * @param share
     * @param shareBitmap flag==1 QQ分享图片 其他值为发送链接  仅QQ
     * @return
     */
    public ShareTools setShareContent(String share, Bitmap shareBitmap, String flag) {
        mShare = share;
        mBitmap = shareBitmap;
        flags = flag;
        return instance;
    }

    /**
     * 设置分享内容
     *
     * @param shareContent
     * @param url
     * @param shareBitmap
     * @return
     */
    public ShareTools setShareContent(String title, String shareContent, String url, Bitmap shareBitmap) {
        this.mTitle = title;
        this.mShare = shareContent;
        this.mBitmap = shareBitmap;
        return instance;
    }

    /**
     * 设置分享回调
     *
     * @param listener
     * @return
     */
    public ShareTools setShareCallBack(ShareCallBackListener listener) {
        this.shareCallBackListener = listener;
        return instance;
    }

    /**
     * 初始化弹窗
     *
     * @param context
     */
    public void initWindow(Context context) {
        View layout = LayoutInflater.from(context).inflate(R.layout.layout_share_popup, null);

        GridView gridView = (GridView) layout.findViewById(R.id.gridview);
        adapter = new AdapterShareTool(context, iconList, nameList);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(onItemClickListener);

        TextView tvCancel = (TextView) layout.findViewById(R.id.tv_cancel);
        tvCancel.setOnClickListener(onClickListener);

        mPopupWindow = new PopupWindow(layout, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);
        // 设置可以获得焦点
        mPopupWindow.setFocusable(true);
        // 设置弹窗内可点击
        mPopupWindow.setTouchable(true);
        // 设置弹窗外可点击
        mPopupWindow.setOutsideTouchable(true);

        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
    }

    public void dismissWindow() {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
    }

    public void dismissWindowOnMainThread() {
        Activity activity = (Activity) mContext;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dismissWindow();
            }
        });
    }

    /**
     * 分享图片
     *
     * @param str
     * @param bitmap
     * @param media
     */
    public void shareWithImage(String str, Bitmap bitmap, SHARE_MEDIA media) {
        UMImage image = new UMImage(mContext, bitmap);
        Activity activity = (Activity) mContext;
        new ShareAction(activity)
                .setPlatform(media)
                .setCallback(umShareListener)
                //.withTitle("知天气决策版分享")
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
    public static void shareWithImageAndText(String content, Bitmap bitmap, SHARE_MEDIA media) {
        UMImage image = new UMImage(mContext, bitmap);
        image.setThumb(new UMImage(mContext, R.drawable.ic_launcher));
        image.setDescription(content);
        image.setTitle(content);
        Activity activity = (Activity) mContext;
        new ShareAction(activity)
                .setPlatform(media)
                .setCallback(umShareListener)
                .withText(content)
                //.withTitle("知天气决策版分享")
                //.withTargetUrl(mShareUrl)
                .withMedia(image)
                .share();
    }

    /**
     * 分享网页链接
     *
     * @param url     分享链接
     * @param content 分享内容
     * @param bitmap  缩略图
     * @param media   分享类型
     */
    public static void shareWeb(String url, String content, Bitmap bitmap, SHARE_MEDIA media) {
        shareWeb(url, "天津气象分享", content, bitmap, media);
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
    public static void shareWeb(String url, String title, String content, Bitmap bitmap, SHARE_MEDIA media) {
        String[] str = url.split("http");
        UMWeb umWeb = new UMWeb("http" + str[1]);
        umWeb.setTitle(title);
        umWeb.setDescription(content);
        umWeb.setThumb(new UMImage(mContext, bitmap));
        Activity activity = (Activity) mContext;
        new ShareAction(activity)
                .setPlatform(media)
                .setCallback(umShareListener)
                .withMedia(umWeb)
                .withText(content)
                .share();
    }

    /**
     * 设置点击回调
     *
     * @param listener
     * @return
     */
    public ShareTools setShareClickListener(ShareOnItemClickListener listener) {
        this.listener = listener;
        return instance;
    }

    /**
     * 跳转拨号页面
     */
    public static void gotoDial() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        mContext.startActivity(intent);
    }

    /**
     * 跳转彩信页面
     */
    private void gotoMMS() {

        /* Attach Url is local (!) URL to file which should be sent */
        String strAttachUrl = "file://" + bitmapToFile(mBitmap).getPath();

        /* Attach Type is a content type of file which should be sent */
        String strAttachType = "image/png";

        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setClassName(mContext, "com.android.mms.ui.ComposeMessageActivity");
        sendIntent.putExtra("sms_body", mShare);

        /* Adding The Attach */
        sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(strAttachUrl));
        sendIntent.setType(strAttachType);

        sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        mContext.startActivity(sendIntent);

    }

    /**
     * 图片转file
     *
     * @param bitmap
     * @return
     */
    private File bitmapToFile(Bitmap bitmap) {
        Bitmap bm = bitmap;
        File f = new File(mContext.getCacheDir(), String.valueOf(System.currentTimeMillis()));
        try {
            f.createNewFile();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.PNG, 100, bos);
            byte[] bitmapdata = bos.toByteArray();
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return f;
    }

    private static UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA share_media) {

        }

        @Override
        public void onResult(SHARE_MEDIA share_media) {
            //showToast("success!");
            Toast.makeText(mContext, "分享成功", Toast.LENGTH_SHORT).show();
            if (shareCallBackListener != null) {
                shareCallBackListener.onSuccess();
            }
        }

        @Override
        public void onError(SHARE_MEDIA share_media, Throwable throwable) {
            Toast.makeText(mContext, "分享失败", Toast.LENGTH_SHORT).show();
            if (shareCallBackListener != null) {
                shareCallBackListener.onError();
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA share_media) {
            Toast.makeText(mContext, "分享取消", Toast.LENGTH_SHORT).show();
            if (shareCallBackListener != null) {
                shareCallBackListener.onCancle();
            }
        }
    };

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            String str = mContext.getString(nameList[position]);
//            Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), iconList[position]);
//            UMImage image = new UMImage(mContext, bitmap);
//            share(image);
            dismissWindowOnHandler();
            if (listener == null) {
                shareListener.onItemClick(position);
            } else {
                listener.onItemClick(position);
            }
        }
    };

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //dismissWindowOnMainThread();
            dismissWindowOnHandler();
            if (shareCallBackListener != null) {
                shareCallBackListener.onCancle();
            }
        }
    };

    /**
     * 分享回调
     */
    private ShareOnItemClickListener shareListener = new ShareOnItemClickListener() {
        @Override
        public void onItemClick(int position) {
            switch (position) {
                case 0:
                    // 电话
                    gotoDial();
                    break;
                case 1:
                    // 朋友圈
                    if (UMShareAPI.get(mContext).isInstall((Activity) mContext, SHARE_MEDIA.WEIXIN_CIRCLE)) {
                        shareWithImage(mShare, mBitmap, SHARE_MEDIA.WEIXIN_CIRCLE);
                    } else {
                        Toast.makeText(mContext, "未安装微信", Toast.LENGTH_LONG).show();
                    }
                    break;
                case 2:
                    // 微信
                    if (UMShareAPI.get(mContext).isInstall((Activity) mContext, SHARE_MEDIA.WEIXIN)) {
                        if (flags.equals("1")) {
                            shareWithImage(mShare, mBitmap, SHARE_MEDIA.WEIXIN);
                        } else {
                            shareWeb(mShareUrl, mTitle, mShare, mBitmap, SHARE_MEDIA.WEIXIN);
                        }
                    } else {
                        Toast.makeText(mContext, "未安装微信", Toast.LENGTH_LONG).show();
                    }
                    break;
                case 3:
                    // 彩信
                    shareWithImageAndText(mShare, mBitmap, SHARE_MEDIA.SMS);
                    //shareWeb("http://www.fjqxfw.com:8099/ztq_wap/", mShare, mBitmap, SHARE_MEDIA.SMS);
                    //gotoMMS();
                    break;
                case 4:
                    // qq空间
                    if (UMShareAPI.get(mContext).isInstall((Activity) mContext, SHARE_MEDIA.QQ)) {
                        shareWeb(mShareUrl, mTitle, mShare, mBitmap, SHARE_MEDIA.QZONE);
                    } else {
                        Toast.makeText(mContext, "未安装QQ", Toast.LENGTH_LONG).show();
                    }
                    break;
                case 5:
                    if (UMShareAPI.get(mContext).isInstall((Activity) mContext, SHARE_MEDIA.SINA)) {
                        // 新浪微博
                        shareWithImageAndText(mShare, mBitmap, SHARE_MEDIA.SINA);
                    } else {
                        Toast.makeText(mContext, "未安装新浪微博", Toast.LENGTH_LONG).show();
                    }
                    break;
            }
        }
    };


    /**
     * 分享点击的回调接口
     */
    public interface ShareOnItemClickListener {
        void onItemClick(int position);
    }

    /**
     * 分享回调接口
     */
    public interface ShareCallBackListener {
        void onSuccess();

        void onError();

        void onCancle();
    }

    private static final int COMPLETED = 0;

    //工作线程
    private class DismissThread extends Thread {
        @Override
        public void run() {
            //......处理比较耗时的操作

            //处理完成后给handler发送消息
            Message msg = new Message();
            msg.what = COMPLETED;
            mHandler.sendMessage(msg);
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == COMPLETED) {
                dismissWindow();
            }
        }
    };

    /**
     * 在handler中关闭分享窗口
     */
    private void dismissWindowOnHandler() {
        new DismissThread().start();
    }

    public enum ShareType {
        ShareImage,
        ShareWeb
    }
}
