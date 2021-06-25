package com.pcs.ztqtj.view.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pcs.lib.lib_pcs_v3.model.image.ImageFetcher;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCity;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityInfo;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityLocation;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityMain;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.adapter_citymanager.AdapterCityList;
import com.pcs.ztqtj.control.adapter.adapter_citymanager.AdapterCityList.CityListDeleteBtnClick;
import com.pcs.ztqtj.control.inter.InterfaceRefresh;
import com.pcs.ztqtj.control.tool.AutoDownloadWeather;
import com.pcs.ztqtj.control.tool.MyConfigure;
import com.pcs.ztqtj.control.tool.ZtqLocationTool;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.view.activity.ActivityMain;
import com.pcs.ztqtj.view.activity.citylist.ActivityCityList;
import com.pcs.ztqtj.view.myview.MyListView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 管理城市
 */
@SuppressLint("InflateParams")
public class FragmentCityManager extends Fragment implements OnClickListener,InterfaceRefresh {

    // 初始化完成
    private boolean mInitSucc = false;
    /**
     * 城市列表
     */
    private MyListView listview;
    /**
     * 城市列表适配器
     */
    private AdapterCityList adapter;
    /**
     * 列表数据
     */
    private List<PackLocalCity> listCityInfo;
    /**
     * 添加城市按钮
     */
    private ImageView addCityButton;
    private View layoutCityAdd;

    /**
     * 设置最多城市个数
     */
    private final int MAXCITY = 10;

    // 刷新视图
    private InterfaceRefresh mRefreshView;
    // 滑动菜单
    private ImageFetcher mImageFetcher;
    private RelativeLayout rl_family_city;
    private CheckBox mCheckBoxLocation;
    private TextView city_state_info;
    private Button editcity;

    public FragmentCityManager() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_city_manager1, null);
        initView(view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
        initEvent();
        refreshData();
        mInitSucc = true;
    }

    private void initView(View viewRoot) {
        city_state_info = (TextView) viewRoot.findViewById(R.id.city_state_info);
        addCityButton = (ImageView) viewRoot.findViewById(R.id.addcity);
        layoutCityAdd = viewRoot.findViewById(R.id.layout_city_add);
        listview = (MyListView) viewRoot.findViewById(R.id.fracitylistview);
        rl_family_city = (RelativeLayout) viewRoot.findViewById(R.id.rl_family_city);
        // 自动定位复选框
        mCheckBoxLocation = (CheckBox) viewRoot.findViewById(R.id.cb_auto_location);
    }

    /**
     * 初始化數據
     */
    private void initData() {
        if(getActivity() instanceof ActivityMain) {
            mImageFetcher = ((ActivityMain)getActivity()).getImageFetcher();
            mRefreshView = ((ActivityMain)getActivity()).getRefreshView();
        }
        doAutoDownload();
        listCityInfo = new ArrayList<>();
        adapter = new AdapterCityList(getActivity(), listCityInfo, btnClickListener, mImageFetcher);
        listview.setAdapter(adapter);
        mCheckBoxLocation.setChecked(ZtqLocationTool.getInstance().getIsAutoLocation());
    }

    /**
     * 刷新数据
     */
    private void refreshData() {
        listCityInfo.clear();
        // 定位城市
        PackLocalCityLocation packLocation = ZtqLocationTool.getInstance().getLocationCity();
        // 已选城市
        PackLocalCityInfo packChoiced = ZtqCityDB.getInstance().getCurrentCityInfo();
        if (ZtqLocationTool.getInstance().getIsAutoLocation()) {
            // 自动定位，添加定位城市
            listCityInfo.add(packLocation);
            // 去除重复城市
            ZtqCityDB.getInstance().delCityFromList(packChoiced.localCityList, packLocation);
            ZtqCityDB.getInstance().setCurrentCityInfo(packChoiced);
        }

        //列表去重
        for (int i = 0; i < packChoiced.localCityList.size(); i++) {
            boolean addToList = true;
            for (int j = 0; j < listCityInfo.size(); j++) {
                if (listCityInfo.get(j).ID.equals(packChoiced.localCityList.get(i).ID)) {
                    addToList = false;
                    break;
                }
            }
            if (addToList) {
                listCityInfo.add(packChoiced.localCityList.get(i));
            }
        }
        // 当城市个数大于规定的个数是这跳转按钮隐藏，不让跳转
        showAddCityButton();
        adapter.notifyDataSetChanged();
    }

    /**
     * 事件监听
     */
    private void initEvent() {
        editcity= getView().findViewById(R.id.editcity);
        editcity.setOnClickListener(this);
        addCityButton.setOnClickListener(this);
        layoutCityAdd.setOnClickListener(this);
        getView().findViewById(R.id.closefragement).setOnClickListener(this);
        getView().findViewById(R.id.btn_closefragement).setOnClickListener(this);
        listview.setOnItemClickListener(listItemClick);
        rl_family_city.setOnClickListener(this);
        // 自动定位复选框
        mCheckBoxLocation.setOnCheckedChangeListener(mOnChecked);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.editcity:
                // 删除城市
                if (adapter.showDeleteBtn) {
                    closeEditState();
                    editcity.setText("编辑");
                } else {
                    adapter.showDeleteButton(true);
                    editcity.setText("确定");
                }
                break;
            case R.id.layout_city_add:
            case R.id.addcity:
                if (listCityInfo.size() >= MAXCITY) {
                    if (adapter.showDeleteBtn) {
                        adapter.showDeleteButton(false);
                        editcity.setText("编辑");
                    } else {
                        adapter.showDeleteButton(true);
                        editcity.setText("确定");
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    // 跳转到城市列表
                    closeEditState();
                    Intent intent = new Intent(getActivity(), ActivityCityList.class);
                    startActivityForResult(intent, MyConfigure.RESULT_LEFT_CITY_LIST);
                }
                break;
            case R.id.closefragement:
            case R.id.btn_closefragement:
                // 返回按钮
                closeFragment();
                break;
            case R.id.rl_family_city:
                // 亲情城市
//                Intent intent1 = new Intent(getActivity(), ActivityFamilyCity.class);
//                Intent intent1 = new Intent(getActivity(), ActivitySelectCityFromLeft.class);
//                startActivityForResult(intent1, MyConfigure.RESULT_SELECT_CITY);
                break;
        }
    }

    private void closeFragment() {
        if(getActivity() instanceof ActivityMain) {
            ((ActivityMain) getActivity()).showCityManager(false);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MyConfigure.RESULT_LEFT_CITY_LIST && resultCode == Activity.RESULT_OK) {
            if (data.getBooleanExtra("showContent", false)) {
                closeFragment();
            }
        }
        if (requestCode == MyConfigure.RESULT_SELECT_CITY && resultCode == Activity.RESULT_OK) {
            if (data.getBooleanExtra("showContent", false)) {
                closeFragment();
            }
        }
        refreshData();
    }

    private OnCheckedChangeListener mOnChecked = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            switch (buttonView.getId()) {
                case R.id.cb_auto_location:
                    // 自动定位
                    checkAutoLocation(isChecked);
                    break;
            }
        }
    };

    /**
     * 选择城市事件
     */
    private OnItemClickListener listItemClick = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // 选择默认城市
            closeEditState();
            PackLocalCity packCity = (PackLocalCity) adapter.getItem(position);
            if (position == 0 && ZtqLocationTool.getInstance().getIsAutoLocation()) {
                // 定位城市
                if (TextUtils.isEmpty(packCity.ID)) {
                    Toast.makeText(getActivity(), packCity.NAME, Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    ZtqCityDB.getInstance().setCityMain(packCity, true);
                }
            } else {
                // 手选城市
                ZtqCityDB.getInstance().setCityMain(packCity, false);
            }
            AutoDownloadWeather.getInstance().setDefaultCity(packCity);
            AutoDownloadWeather.getInstance().beginMainData();
            // 收缩
            closeFragment();
            // 首页刷新
            FragmentHomeWeather.HomeRefreshParam param = new FragmentHomeWeather.HomeRefreshParam();
            param.isChangedCity = true;
            mRefreshView.refresh(param);
        }
    };

    /**
     * 按钮删除事件
     */
    private CityListDeleteBtnClick btnClickListener = new CityListDeleteBtnClick() {
        @Override
        public void itemOnclick(int item) {
            try {
                PackLocalCity packCity = listCityInfo.get(item);
                AutoDownloadWeather.getInstance().removeWeekCity(packCity);
                listCityInfo.remove(item);
                PackLocalCityInfo localcitylist = ZtqCityDB.getInstance().getCurrentCityInfo();
                localcitylist.removeCity(packCity.ID);
                ZtqCityDB.getInstance().setCurrentCityInfo(localcitylist);
                // 刷新数据
                refreshData();
                showAddCityButton();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * 是否显示添加城市按钮
     */
    private void showAddCityButton() {
        if (listCityInfo.size() >= MAXCITY) {
///          是否显示跳转到全国城市；
            //rl_family_city.setVisibility(View.GONE);
            city_state_info.setText("已设置城市");
            addCityButton.setBackgroundResource(R.drawable.btn_citylist_delete);
        } else {
            addCityButton.setBackgroundResource(R.drawable.btn_citymanager_addctiybtns);
            city_state_info.setText("添加城市");
            //rl_family_city.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 复选框事件：自动定位
     */
    private void checkAutoLocation(boolean isChecked) {
        // 定位城市
        PackLocalCityLocation packLocation = ZtqLocationTool.getInstance().getLocationCity();
        // 首页城市
        PackLocalCityMain packMain = ZtqCityDB.getInstance().getCityMain();
        // 已选城市
        PackLocalCityInfo packChoiced = ZtqCityDB.getInstance().getCurrentCityInfo();
        if (!isChecked && packLocation != null && !TextUtils.isEmpty(packLocation.ID)) {
            // -------未选中，把定位城市加入列表----关闭状态
            if (packLocation.ID.equals(packMain.ID)) {
                // 数据：当前城市
                AutoDownloadWeather.getInstance().setDefaultCity(packLocation);
                AutoDownloadWeather.getInstance().beginMainData();
                if (!ZtqCityDB.getInstance().isCityExists(packChoiced.localCityList, packLocation)) {
                    // 添加城市列表
                    PackLocalCityLocation packTemp = new PackLocalCityLocation();
                    packTemp.copyCity(packLocation);
                    packChoiced.localCityList.add(0, packTemp);
                    ZtqCityDB.getInstance().setCurrentCityInfo(packChoiced);
                    // 数据：添加城市
                    AutoDownloadWeather.getInstance().addWeekCity(packTemp);
                }
            } else {
                if (ZtqCityDB.getInstance().isCityExists(packChoiced.localCityList, packLocation)) {
                    packChoiced.localCityList.remove(0);
                    ZtqCityDB.getInstance().setCurrentCityInfo(packChoiced);
                    AutoDownloadWeather.getInstance().removeWeekCity(packLocation);
                }
                // 保存首页城市
//             ZtqCityDB.getInstance().setCityMain(packLocation, packLocation.isFjCity);
            }
        } else if (isChecked && packLocation != null && !TextUtils.isEmpty(packLocation.ID)) {
            // 选中----开启状态
            // 保存首页城市
            ZtqCityDB.getInstance().setCityMain(packLocation, true);
        }

        // 设置是否自动定位
        ZtqLocationTool.getInstance().setIsAutoLocation(isChecked);
        // 取消编辑
        closeEditState();
        // 刷新
        FragmentHomeWeather.HomeRefreshParam param = new FragmentHomeWeather.HomeRefreshParam();
        param.isChangedCity = true;
        mRefreshView.refresh(param);
    }

    /**
     * 关闭删除按钮
     */
    private void closeEditState() {
        if (adapter.showDeleteBtn) {
            adapter.showDeleteButton(false);
        }
    }

    /**
     * 自动下载数据
     */
    private void doAutoDownload() {
        PackLocalCityInfo pack = ZtqCityDB.getInstance().getCurrentCityInfo();
        // 城市列表
        for (int i = 0; i < pack.localCityList.size(); i++) {
            PackLocalCity city = pack.localCityList.get(i);
            AutoDownloadWeather.getInstance().addWeekCity(city);
        }
    }

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

    @Override
    public void refresh(RefreshParam param) {
        if (!mInitSucc) {
            return;
        }
        doAutoDownload();
        refreshData();
    }
}