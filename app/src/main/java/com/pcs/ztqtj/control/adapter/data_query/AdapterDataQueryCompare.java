package com.pcs.ztqtj.control.adapter.data_query;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pcs.ztqtj.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 资料查询月份数据
 * Created by tyaathome on 2017/10/27.
 */

public class AdapterDataQueryCompare extends RecyclerView.Adapter<AdapterDataQueryCompare.ViewHolder> {

    private List<String> dataList = new ArrayList<>();

    public AdapterDataQueryCompare(List<String> dataList) {
        this.dataList = dataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_data_query_compare, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(dataList.size() / 13 == 2) {
            holder.tvSecond.setVisibility(View.GONE);
            holder.tvMonth.setVisibility(View.VISIBLE);
            holder.tvFirst.setVisibility(View.VISIBLE);
        } else {
            holder.tvMonth.setVisibility(View.VISIBLE);
            holder.tvFirst.setVisibility(View.VISIBLE);
            holder.tvSecond.setVisibility(View.VISIBLE);
        }

        if(dataList.size() == 13) {
            holder.tvMonth.setText(dataList.get(position % 13));
        } else if(dataList.size() == 26) {
            holder.tvMonth.setText(dataList.get(position % 13));
            holder.tvFirst.setText(dataList.get(position % 13 + 13));
        } else if(dataList.size() == 39) {
            holder.tvMonth.setText(dataList.get(position % 13));
            holder.tvFirst.setText(dataList.get(position % 13 + 13));
            holder.tvSecond.setText(dataList.get(position % 13 + 26));
        }
    }

    @Override
    public int getItemCount() {
        if(dataList.size() == 0) {
            return 0;
        }
        return 13;
    }

    public void setData(List<String> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvMonth, tvFirst, tvSecond;

        public ViewHolder(View itemView) {
            super(itemView);
            tvMonth = (TextView) itemView.findViewById(R.id.tv_month);
            tvFirst = (TextView) itemView.findViewById(R.id.tv_first);
            tvSecond = (TextView) itemView.findViewById(R.id.tv_second);
        }
    }

}
