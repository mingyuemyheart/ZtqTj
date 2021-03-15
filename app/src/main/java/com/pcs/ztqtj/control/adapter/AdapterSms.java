package com.pcs.ztqtj.control.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackSMSDown.WeatherSMSBean;

public class AdapterSms extends BaseAdapter {
	private Context context;
	private List<WeatherSMSBean> datalist;

	public AdapterSms(Context context, List<WeatherSMSBean> datalist) {
		this.context = context;
		this.datalist = datalist;
	}

	@Override
	public int getCount() {
		return datalist.size();
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
			view = LayoutInflater.from(context).inflate(R.layout.item_set_sms, null);
			holder.texttitle = (TextView) view.findViewById(R.id.text_title);
			holder.textcont = (TextView) view.findViewById(R.id.text_content);
			view.setTag(holder);
		} else {
			holder = (Holder) view.getTag();
		}
		switch (position%3) {
		case 0:
			holder.texttitle.setTextColor(context.getResources().getColor(R.color.warn_red));
			
			break;
		case 1:
			holder.texttitle.setTextColor(context.getResources().getColor(R.color.mblue));
			
			break;
		case 2:
			holder.texttitle.setTextColor(context.getResources().getColor(R.color.blackalpha));
			
			break;
		}
		holder.texttitle.setText(datalist.get(position).title);
		holder.textcont.setText(datalist.get(position).money+"\n\r"+datalist.get(position).open_msg+"\n\r"+datalist.get(position).close_msg);
		return view;
	}

	private class Holder {
		public TextView texttitle;
		public TextView textcont;
	}

}
