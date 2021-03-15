package com.pcs.ztqtj.view.activity.life.travel;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.AdapterTravelWeekWeather;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib.lib_pcs_v3.model.image.ImageFetcher;
import com.pcs.lib_ztqfj_v2.model.pack.net.week.PackTravelWeekDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.week.PackTravelWeekUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.week.WeekWeatherInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 旅游气象第一页
 *
 * @author WeiXJ
 */
@SuppressLint("ValidFragment")
public class TravelFragmentOne extends Fragment {
    private TextView weatherTextView;
    private TextView tempTextView;
    private TextView dateTextView;
    private ListView weekweather;
    private ImageView iconImageView;
    private TextView areaName;
    // private ImageView travelOneBanner;
    private AdapterTravelWeekWeather weekAdapter;
    private String area = "";
    private String cityName = "";
    private List<WeekWeatherInfo> weekWeatherList = new ArrayList<>();
    private ImageFetcher mImageFetcher;
    private MyReceiver myReceiver = new MyReceiver();

    public TravelFragmentOne(String area, String cityName, ImageFetcher imageFetcher) {
        this.area = area;
        this.cityName = cityName;
        mImageFetcher = imageFetcher;
    }

    @Override
    public void onResume() {
        super.onResume();
        PcsDataBrocastReceiver.registerReceiver(getActivity(), myReceiver);
    }

    @Override
    public void onPause() {
        super.onPause();
        PcsDataBrocastReceiver.unregisterReceiver(getActivity(), myReceiver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.travel_pageone_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        areaName = (TextView) getView().findViewById(R.id.area_tv);
        weatherTextView = (TextView) getView().findViewById(R.id.weather_tv);
        tempTextView = (TextView) getView().findViewById(R.id.temp_tv);
        dateTextView = (TextView) getView().findViewById(R.id.date_tv);
        iconImageView = (ImageView) getView().findViewById(R.id.weather_icon);
        weekweather = (ListView) getView().findViewById(R.id.weekweather);
        refreshWeekWeatherList(area);
    }

    String shareC = "";
    private PackTravelWeekUp packTravelWeekUp;
    /**
     * 更新一周天气数据
     **/
    private void refreshWeekWeatherList(String area) {
        packTravelWeekUp = new PackTravelWeekUp();
        packTravelWeekUp.setCity(area, cityName);
        PackTravelWeekDown packTravelWeekDown = (PackTravelWeekDown) PcsDataManager.getInstance().getNetPack(packTravelWeekUp.getName());
        if (packTravelWeekDown == null) {
            PcsDataDownload.addDownload(packTravelWeekUp);
            return;
        }
        weekWeatherList = packTravelWeekDown.getWeek();
        if (weekWeatherList.size() > 0) {
            WeekWeatherInfo info = weekWeatherList.get(0);
            updateWeather(info);
            weekAdapter = new AdapterTravelWeekWeather(getActivity(), weekWeatherList.subList(1, weekWeatherList.size()), mImageFetcher);
            weekweather.setAdapter(weekAdapter);
            weekAdapter.notifyDataSetChanged();
            shareC = packTravelWeekDown.getShareStr(cityName);
        } else {
            return;
        }
        // -----------顶部显示
        WeekWeatherInfo info = weekWeatherList.get(0);
        areaName.setText(cityName);

        String h_l_str = "";
        // ------今天
        weatherTextView.setText(info.weather);
        h_l_str += info.higt + "~" + info.lowt + "°C";

        tempTextView.setText(h_l_str);
        dateTextView.setText(info.gdt + "" + info.week);

        Drawable drawable = mImageFetcher.getImageCache().getBitmapFromAssets(packTravelWeekDown.getIconPath(packTravelWeekDown.getTodayIndex()));
        iconImageView.setImageDrawable(drawable);
    }

    /**
     * 更新实时天气数据
     */
    private void updateWeather(WeekWeatherInfo info) {
        weatherTextView.setText(info.wind_dir_day);
        if (!TextUtils.isEmpty(info.lowt)) {
            tempTextView.setText(info.lowt + "/" + info.higt + "°C");
        }
        dateTextView.setText(info.gdt + info.week);
    }

    public void reflashUI(String area, String cityName) {
        this.area = area;
        this.cityName = cityName;
    }


    private class MyReceiver extends PcsDataBrocastReceiver {
        @Override
        public void onReceive(String name, String errorStr) {
            if (name.equals(packTravelWeekUp.getName())) {
                PackTravelWeekDown down = (PackTravelWeekDown) PcsDataManager.getInstance().getNetPack(name);
                if (down == null) {
                    return;
                }
                refreshWeekWeatherList(area);
            }
        }
    }
}
