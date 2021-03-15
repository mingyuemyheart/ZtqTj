package com.pcs.ztqtj.control.tool;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * 布局工具类
 * @author E.Sun
 * 2015年11月12日
 */
public class LayoutTool {

	/**
	 * 变更ListView布局属性（高度自适应）
	 * @param listview
	 * @return
	 */
	public static ViewGroup.LayoutParams changeLayoutParams(ListView listview) {
    	if(listview == null || listview.getAdapter() == null) {
    		return null;
    	}
    	int totalHeight = 0;
    	ListAdapter adapter = listview.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {    
            View listItem = adapter.getView(i, null, listview);    
            listItem.measure(0, 0);    
            totalHeight += listItem.getMeasuredHeight();    
        }    
    
        ViewGroup.LayoutParams params = listview.getLayoutParams();    
        params.height = totalHeight + (listview.getDividerHeight() * (listview.getCount() - 1));
        return params;
    }
	
	/**
	 * 变更ListView布局属性（限定行数）
	 * @param listview
	 * @param maxCount 限定行数
	 * @return
	 */
	public static ViewGroup.LayoutParams changeLayoutParams(ListView listview, int maxCount) {
    	if(listview == null || listview.getAdapter() == null || maxCount < 0) {
    		return null;
    	}
    	
    	ViewGroup.LayoutParams params = listview.getLayoutParams(); 
    	if(maxCount == 0) {
    		params.height = 0;
    	} else {
    		int totalHeight = 0;
        	ListAdapter adapter = listview.getAdapter();
            for (int i = 0; i < maxCount && i < adapter.getCount(); i++) {    
                View listItem = adapter.getView(i, null, listview);    
                listItem.measure(0, 0);    
                totalHeight += listItem.getMeasuredHeight();    
            }    
            params.height = totalHeight + (listview.getDividerHeight() * (maxCount - 1));
    	}
		return params;
    }
	
}
