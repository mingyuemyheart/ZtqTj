package com.pcs.ztqtj.view.activity.livequery;

import android.support.annotation.DrawableRes;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.livequery.ControlDistribution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 图例区间图
 * Created by tyaathome on 2017/4/21.
 */

public class LegendInterval {

    private static LegendInterval instance;

    private LegendIntervalData rainInterval = new LegendIntervalData();
    private LegendIntervalData rainIntervalTotal = new LegendIntervalData();
    private LegendIntervalData tempInterval = new LegendIntervalData();
    private LegendIntervalData windInterval = new LegendIntervalData();
    private LegendIntervalData windDirectionInterval = new LegendIntervalData();
    private LegendIntervalData visibilityInterval = new LegendIntervalData();
    private LegendIntervalData pressureInterval = new LegendIntervalData();
    private LegendIntervalData humidityInterval = new LegendIntervalData();

    public LegendInterval() {
        init();
    }

    public static LegendInterval getInstance() {
        if (instance == null) {
            instance = new LegendInterval();
        }
        return instance;
    }

    private void init() {
        initRain();
        initTemp();
        initWind();
        initVisibility();
        initPressure();
        initHumidity();
    }

    private void initRain() {
        Float[] intervalList = {0.1f, 1f, 1.6f, 7f, 15f, 40f, 50f};
        Float[] intervalTotalList = {0.1f, 1f, 10f, 25f, 50f, 100f, 250f};
        Integer[] iconList = {R.drawable.icon_rain_value_1, R.drawable.icon_rain_value_2, R.drawable
                .icon_rain_value_3, R.drawable.icon_rain_value_4, R.drawable.icon_rain_value_5, R.drawable
                .icon_rain_value_6, R.drawable.icon_rain_value_7};
        rainInterval.intervalList = new ArrayList<>(Arrays.asList(intervalList));
        rainInterval.iconResIdList = new ArrayList<>(Arrays.asList(iconList));
        rainIntervalTotal.intervalList = new ArrayList<>(Arrays.asList(intervalTotalList));
        rainIntervalTotal.iconResIdList = new ArrayList<>(Arrays.asList(iconList));
    }

    private void initTemp() {
        Float[] intervalList = {-100f, -50f, -40f, -30f, -20f, -10f, -5f, 0f, 5f, 10f, 15f, 20f, 25f, 30f, 35f, 37f, 40f, 45f};
        Integer[] iconList = {R.drawable.icon_temp_value_10, R.drawable.icon_temp_value_11, R.drawable
                .icon_temp_value_12, R.drawable.icon_temp_value_13, R.drawable
                .icon_temp_value_14, R.drawable.icon_temp_value_15, R.drawable.icon_temp_value_16, R.drawable
                .icon_temp_value_17, R.drawable
                .icon_temp_value_18, R.drawable.icon_temp_value_1, R.drawable
                .icon_temp_value_2, R.drawable.icon_temp_value_3, R.drawable.icon_temp_value_4, R.drawable
                .icon_temp_value_5, R.drawable.icon_temp_value_6, R.drawable.icon_temp_value_7, R.drawable
                .icon_temp_value_8, R.drawable.icon_temp_value_9};
        tempInterval.intervalList = new ArrayList<>(Arrays.asList(intervalList));
        tempInterval.iconResIdList = new ArrayList<>(Arrays.asList(iconList));
    }

    private void initWind() {
        Float[] intervalList = {0.2f, 5.4f, 10.7f, 17.1f, 24.4f, 32.6f, 41.4f, 50.9f, 61.2f};
        Integer[] iconList = {R.drawable.icon_wind_value_1, R.drawable.icon_wind_value_2, R.drawable
                .icon_wind_value_3, R.drawable.icon_wind_value_4, R.drawable.icon_wind_value_5, R.drawable
                .icon_wind_value_6, R.drawable.icon_wind_value_7, R.drawable.icon_wind_value_8, R.drawable
                .icon_wind_value_9};
        Integer[] iconDirection = {R.drawable.icon_wind_direction_value_1, R.drawable.icon_wind_direction_value_2,
                R.drawable.icon_wind_direction_value_3, R.drawable.icon_wind_direction_value_4,
                R.drawable.icon_wind_direction_value_5, R.drawable.icon_wind_direction_value_6,
                R.drawable.icon_wind_direction_value_7, R.drawable.icon_wind_direction_value_8,
                R.drawable.icon_wind_direction_value_9};
        windInterval.intervalList = new ArrayList<>(Arrays.asList(intervalList));
        windInterval.iconResIdList = new ArrayList<>(Arrays.asList(iconList));
        windInterval.defaultIconResId = R.drawable.icon_wind_value_default;
        windDirectionInterval.intervalList = new ArrayList<>(Arrays.asList(intervalList));
        windDirectionInterval.iconResIdList = new ArrayList<>(Arrays.asList(iconDirection));
        windDirectionInterval.defaultIconResId = R.drawable.icon_wind_direction_value_defalut;
    }

    private void initVisibility() {
        Float[] intervalList = {0f, 50f, 200f, 500f, 1000f, 5000f};
        Integer[] iconList = {R.drawable.icon_visibility_value_1, R.drawable.icon_visibility_value_2, R.drawable
                .icon_visibility_value_3, R.drawable.icon_visibility_value_4, R.drawable.icon_visibility_value_5, R
                .drawable.icon_visibility_value_6};
        visibilityInterval.intervalList = new ArrayList<>(Arrays.asList(intervalList));
        visibilityInterval.iconResIdList = new ArrayList<>(Arrays.asList(iconList));
    }

    private void initPressure() {
        Float[] intervalList = {0f, 960f, 970f, 980f, 990f, 1000f, 1010f, 1020f};
        Integer[] iconList = {R.drawable.icon_pressure_value_1, R.drawable.icon_pressure_value_2, R.drawable
                .icon_pressure_value_3, R.drawable.icon_pressure_value_4, R.drawable.icon_pressure_value_5, R.drawable
                .icon_pressure_value_6, R.drawable.icon_pressure_value_7, R.drawable.icon_pressure_value_8};
        pressureInterval.intervalList = new ArrayList<>(Arrays.asList(intervalList));
        pressureInterval.iconResIdList = new ArrayList<>(Arrays.asList(iconList));
    }

    private void initHumidity() {
        Float[] intervalList = {0f, 10f, 20f, 30f, 40f, 50f, 60f, 70f, 80f, 90f};
        Integer[] iconList = {R.drawable.icon_humidity_value_1,R.drawable.icon_humidity_value_2,R.drawable.icon_humidity_value_3,
                R.drawable.icon_humidity_value_4,R.drawable.icon_humidity_value_5,R.drawable.icon_humidity_value_6,
                R.drawable.icon_humidity_value_7,R.drawable.icon_humidity_value_8,R.drawable.icon_humidity_value_9,
                R.drawable.icon_humidity_value_10};
        humidityInterval.intervalList = new ArrayList<>(Arrays.asList(intervalList));
        humidityInterval.iconResIdList = new ArrayList<>(Arrays.asList(iconList));
    }

    public
    @DrawableRes
    int getDrawableId(ControlDistribution.ColumnCategory column, float value) {
        int resid = 0;
        switch (column) {
            case RAIN:
                resid = getIdFromList(rainInterval, value);
                break;
            case TEMPERATURE:
                resid = getIdFromList(tempInterval, value);
                break;
            case WIND:
                resid = getIdFromList(windInterval, value);
                break;
            case VISIBILITY:
                resid = getIdFromList(visibilityInterval, value);
                break;
            case PRESSURE:
                resid = getIdFromList(pressureInterval, value);
                break;
            case HUMIDITY:
                resid = getIdFromList(humidityInterval, value);
                break;
        }
        return resid;
    }

    public
    @DrawableRes
    int getDrawableId(ControlDistribution.ColumnCategory column, float value, boolean isTotal) {
        int resid = 0;
        switch (column) {
            case RAIN:
                if(isTotal) {
                    resid = getIdFromList(rainIntervalTotal, value);
                } else {
                    resid = getIdFromList(rainInterval, value);
                }
                break;
            case TEMPERATURE:
                resid = getIdFromList(tempInterval, value);
                break;
            case WIND:
                resid = getIdFromList(windInterval, value);
                break;
            case VISIBILITY:
                resid = getIdFromList(visibilityInterval, value);
                break;
            case PRESSURE:
                resid = getIdFromList(pressureInterval, value);
                break;
            case HUMIDITY:
                resid = getIdFromList(humidityInterval, value);
                break;
        }
        return resid;
    }

    public
    @DrawableRes
    int getWindDrawableId(float value, boolean isWindDirection) {
        int resid;
                if(isWindDirection) {
                    resid = getIdFromList(windDirectionInterval, value);
                } else {
                    resid = getIdFromList(windInterval, value);
                }
        return resid;
    }

    /**
     * 获取字体颜色id
     * @param column
     * @param value
     * @return
     */
    public int getTextColorId(ControlDistribution.ColumnCategory column, float value) {
        if(column == ControlDistribution.ColumnCategory.RAIN && value < 1f) {
            return R.color.text_black;
        } else if (column == ControlDistribution.ColumnCategory.VISIBILITY && value > 5000f) {
            return R.color.text_gray;
        } else if (column == ControlDistribution.ColumnCategory.PRESSURE && value > 970 && value <= 980) {
            return R.color.text_gray;
        } else if (column == ControlDistribution.ColumnCategory.TEMPERATURE && value > -10 && value <= 15) {
            return R.color.text_gray;
        } else {
            return R.color.text_white;
        }
    }

    /**
     * @param data
     * @param value
     * @return
     */
    private
    @DrawableRes
    int getIdFromList(LegendIntervalData data, float value) {
        if (data.iconResIdList.size() == 0 || value == 0.0f) {
            return data.defaultIconResId;
        }
        // 小于第一个值取第一个图标
        if (value <= data.intervalList.get(0)) {
            return data.iconResIdList.get(0);
        }
        // 大于最后一个值取最后一个图标
        if (value >= data.intervalList.get(data.intervalList.size() - 1)) {
            return data.iconResIdList.get(data.iconResIdList.size() - 1);
        }
        for (int i = 0; i < data.intervalList.size() - 1; i++) {
            float firstValue = data.intervalList.get(i);
            float nextValue = data.intervalList.get(i + 1);
            if (value >= firstValue && value < nextValue) {
                return data.iconResIdList.get(i);
            }
        }
        return data.iconResIdList.get(0);
    }


    public class LegendIntervalData {
        public List<Float> intervalList = new ArrayList<>();
        public List<Integer> iconResIdList = new ArrayList<>();
        public int defaultIconResId = R.drawable.icon_blank_value;
    }
}
