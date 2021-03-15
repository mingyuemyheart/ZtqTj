package com.pcs.ztqtj.view.activity.product.locationwarning;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.location_warning.AdapterWarningOrder;
import com.pcs.ztqtj.control.listener.OnOrderDeleteListener;
import com.pcs.ztqtj.control.tool.LayoutTool;
import com.pcs.ztqtj.control.tool.LocalDataHelper;
import com.pcs.ztqtj.control.tool.LoginInformation;
import com.pcs.ztqtj.control.tool.image.GetImageView;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
import com.pcs.ztqtj.view.activity.photoshow.ActivityPhotoLogin;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityMain;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackQueryPushTagDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackQueryPushTagUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.locationwarning.PackPersonalWarningDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.locationwarning.PackPersonalWarningUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.locationwarning.WarningOrderInfo;
import com.pcs.lib_ztqfj_v2.model.pack.net.order.PackDeleteOrderDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.order.PackDeleteOrderUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.push.SetPushTagDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.push.SetPushTagUp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 我的预警--管理页
 * @author E.Sun
 * 2015年10月26日
 */
public class ActivityWarningManage extends FragmentActivityZtqBase implements OnClickListener {

	/** 欢迎语 */
	private TextView tvWelcome;

	/** 登陆按钮 */
	private TextView btnLogin;

	/** 用户头像 */
	private ImageView imageUser;

	/** 无预警订单提示语 */
	private TextView tvNoOrder;

	/** 推送开关布局 */
	private LinearLayout layoutPush;

	/** 已支付订单布局 */
	private LinearLayout layoutOrderPaid;
	
	/** 已支付订单列表 */
	private ListView lvPaid;

	/** 已支付订单数据适配器 */
	private AdapterWarningOrder paidOrderAdapter;

	/** 未支付订单布局 */
	private LinearLayout layoutOrderUnpaid;
	
	/** 未支付订单列表 */
	private ListView lvUnpaid;

	/** 未支付订单数据适配器 */
	private AdapterWarningOrder unpaidOrderAdapter;
	
	/** 推送开关 */
	private CheckBox cbPush;

	private PackQueryPushTagUp queryPushUpPack = new PackQueryPushTagUp();
	private SetPushTagUp setPushUpPack = new SetPushTagUp();
	private PackPersonalWarningUp personalWarningUpPack = new PackPersonalWarningUp();
	private PackDeleteOrderUp deleteOrderUpPack = new PackDeleteOrderUp();
	
	/** 订单删除操作监听器 */
	private OnOrderDeleteListener deleteListener;

	/** 图片工具类 */
	private GetImageView imageTool;
	
	/** 初始登陆状态 */
	private boolean hasLogin = false;

	/** 是否支付或修改成功 */
	private boolean isSetSucceed = false;

	/** 跳转到登陆页 */
	private final int REQUEST_LOGIN = 101;

	/** 跳转到预警定制页 */
	private final int REQUEST_CUSTOMIZE = 102;
	
	/** 跳转到预警订单支付页 */
	private final int REQUEST_PAY = 103;
	
	public static final String EXTRA_NAME_ORDER_INFO = "order_info";
	public static final String EXTRA_NAME_TYPE_ID_LIST = "order_id_list";

	private final PcsDataBrocastReceiver mReceiver = new PcsDataBrocastReceiver() {
		@Override
		public void onReceive(String nameStr, String errorStr) {
			if(nameStr.equals(queryPushUpPack.getName())) {
				// 查询推送设置
                PackQueryPushTagDown downPack = (PackQueryPushTagDown) PcsDataManager.getInstance().getNetPack(nameStr);
				if (downPack == null) {
					return;
				}
				receiveQueryPushData(downPack);
			} else if (nameStr.equals(personalWarningUpPack.getName())) {
				// 定点服务订单
                PackPersonalWarningDown downPack = (PackPersonalWarningDown) PcsDataManager.getInstance().getNetPack(nameStr);
				if (downPack == null) {
					showToast(getString(R.string.error_net));
					dismissProgressDialog();
					return;
				}
				receiveOrderData(downPack);
			} else if(nameStr.equals(deleteOrderUpPack.getName())) {
				// 删除订单
                PackDeleteOrderDown downPack = (PackDeleteOrderDown) PcsDataManager.getInstance().getNetPack(nameStr);
				if (downPack == null) {
					showToast(getString(R.string.error_net));
					dismissProgressDialog();
					return;
				}
				receiveDeleteOrder(downPack);
			} else if(nameStr.equals(setPushUpPack.getName())) {
				// 修改推送设置
                SetPushTagDown downPack = (SetPushTagDown) PcsDataManager.getInstance().getNetPack(nameStr);
				if (downPack == null) {
					showToast(getString(R.string.error_net));
					dismissProgressDialog();
					// 回滚操作
					cbPush.setChecked(!cbPush.isChecked());
					return;
				}
				receiveSetPushData(downPack);
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_warning_manage);

		// 注册广播
		PcsDataBrocastReceiver.registerReceiver(this, mReceiver);

		initParam();
		initView();
		resetView();
		loadData();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != Activity.RESULT_OK) {
			return;
		}

		if (requestCode == REQUEST_LOGIN) {
			// 登陆页返回
			resetView();
		} else if (requestCode == REQUEST_CUSTOMIZE || requestCode == REQUEST_PAY) {
			// 预警订购页、预警订单支付页返回
			isSetSucceed = true;
		}
		loadData();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 注销广播
		PcsDataBrocastReceiver.unregisterReceiver(this, mReceiver);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_login:
			onClickLogin();
			break;
		case R.id.layout_customize:
			onClickCustomize();
			break;
		case R.id.btn_customize:
			onClickCustomize();
			break;
		case R.id.cb_push:
			onClickPush();
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// 替换物理返回键点击事件
			onClickBack();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 初始化参数
	 */
	private void initParam() {
		hasLogin = LoginInformation.getInstance().hasLogin();
		imageTool = new GetImageView();
		paidOrderAdapter = new AdapterWarningOrder(this, null);
		unpaidOrderAdapter = new AdapterWarningOrder(this, null);
	}

	/**
	 * 初始化页面
	 */
	private void initView() {
		setBackListener(new BackOnClickListener());
		setTitleText(getString(R.string.title_warning_manage));
		initPersonalCenter();
		initPushLayout();
		initOrderLayout();
		LinearLayout layoutAdd = (LinearLayout) findViewById(R.id.layout_customize);
		layoutAdd.setOnClickListener(this);
		ImageButton button = (ImageButton) findViewById(R.id.btn_customize);
		button.setOnClickListener(this);
	}

	/**
	 * 初始化个人中心
	 */
	private void initPersonalCenter() {
		tvWelcome = (TextView) findViewById(R.id.tv_welcome);
		imageUser = (ImageView) findViewById(R.id.image_user);
		btnLogin = (TextView) findViewById(R.id.btn_login);
		btnLogin.setOnClickListener(this);
	}
	
	/**
	 * 初始化推送开关布局
	 */
	private void initPushLayout() {
		layoutPush = (LinearLayout) findViewById(R.id.layout_push);
		cbPush = (CheckBox) layoutPush.findViewById(R.id.cb_push);
		cbPush.setOnClickListener(this);
	}
	
	/**
	 * 初始化订单布局
	 */
	private void initOrderLayout() {
		tvNoOrder = (TextView) findViewById(R.id.tv_no_order);
		initPaidOrderLayout();
		initUnpaidOrderLayout();
	}
	
	/**
	 * 初始化已支付订单布局
	 */
	private void initPaidOrderLayout() {
		layoutOrderPaid = (LinearLayout) findViewById(R.id.layout_order_paid);
		lvPaid = (ListView) layoutOrderPaid.findViewById(R.id.listview_order_paid);
		lvPaid.setAdapter(paidOrderAdapter);
	}
	
	/**
	 * 初始化未支付订单布局
	 */
	private void initUnpaidOrderLayout() {
		layoutOrderUnpaid = (LinearLayout) findViewById(R.id.layout_order_unpaid);
		lvUnpaid = (ListView) layoutOrderUnpaid.findViewById(R.id.listview_order_unpaid);
		lvUnpaid.setAdapter(unpaidOrderAdapter);
	}

	/**
	 * 重置页面
	 */
	public void resetView() {
		LoginInformation loginInfo = LoginInformation.getInstance();
		if (loginInfo.hasLogin()) {
			showLoginView();
			// 获取用户定点服务推送开关配置
			cbPush.setChecked(true);
			
			LayoutTool.changeLayoutParams(lvPaid);
			LayoutTool.changeLayoutParams(lvUnpaid);
			
			if(paidOrderAdapter.getCount() <= 0 && unpaidOrderAdapter.getCount() <= 0) {
				hideOrderlayout();
			} else {
				if(paidOrderAdapter.getCount() > 0) {
					showPaidOrderLayout();
				} else {
					hidePaidOrderLayout();
				}
				
				if(unpaidOrderAdapter.getCount() > 0) {
					showUnpaidOrderLayout();
				} else {
					hideUnpaidOrderLayout();
				}
			}
		} else {
			showUnloginView();
			hideOrderlayout();
		}
	}

	/**
	 * 加载数据
	 */
	private void loadData() {
//		if (LoginInformation.getInstance().hasLogin()) {
			requestQueryPushData();
			requestOrderData();
//		}
	}

	/**
	 * 点击返回
	 */
	private void onClickBack() {
		// 变更登陆状态或成功支付订单后返回，刷新预警数据展示页
		if (hasLogin != LoginInformation.getInstance().hasLogin()
				|| isSetSucceed) {
			setResult(Activity.RESULT_OK);
		}
		finish();
	}

	/**
	 * 点击登陆
	 */
	private void onClickLogin() {
		Intent intent = new Intent(this, ActivityPhotoLogin.class);
		startActivityForResult(intent, REQUEST_LOGIN);
	}

	/**
	 * 点击预警定制按钮
	 */
	private void onClickCustomize() {
		startCustomizeActivity(null);
	}
	
	/**
	 * 点击推送开关
	 */
	private void onClickPush() {
		requestSetPushData();
	}
	
	/**
	 * 启动预警定制页
	 * @param info
	 */
	public void startCustomizeActivity(WarningOrderInfo info) {
		ArrayList<String> typeIdList = new ArrayList<String>();
		typeIdList.addAll(unpaidOrderAdapter.getTypeIdList());
		typeIdList.addAll(paidOrderAdapter.getTypeIdList());
		
		Intent intent = new Intent(this, ActivityWarningCustomize.class);
		intent.putExtra(EXTRA_NAME_ORDER_INFO, info);
		intent.putExtra(EXTRA_NAME_TYPE_ID_LIST, typeIdList);
		startActivityForResult(intent, REQUEST_CUSTOMIZE);
	}

	/**
	 * 启动预警订单支付页
	 * @param info
	 */
	public void startPayActivity(WarningOrderInfo info) {
		Intent intent = new Intent(this, ActivityWarningPay.class);
		intent.putExtra(EXTRA_NAME_ORDER_INFO, info);
		startActivityForResult(intent, REQUEST_PAY);
	}

	/**
	 * 请求推送设置数据
	 */
	private void requestQueryPushData() {

		if(!isOpenNet()){
			showToast(getString(R.string.net_err));
			return ;
		}
		queryPushUpPack.token = (String) LocalDataHelper.getPushTag(
				getApplicationContext(), "token", String.class);
		PcsDataDownload.addDownload(queryPushUpPack);
	}
	
	/**
	 * 接收推送设置数据
	 */
	private void receiveQueryPushData(PackQueryPushTagDown downPack) {
		String status = downPack.hashMap.get("WP");
		if("0".equals(status)) {
			cbPush.setChecked(false);
		} else {
			cbPush.setChecked(true);
		}
	}
	
	/**
	 * 请求修改推送设置
	 */
	private void requestSetPushData() {

		if(!isOpenNet()){
			showToast(getString(R.string.net_err));
			return ;
		}
		showProgressDialog();
        PackLocalCityMain cityMain = ZtqCityDB.getInstance().getCityMain();
		Map<String, String> params = new HashMap<String, String>();
		setPushUpPack.token = (String) LocalDataHelper.getPushTag(
				getApplicationContext(), "token", String.class);
		if(cbPush.isChecked()) {
			params.put("WP", "1");
		} else {
			params.put("WP", "0");
		}

        params.put("warning_city",cityMain.ID);
        params.put("yjxx_city",cityMain.ID);
        params.put("weatherForecast_city",cityMain.ID);
        setPushUpPack.params = params;
		PcsDataDownload.addDownload(setPushUpPack);
	}
	
	/**
	 * 接收修改推送设置结果
	 * @param downPack
	 */
	private void receiveSetPushData(SetPushTagDown downPack) {
		dismissProgressDialog();
		if(!downPack.result.equals("1")) {
			// 失败时回滚操作
			cbPush.setChecked(!cbPush.isChecked());
		}
	}
	
	/**
	 * 请求预警订单数据
	 */
	private void requestOrderData() {

		if(!isOpenNet()){
			showToast(getString(R.string.net_err));
			return ;
		}
		showProgressDialog();
		personalWarningUpPack.userID = LoginInformation.getInstance()
				.getUserId();
		PcsDataDownload.addDownload(personalWarningUpPack);
	}

	/**
	 * 接收预警订单数据
	 * @param downPack
	 */
	private void receiveOrderData(PackPersonalWarningDown downPack) {
		dismissProgressDialog();
		paidOrderAdapter.setData(downPack.getPaidOrderList());
		unpaidOrderAdapter.setData(downPack.getUnpaidOrderList());
		tvNoOrder.setText(downPack.getTip());
		resetView();
	}

	/**
	 * 请求删除订单
	 * @param orderID
	 */
	public void requestDeleteOrder(String orderID, OnOrderDeleteListener listener) {

		if(!isOpenNet()){
			showToast(getString(R.string.net_err));
			return ;
		}
		deleteListener = listener;
		showProgressDialog("正在删除定点服务订单信息...");
		deleteOrderUpPack.orderID = orderID;
		PcsDataDownload.addDownload(deleteOrderUpPack);
	}
	
	/**
	 * 接收删除订单结果
	 * @param downPack
	 */
	private void receiveDeleteOrder(PackDeleteOrderDown downPack) {
		dismissProgressDialog();
		if(downPack.isDeleteSucceed()) {
			if(deleteListener != null) {
				deleteListener.onDeleteSucceed();
			}
		} else {
			showToast(getString(R.string.error_delete_order));
		}
	}
	
	/**
	 * 显示未登陆视图
	 */
	private void showUnloginView() {
		btnLogin.setVisibility(View.VISIBLE);
		imageUser.setVisibility(View.GONE);
		tvWelcome.setText(getString(R.string.hint_welcome_unlogin));
	}
	
	/**
	 * 显示已登陆视图
	 */
	private void showLoginView() {
		btnLogin.setVisibility(View.GONE);
		imageUser.setVisibility(View.VISIBLE);
		imageTool.setImageView(this, LoginInformation.getInstance().getUserHeadURL(this), imageUser);
		tvWelcome.setText("亲爱的" + LoginInformation.getInstance().getUsername() + "，欢迎你！");
	}
	
	/**
	 * 显示推送开关
	 */
	private void showPushLayout() {
		cbPush.setClickable(true);
		layoutPush.setVisibility(View.VISIBLE);
	}

	/**
	 * 隐藏推送开关
	 */
	private void hidePushLayout() {
		cbPush.setClickable(false);
		cbPush.setChecked(false);
		layoutPush.setVisibility(View.GONE);
	}

	/**
	 * 隐藏订单布局
	 */
	private void hideOrderlayout() {
		showNoOrder();
		hidePaidOrderLayout();
		hideUnpaidOrderLayout();
	}
	
	/**
	 * 显示已支付订单布局
	 */
	public void showPaidOrderLayout() {
		hideNoOrder();
		layoutOrderPaid.setVisibility(View.VISIBLE);
	}
	
	/**
	 * 隐藏已支付订单布局
	 */
	public void hidePaidOrderLayout() {
		layoutOrderPaid.setVisibility(View.GONE);
	}
	
	/**
	 * 显示未支付订单布局
	 */
	public void showUnpaidOrderLayout() {
		hideNoOrder();
		layoutOrderUnpaid.setVisibility(View.VISIBLE);
	}
	
	/**
	 * 隐藏未支付订单布局
	 */
	public void hideUnpaidOrderLayout() {
		layoutOrderUnpaid.setVisibility(View.GONE);
	}

	/**
	 * 显示无预警订单提示语
	 */
	public void showNoOrder() {
		hidePushLayout();
		tvNoOrder.setVisibility(View.VISIBLE);
	}

	/**
	 * 隐藏无预警订单提示语
	 */
	private void hideNoOrder() {
		tvNoOrder.setVisibility(View.GONE);
		showPushLayout();
	}

	/**
	 * 已支付订单布局是否为显示状态
	 * @return
	 */
//	public boolean isPaidOrderLayoutShow() {
//		if(layoutOrderPaid.getVisibility() != View.VISIBLE) {
//			return false;
//		}
//		return true;
//	}
//	
//	/**
//	 * 未支付订单布局是否为显示状态
//	 * @return
//	 */
//	public boolean isUnpaidOrderLayoutShow() {
//		if(layoutOrderUnpaid.getVisibility() != View.VISIBLE) {
//			return false;
//		}
//		return true;
//	}
	
	/**
	 * 虚拟返回键点击事件监听器
	 * 
	 * @author E.Sun 2015年11月11日
	 */
	private class BackOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			onClickBack();
		}
	}

}
