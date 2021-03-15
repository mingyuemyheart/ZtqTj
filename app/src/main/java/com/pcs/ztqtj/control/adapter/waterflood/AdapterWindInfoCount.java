package com.pcs.ztqtj.control.adapter.waterflood;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.lib_ztqfj_v2.model.pack.net.waterflood.WindInfo;

/**
 * 风情统计适配器
 * @author tya
 *
 */
public class AdapterWindInfoCount extends BaseAdapter{

	private Context mContext = null;
	private List<WindInfo> listdata = null;
	
	public AdapterWindInfoCount(Context context, List<WindInfo> listdata) {
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
		if(convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_wind_info_count, null);
			holder.ll = (LinearLayout) convertView.findViewById(R.id.ll);
			holder.tvStation = (TextView) convertView.findViewById(R.id.tv_station);
			holder.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
			holder.tvPower = (TextView) convertView.findViewById(R.id.tv_power);
			holder.tvSpeed = (TextView) convertView.findViewById(R.id.tv_speed);
			holder.tvPosition = (TextView) convertView.findViewById(R.id.tv_position);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		int color = R.color.text_white;
		if(position%2 == 1) {
			color = R.color.water_grid_head_grey;
		}
		holder.ll.setBackgroundResource(color);
		
		WindInfo info = listdata.get(position);
		holder.tvStation.setText(info.station);
		SimpleDateFormat format = new SimpleDateFormat("dd日 HH时mm分");
		String time = "";
		try {
			Date date = format.parse(info.time);
			format = new SimpleDateFormat("dd日HH时");
			time = format.format(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		holder.tvTime.setText(time);
		holder.tvPower.setText(info.power);
		holder.tvSpeed.setText(info.speed);
		holder.tvPosition.setText(info.position);
		return convertView;
	}
	
	private class ViewHolder {
		LinearLayout ll;
		TextView tvStation;
		TextView tvTime;
		TextView tvPower;
		TextView tvSpeed;
		TextView tvPosition;
	}

}
