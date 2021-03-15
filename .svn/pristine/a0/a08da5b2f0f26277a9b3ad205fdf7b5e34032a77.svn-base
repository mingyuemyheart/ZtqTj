package com.pcs.ztqtj.control.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.inter.ClickImpl;
import com.pcs.lib_ztqfj_v2.model.pack.net.citylist.AroundCityBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tyaathome on 2018/3/21.
 */

public class AdapterHotCity extends RecyclerView.Adapter<AdapterHotCity.ViewHolder> {

    private List<AroundCityBean> cityList = new ArrayList<>();
    private List<Boolean> selectCityList = new ArrayList();
    private ClickImpl<AroundCityBean> listener;
    private Context mcontext;
    public AdapterHotCity(Context context,List<AroundCityBean> cityList, List<Boolean> selectCityList, ClickImpl<AroundCityBean>
            listener) {
        this.cityList = cityList;
        this.selectCityList = selectCityList;
        this.listener = listener;
        this.mcontext=context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hot_city, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final AroundCityBean bean = cityList.get(position);
        boolean isSelect = selectCityList.get(position);
        holder.tv.setText(bean.name);
        if (isSelect) {
            holder.tv.setBackgroundResource(R.drawable.bg_hot_city_sel);
            holder.tv.setTextColor(mcontext.getResources().getColor(R.color.text_white));
        } else {
            holder.tv.setBackgroundResource(R.drawable.bg_hot_city_nor);
            holder.tv.setTextColor(mcontext.getResources().getColor(R.color.text_black_common));
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(bean);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cityList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tv;

        public ViewHolder(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.tv);
        }
    }

}
