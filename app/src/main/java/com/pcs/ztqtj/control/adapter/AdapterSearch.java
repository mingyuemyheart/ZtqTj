package com.pcs.ztqtj.control.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.lib_ztqfj_v2.model.pack.net.airinfopack.PackAirRankDown.AirRankBean;

import java.util.ArrayList;
import java.util.List;

public class AdapterSearch extends BaseAdapter {

    private Context context;
    private List<AirRankBean> listData = new ArrayList<AirRankBean>();

    public AdapterSearch(Context context) {
        this.context = context;
    }

    public void setData(List<AirRankBean> list) {
        listData.clear();
        listData.addAll(list);
    }

    @Override
    public int getCount() {
        return listData.size();
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
        TextView tv;
        if (view == null) {
            view = parent.inflate(context, R.layout.item_search, null);
            tv = (TextView) view.findViewById(R.id.search_tv);
            view.setTag(tv);
        } else {
            tv = (TextView) view.getTag();
        }
        if (listData.get(position).isCity) {
            tv.setText(listData.get(position).province + "      " + listData.get(position).city);
        } else {
            tv.setText(listData.get(position).province);
        }
        return view;
    }

}