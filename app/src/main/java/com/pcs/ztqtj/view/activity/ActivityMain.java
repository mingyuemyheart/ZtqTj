package com.pcs.ztqtj.view.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.pcs.lib.lib_pcs_v3.control.file.PcsFileDownload;
import com.pcs.lib.lib_pcs_v3.control.file.PcsFileDownloadListener;
import com.pcs.lib.lib_pcs_v3.control.file.PcsGetPathValue;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib.lib_pcs_v3.model.image.ImageCache;
import com.pcs.lib.lib_pcs_v3.model.image.ImageFetcher;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityLocation;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityMain;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalUser;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalUserInfo;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackCheckVersionDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackCheckVersionUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackSstqDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackSstqUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackZtqImageDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackZtqImageUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.WarnBean;
import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.inter.InterfaceRefresh;
import com.pcs.ztqtj.control.tool.AutoDownloadWeather;
import com.pcs.ztqtj.control.tool.CommUtils;
import com.pcs.ztqtj.control.tool.MyConfigure;
import com.pcs.ztqtj.control.tool.ZtqAppWidget;
import com.pcs.ztqtj.control.tool.ZtqLocationTool;
import com.pcs.ztqtj.control.tool.ZtqPushTool;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.util.OkHttpUtil;
import com.pcs.ztqtj.view.activity.citylist.ActivityCityList;
import com.pcs.ztqtj.view.activity.warn.ActivityWarnDetails;
import com.pcs.ztqtj.view.activity.warn.ActivityWarningCenterNotFjCity;
import com.pcs.ztqtj.view.dialog.DialogFactory;
import com.pcs.ztqtj.view.dialog.DialogOneButton;
import com.pcs.ztqtj.view.dialog.DialogTwoButton;
import com.pcs.ztqtj.view.fragment.FragmentCityManager;
import com.pcs.ztqtj.view.fragment.FragmentHomeWeather;
import com.pcs.ztqtj.view.fragment.FragmentLife;
import com.pcs.ztqtj.view.fragment.FragmentProduct;
import com.pcs.ztqtj.view.fragment.FragmentService;
import com.pcs.ztqtj.view.fragment.FragmentSet;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ActivityMain extends FragmentActivity {

    // 图片获取类
    public ImageFetcher mImageFetcher = null;
    // ImageFetcher已恢复？
    private boolean mFetcherResumed = false;
    // 左滑动Fragment
    private FragmentCityManager mFragmentLeft;
    // 右边滑动Fragment
    private FragmentSet mFragmentRight;
    //首页
    private FragmentHomeWeather mFragmentHomeWeather;
    // 底部菜单监听
    private MyRadioListener mRadioListener = null;
    private DialogTwoButton checkDialogdescribe;
    private PackCheckVersionUp packcheckversiona;
    private PackCheckVersionDown packcheckversion;
    private DialogOneButton checkDialogdownload;
    private TextView desc_download;
    private ProgressBar progerssBar;
    // 回退目标
    private int mIntBackTarget = -1;

    // 等待对话框
    private ProgressDialog mProgressDialog = null;
    // 首页刷新间隔
    private long REFRESH_INTERVAL = 60 * 1000;

    private WeatherReceiver mWeatherReceiver = null;

    // 上传包：实时天气
    private PackSstqUp mPackSstqUp = new PackSstqUp();
    //文件下载
    private PcsFileDownload mFileDownload;
    //点击回退时间
    private long mBackTime = 0;

    private DrawerLayout drawerLayout;

//    //TODO 下载演示数据
//    private TempAutoData mTempAutoData = new TempAutoData();

    /**
     * 处理升级
     */
    private Handler handlerVersion = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                if (mFileDownload == null) {
                    mFileDownload = new PcsFileDownload();
                }
                View viewdownload = LayoutInflater.from(ActivityMain.this)
                        .inflate(R.layout.dialog_download, null);
                desc_download = (TextView) viewdownload
                        .findViewById(R.id.desc_download);
                progerssBar = (ProgressBar) viewdownload
                        .findViewById(R.id.progressbar);
                checkDialogdownload = new DialogOneButton(ActivityMain.this,
                        viewdownload, "取消", new DialogFactory.DialogListener() {
                    @Override
                    public void click(String str) {
                        checkDialogdownload.dismiss();
                        mFileDownload.cancel();
                    }
                });
                checkDialogdownload.setTitle("正在下载");
                checkDialogdownload.show();
                String[] appname = packcheckversion.file.split("/");
                mFileDownload.downloadFile(
                        downloadlistener,
                        getString(R.string.file_download_url)
                                + packcheckversion.file,
                        PcsGetPathValue.getInstance().getAppPath()
                                + appname[appname.length - 1]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        AnalyticsConfig.enableEncrypt(true);

//        try {
//            getWindow().addFlags(WindowManager.LayoutParams.class.getField("FLAG_NEEDS_MENU_KEY").getInt(null));
//        } catch (NoSuchFieldException e) {
//        } catch (IllegalAccessException e) {
//        }
        createImageFetcher(this.getResources()
                .getDimensionPixelSize(R.dimen.dimen480));
        // 创建fragment
        createFragment();
        initDrawerLayout();
        initBottomMenu();
        checkBottomMenu();
        //刷新小部件
        reflushWidget();
        //下载主题插图
        downloadImage();
        //检查城市
        checkCity();
        // 打开动画
        if (getIntent().getBooleanExtra("back", false)) {
            overridePendingTransition(R.anim.slide_left_in,
                    R.anim.slide_right_out);
        }
        Bundle bundle = getIntent().getBundleExtra(MyConfigure.EXTRA_BUNDLE);
        if(bundle != null) {
            String type = bundle.getString("type");
            if(!TextUtils.isEmpty(type)) {
                if(type.equals("warn")) {
//                    String title = bundle.getString("t");
//                    String icon = bundle.getString("i");
//                    String id = bundle.getString("id");
//                    Intent intent = new Intent(this, ActivityWarnDetails.class);
//                    intent.putExtra("t", title);
//                    intent.putExtra("i", icon);
//                    intent.putExtra("id", id);
                    Intent intent = new Intent(this, ActivityWarnDetails.class);
                    intent.putExtra(MyConfigure.EXTRA_BUNDLE, bundle);
                    startActivity(intent);
                } else if (type.equals("widget_warn")) {
                    WarnBean bean = (WarnBean) bundle.getSerializable("warninfo");
                    if(bean.currentCity != null) {
                        ZtqCityDB.getInstance().setCityMain(bean.currentCity, false);
                    }
                    boolean isfj = bundle.getBoolean("isfj");
                    String unitType = bundle.getString("yj_type");
                    Intent intent = new Intent();
                    intent.setClass(this, ActivityWarningCenterNotFjCity.class);
                    intent.putExtra("warninfo", bean);
                    intent.putExtra("yj_type", unitType);
                    startActivity(intent);
                }
            }
            getIntent().removeExtra(MyConfigure.EXTRA_BUNDLE);
        }

        SharedPreferences sharedPreferences = getSharedPreferences("privacyVersion", Context.MODE_PRIVATE);
        try {
            String currentVer = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            String lastVer = sharedPreferences.getString("ver", "-1");
            if (!TextUtils.equals(currentVer, lastVer)) {
                dialogPrivacy(currentVer);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void dialogPrivacy(final String ver) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_privacy, null);
        TextView tvMessage = view.findViewById(R.id.tvMessage);
        TextView tvContent = view.findViewById(R.id.tvContent);
        TextView tvPrivacy = view.findViewById(R.id.tvPrivacy);
        TextView tvProtocal = view.findViewById(R.id.tvProtocal);
        LinearLayout llNegative = view.findViewById(R.id.llNegative);
        LinearLayout llPositive = view.findViewById(R.id.llPositive);

        final Dialog dialog = new Dialog(this, R.style.CustomProgressDialog);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        tvMessage.setText("隐私服务协议");
        tvContent.setText("感谢您使用“天津惠民”。根据我国网络信息安全相关法律法规的要求，我公司制定了《天津惠民隐私政策》和《天津惠民用户服务协议》，对使用过程中可能出现的个人信息收集、使用、共享和保护等情况进行说明。为了您更好地了解并使用相关服务，请在使用前认真阅读完整版隐私政策。您需确认同意后方可使用“天津惠民”。我公司将尽全力保护您的个人信息安全。");
        tvPrivacy.setText("《天津惠民隐私政策》");
        tvPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityMain.this, HtmlActivity.class);
                intent.putExtra(CONST.ACTIVITY_NAME, "隐私政策");
                intent.putExtra(CONST.WEB_URL, "http://220.243.129.159:8081/web/smart/yszc.html");
                startActivity(intent);
            }
        });
        tvProtocal.setText("《天津惠民用户服务协议》");
        tvProtocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityMain.this, HtmlActivity.class);
                intent.putExtra(CONST.ACTIVITY_NAME, "用户服务协议");
                intent.putExtra(CONST.WEB_URL, "http://220.243.129.159:8081/web/smart/yhxy.html");
                startActivity(intent);
            }
        });
        llNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        llPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                SharedPreferences sharedPreferences = getSharedPreferences("privacyVersion", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("ver", ver);
                editor.apply();
            }
        });
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
            PcsDataBrocastReceiver.registerReceiver(ActivityMain.this,mWeatherReceiver);
        }

        // 添加定位监听
        ZtqLocationTool.getInstance().addListener(mLocationListener);
        if (mRadioListener == null || mRadioListener.getCurrentIndex() != 0) {
            // 未选中首页
            return;
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
        refreshData(param, true);
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
            PcsDataBrocastReceiver.unregisterReceiver(ActivityMain.this,mWeatherReceiver);
            mWeatherReceiver = null;
        }
        //定位监听
        ZtqLocationTool.getInstance().removeListener(mLocationListener);
        //停止首页数据下载
        AutoDownloadWeather.getInstance().setMainDataPause(true);
        AutoDownloadWeather.getInstance().stopMainData();

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
        //UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
        if (mImageFetcher != null && !mFetcherResumed) {
            mFetcherResumed = true;
            mImageFetcher.setExitTasksEarly(false);
        }
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (data.getBooleanExtra("finish", false)) {
                finish();
                System.exit(0);
            }
            if (data.getBooleanExtra("checkVersion", false)) {
                checkVerSion();
            }
        }
    }

    protected void createImageFetcher(int imageThumbSize) {
        ImageCache.ImageCacheParams cacheParams = new ImageCache.ImageCacheParams(this);
        cacheParams.setMemCacheSizePercent(0.25f);
        mImageFetcher = new ImageFetcher(this);
        mImageFetcher.addImageCache(this.getSupportFragmentManager(),cacheParams);
    }

    private void initDrawerLayout() {
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.setScrimColor(Color.TRANSPARENT);
        //drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.LEFT);
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View view, float v) {
                int count = drawerLayout.getChildCount();
                if(count > 0) {
                    View content = drawerLayout.getChildAt(0);
                    switch (view.getId()) {
                        case R.id.fragment_citymanager:
                            content.setX(view.getWidth() * v);
                            break;
                        case R.id.fragment_setting:
                            content.setX(-view.getWidth() * v);
                            break;
                    }

                }
            }

            @Override
            public void onDrawerOpened(View view) {
                switch (view.getId()) {
                    case R.id.fragment_citymanager:
                        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.RIGHT);
                        break;
                    case R.id.fragment_setting:
                        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.LEFT);
                        refreshSet();
                        break;
                }
            }

            @Override
            public void onDrawerClosed(View view) {
                switch (view.getId()) {
                    case R.id.fragment_citymanager:
                        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, Gravity.RIGHT);
                        break;
                    case R.id.fragment_setting:
                        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, Gravity.LEFT);
                        break;
                }
            }

            @Override
            public void onDrawerStateChanged(int i) {

            }
        });
    }

    /**
     * 是否滑出城市列表
     * @param show
     */
    public void showCityManager(boolean show) {
        if(show) {
            drawerLayout.openDrawer(Gravity.LEFT);
            drawerLayout.closeDrawer(Gravity.RIGHT);
        } else {
            drawerLayout.closeDrawer(Gravity.LEFT);
            drawerLayout.closeDrawer(Gravity.RIGHT);
        }
    }

    /**
     * 是否滑出设置
     * @param show
     */
    public void showSetting(boolean show) {
        if(show) {
            drawerLayout.closeDrawer(Gravity.LEFT);
            drawerLayout.openDrawer(Gravity.RIGHT);
        } else {
            drawerLayout.closeDrawer(Gravity.LEFT);
            drawerLayout.closeDrawer(Gravity.RIGHT);
        }
    }

    /**
     * 创建fragment
     */
    private void createFragment() {
        // 左滑动Fragment
        mFragmentLeft = new FragmentCityManager();
        // 右边滑动Fragment
        mFragmentRight = new FragmentSet();
    }

    /**
     * 初始化底部菜单
     */
    private void initBottomMenu() {
        RadioButton radio;
        mRadioListener = new MyRadioListener();

        radio = (RadioButton) findViewById(R.id.radio_home);
        radio.setOnCheckedChangeListener(mRadioListener);

        radio = (RadioButton) findViewById(R.id.radio_product);
        radio.setOnCheckedChangeListener(mRadioListener);

        radio = (RadioButton) findViewById(R.id.radio_service);
        radio.setOnCheckedChangeListener(mRadioListener);

        radio = (RadioButton) findViewById(R.id.radio_live);
        radio.setOnCheckedChangeListener(mRadioListener);
    }

    private void reflushWidget() {
        ZtqAppWidget.getInstance().updateAllWidget(this);
    }

    private class MyRadioListener implements CompoundButton.OnCheckedChangeListener {
        private int mCurrIndex = -1;
        private List<Fragment> mFragmentList = new ArrayList<Fragment>();

        public MyRadioListener() {
            // 首页
            mFragmentHomeWeather = new FragmentHomeWeather();
            mFragmentList.add(mFragmentHomeWeather);
            // 气象产品
            mFragmentList.add(new FragmentProduct());
            // 专项服务
            mFragmentList.add(new FragmentService());
            // 气象生活
            mFragmentList.add(new FragmentLife());
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
            if (!isChecked) {
                return;
            }

            switch (buttonView.getId()) {
                case R.id.radio_home:
                    // 首页
                    changeFragment(0);
                    FragmentHomeWeather.HomeRefreshParam param = new FragmentHomeWeather.HomeRefreshParam();
                    param.isChangedCity = true;
                    refreshData(param, true);
                    //侧边栏
                    lockDrawer(false);
                    break;
                case R.id.radio_product:
                    // 气象产品
                    changeFragment(1);
                    lockDrawer(true);
                    break;
                case R.id.radio_service:
                    // 气象服务
                    changeFragment(2);
                    lockDrawer(true);
                    break;
                case R.id.radio_live:
                    // 气象生活
                    changeFragment(3);
                    lockDrawer(true);
                    break;
            }
        }

        // 切换Fragment
        public void changeFragment(int index) {
            if (index == mCurrIndex) {
                return;
            }
            FragmentTransaction tran = ActivityMain.this.getSupportFragmentManager().beginTransaction();
            // 切换动画
            if (mCurrIndex == -1) {

            } else if (index > mCurrIndex) {
                tran.setCustomAnimations(R.anim.slide_right_in,R.anim.slide_left_out);
            } else {
                tran.setCustomAnimations(R.anim.slide_left_in,R.anim.slide_right_out);
            }
            tran.replace(R.id.layout_content, mFragmentList.get(index));
            tran.commitAllowingStateLoss();
            mCurrIndex = index;
        }

        /**
         * 获取当前index
         *
         * @return
         */
        public final int getCurrentIndex() {
            return mCurrIndex;
        }
    }

    /**
     * 是否锁定drawer
     * @param lock true: 锁定(不可滑出drawer) false: 不锁定(可滑出drawer)
     */
    public void lockDrawer(boolean lock) {
        if(lock) {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        } else {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
        //drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
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
            if (mRadioListener == null || mRadioListener.getCurrentIndex() != 0) {
                // 未选中首页
                return;
            }
            if (name.startsWith(PackSstqUp.NAME)) {
                //refreshData(null, false);
                // 更新桌面小部件
                ZtqAppWidget.getInstance().updateAllWidget(ActivityMain.this);
            }
        }
    }

    private void refreshData(InterfaceRefresh.RefreshParam param, boolean isShowProgress) {
        if (mRefreshView == null) {
            return;
        }
        // 刷新首页
        mFragmentHomeWeather.myRefreshView.refresh(param);
        // 侧边栏
        mFragmentLeft.refresh(param);
        // 推送
        ZtqPushTool.getInstance().refreshPush();
        PackLocalCityMain cityMain = ZtqCityDB.getInstance().getCityMain();
        if (cityMain == null) {
            if (isShowProgress) {
                //showProgressDialog();
            }
            return;
        }
        mPackSstqUp.area = cityMain.ID;
        PackSstqDown down = (PackSstqDown) PcsDataManager.getInstance().getNetPack(mPackSstqUp.getName());
        if (down == null) {
            if (isShowProgress) {
                //showProgressDialog();
            }
            return;
        }
        // 取消等待对话框
        dismissProgressDialog();
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
     * 取消等待对话框
     */
    public void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    /**
     * 等待框OnCancel
     */
    private DialogInterface.OnCancelListener mProgressOnCancel = new DialogInterface.OnCancelListener() {
        @Override
        public void onCancel(DialogInterface dialog) {
            if (mRadioListener.getCurrentIndex() == 2) {
                // 气象服务
            } else {
                new AlertDialog.Builder(ActivityMain.this)
                        .setTitle(R.string.tip)
                        .setMessage(R.string.exit_confirm)
                        .setPositiveButton(
                                R.string.exit,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        exit();
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
        }
    };

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

    /**
     * 检查城市
     */
    private void checkCity() {
        //首页城市
        PackLocalCityMain cityMain = ZtqCityDB.getInstance().getCityMain();
        //定位城市
        PackLocalCityLocation cityLocation = ZtqLocationTool.getInstance().getLocationCity();
        if (cityMain == null) {
            // 选择城市
            toCityListActivity();
//        } else if (cityLocation != null && !cityLocation.isFjCity) {
//            // 亲情城市
//            toFamilyCityActivity(cityLocation);
        } else {
            // 检查版本
            checkVerSion();
            checkUserInfo();
        }
    }

    /**
     * 跳转到城市列表
     */
    private void toCityListActivity() {
        Intent it = new Intent(this, ActivityCityList.class);
        it.putExtra("home_to_add", true);
        it.putExtra("checkVersion", true);
        startActivityForResult(it, MyConfigure.RESULT_CITY_LIST);
    }

    // 检测版本
    private void checkVerSion() {
        packcheckversion = (PackCheckVersionDown) PcsDataManager.getInstance().getNetPack(PackCheckVersionUp.NAME);
        if (packcheckversion == null) {
            return;
        }
        if (packcheckversion.nv == null || "".equals(packcheckversion.nv)) {
            // Toast.makeText(getApplication(), "版本号为空", 0).show();
            return;
        }

        PackageManager packageManager = getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo;
        int version = 0;
        try {
            packInfo = packageManager.getPackageInfo(
                    ActivityMain.this.getPackageName(), 0);
            version = packInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (Integer.parseInt(packcheckversion.nv) > version) {
            View view = LayoutInflater.from(ActivityMain.this).inflate(
                    R.layout.dialog_message, null);
            ((TextView) view.findViewById(R.id.dialogmessage))
                    .setText(packcheckversion.des);

            if(packcheckversion.leve.equals("4")) {
                checkDialogdescribe = new DialogTwoButton(ActivityMain.this,
                        view, "立即升级", "退出客户端", new DialogFactory.DialogListener() {
                    @Override
                    public void click(String str) {
                        //checkDialogdescribe.dismiss();
                        if (str.equals("立即升级")) {
                            if (!isWiFiNewWord()) {
                                reminDialog();
                            } else {
                                handlerVersion.sendEmptyMessage(0);
                            }
                        } else if (str.equals("退出客户端")) {
                            checkDialogdescribe.dismiss();
                            exit();
                        }
                    }
                });
            } else {
                checkDialogdescribe = new DialogTwoButton(ActivityMain.this,
                        view, "立即升级", "以后再说", new DialogFactory.DialogListener() {
                    @Override
                    public void click(String str) {
                        checkDialogdescribe.dismiss();
                        if (str.equals("立即升级")) {
                            if (!isWiFiNewWord()) {
                                reminDialog();
                            } else {
                                handlerVersion.sendEmptyMessage(0);
                            }
                        } else if (str.equals("以后再说")) {
                            checkDialogdescribe.dismiss();
                        }
                    }
                });
            }
            checkDialogdescribe.setCancelable(false);
            checkDialogdescribe.setTitle("天津气象提示");
            checkDialogdescribe.show();
        }
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

    private DialogTwoButton dialogRemain;

    private void reminDialog() {
        View view = LayoutInflater.from(ActivityMain.this).inflate(R.layout.download_remind, null);
        dialogRemain = new DialogTwoButton(ActivityMain.this,
                view, "确定", "取消", new DialogFactory.DialogListener() {
            @Override
            public void click(String str) {
                dialogRemain.dismiss();
                if (str.equals("确定")) {
                    handlerVersion.sendEmptyMessage(0);
                }
            }
        });
        dialogRemain.setTitle("天津气象提示");
        dialogRemain.show();
    }

    public InterfaceRefresh getRefreshView() {
        return mRefreshView;
    }

    public ImageFetcher getImageFetcher() {
        return mImageFetcher;
    }

    /**
     * 版本升级下载监听
     */
    PcsFileDownloadListener downloadlistener = new PcsFileDownloadListener() {
        @Override
        public void progress(String url, String fileName, long netSize,
                             long downSize) {
            if (checkDialogdownload.isShowing()) {
                progerssBar.setMax((int) netSize);
                progerssBar.setProgress((int) downSize);
//               float press = ((downSize / (1024f * 1024f)) / (netSize / (1024f * 1024f))) * 100f;
                float press = ((float) downSize / (float) netSize) * 100f;
                desc_download.setText(String.format("%.2f", press) + "%");

//              desc_download.setText(String.format("%.1f", downSize/ (1024f * 1024f))+ "M/" + String.format("%.1f", netSize / (1024f * 1024f))+ "M");
            }
        }

        @Override
        public void downloadSucc(String url, String fileName) {
            try {
                if (checkDialogdownload.isShowing()) {
                    checkDialogdownload.dismiss();
                }
                String[] appname = packcheckversion.file.split("/");
                File file = new File(PcsGetPathValue.getInstance().getAppPath()
                        + appname[appname.length - 1]);
                CommUtils.openIfAPK(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void downloadErr(String url, String fileName, String errMsg) {
            if (checkDialogdownload.isShowing()) {
                checkDialogdownload.dismiss();
            }
        }
    };


    /**
     * 定位改变监听
     */
    private ZtqLocationTool.PcsLocationListener mLocationListener = new ZtqLocationTool.PcsLocationListener() {

        @Override
        public void onLocationChanged() {
            if (mRadioListener == null || mRadioListener.getCurrentIndex() != 0) {
                // 未选中首页
                return;
            }
            // 刷新定位数据
            mHandler.sendEmptyMessage(0);
            //refreshData(null, false);
        }
    };

    private Handler mHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            refreshData(null, false);
        }
    };

    /**
     * 刷新视图
     */
    private InterfaceRefresh mRefreshView = new InterfaceRefresh() {
        @Override
        public void refresh(RefreshParam param) {
            refreshData(param, false);
        }
    };

    /**
     * 检查底部菜单选中
     */
    private void checkBottomMenu() {
        mIntBackTarget = getIntent().getIntExtra("BackTarget",
                FragmentActivityZtqBase.BackTarget.NORMAL.ordinal());
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radio_group);
        if (mIntBackTarget == FragmentActivityZtqBase.BackTarget.PRODUCT.ordinal()) {
            // 气象产品
            mRadioListener.changeFragment(1);
            radioGroup.check(R.id.radio_product);
        } else if (mIntBackTarget == FragmentActivityZtqBase.BackTarget.SERVICE.ordinal()) {
            // 专项服务
            mRadioListener.changeFragment(2);
            radioGroup.check(R.id.radio_service);
        } else if (mIntBackTarget == FragmentActivityZtqBase.BackTarget.LIVE.ordinal()) {
            // 气象生活
            mRadioListener.changeFragment(3);
            radioGroup.check(R.id.radio_live);
        } else {
            // 默认选中首页
            mRadioListener.changeFragment(0);
            radioGroup.check(R.id.radio_home);
        }
    }

    private void refreshSet() {
        FragmentSet fragment = (FragmentSet) getSupportFragmentManager().findFragmentById(R.id.fragment_setting);
        if(fragment != null) {
            fragment.refresh(null);
        }
    }

    /*
     * 返回键监听处理
     */
    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - mBackTime) > 2000) {
            Toast.makeText(this, getString(R.string.once_again_exit),
                    Toast.LENGTH_SHORT).show();
            mBackTime = System.currentTimeMillis();
        } else {
            exit();
        }
    }

    /**
     * 退出程序
     */
    public void exit() {
        finish();
    }

    private void checkUserInfo() {
        SharedPreferences shared = getSharedPreferences("userinfologin", Context.MODE_PRIVATE);
        boolean first = shared.getBoolean("first", true);
        if(first) {
            if(!ZtqCityDB.getInstance().isLoginService()) {
                showLoginDialog();
            }
            SharedPreferences.Editor editor = shared.edit();
            editor.putBoolean("first", false);
            editor.commit();
        }
    }

    private DialogOneButton loginDialog;
    private void showLoginDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.layout_main_login, null);
        final EditText etUserName = view.findViewById(R.id.et_username);
        final EditText etPassword = view.findViewById(R.id.et_password);
        loginDialog = new DialogOneButton(this, view, "登录", new DialogFactory.DialogListener() {
            @Override
            public void click(String str) {
                if (str.equals("登录")) {
                    if (TextUtils.isEmpty(etUserName.getText().toString())) {
                        Toast.makeText(ActivityMain.this, "请输入账号或手机号", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(etPassword.getText().toString())) {
                        Toast.makeText(ActivityMain.this, "请输入密码", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    okHttpLogin(etUserName.getText().toString(), etPassword.getText().toString());
                } else if(str.equals("close")) {
                    if(loginDialog != null) {
                        loginDialog.dismiss();
                    }
                }
            }
        });
        loginDialog.setCanceledOnTouchOutside(true);
        loginDialog.showCloseBtn();
        loginDialog.show();
    }

    /**
     * 用户登录
     */
    private void okHttpLogin(final String uName, final String pwd) {
        showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = CONST.BASE_URL+"user/login";
                JSONObject param = new JSONObject();
                try {
                    param.put("loginName", uName);
                    param.put("pwd", pwd);
                    String json = param.toString();
                    final RequestBody body = FormBody.create(MediaType.parse("application/json; charset=utf-8"), json);
                    OkHttpUtil.enqueue(new Request.Builder().post(body).url(url).build(), new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                        }
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (!response.isSuccessful()) {
                                return;
                            }
                            final String result = response.body().string();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dismissProgressDialog();
                                    if (!TextUtils.isEmpty(result)) {
                                        try {
                                            JSONObject obj = new JSONObject(result);
                                            if (!obj.isNull("errorMessage")) {
                                                String errorMessage = obj.getString("errorMessage");
                                                Toast.makeText(ActivityMain.this, errorMessage, Toast.LENGTH_SHORT).show();
                                            }
                                            if (!obj.isNull("token")) {
                                                MyApplication.TOKEN = obj.getString("token");
                                            }
                                            if (!obj.isNull("limitInfo")) {
                                                MyApplication.LIMITINFO = obj.getString("limitInfo");
                                            }
                                            if (!obj.isNull("userInfo")) {
                                                JSONObject userInfo = obj.getJSONObject("userInfo");
                                                if (!userInfo.isNull("userId")) {
                                                    MyApplication.UID = userInfo.getString("userId");
                                                }
                                                if (!userInfo.isNull("loginName")) {
                                                    MyApplication.USERNAME = userInfo.getString("loginName");
                                                }
                                                if (!userInfo.isNull("password")) {
                                                    MyApplication.PASSWORD = userInfo.getString("password");
                                                }
                                                if (!userInfo.isNull("userName")) {
                                                    MyApplication.NAME= userInfo.getString("userName");
                                                }
                                                if (!userInfo.isNull("phonenumber")) {
                                                    MyApplication.MOBILE= userInfo.getString("phonenumber");
                                                }
                                                if (!userInfo.isNull("avatar")) {
                                                    MyApplication.PORTRAIT= userInfo.getString("avatar");
                                                }
                                                MyApplication.saveUserInfo(ActivityMain.this);

                                                //存储用户数据
                                                PackLocalUser myUserInfo = new PackLocalUser();
                                                myUserInfo.user_id = MyApplication.UID;
                                                myUserInfo.sys_user_id = MyApplication.UID;
                                                myUserInfo.sys_nick_name = MyApplication.NAME;
                                                myUserInfo.sys_head_url = MyApplication.PORTRAIT;
                                                myUserInfo.mobile = MyApplication.MOBILE;
//                                                myUserInfo.type = packDown.platform_type;
//                                                myUserInfo.is_jc = packDown.is_jc;
                                                PackLocalUserInfo packLocalUserInfo = new PackLocalUserInfo();
                                                packLocalUserInfo.currUserInfo = myUserInfo;
                                                ZtqCityDB.getInstance().setMyInfo(packLocalUserInfo);

                                                Toast.makeText(ActivityMain.this, getString(R.string.login_succ), Toast.LENGTH_SHORT).show();
                                                if(loginDialog != null) {
                                                    loginDialog.dismiss();
                                                }
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            });
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
