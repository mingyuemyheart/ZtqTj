package com.pcs.ztqtj.view.myview.typhoon;

import android.view.animation.LinearInterpolator;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.GroundOverlay;
import com.amap.api.maps.model.GroundOverlayOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.maps.model.animation.Animation;
import com.amap.api.maps.model.animation.RotateAnimation;

import java.util.List;

/**
 * 台风实况路径
 * @author E.Sun
 * 2015年9月9日
 */
public class TyphoonTrueView {
	
	/** 时间 **/
	private String time;
	/** 内容 **/
	private String content;
	/** 实况路径点 **/
	private MarkerOptions truePointOptions;
	/** 风眼 **/
	private MarkerOptions windCenterOptions;
	/** 7级风圈 **/
	private CircleOptions windSevenOptions;
	/** 10级风圈 **/
	private CircleOptions windTenOptions;
	/** 实况路径线 **/
	private PolylineOptions trueLineOptions;

    private GroundOverlayOptions windTenGroudOverlayOptions, windSevenGroudOverlayOptions, windTwelveGroudOverlayOptions;
	
	private Marker truePoint;
	private Polyline trueLine;
	private Marker windCenter;
	private Circle windSeven, windTen;
    private GroundOverlay windTenGroundOverlay, windSevenGroundOverlay, windTwelveGroundOverlay;
	
	public void setTime(String time) {
		this.time = time;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setTruePointOptions(MarkerOptions truePointOptions) {
		this.truePointOptions = truePointOptions;
	}

	public void setWindCenterOptions(MarkerOptions windCenterOptions) {
		this.windCenterOptions = windCenterOptions;
	}

	public void setWindSevenOptions(CircleOptions windSevenOptions) {
		this.windSevenOptions = windSevenOptions;
	}

	public void setWindTenOptions(CircleOptions windTenOptions) {
		this.windTenOptions = windTenOptions;
	}

	public void setTrueLineOptions(PolylineOptions trueLineOptions) {
		this.trueLineOptions = trueLineOptions;
	}

    public void setWindTenGroudOverlayOptions(GroundOverlayOptions options) {
        this.windTenGroudOverlayOptions = options;
    }

    public void setWindTwelveGroudOverlayOptions(GroundOverlayOptions options) {
        this.windTwelveGroudOverlayOptions = options;
    }

    public void setWindSevenGroudOverlayOptions(GroundOverlayOptions options) {
        this.windSevenGroudOverlayOptions = options;
    }

	public String getTime() {
		return time;
	}

	public String getContent() {
		return content;
	}
	
	/**
	 * 获取实况路径经纬度
	 * @return
	 */
	public LatLng getLatLng() {
		if(truePointOptions != null) {
			return truePointOptions.getPosition();
		}
		return null;
	}
	
	/**
	 * 显示实况路径点、实况路径线
	 * @param aMap
	 * @return
	 */
	public String showPoint(AMap aMap) {
		if(aMap == null) {
			return "";
		}
		if(trueLineOptions != null) {
            List<LatLng> list =  trueLineOptions.getPoints();
            PolylineOptions newPolyLineOptions = new PolylineOptions()
                    .addAll(list)
                    .width(trueLineOptions.getWidth())
                    .color(trueLineOptions.getColor());
			trueLine = aMap.addPolyline(newPolyLineOptions);
//            trueLine = aMap.addPolyline(trueLineOptions);
		}
		if(truePointOptions != null) {
			truePoint = aMap.addMarker(truePointOptions);
			return truePoint.getId();
		}
		return "";
	}
	
	/**
	 * 显示风眼、风圈
	 * @param aMap
	 * @return
	 */
	public String showWind(AMap aMap) {
		if(aMap == null) {
			return "";
		}
        if(windTenGroudOverlayOptions != null) {
            windTenGroundOverlay = aMap.addGroundOverlay(windTenGroudOverlayOptions);
        }
        if(windSevenGroudOverlayOptions != null) {
            windSevenGroundOverlay = aMap.addGroundOverlay(windSevenGroudOverlayOptions);
        }
        if(windTwelveGroudOverlayOptions != null) {
            windTwelveGroundOverlay = aMap.addGroundOverlay(windTwelveGroudOverlayOptions);
        }
		if(windSevenOptions != null) {
			windSeven = aMap.addCircle(windSevenOptions);
		}
		if(windTenOptions != null) {
			windTen = aMap.addCircle(windTenOptions);
		}
		if(windCenterOptions != null) {
			windCenter = aMap.addMarker(windCenterOptions);
            Animation animation = new RotateAnimation(windCenter.getRotateAngle(),windCenter.getRotateAngle()+360, 0, 0, 0);
            animation.setDuration(4000);
            animation.setInterpolator(new LinearInterpolator());
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart() {

                }

                @Override
                public void onAnimationEnd() {
                    if(windCenter != null) {
                        windCenter.startAnimation();
                    }
                }
            });
            windCenter.setAnimation(animation);
            windCenter.startAnimation();
			return windCenter.getId();
		}
		return "";
	}
	
	/**
	 * 隐藏实况路径点、实况路径线
	 */
	public void hidePoint() {
		if(truePoint != null) {
			truePoint.remove();
			truePoint = null;
		}
		if(trueLine != null) {
			trueLine.remove();
			trueLine = null;
		}
	}
	
	/**
	 * 隐藏风眼、风圈
	 */
	public void hideWind() {
		if(windCenter != null) {
			windCenter.remove();
			windCenter = null;
		}
		if(windSeven != null) {
			windSeven.remove();
			windSeven = null;
		}
		if(windTen != null) {
			windTen.remove();
			windTen = null;
		}
        if(windTenGroundOverlay != null) {
            windTenGroundOverlay.remove();
            windTenGroundOverlay = null;
        }
        if(windSevenGroundOverlay != null) {
            windSevenGroundOverlay.remove();
            windSevenGroundOverlay = null;
        }
        if(windTwelveGroundOverlay != null) {
            windTwelveGroundOverlay.remove();
            windTwelveGroundOverlay = null;
        }
	}
}
