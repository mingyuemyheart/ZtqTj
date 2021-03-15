package com.pcs.ztqtj.control.tool;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalInit;
import com.pcs.lib_ztqfj_v2.model.pack.net.push.PushTag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocalDataHelper {

    private static final String KEY_PUSH_DIALOG_RINGTONE = "key_push_dialog_ringtone";

	/**
	 * 
	 * @param ctx
	 * @param PREF_NAME
	 * @return
	 */
	public static SharedPreferences getSharedPreferences(Context ctx, String PREF_NAME) {
		SharedPreferences mSettings = ctx.getSharedPreferences(PREF_NAME, 0);
		return mSettings;

	}

	/**
	 * 
	 * @param ctx
	 * @param PREF_NAME
	 * @param mode
	 * @return
	 */
	public static SharedPreferences getSharedPreferences(Context ctx, String PREF_NAME, int mode) {
		SharedPreferences mSettings = ctx.getSharedPreferences(PREF_NAME, mode);
		return mSettings;

	}

	/**
	 * 
	 * @param ctx
	 * @param PREF_NAME
	 * @return
	 */
	public static Editor getEditor(Context ctx, String PREF_NAME) {
		SharedPreferences mSettings = ctx.getSharedPreferences(PREF_NAME, 0);
		Editor editor = mSettings.edit();
		return editor;

	}

	/**
	 * 
	 * @param ctx
	 * @param PREF_NAME
	 * @param mode
	 * @return
	 */
	public static Editor getEditor(Context ctx, String PREF_NAME, int mode) {
		SharedPreferences mSettings = ctx.getSharedPreferences(PREF_NAME, mode);
		Editor editor = mSettings.edit();
		return editor;

	}

	/**
	 * 保存key到配制文件
	 * 
	 * @param ctx
	 * @param PREF_NAME
	 * @param key
	 * @param value
	 */
	public static void saveKey(Context ctx, String PREF_NAME, String key, String value) {
		SharedPreferences mSettings = ctx.getSharedPreferences(PREF_NAME, 0);
		Editor mEditor = mSettings.edit();
		mEditor.putString(key, value);
		mEditor.commit();
	}

	public static void saveBooleanKey(Context ctx, String PREF_NAME, String key, boolean value) {
		SharedPreferences mSettings = ctx.getSharedPreferences(PREF_NAME, 0);
		Editor mEditor = mSettings.edit();
		mEditor.putBoolean(key, value);
		mEditor.commit();
	}

	/**
	 * 保存int到 PREF_NAME文件中
	 * 
	 * @param ctx
	 * @param PREF_NAME
	 * @param key
	 * @param value
	 */
	public static void saveIntKey(Context ctx, String PREF_NAME, String key, int value) {
		SharedPreferences mSettings = ctx.getSharedPreferences(PREF_NAME, 0);
		Editor mEditor = mSettings.edit();
		mEditor.putInt(key, value);
		mEditor.commit();
	}

	/**
	 * 从 PREF_NAME文件中取出int值
	 * 
	 * @param ctx
	 * @param PREF_NAME
	 * @param key
	 * @param defValue
	 * @return
	 */
	public static int loadIntKey(Context ctx, String PREF_NAME, String key, int defValue) {
		SharedPreferences mSettings = ctx.getSharedPreferences(PREF_NAME, 0);
		return mSettings.getInt(key, defValue);
	}

	/**
	 * 从配制文件取出key
	 * 
	 * @param ctx
	 * @param PREF_NAME
	 * @param key
	 * @return
	 */
	public static String loadKey(Context ctx, String PREF_NAME, String key) {
		SharedPreferences mSettings = ctx.getSharedPreferences(PREF_NAME, 0);
		return mSettings.getString(key, "");
	}

	public static boolean loadBooleanKey(Context ctx, String PREF_NAME, String key) {
		SharedPreferences mSettings = ctx.getSharedPreferences(PREF_NAME, 0);
		return mSettings.getBoolean(key, false);
	}

	/**
	 * 从配制文件删除key
	 * 
	 * @param ctx
	 * @param PREF_NAME
	 * @param key
	 */
	public static void removeKey(Context ctx, String PREF_NAME, String key) {
		SharedPreferences mSettings = ctx.getSharedPreferences(PREF_NAME, 0);
		Editor mEditor = mSettings.edit();
		mEditor.remove(key);
		mEditor.commit();
	}

	public static boolean commit(Editor mEditor) {
		return mEditor.commit();
	}

    /**
     * 保存是否使用测试地址
     * @param context
     * @param isDebug
     */
	public static void saveDebug(Context context, boolean isDebug) {
	    Editor editor = getEditor(context, "app_debug_url", Context.MODE_PRIVATE);
	    editor.putBoolean("debug", isDebug);
	    editor.commit();
    }

    /**
     * 获取是否使用测试地址
     * @param context
     * @return
     */
    public static boolean getDebug(Context context) {
	    SharedPreferences sharedPreferences = getSharedPreferences(context, "app_debug_url", Context.MODE_PRIVATE);
	    return sharedPreferences.getBoolean("debug", false);
    }

    /**
     * 保存是否开启推送铃声
     * @param context
     * @param ringtone
     */
    public static void savePushDialogRingtone(Context context, boolean ringtone) {
        Editor editor = getEditor(context, KEY_PUSH_DIALOG_RINGTONE, Context.MODE_PRIVATE);
        editor.putBoolean("ringtone", ringtone);
        editor.commit();
    }

    /**
     * 获取是否开启推送铃声
     * @param context
     * @return
     */
    public static boolean getRingtone(Context context) {
        SharedPreferences sharedPreferences = getSharedPreferences(context, KEY_PUSH_DIALOG_RINGTONE, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("ringtone", false);
    }

	/**
	 * 保存用户的账号、密码
	 * 
	 * @param context
	 * @param user_id
	 */
	public static void saveUserInfo(Context context, String user_id, String user_pwd) {

		Editor editor = getEditor(context, "user_info", Context.MODE_PRIVATE);

		editor.putString("user_id", user_id);
		editor.putString("user_pwd", user_pwd);
		editor.commit();
	}

	/**
	 * 获取登录用户的账号、密码
	 * 
	 * @param context
	 * @return
	 */
	public static String[] getUidAndSid(Context context) {
		String[] userInfo = new String[2];
		SharedPreferences preferences = getSharedPreferences(context, "user_info", Context.MODE_PRIVATE);
		String user_id = preferences.getString("user_id", "");
		String user_pwd = preferences.getString("user_pwd", "");
		userInfo[0] = user_id;
		userInfo[1] = user_pwd;
		return userInfo;
	}

	/**
	 * 删除推送配置的token:
	 * 
	 * @param context
	 */
	public static void removePushTag(Context context) {

		removeKey(context, "push_tag", "token");
	}

	/**
	 * 判断本地是否有推送配置:
	 * 
	 * @param context
	 * @return false时初始化推送标签配置；true时不执行初始化。
	 */
	public static boolean isInitPushTag(Context context) {
		SharedPreferences preferences = getSharedPreferences(context, "push_tag", Context.MODE_PRIVATE);
		String token = preferences.getString("token", "");

        return !TextUtils.isEmpty(token);
    }

	/**
	 * 获取本地的token
	 * 
	 * @param context
	 */
	public static String getToken(Context context) {
		SharedPreferences preferences = getSharedPreferences(context, "push_tag", Context.MODE_PRIVATE);
		String token = preferences.getString("token", "");

		return token;
	}

	/**
	 * 保存推送开关
	 * 
	 * @param context
	 *            上下文
	 * @param key
	 *            键
	 * @param objValue
	 *            值:类型可以为int、float、long、String、boolean
	 */
	public static void savePushTag(Context context, String key, Object objValue) {
		Editor editor = getEditor(context, "push_tag", Context.MODE_PRIVATE);

		if (objValue instanceof Integer) {
			int value = ((Integer) objValue).intValue();
			editor.putInt(key, value);
		} else if (objValue instanceof String) {
			String s = (String) objValue;
			editor.putString(key, s);
		} else if (objValue instanceof Float) {
			float f = ((Float) objValue).floatValue();
			editor.putFloat(key, f);
		} else if (objValue instanceof Long) {
			long l = ((Long) objValue).longValue();
			editor.putLong(key, l);
		} else if (objValue instanceof Boolean) {
			boolean b = ((Boolean) objValue).booleanValue();
			editor.putBoolean(key, b);
		}

		editor.commit();
	}

	/**
	 * 获取本地推送配置数据
	 * 
	 * @param context
	 * @param mKey
	 * @param clazz
	 * @return
	 */
	public static Object getPushTag(Context context, String mKey, Class<?> clazz) {
		SharedPreferences preferences = getSharedPreferences(context, "push_tag", Context.MODE_PRIVATE);

		// Map<String, ?> params = preferences.getAll();
		// Iterator<String> iter = params.keySet().iterator();
		Object obj = null;
		// while (iter.hasNext()) {
		// String key = iter.next();
		// objValue = params.get(key);
		// if (mKey.endsWith(key)) {
		// return objValue;
		// }
		// }
		if (clazz == Integer.class) {
			int value = preferences.getInt(mKey, 0);
			return value;
		} else if (clazz == String.class) {
			String objValue = preferences.getString(mKey, "");
			return objValue;
		} else if (clazz == Float.class) {
			float objValue = preferences.getFloat(mKey, 0);
			return objValue;
		} else if (clazz == Long.class) {
			long objValue = preferences.getLong(mKey, 0);
			return objValue;
		} else if (clazz == Boolean.class) {
			boolean objValue = preferences.getBoolean(mKey, false);
			return objValue;
		}
		return obj;
	}

	/**
	 * 初始化推送配置
	 * 
	 * @param context
	 *            上下文
	 * @param cityId
	 *            地区编号
	 * @param token
	 *            信鸽推送注册返回的设备token
	 */
	public static void initPushTag(Context context, String cityId, String cityName, String token, String threeCityId, String threeCityName, boolean isNofirst) {
		// 保存初始化状态
		PackLocalInit packLocalInit = (PackLocalInit) PcsDataManager.getInstance().getLocalPack(PackLocalInit.KEY);
		packLocalInit.isRegestPush = true;
        PcsDataManager.getInstance().saveLocalData(PackLocalInit.KEY, packLocalInit);

		Editor editor = getEditor(context, "push_tag", Context.MODE_PRIVATE);
		editor.putString("token", token);

		editor.putString(PushTag.getInstance().PUSHTAG_QXYJ_CITY, cityId);
		editor.putString(PushTag.getInstance().PUSHTAG_QXYJ_CITY_NAME, cityName);

		if (!isNofirst) {
			// 预警信息推送 默认为对话框模式
			editor.putInt(PushTag.getInstance().PUSHTAG_QXYJ_MODEL, 1);
			editor.putBoolean(PushTag.getInstance().PUSHTAG_QXYJ_R, true);
			editor.putBoolean(PushTag.getInstance().PUSHTAG_QXYJ_O, true);
			editor.putBoolean(PushTag.getInstance().PUSHTAG_QXYJ_Y, false);
			editor.putBoolean(PushTag.getInstance().PUSHTAG_QXYJ_B, false);
            editor.putBoolean(PushTag.getInstance().PUSHTAG_REMOVE, false);
            editor.putBoolean(PushTag.getInstance().PUSHTAG_QXYJ_NATURAL, true);
            editor.putBoolean(PushTag.getInstance().PUSHTAG_QXYJ_ACCIDENT, true);
            editor.putBoolean(PushTag.getInstance().PUSHTAG_QXYJ_PUBLIC, true);
            editor.putBoolean(PushTag.getInstance().PUSHTAG_QXYJ_EMERGENCY, false);
            editor.putBoolean(PushTag.getInstance().PUSHTAG_TKL, true);
		}

		// 天气预报通知 只以通知栏方式推送
		editor.putString(PushTag.getInstance().PUSHTAG_WEATHER_FORECAST_CITY, cityId);
		editor.putString(PushTag.getInstance().PUSHTAG_WEATHER_FORECAST_CITY_NAME, cityName);
		if (!isNofirst) {
			editor.putInt(PushTag.getInstance().PUSHTAG_WEATHER_FORECAST_MODEL, 1);
			editor.putBoolean(PushTag.getInstance().PUSHTAG_WEATHER_FORECAST_MORNING, false);
			editor.putBoolean(PushTag.getInstance().PUSHTAG_WEATHER_FORECAST_EVENING, false);
            editor.putBoolean(PushTag.getInstance().PUSHTAG_WEATHER_BW,false);
            editor.putBoolean(PushTag.getInstance().PUSHTAG_WEATHER_TSTQ,false);
            editor.putBoolean(PushTag.getInstance().PUSHTAG_WEATHER_TSQW,false);
            editor.putBoolean(PushTag.getInstance().PUSHTAG_WEATHER_KQWR,false);
		}

		// 温馨提示：节日、节气提醒默认为开启状态，产品公告默认为开启状态
		if (!isNofirst) {
			editor.putBoolean(PushTag.getInstance().PUSHTAG_TIPS_HOLIDAY, true);
			editor.putBoolean(PushTag.getInstance().PUSHTAG_TIPS_JIEQI, true);
			editor.putBoolean(PushTag.getInstance().PUSHTAG_TIPS_NOTICE, true);
			// editor.putBoolean(PushTag.getInstance().PUSHTAG_TIPS_SPECIAL,
			// false);
		}
		// 温馨提示默认为通知栏显示模式
		if (!isNofirst) {
			editor.putInt(PushTag.getInstance().PUSHTAG_TIPS_MODEL, 1);
		}
		// 实况告警通知 总开关
		// editor.putBoolean(PushTag.getInstance().PUSHTAG_WARNING_INFO, false);
		editor.putString(PushTag.getInstance().PUSHTAG_WARNING_CITY, threeCityId);

		editor.putString(PushTag.getInstance().PUSHTAG_WARNING_CITY_NAME, threeCityName);
		// 只以通知栏方式推送
		// 实况告警通知本地配置 开关
		if (!isNofirst) {
			editor.putInt(PushTag.getInstance().PUSHTAG_WARNING_MODEL, 1);
			editor.putBoolean(PushTag.getInstance().LOCAL_WARNING_INFO_HIGH_TEMPERATURE, false);
			editor.putBoolean(PushTag.getInstance().LOCAL_WARNING_INFO_LOW_TEMPERATURE, false);
			editor.putBoolean(PushTag.getInstance().LOCAL_WARNING_INFO_VISIBILITY, false);
			editor.putBoolean(PushTag.getInstance().LOCAL_WARNING_INFO_HIGH_HUMIDITY, false);
			editor.putBoolean(PushTag.getInstance().LOCAL_WARNING_INFO_LOW_HUMIDITY, false);
			editor.putBoolean(PushTag.getInstance().LOCAL_WARNING_INFO_HOURLY_RAINFALL, false);
			editor.putBoolean(PushTag.getInstance().LOCAL_WARNING_INFO_WIND_SPEED, false);
		}
		// 实况告警通知本地配置 数值
		if (!isNofirst) {
			editor.putString(PushTag.getInstance().VALUE_HIGH_TEMPERATURE, "38");// 高温范围
			editor.putString(PushTag.getInstance().VALUE_LOW_TEMPERATURE, "5"); // 低温范围
			editor.putString(PushTag.getInstance().VALUE_VISIBILITY, "200"); // 高于
			// 能见度范围
			editor.putString(PushTag.getInstance().VALUE_HIGH_HUMIDITY, "70"); // 高于
			// 相对湿度范围
			editor.putString(PushTag.getInstance().VALUE_LOW_HUMIDITY, "30"); // 低于
			// 相对湿度范围
			editor.putString(PushTag.getInstance().VALUE_HOURLY_RAINFALL, "20"); // 高于
			// 每小时雨量
			editor.putString(PushTag.getInstance().VALUE_WIND_SPEED, "15"); // 高于
			// 风速范围
		}
		// 默认开启定点服务推送
		if (!isNofirst) {
			editor.putBoolean(PushTag.getInstance().SERVICE_LOCATION_WARNING, true);
		}

		// 气象服务推送默认设置：20160329
		if (!isNofirst) {
			editor.putBoolean(PushTag.getInstance().PUSHTAG_QXFW_JC, true);
			editor.putBoolean(PushTag.getInstance().PUSHTAG_QXFW_HY, false);
			editor.putBoolean(PushTag.getInstance().PUSHTAG_QXFW_LJ, false);
		}

		editor.commit();
	}

	/**
	 * 初始化信鸽推送的tags：给标签Map赋值
	 * 
	 * @param context
	 *            上下文
	 * @param cityId
	 *            默认城市id
	 * @return 标签Map
	 */
	public static Map<String, String> initPushTagMap(Context context, String cityId, String threeCityId) {
		Map<String, String> params = new HashMap<String, String>();

		// 预警信息推送 红色预警、橙色预警默认为开启状态，黄色预警、蓝色预警默认为关闭状态
		params.put(PushTag.getInstance().PUSHTAG_QXYJ_CITY, cityId);
		params.put(PushTag.getInstance().PUSHTAG_QXYJ_R, "1");
		params.put(PushTag.getInstance().PUSHTAG_QXYJ_O, "1");
		params.put(PushTag.getInstance().PUSHTAG_QXYJ_Y, "0");
		params.put(PushTag.getInstance().PUSHTAG_QXYJ_B, "0");

		// 天气预报通知 早间、晚间天气预报通知默认为关闭状态
		params.put(PushTag.getInstance().PUSHTAG_WEATHER_FORECAST_CITY, cityId);
		params.put(PushTag.getInstance().PUSHTAG_WEATHER_FORECAST_MORNING, "0");
		params.put(PushTag.getInstance().PUSHTAG_WEATHER_FORECAST_EVENING, "0");
        params.put(PushTag.getInstance().PUSHTAG_WEATHER_BW,"0");
        params.put(PushTag.getInstance().PUSHTAG_WEATHER_TSTQ,"0");
        params.put(PushTag.getInstance().PUSHTAG_WEATHER_TSQW,"0");
        params.put(PushTag.getInstance().PUSHTAG_WEATHER_KQWR,"0");

		// 温馨提示：节日、节气提醒默认为开启状态，专题、产品公告默认为开启状态
		params.put(PushTag.getInstance().PUSHTAG_TIPS_HOLIDAY, "1");
		params.put(PushTag.getInstance().PUSHTAG_TIPS_JIEQI, "1");
		params.put(PushTag.getInstance().PUSHTAG_TIPS_NOTICE, "1");
		// params.put(PushTag.getInstance().PUSHTAG_TIPS_SPECIAL, "0");

		// 实况告警通知 默认关闭状态
		// params.put(PushTag.getInstance().PUSHTAG_WARNING_INFO, "0");
		params.put(PushTag.getInstance().PUSHTAG_WARNING_CITY, threeCityId);

		
//		气象服务推送20160329
		params.put(PushTag.getInstance().PUSHTAG_QXFW_CITY, threeCityId);
		params.put(PushTag.getInstance().PUSHTAG_QXFW_JC, "1");
		params.put(PushTag.getInstance().PUSHTAG_QXFW_HY, "1");
		params.put(PushTag.getInstance().PUSHTAG_QXFW_LJ, "1");
		
		return params;
	}

	/**
	 * 判断本地是否有气象产品配置:
	 * 
	 * @param context
	 * @return false时初始化推送标签配置；true时不执行初始化。
	 */
	public static boolean isInitProduct(Context context) {
		SharedPreferences preferences = getSharedPreferences(context, "product", Context.MODE_PRIVATE);
		String product = preferences.getString("product_config", "");

        return !TextUtils.isEmpty(product);
    }

	/**
	 * 初始化气象产品配置
	 * 
	 * @param context
	 * @param itemNames
	 * @param product
	 */
	public static void InitProduct(Context context, String[] itemNames, String product) {
		Editor editor = getEditor(context, "product", Context.MODE_PRIVATE);
		editor.putString("product_config", product);
		for (int i = 0; i < itemNames.length; i++) {
			editor.putBoolean(itemNames[i], true);
		}
		editor.commit();
	}

	public static void saveProductValue(Context context, String key, boolean objValue) {
		Editor editor = getEditor(context, "product", Context.MODE_PRIVATE);
		editor.putBoolean(key, objValue);
		editor.commit();
	}

	/**
	 * 获取本地气象产品配置
	 * 
	 * @param context
	 * @param mKey
	 * @return
	 */
	public static boolean getProductValue(Context context, String mKey) {
		SharedPreferences preferences = getSharedPreferences(context, "product", Context.MODE_PRIVATE);

		boolean objValue = preferences.getBoolean(mKey, false);
		return objValue;
	}

	/**
	 * 判断新增栏目是否存在,存在返回true，不存在返回false
	 * 
	 * @param context
	 * @param mKey
	 * @return
	 */
	public static Boolean getisSectionValue(Context context, String mKey) {
		SharedPreferences preferences = getSharedPreferences(context, "product", Context.MODE_PRIVATE);
		Boolean isSection = preferences.contains(mKey);
		return isSection;
	}

	/**
	 * 判断新增栏目是否存在，不存在的话往SharedPreferences里面添加一个栏目且value为true
	 * 
	 * @param context
	 * @param itemNames
	 * @param product
	 */
	public static void judgeProduct(Context context, String itemNames, String product) {
		Editor editor = getEditor(context, "product", Context.MODE_PRIVATE);
		editor.putString("product_config", product);
		editor.putBoolean(itemNames, true);
		editor.commit();
	}

	public static void saveMyImei(Context context, String key, String objValue) {
		Editor editor = getEditor(context, "imei", Context.MODE_PRIVATE);
		editor.putString(key, objValue);
		editor.commit();
	}

	/**
	 * 获取本地的imei
	 * 
	 * @param context
	 */
	public static String getMyImei(Context context) {
		SharedPreferences preferences = getSharedPreferences(context, "imei", Context.MODE_PRIVATE);
		String token = preferences.getString("imei", "");

		return token;
	}

	/**
	 * 获取本地缓存的数据列表
	 * 
	 * @param context
	 * 
	 * @param LocalDataName
	 *            存储时的名称
	 * @return
	 */
	public static List<String> LoadLocalEntityList(Context context, String LocalDataName) {
		List<String> list = new ArrayList<String>();
		SharedPreferences more_preferences = getSharedPreferences(context, LocalDataName, Context.MODE_PRIVATE);
		int size = more_preferences.getInt("size", 0);
		for (int i = 0; i < size; i++) {
			String jsonString = more_preferences.getString("id_" + i, "");
			list.add(jsonString);
		}
		return list;
	}

	/**
	 * 获取本地缓存的数据列表长度是否为0
	 * 
	 * @param context
	 * @param LocalDataName
	 * @return
	 */
	public static boolean LocalDataListSize(Context context, String LocalDataName) {
		SharedPreferences more_preferences = getSharedPreferences(context, LocalDataName, Context.MODE_PRIVATE);
		int size = more_preferences.getInt("size", 0);
        return size > 0;
    }

	/**
	 * 把数据列表保存到本地缓存
	 * 
	 * @param context
	 * 
	 * @param list
	 * 
	 * @param LocalDataName
	 *            存储时的名称
	 */
	public static <T> void SaveLocalEntityList(Context context, List<String> list, String LocalDataName) {

		Editor editor = getEditor(context, LocalDataName, Context.MODE_PRIVATE);
		editor.clear();// 清除原有的数据
		editor.commit();

		editor.putInt("size", list.size());
		for (int i = 0; i < list.size(); i++) {
			String content = list.get(i);
			editor.putString("id_" + i, content);
		}
		editor.commit();
	}

	/**
	 * 把数据列表保存到本地缓存
	 * 
	 * @param context
	 * 
	 * @param list
	 * 
	 * @param LocalDataName
	 *            存储时的名称
	 */
	public static <T> void SaveLocalDataList(Context context, List<String> list, String LocalDataName) {
		Editor editor = getEditor(context, LocalDataName, Context.MODE_PRIVATE);
		editor.clear();// 清除原有的数据
		editor.commit();
		editor.putInt("size", list.size());
		for (int i = 0; i < list.size(); i++) {
			String content = list.get(i);
			editor.putString("id_" + i, content);
		}
		editor.commit();
	}


	/**
	 * 首页介绍信息
	 * @param context
	 */
	public static void saveIntroduction(Context context,String item,String key) {
		Editor editor = getEditor(context, "Introduction", Context.MODE_PRIVATE);
		editor.putString("info_"+key, item);
		editor.commit();
	}

	/**
	 * 首页介绍信息
	 * @param context
	 */
	public static String getIntroduction(Context context,String key) {
		SharedPreferences mSettings = context.getSharedPreferences("Introduction", Context.MODE_PRIVATE);;
		String time=mSettings.getString("info_"+key,"");
		return time;
	}
}
