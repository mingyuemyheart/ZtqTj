package com.pcs.ztqtj.view.activity.loading;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.MyConfigure;
import com.pcs.ztqtj.view.activity.ActivityMain;
import com.pcs.ztqtj.view.activity.FragmentActivityBase;
import com.pcs.lib.lib_pcs_v3.control.log.Log;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackZtqImageDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackZtqImageUp;

/**
 * 闪屏页-主题插图
 * @author Administrator
 */
public class ActivityLoadingImage extends FragmentActivityBase {

    //显示时间
    private final long SHOW_TIME = 1300;

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
        Log.e("jzy", "主题插图onCreate");
        setContentView(R.layout.activity_loading_image);
        createImageFetcher();
        //背景
        View layout = findViewById(R.id.layout_image);
        BitmapDrawable drawable = new BitmapDrawable(getBitmap());
        layout.setBackgroundDrawable(drawable);
        //按钮
        View btn = findViewById(R.id.btn_close);
        btn.setOnClickListener(mOnClick);
        //定时跳转
        mHandler.sendEmptyMessageDelayed(0, SHOW_TIME);
    }

    public Bitmap getBitmap() {
        // 下载包：主题插图
        PackZtqImageDown packImageDown = (PackZtqImageDown) PcsDataManager.getInstance().getNetPack(PackZtqImageUp.NAME);
        if (packImageDown == null) {
            return null;
        }
        Bitmap bitmap = packImageDown.getBitmap(getString(R.string.file_download_url), getImageFetcher());

        return bitmap;
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
        //it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
