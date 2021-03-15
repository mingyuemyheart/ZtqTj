package com.pcs.ztqtj.control.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.lib_ztqfj_v2.model.pack.net.lightning.PackThunderQuireDown.CityInfo;

public class AdapterDateQuireTow extends BaseAdapter {

	private Context mContext;
	private CityInfo cityinfo;

	public AdapterDateQuireTow(Context context, CityInfo cityinfo) {
		mContext = context;
		this.cityinfo = cityinfo;
	}

	@Override
	public int getCount() {
		if (cityinfo != null && cityinfo.thunder_list.size() > 0) {
			if (cityinfo.thunder_list.size() > 5) {
				return 5;
			}
			return cityinfo.thunder_list.size();
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

	private void initData(LightHolder handler, int i) {

		handler.tv_data_time.setText(cityinfo.thunder_list.get(i).time);
		handler.tv_data_longitude
				.setText(cityinfo.thunder_list.get(i).longitude);
		handler.tv_data_latitude.setText(cityinfo.thunder_list.get(i).latitude);
		handler.tv_data_type.setText(cityinfo.thunder_list.get(i).type);
		handler.tv_data_strength.setText(cityinfo.thunder_list.get(i).intens);

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LightHolder handler;
		if (convertView == null) {
			handler = new LightHolder();
			convertView = parent.inflate(mContext, R.layout.item_lightning_area,
					null);
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
		if (cityinfo != null && cityinfo.thunder_list.size() > 0) {
			if (cityinfo.thunder_list.size() > 5) {
				for (int j = 0; j < 5; j++) {
					if (position == j) {
						initData(handler, j);
					}
				}
			}else {
				for (int j = 0; j < cityinfo.thunder_list.size(); j++) {
					if (position == j) {
						initData(handler, j);
					}
				}
			}
		}

		return convertView;
	}

	public void setData(CityInfo cityinfo) {
		this.cityinfo = cityinfo;
		notifyDataSetChanged();
	}

	private class LightHolder {
		public TextView tv_data_time;// 时间
		public TextView tv_data_longitude;// 经度
		public TextView tv_data_latitude;// 纬度
		public TextView tv_data_type;// 类型
		public TextView tv_data_strength;// 强度

	}
}
