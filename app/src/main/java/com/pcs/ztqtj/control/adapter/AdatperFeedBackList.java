package com.pcs.ztqtj.control.adapter;

import java.util.List;

import com.pcs.ztqtj.R;
import com.pcs.lib_ztqfj_v2.model.pack.net.SuggestListInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AdatperFeedBackList extends BaseAdapter{
	private Context context;
	private List<SuggestListInfo> suggetList;
	public AdatperFeedBackList(Context context,List<SuggestListInfo> suggetList){
		this.context=context;
		this.suggetList=suggetList;
	}
	@Override
	public int getCount() {
		return suggetList.size();
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
	public View getView(int position, View convertView, ViewGroup parent) {
		Holde holde;
		if (null == convertView) {
			holde = new Holde();
			convertView = LayoutInflater.from(context).inflate(R.layout.item_feedback_list, null);
			holde.tv_feedback_time = (TextView) convertView.findViewById(R.id.tv_feedback_time);
			holde.tv_feedback = (TextView) convertView.findViewById(R.id.tv_feedback);
			convertView.setTag(holde);
		} else {
			holde = (Holde) convertView.getTag();
		}
		holde.tv_feedback_time.setText(suggetList.get(position).create_time);
		holde.tv_feedback.setText(suggetList.get(position).msg);
		return convertView;
	}
	public class Holde{
		public TextView tv_feedback_time;
		public TextView tv_feedback;
	}
}
