package com.pcs.ztqtj.view.activity.photoshow;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.view.activity.FragmentActivityBase;
import com.pcs.ztqtj.view.myview.ImageTouchView;
import com.pcs.lib.lib_pcs_v3.model.image.ImageConstant;
import com.pcs.lib.lib_pcs_v3.model.image.ListenerImageLoad;

/**
 * 图片预览
 */
public class ActivityPhotoFullDetail extends FragmentActivityBase {

	private ImageTouchView imageView;
	private Button btn_finish;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo_full);
		createImageFetcher();
		initView();
		initEvent();
		initData();
	}

	private void initEvent() {
		btn_finish.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	private void initView() {
		imageView = (ImageTouchView) findViewById(R.id.image_view);
		btn_finish = (Button) findViewById(R.id.btn_finish);
	}

	private void initData() {
		String url = getIntent().getStringExtra("url");
		if (!TextUtils.isEmpty(url)) {
			getImageFetcher().addListener(listener);
			getImageFetcher().loadImage(url, null, ImageConstant.ImageShowType.NONE);
		} else {
			// 加载失败
			Toast.makeText(this, R.string.photo_error, Toast.LENGTH_SHORT).show();
		}
	}

	private ListenerImageLoad listener = new ListenerImageLoad() {
		@Override
		public void done(String key, boolean isSucc) {
			if (getImageFetcher().getImageCache() == null) {
				return;
			}
			BitmapDrawable bd = getImageFetcher().getImageCache().getBitmapFromAllCache(key);
			imageView.setMyImageBitmap(bd.getBitmap());
		}
	};
}
