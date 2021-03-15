package com.pcs.ztqtj.control.adapter.waterflood;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.livequery.AdapterData;

import java.util.List;

public class AdapterPopWindow extends AdapterData{

	public AdapterPopWindow(Context context, List<String> dataList) {
		super(context, dataList);
	}
	
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		TextView tv = null;
		if(view==null){
			view=LayoutInflater.from(context).inflate(R.layout.item_popwindow, null);
			tv=(TextView) view.findViewById(R.id.text_string);
			view.setTag(tv);
		}else{
			tv=(TextView) view.getTag();
		}
		if(dataList.size() > position) {
            tv.setText(dataList.get(position));
        }
		return view;
	}

}
