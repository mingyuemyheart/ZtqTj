package com.pcs.ztqtj.view.activity.product.agriculture;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.AdapterAgricultureWeather;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.net.expert.ItemExpert;
import com.pcs.lib_ztqfj_v2.model.pack.net.expert.PackExpertListDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.expert.PackExpertListUp;

import java.util.ArrayList;
import java.util.List;

/**
 * 农业气象
 * Created by tyaathome on 2016/11/7.
 */
public class ActivityAgricultureWeather extends FragmentActivityZtqBase implements View.OnClickListener {

    private ListView listView;

    private List<ItemExpert> listColumn = new ArrayList<>();
    private PackExpertListUp packExpertListUp = new PackExpertListUp();
    private static final int COUNT = 15;
    private int currentPage = 1;
    private MyReceiver receiver = new MyReceiver();
    private AdapterAgricultureWeather adapter;
    private boolean isLoading = false;
    private boolean isReqFinish = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agriculture_weather);
        setTitleText(getIntent().getStringExtra("title"));
        initView();
        initEvent();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(receiver != null) {
            PcsDataBrocastReceiver.unregisterReceiver(this, receiver);
            receiver = null;
        }
    }

    private void initView() {
        listView = (ListView) findViewById(R.id.listview);
    }

    private void initEvent() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ActivityAgricultureWeather.this, ActivityAgricultureWeatherDetail.class);
                intent.putExtra("title", getIntent().getStringExtra("title"));
                intent.putExtra("id", listColumn.get(position).id);
                startActivity(intent);
            }
        });
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // 当不滚动时
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    Log.e("position",view.getLastVisiblePosition() + "");
                    // 判断是否滚动到底部
                    if (view.getLastVisiblePosition() == view.getCount() - 1) {
                        // 加载更多功能的代码
                        Log.e("jzy","到了底部，加载更多");

                        if (!isLoading && !isReqFinish) {
                            reqMoreList();
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    private void initData() {
        PcsDataBrocastReceiver.registerReceiver(this, receiver);
        adapter = new AdapterAgricultureWeather(this, listColumn);
        listView.setAdapter(adapter);
        reqNewList();
    }

    /**
     * 获取新列表
     */
    private void reqNewList() {
        isLoading = true;
        currentPage = 1;
        packExpertListUp = new PackExpertListUp();
        packExpertListUp.channel_id = getIntent().getStringExtra("channel_id");
        packExpertListUp.count = COUNT;
        packExpertListUp.page = currentPage;
        PcsDataDownload.addDownload(packExpertListUp);
    }

    private void reqMoreList() {
        isLoading = true;
        currentPage++;
        packExpertListUp = new PackExpertListUp();
        packExpertListUp.channel_id = getIntent().getStringExtra("channel_id");
        packExpertListUp.count = COUNT;
        packExpertListUp.page = currentPage;
        PcsDataDownload.addDownload(packExpertListUp);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    private class MyReceiver extends PcsDataBrocastReceiver {

        @Override
        public void onReceive(String nameStr, String errorStr) {
            if(!TextUtils.isEmpty(errorStr)) {
                return ;
            }

            if(nameStr.equals(packExpertListUp.getName())) {
                isLoading = false;
                PackExpertListDown down = (PackExpertListDown) PcsDataManager.getInstance().getNetPack(nameStr);
                if(down == null) {
                    return ;
                }

                listColumn.addAll(down.dataList);
                adapter.notifyDataSetChanged();
                if(down.dataList != null) {
                    // 当请求回来的数据列表小于count时则表示已无更多数据了，所以不需要再请求并隐藏加载更多
                    if(down.dataList.size() < COUNT) {
                        adapter.setLoadingVisibility(View.GONE);
                        isReqFinish = true;
                    } else {
                        isReqFinish = false;
                        adapter.setLoadingVisibility(View.VISIBLE);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }
}
