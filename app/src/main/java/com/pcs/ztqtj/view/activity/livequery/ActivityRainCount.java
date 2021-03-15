package com.pcs.ztqtj.view.activity.livequery;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pcs.lib.lib_pcs_v3.control.tool.Util;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackRainStandardUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.LevelListBean;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackYltjMaxHourUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackYltjRankDown.RainFallRank;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackYltj_level_ranking_detailUp;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.AdapterRainStandard;
import com.pcs.ztqtj.control.adapter.livequery.AdapterRainLevelCount;
import com.pcs.ztqtj.control.adapter.livequery.AdapterTempertureRainFall;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
import com.pcs.ztqtj.view.activity.livequery.DataControl.ListSelect;
import com.pcs.ztqtj.view.myview.MyListView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Z 雨量综合分析
 */
public class ActivityRainCount extends FragmentActivityZtqBase implements OnClickListener {
    private TextView _24MaxDrawTextView;
    /**
     * 数据控制中心
     */
    private DataControl dataControl;

    // 1、3小时列表
    private MyListView _1_3_hour_max;
    private List<RainFallRank> _1_3_hour_max_data;
    private AdapterTempertureRainFall _1_3_hour_max_adapter;

    // 24小时列表
    private MyListView _24_max_ranking;
    private AdapterTempertureRainFall _24_max_ranking_adatper;
    private List<RainFallRank> _24_max_ranking_data;

    // 降雨量分级统计
    private MyListView levelRanking;
    private List<LevelListBean> levelRankingData;
    private AdapterRainLevelCount levelRankingAdatper;

    private TextView level_hour;
    private TextView level_count;
    /**
     * 发布时间
     */
    private TextView level_release;
    /**
     * 发布时段统计
     */
    private TextView level_count_time;

    private ImageButton btnRightReflush;
    private MyReceiver receiver = new MyReceiver();

    /**
     * 当前选择的是几小时
     */
    private int rainMax24CheckPosition = 0;

    private Button btn_jyqddjhf;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_raincount);
        PcsDataBrocastReceiver.registerReceiver(this, receiver);
        setTitleText(getIntent().getStringExtra("title"));
//        PackLocalCityMain cityMain = ZtqCityDB.getInstance().getCityMain();
//        PackLocalCity cityinfo_c = ZtqCityDB.getInstance().getCityInfo2_ID(cityMain.PARENT_ID);
        setBtnRight(R.drawable.btn_refresh, new OnClickListener() {
            @Override
            public void onClick(View v) {
                btnRightReflush = (ImageButton) findViewById(R.id.btn_right);
                // 点击动画
                btnRightReflush.setClickable(false);
                Animation animation = AnimationUtils.loadAnimation(ActivityRainCount.this, R.anim.rotate_repeat_1000);
                LinearInterpolator lin = new LinearInterpolator();
                animation.setInterpolator(lin);
                btnRightReflush.startAnimation(animation);
                handler.sendEmptyMessageDelayed(0, 1500);

            }
        });
        initView();
        initData();
        initEvent();
    }

    /**
     * 关闭动画
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            stopRefreshBtnAnim();
            // 重新解析数据数据
            dataControl.successDealWidth();
            // 刷新视图数据
            reflushData();
        }
    };

    /**
     * 停止刷新按钮动画
     */
    private void stopRefreshBtnAnim() {
        btnRightReflush.setClickable(true);
        // 动画
        btnRightReflush.clearAnimation();
    }

    private void initEvent() {
        _24MaxDrawTextView.setOnClickListener(this);
//        to_image_btn.setOnClickListener(this);
        btn_jyqddjhf.setOnClickListener(this);
        level_hour.setOnClickListener(this);
        level_count.setOnClickListener(this);


        levelRanking.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    return;
                }
                LevelListBean bean = levelRankingData.get(position);
                if (bean.isTitle) {

                } else {
                    Intent intent = new Intent(ActivityRainCount.this, ActivityLiveQueryDetail.class);
                    intent.putExtra("stationName", bean.stationData.area_name);
                    intent.putExtra("item", "rain");
                    startActivity(intent);
                }
            }
        });

    }

    /**
     * 初始化数据
     */
    private void initData() {
        dataControl = DataControl.getInstance(ActivityRainCount.this);
        init_1_3Hour();
        init_24_hour();
        init_level();
        request();
    }

    /**
     * 雨量分析统计
     */
    private void init_level() {
        levelRankingData = new ArrayList<LevelListBean>();
        levelRankingAdatper = new AdapterRainLevelCount(ActivityRainCount.this, levelRankingData, dataControl);
        levelRanking.setAdapter(levelRankingAdatper);
    }

    /**
     * 24小时最大雨量统计
     */
    private void init_24_hour() {
        _24_max_ranking_data = new ArrayList<RainFallRank>();
        _24_max_ranking_data.add(dataControl.getMax24Title());
        _24_max_ranking_adatper = new AdapterTempertureRainFall(
                ActivityRainCount.this, _24_max_ranking_data);
        _24_max_ranking.setAdapter(_24_max_ranking_adatper);
    }

    /**
     * 1、3小时统计
     */
    private void init_1_3Hour() {
        _1_3_hour_max_data = new ArrayList<RainFallRank>();
        _1_3_hour_max_data.add(dataControl.getMax24Title());
        _1_3_hour_max_adapter = new AdapterTempertureRainFall(
                ActivityRainCount.this, _1_3_hour_max_data);
        _1_3_hour_max.setAdapter(_1_3_hour_max_adapter);
    }

    /**
     * 刷新数据
     */
    public void reflushData() {
        // 跟新24小時最大降雨量排名
        handleMax24Rain.sendEmptyMessage(rainMax24CheckPosition);
        // 刷新1、3小时数据
        reflush_1_3Hour();
        reflush_level();
    }

    private int level_hour_position = 0;
    private int level_count_position = 0;

    /**
     * 降雨量等级统计分析
     */
    private void reflush_level() {
        if (dataControl.getLevel_data().size() != 0) {
            showLevelList(false);
            progressbar.setVisibility(View.VISIBLE);
            level_hour.setText(dataControl.getLevel_data().get(
                    level_hour_position).name);
            level_count
                    .setText(dataControl.getLevel_data().get(
                            level_hour_position).subDataList
                            .get(level_count_position).name);
            // 获取相应区间对应的值
            reqLevelData(dataControl.getLevel_data().get(level_hour_position).subDataList
                    .get(level_count_position).sign);
        }
    }

    private void reqLevelData(String sign) {
        dataControl.getLevelData(sign);
    }

    /**
     * 刷新1、3小时数据
     */
    private void reflush_1_3Hour() {
        _1_3_hour_max_data.clear();
        // 头部标题栏目
        _1_3_hour_max_data.add(dataControl.getMax24Title());
        _1_3_hour_max_data.addAll(dataControl.get_1_3_hourRanking());
        _1_3_hour_max_adapter.notifyDataSetChanged();
    }

    private ProgressBar progressbar;

    private void initView() {
        _1_3_hour_max = (MyListView) findViewById(R.id._1_3_hour_max);
        _24_max_ranking = (MyListView) findViewById(R.id.livequery_auto_rainfall);
        levelRanking = (MyListView) findViewById(R.id.livequery_max_rainfall);
        _24MaxDrawTextView = (TextView) findViewById(R.id.livequery_totime);
//        to_image_btn = (RelativeLayout) findViewById(to_image_btn);
        level_hour = (TextView) findViewById(R.id.level_hour);
        level_count = (TextView) findViewById(R.id.level_count);
        level_release = (TextView) findViewById(R.id.level_release);
        level_count_time = (TextView) findViewById(R.id.level_count_time);

        progressbar = (ProgressBar) findViewById(R.id.progressbar);
        btn_jyqddjhf = (Button) findViewById(R.id.btn_jyqddjhf);
    }

    private void showLevelList(boolean show) {
        if (show) {
            progressbar.setVisibility(View.GONE);
            levelRanking.setVisibility(View.VISIBLE);
        } else {
            progressbar.setVisibility(View.VISIBLE);
            levelRanking.setVisibility(View.GONE);
        }
    }

    /**
     * 24小时最大降雨量排名Handler
     */
    private Handler handleMax24Rain = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            rainMax24CheckPosition = msg.what;
            _24_max_ranking_data.clear();
            // 头部标题栏目
            _24_max_ranking_data.add(dataControl.getMax24Title());
            switch (rainMax24CheckPosition) {
                case 0:
                    // 1
                    _24_max_ranking_data.addAll(dataControl.getRain24Max1Hour());
                    _24_max_ranking_adatper.notifyDataSetChanged();
                    break;
                case 1:
                    // 3
                    _24_max_ranking_data.addAll(dataControl.getRain24Max3Hour());
                    _24_max_ranking_adatper.notifyDataSetChanged();
                    break;
                case 2:
                    // 6
                    _24_max_ranking_data.addAll(dataControl.getRain24Max6Hour());
                    _24_max_ranking_adatper.notifyDataSetChanged();
                    break;
                case 3:
                    // 12

                    _24_max_ranking_data.addAll(dataControl.getRain24Max12Hour());
                    _24_max_ranking_adatper.notifyDataSetChanged();
                    break;
                case 4:
                    // 24
                    _24_max_ranking_data.addAll(dataControl.getRain24Max24Hour());
                    _24_max_ranking_adatper.notifyDataSetChanged();
                    break;
            }
        }
    };
    private PopupWindow pop24MaxDraw;
    private PopupWindow popLevelHourDraw;
    private PopupWindow popLevelCountDraw;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.livequery_totime:
                // 小时选择列表
                if (pop24MaxDraw == null) {
                    pop24MaxDraw = dataControl.createPopupWindow(
                            _24MaxDrawTextView, dataControl.getMaxDraw(),
                            listenermax, MAX24);
                }
                pop24MaxDraw.showAsDropDown(_24MaxDrawTextView);
                break;
//            case R.id.to_image_btn:
            // 福建省雨量分布图
//                Intent it = new Intent(ActivityRainCount.this, RainFallImage.class);
//                startActivity(it);
//                break;
            case R.id.level_hour:
                if (dataControl.getLevel_data().size() != 0) {

                    // 降雨量统计分析小时
                    if (popLevelHourDraw == null) {
                        popLevelHourDraw = dataControl.createPopupWindow(
                                level_hour, dataControl.getLevelHour(),
                                listenermax, LEVEHOUR);
                    }
                    // if (dataControl.getLevelData().size() < 2) {
                    // //
                    // popLevelHourDraw.setHeight(Util.dip2px(ActivityRainCount.this,
                    // // 90));
                    // // popLevelHourDraw.setWidth(LayoutParams.MATCH_PARENT);
                    // popLevelHourDraw.setWindowLayoutMode(level_hour.getWidth(),
                    // Util.dip2px(ActivityRainCount.this, 90));
                    // } else {
                    // popLevelHourDraw.setWindowLayoutMode(level_hour.getWidth(),
                    // LayoutParams.WRAP_CONTENT);
                    // // popLevelHourDraw.setWidth(LayoutParams.MATCH_PARENT);
                    // }
                    popLevelHourDraw.showAsDropDown(level_hour);
                }
                break;
            case R.id.level_count:
                // 降雨量统计分析区间
                if (dataControl.getLevel_data().size() != 0) {
                    popLevelCountDraw = dataControl.createPopupWindow(
                            level_count,
                            dataControl.getLevelCount(level_hour_position),
                            listenermax, LEVECOUNT);
                    // if (dataControl.getLevelData().size() < 2) {
                    // popLevelCountDraw.setWindowLayoutMode(level_count.getWidth(),
                    // Util.dip2px(ActivityRainCount.this, 90));
                    // } else {
                    // popLevelCountDraw.setWindowLayoutMode(level_count.getWidth(),
                    // LayoutParams.WRAP_CONTENT);
                    // }
                    popLevelCountDraw.showAsDropDown(level_count);
                }
                break;
            case R.id.btn_jyqddjhf:
                showPopWindow();

                break;
            case R.id.btn_delete:
                if (popDwon.isShowing()) {
                    popDwon.dismiss();
                }
                break;
        }
    }

    private ImageView btn_delete;
    private PopupWindow popDwon;

    private void showPopWindow() {
        if (popDwon == null) {
            popDwon = new PopupWindow(this);
            View view = LayoutInflater.from(this).inflate(R.layout.pop_rain, null);
            popDwon.setContentView(view);
            ListView listview_pop = (ListView) view.findViewById(R.id.listview_pop);
            btn_delete = (ImageView) view.findViewById(R.id.btn_delete);
            if (dataControl.getRainStanderdDown() != null) {
                AdapterRainStandard adapter = new AdapterRainStandard(dataControl.getRainStanderdDown().dataList);
                listview_pop.setAdapter(adapter);
            }
            int screenHight = Util.getScreenHeight(this);
            popDwon.setHeight(screenHight);
            popDwon.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
            popDwon.setFocusable(true);
            popDwon.setOutsideTouchable(false);
            int hight = getWindowManager().getDefaultDisplay().getHeight();
            popDwon.setHeight((hight / 5) * 2);
            btn_delete.setOnClickListener(this);
        }
        if (!popDwon.isShowing()) {
            popDwon.showAtLocation(all_view, Gravity.BOTTOM, 0, 0);
        }
    }

    private final String MAX24 = "Max24";
    private final String LEVEHOUR = "hour";
    private final String LEVECOUNT = "count";
    private ListSelect listenermax = new ListSelect() {
        @Override
        public void itemClick(int position, String key) {
            if (MAX24.equals(key)) {
                handleMax24Rain.sendEmptyMessage(position);
            } else if (LEVEHOUR.equals(key)) {
                level_hour_position = position;
                level_count_position = 0;
                reflush_level();

            } else if (LEVECOUNT.equals(key)) {
                level_count_position = position;
                reflush_level();
            }
        }
    };

    /**
     * 获取数据
     */
    private void request() {
        showProgressDialog();
        // 网络获取数据
        DataControl.getInstance(this).request();
    }

    private class MyReceiver extends PcsDataBrocastReceiver {
        @Override
        public void onReceive(String name, String errorStr) {
            if (name.startsWith(PackYltjMaxHourUp.NAME)) {
                dismissProgressDialog();
                if (TextUtils.isEmpty(errorStr) || errorStr.equals("null")) {
                    dataControl.successDealWidth();
                    reflushData();
                }
            } else if (name.startsWith(PackYltj_level_ranking_detailUp.NAME)) {
                dismissProgressDialog();
                if (TextUtils.isEmpty(errorStr) || errorStr.equals("null")) {
                    // 解析数据
                    dataControl.successLevelDetail();
                    showLevelList(true);
                    levelRankingData.clear();
                    levelRankingData.addAll(dataControl.getLevelData());
                    levelRankingAdatper.notifyDataSetChanged();
                    level_release.setText("发布时间："
                            + dataControl.getLevel_release_time());
                    level_count_time.setText("统计时段："
                            + dataControl.getLevel_start_time());
                }
            } else if (name.startsWith(PackRainStandardUp.NAME)) {
                dismissProgressDialog();
                dataControl.successRainStandard();
            }
        }
    }
}