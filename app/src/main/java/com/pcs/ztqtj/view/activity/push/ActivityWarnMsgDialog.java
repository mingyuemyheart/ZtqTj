package com.pcs.ztqtj.view.activity.push;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.pcs.ztqtj.R;

import java.util.HashMap;

/**
 * 防御指南弹窗
 * 
 * @author chenjh
 */
public class ActivityWarnMsgDialog extends Activity {

	private Button closebtn;
	private TextView titleTextView;
	private TextView contentTextView;
	private String type, title, content;
	private String ico;
	
	final String[] level = { "bb_O", "bb_R", "by_B", "by_O", "by_R", "by_Y",
			"df_O", "df_R", "df_Y", "dljb_O", "dljb_R", "dljb_Y", "dw_O",
			"dw_R", "dw_Y", "gh_O", "gh_R", "gw_O", "gw_R", "jw_B", "jw_O",
			"jw_R", "jw_Y", "ld_O", "ld_R", "ld_Y", "m_O", "m_Y", "sd_B",
			"sd_O", "sd_Y", "tf_B", "tf_O", "tf_R", "tf_Y", "xz_O", "xz_R",
			"xz_Y" };

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_dialog_default_layout);

		ActivityWarnMsgDialog.this.setFinishOnTouchOutside(false);
		Intent intent = getIntent();
//		type = intent.getStringExtra("TYPE");
		content = intent.getStringExtra("content");
		ico = intent.getStringExtra("ico");
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
		String icoKey = ico;

		if (!TextUtils.isEmpty(icoKey)) {
			contentTextView.setText(getString(getDefenseMsg(icoKey)));
		} else {
			contentTextView.setText(content);
		}

	}


	private int getDefenseMsg(String icon) {

		HashMap<String, Integer> map = new HashMap<String, Integer>();
		for (int i = 0; i < level.length; i++) {
			map.put(level[i], i);
		}
		if (map.get(icon) == null) {
			return R.string.defense_38;
		}
		try {
			return R.string.defense_00 + map.get(icon);
		} catch (Exception e) {
			return 0;
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
