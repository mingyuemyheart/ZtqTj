package com.pcs.ztqtj.control.adapter;

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
import com.pcs.lib_ztqfj_v2.model.pack.tool.inter.InterfaceColumn;

import java.util.ArrayList;
import java.util.List;

/**
 * 灾情类别适配器
 * Created by tyaathome on 2016/11/11.
 */
public class AdapterDisasterCategory extends BaseAdapter {

    private Context context;
    private List<? extends InterfaceColumn> listdata = new ArrayList<>();
    // 当前点击位置
    private int currentClickedPosition = -1;
    private ImageFetcher imageFetcher;

    public AdapterDisasterCategory(Context context, List<? extends InterfaceColumn> listdata, ImageFetcher imageFetcher) {
        this.context = context;
        this.listdata = listdata;
        this.imageFetcher = imageFetcher;
    }

    @Override
    public int getCount() {
        return listdata.size();
    }

    @Override
    public Object getItem(int position) {
        return listdata.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_disaster_category, parent, false);
            holder = new ViewHolder();
            holder.ivPic = (ImageView) convertView.findViewById(R.id.iv);
            holder.tvName = (TextView) convertView.findViewById(R.id.name);
            holder.ivCheck = (ImageView) convertView.findViewById(R.id.check);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        InterfaceColumn bean = listdata.get(position);
        imageFetcher.loadImage(context.getResources().getString(R.string.file_download_url) + bean.getIconPath(),
                holder.ivPic, ImageConstant.ImageShowType.SRC);
        holder.tvName.setText(bean.getTitle());

        if(currentClickedPosition == position) {
            holder.ivCheck.setVisibility(View.VISIBLE);
        } else {
            holder.ivCheck.setVisibility(View.GONE);
        }

        return convertView;
    }

    /**
     * 设置当前点击位置
     * @param position
     */
    public void setCurrentClickedPosition(int position) {
        currentClickedPosition = position;
        notifyDataSetChanged();
    }

    private class ViewHolder {
        ImageView ivPic;
        TextView tvName;
        ImageView ivCheck;
    }

}
