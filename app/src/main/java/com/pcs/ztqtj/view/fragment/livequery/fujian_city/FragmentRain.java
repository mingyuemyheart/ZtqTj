package com.pcs.ztqtj.view.fragment.livequery.fujian_city;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.pcs.lib.lib_pcs_v3.control.tool.Util;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCity;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackYltjHourDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackYltjHourDown.RainFall;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackYltjRankDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackYltjRankDown.RainFallRank;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackYltjTimeDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackYltjTimeUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackYltjYearDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.YltjYear;
import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.livequery.AdapterCompImage;
import com.pcs.ztqtj.control.adapter.livequery.AdapterData;
import com.pcs.ztqtj.control.adapter.livequery.AdapterRainSearchResult;
import com.pcs.ztqtj.control.adapter.livequery.AdapterTempertureRainFall;
import com.pcs.ztqtj.control.adapter.livequery.AdatperAutoRainFall;
import com.pcs.ztqtj.control.adapter.livequery.AdatperAutoRainFall.RainFallIn;
import com.pcs.ztqtj.control.inter.DrowListClick;
import com.pcs.ztqtj.control.tool.utils.TextUtil;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.util.CommonUtil;
import com.pcs.ztqtj.util.OkHttpUtil;
import com.pcs.ztqtj.view.activity.citylist.ActivityCityListCountry;
import com.pcs.ztqtj.view.activity.livequery.ActivityLiveQuery;
import com.pcs.ztqtj.view.activity.livequery.ActivityLiveQueryDetail;
import com.pcs.ztqtj.view.activity.livequery.ActivityTimeSearch;
import com.pcs.ztqtj.view.dialog.DialogFactory.DialogListener;
import com.pcs.ztqtj.view.dialog.DialogOneButton;
import com.pcs.ztqtj.view.dialog.DialogWaiting;
import com.pcs.ztqtj.view.fragment.livequery.FragmentLiveQueryCommon;
import com.pcs.ztqtj.view.myview.CompleView;
import com.pcs.ztqtj.view.myview.MyListView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 监测预报-实况查询-数据与统计-雨量查询
 */
public class FragmentRain extends FragmentLiveQueryCommon implements OnClickListener {

    private TextView view_desc;
    private LinearLayout table_layout;
    // 雨量查询下拉选项--------
    private TextView livequery_start_date;
    private TextView livequery_start_time;
    private TextView livequery_todata;
    private TextView livequery_totime;
    private TextView livequery_city_spinner;
    private TextView livequery_town_spinner;
    // --------------
    // 全省雨量查询按钮

    private Button livequery_search_btn;

    private MyListView livequery_auto_rainfall;
    private MyListView lvBaseRainfall;
    private MyListView livequery_max_rainfall;
    // 文字标题
    private TextView description_title_search;

    private ActivityLiveQuery activity;
    private MyListView livequery_rainfall_complete;
    private CompleView rainfall_comp_view;
    private List<YltjYear> rainfallcomp;
    private AdapterCompImage adaptercomp;

    private final List<YltjYear> yltjYearList = new ArrayList<>();

    // 绘图说明
    private TextView year_darkblue;
    private TextView year_green;
    private TextView year_low_green;
    private TextView year_blue;
    private LinearLayout comp_layout;
    private ImageView table_data;

    private List<String> start_date_data;
    private List<String> start_time_data;
    private List<String> todata_data;
    private List<String> totime_data;

    private List<RainFall> autoRainFall;
    private List<RainFall> baseRainFall;
    private List<RainFallRank> rankRainFall;

    private AdatperAutoRainFall rainfalladatper;
    private AdatperAutoRainFall baseRainfallAdatper;
    private AdapterTempertureRainFall rainfallMaxadatper;

    private RainFallRank titleRank;
    private RainFall titleauto;

    /**
     * 开始时间列表--day
     */
    private List<Long> startDate;
    /**
     * 结束时间列表--day
     */
    private List<Long> endDate;
    /**
     * 开始时间位置
     */
    private int startDateDayPosition = 0;
    /**
     * 开始时间--小时
     */
    private String startDateTime;
    /**
     * 结束时间位置
     */
    private int endDateDayPosition = 0;
    /**
     * 结束时间--小时
     */
    private String endDateTime;

    private ScrollView scrollView;

    /**
     * 下拉框设置tag 1、为起始时间-日期 2、起始时间-时间 3、结束时间-日期 4、结束时间-时间 5、市下拉列表 6、镇下拉列表
     */
    private final int start_date = 1, start_time = 2, todata = 3, totime = 4, city_spinner = 5, town_spinner = 6;
    private DialogOneButton dialogSeachResult;
    private DialogWaiting searchDialog;
    private boolean isShowCompImage = true;

    private PackYltjHourDown hourDown;

    private PackYltjTimeDown searchpackdwon;
    private PackYltjTimeUp searchpack;
    private Toast toast;

    private TextView halfayear_data_introduction;
    private Button to_citylist;

    private Button time_search;
    private Button btnTimeSearchBase;

    private TextView history_avg;
    private TextView history_max;

    private ImageView iv_auto_hiside;
    private LinearLayout lay_auto_hiside;
    private LinearLayout lay_is_tj,llHour24,layout_a;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (ActivityLiveQuery) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        PcsDataBrocastReceiver.registerReceiver(this.getActivity(), receiver);
        return inflater.inflate(R.layout.fragement_livequery_rain, null);
    }

    private final MyReceiver receiver = new MyReceiver();

    public void refleshData() {
        reqNet();
    }

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

    private Button btn_jyqddjhf;

    private void initView() {
        scrollView = (ScrollView) getView().findViewById(R.id.scrollview);
        btn_jyqddjhf = (Button) getView().findViewById(R.id.btn_jyqddjhf);
        time_search = (Button) getView().findViewById(R.id.time_search);
        btnTimeSearchBase = getView().findViewById(R.id.time_search_base);
        to_citylist = (Button) getView().findViewById(R.id.to_citylist);
        table_layout = (LinearLayout) getView().findViewById(R.id.table_layout);
        view_desc = (TextView) getView().findViewById(R.id.view_desc);
        halfayear_data_introduction = (TextView) getView().findViewById(R.id.halfayear_data_introduction);
        livequery_start_date = (TextView) getView().findViewById(R.id.livequery_start_date);
        livequery_start_time = (TextView) getView().findViewById(R.id.livequery_start_time);
        livequery_todata = (TextView) getView().findViewById(R.id.livequery_todata);
        livequery_totime = (TextView) getView().findViewById(R.id.livequery_totime);
        livequery_city_spinner = (TextView) getView().findViewById(R.id.livequery_city_spinner);
        livequery_town_spinner = (TextView) getView().findViewById(R.id.livequery_town_spinner);
        livequery_search_btn = (Button) getView().findViewById(R.id.livequery_search_btn);
        livequery_auto_rainfall = (MyListView) getView().findViewById(R.id.livequery_auto_rainfall);
        lvBaseRainfall = getView().findViewById(R.id.livequery_auto_rainfall_base);

        // 福州24小时内最大雨量排名（1、3小时）
        livequery_max_rainfall = (MyListView) getView().findViewById(R.id.livequery_max_rainfall);
        description_title_search = (TextView) getView().findViewById(R.id.description_title_search);

        /************* 雨量分布图 **************/
        year_darkblue = (TextView) getView().findViewById(R.id.year_darkblue);
        year_green = (TextView) getView().findViewById(R.id.year_green);
        year_low_green = (TextView) getView().findViewById(R.id.year_low_green);
        year_blue = (TextView) getView().findViewById(R.id.year_blue);

        // 降雨量对比图
        livequery_rainfall_complete = (MyListView) getView().findViewById(R.id.livequery_rainfall_complete_);
        rainfall_comp_view = (CompleView) getView().findViewById(R.id.rainfall_comp_view);
        rainfall_comp_view.isStartZero(true);
        comp_layout = (LinearLayout) getView().findViewById(R.id.comp_layout);
        table_data = (ImageView) getView().findViewById(R.id.table_data);
        history_avg = (TextView) getView().findViewById(R.id.history_avg);
        history_max = (TextView) getView().findViewById(R.id.history_max);
        iv_auto_hiside=getView().findViewById(R.id.iv_auto_hiside);
        lay_auto_hiside=getView().findViewById(R.id.lay_auto_hiside);
        lay_is_tj= getView().findViewById(R.id.lay_is_tj);
        llHour24 = getView().findViewById(R.id.llHour24);
        layout_a = getView().findViewById(R.id.layout_a);
    }

    private CityListControl cityControl;

    /**
     * 初始化数据
     */
    private void initData() {
        halfayear_data_introduction.setText("数据说明：");
        startDate = new ArrayList<>();
        endDate = new ArrayList<>();
        start_date_data = new ArrayList<>();
        start_time_data = new ArrayList<>();
        todata_data = new ArrayList<>();
        totime_data = new ArrayList<>();

        cityControl = new CityListControl(activity.cityinfo);
        setTime();
        autoRainFall = new ArrayList<>();
        baseRainFall = new ArrayList<>();
        rankRainFall = new ArrayList<>();

        titleauto = new PackYltjHourDown().new RainFall();
        titleauto.county = "站点";
        titleauto.hour1 = "1小时";
        titleauto.hour3 = "3小时";
        titleauto.hour6 = "6小时";
        titleauto.hour12 = "12小时";
        titleauto.hour24 = "24小时";
        autoRainFall.add(titleauto);
        baseRainFall.add(titleauto);

        PackYltjRankDown bbb = new PackYltjRankDown();
        titleRank = bbb.new RainFallRank();
        titleRank.area_name = "站点";
        titleRank.time = "日期/时段";
        titleRank.rainfall = "雨量";
        rankRainFall.add(titleRank);

        rainfalladatper = new AdatperAutoRainFall(getActivity(), autoRainFall, titleclickListener);
        baseRainfallAdatper = new AdatperAutoRainFall(getActivity(), baseRainFall, basetitleclickListener);
        rainfallMaxadatper = new AdapterTempertureRainFall(getActivity(), rankRainFall);

        livequery_auto_rainfall.setAdapter(rainfalladatper);
        lvBaseRainfall.setAdapter(baseRainfallAdatper);

        livequery_max_rainfall.setAdapter(rainfallMaxadatper);

        rainfallcomp = new ArrayList<>();

        adaptercomp = new AdapterCompImage(getActivity(), rainfallcomp);
        livequery_rainfall_complete.setAdapter(adaptercomp);

        livequery_city_spinner.setText(cityControl.getCutParentCity().NAME);
        if (cityControl.getCutParentCity().isFjCity) {
            livequery_town_spinner.setText(cityControl.getCutParentCity().NAME);
        } else {
            livequery_town_spinner.setText(cityControl.getCutChildCity().NAME);
        }
//			查询具体城市
        seachCityInfo = activity.cityinfo;
        reflushListTitle();
        reqNet();
        redrawUI();
    }

    /***
     * 刷新列表标题
     */
    private void reflushListTitle() {
        description_title_search.setText(seachCityInfo.NAME + "  本站雨量区间查询");
    }

    private void setTime() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日");
            SimpleDateFormat sdfDay = new SimpleDateFormat("dd");
            Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);

            start_date_data.add(sdf.format(c.getTime()));
            todata_data.add(sdf.format(c.getTime()));
            livequery_todata.setText(sdfDay.format(c.getTime()));
            livequery_totime.setText(hour + ":00");
            startDate.add(c.getTimeInMillis());
            endDate.add(c.getTimeInMillis());
            endDateTime = hour + ":00";
            if (hour > 8) {
                livequery_start_date.setText(sdfDay.format(c.getTime()));
                livequery_start_time.setText(8 + ":00");
                startDateTime = "8:00";
                for (int i = 0; i < 5; i++) {
                    c.add(Calendar.DAY_OF_MONTH, -1);
                    start_date_data.add(sdf.format(c.getTime()));
                    todata_data.add(sdf.format(c.getTime()));
                    startDate.add(c.getTimeInMillis());
                    endDate.add(c.getTimeInMillis());
                }
            } else {
                c.add(Calendar.DAY_OF_MONTH, -1);
                int hour2 = c.get(Calendar.HOUR_OF_DAY);
                livequery_start_time.setText(20 + ":00");
                startDateTime = "20:00";
                livequery_start_date.setText(sdfDay.format(c.getTime()));
                start_date_data.add(sdf.format(c.getTime()));
                todata_data.add(sdf.format(c.getTime()));
                startDate.add(c.getTimeInMillis());
                endDate.add(c.getTimeInMillis());
                for (int i = 0; i < 3; i++) {
                    c.add(Calendar.DAY_OF_MONTH, -1);
                    // int day2 = c.get(Calendar.DAY_OF_MONTH);
                    start_date_data.add(sdf.format(c.getTime()));
                    todata_data.add(sdf.format(c.getTime()));
                    startDate.add(c.getTimeInMillis());
                    endDate.add(c.getTimeInMillis());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        start_time_data.clear();
        totime_data.clear();
        String[] date = getActivity().getResources().getStringArray(R.array.onedaytime);
        for (int i = 0; i < date.length; i++) {
            start_time_data.add(date[i] + ":00");
            totime_data.add(date[i] + ":00");
        }
    }

    private RainFallIn titleclickListener = new RainFallIn() {
        @Override
        public void itemClick(int clickC, int position) {
            if (position == 0) {
                if (clickC != 0) {
                    rainfalladatper.setClickposition(clickC);
                    reRank(clickC, autoRainFall, rainfalladatper);
                }
            } else {
                String stationName = autoRainFall.get(position).county;
                toDetail(stationName);
            }
        }
    };

    private RainFallIn basetitleclickListener = new RainFallIn() {
        @Override
        public void itemClick(int clickC, int position) {
            if (position == 0) {
                if (clickC != 0) {
                    baseRainfallAdatper.setClickposition(clickC);
                    reRank(clickC, baseRainFall, baseRainfallAdatper);
                }
            } else {
                String stationName = baseRainFall.get(position).county;
                toDetail(stationName);
            }
        }
    };

    private void toDetail(String stationName) {
        if (stationName.equals("全部")) {
            return;
        }
        Intent intent = new Intent(getActivity(), ActivityLiveQueryDetail.class);
        intent.putExtra("stationName", stationName);
        intent.putExtra("item", "rain");
        startActivity(intent);
    }

    private boolean isHideAuto=true;

    private void initEvent() {
        livequery_auto_rainfall.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    toDetail(autoRainFall.get(position).county);
                }
            }
        });
        lvBaseRainfall.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    toDetail(baseRainFall.get(position).county);
                }
            }
        });
        livequery_max_rainfall.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    toDetail(rankRainFall.get(position).area_name);
                }
            }
        });

        lay_auto_hiside.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isHideAuto){
                    iv_auto_hiside.setBackground(getContext().getResources().getDrawable(R.drawable.icon_auto_hide));
                    livequery_auto_rainfall.setVisibility(View.GONE);
                    isHideAuto=false;
                }else {
                    iv_auto_hiside.setBackground(getContext().getResources().getDrawable(R.drawable.icon_auto_show));
                    livequery_auto_rainfall.setVisibility(View.VISIBLE);
                    isHideAuto=true;
                }
            }
        });

        btn_jyqddjhf.setOnClickListener(this);
        livequery_start_date.setOnClickListener(this);
        livequery_start_time.setOnClickListener(this);
        livequery_todata.setOnClickListener(this);
        time_search.setOnClickListener(this);
        btnTimeSearchBase.setOnClickListener(this);
        livequery_totime.setOnClickListener(this);
        livequery_city_spinner.setOnClickListener(this);
        livequery_town_spinner.setOnClickListener(this);
        livequery_search_btn.setOnClickListener(this);

        livequery_totime.setOnClickListener(this);
        // table_data.setOnClickListener(this);
        table_layout.setOnClickListener(this);
        to_citylist.setOnClickListener(this);
    }

    /**
     * 创建下拉选择列表
     */
    public PopupWindow createTimePopupWindow(final TextView dropDownView, final List<String> dataeaum, final int floag,
                                             final DrowListClick listener) {
        AdapterData dataAdapter = new AdapterData(getActivity(), dataeaum);
        View popcontent = LayoutInflater.from(getActivity()).inflate(R.layout.pop_list_layout, null);
        ListView lv = (ListView) popcontent.findViewById(R.id.mylistviw);
        lv.setAdapter(dataAdapter);
        final PopupWindow pop = new PopupWindow(getActivity());
        pop.setContentView(popcontent);
        pop.setOutsideTouchable(false);
        pop.setWidth((int) (dropDownView.getWidth() * 2));
        // 调整下拉框长度
        int screenHight = Util.getScreenHeight(getActivity());
        if (dataeaum.size() < 9) {
            pop.setHeight(LayoutParams.WRAP_CONTENT);
        } else {
            pop.setHeight((int) (screenHight * 0.6));
        }
        pop.setFocusable(true);
        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pop.dismiss();
                String showTimeStr = dataeaum.get(position);
                dropDownView.setText(showTimeStr.substring(showTimeStr.indexOf("月") + 1, showTimeStr.indexOf("日")));
                listener.itemClick(floag, position);
            }
        });
        return pop;
    }

    /**
     * 查询具体城市使用的城市信息
     **/
    private PackLocalCity seachCityInfo;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (intentCityCode == requestCode && resultCode == Activity.RESULT_OK) {
            seachCityInfo = (PackLocalCity) data.getSerializableExtra("city_info");
            if (seachCityInfo != null) {
                description_title_search.setText(seachCityInfo.NAME + "  本站降水量区间查询mm");
            }
        }
    }

    private final int intentCityCode = 1012;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.to_citylist:
                //跳转到城市选择列表；
//                Intent intentCityList = new Intent(getActivity(), ActivityCityListCountry.class);
//                intentCityList.putExtra("onlyFjCity", true);
//                startActivityForResult(intentCityList, intentCityCode);
                Intent intentCityList = new Intent(getActivity(), ActivityCityListCountry.class);
                intentCityList.putExtra("isSingleCityList", true);
                intentCityList.putExtra("add_city", false);
                startActivityForResult(intentCityList, intentCityCode);
                break;
            case R.id.livequery_start_date:
                // 开始 日
                createTimePopupWindow(livequery_start_date, start_date_data, start_date, listener)
                        .showAsDropDown(livequery_start_date);
                break;
            case R.id.livequery_start_time:
                // 开始 时
                activity.createPopupWindow(livequery_start_time, start_time_data, start_time, listener)
                        .showAsDropDown(livequery_start_time);
                break;
            case R.id.livequery_todata:
                // 到日
                createTimePopupWindow(livequery_todata, todata_data, todata, listener).showAsDropDown(livequery_todata);
                break;
            case R.id.livequery_totime:
                // 到时
                activity.createPopupWindow(livequery_totime, totime_data, totime, listener)
                        .showAsDropDown(livequery_totime);
                break;
            case R.id.livequery_city_spinner:
                // 城市选择
                activity.createPopupWindow(livequery_city_spinner, cityControl.getParentShowNameList(), city_spinner,
                        listener).showAsDropDown(livequery_city_spinner);
                break;
            case R.id.livequery_town_spinner:
                // 镇选择
                PackLocalCity city = cityControl.getCutParentCity();
//                if(!city.isFjCity || ZtqCityDB.getInstance().isServiceAccessible()) {
                    activity.createPopupWindow(livequery_town_spinner, cityControl.getChildShowNameList(), town_spinner,
                            listener).showAsDropDown(livequery_town_spinner);
//                }
                break;
            case R.id.livequery_search_btn:
                // 查询按钮
                // 时间转换
                searchEvent();
                break;
            case R.id.table_layout:
                // 显示数据还是显示图片
                if (isShowCompImage) {
                    view_desc.setText("对比图");
                    isShowCompImage = false;
                    table_data.setImageResource(R.drawable.icon_comp_view);
                    livequery_rainfall_complete.setVisibility(View.VISIBLE);
                    comp_layout.setVisibility(View.GONE);
                } else {
                    view_desc.setText("对比表");
                    isShowCompImage = true;
                    table_data.setImageResource(R.drawable.icon_livequery_table);
                    livequery_rainfall_complete.setVisibility(View.GONE);
                    comp_layout.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.time_search: {
                    //本时次雨量查询
                    Intent intenTimet = new Intent(activity, ActivityTimeSearch.class);
                    intenTimet.putExtra("town", cityControl.getCutChildCity());
                    intenTimet.putExtra("isbase", false);
                    startActivity(intenTimet);
                }
                break;
            case R.id.time_search_base: {
                    //本时次雨量查询
                    Intent intenTimet = new Intent(activity, ActivityTimeSearch.class);
                    intenTimet.putExtra("town", cityControl.getCutChildCity());
                    intenTimet.putExtra("isbase", true);
                    startActivity(intenTimet);
                }
                break;
            case R.id.btn_jyqddjhf:
                showPopWindowDataIntroduction();
                break;
        }
    }

    private void searchEvent() {
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
        for (int i = 0; i < start_date_data.size(); i++) {
            String showTimeStr = start_date_data.get(i);
            if (livequery_start_date.getText().toString().trim()
                    .equals(showTimeStr.substring(showTimeStr.indexOf("月") + 1, showTimeStr.indexOf("日")))) {
                startDateDayPosition = i;
            }
        }
        long st = startDate.get(startDateDayPosition);
        long et = endDate.get(endDateDayPosition);
        String startTimeStr = sf.format(new Date(st));
        String tostartTimeStr = sf.format(new Date(et));
        if (startDateTime.length() == 1) {
            startTimeStr += "0" + startDateTime;
        } else if (startDateTime.length() == 2) {
            startTimeStr += startDateTime;
        } else if (startDateTime.length() == 4 || startDateTime.length() == 5) {
            String[] value = startDateTime.split(":");
            String str = value[0];
            if (str.length() != 0) {
                if (str.length() == 1) {
                    startTimeStr += "0" + str;
                } else if (str.length() == 2) {
                    startTimeStr += str;
                } else {
                    startTimeStr += "00";
                }
            } else {
                startTimeStr += "00";
            }
        } else {
            startTimeStr += "00";
        }
        if (endDateTime.length() == 1) {
            tostartTimeStr += "0" + endDateTime;
        } else if (endDateTime.length() == 2) {
            tostartTimeStr += endDateTime;
        } else if (endDateTime.length() == 4 || endDateTime.length() == 5) {
            String[] value = endDateTime.split(":");
            String str = value[0];
            if (str.length() != 0) {
                if (str.length() == 1) {
                    tostartTimeStr += "0" + str;
                } else if (str.length() == 2) {
                    tostartTimeStr += str;
                } else {
                    tostartTimeStr += "00";
                }
            } else {
                startTimeStr += "00";
            }
        } else {
            tostartTimeStr += "00";
        }
        Date sdate = strToDateLong(startTimeStr);
        Date edate = strToDateLong(tostartTimeStr);
        if (sdate.compareTo(edate) > 0) {
            if (toast == null) {
                toast = Toast.makeText(getActivity(), "结束时间错误!", Toast.LENGTH_SHORT);
            } else {
                toast.setText("时间段选择错误");
            }
            toast.show();
        } else {
            if (searchDialog == null) {
                searchDialog = new DialogWaiting(getActivity(), "正在查询...");
            }
            if (!searchDialog.isShowing()) {
                searchDialog.show();
            }
            reqsearch(startTimeStr, tostartTimeStr);
        }
    }

    public Date strToDateLong(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHH");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }

    /**
     * 查询接口请求数据
     *
     * @param startTime
     * @param endTime
     */
    private void reqsearch(String startTime, String endTime) {
        if (!activity.isOpenNet()) {
            activity.showToast(getString(R.string.net_err));
            return;
        }
        searchpack = new PackYltjTimeUp();
        searchpack.area_id = seachCityInfo.ID;
        searchpack.beg_time = startTime;
        searchpack.end_time = endTime;
        PcsDataDownload.addDownload(searchpack);
    }

    private final DrowListClick listener = new DrowListClick() {
        @Override
        public void itemClick(int floag, int item) {
            switch (floag) {
                case start_date:
                    startDateDayPosition = item;
                    break;
                case start_time:
                    startDateTime = livequery_start_time.getText().toString().trim();
                    break;
                case todata:
                    endDateDayPosition = item;
                    break;
                case totime:
                    endDateTime = livequery_totime.getText().toString().trim();
                    break;
                case city_spinner:
                    cityControl.checkParent(item);
                    // 默认获取第一个数据
                    livequery_town_spinner.setText(cityControl.getCutChildCity().NAME);
                    reqDataTownSelect();
                    rainfalladatper.setClickposition(0);
                    baseRainfallAdatper.setClickposition(0);
                    redrawUI();
                    break;
                case town_spinner:
                    cityControl.checkChild(item);
                    reqDataTownSelect();
                    rainfalladatper.setClickposition(0);
                    baseRainfallAdatper.setClickposition(0);
                    redrawUI();
                    okHttpYltjRank();
                    break;
            }
        }

        /**
         * 城镇选择后获取信息
         */
        private void reqDataTownSelect() {
            activity.showProgressDialog();
            reflushListTitle();
            reqNet();
        }
    };

    private void redrawUI() {
        PackLocalCity currentParent = cityControl.getCutParentCity();
        if (currentParent != null) {
            if (currentParent.isFjCity/* && ZtqCityDB.getInstance().isServiceAccessible()*/) {
                if (CommonUtil.isHaveAuth("201040701")) {//是否有查看自动站权限
                    lay_is_tj.setVisibility(View.VISIBLE);
                    llHour24.setVisibility(View.VISIBLE);
                    layout_a.setVisibility(View.VISIBLE);
                    time_search.setVisibility(View.VISIBLE);
                    btnTimeSearchBase.setVisibility(View.VISIBLE);
                } else {
                    lay_is_tj.setVisibility(View.GONE);
                    llHour24.setVisibility(View.GONE);
                    layout_a.setVisibility(View.GONE);
                    time_search.setVisibility(View.GONE);
                    btnTimeSearchBase.setVisibility(View.GONE);
                }
            } else {
                lay_is_tj.setVisibility(View.GONE);
                llHour24.setVisibility(View.GONE);
                layout_a.setVisibility(View.GONE);
                time_search.setVisibility(View.GONE);
                btnTimeSearchBase.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 网络请求数据
     */
    private void reqNet() {
        okHttpRankYltjHour();
        okHttpYltjRank();
        okHttpHistoryMonth();
    }

    private class MyReceiver extends PcsDataBrocastReceiver {
        @Override
        public void onReceive(String name, String errorStr) {
            if (searchpack != null && name.equals(searchpack.getName())) {
                activity.dismissProgressDialog();
                searchpackdwon = (PackYltjTimeDown) PcsDataManager.getInstance().getNetPack(name);
                if (searchDialog.isShowing()) {
                    searchDialog.dismiss();
                }
                searchpackdwon = (PackYltjTimeDown) PcsDataManager.getInstance().getNetPack(name);
                if (searchpackdwon != null) {
                    Message msg = new Message();
                    Bundle bundler = new Bundle();
                    bundler.putString("tj_time", format(searchpackdwon.tj_time));
                    bundler.putString("jyzl", searchpackdwon.jyzl);
                    msg.setData(bundler);
                    handler.sendMessage(msg);
                }
            }
        }
    }

    private String[] getNumberList(String value) {
        List<String> list = new ArrayList<String>();
        Pattern p = Pattern.compile("\\d*");
        Matcher m = p.matcher(value);
        while (m.find()) {
            if (!"".equals(m.group())) {
                list.add(m.group());
            }
        }
        String[] strs = list.toArray(new String[list.size()]);
        return strs;
    }

    private String format(String value) {
        if (value == null || "".equals(value)) {
            return "";
        }
        String[] strs = getNumberList(value);
        if (strs == null || strs.length != 7) {
            return value;
        }
        return String.format("%s年%s月%s日%s:00到%s月%s日%s:00", strs[0], strs[1], strs[2], strs[3], strs[4], strs[5],
                strs[6]);
    }

    // 查询结果显示
    private final Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundler = msg.getData();

            View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_livequery_search, null);
            List<PackYltjTimeDown> resubltlist = new ArrayList<PackYltjTimeDown>();
            PackYltjTimeDown titledata = new PackYltjTimeDown();
            titledata.jyzl = "降水总量";
            titledata.tj_time = "降水量统计时段";
            resubltlist.add(titledata);
            PackYltjTimeDown a = new PackYltjTimeDown();
            a.jyzl = bundler.getString("jyzl");
            a.tj_time = bundler.getString("tj_time");
            resubltlist.add(a);

            AdapterRainSearchResult adapterResult = new AdapterRainSearchResult(getActivity(), resubltlist);
            MyListView lv = (MyListView) view.findViewById(R.id.mylistviw);
            lv.setAdapter(adapterResult);
            dialogSeachResult = new DialogOneButton(getActivity(), view, "知道了", new DialogListener() {
                @Override
                public void click(String str) {
                    dialogSeachResult.dismiss();
                }
            });
            dialogSeachResult.setTitle(seachCityInfo.NAME + "  本站降雨量区间查询");
            dialogSeachResult.show();
        }
    };

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

    private void reFlushImage(PackYltjYearDown yearDown) {
        // 雨量查询—地区半年降雨量对比（yltj_year）
        try {
            if (yearDown == null) {
                return;
            }
            halfayear_data_introduction.setText(yearDown.a_desc);
            //横线说明
            history_avg.setText(yearDown.yl_avg);
            history_max.setText(yearDown.yl_max);

            yltjYearList.clear();
            yltjYearList.addAll(yearDown.datalist);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
     * 字符转单精度
     *
     * @param str
     * @return
     */
    private float getfloat(String str) {
        if (str == null || "".equals(str)) {
            return CompleView.DATANull;
        }
        return Float.parseFloat(str);
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
            if (yltjYearList.get(i).year.equals("max_yaer")) {
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
                if (Float.parseFloat(yltjYearList.get(sorting.get(i)).year) > Float
                        .parseFloat(yltjYearList.get(sorting.get(j)).year)) {
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
                year_blue.setVisibility(View.GONE);
                year_darkblue.setText(" " + yltjYearList.get(sorting.get(0)).year + "年");
                year_green.setText(" " + yltjYearList.get(sorting.get(1)).year + "年");
                year_low_green.setText(" " + yltjYearList.get(sorting.get(2)).year + "年");
                year_blue.setText(" " + yltjYearList.get(sorting.get(3)).year + "年");
            } else if (sorting.size() == 3) {

                year_darkblue.setText(" " + yltjYearList.get(sorting.get(0)).year + "年");
                year_green.setText(" " + yltjYearList.get(sorting.get(1)).year + "年");
                year_low_green.setText(" " + yltjYearList.get(sorting.get(2)).year + "年");
                year_darkblue.setVisibility(View.VISIBLE);
                year_green.setVisibility(View.VISIBLE);
                year_low_green.setVisibility(View.VISIBLE);
                year_blue.setVisibility(View.GONE);
            } else if (sorting.size() == 2) {
                year_darkblue.setText(" " + yltjYearList.get(sorting.get(0)).year + "年");
                year_green.setText(" " + yltjYearList.get(sorting.get(1)).year + "年");

                year_darkblue.setVisibility(View.VISIBLE);
                year_green.setVisibility(View.VISIBLE);
                year_low_green.setVisibility(View.GONE);
                year_blue.setVisibility(View.GONE);
            } else if (sorting.size() == 1) {
//                    year_darkblue.setText(" " + yltjYearList.get(sorting.get(0)).year + "年月降雨量");
                year_darkblue.setText(" " + yltjYearList.get(sorting.get(0)).year + "年");
                year_darkblue.setVisibility(View.VISIBLE);
                year_green.setVisibility(View.GONE);
                year_low_green.setVisibility(View.GONE);
                year_blue.setVisibility(View.GONE);
            } else {
                year_darkblue.setVisibility(View.GONE);
                year_green.setVisibility(View.GONE);
                year_low_green.setVisibility(View.GONE);
                year_blue.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private PopupWindow popDwon;

    private void showPopWindowDataIntroduction() {
        if (popDwon == null) {
            popDwon = new PopupWindow(getActivity());
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.pop_rain_fj, null);
            popDwon.setContentView(view);
            ImageView btn_delete = (ImageView) view.findViewById(R.id.btn_delete);
//            popDwon.setWidth(LayoutParams.WRAP_CONTENT);
//            popDwon.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
            popDwon.setFocusable(true);
//            popDwon.setBackgroundDrawable(new BitmapDrawable());
            popDwon.setOutsideTouchable(false);
            int hight = getActivity().getWindowManager().getDefaultDisplay().getHeight();
            int width = getActivity().getWindowManager().getDefaultDisplay().getWidth();
            popDwon.setHeight(LayoutParams.WRAP_CONTENT);
            popDwon.setWidth((width / 10) * 9);
            btn_delete.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (popDwon != null && popDwon.isShowing()) {
                        popDwon.dismiss();
                    }
                }
            });
        }
        TextView textView = (TextView) popDwon.getContentView().findViewById(R.id.text_info);
        if (hourDown != null) {
            String str = hourDown.a_desc;
            if (!TextUtils.isEmpty(str)) {
                str = str.replace("统计说明：\n", "");
                textView.setText(str);
            } else {
                textView.setText("统计说明：");
            }
        }
        if (!popDwon.isShowing()) {
            popDwon.showAtLocation(btn_jyqddjhf, Gravity.CENTER, 0, 0);
        }
    }

    /**
     * 自动站、国家站雨量统计mm
     */
    private void okHttpRankYltjHour() {
        activity.showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    JSONObject info = new JSONObject();
                    info.put("stationId", cityControl.getCutChildCity().ID);
                    if (CommonUtil.isHaveAuth("201040701")) {//是否有查看自动站权限
                        info.put("isAuto", true);
                    } else {
                        info.put("isAuto", false);
                    }
                    param.put("paramInfo", info);
                    String json = param.toString();
                    Log.e("datastatis_rain", json);
                    String url = CONST.BASE_URL+"datastatis_rain";
                    Log.e("datastatis_rain", url);
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
                                    Log.e("datastatis_rain", result);
                                    if (!TextUtil.isEmpty(result)) {
                                        try {
                                            JSONObject obj = new JSONObject(result);
                                            if (!obj.isNull("b")) {
                                                JSONObject bobj = obj.getJSONObject("b");
                                                if (!bobj.isNull("yltj_hour")) {
                                                    JSONObject yltj_hour = bobj.getJSONObject("yltj_hour");
                                                    if (!TextUtil.isEmpty(yltj_hour.toString())) {
                                                        activity.dismissProgressDialog();
                                                        hourDown = new PackYltjHourDown();
                                                        hourDown.fillData(yltj_hour.toString());
                                                        autoRainFall.clear();
                                                        autoRainFall.add(titleauto);
                                                        baseRainFall.clear();
                                                        baseRainFall.add(titleauto);
                                                        if (hourDown != null) {
                                                            autoRainFall.addAll(hourDown.dataList);
                                                            baseRainFall.addAll(hourDown.baseList);
                                                        }
                                                        reRank(1, autoRainFall, rainfalladatper);
                                                        reRank(1, baseRainFall, baseRainfallAdatper);
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
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void reRank(final int clickPosition, List<RainFall> list, AdatperAutoRainFall adapter) {
        if (list.size() <= 1) {
            return;
        }
        Collections.sort(list, new Comparator<PackYltjHourDown.RainFall>() {
            @Override
            public int compare(PackYltjHourDown.RainFall arg0, PackYltjHourDown.RainFall arg1) {
                String value0 = null,value1 = null;
                if (clickPosition == 1) {
                    value0 = arg0.hour1;
                    value1 = arg1.hour1;
                } else if (clickPosition == 2) {
                    value0 = arg0.hour3;
                    value1 = arg1.hour3;
                } else if (clickPosition == 3) {
                    value0 = arg0.hour6;
                    value1 = arg1.hour6;
                } else if (clickPosition == 4) {
                    value0 = arg0.hour12;
                    value1 = arg1.hour12;
                } else if (clickPosition == 5) {
                    value0 = arg0.hour24;
                    value1 = arg1.hour24;
                }
                if (TextUtils.isEmpty(value0) || TextUtils.isEmpty(value1)) {
                    return -1;
                }
                if (value0.contains("小时")) {
                    return -1;
                }
                if (value1.contains("小时")) {
                    return 1;
                }
                float a = Float.parseFloat(value0);
                float b = Float.parseFloat(value1);
                if (a > b) {
                    return -1;
                }
                if (a < b) {
                    return 1;
                }
                return 0;
            }
        });
        adapter.notifyDataSetChanged();
    }

    /**
     * 24小时内任意1、3小时最大雨量排名
     */
    private void okHttpYltjRank() {
        activity.showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    JSONObject info = new JSONObject();
                    info.put("stationId", cityControl.getCutChildCity().ID);
                    param.put("paramInfo", info);
                    String json = param.toString();
                    Log.e("datastatis_rain1h3h", json);
                    final String url = CONST.BASE_URL+"datastatis_rain1h3h";
                    Log.e("datastatis_rain1h3h", url);
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
                                    Log.e("datastatis_rain1h3h", result);
                                    if (!TextUtil.isEmpty(result)) {
                                        try {
                                            JSONObject obj = new JSONObject(result);
                                            if (!obj.isNull("b")) {
                                                JSONObject bobj = obj.getJSONObject("b");
                                                if (!bobj.isNull("yltj_rank")) {
                                                    JSONObject yltj_rank = bobj.getJSONObject("yltj_rank");
                                                    if (!TextUtil.isEmpty(yltj_rank.toString())) {
                                                        PackYltjRankDown rankDown = new PackYltjRankDown();
                                                        rankDown.fillData(yltj_rank.toString());
                                                        rankRainFall.clear();
                                                        rankRainFall.add(titleRank);
                                                        if (rankDown != null || rankDown.dataList.size() != 0) {
                                                            rankRainFall.addAll(rankDown.dataList);
                                                        }
                                                        rainfallMaxadatper.notifyDataSetChanged();
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
                    info.put("stationId", cityControl.getCutChildCity().ID);
                    info.put("element", "pre");
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
                                        try {
                                            JSONObject obj = new JSONObject(result);
                                            if (!obj.isNull("b")) {
                                                JSONObject bobj = obj.getJSONObject("b");
                                                if (!bobj.isNull("yltj_year")) {
                                                    JSONObject yltj_year = bobj.getJSONObject("yltj_year");
                                                    if (!TextUtil.isEmpty(yltj_year.toString())) {
                                                        PackYltjYearDown yearDown = new PackYltjYearDown();
                                                        yearDown.fillData(yltj_year.toString());
                                                        reFlushImage(yearDown);
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
