package com.pcs.ztqtj.view.activity.photoshow;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.view.activity.FragmentActivityBase;
import com.pcs.ztqtj.view.myview.ImageTouchView;
import com.pcs.lib.lib_pcs_v3.model.image.ImageResizer;

import java.io.File;

/**
 * 显示一个完整的照片
 * 
 * @author JiangZy
 * 
 */
public class ActivityPhotoFullSubmit extends FragmentActivityBase {
	private String mPath;
	private ImageResizer mResizer;
	// 等待对话框
	private ProgressDialog mProgress;
    private final int REQUEST_CODE_PHOTO=122;
	/**
	 * 照片的Bitmap
	 */
	private Bitmap mBitmapPhoto;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo_full);
        setResult(Activity.RESULT_OK);
		showProgressDialog();

		mResizer = new ImageResizer();

		mPath = getIntent().getStringExtra("path");
		File file = null;
		if (!TextUtils.isEmpty(mPath)) {
			file = new File(mPath);
		}

		if (file.exists()) {
			// 加载SD卡图片
			reloadImageSd(mPath);
			// 旋转按钮
			initBtnRotate();

		} else {
			// 加载失败
			Toast.makeText(this, R.string.photo_error, Toast.LENGTH_SHORT)
					.show();
		}

		// 点击事件
		View layoutRoot = findViewById(R.id.layout_root);
		layoutRoot.setOnClickListener(mOnClick);

		dismissProgressDialog();
	}

	@Override
	protected void onDestroy() {
		if (mBitmapPhoto != null && !mBitmapPhoto.isRecycled()) {
			mBitmapPhoto.recycle();
		}
		super.onDestroy();
	}

	/**
	 * 初始化旋转按钮
	 */
	private void initBtnRotate() {
		Button rotationLeft,rotationRight,btn_finish;

		rotationLeft = (Button) findViewById(R.id.btn_rotate_left);
		rotationLeft.setVisibility(View.VISIBLE);
		rotationLeft.setOnClickListener(mOnClick);

		rotationRight = (Button) findViewById(R.id.btn_rotate_right);
		rotationRight.setVisibility(View.VISIBLE);
		rotationRight.setOnClickListener(mOnClick);
		
		btn_finish = (Button) findViewById(R.id.btn_finish);
		btn_finish.setVisibility(View.VISIBLE);
		btn_finish.setOnClickListener(mOnClick);
	}

	/**
	 * 加载SD卡图片
	 * 
	 * @param path
	 */
	private void reloadImageSd(String path) {
		if (mBitmapPhoto != null && !mBitmapPhoto.isRecycled()) {
			mBitmapPhoto.recycle();
		}
		mBitmapPhoto = BitmapFactory.decodeFile(mPath);
		ImageTouchView imageView = (ImageTouchView) findViewById(R.id.image_view);
		imageView.setMyImageBitmap(mBitmapPhoto);
	}

	/**
	 * 显示等待对话框
	 */
	public void showProgressDialog(String keyWord) {
		if (mProgress == null) {
			mProgress = new ProgressDialog(this);
			mProgress.setCancelable(true);
			mProgress.setCanceledOnTouchOutside(false);
			mProgress.setOnCancelListener(mProgressOnCancel);
		}
		if (mProgress.isShowing()) {
			mProgress.setMessage(keyWord);
		} else {
			mProgress.show();
			mProgress.setMessage(keyWord);
		}
	}

	public void showProgressDialog() {
		showProgressDialog(getResources().getString(R.string.please_wait));
	}

	/**
	 * 取消等待对话框
	 */
	public void dismissProgressDialog() {
		if (mProgress != null && mProgress.isShowing()) {
			mProgress.dismiss();
		}
	}

	private OnClickListener mOnClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.layout_root:
				ActivityPhotoFullSubmit.this.finish();
				break;
			case R.id.btn_rotate_left:
				// 左旋转
				showProgressDialog();
				mResizer.rotateSD(mPath, -90, mImageListener);
				break;
			case R.id.btn_rotate_right:
				// 右旋转
				showProgressDialog();
				mResizer.rotateSD(mPath, 90, mImageListener);
				break;
			case R.id.btn_finish:
//                Intent intent = new Intent();
//                //把返回数据存入Intent
//                intent.putExtra("result", "My name is linjiqin");
//                //设置返回数据
//                ActivityPhotoFullSubmit.this.setResult(REQUEST_CODE_PHOTO, intent);
				ActivityPhotoFullSubmit.this.finish();
				break;
			}
		}
	};

	/**
	 * 进度框OnCancel
	 */
	private OnCancelListener mProgressOnCancel = new OnCancelListener() {
		@Override
		public void onCancel(DialogInterface dialog) {
			ActivityPhotoFullSubmit.this.finish();
		}
	};

	/**
	 * 图片修改监听
	 */
	private ImageResizer.ImageResizerListener mImageListener = new ImageResizer.ImageResizerListener() {
		@Override
		public void doneSD(String path, boolean isSucc) {
			if (isSucc) {
				reloadImageSd(path);
				setResult(Activity.RESULT_OK);
			}

			dismissProgressDialog();
		}
	};
}
