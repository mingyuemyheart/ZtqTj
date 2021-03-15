package com.pcs.ztqtj.view.activity.air_quality;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.pcs.ztqtj.R;

public class AcitvityAirWhatAQI extends Activity {
	private Button positivebutton;
	private Button close_btn;
	private TextView contenttv,tv_title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activityairwhataqi);
		contenttv = (TextView) findViewById(R.id.contenttv);
        tv_title= (TextView) findViewById(R.id.title);
		String content = getIntent().getStringExtra("w");
		String title=getIntent().getStringExtra("t");
        tv_title.setText(title);
		if (TextUtils.isEmpty(content)) {

		} else {
			contenttv.setText(content);
		}
		positivebutton = (Button) findViewById(R.id.positivebutton);
		close_btn = (Button) findViewById(R.id.close_btn);
		positivebutton.setText("我知道了");
		positivebutton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		close_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

}
