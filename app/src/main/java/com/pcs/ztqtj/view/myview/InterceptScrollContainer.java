package com.pcs.ztqtj.view.myview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/*
 * 
 * 一个视图容器控件
 * 阻止 拦截 ontouch事件传递给其子控件
 * */
public class InterceptScrollContainer extends LinearLayout {

	public InterceptScrollContainer(Context context, AttributeSet attrs) {
		super(context, attrs);

	}

	public InterceptScrollContainer(Context context) {
		super(context);

	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {

		return true;
	}

}
