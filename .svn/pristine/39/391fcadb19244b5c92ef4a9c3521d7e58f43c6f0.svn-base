package com.pcs.ztqtj.control.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCity;

public class AdapterPopWarn extends BaseAdapter{

	private List<PackLocalCity> contentList;
	private Context context;
	public AdapterPopWarn(Context context,List<PackLocalCity> contentList){
		this.context=context;
		this.contentList=contentList;
	}
	@Override
	public int getCount() {
		return contentList.size();
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
		TextView tv = null;
		if(view==null){
			view=LayoutInflater.from(context).inflate(R.layout.item_air_station, null);
			tv=(TextView) view.findViewById(R.id.textcontent);
			view.setTag(tv);
		}else{
			tv=(TextView) view.getTag();
		}
		tv.setText(contentList.get(position).NAME);
		return view;
	}

}
