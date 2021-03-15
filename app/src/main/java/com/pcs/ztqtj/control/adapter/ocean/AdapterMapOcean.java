package com.pcs.ztqtj.control.adapter.ocean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pcs.lib_ztqfj_v2.model.pack.net.oceanweather.OceanWeatherInfo;
import com.pcs.ztqtj.R;

import java.util.ArrayList;


/**
 * 适配器：出行天气卡片
 *
 * @author JiangZY
 */
public class AdapterMapOcean extends BaseAdapter {

    private final Context mContext;
    private ArrayList<OceanWeatherInfo> list;

    public AdapterMapOcean(Context context, ArrayList<OceanWeatherInfo> mlist) {
        list = mlist;
        mContext = context;
    }


    @Override
    public int getCount() {
        return list.size();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        Handler handler = null;
        if (convertView == null) {
            handler = new Handler();
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_ocean_v, null);
            handler.tvWeather = convertView.findViewById(R.id.tv_ocean_weahter);
            handler.tvTime = convertView.findViewById(R.id.tv_ocean_time);
            handler.tvWind = convertView.findViewById(R.id.tv_ocean_wind);
            handler.tvDir = convertView.findViewById(R.id.tv_ocean_dir);
            handler.tvVis = convertView.findViewById(R.id.tv_ocean_vis);
            convertView.setTag(handler);
            // 点击事件
        } else {
            handler = (Handler) convertView.getTag();
        }
        if (list.get(position).weather.equals("") || list.get(position).weather.equals("null")) {
            handler.tvWeather.setText("暂无");
        } else {
            handler.tvWeather.setText(list.get(position).weather);
        }
        if (list.get(position).date.equals("") || list.get(position).date.equals("null")) {
            handler.tvTime.setText("暂无");
        } else {
            handler.tvTime.setText(list.get(position).date);
        }
        if (list.get(position).wind.equals("") || list.get(position).wind.equals("null")) {
            handler.tvWind.setText("暂无");
        } else {
            handler.tvWind.setText(list.get(position).wind);
        }
        if (list.get(position).windDir.equals("") || list.get(position).windDir.equals("null")) {
            handler.tvDir.setText("暂无");
        } else {
            handler.tvDir.setText(list.get(position).windDir);
        }
        if (list.get(position).vis.equals("") || list.get(position).vis.equals("null")) {
            handler.tvVis.setText("暂无");
        } else {
            handler.tvVis.setText(list.get(position).vis);
        }

        return convertView;
    }

    private class Handler {
        /**
         * 详情天气图标
         */
        private TextView tvWeather, tvTime, tvWind, tvDir, tvVis;
    }
}
