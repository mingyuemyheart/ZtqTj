package com.pcs.ztqtj.view.activity.product.lightning;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.OnCameraChangeListener;
import com.amap.api.maps.AMap.OnMapLoadedListener;
import com.amap.api.maps.AMap.OnMapTouchListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackShareAboutDown;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.PoiOverlay;
import com.pcs.ztqtj.control.tool.ShareTools;
import com.pcs.ztqtj.control.tool.ZtqImageTool;
import com.pcs.ztqtj.control.tool.ZtqLocationTool;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;

import java.util.ArrayList;
import java.util.List;

/**
 * 监测预报-闪电定位
 */
public class ActivityLightningMonitor extends FragmentActivityZtqBase implements OnClickListener {

	private ControlThirdMonitor controlMonitor;
	private ControlNearWarn controlWarn;
	private ControlDefenseGuide controlGuide;

	private MapView mMapView;
	private AMap mAMap;

	/**
	 * 标签
	 */
	private RelativeLayout label1, label2, label3;

	/**
	 * 下划线
	 */
	private View bottomLine1, bottomLine2, bottomLine3;

	/** 是否进行自动缩放效果优化 */
	private boolean isOptimize = false;

    // 高德地图截图
    protected Bitmap mAmapBitmap;
    // 分享内容
    protected Bitmap mShareBitmap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lightning_monitor);
		initMap(savedInstanceState);
		initParam();
		initView();
        initEvent();
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
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mMapView.onSaveInstanceState(outState);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		controlWarn.unregisterReceiver();
		controlGuide.unregisterReceiver();
		mMapView.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.label1:
			clickLabel1();
			break;
		case R.id.label2:
			clickLabel2();
			break;
		case R.id.label3:
			clickLabel3();
			break;
		}
	}

	/**
	 * 初始化参数
	 */
	private void initParam() {
		controlMonitor = new ControlThirdMonitor(this, mAMap);
		controlWarn = new ControlNearWarn(this, mAMap);
		controlWarn.registerReceiver();
		controlGuide = new ControlDefenseGuide(this);
		controlGuide.registerReceiver();
	}

	/**
	 * 初始化地图
	 */
	private void initMap(Bundle savedInstanceState) {
		mMapView = (MapView) findViewById(R.id.map);
		mMapView.onCreate(savedInstanceState);
		mAMap = mMapView.getMap();
		mAMap.setOnCameraChangeListener(new MyOnCameraChangeListener());
		mAMap.setOnMapLoadedListener(new MyOnMapLoadedListener());
		mAMap.setOnMapTouchListener(new MyOnMapTouchListener());
		mAMap.setOnMarkerClickListener(new MyOnMarkerClickListener());

		UiSettings set = mAMap.getUiSettings();
		set.setZoomControlsEnabled(false);// 禁用缩放按钮
		set.setRotateGesturesEnabled(false);// 禁止旋转
	}

	/**
	 * 初始化页面视图
	 */
	private void initView() {
		setTitleText(R.string.lightning_monitor);
		initLabel();
	}

    /**
     * 初始化事件
     */
    private void initEvent() {
        setBtnRight(R.drawable.icon_share_new, new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 当前页面是数据查询
                if(bottomLine3.getVisibility() == View.VISIBLE) {
                    String share = "";
                    PackShareAboutDown down = (PackShareAboutDown) PcsDataManager.getInstance().getNetPack("wt_share#ABOUT_QXCP_DXFW");
                    if(down == null) {
                        return;
                    }
                    share = down.share_content;
                    View layout = findViewById(R.id.layout_main).getRootView();
                    mShareBitmap = ZtqImageTool.getInstance().getScreenBitmapNew(ActivityLightningMonitor.this);
					mShareBitmap = ZtqImageTool.getInstance().stitchQR(ActivityLightningMonitor.this, mShareBitmap);
                    ShareTools.getInstance(ActivityLightningMonitor.this).setShareContent(getTitleText(), share, mShareBitmap,"0").showWindow(layout);
                } else {// 不是数据查询页面时
                    mAMap.getMapScreenShot(mScreenShotListener);
                }
            }
        });
    }

	/**
	 * 初始化标签
	 */
	private void initLabel() {
		label1 = (RelativeLayout) findViewById(R.id.label1);
		label1.setOnClickListener(this);
		label2 = (RelativeLayout) findViewById(R.id.label2);
		label2.setOnClickListener(this);
		label3 = (RelativeLayout) findViewById(R.id.label3);
		label3.setOnClickListener(this);
		bottomLine1 = findViewById(R.id.bottom_line1);
		bottomLine2 = findViewById(R.id.bottom_line2);
		bottomLine3 = findViewById(R.id.bottom_line3);
	}

	/**
	 * 点击三维监测
	 */
	private void clickLabel1() {
		if (bottomLine1.getVisibility() == View.VISIBLE) {
			return;
		}
		bottomLine1.setVisibility(View.VISIBLE);
		bottomLine2.setVisibility(View.GONE);
		bottomLine3.setVisibility(View.GONE);
		controlMonitor.show();
		controlWarn.hide();
		controlGuide.hide();
	}

	/**
	 * 点击临近预警
	 */
	private void clickLabel2() {
		if (bottomLine2.getVisibility() == View.VISIBLE) {
			return;
		}
		bottomLine1.setVisibility(View.GONE);
		bottomLine2.setVisibility(View.VISIBLE);
		bottomLine3.setVisibility(View.GONE);
		controlMonitor.hide();
		controlWarn.show();
		controlGuide.hide();
	}

	/**
	 * 点击防御指南
	 */
	private void clickLabel3() {
		if (bottomLine3.getVisibility() == View.VISIBLE) {
			return;
		}
		bottomLine1.setVisibility(View.GONE);
		bottomLine2.setVisibility(View.GONE);
		bottomLine3.setVisibility(View.VISIBLE);
		controlMonitor.hide();
		controlWarn.hide();
		controlGuide.show();
	}

	/**
	 * 重置定位信息
	 */
	public void resetLocation() {
//        LatLng latLng=new LatLng(  34.2345123624, 107.7539062500);
		LatLng latLng = ZtqLocationTool.getInstance().getLatLng();
		if (latLng != null) {
			isOptimize = true;
			mAMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,5));
		}
	}

	/**
	 * 格式化经纬度坐标
	 * @param latLng
	 * @return
	 */
	public LatLng formatLatLng(LatLng latLng) {
		if (latLng == null) {
			return null;
		}

		double latitude, longitude;
		if (latLng.latitude < -90) {
			latitude = -90;
		} else if (latLng.latitude > 90) {
			latitude = 90;
		} else {
			latitude = latLng.latitude;
		}
		if (latLng.longitude <= -180) {
			longitude = -179.999999;
		} else if (latLng.longitude >= 180) {
			longitude = 179.999999;
		} else {
			longitude = latLng.longitude;
		}
		return new LatLng(latitude, longitude);
	}

	/**
	 * 自动缩放地图
	 * @param latLngList
	 */
	public void zoomToSpan(List<LatLng> latLngList) {
		if (latLngList.size() < 2) {
			return;
		}

		List<PoiItem> poiItemList = new ArrayList<PoiItem>();
		LatLonPoint point;
		for (LatLng latLng : latLngList) {
			if (latLng == null) {
				continue;
			}
			point = new LatLonPoint(latLng.latitude, latLng.longitude);
			PoiItem poiItem = new PoiItem("", point, "", "");
			poiItemList.add(poiItem);
		}

		PoiOverlay poiOverlay = new PoiOverlay(mAMap, poiItemList);
		poiOverlay.zoomToSpan();
	}

    /**
     * 获取状态栏高度
     * @return
     */
    protected int getStatusBarHeight() {
        int statusBarHeight1 = 0;
        //获取status_bar_height资源的ID
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight1 = getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight1;
    }

    /**
     * 处理图片
     * @param bm1 高德地图图片
     * @param bm2 UI图片
     * @return
     */
    protected Bitmap procImage(Bitmap bm1, Bitmap bm2, int top) {
        // 创建上层UI图片大小的新图片
        Bitmap newBm = Bitmap.createBitmap(bm2.getWidth(), bm2.getHeight(), bm2.getConfig());
        // 创建画布
        Canvas canvas = new Canvas(newBm);
        // 先画底层高德地图图片
        canvas.drawBitmap(bm1, 0, top, null);
        canvas.drawBitmap(bm2, 0, 0, null);
        return newBm;
    }

    /**
     * 高德地图截屏回调
     */
    private AMap.OnMapScreenShotListener mScreenShotListener = new AMap.OnMapScreenShotListener() {
        @Override
        public void onMapScreenShot(Bitmap bitmap) {
            View layout = findViewById(R.id.layout_main).getRootView();
            mAmapBitmap = bitmap;
            Bitmap bm = ZtqImageTool.getInstance().getScreenBitmapNew(ActivityLightningMonitor.this);

            int[] location = new int[2];
            mMapView.getLocationOnScreen(location);
            int top = location[1] - getStatusBarHeight();

            mShareBitmap = procImage(mAmapBitmap, bm, top);
			mShareBitmap = ZtqImageTool.getInstance().stitchQR(ActivityLightningMonitor.this, mShareBitmap);
            PackShareAboutDown down = (PackShareAboutDown) PcsDataManager.getInstance().getNetPack("wt_share#ABOUT_QXCP_DXFW");
            if(down != null) {
                ShareTools.getInstance(ActivityLightningMonitor.this).setShareContent(getTitleText(), down.share_content, mShareBitmap,"0").showWindow(layout);
            }
        }

        @Override
        public void onMapScreenShot(Bitmap bitmap, int i) {

        }
    };

	/**
	 * 标记点点击事件
	 * @author E.Sun 2015年11月9日
	 */
	private class MyOnMarkerClickListener implements OnMarkerClickListener {

		@Override
		public boolean onMarkerClick(Marker arg0) {
			return false;
		}
	}

	/**
	 * 地图加载动画
	 * @author E.Sun 2015年11月9日
	 */
	private class MyOnCameraChangeListener implements OnCameraChangeListener {

		@Override
		public void onCameraChange(CameraPosition arg0) {

		}

		@Override
		public void onCameraChangeFinish(CameraPosition arg0) {
			if (isOptimize) {
				isOptimize = false;
//				mAMap.animateCamera(
//						CameraUpdateFactory.zoomTo(arg0.zoom + 2.8f), 2000,
//						null);
			}
		}
	}

	/**
	 * 地图加载完成回调事件
	 * @author E.Sun 2015年10月13日
	 */
	private class MyOnMapLoadedListener implements OnMapLoadedListener {

		@Override
		public void onMapLoaded() {
			clickLabel1();
		}
	}

	private class MyOnMapTouchListener implements OnMapTouchListener {

		@Override
		public void onTouch(MotionEvent arg0) {
			// 隐藏视野内弹窗
			List<Marker> list = mAMap.getMapScreenMarkers();
			for (Marker marker : list) {
				marker.hideInfoWindow();
			}
		}
	}

}
