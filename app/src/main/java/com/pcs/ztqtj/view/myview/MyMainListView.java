package com.pcs.ztqtj.view.myview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * 首页的ListView
 * 
 * @author JiangZy
 * 
 */
public class MyMainListView extends ListView {

	private boolean mIsScrollable = true;

	public void setScrollable(boolean b) {
		mIsScrollable = b;
	}

	public MyMainListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyMainListView(Context context) {
		super(context);
	}

	public MyMainListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (!mIsScrollable) {
			int expandSpec = MeasureSpec.makeMeasureSpec(
					Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
			super.onMeasure(widthMeasureSpec, expandSpec);
		} else {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
	}
}
