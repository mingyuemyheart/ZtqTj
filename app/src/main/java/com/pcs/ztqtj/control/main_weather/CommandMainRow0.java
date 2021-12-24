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
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.iflytek.cloud.RecognizerResult;
import com.pcs.lib.lib_pcs_v3.control.tool.BitmapUtil;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCity;
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
import com.pcs.ztqtj.util.CommonUtil;
import com.pcs.ztqtj.util.OkHttpUtil;
import com.pcs.ztqtj.view.activity.ActivityMain;
import com.pcs.ztqtj.view.activity.photoshow.ActivityLogin;
import com.pcs.ztqtj.view.activity.photoshow.ActivityPhotoShow;
import com.pcs.ztqtj.view.activity.warn.ActivityWarningCenterNotFjCity;
import com.pcs.ztqtj.view.dialog.DialogFactory;
import com.pcs.ztqtj.view.dialog.DialogVoiceButton;
import com.pcs.ztqtj.view.myview.MainViewPager;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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

    private ActivityMain mActivity;
    private ViewGroup mRootLayout;
    private ImageView main_voice;
    public PopupWindow popVoice;
    private View mRowView;
    private VoiceTool voiceTool;
    private TextView tvWarning1, tvWarning2;
    private LinearLayout llWarning1, llWarning2;
    private List<String> warningNames;
    private List<YjxxInfo> warningList1, warningList2;
    private TextView tvNews1Title, tvNews1;
    private TextSwitcher tvNews;
    private List<FestivalDto> newsList = new ArrayList<>();
    private RollingThread rollingThread;
    private ImageView ivFestival;

    public CommandMainRow0(ActivityMain activity, ViewGroup rootLayout) {
        mActivity = activity;
        mRootLayout = rootLayout;
        voiceTool = VoiceTool.getInstance(mActivity, CommandMainRow0.this);
    }

    @Override
    protected void init() {
        initView();
    }

    @Override
    protected void refresh() {
        okHttpWarningImages();
    }

    private void initView() {
        mRowView = LayoutInflater.from(mActivity).inflate(R.layout.item_home_weather_0, null);
        mRowView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mRootLayout.addView(mRowView);

        tvNews1Title = mRowView.findViewById(R.id.tvNews1Title);
        tvNews1 = mRowView.findViewById(R.id.tvNews1);
        tvNews = mRowView.findViewById(R.id.tvNews);
        viewPager = mRowView.findViewById(R.id.viewPager);
        viewPager1 = mRowView.findViewById(R.id.viewPager1);
        viewPager2 = mRowView.findViewById(R.id.viewPager2);
        ivFestival = mRowView.findViewById(R.id.ivFestival);

        main_voice = mRowView.findViewById(R.id.main_voice);
        main_voice.setOnTouchListener(touchListener);
        View mContentView = LayoutInflater.from(mActivity).inflate(R.layout.dialog_voice, null);
        iv_voice = mContentView.findViewById(R.id.iv_voice);
        popVoice = new PopupWindow(mContentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popVoice.setBackgroundDrawable(new BitmapDrawable());
        popVoice.setFocusable(true);
        popVoice.setOutsideTouchable(true);

        ImageView ivShare = mRowView.findViewById(R.id.ivShare);
        ivShare.setOnClickListener(mOnClick);
        //实景
        ImageView ivCamera = mRowView.findViewById(R.id.ivCamera);
        ivCamera.setOnClickListener(mOnClick);

        //文字
        tvWarning1 = mRowView.findViewById(R.id.tvWarning1);
        tvWarning2 = mRowView.findViewById(R.id.tvWarning2);
        llWarning1 = mRowView.findViewById(R.id.llWarning1);
        llWarning2 = mRowView.findViewById(R.id.llWarning2);

        okHttpFestival();
        okHttpEvent();
        okHttpAd1();
        okHttpAd2();
    }

    boolean flag = false;
    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (view.getId()) {
                case R.id.main_voice:
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        flag = checkAudioPermissions();
                        if (!flag) {
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

    /**
     * 获取节日，定位信息下方，上下滚动显示
     */
    private void okHttpFestival() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    String json = param.toString();
                    final String url = CONST.BASE_URL + "festival_list";
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
                                                        dto.content = "公告：" + itemObj.getString("content");
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
                                                            InputStream is = mActivity.getResources().getAssets().open("festival/" + data.icon + ".png");
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
        public String name, content, icon;
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
                    InputStream is = mActivity.getResources().getAssets().open("festival/" + data.icon + ".png");
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
     * 点击事件
     */
    private View.OnClickListener mOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                //分享
                case R.id.ivShare:
                    okHttpWeekData();
                    break;
                //实景
                case R.id.ivCamera:
                    if (!ZtqCityDB.getInstance().isLoginService()) {
                        Intent intent = new Intent(mActivity, ActivityLogin.class);
                        mActivity.startActivityForResult(intent, CONST.RESULT_LOGIN);
                    } else {
                        Intent intent = new Intent(mActivity, ActivityPhotoShow.class);
                        intent.putExtra("title", "实景开拍");
                        intent.putExtra("imgType", "1");//imgType:图片类型，1（实景开拍），2（农业开拍分类）必须传，区分哪个业务
                        mActivity.startActivity(intent);
                    }
                    break;
            }
        }
    };

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

    private String errorString = "没查到该城市天气信息";

    public void printResult(RecognizerResult results) {
        String text = JsonParser.parseIatResult(results.getResultString());
        isPopVoice = false;
        okHttpSound(text);
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
     * 获取一周天气
     */
    private void okHttpWeekData() {
        final PackLocalCity city = ZtqCityDB.getInstance().getCityMain();
        if (city == null) {
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
                    Log.e("week_data", json);
                    final String url = CONST.BASE_URL + "week_data";
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
                                                        if (bitmap != null) {
                                                            bitmap = ZtqImageTool.getInstance().stitchQR(mActivity, bitmap);
                                                        }
                                                        if (bitmap != null) {
                                                            ShareTools.getInstance(mActivity).setShareContent("分享天气", shareStr+"", bitmap, "0").showWindow(mRootLayout);
                                                        }
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
        tvWarning1.setVisibility(View.INVISIBLE);
        llWarning1.setVisibility(View.INVISIBLE);
        tvWarning2.setVisibility(View.INVISIBLE);
        llWarning2.setVisibility(View.INVISIBLE);
        final PackLocalCity city = ZtqCityDB.getInstance().getCityMain();
        if (city == null) {
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
                    Log.e("yjxx_index_fb_list", json);
                    final String url = CONST.BASE_URL + "yjxx_index_fb_list";
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
                                    Log.e("yjxx_index_fb_list", result);
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
                                                            return;
                                                        }
                                                        warningNames = packYjxxDown.list;
                                                        warningList1 = packYjxxDown.list_2;
                                                        warningList2 = packYjxxDown.list_3;
                                                        if (warningNames == null || warningNames.size() == 0) {
                                                            return;
                                                        }

                                                        if (warningList1 == null || warningList1.size() == 0) {
                                                            return;
                                                        }

                                                        tvWarning1.setVisibility(View.VISIBLE);
                                                        llWarning1.setVisibility(View.VISIBLE);
                                                        tvWarning1.setText(warningNames.get(0));
                                                        tvWarning1.setBackgroundResource(R.drawable.border_all_alpha_white);

                                                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int) CommonUtil.dip2px(mActivity, 45), (int) CommonUtil.dip2px(mActivity, 45));
                                                        params.leftMargin = (int) CommonUtil.dip2px(mActivity, 5);
                                                        llWarning1.removeAllViews();
                                                        for (int i = 0; i < warningList1.size(); i++) {
                                                            YjxxInfo dto = warningList1.get(i);
                                                            ImageView ivWarning = new ImageView(mActivity);
                                                            ivWarning.setTag(dto.id);
                                                            String path = "img_warn/" + dto.ico + ".png";
                                                            Bitmap bitmap = CommonUtil.getImageFromAssetsFile(mActivity, path);
                                                            if (bitmap != null) {
                                                                ivWarning.setImageBitmap(bitmap);
                                                            }
                                                            ivWarning.setLayoutParams(params);
                                                            llWarning1.addView(ivWarning);
                                                            ivWarning.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    intentWarningCenter(true, v.getTag() + "");
                                                                }
                                                            });
                                                        }

                                                        if (warningList2 == null || warningList2.size() == 0) {
                                                            return;
                                                        }
                                                        tvWarning2.setVisibility(View.VISIBLE);
                                                        llWarning2.setVisibility(View.VISIBLE);
                                                        tvWarning2.setText(warningNames.get(1));
                                                        tvWarning2.setBackgroundResource(R.drawable.border_all_alpha_white);

                                                        llWarning2.removeAllViews();
                                                        for (int i = 0; i < warningList2.size(); i++) {
                                                            YjxxInfo dto = warningList2.get(i);
                                                            ImageView ivWarning = new ImageView(mActivity);
                                                            ivWarning.setTag(dto.id);
                                                            String path = "img_warn/" + dto.ico + ".png";
                                                            Bitmap bitmap = CommonUtil.getImageFromAssetsFile(mActivity, path);
                                                            if (bitmap != null) {
                                                                ivWarning.setImageBitmap(bitmap);
                                                            }
                                                            ivWarning.setLayoutParams(params);
                                                            llWarning2.addView(ivWarning);
                                                            ivWarning.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    intentWarningCenter(false, v.getTag() + "");
                                                                }
                                                            });
                                                        }
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
    private void okHttpSound(String name) {
        if (TextUtil.isEmpty(name)) {
            return;
        }
        final PackLocalCity city = ZtqCityDB.getInstance().getCityInfoInAllCity(name);
        if (city == null) {
            voiceTool.readResult(errorString);
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
                    final String url = CONST.BASE_URL + "sstq_yy";
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
                                    if (!TextUtil.isEmpty(result)) {
                                        try {
                                            JSONObject obj = new JSONObject(result);
                                            if (!obj.isNull("b")) {
                                                JSONObject bobj = obj.getJSONObject("b");
                                                if (!bobj.isNull("sstq_yy")) {
                                                    JSONObject sstq_yy = bobj.getJSONObject("sstq_yy");
                                                    if (!TextUtil.isEmpty(sstq_yy.toString())) {
                                                        if (!sstq_yy.isNull("desc")) {
                                                            String desc = sstq_yy.getString("desc");
                                                            String str = desc.replace("-", "零下");
                                                            //格式化语音报读数字
                                                            if (str.contains("12") || str.contains("22") || str.contains("32") || str.contains("42") || str
                                                                    .contains("12.2") || str.contains("22.2") || str.contains("32.2") || str.contains("42.2")) {
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
                                                            dialogmessage.setText(desc);
                                                            dialogVoiceButton = new DialogVoiceButton(mActivity, view, "关闭", new DialogFactory.DialogListener() {
                                                                @Override
                                                                public void click(String str) {
                                                                    if (str.equals("关闭")) {
                                                                        dialogVoiceButton.dismiss();
                                                                    }
                                                                }
                                                            });
                                                            if (!dialogVoiceButton.isShowing()) {
                                                                dialogVoiceButton.show();
                                                            }
                                                            voiceTool.readResult(str);
                                                        }
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
     * 获取活动，预警中心按钮上方
     */
    private void okHttpEvent() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    String json = param.toString();
                    final String url = CONST.BASE_URL + "activity_list";
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
    private ArrayList<Fragment> fragments = new ArrayList<>();

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
                    Fragment fragment = new FragmentAd();
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
        }

        ;
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
    }

    /**
     * 获取广告
     */
    private void okHttpAd1() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    JSONObject info = new JSONObject();
                    info.put("ad_type", "A001");
                    param.put("paramInfo", info);
                    String json = param.toString();
                    final String url = CONST.BASE_URL + "ad_list";
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
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (!TextUtil.isEmpty(result)) {
                                        initViewPager1(result);
                                    } else {
                                        viewPager1.setVisibility(View.GONE);
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

    private MainViewPager viewPager1 = null;
    private ArrayList<Fragment> fragments1 = new ArrayList<>();

    private void initViewPager1(String result) {
        fragments1.clear();
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
                            String imgUrl = mActivity.getResources().getString(R.string.msyb) + itemObj.getString("img_path");
                            String name = itemObj.getString("title");
                            String dataUrl = itemObj.getString("url");
                            Fragment fragment = new FragmentAd();
                            Bundle bundle = new Bundle();
                            bundle.putString("imgUrl", imgUrl);
                            bundle.putString("name", name);
                            bundle.putString("dataUrl", dataUrl);
                            fragment.setArguments(bundle);
                            fragments1.add(fragment);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (fragments1.size() > 0) {
            viewPager1.setVisibility(View.VISIBLE);
        } else {
            viewPager1.setVisibility(View.GONE);
        }
        viewPager1.setAdapter(new MyPagerAdapter1());
        viewPager1.setSlipping(true);//设置ViewPager是否可以滑动
    }

    private class MyPagerAdapter1 extends PagerAdapter {
        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getCount() {
            return fragments1.size();
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            try {
                ((ViewPager) container).removeView(fragments1.get(position).getView());
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = fragments1.get(position);
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
    }

    /**
     * 获取广告
     */
    private void okHttpAd2() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    JSONObject info = new JSONObject();
                    info.put("ad_type", "A003");
                    param.put("paramInfo", info);
                    String json = param.toString();
                    final String url = CONST.BASE_URL + "ad_list";
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
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (!TextUtil.isEmpty(result)) {
                                        initViewPager2(result);
                                    } else {
                                        viewPager2.setVisibility(View.GONE);
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

    private MainViewPager viewPager2 = null;
    private ArrayList<Fragment> fragments2 = new ArrayList<>();

    private void initViewPager2(String result) {
        fragments2.clear();
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
                            String imgUrl = mActivity.getResources().getString(R.string.msyb) + itemObj.getString("img_path");
                            String name = itemObj.getString("title");
                            String dataUrl = itemObj.getString("url");
                            Fragment fragment = new FragmentAd();
                            Bundle bundle = new Bundle();
                            bundle.putString("imgUrl", imgUrl);
                            bundle.putString("name", name);
                            bundle.putString("dataUrl", dataUrl);
                            fragment.setArguments(bundle);
                            fragments2.add(fragment);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (fragments2.size() > 0) {
            viewPager2.setVisibility(View.VISIBLE);
        } else {
            viewPager2.setVisibility(View.GONE);
        }
        viewPager2.setAdapter(new MyPagerAdapter2());
        viewPager2.setSlipping(true);//设置ViewPager是否可以滑动
    }

    private class MyPagerAdapter2 extends PagerAdapter {
        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getCount() {
            return fragments2.size();
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            try {
                ((ViewPager) container).removeView(fragments2.get(position).getView());
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = fragments2.get(position);
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
    }

}
