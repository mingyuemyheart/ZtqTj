package com.pcs.ztqtj.control.adapter.livequery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackWdtjLowZdzDown.WdtjLowZdz;

import java.util.List;

public class AdapterTempertureLow extends BaseAdapter {
    private Context context;
    private List<WdtjLowZdz> temfalllist;

    public AdapterTempertureLow(Context context, List<WdtjLowZdz> temfalllist) {
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
            view = LayoutInflater.from(context).inflate(R.layout.item_livequery_lowtem, null);
            handler.countryText = (TextView) view.findViewById(R.id.livequerycounty);
            handler.livequeryrainmax = (TextView) view.findViewById(R.id.livequery_temper);
            handler.livequeryranktime = (TextView) view.findViewById(R.id.livequery_temper_time);
            view.setTag(handler);
        } else {
            handler = (Handler) view.getTag();
        }
        if (position == 0) {
            handler.countryText.setBackgroundResource(R.drawable.bg_livequery_item);
            handler.livequeryrainmax.setBackgroundResource(R.drawable.bg_livequery_item);
            handler.livequeryranktime.setBackgroundResource(R.drawable.bg_livequery_item);
        } else {
            handler.countryText.setBackgroundColor(context.getResources().getColor(R.color.alpha100));
            handler.livequeryrainmax.setBackgroundColor(context.getResources().getColor(R.color.alpha100));
            handler.livequeryranktime.setBackgroundColor(context.getResources().getColor(R.color.alpha100));
        }
        handler.countryText.setText(temfalllist.get(position).county);
        handler.livequeryrainmax.setText(temfalllist.get(position).min_wd);
        handler.livequeryranktime.setText(temfalllist.get(position).time);
        return view;
    }

    private class Handler {
        public TextView countryText;
        public TextView livequeryrainmax;
        public TextView livequeryranktime;
    }
}