package com.pcs.ztqtj.view.activity.push;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalUser;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.ServiceLoginTool;
import com.pcs.ztqtj.model.ZtqCityDB;

/**
 * 气象服务弹窗
 */
@SuppressLint("NewApi")
public class ActivityPushServiceDialog extends ActivityBasePushDialog {
	private Button positiveBtn;
	private Button closeBtn;
	private TextView tvTitle;
	private TextView contentTextView;

	private String title = "";
	private String id = "";

    private MyReceiver receiver = new MyReceiver();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_push_service);
		this.setFinishOnTouchOutside(false);
		initView();
		initData();
	}

    @Override
    protected void onResume() {
        super.onResume();
        if(receiver != null) {
            PcsDataBrocastReceiver.registerReceiver(this, receiver);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(receiver != null) {
            PcsDataBrocastReceiver.unregisterReceiver(this, receiver);
        }
    }

    public void initView() {
		closeBtn = (Button) findViewById(R.id.close_btn);
		positiveBtn = (Button) findViewById(R.id.positivebutton);

		tvTitle = (TextView) findViewById(R.id.pushtitle);
		contentTextView = (TextView) findViewById(R.id.content);
		positiveBtn.setOnClickListener(new MClick());

		closeBtn.setOnClickListener(new MClick());
	}

	public void initData() {
        if(receiver != null) {
            PcsDataBrocastReceiver.registerReceiver(this, receiver);
        }

		Intent intent = getIntent();
		try {
			title = intent.getStringExtra("TITLE");// 标题
			String content = intent.getStringExtra("CONTENT");// 内容
            id = intent.getStringExtra("id");
			
			if(!TextUtils.isEmpty(title)){
				tvTitle.setText(title);
			}
			if(!TextUtils.isEmpty(content)){
				contentTextView.setText(content);
			}
			// String jsonStr =
			// "{\"TITLE\":\"决策预报\",\"CONTENT\":\"《专题天气报告2016第2期（0317）防汛会商纪要》，请您收阅！\"}";
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class MClick implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.close_btn:
				finish();
				break;
			case R.id.positivebutton:
                ServiceLoginTool.getInstance().reqLoginQuery();
				break;
			}
		}
	}

	private void intentDetail() {

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 112) {
			PackLocalUser localUserinfo = ZtqCityDB.getInstance().getMyInfo();
			// 登录界面返回还为空。即没有登录，，则不做处理，若不为空这跳转到详细页面
			if (localUserinfo == null || TextUtils.isEmpty(localUserinfo.user_id)) {
			} else {
				intentDetail();
			}
		}
	}

    private class MyReceiver extends PcsDataBrocastReceiver {

        @Override
        public void onReceive(String nameStr, String errorStr) {
            ServiceLoginTool.getInstance().callback(nameStr, new ServiceLoginTool.CheckListener() {
                @Override
                public void onSuccess() {
                    if(!TextUtils.isEmpty(id)){
                        Intent intent = new Intent(ActivityPushServiceDialog.this, ActivityPushServiceDetails.class);
                        intent.putExtra("title", title);
                        intent.putExtra("id", id);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        // packup.id = "201604071838532";
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(ActivityPushServiceDialog.this, "文章不存在", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFail() {
                    ServiceLoginTool.getInstance().createAlreadyLogined(ActivityPushServiceDialog.this);
                }
            });
        }
    }
}
