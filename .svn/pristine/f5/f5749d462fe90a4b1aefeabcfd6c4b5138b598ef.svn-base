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
 * Created by tyaathome on 2018/1/15.
 */

public class AdapterDataQueryCompareList extends RecyclerView.Adapter<AdapterDataQueryCompareList.ViewHolder> {

    private List<DataQueryCompareInfo> dataList = new ArrayList<>();
    private boolean lastRowVisibility = true;

    public AdapterDataQueryCompareList(List<DataQueryCompareInfo> dataList) {
        this.dataList = dataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_data_query_compare_list, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DataQueryCompareInfo info = dataList.get(position);
        if(info != null) {
            holder.tvMonth.setText(info.month);
            holder.tvFirstYear.setText(info.firstYearValue);
            holder.tvSecondYear.setText(info.secondYearValue);
            if(lastRowVisibility) {
                holder.tvSecondYear.setVisibility(View.VISIBLE);
            } else {
                holder.tvSecondYear.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void setData(List<DataQueryCompareInfo> dataList, boolean lastRowVisibility) {
        this.dataList = dataList;
        this.lastRowVisibility = lastRowVisibility;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvMonth, tvFirstYear, tvSecondYear;

        public ViewHolder(View itemView) {
            super(itemView);
            tvMonth = (TextView) itemView.findViewById(R.id.tv_month);
            tvFirstYear = (TextView) itemView.findViewById(R.id.tv_first);
            tvSecondYear = (TextView) itemView.findViewById(R.id.tv_second);
        }
    }

}
