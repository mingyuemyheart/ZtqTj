package com.pcs.ztqtj.control.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.lib_ztqfj_v2.model.pack.net.expert.ItemExpert;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tyaathome on 2016/11/8.
 */
public class AdapterAgricultureWeather extends BaseAdapter {

    private Context context;
    private List<ItemExpert> dataList = new ArrayList<>();
    private int visibility = View.GONE;

    public AdapterAgricultureWeather(Context context, List<ItemExpert> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @Override
    public int getCount() {
        return dataList.size()+1;
    }

    @Override
    public Object getItem(int position) {
        if(position < dataList.size()-1) {
            return dataList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if(convertView == null){
            holder=new Holder();
            convertView= LayoutInflater.from(context).inflate(R.layout.item_agriculture_weather, null);
            holder.title=(TextView) convertView.findViewById(R.id.title);
            holder.time=(TextView) convertView.findViewById(R.id.time);
            holder.loading = (TextView) convertView.findViewById(R.id.loading);
            holder.content = convertView.findViewById(R.id.content);
            holder.line = convertView.findViewById(R.id.line);
            convertView.setTag(holder);
        }else{
            holder=(Holder) convertView.getTag();
        }

        if(position < dataList.size()) {
            holder.title.setText(dataList.get(position).title);
            holder.time.setText(dataList.get(position).release_time);
            holder.content.setVisibility(View.VISIBLE);
            holder.loading.setVisibility(View.GONE);
            holder.line.setVisibility(View.VISIBLE);
        } else if (position == dataList.size()) {
            holder.content.setVisibility(View.GONE);
            holder.loading.setVisibility(visibility);
            holder.line.setVisibility(View.GONE);
        }
        return convertView;
    }

    /**
     * 设置正在加载控件的显示
     * @param visibility
     */
    public void setLoadingVisibility(int visibility) {
        this.visibility = visibility;
    }

    class Holder{
        public TextView title;
        public TextView time;
        public TextView loading;
        public View content;
        public View line;
    }
}
