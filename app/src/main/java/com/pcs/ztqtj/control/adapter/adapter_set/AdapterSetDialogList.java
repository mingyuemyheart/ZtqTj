package com.pcs.ztqtj.control.adapter.adapter_set;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.TextView;

import com.pcs.ztqtj.R;

public class AdapterSetDialogList extends BaseAdapter {
	private Context context;
	private List<Map<String, Object>> listdata;
	private RadioClick listener;

	public AdapterSetDialogList(Context context, List<Map<String, Object>> listdata, RadioClick listener) {
		this.context = context;
		this.listdata = listdata;
		this.listener = listener;
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
			view = LayoutInflater.from(context).inflate(R.layout.item_set_messagemodel, null);
			holder.content = (TextView) view.findViewById(R.id.textcontent);
			holder.radio = (RadioButton) view.findViewById(R.id.radiobutton);
			view.setTag(holder);
		} else {
			holder = (Holder) view.getTag();
		}

		holder.content.setText((CharSequence) listdata.get(position).get("c"));
		holder.radio.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					listener.positionclick(position);
				}
			}
		});
		holder.radio.setChecked((Boolean) listdata.get(position).get("r"));
		
		return view;
	}

	public interface RadioClick {
		public void positionclick(int position);
	}
	class Holder {
		public TextView content;
		public RadioButton radio;
	}

}
