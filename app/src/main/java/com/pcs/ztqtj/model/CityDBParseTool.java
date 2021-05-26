package com.pcs.ztqtj.model;

import android.content.Context;
import android.text.TextUtils;

import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCity;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityUnit;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalStation;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalWarn;
import com.pcs.ztqtj.model.pack.TrafficHighWay;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tyaathome on 2019/04/03.
 */
public class CityDBParseTool {

    private static String getStringFromAssets(Context context, String fileName) {
        if(TextUtils.isEmpty(fileName)) return null;
        String path = "city_info/" + fileName;
        try {
            return getStringFromStream(context.getAssets().open(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 从流获取字符串
     * @param inputStream
     * @return
     */
    private static String getStringFromStream(InputStream inputStream) {
        if(inputStream == null) return null;
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            while((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
//            try {
//                inputStream.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            if(reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return stringBuilder.toString();
    }

    /**
     * 解析城市列表
     * @return
     */
    public static List<PackLocalCity> parseCityList(JSONObject jsonObject) {
        if(jsonObject == null) return null;
        List<PackLocalCity> cityList = new ArrayList<>();
        JSONArray rowJSON = jsonObject.optJSONArray("ROW");
        if(rowJSON == null) return null;
        try {
            for (int i = 0; i < rowJSON.length(); i++) {
                JSONObject cityJSON = rowJSON.getJSONObject(i);
                PackLocalCity city = new PackLocalCity();
                city.ID = cityJSON.optString("ID");
                city.NAME = cityJSON.optString("NAME");
                city.CODE = cityJSON.optString("CODE");
                city.PARENT_ID = cityJSON.optString("PARENTID");
                city.SHOW_NAME = cityJSON.optString("SHOW_NAME");
                city.PINGYIN = cityJSON.optString("PIN_YIN");
                city.PY = cityJSON.optString("PY");
                city.PROVINCE = cityJSON.optString("PNAME");
                city.isFjCity = city.PARENT_ID.equals("25149");
                cityList.add(city);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return cityList;
    }

    public static List<PackLocalCity> parseTJCity(List<PackLocalCity> cityList) {
        List<PackLocalCity> lv1List = new ArrayList<>();
        PackLocalCity tjCity = new PackLocalCity();
        tjCity.ID = "25183";
        tjCity.NAME = "天津";
        tjCity.CODE = "25183";
        tjCity.PARENT_ID = "25149";
        tjCity.SHOW_NAME = "天津-天津";
        tjCity.PINGYIN = "TianJin";
        tjCity.PY = "TJ";
        lv1List.add(tjCity);
        for(PackLocalCity city : cityList) {
            if(city.PARENT_ID.equals("25149")) {
                lv1List.add(city);
            }
        }
        return lv1List;
    }

    public static List<PackLocalStation> parseStationList(JSONObject jsonObject) {
        if(jsonObject == null) return null;
        List<PackLocalStation> cityList = new ArrayList<>();
        JSONArray rowJSON = jsonObject.optJSONArray("ROW");
        if(rowJSON == null) return null;
        try {
            for (int i = 0; i < rowJSON.length(); i++) {
                JSONObject cityJSON = rowJSON.getJSONObject(i);
                PackLocalStation station = new PackLocalStation();
                station.ID = cityJSON.optString("ID");
                station.STATIONID = cityJSON.optString("STATIONID");
                station.STATIONNAME = cityJSON.optString("STATIONNAME");
                station.LONGITUDE = cityJSON.optString("LONGITUDE");
                station.LATITUDE = cityJSON.optString("LATITUDE");
                station.IS_BASE = cityJSON.optString("IS_BASE");
                station.AREA = cityJSON.optString("AREA");
                cityList.add(station);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return cityList;
    }

    /**
     * 获取基本站列表
     * @param stationList
     * @return
     */
    public static List<PackLocalStation> parseBaseStationList(List<PackLocalStation> stationList) {
        if(stationList == null || stationList.size() == 0) return null;
        List<PackLocalStation> result = new ArrayList<>();
        for(PackLocalStation station : stationList) {
            if(station.IS_BASE.equals("1")) {
                result.add(station);
            }
        }
        return result;
    }

    /**
     * 获取单位列表
     * @return
     */
    public static List<PackLocalCityUnit> getUnitList(Context context) {
        String string = getStringFromAssets(context, "unit.json");
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(string);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(jsonObject == null) return null;
        List<PackLocalCityUnit> unitList = new ArrayList<>();
        JSONArray rowJSON = jsonObject.optJSONArray("ROW");
        if(rowJSON == null) return null;
        try {
            for (int i = 0; i < rowJSON.length(); i++) {
                JSONObject cityJSON = rowJSON.getJSONObject(i);
                PackLocalCityUnit unit = new PackLocalCityUnit();
                unit.CITY = cityJSON.optString("CITY");
                unit.UNIT = cityJSON.optString("UNIT");
                unitList.add(unit);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return unitList;
    }

    /**
     * 获取高速数据
     * @param context
     * @return
     */
    public static List<TrafficHighWay> getHighWayList(Context context) {
        String string = getStringFromAssets(context, "highway.json");
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(string);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(jsonObject == null) return null;
        List<TrafficHighWay> roadList = new ArrayList<>();
        JSONArray rowJSON = jsonObject.optJSONArray("ROW");
        if(rowJSON == null) return null;
        try {
            for (int i = 0; i < rowJSON.length(); i++) {
                JSONObject roadJSON = rowJSON.getJSONObject(i);
                TrafficHighWay road = new TrafficHighWay();
                road.ID = roadJSON.optString("ID");
                road.NAME = roadJSON.optString("NAME");
                road.SEARCH_NAME = roadJSON.optString("SEARCH_NAME");
                road.SHOW_LATITUDE = roadJSON.optDouble("SHOW_LATITUDE");
                road.SHOW_LONGITUDE = roadJSON.optDouble("SHOW_LONGITUDE");
                road.IMAGE_PATH = roadJSON.optString("IMAGE_PATH");
                road.DETAIL_IMAGE = roadJSON.optString("DETAIL_IMAGE");
                roadList.add(road);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return roadList;
    }

    /**
     * 获取预警信息列表
     * @param context
     * @return
     */
    public static List<PackLocalWarn> getWarnList(Context context) {
        String string = getStringFromAssets(context, "warn.json");
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(string);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(jsonObject == null) return null;
        List<PackLocalWarn> warnList = new ArrayList<>();
        JSONArray rowJSON = jsonObject.optJSONArray("ROW");
        if(rowJSON == null) return null;
        try {
            for (int i = 0; i < rowJSON.length(); i++) {
                JSONObject warnJSON = rowJSON.getJSONObject(i);
                PackLocalWarn warn = new PackLocalWarn();
                warn.TYPE = warnJSON.optString("TYPE");
                warn.LEVEL_STR = warnJSON.optString("LEVEL_STR");
                warn.ICO = warnJSON.optString("ICO");
                warnList.add(warn);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return warnList;
    }

    /**
     * 获取预警信息列表
     * @return
     */
    public static List<PackLocalCity> getProvinceList(JSONObject jsonObject) {
        if(jsonObject == null) return null;
        List<PackLocalCity> provinceList = new ArrayList<>();
        JSONArray rowJSON = jsonObject.optJSONArray("ROW");
        if(rowJSON == null) return null;
        try {
            for (int i = 0; i < rowJSON.length(); i++) {
                JSONObject provinceJSON = rowJSON.getJSONObject(i);
                PackLocalCity province = new PackLocalCity();
                province.ID = provinceJSON.optString("ID");
                province.NAME = provinceJSON.optString("NAME");
                province.PINGYIN = provinceJSON.optString("PIN_YIN");
                province.PY = provinceJSON.optString("PY");
                province.isFjCity = province.NAME.equals("天津");
                provinceList.add(province);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return provinceList;
    }
}
