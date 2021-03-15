package com.pcs.ztqtj.control.adapter.adapter_set;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.pcs.ztqtj.R;

/**
 * 温馨提示推送列表适配器
 * 
 * @author chenjh
 */
public class AdapterReminder extends BaseAdapter {
	private Context context;
	private List<Map<String,Object>> listdata;
	private CheckItemCheck checklistener;

	public AdapterReminder(Context context, List<Map<String,Object>> listdata, CheckItemCheck checklistener) {
		this.context = context;
		this.listdata = listdata;
		this.checklistener = checklistener;
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
			view = LayoutInflater.from(context).inflate(R.layout.item_reminder_listview, null);

			holder.checkbox = (CheckBox) view.findViewById(R.id.checkbox);
			holder.warncontent = (TextView) view.findViewById(R.id.warncontent);
			view.setTag(holder);
		} else {
			holder = (Holder) view.getTag();
		}
		
		final Map<String ,Object> map =  listdata.get(position);
		String name = map.get("item_name").toString();
		boolean check_value = (Boolean) map.get("item_switch");
		holder.warncontent.setText(name);
		holder.checkbox.setChecked(check_value);
		holder.checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checklistener.positionclick(position,isChecked);
			}
		});
		return view;
	}

	private class Holder {
		public TextView warncontent;
		public CheckBox checkbox;
	}
	
	public interface CheckItemCheck{
		public void positionclick(int position, boolean check);
	}
	
}