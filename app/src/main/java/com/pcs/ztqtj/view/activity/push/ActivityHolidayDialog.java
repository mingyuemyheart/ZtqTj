package com.pcs.ztqtj.view.activity.push;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.pcs.ztqtj.R;

import java.io.IOException;
import java.io.InputStream;

/**
 * 节日、节气提醒弹窗
 * 
 * @author chenjh
 */
public class ActivityHolidayDialog extends ActivityBasePushDialog {

	private Button closebtn;
	private ImageView iconweather;
	private TextView titleTextView;
	private TextView contentTextView;
	private String title, holiday_name, content;
	private String type = "";// 节日or节气
	private String ico = ""; // 图片名称 gq

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_dialog_holiday_layout);

		ActivityHolidayDialog.this.setFinishOnTouchOutside(false);
		Intent intent = getIntent();
		type = intent.getStringExtra("TYPE");
		title = intent.getStringExtra("TITLE");
		holiday_name = intent.getStringExtra("HOLIDAY_NAME");
		content = intent.getStringExtra("CONTENT");
		ico = intent.getStringExtra("ICO");
		initView();
		initData();
	}

	public void initView() {
		closebtn = (Button) findViewById(R.id.close_btn);

		iconweather = (ImageView) findViewById(R.id.iconweather);

		titleTextView = (TextView) findViewById(R.id.title);

		contentTextView = (TextView) findViewById(R.id.content);

		closebtn.setOnClickListener(new MClick());
	}

	public void initData() {
//		String tipTitle = "";
//		if("0".equals(days)){
//			tipTitle = "<font color=\"#FFFFFF\">亲，</font><font color=\"#FCFF00\">今天</font><font color=\"#FFFFFF\">是"+holiday_name+"啦！</font>";
//		}else{
//			tipTitle = "<font color=\"#FFFFFF\">亲，还有</font><font color=\"#FCFF00\">"
//					+ days + "天</font><font color=\"#FFFFFF\">就到"+holiday_name+"啦！</font>";
//		}
//		if (!TextUtils.isEmpty(days)) {
//			titleTextView.setText(Html.fromHtml(tipTitle));
//		}
        titleTextView.setText(title);
		contentTextView.setText(content);
		String str = "";
		if (type.equals("节日")) {
			str = "jr_";
		} else if (type.equals("节气")) {
			str = "jq_";
		}
		System.out.println(str+ico);
		if (!TextUtils.isEmpty(ico)) {
			try {
				InputStream is = getResources().getAssets().open(
						"img_holiday/" + str + ico+".png");
				Bitmap bm = BitmapFactory.decodeStream(is);
				iconweather.setImageBitmap(bm);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
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
