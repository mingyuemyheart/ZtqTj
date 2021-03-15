package com.pcs.ztqtj.view.activity.service;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.AdapterWeatherServeThrid;
import com.pcs.ztqtj.control.tool.MyConfigure;
import com.pcs.ztqtj.control.tool.ServiceLoginTool;
import com.pcs.ztqtj.control.tool.SharedPreferencesUtil;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.view.activity.help.ActivityHelp;
import com.pcs.ztqtj.view.activity.web.FragmentActivityZtqWithHelp;
import com.pcs.ztqtj.view.dialog.DialogFactory.DialogListener;
import com.pcs.ztqtj.view.dialog.DialogTwoButton;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalUser;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalUserInfo;
import com.pcs.lib_ztqfj_v2.model.pack.net.service.PackSHThreeProductDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.service.PackSHThreeProductUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.service.PackServiceMyProductDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.service.PackServiceMyProductUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.service.PackServiceThreeProductDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.service.PackServiceThreeProductUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.service.ServiceProductInfo;

import java.util.ArrayList;
import java.util.List;


/**
 * 气象服务 产品
 *
 * @author chenjh
 */
public class ActivityServeThird extends FragmentActivityZtqWithHelp {

    private TextView tip_title_tv;
    private ListView myListView;

    private AdapterWeatherServeThrid mAdapter;
    private MyReceiver receiver = new MyReceiver();
    private PackServiceMyProductUp packServiceMyProductUp = new PackServiceMyProductUp();

    private PackServiceThreeProductUp packServiceThreeProductUp = new PackServiceThreeProductUp();

    private PackSHThreeProductUp packSHThreeProductUp=new PackSHThreeProductUp();

    /**
     * 产品列表
     **/
    private List<ServiceProductInfo> serviceProductList = new ArrayList<ServiceProductInfo>();

    /**
     * 传入的每页大小
     **/
    private int page_size = 15;
    /**
     * 上次传入的页数
     **/
    private int page_num = 1;
    /**
     * 总页数
     **/
    private int total_page = 0;
    /**
     * 总条数
     **/
    private int total_count = 0;

    private String channel_id = "";

    private String area_id = "";
    private String area_name = "";

    private String title = "";

    private DialogTwoButton myDialog;
    private TextView messageTextView;
    private DialogTwoButton myDialog2;
    private TextView messageTextView2;
    private PackLocalUser localUserinfo;

    // 是否跳转详情页
    private boolean isGotoDetail = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.serve_second_activity);
        initView();
        initEvent();
        initData();
    }

    private void initView() {
        tip_title_tv = (TextView) findViewById(R.id.tip_title_tv);
        myListView = (ListView) findViewById(R.id.mylistviw);
    }

    private void initEvent() {
        myListView.setOnItemClickListener(myOnItemClickListener);
        myListView.setOnScrollListener(myOnScrollListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ServiceLoginTool.SERVICE_RESULT) {
            if (resultCode == RESULT_OK) {
                PackLocalUser info = ZtqCityDB.getInstance().getMyInfo();
                if(info != null && !TextUtils.isEmpty(info.user_id)) {

                    //gotoDetail();
                    showProgressDialog();
                    serviceProductList.clear();
                    page_num = 1;
                    isGotoDetail = true;
                    localUserinfo = ZtqCityDB.getInstance().getMyInfo();
                    reqServiceProductList();
                }
            }
        }
    }

    private boolean show_warn = true;

    private boolean isMyService = false;

    private void initData() {
        showProgressDialog();
        PcsDataBrocastReceiver.registerReceiver(this, receiver);
        channel_id = getIntent().getStringExtra("channel_id");
        area_id = getIntent().getStringExtra("area_id");
        title = getIntent().getStringExtra("title");
        area_name = getIntent().getStringExtra("area_name");
        show_warn = getIntent().getBooleanExtra("show_warn", true);
        setTitleText(title);
        // area_id =
        // ZtqCityDB.getInstance().getCurrentCityInfo().getCurrentCity().ID;
        isMyService = getIntent().getBooleanExtra("MY_SERVICE", false);
        localUserinfo = ZtqCityDB.getInstance().getMyInfo();
        if (isMyService && !TextUtils.isEmpty(localUserinfo.user_id)) {
            reqMyServiceProductList();
        } else {
            reqServiceProductList();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAdapter!=null){
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 获取三级栏目下产品
     **/
    private void reqServiceProductList() {
        if(!isOpenNet()){
            showToast(getString(R.string.net_err));
            return ;
        }
        if (area_id.equals("00")){
            if (packSHThreeProductUp == null) {
                packSHThreeProductUp = new PackSHThreeProductUp();
            }
            if (!TextUtils.isEmpty(packSHThreeProductUp.page_num)) {
                if (packSHThreeProductUp.page_num.equals(page_num + "") && !isGotoDetail) {
                    return;
                }
            }
            packSHThreeProductUp.page_num = page_num + "";
            packSHThreeProductUp.page_size = page_size + "";
            packSHThreeProductUp.channel_id = channel_id;
            packSHThreeProductUp.user_id = localUserinfo.user_id;
            PcsDataDownload.addDownload(packSHThreeProductUp);
        }else{
            if (packServiceThreeProductUp == null) {
                packServiceThreeProductUp = new PackServiceThreeProductUp();
            }
            if (!TextUtils.isEmpty(packServiceThreeProductUp.page_num)) {
                if (packServiceThreeProductUp.page_num.equals(page_num + "") && !isGotoDetail) {
                    return;
                }
            }
            packServiceThreeProductUp.page_num = page_num + "";
            packServiceThreeProductUp.page_size = page_size + "";
            packServiceThreeProductUp.area_id = area_id;
            packServiceThreeProductUp.channel_id = channel_id;
            packServiceThreeProductUp.user_id = localUserinfo.user_id;
            PcsDataDownload.addDownload(packServiceThreeProductUp);
        }
    }

    /**
     * 获取我的服务
     **/
    private void reqMyServiceProductList() {
        if(!isOpenNet()){
            showToast(getString(R.string.net_err));
            return ;
        }
        if (packServiceMyProductUp == null) {
            packServiceMyProductUp = new PackServiceMyProductUp();
        }
        if (!TextUtils.isEmpty(packServiceMyProductUp.page_num)) {
            if (packServiceMyProductUp.page_num.equals(page_num + "")) {
                return;
            }
        }
        packServiceMyProductUp.page_num = page_num + "";
        packServiceMyProductUp.page_size = page_size + "";
        packServiceMyProductUp.user_id = localUserinfo.user_id;

        PcsDataDownload.addDownload(packServiceMyProductUp);

    }

    private void updateListData(List<ServiceProductInfo> dataList) {
        if (dataList.size() > 0) {
            if (mAdapter == null) {
                mAdapter = new AdapterWeatherServeThrid(
                        getApplicationContext(), dataList);
                myListView.setAdapter(mAdapter);
            } else {
                mAdapter.setData(dataList);
            }

            mAdapter.notifyDataSetChanged();
            tip_title_tv.setVisibility(View.GONE);
            myListView.setVisibility(View.VISIBLE);
        } else {
            myListView.setVisibility(View.GONE);
            tip_title_tv.setVisibility(View.VISIBLE);
        }
    }

    private String article_title = "";
    private int currentPosition = -1;

    /**
     * 跳转下级页面
     */
    private void gotoDetail() {
        if(currentPosition != -1) {
            ServiceProductInfo info = serviceProductList.get(currentPosition);
            article_title = area_name + "发布了《" + info.title + "》，请查阅。";

            if ("1".equals(info.type)) {// 用户是否能用: 0,不可用 ;1,可用.
                //存储点击的已读跟未读的状态
                SharedPreferencesUtil.putData(info.html_url,info.html_url);
                Intent intent = new Intent(ActivityServeThird.this,
                        ActivityServeDetails.class);
                intent.putExtra("title", title);
                intent.putExtra("url", info.html_url);
                intent.putExtra("style", info.style);
                intent.putExtra("show_warn", show_warn);
                intent.putExtra("article_title", article_title);
                startActivity(intent);
            } else {
                showCheckTipsDialog();
            }
        }
    }

    private OnItemClickListener myOnItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            currentPosition = position;

            // 当前未登陆的情况直接显示登陆提示弹窗
            if(TextUtils.isEmpty(ZtqCityDB.getInstance().getMyInfo().user_id)) {
                ServiceLoginTool.getInstance().createAlreadyLogined(ActivityServeThird.this);
            } else {
                ServiceLoginTool.getInstance().reqLoginQuery();
            }


//            ServiceProductInfo info = serviceProductList.get(position);
//            article_title = area_name + "发布了《" + info.title + "》，请查阅。";
//            if (localUserinfo == null || TextUtils.isEmpty(localUserinfo.user_id)) {
//                showLoginTipsDialog();
//            } else {
//                if ("1".equals(info.type)) {// 用户是否能用: 0,不可用 ;1,可用.
//                    Intent intent = new Intent(ActivityServeThird.this,
//                            ActivityServeDetails.class);
//                    intent.putExtra("title", title);
//                    intent.putExtra("url", info.html_url);
//                    intent.putExtra("style", info.style);
//                    intent.putExtra("show_warn", show_warn);
//                    intent.putExtra("article_title", article_title);
//                    startActivity(intent);
//                } else {
//                    showCheckTipsDialog();
//                }
//                return;
//            }
        }
    };

    protected void onStart() {
        super.onStart();
        localUserinfo = ZtqCityDB.getInstance().getMyInfo();
    }

    /**
     * 登出当前账号
     */
    private void logout() {
        PackLocalUserInfo info = new PackLocalUserInfo();
        ZtqCityDB.getInstance().setMyInfo(info);
    }

    /**
     * 数据更新广播接收
     */
    private class MyReceiver extends PcsDataBrocastReceiver {

        @Override
        public void onReceive(String name, String error) {
            dismissProgressDialog();
            if (!TextUtils.isEmpty(error)) {
                return;
            }
            if (packServiceMyProductUp != null && packServiceMyProductUp.getName().equals(name)) {
                PackServiceMyProductDown packServiceMyProductDown = (PackServiceMyProductDown) PcsDataManager.getInstance().getNetPack(name);
                if (packServiceMyProductDown == null) {
                    return;
                }
                if (!TextUtils.isEmpty(packServiceMyProductDown.total_page)) {
                    total_page = Integer
                            .valueOf(packServiceMyProductDown.total_page);
                }
                if (!TextUtils.isEmpty(packServiceMyProductDown.total_count)) {
                    total_count = Integer
                            .valueOf(packServiceMyProductDown.total_count);
                }
                if (!TextUtils.isEmpty(packServiceMyProductDown.page_size)) {
                    page_size = Integer
                            .valueOf(packServiceMyProductDown.page_size);
                }
                if (!TextUtils.isEmpty(packServiceMyProductDown.page_num)) {
                    page_num = Integer
                            .valueOf(packServiceMyProductDown.page_num);
                }

                for (int i = 0; i < packServiceMyProductDown.myServiceProductList
                        .size(); i++) {
                    serviceProductList
                            .add(packServiceMyProductDown.myServiceProductList
                                    .get(i));
                }


                if (packServiceMyProductDown.myServiceProductList.size() > 0) {
                    System.out.println("有更多数据");
                    if (serviceProductList.size() < page_size) {
                        page_num = 1;
                    } else {
                        page_num++;
                    }
                    updateListData(serviceProductList);
                } else {
                    System.out.println("无更多数据");
                }
            } else if (packServiceThreeProductUp != null && packServiceThreeProductUp.getName().equals(name)) {
                PackServiceThreeProductDown packServiceThreeProductDown = (PackServiceThreeProductDown) PcsDataManager.getInstance().getNetPack(name);
                if (packServiceThreeProductDown == null) {
                    return;
                }
                if (!TextUtils.isEmpty(packServiceThreeProductDown.total_page)) {
                    total_page = Integer
                            .valueOf(packServiceThreeProductDown.total_page);
                }
                if (!TextUtils.isEmpty(packServiceThreeProductDown.total_count)) {
                    total_count = Integer
                            .valueOf(packServiceThreeProductDown.total_count);
                }
                if (!TextUtils.isEmpty(packServiceThreeProductDown.page_size)) {
                    page_size = Integer
                            .valueOf(packServiceThreeProductDown.page_size);
                }
                if (!TextUtils.isEmpty(packServiceThreeProductDown.page_num)) {
                    page_num = Integer
                            .valueOf(packServiceThreeProductDown.page_num);
                }
                for (int i = 0; i < packServiceThreeProductDown.serviceProductList
                        .size(); i++) {
                    serviceProductList
                            .add(packServiceThreeProductDown.serviceProductList
                                    .get(i));
                }
                if (packServiceThreeProductDown.serviceProductList.size() > 0) {
                    System.out.println("有更多数据");
                    if (serviceProductList.size() < page_size) {
                        page_num = 1;
                    } else {
                        page_num++;
                    }
                    updateListData(serviceProductList);
//                    if(isGotoDetail) {
//                        gotoDetail();
//                        isGotoDetail = false;
//                    }
                } else {
                    System.out.println("无更多数据");
                }
            }else if (packSHThreeProductUp != null && packSHThreeProductUp.getName().equals(name)) {
                PackSHThreeProductDown packSHThreeProductDown = (PackSHThreeProductDown) PcsDataManager.getInstance().getNetPack(name);
                if (packSHThreeProductDown == null) {
                    return;
                }
                if (!TextUtils.isEmpty(packSHThreeProductUp.page_size)) {
                    page_size = Integer
                            .valueOf(packSHThreeProductUp.page_size);
                }
                if (!TextUtils.isEmpty(packSHThreeProductUp.page_num)) {
                    page_num = Integer
                            .valueOf(packSHThreeProductUp.page_num);
                }
                for (int i = 0; i < packSHThreeProductDown.serviceProductList
                        .size(); i++) {
                    serviceProductList
                            .add(packSHThreeProductDown.serviceProductList
                                    .get(i));
                }
                if (packSHThreeProductDown.serviceProductList.size() > 0) {
                    if (serviceProductList.size() < page_size) {
                        page_num = 1;
                    } else {
                        page_num++;
                    }
                    updateListData(serviceProductList);
                } else {
                    System.out.println("无更多数据");
                }
            }

            ServiceLoginTool.getInstance().callback(name, new ServiceLoginTool.CheckListener() {
                @Override
                public void onSuccess() {
                    gotoDetail();
                }

                @Override
                public void onFail() {
                    logout();
                    ServiceLoginTool.getInstance().createAlreadyLogined(ActivityServeThird.this);
                }
            });

        }
    }

    private OnScrollListener myOnScrollListener = new OnScrollListener() {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            // 当不滚动时
            if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
                // 判断是否滚动到底部
                if (view.getLastVisiblePosition() == view.getCount() - 1) {
                    // 加载更多功能的代码
                    System.out.println("到了底部，加载更多");

                    if (isMyService && !TextUtils.isEmpty(localUserinfo.user_id)) {
                        reqMyServiceProductList();
                    } else {
                        reqServiceProductList();
                    }
                }
            }
        }

        @Override
        public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {

        }
    };

    private void showLoginTipsDialog() {

        if (myDialog == null) {
            View view = LayoutInflater.from(ActivityServeThird.this).inflate(
                    R.layout.dialog_message, null);
            messageTextView = (TextView) view.findViewById(R.id.dialogmessage);

            messageTextView.setText(R.string.text_islogin_tips);
            myDialog = new DialogTwoButton(ActivityServeThird.this, view, "登录",
                    "帮助", new DialogListener() {
                @Override
                public void click(String str) {
                    myDialog.dismiss();
                    if (str.equals("登录")) {
                        Intent intent = null;
                        intent = new Intent(ActivityServeThird.this,
                                AcitvityServeLogin.class);
                        startActivityForResult(intent,
                                MyConfigure.RESULT_SERVICE_THREE);
                    } else if (str.equals("帮助")) {
                        Intent intent = null;
                        intent = new Intent(ActivityServeThird.this,
                                ActivityHelp.class);
                        startActivityForResult(intent,
                                MyConfigure.RESULT_SERVICE_THREE);
                    }
                }
            });
            myDialog.setTitle("天津气象提示");
            messageTextView.setTextColor(getResources().getColor(
                    R.color.text_color));
            myDialog.showCloseBtn();
        }

        myDialog.show();
    }

    private void showCheckTipsDialog() {

        if (myDialog2 == null) {
            View view = LayoutInflater.from(ActivityServeThird.this).inflate(
                    R.layout.dialog_message, null);
            messageTextView2 = (TextView) view.findViewById(R.id.dialogmessage);

            messageTextView2.setText(R.string.text_authority_tips);
            myDialog2 = new DialogTwoButton(ActivityServeThird.this, view,
                    "帮助", "返回", new DialogListener() {
                @Override
                public void click(String str) {
                    myDialog2.dismiss();
                    if (str.equals("帮助")) {
                        Intent intent = null;
                        intent = new Intent(ActivityServeThird.this,
                                ActivityHelp.class);
                        startActivityForResult(intent,
                                MyConfigure.RESULT_SERVICE_THREE);
                    }
                }
            });
            myDialog2.setTitle("天津气象提示");
            messageTextView2.setTextColor(getResources().getColor(
                    R.color.text_color));
            myDialog2.showCloseBtn();
        }

        myDialog2.show();
    }

    private void finishView() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onBackPressed() {
        finishView();
        super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(receiver);
    }

}
