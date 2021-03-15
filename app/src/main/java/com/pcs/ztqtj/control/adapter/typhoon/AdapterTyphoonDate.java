package com.pcs.ztqtj.control.adapter.typhoon;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pcs.ztqtj.R;

import java.util.List;

/**
 * 台风路径云图和雷达图时间列表适配器
 * Created by tyaathome on 2017/8/29.
 */

public class AdapterTyphoonDate extends BaseAdapter {

    private List<String> listdata;

    public AdapterTyphoonDate(List<String> listdata) {
        this.listdata = listdata;
    }

    @Override
    public int getCount() {
        return this.listdata.size();
    }

    @Override
    public Object getItem(int position) {
        return this.listdata.get(position);
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
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_typhoon_date, parent, false);
            holder.tv = (TextView) convertView;
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv.setText(listdata.get(position));
        return convertView;
    }

    private static class ViewHolder {
        TextView tv;
    }

}
