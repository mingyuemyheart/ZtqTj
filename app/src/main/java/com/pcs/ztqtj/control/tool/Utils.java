package com.pcs.ztqtj.control.tool;

/**
 * Created by ${jx_chen}
 * on 2019-6-14.
 */
public class Utils {

    public static final int DELAY = 3000;
    private static long lastClickTime = 0;
    public static boolean isNotFastClick(){
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime > DELAY) {
            lastClickTime = currentTime;
            return true;
        }else{
            return false;
        }
    }
}
