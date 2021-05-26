package com.pcs.ztqtj.view.activity.air_quality;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.net.airinfopack.PackAirInfoDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.airinfopack.PackAirInfoUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.airinfopack.PackAirStationDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.airinfopack.PackAirStationInfoDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.airinfopack.PackAirStationInfoUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.airinfopack.PackAirStationUp;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.air_quality.AdapterAirQualityDetail;
import com.pcs.ztqtj.control.adapter.air_quality.AdapterAirStation;
import com.pcs.ztqtj.control.tool.AirQualityTool;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.util.OkHttpUtil;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
import com.pcs.ztqtj.view.dialog.DialogFactory.DialogListener;
import com.pcs.ztqtj.view.dialog.DialogOneButton;
import com.pcs.ztqtj.view.myview.ViewCirclePoint;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 空气质量
 *
 * @author JiangZy
 */
public class ActivityAirQualityDetail extends FragmentActivityZtqBase {
    /**
     * 城市ID
     */
    private static String s_area_id;
    /**
     * 城市名称
     */
    private static String s_area_name;
    /**
     * 显示城市/站点
     */
    private static boolean s_show_city = true;
    /**
     * 站点名称
     */
    private static String s_station_name;
    /**
     * 适配器：检测情况
     */
    private AdapterAirQualityDetail mAdapter = null;
    /**
     * 拖动条
     */
    private SeekBar mSeekBar;
    /**
     * 拖动条最大值
     */
    private final int SEEK_MAX = 5;
    /**
     * 拖动条当前进度
     */
    private int mCurrSeekProgress = SEEK_MAX;
    /**
     * 上传包(城市)
     */
    private final PackAirInfoUp mPackInfoUp = new PackAirInfoUp();
    /**
     * 下载包(城市)
     */
    private PackAirInfoDown mPackInfoDown = new PackAirInfoDown();
    /**
     * 上传包(站点)
     */
    private final PackAirStationInfoUp mPackStationInfoUp = new PackAirStationInfoUp();
    /**
     * 下载包(站点)
     */
    private PackAirStationInfoDown mPackStationInfoDown = new PackAirStationInfoDown();
    /**
     * 上传包(站点列表)
     */
    private final PackAirStationUp mPackStationUp = new PackAirStationUp();
    /**
     * 下载包(站点列表)
     */
    private PackAirStationDown mPackStationDown = new PackAirStationDown();


    /**
     * 对话框：站点
     */
    private DialogOneButton mDialogStation = null;

    /**
     * 适配器：站点
     */
    private AdapterAirStation mAdapterStation = null;

    /**
     * 设置城市
     *
     * @param area_id
     * @param area_name
     */
    public static void setCity(String area_id, String area_name) {
        s_area_id = area_id;
        s_area_name = area_name;
        s_show_city = true;
    }

    /**
     * 设置站点
     *
     * @param station_name
     */
    public void setStation(String station_name) {
        s_station_name = station_name;
        s_show_city = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_airquality_detail);
        setTitleText("空气质量");

        // 初始化排名
        initBetterThan();
        // 初始化拖动条
        initSeekBar();
        // 初始化列表
        initList();
        // 初始化站点按钮
        initStationBtn();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 等待框
        showProgressDialog();
        // 刷新显示名称
        refreshShowName();
        // 注册广播
        PcsDataBrocastReceiver.registerReceiver(this, mReceiver);
        // 请求数据
        reqData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 注销广播
        PcsDataBrocastReceiver.unregisterReceiver(this, mReceiver);
    }

    /**
     * 刷新显示名称
     */
    private void refreshShowName() {
        if (s_show_city) {
            // 城市
            Button btnStation = (Button) findViewById(R.id.btn_station);
            btnStation.setText(s_area_name + "总体");

            TextView textView = (TextView) findViewById(R.id.text_city_center);
            textView.setText(s_area_name);
        } else {
            // 站点
            Button btnStation = (Button) findViewById(R.id.btn_station);
            btnStation.setText(s_station_name);

            TextView textView = (TextView) findViewById(R.id.text_city_center);
            textView.setText(s_station_name);
        }
    }

    /**
     * 初始化排名
     */
    private void initBetterThan() {
        Button button = (Button) findViewById(R.id.btn_better);
        // 下划线
        button.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        // 监听
        button.setOnClickListener(mOnClick);
    }

    /**
     * 初始化拖动条
     */
    private void initSeekBar() {
        mSeekBar = (SeekBar) findViewById(R.id.seek_bar);
        mSeekBar.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
    }

    /**
     * 初始化列表
     */
    private void initList() {
        ListView listView = (ListView) findViewById(R.id.list);
        mAdapter = new AdapterAirQualityDetail(this);
        listView.setAdapter(mAdapter);
    }

    /**
     * 初始化站点按钮
     */
    private void initStationBtn() {
        Button btnStation = (Button) findViewById(R.id.btn_station);
        btnStation.setOnClickListener(mOnClick);
    }

    /**
     * 请求数据
     */
    private void reqData() {
        if(!isOpenNet()){
            showToast(getString(R.string.net_err));
            return ;
        }
        if (s_show_city) {
            // 城市
            mPackInfoUp.area = s_area_id;
            mPackInfoUp.time_num = getTimeNum();
            PcsDataDownload.addDownload(mPackInfoUp);
        } else {
            // 站点
            mPackStationInfoUp.station_name = s_station_name;
            mPackStationInfoUp.time_num = getTimeNum() + 1;
            PcsDataDownload.addDownload(mPackStationInfoUp);
        }
    }

    /**
     * 请求站点列表
     */
    private void reqStationList() {
        if(!isOpenNet()){
            showToast(getString(R.string.net_err));
            return ;
        }
        showProgressDialog();
        mPackStationUp.area_id = s_area_id;
        PcsDataDownload.addDownload(mPackStationUp);
    }

    /**
     * 刷新排行等..
     *
     * @param pack
     */
    private void refreshRankEtc(PackAirInfoDown pack) {
        TextView textView = null;
        Button button = null;
        // 排名
        button = (Button) findViewById(R.id.btn_better);
        button.setText("排名第" + pack.city_num + "位>>");
        // 更新时间
        textView = (TextView) findViewById(R.id.text_time);
        textView.setText(pack.updateTime + "刷新");
    }

    /**
     * 刷新数据
     */
    private void refreshData(PackAirInfoDown pack) {
        TextView textView = null;
        // AQI颜色
        int intAqi = 0;
        if (!TextUtils.isEmpty(pack.aqi)) {
            intAqi = Integer.valueOf(pack.aqi);
        }
        int aqiColor = AirQualityTool.getInstance().getAqiColor(intAqi);
        // AQI数字
        textView = (TextView) findViewById(R.id.text_aqi);
        textView.setText(pack.aqi);
        textView.setTextColor(aqiColor);
        // AQI指针
        ViewCirclePoint viewPoint = (ViewCirclePoint) findViewById(R.id.circle_point);
        viewPoint.setPercent(pack.aqi_percent);
        viewPoint.invalidate();
        // 健康提示
        textView = (TextView) findViewById(R.id.text_health_content);
        String tipArr[] = getResources().getStringArray(
                R.array.AirQualityHeathTip);
        String tipStr = AirQualityTool.getInstance().getHealthTip(tipArr,
                intAqi);
        textView.setText(tipStr);
        // 其他检测项
        mAdapter.setDataPack(pack);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 获取时间数字
     *
     * @return
     */
    private int getTimeNum() {
        int num = SEEK_MAX - mSeekBar.getProgress();
        return num;
    }

    private final int screenHight = 0;

    /**
     * 显示站点对话框
     */
    private void showDialogStation() {
        View view = LayoutInflater.from(this).inflate(
                R.layout.setfragmetnt_dialog_layout, null);

        mAdapterStation = new AdapterAirStation(this);
        mAdapterStation.setData(s_area_name, mPackStationDown);

        ListView listView = (ListView) view.findViewById(R.id.listview);
        listView.setAdapter(mAdapterStation);
        listView.setOnItemClickListener(mOnItemClickStation);

        mDialogStation = new DialogOneButton(this, view, "取消", mDialogListener);
        mDialogStation.setTitle("选择站点");
        mAdapterStation.notifyDataSetChanged();
        mDialogStation.show();
        setListViewHeightBasedOnChildren(listView);
    }

    /**
     * 设置ListView一屏显示的条数
     *
     * @param listView
     */
    private void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        int showItmeCount = 0;
        if (mPackStationDown == null || mPackStationDown.list.isEmpty()) {
            showItmeCount = 0;
        } else {
            if (mPackStationDown.list.size() >= 9) {
                showItmeCount = 9;
            } else {
                showItmeCount = mPackStationDown.list.size() + 1;
            }
        }
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < showItmeCount; i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        ((MarginLayoutParams) params).setMargins(0, 10, 10, 10);
        listView.setLayoutParams(params);
    }

    /**
     * 广播接收器
     */
    private final PcsDataBrocastReceiver mReceiver = new PcsDataBrocastReceiver() {
        @Override
        public void onReceive(String nameStr, String errorStr) {
            if (nameStr.equals(mPackInfoUp.getName())) {
                if (!TextUtils.isEmpty(errorStr)) {
                    return;
                }
                // 取消等待框
                dismissProgressDialog();
                // --------城市信息
                // 取消等待框
                dismissProgressDialog();
                // 加载数据
                mPackInfoDown = (PackAirInfoDown) PcsDataManager.getInstance().getNetPack(mPackInfoUp.getName());
                if (mPackInfoDown == null) {
                    return;
                }
                // 刷新显示名称
                refreshShowName();
                // 刷新排行等...
                refreshRankEtc(mPackInfoDown);
                // 刷新数据
                refreshData(mPackInfoDown);
            } else if (nameStr.equals(mPackStationUp.getName())) {
                if (!TextUtils.isEmpty(errorStr)) {
                    return;
                }
                // 取消等待框
                dismissProgressDialog();
                // --------站点列表
                // 加载数据
//                mPackStationDown = (PackAirStationDown) PcsDataManager.getInstance().getNetPack(mPackStationUp.getName());
                okHttpAirCityStation(mPackStationUp.getName());

                // 弹出对话框
                showDialogStation();
                if (mPackStationDown == null) {
                    return;
                }

                if (mPackStationDown.list.size() == 0) {
                    Toast.makeText(ActivityAirQualityDetail.this, "暂无站点",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

            } else if (nameStr.equals(mPackStationInfoUp.getName())) {
                if (!TextUtils.isEmpty(errorStr)) {
                    return;
                }
                // 取消等待框
                dismissProgressDialog();
                // --------站点信息
                // 加载数据
                mPackStationInfoDown = (PackAirStationInfoDown) PcsDataManager.getInstance().getNetPack(
                        mPackStationInfoUp.getName());

                if (mPackStationInfoDown == null) {
                    return;
                }


                // 刷新显示名称
                refreshShowName();
                // 刷新数据
                refreshData(mPackStationInfoDown);
            }
        }
    };

    private void okHttpAirCityStation(final String name) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String url = CONST.BASE_URL+name;
                Log.e("air_city_station", url);
                OkHttpUtil.enqueue(new Request.Builder().url(url).build(), new Callback() {
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
                                try {
                                    JSONObject obj = new JSONObject(result);
                                    if (!obj.isNull("b")) {
                                        JSONObject bobj = obj.getJSONObject("b");
                                        if (!bobj.isNull("air_city_station")) {
                                            JSONObject air_city_station = bobj.getJSONObject("air_city_station");
                                            mPackStationDown = new PackAirStationDown();
                                            mPackInfoDown.fillData(air_city_station.toString());
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                });
            }
        }).start();
    }

    private final OnSeekBarChangeListener mOnSeekBarChangeListener = new OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            int progress = seekBar.getProgress();
            if (progress != mCurrSeekProgress) {
                mCurrSeekProgress = progress;
                showProgressDialog();
                // 请求网络
                reqData();
            }
        }
    };

    /**
     * 点击事件
     */
    private final OnClickListener mOnClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_better:
                    Intent it = new Intent();
                    it.setClass(ActivityAirQualityDetail.this, ActivityAirQuality.class);
                    ActivityAirQualityDetail.this.startActivity(it);
                    break;
                case R.id.btn_station:
                    // 请求站点列表
                    reqStationList();
            }
        }
    };

    /**
     * 对话框按钮点击事件
     */
    private final DialogListener mDialogListener = new DialogListener() {
        @Override
        public void click(String str) {
            // 对话框取消按钮
            mDialogStation.dismiss();
        }
    };

    /**
     * 列表点击事件：站点
     */
    private final OnItemClickListener mOnItemClickStation = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // 显示等待
            showProgressDialog();
            if (position == 0) {
                s_show_city = true;
            } else {
                setStation((String) mAdapterStation.getItem(position));
            }
            // 关闭对话框
            mDialogStation.dismiss();
            // 请求数据
            reqData();
        }
    };
}
