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
import com.pcs.lib_ztqfj_v2.model.pack.net.locationwarning.LocationWarningInfo;

/**
 * 数据适配器：定点服务
 * @author E.Sun
 * 2015年10月22日
 */
public class AdapterLocationWarning extends BaseAdapter {

    /*
     * 继承BaseAdapter，灵活适配各种控件，需实现抽象方法
     */

    private Context mContext;
    private List<?> mList; // 要展示的数据，不仅限于List

    public AdapterLocationWarning(Context context, List<?> list) {
        mContext = context;
        mList = list;
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
    	if(mList != null && position >= 0 && position < mList.size()) {
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
        	convertView = LayoutInflater.from(mContext).inflate(R.layout.item_livequery_text, null); // 自定义Item布局文件索引
		    holder = new Holder();
		    holder.tv = (TextView) convertView.findViewById(R.id.text_string);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        // 填充内容
        LocationWarningInfo info = (LocationWarningInfo) mList.get(position);
        if(info != null) {
        	holder.tv.setText(info.name);
        }

        // 返回当前position的Item视图
        return convertView;
    }
    
    /**
     * 设置数据源
     * @param list
     */
    public void setData(List<?> list) {
    	mList = list;
    	notifyDataSetChanged();
    }

    /**
     * Item布局控件持久化操作
     */
    private class Holder {
        
        public TextView tv;
    }

}