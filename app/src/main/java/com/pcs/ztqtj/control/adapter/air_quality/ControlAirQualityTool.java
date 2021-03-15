package com.pcs.ztqtj.control.adapter.air_quality;


import com.pcs.ztqtj.R;

/**
 * JiangZy on 2017/1/22.
 */

public class ControlAirQualityTool {
    /**
     * 根据AQI获取drawable的ID
     *
     * @param aqi
     * @return
     */
    public int getDrawableIdByAqi(int aqi) {
        if (aqi <= 50) {
            return R.drawable.color_green;
        } else if (aqi <= 100) {
            return R.drawable.color_yellow;
        } else if (aqi <= 150) {
            return R.drawable.color_orange;
        } else if (aqi <= 200) {
            return R.drawable.color_red;
        } else if (aqi <= 300) {
            return R.drawable.color_violet;
        } else {
            return R.drawable.color_brown_red;
        }
    }

    public String getCnByAqi(int aqi){
        if (aqi <= 50) {
            return "优";
        } else if (aqi <= 100) {
            return "良";
        } else if (aqi <= 150) {
            return "轻度污染";
        } else if (aqi <= 200) {
            return "中度污染";
        } else if (aqi <= 300) {
            return "重度污染";
        } else {
            return "严重污染";
        }
    }

}
