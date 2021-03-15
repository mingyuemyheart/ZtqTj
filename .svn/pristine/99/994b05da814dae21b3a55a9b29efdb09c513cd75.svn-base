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
import com.pcs.ztqtj.view.activity.loading.ActivityLoading;

/**
 * 天气推送信息弹窗
 *
 * @author chenjx
 */
@SuppressLint("NewApi")
public class ActivityPushWeatherDialog extends ActivityBasePushDialog {
    private Button shareBtn;
    private Button positiveBtn;
    private Button closeBtn;
    private TextView weathertitle;
    private TextView weatherdata;
    private TextView contentTextView;

    private String title; // 天气推送标题
    private String ptime; // 天气推送发布时间
    private String content; // 天气推送内容

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_pushweatherlayout);

        ActivityPushWeatherDialog.this.setFinishOnTouchOutside(false);
        Intent intent = getIntent();
        title = intent.getStringExtra("TITLE");
        ptime = intent.getStringExtra("PTIME");
        content = intent.getStringExtra("CONTENT");
        initView();
        initData();
    }

    public void initView() {
        closeBtn = (Button) findViewById(R.id.close_btn);
        positiveBtn = (Button) findViewById(R.id.positivebutton);
        shareBtn = (Button) findViewById(R.id.share);


        weathertitle = (TextView) findViewById(R.id.weathertitle);
        weatherdata = (TextView) findViewById(R.id.weatherdata);
        contentTextView = (TextView) findViewById(R.id.content);

        shareBtn.setOnClickListener(new MClick());
        positiveBtn.setOnClickListener(new MClick());
        closeBtn.setOnClickListener(new MClick());
    }

    public void initData() {
        weathertitle.setText(title);
        weatherdata.setText(ptime);
        contentTextView.setText(content);
    }

    private class MClick implements OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.close_btn:
                    finish();
                    break;
                case R.id.positivebutton:
                    finish();
                    break;
                case R.id.share:
//                    Intent intent = getBaseContext().getPackageManager()
//                            .getLaunchIntentForPackage( getBaseContext().getPackageName());
//                    intent.setAction(Intent.ACTION_MAIN);
//                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(intent);
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    intent.setClass(ActivityPushWeatherDialog.this, ActivityLoading.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                    break;
                default:
                    break;
            }
        }
    }
}
