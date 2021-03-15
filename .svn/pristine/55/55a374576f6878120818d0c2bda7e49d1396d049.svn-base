package com.pcs.ztqtj.control.tool;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.NotificationManagerCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.view.dialog.DialogFactory;
import com.pcs.ztqtj.view.dialog.DialogTwoButton;

/**
 * Created by tyaathome on 2018/1/10.
 */

public class NotificationPermissionUtils {

    private static DialogTwoButton myDialog;

    /**
     * 检查通知栏权限设置
     */
    public static void checkNotificationPermission(final Context context) {
        if(!NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            if(myDialog == null) {
                View view = LayoutInflater.from(context).inflate(R.layout.dialog_message, null);
                TextView tv = (TextView) view.findViewById(R.id.dialogmessage);
                tv.setText("你通知接受功能尚未开通，请到本机\"设置->通知管理->天津气象\"的管理界面开启相应服务。");
                tv.setGravity(Gravity.CENTER);
                myDialog = new DialogTwoButton(context, view, "知道了", "去设置", new DialogFactory.DialogListener() {
                    @Override
                    public void click(String str) {
                        switch (str) {
                            case "知道了":
                                myDialog.dismiss();
                                break;
                            case "去设置":
                                gotoNotificaiontSettings(context);
                                break;
                        }
                        myDialog.dismiss();
                    }
                });
                myDialog.show();
            }else{
                myDialog.show();
            }
        }

    }

    /**
     * 跳转通知栏设置页面
     */
    private static void gotoNotificaiontSettings(Context context) {
        Intent intent = new Intent();
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        context.startActivity(intent);
    }

}
