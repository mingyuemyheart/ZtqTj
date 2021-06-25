package com.pcs.ztqtj.view.activity.livequery;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.pcs.lib.lib_pcs_v3.control.tool.Util;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCity;
import com.pcs.lib_ztqfj_v2.model.pack.net.column.PackColumnUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.column.PackLiveTypeDown;
import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.livequery.AdapterData;
import com.pcs.ztqtj.control.adapter.livequery.AdapterDataButton;
import com.pcs.ztqtj.control.inter.DrowListClick;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.util.OkHttpUtil;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
import com.pcs.ztqtj.view.fragment.livequery.FragmentDistributionMap;
import com.pcs.ztqtj.view.fragment.livequery.FragmentLiveQueryCommon;
import com.pcs.ztqtj.view.fragment.livequery.all_country.FragmentRainCountry;
import com.pcs.ztqtj.view.fragment.livequery.all_country.FragmentTempHightCountry;
import com.pcs.ztqtj.view.fragment.livequery.all_country.FragmentTempLowCountry;
import com.pcs.ztqtj.view.fragment.livequery.all_country.FragmentWindCountry;
import com.pcs.ztqtj.view.fragment.livequery.fujian_city.FragmentHighTemperature;
import com.pcs.ztqtj.view.fragment.livequery.fujian_city.FragmentLowTemperature;
import com.pcs.ztqtj.view.fragment.livequery.fujian_city.FragmentRain;
import com.pcs.ztqtj.view.fragment.livequery.fujian_city.FragmentWind;

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
 * 监测预报-实况查询
 */
public class ActivityLiveQuery extends FragmentActivityZtqBase implements OnClickListener {

    private TextView text_title;
    protected FragmentLiveQueryCommon hightTem;
    protected FragmentLiveQueryCommon lowTem;
    protected FragmentLiveQueryCommon rain;
    protected FragmentLiveQueryCommon wind;

    //全省查询-已隐藏
    private FragmentRainCountry rainCountry;
    private FragmentTempHightCountry hightCountry;
    private FragmentTempLowCountry lowCountry;
    private FragmentWindCountry windCountry;
    private FragmentDistributionMap fragmentDistributionMap;
    //全省查询-已隐藏

    private FragmentLiveQueryCommon cutFragement;
    public PackLocalCity cityinfo;
    private Button data_table;
    protected Button all_city_data;
    private Button data_map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_livequery);
        cityinfo = (PackLocalCity) getIntent().getExtras().getSerializable("city");
        initView();
        initData();
        initEvent();
        setBtnRight(R.drawable.btn_refresh, new OnClickListener() {
            @Override
            public void onClick(View v) {
                cutFragement.refleshData();
            }
        });
    }

    private void initEvent() {
        data_table.setOnClickListener(this);
        all_city_data.setOnClickListener(this);
        data_map.setOnClickListener(this);
    }

    private void initData() {
        initChangeData();
        text_title.setText("实况查询");
        checkItem = itemView;
        listItemCheck = 0;
        okHttpFycxLm();
    }

    private void initView() {
        text_title = (TextView) findViewById(R.id.text_title);
        data_table = (Button) findViewById(R.id.data_table);
        all_city_data = (Button) findViewById(R.id.all_city_data);
        data_map = (Button) findViewById(R.id.data_map);
    }

    /**
     * 创建下拉选择列表
     */
    public PopupWindow createPopupWindow(final TextView dropDownView, final List<String> dataeaum, final int floag, final DrowListClick listener) {
        AdapterData dataAdapter = new AdapterData(ActivityLiveQuery.this, dataeaum);
        View popcontent = LayoutInflater.from(ActivityLiveQuery.this).inflate(R.layout.pop_list_layout, null);
        ListView lv = (ListView) popcontent.findViewById(R.id.mylistviw);
        lv.setAdapter(dataAdapter);
        final PopupWindow pop = new PopupWindow(ActivityLiveQuery.this);
        pop.setContentView(popcontent);
        pop.setOutsideTouchable(false);
        pop.setWidth(dropDownView.getWidth());
        // 调整下拉框长度
        int screenHight = Util.getScreenHeight(ActivityLiveQuery.this);
        if (dataeaum.size() < 9) {
            pop.setHeight(LayoutParams.WRAP_CONTENT);
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
        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pop.dismiss();
                dropDownView.setText(dataeaum.get(position));
                listener.itemClick(floag, position);
            }
        });
        return pop;
    }

    private int listItemCheck = 0;
    private int checkItem = 0;//a默认选中的是第一个栏目的第一条0

    /**
     * 创建下拉选择列表
     */
    public void createActivityPopupWindow(Button dropDownView, final int floag, boolean isCheckItem, final DrowListClick listener) {
        AdapterDataButton dataAdapter = new AdapterDataButton(dataList);
        if (isCheckItem) {
            dataAdapter.setCheckItem(listItemCheck);
        } else {
            dataAdapter.setCheckItem(-1);
        }
        View popcontent = LayoutInflater.from(ActivityLiveQuery.this).inflate(R.layout.pop_list_layout, null);
        ListView lv = (ListView) popcontent.findViewById(R.id.mylistviw);
        lv.setBackgroundResource(R.drawable.btn_livequery_middle_normal);
        lv.setAdapter(dataAdapter);
        final PopupWindow pop = new PopupWindow(ActivityLiveQuery.this);
        pop.setContentView(popcontent);
        pop.setOutsideTouchable(true);
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setWidth(dropDownView.getWidth());
        pop.setHeight(LayoutParams.WRAP_CONTENT);
        pop.setFocusable(true);
        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pop.dismiss();
                listener.itemClick(floag, position);
            }
        });
        int offY = (int) dropDownView.getY() + dropDownView.getHeight() + 2;
        int vX = (int) dropDownView.getX();
        pop.showAtLocation(dropDownView, Gravity.BOTTOM | Gravity.LEFT, vX, offY);
    }

    private List<String> dataList = new ArrayList<>();

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.data_table:
                dataList.clear();
                dataList.add("雨量");
                dataList.add("高温");
                dataList.add("低温");
                dataList.add("风况");
                if (checkItem == itemData) {
                    createActivityPopupWindow(data_table, itemData, true, itemListener);
                } else {
                    createActivityPopupWindow(data_table, itemData, false, itemListener);
                }
                break;
            case R.id.all_city_data:
                dataList.clear();
                dataList.add("雨量");
                dataList.add("高温");
                dataList.add("低温");
                dataList.add("风况");
                if (checkItem == itemAllData) {
                    createActivityPopupWindow(all_city_data, itemAllData, true, itemListener);
                } else {
                    createActivityPopupWindow(all_city_data, itemAllData, false, itemListener);
                }
                break;
            case R.id.data_map:
                dataList.clear();
                dataList.addAll(liveTypeDown.list_str);
                if (checkItem == itemView) {
                    createActivityPopupWindow(data_map, itemView, true, itemListener);
                } else {
                    createActivityPopupWindow(data_map, itemView, false, itemListener);
                }
                break;
        }
    }

    private final int itemData = 100, itemAllData = 200, itemView = 300;
    private DrowListClick itemListener = new DrowListClick() {
        @Override
        public void itemClick(int floag, int item) {
            listItemCheck = item;//列表选中栏目
            checkItem = floag;
            checkQueryItemView(floag, item);
            if (floag == itemData) {
                checkItem(itemData);
                FragmentTransaction tran = getSupportFragmentManager().beginTransaction();
                cutFragement = getCheckItemFrament(dataList.get(item));
                tran.replace(R.id.fragment, cutFragement);
                tran.commit();
            } else if (floag == itemAllData) {
                checkItem(itemAllData);
                FragmentTransaction tran = getSupportFragmentManager().beginTransaction();
                cutFragement = checkItemAllData(dataList.get(item));
                tran.replace(R.id.fragment, cutFragement);
                tran.commit();
            } else if (floag == itemView) {
                checkItem(itemView);
                checkItemView(dataList.get(item));
            }
        }
    };

    public void initChangeData() {
        PackLocalCity mainCity = ZtqCityDB.getInstance().getCityMain();
        if (mainCity != null) {
            isProvince = mainCity.isFjCity;
        }
        column_type = "10";
    }

    public boolean isProvince = false;//全国需要改变
    protected String column_type = "10";//风况栏目，默认福建8，全国9

    public FragmentLiveQueryCommon getCheckItemFrament(String name) {
        FragmentLiveQueryCommon fragment = null;
        switch (name) {
            case "雨量":
                if (rain == null) {
                    rain = new FragmentRain();
                }
                fragment = rain;
                break;
            case "高温":
                hightTem = new FragmentHighTemperature();
                fragment = hightTem;
                break;
            case "低温":
                lowTem = new FragmentLowTemperature();
                fragment = lowTem;
                break;
            case "风况":
                if (wind == null) {
                    wind = new FragmentWind();
                }
                fragment = wind;
                break;
        }
        return fragment;
    }

    private FragmentLiveQueryCommon checkItemAllData(String name) {
        switch (name) {
            case "雨量":
                // 全省雨量查询
                rainCountry = new FragmentRainCountry();
                return rainCountry;
            case "高温":
                hightCountry = new FragmentTempHightCountry();
                return hightCountry;
            case "低温":
                lowCountry = new FragmentTempLowCountry();
                return lowCountry;
            case "风况":
                windCountry = new FragmentWindCountry();
                return windCountry;
        }
        return null;
    }

    /***
     * 修改标题
     * @param item
     * @param position
     */
    private void checkQueryItemView(int item, int position) {
        if (dataList.size() > position) {
            if (item == itemData) {
                setTitleText(dataList.get(position) + "查询");
            } else if (item == itemAllData) {
                setTitleText("全省" + dataList.get(position) + "查询");
            } else if (item == itemView) {
                setTitleText(dataList.get(position) + "分布图");
            } else {
                setTitleText("风雨查询");
            }
        }
    }

    /**
     * 设置添加屏幕的背景透明度
     * @param bgAlpha 屏幕透明度0.0-1.0 1表示完全不透明
     */
    public void setBackgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setAttributes(lp);
    }

    private void checkItem(int item) {
        switch (item) {
            case itemData:
                data_table.setBackgroundResource(R.drawable.bg_livequerytitle);
                all_city_data.setBackgroundColor(getResources().getColor(R.color.livequery_buttom_pop));
                data_map.setBackgroundColor(getResources().getColor(R.color.livequery_buttom_pop));
                break;
            case itemAllData:
                all_city_data.setBackgroundResource(R.drawable.bg_livequerytitle);
                data_table.setBackgroundColor(getResources().getColor(R.color.livequery_buttom_pop));
                data_map.setBackgroundColor(getResources().getColor(R.color.livequery_buttom_pop));
                break;
            case itemView:
                data_map.setBackgroundResource(R.drawable.bg_livequerytitle);
                data_table.setBackgroundColor(getResources().getColor(R.color.livequery_buttom_pop));
                all_city_data.setBackgroundColor(getResources().getColor(R.color.livequery_buttom_pop));
                break;
        }
    }

    private PackLiveTypeDown liveTypeDown;

    private void okHttpFycxLm() {
        showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param  = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    String json = param.toString();
                    final String url = CONST.BASE_URL+"fycx_lm";
                    Log.e("fycx_lm", url);
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
                            Log.e("fycx_lm", result);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dismissProgressDialog();
                                    try {
                                        JSONObject obj = new JSONObject(result);
                                        if (!obj.isNull("b")) {
                                            JSONObject bobj = obj.getJSONObject("b");
                                            if (!bobj.isNull("fycx_lm")) {
                                                JSONObject fycx_lm = bobj.getJSONObject("fycx_lm");
                                                if (!TextUtils.isEmpty(fycx_lm.toString())) {
                                                    dismissProgressDialog();
                                                    liveTypeDown = new PackLiveTypeDown();
                                                    liveTypeDown.fillData(fycx_lm.toString());
                                                    if (liveTypeDown == null || liveTypeDown.list_str.size() == 0) {
                                                        return;
                                                    }
                                                    checkItemView(liveTypeDown.list_str.get(0));
                                                }
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
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

    private void checkItemView(String name) {
        String type = "";
        switch (name) {
            case "雨量":
                type = "rain";
                break;
            case "气温":
                type = "temp";
                break;
            case "风况":
                type = "wind";
                break;
            case "相对湿度":
                type = "humidity";
                break;
            case "能见度":
                type = "visibility";
                break;
            case "气压":
                type = "pressure";
                break;
        }
        Bundle bundle = new Bundle();
        bundle.putString("type", type);
        bundle.putBoolean("isProvince", isProvince);
        if (fragmentDistributionMap == null || fragmentDistributionMap != cutFragement) {
            fragmentDistributionMap = new FragmentDistributionMap();
            fragmentDistributionMap.setArguments(bundle);
            FragmentTransaction tran = getSupportFragmentManager().beginTransaction();
            cutFragement = fragmentDistributionMap;
            tran.replace(R.id.fragment, cutFragement);
            tran.commit();
        } else {
            fragmentDistributionMap.refreshView(type);
        }
    }

}
