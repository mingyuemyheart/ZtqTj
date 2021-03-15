package com.pcs.ztqtj.control.adapter.location_warning;

import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.view.activity.product.locationwarning.ActivityWarningCustomize;
import com.pcs.lib_ztqfj_v2.model.pack.net.AreaInfo;

/**
 * 预警区域数据适配器
 * @author E.Sun
 * 2015年11月7日
 */
@SuppressLint("InflateParams")
public class AdapterWarningArea extends BaseAdapter {

    private ActivityWarningCustomize mActivity;
    private List<AreaInfo> mList;

    public AdapterWarningArea(Activity activity, List<AreaInfo> list) {
    	mActivity = (ActivityWarningCustomize) activity;
        mList = list;
    }

    @Override
    public int getCount() {
        if(mList == null) {
            return 0;
        }
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        if(mList == null || position < 0 || position >= mList.size()) {
            return null;
        }
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View converView, ViewGroup parent) {
        Holder holder;
        if(converView == null) {
            converView = LayoutInflater.from(mActivity).inflate(R.layout.item_warning_area, null);
            holder = new Holder();
            holder.tvCity = (TextView) converView.findViewById(R.id.tv_city);
            holder.tvArea = (TextView) converView.findViewById(R.id.tv_area);
            holder.btnDelete = (Button) converView.findViewById(R.id.btn_delete);
            converView.setTag(holder);
        } else {
            holder = (Holder) converView.getTag();
        }

        // 填充内容
        AreaInfo areaInfo = (AreaInfo) mList.get(position);
        holder.tvCity.setText(areaInfo.cityName);
        holder.tvArea.setText(areaInfo.areaName);
        holder.btnDelete.setVisibility(View.VISIBLE);
    	holder.btnDelete.setOnClickListener(new OnDeleteClickListener(position));
        
        // 返回指定position的选项视图
        return converView;
    }

    /**
     * 替换初始数据
     * @param list
     */
    public void setData(List<AreaInfo> list) {
    	mList = list;
    	notifyDataSetChanged();
    }
    
    /**
     * 添加数据
     * @param city
     */
    public void addData(AreaInfo info) {
    	if(mList == null) {
    		mList = new ArrayList<AreaInfo>();
    	}
    	
    	if(!isExists(info)) {
    		mList.add(info);
    		notifyDataSetChanged();
    	}
    }

    /**
     * 获取区域编号集合
     * @return
     */
    public List<String> getAreaIdList() {
    	if(mList == null) {
    		return null;
    	}
    	
    	List<String> areaIdList = new ArrayList<String>();
    	for(AreaInfo areaInfo : mList) {
    		areaIdList.add(areaInfo.id);
    	}
    	return areaIdList;
    }
    
    /**
     * 判断是否已添加当前城市
     * @param city
     * @return
     */
    private boolean isExists(AreaInfo info) {
    	if(mList == null) {
    		return false;
    	}
    	
    	for(AreaInfo tempArea : mList) {
    		if(tempArea.id.equals(info.id)) {
    			return true;
    		}
    	}
    	
    	return false;
    }
    
    public class Holder {
        public TextView tvCity;
        public TextView tvArea;
        public Button btnDelete;
    }
    
    /**
     * 删除按钮点击事件监听器
     * @author E.Sun
     * 2015年11月7日
     */
    private class OnDeleteClickListener implements OnClickListener {

    	private int position;
    	
    	public OnDeleteClickListener(int position) {
    		this.position = position;
    	}
    	
		@Override
		public void onClick(View v) {
			mList.remove(position);
			notifyDataSetChanged();
		}
    }
    
}