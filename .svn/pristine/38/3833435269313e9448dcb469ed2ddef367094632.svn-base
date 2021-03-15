package com.pcs.ztqtj.control.adapter.location_warning;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.lib_ztqfj_v2.model.pack.net.locationwarning.WarningTypeInfo;

/**
 * 数据适配器：定点服务类型
 * @author E.Sun
 * 2015年11月3日
 */
public class AdapterWarningType extends BaseAdapter {

	private Context mContext;
	private List<WarningTypeInfo> typeInfoList;
	
	public AdapterWarningType(Context context, List<WarningTypeInfo> list) {
		mContext = context;
		typeInfoList = list; 
	}
	
	@Override
	public int getCount() {
		if(typeInfoList != null) {
			return typeInfoList.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if(typeInfoList == null || position < 0 || position >= typeInfoList.size()) {
			return null;
		}
		
		return typeInfoList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder;
		if(convertView == null) {
		    convertView = LayoutInflater.from(mContext).inflate(R.layout.item_livequery_text, null); // 自定义Item布局文件索引
		    holder = new Holder();
		    holder.tv = (TextView) convertView.findViewById(R.id.text_string);
		    convertView.setTag(holder);
		} else {
		    holder = (Holder) convertView.getTag();
		}
		
		// 填充内容
		WarningTypeInfo typeInfo = typeInfoList.get(position);
		holder.code = typeInfo.id;
		holder.tv.setText(typeInfo.name);
		
		// 返回当前position的Item视图
		return convertView;
	}
	
	/**
	 * 设置数据
	 * @param list
	 */
	public void setData(List<WarningTypeInfo> list) {
		typeInfoList = list;
		notifyDataSetChanged();
	}
	
	/**
     * Item布局控件持久化操作
     */
    public class Holder {
        public TextView tv;
        public String code;
    }

}
