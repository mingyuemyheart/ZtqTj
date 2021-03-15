package com.pcs.ztqtj.control.adapter.livequery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackWdtjZdzDown.WdtjZdz;

import java.util.List;

public class AdapterTempertureHight extends BaseAdapter {
    private Context context;
    private List<WdtjZdz> temfalllist;

    public AdapterTempertureHight(Context context, List<WdtjZdz> temfalllist) {
        this.context = context;
        this.temfalllist = temfalllist;
    }

    ;

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
            handler.livequerytemper = (TextView) view.findViewById(R.id.livequery_temper);
            handler.livequerytemtime = (TextView) view.findViewById(R.id.livequery_temper_time);
            view.setTag(handler);
        } else {
            handler = (Handler) view.getTag();
        }
        if (position == 0) {
            handler.countryText.setBackgroundResource(R.drawable.bg_livequery_item);
            handler.livequerytemper.setBackgroundResource(R.drawable.bg_livequery_item);
            handler.livequerytemtime.setBackgroundResource(R.drawable.bg_livequery_item);
        } else {
            handler.countryText.setBackgroundColor(context.getResources().getColor(R.color.alpha100));
            handler.livequerytemper.setBackgroundColor(context.getResources().getColor(R.color.alpha100));
            handler.livequerytemtime.setBackgroundColor(context.getResources().getColor(R.color.alpha100));
        }
        handler.countryText.setText(temfalllist.get(position).county);
        handler.livequerytemper.setText(temfalllist.get(position).max_wd);
        handler.livequerytemtime.setText(temfalllist.get(position).time);
        return view;
    }

    private class Handler {
        public TextView countryText;
        public TextView livequerytemper;
        public TextView livequerytemtime;
    }
}