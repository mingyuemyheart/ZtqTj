package com.pcs.ztqtj.control.tool.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.ImageView;

import com.pcs.ztqtj.R;
import com.pcs.lib.lib_pcs_v3.control.tool.BitmapUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * @author Z
 *个人中心获取圆形图片
 */
public class GetImageView {
	private ImageView imageview;
	private Context context;
	public void setImageView(Context context, String imageviewPath, ImageView imgview) {
		this.context = context;
		this.imageview = imgview;
		if (TextUtils.isEmpty(imageviewPath)) {
			imgview.setImageResource(R.drawable.head_default);
		} else {
			new DownloadImageTask().execute(imageviewPath);
		}
	}
	private class DownloadImageTask extends AsyncTask<String, Void, Drawable> {
		protected Drawable doInBackground(String... urls) {
			return loadImageFromNetwork(context, urls[0]);
		}
		protected void onPostExecute(Drawable result) {
			if (result != null) {
				imageview.setImageDrawable(result);
			}
		}
	}
	public Drawable loadImageFromNetwork(Context context, String imageUrl) {
		Drawable drawable = null;
		if (imageUrl == null) {
			return null;
		}
		String imagePath = "";
		String fileName = "";
		// 获取url中图片的文件名与后缀
		if (imageUrl != null && imageUrl.length() != 0) {
			fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
		}
		// 图片在手机本地的存放路径,注意：fileName为空的情况
		imagePath = context.getCacheDir() + "/" + fileName;
		File file = new File(context.getCacheDir(), fileName);// 保存文件
		if (!file.exists() && !file.isDirectory()) {
			try {
				// 可以在这里通过文件名来判断，是否本地有此图片
				FileOutputStream fos = new FileOutputStream(file);
				InputStream is = new URL(imageUrl).openStream();
				int data = is.read();
				while (data != -1) {
					fos.write(data);
					data = is.read();
				}
				fos.close();
				is.close();
//				保存成圆形图片
				Bitmap bmRoot = BitmapFactory.decodeFile(file.toString());
				Bitmap bmRound = BitmapUtil.toRoundBitmap(bmRoot);
				BitmapUtil.saveBitmap(bmRound, context.getCacheDir().toString(), fileName);
				drawable = new BitmapDrawable(bmRound);
			} catch (IOException e) {
			}
		} else {
			drawable = Drawable.createFromPath(file.toString());
		}
		return drawable;
	}
}
