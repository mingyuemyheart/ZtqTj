package com.pcs.ztqtj.control.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pcs.lib_ztqfj_v2.model.pack.net.week.WeekWeatherInfo;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.inter.InterfaceShowBg;
import com.pcs.ztqtj.util.CommonUtil;

import java.util.List;

/**
 * 首页-逐日预报
 */
public class Adapter7DaysGridView extends BaseAdapter {

    private List<WeekWeatherInfo> weekList;
    private int mCurrentPosition = 1;
    private Context mcontext;
    private InterfaceShowBg mShowBg;

    public Adapter7DaysGridView(Context context, List<WeekWeatherInfo> weekList, InterfaceShowBg mShowBg) {
        this.weekList = weekList;
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
            holder.fragement_week_item_layout = (LinearLayout) convertView.findViewById(R.id.fragement_week_item_layout);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

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
            Bitmap drawableDay = CommonUtil.getImageFromAssetsFile(mcontext, strDay);
            Bitmap drawableNight = CommonUtil.getImageFromAssetsFile(mcontext, strNight);
            if (drawableDay != null) {
                holder.hightImg.setImageBitmap(drawableDay);
            }
            if (drawableNight != null) {
                holder.lowImg.setImageBitmap(drawableNight);
            }

            if (weekList.size() <= position || mCurrentPosition >= weekList.size()) {
            } else {
                // 背景
                if (position == mCurrentPosition) {
                    holder.fragement_week_item_layout.setBackgroundResource(R.drawable.bg_week_sel);
                } else {
                    holder.fragement_week_item_layout.setBackgroundColor(mcontext.getResources().getColor(R.color.alpha100));
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

        String weatherDesc = info.getWeatherContent("");
        TextView viewContentDesc = view.findViewById(R.id.text_content_desc);
        if (!TextUtils.isEmpty(weatherDesc)) {
            viewContentDesc.setText(weatherDesc);
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
