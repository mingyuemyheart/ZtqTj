package com.pcs.ztqtj.control.adapter.air_quality;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.lib_ztqfj_v2.model.pack.net.airinfopack.PackKeyDescDown.DicListBean;

/**
 * 空气质量：选择类型
 * 
 * @author JiangZy
 * 
 */
public class AdapterAirChoiceType extends BaseAdapter {
	private Context mContext;
	private List<DicListBean> dataeaum;
	public AdapterAirChoiceType(Context context, List<DicListBean> dataeaum) {
		mContext = context;
		this.dataeaum = dataeaum;
	}

	@Override
	public int getCount() {
		return dataeaum.size();
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
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_airquality_type, null);
		}
		TextView textView = null;
		// 英文
		textView = (TextView) convertView.findViewById(R.id.text_en);
		
		String showKey=dataeaum.get(position).rankType;
		if(showKey.equals("O3")){
			showKey="O3_1H";
		}else if(showKey.equals("PM2_5")){
			showKey="PM2.5";
		}
		textView.setText(showKey);
		// 中文

		textView = (TextView) convertView.findViewById(R.id.text_cn);
		textView.setText(dataeaum.get(position).name);

		return convertView;
	}
}
