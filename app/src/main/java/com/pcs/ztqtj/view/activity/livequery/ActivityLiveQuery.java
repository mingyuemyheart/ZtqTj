package com.pcs.ztqtj.view.activity.livequery;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.pcs.lib.lib_pcs_v3.control.tool.Util;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCity;
import com.pcs.lib_ztqfj_v2.model.pack.net.column.PackColumnUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.column.PackLiveTypeDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.column.PackLiveTypeUp;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.livequery.AdapterData;
import com.pcs.ztqtj.control.adapter.livequery.AdapterDataButton;
import com.pcs.ztqtj.control.inter.DrowListClick;
import com.pcs.ztqtj.control.tool.LocalDataHelper;
import com.pcs.ztqtj.model.ZtqCityDB;
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

import java.util.ArrayList;
import java.util.List;

/**
 * 实况查询
 */
public class ActivityLiveQuery extends FragmentActivityZtqBase implements OnClickListener {
    private TextView text_title;
    protected FragmentLiveQueryCommon hightTem;
    protected FragmentLiveQueryCommon lowTem;
    protected FragmentLiveQueryCommon rain;
    protected FragmentLiveQueryCommon wind;


    private FragmentRainCountry rainCountry;
    private FragmentTempHightCountry hightCountry;
    private FragmentTempLowCountry lowCountry;
    private FragmentWindCountry windCountry;
    private FragmentDistributionMap fragmentDistributionMap;


    private FragmentLiveQueryCommon cutFragement;
    public PackLocalCity cityinfo;
    private Button data_table;
    protected Button all_city_data;
    private Button data_map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        PcsDataBrocastReceiver.registerReceiver(this, receiver);
        setContentView(R.layout.activity_livequery);
        cityinfo = (PackLocalCity) getIntent().getExtras().getSerializable("city");
        initView();
        initData();
        initEvent();
        setBtnRight(R.drawable.btn_refresh, new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 动画
                // isStartAnim = true;
                // textButton.setClickable(false);
                // Animation animation =
                // AnimationUtils.loadAnimation(ActivityLiveQuery.this,
                // R.anim.rotate_repeat_1000);
                // LinearInterpolator lin = new LinearInterpolator();
                // animation.setInterpolator(lin);
                // textButton.startAnimation(animation);
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
        PcsDataBrocastReceiver.registerReceiver(this, receiver);
        initChangeData();
//        text_title.setText(getResources().getString(R.string.realtime_query));
        text_title.setText("实况查询");
//        checkItemView("雨量");
        checkItem = itemView;
        listItemCheck = 0;
        reqWindItem();
        reqtypelm();
    }

    /**
     * 获取风况小时栏目
     * 福建省
     */
    private void reqWindItem() {
        //		获取风况时间栏目
        PackColumnUp packColumnUp = new PackColumnUp();
        // 预警栏目默认1
        packColumnUp.column_type = column_type;
        PcsDataDownload.addDownload(packColumnUp);

        PackColumnUp packColumnUps = new PackColumnUp();
        packColumnUps.column_type = "8";
        PcsDataDownload.addDownload(packColumnUps);
    }

    private void reqtypelm() {
        showProgressDialog();
        PackLiveTypeUp liveTypeUp = new PackLiveTypeUp();
        liveTypeUp.type = "1";
        PcsDataDownload.addDownload(liveTypeUp);
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
    public PopupWindow createPopupWindow(final TextView dropDownView, final List<String> dataeaum, final int floag,
                                         final DrowListClick listener) {
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
//        lv.setSelectionFromTop();
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
    public void createActivityPopupWindow(Button dropDownView, final int floag, boolean isCheckItem,
                                          final DrowListClick listener) {
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
//                dataList.add("雨量");
//                dataList.add("气温");
//                dataList.add("风况");
//                dataList.add("能见度");
//                dataList.add("相对湿度");
//                dataList.add("气压");
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
//                checkItemAllData(item);
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
        //all_city_data.setVisibility(all_view.VISIBLE);
        PackLocalCity mainCity = ZtqCityDB.getInstance().getCityMain();
        if (mainCity != null) {
            isProvince = !mainCity.isFjCity;
        } else {
            isProvince = false;
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
//                if (hightTem == null) {
//                    hightTem = new FragementHightTem();
//                }
                hightTem = new FragmentHighTemperature();
                fragment = hightTem;
                break;
            case "低温":
//                if (lowTem == null) {
//                    //lowTem = new FragementLowTem();
//                    lowTem = new FragmentLowTemperature();
//                }
                //fragment = lowTem;
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

    private PopupWindow popupWindow;
    private boolean isShowIntro = false;

    /**
     * 弹出底部对话框
     */
    public void openPop() {
        if (isShowIntro) {
            return;
        }
        isShowIntro = !isShowIntro;
        String configMainIntroduction = LocalDataHelper.getIntroduction(this, "live");
        if (TextUtils.isEmpty(configMainIntroduction)) {
            LocalDataHelper.saveIntroduction(this, "live", "live");
            if (popupWindow != null && popupWindow.isShowing()) {
                return;
            }
            View popView = LayoutInflater.from(this).inflate(R.layout.popup_live_introduction, null);
            View rootView = findViewById(R.id.live_root); // 當前頁面的根佈局
            ImageView imgView = (ImageView) popView.findViewById(R.id.imageViewIntroduction);
            popupWindow = new PopupWindow(popView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            setBackgroundAlpha(0.4f);//设置屏幕透明度
            popupWindow.setFocusable(true);// 点击空白处时，隐藏掉pop窗口
            imgView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                }
            });
            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    // popupWindow隐藏时恢复屏幕正常透明度
                    setBackgroundAlpha(1.0f);
                }
            });
            popupWindow.showAtLocation(rootView, Gravity.BOTTOM, 0, (int) data_map.getY());
        }
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha 屏幕透明度0.0-1.0 1表示完全不透明
     */
    public void setBackgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setAttributes(lp);
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        PcsDataBrocastReceiver.registerReceiver(this, receiver);
//    }
//
//    private PcsDataBrocastReceiver receiver = new PcsDataBrocastReceiver() {
//        @Override
//        public void onReceive(String nameStr, String errorStr) {
//            if (nameStr.startsWith(PackYltjHourUp.NAME)) {
//                openPop();
//            }
//        }
//    };


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

//    private void checkItemAllData(int listPosition) {
//        Intent intent = new Intent();
//        switch (listPosition) {
//            case 0:
//                // 全省雨量查询
//                if(rainCountry==null){
//                    rainCountry=new FragmentRainCountry();
//                }
//                intent.setClass(this, nextClass.get("rainCount"));
//                intent.putExtra("title", "全省雨量查询");
//                break;
//            case 1:
//
//                intent.setClass(this, nextClass.get("tempWind"));
//                intent.putExtra("title", "全省高温统计");
//                intent.putExtra("type", "hight_t");
//                break;
//            case 2:
//                intent.setClass(this, nextClass.get("tempWind"));
//                intent.putExtra("title", "全省低温统计");
//                intent.putExtra("type", "low_t");
//                break;
//            case 3:
//                intent.setClass(this, nextClass.get("tempWind"));
//                intent.putExtra("title", "全省风况查询");
//                intent.putExtra("type", "wind");
//                break;
//        }
//        startActivity(intent);
//    }

    private PackLiveTypeDown liveTypeDown;

    private PcsDataBrocastReceiver receiver = new PcsDataBrocastReceiver() {
        @Override
        public void onReceive(String nameStr, String errorStr) {
            if (PackLiveTypeUp.NAME.equals(nameStr)) {
                dismissProgressDialog();
                liveTypeDown = (PackLiveTypeDown) PcsDataManager.getInstance().getNetPack(nameStr);
                if (liveTypeDown == null || liveTypeDown.list_str.size() == 0) {
                    return;
                }
                checkItemView(liveTypeDown.list_str.get(0));
            }
        }
    };

}
