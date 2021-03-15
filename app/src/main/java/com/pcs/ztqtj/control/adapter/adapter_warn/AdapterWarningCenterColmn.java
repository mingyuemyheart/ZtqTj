package com.pcs.ztqtj.control.adapter.adapter_warn;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.view.myview.CheckableRelativeLayout;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.sh_warn.YjZqColumnGrade;

import java.util.List;

/**
 * 预警中心栏目适配器
 * Created by tyaathome on 2016/6/7.
 */
public class AdapterWarningCenterColmn extends BaseAdapter {

    private Context context;
    private List<YjZqColumnGrade> dataList;

    private int currentPosition = 0;

    public AdapterWarningCenterColmn(Context context, List<YjZqColumnGrade> dataList) {
        this.context = context;
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
        ViewHolder holder = null;
        if(convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_warning_center_column, parent, false);
            holder = new ViewHolder();
            holder.layout = (CheckableRelativeLayout)convertView.findViewById(R.id.layout);
            holder.tv = (CheckedTextView) convertView.findViewById(R.id.tv);
            holder.line = (ImageView) convertView.findViewById(R.id.line);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv.setText(dataList.get(position).name);
        if(position == dataList.size()-1
                || (currentPosition-1 == position && currentPosition < dataList.size())) {
            holder.line.setVisibility(View.GONE);
        } else {
            holder.line.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    public void setLine(int position) {
        currentPosition = position;
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        CheckableRelativeLayout layout;
        CheckedTextView tv;
        ImageView line;
    }

}
