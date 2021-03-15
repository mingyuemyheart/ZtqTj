package com.pcs.ztqtj.control.main_en_weather;

import android.app.Activity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.pcs.lib.lib_pcs_v3.control.tool.Util;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib.lib_pcs_v3.model.image.ImageFetcher;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityMain;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackHourForecastDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackHourForecastUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.week.PackMainWeekWeatherDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.week.PackMainWeekWeatherUp;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.AdapterWeekGridView;
import com.pcs.ztqtj.control.adapter.hour_forecast.AdapterMainHourForecastEn;
import com.pcs.ztqtj.control.inter.InterfaceShowBg;
import com.pcs.ztqtj.control.main_weather.CommandMainBase;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.view.activity.activityEn.ActivityMainEn;
import com.pcs.ztqtj.view.myview.Hour24View_En;
import com.pcs.ztqtj.view.myview.MyGridView;
import com.pcs.ztqtj.view.myview.MyHScrollView;
import com.pcs.ztqtj.view.myview.TemperatureView;

import java.util.ArrayList;
import java.util.List;

/**
 * JiangZy on 2016/6/3.
 * 7天天气。逐时预报
 */
public class CommandMainEnRow2 extends CommandMainBase {
    private ActivityMainEn mActivity;
    private ViewGroup mRootLayout;
    private ImageFetcher mImageFetcher;
    private InterfaceShowBg mShowBg;
    //行视图
    private View mRowView;

    //一周天气-7天
    private PackMainWeekWeatherUp packWeekUp = new PackMainWeekWeatherUp();

    private RadioGroup radio_group_week_24;

    private MyHScrollView layout_week;
    private MyHScrollView layout_24house;

    private Hour24View_En main24hour;
    //适配器：小时
    private AdapterMainHourForecastEn adapterMain;
    //适配器：一周
    private AdapterWeekGridView mWeekAdapter;

    //一周高温列表
    private List<Float> mHighList = new ArrayList<Float>();
    //一周低温列表
    private List<Float> mLowList = new ArrayList<Float>();
    //改变城市
    private boolean mChangeCity = false;

    private LinearLayout lay_comman02;
//    private Button btn36Hours;


    private TextView not_time_data;

    private int height;

    public CommandMainEnRow2(Activity activity, ViewGroup rootLayout, ImageFetcher imageFetcher,
                             InterfaceShowBg showBg, int mheight) {
        mActivity = (ActivityMainEn) activity;
        mRootLayout = rootLayout;
        mImageFetcher = imageFetcher;
        mShowBg = showBg;
        height = mheight;
    }

    // 天气内容
    private GridView gridViewWeek;
    private TemperatureView tempertureview;
    private LinearLayout tv_time;

    @Override
    protected void init() {
        mRowView = LayoutInflater.from(mActivity).inflate(R.layout.item_home_weather_en_2, null);
        DisplayMetrics dm = mActivity.getResources().getDisplayMetrics();
        int heights = dm.heightPixels;
        mRowView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                heights / 3));
        mRootLayout.addView(mRowView);
        lay_comman02 = (LinearLayout) mRowView.findViewById(R.id.lay_comman02);
//        radio_group_week_24 = (RadioGroup) mRowView.findViewById(R.id.radio_group_week_24);
        layout_week = (MyHScrollView) mRowView.findViewById(R.id.layout_week);
        main24hour = (Hour24View_En) mRowView.findViewById(R.id.main24hour);

        gridViewWeek = (GridView) mRowView.findViewById(R.id.maingridview);
//        btn36Hours = (Button) mRowView.findViewById(R.id.main_36hour);
        layout_24house = (MyHScrollView) mRowView.findViewById(R.id.layout_24house);
        not_time_data = (TextView) mRowView.findViewById(R.id.not_time_data);
        tv_time = (LinearLayout) mRowView.findViewById(R.id.tv_time);
        tempertureview = (TemperatureView) mRowView.findViewById(R.id.tempertureview);
        WindowManager wm = mActivity.getWindowManager();
        int width = wm.getDefaultDisplay().getWidth();
        not_time_data.setWidth(width);
//        radio_group_week_24.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                switch (checkedId) {
//                    case R.id.main_week:
//                        layout_week.setVisibility(View.VISIBLE);
//                        layout_24house.setVisibility(View.GONE);
//                        break;
//                    case R.id.main_24hour:
//                        layout_24house.setVisibility(View.VISIBLE);
//                        layout_week.setVisibility(View.GONE);
//                        break;
//                }
//            }
//        });
        mWeekAdapter = new AdapterWeekGridView(mActivity,
                mImageFetcher, mShowBg);

//        main24hour.setOnTouchListener(touchListener);
//        layout_24house.setOnTouchListener(touchListener);
//        layout_week.setOnTouchListener(touchListener);

        refresh();
    }

    public void setChangeCity() {
        mChangeCity = true;
    }

    @Override
    protected void refresh() {
        reFlush24House();
        //reFreshWeek();
        if (mChangeCity) {
            layout_week.setVisibility(View.VISIBLE);
            layout_24house.setVisibility(View.GONE);
            if (mWeekAdapter != null) {
                mWeekAdapter.setClickPositon(1);
            }
            radio_group_week_24.check(R.id.main_24hour);
            mChangeCity = false;
        }
    }


    private String changeStr(String value) {
        String temHs_little = "";
        String tempHs = String.valueOf(Float.parseFloat(value) * 1.8 + 32);
        if (!TextUtils.isEmpty(tempHs) && tempHs.indexOf(".") > 1) {
            tempHs = tempHs.substring(0, tempHs.indexOf(".") + 1).replace(".", "");
        }
        if (!TextUtils.isEmpty(String.valueOf(Float.parseFloat(value) * 1.8 + 32)) && String.valueOf(Float
                .parseFloat(value) * 1.8 + 32).indexOf(".") > 1) {
            temHs_little = value.substring(value.indexOf(".") + 1, value.length());
        }
        if (!temHs_little.equals("0")) {
            tempHs = tempHs + "." + temHs_little.substring(0, 1);
        }
        return tempHs;
    }

    private void reFlush24House() {
        //逐时预报获取数据
        MyGridView gridview24hour = (MyGridView) mRowView.findViewById(R.id.gridview24hour);
        adapterMain = new AdapterMainHourForecastEn(mActivity);
        gridview24hour.setAdapter(adapterMain);
        if (main24hour == null) {
            View view = LayoutInflater.from(mActivity).inflate(R.layout.item_home_weather_2, null);
            if (view != null) {
                main24hour = (Hour24View_En) view.findViewById(R.id.main24hour);
            }
        }
        if (main24hour != null) {
            PackLocalCityMain packCity = ZtqCityDB.getInstance().getCityMain();
            if (packCity == null) {
                return;
            }
            //24小时
            PackHourForecastUp packHourUp = new PackHourForecastUp();
            packHourUp.county_id = packCity.ID;
            PackHourForecastDown down = (PackHourForecastDown) PcsDataManager.getInstance().getNetPack(packHourUp
                    .getName());
            List<Float> mTopTemp = new ArrayList<Float>();
            List<Float> mLowRain = new ArrayList<Float>();
            if (down != null && down.list.size() > 0) {
                int size = down.list.size();
                for (int i = 0; i < down.list.size(); i++) {
                    String rain = down.list.get(i).rainfall;
//                  if (i == 10 || i == 12 || i == 14) {
//                        rain = "15";
//                  }
                    String temp;
                    if (CommandMainEnRow1.is_hs) {
                        temp = changeStr(down.list.get(i).temperature);
                    } else {
                        temp = down.list.get(i).temperature;
                    }
                    if (!TextUtils.isEmpty(temp)) {
                        mTopTemp.add(Float.parseFloat(temp));
                    } else {
                        continue;
                    }
                    if (!TextUtils.isEmpty(rain)) {
                        mLowRain.add(Float.parseFloat(rain));
                    } else {
                        mLowRain.add(0f);
                    }
                }


                lay_comman02.setVisibility(View.VISIBLE);
                not_time_data.setVisibility(View.GONE);
                tv_time.setVisibility(View.VISIBLE);
                int width = Util.dip2px(mActivity, 50) * size;

                gridview24hour.setNumColumns(size);
                gridview24hour.setVisibility(View.VISIBLE);
                ViewGroup.LayoutParams gradviewParams = gridview24hour.getLayoutParams();
                gradviewParams.width = width;
                gridview24hour.setLayoutParams(gradviewParams);

                main24hour.setVisibility(View.VISIBLE);
                main24hour.setCount(size);
                ViewGroup.LayoutParams mainParams = main24hour.getLayoutParams();
                mainParams.width = width;
                main24hour.setLayoutParams(mainParams);
            } else {
                lay_comman02.setVisibility(View.INVISIBLE);
                not_time_data.setVisibility(View.VISIBLE);
                tv_time.setVisibility(View.GONE);
                gridview24hour.setVisibility(View.GONE);
                main24hour.setVisibility(View.GONE);
            }
            main24hour.setTemperture(mTopTemp, mLowRain);
        }
    }

    private void reFreshWeek() {
        // 温度
        PackLocalCityMain packCity = ZtqCityDB.getInstance().getCityMain();
        if (packCity == null) {
            return;
        }
        packWeekUp.setCity(packCity);
        PackMainWeekWeatherDown packWeekDown = (PackMainWeekWeatherDown) PcsDataManager.getInstance().getNetPack
                (packWeekUp.getName());
        if (packWeekDown == null || packWeekDown.getWeek() == null
                || packWeekDown.getWeek().size() == 0) {
//            // 清空显示
//            gridViewWeek.setAdapter(null);
//            tempertureview.setTemperture(null, null);
        } else {
            int size = packWeekDown.getWeek().size();
            int width = getWeekItemWidth() * size;
            ViewGroup.LayoutParams params = gridViewWeek.getLayoutParams();
            params.width = width;
            mWeekAdapter.setView(mRowView);
            gridViewWeek.setAdapter(mWeekAdapter);
            gridViewWeek.setNumColumns(packWeekDown.getWeek().size());
            gridViewWeek.setLayoutParams(params);
            gridViewWeek.setColumnWidth(getWeekItemWidth());
            mWeekAdapter.setUpData(packWeekDown);
            mHighList.clear();
            mLowList.clear();
            for (int i = 0; i < packWeekDown.getWeek().size(); i++) {
                //最后一个高温或低温为空这可以单一添加，否者直接丢弃整个高低温数据
                if (i == packWeekDown.getWeek().size() - 1) {
                    if (!TextUtils.isEmpty(packWeekDown.getWeek().get(i).higt)) {
                        mHighList.add(Float.parseFloat(packWeekDown.getWeek().get(i).higt));
                    }
                    if (!TextUtils.isEmpty(packWeekDown.getWeek().get(i).lowt)) {
                        mLowList.add(Float.parseFloat(packWeekDown.getWeek().get(i).lowt));
                    }
                } else {
                    if (TextUtils.isEmpty(packWeekDown.getWeek().get(i).higt) || TextUtils.isEmpty(packWeekDown
                            .getWeek().get(i).lowt)) {
                    } else {
                        mHighList.add(Float.parseFloat(packWeekDown.getWeek().get(i).higt));
                        mLowList.add(Float.parseFloat(packWeekDown.getWeek().get(i).lowt));
                    }
                }
            }
            tempertureview.setTemperture(mHighList, mLowList, size);
            params = tempertureview.getLayoutParams();
            params.width = width;
            tempertureview.setLayoutParams(params);
        }
    }

    private int getWeekItemWidth() {
        int width = (int) (Util.getScreenWidth(mActivity) / 7.0f);
        return width;
    }
}
