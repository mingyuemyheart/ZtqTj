package com.pcs.ztqtj.view.activity.product.agriculture;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.pcs.lib_ztqfj_v2.model.pack.net.agriculture.AgricultureInfo;
import com.pcs.lib_ztqfj_v2.model.pack.net.agriculture.PackAgricultureDown;
import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.product_numerical.AdapterColumn;
import com.pcs.ztqtj.control.tool.utils.TextUtil;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.util.OkHttpUtil;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;

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
 * 专项服务-行业气象-农业气象
 */
public class ActivityAgricultureWeatherColumn extends FragmentActivityZtqBase {

    private GridView gridView;
    private AdapterColumn adapter;
    private List<AgricultureInfo> columnInfoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitleText(R.string.agriculture_weather);
        setContentView(R.layout.activity_agriculture_weather_column);
        initView();
    }

    private void initView() {
//        AgricultureInfo agricultureInfo = new AgricultureInfo();
//        agricultureInfo.title = "农业灾情直报";
//        columnInfoList.add(agricultureInfo);

        gridView = (GridView) findViewById(R.id.gridview);
        adapter = new AdapterColumn(this, columnInfoList, getImageFetcher());
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String type = columnInfoList.get(position).type;
                if(type.equals("1")) {
                    gotoZQZB(columnInfoList.get(position));
                } else {
                    gotoDetail(columnInfoList.get(position));
                }
            }
        });

        okHttpNyqxFw();
    }

    /**
     * 跳转灾情直报栏目
     */
    private void gotoZQZB(AgricultureInfo info) {
        String channel_id = info.channel_id;
        Intent intent = new Intent(this, ActivityAgricultureZQZB.class);
        intent.putExtra("channel_id", channel_id);
        intent.putExtra("type", info.type);
        startActivity(intent);
    }

    /**
     * 跳转详情页
     * @param info
     */
    private void gotoDetail(AgricultureInfo info) {
        String channel_id = info.channel_id;
        Intent intent = new Intent(this, ActivityAgricultureWeather.class);
        intent.putExtra("title", info.title);
        intent.putExtra("channel_id", channel_id);
        intent.putExtra("type", info.type);
        startActivity(intent);
    }

    /**
     * 获取农业气象列表
     */
    private void okHttpNyqxFw() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param  = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    String json = param.toString();
                    final String url = CONST.BASE_URL+"nyqx_fw";
                    Log.e("nyqx_fw", url);
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
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (!TextUtil.isEmpty(result)) {
                                        Log.e("nyqx_fw", result);
                                        try {
                                            JSONObject obj = new JSONObject(result);
                                            if (!obj.isNull("b")) {
                                                JSONObject bobj = obj.getJSONObject("b");
                                                if (!bobj.isNull("nyqx_fw")) {
                                                    JSONObject yjxx_info_query = bobj.getJSONObject("nyqx_fw");
                                                    if (!TextUtil.isEmpty(yjxx_info_query.toString())) {
                                                        PackAgricultureDown down = new PackAgricultureDown();
                                                        down.fillData(yjxx_info_query.toString());
                                                        columnInfoList.clear();
                                                        columnInfoList.addAll(down.info_list);
                                                        adapter.notifyDataSetChanged();
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
