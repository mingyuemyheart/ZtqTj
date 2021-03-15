package com.pcs.ztqtj.view.activity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.View;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.ShareTools;
import com.pcs.ztqtj.control.tool.ZtqImageTool;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackShareAboutDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackShareAboutUp;

/**
 * 带分享的activity父类
 * Created by tyaathome on 2016/9/23.
 */
public class FragmentActivityWithShare extends FragmentActivityZtqBase {

    // 分享文字
    protected String mShare = "";
    // 分享内容
    protected Bitmap mShareBitmap;
    // 高德地图截图
    protected Bitmap mAmapBitmap;

    private PackShareAboutUp packUp = new PackShareAboutUp();
    private MyReceiver receiver = new MyReceiver();

    private View.OnClickListener subClassListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBtnRight(R.drawable.icon_share_new, listener);
        PcsDataBrocastReceiver.registerReceiver(this, receiver);
        reqShare();
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(subClassListener != null) {
                subClassListener.onClick(v);
            } else {

                switch (v.getId()) {
                    case R.id.btn_right:
                        View layout = findViewById(android.R.id.content).getRootView();
                        if (layout != null) {
                            mShareBitmap = ZtqImageTool.getInstance().getScreenBitmapNew(FragmentActivityWithShare.this);
                            mShareBitmap = ZtqImageTool.getInstance().stitchQR(FragmentActivityWithShare.this, mShareBitmap);
                            ShareTools.getInstance(FragmentActivityWithShare.this).setShareContent(getTitleText(), mShare, mShareBitmap,"0").showWindow(layout);
                        }
                        break;
                }
            }
        }
    };

    /**
     * 设置子类分享点击回调
     * @param listener
     */
    protected void setShareListener(View.OnClickListener listener) {
        subClassListener = listener;
    }


    /**
     * 处理图片
     * @param bm1 高德地图图片
     * @param bm2 UI图片
     * @return
     */
    protected Bitmap procImage(Bitmap bm1, Bitmap bm2, int top) {
        // 创建上层UI图片大小的新图片
        Bitmap newBm = Bitmap.createBitmap(bm2.getWidth(), bm2.getHeight(), bm2.getConfig());
        // 创建画布
        Canvas canvas = new Canvas(newBm);
        // 先画底层高德地图图片
        canvas.drawBitmap(bm1, 0, top, null);
        canvas.drawBitmap(bm2, 0, 0, null);
        return newBm;
    }

    /**
     * 获取状态栏高度
     * @return
     */
    protected int getStatusBarHeight() {
        int statusBarHeight1 = 0;
        //获取status_bar_height资源的ID
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight1 = getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight1;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(receiver != null) {
            PcsDataBrocastReceiver.unregisterReceiver(this, receiver);
            receiver = null;
        }
    }

    /**
     * 请求分享接口
     */
    private void reqShare() {
        packUp = new PackShareAboutUp();
        // 气象产品分享--短信分享
        packUp.keyword = "ABOUT_QXCP_DXFW";
        PcsDataDownload.addDownload(packUp);
    }

    private class MyReceiver extends PcsDataBrocastReceiver {

        @Override
        public void onReceive(String nameStr, String errorStr) {
            if(packUp.getName().equals(nameStr)) {
                PackShareAboutDown down = (PackShareAboutDown) PcsDataManager.getInstance().getNetPack(nameStr);
                if(down == null) {
                    return ;
                }
                mShare = down.share_content;
            }
        }
    }
}
