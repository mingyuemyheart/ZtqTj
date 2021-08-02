package com.pcs.ztqtj.view.activity.product.locationwarning;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.AdapterSetMeal;
import com.pcs.ztqtj.control.tool.LayoutTool;
import com.pcs.ztqtj.control.tool.LoginInformation;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
import com.pcs.ztqtj.view.myview.MyDialog;
import com.pcs.ztqtj.view.myview.MyDialog.DialogListener;
import com.tianyi.shawn.tjdecision.wxapi.wxtools.OrderPayInfo;
import com.tianyi.shawn.tjdecision.wxapi.wxtools.WXInfo;
import com.tianyi.shawn.tjdecision.wxapi.wxtools.WXPay;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackProductDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackProductUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.ProductInfo;
import com.pcs.lib_ztqfj_v2.model.pack.net.ProductSetMealInfo;
import com.pcs.lib_ztqfj_v2.model.pack.net.locationwarning.WarningOrderInfo;
import com.pcs.lib_ztqfj_v2.model.pack.net.order.PackPayOrderDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.order.PackPayOrderUp;

/**
 * 我的预警-支付页
 * @author E.Sun
 * 2015年10月26日
 */
@SuppressLint("InflateParams")
public class ActivityWarningPay extends FragmentActivityZtqBase implements OnClickListener, OnCheckedChangeListener {

	/** 套餐类型按钮 */
	private TextView btnSetMeal;
	
	/** 套餐类型弹窗 */
	private PopupWindow pwSetMeal;
	
	/** 套餐类型弹窗列表 */
	private ListView lvSetMeal;
	
	/** 套餐类型数据适配器 */
	private AdapterSetMeal setMealAdapter;
	
	/** 资费 */
	private TextView tvTariff;
	
	/** 支付方式按钮组 */
	private RadioGroup rgPayType;
	
	/** 确认支付按钮 */
	private Button btnConfirm;
	
	/** 取消支付对话框 */
	private MyDialog exitDialog;
	
	private PackProductUp productUpPack = new PackProductUp();
	private PackPayOrderUp payUpPack = new PackPayOrderUp();
	
	/** 请求微信支付 */
	private final int REQUEST_WEIXIN_PAY = 101;
	
	private final PcsDataBrocastReceiver mReceiver = new PcsDataBrocastReceiver() {
		@Override
		public void onReceive(String nameStr, String errorStr) {
			if(nameStr.equals(productUpPack.getName())) {
				// 服务产品
                PackProductDown downPack = (PackProductDown) PcsDataManager.getInstance().getNetPack(nameStr);
				if (downPack == null) {
					showToast(getString(R.string.error_net));
					dismissProgressDialog();
					disableButton();
					return;
				}
				receiveProductData(downPack);
			} else if(nameStr.equals(payUpPack.getName())) {
				// 支付
                PackPayOrderDown downPack = (PackPayOrderDown) PcsDataManager.getInstance().getNetPack(nameStr);
				if (downPack == null) {
					showToast(getString(R.string.error_net));
					dismissProgressDialog();
					return;
				}
				receivePayData(downPack);
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_warning_pay);
		
		// 注册广播
		PcsDataBrocastReceiver.registerReceiver(this, mReceiver);
		
		initParam();
		initView();
		loadData();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		dismissProgressDialog();
		if (resultCode != RESULT_OK) {
			showToast(getString(R.string.error_pay_order));
			finish();
		}
		
		dismissProgressDialog();
		if (requestCode == REQUEST_WEIXIN_PAY && data != null) {
			// 微信支付返回
			String errStr = data.getStringExtra("errStr");
			int errCode = data.getIntExtra("errCode", -1);
			switch (errCode) {
			case 0: // 支付成功
				setResult(RESULT_OK);
				showToast(getString(R.string.succeed_pay_order));
				finish();
				break;
			case -1: // 支付失败
				showToast(getString(R.string.error_pay_order));
				finish();
				break;
			case -2: // 支付取消
				finish();
				break;
			}
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 注销广播
		PcsDataBrocastReceiver.unregisterReceiver(this, mReceiver);
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
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_set_meal:
			onClickSetMeal();
			break;
		case R.id.btn_confirm:
			onClickConfirm();
			break;
		}
	}
	
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		if(checkedId == R.id.rb_weixin) {
			payUpPack.payType = PackPayOrderUp.PAY_TYPE_WEIXIN;
		} else if(checkedId == R.id.rb_alipay) {
//			payUpPack.payType = PackPayOrderUp.PAY_TYPE_ALIPAY;
		}
	}
	
	/**
	 * 初始化页面参数
	 */
	private void initParam() {
		setMealAdapter = new AdapterSetMeal(this, null);
		payUpPack.orderID = getIntent().getStringExtra(ActivityWarningCustomize.EXTRA_NAME_ORDER_ID);
		WarningOrderInfo orderInfo = (WarningOrderInfo) getIntent().getSerializableExtra(ActivityWarningManage.EXTRA_NAME_ORDER_INFO);
		if(orderInfo != null) {
			payUpPack.orderID = orderInfo.orderID;
		}
	}
	
	/**
	 * 初始化页面
	 */
	private void initView() {
		initTopView();
		initSetMealSpinner();
		tvTariff = (TextView) findViewById(R.id.tv_tariff);
		initRadioGroup();
		initButton();
		initExitDialog();
	}
	
	/**
	 * 初始化顶部标题栏
	 */
	private void initTopView() {
		setTitleText(getString(R.string.pay));
		setBackListener(new BackOnClickListener());
	}
	
	/**
	 * 初始化套餐类型下拉框
	 */
	private void initSetMealSpinner() {
		btnSetMeal = (TextView) findViewById(R.id.btn_set_meal);
		btnSetMeal.setOnClickListener(this);
		initSetMealPopupWindow();
	}
	
	/**
	 * 初始化套餐类型下拉弹窗
	 */
	private void initSetMealPopupWindow() {
		View popView = LayoutInflater.from(this).inflate(
				R.layout.pop_typhoon_list, null);
		lvSetMeal = (ListView) popView.findViewById(R.id.mylistviw);
		lvSetMeal.setAdapter(setMealAdapter);
		lvSetMeal.setOnItemClickListener(new OnSetMealItemClickListener());
		pwSetMeal = new PopupWindow(this);
		pwSetMeal.setContentView(popView);
		pwSetMeal.setWidth(LayoutParams.WRAP_CONTENT);
		pwSetMeal.setHeight(LayoutParams.WRAP_CONTENT);
		pwSetMeal.setFocusable(true);
	}
	
	/**
	 * 初始化单选按钮组
	 */
	private void initRadioGroup() {
		rgPayType = (RadioGroup) findViewById(R.id.rg);
		rgPayType.setOnCheckedChangeListener(this);
		RadioButton rb = (RadioButton) findViewById(R.id.rb_weixin);
		rb.setChecked(true);// 默认选择微信支付
		rb = (RadioButton) findViewById(R.id.rb_alipay);
		rb.setClickable(false);// 暂未支持支付宝支付
		rb.setTextColor(getResources().getColor(R.color.text_gray));
	}
	
	/**
	 * 初始化按钮
	 */
	private void initButton() {
		btnConfirm = (Button) findViewById(R.id.btn_confirm);
		btnConfirm.setOnClickListener(this);
	}
	
	/**
	 * 初始化取消支付对话框
	 */
	private void initExitDialog() {
		TextView tv = getTextDialog();
		tv.setText("你要放弃支付吗？");
		exitDialog = new MyDialog(this, tv, "继续支付", "放弃支付", new DialogListener() {
			@Override
			public void click(String str) {
				if (str.equals("继续支付")) {
					exitDialog.dismiss();
				} else if (str.equals("放弃支付")) {
					exitDialog.dismiss();
					setResult(Activity.RESULT_CANCELED);
					finish();
				}
			}
		});
	}
	
	private void resetSetMealSpinner() {
		resetSetMealPopupWindow();
	}
	
	/**
	 * 重置下拉框弹窗
	 */
	private void resetSetMealPopupWindow() {
		if(btnSetMeal.getWidth() > 0) {
			pwSetMeal.setWidth(btnSetMeal.getWidth());
		} else {
			pwSetMeal.setWidth(LayoutParams.WRAP_CONTENT);
		}
		
		LayoutTool.changeLayoutParams(lvSetMeal, 6);
	}
	
	/**
	 * 加载数据
	 */
	private void loadData() {
		requestProductData();
	}
	
	/**
	 * 请求预警套餐数据
	 */
	private void requestProductData() {

		if(!isOpenNet()){
			showToast(getString(R.string.net_err));
			return ;
		}
		showProgressDialog("");
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
		if(productInfo == null || productInfo.setMealList.size() <= 0) {
			showToast(getString(R.string.hint_no_product));
			finish();
		} else {
			setMealAdapter.setData(productInfo.setMealList);
			resetSetMealSpinner();
			if(productInfo.setMealList.size() > 0) {
				selectSetMeal(productInfo.setMealList.get(0));
			}
		}
	}
	
	/**
	 * 请求支付
	 */
	private void requestPayData() {

		if(!isOpenNet()){
			showToast(getString(R.string.net_err));
			return ;
		}
		showProgressDialog("正在支付...");
		payUpPack.userID = LoginInformation.getInstance().getUserId();
		PcsDataDownload.addDownload(payUpPack);
	}
	
	/**
	 * 接收支付结果
	 * @param downPack
	 */
	private void receivePayData(PackPayOrderDown downPack) {
		dismissProgressDialog();
		if(PackPayOrderDown.RESULT_SUCCEED.equals(downPack.result) && downPack.amount > 0) {
			if(PackPayOrderUp.PAY_TYPE_WEIXIN.equals(downPack.payType)) {
				// 微信支付入口
				WXInfo wxInfo = new WXInfo();
				wxInfo.appid = downPack.appID;
				wxInfo.mch_id = downPack.mchID;
				wxInfo.notify_url = downPack.notifyURL;
				wxInfo.key = downPack.key;
				wxInfo.log_url = downPack.logURL;
				
				OrderPayInfo payInfo = new OrderPayInfo();
				payInfo.order_detail_id = downPack.orderDetailID;
				payInfo.product_name = downPack.productName;
				payInfo.weixin_pay_info = wxInfo;
				payInfo.total_amount = String.valueOf(downPack.amount);
				
//				WXPay pay = new WXPay(this, payInfo, 1, REQUEST_WEIXIN_PAY);// 测试金额1分
				WXPay pay = new WXPay(this, payInfo, downPack.amount);// 订单实际金额
				pay.start();
			}
		} else {
			showToast("支付失败");
			finish();
		}
	}
	
	/**
	 * 点击返回
	 */
	private void onClickBack() {
		exitDialog.show();
	}
	
	/**
	 * 点击套餐类型下拉框
	 */
	private void onClickSetMeal() {
		pwSetMeal.showAsDropDown(btnSetMeal);
	}
	
	/**
	 * 点击确认按钮
	 */
	private void onClickConfirm() {
		if(checkAll()) {
			requestPayData();
		}
	}
	
	/**
	 * 禁用页面按钮
	 */
	private void disableButton() {
		btnConfirm.setEnabled(false);
		btnSetMeal.setTextColor(getResources().getColor(R.color.text_gray_light));
		btnSetMeal.setEnabled(false);
	}
	
	/**
	 * 选择套餐
	 * @param info
	 */
	private void selectSetMeal(ProductSetMealInfo info) {
		if(info != null) {
			btnSetMeal.setText(info.name);
			tvTariff.setText(info.amount + getString(R.string.unit_rmb));
			payUpPack.setMealID = info.id;
		}
	}
	
	/**
	 * 校验
	 * @return
	 */
	private boolean checkAll() {
		if(!checkSetMeal()) {
			showToast(getString(R.string.hint_select_set_meal));
			return false;
		}
		return true;
	}
	
	/**
	 * 校验套餐类型
	 * @return
	 */
	private boolean checkSetMeal() {
        return !TextUtils.isEmpty(payUpPack.setMealID);
    }
	
	/**
	 * 构造对话框提示字段
	 * @return
	 */
	private TextView getTextDialog() {
		TextView tv = (TextView) LayoutInflater.from(this).inflate(
				R.layout.dialog_message, null);
		tv.setGravity(Gravity.CENTER);
		return tv;
	}
	
	/**
	 * 套餐类型弹窗选项点击事件监听器
	 * @author E.Sun
	 * 2015年11月7日
	 */
	private class OnSetMealItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			ProductSetMealInfo info = (ProductSetMealInfo) setMealAdapter.getItem(position);
			selectSetMeal(info);
			pwSetMeal.dismiss();
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
