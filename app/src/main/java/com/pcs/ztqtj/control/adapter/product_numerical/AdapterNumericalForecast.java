package com.pcs.ztqtj.control.adapter.product_numerical;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackNumericalForecast.NumberBean;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackNumericalForecastColumnDown.ForList;

/**
 * @author Z
 *数值预报
 */
public class AdapterNumericalForecast extends BaseExpandableListAdapter{
	private List<ForList> listLeve1;
	private Context context;
	private List<List<ForList>> listLeve2;
	public AdapterNumericalForecast(Context context,List<ForList> listLeve1,List<List<ForList>> listLeve2){
		this.listLeve1=listLeve1;
		this.context=context;
		this.listLeve2=listLeve2;
	}
	public void setData(List<ForList> listLeve1,List<List<ForList>> listLeve2){
		this.listLeve1=listLeve1;
		this.listLeve2=listLeve2;
		this.notifyDataSetChanged();
	}
	@Override
	public int getGroupCount() {

		return listLeve1.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return listLeve2.get(groupPosition).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		
		return groupPosition;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
	
		return childPosition;
	}

	@Override
	public long getGroupId(int groupPosition) {
		
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
	
		return childPosition;
	}
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View view, ViewGroup parent) {
		TextView desc;
		if(view==null){
			view=LayoutInflater.from(context).inflate(R.layout.item_group_numericalforecast, null);
			desc=(TextView)view.findViewById(R.id.number_group_text);
			view.setTag(desc);
		}else{
			desc=(TextView) view.getTag();
		}
		desc.setText(listLeve1.get(groupPosition).name);
		
		return view;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view, ViewGroup parent) {
		TextView desc;
		if(view==null){
			view=LayoutInflater.from(context).inflate(R.layout.item_childen_numericalforecast, null);
			desc=(TextView)view.findViewById(R.id.number_childen_text);
			view.setTag(desc);
		}else{
			desc=(TextView) view.getTag();
		}
		desc.setText(listLeve2.get(groupPosition).get(childPosition).name);
		return view;
	}
	@Override
	public boolean hasStableIds() {
		return false;
	}
	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

}
