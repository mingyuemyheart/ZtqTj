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

public class AdapterProductGridView extends BaseAdapter {

	private Context context;
	List<Map<String, Object>> dataList;

	public AdapterProductGridView(Context context,
			List<Map<String, Object>> data) {
		this.context = context;
		this.dataList = data;
	}
	

	public void setData(List<Map<String, Object>> data) {
		this.dataList = data;
	}

	@Override
	public int getCount() {
		return dataList.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		Holder holder;
		if (view == null) {
			holder = new Holder();
			view = LayoutInflater.from(context).inflate(
					R.layout.productgridview_content, null);
			holder.itemImage = (ImageView) view.findViewById(R.id.item_image);
			holder.itemName = (TextView) view.findViewById(R.id.item_name);
			view.setTag(holder);
		} else {
			holder = (Holder) view.getTag();
		}

		Map<String, Object> map = dataList.get(position);
		final int rid = (Integer) map.get("rid");
		final String title = map.get("title").toString();
		holder.itemImage.setImageResource(rid);
		holder.itemName.setText(title);

		return view;
	}

	private class Holder {
		public ImageView itemImage;
		public TextView itemName;
	}
}
