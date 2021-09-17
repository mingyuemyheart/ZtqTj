package com.pcs.ztqtj.view.activity.product.media;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.ActivityInfo;
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
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.pcs.lib.lib_pcs_v3.control.file.PcsGetPathValue;
import com.pcs.lib.lib_pcs_v3.control.file.PcsMD5;
import com.pcs.lib.lib_pcs_v3.control.tool.BitmapUtil;
import com.pcs.lib.lib_pcs_v3.model.image.ImageFetcher;
import com.pcs.lib_ztqfj_v2.model.pack.net.media.MediaInfo;
import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.main_weather.FragmentAd;
import com.pcs.ztqtj.control.tool.CommUtils;
import com.pcs.ztqtj.control.tool.ShareTools;
import com.pcs.ztqtj.control.tool.ZtqImageTool;
import com.pcs.ztqtj.control.tool.image.ImageLoader;
import com.pcs.ztqtj.control.tool.image.ImageUtils;
import com.pcs.ztqtj.control.tool.utils.TextUtil;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.util.OkHttpUtil;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
import com.pcs.ztqtj.view.myview.MainViewPager;
import com.pcs.ztqtj.view.myview.TexureViewVideoView;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 气象影视 播放
 */
public class ActivityMediaPlay extends FragmentActivityZtqBase implements OnClickListener {
    
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

    private Button btntaunted;
    private Bitmap mBitmap;
    private ImageFetcher mImageFetcher;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.media_paly);
        mImageFetcher = getImageFetcher();
        mediaInfo = (MediaInfo) getIntent().getSerializableExtra("mediaInfo");
        title = mediaInfo.title_two;
        imageurl = mediaInfo.imageurl;
        desc = mediaInfo.desc;

        if (!TextUtils.isEmpty(mediaInfo.mediaurl)) {
            mediaurl = getString(R.string.msyb) + mediaInfo.mediaurl;
        }

        if (!TextUtils.isEmpty(mediaInfo.imageurl)) {
            imageurl = getString(R.string.msyb)  + mediaInfo.imageurl;
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

    private void initView() {
        viewPager = findViewById(R.id.viewPager);
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

        okHttpAd();
    }

    private String shareC = "";

    private void initData() {
        // 注册广播接收
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
    }

    /**
     * 获取分享截图
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
            durationTextView.setText(String.format("%02d:%02d:%02d", hour, minute, second));

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
                if (configuration == null) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }else {
                    if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    }else if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    }
                }

                if (isFullScreen) {
                    setVideoScale(SCREEN_DEFAULT);
                } else {
                    setVideoScale(SCREEN_FULL);
                }
                if (isFullScreen) {
                    viewPager.setVisibility(View.VISIBLE);
                } else {
                    viewPager.setVisibility(View.GONE);
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

    /**
     * 获取广告
     */
    private void okHttpAd() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param  = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    JSONObject info = new JSONObject();
                    info.put("ad_type", "B001");
                    param.put("paramInfo", info);
                    String json = param.toString();
                    final String url = CONST.BASE_URL+"ad_list";
                    Log.e("ad_list", url);
                    RequestBody body = FormBody.create(MediaType.parse("application/json; charset=utf-8"), json);
                    OkHttpUtil.enqueue(new Request.Builder().post(body).url(url).build(), new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
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
                                    if (!TextUtil.isEmpty(result)) {
                                        initViewPager(result);
                                    } else {
                                        viewPager.setVisibility(View.GONE);
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

    private MainViewPager viewPager = null;
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private void initViewPager(String result) {
        fragments.clear();
        try {
            JSONObject obj = new JSONObject(result);
            if (!obj.isNull("b")) {
                JSONObject bObj = obj.getJSONObject("b");
                if (!bObj.isNull("ad")) {
                    JSONObject adObj = bObj.getJSONObject("ad");
                    if (!adObj.isNull("ad_list")) {
                        JSONArray array = adObj.getJSONArray("ad_list");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject itemObj = array.getJSONObject(i);
                            String imgUrl = getResources().getString(R.string.msyb) + itemObj.getString("img_path");
                            String name = itemObj.getString("title");
                            String dataUrl = itemObj.getString("url");
                            Fragment fragment = new FragmentAd(mImageFetcher);
                            Bundle bundle = new Bundle();
                            bundle.putString("imgUrl", imgUrl);
                            bundle.putString("name", name);
                            bundle.putString("dataUrl", dataUrl);
                            fragment.setArguments(bundle);
                            fragments.add(fragment);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (fragments.size() > 0) {
            viewPager.setVisibility(View.VISIBLE);
        } else {
            viewPager.setVisibility(View.GONE);
        }
        viewPager.setAdapter(new MyPagerAdapter());
        viewPager.setSlipping(true);//设置ViewPager是否可以滑动
        viewPager.setOnPageChangeListener(new MyOnPageChangeListener());

        mHandler.sendEmptyMessageDelayed(AUTO_PLUS, PHOTO_CHANGE_TIME);
    }

    private final int AUTO_PLUS = 1;
    private static final int PHOTO_CHANGE_TIME = 2000;//定时变量
    private int index_plus = 0;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AUTO_PLUS:
                    viewPager.setCurrentItem(index_plus++);//收到消息后设置当前要显示的图片
                    mHandler.sendEmptyMessageDelayed(AUTO_PLUS, PHOTO_CHANGE_TIME);
                    if (index_plus >= fragments.size()) {
                        index_plus = 0;
                    }
                    break;
                default:
                    break;
            }
        };
    };

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageSelected(int arg0) {
            index_plus = arg0;
        }
        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }
        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }

    private class MyPagerAdapter extends PagerAdapter {
        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            try {
                ((ViewPager) container).removeView(fragments.get(position).getView());
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = fragments.get(position);
            if (!fragment.isAdded()) { // 如果fragment还没有added
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.add(fragment, fragment.getClass().getSimpleName());
                ft.commit();
                /**
                 * 在用FragmentTransaction.commit()方法提交FragmentTransaction对象后
                 * 会在进程的主线程中,用异步的方式来执行。
                 * 如果想要立即执行这个等待中的操作,就要调用这个方法(只能在主线程中调用)。
                 * 要注意的是,所有的回调和相关的行为都会在这个调用中被执行完成,因此要仔细确认这个方法的调用位置。
                 */
                getFragmentManager().executePendingTransactions();
            }

            if (fragment.getView().getParent() == null) {
                container.addView(fragment.getView()); // 为viewpager增加布局
            }
            return fragment.getView();
        }
    }

    
    private Configuration configuration;//方向监听器
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        getScreenSize();
        if (isControllerShow) {
            cancelDelayHide();
            hideController();
            showController();
            hideControllerDelay();
        }

        if (is_v_screen) {
            is_v_screen = false;
            viewPager.setVisibility(View.GONE);
        } else {
            is_v_screen = true;
            if (isFullScreen) {
                viewPager.setVisibility(View.GONE);
            } else {
                viewPager.setVisibility(View.VISIBLE);
            }
        }
        super.onConfigurationChanged(newConfig);
        
        configuration = newConfig;
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            showPort();
        }else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            showLand();
        }
    }

    /**
     * 显示竖屏，隐藏横屏
     */
    private void showPort() {
        headLayout.setVisibility(View.VISIBLE);
        fullScreen(false);
        switchVideo();
    }

    /**
     * 显示横屏，隐藏竖屏
     */
    private void showLand() {
        headLayout.setVisibility(View.GONE);
        fullScreen(true);
        switchVideo();
    }

    /**
     * 横竖屏切换视频窗口
     */
    private void switchVideo() {
        if (myVideoView != null) {
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getRealMetrics(dm);
            int width = dm.widthPixels;
            int height = width*9/16;
            LayoutParams params = myVideoView.getLayoutParams();
            params.width = width;
            params.height = height;
            myVideoView.setLayoutParams(params);
        }
    }

    private void fullScreen(boolean enable) {
        if (enable) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getWindow().setAttributes(lp);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            WindowManager.LayoutParams attr = getWindow().getAttributes();
            attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes(attr);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    private void exit() {
        if (configuration == null) {
            finish();
        }else {
            if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                finish();
            }else if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
        }
        return false;
    }

}
