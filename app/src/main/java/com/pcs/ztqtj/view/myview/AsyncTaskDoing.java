package com.pcs.ztqtj.view.myview;

import android.os.AsyncTask;

public abstract class AsyncTaskDoing extends AsyncTask<Integer, Integer, String>{
	public abstract void inBackground();
	public abstract void preExecute();
	public abstract void progressUpdate(int progress);
	public abstract void postExecute(String result);
	@Override
	protected String doInBackground(Integer... params) {
		inBackground();
		return null;
	}
	@Override
	protected void onPreExecute() {
		preExecute();
		super.onPreExecute();
	}
	@Override
	protected void onPostExecute(String result) {
		postExecute(result);
		super.onPostExecute(result);
	}
	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
	}
	@Override
	protected void onCancelled() {
		super.onCancelled();
	}
}
