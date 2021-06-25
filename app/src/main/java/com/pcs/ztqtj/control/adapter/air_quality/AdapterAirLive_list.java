package com.pcs.ztqtj.control.adapter.air_quality;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pcs.ztqtj.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 监测预报-空气质量-空气质量预报
 */
public class AdapterAirLive_list extends BaseAdapter {

    private Context context;
    public List<String> airListData = new ArrayList<>();

    public AdapterAirLive_list(Context context, ArrayList<String> list) {
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
            view = LayoutInflater.from(context).inflate(R.layout.item_airlive_list, null);
            holder.air_name = (TextView) view.findViewById(R.id.tv_air_name);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }

        holder.air_name.setText(airListData.get(position));

        return view;
    }

    private class Holder {
        public TextView air_name;
    }
}
