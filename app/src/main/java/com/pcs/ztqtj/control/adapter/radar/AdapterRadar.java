package com.pcs.ztqtj.control.adapter.radar;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pcs.ztqtj.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tyaathome on 2017/11/1.
 */

public class AdapterRadar extends RecyclerView.Adapter<AdapterRadar.ViewHolder> {

    private List<String> dataList = new ArrayList<>();

    public AdapterRadar(List<String> list) {
        dataList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_radar, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String value = dataList.get(position);
        holder.tvDesc.setText(value);
        if (value.equals("")){
            holder.view_tabel.setVisibility(View.GONE);
        }else {
            holder.view_tabel.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvDesc;
        public View view_tabel;

        public ViewHolder(View itemView) {
            super(itemView);
            tvDesc = (TextView) itemView.findViewById(R.id.tv_desc);
            view_tabel=itemView.findViewById(R.id.view_tabel);
        }
    }

}
