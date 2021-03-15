package com.pcs.ztqtj.view.activity.set;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.pcs.lib.lib_pcs_v3.control.tool.BitmapUtil;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityMain;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalUrl;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackInitUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackShareAboutDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackShareAboutUp;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.LocalDataHelper;
import com.pcs.ztqtj.control.tool.ShareTools;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;

/**
 * @author Z 关于知天气
 */
public class AcitvityAboutZTQ extends FragmentActivityZtqBase {
    private TextView versionTextView;
    private ImageView qr_code;
    private MyReceiver myReceiver = new MyReceiver();
    private ImageView careabout_weathericon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBackDirection(BackDirection.BACK_RIGHT);
        Bundle bundle = getIntent().getExtras();
        setTitleText(bundle.getString("title"));
        setContentView(R.layout.activity_about_ztq);
        PcsDataBrocastReceiver.registerReceiver(AcitvityAboutZTQ.this,
                myReceiver);
        initView();
        initEvent();
        // 设置关于
        setAbout();
    }


    /**
     * 切换地址结束处理
     */
    private void finishHolder() {
        String defaultUrl = getString(R.string.url);
        String debugUrl = getString(R.string.url_debug);
        PackLocalUrl packUrl = (PackLocalUrl) PcsDataManager.getInstance().getLocalPack(PackLocalUrl.KEY);
        if (packUrl != null) {
            // 如果缓存中不是测试地址(true)，则将url设置成测试地址
            boolean isDebug = !LocalDataHelper.getDebug(this);
            if (isDebug) {
                packUrl.changeUrl(debugUrl);
            } else {
                packUrl.changeUrl(defaultUrl);
            }
            LocalDataHelper.saveDebug(this, isDebug);
            PcsDataManager.getInstance().saveLocalData(PackLocalUrl.KEY, packUrl);
            PcsDataManager.getInstance().removeLocalData(PackInitUp.NAME);
            setResult(Activity.RESULT_OK);
            finish();
        }
    }

    private int clickCount = 0;
    private Handler handlerClick = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            clickCount = 0;
        }
    };

    private void initEvent() {
        careabout_weathericon.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                clickCount++;
                handlerClick.removeMessages(0);
                handlerClick.sendEmptyMessageDelayed(0, 1500);
                if (clickCount == 10) {
                    finishHolder();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myReceiver);
    }

    private void initView() {
        versionTextView = (TextView) findViewById(R.id.versionname);
        careabout_weathericon = (ImageView) findViewById(R.id.careabout_weathericon);
        qr_code = (ImageView) findViewById(R.id.qr_code);

        PackageManager mg = this.getPackageManager();
        PackageInfo information;
        try {
            information = mg.getPackageInfo(this.getPackageName(), 0);
            String version = "";
            version += information.versionName;
            versionTextView.setText(version);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        setBtnRight(R.drawable.btn_share, new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isOpenNet()) {
                    showToast(getString(R.string.net_err));
                    return;
                }
                showProgressDialog();
                uppack.keyword = "ABOUT_WT";
                PcsDataDownload.addDownload(uppack);
            }
        });

        qr_code.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    private PackShareAboutUp uppack = new PackShareAboutUp();
    private PackShareAboutDown down = new PackShareAboutDown();

    private class MyReceiver extends PcsDataBrocastReceiver {
        @Override
        public void onReceive(String nameStr, String errorStr) {
            if (uppack != null) {
                if (uppack.getName().equals(nameStr)) {
                    dismissProgressDialog();
                    down = (PackShareAboutDown) PcsDataManager.getInstance().getNetPack(nameStr);
                    if (down == null) {
                        return;
                    }
                    share(down.share_content);
                }
            }
        }
    }

    private void share(String shareStr) {
        dismissProgressDialog();
        String clickurl = "";
        int index = shareStr.indexOf(":");
        if (index > 0) {
            clickurl = shareStr.substring(index + 1, shareStr.length());
            shareStr = shareStr.substring(0, index);
        }
        if (TextUtils.isEmpty(clickurl)) {
            clickurl = "http://www.fjqxfw.com:8099/gz_wap/";
        }
        Bitmap bitmap = BitmapUtil.takeScreenShot(this);
        //ShareUtil.autoShare(AcitvityAboutZTQ.this, SHARE_MEDIA.WEIXIN, shareStr, "知天气", "http://www.fjqxfw
        // .com:8099/gz_wap/", bitmap);
        //ShareUtil.share(this, shareStr, bitmap);
        View layout = findViewById(R.id.layout);
        ShareTools.getInstance(this).setShareContent(getTitleText(),shareStr, bitmap, "0").showWindow(layout);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    /**
     * 设置关于
     */
    private void setAbout() {
        TextView textView = (TextView) findViewById(R.id.text_view_about);
        // 当前城市
        PackLocalCityMain cityMain = ZtqCityDB.getInstance().getCityMain();
        if (cityMain != null && cityMain.isFjCity) {
            if (cityMain.NAME.equals("天津市区")) {
                String str = getResources().getString(R.string.careaboutasdescribed_1)
                        + "天津市公共气象服务中心"
                        + getResources().getString(R.string.careaboutasdescribed_2);
                textView.setText(str);
            } else {
                String str = getResources().getString(R.string.careaboutasdescribed_1)
                        + cityMain.NAME + "气象局"
                        + getResources().getString(R.string.careaboutasdescribed_2);
                textView.setText(str);
            }
        } else {
            String str = getResources().getString(R.string.careaboutasdescribed_1)
                    + "天津市公共气象服务中心"
                    + getResources().getString(R.string.careaboutasdescribed_2);
            textView.setText(str);
        }
    }
}
