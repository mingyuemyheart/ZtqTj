package com.pcs.ztqtj.control.tool.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tyaathome on 2017/9/11.
 */

public class ImageMultipleLoadFromUrl {

    private List<String> mUrlList = new ArrayList<>();
    private OnMultipleCompleteListener multipleCompleteListener;
    private Object[] mObject;
    private static ImageMultipleLoadFromUrl instance;

    public ImageMultipleLoadFromUrl() {
        init();
    }

    private void init() {

    }

    static public ImageMultipleLoadFromUrl getInstance() {
        if(instance == null) {
            instance = new ImageMultipleLoadFromUrl();
        }
        return instance;
    }

    public ImageMultipleLoadFromUrl setParams(List<String> urlList, OnMultipleCompleteListener listener) {
        this.mUrlList = urlList;
        this.multipleCompleteListener = listener;
        return instance;
    }

    public ImageMultipleLoadFromUrl setObject(Object... object) {
        this.mObject = object;
        return instance;
    }

    public void startMultiple() {
        ImageMultipleLoadAsyncTask task = new ImageMultipleLoadAsyncTask();
        task.execute(mUrlList);
    }

    private class ImageMultipleLoadAsyncTask extends AsyncTask<List<String>, Integer, List<Bitmap>> {

        @Override
        protected List<Bitmap> doInBackground(List<String>... params) {
            try {
                List<Bitmap> bitmapList = new ArrayList<>();
                List<String> urlList = params[0];
                for (int i = 0; i < urlList.size(); i++) {
                    URL url = new URL(urlList.get(i));
                    Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    bitmapList.add(bitmap);
                    publishProgress(i, urlList.size());
                }
                return bitmapList;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if(multipleCompleteListener != null && values.length == 2) {
                multipleCompleteListener.onProgress(values[0], values[1]);
            }
        }

        @Override
        protected void onPostExecute(List<Bitmap> bitmapList) {
            super.onPostExecute(bitmapList);
            if(multipleCompleteListener != null) {
                multipleCompleteListener.onComplete(bitmapList, mObject);
            }
        }

    }

    public interface OnMultipleCompleteListener {
        void onComplete(List<Bitmap> bitmapList, Object... object);
        void onProgress(int progress, int max);
    }

}
