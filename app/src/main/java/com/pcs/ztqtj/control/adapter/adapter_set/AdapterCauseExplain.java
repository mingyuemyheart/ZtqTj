package com.pcs.ztqtj.control.adapter.adapter_set;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pcs.ztqtj.R;

public class AdapterCauseExplain extends BaseAdapter {
	private Context context;
	private List<Map<String, String>> listData;

	public AdapterCauseExplain(Context context, List<Map<String, String>> listData) {
		this.context = context;
		this.listData = listData;
	}

	@Override
	public int getCount() {

		return listData.size();
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
			view = LayoutInflater.from(context).inflate(R.layout.item_causeexplain, null);
			holder.titleText = (TextView) view.findViewById(R.id.title_text);
			holder.contentText = (TextView) view.findViewById(R.id.title_content);
			view.setTag(holder);
		} else {
			holder = (Holder) view.getTag();
		}
//		try {
//			holder.titleText.setTextColor(getColor(position));
//		} catch (Exception e) {
//		}
		holder.titleText.setText(listData.get(position).get("t"));
		holder.contentText.setText(listData.get(position).get("c"));

		return view;
	}

	class Holder {
		TextView titleText;
		TextView contentText;
	}

//	public int getColor(int num) {
//	
//		switch (num%6) {
//		case 0:
//			return context.getResources().getColor(R.color.causeexplaina);
//		case 1:
//			return context.getResources().getColor(R.color.causeexplainb);
//		case 2:
//			return context.getResources().getColor(R.color.causeexplainc);
//		case 3:
//			return context.getResources().getColor(R.color.causeexplaind);
//		case 4:
//			return context.getResources().getColor(R.color.causeexplaine);
//		case 5:
//			return context.getResources().getColor(R.color.causeexplaing);
//		default:
//			return context.getResources().getColor(R.color.causeexplainf);
//
//		}
//
//	}

}
