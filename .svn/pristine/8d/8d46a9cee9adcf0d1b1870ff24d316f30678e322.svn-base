package com.pcs.ztqtj.control.tool.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class ImageUtils {

	private static ImageUtils instance = new ImageUtils();

	public static ImageUtils getInstance() {
		if (instance == null) {
			instance = new ImageUtils();
		}
		return instance;
	}

	// 使用Bitmap加Matrix来缩放
	public Drawable resizeImage(Bitmap bitmap, int w, int h) {
		Bitmap BitmapOrg = bitmap;
		int width = BitmapOrg.getWidth();
		int height = BitmapOrg.getHeight();
		int newWidth = w;
		int newHeight = h;

		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;

		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		// if you want to rotate the Bitmap
		// matrix.postRotate(45);
		Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width,
				height, matrix, true);
		return new BitmapDrawable(resizedBitmap);
	}
	public void setBgImage(Context mContext,ImageView view, int mWidth,int drawableId){
		setBgImage(mContext, view, mWidth, drawableId, true);
	}

	public void setBgImage(Context mContext,ImageView mImageView, int mWidth,int drawableId,boolean type){
//		Drawable drawable = mContext.getResources().getDrawable(
//				drawableId);
		
//		int maxHeight = CommUtils.Dip2Px(mContext, mWidth);
//		int height = (int) ((float) mWidth / drawable.getMinimumWidth() * drawable
//				.getMinimumHeight());
//		if (height > maxHeight) {
//			height = maxHeight;
//		}
		
		Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(),drawableId);
		int width = bitmap.getWidth();// 获取真实宽高
		int height = bitmap.getHeight();
		int layoutHeight = (height * mWidth) / width;// 调整高度
		
//		view.setImageDrawable(resizeImage(bitmap, mWidth, height));
		if(type){
//			mImageView.setLayoutParams(new LinearLayout.LayoutParams(
//					LinearLayout.LayoutParams.FILL_PARENT, height));
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(mWidth, layoutHeight);
			mImageView.setLayoutParams(lp);
			mImageView.setImageBitmap(bitmap);
		}else{
			// 重新设置宽度和高度
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
					mWidth, layoutHeight);
//			view.setLayoutParams(new RelativeLayout.LayoutParams(
//					RelativeLayout.LayoutParams.FILL_PARENT, height));
			mImageView.setLayoutParams(lp);
			mImageView.setImageBitmap(bitmap);
		}
		
//		drawable = null;
		bitmap = null;
	}
	
	public void setBgImage(Context mContext,ImageView mImageView, int mWidth,Bitmap bitmap,boolean type){
//		Drawable drawable = mContext.getResources().getDrawable(
//				drawableId);
		
//		int maxHeight = CommUtils.Dip2Px(mContext, mWidth);
//		int height = (int) ((float) mWidth / drawable.getMinimumWidth() * drawable
//				.getMinimumHeight());
//		if (height > maxHeight) {
//			height = maxHeight;
//		}
		int width = bitmap.getWidth();// 获取真实宽高
		int height = bitmap.getHeight();
		int layoutHeight = (height * mWidth) / width;// 调整高度
		
//		view.setImageDrawable(resizeImage(bitmap, mWidth, height));
		if(type){
//			mImageView.setLayoutParams(new LinearLayout.LayoutParams(
//					LinearLayout.LayoutParams.FILL_PARENT, height));
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(mWidth, layoutHeight);
			mImageView.setLayoutParams(lp);
			mImageView.setImageBitmap(bitmap);
		}else{
			// 重新设置宽度和高度
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
					mWidth, layoutHeight);
//			view.setLayoutParams(new RelativeLayout.LayoutParams(
//					RelativeLayout.LayoutParams.FILL_PARENT, height));
			mImageView.setLayoutParams(lp);
			mImageView.setImageBitmap(bitmap);
		}
		
//		drawable = null;
		bitmap = null;
	}

}
