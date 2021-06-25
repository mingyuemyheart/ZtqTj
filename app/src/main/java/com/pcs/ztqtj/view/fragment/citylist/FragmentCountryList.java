package com.pcs.ztqtj.view.fragment.citylist;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.AdapterSelectAreaExpandList;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.view.activity.citylist.CityChoicableImpl;
import com.pcs.ztqtj.view.myview.mExpandableListView;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 添加城市-国内城市列表
 */
public class FragmentCountryList extends Fragment {

    private mExpandableListView exList;
    private List<PackLocalCity> province = new ArrayList<>();
    private HashMap<String, List<PackLocalCity>> datasMap = new HashMap<>();
    private AdapterSelectAreaExpandList adapter;
    private boolean isSingleCityList = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_country_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        initData();
    }

    private void initViews() {
        exList = (mExpandableListView) getView().findViewById(R.id.cityListView);
        exList.setHeaderView(getActivity().getLayoutInflater().inflate(R.layout.layout_citygroupitem, exList, false));
        exList.setOnChildClickListener(new ExpendListener());
        exList.setOnGroupClickListener(new ExpendChildListener());
    }

    private void initData() {
        if(getArguments() != null && getArguments().containsKey("isSingleCityList")) {
            isSingleCityList = getArguments().getBoolean("isSingleCityList", false);
        }
        province.addAll(ZtqCityDB.getInstance().getProvincesList());

        if(isSingleCityList) {
            for (PackLocalCity city : province) {
                List<PackLocalCity> infoList = ZtqCityDB.getInstance().getCountryCityList(city.ID);
                if(city.ID.equals("10103")) {
                    List<PackLocalCity> shList = ZtqCityDB.getInstance().getCityLv1();
                    datasMap.put("10103", shList);
                } else {
                    datasMap.put(city.ID, infoList);
                }
            }
        } else {
            for (PackLocalCity city : province) {
                List<PackLocalCity> infoList = ZtqCityDB.getInstance().getCityByProcinceID(city.ID);
                datasMap.put(city.ID, infoList);
            }
        }

        adapter = new AdapterSelectAreaExpandList(getActivity(), exList, province, datasMap);
        exList.setAdapter(adapter);
        exList.setSelectedGroup(0);
    }

    /**
     * @author Z 扩展listview子选项点击事件
     */
    private class ExpendListener implements ExpandableListView.OnChildClickListener {
        @Override
        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
            PackLocalCity cityinfo = datasMap.get(province.get(groupPosition).ID).get(childPosition);
            clickCityItem(cityinfo);
            return false;
        }
    }

    private class ExpendChildListener implements ExpandableListView.OnGroupClickListener {
        @Override
        public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
            if (adapter.getGroupClickStatus(groupPosition) == 0) {
                adapter.setGroupClickStatus(groupPosition, 1);
                parent.expandGroup(groupPosition);
                parent.setSelectedGroup(groupPosition);
            } else if (adapter.getGroupClickStatus(groupPosition) == 1) {
                adapter.setGroupClickStatus(groupPosition, 0);
                parent.collapseGroup(groupPosition);
            }
            if (groupPosition == 0 && adapter.isHasloaction()) {
                // onChoiceCity(locationCity, locationProvince);
            }
            return true;
        }
    }

    protected void clickCityItem(PackLocalCity cityinfo) {
        // 城市选择后的事件处理
        CityChoicableImpl activity = (CityChoicableImpl) getActivity();
        if(activity != null) {
            activity.choiceCity(cityinfo, cityinfo);
        }
    }
}
