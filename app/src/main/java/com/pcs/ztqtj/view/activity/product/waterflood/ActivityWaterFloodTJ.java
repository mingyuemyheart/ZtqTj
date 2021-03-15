package com.pcs.ztqtj.view.activity.product.waterflood;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib.lib_pcs_v3.model.pack.PcsPackDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackShareAboutDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackShareAboutUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.floodsummary.PackRavrListDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.floodsummary.PackRavrListUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.floodsummary.PackReverListUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.floodsummary.PackRiverListDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.floodsummary.Ravr;
import com.pcs.lib_ztqfj_v2.model.pack.net.floodsummary.River;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.CommUtils;
import com.pcs.ztqtj.control.tool.NetTask;
import com.pcs.ztqtj.control.tool.ShareTools;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tyaathome on 2019/06/14.
 */
public class ActivityWaterFloodTJ extends FragmentActivityZtqBase implements View.OnClickListener {

    private MapView mapView;
    private AMap aMap;
    private RadioGroup radioGroup;
    private RadioButton rbRiver, rbRavr;
    private List<Marker> riverMarkerList = new ArrayList<>();
    private List<Marker> ravrMarkerList = new ArrayList<>();
    private List<Marker> riverTipMakrerList = new ArrayList<>();
    private List<Marker> ravrTipMakrerList = new ArrayList<>();
    private Dialog dialog;
    private CheckBox cb;
    private boolean currentShowing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_flood_tj);
        setTitleText("水利汛情");
        initView();
        initMap(savedInstanceState);
        initData();
    }

    private void initView() {
        headLayout.setVisibility(View.GONE);
        radioGroup = findViewById(R.id.radiogroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_river:
                        requestRiver();
                        break;
                    case R.id.rb_ravr:
                        requestRavr();
                        break;
                }
            }
        });
        rbRiver = findViewById(R.id.rb_river);
        rbRavr = findViewById(R.id.rb_ravr);
        findViewById(R.id.btn_exit).setOnClickListener(this);
        findViewById(R.id.btn_share).setOnClickListener(this);

        cb = findViewById(R.id.cb);
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                currentShowing = isChecked;
                updateShowTip();
            }
        });
        cb.toggle();
    }

    private void initMap(Bundle savedInstanceState) {
        mapView = findViewById(R.id.mapview);
        aMap = mapView.getMap();
        mapView.onCreate(savedInstanceState);
        aMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                showDialog(marker.getObject());
                return true;
            }
        });
    }

    private void initData() {
        rbRavr.toggle();
    }

    private void updateShowTip() {
        if(currentShowing) {
            cb.setText("隐藏");
        } else {
            cb.setText("显示");
        }
        if(rbRiver.isChecked()) {
            showAllRiverMakrer(currentShowing);
        }
        if(rbRavr.isChecked()) {
            showAllRavrMakrer(currentShowing);
        }
    }

    private void showDialog(Object object) {
        if(dialog == null) {
            dialog = new Dialog(this, R.style.MyDialog);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.dialog_water_floor_info);
        }
        dismissDialog();
        TextView tv1, tv2, tv3, tv4, tv5, tv6, tv7, tvTitle;
        tvTitle = dialog.findViewById(R.id.tv_title);
        tv1 = dialog.findViewById(R.id.tv1);
        tv2 = dialog.findViewById(R.id.tv2);
        tv3 = dialog.findViewById(R.id.tv3);
        tv4 = dialog.findViewById(R.id.tv4);
        tv5 = dialog.findViewById(R.id.tv5);
        tv6 = dialog.findViewById(R.id.tv6);
        tv7 = dialog.findViewById(R.id.tv7);
        dialog.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog();
            }
        });

        if(object instanceof River) { // 河道站
            tvTitle.setText("河道站查询");
            River river = (River) object;
            tv1.setText("测站：" + river.stnm);
            tv2.setText(river.tm);
            tv3.setText("河流名称：" + river.rvnm);
            tv4.setText("水系名称：" + river.hnnm);
            tv5.setText("流域名称：" + river.bsnm);
            tv6.setText("水位：" + river.z + "m");
            tv7.setText("流量：" + river.q + "m³/s");
        } else if (object instanceof Ravr) {
            tvTitle.setText("水库站查询");
            Ravr ravr = (Ravr) object;
            tv1.setText("测站：" + ravr.stnm);
            tv2.setText(ravr.tm);
            tv3.setText("河流名称：" + ravr.rvnm);
            tv4.setText("水系名称：" + ravr.hnnm);
            tv5.setText("流域名称：" + ravr.bsnm);
            tv6.setText("库上水位：" + ravr.rz + "m");
            tv7.setText("入库流量：" + ravr.inq + "m³/s");
        }
        dialog.show();
    }

    private void dismissDialog() {
        if(dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    private void addAllRiverMarker(List<River> riverList) {
        if(aMap == null || riverList == null) return;
        List<River> list = new ArrayList<>(riverList);
        LatLngBounds.Builder builder = LatLngBounds.builder();
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(resizeIcon(R.drawable.icon_water_flood_point));
        for(River river : list) {
            if(river.lttd == 0.0d || river.lgtd == 0.0d) continue;
            LatLng latLng = new LatLng(river.lttd, river.lgtd);
            MarkerOptions markerOptions = new MarkerOptions()
                    .icon(bitmapDescriptor)
                    .position(latLng)
                    .anchor(0.5f, 0f)
                    .zIndex(1);
            Marker marker = aMap.addMarker(markerOptions);
            marker.setObject(river);
            riverMarkerList.add(marker);
            builder.include(latLng);
        }
        aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100));
    }

    private void addAllRavrMarker(List<Ravr> ravrList) {
        if(aMap == null || ravrList == null) return;
        List<Ravr> list = new ArrayList<>(ravrList);
        LatLngBounds.Builder builder = LatLngBounds.builder();
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(resizeIcon(R.drawable.icon_water_flood_point));
        for(Ravr ravr : list) {
            LatLng latLng = new LatLng(ravr.lttd, ravr.lgtd);
            if(latLng.latitude == 0.0d || latLng.longitude == 0.0d) continue;
            MarkerOptions markerOptions = new MarkerOptions()
                    .icon(bitmapDescriptor)
                    .position(latLng)
                    .anchor(0.5f, 0f)
                    .zIndex(1);
            Marker marker = aMap.addMarker(markerOptions);
            marker.setObject(ravr);
            ravrMarkerList.add(marker);
            builder.include(latLng);
        }
        aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100));
    }

    private void showAllRavrMakrer(boolean isShow) {
        if (ravrMarkerList != null && ravrMarkerList.size() > 0) {
            if(ravrTipMakrerList == null || ravrTipMakrerList.size() == 0) {
                if(isShow) {
                    ravrTipMakrerList = new ArrayList<>();
                    for (Marker marker : ravrMarkerList) {
                        if (marker.getObject() == null || !(marker.getObject() instanceof Ravr)) continue;
                        Ravr ravr = (Ravr) marker.getObject();
                        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(toBitmap(ravr.toString()));
                        LatLng latLng = new LatLng(ravr.lttd, ravr.lgtd);
                        if (latLng.latitude == 0.0d || latLng.longitude == 0.0d) continue;
                        MarkerOptions markerOptions = new MarkerOptions()
                                .icon(bitmapDescriptor)
                                .position(latLng)
                                .anchor(0.5f, 1f)
                                .zIndex(1);
                        Marker m = aMap.addMarker(markerOptions);
                        m.setObject(ravr);
                        ravrTipMakrerList.add(m);
                    }
                }
            } else {
                for(Marker makrer : ravrTipMakrerList) {
                    makrer.setVisible(isShow);
                }
            }
        }
    }

    private void showAllRiverMakrer(boolean isShow) {
        if (riverMarkerList != null && riverMarkerList.size() > 0) {
            if(riverTipMakrerList == null || riverTipMakrerList.size() == 0) {
                if(isShow) {
                    riverTipMakrerList = new ArrayList<>();
                    for (Marker marker : riverMarkerList) {
                        if (marker.getObject() == null || !(marker.getObject() instanceof River)) continue;
                        River river = (River) marker.getObject();
                        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(toBitmap(river.toString()));
                        LatLng latLng = new LatLng(river.lttd, river.lgtd);
                        if (latLng.latitude == 0.0d || latLng.longitude == 0.0d) continue;
                        MarkerOptions markerOptions = new MarkerOptions()
                                .icon(bitmapDescriptor)
                                .position(latLng)
                                .anchor(0.5f, 1f)
                                .zIndex(1);
                        Marker m = aMap.addMarker(markerOptions);
                        m.setObject(river);
                        riverTipMakrerList.add(m);
                    }
                }
            } else {
                for(Marker makrer : riverTipMakrerList) {
                    makrer.setVisible(isShow);
                }
            }
        }
    }

    private Bitmap resizeIcon(int resid) {
        Bitmap icon = BitmapFactory.decodeResource(getResources(), resid);
        int width = CommUtils.dp2px(20);
        int height = CommUtils.dp2px(20);
        return Bitmap.createScaledBitmap(icon, width, height, false);
    }

    private Bitmap toBitmap(String value) {
        TextView textView = (TextView) getLayoutInflater().inflate(R.layout.flood_water_marker, null);
        textView.setText(value);
        return BitmapDescriptorFactory.fromView(textView).getBitmap();
    }

    private void clearAllMarker() {
        if(riverMarkerList != null) {
            for(Marker marker : riverMarkerList) {
                if(!marker.isRemoved()) {
                    marker.remove();
                }
            }
            riverMarkerList.clear();
        }
        if(ravrMarkerList != null) {
            for(Marker marker : ravrMarkerList) {
                if(!marker.isRemoved()) {
                    marker.remove();
                }
            }
            ravrMarkerList.clear();
        }
        if(riverTipMakrerList != null) {
            for(Marker marker : riverTipMakrerList) {
                if(!marker.isRemoved()) {
                    marker.remove();
                }
            }
            riverTipMakrerList.clear();
        }
        if(ravrTipMakrerList != null) {
            for(Marker marker : ravrTipMakrerList) {
                if(!marker.isRemoved()) {
                    marker.remove();
                }
            }
            ravrTipMakrerList.clear();
        }
    }

    private void share() {
        if(aMap != null) {
            aMap.getMapScreenShot(new AMap.OnMapScreenShotListener() {
                @Override
                public void onMapScreenShot(Bitmap bitmap) {
                    if(bitmap == null) return;
                    View layout = findViewById(R.id.layout);
                    PackShareAboutDown shareDown = (PackShareAboutDown) PcsDataManager.getInstance().getNetPack(PackShareAboutUp
                            .getNameCom());

                    String shareContnet;
                    if (shareDown != null) {
                        shareContnet = shareDown.share_content;
                    } else {
                        shareContnet = "天津气象分享";
                    }
                    ShareTools.getInstance(ActivityWaterFloodTJ.this).setShareContent(getTitleText(),shareContnet, bitmap, "0").showWindow(layout);
                }

                @Override
                public void onMapScreenShot(Bitmap bitmap, int i) {

                }
            });
        }

    }

    private void requestRiver() {
        clearAllMarker();
        PackReverListUp up = new PackReverListUp();
        NetTask task = new NetTask(this, new NetTask.NetListener() {
            @Override
            public void onComplete(PcsPackDown down) {
                if(down instanceof PackRiverListDown) {
                    PackRiverListDown riverListDown = (PackRiverListDown) down;
                    if(riverListDown.riverList != null) {
                        addAllRiverMarker(riverListDown.riverList);
                        updateShowTip();
                    }
                }
            }
        });
        task.execute(up);
    }

    private void requestRavr() {
        clearAllMarker();
        PackRavrListUp up = new PackRavrListUp();
        NetTask task = new NetTask(this, new NetTask.NetListener() {
            @Override
            public void onComplete(PcsPackDown down) {
                if(down instanceof PackRavrListDown) {
                    PackRavrListDown ravrListDown = (PackRavrListDown) down;
                    if(ravrListDown.ravrList != null) {
                        addAllRavrMarker(ravrListDown.ravrList);
                        updateShowTip();
                    }
                }
            }
        });
        task.execute(up);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_exit:
                clickBack();
                break;
            case R.id.btn_share:
                share();
                break;
        }
    }
}
