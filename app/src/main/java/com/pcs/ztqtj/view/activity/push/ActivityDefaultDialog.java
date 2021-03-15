package com.pcs.ztqtj.view.activity.push;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.pcs.ztqtj.R;

/**
 * 公告提醒弹窗
 * 
 * @author chenjh
 */
public class ActivityDefaultDialog extends ActivityBasePushDialog {

	private Button closebtn;
	private TextView titleTextView;
	private TextView contentTextView;
	private String type = "", title = "", content = "";

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_dialog_default_layout);

		ActivityDefaultDialog.this.setFinishOnTouchOutside(false);
		Intent intent = getIntent();
		try {
            type = intent.getStringExtra("TYPE");
            title = intent.getStringExtra("TITLE");
            content = intent.getStringExtra("CONTENT");
        } catch (Exception e){

        }
		initView();
		initData();
	}

	public void initView() {
		closebtn = (Button) findViewById(R.id.close_btn);

		titleTextView = (TextView) findViewById(R.id.title);

		contentTextView = (TextView) findViewById(R.id.content);

		closebtn.setOnClickListener(new MClick());
	}

	public void initData() {

		titleTextView.setText(title);
		contentTextView.setText(content);

	}

	private class MClick implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.close_btn:
				finish();
				break;
			default:
				break;
			}
		}
	}
}
