package com.pcs.ztqtj.control.adapter.hour_forecast;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityMain;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackHourForecastDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackHourForecastDown.HourForecast;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackHourForecastUp;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.ZtqImageTool;
import com.pcs.ztqtj.model.ZtqCityDB;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 适配器：逐小时预报
 *
 * @author JiangZy
 */
@SuppressLint({"RtlHardcoded"})
public class AdapterMainHourForecastEn extends BaseAdapter {
    private Context mContext;
    /**
     * 数据列表
     */
    private List<HourForecast> mList = new ArrayList<HourForecast>();

    public AdapterMainHourForecastEn(Context context) {
        mContext = context;
        PackLocalCityMain packCity = ZtqCityDB.getInstance().getCityMain();
        if (packCity == null) {
            return;
        }
        PackHourForecastUp packUp = new PackHourForecastUp();
        packUp.county_id = packCity.ID;
        PackHourForecastDown down = (PackHourForecastDown) PcsDataManager.getInstance().getNetPack(packUp.getName());
        mList.clear();
        if (down != null) {
            mList.addAll(down.list);
        }
    }

    @Override
    public void notifyDataSetChanged() {
        PackLocalCityMain packCity = ZtqCityDB.getInstance().getCityMain();
        PackHourForecastUp packUp = new PackHourForecastUp();
        packUp.county_id = packCity.ID;
        PackHourForecastDown downData = (PackHourForecastDown) PcsDataManager.getInstance().getNetPack(packUp.getName());
        mList.clear();
        if (downData != null) {
            mList.addAll(downData.list);
        }
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {

        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_mainhour_forecast_content_en, null);
            holder = new Holder();
            holder.iconWeather = (ImageView) convertView.findViewById(R.id.icon_weather);
            holder.textSw = (TextView) convertView.findViewById(R.id.text_sw);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
            HourForecast pack = mList.get(position);
            // 天气现象
            if (pack.ico != null && !"".equals(pack.ico)) {
                try {
                    Bitmap bitmap = ZtqImageTool.getInstance().getWeatherIcon(
                            mContext, pack.isDayTime, pack.ico);
                    holder.iconWeather.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                holder.iconWeather.setVisibility(View.INVISIBLE);
            }
            holder.textSw.setText(pack.time);

        return convertView;
    }

    private class Holder {
        ImageView iconWeather;//天气图标
        TextView textSw;//风向
    }

}
