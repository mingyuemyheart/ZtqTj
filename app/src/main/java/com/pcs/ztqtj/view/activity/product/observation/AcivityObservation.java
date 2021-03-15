package com.pcs.ztqtj.view.activity.product.observation;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.observation.AdapterObservation;
import com.pcs.ztqtj.control.adapter.observation.ObservaItem;
import com.pcs.ztqtj.control.tool.ShareTools;
import com.pcs.ztqtj.control.tool.ZtqImageTool;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
import com.pcs.ztqtj.view.myview.ObservationView;
import com.pcs.ztqtj.view.myview.observation.ObData;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackShareAboutDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.observation.PackObservationViewDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.observation.PackObservationViewUp;

import java.util.ArrayList;
import java.util.List;

/**
 * 下垫面观测
 */
public class AcivityObservation extends FragmentActivityZtqBase implements OnClickListener {

    private ObservationView ob_view;
    private GridView grid_view;
    private AdapterObservation adapterObservation;
    private ImageView btn_table;

    private RadioGroup radio_group_temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_observation);
        // 注册广播
        PcsDataBrocastReceiver.registerReceiver(this, mReceiver);
        initView();
        initEvent();
        initData();
    }


    private void initEvent() {
        btn_table.setOnClickListener(this);
        grid_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ObservaItem itemViolt = (ObservaItem) adapterObservation.getItem(position);
                Intent intent = new Intent(AcivityObservation.this, ActivityObservationTable.class);
                intent.putExtra("type", itemViolt.type);
                intent.putExtra("title", itemViolt.itemInfo);
                startActivity(intent);
            }
        });

        radio_group_temp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                reflushIsHight();

            }
        });

    }

    /**
     * 初始化控件
     */
    private void initView() {
        ob_view = (ObservationView) findViewById(R.id.ob_view);
        grid_view = (GridView) findViewById(R.id.grid_view);
        btn_table = (ImageView) findViewById(R.id.btn_table);
        radio_group_temp = (RadioGroup) findViewById(R.id.radio_group_temp);

        setBtnRight(R.drawable.icon_share_new, new OnClickListener() {
            @Override
            public void onClick(View v) {
                View layout = findViewById(R.id.layout);
                PackShareAboutDown down = (PackShareAboutDown) PcsDataManager.getInstance().getNetPack("wt_share#ABOUT_QXCP_DXFW");
                if(down != null) {
                    Bitmap bm = ZtqImageTool.getInstance().getScreenBitmapNew(AcivityObservation.this);
                    bm = ZtqImageTool.getInstance().stitchQR(AcivityObservation.this, bm);
                    ShareTools.getInstance(AcivityObservation.this).setShareContent(getTitleText(), down.share_content, bm,"0").showWindow(layout);
                }
            }
        });

        setBtnRight2(R.drawable.btn_refresh, new OnClickListener() {
            @Override
            public void onClick(View v) {
                reqData();
            }
        });


    }

    private List<ObservaItem> listData;

    private void initData() {
        setTitleText("下垫面观测");
        listData = new ArrayList<>();
        setItemValue();
        adapterObservation = new AdapterObservation(listData);
        grid_view.setAdapter(adapterObservation);
        reqData();
    }

    private PackObservationViewUp obUp;
    private PackObservationViewDown obDown;

    private void reqData() {
        if(!isOpenNet()){
            showToast(getString(R.string.net_err));
            return ;
        }
        showProgressDialog();
        obUp = new PackObservationViewUp();
        obUp.station_no = "F0001";
        PcsDataDownload.addDownload(obUp);
    }

    private void setItemValue() {

        ObservaItem itemDeepOrange = new ObservaItem();
        itemDeepOrange.imageResourse = R.drawable.icon_obser_deep_orange;
        itemDeepOrange.itemInfo = "泥土";
        itemDeepOrange.type = "1";
        listData.add(itemDeepOrange);


        ObservaItem itemGray = new ObservaItem();
        itemGray.imageResourse = R.drawable.icon_obser_gray;
        itemGray.itemInfo = "水泥";
        itemGray.type = "2";
        listData.add(itemGray);


        ObservaItem itemBlue = new ObservaItem();
        itemBlue.imageResourse = R.drawable.icon_obser_blue;
        itemBlue.itemInfo = "柏油";
        itemBlue.type = "3";
        listData.add(itemBlue);

        ObservaItem itemViolt = new ObservaItem();
        itemViolt.imageResourse = R.drawable.icon_obser_violt;
        itemViolt.itemInfo = "地砖";
        itemViolt.type = "4";
        listData.add(itemViolt);


        ObservaItem itemOrange = new ObservaItem();
        itemOrange.imageResourse = R.drawable.icon_obser_orange;
        itemOrange.itemInfo = "砂砾石";
        itemOrange.type = "5";
        listData.add(itemOrange);


        ObservaItem itemGreen = new ObservaItem();
        itemGreen.imageResourse = R.drawable.icon_obser_green;
        itemGreen.itemInfo = "空气裸温";
        itemGreen.type = "6";
        listData.add(itemGreen);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 注销广播
        PcsDataBrocastReceiver.unregisterReceiver(this, mReceiver);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_table:
                Intent intent = new Intent(this, ActivityObservationCompTable.class);
                startActivity(intent);
                break;
        }
    }


    private List<ObData> timeValue = new ArrayList<>();

    /**
     * 数据广播
     */
    private PcsDataBrocastReceiver mReceiver = new PcsDataBrocastReceiver() {
        @Override
        public void onReceive(String nameStr, String errorStr) {
            if (obUp != null && nameStr.equals(obUp.getName())) {
                dismissProgressDialog();
                obDown = (PackObservationViewDown) PcsDataManager.getInstance().getNetPack(nameStr);
                reflushIsHight();
            }
        }
    };

    private void reflushIsHight() {
        boolean isHight = true;
        if (obDown == null) {
            return;
        }
        switch (radio_group_temp.getCheckedRadioButtonId()) {
            case R.id.highttemp:
                isHight = true;
                break;
            case R.id.lowtemp:
                isHight = false;
                break;
        }
        timeValue.clear();
        if(obDown.listData == null || obDown.listData.size() == 0) {
            showToast("无数据！");
            return;
        }
        for (int i = 0; i < obDown.listData.get(0).detailListData.size(); i++) {
            ObData obItem = new ObData();
            obItem.xValue = obDown.listData.get(0).detailListData.get(i).hour_time;
            obItem.xDay = obDown.listData.get(0).detailListData.get(i).day;
            for (int j = 0; j < obDown.listData.size(); j++) {
//                        1=泥土; 2=水泥; 3=柏油;4=地砖; 5=砂砾; 6=空气裸温
                try {
                    if (isHight) {
                        if (obDown.listData.get(j).type.equals("1")) {
                            obItem.greenNew = Float.parseFloat(obDown.listData.get(j).detailListData.get(i).h_tem);
                        } else if (obDown.listData.get(j).type.equals("2")) {
                            obItem.orangeNew = Float.parseFloat(obDown.listData.get(j).detailListData.get(i).h_tem);
                        } else if (obDown.listData.get(j).type.equals("3")) {
                            obItem.redNew = Float.parseFloat(obDown.listData.get(j).detailListData.get(i).h_tem);
                        } else if (obDown.listData.get(j).type.equals("4")) {
                            obItem.voiletNew = Float.parseFloat(obDown.listData.get(j).detailListData.get(i).h_tem);
                        } else if (obDown.listData.get(j).type.equals("5")) {
                            obItem.yellowNew = Float.parseFloat(obDown.listData.get(j).detailListData.get(i).h_tem);
                        } else if (obDown.listData.get(j).type.equals("6")) {
                            obItem.blueNew = Float.parseFloat(obDown.listData.get(j).detailListData.get(i).h_tem);
                        }
                    } else {
                        if (obDown.listData.get(j).type.equals("1")) {
                            obItem.greenNew = Float.parseFloat(obDown.listData.get(j).detailListData.get(i).l_tem);
                        } else if (obDown.listData.get(j).type.equals("2")) {
                            obItem.orangeNew = Float.parseFloat(obDown.listData.get(j).detailListData.get(i).l_tem);
                        } else if (obDown.listData.get(j).type.equals("3")) {
                            obItem.redNew = Float.parseFloat(obDown.listData.get(j).detailListData.get(i).l_tem);
                        } else if (obDown.listData.get(j).type.equals("4")) {
                            obItem.voiletNew = Float.parseFloat(obDown.listData.get(j).detailListData.get(i).l_tem);
                        } else if (obDown.listData.get(j).type.equals("5")) {
                            obItem.yellowNew = Float.parseFloat(obDown.listData.get(j).detailListData.get(i).l_tem);
                        } else if (obDown.listData.get(j).type.equals("6")) {
                            obItem.blueNew = Float.parseFloat(obDown.listData.get(j).detailListData.get(i).l_tem);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            timeValue.add(obItem);
        }
        if (isHight) {
            ob_view.setValue(timeValue, "°C", obDown.maxValueH, obDown.minValueH);
        } else {
            ob_view.setValue(timeValue, "°C", obDown.maxValueL, obDown.minValueL);
        }
    }
}