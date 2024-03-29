package com.pcs.ztqtj.control.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pcs.lib_ztqfj_v2.model.pack.net.week.WeekWeatherInfo;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.util.CommonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 旅游气象的一周天气
 */
public class AdapterTravelWeekWeather extends BaseAdapter {

    private Context mContext;
    private List<WeekWeatherInfo> dataList = new ArrayList<>();

    public AdapterTravelWeekWeather(Context context, List<WeekWeatherInfo> data) {
        this.mContext = context;
        dataList.addAll(data);
    }

    @Override
    public int getCount() {
        int size = dataList.size();
        return size;
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
    public View getView(final int position, View view, ViewGroup parent) {
        Holder holder;
        if (view == null) {
            holder = new Holder();
            view = LayoutInflater.from(mContext).inflate(R.layout.travel_pageone_item, null);
            holder.day_weather_icon = (ImageView) view.findViewById(R.id.day_weather_icon);
            holder.night_weather_icon = (ImageView) view.findViewById(R.id.night_weather_icon);
            holder.WeekTv = (TextView) view.findViewById(R.id.item_week);
            holder.WeatherTv = (TextView) view.findViewById(R.id.item_weather);
            holder.tvTempDay = (TextView) view.findViewById(R.id.tvTempDay);
            holder.tvTempNight = (TextView) view.findViewById(R.id.tvTempNight);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }
        WeekWeatherInfo info = dataList.get(position);

        holder.WeekTv.setText(info.week);
        holder.tvTempDay.setText(info.higt+"°C");
        holder.tvTempNight.setText(info.lowt + "°C");

        Bitmap day = CommonUtil.getImageFromAssetsFile(mContext, "weather_icon/daytime/w" + info.wd_day_ico + ".png");
        if (day != null) {
            holder.day_weather_icon.setImageBitmap(day);
        }
        Bitmap night = CommonUtil.getImageFromAssetsFile(mContext, "weather_icon/night/n" + info.wd_night_ico + ".png");
        if (night != null) {
            holder.night_weather_icon.setImageBitmap(night);
        }
//        if (!TextUtils.equals(info.wd_day_ico, info.wd_night_ico)) {
            holder.WeatherTv.setText(info.wd_day+" / "+info.wd_night);
//        }

        return view;
    }

    private class Holder {
        public ImageView day_weather_icon;
        public ImageView night_weather_icon;
        public TextView WeekTv;
        public TextView WeatherTv;
        public TextView tvTempDay,tvTempNight;
    }

}
