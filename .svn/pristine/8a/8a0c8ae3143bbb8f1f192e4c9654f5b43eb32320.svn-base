package com.pcs.ztqtj.control.tool;


import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 日期工具类
 * @author E.Sun
 * 2015年6月9日
 */
@SuppressLint("SimpleDateFormat")
public final class DateTool {

    /**
     * 日期格式转换器：星期
     */
    private SimpleDateFormat dateFormatWeek = new SimpleDateFormat("E");


    /**
     * 日期格式转换器：月份
     */
    private SimpleDateFormat dateFormatMonth = new SimpleDateFormat("MM月");

    /**
     * 日期格式转换器：日期（格式：d）
     */
    private SimpleDateFormat dateFormatDate = new SimpleDateFormat("d");

    /**
     * 日期格式转换器：日期（格式：MMdd）
     */
    private SimpleDateFormat dateFormatDate4 = new SimpleDateFormat("MMdd");

    /**
     * 日期格式转换器：日期（格式："yyyy年M月d日）
     */
    public SimpleDateFormat dateFormatDate6 = new SimpleDateFormat("yyyy年M月d日");

    /**
     * 日期格式转换器：日期（格式：yyyyMMdd）
     */
    public SimpleDateFormat dateFormatDate8 = new SimpleDateFormat("yyyyMMdd");

    /**
     * Map-公历节日/纪念日
     */
    private Map<String, String> festivalMap;

    /**
     * Map-纪念日
     */
    private Map<String, String> memorialDayMap;

    private static DateTool instance;
    private DateTool(){}
    public static DateTool getInstance() {
        if(instance == null) {
            instance = new DateTool();
        }
        return instance;
    }

    /**
     * 获取当前日期
     * @return
     */
    public String getDay() {
        return dateFormatDate.format(new Date());
    }

    /**
     * 获取指定时间月份
     * @return
     */
    public String getMonth(Date date) {
        return dateFormatMonth.format(date);
    }

    /**
     * 获取指定时间日期
     * @return
     */
    public String getDay(Date date) {
        return dateFormatDate.format(date);
    }

    /**
     * 获取当前日期；
     * @return "yyyy年M月d日；
     */
    public String getDate() {
        return dateFormatDate6.format(new Date());
    }

    /**
     * 获取指定日期；
     * @return "yyyy年M月d日；
     */
    public String getDate(Date date) {
        return dateFormatDate6.format(date);
    }

    /**
     * 获取当前时间为星期几
     * @return
     */
    public String getWeek() {
        return dateFormatWeek.format(new Date());
    }

    /**
     * 获取指定时间为星期几
     * @param date
     * @return
     */
    public String getWeek(Date date) {
        return dateFormatWeek.format(date);
    }

    /**
     * 获取当前时间公历节日
     * @return
     */
    public String getFestival() {
        if(festivalMap == null) {
            festivalMap = getFestivalMap();
        }

        String dateStr = dateFormatDate4.format(new Date());

        return festivalMap.get(dateStr);
    }

    /**
     * 获取指定时间公历节日
     * @param date
     * @return
     */
    public String getFestival(Date date) {
        if(festivalMap == null) {
            festivalMap = getFestivalMap();
        }

        String dateStr = dateFormatDate4.format(date);

        return festivalMap.get(dateStr);
    }

    /**
     * 获取当前时间纪念日
     * @return
     */
    public String getMemorialDay() {
        if(memorialDayMap == null) {
            memorialDayMap = getMemorialDayMap();
        }

        String dateStr = dateFormatDate4.format(new Date());

        return memorialDayMap.get(dateStr);
    }

    /**
     * 获取指定时间纪念日
     * @param date
     * @return
     */
    public String getMemorialDay(Date date) {
        if(memorialDayMap == null) {
            memorialDayMap = getMemorialDayMap();
        }

        String dateStr = dateFormatDate4.format(date);

        return memorialDayMap.get(dateStr);
    }

    /**
     * 获取公历节日Map
     * @return
     */
    private Map<String, String> getFestivalMap() {
        Map<String, String> map = new HashMap<String, String>();

        map.put("0101", "元旦");
        //map.put("0214", "情人节");
        map.put("0501", "劳动节");
        map.put("0601", "儿童节");
        map.put("1001", "国庆节");
        //map.put("1225", "圣诞节");

        return map;
    }

    /**
     * 获取纪念日Map
     * @return
     */
    private Map<String, String> getMemorialDayMap() {
        Map<String, String> map = new HashMap<String, String>();

        map.put("0312", "植树节");
        map.put("0321", "世界森林日");
        map.put("0322", "世界水日");
        map.put("0323", "世界气象日");
        map.put("0605", "世界环境日");
        map.put("0910", "教师节");

        return map;
    }


    /**
     * 比较两日期大小(cal < othercal返回值为false, cal >= othercal true)
     * @param cal
     * @param othercal
     * @return
     */
    public boolean compareTo(Calendar cal, Calendar othercal) {
        int nYear = cal.get(Calendar.YEAR);
        int nMonth = cal.get(Calendar.MONTH);
        int nDay = cal.get(Calendar.DATE);
        int nHour = cal.get(Calendar.HOUR);
        int nMinute = cal.get(Calendar.MINUTE);
        int nSecond = cal.get(Calendar.SECOND);
        int nOtherYear = othercal.get(Calendar.YEAR);
        int nOtherMonth = othercal.get(Calendar.MONTH);
        int nOtherDay = othercal.get(Calendar.DATE);
        int nOtherHour = cal.get(Calendar.HOUR);
        int nOtherMinute = cal.get(Calendar.MINUTE);
        int nOtherSecond = cal.get(Calendar.SECOND);
        boolean flag = false;
        if(nYear < nOtherYear) {
            flag = false;
        } else if(nYear == nOtherYear) {
            if(nMonth < nOtherMonth) {
                flag = false;
            } else if(nMonth == nOtherMonth) {
                if(nDay < nOtherDay) {
                    flag = false;
                } else if(nDay == nOtherDay){
                    if(nHour < nOtherHour) {
                        flag = false;
                    } else if(nHour == nOtherHour) {
                        if(nMinute < nOtherMinute) {
                            flag = false;
                        } else if(nMinute == nOtherMinute) {
                            if(nSecond < nOtherSecond) {
                                flag = false;
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
            } else {
                flag = true;
            }
        } else {
            flag = true;
        }
        return flag;
    }

}
