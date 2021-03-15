package com.pcs.ztqtj.control.adapter.adapter_set;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.pcs.ztqtj.R;

public class AdapterZtqLinks extends BaseAdapter{

	private Context mContext = null;
	private List<Map<String, String>> listData = null;
	
	public AdapterZtqLinks(Context context, List<Map<String, String>> listData) {
		mContext = context;
		this.listData = listData;
	}
	
	@Override
	public int getCount() {
		return listData.size();
	}

	@Override
	public Object getItem(int position) {
		return listData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder = null;
		if(convertView == null) {
			holder = new Holder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_fragmentset, null);
			holder.textView = (TextView) convertView.findViewById(R.id.explain_text);
			holder.icon = (ImageView) convertView.findViewById(R.id.setimage_icon);
			holder.choosebutton = (CheckBox) convertView.findViewById(R.id.open_icon);
			holder.subText = (TextView) convertView.findViewById(R.id.sub_text);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		holder.icon.setImageResource(Integer.parseInt((listData
				.get(position).get("i"))));
		holder.choosebutton.setVisibility(View.GONE);
		holder.textView.setText(listData.get(position).get("t"));
		if(position == 0) {
			holder.subText.setText(listData.get(position).get("st"));
		}
		return convertView;
	}
	
	class Holder {
		public TextView textView;
		public ImageView icon;
		public CheckBox choosebutton;
		public TextView subText;
	}

}
