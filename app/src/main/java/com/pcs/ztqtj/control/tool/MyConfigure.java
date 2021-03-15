package com.pcs.ztqtj.control.tool;

/**
 * 
 * @author chenjh
 */
public class MyConfigure {

	private static MyConfigure instance = null;

	public static MyConfigure getInstance() {
		if (instance == null) {
			instance = new MyConfigure();
		}
		return instance;
	}

	// public void init() {
	// isLogin = false;
	// userId = "";
	// mobile = "";
	// nickName = "";
	// defaultCityID = "";
	// defaultCityName = "";
	// }
	//
	// /** 用户编号 **/
	// public String userId = "";// 1416448260125
	// /** 用户手机号码,登录名 **/
	// public String mobile = ""; // 41681691@qq.com chenjh123
	// /** 用户昵称 **/
	// public String nickName = "";
	//
	// public String defaultCityID = "";
	// public String defaultCityName = "";
	//
	// public boolean isLogin = false;
	//
	// /**
	// * 获取用户登录状态
	// *
	// * @return
	// */
	// public boolean getIsLogin() {
	// return isLogin;
	// }
	//
	// /**
	// * 设置用户登录状态
	// *
	// * @param isLogin
	// */
	// public void setLogin(boolean login) {
	// isLogin = login;
	// }

	/** 图片最大数量 **/
	public final static int PHOTO_MAX_NUM = 1;

	public final static int SEND_SMS_TYPE = 24;

	/** 10001:用户注册 **/
	public final static int RESULT_REGISTER = 10001; // 用户注册成功
	/** 10002:用户登录成功 **/
	public final static int RESULT_LOGIN = 10002; // 用户登录成功

	/** 10011:台风路径跳转图列界面 **/
	public final static int RESULT_EXAMPLE = 10011;
	/** 10012:景点选择列表跳转旅游气象界面 **/
	public final static int RESULT_TRAVEL = 10012;
	/** 10013:空气质量详细信息界面跳转空气质量排行榜 **/
	public final static int RESULT_AIR_INFO = 10013;
	/** 10014:跳转自定义相册界面 **/
	public final static int RESULT_PHOTO_ALBUMl = 10014;
	/** 10015:跳转自定义拍照界面 **/
	public final static int RESULT_PHOTO_CAMER = 10015;
	/** 10016:选择了图片数通知 **/
	public final static int RESULT_IMAGE_NUMBER = 10016;
	/** 10017:跳转查看大图 **/
	public final static int RESULT_PHOTO_SHOW = 10017;
	/** 10018:个人中心跳转关注和粉丝列表 **/
	public final static int RESULT_USER_FOCUS = 10018;
	/** 10019:照片墙跳转其他界面返回 **/
	public final static int RESULT_PHOTOWALL = 10019;
	/** 10020:亲情城市选择城市返回 **/
	public final static int RESULT_SELECT_CITY = 10020;
	/** 10021:旅游气象选择景点返回 **/
	public final static int RESULT_SELECT_TRAVELVIEW = 10021;
	/** 10022:从查看发布的图片界面跳转 **/
	public final static int RESULT_PHOTOSHOW_VIEW = 10022;
	/** 10023:从他人的个人中心界面跳转 **/
	public final static int RESULT_OTHER_USER_CENTER = 10023;
	/** 10024:从帮助列表 界面跳转 **/
	public final static int RESULT_HELP_VIEW = 10024;
	/** 10025:从气象服务第三级产品列表 界面跳转 **/
	public final static int RESULT_SERVICE_THREE = 10025;
	/** 10026:跳转到添加城市页面 **/
	public final static int RESULT_CITY_LIST = 10026;
	
	public final static int RESULT_LEFT_CITY_LIST = 10031;// 左侧边栏跳转城市列表
	
	/** 10027:实景发布跳转到实景用户登录界面 **/
	public final static int RESULT_PHOTO_SUBMIT = 10027;
	/** 10028:实况预警推送界面中跳转设置温度(能见度、湿度、小时雨量、风速)的seekbar界面 **/
	public final static int RESULT_WARN_LIVE = 10028;
	/**我的服务*/
	public final static int RESULT_MYSERVICE = 10030;
	/** 10031:旅游气象选择景点返回 **/
	public final static int RESULT_GOTO_TRAVEDETAIL = 10031;
	
	public final static int TYPE_BOTTOM_FOCUS = 1; // 关注
	public final static int TYPE_BOTTOM_FANS = 2; // 粉丝
	public final static int TYPE_BOTTOM_PRAISE = 3; // 点赞
	public final static int TYPE_BOTTOM_IMAGE = 4; // 图片
	/** 侧边栏用户登录 **/
	public final static int RESULT_SET_TO_USER = 10032;
	/** 生活指数更新 **/
	public final static int RESULT_LIFENUMBER = 10033;
    /** 跳转用户注册页面 **/
    public final static int RESULT_USER_REGISTER = 10034;
    /** 跳转相机 **/
    public final static int REQUEST_CAMERA = 10035;
    /** 跳转相册 **/
    public final static int REQUEST_ALBUM = 10036;
    /** 资料查询登录 **/
    public final static int REQUEST_DATA_QUERY_LOGIN = 10037;
    /** 支付 **/
    public final static int REQUEST_PAY = 10038;
    /** intent的bundle **/
    public final static String EXTRA_BUNDLE = "extra_bundle";
    // 跳转系统权限页面
    public final static int REQUEST_SYSTEM_PERMISSION = 10039;
    public final static int REQUEST_PERMISSION_AUDIO = 10040;
}
