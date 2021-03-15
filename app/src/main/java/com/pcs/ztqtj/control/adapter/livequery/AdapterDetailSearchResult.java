package com.pcs.ztqtj.control.adapter.livequery;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalStation;

import java.util.List;

public class AdapterDetailSearchResult extends BaseAdapter {

    private List<PackLocalStation> stationList;

    public AdapterDetailSearchResult(List<PackLocalStation> stationList) {
        this.stationList = stationList;
    }

    @Override
    public int getCount() {
        return stationList.size();
    }

    @Override
    public Object getItem(int position) {
        return stationList.get(position);
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
            view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_livequery_search_result, null);
            handler.livequery_time = (TextView) view.findViewById(R.id.item_text);
            view.setTag(handler);
        } else {
            handler = (Handler) view.getTag();
        }
        handler.livequery_time.setText(stationList.get(position).STATIONNAME);
        return view;
    }

    private class Handler {
        public TextView livequery_time;
    }
}