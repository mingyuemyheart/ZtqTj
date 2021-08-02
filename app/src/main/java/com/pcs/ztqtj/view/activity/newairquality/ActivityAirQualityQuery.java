package com.pcs.ztqtj.view.activity.newairquality;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pcs.lib.lib_pcs_v3.control.tool.BitmapUtil;
import com.pcs.lib.lib_pcs_v3.control.tool.ScreenUtil;
import com.pcs.lib.lib_pcs_v3.control.tool.Util;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib.lib_pcs_v3.model.pack.PcsPackDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.airinfopack.PackAirInfoDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.airinfopack.PackAirLevelDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.airinfopack.PackAirStationDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.airinfopack.PackAirStationInfoDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.airinfopack.PackAirStationInfoUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.airinfopack.PackAirStationUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.airinfopack.PackAirTrendDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.airinfopack.PackKeyDescDown;
import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.air_qualitydetail.AdapterAirStations;
import com.pcs.ztqtj.control.inter.ClickPositionListener;
import com.pcs.ztqtj.control.tool.AirQualityTool;
import com.pcs.ztqtj.control.tool.CommUtils;
import com.pcs.ztqtj.control.tool.NetTask;
import com.pcs.ztqtj.control.tool.ShareTools;
import com.pcs.ztqtj.control.tool.ZtqImageTool;
import com.pcs.ztqtj.control.tool.utils.TextUtil;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.util.OkHttpUtil;
import com.pcs.ztqtj.view.activity.FragmentActivityWithShare;
import com.pcs.ztqtj.view.activity.air_quality.AcitvityAirWhatAQI;
import com.pcs.ztqtj.view.myview.AirQualityView;
import com.pcs.ztqtj.view.myview.CircleProgressView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 监测预报-空气质量-空气质量实况，天津外
 */
public class ActivityAirQualityQuery extends FragmentActivityWithShare implements View.OnClickListener {

    private AirQualityView airQueryView;
    private ActivityAirQueryDetailControl control;
    private TextView null_air_data, tv_choose_station;
    private LinearLayout lay_citiao, lay_PM2, lay_PM10, lay_CO, lay_NO2, lay_SO2, lay_031h, lay_038h;
    private MyReceiver receiver = new MyReceiver();
    private LinearLayout lay_airRanking, lay_choose_station;
    private ImageView iv_choose_station;
    private String areatype = "1";
    /**
     * 城市ID
     */
    private static String s_area_id;
    /**
     * 城市名称
     */
    private static String s_area_name;
    /**
     * 显示城市/站点
     */
    private static boolean s_show_city = true;

    private TextView tv_038h, tv_031h, tv_pm2, tv_pm10, tv_co, tv_no2, tv_so2, tv_aqi, tv_quality, tv_city_num,
            tv_pub_time, tv_healthy, tv_airquality_name, tv_aqi_name, tv_city_total;
    /**
     * 上传包(站点)
     */
    private final PackAirStationInfoUp mPackStationInfoUp = new PackAirStationInfoUp();
    private CircleProgressView circle_progress_view;
    private TextView tv_type;
    private TextView tv_ys, tv_zd, tv_zdwr, tv_qd, tv_l, tv_y;
    private String type = "0";
    private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH", Locale.CHINA);
    private SimpleDateFormat sdf2 = new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_airqualityquery);
        setTitleText("空气质量");
        initView();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String id = bundle.getString("id");
            String name = bundle.getString("name");
            setCity(id, name);
            tv_choose_station.setText(name + "总体");
        } else {
            tv_choose_station.setText(s_area_name + "总体");
        }

        okHttpAirLev();
        okHttpAirRemark();
        if (s_show_city) {
            okHttpAirCityStation();
        } else {
            reqAirStationInfo(station_name, 1);
        }
        reqStationList2();
    }

    public void dataIsNull() {
        null_air_data.setVisibility(View.VISIBLE);
    }

    private void initView() {
//        btn_right = (ImageButton) findViewById(R.id.btn_right);
//        btn_right.setVisibility(View.VISIBLE);
        //btn_right.setOnClickListener(this);
        null_air_data = (TextView) findViewById(R.id.null_air_data);
        control = new ActivityAirQueryDetailControl(ActivityAirQualityQuery.this);
        airQueryView = findViewById(R.id.act_airQueryView);
        airQueryView.setItemName("", AirQualityView.IsDrawRectangele.BROKENLINE);
        airQueryView.setClickPositionListener(clicklistener);
        tv_ys = findViewById(R.id.tv_level_ys);
        tv_zd = findViewById(R.id.tv_level_zd);
        tv_zdwr = findViewById(R.id.tv_level_zdwr);
        tv_qd = findViewById(R.id.tv_level_qd);
        tv_l = findViewById(R.id.tv_level_l);
        tv_y = findViewById(R.id.tv_level_y);
        //control.reqData(ControlDistribution.ColumnCategory.TEMPERATURE, s_area_id, areatype, "aqi");
        lay_citiao = (LinearLayout) findViewById(R.id.lay_citiao);
        lay_citiao.setOnClickListener(this);
        lay_PM2 = (LinearLayout) findViewById(R.id.lay_PM2);
        lay_PM2.setOnClickListener(this);
        lay_PM10 = (LinearLayout) findViewById(R.id.lay_PM10);
        lay_PM10.setOnClickListener(this);
        lay_CO = (LinearLayout) findViewById(R.id.lay_CO);
        lay_CO.setOnClickListener(this);
        lay_NO2 = (LinearLayout) findViewById(R.id.lay_N02);
        lay_NO2.setOnClickListener(this);
        lay_SO2 = (LinearLayout) findViewById(R.id.lay_SO2);
        lay_SO2.setOnClickListener(this);
        lay_031h = (LinearLayout) findViewById(R.id.lay_031h);
        lay_031h.setOnClickListener(this);
        lay_038h = (LinearLayout) findViewById(R.id.lay_038h);
        lay_038h.setOnClickListener(this);
        tv_type = findViewById(R.id.tv_type);
        lay_airRanking = (LinearLayout) findViewById(R.id.lay_airRanking);
        lay_airRanking.setOnClickListener(this);
        lay_choose_station = (LinearLayout) findViewById(R.id.lay_choose_station);
        lay_choose_station.setOnClickListener(this);
        LinearLayout llMap = (LinearLayout) findViewById(R.id.ll_map);
        llMap.setOnClickListener(this);
        iv_choose_station = (ImageView) findViewById(R.id.iv_choose_station);
        tv_airquality_name = (TextView) findViewById(R.id.tv_airquality_name);

        tv_choose_station = (TextView) findViewById(R.id.tv_choose_station);
        tv_city_num = (TextView) findViewById(R.id.tv_city_num);
        tv_city_total = findViewById(R.id.tv_city_total);
        tv_pub_time = (TextView) findViewById(R.id.tv_pub_time);
        tv_healthy = (TextView) findViewById(R.id.tv_healthy);

        tv_aqi = (TextView) findViewById(R.id.tv_aqi);
        tv_031h = (TextView) findViewById(R.id.tv_031h);
        tv_pm2 = (TextView) findViewById(R.id.tv_pm2);
        tv_pm10 = (TextView) findViewById(R.id.tv_pm10);
        tv_co = (TextView) findViewById(R.id.tv_co);
        tv_no2 = (TextView) findViewById(R.id.tv_no2);
        tv_so2 = (TextView) findViewById(R.id.tv_so2);
        tv_038h = (TextView) findViewById(R.id.tv_038h);
        tv_quality = (TextView) findViewById(R.id.tv_quality);

        circle_progress_view = (CircleProgressView) findViewById(R.id.circle_progress_view);
        RelativeLayout rel_circle_aqi = (RelativeLayout) findViewById(R.id.rel_circle_aqi);
        rel_circle_aqi.setOnClickListener(this);
        tv_aqi_name = (TextView) findViewById(R.id.tv_aqi_name);

        screenwidth = Util.getScreenWidth(ActivityAirQualityQuery.this);
        setBtnRightListener(this);
    }

    private Handler handler = new Handler();
    private int curProgress = 0;
    Runnable runnableProgress = new Runnable() {

        @Override
        public void run() {
            if (circle_progress_view != null) {

                if (curProgress <= 100) {
                    curProgress += 1;
                }
                circle_progress_view.setProgress(curProgress, aqi);
            }
            handler.post(this);
            //handler.postDelayed(this, TIME_PROGRESS); //handler自带方法实现定时器
        }
    };

    private ClickPositionListener clicklistener = new ClickPositionListener() {
        @Override
        public void positionListener(int x, int y, String value, boolean isYb) {
            if (popupWindow != null && popupWindow.isShowing()) {
                popupWindow.dismiss();
            }
            showValue(airQueryView, value, x, y, isYb);
        }

        @Override
        public void moveListener() {
            if (popupWindow != null && popupWindow.isShowing()) {
                popupWindow.dismiss();
            }
        }
    };

    private PopupWindow popupWindow;

    /**
     * 显示下来选择列表
     */
    public void showValue(View view, String value, int x, int y, boolean isYb) {
        // 一个自定义的布局，作为显示的内容
        View contentView = LayoutInflater.from(this).inflate(R.layout.pop_livequery, null);
        TextView tv_value = (TextView) contentView.findViewById(R.id.tv_value);
        tv_value.setText(value);
        contentView.setBackgroundResource(R.drawable.icon_airquality_sk);
        // 设置按钮的点击事件
        popupWindow = new PopupWindow(contentView, ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.color.alpha100));
        // 设置好参数之后再show

        int indexTime = value.indexOf("时");
        int lengText = value.length() - indexTime;
        int widtha = (int) ((tv_value.getTextSize() * (lengText))) / 3 * 2;
        int width = ScreenUtil.dip2px(this, 80);
        int hight = ScreenUtil.dip2px(this, 50);
        if (widtha > width) {
            width = widtha;
        }
        int scW = getWindowManager().getDefaultDisplay().getWidth();
        if ((x - width / 2) < 0 || (x + width / 2) > scW) {
            return;
        }
        popupWindow.setWidth(width);
        popupWindow.setHeight(hight);
        popupWindow.showAsDropDown(view, x - width / 2, -(y + hight));
    }

    public void reFlushList(PackAirTrendDown trendDown) {
        if (trendDown.skList.size() == 0) {
            dataIsNull();
        } else {
            boolean hasValue = false;
            for (int i = 0; i < trendDown.skList.size(); i++) {
                PackAirTrendDown.AirMapBean bena = trendDown.skList.get(i);
                if (!TextUtils.isEmpty(bena.val)) {
                    hasValue = true;
                    break;
                }
            }
            String[] str = new String[7];
            for (int j = 0; j < list_level.size(); j++) {
                PackAirLevelDown.AirLecel airLecel = list_level.get(j);
                if (tv_type.getText().toString().contains(airLecel.type)) {
                    str = airLecel.name.replace("[", "").replace("]", "").split(",");
                } else if (tv_type.getText().toString().contains("PM2") && airLecel.type.contains("PM2")) {
                    str = airLecel.name.replace("[", "").replace("]", "").split(",");
                } else if (tv_type.getText().toString().contains("O₃") && airLecel.type.contains("03")) {
                    str = airLecel.name.replace("[", "").replace("]", "").split(",");
                } else if (tv_type.getText().toString().contains("NO₂") && airLecel.type.contains("NO2")) {
                    str = airLecel.name.replace("[", "").replace("]", "").split(",");
                } else if (tv_type.getText().toString().contains("SO₂") && airLecel.type.contains("SO2")) {
                    str = airLecel.name.replace("[", "").replace("]", "").split(",");
                }
            }
            airQueryView.setNewData(trendDown.skList, str, type);
            if (hasValue) {
                null_air_data.setVisibility(View.GONE);
            } else {
                null_air_data.setVisibility(View.VISIBLE);
            }
            dismissProgressDialog();
        }
    }

    /**
     * 设置城市
     * @param area_id
     * @param area_name
     */
    public static void setCity(String area_id, String area_name) {
        s_area_id = area_id;
        s_area_name = area_name;
        s_show_city = true;
    }

    public static void setTitel(String cityname) {
    }

    @Override
    protected void onResume() {
        super.onResume();
        CommUtils.closeKeyboard(this);
        PcsDataBrocastReceiver.registerReceiver(ActivityAirQualityQuery.this, receiver);
    }

    private List<PackAirStationDown.PackAirStation> list = new ArrayList<>();
    private PackAirStationUp mPackStationUp = new PackAirStationUp();

    private void reqStationList2() {
        showProgressDialog();
        if (s_show_city) {
            mPackStationUp.area_id = s_area_id;
        } else {
            mPackStationUp.area_id = station_id;
        }
        NetTask task = new NetTask(this, new NetTask.NetListener() {
            @Override
            public void onComplete(PcsPackDown down) {
                // 加载数据
                mPackStationDown = (PackAirStationDown) down;
                // 弹出对话框
                //showDialogStation();
                list.clear();
                PackAirStationDown.PackAirStation pack = new PackAirStationDown.PackAirStation();
                pack.position_name = s_area_name + "总体";
                pack.station_code = s_area_id;
                list.add(pack);
                if (mPackStationDown == null) {
                    return;
                }

                if (mPackStationDown.list.size() == 0) {
//                    Toast.makeText(ActivityAirQualityQuery.this, "暂无站点",
//                            Toast.LENGTH_SHORT).show();
                    return;
                }

                list.addAll(mPackStationDown.list);
            }
        });
        task.execute(mPackStationUp);
    }

    private void reqAirStationInfo(String name, int id) {
        mPackStationInfoUp.station_name = name;
        mPackStationInfoUp.time_num = id;
        NetTask task = new NetTask(this, new NetTask.NetListener() {
            @Override
            public void onComplete(PcsPackDown down) {
                // 取消等待框
                dismissProgressDialog();
                // 加载数据
                PackAirStationInfoDown mPackStationInfoDown = (PackAirStationInfoDown) down;
                if (mPackStationInfoDown == null) {
                    return;
                }
                // 刷新数据
                refreshData(mPackStationInfoDown, 18);
            }
        });
        task.execute(mPackStationInfoUp);
    }

    @Override
    protected void onPause() {
        super.onPause();
        PcsDataBrocastReceiver.unregisterReceiver(this, receiver);
    }

    private PackKeyDescDown packKey = new PackKeyDescDown();

    private int aqi;

    public void refreshData(PackAirInfoDown packAirInfoDown, int delay) {
        if (!TextUtils.isEmpty(packAirInfoDown.aqi)) {
            tv_aqi.setText(packAirInfoDown.aqi);
        }
        tv_pm2.setText(packAirInfoDown.pm2_5);
        tv_031h.setText(packAirInfoDown.o3);
        tv_038h.setText(packAirInfoDown.o3_8h);
        tv_co.setText(packAirInfoDown.co);
        tv_no2.setText(packAirInfoDown.no2);
        tv_pm10.setText(packAirInfoDown.pm10);
        tv_so2.setText(packAirInfoDown.so2);
        //tv_quality.setText(packAirInfoDown.quality);
        tv_aqi_name.setText("AQI");
        if (!TextUtils.isEmpty(packAirInfoDown.aqi)) {
            aqi = Integer.valueOf(packAirInfoDown.aqi);
            String tipArr[] = getResources().getStringArray(
                    R.array.AirQualityHeathTip);
            String tipStr = AirQualityTool.getInstance().getHealthTip(tipArr,
                    aqi);
            tv_healthy.setText(tipStr);
            getTvColor(aqi);
            handler.postDelayed(runnableProgress, delay);
        }
    }

    private ArrayList<PackAirLevelDown.AirLecel> list_level = new ArrayList<>();

    /**
     * 下载包(站点列表)
     */
    private PackAirStationDown mPackStationDown = new PackAirStationDown();

    private class MyReceiver extends PcsDataBrocastReceiver {
        @Override
        public void onReceive(String name, String errorStr) {
            if (name.equals(mPackStationUp.getName())) {
                if (!TextUtils.isEmpty(errorStr)) {
                    return;
                }
                // 加载数据
                mPackStationDown = (PackAirStationDown) PcsDataManager.getInstance().getNetPack(mPackStationUp.getName());
                // 弹出对话框
                //showDialogStation();
                list.clear();
                PackAirStationDown.PackAirStation pack = new PackAirStationDown.PackAirStation();
                pack.position_name = s_area_name + "总体";
                pack.station_code = s_area_id;
                list.add(pack);
                if (mPackStationDown == null) {
                    return;
                }
                if (mPackStationDown.list.size() == 0) {
//                    Toast.makeText(ActivityAirQualityQuery.this, "暂无站点",
//                            Toast.LENGTH_SHORT).show();
                    return;
                }
                list.addAll(mPackStationDown.list);
            } else if (name.equals(mPackStationInfoUp.getName())) {
                if (!TextUtils.isEmpty(errorStr)) {
                    return;
                }
                // 取消等待框
                dismissProgressDialog();
                // 加载数据
                PackAirStationInfoDown mPackStationInfoDown = (PackAirStationInfoDown) PcsDataManager.getInstance()
                        .getNetPack(
                                mPackStationInfoUp.getName());
                if (mPackStationInfoDown == null) {
                    return;
                }
                // 刷新数据
                refreshData(mPackStationInfoDown, 18);
            }
        }
    }

    private List<PackKeyDescDown.DicListBean> dataeaum = new ArrayList<>();

    /**
     * 处理aqi字段
     * @param packKey
     */
    private void dealWidthKeyData(PackKeyDescDown packKey) {
        dataeaum.clear();
        for (int i = 0; i < packKey.dicList.size(); i++) {
            dataeaum.add(packKey.dicList.get(i));
        }
        if (dataeaum.size() > 0) {
            // 如果第一个不是aqi的话，则从新请求数据
//            if (dataeaum.get(0).rankType.toLowerCase().equals("aqi")) {
//            } else {
//                changeValueKey(0);
//            }
        }
    }

    private void changeValueKey(int position) {
        try {
            if (packKey.dicList == null || packKey.dicList.size() == 0) {

            } else {
                Intent intent = new Intent(ActivityAirQualityQuery.this, AcitvityAirWhatAQI.class);
                intent.putExtra("w", packKey.dicList.get(position).des);
                intent.putExtra("t", "小词条");
                startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lay_citiao:
                break;
            case R.id.lay_PM2:
                type = "0";
                setTextContentColor("0");
                tv_type.setText("PM2.5走势图");
                okHttpAirTrend("pm2");
                break;
            case R.id.lay_PM10:
                type = "0";
                setTextContentColor("0");
                tv_type.setText("PM10走势图");
                okHttpAirTrend("pm10");
                break;
            case R.id.lay_CO:
                type = "1";
                setTextContentColor("1");
                tv_type.setText("CO走势图");
                okHttpAirTrend("co");
                break;
            case R.id.lay_N02:
                type = "2";
                setTextContentColor("2");
                tv_type.setText("NO₂走势图");
                okHttpAirTrend("no2");
                break;
            case R.id.lay_SO2:
                type = "3";
                setTextContentColor("1");
                tv_type.setText("SO₂走势图");
                okHttpAirTrend("so2");
                break;
            case R.id.lay_031h:
                type = "4";
                setTextContentColor("3");
                tv_type.setText("O₃-1h走势图");
                okHttpAirTrend("o3");
                break;
            case R.id.lay_038h:
                tv_type.setText("O₃-8h走势图");
                okHttpAirTrend("o3");
                break;
            case R.id.lay_airRanking:
                Intent intent = new Intent(ActivityAirQualityQuery.this, ActivityAirQualityRandking.class);
                intent.putExtra("name", s_area_name);
                startActivity(intent);
                break;
            case R.id.lay_choose_station:
                showStationPopup();
                break;
            case R.id.rel_circle_aqi:
                type = "0";
                setTextContentColor("0");
                tv_type.setText("AQI走势图");
                okHttpAirTrend("aqi");
                break;
            case R.id.ll_map:
                startActivity(new Intent(this, ActivityAir.class));
                break;
            case R.id.btn_right:
                View layout = findViewById(R.id.all_view);
                Bitmap shareBitmap = BitmapUtil.takeScreenShot(this);
//                Bitmap shareBitmap = ZtqImageTool.getInstance().getScreenBitmap(layout);
                shareBitmap = ZtqImageTool.getInstance().stitchQR(ActivityAirQualityQuery.this, shareBitmap);
                ShareTools.getInstance(ActivityAirQualityQuery.this).setShareContent(getTitleText(), mShare, shareBitmap, "0").showWindow(layout);
                break;
//            case R.id.btn_right:
//                showProgressDialog();
//                reqKey();
//                if (s_show_city) {
//                    reqStationList();
//                    control.reqData(ControlDistribution.ColumnCategory.TEMPERATURE, s_area_id, "1", "aqi");
//                } else {
//                    control.reqData(ControlDistribution.ColumnCategory.TEMPERATURE, station_id, "2", "aqi");
//                }
//
//                break;
        }
    }

    private void setTextContentColor(String type) {
        if (type.equals("0")) {
            tv_ys.setText("严重污染");
            tv_ys.setTextColor(getResources().getColor(R.color.air_quality_6));
            tv_zd.setText("重度污染");
            tv_zd.setTextColor(getResources().getColor(R.color.air_quality_5));
            tv_zdwr.setText("中度污染");
            tv_zdwr.setTextColor(getResources().getColor(R.color.air_quality_4));
            tv_qd.setText("轻度污染");
            tv_qd.setTextColor(getResources().getColor(R.color.air_quality_3));
            tv_l.setText("良");
            tv_l.setTextColor(getResources().getColor(R.color.air_quality_2));
            tv_y.setText("优");
            tv_y.setTextColor(getResources().getColor(R.color.air_quality_1));
        } else if (type.equals("1")) {
            tv_ys.setText("良");
            tv_ys.setTextColor(getResources().getColor(R.color.air_quality_2));
            tv_zd.setText("良");
            tv_zd.setTextColor(getResources().getColor(R.color.air_quality_2));
            tv_zdwr.setText("良");
            tv_zdwr.setTextColor(getResources().getColor(R.color.air_quality_2));
            tv_qd.setText("优");
            tv_qd.setTextColor(getResources().getColor(R.color.air_quality_1));
            tv_l.setText("优");
            tv_l.setTextColor(getResources().getColor(R.color.air_quality_1));
            tv_y.setText("优");
            tv_y.setTextColor(getResources().getColor(R.color.air_quality_1));
        } else if (type.equals("2")) {
            tv_ys.setText("轻度污染");
            tv_ys.setTextColor(getResources().getColor(R.color.air_quality_3));
            tv_zd.setText("轻度污染");
            tv_zd.setTextColor(getResources().getColor(R.color.air_quality_3));
            tv_zdwr.setText("良");
            tv_zdwr.setTextColor(getResources().getColor(R.color.air_quality_2));
            tv_qd.setText("良");
            tv_qd.setTextColor(getResources().getColor(R.color.air_quality_2));
            tv_l.setText("优");
            tv_l.setTextColor(getResources().getColor(R.color.air_quality_1));
            tv_y.setText("优");
            tv_y.setTextColor(getResources().getColor(R.color.air_quality_1));
        } else {
            tv_ys.setText("中度污染");
            tv_ys.setTextColor(getResources().getColor(R.color.air_quality_4));
            tv_zd.setText("轻度污染");
            tv_zd.setTextColor(getResources().getColor(R.color.air_quality_3));
            tv_zdwr.setText("轻度污染");
            tv_zdwr.setTextColor(getResources().getColor(R.color.air_quality_3));
            tv_qd.setText("良");
            tv_qd.setTextColor(getResources().getColor(R.color.air_quality_2));
            tv_l.setText("优");
            tv_l.setTextColor(getResources().getColor(R.color.air_quality_1));
            tv_y.setText("优");
            tv_y.setTextColor(getResources().getColor(R.color.air_quality_1));
        }

    }

    private int screenwidth = 0;

    /**
     * 显示站点下拉框
     */
    private void showStationPopup() {
        List<String> listString = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            listString.add(list.get(i).position_name);
        }
        AdapterAirStations adapter = new AdapterAirStations(this, listString);
        View view = LayoutInflater.from(ActivityAirQualityQuery.this).inflate(R.layout.pop_airlist_layout, null);
        ListView listView = (ListView) view.findViewById(R.id.myairlistviw);
//        listView.setCacheColorHint(0);
//        listView.setBackgroundResource(R.drawable.air_alpha_all);
        listView.setAdapter(adapter);

        final PopupWindow pop = new PopupWindow(ActivityAirQualityQuery.this);
        pop.setContentView(view);
        pop.setOutsideTouchable(false);
        pop.setWidth(screenwidth * 3 / 7);
        if (list.size() < 5) {
            pop.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        } else {
            pop.setHeight(getResources().getDimensionPixelOffset(R.dimen.dimen165));
        }
        pop.setFocusable(true);

        pop.showAsDropDown(iv_choose_station);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    pop.dismiss();
                    tv_choose_station.setText(list.get(position).position_name);
                    showProgressDialog();
                    areatype = "2";
                    if (position == 0) {
                        //mPackInfoUp.area = s_area_id;
                        //control.reqData(ControlDistribution.ColumnCategory.TEMPERATURE, s_area_id, "1", "aqi");
                        //PcsDataDownload.addDownload(mPackInfoUp);
                        okHttpAirTrend("aqi");
                        okHttpAirCityStation();
                        s_show_city = true;
                    } else {
                        s_show_city = false;
                        station_id = list.get(position).station_code;
                        station_name = list.get(position).position_name;
                        //mPackStationInfoUp.station_name = station_name;
                        //mPackStationInfoUp.time_num = 1;
                        //control.reqData(ControlDistribution.ColumnCategory.TEMPERATURE, station_id, areatype, "aqi");
                        okHttpAirTrend("aqi");
                        //PcsDataDownload.addDownload(mPackStationInfoUp);
                        reqAirStationInfo(station_name, 1);
                    }
                    handler.removeCallbacks(runnableProgress);
//                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private String station_name, station_id;

    public void getTvColor(int aqi) {
        curProgress = 0;
        if (aqi <= 50) {
            // 优
            tv_aqi.setTextColor(getResources().getColor(R.color.air_quality_1));
//            tv_aqi.setBackgroundResource(R.drawable.design_circle);
            tv_quality.setTextColor(getResources().getColor(R.color.air_quality_1));
            tv_aqi_name.setTextColor(getResources().getColor(R.color.air_quality_1));
            tv_quality.setText("优");
        } else if (aqi > 50 && aqi <= 100) {
            // 良
            tv_aqi.setTextColor(getResources().getColor(R.color.air_quality_2));
            //tv_aqi.setBackgroundResource(R.drawable.design_circle2);
            tv_quality.setTextColor(getResources().getColor(R.color.air_quality_2));
            tv_aqi_name.setTextColor(getResources().getColor(R.color.air_quality_2));
            tv_quality.setText("良");
        } else if (aqi > 100 && aqi <= 150) {
            // 轻度污染
            tv_aqi.setTextColor(getResources().getColor(R.color.air_quality_3));
            // tv_aqi.setBackgroundResource(R.drawable.design_circle3);
            tv_quality.setTextColor(getResources().getColor(R.color.air_quality_3));
            tv_aqi_name.setTextColor(getResources().getColor(R.color.air_quality_3));
            tv_quality.setText("轻度污染");
        } else if (aqi > 150 && aqi <= 200) {
            // 中度污染
            tv_aqi.setTextColor(getResources().getColor(R.color.air_quality_4));
            //tv_aqi.setBackgroundResource(R.drawable.design_circle4);
            tv_quality.setTextColor(getResources().getColor(R.color.air_quality_4));
            tv_aqi_name.setTextColor(getResources().getColor(R.color.air_quality_4));
            tv_quality.setText("中度污染");
        } else if (aqi > 200 && aqi <= 300) {
            // 重度污染
            tv_aqi.setTextColor(getResources().getColor(R.color.air_quality_5));
            //tv_aqi.setBackgroundResource(R.drawable.design_circle5);
            tv_quality.setTextColor(getResources().getColor(R.color.air_quality_5));
            tv_aqi_name.setTextColor(getResources().getColor(R.color.air_quality_5));
            tv_quality.setText("重度污染");
        } else if (aqi > 300) {
            // 严重污染
            tv_aqi.setTextColor(getResources().getColor(R.color.air_quality_6));
            // tv_aqi.setBackgroundResource(R.drawable.design_circle6);
            tv_quality.setTextColor(getResources().getColor(R.color.air_quality_6));
            tv_aqi_name.setTextColor(getResources().getColor(R.color.air_quality_6));
            tv_quality.setText("严重污染");
        }
    }

    /**
     * 获取空气质量实况、排名信息
     */
    private void okHttpAirCityStation() {
        dismissProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    JSONObject info = new JSONObject();
                    info.put("stationId", s_area_id);
                    param.put("paramInfo", info);
                    String json = param.toString();
                    String url = CONST.BASE_URL + "air_city_station";
                    Log.e("air_city_station", url);
                    RequestBody body = FormBody.create(MediaType.parse("application/json; charset=utf-8"), json);
                    OkHttpUtil.enqueue(new Request.Builder().post(body).url(url).build(), new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            Log.e("onFailure", e.getMessage());
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            if (!response.isSuccessful()) {
                                return;
                            }
                            final String result = response.body().string();
                            Log.e("air_city_station", result);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        if (!TextUtil.isEmpty(result)) {
                                            JSONObject obj = new JSONObject(result);
                                            if (!obj.isNull("b")) {
                                                JSONObject bobj = obj.getJSONObject("b");
                                                if (!bobj.isNull("airinfo_one")) {
                                                    JSONObject airinfo_one = bobj.getJSONObject("airinfo_one");
                                                    if (!TextUtil.isEmpty(airinfo_one.toString())) {
                                                        PackAirInfoDown packAirInfoDown = new PackAirInfoDown();
                                                        packAirInfoDown.fillData(airinfo_one.toString());
                                                        refreshData(packAirInfoDown, 300);
                                                        tv_city_num.setText(packAirInfoDown.city_num + "/");
                                                        tv_city_total.setText(packAirInfoDown.totalCity + "位");
                                                        tv_pub_time.setText(packAirInfoDown.pub_time+ " 更新");
                                                        try {
                                                            tv_pub_time.setText(sdf2.format(sdf1.parse(packAirInfoDown.pub_time)) + " 更新");
                                                        } catch (ParseException e) {
                                                            e.printStackTrace();
                                                        }
                                                        tv_airquality_name.setText(packAirInfoDown.pub_unit);
                                                    }
                                                }
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
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
     * 获取空气质量等级
     */
    private void okHttpAirLev() {
        showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    String json = param.toString();
                    String url = CONST.BASE_URL + "air_lev";
                    Log.e("air_lev", url);
                    RequestBody body = FormBody.create(MediaType.parse("application/json; charset=utf-8"), json);
                    OkHttpUtil.enqueue(new Request.Builder().post(body).url(url).build(), new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            Log.e("onFailure", e.getMessage());
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            if (!response.isSuccessful()) {
                                return;
                            }
                            final String result = response.body().string();
                            Log.e("air_lev", result);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        if (!TextUtil.isEmpty(result)) {
                                            JSONObject obj = new JSONObject(result);
                                            if (!obj.isNull("b")) {
                                                JSONObject bobj = obj.getJSONObject("b");
                                                if (!bobj.isNull("air_lev")) {
                                                    JSONObject oobj = bobj.getJSONObject("air_lev");
                                                    if (!TextUtil.isEmpty(oobj.toString())) {
                                                        dismissProgressDialog();
                                                        // 加载数据
                                                        PackAirLevelDown airLevelDown = new PackAirLevelDown();
                                                        airLevelDown.fillData(oobj.toString());
                                                        list_level.clear();
                                                        list_level.addAll(airLevelDown.list);
                                                        okHttpAirTrend("aqi");
                                                    }
                                                }
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
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
     * 获取空气质量趋势数据，图表
     */
    private void okHttpAirTrend(final String airType) {
        showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    JSONObject info = new JSONObject();
                    info.put("stationId", s_area_id);
                    info.put("airType", airType);//aqi/pm2/pm10/no2/so2/co/o3
                    param.put("paramInfo", info);
                    String json = param.toString();
                    String url = CONST.BASE_URL + "air_trend";
                    Log.e("air_trend", url);
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
                                    try {
                                        Log.e("air_trend", result);
                                        dismissProgressDialog();
                                        if (!TextUtil.isEmpty(result)) {
                                            JSONObject obj = new JSONObject(result);
                                            if (!obj.isNull("b")) {
                                                JSONObject bobj = obj.getJSONObject("b");
                                                if (!bobj.isNull("air_trend")) {
                                                    JSONObject air_trend = bobj.getJSONObject("air_trend");
                                                    if (!TextUtil.isEmpty(air_trend.toString())) {
                                                        PackAirTrendDown airTrendDown = new PackAirTrendDown();
                                                        airTrendDown.fillData(air_trend.toString());
                                                        reFlushList(airTrendDown);
                                                    }
                                                }
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
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

    private void okHttpAirRemark() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    String json = param.toString();
                    final String url = CONST.BASE_URL + "air_remark";
                    Log.e("air_remark", url);
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
                            Log.e("air_remark", result);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dismissProgressDialog();
                                    try {
                                        JSONObject obj = new JSONObject(result);
                                        if (!obj.isNull("b")) {
                                            JSONObject bobj = obj.getJSONObject("b");
                                            if (!bobj.isNull("air_remark")) {
                                                JSONObject air_remark = bobj.getJSONObject("air_remark");
                                                PackKeyDescDown packKey = new PackKeyDescDown();
                                                packKey.fillData(air_remark.toString());
                                                dealWidthKeyData(packKey);
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
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
