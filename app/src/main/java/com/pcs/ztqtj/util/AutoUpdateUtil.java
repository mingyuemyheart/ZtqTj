package com.pcs.ztqtj.util;

import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.pcs.ztqtj.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 自动更新
 */
public class AutoUpdateUtil {

	/**
	 * 获取版本号
	 * @return 当前应用的版本号
	 */
	private static String getVersionName(Context context) {
	    try {
	        PackageManager manager = context.getPackageManager();
	        PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
	        return info.versionName;
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return null;
	}

	/**
	 * 检查更新
	 * @param context
	 */
	public static void checkUpdate(final Activity activity, final Context context, final boolean flag) {
		final String versionName = getVersionName(context);
		new Thread(new Runnable() {
			@Override
			public void run() {
				String url = String.format("https://tjhm-app.weather.com.cn/rest/v/TJQX2021-%s-official-android", versionName);
				Log.e("checkUpdate", url);
				OkHttpUtil.enqueue(new okhttp3.Request.Builder().url(url).build(), new Callback() {
					@Override
					public void onFailure(Call call, IOException e) {
					}
					@Override
					public void onResponse(Call call, Response response) throws IOException {
						if (!response.isSuccessful()) {
							return;
						}
						final String result = response.body().string();
						activity.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (!TextUtils.isEmpty(result)) {
									try {
										JSONObject object = new JSONObject(result);
										if (!object.isNull("code")) {
											String code = object.getString("code");
											if (TextUtils.equals(code, "200")) {
												if (!object.isNull("data")) {
													JSONObject obj = object.getJSONObject("data");
													UpdateDto dto = new UpdateDto();
													if (!obj.isNull("version")) {
														dto.version = obj.getString("version");
													}
													if (!obj.isNull("description")) {
														dto.description = obj.getString("description");
													}
													if (!obj.isNull("downloadUrl")) {
														dto.downloadUrl = obj.getString("downloadUrl");
													}

													int ver = 0;
													int currentVer = 0;
													if (dto.version != null && dto.version.contains(".")) {
														String version = dto.version.replace(".", "");
														if (!TextUtils.isEmpty(version)) {
															ver = Integer.parseInt(version);
														}
													}
													if (versionName != null && versionName.contains(".")) {
														String version = versionName.replace(".", "");
														if (!TextUtils.isEmpty(version)) {
															currentVer = Integer.parseInt(version);
														}
													}
													//检查版本不一样时候才更新
													if (ver > currentVer) {
														updateDialog(context, dto);
													} else {
														if (!flag) {
															Toast.makeText(context, "已经是最新版本", Toast.LENGTH_SHORT).show();
														}
													}
												}
											}
										}
									} catch (JSONException e) {
										e.printStackTrace();
									}
								}
							}
						});
					}
				});
			}
		}).start();
	}
	
	private static class UpdateDto {
		private String version = "";
		private String description = "";
		private String downloadUrl = "";
	}

	private static void updateDialog(final Context context, final UpdateDto dto) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.dialog_update, null);
		TextView tvVersion = view.findViewById(R.id.tvVersion);
		TextView tvContent = view.findViewById(R.id.tvContent);
		TextView tvNegtive = view.findViewById(R.id.tvNegtive);
		TextView tvPositive = view.findViewById(R.id.tvPositive);

		final Dialog dialog = new Dialog(context, R.style.CustomProgressDialog);
		dialog.setContentView(view);
		dialog.show();

		if (!TextUtils.isEmpty(dto.version)) {
			tvVersion.setText("更新版本："+dto.version);
		}
		if (!TextUtils.isEmpty(dto.description)) {
			tvContent.setText(dto.description);
		}
		tvNegtive.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});
		tvPositive.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
				if (!TextUtils.isEmpty(dto.downloadUrl)) {
					new Thread() {
						public void run() {
							intoDownloadManager(context, dto.downloadUrl);
						}
					}.start();
				}
			}
		});
	}

	private static void intoDownloadManager(Context context, String downloadUrl) {
		DownloadManager dManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
		Uri uri = Uri.parse(downloadUrl);
		Request request = new Request(uri);
		// 设置下载路径和文件名
		String filename = downloadUrl.substring(downloadUrl.lastIndexOf("/") + 1);//获取文件名称
		String filePath = Environment.getExternalStorageDirectory()+"/"+Environment.DIRECTORY_DOWNLOADS+"/"+filename;
		request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);
		String appName = context.getString(R.string.app_name);
		request.setTitle(appName);
		request.setDescription(appName);
		request.setNotificationVisibility(Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
		request.setMimeType("application/vnd.android.package-archive");
		// 设置为可被媒体扫描器找到
		request.allowScanningByMediaScanner();
		// 设置为可见和可管理
		request.setVisibleInDownloadsUi(true);
		long referneceId = dManager.enqueue(request);
//		// 把当前下载的ID保存起来
		SharedPreferences sPreferences = context.getSharedPreferences("downloadplato", 0);
		sPreferences.edit().putLong("plato", referneceId).apply();
		initBroadCast(context, referneceId, filePath);
	}

	/**
	 * 注册广播监听系统的下载完成事件
	 */
	private static void initBroadCast(Context context, final long referneceId, final String filePath) {
		BroadcastReceiver mReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
				if (id == referneceId) {
					intent = new Intent(Intent.ACTION_VIEW);
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
						intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
						Uri contentUri = FileProvider.getUriForFile(context, context.getPackageName()+".FileProvider", new File(filePath));
						intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
					} else {
						intent.setDataAndType(Uri.fromFile(new File(filePath)), "application/vnd.android.package-archive");
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					}
					context.startActivity(intent);
				}

			}
		};
		IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
		context.registerReceiver(mReceiver, intentFilter);
	}
	
}
