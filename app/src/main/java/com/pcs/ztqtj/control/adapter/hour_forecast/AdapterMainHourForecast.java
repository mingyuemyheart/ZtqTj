package com.pcs.ztqtj.control.adapter.hour_forecast;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.ZtqImageTool;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityMain;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackHourForecastDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackHourForecastDown.HourForecast;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackHourForecastUp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

/**
 * 适配器：逐小时预报
 *
 * @author JiangZy
 */
@SuppressLint({"RtlHardcoded"})
public class AdapterMainHourForecast extends BaseAdapter {
    private Context mContext;
    /**
     * 数据列表
     */
    private List<HourForecast> mList = new ArrayList<HourForecast>();

    public AdapterMainHourForecast(Context context, List<HourForecast> mArrayList) {
        mContext = context;
        mList = mArrayList;
//        PackLocalCityMain packCity = ZtqCityDB.getInstance().getCityMain();
//        if (packCity == null) {
//            return;
//        }
//        PackHourForecastUp packUp = new PackHourForecastUp();
//        packUp.county_id = packCity.ID;
//        PackHourForecastDown down = (PackHourForecastDown) PcsDataManager.getInstance().getNetPack(packUp.getName());
//        mList.clear();
//        if (down != null) {
//            mList.addAll(down.list);
//        }
    }

//    @Override
//    public void notifyDataSetChanged() {
//        PackLocalCityMain packCity = ZtqCityDB.getInstance().getCityMain();
//        PackHourForecastUp packUp = new PackHourForecastUp();
//        packUp.county_id = packCity.ID;
//        PackHourForecastDown downData = (PackHourForecastDown) PcsDataManager.getInstance().getNetPack(packUp.getName());
//        mList.clear();
//        if (downData != null) {
//            mList.addAll(downData.list);
//        }
//        super.notifyDataSetChanged();
//    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {

        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_mainhour_forecast_content, null);
            holder = new Holder();
            holder.textTime = (TextView) convertView.findViewById(R.id.text_time);
            holder.iconWeather = (ImageView) convertView.findViewById(R.id.icon_weather);
            holder.textWeather = (TextView) convertView.findViewById(R.id.text_weather);
            holder.textSw = (TextView) convertView.findViewById(R.id.text_sw);
            holder.TextSpeed = (TextView) convertView.findViewById(R.id.text_speed);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        HourForecast pack = mList.get(position);
        // 天气现象
        if (pack.ico != null && !"".equals(pack.ico)) {
            try {
                Bitmap bitmap = ZtqImageTool.getInstance().getWeatherIcon(
                        mContext, pack.isDayTime, pack.ico);
                holder.iconWeather.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            holder.iconWeather.setVisibility(View.INVISIBLE);
        }
        holder.textWeather.setText(pack.desc);
        holder.textSw.setText(pack.winddir);
        if (position == 0) {
            holder.TextSpeed.setText(pack.windspeed + "m/s");
        } else {
            holder.TextSpeed.setText(pack.windspeed);
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

}
