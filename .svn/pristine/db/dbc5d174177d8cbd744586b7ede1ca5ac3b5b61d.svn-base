package com.pcs.ztqtj.control.listener;

import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

import com.pcs.ztqtj.control.inter.InterfaceRefresh;

/**
 * ListView上拉刷新
 * 
 * @author JiangZy
 * 
 */
public class ListenerOnScrollUpRefresh implements OnScrollListener {
	// 刷新接口
	private InterfaceRefresh mInterfaceRefresh;
	// 是否刷新中
	private boolean mIsRefreshing = false;

	public ListenerOnScrollUpRefresh(InterfaceRefresh interfaceRefresh) {
		mInterfaceRefresh = interfaceRefresh;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		int lastItem = firstVisibleItem + visibleItemCount;
		if (!mIsRefreshing && lastItem == totalItemCount) {
			mInterfaceRefresh.refresh(null);
			mIsRefreshing = true;
		}
	}

	/**
	 * 设置刷新完成
	 */
	public void setRefreshDone() {
		mIsRefreshing = false;
	}
}
