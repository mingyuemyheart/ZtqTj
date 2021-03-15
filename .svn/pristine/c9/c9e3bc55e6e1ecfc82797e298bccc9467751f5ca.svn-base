package com.pcs.ztqtj.view.activity.life.travel;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.AdapterTravelCity;
import com.pcs.ztqtj.control.adapter.AdapterTravelCity.FamilyCityListDeleteBtnClick;
import com.pcs.ztqtj.control.adapter.AdapterTravelFragement;
import com.pcs.ztqtj.control.adapter.AdapterTravelViewPager;
import com.pcs.ztqtj.control.tool.MyConfigure;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.view.activity.FragmentActivitySZYBBase;
import com.pcs.ztqtj.view.activity.citylist.ActivitySelectTravelViewList;
import com.pcs.ztqtj.view.activity.web.MyWebView;
import com.pcs.ztqtj.view.myview.LeadPoint;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCity;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalTravelViewInfo;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackTravelWeatherColumnDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackTravelWeatherColumnUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.TravelWeatherColumn;
import com.pcs.lib_ztqfj_v2.model.pack.net.TravelWeatherSubject;
import com.pcs.lib_ztqfj_v2.model.pack.net.hot_tourist_spot.HotTouristSpot;
import com.pcs.lib_ztqfj_v2.model.pack.net.hot_tourist_spot.PackTouristSpotDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.hot_tourist_spot.PackTouristSpotUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.week.PackTravelWeekDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.week.PackTravelWeekUp;

import java.util.ArrayList;
import java.util.List;

/**
 * 旅游气象
 *
 * @author chenjh
 */
public class ActivityTravelView extends FragmentActivitySZYBBase implements
        OnClickListener {
    private final int MAXCITY = 5;
    private ListView listview;
    private List<PackTravelWeekDown> listCityInfo = new ArrayList<PackTravelWeekDown>();
    private ArrayList<HotTouristSpot> touristSoptList = new ArrayList<HotTouristSpot>();
    private int defaultCityItem = 0;
    private MyReceiver receiver = new MyReceiver();
    private TouristSpotReceiver spotReceiver = new TouristSpotReceiver();
    private AdapterTravelCity adapter;
    private PackTravelWeekUp packUp;
    private PackTouristSpotUp touristSpotUp = new PackTouristSpotUp();
    private GridView travel_gridview;
    private PackTouristSpotDown touristSpotDown = new PackTouristSpotDown();
    private AdapterTravelFragement travelAdapter;
    private LeadPoint pointlayout;
    private AdapterTravelViewPager adapterPager = null;
    private List<TravelWeatherColumn> columnList = new ArrayList<TravelWeatherColumn>();
    private PackTravelWeatherColumnDown packColumnDown = new PackTravelWeatherColumnDown();
    private PackTravelWeatherColumnUp packColumnUp = new PackTravelWeatherColumnUp();
    private LinearLayout llSubject = null;
    private RelativeLayout rlSubject = null;
    private TextView tvBookmark = null;
    private boolean flag = false;
    private EditText ss_alertedittext;
    /**
     * 顶部图片导航
     */
    private ViewPager vp = null;
    private int pagerCurrentPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.travel_viewlist_layout);
        setTitleText("旅游气象");
        setBackground(Color.WHITE);
        createImageFetcher();
        initView();
        initEvent();
        initData();

//        setBtnRight(R.drawable.btn_search_nor, new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(ActivityTravelView.this,
//                        ActivitySelectTravelViewList.class);
//                startActivityForResult(intent,
//                        MyConfigure.RESULT_GOTO_TRAVEDETAIL);
//            }
//        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    private void initView() {
        listview = (ListView) findViewById(R.id.listview2);
        travel_gridview = (GridView) findViewById(R.id.travel_gridview);
        llSubject = (LinearLayout) findViewById(R.id.ll_subject);
        rlSubject = (RelativeLayout) findViewById(R.id.rl_subject);
        tvBookmark = (TextView) findViewById(R.id.tv_bookmark);
        ss_alertedittext= (EditText) findViewById(R.id.ss_alertedittext);
        initBannerView();
    }

    private void initEvent() {

        listview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                intentNextActivity(listCityInfo.get(position).cityId,
                        listCityInfo.get(position).cityName);
            }
        });
        travel_gridview.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                intentNextActivity(touristSoptList.get(position).getId(),
                        touristSoptList.get(position).getName());
            }
        });
        tvBookmark.setOnClickListener(this);
        ss_alertedittext.setFocusable(false);
        ss_alertedittext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityTravelView.this,
                        ActivitySelectTravelViewList.class);
                startActivityForResult(intent,
                        MyConfigure.RESULT_GOTO_TRAVEDETAIL);
            }
        });
    }

    private void intentNextActivity(String cityID, String CityName) {
        Intent intent = new Intent(ActivityTravelView.this,
                ActivityTravelDetail.class);
        intent.putExtra("cityId", cityID);
        intent.putExtra("cityName", CityName);
//        Toast.makeText(ActivityTravelView.this,cityID+"--"+CityName,Toast.LENGTH_LONG).show();
        startActivity(intent);
    }

    private void initData() {

        if(!isOpenNet()){
            showToast(getString(R.string.net_err));
            return ;
        }
        PcsDataBrocastReceiver.registerReceiver(this, receiver);
        PcsDataBrocastReceiver.registerReceiver(this, spotReceiver);
        getDefluatData();

        adapter = new AdapterTravelCity(this, listCityInfo, defaultCityItem,
                btnClickListener, getImageFetcher());
        listview.setAdapter(adapter);
        touristSpotUp.area_id = ZtqCityDB.getInstance().getCityMain().ID;
        PcsDataDownload.addDownload(touristSpotUp);

        reqColumn();
        initBanner();
    }

    @SuppressWarnings("deprecation")
    private void initBannerView() {
        vp = (ViewPager) findViewById(R.id.viewpager);
        pointlayout = (LeadPoint) findViewById(R.id.pointlayout);
        vp.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
                pagerCurrentPosition = arg0;
                if (columnList.size() > 1) {
                    pointlayout.setPointSelect(pagerCurrentPosition
                            % columnList.size());
                    addSubjectToLayout(columnList.get(pagerCurrentPosition).subject_list);
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }

    /**
     * 动态添加主题至布局
     *
     * @param subjectList
     */
    private void addSubjectToLayout(List<TravelWeatherSubject> subjectList) {
        llSubject.removeAllViews();
        if (subjectList != null && subjectList.size() > 0) {
           // rlSubject.setVisibility(View.VISIBLE);
            for (final TravelWeatherSubject subject : subjectList) {
                View view = LayoutInflater.from(this).inflate(
                        R.layout.item_travel_subjest, null);
                TextView tv = (TextView) view.findViewById(R.id.tv);
                tv.setText(subject.name);
                LayoutParams params = new LayoutParams(0,
                        LayoutParams.MATCH_PARENT, 1);
                view.setLayoutParams(params);
                tv.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (subject.key_type.equals("1")) {
                            String url = getResources().getString(
                                    R.string.file_download_url)
                                    + subject.link_url;
                            gotoWebView("旅游气象", url);
                        } else if (subject.key_type.equals("tour_subject")) {
                            Intent intent = new Intent(ActivityTravelView.this,
                                    ActivityTravelSubjectMap.class);
                            intent.putExtra("subject", subject);
                            startActivity(intent);
                        }

                    }
                });
                llSubject.addView(view);
            }
        } else {
           // rlSubject.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 获取数据完成
     */
    private void initBanner() {
        dismissProgressDialog();
        pagerCurrentPosition = 0;
        adapterPager = new AdapterTravelViewPager(this, columnList,
                getImageFetcher());
        vp.setAdapter(adapterPager);
        if (columnList.size() == 0) {
            // 如果大小为0的话则不需要计算当前位置
        } else {
            // 不为0则计算当前位置
            pagerCurrentPosition = ((adapterPager.getCount() / columnList
                    .size()) / 2) * columnList.size();
            vp.setCurrentItem(pagerCurrentPosition);
            pointlayout.initPoint(columnList.size());
        }
    }

    private void getDefluatData() {
        listCityInfo.clear();
        PackLocalTravelViewInfo localTravelViewInfo = ZtqCityDB.getInstance()
                .getCurrentTravelViewInfo();
        if (localTravelViewInfo == null) {
            return;
        }
        // 取数据
        for (int i = 0; i < localTravelViewInfo.localViewList.size(); i++) {
            // 加载本地数据
            PackTravelWeekUp up = new PackTravelWeekUp();
            up.setCity(localTravelViewInfo.localViewList.get(i));
            PackTravelWeekDown down = (PackTravelWeekDown) PcsDataManager.getInstance().getNetPack(up.getName());
            if (null == down) {
            } else {
                listCityInfo.add(down);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                // case MyConfigure.RESULT_SELECT_TRAVELVIEW: {
                // PackLocalCity cityinfo = (PackLocalCity) data
                // .getSerializableExtra("cityinfo");
                // addcityinfo(cityinfo);
                // break;
                // }
                case MyConfigure.RESULT_GOTO_TRAVEDETAIL: {
                    PackLocalCity cityinfo = (PackLocalCity) data.getSerializableExtra("cityinfo");
                    // reqNet(cityinfo);
                    addcityinfo(cityinfo);
                    flag = true;
                    break;
                }
            }
        }
    }

    private void gotoWebView(String title, String url) {
        Intent intent = new Intent(ActivityTravelView.this, MyWebView.class);
        intent.putExtra("title", title);
        intent.putExtra("url", url);
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
                intentNextActivity(cityInfo.ID, cityInfo.NAME);
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
     * 按钮删除事件
     */
    private FamilyCityListDeleteBtnClick btnClickListener = new FamilyCityListDeleteBtnClick() {
        @Override
        public void itemOnclick(int item) {
            listCityInfo.remove(item);
            PackLocalTravelViewInfo localcitylist = ZtqCityDB.getInstance()
                    .getCurrentTravelViewInfo();
            localcitylist.localViewList.remove(item);
            localcitylist.defaultPosition = defaultCityItem;
            ZtqCityDB.getInstance().setCurrentTravelViewInfo(localcitylist);
            adapter.notifyDataSetChanged();
        }
    };

    /**
     * 添加城市信息
     *
     * @param pack
     */
    private void addCityInfoToListView(PackTravelWeekDown pack) {
        for (int i = 0; i < listCityInfo.size(); i++) {
            if (listCityInfo.get(i).cityId.equals(pack.cityId)) {
                listCityInfo.set(i, pack);
                adapter.notifyDataSetChanged();
                break;
            }
        }
        dismissProgressDialog();
    }

    /**
     * 保存城市
     *
     * @param cityinfo
     */
    private void saveLocalTravelViewInfo(PackLocalCity cityinfo) {
        PackLocalTravelViewInfo localcitylist = ZtqCityDB.getInstance()
                .getCurrentTravelViewInfo();
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
        packUp = new PackTravelWeekUp();
        packUp.setCity(cityInfo);
        PackTravelWeekDown down = (PackTravelWeekDown) PcsDataManager.getInstance().getNetPack(packUp.getName());
        if (down == null) {
            // 判断是缓存数据中是否已经存在，不存在则网络请求取数据
            PcsDataDownload.addDownload(packUp);
        } else {
            // 取城市信息后解析数据
            down.cityId = cityInfo.ID;
            down.cityName = cityInfo.NAME;
            addCityInfoToListView(down);
            intentNextActivity(down.cityId, down.cityName);
        }
    }

    private void reqColumn() {

        if(!isOpenNet()){
            showToast(getString(R.string.net_err));
            return ;
        }
        packColumnUp = new PackTravelWeatherColumnUp();
        packColumnUp.type = "1";
        PcsDataDownload.addDownload(packColumnUp);
    }

    private boolean isCreatGetData = false;

    /**
     * 网络请求数据,请求默认的九个推荐地方
     */
    private void reqNetDefault() {

        if(!isOpenNet()){
            showToast(getString(R.string.net_err));
            return ;
        }
        showProgressDialog();
        isCreatGetData = true;
        // 默认列表请求数据
        for (int i = 0; i < touristSoptList.size(); i++) {
            packUp = new PackTravelWeekUp();
            packUp.setCity(touristSoptList.get(i).getId(), touristSoptList.get(i).getName());
            PcsDataDownload.addDownload(packUp);
        }
        // // 选择列表请求数据
        for (int i = 0; i < listCityInfo.size(); i++) {
            packUp = new PackTravelWeekUp();
            packUp.setCity(listCityInfo.get(i).cityId, listCityInfo.get(i).cityName);
            PcsDataDownload.addDownload(packUp);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_bookmark:
                Intent intent = new Intent(this, ActivityTravelBookmark.class);
                startActivity(intent);
                break;
        }
    }

    private class TouristSpotReceiver extends PcsDataBrocastReceiver {

        @Override
        public void onReceive(String nameStr, String errorStr) {
            if (!nameStr.contains(PackTouristSpotUp.NAME)) {
                return;
            }
            touristSpotDown = (PackTouristSpotDown) PcsDataManager.getInstance().getNetPack(nameStr);
            if (touristSpotDown == null)
                return;
            touristSoptList = touristSpotDown.getTouristSpots();
            travelAdapter = new AdapterTravelFragement(getApplicationContext(),
                    touristSoptList, getImageFetcher());
            travel_gridview.setAdapter(travelAdapter);
            reqNetDefault();
        }
    }

    private class MyReceiver extends PcsDataBrocastReceiver {
        @Override
        public void onReceive(String name, String errorStr) {
            dismissProgressDialog();
            if (name.indexOf("weeklytq") != -1) {
                if (flag) {
                    PackTravelWeekDown down = (PackTravelWeekDown) PcsDataManager.getInstance().getNetPack(name);
                    if (down == null) {
                        return;
                    }
                    down.cityId = name.substring(name.indexOf("#") + 1,
                            name.indexOf("_"));
                    down.cityName = name.substring(name.indexOf("_") + 1);

                    intentNextActivity(down.cityId, down.cityName);
                    flag = false;
                    // addCityInfoToListView(pack);
                    // if (isCreatGetData) {
                    // getDefluatData();
                    // adapter.notifyDataSetChanged();
                    // }
                }
            }
            if (name.equals(packColumnUp.getName())) {
                packColumnDown = (PackTravelWeatherColumnDown) PcsDataManager.getInstance().getNetPack(name);
                if (packColumnDown == null) {
                    return;
                }
                columnList = packColumnDown.column_list;
                if (columnList != null && columnList.size() > 0) {
                    addSubjectToLayout(columnList.get(0).subject_list);
                }
                initBanner();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(receiver);
        this.unregisterReceiver(spotReceiver);
    }

}