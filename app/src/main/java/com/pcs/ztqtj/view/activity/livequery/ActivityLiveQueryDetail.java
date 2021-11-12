package com.pcs.ztqtj.view.activity.livequery;

import android.app.ActionBar;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Text;
import com.amap.api.maps.model.TextOptions;
import com.pcs.lib.lib_pcs_v3.control.tool.ScreenUtil;
import com.pcs.lib.lib_pcs_v3.control.tool.Util;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCity;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityMain;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalStation;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackSstqDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackFycxSstqDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackFycxTrendDown;
import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.livequery.AdapterData;
import com.pcs.ztqtj.control.adapter.livequery.AdapterDetailSearchResult;
import com.pcs.ztqtj.control.adapter.livequery.AdapterLiveQueryDetail;
import com.pcs.ztqtj.control.inter.ClickPositionListener;
import com.pcs.ztqtj.control.inter.DrowListClick;
import com.pcs.ztqtj.control.livequery.ControlDistribution;
import com.pcs.ztqtj.control.tool.CommUtils;
import com.pcs.ztqtj.control.tool.utils.TextUtil;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.model.pack.ItemLiveQuery;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.util.OkHttpUtil;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
import com.pcs.ztqtj.view.myview.LiveQueryView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 监测预报-整点天气，实况查询的详情
 */
public class ActivityLiveQueryDetail extends FragmentActivityZtqBase implements AMap.OnMarkerClickListener {

    private ListView rain_table;
    private LinearLayout layout_content_myview;//自定义视图布局
    private LiveQueryView liveQueryView;
    private RadioGroup radioGroupViewOrTable;
    private RadioGroup radioGroupItem;
    private AdapterLiveQueryDetail adatper;
    private ActivityLiveQueryDetailControl control;
    private String stationName;

    private TextView acTime;
    private TextView null_data;
    private String itemType;
    private EditText ed_search;

    private TextView sstq_info_left;
    private TextView sstq_info_right;
    private TextView preTime;
    private TextView btn_sstq;

    private ListView list_station;
    private AdapterDetailSearchResult editSearchResult;
    private List<PackLocalStation> stationList;

    private CheckBox btn_double;
    private List<ItemLiveQuery> mItems;
    private PackLocalStation cutStation;

    private TextView myviewUnit;
    private LinearLayout table_layout;
    private RelativeLayout layout_content_viewTableMap;
    private TextView text_item_over24, text_item_prediction24;
    private boolean isLocal = false;

    private View layoutSelector;
    private TextView tvCity, tvSite;
    private int currentSelectedCityPosition = 0;
    private int currentSelectedSitePosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_livequery_detail);
        itemType = getIntent().getStringExtra("item");// temp,气温 rain,降雨,wind风况
        stationName = getIntent().getStringExtra("stationName");
        isLocal = ZtqCityDB.getInstance().getStationIsTjByName(stationName);
        if (TextUtils.isEmpty(stationName)) {
//            传进来的名称是空的话就取站点id然后匹配站点名称
            String stationId = getIntent().getStringExtra("id");
            PackLocalStation station = ZtqCityDB.getInstance().getStationById(stationId);
            if (station != null) {
                stationName = station.STATIONNAME;
                init();
            } else {
                okHttpSstq();
            }
        } else {
            init();
        }
        initMap(savedInstanceState);
    }

    private void init() {
        setTitleText(stationName);
        isLocal = ZtqCityDB.getInstance().getStationIsTjByName(stationName);
        TextView textView = getTitleTextView();
        textView.setTextSize(1, 19);
        initView();
        initEvent();
        initData();
    }

    /**
     * 获取实况信息
     */
    private void okHttpSstq() {
        final PackLocalCityMain packCity = ZtqCityDB.getInstance().getCityMain();
        if (packCity == null || packCity.ID == null) {
            return;
        }
        showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param  = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    JSONObject info = new JSONObject();
                    info.put("stationId", packCity.ID);
                    param.put("paramInfo", info);
                    String json = param.toString();
                    Log.e("sstq", json);
                    final String url = CONST.BASE_URL+"sstq";
                    Log.e("sstq", url);
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
//                                    Log.e("sstq", result);
                                    if (!TextUtil.isEmpty(result)) {
                                        try {
                                            JSONObject obj = new JSONObject(result);
                                            if (!obj.isNull("b")) {
                                                JSONObject bobj = obj.getJSONObject("b");
                                                if (!bobj.isNull("sstq")) {
                                                    JSONObject sstqobj = bobj.getJSONObject("sstq");
                                                    if (!TextUtil.isEmpty(sstqobj.toString())) {
                                                        PackSstqDown packSstq = new PackSstqDown();
                                                        packSstq.fillData(sstqobj.toString());
                                                        if (!TextUtils.isEmpty(packSstq.stationname)) {
                                                            stationName = packSstq.stationname;
                                                        } else {
                                                            stationName = packCity.NAME;
                                                        }
                                                        init();
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

    private Button btn_to_pro, btn_to_live;

    private void initView() {
        layout_content_viewTableMap = (RelativeLayout) findViewById(R.id.layout_content_viewTableMap);
        table_layout = (LinearLayout) findViewById(R.id.table_layout);
        layout_content_myview = (LinearLayout) findViewById(R.id.layout_content_myview);
        myviewUnit = (TextView) findViewById(R.id.myviewUnit);
        text_item_over24 = (TextView) findViewById(R.id.talbe_title_item_over24);
        text_item_prediction24 = (TextView) findViewById(R.id.talbe_title_item_prediction24);

        btn_sstq = (TextView) findViewById(R.id.btn_sstq);
        btn_sstq.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        sstq_info_left = (TextView) findViewById(R.id.sstq_info_left);
        sstq_info_right = (TextView) findViewById(R.id.sstq_info_right);
        acTime = (TextView) findViewById(R.id.acTime);
        null_data = (TextView) findViewById(R.id.null_data);
        preTime = (TextView) findViewById(R.id.preTime);

        rain_table = (ListView) findViewById(R.id.rain_table);
        list_station = (ListView) findViewById(R.id.list_station);
        liveQueryView = (LiveQueryView) findViewById(R.id.liveQueryView);
        radioGroupViewOrTable = (RadioGroup) findViewById(R.id.radioGroupViewOrTable);
        radioGroupItem = (RadioGroup) findViewById(R.id.radioGroupItem);
        ed_search = (EditText) findViewById(R.id.ed_search);
        btn_double = (CheckBox) findViewById(R.id.btn_double);
        btn_to_pro = (Button) findViewById(R.id.btn_to_pro);
        btn_to_live = (Button) findViewById(R.id.btn_to_live);

        layoutSelector = findViewById(R.id.layout_selector);
        tvCity = findViewById(R.id.tv_city);
        tvSite = findViewById(R.id.tv_site);
        tvCity.setOnClickListener(selectorClickListener);
        tvSite.setOnClickListener(selectorClickListener);
        layoutSelector.setOnClickListener(selectorClickListener);
        if(isLocal) {
            layoutSelector.setVisibility(View.VISIBLE);
        } else {
            layoutSelector.setVisibility(View.GONE);
        }
    }

    private void initEvent() {
        radioGroupViewOrTable.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                ed_search.clearFocus();
                if (checkedId == R.id.show_view) {
                    table_layout.setVisibility(View.GONE);
                    layout_content_myview.setVisibility(View.VISIBLE);
                } else if (checkedId == R.id.show_table) {
                    table_layout.setVisibility(View.VISIBLE);
                    layout_content_myview.setVisibility(View.GONE);
                } else if (checkedId == R.id.show_map) {
                    table_layout.setVisibility(View.GONE);
                    layout_content_myview.setVisibility(View.GONE);
                } else {
                    table_layout.setVisibility(View.GONE);
                    layout_content_myview.setVisibility(View.VISIBLE);
                }
            }
        });

        radioGroupItem.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                ed_search.clearFocus();
//                栏目选择
                switch (checkedId) {
                    case R.id.live_rain:
//                        btn_double.setChecked(false);
                        myviewUnit.setText("毫米(mm)");
                        liveQueryView.setItemName("雨量：", ControlDistribution.ColumnCategory.RAIN, "mm", LiveQueryView.IsDrawRectangele.RECTANGLE);
                        control.setCurrentType(ControlDistribution.ColumnCategory.RAIN);
                        updateData(stationName);
                        break;
                    case R.id.live_temperture:
//                        btn_double.setChecked(false);
                        myviewUnit.setText("摄氏度(°C）");
                        liveQueryView.setItemName("气温：", ControlDistribution.ColumnCategory.TEMPERATURE, "°C", LiveQueryView.IsDrawRectangele.BROKENLINE);
                        control.setCurrentType(ControlDistribution.ColumnCategory.TEMPERATURE);
                        updateData(stationName);
                        break;
                    case R.id.live_wind_speed:
//                        btn_double.setChecked(false);
                        myviewUnit.setText("米/秒（m/s）");
                        liveQueryView.setItemName("风速：", ControlDistribution.ColumnCategory.WIND, "m/s", LiveQueryView.IsDrawRectangele.DIRECTIONLINE);
                        control.setCurrentType(ControlDistribution.ColumnCategory.WIND);
                        updateData(stationName);
                        break;
                    case R.id.live_visibility:
//                        btn_double.setChecked(false);
                        myviewUnit.setText("米（m）");
                        liveQueryView.setItemName("能见度：", ControlDistribution.ColumnCategory.VISIBILITY, "m", LiveQueryView.IsDrawRectangele.BROKENLINE);
                        control.setCurrentType(ControlDistribution.ColumnCategory.VISIBILITY);
                        updateData(stationName);
                        break;
                    case R.id.live_pressure:
//                        btn_double.setChecked(false);
                        myviewUnit.setText("百帕（hPa）");
                        liveQueryView.setItemName("气压：", ControlDistribution.ColumnCategory.PRESSURE, "hPa", LiveQueryView.IsDrawRectangele.BROKENLINE);
                        control.setCurrentType(ControlDistribution.ColumnCategory.PRESSURE);
                        updateData(stationName);
                        break;
                    case R.id.live_humidity:
//                        btn_double.setChecked(false);
                        myviewUnit.setText("百分比（%）");
                        liveQueryView.setItemName("相对湿度：", ControlDistribution.ColumnCategory.HUMIDITY, "%", LiveQueryView.IsDrawRectangele.BROKENLINE);
                        control.setCurrentType(ControlDistribution.ColumnCategory.HUMIDITY);
                        updateData(stationName);
                        break;
                }
            }
        });

        //刷新动作
        setBtnRight(R.drawable.btn_refresh, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ed_search.clearFocus();
                control.reqData(control.getCuttentType(), stationName);
            }
        });
        ed_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                搜索具体的站点
                searchStation(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        ed_search.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    //focusSearch();
                } else {
                    CommUtils.closeKeyboard(ActivityLiveQueryDetail.this);
                    ed_search.setText("");
                    searchStation("");
                }
            }
        });

        list_station.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cutStation = stationList.get(position);
                if(cutStation == null) {
                    return;
                }
                updateData(cutStation.STATIONNAME);
                ed_search.setText("");
                ed_search.clearFocus();
            }
        });
        btn_double.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    liveQueryView.setDoubleWidth(true);
                } else {
                    liveQueryView.setDoubleWidth(false);
                }
            }
        });

        liveQueryView.setClickPositionListener(clicklistener);
        btn_to_pro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                liveQueryView.moveToPro(true);
            }
        });
        btn_to_live.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                liveQueryView.moveToPro(false);
            }
        });

        btn_sstq.setOnTouchListener(onTouchListener);
        sstq_info_left.setOnTouchListener(onTouchListener);
        sstq_info_right.setOnTouchListener(onTouchListener);
        layout_content_viewTableMap.setOnTouchListener(onTouchListener);
        acTime.setOnTouchListener(onTouchListener);
        preTime.setOnTouchListener(onTouchListener);
        text_item_over24.setOnTouchListener(onTouchListener);
        text_item_prediction24.setOnTouchListener(onTouchListener);
        rain_table.setOnTouchListener(onTouchListener);
        radioGroupItem.setOnTouchListener(onTouchListener);
    }

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            ed_search.clearFocus();
            return false;
        }
    };

    private void initData() {
        //城市
        if (isLocal){
            ed_search.setHint("输入自动站名称");
        }else{
            ed_search.setHint("输入国内城市名称");
        }
        control = new ActivityLiveQueryDetailControl(this);
        cutStation = ZtqCityDB.getInstance().getStation(stationName);
        text_item_over24.setText("未来24小时预报值");
        text_item_prediction24.setText("过去24小时实况值");
        mItems = new ArrayList<>();
        adatper = new AdapterLiveQueryDetail(mItems);
        rain_table.setAdapter(adatper);

        stationList = new ArrayList<>();
        editSearchResult = new AdapterDetailSearchResult(stationList);
        list_station.setAdapter(editSearchResult);

        RadioButton rb;
        if (itemType.equals("temp")) {
            rb = (RadioButton) findViewById(R.id.live_temperture);
            rb.setChecked(true);
        } else if (itemType.equals("rain")) {
            rb = (RadioButton) findViewById(R.id.live_rain);
            rb.setChecked(true);
        } else if (itemType.equals("wind")) {
            rb = (RadioButton) findViewById(R.id.live_wind_speed);
            rb.setChecked(true);
        } else if (itemType.equals("visibility")) {
            rb = (RadioButton) findViewById(R.id.live_visibility);
            rb.setChecked(true);
        } else if (itemType.equals("pressure")) {
            rb = (RadioButton) findViewById(R.id.live_pressure);
            rb.setChecked(true);
        } else if (itemType.equals("humidity")) {
            rb = (RadioButton) findViewById(R.id.live_humidity);
            rb.setChecked(true);
        }
    }

    private void searchStation(String str) {
        ViewGroup.LayoutParams lp = list_station.getLayoutParams();
        stationList.clear();
        if (!TextUtils.isEmpty(str)) {
            List<PackLocalStation> stationList = control.searchStation(stationName, str);
            this.stationList.addAll(stationList);
        }
        if (stationList.size() > 7) {
            lp.height = 7 * (int) getResources().getDimension(R.dimen.dimen30);
        } else {
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
        list_station.setLayoutParams(lp);
        editSearchResult.notifyDataSetChanged();
    }

    private void focusSearch() {
        if(!TextUtils.isEmpty(ed_search.getText().toString())) return;
        ViewGroup.LayoutParams lp = list_station.getLayoutParams();
        stationList.clear();
        List<PackLocalStation> result;
        if (isLocal) {
            result = ZtqCityDB.getInstance().getBaseStationList();
        } else {
            result = ZtqCityDB.getInstance().getNationStationList();
        }
        if(result != null) {
            this.stationList.addAll(result);
        }
        if (stationList.size() > 7) {
            lp.height = 7 * (int) getResources().getDimension(R.dimen.dimen30);
        } else {
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
        list_station.setLayoutParams(lp);
        editSearchResult.notifyDataSetChanged();
    }

    public void reFlushList(PackFycxTrendDown trendDown) {
        if (liveQueryView == null) {
            return;
        }
        text_item_over24.setText("过去24小时实况值" + liveQueryView.getUnit());
        text_item_prediction24.setText("未来24小时预报值" + liveQueryView.getUnit());
        this.mItems.clear();
        if (trendDown != null) {
            for (int i = 0; i < trendDown.skList.size(); i++) {
                ItemLiveQuery item = new ItemLiveQuery();
                item.value_over24_hour = trendDown.skList.get(i).dt;
                if (TextUtils.isEmpty(trendDown.skList.get(i).val)) {
                    item.value_over24_value = "--";
                } else {
                    if (trendDown.skList.get(i).val.equals("0.0")) {
                        item.value_over24_value = "0";
                    } else {
                        item.value_over24_value = trendDown.skList.get(i).val;
                    }
                }

                try {
                    if (trendDown.ybList.size() > 0) {
                        item.value_future24_hour = trendDown.ybList.get(i).dt;
                        if (TextUtils.isEmpty(trendDown.ybList.get(i).val)) {
                            item.value_future24_value = "--";
                        } else {
                            if (trendDown.ybList.get(i).val.equals("0.0")) {
                                item.value_future24_value = "0";
                            } else {
                                item.value_future24_value = trendDown.ybList.get(i).val;
                            }
                        }
                    }
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }

                this.mItems.add(item);
            }
        }
        adatper.notifyDataSetChanged();
        if (trendDown != null) {
            acTime.setText("实况时段：" + trendDown.sk_time);
            preTime.setText("预报时段：" + trendDown.yb_time);
            if (trendDown.skList.size() == 0 && trendDown.ybList.size() == 0) {
                dataIsNull();
            } else {
                boolean hasValue = false;
                for (int i = 0; i < trendDown.skList.size(); i++) {
                    PackFycxTrendDown.FycxMapBean bena = trendDown.skList.get(i);
                    if (!TextUtils.isEmpty(bena.val)) {
                        hasValue = true;
                        break;
                    }
                }
                for (int i = 0; i < trendDown.ybList.size(); i++) {
                    PackFycxTrendDown.FycxMapBean bena = trendDown.ybList.get(i);
                    if (!TextUtils.isEmpty(bena.val) || hasValue) {
                        hasValue = true;
                        break;
                    }
                }
                liveQueryView.setNewData(trendDown.skList, trendDown.ybList);
                if (hasValue) {
                    null_data.setVisibility(View.GONE);
                } else {
                    null_data.setVisibility(View.VISIBLE);
                }
            }
        } else {
            acTime.setText("实况时段：");
            preTime.setText("预报时段：");
            //暂无数据
            dataIsNull();
//            liveQueryView.cleanView();
        }
    }

    public void dataIsNull() {
        null_data.setVisibility(View.VISIBLE);
        liveQueryView.setNewData(new ArrayList<PackFycxTrendDown.FycxMapBean>(), new ArrayList<PackFycxTrendDown.FycxMapBean>());
    }

    private String upDateTime = "更新";

    public void reFlushSstq(PackFycxSstqDown sstq) {
        String value = "";
        if (sstq != null) {
            upDateTime = sstq.upt + "更新";
            String ssqtTextValueLeft = "";
            String ssqtTextValueRight = "";
            if (TextUtils.isEmpty(sstq.ct)) {
                ssqtTextValueLeft += "气温：--" + "\n";
            } else {
                ssqtTextValueLeft += "气温：" + sstq.ct + "°C\n";
            }
            if (TextUtils.isEmpty(sstq.humidity)) {
                ssqtTextValueLeft += "湿度：--" + "\n";
            } else {
                ssqtTextValueLeft += "湿度：" + sstq.humidity + "%\n";
            }
            if (TextUtils.isEmpty(sstq.visibility)) {
                ssqtTextValueLeft += "能见度：--" + "\n";
            } else {
                ssqtTextValueLeft += "能见度：" + sstq.visibility + "m\n";
            }

            if (TextUtils.isEmpty(sstq.rainfall)) {
                ssqtTextValueRight += "本时次雨量：--" + "\n";
            } else {
                ssqtTextValueRight += "本时次雨量：" + sstq.rainfall + "mm" + "\n";
            }

            if (TextUtils.isEmpty(sstq.wind_dir)) {
                ssqtTextValueRight += "风况：--";
            } else {
                ssqtTextValueRight += "风况：" + sstq.wind_dir;
            }
            if (TextUtils.isEmpty(sstq.wind_speed)) {
                ssqtTextValueRight += "  --" + "\n";
            } else {
                ssqtTextValueRight += "  " + sstq.wind_speed + "m/s" + "\n";
            }


            if (TextUtils.isEmpty(sstq.vaporpressuser)) {
                ssqtTextValueRight += "气压：--";
            } else {
                ssqtTextValueRight += "气压：" + sstq.vaporpressuser + "hPa";
            }

            sstq_info_left.setText(ssqtTextValueLeft);
            sstq_info_right.setText(ssqtTextValueRight);
            btn_sstq.setText("当前实况" + upDateTime);

            if (control.getCuttentType() == ControlDistribution.ColumnCategory.RAIN) {
                value = sstq.rainfall;
            } else if (control.getCuttentType() == ControlDistribution.ColumnCategory.TEMPERATURE) {
                value = sstq.ct;
            } else if (control.getCuttentType() == ControlDistribution.ColumnCategory.WIND) {
                value = sstq.wind_speed;
            } else if (control.getCuttentType() == ControlDistribution.ColumnCategory.VISIBILITY) {
                value = sstq.visibility;
            } else if (control.getCuttentType() == ControlDistribution.ColumnCategory.PRESSURE) {
                value = sstq.vaporpressuser;
            } else if (control.getCuttentType() == ControlDistribution.ColumnCategory.HUMIDITY) {
                value = sstq.humidity;
            }
        }
        if (value.equals("0.0")) {
            value = "0";
        }
        PackLocalStation station = ZtqCityDB.getInstance().getStation(stationName);
        setStation(station, value);
    }

    private ClickPositionListener clicklistener = new ClickPositionListener() {
        @Override
        public void positionListener(int x, int y, String value, boolean isYb) {
            if (popupWindow != null && popupWindow.isShowing()) {
                popupWindow.dismiss();
            }
            showValue(liveQueryView, value, x, y, isYb);
        }

        @Override
        public void moveListener() {
            if (popupWindow != null && popupWindow.isShowing()) {
                popupWindow.dismiss();
            }
        }
    };


    private PopupWindow popupWindow;

    /**
     * 显示下来选择列表
     */
    public void showValue(View view, String value, int x, int y, boolean isYb) {
        // 一个自定义的布局，作为显示的内容
        View contentView = LayoutInflater.from(this).inflate(R.layout.pop_livequery, null);
        TextView tv_value = (TextView) contentView.findViewById(R.id.tv_value);
        tv_value.setText(value);
        if (isYb) {
            contentView.setBackgroundResource(R.drawable.icon_livequery_yb);
        } else {
            contentView.setBackgroundResource(R.drawable.icon_livequery_sk);
        }
        // 设置按钮的点击事件
        popupWindow = new PopupWindow(contentView, ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.color.alpha100));
        // 设置好参数之后再show

        int indexTime = value.indexOf("时");
        int lengText = value.length() - indexTime;
        int widtha = (int) ((tv_value.getTextSize() * (lengText))) / 3 * 2;
        int width = ScreenUtil.dip2px(this, 80);
        int hight = ScreenUtil.dip2px(this, 50);
        if (widtha > width) {
            width = widtha;
        }
        int scW = getWindowManager().getDefaultDisplay().getWidth();
        if ((x - width / 2) < 0 || (x + width / 2) > scW) {
            return;
        }
        popupWindow.setWidth(width);
        popupWindow.setHeight(hight);
        popupWindow.showAsDropDown(view, x - width / 2, -(y + hight));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    private AMap mAMap;
    private MapView mMapView;
    // 当前经纬度
    private LatLng mLatLng = null;
    // 标记
    private MarkerOptions mMarker = null;

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    private void initMap(Bundle savedInstanceState) {
        mMapView = (MapView) findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        mAMap = mMapView.getMap();
        // 点击事件
        mAMap.setOnMapClickListener(mOnMapClick);
        mAMap.setOnMarkerClickListener(this);
        // 缩放
        mAMap.moveCamera(CameraUpdateFactory.zoomTo(12));
        // 显示比例尺
        mAMap.getUiSettings().setScaleControlsEnabled(true);
        // 禁止缩放
//            mAMap.getUiSettings().setScrollGesturesEnabled(false);
//            mAMap.getUiSettings().setZoomGesturesEnabled(false);
        // 禁止显示缩放按钮
        mAMap.getUiSettings().setZoomControlsEnabled(false);
    }

    private void setStation(PackLocalStation station, String value) {
        if (station != null && !TextUtils.isEmpty(station.LONGITUDE)) {
            Double LATITUDE = Double.parseDouble(station.LATITUDE);
            Double LONGITUDE = Double.parseDouble(station.LONGITUDE);
            mLatLng = new LatLng(LATITUDE, LONGITUDE);
            setLocation(mLatLng, value);
        }
    }

    /**
     * 设置定位点，显示标识
     * @param latLng
     */
    private void setLocation(LatLng latLng, String value) {
        mLatLng = latLng;
        mAMap.clear();
        // 地图位置
        // 标记
        if (mMarker == null) {
            mMarker = new MarkerOptions();
        }
//      mMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_location)).title("aa").snippet("11").anchor(0.5f, 0.5f).period(5).zIndex(1);
        if (stationName == null) {
            return;
        }
        TextOptions textOptions = new TextOptions()
                .position(latLng)
                .text(stationName)
                .fontColor(Color.WHITE)
                .backgroundColor(getResources().getColor(R.color.bg_livequery))
                .fontSize(ScreenUtil.dip2px(this, 14))
//                .rotate(20)
                .align(Text.ALIGN_CENTER_HORIZONTAL, Text.ALIGN_TOP)
                .zIndex(1.f);
//                .typeface(Typeface.DEFAULT_BOLD);
        mAMap.addText(textOptions);
        mMarker.position(latLng).icon(BitmapDescriptorFactory.fromBitmap(control.getIcon(this, value)));
        mAMap.addMarker(mMarker);
        LatLng tempLatLng = new LatLng(latLng.latitude, latLng.longitude);
        mAMap.moveCamera(CameraUpdateFactory.changeLatLng(tempLatLng));
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    /**
     * 地图点击事件
     */
    private final AMap.OnMapClickListener mOnMapClick = new AMap.OnMapClickListener() {
        @Override
        public void onMapClick(LatLng arg0) {

        }
    };

    /**
     * 创建下拉选择列表
     */
    public PopupWindow createPopupWindow(final TextView dropDownView, final List<String> dataeaum, final int floag, final DrowListClick listener) {
        AdapterData dataAdapter = new AdapterData(this, dataeaum);
        View popcontent = LayoutInflater.from(this).inflate(R.layout.pop_list_layout, null);
        ListView lv = (ListView) popcontent.findViewById(R.id.mylistviw);
        lv.setAdapter(dataAdapter);
        final PopupWindow pop = new PopupWindow(this);
        pop.setContentView(popcontent);
        pop.setOutsideTouchable(false);
        pop.setWidth(dropDownView.getWidth());
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
//        lv.setSelectionFromTop();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pop.dismiss();
                dropDownView.setText(dataeaum.get(position));
                listener.itemClick(floag, position);
            }
        });
        return pop;
    }

    private View.OnClickListener selectorClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_city: {
                    List<PackLocalCity> cityList = ZtqCityDB.getInstance().getCityLv1WithoutTJ();
                    List<String> stringList = new ArrayList<>();
                    for (PackLocalCity city : cityList) {
                        stringList.add(city.NAME);
                    }
                    createPopupWindow((TextView) v, stringList, 0, new DrowListClick() {
                        @Override
                        public void itemClick(int floag, int item) {
                            updateSelectorCity(item);
                        }
                    }).showAsDropDown(v);
                    break;
                }
                case R.id.tv_site: {
//                    if(ZtqCityDB.getInstance().isServiceAccessible()) {
                        List<PackLocalCity> cityList = ZtqCityDB.getInstance().getCityLv1WithoutTJ();
                        if (cityList != null && cityList.size() > currentSelectedCityPosition) {
                            final PackLocalCity city = cityList.get(currentSelectedCityPosition);
                            final String area = city.ID;
                            List<PackLocalStation> stationList;
                            if (area.equals("10103")) {
                                stationList = ZtqCityDB.getInstance().getAllStationList();
                            } else {
                                stationList = ZtqCityDB.getInstance().getStationListByArea(area);
                            }
                            List<String> stringList = new ArrayList<>();
                            for (PackLocalStation station : stationList) {
                                stringList.add(station.STATIONNAME);
                            }
                            createPopupWindow((TextView) v, stringList, 0, new DrowListClick() {
                                @Override
                                public void itemClick(int floag, int item) {
                                    updateSelectorSite(area, item);
                                }
                            }).showAsDropDown(v);
                        }
//                    }
                    break;
                }
            }
        }
    };

    /**
     * 更新城市
     * @param position
     */
    private void updateSelectorCity(int position) {
        currentSelectedCityPosition = position;
        List<PackLocalCity> cityList = ZtqCityDB.getInstance().getCityLv1WithoutTJ();
        if(cityList != null && cityList.size() > position) {
            PackLocalCity city = cityList.get(position);
            //tvCity.setText(city.NAME);
            updateSelectorSite(city.ID, 0);
        }
    }

    /**
     * 更新自动站
     * @param area
     */
    private void updateSelectorSite(String area, int position) {
        currentSelectedSitePosition = position;
        List<PackLocalStation> stationList;
        if(area.equals("10103")) {
            stationList = ZtqCityDB.getInstance().getAllStationList();
        } else {
            stationList = ZtqCityDB.getInstance().getStationListByArea(area);
        }
        if(stationList != null && stationList.size() > position) {
            PackLocalStation station = stationList.get(position);
            //tvSite.setText(station.STATIONNAME);
            updateData(station.STATIONNAME);
        }
    }

    private void updateData(String name) {
        if(isLocal) {
            PackLocalStation station = ZtqCityDB.getInstance().getStation(name);
            if (station == null) return;
            PackLocalCity city = ZtqCityDB.getInstance().getCityInfo1_ID(station.AREA);
            if (city == null) return;
            List<PackLocalCity> cityList = ZtqCityDB.getInstance().getCityLv1WithoutTJ();
            for (PackLocalCity c : cityList) {
                if (city.ID.equals(c.ID)) {
                    tvCity.setText(city.NAME);
                    tvSite.setText(name);
                    currentSelectedCityPosition = cityList.indexOf(c);
                    stationName = name;
                    setTitleText(name);
                    control.reqData(control.getCuttentType(), name);
                    break;
                }
            }
        } else {
            stationName = name;
            setTitleText(name);
            control.reqData(control.getCuttentType(), name);
        }
    }
}
