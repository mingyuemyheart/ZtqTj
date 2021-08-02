package com.pcs.ztqtj.view.activity.product.agriculture;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.pcs.lib_ztqfj_v2.model.pack.net.expert.ItemExpert;
import com.pcs.lib_ztqfj_v2.model.pack.net.expert.PackExpertListDown;
import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.AdapterAgricultureWeather;
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
 * 专项服务-行业气象-农业气象-旬报，月报
 */
public class ActivityAgricultureWeather extends FragmentActivityZtqBase {

    private List<ItemExpert> listColumn = new ArrayList<>();
    private AdapterAgricultureWeather adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agricuture_fact);
        initWidget();
        initListView();
    }

    private void initWidget() {
        if (getIntent().hasExtra("title")) {
            String title = getIntent().getStringExtra("title");
            if (title != null) {
                setTitleText(title);
            }
        }
        okHttpInfoList();
    }

    private void initListView() {
        ListView listView = findViewById(R.id.listView);
        adapter = new AdapterAgricultureWeather(this, listColumn);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ItemExpert dto = listColumn.get(position);
                Intent intent = new Intent(ActivityAgricultureWeather.this, ActivityAgricultureWeatherDetail.class);
                intent.putExtra("title", getIntent().getStringExtra("title"));
                intent.putExtra("id", dto.id);
                intent.putExtra("channel_id", getIntent().getStringExtra("channel_id"));
                startActivity(intent);
            }
        });
    }

    /**
     * 获取农业气象旬报、月报
     */
    private void okHttpInfoList() {
        showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String type = getIntent().getStringExtra("type");
                    String channel_id = getIntent().getStringExtra("channel_id");
                    JSONObject param  = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    JSONObject info = new JSONObject();
                    info.put("stationId", channel_id);
                    info.put("extra", type);
                    param.put("paramInfo", info);
                    String json = param.toString();
                    Log.e("info_list", json);
                    final String url = CONST.BASE_URL+"info_list";
                    Log.e("info_list", url);
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
                                    Log.e("info_list", result);
                                    if (!TextUtil.isEmpty(result)) {
                                        try {
                                            JSONObject obj = new JSONObject(result);
                                            if (!obj.isNull("b")) {
                                                JSONObject bobj = obj.getJSONObject("b");
                                                if (!bobj.isNull("tq_zx")) {
                                                    JSONObject tq_zx = bobj.getJSONObject("tq_zx");
                                                    if (!TextUtil.isEmpty(tq_zx.toString())) {
                                                        dismissProgressDialog();
                                                        PackExpertListDown down = new PackExpertListDown();
                                                        down.fillData(tq_zx.toString());
                                                        listColumn.addAll(down.dataList);
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
