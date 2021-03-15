package com.pcs.ztqtj.view.activity.product.agriculture;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.product_numerical.AdapterColumn;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.net.agriculture.AgricultureInfo;
import com.pcs.lib_ztqfj_v2.model.pack.net.agriculture.PackAgricultureDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.agriculture.PackAgricultureUp;

import java.util.ArrayList;
import java.util.List;

/**
 * 农业气象栏目
 * Created by tyaathome on 2016/11/8.
 */
public class ActivityAgricultureWeatherColumn extends FragmentActivityZtqBase {

    private GridView gridView;
    private AdapterColumn adapter;
    private MyReceiver receiver = new MyReceiver();

    private List<AgricultureInfo> columnInfoList = new ArrayList<>();

    private PackAgricultureUp packAgricultureUp = new PackAgricultureUp();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitleText(R.string.agriculture_weather);
        setContentView(R.layout.activity_agriculture_weather_column);
        initView();
        initEvent();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(receiver != null) {
            PcsDataBrocastReceiver.unregisterReceiver(this, receiver);
            receiver = null;
        }
    }

    private void initView() {
        gridView = (GridView) findViewById(R.id.gridview);
    }

    private void initEvent() {
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String type = columnInfoList.get(position).type;
                if(type.equals("1")) {
                    gotoZQZB(columnInfoList.get(position));
                } else {
                    gotoDetail(columnInfoList.get(position));
                }
            }
        });
    }

    private void initData() {
        receiver = new MyReceiver();
        PcsDataBrocastReceiver.registerReceiver(this, receiver);

//        AgricultureInfo agricultureInfo = new AgricultureInfo();
//        agricultureInfo.title = "农业灾情直报";
//        columnInfoList.add(agricultureInfo);

        adapter = new AdapterColumn(this, columnInfoList, getImageFetcher());
        gridView.setAdapter(adapter);

        req();
    }

    /**
     * 跳转灾情直报栏目
     */
    private void gotoZQZB(AgricultureInfo info) {
        String channel_id = info.channel_id;
        Intent intent = new Intent(this, ActivityAgricultureZQZB.class);
        intent.putExtra("channel_id", channel_id);
        startActivity(intent);
    }

    /**
     * 跳转详情页
     * @param info
     */
    private void gotoDetail(AgricultureInfo info) {
        String channel_id = info.channel_id;
        Intent intent = new Intent(this, ActivityAgricultureWeather.class);
        intent.putExtra("title", info.title);
        intent.putExtra("channel_id", channel_id);
        startActivity(intent);
    }

    /**
     * 请求列表
     */
    private void req() {
        packAgricultureUp = new PackAgricultureUp();
        packAgricultureUp.type = "6";
        PcsDataDownload.addDownload(packAgricultureUp);
    }

    private class MyReceiver extends PcsDataBrocastReceiver {

        @Override
        public void onReceive(String nameStr, String errorStr) {
            if(!TextUtils.isEmpty(errorStr)) {
                return ;
            }

            if(nameStr.equals(packAgricultureUp.getName())) {
                PackAgricultureDown down = (PackAgricultureDown) PcsDataManager.getInstance().getNetPack(nameStr);
                if(down == null) {
                    return ;
                }

                columnInfoList.clear();
                columnInfoList.addAll(down.info_list);
                adapter.notifyDataSetChanged();
            }

        }
    }
}
