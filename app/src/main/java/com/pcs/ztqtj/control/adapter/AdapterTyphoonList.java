package com.pcs.ztqtj.control.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.pcs.lib_ztqfj_v2.model.pack.net.typhoon.TyphoonInfo;
import com.pcs.ztqtj.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 监测预报-台风路径
 */
@SuppressLint("InflateParams")
public class AdapterTyphoonList extends BaseAdapter {

    private Context mContext;
    private List<TyphoonInfo> mList;
    private Map<String, Boolean> checkedCodeMap = new TreeMap<String, Boolean>();

    public AdapterTyphoonList(Context context, List<TyphoonInfo> list) {
        mContext = context;
        mList = list;
        initMap();
    }
    
    @Override
    public int getCount() {
        if(mList != null) {
            return mList.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
    	if(mList != null) {
            return mList.get(position);
        }
        return null;
    }

    public TyphoonInfo getItem(String code) {
        if(mList != null) {
            for(TyphoonInfo info : mList) {
                if(info.code.equals(code)) {
                    return info;
                }
            }
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        /*
         * getCount()方法返回值大于0时，根据返回值循环调用getView()方法填充当前适配器所绑定的父控件。
         * 通过position控制每个Item需要展示的内容。
         */

        // 视图缓存处理，用于性能优化
        Holder holder;
        if(convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_livequery_text, null); // 自定义Item布局文件索引
            holder = new Holder();
            holder.tv = (TextView) convertView.findViewById(R.id.text_string);
            holder.cb = (CheckBox) convertView.findViewById(R.id.checkbox);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        // 填充内容
        TyphoonInfo info = mList.get(position);
        holder.tv.setText(info.name);
        holder.cb.setVisibility(View.VISIBLE);
        holder.cb.setClickable(false);
        holder.cb.setChecked(checkedCodeMap.get(info.code));
        holder.code = info.code;

        // 返回当前position的Item视图
        return convertView;
    }

    private void initMap() {
    	if(mList != null) {
        	for(TyphoonInfo info : mList) {
        		checkedCodeMap.put(info.code, false);
        	}
        } else {
        	checkedCodeMap.clear();
        }
    }
    
    /**
     * 设置数据源
     * @param list
     */
    public void setData(List<TyphoonInfo> list) {
    	mList = list;
    	initMap();
    	notifyDataSetChanged();
    }
    
    /**
     * 设置列表项选中状态
     * @param position
     * @param isChecked
     */
    public void setItemState(int position, boolean isChecked) {
    	if(position >= mList.size()) {
    		return;
    	}
    	
    	TyphoonInfo info = mList.get(position);
    	if(info != null) {
    		checkedCodeMap.put(info.code, isChecked);
    		notifyDataSetChanged();
    	}
    }
    
    /**
     * 设置列表项选择状态
     * @param code
     * @param isChecked
     */
    public void setItemState(String code, boolean isChecked) {
    	checkedCodeMap.put(code, isChecked);
    	notifyDataSetChanged();
    }
    
    /**
     * 获取首选项
     * @return
     */
    public Object getFirstCheckedItem() {
    	Iterator<String> iterator = checkedCodeMap.keySet().iterator();
    	String code = "";
    	String key;
    	while(iterator.hasNext()) {
    		key = iterator.next();
    		if(checkedCodeMap.get(key)) {
    			code = key;
    		}
    	}
    	return code;
    }
    
    /**
     * 获取已选项
     * @return
     */
    public List<String> getAllCheckedItem() {
    	List<String> codeList = new ArrayList<String>();
    	
    	Iterator<String> iterator = checkedCodeMap.keySet().iterator();
    	String key;
    	while(iterator.hasNext()) {
    		key = iterator.next();
    		if(checkedCodeMap.get(key)) {
    			codeList.add(key);
    		}
    	}
    	
    	return codeList;
    }
    
    /**
     * 获取已选项数量
     * @return
     */
    public int getCheckedCount() {
    	int count = 0;
    	Iterator<String> iterator = checkedCodeMap.keySet().iterator();
    	String key;
    	while(iterator.hasNext()) {
    		key = iterator.next();
    		if(checkedCodeMap.get(key)) {
    			count++;
    		}
    	}
    	return count;
    }
    
    /**
     * Item布局控件持久化操作
     */
    public class Holder {
        public TextView tv;
        public CheckBox cb;
        public String code;
    }

}
