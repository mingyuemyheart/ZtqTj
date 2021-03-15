package com.pcs.ztqtj.control.adapter.livequery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.YltjYear;

import java.util.List;

public class AdapterCompImage extends BaseAdapter {
    private Context context;
    private List<YltjYear> rainfalllist;

    public AdapterCompImage(Context context, List<YltjYear> rainfalllist) {
        this.context = context;
        this.rainfalllist = rainfalllist;
    }


    @Override
    public int getCount() {
        return rainfalllist.size();
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
            view = LayoutInflater.from(context).inflate(R.layout.item_livequery_compimage, null);
            handler.livequery_site = (TextView) view.findViewById(R.id.livequery_site);
            handler.livequery_mouth_a = (TextView) view.findViewById(R.id.livequery_mouth_a);
            handler.livequery_mouth_b = (TextView) view.findViewById(R.id.livequery_mouth_b);
            handler.livequery_mouth_c = (TextView) view.findViewById(R.id.livequery_mouth_c);
            handler.livequery_mouth_d = (TextView) view.findViewById(R.id.livequery_mouth_d);
            handler.livequery_mouth_e = (TextView) view.findViewById(R.id.livequery_mouth_e);
            handler.livequery_mouth_f = (TextView) view.findViewById(R.id.livequery_mouth_f);
            view.setTag(handler);
        } else {
            handler = (Handler) view.getTag();
        }
        if (position == 0) {

                handler.livequery_site.setBackgroundResource(R.drawable.bg_livequery_item);
                handler.livequery_mouth_a.setBackgroundResource(R.drawable.bg_livequery_item);
                handler.livequery_mouth_b.setBackgroundResource(R.drawable.bg_livequery_item);
                handler.livequery_mouth_c.setBackgroundResource(R.drawable.bg_livequery_item);
                handler.livequery_mouth_d.setBackgroundResource(R.drawable.bg_livequery_item);
                handler.livequery_mouth_e.setBackgroundResource(R.drawable.bg_livequery_item);
                handler.livequery_mouth_f.setBackgroundResource(R.drawable.bg_livequery_item);

            handler.livequery_site.setText("年份");
            handler.livequery_mouth_a.setText(rainfalllist.get(position).month_name1);
            handler.livequery_mouth_b.setText(rainfalllist.get(position).month_name2);
            handler.livequery_mouth_c.setText(rainfalllist.get(position).month_name3);
            handler.livequery_mouth_d.setText(rainfalllist.get(position).month_name4);
            handler.livequery_mouth_e.setText(rainfalllist.get(position).month_name5);
            handler.livequery_mouth_f.setText(rainfalllist.get(position).month_name6);
        } else {
            if (rainfalllist.get(position).year.equals("max_yaer") || rainfalllist.get(position).year.equals("m_year")) {
                handler.livequery_site.setText(historyMax);
            } else if (rainfalllist.get(position).year.equals("avg_yaer")) {
                handler.livequery_site.setText(historyAvg);
            } else {
                handler.livequery_site.setText(rainfalllist.get(position).year +usualDesc );
            }
            handler.livequery_mouth_a.setText(rainfalllist.get(position).month1);
            handler.livequery_mouth_b.setText(rainfalllist.get(position).month2);
            handler.livequery_mouth_c.setText(rainfalllist.get(position).month3);
            handler.livequery_mouth_d.setText(rainfalllist.get(position).month4);
            handler.livequery_mouth_e.setText(rainfalllist.get(position).month5);
            handler.livequery_mouth_f.setText(rainfalllist.get(position).month6);
        }
        return view;
    }


    private String historyMax="历史同期月最大降雨量",
            historyAvg ="历史同期月平均降雨量",usualDesc="年月累计降雨量";

    public void setDescVaule(String historyMax,String historyMin,String usualDesc){
        this.historyMax=historyMax;this.historyAvg =historyMin;
        this.usualDesc=usualDesc;
    }


    private class Handler {
        public TextView livequery_site;
        public TextView livequery_mouth_a;
        public TextView livequery_mouth_b;
        public TextView livequery_mouth_c;
        public TextView livequery_mouth_d;
        public TextView livequery_mouth_e;
        public TextView livequery_mouth_f;
    }
}
