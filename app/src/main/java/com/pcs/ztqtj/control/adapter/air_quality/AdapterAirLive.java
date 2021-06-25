package com.pcs.ztqtj.control.adapter.air_quality;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pcs.lib_ztqfj_v2.model.pack.net.airinfopack.AirInfoSh;
import com.pcs.ztqtj.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 监测预报-空气质量-空气质量预报
 */
public class AdapterAirLive extends BaseAdapter {

    private Context context;
    public List<AirInfoSh> airListData = new ArrayList<>();

    public AdapterAirLive(Context context, ArrayList<AirInfoSh> list) {
        this.context = context;
        this.airListData = list;
    }

    @Override
    public int getCount() {
        return airListData.size();
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
        Holder holder;
        if (view == null) {
            holder = new Holder();
            view = LayoutInflater.from(context).inflate(R.layout.item_airlive, null);
            holder.tv_air_aqi = (TextView) view.findViewById(R.id.tv_air_aqi);
            holder.air_so2 = (TextView) view.findViewById(R.id.tv_air_so2);
            holder.air_no2 = (TextView) view.findViewById(R.id.tv_air_no2);

            holder.air_pm10 = (TextView) view.findViewById(R.id.tv_air_pm10);
            holder.air_co = (TextView) view.findViewById(R.id.tv_air_co);
            holder.air_o3 = (TextView) view.findViewById(R.id.tv_air_o3);

            holder.air_pm2 = (TextView) view.findViewById(R.id.tv_air_pm2_5);
            holder.air_wr = (TextView) view.findViewById(R.id.tv_air_wr);
            holder.air_sy = (TextView) view.findViewById(R.id.tv_air_sywr);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }

        holder.tv_air_aqi.setText(airListData.get(position).aqi);
        holder.air_wr.setText(airListData.get(position).quality_lv);
        holder.air_sy.setText(airListData.get(position).pri_pollutant);
        holder.air_no2.setText(airListData.get(position).no2);
        holder.air_so2.setText(airListData.get(position).so2);
        holder.air_co.setText(airListData.get(position).co);
        holder.air_pm10.setText(airListData.get(position).pm10);
        holder.air_pm2.setText(airListData.get(position).pm25);
        holder.air_o3.setText(airListData.get(position).o3);

        return view;
    }

    private class Holder {
        public TextView tv_air_aqi;
        public TextView air_so2;
        public TextView air_no2;
        public TextView air_pm10;
        public TextView air_co;
        public TextView air_o3;
        public TextView air_pm2;
        public TextView air_wr;
        public TextView air_sy;
    }
}
