package com.pcs.ztqtj.view.activity.citylist;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.AdapterFamilySelectCityExpandableListView;
import com.pcs.ztqtj.control.tool.AutoDownloadWeather;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
import com.pcs.ztqtj.view.fragment.citylist.FragmentCountryList;
import com.pcs.ztqtj.view.fragment.citylist.FragmentSHHotCity;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCity;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityInfo;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityMain;

/**
 * 添加城市
 */
public class ActivityCityList extends FragmentActivityZtqBase implements CityChoicableImpl {

    private RadioButton rbSh,rbContry;
    private RadioGroup radioGroup;
    private EditText etSearch;
    private View layoutDefault, layoutSearch;
    private ExpandableListView selectCityListview;
    private boolean from_home;
    // 是否是添加城市
    private boolean isAddCity = true;
    private AdapterFamilySelectCityExpandableListView adapterSearch;
    private boolean isSingleCityList = false;
    private RelativeLayout rel_fragment,rel_et;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_list);
        initViews();
        setTitleText("添加城市");
        initEventsAndDatas();
    }

    private void initViews() {
        radioGroup = (RadioGroup) findViewById(R.id.radgroup);
        rbSh = (RadioButton) findViewById(R.id.rb_city);
        rbContry = (RadioButton) findViewById(R.id.rb_country);
        etSearch = (EditText) findViewById(R.id.et_search);
        layoutDefault = findViewById(R.id.layout_default);
        layoutSearch = findViewById(R.id.layout_search);
        rel_fragment= (RelativeLayout) findViewById(R.id.fragment);
        rel_et= (RelativeLayout) findViewById(R.id.rel_et);
        selectCityListview = (ExpandableListView) findViewById(R.id.ss_alertgridview);
    }

    private void initEventsAndDatas() {
        from_home = getIntent().getBooleanExtra("home_to_add", false);
        isAddCity = getIntent().getBooleanExtra("add_city", true);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_city:
                        layoutSearch.setVisibility(View.GONE);
                        rel_fragment.setVisibility(View.VISIBLE);
                        replaceFragment(0);
                        etSearch.setText("");
                        break;
                    case R.id.rb_country:
                        layoutSearch.setVisibility(View.GONE);
//                        rel_fragment.setVisibility(View.VISIBLE);
                        replaceFragment(1);
                        etSearch.setText("");
                        break;
                }
            }
        });
        etSearch.addTextChangedListener(textWatcher);
        adapterSearch = new AdapterFamilySelectCityExpandableListView(this);
        selectCityListview.setAdapter(adapterSearch);
        selectCityListview.setOnChildClickListener(new listChildOnClick());
        isSingleCityList = getIntent().getBooleanExtra("isSingleCityList", false);
        // 是否是单个城市列表
        if(!isSingleCityList) {
            radioGroup.setVisibility(View.VISIBLE);
            replaceFragment(0);
        } else {
            radioGroup.setVisibility(View.GONE);
            replaceFragment(1);
        }

    }

    private void replaceFragment(int index) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = null;
        switch (index) {
            case 0:
                fragment = new FragmentSHHotCity();
                break;
            case 1:
                fragment = new FragmentCountryList();
                Bundle bundle = new Bundle();
                bundle.putBoolean("isSingleCityList", isSingleCityList);
                fragment.setArguments(bundle);
                break;
        }
        transaction.replace(R.id.fragment, fragment).commit();
    }

    @Override
    public void choiceCity(PackLocalCity cityInfo, PackLocalCity parent) {
        if(isAddCity) {
            onChoiceCity(cityInfo, parent);
        } else {
            onChoiceCityWithoutAddCity(cityInfo, parent);
        }
    }

    /**
     * 选择城市后做处理
     *
     * @param cityInfo
     */
    protected void onChoiceCity(PackLocalCity cityInfo, PackLocalCity parent) {
        Intent intent = getIntent();
        // 不显示侧边栏
        intent.putExtra("showContent", true);
        addcityinfo(cityInfo);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void onChoiceCityWithoutAddCity(PackLocalCity cityInfo, PackLocalCity parent) {
        Intent intent = getIntent();
        intent.putExtra("city_info", cityInfo);
        intent.putExtra("parent", parent);
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * 向列表中添加城市并刷新
     *
     * @param cityInfo
     */
    public void addcityinfo(PackLocalCity cityInfo) {
        PackLocalCityMain cityMain = ZtqCityDB.getInstance().getCityMain();
        if (cityMain != null && cityMain.ID.equals(cityInfo.ID)) {
            // 该城市已经添加，无需重复操作。
            ZtqCityDB.getInstance().setCityMain(cityInfo, false);
            AutoDownloadWeather.getInstance().setDefaultCity(cityInfo);
            AutoDownloadWeather.getInstance().beginMainData();
            return;
        }
        PackLocalCityInfo packCityInfo = ZtqCityDB.getInstance().getCurrentCityInfo();
        for (int i = 0; i < packCityInfo.localCityList.size(); i++) {
            if (packCityInfo.localCityList.get(i).ID.equals(cityInfo.ID)) {
                if (!packCityInfo.localCityList.get(i).NAME.equals(cityInfo.NAME)) {
                    packCityInfo.localCityList.set(i, cityInfo);
                    ZtqCityDB.getInstance().setCurrentCityInfo(packCityInfo);
                }
                ZtqCityDB.getInstance().setCityMain(cityInfo, false);
                AutoDownloadWeather.getInstance().setDefaultCity(cityInfo);
                AutoDownloadWeather.getInstance().beginMainData();
                Log.i("z", "in for equals id return;");
                return;
            }
        }
        packCityInfo.localCityList.add(cityInfo);
        Log.i("z", "add localCityList  after list size=" + packCityInfo.localCityList.size());
        ZtqCityDB.getInstance().setCurrentCityInfo(packCityInfo);
        // 设置首页城市
        ZtqCityDB.getInstance().setCityMain(cityInfo, false);
        AutoDownloadWeather.getInstance().setDefaultCity(cityInfo);
        AutoDownloadWeather.getInstance().beginMainData();
        AutoDownloadWeather.getInstance().addWeekCity(cityInfo);
    }

    private void exitApp() {
        new AlertDialog.Builder(this)
                .setTitle("提示:")
                .setMessage("还没选择城市，是否要退出程序？")
                .setPositiveButton("确认退出",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                                Intent it = new Intent();
                                it.putExtra("finish", true);
                                setResult(RESULT_OK, it);
                                finish();
                            }
                        })
                .setNegativeButton("继续选择",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
    }

    @Override
    public void onBackPressed() {
        if (from_home) {
            exitApp();
        } else {
            finish();
        }
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (null != s && s.length() != 0) {
                layoutSearch.setVisibility(View.VISIBLE);
//                layoutDefault.setVisibility(View.GONE);
                rel_fragment.setVisibility(View.GONE);
                //arrange(cs);
                adapterSearch.setSearchStr(s.toString());
                // 控制展开
                for (int i = 0; i < adapterSearch.getGroupCount(); i++) {
                    if (i == adapterSearch.getGroupCount() - 1) {
                        // 展开最后一个
                        selectCityListview.expandGroup(i);
                    } else {
                        selectCityListview.collapseGroup(i);
                    }
                }
            } else {
                layoutSearch.setVisibility(View.GONE);
                rel_fragment.setVisibility(View.VISIBLE);
                adapterSearch.notifyDataSetChanged();
            }
        }
    };

    private class listChildOnClick implements ExpandableListView.OnChildClickListener {

        @Override
        public boolean onChildClick(ExpandableListView parent, View v,
                                    int groupPosition, int childPosition, long id) {
            PackLocalCity cityinfo = (PackLocalCity) adapterSearch.getChild(groupPosition, childPosition);
            // 城市选择后的事件处理
            choiceCity(cityinfo, cityinfo);
            return false;
        }
    }
}
