package com.pcs.ztqtj.control.tool;

import android.graphics.Color;

/**
 * 空气质量工具
 * 
 * @author JiangZy
 * 
 */
public class AirQualityTool {
	private static AirQualityTool instance;

	private AirQualityTool() {
	}

	public static AirQualityTool getInstance() {
		if (instance == null) {
			instance = new AirQualityTool();
		}

		return instance;
	}

	/**
	 * 获取AQI颜色值
	 *
	 * @return
	 */
	public int getAqiColor(int aqi) {
		int color = 0;
		if (aqi <= 50) {
			// 优
			color = Color.rgb(3, 227, 6);
		} else if (aqi > 50 && aqi <= 100) {
			// 良
			color = Color.rgb(255, 254, 3);
		} else if (aqi > 100 && aqi <= 150) {
			// 轻度污染
			color = Color.rgb(253, 167, 4);
		} else if (aqi > 150 && aqi <= 200) {
			// 中度污染
			color = Color.rgb(255, 2, 4);
		} else if (aqi > 200 && aqi <= 300) {
			// 重度污染
			color = Color.rgb(130, 3, 134);
		} else if (aqi > 300) {
			// 严重污染
			color = Color.rgb(128, 6, 29);
		}

		return color;
	}

	/**
	 * 获取健康提示
	 *
	 * @param arr
	 * @param aqi
	 * @return
	 */
	public String getHealthTip(String arr[], int aqi) {
		String str = arr[0];
		if (aqi <= 50) {
			// 优
			str = arr[0];
		} else if (aqi > 50 && aqi <= 100) {
			// 良
			str = arr[1];
		} else if (aqi > 100 && aqi <= 150) {
			// 轻度污染
			str = arr[2];
		} else if (aqi > 150 && aqi <= 200) {
			// 中度污染
			str = arr[3];
		} else if (aqi > 200 && aqi <= 300) {
			// 重度污染
			str = arr[4];
		} else if (aqi > 300) {
			// 严重污染
			str = arr[5];
		}
		return str;
	}
}
