package com.pcs.ztqtj.view.activity.loading;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.MyConfigure;
import com.pcs.ztqtj.control.tool.utils.TextUtil;
import com.pcs.ztqtj.view.activity.ActivityMain;
import com.pcs.ztqtj.view.activity.FragmentActivityBase;
import com.squareup.picasso.Picasso;

/**
 * 闪屏页-主题插图
 */
public class ActivityLoadingImage extends FragmentActivityBase {

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            gotoMain();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_loading_image);
        View btn = findViewById(R.id.btn_close);
        btn.setOnClickListener(mOnClick);

        ImageView imageView = findViewById(R.id.imageView);
        if (getIntent().hasExtra("imgUrl")) {
            String imgUrl = getIntent().getStringExtra("imgUrl");
            if (!TextUtil.isEmpty(imgUrl)) {
                Picasso.get().load(imgUrl).into(imageView);
                //定时跳转
                long showTime = 1500;
                if (getIntent().hasExtra("showTime")) {
                    showTime = getIntent().getLongExtra("showTime", 1500);
                }
                mHandler.sendEmptyMessageDelayed(0, showTime);
            }
        }
    }

    /**
     * 前往首页
     */
    public void gotoMain() {
        Intent it = new Intent();
        Bundle bundle = getIntent().getBundleExtra(MyConfigure.EXTRA_BUNDLE);
        if(bundle != null) {
            it.putExtra(MyConfigure.EXTRA_BUNDLE, bundle);
        }
        it.setClass(this, ActivityMain.class);
        startActivity(it);
        finish();
    }

    private View.OnClickListener mOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mHandler.removeMessages(0);
            gotoMain();
        }
    };

}
