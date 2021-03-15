package com.pcs.ztqtj.view.myview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ViewPageControl extends LinearLayout {

	private int oldIndex;
	private int size;

	private Drawable d1, d2;

	public ViewPageControl(Context context) {
		super(context);
	}

	public ViewPageControl(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setDrableView(int defautl, int checked) {
		d1 = getResources().getDrawable(defautl);
		d2 = getResources().getDrawable(checked);
	}

	/**
	 * 初始化导航位置点
	 * 
	 * @param size
	 *            总共多少点
	 */
	public void init(int size) {
		oldIndex = 0;
		this.size = size;
		this.setOrientation(LinearLayout.HORIZONTAL);
		this.removeAllViews();
		for (int i = 0; i < size; i++) {
			ImageView imageView = new ImageView(getContext());
			imageView.setPadding(10, 0, 10, 0);
			if (0 == i) {
				imageView.setImageDrawable(d2);
			} else {
				imageView.setImageDrawable(d1);
			}
			this.addView(imageView);
		}

	}

	public void generatePageControl(int currentIndex) {
		if (size == 0) {
			return;
		}
		if (currentIndex < size) {
			ImageView v = null;
			v = (ImageView) this.getChildAt(oldIndex);
			v.setImageDrawable(d1);
			v = (ImageView) this.getChildAt(currentIndex);
			v.setImageDrawable(d2);
			this.oldIndex = currentIndex;
		}

	}
}
