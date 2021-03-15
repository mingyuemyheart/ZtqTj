package com.pcs.ztqtj.control.adapter.observation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pcs.ztqtj.R;

import java.util.List;

/**
 * Created by Z on 2016/8/24.
 */
public class AdapterObservation extends BaseAdapter {
    private List<ObservaItem> listData;

    public AdapterObservation(List<ObservaItem> listData) {
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
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_observation, null);
            holder.item_text = (TextView) view.findViewById(R.id.item_test);
            holder.item_img = (ImageView) view.findViewById(R.id.item_img);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }
        ObservaItem bean = listData.get(position);
        holder.item_text.setText(bean.itemInfo);
        holder.item_img.setImageResource(bean.imageResourse);
        return view;
    }

    private class Holder {
        public TextView item_text;
        public ImageView item_img;
    }
}
