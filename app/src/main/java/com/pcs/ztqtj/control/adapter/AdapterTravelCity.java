package com.pcs.ztqtj.control.adapter;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.lib.lib_pcs_v3.model.image.ImageFetcher;
import com.pcs.lib_ztqfj_v2.model.pack.net.week.PackTravelWeekDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.week.WeekWeatherInfo;

import java.util.List;

public class AdapterTravelCity extends BaseAdapter {

    private Context context;
    private List<PackTravelWeekDown> data;
    private FamilyCityListDeleteBtnClick btnClickListener;
    private int isDefaultCity = 0;
    private ImageFetcher mImageFetcher;

    public AdapterTravelCity(Context context, List<PackTravelWeekDown> data,
                             int item_defaultcity,
                             FamilyCityListDeleteBtnClick btnClickListener,
                             ImageFetcher imageFetcher) {
        this.context = context;
        this.data = data;
        this.btnClickListener = btnClickListener;
        this.isDefaultCity = item_defaultcity;
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
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        Holder holder;
        if (view == null) {
            holder = new Holder();
            view = View.inflate(context, R.layout.familycity_item, null);
            holder.shi = (TextView) view.findViewById(R.id.shi);
            holder.tu = (ImageView) view.findViewById(R.id.tu);
            holder.tianqi = (TextView) view.findViewById(R.id.tianqi);
            holder.wendu = (TextView) view.findViewById(R.id.wendu);
            holder.delete = (ImageButton) view.findViewById(R.id.deletecity);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }

        WeekWeatherInfo pack = data.get(position).getToday();
        if (pack == null) {
            return view;
        }

        String lowt_hight = "";

        if (!TextUtils.isEmpty(pack.higt)) {
            lowt_hight = pack.higt + "°C";
        }
        if (!TextUtils.isEmpty(pack.lowt)) {
            lowt_hight += "~" + pack.lowt + "°C";
        }
        BitmapDrawable drawable = mImageFetcher.getImageCache()
                .getBitmapFromAssets(data.get(position).getIconPath(0));
        holder.tu.setImageDrawable(drawable);
        holder.wendu.setText(lowt_hight);
        holder.shi.setText(data.get(position).cityName);
        holder.delete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                btnClickListener.itemOnclick(position);
            }
        });

        holder.tianqi.setText(pack.weather);

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
