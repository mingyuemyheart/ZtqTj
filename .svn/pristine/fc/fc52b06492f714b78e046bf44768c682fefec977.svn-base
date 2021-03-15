package com.pcs.ztqtj.control.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.lib_ztqfj_v2.model.pack.net.radar.PackRadarListDown;

import java.util.List;

/**
 * listview 的适配器
 **/
public class MyListBaseAdapter extends BaseAdapter {
    private List<PackRadarListDown.StationInfo> dataList;

    public MyListBaseAdapter(List<PackRadarListDown.StationInfo> dataList) {
        this.dataList = dataList;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.typhoon_list_item, null);
        ((TextView) convertView)
                .setText(dataList.get(position).station_name);
        return convertView;
    }
}