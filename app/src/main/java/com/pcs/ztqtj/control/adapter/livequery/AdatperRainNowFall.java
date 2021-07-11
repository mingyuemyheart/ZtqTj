package com.pcs.ztqtj.control.adapter.livequery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.ItemRainNow;
import com.pcs.ztqtj.R;

import java.util.List;

/**
 * 监测预报-实况查询-数据与统计-雨量查询-任意时段查询
 */
public class AdatperRainNowFall extends BaseAdapter {

    private List<ItemRainNow> rainfalllist;

    public AdatperRainNowFall(List<ItemRainNow> rainfalllist) {
        this.rainfalllist = rainfalllist;
    }

    @Override
    public int getCount() {
        return rainfalllist.size();
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
    public View getView(int position, View view, ViewGroup parent) {
        Handler handler = null;
        Context context = parent.getContext();
        if (view == null) {
            handler = new Handler();
            view = LayoutInflater.from(context).inflate(R.layout.item_livequery_rainnow, null);
            handler.rain_station = (TextView) view.findViewById(R.id.rain_station);
            handler.rain_value = (TextView) view.findViewById(R.id.rain_value);
//            handler.tvTime = (TextView) view.findViewById(R.id.tv_time);
            view.setTag(handler);
        } else {
            handler = (Handler) view.getTag();
        }
        if (position == 0) {
            handler.rain_station.setBackgroundResource(R.drawable.bg_livequery_item);
            handler.rain_value.setBackgroundResource(R.drawable.bg_livequery_item);
//            handler.tvTime.setBackgroundResource(R.drawable.bg_livequery_item);
        } else {
            handler.rain_station.setBackgroundColor(context.getResources().getColor(R.color.alpha100));
            handler.rain_value.setBackgroundColor(context.getResources().getColor(R.color.alpha100));
//            handler.tvTime.setBackgroundColor(context.getResources().getColor(R.color.alpha100));
        }
        ItemRainNow item = rainfalllist.get(position);
        handler.rain_station.setText(item.stat_name);
        handler.rain_value.setText(item.rainfall);
//        handler.tvTime.setText(item.time);
        return view;
    }

    private class Handler {
        public TextView rain_station;
        public TextView rain_value;
//        public TextView tvTime;
    }

}
