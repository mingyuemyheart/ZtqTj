package com.pcs.ztqtj.control.tool;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.view.dialog.PermissionVerifyDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tyaathome on 2017/12/11.
 */

public class PermissionsTools {

    public static final int PERMISSON_REQUESTCODE = 0;

    public static boolean checkPermissions(Activity activity, String[] needPermissions) {
        if (Build.VERSION.SDK_INT >= 23
                && activity.getApplicationInfo().targetSdkVersion >= 23) {
            List<String> needRequestPermissonList = findDeniedPermissions(activity, needPermissions);
            if(needRequestPermissonList == null || needRequestPermissonList.size() == 0) {
                return true;
            }
            String[] array = needRequestPermissonList.toArray(new String[needRequestPermissonList.size()]);
            ActivityCompat.requestPermissions(activity, array, PERMISSON_REQUESTCODE);
            return false;
        }
        return true;
    }

    public static boolean checkPermissions(Activity activity, String[] needPermissions, int requestCode) {
        if (Build.VERSION.SDK_INT >= 23
                && activity.getApplicationInfo().targetSdkVersion >= 23) {
            List<String> needRequestPermissonList = findDeniedPermissions(activity, needPermissions);
            if(needRequestPermissonList == null || needRequestPermissonList.size() == 0) {
                return true;
            }
            String[] array = needRequestPermissonList.toArray(new String[needRequestPermissonList.size()]);
            ActivityCompat.requestPermissions(activity, array, requestCode);
            return false;
        }
        return true;
    }

    public static boolean checkPermissions(Fragment fragment, String[] needPermissions, int requestCode) {
        Activity activity = fragment.getActivity();
        if (Build.VERSION.SDK_INT >= 23
                && activity.getApplicationInfo().targetSdkVersion >= 23) {
            List<String> needRequestPermissonList = findDeniedPermissions(activity, needPermissions);
            if(needRequestPermissonList == null || needRequestPermissonList.size() == 0) {
                return true;
            }
            String[] array = needRequestPermissonList.toArray(new String[needRequestPermissonList.size()]);
            fragment.requestPermissions(array, requestCode);
            return false;
        }
        return true;
    }

    /**
     * 获取权限集中需要申请权限的列表
     *
     * @param permissions 权限
     * @return 获取权限集中需要申请权限的列表
     * @since 2.5.0
     */
    private static List<String> findDeniedPermissions(Activity activity, String[] permissions) {
        List<String> needRequestPermissonList = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= 23
                && activity.getApplicationInfo().targetSdkVersion >= 23) {
            for (String perm : permissions) {
                if (ContextCompat.checkSelfPermission(activity, perm) != PackageManager.PERMISSION_GRANTED) {
                    needRequestPermissonList.add(perm);
                }
            }
        }
        return needRequestPermissonList;
    }

    public static boolean verifyCameraPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= 23
                && activity.getApplicationInfo().targetSdkVersion >= 23) {
            return ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    /**
     * 检测是否所有的权限都已经授权
     *
     * @param grantResults 权限
     * @return 检测是否所有的权限都已经授权
     * @since 2.5.0
     */
    public static boolean verifyPermissions(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检测是否必要的权限都已授权
     * @param nessaryPermissions 必要权限
     * @param permissions 申请授权的权限
     * @param grantResults 权限申请结果
     * @return 检测是否必要的权限都已授权
     */
    public static boolean verifyNessaryPermissions(String[] nessaryPermissions, String[] permissions, int[] grantResults) {
        for(String nPermission : nessaryPermissions) {
            for(int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                if(nPermission.equals(permission) && grantResults.length > i) {
                    int result = grantResults[i];
                    if(result == PackageManager.PERMISSION_GRANTED) {
                        break;
                    } else {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * 获取未授权的必要权限
     * @param nessaryPermissions
     * @param permissions
     * @param grantResults
     * @return
     */
    public static String[] getUnauthorizedNessaryPermissions(String[] nessaryPermissions, String[] permissions, int[] grantResults) {
        List<String> permissionResultList = new ArrayList<>();
        for(String nPermission : nessaryPermissions) {
            for(int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                if(nPermission.equals(permission) && grantResults.length > i) {
                    int result = grantResults[i];
                    if(result != PackageManager.PERMISSION_GRANTED) {
                        permissionResultList.add(permission);
                    }
                }
            }
        }
        String[] resultArray = new String[permissionResultList.size()];
        return permissionResultList.toArray(resultArray);
    }

    public static String getPermissionName(String permission) {
        switch (permission) {
            case Manifest.permission.ACCESS_COARSE_LOCATION:
                return "位置信息";
            case Manifest.permission.ACCESS_FINE_LOCATION:
                return "位置信息";
            case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                return "存储空间";
            case Manifest.permission.READ_EXTERNAL_STORAGE:
                return "存储空间";
            case Manifest.permission.READ_PHONE_STATE:
                return "设备信息";
            case Manifest.permission.CAMERA:
                return "相机";
            case Manifest.permission.RECORD_AUDIO:
                return "麦克风";
            default:
                return "";
        }
    }

    /**
     * 启动应用的设置
     *
     * @since 2.5.0
     */
    protected void startAppSettings(Activity activity) {
        Intent intent = new Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + activity.getPackageName()));
        activity.startActivity(intent);
    }

    public static boolean shouldShowRequestPermissionRationale(Activity activity, int[] grantResults, String[] permissions) {
        for(int i = 0; i < permissions.length; i++) {
            String permission = permissions[i];
            int result = grantResults[i];
            if(result == PackageManager.PERMISSION_DENIED && !ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                return false;
            }
        }
        return true;
    }

    /**
     * activity请求权限之后回调的代理
     * @param activity
     * @param permissions
     * @param grantResults
     * @param callback
     */
    public static void onRequestPermissionsResult(final Activity activity, @NonNull String[] permissions
            , @NonNull int[] grantResults, final RequestPermissionResultCallback callback) {
        if(verifyPermissions(grantResults)) {
            if(callback != null) {
                callback.onSuccess();
            }
        } else {
            if(shouldShowRequestPermissionRationale(activity, grantResults, permissions)) {
                if(callback != null) {
                    callback.onDeny();
                }
            } else {
                String[] permissionList = PermissionsTools.getUnauthorizedNessaryPermissions(permissions, permissions, grantResults);
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
                final PermissionVerifyDialog dialog = new PermissionVerifyDialog(activity, R.style.MyDialog);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setMessage(result);
                dialog.setOKButtonMessage("去设置");
                dialog.setOnConfirmListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        gotoSystemPermissionActivity(activity);
                        if(callback != null) {
                            callback.onDenyNeverAsk();
                            dialog.dismiss();
                        }
                    }
                });
                dialog.show();
            }
        }
    }

    public static void gotoSystemPermissionActivity(Activity activity) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", activity.getPackageName(), null));
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    public interface RequestPermissionResultCallback {
        // 请求成功
        void onSuccess();
        // 请求拒绝
        void onDeny();
        // 请求拒绝并不再询问
        void onDenyNeverAsk();
    }

}
