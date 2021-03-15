package com.pcs.ztqtj.view.activity.product.typhoon;

import android.os.Bundle;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.pcs.ztqtj.view.activity.FragmentActivityWithShare;

public class AMapUtil extends FragmentActivityWithShare {

	protected MapView mapView;
	protected AMap aMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mapView = new MapView(this);
		mapView.onCreate(savedInstanceState);// 此方法必须重写
		if (aMap == null) {
			aMap = mapView.getMap();
		}
		aMap.getUiSettings().setZoomControlsEnabled(false);
		aMap.moveCamera(CameraUpdateFactory.zoomTo(1));
	}

	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
		mapView = null;
	}
}
