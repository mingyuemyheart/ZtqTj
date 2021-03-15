package com.pcs.ztqtj.view.myview;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polygon;
import com.amap.api.maps.model.PolygonOptions;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.LatLngFactory;
import com.pcs.lib_ztqfj_v2.model.pack.net.locationwarning.LocationWarningInfo;
import com.pcs.lib_ztqfj_v2.model.pack.net.locationwarning.MyPosition;

/**
 * 定点服务视图
 * @author E.Sun
 * 2015年11月18日
 */
public class WarningView {

	/** 标识 */
	private String name = "";
	/** 七级台风预警区域 */
	private Circle sevenTyphoonView;
	/** 十级台风预警区域 */
	private Circle tenTyphoonView;
	/** 其他类型预警区域 */
	private Polygon otherView;
	/** 管理平台指定区域 */
	private Polygon managerView;
	/** 用户定制服务区域列表 */
	private List<Marker> areaViewList = new ArrayList<Marker>();
	/** 区域经纬度列表 */
	private List<LatLng> latLngList = new ArrayList<LatLng>();
	
	private CircleOptions sevenTyphoonOptions;
	private CircleOptions tenTyphoonOptions;
	private PolygonOptions otherOptions;
	private PolygonOptions managerOptions;
	private List<MarkerOptions> areaOptionsList = new ArrayList<MarkerOptions>();
	
	public WarningView(Context context, LocationWarningInfo info) {
		if(info != null && context != null) {
			fillView(context, info);
		}
	}
	
	private void fillView(Context context, LocationWarningInfo info) {
		name = info.name;
		
		// 预警红色，七级风圈红色，十级风圈紫色，后台区域蓝色，用户蓝点
		
		// 七级台风预警
		if(info.windSeven > 0) {
			LatLng latLng = LatLngFactory.getFormatLatLng(info.latitude, info.longitude);
			sevenTyphoonOptions = new CircleOptions()
			.center(latLng)
			.fillColor(Color.TRANSPARENT)
			.strokeWidth(6)
			.strokeColor(context.getResources().getColor(R.color.warn_red))
			.radius(info.windSeven * 1000);
			latLngList.add(latLng);
		}
		
		// 十级台风预警
		if(info.windTen > 0) {
			LatLng latLng = LatLngFactory.getFormatLatLng(info.latitude, info.longitude);
			tenTyphoonOptions = new CircleOptions()
			.center(latLng)
			.fillColor(Color.TRANSPARENT)
			.strokeWidth(6)
			.strokeColor(context.getResources().getColor(R.color.warn_purple))
			.radius(info.windTen * 1000);
			latLngList.add(latLng);
		}
		
		// 其他预警
		if(info.warningPositions.size() >= 3) {
			List<LatLng> list = getRange(info.warningPositions);
			if(list != null) {
				otherOptions = new PolygonOptions()
				.addAll(list)
				.fillColor(Color.TRANSPARENT)
				.strokeWidth(6)
				.strokeColor(context.getResources().getColor(R.color.warn_red));
				latLngList.addAll(list);
			}
		}
		
		// 管理平台区域
		if(info.managerPositions.size() >= 3) {
			List<LatLng> list = getRange(info.managerPositions);
			if(list != null) {
				managerOptions = new PolygonOptions()
				.addAll(list)
				.fillColor(Color.TRANSPARENT)
				.strokeWidth(6)
				.strokeColor(context.getResources().getColor(R.color.warn_blue));
				latLngList.addAll(list);
			}
		}
		
		// 用户定制服务区域
		if(info.userPositions.size() > 0) {
			LatLng latLng;
			MarkerOptions options;
			for(MyPosition position : info.userPositions) {
				latLng = LatLngFactory.getFormatLatLng(position.latitude, position.longitude);
				options = new MarkerOptions()
				.position(latLng)
				.anchor(0.5f, 0.75f)
				.icon(getUserAreaOptionsIcon(context, position));
				areaOptionsList.add(options);
				
				latLngList.add(latLng);
			}
		}
	}
	
	/**
	 * 获取用户区域图标
	 * @param context
	 * @param position
	 * @return
	 */
	private BitmapDescriptor getUserAreaOptionsIcon(Context context, MyPosition position) {
		LinearLayout layout = (LinearLayout) LayoutInflater.from(context)
				.inflate(R.layout.view_service_area_point, null);
		TextView tv = (TextView) layout.findViewById(R.id.tv);
		
		tv.setText(position.name);
		return BitmapDescriptorFactory.fromView(layout);
	}
	
	/**
	 * 生成预警范围
	 * @param list
	 * @return
	 */
	private List<LatLng> getRange(List<MyPosition> list) {
		List<LatLng> latLngList = new ArrayList<LatLng>();
		LatLng latLng;
		for (MyPosition position : list) {
			latLng = LatLngFactory.getFormatLatLng(position.latitude, position.longitude);
			latLngList.add(latLng);
		}
		return latLngList;
	}
	
	/**
	 * 显示
	 * @param aMap
	 */
	public void show(AMap aMap) {
		if(aMap == null) {
			return;
		}
		
		if(sevenTyphoonOptions != null) {
			sevenTyphoonView = aMap.addCircle(sevenTyphoonOptions);
		}
		if(tenTyphoonOptions != null) {
			tenTyphoonView = aMap.addCircle(tenTyphoonOptions);
		}
		if(otherOptions != null) {
			otherView = aMap.addPolygon(otherOptions);
		}
		if(managerOptions != null) {
			managerView = aMap.addPolygon(managerOptions);
		}
		areaViewList.clear();
		for(MarkerOptions options : areaOptionsList) {
			areaViewList.add(aMap.addMarker(options));
		}
	}
	
	/**
	 * 移除
	 */
	public void remove() {
		if(sevenTyphoonView != null) {
			sevenTyphoonView.remove();
			sevenTyphoonView = null;
		}
		if(tenTyphoonView != null) {
			tenTyphoonView.remove();
			tenTyphoonView = null;
		}
		if(otherView != null) {
			otherView.remove();
			otherView = null;
		}
		if(managerView != null) {
			managerView.remove();
			managerView = null;
		}
		for(Marker marker : areaViewList) {
			marker.remove();
		}
		areaViewList.clear();
	}
	
	/**
	 * 获取视图经纬度列表
	 * @return
	 */
	public List<LatLng> getLatLngList() {
		return latLngList;
	}
	
}
