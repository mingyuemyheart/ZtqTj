package com.pcs.ztqtj.control.adapter.waterflood;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.lib_ztqfj_v2.model.pack.net.waterflood.PackWaterInfoDown.ItemTimeInfo;

/**
 * 水位信息列表
 * 
 */
public class AdapterWaterFloodList extends BaseAdapter {
	private List<ItemTimeInfo> listdata;

	public AdapterWaterFloodList(List<ItemTimeInfo> listdata) {
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
			convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_weather_flood_list, null);
			holder.item_time = (TextView) convertView.findViewById(R.id.item_time);
			holder.item_value = (TextView) convertView.findViewById(R.id.item_value);
			holder.item_layout = (LinearLayout) convertView.findViewById(R.id.item_layout);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (position == 0) {
			holder.item_layout.setBackgroundResource(R.color.water_grid_head_light_blue);
		} else {
			if (position % 2 == 0) {

				holder.item_layout.setBackgroundColor(parent.getContext().getResources().getColor(R.color.gray));
			} else {
				holder.item_layout.setBackgroundColor(parent.getContext().getResources().getColor(R.color.alpha100));
			}
		}
		holder.item_time.setText(listdata.get(position).detail_hour);
		holder.item_value.setText(listdata.get(position).water);
		return convertView;
	}

	/**
	 * ViewHolder
	 */
	private class ViewHolder {
		public TextView item_time;
		public TextView item_value;
		public LinearLayout item_layout;
	}

}
