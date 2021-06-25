package com.pcs.ztqtj.view.fragment.warning;

import com.pcs.lib.lib_pcs_v3.model.pack.PcsPackDown;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCity;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.WarnCenterYJXXGridBean;
import com.pcs.lib_ztqfj_v2.model.pack.tool.GetJSONObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MyPackWarnWeatherDown extends PcsPackDown {
    public List<WarnCenterYJXXGridBean> county;
    public List<WarnCenterYJXXGridBean> province;
    public List<WarnCenterYJXXGridBean> city;
    public List<PackLocalCity> cityidex;
    public String countyname;
    public String provincesName;
    public String cityname;

    public MyPackWarnWeatherDown() {
    }

    public void fillData(String jsonStr) {
        this.county = new ArrayList();
        this.province = new ArrayList();
        this.city = new ArrayList();
        this.cityidex = new ArrayList();

        try {
            JSONObject obj = new JSONObject(jsonStr);
            this.updateMill = obj.optLong("updateMill");
            GetJSONObject getJson = new GetJSONObject(obj);
            this.countyname = (String)getJson.getJSONObjectValue("countyname");
            this.provincesName = (String)getJson.getJSONObjectValue("provincesName");
            this.cityname = (String)getJson.getJSONObjectValue("cityname");

            if (!obj.isNull("city")) {
                JSONArray warn_city = obj.optJSONArray("city");
                for(int i = 0; i < warn_city.length(); ++i) {
                    WarnCenterYJXXGridBean warn = new WarnCenterYJXXGridBean();
                    warn.id = warn_city.getJSONObject(i).optString("id");
                    warn.level = warn_city.getJSONObject(i).optString("level");
                    warn.ico = warn_city.getJSONObject(i).optString("ico");
                    warn.put_str = warn_city.getJSONObject(i).optString("put_str");
                    warn.put_time = warn_city.getJSONObject(i).optString("yj_time");
                    this.city.add(warn);
                }
            }

            if (!obj.isNull("province")) {
                JSONArray warn_province = obj.optJSONArray("province");
                for(int i = 0; i < warn_province.length(); ++i) {
                    WarnCenterYJXXGridBean warn = new WarnCenterYJXXGridBean();
                    warn.id = warn_province.getJSONObject(i).optString("id");
                    warn.level = warn_province.getJSONObject(i).optString("level");
                    warn.ico = warn_province.getJSONObject(i).optString("ico");
                    warn.put_str = warn_province.getJSONObject(i).optString("put_str");
                    warn.put_time = warn_province.getJSONObject(i).optString("yj_time");
                    this.province.add(warn);
                }
            }

            if (!obj.isNull("county")) {
                JSONArray warn_county = obj.optJSONArray("county");
                for(int i = 0; i < warn_county.length(); ++i) {
                    WarnCenterYJXXGridBean warn = new WarnCenterYJXXGridBean();
                    warn.id = warn_county.getJSONObject(i).optString("id");
                    warn.level = warn_county.getJSONObject(i).optString("level");
                    warn.ico = warn_county.getJSONObject(i).optString("ico");
                    warn.put_str = warn_county.getJSONObject(i).optString("put_str");
                    warn.put_time = warn_county.getJSONObject(i).optString("yj_time");
                    this.county.add(warn);
                }
            }

            if (!obj.isNull("cityidex")) {
                JSONArray cityidex_county = obj.optJSONArray("cityidex");
                if (cityidex_county != null) {
                    for(int i = 0; i < cityidex_county.length(); ++i) {
                        PackLocalCity warn = new PackLocalCity();
                        warn.ID = cityidex_county.getJSONObject(i).optString("id");
                        warn.NAME = cityidex_county.getJSONObject(i).optString("name");
                        warn.isFjCity = false;
                        this.cityidex.add(warn);
                    }
                }
            }
        } catch (JSONException var10) {
            var10.printStackTrace();
        }

    }

    public String toString() {
        return null;
    }
}

