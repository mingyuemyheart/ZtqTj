package com.pcs.ztqtj.control.adapter.im_weather;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackImWeatherDown;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/10/18 0018.
 * chen_jx
 */

public class AdapterImWeather extends BaseAdapter {
    private Context mcontext;
    private ArrayList<PackImWeatherDown.ImWeather> str_data;

    private boolean isVisibility = true;

    public AdapterImWeather(Context context, ArrayList<PackImWeatherDown.ImWeather> list) {
        this.mcontext = context;
        this.str_data = list;
    }

    @Override
    public int getCount() {
        return str_data.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Handler handler = null;
        if (view == null) {
            handler=new Handler();
            view = LayoutInflater.from(mcontext).inflate(R.layout.item_imweather, null);
            handler.tv_title = (TextView) view.findViewById(R.id.tv_imweather_title);
            handler.tv_date = (TextView) view.findViewById(R.id.tv_imweather_date);
            handler.tv_imweather_loading = (TextView) view.findViewById(R.id.tv_imweather_loading);
            view.setTag(handler);
        }else{
            handler= (Handler) view.getTag();
        }

        if (i==str_data.size()-1){
            if(isVisibility) {
                handler.tv_imweather_loading.setVisibility(View.GONE);
            } else {
                handler.tv_imweather_loading.setVisibility(View.GONE);
            }
        }else{
            handler.tv_imweather_loading.setVisibility(View.GONE);
        }

        handler.tv_title.setText(str_data.get(i).title);
        handler.tv_date.setText(str_data.get(i).date);

        return view;
    }

    public void setLoadingVisibility(boolean isVisibility) {
        this.isVisibility = isVisibility;
    }
    private static class Handler {
        private TextView tv_title, tv_date, tv_imweather_loading;
    }
}
