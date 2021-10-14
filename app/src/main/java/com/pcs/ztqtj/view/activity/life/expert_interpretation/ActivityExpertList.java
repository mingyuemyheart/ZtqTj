package com.pcs.ztqtj.view.activity.life.expert_interpretation;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.AdapterExpertList;
import com.pcs.ztqtj.control.tool.utils.TextUtil;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.util.OkHttpUtil;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
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
 * 生活气象-专家解读
 */
public class ActivityExpertList extends FragmentActivityZtqBase {

    public class MyItemExpert {
        public String id;
        public String title;
        public String release_time;
        public String small_img;
        public String big_img;
        public String desc;
        public String link;
    }

    private EditText search_text;
    private ListView list_content;
    private AdapterExpertList adapterExpertList;
    private List<MyItemExpert> listData;
    private List<MyItemExpert> listDataSource;

    /**
     * 记录当前已加载到第几页
     **/
    private int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expert_list);
        initView();
        initData();
        initEvent();
    }

    private void initView() {
        search_text = (EditText) findViewById(R.id.search_text);
        list_content = (ListView) findViewById(R.id.list_content);
    }

    private void initData() {
        String title = getIntent().getStringExtra("title");
        if (title != null) {
            setTitleText(title);
        }
        listData = new ArrayList<>();
        listDataSource = new ArrayList<>();
        adapterExpertList = new AdapterExpertList(listData);
        list_content.setAdapter(adapterExpertList);
        getTitleList();
    }

    private void initEvent() {
        search_text.addTextChangedListener(textWatcher);
        list_content.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ActivityExpertList.this, ActivityExpertDetail.class);
                intent.putExtra("id", listData.get(position).id);
                intent.putExtra("big_img", listData.get(position).big_img);
                intent.putExtra("desc", listData.get(position).desc);
                intent.putExtra("title", listData.get(position).title);
                intent.putExtra("release_time", listData.get(position).release_time);
                startActivity(intent);
            }
        });
        list_content.setOnScrollListener(myOnScrollListener);
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!TextUtils.isEmpty(s.toString())) {
                listData.clear();
                for (int i = 0; i < listDataSource.size(); i++) {
                    if (listDataSource.get(i).title.contains(s.toString())) {
                        listData.add(listDataSource.get(i));
                    }
                }
                adapterExpertList.notifyDataSetChanged();
            } else {
                listData.clear();
                listData.addAll(listDataSource);
                adapterExpertList.notifyDataSetChanged();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    private AbsListView.OnScrollListener myOnScrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            // 当不滚动时
            if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                // 判断是否滚动到底部
                if (view.getLastVisiblePosition() == view.getCount() - 1) {
                    // 加载更多功能的代码
                    System.out.println("到了底部，加载更多");
                    getTitleList();
                }
            }
        }

        @Override
        public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {

        }
    };

    private void getTitleList() {
        if (!isOpenNet()) {
            showToast(getString(R.string.net_err));
            return;
        }
        if (page == -1) {
            return;
        }
        okHttpExpertList();
    }

    /**
     * 获取专家解读列表
     */
    private void okHttpExpertList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param  = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    JSONObject info = new JSONObject();
                    info.put("stationId", "");
                    param.put("paramInfo", info);
                    String json = param.toString();
                    final String url = CONST.BASE_URL+"zjjd";
                    Log.e("zjjd", url);
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
                                        Log.e("zjjd", result);
                                        try {
                                            JSONObject obj = new JSONObject(result);
                                            if (!obj.isNull("b")) {
                                                JSONObject bobj = obj.getJSONObject("b");
                                                if (!bobj.isNull("tq_zx")) {
                                                    JSONObject tq_zx = bobj.getJSONObject("tq_zx");
                                                    dismissProgressDialog();
                                                    listDataSource.clear();
                                                    listData.clear();
                                                    if (!tq_zx.isNull("info_list")) {
                                                        JSONArray array = tq_zx.getJSONArray("info_list");
                                                        for (int i = 0; i < array.length(); i++) {
                                                            MyItemExpert myItemExpert = new MyItemExpert();
                                                            JSONObject itemObj = array.getJSONObject(i);
                                                            myItemExpert.id = itemObj.getString("id");
                                                            myItemExpert.title = itemObj.getString("title");
                                                            myItemExpert.small_img = itemObj.getString("small_img");
                                                            myItemExpert.big_img = itemObj.getString("big_img");
                                                            myItemExpert.link = itemObj.getString("link");
                                                            myItemExpert.desc = itemObj.getString("desc");
                                                            myItemExpert.release_time = itemObj.getString("release_time");
                                                            listData.add(myItemExpert);
                                                            listDataSource.add(myItemExpert);
                                                        }
                                                    }
                                                    adapterExpertList.notifyDataSetChanged();
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


