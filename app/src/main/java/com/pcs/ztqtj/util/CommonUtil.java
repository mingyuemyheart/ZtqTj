package com.pcs.ztqtj.util;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.pcs.ztqtj.view.activity.prove.ProveDto;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CommonUtil {

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static float dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return dpValue * scale;
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static float px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return pxValue / scale;
    }

    public static int widthPixels(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    public static int heightPixels(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    /**
     * 显示虚拟键盘
     * @param editText 输入框
     * @param context 上下文
     */
    public static void showInputSoft(EditText editText, Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        imm.showSoftInput(editText, 0);
    }

    /**
     * 隐藏虚拟键盘
     * @param editText 输入框
     * @param context 上下文
     */
    public static void hideInputSoft(EditText editText, Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    /**
     * 获取所有本地图片文件信息
     * @return
     */
    public static List<ProveDto> getAllLocalImages(Context context) {
        List<ProveDto> list = new ArrayList<>();
        if (context != null) {
            Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    null, null,null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
                    String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE));
                    String displayName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME));
                    String mimeType = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE));
                    String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                    long fileSize = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE));

                    ProveDto dto = new ProveDto();
                    dto.imgName = title;
                    dto.imgUrl = path;
                    dto.fileSize = fileSize;
                    list.add(0, dto);
                }
                cursor.close();
            }
        }
        return list;
    }

    /**
     * 格式化文件单位
     * @param size
     * @return
     */
    public static String getFormatSize(long size) {
        long kiloByte = size / 1024;
        if (kiloByte < 1) {
            return "0KB";
        }

        long megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB";
        }

        long gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB";
        }

        long teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()+ "TB";
    }

    /**
     * 广播通知相册刷新
     * @param context
     * @param file
     */
    public static void notifyAlbum(Context context, File file) {
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), file.getName(), null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
    }

    /**
     * 获取状态栏高度
     * @param context
     * @return
     */
    public static int statusBarHeight(Context context) {
        int statusBarHeight = -1;//状态栏高度
        //获取status_bar_height资源的ID
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }

    /**
     * 获取底部导航栏高度
     * @param context
     * @return
     */
    public static int navigationBarHeight(Context context) {
        int navigationBarHeight = -1;//状态栏高度
        //获取status_bar_height资源的ID
        int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            navigationBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        }
        return navigationBarHeight;
    }

    /**
     * 判断模块权限，是否可点击进入
     * @param flag
     * @return
     */
    public static boolean isCanAccess(String flag) {
        if (TextUtils.equals(flag, "0")) {
            return true;
        }
        return false;
    }

    /**
     * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的
     * @param context
     * @return true 表示开启
     */
    public static boolean isLocationOpen(final Context context) {
        LocationManager locationManager  = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || network) {
            return true;
        }
        return false;
    }

}
