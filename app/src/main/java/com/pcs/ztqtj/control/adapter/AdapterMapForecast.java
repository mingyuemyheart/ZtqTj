package com.pcs.ztqtj.control.adapter;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.amap.api.maps.model.LatLng;
import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.utils.TextUtil;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.util.OkHttpUtil;
import com.pcs.ztqtj.view.activity.product.ActivityMapForecast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 首页-指点天气
 */
public class AdapterMapForecast extends BaseAdapter {

    private Handler mUIHandler = new Handler();
    private ActivityMapForecast mContext;
    private MyPackMapForecastDown mPackDown = new MyPackMapForecastDown();

    public AdapterMapForecast(ActivityMapForecast context) {
        mContext = context;
    }

    /**
     * 刷新数据
     *
     * @param latLng
     */
    public void refresh(int hour, LatLng latLng) {
        okHttpGrid(latLng.latitude, latLng.longitude);
        this.notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mPackDown.list.size();
    }

    @Override
    public Object getItem(int position) {
        return mPackDown.list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(
                    R.layout.item_map_forecast, null);
        }
        // 背景
        if (position % 2 == 0) {
            view.setBackgroundColor(mContext.getResources().getColor(
                    android.R.color.white));
        } else {
            view.setBackgroundColor(mContext.getResources().getColor(
                    R.color.map_forecast_list_bg));
        }
        MyPackMapForecastDown.MapForecast pack = mPackDown.list.get(position);
        TextView textView = null;

        // 天气
        textView = (TextView) view.findViewById(R.id.text_weather);
        textView.setText(pack.desc);
        // 降雨量
        textView = (TextView) view.findViewById(R.id.text_rainfall);
        textView.setText(pack.rainfall + "mm");
        // 气温
        textView = (TextView) view.findViewById(R.id.text_temperature);
        textView.setText(pack.temperature + "°C");
        // 能见度
        textView = (TextView) view.findViewById(R.id.text_visibility);
        textView.setText(pack.visibility + "m");
        // 时间
        textView = (TextView) view.findViewById(R.id.text_time);
        textView.setText(pack.time + "时");
        // 风向
        textView = (TextView) view.findViewById(R.id.text_winddir);
        textView.setText(pack.winddir);
        // 风力
        textView = (TextView) view.findViewById(R.id.text_windspeed);
        if (!TextUtil.isEmpty(pack.windspeed)) {
            textView.setText(pack.windspeed);
        } else {
            textView.setText(pack.windlevel);
        }

        return view;
    }

    /**
     * 获取指点天气数据
     */
    private void okHttpGrid(final double lat, final double lng) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param  = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    JSONObject info = new JSONObject();
                    info.put("lat", lat+"");
                    info.put("lon", lng+"");
                    param.put("paramInfo", info);
                    String json = param.toString();
                    Log.e("grid", json);
                    final String url = CONST.BASE_URL+"grid";
                    Log.e("grid", url);
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
                            mUIHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Log.e("grid", result);
                                    if (!TextUtil.isEmpty(result)) {
                                        try {
                                            JSONObject obj = new JSONObject(result);
                                            if (!obj.isNull("b")) {
                                                JSONObject bobj = obj.getJSONObject("b");
                                                if (!bobj.isNull("grid")) {
                                                    JSONObject grid = bobj.getJSONObject("grid");
                                                    if (!TextUtil.isEmpty(grid.toString())) {
                                                        mPackDown.fillData(grid.toString());
                                                        if (mPackDown == null || mPackDown.list.size() == 0) {
                                                            mContext.setView(true);
                                                        } else {
                                                            mContext.setView(false);
                                                            AdapterMapForecast.this.notifyDataSetChanged();
                                                        }
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
