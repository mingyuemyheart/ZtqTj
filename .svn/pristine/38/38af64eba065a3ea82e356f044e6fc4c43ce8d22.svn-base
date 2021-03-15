package com.pcs.ztqtj.control.adapter.data_query;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.lib_ztqfj_v2.model.pack.net.dataquery.WeatherValue;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tyaathome on 2017/11/1.
 */

public class AdapterWeatherValue extends RecyclerView.Adapter<AdapterWeatherValue.ViewHolder> {

    private List<WeatherValue> dataList = new ArrayList<>();

    public AdapterWeatherValue(List<WeatherValue> list) {
        dataList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_weather_value, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        WeatherValue value = dataList.get(position);
        holder.tvDesc.setText(value.desc);
        holder.tvValue1.setText(value.value_1);
        holder.tvValue2.setText(value.value_2);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvDesc, tvValue1, tvValue2;
        private View layout;

        public ViewHolder(View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.layout);
            tvDesc = (TextView) itemView.findViewById(R.id.tv_desc);
            tvValue1 = (TextView) itemView.findViewById(R.id.tv_value_1);
            tvValue2 = (TextView) itemView.findViewById(R.id.tv_value_2);
        }
    }

}
