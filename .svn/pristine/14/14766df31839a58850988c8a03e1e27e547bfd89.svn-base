package com.pcs.ztqtj.control.adapter.observation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.lib_ztqfj_v2.model.pack.net.observation.ItemObservationCompTable;

import java.util.List;

/**
 * Created by Z on 2016/8/24.
 */
public class AdapterObservationCompTable extends BaseAdapter {
    private List<ItemObservationCompTable> listData;


    public AdapterObservationCompTable(List<ItemObservationCompTable> listData) {
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
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_observation_comp_table, null);

            holder.item_c00 = (TextView) view.findViewById(R.id.item_c00);
            holder.item_c10 = (TextView) view.findViewById(R.id.item_c10);
            holder.item_c11 = (TextView) view.findViewById(R.id.item_c11);
            holder.item_c20 = (TextView) view.findViewById(R.id.item_c20);
            holder.item_c21 = (TextView) view.findViewById(R.id.item_c21);
            holder.item_c30 = (TextView) view.findViewById(R.id.item_c30);
            holder.item_c31 = (TextView) view.findViewById(R.id.item_c31);
            holder.item_c40 = (TextView) view.findViewById(R.id.item_c40);
            holder.item_c41 = (TextView) view.findViewById(R.id.item_c41);
            holder.item_c50 = (TextView) view.findViewById(R.id.item_c50);
            holder.item_c51 = (TextView) view.findViewById(R.id.item_c51);
            holder.item_c60 = (TextView) view.findViewById(R.id.item_c60);
            holder.item_c61 = (TextView) view.findViewById(R.id.item_c61);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }
        ItemObservationCompTable bean = listData.get(position);

        holder.item_c00.setText(bean.show_time);
        holder.item_c10.setText(bean.soil_tem);
        holder.item_c11.setText(bean.soil_time);
        holder.item_c20.setText(bean.cement_tem);
        holder.item_c21.setText(bean.cement_time);
        holder.item_c30.setText(bean.asphalt_tem);
        holder.item_c31.setText(bean.asphalt_time);
        holder.item_c40.setText(bean.brick_tem);
        holder.item_c41.setText(bean.brick_time);
        holder.item_c50.setText(bean.sandgravel_tem);
        holder.item_c51.setText(bean.sandgravel_time);
        holder.item_c60.setText(bean.tem);
        holder.item_c61.setText(bean.time);
        return view;
    }

    private class Holder {
        public TextView item_c00;
        public TextView item_c10;
        public TextView item_c11;
        public TextView item_c20;
        public TextView item_c21;
        public TextView item_c30;
        public TextView item_c31;
        public TextView item_c40;
        public TextView item_c41;
        public TextView item_c50;
        public TextView item_c51;
        public TextView item_c60;
        public TextView item_c61;

    }
}
