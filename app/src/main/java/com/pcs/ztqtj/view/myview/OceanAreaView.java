package com.pcs.ztqtj.view.myview;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * 区域视图
 * @author E.Sun
 * 2015年11月3日
 */
public class OceanAreaView {

	/** 区域名称列表 */
	private List<MarkerOptions> nameOptionsList = new ArrayList<MarkerOptions>();
	/** 区域边界线列表 */
	private List<PolylineOptions> rangeOptionsList = new ArrayList<PolylineOptions>();
	
	private List<Marker> nameList = new ArrayList<Marker>();
	
	/**
	 * 添加区域名称
	 * @param options
	 */
	public void addNameOptions(MarkerOptions options) {
		if(options != null) {
			nameOptionsList.add(options);
		}
	}
	
	/**
	 * 添加区域边界线
	 * @param options
	 */
	public void addRangeOptions(PolylineOptions options) {
		if(options != null) {
			rangeOptionsList.add(options);
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
		
		nameList.clear();
		for(MarkerOptions options : nameOptionsList) {
			if(options != null) {
				nameList.add(aMap.addMarker(options));
			}
		}
		for(PolylineOptions options : rangeOptionsList) {
			if(options != null) {
				aMap.addPolyline(options);
			}
		}
	}
	
	/**
	 * 获取区域名称显示位置经纬度列表
	 * @return
	 */
	public List<LatLng> getLatLngs() {
		List<LatLng> list = new ArrayList<LatLng>();
		for(MarkerOptions options : nameOptionsList) {
			if(options != null) {
				list.add(options.getPosition());
			}
		}
		return list;
	}
	
	/**
	 * 比较指定编号标记是否为区域名称标记
	 * @param id
	 * @return
	 */
	public boolean isAreaView(String id) {
		for(Marker marker : nameList) {
			if(marker.getId().equals(id)) {
				return true;
			}
		}
		return false;
	}
	
}
