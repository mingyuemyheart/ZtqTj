package com.pcs.ztqtj.control.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.model.SetsBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 设置
 */
public class AdapterRightSets extends BaseAdapter {

    private List<SetsBean> setsList = new ArrayList<>();

    public AdapterRightSets(List<SetsBean> setsList) {
        this.setsList = setsList;
    }

    @Override
    public int getCount() {
        return setsList.size();
    }

    @Override
    public Object getItem(int position) {
        return setsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sets, parent, false);
            holder = new ViewHolder();
            holder.iv = (ImageView) convertView.findViewById(R.id.iv);
            holder.tv = (TextView) convertView.findViewById(R.id.tv_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        SetsBean bean = setsList.get(position);
        if(bean != null) {
            holder.iv.setImageResource(bean.resid);
            holder.tv.setText(bean.name);
        }
        return convertView;
    }

    private class ViewHolder {
        public ImageView iv;
        public TextView tv;
    }
}
