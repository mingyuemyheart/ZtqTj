package com.pcs.ztqtj.util;

public class CONST {

//    public static String BASE_IP = "https://tjhm-app.weather.com.cn:8081";
    public static String BASE_IP = "http://60.29.105.41:8088";
    public static String GEO_URL = BASE_IP+"/geo/station/parse";
    public static String BASE_URL = BASE_IP+"/tjapi/app/";


    public static final String PROTOCAL = BASE_IP+"/web/tjfile/smart/yhxy.html";//用户协议
    public static final String PRIVACY = BASE_IP+"/web/tjfile/smart/yszc.html";//隐私政策

    public static final String WEB_URL = "web_Url";//网页地址的标示
    public static final String ACTIVITY_NAME = "activity_name";//界面名称

    public static String JPG = ".jpg";

    public static int RESULT_LOGIN = 10001;//登录

    public static final String BROADCAST_REFRESH_COLUMNN = "broadcast_refresh_columnn";//刷新主页面栏目数据
    public static final String BROADCAST_REFRESH_PROVE = "broadcast_refresh_prove";//刷新气象证明广播
    public static final String BROADCAST_REFRESH_EACH = "broadcast_refresh_each";//刷新农情互动广播

    //下拉刷新progresBar四种颜色
    public static final int color1 = android.R.color.holo_blue_bright;
    public static final int color2 = android.R.color.holo_blue_light;
    public static final int color3 = android.R.color.holo_blue_bright;
    public static final int color4 = android.R.color.holo_blue_light;

}
