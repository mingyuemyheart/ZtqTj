package com.pcs.ztqtj.control.adapter.air_qualitydetail;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.lib_ztqfj_v2.model.pack.net.airinfopack.AirRankNew;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Z 空气质量列表
 */
public class AdapterAirQualityRanking extends BaseAdapter {

    private Context context;
    public boolean isDown = true;
    public List<AirRankNew> airListData = new ArrayList<AirRankNew>();
    public boolean isAQI = true;

    public AdapterAirQualityRanking(Context context) {
        this.context = context;
    }

    public void setData(List<AirRankNew> list) {
        airListData.clear();
        airListData.addAll(list);
    }

    @Override
    public int getCount() {
        return airListData.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
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
            view = LayoutInflater.from(context).inflate(R.layout.item_airquality, null);
            holder.air_equence = (TextView) view.findViewById(R.id.air_equence);
            holder.air_province = (TextView) view.findViewById(R.id.air_province);
            holder.air_city = (TextView) view.findViewById(R.id.air_city);
            holder.air_num = (TextView) view.findViewById(R.id.air_num);
            holder.iv_num_flag = (ImageView) view.findViewById(R.id.iv_num_flag);
            holder.air_num_flag = (TextView) view.findViewById(R.id.air_num_flag);
            holder.lay_aqi= (LinearLayout) view.findViewById(R.id.lay_aqi);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }
        if (isAQI) {
            holder.lay_aqi.setGravity(Gravity.CENTER_VERTICAL);
            if (!TextUtils.isEmpty(airListData.get(position).rank_ud)) {
                int rank_ud = Integer.valueOf(airListData.get(position).rank_ud);
                if (rank_ud > 0) {
                    holder.iv_num_flag.setVisibility(View.VISIBLE);
                    holder.iv_num_flag.setBackgroundResource(R.drawable.direc_up);
                    holder.air_num_flag.setVisibility(View.VISIBLE);
                    holder.air_num_flag.setTextColor(Color.GREEN);
                    holder.air_num_flag.setText(airListData.get(position).rank_ud);
                } else if (rank_ud < 0) {
                    holder.iv_num_flag.setVisibility(View.VISIBLE);
                    holder.iv_num_flag.setBackgroundResource(R.drawable.direc_down);
                    holder.air_num_flag.setVisibility(View.VISIBLE);
                    holder.air_num_flag.setTextColor(Color.RED);
                    holder.air_num_flag.setText(""+Math.abs(rank_ud));
                } else {
                    holder.iv_num_flag.setVisibility(View.VISIBLE);
                    holder.iv_num_flag.setBackgroundResource(R.drawable.direc_null);
                    holder.air_num_flag.setText("0");
                    holder.air_num_flag.setTextColor(Color.WHITE);
                }
            } else {
                holder.iv_num_flag.setVisibility(View.VISIBLE);
                holder.iv_num_flag.setBackgroundResource(R.drawable.direc_null);
                holder.air_num_flag.setText("0");
            }
        } else {
            holder.lay_aqi.setGravity(Gravity.CENTER);
            holder.iv_num_flag.setVisibility(View.GONE);
            holder.air_num_flag.setVisibility(View.GONE);
        }
        try {
            if (isDown) {
                holder.air_equence.setText(position + 1 + "");
            } else {
                holder.air_equence.setText(airListData.size() - position + "");
            }
            holder.air_province.setText(airListData.get(position).province);
            holder.air_city.setText(airListData.get(position).city);
            holder.air_num.setText(airListData.get(position).num);

            if (isAQI) {
                if (Integer.parseInt(airListData.get(position).num) < 50) {
                    holder.air_num.setBackgroundResource(R.drawable.color_green);
                } else if (50 <= Integer.parseInt(airListData.get(position).num) && Integer.parseInt(airListData.get(position).num) < 100) {
                    holder.air_num.setBackgroundResource(R.drawable.color_yellow);
                } else if (100 <= Integer.parseInt(airListData.get(position).num) && Integer.parseInt(airListData.get(position).num) < 150) {
                    holder.air_num.setBackgroundResource(R.drawable.color_orange);
                } else if (150 <= Integer.parseInt(airListData.get(position).num) && Integer.parseInt(airListData.get(position).num) < 200) {
                    holder.air_num.setBackgroundResource(R.drawable.color_red);
                } else if (200 <= Integer.parseInt(airListData.get(position).num) && Integer.parseInt(airListData.get(position).num) < 300) {
                    holder.air_num.setBackgroundResource(R.drawable.color_violet);
                } else {
                    holder.air_num.setBackgroundResource(R.drawable.color_brown_red);
                }
                holder.air_num.setTextColor(context.getResources().getColor(R.color.AQI_textcolor));
            } else {
                holder.air_num.setBackgroundColor(context.getResources().getColor(R.color.alpha100));
                holder.air_num.setTextColor(context.getResources().getColor(R.color.text_white));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 优0~50：绿色
        // 良51~100：黄色
        // 轻度污染101~150：橙色
        // 中度污染151~200：红色
        // 重度污染201~300：紫色
        // 严重污染301~：褐红色
        return view;
    }

    private class Holder {
        public TextView air_equence;
        public TextView air_province;
        public TextView air_city;
        public TextView air_num, air_num_flag;
        public ImageView  iv_num_flag;
        public LinearLayout lay_aqi;
    }
}
