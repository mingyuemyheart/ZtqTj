package com.pcs.ztqtj.control.tool;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.pcs.lib.lib_pcs_v3.PcsInit;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommUtils {

	public static int getScreenHeight(Context c) {
		WindowManager windowManager = (WindowManager) c
				.getSystemService(Context.WINDOW_SERVICE);
		return windowManager.getDefaultDisplay().getHeight();// 获得高度，获得宽度也类似
	}

	public static int getScreenWidth(Context c) {
		WindowManager windowManager = (WindowManager) c
				.getSystemService(Context.WINDOW_SERVICE);
		return windowManager.getDefaultDisplay().getWidth();// 获得宽度，获得高度也类似
	}

	// dip值转换到px值
	public static int Dip2Px(Context c, int dipValue) {
		float scale = c.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	public static int px2dp(Context c, float pxValue) {
		final float scale = c.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 判断用户密码是否符合规则: 密码格式为数字+字母区分大小；长度大于6小于16位；
	 * 
	 * @param passWord
	 * @return
	 */
	public static boolean checkPassWord(String passWord) {
		// ^ 匹配一行的开头位置
		// (?![0-9]+$) 预测该位置后面不全是数字
		// (?![a-zA-Z]+$) 预测该位置后面不全是字母
		// [0-9A-Za-z] {7,15} 由7-15位数字或这字母组成
		// $ 匹配行结尾位置
		return passWord.matches("^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,16}$");
	}

	/**
	 * 判断昵称是否符合规则:可以显示数字也可以显示数字+字母，也可以是汉字；昵称屏蔽字词“操、傻B、妈逼、干你妈、fuck”
	 * 
	 * @param userName
	 * @return
	 */
	public static boolean checkUserName(String userName) {
		return userName.matches("^[a-zA-Z0-9\u4e00-\u9fa5]+$");
	}

	/**
	 * 判断是否输入的字符串是否数值(正负数0)
	 * 
	 * @param num
	 * @return
	 */
	public static boolean checkNum(String num) {

		return num.matches("^(-)?[0-9][0-9]*$");
	}

	/**
	 * 判断是否输入的字符串是否数值(包含小数) 不判断负数
	 * 
	 * @param num
	 * @return
	 */
	public static boolean checkIsNum(String num) {
		String reg = "\\d+(\\.\\d+)?";
		return num.matches(reg);
	}

	/**
	 * 判断账号是否符合规则:账号格式必须是邮箱格式
	 * 
	 * @param email
	 * @return
	 */
	public static boolean checkEmail(String email) {

		return email
				.matches("^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$");
	}

	/**
	 * 验证字符串是否包含中文
	 * 
	 * @param s
	 * @return
	 */
	public static boolean checkChinese(String s) {
		s = new String(s.getBytes());// 用GBK编码
		String pattern = "[\u4e00-\u9fa5]+";
		Pattern p = Pattern.compile(pattern);
		Matcher result = p.matcher(s);
		return result.find(); // 是否含有中文字符
	}

	/**
	 * 验证字符串是否全部为中文
	 * 
	 * @param s
	 * @return
	 */
	public static boolean checkAllChinese(String s) {
		s = new String(s.getBytes());// 用GBK编码
		String pattern = "[\u4e00-\u9fa5]+";
		Pattern p = Pattern.compile(pattern);
		Matcher result = p.matcher(s);
		return result.matches();// 全部为中文
	}

	/**
	 * List<String> 转 String []
	 * 
	 * @param data
	 * @return String []
	 */
	public static String[] ListToString(List<String> data) {
		int size = data.size();
		String[] str = new String[size];

		for (int i = 0; i < size; i++) {
			str[i] = data.get(i);
		}
		return str;
	}

	/**
	 * String [] 转 List<String>
	 * 
	 * @param data
	 * @return
	 */
	public static ArrayList<String> StringToList(String[] data) {
		ArrayList<String> dataList = new ArrayList<String>();
		for (int i = 0; i < data.length; i++) {
			dataList.add(data[i]);
		}
		return dataList;
	}

	/**
	 * 截取list
	 * 
	 * @param data
	 * @param num
	 * @return
	 */
	public static ArrayList<String> updateListData(ArrayList<String> data,
			int num) {
		ArrayList<String> dataList = new ArrayList<String>();
		for (int i = 0; i < num; i++) {
			dataList.add(data.get(i));
		}
		return dataList;
	}

	/**
	 * 文件转化为字节数组
	 * 
	 */
	public static byte[] getBytesFromFile(File f) {
		if (f == null) {
			return null;
		}
		try {
			FileInputStream stream = new FileInputStream(f);
			ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
			byte[] b = new byte[1000];
			int n;
			while ((n = stream.read(b)) != -1)
				out.write(b, 0, n);
			stream.close();
			out.close();
			return out.toByteArray();
		} catch (IOException e) {
		}
		return null;
	}

	/**
	 * 获取文件名
	 * 
	 * @param path
	 * @return
	 */
	public static String getFileName(String path) {

		String str = path.substring(path.lastIndexOf("/") + 1, path.length());

		return str;
	}

	/**
	 * 把bitmap保存到本地SD卡
	 * 
	 * @param imageDir
	 *            图片保存路径
	 * @param photoName
	 *            图片名称
	 * @param photoBitmap
	 *            要保存到本地的Bitmap
	 */
	public static boolean savePhotoToSD(String imageDir, String photoName,
			Bitmap photoBitmap) {

		File path = new File(imageDir);
		if (!path.exists()) {
			path.mkdirs();
		}

		File photoFile = new File(imageDir, photoName);

		FileOutputStream fileOutputStream = null;

		try {
			fileOutputStream = new FileOutputStream(photoFile);
			if (photoBitmap != null) {
				if (photoBitmap.compress(Bitmap.CompressFormat.JPEG, 100,
						fileOutputStream)) {
					fileOutputStream.flush();
					fileOutputStream.close();
					return true;
				}

			}
		} catch (FileNotFoundException e) {
			photoFile.delete();
			// e.printStackTrace();
			return false;
		} catch (IOException e) {
			photoFile.delete();
			// e.printStackTrace();
			return false;
		} finally {
			try {
				fileOutputStream.close();
			} catch (IOException e) {
				// e.printStackTrace();
				return false;
			}
		}
		return false;
	}

	public static String getStorage() {

		String PATH_BASE = null;

		if (PATH_BASE == null) {
			PATH_BASE = Environment.getExternalStorageDirectory() + "/";
			File file = new File(PATH_BASE);
			if (!file.canWrite()) {
				PATH_BASE = Environment.getExternalStorageDirectory() + "0/";
				file = new File(PATH_BASE);
			}
			if (!file.canWrite()) {
				PATH_BASE = Environment.getExternalStorageDirectory() + "1/";
				file = new File(PATH_BASE);
			}
			if (!file.canWrite()) {
				PATH_BASE = Environment.getExternalStorageDirectory() + "2/";
				file = new File(PATH_BASE);
			}

			if (!file.canWrite()) {
				PATH_BASE = "/sdcard/";
				file = new File(PATH_BASE);
			}
			if (!file.canWrite()) {
				PATH_BASE = "/sdcard0/";
				file = new File(PATH_BASE);
			}
			if (!file.canWrite()) {
				PATH_BASE = "/sdcard1/";
				file = new File(PATH_BASE);
			}
			if (!file.canWrite()) {
				PATH_BASE = "/sdcard2/";
				file = new File(PATH_BASE);
			}

			if (!file.canWrite()) {
				PATH_BASE = "/storage/sdcard/";
				file = new File(PATH_BASE);
			}
			if (!file.canWrite()) {
				PATH_BASE = "/storage/sdcard0/";
				file = new File(PATH_BASE);
			}
			if (!file.canWrite()) {
				PATH_BASE = "/storage/sdcard1/";
				file = new File(PATH_BASE);
			}
			if (!file.canWrite()) {
				PATH_BASE = "/storage/sdcard2/";
				file = new File(PATH_BASE);
			}

		}

		return PATH_BASE;
	}

	/**
	 * 日期转换成字符串
	 * 
	 * @param date
	 * @return str
	 */
	public static String DateToStr(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String str = format.format(date);
		return str;
	}

	/**
	 * 字符串转换成日期
	 * 
	 * @param str
	 * @return date
	 */
	public static Date StrToDate(String str) {

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		try {
			date = format.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * 字符串转换成日期: 08/25 19:02
	 * 
	 * @param str
	 * @return date
	 */
	public static Date StrToDate2(String str) {

		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm");
		Date date = null;
		try {
			date = format.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * 日期转换成字符串
	 * 
	 * @param date
	 * @return str
	 */
	public static String DateToStr2(Date date) {
	    if(date != null) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
            return format.format(date);
        }
		return "";
	}

	/**
	 * 判断是否MIUI
	 * 
	 * @return
	 */
	public static boolean isMIUI() {
		String propName = "ro.miui.ui.version.name";
		String line;
		BufferedReader input = null;
		try {
			Process p = Runtime.getRuntime().exec("getprop " + propName);
			input = new BufferedReader(
					new InputStreamReader(p.getInputStream()), 1024);
			line = input.readLine();
			input.close();
		} catch (IOException ex) {
			return false;
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
				}
			}
		}
		if (TextUtils.isEmpty(line)) {
			// 不是MIUI
			return false;
		}

		return true;
	}

    /**
     * 收起键盘
     */
    public static void closeKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive() && activity.getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 收起键盘
     */
    public static void closeKeyboard(Activity activity, View view) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive() && view != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 判断应用是否已经启动
     * @param context 一个context
     * @param packageName 要判断应用的包名
     * @return boolean
     */
    public static boolean isAppAlive(Context context, String packageName){
        ActivityManager activityManager =
                (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processInfos
                = activityManager.getRunningAppProcesses();
        for(ActivityManager.RunningAppProcessInfo info : processInfos){
            if(info.processName.equals(packageName)){
                return true;
            }
        }
        return false;
    }

    public static void ringtone(Context context) {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(context, notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void openCamera(Activity activity, File file, int requestCode) {
        String[] nessaryPermissions = {
                Manifest.permission.CAMERA
        };
        if (PermissionsTools.checkPermissions(activity, nessaryPermissions, requestCode)) {
            if (Build.VERSION.SDK_INT < 24) {
                Uri uri = Uri.fromFile(file);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                activity.startActivityForResult(intent, requestCode);
            } else {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Uri photoURI = FileProvider.getUriForFile(activity, "com.pcs.ztqtj.provider",
                        file);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                activity.startActivityForResult(intent, requestCode);
            }
        }
    }

    public static void openCamera(Fragment fragment, File file, int requestCode) {
        String[] nessaryPermissions = {
                Manifest.permission.CAMERA
        };
        if (PermissionsTools.checkPermissions(fragment, nessaryPermissions, requestCode)) {
            if (Build.VERSION.SDK_INT < 24) {
                Uri uri = Uri.fromFile(file);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                fragment.startActivityForResult(intent, requestCode);
            } else {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Uri photoURI = FileProvider.getUriForFile(fragment.getActivity(), "com.pcs.ztqtj.provider",
                        file);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                fragment.startActivityForResult(intent, requestCode);
            }
        }
    }

    public static void openVideoCamera(Fragment fragment, int requestCode, int time) {
        String[] nessaryPermissions = {
                Manifest.permission.CAMERA
        };
        if (PermissionsTools.checkPermissions(fragment, nessaryPermissions, requestCode)) {
            if (Build.VERSION.SDK_INT < 24) {
                Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);// 设置视频的质量，值为0-1，
                intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, time);// 设置视频的录制长度，s为单位
                fragment.startActivityForResult(intent, requestCode);
            } else {
                Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);// 设置视频的质量，值为0-1，
                intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, time);// 设置视频的录制长度，s为单位
                fragment.startActivityForResult(intent, requestCode);
            }
        }
    }

    /**
     * 如果是APK则打开
     *
     * @param file
     */
    public static void openIfAPK(File file) {
        if (!file.getName().toLowerCase().endsWith(".apk")) {
            return;
        }
        if(Build.VERSION.SDK_INT < 24) {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(android.content.Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            PcsInit.getInstance().getContext().startActivity(intent);
        } else {
            Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri uri = FileProvider.getUriForFile(PcsInit.getInstance().getContext(),
                    "com.pcs.ztqtj.provider", file);
            intent.setData(uri);
            PcsInit.getInstance().getContext().startActivity(intent);
        }
    }

    public static int dp2px(float value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, Resources.getSystem().getDisplayMetrics());
    }

}
