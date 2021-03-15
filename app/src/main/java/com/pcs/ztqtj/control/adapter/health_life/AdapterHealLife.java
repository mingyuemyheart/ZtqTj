package com.pcs.ztqtj.control.adapter.health_life;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.lib_ztqfj_v2.model.pack.net.healthyqx.PackHealthQxDown;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/10/18 0018.
 * chen_jx
 */

public class AdapterHealLife extends BaseAdapter {
    private Context mcontext;
    private ArrayList<PackHealthQxDown.HealthQx> str_data;

    public AdapterHealLife(Context context, ArrayList<PackHealthQxDown.HealthQx> list) {
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
            handler = new Handler();
            view = LayoutInflater.from(mcontext).inflate(R.layout.item_healthlife_list, null);
            handler.tv_time = (TextView) view.findViewById(R.id.tv_heallife_time);
            handler.tv_lev_01 = (TextView) view.findViewById(R.id.tv_lev_01);
            handler.tv_lev_02 = (TextView) view.findViewById(R.id.tv_lev_02);
            handler.tv_lev_03 = (TextView) view.findViewById(R.id.tv_lev_03);
            handler.tv_lev_04 = (TextView) view.findViewById(R.id.tv_lev_04);
            handler.tv_lev_05 = (TextView) view.findViewById(R.id.tv_lev_05);
            handler.tv_content = (TextView) view.findViewById(R.id.tv_heallife_content);
            handler.tv_suggest = (TextView) view.findViewById(R.id.tv_heallife_suggest);
            view.setTag(handler);
        } else {
            handler = (Handler) view.getTag();
        }

        handler.tv_time.setText(str_data.get(i).action_time);
        handler.tv_suggest.setText("防范建议：" + str_data.get(i).advise);
        handler.tv_content.setText("防范人群：" + str_data.get(i).guard);

        if (!TextUtils.isEmpty(str_data.get(i).health_level)) {
            String health_level = str_data.get(i).health_level;
            if (health_level.equals("高")) {
                handler.tv_lev_01.setBackgroundResource(R.drawable.corner_view_gray);
                handler.tv_lev_02.setBackgroundResource(R.drawable.corner_view_gray);
                handler.tv_lev_03.setBackgroundResource(R.drawable.corner_view_gray);
                handler.tv_lev_04.setBackgroundResource(R.drawable.corner_view_gray);
                handler.tv_lev_05.setBackgroundResource(R.drawable.corner_view_red);
            } else if (health_level.equals("较高")) {
                handler.tv_lev_02.setBackgroundResource(R.drawable.corner_view_gray);
                handler.tv_lev_01.setBackgroundResource(R.drawable.corner_view_gray);
                handler.tv_lev_03.setBackgroundResource(R.drawable.corner_view_gray);
                handler.tv_lev_04.setBackgroundResource(R.drawable.corner_view_orange);
                handler.tv_lev_05.setBackgroundResource(R.drawable.corner_view_gray);
            } else if (health_level.equals("中等")) {
                handler.tv_lev_03.setBackgroundResource(R.drawable.corner_view_yellow);
                handler.tv_lev_02.setBackgroundResource(R.drawable.corner_view_gray);
                handler.tv_lev_01.setBackgroundResource(R.drawable.corner_view_gray);
                handler.tv_lev_04.setBackgroundResource(R.drawable.corner_view_gray);
                handler.tv_lev_05.setBackgroundResource(R.drawable.corner_view_gray);
            } else if (health_level.equals("轻微")) {
                handler.tv_lev_04.setBackgroundResource(R.drawable.corner_view_gray);
                handler.tv_lev_03.setBackgroundResource(R.drawable.corner_view_gray);
                handler.tv_lev_02.setBackgroundResource(R.drawable.corner_view_blue);
                handler.tv_lev_01.setBackgroundResource(R.drawable.corner_view_gray);
                handler.tv_lev_05.setBackgroundResource(R.drawable.corner_view_gray);
            } else {
                handler.tv_lev_04.setBackgroundResource(R.drawable.corner_view_gray);
                handler.tv_lev_03.setBackgroundResource(R.drawable.corner_view_gray);
                handler.tv_lev_02.setBackgroundResource(R.drawable.corner_view_gray);
                handler.tv_lev_01.setBackgroundResource(R.drawable.corner_view_green);
                handler.tv_lev_05.setBackgroundResource(R.drawable.corner_view_gray);
            }
        } else {
            handler.tv_lev_04.setBackgroundResource(R.drawable.corner_view_gray);
            handler.tv_lev_03.setBackgroundResource(R.drawable.corner_view_gray);
            handler.tv_lev_02.setBackgroundResource(R.drawable.corner_view_gray);
            handler.tv_lev_01.setBackgroundResource(R.drawable.corner_view_gray);
            handler.tv_lev_05.setBackgroundResource(R.drawable.corner_view_gray);
        }
        return view;
    }

    private static class Handler {
        private TextView tv_time, tv_content, tv_suggest, tv_lev_01, tv_lev_02, tv_lev_03, tv_lev_04, tv_lev_05;
    }
}
