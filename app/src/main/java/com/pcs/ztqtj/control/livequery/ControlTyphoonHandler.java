package com.pcs.ztqtj.control.livequery;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.net.typhoon.PackTyphoonListCurrentActivityDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.typhoon.PackTyphoonListCurrentActivityUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.typhoon.PackTyphoonPathDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.typhoon.PackTyphoonPathUp;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.typhoon.AdapterDistributionTyphoonList;
import com.pcs.ztqtj.control.tool.ZtqLocationTool;
import com.pcs.ztqtj.view.activity.livequery.ActivityLiveQuery;
import com.pcs.ztqtj.view.activity.livequery.TyphoonInfoCheck;
import com.pcs.ztqtj.view.fragment.livequery.FragmentDistributionMap;
import com.pcs.ztqtj.view.myview.typhoon.TyphoonView;

import java.util.ArrayList;
import java.util.List;

/**
 * 监测预报-实况查询-要素分布图-台风操作控制器
 */
public class ControlTyphoonHandler extends ControlDistributionBase {

    private FragmentDistributionMap mFragment;
    private ActivityLiveQuery mActivity;
    private Context mContext;
    private ViewGroup mRootLayout;
    private AMap aMap;
    // 台风按钮
    private CheckBox cbSb, cbZd, cbRadar, cbCloud, cbRadarPlay, cbCloudPlay, cbTyphoon;

    // 台风列表
    private ListView lvTyphoonList;
    // 台风列表适配器
    private AdapterDistributionTyphoonList adapterTyphoonList;
    //  台风列表数据
    private List<TyphoonInfoCheck> typhoonInfoList = new ArrayList<>();

    // 台风路径上传包
    private PackTyphoonPathUp typhoonPathUp = new PackTyphoonPathUp();
    private PackTyphoonListCurrentActivityUp typhoonListUp = new PackTyphoonListCurrentActivityUp();

    // 台风路径控制器
    private ControlDistributionTyphoon controlTyphoon;
    // 地图缩放层级
    private static final float MAP_ZOOM = 7.2f;

    // 下拉控件
    private View layoutTime;
    // 站点下拉控件
    private View layoutSite;
    // 当前无台风控件
    private TextView tvNullTyphoon;


    public ControlTyphoonHandler(FragmentDistributionMap fragment, ActivityLiveQuery activity, ViewGroup rootLayout) {
        mFragment = fragment;
        mActivity = activity;
        mContext = activity;
        mRootLayout = rootLayout;
        aMap = fragment.getMap();
    }

    @Override
    public void init() {
        cbSb = (CheckBox) mRootLayout.findViewById(R.id.rb_sb);
        cbZd = (CheckBox) mRootLayout.findViewById(R.id.rb_zd);
        cbRadar = (CheckBox) mRootLayout.findViewById(R.id.rb_radar);
        cbCloud = (CheckBox) mRootLayout.findViewById(R.id.rb_cloud);
        layoutTime = mRootLayout.findViewById(R.id.layout_drop_search);
        layoutSite = mRootLayout.findViewById(R.id.layout_site);
        cbTyphoon = (CheckBox) mRootLayout.findViewById(R.id.cb_typhoon);
        lvTyphoonList = (ListView) mRootLayout.findViewById(R.id.lv_typhoon_list);
        cbTyphoon.setOnCheckedChangeListener(cbTyphoonListener);
        tvNullTyphoon = (TextView) mRootLayout.findViewById(R.id.tv_null_typhoon);
        // 初始化台风列表
        adapterTyphoonList = new AdapterDistributionTyphoonList(mContext, typhoonInfoList);
        adapterTyphoonList.setListener(onTyphoonItemClickListener);
        lvTyphoonList.setAdapter(adapterTyphoonList);
        controlTyphoon = new ControlDistributionTyphoon(mContext, aMap);
        PcsDataBrocastReceiver.registerReceiver(mContext, receiver);
    }

    @Override
    public void updateView(ControlDistribution.ColumnCategory column) {
        if(cbTyphoon.isChecked()) {
            clearAllTyphoonPath();
            //reqCurrentTyphoon();
        }
    }

    @Override
    public void clear() {
        if(cbTyphoon.isChecked()) {
            cbTyphoon.setChecked(false);
        }
    }

    @Override
    public void destroy() {
        if(receiver != null) {
            PcsDataBrocastReceiver.unregisterReceiver(mContext, receiver);
            receiver = null;
        }
    }

    /**
     * 添加台风路径到地图
     */
    private void addTyphoonToMap(PackTyphoonPathDown down) {
        controlTyphoon.showTyphoonPath(down);
        controlTyphoon.setFinishListener(onFinishListener);
    }

    /**
     * 清除某个台风路径
     * @param code
     */
    private void clearTyphoonPath(String code) {
        controlTyphoon.hideTyphoonPath(code);

        for(TyphoonInfoCheck bean : typhoonInfoList) {
            if(bean.typhoonInfo.code.equals(code)) {
                boolean isChecked = typhoonInfoList.get(typhoonInfoList.indexOf(bean)).isChecked;
                typhoonInfoList.get(typhoonInfoList.indexOf(bean)).isChecked = !isChecked;
                adapterTyphoonList.notifyDataSetChanged();
                break;
            }
        }
    }

    /**
     * 自动缩放地图
     *
     * @param latLngList
     */
    private void zoomToSpan(List<LatLng> latLngList) {
        if (latLngList == null || latLngList.size() <= 0) {
            return;
        }
        if (latLngList.size() == 1) {
            aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngList.get(0), MAP_ZOOM));
        } else {
            aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(getLatLngBounds(latLngList), 200));
        }
    }

    /**
     * 获取台风路径范围区域
     *
     * @param list
     * @return
     */
    private LatLngBounds getLatLngBounds(List<LatLng> list) {
        if (list == null || list.size() <= 1) {
            return null;
        }

        double swLatitude = 90;
        double swLongitude = 180;

        double neLatitude = -90;
        double neLongitude = -180;

        for (LatLng latlng : list) {
            if (latlng.latitude < swLatitude) {
                swLatitude = latlng.latitude;
            }
            if (latlng.longitude < swLongitude) {
                swLongitude = latlng.longitude;
            }

            if (latlng.latitude > neLatitude) {
                neLatitude = latlng.latitude;
            }
            if (latlng.longitude > neLongitude) {
                neLongitude = latlng.longitude;
            }
        }

        LatLng swLatLng = new LatLng(swLatitude, swLongitude);
        LatLng neLatLng = new LatLng(neLatitude, neLongitude);
        return new LatLngBounds(swLatLng, neLatLng);
    }

    /**
     * 请求台风路径数据
     */
    private void reqTyphoonPath(String code) {
        //if(currentColumn == ControlDistribution.ColumnCategory.RAIN && cbTyphoon.isChecked()) {
        mActivity.showProgressDialog();
        typhoonPathUp = new PackTyphoonPathUp();
        typhoonPathUp.code = code;
        PcsDataDownload.addDownload(typhoonPathUp);
        //}
    }

    private void reqCurrentTyphoon() {
        if(mFragment.getCurrentColumn() == ControlDistribution.ColumnCategory.RAIN && cbTyphoon.isChecked()) {
            mActivity.showProgressDialog();
            typhoonListUp = new PackTyphoonListCurrentActivityUp();
            PcsDataDownload.addDownload(typhoonListUp);
        }
    }

    /**
     * 清除台风路径
     */
    private void clearAllTyphoonPath() {
        if(controlTyphoon != null) {
            controlTyphoon.hideAllTyphoonPath();
        }
        typhoonInfoList.clear();
        lvTyphoonList.setVisibility(View.GONE);
        adapterTyphoonList.notifyDataSetChanged();
    }

    private void setDropdownState() {
        if(cbTyphoon.isChecked() && !cbRadar.isChecked() && !cbSb.isChecked() && !cbZd.isChecked()) {
            layoutSite.setVisibility(View.INVISIBLE);
            layoutTime.setVisibility(View.INVISIBLE);
        }
    }

    CompoundButton.OnCheckedChangeListener cbTyphoonListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked) {
                reqCurrentTyphoon();
            } else {
                clearAllTyphoonPath();
                // 还原至初始点
                //aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(INIT_LATLNG, MAP_ZOOM));
            }
            setDropdownState();
        }
    };

    AdapterDistributionTyphoonList.OnTyphoonItemClickListener onTyphoonItemClickListener = new AdapterDistributionTyphoonList.OnTyphoonItemClickListener() {

        @Override
        public void onItemClickListener(int position, boolean isChecked) {
            //typhoonInfoList.get(position).isChecked = !isChecked;
            String code = typhoonInfoList.get(position).typhoonInfo.code;
            if(!isChecked) {
                reqTyphoonPath(code);
            } else {
                clearTyphoonPath(code);
            }
        }
    };

    ControlDistributionTyphoon.OnFinishListener onFinishListener = new ControlDistributionTyphoon.OnFinishListener() {
        @Override
        public void onComplete(TyphoonView typhoonView) {
            List<LatLng> latLngList = new ArrayList<>(typhoonView.getAllLatLng());
            // 添加定位点
            LatLng latLng = ZtqLocationTool.getInstance().getLatLng();
            if(latLng != null) {
                //latLngList.add(latLng);
            }
            zoomToSpan(latLngList);
            if(latLngList.size() == 0) {
                mActivity.showToast("当前无台风路径");
            }
            mActivity.dismissProgressDialog();
        }
    };

    private PcsDataBrocastReceiver receiver = new PcsDataBrocastReceiver() {

        @Override
        public void onReceive(String nameStr, String errorStr) {
            if(typhoonListUp.getName().equals(nameStr)) {
                PackTyphoonListCurrentActivityDown down = (PackTyphoonListCurrentActivityDown) PcsDataManager.getInstance().getNetPack(nameStr);
                if(down == null || down.typhoonList.size() == 0) {
                    if(cbTyphoon.isChecked()) {
                        cbTyphoon.setChecked(false);
                    }
                    mActivity.dismissProgressDialog();
                    mActivity.showToast("当前无台风！");
                    tvNullTyphoon.setVisibility(View.VISIBLE);
                    lvTyphoonList.setVisibility(View.GONE);
                    return ;
                }
                tvNullTyphoon.setVisibility(View.GONE);
                lvTyphoonList.setVisibility(View.VISIBLE);
                reqTyphoonPath(down.typhoonList.get(0).code);
                typhoonInfoList.clear();
                for(int i = down.typhoonList.size()-1; i >= 0; i--) {
                    typhoonInfoList.add(new TyphoonInfoCheck(down.typhoonList.get(i)));
                }
                adapterTyphoonList.notifyDataSetChanged();
            }

            if(typhoonPathUp.getName().equals(nameStr)) {
                PackTyphoonPathDown down = (PackTyphoonPathDown) PcsDataManager.getInstance().getNetPack(nameStr);
                if(down == null) {
                    return;
                }
                addTyphoonToMap(down);

                for(TyphoonInfoCheck bean : typhoonInfoList) {
                    if(bean.typhoonInfo.code.equals(down.typhoonPathInfo.code)) {
                        boolean isChecked = typhoonInfoList.get(typhoonInfoList.indexOf(bean)).isChecked;
                        typhoonInfoList.get(typhoonInfoList.indexOf(bean)).isChecked = !isChecked;
                        adapterTyphoonList.notifyDataSetChanged();
                        break;
                    }
                }
            }
        }
    };
}
