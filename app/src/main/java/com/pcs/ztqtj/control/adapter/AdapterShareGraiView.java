package com.pcs.ztqtj.control.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pcs.ztqtj.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Z
 *         引导页适配器
 */
public class AdapterShareGraiView extends BaseAdapter {
    public List<ItemInfo> imageSrc;

    public AdapterShareGraiView() {
        imageSrc = new ArrayList<>();

//        ItemInfo itemWeiXin = new ItemInfo();
//        itemWeiXin.img = R.drawable.icon_share_weixin;
//        itemWeiXin.content = "微信";
//        imageSrc.add(itemWeiXin);


        ItemInfo itemWeiXinF = new ItemInfo();
        itemWeiXinF.img = R.drawable.icon_share_friend;
        itemWeiXinF.content = "朋友圈";
        imageSrc.add(itemWeiXinF);


        ItemInfo itemSina = new ItemInfo();
        itemSina.img = R.drawable.icon_share_weibo;
        itemSina.content = "微博";
        imageSrc.add(itemSina);

        ItemInfo itemQQ = new ItemInfo();
        itemQQ.img = R.drawable.icon_share_qq;
        itemQQ.content = "QQ";
        imageSrc.add(itemQQ);

//        imageSrc.add(R.drawable.icon_share_weixin);
    }

    @Override
    public int getCount() {
        return imageSrc.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
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
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_share, null);
            holder.item_desc = (TextView) view.findViewById(R.id.item_desc);
            holder.item_img = (ImageView) view.findViewById(R.id.item_img);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }
        holder.item_img.setImageResource(imageSrc.get(position).img);
        holder.item_desc.setText(imageSrc.get(position).content);
        return view;
    }

    private class Holder {
        public ImageView item_img;
        public TextView item_desc;
    }

    private class ItemInfo {
        public String content;
        public int img;
    }

}