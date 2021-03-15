package com.pcs.ztqtj.control.tool.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by tyaathome on 2017/4/24.
 */

public class ImageLoadFromUrl {

    private String mUrl = "";
    private OnCompleteListener mListener;

    private static ImageLoadFromUrl instance;

    private Object[] mObject;

    public ImageLoadFromUrl() {
        init();
    }

    private void init() {

    }

    static public ImageLoadFromUrl getInstance() {
        if(instance == null) {
            instance = new ImageLoadFromUrl();
        }
        return instance;
    }

    public ImageLoadFromUrl setParams(String url, OnCompleteListener listener) {
        this.mUrl = url;
        this.mListener = listener;
        return instance;
    }

    public ImageLoadFromUrl setObject(Object... object) {
        this.mObject = object;
        return instance;
    }

    public void start() {
        ImageLoadAsyncTask task = new ImageLoadAsyncTask();
        task.execute(mUrl);
    }

    private class ImageLoadAsyncTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                BitmapFactory.Options options = new BitmapFactory.Options();
                //options.inPreferredConfig = Bitmap.Config.RGB_565;
                options.inSampleSize = 2;
                Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream(), null, options);
                //Bitmap bitmap = decodeStream(url.openConnection().getInputStream());
                return bitmap;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if(mListener != null) {
                mListener.onComplete(bitmap, mObject);
            }
        }
    }

    public interface OnCompleteListener {
        void onComplete(Bitmap bitmap, Object... object);
    }

}
