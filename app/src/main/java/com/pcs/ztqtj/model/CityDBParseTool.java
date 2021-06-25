package com.pcs.ztqtj.model;

import android.content.Context;
import android.text.TextUtils;

import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCity;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalStation;
import com.pcs.lib_ztqfj_v2.model.pack.net.citylist.AroundCityBean;

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
 * 数据解析工具类
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
                if (!cityJSON.isNull("ID")) {
                    city.ID = cityJSON.optString("ID");
                }
                if (!cityJSON.isNull("NAME")) {
                    city.NAME = cityJSON.optString("NAME");
                }
                if (!cityJSON.isNull("CODE")) {
                    city.CODE = cityJSON.optString("CODE");
                }
                if (!cityJSON.isNull("PARENTID")) {
                    city.PARENT_ID = cityJSON.optString("PARENTID");
                }
                if (!cityJSON.isNull("SHOW_NAME")) {
                    city.SHOW_NAME = cityJSON.optString("SHOW_NAME");
                }
                if (!cityJSON.isNull("PIN_YIN")) {
                    city.PINGYIN = cityJSON.optString("PIN_YIN");
                }
                if (!cityJSON.isNull("PY")) {
                    city.PY = cityJSON.optString("PY");
                }
                if (!cityJSON.isNull("PNAME")) {
                    city.PROVINCE = cityJSON.optString("PNAME");
                }
                city.isFjCity = city.PARENT_ID.equals("10103");
                cityList.add(city);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return cityList;
    }

    public static List<PackLocalCity> parseTJCity(List<PackLocalCity> cityList) {
        List<PackLocalCity> lv1List = new ArrayList<>();
        for(PackLocalCity city : cityList) {
            if(city.PARENT_ID.equals("10103")) {
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
                if (!cityJSON.isNull("ID")) {
                    station.ID = cityJSON.optString("ID");
                }
                if (!cityJSON.isNull("STATIONID")) {
                    station.STATIONID = cityJSON.optString("STATIONID");
                }
                if (!cityJSON.isNull("STATIONNAME")) {
                    station.STATIONNAME = cityJSON.optString("STATIONNAME");
                }
                if (!cityJSON.isNull("LONGITUDE")) {
                    station.LONGITUDE = cityJSON.optString("LONGITUDE");
                }
                if (!cityJSON.isNull("LATITUDE")) {
                    station.LATITUDE = cityJSON.optString("LATITUDE");
                }
                if (!cityJSON.isNull("IS_BASE")) {
                    station.IS_BASE = cityJSON.optString("IS_BASE");
                }
                if (!cityJSON.isNull("AREA")) {
                    station.AREA = cityJSON.optString("AREA");
                }
                cityList.add(station);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return cityList;
    }

    /**
     * 解析天津及周边城市
     * @return
     */
    public static List<AroundCityBean> parseAroundCity(JSONObject jsonObject) {
        if(jsonObject == null) return null;
        List<AroundCityBean> cityList = new ArrayList<>();
        try {
            if (!jsonObject.isNull("b")) {
                JSONObject obj = jsonObject.getJSONObject("b");
                if (!obj.isNull("around_area")) {
                    JSONObject around_area = obj.getJSONObject("around_area");
                    if (!around_area.isNull("info_list")) {
                        JSONArray info_list = around_area.getJSONArray("info_list");
                        for (int i = 0; i < info_list.length(); i++) {
                            JSONObject cityJSON = info_list.getJSONObject(i);
                            AroundCityBean city = new AroundCityBean();
                            if (!cityJSON.isNull("id")) {
                                city.id = cityJSON.optString("id");
                            }
                            if (!cityJSON.isNull("name")) {
                                city.name = cityJSON.optString("name");
                            }
                            if (!cityJSON.isNull("parent_id")) {
                                city.parent_id = cityJSON.optString("parent_id");
                            }
                            if (!cityJSON.isNull("parent_name")) {
                                city.parent_name = cityJSON.optString("parent_name");
                            }
                            cityList.add(city);
                        }
                    }
                }
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
