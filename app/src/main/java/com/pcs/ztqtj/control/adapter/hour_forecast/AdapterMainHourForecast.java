package com.pcs.ztqtj.control.adapter.hour_forecast;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.MyPackHourForecastDown;
import com.pcs.ztqtj.control.tool.ZtqImageTool;
import com.pcs.ztqtj.control.tool.utils.TextUtil;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

/**
 * 首页-逐时预报
 */
public class AdapterMainHourForecast extends BaseAdapter {

    private Context mContext;
    private List<MyPackHourForecastDown.HourForecast> mArrayList;

    public AdapterMainHourForecast(Context context, List<MyPackHourForecastDown.HourForecast> mArrayList) {
        mContext = context;
        this.mArrayList = mArrayList;
    }

    @Override
    public int getCount() {
        return mArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return mArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_mainhour_forecast_content, null);
            holder = new Holder();
            holder.textTime = convertView.findViewById(R.id.text_time);
            holder.iconWeather = convertView.findViewById(R.id.icon_weather);
            holder.textWeather = convertView.findViewById(R.id.text_weather);
            holder.textSw = convertView.findViewById(R.id.text_sw);
            holder.TextSpeed = convertView.findViewById(R.id.text_speed);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        MyPackHourForecastDown.HourForecast pack = mArrayList.get(position);
        // 天气现象
        if (pack.ico != null && !"".equals(pack.ico)) {
            try {
                Bitmap bitmap = ZtqImageTool.getInstance().getWeatherIcon(mContext, pack.isDayTime, pack.ico);
                holder.iconWeather.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            holder.iconWeather.setVisibility(View.INVISIBLE);
        }
        holder.textWeather.setText(pack.desc);
        holder.textSw.setText(pack.winddir);
        if (!TextUtil.isEmpty(pack.windspeed)) {
            if (position == 0) {
                holder.TextSpeed.setText(pack.windspeed+"m/s");
            } else {
                holder.TextSpeed.setText(pack.windspeed);
            }
        } else {
            holder.TextSpeed.setText(pack.windlevel);
        }
        holder.textTime.setText(pack.getTime());
        return convertView;
    }

    private class Holder {
        TextView textTime;//时间
        ImageView iconWeather;//天气图标
        TextView textWeather;//天气描述
        TextView textSw;//风向
        TextView TextSpeed;//风速
    }

    public String getWeek(Calendar c) {
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        String mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
        if ("1".equals(mWay)) {
            mWay = "天";
        } else if ("2".equals(mWay)) {
            mWay = "一";
        } else if ("3".equals(mWay)) {
            mWay = "二";
        } else if ("4".equals(mWay)) {
            mWay = "三";
        } else if ("5".equals(mWay)) {
            mWay = "四";
        } else if ("6".equals(mWay)) {
            mWay = "五";
        } else if ("7".equals(mWay)) {
            mWay = "六";
        }
        return " 星期" + mWay;
    }

}
