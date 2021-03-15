package com.pcs.ztqtj.control.adapter.waterflood;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.listener.OnMyItemClickListener;
import com.pcs.lib_ztqfj_v2.model.pack.net.waterflood.WaterMonitorInfo;

import java.util.List;

/**
 * 统计雨量适配器
 * @author tya
 *
 */
public class AdapterWaterMonitorCount extends BaseAdapter{

	private Context mContext = null;
	private List<WaterMonitorInfo> listdata = null;
	private OnMyItemClickListener l = null;

	public AdapterWaterMonitorCount(Context context, List<WaterMonitorInfo> listdata) {
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
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_water_monitor_count, null);
			holder.ll = (LinearLayout) convertView.findViewById(R.id.ll);
			holder.tvStationName = (TextView) convertView.findViewById(R.id.tv_station_m);
			holder.tvWaterTime = (TextView) convertView.findViewById(R.id.tv_water_time);
			holder.tvWater_h = (TextView) convertView.findViewById(R.id.tv_water_h);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		if(position%2 == 1) {
			holder.ll.setBackgroundResource(R.color.water_grid_head_grey);
		} else {
			holder.ll.setBackgroundResource(R.color.text_white);
		}

        WaterMonitorInfo rsi = listdata.get(position);
        holder.tvStationName.setText(rsi.station_name);
		holder.tvWaterTime.setText(rsi.rectime);
		holder.tvWater_h.setText(rsi.js_h);
        if (rsi.lev.equals("重度")) {
            holder.tvWater_h.setTextColor(mContext.getResources().getColor(R.color.warn_red));
        } else if (rsi.lev.equals("中度")) {
            holder.tvWater_h.setTextColor(mContext.getResources().getColor(R.color.curve_orange));
        } else {
            holder.tvWater_h.setTextColor(mContext.getResources().getColor(R.color.text_gray));
        }
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
		TextView tvStationName;
		TextView tvWaterTime;
		TextView tvWater_h;
	}

}
