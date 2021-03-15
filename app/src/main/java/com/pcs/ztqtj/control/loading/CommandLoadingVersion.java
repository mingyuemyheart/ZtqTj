package com.pcs.ztqtj.control.loading;

import android.app.Activity;
import android.content.pm.PackageManager.NameNotFoundException;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.command.AbstractCommand;
import com.pcs.lib.lib_pcs_v3.control.log.Log;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackCheckVersionUp;

/**
 * 版本信息
 *
 * @author JiangZY
 */
public class CommandLoadingVersion extends AbstractCommand {

    private Activity mActivity;

    public CommandLoadingVersion(Activity activity) {
        mActivity = activity;
    }

    @Override
    public void execute() {
        super.execute();
        Log.e("jzy", "执行CommandLoadingVersion");
        setStatus(Status.SUCC);

        TextView text = (TextView) mActivity.findViewById(R.id.text_version);
        String pkName = mActivity.getPackageName();
        try {
            String versionName = mActivity.getPackageManager().getPackageInfo(
                    pkName, 0).versionName;
            text.setText(versionName);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        // 下载版本信息
        PackCheckVersionUp packUp = new PackCheckVersionUp();
        PcsDataDownload.addDownload(packUp);
    }

}
