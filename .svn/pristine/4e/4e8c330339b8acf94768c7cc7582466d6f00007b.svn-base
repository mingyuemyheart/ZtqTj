package com.pcs.ztqtj.control.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.model.LatLng;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackMapForecastDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackMapForecastDown.MapForecast;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackMapForecastUp;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.ZtqImageTool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 适配器：网格预报
 *
 * @author JiangZy
 */
public class AdapterMapForecastEn extends BaseAdapter {

    private Context mContext;
    private PackMapForecastUp mPackUp = new PackMapForecastUp();
    private PackMapForecastDown mPackDown = new PackMapForecastDown();
    private List<MapForecast> list = new ArrayList<MapForecast>();

    public AdapterMapForecastEn(Context context,List<MapForecast> mlist) {
        mContext = context;
        list=mlist;
    }

    /**
     * 刷新数据
     *
     * @param latLng
     */
    public void refresh(LatLng latLng) {
        String latitude = String.format("%.1f", latLng.latitude);
        String longitude = String.format("%.1f", latLng.longitude);
        mPackDown.list.clear();
        mPackUp.hour = "24";
        mPackUp.latitude = latitude;
        mPackUp.longitude = longitude;
        PcsDataDownload.addDownload(mPackUp);
        this.notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        Holder holder;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(
                    R.layout.item_mainhour_forecast_content_en, null);
            holder = new Holder();
            holder.iconWeather = (ImageView) view.findViewById(R.id.icon_weather);
            holder.textSw = (TextView) view.findViewById(R.id.text_sw);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }
        MapForecast pack = list.get(position);
        // 天气现象
        if (pack.ico != null && !"".equals(pack.ico)) {
            try {
                Bitmap bitmap = ZtqImageTool.getInstance().getWeatherIcon(
                        mContext, true, pack.ico);
                holder.iconWeather.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            holder.iconWeather.setVisibility(View.INVISIBLE);
        }
        holder.textSw.setText(pack.time);

        return view;
    }

    private class Holder {
        ImageView iconWeather;//天气图标
        TextView textSw;//风向
    }

    /**
     * 注册广播
     */
    public void registerReceiver() {
        PcsDataBrocastReceiver.registerReceiver(mContext, mReceiver);
    }

    /**
     * 注销广播
     */
    public void unregisterReceiver() {
        PcsDataBrocastReceiver.unregisterReceiver(mContext, mReceiver);
    }

    private PcsDataBrocastReceiver mReceiver = new PcsDataBrocastReceiver() {
        @Override
        public void onReceive(String nameStr, String errorStr) {

            if (!TextUtils.isEmpty(errorStr)) {
                Toast.makeText(mContext, errorStr, Toast.LENGTH_SHORT)
                        .show();
                return;
            }
            if (mPackUp.hour == null) {
                return;
            }
            if (nameStr.equals(mPackUp.getName())) {
                mPackDown = (PackMapForecastDown) PcsDataManager.getInstance().getNetPack(mPackUp.getName());
                if (mPackDown == null || mPackDown.list.size() == 0) {
                    // 提示无数据
//                    Toast.makeText(mContext, R.string.no_data, Toast.LENGTH_SHORT)
//                            .show();
//                    mContext.setView(true);
                    //return ;
                } else {
//                    mContext.setView(false);
                    AdapterMapForecastEn.this.notifyDataSetChanged();
                }
            }
        }
    };



}
