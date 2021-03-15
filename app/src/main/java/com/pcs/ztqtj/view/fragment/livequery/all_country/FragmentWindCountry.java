package com.pcs.ztqtj.view.fragment.livequery.all_country;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.livequery.AdapterWind;
import com.pcs.ztqtj.view.activity.livequery.ActivityLiveQueryDetail;
import com.pcs.ztqtj.view.fragment.livequery.FragmentLiveQueryCommon;
import com.pcs.lib.lib_pcs_v3.control.tool.Util;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.net.column.ColumnInfo;
import com.pcs.lib_ztqfj_v2.model.pack.net.column.PackColumnDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.column.PackColumnUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackFltjProDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackFltjProUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackFltjZdDown;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Z on 2017/6/6.
 */

public class FragmentWindCountry extends FragmentLiveQueryCommon {

    private LinearLayout layout;
    private RadioGroup rgWind;
    private RadioGroup rgTimeTable;
    private TextView tvTitle;
    private ListView listView;
    private AdapterWind adapterWind;
    private List<PackFltjZdDown.FltjZd> windList = new ArrayList<>();
    private PackColumnUp packColumnUp = new PackColumnUp();
    private PackColumnDown packColumnDown;
    private MyReceiver receiver = new MyReceiver();
    private PackFltjProUp packFltjProUp = new PackFltjProUp();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState) {
        return LayoutInflater.from(getActivity()).inflate(R.layout.fragment_wind_country, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initEvent();
        initData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            PcsDataBrocastReceiver.unregisterReceiver(getActivity(), receiver);
            receiver = null;
        }
    }

    @Override
    public void refleshData() {
        RadioButton btn = (RadioButton) getView().findViewById(R.id.btn_left);
        btn.performClick();
    }

    private void initView() {
        layout = (LinearLayout) getView().findViewById(R.id.layout);
        rgWind = (RadioGroup) getView().findViewById(R.id.group_wind);
        rgTimeTable = (RadioGroup) getView().findViewById(R.id.rg_time);
        tvTitle = (TextView) getView().findViewById(R.id.tv_title);
        listView = (ListView) getView().findViewById(R.id.listview_wind);
    }

    private void initEvent() {
//        rgWind.setOnCheckedChangeListener(onCheckedChangeListener);
//        rgTimeTable.setOnCheckedChangeListener(onTimeTableListener);
        for(int i = 0; i < rgWind.getChildCount(); i++) {
            View view = rgWind.getChildAt(i);
            view.setOnClickListener(onLRListener);
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ActivityLiveQueryDetail.class);
                PackFltjZdDown.FltjZd bean = windList.get(position);
                if(bean != null) {
                    intent.putExtra("item", "wind");
                    intent.putExtra("stationName", bean.county);
                    startActivity(intent);
                }
            }
        });
    }

    private void initData() {
        PcsDataBrocastReceiver.registerReceiver(getActivity(), receiver);
        adapterWind = new AdapterWind(getActivity(), windList);
        listView.setAdapter(adapterWind);
        reqTimeTable();
        reqCurrentWindData();
        tvTitle.setText("全省站点当前瞬时风速排名");
    }

    /**
     * 初始化风速时间表头
     */
    private void initTimeTable(List<ColumnInfo> list) {
        int width = Util.getScreenWidth(getActivity());
        int radioWidth = width / list.size();
        int pad = Util.dip2px(getActivity(), 6);

        for (int i = 0; i < list.size(); i++) {
            RadioButton radioButton = new RadioButton(getActivity());
            radioButton.setId(101 + i);
            radioButton.setTextColor(getResources().getColor(R.color.text_black));
            radioButton.setBackgroundResource(R.drawable.radio_rain_selet);
            radioButton.setPadding(0, pad, 0, pad);
            radioButton.setMaxLines(1);
            radioButton.setGravity(Gravity.CENTER);
            radioButton.setTextSize(14);
            radioButton.setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));
            radioButton.setText(list.get(i).name);
            radioButton.setOnClickListener(onTimeTableListener);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(radioWidth, ViewGroup.LayoutParams
                    .WRAP_CONTENT);
            rgTimeTable.addView(radioButton, lp);
        }
    }

    private void clickLeft() {
        setTimeTableVisibility(false);
        tvTitle.setText("全省站点当前瞬时风速排名");
        reqCurrentWindData();
    }

    private void clickRight() {
        setTimeTableVisibility(true);
        tvTitle.setText("全省站点近24小时极大风速统计表");
        if(rgTimeTable.getChildCount() > 0) {
            //rgTimeTable.check(rgTimeTable.getChildAt(0).getId());
            rgTimeTable.getChildAt(0).performClick();
        }

    }

    /**
     * 点击时间列表
     */
    private void clickTimeTable(int index) {
        if(packColumnDown != null
                && packColumnDown.arrcolumnInfo.size() > index) {
            String name = packColumnDown.arrcolumnInfo.get(index).name;
            tvTitle.setText("全省站点" + name + "极大风速统计表");
            reqHourWindData(packColumnDown.arrcolumnInfo.size()-1-index);
        }
    }

    /**
     * 设置表头时间是否显示
     *
     * @param visibility
     */
    private void setTimeTableVisibility(boolean visibility) {
        if (visibility) {
            rgTimeTable.setVisibility(View.VISIBLE);
        } else {
            rgTimeTable.setVisibility(View.GONE);
        }
    }

    private void updateCurrentWindData(List<PackFltjZdDown.FltjZd> list) {
        windList.clear();
        windList.add(getHead());
        windList.addAll(list);
        adapterWind.notifyDataSetChanged();
        layout.scrollTo(0, 0);
    }

    private PackFltjZdDown.FltjZd getHead() {
        PackFltjZdDown.FltjZd bean = new PackFltjZdDown().new FltjZd();
        bean.county = "站点";
        bean.time = "日期/时段";
        bean.winddirection = "风向";
        bean.windFengLi = "风力";
        bean.windpower = "风速m/s";
        return bean;
    }

    /**
     * 请求时间数据
     */
    private void reqTimeTable() {
        packColumnUp.column_type = "8";
        PcsDataDownload.addDownload(packColumnUp);
    }

    /**
     * 请求当前实况风速
     */
    private void reqCurrentWindData() {
        packFltjProUp = new PackFltjProUp();
        packFltjProUp.type = "1";
        PcsDataDownload.addDownload(packFltjProUp);
    }

    /**
     * 请求小时极大风速数据
     */
    private void reqHourWindData(int index) {
        packFltjProUp = new PackFltjProUp();
        packFltjProUp.type = "2";
        packFltjProUp.flag = String.valueOf(index);
        PcsDataDownload.addDownload(packFltjProUp);
    }

    private RadioGroup.OnCheckedChangeListener onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
            switch (checkedId) {
                case R.id.btn_left:
                    clickLeft();
                    break;
                case R.id.btn_right:
                    clickRight();
                    break;
            }
        }
    };

//    private RadioGroup.OnCheckedChangeListener onTimeTableListener = new RadioGroup.OnCheckedChangeListener() {
//        @Override
//        public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
//            clickTimeTable(checkedId - 101);
//        }
//    };

    private View.OnClickListener onLRListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_left:
                    clickLeft();
                    break;
                case R.id.btn_right:
                    clickRight();
                    break;
            }
        }
    };

    private View.OnClickListener onTimeTableListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            clickTimeTable(v.getId()-101);
        }
    };

    private class MyReceiver extends PcsDataBrocastReceiver {

        @Override
        public void onReceive(String nameStr, String errorStr) {
            if (nameStr.equals(packColumnUp.getName())) {
                packColumnDown = (PackColumnDown) PcsDataManager.getInstance().getNetPack(packColumnUp.getName());
                if (packColumnDown == null) {
                    return;
                }
                initTimeTable(packColumnDown.arrcolumnInfo);
            } else if(nameStr.equals(packFltjProUp.getName())) {
                PackFltjProDown down = (PackFltjProDown) PcsDataManager.getInstance().getNetPack(packFltjProUp.getName());
                if(down == null) {
                    return;
                }
                updateCurrentWindData(down.datalist);
            }

        }
    }

}
