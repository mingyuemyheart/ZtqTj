package com.pcs.ztqtj.control.adapter.air_quality;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.lib_ztqfj_v2.model.pack.net.airinfopack.AirRankNew;

import java.util.List;

/**
 * Created by tyaathome on 2017/9/7.
 */

public class AdapterAirRank extends BaseAdapter {

    private Context context;
    private List<AirRankNew> airListData;
    private boolean isDown = true;
    private boolean isCenter = false;

    public AdapterAirRank(Context context, List<AirRankNew> datalist) {
        this.context = context;
        this.airListData = datalist;
    }

    @Override
    public int getCount() {
        return airListData.size();
    }

    @Override
    public Object getItem(int position) {
        return airListData.get(position);
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
            view = LayoutInflater.from(context).inflate(R.layout.item_airquality, parent, false);
            holder.air_equence = (TextView) view.findViewById(R.id.air_equence);
            holder.air_province = (TextView) view.findViewById(R.id.air_province);
            holder.air_city = (TextView) view.findViewById(R.id.air_city);
            holder.air_num = (TextView) view.findViewById(R.id.air_num);
            holder.layoutAqi = (LinearLayout) view.findViewById(R.id.lay_aqi);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }

        if (isDown) {
            holder.air_equence.setText(position + 1 + "");
        } else {
            holder.air_equence.setText(airListData.size() - position + "");
        }
        holder.air_province.setText(airListData.get(position).province);
        holder.air_city.setText(airListData.get(position).city);
        holder.air_num.setText(airListData.get(position).num);

        if (Integer.parseInt(airListData.get(position).num) < 50) {
            holder.air_num.setBackgroundResource(R.drawable.color_green);
        } else if (50 <= Integer.parseInt(airListData.get(position).num) && Integer.parseInt(airListData.get
                (position).num) < 100) {
            holder.air_num.setBackgroundResource(R.drawable.color_yellow);
        } else if (100 <= Integer.parseInt(airListData.get(position).num) && Integer.parseInt(airListData.get
                (position).num) < 150) {
            holder.air_num.setBackgroundResource(R.drawable.color_orange);
        } else if (150 <= Integer.parseInt(airListData.get(position).num) && Integer.parseInt(airListData.get
                (position).num) < 200) {
            holder.air_num.setBackgroundResource(R.drawable.color_red);
        } else if (200 <= Integer.parseInt(airListData.get(position).num) && Integer.parseInt(airListData.get
                (position).num) < 300) {
            holder.air_num.setBackgroundResource(R.drawable.color_violet);
        } else {
            holder.air_num.setBackgroundResource(R.drawable.color_brown_red);
        }
        holder.air_num.setTextColor(context.getResources().getColor(R.color.AQI_textcolor));
        // 优0~50：绿色
        // 良51~100：黄色
        // 轻度污染101~150：橙色
        // 中度污染151~200：红色
        // 重度污染201~300：紫色
        // 严重污染301~：褐红色

        if(isCenter) {
            holder.layoutAqi.setGravity(Gravity.CENTER);
        } else {
            holder.layoutAqi.setGravity(Gravity.CENTER_VERTICAL);
        }

        return view;
    }

    public void order() {
        isDown = !isDown;
        notifyDataSetChanged();
    }

    public void setCenter() {
        isCenter = true;
    }

    private class Holder {
        public TextView air_equence;
        public TextView air_province;
        public TextView air_city;
        public TextView air_num;
        public LinearLayout layoutAqi;
    }
}
