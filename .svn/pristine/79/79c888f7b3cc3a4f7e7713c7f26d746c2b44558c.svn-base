package com.pcs.ztqtj.control.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pcs.lib_ztqfj_v2.model.pack.net.GuideBean;
import com.pcs.ztqtj.R;

import java.util.ArrayList;
import java.util.List;

public class AdapterUseGuide extends BaseAdapter{

	private Context context;
	private List<GuideBean> guideListData;
	public AdapterUseGuide(Context context){
		this.context=context;
		this.guideListData=new ArrayList<GuideBean>();
	}

	public void setData(List<GuideBean> pro_list){
		this.guideListData=pro_list;
		this.notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return guideListData.size();
	}

	@Override
	public Object getItem(int position) {
		return guideListData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder=null;
		if(convertView==null){
			holder=new Holder();
			convertView=LayoutInflater.from(context).inflate(R.layout.item_useguide, null);
			holder.title=(TextView) convertView.findViewById(R.id.myserver_title);
			convertView.setTag(holder);
		}else{
			holder=(Holder) convertView.getTag();
		}
		
		holder.title.setText(guideListData.get(position).title);

		return convertView;
	}
	class Holder{
		public TextView title;
	}
	
}
