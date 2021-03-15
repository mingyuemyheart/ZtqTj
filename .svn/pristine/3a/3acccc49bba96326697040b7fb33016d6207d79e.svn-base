package com.pcs.ztqtj.control.main_weather;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
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
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.RecognizerResult;
import com.pcs.lib.lib_pcs_v3.control.tool.BitmapUtil;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib.lib_pcs_v3.model.image.ImageConstant;
import com.pcs.lib.lib_pcs_v3.model.image.ImageFetcher;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCity;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityMain;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackBannerDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackBannerUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackHourForecastDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackHourForecastUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackShareAboutDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackShareAboutUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.voice.PackVoiceDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.voice.PackVoiceUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.PackWarnPubDetailDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.PackWarnPubDetailUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.PackYjxxIndexFbDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.PackYjxxIndexFbUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.WarnBean;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.YjxxInfo;
import com.pcs.lib_ztqfj_v2.model.pack.net.week.PackMainWeekWeatherDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.week.PackMainWeekWeatherUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.week.WeekWeatherInfo;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.MyConfigure;
import com.pcs.ztqtj.control.tool.PermissionsTools;
import com.pcs.ztqtj.control.tool.ShareTools;
import com.pcs.ztqtj.control.tool.SharedPreferencesUtil;
import com.pcs.ztqtj.control.tool.VoiceTool;
import com.pcs.ztqtj.control.tool.ZtqImageTool;
import com.pcs.ztqtj.model.JsonParser;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.view.activity.ActivityCompetitionEntry;
import com.pcs.ztqtj.view.activity.ActivityMain;
import com.pcs.ztqtj.view.activity.photoshow.ActivityPhotoLogin;
import com.pcs.ztqtj.view.activity.photoshow.ActivityPhotoShow;
import com.pcs.ztqtj.view.activity.warn.ActivityWarningCenterNotFjCity;
import com.pcs.ztqtj.view.activity.web.ActivityWeatherDay;
import com.pcs.ztqtj.view.activity.web.webview.ActivityWebView;
import com.pcs.ztqtj.view.dialog.DialogFactory;
import com.pcs.ztqtj.view.dialog.DialogOneButton;
import com.pcs.ztqtj.view.dialog.DialogTwoButton;
import com.pcs.ztqtj.view.dialog.DialogVoiceButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * JiangZy on 2016/6/3.
 */
public class CommandMainRow0 extends CommandMainBase {

    private ActivityMain mActivity;
    private ViewGroup mRootLayout;
    private ImageFetcher mImageFetcher;
    private ImageView main_voice;
    public PopupWindow popVoice;
    //行视图
    private View mRowView;
    //上传包：一周天气
    private PackMainWeekWeatherUp mPackWeekUp = new PackMainWeekWeatherUp();
    //上传包：广告
    private PackBannerUp mPackBannerUp = new PackBannerUp();
    private Fragment mFragment;
    private String  ids;
    private VoiceTool voiceTool;
    private TextView tvDesc;

    public CommandMainRow0(ActivityMain activity, ViewGroup rootLayout, ImageFetcher
            imageFetcher, Fragment fragment) {
        mActivity = activity;
        mRootLayout = rootLayout;
        mFragment = fragment;
        mImageFetcher = imageFetcher;
        voiceTool = VoiceTool.getInstance(mActivity, CommandMainRow0.this);
    }

    @Override
    protected void init() {
        initView();
    }

    @Override
    protected void refresh() {
        //刷新天气
        refreshWeather();
        //刷新广告
        refreshAD("11", (ImageView) mRowView.findViewById(R.id.img_bel_data));
        refreshAD("14", (ImageView) mRowView.findViewById(R.id.img_youth_bigtitle));
        refreshAD("27", (ImageView) mRowView.findViewById(R.id.iv_weather_day));
        refreshAD("13", (ImageView) mRowView.findViewById(R.id.img_banner_right));
        refreshWarn();
    }

    private PackYjxxIndexFbUp mPackYjxxUp = new PackYjxxIndexFbUp();
    private List<String> list;
    private List<YjxxInfo> list2, list3;

    private void initView() {
        //行视图
        mRowView = LayoutInflater.from(mActivity).inflate(R.layout.item_home_weather_0, null);
        mRowView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup
                .LayoutParams.WRAP_CONTENT));
        mRootLayout.addView(mRowView);
        //点击事件
        View btn;
        //分享
        btn = mRowView.findViewById(R.id.lay_bt_share);
        btn.setOnClickListener(mOnClick);
        //实景
        btn = mRowView.findViewById(R.id.lay_bt_real);
        btn.setOnClickListener(mOnClick);
        //设置
        btn = mRowView.findViewById(R.id.lay_bt_setting);
        btn.setOnClickListener(mOnClick);
        //实景
        btn = mRowView.findViewById(R.id.lay_bt_recommend);
        btn.setOnClickListener(mOnClick);
        //广告14（青运气象）
        btn = mRowView.findViewById(R.id.img_youth_bigtitle);
        btn.setOnClickListener(mOnClick);

        main_voice = mRowView.findViewById(R.id.main_voice);
        main_voice.setOnTouchListener(touchListener);

        View mContentView = LayoutInflater.from(mActivity).inflate(R.layout.dialog_voice, null);
        iv_voice = (ImageView) mContentView.findViewById(R.id.iv_voice);
        popVoice = new PopupWindow(mContentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams
                .WRAP_CONTENT, true);
        popVoice.setBackgroundDrawable(new BitmapDrawable());
        // 设置可以获得焦点
        popVoice.setFocusable(true);
        popVoice.setOutsideTouchable(true);

        //日历下广告13
        btn = mRowView.findViewById(R.id.img_bel_data);
        btn.setOnClickListener(mOnClick);
        //日历右边
        btn = mRowView.findViewById(R.id.iv_weather_day);
        btn.setOnClickListener(mOnClick);
        //报名按钮
        btn = mRowView.findViewById(R.id.btn_close);
        btn.setOnClickListener(mOnClick);

        //广告19预警右边
        View btns = mRowView.findViewById(R.id.img_banner_right);
        btns.setOnClickListener(mOnClick);

        tvDesc = mRowView.findViewById(R.id.tv_desc);
    }

    /**
     * 刷新预警
     */
    private void refreshWarn() {
        //文字
        TextView textWarn_second = (TextView) mRowView.findViewById(R.id.text_warn_area_second);
        TextView textWarn = (TextView) mRowView.findViewById(R.id.text_warn_area);
        //预警列表
        GridView lv_warn_content = (GridView) mRowView.findViewById(R.id.grid);
        //预警列表2
        GridView lv_warn_contents = (GridView) mRowView.findViewById(R.id.grid_second);
        LinearLayout lay_yj01 = (LinearLayout) mRowView.findViewById(R.id.lay_yj01);
        LinearLayout lay_yj02 = (LinearLayout) mRowView.findViewById(R.id.lay_yj02);
        // 当前城市
        PackLocalCity packCity = ZtqCityDB.getInstance().getCityMain();
        if (packCity == null || packCity.ID == null) {
            lv_warn_content.setVisibility(View.GONE);
            return;
        }
        mPackYjxxUp.setCity(packCity);
        PackYjxxIndexFbDown packYjxxDown = (PackYjxxIndexFbDown) PcsDataManager.getInstance().getNetPack(mPackYjxxUp
                .getName());

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
                ids = list2.get(i).id;
                clickWarnText(ids);
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
                ids = list3.get(i).id;
                clickWarnText(list3.get(i).id);
            }
        });

    }

    class Warn_infoAdapter extends BaseAdapter {

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
            view = LayoutInflater.from(mContext).inflate(
                    R.layout.list_warn_main, null);
            iv_warn_content = (ImageView) view.findViewById(R.id.iv_warn_content);
            if (imageFetcher != null && mList.size() > i) {
                String path = "img_warn/" + mList.get(i).ico + ".png";
                BitmapDrawable bitmapDrawable = imageFetcher.getImageCache().getBitmapFromAssets(path);
                iv_warn_content.setImageDrawable(bitmapDrawable);
            }
            return view;
        }

    }


    //点击预警详情
    private void clickWarnText(String id) {
        //城市
        PackLocalCity packCity = ZtqCityDB.getInstance().getCityMain();
        mPackYjxxUp.setCity(packCity);
        //进度条
        mActivity.showProgressDialog();
        PackWarnPubDetailUp packDetailUp = new PackWarnPubDetailUp();
        packDetailUp.id = id;
        SharedPreferencesUtil.putData(id,id);
        //广播接收
        PcsDataBrocastReceiver.registerReceiver(mActivity, mReceiver);
        //下载
        PcsDataDownload.addDownload(packDetailUp);
    }

    private View view;
    private PcsDataBrocastReceiver mReceiver = new PcsDataBrocastReceiver() {
        @Override
        public void onReceive(String nameStr, String errorStr) {
            if (TextUtils.isEmpty(nameStr)) {
                return;
            }

            if (nameStr.startsWith(PackWarnPubDetailUp.NAME)) {
                //预警详情
                PcsDataBrocastReceiver.unregisterReceiver(mActivity, mReceiver);
                if (!TextUtils.isEmpty(errorStr)) {
                    //获取详情失败
                    showDetilError();
                    return;
                }
                PackWarnPubDetailDown packDown = (PackWarnPubDetailDown) PcsDataManager.getInstance().getNetPack
                        (nameStr);
                if (packDown == null) {
                    //获取详情失败
                    showDetilError();
                    return;
                }
                //城市
                PackLocalCity packCity = ZtqCityDB.getInstance().getCityMain();

                //数据
                WarnBean bean = new WarnBean();
                bean.level = packDown.desc;
                bean.ico = packDown.ico;
                bean.msg = packDown.content;
                bean.pt = packDown.pt;
                bean.defend = packDown.defend;
                bean.put_str = packDown.put_str;
                SharedPreferencesUtil.putData(ids, ids);
                //跳转
                Intent intent2 = new Intent(mActivity, ActivityWarningCenterNotFjCity.class);
                intent2.putExtra("warninfo", bean);
                intent2.putExtra("cityid", packCity.ID);
                mActivity.startActivity(intent2);
            }else   if (nameStr.startsWith(PackVoiceUp.NAME)) {
                PcsDataBrocastReceiver.unregisterReceiver(mActivity, mReceiver);
                PackVoiceDown down = (PackVoiceDown) PcsDataManager.getInstance().getNetPack(PackVoiceUp.NAME);
                mActivity.dismissProgressDialog();
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
                if (dialogVoiceButton == null) {
                    view = LayoutInflater.from(mActivity).inflate(
                            R.layout.dialog_message, null);
                    ((TextView) view.findViewById(R.id.dialogmessage))
                            .setText(down.desc);
                    dialogVoiceButton = new DialogVoiceButton(mActivity,
                            view, "关闭", new DialogFactory.DialogListener() {
                        @Override
                        public void click(String str) {
                            if (str.equals("关闭")) {
                                dialogVoiceButton.dismiss();
                            }
                        }
                    });
                } else {
                    ((TextView) view.findViewById(R.id.dialogmessage))
                            .setText(down.desc);
                }

                if (!dialogVoiceButton.isShowing()) {
                    dialogVoiceButton.show();
                }
                voiceTool.readResult(str);
            }

        }
    };

    /**
     * 获取详情失败
     */
    private void showDetilError() {
        Toast.makeText(mActivity, R.string.get_detail_error, Toast.LENGTH_SHORT).show();
    }

    /**
     * 取一周天气列表
     *
     * @return
     */
    private List<WeekWeatherInfo> getWeatherList() {
        // 当前城市
        PackLocalCity packCity = ZtqCityDB.getInstance().getCityMain();
        if (packCity == null || packCity.ID == null) {
            return new ArrayList<WeekWeatherInfo>();
        }
        mPackWeekUp.setCity(packCity);
        PackMainWeekWeatherDown packMainWeekDown = (PackMainWeekWeatherDown) PcsDataManager.getInstance().getNetPack
                (mPackWeekUp.getName());

        return packMainWeekDown.getWeek();
    }

    /**
     * 刷新天气
     */
    private void refreshWeather() {
        // 当前城市
        PackLocalCity packCity = ZtqCityDB.getInstance().getCityMain();
        if (packCity == null || packCity.ID == null) {
            return;
        }
        //一周天气
        mPackWeekUp.setCity(packCity);

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


    /**
     * 刷新广告
     *
     * @param position_id 广告ID
     * @param imageView
     */
    private void refreshAD(String position_id, ImageView imageView) {

        mPackBannerUp.position_id = position_id;
        PackBannerDown packDown = (PackBannerDown) PcsDataManager.getInstance().getNetPack(mPackBannerUp.getName());
        if (packDown == null || packDown.arrBannerInfo.size() == 0) {
            imageView.setVisibility(View.GONE);
            return;
        } else {
            imageView.setVisibility(View.VISIBLE);
        }
        String url = mActivity.getResources().getString(R.string.file_download_url) + packDown.arrBannerInfo.get(0)
                .img_path;
        mImageFetcher.loadImage(url, imageView, ImageConstant.ImageShowType.SRC);
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
                                voiceTool.mIat.startListening
                                        (voiceTool.mRecognizerListener);
                                if (!popVoice.isShowing()) {
                                    popVoice.showAtLocation(mActivity.getWindow().getDecorView(), Gravity.CENTER, 0,
                                            -250);
                                }
//                            }
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

    String[] nessaryPermissions = {
            Manifest.permission.RECORD_AUDIO
    };

    private boolean checkAudioPermissions() {
        return PermissionsTools.checkPermissions(mFragment, nessaryPermissions, MyConfigure.REQUEST_PERMISSION_AUDIO);
    }

    //点击分享
    private void clickShare() {
        PackLocalCity packCity = ZtqCityDB.getInstance().getCityMain();
        List<WeekWeatherInfo> weatherList = getWeatherList();
        if (packCity == null || TextUtils.isEmpty(packCity.ID) || weatherList == null) {
            return;
        }
        StringBuffer shareStr = new StringBuffer(packCity.NAME + ":");
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
        PackShareAboutDown shareDown = (PackShareAboutDown) PcsDataManager.getInstance().getNetPack(PackShareAboutUp
                .getNameCom());

        String shareContnet = "";
        if (shareDown != null) {
            shareContnet = shareStr + shareDown.share_content;
        }
        ShareTools.getInstance(mActivity).setShareContent("分享天气",shareContnet, bitmap, "0").showWindow(mRootLayout);
    }

    //实景
    private void clickUserGuide() {

        Intent it = new Intent();
        it.setClass(mActivity, ActivityPhotoShow.class);
        it.putExtra(ActivityPhotoShow.CITY_ID, getPhotoCityId());
        mActivity.startActivity(it);
    }

    /**
     * 获取实景地区ID
     *
     * @return
     */
    public String getPhotoCityId() {
        PackLocalCityMain cityMain = ZtqCityDB.getInstance().getCityMain();
        return cityMain.ID;
    }


    //点击设置
    private void clickSet() {
        mActivity.showSetting(true);
    }

    //点击广告
    private void clickAD(String position_id) {
        mPackBannerUp.position_id = position_id;
        PackBannerDown packDown = (PackBannerDown) PcsDataManager.getInstance().getNetPack(mPackBannerUp.getName());
        if (packDown == null || packDown.arrBannerInfo.size() == 0) {
            return;
        }


        Intent it = new Intent(mActivity, ActivityWebView.class);
        it.putExtra("title", packDown.arrBannerInfo.get(0).title);
        it.putExtra("url", packDown.arrBannerInfo.get(0).url);
        it.putExtra("shareContent", packDown.arrBannerInfo.get(0).fx_content);
        mActivity.startActivity(it);
    }

    //点击报名按钮
    private void clickCompetition() {
        Intent intentCompetition = new Intent(mActivity,
                ActivityCompetitionEntry.class);
        mActivity.startActivity(intentCompetition);
    }

    /**
     * 点击跳转世界气象日活动页面
     */
    private void clickWeatherDay() {

        mPackBannerUp.position_id = "27";
        PackBannerDown packDown = (PackBannerDown) PcsDataManager.getInstance().getNetPack(mPackBannerUp.getName());
        if (packDown == null || packDown.arrBannerInfo.size() == 0) {
            return;
        }
        Intent intent = new Intent(mActivity, ActivityWeatherDay.class);
        intent.putExtra("title", packDown.arrBannerInfo.get(0).title);
        intent.putExtra("url", packDown.arrBannerInfo.get(0).url);
        intent.putExtra("BannerInfo", packDown.arrBannerInfo.get(0));
        mActivity.startActivity(intent);
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
        ImageView iv = (ImageView) mRowView.findViewById(R.id.iv_weather_day);
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
                //分享
                case R.id.lay_bt_share:
                    clickShare();
                    break;
                //实景
                case R.id.lay_bt_real:
                    clickUserGuide();
                    break;
                //设置
                case R.id.lay_bt_setting:
                    clickSet();
                    break;
                //实景
                case R.id.lay_bt_recommend:
                    clickUserGuide();
                    break;
                //日历入口
                case R.id.calender_enter:
                    //clickCalenderEnter();
                    break;
                //广告14（青运气象）
                case R.id.img_youth_bigtitle:
                    clickAD("14");
                    break;
                //广告11
                case R.id.img_bel_data:
                    clickAD("11");
                    break;
                //广告13
                case R.id.img_banner_right:
                    //checkPermission();
                    clickAD("13");
                    break;
                //报名按钮
                case R.id.btn_competition:
                    clickCompetition();
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

    DialogTwoButton dialogLogin = null;
    private void showLoginDialog() {
        TextView view = (TextView) LayoutInflater.from(mActivity).inflate(
                R.layout.dialog_message, null);
        view.setText("该功能仅限内部人员使用，请先登录！");

        dialogLogin = new DialogTwoButton(mActivity,
                view, "返回", "登录", new DialogFactory.DialogListener() {
            @Override
            public void click(String str) {
                dialogLogin.dismiss();
                if (str.equals("返回")) {

                } else if (str.equals("登录")) {
                    Intent intent = new Intent(mActivity, ActivityPhotoLogin.class);
                    mActivity.startActivity(intent);
                }
            }
        });
        dialogLogin.show();
    }

    private DialogOneButton dialogPermission = null;
    private void showNoPermission() {
        TextView view = (TextView) LayoutInflater.from(mActivity).inflate(
                R.layout.dialog_message, null);
        view.setText("暂无此权限！");
        dialogPermission = new DialogOneButton(mActivity, view, "确定", new DialogFactory.DialogListener() {
            @Override
            public void click(String str) {
                dialogPermission.dismiss();
            }
        });
        dialogPermission.show();
    }

    private void checkPermission() {
        if(ZtqCityDB.getInstance().isLoginService()) {
            if(ZtqCityDB.getInstance().isServiceAccessible()) {
                clickAD("13");
            } else {
                showNoPermission();
            }
        } else {
            showLoginDialog();
        }
    }

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
                iv_voice.setBackgroundResource(R.drawable
                        .mic_0);
            } else if (volume < 5) {
                iv_voice.setBackgroundResource(R.drawable
                        .mic_1);
            } else if (volume < 10) {
                iv_voice.setBackgroundResource(R.drawable
                        .mic_2);
            } else if (volume < 15) {
                iv_voice.setBackgroundResource(R.drawable
                        .mic_3);
            } else if (volume < 20) {
                iv_voice.setBackgroundResource(R.drawable
                        .mic_4);
            } else if (volume >= 20) {
                iv_voice.setBackgroundResource(R.drawable
                        .mic_5);
            }
        }

    }

    private HashMap<String, String> mIatResults = new LinkedHashMap<>();
    private List<PackLocalCity> lists = new ArrayList<>();
    private String errorString = "没查到该城市天气信息";
    private PackVoiceUp voiceUp = new PackVoiceUp();
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
            mActivity.showProgressDialog();
            PcsDataBrocastReceiver.registerReceiver(mActivity, mReceiver);
            voiceUp.county_id = lists.get(0).ID;
            PcsDataDownload.addDownload(voiceUp);
        }
    }

    public void dismissPopupWindow() {
        if (popVoice != null && popVoice.isShowing()) {
            popVoice.dismiss();
        }
    }


    /* 判断是否是处于wifi状态下*/
    protected boolean isWiFiNewWord() {
        ConnectivityManager connectivityManager = (ConnectivityManager) mActivity.getSystemService(Context
                .CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI;
    }




}
