package com.pcs.ztqtj.control.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.lib_ztqfj_v2.model.pack.net.satellite.PackSatelliteListDown;

public class AdatperSatelliteCloudTab extends BaseAdapter {
	private final Context context;
	private final PackSatelliteListDown mSatelliteList;

	public AdatperSatelliteCloudTab(Context context,
			PackSatelliteListDown satelliteList) {
		this.context = context;
		this.mSatelliteList = satelliteList;
	}

	int itemid = 0;

	public void setItemId(int itmeid) {
		this.itemid = itmeid;
	}

	@Override
	public int getCount() {
		if (mSatelliteList == null
				|| mSatelliteList.nephanalysis_list.isEmpty()) {
			return 0;
		}
		return mSatelliteList.nephanalysis_list.size();
	}

	@Override
	public Object getItem(int position) {
		if (mSatelliteList == null
				|| mSatelliteList.nephanalysis_list.isEmpty()) {
			return null;
		}
		return mSatelliteList.nephanalysis_list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null)
			convertView = LayoutInflater.from(context).inflate(
					R.layout.typhoon_list_item, null);
		((TextView) convertView).setText(mSatelliteList.nephanalysis_list
				.get(position).name);
		return convertView;
	}

}
