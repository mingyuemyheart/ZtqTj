package com.pcs.ztqtj.control.adapter.livequery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.model.pack.ItemLiveQuery;

import java.util.List;

/**
 * 监测预报-整点天气，实况查询的详情
 */
public class AdapterLiveQueryDetail extends BaseAdapter {
    private List<ItemLiveQuery> mItems;

    public AdapterLiveQueryDetail(List<ItemLiveQuery> items) {
        this.mItems = items;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHodler viewHodler = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_livequery_detail, null);
            viewHodler = new ViewHodler();
            viewHodler.item_over24_hour = (TextView) convertView.findViewById(R.id.item_over24_hour);
            viewHodler.item_over24_value = (TextView) convertView.findViewById(R.id.item_over24_value);
            viewHodler.item_future24_hour = (TextView) convertView.findViewById(R.id.item_future24_hour);
            viewHodler.item_future24_value = (TextView) convertView.findViewById(R.id.item_future24_value);
            convertView.setTag(viewHodler);
        } else {
            viewHodler = (ViewHodler) convertView.getTag();
        }
        Context c=parent.getContext();
//        if (position==0) {
//            viewHodler.item_over24_hour.setBackgroundColor(c.getResources().getColor(R.color.livequery_actual));
//            viewHodler.item_over24_value.setBackgroundColor(c.getResources().getColor(R.color.livequery_actual));
//            viewHodler.item_future24_hour.setBackgroundColor(c.getResources().getColor(R.color.livequery_prediction));
//            viewHodler.item_future24_value.setBackgroundColor(c.getResources().getColor(R.color.livequery_prediction));
//        }else{
//            viewHodler.item_over24_hour.setBackgroundColor(c.getResources().getColor(R.color.bg_white));
//            viewHodler.item_over24_value.setBackgroundColor(c.getResources().getColor(R.color.bg_white));
//            viewHodler.item_future24_hour.setBackgroundColor(c.getResources().getColor(R.color.bg_white));
//            viewHodler.item_future24_value.setBackgroundColor(c.getResources().getColor(R.color.bg_white));
//        }
        ItemLiveQuery info = mItems.get(position);
        viewHodler.item_over24_hour.setText(info.value_over24_hour);
        viewHodler.item_over24_value.setText(info.value_over24_value);
        viewHodler.item_future24_hour.setText(info.value_future24_hour);
        viewHodler.item_future24_value.setText(info.value_future24_value);
        return convertView;
    }

    public class ViewHodler {
        TextView item_over24_hour;
        TextView item_over24_value;
        TextView item_future24_hour;
        TextView item_future24_value;
    }


}
