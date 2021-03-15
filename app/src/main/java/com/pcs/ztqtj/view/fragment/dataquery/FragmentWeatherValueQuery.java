package com.pcs.ztqtj.view.fragment.dataquery;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.data_query.AdapterWeatherValue;
import com.pcs.ztqtj.control.adapter.data_query.SpaceItemDecoration;
import com.pcs.ztqtj.control.inter.ItemClickListener;
import com.pcs.ztqtj.view.activity.product.dataquery.ActivityDataQuery;
import com.pcs.lib.lib_pcs_v3.control.tool.Util;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.net.dataquery.AreaConfig;
import com.pcs.lib_ztqfj_v2.model.pack.net.dataquery.PackDataQueryAreaConfigDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.dataquery.PackDataQueryAreaConfigUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.dataquery.PackDataQueryHistoryTodayDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.dataquery.PackDataQueryHistoryTodayUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.dataquery.PackWeatherValueQueryDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.dataquery.PackWeatherValueQueryUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.dataquery.SubAreaConfig;
import com.pcs.lib_ztqfj_v2.model.pack.net.dataquery.WeatherValue;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tyaathome on 2017/11/1.
 */

public class FragmentWeatherValueQuery extends Fragment {

    private PackDataQueryAreaConfigUp areaUp = new PackDataQueryAreaConfigUp();
    private PackWeatherValueQueryUp valueUp = new PackWeatherValueQueryUp();
    private TextView tvCity, tvStation, tvTime;
    private RecyclerView rvDataYear, rvDataMonth;
    private ActivityDataQuery mActivity;
    private MyReceiver receiver = new MyReceiver();
    private List<AreaConfig> areaList = new ArrayList<>();
    private List<SubAreaConfig> subAreaList = new ArrayList<>();
    private int currentCityPosition = 0, currentStationPosition = 0, currentTimePosition = 0;
    private List<WeatherValue> dataListYear = new ArrayList<>();
    private List<WeatherValue> dataListMonth = new ArrayList<>();
    private List<String> timeList = new ArrayList<>();
    private View layoutToday;
    private AdapterWeatherValue adapterYear, adapterMonth;
    private TextView tvToday;
    private String mType = "", mCategory = "";
    private TextView tvTips;
    private TextView tvHeadYear, tvHeadMonth;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_weather_value, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initEvent();
        initData();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (ActivityDataQuery) activity;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            PcsDataBrocastReceiver.unregisterReceiver(getActivity(), receiver);
            receiver = null;
        }
    }

    private void initView() {
        tvCity = (TextView) getView().findViewById(R.id.tv_city);
        tvStation = (TextView) getView().findViewById(R.id.tv_station);
        tvTime = (TextView) getView().findViewById(R.id.tv_time);

        rvDataYear = (RecyclerView) getView().findViewById(R.id.list_data_year);
        rvDataYear.setNestedScrollingEnabled(false);
        rvDataYear.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvDataYear.addItemDecoration(new SpaceItemDecoration(Util.dip2px(getContext(), 1)));
        adapterYear = new AdapterWeatherValue(dataListYear);
        rvDataYear.setAdapter(adapterYear);

        rvDataMonth = (RecyclerView) getView().findViewById(R.id.list_data_month);
        rvDataMonth.setNestedScrollingEnabled(false);
        rvDataMonth.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvDataMonth.addItemDecoration(new SpaceItemDecoration(Util.dip2px(getContext(), 1)));
        adapterMonth = new AdapterWeatherValue(dataListMonth);
        rvDataMonth.setAdapter(adapterMonth);

        layoutToday = getView().findViewById(R.id.layout_today);
        tvToday = (TextView) getView().findViewById(R.id.tv_history_today);
        tvTips = (TextView) getView().findViewById(R.id.tv_tips);
        tvHeadYear = (TextView) getView().findViewById(R.id.tv_head_year);
        tvHeadMonth = (TextView) getView().findViewById(R.id.tv_head_month);
    }

    private void initEvent() {
        tvCity.setOnClickListener(onClickListener);
        tvStation.setOnClickListener(onClickListener);
        tvTime.setOnClickListener(onClickListener);
    }

    private void initData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            mCategory = bundle.getString("category", "");
            mType = bundle.getString("column", "");
        }
        PcsDataBrocastReceiver.registerReceiver(getActivity(), receiver);
        timeList = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            timeList.add(i + "月");
        }
        tvTime.setText("1月");
//        if (!TextUtils.isEmpty(mType) && mType.equals("1")) {
//            layoutToday.setVisibility(View.VISIBLE);
//        } else {
//            layoutToday.setVisibility(View.GONE);
//        }
        requestArea();
    }

    private String getTypeName() {
        switch (mType) {
            case "1":
                return "气温";
            case "2":
                return "降水";
            case "3":
                return "风况";
            case "4":
                return "日照";
            default:
                return "";
        }
    }

    private void updateArea(int cityPosition, int stationPosition, int timePosition) {
        currentCityPosition = cityPosition;
        currentStationPosition = stationPosition;
        currentTimePosition = timePosition;
        subAreaList.clear();
        if (areaList.size() > currentCityPosition) {
            tvCity.setText(areaList.get(currentCityPosition).name);
            subAreaList.addAll(areaList.get(currentCityPosition).sub_list);
            if (subAreaList.size() > currentStationPosition) {
                tvStation.setText(subAreaList.get(currentStationPosition).s_name);
            } else {
                tvStation.setText("");
            }
        }
        // 请求年值数据
        requestData(0);
        // 请求一月数据
        requestData(currentTimePosition);
    }

    /**
     * 请求地区数据
     */
    private void requestArea() {
        areaUp = new PackDataQueryAreaConfigUp();
        areaUp.type = mType;
        areaUp.d_type = mCategory;
        PcsDataDownload.addDownload(areaUp);
    }

    private void requestData(int timeValue) {
        if (subAreaList.size() > currentStationPosition) {
            mActivity.showProgressDialog();
            valueUp = new PackWeatherValueQueryUp();
            valueUp.type = mType;
            valueUp.area_id = subAreaList.get(currentStationPosition).s_area_id;
            valueUp.time_val = String.valueOf(timeValue);
            PcsDataDownload.addDownload(valueUp);
            if (mType.equals("1")) {
                requestHistory();
            }
            if (timeValue == 0) {
                if (subAreaList.size() > currentStationPosition) {
                    String headString = subAreaList.get(currentStationPosition).s_name + " " + getTypeName() + "年值统计";
                    tvHeadYear.setText(headString);
                }
            } else {
                if(subAreaList.size() > currentStationPosition && timeList.size() > timeValue-1) {
                    String headString = subAreaList.get(currentStationPosition).s_name + " " + timeValue + "月" + getTypeName() + "月值统计";
                    tvHeadMonth.setText(headString);
                    tvTime.setText(timeValue + "月");
                }
            }
        }
    }

    /**
     * 请求历史上的今天
     */
    private void requestHistory() {
        PackDataQueryHistoryTodayUp up = new PackDataQueryHistoryTodayUp();
        up.type = mType;
        if (subAreaList.size() > currentStationPosition) {
            up.id = subAreaList.get(currentStationPosition).s_id;
        }
        PcsDataDownload.addDownload(up);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_city:
                    if (areaList.size() == 0) {
                        return;
                    }
                    List<String> cityList = new ArrayList<>();
                    for (AreaConfig config : areaList) {
                        cityList.add(config.name);
                    }
                    mActivity.createPopupWindow((TextView) v, cityList, new ItemClickListener() {
                        @Override
                        public void itemClick(int position, Object... objects) {
                            updateArea(position, 0, 1);
                        }
                    }).showAsDropDown(v);
                    break;
                case R.id.tv_station:
                    if (subAreaList.size() == 0) {
                        return;
                    }
                    List<String> stationList = new ArrayList<>();
                    for (SubAreaConfig config : subAreaList) {
                        stationList.add(config.s_name);
                    }
                    mActivity.createPopupWindow((TextView) v, stationList, new ItemClickListener() {
                        @Override
                        public void itemClick(int position, Object... objects) {
                            updateArea(currentCityPosition, position, 1);
                        }
                    }).showAsDropDown(v);
                    break;
                case R.id.tv_time:
                    mActivity.createPopupWindow((TextView) v, timeList, new ItemClickListener() {
                        @Override
                        public void itemClick(int position, Object... objects) {
                            currentTimePosition = position+1;
                            requestData(currentTimePosition);
                        }
                    }).showAsDropDown(v);
                    break;
            }
        }
    };

    private class MyReceiver extends PcsDataBrocastReceiver {

        @Override
        public void onReceive(String nameStr, String errorStr) {
            if (areaUp.getName().equals(nameStr)) {
                PackDataQueryAreaConfigDown down = (PackDataQueryAreaConfigDown) PcsDataManager.getInstance()
                        .getNetPack(nameStr);
                if (down == null) {
                    return;
                }
                areaList.clear();
                areaList.addAll(down.p_list);
                updateArea(0, 0, 1);
                // 当前类型为气温(1)时，请求历史上的今天数据
                if (mType.equals("1")) {
                    requestHistory();
                }
                if (!TextUtils.isEmpty(down.des)) {
                    tvTips.setVisibility(View.VISIBLE);
                    tvTips.setText(down.des);
                } else {
                    tvTips.setVisibility(View.GONE);
                }
            } else if (nameStr.contains(PackWeatherValueQueryUp.NAME)) {
                mActivity.dismissProgressDialog();
                PackWeatherValueQueryDown down = (PackWeatherValueQueryDown) PcsDataManager.getInstance().getNetPack
                        (nameStr);
                if (down == null) {
                    return;
                }
                if (nameStr.equals(PackWeatherValueQueryUp.NAME + "#0")) {
                    dataListYear.clear();
                    dataListYear.addAll(down.info_list);
                    adapterYear.notifyDataSetChanged();
                } else {
                    dataListMonth.clear();
                    dataListMonth.addAll(down.info_list);
                    adapterMonth.notifyDataSetChanged();
                }
            } else if (PackDataQueryHistoryTodayUp.NAME.equals(nameStr)) {
                PackDataQueryHistoryTodayDown down = (PackDataQueryHistoryTodayDown) PcsDataManager.getInstance()
                        .getNetPack(nameStr);
                if (down == null || TextUtils.isEmpty(down.content)) {
                    layoutToday.setVisibility(View.GONE);
                    return;
                }
                layoutToday.setVisibility(View.VISIBLE);
                tvToday.setText(down.content);
            }
        }
    }
}
