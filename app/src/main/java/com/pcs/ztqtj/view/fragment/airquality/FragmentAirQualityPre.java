package com.pcs.ztqtj.view.fragment.airquality;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import com.pcs.lib.lib_pcs_v3.control.tool.ScreenUtil;
import com.pcs.lib.lib_pcs_v3.control.tool.Util;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib.lib_pcs_v3.model.pack.PcsPackDown;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityMain;
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
import com.pcs.ztqtj.control.tool.utils.TextUtil;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.util.OkHttpUtil;
import com.pcs.ztqtj.view.activity.air_quality.AcitvityAirWhatAQI;
import com.pcs.ztqtj.view.activity.newairquality.ActivityAir;
import com.pcs.ztqtj.view.activity.newairquality.ActivityAirQualityRandking;
import com.pcs.ztqtj.view.myview.AirQualityView;
import com.pcs.ztqtj.view.myview.CircleProgressView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
 * 监测预报-空气质量-空气质量实况，天津
 */
public class FragmentAirQualityPre extends Fragment implements View.OnClickListener {

    private AirQualityView airQueryView;
    private TextView null_air_data, tv_choose_station;
    private LinearLayout lay_citiao, lay_PM2, lay_PM10, lay_CO, lay_NO2, lay_SO2, lay_031h, lay_038h;
    private MyReceiver receiver = new MyReceiver();
    private LinearLayout lay_airRanking, lay_choose_station;
    private ImageView iv_choose_station;
    /**
     * 城市ID
     */
    private String s_area_id;
    /**
     * 城市名称
     */
    private String s_area_name;

    private TextView tv_038h, tv_031h, tv_pm2, tv_pm10, tv_co, tv_no2, tv_so2, tv_aqi, tv_quality, tv_city_num,
            tv_pub_time, tv_healthy, tv_airquality_name, tv_aqi_name, tv_city_total;
    /**
     * 上传包(站点)
     */
    private final PackAirStationInfoUp mPackStationInfoUp = new PackAirStationInfoUp();
    private LinearLayout llMap;
    private CircleProgressView circle_progress_view;
    private RelativeLayout rel_circle_aqi;
    private TextView tv_type;
    private String type="0";

    private TextView tv_ys, tv_zd, tv_zdwr, tv_qd, tv_l, tv_y;
    private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH", Locale.CHINA);
    private SimpleDateFormat sdf2 = new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(getActivity()).inflate(R.layout.fragment_airqualityquery, null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        showProgressDialog();
        PackLocalCityMain cityMain = ZtqCityDB.getInstance().getCityMain();
        if (s_area_id == null) {
            s_area_id = cityMain.ID;
            s_area_name = cityMain.NAME;
        }
        okHttpAirLev();
        if (cityMain.isFjCity) {
            tv_choose_station.setText("天津总体");
        } else {
            tv_choose_station.setText(s_area_name + "总体");
        }
        okHttpAirRemark();
        okHttpAirCityStation();
        reqStationList2();
    }

    public void setDate(String id, String name) {
        this.s_area_id = id;
        this.s_area_name = name;
    }

    public void dataIsNull() {
        null_air_data.setVisibility(View.VISIBLE);
    }

    private void initView() {
        null_air_data = (TextView) getActivity().findViewById(R.id.null_air_data);
        airQueryView = (AirQualityView) getActivity().findViewById(R.id.airQueryView);
        airQueryView.setItemName("", AirQualityView.IsDrawRectangele.BROKENLINE);
        airQueryView.setClickPositionListener(clicklistener);
        tv_ys = getActivity().findViewById(R.id.tv_level_ys);
        tv_zd = getActivity().findViewById(R.id.tv_level_zd);
        tv_zdwr = getActivity().findViewById(R.id.tv_level_zdwr);
        tv_qd = getActivity().findViewById(R.id.tv_level_qd);
        tv_l = getActivity().findViewById(R.id.tv_level_l);
        tv_y = getActivity().findViewById(R.id.tv_level_y);
        lay_citiao = (LinearLayout) getActivity().findViewById(R.id.lay_citiao);
        lay_citiao.setOnClickListener(this);
        lay_PM2 = (LinearLayout) getActivity().findViewById(R.id.lay_PM2);
        lay_PM2.setOnClickListener(this);
        lay_PM10 = (LinearLayout) getActivity().findViewById(R.id.lay_PM10);
        lay_PM10.setOnClickListener(this);
        lay_CO = (LinearLayout) getActivity().findViewById(R.id.lay_CO);
        lay_CO.setOnClickListener(this);
        lay_NO2 = (LinearLayout) getActivity().findViewById(R.id.lay_N02);
        lay_NO2.setOnClickListener(this);
        lay_SO2 = (LinearLayout) getActivity().findViewById(R.id.lay_SO2);
        lay_SO2.setOnClickListener(this);
        lay_031h = (LinearLayout) getActivity().findViewById(R.id.lay_031h);
        lay_031h.setOnClickListener(this);
        lay_038h = (LinearLayout) getActivity().findViewById(R.id.lay_038h);
        lay_038h.setOnClickListener(this);
        tv_type = getActivity().findViewById(R.id.tv_type);
        lay_airRanking = (LinearLayout) getActivity().findViewById(R.id.lay_airRanking);
        lay_airRanking.setOnClickListener(this);
        lay_choose_station = (LinearLayout) getActivity().findViewById(R.id.lay_choose_station);
        lay_choose_station.setOnClickListener(this);
        llMap = (LinearLayout) getActivity().findViewById(R.id.ll_map);
        llMap.setOnClickListener(this);
        iv_choose_station = (ImageView) getActivity().findViewById(R.id.iv_choose_station);
        tv_airquality_name = (TextView) getActivity().findViewById(R.id.tv_airquality_name);

        tv_choose_station = (TextView) getActivity().findViewById(R.id.tv_choose_station);
        tv_city_num = (TextView) getActivity().findViewById(R.id.tv_city_num);
        tv_city_total = getActivity().findViewById(R.id.tv_city_total);
        tv_pub_time = (TextView) getActivity().findViewById(R.id.tv_pub_time);
        tv_healthy = (TextView) getActivity().findViewById(R.id.tv_healthy);

        tv_aqi = (TextView) getActivity().findViewById(R.id.tv_aqi);
        tv_031h = (TextView) getActivity().findViewById(R.id.tv_031h);
        tv_pm2 = (TextView) getActivity().findViewById(R.id.tv_pm2);
        tv_pm10 = (TextView) getActivity().findViewById(R.id.tv_pm10);
        tv_co = (TextView) getActivity().findViewById(R.id.tv_co);
        tv_no2 = (TextView) getActivity().findViewById(R.id.tv_no2);
        tv_so2 = (TextView) getActivity().findViewById(R.id.tv_so2);
        tv_038h = (TextView) getActivity().findViewById(R.id.tv_038h);
        tv_quality = (TextView) getActivity().findViewById(R.id.tv_quality);

        circle_progress_view = (CircleProgressView) getActivity().findViewById(R.id.circle_progress_view);
        rel_circle_aqi = (RelativeLayout) getActivity().findViewById(R.id.rel_circle_aqi);
        rel_circle_aqi.setOnClickListener(this);
        tv_aqi_name = (TextView) getActivity().findViewById(R.id.tv_aqi_name);

        screenwidth = Util.getScreenWidth(getActivity());
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
            //handler自带方法实现定时器
        }
    };

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

            airQueryView.setNewData(trendDown.skList, str,type);
            if (hasValue) {
                null_air_data.setVisibility(View.GONE);
            } else {
                null_air_data.setVisibility(View.VISIBLE);
            }
            dismissProgressDialog();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        CommUtils.closeKeyboard(getActivity());
        PcsDataBrocastReceiver.registerReceiver(getActivity(), receiver);
    }

    private List<PackAirStationDown.PackAirStation> list = new ArrayList<>();
    private PackAirStationUp mPackStationUp = new PackAirStationUp();

    private void reqStationList2() {
        mPackStationUp.area_id = s_area_id;
        NetTask task = new NetTask(getActivity(), new NetTask.NetListener() {
            @Override
            public void onComplete(PcsPackDown down) {
                // 加载数据
                mPackStationDown = (PackAirStationDown) down;
                // 弹出对话框
                //showDialogStation();

                list.clear();
                PackAirStationDown.PackAirStation pack = new PackAirStationDown.PackAirStation();
                pack.position_name = "天津总体";
                pack.station_code = s_area_id;
                list.add(pack);
                if (mPackStationDown == null) {
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
        NetTask task = new NetTask(getActivity(), new NetTask.NetListener() {
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

    private ArrayList<PackAirLevelDown.AirLecel> list_level = new ArrayList<>();

    @Override
    public void onPause() {
        super.onPause();
        PcsDataBrocastReceiver.unregisterReceiver(getActivity(), receiver);
    }

    private PackKeyDescDown packKey = new PackKeyDescDown();

    private int aqi;

    public void refreshData(PackAirInfoDown packAirInfoDown, int delay) {
        tv_aqi.setText(packAirInfoDown.aqi);
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
            String tipStr = AirQualityTool.getInstance().getHealthTip(tipArr,aqi);
            tv_healthy.setText(tipStr);
            getTvColor(aqi);
            handler.postDelayed(runnableProgress, delay);
        }
    }

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
                pack.position_name = "天津总体";
                pack.station_code = s_area_id;
                list.add(pack);
                if (mPackStationDown == null) {
                    return;
                }
                if (mPackStationDown.list.size() == 0) {
//                    Toast.makeText(getActivity(), "暂无站点",
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
                PackAirStationInfoDown mPackStationInfoDown = (PackAirStationInfoDown) PcsDataManager.getInstance().getNetPack(mPackStationInfoUp.getName());
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
            if (dataeaum.get(0).rankType.toLowerCase().equals("aqi")) {
            } else {
                changeValueKey(0);
            }
        }
    }

    private void changeValueKey(int position) {
        try {
            if (packKey.dicList == null || packKey.dicList.size() == 0) {

            } else {
                Intent intent = new Intent(getActivity(), AcitvityAirWhatAQI.class);
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
            case R.id.rel_circle_aqi:
                type="0";
                setTextContentColor("0");
                tv_type.setText("AQI走势图");
                okHttpAirTrend("aqi");
                break;
            case R.id.lay_PM2:
                type="0";
                setTextContentColor("0");
                tv_type.setText("PM2.5走势图");
                okHttpAirTrend("pm2");
                break;
            case R.id.lay_PM10:
                type="0";
                setTextContentColor("0");
                tv_type.setText("PM10走势图");
                okHttpAirTrend("pm10");
                break;
            case R.id.lay_CO:
                type="1";
                setTextContentColor("1");
                tv_type.setText("CO走势图");
                okHttpAirTrend("co");
                break;
            case R.id.lay_N02:
                type="2";
                setTextContentColor("2");
                tv_type.setText("NO₂走势图");
                okHttpAirTrend("no2");
                break;
            case R.id.lay_SO2:
                type="3";
                setTextContentColor("1");
                tv_type.setText("SO₂走势图");
                okHttpAirTrend("so2");
                break;
            case R.id.lay_031h:
                type="4";
                setTextContentColor("3");
                tv_type.setText("O₃-1H走势图");
                okHttpAirTrend("o3");
                break;
            case R.id.lay_038h:
                tv_type.setText("O₃-8H走势图");
                okHttpAirTrend("o3");
                break;
            case R.id.lay_airRanking:
                Intent intent = new Intent(getActivity(), ActivityAirQualityRandking.class);
                intent.putExtra("name", s_area_name);
                startActivity(intent);
                break;
            case R.id.lay_choose_station:
                showStationPopup();
                break;
            case R.id.ll_map:
                startActivity(new Intent(getActivity(), ActivityAir.class));
                break;
//            case R.id.btn_right:
//                View layout = getActivity().findViewById(R.id.all_view);
//                Bitmap shareBitmap = BitmapUtil.takeScreenShot(getActivity());
////                Bitmap shareBitmap = ZtqImageTool.getInstance().getScreenBitmap(layout);
//                shareBitmap = ZtqImageTool.getInstance().stitchQR(getActivity(), shareBitmap);
//                ShareTools.getInstance(getActivity()).setShareContent(getTitleText() + " " + mShare,
//                        shareBitmap, "0").showWindow(layout);
//                break;
//            case R.id.btn_right:
//                showProgressDialog();
//                reqKey();
//                if (s_show_city) {
//                    reqStationList();
//                    control.reqData(ControlDistribution.ColumnCategory.TEMPERATURE, s_area_id, "1", "aqi");
//                } else {
//                    control.reqData(ControlDistribution.ColumnCategory.TEMPERATURE, station_id, "2", "aqi");
//                }
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
        AdapterAirStations adapter = new AdapterAirStations(getActivity(), listString);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.pop_airlist_layout, null);
        ListView listView = (ListView) view.findViewById(R.id.myairlistviw);
//        listView.setCacheColorHint(0);
//        listView.setBackgroundResource(R.drawable.air_alpha_all);
        listView.setAdapter(adapter);

        final PopupWindow pop = new PopupWindow(getActivity());
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
                    if (position == 0) {
                        okHttpAirTrend("AQI");
                        okHttpAirCityStation();
                    } else {
                        station_id = list.get(position).station_code;
                        station_name = list.get(position).position_name;
                        okHttpAirTrend("AQI");
                        reqAirStationInfo(station_name, 1);
                    }
                    handler.removeCallbacks(runnableProgress);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 将长时间格式字符串转换为时间 yyyy-MM-dd HH:mm:ss
     * @param strDate
     * @return
     */
    public String strToDateLong(String strDate) {
        Date d1 = null;
        try {
            d1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(strDate);//定义起始日期
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (d1 == null) {
            return "";
        }

        SimpleDateFormat sdf0 = new SimpleDateFormat("MM");

        SimpleDateFormat sdf1 = new SimpleDateFormat("dd");

        SimpleDateFormat sdf2 = new SimpleDateFormat("HH");

        SimpleDateFormat sdf3 = new SimpleDateFormat("mm");

        String dates = sdf0.format(d1) + "-" + sdf1.format(d1) + " " + sdf2.format(d1) + ":" + sdf3.format(d1);
        return dates;
    }

    private String station_name, station_id;

    public void getTvColor(int aqi) {
        curProgress = 0;
        //circle_progress_view.setProgress(curProgress,aqi);
        //handler.post(runnableProgress);

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

    // 等待对话框
    private ProgressDialog mProgress;

    /**
     * 显示等待对话框
     */
    public void showProgressDialog(String keyWord) {
        if (mProgress == null) {
            mProgress = new ProgressDialog(getActivity());
            mProgress.setCancelable(true);
            mProgress.setCanceledOnTouchOutside(false);
            mProgress.setOnCancelListener(mProgressOnCancel);
        }
        if (mProgress.isShowing()) {
            mProgress.setMessage(keyWord);
        } else {
            mProgress.show();
            mProgress.setMessage(keyWord);
        }
    }

    public void showProgressDialog() {
        showProgressDialog(getResources().getString(R.string.please_wait));
    }

    /**
     * 取消等待对话框
     */
    public void dismissProgressDialog() {
        if (mProgress != null && mProgress.isShowing()) {
            mProgress.dismiss();
        }
    }

    /**
     * 进度框OnCancel
     */
    private DialogInterface.OnCancelListener mProgressOnCancel = new DialogInterface.OnCancelListener() {
        @Override
        public void onCancel(DialogInterface dialog) {
            getActivity().finish();
        }
    };

    /**
     * java.lang.IllegalStateException: No activity
     * 错误解决方案
     */
    @Override
    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

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
        View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.pop_livequery, null);
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
        int width = ScreenUtil.dip2px(getActivity(), 80);
        int hight = ScreenUtil.dip2px(getActivity(), 50);
        if (widtha > width) {
            width = widtha;
        }
        int scW = getActivity().getWindowManager().getDefaultDisplay().getWidth();
        if ((x - width / 2) < 0 || (x + width / 2) > scW) {
            return;
        }
        popupWindow.setWidth(width);
        popupWindow.setHeight(hight);
        popupWindow.showAsDropDown(view, x - width / 2, -(y + hight));
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
                    JSONObject param  = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    String json = param.toString();
                    String url = CONST.BASE_URL+"air_lev";
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
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dismissProgressDialog();
                                    try {
                                        Log.e("air_lev", result);
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
     * 获取空气质量实况、排名信息
     */
    private void okHttpAirCityStation() {
        showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param  = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    JSONObject info = new JSONObject();
                    info.put("stationId", s_area_id);
                    param.put("paramInfo", info);
                    String json = param.toString();
                    String url = CONST.BASE_URL+"air_city_station";
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
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dismissProgressDialog();
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
                                                        // 刷新数据
                                                        refreshData(packAirInfoDown, 300);
                                                        tv_city_num.setText(packAirInfoDown.city_num + "/");
                                                        tv_city_total.setText(packAirInfoDown.totalCity + "位");
                                                        tv_pub_time.setText(packAirInfoDown.pub_time + " 更新");
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

    private void okHttpAirRemark() {
        showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param  = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    String json = param.toString();
                    final String url = CONST.BASE_URL+"air_remark";
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
                            getActivity().runOnUiThread(new Runnable() {
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

    /**
     * 获取空气质量趋势数据，图表
     */
    private void okHttpAirTrend(final String airType) {
        showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param  = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    JSONObject info = new JSONObject();
                    info.put("stationId", s_area_id);
                    info.put("airType", airType);//aqi/pm2/pm10/no2/so2/co/o3
                    param.put("paramInfo", info);
                    String json = param.toString();
                    String url = CONST.BASE_URL+"air_trend";
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
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dismissProgressDialog();
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

}
