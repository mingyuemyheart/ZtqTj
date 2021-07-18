package com.pcs.ztqtj.control.main_weather;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.iflytek.cloud.RecognizerResult;
import com.pcs.lib.lib_pcs_v3.control.tool.BitmapUtil;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib.lib_pcs_v3.model.image.ImageConstant;
import com.pcs.lib.lib_pcs_v3.model.image.ImageFetcher;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCity;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalUser;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackHourForecastDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackHourForecastUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackShareAboutDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackShareAboutUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.voice.PackVoiceDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.PackYjxxIndexFbDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.YjxxInfo;
import com.pcs.lib_ztqfj_v2.model.pack.net.week.PackMainWeekWeatherDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.week.WeekWeatherInfo;
import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.MyConfigure;
import com.pcs.ztqtj.control.tool.PermissionsTools;
import com.pcs.ztqtj.control.tool.ShareTools;
import com.pcs.ztqtj.control.tool.VoiceTool;
import com.pcs.ztqtj.control.tool.ZtqImageTool;
import com.pcs.ztqtj.control.tool.utils.TextUtil;
import com.pcs.ztqtj.model.JsonParser;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.util.OkHttpUtil;
import com.pcs.ztqtj.view.activity.ActivityCompetitionEntry;
import com.pcs.ztqtj.view.activity.ActivityMain;
import com.pcs.ztqtj.view.activity.photoshow.ActivityPhotoLogin;
import com.pcs.ztqtj.view.activity.photoshow.ActivityPhotoShow;
import com.pcs.ztqtj.view.activity.prove.WeatherProveActivity;
import com.pcs.ztqtj.view.activity.warn.ActivityWarningCenterNotFjCity;
import com.pcs.ztqtj.view.activity.web.webview.ActivityWebView;
import com.pcs.ztqtj.view.dialog.DialogFactory;
import com.pcs.ztqtj.view.dialog.DialogVoiceButton;
import com.pcs.ztqtj.view.myview.MainViewPager;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 首页-预警，右侧设置等按钮
 */
public class CommandMainRow0 extends CommandMainBase {

    private PackLocalUser localUserinfo;
    private ActivityMain mActivity;
    private ViewGroup mRootLayout;
    private ImageFetcher mImageFetcher;
    private ImageView main_voice;
    public PopupWindow popVoice;
    private View mRowView;
    private VoiceTool voiceTool;
    private TextView tvDesc;
    private TextView textWarn_second;
    private TextView textWarn;
    private GridView lv_warn_content;
    private GridView lv_warn_contents;
    private LinearLayout lay_yj01;
    private LinearLayout lay_yj02;
    private List<String> list;
    private List<YjxxInfo> list2, list3;
    private ImageView img_bel_data,img_youth_bigtitle,iv_weather_day;
    private TextView tvNews1Title,tvNews1;
    private TextSwitcher tvNews;
    private List<FestivalDto> newsList = new ArrayList<>();
    private RollingThread rollingThread;
    private ImageView ivFestival;

    public CommandMainRow0(ActivityMain activity, ViewGroup rootLayout, ImageFetcher imageFetcher) {
        mActivity = activity;
        mRootLayout = rootLayout;
        mImageFetcher = imageFetcher;
        voiceTool = VoiceTool.getInstance(mActivity, CommandMainRow0.this);
    }

    @Override
    protected void init() {
        initView();
    }

    @Override
    protected void refresh() {
        refreshWeather();
        okHttpWarningImages();
    }

    private void initView() {
        localUserinfo = ZtqCityDB.getInstance().getMyInfo();
        mRowView = LayoutInflater.from(mActivity).inflate(R.layout.item_home_weather_0, null);
        mRowView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mRootLayout.addView(mRowView);

        tvNews1Title = mRowView.findViewById(R.id.tvNews1Title);
        tvNews1 = mRowView.findViewById(R.id.tvNews1);
        tvNews = mRowView.findViewById(R.id.tvNews);
        viewPager = mRowView.findViewById(R.id.viewPager);
        ivFestival = mRowView.findViewById(R.id.ivFestival);

        main_voice = mRowView.findViewById(R.id.main_voice);
        main_voice.setOnTouchListener(touchListener);
        View mContentView = LayoutInflater.from(mActivity).inflate(R.layout.dialog_voice, null);
        iv_voice = mContentView.findViewById(R.id.iv_voice);
        popVoice = new PopupWindow(mContentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popVoice.setBackgroundDrawable(new BitmapDrawable());
        popVoice.setFocusable(true);
        popVoice.setOutsideTouchable(true);

        View btn;
        //设置
        btn = mRowView.findViewById(R.id.lay_bt_setting);
        btn.setOnClickListener(mOnClick);
        //分享
        btn = mRowView.findViewById(R.id.lay_bt_share);
        btn.setOnClickListener(mOnClick);
        //实景
        btn = mRowView.findViewById(R.id.lay_bt_recommend);
        btn.setOnClickListener(mOnClick);
        //报名按钮
        btn = mRowView.findViewById(R.id.btn_close);
        btn.setOnClickListener(mOnClick);

        //日历下广告13
        img_bel_data = mRowView.findViewById(R.id.img_bel_data);
        img_bel_data.setOnClickListener(mOnClick);
        //广告14（青运气象）
        img_youth_bigtitle = mRowView.findViewById(R.id.img_youth_bigtitle);
        img_youth_bigtitle.setOnClickListener(mOnClick);
        //日历右边
        iv_weather_day = mRowView.findViewById(R.id.iv_weather_day);
        iv_weather_day.setOnClickListener(mOnClick);

        tvDesc = mRowView.findViewById(R.id.tv_desc);

        //文字
        textWarn_second = mRowView.findViewById(R.id.text_warn_area_second);
        textWarn = mRowView.findViewById(R.id.text_warn_area);
        //预警列表
        lv_warn_content = mRowView.findViewById(R.id.grid);
        //预警列表2
        lv_warn_contents = mRowView.findViewById(R.id.grid_second);
        lay_yj01 = mRowView.findViewById(R.id.lay_yj01);
        lay_yj02 = mRowView.findViewById(R.id.lay_yj02);

        okHttpFestival();
        okHttpEvent();
    }

    /**
     * 刷新天气
     */
    private void refreshWeather() {
        PackLocalCity packCity = ZtqCityDB.getInstance().getCityMain();
        if (packCity == null || packCity.ID == null) {
            return;
        }

        // 天气描述
        PackHourForecastUp packHourUp = new PackHourForecastUp();
        packHourUp.county_id = packCity.ID;
        PackHourForecastDown down = (PackHourForecastDown) PcsDataManager.getInstance().getNetPack(packHourUp.getName());
        if(down != null && !TextUtils.isEmpty(down.desc)) {
            tvDesc.setText(down.desc);
            tvDesc.setVisibility(View.VISIBLE);
            tvDesc.setSelected(true);
        } else {
            tvDesc.setVisibility(View.GONE);
        }
    }

    boolean flag = false;
    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (view.getId()) {
                case R.id.main_voice:
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        flag = checkAudioPermissions();
                        if(!flag) {
                            break;
                        }
                        voiceTool.setRecParam();
                        if (clickNum == 0) {
                            voiceTool.readResult("您好，欢迎使用气象语音服务。请问，您要查询哪个城市的天气？");
                            clickNum = 1;
                        } else {
                            if (isPlay) {
                                voiceTool.mTts.stopSpeaking();
                                main_voice.setBackgroundResource(R.drawable.btn_mainvoice_nor);
                                clickNum = 2;
                                isPlay = false;
                            } else {
                                clickNum = 1;
                                isPopVoice = true;
                                voiceTool.mIat.startListening(voiceTool.mRecognizerListener);
                                if (!popVoice.isShowing()) {
                                    popVoice.showAtLocation(mActivity.getWindow().getDecorView(), Gravity.CENTER, 0, -250);
                                }
                            }
                        }
                    }
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        voiceTool.mRecognizerListener.onEndOfSpeech();
                    }
                    break;
            }
            return true;
        }
    };

    String[] nessaryPermissions = {Manifest.permission.RECORD_AUDIO};

    private boolean checkAudioPermissions() {
        return PermissionsTools.checkPermissions(mActivity, nessaryPermissions, MyConfigure.REQUEST_PERMISSION_AUDIO);
    }

    //点击设置
    private void clickSet() {
        mActivity.showSetting(true);
    }

    /**
     * 获取节日，定位信息下方，上下滚动显示
     */
    private void okHttpFestival() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param  = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    String json = param.toString();
                    final String url = CONST.BASE_URL+"festival_list";
                    Log.e("festival_list", url);
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
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
//                                    Log.e("festival_list", result);
                                    if (!TextUtils.isEmpty(result)) {
                                        try {
                                            JSONObject obj = new JSONObject(result);
                                            if (!obj.isNull("result")) {
                                                newsList.clear();
                                                JSONArray array = obj.getJSONArray("result");
                                                for (int i = 0; i < array.length(); i++) {
                                                    FestivalDto dto = new FestivalDto();
                                                    JSONObject itemObj = array.getJSONObject(i);
                                                    if (!itemObj.isNull("name")) {
                                                        dto.name = itemObj.getString("name");
                                                    }
                                                    if (!itemObj.isNull("content")) {
                                                        dto.content = "公告："+itemObj.getString("content");
                                                    }
                                                    if (!itemObj.isNull("icon")) {
                                                        dto.icon = itemObj.getString("icon");
                                                    }
                                                    newsList.add(dto);
                                                }

                                                tvNews.removeAllViews();
                                                tvNews.setFactory(new ViewSwitcher.ViewFactory() {
                                                    @Override
                                                    public View makeView() {
                                                        TextView textView = new TextView(mActivity);
                                                        textView.setSingleLine();
                                                        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
                                                        textView.setTextColor(Color.WHITE);
                                                        textView.setEllipsize(TextUtils.TruncateAt.END);
                                                        return textView;
                                                    }
                                                });
                                                if (newsList.size() >= 2) {
                                                    tvNews.setVisibility(View.VISIBLE);
                                                    tvNews1Title.setVisibility(View.GONE);
                                                    tvNews1.setVisibility(View.GONE);

                                                    removeThread();
                                                    rollingThread = new RollingThread();
                                                    rollingThread.start();
                                                } else if (newsList.size() == 1) {
                                                    final FestivalDto data = newsList.get(0);
                                                    tvNews1Title.setVisibility(View.VISIBLE);
                                                    if (!TextUtils.isEmpty(data.content)) {
                                                        tvNews1Title.setText(data.content);
                                                    }
                                                    tvNews.setVisibility(View.GONE);
                                                    tvNews1.setText(data.content);
                                                    tvNews1.setVisibility(View.GONE);

                                                    if (!TextUtils.isEmpty(data.icon)) {
                                                        try {
                                                            InputStream is = mActivity.getResources().getAssets().open("festival/" + data.icon +".png");
                                                            Bitmap bitmap = BitmapFactory.decodeStream(is);
                                                            ivFestival.setImageBitmap(bitmap);
                                                            ivFestival.setVisibility(View.VISIBLE);
                                                        } catch (IOException e) {
                                                            e.printStackTrace();
                                                        }
                                                    } else {
                                                        ivFestival.setVisibility(View.GONE);
                                                    }
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

    private class FestivalDto {
        public String name,content,icon;
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            final int index = msg.arg1;
            FestivalDto data = newsList.get(index);
            if (data.content != null) {
                tvNews.setText(data.content);
            }

            if (!TextUtils.isEmpty(data.icon)) {
                try {
                    InputStream is = mActivity.getResources().getAssets().open("festival/" + data.icon +".png");
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    ivFestival.setImageBitmap(bitmap);
                    ivFestival.setVisibility(View.VISIBLE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                ivFestival.setVisibility(View.GONE);
            }
        }
    };

    private void removeThread() {
        if (rollingThread != null) {
            rollingThread.cancel();
            rollingThread = null;
        }
    }

    private class RollingThread extends Thread {
        static final int STATE_PLAYING = 1;
        static final int STATE_PAUSE = 2;
        static final int STATE_CANCEL = 3;
        private int state;
        private int index;
        private boolean isTracking = false;

        @Override
        public void run() {
            super.run();
            this.state = STATE_PLAYING;
            while (index < newsList.size()) {
                if (state == STATE_CANCEL) {
                    break;
                }
                if (state == STATE_PAUSE) {
                    continue;
                }
                if (isTracking) {
                    continue;
                }
                try {
                    Message msg = handler.obtainMessage();
                    msg.arg1 = index;
                    handler.sendMessage(msg);
                    sleep(4000);
                    index++;
                    if (index >= newsList.size()) {
                        index = 0;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void cancel() {
            this.state = STATE_CANCEL;
        }
    }

    /**
     * 获取活动，预警中心按钮上方
     */
    private void okHttpEvent() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param  = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    String json = param.toString();
                    final String url = CONST.BASE_URL+"activity_list";
                    Log.e("activity_list", url);
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
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
//                                    Log.e("activity_list", result);
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
    private List<Fragment> fragments = new ArrayList<>();
    private void initViewPager(String result) {
        fragments.clear();
        try {
            JSONObject obj = new JSONObject(result);
            if (!obj.isNull("result")) {
                JSONArray array = obj.getJSONArray("result");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject itemObj = array.getJSONObject(i);
                    String imgUrl = mActivity.getResources().getString(R.string.msyb) + itemObj.getString("icon");
                    String name = itemObj.getString("name");
                    String dataUrl = itemObj.getString("url");
                    Fragment fragment = new FragmentEvent(mImageFetcher);
                    Bundle bundle = new Bundle();
                    bundle.putString("imgUrl", imgUrl);
                    bundle.putString("name", name);
                    bundle.putString("dataUrl", dataUrl);
                    fragment.setArguments(bundle);
                    fragments.add(fragment);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        viewPager.setVisibility(View.VISIBLE);
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
                FragmentTransaction ft = mActivity.getFragmentManager().beginTransaction();
                ft.add(fragment, fragment.getClass().getSimpleName());
                ft.commit();
                /**
                 * 在用FragmentTransaction.commit()方法提交FragmentTransaction对象后
                 * 会在进程的主线程中,用异步的方式来执行。
                 * 如果想要立即执行这个等待中的操作,就要调用这个方法(只能在主线程中调用)。
                 * 要注意的是,所有的回调和相关的行为都会在这个调用中被执行完成,因此要仔细确认这个方法的调用位置。
                 */
                mActivity.getFragmentManager().executePendingTransactions();
            }

            if (fragment.getView().getParent() == null) {
                container.addView(fragment.getView()); // 为viewpager增加布局
            }
            return fragment.getView();
        }
    };

    //点击广告
    private void clickAD(ImageView imageView) {
        if (imageView.getTag() != null && TextUtils.isEmpty(imageView.getTag().toString())) {
            return;
        }
        String[] tag = imageView.getTag().toString().split(",");
        if (tag == null) {
            return;
        }
        Intent it = new Intent(mActivity, ActivityWebView.class);
        it.putExtra("title", tag[0]);
        it.putExtra("url", tag[1]);
        it.putExtra("shareContent", tag[0]);
        mActivity.startActivity(it);
    }

    /**
     * 点击跳转世界气象日活动页面
     */
    private void clickWeatherDay() {
//        mPackBannerUp.position_id = "27";
//        PackBannerDown packDown = (PackBannerDown) PcsDataManager.getInstance().getNetPack(mPackBannerUp.getName());
//        if (packDown == null || packDown.arrBannerInfo.size() == 0) {
//            return;
//        }
//        Intent intent = new Intent(mActivity, ActivityWeatherDay.class);
//        intent.putExtra("title", packDown.arrBannerInfo.get(0).title);
//        intent.putExtra("url", packDown.arrBannerInfo.get(0).url);
//        intent.putExtra("BannerInfo", packDown.arrBannerInfo.get(0));
//        mActivity.startActivity(intent);
    }

    /**
     * 关闭气象活动栏目
     */
    public void clickClose() {
        View layout = mRowView.findViewById(R.id.layout_weather_day);
        layout.setVisibility(View.GONE);
    }

    /**
     * 开启气象活动栏目
     */
    public void clickShow(String url, String title) {
        View layout = mRowView.findViewById(R.id.layout_weather_day);
        ImageView iv = mRowView.findViewById(R.id.iv_weather_day);
        url = mActivity.getResources().getString(R.string.file_download_url) + url;
        mImageFetcher.loadImage(url, iv, ImageConstant.ImageShowType.SRC);
        layout.setVisibility(View.VISIBLE);
    }

    /**
     * 点击事件
     */
    private View.OnClickListener mOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                //设置
                case R.id.lay_bt_setting:
                    clickSet();
                    break;
                //分享
                case R.id.lay_bt_share:
                    okHttpWeekData();
                    break;
                //实景
                case R.id.lay_bt_recommend:
                    if (TextUtils.isEmpty(localUserinfo.sys_user_id)) {
                        Toast.makeText(mActivity, "请先登录", Toast.LENGTH_SHORT).show();
                    } else {
                        mActivity.startActivity(new Intent(mActivity, ActivityPhotoShow.class));
                    }
                    break;
                //日历入口
                case R.id.calender_enter:
                    //clickCalenderEnter();
                    break;
                //广告14（青运气象）
                case R.id.img_youth_bigtitle:
                    clickAD(img_youth_bigtitle);
                    break;
                //广告11
                case R.id.img_bel_data:
                    clickAD(img_bel_data);
                    break;
                //报名按钮
                case R.id.btn_competition:
                    mActivity.startActivity(new Intent(mActivity, ActivityCompetitionEntry.class));
                    break;
                // 世界气象日活动
                case R.id.iv_weather_day:
                    clickWeatherDay();
                    break;
                case R.id.btn_close:
                    clickClose();
                    break;
            }
        }
    };

//    DialogTwoButton dialogLogin = null;
//    private void showLoginDialog() {
//        TextView view = (TextView) LayoutInflater.from(mActivity).inflate(R.layout.dialog_message, null);
//        view.setText("该功能仅限内部人员使用，请先登录！");
//
//        dialogLogin = new DialogTwoButton(mActivity,
//                view, "返回", "登录", new DialogFactory.DialogListener() {
//            @Override
//            public void click(String str) {
//                dialogLogin.dismiss();
//                if (str.equals("返回")) {
//
//                } else if (str.equals("登录")) {
//                    Intent intent = new Intent(mActivity, ActivityPhotoLogin.class);
//                    mActivity.startActivity(intent);
//                }
//            }
//        });
//        dialogLogin.show();
//    }
//
//    private DialogOneButton dialogPermission = null;
//    private void showNoPermission() {
//        TextView view = (TextView) LayoutInflater.from(mActivity).inflate(R.layout.dialog_message, null);
//        view.setText("暂无此权限！");
//        dialogPermission = new DialogOneButton(mActivity, view, "确定", new DialogFactory.DialogListener() {
//            @Override
//            public void click(String str) {
//                dialogPermission.dismiss();
//            }
//        });
//        dialogPermission.show();
//    }
//
//    private void checkPermission() {
//        if(ZtqCityDB.getInstance().isLoginService()) {
//            if(ZtqCityDB.getInstance().isServiceAccessible()) {
//                clickAD("13");
//            } else {
//                showNoPermission();
//            }
//        } else {
//            showLoginDialog();
//        }
//    }

    private DialogVoiceButton dialogVoiceButton;
    private boolean isPlay = false;
    private int clickNum = 0;
    public void speakBegin() {
        main_voice.setBackgroundResource(R.drawable.btn_mainvoice_sel);
        isPlay = true;
    }
    public void completeRead() {
        main_voice.setBackgroundResource(R.drawable.btn_mainvoice_nor);
        isPlay = false;
        clickNum = 1;
        if (dialogVoiceButton != null) {
            dialogVoiceButton.dismiss();
        }
    }
    private boolean isPopVoice = false;
    private ImageView iv_voice;
    public void setImageChange(int volume) {
        if (isPopVoice) {
            if (volume == 0) {
                iv_voice.setBackgroundResource(R.drawable.mic_0);
            } else if (volume < 5) {
                iv_voice.setBackgroundResource(R.drawable.mic_1);
            } else if (volume < 10) {
                iv_voice.setBackgroundResource(R.drawable.mic_2);
            } else if (volume < 15) {
                iv_voice.setBackgroundResource(R.drawable.mic_3);
            } else if (volume < 20) {
                iv_voice.setBackgroundResource(R.drawable.mic_4);
            } else if (volume >= 20) {
                iv_voice.setBackgroundResource(R.drawable.mic_5);
            }
        }
    }

    private HashMap<String, String> mIatResults = new LinkedHashMap<>();
    private List<PackLocalCity> lists = new ArrayList<>();
    private String errorString = "没查到该城市天气信息";
    public void printResult(RecognizerResult results) {
        String text = JsonParser.parseIatResult(results.getResultString());
        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mIatResults.put(sn, text);

        StringBuffer resultBuffer = new StringBuffer();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }
        lists.clear();
        lists.addAll(ZtqCityDB.getInstance().searchCityConfirm(resultBuffer.toString()));
        if (TextUtils.isEmpty(resultBuffer.toString())) {
            voiceTool.readResult(errorString);
        } else if (lists.size() == 0 || lists == null) {
            mActivity.showProgressDialog();
            voiceTool.readResult(errorString);
            mActivity.dismissProgressDialog();
        } else {
            isPopVoice = false;
            okHttpSound();
        }
    }

    public void dismissPopupWindow() {
        if (popVoice != null && popVoice.isShowing()) {
            popVoice.dismiss();
        }
    }

    /* 判断是否是处于wifi状态下*/
    protected boolean isWiFiNewWord() {
        ConnectivityManager connectivityManager = (ConnectivityManager) mActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI;
    }

    /**
     * 首页预警图标适配器
     */
    private class Warn_infoAdapter extends BaseAdapter {

        private Context mContext;
        private ImageView iv_warn_content;
        private List<YjxxInfo> mList;
        private ImageFetcher imageFetcher;

        public Warn_infoAdapter(Context context, List<YjxxInfo> list, ImageFetcher imageFetcher) {
            super();
            this.mContext = context;
            this.mList = list;
            this.imageFetcher = imageFetcher;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = LayoutInflater.from(mContext).inflate(R.layout.list_warn_main, null);
            iv_warn_content = view.findViewById(R.id.iv_warn_content);
            if (imageFetcher != null && mList.size() > i) {
                String path = "img_warn/" + mList.get(i).ico + ".png";
                BitmapDrawable bitmapDrawable = imageFetcher.getImageCache().getBitmapFromAssets(path);
                iv_warn_content.setImageDrawable(bitmapDrawable);
            }
            return view;
        }
    }

    /**
     * 获取一周天气
     */
    private void okHttpWeekData() {
        mActivity.showProgressDialog();
        final PackLocalCity city = ZtqCityDB.getInstance().getCityMain();
        if(city == null) {
            mActivity.dismissProgressDialog();
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param  = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    JSONObject info = new JSONObject();
                    info.put("stationId", city.ID);
                    param.put("paramInfo", info);
                    String json = param.toString();
                    Log.e("week_data", json);
                    final String url = CONST.BASE_URL+"week_data";
                    Log.e("week_data", url);
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
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
//                                        Log.e("week_data", result);
                                    mActivity.dismissProgressDialog();
                                    if (!TextUtil.isEmpty(result)) {
                                        try {
                                            JSONObject obj = new JSONObject(result);
                                            if (!obj.isNull("b")) {
                                                JSONObject bobj = obj.getJSONObject("b");
                                                if (!bobj.isNull("p_new_week")) {
                                                    JSONObject p_new_weekobj = bobj.getJSONObject("p_new_week");
                                                    if (!TextUtil.isEmpty(p_new_weekobj.toString())) {
                                                        PackMainWeekWeatherDown packWeekDown = new PackMainWeekWeatherDown();
                                                        packWeekDown.fillData(p_new_weekobj.toString());
                                                        List<WeekWeatherInfo> weatherList = packWeekDown.getWeek();
                                                        if (city == null || TextUtils.isEmpty(city.ID) || weatherList == null) {
                                                            return;
                                                        }
                                                        StringBuffer shareStr = new StringBuffer(city.NAME + ":");
                                                        if (weatherList.size() > 1) {
                                                            shareStr.append(weatherList.get(1).gdt + ",");
                                                            shareStr.append(weatherList.get(1).weather + ",");
                                                            shareStr.append(weatherList.get(1).higt + "~");
                                                            shareStr.append(weatherList.get(1).lowt + "°C,");
                                                        }
                                                        if (weatherList.size() > 2) {
                                                            shareStr.append(weatherList.get(2).gdt + ",");
                                                            shareStr.append(weatherList.get(2).weather + ",");
                                                            shareStr.append(weatherList.get(2).higt + "~");
                                                            shareStr.append(weatherList.get(2).lowt + "°C,");
                                                        }
                                                        if (weatherList.size() > 3) {
                                                            shareStr.append(weatherList.get(3).gdt + ",");
                                                            shareStr.append(weatherList.get(3).weather + ",");
                                                            shareStr.append(weatherList.get(3).higt + "~");
                                                            shareStr.append(weatherList.get(3).lowt + "°C。");
                                                        }
                                                        if (weatherList.size() > 4) {
                                                            shareStr.append(weatherList.get(4).gdt + ",");
                                                            shareStr.append(weatherList.get(4).weather + ",");
                                                            shareStr.append(weatherList.get(4).higt + "~");
                                                            shareStr.append(weatherList.get(4).lowt + "°C,");
                                                        }
                                                        if (weatherList.size() > 5) {
                                                            shareStr.append(weatherList.get(5).gdt + ",");
                                                            shareStr.append(weatherList.get(5).weather + ",");
                                                            shareStr.append(weatherList.get(5).higt + "~");
                                                            shareStr.append(weatherList.get(5).lowt + "°C,");
                                                        }
                                                        if (weatherList.size() > 6) {
                                                            shareStr.append(weatherList.get(6).gdt + ",");
                                                            shareStr.append(weatherList.get(6).weather + ",");
                                                            shareStr.append(weatherList.get(6).higt + "~");
                                                            shareStr.append(weatherList.get(6).lowt + "°C。");
                                                        }
                                                        if (weatherList.size() >= 7) {
                                                            shareStr.append(weatherList.get(7).gdt + ",");
                                                            shareStr.append(weatherList.get(7).weather + ",");
                                                            shareStr.append(weatherList.get(7).higt + "~");
                                                            shareStr.append(weatherList.get(7).lowt + "°C。");
                                                        }

                                                        Bitmap bitmap = BitmapUtil.takeScreenShot(mActivity);
                                                        bitmap = ZtqImageTool.getInstance().stitchQR(mActivity, bitmap);
                                                        PackShareAboutDown shareDown = (PackShareAboutDown) PcsDataManager.getInstance().getNetPack(PackShareAboutUp.getNameCom());

                                                        String shareContnet = "";
                                                        if (shareDown != null) {
                                                            shareContnet = shareStr + shareDown.share_content;
                                                        }
                                                        ShareTools.getInstance(mActivity).setShareContent("分享天气",shareContnet, bitmap, "0").showWindow(mRootLayout);
                                                    }
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

    /**
     * 获取预警，首页预警图标
     */
    private void okHttpWarningImages() {
        mActivity.showProgressDialog();
        final PackLocalCity city = ZtqCityDB.getInstance().getCityMain();
        if(city == null) {
            mActivity.dismissProgressDialog();
            lv_warn_content.setVisibility(View.GONE);
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param  = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    JSONObject info = new JSONObject();
                    info.put("stationId", city.ID);
                    param.put("paramInfo", info);
                    String json = param.toString();
                    Log.e("yjxx_index_fb_list", json);
                    final String url = CONST.BASE_URL+"yjxx_index_fb_list";
                    Log.e("yjxx_index_fb_list", url);
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
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mActivity.dismissProgressDialog();
//                                    Log.e("yjxx_index_fb_list", result);
                                    if (!TextUtil.isEmpty(result)) {
                                        try {
                                            JSONObject obj = new JSONObject(result);
                                            if (!obj.isNull("b")) {
                                                JSONObject bobj = obj.getJSONObject("b");
                                                if (!bobj.isNull("yjxx_index_fb_list")) {
                                                    JSONObject listobj = bobj.getJSONObject("yjxx_index_fb_list");
                                                    if (!TextUtil.isEmpty(listobj.toString())) {
                                                        PackYjxxIndexFbDown packYjxxDown = new PackYjxxIndexFbDown();
                                                        packYjxxDown.fillData(listobj.toString());
                                                        if (packYjxxDown == null) {
                                                            lay_yj01.setVisibility(View.GONE);
                                                            lay_yj02.setVisibility(View.GONE);
                                                            return;
                                                        }
                                                        list = packYjxxDown.list;
                                                        list2 = packYjxxDown.list_2;
                                                        list3 = packYjxxDown.list_3;
                                                        if (list == null || list.size() == 0) {
                                                            lay_yj01.setVisibility(View.GONE);
                                                            lay_yj02.setVisibility(View.GONE);
                                                            return;
                                                        }
                                                        if (list2 == null || list2.size() == 0) {
                                                            lay_yj01.setVisibility(View.GONE);
                                                            lay_yj02.setVisibility(View.GONE);
                                                            return;
                                                        }
                                                        lay_yj01.setVisibility(View.VISIBLE);
                                                        lay_yj02.setVisibility(View.VISIBLE);
                                                        textWarn.setText(list.get(0));
                                                        textWarn.setBackgroundResource(R.drawable.border_all_alpha_white);
                                                        int size = list2.size();
                                                        int length = 50;
                                                        DisplayMetrics dm = new DisplayMetrics();
                                                        mActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
                                                        float density = dm.density;
                                                        int gridviewWidth = (int) (size * (length + 4) * density);
                                                        int itemWidth = (int) (length * density);

                                                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(gridviewWidth, 300);
                                                        lv_warn_content.setLayoutParams(params); // 设置GirdView布局参数,横向布局的关键
                                                        lv_warn_content.setColumnWidth(itemWidth); // 设置列表项宽
                                                        lv_warn_content.setHorizontalSpacing(5); // 设置列表项水平间距
                                                        lv_warn_content.setVerticalSpacing(-3);
                                                        lv_warn_content.setStretchMode(GridView.NO_STRETCH);
                                                        lv_warn_content.setNumColumns(size); // 设置列数量=列表集合数

                                                        Warn_infoAdapter adapter = new Warn_infoAdapter(mActivity, list2, mImageFetcher);
                                                        lv_warn_content.setAdapter(adapter);
                                                        lv_warn_content.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                            @Override
                                                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                                                intentWarningCenter(false, list2.get(i).id);
                                                            }
                                                        });
                                                        if (list3 == null || list3.size() == 0) {
                                                            lay_yj01.setVisibility(View.GONE);
                                                            return;
                                                        }
                                                        int gridviewWidth2 = (int) (list3.size() * (length + 4) * density);
                                                        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(gridviewWidth2, 300);
                                                        lay_yj01.setVisibility(View.VISIBLE);
                                                        textWarn_second.setText(list.get(1));
                                                        textWarn_second.setBackgroundResource(R.drawable.border_all_alpha_white);
                                                        int sizes = list3.size();

                                                        lv_warn_contents.setLayoutParams(params2); // 设置GirdView布局参数,横向布局的关键
                                                        lv_warn_contents.setColumnWidth(itemWidth); // 设置列表项宽
                                                        lv_warn_contents.setHorizontalSpacing(5); // 设置列表项水平间距
                                                        lv_warn_contents.setVerticalSpacing(-3);
                                                        lv_warn_contents.setStretchMode(GridView.NO_STRETCH);
                                                        lv_warn_contents.setNumColumns(sizes); // 设置列数量=列表集合数

                                                        Warn_infoAdapter adapterS = new Warn_infoAdapter(mActivity, list3, mImageFetcher);
                                                        lv_warn_contents.setAdapter(adapterS);
                                                        lv_warn_contents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                            @Override
                                                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                                                intentWarningCenter(true, list3.get(i).id);
                                                            }
                                                        });
                                                    }
                                                }
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        lay_yj01.setVisibility(View.GONE);
                                        lay_yj02.setVisibility(View.GONE);
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

    /**
     * 跳转到预警中心
     */
    private void intentWarningCenter(boolean isDisWaring, final String id) {
        Intent intent = new Intent(mActivity, ActivityWarningCenterNotFjCity.class);
        intent.putExtra("isDisWaring", isDisWaring);
        intent.putExtra("warningId", id);
        mActivity.startActivity(intent);
    }

    /**
     * 获取实况语音
     */
    private void okHttpSound() {
        mActivity.showProgressDialog();
        final PackLocalCity city = ZtqCityDB.getInstance().getCityMain();
        if(city == null) {
            mActivity.dismissProgressDialog();
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    JSONObject info = new JSONObject();
                    info.put("stationId", city.ID);
                    param.put("paramInfo", info);
                    String json = param.toString();
                    Log.e("sstq_yy", json);
                    final String url = CONST.BASE_URL+"sstq_yy";
                    Log.e("sstq_yy", url);
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
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mActivity.dismissProgressDialog();
                                    if (!TextUtil.isEmpty(result)) {
                                        try {
                                            JSONObject obj = new JSONObject(result);
                                            if (!obj.isNull("b")) {
                                                JSONObject bobj = obj.getJSONObject("b");
                                                if (!bobj.isNull("sstq_yy")) {
                                                    JSONObject sstq_yy = bobj.getJSONObject("sstq_yy");
                                                    if (!TextUtil.isEmpty(sstq_yy.toString())) {
                                                        PackVoiceDown down = new PackVoiceDown();
                                                        down.fillData(sstq_yy.toString());
                                                        if (down == null) {
                                                            return;
                                                        }
                                                        lists.clear();
                                                        String str = down.desc.replace("-", "零下");
                                                        //格式化语音报读数字
                                                        if (str.contains("12") || str.contains("22") || str.contains("32") || str.contains("42") || str
                                                                .contains("12.2") || str
                                                                .contains("22.2") || str.contains("32.2") || str.contains("42.2")) {

                                                        } else {
                                                            if (str.contains("2.2")) {
                                                                str = str.replace("2.2", "二点二");
                                                            } else {
                                                                if (str.contains("2.")) {
                                                                    str = str.replace("2.", "二点");
                                                                }
                                                            }
                                                        }
                                                        str = str.replace(".2", "点二");
                                                        str = str.replace(".", "点");
                                                        View view = LayoutInflater.from(mActivity).inflate(R.layout.dialog_message, null);
                                                        TextView dialogmessage = view.findViewById(R.id.dialogmessage);
                                                        dialogmessage.setText(down.desc);
                                                        if (dialogVoiceButton == null) {
                                                            dialogVoiceButton = new DialogVoiceButton(mActivity, view, "关闭", new DialogFactory.DialogListener() {
                                                                @Override
                                                                public void click(String str) {
                                                                    if (str.equals("关闭")) {
                                                                        dialogVoiceButton.dismiss();
                                                                    }
                                                                }
                                                            });
                                                        }
                                                        if (!dialogVoiceButton.isShowing()) {
                                                            dialogVoiceButton.show();
                                                        }
                                                        voiceTool.readResult(str);
                                                    }
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
