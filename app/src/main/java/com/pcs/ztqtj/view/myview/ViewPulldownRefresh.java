package com.pcs.ztqtj.view.myview;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.listener.ListenerRefreshTouch.InterfacePulldownView;
import com.pcs.lib.lib_pcs_v3.control.tool.TimeTool;

/**
 * 控制下拉刷新视图
 * 
 * @author JiangZy
 * 
 */
public class ViewPulldownRefresh implements InterfacePulldownView {
	private Context mContext;
	private View mRefreshLayout;
	// 正在刷新？
	private boolean mIsRefreshing = false;
	// 上次更新时间
	private long mLastTime = 0;

	public ViewPulldownRefresh(Context context, View refreshLayout) {
		mContext = context;
		mRefreshLayout = refreshLayout;
	}

	/**
	 * 设置上次更新时间
	 */
	@Override
	public void setLastTime(long time) {
		mLastTime = time;
		resetContent();
	}

	/**
	 * 设置高度
	 */
	@Override
	public void setHeight(int h) {
		LayoutParams layoutParams = mRefreshLayout.getLayoutParams();
		layoutParams.height = h;

		mRefreshLayout.setLayoutParams(layoutParams);
	}

	/**
	 * 回滚
	 */
	@Override
	public void rollBack() {
		// 设置高度
		LayoutParams layoutParams = mRefreshLayout.getLayoutParams();
		layoutParams.height = 0;
		mRefreshLayout.setLayoutParams(layoutParams);
		// 重设内容
		resetContent();
		// 正在刷新？
		mIsRefreshing = false;
	}

	/**
	 * 显示正在刷新
	 */
	@Override
	public void showRefresh() {
		// 图片
		ImageView imageView = (ImageView) mRefreshLayout.findViewById(R.id.image_pulldown);
		imageView.setBackgroundResource(R.drawable.icon_pulldown_sun);

		Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.rotate_repeat_2000);
		LinearInterpolator lin = new LinearInterpolator();
		animation.setInterpolator(lin);
		imageView.startAnimation(animation);
		// 文字
		TextView textView = (TextView) mRefreshLayout.findViewById(R.id.text_pulldown);
		textView.setText(mContext.getString(R.string.refreshing));
		// 正在刷新？
		mIsRefreshing = true;
	}

	/**
	 * 正在刷新？
	 */
	@Override
	public boolean isRefreshing() {
		return mIsRefreshing;
	}

	/**
	 * 重设内容
	 */
	private void resetContent() {
		// 图片
		ImageView imageView = (ImageView) mRefreshLayout.findViewById(R.id.image_pulldown);
		imageView.setBackgroundResource(R.drawable.icon_pullwodn_arrow);
		imageView.clearAnimation();
		AnimationDrawable animation = (AnimationDrawable) imageView.getBackground();
		animation.start();

		// 文字
		TextView textView = (TextView) mRefreshLayout.findViewById(R.id.text_pulldown);
		if (mLastTime > 0) {
			String str = TimeTool.getInstance().getRefreshTime(mLastTime)
					+ mContext.getString(R.string.refresh) + "\n" 
					+ mContext.getString(R.string.refresh_pulldown);
			textView.setText(str);
		} else {
			textView.setText(mContext.getString(R.string.refresh_pulldown));
		}
	}
}
