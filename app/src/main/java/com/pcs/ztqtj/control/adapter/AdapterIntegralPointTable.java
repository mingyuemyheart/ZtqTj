package com.pcs.ztqtj.control.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pcs.ztqtj.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 整点实况列表adapter
 * @author Administrator
 *
 */
public class AdapterIntegralPointTable extends BaseAdapter{

	private Context context = null;
	private List<Map<String, String>> listdata = null;
	
	public AdapterIntegralPointTable(Context context, List<Map<String, String>> listdata) {
		this.context = context;
		this.listdata = format(listdata);
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
			convertView = LayoutInflater.from(context).inflate(R.layout.item_integral_point_table, null);
			holder.tvTime1 = (TextView) convertView.findViewById(R.id.tv_time1);
			holder.tvTime2 = (TextView) convertView.findViewById(R.id.tv_time2);
			holder.tvTemperature1 = (TextView) convertView.findViewById(R.id.tv_temperature1);
			holder.tvTemperature2 = (TextView) convertView.findViewById(R.id.tv_temperature2);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if(listdata.size() == 12) {
			Map<String, String> map = listdata.get(position);
			String time1 = map.get("time1");
			String value1 = map.get("value1");
			String time2 = map.get("time2");
			String value2 = map.get("value2");
			holder.tvTime1.setText(time1);
			holder.tvTime2.setText(time2);
			holder.tvTemperature1.setText(value1);
			holder.tvTemperature2.setText(value2);
		}
		return convertView;
	}
	
	public void setData(List<Map<String, String>> listdata) {
		this.listdata = format(listdata);
		notifyDataSetChanged();
	}
	
	private List<Map<String, String>> format(List<Map<String, String>> listdata) {
		List<Map<String, String>> result = new ArrayList<Map<String,String>>();
		if(listdata.size() == 24) {
			for(int i = 0; i < 12; i++) {
				Map<String, String> map = new HashMap<String, String>();
				Map<String, String> dataMap1 = listdata.get(i);
				Map<String, String> dataMap2 = listdata.get(i+12);
				map.put("time1", dataMap1.get("time"));
				map.put("time2", dataMap2.get("time"));
				map.put("value1", dataMap1.get("value"));
				map.put("value2", dataMap2.get("value"));
				result.add(map);
			}
		}
		return result;
	}

	private static class ViewHolder {
		public TextView tvTime1;
		public TextView tvTime2;
		public TextView tvTemperature1;
		public TextView tvTemperature2;
	}
}
