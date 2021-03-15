package com.pcs.ztqtj.control.tool;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2017/11/7 0007.
 * chen_jx
 */

public class SharedPreferencesUtil {

    private static SharedPreferencesUtil util;
    private static SharedPreferences sp;

    private SharedPreferencesUtil(Context context, String name) {
        sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    /**
     * 初始化SharedPreferencesUtil,只需要初始化一次，建议在Application中初始化
     *
     * @param context 上下文对象
     * @param name    SharedPreferences Name
     */
    public static void getInstance(Context context, String name) {
        if (util == null) {
            util = new SharedPreferencesUtil(context, name);
        }
    }

    /**
     * 保存数据到SharedPreferences
     *
     * @param key   键
     * @param value 需要保存的数据
     * @return 保存结果
     */
    public static boolean putData(String key, String value) {
        boolean result;
        SharedPreferences.Editor editor = sp.edit();
        try {
            editor.putString(key, value);
            result = true;
        } catch (Exception e) {
            result = false;
            e.printStackTrace();
        }
        editor.apply();
        return result;
    }

    /**
     * 获取SharedPreferences中保存的数据
     *
     * @param key          键
     * @param defaultValue 获取失败默认值
     * @return 从SharedPreferences读取的数据
     */
    public static String  getData(String key, String defaultValue) {
        String result = null;

        try {
            result = sp.getString(key, (String) defaultValue);
        } catch (Exception e) {
            result = null;
            e.printStackTrace();
        }
        return result;
    }
}