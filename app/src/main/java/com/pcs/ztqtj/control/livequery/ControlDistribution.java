package com.pcs.ztqtj.control.livequery;

/**
 * Created by tyaathome on 2017/4/27.
 */

public class ControlDistribution {

    /**
     * 分布图类型枚举
     */
    public enum DistributionStatus {
        SB, // 雨量色斑图
        GJ, // 国家站
        DB, // 代表站
        ZD, // 雨量自动站
        RADAR, // 雷达
        CLOUD, // 云图
        NONE, // 空
    }

    /**
     * 栏目类型
     */
    public enum ColumnCategory {
        RAIN, // 降水
        TEMPERATURE, // 气温
        WIND, // 风速
        VISIBILITY, // 能见度
        PRESSURE, // 气压
        HUMIDITY, // 相对湿度
    }

    public static String getColumnName(ColumnCategory column) {
        switch (column) {
            case RAIN:
                return "rain";
            case TEMPERATURE:
                return "temp";
            case WIND:
                return "wind";
            case VISIBILITY:
                return "visibility";
            case PRESSURE:
                return "pressure";
            case HUMIDITY:
                return "humidity";
        }
        return "";
    }

    public static ColumnCategory getColumnType(String type) {
        switch (type) {
            case "rain":
                return ColumnCategory.RAIN;
            case "temp":
                return ColumnCategory.TEMPERATURE;
            case "wind":
                return ColumnCategory.WIND;
            case "visibility":
                return ColumnCategory.VISIBILITY;
            case "pressure":
                return ColumnCategory.PRESSURE;
            case "humidity":
                return ColumnCategory.HUMIDITY;
            default:
                return null;
        }
    }

}
