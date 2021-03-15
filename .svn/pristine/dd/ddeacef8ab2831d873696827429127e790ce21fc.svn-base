package com.pcs.ztqtj.view.activity.loading;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Toast;

import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalInit;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.command.CommandBatching;
import com.pcs.ztqtj.control.command.InterCommand;
import com.pcs.ztqtj.control.inter.Callback;
import com.pcs.ztqtj.control.loading.CommandLoadingCheck;
import com.pcs.ztqtj.control.loading.CommandLoadingCity;
import com.pcs.ztqtj.control.loading.CommandLoadingGoto;
import com.pcs.ztqtj.control.loading.CommandLoadingMainData;
import com.pcs.ztqtj.control.loading.CommandLoadingReqImage;
import com.pcs.ztqtj.control.loading.CommandLoadingUnit;
import com.pcs.ztqtj.control.loading.CommandLoadingVersion;
import com.pcs.ztqtj.control.loading.CommandReqInit;
import com.pcs.ztqtj.control.tool.MyConfigure;
import com.pcs.ztqtj.control.tool.PermissionsTools;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.view.activity.FragmentActivityBase;
import com.pcs.ztqtj.view.dialog.PermissionVerifyDialog;

public class ActivityLoading extends FragmentActivityBase {

    private String[] needPermissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
    };

    private String[] nessaryPermissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
    };

    private boolean mResult = true;
    private boolean requestOnResume = false;
    private PermissionVerifyDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        dialog = new PermissionVerifyDialog(this, R.style.MyDialog);
        PackLocalInit initDown = (PackLocalInit) PcsDataManager.getInstance().getLocalPack(PackLocalInit.KEY);
        if (initDown != null && initDown.isNotFirst) {
            checkPermissions(nessaryPermissions);
        } else {
            checkPermissions(needPermissions);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(requestOnResume) {
            checkPermissions(nessaryPermissions);
            requestOnResume = false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull final String[] permissions, @NonNull int[]
            grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionsTools.PERMISSON_REQUESTCODE) {
            if(PermissionsTools.verifyNessaryPermissions(nessaryPermissions, permissions, grantResults)) {
                init();
            } else {
                String[] permissionList = PermissionsTools.getUnauthorizedNessaryPermissions(nessaryPermissions, permissions, grantResults);
                String result = "";
                for(int i = 0; i < permissionList.length; i++) {
                    String permission = permissionList[i];
                    String name = PermissionsTools.getPermissionName(permission);
                    if(i == 0) {
                        result = name;
                    } else {
                        if(!result.contains(name)) {
                            result += "、" + name;
                        }
                    }
                }
                dialog.setMessage(result);
                if(PermissionsTools.shouldShowRequestPermissionRationale(ActivityLoading.this, grantResults, permissions)) {
                    dialog.setOKButtonMessage("确定");
                    dialog.setOnConfirmListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            checkPermissions(nessaryPermissions);
                            //requestOnResume = false;
                        }
                    });

                } else {
                    dialog.setOKButtonMessage("去设置");
                    dialog.setOnConfirmListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            gotoSystemPermissionActivity();
                            //handler.sendEmptyMessageDelayed(0, 500);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    requestOnResume = true;
                                }
                            }, 1000);

                        }
                    });
                }

                dialog.show();
            }
        }
    }

    /**
     * 检查权限
     */
    private void checkPermissions(String[] permissions) {
        if (PermissionsTools.checkPermissions(this, permissions)) {
            init();
        }
    }

    private void gotoSystemPermissionActivity() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", getPackageName(), null));
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityForResult(intent, MyConfigure.REQUEST_SYSTEM_PERMISSION);
    }

    private void init() {
        if(dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        clearUserInfo();
        createImageFetcher();
        ZtqCityDB.getInstance().initnew(this, new Callback() {
            @Override
            public void onCallback() {
                CommandBatching batchingLoading = new CommandBatching();
                // 超时20秒
                batchingLoading.setOverTime(40000);
                // 最少2秒
                batchingLoading.setMinTime(2000);
                // 顺序执行
                batchingLoading.setQueueExecute(true);
                //监听
                batchingLoading.addListener(mLoadingListener);
                //检查初始化
                CommandLoadingCheck commandCheck = new CommandLoadingCheck(getApplicationContext());
                batchingLoading.addCommand(commandCheck);

                // 城市
                CommandLoadingCity commandCity = new CommandLoadingCity(ActivityLoading.this);
                batchingLoading.addCommand(commandCity);
                // 单位
                CommandLoadingUnit commandUnit = new CommandLoadingUnit(ActivityLoading.this);
                batchingLoading.addCommand(commandUnit);
                // 版本
                CommandLoadingVersion commandVersion = new CommandLoadingVersion(ActivityLoading.this);
                batchingLoading.addCommand(commandVersion);
                // 初始化
                CommandReqInit commandInit = new CommandReqInit(ActivityLoading.this);
                batchingLoading.addCommand(commandInit);
                //首页数据
                CommandLoadingMainData commandMainData = new CommandLoadingMainData();
                batchingLoading.addCommand(commandMainData);
                //主题插图
                CommandLoadingReqImage commandReqImage = new CommandLoadingReqImage(ActivityLoading.this);
                batchingLoading.addCommand(commandReqImage);
                // 执行
                batchingLoading.execute();
            }
        });
    }

    private void clearUserInfo() {
        SharedPreferences shared = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        boolean first = shared.getBoolean("first", true);
        if(first) {
            if(ZtqCityDB.getInstance().isLoginService()) {
                ZtqCityDB.getInstance().removeMyInfo();
            }
            SharedPreferences.Editor editor = shared.edit();
            editor.putBoolean("first", false);
            editor.commit();
        }

    }

    private InterCommand.InterCommandListener mLoadingListener = new InterCommand.InterCommandListener() {
        @Override
        public void done(InterCommand.Status status) {
            if (status == InterCommand.Status.FAIL) {
                Toast.makeText(ActivityLoading.this, R.string.init_error, Toast.LENGTH_SHORT).show();
                return;
            }
//            changeCityName();
            // 跳转
            CommandLoadingGoto commandGoto = new CommandLoadingGoto(ActivityLoading.this, getImageFetcher());
            commandGoto.execute();
        }
    };

}
