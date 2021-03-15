package com.pcs.ztqtj.control.adapter.waterflood;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pcs.ztqtj.R;

/**
 * 水利汛情产品适配器
 * @author tya
 *
 */
public class AdapterWaterFlood extends BaseAdapter{

	/**
	 * 上下文
	 */
	private Context mContext = null;
	
	/**
	 * 产品列表
	 */
	private List<Map<String, Object>> listdata = null;
	
	public AdapterWaterFlood(Context context, List<Map<String, Object>> listdata) {
		this.mContext = context;
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
		if(convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_water_flood_products, null);
			holder.tv = (TextView) convertView.findViewById(R.id.tv);
			holder.iv = (ImageView) convertView.findViewById(R.id.iv);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Map<String, Object> map = listdata.get(position);
		int id = (Integer) map.get("resid");
		String title = (String) map.get("title");
		holder.tv.setText(title);
		holder.iv.setImageResource(id);

		return convertView;
	}
	
	/**
	 * ViewHolder
	 */
	private class ViewHolder {
		TextView tv;
		ImageView iv;
	}
	
}
