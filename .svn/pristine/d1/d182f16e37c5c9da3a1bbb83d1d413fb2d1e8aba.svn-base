package com.pcs.ztqtj.control.adapter.waterflood;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.lib_ztqfj_v2.model.pack.net.waterflood.RainInfo;

/**
 * 气象站雨量信息适配器
 * 
 * @author Administrator
 *
 */
public class AdapterRainInfoStation extends BaseAdapter {

	private Context mContext = null;
	private List<RainInfo> listdata = null;

	public AdapterRainInfoStation(Context context, List<RainInfo> listdata) {
		mContext = context;
		this.listdata = listdata;
	}

	@Override
	public int getCount() {
		return listdata.size();
	}

	@Override
	public Object getItem(int position) {
		return listdata.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.item_rain_info_station, null);
			holder.ll = (LinearLayout) convertView.findViewById(R.id.ll);
			holder.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
			holder.tvRain = (TextView) convertView.findViewById(R.id.tv_rain);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		if(position%2 == 1) {
			holder.ll.setBackgroundResource(R.color.water_grid_head_grey);
		} else {
			holder.ll.setBackgroundResource(R.color.text_white);
		}
		
		RainInfo ri = listdata.get(position);
		holder.tvTime.setText(ri.time);
		if(ri.rain.equals("")) {
			holder.tvRain.setText("暂无数据");
		} else {
			holder.tvRain.setText(ri.rain);
		}
		return convertView;
	}

	private class ViewHolder {
		LinearLayout ll;
		TextView tvTime;
		TextView tvRain;
	}

}
