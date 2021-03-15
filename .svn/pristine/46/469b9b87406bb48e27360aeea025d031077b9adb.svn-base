package com.pcs.ztqtj.view.myview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class MyScrollView extends ScrollView {

	public interface MyOnScrollListener {
		void onScrollChanged(int l, int t, int oldl, int oldt);
	}

	private MyOnScrollListener mListener;

	public MyScrollView(Context context) {
		super(context);
	}

	public MyScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public void setListener(MyOnScrollListener listener) {
		mListener = listener;
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		if (mListener != null) {
			mListener.onScrollChanged(l, t, oldl, oldt);
		}
	}

}
