package com.pcs.ztqtj.control.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pcs.lib_ztqfj_v2.model.pack.net.expert.PackExpertDetailTalkDown;
import com.pcs.ztqtj.R;

import java.util.List;

/**
 * Created by Z on 2016/11/10.
 * 专家解读
 */

public class AdapterExpertTalk extends BaseAdapter {

    private List<PackExpertDetailTalkDown.ItemTalk> listData;

    public AdapterExpertTalk(List<PackExpertDetailTalkDown.ItemTalk> listData) {
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
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expert_list_talk, null);
            holder.item_img = (ImageView) view.findViewById(R.id.item_img);
            holder.item_content = (TextView) view.findViewById(R.id.item_text_content);
            holder.item_text_name = (TextView) view.findViewById(R.id.item_text_name);
            holder.item_time = (TextView) view.findViewById(R.id.item_text_time);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }
        PackExpertDetailTalkDown.ItemTalk item = listData.get(position);

//       String imagePath = item.head_url;
//        if (!TextUtils.isEmpty(imagePath) || "null".equals(imagePath)) {
//            imageFetcher.loadImage(imagePath, holder.item_img, ImageConstant.ImageShowType.SRC);
//        }
        holder.item_text_name.setText(item.nick_name);
        holder.item_content.setText(item.content);
        holder.item_time.setText(item.pub_time);
        return view;
    }

    private class Holder {
        public ImageView item_img;
        public TextView item_content;
        public TextView item_text_name;
        public TextView item_time;
    }
}
