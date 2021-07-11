package com.pcs.ztqtj.control.tool;

import android.app.ActivityManager;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.pcs.lib.lib_pcs_v3.model.pack.PcsPackDown;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCity;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackSstqUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.airinfopack.PackAirInfoSimpleUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.PackYjxxIndexFbUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.week.PackMainWeekWeatherUp;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.view.appwidget.WeatherWidget_4x2;
import com.pcs.ztqtj.view.appwidget.WeatherWidget_5x1;
import com.pcs.ztqtj.view.appwidget.WeatherWidget_5x2;
import com.pcs.ztqtj.view.appwidget.WeatherWidget_5x3;

import java.util.List;

/**
 * 小部件工具类
 */
public class ZtqAppWidget {

	private static ZtqAppWidget instance = null;

	public static ZtqAppWidget getInstance() {
		if (instance == null) {
			instance = new ZtqAppWidget();
		}
		return instance;
	}

	/**
	 * 更新小部件
	 * @param context 上下文
	 * @param cls  类名
	 */
	public void updateWidget(Context context, Class<?> cls) {
		Intent intent = new Intent(context.getApplicationContext(), cls);
		intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE+cls.getName());
		// Use an array and EXTRA_APPWIDGET_IDS instead of
		// AppWidgetManager.EXTRA_APPWIDGET_ID,
		// since it seems the onUpdate() is only fired on that:
		int ids[] = AppWidgetManager.getInstance(
				context.getApplicationContext()).getAppWidgetIds(
				new ComponentName(context.getApplicationContext(), cls));
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
		context.getApplicationContext().sendBroadcast(intent);
	}
	
	/**
	 * 更新所有小部件
	 * @param context
	 */
	public void updateAllWidget(Context context) {
		updateWidget(context, WeatherWidget_5x2.class);
		updateWidget(context, WeatherWidget_5x1.class);
        updateWidget(context, WeatherWidget_5x3.class);
        updateWidget(context, WeatherWidget_4x2.class);
	}

    /**
     * 时间服务是否运行
     * @param context
     * @param serviceClass
     * @return
     */
    public boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void request(Context context) {
        PackLocalCity city = ZtqCityDB.getInstance().getCityMain();
        if(city != null) {
            NetMultiTask task = new NetMultiTask(context, netListener);
            //实时天气
            PackSstqUp packSstqUp = new PackSstqUp();
            packSstqUp.area = city.ID;
            //一周天气
            PackMainWeekWeatherUp packWeekUp = new PackMainWeekWeatherUp();
            packWeekUp.setCity(city);
            //首页预警
            PackYjxxIndexFbUp packYjxxUp = new PackYjxxIndexFbUp();
            packYjxxUp.setCity(city);
            // 空气质量
            PackAirInfoSimpleUp packAirUp = new PackAirInfoSimpleUp();
            packAirUp.setCity(city);
            packAirUp.type = "1";
            task.execute(packSstqUp, packWeekUp, packYjxxUp, packAirUp);
        }
    }

    private NetMultiTask.NetListener netListener = new NetMultiTask.NetListener() {
        @Override
        public void onComplete(Context context, List<PcsPackDown> down) {
            updateAllWidget(context);
        }
    };
}
