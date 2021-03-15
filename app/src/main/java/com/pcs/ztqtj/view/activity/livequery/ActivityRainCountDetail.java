package com.pcs.ztqtj.view.activity.livequery;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.livequery.AdapterRainLevelCountDetail;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackYltj_level_ranking_detailDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackYltj_level_ranking_detailDown.StationData;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Z
 * 雨量综合分析详情
 */
public class ActivityRainCountDetail extends FragmentActivityZtqBase {

	// 降雨量分级统计
	private ListView levelRanking;
	private List<StationData> levelRankingData;
	private AdapterRainLevelCountDetail levelRankingAdatper;
	/** 发布时间 */
	private TextView level_release;
	/** 发布时段统计 */
	private TextView level_count_time;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_raincount_detail);
		setTitleText("降雨量分级统计");
		initView();
		initData();
		initEvent();
	}

	private void initView() {
		levelRanking = (ListView) findViewById(R.id.livequery_max_rainfall);
		level_release = (TextView) findViewById(R.id.level_release);
		level_count_time = (TextView) findViewById(R.id.level_count_time);

	}

	private DataControl dataControl;

	private void initData() {
		dataControl = DataControl.getInstance(ActivityRainCountDetail.this);
		levelRankingData = new ArrayList<StationData>();
		PackYltj_level_ranking_detailDown down = new PackYltj_level_ranking_detailDown();
		StationData stationData = down.new StationData();
		stationData.area_name = "站点";
		stationData.order_id = "序号";
		stationData.rainfall = "雨量（mm）";
		StationData station2 = down.new StationData();
		station2.area_name = dataControl.getMoreData().titleName;
		station2.order_id = "";
		station2.rainfall = "";
		levelRankingData.add(stationData);
		levelRankingData.add(station2);//第二个用于显示省级地区
		levelRankingData.addAll(dataControl.getMoreData().rain_station_list);
		levelRankingAdatper = new AdapterRainLevelCountDetail(ActivityRainCountDetail.this, levelRankingData);
		levelRanking.setAdapter(levelRankingAdatper);
		level_release.setText("发布时间：" + dataControl.getLevel_release_time());
		level_count_time.setText("统计时段：" + dataControl.getLevel_start_time());
	}

	private void initEvent() {
		levelRanking.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(position>1){
					Intent intent = new Intent(ActivityRainCountDetail.this, ActivityLiveQueryDetail.class);
					intent.putExtra("stationName", levelRankingData.get(position).area_name);
					intent.putExtra("item", "rain");
					startActivity(intent);
				}
			}
		});
	}
}
