package com.pcs.ztqtj.control.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.inter.InterfaceShowBg;
import com.pcs.ztqtj.control.tool.ChineseDateUtil;
import com.pcs.ztqtj.control.tool.DateTool;
import com.pcs.ztqtj.control.tool.youmeng.SolarTermsUtil;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.view.activity.calendar.ActivityCalendarSecond;
import com.pcs.lib.lib_pcs_v3.model.image.ImageFetcher;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityMain;
import com.pcs.lib_ztqfj_v2.model.pack.net.week.PackMainWeekWeatherDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.week.WeekWeatherInfo;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 一周天气GridView的适配器
 *
 * @author JiangZy
 */
public class AdapterWeekGridView extends BaseAdapter {
    // 上下文
    private Context mContext;
    // 当前选中position
    private int mCurrentPosition = 1;
    private List<WeekWeatherInfo> weekList;
    private ImageFetcher mImageFetcher;
    private InterfaceShowBg mShowBg;
    private Calendar calCurrent;
    private DateTool mDateTool;
    public AdapterWeekGridView(Context context,
                               ImageFetcher imageFetcher,
                               InterfaceShowBg mShowBg) {
        mContext = context;
        mImageFetcher = imageFetcher;
        this.mShowBg = mShowBg;
    }

    public void setClickPositon(int itemClick) {
        mCurrentPosition = itemClick;
        buttonChange();
        this.notifyDataSetChanged();
    }

    public void setUpData(PackMainWeekWeatherDown packWeek) {
        this.weekList = packWeek.getWeek();
        intiDay();
        buttonChange();
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return weekList == null ? 0 : weekList.size();
    }

    private View view;

    public void setView(View view) {
        this.view = view;
    }

    private void buttonChange() {
        if (view == null) {
            return;
        }
        if (mCurrentPosition >= weekList.size()) {
            return;
        }
        WeekWeatherInfo info = weekList.get(mCurrentPosition);

//      String weatherDesc = info.getWeatherContent(packCity.NAME);
        String weatherDesc = info.getWeatherContent("");
        TextView viewContentDesc = (TextView) view.findViewById(R.id.text_content_desc);
        if (!TextUtils.isEmpty(weatherDesc)) {
//            String[] str=weatherDesc.split("；");
//            String strs="";
//            String strs_2="";
//            if (str.length==0){
                viewContentDesc.setText(weatherDesc);
//            }else {
//                for (int i = 0; i < str.length; i++) {
//                    if (strs == "") {
//                        strs = strs + ToDBC(str[i]);
//                    } else {
//                        strs = strs + ToDBC(" ； ") + ToDBC(str[i]);
//                    }
//                }
//                viewContentDesc.setText(strs);
//            }
        }
        String path = weekList.get(mCurrentPosition).getWeatherBg();
        String pathThumb = weekList.get(mCurrentPosition).getWeatherThumb();
        if (!TextUtils.isEmpty(path)) {
            mShowBg.showBg(path, pathThumb);
        }
    }

    private  void intiDay(){
        if(weekList == null || weekList.size() <= 1) {
            return;
        }
        WeekWeatherInfo info = weekList.get(1);
        // 天气标题
        TextView viewTitle = (TextView) view.findViewById(R.id.text_title);
        TextView viewCanlder= (TextView) view.findViewById(R.id.text_Calander);
        PackLocalCityMain packCity = ZtqCityDB.getInstance().getCityMain();

        Calendar calStartDay = Calendar.getInstance();
        ChineseDateUtil c = new ChineseDateUtil(calStartDay);

        Date date = calStartDay.getTime();
        mDateTool = DateTool.getInstance();
        if (info != null && !TextUtils.isEmpty(info.gdt) && !TextUtils.isEmpty(info.week)) {
            viewTitle.setText( info.gdt + "" + info.week + "  " + c
                    .getChinaYear() + c.getChinaMonthString() + c.getChinaDayString() + "  " +getSolarTermAndFestival(date));
        }

        viewCanlder.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent3 = new Intent(mContext, ActivityCalendarSecond.class);
                mContext.startActivity(intent3);
            }
        });
    }

    //半角转为全角的代码
    public  String ToDBC(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i< c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
                continue;
            }if (c[i]> 65280&& c[i]< 65375)
                c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }


    /**
     * 获取公历节日/纪念日； 节日和纪念日在同一天的，只返回节日；
     *
     * @param date
     * @return
     */
    private String getFestivalOrMemorialDay(Date date) {
        String str;

        str = mDateTool.getFestival(date);
        if (str != null && !TextUtils.isEmpty(str)) {
            return str;
        }

        str = mDateTool.getMemorialDay(date);
        if (str != null && !TextUtils.isEmpty(str)) {
            return str;
        }

        return "";
    }

    private String getSolarTermAndFestival(Date date) {
        // 获取前一天
        Calendar calBefore = Calendar.getInstance();
        calBefore.setTime(date);
        calBefore.add(Calendar.DAY_OF_YEAR, -1);
        Date beforeDay = calBefore.getTime();
        // 前一天节气
        String beforeDaysSolarTermDay = getSolarTermDay(beforeDay);
        // 今天节气
        String todaySolarTermDay = getSolarTermDay(date);
        String str = "";
        // 如果当天节气和前一天节气相同则显示**节气，如果当天节气与前一天不同则表示今天是新节气，故显示今日节气
        if(beforeDaysSolarTermDay.equals(todaySolarTermDay)) {
            str = todaySolarTermDay + "节气";
        } else {
            str = "今日" + todaySolarTermDay;
        }

//        String festival = getFestivalOrMemorialDay(date);
//        if (!festival.equals("")) {
//            str += "/" + festival;
//        }
        return str;
    }

    private String getSolarTermDay(Date date) {
//        String str = "";
//        Calendar cal = Calendar.getInstance();
//        cal.setTimeInMillis(date.getTime());
//        int year = cal.get(Calendar.YEAR);
//        int month = cal.get(Calendar.MONTH);
//        int day = cal.get(Calendar.DATE);
//        Long[] solarTermList = ChineseDateUtil.getSolarTermInYear(year);
//        for (int i = 0; i < solarTermList.length - 1; i++) {
//            Calendar firstCal = Calendar.getInstance();
//            firstCal.setTimeInMillis(solarTermList[i]);
//            Calendar nextCal = Calendar.getInstance();
//            nextCal.setTimeInMillis(solarTermList[i + 1]);
//
//            if (mDateTool.compareTo(cal, firstCal) && !mDateTool.compareTo(cal, nextCal)) {
//                str = ChineseDateUtil.solarTerm[i];
//                break;
//            }
//            // 特殊情况
//            if (mDateTool.compareTo(cal, nextCal) && i == 22 ||
//                    !mDateTool.compareTo(cal, firstCal) && i == 0) {
//                str = ChineseDateUtil.solarTerm[23];
//                break;
//            }
//        }
//        return str;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        SolarTermsUtil util = new SolarTermsUtil(calendar);
        return util.getSolartermsNameNew();
    }


    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ItemView item = null;
        if (convertView == null) {
            item = new ItemView();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_week_weather, null);
            item.tv = (TextView) convertView.findViewById(R.id.week_tv);
            item.tv_low_temp = (TextView) convertView.findViewById(R.id.tv_low_temp);
            item.tv_hight_temp = (TextView) convertView.findViewById(R.id.tv_hight_temp);
            item.week_date = (TextView) convertView.findViewById(R.id.week_date);
            item.tv_speed = (TextView) convertView.findViewById(R.id.tv_speed);
            item.Img = (TextView) convertView.findViewById(R.id.week_bg);
            item.hightImg = (ImageView) convertView.findViewById(R.id.hight_temp);
            item.lowImg = (ImageView) convertView.findViewById(R.id.low_temp);
            item.fragement_week_item_layout = (LinearLayout) convertView.findViewById(R.id.fragement_week_item_layout);
            convertView.setTag(item);
        } else {
            item = (ItemView) convertView.getTag();
        }


        if (position == 0) {
            item.tv_low_temp.setVisibility(View.VISIBLE);
            item.tv_hight_temp.setVisibility(View.VISIBLE);
            item.hightImg.setVisibility(View.GONE);
            item.lowImg.setVisibility(View.GONE);
        } else {
            item.tv_low_temp.setVisibility(View.GONE);
            item.tv_hight_temp.setVisibility(View.GONE);
            item.hightImg.setVisibility(View.VISIBLE);
            item.lowImg.setVisibility(View.VISIBLE);
        }


        try {
            if (weekList == null || weekList.size() == 0) {
            } else {
                if (weekList.size() <= position) {
                    item.hightImg.setImageDrawable(null);
                    item.lowImg.setImageDrawable(null);
                    item.tv.setText("");
                    item.tv_speed.setText("");
                } else {
                    WeekWeatherInfo info = weekList.get(position);
                    if (position == 0) {
                        item.tv.setText("昨天");
                    } else if (position == 1) {
                        item.tv.setText("今天");
                    } else {
                        item.tv.setText(info.week);
                    }
                    item.tv_speed.setText(info.getSpeed());
                    try {
                        String str = info.gdt;
                        String[] strM = str.split("月");
                        String day = strM[1].replace("日", "");
                        if (day.equals("1")) {
                            item.week_date.setText(strM[0] + "月");
                        } else {
                            item.week_date.setText(strM[1]);
                        }
                    } catch (Exception e) {
                    }

                    String strDay = "weather_icon/daytime/w" + info.wd_day_ico + ".png";
                    String strNight = "weather_icon/night/n" + info.wd_night_ico + ".png";
                    BitmapDrawable drawableDay = mImageFetcher.getImageCache()
                            .getBitmapFromAssets(strDay);
                    BitmapDrawable drawableNight = mImageFetcher
                            .getImageCache().getBitmapFromAssets(strNight);
                    item.hightImg.setImageDrawable(drawableDay);
                    item.lowImg.setImageDrawable(drawableNight);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (weekList.size() <= position || mCurrentPosition >= weekList.size()) {
        } else {
            // 背景
            if (position == mCurrentPosition) {
                item.fragement_week_item_layout.setBackgroundResource(R.drawable.bg_week_sel);
            } else {
                item.fragement_week_item_layout.setBackgroundColor(mContext.getResources().getColor(
                        R.color.alpha100));
            }
        }
        item.fragement_week_item_layout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mCurrentPosition = position;
                setClickPositon(position);
                buttonChange();
            }
        });
        return convertView;
    }

    private class ItemView {
        TextView tv_low_temp;
        TextView tv_hight_temp;
        TextView tv;
        TextView week_date;
        TextView tv_speed;
        TextView Img;

        ImageView hightImg;
        ImageView lowImg;
        LinearLayout fragement_week_item_layout;
    }


}
