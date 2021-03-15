package com.pcs.ztqtj.control.adapter.test_location;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalTestLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * JiangZy on 2016/11/30.
 * 测试定位
 */

public class AdapterTestLocation extends BaseAdapter {

    private List<PackLocalTestLocation> mList = new ArrayList<PackLocalTestLocation>();
    private Context mContext;


    public AdapterTestLocation(Context context) {
        mContext = context;
    }


    public void setData(List<PackLocalTestLocation> list) {
        mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_test_location, null);
        }

        PackLocalTestLocation pack = mList.get(position);
        TextView textView = (TextView) convertView.findViewById(R.id.text_view);
        textView.setText(pack.show_text);

        return convertView;
    }
}
