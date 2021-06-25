package com.pcs.ztqtj.view.fragment.warning.disaster_reporting;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.pcs.lib.lib_pcs_v3.control.tool.Util;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCity;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.sh_warn.PackYjZqInfoListDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.sh_warn.PackYjZqInfoListUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.sh_warn.YjZqInfoList;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.disaster.AdatperDisasterInfo;
import com.pcs.ztqtj.control.adapter.livequery.AdapterData;
import com.pcs.ztqtj.model.ZtqCityDB;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页-预警中心-灾害直报-灾情信息，审核后的
 */
public class FragmentDisasterInfo extends FragmentReportBase implements View.OnClickListener {

    private ListView lv_disaster_info;
    private AdatperDisasterInfo adapter;
    private List<YjZqInfoList> list = new ArrayList<>();
    private TextView tv_disaster_county, tv_repson_name, tv_info_list,
            tv_info_content, tv_info_search;
    private List<String> mlist_county = new ArrayList<>();
    private List<String> mlist_county_id = new ArrayList<>();
    private ProgressDialog mProgress;
    // 每页条目数量
    private static final String COUNT = "30";
    // 是否在加载中
    private boolean isLoading = false;
    // 是否全部请求完成
    private boolean isReqFinish = false;
    //当前页数
    private int currentPage = 1;
    private String town_id = "";
    private boolean isFirstReq = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState) {
        return LayoutInflater.from(getActivity()).inflate(R.layout.fragment_disaster_info, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initData();
        showProgressDialog();
    }

    private void initData() {
        PcsDataBrocastReceiver.registerReceiver(getActivity(), mReceiver);
        mlist_county = new ArrayList<>();
        mlist_county_id = new ArrayList<>();
        ArrayList<PackLocalCity> mlist = new ArrayList<>();
        mlist.addAll(ZtqCityDB.getInstance().getCityLv1());
        for (int i = 0; i < mlist.size(); i++) {
            mlist_county.add(mlist.get(i).NAME);
            mlist_county_id.add(mlist.get(i).ID);
        }
        tv_disaster_county.setText(mlist_county.get(0));
        town_id=mlist_county_id.get(0);
        checkList();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            currentPage = 1;
            RefreshDate();
        }
    }

    private void initView() {
        lv_disaster_info = (ListView) getView().findViewById(R.id.lv_disaster_info);
        lv_disaster_info.setOnScrollListener(myOnScrollListener);
        adapter = new AdatperDisasterInfo(getActivity(), list);
        lv_disaster_info.setAdapter(adapter);
        lv_disaster_info.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                commitFragment(list.get(i).id);
            }
        });
        tv_disaster_county = (TextView) getView().findViewById(R.id.tv_disaster_county);
        tv_disaster_county.setOnClickListener(this);
        tv_repson_name = (TextView) getView().findViewById(R.id.tv_repson_name);
        tv_repson_name.setOnClickListener(this);
        tv_info_content = (TextView) getView().findViewById(R.id.tv_info_content);
        tv_info_list = (TextView) getView().findViewById(R.id.tv_info_list);
        tv_info_search = (TextView) getView().findViewById(R.id.tv_info_search);
        tv_info_search.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_disaster_county:
                createTimePopupWindow(tv_disaster_county, mlist_county )
                        .showAsDropDown(tv_disaster_county);
                break;

            case R.id.tv_info_search:
                showProgressDialog();
                list.clear();
                checkList();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

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
        //isLoading = true;
        currentPage++;
        packYjZqInfoListUp.page = String.valueOf(currentPage);
        packYjZqInfoListUp.sub_id = town_id;

        PcsDataDownload.addDownload(packYjZqInfoListUp);
    }

    private int screenHight = 0;

    /**
     * 创建下拉选择列表
     */
    public PopupWindow createTimePopupWindow(final TextView dropDownView, final List<String> dataeaum) {
        AdapterData dataAdapter = new AdapterData(getActivity(), dataeaum);
        View popcontent = LayoutInflater.from(getActivity()).inflate(R.layout.pop_list_layout, null);
        ListView lv = (ListView) popcontent.findViewById(R.id.mylistviw);
        lv.setAdapter(dataAdapter);
        final PopupWindow pop = new PopupWindow(getActivity());
        pop.setContentView(popcontent);
        pop.setOutsideTouchable(false);
        pop.setWidth((dropDownView.getWidth()));
        // 调整下拉框长度
        screenHight = Util.getScreenHeight(getActivity());
        if (dataeaum.size() < 9) {
            pop.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        } else {
            pop.setHeight((int) (screenHight * 0.5));
        }
        pop.setFocusable(true);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pop.dismiss();
                currentPage = 1;
                town_id=mlist_county_id.get(position);
                dropDownView.setText(dataeaum.get(position));
            }
        });
        return pop;
    }


    public void checkList() {
        packYjZqInfoListUp.page = String.valueOf(currentPage);
        packYjZqInfoListUp.sub_id = town_id;
        PcsDataDownload.addDownload(packYjZqInfoListUp);
    }

    public void RefreshDate() {
        list.clear();
        packYjZqInfoListUp.page = String.valueOf(currentPage);
        packYjZqInfoListUp.sub_id = town_id;
        PcsDataDownload.addDownload(packYjZqInfoListUp);
    }

    private PackYjZqInfoListUp packYjZqInfoListUp = new PackYjZqInfoListUp();

    private PcsDataBrocastReceiver mReceiver = new PcsDataBrocastReceiver() {
        @Override
        public void onReceive(String nameStr, String errorStr) {
            if (TextUtils.isEmpty(nameStr)) {
                return;
            }
            if (nameStr.equals(packYjZqInfoListUp.getName())) {
                dismissProgressDialog();
                PackYjZqInfoListDown packDown = (PackYjZqInfoListDown) PcsDataManager.getInstance().getNetPack(nameStr);
                if (packDown == null) {
                    return;
                }
                tv_info_content.setText(packDown.desc);
                if (isFirstReq) {
                    if (packDown.list_2.size() == 0 || packDown.list_2 == null) {
                        tv_info_list.setVisibility(View.VISIBLE);
                        lv_disaster_info.setVisibility(View.GONE);
                    }
                }
                if (packDown.list_2 != null && packDown.list_2.size() > 0) {
                    tv_info_list.setVisibility(View.GONE);
                    lv_disaster_info.setVisibility(View.VISIBLE);
                    list.addAll(packDown.list_2);
                    int count = Integer.parseInt(COUNT);
                    // 当请求回来的数据列表小于count时则表示已无更多数据了，所以不需要再请求并隐藏加载更多
                    if (packDown.list_2.size() < count) {
                        adapter.setLoadingVisibility(false);
                        isReqFinish = true;
                    } else {
                        isReqFinish = false;
                        adapter.setLoadingVisibility(true);
                    }
                    adapter.notifyDataSetChanged();
                } else if (packDown.list_2.size() == 0) {
                    adapter.notifyDataSetChanged();
                }
            }
        }
    };


    public void showProgressDialog() {
        showProgressDialog("请等待...");
    }

    /**
     * 取消等待对话框
     */
    public void dismissProgressDialog() {
        if (mProgress != null && mProgress.isShowing()) {
            mProgress.dismiss();
        }
    }

    /**
     * 显示等待对话框
     */
    public void showProgressDialog(String keyWord) {
        if (mProgress == null) {
            mProgress = new ProgressDialog(getActivity());
            mProgress.setCancelable(true);
            mProgress.setCanceledOnTouchOutside(false);
            mProgress.setOnCancelListener(mProgressOnCancel);
        }
        if (mProgress.isShowing()) {
            mProgress.setMessage(keyWord);
        } else {
            mProgress.show();
            mProgress.setMessage(keyWord);
        }
    }

    /**
     * 进度框OnCancel
     */
    private DialogInterface.OnCancelListener mProgressOnCancel = new DialogInterface.OnCancelListener() {
        @Override
        public void onCancel(DialogInterface dialog) {
            getActivity().finish();
        }
    };

}
