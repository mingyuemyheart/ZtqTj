package com.pcs.ztqtj.control.adapter.product_numerical;

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

import java.util.List;

/**
 * 监测预报-模式预报
 */
public class AdapterColumn extends BaseAdapter{

    private Context context;
    private List<? extends InterfaceColumn> dataList;
    private ImageFetcher imageFetcher;

    public AdapterColumn(Context context, List<? extends InterfaceColumn> dataList, ImageFetcher imageFetcher) {
        this.context = context;
        this.dataList = dataList;
        this.imageFetcher = imageFetcher;
        this.imageFetcher.setLoadingImage(R.drawable.alph100png);
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_column, null);
            holder.itemImage = convertView.findViewById(R.id.item_image);
            holder.itemText = convertView.findViewById(R.id.item_text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String path = context.getResources().getString(R.string.msyb) + dataList.get(position).getIconPath();
        imageFetcher.loadImage(path, holder.itemImage, ImageConstant.ImageShowType.SRC);
        holder.itemText.setText(dataList.get(position).getTitle());

        return convertView;
    }

    class ViewHolder {
        public TextView itemText;
        public ImageView itemImage;
    }
}
