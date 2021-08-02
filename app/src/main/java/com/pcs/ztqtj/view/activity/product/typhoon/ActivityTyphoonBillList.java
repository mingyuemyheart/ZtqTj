package com.pcs.ztqtj.view.activity.product.typhoon;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.typhoon.AdapterTyphoonBill;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
import com.pcs.ztqtj.view.activity.web.WebViewWithShare;
import com.pcs.lib.lib_pcs_v3.control.tool.Util;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.net.column.ColumnInfo;
import com.pcs.lib_ztqfj_v2.model.pack.net.column.PackColumnDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.column.PackColumnUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.PackWarningCenterTfggsjDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.PackWarningCenterTfggsjUp;

import java.util.ArrayList;
import java.util.List;

/**
 * 台风路径-警报单
 */
public class ActivityTyphoonBillList extends FragmentActivityZtqBase {

    private RadioGroup radioGroup;
    private ListView listView = null;
    // 上传下载包
    PackWarningCenterTfggsjUp packUp = new PackWarningCenterTfggsjUp();
    PackWarningCenterTfggsjDown packDown = new PackWarningCenterTfggsjDown();
    PackColumnUp packColumnUp = new PackColumnUp();
    // 广播
    private MyReceiver receiver = new MyReceiver();
    // 数据
    private List<PackWarningCenterTfggsjDown.WarnTFGGSJ> datalist = new ArrayList<>();

    // 适配器
    private AdapterTyphoonBill adapter = null;
    private List<ColumnInfo> columnInfoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_typhoon_bill_list);
        setTitleText(R.string.typhoon_bill_list);
        initView();
        initEvent();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            PcsDataBrocastReceiver.unregisterReceiver(this, receiver);
            receiver = null;
        }
    }

    private void initView() {
        radioGroup = (RadioGroup) findViewById(R.id.radiogroup);
        listView = (ListView) findViewById(R.id.warnlist);
    }

    private void initEvent() {
        radioGroup.setOnCheckedChangeListener(onCheckedChangeListener);
        listView.setOnItemClickListener(onItemClickListener);
    }

    private void initData() {
        PcsDataBrocastReceiver.registerReceiver(this, receiver);
        adapter = new AdapterTyphoonBill(this, datalist);
        listView.setAdapter(adapter);
        reqColumn();
    }

    private void initTable(List<ColumnInfo> list) {
        columnInfoList = list;
        int width = Util.getScreenWidth(this);
        int radioWidth = width / list.size();
        int pad = Util.dip2px(this, 6);
        for(int i = 0; i < list.size(); i++) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setId(101 + i);
            radioButton.setTextColor(getResources()
                    .getColor(R.color.text_black));
            radioButton
                    .setBackgroundResource(R.drawable.btn_warn_radiobutton_select);
            radioButton.setPadding(0, pad, 0, pad);
            radioButton.setMaxLines(1);
            radioButton.setGravity(Gravity.CENTER);
            radioButton.setTextSize(18);
            radioButton.setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));
            radioButton.setText(list.get(i).name);

            //radioButton.setOnClickListener(onTimeTableListener);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(radioWidth, ViewGroup.LayoutParams
                    .MATCH_PARENT);
            radioGroup.addView(radioButton, lp);
        }
        if(list.size() > 0 && radioGroup.getChildCount() > 0) {
            radioGroup.check(radioGroup.getChildAt(0).getId());
        }
    }

    // 处理数据
    private void dealwithData(PackWarningCenterTfggsjDown down) {
        datalist.clear();
        datalist.addAll(down.list);
        adapter.notifyDataSetChanged();
    }

    /**
     * 清空警报单列表
     */
    private void clearList() {
        datalist.clear();
        adapter.notifyDataSetChanged();
    }

    private void clickButton(int index) {
        if(columnInfoList.size() > index) {
            reqNet(columnInfoList.get(index).type);
        }
    }

    private void gotoWebview(String url) {
        Intent intent = new Intent(this, WebViewWithShare.class);
        intent.putExtra("title", "台风警报单");
        intent.putExtra("url", url);
        startActivity(intent);
    }

    // 请求数据
    private void reqNet(String type) {
        clearList();
        packUp = new PackWarningCenterTfggsjUp();
        packUp.type = type;
        PcsDataDownload.addDownload(packUp);
    }

    /**
     * 请求栏目
     */
    private void reqColumn() {
        packColumnUp = new PackColumnUp();
        packColumnUp.column_type = "22";
        PcsDataDownload.addDownload(packColumnUp);
    }

    private RadioGroup.OnCheckedChangeListener onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
            clickButton(checkedId-101);
        }
    };

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            gotoWebview("http://www.weather-jy.cn:8099/ftp/" + datalist.get(position).html_path);
        }
    };

    private View.OnClickListener onTimeTableListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            clickButton(v.getId()-101);
        }
    };

    private class MyReceiver extends PcsDataBrocastReceiver {

        @Override
        public void onReceive(String nameStr, String errorStr) {
            if (nameStr.equals(packUp.getName())) {
                packDown = (PackWarningCenterTfggsjDown) PcsDataManager.getInstance().getNetPack(nameStr);
                if (packDown == null) {
                    return;
                }
                dealwithData(packDown);
            } else if(nameStr.equals(packColumnUp.getName())) {
                PackColumnDown down = (PackColumnDown) PcsDataManager.getInstance().getNetPack(nameStr);
                if(down == null) {
                    return;
                }
                initTable(down.arrcolumnInfo);
            }
        }
    }

}
