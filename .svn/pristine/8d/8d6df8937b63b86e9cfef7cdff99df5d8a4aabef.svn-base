package com.pcs.ztqtj.control.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.pcs.ztqtj.R;
import com.pcs.lib_ztqfj_v2.model.pack.net.push.PackPushQueryTagTypeDown;

import java.util.List;

/**
 * Created by tyaathome on 2017/8/12.
 */

public class AdapterWarningTagType extends BaseAdapter {

    private List<PackPushQueryTagTypeDown.PushTagTypeBean> listData;

    public AdapterWarningTagType(List<PackPushQueryTagTypeDown.PushTagTypeBean> listData) {
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_warning_type, parent, false);
            holder = new ViewHolder();
            holder.cb = (CheckBox) convertView.findViewById(R.id.cb);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        PackPushQueryTagTypeDown.PushTagTypeBean bean = listData.get(position);
        holder.cb.setText(bean.name);
        boolean isCheck = !bean.ischeck.equals("0");
        holder.cb.setChecked(isCheck);
        holder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setData(position, isChecked);
            }
        });
        return convertView;
    }

    private void setData(int position, boolean isCheck) {
        if(listData.size() <= position) {
            return;
        }
        PackPushQueryTagTypeDown.PushTagTypeBean bean = listData.get(position);
        if(isCheck) {
            bean.ischeck = "1";
        } else {
            bean.ischeck = "0";
        }
        listData.set(position, bean);
        notifyDataSetChanged();
    }

    private class ViewHolder {
        public CheckBox cb;
    }

}
