package com.pcs.ztqtj.view.fragment.citylist;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCity;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityInfo;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityLocation;
import com.pcs.lib_ztqfj_v2.model.pack.net.citylist.AroundCityBean;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.AdapterHotCity;
import com.pcs.ztqtj.control.adapter.data_query.SpaceItemDecoration;
import com.pcs.ztqtj.control.inter.ClickImpl;
import com.pcs.ztqtj.control.tool.CommUtils;
import com.pcs.ztqtj.control.tool.ZtqLocationTool;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.view.activity.citylist.CityChoicableImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * 添加城市-天津及周边城市
 */
public class FragmentSHHotCity extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sh_hot_city_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    private void initView() {
        RecyclerView recyclerView = getView().findViewById(R.id.grid);
        List<AroundCityBean> cityList = new ArrayList<>();
        cityList.addAll(ZtqCityDB.getInstance().getAroundAreas());
        if (cityList.size() != 0) {
            List<Boolean> selectCityList = getSelectCityList(cityList);
            AdapterHotCity adapterHotCity = new AdapterHotCity(getActivity(), cityList, selectCityList, recyclerViewClickListener);
            recyclerView.setAdapter(adapterHotCity);
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
            int lr = CommUtils.Dip2Px(getContext(), 30);
            int tb = CommUtils.Dip2Px(getContext(), 20);
            recyclerView.addItemDecoration(new SpaceItemDecoration(lr, tb, 3));
        }
    }

    private List<Boolean> getSelectCityList(List<AroundCityBean> cityList) {
        List<PackLocalCity> listCityInfo = new ArrayList<>();
        Bundle arguments = getArguments();
        if (arguments == null) {
            // 定位城市
            PackLocalCityLocation packLocation = ZtqLocationTool.getInstance().getLocationCity();
            // 已选城市
            PackLocalCityInfo packChoiced = ZtqCityDB.getInstance().getCurrentCityInfo();
            if (ZtqLocationTool.getInstance().getIsAutoLocation()) {
                // 自动定位，添加定位城市
                listCityInfo.add(packLocation);
                // 去除重复城市
                ZtqCityDB.getInstance().delCityFromList(packChoiced.localCityList, packLocation);
                ZtqCityDB.getInstance().setCurrentCityInfo(packChoiced);
            }

            //列表去重
            for (int i = 0; i < packChoiced.localCityList.size(); i++) {
                boolean addToList = true;
                for (int j = 0; j < listCityInfo.size(); j++) {
                    if (listCityInfo.get(j).ID.equals(packChoiced.localCityList.get(i).ID)) {
                        addToList = false;
                        break;
                    }
                }
                if (addToList) {
                    listCityInfo.add(packChoiced.localCityList.get(i));
                }
            }
        } else {
            List<PackLocalCity> passCityList = (List<PackLocalCity>) arguments.getSerializable("citylist");
            if (passCityList != null) {
                listCityInfo.addAll(passCityList);
            }
        }
        List<Boolean> idList = new ArrayList<>();
        labelA:
        for (AroundCityBean aroundCity : cityList) {
            for (PackLocalCity selectCity : listCityInfo) {
                if (aroundCity.id.equals(selectCity.ID)) {
                    idList.add(true);
                    continue labelA;
                }
            }
            idList.add(false);
        }
        return idList;
    }

    private ClickImpl<AroundCityBean> recyclerViewClickListener = new ClickImpl<AroundCityBean>() {
        @Override
        public void onClick(AroundCityBean value) {
            // 城市选择后的事件处理
            CityChoicableImpl activity = (CityChoicableImpl) getActivity();
            if (activity != null && value != null) {
                PackLocalCity city = ZtqCityDB.getInstance().getAllCityByID(value.id);
                if (city != null) {
                    activity.choiceCity(city, city);
                }
            }
        }
    };

}
