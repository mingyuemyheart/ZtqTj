package com.pcs.ztqtj.view.fragment.warning;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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

import com.pcs.lib.lib_pcs_v3.control.tool.Util;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.PackWarningCenterTfggsjDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.PackWarningCenterTfggsjUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.sh_warn.YjColumnGradeDown;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.adapter_warn.AdatperWeaRiskWarn;
import com.pcs.ztqtj.control.tool.SharedPreferencesUtil;
import com.pcs.ztqtj.view.fragment.FragmentWebView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tyaathome on 2016/6/14.
 */
public class FragmentWeatherRiskWarn extends Fragment {

    private ListView listview;
    private RadioGroup radioGroup;
    private MyReceiver receiver = new MyReceiver();
    private AdatperWeaRiskWarn adatper;
    private List<PackWarningCenterTfggsjDown.WarnTFGGSJ> datalist;
    private TextView textnull;
    private LinearLayout llFragmentContent = null;
    private ArrayList<YjColumnGradeDown> list_column;
    // 上传下载包
    private PackWarningCenterTfggsjUp packup;
    private PackWarningCenterTfggsjDown packdown;

    /** 第几个单选按钮被选择 */
    private int checkRadioPostion = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(getActivity()).inflate(R.layout.fragment_weather_risk_warn, null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initEvent();
        initData();
    }

    private void initView() {
        listview = (ListView) getView().findViewById(R.id.warnlist);
        radioGroup = (RadioGroup) getView().findViewById(R.id.radiogroup);
        textnull = (TextView) getView().findViewById(R.id.textnull);
        llFragmentContent = (LinearLayout) getView().findViewById(R.id.fragment);
    }

    private void initEvent() {
//        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                reqWarn(checkedId-100);
//            }
//        });
//        for(int i = 0; i < radioGroup.getChildCount(); i++) {
//            final int index = i;
//            RadioButton btn = (RadioButton) radioGroup.getChildAt(i);
//            btn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    checkRadioPostion = index;
//                    reqWarn(checkRadioPostion);
//                }
//            });
//        }

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String htmlpath;
                htmlpath = packdown.list.get(position).html_path;

                SharedPreferencesUtil.putData(htmlpath,htmlpath);

                // 隐藏列表和暂无数据，显示网页fragment
                listview.setVisibility(View.GONE);
                textnull.setVisibility(View.GONE);
                llFragmentContent.setVisibility(View.VISIBLE);

                Bundle bundle = new Bundle();
                // 传入完整url
                bundle.putString("url", getString(R.string.file_download_url) + htmlpath);
                FragmentWebView fragmentWebView = new FragmentWebView();
                fragmentWebView.setArguments(bundle);
                FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment, fragmentWebView);
                fragmentTransaction.commitAllowingStateLoss();
            }
        });
    }

    /**
     * 添加标题的单选按钮
     *
     */
    private void addRadioButton() {
        radioGroup.removeAllViews();
        int width = Util.getScreenWidth(getActivity());
        int _10dp = Util.dip2px(getActivity(), 10);
        int _35dp = Util.dip2px(getActivity(), 35);
        int radioWidth = (width /3);
        int pad = Util.dip2px(getActivity(), 10);
        // 这里修改过，省市县的显示顺序有变化
        for (int i = 0; i <list_column.size(); i++) {
            RadioButton radioButton = new RadioButton(getActivity());
            radioButton.setId(i);
            radioButton.setGravity(Gravity.CENTER);
            radioButton.setTextColor(getResources()
                    .getColor(R.color.text_black));
            radioButton
                    .setBackgroundResource(R.drawable.btn_disaster_reporting);
            radioButton.setPadding(pad, 0, pad, 0);
            radioButton.setButtonDrawable(getResources().getDrawable(
                    android.R.color.transparent));
            radioButton.setText(list_column.get(i).name);
            radioButton.setSingleLine(true);
            radioButton.setChecked(false);
            radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reqWarn(v.getId());
                }
            });
            RadioGroup.LayoutParams lp = new RadioGroup.LayoutParams(radioWidth, _35dp, 1.0f);
//            if(i != 1) {
                lp.rightMargin = _10dp;
//            }
            radioGroup.addView(radioButton, lp);
        }

        if (list_column.size() > 0) {
            // 默认选中的栏目序号
            int index = 0;
            if (index > list_column.size() - 1) {
                index = 0;
            }
            if (index==-1){
                index = 0;
            }
            RadioButton btn = (RadioButton) radioGroup.getChildAt(index);
            if(btn==null){
                return;
            }
            // 点击按钮
            btn.performClick();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(receiver != null) {
            PcsDataBrocastReceiver.unregisterReceiver(getActivity(), receiver);
            receiver = null;
        }
    }

    private void initData() {
        Bundle bundle = getArguments();
        list_column = bundle.getParcelableArrayList("list");
        addRadioButton();
//        if (list_column.size()!=0){
//            rb01.setText(list_column.get(0).name);
//            rb02.setText(list_column.get(1).name);
//            rb03.setText(list_column.get(2).name);
//        }
        PcsDataBrocastReceiver.registerReceiver(getActivity(),
                receiver);

        datalist = new ArrayList<>();
        adatper = new AdatperWeaRiskWarn(getActivity(), datalist);
        listview.setAdapter(adatper);
        reqWarn(0);
    }

    private void reqWarn(int position) {
//        datalist.clear();
//        adatper.notifyDataSetChanged();
        String type = list_column.get(position).type;
        packup = new PackWarningCenterTfggsjUp();
        packup.type = type;
        PcsDataDownload.addDownload(packup);
    }

    /**
     * 处理数据
     *
     */
    private void dealWithData() {
        if (packdown == null) {
            return;
        }
        datalist.clear();
        for (PackWarningCenterTfggsjDown.WarnTFGGSJ bean : packdown.list) {
            datalist.add(bean);
        }
        llFragmentContent.setVisibility(View.GONE);
        if (datalist.size() == 0) {
            listview.setVisibility(View.GONE);
            textnull.setVisibility(View.VISIBLE);
        } else {
            listview.setVisibility(View.VISIBLE);
            textnull.setVisibility(View.GONE);
        }
        adatper.notifyDataSetChanged();
    }


    private class MyReceiver extends PcsDataBrocastReceiver {
        @Override
        public void onReceive(String name, String errorStr) {
            if (name.equals(packup.getName())) {
                packdown = (PackWarningCenterTfggsjDown) PcsDataManager.getInstance().getNetPack(packup.getName());
                if(packdown==null){
                    return;
                }
                dealWithData();
            }
        }
    }
}
