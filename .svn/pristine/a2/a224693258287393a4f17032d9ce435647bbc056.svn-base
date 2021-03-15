package com.pcs.ztqtj.view.myview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.ImageView;

@SuppressLint({ "HandlerLeak", "NewApi" })
public class ViewCirclePoint extends ImageView {

	// 刷新间隔
	private final long REFRESH_TIME = 50;
	// 刷新Handler
	private Handler mRefreshHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			ViewCirclePoint.this.invalidate();
			mRefreshHandler.sendEmptyMessageDelayed(0, REFRESH_TIME);
		}
	};

	public ViewCirclePoint(Context context, AttributeSet attrs) {
		super(context, attrs);

		// 定时刷新
		mRefreshHandler.sendEmptyMessageDelayed(0, REFRESH_TIME);
	}

	// 百分比
	private float mPercent = 0;
	// 当前百分比
	private float mCurrPercent = 0;
	// 每次增加百分比
	private final float ADD_PERCENT = 0.015f;
	// 每次减少百分比
	private final float RED_PERCENT = -0.015f;

	/**
	 * 设置百分比
	 * 
	 * @param per
	 *            0.0~1.0
	 */
	public void setPercent(float per) {
		if (per < 0f || per > 1f) {
			throw new RuntimeException("百分比超出范围：" + per);
		}
		mPercent = per;
		// 定时刷新
		mRefreshHandler.sendEmptyMessageDelayed(0, REFRESH_TIME);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (mCurrPercent != mPercent) {
			float addPercent = mPercent - mCurrPercent;
			if (addPercent > ADD_PERCENT) {
				addPercent = ADD_PERCENT;
			} else if (addPercent < RED_PERCENT) {
				addPercent = RED_PERCENT;
			}

			mCurrPercent += addPercent;
		} else if (mPercent != 0) {
			// 删除刷新消息
			mRefreshHandler.removeMessages(0);
		}

		this.setRotation(330f * mCurrPercent);
	}
}
