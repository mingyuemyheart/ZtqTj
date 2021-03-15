package com.pcs.ztqtj.view.fragment.livequery.fujian_city;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.livequery.AdapterCompImage;
import com.pcs.ztqtj.control.adapter.livequery.AdapterTempertureHight;
import com.pcs.ztqtj.control.inter.DrowListClick;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.view.activity.livequery.ActivityLiveQuery;
import com.pcs.ztqtj.view.activity.livequery.ActivityLiveQueryDetail;
import com.pcs.ztqtj.view.activity.livequery.LiveQueryPopupWindowTool;
import com.pcs.ztqtj.view.fragment.livequery.FragmentLiveQueryCommon;
import com.pcs.ztqtj.view.myview.CompleView;
import com.pcs.ztqtj.view.myview.MyListView;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCity;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackWdtjZdzDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackWdtjZdzUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackYltjYearTempDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackYltjYearTempUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.YltjYear;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.pcs.ztqtj.R.id.livequery_city_spinner;
import static com.pcs.ztqtj.R.id.livequery_town_spinner;
import static com.pcs.ztqtj.view.fragment.livequery.fujian_city.FragmentLowTemperature.SelectType.CURRENT;
import static com.pcs.ztqtj.view.fragment.livequery.fujian_city.FragmentLowTemperature.SelectType.HOURS;
import static com.pcs.ztqtj.view.fragment.livequery.fujian_city.FragmentLowTemperature.SelectType._24HOUR;

/**
 * Created by tyaathome on 2017/8/21.
 */

public class FragmentHighTemperature extends FragmentLiveQueryCommon {

    private ScrollView scrollView;
    private TextView tvDataDesc;
    private RadioButton left, right, rb24h, rbHours;
    private TextView tvCityDropDown, tvTownDropDown;
    private RadioGroup radioGroup;
    private TextView tvTempDescTitle;
    // 时间段控件
    private RadioGroup rgHours;
    private MyListView listView;
    private TextView halfayear_data_introduction;
    private TextView history_avg;
    private TextView history_max;
    // 绘图说明
    private TextView year_darkblue;
    private TextView year_green;
    private TextView year_low_green;
    private LinearLayout table_layout;
    //对比图视图
    private TextView view_desc;
    private ImageView table_data;
    private LinearLayout comp_layout;

    private MyReceiver receiver = new MyReceiver();
    private ActivityLiveQuery activity;
    private CityListControl cityControl;
    private boolean isShowPopupWindow = false;
    // 当前时间段选择位置
    private int currentHourPosition = 0;

    private PackWdtjZdzUp highTempUp = new PackWdtjZdzUp();
    private PackWdtjZdzUp high24HourUp = new PackWdtjZdzUp();
    private PackWdtjZdzUp highHoursUp = new PackWdtjZdzUp();
    /**
     * 半年对比图
     */
    private PackYltjYearTempUp yltjYearUp;

    private final List<YltjYear> yltjYearList = new ArrayList<YltjYear>();
    private List<YltjYear> rainfallcomp = new ArrayList<>();
    private AdapterCompImage adaptercomp;
    private MyListView livequery_rainfall_complete;
    private CompleView rainfall_comp_view;
    private boolean isShowCompImage = true;
    private TextView description_title_search3_;
    private LinearLayout lay_tem_a;
    // 当前适配器
    private AdapterTempertureHight adapter;
    // 当前使用数据
    private List<PackWdtjZdzDown.WdtjZdz> mDataList = new ArrayList<>();
    /**
     * 列表标题
     */
    private PackWdtjZdzDown.WdtjZdz titletemp = new PackWdtjZdzDown().new WdtjZdz();

    private FragmentLowTemperature.SelectType currentSelectType = CURRENT;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (ActivityLiveQuery) activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState) {
        return inflater.inflate(R.layout.fragement_livequery_low_tem, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initEvent();
        initData();
        redrawUI();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(receiver != null) {
            getActivity().unregisterReceiver(receiver);
            receiver = null;
        }
    }

    @Override
    public void refleshData() {
        req();
    }

    private void initView() {
        scrollView = (ScrollView) getView().findViewById(R.id.scrollview);
        lay_tem_a= (LinearLayout) getView().findViewById(R.id.lay_tem_a);
        tvDataDesc = (TextView) getView().findViewById(R.id.data_desc);
        left = (RadioButton) getView().findViewById(R.id.lowtemradiogroupleft);
        right = (RadioButton) getView().findViewById(R.id.lowtemradiogroupright);
        tvCityDropDown = (TextView) getView().findViewById(livequery_city_spinner);
        tvTownDropDown = (TextView) getView().findViewById(livequery_town_spinner);
        radioGroup = (RadioGroup) getView().findViewById(R.id.lowtemradiogroup);
        rgHours = (RadioGroup) getView().findViewById(R.id.group_hours);
        rb24h = (RadioButton) getView().findViewById(R.id.rb_24h);
        rbHours = (RadioButton) getView().findViewById(R.id.rb_hours);
        tvTempDescTitle = (TextView) getView().findViewById(R.id.description_title_low_on);
        listView = (MyListView) getView().findViewById(R.id.livequery_auto_min_table);
        halfayear_data_introduction = (TextView) getView().findViewById(R.id.halfayear_data_introduction);
        history_avg = (TextView) getView().findViewById(R.id.history_avg);
        history_max = (TextView) getView().findViewById(R.id.history_max);
        year_darkblue = (TextView) getView().findViewById(R.id.year_darkblue);
        year_green = (TextView) getView().findViewById(R.id.year_green);
        year_low_green = (TextView) getView().findViewById(R.id.year_low_green);
        view_desc = (TextView) getView().findViewById(R.id.view_desc);
        table_data = (ImageView) getView().findViewById(R.id.table_data);
        description_title_search3_ = (TextView) getView().findViewById(R.id.description_title_search3_);
        livequery_rainfall_complete = (MyListView) getView().findViewById(R.id.livequery_rainfall_complete_);
        comp_layout = (LinearLayout) getView().findViewById(R.id.comp_layout);
        table_layout = (LinearLayout) getView().findViewById(R.id.table_layout);
    }

    private void initEvent() {
        tvCityDropDown.setOnClickListener(onClickListener);
        tvTownDropDown.setOnClickListener(onClickListener);
        radioGroup.setOnCheckedChangeListener(onCheckedChangeListener);
        rgHours.setOnCheckedChangeListener(onCheckedChangeListener);
        rb24h.setOnClickListener(onClickListener);
        rbHours.setOnClickListener(onClickListener);
        table_layout.setOnClickListener(onClickListener);
        listView.setOnItemClickListener(onItemClickListener);
    }

    private void initData() {
        PcsDataBrocastReceiver.registerReceiver(this.getActivity(), receiver);
        cityControl = new CityListControl(activity.cityinfo);
        tvDataDesc.setText("区域、地区自动站高温查询");

        left.setText("高温实况值");
        right.setText("24小时内最高");
        rb24h.setText("近24小时最高");
        setHours();
        tvCityDropDown.setText(cityControl.getCutParentCity().NAME);
        tvTownDropDown.setText(cityControl.getCutChildCity().NAME);
        if (tvCityDropDown.getText().toString().equals("天津")){
            tvTempDescTitle.setText(cityControl.getCutChildCity().NAME + " 自动站高温实况统计表");
        }else{
            tvTempDescTitle.setText(cityControl.getCutChildCity().NAME + " 高温实况统计表");
        }
        adapter = new AdapterTempertureHight(getActivity(), mDataList);
        listView.setAdapter(adapter);
        titletemp.county = "站点";
        titletemp.time = "日期/时段";
        titletemp.max_wd = "气温°C";
        adaptercomp = new AdapterCompImage(getActivity(), rainfallcomp);
        adaptercomp.setDescVaule("历史同期月最高温", "历史同期月平均高温", "年月最高温");
        livequery_rainfall_complete.setAdapter(adaptercomp);
        rainfall_comp_view = (CompleView) getView().findViewById(R.id.rainfall_comp_view);
        rainfall_comp_view.setUnit("高温");
        description_title_search3_.setText(cityControl.getCutChildCity().NAME + " 高温对比图℃");
        req();
    }

    private void setHours() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if(hour == 0) {
            rbHours.setText("今日0时最高▼");
        } else {
            rbHours.setText("今日" + currentHourPosition + "时至" + hour + "时最高▼");
        }
    }

    private void redrawUI() {
        PackLocalCity currentParent = cityControl.getCutParentCity();
        if (currentParent != null) {
            if (currentParent.isFjCity) {
                PackLocalCity currentChild = cityControl.getCutChildCity();
                if(currentChild != null && currentChild.ID.equals("25183")) {
                    lay_tem_a.setVisibility(View.GONE);
                } else {
                    lay_tem_a.setVisibility(View.VISIBLE);
                }
            } else {
                lay_tem_a.setVisibility(View.GONE);
            }
        }
    }

    private void setTempDesc() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if(hour == 0) {
            if (tvCityDropDown.getText().toString().equals("天津")){
                tvTempDescTitle.setText(cityControl.getCutChildCity().NAME + " 自动站今日0时最高气温统计表");
            }else{
                tvTempDescTitle.setText(cityControl.getCutChildCity().NAME + " 今日0时最高气温统计表");
            }
        } else {
            if (tvCityDropDown.getText().toString().equals("天津")){
                tvTempDescTitle.setText(cityControl.getCutChildCity().NAME + " 自动站" + "今日" + currentHourPosition + "时至" + hour + "时最高气温统计表");
            }else{
                tvTempDescTitle.setText(cityControl.getCutChildCity().NAME + " " + "今日" + currentHourPosition + "时至" + hour + "时最高气温统计表");
            }
        }
    }

    /**
     * 设置24小时按钮是否显示
     * @param visibility
     */
    private void set24HoursGroupVisibility(boolean visibility) {
        if(visibility) {
            rgHours.setVisibility(View.VISIBLE);
            rb24h.performClick();
        } else {
            rgHours.setVisibility(View.GONE);
        }
    }

    /**
     * 创建今日时间列表
     * @return
     */
    private List<String> createHoursList() {
        List<String> list = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        if(hourOfDay == 0) {
            //list.add("今日0时");
        } else {
            for (int i = 0; i < hourOfDay; i++) {
                String str = "今日" + i + "时至" + hourOfDay + "时";
                list.add(str);
            }
        }
        return list;
    }

    /*isCity 获取的是否是九地市的，true为是*/
    private void getOutoLine() {
        if (cityControl.getParentData()) {
            highTempUp.type = "1";
            highTempUp.city = cityControl.getCutChildCity().NAME;
            highTempUp.county="";
            highTempUp.province = cityControl.getCutParentCity().ID;
            high24HourUp.type = "2";
            high24HourUp.city = cityControl.getCutChildCity().NAME;
            high24HourUp.county="";
            high24HourUp.province = cityControl.getCutParentCity().ID;
            highHoursUp.type = "3";
            highHoursUp.city = cityControl.getCutChildCity().NAME;
            highHoursUp.county="";
            highHoursUp.province = cityControl.getCutParentCity().ID;
        } else {
            highTempUp.type = "1";
            highTempUp.county = cityControl.getCutChildCity().NAME;
            highTempUp.city ="";
            highTempUp.province = cityControl.getCutParentCity().ID;
            high24HourUp.type = "2";
            high24HourUp.county = cityControl.getCutChildCity().NAME;
            high24HourUp.city ="";
            high24HourUp.province = cityControl.getCutParentCity().ID;
            highHoursUp.type = "3";
            highHoursUp.county = cityControl.getCutChildCity().NAME;
            highHoursUp.city="";
            highHoursUp.province = cityControl.getCutParentCity().ID;
        }
        highTempUp.is_jc = ZtqCityDB.getInstance().isServiceAccessible();
        high24HourUp.is_jc = ZtqCityDB.getInstance().isServiceAccessible();
        highHoursUp.is_jc = ZtqCityDB.getInstance().isServiceAccessible();
    }

    private void reFlushImage(String name) {
        // 雨量查询—地区半年降雨量对比（yltj_year）
        PackYltjYearTempDown yltjYearTempDown = (PackYltjYearTempDown) PcsDataManager.getInstance().getNetPack(name);
        if (yltjYearTempDown == null) {
            return;
        }
        halfayear_data_introduction.setText(yltjYearTempDown.a_desc);
        //横线说明
        history_avg.setText(yltjYearTempDown.wd_avg);
        history_max.setText(yltjYearTempDown.wd_m);
        yltjYearList.clear();
        yltjYearList.addAll(yltjYearTempDown.datalist);

        if (yltjYearList.size() > 0) {
            rainfallcomp.clear();
            rainfallcomp.add(yltjYearList.get(0));// 标题
            rainfallcomp.addAll(yltjYearList);
            adaptercomp.notifyDataSetChanged();
            scrollView.scrollTo(0, 0);
        } else {
            rainfallcomp.clear();
            adaptercomp.notifyDataSetChanged();
            scrollView.scrollTo(0, 0);
        }
        // 趋势图设置数据；
        if (yltjYearList.size() > 0) {
            rainfall_comp_view.setVisibility(View.VISIBLE);
            setRainView();
        } else {
            year_darkblue.setVisibility(View.GONE);
            year_green.setVisibility(View.GONE);
            year_low_green.setVisibility(View.GONE);
            rainfall_comp_view.setVisibility(View.GONE);
        }
    }

    /**
     * 趨勢圖數據處理
     */
    private void setRainView() {

        float[] h_rain = new float[6];
        float[] l_rain = new float[6];
        float[][] rect = new float[6][3];

        for (int i = 0; i < 3; i++) {
            rect[0][i] = CompleView.DATANull;
            rect[1][i] = CompleView.DATANull;
            rect[2][i] = CompleView.DATANull;
            rect[3][i] = CompleView.DATANull;
            rect[4][i] = CompleView.DATANull;
            rect[5][i] = CompleView.DATANull;
        }


        List<Integer> sorting = new ArrayList<Integer>();
        for (int i = 0; i < yltjYearList.size(); i++) {
            if (yltjYearList.get(i).year.equals("m_year")) {
                // 取出最大值
                h_rain[0] = getfloat(yltjYearList.get(i).month1);
                h_rain[1] = getfloat(yltjYearList.get(i).month2);
                h_rain[2] = getfloat(yltjYearList.get(i).month3);
                h_rain[3] = getfloat(yltjYearList.get(i).month4);
                h_rain[4] = getfloat(yltjYearList.get(i).month5);
                h_rain[5] = getfloat(yltjYearList.get(i).month6);
            } else if (yltjYearList.get(i).year.equals("avg_yaer")) {
                // 取出平均值
                l_rain[0] = getfloat(yltjYearList.get(i).month1);
                l_rain[1] = getfloat(yltjYearList.get(i).month2);
                l_rain[2] = getfloat(yltjYearList.get(i).month3);
                l_rain[3] = getfloat(yltjYearList.get(i).month4);
                l_rain[4] = getfloat(yltjYearList.get(i).month5);
                l_rain[5] = getfloat(yltjYearList.get(i).month6);
            } else {
                // 记录序号
                sorting.add(i);
            }
        }
        // 年分排序
        for (int i = 0; i < sorting.size() - 1; i++) {
            for (int j = i + 1; j < sorting.size(); j++) {
                if (Float.parseFloat(yltjYearList.get(sorting.get(i)).year) > Float.parseFloat(yltjYearList.get(sorting.get(j)).year)) {
                    YltjYear between = yltjYearList.get(sorting.get(i));
                    yltjYearList.set(sorting.get(i), yltjYearList.get(sorting.get(j)));
                    yltjYearList.set(sorting.get(j), between);
                }
            }
        }
        for (int i = 0; i < sorting.size(); i++) {
            // 取出年份并设置矩形图的数据
            if (i < 3) {
                rect[0][i] = getfloat(yltjYearList.get(sorting.get(i)).month1);
                rect[1][i] = getfloat(yltjYearList.get(sorting.get(i)).month2);
                rect[2][i] = getfloat(yltjYearList.get(sorting.get(i)).month3);
                rect[3][i] = getfloat(yltjYearList.get(sorting.get(i)).month4);
                rect[4][i] = getfloat(yltjYearList.get(sorting.get(i)).month5);
                rect[5][i] = getfloat(yltjYearList.get(sorting.get(i)).month6);
            }
        }


        String[] listXValue = new String[]{
                yltjYearList.get(0).month_name1.toString(),
                yltjYearList.get(0).month_name2.toString(),
                yltjYearList.get(0).month_name3.toString(),
                yltjYearList.get(0).month_name4.toString(),
                yltjYearList.get(0).month_name5.toString(),
                yltjYearList.get(0).month_name6.toString()};

        rainfall_comp_view.setViewData(h_rain, l_rain, rect, listXValue);

        try {
            if (sorting.size() == 4) {

                year_darkblue.setVisibility(View.VISIBLE);
                year_green.setVisibility(View.VISIBLE);
                year_low_green.setVisibility(View.VISIBLE);
                year_darkblue.setText(" " + yltjYearList.get(sorting.get(0)).year + "年");
                year_green.setText(" " + yltjYearList.get(sorting.get(1)).year + "年");
                year_low_green.setText(" " + yltjYearList.get(sorting.get(2)).year + "年");
            } else if (sorting.size() == 3) {
                year_darkblue.setVisibility(View.VISIBLE);
                year_green.setVisibility(View.VISIBLE);
                year_low_green.setVisibility(View.VISIBLE);
                year_darkblue.setText(" " + yltjYearList.get(sorting.get(0)).year + "年");
                year_green.setText(" " + yltjYearList.get(sorting.get(1)).year + "年");
                year_low_green.setText(" " + yltjYearList.get(sorting.get(2)).year + "年");
            } else if (sorting.size() == 2) {
                year_darkblue.setText(" " + yltjYearList.get(sorting.get(0)).year + "年");
                year_green.setText(" " + yltjYearList.get(sorting.get(1)).year + "年");
                year_darkblue.setVisibility(View.VISIBLE);
                year_green.setVisibility(View.VISIBLE);
                year_low_green.setVisibility(View.GONE);
            } else if (sorting.size() == 1) {
                year_darkblue.setText(" " + yltjYearList.get(sorting.get(0)).year + "年");
                year_darkblue.setVisibility(View.VISIBLE);
                year_green.setVisibility(View.GONE);
                year_low_green.setVisibility(View.GONE);
            } else {
                year_darkblue.setVisibility(View.GONE);
                year_green.setVisibility(View.GONE);
                year_low_green.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 字符转单精度
     *
     * @param str
     * @return
     */
    private float getfloat(String str) {
        if (TextUtils.isEmpty(str)) {
            return CompleView.DATANull;
        }
        return Float.parseFloat(str);
    }

    // 请求高温实况
    private void reqCurrentLow() {
        getOutoLine();
        PcsDataDownload.addDownload(highTempUp);
    }

    // 请求近24小时
    private void req24HourLow() {
        getOutoLine();
        PcsDataDownload.addDownload(high24HourUp);
    }

    // 请求时间段高温数据
    private void reqHour() {
        getOutoLine();
        highHoursUp.s_hour = String.valueOf(currentHourPosition);
        PcsDataDownload.addDownload(highHoursUp);
    }

    private void req() {
        switch (currentSelectType) {
            case CURRENT:
                reqCurrentLow();
                break;
            case _24HOUR:
                req24HourLow();
                break;
            case HOURS:
                reqHour();
                break;
        }
        reqYear();
    }

    private void reqYear() {
        // 雨量查询—地区半年降雨量对比（yltj_year）
        yltjYearUp = new PackYltjYearTempUp();
        yltjYearUp.area_id = cityControl.getCutChildCity().ID;
        yltjYearUp.type = "1";
        PcsDataDownload.addDownload(yltjYearUp);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case livequery_city_spinner:
                    // 省级列表选择
                    activity.createPopupWindow((TextView) v, cityControl.getParentShowNameList(), 0, dropDownListener)
                            .showAsDropDown(v);
                    break;
                case livequery_town_spinner:
                    // 城镇列表
                    PackLocalCity city = cityControl.getCutParentCity();
                    if(!city.isFjCity || ZtqCityDB.getInstance().isServiceAccessible()) {
                        activity.createPopupWindow((TextView) v, cityControl.getChildShowNameList(), 1, dropDownListener)
                                .showAsDropDown(v);
                    }
                    break;
                case R.id.rb_24h:
                    currentSelectType = _24HOUR;
                    req24HourLow();
                    break;
                case R.id.rb_hours:
                    currentSelectType = HOURS;
                    if(isShowPopupWindow) {
                        List<String> list = createHoursList();
                        if(list.size() > 0) {
                            LiveQueryPopupWindowTool
                                    .getInstance(getActivity())
                                    .createPopupWindow(v, list)
                                    .setListener(popupWindowItemClickListener)
                                    .show();
                        }
                    } else {
                        reqHour();
                        setHours();
                    }
                    isShowPopupWindow = true;
                    break;
                case R.id.table_layout:
                    // 显示数据还是显示图片
                    if (isShowCompImage) {
                        view_desc.setText("对比图");
                        isShowCompImage = false;
                        table_data.setImageResource(R.drawable.icon_comp_view);
                        description_title_search3_.setText(cityControl.getCutChildCity().NAME + " 高温对比表");
                        livequery_rainfall_complete.setVisibility(View.VISIBLE);
                        comp_layout.setVisibility(View.GONE);
                    } else {
                        view_desc.setText("对比表");
                        isShowCompImage = true;
                        table_data.setImageResource(R.drawable.icon_livequery_table);
                        description_title_search3_.setText(cityControl.getCutChildCity().NAME + " 高温对比图");
                        livequery_rainfall_complete.setVisibility(View.GONE);
                        comp_layout.setVisibility(View.VISIBLE);
                    }
                    break;
            }
        }
    };

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (position != 0) {
                String stationName = mDataList.get(position).county;
                if (stationName.equals("全部")) {
                    return;
                }
                Intent intent = new Intent(getActivity(), ActivityLiveQueryDetail.class);
                intent.putExtra("stationName", stationName);
                intent.putExtra("item", "temp");
                startActivity(intent);
            }

        }
    };

    private LiveQueryPopupWindowTool.OnPopupWindowItemClickListener popupWindowItemClickListener = new LiveQueryPopupWindowTool.OnPopupWindowItemClickListener() {

        @Override
        public void onClick(int position) {
            currentHourPosition = position;
            reqHour();
            setHours();
            setTempDesc();
        }
    };

    private RadioGroup.OnCheckedChangeListener onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
            switch (checkedId) {
                case R.id.lowtemradiogroupleft:
                    currentSelectType = CURRENT;
                    if (tvCityDropDown.getText().toString().equals("天津")){
                        tvTempDescTitle
                                .setText(cityControl.getCutChildCity().NAME + " 自动站高温实况统计表");
                    }else{
                        tvTempDescTitle
                                .setText(cityControl.getCutChildCity().NAME + " 高温实况统计表");
                    }
                    set24HoursGroupVisibility(false);
                    reqCurrentLow();
                    break;
                case R.id.lowtemradiogroupright:
                    set24HoursGroupVisibility(true);
                    if (tvCityDropDown.getText().toString().equals("天津")){
                        tvTempDescTitle.setText(cityControl.getCutChildCity().NAME + " 自动站近24小时最高气温统计表");
                    }else{
                        tvTempDescTitle.setText(cityControl.getCutChildCity().NAME + " 近24小时最高气温统计表");
                    }
                    rgHours.check(R.id.rb_24h);
                    currentHourPosition = 0;
                    setHours();
                    break;
                case R.id.rb_24h:
                    currentSelectType = _24HOUR;
                    isShowPopupWindow = false;
                    if (tvCityDropDown.getText().toString().equals("天津")){
                        tvTempDescTitle.setText(cityControl.getCutChildCity().NAME + " 自动站近24小时最高气温统计表");
                    }else{
                        tvTempDescTitle.setText(cityControl.getCutChildCity().NAME + " 近24小时最高气温统计表");
                    }

                    break;
                case R.id.rb_hours:
                    currentSelectType = HOURS;
                    isShowPopupWindow = false;
                    setTempDesc();
                    break;
            }
        }
    };

    /**
     * 下拉数据点击事件监听
     */
    private DrowListClick dropDownListener = new DrowListClick() {
        @Override
        public void itemClick(int floag, int item) {
            switch (floag) {
                case 0:
                    cityControl.checkParent(item);
                    tvTownDropDown.setText(cityControl.getCutChildCity().NAME);
                    if(isShowCompImage) {
                        description_title_search3_.setText(cityControl.getCutChildCity().NAME + "高温对比图℃");
                    } else {
                        description_title_search3_.setText(cityControl.getCutChildCity().NAME + "高温对比表℃");
                    }
                    setTempDescTitle();
                    req();
                    redrawUI();
                    break;
                case 1:
                    cityControl.checkChild(item);
                    setTempDescTitle();
                    if(isShowCompImage) {
                        description_title_search3_.setText(cityControl.getCutChildCity().NAME + "高温对比图℃");
                    } else {
                        description_title_search3_.setText(cityControl.getCutChildCity().NAME + "高温对比表℃");
                    }
                    req();
                    redrawUI();
                    break;
            }
        }
    };

    private void setTempDescTitle() {
        switch (currentSelectType) {
            case CURRENT:
                if (tvCityDropDown.getText().toString().equals("天津")){
                    tvTempDescTitle.setText(cityControl.getCutChildCity().NAME + " 自动站高温实况统计表");
                }else{
                    tvTempDescTitle.setText(cityControl.getCutChildCity().NAME + " 高温实况统计表");
                }
                break;
            case _24HOUR:
                if (tvCityDropDown.getText().toString().equals("天津")){
                    tvTempDescTitle.setText(cityControl.getCutChildCity().NAME + " 自动站近24小时最高气温统计表");
                }else{
                    tvTempDescTitle.setText(cityControl.getCutChildCity().NAME + " 近24小时最高气温统计表");
                }
                break;
            case HOURS:
                setTempDesc();
                break;
        }
    }

    public enum SelectType {
        CURRENT,
        _24HOUR,
        HOURS
    }

    private class MyReceiver extends PcsDataBrocastReceiver {

        @Override
        public void onReceive(String nameStr, String errorStr) {
            if(nameStr.equals(highTempUp.getName())) {
                activity.dismissProgressDialog();
                PackWdtjZdzDown down = (PackWdtjZdzDown) PcsDataManager.getInstance().getNetPack(nameStr);
                if(down == null) {
                    return;
                }
                mDataList.clear();
                mDataList.add(titletemp);
                mDataList.addAll(down.datalist);
                adapter.notifyDataSetChanged();
                scrollView.scrollTo(0, 0);
            } else if (nameStr.equals(high24HourUp.getName())) {
                activity.dismissProgressDialog();
                PackWdtjZdzDown down = (PackWdtjZdzDown) PcsDataManager.getInstance().getNetPack(nameStr);
                if(down == null) {
                    return;
                }
                mDataList.clear();
                mDataList.add(titletemp);
                mDataList.addAll(down.datalist);
                adapter.notifyDataSetChanged();
                scrollView.scrollTo(0, 0);
            } else if (nameStr.equals(highHoursUp.getName())) {
                activity.dismissProgressDialog();
                PackWdtjZdzDown down = (PackWdtjZdzDown) PcsDataManager.getInstance().getNetPack(nameStr);
                if(down == null) {
                    return;
                }
                mDataList.clear();
                mDataList.add(titletemp);
                mDataList.addAll(down.datalist);
                adapter.notifyDataSetChanged();
                scrollView.scrollTo(0, 0);
            } else if (yltjYearUp != null && nameStr.equals(yltjYearUp.getName())) {
                reFlushImage(nameStr);
            }
        }
    }
}
