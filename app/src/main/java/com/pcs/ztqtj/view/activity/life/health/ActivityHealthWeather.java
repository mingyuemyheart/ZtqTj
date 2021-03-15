package com.pcs.ztqtj.view.activity.life.health;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.health_life.AdapterHealLifeGrid;
import com.pcs.ztqtj.view.activity.FragmentActivityWithShare;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.net.healthyqx.PackHealthQxLmDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.healthyqx.PackHealthQxLmUp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenjx
 * on 2018/3/2.
 */

public class ActivityHealthWeather extends FragmentActivityWithShare {

    private MyReceiver receiver = new MyReceiver();

    private GridView gv_health_weather;
    private TextView tv_health_clara, tv_health_time;

    private AdapterHealLifeGrid adapterHealLifeGrid;
    private PackHealthQxLmDown down ;
    private List<PackHealthQxLmDown.HealthType> healthQxType = new ArrayList<>();
    private PackHealthQxLmUp packHealthQxLmUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_weather);
        init();
        initData();
        initEvent();
    }

    private void initEvent() {
        showProgressDialog();
        req();
    }

    private void req() {
        packHealthQxLmUp = new PackHealthQxLmUp();
        packHealthQxLmUp.column_type = "101";
        PcsDataDownload.addDownload(packHealthQxLmUp);

    }


    private void initData() {
        adapterHealLifeGrid = new AdapterHealLifeGrid(this, healthQxType, getImageFetcher());
        gv_health_weather.setAdapter(adapterHealLifeGrid);
        gv_health_weather.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(ActivityHealthWeather.this, ActivityHealthDown.class);
                intent.putExtra("img", down.healthQxType.get(i).img_url);
                intent.putExtra("type", down.healthQxType.get(i).type);
                intent.putExtra("name",down.healthQxType.get(i).name);
                startActivity(intent);
//                adapterHealLifeGrid.setSeclection(i);
//                adapterHealLifeGrid.notifyDataSetChanged();
            }
        });
    }

    private void init() {
        setTitleText("健康气象");
        gv_health_weather = (GridView) findViewById(R.id.gv_health_life);
        tv_health_time = (TextView) findViewById(R.id.tv_health_time);
        tv_health_clara = (TextView) findViewById(R.id.tv_health_clara);
    }

    @Override
    protected void onResume() {
        super.onResume();
        PcsDataBrocastReceiver.registerReceiver(ActivityHealthWeather.this, receiver);
    }

    @Override
    protected void onPause() {
        super.onPause();
        PcsDataBrocastReceiver.unregisterReceiver(ActivityHealthWeather.this, receiver);
    }

    private class MyReceiver extends PcsDataBrocastReceiver {

        @Override
        public void onReceive(String nameStr, String errorStr) {
            if (nameStr.equals(packHealthQxLmUp.getName())) {
                dismissProgressDialog();
                down = (PackHealthQxLmDown) PcsDataManager.getInstance().getNetPack(packHealthQxLmUp.getName());
                if (down == null) {
                    return;
                }
                healthQxType.clear();
                healthQxType.addAll(down.healthQxType);
                adapterHealLifeGrid.notifyDataSetChanged();
                tv_health_clara.setText(down.intro);
                tv_health_time.setText(down.time_pub);
            }
        }
    }
}
