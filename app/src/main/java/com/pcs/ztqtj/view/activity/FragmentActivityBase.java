package com.pcs.ztqtj.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import com.pcs.ztqtj.R;
import com.pcs.lib.lib_pcs_v3.model.image.ImageCache;
import com.pcs.lib.lib_pcs_v3.model.image.ImageFetcher;

public class FragmentActivityBase extends FragmentActivity {
	// 图片获取类
	private ImageFetcher mImageFetcher = null;
	// ImageFetcher已恢复？
	private boolean mFetcherResumed = false;

	@Override
	protected void onCreate(@Nullable Bundle arg0) {
		super.onCreate(arg0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (mImageFetcher != null && !mFetcherResumed) {
			mFetcherResumed = true;
			mImageFetcher.setExitTasksEarly(false);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (mImageFetcher != null && !mFetcherResumed) {
			mFetcherResumed = true;
			mImageFetcher.setExitTasksEarly(false);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mImageFetcher != null) {
			mFetcherResumed = false;
			mImageFetcher.setPauseWork(false);
			mImageFetcher.setExitTasksEarly(true);
			mImageFetcher.flushCache();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mImageFetcher != null) {
			mImageFetcher.closeCache();
		}
	}
	
	public ImageFetcher getImageFetcher() {
		if (mImageFetcher == null) {
			createImageFetcher();
		}

		return mImageFetcher;
	}

	/**
	 * 创建图片获取类
	 * 
	 */
	protected void createImageFetcher() {
		ImageCache.ImageCacheParams cacheParams = new ImageCache.ImageCacheParams(
				this);
		cacheParams.setMemCacheSizePercent(0.25f);
		mImageFetcher = new ImageFetcher(this);
		mImageFetcher.addImageCache(this.getSupportFragmentManager(),
				cacheParams);
		mImageFetcher.setLoadingImage(R.drawable.no_pic);
	}
}
