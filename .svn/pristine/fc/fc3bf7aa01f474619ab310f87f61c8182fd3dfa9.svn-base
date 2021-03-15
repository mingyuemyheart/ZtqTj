package com.pcs.ztqtj.control.adapter.livequery;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pcs.ztqtj.R;

import java.util.List;

public class AdapterDataButton extends BaseAdapter {
    protected List<String> dataList;
    private int itemCheck = -1;

    public void setCheckItem(int itemCheck) {
        this.itemCheck = itemCheck;
    }

    public AdapterDataButton(List<String> dataList) {
        this.dataList = dataList;
    }

    @Override
    public int getCount() {
        return dataList.size();
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
        Holder holder = null;
        if (view == null) {
            holder=new Holder();
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_livequery_pop_buttom, null);
            holder.tv = (TextView) view.findViewById(R.id.text_string);
            holder.img= (ImageView) view.findViewById(R.id.img_check);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }
        if (position == itemCheck && itemCheck != -1) {
//            holder.tv.setBackgroundResource(R.drawable.bg_livequerytitle);
            holder.img.setVisibility(View.VISIBLE);
        } else {
            holder.img.setVisibility(View.INVISIBLE);
//            holder.tv.setBackgroundColor(parent.getContext().getResources().getColor(R.color.alpha100));
        }
        holder.tv.setText(dataList.get(position));
        return view;
    }

    private class Holder{
        public TextView tv;
       public  ImageView img;
    }

}
