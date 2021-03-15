package com.pcs.ztqtj.control.adapter;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.lib.lib_pcs_v3.model.image.ImageFetcher;
import com.pcs.lib_ztqfj_v2.model.pack.net.week.WeekWeatherInfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 旅游气象的一周天气
 *
 * @author chenjh
 */
public class AdapterTravelWeekWeather extends BaseAdapter {

    private Context mContext;
    private List<WeekWeatherInfo> dataList = new ArrayList<WeekWeatherInfo>();
    private ImageFetcher mImageFetcher;

    public AdapterTravelWeekWeather(Context context,
                                    List<WeekWeatherInfo> data, ImageFetcher imageFetcher) {
        this.mContext = context;
        dataList.addAll(data);
        mImageFetcher = imageFetcher;
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
            view = LayoutInflater.from(mContext).inflate(
                    R.layout.travel_pageone_item, null);
            holder.weekWeatherIcon = (ImageView) view
                    .findViewById(R.id.week_weather_icon);
            holder.weekWeatherIcon = (ImageView) view
                    .findViewById(R.id.week_weather_icon);
            holder.WeekTv = (TextView) view.findViewById(R.id.item_week);
            holder.WeatherTv = (TextView) view.findViewById(R.id.item_weather);
            holder.TempTv = (TextView) view.findViewById(R.id.item_temp);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }
        WeekWeatherInfo info = dataList.get(position);

        holder.WeekTv.setText(info.week);
        holder.WeatherTv.setText(info.weather);
        holder.TempTv.setText(info.higt + "/" + info.lowt + "°C");
        String path = "weather_icon/daytime/w" + info.wd_day_ico + ".png";
        BitmapDrawable drawable = mImageFetcher.getImageCache()
                .getBitmapFromAssets(path);
        holder.weekWeatherIcon.setImageDrawable(drawable);

        return view;
    }

    private class Holder {
        public ImageView weekWeatherIcon;
        public TextView WeekTv;
        public TextView WeatherTv;
        public TextView TempTv;
    }

    /**
     * 是否是白天
     *
     * @return
     */
    private String inTime() {
        Date date1 = new Date();
        date1.setHours(0);
        date1.setMinutes(0);
        Date date2 = new Date();
        date2.setHours(6);
        date2.setMinutes(40);
        Date date3 = new Date();
        date3.setHours(18);
        date3.setMinutes(30);
        Date now = new Date();
        if (now.after(date1) && now.before(date2)) {
            return "1";
        } else if (now.after(date2) && now.before(date3)) {
            return "2";
        } else {
            return "3";
        }
    }
}
