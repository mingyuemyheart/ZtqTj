package com.pcs.ztqtj.control.adapter.adapter_warn;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.SharedPreferencesUtil;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.PackWarningCenterTfggsjDown;

import java.util.ArrayList;
import java.util.List;

/**
 * 突发公共事件预警默认页面适配器
 * Created by tyaathome on 2016/6/14.
 */
public class AdapterWarningCommonDefault extends BaseAdapter{

    private Context context;
    private List<PackWarningCenterTfggsjDown.WarnTFGGSJ> datalist = new ArrayList<>();

    public AdapterWarningCommonDefault(Context context, List<PackWarningCenterTfggsjDown.WarnTFGGSJ> datalist) {
        this.context = context;
        this.datalist = datalist;
    }

    @Override
    public int getCount() {
        return datalist.size();
    }

    @Override
    public Object getItem(int position) {
        return datalist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.item_weather_warn_child,
                    null);
            holder.message = (TextView) convertView.findViewById(R.id.warn_title);
            holder.unitTime = (TextView) convertView.findViewById(R.id.warn_info);
            holder.tv_warn_info_flag= (TextView) convertView.findViewById(R.id.tv_warn_info_flag);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String url= SharedPreferencesUtil.getData(datalist.get(position).html_path,"");

        if (!TextUtils.isEmpty(url)){
            holder.tv_warn_info_flag.setText("");
        }else {
            holder.tv_warn_info_flag.setText("未读");
        }
        holder.message.setText(datalist.get(position).title);
        holder.unitTime.setText(datalist.get(position).fb_unit
                + datalist.get(position).fb_time);
        return convertView;
    }

    private class ViewHolder {
        public TextView message;
        public TextView unitTime,tv_warn_info_flag;
    }
}
