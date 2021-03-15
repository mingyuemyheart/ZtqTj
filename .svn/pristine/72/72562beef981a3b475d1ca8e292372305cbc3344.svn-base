package com.pcs.ztqtj.control.adapter.health_life;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.lib.lib_pcs_v3.model.image.ImageConstant;
import com.pcs.lib.lib_pcs_v3.model.image.ImageFetcher;
import com.pcs.lib_ztqfj_v2.model.pack.net.healthyqx.PackHealthQxLmDown;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/10/18 0018.
 * chen_jx
 */

public class AdapterHealLifeGrid extends BaseAdapter {
    private Context mcontext;

    private boolean isVisibility = true;
    private List<PackHealthQxLmDown.HealthType> list_Type = new ArrayList<>();
    private ImageFetcher mimageFetcher;

    public AdapterHealLifeGrid(Context context, List<PackHealthQxLmDown.HealthType> list_Type, ImageFetcher imageFetcher) {
        this.mcontext = context;
        this.list_Type = list_Type;
        this.mimageFetcher = imageFetcher;
    }

    private int clickTemp = 0;
//
//    public void setSeclection(int position) {
//        clickTemp = position;
//    }

    @Override
    public int getCount() {
        return list_Type.size();
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
            view = LayoutInflater.from(mcontext).inflate(R.layout.item_healthlife_grid, null);
            handler.tv_heallife_grid = (TextView) view.findViewById(R.id.tv_heallife_grid);
            handler.iv_health_label = (ImageView) view.findViewById(R.id.iv_health_label);
            handler.tv_health_time = (TextView) view.findViewById(R.id.tv_health_time);
            handler.tv_health_time_s = (TextView) view.findViewById(R.id.tv_health_time_s);
            handler.tv_grid_s = (TextView) view.findViewById(R.id.tv_grid_s);
            handler.tv_grid_m = (TextView) view.findViewById(R.id.tv_grid_m);
            handler.tv_grid_l = (TextView) view.findViewById(R.id.tv_grid_l);
            handler.tv_grid_xl = (TextView) view.findViewById(R.id.tv_grid_xl);
            handler.tv_grid_xxl = (TextView) view.findViewById(R.id.tv_grid_xxl);

            handler.tv_grid_s_s = (TextView) view.findViewById(R.id.tv_grid_s_s);
            handler.tv_grid_s_m = (TextView) view.findViewById(R.id.tv_grid_s_m);
            handler.tv_grid_s_l = (TextView) view.findViewById(R.id.tv_grid_s_l);
            handler.tv_grid_s_xl = (TextView) view.findViewById(R.id.tv_grid_s_xl);
            handler.tv_grid_s_xxl = (TextView) view.findViewById(R.id.tv_grid_s_xxl);


            view.setTag(handler);
        } else {
            handler = (Handler) view.getTag();
        }
        String path = mcontext.getString(R.string.file_download_url) +list_Type.get(i).img_url;
        mimageFetcher.loadImage(path, handler.iv_health_label, ImageConstant
                .ImageShowType.SRC);
        handler.tv_heallife_grid.setText(list_Type.get(i).name);

        int size = list_Type.get(i).healthQxTime.size();
        for (int j = 0; j < size; j++) {
            if (j==0){
                handler.tv_health_time.setText(list_Type.get(i).healthQxTime.get(j).action_time);
                if (list_Type.get(i).healthQxTime.get(j).health_level.equals("高")) {
                    handler.tv_grid_s.setBackgroundResource(R.drawable.corner_view_gray);
                    handler.tv_grid_m.setBackgroundResource(R.drawable.corner_view_gray);
                    handler.tv_grid_l.setBackgroundResource(R.drawable.corner_view_gray);
                    handler.tv_grid_xl.setBackgroundResource(R.drawable.corner_view_gray);
                    handler.tv_grid_xxl.setBackgroundResource(R.drawable.corner_view_red);
                } else if (list_Type.get(i).healthQxTime.get(j).health_level.equals("较高")) {
                    handler.tv_grid_s.setBackgroundResource(R.drawable.corner_view_gray);
                    handler.tv_grid_m.setBackgroundResource(R.drawable.corner_view_gray);
                    handler.tv_grid_l.setBackgroundResource(R.drawable.corner_view_gray);
                    handler.tv_grid_xl.setBackgroundResource(R.drawable.corner_view_orange);
                    handler.tv_grid_xxl.setBackgroundResource(R.drawable.corner_view_gray);
                } else if (list_Type.get(i).healthQxTime.get(j).health_level.equals("中等")) {
                    handler.tv_grid_s.setBackgroundResource(R.drawable.corner_view_gray);
                    handler.tv_grid_m.setBackgroundResource(R.drawable.corner_view_gray);
                    handler.tv_grid_l.setBackgroundResource(R.drawable.corner_view_yellow);
                    handler.tv_grid_xl.setBackgroundResource(R.drawable.corner_view_gray);
                    handler.tv_grid_xxl.setBackgroundResource(R.drawable.corner_view_gray);
                } else if (list_Type.get(i).healthQxTime.get(j).health_level.equals("轻微")) {
                    handler.tv_grid_s.setBackgroundResource(R.drawable.corner_view_gray);
                    handler.tv_grid_m.setBackgroundResource(R.drawable.corner_view_blue);
                    handler.tv_grid_l.setBackgroundResource(R.drawable.corner_view_gray);
                    handler.tv_grid_xl.setBackgroundResource(R.drawable.corner_view_gray);
                    handler.tv_grid_xxl.setBackgroundResource(R.drawable.corner_view_gray);
                } else {
                    handler.tv_grid_s.setBackgroundResource(R.drawable.corner_view_green);
                    handler.tv_grid_m.setBackgroundResource(R.drawable.corner_view_gray);
                    handler.tv_grid_l.setBackgroundResource(R.drawable.corner_view_gray);
                    handler.tv_grid_xl.setBackgroundResource(R.drawable.corner_view_gray);
                    handler.tv_grid_xxl.setBackgroundResource(R.drawable.corner_view_gray);
                }

            }else{
                handler.tv_health_time_s.setText(list_Type.get(i).healthQxTime.get(j).action_time);
                if (list_Type.get(i).healthQxTime.get(j).health_level.equals("高")) {
                    handler.tv_grid_s_s.setBackgroundResource(R.drawable.corner_view_gray);
                    handler.tv_grid_s_m.setBackgroundResource(R.drawable.corner_view_gray);
                    handler.tv_grid_s_l.setBackgroundResource(R.drawable.corner_view_gray);
                    handler.tv_grid_s_xl.setBackgroundResource(R.drawable.corner_view_gray);
                    handler.tv_grid_s_xxl.setBackgroundResource(R.drawable.corner_view_red);
                } else if (list_Type.get(i).healthQxTime.get(j).health_level.equals("较高")) {
                    handler.tv_grid_s_s.setBackgroundResource(R.drawable.corner_view_gray);
                    handler.tv_grid_s_m.setBackgroundResource(R.drawable.corner_view_gray);
                    handler.tv_grid_s_l.setBackgroundResource(R.drawable.corner_view_gray);
                    handler.tv_grid_s_xl.setBackgroundResource(R.drawable.corner_view_orange);
                    handler.tv_grid_s_xxl.setBackgroundResource(R.drawable.corner_view_gray);
                } else if (list_Type.get(i).healthQxTime.get(j).health_level.equals("中等")) {
                    handler.tv_grid_s_s.setBackgroundResource(R.drawable.corner_view_gray);
                    handler.tv_grid_s_m.setBackgroundResource(R.drawable.corner_view_gray);
                    handler.tv_grid_s_l.setBackgroundResource(R.drawable.corner_view_yellow);
                    handler.tv_grid_s_xl.setBackgroundResource(R.drawable.corner_view_gray);
                    handler.tv_grid_s_xxl.setBackgroundResource(R.drawable.corner_view_gray);
                } else if (list_Type.get(i).healthQxTime.get(j).health_level.equals("轻微")) {
                    handler.tv_grid_s_s.setBackgroundResource(R.drawable.corner_view_gray);
                    handler.tv_grid_s_m.setBackgroundResource(R.drawable.corner_view_blue);
                    handler.tv_grid_s_l.setBackgroundResource(R.drawable.corner_view_gray);
                    handler.tv_grid_s_xl.setBackgroundResource(R.drawable.corner_view_gray);
                    handler.tv_grid_s_xxl.setBackgroundResource(R.drawable.corner_view_gray);
                } else {
                    handler.tv_grid_s_s.setBackgroundResource(R.drawable.corner_view_green);
                    handler.tv_grid_s_m.setBackgroundResource(R.drawable.corner_view_gray);
                    handler.tv_grid_s_l.setBackgroundResource(R.drawable.corner_view_gray);
                    handler.tv_grid_s_xl.setBackgroundResource(R.drawable.corner_view_gray);
                    handler.tv_grid_s_xxl.setBackgroundResource(R.drawable.corner_view_gray);
                }
            }

        }


        return view;
    }

    private static class Handler {
        private TextView tv_heallife_grid, tv_health_time, tv_health_time_s;
        private ImageView iv_health_label;
        private TextView tv_grid_s, tv_grid_m, tv_grid_l, tv_grid_xl, tv_grid_xxl;
        private TextView tv_grid_s_s, tv_grid_s_m, tv_grid_s_l, tv_grid_s_xl, tv_grid_s_xxl;
    }

}
