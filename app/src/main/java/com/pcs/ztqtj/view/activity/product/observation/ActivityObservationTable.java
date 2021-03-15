package com.pcs.ztqtj.view.activity.product.observation;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.observation.AdapterObservationTable;
import com.pcs.ztqtj.control.tool.ShareTools;
import com.pcs.ztqtj.control.tool.ZtqImageTool;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackShareAboutDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.observation.ItemObservationTable;
import com.pcs.lib_ztqfj_v2.model.pack.net.observation.PackObservationTableDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.observation.PackObservationTableUp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Z on 2016/8/26.
 * 观测数据
 */
public class ActivityObservationTable extends FragmentActivityZtqBase {
    private ListView listview;

    private AdapterObservationTable adatper;

    List<ItemObservationTable> listData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ovservation_table);
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

    private String type = "1";

    private void initData() {
        type = getIntent().getStringExtra("type");
        String title = getIntent().getStringExtra("title");
        setTitleText(title + "下垫面观测");
        listData = new ArrayList<>();
        adatper = new AdapterObservationTable(listData);
        listview.setAdapter(adatper);
        reqData();
    }

    private PackObservationTableUp packTableUp;

    private void reqData() {
        if(!isOpenNet()){
            showToast(getString(R.string.net_err));
            return ;
        }
        showProgressDialog();
        packTableUp = new PackObservationTableUp();
        packTableUp.station_no = "F0001";
        packTableUp.type = type;
        PcsDataDownload.addDownload(packTableUp);

    }

    private void initEvent() {

    }

    private void initView() {
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
                    Bitmap bm = ZtqImageTool.getInstance().getScreenBitmapNew(ActivityObservationTable.this);
                    bm = ZtqImageTool.getInstance().stitchQR(ActivityObservationTable.this, bm);
                    ShareTools.getInstance(ActivityObservationTable.this).setShareContent(getTitleText(),down.share_content, bm,"0").showWindow(layout);
                }
            }
        });
    }

    /**
     * 数据广播
     */
    private PcsDataBrocastReceiver mReceiver = new PcsDataBrocastReceiver() {
        @Override
        public void onReceive(String nameStr, String errorStr) {
            if (packTableUp != null && nameStr.equals(packTableUp.getName())) {
                dismissProgressDialog();
                PackObservationTableDown obDown = (PackObservationTableDown) PcsDataManager.getInstance().getNetPack(nameStr);
                if (obDown != null) {
                    listData.addAll(obDown.listData);
                    adatper.notifyDataSetChanged();
                }
            }
        }
    };

}
