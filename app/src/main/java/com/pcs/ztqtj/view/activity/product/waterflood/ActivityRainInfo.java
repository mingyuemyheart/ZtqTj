package com.pcs.ztqtj.view.activity.product.waterflood;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.OnMapScreenShotListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds.Builder;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.waterflood.AdapterPopWindow;
import com.pcs.ztqtj.control.adapter.waterflood.AdapterRainInfoCount;
import com.pcs.ztqtj.control.adapter.waterflood.AdapterRainInfoLegend;
import com.pcs.ztqtj.control.adapter.waterflood.AdapterRainInfoStation;
import com.pcs.ztqtj.control.tool.ShareTools;
import com.pcs.ztqtj.control.tool.ZtqImageTool;
import com.pcs.ztqtj.control.tool.ZtqLocationTool;
import com.pcs.ztqtj.control.tool.youmeng.ShareUtil;
import com.pcs.ztqtj.view.activity.FragmentActivityWithShare;
import com.pcs.ztqtj.view.myview.MyGridView;
import com.tianyi.shawn.tjdecision.wxapi.wxtools.MD5;
import com.pcs.lib.lib_pcs_v3.control.tool.BitmapUtil;
import com.pcs.lib.lib_pcs_v3.control.tool.Util;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackShareAboutDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.waterflood.PackRainInfoAddDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.waterflood.PackRainInfoAddUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.waterflood.PackRainInfoLegendDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.waterflood.PackRainInfoLegendUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.waterflood.PackRainInfoOneDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.waterflood.PackRainInfoOneUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.waterflood.PackRainInfoStationAddDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.waterflood.PackRainInfoStationAddUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.waterflood.PackRainInfoStationOneDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.waterflood.PackRainInfoStationOneUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.waterflood.RainInfo;
import com.pcs.lib_ztqfj_v2.model.pack.net.waterflood.RainStationInfo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * 雨量信息
 * 
 * @author tya
 * 
 */
public class ActivityRainInfo extends FragmentActivityWithShare implements
		OnClickListener, OnCheckedChangeListener {

	// UI
	/**
	 * 高德地图控件
	 */
	private MapView mMapView = null;

	/**
	 * 时间选择按钮
	 */
	private Button btnChooseTime = null;

	/**
	 * 主布局
	 */
	private RelativeLayout mMainLayout = null;

	/**
	 * 选择时间的布局
	 */
	private LinearLayout mChooseTimeLayout = null;

	/**
	 * 单时次雨量按钮
	 */
	private RadioButton rbUnitTimeRain = null;

	/**
	 * 累计雨量按钮
	 */
	private RadioButton rbTotalTimeRain = null;

	/**
	 * 主radiogroup
	 */
	private RadioGroup mRadioGroup = null;

	/**
	 * 时间段显示
	 */
	private TextView tvTimeSlot = null;

	/**
	 * 图例布局
	 */
	private LinearLayout mLegendLayout = null;

	/**
	 * 单个气象站的雨量信息布局
	 */
	private LinearLayout mStationRainInfoLayout = null;

	/**
	 * 雨量统计布局
	 */
	private LinearLayout mCountRainLayout = null;

	/**
	 * 图例按钮
	 */
	private Button btnLegend = null;

	/**
	 * 雨量统计按钮
	 */
	private Button btnCountRain = null;

	/**
	 * 选择时间起始时间的日期控件
	 */
	private TextView tvStartDate = null;

	/**
	 * 选择时间结束时间的日期控件
	 */
	private TextView tvEndDate = null;

	/**
	 * 选择时间起始时间的时间控件
	 */
	private TextView tvStartTime = null;

	/**
	 * 选择时间结束时间的时间控件
	 */
	private TextView tvEndTime = null;

	/**
	 * 单时时间的日期控件
	 */
	private TextView tvTotalDate = null;

	/**
	 * 单时时间的时间控件
	 */
	private TextView tvTotalTime = null;

	// 数据

	/**
	 * 地图层级
	 */
	private final static int MAP_LEVEL = 14;

	/**
	 * 高德地图对象
	 */
	private AMap aMap = null;

	/**
	 * 统计时间
	 */
	private Calendar totalCalendar = Calendar.getInstance();

	/**
	 * 起始时间
	 */
	private Calendar startCalendar = null;

	/**
	 * 结束时间
	 */
	private Calendar endCalendar = null;

	/**
	 * 广播对象
	 */
	private MyReceiver mReceiver = new MyReceiver();

	/**
	 * 图例上传包
	 */
	private PackRainInfoLegendUp legendUp = new PackRainInfoLegendUp();

	/**
	 * 图例下载包
	 */
	private PackRainInfoLegendDown legendDown = new PackRainInfoLegendDown();

	/**
	 * 所有站点的单时次雨量信息上传包
	 */
	private PackRainInfoOneUp oneStationUp = new PackRainInfoOneUp();

	/**
	 * 所有站点的单时次雨量信息下载包
	 */
	private PackRainInfoOneDown oneStationDown = new PackRainInfoOneDown();

	/**
	 * 所有站点的累计次雨量信息上传包
	 */
	private PackRainInfoAddUp addStationUp = new PackRainInfoAddUp();

	/**
	 * 所有站点的累计次雨量信息下载包
	 */
	private PackRainInfoAddDown addStationDown = new PackRainInfoAddDown();

	/**
	 * 该站点的单时次雨量信息上传包
	 */
	private PackRainInfoStationOneUp rainInfoOneUp = new PackRainInfoStationOneUp();

	/**
	 * 该站点的单时次雨量信息下载包
	 */
	private PackRainInfoStationOneDown rainInfoOneDown = new PackRainInfoStationOneDown();

	/**
	 * 该站点的累计雨量信息上传包
	 */
	private PackRainInfoStationAddUp rainInfoAddUp = new PackRainInfoStationAddUp();

	/**
	 * 该站点的累计雨量信息下载包
	 */
	private PackRainInfoStationAddDown rainInfoAddDown = new PackRainInfoStationAddDown();

	/**
	 * 图例适配器
	 */
	private AdapterRainInfoLegend adapterLegend = null;

	/**
	 * 气象站坐标集合构造器
	 */
	private Builder builder = new Builder();

	/**
	 * 上一个被点击的marker
	 */
	private Marker lastClickedMarker = null;

	/**
	 * 地图上所有的marker
	 */
	private List<Marker> markerList = new ArrayList<Marker>();

	/**
	 * 上一个被点击的marker的图标
	 */
	private BitmapDescriptor lastClickedMarkerIcon = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rain_info);
		setTitleText(R.string.title_rain_info);
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
		btnChooseTime = (Button) findViewById(R.id.btn_choose_time);
		mMainLayout = (RelativeLayout) findViewById(R.id.layout_main);
		mChooseTimeLayout = (LinearLayout) LayoutInflater.from(this).inflate(
				R.layout.layout_waterflood_rain_timeslot, null);
		rbUnitTimeRain = (RadioButton) findViewById(R.id.rb_unit_time_rain);
		rbTotalTimeRain = (RadioButton) findViewById(R.id.rb_total_time_rain);
		mRadioGroup = (RadioGroup) findViewById(R.id.rg_main);
		tvTimeSlot = (TextView) findViewById(R.id.tv_time_slot);
		mLegendLayout = (LinearLayout) LayoutInflater.from(this).inflate(
				R.layout.layout_rain_info_legend, null);
		mStationRainInfoLayout = (LinearLayout) LayoutInflater.from(this)
				.inflate(R.layout.layout_rain_info_station, null);
		btnLegend = (Button) findViewById(R.id.btn_legend);
		btnCountRain = (Button) findViewById(R.id.btn_count_rain);
		mCountRainLayout = (LinearLayout) LayoutInflater.from(this).inflate(
				R.layout.layout_rain_info_count, null);
		initChooseTimeView();
	}

	/**
	 * 初始化事件
	 */
	private void initEvent() {
		btnChooseTime.setOnClickListener(this);
		btnLegend.setOnClickListener(this);
		btnCountRain.setOnClickListener(this);
		rbUnitTimeRain.setOnClickListener(this);
		rbTotalTimeRain.setOnClickListener(this);
		mRadioGroup.setOnCheckedChangeListener(this);
        setShareListener(this);
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		PcsDataBrocastReceiver.registerReceiver(this, mReceiver);
		startCalendar = Calendar.getInstance();
		int hour = startCalendar.get(Calendar.HOUR_OF_DAY) - 1;
		startCalendar.set(Calendar.HOUR_OF_DAY, hour);
		endCalendar = Calendar.getInstance();
		initMap();
		setTimeSlotString(Calendar.getInstance(), null);
		// 获取图例数据
		reqLegend();
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

	private void initChooseTimeView() {
		tvStartDate = (TextView) mChooseTimeLayout
				.findViewById(R.id.tv_start_date);
		tvEndDate = (TextView) mChooseTimeLayout.findViewById(R.id.tv_end_date);
		tvStartTime = (TextView) mChooseTimeLayout
				.findViewById(R.id.tv_start_time);
		tvEndTime = (TextView) mChooseTimeLayout.findViewById(R.id.tv_end_time);
		tvTotalDate = (TextView) mChooseTimeLayout
				.findViewById(R.id.tv_total_date);
		tvTotalTime = (TextView) mChooseTimeLayout
				.findViewById(R.id.tv_total_time);
	}

	/**
	 * 地图监听事件
	 */
	private void initMapEvent() {
		aMap.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(Marker marker) {
				closeOtherLayout(mStationRainInfoLayout);
				switch (getRainSTATUS()) {
				case 0: {
					String station_id = getStationIdOnMap(marker);
					if (TextUtils.isEmpty(station_id)) {
						return true;
					}
					Date date = new Date(totalCalendar.getTimeInMillis());
					// 请求气象站雨量数据
					reqUnitTimeRainInfo(date, station_id);
					break;
				}
				case 1: {
					String station_id = getStationIdOnMap(marker);
					if (TextUtils.isEmpty(station_id)) {
						return true;
					}
					Date startdate = new Date(startCalendar.getTimeInMillis());
					Date enddate = new Date(endCalendar.getTimeInMillis());
					// 请求气象站雨量数据
					reqTotalTimeRainInfo(startdate, enddate, station_id);
					break;
				}
				}
				return true;
			}
		});
	}

	/**
	 * 通过marker获取气象站ID
	 * 
	 * @param marker
	 * @return
	 */
	private String getStationIdOnMap(Marker marker) {
		RainStationInfo info = (RainStationInfo) marker.getObject();
		selectMarker(marker);
		return info.station_id;
	}

	/**
	 * 选中移动并标记marker
	 */
	private void selectMarker(Marker marker) {
		LatLng latLng = marker.getPosition();
		moveCamera(latLng);
		// 如果上一个被点击的Marker不是是当前的Marker
		if (lastClickedMarker != null && lastClickedMarkerIcon != null
				&& !lastClickedMarker.getId().equals(marker.getId())) {
			lastClickedMarker.setIcon(lastClickedMarkerIcon);
		}
		lastClickedMarker = marker;
		List<BitmapDescriptor> list = marker.getIcons();
		if (list.size() > 0) {
			lastClickedMarkerIcon = marker.getIcons().get(0);
		}
		marker.setIcon(BitmapDescriptorFactory
				.fromResource(R.drawable.icon_typhoon_location));
	}

	/**
	 * 通过起始时间和结束时间设置TextView
	 * 
	 * @param startCal
	 * @param endCal
	 */
	private void setTimeSlotString(Calendar startCal, Calendar endCal) {
		if (startCal == null) {
			return;
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd H时");
		String str = "";
		if (endCal == null) { // 单时次雨量处理
			Date date = new Date(startCal.getTimeInMillis());
			str = format.format(date);
		} else { // 累计雨量处理
			Date startDate = new Date(startCal.getTimeInMillis());
			Date endDate = new Date(endCal.getTimeInMillis());
			String start = format.format(startDate);
			String end = format.format(endDate);
			str = start + " 到 " + end;
		}
		tvTimeSlot.setText(str);
	}

	/**
	 * 显示时间选择布局
	 */
	private void showChooseTimeLayout() {

		// 如果没显示布局就显示，反之则关闭
		if (!mChooseTimeLayout.isShown()) {
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.BELOW, R.id.ll_time_slot);
			params.setMargins(0, 10, 0, 0);
			mMainLayout.addView(mChooseTimeLayout, params);
		} else {
			closeView(mChooseTimeLayout);
			return;
		}

		switch (getRainSTATUS()) {
		case 0: { // 单时次雨量
			procUnitTimeRain();
			break;
		}
		case 1: { // 累计雨量
			procTotalTimeRain();
			break;
		}
		default:
			Toast.makeText(this, "数据错误！", Toast.LENGTH_LONG).show();
		}

		// 确定键
		Button btn = (Button) mChooseTimeLayout.findViewById(R.id.btn_sure);
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				procOKButton();
			}
		});
	}

	/**
	 * 打开图例布局
	 */
	private void showLegendLayout() {
		if (legendDown == null
				|| (legendDown.one_list.size() == 0 || legendDown.add_list
						.size() == 0)) {
			Toast.makeText(this, "图例错误！", Toast.LENGTH_LONG).show();
			return;
		}
		List<String> listdata = new ArrayList<String>();
		if (legendDown.one_list.size() != 7 || legendDown.add_list.size() != 7) {
			return;
		}
		int length = legendDown.one_list.size();
		for (int i = 0; i < length; i++) {
			listdata.add(legendDown.one_list.get(i));
			listdata.add(legendDown.add_list.get(i));
		}
		adapterLegend = new AdapterRainInfoLegend(this, listdata);
		MyGridView grid = (MyGridView) mLegendLayout
				.findViewById(R.id.gridview);
		grid.setAdapter(adapterLegend);

		// 如果没显示布局就显示，反之则关闭
		if (!mLegendLayout.isShown()) {
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.ABOVE, R.id.ll_bottom_button);
			params.setMargins(0, 10, 0, 0);
			mMainLayout.addView(mLegendLayout, params);
		} else {
			closeView(mLegendLayout);
		}
	}

	/**
	 * 显示站点雨量信息
	 */
	private void showStationRainInfo(List<RainInfo> list, String des) {
		closeAllLayout();
		ImageView btnClose = (ImageView) mStationRainInfoLayout
				.findViewById(R.id.btn_close);
		btnClose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				closeView(mStationRainInfoLayout);
			}
		});

		TextView tvRainFall = (TextView) mStationRainInfoLayout
				.findViewById(R.id.tv_rain_fall);
		switch (getRainSTATUS()) {
		case 0:
			tvRainFall.setText("单时次雨量mm");
			break;
		case 1:
			tvRainFall.setText("累积雨量mm");
			break;
		}

		TextView tvTitle = (TextView) mStationRainInfoLayout
				.findViewById(R.id.tv_title);
		tvTitle.setText(des);
		ListView listView = (ListView) mStationRainInfoLayout
				.findViewById(R.id.lv_rain_info);
		AdapterRainInfoStation adapter = new AdapterRainInfoStation(this, list);
		listView.setAdapter(adapter);

		// 如果没显示布局就显示，反之则关闭
		if (!mStationRainInfoLayout.isShown()) {
			LinearLayout ll = (LinearLayout) findViewById(R.id.ll_bottom_button);
			RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) ll
					.getLayoutParams();
			int bottomHeight = p.height + p.bottomMargin * 2;
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.BELOW, R.id.ll_time_slot);
			params.setMargins(0, 0, 0, bottomHeight);
			mMainLayout.addView(mStationRainInfoLayout, params);
		} else {
			adapter.notifyDataSetChanged();
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
	 * 获取当前雨量查询状态
	 * 
	 * @return 返回0则状态为单时次雨量，返回1则状态为累计雨量,返回-1则错误
	 */
	private int getRainSTATUS() {
		if (rbUnitTimeRain == null || rbTotalTimeRain == null) {
			return -1;
		}
		if (rbUnitTimeRain.isChecked()) {
			return 0;
		} else {
			return 1;
		}
	}

	/**
	 * 获取今天加过去五天的日期列表
	 * 
	 * @return 今天加过去五天的日期列表
	 */
	private List<String> getFiveDayDateList() {
		List<String> result = new ArrayList<String>();
		Calendar endCal = Calendar.getInstance();
		for (int i = 0; i < 6; i++) {
			Calendar cal = Calendar.getInstance();
			int day = cal.get(Calendar.DATE);
			cal.set(Calendar.DATE, day - i);
			Date date = new Date(cal.getTimeInMillis());
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			String str = format.format(date);
			result.add(str);
		}
		return result;
	}

	/**
	 * 获取24小时时间列表
	 * 
	 * @return 24小时时间列表
	 */
	private List<String> get24HoursTimeList() {
		List<String> result = new ArrayList<String>();
		for (int i = 1; i <= 24; i++) {
			result.add(String.valueOf(i));
		}
		return result;
	}

	/**
	 * 获取时间列表
	 * 
	 * @return
	 */
	private List<String> getHoursList(String time) {
		int endHour = 0;
		List<String> hourList = new ArrayList<String>();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		// 获取今天时间
		Date todayDate = new Date(System.currentTimeMillis());
		String todayTime = format.format(todayDate);
		/*
		 * 如果日期的textview时间是今天就返回当前时间的时间列表 比如现在是13点，则返回1-13的列表
		 */
		if (time.equals(todayTime)) {
			SimpleDateFormat timeFormat = new SimpleDateFormat("HH");
			endHour = Integer.parseInt(timeFormat.format(todayDate));
		} else {
			endHour = 23;
		}
		for (int i = 0; i <= endHour; i++) {
			hourList.add(String.valueOf(i));
		}
		return hourList;
	}

	/**
	 * 弹出日期选择框
	 */
	private void createPopupWindow(final TextView dropDownView,
			final List<String> dataeaum) {
		AdapterPopWindow dataAdapter = new AdapterPopWindow(this, dataeaum);
		View popcontent = LayoutInflater.from(this).inflate(
				R.layout.pop_list_layout, null);
		ListView lv = (ListView) popcontent.findViewById(R.id.mylistviw);
		lv.setAdapter(dataAdapter);
		final PopupWindow pop = new PopupWindow(this);
		pop.setContentView(popcontent);
		pop.setOutsideTouchable(false);
		pop.setWidth(dropDownView.getWidth());
		// 调整下拉框长度
		int screenHight = Util.getScreenHeight(this);
		if (dataeaum.size() < 9) {
			pop.setHeight(LayoutParams.WRAP_CONTENT);
		} else {
			pop.setHeight((int) (screenHight * 0.6));
		}
		pop.setFocusable(true);
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				pop.dismiss();
				dropDownView.setText(dataeaum.get(position));

				tvStartTime.setOnClickListener(new MyClickListener(
						getHoursList(tvStartDate.getText().toString())));
				tvEndTime.setOnClickListener(new MyClickListener(
						getHoursList(tvEndDate.getText().toString())));
				tvTotalTime.setOnClickListener(new MyClickListener(
						getHoursList(tvTotalDate.getText().toString())));
			}
		});
		pop.showAsDropDown(dropDownView);
	}

	/**
	 * 单时次雨量处理函数
	 */
	private void procUnitTimeRain() {
		LinearLayout llUnitRain = (LinearLayout) mChooseTimeLayout
				.findViewById(R.id.ll_unit_time);
		LinearLayout llTotalRain = (LinearLayout) mChooseTimeLayout
				.findViewById(R.id.ll_total_time);
		List<String> timeList = get24HoursTimeList();
		llUnitRain.setVisibility(View.VISIBLE);
		llTotalRain.setVisibility(View.GONE);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date(totalCalendar.getTimeInMillis());
		String strDate = format.format(date);
		tvTotalDate
				.setOnClickListener(new MyClickListener(getFiveDayDateList()));
		tvTotalTime.setOnClickListener(new MyClickListener(
				getHoursList(strDate)));
		String strTime = String
				.valueOf(totalCalendar.get(Calendar.HOUR_OF_DAY));
		tvTotalDate.setText(strDate);
		tvTotalTime.setText(strTime);

		setTimeSlotString(totalCalendar, null);

		// 如果选择时间段的布局没显示则请求数据
		if (!mChooseTimeLayout.isShown()) {
			reqUnitTimeStationInfo(date, "1");
		}
	}

	/**
	 * 累计雨量处理函数
	 */
	private void procTotalTimeRain() {
		LinearLayout llUnitRain = (LinearLayout) mChooseTimeLayout
				.findViewById(R.id.ll_unit_time);
		LinearLayout llTotalRain = (LinearLayout) mChooseTimeLayout
				.findViewById(R.id.ll_total_time);
		List<String> timeList = get24HoursTimeList();
		llUnitRain.setVisibility(View.GONE);
		llTotalRain.setVisibility(View.VISIBLE);

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date startDate = new Date(startCalendar.getTimeInMillis());
		String strStart = format.format(startDate);
		Date endDate = new Date(endCalendar.getTimeInMillis());
		String strEnd = format.format(endDate);

		tvStartDate
				.setOnClickListener(new MyClickListener(getFiveDayDateList()));
		tvEndDate.setOnClickListener(new MyClickListener(getFiveDayDateList()));
		tvStartTime.setOnClickListener(new MyClickListener(
				getHoursList(strStart)));
		tvEndTime.setOnClickListener(new MyClickListener(getHoursList(strEnd)));

		String strStartDate = format.format(startDate);
		String strStartTime = String.valueOf(startCalendar
				.get(Calendar.HOUR_OF_DAY));
		String strEndDate = format.format(endDate);
		String strEndTime = String.valueOf(endCalendar
				.get(Calendar.HOUR_OF_DAY));

		tvStartDate.setText(strStartDate);
		tvStartTime.setText(strStartTime);
		tvEndDate.setText(strEndDate);
		tvEndTime.setText(strEndTime);

		setTimeSlotString(startCalendar, endCalendar);

		// 如果选择时间段的布局没显示则请求数据
		if (!mChooseTimeLayout.isShown()) {
			reqTotalTimeStationInfo(startDate, endDate, "1");
		}
	}

	/**
	 * 确定键处理函数
	 */
	private void procOKButton() {
		closeAllLayout();
		clearMarker();
		switch (getRainSTATUS()) {
		case 0: { // 单时次雨量
			String strDate = tvTotalDate.getText().toString();
			String strTime = tvTotalTime.getText().toString();
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Calendar cal = stringToCalendar(strDate, format);
			if (cal == null) {
				Toast.makeText(this, "参数错误！", Toast.LENGTH_LONG).show();
				return;
			}
			cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(strTime));
			Date date = new Date(cal.getTimeInMillis());
			format = new SimpleDateFormat("yyyy-MM-dd-HH");
			String time = format.format(date);
			totalCalendar = cal;
			closeView(mChooseTimeLayout);
			setTimeSlotString(totalCalendar, null);
			// 请求所有站点信息
			reqUnitTimeStationInfo(date, "1");
			break;
		}
		case 1: { // 累计雨量

			String strStartDate = tvStartDate.getText().toString();
			String strStartTime = tvStartTime.getText().toString();
			String strEndDate = tvEndDate.getText().toString();
			String strEndTime = tvEndTime.getText().toString();

			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Calendar startCal = stringToCalendar(strStartDate, format);
			Calendar endCal = stringToCalendar(strEndDate, format);
			startCal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(strStartTime));
			endCal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(strEndTime));
			// 如果结束时间大于等于开始时间则取数据
			if (compareTo(endCal, startCal)) {
				Date startDate = new Date(startCal.getTimeInMillis());
				Date endDate = new Date(endCal.getTimeInMillis());
				startCalendar = startCal;
				endCalendar = endCal;
				closeView(mChooseTimeLayout);
				setTimeSlotString(startCalendar, endCalendar);
				reqTotalTimeStationInfo(startDate, endDate, "1");
			} else {
				Toast.makeText(this, "开始时间不能晚于结束时间！", Toast.LENGTH_LONG).show();
			}
			break;
		}
		}
	}

	/**
	 * 处理雨量统计按钮
	 */
	private void procCountRain() {

		switch (getRainSTATUS()) {
		case 0: {
			if (oneStationDown.listdata.size() == 0) {
				Toast.makeText(this, "暂无数据！", Toast.LENGTH_LONG).show();
				return;
			}
			showCountRainLayout(oneStationDown.listdata);
			break;
		}
		case 1: {
			if (addStationDown.listdata.size() == 0) {
				Toast.makeText(this, "暂无数据！", Toast.LENGTH_LONG).show();
				return;
			}
			showCountRainLayout(addStationDown.listdata);
			break;
		}
		}

	}

	/**
	 * 显示雨量统计布局
	 */
	private void showCountRainLayout(final List<RainStationInfo> list) {
		ListView listView = (ListView) mCountRainLayout
				.findViewById(R.id.lv_count_rain);
		AdapterRainInfoCount adapter = new AdapterRainInfoCount(this, list);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				closeOtherLayout(mStationRainInfoLayout);
				RainStationInfo rsi = list.get(position);
				String station_id = rsi.station_id;
				for (int i = 0; i < markerList.size(); i++) {
					Marker marker = markerList.get(i);
					RainStationInfo info = (RainStationInfo) marker.getObject();
					// 如果列表所点击的气象站是在地图上则移动到该点
					if (rsi.equals(info)) {
						selectMarker(marker);
					}
				}
				switch (getRainSTATUS()) {
				case 0: {
					Date date = new Date(totalCalendar.getTimeInMillis());
					reqUnitTimeRainInfo(date, station_id);
					break;
				}
				case 1: {
					Date startdate = new Date(startCalendar.getTimeInMillis());
					Date enddate = new Date(endCalendar.getTimeInMillis());
					reqTotalTimeRainInfo(startdate, enddate, station_id);
					break;
				}
				}
			}
		});

		// 如果没显示布局就显示，反之则关闭
		if (!mCountRainLayout.isShown()) {
			LinearLayout ll = (LinearLayout) findViewById(R.id.ll_bottom_button);
			RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) ll
					.getLayoutParams();
			int bottomHeight = p.height + p.bottomMargin * 2;
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.BELOW, R.id.ll_time_slot);
			params.setMargins(0, 0, 0, bottomHeight);
			mMainLayout.addView(mCountRainLayout, params);
		} else {
			closeView(mCountRainLayout);
		}
	}

	/**
	 * 比较两日期大小(cal < othercal返回值为false, cal >= othercal true)
	 * 
	 * @param cal
	 * @param othercal
	 * @return
	 */
	public boolean compareTo(Calendar cal, Calendar othercal) {
		int nYear = cal.get(Calendar.YEAR);
		int nMonth = cal.get(Calendar.MONTH);
		int nDay = cal.get(Calendar.DATE);
		int nHour = cal.get(Calendar.HOUR_OF_DAY);
		int nOtherYear = othercal.get(Calendar.YEAR);
		int nOtherMonth = othercal.get(Calendar.MONTH);
		int nOtherDay = othercal.get(Calendar.DATE);
		int nOtherHour = othercal.get(Calendar.HOUR_OF_DAY);
		boolean flag = false;
		if (nYear < nOtherYear) {
			flag = false;
		} else if (nYear == nOtherYear) {
			if (nMonth < nOtherMonth) {
				flag = false;
			} else if (nMonth == nOtherMonth) {
				if (nDay < nOtherDay) {
					flag = false;
				} else if (nDay == nOtherDay) {
					if (nHour < nOtherHour) {
						flag = false;
					} else if (nHour == nOtherHour) {
						flag = true;
					} else {
						flag = true;
					}
				} else {
					flag = true;
				}
			} else {
				flag = true;
			}
		} else {
			flag = true;
		}
		return flag;
	}

	/**
	 * 获取图例信息
	 */
	private void reqLegend() {

		if(!isOpenNet()){
			showToast(getString(R.string.net_err));
			return ;
		}
		showProgressDialog();
		if (legendUp == null) {
			legendUp = new PackRainInfoLegendUp();
		}
		PcsDataDownload.addDownload(legendUp);
	}

	/**
	 * 获取单时次的站点雨量信息
	 * 
	 * @param date
	 *            时间
	 * @param station_id
	 *            站点ID
	 */
	private void reqUnitTimeRainInfo(Date date, String station_id) {

		if(!isOpenNet()){
			showToast(getString(R.string.net_err));
			return ;
		}
		showProgressDialog();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHH");
		String time = format.format(date);
		rainInfoOneUp.time = time;
		rainInfoOneUp.station_id = station_id;
		PcsDataDownload.addDownload(rainInfoOneUp);
	}

	/**
	 * 获取累计的站点雨量信息
	 * 
	 * @param startdate
	 *            开始时间
	 * @param enddate
	 *            结束时间
	 * @param station_id
	 *            站点信息
	 */
	private void reqTotalTimeRainInfo(Date startdate, Date enddate,
			String station_id) {

		if(!isOpenNet()){
			showToast(getString(R.string.net_err));
			return ;
		}
		showProgressDialog();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHH");
		String starttime = format.format(startdate);
		String endtime = format.format(enddate);
		rainInfoAddUp.starttime = starttime;
		rainInfoAddUp.endtime = endtime;
		rainInfoAddUp.station_id = station_id;
		PcsDataDownload.addDownload(rainInfoAddUp);
	}

	/**
	 * 单时次的所有站点信息
	 * 
	 * @param date
	 * @param rank
	 */
	private void reqUnitTimeStationInfo(Date date, String rank) {

		if(!isOpenNet()){
			showToast(getString(R.string.net_err));
			return ;
		}
		showProgressDialog();
		if (oneStationUp == null) {
			oneStationUp = new PackRainInfoOneUp();
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHH");
		String time = format.format(date);
		oneStationUp.time = time;
		oneStationUp.rank = rank;
		PcsDataDownload.addDownload(oneStationUp);
	}

	/**
	 * 请求累计时间所有站点信息
	 * 
	 * @param start
	 * @param end
	 * @param rank
	 */
	private void reqTotalTimeStationInfo(Date start, Date end, String rank) {

		if(!isOpenNet()){
			showToast(getString(R.string.net_err));
			return ;
		}
		showProgressDialog();
		if (addStationUp == null) {
			addStationUp = new PackRainInfoAddUp();
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHH");
		String startTime = format.format(start);
		String endTime = format.format(end);
		addStationUp.starttime = startTime;
		addStationUp.endtime = endTime;
		addStationUp.rank = rank;
		PcsDataDownload.addDownload(addStationUp);
	}

	/**
	 * 字符串转日历
	 * 
	 * @return
	 */
	private Calendar stringToCalendar(String value, SimpleDateFormat format) {
		Calendar cal = null;
		try {
			Date date = format.parse(value);
			cal = Calendar.getInstance();
			cal.setTime(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return cal;
	}

	/**
	 * 定位
	 */
	private void location() {
		LatLng latLng = ZtqLocationTool.getInstance().getLatLng();
		if (latLng != null) {
			aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,
					MAP_LEVEL));
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
            case R.id.btn_right:
                aMap.getMapScreenShot(mScreenShotListener);
                break;
		case R.id.btn_choose_time: {
			// 先关闭其他页面
			closeOtherLayout(mChooseTimeLayout);
			showChooseTimeLayout();
			break;
		}
		case R.id.btn_legend: {
			// 先关闭其他页面
			closeOtherLayout(mLegendLayout);
			showLegendLayout();
			break;
		}
		case R.id.rb_unit_time_rain: {
			closeAllLayout();
			clearMarker();
			procUnitTimeRain();
			break;
		}
		case R.id.rb_total_time_rain: {
			closeAllLayout();
			clearMarker();
			procTotalTimeRain();
			break;
		}
		case R.id.btn_count_rain: {
			closeOtherLayout(mCountRainLayout);
			procCountRain();
			break;
		}
		}
	}

	private String genNonceStr() {
		Random random = new Random();
		return MD5.getMessageDigest(String.valueOf(random.nextInt(10000))
				.getBytes());
	}

	/**
	 * 在地图上加点
	 * 
	 * @param list
	 */
	private void addMarkerOnMap(List<RainStationInfo> list) {
		if (list == null) {
			return;
		}
		Builder builder = new Builder();
		clearMarker();
		float allLat = 0f;
		float allLog = 0f;
		for (int i = 0; i < list.size(); i++) {
			RainStationInfo info = list.get(i);
			float rainfall = Float.parseFloat(info.rain);
			if (TextUtils.isEmpty(info.lat) || TextUtils.isEmpty(info.log)) {
				continue;
			}
			double lat = Double.parseDouble(info.lat);
			double log = Double.parseDouble(info.log);
			LatLng latLng = new LatLng(lat, log);
			MarkerOptions markerOptions = new MarkerOptions()
					.position(latLng)
					.anchor(0.5f, 0.5f)
					.icon(BitmapDescriptorFactory
							.fromResource(getIconID(rainfall)));
			addMarker(markerOptions, info);
			builder.include(latLng);
			allLat += lat;
			allLog += log;
		}
		LatLng latLng = new LatLng(allLat / list.size(), allLog / list.size());
		// int maxZoom = (int) aMap.getMaxZoomLevel();
		// aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(),
		// MAP_LEVEL));
		aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 7));
	}

	/**
	 * 添加marker
	 * 
	 * @param markerOptions
	 */
	private void addMarker(MarkerOptions markerOptions, RainStationInfo info) {
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

	/**
	 * 通过雨量获得图片ID
	 * 
	 * @param rainfall
	 * @return
	 */
	private int getIconID(float rainfall) {
		int[] resIDs = { R.drawable.icon_marker_0, R.drawable.icon_marker_1,
				R.drawable.icon_marker_2, R.drawable.icon_marker_3,
				R.drawable.icon_marker_4, R.drawable.icon_marker_5,
				R.drawable.icon_marker_6, };
		int result = R.drawable.icon_marker_0;
		switch (getRainSTATUS()) {
		case 0: { // 单时次雨量
			result = resIDs[getRainLevel(legendDown.one_list, rainfall)];
			break;
		}
		case 1: { // 累计雨量
			result = resIDs[getRainLevel(legendDown.add_list, rainfall)];
			break;
		}
		}
		return result;
	}

	/**
	 * 获取雨量等级
	 * 
	 * @param list
	 *            雨量等级列表
	 * @param rainfall
	 *            雨量
	 * @return
	 */
	private int getRainLevel(List<String> list, float rainfall) {
		if (list.size() == 7) {
			// 轮询前六个字符串(因为前六个字符串格式为 x~y)
			for (int i = 0; i < list.size() - 1; i++) {
				// 分割字符串
				String[] strs = list.get(i).split("~");
				if (strs.length == 2) {
					float min = Float.parseFloat(strs[0]);
					float max = Float.parseFloat(strs[1]);
					if (rainfall >= min && rainfall <= max) {
						return i;
					}
				}
			}
			// 如果雨量不在前六等级，则必定在第七等级
			return 6;
		}
		return 0;

	}

	/**
	 * 移动摄像机
	 * 
	 * @param latLng
	 *            坐标
	 */
	private void moveCamera(LatLng latLng) {
		float currentLevel = aMap.getCameraPosition().zoom;
		aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,
				currentLevel));
	}

	/**
	 * 关闭所有布局
	 */
	private void closeAllLayout() {
		if (mChooseTimeLayout != null) {
			closeView(mChooseTimeLayout);
		}
		if (mLegendLayout != null) {
			closeView(mLegendLayout);
		}
		if (mStationRainInfoLayout != null) {
			closeView(mStationRainInfoLayout);
		}
		if (mCountRainLayout != null) {
			closeView(mCountRainLayout);
		}
	}

	/**
	 * 关闭其他页面
	 */
	private void closeOtherLayout(View layout) {
		if (mChooseTimeLayout != null && !mChooseTimeLayout.equals(layout)) {
			closeView(mChooseTimeLayout);
		}
		if (mLegendLayout != null && !mLegendLayout.equals(layout)) {
			closeView(mLegendLayout);
		}
		if (mStationRainInfoLayout != null
				&& !mStationRainInfoLayout.equals(layout)) {
			closeView(mStationRainInfoLayout);
		}
		if (mCountRainLayout != null && !mCountRainLayout.equals(layout)) {
			closeView(mCountRainLayout);
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// switch (checkedId) {
		// case R.id.rb_unit_time_rain:
		// procUnitTimeRain();
		// break;
		// case R.id.rb_total_time_rain:
		// procTotalTimeRain();
		// break;
		// }
	}

	/**
	 * 点击下拉菜单回调类
	 * 
	 * @author tya
	 * 
	 */
	private class MyClickListener implements OnClickListener {

		private List<String> dataList = new ArrayList<String>();

		public MyClickListener() {
		}

		public MyClickListener(List<String> dataList) {
			this.dataList = dataList;
		}

		@Override
		public void onClick(View v) {
			if (dataList == null || dataList.size() == 0) {
				return;
			}
			createPopupWindow((TextView) v, dataList);
		}

		/**
		 * 设置popwindow显示数据列表
		 * 
		 * @param dataList
		 */
		public void setDateList(List<String> dataList) {
			this.dataList.clear();
			this.dataList.addAll(dataList);
		}

	}

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

			// 图例
			if (legendUp.getName().equals(nameStr)) {
				dismissProgressDialog();
                legendDown = (PackRainInfoLegendDown) PcsDataManager.getInstance().getNetPack(nameStr);
				if (legendDown == null) {
					return;
				}
				if (legendDown.one_list.size() == 0
						|| legendDown.add_list.size() == 0) {
					return;
				}
				Date date = new Date(totalCalendar.getTimeInMillis());
				reqUnitTimeStationInfo(date, "1");
			}

			// 单时次站点信息
			if (oneStationUp.getName().equals(nameStr)) {
				dismissProgressDialog();
                oneStationDown = (PackRainInfoOneDown) PcsDataManager.getInstance().getNetPack(nameStr);
				if (oneStationDown == null) {
					return;
				}
				if (oneStationDown.listdata.size() == 0) {
					Toast.makeText(ActivityRainInfo.this, "暂无数据!",
							Toast.LENGTH_LONG).show();
					return;
				}
				addMarkerOnMap(oneStationDown.listdata);
			}

			// 累计雨量站点信息
			if (addStationUp.getName().equals(nameStr)) {
				dismissProgressDialog();
                addStationDown = (PackRainInfoAddDown) PcsDataManager.getInstance().getNetPack(nameStr);
				if (addStationDown == null) {
					return;
				}
				if (addStationDown.listdata.size() == 0) {
					Toast.makeText(ActivityRainInfo.this, "暂无数据!",
							Toast.LENGTH_LONG).show();
					return;
				}
				addMarkerOnMap(addStationDown.listdata);
			}

			// 单时次某站点的雨量信息
			if (rainInfoOneUp.getName().equals(nameStr)) {
				dismissProgressDialog();
                rainInfoOneDown = (PackRainInfoStationOneDown) PcsDataManager.getInstance().getNetPack(nameStr);
				if (rainInfoOneDown == null) {
					return;
				}
				if (rainInfoOneDown.raininfo_list.size() == 0) {
					Toast.makeText(ActivityRainInfo.this, "无数据！",
							Toast.LENGTH_LONG).show();
					return;
				}
				showStationRainInfo(rainInfoOneDown.raininfo_list,
						rainInfoOneDown.des);
			}

			// 累计某站点的雨量信息
			if (rainInfoAddUp.getName().equals(nameStr)) {
				dismissProgressDialog();
                rainInfoAddDown = (PackRainInfoStationAddDown) PcsDataManager.getInstance().getNetPack(nameStr);
				if (rainInfoAddDown == null) {
					return;
				}
				if (rainInfoAddDown.raininfo_list.size() == 0) {
					Toast.makeText(ActivityRainInfo.this, "无数据！",
							Toast.LENGTH_LONG).show();
					return;
				}
				showStationRainInfo(rainInfoAddDown.raininfo_list,
						rainInfoAddDown.des);
			}
		}

	}

	private Bitmap bitmapMap;

	private void getBitmap() {
		if (mMapView != null) {
			mMapView.getMap().getMapScreenShot(new OnMapScreenShotListener() {
				@Override
				public void onMapScreenShot(Bitmap arg0) {
					bitmapMap = arg0;
					shareMap(ActivityRainInfo.this);
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
		if (mCountRainLayout != null
				&& mCountRainLayout.getVisibility() == View.INVISIBLE) {
			ShareUtil.share(activity);
		} else {
			getBitmap();
		}
	}

	private void shareMap(Activity activity) {
		Bitmap bitmap = BitmapUtil.takeScreenShot(activity);
		Canvas canvas = new Canvas(bitmap);
		if (bitmapMap != null) {
			int hight = bitmap.getHeight() - bitmapMap.getHeight()
					+ Util.dip2px(this, 40);
			canvas.drawBitmap(bitmapMap, 0, hight, null);
		}
		ShareUtil.share(activity, "", bitmap);
	}

    /**
     * 高德地图截屏回调
     */
    private OnMapScreenShotListener mScreenShotListener = new OnMapScreenShotListener() {
        @Override
        public void onMapScreenShot(Bitmap bitmap) {
            View layout = findViewById(R.id.layout_main).getRootView();
            mAmapBitmap = bitmap;
            Bitmap bm = ZtqImageTool.getInstance().getScreenBitmapNew(ActivityRainInfo.this);

            int[] location = new int[2];
            mMapView.getLocationOnScreen(location);

            mShareBitmap = procImage(mAmapBitmap, bm, location[1]);
			mShareBitmap = ZtqImageTool.getInstance().stitchQR(ActivityRainInfo.this, mShareBitmap);
            PackShareAboutDown down = (PackShareAboutDown) PcsDataManager.getInstance().getNetPack("wt_share#ABOUT_QXCP_DXFW");
            if(down != null) {
                ShareTools.getInstance(ActivityRainInfo.this).setShareContent(getTitleText(), down.share_content, mShareBitmap,"0").showWindow(layout);
            }
        }

        @Override
        public void onMapScreenShot(Bitmap bitmap, int i) {

        }
    };
}
