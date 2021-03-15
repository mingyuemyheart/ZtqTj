package com.pcs.ztqtj.view.activity.service;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.ShareTools;
import com.pcs.ztqtj.control.tool.ZtqImageTool;
import com.pcs.ztqtj.view.activity.FragmentActivitySZYBBase;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackShareAboutDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackShareAboutUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.service.PackNearReportDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.service.PackNearReportUp;

/**
 * Created by chenjx
 * on 2018/4/27.
 */
public class ActivityNearReport extends FragmentActivitySZYBBase {

    private MyReceiver receiver = new MyReceiver();
    private TextView content;
    private PackNearReportUp nearReportUp;
    private TextView null_context;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        // 注册广播接收
        PcsDataBrocastReceiver.registerReceiver(ActivityNearReport.this, receiver);
        setContentView(R.layout.activity_near);
        setTitleText("临近报告");
        initView();
        initEvent();
        initData();
    }

    private void initView() {
        content = (TextView) findViewById(R.id.context);
        null_context = (TextView) findViewById(R.id.null_context);
    }

    private void initEvent() {
        setBtnRight(R.drawable.icon_share_new, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View layout = findViewById(R.id.layout);
                layout.measure(View.MeasureSpec.makeMeasureSpec(layout.getWidth(), View.MeasureSpec.AT_MOST),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                // layout高度
                int height = layout.getMeasuredHeight();

                Bitmap shareBitmap;
                DisplayMetrics metrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metrics);
                // 屏幕高度
                int screenHeight = metrics.heightPixels;

                if (height < screenHeight) {
                    // 截取全屏
                    shareBitmap = ZtqImageTool.getInstance().getScreenBitmapNew(ActivityNearReport.this);
                } else {
                    // 截图整个layout
                    Bitmap headBitmap = ZtqImageTool.getInstance().getScreenBitmap(headLayout);
                    shareBitmap = ZtqImageTool.getInstance().getScreenBitmap(layout);
                    shareBitmap = ZtqImageTool.getInstance().stitch(headBitmap, shareBitmap);

                }
                shareBitmap = ZtqImageTool.getInstance().stitchQR(ActivityNearReport.this, shareBitmap);
                PackShareAboutDown shareDown = (PackShareAboutDown) PcsDataManager.getInstance().getNetPack
                        (PackShareAboutUp.getNameCom());
                String shareContent = "";
                if (shareDown != null) {
                    shareContent = shareDown.share_content;
                }
                ShareTools.getInstance(ActivityNearReport.this).setShareContent(getTitleText(),
                        shareContent, shareBitmap, "0").showWindow(layout);
            }
        });
    }

    private void initData() {

        if (!isOpenNet()) {
            showToast(getString(R.string.net_err));
            return;
        }
        showProgressDialog();

        nearReportUp = new PackNearReportUp();
        PcsDataDownload.addDownload(nearReportUp);
    }

    private class MyReceiver extends PcsDataBrocastReceiver {
        @Override
        public void onReceive(String name, String error) {
            if (nearReportUp == null) {
                return;
            }
            if (name.equals(nearReportUp.getName())) {
                dismissProgressDialog();
                PackNearReportDown nearReportDown = (PackNearReportDown) PcsDataManager.getInstance().getNetPack
                        (name);
                if (nearReportDown == null) {
                    return;
                }
                showData(nearReportDown);
            }
        }
    }

    /**
     * 数据返回处理数据
     */
    private void showData(PackNearReportDown nearReportDown) {
        if (nearReportDown.txt.isEmpty()) {
            null_context.setVisibility(View.VISIBLE);
        } else {
            content.setText(nearReportDown.txt);
            null_context.setVisibility(View.GONE);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(receiver);
    }
}
