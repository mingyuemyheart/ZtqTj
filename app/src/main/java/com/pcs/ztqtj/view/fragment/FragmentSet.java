package com.pcs.ztqtj.view.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.pcs.lib.lib_pcs_v3.control.file.PcsFileDownload;
import com.pcs.lib.lib_pcs_v3.control.file.PcsFileDownloadListener;
import com.pcs.lib.lib_pcs_v3.control.file.PcsGetPathValue;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityMain;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalSetUpdate;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalUser;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackCheckVersionDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackCheckVersionUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackgetrecommendfriendsmsgDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackgetrecommendfriendsmsgUP;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.AdapterRightSets;
import com.pcs.ztqtj.control.adapter.adapter_set.AdapterColumnManager;
import com.pcs.ztqtj.control.adapter.adapter_set.AdapterSetDialogList;
import com.pcs.ztqtj.control.inter.InterfaceRefresh;
import com.pcs.ztqtj.control.tool.CommUtils;
import com.pcs.ztqtj.control.tool.MyConfigure;
import com.pcs.ztqtj.control.tool.image.GetImageView;
import com.pcs.ztqtj.model.SetsBean;
import com.pcs.ztqtj.model.SettingDB;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.util.AuthorityUtil;
import com.pcs.ztqtj.view.activity.ActivityMain;
import com.pcs.ztqtj.view.activity.photoshow.ActivityLogin;
import com.pcs.ztqtj.view.activity.photoshow.ActivityUserCenter;
import com.pcs.ztqtj.view.activity.prove.WeatherProveActivity;
import com.pcs.ztqtj.view.activity.set.AcitvityAboutZTQ;
import com.pcs.ztqtj.view.activity.set.AcitvityFeedBack;
import com.pcs.ztqtj.view.activity.set.ActivityDisclaimer;
import com.pcs.ztqtj.view.dialog.DialogFactory;
import com.pcs.ztqtj.view.dialog.DialogOneButton;
import com.pcs.ztqtj.view.dialog.DialogTwoButton;
import com.pcs.ztqtj.view.dialog.DialogWaiting;
import com.pcs.ztqtj.view.myview.MyGridView;
import com.pcs.ztqtj.view.myview.MyListView;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 设置
 */
public class FragmentSet extends Fragment implements OnClickListener, InterfaceRefresh {

    private ActivityMain activity;
    private MyReceiver mReceiver = null;
    private Button btnLogin;
    private TextView tvUserName;
    private ImageView ivHome,ivHead;
    private DialogTwoButton dialogClearCache;
    private GetImageView imageTool = new GetImageView();
    private MyListView listView;
    private AdapterColumnManager adapter;
    private List<Map<String, String>> listData = new ArrayList<>();

    private MyGridView gridSets;
    private List<SetsBean> setsBeanList = new ArrayList<>();
    private AdapterRightSets setsAdapter;

    private PackCheckVersionUp uppack;
    private PackCheckVersionDown pack;
    private PackgetrecommendfriendsmsgUP uppackgetshareinfo;
    private PackgetrecommendfriendsmsgDown Downpackgetshareinfo;
    private DialogWaiting getShareFriendDataDialog;
    private PackLocalUser localUserinfo;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (ActivityMain) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_set, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        btnLogin = (Button) getView().findViewById(R.id.btn_login2);
        tvUserName = (TextView) getView().findViewById(R.id.tv_username);
        ivHome = (ImageView) getView().findViewById(R.id.ivHome);
        ivHead = (ImageView) getView().findViewById(R.id.iv_head);
        listView = (MyListView) getView().findViewById(R.id.lv_list);
        gridSets = (MyGridView) getView().findViewById(R.id.grid_set);
        init();
        initListener();
        initUser();
        initData();
        reqNet();
    }

    public void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            PcsDataBrocastReceiver.unregisterReceiver(getActivity(), mReceiver);
            mReceiver = null;
        }
    }

    private void reqNet() {
        if (!isOpenNet()) {
            Toast.makeText(getActivity(), getString(R.string.net_err), Toast.LENGTH_SHORT);
            return;
        }
        uppack = new PackCheckVersionUp();
        pack = new PackCheckVersionDown();
        PcsDataDownload.addDownload(uppack);
    }

    private void reqNetgetsharefriend() {
        if (!isOpenNet()) {
            Toast.makeText(getActivity(), getString(R.string.net_err), Toast.LENGTH_SHORT);
            return;
        }
        uppackgetshareinfo = new PackgetrecommendfriendsmsgUP();
        Downpackgetshareinfo = new PackgetrecommendfriendsmsgDown();
        PcsDataDownload.addDownload(uppackgetshareinfo);
    }

    public void init() {
        initdata();
        initUser();
        setsAdapter = new AdapterRightSets(setsBeanList);
        gridSets.setAdapter(setsAdapter);
    }

    private void initUser() {
        localUserinfo=ZtqCityDB.getInstance().getMyInfo();
        if (!TextUtils.isEmpty(localUserinfo.sys_user_id)) {
            logged();
        } else {
            notLogged();
        }
    }

    private void initData() {
        listData = new ArrayList<>();
        Map<String, String> itemBootStart = new HashMap<>();
        itemBootStart.put("t", "清除缓存");
        itemBootStart.put("i", R.drawable.icon_column_manager_autostart + "");
        listData.add(itemBootStart);

        Map<String, String> version = new HashMap<>();
        version.put("t", "您的建议");
        version.put("i", R.drawable.icon_column_manager_advice + "");
        listData.add(version);

        Map<String, String> itemUpdate = new HashMap<>();
        itemUpdate.put("t", "免责申明");
        itemUpdate.put("i", "" + R.drawable.icon_column_manager_update);
        listData.add(itemUpdate);

        Map<String, String> itemAutoShare = new HashMap<>();
        itemAutoShare.put("t", "版本检测");
        itemAutoShare.put("i", R.drawable.icon_column_manager_version + "");
        listData.add(itemAutoShare);

        Map<String, String> advice = new HashMap<>();
        advice.put("t", "更新频率");
        advice.put("i", R.drawable.icon_set_item_autoshare + "");
        listData.add(advice);

        Map<String, String> recommend = new HashMap<>();
        recommend.put("t", "推荐好友");
        recommend.put("i", R.drawable.icon_column_manager_recommend + "");
        listData.add(recommend);

        adapter = new AdapterColumnManager(getActivity(), listData, 0, 1);
        listView.setAdapter(adapter);
    }

    /**
     * 已登录处理
     */
    private void logged() {
//        tvUserTitle.setText(LoginInformation.getInstance().getUsername());
//        btnUserLogin.setText("退出");
        btnLogin.setVisibility(View.GONE);
        tvUserName.setVisibility(View.VISIBLE);
        tvUserName.setText(localUserinfo.sys_nick_name);
        imageTool.setImageView(getActivity(), localUserinfo.sys_head_url, ivHead);
    }

    /**
     * 未登录处理
     */
    private void notLogged() {
//        tvUserTitle.setText("个人中心");
//        btnUserLogin.setText("登录");
        btnLogin.setVisibility(View.VISIBLE);
        tvUserName.setVisibility(View.GONE);
        ivHead.setImageResource(R.drawable.icon_head_default);
    }

    @Override
    public void onResume() {
        super.onResume();
        initUser();
    }

    private void gotoLogin() {
        startActivityForResult(new Intent(activity, ActivityLogin.class), 1001);
    }

    /**
     * 页面切换动画
     */
    private void rightInAnimation() {
        getActivity().overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
    }

    /**
     * 初始化数据
     */
    @SuppressLint("NewApi")
    public void initdata() {
        mReceiver = new MyReceiver();
        PcsDataBrocastReceiver.registerReceiver(getActivity(), mReceiver);
        SetsBean bean1 = new SetsBean(R.drawable.icon_set_item_ztq, "气象证明");
        SetsBean bean2 = new SetsBean(R.drawable.icon_set_about, "二维码");
        SetsBean bean3 = new SetsBean(R.drawable.icon_set_item_push, "推送设置");
        SetsBean bean4 = new SetsBean(R.drawable.icon_set_item_useguide, "产品定制");
        setsBeanList.add(bean1);
        setsBeanList.add(bean2);
//        setsBeanList.add(bean3);
        setsBeanList.add(bean4);
    }

    public void initListener() {
        gridSets.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        // 气象证明
                        if (TextUtils.isEmpty(localUserinfo.sys_user_id)) {
                            gotoLogin();
                        } else {
                            gotoAcitvity(WeatherProveActivity.class, "气象灾害证明");
                        }
                        break;
                    case 1:
                        // 关于
                        gotoAcitvity(AcitvityAboutZTQ.class, "二维码");
                        break;
//                    case 2:
//                        // 推送设置
//                        gotoAcitvity(ActivityPushMain.class, "推送设置");
//                        break;
                    case 2:
                        // 产品定制
                        dialogProductReserve();
                        break;
                    default:
                        break;
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        // 清除缓存
                        clearCache();
                        break;
                    case 1:
                        // 您的建议
                        gotoAcitvity(AcitvityFeedBack.class, listData.get(position).get("t"));
                        break;
                    case 2:
                        // 免责声明
                        gotoAcitvity(ActivityDisclaimer.class, listData.get(position).get("t"));
                        break;
                    case 3:
                        // 版本监测
                        checkVersion();
                        break;
                    case 4:
                        // 更新频率
                        itemUpdate();
                        break;
                    case 5:
                        // 推荐好友
                        if (getShareFriendDataDialog == null) {
                            getShareFriendDataDialog = (DialogWaiting) DialogFactory.getWaitDialog(getActivity(), "正在准备分享数据");
                        }
                        getShareFriendDataDialog.show();
                        reqNetgetsharefriend();
                        break;
                }
            }
        });

        ivHome.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        tvUserName.setOnClickListener(this);
        ivHead.setOnClickListener(this);
    }

    private void clearCache() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_message, null);
        if (dialogClearCache == null) {
            TextView tv = (TextView) view.findViewById(R.id.dialogmessage);
            tv.setText("即将清除缓存的图片，是否继续？");
            tv.setTextColor(getResources().getColor(R.color.text_color));
            dialogClearCache = new DialogTwoButton(getActivity(), view, "继续", "返回", new DialogFactory.DialogListener() {
                @Override
                public void click(String str) {
                    dialogClearCache.dismiss();
                    if (str.equals("继续")) {
                        ActivityMain activity = (ActivityMain) getActivity();
                        activity.getImageFetcher().clearCache();
                        Toast.makeText(getActivity(), "缓存已清除", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        dialogClearCache.show();
    }

    private static final int toanther = 9154;

    /**
     * 跳转到下一级页面
     * @param intentactivity 跳转的class
     * @param titletext      绑定的标题
     */
    private void gotoAcitvity(Class intentactivity, String titletext) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        PackLocalCityMain cityMain = ZtqCityDB.getInstance().getCityMain();
        bundle.putSerializable("city", cityMain);
        bundle.putString("title", titletext);
        intent.putExtras(bundle);
        intent.setClass(activity, intentactivity);
        startActivity(intent);
        rightInAnimation();
    }

    @Override
    public void refresh(RefreshParam param) {
        initUser();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_username:
            case R.id.iv_head:
                if (!TextUtils.isEmpty(localUserinfo.sys_user_id)) {
                    Intent intent = new Intent(getActivity(), ActivityUserCenter.class);
                    startActivityForResult(intent, MyConfigure.RESULT_SET_TO_USER);
                }
                break;
            case R.id.ivHome:
                activity.showSetting(false);
                break;
            case R.id.btn_login2:
                if (TextUtils.isEmpty(localUserinfo.sys_user_id)) {
                    gotoLogin();
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 1001:
                    initUser();
                    if (!TextUtils.isEmpty(localUserinfo.sys_user_id)) {
                        gotoAcitvity(ActivityUserCenter.class, "我");
                    }
                    break;
                case toanther:
                    initUser();
                    activity.finish();
                    break;
                case MyConfigure.RESULT_SET_TO_USER:
                    initUser();
                    break;
            }
        }
    }

    /**
     * 广播接收
     * @author tya
     */
    private class MyReceiver extends PcsDataBrocastReceiver {
        @Override
        public void onReceive(String nameStr, String errorStr) {
            if (uppack != null && uppack.getName().equals(nameStr)) {
                pack = (PackCheckVersionDown) PcsDataManager.getInstance().getNetPack(uppack.getName());
                if (pack == null) {
                    return;
                }
                if (pack.nv == null || "".equals(pack.nv)) {
                    return;
                }
                PackageManager packageManager = getActivity().getPackageManager();
                // getPackageName()是你当前类的包名，0代表是获取版本信息
                PackageInfo packInfo;
                int version = 0;
                // 获取版本号
                try {
                    packInfo = packageManager.getPackageInfo(getActivity().getPackageName(), 0);
                    version = packInfo.versionCode;
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                String versionName = "";
                // 获取版本名
                try {
                    versionName = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

                String text = "";
                if (Integer.parseInt(pack.nv) > version) {
                    text = "新版本" + pack.sv;
                    adapter.setRightText(text, getResources().getColor(R.color.warn_red));
                } else {
                    text = !TextUtils.isEmpty(versionName) ? "当前" + versionName : versionName;
                    adapter.setRightText(text, getResources().getColor(R.color.text_black_login));
                }
                adapter.notifyDataSetChanged();
            } else if (uppackgetshareinfo != null && uppackgetshareinfo.getName().equals(nameStr)) {
                if (getShareFriendDataDialog != null) {
                    if (getShareFriendDataDialog.isShowing()) {
                        getShareFriendDataDialog.dismiss();
                    }
                }
                Downpackgetshareinfo = (PackgetrecommendfriendsmsgDown) PcsDataManager.getInstance().getNetPack(nameStr);
                if (Downpackgetshareinfo == null) {
                    return;
                }
                share(getActivity(), Downpackgetshareinfo.result);
            }
        }

    }

    public void share(final Activity activity, final String sharecontent) {
        Uri smsToUri = Uri.parse("smsto:");
        Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
        intent.putExtra("sms_body", sharecontent);
        startActivity(intent);
    }

    private Dialog waitingDialog;
    private TextView dialogcontent;
    private Dialog checkDialogresult;
    private Dialog checkDialogdescribe;

    /**
     * 版本检测
     */
    private void checkVersion() {
        if (uppack != null) {
            if (waitingDialog != null) {
                if (waitingDialog.isShowing()) {
                    waitingDialog.dismiss();
                }
            }
            pack = (PackCheckVersionDown) PcsDataManager.getInstance().getNetPack(uppack.getName());
            if (pack == null) {
                return;
            }

            if (pack.nv == null || "".equals(pack.nv)) {
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_setanther_layout, null);
                dialogcontent = (TextView) view.findViewById(R.id.dialog_info);
                dialogcontent.setText("当前已经是最新版本，无需更新。");
                checkDialogresult = new DialogOneButton(getActivity(), view, "我知道了",
                        new DialogFactory.DialogListener() {
                            @Override
                            public void click(String str) {
                                checkDialogresult.dismiss();
                            }
                        });
                return;
            }

            PackageManager packageManager = getActivity().getPackageManager();
            // getPackageName()是你当前类的包名，0代表是获取版本信息
            PackageInfo packInfo;
            int version = 0;
            try {
                packInfo = packageManager.getPackageInfo(getActivity().getPackageName(), 0);
                version = packInfo.versionCode;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            if (Integer.parseInt(pack.nv) > version) {
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_message, null);
                ((TextView) view.findViewById(R.id.dialogmessage)).setText(pack.des);
                checkDialogdescribe = new DialogTwoButton(getActivity(), view, "立即升级", "以后再说",
                        new DialogFactory.DialogListener() {
                            @Override
                            public void click(String str) {
                                checkDialogdescribe.dismiss();
                                if (str.equals("立即升级")) {
                                    handler.sendEmptyMessage(0);
                                }
                            }
                        });
                checkDialogdescribe.show();
            } else {
                // 已经是最新版本
                if (checkDialogresult == null) {
                    View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_setanther_layout, null);
                    dialogcontent = (TextView) view.findViewById(R.id.dialog_info);
                    dialogcontent.setText("当前已经是最新版本，无需更新。");
                    checkDialogresult = new DialogOneButton(getActivity(), view, "我知道了",
                            new DialogFactory.DialogListener() {
                                @Override
                                public void click(String str) {
                                    checkDialogresult.dismiss();
                                }
                            });
                }
                checkDialogresult.show();
            }

        }
    }

    private PcsFileDownload mFileDownload;
    private TextView desc_download;
    private ProgressBar progerssBar;
    private Dialog checkDialogdownload;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (mFileDownload == null) {
                mFileDownload = new PcsFileDownload();
            }

            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    View viewdownload = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_download, null);
                    desc_download = (TextView) viewdownload.findViewById(R.id.desc_download);
                    progerssBar = (ProgressBar) viewdownload.findViewById(R.id.progressbar);
                    checkDialogdownload = new DialogOneButton(getActivity(), viewdownload, "取消",
                            new DialogFactory.DialogListener() {
                        @Override
                        public void click(String str) {
                            checkDialogdownload.dismiss();
                            mFileDownload.cancel();
                        }
                    });
                    checkDialogdownload.setTitle("正在下载");
                    checkDialogdownload.show();
                    String[] appname = pack.file.split("/");
                    mFileDownload.downloadFile(downloadlistener, getString(R.string.file_download_url) + pack.file,
                            PcsGetPathValue.getInstance().getAppPath() + appname[appname.length - 1]);
                    break;

                default:
                    break;
            }
        }
    };

    PcsFileDownloadListener downloadlistener = new PcsFileDownloadListener() {
        @Override
        public void progress(String url, String fileName, long netSize, long downSize) {
            if (checkDialogdownload.isShowing()) {
                progerssBar.setMax((int) netSize);
                progerssBar.setProgress((int) downSize);
                float press = ((float) downSize / (float) netSize) * 100f;
                desc_download.setText(String.format("%.2f", press) + "%");
//                desc_download.setText(String.format("%.1f", downSize / (1024f * 1024f)) + "M/" + String.format("%
// .1f", netSize / (1024f * 1024f)) + "M");
            }
        }

        @Override
        public void downloadSucc(String url, String fileName) {
            if (checkDialogdownload.isShowing()) {
                checkDialogdownload.dismiss();
            }
            String[] appname = pack.file.split("/");
            File file = new File(PcsGetPathValue.getInstance().getAppPath() + appname[appname.length - 1]);
            CommUtils.openIfAPK(file);
        }

        @Override
        public void downloadErr(String url, String fileName, String errMsg) {
            if (checkDialogdownload.isShowing()) {
                checkDialogdownload.dismiss();
            }
            Toast.makeText(getActivity(), errMsg, Toast.LENGTH_SHORT).show();
        }
    };

    private List<Map<String, Object>> dialoglistData;

    /**
     * 更新频率
     */
    private void itemUpdate() {
        // 弹出框数据
        dialoglistData = new ArrayList<Map<String, Object>>();
        // Map<String, Object> dialogmapa = new HashMap<String, Object>();
        // dialogmapa.put("c", "启动时更新");
        // dialogmapa.put("r", true);
        Map<String, Object> dialogNow = new HashMap<>();
        dialogNow.put("c", "实时更新");
        dialogNow.put("r", true);
        Map<String, Object> dialogPart = new HashMap<>();
        dialogPart.put("c", "半小时更新");
        dialogPart.put("r", false);

        Map<String, Object> dialoTwoHours = new HashMap<>();
        dialoTwoHours.put("c", "2小时更新");
        dialoTwoHours.put("r", false);

        Map<String, Object> dialogSixHours = new HashMap<>();
        dialogSixHours.put("c", "6小时更新");
        dialogSixHours.put("r", false);
        Map<String, Object> dialogTwelveHours = new HashMap<>();
        dialogTwelveHours.put("c", "12小时更新");
        dialogTwelveHours.put("r", false);
        Map<String, Object> dialogTwentyFourHours = new HashMap<>();
        dialogTwentyFourHours.put("c", "24小时更新");
        dialogTwentyFourHours.put("r", false);

        // dialoglistData.add(dialogmapa);

        dialoglistData.add(dialogNow);
        dialoglistData.add(dialogPart);
        dialoglistData.add(dialoTwoHours);
        dialoglistData.add(dialogSixHours);
        dialoglistData.add(dialogTwelveHours);
        dialoglistData.add(dialogTwentyFourHours);
        int item = SettingDB.getInstance().getSetUpdate().choiceItem;
        for (int i = 0; i < dialoglistData.size(); i++) {
            if (i == item) {
                dialoglistData.get(i).put("r", true);
            } else {
                dialoglistData.get(i).put("r", false);
            }
        }
        showUpdateDialog();
    }

    private DialogOneButton updateDialog;
    private MyListView dialogListview;
    private AdapterSetDialogList dialogadapter;

    private void showUpdateDialog() {
        if (updateDialog == null) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.setfragmetnt_dialog_layout, null);
            dialogListview = (MyListView) view.findViewById(R.id.listview);
            dialogadapter = new AdapterSetDialogList(getActivity(), dialoglistData, radioClick);
            dialogListview.setAdapter(dialogadapter);
            updateDialog = new DialogOneButton(getActivity(), view, "确定", new DialogFactory.DialogListener() {
                        @Override
                        public void click(String str) {
                            updateDialog.dismiss();
                        }
                    });
        }
        updateDialog.setTitle("更新频率设置");
        updateDialog.show();
    }

    /**
     * 列表中的单选按钮被选中的时候更改其他的按钮并更新适配器
     */
    private AdapterSetDialogList.RadioClick radioClick = new AdapterSetDialogList.RadioClick() {
        @Override
        public void positionclick(int position) {
            for (int i = 0; i < dialoglistData.size(); i++) {
                if (i == position) {
                    dialoglistData.get(i).put("r", true);
                    PackLocalSetUpdate pack = SettingDB.getInstance().getSetUpdate();
                    pack.choiceItem = i;
                    SettingDB.getInstance().saveSetUpdate(pack);
                } else {
                    dialoglistData.get(i).put("r", false);
                }
            }
            dialogadapter.notifyDataSetChanged();
        }
    };

    /* 判断是否是处于wifi状态下*/
    protected boolean isWiFiNewWord() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI;
    }

    /* 判断是否是有网络*/
    public boolean isOpenNet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null) {
            return true;
        } else {
            return false;
        }
    }

    private String dialNumber = "022-23333578";
    /**
     * 产品定制
     */
    private void dialogProductReserve() {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_product_reserve, null);
        TextView tvClose = view.findViewById(R.id.tvClose);
        final TextView tvNumber1 = view.findViewById(R.id.tvNumber1);
        final TextView tvNumber2 = view.findViewById(R.id.tvNumber2);

        final Dialog dialog = new Dialog(activity, R.style.CustomProgressDialog);
        dialog.setContentView(view);
        dialog.show();
        tvNumber1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialNumber = tvNumber1.getText().toString();
                checkPhoneAuthority(dialNumber);
            }
        });
        tvNumber2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialNumber = tvNumber2.getText().toString();
                checkPhoneAuthority(dialNumber);
            }
        });
        tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    /**
     * 申请电话权限
     */
    private void checkPhoneAuthority(String dialNumber) {
        if (Build.VERSION.SDK_INT < 23) {
            try {
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+dialNumber)));
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }else {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CALL_PHONE}, AuthorityUtil.AUTHOR_PHONE);
            }else {
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+dialNumber)));
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case AuthorityUtil.AUTHOR_PHONE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try {
                        startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+dialNumber)));
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }
                }else {
//                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
//                        AuthorityUtil.intentAuthorSetting(mContext, "\""+getString(R.string.app_name)+"\""+"需要使用电话权限，是否前往设置？");
//                    }
                }
                break;
        }
    }

}
