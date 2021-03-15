package com.pcs.ztqtj.control.tool;

import android.os.AsyncTask;

public abstract class MAsyncTask extends AsyncTask<Integer, Integer, String> {
	public abstract void doInBackGround();
	public abstract void onPreExercute();
	public abstract void onPostExecute();
	@Override
	protected String doInBackground(Integer... params) {
		doInBackGround();
		return null;
	}

	@Override
	protected void onPreExecute() {
		onPreExercute();
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
	}

	@Override
	protected void onPostExecute(String result) {
		onPostExecute();
	}
}