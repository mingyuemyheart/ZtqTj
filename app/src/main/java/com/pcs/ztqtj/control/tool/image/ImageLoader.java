package com.pcs.ztqtj.control.tool.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.pcs.ztqtj.R;
import com.pcs.lib.lib_pcs_v3.control.file.PcsGetPathValue;
import com.pcs.lib.lib_pcs_v3.control.file.PcsMD5;
import com.pcs.lib.lib_pcs_v3.control.tool.BitmapUtil;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

public class ImageLoader {
	private String imagePath = "";
	private static final String TAG = "ImageLoader";
	private static final int MAX_CAPACITY = 10;// 一级缓存的最大空间
	private static final long DELAY_BEFORE_PURGE = 10 * 1000;// 定时清理缓存
	// 0.75是加载因子为经验值，true则表示按照最近访问量的高低排序，false则表示按照插入顺序排序
	private HashMap<String, Bitmap> mFirstLevelCache = new LinkedHashMap<String, Bitmap>(
			MAX_CAPACITY / 2, 0.75f, true) {
		private static final long serialVersionUID = 1L;

		protected boolean removeEldestEntry(Entry<String, Bitmap> eldest) {
			if (size() > MAX_CAPACITY) {// 当超过一级缓存阈值的时候，将老的值从一级缓存搬到二级缓存
				mSecondLevelCache.put(eldest.getKey(),
						new SoftReference<Bitmap>(eldest.getValue()));
				return true;
			}
			return false;
		}
    };
	// 二级缓存，采用的是软应用，只有在内存吃紧的时候软应用才会被回收，有效的避免了oom
	private ConcurrentHashMap<String, SoftReference<Bitmap>> mSecondLevelCache = new ConcurrentHashMap<String, SoftReference<Bitmap>>(
			MAX_CAPACITY / 2);

	// 定时清理缓存
	private Runnable mClearCache = new Runnable() {
		@Override
		public void run() {
			clear();
		}
	};
	private Handler mPurgeHandler = new Handler();

	Executor mExecutor = new Executor();

	public ImageLoader(Context mContext) {
		imagePath = PcsGetPathValue.getInstance().getImagePath();
		mExecutor.start();
	}

	private static final BlockingQueue<ImageLoadTask> mTasks = new LinkedBlockingQueue<ImageLoadTask>();
	// 通过信号量控制同时执行的线程数
	Semaphore mSemaphore = new Semaphore(150);

	// 这里是任务的消费者，去任务队列取出下载任务，然后执行，当没有任务的时候消费者就等待
	class Executor extends Thread {
		@Override
		public void run() {
			while (true) {
				ImageLoadTask task = null;
				try {
					task = mTasks.take();
					if (task != null) {
						mSemaphore.acquire();
						task.execute();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (Exception e) {
					Log.d(TAG, "[ImageLoadTask]:"
							+ e.getStackTrace().toString());
				}
			}
		}
	}

	// 重置缓存清理的timer
	private void resetPurgeTimer() {
		mPurgeHandler.removeCallbacks(mClearCache);
		mPurgeHandler.postDelayed(mClearCache, DELAY_BEFORE_PURGE);
	}

	/**
	 * 清理缓存
	 */
	private void clear() {
		mFirstLevelCache.clear();
		mSecondLevelCache.clear();
	}

	/**
	 * 返回缓存，如果没有则返回null
	 * 
	 * @param url
	 * @return
	 */
	public Bitmap getBitmapFromCache(String url) {
		Bitmap bitmap = null;
		bitmap = getFromFirstLevelCache(url);// 从一级缓存中拿
		if (bitmap != null) {
			return bitmap;
		}
		bitmap = getFromSecondLevelCache(url);// 从二级缓存中拿
		return bitmap;
	}

	/**
	 * 从二级缓存中拿
	 * 
	 * @param url
	 * @return
	 */
	private Bitmap getFromSecondLevelCache(String url) {
		Bitmap bitmap = null;
		SoftReference<Bitmap> softReference = null;
		softReference = mSecondLevelCache.get(url);
		if (softReference != null) {
			bitmap = softReference.get();
			if (bitmap == null) {// 由于内存吃紧，软引用已经被gc回收了
				mSecondLevelCache.remove(url);
			}
		}
		return bitmap;
	}

	/**
	 * 从一级缓存中拿
	 * 
	 * @param url
	 * @return
	 */
	private Bitmap getFromFirstLevelCache(String url) {
		Bitmap bitmap = null;
		synchronized (mFirstLevelCache) {
			bitmap = mFirstLevelCache.get(url);
			if (bitmap != null) {// 将最近访问的元素放到链的头部，提高下一次访问该元素的检索速度（LRU算法）
				mFirstLevelCache.remove(url);
				mFirstLevelCache.put(url, bitmap);
			}
		}
		return bitmap;
	}

	/**
	 * 
	 * @param url
	 *            图片地址
	 * @param adapter
	 *            适配器
	 * @param imageView
	 *            图片控件
	 */
	public void loadImage(String url, BaseAdapter adapter, ImageView imageView) {

		resetPurgeTimer();
		Bitmap bitmap = getBitmapFromCache(url);// 从缓存中读取
		if (bitmap == null) {
			imageView.setImageResource(R.drawable.no_pic);// 缓存没有设为默认图片
			File file = new File(imagePath + PcsMD5.Md5(url) + ".png");
			if (file.exists()) {
				bitmap = decodeSampledBitmapFromResource(
						imagePath + PcsMD5.Md5(url) + ".png", 720);
				imageView.setImageBitmap(bitmap);
			} else {
				ImageLoadTask imageLoadTask = new ImageLoadTask(url, adapter,
						imageView);
				try {
					// 将任务放入队列中
					mTasks.put(imageLoadTask);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} else {
			imageView.setImageBitmap(bitmap);// 设为缓存图片
		}
		bitmap = null;
	}

	/**
	 * 
	 * @param url
	 *            图片地址
	 * @param adapter
	 *            适配器
	 * @param imageView
	 *            图片控件
	 * @param rid
	 *            默认图片id：R.drawable.no_pic
	 */
	public void loadImage(String url, BaseAdapter adapter, ImageView imageView,
			int rid) {
		resetPurgeTimer();
		Bitmap bitmap = getBitmapFromCache(url);// 从缓存中读取
		if (bitmap == null) {
			imageView.setImageResource(rid);// 缓存没有设为默认图片
			File file = new File(imagePath + PcsMD5.Md5(url) + ".png");
			if (file.exists()) {
				bitmap = decodeSampledBitmapFromResource(
						imagePath + PcsMD5.Md5(url) + ".png", 720);
				imageView.setImageBitmap(bitmap);
				// imageView.setImageURI(Uri.fromFile(file));
			} else {
				ImageLoadTask imageLoadTask = new ImageLoadTask(url, adapter,
						imageView);
				try {
					// 将任务放入队列中
					mTasks.put(imageLoadTask);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} else {
			imageView.setImageBitmap(bitmap);// 设为缓存图片
		}
		bitmap = null;
	}

	/**
	 * 加载图片，如果缓存中有就直接从缓存中拿，缓存中没有就下载
	 * 
	 * @param url
	 * @param imageView
	 */
	public void loadImage(String url, ImageView imageView) {

		resetPurgeTimer();
		Bitmap bitmap = getBitmapFromCache(url);// 从缓存中读取
		if (bitmap == null) {
			imageView.setImageResource(R.drawable.no_pic);// 缓存没有设为默认图片
			File file = new File(imagePath + PcsMD5.Md5(url) + ".png");
			if (file.exists()) {
				imageView.setImageURI(Uri.fromFile(file));
			} else {
				ImageLoadTask imageLoadTask = new ImageLoadTask(url, imageView);
				try {
					// 将任务放入队列中
					mTasks.put(imageLoadTask);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} else {
			imageView.setImageBitmap(bitmap);// 设为缓存图片
		}
		bitmap = null;
	}

	/**
	 * 下载图片
	 * 
	 * @param url
	 */
	public void loadImage(String url) {

		Bitmap bitmap = getBitmapFromCache(url);// 从缓存中读取
		if (bitmap == null) {
			File file = new File(imagePath + PcsMD5.Md5(url) + ".png");
			if (!file.exists()) {
				ImageLoadTask imageLoadTask = new ImageLoadTask(url);
				try {
					// 将任务放入队列中
					mTasks.put(imageLoadTask);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		bitmap = null;
	}

	/**
	 * 
	 * @param url
	 *            图片地址
	 * @param adapter
	 *            适配器
	 * @param imageView
	 *            图片控件
	 * @param rid
	 *            默认图片id：R.drawable.no_pic
	 * @param screenWidth
	 */
	public void loadImage(String url, Context context, BaseAdapter adapter,
			ImageView imageView, int rid, int screenWidth) {
		resetPurgeTimer();
		Bitmap bitmap = getBitmapFromCache(url);// 从缓存中读取
		if (bitmap == null) {
			// imageView.setImageResource(rid);// 缓存没有设为默认图片
			ImageUtils.getInstance().setBgImage(context, imageView,
					screenWidth, R.drawable.no_pic, false);
			File file = new File(imagePath + PcsMD5.Md5(url) + ".png");
			if (file.exists()) {
				bitmap = decodeSampledBitmapFromResource(
						imagePath + PcsMD5.Md5(url) + ".png", 720);
				imageView.setImageBitmap(bitmap);
				// imageView.setImageURI(Uri.fromFile(file));
			} else {
				ImageLoadTask imageLoadTask = new ImageLoadTask(url, adapter,
						imageView);
				try {
					// 将任务放入队列中
					mTasks.put(imageLoadTask);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} else {
			imageView.setImageBitmap(bitmap);// 设为缓存图片
		}
		bitmap = null;
	}

	/**
	 * 
	 * @param url
	 *            图片地址
	 * @param context
	 *            适配器
	 * @param imageView
	 *            图片控件
	 * @param rid
	 *            默认图片id：R.drawable.no_pic
	 * @param screenWidth
	 */
	public void loadImage(String url, Context context, ImageView imageView,
			int rid, int screenWidth, boolean flag) {
		resetPurgeTimer();
		Bitmap bitmap = getBitmapFromCache(url);// 从缓存中读取
		if (bitmap == null) {
			// imageView.setImageResource(rid);// 缓存没有设为默认图片
			ImageUtils.getInstance().setBgImage(context, imageView,
					screenWidth, rid, flag);
			File file = new File(imagePath + PcsMD5.Md5(url) + ".png");
			if (file.exists()) {
				bitmap = decodeSampledBitmapFromResource(
						imagePath + PcsMD5.Md5(url) + ".png", screenWidth);
				ImageUtils.getInstance().setBgImage(context, imageView,
						screenWidth, bitmap, flag);
				// imageView.setImageURI(Uri.fromFile(file));
			} else {
				ImageLoadTask imageLoadTask = new ImageLoadTask(url, imageView);
				try {
					// 将任务放入队列中
					mTasks.put(imageLoadTask);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} else {
			imageView.setImageBitmap(bitmap);// 设为缓存图片
		}
		bitmap = null;
	}

	/**
	 * 放入缓存
	 * 
	 * @param url
	 * @param value
	 */
	public void addImage2Cache(String url, Bitmap value) {
		if (value == null || url == null) {
			return;
		}
		synchronized (mFirstLevelCache) {
			mFirstLevelCache.put(url, value);
		}
	}

	public class ImageLoadTask extends AsyncTask<Object, Void, Bitmap> {
		String url;
		BaseAdapter adapter;
		ImageView imageView;

		public ImageLoadTask(String url, BaseAdapter adapter,
				ImageView imageView) {
			this.url = url;
			this.adapter = adapter;
			this.imageView = imageView;
		}

		public ImageLoadTask(String url, ImageView imageView) {
			this.url = url;
			this.imageView = imageView;
		}

		public ImageLoadTask(String url) {
			this.url = url;
		}

		@Override
		protected Bitmap doInBackground(Object... params) {
			// Log.d(TAG, "func doInBackground-----");
			Bitmap drawable = loadImageFromInternet(url);// 获取网络图片
			return drawable;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			mSemaphore.release();
			if (result == null) {
				return;
			}
			try {
				BitmapUtil.saveBitmap(result, imagePath, PcsMD5.Md5(url)
						+ ".png");
			} catch (FileNotFoundException e) {

				e.printStackTrace();
			}

			addImage2Cache(url, result);// 放入缓存
			if (imageView != null && adapter == null) {
				imageView.setImageBitmap(result);// 设为缓存图片
				imageView.invalidate();
			}
			if (adapter != null) {
				adapter.notifyDataSetChanged();// 触发getView方法执行，这个时候getView实际上会拿到刚刚缓存好的图片
			}

		}
	}

	public Bitmap loadImageFromInternet(String url) {
		Bitmap bitmap = null;
		HttpClient client = AndroidHttpClient.newInstance("Android");
		HttpParams params = client.getParams();
		HttpConnectionParams.setConnectionTimeout(params, 3000);
		HttpConnectionParams.setSocketBufferSize(params, 3000);
		HttpResponse response = null;
		InputStream inputStream = null;
		HttpGet httpGet = null;
		try {
			httpGet = new HttpGet(url);
			response = client.execute(httpGet);
			int stateCode = response.getStatusLine().getStatusCode();
			if (stateCode != HttpStatus.SC_OK) {
				// Log.d(TAG, "func [loadImage] stateCode=" + stateCode);
				return bitmap;
			}
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				try {
					inputStream = entity.getContent();
					return bitmap = BitmapFactory.decodeStream(inputStream);
				} finally {
					if (inputStream != null) {
						inputStream.close();
					}
					entity.consumeContent();
				}
			}
		} catch (ClientProtocolException e) {
			httpGet.abort();
			e.printStackTrace();
		} catch (IOException e) {
			httpGet.abort();
			e.printStackTrace();
		} finally {
			((AndroidHttpClient) client).close();
		}
		return bitmap;
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth) {
		// 源图片的宽度
		final int width = options.outWidth;
		int inSampleSize = 1;
		if (width > reqWidth) {
			// 计算出实际宽度和目标宽度的比率
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = widthRatio;
		}
		return inSampleSize;
	}

	public static Bitmap decodeSampledBitmapFromResource(String pathName,
			int reqWidth) {
		// 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(pathName, options);
		// 调用上面定义的方法计算inSampleSize值
		options.inSampleSize = calculateInSampleSize(options, reqWidth);
		// 使用获取到的inSampleSize值再次解析图片
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(pathName, options);
	}
}
