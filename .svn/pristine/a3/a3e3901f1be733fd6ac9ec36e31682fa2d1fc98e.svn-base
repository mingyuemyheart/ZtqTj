package com.pcs.ztqtj.control.tool;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import java.util.List;


public class AppTool {
	/**
	 * 打开app,打开launch
	 * 
	 * @param packName
	 *            包名路径
	 */
	public static void openApp(Context context, String packName) {
		if (context == null || TextUtils.isEmpty(packName)) {

		} else {
			Intent intent = context.getPackageManager().getLaunchIntentForPackage(packName);
			context.startActivity(intent);
		}
	}
	/**
	 * 
	 * 知道包名和具体类名启动app
	 * 
	 * @param packPath
	 *            报名
	 * @param lunachActivity
	 *            启动类
	 */
	public static void openApp(Context context, String packPath, String lunachActivity) {
		ComponentName com = new ComponentName(packPath, packPath + "." + lunachActivity);
		Intent intent = new Intent();
		// 设置部件
		intent.setComponent(com);
		context.startActivity(intent);
	}
	/**
	 * 是否已经安装
	 * 
	 * @param packPath
	 * @return
	 */
	public static boolean isInstalled(Context context, String packPath) {
		boolean has = false;
		if (TextUtils.isEmpty(packPath) || context == null) {
			return has;
		}
		PackageManager packageManager = context.getPackageManager();// 获取packagemanager
		List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
		if (pinfo != null) {
			for (int i = 0; i < pinfo.size(); i++) {
				if (pinfo.get(i).packageName.equals(packPath)) {
					has = true;
					break;
				}
			}
		}
		return has;
	}
	
}
