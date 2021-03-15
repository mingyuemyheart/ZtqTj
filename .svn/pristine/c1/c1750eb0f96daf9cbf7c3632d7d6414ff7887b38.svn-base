package com.pcs.ztqtj.control.tool;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.view.activity.help.ActivityHelp;
import com.pcs.ztqtj.view.activity.service.AcitvityServeLogin;
import com.pcs.ztqtj.view.dialog.DialogFactory;
import com.pcs.ztqtj.view.dialog.DialogTwoButton;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalUser;
import com.pcs.lib_ztqfj_v2.model.pack.net.service.PackServiceLoginQueryDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.service.PackServiceLoginQueryUp;

/**
 * 气象服务登录工具类
 * Created by tyaathome on 2016/8/24.
 */
public class ServiceLoginTool {

    private static ServiceLoginTool instance;
    private DialogTwoButton dialog;
    private PackServiceLoginQueryUp loginQueryUp = new PackServiceLoginQueryUp();
    private PackServiceLoginQueryDown loginQueryDown = new PackServiceLoginQueryDown();

    public static final int SERVICE_RESULT = 103;

    public ServiceLoginTool() {

    }

    public static ServiceLoginTool getInstance() {
        if(instance == null) {
            instance = new ServiceLoginTool();
        }
        return instance;
    }

    /**
     * 显示已经登录的对话框(需要传入回调)
     * @param context
     * @param listener
     */
    public void createAlreadyLogined(Context context, final DialogClickListener listener) {
        // 删除当前气象服务用户信息
        ZtqCityDB.getInstance().removeMyInfo();
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_message, null);
        TextView tv = (TextView) view.findViewById(R.id.dialogmessage);
        tv.setText(context.getResources().getString(R.string.promess_service_intro));
        dialog = new DialogTwoButton(context, view, "帮助", "登录", new DialogFactory.DialogListener() {

            @Override
            public void click(String str) {
                dialog.dismiss();
                if (listener != null) {
                    if (str.equals("帮助")) {
                        listener.onPositive();
                    } else {
                        listener.onNegative();
                    }
                }
            }
        });
        dialog.setCloseBtnListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.showCloseBtn();
        dialog.show();
    }

    /**
     * 显示已经登录的对话框(默认回调)
     * @param context
     */
    public void createAlreadyLogined(final Context context) {
        createAlreadyLogined(context, new DialogClickListener() {
            @Override
            public void onPositive() {
                Intent intent = new Intent(context, ActivityHelp.class);
                context.startActivity(intent);
            }

            @Override
            public void onNegative() {
                Intent intent = new Intent(context, AcitvityServeLogin.class);
                Activity activity = (Activity) context;
                activity.startActivityForResult(intent, SERVICE_RESULT);
            }
        });
    }

    public void createAlreadyLoginedWithFragment(final Activity activity, final Fragment fragment) {
        createAlreadyLogined(activity, new DialogClickListener() {
            @Override
            public void onPositive() {
                Intent intent = new Intent(activity, ActivityHelp.class);
                activity.startActivity(intent);
            }

            @Override
            public void onNegative() {
                Intent intent = new Intent(activity, AcitvityServeLogin.class);
                fragment.startActivityForResult(intent, SERVICE_RESULT);
            }
        });
    }

    /**
     * 请求登录状态查询
     */
    public void reqLoginQuery() {
        PackLocalUser info = ZtqCityDB.getInstance().getMyInfo();
        if(info == null) {
            return;
        }
        loginQueryUp = new PackServiceLoginQueryUp();
        loginQueryUp.user_id = info.user_id;
        PcsDataDownload.addDownload(loginQueryUp);
    }

    /**
     * 请求回调
     * @param packName
     * @param listener
     */
    public void callback(String packName, CheckListener listener) {
        if(loginQueryUp.getName().equals(packName)) {
            loginQueryDown = (PackServiceLoginQueryDown) PcsDataManager.getInstance().getNetPack(packName);
            PcsDataDownload.removeDownload(packName);
            if(loginQueryDown != null && loginQueryDown.status.equals("1")) {
                if(listener != null) {
                    listener.onSuccess();
                }
                return;
            }
            if(listener != null) {
                listener.onFail();
            }
        }
    }

    public PackServiceLoginQueryUp getPackUp() {
        return loginQueryUp;
    }

    public PackServiceLoginQueryDown getPackDown() {
        return loginQueryDown;
    }

    /**
     * 登录状态回调接口
     */
    public interface CheckListener {
        void onSuccess();
        void onFail();
    }

    /**
     * 对话框按钮点击回调接口
     */
    public interface DialogClickListener {
        void onPositive();
        void onNegative();
    }

}
