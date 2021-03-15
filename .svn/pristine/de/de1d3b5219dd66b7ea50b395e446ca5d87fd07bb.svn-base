package com.pcs.ztqtj.control.adapter.waterflood;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.lib_ztqfj_v2.model.pack.net.waterflood.WaterMonitorInfo;

import java.util.List;

/**
 * 气象站雨量信息适配器
 *
 * @author Administrator
 */
public class AdapterWaterMonitorStation extends BaseAdapter {

    private Context mContext = null;
    private List<WaterMonitorInfo> listdata = null;

    public AdapterWaterMonitorStation(Context context, List<WaterMonitorInfo> listdata) {
        mContext = context;
        this.listdata = listdata;
    }

    @Override
    public int getCount() {
        return listdata.size();
    }

    @Override
    public Object getItem(int position) {
        return listdata.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_water_monitor_station, null);
            holder.ll = (LinearLayout) convertView.findViewById(R.id.ll);
            holder.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
            holder.tvRain_h = (TextView) convertView.findViewById(R.id.tv_rain_h);
            holder.tvRain_d = (TextView) convertView.findViewById(R.id.tv_rain_d);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (position % 2 == 1) {
            holder.ll.setBackgroundResource(R.color.water_grid_head_grey);
        } else {
            holder.ll.setBackgroundResource(R.color.text_white);
        }

        WaterMonitorInfo ri = listdata.get(position);
        holder.tvTime.setText(ri.rectime);
//		if(ri.rain.equals("")) {
//			holder.tvRain_h.setText("暂无数据");
//		} else {
        holder.tvRain_h.setText(ri.js_h);
        if (ri.lev.equals("1")){
            holder.tvRain_d.setText("重度");
        }else if (ri.lev.equals("2")){
            holder.tvRain_d.setText("中度");
        }else if (ri.lev.equals("3")){
            holder.tvRain_d.setText("轻度");
        }else{
            holder.tvRain_d.setText("无积水");
        }

//		}
        return convertView;
    }

    private class ViewHolder {
        LinearLayout ll;
        TextView tvTime;
        TextView tvRain_h, tvRain_d;
    }

}
