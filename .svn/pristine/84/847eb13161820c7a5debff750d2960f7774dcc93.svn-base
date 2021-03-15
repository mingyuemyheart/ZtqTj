package com.pcs.ztqtj.control.adapter;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.lib_ztqfj_v2.model.pack.net.service.OrgInfo;

public class AdapterHelpOrgList extends BaseAdapter {
	private Context context;
	private List<OrgInfo> dataList;

	public AdapterHelpOrgList(Context context, List<OrgInfo> dataList) {
		this.context = context;
		this.dataList = dataList;
	}

	public void setData(List<OrgInfo> orgList){
		this.dataList = orgList;
	}

	@Override
	public int getCount() {
		if(dataList==null){
			return 0;
		}
		return dataList.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		if(dataList==null){
			return 0;
		}
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		Holder hold;
		if (view == null) {
			hold = new Holder();
			view = LayoutInflater.from(context).inflate(
					R.layout.help_orglist_item, null);
			hold.itemText = (TextView) view.findViewById(R.id.item_text);
			hold.lineView = view.findViewById(R.id.line_white_view);
			view.setTag(hold);
		} else {
			hold = (Holder) view.getTag();
		}
		
		final OrgInfo info= dataList.get(position);
		
		if(!TextUtils.isEmpty(info.type)){
			if((position+1) < getCount()){
				OrgInfo info2= dataList.get(position+1);
				
				if(!info.org_pid.equals(info2.org_pid)){
					hold.lineView.setVisibility(View.VISIBLE);
				}else{
					hold.lineView.setVisibility(View.GONE);
				}
			}
		}
		
		hold.itemText.setText(info.org_name);
		return view;
	}

	class Holder {
		public TextView itemText;
		public View lineView;
	}

}