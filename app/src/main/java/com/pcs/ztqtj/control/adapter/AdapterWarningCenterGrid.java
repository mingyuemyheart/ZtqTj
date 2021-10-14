package com.pcs.ztqtj.control.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.pcs.lib_ztqfj_v2.model.pack.net.warn.WarnCenterYJXXGridBean;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.util.CommonUtil;

import java.util.List;

/**
 * 首页-指点天气-预警
 */
public class AdapterWarningCenterGrid extends BaseAdapter {

    private Context context;
    private List<WarnCenterYJXXGridBean> dataList;

    public AdapterWarningCenterGrid(Context context, List<WarnCenterYJXXGridBean> dataList) {
        this.context = context;
        this.dataList = dataList;
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
        ViewHolder holder;
        if(convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_main_warning_center_grid, null);
            holder.iv = convertView.findViewById(R.id.iv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        WarnCenterYJXXGridBean info = dataList.get(position);
        String path = "img_warn/" + info.ico + ".png";
        Bitmap bitmap = CommonUtil.getImageFromAssetsFile(context, path);
        if (bitmap != null) {
            holder.iv.setImageBitmap(bitmap);
        }
        return convertView;
    }

    private static class ViewHolder {
        ImageView iv;
    }
}
