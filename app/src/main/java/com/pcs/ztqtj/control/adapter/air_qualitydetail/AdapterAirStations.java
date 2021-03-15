package com.pcs.ztqtj.control.adapter.air_qualitydetail;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pcs.ztqtj.R;

import java.util.ArrayList;
import java.util.List;

/**
 * JiangZy on 2016/8/25.
 */
public class AdapterAirStations extends BaseAdapter {
    private Context mContext;

    private List<String> mListContent = new ArrayList<String>();

    public AdapterAirStations(Context context, List<String> list) {
        mContext = context;
        mListContent.addAll(list);
    }

    @Override
    public int getCount() {
        return mListContent.size();
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
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_airquality_station, null);
        }
        TextView textView = (TextView) convertView.findViewById(R.id.text_content);
        textView.setText(mListContent.get(position));

        return convertView;
    }
}
