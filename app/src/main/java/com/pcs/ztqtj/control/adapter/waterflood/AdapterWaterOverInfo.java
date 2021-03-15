package com.pcs.ztqtj.control.adapter.waterflood;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.lib_ztqfj_v2.model.pack.net.waterflood.WaterOverInfo;

/**
 * 水位信息-超防汛适配器
 * 
 * @author Administrator
 *
 */
public class AdapterWaterOverInfo extends BaseAdapter {

	private Context mContext = null;
	private List<WaterOverInfo> listdata = null;

	public AdapterWaterOverInfo(Context context, List<WaterOverInfo> listdata) {
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
					R.layout.item_water_info_over, null);
			holder.ll = (LinearLayout) convertView.findViewById(R.id.ll);
			holder.tvNum = (TextView) convertView.findViewById(R.id.tv_num);
			holder.tvCounty = (TextView) convertView.findViewById(R.id.tv_county);
			holder.tvStation = (TextView) convertView.findViewById(R.id.tv_station);
			holder.tvBeyond = (TextView) convertView.findViewById(R.id.tv_beyond);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		int color = R.color.text_white;
		if(position%2 == 1) {
			color = R.color.water_grid_head_grey;
		}
		holder.ll.setBackgroundResource(color);
		
		WaterOverInfo woi = listdata.get(position);
		holder.tvNum.setText(woi.num);
		holder.tvCounty.setText(woi.county);
		holder.tvStation.setText(woi.station);
		holder.tvBeyond.setText(woi.beyond);
		return convertView;
	}

	private class ViewHolder {
		LinearLayout ll;
		TextView tvNum;
		TextView tvCounty;
		TextView tvStation;
		TextView tvBeyond;
	}

}
