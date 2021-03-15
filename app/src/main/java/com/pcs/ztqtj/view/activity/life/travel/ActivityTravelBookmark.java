package com.pcs.ztqtj.view.activity.life.travel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.AdapterTravelBookmarks;
import com.pcs.ztqtj.control.adapter.AdapterTravelBookmarks.OnClickDeleteButtonListener;
import com.pcs.ztqtj.control.adapter.AdapterTravelBookmarks.OnClickItemListener;
import com.pcs.ztqtj.control.tool.MyConfigure;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.view.activity.citylist.ActivitySelectTravelViewList;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCity;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalTravelViewInfo;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackTravelWeatherUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.week.PackTravelWeekDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.week.PackTravelWeekUp;

import java.util.ArrayList;
import java.util.List;

/**
 * 旅游气象 - 我的收藏夹
 *
 * @author tya
 */
public class ActivityTravelBookmark extends FragmentActivityZtqBase {

    // UI

    /**
     * 我的收藏夹列表
     */
    private GridView gridBookmark = null;

    // 数据

    /**
     * 广播
     */
    private MyReceiver receiver = new MyReceiver();

    private AdapterTravelBookmarks adapterBookmark = null;

    /**
     * 城市列表
     */
    private List<PackTravelWeekDown> listCityInfo = new ArrayList<PackTravelWeekDown>();

    /**
     * 一周天气上传包
     */
    private PackTravelWeekUp packWeekUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_bookmark);
        setTitleText(R.string.my_bookmark);
        createImageFetcher();
        initView();
        initEvent();
        initData();

        setBtnRight(R.drawable.btn_search_nor, new OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoSelectCity();
            }
        });
        setBackground(this.getResources().getColor(R.color.bg_bookmark));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(receiver);
    }

    private boolean toDetail = false;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case MyConfigure.RESULT_SELECT_TRAVELVIEW:
                if (resultCode == Activity.RESULT_OK) {
                    PackLocalCity cityinfo = (PackLocalCity) data.getSerializableExtra("cityinfo");
                    addcityinfo(cityinfo);
                    toDetail = true;

                } else {
                    // finish();
                }
                break;
        }
    }

    private void initView() {
        gridBookmark = (GridView) findViewById(R.id.grid_bookmark);
    }

    private void initEvent() {

    }

    private void initData() {
        PcsDataBrocastReceiver.registerReceiver(this, receiver);
        getDefaultData();
        updateAllCity();
        adapterBookmark = new AdapterTravelBookmarks(this, listCityInfo,
                getImageFetcher());
        adapterBookmark.setOnClickItemListener(new OnClickItemListener() {

            @Override
            public void onItemClick(int position, boolean isAdd) {
                if (isAdd) {
                    gotoSelectCity();
                } else {
                    PackTravelWeekDown pack = listCityInfo.get(position);
                    gotoCityDetail(pack.cityId, pack.cityName);
                }
            }
        });
        adapterBookmark
                .setOnClickDeleteButtonListener(new OnClickDeleteButtonListener() {

                    @Override
                    public void onDelete(int position) {
                        listCityInfo.remove(position);
                        PackLocalTravelViewInfo localcitylist = ZtqCityDB
                                .getInstance().getCurrentTravelViewInfo();
                        localcitylist.localViewList.remove(position);
                        // localcitylist.defaultPosition = defaultCityItem;
                        ZtqCityDB.getInstance().setCurrentTravelViewInfo(localcitylist);
                        adapterBookmark.notifyDataSetChanged();
                    }
                });
        gridBookmark.setAdapter(adapterBookmark);
    }

    /**
     * 跳转至选择景点页面
     */
    private void gotoSelectCity() {
        Intent intent = new Intent(ActivityTravelBookmark.this,
                ActivitySelectTravelViewList.class);
        startActivityForResult(intent, MyConfigure.RESULT_SELECT_TRAVELVIEW);
    }

    /**
     * 跳转至景点详细页面
     *
     * @param cityID
     * @param CityName
     */
    private void gotoCityDetail(String cityID, String CityName) {
        Intent intent = new Intent(ActivityTravelBookmark.this,
                ActivityTravelDetail.class);
        intent.putExtra("cityId", cityID);
        intent.putExtra("cityName", CityName);
        startActivity(intent);
    }

    /**
     * 向列表中添加城市并刷新
     *
     * @param cityInfo
     */
    private void addcityinfo(PackLocalCity cityInfo) {
        for (int i = 0; i < listCityInfo.size(); i++) {
            if (listCityInfo.get(i).cityId.equals(cityInfo.ID)) {
                // 列表中已经存在值
                toDetail = false;
                gotoCityDetail(cityInfo.ID, cityInfo.NAME);
                return;
            }
        }

        // 不存在则保存，并添加
        PackTravelWeekDown packDown = new PackTravelWeekDown();
        packDown.cityName = cityInfo.NAME;
        packDown.cityId = cityInfo.ID;
        /**
         * 如果缓存列表大小等于8时，则删除第一个然后加入一个新的数据以保持列表大小不超过8
         */
        if (listCityInfo.size() >= 8) {
            listCityInfo.remove(0);
        }
        listCityInfo.add(packDown);
        saveLocalTravelViewInfo(cityInfo);
        reqNet(cityInfo);
    }

    /**
     * 保存城市
     *
     * @param cityinfo
     */
    private void saveLocalTravelViewInfo(PackLocalCity cityinfo) {
        PackLocalTravelViewInfo localcitylist = ZtqCityDB.getInstance()
                .getCurrentTravelViewInfo();
        if (localcitylist == null) {
            localcitylist = new PackLocalTravelViewInfo();
        }

        for (int i = 0; i < localcitylist.localViewList.size(); i++) {
            if (localcitylist.localViewList.get(i).ID.equals(cityinfo.ID)) {
                return;
            }
        }

        // 如果缓存列表大小等于8时，则删除第一个然后加入一个新的数据以保持列表大小不超过8
        if (localcitylist.localViewList.size() >= 8) {
            localcitylist.localViewList.remove(0);
        }

        localcitylist.localViewList.add(cityinfo);
        ZtqCityDB.getInstance().setCurrentTravelViewInfo(localcitylist);
    }

    /**
     * 网络请求数据
     *
     * @param cityInfo
     */
    private void reqNet(PackLocalCity cityInfo) {

        if(!isOpenNet()){
            showToast(getString(R.string.net_err));
            return ;
        }
        showProgressDialog();
        packWeekUp = new PackTravelWeekUp();
        packWeekUp.setCity(cityInfo);
        PcsDataDownload.addDownload(packWeekUp);

        // 请求景点信息
        PackTravelWeatherUp travelWeatherUp = new PackTravelWeatherUp();
        travelWeatherUp.area = cityInfo.ID;
        PcsDataDownload.addDownload(travelWeatherUp);

//        PackTravelWeekDown down = (PackTravelWeekDown) PcsDataManager.getInstance().getNetPack(packWeekUp.getName());
//        if (down == null) {
//            // 判断是缓存数据中是否已经存在，不存在则网络请求取数据
//            PcsDataDownload.addDownload(packWeekUp);
//        } else {
//            // 取城市信息后解析数据
//            down.cityId = cityInfo.ID;
//            down.cityName = cityInfo.NAME;
//            if (toDetail) {
//                toDetail = false;
//                gotoCityDetail(cityInfo.ID, cityInfo.NAME);
//            }
//            addCityInfoToListView(down);
//        }
    }

    /**
     * 添加城市信息
     *
     * @param pack
     */
    private void addCityInfoToListView(PackTravelWeekDown pack) {
        for (int i = 0; i < listCityInfo.size(); i++) {
            if (listCityInfo.get(i).cityId.equals(pack.cityId)) {
                listCityInfo.set(i, pack);
                adapterBookmark.notifyDataSetChanged();
                break;
            }
        }
        dismissProgressDialog();
    }

    private void getDefaultData() {
        listCityInfo.clear();
        PackLocalTravelViewInfo localTravelViewInfo = ZtqCityDB.getInstance().getCurrentTravelViewInfo();
        if (localTravelViewInfo == null) {
            return;
        }
        // 取数据
        for (int i = 0; i < localTravelViewInfo.localViewList.size(); i++) {
            // 加载本地数据
            PackTravelWeekUp up = new PackTravelWeekUp();
            up.setCity(localTravelViewInfo.localViewList.get(i));
            PackTravelWeekDown down = (PackTravelWeekDown) PcsDataManager.getInstance().getNetPack(up.getName());
            down.cityName = localTravelViewInfo.localViewList.get(i).NAME;
            down.cityId = localTravelViewInfo.localViewList.get(i).ID;
            if (down == null) {
            } else {
                listCityInfo.add(down);
            }
        }
    }

    private void updateAllCity() {
        PackLocalTravelViewInfo localTravelViewInfo = ZtqCityDB.getInstance().getCurrentTravelViewInfo();
        if (localTravelViewInfo == null) {
            return;
        }
        for (PackLocalCity city : localTravelViewInfo.localViewList) {
            reqNet(city);
        }
    }

    /**
     * 广播
     *
     * @author tya
     */
    private class MyReceiver extends PcsDataBrocastReceiver {
        @Override
        public void onReceive(String nameStr, String errorStr) {
            if (TextUtils.isEmpty(nameStr)) {
                return;
            }
            if (!TextUtils.isEmpty(errorStr)) {
                return;
            }
            if (packWeekUp != null && nameStr.equals(packWeekUp.getName())) {
                dismissProgressDialog();
                PackTravelWeekDown down = (PackTravelWeekDown) PcsDataManager.getInstance().getNetPack(nameStr);
                if (down == null) {
                    return;
                }
                down.cityId = nameStr.substring(nameStr.indexOf("#") + 1, nameStr.indexOf("_"));
                down.cityName = nameStr.substring(nameStr.indexOf("_") + 1);
                getDefaultData();
                adapterBookmark.notifyDataSetChanged();
                if (toDetail) {
                    toDetail = false;
                    gotoCityDetail(down.cityId, down.cityName);
                }
                addCityInfoToListView(down);
            }
        }
    }

}
