package com.pcs.ztqtj.view.activity.product.lightning;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.MarkerOptions;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.net.lightning.PackThirdMonitorDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.lightning.PackThirdMonitorUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.lightning.ThirdMonitorInfo;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.ZtqImageTool;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 控制器：三维监测
 * @author E.Sun
 * 2015年10月12日
 */
@SuppressLint("SimpleDateFormat")
public class ControlThirdMonitor implements OnClickListener {

	private ActivityLightningMonitor mActivity;
	
	/**
	 * 地图驱动
	 */
	private AMap mAMap;
	
	/**
	 * 模块布局
	 */
	private RelativeLayout layoutMonitor;
	
	/**
	 * 图例布局
	 */
	private LinearLayout layoutLegend, layoutLegendDetail; 
	
	/**
	 * 图例图标
	 */
	private ImageView imageCloud, imagePositive, imageNegative;
	
	/**
	 * 展开/收起按钮
	 */
	private ImageButton btnUp, btnDown;
	
	private PackThirdMonitorUp upPack = new PackThirdMonitorUp();
	
	/**
	 * 监测数据集合
	 */
	private List<MarkerOptions> dataList = new ArrayList<MarkerOptions>();
	
	private final String TAG = "ControlThirdMonitor";
	
	private PcsDataBrocastReceiver mReceiver = new PcsDataBrocastReceiver() {
		
		@Override
		public void onReceive(String nameStr, String errorStr) {
			if(upPack.getName().equals(nameStr)) {
				// 三维监测
				mActivity.dismissProgressDialog();
				
				if(!TextUtils.isEmpty(errorStr)) {
					Log.e(TAG, "获取三维监测数据失败");
					mActivity.showToast(mActivity.getString(R.string.error_net));
					return;
				}
				
                PackThirdMonitorDown packDown = (PackThirdMonitorDown) PcsDataManager.getInstance().getNetPack(nameStr);
                if(packDown == null) {
                    return ;
                }
				receiveData(packDown);
			}
		}
	};
	
	public ControlThirdMonitor(ActivityLightningMonitor activity, AMap aMap) {
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
		layoutMonitor.setVisibility(View.VISIBLE);
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
		layoutMonitor.setVisibility(View.GONE);
	}
	
	/**
	 * 初始化
	 */
	private void init() {
		initlayout();
		initLegend();
	}
	
	/**
	 * 初始化布局
	 */
	private void initlayout() {
		layoutMonitor = (RelativeLayout) mActivity.findViewById(R.id.layout_monitor);
	}
	
	/**
	 * 初始化图例
	 */
	private void initLegend() {
		layoutLegend = (LinearLayout) layoutMonitor.findViewById(R.id.layout_legend);
		layoutLegend.setOnClickListener(this);
		layoutLegendDetail = (LinearLayout) layoutMonitor.findViewById(R.id.layout_legend_detail);
		imageCloud = (ImageView) layoutLegendDetail.findViewById(R.id.image_cloud);
		imagePositive = (ImageView) layoutLegendDetail.findViewById(R.id.image_positive);
		imageNegative = (ImageView) layoutLegendDetail.findViewById(R.id.image_negative);
		btnUp = (ImageButton) layoutMonitor.findViewById(R.id.arrow_up);
		btnDown = (ImageButton) layoutMonitor.findViewById(R.id.arrow_down);
	}
	
	/**
	 * 重置
	 */
	private void reset() {
		resetMap();
		resetLegend();
	}
	
	/**
	 * 重置地图
	 */
	private void resetMap() {
		mAMap.clear();
		mActivity.resetLocation();
	}
	
	/**
	 * 重置图例
	 */
	private void resetLegend() {
		showLegend();
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
	private void receiveData(PackThirdMonitorDown packDown) {
		dataList.clear();
		
		setLegendIcon(packDown.systemTime);

		if(!TextUtils.isEmpty(packDown.msg) || packDown.list.size() <= 0) {
			mActivity.showToast(packDown.msg);
			return;
		}

		MarkerOptions options;
		for(ThirdMonitorInfo info : packDown.list) {
			options = addDataMarker(info);
			if(options != null) {
				dataList.add(options);
			}
		}
		
		showData();
	}
	
	/**
	 * 设置图例图标
	 * @param time
	 */
	private void setLegendIcon(String time) {
		if(TextUtils.isEmpty(time)) {
			return;
		}
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		try {
			int hour = getHour(dateFormat.parse(time));
			switch (hour) {
			case 0:
			case 1:
			case 2:
			case 3:
				imageCloud.setBackgroundResource(R.drawable.icon_lightning_cloud_0_4);
				imagePositive.setBackgroundResource(R.drawable.icon_lightning_positive_0_4);
				imageNegative.setBackgroundResource(R.drawable.icon_lightning_negative_0_4);
				break;
			case 4:
			case 5:
			case 6:
			case 7:
				imageCloud.setBackgroundResource(R.drawable.icon_lightning_cloud_4_8);
				imagePositive.setBackgroundResource(R.drawable.icon_lightning_positive_4_8);
				imageNegative.setBackgroundResource(R.drawable.icon_lightning_negative_4_8);
				break;
			case 8:
			case 9:
			case 10:
			case 11:
				imageCloud.setBackgroundResource(R.drawable.icon_lightning_cloud_8_12);
				imagePositive.setBackgroundResource(R.drawable.icon_lightning_positive_8_12);
				imageNegative.setBackgroundResource(R.drawable.icon_lightning_negative_8_12);
				break;
			case 12:
			case 13:
			case 14:
			case 15:
				imageCloud.setBackgroundResource(R.drawable.icon_lightning_cloud_12_16);
				imagePositive.setBackgroundResource(R.drawable.icon_lightning_positive_12_16);
				imageNegative.setBackgroundResource(R.drawable.icon_lightning_negative_12_16);
				break;
			case 16:
			case 17:
			case 18:
			case 19:
				imageCloud.setBackgroundResource(R.drawable.icon_lightning_cloud_16_20);
				imagePositive.setBackgroundResource(R.drawable.icon_lightning_positive_16_20);
				imageNegative.setBackgroundResource(R.drawable.icon_lightning_negative_16_20);
				break;
			case 20:
			case 21:
			case 22:
			case 23:
				imageCloud.setBackgroundResource(R.drawable.icon_lightning_cloud_20_24);
				imagePositive.setBackgroundResource(R.drawable.icon_lightning_positive_20_24);
				imageNegative.setBackgroundResource(R.drawable.icon_lightning_negative_20_24);
				break;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取小时
	 * @param date
	 * @return
	 */
	private int getHour(Date date) {
		Calendar calendar = Calendar.getInstance();
//		calendar.setTime(date);
		int hour = calendar.get(Calendar.HOUR_OF_DAY); 
		return hour;
	}
	
	/**
	 * 添加监测数据标记
	 * @param info
	 * @return
	 */
	private MarkerOptions addDataMarker(ThirdMonitorInfo info) {
		if(info == null) {
			return null;
		}
		
		LatLng latLng = new LatLng(info.latitude, info.longitude);
		latLng = mActivity.formatLatLng(latLng);
		//BitmapDescriptor bitmap = null;
        Bitmap bitmap = null;
		String context = "";
		if(ThirdMonitorInfo.CLOUD_LIGHTNING.equals(info.lightning)) {
			context += "类型: 云闪";
			if(PackThirdMonitorDown.COLOR_RED.equals(info.color)) {
				bitmap = BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.icon_lightning_cloud_0_4);
			} else if(PackThirdMonitorDown.COLOR_ORANGE.equals(info.color)) {
                bitmap = BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.icon_lightning_cloud_4_8);
			} else if(PackThirdMonitorDown.COLOR_YELLOW.equals(info.color)) {
                bitmap = BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.icon_lightning_cloud_8_12);
			} else if(PackThirdMonitorDown.COLOR_GREEN.equals(info.color)) {
                bitmap = BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.icon_lightning_cloud_12_16);
			} else if(PackThirdMonitorDown.COLOR_BLUE.equals(info.color)) {
                bitmap = BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.icon_lightning_cloud_16_20);
			} else {
                bitmap = BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.icon_lightning_cloud_20_24);
			}
		} else if(ThirdMonitorInfo.GROUND_LIGHTNING.equals(info.lightning)) {
			if(ThirdMonitorInfo.FLAG_POSITIVE == info.flag) {
				context += "类型: 正地闪";
				if(PackThirdMonitorDown.COLOR_RED.equals(info.color)) {
                    bitmap = BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.icon_lightning_positive_0_4);
				} else if(PackThirdMonitorDown.COLOR_ORANGE.equals(info.color)) {
                    bitmap = BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.icon_lightning_positive_4_8);
				} else if(PackThirdMonitorDown.COLOR_YELLOW.equals(info.color)) {
                    bitmap = BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.icon_lightning_positive_8_12);
				} else if(PackThirdMonitorDown.COLOR_GREEN.equals(info.color)) {
                    bitmap = BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.icon_lightning_positive_12_16);
				} else if(PackThirdMonitorDown.COLOR_BLUE.equals(info.color)) {
                    bitmap = BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.icon_lightning_positive_16_20);
				} else {
                    bitmap = BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.icon_lightning_positive_20_24);
				}
			} else if(ThirdMonitorInfo.FLAG_NEGATIVE == info.flag) {
				context += "类型: 负地闪";
				if(PackThirdMonitorDown.COLOR_RED.equals(info.color)) {
                    bitmap = BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.icon_lightning_negative_0_4);
				} else if(PackThirdMonitorDown.COLOR_ORANGE.equals(info.color)) {
                    bitmap = BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.icon_lightning_negative_4_8);
				} else if(PackThirdMonitorDown.COLOR_YELLOW.equals(info.color)) {
                    bitmap = BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.icon_lightning_negative_8_12);
				} else if(PackThirdMonitorDown.COLOR_GREEN.equals(info.color)) {
                    bitmap = BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.icon_lightning_negative_12_16);
				} else if(PackThirdMonitorDown.COLOR_BLUE.equals(info.color)) {
                    bitmap = BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.icon_lightning_negative_16_20);
				} else {
                    bitmap = BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.icon_lightning_negative_20_24);
				}
			}
		}
		context += "  强度: " + info.intens;
		String title = "时间: " + info.time;
		MarkerOptions options = new MarkerOptions();
		options.anchor(0.5f, 0.5f);
		options.position(latLng);
        options.title(title);
        options.snippet(context);
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
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
		List<LatLng> list = new ArrayList<LatLng>();
		for(MarkerOptions options : dataList) {
			mAMap.addMarker(options);
			list.add(options.getPosition());
            builder.include(options.getPosition());
		}
		mActivity.resetLocation();
//        mAMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 0), 500, mCancelableCallback);
	}

    /**
     * 用于实现缩放动画
     */
    private AMap.CancelableCallback mCancelableCallback = new AMap.CancelableCallback() {

        @Override
        public void onCancel() {

        }

        @Override
        public void onFinish() {
        }
    };
	
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
	
}
