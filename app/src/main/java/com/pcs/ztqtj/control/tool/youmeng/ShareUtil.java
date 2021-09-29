package com.pcs.ztqtj.control.tool.youmeng;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.Toast;

import com.pcs.lib.lib_pcs_v3.control.tool.BitmapUtil;
import com.pcs.ztqtj.util.CONST;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.ShareContent;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

public class ShareUtil {
    /**
     * 分享
     */
    public static void share(Activity activity, String content) {
        Bitmap bitmap = BitmapUtil.takeScreenShot(activity);
        ShareUtil.shareContent(activity, "天津气象", content, bitmap);
    }

    /**
     * 分享
     */
    public static void share(Activity activity) {
        Bitmap bitmap = BitmapUtil.takeScreenShot(activity);
        shareContent(activity, bitmap);
    }

    /**
     * 分享
     */
    public static void share(final Activity activity, String content, Bitmap bitmap) {
        ShareUtil.shareContent(activity, content, content, bitmap);
    }

    /**
     * 分享(带url)
     * @param activity
     * @param content
     * @param url
     * @param bitmap
     */
    public static void share(final Activity activity, String content, String url, Bitmap bitmap) {
        ShareUtil.shareContent(activity, "天津气象分享", content, url, bitmap);
    }

    /**
     * 微博分享
     *
     * @param activity
     * @param content  分享内容
     * @param bitmap   分享图片
     * @param url      分享网页链接
     */
    public static void weiboShare(Activity activity, String content, Bitmap bitmap, String url) {
//        TextObject textObject = createTextObject(content);
//        ImageObject imageObject = createImageObject(bitmap);
//        WebpageObject mediaObject = createWebpageObject(activity, url);
//        Intent intent = new Intent(activity, WeiboShareActivity.class);
//        intent.putExtra("text", textObject);
//        intent.putExtra("image", imageObject);
//        intent.putExtra("media", mediaObject);
//        activity.startActivity(intent);
    }
//
//    /**
//     * 创建文本消息对象
//     *
//     * @param content
//     * @return
//     */
//    private static TextObject createTextObject(String content) {
//        if (TextUtils.isEmpty(content)) {
//            return null;
//        }
//
//        TextObject textObject = new TextObject();
//        textObject.text = content;
//        return textObject;
//    }

//    /**
//     * 创建图片消息对象。
//     *
//     * @param bitmap
//     * @return
//     */
//    private static ImageObject createImageObject(Bitmap bitmap) {
//        if (bitmap == null) {
//            return null;
//        }
//        ImageObject imageObject = new ImageObject();
//        imageObject.setImageObject(bitmap);
//        return imageObject;
//    }

//    /**
//     * 创建多媒体（网页）消息对象。
//     *
//     * @param url
//     * @return
//     */
//    private static WebpageObject createWebpageObject(Context context, String url) {
//        if (TextUtils.isEmpty(url)) {
//            return null;
//        }
//        WebpageObject mediaObject = new WebpageObject();
//        mediaObject.identify = Utility.generateGUID();
//        mediaObject.title = "分享链接";
//        mediaObject.description = "description";
//
//        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
//        // 设置 Bitmap 类型的图片到视频对象里         设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
//        mediaObject.setThumbImage(bitmap);
//        mediaObject.actionUrl = url;
//        mediaObject.defaultText = "defaultText";
//        return mediaObject;
//    }

    /**
     * 分享到短信
     *
     * @param conetxt 上下文
     * @param content 内容
     */
    public static void shareToSMS(Context conetxt, String content, String number) {
        Uri uri = Uri.parse("smsto:" + number);
        Intent sendIntent = new Intent(Intent.ACTION_SENDTO, uri);
        sendIntent.putExtra("sms_body", content);
        sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        conetxt.startActivity(sendIntent);
    }

    private static UMShareListener umShareListenr = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA share_media) {

        }

        @Override
        public void onResult(SHARE_MEDIA share_media) {
//            Toast.makeText(context, "success", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(SHARE_MEDIA share_media, Throwable throwable) {
            Toast.makeText(context, "分享失败", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA share_media) {

        }
    };

    private static Context context;

    private static void shareContent(Activity activity, String title, String contentstr, Bitmap shareImage) {
        context = activity;
        final SHARE_MEDIA[] displaylist = new SHARE_MEDIA[]{SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ, SHARE_MEDIA.SMS};

        UMWeb web = new UMWeb(CONST.SHARE_URL);
        web.setTitle(title);//标题
        web.setThumb(new UMImage(activity,shareImage));  //缩略图
        web.setDescription(contentstr);//描述

        ShareAction umSharhAction = new ShareAction(activity);
//        umSharhAction.withTitle(title);
        umSharhAction.setDisplayList(displaylist);
        umSharhAction.withMedia(web);
//        umSharhAction.setContentList(weixin, circle, sina, sms);
        umSharhAction.setListenerList(umShareListenr);
        umSharhAction.open();
    }

    private static void shareContent(Activity activity, String title, String contentstr, String shareUrl, Bitmap shareImage) {
        context = activity;
        final SHARE_MEDIA[] displaylist = new SHARE_MEDIA[]{SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ, SHARE_MEDIA.SMS};
        ShareContent weixin = new ShareContent();
        ShareContent circle = new ShareContent();
        ShareContent sina = new ShareContent();
        ShareContent sms = new ShareContent();
        if (contentstr != null) {
            weixin.mText = contentstr;
//            circle.mText = contentstr;
            sina.mText = contentstr;
            sms.mText = contentstr;
        }
        if (shareImage != null) {
            UMImage umImage = new UMImage(activity, shareImage);
            weixin.mMedia = umImage;
            circle.mMedia = umImage;
            sina.mMedia = umImage;
            sms.mMedia = umImage;
        }
//      String url = "http://sns.whalecloud.com/sina2/callback";
//        weixin.mTargetUrl = shareUrl;
//        circle.mTargetUrl = shareUrl;
//        sina.mTargetUrl = shareUrl;
//        sms.mTargetUrl = shareUrl;

        ShareAction umSharhAction = new ShareAction(activity);
//        umSharhAction.withTitle(title);
        umSharhAction.setDisplayList(displaylist);
        umSharhAction.setContentList(weixin, circle, sina, sms);
        umSharhAction.setListenerList(umShareListenr);
        umSharhAction.open();
    }

    private static void shareContent(Activity activity, String contentstr) {
        shareContent(activity, "天津气象", contentstr, null);
    }

    private static void shareContent(Activity activity, Bitmap shareImage) {
        shareContent(activity, "天津气象", null, shareImage);
    }

    //自定义分享代码
    public static void autoShare(Activity activity, SHARE_MEDIA platform, String context,String title,String clickPath,Bitmap bm) {
        UMImage umImage = new UMImage(activity, bm);
        UMWeb  web = new UMWeb(CONST.SHARE_URL);
        web.setTitle(title);//标题
        web.setThumb(umImage);  //缩略图
        web.setDescription(context);//描述
        new ShareAction(activity)
                .setPlatform(platform)
                .setCallback(umShareListenr)
                .withText(context)
//                .withTitle(title)
//                .withTargetUrl(clickPath)
                .withMedia(web)
                .share();
    }

    //分享返回
    public static void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        UMShareAPI.get(activity).onActivityResult(requestCode, resultCode, data);
    }


}
