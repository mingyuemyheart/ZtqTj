package com.pcs.ztqtj.control.adapter.air_quality;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.lib_ztqfj_v2.model.pack.net.airinfopack.PackAirStationDown;

/**
 * 适配器：空气质量站点
 * 
 * @author JiangZy
 * 
 */
public class AdapterAirStation extends BaseAdapter {
	private Context mContext;
	private String mCityName;
	private PackAirStationDown mPack = null;

	public AdapterAirStation(Context context) {
		mContext = context;
	}

	/**
	 * 设置数据
	 * 
	 * @param cityName
	 * @param pack
	 */
	public void setData(String cityName, PackAirStationDown pack) {
		mCityName = cityName;
		mPack = pack;
	}

	@Override
	public int getCount() {
		if (mPack == null) {
			return 1;
		}
		return mPack.list.size() + 1;
	}

	@Override
	public Object getItem(int position) {
		if (position == 0) {
			return mCityName;
		}

		return mPack.list.get(position - 1).position_name;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		if (view == null) {
			view = LayoutInflater.from(mContext).inflate(
					R.layout.item_air_station, null);
		}
		TextView textView = null;
		// 名称
		textView = (TextView) view.findViewById(R.id.textcontent);
		textView.setText((String) getItem(position));

		return view;
	}

}
