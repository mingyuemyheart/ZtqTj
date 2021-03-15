package com.pcs.ztqtj.view.activity.product.media;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.CommUtils;
import com.pcs.ztqtj.control.tool.ShareTools;
import com.pcs.ztqtj.control.tool.ZtqImageTool;
import com.pcs.ztqtj.control.tool.image.ImageLoader;
import com.pcs.ztqtj.control.tool.image.ImageUtils;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
import com.pcs.ztqtj.view.activity.web.webview.ActivityWebView;
import com.pcs.ztqtj.view.myview.TexureViewVideoView;
import com.pcs.lib.lib_pcs_v3.control.file.PcsGetPathValue;
import com.pcs.lib.lib_pcs_v3.control.file.PcsMD5;
import com.pcs.lib.lib_pcs_v3.control.tool.BitmapUtil;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib.lib_pcs_v3.model.image.ImageConstant;
import com.pcs.lib.lib_pcs_v3.model.image.ImageFetcher;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackBannerDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackBannerUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.media.MediaInfo;
import com.umeng.socialize.bean.SHARE_MEDIA;


/**
 * 气象影视 播放
 *
 * @author chenjh
 * @version 2014-10-27 上午10:24:39
 */
public class ActivityMediaPlay extends FragmentActivityZtqBase implements
        OnClickListener {

    private final static String TAG = "MediaPlayActivity";

    private int playedTime; // 播放时间

    private TexureViewVideoView myVideoView = null;
    private SeekBar seekBar = null;
    private TextView durationTextView = null;
    private TextView playedTextView = null;
    private TextView descTextView = null;

    private ImageView itemImageView;
    private ImageButton palyBtn = null;
    private ImageButton zoomBtn = null;

    private RelativeLayout play_layout;

    // private View controlView = null;
    // private PopupWindow controler = null;

    private static int screenWidth = 0;
    private static int screenHeight = 0;
    private static int controlHeight = 0;

    private final static int TIME = 6868;

    private boolean isControllerShow = true;
    private boolean isPaused = false; // 是否暂停播放
    private boolean isFullScreen = false; // 是否全屏
    private final static int PROGRESS_CHANGED = 0;
    private final static int HIDE_CONTROLER = 1;

    private String title = "";
    private String mediaurl = "";
    private String imageurl = "";
    private String desc = "";

    private String time_Text = "";
    private MediaInfo mediaInfo;
    private String imagePath = "";
    private ImageLoader mImageLoader;
    private TextView time_textview;
    private ImageView imageview_bgplaybanner;

    private Button btntaunted;
    private Bitmap mBitmap;
    private ImageFetcher mImageFetcher;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.media_paly);
        mediaInfo = (MediaInfo) getIntent().getSerializableExtra("mediaInfo");
        title = mediaInfo.title_two;
        imageurl = mediaInfo.imageurl;
        desc = mediaInfo.desc;
        mImageFetcher = ActivityMediaPlay.this.getImageFetcher();

        if (!TextUtils.isEmpty(mediaInfo.mediaurl)) {
            mediaurl = getString(R.string.file_download_url)
                    + mediaInfo.mediaurl;
        }

        if (!TextUtils.isEmpty(mediaInfo.imageurl)) {
            imageurl = getString(R.string.file_download_url)
                    + mediaInfo.imageurl;
        }

        shareC = "看气象影视，不用再围着电视！看" + title + "点击：" + mediaInfo.fxurl + "  "+"。来自天津气象客户端。点击即可下载：http://ztq.soweather.com:8096/ztq_sh_download/";
        if (!TextUtils.isEmpty(title)) {
            setTitleText(title);
        } else {
            if (!TextUtils.isEmpty(mediaInfo.title)) {
                setTitleText(mediaInfo.title);
            } else {
                setTitleText("气象影视");
            }
        }
        time_Text = mediaInfo.time;

        getScreenSize();

        initView();
        initEvent();
        initData();
    }

    private PackBannerUp mPackBannerUp;

    /**
     * 刷新广告
     *
     * @param position_id 广告ID
     * @param imageView
     */
    private void refreshAD(String position_id, ImageView imageView) {

//
//        imageView.setBackgroundResource(R.color.text_color);
        //       imageView.setVisibility(View.VISIBLE);
        mPackBannerUp = new PackBannerUp();
        mPackBannerUp.position_id = position_id;
        PcsDataDownload.addDownload(mPackBannerUp);
    }

    private void initView() {
        durationTextView = (TextView) findViewById(R.id.duration);
        playedTextView = (TextView) findViewById(R.id.has_played);

        descTextView = (TextView) findViewById(R.id.desc_tv);
        itemImageView = (ImageView) findViewById(R.id.item_image);
        palyBtn = (ImageButton) findViewById(R.id.play_btn);
        zoomBtn = (ImageButton) findViewById(R.id.zoom_btn);
        myVideoView = (TexureViewVideoView) findViewById(R.id.my_videoview);

        palyBtn.setImageResource(R.drawable.btn_play);

        seekBar = (SeekBar) findViewById(R.id.seekbar);
        play_layout = (RelativeLayout) findViewById(R.id.play_layout);

        time_textview = (TextView) findViewById(R.id.time_textview);
        imageview_bgplaybanner = (ImageView) findViewById(R.id.imageview_bgplaybanner);

        btntaunted = (Button) findViewById(R.id.btntaunted);
    }

    private void initEvent() {

        myVideoView.setOnPreparedListener(myOnPreparedListener);
        myVideoView.setMySizeChangeLinstener(mySizeChangeLinstener);
        myVideoView.setOnCompletionListener(myOnCompletionListener);

        seekBar.setOnSeekBarChangeListener(myOnSeekBarChangeListener);

        palyBtn.setOnClickListener(this);
        zoomBtn.setOnClickListener(this);
        btntaunted.setOnClickListener(this);
        imageview_bgplaybanner.setOnClickListener(this);
        refreshAD("21", imageview_bgplaybanner);
    }

    private String shareC = "";
    private MyReceiver receiver = new MyReceiver();

    private void initData() {
        // 注册广播接收
        PcsDataBrocastReceiver.registerReceiver(this, receiver);
        ShareTools.getInstance(this).reqShare();
        setBtnRight(R.drawable.icon_share_new, new OnClickListener() {
            @Override
            public void onClick(View v) {
                mBitmap = getShareBitmap();
                PlayMeadia();
                View layout = findViewById(R.id.layout);
                ShareTools.getInstance(ActivityMediaPlay.this)
                        .setShareClickListener(shareListener)
                        .setShareCallBack(shareCallBackListener)
                        .showWindow(layout);
            }
        });

        imagePath = PcsGetPathValue.getInstance().getImagePath();
        mImageLoader = new ImageLoader(getApplicationContext());
        showProgressDialog();
        descTextView.setText(desc);

        Bitmap bitmap = ImageLoader.decodeSampledBitmapFromResource(imagePath + PcsMD5.Md5(imageurl) + ".png", 720);

        if (bitmap == null) {//图片不存在
            ImageUtils.getInstance().setBgImage(ActivityMediaPlay.this, itemImageView,
                    screenWidth, R.drawable.no_pic, false);
        } else {
            ImageUtils.getInstance().setBgImage(ActivityMediaPlay.this,
                    itemImageView,
                    CommUtils.getScreenWidth(getApplicationContext()), bitmap,
                    false);
            bitmap = null;
        }

        Uri uri = Uri.parse(mediaurl);// 此url就是流媒体文件的下载地址
        myVideoView.setVideoURI(uri);
        time_textview.setText("更新：" + time_Text);

        if (!isOpenNet()) {
            showToast(getString(R.string.open_netword));
        } else {
            if (!isWiFiNewWord()) {
                showToast(getString(R.string.un_wifi_desc));
            }
        }


    }

    /**
     * 获取分享截图
     *
     * @return
     */
    private Bitmap getShareBitmap() {
        Bitmap background = BitmapUtil.takeScreenShot(ActivityMediaPlay.this);
        float width = myVideoView.getWidth();
        float height = myVideoView.getHeight();
        float scale = background.getWidth() / width;
        int statebarHeight = ZtqImageTool.getInstance().getStatusBarHeight(ActivityMediaPlay.this);
        Bitmap videoBitmap = myVideoView.getBitmap();
        videoBitmap = Bitmap.createScaledBitmap(videoBitmap, background.getWidth(), (int) (height * scale), false);
        int[] location = new int[2];
        myVideoView.getLocationOnScreen(location);
        Bitmap result = Bitmap.createBitmap(background.getWidth(), background.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(background, 0, 0, new Paint());
        canvas.drawBitmap(videoBitmap, location[0], location[1] - statebarHeight, new Paint());
        return result;
    }

    private OnLongClickListener myOnLongClickListener = new OnLongClickListener() {

        @Override
        public boolean onLongClick(View arg0) {
            Log.d(TAG, "myOnLongClickListener");
            cancelDelayHide();
            hideControllerDelay();
            return true;
        }
    };

    /**
     * 回调函数，在seek操作完成后调用。
     */
    private OnCompletionListener myOnCompletionListener = new OnCompletionListener() {

        @Override
        public void onCompletion(MediaPlayer arg0) {
            Log.d(TAG, "myOnCompletionListener");
        }
    };

    private TexureViewVideoView.MySizeChangeLinstener mySizeChangeLinstener = new TexureViewVideoView
            .MySizeChangeLinstener() {

        @Override
        public void doMyThings() {
            Log.d(TAG, "mySizeChangeLinstener");
            setVideoScale(SCREEN_DEFAULT);
        }
    };

    private OnPreparedListener myOnPreparedListener = new OnPreparedListener() {

        @Override
        public void onPrepared(MediaPlayer arg0) {
            itemImageView.setVisibility(View.GONE);
            showController();
            dismissProgressDialog();
            Log.d(TAG, "myOnPreparedListener");
            setVideoScale(SCREEN_DEFAULT);
            isFullScreen = false;

            if (isControllerShow) {
                showController();
            }

            int i = myVideoView.getDuration();// 获取视频播放时长。

            seekBar.setMax(i);
            i /= 1000;
            int minute = i / 60;
            int hour = minute / 60;
            int second = i % 60;
            minute %= 60;
            durationTextView.setText(String.format("%02d:%02d:%02d", hour,
                    minute, second));

            /*
             * controler.showAtLocation(myVideoView, Gravity.BOTTOM, 0, 0);
             * controler.update(screenWidth, controlHeight);
             * myHandler.sendEmptyMessageDelayed(HIDE_CONTROLER, TIME);
             */

            myVideoView.start();
            palyBtn.setImageResource(R.drawable.btn_pause);
            hideControllerDelay();
            myHandler.sendEmptyMessage(PROGRESS_CHANGED);
        }
    };

    private ShareTools.ShareOnItemClickListener shareListener = new ShareTools.ShareOnItemClickListener() {
        @Override
        public void onItemClick(int position) {
            String shareTitle;
            switch (position) {
                case 0:
                    // 电话
                    ShareTools.gotoDial();
                    break;
                case 1:
                    // 朋友圈
                    shareTitle = "【天津气象决策版分享】 看气象影视，不用再围着电视！看" + title + "。";
                    ShareTools.shareWeb(mediaInfo.fxurl, getTitleText(), shareC, mBitmap, SHARE_MEDIA.WEIXIN_CIRCLE);
                    break;
                case 2:
                    // 微信
                    ShareTools.shareWeb(mediaInfo.fxurl, getTitleText(),shareC, mBitmap, SHARE_MEDIA.WEIXIN);
                    break;
                case 3:
                    // 彩信
                    ShareTools.shareWithImageAndText(shareC, mBitmap, SHARE_MEDIA.SMS);
                    break;
                case 4:
                    // qq空间
                    ShareTools.shareWeb(mediaInfo.fxurl,getTitleText(), shareC, mBitmap, SHARE_MEDIA.QZONE);
                    break;
                case 5:
                    // 新浪微博
                    ShareTools.shareWithImageAndText(shareC, mBitmap, SHARE_MEDIA.SINA);
                    break;
            }
        }
    };

    private ShareTools.ShareCallBackListener shareCallBackListener = new ShareTools.ShareCallBackListener() {
        @Override
        public void onSuccess() {
            PlayMeadia();
        }

        @Override
        public void onError() {
            PlayMeadia();
        }

        @Override
        public void onCancle() {
            PlayMeadia();
        }
    };

    private OnSeekBarChangeListener myOnSeekBarChangeListener = new OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekbar, int progress,
                                      boolean fromUser) {
            // Log.d(TAG, "myOnVolumeChangemyOnSeekBarChangeListenerdListener");
            if (fromUser) {

                myVideoView.seekTo(progress);// 设置播放位置
            }

        }

        @Override
        public void onStartTrackingTouch(SeekBar arg0) {

            myHandler.removeMessages(HIDE_CONTROLER);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

            myHandler.sendEmptyMessageDelayed(HIDE_CONTROLER, TIME);
        }
    };

    @SuppressWarnings("deprecation")
    private GestureDetector mGestureDetector = new GestureDetector(
            new SimpleOnGestureListener() {
                /**
                 * 双击的第二下Touch down时触发
                 */
                @Override
                public boolean onDoubleTap(MotionEvent e) {

                    if (isFullScreen) {
                        setVideoScale(SCREEN_DEFAULT);
                    } else {
                        setVideoScale(SCREEN_FULL);
                    }
                    isFullScreen = !isFullScreen;


                    if (isControllerShow) {
                        showController();
                    }
                    // return super.onDoubleTap(e);
                    return true;
                }

                /**
                 * 只要按下就会调用此方法，当双击时，第一次按下时会调用此方法，而第二次不会调用
                 */
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    System.out.println("*********onSingleTapUp*******");
                    return super.onSingleTapUp(e);
                }

                /**
                 * 和onSingleTapup不一样，当监听器确定没有第二次按下事件时，才调用此方法，
                 * 也就是onSingleTapUp不能确定是单击还是双击，而此方法响应可以确定一定是单击
                 */
                @Override
                public boolean onSingleTapConfirmed(MotionEvent e) {

                    if (!isControllerShow) {
                        showController();
                        hideControllerDelay();
                    } else {
                        cancelDelayHide();
                        hideController();
                    }
                    // return super.onSingleTapConfirmed(e);
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {// Touch了不移动一直Touch
                    // down时触发

                    if (isPaused) {
                        myVideoView.start();
                        palyBtn.setImageResource(R.drawable.btn_pause);
                        cancelDelayHide();
                        hideControllerDelay();
                    } else {
                        myVideoView.pause();
                        palyBtn.setImageResource(R.drawable.btn_play);
                        cancelDelayHide();
                        showController();
                    }
                    isPaused = !isPaused;
                    // super.onLongPress(e);
                }
            });

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        boolean result = mGestureDetector.onTouchEvent(event);

        if (!result) {
            if (event.getAction() == MotionEvent.ACTION_UP) {

                // if (!isControllerShow) {
                // showController();
                // hideControllerDelay();
                // } else {
                // cancelDelayHide();
                // hideController();
                // }

            }
            result = super.onTouchEvent(event);
        }

        return result;
    }

    private boolean is_v_screen = true;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {// 横竖屏
        Log.d(TAG, "onConfigurationChanged");
        getScreenSize();
        if (isControllerShow) {
            cancelDelayHide();
            hideController();
            showController();
            hideControllerDelay();
        }

        if (is_v_screen) {
            is_v_screen = false;
            imageview_bgplaybanner.setVisibility(View.GONE);
        } else {
            is_v_screen = true;
            if (isFullScreen) {
                imageview_bgplaybanner.setVisibility(View.GONE);
            } else {
                imageview_bgplaybanner.setVisibility(View.VISIBLE);
            }
        }
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPause() {

        playedTime = myVideoView.getCurrentPosition();// 获取当前播放位置。
        myVideoView.pause();
        palyBtn.setImageResource(R.drawable.btn_play);
        super.onPause();
    }

    @Override
    protected void onResume() {
        myVideoView.seekTo(playedTime);
        myVideoView.start();
        if (myVideoView.getVideoHeight() != 0) {
            palyBtn.setImageResource(R.drawable.btn_pause);
            hideControllerDelay();
        }
        Log.d(TAG, "onResume");
        super.onResume();
    }

    @Override
    protected void onDestroy() {

        // if (controler.isShowing()) {
        // controler.dismiss();
        // }
        myVideoView.pause();
        myVideoView.stopPlayback();
        myHandler.removeMessages(PROGRESS_CHANGED);
        myHandler.removeMessages(HIDE_CONTROLER);

        // playList.clear();

        super.onDestroy();
    }

    /**
     * 取得屏幕的宽高
     */
    private void getScreenSize() {
        Display display = getWindowManager().getDefaultDisplay();
        screenHeight = display.getHeight();
        screenWidth = display.getWidth();
        // controlHeight = screenHeight / 4;
        controlHeight = CommUtils.Dip2Px(getApplicationContext(), 65);

    }

    /**
     * 隐藏播放进度条
     */
    private void hideController() {
        Log.d(TAG, "隐藏播放进度条");
        // if (controler.isShowing()) {
        // controler.update(0, 0, 0, 0);
        isControllerShow = false;
        // }
        play_layout.setVisibility(View.INVISIBLE);
    }

    /**
     * 向myHandler通知隐藏播放进度条
     */
    private void hideControllerDelay() {
        myHandler.sendEmptyMessageDelayed(HIDE_CONTROLER, TIME);
    }

    /**
     * 显示播放进度条
     */
    private void showController() {
        Log.d(TAG, "显示播放进度条");
        play_layout.setVisibility(View.VISIBLE);
        // controler.update(0, 0, screenWidth, controlHeight);
        isControllerShow = true;
    }

    private void cancelDelayHide() {
        myHandler.removeMessages(HIDE_CONTROLER);
    }

    private final static int SCREEN_FULL = 0; // 全屏
    private final static int SCREEN_DEFAULT = 1; // 默认

    /**
     * 是否全屏
     *
     * @param flag
     */
    private void setVideoScale(int flag) {

        LayoutParams lp = myVideoView.getLayoutParams();

        switch (flag) {
            case SCREEN_FULL:
                zoomBtn.setImageResource(R.drawable.media_reduce);
                Log.d(TAG, "screenWidth: " + screenWidth + " screenHeight: "
                        + screenHeight);
                myVideoView.setVideoScale(screenWidth, screenHeight);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

                break;

            case SCREEN_DEFAULT:

                int videoWidth = myVideoView.getVideoWidth();
                int videoHeight = myVideoView.getVideoHeight();
                int mWidth = screenWidth;
                int mHeight = screenHeight - 25;

                if (videoWidth > 0 && videoHeight > 0) {
                    if (videoWidth * mHeight > mWidth * videoHeight) {
                        // Log.i("@@@", "image too tall, correcting");
                        mHeight = mWidth * videoHeight / videoWidth;
                    } else if (videoWidth * mHeight < mWidth * videoHeight) {
                        // Log.i("@@@", "image too wide, correcting");
                        mWidth = mHeight * videoWidth / videoHeight;
                    } else {

                    }
                }
                zoomBtn.setImageResource(R.drawable.media_enlarge);
                myVideoView.setVideoScale(mWidth, mHeight);

                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

                break;
        }
    }

    private void PlayMeadia() {
        cancelDelayHide();
        if (isPaused) {
            myVideoView.start();
            palyBtn.setImageResource(R.drawable.btn_pause);
            hideControllerDelay();
        } else {
            myVideoView.pause();
            palyBtn.setImageResource(R.drawable.btn_play);
        }
        isPaused = !isPaused;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play_btn:
                PlayMeadia();
                break;
            case R.id.zoom_btn:
                if (isFullScreen) {
                    setVideoScale(SCREEN_DEFAULT);
                } else {
                    setVideoScale(SCREEN_FULL);
                }
                if (isFullScreen) {
                    imageview_bgplaybanner.setVisibility(View.VISIBLE);
                } else {
                    imageview_bgplaybanner.setVisibility(View.GONE);
                }
                isFullScreen = !isFullScreen;
                break;

            case R.id.btntaunted:
                if (mediaInfo != null && mediaInfo.channel_id != null) {
                    Intent intent = new Intent(this, ActivityMediaTaunted.class);
                    intent.putExtra("mediaInfo", mediaInfo);
                    startActivity(intent);
                } else {
                    showToast("栏目id错误");
                }
                break;
            case R.id.imageview_bgplaybanner:

                if (!packDown.arrBannerInfo.get(0).url.isEmpty()) {
                    Intent it = new Intent(ActivityMediaPlay.this, ActivityWebView.class);
                    it.putExtra("title", packDown.arrBannerInfo.get(0).title);
                    it.putExtra("url", packDown.arrBannerInfo.get(0).url);
                    it.putExtra("shareContent", packDown.arrBannerInfo.get(0).fx_content);
                    startActivity(it);
                }

                break;
        }

    }

    Handler myHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {

                case PROGRESS_CHANGED:

                    int i = myVideoView.getCurrentPosition();// 获取当前播放位置。
                    seekBar.setProgress(i);

                    i /= 1000;
                    int minute = i / 60;
                    int hour = minute / 60;
                    int second = i % 60;
                    minute %= 60;
                    playedTextView.setText(String.format("%02d:%02d:%02d", hour,
                            minute, second));

                    sendEmptyMessage(PROGRESS_CHANGED);
                    break;

                case HIDE_CONTROLER:
                    hideController();
                    break;
            }

            super.handleMessage(msg);
        }
    };

    private PackBannerDown packDown;

    /**
     * 数据更新广播接收
     *
     * @author JiangZy
     */
    private class MyReceiver extends PcsDataBrocastReceiver {
        @Override
        public void onReceive(String name, String error) {
            if (name.startsWith(mPackBannerUp.getName())) {
                packDown = (PackBannerDown) PcsDataManager.getInstance().getNetPack(mPackBannerUp.getName());
                if (packDown == null || packDown.arrBannerInfo.size() == 0) {
                    imageview_bgplaybanner.setVisibility(View.GONE);
                    return;
                } else {
                    imageview_bgplaybanner.setVisibility(View.VISIBLE);
                }

                String url = getResources().getString(R.string.file_download_url) + packDown.arrBannerInfo.get(0).img_path;
                mImageFetcher.loadImage(url, imageview_bgplaybanner, ImageConstant.ImageShowType.SRC);
            }
        }
    }
}
