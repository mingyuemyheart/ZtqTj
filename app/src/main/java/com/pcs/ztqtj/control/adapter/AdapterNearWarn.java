package com.pcs.ztqtj.control.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.lib_ztqfj_v2.model.pack.net.lightning.NearWarnInfo;

/**
 * 数据适配器：雷电监测--临近预警
 * @author E.Sun
 * 2015年10月13日
 */
public class AdapterNearWarn extends BaseAdapter {

	private Context mContext;
	private List<NearWarnInfo> mList;
	
	@SuppressWarnings("unchecked")
	public AdapterNearWarn(Context context, List<?> list) {
		this.mContext = context;
		this.mList = (List<NearWarnInfo>) list;
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

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        /*
         * getCount()方法返回值大于0时，根据返回值循环调用getView()方法填充当前适配器所绑定的父控件。
         * 通过position控制每个Item需要展示的内容。
         */

        // 视图缓存处理，用于性能优化
        Holder holder;
        if(convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_near_warn, null); // 自定义Item布局文件索引
            holder = new Holder();
            holder.tvCode = (TextView) convertView.findViewById(R.id.tv_code);
            holder.tvArea = (TextView) convertView.findViewById(R.id.tv_area);
            holder.tvProbability = (TextView) convertView.findViewById(R.id.tv_probability );
            holder.tvFrequency = (TextView) convertView.findViewById(R.id.tv_frequency);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
    	
    	// 背景
		if (position % 2 == 0) {
			convertView.setBackgroundColor(mContext.getResources().getColor(
					android.R.color.white));
		} else {
			convertView.setBackgroundColor(mContext.getResources().getColor(
					R.color.map_forecast_list_bg));
		}

        // 填充内容
		NearWarnInfo info = mList.get(position);
		holder.tvCode.setText(info.number);
		holder.tvArea.setText(info.area);
        holder.tvProbability.setText(String.valueOf(info.probability));
        holder.tvFrequency.setText(String.valueOf(info.frequency));

        // 返回当前position的Item视图
        return convertView;
    }
    
    @SuppressWarnings("unchecked")
	public void setData(List<?> list) {
    	mList = (List<NearWarnInfo>) list;
    	notifyDataSetChanged();
    }
    
    /**
     * Item布局控件持久化操作
     */
    private class Holder {
        
        public TextView tvCode;
        public TextView tvArea;
        public TextView tvProbability;
        public TextView tvFrequency;

    }
    
}
