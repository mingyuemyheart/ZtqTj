package com.pcs.ztqtj.view.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.pcs.lib.lib_pcs_v3.control.file.PcsFileDownload;
import com.pcs.lib.lib_pcs_v3.control.file.PcsFileDownloadListener;
import com.pcs.lib.lib_pcs_v3.control.file.PcsGetPathValue;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib.lib_pcs_v3.model.image.ImageCache;
import com.pcs.lib.lib_pcs_v3.model.image.ImageFetcher;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityMain;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackCheckVersionDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackCheckVersionUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackZtqImageDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackZtqImageUp;
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
import com.pcs.ztqtj.util.ColumnDto;
import com.pcs.ztqtj.util.CommonUtil;
import com.pcs.ztqtj.util.OkHttpUtil;
import com.pcs.ztqtj.view.activity.citylist.ActivityCityList;
import com.pcs.ztqtj.view.activity.web.webview.ActivityWebView;
import com.pcs.ztqtj.view.dialog.DialogFactory;
import com.pcs.ztqtj.view.dialog.DialogOneButton;
import com.pcs.ztqtj.view.dialog.DialogTwoButton;
import com.pcs.ztqtj.view.fragment.FragmentCityManager;
import com.pcs.ztqtj.view.fragment.FragmentHomeWeather;
import com.pcs.ztqtj.view.fragment.FragmentLife;
import com.pcs.ztqtj.view.fragment.FragmentProduct;
import com.pcs.ztqtj.view.fragment.FragmentService;
import com.pcs.ztqtj.view.fragment.FragmentSet;
import com.pcs.ztqtj.view.myview.MainViewPager;
import com.pcs.ztqtj.view.myview.MyPagerAdapter;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
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

/**
 * 主页面
 */
public class ActivityMain extends FragmentActivity {

    private ArrayList<ColumnDto> columnList = new ArrayList<>();
    private MainViewPager viewPager = null;
    private LinearLayout llContainer;
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private String BROADCAST_ACTION_NAME = "";//四个fragment广播名字

    public ImageFetcher mImageFetcher = null;
    private boolean mFetcherResumed = false;
    private FragmentCityManager mFragmentLeft;
    private FragmentHomeWeather mFragmentHomeWeather;
    private DialogTwoButton checkDialogdescribe;
    private PackCheckVersionDown packcheckversion;
    private DialogOneButton checkDialogdownload;
    private TextView desc_download;
    private ProgressBar progerssBar;

    // 等待对话框
    private ProgressDialog mProgressDialog = null;

    //文件下载
    private PcsFileDownload mFileDownload;
    //点击回退时间
    private long mBackTime = 0;
    private DrawerLayout drawerLayout;

    private MyBroadCastReceiver mReceiver = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initBroadCast();
        createImageFetcher();
        mFragmentLeft = new FragmentCityManager();
        initDrawerLayout();
        downloadImage();//下载主题插图
        checkCity();//检查城市
        initPrivacy();
        ZtqAppWidget.getInstance().updateAllWidget(this);//刷新小部件
        okHttpColumn();
    }

    private void initBroadCast() {
        mReceiver = new MyBroadCastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(CONST.BROADCAST_REFRESH_COLUMNN);
        registerReceiver(mReceiver, intentFilter);
    }

    private class MyBroadCastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (TextUtils.equals(intent.getAction(), CONST.BROADCAST_REFRESH_COLUMNN)) {
                okHttpColumn();
            }
        }
    }

    private void init() {
        llContainer = findViewById(R.id.llContainer);
        initViewPager();
    }

    protected void createImageFetcher() {
        ImageCache.ImageCacheParams cacheParams = new ImageCache.ImageCacheParams(this);
        cacheParams.setMemCacheSizePercent(0.25f);
        mImageFetcher = new ImageFetcher(this);
        mImageFetcher.addImageCache(this.getSupportFragmentManager(),cacheParams);

        BitmapDrawable bitmapBg = mImageFetcher.getImageCache().getBitmapFromAssets("weather_bg/01.png");
        if (bitmapBg != null) {
            ConstraintLayout clMain = findViewById(R.id.clMain);
            clMain.setBackgroundDrawable(bitmapBg);
        }
    }

    private void initDrawerLayout() {
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.setScrimColor(Color.TRANSPARENT);
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
                        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.END);
                        break;
                    case R.id.fragment_setting:
                        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.START);
                        refreshSet();
                        break;
                }
            }

            @Override
            public void onDrawerClosed(View view) {
                switch (view.getId()) {
                    case R.id.fragment_citymanager:
                        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, Gravity.END);
                        break;
                    case R.id.fragment_setting:
                        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, Gravity.START);
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
        if (show) {
            drawerLayout.openDrawer(Gravity.START);
            drawerLayout.closeDrawer(Gravity.END);
        } else {
            drawerLayout.closeDrawer(Gravity.START);
            drawerLayout.closeDrawer(Gravity.END);
        }
    }

    /**
     * 是否滑出设置
     * @param show
     */
    public void showSetting(boolean show) {
        if (show) {
            drawerLayout.closeDrawer(Gravity.START);
            drawerLayout.openDrawer(Gravity.END);
        } else {
            drawerLayout.closeDrawer(Gravity.START);
            drawerLayout.closeDrawer(Gravity.END);
        }
    }

    /**
     * 是否锁定drawer
     * @param lock true: 锁定(不可滑出drawer) false: 不锁定(可滑出drawer)
     */
    public void lockDrawer(boolean lock) {
        if (lock) {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        } else {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
    }

    /**
     * 初始化viewpager
     */
    private void initViewPager() {
        int columnSize = columnList.size();
        if (columnSize <= 1) {
            llContainer.setVisibility(View.GONE);
        }
        llContainer.removeAllViews();
        fragments.clear();
        for (int i = 0; i < columnSize; i++) {
            Fragment fragment = null;
            ColumnDto dto = columnList.get(i);
            LinearLayout ll = new LinearLayout(this);
            ll.setOrientation(LinearLayout.VERTICAL);
            ll.setGravity(Gravity.CENTER_HORIZONTAL);
            ll.setPadding(0, 10, 0, 5);
            ll.setTag(dto.dataCode);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.weight = 1;
            ll.setLayoutParams(params);
            ll.setOnClickListener(new MyOnClickListener(i));
            ImageView iv = new ImageView(this);
            iv.setAdjustViewBounds(true);
            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params1.width = (int)CommonUtil.dip2px(this, 25f);
            params1.height = (int)CommonUtil.dip2px(this, 25f);
            iv.setLayoutParams(params1);
            TextView tvName = new TextView(this);
            tvName.setGravity(Gravity.CENTER);
            tvName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
            tvName.setTextColor(getResources().getColor(R.color.text_black_common));
            tvName.setText(dto.dataName);
            if (i == 0) {
                tvName.setTextColor(getResources().getColor(R.color.text_blue_common));
                if (TextUtils.equals(dto.dataCode, "1010301")) {
                    iv.setImageResource(R.drawable.radio_home_sel);
                    fragment = new FragmentHomeWeather();
                    mFragmentHomeWeather = new FragmentHomeWeather();
                } else if (TextUtils.equals(dto.dataCode, "1010302")) {
                    iv.setImageResource(R.drawable.radio_product_sel);
                    fragment = new FragmentProduct();
                } else if (TextUtils.equals(dto.dataCode, "1010303")) {
                    iv.setImageResource(R.drawable.radio_service_sel);
                    fragment = new FragmentService();
                } else if (TextUtils.equals(dto.dataCode, "1010304")) {
                    iv.setImageResource(R.drawable.radio_live_sel);
                    fragment = new FragmentLife();
                }
            } else {
                tvName.setTextColor(getResources().getColor(R.color.text_black_common));
                if (TextUtils.equals(dto.dataCode, "1010301")) {
                    iv.setImageResource(R.drawable.radio_home_nor);
                    fragment = new FragmentHomeWeather();
                    mFragmentHomeWeather = new FragmentHomeWeather();
                } else if (TextUtils.equals(dto.dataCode, "1010302")) {
                    iv.setImageResource(R.drawable.radio_product_nor);
                    fragment = new FragmentProduct();
                } else if (TextUtils.equals(dto.dataCode, "1010303")) {
                    iv.setImageResource(R.drawable.radio_service_nor);
                    fragment = new FragmentService();
                } else if (TextUtils.equals(dto.dataCode, "1010304")) {
                    iv.setImageResource(R.drawable.radio_live_nor);
                    fragment = new FragmentLife();
                }
            }

            ll.addView(iv);
            ll.addView(tvName);
            llContainer.addView(ll, i);

            if (fragment != null) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("data", dto);
                fragment.setArguments(bundle);
                fragments.add(fragment);
            }
        }

        viewPager = findViewById(R.id.viewPager);
        viewPager.setSlipping(false);//设置ViewPager是否可以滑动
//        viewPager.setOffscreenPageLimit(fragments.size());
        viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
        viewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager(), fragments));
    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageSelected(int arg0) {
            if (llContainer != null) {
                for (int i = 0; i < llContainer.getChildCount(); i++) {
                    LinearLayout ll = (LinearLayout) llContainer.getChildAt(i);
                    ImageView iv = (ImageView) ll.getChildAt(0);
                    TextView tvName = (TextView) ll.getChildAt(1);
                    if (i == arg0) {
                        tvName.setTextColor(getResources().getColor(R.color.text_blue_common));
                        if (TextUtils.equals(ll.getTag()+"", "1010301")) {
                            iv.setImageResource(R.drawable.radio_home_sel);
                            FragmentHomeWeather.HomeRefreshParam param = new FragmentHomeWeather.HomeRefreshParam();
                            param.isChangedCity = true;
                            refreshData(param, true);
                            lockDrawer(false);
                        } else if (TextUtils.equals(ll.getTag()+"", "1010302")) {
                            iv.setImageResource(R.drawable.radio_product_sel);
                            lockDrawer(true);
                        } else if (TextUtils.equals(ll.getTag()+"", "1010303")) {
                            if (!BROADCAST_ACTION_NAME.contains(FragmentService.class.getName())) {
                                Intent intent = new Intent();
                                intent.setAction(FragmentService.class.getName());
                                sendBroadcast(intent);
                                BROADCAST_ACTION_NAME += FragmentService.class.getName();
                            }
                            iv.setImageResource(R.drawable.radio_service_sel);
                            lockDrawer(true);
                        } else if (TextUtils.equals(ll.getTag()+"", "1010304")) {
                            iv.setImageResource(R.drawable.radio_live_sel);
                            lockDrawer(true);
                        }
                    } else {
                        tvName.setTextColor(getResources().getColor(R.color.text_black_common));
                        if (TextUtils.equals(ll.getTag()+"", "1010301")) {
                            iv.setImageResource(R.drawable.radio_home_nor);
                            lockDrawer(false);
                        } else if (TextUtils.equals(ll.getTag()+"", "1010302")) {
                            iv.setImageResource(R.drawable.radio_product_nor);
                            lockDrawer(true);
                        } else if (TextUtils.equals(ll.getTag()+"", "1010303")) {
                            iv.setImageResource(R.drawable.radio_service_nor);
                            lockDrawer(true);
                        } else if (TextUtils.equals(ll.getTag()+"", "1010304")) {
                            iv.setImageResource(R.drawable.radio_live_nor);
                            lockDrawer(true);
                        }
                    }
                }
            }
        }
        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }
        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }

    /**
     * 头标点击监听
     * @author shawn_sun
     */
    private class MyOnClickListener implements View.OnClickListener {
        private int index;

        private MyOnClickListener(int i) {
            index = i;
        }

        @Override
        public void onClick(View v) {
            if (viewPager != null) {
                viewPager.setCurrentItem(index, true);
            }
        }
    }

    private void initPrivacy() {
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
        TextView tvNegtive = view.findViewById(R.id.tvNegtive);
        TextView tvPositive = view.findViewById(R.id.tvPositive);

        final Dialog dialog = new Dialog(this, R.style.CustomProgressDialog);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        tvMessage.setText("隐私服务协议");
        tvContent.setText("感谢您使用“天津惠民”。根据我国网络信息安全相关法律法规的要求，我公司制定了《天津惠民隐私政策》和《天津惠民用户服务协议》，对使用过程中可能出现的个人信息收集、使用、共享和保护等情况进行说明。为了您更好地了解并使用相关服务，请在使用前认真阅读完整版隐私政策。您需确认同意后方可使用“天津惠民”。我公司将尽全力保护您的个人信息安全。");
        tvPrivacy.setText("《天津惠民软件用户隐私政策》");
        tvPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityMain.this, ActivityWebView.class);
                intent.putExtra("title", "天津惠民软件用户隐私政策");
                intent.putExtra("url", CONST.PROTOCAL);
                intent.putExtra("shareContent", "天津惠民软件用户隐私政策");
                startActivity(intent);
            }
        });
        tvProtocal.setText("《天津惠民软件许可及服务协议》");
        tvProtocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityMain.this, ActivityWebView.class);
                intent.putExtra("title", "天津惠民软件许可及服务协议");
                intent.putExtra("url", CONST.PRIVACY);
                intent.putExtra("shareContent", "天津惠民软件许可及服务协议");
                startActivity(intent);
            }
        });
        tvNegtive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        tvPositive.setOnClickListener(new View.OnClickListener() {
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

    /**
     * 处理升级
     */
    @SuppressLint("HandlerLeak")
    private final Handler handlerVersion = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                if (mFileDownload == null) {
                    mFileDownload = new PcsFileDownload();
                }
                View viewdownload = LayoutInflater.from(ActivityMain.this).inflate(R.layout.dialog_download, null);
                desc_download = viewdownload.findViewById(R.id.desc_download);
                progerssBar = viewdownload.findViewById(R.id.progressbar);
                checkDialogdownload = new DialogOneButton(ActivityMain.this, viewdownload, "取消", new DialogFactory.DialogListener() {
                    @Override
                    public void click(String str) {
                        checkDialogdownload.dismiss();
                        mFileDownload.cancel();
                    }
                });
                checkDialogdownload.setTitle("正在下载");
                checkDialogdownload.show();
                String[] appname = packcheckversion.file.split("/");
                mFileDownload.downloadFile(downloadlistener, getString(R.string.file_download_url) + packcheckversion.file,
                        PcsGetPathValue.getInstance().getAppPath() + appname[appname.length - 1]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if (mImageFetcher != null && !mFetcherResumed) {
            mFetcherResumed = true;
            mImageFetcher.setExitTasksEarly(false);
        }
        MobclickAgent.onResume(this);

        // 添加定位监听
        ZtqLocationTool.getInstance().addListener(mLocationListener);

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

        //定位监听
        ZtqLocationTool.getInstance().removeListener(mLocationListener);
        //停止首页数据下载
        AutoDownloadWeather.getInstance().setMainDataPause(true);
        AutoDownloadWeather.getInstance().stopMainData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
        if (mImageFetcher != null) {
            mImageFetcher.closeCache();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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

    private void refreshData(InterfaceRefresh.RefreshParam param, boolean isShowProgress) {
        if (mRefreshView == null) {
            return;
        }
        // 刷新首页
        if (mFragmentHomeWeather != null) {
            mFragmentHomeWeather.myRefreshView.refresh(param);
        }
        // 侧边栏
        if (mFragmentLeft != null) {
            mFragmentLeft.refresh(param);
        }
        // 推送
        ZtqPushTool.getInstance().refreshPush();
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
        mProgressDialog.setMessage(getResources().getString(R.string.please_wait));
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
    private final DialogInterface.OnCancelListener mProgressOnCancel = new DialogInterface.OnCancelListener() {
        @Override
        public void onCancel(DialogInterface dialog) {
//            if (mRadioListener.getCurrentIndex() == 2) {
//                // 气象服务
//            } else {
                new AlertDialog.Builder(ActivityMain.this)
                        .setTitle(R.string.tip)
                        .setMessage(R.string.exit_confirm)
                        .setPositiveButton(R.string.exit, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        exit();
                                    }
                                })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).create().show();
//            }
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
        if (cityMain == null) {
            // 选择城市
            toCityListActivity();
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
            return;
        }

        PackageManager packageManager = getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo;
        int version = 0;
        try {
            packInfo = packageManager.getPackageInfo(ActivityMain.this.getPackageName(), 0);
            version = packInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (Integer.parseInt(packcheckversion.nv) > version) {
            View view = LayoutInflater.from(ActivityMain.this).inflate(R.layout.dialog_message, null);
            ((TextView) view.findViewById(R.id.dialogmessage)).setText(packcheckversion.des);
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
        return activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI;
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
        public void progress(String url, String fileName, long netSize, long downSize) {
            if (checkDialogdownload.isShowing()) {
                progerssBar.setMax((int) netSize);
                progerssBar.setProgress((int) downSize);
                float press = ((float) downSize / (float) netSize) * 100f;
                desc_download.setText(String.format("%.2f", press) + "%");
            }
        }

        @Override
        public void downloadSucc(String url, String fileName) {
            try {
                if (checkDialogdownload.isShowing()) {
                    checkDialogdownload.dismiss();
                }
                String[] appname = packcheckversion.file.split("/");
                File file = new File(PcsGetPathValue.getInstance().getAppPath() + appname[appname.length - 1]);
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
    private final ZtqLocationTool.PcsLocationListener mLocationListener = new ZtqLocationTool.PcsLocationListener() {
        @Override
        public void onLocationChanged() {
            // 刷新定位数据
            mHandler.sendEmptyMessage(0);
        }
    };

    private final Handler mHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            refreshData(null, false);
        }
    };

    /**
     * 刷新视图
     */
    private final InterfaceRefresh mRefreshView = new InterfaceRefresh() {
        @Override
        public void refresh(RefreshParam param) {
            refreshData(param, false);
        }
    };

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
            Toast.makeText(this, getString(R.string.once_again_exit), Toast.LENGTH_SHORT).show();
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
            editor.apply();
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
                try {
                    JSONObject param = new JSONObject();
                    param.put("loginName", uName);
                    param.put("pwd", pwd);
                    String json = param.toString();
                    String url = CONST.BASE_URL+"user/login";
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

                                                Toast.makeText(ActivityMain.this, getString(R.string.login_succ), Toast.LENGTH_SHORT).show();
                                                if(loginDialog != null) {
                                                    loginDialog.dismiss();
                                                }

                                                okHttpColumn();
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

    /**
     * 获取栏目信息
     */
    private void okHttpColumn() {
        showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    JSONObject info = new JSONObject();
                    info.put("userId", MyApplication.UID);
                    param.put("paramInfo", info);
                    String json = param.toString();
                    Log.e("tjmoduleList", json);
                    String url = CONST.BASE_URL+"tjmoduleList";
                    Log.e("tjmoduleList", url);
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
                                    Log.e("tjmoduleList", result);
                                    if (!TextUtils.isEmpty(result)) {
                                        try {
                                            JSONObject obj = new JSONObject(result);
                                            if (!obj.isNull("result")) {
                                                columnList.clear();
                                                JSONArray array = obj.getJSONArray("result");
                                                for (int i = 0; i < array.length(); i++) {
                                                    JSONObject itemObj = array.getJSONObject(i);
                                                    ColumnDto dto = new ColumnDto();
                                                    parseItemObj(itemObj, dto);
                                                    if (!itemObj.isNull("childList")) {
                                                        List<ColumnDto> childList = new ArrayList<>();
                                                        JSONArray itemArray = itemObj.getJSONArray("childList");
                                                        for (int j = 0; j < itemArray.length(); j++) {
                                                            JSONObject itemObj2 = itemArray.getJSONObject(j);
                                                            ColumnDto dto2 = new ColumnDto();
                                                            parseItemObj(itemObj2, dto2);
                                                            if (!itemObj2.isNull("childList")) {
                                                                List<ColumnDto> childList2 = new ArrayList<>();
                                                                JSONArray itemArray2 = itemObj2.getJSONArray("childList");
                                                                for (int m = 0; m < itemArray2.length(); m++) {
                                                                    JSONObject itemObj3 = itemArray2.getJSONObject(m);
                                                                    ColumnDto dto3 = new ColumnDto();
                                                                    parseItemObj(itemObj3, dto3);
                                                                    if (!itemObj3.isNull("childList")) {
                                                                        List<ColumnDto> childList3 = new ArrayList<>();
                                                                        JSONArray itemArray3 = itemObj3.getJSONArray("childList");
                                                                        for (int n = 0; n < itemArray3.length(); n++) {
                                                                            JSONObject itemObj4 = itemArray3.getJSONObject(n);
                                                                            ColumnDto dto4 = new ColumnDto();
                                                                            parseItemObj(itemObj4, dto4);
                                                                            if (!itemObj4.isNull("childList")) {
                                                                                List<ColumnDto> childList4 = new ArrayList<>();
                                                                                JSONArray itemArray4 = itemObj4.getJSONArray("childList");
                                                                                for (int a = 0; a < itemArray4.length(); a++) {
                                                                                    JSONObject itemObj5 = itemArray4.getJSONObject(a);
                                                                                    ColumnDto dto5 = new ColumnDto();
                                                                                    parseItemObj(itemObj5, dto5);
                                                                                    childList4.add(dto5);
                                                                                }
                                                                                dto4.childList.addAll(childList4);
                                                                            }
                                                                            childList3.add(dto4);
                                                                        }
                                                                        dto3.childList.addAll(childList3);
                                                                    }
                                                                    childList2.add(dto3);
                                                                }
                                                                dto2.childList.addAll(childList2);
                                                            }
                                                            childList.add(dto2);
                                                        }
                                                        dto.childList.addAll(childList);
                                                    }
                                                    columnList.add(dto);
                                                }
                                                init();
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

    private void parseItemObj(JSONObject itemObj, ColumnDto dto) {
        try {
            if (!itemObj.isNull("dataCode")) {
                dto.dataCode = itemObj.getString("dataCode");
            }
            if (!itemObj.isNull("dataName")) {
                dto.dataName = itemObj.getString("dataName");
            }
            if (!itemObj.isNull("parentId")) {
                dto.parentId = itemObj.getString("parentId");
            }
            if (!itemObj.isNull("icon")) {
                dto.icon = itemObj.getString("icon");
            }
            if (!itemObj.isNull("flag")) {
                dto.flag = itemObj.getString("flag");
            }
            if (!itemObj.isNull("url")) {
                dto.url = itemObj.getString("url");
            }
            if (!itemObj.isNull("desc")) {
                dto.desc = itemObj.getString("desc");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
