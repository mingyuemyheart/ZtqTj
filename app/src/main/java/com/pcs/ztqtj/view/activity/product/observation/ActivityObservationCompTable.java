package com.pcs.ztqtj.view.activity.product.observation;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.RadioGroup;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.observation.AdapterObservationCompTable;
import com.pcs.ztqtj.control.tool.ShareTools;
import com.pcs.ztqtj.control.tool.ZtqImageTool;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackShareAboutDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.observation.ItemObservationCompTable;
import com.pcs.lib_ztqfj_v2.model.pack.net.observation.PackObservationCompTableDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.observation.PackObservationCompTableUp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Z on 2016/8/26.
 * <p/>
 * 对比表
 */
public class ActivityObservationCompTable extends FragmentActivityZtqBase {
    private ListView listview;
    private List<ItemObservationCompTable> listData;
    private AdapterObservationCompTable adatper;

    private RadioGroup radio_group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ovservation_comp_table);
        PcsDataBrocastReceiver.registerReceiver(this, mReceiver);
        initView();
        initEvent();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 注销广播
        PcsDataBrocastReceiver.unregisterReceiver(this, mReceiver);
    }

    private void initData() {
        setTitleText("下垫面对比表");
        listData = new ArrayList<>();
        adatper = new AdapterObservationCompTable(listData);
        listview.setAdapter(adatper);
        reqData();
    }

    private PackObservationCompTableUp packTableUp;

    private void reqData() {
        if(!isOpenNet()){
            showToast(getString(R.string.net_err));
            return ;
        }
        showProgressDialog();
        packTableUp = new PackObservationCompTableUp();
        packTableUp.station_no = "F0001";
        packTableUp.s_type = "0";
        PcsDataDownload.addDownload(packTableUp);
    }

    private void initEvent() {
        radio_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.highttemp:
                        if (obDown != null) {
                            listData.clear();
                            listData.addAll(obDown.listDataH);
                            adatper.notifyDataSetChanged();
                        }
                        break;
                    case R.id.lowtemp:
                        if (obDown != null) {
                            listData.clear();
                            listData.addAll(obDown.listDataL);
                            adatper.notifyDataSetChanged();
                        }
                        break;
                }
            }
        });
    }

    private void initView() {
        radio_group = (RadioGroup) findViewById(R.id.radio_group);

        listview = (ListView) findViewById(R.id.listview);

        setBtnRight2(R.drawable.btn_refresh, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reqData();
            }
        });

        setBtnRight(R.drawable.icon_share_new, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View layout = findViewById(R.id.layout);
                PackShareAboutDown down = (PackShareAboutDown) PcsDataManager.getInstance().getNetPack("wt_share#ABOUT_QXCP_DXFW");
                if(down != null) {
                    Bitmap bm = ZtqImageTool.getInstance().getScreenBitmapNew(ActivityObservationCompTable.this);
                    ShareTools.getInstance(ActivityObservationCompTable.this).setShareContent(getTitleText(),down.share_content, bm,"0").showWindow(layout);
                }
            }
        });

    }

    private PackObservationCompTableDown obDown;
    /**
     * 数据广播
     */
    private PcsDataBrocastReceiver mReceiver = new PcsDataBrocastReceiver() {
        @Override
        public void onReceive(String nameStr, String errorStr) {
            if (packTableUp != null && nameStr.equals(packTableUp.getName())) {
                dismissProgressDialog();
                obDown = (PackObservationCompTableDown) PcsDataManager.getInstance().getNetPack(nameStr);
                if (obDown != null) {
                    listData.clear();
                    switch (radio_group.getCheckedRadioButtonId()) {
                        case R.id.highttemp:
                            listData.addAll(obDown.listDataH);
                            adatper.notifyDataSetChanged();
                            break;
                        case R.id.lowtemp:
                            listData.addAll(obDown.listDataL);
                            adatper.notifyDataSetChanged();
                            break;
                    }
                }
            }
        }
    };


}
