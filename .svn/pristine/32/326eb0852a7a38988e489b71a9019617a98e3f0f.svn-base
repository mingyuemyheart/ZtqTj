package com.pcs.ztqtj.control.adapter.air_quality;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.lib_ztqfj_v2.model.pack.net.airinfopack.PackAirCityInfoDown;

import java.util.ArrayList;
import java.util.List;


/**
 * JiangZy on 2017/1/22.
 * 城市站点详情
 */

public class AdapterAirCityStation extends BaseAdapter {
    //数据列表
    private List<PackAirCityInfoDown.ItemAirCityInfo> mList = new ArrayList<PackAirCityInfoDown.ItemAirCityInfo>();
    //AQI工具
    private ControlAirQualityTool mQualityTool = new ControlAirQualityTool();
    private Context mContext;

    public AdapterAirCityStation(Context context) {
        mContext = context;
    }

    /**
     * 设置数据包
     *
     * @param packDown
     */
    public void setPack(PackAirCityInfoDown packDown) {
        mList.clear();
        if (packDown != null) {
            mList.addAll(packDown.list);
        }
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
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_map_aqi_list, null);
        }

        //数据
        PackAirCityInfoDown.ItemAirCityInfo info = mList.get(position);
        //地区
        TextView textCity = (TextView) convertView.findViewById(R.id.text_area_name);
        textCity.setText(info.station_name);
        //数值
        TextView textNum = (TextView) convertView.findViewById(R.id.text_aqi_num);
        textNum.setText(info.aqi);
        int tempInt = 0;
        try {
            tempInt = Integer.parseInt(info.aqi);
        } catch (Exception ex) {
        }
        //中文
        TextView textCN = (TextView) convertView.findViewById(R.id.text_aqi_cn);
        textCN.setText(mQualityTool.getCnByAqi(tempInt));
        //颜色
        ImageView imageColor = (ImageView) convertView.findViewById(R.id.image_aqi_color);
        imageColor.setBackgroundResource(mQualityTool.getDrawableIdByAqi(tempInt));

        return convertView;
    }
}
