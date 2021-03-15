package com.pcs.ztqtj.view.myview;

import java.util.ArrayList;
import java.util.List;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;

/**
 * 海洋天气视图
 * @author E.Sun
 * 2015年11月2日
 */
public class OceanWeatherView {

	/** 天气图标列表 */
	private List<MarkerOptions> weatherList = new ArrayList<MarkerOptions>();
	
	/**
	 * 添加天气图标
	 * @param options
	 */
	public void addWeatherMarker(MarkerOptions options) {
		if(options != null) {
			weatherList.add(options);
		}
	}
	
	/**
	 * 显示
	 * @param aMap
	 */
	public void show(AMap aMap) {
		if(aMap == null) {
			return;
		}
		
		for(MarkerOptions options : weatherList) {
			if(options != null) {
				aMap.addMarker(options);
			}
		}
	}
	
	/**
	 * 获取天气图标显示位置经纬度列表
	 * @return
	 */
	public List<LatLng> getLatLngs() {
		List<LatLng> list = new ArrayList<LatLng>();
		for(MarkerOptions options : weatherList) {
			if(options != null) {
				list.add(options.getPosition());
			}
		}
		return list;
	}
	
}
