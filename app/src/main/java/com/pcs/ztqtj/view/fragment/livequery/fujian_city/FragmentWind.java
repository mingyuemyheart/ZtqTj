package com.pcs.ztqtj.view.fragment.livequery.fujian_city;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.pcs.lib.lib_pcs_v3.control.tool.Util;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCity;
import com.pcs.lib_ztqfj_v2.model.pack.net.column.ColumnInfo;
import com.pcs.lib_ztqfj_v2.model.pack.net.column.PackColumnDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.column.PackColumnUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackFltjHourDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackFltjZdDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackFltjZdDown.FltjZd;
import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.livequery.AdapterWind;
import com.pcs.ztqtj.control.inter.DrowListClick;
import com.pcs.ztqtj.control.tool.utils.TextUtil;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.util.OkHttpUtil;
import com.pcs.ztqtj.view.activity.livequery.ActivityLiveQuery;
import com.pcs.ztqtj.view.activity.livequery.ActivityLiveQueryDetail;
import com.pcs.ztqtj.view.fragment.livequery.FragmentLiveQueryCommon;
import com.pcs.ztqtj.view.myview.MyListView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
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
 * 监测预报-实况查询-数据与统计-风况查询
 */
public class FragmentWind extends FragmentLiveQueryCommon implements OnClickListener {
    private MyListView livequery_auto_rainfall;
    private ActivityLiveQuery activity;
    private TextView livequery_city_spinner;
    private TextView livequery_town_spinner;
    private TextView description_title_search2;
    private RadioGroup windradiogroup;

    /**
     * 列表使用数据-本市自动站统计表
     */
    private List<FltjZd> windAutoList;

    private AdapterWind autoatper;

    // 列表头标题
    private FltjZd titleMaxRain;

    private String currentStationId = "10103";

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (ActivityLiveQuery) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragement_livequery_wind, null);
    }

    public void refleshData() {
        reqAll();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initData();
        initEvent();
    }

    /**
     * 初始化视图
     */
    private void initView() {
        windradiogroup = (RadioGroup) getView().findViewById(R.id.windradiogroup);
        livequery_city_spinner = (TextView) getView().findViewById(R.id.livequery_city_spinner);
        livequery_town_spinner = (TextView) getView().findViewById(R.id.livequery_town_spinner);
        livequery_auto_rainfall = (MyListView) getView().findViewById(R.id.livequery_auto_min_table);
        description_title_search2 = (TextView) getView().findViewById(R.id.description_title_search2);
        gd_wind = (RadioGroup) getView().findViewById(R.id.gd_wind);
    }

    public List<ColumnInfo> arrcolumnInfo;

    private void addRadioButtom() {
        arrcolumnInfo = new ArrayList<>();
        ColumnInfo info = new ColumnInfo();
        info.name = "近24小时";
        info.type = "24";
        arrcolumnInfo.add(info);
        info = new ColumnInfo();
        info.name = "近12小时";
        info.type = "12";
        arrcolumnInfo.add(info);
        info = new ColumnInfo();
        info.name = "近6小时";
        info.type = "6";
        arrcolumnInfo.add(info);
        info = new ColumnInfo();
        info.name = "近3小时";
        info.type = "3";
        arrcolumnInfo.add(info);
        info = new ColumnInfo();
        info.name = "近1小时";
        info.type = "1";
        arrcolumnInfo.add(info);
        info = new ColumnInfo();
        info.name = "本时次";
        info.type = "0";
        arrcolumnInfo.add(info);
        int len=0;
        if (cityControl.getCutParentCity().NAME.equals("天津")){
            len=arrcolumnInfo.size();
        }else{
            len=arrcolumnInfo.size()-1;
        }
        int width = Util.getScreenWidth(getActivity());
        int radioWidth = width / len;
        int pad = Util.dip2px(getActivity(), 6);
        gd_wind.removeAllViews();
        for (int i = 0; i < len; i++) {
            RadioButton radioButton = new RadioButton(getActivity());
            //radioButton.setId(101 + i);
            radioButton.setTextColor(getResources().getColor(R.color.text_black));
            radioButton.setBackgroundResource(R.drawable.radio_rain_selet);
            radioButton.setPadding(0, pad, 0, pad);
            radioButton.setMaxLines(1);
            radioButton.setGravity(Gravity.CENTER);
            radioButton.setTextSize(14);
            radioButton.setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));
            radioButton.setText(arrcolumnInfo.get(i).name);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(radioWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
            gd_wind.addView(radioButton, lp);
            if(i == 0) {
                gd_wind.check(radioButton.getId());
            }
        }
    }

    private RadioGroup gd_wind;
    private CityListControl cityControl;

    /**
     * 初始化数据
     */
    private void initData() {
        windAutoList = new ArrayList<FltjZd>();
        // 列表头部说明
        titleMaxRain = new PackFltjZdDown().new FltjZd();
        titleMaxRain.county = "站点";
        titleMaxRain.time = "日期/时段";
        titleMaxRain.winddirection = "风向";
        titleMaxRain.windFengLi = "风力";
        titleMaxRain.windpower = "风速m/s";
        windAutoList.add(titleMaxRain);
        cityControl = new CityListControl(activity.cityinfo);
        livequery_city_spinner.setText(cityControl.getCutParentCity().NAME);
        if (cityControl.getCutParentCity().isFjCity) {
            livequery_town_spinner.setText(cityControl.getCutParentCity().NAME);
            currentStationId = cityControl.getCutParentCity().ID;
        } else {
            livequery_town_spinner.setText(cityControl.getCutChildCity().NAME);
            currentStationId = cityControl.getCutChildCity().ID;
        }

        if (livequery_city_spinner.getText().toString().equals("天津")){
            description_title_search2.setText(cityControl.getCutChildCity().NAME + " 自动站瞬时风况统计表");
        }else{
            description_title_search2.setText(cityControl.getCutChildCity().NAME + " 瞬时风况统计表");
        }
        autoatper = new AdapterWind(getActivity(), windAutoList);
        livequery_auto_rainfall.setAdapter(autoatper);
        okHttpRankWind();
        addRadioButtom();
    }

    private void initEvent() {
        livequery_auto_rainfall.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    String stationName = windAutoList.get(position).county;
                    if (stationName.equals("全部")) {
                        return;
                    }
                    Intent intent = new Intent(getActivity(), ActivityLiveQueryDetail.class);
                    intent.putExtra("stationName", stationName);
                    intent.putExtra("item", "wind");
                    startActivity(intent);
                }
            }
        });

        livequery_city_spinner.setOnClickListener(this);
        livequery_town_spinner.setOnClickListener(this);
        windradiogroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // 页面数据刷新
                switch (checkedId) {
                    case R.id.windradiogroupleft:
                        gd_wind.setVisibility(View.GONE);
                        if (livequery_city_spinner.getText().toString().equals("天津")){
                            description_title_search2.setText(livequery_town_spinner.getText().toString().trim() + " 自动站瞬时风况统计表");
                        }else{
                            description_title_search2.setText(livequery_town_spinner.getText().toString().trim() + " 瞬时风况统计表");
                        }
                        okHttpRankWind();
                        break;
                    case R.id.windradiogroupright:
                        gd_wind.setVisibility(View.VISIBLE);
                        if(gd_wind.getChildCount() == 0) {
                            return;
                        }
                        int firstRadioButtonid = gd_wind.getChildAt(0).getId();
                        int checkId = gd_wind.getCheckedRadioButtonId();
                        if (checkId == firstRadioButtonid) {
                            reqMaxWind(0);
                        } else {
                            if (gd_wind != null) {
                                gd_wind.check(firstRadioButtonid);
                            }
                        }
                        break;
                }
            }
        });

        gd_wind.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                for(int i = 0; i < group.getChildCount(); i++) {
                    View view = group.getChildAt(i);
                    if(view.getId() == checkedId) {
                        reqMaxWind(i);
                        break;
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.livequery_city_spinner:
                activity.createPopupWindow(livequery_city_spinner, cityControl.getParentShowNameList(), 0, listener).showAsDropDown(livequery_city_spinner);
                break;
            case R.id.livequery_town_spinner:
                PackLocalCity city = cityControl.getCutParentCity();
//                if(!city.isFjCity || ZtqCityDB.getInstance().isServiceAccessible()) {
                    activity.createPopupWindow(livequery_town_spinner, cityControl.getChildShowNameList(), 1, listener).showAsDropDown(livequery_town_spinner);
//                }
                break;
        }
    }

    private DrowListClick listener = new DrowListClick() {
        @Override
        public void itemClick(int floag, int item) {
            switch (floag) {
                case 0:
                    cityControl.checkParent(item);
                    livequery_town_spinner.setText(cityControl.getCutChildCity().NAME);
                    down_draw_select();
                    addRadioButtom();
                    break;
                case 1:
                    cityControl.checkChild(item);
                    currentStationId = cityControl.getCutChildCity().ID;
                    down_draw_select();
                    break;
            }
        }

        private void down_draw_select() {
            windAutoList.clear();
            autoatper.notifyDataSetChanged();
            activity.showProgressDialog();
            //windradiogroup.check(R.id.windradiogroupleft);
            if (livequery_city_spinner.getText().toString().equals("天津")){
                description_title_search2.setText(cityControl.getCutChildCity().NAME + " 自动站瞬时风况统计表");
            }else{
                description_title_search2.setText(cityControl.getCutChildCity().NAME + " 瞬时风况统计表");
            }

            reqAll();
        }
    };

    private void reqAll() {
        switch (windradiogroup.getCheckedRadioButtonId()) {
            case R.id.windradiogroupleft:
                okHttpRankWind();
                break;
            case R.id.windradiogroupright:
                reqMaxWind(currentMaxWindIndex);
                break;
        }
    }

    private int currentMaxWindIndex = 0;

    private void reqMaxWind(int item) {
        currentMaxWindIndex = item;
        activity.showProgressDialog();
        if (arrcolumnInfo == null || arrcolumnInfo.size() == 0 || item >= arrcolumnInfo.size()) {
            return;
        }
        if (livequery_city_spinner.getText().toString().equals("天津")){
            description_title_search2.setText(livequery_town_spinner.getText().toString().trim() + " 自动站" + arrcolumnInfo.get(item).name + "极大风速统计表");
        }else{
            description_title_search2.setText(livequery_town_spinner.getText().toString().trim() + " " + arrcolumnInfo.get(item).name + "极大风速统计表");
        }

        okHttpWindMax(arrcolumnInfo.get(item).type);
    }

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

    /**
     * 获取风况排行
     */
    private void okHttpRankWind() {
        activity.showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    JSONObject info = new JSONObject();
                    info.put("stationId", currentStationId);
                    param.put("paramInfo", info);
                    String json = param.toString();
                    Log.e("datastatis_wd", json);
                    String url = CONST.BASE_URL+"datastatis_wd";
                    Log.e("datastatis_wd", url);
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
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.e("datastatis_wd", result);
                                    if (!TextUtil.isEmpty(result)) {
                                        try {
                                            JSONObject obj = new JSONObject(result);
                                            if (!obj.isNull("b")) {
                                                JSONObject bobj = obj.getJSONObject("b");
                                                if (!bobj.isNull("fltj_hour")) {
                                                    JSONObject fltj_zd = bobj.getJSONObject("fltj_hour");
                                                    if (!TextUtil.isEmpty(fltj_zd.toString())) {
                                                        activity.dismissProgressDialog();
                                                        PackFltjZdDown fltjZdDown = new PackFltjZdDown();
                                                        fltjZdDown.fillData(fltj_zd.toString());
                                                        windAutoList.clear();
                                                        windAutoList.add(titleMaxRain);
                                                        windAutoList.addAll(fltjZdDown.datalist);
                                                        autoatper.setData(windAutoList);
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
     * 获取极大风速
     */
    private void okHttpWindMax(final String hourType) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    JSONObject info = new JSONObject();
                    info.put("stationId", currentStationId);
                    info.put("hourType", hourType);
                    param.put("paramInfo", info);
                    String json = param.toString();
                    Log.e("datastatis_ws", json);
                    String url = CONST.BASE_URL+"datastatis_ws";
                    Log.e("datastatis_ws", url);
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
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.e("datastatis_ws", result);
                                    if (!TextUtil.isEmpty(result)) {
                                        try {
                                            JSONObject obj = new JSONObject(result);
                                            if (!obj.isNull("b")) {
                                                JSONObject bobj = obj.getJSONObject("b");
                                                if (!bobj.isNull("fltj_hour")) {
                                                    JSONObject fltj_hour = bobj.getJSONObject("fltj_hour");
                                                    if (!TextUtil.isEmpty(fltj_hour.toString())) {
                                                        activity.dismissProgressDialog();
                                                        PackFltjHourDown fltjMaxHourDown = new PackFltjHourDown();
                                                        fltjMaxHourDown.fillData(fltj_hour.toString());
                                                        windAutoList.clear();
                                                        windAutoList.add(titleMaxRain);
                                                        windAutoList.addAll(fltjMaxHourDown.datalist);
                                                        autoatper.setData(windAutoList);
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
