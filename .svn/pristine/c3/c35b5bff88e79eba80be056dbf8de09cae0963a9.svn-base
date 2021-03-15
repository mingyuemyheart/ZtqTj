package com.pcs.ztqtj.control.adapter;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.pcs.ztqtj.R;
import com.pcs.lib.lib_pcs_v3.model.image.ImageFetcher;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.WarnCenterYJXXGridBean;

import java.util.List;

/**
 * 首页预警格点信息适配器
 * Created by tyaathome on 2016/6/7.
 */
public class AdapterWarningCenterGrid extends BaseAdapter {

    private Context context;
    private List<WarnCenterYJXXGridBean> dataList;
    private ImageFetcher imageFetcher;

    public AdapterWarningCenterGrid(Context context, List<WarnCenterYJXXGridBean> dataList, ImageFetcher imageFetcher) {
        this.context = context;
        this.dataList = dataList;
        this.imageFetcher = imageFetcher;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_main_warning_center_grid, null);
            holder.iv = (ImageView) convertView.findViewById(R.id.iv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        WarnCenterYJXXGridBean info = dataList.get(position);
        if(imageFetcher != null) {
            String path = "img_warn/" + info.ico + ".png";
            BitmapDrawable bitmapDrawable = imageFetcher.getImageCache().getBitmapFromAssets(path);
            holder.iv.setImageDrawable(bitmapDrawable);
        }
        return convertView;
    }

    private static class ViewHolder {
        ImageView iv;
    }
}
