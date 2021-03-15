package com.pcs.ztqtj.view.activity.web;//package com.pcs.knowing_weather.view.activity.web;
//
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.text.TextUtils;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.Window;
//import android.webkit.WebChromeClient;
//import android.webkit.WebSettings;
//import android.webkit.WebView;
//import android.webkit.WebViewClient;
//import android.widget.AdapterView;
//import android.widget.GridView;
//import android.widget.RelativeLayout;
//import android.widget.Toast;
//
//import com.pcs.knowing_weather.R;
//import com.pcs.knowing_weather.control.adapter.AdapterShareGraiView;
//import com.pcs.knowing_weather.control.tool.LoginInformation;
//import com.pcs.knowing_weather.control.tool.ZtqImageTool;
//import com.pcs.knowing_weather.view.activity.FragmentActivityZtqBase;
//import com.pcs.knowing_weather.view.activity.photoshow.ActivityPhotoLogin;
//import com.pcs.lib.lib_pcs_v3.control.file.PcsMD5;
//import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
//import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
//import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
//import com.pcs.lib_ztqfj_v2.model.pack.net.PackInitDown;
//import com.pcs.lib_ztqfj_v2.model.pack.net.PackInitUp;
//import com.pcs.lib_ztqfj_v2.model.pack.net.banner_share.PackShareToServerDown;
//import com.pcs.lib_ztqfj_v2.model.pack.net.banner_share.PackShareToServerUp;
//import com.umeng.socialize.ShareAction;
//import com.umeng.socialize.ShareContent;
//import com.umeng.socialize.UMShareAPI;
//import com.umeng.socialize.UMShareListener;
//import com.umeng.socialize.bean.SHARE_MEDIA;
//import com.umeng.socialize.media.UMImage;
//
//import java.util.Random;
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
//public class ActivityMainBannerWebView extends FragmentActivityZtqBase {
//    private WebView webview;
//    private String url = "http://218.85.78.125:8099/ztq_llhk/";
//    private RelativeLayout layout_content_web;
//
//    private String shareContent;
//
//
//    @Override
//    protected void onCreate(Bundle arg0) {
//        super.onCreate(arg0);
//        setContentView(R.layout.activity_webview);
//        initView();
//        initData();
//    }
//
//    private void initData() {
//        PcsDataBrocastReceiver.registerReceiver(this, mReceiver);
//        Intent intent = getIntent();
//        String pagetitle = intent.getStringExtra("title");
//        setTitleText(pagetitle);
//        url = intent.getStringExtra("url");
//        shareContent = intent.getStringExtra("shareContent");
//        try {
//            String userId = "?USER_ID=" + LoginInformation.getInstance().getUserId();
//            String pid = "&PID=";
//            PackInitDown packDown = (PackInitDown) PcsDataManager.getInstance().getNetPack(PackInitUp.NAME);
//            if (packDown != null) {
//                pid += packDown.pid;
//            }
//            String loadUrl = url + userId + pid;
//            webview.loadUrl(loadUrl);
//        } catch (Exception e) {
//        }
//
////        PcsDataDownload.addDownload(shareUp);
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        unregisterReceiver(mReceiver);
//    }
//
//
//    private PcsDataBrocastReceiver mReceiver = new PcsDataBrocastReceiver() {
//        @Override
//        public void onReceive(String nameStr, String errorStr) {
//            if (TextUtils.isEmpty(nameStr)) {
//                return;
//            }
//            if (!TextUtils.isEmpty(errorStr)) {
//                return;
//            }
//            if (packShareToServerUp != null && packShareToServerUp.getName().equals(nameStr)) {
//                dismissProgressDialog();
//                PackShareToServerDown down = (PackShareToServerDown) PcsDataManager.getInstance().getNetPack(packShareToServerUp.getName());
//                if (down != null && down.result.equals("1")) {
////                    Toast.makeText(ActivityMainBannerWebView.this, "分享记录提交成功。", Toast.LENGTH_SHORT).show();
//                    webview.loadUrl("javascript:shareCallback('1')");
//                } else {
//                    Toast.makeText(ActivityMainBannerWebView.this, "分享提交失败。", Toast.LENGTH_SHORT).show();
//                }
//            }
//        }
//    };
//
//    private void initView() {
//
//        layout_content_web = (RelativeLayout) findViewById(R.id.layout_content_web);
//
//        webview = (WebView) findViewById(R.id.webview);
//
//        webview.setWebViewClient(new WebViewClient() {
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                view.loadUrl(url);
//                return true;
//            }
//
//            @Override
//            public void onPageStarted(WebView view, String url, Bitmap favicon) {
//
//            }
//
//            @Override
//            public void onPageFinished(WebView view, String url) {
//
//            }
//        });
//
//        webview.setWebChromeClient(new WebChromeClient() {
//            @Override
//            public void onProgressChanged(WebView view, int progress) {
//
//                super.onProgressChanged(view, progress);
//            }
//
//            @Override
//            public void onReceivedTitle(WebView view, String title) {
//
//                super.onReceivedTitle(view, title);
//            }
//
//        });
//        WebSettings webSettings = webview.getSettings();
//        webSettings.setJavaScriptEnabled(true);//允许js
//        webSettings.setBlockNetworkImage(false);//后台处理加载图片
//        JavaScriptinterface interBanner = new JavaScriptinterface(this);
//        webview.addJavascriptInterface(interBanner, "js");
//        webview.setDrawingCacheEnabled(true);
//    }
//
//
//    @Override
//    protected void onActivityResult(int arg0, int arg1, Intent arg2) {
//        super.onActivityResult(arg0, arg1, arg2);
//        UMShareAPI.get(this).onActivityResult(arg0, arg1, arg2);
//        if (arg0 == resultCode && arg1 == Activity.RESULT_OK) {
////         判断是否登录成功。成功则
//            if (LoginInformation.getInstance().hasLogin()) {
//                String userId = "?USER_ID=" + LoginInformation.getInstance().getUserId();
//                String pid = "&PID=";
//                PackInitDown packDown = (PackInitDown) PcsDataManager.getInstance().getNetPack(PackInitUp.NAME);
//                if (packDown != null) {
//                    pid += packDown.pid;
//                }
//                webview.loadUrl(url + userId + pid);
//            }
//        }
//    }
//
////    public String getFromAssets(String fileName) {
////        try {
////            InputStreamReader inputReader = new InputStreamReader(
////                    getResources().getAssets().open(fileName));
////            BufferedReader bufReader = new BufferedReader(inputReader);
////            String line = "";
////            String Result = "";
////            while ((line = bufReader.readLine()) != null)
////                Result += line;
////            if (bufReader != null)
////                bufReader.close();
////            if (inputReader != null)
////                inputReader.close();
////            return Result;
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
////        return null;
////    }
//
//    @Override
//    public void onBackPressed() {
////        if (webview != null && webview.canGoBack()) {
////            webview.goBack();
////        } else {
//        finish();
////        }
//    }
//
//    private SHARE_MEDIA cutMedia;
//    private Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            int what = msg.what;
//            if (what == 0) {
//                showPopShare();
//            } else if (what == 1) {
//                String shareStr = "";
//                String clickurl = "";
//                if (!TextUtils.isEmpty(shareContent)) {
//                    shareStr = shareContent;
//                    int index = shareStr.indexOf("http:");
//                    if (index > 0) {
//                        clickurl = shareStr.substring(index, shareStr.length());
//                        shareStr = shareStr.substring(0, index);
//                    }
//                }
//                Bitmap bitmap = ZtqImageTool.getInstance().getScreenBitmap(webview);
//                if (TextUtils.isEmpty(clickurl)) {
//                    clickurl = "http://weather.ikan365.cn/";
//                }
//                ShareContent shareContent = new ShareContent();
////                shareContent.mTargetUrl = clickurl;
//                shareContent.mMedia = new UMImage(ActivityMainBannerWebView.this, bitmap);
//                if (cutMedia == SHARE_MEDIA.QZONE) {
//                    shareContent.mText = shareStr;
//                } else if (cutMedia == SHARE_MEDIA.QQ) {
//                    shareContent.mText = shareStr;
//                } else if (cutMedia == SHARE_MEDIA.WEIXIN_CIRCLE) {
//                    new ShareAction(ActivityMainBannerWebView.this)
//                            .withText(shareStr)
//                            .setPlatform(cutMedia)
////                            .withTitle("知天气")
////                            .withTargetUrl(clickurl)
//                            .setCallback(umShareListenr)
//                            .withMedia(new UMImage(ActivityMainBannerWebView.this, bitmap))
//                            .share();
//                    return;
//                } else if (cutMedia == SHARE_MEDIA.WEIXIN) {
//                    shareContent.mText = shareStr;
//                } else if (cutMedia == SHARE_MEDIA.SINA) {
//                    shareContent.mText = shareStr;
//                }
//                new ShareAction(ActivityMainBannerWebView.this)
//                        .setPlatform(cutMedia)
//                        .setShareContent(shareContent)
//                        .setCallback(umShareListenr)
//                        .share();
//            }
//
////                    new ShareAction(ActivityMainBannerWebView.this)
////                            .withText(shareStr)
////                            .setPlatform(cutMedia)
////                            .withTitle("知天气")
////                            .withTargetUrl("http://weather.ikan365.cn/")
//////                            .withTargetUrl("http://weather.ikan365.cn/")
////                            .setCallback(umShareListenr)
////                            .withMedia(new UMImage(ActivityMainBannerWebView.this, bitmap))
////                            .share();
//        }
//    };
//
//    public void shareClick(SHARE_MEDIA p) {
//        handler.sendEmptyMessage(1);
//        cutMedia = p;
////      webview.loadUrl("javascript:shareCallback('1')");
//    }
//
//    public void openShare(String act_id,String type) {
//        packShareToServerUp = new PackShareToServerUp();
//        packShareToServerUp.act_id=act_id;
//        packShareToServerUp.type=type;
//        handler.sendEmptyMessage(0);
//    }
//
//    private UMShareListener umShareListenr = new UMShareListener() {
//        @Override
//        public void onStart(SHARE_MEDIA share_media) {
//
//        }
//
//        @Override
//        public void onResult(SHARE_MEDIA share_media) {
////            Toast.makeText(ActivityMainBannerWebView.this, "分享成功。", Toast.LENGTH_SHORT).show();
//            showProgressDialog();
//            commitShareResult(share_media);
//        }
//
//        @Override
//        public void onError(SHARE_MEDIA share_media, Throwable throwable) {
////            Toast.makeText(ActivityMainBannerWebView.this, "分享失败", Toast.LENGTH_SHORT).show();
//
//        }
//
//        @Override
//        public void onCancel(SHARE_MEDIA share_media) {
//
//        }
//    };
//
//    private void commitShareResult(SHARE_MEDIA share_media) {
//        packShareToServerUp.user_id = LoginInformation.getInstance().getUserId();
//        Random rand = new Random();
//        packShareToServerUp.req_mid = System.currentTimeMillis() + "" + (1000 + rand.nextInt(8999));
//        if (share_media == SHARE_MEDIA.QZONE) {
//            packShareToServerUp.fx_qd = "2";
//        } else if (share_media == SHARE_MEDIA.QQ) {
//            packShareToServerUp.fx_qd = "2";
//        } else if (share_media == SHARE_MEDIA.WEIXIN_CIRCLE) {
//            packShareToServerUp.fx_qd = "3";
//        } else if (share_media == SHARE_MEDIA.WEIXIN) {
//            packShareToServerUp.fx_qd = "3";
//        } else if (share_media == SHARE_MEDIA.SINA) {
//            packShareToServerUp.fx_qd = "1";
//        }
//        packShareToServerUp.md5 = PcsMD5.Md5(packShareToServerUp.user_id + packShareToServerUp.req_mid + "pcs_ztq");
//        PcsDataDownload.addDownload(packShareToServerUp);
//    }
//
//
//    private PackShareToServerUp packShareToServerUp;
//
//    public void toLoginActivity() {
//        if (LoginInformation.getInstance().hasLogin()) {
//            webview.clearHistory();
//            Toast.makeText(ActivityMainBannerWebView.this, "login finish second login", Toast.LENGTH_SHORT).show();
//            String userId = "?USER_ID=" + LoginInformation.getInstance().getUserId();
//            String pid = "&PID=";
//            PackInitDown packDown = (PackInitDown) PcsDataManager.getInstance().getNetPack(PackInitUp.NAME);
//            if (packDown != null) {
//                pid += packDown.pid;
//            }
//            webview.loadUrl(url + userId + pid);
//        } else {
//            Intent intent = new Intent(ActivityMainBannerWebView.this, ActivityPhotoLogin.class);
//            startActivityForResult(intent, resultCode);
//        }
//    }
//
//    private void showPopShare() {
//        View view = LayoutInflater.from(ActivityMainBannerWebView.this).inflate(R.layout.pop_share_windown, null);
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
//        dialog = new AlertDialog.Builder(ActivityMainBannerWebView.this).create();
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setView(view, 0, 0, 0, 0);
//        dialog.show();
//        dialog.show();
//    }
//
//    private AlertDialog dialog;
//
//}
