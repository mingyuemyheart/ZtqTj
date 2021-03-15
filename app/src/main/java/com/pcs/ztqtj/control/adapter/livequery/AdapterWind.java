package com.pcs.ztqtj.control.adapter.livequery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackFltjZdDown.FltjZd;

import java.util.List;

public class AdapterWind extends BaseAdapter {
	private Context context;
	private List<FltjZd> temfalllist;

	public AdapterWind(Context context, List<FltjZd> temfalllist) {
		this.context = context;
		this.temfalllist = temfalllist;
	};

	@Override
	public int getCount() {
		return temfalllist.size();
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

		Handler handler = null;

		if (view == null) {
			handler = new Handler();
			view = LayoutInflater.from(context).inflate(
					R.layout.item_livequery_wind, null);

			handler.country = (TextView) view
					.findViewById(R.id.livequery_wind_county);
			handler.windpower = (TextView) view
					.findViewById(R.id.livequery_wind_windpower);
			handler.winddirection = (TextView) view
					.findViewById(R.id.livequery_wind_winddirection);
			handler.time = (TextView) view
					.findViewById(R.id.livequery_wind_time);
			handler.livequery_wind_power = (TextView) view
					.findViewById(R.id.livequery_wind_power);

			view.setTag(handler);
		} else {
			handler = (Handler) view.getTag();
		}
		if (position == 0) {
			handler.country.setBackgroundResource(R.drawable.bg_livequery_item);
			handler.windpower.setBackgroundResource(R.drawable.bg_livequery_item);
			handler.winddirection.setBackgroundResource(R.drawable.bg_livequery_item);
			handler.time.setBackgroundResource(R.drawable.bg_livequery_item);
			handler.livequery_wind_power.setBackgroundResource(R.drawable.bg_livequery_item);
		}else{
			handler.country.setBackgroundColor(context.getResources().getColor(R.color.alpha100));
			handler.windpower.setBackgroundColor(context.getResources().getColor(R.color.alpha100));
			handler.winddirection.setBackgroundColor(context.getResources().getColor(R.color.alpha100));
			handler.time.setBackgroundColor(context.getResources().getColor(R.color.alpha100));
			handler.livequery_wind_power.setBackgroundColor(context.getResources().getColor(R.color.alpha100));
		}
		try {
			handler.country.setText(temfalllist.get(position).county);
			handler.windpower.setText(temfalllist.get(position).windpower);
			handler.winddirection.setText(temfalllist.get(position).winddirection);
			handler.time.setText(temfalllist.get(position).time);
			handler.livequery_wind_power.setText(temfalllist.get(position).windFengLi);
		} catch (Exception e) {
		}
		return view;
	}

	public void setData(List<FltjZd> temfalllist) {
	    this.temfalllist = temfalllist;
	    notifyDataSetChanged();
    }

	private class Handler {
		public TextView country;
		public TextView windpower;
		public TextView winddirection;
		public TextView time;
		public TextView livequery_wind_power;

	}
}