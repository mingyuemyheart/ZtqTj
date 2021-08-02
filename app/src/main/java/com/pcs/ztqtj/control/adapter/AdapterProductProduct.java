package com.pcs.ztqtj.control.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.utils.TextUtil;
import com.pcs.ztqtj.util.ColumnDto;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * 监测预报
 */
public class AdapterProductProduct extends BaseAdapter {

	private Context context;
	private ArrayList<ColumnDto> dataList;

	public AdapterProductProduct(Context context, ArrayList<ColumnDto> dataList) {
		this.context = context;
		this.dataList = dataList;
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
			view = LayoutInflater.from(context).inflate(R.layout.adapter_fragment_product, null);
			holder.itemImage = view.findViewById(R.id.item_image);
			holder.itemName = view.findViewById(R.id.item_name);
			view.setTag(holder);
		} else {
			holder = (Holder) view.getTag();
		}

		ColumnDto data = dataList.get(position);

		if (data.dataName != null) {
			holder.itemName.setText(data.dataName);
		}

		if (!TextUtil.isEmpty(data.icon)) {
			Picasso.get().load(context.getResources().getString(R.string.msyb)+data.icon).error(R.drawable.no_pic).into(holder.itemImage);
		} else {
			holder.itemImage.setImageResource(R.drawable.no_pic);
		}

		return view;
	}

	private class Holder {
		public ImageView itemImage;
		public TextView itemName;
	}
}
