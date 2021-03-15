package com.pcs.ztqtj.control.adapter.typhoon;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.view.activity.livequery.TyphoonInfoCheck;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tyaathome on 2017/5/16.
 */

public class AdapterDistributionTyphoonList extends BaseAdapter {

    private Context mContext;
    private List<TyphoonInfoCheck> listdata = new ArrayList<>();
    private OnTyphoonItemClickListener mListener;

    public AdapterDistributionTyphoonList(Context context, List<TyphoonInfoCheck> listdata) {
        this.mContext = context;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if(convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_distribution_typhoon_list, parent, false);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.cb);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final TyphoonInfoCheck bean = listdata.get(position);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null) {
                    mListener.onItemClickListener(position, bean.isChecked);
                }
            }
        });

        holder.checkBox.setText(bean.typhoonInfo.name);
        holder.checkBox.setChecked(bean.isChecked);

        return convertView;
    }

    public void setListener(OnTyphoonItemClickListener listener) {
        mListener = listener;
    }

    private static class ViewHolder {
        public CheckBox checkBox;
    }

    public interface OnTyphoonItemClickListener {
        void onItemClickListener(int position, boolean isChecked);
    }
}
