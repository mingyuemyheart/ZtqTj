package com.pcs.ztqtj.control.adapter;

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
 * 生活气象-气象科普
 */
public class AdapterLifeGridView extends BaseAdapter {
	private Context context;
	private List<Map<String, Object>> data;

	public AdapterLifeGridView(Context context,List<Map<String, Object>> data) {
		this.context = context;
		this.data=data;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder;
		if (convertView == null) {
			holder = new Holder();
			convertView = LayoutInflater.from(context).inflate(R.layout.life_gridview_item, null);
			holder.itemImage = (ImageView) convertView.findViewById(R.id.item_image);
			holder.itemText = (TextView) convertView.findViewById(R.id.item_text);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		
		holder.itemImage.setImageResource((Integer)data.get(position).get("i"));
		
		holder.itemText.setText(""+data.get(position).get("t"));
		return convertView;
	}

	class Holder {
		public TextView itemText;
		public ImageView itemImage;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public int getCount() {
		return data.size();
	}
}
