package com.pcs.ztqtj.view.activity.livequery;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalStation;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackFycxSstqDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackFycxTrendDown;
import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.livequery.ControlDistribution;
import com.pcs.ztqtj.control.tool.utils.TextUtil;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.util.OkHttpUtil;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 监测预报-整点天气
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
    }

    public void reqData(ControlDistribution.ColumnCategory correntType, String stationName) {
        this.correntType = correntType;
        String id = ZtqCityDB.getInstance().getStationId(stationName);
        if (TextUtils.isEmpty(id)) {
            activity.dataIsNull();
            return;
        }
        activity.showProgressDialog();
        if (correntType == ControlDistribution.ColumnCategory.RAIN) {
            getData(id, "rain");
        } else if (correntType == ControlDistribution.ColumnCategory.TEMPERATURE) {
            getData(id, "temp");
        } else if (correntType == ControlDistribution.ColumnCategory.WIND) {
            getData(id, "wins");
        } else if (correntType == ControlDistribution.ColumnCategory.PRESSURE) {
            getData(id, "prs");
        } else if (correntType == ControlDistribution.ColumnCategory.VISIBILITY) {
            getData(id, "visibility");
        } else if (correntType == ControlDistribution.ColumnCategory.HUMIDITY) {
            getData(id, "rh");
        }
    }

    private void getData(String stationId, String type) {
        okHttpFycxSstq(stationId);
        okHttpTrend(stationId, type);
    }

    public List<PackLocalStation> searchStation(String stationName, String str) {
        List<PackLocalStation> stationList = new ArrayList<>();
        boolean isTj = ZtqCityDB.getInstance().getStationIsTjByName(stationName);
        if(isTj/* && ZtqCityDB.getInstance().isServiceAccessible()*/) {
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

    /**
     * 获取整点天气实况
     */
    private void okHttpFycxSstq(final String stationId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param  = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    JSONObject info = new JSONObject();
                    info.put("stationId", stationId);
                    param.put("paramInfo", info);
                    String json = param.toString();
                    Log.e("fycx_sstq", json);
                    final String url = CONST.BASE_URL+"fycx_sstq";
                    Log.e("fycx_sstq", url);
                    RequestBody body = FormBody.create(MediaType.parse("application/json; charset=utf-8"), json);
                    OkHttpUtil.enqueue(new Request.Builder().post(body).url(url).build(), new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        }
                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            if (!response.isSuccessful()) {
                                return;
                            }
                            final String result = response.body().string();
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.e("fycx_sstq", result);
                                    if (!TextUtil.isEmpty(result)) {
                                        try {
                                            JSONObject obj = new JSONObject(result);
                                            if (!obj.isNull("b")) {
                                                JSONObject bobj = obj.getJSONObject("b");
                                                if (!bobj.isNull("fycx_sstq")) {
                                                    JSONObject fycx_sstq = bobj.getJSONObject("fycx_sstq");
                                                    if (!TextUtil.isEmpty(fycx_sstq.toString())) {
                                                        activity.dismissProgressDialog();
                                                        PackFycxSstqDown sstq = new PackFycxSstqDown();
                                                        sstq.fillData(fycx_sstq.toString());
                                                        activity.reFlushSstq(sstq);
                                                    }
                                                }
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            });
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 获取趋势数据
     */
    private void okHttpTrend(final String stationId, final String dataType) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param  = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    JSONObject info = new JSONObject();
                    info.put("stationId", stationId);
                    info.put("dataType", dataType);
                    param.put("paramInfo", info);
                    String json = param.toString();
                    Log.e("fycx_trend_sta", json);
                    final String url = CONST.BASE_URL+"fycx_trend_sta";
                    Log.e("fycx_trend_sta", url);
                    RequestBody body = FormBody.create(MediaType.parse("application/json; charset=utf-8"), json);
                    OkHttpUtil.enqueue(new Request.Builder().post(body).url(url).build(), new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        }
                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            if (!response.isSuccessful()) {
                                return;
                            }
                            final String result = response.body().string();
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.e("fycx_trend_sta", result);
                                    if (!TextUtil.isEmpty(result)) {
                                        try {
                                            JSONObject obj = new JSONObject(result);
                                            if (!obj.isNull("b")) {
                                                JSONObject bobj = obj.getJSONObject("b");
                                                if (!bobj.isNull("fycx_trend_sta")) {
                                                    JSONObject fycx_trend_sta = bobj.getJSONObject("fycx_trend_sta");
                                                    if (!TextUtil.isEmpty(fycx_trend_sta.toString())) {
                                                        activity.dismissProgressDialog();
                                                        PackFycxTrendDown trendDown = new PackFycxTrendDown();
                                                        trendDown.fillData(fycx_trend_sta.toString());
                                                        activity.reFlushList(trendDown);
                                                    }
                                                }
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            });
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
