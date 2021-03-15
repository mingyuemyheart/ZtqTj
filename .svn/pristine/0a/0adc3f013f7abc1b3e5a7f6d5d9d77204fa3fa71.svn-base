package com.pcs.ztqtj.control.adapter.waterflood;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap.OnMyLocationChangeListener;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.listener.OnMyItemClickListener;
import com.pcs.lib_ztqfj_v2.model.pack.net.waterflood.RainStationInfo;

/**
 * 统计雨量适配器
 * @author tya
 *
 */
public class AdapterRainInfoCount extends BaseAdapter{

	private Context mContext = null;
	private List<RainStationInfo> listdata = null;
	private OnMyItemClickListener l = null;
	
	public AdapterRainInfoCount(Context context, List<RainStationInfo> listdata) {
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if(convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_rain_info_count, null);
			holder.ll = (LinearLayout) convertView.findViewById(R.id.ll);
			holder.tvIndex = (TextView) convertView.findViewById(R.id.tv_index);
			holder.tvCity = (TextView) convertView.findViewById(R.id.tv_city);
			holder.tvcounty = (TextView) convertView.findViewById(R.id.tv_county);
			holder.tvStationName = (TextView) convertView.findViewById(R.id.tv_station_name);
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
		
		RainStationInfo rsi = listdata.get(position);
		holder.tvIndex.setText(rsi.num);
		holder.tvCity.setText(rsi.city);
		holder.tvcounty.setText(rsi.county);
		holder.tvStationName.setText(rsi.station);
		holder.tvRain.setText(rsi.rain);
		if(l != null) {
			holder.tvStationName.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					l.onItemClick(position, null);
				}
			});
		}
		return convertView;
	}
	
	public void setOnClickListener(OnMyItemClickListener l) {
		this.l = l;
	}
	
	private class ViewHolder {
		LinearLayout ll;
		TextView tvIndex;
		TextView tvCity;
		TextView tvcounty;
		TextView tvStationName;
		TextView tvRain;
	}

}
