package com.pcs.ztqtj.view.activity.product.traffic;//package com.pcs.knowing_weather.view.activity.product.traffic;
//
//import android.graphics.Bitmap;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.text.TextUtils;
//import android.view.KeyEvent;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.Button;
//import android.widget.ListView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.amap.api.maps.AMap;
//import com.amap.api.maps.AMap.OnMapClickListener;
//import com.amap.api.maps.AMap.OnMarkerClickListener;
//import com.amap.api.maps.CameraUpdateFactory;
//import com.amap.api.maps.MapView;
//import com.amap.api.maps.model.BitmapDescriptorFactory;
//import com.amap.api.maps.model.LatLng;
//import com.amap.api.maps.model.Marker;
//import com.amap.api.maps.model.MarkerOptions;
//import com.amap.api.services.core.LatLonPoint;
//import com.amap.api.services.geocoder.GeocodeResult;
//import com.amap.api.services.geocoder.GeocodeSearch;
//import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
//import com.amap.api.services.geocoder.RegeocodeQuery;
//import com.amap.api.services.geocoder.RegeocodeResult;
//import com.pcs.knowing_weather.R;
//import com.pcs.knowing_weather.control.adapter.AdapterMapForecast;
//import com.pcs.knowing_weather.control.inter.InterfaceInit;
//import com.pcs.knowing_weather.control.tool.PoiOverlay;
//import com.pcs.knowing_weather.control.tool.ShareTools;
//import com.pcs.knowing_weather.control.tool.ZtqImageTool;
//import com.pcs.knowing_weather.control.tool.ZtqLocationTool;
//import com.pcs.knowing_weather.model.TrafficWeatherDB;
//import com.pcs.knowing_weather.model.ZtqCityDB;
//import com.pcs.knowing_weather.model.pack.TrafficHighWay;
//import com.pcs.knowing_weather.view.activity.FragmentActivityWithShare;
//import com.pcs.knowing_weather.view.activity.product.ActivityMapForecast;
//import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
//import com.pcs.lib_ztqfj_v2.model.pack.net.PackRoadDescDown;
//import com.pcs.lib_ztqfj_v2.model.pack.net.PackShareAboutDown;
//
//import java.util.List;
//
///**
// * 交通气象
// *
// * @author JiangZy
// *
// */
//public class ActivityTrafficWeather extends FragmentActivityWithShare {
//	private AMap mAMap;
//	private MapView mMapView;
//	// 交通气象DB
//	private TrafficWeatherDB mTrafficDB = new TrafficWeatherDB();
//	// 当前公路ID
//	private String mCurrId;
//	// 数据适配器
//	private AdapterMapForecast mAdapter = null;
//	// 地理编码搜索
//	private GeocodeSearch mGeocodeSearch;
//
//	/**
//	 * 地图类型
//	 *
//	 * @author JiangZy
//	 *
//	 */
//	private enum MapType {
//		ALL, DETAIL
//	}
//
//	// 当前地图类型
//	private MapType mMapType = MapType.ALL;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_traffic_weather);
//		mMapView = (MapView) findViewById(R.id.map);
//		mMapView.onCreate(savedInstanceState);
//		setTitleText(R.string.traffic_weather);
//        showProgressDialog();
//		// 注册广播接收
//		mTrafficDB.registerReceiver(this);
//		// 初始化地图
//		initMap();
//		// 初始化列表
//		initList();
//        // 初始化事件
//        initEvent();
//		// 初始化地理编码搜索
//		initGeocodeSearch();
//		// 回退按钮
//		setBackListener(mOnClick);
//        // 初始化
//        TrafficTask task = new TrafficTask();
//        task.execute();
//	}
//
//	@Override
//	protected void onResume() {
//		super.onResume();
//		mMapView.onResume();
//		// 注册广播
//		mAdapter.registerReceiver();
//	}
//
//	@Override
//	protected void onPause() {
//		super.onPause();
//		mMapView.onPause();
//		// 注销广播
//		mAdapter.unregisterReceiver();
//	}
//
//	@Override
//	protected void onSaveInstanceState(Bundle outState) {
//		super.onSaveInstanceState(outState);
//		mMapView.onSaveInstanceState(outState);
//	}
//
//	@Override
//	protected void onDestroy() {
//		super.onDestroy();
//		mMapView.onDestroy();
//		// 注销广播接收
//		mTrafficDB.unregisterReceiver(this);
//	}
//
//	/**
//	 * 初始化地图
//	 */
//	private void initMap() {
//		mAMap = mMapView.getMap();
//		// 显示比例尺
//		mAMap.getUiSettings().setScaleControlsEnabled(true);
//	}
//
//	/**
//	 * 初始化列表
//	 */
//	private void initList() {
//		ListView listView = (ListView) findViewById(R.id.listView);
//		mAdapter = new AdapterMapForecast(ActivityMapForecast.this);
//		listView.setAdapter(mAdapter);
//		// 关闭按钮
//		Button btnClose = (Button) findViewById(R.id.btnClose);
//		btnClose.setOnClickListener(mOnClick);
//	}
//
//    /**
//     * 初始化事件
//     */
//    private void initEvent() {
//        setShareListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                switch (v.getId()) {
//                    case R.id.btn_right:
//                        mAMap.getMapScreenShot(mScreenShotListener);
//                        break;
//                }
//            }
//        });
//    }
//
//	/**
//	 * 初始化地理编码搜索
//	 */
//	private void initGeocodeSearch() {
//		mGeocodeSearch = new GeocodeSearch(this);
//		mGeocodeSearch.setOnGeocodeSearchListener(mGeocodeSearchListener);
//	}
//
//	/**
//	 * 显示所有公路
//	 */
//	private void showHighwayAll() {
//		// 标题
//		setTitleText(R.string.traffic_weather);
//		// 隐藏气象概况
//		hiddWeatherDesc();
//		// 清空地图
//		mAMap.clear();
//
//		// 公路列表
//		List<MarkerOptions> markerList = mTrafficDB.getMarkerList();
//
//		for (int i = 0; i < markerList.size(); i++) {
//			MarkerOptions marker = markerList.get(i);
//			// 标记图片
//			TrafficHighWay pack = ZtqCityDB.getInstance().getHighWayByID(
//					marker.getTitle());
//			marker.icon(BitmapDescriptorFactory.fromAsset(pack.IMAGE_PATH));
//			// 添加标记
//			mAMap.addMarker(marker);
//		}
//
//		// 自动缩放地图
//		PoiOverlay poiOverlay = new PoiOverlay(mAMap,
//				mTrafficDB.getPoiItemList());
//		poiOverlay.zoomToSpan();
//		// 地图类型
//		mMapType = MapType.ALL;
//	}
//
//	/**
//	 * 显示公路详情
//	 */
//	private void showHighwayDetail(String id) {
//		mCurrId = id;
//		TrafficHighWay pack = ZtqCityDB.getInstance().getHighWayByID(id);
//		if(pack == null) {
//			return ;
//		}
//		String title = pack.NAME;
//		// 标题
//		setTitleText(title);
//		// 显示气象概况
//		showWeatherDesc(id);
//		// 刷新详情标记
//		refreshDetailMarker(id);
//		// 自动缩放地图
//		PoiOverlay poiOverlay = new PoiOverlay(mAMap,
//				mTrafficDB.getPoiItemDetailList(id));
//		poiOverlay.zoomToSpan();
//		// 地图类型
//		mMapType = MapType.DETAIL;
//	}
//
//	/**
//	 * 隐藏交通气象概况
//	 */
//	private void hiddWeatherDesc() {
//		View view = findViewById(R.id.layout_weather_desc);
//		view.setVisibility(View.GONE);
//	}
//
//	/**
//	 * 显示交通气象概况
//	 */
//	private void showWeatherDesc(String id) {
//		PackRoadDescDown pack = mTrafficDB.getRoadDesc(id);
//		if (pack == null || TextUtils.isEmpty(pack.weather_disc)) {
//			return;
//		}
//		TextView textView = null;
//		// 标题
//		textView = (TextView) findViewById(R.id.text_desc_title);
//		textView.setText(pack.title);
//		// 内容
//		textView = (TextView) findViewById(R.id.text_desc_content);
//		textView.setText(pack.weather_disc);
//		// 布局
//		View view = findViewById(R.id.layout_weather_desc);
//		view.setVisibility(View.VISIBLE);
//	}
//
//	/**
//	 * 刷新详情标记
//	 */
//	private void refreshDetailMarker(String id) {
//		// 清空地图
//		mAMap.clear();
//		// 公路列表
//		for (int i = 0; i < mTrafficDB.getMarkerDetail(id).size(); i++) {
//			MarkerOptions marker = mTrafficDB.getMarkerDetail(id).get(i);
//			// 标记图片
//			TrafficHighWay pack = ZtqCityDB.getInstance().getHighWayByID(id);
//			if(pack != null) {
//				marker.icon(BitmapDescriptorFactory.fromAsset(pack.DETAIL_IMAGE));
//				// 添加标记
//				mAMap.addMarker(marker);
//			}
//		}
//	}
//
//	/**
//	 * 显示数据列表
//	 *
//	 * @param latLng
//	 */
//	private void showDataList(LatLng latLng) {
//		View layout = findViewById(R.id.layout_data);
//		layout.setVisibility(View.VISIBLE);
//		mAdapter.refresh(6, latLng);
//	}
//
//	/**
//	 * 隐藏天气列表
//	 */
//	private void hiddDataList() {
//		View layout = findViewById(R.id.layout_data);
//		layout.setVisibility(View.GONE);
//	}
//
//	/**
//	 * 显示地名
//	 *
//	 * @param addrName
//	 */
//	private void showAddrName(String addrName) {
//		TextView textView = (TextView) findViewById(R.id.text_list_title);
//		textView.setText(addrName);
//	}
//
//	/**
//	 * 调用地理编码搜索（获取地名）
//	 *
//	 * @param latLonPoint
//	 */
//	private void callGeocodeSearch(final LatLonPoint latLonPoint) {
//		// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
//		RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,
//				GeocodeSearch.AMAP);
//		// 设置同步逆地理编码请求
//		mGeocodeSearch.getFromLocationAsyn(query);
//	}
//
//	/**
//	 * 点击回退
//	 */
//	private void clickBack() {
//		if (mMapType == MapType.DETAIL) {
//			// 显示所有公路
//			showHighwayAll();
//			// 隐藏天气列表
//			hiddDataList();
//			return;
//		}
//
//		ActivityTrafficWeather.this.finish();
//	}
//
//	/**
//	 * 获取纬度显示偏移
//	 *
//	 * @return
//	 */
//	private double getLatitudeAdd() {
//		double latitudeAdd = 0d;
//		if (mAMap.getScalePerPixel() > 0) {
//			latitudeAdd = (double) mAMap.getScalePerPixel();
//		}
//
//		latitudeAdd = -latitudeAdd / 800.0d;
//
//		return latitudeAdd;
//	}
//
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		switch (keyCode) {
//		case KeyEvent.KEYCODE_BACK:
//			clickBack();
//			return true;
//		}
//		return super.onKeyDown(keyCode, event);
//	}
//
//	private Handler mHandler = new Handler() {
//        @Override
//        public void dispatchMessage(Message msg) {
//            super.dispatchMessage(msg);
//            showProgressDialog();
//            // 初始化DB
//            mTrafficDB.init(ActivityTrafficWeather.this, mInitListener);
//        }
//    };
//
//	/**
//	 * 初始化回调
//	 */
//	private InterfaceInit mInitListener = new InterfaceInit() {
//		@Override
//		public void initDone(boolean isSucc) {
//			if (!isSucc) {
//				dismissProgressDialog();
//				// 提示失败
//				Toast.makeText(ActivityTrafficWeather.this,
//						R.string.init_error, Toast.LENGTH_SHORT).show();
//				return;
//			}
//			// 显示所有公路
//			showHighwayAll();
//			// 地图点击事件
//			mAMap.setOnMapClickListener(mOnMapClick);
//			// 标记点击事件
//			mAMap.setOnMarkerClickListener(mOnMarkerClick);
//
//			dismissProgressDialog();
//		}
//	};
//
//	/**
//	 * 地图点击事件
//	 */
//	private OnMapClickListener mOnMapClick = new OnMapClickListener() {
//		@Override
//		public void onMapClick(LatLng latLng) {
//			if (mMapType != MapType.DETAIL) {
//				return;
//			}
//
//			// 刷新详情标记
//			refreshDetailMarker(mCurrId);
//			// 当前点击标记
//			MarkerOptions marker = new MarkerOptions();
//			marker.icon(BitmapDescriptorFactory
//					.fromResource(R.drawable.icon_location));
//			marker.position(latLng);
//			mAMap.addMarker(marker);
//			// 移动地图
//			LatLng tempLatLng = new LatLng(latLng.latitude + getLatitudeAdd(),
//					latLng.longitude);
//			mAMap.moveCamera(CameraUpdateFactory.changeLatLng(tempLatLng));
//			// 天气列表
//			showDataList(latLng);
//			// 调用地理编码搜索
//			LatLonPoint point = new LatLonPoint(latLng.latitude,
//					latLng.longitude);
//			callGeocodeSearch(point);
//		}
//	};
//
//	/**
//	 * 标记点击事件
//	 */
//	private OnMarkerClickListener mOnMarkerClick = new OnMarkerClickListener() {
//		@Override
//		public boolean onMarkerClick(Marker marker) {
//			if (mMapType == MapType.ALL) {
//				String id = marker.getTitle();
//				// 显示公路详情
//				showHighwayDetail(id);
//			} else {
//				mOnMapClick.onMapClick(marker.getPosition());
//			}
//
//			return false;
//		}
//	};
//
//	/**
//	 * 按钮点击监听
//	 */
//	private OnClickListener mOnClick = new OnClickListener() {
//		@Override
//		public void onClick(View v) {
//			switch (v.getId()) {
//			case R.id.btnClose:
//				// 关闭按钮
//				hiddDataList();
//				break;
//			case R.id.btn_back:
//				// 回退按钮
//				clickBack();
//				break;
//			}
//		}
//	};
//
//	/**
//	 * 地理编码搜索监听
//	 */
//	private OnGeocodeSearchListener mGeocodeSearchListener = new OnGeocodeSearchListener() {
//		@Override
//		public void onGeocodeSearched(GeocodeResult result, int rCode) {
//		}
//
//		@Override
//		public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
//			if (rCode == ZtqLocationTool.RCODE) {
//				if (result != null
//						&& result.getRegeocodeAddress() != null
//						&& result.getRegeocodeAddress().getFormatAddress() != null) {
//					// 显示地名
//					showAddrName(result.getRegeocodeAddress()
//							.getFormatAddress());
//				}
//			}
//		}
//	};
//
//    private AMap.OnMapScreenShotListener mScreenShotListener = new AMap.OnMapScreenShotListener() {
//        @Override
//        public void onMapScreenShot(Bitmap bitmap) {
//            View layout = findViewById(R.id.layout).getRootView();
//            mAmapBitmap = bitmap;
//            Bitmap bm = ZtqImageTool.getInstance().getScreenBitmapNew(ActivityTrafficWeather.this);
//
//            int top;
//            int[] location = new int[2];
//            mMapView.getLocationOnScreen(location);
//            top = location[1];
//
//            mShareBitmap = procImage(mAmapBitmap, bm, top);
//			mShareBitmap = ZtqImageTool.getInstance().stitchQR(ActivityTrafficWeather.this, mShareBitmap);
//
//            PackShareAboutDown down = (PackShareAboutDown) PcsDataManager.getInstance().getNetPack("wt_share#ABOUT_QXCP_DXFW");
//            if(down != null) {
//                ShareTools.getInstance(ActivityTrafficWeather.this).setShareContent(getTitleText() + " " + down.share_content, mShareBitmap,"0").showWindow(layout);
//            }
//        }
//
//        @Override
//        public void onMapScreenShot(Bitmap bitmap, int i) {
//
//        }
//    };
//
//    private class TrafficTask extends AsyncTask<Void, Void, Void> {
//
//        @Override
//        protected Void doInBackground(Void... params) {
//            // 初始化DB
//            mTrafficDB.init(ActivityTrafficWeather.this, mInitListener);
//            return null;
//        }
//    }
//}
