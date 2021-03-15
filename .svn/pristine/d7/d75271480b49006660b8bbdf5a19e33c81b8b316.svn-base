package com.pcs.ztqtj.view.fragment.weatherflood;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.view.activity.product.waterflood.ActivityWaterLevelInfo;
import com.pcs.ztqtj.view.myview.WeatherView;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.net.waterflood.PackReservoirWaterInfoUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.waterflood.PackRiverWaterInfoUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.waterflood.PackWaterInfoDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.waterflood.PackWaterInfoDown.ItemTimeInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Z 视图展示
 */
public class FragmentWeatherView extends Fragment {
    private PackWaterInfoDown mPackDown;
    private WeatherView weatherfload_view;
    private TextView weather_top_info;
    private TextView weather_buttom_info;
    private ActivityWaterLevelInfo activity;

    private TextView line_usuall;
    private TextView line_warn;
    private TextView line_protect;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (ActivityWaterLevelInfo) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fra_weather_flood_view, null);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        intData();
    }

    private void initView() {
        weatherfload_view = (WeatherView) getActivity().findViewById(R.id.weatherfload_view);
        weather_top_info = (TextView) getActivity().findViewById(R.id.weather_top_info);
        weather_buttom_info = (TextView) getActivity().findViewById(R.id.weather_buttom_info);

        line_usuall = (TextView) getActivity().findViewById(R.id.line_usuall);
        line_warn = (TextView) getActivity().findViewById(R.id.line_warn);
        line_protect = (TextView) getActivity().findViewById(R.id.line_protect);
    }

    private void intData() {
        mPackDown = new PackWaterInfoDown();
        String top = "";
        String buttom = "";
        List<Float> valueTemp = new ArrayList<Float>();
        List<String> valueTime = new ArrayList<String>();
        // 取哪个数据？
        if (activity.getButtonSTATUS() == 1) {
            // 河道水位
            mPackDown = (PackWaterInfoDown) PcsDataManager.getInstance().getNetPack(PackRiverWaterInfoUp.NAME + "#" + activity.station_id);
            line_protect.setVisibility(View.VISIBLE);
            line_usuall.setText("水位");
            line_warn.setText("警戒水位");
            line_protect.setText("保证水位");
        } else {
            line_protect.setVisibility(View.GONE);
            line_usuall.setText("水位");
            line_warn.setText("汛限水位");
            // 水库
            mPackDown = (PackWaterInfoDown) PcsDataManager.getInstance().getNetPack(PackReservoirWaterInfoUp.NAME + "#" + activity.station_id);
        }
        if (mPackDown == null) {
        } else {
            // 加载数据
            for (int i = 0; i < mPackDown.riverList.size(); i++) {
                ItemTimeInfo pack = mPackDown.riverList.get(i);
                if (valueTemp.size() > 24) {
                } else {
                    // 值或时间不存在这直接剔除该点数据
                    if (!TextUtils.isEmpty(pack.water) && !TextUtils.isEmpty(pack.hour)) {
                        valueTemp.add(Float.parseFloat(pack.water));
                        valueTime.add(pack.hour);
                    }
                }
            }

            // Collections.reverse(valueTemp);
            // Collections.reverse(valueTime);
            activity.setStationName(mPackDown.des);
            weatherfload_view.cleanData();
            weatherfload_view.setCompany("水位m");
            weatherfload_view.setValue(valueTemp, valueTime);

            if (activity.getButtonSTATUS() == 1) {

                try {
                    // 河道--预警水位，保证水位
                    if (!TextUtils.isEmpty(mPackDown.warn)) {
                        weatherfload_view.setWranLine(Float.parseFloat(mPackDown.warn));
                    }
                    if (!TextUtils.isEmpty(mPackDown.ensure)) {
                        weatherfload_view.setProtectLine(Float.parseFloat(mPackDown.ensure));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                top += " 所在流域:" + mPackDown.river + "\n\r";
                top += "所在溪流:" + mPackDown.basin + "\n\r";
                top += "统计时段:" + mPackDown.count_time;

                buttom += " 当前水位:" + mPackDown.water_time + "\n\r";
                buttom += "最高水位:" + mPackDown.max + "\n\r";
                buttom += "最低水位:" + mPackDown.min + "\n\r";
                String strensure = "";

                if (!TextUtils.isEmpty(mPackDown.ensure)) {
                    strensure += mPackDown.ensure + "米";
                }

                String strwarn = "";

                if (!TextUtils.isEmpty(mPackDown.warn)) {
                    strwarn += mPackDown.warn + "米";
                }

                buttom += "保证水位:" + strensure + "\n\r";
                buttom += "警戒水位:" + strwarn;

            } else {
                try {
                    // 水库--预警水位
                    if (!TextUtils.isEmpty(mPackDown.flood)) {
                        weatherfload_view.setWranLine(Float.parseFloat(mPackDown.flood));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                top += " 所在流域:" + mPackDown.river + "\n\r";
                top += "所在溪流:" + mPackDown.basin + "\n\r";
                top += "统计时段:" + mPackDown.count_time;

                buttom += " 当前水位:" + mPackDown.water_time + "\n\r";
                buttom += "最高水位:" + mPackDown.max + "\n\r";
                buttom += "最低水位:" + mPackDown.min + "\n\r";
                buttom += "正常水位:" + mPackDown.normal + "\n\r";

                String strflood = "";
                if (!TextUtils.isEmpty(mPackDown.flood)) {
                    strflood += mPackDown.flood + "米";
                }

                buttom += "汛限水位:" + strflood + "\n\r";
                buttom += "汛限时段:" + mPackDown.time;
            }
            weatherfload_view.startDrawView();
            weather_top_info.setText(top);
            weather_buttom_info.setText(buttom);

        }
    }

}
