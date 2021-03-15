package com.pcs.ztqtj.control.adapter.livequery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackYltjRankDown.RainFallRank;

import java.util.List;

/**
 * @author Z 24小时最大降雨量排名（1、3）
 */
public class AdapterTempertureRainFall extends BaseAdapter {
    private Context context;
    private List<RainFallRank> temfalllist;
    public AdapterTempertureRainFall(Context context, List<RainFallRank> temfalllist) {
        this.context = context;
        this.temfalllist = temfalllist;
    }
    @Override
    public int getCount() {

        return temfalllist.size();
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

        Handler handler = null;

        if (view == null) {
            handler = new Handler();
            view = LayoutInflater.from(context).inflate(R.layout.item_livequery_rankrainfall, null);

            handler.countryText = (TextView) view.findViewById(R.id.livequerycounty);
            handler.livequeryrainmax = (TextView) view.findViewById(R.id.livequeryrainmax);
            handler.livequeryranktime = (TextView) view.findViewById(R.id.livequeryranktime);

            view.setTag(handler);
        } else {
            handler = (Handler) view.getTag();
        }

        RainFallRank itemInfo = temfalllist.get(position);

        if (position == 0) {
//            handler.countryText.setBackgroundResource(R.drawable.bg_livequery_item);
//            handler.livequeryrainmax.setBackgroundResource(R.drawable.bg_livequery_item);
//            handler.livequeryranktime.setBackgroundResource(R.drawable.bg_livequery_item);

            // handler.countryText.setTextColor(context.getResources().getColor(R.color.livequery_text_yellow));
            // handler.livequeryrainmax.setTextColor(context.getResources().getColor(R.color.livequery_text_yellow));
            // handler.livequeryranktime.setTextColor(context.getResources().getColor(R.color.livequery_text_yellow));
        } else {
            if (itemInfo.isThreeTime) {
                handler.countryText.setBackgroundColor(context.getResources().getColor(R.color.three_time));
                handler.livequeryrainmax.setBackgroundColor(context.getResources().getColor(R.color.three_time));
                handler.livequeryranktime.setBackgroundColor(context.getResources().getColor(R.color.three_time));
            } else {
                handler.countryText.setBackgroundColor(context.getResources().getColor(R.color.alpha100));
                handler.livequeryrainmax.setBackgroundColor(context.getResources().getColor(R.color.alpha100));
                handler.livequeryranktime.setBackgroundColor(context.getResources().getColor(R.color.alpha100));
            }
        }
        try {
            handler.countryText.setText(itemInfo.area_name);
            handler.livequeryrainmax.setText(itemInfo.rainfall);
            handler.livequeryranktime.setText(itemInfo.time);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    private class Handler {
        public TextView countryText;
        public TextView livequeryrainmax;
        public TextView livequeryranktime;

    }
}