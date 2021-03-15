package com.pcs.ztqtj.control.adapter.livequery;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.view.activity.livequery.DataControl;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.LevelListBean;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackYltj_level_ranking_detailDown.StationData;

/**
 * @author Z 福建省降雨量分级统计
 */
public class AdapterRainLevelCountDetail extends BaseAdapter {
	private Context context;
	private List<StationData> levelData;

	public AdapterRainLevelCountDetail(Context context, List<StationData> rainfalllist) {
		this.context = context;
		this.levelData = rainfalllist;
	};

	@Override
	public int getCount() {
		return levelData.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		Handler handler = null;
		if (view == null) {
			handler = new Handler();
			view = LayoutInflater.from(context).inflate(R.layout.item_livequery_level_detail, null);
			handler.city_name_layout = (RelativeLayout) view.findViewById(R.id.city_name_layout);
			handler.livequery_cityname = (TextView) view.findViewById(R.id.livequery_cityname);
			handler.more_cityinfo = (Button) view.findViewById(R.id.more_cityinfo);

			handler.layout = (LinearLayout) view.findViewById(R.id.layout);
			handler.livequery_site = (TextView) view.findViewById(R.id.livequery_site);
			handler.livequery_number = (TextView) view.findViewById(R.id.livequery_number);
			handler.livequery_value = (TextView) view.findViewById(R.id.livequery_value);
			view.setTag(handler);
		} else {
			handler = (Handler) view.getTag();
		}
		StationData bean = levelData.get(position);
		if (position == 1) {
			handler.layout.setVisibility(View.GONE);
			handler.city_name_layout.setVisibility(View.VISIBLE);
			handler.livequery_cityname.setText(bean.area_name);
		} else {
			handler.city_name_layout.setVisibility(View.GONE);
			handler.layout.setVisibility(View.VISIBLE);
		}

		// if (bean.isTitle) {
		// handler.layout.setVisibility(View.GONE);
		// handler.city_name_layout.setVisibility(View.VISIBLE);
		//
		// if (bean.isSmallTen) {
		// // 不超过10个
		// handler.more_cityinfo.setVisibility(View.GONE);
		// } else {
		// handler.more_cityinfo.setVisibility(View.VISIBLE);
		// }
		//
		// handler.livequery_cityname.setText(bean.titleName);
		// } else {
		// handler.city_name_layout.setVisibility(View.GONE);
		// handler.layout.setVisibility(View.VISIBLE);
		handler.livequery_site.setText(bean.area_name);
		handler.livequery_number.setText(bean.order_id);
		handler.livequery_value.setText(bean.rainfall);
		// }

		return view;
	}

	private class Handler {
		public RelativeLayout city_name_layout;
		public TextView livequery_cityname;
		public Button more_cityinfo;
		public LinearLayout layout;
		public TextView livequery_site;
		public TextView livequery_number;
		public TextView livequery_value;
	}
}
