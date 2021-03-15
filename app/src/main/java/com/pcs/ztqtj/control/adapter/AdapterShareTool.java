package com.pcs.ztqtj.control.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pcs.ztqtj.R;

/**
 * 分享窗口适配器
 * Created by tyaathome on 2016/9/21.
 */
public class AdapterShareTool extends BaseAdapter {

    private Context context;
    private int[] iconList;
    private int[] nameList;

    public AdapterShareTool(Context context, int[] iconList, int[] nameList) {
        this.context = context;
        this.iconList = iconList;
        this.nameList = nameList;
    }

    @Override
    public int getCount() {
        return iconList.length;
    }

    @Override
    public Object getItem(int position) {
        return nameList[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_share_tools, null);
            holder = new ViewHolder();
            holder.icon = (ImageView) convertView.findViewById(R.id.icon);
            holder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.icon.setImageResource(iconList[position]);
        holder.tvName.setText(nameList[position]);
        return convertView;
    }

    private class ViewHolder {
        ImageView icon;
        TextView tvName;
    }
}
