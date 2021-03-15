package com.pcs.ztqtj.control.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.lib_ztqfj_v2.model.pack.net.lightning.PackThunderMoreListDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.lightning.PackThunderMoreListDown.AreaInfo;

public class AdapterThunderMoreList extends BaseAdapter {
	private Context context;
	//private PackThunderMoreListDown packThunderMoreListDown;
	private List<AreaInfo> listdata = null;

	public AdapterThunderMoreList(Context context,
			List<AreaInfo> listdata) {
		this.context = context;
		this.listdata = listdata;
	}

	public void setData(List<AreaInfo> listdata) {
		this.listdata.addAll(listdata);
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		if (listdata != null
				&& listdata.size() > 0) {
			return listdata.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LightHolder handler;
		if (convertView == null) {
			handler = new LightHolder();
			convertView = parent.inflate(context,
					R.layout.item_thunder_more_list, null);
			handler.tv_data_time = (TextView) convertView
					.findViewById(R.id.tv_data_time);
			handler.tv_data_longitude = (TextView) convertView
					.findViewById(R.id.tv_data_longitude);
			handler.tv_data_latitude = (TextView) convertView
					.findViewById(R.id.tv_data_latitude);
			handler.tv_data_type = (TextView) convertView
					.findViewById(R.id.tv_data_type);
			handler.tv_data_strength = (TextView) convertView
					.findViewById(R.id.tv_data_strength);
			convertView.setTag(handler);
		} else {
			handler = (LightHolder) convertView.getTag();
		}
		if (listdata != null && listdata.size() > 0) {
			AreaInfo areainfo = listdata.get(position);
			handler.tv_data_time.setText(areainfo.time);
			handler.tv_data_longitude.setText(areainfo.longitude);
			handler.tv_data_latitude.setText(areainfo.latitude);
			handler.tv_data_type.setText(areainfo.type);
			handler.tv_data_strength.setText(areainfo.intens);
		}

		return convertView;
	}

	private class LightHolder {
		/**
		 * 时间
		 */
		public TextView tv_data_time;
		/**
		 * 经度
		 */
		public TextView tv_data_longitude;
		/**
		 * 纬度
		 */
		public TextView tv_data_latitude;
		/**
		 * 类型
		 */
		public TextView tv_data_type;
		/**
		 * 强度
		 */
		public TextView tv_data_strength;

	}
}
