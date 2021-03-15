package com.pcs.ztqtj.view.activity.product;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Contacts;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.AdatperSatelliteCloudTab;
import com.pcs.ztqtj.control.tool.CommUtils;
import com.pcs.ztqtj.view.activity.FragmentActivityWithShare;
import com.pcs.ztqtj.view.myview.ImageTouchView;
import com.pcs.ztqtj.view.myview.MySeekBar;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib.lib_pcs_v3.model.image.ImageConstant;
import com.pcs.lib.lib_pcs_v3.model.image.ListenerImageLoad;
import com.pcs.lib_ztqfj_v2.model.pack.net.satellite.PackSatelliteDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.satellite.PackSatelliteDown.Satellite;
import com.pcs.lib_ztqfj_v2.model.pack.net.satellite.PackSatelliteListDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.satellite.PackSatelliteListUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.satellite.PackSatelliteUp;

import java.util.ArrayList;
import java.util.List;

/**
 * 卫星云图
 *
 * @author chenjh
 */
public class ActivitySatelliteCloudChart extends FragmentActivityWithShare
        implements View.OnClickListener, AnimationListener {

    private final MyReceiver receiver = new MyReceiver();
    private final PackSatelliteUp packSatelliteUp = new PackSatelliteUp();
    public List<Satellite> mySatelliteList = new ArrayList<Satellite>();

    private AdatperSatelliteCloudTab myBaseAdapter;

    /**
     * 卫星云图列表上传包
     */
    private PackSatelliteListUp mPackSatelliteListUp = new PackSatelliteListUp();

    /**
     * 卫星云图列表下载包
     */
    private PackSatelliteListDown mPackSatelliteListDown;

    /**
     * 当前栏目类型
     */
    private int currTab = 0;
    /**
     * 存放bitmap图片
     */
    private Bitmap[] comm_imgs;

    /**
     * 最大张数
     */
    private final int imgCount = 8;

    /**
     * 当前第几张
     */
    private int index = 1;

    private LinearLayout tabLayout;
    private MySeekBar myPopSeekBar;
    private TextView tvPopProgress;

    private SeekBar mSeekBar;

    private TextView tvProgress;
    private RelativeLayout mBottomBar;// 底部布局
    /**
     * 是否处于播放状态
     */
    private int status;
    /**
     * 暂停状态
     */
    private static final int STATUS_PAUSE = 1;
    /**
     * 播放状态
     */
    private static final int STATUS_START = 2;
    /**
     * 记录下载第几张图片
     */
    private int count = 0;
    /**
     * 是否为只显示最后一条信息
     */
    private boolean showLastImage = true;
    /**
     * 图片停留时间
     */
    private final int mAnimDelay = 1500;
    private TextView tabName;
    private TextView tabDateText;
    private PopupWindow window;
    private ImageTouchView mImage;
    private ImageView btnStart;
    private PopupWindow mPopWindow;

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    showNextImageView();
                    break;
                case 1:
                    hideButton();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 禁止休眠
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setTitleText("卫星云图");
        setContentView(R.layout.satellite_cloud_chart);
        createImageFetcher();
        initView();
        initEvent();
        initData();
    }

    private void initView() {
        tabLayout = (LinearLayout) findViewById(R.id.tab_layout);
        tabName = (TextView) findViewById(R.id.tab_name_station);
        // tabNameText = (TextView) findViewById(R.id.text_name);
        tabDateText = (TextView) findViewById(R.id.text_date);
        mBottomBar = (RelativeLayout) findViewById(R.id.bottom_bar);
        mImage = (ImageTouchView) findViewById(R.id.img);
        mImage.setHightFillScale(true);
        mImage.setImagePositon(ImageTouchView.StartPostion.ImageTJ);
        tvProgress = (TextView) findViewById(R.id.txt_progress);
        mSeekBar = (SeekBar) findViewById(R.id.seekBar);
        mSeekBar.setMax(imgCount - 1);
        btnStart = (ImageView) findViewById(R.id.btn_start);
    }

    private void initEvent() {
        findViewById(R.id.spinner_puzzle).setOnClickListener(this);
        //setBtnRight(R.drawable.maillist_button, this);
        btnStart.setOnClickListener(this);
        mImage.setTouchListener(imageTouchViewListener);
        mSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
    }

    private void initData() {
        showProgressDialog();
        // 注册广播接收
        PcsDataBrocastReceiver.registerReceiver(this, receiver);
        // 获取卫星云图列表
        getSatelliteList();
        comm_imgs = new Bitmap[imgCount];
        if (!isOpenNet()) {
            showToast(getString(R.string.open_netword));
        } else {
            if (!isWiFiNewWord()) {
                showToast(getString(R.string.un_wifi_desc));
            }
        }

    }

    /**
     * 获取卫星云图列表
     */
    private void getSatelliteList() {

//        if (!isOpenNet()) {
//            showToast(getString(R.string.net_err));
//            return;
//        }
        mPackSatelliteListUp = new PackSatelliteListUp();
        mPackSatelliteListDown =
                (PackSatelliteListDown) PcsDataManager.getInstance().getLocalPack(mPackSatelliteListUp.getName());
        dealListDown(false);
        PcsDataDownload.addDownload(mPackSatelliteListUp);
    }

    /**
     * 显示底部布局
     */
    private void showButton() {
        mHandler.removeMessages(1);
        if (mBottomBar != null && mBottomBar.getVisibility() != View.VISIBLE) {
            mBottomBar.setVisibility(View.VISIBLE);
            mBottomBar.startAnimation(AnimationUtils.loadAnimation(
                    getApplicationContext(), R.anim.fade_in));
        }
    }

    /**
     * 隐藏底部布局
     */
    private void hideButton() {
        if (mBottomBar != null && status == STATUS_START
                && mBottomBar.getVisibility() == View.VISIBLE) {
            Animation anim = AnimationUtils.loadAnimation(
                    getApplicationContext(), R.anim.fade_out);
            anim.setAnimationListener(this);
            mBottomBar.startAnimation(anim);
        }
    }

    /**
     * 获取图片信息，
     *
     * @param type 栏目： 云图类型: type=1.彩色云图、 type=2.红外云图、 type=3.可见光图、 type=4.水汽云图
     */
    private void getImageInformation(int type) {

//        if (!isOpenNet()) {
//            showToast(getString(R.string.net_err));
//            return;
//        }
        mHandler.removeMessages(1);
        showButton();
        mSeekBar.setEnabled(false);
        status = STATUS_PAUSE;
        btnStart.setImageResource(R.drawable.btn_play);
        btnStart.setEnabled(true);
        currTab = type;
        index = 1;
        count = 0;

        mSeekBar.setProgress(imgCount - 1);
        packSatelliteUp.type = (type) + "";
        packSatelliteUp.count = imgCount + "";
        PackSatelliteDown packSatelliteDown =
                (PackSatelliteDown) PcsDataManager.getInstance().getLocalPack(packSatelliteUp.getName());
        dealSatellite(packSatelliteDown, false);
        PcsDataDownload.addDownload(packSatelliteUp);
    }

    private void dealSatellite(PackSatelliteDown packSatelliteDown, boolean isnet) {
        if (packSatelliteDown == null) {
            if (isnet) {
                PackSatelliteDown down =
                        (PackSatelliteDown) PcsDataManager.getInstance().getLocalPack(packSatelliteUp.getName());
                if (down == null) {
                    return;
                }
                mySatelliteList.clear();
                mySatelliteList.addAll(down.satelliteList);
                refreshData();
            }
        } else {
            mySatelliteList.clear();
            mySatelliteList.addAll(packSatelliteDown.satelliteList);
            refreshData();
        }

    }


    private void dealListDown(boolean isnet) {
        if (mPackSatelliteListDown == null) {
            if (isnet) {
                mPackSatelliteListDown =
                        (PackSatelliteListDown) PcsDataManager.getInstance().getLocalPack(mPackSatelliteListUp.getName());
                if (mPackSatelliteListDown == null) {
                    return;
                }
                if (tabName != null && mPackSatelliteListDown.nephanalysis_list.size() > 0) {
                    tabName.setText(mPackSatelliteListDown.nephanalysis_list.get(0).name);
                    String type = mPackSatelliteListDown.nephanalysis_list.get(0).type;
                    if (!TextUtils.isEmpty(type)) {
                        try {
                            int typeInt = Integer.parseInt(type);
                            getImageInformation(typeInt);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } else {
            if (tabName != null && mPackSatelliteListDown.nephanalysis_list.size() > 0) {
                tabName.setText(mPackSatelliteListDown.nephanalysis_list.get(0).name);
                String type = mPackSatelliteListDown.nephanalysis_list.get(0).type;
                if (!TextUtils.isEmpty(type)) {
                    try {
                        int typeInt = Integer.parseInt(type);
                        getImageInformation(typeInt);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }


    /**
     * 数据更新广播接收
     */
    private class MyReceiver extends PcsDataBrocastReceiver {
        @Override
        public void onReceive(String name, String error) {
//            if (!TextUtils.isEmpty(error)) {
////                showToast(error);
//                return;
//            }

            if (packSatelliteUp != null && packSatelliteUp.getName().equals(name)) {
                dismissProgressDialog();
                PackSatelliteDown packSatelliteDown =
                        (PackSatelliteDown) PcsDataManager.getInstance().getNetPack(name);
                dealSatellite(packSatelliteDown, true);
            } else if (mPackSatelliteListUp != null && mPackSatelliteListUp.getName().equals(name)) {
                // 获取卫星云图列表成功
                mPackSatelliteListDown =
                        (PackSatelliteListDown) PcsDataManager.getInstance().getNetPack(name);
                dealListDown(true);
            }

//            if (packSatelliteUp!=null&&packSatelliteUp.getName().equals(name)) {
//                dismissProgressDialog();
//                PackSatelliteDown packSatelliteDown = (PackSatelliteDown) PcsDataManager.getInstance().getNetPack(name);
//                if (packSatelliteDown == null) {
//                    return;
//                }
//                mySatelliteList.clear();
//                mySatelliteList.addAll(packSatelliteDown.satelliteList);
//                refreshData();
//            } else if (mPackSatelliteListUp!=null&&mPackSatelliteListUp.getName().equals(name)) {
//                // 获取卫星云图列表成功
//                mPackSatelliteListDown = (PackSatelliteListDown) PcsDataManager.getInstance().getNetPack(name);
//                if (mPackSatelliteListDown == null) {
//                    return;
//                }
//                if (tabName != null && mPackSatelliteListDown.nephanalysis_list.size() > 0) {
//                    tabName.setText(mPackSatelliteListDown.nephanalysis_list.get(0).name);
//                    String type = mPackSatelliteListDown.nephanalysis_list.get(0).type;
//                    if (!TextUtils.isEmpty(type)) {
//                        try {
//                            int typeInt = Integer.parseInt(type);
//                            getImageInformation(typeInt);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//
//            }
        }
    }

    /**
     * 栏目列表
     *
     * @param v
     */
    private void loadListContent(View v) {
        int screenWidth = CommUtils.getScreenWidth(getApplicationContext());
        View view = View.inflate(this, R.layout.typhoon_list_dlg, null);
        final ListView listView = (ListView) view.findViewById(R.id.typhoon_list);
        myBaseAdapter = new AdatperSatelliteCloudTab(ActivitySatelliteCloudChart.this, mPackSatelliteListDown);
        listView.setAdapter(myBaseAdapter);
        window = new PopupWindow(view, screenWidth / 2, LayoutParams.WRAP_CONTENT, true);
        //window.setAnimationStyle(R.style.Popup_right);
        window.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_drag_content));
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                window.dismiss();
                showProgressDialog();
                tabName.setText(mPackSatelliteListDown.nephanalysis_list.get(position).name);
                draSelectPosition = position;
                getImageInformation(Integer.parseInt(mPackSatelliteListDown.nephanalysis_list.get(position).type));
            }
        });
        int[] coords = new int[2];
        v.getLocationOnScreen(coords);
        int y = v.getHeight() + coords[1];
        int x = coords[0];
        window.showAtLocation(getWindow().getDecorView(), Gravity.TOP | Gravity.LEFT, x - 8, y);
    }

    private int draSelectPosition = 0;
    private final ListenerImageLoad listener = new ListenerImageLoad() {
        @Override
        public void done(String key, boolean isSucc) {
            if (showLastImage) {
                // 显示最后一张
                if (isSucc && getImageFetcher().getImageCache() != null) {
                    Bitmap bm = getImageFetcher().getImageCache()
                            .getBitmapFromAllCache(key).getBitmap();
                    mImage.setMyImageBitmap(bm);
                } else {
                    // 最后一张图片显示取值失败
                }
                tabName.setText(mPackSatelliteListDown.nephanalysis_list.get(draSelectPosition).name);
            } else {
                if (isSucc) {
                    for (int i = 0; i < mySatelliteList.size(); i++) {
                        Satellite info = mySatelliteList.get(i);
                        String url = getString(R.string.file_download_url)
                                + info.url;
                        if (key.equals(url)) {
                            // Log.i("z", "result=" + i + "   " + url);
                            if (getImageFetcher().getImageCache() != null) {
                                comm_imgs[i] = getImageFetcher().getImageCache()
                                        .getBitmapFromAllCache(key).getBitmap();
                                // myPopSeekBar.setProgress(i + 1);
                                // tvPopProgress.setText((i + 1) + "/" +
                                // mySatelliteList.size());
                                downloadImageAll();
                            }
                            break;
                        }
                    }
                } else {
                    try {
                        if(comm_imgs!=null){
                            comm_imgs[count] = BitmapFactory.decodeResource(getResources(), R.drawable.alph100png);
                        }else{
                            comm_imgs = new Bitmap[imgCount];
                            comm_imgs[count] = BitmapFactory.decodeResource(getResources(), R.drawable.alph100png);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                count++;
            }
        }
    };

    /**
     * 更新图片数据，默认显示最后一条
     */
    private void refreshData() {
        showLastImage = true;//显示最后一张图片。更新雷达图片列表说明栏目已经切换，则显示默认的最后一张图片。
        if (mySatelliteList == null) {
            return;
        }
        if (mySatelliteList.size() > 0) {
            comm_imgs = new Bitmap[mySatelliteList.size()];
            // 显示最后一张图片
            Satellite info = mySatelliteList.get(mySatelliteList.size() - 1);
            String url = getString(R.string.file_download_url) + info.url;
            getImageFetcher().addListener(listener);
            getImageFetcher().loadImage(url, null, ImageConstant.ImageShowType.NONE);
            initSeekBar(mySatelliteList.size());
            tabDateText.setText(info.time);
        } else {
            tabDateText.setText("");
            mImage.setVisibility(View.GONE);
        }
    }

    /**
     * 下载数据pop
     */
    public void showPopup() {
        if (mPopWindow == null) {
            View popupView = getLayoutInflater().inflate(
                    R.layout.popview_image_download, null);
            popupView.setOnKeyListener(new OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        if (mPopWindow != null && mPopWindow.isShowing()) {
                            mPopWindow.dismiss();
                        }
                        return true;
                    }
                    return false;
                }
            });
            myPopSeekBar = (MySeekBar) popupView.findViewById(R.id.mySeekBar);
            tvPopProgress = (TextView) popupView
                    .findViewById(R.id.download_num_tv);
            mPopWindow = new PopupWindow(popupView, LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT, true);
        }
        myPopSeekBar.setMax(imgCount - 1);
        myPopSeekBar.setProgress(0);
        // 设置不可以获得焦点
        mPopWindow.setFocusable(false);
        // 设置弹窗内不可点击
        mPopWindow.setTouchable(false);
        // 设置弹窗外不可点击
        mPopWindow.setOutsideTouchable(false);
        mPopWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopWindow.showAtLocation(findViewById(R.id.satelliteView),
                Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
    }

    /**
     * 图片触摸事件，显示底部进度条
     */
    private final ImageTouchView.TouchViewLisetner imageTouchViewListener = new ImageTouchView.TouchViewLisetner() {

        @Override
        public void touchEvent(MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    showButton();
                    break;
                case MotionEvent.ACTION_UP:
                    postDelayHideBar();
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
            }

        }
    };

    /**
     * 拖动是改变图片显示状态和时间
     */
    private final OnSeekBarChangeListener seekBarChangeListener = new OnSeekBarChangeListener() {

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            postDelayHideBar();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            showButton();
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            if (null == comm_imgs) {
                return;
            }
            index = progress + 1;
            try {
                tvProgress.setText(progress + 1 + "/" + comm_imgs.length);
                mImage.changeImageBitmap(comm_imgs[progress]);
                if (progress < mySatelliteList.size()) {
                    tabDateText.setText(mySatelliteList.get(progress).time);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * 图片显示位置，
     *
     * @param progress
     */
    private void initSeekBar(int progress) {
        int size = mySatelliteList.size();
        if (size > 1) {
            mSeekBar.setMax(size - 1);
            mSeekBar.setProgress(progress - 1);
            tvProgress.setText(progress + "/" + size);
        } else {
            mSeekBar.setProgress(1);
            mSeekBar.setMax(1);
            tvProgress.setText("0/0");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.btn_right:
//                toPhonePeople();
//                break;
            case R.id.spinner_puzzle:
                loadListContent(v);
                break;
            case R.id.btn_start:
                mHandler.removeMessages(0);
                showLastImage = false;
                startEvent();
                break;
            default:
                break;
        }
    }

    /**
     * 点击开始按钮
     */
    private void startEvent() {
        switch (status) {
            case STATUS_START:
                // 如果为播放状态则暂停
                pause();
                break;
            default:
                if (check()) {
                    start();
                } else {
                    // 否则下载，不允许拖动
                    mSeekBar.setEnabled(false);
                    btnStart.setEnabled(false);
                    count = 0;
                    downloadImageAll();
                }
                break;
        }
    }

    /**
     * 暂停播放图片
     */
    private void pause() {
        status = STATUS_PAUSE;
        btnStart.setImageResource(R.drawable.btn_play);
        // 允许拖动
        mSeekBar.setEnabled(true);
    }

    /**
     * 显示下一张图片
     */
    private void showNextImageView() {
        try {
            if (status == STATUS_START) {
                if (index > comm_imgs.length) {
                    index = 1;
                }
                initSeekBar(index);
                // mImage.changeImageBitmap(comm_imgs[index-1]);
                // Satellite info = mySatelliteList.get(index-1);
                // tabDateText.setText(info.time);
                mHandler.removeMessages(0);
                mHandler.sendEmptyMessageDelayed(0, mAnimDelay);
                index++;
            } else {
                index--;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 开始播放动画
     */
    private void start() {
        mSeekBar.setEnabled(true);
        status = STATUS_START;
        mHandler.removeMessages(0);
        mHandler.sendEmptyMessage(0);
        btnStart.setImageResource(R.drawable.btn_pause);
        postDelayHideBar();
    }

    /**
     * 下载所有图片
     */
    private void downloadImageAll() {
        showPopup();
        myPopSeekBar.setProgress(count);
        tvPopProgress.setText(count + 1 + "/" + mySatelliteList.size());
        if (count < mySatelliteList.size()) {
            // 下载第count张图片
            Satellite info = mySatelliteList.get(count);
            String url = getString(R.string.file_download_url) + info.url;
            getImageFetcher().addListener(listener);
            getImageFetcher().loadImage(url, null, ImageConstant.ImageShowType.NONE);
        } else {
            // 当count等于列表大小的时候则下载完成，开始播放动画
            if (mPopWindow != null && mPopWindow.isShowing()) {
                mPopWindow.dismiss();
            }
            btnStart.setEnabled(true);
            index = 1;
            start();
        }
    }

    /**
     * 检测 comm_images bitmap 列表是否为空。或者里面图片是否为空。只要有一张图片为空则返回false
     *
     * @return
     */
    private boolean check() {
        if (null == comm_imgs) {
            return false;
        }
        for (int i = 0; i < comm_imgs.length; i++) {
            if (null == comm_imgs[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * 跳转到通讯录
     */
    private void toPhonePeople() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Contacts.People.CONTENT_URI);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(receiver);
        for (int i = 0; i < comm_imgs.length; i++) {
            comm_imgs[i] = null;
        }
        comm_imgs = null;
        pause();
    }

    /**
     * 延迟隐藏底部布局
     */
    private void postDelayHideBar() {
        mHandler.removeMessages(1);
        mHandler.sendEmptyMessageDelayed(1, 3000);
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        mBottomBar.setVisibility(View.GONE);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    @Override
    public void onAnimationStart(Animation animation) {

    }
}