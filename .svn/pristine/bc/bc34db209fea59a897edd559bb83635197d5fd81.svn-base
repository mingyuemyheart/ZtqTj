package com.pcs.ztqtj.view.activity.product.lightning;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.AdapterThunderMoreList;
import com.pcs.ztqtj.control.inter.InterfaceRefresh;
import com.pcs.ztqtj.control.listener.ListenerRefreshTouch;
import com.pcs.ztqtj.control.listener.ListenerRefreshTouch.InterfaceScrollView;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
import com.pcs.ztqtj.view.myview.ViewPulldownRefresh;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.net.lightning.PackLocalThunderQuireDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.lightning.PackThunderMoreListDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.lightning.PackThunderMoreListDown.AreaInfo;
import com.pcs.lib_ztqfj_v2.model.pack.net.lightning.PackThunderMoreListUp;

import java.util.ArrayList;
import java.util.List;

/**
 * 雷电数据统计 Chensq 2015年11月13日
 */
public class ActivityThunderMoreList extends FragmentActivityZtqBase {

    /**
     * 子城市信息
     */
    public ListView data_lightning_list;
    /**
     * 闪电活动综述
     */
    public TextView tv_lightning_review;
    public PackThunderMoreListDown packThunderMoreListDown;
    public PackThunderMoreListUp packThunderMoreListUp;
    public AdapterThunderMoreList adapterThunderMoreList;
    public PackLocalThunderQuireDown packLocalThunderQuireDown;
    private Bundle bundle;
    // 是否允许滚动？
    private boolean mIsScrollable = true;
    // 正在加载？
    private boolean mIsLoading = true;
    /**
     * 当前页
     */
    private int mCurrPageSpecial = 1;

    private boolean misPackDown = false;// 数据是否都加载完成

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_lightning);
        setTitleText("雷电数据统计");
        bundle = getIntent().getExtras();
        registerReceiver();

        request();
        initView();
        initData();
    }

    public void initView() {
        tv_lightning_review = (TextView) findViewById(R.id.tv_lightning_review);
        data_lightning_list = (ListView) findViewById(R.id.data_lightning_list);
        // 下拉视图
        View pullLayout = findViewById(R.id.layout_pulldowns);
        ViewPulldownRefresh viewPullDown = new ViewPulldownRefresh(this,
                pullLayout);
        // 滚动监听
        MyOnScrollListener refreshScroll = new MyOnScrollListener();
        data_lightning_list.setOnScrollListener(refreshScroll);
        // 触摸监听
        MyListenerRefreshTouch refreshTouch = new MyListenerRefreshTouch(
                this.getWindowManager(), viewPullDown, mRefreshEnd,
                mRefreshBegin, refreshScroll);
        data_lightning_list.setOnTouchListener(refreshTouch);
    }

    public void initData() {
        List<AreaInfo> list = new ArrayList<AreaInfo>();
        if (packThunderMoreListDown != null) {
            list = packThunderMoreListDown.area_info_list;
        }
        adapterThunderMoreList = new AdapterThunderMoreList(
                ActivityThunderMoreList.this, list);
        data_lightning_list.setAdapter(adapterThunderMoreList);
        if (packThunderMoreListDown != null
                && !TextUtils.isEmpty(packThunderMoreListDown.des)) {
            tv_lightning_review.setText(packThunderMoreListDown.des);
        } else {
            //tv_lightning_review.setText("暂无闪电活动综述！");
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver();
    }

    /**
     * 设置底部加载View
     *
     * @param isShow
     */
    private void setBottomView(boolean isShow) {
        TextView view = (TextView) findViewById(R.id.text_bottom);
        if (isShow) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    /**
     * 通过Bottomview的显示与否来判断是否正在加载数据
     *
     * @return
     */
    private boolean isLoading() {
        TextView view = (TextView) findViewById(R.id.text_bottom);
        switch (view.getVisibility()) {
            case View.VISIBLE:
                return true;
            case View.INVISIBLE:
            case View.GONE:
                return false;
        }
        return false;
    }

    /**
     * 请求数据
     */
    public void request() {

        if(!isOpenNet()){
            showToast(getString(R.string.net_err));
            return ;
        }
        showProgressDialog();
        packThunderMoreListUp = new PackThunderMoreListUp();
        packThunderMoreListUp.start_time = bundle.getString("start_time");// 开始年月日时分
        packThunderMoreListUp.end_time = bundle.getString("end_time");// 结束年月日时分
        packThunderMoreListUp.cg_ic = bundle.getString("cg_ic");// ic:云闪 cg:地闪
        packThunderMoreListUp.code = bundle.getString("code");// 城市id或地区id
        packThunderMoreListUp.type = bundle.getString("type");// 1表示地区，2表示城市
        packThunderMoreListUp.processflag = bundle.getString("processflag");// 1:正闪
        // -1:负闪
        packThunderMoreListUp.page = "1";// 页数
        PcsDataDownload.addDownload(packThunderMoreListUp);
    }

    private PcsDataBrocastReceiver mReceiver = new PcsDataBrocastReceiver() {

        @Override
        public void onReceive(String nameStr, String errorStr) {
            if (packThunderMoreListUp.getName().equals(nameStr)) {
                packThunderMoreListDown = (PackThunderMoreListDown) PcsDataManager.getInstance().getNetPack(PackThunderMoreListUp.NAME);
                if (packThunderMoreListDown == null) {
                    return;
                }
                if (packThunderMoreListDown != null
                        && !TextUtils.isEmpty(packThunderMoreListDown.des)) {
                    tv_lightning_review.setText(packThunderMoreListDown.des);
                } else {
                    tv_lightning_review.setText("暂无闪电活动综述！");
                }

                adapterThunderMoreList
                        .setData(packThunderMoreListDown.area_info_list);
                mCurrPageSpecial++;
                if (packThunderMoreListDown != null
                        && packThunderMoreListDown.area_info_list.size() == 0) {
                    mIsLoading = true;
                    misPackDown = true;// 表示没有数据
                } else {
                    mIsLoading = false;
                    misPackDown = false;// 表示有数据
                }
                // 底部加载View
                setBottomView(false);

            }
            dismissProgressDialog();
        }
    };


    /**
     * 请求下一条
     */
    private void requestNex() {

        if(!isOpenNet()){
            showToast(getString(R.string.net_err));
            return ;
        }
        packThunderMoreListUp.page = Integer.toString(mCurrPageSpecial);
        PcsDataDownload.addDownload(packThunderMoreListUp);
    }

    /**
     * 注册广播
     */
    public void registerReceiver() {
        PcsDataBrocastReceiver.registerReceiver(this, mReceiver);
    }

    /**
     * 注销广播
     */
    public void unregisterReceiver() {
        PcsDataBrocastReceiver.unregisterReceiver(this, mReceiver);
    }

    /**
     * 滚动触摸监听
     */
    private class MyListenerRefreshTouch extends ListenerRefreshTouch {
        public MyListenerRefreshTouch(WindowManager windowManager,
                                      InterfacePulldownView pulldownView,
                                      InterfaceRefresh refreshView, InterfaceRefresh refreshAnim,
                                      InterfaceScrollView scrollView) {
            super(windowManager, pulldownView, refreshView, refreshAnim,
                    scrollView);
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            boolean b = super.onTouch(v, event);
            return b || isLoading();
            //return b || !mIsScrollable || mIsLoading;
        }
    }

    /**
     * 滚动监听
     *
     * @author JiangZy
     */
    private class MyOnScrollListener implements OnScrollListener,
            InterfaceScrollView {
        private int mFirstVisibleItem = 0;
        private int mVisibleItemCount = 0;
        private int mTotalItemCount = 0;
        private boolean isTouchChange = false;

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            // 有触摸
            isTouchChange = true;
            System.out.println("ListView scroll state change" + scrollState);
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {
            mFirstVisibleItem = firstVisibleItem;
            mVisibleItemCount = visibleItemCount;
            mTotalItemCount = totalItemCount;
            // 滚动到底部
            if (isTouchChange && isScrollBottom()) {
                if (misPackDown) {
                    if (mIsLoading) {
                        Toast.makeText(ActivityThunderMoreList.this, "已加载完数据!", Toast.LENGTH_SHORT).show();
                    }
                    mIsLoading = false;
                } else {
                    if (!mIsLoading) {
                        requestNex();
                        // 显示正在加载
                        setBottomView(true);
                        mIsLoading = true;
                    }
                }
            }
        }

        /**
         * 是否已滚动到顶部
         *
         * @return
         */
        @Override
        public boolean isScrollTop() {
            return mFirstVisibleItem == 0;

        }

        /**
         * 是否滚动到底部
         *
         * @return
         */
        public boolean isScrollBottom() {
            return mTotalItemCount != 0
                    && mFirstVisibleItem + mVisibleItemCount == mTotalItemCount;
        }

        @Override
        public void setScrollable(boolean b) {
            mIsScrollable = b;
        }
    }

    /**
     * 刷新视图的接口
     */
    public InterfaceRefresh mRefreshEnd = new InterfaceRefresh() {
        @Override
        public void refresh(RefreshParam param) {

        }
    };
    /**
     * 刷新视图的接口
     */
    public InterfaceRefresh mRefreshBegin = new InterfaceRefresh() {
        @Override
        public void refresh(RefreshParam param) {
            mIsLoading = true;
        }
    };

}
