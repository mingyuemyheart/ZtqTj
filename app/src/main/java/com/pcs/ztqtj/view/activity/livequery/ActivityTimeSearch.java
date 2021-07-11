package com.pcs.ztqtj.view.activity.livequery;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.livequery.AdapterData;
import com.pcs.ztqtj.control.adapter.livequery.AdatperRainNowFall;
import com.pcs.ztqtj.control.inter.DrowListClick;
import com.pcs.ztqtj.control.tool.utils.TextUtil;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.util.OkHttpUtil;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
import com.pcs.ztqtj.view.fragment.livequery.fujian_city.CityListControl;
import com.pcs.ztqtj.view.myview.MyListView;
import com.pcs.ztqtj.view.myview.TimeSelector;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 监测预报-实况查询-数据与统计-雨量查询-任意时段查询
 */
public class ActivityTimeSearch extends FragmentActivityZtqBase implements View.OnClickListener {

    private TextView data_desc;
    private TextView livequery_city_spinner;
    private TextView livequery_town_spinner;
    private MyListView livequery_town;

    private TextView description_title_town;
    private TextView description_title_city;

    private AdatperRainNowFall adatperTown;

    private List<ItemRainNow> baseAutoList = new ArrayList<>();
    private TextView livequery_begintime, livequery_endtime;
    private Button livequery_search_btn;
    private int begin_num = 0;
    private String datatype = "0";
    private PackRainstatHourTimeUp packRainstatHourTimeUp = new PackRainstatHourTimeUp();
    private boolean isBase = false;
    private TimeSelector timeSelector_begin, timeSelector_end;
    private String startTime, endTime;
    private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddHHmm", Locale.CHINA);
    private SimpleDateFormat sdf2 = new SimpleDateFormat("MM月dd日 HH时mm分", Locale.CHINA);
    private SimpleDateFormat sdf3 = new SimpleDateFormat("MM月dd日 HH时00分", Locale.CHINA);
    private SimpleDateFormat sdf4 = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
    private SimpleDateFormat sdf5 = new SimpleDateFormat("yyyy-MM-dd HH:00", Locale.CHINA);

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
        livequery_town_spinner = (TextView) findViewById(R.id.livequery_town_spinner);
        data_desc = (TextView) findViewById(R.id.data_desc);
        livequery_town = (MyListView) findViewById(R.id.livequery_town);
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

    private CityListControl cityControl;

    private void initData() {
        isBase = getIntent().getBooleanExtra("isbase", false);
        adatperTown = new AdatperRainNowFall(baseAutoList);
        livequery_town.setAdapter(adatperTown);
        Intent intent = getIntent();
        PackLocalCity userCity = (PackLocalCity) intent.getSerializableExtra("town");
        cityControl = new CityListControl(userCity, true);

        livequery_city_spinner.setText(cityControl.getCutParentCity().NAME);
        livequery_town_spinner.setText(cityControl.getCutChildCity().NAME);
        setListViewTitle(cityControl.getCutChildCity().NAME, cityControl.getCutParentCity().NAME);

        PcsDataDownload.addDownload(packRainstatHourTimeUp);
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
                timeSelector_begin.setTime(startTime);
                timeSelector_begin.show();
//                createPopupWindow(livequery_begintime, getTimes(0), 2, listener)
//                        .showAsDropDown(livequery_begintime);
                break;
            case R.id.livequery_endtime:
                timeSelector_end.setTime(endTime);
                timeSelector_end.show();
//                createPopupWindow(livequery_endtime, getTimes(1), 3, listener)
//                        .showAsDropDown(livequery_endtime);
                break;
            case R.id.livequery_search_btn:
                okHttpRainWill();
                break;
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
                    setListViewTitle(cityControl.getCutChildCity().NAME, cityControl.getCutParentCity().NAME);
                    break;
                case 1:
                    cityControl.checkChild(item);
                    setListViewTitle(cityControl.getCutChildCity().NAME, cityControl.getCutParentCity().NAME);
                    break;
            }
        }
    };

    private PackRainstatHourTimeDown downs;

    private PcsDataBrocastReceiver receiver = new PcsDataBrocastReceiver() {
        @Override
        public void onReceive(String nameStr, String errorStr) {
            if (packRainstatHourTimeUp.getName().equals(nameStr)) {
                downs = (PackRainstatHourTimeDown) PcsDataManager.getInstance().getNetPack(nameStr);
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

    /**
     * 创建下拉选择列表
     */
    public PopupWindow createPopupWindow(final TextView dropDownView, final List<String> dataeaum, final int floag, final DrowListClick listener) {
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
        int screenHight = Util.getScreenHeight(this);
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

    public void initTime() {
        Calendar calendar = Calendar.getInstance();

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        //得到一个月最最后一天日期(31/30/29/28)
        int MaxDay=cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        calendar.add(Calendar.DATE, -MaxDay);

        try {
            livequery_endtime.setText(sdf2.format(sdf1.parse(downs.time_str)));
            livequery_begintime.setText(sdf3.format(sdf1.parse(downs.time_str)));
            endTime = sdf4.format(sdf1.parse(downs.time_str));
            startTime = sdf5.format(sdf1.parse(downs.time_str));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        timeSelector_begin = new TimeSelector(this, new TimeSelector.ResultHandler() {
            @Override
            public void handle(String time) {
                try {
                    livequery_begintime.setText(sdf3.format(sdf4.parse(time)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                startTime = time;
            }
        }, startTime, endTime);

        timeSelector_end = new TimeSelector(this, new TimeSelector.ResultHandler() {
            @Override
            public void handle(String time) {
                try {
                    livequery_endtime.setText(sdf2.format(sdf4.parse(time)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                endTime = time;
            }
        }, startTime, endTime);

        okHttpRainWill();
    }

    public int timeCompare(String startTime, String endTime) {
        int i = 0;
        try {
            Date date1 = sdf4.parse(startTime);//开始时间
            Date date2 = sdf4.parse(endTime);//结束时间
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

    /**
     * 雨量统计任意时间
     */
    private void okHttpRainWill() {
        if (timeCompare(startTime, endTime) == 1) {
            Toast.makeText(ActivityTimeSearch.this, "开始时间大于结束时间,请重新选择结束时间", Toast.LENGTH_SHORT).show();
            return;
        }
        showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    JSONObject info = new JSONObject();
                    info.put("stationId", cityControl.getCutChildCity().ID);
                    info.put("startTime", startTime);
                    info.put("endTime", endTime);
                    if (isBase) {
                        info.put("isTj", "");
                    } else {
                        info.put("isTj", "天津");
                    }
                    param.put("paramInfo", info);
                    String json = param.toString();
                    Log.e("datastatis_rainwill", json);
                    final String url = CONST.BASE_URL+"datastatis_rainwill";
                    Log.e("datastatis_rainwill", url);
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
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dismissProgressDialog();
                                    baseAutoList.clear();
                                    adatperTown.notifyDataSetChanged();
                                    Log.e("datastatis_rainwill", result);
                                    if (!TextUtil.isEmpty(result)) {
                                        try {
                                            JSONObject obj = new JSONObject(result);
                                            if (!obj.isNull("b")) {
                                                JSONObject bobj = obj.getJSONObject("b");
                                                if (!bobj.isNull("rainstat_min")) {
                                                    JSONObject yltj_rank = bobj.getJSONObject("rainstat_min");
                                                    if (!TextUtil.isEmpty(yltj_rank.toString())) {
                                                        dismissProgressDialog();
                                                        PackRainstatNewDown down = new PackRainstatNewDown();
                                                        down.fillData(yltj_rank.toString());
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
                                                        reRank(baseAutoList, adatperTown);
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

    private void reRank(List<ItemRainNow> list, AdatperRainNowFall adapter) {
        if (list.size() <= 1) {
            return;
        }
        Collections.sort(list, new Comparator<ItemRainNow>() {
            @Override
            public int compare(ItemRainNow arg0, ItemRainNow arg1) {
                String value0 = arg0.rainfall,value1 = arg1.rainfall;
                if (TextUtils.isEmpty(value0) || TextUtils.isEmpty(value1)) {
                    return -1;
                }
                if (value0.contains("雨量")) {
                    return -1;
                }
                if (value1.contains("雨量")) {
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

}
