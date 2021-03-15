package com.pcs.ztqtj.view.activity.calendar;

import android.app.DatePickerDialog;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.AdapterFestival;
import com.pcs.ztqtj.control.adapter.AdapterSchedule;
import com.pcs.ztqtj.control.adapter.AdapterSchedule.DelBtnOnClick;
import com.pcs.ztqtj.control.tool.ChineseDateUtil;
import com.pcs.ztqtj.control.tool.CommUtils;
import com.pcs.ztqtj.control.tool.LocalDataHelper;
import com.pcs.ztqtj.control.tool.youmeng.SolarTermsUtil;
import com.pcs.ztqtj.model.pack.FestivalInfo;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 日历
 * 
 * @author WeiXJ
 * 
 */
public class ActivityCalendarSecond extends FragmentActivityZtqBase implements OnClickListener {
	public int notThisMonthCount = 0;
	private boolean isShow = true;
	private Calendar calCurrent;
	private Calendar eventCalCurrent;// 选择日期事件日期
	private ArrayList<Calendar> mCalendars = new ArrayList<Calendar>();
	private GestureDetector gesture;
	private CalendarAdapterSecond mAdapter;
	private GridView mGrid;
	private TextView txtCurdate;
	private View view;
	private Button btn_pre;
	private Button btn_next;
	private Button add_btn;
	private TextView should;
	private TextView canot;
	private TextView crash;
	private ListView scheduleListView;
	private TextView god;
	private TextView pzbj;
	private TextView xswf;
	private TextView tszf;
	private TextView rularDate;
	protected int todayPosition;
	private AdapterSchedule adapterSchedule;
	// private LinearLayout calenderDropView;
	private ScrollView bottomLayout;
	private List<String> ScheduleList = new ArrayList<String>();
	private List<String> scheduleListResource = new ArrayList<String>();
	/** 本地备忘记录标记 **/
	private static String LocalDataName = "ScheduleList";
	private PopupWindow mPopWindow; // 备忘输入弹出层
	private PopupWindow mPopWindowDel; // 备忘删除弹出层
	private View popupViewDel;
	private View popupView;
	private EditText pEditText; // 备忘编辑框
	private long selectData;// 当前选择的时间
	private ListView festivalListView;
	private AdapterFestival adapterFestival;
	private List<FestivalInfo> festivalList = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_calendar_second);
		setTitleText("日历中心");
		setRightTextButton(R.drawable.calendar_today_bg, "今", new OnClickListener() {
			@Override
			public void onClick(View v) {
				calCurrent = Calendar.getInstance();
				toToday();
			}
		});
		initView();
		initEvent();
		initData();
	}

	private void initView() {
		btn_pre = (Button) findViewById(R.id.btn_pre);
		btn_next = (Button) findViewById(R.id.btn_next);
		txtCurdate = (TextView) findViewById(R.id.txt_curdate);
		bottomLayout = (ScrollView) findViewById(R.id.bottomLayout);
		add_btn = (Button) findViewById(R.id.add_btn);
		mGrid = (GridView) findViewById(R.id.grid_calendar);
		initBotton();
		scheduleListView = (ListView) findViewById(R.id.scheduleListView);
		popupView = getLayoutInflater().inflate(R.layout.schedule_popup_view, null);
		popupViewDel = getLayoutInflater().inflate(R.layout.schedule_del_popup_view, null);
		festivalListView = (ListView) findViewById(R.id.list_festival);
	}

	private void initEvent() {
		txtCurdate.setOnClickListener(this);
		btn_pre.setOnClickListener(this);
		btn_next.setOnClickListener(this);
		add_btn.setOnClickListener(this);
		gesture = new GestureDetector(this, new MyOnGestureListener());
		mGrid.setOnTouchListener(new MyOnTouchListener());
		mGrid.setOnItemClickListener(myOnItemClickListener);
	}

	private void initData() {
		try {
			scheduleListResource = LocalDataHelper.LoadLocalEntityList(ActivityCalendarSecond.this, LocalDataName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		calCurrent = Calendar.getInstance();
		eventCalCurrent = calCurrent;
		mAdapter = new CalendarAdapterSecond(ActivityCalendarSecond.this, mCalendars);
		mGrid.setAdapter(mAdapter);
		adapterSchedule = new AdapterSchedule(this, ScheduleList, delListener);
		scheduleListView.setAdapter(adapterSchedule);
		adapterFestival = new AdapterFestival(festivalList);
		festivalListView.setAdapter(adapterFestival);
		toToday();
	}

	/* 初始化底部 */
	private void initBotton() {
		should = (TextView) findViewById(R.id.should);
		canot = (TextView) findViewById(R.id.canot);
		crash = (TextView) findViewById(R.id.crash);
		god = (TextView) findViewById(R.id.god);
		pzbj = (TextView) findViewById(R.id.pzbj);
		xswf = (TextView) findViewById(R.id.xswf);
		tszf = (TextView) findViewById(R.id.tszf);
		rularDate = (TextView) findViewById(R.id.rular_date);
	}

	/**
	 * 更新宜忌冲
	 * 
	 * @param cal
	 */
	private void reflashButtonUI(Calendar cal) {
		ChineseDateUtil c = new ChineseDateUtil(cal);
		cal.get(Calendar.YEAR);
		should.setText(c.getDayYi());
		canot.setText(c.getDayJi());
		crash.setText(c.getChongString());
		god.setText(c.getDayRijian());
		pzbj.setText(c.getPengzuBaiJI());
		xswf.setText(c.getXingsu());
		tszf.setText(c.getTaiShen());
		
		rularDate.setText(c.getChinaYearString() + c.getChinaMonthString() + c.getChinaDayString());
		// String dateStr = ChineseDateUtil.sdf.format(cal.getTime());
		// selectDate = dateStr + "";
	}

	// ====== 传入 月日的offset 传回干支, 0=甲子
	final private static String cyclicalm(int num) {
		final String[] Gan = new String[] { "甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸" };
		final String[] Zhi = new String[] { "子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥" };
		return (Gan[num % 10] + Zhi[num % 12]);
	}

	// ====== 传入 offset 传回干支, 0=甲子
	final public String cyclical(Calendar cal) {
		int num = cal.get(Calendar.YEAR) - 1900 + 36;
		return (cyclicalm(num));
	}

	/**
	 * 到今天
	 */
	private void toToday() {
		selectData = Calendar.getInstance().getTime().getTime();
		todayPosition = 0;
		Calendar calStartDay = getStartDayOfMouth(calCurrent);// 得到当月的第一天
		while (true) {
			if (calStartDay.equals(calCurrent)) {
				break;
			}
			calStartDay.add(Calendar.DAY_OF_MONTH, 1);
			todayPosition++;
		}
		toCalendar(calCurrent);
		reflashButtonUI(calCurrent);
	}

	/**
	 * 重新选择时间
	 */
	private void showPickerDialog() {
		DatePickerDialog.OnDateSetListener callBack = new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				calCurrent.set(year, monthOfYear, dayOfMonth);
				toCalendar(calCurrent);
			}
		};
		Calendar calToday = Calendar.getInstance();
		int y = calToday.get(Calendar.YEAR);
		int m = calToday.get(Calendar.MONTH);
		int d = calToday.get(Calendar.DAY_OF_MONTH);
		new DatePickerDialog(this, callBack, y, m, d).show();
	}

	// private int currentMouth=0;//当前选择的月份

	/**
	 * 刷新时间
	 * 
	 * @param cal
	 */
	private void toCalendar(final Calendar cal) {
		isShow = true;
		setPreOrNextMonth(cal);
		txtCurdate.setText(ChineseDateUtil.sdf_ym_chinese.format(cal.getTime()));
		mCalendars.clear();
		Calendar calStartDay = getStartDayOfMouth(cal);// 得到当月的第一天
		int day = calStartDay.get(Calendar.DATE);
		int mouth = calStartDay.get(Calendar.MONTH);
		if (day == 1) {
		} else {
			if (mouth == 11) {
				mouth = 0;
			} else {
				mouth += 1;
			}
		}
		Levela: for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 7; j++) {
				Calendar calTemp = Calendar.getInstance();
				calTemp.setTimeInMillis(calStartDay.getTimeInMillis());
				int cmouth = calTemp.get(Calendar.MONTH);
				calStartDay.add(Calendar.DAY_OF_MONTH, 1);
				if (j == 0) {
					if (cmouth > mouth && mouth != 0) {
						break Levela;
					} else if (mouth == 0 && cmouth == 1) {
						break Levela;
					} else if (mouth == 11 && cmouth == 0) {
						break Levela;
					}
				}
				mCalendars.add(calTemp);
			}
		}
		mAdapter.setPosition(todayPosition);
		mAdapter.setCalendarMouth(mouth);
		mAdapter.notifyDataSetChanged();
		reflashSchedule();
		refreshFestivalList(cal);
	}

	/**
	 * 得到当月的第一天
	 *
	 * @return
	 */
	private Calendar getStartDayOfMouth(Calendar cal) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(cal.getTimeInMillis());
		Calendar calStartDay = Calendar.getInstance();
		calStartDay.setTimeInMillis(c.getTimeInMillis());
		calStartDay.set(Calendar.DAY_OF_MONTH, 1);
		int iDayOfWeek = calStartDay.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY;// 一周的第一天为周末
		calStartDay.add(Calendar.DAY_OF_MONTH, -iDayOfWeek);
		return calStartDay;
	}

	public void setPreOrNextMonth(Calendar calNow) {
		String nowMonth = ChineseDateUtil.sdf_m.format(calNow.getTime());
		int currMonth = Integer.valueOf(nowMonth);
		int preMonth = 0;
		int NextMonth = 0;
		switch (currMonth) {
		case 1:
			preMonth = 12;
			NextMonth = currMonth + 1;
			break;
		case 12:
			preMonth = currMonth - 1;
			NextMonth = 1;
			break;
		default:
			preMonth = currMonth - 1;
			NextMonth = currMonth + 1;
			break;
		}
		btn_pre.setText(preMonth + "月");
		btn_next.setText(NextMonth + "月");
		btn_pre.setVisibility(View.VISIBLE);
		btn_next.setVisibility(View.VISIBLE);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.txt_curdate:
			// 当前日期点击事件
			// showPickerDialog();
			break;
		case R.id.btn_pre:
			eventCalCurrent=null;
			todayPosition = -1;
			selectData = 0;// 清空选择的日期
			calCurrent.add(Calendar.MONTH, -1);
			toCalendar(calCurrent);
			break;
		case R.id.btn_next:
			eventCalCurrent=null;
			todayPosition = -1;
			selectData = 0;// 清空选择的日期
			calCurrent.add(Calendar.MONTH, 1);
			toCalendar(calCurrent);
			break;
		case R.id.add_btn:
			if (todayPosition == -1) {
				if (toast == null) {
					toast = Toast.makeText(this, "请先选择日期", Toast.LENGTH_SHORT);
				} else {
					toast.setText("请先选择日期");
				}
				toast.show();
			} else {
				showPopup();
				// mPopWindow.setAnimationStyle(R.style.anim_popup_dir);
				mPopWindow.showAsDropDown(findViewById(R.id.calendar_view), 0, 0);
				// 设置背景颜色变暗
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = .3f;
				getWindow().setAttributes(lp);
			}
			break;
		}
	}

	private Toast toast;

	private OnItemClickListener myOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		    Calendar calendar = mCalendars.get(position);
			reflashButtonUI(calendar);
			selectData =calendar.getTime().getTime();
			ActivityCalendarSecond.this.todayPosition = position;
			mAdapter.setPosition(position);
			mAdapter.notifyDataSetChanged();
			eventCalCurrent = calendar;
			reflashSchedule();
			refreshFestivalList(calendar);
		}
	};

	private class MyOnTouchListener implements OnTouchListener {
		@Override
		public boolean onTouch(View arg0, MotionEvent arg1) {
			gesture.onTouchEvent(arg1);
			return false;
		}
	}

	private class MyOnGestureListener extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			if (e2.getX() - e1.getX() > 150) {
				calCurrent.add(Calendar.MONTH, -1);
				toCalendar(calCurrent);
				return true;
			} else if (e2.getX() - e1.getX() < -150) {
				calCurrent.add(Calendar.MONTH, 1);
				toCalendar(calCurrent);
				return true;
			}
			return false;
		};
	}

	/**
	 * 弹出框
	 */
	public void showPopup() {
		if (mPopWindow == null) {
			mPopWindow = new PopupWindow(popupView, CommUtils.Dip2Px(ActivityCalendarSecond.this, 300), CommUtils.Dip2Px(ActivityCalendarSecond.this, 200), true);
			pEditText = (EditText) popupView.findViewById(R.id.myEditText);
			mPopWindow.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss() {
					// 设置背景颜色变暗
					WindowManager.LayoutParams lp = getWindow().getAttributes();
					lp.alpha = 1.0f;
					getWindow().setAttributes(lp);
				}
			});
		}
		// 设置可以获得焦点
		mPopWindow.setFocusable(true);
		// 设置弹窗内可点击
		mPopWindow.setTouchable(true);
		// 设置弹窗外可点击
		mPopWindow.setOutsideTouchable(true);
		mPopWindow.setBackgroundDrawable(new BitmapDrawable());
		popupView.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					if (mPopWindow != null && mPopWindow.isShowing()) {
						mPopWindow.dismiss();
					}
					return true;
				}
				return false;
			}
		});
		pEditText.setText("");// 清空输入框内容
		popupView.findViewById(R.id.save_btn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String contentStr = pEditText.getText().toString().trim();
				if (!TextUtils.isEmpty(contentStr)) {
					addSchedule(selectData + DATACONTEXT + contentStr);
					if (mPopWindow != null && mPopWindow.isShowing()) {
						mPopWindow.dismiss();
					}
				}
			}
		});

		popupView.findViewById(R.id.cancel_btn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (mPopWindow != null && mPopWindow.isShowing()) {
					mPopWindow.dismiss();
				}
			}
		});
		mPopWindow.showAtLocation(findViewById(R.id.calendar_view), Gravity.CENTER, 0, 0);
	}

	private final String DATACONTEXT = "_data_";// 日期（long）_data_事件类型

	/**
	 * 删除弹出框
	 */
	public void showPopupDel(final int del_position) {
		if (mPopWindowDel == null) {
			mPopWindowDel = new PopupWindow(popupViewDel, CommUtils.Dip2Px(ActivityCalendarSecond.this, 300), CommUtils.Dip2Px(ActivityCalendarSecond.this, 130), true);
			mPopWindowDel.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss() {
					// 设置背景颜色变暗
					WindowManager.LayoutParams lp = getWindow().getAttributes();
					lp.alpha = 1.0f;
					getWindow().setAttributes(lp);
				}
			});
		}

		// 设置可以获得焦点
		mPopWindowDel.setFocusable(true);
		// 设置弹窗内可点击
		mPopWindowDel.setTouchable(true);
		// 设置弹窗外可点击
		mPopWindowDel.setOutsideTouchable(true);

		mPopWindowDel.setBackgroundDrawable(new BitmapDrawable());

		popupViewDel.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					if (mPopWindowDel != null && mPopWindowDel.isShowing()) {
						mPopWindowDel.dismiss();
					}
					return true;
				}
				return false;
			}
		});

		popupViewDel.findViewById(R.id.save_btn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (mPopWindowDel != null && mPopWindowDel.isShowing()) {
					mPopWindowDel.dismiss();
				}
				delSchedule(del_position);
			}
		});

		popupViewDel.findViewById(R.id.cancel_btn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (mPopWindowDel != null && mPopWindowDel.isShowing()) {
					mPopWindowDel.dismiss();
				}
			}
		});
		mPopWindowDel.showAtLocation(findViewById(R.id.calendar_view), Gravity.CENTER, 0, 0);
	}

	/**
	 * 刷新日程数据
	 */
	private void reflashSchedule() {
		// System.out.println("刷新数据:" + LocalDataName);
		if(eventCalCurrent==null){
			cleanSchedule();
			return;
		}
		ScheduleList.clear();
		Calendar c = Calendar.getInstance();
		SimpleDateFormat simp = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date();
		try {
			for (int i = 0; i < scheduleListResource.size(); i++) {
				String[] str = scheduleListResource.get(i).split(DATACONTEXT);
				long timeLong = Long.parseLong(str[0]);
				// eventCalCurrent.getTimeInMillis()
				date.setTime(timeLong);
				if (simp.format(date).equals(simp.format(eventCalCurrent.getTime()))) {
					ScheduleList.add(str[1]);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		adapterSchedule.setData(ScheduleList);
		mAdapter.setData(scheduleListResource, DATACONTEXT);
		mAdapter.notifyDataSetChanged();
		adapterSchedule.notifyDataSetChanged();
	}

	private void refreshFestivalList(Calendar calendar) {
	    festivalList.clear();
	    int currentMonth = calendar.get(Calendar.MONTH);
	    int firstDay = calendar.getActualMinimum(Calendar.DAY_OF_MONTH);
	    int lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	    for(int i = firstDay; i <= lastDay; i++) {
	        Calendar cal = Calendar.getInstance();
	        cal.set(Calendar.MONTH, currentMonth);
            cal.set(Calendar.DAY_OF_MONTH, i);
            // 添加节日
            ChineseDateUtil chineseDateUtil = new ChineseDateUtil(cal);
            String festival = chineseDateUtil.getChineseFestival();
            if(!TextUtils.isEmpty(festival)) {
                FestivalInfo info = new FestivalInfo();
                info.name = festival;
                info.calendar = cal;
                festivalList.add(info);
            }
            // 添加节气
            SolarTermsUtil solarTermsUtil = new SolarTermsUtil(cal);
            String solarTerms = solarTermsUtil.getSolartermsName();
            if(!TextUtils.isEmpty(solarTerms)) {
                FestivalInfo info = new FestivalInfo();
                info.name = solarTerms;
                info.calendar = cal;
                festivalList.add(info);
            }
        }
        adapterFestival.notifyDataSetChanged();
    }

	/**
	 * 清空事件显示
	 */
	private void cleanSchedule(){
		try {
			ScheduleList.clear();
			adapterSchedule.setData(ScheduleList);
			adapterSchedule.notifyDataSetChanged();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 添加备忘信息
	 * 
	 * @param contentStr
	 */
	private void addSchedule(String contentStr) {
		scheduleListResource.add(contentStr);
		LocalDataHelper.SaveLocalDataList(ActivityCalendarSecond.this, scheduleListResource, LocalDataName);
		reflashSchedule();
	}

	/**
	 * 删除日程
	 * 
	 * @param del_position
	 */
	private void delSchedule(int del_position) {
		scheduleListResource.remove(del_position);
		LocalDataHelper.SaveLocalDataList(ActivityCalendarSecond.this, scheduleListResource, LocalDataName);
		reflashSchedule();
	}

	/**
	 * 点击删除按钮，弹出提示框
	 */
	private DelBtnOnClick delListener = new DelBtnOnClick() {
		@Override
		public void delOnclick(int position) {
			showPopupDel(position);
			// mPopWindow.setAnimationStyle(R.style.anim_popup_dir);
			mPopWindowDel.showAsDropDown(findViewById(R.id.calendar_view), 0, 0);
			// 设置背景颜色变暗
			WindowManager.LayoutParams lp = getWindow().getAttributes();
			lp.alpha = .3f;
			getWindow().setAttributes(lp);
		}
	};
}