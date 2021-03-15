package com.pcs.ztqtj.control.adapter.product_numerical;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackNumericalForecast.NumberBean;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackNumericalForecastColumnDown.ForList;

/**
 * @author Z
 *数值预报-首頁列表適配器
 */
public class AdapterNumericalMain extends BaseAdapter{
	private List<ForList> listLeve1;
	private Context context;
	public AdapterNumericalMain(Context context,List<ForList> listLeve1){
		this.listLeve1=listLeve1;
		this.context=context;
	}
	public void setData(List<ForList> listLeve1){
		this.listLeve1=listLeve1;
		this.notifyDataSetChanged();
	}
	@Override
	public int getCount() {
		return listLeve1.size();
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
		TextView desc;
		if(view==null){
			view=LayoutInflater.from(context).inflate(R.layout.item_group_numericalforecast, null);
			desc=(TextView)view.findViewById(R.id.number_group_text);
			view.setTag(desc);
		}else{
			desc=(TextView) view.getTag();
		}
		desc.setText(listLeve1.get(position).name);
		return view;
	}
	
}
