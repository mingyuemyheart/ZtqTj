package com.pcs.ztqtj.control.adapter.waterflood;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pcs.ztqtj.R;

/**
 * 雨情信息-图例适配器
 * 
 * @author tya
 *
 */
public class AdapterRainInfoLegend extends BaseAdapter {

	private Context mContext = null;
	private List<String> listdata = null;
	private int[] resIDs = { R.drawable.icon_legend_0,
			R.drawable.icon_legend_1, R.drawable.icon_legend_2,
			R.drawable.icon_legend_3, R.drawable.icon_legend_4,
			R.drawable.icon_legend_5, R.drawable.icon_legend_6 };

	public AdapterRainInfoLegend(Context context, List<String> listdata) {
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
					R.layout.item_rain_info_legend, null);
			holder.iv = (ImageView) convertView.findViewById(R.id.iv);
			holder.tv = (TextView) convertView.findViewById(R.id.tv);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		int index = (int) (position/2.0f);
		holder.iv.setImageResource(resIDs[index]);
		holder.tv.setText(listdata.get(position));

		return convertView;
	}

	private class ViewHolder {
		public ImageView iv;
		public TextView tv;
	}

}
