package com.pcs.ztqtj.control.adapter.observation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.lib_ztqfj_v2.model.pack.net.observation.ItemObservationTable;

import java.util.List;

/**
 * Created by Z on 2016/8/24.
 */
public class AdapterObservationTable extends BaseAdapter {
    private List<ItemObservationTable> listData;

    public AdapterObservationTable(List<ItemObservationTable> listData) {
        this.listData = listData;
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
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
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_observation_table, null);
            holder.item_c1 = (TextView) view.findViewById(R.id.item_c1);
            holder.item_c2 = (TextView) view.findViewById(R.id.item_c2);
            holder.item_c3 = (TextView) view.findViewById(R.id.item_c3);
            holder.item_c4 = (TextView) view.findViewById(R.id.item_c4);
            holder.item_c5 = (TextView) view.findViewById(R.id.item_c5);
            holder.item_c6 = (TextView) view.findViewById(R.id.item_c6);

            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }
        ItemObservationTable bean = listData.get(position);

        holder.item_c1.setText(bean.time);
        holder.item_c2.setText(bean.tem);
        holder.item_c3.setText(bean.h_time);
        holder.item_c4.setText(bean.h_tem);
        holder.item_c5.setText(bean.l_time);
        holder.item_c6.setText(bean.l_tem);

        return view;
    }

    private class Holder {
        public TextView item_c1;
        public TextView item_c2;
        public TextView item_c3;
        public TextView item_c4;
        public TextView item_c5;
        public TextView item_c6;

    }
}
