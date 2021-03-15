package com.pcs.ztqtj.view.myview.typhoon;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polygon;
import com.amap.api.maps.model.PolygonOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.maps.model.Text;
import com.amap.api.maps.model.TextOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * 台风预测路径
 * @author E.Sun
 * 2015年9月9日
 */
public class TyphoonForecastView {
	
	/** 预测时间标记 **/
	private List<TextOptions> timeOptionsList = new ArrayList<>();
    // 点击提示的标记点列表
    private List<MarkerOptions> tipsOptionList = new ArrayList<>();
	/** 预测路径线 **/
	private PolylineOptions lineOptions;
    /** 台风路径概率图 **/
    private PolygonOptions polygonOptions;
    /** 台风路径概率图最后一个点的圆形图 **/
    private CircleOptions lastCircleOptions;
    private List<CircleOptions> lastCircleOptions2;
    // 预测点坐标
    private List<LatLng> forecastLatlngList = new ArrayList<>();
	
	private List<Text> timeList = new ArrayList<>();
    private List<Marker> tipsList = new ArrayList<>();
	private Polyline line;
    private Polygon probabilistiPolygon;
    private Circle lastPoint;
	
	public void addTimeOptions(TextOptions timeOptions) {
		timeOptionsList.add(timeOptions);
	}

    public void addTipsOptions(MarkerOptions tipsOptions) {
        tipsOptionList.add(tipsOptions);
    }

	public void setLineOptions(PolylineOptions lineOptions) {
		this.lineOptions = lineOptions;
	}

    /**
     * 设置台风路径概率图
     */
    public void setProbabilisticChart(PolygonOptions polygonOptions, CircleOptions lastCircleOptions) {
        this.polygonOptions = polygonOptions;
        this.lastCircleOptions = lastCircleOptions;
    }

    public void setProbabilisticChart(PolygonOptions polygonOptions, List<CircleOptions> lastCircleOptions) {
        this.polygonOptions = polygonOptions;
        this.lastCircleOptions2 = lastCircleOptions;
    }

	/**
	 * @param aMap
	 */
	public void show(AMap aMap) {
		if(aMap == null) {
			return;
		}
        for(MarkerOptions options : tipsOptionList) {
            forecastLatlngList.add(options.getPosition());
            tipsList.add(aMap.addMarker(options));
        }
        for(TextOptions options : timeOptionsList) {
            timeList.add(aMap.addText(options));
        }
		if(lineOptions != null) {
            List<LatLng> list =  lineOptions.getPoints();
            PolylineOptions newPolyLineOptions = new PolylineOptions()
                    .addAll(list)
                    .width(lineOptions.getWidth())
                    .color(lineOptions.getColor())
                    .zIndex(lineOptions.getZIndex())
                    .setDottedLine(true);
			line = aMap.addPolyline(newPolyLineOptions);
//            line = aMap.addPolyline(lineOptions);
		}
//		if(polygonOptions != null && lastCircleOptions != null) {
//            probabilistiPolygon = aMap.addPolygon(polygonOptions);
//            lastPoint = aMap.addCircle(lastCircleOptions);
//        }
        if(polygonOptions != null) {
            probabilistiPolygon = aMap.addPolygon(polygonOptions);
        }
        if(lastCircleOptions != null) {
            lastPoint = aMap.addCircle(lastCircleOptions);
        }
        if(lastCircleOptions2 != null) {
            for(CircleOptions circle : lastCircleOptions2) {
                aMap.addCircle(circle);
            }
            //lastPoint = aMap.addCircle(lastCircleOptions);
        }
	}
	
	/**
	 * 显示预测路径线
	 * @param aMap
	 */
	public void showLine(AMap aMap) {
		if(lineOptions != null) {
			line = aMap.addPolyline(lineOptions);
		}
	}
	
	/**
	 * 隐藏预测路径
	 */
	public void hide() {
        // 隐藏时间点
		for(Text Text : timeList) {
            Text.remove();
		}
		timeList.clear();
        // 隐藏提示点
        for(Marker marker : tipsList) {
            marker.remove();
        }
        tipsList.clear();
		if(line != null) {
			line.remove();
			line = null;
		}
		if(probabilistiPolygon != null) {
            probabilistiPolygon.remove();
            probabilistiPolygon = null;
        }
        if(lastPoint != null) {
            lastPoint.remove();
            lastPoint = null;
        }
		forecastLatlngList.clear();
	}

	public List<LatLng> getForecastLatlngList() {
        return forecastLatlngList;
    }
}
