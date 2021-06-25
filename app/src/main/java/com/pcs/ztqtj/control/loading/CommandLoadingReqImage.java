package com.pcs.ztqtj.control.loading;

import android.app.Activity;
import android.util.DisplayMetrics;

import com.pcs.ztqtj.control.command.AbstractCommand;
import com.pcs.lib.lib_pcs_v3.control.log.Log;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.pack.PcsPackUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackZtqImageUp;

/**
 * 闪屏页面，请求图片URL
 */
public class CommandLoadingReqImage extends AbstractCommand {

    private Activity mActivity;

    public CommandLoadingReqImage(Activity activity) {
        mActivity = activity;
    }

    @Override
    public void execute() {
        super.execute();
        // 图片URL包
        PcsPackUp packImage = createPackUp();
        // 请求图片url
        PcsDataDownload.addDownload(packImage);
        setStatus(Status.SUCC);
    }

    private PcsPackUp createPackUp() {
        PackZtqImageUp packImage = new PackZtqImageUp();
        packImage.size_type = 2;
        DisplayMetrics metric = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(metric);
        if (metric.widthPixels > 480) {
            packImage.size_type = 1;
        }
        return packImage;
    }
}
