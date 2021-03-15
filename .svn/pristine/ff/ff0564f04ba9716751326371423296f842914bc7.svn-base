package com.pcs.ztqtj.view.activity.set;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.pcs.lib.lib_pcs_v3.control.file.PcsFileDownload;
import com.pcs.lib.lib_pcs_v3.control.file.PcsFileDownloadListener;
import com.pcs.lib.lib_pcs_v3.control.file.PcsGetPathValue;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityInfo;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityMain;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalSetUpdate;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackCheckVersionDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackCheckVersionUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackgetrecommendfriendsmsgDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackgetrecommendfriendsmsgUP;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.adapter_set.AdapterColumnManager;
import com.pcs.ztqtj.control.adapter.adapter_set.AdapterSetDialogList;
import com.pcs.ztqtj.control.tool.CommUtils;
import com.pcs.ztqtj.model.SettingDB;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
import com.pcs.ztqtj.view.dialog.DialogFactory;
import com.pcs.ztqtj.view.dialog.DialogOneButton;
import com.pcs.ztqtj.view.dialog.DialogTwoButton;
import com.pcs.ztqtj.view.dialog.DialogWaiting;
import com.pcs.ztqtj.view.myview.MyListView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tyaathome on 2016/8/23.
 */
public class ActivityColumnManager extends FragmentActivityZtqBase {

    private MyListView listView;
    private AdapterColumnManager adapter;
    private List<Map<String, String>> listData = new ArrayList<>();
    private Dialog waitingDialog;
    private PackCheckVersionUp uppack;
    private PackCheckVersionDown pack;
    private MyReceiver receiver = new MyReceiver();
    private Dialog checkDialogdescribe;
    private PcsFileDownload mFileDownload;
    private TextView desc_download;
    private ProgressBar progerssBar;
    private Dialog checkDialogdownload;
    private Dialog checkDialogresult;
    private TextView dialogcontent;
    private PackgetrecommendfriendsmsgUP uppackgetshareinfo;
    private PackgetrecommendfriendsmsgDown Downpackgetshareinfo;
    private DialogWaiting getShareFriendDataDialog;
    private List<Map<String, Object>> dialoglistData;
    private DialogOneButton updateDialog;
    private MyListView dialogListview;
    private AdapterSetDialogList dialogadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PcsDataBrocastReceiver.registerReceiver(this, receiver);
        setContentView(R.layout.activity_set_system);
        setTitleText("更多");
        initView();
        initEvent();
        initData();
        reqNet();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private void initView() {
        listView = (MyListView) findViewById(R.id.lv_list);
    }

    private void initEvent() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch(position) {
                    case 0:
                        // 开机自启动
                        break;
                    case 1:
                        // 版本监测
                        checkVersion();
                        break;
                    case 2:
                        // 更新频率
                        itemUpdate();
                        break;
//                    case 3:
//                        // 天气闹钟
//                        gotoAcitvity(ActivityWeatherClock.class, listData.get(position)
//                                .get("t"));
//                        break;
//                    case 5:
//                        // 自动分享
//                        gotoAcitvity(ActivityAutoShare.class,
//                                listData.get(5).get("t"));
//                        break;
                    case 3:
                        // 您的建议
                        gotoAcitvity(AcitvityFeedBack.class, listData.get(position).get("t"));
                        break;
                    case 4:
                        // 推荐好友
                        if (getShareFriendDataDialog == null) {
                            getShareFriendDataDialog = (DialogWaiting) DialogFactory.getWaitDialog(ActivityColumnManager.this, "正在准备分享数据");
                        }
                        getShareFriendDataDialog.show();
                        reqNetgetsharefriend();
                        break;
                    case 5:
                        // 免责声明
                        gotoAcitvity(ActivityDisclaimer.class, listData.get(position).get("t"));
                        break;
//                    case 7:
//                        // 气象短信
//                        gotoAcitvity(ActivitySms.class, listData.get(position).get("t"));
//                        break;
                }
            }
        });
    }

    private void initData() {

        listData = new ArrayList<>();
        Map<String, String> itemBootStart = new HashMap<String, String>();
        itemBootStart.put("t", "开机自启动");
        itemBootStart.put("i", R.drawable.icon_column_manager_autostart + "");
        listData.add(itemBootStart);

        Map<String, String> version = new HashMap<String, String>();
        version.put("t", "版本检测");
        version.put("i", R.drawable.icon_column_manager_version + "");
        listData.add(version);

        //Map<String, String> cache = new HashMap<String, String>();
        //cache.put("t", "清除缓存");
        //cache.put("i", R.drawable.icon_column_manager_clear_cache + "");
        //listData.add(cache);

        Map<String, String> itemUpdate = new HashMap<String, String>();
        itemUpdate.put("t", "更新频率");
        itemUpdate.put("i", "" + R.drawable.icon_column_manager_update);
        listData.add(itemUpdate);

//        Map<String, String> itemClock = new HashMap<String, String>();
//        itemClock.put("t", "天气闹钟");
//        itemClock.put("i", R.drawable.icon_column_manager_alarm + "");
//        listData.add(itemClock);

        Map<String, String> itemAutoShare = new HashMap<String, String>();
        itemAutoShare.put("t", "自动分享");
        itemAutoShare.put("i", R.drawable.icon_set_item_autoshare + "");
        //listData.add(itemAutoShare);

        Map<String, String> advice = new HashMap<String, String>();
        advice.put("t", "您的建议");
        advice.put("i", R.drawable.icon_column_manager_advice + "");
        listData.add(advice);

        Map<String, String> recommend = new HashMap<String, String>();
        recommend.put("t", "推荐好友");
        recommend.put("i", R.drawable.icon_column_manager_recommend + "");
        listData.add(recommend);

        Map<String, String> disclaimer = new HashMap<String, String>();
        disclaimer.put("t", "免责声明");
        disclaimer.put("i", R.drawable.icon_column_manager_disclaimer + "");
        listData.add(disclaimer);

//        Map<String, String> weatherMessage = new HashMap<String, String>();
//        weatherMessage.put("t", "气象短信");
//        weatherMessage.put("i", R.drawable.icon_column_manager_message + "");
//        listData.add(weatherMessage);

        adapter = new AdapterColumnManager(this, listData, 0, 1);
        listView.setAdapter(adapter);
    }

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
                View view = LayoutInflater.from(ActivityColumnManager.this).inflate(R.layout.dialog_setanther_layout, null);
                dialogcontent = (TextView) view.findViewById(R.id.dialog_info);
                dialogcontent.setText("当前已经是最新版本，无需更新。");
                checkDialogresult = new DialogOneButton(ActivityColumnManager.this, view, "我知道了", new DialogFactory.DialogListener() {
                    @Override
                    public void click(String str) {
                        checkDialogresult.dismiss();
                    }
                });
                return;
            }

            PackageManager packageManager = getPackageManager();
            // getPackageName()是你当前类的包名，0代表是获取版本信息
            PackageInfo packInfo;
            int version = 0;
            try {
                packInfo = packageManager.getPackageInfo(ActivityColumnManager.this.getPackageName(), 0);
                version = packInfo.versionCode;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            if (Integer.parseInt(pack.nv) > version) {
                View view = LayoutInflater.from(ActivityColumnManager.this).inflate(R.layout.dialog_message, null);
                ((TextView) view.findViewById(R.id.dialogmessage)).setText(pack.des);
                checkDialogdescribe = new DialogTwoButton(ActivityColumnManager.this, view, "立即升级", "以后再说", new DialogFactory.DialogListener() {
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
                    View view = LayoutInflater.from(ActivityColumnManager.this).inflate(R.layout.dialog_setanther_layout, null);
                    dialogcontent = (TextView) view.findViewById(R.id.dialog_info);
                    dialogcontent.setText("当前已经是最新版本，无需更新。");
                    checkDialogresult = new DialogOneButton(ActivityColumnManager.this, view, "我知道了", new DialogFactory.DialogListener() {
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

    /**
     * 更新频率
     */
    private void itemUpdate() {
        // 弹出框数据
        dialoglistData = new ArrayList<Map<String, Object>>();
        // Map<String, Object> dialogmapa = new HashMap<String, Object>();
        // dialogmapa.put("c", "启动时更新");
        // dialogmapa.put("r", true);
        Map<String, Object> dialogNow = new HashMap<String, Object>();
        dialogNow.put("c", "实时更新");
        dialogNow.put("r", true);
        Map<String, Object> dialogPart = new HashMap<String, Object>();
        dialogPart.put("c", "半小时更新");
        dialogPart.put("r", false);

        Map<String, Object> dialoTwoHours = new HashMap<String, Object>();
        dialoTwoHours.put("c", "2小时更新");
        dialoTwoHours.put("r", false);

        Map<String, Object> dialogSixHours = new HashMap<String, Object>();
        dialogSixHours.put("c", "6小时更新");
        dialogSixHours.put("r", false);
        Map<String, Object> dialogTwelveHours = new HashMap<String, Object>();
        dialogTwelveHours.put("c", "12小时更新");
        dialogTwelveHours.put("r", false);
        Map<String, Object> dialogTwentyFourHours = new HashMap<String, Object>();
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

    /**
     * 列表中的单选按钮被选中的时候更改其他的按钮并更新适配器
     */
    private AdapterSetDialogList.RadioClick radioClick = new AdapterSetDialogList.RadioClick() {
        @Override
        public void positionclick(int position) {
            for (int i = 0; i < dialoglistData.size(); i++) {
                if (i == position) {
                    dialoglistData.get(i).put("r", true);
                    PackLocalSetUpdate pack = SettingDB.getInstance()
                            .getSetUpdate();
                    pack.choiceItem = i;
                    SettingDB.getInstance().saveSetUpdate(pack);
                } else {
                    dialoglistData.get(i).put("r", false);
                }
            }
            dialogadapter.notifyDataSetChanged();
        }
    };

    private void showUpdateDialog() {
        if (updateDialog == null) {
            View view = LayoutInflater.from(this).inflate(
                    R.layout.setfragmetnt_dialog_layout, null);
            dialogListview = (MyListView) view.findViewById(R.id.listview);
            dialogadapter = new AdapterSetDialogList(this, dialoglistData,
                    radioClick);
            dialogListview.setAdapter(dialogadapter);
            updateDialog = new DialogOneButton(this, view, "确定",
                    new DialogFactory.DialogListener() {
                        @Override
                        public void click(String str) {
                            updateDialog.dismiss();
                        }
                    });
        }
        updateDialog.setTitle("更新频率设置");
        updateDialog.show();
    }

    private void reqNet() {
        if(!isOpenNet()){
            showToast(getString(R.string.net_err));
            return ;
        }
        // 上传包
        uppack = new PackCheckVersionUp();
        pack = new PackCheckVersionDown();
        // 请求网络
        PcsDataDownload.addDownload(uppack);
    }

    private void reqNetgetsharefriend() {
        if(!isOpenNet()){
            showToast(getString(R.string.net_err));
            return ;
        }
        // 上传包
        uppackgetshareinfo = new PackgetrecommendfriendsmsgUP();
        Downpackgetshareinfo = new PackgetrecommendfriendsmsgDown();
        // 请求网络
        PcsDataDownload.addDownload(uppackgetshareinfo);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (mFileDownload == null) {
                mFileDownload = new PcsFileDownload();
            }

            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    View viewdownload = LayoutInflater.from(ActivityColumnManager.this).inflate(R.layout.dialog_download, null);
                    desc_download = (TextView) viewdownload.findViewById(R.id.desc_download);
                    progerssBar = (ProgressBar) viewdownload.findViewById(R.id.progressbar);
                    checkDialogdownload = new DialogOneButton(ActivityColumnManager.this, viewdownload, "取消", new DialogFactory.DialogListener() {
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
                float press = ((float)downSize/(float)netSize) * 100f;
                desc_download.setText(String.format("%.2f", press) + "%");
//                desc_download.setText(String.format("%.1f", downSize / (1024f * 1024f)) + "M/" + String.format("%.1f", netSize / (1024f * 1024f)) + "M");
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
            Toast.makeText(ActivityColumnManager.this, errMsg, Toast.LENGTH_SHORT).show();
        }
    };

    public void share(final Activity activity, final String sharecontent) {
        Uri smsToUri = Uri.parse("smsto:");
        Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
        intent.putExtra("sms_body", sharecontent);
        startActivity(intent);
    }

    /**
     * 跳转到下一级页面
     *
     * @param intentactivity 跳转的class
     * @param titletext      绑定的标题
     */
    private void gotoAcitvity(Class intentactivity, String titletext) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        PackLocalCityInfo citylist = ZtqCityDB.getInstance()
                .getCurrentCityInfo();
        PackLocalCityMain cityMain = ZtqCityDB.getInstance().getCityMain();
        bundle.putSerializable("city", cityMain);
        bundle.putString("title", titletext);
        intent.putExtras(bundle);
        intent.setClass(this, intentactivity);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
    }

    private class MyReceiver extends PcsDataBrocastReceiver {

        @Override
        public void onReceive(String name, String errorStr) {
            if (uppack != null && uppack.getName().equals(name)) {
                pack = (PackCheckVersionDown) PcsDataManager.getInstance().getNetPack(uppack.getName());
                if (pack == null) {
                    return;
                }

                if (pack.nv == null || "".equals(pack.nv)) {
                    return;
                }

                PackageManager packageManager = getPackageManager();
                // getPackageName()是你当前类的包名，0代表是获取版本信息
                PackageInfo packInfo;
                int version = 0;
                // 获取版本号
                try {
                    packInfo = packageManager.getPackageInfo(ActivityColumnManager.this.getPackageName(), 0);
                    version = packInfo.versionCode;
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                String versionName = "";
                // 获取版本名
                try {
                    versionName = getPackageManager().getPackageInfo(getPackageName(), 0)
                            .versionName;
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

                String text = "";
                if(Integer.parseInt(pack.nv) > version) {
                    text = "新版本" + pack.sv;
                    adapter.setRightText(text, getResources().getColor(R.color.warn_red));
                } else {
                    text = !TextUtils.isEmpty(versionName) ? "当前" + versionName : versionName;
                    adapter.setRightText(text, getResources().getColor(R.color.text_black_login));
                }

                adapter.notifyDataSetChanged();
            } else if (uppackgetshareinfo != null && uppackgetshareinfo.getName().equals(name)) {
                if (getShareFriendDataDialog != null) {
                    if (getShareFriendDataDialog.isShowing()) {
                        getShareFriendDataDialog.dismiss();
                    }
                }
                Downpackgetshareinfo = (PackgetrecommendfriendsmsgDown) PcsDataManager.getInstance().getNetPack(name);
                if (Downpackgetshareinfo == null) {
                    return;
                }
                share(ActivityColumnManager.this, Downpackgetshareinfo.result);
            }
        }
    }
}
