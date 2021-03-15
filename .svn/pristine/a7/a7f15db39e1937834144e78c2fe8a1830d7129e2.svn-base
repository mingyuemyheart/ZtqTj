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
import com.pcs.ztqtj.control.tool.CommUtils;
import com.pcs.ztqtj.control.tool.SharedPreferencesUtil;

import java.io.IOException;
import java.io.InputStream;

/**
 * 预警信息弹窗
 *
 * @author chenjh
 */
@SuppressLint("NewApi")
public class ActivityPushDialog extends ActivityBasePushDialog {
    private Button shareBtn;
    private Button positiveBtn;
    private Button closeBtn;
    private ImageView iconweather;
    private TextView weathertitle;
    private TextView weatherdata;
    private TextView contentTextView;

    private String title; // 预警标题
    // private String signaltype; // 预警类型
    // private String signallevel; // 预警等级
    private String author; // 预警来源
    private String ptime; // 预警发布时间
    private String content; // 预警内容
    private String ico; // 预警图片名称
    private String id;//预警ID
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_notificlicklayout);

        ActivityPushDialog.this.setFinishOnTouchOutside(false);
        Intent intent = getIntent();
        title = intent.getStringExtra("TITLE");
        // signaltype = intent.getStringExtra("SIGNALTYPE");
        // signallevel = intent.getStringExtra("SIGNALLEVEL");
        author = intent.getStringExtra("AUTHOR");
        ptime = intent.getStringExtra("PTIME");
        content = intent.getStringExtra("CONTENT");
        ico = intent.getStringExtra("ICO");
        id=intent.getStringExtra("ID");
        initView();
        initData();
    }

    public void initView() {
        closeBtn = (Button) findViewById(R.id.close_btn);
        positiveBtn = (Button) findViewById(R.id.positivebutton);
        shareBtn = (Button) findViewById(R.id.share);

        iconweather = (ImageView) findViewById(R.id.iconweather);

        weathertitle = (TextView) findViewById(R.id.weathertitle);
        weatherdata = (TextView) findViewById(R.id.weatherdata);
        contentTextView = (TextView) findViewById(R.id.content);

        shareBtn.setOnClickListener(new MClick());
        positiveBtn.setOnClickListener(new MClick());
        closeBtn.setOnClickListener(new MClick());
    }

    public void initData() {
        String cptime = "";
        if (!TextUtils.isEmpty(ptime)) {
            try {
                cptime = CommUtils.DateToStr2(CommUtils.StrToDate2(ptime));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        weathertitle.setText(title);
        weatherdata.setText(author + " " + cptime);
        contentTextView.setText(content);
        try {
            InputStream is = getResources().getAssets().open("img_warn/" + ico + ".png");
            Bitmap bm = BitmapFactory.decodeStream(is);
            iconweather.setImageBitmap(bm);
        } catch (IOException e) {
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
                    finish();
//				Intent it1 = new Intent(ActivityPushDialog.this, ActivityWarnMsgDialog.class);
//				it1.putExtra("ico", ico);
//				it1.putExtra("content", content);
//				startActivity(it1);

                    break;
                case R.id.share:
                    // 分享替换成更多：跳转到詳情。
                    SharedPreferencesUtil.putData(id,id);
                    Intent intent = new Intent(ActivityPushDialog.this, ActivityPushWarnDetails.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("TITLE", title);
                    intent.putExtra("AUTHOR", author);
                    intent.putExtra("PTIME", ptime);
                    intent.putExtra("CONTENT", content);
                    intent.putExtra("ICO", ico);// 预警图片
                    intent.putExtra("ID", id);// 预警图片
                    startActivity(intent);
                    // ShareUtil.share(ActivityPushDialog.this);
                    break;
                default:
                    break;
            }
        }
    }
}
