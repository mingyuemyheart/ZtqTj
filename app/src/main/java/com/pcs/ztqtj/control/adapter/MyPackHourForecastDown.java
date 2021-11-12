package com.pcs.ztqtj.control.adapter;

import com.pcs.lib_ztqfj_v2.model.pack.net.PackHourForecastDown;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MyPackHourForecastDown extends PackHourForecastDown {
    public List<MyPackHourForecastDown.HourForecast> list = new ArrayList();
    public String desc = "";

    public MyPackHourForecastDown() {
    }

    public void fillData(String jsonStr) {
        this.list.clear();

        try {
            JSONObject obj = new JSONObject(jsonStr);
            this.desc = obj.optString("desc");
            JSONArray arr = obj.getJSONArray("today");

            for(int i = 0; i < arr.length(); ++i) {
                JSONObject row = arr.getJSONObject(i);
                MyPackHourForecastDown.HourForecast pack = new MyPackHourForecastDown.HourForecast();
                pack.ico = row.getString("ico");
                pack.rainfall = row.getString("rainfall");
                if (!row.isNull("temperature")) {
                    pack.temperature = row.getString("temperature");
                } else {
                    pack.temperature = "";
                }
                pack.windspeed = row.getString("windspeed");
                pack.windlevel = row.getString("windlevel");
                pack.winddir = row.getString("winddir");
                pack.airpressure = row.getString("airpressure");
                pack.visibility = row.getString("visibility");
                pack.w_datetime = row.getString("w_datetime");
                pack.rh = row.getString("rh");
                pack.desc = row.optString("desc");
                pack.time = row.getString("time");
                if (pack.time != null && !"".equals(pack.time)) {
                    int tempTime = Integer.valueOf(pack.time);
                    pack.isDayTime = tempTime >= 7 && tempTime <= 18;
                }

                this.list.add(pack);
            }
        } catch (JSONException var8) {
            var8.printStackTrace();
        }

    }

    public class HourForecast {
        public String ico = "";
        public String rainfall = "";
        public String temperature = "";
        public String windspeed = "";
        public String windlevel = "";
        public String winddir = "";
        public String airpressure = "";
        public String visibility = "";
        public String rh = "";
        public String time = "";
        public String desc = "";
        public String w_datetime = "";
        public boolean isDayTime;

        public HourForecast() {
        }

        public String getTime() {
            if (this.time.equals("0")) {
                String str = this.w_datetime.substring(6, 8);
                return str + "日";
            } else {
                return this.time + "时";
            }
        }
    }
}

