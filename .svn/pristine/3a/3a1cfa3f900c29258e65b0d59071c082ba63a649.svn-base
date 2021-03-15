package com.pcs.ztqtj.view.fragment.airquality;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.net.airinfopack.AirInfoSh;
import com.pcs.lib_ztqfj_v2.model.pack.net.airinfopack.AirInfoYb;
import com.pcs.lib_ztqfj_v2.model.pack.net.airinfopack.PackAirInfoShDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.airinfopack.PackAirInfoShUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.airinfopack.PackAirYbDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.airinfopack.PackAirYbUp;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.air_quality.AdapterAirLive;
import com.pcs.ztqtj.control.adapter.air_quality.AdapterAirLiveYb;
import com.pcs.ztqtj.control.adapter.air_quality.AdapterAirLive_list;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by chenjx
 * on 2018/2/26.
 */

public class FragmentAirQualityLive extends Fragment {

    private AdapterAirLive adapterAirLive;
    private AdapterAirLive_list adapterAirLive_list, adapterAirLive_list_down;
    private AdapterAirLiveYb adapterAirLiveYb;
    private TextView tv_title, tv_clara, tv_air_yb_unit, tv_air_yb_time;
    private GridView tablegrid, lv_airlive, lv_airlive_down, tablegrid_down;
    private PackAirInfoShUp packAirInfoShUp;
    private PackAirInfoShDown packAirInfoShDown;
    private MyReceiver myReceiver = new MyReceiver();
    private ArrayList<AirInfoSh> list_grid;
    private ArrayList<AirInfoYb> list_yb;
    private ArrayList<String> list, list_down;
    private PackAirYbUp airYbUp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_airlive, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initData();
        req();
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
    }

    private void req() {
        packAirInfoShUp = new PackAirInfoShUp();
        packAirInfoShUp.area = ids;
        PcsDataDownload.addDownload(packAirInfoShUp);
        airYbUp = new PackAirYbUp();
        PcsDataDownload.addDownload(airYbUp);
    }

    private String ids, names;

    public void setDate(String id, String name) {
        this.ids = id;
        this.names = name;
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

    private void initView() {

        tv_title = (TextView) getView().findViewById(R.id.tv_airlive_time);
        tv_clara = (TextView) getView().findViewById(R.id.tv_airlive_clara);
        lv_airlive = (GridView) getView().findViewById(R.id.lv_airlive);
        tablegrid = (GridView) getView().findViewById(R.id.tablegrid);

        tv_air_yb_unit = getView().findViewById(R.id.tv_air_yb_unit);
        tv_air_yb_time = getView().findViewById(R.id.tv_air_yb_time);
        lv_airlive_down = getView().findViewById(R.id.lv_airlive_down);
        tablegrid_down = getView().findViewById(R.id.tablegrid_down);
    }

    private class MyReceiver extends PcsDataBrocastReceiver {
        @Override
        public void onReceive(String name, String errorStr) {
            if (name.equals(packAirInfoShUp.NAME)) {
                packAirInfoShDown = (PackAirInfoShDown) PcsDataManager.getInstance().getNetPack(name);
                if (packAirInfoShDown == null) {
                    return;
                }
                list_grid.clear();
                list_grid.addAll(packAirInfoShDown.list);
                list.clear();
                list.addAll(packAirInfoShDown.list_arr);
                tv_title.setText("天津市空气质量分区预报（" + packAirInfoShDown.time_pub + " 发布)");
                tv_clara.setText(packAirInfoShDown.pub_unit);
                adapterAirLive.notifyDataSetChanged();
                adapterAirLive_list.notifyDataSetChanged();
            } else if (name.equals(airYbUp.getName())) {
                PackAirYbDown down = (PackAirYbDown) PcsDataManager.getInstance().getNetPack(name);
                if (down == null) {
                    return;
                }
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

}
