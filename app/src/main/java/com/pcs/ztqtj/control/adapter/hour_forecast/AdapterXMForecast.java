package com.pcs.ztqtj.control.adapter.hour_forecast;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pcs.lib_ztqfj_v2.model.pack.net.PackHourForecastDown.HourForecast;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.ZtqImageTool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tyaathome on 2019/01/14.
 */
public class AdapterXMForecast extends BaseAdapter {

    private List<HourForecast> dataList = new ArrayList<>();

    public AdapterXMForecast(List<HourForecast> dataList) {
        this.dataList = dataList;
    }

    @Override
    public int getCount() {
        return dataList == null ? 0 : dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList == null ? null : dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_xm_ocean_forecast, null);
            holder = new Holder();
            holder.textTime = (TextView) convertView.findViewById(R.id.text_time);
            holder.iconWeather = (ImageView) convertView.findViewById(R.id.icon_weather);
            holder.textSw = (TextView) convertView.findViewById(R.id.text_sw);
            holder.TextSpeed = (TextView) convertView.findViewById(R.id.text_speed);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        HourForecast pack = (HourForecast) getItem(position);
        if(pack != null) {
            // 天气现象
            if (pack.ico != null && !"".equals(pack.ico)) {
                try {
                    Bitmap bitmap = ZtqImageTool.getInstance().getWeatherIcon(
                            parent.getContext(), pack.isDayTime, pack.ico);
                    holder.iconWeather.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                holder.iconWeather.setVisibility(View.INVISIBLE);
            }
            holder.textSw.setText(pack.winddir);
            if (position == 0) {
                holder.TextSpeed.setText(pack.windspeed + "m/s");
            } else {
                holder.TextSpeed.setText(pack.windspeed);
            }
            holder.textTime.setText(pack.getTime());
        }
        return convertView;
    }

    private class Holder {
        TextView textTime;//时间
        ImageView iconWeather;//天气图标
        TextView textSw;//风向
        TextView TextSpeed;//风速
    }
}
