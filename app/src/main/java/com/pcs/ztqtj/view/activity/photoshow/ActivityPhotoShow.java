package com.pcs.ztqtj.view.activity.photoshow;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pcs.lib.lib_pcs_v3.control.file.PcsGetPathValue;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib.lib_pcs_v3.model.image.ImageResizer;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalUser;
import com.pcs.lib_ztqfj_v2.model.pack.net.BannerInfo;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackBannerDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackBannerUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.photowall.PackPhotoSingle;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.AdapterControlMainRow8;
import com.pcs.ztqtj.control.adapter.photo.AdapterPhotoShow;
import com.pcs.ztqtj.control.inter.ImageClick;
import com.pcs.ztqtj.control.inter.InterfaceRefresh;
import com.pcs.ztqtj.control.listener.ListenerRefreshTouch;
import com.pcs.ztqtj.control.listener.ListenerRefreshTouch.InterfaceScrollView;
import com.pcs.ztqtj.control.tool.CommUtils;
import com.pcs.ztqtj.control.tool.PermissionsTools;
import com.pcs.ztqtj.model.PhotoShowDB;
import com.pcs.ztqtj.model.PhotoShowDB.PhotoShowDBListener;
import com.pcs.ztqtj.model.PhotoShowDB.PhotoShowType;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
import com.pcs.ztqtj.view.activity.web.ActivityWeatherDay;
import com.pcs.ztqtj.view.activity.web.MyWebView;
import com.pcs.ztqtj.view.myview.LeadPoint;
import com.pcs.ztqtj.view.myview.ViewPulldownRefresh;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 实景开拍主页面
 */
public class ActivityPhotoShow extends FragmentActivityZtqBase {

    public static String CITY_ID = "CITY_ID";
    // 图片最大长度
    private final int PHOTO_MAX_PENGTH = 1920;
    // 相册
    private final int REQUEST_ALBUM = 101;
    // 拍照
    private final int REQUEST_CAMERA = 102;

    private String imgType = "1";//imgType:图片类型，1（实景开拍），2（农业开拍分类）必须传，区分哪个业务

    // 数据
    private PhotoShowDB mPhotoShowDB;
    // 适配器
    private AdapterPhotoShow mAdapter;
    // 弹出框
    private PopupWindow mPopupWindow;
    // 首次显示弹出框
    private boolean isFirstPopup = true;
    // 是否允许滚动？
    private boolean mIsScrollable = true;
    // 正在加载？
    private boolean mIsLoading = true;
    // 图片文件
    private File mFilePhoto = null;

    private ViewPager vp;
    private int pagerCurrentPosition = 0;
    private LeadPoint pointlayout;
    private static final long delayMillis = 5000;
    private AdapterControlMainRow8 adapterAdvertisement;
    private MyReceiver receiver = new MyReceiver();
    private PackBannerUp packBannerUp = new PackBannerUp();
    // banner广告list
    private List<BannerInfo> bannerList = new ArrayList<>();
    // banner广告活动地址
    private List<String> urlList = new ArrayList<>();

    /**
     * 默认去的是普通图片。不是精选图片
     */
    private PhotoShowType getDataType = PhotoShowType.ORDINARY;

    private List<PackPhotoSingle> photoList;

    private enum SlideDirection {
        LEFTIN, RIGHTIN, STOPSLIDE
    }

    /**
     * 从左边飞入
     */
    private SlideDirection isLeftIn = SlideDirection.STOPSLIDE;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_show);
        if (getIntent().hasExtra("title")) {
            String title = getIntent().getStringExtra("title");
            if (title != null) {
                setTitleText(title);
            }
        }
        createImageFetcher();
        showProgressDialog();
        String cityId = ZtqCityDB.getInstance().getCityMain().ID;
        imgType = getIntent().getStringExtra("imgType");
        // 初始化数据
        mPhotoShowDB = PhotoShowDB.getInstance();
        mPhotoShowDB.onCreate(this, cityId, imgType);
        mPhotoShowDB.setListener(mDBListener);
        // 初始化按钮
        initButton();
        // 初始化适配器
        initAdapter();
        // 初始化下拉刷新
        initPullDownRefresh();
        // 初始化弹出框
        initPopupWindow();
        initRadioGroup();
        // 初始化banner控件
        initBannerView();
        PcsDataBrocastReceiver.registerReceiver(this, receiver);
        // 请求数据
        mPhotoShowDB.reqNextPage(getDataType);
        reqBanner();
    }

    private void initRadioGroup() {
        RadioGroup radiogroup_chose = findViewById(R.id.radiogroup_chose);
        radiogroup_chose.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        if (checkedId == R.id.ordinary_button) {
                            getDataType = PhotoShowType.ORDINARY;
                            isLeftIn = SlideDirection.LEFTIN;
                        } else if (checkedId == R.id.special_button) {
                            getDataType = PhotoShowType.SPECIAL;
                            isLeftIn = SlideDirection.RIGHTIN;
                        }

                        for (int i = 0; i < group.getChildCount(); i++) {
                            TextView tv = (TextView) group.getChildAt(i);
                            if (checkedId == tv.getId()) {
                                tv.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
                                tv.setTextColor(getResources().getColor(R.color.text_blue));
                            } else {
                                tv.getPaint().setFlags(0);
                                tv.setTextColor(getResources().getColor(R.color.text_black));
                            }
                        }

                        photoList.clear();
                        showProgressDialog();
                        mPhotoShowDB.clearData();
                        mPhotoShowDB.reqNextPage(getDataType);
                    }
                });

        TextView firstTv = findViewById(R.id.ordinary_button);
        firstTv.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        firstTv.setTextColor(getResources().getColor(R.color.text_blue));
    }

    @Override
    public void onResume() {
        super.onResume();
        mPhotoShowDB.setListener(mDBListener);
        mPhotoShowDB.onResume();
        if (mPhotoShowDB.getRefreshType() == PhotoShowDB.PhotoRefreshType.DATA) {
            // 刷新数据
            mPhotoShowDB.clearData();
            mPhotoShowDB.reqNextPage(getDataType);
            mPhotoShowDB.setRefreshType(PhotoShowDB.PhotoRefreshType.NO_NEED);
        }
        if (mPhotoShowDB.getRefreshType() == PhotoShowDB.PhotoRefreshType.VIEW) {
            photoList.clear();
            // 刷新视图
            photoList.addAll(mPhotoShowDB.getPhotoList(getDataType));
            mAdapter.notifyDataSetChanged();
            mPhotoShowDB.setRefreshType(PhotoShowDB.PhotoRefreshType.NO_NEED);
        }
        // banner
        if (bannerList == null || bannerList.size() == 0) {
        } else if (pagerCurrentPosition == 0) {
            pagerCurrentPosition = ((adapterAdvertisement.getCount() / bannerList.size()) / 2) * bannerList.size();
            if (vp != null && adapterAdvertisement != null) {
                vp.setCurrentItem(pagerCurrentPosition);
                moveToNextPager();
            }
        } else {
            if (vp != null && adapterAdvertisement != null) {
                vp.setCurrentItem(pagerCurrentPosition);
                moveToNextPager();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mPhotoShowDB.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPhotoShowDB.onDestory();
        if (receiver != null) {
            PcsDataBrocastReceiver.unregisterReceiver(this, receiver);
            receiver = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_ALBUM:
                // 相册
                resultAlbum(intent);
                break;
            case REQUEST_CAMERA:
                // 相机
                resultCamera(intent);
                break;
        }
    }

    /**
     * 初始化按钮
     */
    private void initButton() {
        // 个人中心
//        setBtnRight(R.drawable.icon_photo_show_user_new, mOnClick);
        setBtnRight2(R.drawable.icon_take_picture_new_2, mOnClick);
        // 拍照
        //setBtnRight2(R.drawable.btn_camera1, mOnClick);
        findViewById(R.id.iv_take_picture).setOnClickListener(mOnClick);
    }

    /**
     * 初始化适配器
     */
    private void initAdapter() {
        photoList = new ArrayList<>();
        GridView gridView = findViewById(R.id.gridView);
        mAdapter = new AdapterPhotoShow(ActivityPhotoShow.this, getImageFetcher(), photoList);
        gridView.setAdapter(mAdapter);
        gridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mIsLoading) {
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra("imgType", imgType);
                intent.putExtra("position", position);
                if (getDataType == PhotoShowType.ORDINARY) {
                    intent.putExtra("isSpecial", false);
                } else if (getDataType == PhotoShowType.SPECIAL) {
                    intent.putExtra("isSpecial", true);
                }
                intent.setClass(ActivityPhotoShow.this, ActivityPhotoDetail.class);
                startActivity(intent);
            }
        });
    }

    /**
     * 初始化下拉刷新
     */
    private void initPullDownRefresh() {
        // 下拉视图
        View pullLayout = findViewById(R.id.layout_pulldown);
        ViewPulldownRefresh viewPullDown = new ViewPulldownRefresh(this, pullLayout);
        // 滚动监听
        GridView gridView = findViewById(R.id.gridView);
        MyOnScrollListener refreshScroll = new MyOnScrollListener();
        gridView.setOnScrollListener(refreshScroll);
        // 触摸监听
        MyListenerRefreshTouch refreshTouch = new MyListenerRefreshTouch(
                this.getWindowManager(), viewPullDown, mRefreshEnd,
                mRefreshBegin, refreshScroll);
        gridView.setOnTouchListener(refreshTouch);
    }

    /**
     * 初始化弹出框
     */
    private void initPopupWindow() {
        View view = LayoutInflater.from(this).inflate(R.layout.pop_photograph, null);
        mPopupWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                // 设置背景颜色变暗
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1.0f;
                getWindow().setAttributes(lp);
            }
        });
        // 设置可以获得焦点
        mPopupWindow.setFocusable(true);
        // 设置弹窗内可点击
        mPopupWindow.setTouchable(true);
        // 设置弹窗外可点击
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());

        view.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dismissPopupWindow();
                    return true;
                }
                return false;
            }
        });
        View btn;
        // 相机按钮
        btn = view.findViewById(R.id.btnCamera);
        btn.setOnClickListener(mOnClick);
        // 相册按钮
        btn = view.findViewById(R.id.btnAlbum);
        btn.setOnClickListener(mOnClick);
        // 取消按钮
        btn = view.findViewById(R.id.btnCancel);
        btn.setOnClickListener(mOnClick);
    }

    /**
     * 关闭弹出框
     */
    private void dismissPopupWindow() {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
    }

    /**
     * 设置底部加载View
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
    private  String perfixUrl;

    /**
     * 获取数据完成
     */
    private void initBanner() {
        pagerCurrentPosition = 0;
        perfixUrl = getResources().getString(R.string.file_download_url);
        urlList.clear();
        for (BannerInfo info : bannerList) {
            urlList.add(perfixUrl + info.img_path);
        }
        adapterAdvertisement = new AdapterControlMainRow8(urlList, imageClick, getImageFetcher());
        vp.setAdapter(adapterAdvertisement);
        if (bannerList.size() == 0) {
            // 如果大小为0的话则不需要计算当前位置
            layout_banner.setVisibility(View.GONE);
        } else {
            layout_banner.setVisibility(View.VISIBLE);
            // 不为0则计算当前位置
            pagerCurrentPosition = ((adapterAdvertisement.getCount() / bannerList.size()) / 2) * bannerList.size();
            vp.setCurrentItem(pagerCurrentPosition);
            pointlayout.initPoint(bannerList.size());
        }
        if (bannerList.size() > 1) {
            // 如果广告小于等于一个的话这不跳转播放
            moveToNextPager();
        }
    }

    private RelativeLayout layout_banner;
    private void initBannerView() {
         layout_banner = (RelativeLayout) findViewById(R.id.layout_banner);
        ViewGroup.LayoutParams rootParams = layout_banner.getLayoutParams();
        float scale = 17f / 36f;
        float screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        rootParams.height = (int) (screenWidth * scale);
        layout_banner.setLayoutParams(rootParams);
        vp = (ViewPager) findViewById(R.id.viewpager);
        pointlayout = (LeadPoint) findViewById(R.id.pointlayout);
        vp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
                pagerCurrentPosition = arg0;
                if (bannerList.size() > 1) {
                    pointlayout.setPointSelect(pagerCurrentPosition
                            % bannerList.size());
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

        vp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        brannerHandler.removeMessages(0);
                        break;
                    case MotionEvent.ACTION_UP:
                        moveToNextPager();
                        break;
                }
                return false;
            }
        });
    }

    /**
     * 展示下一张图片
     */
    private void moveToNextPager() {
        brannerHandler.sendEmptyMessageDelayed(0, delayMillis);
    }

    private final Handler brannerHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    brannerHandler.removeMessages(0);
                    vp.setCurrentItem(pagerCurrentPosition + 1);
                    moveToNextPager();
                    break;
            }
            return false;
        }
    });

    /**
     * 请求banner数据
     */
    private void reqBanner() {
        packBannerUp = new PackBannerUp();
        packBannerUp.position_id = "22";
        PcsDataDownload.addDownload(packBannerUp);
    }

    /**
     * 点击个人中心
     */
    private void clickUserCenter() {
        PackLocalUser localUser= ZtqCityDB.getInstance().getMyInfo();
        if (!TextUtils.isEmpty(localUser.user_id)) {
            Intent it = new Intent();
            it.setClass(this, ActivityUserCenter.class);
            this.startActivity(it);
        } else {
            // 未登录
            Intent it = new Intent();
            it.setClass(this, ActivityLogin.class);
            this.startActivity(it);
            return;
        }
    }

    /**
     * 点击拍照
     */
    private void clickPhotoGraph() {
        String status = Environment.getExternalStorageState();
        if (!status.equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, R.string.no_sdcard, Toast.LENGTH_SHORT).show();
            return;
        }
        GridView gridView = (GridView) findViewById(R.id.gridView);
        mPopupWindow.showAtLocation(gridView, Gravity.BOTTOM, 0, 0);
    }

    /**
     * 点击照相
     */
    private void clickCamera() {
        dismissPopupWindow();
        String tempStr = String.valueOf(System.currentTimeMillis());
        mFilePhoto = new File(PcsGetPathValue.getInstance().getMyPhotoPath() + tempStr + ".jpg");
        mFilePhoto.getParentFile().mkdirs();
        CommUtils.openCamera(this, mFilePhoto, REQUEST_CAMERA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CAMERA) {
            PermissionsTools.onRequestPermissionsResult(this, permissions, grantResults, new PermissionsTools.RequestPermissionResultCallback() {
                @Override
                public void onSuccess() {
                    clickCamera();
                }

                @Override
                public void onDeny() {

                }

                @Override
                public void onDenyNeverAsk() {

                }
            });
        }
    }

    /**
     * 点击相册
     */
    private void clickAlbum() {
        dismissPopupWindow();
        Intent it = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        this.startActivityForResult(it, REQUEST_ALBUM);
    }

    /**
     * 点击取消
     */
    private void clickCancel() {
        dismissPopupWindow();
    }

    /**
     * 从相机返回
     */
    private void resultCamera(Intent fromIntent) {
        if (mFilePhoto == null || !mFilePhoto.exists()) {
            Toast.makeText(this, R.string.photo_error, Toast.LENGTH_SHORT).show();
            return;
        }
        showProgressDialog();
        // 调整图片大小
        resizeImage();
    }

    /**
     * 从相册返回
     * @param fromIntent
     */
    private void resultAlbum(Intent fromIntent) {
        showProgressDialog();
        Uri uri = fromIntent.getData();
        String[] filePathColumns = {MediaStore.Images.Media.DATA};
        Cursor c = this.getContentResolver().query(uri, filePathColumns, null, null, null);
        if (c == null) {
            return;
        }
        c.moveToFirst();
        int columnIndex = c.getColumnIndex(filePathColumns[0]);
        String picturePath = c.getString(columnIndex);
        c.close();
        // ---------拷贝文件
        // 旧文件
        File oldFile = new File(picturePath);
        // 新文件
        mFilePhoto = new File(PcsGetPathValue.getInstance().getMyPhotoPath() + oldFile.getName());
        // 当前文件夹不是ztq文件夹
        if (!oldFile.getParent().equals(mFilePhoto.getParent())) {
            if (mFilePhoto.exists()) {
                mFilePhoto.delete();
            }
            mFilePhoto.getParentFile().mkdirs();
            if (!copyFile(oldFile, mFilePhoto)) {
                Toast.makeText(this, R.string.photo_error, Toast.LENGTH_SHORT).show();
                dismissProgressDialog();
                return;
            }
        }

        // 调整图片大小
        resizeImage();
    }

    /**
     * 调整图片大小
     */
    private void resizeImage() {
        Point point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);
        int maxSize = point.y;
        if (point.x > maxSize) {
            maxSize = point.x;
        }
        if (maxSize > PHOTO_MAX_PENGTH) {
            maxSize = PHOTO_MAX_PENGTH;
        }

        ImageResizer resizer = new ImageResizer();
        resizer.resizeSD(mFilePhoto.getPath(), maxSize, mImageResizerListener);
    }

    /**
     * 拷贝文件
     * @param oldFile
     * @param newFile
     */
    private boolean copyFile(File oldFile, File newFile) {
        if (!oldFile.exists()) {
            return false;
        }
        newFile.getParentFile().mkdirs();
        InputStream inStream = null;
        FileOutputStream fs = null;
        int byteread = 0;
        try {
            inStream = new FileInputStream(oldFile); // 读入原文件
            fs = new FileOutputStream(newFile);
            byte[] buffer = new byte[1444];
            while ((byteread = inStream.read(buffer)) != -1) {
                fs.write(buffer, 0, byteread);
            }
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inStream.close();
                fs.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 滚动触摸监听
     */
    private class MyListenerRefreshTouch extends ListenerRefreshTouch {
        public MyListenerRefreshTouch(WindowManager windowManager,
                                      InterfacePulldownView pulldownView,
                                      InterfaceRefresh refreshView, InterfaceRefresh refreshAnim,
                                      InterfaceScrollView scrollView) {
            super(windowManager, pulldownView, refreshView, refreshAnim, scrollView);
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            boolean b = super.onTouch(v, event);
            return b || !mIsScrollable || mIsLoading;
        }
    }

    /**
     * 滚动监听
     */
    private class MyOnScrollListener implements OnScrollListener, InterfaceScrollView {
        private int mFirstVisibleItem = 0;
        private int mVisibleItemCount = 0;
        private int mTotalItemCount = 0;
        private boolean isTouchChange = false;

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            // 有触摸
            isTouchChange = true;
            System.out.println("GridView scroll state change" + scrollState);
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            mFirstVisibleItem = firstVisibleItem;
            mVisibleItemCount = visibleItemCount;
            mTotalItemCount = totalItemCount;
            // 滚动到底部
            if (isTouchChange && isScrollBottom()) {
                if (mPhotoShowDB.reqNextPage(getDataType)) {
                    // 显示正在加载
                    setBottomView(true);
                    mIsLoading = true;
                }
            }
        }

        /**
         * 是否已滚动到顶部
         * @return
         */
        @Override
        public boolean isScrollTop() {
            if (mFirstVisibleItem == 0) {
                return true;
            }
            return false;
        }

        /**
         * 是否滚动到底部
         * @return
         */
        public boolean isScrollBottom() {
            if (mTotalItemCount != 0
                    && mFirstVisibleItem + mVisibleItemCount == mTotalItemCount) {
                return true;
            }
            return false;
        }

        @Override
        public void setScrollable(boolean b) {
            mIsScrollable = b;
        }
    }

    /**
     * 刷新视图的接口
     */
    public InterfaceRefresh mRefreshBegin = new InterfaceRefresh() {
        @Override
        public void refresh(RefreshParam param) {
            mPhotoShowDB.clearData();
            mPhotoShowDB.reqNextPage(getDataType);
            mIsLoading = true;
        }
    };

    /**
     * 刷新视图的接口
     */
    public InterfaceRefresh mRefreshEnd = new InterfaceRefresh() {
        @Override
        public void refresh(RefreshParam param) {

        }
    };

    private OnClickListener mOnClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_right:
                    // 个人中心
                    clickUserCenter();
                    break;
                case R.id.btn_right2:
                    // 拍照
                    clickPhotoGraph();
                    break;
                case R.id.btnCamera:

                    clickCamera();
                    break;
                case R.id.btnAlbum:
                    // 点击相机
                    clickAlbum();
                    break;
                case R.id.btnCancel:
                    // 点击相机
                    clickCancel();
                    break;
                case R.id.iv_take_picture:
                    clickPhotoGraph();
                    break;
            }
        }
    };

    private final ImageClick imageClick = new ImageClick() {
        @Override
        public void itemClick(Object path) {
            BannerInfo bean = null;
            for (BannerInfo info : bannerList) {
                if (path.toString().equals(getResources().getString(R.string.file_download_url) + info.img_path)) {
                    bean = info;
                    break;
                }
            }
            if (bean != null) {
                if (bean.title.equals("观云识天.随手拍")) {
//                    String url = "";
//                    if(LoginInformation.getInstance().hasLogin()) {
//                        String userid = LoginInformation.getInstance().getUserId();
//                        PackInitDown down = (PackInitDown) PcsDataManager.getInstance().getNetPack(PackInitUp.NAME);
//                        String pid = down.pid;
//                        url = bean.url + "?USER_ID=" + userid + "&PID=" + pid;
//                    } else {
//                        PackInitDown down = (PackInitDown) PcsDataManager.getInstance().getNetPack(PackInitUp.NAME);
//                        String pid = down.pid;
//                        url = bean.url + "?USER_ID=&PID=" + pid;
//                    }
                    toWeatherDay(bean.url, bean);
                } else {
                    toWebView(bean.url, bean.title);
                }
            }

        }
    };

    /**
     * 顶部图片列表点击进入下一级详情
     * @param path
     */
    private void toWebView(String path, String title) {
        if (TextUtils.isEmpty(path)||path.equals(perfixUrl)) {
            return;
        }
        Intent intent = new Intent(this, MyWebView.class);
        intent.putExtra("title", title);
        intent.putExtra("url", path);
        startActivity(intent);
    }

    private void toWeatherDay(String url, BannerInfo bean) {
        if (TextUtils.isEmpty(url)||url.equals(perfixUrl)) {
            return;
        }
        Intent intent = new Intent(this, ActivityWeatherDay.class);
        intent.putExtra("title", bean.title);
        intent.putExtra("url", url);
        intent.putExtra("BannerInfo", bean);
        startActivity(intent);
    }

    private AnimationSet getAnmation() {
        AnimationSet animation = new AnimationSet(true);
        if (isLeftIn == SlideDirection.LEFTIN) {
            TranslateAnimation ta = new TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, -1f,
                    Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f);
            ta.setDuration(150);
            animation.addAnimation(ta);
        } else if (isLeftIn == SlideDirection.RIGHTIN) {
            TranslateAnimation ta = new TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF,
                    0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f);
            ta.setDuration(150);
            animation.addAnimation(ta);
        }
        return animation;
    }

    /**
     * 数据监听
     */
    private PhotoShowDBListener mDBListener = new PhotoShowDBListener() {
        @Override
        public void done() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    photoList.clear();
                    photoList.addAll(mPhotoShowDB.getPhotoList(getDataType));
                    // 取消等待
                    dismissProgressDialog();
                    // 刷新列表
                    mAdapter.notifyDataSetChanged();
                    if (isLeftIn != SlideDirection.STOPSLIDE) {
                        GridView gridView = (GridView) findViewById(R.id.gridView);
                        gridView.startAnimation(getAnmation());
                        isLeftIn = SlideDirection.STOPSLIDE;
                    }
                    // 底部加载View
                    setBottomView(false);
                    mIsLoading = false;
                    // 暂无图片？
                    View view_nopicture = findViewById(R.id.text_nopicture);
                    if (mPhotoShowDB.hasPhotoList(getDataType)) {
                        view_nopicture.setVisibility(View.GONE);
                    } else {
                        view_nopicture.setVisibility(View.VISIBLE);
                    }
                    // 默认显示拍照菜单
                    if (isFirstPopup) {
                        clickPhotoGraph();
                        isFirstPopup = false;
                    }
                }
            });
        }
    };

    private ImageResizer.ImageResizerListener mImageResizerListener = new ImageResizer.ImageResizerListener() {

        @Override
        public void doneSD(String path, boolean isSucc) {
            showProgressDialog();
            // 前往提交页面
            Intent intent = new Intent();
            intent.setClass(ActivityPhotoShow.this, ActivityPhotoSubmit.class);
            intent.putExtra("photo_path", mFilePhoto.getPath());
            intent.putExtra("imgType", imgType);
            startActivity(intent);
            dismissProgressDialog();
        }
    };

    private class MyReceiver extends PcsDataBrocastReceiver {
        @Override
        public void onReceive(String nameStr, String errorStr) {
            if (nameStr.equals(packBannerUp.getName())) {
                PackBannerDown down = (PackBannerDown) PcsDataManager.getInstance().getNetPack(nameStr);
                if (down == null) {
                    return;
                }
                bannerList.clear();
                bannerList.addAll(down.arrBannerInfo);
                initBanner();
            }
        }
    }

}
