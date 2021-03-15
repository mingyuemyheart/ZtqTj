package com.pcs.ztqtj.view.activity.product.lightning;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.AdapterDataQuire;
import com.pcs.ztqtj.control.adapter.AdapterDataQuire.LightningMoreBtn;
import com.pcs.ztqtj.control.adapter.livequery.AdapterData;
import com.pcs.ztqtj.control.inter.DrowListClick;
import com.pcs.ztqtj.view.myview.MyListView;
import com.pcs.lib.lib_pcs_v3.control.tool.Util;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackKnowWarnDetailUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.lightning.PackLocalThunderQuireDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.lightning.PackThunderListDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.lightning.PackThunderListUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.lightning.PackThunderQuireDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.lightning.PackThunderQuireUp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 控制器：数据查询
 * 
 * @author Chensq 2015年11月06日
 */
public class ControlDefenseGuide implements OnClickListener {

	private ActivityLightningMonitor mActivity;

	/**
	 * 模块布局
	 */
	private LinearLayout layoutGuide;
	/**
	 * 开始年份
	 */
	private TextView data_quire_start_year;
	/**
	 * 开始月份
	 */
	private TextView data_quire_start_month;
	/**
	 * 开始日期
	 */
	private TextView data_quire_start_date;
	/**
	 * 开始时间
	 */
	private TextView data_quire_start_time;
	/**
	 * 开始分钟
	 */
	private TextView data_quire_start_todata;
	/**
	 * 结束年份
	 */
	private TextView data_quire_end_year;
	/**
	 * 结束月份
	 */
	private TextView data_quire_end_date;
	/**
	 * 结束日期
	 */
	private TextView data_quire_end_month;
	/**
	 * 结束时间
	 */
	private TextView data_quire_end_time;
	/**
	 * 结束分钟
	 */
	private TextView data_quire_end_todata;
	/**
	 * 城市
	 */
	private TextView data_quire_province;
	/**
	 * 地区
	 */
	private TextView data_quire_city;
	/**
	 * 闪电类型
	 */
	private TextView data_lightning_type;
	/**
	 * 极性
	 */
	private TextView data_lightning__polarity;
	/**
	 * 闪电活动综述
	 */
	private TextView tv_lightning_review;
	/**
	 * 列表
	 */
	private MyListView data_lightning_list;
	/**
	 * 更多
	 */
	private TextView tv_data_more;
	/**
	 * 查询按钮
	 */
	private Button data_search_btn;
	private LinearLayout lightning_quire_list;

	/**
	 * 列表数据集合
	 */
	private List<String> dataList = new ArrayList<String>();

	/**
	 * 数据查询列表适配器
	 */
	private AdapterDataQuire mAdapter;

	PackKnowWarnDetailUp upPack = new PackKnowWarnDetailUp();

	private final String TAG = "ControlDefenseGuide";

	/**
	 * 开始年
	 */
	private List<String> start_date_year;
	/**
	 * 开始月
	 */
	private List<String> start_date_month;
	/**
	 * 开始日
	 */
	private List<String> start_date_data;
	/**
	 * 开始时分
	 */
	private List<String> start_time_data;
	/**
	 * 开始分钟
	 */
	private List<String> start_time_minute;

	/**
	 * 结束年
	 */
	private List<String> end_date_year;
	/**
	 * 结束月
	 */
	private List<String> end_date_month;
	/**
	 * 结束日
	 */
	private List<String> end_date_data;
	/**
	 * 结束时分
	 */
	private List<String> end_time_data;
	/**
	 * 结束分钟
	 */
	private List<String> end_time_minute;

	/**
	 * 城市
	 */
	private List<String> city_spinner_data;
	/**
	 * 县 地区
	 */
	private List<String> town_spinner_data;
	/**
	 * 闪电类型
	 */
	private List<String> data_Lightning_type;
	/**
	 * 极性
	 */
	private List<String> data_Lightning_polarity;

	/**
	 * 下拉框设置tag 1、为起始时间-年份 2、为起始时间-月份 3、为起始时间-日期 4、起始时间-时间 5、起始时间-分钟 6、结束时间-年份
	 * 7、结束时间-月份 8、结束时间-日期 9、结束时间-时间 10、结束时间-分钟 11、市下拉列表 12、镇下拉列表 13、闪电类型 14、极性
	 * */
	private final int start_year = 1, start_month = 2, start_date = 3,
			start_time = 4, start_todata = 5, toyear = 6, tomonth = 7,
			todata = 8, totime = 9, totodata = 10, city_spinner = 11,
			town_spinner = 12, lightning_type = 13, lightning__polarity = 14;
	/** 雷电查询—地市地区列表下载包 **/
	private PackThunderListDown packThunderListDown;
	/** 雷电查询—地市地区列表上传包 **/
	private PackThunderListUp packThunderListUp = new PackThunderListUp();
	/** 雷电查询—地市地区查询下载包 **/
	private PackThunderQuireDown packThunderQuireDown;
	/** 雷电查询—地市地区查询上传包 **/
	private PackThunderQuireUp packThunderQuireUp = new PackThunderQuireUp();
	private Toast toast;

	private void initView() {
		data_quire_start_year = (TextView) mActivity
				.findViewById(R.id.data_quire_start_year);
		data_quire_start_month = (TextView) mActivity
				.findViewById(R.id.data_quire_start_month);
		data_quire_start_date = (TextView) mActivity
				.findViewById(R.id.data_quire_start_date);
		data_quire_start_time = (TextView) mActivity
				.findViewById(R.id.data_quire_start_time);
		data_quire_start_todata = (TextView) mActivity
				.findViewById(R.id.data_quire_start_todata);

		data_quire_end_year = (TextView) mActivity
				.findViewById(R.id.data_quire_end_year);
		data_quire_end_month = (TextView) mActivity
				.findViewById(R.id.data_quire_end_month);
		data_quire_end_date = (TextView) mActivity
				.findViewById(R.id.data_quire_end_date);
		data_quire_end_time = (TextView) mActivity
				.findViewById(R.id.data_quire_end_time);
		data_quire_end_todata = (TextView) mActivity
				.findViewById(R.id.data_quire_end_todata);

		data_quire_province = (TextView) mActivity
				.findViewById(R.id.data_quire_province);
		data_quire_city = (TextView) mActivity
				.findViewById(R.id.data_quire_city);
		tv_lightning_review = (TextView) mActivity
				.findViewById(R.id.tv_lightning_review);

		data_lightning_type = (TextView) mActivity
				.findViewById(R.id.data_lightning_type);
		data_lightning__polarity = (TextView) mActivity
				.findViewById(R.id.data_lightning__polarity);
		tv_data_more = (TextView) mActivity.findViewById(R.id.tv_data_more);
		data_search_btn = (Button) mActivity.findViewById(R.id.data_search_btn);
		lightning_quire_list = (LinearLayout) mActivity
				.findViewById(R.id.lightning_quire_list);
	}

	private void initEvent() {
		data_quire_start_year.setOnClickListener(this);
		data_quire_start_month.setOnClickListener(this);
		data_quire_start_date.setOnClickListener(this);
		data_quire_start_time.setOnClickListener(this);
		data_quire_start_todata.setOnClickListener(this);

		data_quire_end_year.setOnClickListener(this);
		data_quire_end_month.setOnClickListener(this);
		data_quire_end_date.setOnClickListener(this);
		data_quire_end_time.setOnClickListener(this);
		data_quire_end_todata.setOnClickListener(this);

		data_quire_province.setOnClickListener(this);
		data_quire_city.setOnClickListener(this);

		data_lightning_type.setOnClickListener(this);
		data_lightning__polarity.setOnClickListener(this);
		data_search_btn.setOnClickListener(this);
		tv_data_more.setOnClickListener(this);

		start_date_year = new ArrayList<String>();
		start_date_month = new ArrayList<String>();
		start_date_data = new ArrayList<String>();
		start_time_data = new ArrayList<String>();
		start_time_minute = new ArrayList<String>();

		end_date_year = new ArrayList<String>();
		end_date_month = new ArrayList<String>();
		end_date_data = new ArrayList<String>();
		end_time_data = new ArrayList<String>();
		end_time_minute = new ArrayList<String>();

		city_spinner_data = new ArrayList<String>();
		town_spinner_data = new ArrayList<String>();
		data_Lightning_type = new ArrayList<String>();

	}

	private void initData() {

		data_Lightning_type.add("云闪");
		data_Lightning_type.add("地闪");
		data_Lightning_type.add("不限");

		data_Lightning_polarity = new ArrayList<String>();
		data_Lightning_polarity.add("正");
		data_Lightning_polarity.add("负");
		data_Lightning_polarity.add("不限");

		c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);// 获取当前年份
		start_date_year.add(year + "");
		start_date_year.add(year - 1 + "");

		int month = (c.get(Calendar.MONTH)) + 1;// 获取当前月份
		day = c.get(Calendar.DAY_OF_MONTH);
		int hour = c.get(Calendar.HOUR_OF_DAY);// 获取当前的小时数
		int mMinute = c.get(Calendar.MINUTE);// 获取当前的分钟数
		// 初始化开始时间
		data_quire_start_year.setText(year + "");
		data_quire_start_month.setText(month + "");
		data_quire_start_date.setText(day + "");
		if (hour <= 10) {
			data_quire_start_time.setText("0" + (hour - 1));
		} else {
			data_quire_start_time.setText(hour - 1 + "");
		}
		if (mMinute <= 10) {
			data_quire_start_todata.setText("0" + mMinute);
		} else {
			data_quire_start_todata.setText(mMinute - 1 + "");
		}

		// 初始化结束时间
		data_quire_end_year.setText(year + "");
		data_quire_end_month.setText(month + "");
		data_quire_end_date.setText(day + "");
		if (hour < 10) {
			data_quire_end_time.setText("0" + hour);
		} else {
			data_quire_end_time.setText(hour + "");
		}
		if (mMinute < 10) {
			data_quire_end_todata.setText("0" + mMinute);
		} else {
			data_quire_end_todata.setText(mMinute + "");
		}

		for (int i = 1; i < 13; i++) {
			// 加入12月
			start_date_month.add(i + "");
		}

		for (int i = 0; i < 24; i++) {
			// 加入小时数
			if (i < 10) {
				start_time_data.add("0" + i);
			} else {
				start_time_data.add("" + i);
			}
		}
		for (int i = 0; i < 60; i++) {
			// 加入分钟数
			if (i < 10) {
				start_time_minute.add("0" + i);
			} else {
				start_time_minute.add("" + i);
			}
		}
		addEndData();// 初始化时根据开始时间初始化结束时间的天数
		// 请求数据
//		requestThunderLis();
//		requestThunderQuire();

	}

	/**
	 * 是否为闰年
	 * 
	 * @param year
	 *            年份
	 * @return
	 */
	private boolean isLeap(int year) {
		if (year % 4 == 0) {
			if (year % 100 != 0) {
				return true;
			} else if (year % 400 == 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 获取某年某月中的天数
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	private int getDayCount(int year, int month) {
		int result = 0;
		switch (month) {
		case 1:
		case 3:
		case 5:
		case 7:
		case 8:
		case 10:
		case 12:
			result = 31;
			break;
		case 4:
		case 6:
		case 9:
		case 11:
			result = 30;
			break;
		case 2: {
			if (isLeap(year)) {
				result = 29;
			} else {
				result = 28;
			}
			break;
		}
		}
		return result;
	}

	/**
	 * 根据当前年月获取当前的天数加入集合中
	 */
	private void getDayList(int id) {
		int year = 0;
		int month = 0;
		if (id == R.id.data_quire_start_date) {
			year = Integer.parseInt(data_quire_start_year.getText().toString());
			month = Integer.parseInt(data_quire_start_month.getText()
					.toString());
		} else {
			year = Integer.parseInt(data_quire_end_year.getText().toString());
			month = Integer.parseInt(data_quire_end_month.getText().toString());
		}

		int end = getDayCount(year, month);// 得到某年某月的有多少天
		start_date_data.clear();
		for (int i = 1; i < end + 1; i++) {
			start_date_data.add(i + "");
		}
	}

	/**
	 * 判断当前时间和结束时间是否超过数据可查询范围（查询周期最长5天）
	 */
	private long judgeDateTime() {
		SimpleDateFormat sff = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
		getStartTextValue();
		Date s_date = null;
		try {
			s_date = sff.parse(s_sumDate);// 转换开始时间
		} catch (ParseException e) {
			e.printStackTrace();
		}
		getEndTextValue();
		Date e_date = null;
		try {
			e_date = sff.parse(e_sumDate);// 转换结束时间
		} catch (ParseException e) {
			e.printStackTrace();
		}
		long less_date = e_date.getTime() - s_date.getTime();// 得到相差多少时间

		return less_date;
	}

	private Toast getToast(String info) {
		Toast.makeText(mActivity, info, Toast.LENGTH_SHORT).show();
		return toast;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.data_quire_start_year:// 开始年份
			createPopupWindow(data_quire_start_year, start_date_year,
					start_year, listener).showAsDropDown(data_quire_start_year);
			break;
		case R.id.data_quire_start_month:// 开始月份
			createPopupWindow(data_quire_start_month, start_date_month,
					start_month, listener).showAsDropDown(
					data_quire_start_month);
			break;
		case R.id.data_quire_start_date:// 开始日期
			getDayList(R.id.data_quire_start_date);
			createPopupWindow(data_quire_start_date, start_date_data,
					start_date, listener).showAsDropDown(data_quire_start_date);
			break;
		case R.id.data_quire_start_time:// 开始时间
			createPopupWindow(data_quire_start_time, start_time_data,
					start_time, listener).showAsDropDown(data_quire_start_time);
			break;
		case R.id.data_quire_start_todata:// 开始分钟
			createPopupWindow(data_quire_start_todata, start_time_minute,
					start_todata, listener).showAsDropDown(
					data_quire_start_todata);
			break;
		case R.id.data_quire_end_year:// 结束年份
			createPopupWindow(data_quire_end_year, start_date_year, toyear,
					listener).showAsDropDown(data_quire_end_year);
			break;
		case R.id.data_quire_end_month:// 结束月份
			createPopupWindow(data_quire_end_month, end_date_month, tomonth,
					listener).showAsDropDown(data_quire_end_month);
			break;
		case R.id.data_quire_end_date:// 结束日期
			getDayList(R.id.data_quire_end_date);
			createPopupWindow(data_quire_end_date, end_date_data, todata,
					listener).showAsDropDown(data_quire_end_date);
			break;
		case R.id.data_quire_end_time:// 结束时间
			createPopupWindow(data_quire_end_time, start_time_data, totime,
					listener).showAsDropDown(data_quire_end_time);
			break;
		case R.id.data_quire_end_todata:// 结束分钟
			createPopupWindow(data_quire_end_todata, start_time_minute,
					totodata, listener).showAsDropDown(data_quire_end_todata);
			break;
		case R.id.data_quire_province:// 城市
			resolveThunderCityLis();
			if (city_spinner_data != null && city_spinner_data.size() > 0) {
				createPopupWindow(data_quire_province, city_spinner_data,
						city_spinner, listener).showAsDropDown(
						data_quire_province);
			}
			break;
		case R.id.data_quire_city:// 地区
			String cityname = data_quire_province.getText().toString();
			if (!cityname.equals("不限地市")) {
				if (town_spinner_data != null && town_spinner_data.size() > 0) {
					createPopupWindow(data_quire_city, town_spinner_data,
							town_spinner, listener).showAsDropDown(
							data_quire_city);
				}
			}
			break;
		case R.id.data_lightning_type:// 闪电类型
			createPopupWindow(data_lightning_type, data_Lightning_type,
					lightning_type, listener).showAsDropDown(
					data_lightning_type);

			break;
		case R.id.data_lightning__polarity:// 极性
			createPopupWindow(data_lightning__polarity,
					data_Lightning_polarity, lightning__polarity, listener)
					.showAsDropDown(data_lightning__polarity);

			break;
		case R.id.data_search_btn:
			long less_date1 = judgeDateTime();
			if (less_date1 <= 0) {
				getToast(mActivity.getString(R.string.text_toast_info_tow));// 显示结束时间不能早于开始时间
			} else {
				if ((less_date1 / 1000 / 60 / 60 / 24) < 6) {// 是否在查询的五天时间内
					requestThunderQuire();

				} else {
					getToast(mActivity.getString(R.string.text_toast_info_one));// 超过数据可查询范围，系统提示显示错误时间
				}
			}

			break;
		case R.id.tv_data_more:// 更多按钮
			// 跳转页面方法
			jumpActivity();
			if (packLocalThunderQuireDown != null) {
				try {
					if (TextUtils.isEmpty(packLocalThunderQuireDown.area_id)) {
						putBundle(packLocalThunderQuireDown.city_id);// 赋值方法和跳转页面
					} else {
						putBundle(packLocalThunderQuireDown.area_id);
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		default:
			break;
		}

	}

	/**
	 * 根据开始时间加入结束时间的月份和天数
	 */
	private void addEndData() {
		String s_years = data_quire_end_year.getText().toString();
		String s_months = data_quire_start_month.getText().toString();
		String s_dates = data_quire_start_date.getText().toString();
		c.set(Integer.parseInt(s_years), Integer.parseInt(s_months) - 1,
				Integer.parseInt(s_dates));
		end_date_data.clear();
		end_date_month.clear();
		end_date_data.add(s_dates + "");
		int is_month = 0;
		for (int i = 0; i < 5; i++) {
			c.add(Calendar.DAY_OF_MONTH, 1);
			int day3 = c.get(Calendar.DAY_OF_MONTH);
			int month = (c.get(Calendar.MONTH)) + 1;// 获取当前月份
			if (i == 0) {
				end_date_month.add(s_months + "");// 加入月份
				is_month = Integer.parseInt(s_months);
			}
			if (i > 0) {
				if (month != is_month) {
					end_date_month.add(month + "");// 加入月份
					is_month = month;
				}
			}
			end_date_data.add(day3 + "");
		}
		data_quire_end_date.setText(s_dates);
		data_quire_end_month.setText(s_months);
	}

	private final DrowListClick listener = new DrowListClick() {
		@Override
		public void itemClick(int floag, int item) {
			switch (floag) {
			case start_year:// 开始年份
				String s_year = data_quire_start_year.getText().toString();
				end_date_year.add(s_year);
				break;
			case start_month:// 开始月份
				data_quire_start_date.setText("1");// 每当选择新的月份时初始化开始日期
				addEndData();
				break;
			case start_date:// 开始日期
				addEndData();

				break;
			case start_time:// 开始时间
				String s_time = data_quire_start_time.getText().toString();
				end_time_data.add(s_time);
				break;
			case start_todata:// 开始分钟
				String s_todata = data_quire_start_todata.getText().toString();
				end_time_data.add(s_todata);
				break;
			case todata:
				break;
			case totime://
				break;
			case city_spinner:
				if (item == 0) {
					data_quire_city.setText("不限县市区");
					return;
				} else {
					data_quire_city.setText("不限县市区");
				}
				resolveThunderAreaLis(item - 1);

				break;
			case town_spinner:
				break;
			case lightning_type:
				String type = data_lightning_type.getText().toString();
				if (type.equals("不限")) {
					data_lightning__polarity.setText("不限");
				}
				break;
			}
		}
	};
	private PcsDataBrocastReceiver mReceiver = new PcsDataBrocastReceiver() {

		@Override
		public void onReceive(String nameStr, String errorStr) {
			if (packThunderListUp.getName().equals(nameStr)) {
				resolveThunderCityLis();

			} else if (packThunderQuireUp.getName().equals(nameStr)) {
                packThunderQuireDown = (PackThunderQuireDown) PcsDataManager.getInstance().getNetPack(PackThunderQuireUp.NAME);
				if (packThunderQuireDown == null) {
					return;
				}
                if (packThunderQuireDown != null) {
                    if (!TextUtils.isEmpty(packThunderQuireDown.des)) {
                        Message msg = Message.obtain();
                        msg.what = 1;
                        mHandler.sendMessage(msg);
                    }
                }

			}
			mActivity.dismissProgressDialog();
		}
	};

//	private class MyRunnable implements Runnable {
//
//		String josn;
//
//		public MyRunnable(String josn) {
//			this.josn = josn;
//		}
//
//		@Override
//		public void run() {
//			packThunderQuireDown = new PackThunderQuireDown(packThunderQuireUp);
//			packThunderQuireDown.fillData(josn);
//			if (packThunderQuireDown != null) {
//				if (!TextUtils.isEmpty(packThunderQuireDown.des)) {
//					Message msg = Message.obtain();
//					msg.what = 1;
//					mHandler.sendMessage(msg);
//				}
//			}
//		}
//	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				if (!TextUtils.isEmpty(packThunderQuireDown.des)) {
					tv_lightning_review.setText(packThunderQuireDown.des);
				}
				if (packThunderQuireDown != null
						&& packThunderQuireDown.area_list.size() > 0) {
					tv_data_more.setVisibility(View.GONE);
					lightning_quire_list.setVisibility(View.VISIBLE);
					data_lightning_list.setVisibility(View.VISIBLE);
				} else if (packThunderQuireDown != null
						&& packThunderQuireDown.area_list1.size() > 0) {
					if (packThunderQuireDown.area_list1.size() > 5) {
						tv_data_more.setVisibility(View.VISIBLE);
					} else {
						tv_data_more.setVisibility(View.GONE);
					}
					lightning_quire_list.setVisibility(View.VISIBLE);
					data_lightning_list.setVisibility(View.VISIBLE);
				} else if (packThunderQuireDown == null
						|| packThunderQuireDown.area_list1.size() == 0
						|| packThunderQuireDown.area_list.size() == 0) {
					lightning_quire_list.setVisibility(View.GONE);
					data_lightning_list.setVisibility(View.GONE);
					tv_data_more.setVisibility(View.GONE);
				}
				mAdapter.setData(packThunderQuireDown);
			}
		}
	};

	private String s_sumDate = "";

	private String e_sumDate = "";

	private String start_SunDate = "";

	private String end_SunDate = "";

	private String cid = "";

	private String aid = "";

	private String lightning_type2 = "";

	private String lightning_polarity = "";

	private String e_year_month_day;

	private String judge_end_time;

	private String judge_end_todata;

	private String s_year_month_day;

	private String judge_start_time;

	private String judge_start_todata;

	/**
	 * 解析城市下拉框数据
	 */
	private void resolveThunderCityLis() {
        packThunderListDown = (PackThunderListDown) PcsDataManager.getInstance().getNetPack(PackThunderListUp.NAME);
		if (packThunderListDown == null) {
			return;
        }
        // 取城市列表
        int cityCount = packThunderListDown.area_list1.size();
        if (cityCount > 0) {
            city_spinner_data.clear();
            city_spinner_data.add("不限地市");
            for (int i = 0; i < cityCount; i++) {
                city_spinner_data
                        .add(packThunderListDown.area_list1.get(i).city_name);
            }

        }

	}

	/**
	 * 解析地区列表数据
	 */
	private void resolveThunderAreaLis(int i) {
		// 取地区列表
		if (packThunderListDown != null
				&& packThunderListDown.area_list1.size() > 0) {
			int areaCount = packThunderListDown.area_list1.get(i).area_list
					.size();
			if (areaCount > 0) {
				town_spinner_data.clear();
				town_spinner_data.add("不限县市区");
				for (int j = 0; j < areaCount; j++) {
					town_spinner_data
							.add(packThunderListDown.area_list1.get(i).area_list
									.get(j).area_name);
				}

			}
		}

	}

	/** 地区列表请求 **/
	private void requestThunderLis() {
		PcsDataDownload.addDownload(packThunderListUp);
	}

	/**
	 * 获取开始时间文本框的值
	 */
	private void getStartTextValue() {
		// 获取开始时间
		String judge_start_year = data_quire_start_year.getText().toString();
		String judge_start_month = data_quire_start_month.getText().toString();
		if (Integer.parseInt(judge_start_month) < 10) {
			judge_start_month = "0" + judge_start_month;
		}
		String judge_start_day = data_quire_start_date.getText().toString();
		if (Integer.parseInt(judge_start_day) < 10) {
			judge_start_day = "0" + judge_start_day;
		}
		judge_start_time = data_quire_start_time.getText().toString();
		judge_start_todata = data_quire_start_todata.getText().toString();
		s_year_month_day = judge_start_year + judge_start_month
				+ judge_start_day;
		// 取时分秒字符串
		String s_time_todata_ss = judge_start_time + ":" + judge_start_todata
				+ ":" + "00";
		s_sumDate = s_year_month_day + " " + s_time_todata_ss;

	}

	/**
	 * 获取结束时间文本框的值
	 */
	private void getEndTextValue() {
		// 获取结束时间
		String judge_end_year = data_quire_end_year.getText().toString();
		String judge_end_month = data_quire_end_month.getText().toString();
		if (Integer.parseInt(judge_end_month) < 10) {
			judge_end_month = "0" + judge_end_month;
		}
		String judge_end_day = data_quire_end_date.getText().toString();
		if (Integer.parseInt(judge_end_day) < 10) {
			judge_end_day = "0" + judge_end_day;
		}
		judge_end_time = data_quire_end_time.getText().toString();
		judge_end_todata = data_quire_end_todata.getText().toString();
		e_year_month_day = judge_end_year + judge_end_month + judge_end_day
				+ "";
		// 取时分秒字符串
		String e_time_todata_ss = judge_end_time + ":" + judge_end_todata + ":"
				+ "00";
		e_sumDate = e_year_month_day + " " + e_time_todata_ss;

	}

	private void getCityInFoText() {
		getStartTextValue();
		start_SunDate = s_year_month_day + judge_start_time
				+ judge_start_todata;
		getEndTextValue();
		end_SunDate = e_year_month_day + judge_end_time + judge_end_todata;// 获取结束时间
		String quire_province = data_quire_province.getText().toString();
		if (quire_province.equals("不限地市")) {
			cid = "";
		} else {
			cid = packThunderListDown.getById(quire_province);
		}
		String quire_city = data_quire_city.getText().toString();
		if (quire_city.equals("不限县市区")) {
			aid = "";
		} else {
			aid = packThunderListDown.getByAreaId(cid, quire_city);
		}
		lightning_type2 = data_lightning_type.getText().toString();
		lightning_polarity = data_lightning__polarity.getText().toString();

		if (lightning_type2.equals("云闪")) {
			type = "ic";
		} else if (lightning_type2.equals("地闪")) {
			type = "cg";
		} else {
			type = "";
		}
		if (lightning_polarity.equals("正")) {
			polarity = "1";
		} else if (lightning_polarity.equals("负")) {
			polarity = "-1";
		} else {
			polarity = "";
		}
	}

	/** 雷电列表请求 **/
	private void requestThunderQuire() {
		getCityInFoText();// 获取各个文本框值
		mActivity.showProgressDialog();
		packThunderQuireUp.start_time = start_SunDate;
		packThunderQuireUp.end_time = end_SunDate;
		packThunderQuireUp.city_id = cid;
		packThunderQuireUp.area_id = aid;
		packThunderQuireUp.cg_ic = type;
		packThunderQuireUp.processflag = polarity;
		PcsDataDownload.addDownload(packThunderQuireUp);

		// 保存请求
		Util.setPreferencesValue(mActivity, "requestinfo",
				"packThunderQuireUp", packThunderQuireUp
						.toJSONObject().toString());
	}

	public ControlDefenseGuide(ActivityLightningMonitor activity) {
		this.mActivity = activity;
		init();
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
		layoutGuide.setVisibility(View.VISIBLE);
		requestThunderLis();
		requestThunderQuire();
		if (dataList.size() <= 0) {
			// requestData();
//			1
		}
	}

	/**
	 * 隐藏
	 */
	public void hide() {
		layoutGuide.setVisibility(View.GONE);
	}

	/**
	 * 初始化
	 */
	private void init() {
		layoutGuide = (LinearLayout) mActivity.findViewById(R.id.layout_guide);
		initView();
		initEvent();
		initData();
		initListView();
	}

	/**
	 * 初始化列表
	 */
	private void initListView() {
		mAdapter = new AdapterDataQuire(mActivity, packThunderQuireDown,
				btn_more_list);
		data_lightning_list = (MyListView) layoutGuide
				.findViewById(R.id.data_lightning_list);
		data_lightning_list.setAdapter(mAdapter);
	}

	/**
	 * 获取SharePreferences并且解析
	 */
	private void jumpActivity() {
		String josn = Util.getPreferencesValue(mActivity, "requestinfo",
				"packThunderQuireUp");
		if (josn == null) {
			return;
		}
		packLocalThunderQuireDown = new PackLocalThunderQuireDown();
		packLocalThunderQuireDown.fillData(josn);
	}

	/**
	 * Bundle赋值方法和跳转页面
	 */
	private void putBundle(String cid) {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		if (TextUtils.isEmpty(packLocalThunderQuireDown.city_id)) {
			bundle.putString("type", "2");// 1表示地区，2表示城市
		} else {
			bundle.putString("type", "1");
		}
		bundle.putString("code", cid);
		bundle.putString("start_time", packLocalThunderQuireDown.start_time);
		bundle.putString("end_time", packLocalThunderQuireDown.end_time);
		bundle.putString("cg_ic", packLocalThunderQuireDown.cg_ic);
		bundle.putString("processflag", packLocalThunderQuireDown.processflag);
		intent.putExtras(bundle);
		intent.setClass(mActivity, ActivityThunderMoreList.class);
		mActivity.startActivity(intent);
	}

	/** 按钮更多事件 */
	private LightningMoreBtn btn_more_list = new LightningMoreBtn() {
		@Override
		public void itemOnclick(int item) {
			// 跳转页面方法
			jumpActivity();
			if (packLocalThunderQuireDown != null) {
				try {
					String cid = (String) mAdapter.getItem(item);
					putBundle(cid);// 赋值方法和跳转页面

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	};

	private String type;

	private String polarity;

	private PackLocalThunderQuireDown packLocalThunderQuireDown;

	private Calendar c;

	private int day;

	/** 创建城市下拉选择列表 */
	public PopupWindow createPopupWindow(final TextView dropDownView,
			final List<String> dataeaum, final int floag,
			final DrowListClick listener) {
		AdapterData dataAdapter = new AdapterData(mActivity, dataeaum);
		View popcontent = LayoutInflater.from(mActivity).inflate(
				R.layout.pop_list_layout, null);
		ListView lv = (ListView) popcontent.findViewById(R.id.mylistviw);
		lv.setAdapter(dataAdapter);
		final PopupWindow pop = new PopupWindow(mActivity);
		pop.setContentView(popcontent);
		pop.setOutsideTouchable(false);
		pop.setWidth(dropDownView.getWidth());
		// 调整下拉框长度
		View mView = dataAdapter.getView(0, null, lv);
		mView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		int height = mView.getMeasuredHeight();
		height += lv.getDividerHeight();
		height *= 6; // item的高度*个数
		if (dataeaum.size() < 6) {
			pop.setHeight(LayoutParams.WRAP_CONTENT);
		} else {
			pop.setHeight(height);
		}
		pop.setFocusable(true);
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				pop.dismiss();
				dropDownView.setText(dataeaum.get(position));
				listener.itemClick(floag, position);
			}
		});
		return pop;
	}

}
