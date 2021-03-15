package com.pcs.ztqtj.view.activity.livequery;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.pcs.lib.lib_pcs_v3.control.tool.Util;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCity;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.ItemRainNow;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackRainstatHourTimeDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackRainstatHourTimeUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackRainstatNewDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackRainstatNewUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackRainstatNowDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackRainstatNowUp;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.livequery.AdapterData;
import com.pcs.ztqtj.control.adapter.livequery.AdatperRainNowFall;
import com.pcs.ztqtj.control.inter.DrowListClick;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
import com.pcs.ztqtj.view.fragment.livequery.fujian_city.CityListControl;
import com.pcs.ztqtj.view.myview.MyListView;
import com.pcs.ztqtj.view.myview.TimeSelector;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Z on 2016/6/16.
 * 本时次雨量查询
 */
public class ActivityTimeSearch extends FragmentActivityZtqBase implements View.OnClickListener {

    private TextView data_desc;
    private TextView livequery_city_spinner;
    private TextView livequery_town_spinner;
    private MyListView livequery_town;
    private MyListView description_city;

    private TextView description_title_town;
    private TextView description_title_city;

    private AdatperRainNowFall adatperTown;

    private List<ItemRainNow> baseAutoList = new ArrayList<>();
    private PackRainstatNewUp packRainstatNewUp = new PackRainstatNewUp();
    private TextView livequery_begintime, livequery_endtime;
    private Button livequery_search_btn;
    private int begin_num = 0;
    private String datatype = "0";
    private PackRainstatHourTimeUp packRainstatHourTimeUp = new PackRainstatHourTimeUp();
    private boolean isBase = false;
    private TimeSelector timeSelector_begin, timeSelector_end;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitleText("任意时段查询");
        setContentView(R.layout.activity_time_search);
        PcsDataBrocastReceiver.registerReceiver(this, receiver);
        initView();
        initData();
        initEvent();
    }


    private void initView() {
        livequery_city_spinner = (TextView) findViewById(R.id.livequery_city_spinner);
        data_desc = (TextView) findViewById(R.id.data_desc);
        livequery_town_spinner = (TextView) findViewById(R.id.livequery_town_spinner);
        livequery_town = (MyListView) findViewById(R.id.livequery_town);
        description_city = (MyListView) findViewById(R.id.description_city);
        description_title_town = (TextView) findViewById(R.id.description_title_low_on);
        description_title_city = (TextView) findViewById(R.id.description_title_low_sc);
        livequery_begintime = (TextView) findViewById(R.id.livequery_begintime);
        livequery_endtime = (TextView) findViewById(R.id.livequery_endtime);
        livequery_search_btn = (Button) findViewById(R.id.livequery_search_btn);
    }

    private void setListViewTitle(String town, String city) {
        if (isBase) {
            description_title_town.setText("国家站本时次雨量统计（mm）");
            description_title_city.setText("国家站本时次雨量最大排名（mm）");
        } else {
            description_title_town.setText("自动站本时次雨量统计（mm）");
            description_title_city.setText("自动站本时次雨量最大排名（mm）");
        }
    }

    private ItemRainNow listTitle;

    private CityListControl cityControl;

    private void initData() {
        isBase = getIntent().getBooleanExtra("isbase", false);
        listTitle = new ItemRainNow();
        listTitle.stat_name = "站点";
        listTitle.rainfall = "雨量mm";
        listTitle.time = "时间";
        baseAutoList.add(listTitle);
        adatperTown = new AdatperRainNowFall(baseAutoList);
        livequery_town.setAdapter(adatperTown);
        Intent intent = getIntent();
        PackLocalCity userCity = (PackLocalCity) intent.getSerializableExtra("town");
        cityControl = new CityListControl(userCity, true);

        livequery_town_spinner.setText(cityControl.getCutChildCity().NAME);
        if (cityControl.getCutChildCity().NAME.equals("天津")) {
            datatype = "2";
        } else if (cityControl.getCutChildCity().NAME.equals("天津市区")) {
            datatype = "1";
        } else {
            datatype = "0";
        }
        livequery_city_spinner.setText(cityControl.getCutParentCity().NAME);
        setListViewTitle(cityControl.getCutChildCity().NAME, cityControl.getCutParentCity().NAME);
        reqData();

    }


    private void initEvent() {
        //livequery_city_spinner.setOnClickListener(this);
        setBtnRight2(R.drawable.btn_refresh, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                baseAutoList.clear();
                adatperTown.notifyDataSetChanged();
                initData();
            }
        });
        livequery_town_spinner.setOnClickListener(this);
        livequery_begintime.setOnClickListener(this);
        livequery_endtime.setOnClickListener(this);
        livequery_search_btn.setOnClickListener(this);

        livequery_town.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    toDetail(baseAutoList.get(position).stat_name);
                }

            }
        });
    }


    private void toDetail(String stationName) {
        if (stationName.equals("全部")) {
            return;
        }
        Intent intent = new Intent(this, ActivityLiveQueryDetail.class);
        intent.putExtra("stationName", stationName);
        intent.putExtra("item", "rain");
        startActivity(intent);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.livequery_city_spinner:
                // 省级列表选择
                createPopupWindow(livequery_city_spinner, cityControl.getParentShowNameList(), 0, listener)
                        .showAsDropDown(livequery_city_spinner);
                break;
            case R.id.livequery_town_spinner:
                // 城镇列表
                createPopupWindow(livequery_town_spinner, cityControl.getChildShowNameList(), 1, listener)
                        .showAsDropDown(livequery_town_spinner);
                break;
            case R.id.livequery_begintime:

                timeSelector_begin.setTime( start_live);
                timeSelector_begin.show();
//                createPopupWindow(livequery_begintime, getTimes(0), 2, listener)
//                        .showAsDropDown(livequery_begintime);
                break;
            case R.id.livequery_endtime:

                timeSelector_end.setTime( end_live);
                timeSelector_end.show();
//                createPopupWindow(livequery_endtime, getTimes(1), 3, listener)
//                        .showAsDropDown(livequery_endtime);
                break;
            case R.id.livequery_search_btn:
                reqTime();
                break;
        }
    }


    private void reqTime() {
        String st_begin = null, st_end = null;

        SimpleDateFormat format_1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat format_2 = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分");
        try {
            st_begin = format_1.format(format_2.parse("2019年" + livequery_begintime.getText().toString()));
            st_end = format_1.format(format_2.parse("2019年" + livequery_endtime.getText().toString()));
        } catch (ParseException e) {
            e.printStackTrace();
        }


        if (timeCompare(st_begin, st_end) == 1) {
            Toast.makeText(ActivityTimeSearch.this, "开始时间大于结束时间,请重新选择结束时间", Toast.LENGTH_SHORT).show();
        } else {
            showProgressDialog();
            Calendar calendar = Calendar.getInstance();
            String begintime = livequery_begintime.getText().toString().replace("月", "")
                    .replace("日 ", "").replace("时",
                            "").trim();
            String endtime = livequery_endtime.getText().toString().replace("月", "").replace("日 ", "").replace("时", "")
                    .trim();
            if (begintime.contains("分")) {
                begintime = begintime.substring(0, 8);
            }
            if (endtime.contains("分")) {
                endtime = endtime.substring(0, 8);
            }
            packRainstatNewUp.city = cityControl.getCutParentCity().NAME;
            packRainstatNewUp.county = cityControl.getCutChildCity().NAME;
            if (cityControl.getCutChildCity().NAME.equals("天津")) {
                packRainstatNewUp.datatype = "2";
            } else if (cityControl.getCutChildCity().NAME.equals("天津市区")) {
                packRainstatNewUp.datatype = "1";
            } else {
                packRainstatNewUp.datatype = "0";
            }
            packRainstatNewUp.begtime = calendar.get(Calendar.YEAR) + begintime.trim();
            packRainstatNewUp.endtime = calendar.get(Calendar.YEAR) + endtime.trim();
            PcsDataDownload.addDownload(packRainstatNewUp);
        }


    }

    /**
     * 下拉数据点击事件监听
     */
    private DrowListClick listener = new DrowListClick() {
        @Override
        public void itemClick(int floag, int item) {
            switch (floag) {
                case 0:
                    cityControl.checkParent(item);
                    livequery_town_spinner.setText(cityControl.getCutChildCity().NAME);
                    down_select();
                    break;
                case 1:
                    cityControl.checkChild(item);
                    down_select();
                    break;
            }
        }
    };

    private void down_select() {
        setListViewTitle(cityControl.getCutChildCity().NAME, cityControl.getCutParentCity().NAME);
        reqTime();
    }

    private PackRainstatNowUp rainUp;

    private void reqData() {
        if (!isOpenNet()) {
            showToast(getString(R.string.net_err));
            return;
        }
        showProgressDialog();
        PcsDataDownload.addDownload(packRainstatHourTimeUp);
        rainUp = new PackRainstatNowUp();
        rainUp.city = cityControl.getCutParentCity().NAME;
        rainUp.county = cityControl.getCutChildCity().NAME;
        if (cityControl.getCutChildCity().NAME.equals("天津")) {
            rainUp.datatype = "2";
        } else if (cityControl.getCutChildCity().NAME.equals("天津市区")) {
            rainUp.datatype = "1";
        } else {
            rainUp.datatype = "0";
        }
        PcsDataDownload.addDownload(rainUp);
    }

    private PackRainstatHourTimeDown downs;

    private PcsDataBrocastReceiver receiver = new PcsDataBrocastReceiver() {
        @Override
        public void onReceive(String nameStr, String errorStr) {
            if (rainUp != null && rainUp.getName().equals(nameStr)) {
                dismissProgressDialog();
                PackRainstatNowDown rainDown = (PackRainstatNowDown) PcsDataManager.getInstance().getNetPack(nameStr);
                if (rainDown == null) {
                    return;
                }
                baseAutoList.clear();
                baseAutoList.add(listTitle);
                data_desc.setText(rainDown.time_str);
                if (isBase) {
                    baseAutoList.addAll(rainDown.baseList);
                } else {
                    baseAutoList.addAll(rainDown.dataList);
                }
                adatperTown.notifyDataSetChanged();
            } else if (packRainstatNewUp.getName().equals(nameStr)) {
                dismissProgressDialog();
                PackRainstatNewDown down = (PackRainstatNewDown) PcsDataManager.getInstance().getNetPack(nameStr);
                if (down == null) {
                    return;
                }
                baseAutoList.clear();
                ItemRainNow listTitles = new ItemRainNow();
                listTitles.stat_name = "站点";
                listTitles.rainfall = "雨量mm";
                listTitles.time = "最大小时雨量/时间";
                baseAutoList.add(listTitles);
                if (isBase) {
                    baseAutoList.addAll(down.baseList);
                } else {
                    baseAutoList.addAll(down.dataList);
                }
                adatperTown.notifyDataSetChanged();
            } else if (packRainstatHourTimeUp.getName().equals(nameStr)) {
                downs = (PackRainstatHourTimeDown) PcsDataManager.getInstance().getNetPack
                        (nameStr);
                if (downs == null) {
                    return;
                }
                initTime();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }


    private int screenHight = 0;

    /**
     * 创建下拉选择列表
     */
    public PopupWindow createPopupWindow(final TextView dropDownView, final List<String> dataeaum, final int floag,
                                         final DrowListClick listener) {
        AdapterData dataAdapter = new AdapterData(this, dataeaum);
        View popcontent = LayoutInflater.from(this).inflate(R.layout.pop_list_layout, null);
        ListView lv = (ListView) popcontent.findViewById(R.id.mylistviw);
        lv.setAdapter(dataAdapter);
        if (floag == 2 || floag == 3) {
            dataAdapter.setTextViewSize(13);
        } else {
            dataAdapter.setTextViewSize(17);
        }
        final PopupWindow pop = new PopupWindow(this);
        pop.setContentView(popcontent);
        pop.setOutsideTouchable(false);
        if (floag == 3) {
            pop.setWidth((int) (dropDownView.getWidth() + 20));
        } else {
            pop.setWidth((int) (dropDownView.getWidth()));
        }
        // 调整下拉框长度
        screenHight = Util.getScreenHeight(this);
        if (dataeaum.size() < 9) {
            pop.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        } else {
            pop.setHeight((int) (screenHight * 0.5));
        }
        pop.setFocusable(true);

        String selName = dropDownView.getText().toString();
        int selNum = 0;
        for (int i = 0; i < dataeaum.size(); i++) {
            if (selName.equals(dataeaum.get(i))) {
                selNum = i;
            }
        }
        lv.setSelection(selNum);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pop.dismiss();
                if (floag == 1) {
                    if (position == 1) {
                        datatype = "2";
                    } else if (position == 2) {
                        datatype = "1";
                    } else {
                        datatype = "0";
                    }
                }
                if (floag == 2) {
                    begin_num = position;
                    dropDownView.setText(dataeaum.get(position));
                } else if (floag == 3) {
                    if (begin_num < position) {
                        Toast.makeText(ActivityTimeSearch.this, "开始时间大于结束时间,请重新选择结束时间", Toast.LENGTH_SHORT).show();
                    } else {
                        dropDownView.setText(dataeaum.get(position));
                    }
                } else {
                    listener.itemClick(floag, position);
                    dropDownView.setText(dataeaum.get(position));
                }
            }
        });
        return pop;
    }

    private String m_time = null;
    private String start_live, end_live;

    public void initTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf_input = new SimpleDateFormat("yyyyMMddHHmm");
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分");
        try {
            m_time = format.format(sdf_input.parse(downs.time_str));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        //得到一个月最最后一天日期(31/30/29/28)
        int MaxDay=cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        calendar.add(Calendar.DATE, -MaxDay);
        Date d = calendar.getTime();
        String days = format.format(d);



        String str[] = m_time.split("年");
        livequery_endtime.setText(str[1]);
        String strs[] = str[1].split("时");
        livequery_begintime.setText(strs[0] + "时00分");

        String str_o[] = days.split("时");
        start_live = year + "-" + month + "-" + day + " " + hour + ":00";
        timeSelector_begin = new TimeSelector(this, new TimeSelector.ResultHandler() {
            @Override
            public void handle(String time) {
                start_live = time;
                SimpleDateFormat sdf_input = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                SimpleDateFormat format = new SimpleDateFormat("MM月dd日 HH时mm分");

                try {
                    time = format.format(sdf_input.parse(time));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                livequery_begintime.setText(time);
            }
        }, str_o[0].replace("年", "-").replace("月", "-").replace("日", "") + ":00",

                (m_time).replace("年", "-").replace("月", "-").replace("日",
                        "").replace("时", ":"));
//        start_live = year + "-" + (month - 1) + "-" + days + " " + hour + ":00";

        timeSelector_end = new TimeSelector(this, new TimeSelector.ResultHandler() {
            @Override
            public void handle(String time) {
                end_live = time;
                SimpleDateFormat sdf_input = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                SimpleDateFormat format = new SimpleDateFormat("MM月dd日 HH时mm分");
                try {
                    time = format.format(sdf_input.parse(time));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                livequery_endtime.setText(time);
            }
        }, str_o[0].replace("年", "-").replace("月", "-").replace("日", "") + ":00"
                ,
                (year + "年" + livequery_endtime.getText().toString()).replace("年", "-").replace("月", "-").replace("日"
                        , "").replace("时", ":").replace("分", ""));
        end_live = (year + "年" + livequery_endtime.getText().toString()).replace("年", "-").replace("月", "-").replace(
                "日", "").replace("时", ":").replace("分", "");
    }


    public int timeCompare(String startTime, String endTime) {
        int i = 0;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            Date date1 = dateFormat.parse(startTime);//开始时间
            Date date2 = dateFormat.parse(endTime);//结束时间
            // 1 结束时间小于开始时间 2 开始时间与结束时间相同 3 结束时间大于开始时间
            if (date2.getTime() < date1.getTime()) {
                //结束时间小于开始时间
                i = 1;
            } else if (date2.getTime() == date1.getTime()) {
                //开始时间与结束时间相同
                i = 2;
            } else if (date2.getTime() > date1.getTime()) {
                //结束时间大于开始时间
                i = 3;
            }
        } catch (Exception e) {

        }
        return i;
    }


}
