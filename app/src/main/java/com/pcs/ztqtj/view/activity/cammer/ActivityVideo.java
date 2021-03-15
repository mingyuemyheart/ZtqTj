package com.pcs.ztqtj.view.activity.cammer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.pcs.ztqtj.R;

public class ActivityVideo extends Activity {

	private MovieRecorderView mRecorderView;// 视频录制控件
	private Button mShootBtn;// 视频开始录制按钮
	private boolean isFinish = true;
	private boolean success = false;// 防止录制完成后出现多次跳转事件
	private TextView tv_usetime;

	private int timeUse=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cammer);
		mRecorderView = (MovieRecorderView) findViewById(R.id.movieRecorderView);
		mShootBtn = (Button) findViewById(R.id.shoot_button);
		tv_usetime = (TextView) findViewById(R.id.tv_usetime);
		// 用户长按事件监听
		mShootBtn.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {// 用户按下拍摄按钮
					mShootBtn.setBackgroundResource(R.drawable.bg_movie_add_shoot_select);
					mRecorderView.record(new MovieRecorderView.OnRecordFinishListener() {
						@Override
						public void onRecordFinish() {
							if (!success && mRecorderView.getTimeCount() < mRecorderView.Max) {// 判断用户按下时间是否大于10秒
								success = true;
								handler.sendEmptyMessage(1);
							}
						}
						@Override
						public void onRecordTime(int time) {
							timeUse=time;
							handler.sendEmptyMessage(0);
						}
					});
				} else if (event.getAction() == MotionEvent.ACTION_UP) {// 用户抬起拍摄按钮
					mShootBtn.setBackgroundResource(R.drawable.bg_movie_add_shoot);
					if (mRecorderView.getTimeCount() > 3) {// 判断用户按下时间是否大于3秒
						if (!success) {
							success = true;
							handler.sendEmptyMessage(1);
						}
					} else {
						success = false;
						if (mRecorderView.getmVecordFile() != null)
							mRecorderView.getmVecordFile().delete();// 删除录制的过短视频
						mRecorderView.stop();// 停止录制
						Toast.makeText(ActivityVideo.this, "视频录制时间太短", Toast.LENGTH_SHORT).show();
					}
				}
				return true;
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		isFinish = true;
		if (mRecorderView.getmVecordFile() != null)
			mRecorderView.getmVecordFile().delete();// 视频使用后删除
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		isFinish = false;
		success = false;
		mRecorderView.stop();// 停止录制
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what==1) {
				if (success) {
					finishActivity();
				}
			}else{
				tv_usetime.setText(timeUse+"秒");
			}
		}
	};

	// 视频录制结束后，跳转的函数
	private void finishActivity() {
		if (isFinish) {
			mRecorderView.stop();
			Intent intent=new Intent();
			intent.putExtra("file",mRecorderView.getmVecordFile().toString());
			setResult(Activity.RESULT_OK,intent);
			finish();
//			Log.i("z","------------------>"+mRecorderView.getmVecordFile().toString());
//			Intent intent = new Intent(this, SuccessActivity.class);
//			Bundle bundle = new Bundle();
//			bundle.putString("text", mRecorderView.getmVecordFile().toString());
//			intent.putExtras(bundle);
//			startActivity(intent);
		}
		success = false;
	}

	/**
	 * 录制完成回调
	 */
	public interface OnShootCompletionListener {
		public void OnShootSuccess(String path, int second);

		public void OnShootFailure();
	}
}
