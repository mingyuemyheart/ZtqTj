package com.pcs.ztqtj.control.tool;

import com.amap.api.maps.model.LatLng;

/**
 * 高德地图经纬度工具
 * @author E.Sun
 * 2015年11月2日
 */
public class LatitudeLongitudeTool {

	private LatitudeLongitudeTool(){}
	
	/**
	 * 格式化经纬度
	 * @param latLng
	 * @return
	 */
	public static LatLng formatLatLong(LatLng latLng) {
		if(latLng == null) {
			return null;
		}
		
		double latitude = latLng.latitude;
		if(latitude < -90) {
			latitude = -90;
		} else if(latitude > 90) {
			latitude = 90;
		}
		double longitude = latLng.longitude;
		if(longitude <= -180) {
			longitude = -179.999999;
		} else if(longitude >= 180) {
			longitude = 179.999999;
		}
		return new LatLng(latitude, longitude);
	}
	
}
