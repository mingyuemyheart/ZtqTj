package com.pcs.ztqtj.control.adapter;

import android.text.TextUtils;

import com.pcs.lib_ztqfj_v2.model.pack.net.PackMapForecastDown;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MyPackMapForecastDown extends PackMapForecastDown {
    public List<MyPackMapForecastDown.MapForecast> list = new ArrayList();

    public MyPackMapForecastDown() {
    }

    public void fillData(String jsonStr) {
        if (jsonStr != null && !"".equals(jsonStr)) {
            try {
                this.list.clear();
                JSONObject obj = new JSONObject(jsonStr);
                this.updateMill = obj.optLong("updateMill");
                JSONArray arr = obj.getJSONArray("today");

                for (int i = 0; i < arr.length(); ++i) {
                    JSONObject row = arr.getJSONObject(i);
                    MyPackMapForecastDown.MapForecast pack = new MyPackMapForecastDown.MapForecast();
                    pack.ico = row.getString("ico");
                    pack.desc = row.getString("desc");
                    pack.rainfall = row.getString("rainfall");
                    pack.temperature = row.getString("temperature");
                    pack.visibility = row.getString("visibility");
                    pack.time = row.getString("time");
                    if (pack.time != null && !"".equals(pack.time)) {
                        int tempTime = Integer.valueOf(pack.time);
                        pack.isDayTime = tempTime >= 7 && tempTime <= 18;
                    }

                    pack.winddir = row.getString("winddir");
                    pack.windspeed = row.getString("windspeed");
                    pack.windlevel = row.getString("windlevel");
                    this.list.add(pack);
                }
            } catch (JSONException var8) {
                var8.printStackTrace();
            }

        }
    }

    public String toString() {
        return this.list.toString();
    }

    public class MapForecast {
        public String ico;
        public String desc;
        public String rainfall;
        public String temperature;
        public String visibility;
        public String time;
        public String winddir;
        public String windspeed;
        public String windlevel;
        public boolean isDayTime;

        public MapForecast() {
        }

        public String getTime() {
            return TextUtils.isEmpty(this.time) ? "" : this.time + "时";
        }
    }
}

