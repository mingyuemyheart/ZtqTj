package com.pcs.ztqtj.control.adapter;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pcs.lib.lib_pcs_v3.model.image.ImageFetcher;
import com.pcs.lib_ztqfj_v2.model.pack.net.week.WeekWeatherInfo;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.main_en_weather.CommandMainEnRow1;

import java.util.List;

/**
 * 一周天气ListView的适配器
 *
 * @author JiangZy
 */
public class AdapterWeekListView extends BaseAdapter {
    // 上下文
    private Context mContext;
    private List<WeekWeatherInfo> weekList;
    private ImageFetcher mImageFetcher;

    public AdapterWeekListView(Context context,
                               ImageFetcher imageFetcher, List<WeekWeatherInfo> list) {
        mContext = context;
        mImageFetcher = imageFetcher;
        weekList = list;
    }

    @Override
    public int getCount() {
        return weekList == null ? 0 : weekList.size();
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_weather_en, null);
            item.tv = (TextView) convertView.findViewById(R.id.tv_date_xq);
            item.tv_hight_temp = (TextView) convertView.findViewById(R.id.tv_date_wd);
            item.week_date = (TextView) convertView.findViewById(R.id.tv_date_rq);
            item.tv_dec = (TextView) convertView.findViewById(R.id.tv_date_dec);
            item.hightImg = (ImageView) convertView.findViewById(R.id.hight_temp);
            item.lowImg = (ImageView) convertView.findViewById(R.id.low_temp);
            convertView.setTag(item);
        } else {
            item = (ItemView) convertView.getTag();
        }


            if (weekList == null || weekList.size() == 0) {
            } else {
                if (weekList.size() <= position) {
                    item.hightImg.setImageDrawable(null);
                    item.lowImg.setImageDrawable(null);
                    item.tv.setText("");
                } else {
                    WeekWeatherInfo info = weekList.get(position);
                    item.tv.setText(info.us_week);
                    item.week_date.setText(info.us_gdt);
                    item.tv_dec.setText(info.us_weather);
                    if (CommandMainEnRow1.is_hs){
                        item.tv_hight_temp.setText(changeStr(info.higt)+"°F/"+info.lowt+"°F");
                    }else {
                        item.tv_hight_temp.setText(info.higt+"℃/"+info.lowt+"℃");
                    }
                    String strDay = "weather_icon/daytime/w" + info.wd_day_ico + ".png";
                    String strNight = "weather_icon/night/n" + info.wd_night_ico + ".png";
                    BitmapDrawable drawableDay = mImageFetcher.getImageCache()
                            .getBitmapFromAssets(strDay);
                    BitmapDrawable drawableNight = mImageFetcher
                            .getImageCache().getBitmapFromAssets(strNight);
                    item.hightImg.setImageDrawable(drawableDay);
                    item.lowImg.setImageDrawable(drawableNight);
                }
            }
        return convertView;
    }

    private class ItemView {
        TextView tv_hight_temp;
        TextView tv;
        TextView week_date;
        TextView tv_dec;

        ImageView hightImg;
        ImageView lowImg;
    }

    private String changeStr(String value) {
        String temHs_little = "";
        String tempHs = String.valueOf(Float.parseFloat(value) * 1.8 + 32);
        if (!TextUtils.isEmpty(tempHs) && tempHs.indexOf(".") > 1) {
            tempHs = tempHs.substring(0, tempHs.indexOf(".") + 1).replace(".", "");
        }
        if (!TextUtils.isEmpty(String.valueOf(Float.parseFloat(value) * 1.8 + 32)) && String.valueOf(Float
                .parseFloat(value) * 1.8 + 32).indexOf(".") > 1) {
            temHs_little = value.substring(value.indexOf(".") + 1, value.length());
        }
        if (!temHs_little.equals("0")) {
            tempHs = tempHs + "." + temHs_little.substring(0, 1);
        }
        return tempHs;
    }


}
