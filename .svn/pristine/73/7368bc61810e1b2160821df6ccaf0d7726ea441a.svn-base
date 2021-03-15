package com.pcs.ztqtj.view.activity.newairquality;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.air_quality.AdapterAirChoiceCity;
import com.pcs.ztqtj.control.adapter.air_quality.AdapterAirRank;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
import com.pcs.ztqtj.view.activity.air_quality.ActivityAirQualityDetail;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCity;
import com.pcs.lib_ztqfj_v2.model.pack.net.airinfopack.AirRankNew;
import com.pcs.lib_ztqfj_v2.model.pack.net.airinfopack.PackAirRankNewDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.airinfopack.PackAirRankNewUp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.pcs.ztqtj.view.activity.air_quality.ActivityAirQualityProvince.keyPosition;

public class ActivityAirRankLevel extends FragmentActivityZtqBase {

    private Button pm_city;
    private Button pm_province;
    private Button pm_rank_name;
    private ListView lvRank;
    private AdapterAirRank adapterAirRank;
    private List<AirRankNew> dataList = new ArrayList<>();
    private List<AirRankNew> airListDataParent = new ArrayList<>();
    private List<AirRankNew> listCityPop = new ArrayList<>();
    private List<AirRankNew> listProvincePop = new ArrayList<>();
    private PackAirRankNewUp packDetialup ;
    private MyReceiver receiver = new MyReceiver();
    private CheckBox cbRank;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_air_rank_level);
        setTitleText(R.string.air_quality);
        initView();
        initEvent();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(receiver != null) {
            PcsDataBrocastReceiver.unregisterReceiver(this, receiver);
            receiver = null;
        }
    }

    private void initView() {
        pm_province = (Button) findViewById(R.id.pm_province);
        pm_city = (Button) findViewById(R.id.pm_city);
        pm_rank_name = (Button) findViewById(R.id.pm_rank_name);
        lvRank = (ListView) findViewById(R.id.lv_rank);
        cbRank = (CheckBox) findViewById(R.id.cb_rank);
    }

    private void initEvent() {
        pm_province.setOnClickListener(onClickListener);
        pm_city.setOnClickListener(onClickListener);
        pm_rank_name.setOnClickListener(onClickListener);
        cbRank.setOnCheckedChangeListener(onCheckedChangeListener);
        lvRank.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                gotoQuery(dataList.get(position).areaId, dataList.get(position).city);
            }
        });
    }

    private void initData() {
        PcsDataBrocastReceiver.registerReceiver(this, receiver);
        dataList = getIntent().getParcelableArrayListExtra("listdata");
        adapterAirRank = new AdapterAirRank(this, dataList);
        adapterAirRank.setCenter();
        lvRank.setAdapter(adapterAirRank);
        adapterAirRank.notifyDataSetChanged();
        reqD("aqi");
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
//                    Intent intent = new Intent(ActivityAirRankLevel.this, ActivityAirQualityProvince.class);
//                    ActivityAirQualityProvince.province = listProvincePop.get(position).province;
//                    ActivityAirQualityProvince.reqCode = packDetialup.rank_type;
//                    keyPosition = keyPosition;

                    Intent intent = new Intent(ActivityAirRankLevel.this, ActivityAirQualityProvinceRranking.class);
                    ActivityAirQualityProvinceRranking.province = listProvincePop.get(position).province;
                    ActivityAirQualityProvinceRranking.reqCode = packDetialup.rank_type;
                    ActivityAirQualityProvinceRranking.keyPosition = keyPosition;

                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
                    intentNextActivity(listCityPop.get(position).areaId, listCityPop.get(position).city);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void intentNextActivity(String AreaID, String area_name) {
        ActivityAirQualityDetail.setCity(AreaID, area_name);
        Intent intent = new Intent(this, ActivityAirQualityQuery.class);
        startActivity(intent);
    }

    private void gotoQuery(String AreaID, String area_name) {
        ActivityAirQualityQuery.setCity(AreaID, area_name);
        Intent intent = new Intent(this, ActivityAirQualityQuery.class);
        startActivity(intent);
    }

    /**
     * 初始化下拉列表
     */
    private void initPopList(PackAirRankNewDown pack) {
        //下拉列表
        listCityPop.clear();
        listCityPop.addAll(pack.rank_list);
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

    private void dealWidthData(PackAirRankNewDown pack) {
        airListDataParent.clear();
        Iterator it = pack.allProvince.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            airListDataParent.add((AirRankNew) entry.getValue());
        }
        initPopList(pack);
    }

    private void reqD(String reqcode) {
        showProgressDialog();
        packDetialup = new PackAirRankNewUp();
        packDetialup.rank_type = reqcode;
        PcsDataDownload.addDownload(packDetialup);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.pm_province:
                    showProvincePopup();
                    break;
                case R.id.pm_city:
                    showCityPopup();
                    break;
                case R.id.pm_rank_name:
                    break;
            }
        }
    };

    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {


        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            Collections.reverse(dataList);
            adapterAirRank.order();
        }
    };

    private class MyReceiver extends PcsDataBrocastReceiver {

        @Override
        public void onReceive(String nameStr, String errorStr) {
            if(nameStr.equals(packDetialup.getName())) {
                dismissProgressDialog();
                PackAirRankNewDown down = (PackAirRankNewDown) PcsDataManager.getInstance().getNetPack(nameStr);
                if(down == null) {
                    return;
                }
                dealWidthData(down);
            }
        }
    }
}
