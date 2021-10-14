package com.pcs.ztqtj.view.activity.photoshow;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
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
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
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
import android.widget.TextView;
import android.widget.Toast;

import com.pcs.lib.lib_pcs_v3.control.file.PcsGetPathValue;
import com.pcs.lib.lib_pcs_v3.model.image.ImageResizer;
import com.pcs.lib_ztqfj_v2.model.pack.net.photowall.PackPhotoSingle;
import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.photo.AdapterPhotoShow;
import com.pcs.ztqtj.control.inter.InterfaceRefresh;
import com.pcs.ztqtj.control.listener.ListenerRefreshTouch;
import com.pcs.ztqtj.control.listener.ListenerRefreshTouch.InterfaceScrollView;
import com.pcs.ztqtj.control.main_weather.FragmentAd;
import com.pcs.ztqtj.control.tool.CommUtils;
import com.pcs.ztqtj.control.tool.PermissionsTools;
import com.pcs.ztqtj.control.tool.utils.TextUtil;
import com.pcs.ztqtj.model.PhotoShowDB;
import com.pcs.ztqtj.model.PhotoShowDB.PhotoShowDBListener;
import com.pcs.ztqtj.model.PhotoShowDB.PhotoShowType;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.util.OkHttpUtil;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
import com.pcs.ztqtj.view.myview.MainViewPager;
import com.pcs.ztqtj.view.myview.ViewPulldownRefresh;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
        // 请求数据
        mPhotoShowDB.reqNextPage(getDataType);
        okHttpAd();
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
        viewPager = findViewById(R.id.viewPager);
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

    /**
     * 点击个人中心
     */
    private void clickUserCenter() {
        if (ZtqCityDB.getInstance().isLoginService()) {
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

    /**
     * 获取广告
     */
    private void okHttpAd() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param  = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    JSONObject info = new JSONObject();
                    info.put("ad_type", "B002");
                    param.put("paramInfo", info);
                    String json = param.toString();
                    final String url = CONST.BASE_URL+"ad_list";
                    Log.e("ad_list", url);
                    RequestBody body = FormBody.create(MediaType.parse("application/json; charset=utf-8"), json);
                    OkHttpUtil.enqueue(new Request.Builder().post(body).url(url).build(), new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        }
                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            if (!response.isSuccessful()) {
                                return;
                            }
                            final String result = response.body().string();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (!TextUtil.isEmpty(result)) {
                                        initViewPager(result);
                                    } else {
                                        viewPager.setVisibility(View.GONE);
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

    private MainViewPager viewPager = null;
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private void initViewPager(String result) {
        fragments.clear();
        try {
            JSONObject obj = new JSONObject(result);
            if (!obj.isNull("b")) {
                JSONObject bObj = obj.getJSONObject("b");
                if (!bObj.isNull("ad")) {
                    JSONObject adObj = bObj.getJSONObject("ad");
                    if (!adObj.isNull("ad_list")) {
                        JSONArray array = adObj.getJSONArray("ad_list");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject itemObj = array.getJSONObject(i);
                            String imgUrl = getResources().getString(R.string.msyb) + itemObj.getString("img_path");
                            String name = itemObj.getString("title");
                            String dataUrl = itemObj.getString("url");
                            Fragment fragment = new FragmentAd();
                            Bundle bundle = new Bundle();
                            bundle.putString("imgUrl", imgUrl);
                            bundle.putString("name", name);
                            bundle.putString("dataUrl", dataUrl);
                            fragment.setArguments(bundle);
                            fragments.add(fragment);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (fragments.size() > 0) {
            viewPager.setVisibility(View.VISIBLE);
        } else {
            viewPager.setVisibility(View.GONE);
        }
        viewPager.setAdapter(new MyPagerAdapter());
        viewPager.setSlipping(true);//设置ViewPager是否可以滑动
        viewPager.setOnPageChangeListener(new MyOnPageChangeListener());

        mHandler.sendEmptyMessageDelayed(AUTO_PLUS, PHOTO_CHANGE_TIME);
    }

    private final int AUTO_PLUS = 1;
    private static final int PHOTO_CHANGE_TIME = 2000;//定时变量
    private int index_plus = 0;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AUTO_PLUS:
                    viewPager.setCurrentItem(index_plus++);//收到消息后设置当前要显示的图片
                    mHandler.sendEmptyMessageDelayed(AUTO_PLUS, PHOTO_CHANGE_TIME);
                    if (index_plus >= fragments.size()) {
                        index_plus = 0;
                    }
                    break;
                default:
                    break;
            }
        };
    };

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageSelected(int arg0) {
            index_plus = arg0;
        }
        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }
        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }

    private class MyPagerAdapter extends PagerAdapter {
        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            try {
                ((ViewPager) container).removeView(fragments.get(position).getView());
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = fragments.get(position);
            if (!fragment.isAdded()) { // 如果fragment还没有added
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.add(fragment, fragment.getClass().getSimpleName());
                ft.commit();
                /**
                 * 在用FragmentTransaction.commit()方法提交FragmentTransaction对象后
                 * 会在进程的主线程中,用异步的方式来执行。
                 * 如果想要立即执行这个等待中的操作,就要调用这个方法(只能在主线程中调用)。
                 * 要注意的是,所有的回调和相关的行为都会在这个调用中被执行完成,因此要仔细确认这个方法的调用位置。
                 */
                getFragmentManager().executePendingTransactions();
            }

            if (fragment.getView().getParent() == null) {
                container.addView(fragment.getView()); // 为viewpager增加布局
            }
            return fragment.getView();
        }
    }

}
