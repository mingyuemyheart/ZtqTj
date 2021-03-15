package com.pcs.ztqtj.view.activity.product.locationwarning;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.OnCameraChangeListener;
import com.amap.api.maps.AMap.OnMapLoadedListener;
import com.amap.api.maps.AMap.OnMapScreenShotListener;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.AdapterLocationWarning;
import com.pcs.ztqtj.control.tool.LayoutTool;
import com.pcs.ztqtj.control.tool.LoginInformation;
import com.pcs.ztqtj.control.tool.NetTask;
import com.pcs.ztqtj.control.tool.PoiOverlay;
import com.pcs.ztqtj.control.tool.ZtqLocationTool;
import com.pcs.ztqtj.control.tool.youmeng.ShareUtil;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
import com.pcs.ztqtj.view.activity.photoshow.ActivityPhotoLogin;
import com.pcs.ztqtj.view.myview.WarningView;
import com.pcs.lib.lib_pcs_v3.control.tool.BitmapUtil;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib.lib_pcs_v3.model.pack.PcsPackDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.locationwarning.LocationWarningInfo;
import com.pcs.lib_ztqfj_v2.model.pack.net.locationwarning.PackAccurateWarningDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.locationwarning.PackAccurateWarningUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.locationwarning.PackPublicWarningDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.locationwarning.PackPublicWarningUp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 气象产品--定点服务
 * 
 * @author YangWj
 * @author E.Sun 2015年10月22日
 */
@SuppressLint({ "InlinedApi", "InflateParams" })
public class ActivityLocationWarning extends FragmentActivityZtqBase implements
		OnClickListener {

	/** 地图 */
	private MapView mapView;

	/** 地图驱动 */
	private AMap aMap;

	/** 公众定点服务按钮 */
	private Button btnPublic;

	/** 公众定点服务列表弹窗 */
	private PopupWindow pwPublic;

	/** 公众定点服务下拉列表 */
	private ListView lvPublic;

	/** 公众定点服务信息框 */
	private LinearLayout layoutPublicInfo, layoutPublicContent;

	/** 公众定点服务标题、有效期、内容 */
	private TextView tvPublicTitle, tvPublicTime, tvPublicContent;

	/** 公众定点服务信息框展开/收起按钮 */
	private ImageView arrowPublicShow, arrowPublicHide;

	/** 精准定点服务按钮 */
	private Button btnAccurate;

	/** 精确定点服务列表弹窗 */
	private PopupWindow pwAccurate;

	/** 精确定点服务下拉列表 */
	private ListView lvAccurate;

	/** 精确定点服务信息框 */
	private LinearLayout layoutAccurateInfo, layoutAccurateContent;

	/** 精确定点服务标题、有效期、内容 */
	private TextView tvAccurateTitle, tvAccurateTime, tvAccurateContent;

	/** 精确定点服务信息框展开/收起按钮 */
	private ImageView arrowAccurateShow, arrowAccurateHide;

	/** 暂无定点服务提示语 */
	private TextView tvHint;

	/** 公众定点服务列表数据适配器 */
	private AdapterLocationWarning publicAdapter;

	/** 精确定点服务列表数据适配器 */
	private AdapterLocationWarning accurateAdapter;

	private PackPublicWarningUp publicUpPack = new PackPublicWarningUp();
	private PackAccurateWarningUp accurateUpPack = new PackAccurateWarningUp();

	/** 公众定点服务范围视图 */
	private Map<String, WarningView> publicViewMap = new HashMap<String, WarningView>();
	/** 精确定点服务范围视图 */
	private Map<String, WarningView> accurateViewMap = new HashMap<String, WarningView>();

	/** 是否自动缩放效果优化 */
	private boolean isZoomToSpan = false;

	/** 是否进行地图加载效果优化 */
	private boolean isOptimize = false;

	/** 是否鉴权 */
	private boolean hasAuth = false;

	/** 地图目标缩放级别 */
	private final float TARGET_ZOOM = 11;

	/** 地图默认缩放级别 */
	private final float DEFAULT_ZOOM = 8;

	/** 跳转到登陆页 */
	private final int REQUEST_LOGIN = 101;

	/** 跳转到预警产品订购页 */
	private final int REQUEST_MANAGE = 102;

	/** 消息：隐藏定位信息提示框 */
	private final int MSG_HIDE_NO_LOCATION = 1001;

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_HIDE_NO_LOCATION:
				hideHint();
				break;
			}
		}
	};

	private final PcsDataBrocastReceiver mReceiver = new PcsDataBrocastReceiver() {
		@Override
		public void onReceive(String nameStr, String errorStr) {
			if (nameStr.equals(publicUpPack.getName())) {
				// 公众定点服务
                PackPublicWarningDown downPack = (PackPublicWarningDown) PcsDataManager.getInstance().getNetPack(nameStr);
				if (downPack == null) {
					showToast(getString(R.string.error_net));
					dismissProgressDialog();
					return;
				}

				receivePublicData(downPack);

				if (LoginInformation.getInstance().hasLogin()) {
					requestAccurateData(LoginInformation.getInstance()
							.getUserId());
				} else {
					dismissProgressDialog();
				}
			} else if (nameStr.equals(accurateUpPack.getName())) {
				// 精确定点服务
                PackAccurateWarningDown downPack = (PackAccurateWarningDown) PcsDataManager.getInstance().getNetPack(nameStr);
				if (downPack == null) {
					showToast(getString(R.string.error_net));
					dismissProgressDialog();
					return;
				}

				receiveAccurateData(downPack);
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location_warning);

		// 注册广播
		PcsDataBrocastReceiver.registerReceiver(this, mReceiver);

		initView();
		resetView();
		initMap(savedInstanceState);
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != Activity.RESULT_OK) {
			// 登陆失败或取消登陆
			return;
		}

		// 登陆页、预警管理页返回
		if (requestCode == REQUEST_LOGIN || requestCode == REQUEST_MANAGE) {
			if (publicAdapter.getCount() > 0) {
				selectPublicWarning((LocationWarningInfo) publicAdapter
						.getItem(0));
			}

			resetAccurateButtonText();
			if (!LoginInformation.getInstance().hasLogin()) {
				accurateAdapter.setData(null);
			} else {
				requestAccurateData(LoginInformation.getInstance().getUserId());
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
		// 注销广播
		PcsDataBrocastReceiver.unregisterReceiver(this, mReceiver);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rightbtn:
			onClickWarningManage();
			break;
		case R.id.btn_location_warning_public:
			onClickPublic();
			break;
		case R.id.btn_location_warning_accurate:
			if (!LoginInformation.getInstance().hasLogin()) {
				onClickLogin();
			} else if (!hasAuth) {
				onClickWarningManage();
			} else {
				onClickAccurate();
			}
			break;
		case R.id.layout_public_info:
			onClickPublicInfoLayout();
			break;
		case R.id.layout_accurate_info:
			onClickAccurateInfoLayout();
			break;
		}
	}

	/**
	 * 点击我的预警
	 */
	private void onClickWarningManage() {
		Intent intent = new Intent(this, ActivityWarningManage.class);
		startActivityForResult(intent, REQUEST_MANAGE);
	}

	/**
	 * 点击登陆
	 */
	private void onClickLogin() {
		Intent intent = new Intent(this, ActivityPhotoLogin.class);
		startActivityForResult(intent, REQUEST_LOGIN);
	}

	/**
	 * 点击大众预警按钮
	 */
	private void onClickPublic() {
		if (publicAdapter.getCount() <= 0) {
			return;
		}

		pwPublic.showAsDropDown(btnPublic, 0, 10);
	}

	/**
	 * 点击精确预警按钮
	 */
	private void onClickAccurate() {
		if (accurateAdapter.getCount() <= 0) {
			return;
		}

		pwAccurate.showAsDropDown(btnAccurate, 0, 10);
	}

	/**
	 * 点击公众定点服务信息框
	 */
	private void onClickPublicInfoLayout() {
		if (layoutPublicContent.getVisibility() == View.GONE) {
			showPublicContentLayout();
		} else {
			hidePublicContentLayout();
		}
	}

	/**
	 * 点击精确定点服务信息框
	 */
	private void onClickAccurateInfoLayout() {
		if (layoutAccurateContent.getVisibility() == View.GONE) {
			showAccurateContentLayout();
		} else {
			hideAccurateContentLayout();
		}
	}

	/**
	 * 初始化页面视图
	 */
	private void initView() {
		initTopLayout();
		initButton();
		initPopupWindow();
		initInfoLayout();
		initHint();
	}

	/**
	 * 初始化顶部布局
	 */
	private void initTopLayout() {
		setTitleText(getString(R.string.title_location_warning));
		setRightTextButton(R.drawable.btn_personal_warning, "我的服务", this);
	}

	/**
	 * 初始化按钮
	 */
	private void initButton() {
		// 公众预警按钮
		btnPublic = (Button) findViewById(R.id.btn_location_warning_public);
		btnPublic.setOnClickListener(this);

		// 精确预警按钮
		btnAccurate = (Button) findViewById(R.id.btn_location_warning_accurate);
		btnAccurate.setOnClickListener(this);
	}

	/**
	 * 初始化列表弹窗
	 */
	@SuppressLint("InlinedApi")
	private void initPopupWindow() {
		initPublicPopupWindow();
		initAccuratePopupWindow();
	}

	/**
	 * 初始化公众定点服务列表弹窗
	 */
	private void initPublicPopupWindow() {
		View view = LayoutInflater.from(this).inflate(
				R.layout.pop_typhoon_list, null);
		lvPublic = (ListView) view.findViewById(R.id.mylistviw);
		publicAdapter = new AdapterLocationWarning(this, null);
		lvPublic.setAdapter(publicAdapter);
		lvPublic.setOnItemClickListener(new OnPublicItemClickListener());
		pwPublic = new PopupWindow(this);
		pwPublic.setWidth(LayoutParams.WRAP_CONTENT);
		pwPublic.setHeight(LayoutParams.WRAP_CONTENT);
		pwPublic.setContentView(view);
		pwPublic.setFocusable(true);
	}

	/**
	 * 初始化精确定点服务列表弹窗
	 */
	private void initAccuratePopupWindow() {
		View view = LayoutInflater.from(this).inflate(
				R.layout.pop_typhoon_list, null);
		ListView listView = (ListView) view.findViewById(R.id.mylistviw);
		accurateAdapter = new AdapterLocationWarning(this, null);
		listView.setAdapter(accurateAdapter);
		listView.setOnItemClickListener(new OnAccurateItemClickListener());
		pwAccurate = new PopupWindow(this);
		pwAccurate.setWidth(LayoutParams.WRAP_CONTENT);
		pwAccurate.setHeight(LayoutParams.WRAP_CONTENT);
		pwAccurate.setContentView(view);
		pwAccurate.setFocusable(true);
	}

	/**
	 * 初始化信息框
	 */
	private void initInfoLayout() {
		initPublicInfoLayout();
		initAccurateInfoLayout();
	}

	/**
	 * 初始化公众定点服务信息框
	 */
	private void initPublicInfoLayout() {
		layoutPublicInfo = (LinearLayout) findViewById(R.id.layout_public_info);
		layoutPublicInfo.setOnClickListener(this);
		layoutPublicContent = (LinearLayout) layoutPublicInfo
				.findViewById(R.id.layout_content);
		tvPublicTitle = (TextView) layoutPublicInfo.findViewById(R.id.tv_title);
		tvPublicTime = (TextView) layoutPublicInfo.findViewById(R.id.tv_time);
		tvPublicContent = (TextView) layoutPublicInfo
				.findViewById(R.id.tv_content);
		arrowPublicShow = (ImageView) layoutPublicInfo
				.findViewById(R.id.arrow_show);
		arrowPublicHide = (ImageView) layoutPublicInfo
				.findViewById(R.id.arrow_hide);
	}

	/**
	 * 初始化精确定点服务信息框
	 */
	private void initAccurateInfoLayout() {
		layoutAccurateInfo = (LinearLayout) findViewById(R.id.layout_accurate_info);
		layoutAccurateInfo.setOnClickListener(this);
		layoutAccurateContent = (LinearLayout) layoutAccurateInfo
				.findViewById(R.id.layout_content);
		tvAccurateTitle = (TextView) layoutAccurateInfo
				.findViewById(R.id.tv_title);
		tvAccurateTime = (TextView) layoutAccurateInfo
				.findViewById(R.id.tv_time);
		tvAccurateContent = (TextView) layoutAccurateInfo
				.findViewById(R.id.tv_content);
		arrowAccurateShow = (ImageView) layoutAccurateInfo
				.findViewById(R.id.arrow_show);
		arrowAccurateHide = (ImageView) layoutAccurateInfo
				.findViewById(R.id.arrow_hide);
	}

	/**
	 * 初始化提示语
	 */
	private void initHint() {
		tvHint = (TextView) findViewById(R.id.tv_hint);
	}

	/**
	 * 初始化地图
	 * 
	 * @param savedInstanceState
	 */
	private void initMap(Bundle savedInstanceState) {
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);
		aMap = mapView.getMap();
		aMap.getUiSettings().setZoomControlsEnabled(false);
		aMap.setOnCameraChangeListener(new MyOnCameraChangeListener());
		aMap.setOnMapLoadedListener(new MyOnMapLoadedListener());
	}

	/**
	 * 重置页面状态
	 */
	private void resetView() {
		resetPublicButtonText();
		resetAccurateButtonText();
		resetInfoLayout();
		resetHint();
	}

	/**
	 * 重置公众定点服务按钮文本
	 */
	private void resetPublicButtonText() {
		setPublicButtonText(getString(R.string.hint_no_public_warning));
	}

	/**
	 * 重置精确定点服务按钮文本
	 */
	private void resetAccurateButtonText() {
		if (!LoginInformation.getInstance().hasLogin()) {
			setAccurateButtonText(getString(R.string.hint_unlogin));
		} else if (!hasAuth) {
			setAccurateButtonText(getString(R.string.hint_uncustomize));
		} else {
			setAccurateButtonText(getString(R.string.hint_no_accurate_warning));
		}
	}

	/**
	 * 重置信息框
	 */
	private void resetInfoLayout() {
		resetPublicInfoLayout();
		resetAccurateInfoLayout();
	}

	/**
	 * 重置公众定点服务信息框
	 */
	private void resetPublicInfoLayout() {
		tvPublicTitle.setText("");
		tvPublicTime.setText("");
		tvPublicContent.setText("");
		hidePublicInfoLayout();
	}

	/**
	 * 重置精确定点服务信息框
	 */
	private void resetAccurateInfoLayout() {
		tvAccurateTitle.setText("");
		tvAccurateTime.setText("");
		tvAccurateContent.setText("");
		hideAccurateInfoLayout();
	}

	/**
	 * 重置定位提示文本
	 */
	private void resetHint() {
		setHint(getString(R.string.hint_search_location));
	}

	/**
	 * 设置定位提示文本
	 * 
	 * @param text
	 */
	private void setHint(String text) {
		tvHint.setText(text);
	}

	/**
	 * 设置公众定点服务按钮文本
	 * 
	 * @param text
	 */
	private void setPublicButtonText(String text) {
		btnPublic.setText(text);
	}

	/**
	 * 设置精确定点服务按钮文本
	 * 
	 * @param text
	 */
	private void setAccurateButtonText(String text) {
		btnAccurate.setText(text);
	}

	/**
	 * 重置公众定点服务下拉框弹窗
	 */
	private void resetPublicPopupWindow() {
		if (btnPublic.getWidth() > 0) {
			pwPublic.setWidth(btnPublic.getWidth());
		} else {
			pwPublic.setWidth(LayoutParams.WRAP_CONTENT);
		}

		LayoutTool.changeLayoutParams(lvPublic, 6);
	}

	/**
	 * 重置精确定点服务下拉框弹窗
	 */
	private void resetAccuratePopupWindow() {
		if (btnAccurate.getWidth() > 0) {
			pwAccurate.setWidth(btnAccurate.getWidth());
		} else {
			pwAccurate.setWidth(LayoutParams.WRAP_CONTENT);
		}

		LayoutTool.changeLayoutParams(lvAccurate, 6);
	}

	/**
	 * 显示公众定点服务信息内容
	 */
	private void showPublicContentLayout() {
		arrowPublicHide.setVisibility(View.VISIBLE);
		arrowPublicShow.setVisibility(View.GONE);
		layoutPublicContent.setVisibility(View.VISIBLE);
	}

	/**
	 * 显示精确定点服务信息内容
	 */
	private void showAccurateContentLayout() {
		arrowAccurateHide.setVisibility(View.VISIBLE);
		arrowAccurateShow.setVisibility(View.GONE);
		layoutAccurateContent.setVisibility(View.VISIBLE);
	}

	/**
	 * 隐藏公众定点服务信息框
	 */
	private void hidePublicInfoLayout() {
		hidePublicContentLayout();
		layoutPublicInfo.setVisibility(View.GONE);
	}

	/**
	 * 隐藏公众定点服务信息内容
	 */
	private void hidePublicContentLayout() {
		layoutPublicContent.setVisibility(View.GONE);
		arrowPublicShow.setVisibility(View.VISIBLE);
		arrowPublicHide.setVisibility(View.GONE);
	}

	/**
	 * 隐藏精确定点服务信息框
	 */
	private void hideAccurateInfoLayout() {
		hideAccurateContentLayout();
		layoutAccurateInfo.setVisibility(View.GONE);
	}

	/**
	 * 隐藏精确定点服务信息内容
	 */
	private void hideAccurateContentLayout() {
		layoutAccurateContent.setVisibility(View.GONE);
		arrowAccurateShow.setVisibility(View.VISIBLE);
		arrowAccurateHide.setVisibility(View.GONE);
	}

	/**
	 * 隐藏提示语
	 */
	private void hideHint() {
		tvHint.setVisibility(View.GONE);
	}

	/**
	 * 更新定位
	 */
	private void refreshLocation() {
		LatLng latLng = ZtqLocationTool.getInstance().getLatLng();
		if (latLng == null) {
			setHint(getString(R.string.hint_no_location));
			mHandler.sendEmptyMessageDelayed(MSG_HIDE_NO_LOCATION, 2000);
			return;
		}
		hideHint();

		MarkerOptions options = new MarkerOptions().position(latLng).icon(
				BitmapDescriptorFactory
						.fromResource(R.drawable.icon_location_02));
		aMap.addMarker(options);

		isOptimize = true;
		aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
	}

	/**
	 * 展示公众定点服务详情
	 * 
	 * @param info
	 *            预警信息
	 */
	private void showPublicInfoLayout(LocationWarningInfo info) {
		tvPublicTitle.setText(info.name);
		tvPublicTime.setText(info.expiryDate);
		tvPublicContent.setText(info.content);
		showPublicInfoLayout();
	}

	/**
	 * 显示公众定点服务信息框
	 */
	private void showPublicInfoLayout() {
		layoutPublicInfo.setVisibility(View.VISIBLE);
		showPublicContentLayout();
	}

	/**
	 * 展示精确定点服务详情
	 * 
	 * @param info
	 *            预警信息
	 */
	private void showAccurateInfoLayout(LocationWarningInfo info) {
		tvAccurateTitle.setText(info.name);
		tvAccurateTime.setText(info.expiryDate);
		tvAccurateContent.setText(info.content);
		showAccurateInfoLayout();
	}

	/**
	 * 显示精确定点服务信息框
	 */
	private void showAccurateInfoLayout() {
		layoutAccurateInfo.setVisibility(View.VISIBLE);
		showAccurateContentLayout();
	}

	/**
	 * 显示公众定点服务视图
	 * 
	 * @param arg0
	 */
	private void showPublicView(String arg0) {
		WarningView view = publicViewMap.get(arg0);
		if (view != null) {
			view.show(aMap);
			List<LatLng> latLngList = view.getLatLngList();
			LatLng latLng = ZtqLocationTool.getInstance().getLatLng();
			if (latLng != null) {
				latLngList.add(latLng);
			}
			zoomToSpan(latLngList);
		}
	}

	/**
	 * 显示精确定点服务视图
	 * 
	 * @param arg0
	 */
	private void showAccurateView(String arg0) {
		WarningView view = accurateViewMap.get(arg0);
		if (view != null) {
			view.show(aMap);
			List<LatLng> latLngList = view.getLatLngList();
			LatLng latLng = ZtqLocationTool.getInstance().getLatLng();
			if (latLng != null) {
				latLngList.add(latLng);
			}
			zoomToSpan(latLngList);
		}
	}

	/**
	 * 移除预警视图
	 * 
	 */
	private void removeWarningView() {
		WarningView view = publicViewMap.get(btnPublic.getText());
		if (view != null) {
			view.remove();
		}
		view = accurateViewMap.get(btnAccurate.getText());
		if (view != null) {
			view.remove();
		}
	}

	/**
	 * 请求公众定点服务数据
	 */
	private void requestPublicData() {
		if(!isOpenNet()){
			showToast(getString(R.string.net_err));
			return ;
		}
		showProgressDialog();
		NetTask task = new NetTask(this, new NetTask.NetListener() {
            @Override
            public void onComplete(PcsPackDown down) {
                if (down == null) {
                    showToast(getString(R.string.error_net));
                    dismissProgressDialog();
                    return;
                }

                receivePublicData((PackPublicWarningDown) down);

                if (LoginInformation.getInstance().hasLogin()) {
                    requestAccurateData(LoginInformation.getInstance()
                            .getUserId());
                } else {
                    dismissProgressDialog();
                }
            }
        });
		task.execute(publicUpPack);
		//PcsDataDownload.addDownload(publicUpPack);
	}

	/**
	 * 接收公众定点服务数据
	 * 
	 * @param downPack
	 */
	private void receivePublicData(PackPublicWarningDown downPack) {
        dismissProgressDialog();
		publicAdapter.setData(downPack.publicList);
		resetPublicPopupWindow();

		publicViewMap.clear();
		WarningView view;
		for (LocationWarningInfo info : downPack.publicList) {
			view = new WarningView(this, info);
			publicViewMap.put(info.name, view);
		}

		if (publicAdapter.getCount() > 0) {
			selectPublicWarning((LocationWarningInfo) publicAdapter.getItem(0));
		}
	}

	/**
	 * 请求精确定点服务数据
	 * 
	 * @param userID
	 */
	private void requestAccurateData(String userID) {
		if(!isOpenNet()){
			showToast(getString(R.string.net_err));
			return ;
		}
		showProgressDialog();
		accurateUpPack.userID = userID;
        NetTask task = new NetTask(this, new NetTask.NetListener() {
            @Override
            public void onComplete(PcsPackDown down) {
                if (down == null) {
                    showToast(getString(R.string.error_net));
                    dismissProgressDialog();
                    return;
                }

                receiveAccurateData((PackAccurateWarningDown) down);
            }
        });
        task.execute(accurateUpPack);
		//PcsDataDownload.addDownload(accurateUpPack);
	}

	/**
	 * 接收精确定点服务数据
	 * 
	 * @param downPack
	 */
	private void receiveAccurateData(PackAccurateWarningDown downPack) {
		dismissProgressDialog();
		// 鉴权处理
		if (!PackAccurateWarningDown.AUTHED.equals(downPack.auth)) {
			hasAuth = false;
			setAccurateButtonText(getString(R.string.hint_uncustomize));
			return;
		}
		hasAuth = true;
		accurateAdapter.setData(downPack.accurateList);
		resetAccuratePopupWindow();

		accurateViewMap.clear();
		WarningView view;
		for (LocationWarningInfo info : downPack.accurateList) {
			view = new WarningView(this, info);
			accurateViewMap.put(info.name, view);
		}

		if (accurateAdapter.getCount() <= 0) {
			setAccurateButtonText(getString(R.string.hint_no_accurate_warning));
		} else {
			selectAccurateWarning((LocationWarningInfo) accurateAdapter
					.getItem(0));
		}
	}

	/**
	 * 选择公众定点服务
	 * 
	 * @param info
	 *            要显示的预警信息
	 */
	private void selectPublicWarning(LocationWarningInfo info) {
		if (info != null) {
			removeWarningView();

			setPublicButtonText(info.name);
			hideAccurateInfoLayout();
			showPublicInfoLayout(info);
			showPublicView(info.name);
		}
	}

	/**
	 * 选择精确定点服务
	 * 
	 * @param info
	 */
	private void selectAccurateWarning(LocationWarningInfo info) {
		if (info != null) {
			removeWarningView();

			setAccurateButtonText(info.name);
			hidePublicInfoLayout();
			showAccurateInfoLayout(info);

			showAccurateView(info.name);
		}
	}

	/**
	 * 自动缩放地图
	 * 
	 * @param latLngList
	 */
	private void zoomToSpan(List<LatLng> latLngList) {
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

		PoiOverlay poiOverlay = new PoiOverlay(aMap, poiItemList);
		poiOverlay.zoomToSpan();
	}

	private class MyOnCameraChangeListener implements OnCameraChangeListener {

		@Override
		public void onCameraChange(CameraPosition arg0) {

		}

		@Override
		public void onCameraChangeFinish(CameraPosition arg0) {
			if (isOptimize) {
				isOptimize = false;
				aMap.animateCamera(CameraUpdateFactory.zoomTo(TARGET_ZOOM),
						2000, null);
			} else if (isZoomToSpan) {
				isZoomToSpan = false;
				aMap.animateCamera(CameraUpdateFactory.zoomOut(), 2000, null);
			}
		}
	}

    /**
	 * 地图加载完成事件监听器
	 * 
	 * @author E.Sun 2015年10月21日
	 */
	private class MyOnMapLoadedListener implements OnMapLoadedListener {

		@Override
		public void onMapLoaded() {
			refreshLocation();
			requestPublicData();
		}
	}

	/**
	 * 公众预警列表项点击事件
	 * 
	 * @author E.Sun 2015年10月22日
	 */
	private class OnPublicItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			LocationWarningInfo info = (LocationWarningInfo) publicAdapter
					.getItem(position);
			if (info != null) {
				pwPublic.dismiss();
				isZoomToSpan = true;
				selectPublicWarning(info);
			}
		}
	}

	/**
	 * 精确预警列表项点击事件
	 * 
	 * @author E.Sun 2015年10月22日
	 */
	private class OnAccurateItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			LocationWarningInfo info = (LocationWarningInfo) accurateAdapter
					.getItem(position);
			if (info != null) {
				pwAccurate.dismiss();
				isZoomToSpan = true;
				selectAccurateWarning(info);
			}
		}
	}

	private Bitmap bitmapMap;

	private void getBitmap() {
		if (mapView != null) {
			mapView.getMap().getMapScreenShot(new OnMapScreenShotListener() {
				@Override
				public void onMapScreenShot(Bitmap arg0) {
					bitmapMap = arg0;
					shareMap(ActivityLocationWarning.this);
				}

                @Override
                public void onMapScreenShot(Bitmap bitmap, int i) {

                }
            });
		}
	}

	/**
	 * 分享
	 */
	@Override
	public void share(final Activity activity) {
		getBitmap();
	}

	private void shareMap(Activity activity) {
		Bitmap bitmap = BitmapUtil.takeScreenShot(activity);
		Canvas canvas = new Canvas(bitmap);
		if (bitmapMap != null) {
			int hight = bitmap.getHeight() - bitmapMap.getHeight();
			canvas.drawBitmap(bitmapMap, 0, hight, null);
		}
		ShareUtil.share(activity, "", bitmap);
	}
}
