package com.pcs.ztqtj.view.activity.livequery;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalStation;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackFycxSstqDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackFycxSstqUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackFycxTrendDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackFycxTrendUp;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.livequery.ControlDistribution;
import com.pcs.ztqtj.model.ZtqCityDB;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Z on 2017/4/17.
 * 风雨查询详情控制
 */

public class ActivityLiveQueryDetailControl {
    private ControlDistribution.ColumnCategory correntType = ControlDistribution.ColumnCategory.RAIN;

    public ControlDistribution.ColumnCategory getCuttentType() {
        return correntType;
    }

    public void setCurrentType(ControlDistribution.ColumnCategory type) {
        this.correntType = type;
    }

    private ActivityLiveQueryDetail activity;

    public ActivityLiveQueryDetailControl(ActivityLiveQueryDetail activity) {
        this.activity = activity;
        setOnCreate();
    }

    private PackFycxSstqUp fycxSstqUp = new PackFycxSstqUp();
    ;//实时天气
    private PackFycxTrendUp fycxTrendUp = new PackFycxTrendUp();
    ;//实况周视图_统计
//    private PackFycxComparisonUp fycxComparisonUp = new PackFycxComparisonUp();
    ;//对比图

    public void reqData(ControlDistribution.ColumnCategory correntType, String stationName) {
        this.correntType = correntType;
        String id = ZtqCityDB.getInstance().getStationId(stationName);
        if (TextUtils.isEmpty(id)) {
            activity.dataIsNull();
            return;
        }
        activity.showProgressDialog();
        if (correntType == ControlDistribution.ColumnCategory.RAIN) {
            getData(id, "10");
        } else if (correntType == ControlDistribution.ColumnCategory.TEMPERATURE) {
            getData(id, "11");
        } else if (correntType == ControlDistribution.ColumnCategory.WIND) {
            getData(id, "12");
        } else if (correntType == ControlDistribution.ColumnCategory.PRESSURE) {
            getData(id, "14");
        } else if (correntType == ControlDistribution.ColumnCategory.VISIBILITY) {
            getData(id, "13");
        } else if (correntType == ControlDistribution.ColumnCategory.HUMIDITY) {
//            相对湿度
            getData(id, "17");
        }
    }

    private void getData(String stationID, String type) {
        fycxSstqUp.stationid = stationID;
        PcsDataDownload.addDownload(fycxSstqUp);

        fycxTrendUp.channel = "2";
        fycxTrendUp.stationid = stationID;
        fycxTrendUp.type = type;
        PcsDataDownload.addDownload(fycxTrendUp);

//        fycxComparisonUp.stationid = stationID;
//        fycxComparisonUp.type = type;
//        PcsDataDownload.addDownload(fycxComparisonUp);
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
            if (fycxSstqUp.getName().equals(nameStr)) {
                activity.dismissProgressDialog();
                PackFycxSstqDown sstq = (PackFycxSstqDown) PcsDataManager.getInstance().getNetPack(nameStr);
                activity.reFlushSstq(sstq);
            } else if (fycxTrendUp.getName().equals(nameStr)) {
                activity.dismissProgressDialog();
                PackFycxTrendDown trendDown = (PackFycxTrendDown) PcsDataManager.getInstance().getNetPack(nameStr);
//   -------------------- 测试数据------------------
//                if (trendDown == null) {
//                    trendDown = new PackFycxTrendDown();
//                }
//                for (int i = 0; i < trendDown.skList.size(); i++) {
//                    Random rand = new Random();
//                    trendDown.skList.get(i).val = "";
//                    trendDown.ybList.get(i).val =  "0";
//                    trendDown.skList.get(i).val = rand.nextInt(8)+rand.nextFloat() +"";
//                    trendDown.ybList.get(i).val = rand.nextInt(7)+rand.nextFloat()+ "";
//                    trendDown.skList.get(i).val = rand.nextFloat() +"";
//                    trendDown.ybList.get(i).val = rand.nextFloat()+ "";
//                }
            // --------------------------------------
                activity.reFlushList(trendDown);
            }
        }
    };


    public List<PackLocalStation> searchStation(String stationName, String str) {
        List<PackLocalStation> stationList = new ArrayList<>();
        boolean isTj = ZtqCityDB.getInstance().getStationIsTjByName(stationName);
        if(isTj && ZtqCityDB.getInstance().isServiceAccessible()) {
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
