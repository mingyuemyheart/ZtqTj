package com.pcs.ztqtj.control.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.pcs.ztqtj.view.activity.ActivityMain;
import com.pcs.lib.lib_pcs_v3.control.tool.Util;

/**
 * @author Z 开机启动广播
 */
public class BootStrapReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // 判断是否是开机启动
        if (Util.getPreferencesBooleanValue(context, "root", "start")) {
            Intent i = new Intent(context, ActivityMain.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }
}
