package com.pcs.ztqtj.control.adapter.data_query;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.lib_ztqfj_v2.model.pack.net.dataquery.ElementQueryMonth;

import java.util.ArrayList;
import java.util.List;

/**
 * 资料查询月份数据
 * Created by tyaathome on 2017/10/27.
 */

public class AdapterDataQueryMonth extends RecyclerView.Adapter<AdapterDataQueryMonth.ViewHolder> {

    private List<ElementQueryMonth> datalist = new ArrayList<>();

    public AdapterDataQueryMonth(List<ElementQueryMonth> datalist) {
        this.datalist = datalist;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_data_query_month, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ElementQueryMonth info = datalist.get(position);
        if(info != null) {
            holder.tvMonth.setText(info.month);
            holder.tvCount.setText(info.num);
        }
//        if(position == datalist.size()-1) {
//            holder.tvMonth.setBackgroundResource(R.drawable.border_without_bottom);
//            holder.tvCount.setBackgroundResource(R.drawable.border);
//        } else {
//            holder.tvMonth.setBackgroundResource(R.drawable.border_left_top);
//            holder.tvCount.setBackgroundResource(R.drawable.border_without_right);
//        }
    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvMonth, tvCount;

        public ViewHolder(View itemView) {
            super(itemView);
            tvMonth = (TextView) itemView.findViewById(R.id.tv_month);
            tvCount = (TextView) itemView.findViewById(R.id.tv_count);
        }
    }

}
