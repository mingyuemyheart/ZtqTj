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
import java.util.List;

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

}
