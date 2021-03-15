package com.pcs.ztqtj.control.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.model.LatLng;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.view.activity.product.ActivityMapForecast;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackMapForecastDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackMapForecastDown.MapForecast;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackMapForecastUp;

/**
 * 适配器：网格预报
 *
 * @author JiangZy
 */
public class AdapterMapForecast extends BaseAdapter {

    private ActivityMapForecast mContext;
    private PackMapForecastUp mPackUp = new PackMapForecastUp();
    private PackMapForecastDown mPackDown = new PackMapForecastDown();

    public AdapterMapForecast(ActivityMapForecast context) {
        mContext = context;
    }

    /**
     * 刷新数据
     *
     * @param latLng
     */
    public void refresh(int hour, LatLng latLng) {
        String latitude = String.format("%.1f", latLng.latitude);
        String longitude = String.format("%.1f", latLng.longitude);
        if (mPackUp.hour != null && mPackUp.hour.equals(String.valueOf(hour))
                && mPackUp.longitude.equals(latitude)
                && mPackUp.latitude.equals(latitude)) {
            // 完全相同
            return;
        }
        if (mPackUp.hour != null) {
            // 删除之前的下载
            PcsDataDownload.removeDownload(mPackUp);
        }
        mPackDown.list.clear();

        mPackUp.hour = String.valueOf(hour);
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
        return mPackDown.list.size();
    }

    @Override
    public Object getItem(int position) {
        return mPackDown.list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(
                    R.layout.item_map_forecast, null);
        }
        // 背景
        if (position % 2 == 0) {
            view.setBackgroundColor(mContext.getResources().getColor(
                    android.R.color.white));
        } else {
            view.setBackgroundColor(mContext.getResources().getColor(
                    R.color.map_forecast_list_bg));
        }
        MapForecast pack = mPackDown.list.get(position);
        TextView textView = null;

        // 天气
        textView = (TextView) view.findViewById(R.id.text_weather);
        textView.setText(pack.desc);
        // 降雨量
        textView = (TextView) view.findViewById(R.id.text_rainfall);
        textView.setText(pack.rainfall + "mm");
        // 气温
        textView = (TextView) view.findViewById(R.id.text_temperature);
        textView.setText(pack.temperature + "°C");
        // 能见度
        textView = (TextView) view.findViewById(R.id.text_visibility);
        textView.setText(pack.visibility + "m");
        // 时间
        textView = (TextView) view.findViewById(R.id.text_time);
        textView.setText(pack.time + "时");
        // 风向
        textView = (TextView) view.findViewById(R.id.text_winddir);
        textView.setText(pack.winddir);
        // 风速
        textView = (TextView) view.findViewById(R.id.text_windspeed);
        textView.setText(pack.windspeed + "m/s");

        return view;
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
                    mContext.setView(true);
                    //return ;
                } else {
                    mContext.setView(false);
                    AdapterMapForecast.this.notifyDataSetChanged();
                }
            }
        }
    };
}
