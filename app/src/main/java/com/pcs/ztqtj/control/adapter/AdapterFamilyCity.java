package com.pcs.ztqtj.control.adapter;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.lib.lib_pcs_v3.model.image.ImageFetcher;
import com.pcs.lib_ztqfj_v2.model.pack.net.week.PackWeekWeatherFamilyDown;

import java.util.List;

public class AdapterFamilyCity extends BaseAdapter {

    private List<PackWeekWeatherFamilyDown> data;
    private FamilyCityListDeleteBtnClick btnClickListener;
    private ImageFetcher mImageFetcher;

    public AdapterFamilyCity(Context context,
                             List<PackWeekWeatherFamilyDown> data,
                             FamilyCityListDeleteBtnClick btnClickListener,
                             ImageFetcher imageFetcher) {
        this.data = data;
        this.btnClickListener = btnClickListener;
        mImageFetcher = imageFetcher;
    }

    @Override
    public int getCount() {
        return data.size();
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
    public View getView(final int position, View view, ViewGroup parent) {
        Holder holder;
        if (view == null) {
            holder = new Holder();
            view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.familycity_item, null);
            holder.shi = (TextView) view.findViewById(R.id.shi);
            holder.tu = (ImageView) view.findViewById(R.id.tu);
            holder.tianqi = (TextView) view.findViewById(R.id.tianqi);
            holder.wendu = (TextView) view.findViewById(R.id.wendu);
            holder.delete = (ImageButton) view.findViewById(R.id.deletecity);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }
        if (position == 0) {
            holder.delete.setVisibility(View.GONE);
        } else {
            holder.delete.setVisibility(View.VISIBLE);
        }
        try {
            if (data.get(position) == null || data.get(position).getToday() == null) {
            } else {
                BitmapDrawable drawable = mImageFetcher.getImageCache()
                        .getBitmapFromAssets(
                                data.get(position).getIconPath(1));
                holder.tu.setImageDrawable(drawable);

                String l_htemp = "";
                l_htemp = data.get(position).getToday().higt + "°C~"
                        + data.get(position).getToday().lowt + "°C";
                holder.wendu.setText(l_htemp);
                holder.tianqi.setText(data.get(position).getToday().weather);
            }
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btnClickListener.itemOnclick(position);
                }
            });
            holder.shi.setText(data.get(position).cityName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    private class Holder {
        public TextView shi;
        public ImageView tu;
        public TextView tianqi;
        public TextView wendu;
        public ImageButton delete;
    }

    public interface FamilyCityListDeleteBtnClick {
        void itemOnclick(int item);
    }
}
