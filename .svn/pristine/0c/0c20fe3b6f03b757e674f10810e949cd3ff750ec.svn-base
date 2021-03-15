package com.pcs.ztqtj.control.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pcs.ztqtj.R;

/**
 * 日程列表适配器
 * 
 * @author chenjh
 * 
 */
public class AdapterSchedule extends BaseAdapter {
	private Context context;
	private List<String> dataList;
	private DelBtnOnClick listener;

	public void setData(List<String> dataList) {
		this.dataList = dataList;
	}

	public AdapterSchedule(Context context,List<String> dataList, DelBtnOnClick listener) {
		this.context = context;
		this.dataList = dataList;
		this.listener = listener;
	}

	@Override
	public int getCount() {
		return dataList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(final int position, View arg1, ViewGroup arg2) {

		Holder holder;
		if (arg1 == null) {
			arg1 = LayoutInflater.from(context).inflate(R.layout.schedule_item,
					null);
			holder = new Holder();
			holder.itemName = (TextView) arg1.findViewById(R.id.item_name);
			holder.delBtn = (ImageView) arg1.findViewById(R.id.del_iv);
			arg1.setTag(holder);
		} else {
			holder = (Holder) arg1.getTag();
		}
		String info = dataList.get(position);
		holder.itemName.setText(info);
		holder.delBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				listener.delOnclick(position);
			}
		});

		return arg1;
	}

	class Holder {
		TextView itemName;
		ImageView delBtn;
	}
	
	public interface DelBtnOnClick {
		public void delOnclick(int position);
	}
}
