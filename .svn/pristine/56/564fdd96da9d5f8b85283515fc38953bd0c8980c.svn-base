package com.pcs.ztqtj.view.myview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.pcs.ztqtj.R;
import com.pcs.lib.lib_pcs_v3.control.tool.Util;

/**
 * @author Z
 *	广告栏引导点
 */
public class LeadPoint extends LinearLayout {
	private Context context;
	private int oldSelec = 0;
	private ImageView[] viewList;
	public LeadPoint(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		setOrientation(LinearLayout.HORIZONTAL);
	}
	/**
	 * 移动第几个点
	 * @param position
	 */
	public void setPointSelect(int position) {
		if (viewList == null || viewList.length < position) {
		} else {
			viewList[oldSelec].setImageResource(R.drawable.pointdefault);
			viewList[position].setImageResource(R.drawable.pointitemselect);
			oldSelec = position;
		}
	}

	/**
	 * 初始化点数个数
	 * @param pointSize
	 */
	public void initPoint(int pointSize) {
		int size = Util.dip2px(context, 10);
		int margin = Util.dip2px(context, 1);
		viewList = new ImageView[pointSize];
		this.removeAllViews();
		for (int i = 0; i < pointSize; i++) {
			ImageView view = new ImageView(context);
			viewList[i] = view;
			if (i == 0) {
				view.setImageResource(R.drawable.pointitemselect);
			} else {
				view.setImageResource(R.drawable.pointdefault);
			}
			LayoutParams lp = new LayoutParams(size,
					size);
			lp.setMargins(margin, 0, margin, 0);
			this.addView(viewList[i], lp);
		}
	}
}
