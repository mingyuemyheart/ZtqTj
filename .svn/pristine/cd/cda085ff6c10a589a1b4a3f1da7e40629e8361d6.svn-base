package com.pcs.ztqtj.control.listener;

import java.util.ArrayList;
import java.util.List;

import com.pcs.ztqtj.view.myview.MyHScrollView;

/**
 * 观察者：同步滚动
 * 
 * @author JiangZy
 * 
 */
public class ObserverScrollSync {

	List<MyHScrollView> mList;

	public ObserverScrollSync() {
		super();
		mList = new ArrayList<MyHScrollView>();
	}

	public void addScrollView(MyHScrollView scrollView) {
		mList.add(scrollView);
	}

	public void removeScrollView(MyHScrollView scrollView) {
		mList.remove(scrollView);
	}

	public void callAllScroll(int hashCode, int l, int t) {
		if (mList == null || mList.size() == 0) {
			return;
		}
		for (int i = 0; i < mList.size(); i++) {
			if (mList.get(i) != null) {
				mList.get(i).callScroll(hashCode, l, t);
			}
		}
	}

}
