package com.pcs.ztqtj.view.fragment.airquality;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.livequery.ControlDistribution;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.view.activity.livequery.LegendInterval;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCity;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalStation;
import com.pcs.lib_ztqfj_v2.model.pack.net.airinfopack.PackAirTrendDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.airinfopack.PackAirTrendUp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Z on 2017/4/17.
 * 风雨查询详情控制
 */

public class FragmentAirQueryDetailControl {
    private ControlDistribution.ColumnCategory correntType = ControlDistribution.ColumnCategory.RAIN;

    public ControlDistribution.ColumnCategory getCuttentType() {
        return correntType;
    }

    private FragmentAirQualityPre fragment;
    private FragmentActivity activity;

    public FragmentAirQueryDetailControl(FragmentAirQualityPre fragment,FragmentActivity activity) {
        this.fragment = fragment;
        this.activity=activity;
        setOnCreate();
    }

    //实况统计折线图
    private PackAirTrendUp airTrendUp = new PackAirTrendUp();


    public void reqData(ControlDistribution.ColumnCategory correntType, String station_id,String areatype,String sx) {
        this.correntType = correntType;
//        String id = ZtqCityDB.getInstance().getStationId(stationName);
//        if (TextUtils.isEmpty(id)) {
//           activity.dataIsNull();
//            return;
//        }
        fragment.showProgressDialog();
            getData(station_id, sx,areatype);
    }

    private void getData(String stationID, String sx,String areatype) {
        airTrendUp.num = "24";
        airTrendUp.station_id = stationID;
        airTrendUp.sx = sx;
        airTrendUp.areatype=areatype;
        PcsDataDownload.addDownload(airTrendUp);
    }

    public void setOnCreate() {
        PcsDataBrocastReceiver.registerReceiver(activity, receiver);
    }

    public void setOnDestory() {
        activity.unregisterReceiver(receiver);
    }

    private PcsDataBrocastReceiver receiver = new PcsDataBrocastReceiver() {
        @Override
        public void onReceive(String nameStr, String errorStr) {
            if (airTrendUp.getName().equals(nameStr)) {
                fragment.dismissProgressDialog();
                PackAirTrendDown trendDown = (PackAirTrendDown) PcsDataManager.getInstance().getNetPack(nameStr);
                fragment.reFlushList(trendDown);
            }
        }
    };


    public List<PackLocalStation> searchStation(String str) {
        List<PackLocalStation> stationList = new ArrayList<>();
        PackLocalCity currentCity = ZtqCityDB.getInstance().getCityMain();
        if(currentCity.isFjCity) {
            ZtqCityDB.getInstance().searchStation(stationList, str);
        } else {
            ZtqCityDB.getInstance().searchCountryStation(stationList, str);
        }

        return stationList;
    }



    /**
     * 通过aqi获取图标
     *
     * @param value
     * @return
     */
    public Bitmap getIcon(Context context, String value) {
        //视图
        View view = LayoutInflater.from(context).inflate(R.layout.livequery_marker, null);
        TextView textView = (TextView) view.findViewById(R.id.marker_text);
        int iconInt;
        if (TextUtils.isEmpty(value)) {
            iconInt = valueIsNull(context, textView);
        } else {
            float valueFloat = 0;
            try {
                valueFloat = Float.parseFloat(value);
                iconInt = LegendInterval.getInstance().getDrawableId(getCuttentType(), valueFloat);
//                if(getCuttentType()== ControlDistribution.ColumnCategory.WIND){
//                }else{
                    textView.setText(value);
                    textView.setTextColor(context.getResources().getColor(LegendInterval.getInstance().getTextColorId(getCuttentType(), valueFloat)));
//                }
            } catch (Exception e) {
                e.printStackTrace();
                iconInt = valueIsNull(context, textView);
            }
        }
        view.setBackgroundResource(iconInt);
        return BitmapDescriptorFactory.fromView(view).getBitmap();
    }

    /**
     * value值不能转换成int型的时候
     * @param context
     * @param textView
     * @return
     */
    private int valueIsNull(Context context, TextView textView) {
        int iconInt;
        iconInt = R.drawable.icon_blank_value;
        textView.setText("--");
        textView.setTextColor(context.getResources().getColor(R.color.text_black));
        return iconInt;
    }
}
