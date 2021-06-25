package com.pcs.ztqtj.control.listener;

import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;

import com.pcs.ztqtj.control.inter.InterfaceRefresh;

/**
 * 刷新触摸监听
 * 
 * @author JiangZy
 * 
 */
public class ListenerRefreshTouch implements OnTouchListener {
	/**
	 * 接口：下拉刷新视图
	 * 
	 * @author JiangZy
	 * 
	 */
	public interface InterfacePulldownView {
		/**
		 * 设置上次更新时间
		 * 
		 * @param time
		 */
		public void setLastTime(long time);

		/**
		 * 设置高度
		 * 
		 * @param percent
		 */
		public void setHeight(int h);

		/**
		 * 回滚
		 */
		public void rollBack();

		/**
		 * 显示刷新
		 * 
		 * @param showTime
		 */
		public void showRefresh();

		/**
		 * 刷新中？
		 * 
		 * @return
		 */
		public boolean isRefreshing();
	}

	/**
	 * 接口：滚动视图
	 * 
	 * @author JiangZy
	 * 
	 */
	public interface InterfaceScrollView {
		/**
		 * 是否滚动到顶部？
		 * 
		 * @return
		 */
		public boolean isScrollTop();

		public void setScrollable(boolean b);
	}

	// 下拉视图
	private InterfacePulldownView mPulldownView;
	// 刷新视图
	private InterfaceRefresh mRefreshView;
	// 刷新动画
	private InterfaceRefresh mRefreshAnim;
	// 滚动视图
	private InterfaceScrollView mScrollView;
	// 超过距离再启动下拉
	private float MIN_Y;
	// 到达距离则开始刷新
	private float MAX_Y;
	// 下拉最大角度，太大就不算下拉
	private double MAX_ANGLE = 30;
	// 触摸开始点
	private float beginX = 0, beginY = 0;
	// 当前
	private float currentX = -1, currentY = -1;
	// 下拉高度
	private float height = 1;
	// 最后触摸时间（防卡死）
	private long lastTouchTime = 0;
	// 刷新延迟时间
	private final long REFRESH_DELAY = 1500;
	// WHAT：回滚
	private final int WHAT_ROLLBACK = 1001;

	private Handler mHandler = new Handler() {
		@Override
		public void dispatchMessage(Message msg) {
			super.dispatchMessage(msg);
			switch (msg.what) {
			case WHAT_ROLLBACK:
				// 重置
				reset();
				mScrollView.setScrollable(true);
				// 刷新数据界面
				mRefreshView.refresh(null);
				break;
			}
		}
	};

	/**
	 * 重置
	 */
	public void reset() {
		// 下拉视图回滚
		if (mPulldownView != null) {
			mPulldownView.rollBack();
		}
		// 设置高度
		height = 1;
		beginX = 0;
		beginY = 0;
		currentX = -1;
		currentY = -1;
	}

	/**
	 * 设置更新时间
	 * @param time
	 */
	public void setRefreshTime(long time) {
		mPulldownView.setLastTime(time);
	}

	public ListenerRefreshTouch(WindowManager windowManager,
			InterfacePulldownView pulldownView, InterfaceRefresh refreshView,
			InterfaceRefresh refreshAnim, InterfaceScrollView scrollView) {
		DisplayMetrics metric = new DisplayMetrics();
		windowManager.getDefaultDisplay().getMetrics(metric);
		MIN_Y = ((float) metric.heightPixels) / 15f;
		MAX_Y = ((float) metric.heightPixels) / 7f;

		mPulldownView = pulldownView;
		mRefreshView = refreshView;
		mRefreshAnim = refreshAnim;
		mScrollView = scrollView;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		long currTime = System.currentTimeMillis();
		if (currTime - lastTouchTime > REFRESH_DELAY + 300) {
			// 重置
			reset();
			mScrollView.setScrollable(true);
		} else {
			if (mPulldownView.isRefreshing()) {
				// 刷新中
				return false;
			}
			if (!mScrollView.isScrollTop()) {
				// 列表未滚到顶部
				return false;
			}
		}

		lastTouchTime = currTime;

		float x = event.getX();
		float y = event.getY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			beginX = x;
			beginY = y;
			currentX = -1;
			currentY = -1;
			break;
		case MotionEvent.ACTION_MOVE:
			if (beginX == 0 && beginY == 0) {
				beginX = x;
				beginY = y;
			}
			if (x == currentX && y == currentY) {
				return false;
			}
			currentX = x;
			currentY = y;
			// 角度超过了？
			if (isAngleBeyond(x, y)) {
				beginX = x;
				beginY = y;
				return false;
			}
			// 设置是否可滚动
			setScrollable(y);
			// 检查刷新
			checkRefresh(y);
			break;
		case MotionEvent.ACTION_UP:
			// 重置
			reset();
			mScrollView.setScrollable(true);
			break;
		}

		return false;
	}

	/**
	 * 角度超过了？
	 * 
	 * @return
	 */
	private boolean isAngleBeyond(float x, float y) {
		float absX = Math.abs(x - beginX);
		float absY = Math.abs(y - beginY);
		if (absY == 0) {
			return true;
		}
		double angle = Math.tan(absX / absY);
		if (angle > MAX_ANGLE) {
			return true;
		}

		return false;
	}

	/**
	 * 设置是否可滚动
	 * 
	 * @param y
	 */
	private void setScrollable(float y) {
		if (y - beginY > MIN_Y) {
			mScrollView.setScrollable(false);
		}
	}

	/**
	 * 检查刷新
	 * 
	 * @param y
	 */
	private void checkRefresh(float y) {
		height += y - beginY - MIN_Y;
		if (height < 1) {
			height = 1;
			mPulldownView.setHeight((int) height);
			return;
		}
		if (height >= MAX_Y) {
			beginRefresh();
		} else {
			mPulldownView.setHeight((int) height);
		}
	}

	/**
	 * 开始刷新
	 */
	public void beginRefresh() {
		mPulldownView.setHeight((int) MAX_Y);
		mPulldownView.showRefresh();
		// 其他界面的刷新动画
		if (mRefreshAnim != null) {
			mRefreshAnim.refresh(null);
		}
		// 延迟刷新
		Message message = new Message();
		message.what = WHAT_ROLLBACK;
		mHandler.sendMessageDelayed(message, REFRESH_DELAY);
	}
}
