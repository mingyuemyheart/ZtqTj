package com.pcs.ztqtj.view.activity.life.travel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;

import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCity;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalTravelViewInfo;
import com.pcs.lib_ztqfj_v2.model.pack.net.week.PackTravelWeekDown;
import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.AdapterTravelBookmarks;
import com.pcs.ztqtj.control.adapter.AdapterTravelBookmarks.OnClickDeleteButtonListener;
import com.pcs.ztqtj.control.adapter.AdapterTravelBookmarks.OnClickItemListener;
import com.pcs.ztqtj.control.tool.MyConfigure;
import com.pcs.ztqtj.control.tool.utils.TextUtil;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.util.OkHttpUtil;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
import com.pcs.ztqtj.view.activity.citylist.ActivitySelectTravelViewList;

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
 * 生活气象-旅游气象我的收藏夹
 */
public class ActivityTravelBookmark extends FragmentActivityZtqBase {

    private GridView gridBookmark = null;
    private AdapterTravelBookmarks adapterBookmark = null;
    private List<PackTravelWeekDown> listCityInfo = new ArrayList<PackTravelWeekDown>();
    private boolean toDetail = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_bookmark);
        setTitleText(R.string.my_bookmark);
        createImageFetcher();
        initView();

        setBtnRight(R.drawable.btn_search_nor, new OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoSelectCity();
            }
        });
        setBackground(this.getResources().getColor(R.color.bg_bookmark));
    }

    private void initView() {
        gridBookmark = (GridView) findViewById(R.id.grid_bookmark);
        adapterBookmark = new AdapterTravelBookmarks(this, listCityInfo,getImageFetcher());
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
        adapterBookmark.setOnClickDeleteButtonListener(new OnClickDeleteButtonListener() {

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

        updateAllCity();
    }

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

    /**
     * 跳转至选择景点页面
     */
    private void gotoSelectCity() {
        Intent intent = new Intent(ActivityTravelBookmark.this, ActivitySelectTravelViewList.class);
        startActivityForResult(intent, MyConfigure.RESULT_SELECT_TRAVELVIEW);
    }

    /**
     * 跳转至景点详细页面
     *
     * @param cityID
     * @param CityName
     */
    private void gotoCityDetail(String cityID, String CityName) {
        Intent intent = new Intent(ActivityTravelBookmark.this, ActivityTravelDetail.class);
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
        okHttpWeeklytq(cityInfo.ID, cityInfo.NAME);
    }

    /**
     * 保存城市
     * @param cityinfo
     */
    private void saveLocalTravelViewInfo(PackLocalCity cityinfo) {
        PackLocalTravelViewInfo localcitylist = ZtqCityDB.getInstance().getCurrentTravelViewInfo();
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

    private void updateAllCity() {
        PackLocalTravelViewInfo localTravelViewInfo = ZtqCityDB.getInstance().getCurrentTravelViewInfo();
        if (localTravelViewInfo == null) {
            return;
        }
        for (int i = 0; i < localTravelViewInfo.localViewList.size(); i++) {
            PackLocalCity city = localTravelViewInfo.localViewList.get(i);
            okHttpWeeklytq(city.ID, city.NAME);
        }
    }

    /**
     * 获取一周预报
     */
    private void okHttpWeeklytq(final String cityId, final String cityName) {
        showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param  = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    JSONObject info = new JSONObject();
                    info.put("stationId", cityId);
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
                                                if (!bobj.isNull("weeklytq#"+cityId)) {
                                                    JSONObject weeklytq = bobj.getJSONObject("weeklytq#"+cityId);
                                                    if (!TextUtil.isEmpty(weeklytq.toString())) {
                                                        dismissProgressDialog();
                                                        PackTravelWeekDown packTravelWeekDown = new PackTravelWeekDown();
                                                        packTravelWeekDown.fillData(weeklytq.toString());
                                                        packTravelWeekDown.cityId = cityId;
                                                        packTravelWeekDown.cityName = cityName;
                                                        listCityInfo.add(packTravelWeekDown);
                                                        adapterBookmark.notifyDataSetChanged();
                                                        if (toDetail) {
                                                            toDetail = false;
                                                            gotoCityDetail(packTravelWeekDown.cityId, packTravelWeekDown.cityName);
                                                        }
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
