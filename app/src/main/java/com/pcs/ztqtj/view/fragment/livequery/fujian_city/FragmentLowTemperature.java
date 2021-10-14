package com.pcs.ztqtj.view.fragment.livequery.fujian_city;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
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

import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCity;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackWdtjLowZdzDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackYltjYearTempDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.YltjYear;
import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.livequery.AdapterCompImage;
import com.pcs.ztqtj.control.adapter.livequery.AdapterTempertureLow;
import com.pcs.ztqtj.control.inter.DrowListClick;
import com.pcs.ztqtj.control.tool.utils.TextUtil;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.util.CommonUtil;
import com.pcs.ztqtj.util.OkHttpUtil;
import com.pcs.ztqtj.view.activity.livequery.ActivityLiveQuery;
import com.pcs.ztqtj.view.activity.livequery.ActivityLiveQueryDetail;
import com.pcs.ztqtj.view.activity.livequery.LiveQueryPopupWindowTool;
import com.pcs.ztqtj.view.fragment.livequery.FragmentLiveQueryCommon;
import com.pcs.ztqtj.view.myview.CompleView;
import com.pcs.ztqtj.view.myview.MyListView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.pcs.ztqtj.R.id.livequery_city_spinner;
import static com.pcs.ztqtj.R.id.livequery_town_spinner;

/**
 * 监测预报-实况查询-数据与统计-低温查询
 */
public class FragmentLowTemperature extends FragmentLiveQueryCommon {

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

    private ActivityLiveQuery activity;
    private CityListControl cityControl;
    private boolean isShowPopupWindow = false;
    // 当前时间段选择位置
    private int currentHourPosition = 0;

    private final List<YltjYear> yltjYearList = new ArrayList<YltjYear>();
    private List<YltjYear> rainfallcomp = new ArrayList<>();
    private AdapterCompImage adaptercomp;
    private MyListView livequery_rainfall_complete;
    private CompleView rainfall_comp_view;
    private boolean isShowCompImage = true;
    private TextView description_title_search3_;

    // 当前适配器
    private AdapterTempertureLow adapter;
    // 当前使用数据
    private List<PackWdtjLowZdzDown.WdtjLowZdz> mDataList = new ArrayList<>();
    /**
     * 列表标题
     */
    private PackWdtjLowZdzDown.WdtjLowZdz titletemp = new PackWdtjLowZdzDown().new WdtjLowZdz();

    private SelectType currentSelectType = SelectType.CURRENT;
    private LinearLayout lay_tem_a;

    private String currentStationId = "10103";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (ActivityLiveQuery) activity;
    }

    @Override
    public void refleshData() {
        req();
    }

    private void initView() {
        scrollView = (ScrollView) getView().findViewById(R.id.scrollview);
        lay_tem_a = (LinearLayout) getView().findViewById(R.id.lay_tem_a);
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
        cityControl = new CityListControl(activity.cityinfo);
        tvDataDesc.setText("区域、地区自动站低温查询");
        left.setText("低温实况值");
        right.setText("24小时内最低");
        rb24h.setText("近24小时最低");
        setHours();
        tvCityDropDown.setText(cityControl.getCutParentCity().NAME);
        if (cityControl.getCutParentCity().isFjCity) {
            tvTownDropDown.setText(cityControl.getCutParentCity().NAME);
            currentStationId = cityControl.getCutParentCity().ID;
        } else {
            tvTownDropDown.setText(cityControl.getCutChildCity().NAME);
            currentStationId = cityControl.getCutChildCity().ID;
        }

        if (tvCityDropDown.getText().toString().equals("天津")) {
            tvTempDescTitle.setText(cityControl.getCutChildCity().NAME + " 自动站低温实况统计表");
        } else {
            tvTempDescTitle.setText(cityControl.getCutChildCity().NAME + " 低温实况统计表");
        }

        adapter = new AdapterTempertureLow(getActivity(), mDataList);
        listView.setAdapter(adapter);
        titletemp.county = "站点";
        titletemp.time = "日期/时段";
        titletemp.min_wd = "气温°C";
        adaptercomp = new AdapterCompImage(getActivity(), rainfallcomp);
        adaptercomp.setDescVaule("历史同期月最低温", "历史同期月平均低温", "年月最低温");
        livequery_rainfall_complete.setAdapter(adaptercomp);
        rainfall_comp_view = (CompleView) getView().findViewById(R.id.rainfall_comp_view);
        rainfall_comp_view.setUnit("低温");
        description_title_search3_.setText(cityControl.getCutChildCity().NAME + " 低温对比图℃");
        req();
    }

    private void setHours() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (hour == 0) {
            rbHours.setText("今日0时最低▼");
        } else {
            rbHours.setText("今日" + currentHourPosition + "时至" + hour + "时最低▼");
        }
    }

    private void redrawUI() {
        PackLocalCity currentParent = cityControl.getCutParentCity();
        if (currentParent != null) {
            if (currentParent.isFjCity) {
                if (CommonUtil.isHaveAuth("201040701")) {//是否有查看自动站权限
                    lay_tem_a.setVisibility(View.VISIBLE);
                } else {
                    lay_tem_a.setVisibility(View.GONE);
                }
            } else {
                lay_tem_a.setVisibility(View.GONE);
            }
        }
    }

    private void setTempDesc() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (hour == 0) {
            if (tvCityDropDown.getText().toString().equals("天津")) {
                tvTempDescTitle.setText(cityControl.getCutChildCity().NAME + " 自动站今日0时最低气温统计表");
            } else {
                tvTempDescTitle.setText(cityControl.getCutChildCity().NAME + " 今日0时最低气温统计表");
            }
        } else {
            if (tvCityDropDown.getText().toString().equals("天津")) {
                tvTempDescTitle.setText(cityControl.getCutChildCity().NAME + " 自动站" + "今日" + currentHourPosition +
                        "时至" + hour + "时最低气温统计表");
            } else {
                tvTempDescTitle.setText(cityControl.getCutChildCity().NAME + " " + "今日" + currentHourPosition +
                        "时至" + hour + "时最低气温统计表");
            }

        }
    }

    /**
     * 设置24小时按钮是否显示
     *
     * @param visibility
     */
    private void set24HoursGroupVisibility(boolean visibility) {
        if (visibility) {
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
        if (hourOfDay == 0) {
            //list.add("今日0时");
        } else {
            for (int i = 0; i < hourOfDay; i++) {
                String str = "今日" + i + "时至" + hourOfDay + "时";
                list.add(str);
            }
        }
        return list;
    }

    private void reFlushImage(PackYltjYearTempDown yltjYearTempDown) {
        // 雨量查询—地区半年降雨量对比（yltj_year）
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
                if (Float.parseFloat(yltjYearList.get(sorting.get(i)).year) > Float.parseFloat(yltjYearList.get
                        (sorting.get(j)).year)) {
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

    // 请求低温实况
    private void reqCurrentLow() {
        try {
            JSONObject param = new JSONObject();
            param.put("token", MyApplication.TOKEN);
            JSONObject info = new JSONObject();
            info.put("stationId", currentStationId);
            info.put("flag", "minTempObs");//minTempObs （低温实况值）、min24h（24小时低温）
            if (currentStationId.startsWith("10103")) {
                info.put("type", "天津");//如果是天津及其下属区，传天津，不是则为""
            } else {
                info.put("type", "");//如果是天津及其下属区，传天津，不是则为""
            }
            param.put("paramInfo", info);
            String json = param.toString();
            okHttpRankLowTemp(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // 请求近24小时
    private void req24HourLow() {
        try {
            JSONObject param = new JSONObject();
            param.put("token", MyApplication.TOKEN);
            JSONObject info = new JSONObject();
            info.put("stationId", currentStationId);
            info.put("flag", "min24h");//minTempObs （低温实况值）、min24h（24小时低温）
            if (currentStationId.startsWith("10103")) {
                info.put("type", "天津");//如果是天津及其下属区，传天津，不是则为""
            } else {
                info.put("type", "");//如果是天津及其下属区，传天津，不是则为""
            }
            info.put("time", "9999");//当flag为min24h的时候，分传时间和不传时间，不传的时候填 "9999" 传的时候格式为：0~23（代表今日多少时）
            param.put("paramInfo", info);
            String json = param.toString();
            okHttpRankLowTemp(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // 请求时间段低温数据
    private void reqHour() {
        try {
            JSONObject param = new JSONObject();
            param.put("token", MyApplication.TOKEN);
            JSONObject info = new JSONObject();
            info.put("stationId", currentStationId);
            info.put("flag", "min24h");//minTempObs （低温实况值）、min24h（24小时低温）
            if (currentStationId.startsWith("10103")) {
                info.put("type", "天津");//如果是天津及其下属区，传天津，不是则为""
            } else {
                info.put("type", "");//如果是天津及其下属区，传天津，不是则为""
            }
            info.put("time", currentHourPosition+"");//当flag为min24h的时候，分传时间和不传时间，不传的时候填 "9999" 传的时候格式为：0~23（代表今日多少时）
            param.put("paramInfo", info);
            String json = param.toString();
            okHttpRankLowTemp(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

        okHttpHistoryMonth();
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
//                    if(!city.isFjCity || ZtqCityDB.getInstance().isServiceAccessible()) {
                        activity.createPopupWindow((TextView) v, cityControl.getChildShowNameList(), 1, dropDownListener)
                                .showAsDropDown(v);
//                    }
                    break;
                case R.id.rb_24h:
                    currentSelectType = SelectType._24HOUR;
                    req24HourLow();
                    break;
                case R.id.rb_hours:
                    currentSelectType = SelectType.HOURS;
                    if (isShowPopupWindow) {
                        List<String> list = createHoursList();
                        if (list.size() > 0) {
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
                        description_title_search3_.setText(cityControl.getCutChildCity().NAME + " 低温对比表");
                        livequery_rainfall_complete.setVisibility(View.VISIBLE);
                        comp_layout.setVisibility(View.GONE);
                    } else {
                        view_desc.setText("对比表");
                        isShowCompImage = true;
                        table_data.setImageResource(R.drawable.icon_livequery_table);
                        description_title_search3_.setText(cityControl.getCutChildCity().NAME + " 低温对比图");
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
                intent.putExtra("item", "temp");
                intent.putExtra("stationName", stationName);
                startActivity(intent);
            }
        }
    };

    private LiveQueryPopupWindowTool.OnPopupWindowItemClickListener popupWindowItemClickListener = new
            LiveQueryPopupWindowTool.OnPopupWindowItemClickListener() {

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
                    currentSelectType = SelectType.CURRENT;
                    if (tvCityDropDown.getText().toString().equals("天津")) {
                        tvTempDescTitle
                                .setText(cityControl.getCutChildCity().NAME + " 自动站低温实况统计表");
                    } else {
                        tvTempDescTitle
                                .setText(cityControl.getCutChildCity().NAME + " 低温实况统计表");
                    }

                    set24HoursGroupVisibility(false);
                    reqCurrentLow();
                    break;
                case R.id.lowtemradiogroupright:
                    set24HoursGroupVisibility(true);
                    if (tvCityDropDown.getText().toString().equals("天津")) {
                        tvTempDescTitle.setText(cityControl.getCutChildCity().NAME + " 自动站近24小时最低气温统计表");
                    } else {
                        tvTempDescTitle.setText(cityControl.getCutChildCity().NAME + " 近24小时最低气温统计表");
                    }

                    rgHours.check(R.id.rb_24h);
                    currentHourPosition = 0;
                    setHours();
                    break;
                case R.id.rb_24h:
                    currentSelectType = SelectType._24HOUR;
                    isShowPopupWindow = false;
                    if (tvCityDropDown.getText().toString().equals("天津")) {
                        tvTempDescTitle.setText(cityControl.getCutChildCity().NAME + " 自动站近24小时最低气温统计表");
                    } else {
                        tvTempDescTitle.setText(cityControl.getCutChildCity().NAME + " 近24小时最低气温统计表");
                    }

                    break;
                case R.id.rb_hours:
                    currentSelectType = SelectType.HOURS;
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
                    if (isShowCompImage) {
                        description_title_search3_.setText(cityControl.getCutChildCity().NAME + "低温对比图℃");
                    } else {
                        description_title_search3_.setText(cityControl.getCutChildCity().NAME + "低温对比表℃");
                    }
                    setTempDescTitle();
                    req();
                    redrawUI();
                    break;
                case 1:
                    cityControl.checkChild(item);
                    currentStationId = cityControl.getCutChildCity().ID;
                    setTempDescTitle();
                    if (isShowCompImage) {
                        description_title_search3_.setText(cityControl.getCutChildCity().NAME + "低温对比图℃");
                    } else {
                        description_title_search3_.setText(cityControl.getCutChildCity().NAME + "低温对比表℃");
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
                if (tvCityDropDown.getText().toString().equals("天津")) {
                    tvTempDescTitle.setText(cityControl.getCutChildCity().NAME + " 自动站低温实况统计表");
                } else {
                    tvTempDescTitle.setText(cityControl.getCutChildCity().NAME + " 低温实况统计表");
                }

                break;
            case _24HOUR:
                if (tvCityDropDown.getText().toString().equals("天津")) {
                    tvTempDescTitle.setText(cityControl.getCutChildCity().NAME + " 自动站近24小时最低气温统计表");
                } else {
                    tvTempDescTitle.setText(cityControl.getCutChildCity().NAME + " 近24小时最低气温统计表");
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

    /**
     * 获取低温排行
     */
    private void okHttpRankLowTemp(final String json) {
        activity.showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e("minTemp", json);
                String url = CONST.BASE_URL+"minTemp";
                Log.e("minTemp", url);
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
                                if (!TextUtil.isEmpty(result)) {
                                    try {
                                        JSONObject obj = new JSONObject(result);
                                        if (!obj.isNull("b")) {
                                            JSONObject bobj = obj.getJSONObject("b");
                                            if (!bobj.isNull("wdtj_low_zdz")) {
                                                JSONObject wdtj_low_zdz = bobj.getJSONObject("wdtj_low_zdz");
                                                if (!TextUtil.isEmpty(wdtj_low_zdz.toString())) {
                                                    activity.dismissProgressDialog();
                                                    PackWdtjLowZdzDown down = new PackWdtjLowZdzDown();
                                                    down.fillData(wdtj_low_zdz.toString());
                                                    mDataList.clear();
                                                    mDataList.add(titletemp);
                                                    mDataList.addAll(down.datalist);
                                                    adapter.notifyDataSetChanged();
                                                    scrollView.scrollTo(0, 0);
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
            }
        }).start();
    }

    /**
     * 获取图表
     */
    private void okHttpHistoryMonth() {
        activity.showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    JSONObject info = new JSONObject();
                    info.put("stationId", currentStationId);
                    info.put("element", "tmin");
                    param.put("paramInfo", info);
                    String json = param.toString();
                    Log.e("history_month", json);
                    final String url = CONST.BASE_URL+"history_month";
                    Log.e("history_month", url);
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
                                    activity.dismissProgressDialog();
                                    Log.e("history_month", result);
                                    if (!TextUtil.isEmpty(result)) {
                                        if (CommonUtil.isHaveAuth("201040701")) {//是否有查看自动站权限
                                            lay_tem_a.setVisibility(View.VISIBLE);
                                        } else {
                                            lay_tem_a.setVisibility(View.GONE);
                                        }
                                        try {
                                            JSONObject obj = new JSONObject(result);
                                            if (!obj.isNull("b")) {
                                                JSONObject bobj = obj.getJSONObject("b");
                                                if (!bobj.isNull("yltj_wd_year")) {
                                                    JSONObject yltj_wd_year = bobj.getJSONObject("yltj_wd_year");
                                                    if (!TextUtil.isEmpty(yltj_wd_year.toString())) {
                                                        PackYltjYearTempDown yearDown = new PackYltjYearTempDown();
                                                        yearDown.fillData(yltj_wd_year.toString());
                                                        reFlushImage(yearDown);
                                                    }
                                                }
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        lay_tem_a.setVisibility(View.GONE);
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
