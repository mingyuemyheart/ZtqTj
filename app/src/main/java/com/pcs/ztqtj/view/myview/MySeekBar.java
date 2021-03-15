package com.pcs.ztqtj.view.myview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

/**
 * @author chenjh
 * 继承 SeekBar 实现自己的SeekBar
 */
public class MySeekBar extends SeekBar {

	public MySeekBar(Context context) {
		super(context);
	}

	public MySeekBar(Context context, AttributeSet attrs) {
		this(context, attrs, android.R.attr.seekBarStyle);
	}

	public MySeekBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * onTouchEvent 是在 SeekBar 继承的抽象类 AbsSeekBar 里 你可以看下他们的继承关系
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		return false;
	}

}
