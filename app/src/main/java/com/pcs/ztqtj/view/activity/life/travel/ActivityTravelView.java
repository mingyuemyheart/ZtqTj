package com.pcs.ztqtj.view.activity.life.travel;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
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
import android.widget.TextView;

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
import com.pcs.lib_ztqfj_v2.model.pack.net.week.PackTravelWeekDown;
import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.AdapterTravelCity;
import com.pcs.ztqtj.control.adapter.AdapterTravelCity.FamilyCityListDeleteBtnClick;
import com.pcs.ztqtj.control.adapter.AdapterTravelFragement;
import com.pcs.ztqtj.control.adapter.AdapterTravelViewPager;
import com.pcs.ztqtj.control.tool.MyConfigure;
import com.pcs.ztqtj.control.tool.utils.TextUtil;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.util.OkHttpUtil;
import com.pcs.ztqtj.view.activity.FragmentActivitySZYBBase;
import com.pcs.ztqtj.view.activity.citylist.ActivitySelectTravelViewList;
import com.pcs.ztqtj.view.activity.web.MyWebView;
import com.pcs.ztqtj.view.myview.LeadPoint;

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
 * 生活气象-旅游气象
 */
public class ActivityTravelView extends FragmentActivitySZYBBase implements OnClickListener {

    private ListView listview;
    private List<PackTravelWeekDown> listCityInfo = new ArrayList<PackTravelWeekDown>();
    private ArrayList<HotTouristSpot> touristSoptList = new ArrayList<HotTouristSpot>();
    private int defaultCityItem = 0;
    private MyReceiver receiver = new MyReceiver();
    private AdapterTravelCity adapter;
    private GridView travel_gridview;
    private PackTouristSpotDown touristSpotDown = new PackTouristSpotDown();
    private AdapterTravelFragement travelAdapter;
    private LeadPoint pointlayout;
    private AdapterTravelViewPager adapterPager = null;
    private List<TravelWeatherColumn> columnList = new ArrayList<TravelWeatherColumn>();
    private PackTravelWeatherColumnDown packColumnDown = new PackTravelWeatherColumnDown();
    private PackTravelWeatherColumnUp packColumnUp = new PackTravelWeatherColumnUp();
    private LinearLayout llSubject = null;
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
                Intent intent = new Intent(ActivityTravelView.this, ActivitySelectTravelViewList.class);
                startActivityForResult(intent, MyConfigure.RESULT_GOTO_TRAVEDETAIL);
            }
        });
    }

    private void intentNextActivity(String cityID, String CityName) {
        Intent intent = new Intent(ActivityTravelView.this, ActivityTravelDetail.class);
        intent.putExtra("cityId", cityID);
        intent.putExtra("cityName", CityName);
        startActivity(intent);
    }

    private void initData() {
        if(!isOpenNet()){
            showToast(getString(R.string.net_err));
            return ;
        }
        PcsDataBrocastReceiver.registerReceiver(this, receiver);
        getDefluatData();

        adapter = new AdapterTravelCity(this, listCityInfo, defaultCityItem, btnClickListener, getImageFetcher());
        listview.setAdapter(adapter);
        okHttpLyCityList();
        okHttpBanner();
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
                    pointlayout.setPointSelect(pagerCurrentPosition % columnList.size());
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
     * @param subjectList
     */
    private void addSubjectToLayout(List<TravelWeatherSubject> subjectList) {
        llSubject.removeAllViews();
        if (subjectList != null && subjectList.size() > 0) {
            for (final TravelWeatherSubject subject : subjectList) {
                View view = LayoutInflater.from(this).inflate(R.layout.item_travel_subjest, null);
                TextView tv = (TextView) view.findViewById(R.id.tv);
                tv.setText(subject.name);
                LayoutParams params = new LayoutParams(0, LayoutParams.MATCH_PARENT, 1);
                view.setLayoutParams(params);
                tv.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (subject.key_type.equals("1")) {
                            String url = getResources().getString(
                                    R.string.file_download_url) + subject.link_url;
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
        }
    }

    /**
     * 获取数据完成
     */
    private void initBanner() {
        dismissProgressDialog();
        pagerCurrentPosition = 0;
        adapterPager = new AdapterTravelViewPager(this, columnList, getImageFetcher());
        vp.setAdapter(adapterPager);
        if (columnList.size() == 0) {
            // 如果大小为0的话则不需要计算当前位置
        } else {
            // 不为0则计算当前位置
            pagerCurrentPosition = ((adapterPager.getCount() / columnList.size()) / 2) * columnList.size();
            vp.setCurrentItem(pagerCurrentPosition);
            pointlayout.initPoint(columnList.size());
        }
    }

    private void getDefluatData() {
        listCityInfo.clear();
        PackLocalTravelViewInfo localTravelViewInfo = ZtqCityDB.getInstance().getCurrentTravelViewInfo();
        if (localTravelViewInfo == null) {
            return;
        }
        // 取数据
        for (int i = 0; i < localTravelViewInfo.localViewList.size(); i++) {
            PackLocalCity packLocalCity = localTravelViewInfo.localViewList.get(i);
            okHttpLyHjjc(packLocalCity.ID);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case MyConfigure.RESULT_GOTO_TRAVEDETAIL: {
                    PackLocalCity cityinfo = (PackLocalCity) data.getSerializableExtra("cityinfo");
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
        okHttpWeeklytq(cityInfo.ID, cityInfo.NAME);
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

    private void okHttpBanner() {
        if(!isOpenNet()){
            showToast(getString(R.string.net_err));
            return ;
        }
        packColumnUp = new PackTravelWeatherColumnUp();
        packColumnUp.type = "1";
        PcsDataDownload.addDownload(packColumnUp);
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
    }

    /**
     * 获取旅游城市列表
     */
    private void okHttpLyCityList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param  = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    String json = param.toString();
                    final String url = CONST.BASE_URL+"ly_city_list";
                    Log.e("ly_city_list", url);
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
                                    if (!TextUtil.isEmpty(result)) {
                                        Log.e("ly_city_list", result);
                                        try {
                                            JSONObject obj = new JSONObject(result);
                                            if (!obj.isNull("b")) {
                                                JSONObject bobj = obj.getJSONObject("b");
                                                if (!bobj.isNull("ly_city_list")) {
                                                    JSONObject listobj = bobj.getJSONObject("ly_city_list");
                                                    if (!TextUtil.isEmpty(listobj.toString())) {
                                                        touristSpotDown = new PackTouristSpotDown();
                                                        touristSpotDown.fillData(listobj.toString());
                                                        if (touristSpotDown == null) {
                                                            return;
                                                        }
                                                        touristSoptList = touristSpotDown.getTouristSpots();
                                                        travelAdapter = new AdapterTravelFragement(getApplicationContext(), touristSoptList, getImageFetcher());
                                                        travel_gridview.setAdapter(travelAdapter);
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

    /**
     * 获取默认景点信息
     */
    private void okHttpLyHjjc(final String stationId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param  = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    JSONObject info = new JSONObject();
                    info.put("stationId", stationId);
                    param.put("paramInfo", info);
                    String json = param.toString();
                    final String url = CONST.BASE_URL+"ly_hjjc";
                    Log.e("ly_hjjc", url);
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
                                    Log.e("ly_hjjc", result);
                                    if (!TextUtil.isEmpty(result)) {
                                        try {
                                            JSONObject obj = new JSONObject(result);
                                            if (!obj.isNull("b")) {
                                                JSONObject bobj = obj.getJSONObject("b");
                                                if (!bobj.isNull("weeklytq")) {
                                                    JSONObject weeklytq = bobj.getJSONObject("weeklytq");
                                                    if (!TextUtil.isEmpty(weeklytq.toString())) {
                                                        PackTravelWeekDown down = new PackTravelWeekDown();
                                                        down.fillData(weeklytq.toString());
                                                        listCityInfo.add(down);
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

    /**
     * 获取一周预报
     */
    private void okHttpWeeklytq(final String stationId, final String cityName) {
        showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param  = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    JSONObject info = new JSONObject();
                    info.put("stationId", stationId);
                    param.put("paramInfo", info);
                    String json = param.toString();
                    final String url = CONST.BASE_URL+"weeklytq";
                    Log.e("weeklytq", url);
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
                                    Log.e("weeklytq", result);
                                    if (!TextUtil.isEmpty(result)) {
                                        try {
                                            JSONObject obj = new JSONObject(result);
                                            if (!obj.isNull("b")) {
                                                JSONObject bobj = obj.getJSONObject("b");
                                                if (!bobj.isNull("weeklytq#"+stationId)) {
                                                    JSONObject weeklytq = bobj.getJSONObject("weeklytq#"+stationId);
                                                    if (!TextUtil.isEmpty(weeklytq.toString())) {
                                                        PackTravelWeekDown down = new PackTravelWeekDown();
                                                        down.fillData(weeklytq.toString());
                                                        // 取城市信息后解析数据
                                                        down.cityId = stationId;
                                                        down.cityName = cityName;
                                                        addCityInfoToListView(down);
                                                        intentNextActivity(down.cityId, down.cityName);
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

}