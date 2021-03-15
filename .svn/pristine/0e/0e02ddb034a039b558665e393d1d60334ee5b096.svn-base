package com.pcs.ztqtj.control.loading;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.pcs.lib.lib_pcs_v3.control.log.Log;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib.lib_pcs_v3.model.image.ImageFetcher;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalInit;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackZtqImageDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackZtqImageUp;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.command.AbstractCommand;
import com.pcs.ztqtj.control.tool.MyConfigure;
import com.pcs.ztqtj.view.activity.ActivityMain;
import com.pcs.ztqtj.view.activity.loading.ActivityLoadingImage;

/**
 * 跳转
 *
 * @author Administrator
 */
public class CommandLoadingGoto extends AbstractCommand {
    private Activity mActivity;
    private ImageFetcher mImageFetcher;

    public CommandLoadingGoto(Activity activity, ImageFetcher imageFetcher) {
        mActivity = activity;
        mImageFetcher = imageFetcher;
    }

    @Override
    public void execute() {
        super.execute();
        Log.e("jzy", "执行CommandLoadingGoto");
        // 数据包：初始化
        PackLocalInit packInit = (PackLocalInit) PcsDataManager.getInstance().getLocalPack(PackLocalInit.KEY);
        String versionCode = getVersionCode();

        Intent it = new Intent();
        if (packInit != null && packInit.isNotFirst && versionCode.equals(packInit.versionCode)) {
            Bitmap bitmap = getBitmap();
            if (bitmap != null && bitmap.getByteCount() > 0) {
                // 主题插图
                it.setClass(mActivity, ActivityLoadingImage.class);
            } else {
                // 首页
                it.setClass(mActivity, ActivityMain.class);
            }
        } else {
            if (packInit == null) {
                packInit = new PackLocalInit();
            }
            packInit.isNotFirst = true;
            packInit.versionCode = versionCode;
            PcsDataManager.getInstance().saveLocalData(PackLocalInit.KEY, packInit);
            it.setClass(mActivity, ActivityMain.class);
//            // 引导
//            it.setClass(mActivity, ActivityLoadingGuide.class);
        }
        Bundle bundle = mActivity.getIntent().getBundleExtra(MyConfigure.EXTRA_BUNDLE);
        if(bundle != null) {
            it.putExtra(MyConfigure.EXTRA_BUNDLE, bundle);
        }
        //it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mActivity.startActivity(it);
        mActivity.finish();

        setStatus(Status.SUCC);
    }

    public Bitmap getBitmap() {
        // 下载包：主题插图
        PackZtqImageDown packImageDown = (PackZtqImageDown) PcsDataManager.getInstance().getNetPack(PackZtqImageUp.NAME);
        if (packImageDown == null) {
            return null;
        }
        Bitmap bitmap = packImageDown.getBitmap(mActivity.getString(R.string.file_download_url), mImageFetcher);

        return bitmap;
    }

    private String getVersionCode() {
        String str = "";
        PackageManager pm = mActivity.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(mActivity.getPackageName(), 0);
            str = String.valueOf(pi.versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return str;
    }
}
