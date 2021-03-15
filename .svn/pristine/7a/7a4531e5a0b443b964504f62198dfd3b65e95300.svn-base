package com.pcs.ztqtj.control.adapter.livequery;

import android.content.Context;
import android.content.Intent;
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
import com.pcs.ztqtj.view.activity.livequery.ActivityRainCountDetail;
import com.pcs.ztqtj.view.activity.livequery.DataControl;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.LevelListBean;

import java.util.List;

/**
 * @author Z 福建省降雨量分级统计
 */
public class AdapterRainLevelCount extends BaseAdapter {
	private Context context;
	private List<LevelListBean> levelData;
	private DataControl dataControl;

	public AdapterRainLevelCount(Context context, List<LevelListBean> rainfalllist, DataControl dataControl) {
		this.context = context;
		this.levelData = rainfalllist;
		this.dataControl = dataControl;
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
			view = LayoutInflater.from(context).inflate(R.layout.item_livequery_level, null);
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
		if (position == 0) {
			handler.livequery_site.setBackgroundResource(R.drawable.bg_livequery_item);
			handler.livequery_number.setBackgroundResource(R.drawable.bg_livequery_item);
			handler.livequery_value.setBackgroundResource(R.drawable.bg_livequery_item);
		} else {
			handler.livequery_site.setBackgroundColor(context.getResources().getColor(R.color.alpha100));
			handler.livequery_number.setBackgroundColor(context.getResources().getColor(R.color.alpha100));
			handler.livequery_value.setBackgroundColor(context.getResources().getColor(R.color.alpha100));
		}
	
		LevelListBean bean=levelData.get(position);
		handler.more_cityinfo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dataControl.moreInfo(position);
				Intent intent=new Intent(context,ActivityRainCountDetail.class);
				context.startActivity(intent);
			}
		});
		if (bean.isTitle) {
			handler.layout.setVisibility(View.GONE);
			handler.city_name_layout.setVisibility(View.VISIBLE);
			if (bean.isSmallTen) {
				// 不超过DataControl.ROWSIZE个
				handler.more_cityinfo.setVisibility(View.GONE);
			} else {
				handler.more_cityinfo.setVisibility(View.VISIBLE);
			}
			handler.livequery_cityname.setText(bean.titleName);
		} else {
			handler.city_name_layout.setVisibility(View.GONE);
			handler.layout.setVisibility(View.VISIBLE);
			handler.livequery_site.setText(bean.stationData.area_name);
			handler.livequery_number.setText(bean.stationData.order_id);
			handler.livequery_value.setText(bean.stationData.rainfall);
		}
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
