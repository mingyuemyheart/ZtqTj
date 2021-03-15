package com.pcs.ztqtj.view.activity.life.health;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.health_life.AdapterHealLife;
import com.pcs.ztqtj.view.activity.FragmentActivityWithShare;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib.lib_pcs_v3.model.image.ImageConstant;
import com.pcs.lib_ztqfj_v2.model.pack.net.healthyqx.PackHealthQxDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.healthyqx.PackHealthQxUp;

import java.util.ArrayList;

/**
 * Created by chenjx
 * on 2018/4/17.
 */

public class ActivityHealthDown extends FragmentActivityWithShare{
    private AdapterHealLife adapterHealLife;
    private ListView lv_health_weather;
    private ArrayList<PackHealthQxDown.HealthQx> list=new ArrayList<>();
    private PackHealthQxUp packHealthQxUp =new PackHealthQxUp();
    private MyReceiver receiver = new MyReceiver();
    private String img_url,type,path,name;
    private ImageView iv_health_t;
    private TextView tv_health_type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_down);
        initView();
        initData();
    }

    private void initData() {
        Intent intent=getIntent();
        img_url=intent.getStringExtra("img");
        type=intent.getStringExtra("type");
        name=intent.getStringExtra("name");

        setTitleText(name.replaceAll("[\\t\\n\\r]", ""));
        tv_health_type.setText(name.replaceAll("[\\t\\n\\r]", ""));
        adapterHealLife=new AdapterHealLife(this,list);
        lv_health_weather.setAdapter(adapterHealLife);

        if (img_url!=null){
            path = getString(R.string.file_download_url) + img_url;
            getImageFetcher().loadImage(path, iv_health_t, ImageConstant
                    .ImageShowType.SRC);
        }

        req_type(type);
    }

    private void initView() {
        iv_health_t= (ImageView) findViewById(R.id.iv_health_t);
        lv_health_weather = (ListView) findViewById(R.id.lv_health_weather);
        tv_health_type= (TextView) findViewById(R.id.tv_health_type);
    }

    private void req_type(String type){
        showProgressDialog();
        packHealthQxUp.d_type = type;
        PcsDataDownload.addDownload(packHealthQxUp);
    }
    @Override
    protected void onResume() {
        super.onResume();
        PcsDataBrocastReceiver.registerReceiver(ActivityHealthDown.this, receiver);
    }

    @Override
    protected void onPause() {
        super.onPause();
        PcsDataBrocastReceiver.unregisterReceiver(ActivityHealthDown.this, receiver);
    }


    private class MyReceiver extends PcsDataBrocastReceiver {

        @Override
        public void onReceive(String nameStr, String errorStr) {
           if (nameStr.equals(packHealthQxUp.getName())) {
                dismissProgressDialog();
                PackHealthQxDown down = (PackHealthQxDown) PcsDataManager.getInstance().getNetPack(packHealthQxUp.getName());
                if (down == null) {
                    return;
                }
                list.clear();
                list.addAll(down.healthQxList);
                adapterHealLife.notifyDataSetChanged();
            }
        }
    }
}
