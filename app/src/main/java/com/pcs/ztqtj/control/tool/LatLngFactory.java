package com.pcs.ztqtj.control.tool;

import com.amap.api.maps.model.LatLng;

/**
 * 经纬度工具类
 * @author E.Sun
 * 2015年10月22日
 */
public class LatLngFactory {

	private LatLngFactory() {}
	
	/**
	 * 格式化经纬度
	 * @param latLng
	 * @return
	 */
	public static LatLng formatLatLng(LatLng latLng) {
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
	
	/**
	 * 获取格式化后的经纬度
	 * @param latitude
	 * @param longitude
	 * @return
	 */
	public static LatLng getFormatLatLng(double latitude, double longitude) {
		if(latitude < -90) {
			latitude = -90;
		} else if(latitude > 90) {
			latitude = 90;
		}
		if(longitude <= -180) {
			longitude = -179.999999;
		} else if(longitude >= 180) {
			longitude = 179.999999;
		}
		return new LatLng(latitude, longitude);
	}
	
}
