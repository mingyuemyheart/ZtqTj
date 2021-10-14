package com.pcs.ztqtj.control.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.view.activity.life.expert_interpretation.ActivityExpertList;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * 生活气象-专家解读
 */
public class AdapterExpertList extends BaseAdapter {

    private List<ActivityExpertList.MyItemExpert> listData;

    public AdapterExpertList(List<ActivityExpertList.MyItemExpert> listData) {
        this.listData = listData;
    }


    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        Holder holder;
        if (view == null) {
            holder = new Holder();
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expert_list, null);
            holder.item_img = (ImageView) view.findViewById(R.id.item_img);
            holder.item_content = (TextView) view.findViewById(R.id.item_text_content);
            holder.item_time = (TextView) view.findViewById(R.id.item_text_time);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }
        ActivityExpertList.MyItemExpert item = listData.get(position);
        holder.item_content.setText(item.title);
        holder.item_time.setText(item.release_time);
        String imagePath = item.small_img;
        if (!TextUtils.isEmpty(imagePath) || "null".equals(imagePath)) {
            Picasso.get().load(parent.getContext().getString(R.string.msyb) + imagePath).into(holder.item_img);
        }
        return view;
    }

    private class Holder {
        public ImageView item_img;
        public TextView item_content;
        public TextView item_time;
    }
}
