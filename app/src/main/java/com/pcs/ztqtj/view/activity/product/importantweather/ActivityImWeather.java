package com.pcs.ztqtj.view.activity.product.importantweather;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackImWeatherDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackImWeatherUp;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.im_weather.AdapterImWeather;
import com.pcs.ztqtj.control.tool.CommUtils;
import com.pcs.ztqtj.view.activity.FragmentActivityWithShare;

import java.util.ArrayList;

/**
 * 海洋气象 弃用
 * Created by Administrator on 2017/10/18 0018.
 */

public class ActivityImWeather extends FragmentActivityWithShare {

    private ListView lv_content;
    private TextView tv_imweather_null;
    private AdapterImWeather adapterImWeather;
    private PackImWeatherUp imWeatherUp=new PackImWeatherUp();
    private ArrayList<PackImWeatherDown.ImWeather> list=new ArrayList<>();
    // 每页条目数量
    private static final String COUNT = "8";
    // 是否全部请求完成
    private boolean isReqFinish = false;
    // 是否在加载中
    private boolean isLoading = false;
    //当前页数
    private int currentPage = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imweather);
        setTitleText("海洋气象");
        PcsDataBrocastReceiver.registerReceiver(ActivityImWeather.this, mReceiver);
        initView();
        req();
        initEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        CommUtils.closeKeyboard(this);
    }

    private void initView() {
        lv_content= (ListView) findViewById(R.id.lv_imweather);
        tv_imweather_null= (TextView) findViewById(R.id.tv_imweather_null);
        adapterImWeather=new AdapterImWeather(ActivityImWeather.this,list);
        lv_content.setAdapter(adapterImWeather);
    }
    private void initEvent(){
        lv_content.setOnScrollListener(myOnScrollListener);
        lv_content.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent=new Intent(ActivityImWeather.this,ActivityImWeatherDown.class);
                intent.putExtra("url",list.get(i).url);
                startActivity(intent);
            }
        });
    }
    private void req(){
        showProgressDialog();
        imWeatherUp.page=String.valueOf(currentPage);
        PcsDataDownload.addDownload(imWeatherUp);
    }

    private AbsListView.OnScrollListener myOnScrollListener = new AbsListView.OnScrollListener() {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            // 当不滚动时
            if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                Log.e("position", view.getLastVisiblePosition() + "");
                // 判断是否滚动到底部
                if (view.getLastVisiblePosition() == view.getCount() - 1) {
                    // 加载更多功能的代码
                    Log.e("jzy", "到了底部，加载更多");
                    if (!isLoading && !isReqFinish) {
                        showProgressDialog();
                        reqCenterDataMore();
                    }
                }
            }
        }

        @Override
        public void onScroll(AbsListView absListView, int i, int i1, int i2) {

        }
    };

    private void reqCenterDataMore() {
        currentPage++;
        imWeatherUp.page=String.valueOf(currentPage);
        PcsDataDownload.addDownload(imWeatherUp);
    }

    private PcsDataBrocastReceiver mReceiver = new PcsDataBrocastReceiver() {
        @Override
        public void onReceive(String nameStr, String errorStr) {
            if (TextUtils.isEmpty(nameStr)) {
                return;
            }
            if (nameStr.equals(imWeatherUp.getName())) {
                dismissProgressDialog();
                PackImWeatherDown  imWeatherDown = (PackImWeatherDown) PcsDataManager.getInstance().getNetPack(nameStr);
                if (imWeatherDown == null) {
                    return;
                }
                if (imWeatherDown.arrayList != null && imWeatherDown.arrayList.size() > 0) {
                    tv_imweather_null.setVisibility(View.GONE);
                    list.addAll(imWeatherDown.arrayList);
                    int count = Integer.parseInt(COUNT);
                    // 当请求回来的数据列表小于count时则表示已无更多数据了，所以不需要再请求并隐藏加载更多
                    if (imWeatherDown.arrayList.size() < count) {
                        adapterImWeather.setLoadingVisibility(false);
                        isReqFinish = true;
                    } else {
                        isReqFinish = false;
                        adapterImWeather.setLoadingVisibility(true);
                    }
                    adapterImWeather.notifyDataSetChanged();
                } else if (imWeatherDown.arrayList.size() == 0) {
                    tv_imweather_null.setVisibility(View.VISIBLE);
                    adapterImWeather.notifyDataSetChanged();
                    adapterImWeather.setLoadingVisibility(false);
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PcsDataBrocastReceiver.unregisterReceiver(ActivityImWeather.this, mReceiver);
    }
}
