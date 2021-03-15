package com.pcs.ztqtj.view.activity.life.expert_interpretation;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.AdapterExpertList;
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
 * Created by Z on 2016/11/9.
 */

public class ActivityExpertList extends FragmentActivityZtqBase {

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    private EditText search_text;
    private ListView list_content;
    private AdapterExpertList adapterExpertList;
    private List<ItemExpert> listData;
    private List<ItemExpert> listDataSource;

    /**
     * 记录当前已加载到第几页
     **/
    private int page = 1;

    private PackExpertListUp expertListUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expert_list);
        PcsDataBrocastReceiver.registerReceiver(this, mReceiver);
        initView();
        initData();
        initEvent();
    }

    private void initView() {
        search_text = (EditText) findViewById(R.id.search_text);
        list_content = (ListView) findViewById(R.id.list_content);
    }

    private void initData() {
        expertListUp = new PackExpertListUp();
        expertListUp.channel_id = "100014";
        setTitleText("专家解读");
        listData = new ArrayList<>();
        listDataSource = new ArrayList<>();
        adapterExpertList = new AdapterExpertList(listData, getImageFetcher());
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


    private PcsDataBrocastReceiver mReceiver = new PcsDataBrocastReceiver() {
        @Override
        public void onReceive(String nameStr, String errorStr) {
            if (TextUtils.isEmpty(nameStr) || !TextUtils.isEmpty(errorStr)) {
                return;
            }
            if (expertListUp != null && expertListUp.getName().equals(nameStr)) {
                dismissProgressDialog();
                PackExpertListDown sharePackDown = (PackExpertListDown) PcsDataManager.getInstance().getNetPack(nameStr);
                if (sharePackDown == null) {
                    page = -1;
                } else {
                    listDataSource.addAll(sharePackDown.dataList);
                    listData.addAll(sharePackDown.dataList);
                    adapterExpertList.notifyDataSetChanged();
                    if (sharePackDown.dataList.size() == expertListUp.count) {
                        page++;
                        System.out.println("有更多数据");
                    } else {
                        System.out.println("无更多数据");
                        page = -1;
                    }
                }
            }
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
        expertListUp.page = page;
        PcsDataDownload.addDownload(expertListUp);
    }
}


