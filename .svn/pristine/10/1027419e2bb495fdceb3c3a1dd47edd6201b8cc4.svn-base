package com.pcs.ztqtj.view.activity.product.lightning;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.AdapterNearWarn;
import com.pcs.ztqtj.control.tool.ZtqImageTool;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.net.lightning.NearWarnInfo;
import com.pcs.lib_ztqfj_v2.model.pack.net.lightning.PackNearWarnDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.lightning.PackNearWarnUp;

import java.util.ArrayList;
import java.util.List;

/**
 * 控制器：临近预警
 * @author E.Sun
 * 2015年10月12日
 */
public class ControlNearWarn implements OnClickListener {
	
	private ActivityLightningMonitor mActivity;
	
	/**
	 * 地图驱动
	 */
	private AMap mAMap;
	
	/**
	 * 模块布局
	 */
	private RelativeLayout layoutWarn;
	
	/**
	 * 时间
	 */
	private TextView tvTime;
	
	/**
	 * 图例布局
	 */
	private LinearLayout layoutLegend, layoutLegendDetail; 
	
	/**
	 * 表格布局
	 */
	private LinearLayout layoutTable;
	
	/**
	 * 表格展开/收起按钮
	 */
	private ImageButton btnClose, btnOpen;
	
	/**
	 * 表格文本
	 */
	private TextView tvNull;
	
	/**
	 * 列表
	 */
	private ListView mListView;
	
	/**
	 * 图例展开/收起按钮
	 */
	private ImageButton btnUp, btnDown;
	
	/**
	 * 数据适配器
	 */
	private AdapterNearWarn mAdapter;
	
	private PackNearWarnUp upPack = new PackNearWarnUp();
	
	/**
	 * 预警数据集合
	 */
	private List<MarkerOptions> dataList = new ArrayList<MarkerOptions>();
	
	private final String TAG = "ControlNearWarn";
	
	private PcsDataBrocastReceiver mReceiver = new PcsDataBrocastReceiver() {
		
		@Override
		public void onReceive(String nameStr, String errorStr) {
			if(upPack.getName().equals(nameStr)) {
				// 临近预警
				mActivity.dismissProgressDialog();
				
				if(!TextUtils.isEmpty(errorStr)) {
					Log.e(TAG, "获取临近预警数据失败");
					mActivity.showToast(mActivity.getString(R.string.error_net));
					return;
				}
				
                PackNearWarnDown packDown = (PackNearWarnDown) PcsDataManager.getInstance().getNetPack(nameStr);
                if(packDown == null) {
                    return ;
                }
				receiveData(packDown);
			}
		}
	};
	
	public ControlNearWarn(ActivityLightningMonitor activity, AMap aMap) {
		this.mActivity = activity;
		this.mAMap = aMap;
		init();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.layout_legend:
			clickLegend();
			break;
		case R.id.tv_table_title:
			clickCloseLayout();
			break;
		}
	}
	
	/**
	 * 注册广播
	 */
	public void registerReceiver() {
		PcsDataBrocastReceiver.registerReceiver(mActivity, mReceiver);
	}

	/**
	 * 注销广播
	 */
	public void unregisterReceiver() {
		PcsDataBrocastReceiver.unregisterReceiver(mActivity, mReceiver);
	}
	
	/**
	 * 显示
	 */
	public void show() {
		layoutWarn.setVisibility(View.VISIBLE);
		reset();
		if(dataList.size() <= 0) {
			requestData();
		} else {
			showData();
		}
	}
	
	/**
	 * 隐藏
	 */
	public void hide() {
		layoutWarn.setVisibility(View.GONE);
	}
	
	/**
	 * 初始化
	 */
	private void init() {
		initLayout();
		initLegend();
		initTable();
	}
	
	/**
	 * 初始化布局
	 */
	private void initLayout() {
		layoutWarn = (RelativeLayout) mActivity.findViewById(R.id.layout_warn);
		tvTime = (TextView) layoutWarn.findViewById(R.id.tv_time);
	}
	
	/**
	 * 初始化图例
	 */
	private void initLegend() {
		layoutLegend = (LinearLayout) layoutWarn.findViewById(R.id.layout_legend);
		layoutLegend.setOnClickListener(this);
		layoutLegendDetail = (LinearLayout) layoutWarn.findViewById(R.id.layout_legend_detail);
		btnUp = (ImageButton) layoutWarn.findViewById(R.id.arrow_up);
		btnDown = (ImageButton) layoutWarn.findViewById(R.id.arrow_down);
	}
	
	/**
	 * 初始化表格
	 */
	private void initTable() {
		layoutTable = (LinearLayout) layoutWarn.findViewById(R.id.layout_data);
		TextView tvTitle = (TextView) layoutWarn.findViewById(R.id.tv_table_title);
		tvTitle.setOnClickListener(this);
		tvNull = (TextView) layoutWarn.findViewById(R.id.tv_null);
		btnClose =  (ImageButton) layoutWarn.findViewById(R.id.btn_close);
		btnOpen = (ImageButton) layoutWarn.findViewById(R.id.btn_open);
		mListView = (ListView) layoutWarn.findViewById(R.id.listView);
		mAdapter = new AdapterNearWarn(mActivity, null);
		mListView.setAdapter(mAdapter);
	}
	
	/**
	 * 重置
	 */
	private void reset() {
		resetMap();
		resetTime();
		resetLegend();
		resetTable();
	}
	
	/**
	 * 重置地图
	 */
	private void resetMap() {
		mAMap.clear();
		mActivity.resetLocation();
	}

	/**
	 * 重置时间栏
	 */
	private void resetTime() {
		hideTime();
	}
	
	/**
	 * 重置图例
	 */
	private void resetLegend() {
		showLegend();
	}
	
	/**
	 * 重置表格
	 */
	private void resetTable() {
		hideNull();
		hideTable();
	}
	
	/**
	 * 展开图例
	 */
	private void showLegend() {
		layoutLegendDetail.setVisibility(View.VISIBLE);
		btnUp.setVisibility(View.GONE);
		btnDown.setVisibility(View.VISIBLE);
	}
	
	/**
	 * 收起图例
	 */
	private void hideLegend() {
		layoutLegendDetail.setVisibility(View.GONE);
		btnUp.setVisibility(View.VISIBLE);
		btnDown.setVisibility(View.GONE);
	}
	
	/**
	 * 显示表格数据
	 */
	private void showTable() {
		layoutTable.setVisibility(View.VISIBLE);
		if(mAdapter.getCount() <= 3) {
			mListView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		} else {
			mListView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, mActivity.getResources().getDimensionPixelSize(R.dimen.item_hight) * 3));
		}
	}
	
	/**
	 * 隐藏表格数据
	 */
	private void hideTable() {
		layoutTable.setVisibility(View.GONE);
	}
	
	/**
	 * 显示时间
	 */
	private void showTime() {
		tvTime.setVisibility(View.VISIBLE);
	}
	
	/**
	 * 隐藏时间
	 */
	private void hideTime() {
		tvTime.setVisibility(View.GONE);
	}
	
	/**
	 * 显示空数据文本
	 */
	private void showNull() {
		tvNull.setVisibility(View.VISIBLE);
	}
	
	/**
	 * 隐藏空数据文本
	 */
	private void hideNull() {
		tvNull.setVisibility(View.GONE);
	}
	
	/**
	 * 显示表格关闭按钮
	 */
	private void showCloseButton() {
		btnOpen.setVisibility(View.GONE);
		btnClose.setVisibility(View.VISIBLE);
	}
	
	/**
	 * 显示表格展开按钮
	 */
	private void showOpenButton() {
		btnOpen.setVisibility(View.VISIBLE);
		btnClose.setVisibility(View.GONE);
	}
	
	/**
	 * 点击三维监测图例
	 */
	private void clickLegend() {
		if(layoutLegendDetail.getVisibility() == View.VISIBLE) {
			hideLegend();
		} else {
			showLegend();
		}
	}
	
	/**
	 * 点击展开/收起表格按钮响应区域
	 */
	private void clickCloseLayout() {
		if(btnOpen.getVisibility() == View.VISIBLE) {
			clickOpen();
		} else {
			clickClose();
		}
	}
	
	/**
	 * 点击收起表格按钮按钮
	 */
	private void clickClose() {
		showOpenButton();
		hideTable();
		if(tvNull.getVisibility() != View.VISIBLE) {
			hideTime();
		}
	}
	
	/**
	 * 点击展开表格按钮
	 */
	private void clickOpen() {
		showCloseButton();
		showTable();
		showTime();
	}
	
	/**
	 * 请求数据
	 */
	private void requestData() {
		mActivity.showProgressDialog();
		PcsDataDownload.addDownload(upPack);
	}
	
	/**
	 * 接收数据
	 * @param packDown
	 */
	private void receiveData(PackNearWarnDown packDown) {
		// 未来30分钟内无雷电提示
		if(!TextUtils.isEmpty(packDown.msg)) {
			tvTime.setText(packDown.time);
			showTime();
			tvNull.setText(packDown.msg);
			showNull();
			return;
		}
		
		// 空数据或数据异常
		if(packDown.list.size() <= 0) {
			mActivity.showToast(mActivity.getString(R.string.hint_no_warn_data));
			return;
		}
		
		// 更新页面数据
		MarkerOptions options;
		for(NearWarnInfo info : packDown.list) {
			options = addDataMarker(info);
			if(options != null) {
				dataList.add(options);
			}
		}
		tvTime.setText(packDown.time);
		mAdapter.setData(packDown.list);
		
		showData();
	}
	
	/**
	 * 添加监测数据标记
	 * @param info
	 * @return
	 */
	private MarkerOptions addDataMarker(NearWarnInfo info) {
		if(info == null) {
			return null;
		}
		
		LatLng latLng = new LatLng(info.latitude, info.longitude);
		latLng = mActivity.formatLatLng(latLng);
		// BitmapDescriptor bitmap = null;
        Bitmap bitmap = null;
		if(NearWarnInfo.COLOR_YELLOW.equals(info.color)) {
			bitmap = BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.icon_lightning_warn_30_60);
		} else if(NearWarnInfo.COLOR_ORANGE.equals(info.color)) {
            bitmap = BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.icon_lightning_warn_60_80);
		} else if(NearWarnInfo.COLOR_RED.equals(info.color)) {
            bitmap = BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.icon_lightning_warn_80_100);
		} else {
            bitmap = BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.icon_lightning_warn_legend_0_30);
		}
		
		MarkerOptions options = new MarkerOptions();
		options.anchor(0, 1);
		options.position(latLng);
		options.title(info.area);
		options.snippet(getContent(info));
		if(bitmap != null) {
            bitmap = ZtqImageTool.getInstance().zoomBitmap(bitmap, 1.2f);
            BitmapDescriptor bd = BitmapDescriptorFactory.fromBitmap(bitmap);
			options.icon(bd);
		}
		return options;
	}

	/**
	 * 显示数据
	 */
	private void showData() {
		showTime();
		showCloseButton();
		showTable();
		
		List<LatLng> list = new ArrayList<LatLng>();
		for(MarkerOptions options : dataList) {
			mAMap.addMarker(options);
			list.add(options.getPosition());
		}
	}
	
	/**
	 * 获取弹窗内容
	 * @param info
	 * @return
	 */
	private String getContent(NearWarnInfo info) {
		if(info == null) {
			return null;
		}
		String content = "概率：" + info.probability + "%"
				+ "\n频率：" + info.frequency + "个/分";
		return content;
	}
	
}
