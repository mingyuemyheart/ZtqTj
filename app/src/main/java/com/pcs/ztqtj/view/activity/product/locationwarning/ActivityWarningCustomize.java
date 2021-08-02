package com.pcs.ztqtj.view.activity.product.locationwarning;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.location_warning.AdapterWarningArea;
import com.pcs.ztqtj.control.adapter.location_warning.AdapterWarningType;
import com.pcs.ztqtj.control.tool.LayoutTool;
import com.pcs.ztqtj.control.tool.LoginInformation;
import com.pcs.ztqtj.control.tool.MyConfigure;
import com.pcs.ztqtj.control.tool.image.GetImageView;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
import com.pcs.ztqtj.view.activity.photoshow.ActivityLogin;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCity;
import com.pcs.lib_ztqfj_v2.model.pack.net.AreaInfo;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackProductDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackProductUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.ProductInfo;
import com.pcs.lib_ztqfj_v2.model.pack.net.locationwarning.PackWarningTypeDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.locationwarning.PackWarningTypeUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.locationwarning.WarningOrderInfo;
import com.pcs.lib_ztqfj_v2.model.pack.net.locationwarning.WarningTypeInfo;
import com.pcs.lib_ztqfj_v2.model.pack.net.order.PackSetOrderDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.order.PackSetOrderUp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 我的预警--定制页
 * @author E.Sun
 * 2015年10月26日
 */
@SuppressLint("InflateParams")
public class ActivityWarningCustomize extends FragmentActivityZtqBase implements OnClickListener {

	/** 欢迎语 */
	private TextView tvWelcome;
	
	/** 登陆提示 */
	private TextView tvLogin;
	
	/** 登陆按钮 */
	private TextView btnLogin;
	
	/** 用户头像 */
	private ImageView imageUser;
	
	/** 资费 */
	private TextView tvTariff;
	
	/** 预警类型选择按钮 */
	private TextView btnType;

	/** 预警类型弹窗 */
	private PopupWindow pwType;
	
	/** 预警类型弹窗列表 */
	private ListView lvTpye;
	
	/** 预警区域选择按钮 */
	private TextView btnArea;
	
	/** 预警类型数据适配器 */
	private AdapterWarningType typeAdapter;
	
	/** 预警区域数据适配器 */
	private AdapterWarningArea areaAdapter;
	
	/** 已订制的预警区域列表 */
	private ListView lvArea;
	
	/** 确认按钮 */
	private Button btnConfirm;
	
	/** 订单信息 */
	private WarningOrderInfo orderInfo;
	
	/** 已定制预警类型编号列表 */
	private ArrayList<String> typeIdList;
	
	private PackProductUp productUpPack = new PackProductUp();
	private PackWarningTypeUp typeUpPack = new PackWarningTypeUp();
	private PackSetOrderUp orderUpPack = new PackSetOrderUp();
	
	/** 图片工具类 */
	private GetImageView imageTool;
	
	/** 初始登陆状态 */
	private boolean hasLogin = false;
	
	/** 是否新增/修改订单成功 */
	private boolean isSetOrderSucceed = false;
	
	/** 跳转到登陆页 */
	private final int REQUEST_LOGIN = 101;
	
	/** 跳转到支付页 */
	private final int REQUEST_PAY = 102;
	
	public static final String EXTRA_NAME_ORDER_ID = "order_id";
	
	private final PcsDataBrocastReceiver mReceiver = new PcsDataBrocastReceiver() {
		@Override
		public void onReceive(String nameStr, String errorStr) {
			if(nameStr.equals(productUpPack.getName())) {
				// 服务产品
                PackProductDown downPack = (PackProductDown) PcsDataManager.getInstance().getNetPack(nameStr);
				if (downPack == null) {
					showToast(getString(R.string.error_net));
					dismissProgressDialog();
					return;
				}
				receiveProductData(downPack);
			} else if(nameStr.equals(typeUpPack.getName())) {
				// 预警类型
                PackWarningTypeDown downPack = (PackWarningTypeDown) PcsDataManager.getInstance().getNetPack(nameStr);
				if (downPack == null) {
					showToast(getString(R.string.error_net));
					dismissProgressDialog();
					return;
				}
				receiveWarningTypeData(downPack);
			} else if(nameStr.equals(orderUpPack.getName())) {
				// 新增/修改订单
                PackSetOrderDown downPack = (PackSetOrderDown) PcsDataManager.getInstance().getNetPack(nameStr);
				if (downPack == null) {
					showToast(getString(R.string.error_net));
					dismissProgressDialog();
					return;
				}
				receiveSetOrderData(downPack);
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_warning_customize);
		
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
		if(requestCode == REQUEST_LOGIN && resultCode == Activity.RESULT_OK) {
			// 登陆页返回
			resetView();
			loadData();
		} else if (requestCode == MyConfigure.RESULT_LEFT_CITY_LIST && resultCode == Activity.RESULT_OK) {
			// 选择城市页返回
			PackLocalCity cityInfo = (PackLocalCity) data.getSerializableExtra("cityinfo");
			PackLocalCity parentCity = (PackLocalCity) data.getSerializableExtra("parent_city");
			receiveCityData(cityInfo, parentCity);
		} else if(requestCode == REQUEST_PAY) {
			// 支付页返回
			onClickBack();
		}
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
		case R.id.btn_type:
			if(typeAdapter.getCount() > 0) {
				onClickType();
			} else {
				showToast(getString(R.string.hint_all_warning_type_selected));
			}
			break;
		case R.id.btn_area:
			if(areaAdapter.getCount() < 5) {
				onClickArea();
			}
			break;
		case R.id.btn_confirm:
			onClickConfirm();
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
	 * 初始化页面参数
	 */
	@SuppressWarnings("unchecked")
	private void initParam() {
		hasLogin = LoginInformation.getInstance().hasLogin();
		imageTool = new GetImageView();
		typeAdapter = new AdapterWarningType(this, null);
		areaAdapter = new AdapterWarningArea(this, null);
		orderInfo = (WarningOrderInfo) getIntent().getSerializableExtra(ActivityWarningManage.EXTRA_NAME_ORDER_INFO);
		typeIdList = (ArrayList<String>) getIntent().getSerializableExtra(ActivityWarningManage.EXTRA_NAME_TYPE_ID_LIST);
	}
	
	/**
	 * 初始化页面
	 */
	private void initView() {
		initTopView();
		initPersonalCenter();
		initTypeSpinner();
		btnArea = (TextView) findViewById(R.id.btn_area);
		btnArea.setOnClickListener(this);
		initAreaListView();
		tvTariff = (TextView) findViewById(R.id.tv_tariff);
		btnConfirm = (Button) findViewById(R.id.btn_confirm);
		btnConfirm.setOnClickListener(this);
	}
	
	/**
	 * 初始化顶部标题栏
	 */
	private void initTopView() {
		setBackListener(new BackOnClickListener());
		setTitleText(getString(R.string.title_warning_customize));
	}
	
	/**
	 * 初始化个人中心
	 */
	private void initPersonalCenter() {
		tvWelcome = (TextView) findViewById(R.id.tv_welcome);
		tvLogin = (TextView) findViewById(R.id.tv_login);
		imageUser = (ImageView) findViewById(R.id.image_user);
		btnLogin = (TextView) findViewById(R.id.btn_login);
		btnLogin.setOnClickListener(this);
	}
	
	/**
	 * 初始化预警类型下拉框
	 */
	private void initTypeSpinner() {
		btnType = (TextView) findViewById(R.id.btn_type);
		btnType.setOnClickListener(this);
		initTypePopupWindow();
	}
	
	/**
	 * 初始化预警类型弹窗
	 */
	private void initTypePopupWindow() {
		View popView = LayoutInflater.from(this).inflate(
				R.layout.pop_typhoon_list, null);
		lvTpye = (ListView) popView.findViewById(R.id.mylistviw);
		lvTpye.setAdapter(typeAdapter);
		lvTpye.setOnItemClickListener(new OnTypeItemClickListener());
		pwType = new PopupWindow(this);
		pwType.setContentView(popView);
		pwType.setWidth(LayoutParams.WRAP_CONTENT);
		pwType.setHeight(LayoutParams.WRAP_CONTENT);
		pwType.setFocusable(true);
	}
	
	/**
	 * 初始化预警区域列表
	 */
	private void initAreaListView() {
		lvArea = (ListView) findViewById(R.id.list_view);
		lvArea.setAdapter(areaAdapter);
	}
	
	/**
	 * 重置页面；
	 * <br>未登陆状态：个人中心显示登陆提示语及登陆按钮，禁用类型按钮【选择预警类型】、区域按钮、确认按钮【保存并支付】；
	 * <br>已登录状态：个人中心显示欢迎辞及用户头像；
	 * <br>新增订单：启用类型按钮【选择预警类型】、区域按钮、确认按钮【保存并支付】；
	 * <br>修改未支付订单：禁用类型按钮【预警类型】，启用区域按钮、确认按钮【保存并支付】；
	 * <br>修改已支付订单：禁用类型按钮【预警类型】，启用区域按钮、确认按钮【保存】；
	 */
	private void resetView() {
		LoginInformation loginInfo = LoginInformation.getInstance();
		if (!loginInfo.hasLogin()) {
			showUnloginView();
			disableTypeButton("");
			disableAreaButton();
			disableConfirmButton();
		} else {
			orderUpPack.userID = loginInfo.getUserId();
			showLoginView();
			if(orderInfo == null) {
				// 新增订单
				enableTypeButton();
				setConfirmButtonText(getString(R.string.text_save_pay));
			} else {
				// 修改订单
				orderUpPack.orderID = orderInfo.orderID;
				disableTypeButton(orderInfo.name);
				areaAdapter.setData(orderInfo.areaList);
				
				if(!orderInfo.isPaid) {
					// 修改未支付订单
					setConfirmButtonText(getString(R.string.text_save_pay));
				} else {
					// 修改已支付订单
					setConfirmButtonText(getString(R.string.save));
				}
			}
			enableAreaButton();
			enableConfirmButton();
		}
		
		if(areaAdapter.getCount() > 0) {
			showAreaListView();
		} else {
			hideAreaListView();
		}
	}
	
	/**
	 * 设置资费提示文本
	 * @param text
	 */
	private void setTariffText(String text) {
		tvTariff.setText(text);
	}
	
	/**
	 * 重置预警类型选择按钮文本
	 */
	private void resetTypeButtonText() {
		setTypeButtonText(getString(R.string.text_select_warning_type));
	}
	
	/**
	 * 设置预警类型选择按钮文本
	 * @param text
	 */
	private void setTypeButtonText(String text) {
		btnType.setText(text);
	}
	
	/**
	 * 重置下拉框弹窗
	 */
	private void resetTypePopupWindow() {
		if(btnType.getWidth() > 0) {
			pwType.setWidth(btnType.getWidth());
		} else {
			pwType.setWidth(LayoutParams.WRAP_CONTENT);
		}
		
		LayoutTool.changeLayoutParams(lvTpye, 6);
	}
	
	/**
	 * 重置预警类型下拉框
	 */
	private void resetTypeSpinner() {
		resetTypeButtonText();
		resetTypePopupWindow();
	}
	
	/**
	 * 设置确认按钮文本
	 * @param text
	 */
	private void setConfirmButtonText(String text) {
		btnConfirm.setText(text);
	}
	
	/**
	 * 启用预警类型选择按钮，显示默认文本。
	 */
	private void enableTypeButton() {
		setTypeButtonText(getString(R.string.text_select_warning_type));
		btnType.setTextColor(getResources().getColor(R.color.text_black));
		btnType.setEnabled(true);
	}
	
	/**
	 * 禁用预警类型选择按钮
	 * @param text 显示文本 若text为空则显示默认文本。
	 */
	private void disableTypeButton(String text) {
		if(TextUtils.isEmpty(text)) {
			setTypeButtonText(getString(R.string.text_select_warning_type));
		} else {
			setTypeButtonText(text);
		}
		btnType.setTextColor(getResources().getColor(R.color.text_gray_light));
		btnType.setEnabled(false);
	}
	
	/**
	 * 启用预警区域选择按钮
	 */
	private void enableAreaButton() {
		btnArea.setTextColor(getResources().getColor(R.color.text_black));
		btnArea.setEnabled(true);
	}
	
	/**
	 * 禁用预警区域选择按钮
	 */
	private void disableAreaButton() {
		btnArea.setTextColor(getResources().getColor(R.color.text_gray_light));
		btnArea.setEnabled(false);
	}
	
	/**
	 * 启用确认按钮
	 */
	private void enableConfirmButton() {
		btnConfirm.setEnabled(true);
	}
	
	/**
	 * 禁用确认按钮
	 */
	private void disableConfirmButton() {
		setConfirmButtonText(getString(R.string.text_save_pay));
		btnConfirm.setEnabled(false);
	}
	
	/**
	 * 加载页面数据
	 */
	private void loadData() {
		if(LoginInformation.getInstance().hasLogin()) {
			requestProductData();
			if(orderInfo != null) {
				WarningTypeInfo typeInfo = new WarningTypeInfo();
				typeInfo.id = orderInfo.typeID;
				typeInfo.name = orderInfo.name;
				selectType(typeInfo);
			}
		}
	}
	
	/**
	 * 请求预警套餐数据
	 */
	private void requestProductData() {

		if(!isOpenNet()){
			showToast(getString(R.string.net_err));
			return ;
		}
		showProgressDialog();
		productUpPack.type = PackProductUp.TYPE_LOCATION_WARNING;
		PcsDataDownload.addDownload(productUpPack);
	}
	
	/**
	 * 接收预警套餐数据
	 * @param downPack
	 */
	private void receiveProductData(PackProductDown downPack) {
        dismissProgressDialog();
		ProductInfo productInfo = downPack.getProductInfo(0);
		if(productInfo == null) {
			dismissProgressDialog();
			showToast(getString(R.string.hint_no_product));
			finish();
		} else {
			// 填充资费信息
			orderUpPack.productID = productInfo.id;
			setTariffText(getString(R.string.hint_tariff) + productInfo.amount + 
					getString(R.string.tariff_unit_warning));
			
			if(orderInfo == null) {
				requestWarningTypeData();
			} else {
				dismissProgressDialog();
			}
		}
	}
	
	/**
	 * 请求预警类型数据
	 */
	private void requestWarningTypeData() {

		if(!isOpenNet()){
			showToast(getString(R.string.net_err));
			return ;
		}
		showProgressDialog();
		PcsDataDownload.addDownload(typeUpPack);
	}
	
	/**
	 * 接收预警类型数据
	 * @param downPack
	 */
	private void receiveWarningTypeData(PackWarningTypeDown downPack) {
        dismissProgressDialog();
		List<WarningTypeInfo> list = downPack.getTypeInfoList();
		
		if(list == null || list.size() <= 0) {
			dismissProgressDialog();
			showToast(getString(R.string.hint_no_warning_type));
			finish();
			return;
		}
		
		// 新增订单时过滤已定制预警类型
		if(orderInfo == null) {
			list = filterTypeInfos(list, typeIdList);
		} else {
			// 修改订单
			for(WarningTypeInfo typeInfo : list) {
				if(orderUpPack.typeID.equals(typeInfo.id)) {
					selectType(typeInfo);
					break;
				}
			}
		}
		typeAdapter.setData(list);
		resetTypeSpinner();
		dismissProgressDialog();
	}
	
	/**
	 * 接收城市数据
	 * @param cityInfo
	 */
	private void receiveCityData(PackLocalCity cityInfo, PackLocalCity parentCity) {
		if(cityInfo == null || parentCity == null) {
			return;
		}
		
		showAreaListView();
		AreaInfo areaInfo = new AreaInfo();
		areaInfo.id = cityInfo.ID;
        if(cityInfo.isFjCity) {
            areaInfo.cityName = parentCity.NAME;
            areaInfo.areaName = cityInfo.NAME;
        } else {
            areaInfo.cityName = parentCity.CITY;
            areaInfo.areaName = parentCity.NAME;
        }
		areaAdapter.addData(areaInfo);
	}
	
	/**
	 * 请求新增/修改预警订单
	 */
	private void requestSetOrderData() {
		if(TextUtils.isEmpty(orderUpPack.orderID)) {
			orderUpPack.action = PackSetOrderUp.ACTION_ADD;
		} else {
			orderUpPack.action = PackSetOrderUp.ACTION_UPDATE;
		}

		if(!isOpenNet()){
			showToast(getString(R.string.net_err));
			return ;
		}
		showProgressDialog();
		PcsDataDownload.addDownload(orderUpPack);
	}
	
	/**
	 * 接收新增/修改预警订单结果数据
	 * @param downPack
	 */
	private void receiveSetOrderData(PackSetOrderDown downPack) {
		dismissProgressDialog();
		if(!PackSetOrderDown.RESULT_SUCCEED.equals(downPack.result)) {
			// 操作失败
			isSetOrderSucceed = false;
			showToast(getString(R.string.error_set_order));
			return;
		}
		isSetOrderSucceed = true;
		
		if(PackSetOrderUp.ACTION_UPDATE.equals(orderUpPack.action) && orderInfo.isPaid) {
			// 修改已支付订单
			setResult(RESULT_OK);
			showToast(getString(R.string.succeed_set_order));
			finish();
		} else {
			// 新增/修改未支付订单
			Intent intent = new Intent(this, ActivityWarningPay.class);
			intent.putExtra(EXTRA_NAME_ORDER_ID, downPack.orderID);
			startActivityForResult(intent, REQUEST_PAY);
		}
	}
	
	/**
	 * 点击登陆
	 */
	private void onClickLogin() {
		Intent intent = new Intent(this, ActivityLogin.class);
		startActivityForResult(intent, REQUEST_LOGIN);
	}
	
	/**
	 * 点击预警类型
	 */
	private void onClickType() {
		pwType.showAsDropDown(btnType);
	}
	
	/**
	 * 点击预警区域
	 */
	private void onClickArea() {
//		Intent intent = new Intent(this, ActivityAntherCityList.class);
//		startActivityForResult(intent, MyConfigure.RESULT_LEFT_CITY_LIST);
	}
	
	/**
	 * 点击确认按钮
	 */
	private void onClickConfirm() {
		if(checkAll()) {
			requestSetOrderData();
		}
	}
	
	/**
	 * 点击返回
	 */
	private void onClickBack() {
		// 变更登陆状态或成功支付订单后返回，刷新预警数据展示页
		if (hasLogin != LoginInformation.getInstance().hasLogin()
				|| isSetOrderSucceed) {
			setResult(Activity.RESULT_OK);
		}
		finish();
	}

	/**
	 * 显示未登陆视图
	 */
	private void showUnloginView() {
		btnLogin.setVisibility(View.VISIBLE);
		imageUser.setVisibility(View.GONE);
		tvWelcome.setText(getString(R.string.hint_welcome_unlogin));
		tvLogin.setText(getString(R.string.hint_customize_unlogin));
	}
	
	/**
	 * 显示已登陆视图
	 */
	private void showLoginView() {
		btnLogin.setVisibility(View.GONE);
		imageUser.setVisibility(View.VISIBLE);
		imageTool.setImageView(this, LoginInformation.getInstance().getUserHeadURL(this), imageUser);
		tvWelcome.setText("亲爱的" + LoginInformation.getInstance().getUsername() + "，欢迎你！");
		tvLogin.setText(getString(R.string.hint_customize_login));
	}
	
	/**
	 * 显示预警区域列表
	 */
	private void showAreaListView() {
		lvArea.setVisibility(View.VISIBLE);
	}
	
	/**
	 * 隐藏预警区域列表
	 */
	private void hideAreaListView() {
		lvArea.setVisibility(View.INVISIBLE);
	}
	
	/**
	 * 选择预警区域
	 * @param info
	 */
	private void selectType(WarningTypeInfo info) {
		if(info != null) {
			orderUpPack.typeID = info.id;
			setTypeButtonText(info.name);
		}
	}
	
	/**
	 * 校验
	 * @return
	 */
	private boolean checkAll() {
		if(!checkType()) {
			showToast(getString(R.string.hint_select_warning_type));
			return false;
		}
		if(!checkArea()) {
			showToast(getString(R.string.hint_select_warning_area));
			return false;
		}
		orderUpPack.areaIdList = areaAdapter.getAreaIdList();
		return true;
	}
	
	/**
	 * 校验预警类型
	 * @return
	 */
	private boolean checkType() {
        return !TextUtils.isEmpty(orderUpPack.typeID);
    }
	
	/**
	 * 校验预警区域
	 * @return
	 */
	private boolean checkArea() {
        return areaAdapter.getCount() > 0;
    }
	
	/**
	 * 过滤已定制预警类型
	 * @param infoList
	 * @param idList
	 * @return
	 */
	private List<WarningTypeInfo> filterTypeInfos(List<WarningTypeInfo> infoList, List<String> idList) {
		if(idList != null) {
			Iterator<WarningTypeInfo> iterator = infoList.iterator();
			WarningTypeInfo typeInfo;
			while (iterator.hasNext()) {
				typeInfo = iterator.next();
		        for(String id : idList) {
					if(typeInfo.id.equals(id)) {
						iterator.remove();
						break;
					}
				}
		    }
		}
		return infoList;
	}
	
	/**
	 * 预警类型弹窗选项点击事件监听器
	 * @author E.Sun
	 * 2015年11月7日
	 */
	private class OnTypeItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			WarningTypeInfo typeInfo = (WarningTypeInfo) typeAdapter.getItem(position);
			if(typeInfo.id.equals(orderUpPack.typeID)) {
				return;
			}
			selectType(typeInfo);
			pwType.dismiss();
		}
	}

	/**
	 * 虚拟返回键点击事件监听器
	 * @author E.Sun
	 * 2015年11月11日
	 */
	private class BackOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			onClickBack();
		}
	}
	
}
