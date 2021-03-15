package com.pcs.ztqtj.control.adapter.adapter_warn;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.SharedPreferencesUtil;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.PackWarningCenterTfggsjDown;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AdatperWeaRiskWarn extends BaseAdapter {
    private Context context;
    private List<PackWarningCenterTfggsjDown.WarnTFGGSJ> datalist;

    public AdatperWeaRiskWarn(Context context, List<PackWarningCenterTfggsjDown.WarnTFGGSJ> datalist) {
        this.context = context;
        this.datalist = datalist;
    }

    @Override
    public int getCount() {
        return datalist.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressWarnings("static-access")
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        Holder holder = null;
        if (view == null) {
            holder = new Holder();
            view = parent.inflate(context, R.layout.item_warn_wearisk, null);
            holder.message = (TextView) view.findViewById(R.id.showmessage);
            holder.unitTime = (TextView) view.findViewById(R.id.unit_time);
            holder.warnIcon = (ImageView) view.findViewById(R.id.warn_icon);
            holder.tv_unit_flag = (TextView) view.findViewById(R.id.tv_unit_flag);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }
        String date2 =  datalist.get(position).put_time + ":00";
        date2 = date2.replace("年", "-");
        date2 = date2.replace("月", "-");
        date2 = date2.replace("日", " ");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int days = 0;
        try {
            Date date3 = format.parse(date2);
            String date = format.format(new Date());
            Date date4 = format.parse(date);
            days = differentDaysByMillisecond(date3, date4);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String flag_url = SharedPreferencesUtil.getData(datalist.get(position).html_path, "");
        if (days<1) {
            if (!TextUtils.isEmpty(flag_url)) {
                holder.tv_unit_flag.setText("");
            } else {
                holder.tv_unit_flag.setText("未读");
            }
        }else {
            holder.tv_unit_flag.setText("");
        }
        holder.message.setText(datalist.get(position).title);
        holder.unitTime.setText(datalist.get(position).fb_unit + datalist.get(position).fb_time);
        return view;
    }

    private class Holder {
        public TextView message;
        public TextView unitTime, tv_unit_flag;
        public ImageView warnIcon;
    }

    /**
     * 通过时间秒毫秒数判断两个时间的间隔
     *
     * @param date1
     * @param date2
     * @return
     */
    public static int differentDaysByMillisecond(Date date1, Date date2) {
        int days = (int) ((date2.getTime() - date1.getTime()) / (1000 * 3600 * 24));
         return Math.abs(days);
    }
}