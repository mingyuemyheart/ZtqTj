package com.pcs.ztqtj.control.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.lib.lib_pcs_v3.model.image.ImageConstant;
import com.pcs.lib.lib_pcs_v3.model.image.ImageFetcher;
import com.pcs.lib_ztqfj_v2.model.pack.net.expert.ItemExpert;
import com.pcs.ztqtj.view.activity.life.expert_interpretation.ActivityExpertList;

import java.util.List;

/**
 * 生活气象-专家解读
 */
public class AdapterExpertList extends BaseAdapter {

    private List<ActivityExpertList.MyItemExpert> listData;
    private ImageFetcher imageFetcher;

    public AdapterExpertList(List<ActivityExpertList.MyItemExpert> listData, ImageFetcher imageFetcher) {
        this.listData = listData;
        this.imageFetcher = imageFetcher;
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
            imageFetcher.loadImage(parent.getContext().getString(R.string.msyb) + imagePath, holder.item_img, ImageConstant.ImageShowType.SRC);
        }
        return view;
    }

    private class Holder {
        public ImageView item_img;
        public TextView item_content;
        public TextView item_time;
    }
}
