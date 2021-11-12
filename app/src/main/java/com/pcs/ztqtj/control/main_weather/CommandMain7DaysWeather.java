package com.pcs.ztqtj.control.main_weather;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.pcs.lib.lib_pcs_v3.control.tool.Util;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCity;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityMain;
import com.pcs.lib_ztqfj_v2.model.pack.net.week.PackMainWeekWeatherDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.week.WeekWeatherInfo;
import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.Adapter7DaysGridView;
import com.pcs.ztqtj.control.inter.InterfaceShowBg;
import com.pcs.ztqtj.control.tool.utils.TextUtil;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.util.OkHttpUtil;
import com.pcs.ztqtj.view.myview.TemperatureView;
import com.pcs.ztqtj.view.myview.typhoon.AutoFitTextView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 首页-逐日预报
 */
public class CommandMain7DaysWeather extends CommandMainBase {

    private Activity activity;
    private ViewGroup rootLayout;
    private View rowView;
    private Adapter7DaysGridView adapter;
    private List<WeekWeatherInfo> weekList = new ArrayList<>();
    //改变城市
    private boolean mChangeCity = true;
    private GridView gridViewWeek;
    private TemperatureView tempertureview;
    private InterfaceShowBg mShowBg;
    private TextView tvPublicUnit;
    private ImageView ivPublicUnit;
    private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:00", Locale.CHINA);

    public CommandMain7DaysWeather(Activity activity , ViewGroup rootLayout, InterfaceShowBg mShowBg) {
        this.activity = activity;
        this.rootLayout = rootLayout;
        this.mShowBg = mShowBg;
    }

    @Override
    protected void init() {
        rowView = LayoutInflater.from(activity).inflate(R.layout.layout_main_7days_weather, rootLayout, false);
        rootLayout.addView(rowView);
        tvPublicUnit = rowView.findViewById(R.id.tvPublicUnit);
        ivPublicUnit = rowView.findViewById(R.id.ivPublicUnit);
        tempertureview = rowView.findViewById(R.id.tempertureview);
        gridViewWeek = rowView.findViewById(R.id.maingridview);
        adapter = new Adapter7DaysGridView(activity, weekList, mShowBg);
        gridViewWeek.setAdapter(adapter);
    }

    public void setChangeCity() {
        mChangeCity = true;
    }

    @Override
    protected void refresh() {
        okHttpWeekData();
    }

    /**
     * 获取一周天气
     */
    private void okHttpWeekData() {
        final PackLocalCity city = ZtqCityDB.getInstance().getCityMain();
        if(city == null) return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param  = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    JSONObject info = new JSONObject();
                    info.put("stationId", city.ID);
                    param.put("paramInfo", info);
                    String json = param.toString();
                    Log.e("week_data", json);
                    final String url = CONST.BASE_URL+"week_data";
                    Log.e("week_data", url);
                    RequestBody body = FormBody.create(MediaType.parse("application/json; charset=utf-8"), json);
                    OkHttpUtil.enqueue(new Request.Builder().post(body).url(url).build(), new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        }
                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            if (!response.isSuccessful()) {
                                return;
                            }
                            final String result = response.body().string();
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
//                                    Log.e("week_data", result);
                                    if (!TextUtil.isEmpty(result)) {
                                        try {
                                            JSONObject obj = new JSONObject(result);
                                            if (!obj.isNull("b")) {
                                                JSONObject bobj = obj.getJSONObject("b");
                                                if (!bobj.isNull("p_new_week")) {
                                                    JSONObject p_new_weekobj = bobj.getJSONObject("p_new_week");
                                                    if (!TextUtil.isEmpty(p_new_weekobj.toString())) {
                                                        PackMainWeekWeatherDown packWeekDown = new PackMainWeekWeatherDown();
                                                        packWeekDown.fillData(p_new_weekobj.toString());
                                                        if (packWeekDown != null && packWeekDown.getWeek() != null && packWeekDown.getWeek().size() != 0) {
                                                            if (!TextUtils.isEmpty(getAreaName())) {
                                                                ivPublicUnit.setVisibility(View.VISIBLE);
                                                            } else {
                                                                ivPublicUnit.setVisibility(View.GONE);
                                                            }
                                                            tvPublicUnit.setText(getAreaName()+sdf1.format(new Date())+"发布");

                                                            weekList = new ArrayList<>(packWeekDown.getWeek());
                                                            int size = weekList.size();
                                                            int width = getWeekItemWidth()*size;
                                                            ViewGroup.LayoutParams params = gridViewWeek.getLayoutParams();
                                                            params.width = width;
                                                            adapter.setView(rowView);
                                                            gridViewWeek.setNumColumns(size);
                                                            gridViewWeek.setLayoutParams(params);
                                                            gridViewWeek.setColumnWidth(getWeekItemWidth());
                                                            adapter.setUpdate(weekList);

                                                            List<Float> mHighList = new ArrayList<>();
                                                            List<Float> mLowList = new ArrayList<>();
                                                            for (int i = 0; i < weekList.size(); i++) {
                                                                WeekWeatherInfo info = weekList.get(i);
                                                                if (!TextUtils.isEmpty(info.higt) && !TextUtils.equals("�", info.higt)) {
                                                                    mHighList.add(Float.parseFloat(info.higt));
                                                                }
                                                                if (!TextUtils.isEmpty(info.lowt) && !TextUtils.equals("�", info.lowt)) {
                                                                    mLowList.add(Float.parseFloat(info.lowt));
                                                                }
                                                            }
                                                            tempertureview.setTemperture(mHighList, mLowList, size);
                                                            params = tempertureview.getLayoutParams();
                                                            params.width = width;
                                                            tempertureview.setLayoutParams(params);

                                                            if (mChangeCity) {
                                                                if (adapter != null) {
                                                                    adapter.setClickPositon(0);
                                                                }
                                                                mChangeCity = false;
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            });
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private int getWeekItemWidth() {
        return (int) (Util.getScreenWidth(activity)/7.0f);
    }

    private String getAreaName() {
        String area = "";
        PackLocalCityMain cityMain = ZtqCityDB.getInstance().getCityMain();
        String name = cityMain.NAME;
        if (name.equals("天津市区")) {
            area = "天津市";
        } else if (name.equals("宝坻区") || name.equals("北辰区") || name.equals("东丽区") || name.equals("滨海新区") || name.equals("静海区")
                || name.equals("蓟州区") || name.equals("津南区") || name.equals("武清区") || name.equals("宁河区") || name.equals("西青区")){
            area = name;
        } else {
            area = "";
        }
        if (!cityMain.ID.startsWith("10103")) {
            return "";
        }
        return area+"气象台：";
    }

}
