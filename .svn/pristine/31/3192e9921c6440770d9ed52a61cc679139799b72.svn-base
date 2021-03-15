package com.pcs.ztqtj.control.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackRainStandardDown;

import java.util.List;

/**
 * Created by Z on 2016/8/25.
 */
public class AdapterRainStandard extends BaseAdapter {
    private List<PackRainStandardDown.ItemRainStandard> dataList;

    public AdapterRainStandard(List<PackRainStandardDown.ItemRainStandard> dataList) {
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
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rain_standard, null);
            holder.item_desc = (TextView) view.findViewById(R.id.item_desc);
            holder.item_value = (TextView) view.findViewById(R.id.item_value);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }
        PackRainStandardDown.ItemRainStandard bean = dataList.get(position);
        holder.item_desc.setText(bean.level);
        holder.item_value.setText(bean.val);
        return view;
    }

    public class Holder {
        public TextView item_desc;
        public TextView item_value;
    }


}
