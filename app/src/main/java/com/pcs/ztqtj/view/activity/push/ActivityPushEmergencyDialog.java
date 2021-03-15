package com.pcs.ztqtj.view.activity.push;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.SharedPreferencesUtil;

/**
 * Created by tyaathome on 2017/7/25.
 */

public class ActivityPushEmergencyDialog extends ActivityBasePushDialog {
    private Button shareBtn;
    private Button positiveBtn;
    private Button closeBtn;
    private TextView weathertitle;
    private TextView weatherdata;
    private TextView contentTextView;
    private String title; // 预警标题
    private String author; // 预警来源
    private String ptime; // 预警发布时间
    private String content; // 预警内容
    private String id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_notificlicklayout_tfgg);
        setFinishOnTouchOutside(false);
        Intent intent = getIntent();
        title = intent.getStringExtra("TITLE");
        author = intent.getStringExtra("AUTHOR");
        ptime = intent.getStringExtra("PTIME");
        content = intent.getStringExtra("CONTENT");
        id = intent.getStringExtra("ID");
        initView();
        initData();
    }

    private void initView() {
        closeBtn = (Button) findViewById(R.id.close_btn);
        positiveBtn = (Button) findViewById(R.id.positivebutton);
        shareBtn = (Button) findViewById(R.id.share);

        weathertitle = (TextView) findViewById(R.id.weathertitle);
        weatherdata = (TextView) findViewById(R.id.weatherdata);
        contentTextView = (TextView) findViewById(R.id.content);

        shareBtn.setOnClickListener(mListener);
        positiveBtn.setOnClickListener(mListener);
        closeBtn.setOnClickListener(mListener);
    }

    private void initData() {
//        String cptime = "";
//        if (!TextUtils.isEmpty(ptime)) {
//            try {
//                cptime = CommUtils.DateToStr2(CommUtils.StrToDate2(ptime));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
        weathertitle.setText(title);
        weatherdata.setText(author + " " + ptime + " 发布");
        contentTextView.setText(content);
    }

    private View.OnClickListener mListener = new View.OnClickListener() {
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
                    // 分享替换成更多：跳转到詳情。
                    SharedPreferencesUtil.putData(id,id);
                    Intent intent = new Intent(ActivityPushEmergencyDialog.this, ActivityPushEmergencyDetails.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("TITLE", title);
                    intent.putExtra("AUTHOR", author);
                    intent.putExtra("PTIME", ptime);
                    intent.putExtra("CONTENT", content);
                    intent.putExtra("ID", id);
                    startActivity(intent);
                    break;
                default:
                    break;
            }
        }
    };
}
