package com.pcs.ztqtj.view.fragment.dataquery;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.data_query.AdapterDataQueryMonth;
import com.pcs.ztqtj.control.inter.ItemClickListener;
import com.pcs.ztqtj.view.activity.product.dataquery.ActivityDataQuery;
import com.pcs.ztqtj.view.myview.ElementWeatherView;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.net.dataquery.AreaConfig;
import com.pcs.lib_ztqfj_v2.model.pack.net.dataquery.ElementQueryMonth;
import com.pcs.lib_ztqfj_v2.model.pack.net.dataquery.ElementQueryYear;
import com.pcs.lib_ztqfj_v2.model.pack.net.dataquery.ItemConfig;
import com.pcs.lib_ztqfj_v2.model.pack.net.dataquery.PackDataQueryAreaConfigDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.dataquery.PackDataQueryAreaConfigUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.dataquery.PackDataQueryItemConfigDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.dataquery.PackDataQueryItemConfigUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.dataquery.PackWeatherElementMonthDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.dataquery.PackWeatherElementMonthUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.dataquery.PackWeatherElementYearDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.dataquery.PackWeatherElementYearUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.dataquery.SubAreaConfig;
import com.pcs.lib_ztqfj_v2.model.pack.net.dataquery.SubItemConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * Created by tyaathome on 2017/10/24.
 */

public class FragmentWeatherElementsDayQuery extends Fragment {

    private ActivityDataQuery mActivity;
    private RecyclerView listMonth;
    private List<ElementQueryYear> yearDataList = new ArrayList<>();
    private List<ElementQueryMonth> monthDataList = new ArrayList<>();
    private AdapterDataQueryMonth monthAdapter;
    private PackDataQueryAreaConfigUp areaUp = new PackDataQueryAreaConfigUp();
    private PackDataQueryItemConfigUp itemUp = new PackDataQueryItemConfigUp();
    private MyRecevier recevier = new MyRecevier();
    private List<AreaConfig> areaList = new ArrayList<>();
    private List<SubAreaConfig> subAreaList = new ArrayList<>();
    private List<ItemConfig> itemList = new ArrayList<>();
    private List<SubItemConfig> subItemList = new ArrayList<>();
    private TextView tvCity, tvStation;
    private int currentCityPosition = 0, currentStationPosition = 0;
    private TextView tvItemSingle, tvItemMulti, tvSubItem;
    private int currentItemPosition = 0, currentSubItemPosition = 0;
    private int currentMonthPosition = 0;
    private Button btnQuery;
    private String mType = "", mCategory = "";
    private ElementWeatherView elementWeatherView;
    private List<String> startYearList = new ArrayList<>();
    private List<String> startMonthList = new ArrayList<>();
    private List<String> endYearList = new ArrayList<>();
    private List<String> endMonthList = new ArrayList<>();
    private TextView tvStartYear, tvStartMonth, tvEndYear, tvEndMonth;
    private TextView tvDayCount;
    private List<String> sortList = new ArrayList<>();
    private TextView tvSort;
    private String sortFlag = "1";
    private View layoutMonth;
    private View layoutDayCount;
    private EditText etDayCount;
    private View layoutMask;
    private View viewRight, viewRight2;
    private Handler mHandler = new Handler();
    private TextView tvTips;
    private TextView tvDataInfo;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState) {
        return inflater.inflate(R.layout.fragment_weather_elements_day_query, container, false);
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
        if (recevier != null) {
            PcsDataBrocastReceiver.unregisterReceiver(getActivity(), recevier);
        }
    }

    private void initView() {
        listMonth = (RecyclerView) getView().findViewById(R.id.list_month);
        tvCity = (TextView) getView().findViewById(R.id.tv_city);
        tvStation = (TextView) getView().findViewById(R.id.tv_station);
        tvItemSingle = (TextView) getView().findViewById(R.id.tv_item_single);
        tvItemMulti = (TextView) getView().findViewById(R.id.tv_item_multi);
        tvSubItem = (TextView) getView().findViewById(R.id.tv_sub_item);
        btnQuery = (Button) getView().findViewById(R.id.btn_query);
        elementWeatherView = (ElementWeatherView) getView().findViewById(R.id.elementview);
        tvStartYear = (TextView) getView().findViewById(R.id.tv_start_year);
        tvStartMonth = (TextView) getView().findViewById(R.id.tv_start_month);
        tvEndYear = (TextView) getView().findViewById(R.id.tv_end_year);
        tvEndMonth = (TextView) getView().findViewById(R.id.tv_end_month);
        tvDayCount = (TextView) getView().findViewById(R.id.tv_day_count);
        tvSort = (TextView) getView().findViewById(R.id.tv_time_drop_down);
        layoutMonth = getView().findViewById(R.id.layout_month);
        layoutDayCount = getView().findViewById(R.id.layout_day_num);
        etDayCount = (EditText) getView().findViewById(R.id.et_day_num);
        layoutMask = getView().findViewById(R.id.layout_mask);
        viewRight = getView().findViewById(R.id.iv_right);
        viewRight2 = getView().findViewById(R.id.iv_right2);
        tvTips = (TextView) getView().findViewById(R.id.tv_tips);
        tvDataInfo = (TextView) getView().findViewById(R.id.tv_data_info);
    }

    private void initEvent() {
        tvCity.setOnClickListener(onClickListener);
        tvStation.setOnClickListener(onClickListener);
        tvItemSingle.setOnClickListener(onClickListener);
        tvItemMulti.setOnClickListener(onClickListener);
        tvSubItem.setOnClickListener(onClickListener);
        btnQuery.setOnClickListener(onClickListener);
        tvStartYear.setOnClickListener(onClickListener);
        tvStartMonth.setOnClickListener(onClickListener);
        tvEndYear.setOnClickListener(onClickListener);
        tvEndMonth.setOnClickListener(onClickListener);
        tvSort.setOnClickListener(onClickListener);
        elementWeatherView.setOnItemClickListener(barItemClickListener);
        elementWeatherView.setOnScrollListener(barScrollListener);
        layoutMask.setOnTouchListener(maskTouchListener);
        listMonth.addOnScrollListener(recyclerviewScrollListener);
    }

    private void initData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            mCategory = bundle.getString("category", "");
            mType = bundle.getString("column", "");
        }
        if(mType.equals("5")) {
            layoutDayCount.setVisibility(View.VISIBLE);
            tvSubItem.setVisibility(View.GONE);
        } else {
            layoutDayCount.setVisibility(View.GONE);
            tvSubItem.setVisibility(View.VISIBLE);
        }
        recevier = new MyRecevier();
        PcsDataBrocastReceiver.registerReceiver(getActivity(), recevier);
        monthAdapter = new AdapterDataQueryMonth(monthDataList);
        listMonth.setAdapter(monthAdapter);
        listMonth.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        initTimeList();
        initSortList();
        requestArea();
        requestItem();
    }

    private void initTimeList() {
        startYearList.clear();
        startMonthList.clear();
        endYearList.clear();
        endMonthList.clear();
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        if(year < 2018) {
            year = 2018;
        }
        for(int i = year; i >= 1961; i--) {
            startYearList.add(String.valueOf(i));
            endYearList.add(String.valueOf(i));
        }
        for(int i = 1; i <= 12; i++) {
            startMonthList.add(String.valueOf(i));
        }
        Calendar currentTime = Calendar.getInstance();
        int selectYear = Integer.parseInt(tvEndYear.getText().toString());
        int endMonth = 12;
        if(selectYear == currentTime.get(Calendar.YEAR)) {
            endMonth = currentTime.get(Calendar.MONTH) + 1;
        }
        for(int i = 1; i <= endMonth; i++) {
            endMonthList.add(String.valueOf(i));
        }
    }

    /**
     * 初始化排序下拉列表
     */
    private void initSortList() {
        sortList = Arrays.asList(getResources().getStringArray(R.array.DataQueryElementSort));
        if(sortList.size() > 0) {
            tvSort.setText(sortList.get(0));
        }
    }

    private void updateMonth(List<ElementQueryMonth> dataList) {
        if(dataList == null || dataList.size() == 0) {
            layoutMonth.setVisibility(View.GONE);
        } else {
            layoutMonth.setVisibility(View.VISIBLE);
        }
        if(yearDataList.size() > currentMonthPosition
                && itemList.size() > currentItemPosition
                && subItemList.size() > currentSubItemPosition) {
            String year = yearDataList.get(currentMonthPosition).year;
            String item = itemList.get(currentItemPosition).name;
            String subItem = subItemList.get(currentSubItemPosition).s_name;
            tvDataInfo.setText(year + "年" + item + subItem + "的各月天数统计:");
        }
        monthDataList.clear();
        monthDataList.addAll(dataList);
        monthAdapter.notifyDataSetChanged();
        mHandler.postDelayed(updateRight, 500);
        //setListRightVisibility();
    }

    private Runnable updateRight = new Runnable() {
        @Override
        public void run() {
            setListRightVisibility();
        }
    };

    private void updateArea(int cityPosition, int stationPosition) {
        currentCityPosition = cityPosition;
        currentStationPosition = stationPosition;
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
    }

    private void updateItem(int position, int subPosition) {
        currentItemPosition = position;
        currentSubItemPosition = subPosition;
        subItemList.clear();
        if (itemList.size() > currentItemPosition) {
            if (itemList.size() == 1) {
                tvItemSingle.setVisibility(View.VISIBLE);
                tvItemMulti.setVisibility(View.GONE);
                tvItemSingle.setText(itemList.get(currentItemPosition).name);
            } else {
                tvItemSingle.setVisibility(View.GONE);
                tvItemMulti.setVisibility(View.VISIBLE);
                tvItemMulti.setText(itemList.get(currentItemPosition).name);
            }
            subItemList.addAll(itemList.get(currentItemPosition).sub_list);
            if (subItemList.size() > currentSubItemPosition) {
                if(mType.equals("5")) {
                    etDayCount.setText(subItemList.get(currentSubItemPosition).s_name);
                } else {
                    tvSubItem.setText(subItemList.get(currentSubItemPosition).s_name);
                }
            } else {
                tvSubItem.setText("");
            }
        }
    }

    private void requestArea() {
        mActivity.showProgressDialog();
        areaUp = new PackDataQueryAreaConfigUp();
        areaUp.type = mType;
        areaUp.d_type = mCategory;
        PcsDataDownload.addDownload(areaUp);
    }

    private void requestItem() {
        mActivity.showProgressDialog();
        itemUp = new PackDataQueryItemConfigUp();
        itemUp.type = mType;
        PcsDataDownload.addDownload(itemUp);
    }

    /**
     * 气象要素日查询
     */
    private void requestElement() {
        if (subAreaList.size() > currentStationPosition
                && itemList.size() > currentItemPosition) {
            mActivity.showProgressDialog();
            currentMonthPosition = 0;
            PackWeatherElementYearUp up = new PackWeatherElementYearUp();
            up.type = mType;
            up.id = subAreaList.get(currentStationPosition).s_id;
            up.s_time = getStartTime();
            up.e_time = getEndTime();
            up.item_id = itemList.get(currentItemPosition).item_id;
            if(mType.equals("5")) {
                if(TextUtils.isEmpty(etDayCount.getText().toString())) {
                    mActivity.showToast("请填写天数");
                    return;
                } else {
                    up.s_item_id = etDayCount.getText().toString();
                }
            } else {
                if(subItemList.size() > currentSubItemPosition){
                    up.s_item_id = subItemList.get(currentSubItemPosition).s_item_id;
                }
            }
            up.falg = sortFlag;
            PcsDataDownload.addDownload(up);
        }
    }

    private String getStartTime() {
        int month = Integer.parseInt(tvStartMonth.getText().toString());
        String strMonth = String.valueOf(month);
        if(month > 0 && month < 10) {
            strMonth = "0" + month;
        }
        return tvStartYear.getText().toString() + strMonth;
    }

    private String getEndTime() {
        int month = Integer.parseInt(tvEndMonth.getText().toString());
        String strMonth = String.valueOf(month);
        if(month > 0 && month < 10) {
            strMonth = "0" + month;
        }
        return tvEndYear.getText().toString() + strMonth;
    }

    private void setListRightVisibility() {
        if(listMonth.canScrollHorizontally(1)) {
            viewRight2.setVisibility(View.VISIBLE);
        } else {
            viewRight2.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 判断选择时间是否正确
     * @return
     */
    private boolean checkTime() {
        int startYear = Integer.parseInt(tvStartYear.getText().toString());
        int startMonth = Integer.parseInt(tvStartMonth.getText().toString());
        int endYear = Integer.parseInt(tvEndYear.getText().toString());
        int endMonth = Integer.parseInt(tvEndMonth.getText().toString());
        if(startYear < endYear) {
            return true;
        } else if(startYear == endYear) {
            if(startMonth <= endMonth) {
                return true;
            }
        }
        return false;
    }

    /**
     * 查询月份
     * @param position
     */
    private void requestMonth(int position) {
        if(subAreaList.size() > currentStationPosition
                && yearDataList.size() > position
                && itemList.size() > currentItemPosition
                && subItemList.size() > currentSubItemPosition) {
            mActivity.showProgressDialog();
            PackWeatherElementMonthUp up = new PackWeatherElementMonthUp();
            up.type = mType;
            up.id = subAreaList.get(currentStationPosition).s_id;
            up.year = yearDataList.get(position).year;
            up.item_id = itemList.get(currentItemPosition).item_id;
            up.s_item_id = subItemList.get(currentSubItemPosition).s_item_id;
            PcsDataDownload.addDownload(up);
        }
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
                            updateArea(position, 0);
                            requestElement();
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
                            updateArea(currentCityPosition, position);
                            requestElement();
                        }
                    }).showAsDropDown(v);
                    break;
                case R.id.tv_item_single:
                    break;
                case R.id.tv_item_multi:
                    if (itemList.size() == 0) {
                        return;
                    }
                    List<String> iList = new ArrayList<>();
                    for (ItemConfig config : itemList) {
                        iList.add(config.name);
                    }
                    mActivity.createPopupWindow((TextView) v, iList, new ItemClickListener() {
                        @Override
                        public void itemClick(int position, Object... objects) {
                            updateItem(position, 0);
                        }
                    }).showAsDropDown(v);
                    break;
                case R.id.tv_sub_item:
                    if (subItemList.size() == 0) {
                        return;
                    }
                    List<String> subList = new ArrayList<>();
                    for (SubItemConfig config : subItemList) {
                        subList.add(config.s_name);
                    }
                    mActivity.createPopupWindow((TextView) v, subList, new ItemClickListener() {
                        @Override
                        public void itemClick(int position, Object... objects) {
                            updateItem(currentItemPosition, position);
                        }
                    }).showAsDropDown(v);
                    break;
                case R.id.btn_query:
                    if(mActivity.isBought) {
                        if(checkTime()) {
                            requestElement();
                        } else {
                            Toast.makeText(getActivity(), "截止时间应大于开始时间", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        mActivity.checkLogin();
                    }
                    break;
                case R.id.tv_start_year:
                    initTimeList();
                    mActivity.createPopupWindow((TextView) v, startYearList, startYearListener).showAsDropDown(v);
                    break;
                case R.id.tv_start_month:
                    initTimeList();
                    mActivity.createPopupWindow((TextView) v, startMonthList, null).showAsDropDown(v);
                    break;
                case R.id.tv_end_year:
                    initTimeList();
                    mActivity.createPopupWindow((TextView) v, endYearList, endYearListener).showAsDropDown(v);
                    break;
                case R.id.tv_end_month:
                    initTimeList();
                    mActivity.createPopupWindow((TextView) v, endMonthList, null).showAsDropDown(v);
                    break;
                case R.id.tv_time_drop_down:
                    mActivity.createPopupWindow((TextView) v, sortList, new ItemClickListener() {
                        @Override
                        public void itemClick(int position, Object... objects) {
                            sortFlag = String.valueOf(position+1);
                            requestElement();
                        }
                    }).showAsDropDown(v);
                    break;
            }
        }
    };

    private ItemClickListener startYearListener = new ItemClickListener() {
        @Override
        public void itemClick(int position, Object... objects) {
            tvStartMonth.setText("1");
        }
    };

    private ItemClickListener endYearListener = new ItemClickListener() {
        @Override
        public void itemClick(int position, Object... objects) {
            tvEndMonth.setText("1");
        }
    };

    private ElementWeatherView.OnBarItemClickListener barItemClickListener = new ElementWeatherView.OnBarItemClickListener() {

        @Override
        public void onItemClick(int position) {
            if(yearDataList.size() > position) {
                currentMonthPosition = position;
                requestMonth(position);
            }
        }
    };

    private ElementWeatherView.OnBarScrollListener barScrollListener = new ElementWeatherView.OnBarScrollListener() {
        @Override
        public void onScrollToEnd(boolean b) {
            if(b) {
                viewRight.setVisibility(View.INVISIBLE);
            } else {
                viewRight.setVisibility(View.VISIBLE);
            }
        }
    };

    private View.OnTouchListener maskTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(v.getId() == R.id.layout_mask && event.getAction() == MotionEvent.ACTION_DOWN) {
                if (!mActivity.isBought) {
                    mActivity.checkLogin();
                    return true;
                } else {
                    return false;
                }
            }
            return false;
        }
    };

    private RecyclerView.OnScrollListener recyclerviewScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            setListRightVisibility();
        }
    };

    private class MyRecevier extends PcsDataBrocastReceiver {

        @Override
        public void onReceive(String nameStr, String errorStr) {
            if (areaUp.getName().equals(nameStr)) {
                PackDataQueryAreaConfigDown down = (PackDataQueryAreaConfigDown) PcsDataManager.getInstance()
                        .getNetPack(nameStr);
                if (down == null) {
                    return;
                }
                if(!TextUtils.isEmpty(down.des)) {
                    tvTips.setText(down.des);
                }
                areaList.clear();
                areaList.addAll(down.p_list);
                updateArea(0, 0);
                requestElement();
            } else if (itemUp.getName().equals(nameStr)) {
                PackDataQueryItemConfigDown down = (PackDataQueryItemConfigDown) PcsDataManager.getInstance()
                        .getNetPack(nameStr);
                if (down == null) {
                    return;
                }
                itemList.clear();
                itemList.addAll(down.p_list);
                int position = 0;
                if(itemList.size() > 0) {
                    List<SubItemConfig> list = itemList.get(0).sub_list;
                    for(SubItemConfig subConfig : list) {
                        if(subConfig.is_check.equals("1")) {
                            position = list.indexOf(subConfig);
                            break;
                        }
                    }
                }
                updateItem(0, position);
                requestElement();
            } else if (PackWeatherElementYearUp.NAME.equals(nameStr)) {
                mActivity.dismissProgressDialog();
                PackWeatherElementYearDown down = (PackWeatherElementYearDown) PcsDataManager.getInstance().getNetPack(nameStr);
                if(down == null) {
                    return;
                }
                tvDayCount.setText(String.format("查询结果：共计%s日", down.day_sum));
                yearDataList.clear();
                yearDataList.addAll(down.info_list);
                elementWeatherView.setData(yearDataList);
                layoutMonth.setVisibility(View.GONE);
                requestMonth(currentMonthPosition);
            } else if (PackWeatherElementMonthUp.NAME.equals(nameStr)) {
                mActivity.dismissProgressDialog();
                PackWeatherElementMonthDown down = (PackWeatherElementMonthDown) PcsDataManager.getInstance().getNetPack(nameStr);
                if(down == null) {
                    return;
                }
                updateMonth(down.info_list);
            }
        }
    }
}
