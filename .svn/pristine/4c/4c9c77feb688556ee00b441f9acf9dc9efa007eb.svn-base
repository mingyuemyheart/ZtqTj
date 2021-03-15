package com.pcs.ztqtj.control.adapter;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.inter.InterfaceShowBg;
import com.pcs.ztqtj.view.myview.TemperatureView;
import com.pcs.lib.lib_pcs_v3.model.image.ImageFetcher;
import com.pcs.lib_ztqfj_v2.model.pack.net.week.PackMainWeekWeatherDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.week.WeekWeatherInfo;

import java.util.List;

public class AdapterFamilyWeekGridView extends BaseAdapter {
    // 上下文
    private Context mContext;
    // 当前选中position
    private int mCurrentPosition = 1;
    private List<WeekWeatherInfo> listWeather;
    private ImageFetcher mImageFetcher;

    private InterfaceShowBg mShowBg;
    private String cityname;

    public void setClickPositon(int itemClick) {
        mCurrentPosition = itemClick;
        buttonChange();
        this.notifyDataSetChanged();
    }

    public AdapterFamilyWeekGridView(Context context, PackMainWeekWeatherDown packWeek, ImageFetcher imageFetcher, InterfaceShowBg mShowBg, String cityname) {
        mContext = context;
        if (packWeek != null) {
            listWeather = packWeek.getWeek();
        }
        mImageFetcher = imageFetcher;
        this.mShowBg = mShowBg;
        this.cityname = cityname;
    }

    private View view;

    public void setView(View view) {
        this.view = view;
        buttonChange();
    }

    private void buttonChange() {
        if (view == null) {
            return;
        }
        if (listWeather == null || mCurrentPosition >= listWeather.size()) {
            return;
        }
        WeekWeatherInfo info = listWeather.get(mCurrentPosition);
        // 天气标题
        TextView text_date = (TextView) view.findViewById(R.id.text_date);
        TextView viewContent = (TextView) view.findViewById(R.id.text_content);
        TemperatureView tempertureview = (TemperatureView) view.findViewById(R.id.tempertureview);
        // 天气内容
        GridView gridView = (GridView) view.findViewById(R.id.maingridview);
        if (TextUtils.isEmpty(info.gdt) || TextUtils.isEmpty(listWeather.get(mCurrentPosition).week)) {
        } else {
            text_date.setText(info.gdt + "  " + listWeather.get(mCurrentPosition).week);
        }
        viewContent.setText(info.getWeatherContent(cityname));
        String path = listWeather.get(mCurrentPosition).getWeatherBg();
        String pathThumb = listWeather.get(mCurrentPosition).getWeatherThumb();
        if (!TextUtils.isEmpty(path)) {
            mShowBg.showBg(path, pathThumb);
        }
    }

    @Override
    public int getCount() {
        return 7;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ItemView item = null;
        if (convertView == null) {
            item = new ItemView();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_week_weather, null);
            item.tv = (TextView) convertView.findViewById(R.id.week_tv);
            item.Img = (TextView) convertView.findViewById(R.id.week_bg);
            item.hightImg = (ImageView) convertView.findViewById(R.id.hight_temp);
            item.lowImg = (ImageView) convertView.findViewById(R.id.low_temp);
            item.fragement_week_item_layout = (LinearLayout) convertView.findViewById(R.id.fragement_week_item_layout);
            convertView.setTag(item);
        } else {
            item = (ItemView) convertView.getTag();
        }
        try {
            if (listWeather == null || listWeather.size() == 0) {
            } else {

                if (listWeather.size() <= position) {
                    item.hightImg.setImageDrawable(null);
                    item.lowImg.setImageDrawable(null);
                    item.tv.setText("");
                } else {
                    if (position == 0) {
                        item.tv.setText("昨天");
                    } else if (position == 1) {
                        item.tv.setText("今天");
                    } else {
                        WeekWeatherInfo info = listWeather.get(position);
                        item.tv.setText(info.week);
                    }
                    String strDay = "weather_icon/daytime/w" + listWeather.get(position).wd_day_ico + ".png";
                    String strNight = "weather_icon/night/n" + listWeather.get(position).wd_night_ico + ".png";
                    BitmapDrawable drawableDay = mImageFetcher.getImageCache().getBitmapFromAssets(strDay);
                    BitmapDrawable drawableNight = mImageFetcher.getImageCache().getBitmapFromAssets(strNight);
                    item.hightImg.setImageDrawable(drawableDay);
                    item.lowImg.setImageDrawable(drawableNight);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (listWeather.size() <= position || mCurrentPosition >= listWeather.size()) {
        } else {
            // 背景
            if (position == mCurrentPosition) {
                item.Img.setBackgroundResource(R.drawable.bg_week_sel);
            } else {
                item.Img.setBackgroundColor(mContext.getResources().getColor(R.color.alpha100));
            }
        }

        item.fragement_week_item_layout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mCurrentPosition = position;
                setClickPositon(position);
                buttonChange();
            }
        });

        return convertView;
    }

    private class ItemView {
        TextView tv;
        TextView Img;
        ImageView hightImg;
        ImageView lowImg;
        LinearLayout fragement_week_item_layout;
    }
}
