package com.pcs.ztqtj.view.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib.lib_pcs_v3.model.image.ImageConstant;
import com.pcs.lib.lib_pcs_v3.model.image.ImageFetcher;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalUser;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackBannerDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackBannerUp;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.ServiceLoginTool;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.view.activity.ActivityMain;
import com.pcs.ztqtj.view.activity.help.ActivityHelp;
import com.pcs.ztqtj.view.activity.service.AcitvityServeLogin;
import com.pcs.ztqtj.view.activity.service.ActivityMyServer;
import com.pcs.ztqtj.view.activity.service.ActivityServerHyqx;
import com.pcs.ztqtj.view.activity.service.ActivityServerSecond;
import com.pcs.ztqtj.view.activity.web.webview.ActivityWebView;

import java.lang.reflect.Field;

/**
 * 专项服务
 */
public class FragmentService extends Fragment implements OnClickListener {

    private ImageButton imageghelp;
    private Button loginBtn;
    private Button logoutBtn;
    //是否是第一次进入该页面
    private boolean isFirst = true;
    private MyReceiver receiver = new MyReceiver();
    private PackLocalUser localUserinfo;
    private ImageFetcher mImageFetcher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        isFirst = true;
        return inflater.inflate(R.layout.fragment_service, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ActivityMain activity = (ActivityMain) getActivity();
        mImageFetcher = activity.getImageFetcher();
        initView();
        initData();
        initListener();
//        if (!TextUtils.isEmpty(localUserinfo.user_id)) {
//            if (TextUtils.isEmpty(area_id)) {
//                gotoService();
//            }
//        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case ServiceLoginTool.SERVICE_RESULT:
                    isFirst = false;
                    localUserinfo = ZtqCityDB.getInstance().getMyInfo();
                    if (!TextUtils.isEmpty(localUserinfo.user_id)) {
                        if (TextUtils.isEmpty(area_id)) {
                            gotoService();
                        } else {
                            toServiceActivity(area_id);
                        }
                        logoutBtn.setText("退出");
                    }
                    break;
            }
        }
    }

    public void initView() {
        imageghelp = (ImageButton) getView().findViewById(R.id.imageghelp);
        loginBtn = (Button) getView().findViewById(R.id.bt_service_jc);
        logoutBtn = (Button) getView().findViewById(R.id.bt_service_login);
        Button bt_service_hy = getView().findViewById(R.id.bt_service_hy);
        bt_service_hy.setOnClickListener(this);
        TextView tv_service_tj = (TextView) getView().findViewById(R.id.tv_service_tj);
        tv_service_tj.setOnClickListener(this);
        TextView tv_bhxq = (TextView) getView().findViewById(R.id.tv_bhxq);
        tv_bhxq.setOnClickListener(this);
        TextView tv_dlq = (TextView) getView().findViewById(R.id.tv_dlq);
        tv_dlq.setOnClickListener(this);
        TextView tv_xqq = (TextView) getView().findViewById(R.id.tv_xqq);
        tv_xqq.setOnClickListener(this);
        TextView tv_jnq = (TextView) getView().findViewById(R.id.tv_jnq);
        tv_jnq.setOnClickListener(this);
        TextView tv_bcq = (TextView) getView().findViewById(R.id.tv_bcq);
        tv_bcq.setOnClickListener(this);
        TextView tv_wqq = (TextView) getView().findViewById(R.id.tv_wqq);
        tv_wqq.setOnClickListener(this);
        TextView tv_bdq = (TextView) getView().findViewById(R.id.tv_bdq);
        tv_bdq.setOnClickListener(this);
        TextView tv_nhq = (TextView) getView().findViewById(R.id.tv_nhq);
        tv_nhq.setOnClickListener(this);
        TextView tv_jhq = (TextView) getView().findViewById(R.id.tv_jhq);
        tv_jhq.setOnClickListener(this);
        TextView tv_jzq = (TextView) getView().findViewById(R.id.tv_jzq);
        tv_jzq.setOnClickListener(this);
        TextView tv_service_person_office = getView().findViewById(R.id.tv_service_person_office);
        tv_service_person_office.setOnClickListener(this);
        TextView tv_service_weather_office = getView().findViewById(R.id.tv_service_weather_office);
        tv_service_weather_office.setOnClickListener(this);
        ImageView iv_service_ad = getView().findViewById(R.id.iv_service_ad);
        iv_service_ad.setOnClickListener(this);
        refreshAD("35", iv_service_ad);
    }

    @Override
    public void onResume() {
        super.onResume();
        reflashUserData();
    }

    private void initData() {
        if (receiver == null) {
            receiver = new MyReceiver();
        }
        PcsDataBrocastReceiver.registerReceiver(getActivity(), receiver);
        reflashUserData();

        if (localUserinfo != null && !TextUtils.isEmpty(localUserinfo.user_id)) {
            ServiceLoginTool.getInstance().reqLoginQuery();
        }

        if (!TextUtils.isEmpty(localUserinfo.user_id)) {
            logoutBtn.setText("退出");
        }else{
            logoutBtn.setText("登录");
        }
    }

    public void initListener() {
        imageghelp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoHelp();
            }
        });
        loginBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (localUserinfo == null || TextUtils.isEmpty(localUserinfo.user_id)) {
                    gotoLogin();
                } else {
                    ServiceLoginTool.getInstance().reqLoginQuery();
                    isFirst = false;
                }
            }
        });
        logoutBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (logoutBtn.getText().toString().equals("登录")) {
                    gotoLogin();
                } else {
                    logout();
                    logoutBtn.setText("登录");
                }
            }
        });
    }

    private void logout() {
        ZtqCityDB.getInstance().removeMyInfo();
        localUserinfo = ZtqCityDB.getInstance().getMyInfo();
        localUserinfo.user_id = null;
        localUserinfo.user_id = "";
        reflashUserData();
    }

    private void gotoLogin() {
        try {
            Intent intent = new Intent(getActivity(), AcitvityServeLogin.class);
            startActivityForResult(intent, ServiceLoginTool.SERVICE_RESULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void gotoHelp() {
        Intent intent = new Intent(getActivity(), ActivityHelp.class);
        startActivity(intent);
    }

    private void reflashUserData() {
        localUserinfo = ZtqCityDB.getInstance().getMyInfo();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_service_tj:
                toServiceActivity("101030303");
                break;
            case R.id.tv_bhxq:
                toServiceActivity("101030305");
                break;
            case R.id.tv_dlq:
                toServiceActivity("101030306");
                break;
            case R.id.tv_xqq:
                toServiceActivity("101030307");
                break;
            case R.id.tv_jnq:
                toServiceActivity("101030308");
                break;
            case R.id.tv_bcq:
                toServiceActivity("101030309");
                break;
            case R.id.tv_wqq:
                toServiceActivity("101030310");
                break;
            case R.id.tv_bdq:
                toServiceActivity("101030311");
                break;
            case R.id.tv_nhq:
                toServiceActivity("101030313");
                break;
            case R.id.tv_jhq:
                toServiceActivity("101030312");
                break;
            case R.id.tv_jzq:
                toServiceActivity("101030314");
                break;
            case R.id.tv_service_person_office:
                toServiceActivity("101030304");
                break;
            case R.id.tv_service_weather_office:
                toServiceActivity("3603");
                break;
            case R.id.iv_service_ad:
                clickAD("35");
                break;
            case R.id.bt_service_hy:
                startActivity(new Intent(getActivity(), ActivityServerHyqx.class));
                break;
        }
    }

    public void toServiceActivity(String areaid) {
        Intent intent = new Intent(getActivity(), ActivityServerSecond.class);
        intent.putExtra("area_id", areaid);
        startActivity(intent);
    }

    //上传包：广告
    private PackBannerUp mPackBannerUp = new PackBannerUp();

    /**
     * 刷新广告
     *
     * @param position_id 广告ID
     * @param imageView
     */
    private void refreshAD(String position_id, ImageView imageView) {
        PackLocalUser info = ZtqCityDB.getInstance().getMyInfo();
        if (!TextUtils.isEmpty(info.user_id)) {
            mPackBannerUp.position_id = position_id;
            PackBannerDown packDown = (PackBannerDown) PcsDataManager.getInstance().getNetPack(mPackBannerUp.getName());
            if (packDown == null || packDown.arrBannerInfo.size() == 0) {
                imageView.setVisibility(View.GONE);
                return;
            } else {
                imageView.setVisibility(View.VISIBLE);
            }
            String url = getResources().getString(R.string.file_download_url) + packDown.arrBannerInfo.get(0).img_path;
            mImageFetcher.loadImage(url, imageView, ImageConstant.ImageShowType.SRC);
        }

    }

    //点击广告
    private void clickAD(String position_id) {
        mPackBannerUp.position_id = position_id;
        PackBannerDown packDown = (PackBannerDown) PcsDataManager.getInstance().getNetPack(mPackBannerUp.getName());
        if (packDown == null || packDown.arrBannerInfo.size() == 0) {
            return;
        }

        Intent it = new Intent(getActivity(), ActivityWebView.class);
        it.putExtra("title", packDown.arrBannerInfo.get(0).title);
        it.putExtra("url", packDown.arrBannerInfo.get(0).url);
        it.putExtra("shareContent", packDown.arrBannerInfo.get(0).fx_content);
        startActivity(it);
    }

    /**
     * 数据更新广播接收
     */
    private class MyReceiver extends PcsDataBrocastReceiver {
        @Override
        public void onReceive(String name, String error) {
            if (!TextUtils.isEmpty(error)) {
                return;
            }
            ServiceLoginTool.getInstance().callback(name, new ServiceLoginTool.CheckListener() {
                @Override
                public void onSuccess() {
                    if (isFirst) {
                        isFirst = false;
                    } else {
                        gotoService();
                    }
                }

                @Override
                public void onFail() {
                    logout();
                    if (!isFirst) {
                        ServiceLoginTool.getInstance().createAlreadyLogined(getActivity(), new ServiceLoginTool.DialogClickListener() {
                            @Override
                            public void onPositive() {
                                gotoHelp();
                            }

                            @Override
                            public void onNegative() {
                                gotoLogin();
                            }
                        });
                    }
                }
            });

        }

    }

    private void gotoService() {
        Intent intent = new Intent(getActivity(), ActivityMyServer.class);
        intent.putExtra("channel", "");
        intent.putExtra("title", "我的服务");
        intent.putExtra("subtitle", "1");
        startActivity(intent);
    }

    public String area_id = null;

    public void onStart() {
        super.onStart();
        if (receiver == null) {
            receiver = new MyReceiver();
        }
        PcsDataBrocastReceiver.registerReceiver(getActivity(), receiver);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (receiver != null) {
            PcsDataBrocastReceiver.unregisterReceiver(getActivity(), receiver);
            receiver = null;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (receiver != null) {
            getActivity().unregisterReceiver(receiver);
            receiver = null;
        }
    }

    /**
     * java.lang.IllegalStateException: No activity 错误解决方案
     */
    @Override
    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}