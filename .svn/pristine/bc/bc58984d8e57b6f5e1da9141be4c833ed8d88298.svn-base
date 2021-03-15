package com.pcs.ztqtj.control.adapter.livequery;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pcs.ztqtj.R;

import java.util.List;

public class AdapterData extends BaseAdapter{

	protected List<String> dataList;
	protected Context context;
	private int textSizeDp = 17;
	private final int DEFAULT_TEXT_SIZE = 17;
	public AdapterData(Context context,List<String> dataList){
		this.context=context;
		this.dataList=dataList;
	}
	@Override
	public int getCount() {
		return dataList.size();
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
		TextView tv = null;
		if(view==null){
			view=LayoutInflater.from(context).inflate(R.layout.item_livequery_text, null);
			tv=(TextView) view.findViewById(R.id.text_string);
			view.setTag(tv);
		}else{
			tv=(TextView) view.getTag();
		}
		tv.setText(dataList.get(position));
		if(textSizeDp != DEFAULT_TEXT_SIZE) {
            //tv.setTextSize(context.getResources().getDimension(textSizeDp));
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSizeDp);
        }
		return view;
	}

	public void setTextViewSize(int dp) {
        textSizeDp = dp;
        notifyDataSetChanged();
    }

}
