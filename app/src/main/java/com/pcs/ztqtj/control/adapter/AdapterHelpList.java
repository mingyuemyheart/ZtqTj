package com.pcs.ztqtj.control.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pcs.ztqtj.R;

public class AdapterHelpList extends BaseAdapter{
	private Context context;
	private List<String> data;
	public AdapterHelpList(Context context,List<String> data){
		this.context=context;
		this.data=data;
	}
	
	@Override
	public int getCount() {
		return data.size();
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
		Holder hold;
		if(view==null){
			hold=new Holder();
			view=LayoutInflater.from(context).inflate(R.layout.helplistviewitem, null);
			hold.warnKidn=(TextView) view.findViewById(R.id.warnkindtext);
		view.setTag(hold);
		}else{
			hold=(Holder) view.getTag();
		}
		hold.warnKidn.setText(data.get(position));
		return view;
	}
	class Holder{
		public TextView warnKidn;
	}

}