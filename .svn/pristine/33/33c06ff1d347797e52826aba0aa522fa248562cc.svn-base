package com.pcs.ztqtj.control.adapter.adapter_set;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.LocalDataHelper;

/**
 * @author Z 列表适配器
 */
public class AdapterProjectManagemer extends BaseAdapter {
	private Context context;
	private List<String> listdata;

	public AdapterProjectManagemer(Context context, List<String> listdata) {
		this.context = context;
		this.listdata = listdata;
	}

	@Override
	public int getCount() {
		return listdata.size();
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
	public View getView(final int position, View view, ViewGroup parent) {
		Holder holder;
		if (view == null) {
			holder = new Holder();
			view = LayoutInflater.from(context).inflate(
					R.layout.item_projectmanager_listview, null);
			holder.checkbox = (CheckBox) view.findViewById(R.id.checkbox);
			holder.warncontent = (TextView) view.findViewById(R.id.warncontent);
			holder.itemLayout = (RelativeLayout) view.findViewById(R.id.item_layout);
			view.setTag(holder);
		} else {
			holder = (Holder) view.getTag();
		}
		final String itemName = listdata.get(position).toString();
		holder.warncontent.setText(itemName);
		boolean boolean1 = LocalDataHelper.getProductValue(context, itemName);
		holder.checkbox.setChecked(boolean1);
		if("整点实况".equals(itemName)||"网格预报".equals(itemName)){
//			view.setBackgroundColor(0xEDEDED);
//			System.out.println("itemName:"+itemName);
			
			holder.itemLayout.setBackgroundResource(R.drawable.gray_background);
		}else{
			holder.itemLayout.setBackgroundResource(R.drawable.white_background);
		}

		return view;
	}

	private class Holder {
		public TextView warncontent;
		public CheckBox checkbox;
		public RelativeLayout itemLayout;
	}

}
