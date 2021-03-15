package com.pcs.ztqtj.control;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.pcs.lib.lib_pcs_v3.PcsInit;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib.lib_pcs_v3.model.db.DBHelper;
import com.pcs.lib_ztqfj_v2.model.pack.FactoryPack;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCity;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityInfo;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityLocation;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalUrl;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.command.InterCommand;
import com.pcs.ztqtj.control.inter.Callback;
import com.pcs.ztqtj.control.loading.CommandInitCityList;
import com.pcs.ztqtj.control.loading.CommandReqInit;
import com.pcs.ztqtj.control.tool.AutoDownloadWeather;
import com.pcs.ztqtj.control.tool.LocalDataHelper;
import com.pcs.ztqtj.control.tool.ZtqAppWidget;
import com.pcs.ztqtj.control.tool.ZtqLocationTool;
import com.pcs.ztqtj.control.tool.ZtqPushTool;
import com.pcs.ztqtj.model.ZtqCityDB;

/**
 * 初始化
 *
 * @author JiangZY
 */
public class ControlAppInit {
    private static ControlAppInit instance = null;

    private ControlAppInit() {

    }

    public static ControlAppInit getInstance() {
        if (instance == null) {
            instance = new ControlAppInit();
        }

        return instance;
    }
    public static boolean Is_Main=false;

    public static boolean isIs_Main() {
        return Is_Main;
    }

    public static void setIs_Main(boolean is_Main) {
        Is_Main = is_Main;
    }

    private InterCommand.Status mStatus = InterCommand.Status.CREATE;
    private Context mContext;

    public void init(Context context) {
        if (mStatus == InterCommand.Status.RUNNING || mStatus == InterCommand.Status.SUCC) {
            return;
        }
        mStatus = InterCommand.Status.RUNNING;
        mContext = context;
        try {
            // 创建数据库
            DBHelper.getInstance(context);
            // Pcs初始化
            PcsInit.getInstance().init(context);
            // 数据包工厂
            FactoryPack.init();

            // 本地配置
            doLocalInit(context);
            // 数据下载服务
            doDataDownload(context);


            // 获取后台城市db初始化
            //reqCityDB(context);
//            CityListObservable observable = new CityListObservable(context);
//            observable.execute();

//-------测试直接使用本地城市db数据库access--------
//            reqInit(context);
//            ZtqCityDB.getInstance().init(context);
//            --------------------
        } catch (Exception e) {
            mStatus = InterCommand.Status.FAIL;
            e.printStackTrace();
        }
    }

    /**
     * 数据下载服务
     */
    private void doDataDownload(Context context) {
        PackLocalUrl pack = (PackLocalUrl) PcsDataManager.getInstance().getLocalPack(PackLocalUrl.KEY);
        PcsDataDownload.setUrl(pack.url);
        PcsDataDownload.start();
    }

    /**
     * 自动下载数据
     */
    private void doAutoDownload() {
        PackLocalCityInfo pack = ZtqCityDB.getInstance().getCurrentCityInfo();
        // 城市列表
        for (int i = 0; i < pack.localCityList.size(); i++) {
            PackLocalCity city = pack.localCityList.get(i);
            AutoDownloadWeather.getInstance().addWeekCity(city);
        }
        // 定位城市
        PackLocalCityLocation packLocationCity = ZtqLocationTool.getInstance().getLocationCity();
        if (packLocationCity != null && packLocationCity.isFjCity == true && !TextUtils.isEmpty(packLocationCity.ID)) {
            AutoDownloadWeather.getInstance().setDefaultCity(packLocationCity);
            AutoDownloadWeather.getInstance().beginMainData();
        }
    }

    /**
     * 本地配置
     */
    private void doLocalInit(Context context) {
        // 本地配置包
        PackLocalUrl packUrl = (PackLocalUrl) PcsDataManager.getInstance().getLocalPack(PackLocalUrl.KEY);
        // url
        if (packUrl == null) {
            packUrl = new PackLocalUrl();
        }
        String defaultUrl = context.getString(R.string.url);
        boolean isDebug = LocalDataHelper.getDebug(context);
        // 如果不是测试地址，则更新url
        if(!isDebug) {
            packUrl.url = defaultUrl;
            PcsDataManager.getInstance().saveLocalData(PackLocalUrl.KEY, packUrl);
        } else {
            Toast.makeText(context, "正在使用测试地址", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 请求初始化数据
     */
    public void reqInit(Context context) {
        //请求初始化
        CommandReqInit commandReqInit = new CommandReqInit(context);
        commandReqInit.addListener(mReqInitListener);
        commandReqInit.execute();
    }

    private Callback callback;

    /**
     * 请求初始化数据
     */
    public void reqInit(Context context, Callback callback) {
        this.callback = callback;
        //请求初始化
        CommandReqInit commandReqInit = new CommandReqInit(context);
        commandReqInit.addListener(mReqInitListener);
        commandReqInit.execute();
    }

    /**
     * 请求城市列表
     *
     * @param context
     */
    public void reqCityDB(Context context) {
        CommandInitCityList commandInitCityList = new CommandInitCityList(context);
        //commandInitCityList.addListener(null);
        commandInitCityList.execute();
    }

    public InterCommand.Status getStatus() {
        return mStatus;
    }

    private InterCommand.InterCommandListener mReqInitListener = new InterCommand.InterCommandListener() {

        @Override
        public void done(InterCommand.Status status) {
            if (status == InterCommand.Status.FAIL) {
                mStatus = InterCommand.Status.FAIL;
                return;
            }

            try {
                // 自动下载数据
                doAutoDownload();
                // 推送初始化
                ZtqPushTool.getInstance().refreshPush();
                // 开始定位
                ZtqLocationTool.getInstance().beginLocation(mContext);
                // 刷新小部件
                ZtqAppWidget.getInstance().updateAllWidget(mContext);

                if(callback != null) {
                    callback.onCallback();
                }
                mStatus = InterCommand.Status.SUCC;
            } catch (Exception e) {
                mStatus = InterCommand.Status.FAIL;
                e.printStackTrace();
            }
        }
    };
}
