package com.pcs.ztqtj.control.adapter.air_quality;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pcs.ztqtj.R;


/**
 * 空气质量分级统计
 * Created by tyaathome on 2017/1/19.
 */

public class AdapterAirRankCount extends BaseAdapter {

    private Context context;
    private String[] datalist;
    private int[] iconList = {R.drawable.color_green, R.drawable.color_yellow,
            R.drawable.color_orange, R.drawable.color_red,
            R.drawable.color_violet, R.drawable.color_brown_red};

//    private static final int[] iconList = {R.color.AQI_0, R.color.AQI_1,
//            R.color.AQI_2, R.color.AQI_3,
//            R.color.AQI_4, R.color.AQI_5};

    public AdapterAirRankCount(Context context, String[] datalist) {
        this.context = context;
        this.datalist = datalist;
    }

    @Override
    public int getCount() {
        return datalist.length;
    }

    @Override
    public Object getItem(int position) {
        return datalist[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private int getRealPosition(int position) {
        //优、良、轻度、中度、重度、严重
        //优、中度、良、重度、轻度、严重
        int temp = 0;

        switch (position) {
            case 0:
                temp = 0;
                break;
            case 1:
                temp = 3;
                break;
            case 2:
                temp = 1;
                break;
            case 3:
                temp = 4;
                break;
            case 4:
                temp = 2;
                break;
            case 5:
                temp = 5;
                break;
        }
        return temp;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_air_rank_count, null);
            holder.icon = (ImageView) convertView.findViewById(R.id.iv);
            holder.tv = (TextView) convertView.findViewById(R.id.tv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.icon.setBackgroundResource(iconList[getRealPosition(position)]);
//        holder.icon.setBackgroundColor(parent.getContext().getResources().getColor(iconList[getRealPosition(position)]));
        holder.tv.setText(datalist[getRealPosition(position)]);

        return convertView;
    }

    private class ViewHolder {
        ImageView icon;
        TextView tv;
    }
}
