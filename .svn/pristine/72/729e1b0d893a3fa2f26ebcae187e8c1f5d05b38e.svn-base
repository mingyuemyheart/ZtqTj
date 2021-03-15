package com.pcs.ztqtj.control.adapter;

import java.util.List;
import java.util.Map;

import com.pcs.ztqtj.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 知天气家族适配器
 * @author tya
 *
 */
public class AdapterZTQFamily extends BaseAdapter{

	private Context mContext = null;
	private List<Map<String, String>> listdata = null;
	private LayoutInflater mInflater = null;
	
	public AdapterZTQFamily(Context context, List<Map<String, String>> listdata) {
		mContext = context;
		this.listdata = listdata;
		mInflater = LayoutInflater.from(mContext);
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
		ViewHolder viewHolder = null;
		if(convertView == null) {
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.item_ztq_family, null);
			viewHolder.textView = (TextView) convertView.findViewById(R.id.text);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.textView.setText(listdata.get(position).get("t"));
		return convertView;
	}
	
	private static class ViewHolder {
		TextView textView;
	}

}
