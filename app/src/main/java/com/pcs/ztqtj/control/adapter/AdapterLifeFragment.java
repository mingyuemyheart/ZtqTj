package com.pcs.ztqtj.control.adapter;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.utils.TextUtil;
import com.pcs.ztqtj.util.ColumnDto;
import com.pcs.ztqtj.util.CommonUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * 生活气象
 */
public class AdapterLifeFragment extends BaseAdapter {

    private Context context;
    private ArrayList<ColumnDto> dataList;
    private int itemHeight = 0;

    public AdapterLifeFragment(Context context, ArrayList<ColumnDto> dataList, int itemHeight) {
        this.context = context;
        this.dataList = dataList;
        this.itemHeight = itemHeight;
    }

    @Override
    public int getCount() {
        return  dataList.size();
    }

    private class ViewHolder {
        public TextView itemText;
        public ImageView itemImage;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_life_fragment, null);
            holder.itemText = convertView.findViewById(R.id.item_text);
            holder.itemImage = convertView.findViewById(R.id.item_image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ColumnDto data = dataList.get(position);

        if (data.dataName != null) {
            holder.itemText.setText(data.dataName);
        }

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.height = itemHeight-(int) CommonUtil.dip2px(context, 20);
        holder.itemImage.setLayoutParams(params);
        if (!TextUtil.isEmpty(data.icon)) {
            String imgUrl = context.getString(R.string.msyb)+data.icon;
            Picasso.get().load(imgUrl).error(R.drawable.no_pic).into(holder.itemImage);
        } else {
            holder.itemImage.setImageResource(R.drawable.no_pic);
        }

        return convertView;
    }

}
