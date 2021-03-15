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
 * @author chenjh 列表适配器
 */
public class AdapterCommListview extends BaseAdapter {
	private Context context;
	private List<Map<String,Object>> listdata;
	private boolean isShowIcon = false;

	public AdapterCommListview(Context context, List<Map<String,Object>> listdata,boolean isShowIcon) {
		this.context = context;
		this.listdata = listdata;
		this.isShowIcon = isShowIcon;
	}

	@Override
	public int getCount() {
		return listdata.size();
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
	public View getView(final int position, View view, ViewGroup parent) {
		Holder holder;
		if (view == null) {
			holder = new Holder();
			view = LayoutInflater.from(context).inflate(
					R.layout.listview_item_comm, null);
			holder.item_tv = (TextView) view.findViewById(R.id.item_tv);
			holder.item_iv = (ImageView) view.findViewById(R.id.item_iv);
			view.setTag(holder);
		} else {
			holder = (Holder) view.getTag();
		}
		Map<String,Object> map = listdata.get(position);
		
		if(isShowIcon){
			holder.item_iv.setImageResource((Integer) map.get("rid"));
			holder.item_iv.setVisibility(View.VISIBLE);
		}else{
			holder.item_iv.setVisibility(View.GONE);
		}
		
		holder.item_tv.setText(map.get("title").toString());

		return view;
	}

	private class Holder {
		public TextView item_tv;
		public ImageView item_iv;
	}

}
