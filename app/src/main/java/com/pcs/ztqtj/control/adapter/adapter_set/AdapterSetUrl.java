package com.pcs.ztqtj.control.adapter.adapter_set;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.pcs.ztqtj.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 右侧边栏友情链接adapter
 * @author tya
 *
 */
public class AdapterSetUrl extends BaseAdapter{

	private Context mContext = null;
	private List<Map<String, String>> listdata;
	
	public AdapterSetUrl(Context context, List<Map<String, String>> listdata) {
		mContext = context;
		this.listdata = listdata;
	}
	
	@Override
	public int getCount() {
		return listdata.size();
	}

	@Override
	public Object getItem(int position) {
		return listdata.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if(convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_fragmentset, null);
			holder.tv = (TextView) convertView.findViewById(R.id.explain_text);
			holder.iv = (ImageView) convertView.findViewById(R.id.setimage_icon);
			holder.choosebutton = (CheckBox) convertView.findViewById(R.id.open_icon);
			holder.choosebutton.setVisibility(View.GONE);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Map<String, String> map = listdata.get(position);
		holder.iv.setImageResource(Integer.parseInt(map.get("i")));
		holder.tv.setText(map.get("t"));
		return convertView;
	}
	
	/**
	 * 设置数据
	 * @param listdata
	 */
	public void setData(List<Map<String, String>> listdata) {
		this.listdata = new ArrayList<Map<String,String>>();
		this.listdata.addAll(listdata);
		notifyDataSetChanged();
	}
	
	private static class ViewHolder {
		public ImageView iv;
		public TextView tv;
		public CheckBox choosebutton;
	}

}
