package com.pcs.ztqtj.view.activity.product.waterflood;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.AMap.OnInfoWindowClickListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.LatLngBounds.Builder;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.waterflood.AdapterWindInfoCount;
import com.pcs.ztqtj.control.tool.ShareTools;
import com.pcs.ztqtj.control.tool.ZtqImageTool;
import com.pcs.ztqtj.control.tool.ZtqLocationTool;
import com.pcs.ztqtj.view.activity.FragmentActivityWithShare;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackShareAboutDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.waterflood.PackWindInfoDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.waterflood.PackWindInfoUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.waterflood.WindInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 风情信息
 * 
 * @author tya
 * 
 */
public class ActivityWindInfo extends FragmentActivityWithShare implements
		OnClickListener, OnMarkerClickListener, OnInfoWindowClickListener,
		InfoWindowAdapter {

	// UI
	/**
	 * 高德地图控件
	 */
	private MapView mMapView = null;

	/**
	 * 风情地图按钮
	 */
	private RadioButton rbMap = null;

	/**
	 * 风情统计按钮
	 */
	private RadioButton rbCount = null;

	/**
	 * 风情统计布局
	 */
	private LinearLayout mWindInfoCountLayout = null;

	/**
	 * 主布局
	 */
	private RelativeLayout mMainLayout = null;

	// 数据
	/**
	 * 高德地图对象
	 */
	private AMap aMap = null;

	/**
	 * 风情信息上传包
	 */
	private PackWindInfoUp windInfoUp = new PackWindInfoUp();

	/**
	 * 风情信息下载包
	 */
	private PackWindInfoDown windInfoDown = new PackWindInfoDown();

	/**
	 * 广播对象
	 */
	private MyReceiver mReceiver = new MyReceiver();

	/**
	 * 地图上所有的marker
	 */
	private List<Marker> markerList = new ArrayList<Marker>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wind_info);
		setTitleText(R.string.title_wind_info);
		initView();
		mMapView.onCreate(savedInstanceState);
		initEvent();
		initData();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mMapView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mMapView.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mReceiver != null) {
			PcsDataBrocastReceiver.unregisterReceiver(this, mReceiver);
		}
		mMapView.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mMapView.onSaveInstanceState(outState);
	}

	/**
	 * 初始化UI
	 */
	private void initView() {
		mMapView = (MapView) findViewById(R.id.map);
		rbMap = (RadioButton) findViewById(R.id.rb_map_wind);
		rbCount = (RadioButton) findViewById(R.id.rb_count_wind);
		mMainLayout = (RelativeLayout) findViewById(R.id.layout_main);
		mWindInfoCountLayout = (LinearLayout) LayoutInflater.from(this)
				.inflate(R.layout.layout_wind_info_count, null);
	}

	/**
	 * 初始化事件
	 */
	private void initEvent() {
		rbMap.setOnClickListener(this);
		rbCount.setOnClickListener(this);
        setShareListener(this);
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		PcsDataBrocastReceiver.registerReceiver(this, mReceiver);
		initMap();
		reqWindInfo();
	}

	/**
	 * 初始化高德地图
	 */
	private void initMap() {
		if (aMap == null) {
			aMap = mMapView.getMap();
			aMap.getUiSettings().setZoomControlsEnabled(false);
		}
		initMapEvent();
		// 定位
		location();
	}

	/**
	 * 地图监听事件
	 */
	private void initMapEvent() {
		aMap.setOnMarkerClickListener(this);
		aMap.setInfoWindowAdapter(this);
		aMap.setOnInfoWindowClickListener(this);
	}

	/**
	 * 定位
	 */
	private void location() {
		LatLng latLng = ZtqLocationTool.getInstance().getLatLng();
		if (latLng != null) {
			aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
		}
	}

	/**
	 * 请求风情信息
	 */
	private void reqWindInfo() {

		if(!isOpenNet()){
			showToast(getString(R.string.net_err));
			return ;
		}
		showProgressDialog();
		if (windInfoUp == null) {
			windInfoUp = new PackWindInfoUp();
		}
		PcsDataDownload.addDownload(windInfoUp);
	}

	private void addMarkersOnMap(List<WindInfo> list) {
		if (list == null) {
			return;
		}
		Builder builder = new Builder();
		clearMarker();
		for (int i = 0; i < list.size(); i++) {
			WindInfo info = list.get(i);
			if (TextUtils.isEmpty(info.lat) || TextUtils.isEmpty(info.log)) {
				continue;
			}
			double lat = Double.parseDouble(info.lat);
			double log = Double.parseDouble(info.log);
			String format = "%s\n时间：%s\n风力：%s\n风速：%s\n风向：%s";
			String str = String.format(format, info.station, info.time,
					info.power, info.speed, info.position);
			str = str.replaceAll(" ", "");
			LatLng latLng = new LatLng(lat, log);
			MarkerOptions markerOptions = new MarkerOptions()
					.position(latLng)
					.anchor(0.5f, 0.5f)
					.snippet(str)
					.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.icon_wind_info_location));
			addMarker(markerOptions, info);
			builder.include(latLng);
		}
		int maxZoom = (int) aMap.getMaxZoomLevel();
		aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(),
				maxZoom));
	}

	/**
	 * 添加marker
	 * 
	 * @param markerOptions
	 */
	private void addMarker(MarkerOptions markerOptions, WindInfo info) {
		Marker marker = aMap.addMarker(markerOptions);
		marker.setObject(info);
		markerList.add(marker);
	}

	/**
	 * 清除所有的marker
	 */
	private void clearMarker() {
		aMap.clear();
		markerList.clear();
	}

	private void showWindInfoLayout() {
		if (windInfoDown.windinfo_list.size() == 0) {
			Toast.makeText(this, "无数据！", Toast.LENGTH_LONG).show();
			return;
		}

		// 隐藏信息窗口
		hideAllInfoWindow();

		ListView listView = (ListView) mWindInfoCountLayout
				.findViewById(R.id.lv_wind_count);
		AdapterWindInfoCount adapter = new AdapterWindInfoCount(this,
				windInfoDown.windinfo_list);
		listView.setAdapter(adapter);

		// 如果没显示布局就显示，反之则关闭
		if (!mWindInfoCountLayout.isShown()) {
			LinearLayout ll = (LinearLayout) findViewById(R.id.ll_bottom_button);
			RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) ll
					.getLayoutParams();
			int bottomHeight = p.height + p.bottomMargin * 2;
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			// params.addRule(RelativeLayout.BELOW, R.id.ll_time_slot);
			params.setMargins(0, 0, 0, bottomHeight);
			mMainLayout.addView(mWindInfoCountLayout, params);
		} else {
			closeView(mWindInfoCountLayout);
		}
	}

	/**
	 * 关闭指定布局
	 * 
	 * @param view
	 */
	private void closeView(View view) {
		if (view.isShown()) {
			mMainLayout.removeView(view);
		}
	}

	/**
	 * 关闭所有布局
	 */
	private void closeAllLayout() {
		if (mWindInfoCountLayout != null) {
			closeView(mWindInfoCountLayout);
		}
	}

	/**
	 * 关闭所有信息窗口
	 */
	private void hideAllInfoWindow() {
		for (int i = 0; i < markerList.size(); i++) {
			Marker marker = markerList.get(i);
			if (marker.isInfoWindowShown()) {
				marker.hideInfoWindow();
			}
		}
	}

	@Override
	public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_right:
                aMap.getMapScreenShot(mScreenShotListener);
                break;
            case R.id.rb_map_wind: { // 风情地图
                closeAllLayout();
                reqWindInfo();
                break;
            }
            case R.id.rb_count_wind: { // 风情统计
                showWindInfoLayout();
                break;
            }
        }
    }

	/**
	 * marker的infoWindow显示回调
	 */
	@Override
	public View getInfoContents(Marker marker) {
		View content = getLayoutInflater().inflate(
				R.layout.layout_wind_info_marker_window, null);
		TextView tv = (TextView) content.findViewById(R.id.tv_info);
		tv.setText(marker.getSnippet());
		return content;
	}

	/**
	 * marker的infoWindow显示回调
	 */
	@Override
	public View getInfoWindow(Marker marker) {
		// View content = getLayoutInflater().inflate(
		// R.layout.layout_wind_info_marker_window, null);
		// TextView tv = (TextView) content.findViewById(R.id.tv_info);
		// tv.setText(marker.getSnippet());
		// return content;
		return null;
	}

	/**
	 * infoWindow的点击回调
	 */
	@Override
	public void onInfoWindowClick(Marker marker) {
		marker.hideInfoWindow();
	}

	/**
	 * marker点击回调
	 */
	@Override
	public boolean onMarkerClick(Marker marker) {
		closeAllLayout();
		WindInfo info = (WindInfo) marker.getObject();
		if (info == null) {
			return true;
		}
		marker.showInfoWindow();
		return true;
	}

    /**
     * 高德地图截屏回调
     */
    private AMap.OnMapScreenShotListener mScreenShotListener = new AMap.OnMapScreenShotListener() {
        @Override
        public void onMapScreenShot(Bitmap bitmap) {
            View layout = findViewById(R.id.layout_main).getRootView();
            mAmapBitmap = bitmap;
            Bitmap bm = ZtqImageTool.getInstance().getScreenBitmapNew(ActivityWindInfo.this);

            int[] location = new int[2];
            mMapView.getLocationOnScreen(location);

            mShareBitmap = procImage(mAmapBitmap, bm, location[1]);
			mShareBitmap = ZtqImageTool.getInstance().stitchQR(ActivityWindInfo.this, mShareBitmap);
            PackShareAboutDown down = (PackShareAboutDown) PcsDataManager.getInstance().getNetPack("wt_share#ABOUT_QXCP_DXFW");
            if(down != null) {
                ShareTools.getInstance(ActivityWindInfo.this).setShareContent(getTitleText(), down.share_content, mShareBitmap,"0").showWindow(layout);
            }
        }

        @Override
        public void onMapScreenShot(Bitmap bitmap, int i) {

        }
    };

	/**
	 * 广播类
	 * 
	 * @author tya
	 * 
	 */
	private class MyReceiver extends PcsDataBrocastReceiver {

		@Override
		public void onReceive(String nameStr, String errorStr) {
			if (TextUtils.isEmpty(nameStr)) {
				return;
			}

			if (!TextUtils.isEmpty(errorStr)) {
				return;
			}

			if (windInfoUp.getName().equals(nameStr)) {
				dismissProgressDialog();
                windInfoDown = (PackWindInfoDown) PcsDataManager.getInstance().getNetPack(nameStr);
				if (windInfoDown == null) {
					return;
				}
				if (windInfoDown.windinfo_list.size() == 0) {
					Toast.makeText(ActivityWindInfo.this, "无数据！",
							Toast.LENGTH_LONG).show();
					return;
				}
				addMarkersOnMap(windInfoDown.windinfo_list);
			}
		}

	}

}
