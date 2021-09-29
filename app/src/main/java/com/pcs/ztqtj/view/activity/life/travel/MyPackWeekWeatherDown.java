package com.pcs.ztqtj.view.activity.life.travel;

import com.pcs.lib.lib_pcs_v3.model.pack.PcsPackDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.week.WeekWeatherInfo;
import com.pcs.ztqtj.util.CONST;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public abstract class MyPackWeekWeatherDown extends PcsPackDown {
    public String key;
    public String sys_time;
    public long sys_time_l;
    public String p_name = "";
    protected List<WeekWeatherInfo> weekWeatherList = new ArrayList();
    protected List<WeekWeatherInfo> threeWeatherList = new ArrayList();

    public MyPackWeekWeatherDown() {
    }

    public void fillData(String jsonStr) {
        if (jsonStr != null && !"".equals(jsonStr) && jsonStr.indexOf("week") >= 0) {
            try {
                this.weekWeatherList.clear();
                JSONObject temp = new JSONObject(jsonStr);
                this.sys_time = temp.optString("sys_time");
                if (this.sys_time != null && !"".equals(this.sys_time)) {
                    this.sys_time_l = Long.valueOf(this.sys_time);
                } else {
                    this.sys_time_l = System.currentTimeMillis();
                }

                this.p_name = temp.optString("p_name");
                JSONArray arrWeek = temp.getJSONArray("week");

                for(int i = 0; i < arrWeek.length(); ++i) {
                    WeekWeatherInfo info = new WeekWeatherInfo();
                    JSONObject json = arrWeek.getJSONObject(i);
                    info.gdt = json.optString("gdt");
                    info.week = json.optString("week");
                    info.higt = json.optString("higt");
                    info.lowt = json.optString("lowt");
                    info.weather = json.optString("weather");
                    info.wd_day = json.optString("wd_day");
                    info.wd_day_ico = json.optString("wd_day_ico");
                    info.wd_night = json.optString("wd_night");
                    info.wd_night_ico = json.optString("wd_night_ico");
                    info.wind_dir_day = json.optString("wind_dir_day");
                    info.wind_dir_night = json.optString("wind_dir_night");
                    info.wind_speed_day = json.optString("wind_speed_day");
                    info.wind_speed_night = json.optString("wind_speed_night");
                    info.is_night = json.optString("is_night");
                    info.yb_desc = json.optString("yb_desc");
                    info.yb_time = json.optString("yb_time");
                    info.us_day = json.optString("us_day");
                    info.us_gdt = json.optString("us_gdt");
                    info.us_weather = json.optString("us_weather");
                    info.us_week = json.optString("us_week");
                    info.setShowTemperature();
                    this.weekWeatherList.add(info);
                }

                this.fillThreeDay();
            } catch (JSONException var7) {
                var7.printStackTrace();
            }

        }
    }

    public String getIconPath(int index) {
        String path = "";
        if (index > this.weekWeatherList.size() - 1) {
            return path;
        } else {
            WeekWeatherInfo info = (WeekWeatherInfo)this.weekWeatherList.get(index);
            path = "weather_icon/daytime/w" + info.wd_day_ico + ".png";
            if (index == this.getTodayIndex() && "1".equals(info.is_night)) {
                path = "weather_icon/night/n" + info.wd_night_ico + ".png";
            }

            return path;
        }
    }

    public abstract int getTodayIndex();

    public WeekWeatherInfo getToday() {
        return this.getTodayIndex() > this.weekWeatherList.size() - 1 ? null : (WeekWeatherInfo)this.weekWeatherList.get(this.getTodayIndex());
    }

    private void fillThreeDay() {
        this.threeWeatherList.clear();
        int index = this.getTodayIndex();
        if (index <= this.weekWeatherList.size() - 1) {
            if (index <= this.weekWeatherList.size() - 1) {
                this.threeWeatherList.add(this.weekWeatherList.get(index));
            }

            index = this.getTodayIndex() + 1;
            if (index <= this.weekWeatherList.size() - 1) {
                this.threeWeatherList.add(this.weekWeatherList.get(index));
            }

            index = this.getTodayIndex() + 2;
            if (index <= this.weekWeatherList.size() - 1) {
                this.threeWeatherList.add(this.weekWeatherList.get(index));
            }

        }
    }

    public List<WeekWeatherInfo> getThreeDay() {
        return this.threeWeatherList;
    }

    public List<WeekWeatherInfo> getWeek() {
        return this.weekWeatherList;
    }

    public String getShareStr(String cityName) {
        StringBuffer shareC = new StringBuffer();
        List<WeekWeatherInfo> list = this.getThreeDay();
        shareC.append(cityName + ":");
        if (list.size() > 0) {
            shareC.append(((WeekWeatherInfo)list.get(0)).gdt + ",");
            shareC.append(((WeekWeatherInfo)list.get(0)).weather + ",");
            shareC.append(((WeekWeatherInfo)list.get(0)).higt + "~");
            shareC.append(((WeekWeatherInfo)list.get(0)).lowt + "°,");
        }

        if (list.size() > 1) {
            shareC.append(((WeekWeatherInfo)list.get(1)).gdt + ",");
            shareC.append(((WeekWeatherInfo)list.get(1)).weather + ",");
            shareC.append(((WeekWeatherInfo)list.get(1)).higt + "~");
            shareC.append(((WeekWeatherInfo)list.get(1)).lowt + "°,");
        }

        if (list.size() > 2) {
            shareC.append(((WeekWeatherInfo)list.get(2)).gdt + ",");
            shareC.append(((WeekWeatherInfo)list.get(2)).weather + ",");
            shareC.append(((WeekWeatherInfo)list.get(2)).higt + "~");
            shareC.append(((WeekWeatherInfo)list.get(2)).lowt + "°");
        }
        shareC.append(CONST.SHARE_URL);
        return shareC.toString();
    }
}

