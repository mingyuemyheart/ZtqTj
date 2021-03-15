package com.pcs.ztqtj.control.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.model.pack.FestivalInfo;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterFestival extends BaseAdapter {

    private List<FestivalInfo> dataList;

    public AdapterFestival(List<FestivalInfo> dataList) {
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
        ViewHolder holder;
        if(convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_festival, parent, false);
            holder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
            holder.tvWeek = (TextView) convertView.findViewById(R.id.tv_week);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        FestivalInfo info = dataList.get(position);
        holder.tvName.setText(info.name);
        int month = info.calendar.get(Calendar.MONTH)+1;
        int day = info.calendar.get(Calendar.DAY_OF_MONTH);
        holder.tvTime.setText(month + "月" + day + "日");
        String week = new SimpleDateFormat("EE", Locale.CHINESE).format(info.calendar.getTimeInMillis());
        holder.tvWeek.setText(week);
        return convertView;
    }

    private static class ViewHolder {
        TextView tvName;
        TextView tvTime;
        TextView tvWeek;
    }
}
