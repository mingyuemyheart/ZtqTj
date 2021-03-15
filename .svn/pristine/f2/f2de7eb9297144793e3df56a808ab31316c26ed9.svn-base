package com.pcs.ztqtj.view.fragment.livequery.fujian_city;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.livequery.AdapterWind;
import com.pcs.ztqtj.control.inter.DrowListClick;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.view.activity.livequery.ActivityLiveQuery;
import com.pcs.ztqtj.view.activity.livequery.ActivityLiveQueryDetail;
import com.pcs.ztqtj.view.fragment.livequery.FragmentLiveQueryCommon;
import com.pcs.ztqtj.view.myview.MyListView;
import com.pcs.lib.lib_pcs_v3.control.tool.Util;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCity;
import com.pcs.lib_ztqfj_v2.model.pack.net.column.ColumnInfo;
import com.pcs.lib_ztqfj_v2.model.pack.net.column.PackColumnDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.column.PackColumnUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackFltjHourDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackFltjHourUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackFltjZdDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackFltjZdDown.FltjZd;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackFltjZdUp;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


/**
 * @author Z 风况
 */
public class FragmentWind extends FragmentLiveQueryCommon implements OnClickListener {
    private MyListView livequery_auto_rainfall;
    private ActivityLiveQuery activity;
    private TextView livequery_city_spinner;
    private TextView livequery_town_spinner;
    private TextView description_title_search2;
    private RadioGroup windradiogroup;

    /**
     * 原始数据-本市自动站统计表
     */
    private List<FltjZd> windAutoList_source;
    /**
     * 列表使用数据-本市自动站统计表
     */
    private List<FltjZd> windAutoList;

    private AdapterWind autoatper;

    // 网络请求数据包
    private PackFltjZdUp fltjZdUp;// 风况查询--自动站风况统计表（fltj_zd）


    // 列表头标题
    private FltjZd titleMaxRain;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (ActivityLiveQuery) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        PcsDataBrocastReceiver.registerReceiver(this.getActivity(), receiver);
        View view = inflater.inflate(R.layout.fragement_livequery_wind, null);
        return view;
    }

    public void refleshData() {
        //req();
        reqAll();
    }

    private MyReceiver receiver = new MyReceiver();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(receiver);
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
        arrcolumnInfo = new ArrayList<ColumnInfo>();
        PackColumnUp packColumnUp = new PackColumnUp();
        //城市
        PackLocalCity packCity = ZtqCityDB.getInstance().getCityMain();
//        if(packCity.isFjCity){
            packColumnUp.column_type = "8";
//        }else{
//            packColumnUp.column_type = "9";
//        }
        PackColumnDown columnList = (PackColumnDown) PcsDataManager.getInstance().getNetPack(packColumnUp.getName());
        if (columnList == null || columnList.arrcolumnInfo.size() == 0) {
            return;
        }
        arrcolumnInfo.addAll(columnList.arrcolumnInfo);
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
//        new MAsyncTask() {
//            @Override
//            public void doInBackGround() {
//                cityControl = new CityListControl(activity.cityinfo);
//            }
//
//            @Override
//            public void onPreExercute() {
//                windAutoList_source = new ArrayList<FltjZd>();
//                windAutoList = new ArrayList<FltjZd>();
//                // 列表头部说明
//                titleMaxRain = new PackFltjZdDown().new FltjZd();
//                titleMaxRain.county = "站点";
//                titleMaxRain.time = "日期/时段";
//                titleMaxRain.winddirection = "风向";
//                titleMaxRain.windFengLi = "风力";
//                titleMaxRain.windpower = "风速m/s";
//                windAutoList.add(titleMaxRain);
//            }
//
//            @Override
//            public void onPostExecute() {
//                livequery_city_spinner.setText(cityControl.getCutParentCity().NAME);
//                livequery_town_spinner.setText(cityControl.getCutChildCity().NAME);
//                description_title_search2.setText(cityControl.getCutChildCity().NAME + " 自动站瞬时风况统计表");
//                autoatper = new AdapterWind(getActivity(), windAutoList);
//                livequery_auto_rainfall.setAdapter(autoatper);
//                req();
//            }
//        }.execute();


        windAutoList_source = new ArrayList<FltjZd>();
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
        livequery_town_spinner.setText(cityControl.getCutChildCity().NAME);
        if (livequery_city_spinner.getText().toString().equals("天津")){
            description_title_search2.setText(cityControl.getCutChildCity().NAME + " 自动站瞬时风况统计表");
        }else{
            description_title_search2.setText(cityControl.getCutChildCity().NAME + " 瞬时风况统计表");
        }
        autoatper = new AdapterWind(getActivity(), windAutoList);
        livequery_auto_rainfall.setAdapter(autoatper);
        req();
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
                        req();
                        break;
                    case R.id.windradiogroupright:
                        boolean b = windradiogroup.getCheckedRadioButtonId() == R.id.windradiogroupright;
                        gd_wind.setVisibility(View.VISIBLE);
                        //int firstRadioButtonid = 101;
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
                if(!city.isFjCity || ZtqCityDB.getInstance().isServiceAccessible()) {
                    activity.createPopupWindow(livequery_town_spinner, cityControl.getChildShowNameList(), 1, listener).showAsDropDown(livequery_town_spinner);
                }
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

            //req();
            reqAll();
        }
    };


    public void req() {
        if (!activity.isOpenNet()) {
            activity.showToast(getString(R.string.net_err));
            return;
        }
        activity.showProgressDialog();
        // 风况查询--自动站风况统计表（fltj_zd）
        fltjZdUp = new PackFltjZdUp();
        getOutoLine();
        PcsDataDownload.addDownload(fltjZdUp);
    }

    private void reqAll() {
        switch (windradiogroup.getCheckedRadioButtonId()) {
            case R.id.windradiogroupleft:
                req();
                break;
            case R.id.windradiogroupright:
                reqMaxWind(currentMaxWindIndex);
                break;
        }
    }

    /*isCity 获取的是否是九地市的，true为是*/
    private void getOutoLine() {
        if (cityControl.getParentData()) {
            fltjZdUp.city = cityControl.getCutChildCity().NAME;
            fltjZdUp.county="";
        } else {
            fltjZdUp.county = cityControl.getCutChildCity().NAME;
            fltjZdUp.city="";
        }
        fltjZdUp.province = cityControl.getCutParentCity().ID;
        fltjZdUp.is_jc = ZtqCityDB.getInstance().isServiceAccessible();
    }

    private PackFltjHourUp maxWind;

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

//            description_title_search3.setText(livequery_city_spinner.getText().toString().trim() + "地区 自动站" + arrcolumnInfo.get(item).name + "极大风速排名");

//极大风处理
        maxWind = new PackFltjHourUp();
        maxWind.province = cityControl.getCutParentCity().ID;
        maxWind.county = cityControl.getCutChildCity().NAME;
        maxWind.falg = arrcolumnInfo.get(item).type;
        maxWind.is_jc = ZtqCityDB.getInstance().isServiceAccessible();
        PcsDataDownload.addDownload(maxWind);

//        // 风况查询—本市自动站统计表（fltj_city）前6 24小时  极大风
//        fltjCity24Up = new PackFltjCityUp();
//        fltjCity24Up.country = reqParentName;
//        fltjCity24Up.type = "2";
//        fltjCity24Up.falg = arrcolumnInfo.get(item).type;
//        PcsDataDownload.addDownload(fltjCity24Up);
    }

    private class MyReceiver extends PcsDataBrocastReceiver {
        @Override
        public void onReceive(String name, String errorStr) {
            if (fltjZdUp != null && name.equals(fltjZdUp.getName())) {
                activity.dismissProgressDialog();
                PackFltjZdDown fltjZdDown = (PackFltjZdDown) PcsDataManager.getInstance().getNetPack(name);
                windAutoList.clear();
                windAutoList_source.clear();
                windAutoList.add(titleMaxRain);
                if (fltjZdDown != null) {
                    windAutoList.addAll(fltjZdDown.datalist);
                    windAutoList_source.addAll(fltjZdDown.datalist);
                }
                //autoatper.notifyDataSetChanged();
                autoatper.setData(windAutoList);
            } else if (maxWind != null && maxWind.getName().equals(name)) {
//                极大风速
                activity.dismissProgressDialog();
                PackFltjHourDown fltjMaxHourDown = (PackFltjHourDown) PcsDataManager.getInstance().getNetPack(name);
                windAutoList.clear();
                windAutoList.add(titleMaxRain);
                if (fltjMaxHourDown == null) {
                } else {
                    windAutoList.addAll(fltjMaxHourDown.datalist);
                }
                //autoatper.notifyDataSetChanged();
                autoatper.setData(windAutoList);
            }
        }
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
}
