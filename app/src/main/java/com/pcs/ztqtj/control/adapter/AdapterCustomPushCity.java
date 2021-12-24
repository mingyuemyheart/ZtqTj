package com.pcs.ztqtj.control.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCity;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.inter.ItemClickListener;

import java.util.List;

/**
 * Created by tyaathome on 2017/8/9.
 */

public class AdapterCustomPushCity extends BaseAdapter {

    private List<PackLocalCity> datalist;
    private ItemClickListener listener;

    public AdapterCustomPushCity(List<PackLocalCity> datalist, ItemClickListener listener) {
        this.datalist = datalist;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return datalist.size();
    }

    @Override
    public Object getItem(int position) {
        return datalist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Context context = parent.getContext();
        ViewHolder holder;
        if(convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_custom_push_city, parent, false);
            holder.tv = (TextView) convertView.findViewById(R.id.city_name);
            holder.btnDelete = (Button) convertView.findViewById(R.id.btn_delete);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv.setText(context.getString(R.string.custom_pushcity) + datalist.get(position).NAME);
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.itemClick(position);
            }
        });
        return convertView;
    }

    private class ViewHolder {
        TextView tv;
        Button btnDelete;
    }
}
