package com.pcs.ztqtj.control.adapter.air_quality;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.lib_ztqfj_v2.model.pack.net.airinfopack.PackAirPollutionDown;

import java.util.List;

/**
 * @author Z
 *         引导页适配器
 */
public class AdapterAirForecast extends BaseAdapter {

    private int selectItem = 0;

    public void selectItem(int item) {
        selectItem = item;
    }

    public int getSelectItem() {
        return selectItem;
    }

    public List<PackAirPollutionDown.ForecustItem> dataList;

    public AdapterAirForecast(List<PackAirPollutionDown.ForecustItem> dataList) {
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
    public View getView(int position, View view, ViewGroup parent) {
        Holder holder;
        if (view == null) {
            holder = new Holder();
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_air_forecast, null);
            holder.desc = (TextView) view.findViewById(R.id.desc);
            holder.item_layout = (LinearLayout) view.findViewById(R.id.item_layout);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }
        if (selectItem == position) {
//            holder.item_layout.setBackgroundResource(R.drawable.bg_air_forecast_select);
            holder.desc.setTextColor(parent.getContext().getResources().getColor(R.color.text_white));
            holder.desc.setBackgroundResource(R.drawable.bg_air_forecast_select);
        } else {
//            holder.desc.setTextColor(parent.getContext().getResources().getColor(R.color.text_blue_air));
            holder.desc.setTextColor(parent.getContext().getResources().getColor(R.color.text_black));
            holder.desc.setBackgroundResource(R.drawable.bg_air_forecast_nor);
//            holder.item_layout.setBackgroundResource(R.drawable.bg_air_forecast_nor);
        }
        PackAirPollutionDown.ForecustItem item = dataList.get(position);
        holder.desc.setText(item.mTime);
        return view;
    }

    private class Holder {
        public TextView desc;
        public LinearLayout item_layout;
    }


}