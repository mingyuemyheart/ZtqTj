package com.pcs.ztqtj.view.activity.air_quality;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.air_quality.AdapterAirChoiceCity;
import com.pcs.ztqtj.control.adapter.air_quality.AdapterAirChoiceType;
import com.pcs.ztqtj.control.adapter.air_quality.AdapterAirQuality;
import com.pcs.ztqtj.control.inter.DrowListClick;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
import com.pcs.ztqtj.view.activity.newairquality.ActivityAirQualityQuery;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCity;
import com.pcs.lib_ztqfj_v2.model.pack.net.airinfopack.AirRankNew;
import com.pcs.lib_ztqfj_v2.model.pack.net.airinfopack.PackAirRankNewDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.airinfopack.PackAirRankNewUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.airinfopack.PackKeyDescDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.airinfopack.PackKeyDescUp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * JiangZy on 2016/8/5.
 */
public class ActivityAirQualityProvince extends FragmentActivityZtqBase implements View.OnClickListener {
    private ListView cityList;
    private View tv_ph_down;
    private View tv_ph_up;
    private Button pm_city;
    private Button pm_province;
    private Button pm_rank_name;
    private MyReceiver receiver = new MyReceiver();
    private AdapterAirQuality adapter;
    private TextView whatAQI;

    private List<PackKeyDescDown.DicListBean> dataeaum;

    private List<AirRankNew> airListData = new ArrayList<>();
    private List<AirRankNew> airListDataAll = new ArrayList<>();
    private List<AirRankNew> airListDataSourse = new ArrayList<>();
    private List<AirRankNew> airListDataParent = new ArrayList<>();

    private List<AirRankNew> listCityPop = new ArrayList<>();
    private List<AirRankNew> listProvincePop = new ArrayList<>();

    private PackAirRankNewUp packDetialup = new PackAirRankNewUp();
    private PackKeyDescDown packKey = new PackKeyDescDown();

    public static String province;
    public static String reqCode;
    public static int keyPosition = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_airquality_list);
        setTitleText("空气质量");
        whatAQI = ((TextView) findViewById(R.id.citiao));// 设置文字下划线
        initListView();
        initListTitle();
        initEvent();
        initDate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private void initDate() {
        PcsDataBrocastReceiver.registerReceiver(ActivityAirQualityProvince.this, receiver);
        whatAQI.setText(Html.fromHtml("<u>什么是空气质量指数(AQI)?</u>"));
        dataeaum = new ArrayList<>();
        airListData.clear();
        airListDataAll.clear();
//-------------------------------------------
        PackAirRankNewUp airPackup = new PackAirRankNewUp();
        airPackup.rank_type = reqCode;
        PackAirRankNewDown packAirRankDown = (PackAirRankNewDown) PcsDataManager.getInstance().getNetPack(airPackup.getName());
        airListDataSourse.clear();
        airListDataSourse.addAll(packAirRankDown.rank_list);
        adapter.isDown = true;
        airListDataParent.clear();
        for (int i = 0; i < packAirRankDown.rank_list.size(); i++) {
            if (packAirRankDown.rank_list.get(i).province.equals(province)) {
                airListData.add(packAirRankDown.rank_list.get(i));
            }
        }
        airListDataAll.addAll(packAirRankDown.rank_list);
        Iterator it = packAirRankDown.allProvince.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            airListDataParent.add((AirRankNew) entry.getValue());
        }
        if (reqCode.trim().equals("aqi") || reqCode.trim().toLowerCase().equals("aqi")) {
            adapter.isAQI = true;
        } else {
            adapter.isAQI = false;
        }
        adapter.setData(airListData);
        adapter.notifyDataSetChanged();
//        packKey = (PackKeyDescDown) PcsDataManager.getInstance().getNetPack(PackKeyDescUp.NAME);
//        dataeaum.clear();
//        for (int i = 0; i < packKey.dicList.size(); i++) {
//            dataeaum.add(packKey.dicList.get(i));
//        }
//
//        // 设置rankname
//        String showKey = dataeaum.get(keyPosition).rankType;
//        if (showKey.equals("O3")) {
//            showKey = "O3_1H";
//        } else if (showKey.equals("PM2_5")) {
//            showKey = "PM2.5";
//        }
//        pm_rank_name.setText(showKey);
//
//        changeValueKey(keyPosition);

        req();
        //初始化下拉列表
        initPopList();
    }

    private void req() {
        PackKeyDescUp up = new PackKeyDescUp();
        PcsDataDownload.addDownload(up);
    }

    /**
     * 处理aqi字段
     *
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
                reqD(dataeaum.get(0).rankType);
            }
        }
    }

    private void changeValueKey(int position) {
        try {
            keyPosition = position;
            if (packKey.dicList == null || packKey.dicList.size() == 0) {

            } else {
                whatMessage = packKey.dicList.get(position).rankType;
                whatAQI.setText(Html.fromHtml("<u>" + packKey.dicList.get(position).questions + "</u>"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void reqD(String reqcode) {
        if(!isOpenNet()){
            showToast(getString(R.string.net_err));
            return ;
        }
        showProgressDialog();
        packDetialup.rank_type = reqcode;
        // 如果是aqi则有背景
        adapter.isAQI = reqcode.trim().equals("aqi") || reqcode.trim().toLowerCase().equals("aqi");
        PcsDataDownload.addDownload(packDetialup);
    }

    private void initEvent() {
        whatAQI.setOnClickListener(this);
        tv_ph_down.setOnClickListener(this);
        tv_ph_up.setOnClickListener(this);
        pm_province.setOnClickListener(this);
        pm_city.setOnClickListener(this);
        pm_rank_name.setOnClickListener(this);
        cityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                intentNextActivity(airListData.get(arg2).areaId, airListData.get(arg2).city);
            }
        });
        adapter = new AdapterAirQuality(ActivityAirQualityProvince.this);
        cityList.setAdapter(adapter);
    }

    private void intentNextActivity(String AreaID, String area_name) {
        ActivityAirQualityDetail.setCity(AreaID, area_name);
        Intent intent = new Intent(ActivityAirQualityProvince.this, ActivityAirQualityQuery.class);
        startActivity(intent);
    }

    /* 初始化列表标题 */
    private void initListTitle() {
        pm_province = (Button) findViewById(R.id.pm_province);
        pm_city = (Button) findViewById(R.id.pm_city);
        pm_rank_name = (Button) findViewById(R.id.pm_rank_name);
        tv_ph_down = findViewById(R.id.tv_ph_down);
        tv_ph_up = findViewById(R.id.tv_ph_up);
    }

    /* 初始化城市列表 */
    private void initListView() {
        cityList = (ListView) findViewById(R.id.paihang);
    }

    private String whatMessage = "AQI";
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            adapter.isDown = true;
            tv_ph_down.setVisibility(View.VISIBLE);
            tv_ph_up.setVisibility(View.INVISIBLE);
            changeValueKey(msg.what);
            reqD(whatMessage);
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.citiao:
                try {
                    Intent intent = new Intent(ActivityAirQualityProvince.this, AcitvityAirWhatAQI.class);
                    intent.putExtra("w", packKey.dicList.get(keyPosition).des);
                    intent.putExtra("t","小词条");
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                    Intent intent = new Intent(ActivityAirQualityProvince.this, AcitvityAirWhatAQI.class);
                    intent.putExtra("w", getString(R.string.air_what_aqi));
                    intent.putExtra("t","小词条");
                    startActivity(intent);
                }

                break;
            case R.id.pm_province:
                showProvincePopup();
                break;
            case R.id.pm_city:
                showCityPopup();
                break;
            case R.id.pm_rank_name:
                createAQIPopup(pm_rank_name, dataeaum, 0, new DrowListClick() {
                    @Override
                    public void itemClick(int floag, int item) {
                        handler.sendEmptyMessage(item);
                    }
                }).showAsDropDown(pm_rank_name);
                break;
            case R.id.tv_ph_down:
            case R.id.tv_ph_up:
                showProgressDialog();
                adapter.isDown = !adapter.isDown;
                if (adapter.isDown) {
                    tv_ph_down.setVisibility(View.VISIBLE);
                    tv_ph_up.setVisibility(View.INVISIBLE);
                } else {
                    tv_ph_down.setVisibility(View.INVISIBLE);
                    tv_ph_up.setVisibility(View.VISIBLE);
                }
                chagesequ(adapter.isDown);
                dismissProgressDialog();
                break;
        }
    }

    private AdapterView.OnItemClickListener onItemClickProvince = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            try {
                province = airListDataParent.get(position).province;
                airListData.clear();
                for (int i = 0; i < airListDataSourse.size(); i++) {
                    if (airListDataSourse.get(i).province.equals(province)) {
                        airListData.add(airListDataSourse.get(i));
                    }
                }
                adapter.setData(airListData);
                adapter.notifyDataSetChanged();
                //省份拼音
                for (int i = 0; i < airListDataParent.size(); i++) {
                    AirRankNew bean = airListDataParent.get(i);
                    PackLocalCity packLocalCity = ZtqCityDB.getInstance().getProvinceByName(bean.province);
                    if (packLocalCity != null) {
                        bean.pinyin = packLocalCity.PINGYIN;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private AdapterView.OnItemClickListener onItemClickCity = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            try {
                intentNextActivity(airListData.get(position).areaId, airListData.get(position).city);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * true 是从小到大 false 从大到小
     */
    private void chagesequ(boolean floag) {
        if (floag) {
            for (int i = 0; i < airListData.size(); i++) {
                for (int j = i + 1; j < airListData.size(); j++) {
                    if (Float.compare(Float.parseFloat(airListData.get(i).num), Float.parseFloat(airListData.get(j).num)) > 0) {
                        AirRankNew bena = airListData.get(i);
                        airListData.set(i, airListData.get(j));
                        airListData.set(j, bena);
                    }
                }
            }
        } else {
            for (int i = 0; i < airListData.size(); i++) {
                for (int j = i + 1; j < airListData.size(); j++) {
                    if (Float.compare(Float.parseFloat(airListData.get(i).num), Float.parseFloat(airListData.get(j).num)) < 0) {
                        AirRankNew bena = airListData.get(i);
                        airListData.set(i, airListData.get(j));
                        airListData.set(j, bena);
                    }
                }
            }
        }
        adapter.setData(airListData);
        adapter.notifyDataSetChanged();
    }

    private class MyReceiver extends PcsDataBrocastReceiver {
        @Override
        public void onReceive(String name, String errorStr) {
            if (name.equals(packDetialup.getName())) {
                PackAirRankNewDown pack = (PackAirRankNewDown) PcsDataManager.getInstance().getNetPack(name);
                if (pack == null) {
                    return;
                }
                dealWidthData(pack);
                dismissProgressDialog();
            } else if (name.equals(PackKeyDescUp.NAME)) {
                packKey = (PackKeyDescDown) PcsDataManager.getInstance().getNetPack(name);
                if (packKey == null) {
                    return;
                }
                dealWidthKeyData(packKey);
                // 设置rankname
                String showKey = dataeaum.get(keyPosition).rankType;
                if (showKey.equals("O3")) {
                    showKey = "O3_1H";
                } else if (showKey.equals("PM2_5")) {
                    showKey = "PM2.5";
                }
                pm_rank_name.setText(showKey);

                changeValueKey(keyPosition);
                dismissProgressDialog();
            }
        }
    }

    private void dealWidthData(PackAirRankNewDown pack) {
        try {
            adapter.isDown = true;
            airListDataParent.clear();
            airListData.clear();
            for (int i = 0; i < pack.allProvince.size(); i++) {
                if (pack.allProvince.get(i).province.equals(province)) {
                    airListData.add(pack.rank_list.get(i));
                }
            }
            Iterator it = pack.allProvince.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                airListDataParent.add((AirRankNew) entry.getValue());
            }
            initPopList();
            adapter.setData(airListData);
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化下拉列表
     */
    private void initPopList() {
        //下拉列表
        listCityPop.clear();
        listCityPop.addAll(airListData);
        listProvincePop.clear();
        listProvincePop.addAll(airListDataParent);
        //省份拼音
        for (int i = 0; i < listProvincePop.size(); i++) {
            AirRankNew bean = listProvincePop.get(i);
            PackLocalCity packLocalCity = ZtqCityDB.getInstance().getProvinceByName(bean.province);
            if (packLocalCity != null) {
                bean.pinyin = packLocalCity.PINGYIN;
            }
        }
        //省份排序
        Collections.sort(listProvincePop, new Comparator<AirRankNew>() {
            public int compare(AirRankNew arg0, AirRankNew arg1) {
                if (arg0.province.equals("北京")) {
                    return -1;
                } else if (arg1.province.equals("北京")) {
                    return 1;
                }
                if (arg0.province.equals("天津")) {
                    return -2;
                } else if (arg1.province.equals("天津")) {
                    return 2;
                }
                return arg0.pinyin.compareTo(arg1.pinyin);
            }
        });
        //城市排序
        Collections.sort(listCityPop, new Comparator<AirRankNew>() {
            public int compare(AirRankNew arg0, AirRankNew arg1) {
//                if (arg0.city.equals("北京")) {
//                    return -1;
//                } else if (arg1.city.equals("北京")) {
//                    return 1;
//                }
//                if (arg0.city.equals("天津")) {
//                    return -2;
//                } else if (arg1.city.equals("天津")) {
//                    return 2;
//                }
                return arg0.pinyin.compareTo(arg1.pinyin);
            }
        });
    }

    /**
     * 显示城市下拉框
     */
    private void showCityPopup() {
        List<String> listString = new ArrayList<>();
        for (int i = 0; i < listCityPop.size(); i++) {
            listString.add(listCityPop.get(i).city);
        }
        AdapterAirChoiceCity adapter = new AdapterAirChoiceCity(this, listString);
        View view = LayoutInflater.from(this).inflate(R.layout.pop_list_layout, null);
        ListView listView = (ListView) view.findViewById(R.id.mylistviw);
        listView.setAdapter(adapter);

        final PopupWindow pop = new PopupWindow(this);
        pop.setContentView(view);
        pop.setOutsideTouchable(false);
        pop.setWidth(getResources().getDimensionPixelOffset(R.dimen.dimen140));
        if (listCityPop.size() < 12) {
            pop.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        } else {
            pop.setHeight(getResources().getDimensionPixelOffset(R.dimen.dimen300));
        }
        pop.setFocusable(true);

        pop.showAsDropDown(pm_city);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pop.dismiss();
                try {
                    intentNextActivity(airListData.get(position).areaId, listCityPop.get(position).city);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 显示省份下拉框
     */
    private void showProvincePopup() {
        List<String> listString = new ArrayList<>();
        for (int i = 0; i < listProvincePop.size(); i++) {
            listString.add(listProvincePop.get(i).province);
        }
        AdapterAirChoiceCity adapter = new AdapterAirChoiceCity(this, listString);
        View view = LayoutInflater.from(this).inflate(R.layout.pop_list_layout, null);
        ListView listView = (ListView) view.findViewById(R.id.mylistviw);
        listView.setAdapter(adapter);

        final PopupWindow pop = new PopupWindow(this);
        pop.setContentView(view);
        pop.setOutsideTouchable(false);
        pop.setWidth(getResources().getDimensionPixelOffset(R.dimen.dimen140));
        if (listProvincePop.size() < 12) {
            pop.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        } else {
            pop.setHeight(getResources().getDimensionPixelOffset(R.dimen.dimen300));
        }
        pop.setFocusable(true);

        pop.showAsDropDown(pm_province);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    pop.dismiss();
//                    Intent intent = new Intent(ActivityAirQualityProvince.this, ActivityAirQualityProvince.class);
//                    ActivityAirQualityProvince.province = listProvincePop.get(position).province;
//                    ActivityAirQualityProvince.reqCode = packDetialup.rankType;
//                    ActivityAirQualityProvince.keyPosition = keyPosition;
//
//                    startActivity(intent);
                    ActivityAirQualityProvince.province = listProvincePop.get(position).province;
                    reqD(packDetialup.rank_type);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 创建AQI下拉框
     */
    private PopupWindow createAQIPopup(final Button dropDownView, final List<PackKeyDescDown.DicListBean> dataeaum, final int floag, final DrowListClick listener) {
        AdapterAirChoiceType dataAdapter = new AdapterAirChoiceType(this, dataeaum);
        View popcontent = LayoutInflater.from(this).inflate(R.layout.pop_list_layout, null);
        ListView lv = (ListView) popcontent.findViewById(R.id.mylistviw);
        lv.setAdapter(dataAdapter);
        final PopupWindow pop = new PopupWindow(this);
        pop.setContentView(popcontent);
        pop.setOutsideTouchable(false);
        pop.setWidth(getResources().getDimensionPixelOffset(R.dimen.dimen140));
        if (dataeaum.size() < 12) {
            pop.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        } else {
            pop.setHeight(getResources().getDimensionPixelOffset(R.dimen.dimen300));
        }
        pop.setFocusable(true);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pop.dismiss();
                String showKey = dataeaum.get(position).rankType;
                if (showKey.equals("O3")) {
                    showKey = "O3_1H";
                } else if (showKey.equals("PM2_5")) {
                    showKey = "PM2.5";
                }
                dropDownView.setText(showKey);
                listener.itemClick(floag, position);
            }
        });
        return pop;
    }

}
