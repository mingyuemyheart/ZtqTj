package com.pcs.ztqtj.control.livequery;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.amap.api.maps.model.GroundOverlayOptions;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCity;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityMain;
import com.pcs.lib_ztqfj_v2.model.pack.net.column.ColumnInfo;
import com.pcs.lib_ztqfj_v2.model.pack.net.column.PackColumnDown;
import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.waterflood.AdapterPopWindow;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.util.OkHttpUtil;
import com.pcs.ztqtj.view.activity.livequery.ActivityLiveQuery;
import com.pcs.ztqtj.view.fragment.livequery.FragmentDistributionMap;

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

import static com.pcs.ztqtj.control.livequery.ControlDistribution.DistributionStatus.CLOUD;
import static com.pcs.ztqtj.control.livequery.ControlDistribution.DistributionStatus.DB;
import static com.pcs.ztqtj.control.livequery.ControlDistribution.DistributionStatus.GJ;
import static com.pcs.ztqtj.control.livequery.ControlDistribution.DistributionStatus.RADAR;
import static com.pcs.ztqtj.control.livequery.ControlDistribution.DistributionStatus.SB;
import static com.pcs.ztqtj.control.livequery.ControlDistribution.DistributionStatus.ZD;

/**
 * 监测预报-实况查询-要素分布图-色斑图操作控制器
 */
public class ControlDistributionHandler extends ControlDistributionBase {

    private FragmentDistributionMap mFragment;
    private Context mContext;
    private ActivityLiveQuery mActivity;
    private ViewGroup mRootLayout;
    private ControlDistributionDetail controlDistribution;

    // 站点名
    private TextView tvSite;
    // 时间段
    private TextView tvTime;
    private CheckBox cbSb, cbGj, cbZd, cbDb, cbRadar, cbCloud, cbRadarPlay, cbCloudPlay, cbTyphoon;
    // 下拉控件
    private View layoutTime, layoutBaseTime;
    // 站点下拉控件
    private View layoutSite;

    // 时间段下拉菜单列表
    private List<ColumnInfo> timeColumnList = new ArrayList<>();
    // 站点下拉菜单列表
    private List<ColumnInfo> siteColumnList = new ArrayList<>();
    // 是否是全国类型
    private boolean isProvince = false;
    // 当前色斑图flag
    private ColumnInfo currentFlagInfo = new ColumnInfo();
    // 当前站点信息
    private ColumnInfo currentSiteInfo = new ColumnInfo();
    // 当前栏目类型
    ControlDistribution.ColumnCategory currentColumn;
    // 当前分布图状态
    private ControlDistribution.DistributionStatus currentStatus = SB;

    private static final int WHAT_PLAY = 0;
    private static final int WHAT_PAUSE = 1;

    public ControlDistributionHandler(FragmentDistributionMap fragment, ActivityLiveQuery activity, ViewGroup rootLayout) {
        mFragment = fragment;
        mActivity = activity;
        mContext = activity;
        mRootLayout = rootLayout;
        isProvince = mFragment.getIsProvince();
        controlDistribution = new ControlDistributionDetail(mFragment, activity, rootLayout);
        controlDistribution.setCallBack(multiplePictureCallBack);
    }

    @Override
    public void init() {
        initView();
        initEvent();
        initData();
    }

    @Override
    public void updateView(ControlDistribution.ColumnCategory column) {
        if(column == ControlDistribution.ColumnCategory.WIND) {
            cbSb.setText("风向图");
        } else {
            cbSb.setText("色斑图");
        }
        update();
        controlDistribution.clearAll();
        mActivity.showProgressDialog();
        // 请求栏目下拉列表
        if(siteColumnList.size() == 0) {
            reqSiteColumn();
        } else {
            reqColumnValue(column);
        }
        currentColumn = column;
    }

    @Override
    public void clear() {
        update();
    }

    @Override
    public void destroy() {
    }

    private void initView() {
        tvSite = (TextView) mRootLayout.findViewById(R.id.tv_site);
        tvTime = (TextView) mRootLayout.findViewById(R.id.tv_drop_down);
        cbSb = (CheckBox) mRootLayout.findViewById(R.id.rb_sb);
        cbGj = mRootLayout.findViewById(R.id.rb_gj);
        cbZd = (CheckBox) mRootLayout.findViewById(R.id.rb_zd);
        cbDb = mRootLayout.findViewById(R.id.rb_db);
        cbRadar = (CheckBox) mRootLayout.findViewById(R.id.rb_radar);
        cbCloud = (CheckBox) mRootLayout.findViewById(R.id.rb_cloud);
        cbTyphoon = (CheckBox) mRootLayout.findViewById(R.id.cb_typhoon);
        cbRadarPlay = (CheckBox) mRootLayout.findViewById(R.id.rb_radar_play);
        cbCloudPlay = (CheckBox) mRootLayout.findViewById(R.id.rb_cloud_play);
        layoutTime = mRootLayout.findViewById(R.id.layout_drop_down);
        layoutSite = mRootLayout.findViewById(R.id.layout_site);
        layoutBaseTime = mRootLayout.findViewById(R.id.layout_drop_search);
    }

    private void initEvent() {
        cbSb.setOnCheckedChangeListener(cbSbListener);
        cbZd.setOnCheckedChangeListener(cbZdListener);
        cbDb.setOnCheckedChangeListener(cbDbListener);
        cbGj.setOnCheckedChangeListener(cbGjListener);
//        cbRadar.setOnClickListener(cbRadarListener);

        cbCloud.setOnCheckedChangeListener(cbCloudListener);
        cbCloudPlay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    if(currentPlayPosition == 0 || currentPlayPosition == imageSize-1) {
                        play();
                    } else {
                        resume();
                    }
                } else {
                    if(currentPlayPosition == 0) {
                        stop();
                    } else {
                        pause();
                    }
                }
            }
        });
        cbCloud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(isProvince) {
//                    Toast.makeText(mActivity, "建设中", Toast.LENGTH_LONG).show();
//                    cbCloud.setChecked(false);
//                }
            }
        });
        cbRadar.setOnCheckedChangeListener(cbRadarListener);
        cbRadarPlay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    if(currentPlayPosition == 0 || currentPlayPosition == imageSize-1) {
                        play();
                    } else {
                        resume();
                    }
                } else {
                    if(currentPlayPosition == 0) {
                        stop();
                    } else {
                        pause();
                    }
                }
            }
        });

        layoutTime.setOnClickListener(layoutTimeListener);
        layoutSite.setOnClickListener(layoutSiteListener);
    }

    private void initData() {
        currentColumn = mFragment.getCurrentColumn();
    }

    private void update() {
        controlDistribution.clearAll();
        cbSb.setChecked(false);
        cbDb.setChecked(false);
        cbZd.setChecked(false);
        cbGj.setChecked(false);
        cbRadar.setChecked(false);
        cbCloud.setChecked(false);
    }

    /**
     * 设置站点下拉弹窗数据
     * @param list
     */
    private void setSitePopupWindow(List<ColumnInfo> list) {
        siteColumnList = list;
        if (siteColumnList.size() > 0) {
            tvSite.setText(siteColumnList.get(0).name);
            currentSiteInfo = siteColumnList.get(0);
        } else {
            tvSite.setText("");
            currentSiteInfo = new ColumnInfo();
        }
    }

    /**
     * 设置下拉弹框数据
     * @param list
     */
    private void setTimePopupWindow(List<ColumnInfo> list) {
        timeColumnList = list;
        if (timeColumnList.size() > 0) {
            tvTime.setText(timeColumnList.get(0).name);
            currentFlagInfo = timeColumnList.get(0);
        } else {
            tvTime.setText("");
            currentFlagInfo = new ColumnInfo();
        }
    }

    /**
     * 请求自动站列表
     */
    private void reqSiteColumn() {
        mActivity.showProgressDialog();
        siteColumnList.clear();
        tvSite.setText("");
        String column_type = "";
        if(isProvince) {
            column_type = "16";
        } else {
            column_type = "15";
        }
        okHttpObsColumnList(column_type);
    }

    /**
     * 请求栏目
     *
     * @param column
     */
    private void reqColumnValue(ControlDistribution.ColumnCategory column) {
        timeColumnList.clear();
        tvTime.setText("");
        String column_type = "";
        switch (column) {
            case RAIN: // 雨量
                column_type = "10";
                break;
            case TEMPERATURE:
                column_type = "11";
                break;
            case WIND:
                column_type = "12";
                break;
            case VISIBILITY:
                column_type = "13";
                break;
            case PRESSURE:
                column_type = "14";
                break;
            case HUMIDITY:
                column_type = "17";
                break;
        }
        okHttpObsColumnList(column_type);
    }

    /**
     * 请求分布图数据
     */
    private void reqDistribution() {
        controlDistribution.reqDistribution(currentColumn, currentStatus, currentSiteInfo, currentFlagInfo);
    }

    /**
     * 创建城市下拉选择列表
     * @param dropDownView
     * @param dataeaum
     * @param type 0: 时间段下拉框 1: 站点下拉框
     */
    private void createPopupWindow(final TextView dropDownView, final List<String> dataeaum, int type) {
        AdapterPopWindow dataAdapter = new AdapterPopWindow(mContext, dataeaum);
        View popcontent = LayoutInflater.from(mContext).inflate(R.layout.pop_list_layout, null);
        ListView lv = (ListView) popcontent.findViewById(R.id.mylistviw);
        lv.setAdapter(dataAdapter);
        final PopupWindow pop = new PopupWindow(mContext);
        pop.setContentView(popcontent);
        pop.setOutsideTouchable(false);
        pop.setWidth(dropDownView.getWidth());
        // 调整下拉框长度
        pop.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        pop.setFocusable(true);
        if(type == 0) {
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                    pop.dismiss();
                    dropDownView.setText(dataeaum.get(position));
                    currentFlagInfo = timeColumnList.get(position);
                    //controlDistribution.clearAll();
                    controlDistribution.clearDistribution();
                    controlDistribution.clearAutoSite();
                    if(cbSb.isChecked()) {
                        controlDistribution.reqDistribution(currentColumn, SB, currentSiteInfo, currentFlagInfo);
                    }
                    if(cbZd.isChecked()) {
                        controlDistribution.reqDistribution(currentColumn, ZD, currentSiteInfo, currentFlagInfo);
                    } else if(cbDb.isChecked()) {
                        controlDistribution.reqDistribution(currentColumn, DB, currentSiteInfo, currentFlagInfo);
                    } else if(cbGj.isChecked()) {
                        controlDistribution.reqDistribution(currentColumn, GJ, currentSiteInfo, currentFlagInfo);
                    }
//                    if(cbRadar.isChecked()) {
//                        controlDistribution.reqDistribution(currentColumn, RADAR, currentSiteInfo, currentFlagInfo);
//                    } else if(cbCloud.isChecked()) {
//                        controlDistribution.reqDistribution(currentColumn, CLOUD, currentSiteInfo, currentFlagInfo);
//                    }
                }
            });
        } else {
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    pop.dismiss();
                    dropDownView.setText(dataeaum.get(position));
                    currentSiteInfo = siteColumnList.get(position);
                    //controlDistribution.clearAll();
                    controlDistribution.clearDistribution();
                    controlDistribution.clearAutoSite();
                    controlDistribution.clearRadar();
                    if(cbSb.isChecked()) {
                        controlDistribution.reqDistribution(currentColumn, SB, currentSiteInfo, currentFlagInfo);
                    }
                    if(cbZd.isChecked()) {
                        controlDistribution.reqDistribution(currentColumn, ZD, currentSiteInfo, currentFlagInfo);
                    } else if(cbDb.isChecked()) {
                        controlDistribution.reqDistribution(currentColumn, DB, currentSiteInfo, currentFlagInfo);
                    } else if(cbGj.isChecked()) {
                        controlDistribution.reqDistribution(currentColumn, GJ, currentSiteInfo, currentFlagInfo);
                    }
                    if(cbRadar.isChecked()) {
                        controlDistribution.reqDistribution(currentColumn, RADAR, currentSiteInfo, currentFlagInfo);
                        stopDraw();
                    }
//                    else if(cbCloud.isChecked()) {
//                        controlDistribution.reqDistribution(currentColumn, CLOUD, currentSiteInfo, currentFlagInfo);
//                        stopDraw();
//                    }
                }
            });
        }
        pop.showAsDropDown(dropDownView);
    }

    private void stopDraw() {
        currentPlayPosition = 0;
        if(currentStatus == RADAR) {
            cbRadarPlay.setChecked(false);
        } else {
            cbCloudPlay.setChecked(false);
        }
    }

    private ColumnInfo getCurrentCityInfo() {
        ColumnInfo info = new ColumnInfo();
        if(isProvince) {
            PackLocalCityMain localCityMain =  ZtqCityDB.getInstance().getCityMain();
            if(localCityMain != null) {
                String parentId = localCityMain.PARENT_ID;
                PackLocalCity provinceCity = ZtqCityDB.getInstance().getProvinceById(parentId);
                if(provinceCity != null) {
                    String name = provinceCity.NAME;
                    info = new ColumnInfo(name, parentId);
                }
            }
        }
        return info;
    }

    private void addCity() {
        if(isProvince) {
            PackLocalCityMain localCityMain =  ZtqCityDB.getInstance().getCityMain();
            if(localCityMain != null) {
                String parentId = localCityMain.PARENT_ID;
                PackLocalCity provinceCity = ZtqCityDB.getInstance().getProvinceById(parentId);
                if(provinceCity != null) {
                    String name = provinceCity.NAME;
                    ColumnInfo info = new ColumnInfo(name, parentId);
                    siteColumnList.add(0, info);
                }
            }
            setSitePopupWindow(siteColumnList);
        }
    }

    private void deleteCity() {
        if(isProvince && siteColumnList.size() > 0) {
            siteColumnList.remove(0);
            setSitePopupWindow(siteColumnList);
        }
    }

    /**
     * 隐藏图例
     */
    private void hideLegend() {
        if(!cbSb.isChecked() && !cbZd.isChecked() && !cbDb.isChecked() && !cbGj.isChecked()) {
            mFragment.hideLegend();
        }
    }

    /**
     * 显示图例
     */
    private void showLegend() {
        if(cbSb.isChecked() || cbZd.isChecked() || cbDb.isChecked() || cbGj.isChecked()) {
            mFragment.showLegend();
        }
    }

    /**
     * 隐藏轮播信息
     */
    private void hidePlayInfo() {
        if(!cbRadar.isChecked() && !cbCloud.isChecked()) {
            controlDistribution.hidePlayInfo();
        }
    }

    private void setDropdownState() {
        if(cbRadar.isChecked() && !cbSb.isChecked() && !cbZd.isChecked() && !cbDb.isChecked() && !cbGj.isChecked()) {
            layoutBaseTime.setVisibility(View.INVISIBLE);
            layoutSite.setVisibility(View.VISIBLE);
        }
        if(cbCloud.isChecked() && !cbSb.isChecked() && !cbZd.isChecked() && !cbDb.isChecked() && !cbGj.isChecked()) {
            layoutSite.setVisibility(View.INVISIBLE);
            layoutBaseTime.setVisibility(View.INVISIBLE);
        }
        if(cbTyphoon.isChecked() && !cbRadar.isChecked() && !cbSb.isChecked() && !cbZd.isChecked() && !cbDb.isChecked() && !cbGj.isChecked()) {
            layoutSite.setVisibility(View.INVISIBLE);
            layoutBaseTime.setVisibility(View.INVISIBLE);
        }
        if(cbSb.isChecked() || cbZd.isChecked() || cbDb.isChecked() || cbGj.isChecked()) {
            layoutSite.setVisibility(View.VISIBLE);
            layoutBaseTime.setVisibility(View.VISIBLE);
        }
    }

    private CompoundButton.OnCheckedChangeListener cbSbListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked) { // 色斑图按钮选中状态
//                if(cbZd.isChecked()) { // 如果自动站按钮也是选中状态则将自动站按钮设置为未选中状态
//                    cbZd.setChecked(false);
//                }
                currentStatus = SB;
                reqDistribution();
                showLegend();
            } else {
                controlDistribution.clear(SB);
                hideLegend();
            }
            setDropdownState();
        }
    };

    private CompoundButton.OnCheckedChangeListener cbZdListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked) {
                if(cbDb.isChecked()) {
                    cbDb.setChecked(false);
                }
                if(cbGj.isChecked()) {
                    cbGj.setChecked(false);
                }
                currentStatus = ZD;
                addCity();
                reqDistribution();
                showLegend();
            } else {
                controlDistribution.clear(ZD);
                deleteCity();
                hideLegend();
            }
            setDropdownState();
        }
    };

    private CompoundButton.OnCheckedChangeListener cbDbListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked) {
                if(cbZd.isChecked()) {
                    cbZd.setChecked(false);
                }
                if(cbGj.isChecked()) {
                    cbGj.setChecked(false);
                }
                currentStatus = DB;
                addCity();
                reqDistribution();
                showLegend();
            } else {
                controlDistribution.clear(DB);
                deleteCity();
                hideLegend();
            }
            setDropdownState();
        }
    };

    private CompoundButton.OnCheckedChangeListener cbGjListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked) {
                if(cbZd.isChecked()) {
                    cbZd.setChecked(false);
                }
                if(cbDb.isChecked()) {
                    cbDb.setChecked(false);
                }
                currentStatus = GJ;
                addCity();
                reqDistribution();
                showLegend();
            } else {
                controlDistribution.clear(GJ);
                deleteCity();
                hideLegend();
            }
            setDropdownState();
        }
    };

    CompoundButton.OnCheckedChangeListener cbRadarListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked) {
                currentPlayPosition = 0;
                if(cbCloud.isChecked()) {
                    cbCloud.setChecked(false);
                }
                if(cbCloudPlay.isChecked()) {
                    cbCloudPlay.setChecked(false);
                }
                currentStatus = RADAR;
                reqDistribution();
            } else {
                controlDistribution.clear(RADAR);
                cbRadarPlay.setChecked(false);
                hidePlayInfo();
            }
            // 同步更新播放按钮状态
            cbRadarPlay.setEnabled(isChecked);
            setDropdownState();
        }
    };

    CompoundButton.OnCheckedChangeListener cbCloudListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked) {
                currentPlayPosition = 0;
                if(cbRadar.isChecked()) {
                    cbRadar.setChecked(false);
                }
                if(cbRadarPlay.isChecked()) {
                    cbRadarPlay.setChecked(false);
                }
                currentStatus = CLOUD;
                reqDistribution();
            } else {
                controlDistribution.clear(CLOUD);
                cbCloudPlay.setChecked(false);
                hidePlayInfo();
            }
            // 同步更新播放按钮状态
            cbCloudPlay.setEnabled(isChecked);
            setDropdownState();
        }
    };

    /**
     * 显示时间下拉菜单回调
     */
    private View.OnClickListener layoutTimeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (timeColumnList != null && timeColumnList.size() > 0) {
                List<String> list = new ArrayList<>();
                for (ColumnInfo bean : timeColumnList) {
                    list.add(bean.name);
                }
                createPopupWindow(tvTime, list, 0);
            }
        }
    };

    View.OnClickListener layoutSiteListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (siteColumnList != null && siteColumnList.size() > 0) {
                List<String> list = new ArrayList<>();
                for (ColumnInfo bean : siteColumnList) {
                    list.add(bean.name);
                }
                createPopupWindow(tvSite, list, 1);
            }
        }
    };

    private ControlDistributionDetail.MultiplePictureCallBack multiplePictureCallBack = new ControlDistributionDetail.MultiplePictureCallBack() {
        @Override
        public void onFinish(ControlDistribution.DistributionStatus status, List<GroundOverlayOptions> list) {
            imageSize = list.size();
            showFitstPicture(imageSize);
        }
    };

    private void showFitstPicture(int position) {
        currentPlayPosition = position-1 < 0 ? 0 : position-1;
        controlDistribution.addPolyToMap(controlDistribution.getMultipleImageState(), currentPlayPosition, false);
    }

    private void play() {
        currentPlayPosition = 0;
        Message message = Message.obtain();
        message.what = WHAT_PLAY;
        message.arg1 = currentPlayPosition;
        message.arg2 = imageSize;
        mHandler.sendMessage(message);
    }

    private void pause() {
        Message message = Message.obtain();
        message.what = WHAT_PAUSE;
        message.arg1 = currentPlayPosition;
        message.arg2 = imageSize;
        mHandler.sendMessage(message);
    }

    private void resume() {
        Message message = Message.obtain();
        message.what = WHAT_PLAY;
        message.arg1 = currentPlayPosition;
        message.arg2 = imageSize;
        mHandler.sendMessage(message);
    }

    private void stop() {
        currentPlayPosition = 0;
        Message message = Message.obtain();
        message.what = WHAT_PAUSE;
        message.arg1 = currentPlayPosition;
        message.arg2 = imageSize;
        mHandler.sendMessage(message);
    }


    private int imageSize = 0;
    private int currentPlayPosition = 0;
    private Handler mHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what) {
                case WHAT_PLAY:
                    currentPlayPosition = msg.arg1;
                    int size = msg.arg2;
                    if(size > currentPlayPosition) {
                        controlDistribution.addPolyToMap(controlDistribution.getMultipleImageState(), msg.arg1, false);
                        Message message = Message.obtain();
                        message.what = WHAT_PLAY;
                        message.arg1 = currentPlayPosition+1;
                        message.arg2 = size;
                        mHandler.sendMessageDelayed(message, 1000);
                    } else {
                        stopDraw();
                    }
                    break;
                case WHAT_PAUSE:
                    currentPlayPosition = msg.arg1;
                    mHandler.removeMessages(WHAT_PLAY);
                    break;
            }
        }
    };

    /**
     * 获取下拉框时间数据
     */
    private void okHttpObsColumnList(final String column_type) {
        mActivity.showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param  = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    JSONObject info = new JSONObject();
                    info.put("stationId", column_type);//10(雨量),12(风况),11(气温),13(能见度),14(气压),17(相对湿度),16(天津)，15(全国)
                    param.put("paramInfo", info);
                    String json = param.toString();
                    Log.e("obs_column_list", json);
                    final String url = CONST.BASE_URL+"obs_column_list";
                    Log.e("obs_column_list", url);
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
                            Log.e("obs_column_list", result);
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        JSONObject obj = new JSONObject(result);
                                        if (!obj.isNull("b")) {
                                            JSONObject bobj = obj.getJSONObject("b");
                                            if (!bobj.isNull("column")) {
                                                JSONObject column = bobj.getJSONObject("column");
                                                if (!TextUtils.isEmpty(column.toString())) {
                                                    mActivity.dismissProgressDialog();
                                                    PackColumnDown down = new PackColumnDown();
                                                    down.fillData(column.toString());
                                                    // 自动站
                                                    if (column_type.equals("15")) {
                                                        setSitePopupWindow(down.arrcolumnInfo);
                                                        reqColumnValue(currentColumn);
                                                    } else if (column_type.equals("16")) {
                                                        setSitePopupWindow(down.arrcolumnInfo);
                                                        reqColumnValue(currentColumn);
                                                    } else {
                                                        mActivity.dismissProgressDialog();
                                                        setTimePopupWindow(down.arrcolumnInfo);
                                                    }
                                                    // 时间段和站点都有数据时取初始色斑图数据
                                                    if (!TextUtils.isEmpty(currentFlagInfo.type) && !TextUtils.isEmpty(currentSiteInfo.type)) {
                                                        //cbZd.setChecked(true);
                                                        cbSb.setChecked(true);
//                    cbRadar.setChecked(false);
//                    cbCloud.setChecked(false);
                                                    }
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

}
