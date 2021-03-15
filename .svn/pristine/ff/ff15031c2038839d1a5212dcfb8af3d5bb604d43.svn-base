package com.pcs.ztqtj.view.myview.typhoon;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;

/**
 * 测距View
 * @author E.Sun
 * 2015年9月7日
 */
public class DistanceView {
	
	/** 中心点 **/
	private MarkerOptions centerOptions;
	private Marker center;
	/** 连接线 **/
	private PolylineOptions lineOptions;
	private Polyline line;
	
	/**
	 * 设置中心点
	 * @param options
	 */
	public void setCenter(MarkerOptions options) {
		centerOptions = options;
	}
	
	/**
	 * 设置连接线
	 * @param options
	 */
	public void setLine(PolylineOptions options) {
		lineOptions = options;
	}
	
	/**
	 * 显示测距视图
	 * @param aMap
	 */
	public void show(AMap aMap) {
		if(aMap == null) {
			return;
		}
		
		if(centerOptions != null) {
			center = aMap.addMarker(centerOptions);
			center.showInfoWindow();
		}
		if(lineOptions != null) {
			line = aMap.addPolyline(lineOptions);
		}
	}
	
	/**
	 * 移除测距View
	 */
	public void hide() {
		if(center != null) {
			center.remove();
			center = null;
		}
		if(line != null) {
			line.remove();
			line = null;
		}
	}
}
