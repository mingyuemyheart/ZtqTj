package com.pcs.ztqtj.view.activity.calendar;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.ChineseDateUtil;
import com.pcs.ztqtj.control.tool.youmeng.SolarTermsUtil;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackHolidayInfoDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackHolidayInfoUp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 日历适配器
 */
class CalendarAdapterSecond extends BaseAdapter {

    private int yellowPointPosition;
    private Context mContext;
    private ArrayList<Calendar> mCalendars;
    /**
     * 本地备忘记录标记
     **/
    private static String LocalDataName = "ScheduleList";

    private Calendar todayCalendar;
    private int tMouth;

    public void setPosition(int position) {
        this.yellowPointPosition = position;
    }

    public void setCalendarMouth(int mouth) {
        tMouth = mouth;

    }

    private SimpleDateFormat sf;
    private SimpleDateFormat sfHoliday;
    private Date dateinstance;


    private PackHolidayInfoDown packLocalInit;

    public CalendarAdapterSecond(Context context, ArrayList<Calendar> mCalendars) {
        this.mContext = context;
        this.mCalendars = mCalendars;
        this.todayCalendar = Calendar.getInstance();
        sf = new SimpleDateFormat("yyyyMMdd");
        sfHoliday = new SimpleDateFormat("yyyy-MM-dd");
        dateinstance = new Date();
        packLocalInit = (PackHolidayInfoDown) PcsDataManager.getInstance().getNetPack(PackHolidayInfoUp.NAME);
    }

    @Override
    public int getCount() {
        return mCalendars.size();
    }

    @Override
    public Object getItem(int position) {
        return mCalendars.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View myView, ViewGroup parent) {
        Holder holder = null;
        if (myView == null) {
            myView = View.inflate(mContext, R.layout.list_item_calendar, null);
            holder = new Holder();
            holder.dateTv = (TextView) myView.findViewById(R.id.list_item_textview);
            holder.todayPosition = (View) myView.findViewById(R.id.today_position);
            holder.tv2 = (TextView) myView.findViewById(R.id.list_item_chinese_date);
            holder.point_iv = (ImageView) myView.findViewById(R.id.point_iv);
            holder.work = (ImageView) myView.findViewById(R.id.work);
            holder.holiday = (ImageView) myView.findViewById(R.id.holiday);
            myView.setTag(holder);
        } else {
            holder = (Holder) myView.getTag();
        }
        myView.setBackgroundResource(0);

        Calendar currentCalendar = mCalendars.get(position);
        SolarTermsUtil util = new SolarTermsUtil(currentCalendar);

        String sDay = currentCalendar.get(Calendar.DAY_OF_MONTH) + "";
        holder.dateTv.setText(sDay);
        holder.dateTv.setTextColor(Color.parseColor("#5C5C5C"));


        int cMouth = currentCalendar.get(Calendar.MONTH);
        int cYear = currentCalendar.get(Calendar.YEAR);
        String solarTerms = util.getSolartermsName();

//		显示节气
        if (!TextUtils.isEmpty(solarTerms)) {
            holder.tv2.setText(solarTerms);
            holder.tv2.setTextColor(Color.parseColor("#3f9bf8"));
        } else {
            holder.tv2.setTextColor(Color.parseColor("#A1A1A1"));
            ChineseDateUtil c = new ChineseDateUtil(mCalendars.get(position));
            holder.tv2.setText(c.getChinaDayString());
        }

        ChineseDateUtil cdu = new ChineseDateUtil(currentCalendar);
        String festival = cdu.getChineseFestival();
        if (!TextUtils.isEmpty(festival)) {
            holder.tv2.setText(festival);
            holder.tv2.setTextColor(Color.parseColor("#ff0000"));
        }

//有节日则显示节日
        if (packLocalInit != null) {
            String day = sfHoliday.format(new Date(currentCalendar.getTimeInMillis()));
            if (packLocalInit.holiday_list_String.contains(day)) {
                holder.holiday.setVisibility(View.VISIBLE);
            } else {
                holder.holiday.setVisibility(View.GONE);

            }
            if (packLocalInit.work_list_String.contains(day)) {
                holder.work.setVisibility(View.VISIBLE);
            } else {
                holder.work.setVisibility(View.GONE);
            }
        }


        if (position == yellowPointPosition) {
            holder.dateTv.setBackgroundResource(R.drawable.calerder_check);
        } else {
            holder.dateTv.setBackgroundColor(mContext.getResources().getColor(R.color.alpha100));
        }
        if (compare(todayCalendar, currentCalendar)) {
            holder.todayPosition.setVisibility(View.VISIBLE);
        } else {
            holder.todayPosition.setVisibility(View.GONE);
        }
        // 非当月条目灰色显示
        if (cMouth != tMouth) {
            holder.dateTv.setTextColor(Color.parseColor("#A1A1A1"));
        }
        boolean flag = false;
        for (int i = 0; i < dataList.size(); i++) {
            dateinstance.setTime(dataList.get(i));
            if (sf.format(currentCalendar.getTime()).equals(sf.format(dateinstance))) {
                flag = true;
                break;
            }
        }
        if (flag) {
            holder.point_iv.setVisibility(View.VISIBLE);
        } else {
            holder.point_iv.setVisibility(View.GONE);
        }
        return myView;
    }

    private boolean compare(Calendar cal1 , Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }


    private List<Long> dataList = new ArrayList<Long>();//当月所有事件

    public void setData(List<String> acitonList, String key) {
        dataList.clear();
        for (int i = 0; i < acitonList.size(); i++) {
            try {
                String[] str = acitonList.get(i).split(key);
                if (str.length == 2) {
                    dataList.add(Long.parseLong(str[0]));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private class Holder {
        TextView dateTv;
        TextView tv2;
        ImageView point_iv;
        View todayPosition;

        public ImageView holiday;
        public ImageView work;

    }

}