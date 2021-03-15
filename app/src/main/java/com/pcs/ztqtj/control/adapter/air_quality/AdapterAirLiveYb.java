package com.pcs.ztqtj.control.adapter.air_quality;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pcs.lib_ztqfj_v2.model.pack.net.airinfopack.AirInfoYb;
import com.pcs.ztqtj.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Z 空气质量列表
 */
public class AdapterAirLiveYb extends BaseAdapter {

    private Context context;
    public List<AirInfoYb> airListData = new ArrayList<>();

    public AdapterAirLiveYb(Context context, ArrayList<AirInfoYb> list) {
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
            view = LayoutInflater.from(context).inflate(R.layout.item_airlive_yb, null);
            holder.tv_air_aqi = (TextView) view.findViewById(R.id.tv_air_aqi);
            holder.tv_aqi_index = (TextView) view.findViewById(R.id.tv_aqi_index);
            holder.tv_pri_pollutant = (TextView) view.findViewById(R.id.tv_air_pri_pollutant);

            holder.tv_pollutant_index = (TextView) view.findViewById(R.id.tv_air_pollutant_index);
            holder.tv_haze = (TextView) view.findViewById(R.id.tv_air_haze);
            holder.tv_sandstorm = (TextView) view.findViewById(R.id.tv_air_sandstorm);

            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }

        holder.tv_air_aqi.setText(airListData.get(position).aqi);
        holder.tv_aqi_index.setText(airListData.get(position).aqi_index);
        holder.tv_pri_pollutant.setText(airListData.get(position).pri_pollutant);
        holder.tv_pollutant_index.setText(airListData.get(position).pollutant_index);
        holder.tv_haze.setText(airListData.get(position).haze);
        holder.tv_sandstorm.setText(airListData.get(position).sandstorm);
        return view;
    }

    private class Holder {
        public TextView tv_air_aqi;
        public TextView tv_aqi_index;
        public TextView tv_pri_pollutant;
        public TextView tv_pollutant_index;
        public TextView tv_haze;
        public TextView tv_sandstorm;
    }
}
