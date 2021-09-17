package com.pcs.ztqtj.view.activity.life.travel;

public class MyPackTravelWeekDown extends MyPackWeekWeatherDown {
    public String cityId = "";
    public String cityName = "";

    public MyPackTravelWeekDown() {
    }

    public void fillData(String jsonStr) {
        super.fillData(jsonStr);
    }

    public int getTodayIndex() {
        return 0;
    }
}

