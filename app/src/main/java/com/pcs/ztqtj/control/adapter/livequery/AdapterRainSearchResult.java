package com.pcs.ztqtj.control.adapter.livequery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackYltjTimeDown;

import java.util.List;

public class AdapterRainSearchResult extends BaseAdapter {
    private Context context;
    private List<PackYltjTimeDown> rainfalllist;

    public AdapterRainSearchResult(Context context,
                                   List<PackYltjTimeDown> rainfalllist) {
        this.context = context;
        this.rainfalllist = rainfalllist;
    }

    ;

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
            view = LayoutInflater.from(context).inflate(
                    R.layout.item_livequery_autorainfall_search_result, null);

            handler.livequery_time = (TextView) view
                    .findViewById(R.id.livequery_time);
            handler.livequery_count = (TextView) view
                    .findViewById(R.id.livequery_count);

            view.setTag(handler);
        } else {
            handler = (Handler) view.getTag();
        }
        if (position == 0) {
            handler.livequery_time
                    .setBackgroundResource(R.drawable.bg_livequery_item);
            handler.livequery_count
                    .setBackgroundResource(R.drawable.bg_livequery_item);

            handler.livequery_time.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                }
            });
            handler.livequery_count.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {


                }
            });
        }
        handler.livequery_time.setText(rainfalllist.get(position).tj_time);
        handler.livequery_count.setText(rainfalllist.get(position).jyzl);
        return view;
    }

    private class Handler {
        public TextView livequery_time;
        public TextView livequery_count;
    }
}