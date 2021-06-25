package com.pcs.ztqtj.view.fragment.airquality;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.pcs.lib_ztqfj_v2.model.pack.net.airinfopack.AirInfoSh;
import com.pcs.lib_ztqfj_v2.model.pack.net.airinfopack.AirInfoYb;
import com.pcs.lib_ztqfj_v2.model.pack.net.airinfopack.PackAirInfoShDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.airinfopack.PackAirInfoShUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.airinfopack.PackAirYbDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.airinfopack.PackAirYbUp;
import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.air_quality.AdapterAirLive;
import com.pcs.ztqtj.control.adapter.air_quality.AdapterAirLiveYb;
import com.pcs.ztqtj.control.adapter.air_quality.AdapterAirLive_list;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.util.OkHttpUtil;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 监测预报-空气质量-空气质量预报
 */
public class FragmentAirQualityLive extends Fragment {

    private AdapterAirLive adapterAirLive;
    private AdapterAirLive_list adapterAirLive_list, adapterAirLive_list_down;
    private AdapterAirLiveYb adapterAirLiveYb;
    private TextView tv_title, tv_clara, tv_air_yb_unit, tv_air_yb_time;
    private GridView tablegrid, lv_airlive, lv_airlive_down, tablegrid_down;
    private ArrayList<AirInfoSh> list_grid;
    private ArrayList<AirInfoYb> list_yb;
    private ArrayList<String> list, list_down;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_airlive, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initData();
    }

    private void initData() {
        list = new ArrayList<>();
        list_down = new ArrayList<>();
        adapterAirLive_list = new AdapterAirLive_list(getActivity(), list);
        adapterAirLive_list_down = new AdapterAirLive_list(getActivity(), list_down);
        lv_airlive.setAdapter(adapterAirLive_list);
        lv_airlive_down.setAdapter(adapterAirLive_list_down);

        list_grid = new ArrayList<>();
        list_yb = new ArrayList<>();
        adapterAirLive = new AdapterAirLive(getActivity(), list_grid);
        adapterAirLiveYb = new AdapterAirLiveYb(getActivity(), list_yb);

        tablegrid.setAdapter(adapterAirLive);
        tablegrid_down.setAdapter(adapterAirLiveYb);

        okHttpAirinfoYb();
        okHttpAirinfoSYb();
    }

    public void setDate(String id, String name) {
    }

    private void initView() {
        tv_title = getView().findViewById(R.id.tv_airlive_time);
        tv_clara = getView().findViewById(R.id.tv_airlive_clara);
        lv_airlive = getView().findViewById(R.id.lv_airlive);
        tablegrid = getView().findViewById(R.id.tablegrid);
        tv_air_yb_unit = getView().findViewById(R.id.tv_air_yb_unit);
        tv_air_yb_time = getView().findViewById(R.id.tv_air_yb_time);
        lv_airlive_down = getView().findViewById(R.id.lv_airlive_down);
        tablegrid_down = getView().findViewById(R.id.tablegrid_down);
    }

    /**
     * java.lang.IllegalStateException: No activity
     * 错误解决方案
     */
    @Override
    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取天津168h预报
     */
    private void okHttpAirinfoYb() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param  = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    String json = param.toString();
                    final String url = CONST.BASE_URL+"airinfo_s_yb";
                    Log.e("airinfo_s_yb", url);
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
                            Log.e("airinfo_s_yb", result);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        JSONObject obj = new JSONObject(result);
                                        if (!obj.isNull("b")) {
                                            JSONObject bobj = obj.getJSONObject("b");
                                            if (!bobj.isNull("airinfo_s_yb")) {
                                                JSONObject air_remark = bobj.getJSONObject("airinfo_s_yb");
                                                PackAirYbDown down = new PackAirYbDown();
                                                down.fillData(air_remark.toString());
                                                tv_air_yb_unit.setText(down.pub_unit);
                                                tv_air_yb_time.setText("天津市空气质量168小时预报（"+down.time_pub+" 发布)");
                                                list_down.clear();
                                                list_down.addAll(down.list_arr);
                                                adapterAirLive_list_down.notifyDataSetChanged();

                                                list_yb.clear();
                                                list_yb.addAll(down.list);
                                                adapterAirLiveYb.notifyDataSetChanged();
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
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

    /**
     * 获取天津分区预报
     */
    private void okHttpAirinfoSYb() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param  = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    String json = param.toString();
                    final String url = CONST.BASE_URL+"airinfo_yb";
                    Log.e("airinfo_yb", url);
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
                            Log.e("airinfo_yb", result);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        JSONObject obj = new JSONObject(result);
                                        if (!obj.isNull("b")) {
                                            JSONObject bobj = obj.getJSONObject("b");
                                            if (!bobj.isNull("airinfo_yb")) {
                                                JSONObject air_remark = bobj.getJSONObject("airinfo_yb");
                                                PackAirInfoShDown packAirInfoShDown = new PackAirInfoShDown();
                                                packAirInfoShDown.fillData(air_remark.toString());
                                                list_grid.clear();
                                                list_grid.addAll(packAirInfoShDown.list);
                                                list.clear();
                                                list.addAll(packAirInfoShDown.list_arr);
                                                tv_title.setText("天津市空气质量分区预报（" + packAirInfoShDown.time_pub + " 发布)");
                                                tv_clara.setText(packAirInfoShDown.pub_unit);
                                                adapterAirLive.notifyDataSetChanged();
                                                adapterAirLive_list.notifyDataSetChanged();
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
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

}
