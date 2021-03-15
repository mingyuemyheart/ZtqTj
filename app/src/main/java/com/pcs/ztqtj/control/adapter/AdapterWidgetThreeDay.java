package com.pcs.ztqtj.control.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.lib.lib_pcs_v3.control.tool.Util;
import com.pcs.lib_ztqfj_v2.model.pack.net.week.WeekWeatherInfo;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * 小部件5x3三天天气适配器
 * Created by tyaathome on 2016/6/23.
 */
public class AdapterWidgetThreeDay extends BaseAdapter {

    private Context context;
    private List<WeekWeatherInfo> datalist;

    public AdapterWidgetThreeDay(Context context, List<WeekWeatherInfo> datalist) {
        this.context = context;
        this.datalist = datalist;
    }

    @Override
    public int getCount() {
        return datalist.size();
    }

    @Override
    public Object getItem(int position) {
        return datalist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_widget_three_day, null);
            holder = new ViewHolder();
            holder.tvDate = (TextView) convertView.findViewById(R.id.tv_date);
            holder.ivHighTemp = (ImageView) convertView.findViewById(R.id.iv_high_temp);
            holder.tvHighTemp = (TextView) convertView.findViewById(R.id.tv_high_temp);
            holder.tvLowTemp = (TextView) convertView.findViewById(R.id.tv_low_temp);
            holder.ivLowTemp = (ImageView) convertView.findViewById(R.id.iv_low_temp);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        WeekWeatherInfo info = datalist.get(position);

        // 日期
        if (position == 0) {
            holder.tvDate.setText(context.getResources().getString(R.string.today));
        } else {
            holder.tvDate.setText(info.week);
        }

        // 白天天气图标
        Bitmap bmHigh = getIcon(info.wd_day_ico);
        if (bmHigh != null) {
            holder.ivHighTemp.setImageBitmap(bmHigh);
        }

        // 最高温
        holder.tvHighTemp.setText(info.higt);

        // 最低温
        holder.tvLowTemp.setText(info.lowt);

        // 夜间天气
        Bitmap bmLow = getIcon(info.wd_night_ico);
        if (bmLow != null) {
            holder.ivLowTemp.setImageBitmap(bmLow);
        }
        return convertView;
    }

    private static class ViewHolder {
        TextView tvDate;
        ImageView ivHighTemp;
        TextView tvHighTemp;
        TextView tvLowTemp;
        ImageView ivLowTemp;
    }

    private Bitmap getIcon(String path) {
        InputStream iput;
        Bitmap bitmap = null;
        try {
            iput = context.getAssets().open(path);
            bitmap = BitmapFactory.decodeStream(iput);
            iput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (bitmap != null) {
            int width = Util.dip2px(context, 40);
            int height = Util.dip2px(context, 40);
            Matrix m = new Matrix();
            m.setRectToRect(
                    new RectF(0, 0, bitmap.getWidth(),
                            bitmap.getHeight()),
                    new RectF(0, 0, width, height),
                    Matrix.ScaleToFit.CENTER);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0,
                    bitmap.getWidth(),
                    bitmap.getHeight(), m, true);
        }
        return bitmap;
    }
}
