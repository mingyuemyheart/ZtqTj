package com.pcs.ztqtj.view.fragment.livequery.all_country;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.livequery.AdapterTempertureHight;
import com.pcs.ztqtj.control.adapter.livequery.AdapterTempertureLow;
import com.pcs.ztqtj.control.adapter.livequery.AdapterWind;
import com.pcs.ztqtj.view.activity.livequery.ActivityLiveQuery;
import com.pcs.ztqtj.view.activity.livequery.ActivityLiveQueryDetail;
import com.pcs.ztqtj.view.activity.livequery.LiveQueryPopupWindowTool;
import com.pcs.ztqtj.view.fragment.livequery.FragmentLiveQueryCommon;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackFltjProDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackFltjProUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackFltjZdDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackWdtjLowZdzDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackWdtjProLowDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackWdtjProLowUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackWdtjProMaxDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackWdtjProMaxUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackWdtjZdzDown;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.pcs.ztqtj.R.id.rb_hours;

/**
 * Created by Z on 2017/6/6.
 */

public class FragmentTempHightCountry extends FragmentLiveQueryCommon {
    private ActivityLiveQuery activity;
    private int currentHourPosition = 0;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (ActivityLiveQuery) activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_detail_wind, null);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        PcsDataBrocastReceiver.registerReceiver(getActivity(), receiver);
        initType();
        initView();
        initData();
        initEvent();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(receiver);
    }

    private ListView livequery_auto;
    private ListView livequery_24_hour;
    private TextView description_title_search2, description_title_search3;
    private RadioGroup radiogroup;
    // 时间段控件
    private RadioGroup rgHours;
    private RadioButton rb24h, rbHours;

    public String whatType;

    private int whatColumn = -1;
    // 风速
    private List<PackFltjZdDown.FltjZd> windAutoList;
    private List<PackFltjZdDown.FltjZd> windcurrentList;
    private AdapterWind windAutoAtper;
    private AdapterWind windCurrentAtper;

    // 低温
    private List<PackWdtjLowZdzDown.WdtjLowZdz> lowAutoTemper;
    private List<PackWdtjLowZdzDown.WdtjLowZdz> lowCurrentTemper;
    private AdapterTempertureLow lowAutoListViewAdatper;
    private AdapterTempertureLow lowCurrentListViewAdatper;

    // 高温
    private List<PackWdtjZdzDown.WdtjZdz> hightAutoTemper;
    private List<PackWdtjZdzDown.WdtjZdz> hightCurrentTemper;
    private AdapterTempertureHight hightAutoListViewAdatper;
    private AdapterTempertureHight hightCurrentListViewAdatper;
    private boolean isShowPopupWindow = false;

    private PackWdtjProLowUp packWdtjProLowUp = new PackWdtjProLowUp();
    private PackWdtjProMaxUp packWdtjProMaxUp = new PackWdtjProMaxUp();
    private PackFltjProUp packFltjProUp = new PackFltjProUp();

    private void initView() {
        livequery_auto = (ListView) getActivity().findViewById(R.id.livequery_auto_min_table);
        livequery_24_hour = (ListView) getActivity().findViewById(R.id.livequery_day_min_table);
        description_title_search2 = (TextView) getActivity().findViewById(R.id.description_title_search2);
        description_title_search3 = (TextView) getActivity().findViewById(R.id.description_title_search3);
        radiogroup = (RadioGroup) getView().findViewById(R.id.lowtemradiogroup);
        rgHours = (RadioGroup) getView().findViewById(R.id.group_hours);
        rb24h = (RadioButton) getView().findViewById(R.id.rb_24h);
        rbHours = (RadioButton) getView().findViewById(R.id.rb_hours);
        initButton();
    }

    private void initButton() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if(whatType.equals("hight_t")) {
            RadioButton button = (RadioButton) getView().findViewById(R.id.lowtemradiogroupleft);
            button.setText("高温实况值");
            button = (RadioButton) getView().findViewById(R.id.lowtemradiogroupright);
            button.setText("24小时内最高");
            button = (RadioButton) getView().findViewById(R.id.rb_24h);
            button.setOnClickListener(onRBClickListener);
            button.setText("近24小时最高");
            rbHours.setOnClickListener(onRBClickListener);
            setHours(true);
        } else if(whatType.equals("low_t")) {
            RadioButton button = (RadioButton) getView().findViewById(R.id.lowtemradiogroupleft);
            button.setText("低温实况值");
            button = (RadioButton) getView().findViewById(R.id.lowtemradiogroupright);
            button.setText("24小时内最低");
            button = (RadioButton) getView().findViewById(R.id.rb_24h);
            button.setOnClickListener(onRBClickListener);
            button.setText("近24小时最低");
            rbHours.setOnClickListener(onRBClickListener);
            setHours(false);
        }

    }

    protected void initType() {
        whatType = "hight_t";
    }

    private void initData() {
        if(!whatType.equals("wind")) {
            radiogroup.setVisibility(View.VISIBLE);
            clickLeft();
        } else {
            radiogroup.setVisibility(View.GONE);
        }
        if (whatType.equals("wind")) {
            initDataWind();
        } else if (whatType.equals("hight_t")) {
            initDataHightTemper();
        } else if (whatType.equals("low_t")) {
            initDataLowTemper();
        }
    }

    private void setHours(boolean isHigh) {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if(isHigh) {
            if (hour == 0) {
                rbHours.setText("今日0时最高▼");
            } else {
                rbHours.setText("今日" + currentHourPosition + "时至" + hour + "时最高▼");
            }
        } else {
            if (hour == 0) {
                rbHours.setText("今日0时最低▼");
            } else {
                rbHours.setText("今日" + currentHourPosition + "时至" + hour + "时最低▼");
            }
        }
    }

    private void setHour() {
        if(whatType.equals("hight_t")) {
            setHours(true);
        } else if(whatType.equals("low_t")) {
            setHours(false);
        }
    }

    private void setTempDesc() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if(whatType.equals("hight_t")) {
            if (hour == 0) {
                description_title_search3.setText("全省今日0时最高气温统计表");
            } else {
                description_title_search3.setText("全省今日" + currentHourPosition + "时至" + hour + "时最高气温统计表");
            }
        } else if(whatType.equals("low_t")) {
            if (hour == 0) {
                description_title_search3.setText("全省今日0时最低气温统计表");
            } else {
                description_title_search3.setText("全省今日" + currentHourPosition + "时至" + hour + "时最低气温统计表");
            }
        }
    }


    private void updateLowTemperData() {
        activity.dismissProgressDialog();
        PackWdtjLowZdzDown.WdtjLowZdz titletemp = new PackWdtjLowZdzDown().new WdtjLowZdz();
        titletemp.county = "站点";
        titletemp.time = "日期/时段";
        titletemp.min_wd = "气温°C";
        lowAutoTemper.add(titletemp);
        lowCurrentTemper.add(titletemp);

        packWdtjProLowUp.type = "1";
        PackWdtjProLowDown wdtjProLowDown = (PackWdtjProLowDown) PcsDataManager.getInstance().getNetPack(packWdtjProLowUp.getName());
        if (wdtjProLowDown != null) {
            lowAutoTemper.clear();
            lowAutoTemper.add(titletemp);
            lowAutoTemper.addAll(wdtjProLowDown.datalist);
        }

        packWdtjProLowUp.type = "2";
        PackWdtjProLowDown wdtjProLow24Down = (PackWdtjProLowDown) PcsDataManager.getInstance().getNetPack(packWdtjProLowUp.getName());
        if (null != wdtjProLow24Down) {
            activity.dismissProgressDialog();
            lowCurrentTemper.clear();
            lowCurrentTemper.add(titletemp);
            lowCurrentTemper.addAll(wdtjProLow24Down.datalist);
        }
        lowAutoListViewAdatper.notifyDataSetChanged();
        lowCurrentListViewAdatper.notifyDataSetChanged();
    }

    /**
     * 低温详情
     */
    private void initDataLowTemper() {
        description_title_search2.setText("全省实况气温最低统计表");
        description_title_search3.setText("全省近24小时最低统计表");
        lowAutoTemper = new ArrayList<>();
        lowCurrentTemper = new ArrayList<>();
        lowAutoListViewAdatper = new AdapterTempertureLow(getActivity(), lowAutoTemper);
        lowCurrentListViewAdatper = new AdapterTempertureLow(getActivity(), lowCurrentTemper);
        livequery_auto.setAdapter(lowAutoListViewAdatper);
        livequery_24_hour.setAdapter(lowCurrentListViewAdatper);
        request(3);
    }

    /**
     * 最高温详情
     */
    private void initDataHightTemper() {
        description_title_search2.setText("全省高温实况统计表");
        description_title_search3.setText("全省近24小时最高气温统计表");
        hightAutoTemper = new ArrayList<>();
        hightCurrentTemper = new ArrayList<>();
        hightAutoListViewAdatper = new AdapterTempertureHight(getActivity(), hightAutoTemper);
        hightCurrentListViewAdatper = new AdapterTempertureHight(getActivity(), hightCurrentTemper);
        livequery_auto.setAdapter(hightAutoListViewAdatper);
        livequery_24_hour.setAdapter(hightCurrentListViewAdatper);
        request(2);
    }

    /**
     * 风速详情
     */
    private void initDataWind() {
        description_title_search2.setText("全省站点当前瞬时风速排名");
        description_title_search3.setText("全省站点24小时极大风速统计");
        windAutoList = new ArrayList<>();
        windcurrentList = new ArrayList<>();
        windAutoAtper = new AdapterWind(getActivity(), windAutoList);
        windCurrentAtper = new AdapterWind(getActivity(), windcurrentList);
        livequery_auto.setAdapter(windAutoAtper);
        livequery_24_hour.setAdapter(windCurrentAtper);
        request(4);
    }

    private void updateHightTemperData() {
        PackWdtjZdzDown.WdtjZdz titleMaxRain = new PackWdtjZdzDown().new WdtjZdz();
        titleMaxRain.county = "站点";
        titleMaxRain.time = "日期/时段";
        titleMaxRain.max_wd = "气温℃";
        hightAutoTemper.add(titleMaxRain);
        hightCurrentTemper.add(titleMaxRain);
        packWdtjProMaxUp.type = "1";
        PackWdtjProMaxDown wdtjProMaxDown = (PackWdtjProMaxDown) PcsDataManager.getInstance().getNetPack(packWdtjProMaxUp.getName());
        activity.dismissProgressDialog();
        if (null != wdtjProMaxDown) {
            hightAutoTemper.clear();
            hightAutoTemper.add(titleMaxRain);
            hightAutoTemper.addAll(wdtjProMaxDown.datalist);
        }

        packWdtjProMaxUp.type = "2";
        PackWdtjProMaxDown wdtjProMax24Down = (PackWdtjProMaxDown) PcsDataManager.getInstance().getNetPack(packWdtjProMaxUp.getName());
        if (null != wdtjProMax24Down) {
            hightCurrentTemper.clear();
            hightCurrentTemper.add(titleMaxRain);
            hightCurrentTemper.addAll(wdtjProMax24Down.datalist);
        }

        hightAutoListViewAdatper.notifyDataSetChanged();
        hightCurrentListViewAdatper.notifyDataSetChanged();
    }

    private void updateHourLowTempData(List<PackWdtjLowZdzDown.WdtjLowZdz> datalist) {
        PackWdtjLowZdzDown.WdtjLowZdz titletemp = new PackWdtjLowZdzDown().new WdtjLowZdz();
        titletemp.county = "站点";
        titletemp.time = "日期/时段";
        titletemp.min_wd = "气温°C";
        lowCurrentTemper.clear();
        lowCurrentTemper.add(titletemp);
        lowCurrentTemper.addAll(datalist);
        lowCurrentListViewAdatper.notifyDataSetChanged();
    }

    private void updateHourHighTempData(List<PackWdtjZdzDown.WdtjZdz> datalist) {
        PackWdtjZdzDown.WdtjZdz titleMaxRain = new PackWdtjZdzDown().new WdtjZdz();
        titleMaxRain.county = "站点";
        titleMaxRain.time = "日期/时段";
        titleMaxRain.max_wd = "气温℃";
        hightCurrentTemper.clear();
        hightCurrentTemper.add(titleMaxRain);
        hightCurrentTemper.addAll(datalist);
        hightCurrentListViewAdatper.notifyDataSetChanged();
    }


    private void upWindData() {
        // 列表头部说明
        activity.dismissProgressDialog();
        PackFltjZdDown.FltjZd titleMaxRain = new PackFltjZdDown().new FltjZd();
        titleMaxRain.county = "站点";
        titleMaxRain.time = "日期/时段";
        titleMaxRain.winddirection = "风向";
        titleMaxRain.windFengLi = "风力";
        titleMaxRain.windpower = "风速m/s";
        windAutoList.add(titleMaxRain);
        windcurrentList.add(titleMaxRain);
        // 风况查询—省级排名 前9名 24小时


        packFltjProUp.type = "1";
        PackFltjProDown fltjProDown = (PackFltjProDown) PcsDataManager.getInstance().getNetPack(packFltjProUp.getName());
        if (null != fltjProDown) {
            windAutoList.clear();
            windAutoList.add(titleMaxRain);
            windAutoList.addAll(fltjProDown.datalist);
        }

        packFltjProUp.type = "2";
        PackFltjProDown fltjPro24Down = (PackFltjProDown) PcsDataManager.getInstance().getNetPack(packFltjProUp.getName());
        if (null != fltjPro24Down) {
            windcurrentList.clear();
            windcurrentList.add(titleMaxRain);
            windcurrentList.addAll(fltjPro24Down.datalist);
        }

        windAutoAtper.notifyDataSetChanged();
        windCurrentAtper.notifyDataSetChanged();
    }

    private void initEvent() {
        livequery_auto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                } else {
                    Intent intent = new Intent(getActivity(), ActivityLiveQueryDetail.class);
                    String stationName = "";
                    if (whatType.equals("wind")) {
                        PackFltjZdDown.FltjZd bean = windAutoList.get(position);
                        stationName = bean.county;
                        intent.putExtra("item", "wind");
                    } else if (whatType.equals("hight_t")) {
                        PackWdtjZdzDown.WdtjZdz bean = hightAutoTemper.get(position);
                        stationName = bean.county;
                        intent.putExtra("item", "temp");
                    } else if (whatType.equals("low_t")) {
                        PackWdtjLowZdzDown.WdtjLowZdz bean = lowAutoTemper.get(position);
                        stationName = bean.county;
                        intent.putExtra("item", "temp");
                    }
                    if (stationName.equals("全部")) {
                        return;
                    }
                    intent.putExtra("stationName", stationName);
                    startActivity(intent);
                }
            }
        });


        livequery_24_hour.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                } else {
                    Intent intent = new Intent(getActivity(), ActivityLiveQueryDetail.class);
                    String stationName = "";
                    if (whatType.equals("wind")) {
                        PackFltjZdDown.FltjZd bean = windcurrentList.get(position);
                        stationName = bean.county;
                        intent.putExtra("item", "wind");
                    } else if (whatType.equals("hight_t")) {

                        PackWdtjZdzDown.WdtjZdz bean = hightCurrentTemper.get(position);
                        stationName = bean.county;
                        intent.putExtra("item", "temp");
                    } else if (whatType.equals("low_t")) {
                        PackWdtjLowZdzDown.WdtjLowZdz bean = lowCurrentTemper.get(position);
                        stationName = bean.county;
                        intent.putExtra("item", "temp");
                    }
                    if (stationName.equals("全部")) {
                        return;
                    }
                    intent.putExtra("stationName", stationName);
                    startActivity(intent);
                }
            }
        });

        radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.lowtemradiogroupleft:
                        clickLeft();
                        break;
                    case R.id.lowtemradiogroupright:
                        clickRight();
                        break;
                }
            }
        });

        rgHours.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                isShowPopupWindow = false;
                switch (checkedId) {
                    case R.id.rb_24h:
                        if(whatType.equals("hight_t")) { // 高温
                            description_title_search3.setText("全省近24小时最高统计表");
                        } else if(whatType.equals("low_t")) { // 低温
                            description_title_search3.setText("全省近24小时最低统计表");
                        }
                        break;
                    case rb_hours:
                        setTempDesc();
                        break;
                }
            }
        });
    }

    private View.OnClickListener onRBClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.rb_24h:
                    if(whatType.equals("hight_t")) { // 高温
                        request(2);
                    } else if(whatType.equals("low_t")) { // 低温
                        request(3);
                    }
                    break;
                case rb_hours:
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
                        reqHour(currentHourPosition);
                        if(whatType.equals("hight_t")) {
                            setHours(true);
                        } else if(whatType.equals("low_t")) {
                            setHours(false);
                        }
                    }
                    isShowPopupWindow = true;
                    break;
            }
        }
    };

    private LiveQueryPopupWindowTool.OnPopupWindowItemClickListener popupWindowItemClickListener = new LiveQueryPopupWindowTool.OnPopupWindowItemClickListener() {

        @Override
        public void onClick(int position) {
            currentHourPosition = position;
            reqHour(position);
            int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            setHour();
            setTempDesc();
        }
    };

    /**
     * 创建今日时间列表
     * @return
     */
    private List<String> createHoursList() {
        List<String> list = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        if(hourOfDay == 0) {

        } else {
            for (int i = 0; i < hourOfDay; i++) {
                String str = "今日" + i + "时至" + hourOfDay + "时";
                list.add(str);
            }
        }
        return list;
    }

    private void clickLeft() {
        setTempListVisibility(true);
        set24HoursGroupVisibility(false);
    }

    private void clickRight() {
        setTempListVisibility(false);
        set24HoursGroupVisibility(true);
        rgHours.check(R.id.rb_24h);
        currentHourPosition = 0;
        setHour();
    }

    /**
     * 设置24小时按钮是否显示
     * @param visibility
     */
    private void set24HoursGroupVisibility(boolean visibility) {
        if(visibility) {
            rgHours.setVisibility(View.VISIBLE);
            RadioButton button = (RadioButton) getView().findViewById(R.id.rb_24h);
            if(button != null) {
                button.performClick();
            }
        } else {
            rgHours.setVisibility(View.GONE);
        }
    }

    private void setTempListVisibility(boolean visibility) {
        if(visibility) {
            description_title_search2.setVisibility(View.VISIBLE);
            livequery_auto.setVisibility(View.VISIBLE);
            description_title_search3.setVisibility(View.GONE);
            livequery_24_hour.setVisibility(View.GONE);
        } else {
            description_title_search2.setVisibility(View.GONE);
            livequery_auto.setVisibility(View.GONE);
            description_title_search3.setVisibility(View.VISIBLE);
            livequery_24_hour.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void refleshData() {
        if (whatType.equals("wind")) {
            request(4);
        } else if (whatType.equals("hight_t")) {
            request(2);
        } else if (whatType.equals("low_t")) {
            request(3);
        }
    }


    /**
     * 请求数据，
     *
     * @param message 4为风速 3为低温 2为高温
     */
    private void request(int message) {
        if (!activity.isOpenNet()) {
            activity.showToast(getString(R.string.net_err));
            return;
        }
        activity.showProgressDialog();
        whatColumn = message;
        switch (message) {
            case 4:
//			风况
                PackFltjProUp fltjPro24Up = new PackFltjProUp();
                // 风况查询—省级排名 前9名 24小时
                fltjPro24Up = new PackFltjProUp();
                fltjPro24Up.type = "2";
                PcsDataDownload.addDownload(fltjPro24Up);
                PackFltjProUp fltjProUp;// 风况查询—省级排名 前9名
                // 风况查询—省级排名 前9名
                fltjProUp = new PackFltjProUp();
                fltjProUp.type = "1";
                PcsDataDownload.addDownload(fltjProUp);
                break;
            case 3:
//			低温
                PackWdtjProLowUp wdtjProLowUp = new PackWdtjProLowUp();
                wdtjProLowUp.type = "1";
                PcsDataDownload.addDownload(wdtjProLowUp);
                PackWdtjProLowUp wdtjProLow24Up = new PackWdtjProLowUp();
                wdtjProLow24Up.type = "2";
                PcsDataDownload.addDownload(wdtjProLow24Up);
                break;
            case 2:
//			高温
                PackWdtjProMaxUp wdtjProUp = new PackWdtjProMaxUp();
                wdtjProUp.type = "1";
                PcsDataDownload.addDownload(wdtjProUp);
                PackWdtjProMaxUp wdtjPro24Up = new PackWdtjProMaxUp();
                wdtjPro24Up.type = "2";
                PcsDataDownload.addDownload(wdtjPro24Up);
                break;
        }
    }

    private void reqHour(int position) {
        if (whatType.equals("hight_t")) {
            PackWdtjProMaxUp packUp = new PackWdtjProMaxUp();
            packUp.type = "3";
            packUp.s_hour = String.valueOf(position);
            PcsDataDownload.addDownload(packUp);
        } else if (whatType.equals("low_t")) {
            PackWdtjProLowUp packUp = new PackWdtjProLowUp();
            packUp.type = "3";
            packUp.s_hour = String.valueOf(position);
            PcsDataDownload.addDownload(packUp);
        }
    }


    private PcsDataBrocastReceiver receiver = new PcsDataBrocastReceiver() {
        @Override
        public void onReceive(String name, String errorStr) {
//            switch (whatColumn) {
//                case 4:
//                    upWindData();
//                    whatColumn = -1;
//                    break;
//                case 3:
//                    updateLowTemperData();
//                    whatColumn = -1;
//                    break;
//                case 2:
//                    updateHightTemperData();
//                    whatColumn = -1;
//                    break;
//            }

            if(name.startsWith(PackWdtjProLowUp.NAME + "#1")
                    || name.startsWith(PackWdtjProLowUp.NAME + "#2")) {
                updateLowTemperData();
                whatColumn = -1;
            } else if(name.startsWith(PackWdtjProMaxUp.NAME + "#1")
                    || name.startsWith(PackWdtjProMaxUp.NAME + "#2")) {
                updateHightTemperData();
                whatColumn = -1;
            } else if (name.startsWith(PackFltjProUp.NAME + "#1")
                    || name.startsWith(PackFltjProUp.NAME + "#2")) {
                upWindData();
                whatColumn = -1;
            } else if (name.startsWith(PackWdtjProLowUp.NAME + "#3")) {
                PackWdtjProLowDown down = (PackWdtjProLowDown) PcsDataManager.getInstance().getNetPack(name);
                if(down == null) {
                    return ;
                }
                updateHourLowTempData(down.datalist);
            } else if (name.startsWith(PackWdtjProMaxUp.NAME + "#3")) {
                PackWdtjProMaxDown down = (PackWdtjProMaxDown) PcsDataManager.getInstance().getNetPack(name);
                if(down == null) {
                    return ;
                }
                updateHourHighTempData(down.datalist);
            }
        }
    };
}
