package com.pcs.ztqtj.control.tool;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;

import com.pcs.ztqtj.R;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ZtqImageTool {
	private static ZtqImageTool instance;

	public static ZtqImageTool getInstance() {
		if (instance == null) {
			instance = new ZtqImageTool();
		}
		return instance;
	}

	private ZtqImageTool() {
	}

	/**
	 * 获取天气图标
	 * 
	 * @param context
	 *            上下文
	 * @param isDay
	 *            是否是白天，是的话则取白天的天气图标
	 * @param icon_when
	 *            天气图标标识
	 * @return Bitmap
	 * @throws IOException
	 */
	public Bitmap getWeatherIcon(Context context, boolean isDay,
			String icon_when) throws IOException {
		if (icon_when == null || "".equals(icon_when)) {
			Bitmap bm = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.icon_weather_default);
			return bm;
		}
		String iconpath = "weather_icon/";
		if (isDay) {
			iconpath += "daytime/w";
		} else {
			iconpath += "night/n";
		}
		iconpath = iconpath + icon_when + ".png";

		InputStream iput;
		Bitmap bm = null;
		try {
			iput = context.getAssets().open(iconpath);
			bm = BitmapFactory.decodeStream(iput);
			iput.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bm;
	}

    /**
     * 截取view的图
     * @param v
     * @return
     */
    public Bitmap getScreenBitmap(View v) {
        // 保存view原始大小
        int width = v.getWidth();
        int height = v.getHeight();

        v.measure(View.MeasureSpec.makeMeasureSpec(v.getWidth(), View.MeasureSpec.AT_MOST),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());

        Bitmap bitmap = Bitmap.createBitmap(v.getMeasuredWidth() , v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        v.draw(c);
        // 压缩图片
        Bitmap b = compress(bitmap);

        // 还原参数
        v.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.AT_MOST),
                View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.AT_MOST));
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());

        return b;
    }

    /**
     * 截图
     * @param activity
     * @return
     */
    public Bitmap getScreenBitmapNew(Activity activity) {
        View v1 = activity.getWindow().getDecorView().getRootView();
        v1.setDrawingCacheEnabled(true);
        v1.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
        v1.setDrawingCacheEnabled(false);
        int statucBarHeight = getStatusBarHeight(activity);
        if(statucBarHeight > 0) {
            Bitmap bmResult = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight()-statucBarHeight, bitmap.getConfig());
            Canvas canvas = new Canvas(bmResult);
            canvas.drawBitmap(bitmap, 0, -statucBarHeight, null);
            return bmResult;
        }
        return bitmap;
    }

    /**
     * 压缩图片(采样率压缩法)
     * @param image
     * @return
     */
    private Bitmap compress(Bitmap image) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, out);
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        int be = 2;
        newOpts.inSampleSize = be;
        ByteArrayInputStream isBm = new ByteArrayInputStream(out.toByteArray());
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        return bitmap;
    }

    /**
     * 拼接二维码
     * @param image
     * @return
     */
    public Bitmap stitchQR(Context context, Bitmap image) {
        // 二维码
        Bitmap qrBm = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_screen);
        // 缩放二维码至原始图片的宽
        qrBm = resizeBitmap(qrBm, image.getWidth());
        Bitmap finalImage = Bitmap.createBitmap(image.getWidth(), image.getHeight() + qrBm.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(finalImage);
        // 拼接图片
        canvas.drawBitmap(image, 0, 0, null);
        canvas.drawBitmap(qrBm, 0, image.getHeight(), null);
        return finalImage;
    }

    /**
     * 拼接两个图片
     * @param bm1
     * @param bm2
     * @return
     */
    public Bitmap stitch(Bitmap bm1, Bitmap bm2) {
        bm2 = resizeBitmap(bm2, bm1.getWidth());
        int height = bm1.getHeight() + bm2.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(bm1.getWidth(), height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bm1, 0, 0, null);
        canvas.drawBitmap(bm2, 0, bm1.getHeight(), null);
        return bitmap;
    }

    /**
     * 缩放图片大小
     * @param bitmap
     * @param targetWidth
     * @return
     */
    public Bitmap resizeBitmap(Bitmap bitmap, int targetWidth) {
        float scale = (float)targetWidth/(float)bitmap.getWidth();
        int newWidth = targetWidth;
        int newHeight = (int)(bitmap.getHeight()*scale);
        Bitmap newBm = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
        return newBm;
    }



    public Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * 获取状态栏高度
     * @return
     */
    public int getStatusBarHeight(Context context) {
        int statusBarHeight1 = 0;
        //获取status_bar_height资源的ID
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight1 = context.getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight1;
    }

    /**
     * 截取整个webview
     * @param webView
     * @return
     */
    public Bitmap screenshotWebView(WebView webView) {
        webView.measure(View.MeasureSpec.makeMeasureSpec(
                View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        webView.setDrawingCacheEnabled(true);
        webView.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(webView.getMeasuredWidth(),
                webView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        int iHeight = bitmap.getHeight();
        canvas.drawBitmap(bitmap, 0, iHeight, paint);
        webView.draw(canvas);
        return bitmap;
    }

    /**
     * 获取 WebView 视图截图
     * @param context
     * @param view
     * @return
     */
    public Bitmap getWebViewBitmap(Context context, WebView view) {
        if (null == view) return null;
        int y = view.getScrollY();
        view.scrollTo(0, 0);
        view.buildDrawingCache(true);
        view.setDrawingCacheEnabled(true);
        view.setVerticalScrollBarEnabled(false);
        Bitmap b = getViewBitmapWithoutBottom(view);
        // 可见高度
        int vh = view.getHeight();
        // 容器内容实际高度
        int th = (int)(view.getContentHeight()*view.getScale());
        Bitmap temp = null;
        if (th > vh) {
            int w = getScreenWidth(context);
            int absVh = vh - view.getPaddingTop() - view.getPaddingBottom();
            do {
                // 剩余高度
                int restHeight = th - vh;
                if (restHeight <= absVh) {
                    view.scrollBy(0, restHeight);
                    vh += restHeight;
                    temp = getViewBitmap(view);
                } else {
                    view.scrollBy(0, absVh);
                    vh += absVh;
                    temp = getViewBitmapWithoutBottom(view);
                }
                b = mergeBitmap(vh, w, temp, 0, view.getScrollY(), b, 0, 0);
            } while (vh < th);
        }
        // 回滚到当前位置
        view.scrollTo(0, y);
        view.setVerticalScrollBarEnabled(true);
        view.setDrawingCacheEnabled(false);
        view.destroyDrawingCache();
        return b;
    }

    /**
     * 拼接图片
     * @param newImageH
     * @param newImageW
     * @param background
     * @param backX
     * @param backY
     * @param foreground
     * @param foreX
     * @param foreY
     * @return
     */
    private Bitmap mergeBitmap(int newImageH, int newImageW, Bitmap background, float backX, float backY, Bitmap foreground, float foreX, float foreY) {
        if (null == background || null == foreground) {
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(newImageW, newImageH, Bitmap.Config.RGB_565);
        Canvas cv = new Canvas(bitmap);
        cv.drawBitmap(background, backX, backY, null);
        cv.drawBitmap(foreground, foreX, foreY, null);
        cv.save();
        cv.restore();
        return bitmap;
    }

    /**
     * get the width of screen
     */
    private int getScreenWidth(Context ctx) {
        int w = 0;
        if (Build.VERSION.SDK_INT > 13) {
            Point p = new Point();
            ((WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getSize(p);
            w = p.x;
        } else {
            w = ((WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();
        }
        return w;
    }

    private static Bitmap getViewBitmapWithoutBottom(View v) {
        if (null == v) {
            return null;
        }
        v.setDrawingCacheEnabled(true);
        v.buildDrawingCache();
        if (Build.VERSION.SDK_INT >= 11) {
            v.measure(View.MeasureSpec.makeMeasureSpec(v.getWidth(), View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(v.getHeight(), View.MeasureSpec.EXACTLY));
            v.layout((int) v.getX(), (int) v.getY(), (int) v.getX() + v.getMeasuredWidth(), (int) v.getY() + v.getMeasuredHeight());
        } else {
            v.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
        }
        Bitmap bp = Bitmap.createBitmap(v.getDrawingCache(), 0, 0, v.getMeasuredWidth(), v.getMeasuredHeight() - v.getPaddingBottom());
        v.setDrawingCacheEnabled(false);
        v.destroyDrawingCache();
        return bp;
    }

    public Bitmap getViewBitmap(View v) {
        if (null == v) {
            return null;
        }
        v.setDrawingCacheEnabled(true);
        v.buildDrawingCache();
        if (Build.VERSION.SDK_INT >= 11) {
            v.measure(View.MeasureSpec.makeMeasureSpec(v.getWidth(), View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(v.getHeight(), View.MeasureSpec.EXACTLY));
            v.layout((int) v.getX(), (int) v.getY(), (int) v.getX() + v.getMeasuredWidth(), (int) v.getY() + v.getMeasuredHeight());
        } else {
            v.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
        }
        Bitmap b = Bitmap.createBitmap(v.getDrawingCache(), 0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
        v.setDrawingCacheEnabled(false);
        v.destroyDrawingCache();
        return b;
    }

    /**
     * 缩放图片
     * @param bitmap
     * @param scale
     * @return
     */
    public Bitmap zoomBitmap(Bitmap bitmap, float scale) {
        Matrix matrix = new Matrix();
        matrix.postScale(scale,scale); //长和宽放大缩小的比例
        return Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
    }


}
