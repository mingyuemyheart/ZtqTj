package com.pcs.ztqtj.control.adapter;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pcs.lib.lib_pcs_v3.model.image.ImageFetcher;
import com.pcs.lib_ztqfj_v2.model.pack.net.week.WeekWeatherInfo;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.inter.InterfaceShowBg;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * 逐日预报
 */
public class Adapter7DaysGridView extends BaseAdapter {

    private List<WeekWeatherInfo> weekList;
    private ImageFetcher imageFetcher;
    private int mCurrentPosition = 1;
    private final SimpleDateFormat format = new SimpleDateFormat("M月d日", Locale.getDefault());
    private Context mcontext;
    private InterfaceShowBg mShowBg;

    public Adapter7DaysGridView(Context context, ImageFetcher imageFetcher, List<WeekWeatherInfo> weekList, InterfaceShowBg mShowBg) {
        this.weekList = weekList;
        this.imageFetcher = imageFetcher;
        this.mcontext = context;
        this.mShowBg = mShowBg;
    }

    @Override
    public int getCount() {
        return  weekList.size();
    }

    public void setClickPositon(int itemClick) {
        mCurrentPosition = itemClick;
        buttonChange();
        this.notifyDataSetChanged();
    }

    @Override
    public Object getItem(int position) {
        return weekList.get(position);
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
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_week_weather, null);
            holder.tv = (TextView) convertView.findViewById(R.id.week_tv);
            holder.tv_low_temp = (TextView) convertView.findViewById(R.id.tv_low_temp);
            holder.tv_hight_temp = (TextView) convertView.findViewById(R.id.tv_hight_temp);
            holder.week_date = (TextView) convertView.findViewById(R.id.week_date);
            holder.tv_speed = (TextView) convertView.findViewById(R.id.tv_speed);
            holder.Img = (TextView) convertView.findViewById(R.id.week_bg);
            holder.hightImg = (ImageView) convertView.findViewById(R.id.hight_temp);
            holder.lowImg = (ImageView) convertView.findViewById(R.id.low_temp);
            holder.fragement_week_item_layout =
                    (LinearLayout) convertView.findViewById(R.id.fragement_week_item_layout);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

//        if (position == 0) {
//            holder.tv_low_temp.setVisibility(View.VISIBLE);
//            holder.tv_hight_temp.setVisibility(View.VISIBLE);
//            holder.hightImg.setVisibility(View.GONE);
//            holder.lowImg.setVisibility(View.GONE);
//        } else {
//            holder.tv_low_temp.setVisibility(View.GONE);
//            holder.tv_hight_temp.setVisibility(View.GONE);
//            holder.hightImg.setVisibility(View.VISIBLE);
//            holder.lowImg.setVisibility(View.VISIBLE);
//        }

        WeekWeatherInfo info = (WeekWeatherInfo) getItem(position);
        if (info == null) {
            holder.hightImg.setImageDrawable(null);
            holder.lowImg.setImageDrawable(null);
            holder.tv.setText("");
            holder.tv_speed.setText("");
        } else {
            if (position == 0) {
                holder.tv.setText("今天");
            } else {
                holder.tv.setText(info.week);
            }
            holder.tv_speed.setText(info.getSpeed());

            try {
                String str = info.gdt;
                String[] strM = str.split("月");
                String day = strM[1].replace("日", "");
                if (day.equals("1")) {
                    holder.week_date.setText(strM[0] + "月");
                } else {
                    holder.week_date.setText(strM[1]);
                }
            } catch (Exception e) {
            }

            String strDay = "weather_icon/daytime/w" + info.wd_day_ico + ".png";
            String strNight = "weather_icon/night/n" + info.wd_night_ico + ".png";
            BitmapDrawable drawableDay = imageFetcher.getImageCache()
                    .getBitmapFromAssets(strDay);
            BitmapDrawable drawableNight = imageFetcher
                    .getImageCache().getBitmapFromAssets(strNight);
            holder.hightImg.setImageDrawable(drawableDay);
            holder.lowImg.setImageDrawable(drawableNight);

            if (weekList.size() <= position || mCurrentPosition >= weekList.size()) {
            } else {
                // 背景
                if (position == mCurrentPosition) {
                    holder.fragement_week_item_layout.setBackgroundResource(R.drawable.bg_week_sel);
                } else {
                    holder.fragement_week_item_layout.setBackgroundColor(mcontext.getResources().getColor(
                            R.color.alpha100));
                }
            }
            holder.fragement_week_item_layout.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    mCurrentPosition = position;
                    setClickPositon(position);
                    buttonChange();
                }
            });
        }

        return convertView;
    }

    public void setUpdate(List<WeekWeatherInfo> weekList) {
        this.weekList = weekList;
        notifyDataSetChanged();
    }

    private View view;

    public void setView(View view) {
        this.view = view;
    }

    private void buttonChange() {
        if (view == null) {
            return;
        }
        if (mCurrentPosition >= weekList.size()) {
            return;
        }
        WeekWeatherInfo info = weekList.get(mCurrentPosition);

//      String weatherDesc = info.getWeatherContent(packCity.NAME);
        String weatherDesc = info.getWeatherContent("");
        TextView viewContentDesc = (TextView) view.findViewById(R.id.text_content_desc);
        if (!TextUtils.isEmpty(weatherDesc)) {
//            String[] str=weatherDesc.split("；");
//            String strs="";
//            String strs_2="";
//            if (str.length==0){
            viewContentDesc.setText(weatherDesc);
//            }else {
//                for (int i = 0; i < str.length; i++) {
//                    if (strs == "") {
//                        strs = strs + ToDBC(str[i]);
//                    } else {
//                        strs = strs + ToDBC(" ； ") + ToDBC(str[i]);
//                    }
//                }
//                viewContentDesc.setText(strs);
//            }
        }
        String path = weekList.get(mCurrentPosition).getWeatherBg();
        String pathThumb = weekList.get(mCurrentPosition).getWeatherThumb();
        if (!TextUtils.isEmpty(path) && mShowBg != null) {
            mShowBg.showBg(path, pathThumb);
        }
    }

    private class ViewHolder {
        TextView tv_low_temp;
        TextView tv_hight_temp;
        TextView tv;
        TextView week_date;
        TextView tv_speed;
        TextView Img;

        ImageView hightImg;
        ImageView lowImg;
        LinearLayout fragement_week_item_layout;
    }
}
