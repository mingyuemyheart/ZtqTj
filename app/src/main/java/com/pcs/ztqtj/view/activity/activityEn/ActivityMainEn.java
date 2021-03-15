package com.pcs.ztqtj.view.activity.activityEn;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;

import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib.lib_pcs_v3.model.image.ImageCache;
import com.pcs.lib.lib_pcs_v3.model.image.ImageFetcher;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityMain;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackSstqUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackZtqImageDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackZtqImageUp;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.AutoDownloadWeather;
import com.pcs.ztqtj.control.tool.ZtqAppWidget;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.view.fragment.FragmentHomeEnWeather;
import com.pcs.ztqtj.view.fragment.FragmentHomeWeather;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.UMShareAPI;

public class ActivityMainEn extends FragmentActivity {

    // 图片获取类
    public ImageFetcher mImageFetcher = null;
    // ImageFetcher已恢复？
    private boolean mFetcherResumed = false;
    // 等待对话框
    private ProgressDialog mProgressDialog = null;

    private WeatherReceiver mWeatherReceiver = null;
    //首页
    private FragmentHomeEnWeather mFragmentHomeWeather;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_en);
//        AnalyticsConfig.enableEncrypt(true);
        intView();
        createImageFetcher(this.getResources()
                .getDimensionPixelSize(R.dimen.dimen480));
        //刷新小部件
        reflushWidget();
        //下载主题插图
        downloadImage();
    }

    private void intView() {
        mFragmentHomeWeather = new FragmentHomeEnWeather();
        FragmentTransaction tran = ActivityMainEn.this.getSupportFragmentManager().beginTransaction();
        tran.replace(R.id.layout_content, mFragmentHomeWeather);
        tran.commitAllowingStateLoss();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mImageFetcher != null && !mFetcherResumed) {
            mFetcherResumed = true;
            mImageFetcher.setExitTasksEarly(false);
        }
        MobclickAgent.onResume(this);
        // 天气广播接收
        if (mWeatherReceiver == null) {
            mWeatherReceiver = new WeatherReceiver();
            PcsDataBrocastReceiver.registerReceiver(ActivityMainEn.this,mWeatherReceiver);
        }
        //下载数据
        PackLocalCityMain cityMain = ZtqCityDB.getInstance().getCityMain();
        if (cityMain != null) {
            AutoDownloadWeather.getInstance().setMainDataPause(false);
            AutoDownloadWeather.getInstance().beginMainData();
            AutoDownloadWeather.getInstance().setDefaultCity(cityMain);
        }
        // 刷新数据
        FragmentHomeWeather.HomeRefreshParam param = new FragmentHomeWeather.HomeRefreshParam();
        param.isChangedCity = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mImageFetcher != null) {
            mFetcherResumed = false;
            mImageFetcher.setPauseWork(false);
            mImageFetcher.setExitTasksEarly(true);
            mImageFetcher.flushCache();
        }
        // 天气广播
        if (mWeatherReceiver != null) {
            PcsDataBrocastReceiver.unregisterReceiver(ActivityMainEn.this,mWeatherReceiver);
            mWeatherReceiver = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mImageFetcher != null) {
            mImageFetcher.closeCache();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
        if (mImageFetcher != null && !mFetcherResumed) {
            mFetcherResumed = true;
            mImageFetcher.setExitTasksEarly(false);
        }
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (data.getBooleanExtra("finish", false)) {
                finish();
                System.exit(0);
            }
        }
    }

    protected void createImageFetcher(int imageThumbSize) {
        ImageCache.ImageCacheParams cacheParams = new ImageCache.ImageCacheParams(this);
        cacheParams.setMemCacheSizePercent(0.25f);
        mImageFetcher = new ImageFetcher(this);
        mImageFetcher.addImageCache(this.getSupportFragmentManager(),cacheParams);
    }


    private void reflushWidget() {
        ZtqAppWidget.getInstance().updateAllWidget(this);
    }


    /**
     * 实时天气广播
     *
     * @author JiangZY
     */
    private class WeatherReceiver extends PcsDataBrocastReceiver {
        @Override
        public void onReceive(String name, String errorStr) {
            if (TextUtils.isEmpty(name)) {
                // 名字NULL
                return;
            }
            if (!TextUtils.isEmpty(errorStr)) {
                // 出错
                return;
            }
            if (name.startsWith(PackSstqUp.NAME)) {
            }
        }
    }


    /**
     * 显示等待对话框
     */
    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setCancelable(true);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setOnCancelListener(mProgressOnCancel);
        }
        if (mProgressDialog.isShowing()) {

        } else {
            mProgressDialog.show();
        }
        mProgressDialog.setMessage(getResources().getString(
                R.string.please_wait));
    }

    /**
     * 等待框OnCancel
     */
    private DialogInterface.OnCancelListener mProgressOnCancel = new DialogInterface.OnCancelListener() {
        @Override
        public void onCancel(DialogInterface dialog) {
                new AlertDialog.Builder(ActivityMainEn.this)
                        .setTitle(R.string.tip)
                        .setMessage(R.string.exit_confirm)
                        .setPositiveButton(
                                R.string.exit,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
//                                        exit();
                                    }
                                })
                        .setNegativeButton(
                                R.string.cancel,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        //showProgressDialog();
                                        dialog.dismiss();
                                    }
                                }).create().show();
        }
    };

    /**
     * 取消等待对话框
     */
    public void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }


    /**
     * 下载主题插图
     */
    private void downloadImage() {
        PackZtqImageDown packImage = (PackZtqImageDown) PcsDataManager.getInstance().getNetPack(PackZtqImageUp.NAME);
        if (packImage == null) {
            return;
        }

        packImage.beginDownload(getString(R.string.file_download_url), mImageFetcher);
    }



    /* 判断是否是处于wifi状态下*/
    protected boolean isWiFiNewWord() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        } else {
            return false;
        }
    }

    public ImageFetcher getImageFetcher() {
        return mImageFetcher;
    }

}
