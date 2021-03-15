package com.pcs.ztqtj.view.fragment.livequery.all_country;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.AdapterRainStandard;
import com.pcs.ztqtj.control.adapter.livequery.AdapterRainLevelCount;
import com.pcs.ztqtj.control.adapter.livequery.AdapterTempertureRainFall;
import com.pcs.ztqtj.view.activity.livequery.ActivityLiveQuery;
import com.pcs.ztqtj.view.activity.livequery.ActivityLiveQueryDetail;
import com.pcs.ztqtj.view.activity.livequery.DataControl;
import com.pcs.ztqtj.view.fragment.livequery.FragmentLiveQueryCommon;
import com.pcs.ztqtj.view.myview.MyListView;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackRainStandardUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.LevelListBean;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackYltjMaxHourUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackYltjRankDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackYltj_level_ranking_detailUp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Z on 2017/6/6.
 * 全省雨量查询
 */

public class FragmentRainCountry extends FragmentLiveQueryCommon implements View.OnClickListener {
    private ActivityLiveQuery activity;
    private ScrollView layout;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (ActivityLiveQuery) activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_raincount, null);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        PcsDataBrocastReceiver.registerReceiver(getActivity(), receiver);
        initView();
        initData();
        initEvent();
    }

    private TextView _24MaxDrawTextView;
    /**
     * 数据控制中心
     */
    private DataControl dataControl;

    // 1、3小时列表
    private MyListView _1_3_hour_max;
    private List<PackYltjRankDown.RainFallRank> _1_3_hour_max_data;
    private AdapterTempertureRainFall _1_3_hour_max_adapter;

    // 24小时列表
    private MyListView _24_max_ranking;
    private AdapterTempertureRainFall _24_max_ranking_adatper;
    private List<PackYltjRankDown.RainFallRank> _24_max_ranking_data;

    // 降雨量分级统计
    private MyListView levelRanking;
    private List<LevelListBean> levelRankingData;
    private AdapterRainLevelCount levelRankingAdatper;

    private TextView level_hour;
    private TextView level_count;
    /**
     * 当前选择的是几小时
     */
    private int rainMax24CheckPosition = 0;

    private ProgressBar progressbar;
    private Button btn_jyqddjhf;

    private void initView() {
        layout = (ScrollView) getView().findViewById(R.id.root_layout);
        _1_3_hour_max = (MyListView) getActivity().findViewById(R.id._1_3_hour_max);
        _24_max_ranking = (MyListView) getActivity().findViewById(R.id.livequery_auto_rainfall);
        levelRanking = (MyListView) getActivity().findViewById(R.id.livequery_max_rainfall);
        _24MaxDrawTextView = (TextView) getActivity().findViewById(R.id.livequery_totime);
        level_hour = (TextView) getActivity().findViewById(R.id.level_hour);
        level_count = (TextView) getActivity().findViewById(R.id.level_count);
        progressbar = (ProgressBar) getActivity().findViewById(R.id.progressbar);
        btn_jyqddjhf = (Button) getActivity().findViewById(R.id.btn_jyqddjhf);
    }

    private void initData() {
        dataControl = DataControl.getInstance(getActivity());
        init_1_3Hour();
        init_24_hour();
        init_level();
        request();
    }

    /**
     * 获取数据
     */
    private void request() {
        activity.showProgressDialog();
        // 网络获取数据
        DataControl.getInstance(getActivity()).request();
    }

    private void scrlltoTop() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                layout.scrollTo(0,0);
            }
        },50);
    }

    /**
     * 雨量分析统计
     */
    private void init_level() {
        levelRankingData = new ArrayList<LevelListBean>();
        levelRankingAdatper = new AdapterRainLevelCount(getActivity(), levelRankingData, dataControl);
        levelRanking.setAdapter(levelRankingAdatper);
    }

    /**
     * 24小时最大雨量统计
     */
    private void init_24_hour() {
        _24_max_ranking_data = new ArrayList<>();
        _24_max_ranking_data.add(dataControl.getMax24Title());
        _24_max_ranking_adatper = new AdapterTempertureRainFall(
                getActivity(), _24_max_ranking_data);
        _24_max_ranking.setAdapter(_24_max_ranking_adatper);
    }

    /**
     * 1、3小时统计
     */
    private void init_1_3Hour() {
        _1_3_hour_max_data = new ArrayList<>();
        _1_3_hour_max_data.add(dataControl.getMax24Title());
        _1_3_hour_max_adapter = new AdapterTempertureRainFall(getActivity(), _1_3_hour_max_data);
        _1_3_hour_max.setAdapter(_1_3_hour_max_adapter);
    }


    private void initEvent() {
        _24MaxDrawTextView.setOnClickListener(this);
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
                    toDetail(bean.stationData.area_name);
                }
            }


        });

        _1_3_hour_max.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    return;
                }
                toDetail(_1_3_hour_max_data.get(position).area_name);

            }
        });

        _24_max_ranking.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    return;
                }
                toDetail(_24_max_ranking_data.get(position).area_name);
            }
        });


    }

    private void toDetail(String stationName) {
        if (stationName.equals("全部")) {
            return;
        }
        Intent intent = new Intent(getActivity(), ActivityLiveQueryDetail.class);
        intent.putExtra("stationName", stationName);
        intent.putExtra("item", "rain");
        startActivity(intent);
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
        scrlltoTop();
    }

    @Override
    public void refleshData() {

        // 重新解析数据数据
        dataControl.successDealWidth();
        // 刷新视图数据
        brReflush();
    }

    private void brReflush() {
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
            level_hour.setText(dataControl.getLevel_data().get(level_hour_position).name);
            level_count.setText(dataControl.getLevel_data().get(level_hour_position).subDataList.get(level_count_position).name);
            // 获取相应区间对应的值
            reqLevelData(dataControl.getLevel_data().get(level_hour_position).subDataList.get(level_count_position).sign);
        }
    }

    private void reqLevelData(String sign) {
        dataControl.getLevelData(sign);
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
            scrlltoTop();
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
            case R.id.level_hour:
                if (dataControl.getLevel_data().size() != 0) {
                    // 降雨量统计分析小时
                    if (popLevelHourDraw == null) {
                        popLevelHourDraw = dataControl.createPopupWindow(
                                level_hour, dataControl.getLevelHour(),
                                listenermax, LEVEHOUR);
                    }
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
            popDwon = new PopupWindow(getActivity());
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.pop_rain, null);
            popDwon.setContentView(view);

            ListView listview_pop = (ListView) view.findViewById(R.id.listview_pop);
            btn_delete = (ImageView) view.findViewById(R.id.btn_delete);
            if (dataControl.getRainStanderdDown() != null) {
                AdapterRainStandard adapter = new AdapterRainStandard(dataControl.getRainStanderdDown().dataList);
                listview_pop.setAdapter(adapter);
            }
            popDwon.setFocusable(true);
//            popDwon.setBackgroundDrawable(new BitmapDrawable());
            popDwon.setOutsideTouchable(false);
            int hight = getActivity().getWindowManager().getDefaultDisplay().getHeight();
            int width = getActivity().getWindowManager().getDefaultDisplay().getWidth();
            popDwon.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
            popDwon.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
//            popDwon.setWidth((width / 10) * 9);
//            int hight = getActivity().getWindowManager().getDefaultDisplay().getHeight();
//            popDwon.setHeight((hight / 5) * 2);
            btn_delete.setOnClickListener(this);
        }

        TextView textView = (TextView) popDwon.getContentView().findViewById(R.id.text_info);
        String str = "发布时间：" + dataControl.getLevel_release_time() + "\n" + "统计时段：" + dataControl.getLevel_start_time();
        textView.setText(str);

        if (!popDwon.isShowing()) {
            View view = getView().findViewById(R.id.root_layout);
            popDwon.showAtLocation(view, Gravity.BOTTOM, 0, 0);
        }
    }

    private final String MAX24 = "Max24";
    private final String LEVEHOUR = "hour";
    private final String LEVECOUNT = "count";
    private DataControl.ListSelect listenermax = new DataControl.ListSelect() {
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


    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(receiver);
    }

    private PcsDataBrocastReceiver receiver = new PcsDataBrocastReceiver() {
        @Override
        public void onReceive(String name, String errorStr) {
            if (name.startsWith(PackYltjMaxHourUp.NAME)) {
                activity.dismissProgressDialog();
                if (TextUtils.isEmpty(errorStr) || errorStr.equals("null")) {
                    dataControl.successDealWidth();
                    brReflush();
                }
            } else if (name.startsWith(PackYltj_level_ranking_detailUp.NAME)) {
                activity.dismissProgressDialog();
                if (TextUtils.isEmpty(errorStr) || errorStr.equals("null")) {
                    // 解析数据
                    dataControl.successLevelDetail();
                    showLevelList(true);
                    levelRankingData.clear();
                    levelRankingData.addAll(dataControl.getLevelData());
                    levelRankingAdatper.notifyDataSetChanged();
                    scrlltoTop();
                }
            } else if (name.startsWith(PackRainStandardUp.NAME)) {
                activity.dismissProgressDialog();
                dataControl.successRainStandard();
            }
        }
    };
}
