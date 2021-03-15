package com.pcs.ztqtj.view.activity.product;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.Images;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.model.LatLng;
import com.pcs.lib.lib_pcs_v3.control.file.PcsGetPathValue;
import com.pcs.lib.lib_pcs_v3.control.tool.BitmapUtil;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib.lib_pcs_v3.model.image.ImageConstant;
import com.pcs.lib.lib_pcs_v3.model.image.ListenerImageLoad;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityMain;
import com.pcs.lib_ztqfj_v2.model.pack.net.radar.PackRadarDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.radar.PackRadarListDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.radar.PackRadarListDown.StationInfo;
import com.pcs.lib_ztqfj_v2.model.pack.net.radar.PackRadarListUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.radar.PackRadarNewDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.radar.PackRadarNewUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.radar.PackRadarUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.radar.RadarImgInfo;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.MyListBaseAdapter;
import com.pcs.ztqtj.control.adapter.radar.AdapterRadar;
import com.pcs.ztqtj.control.livequery.ControlDistributionBase;
import com.pcs.ztqtj.control.livequery.ControlMapBound;
import com.pcs.ztqtj.control.radar.RadarMapControl;
import com.pcs.ztqtj.control.tool.CommUtils;
import com.pcs.ztqtj.control.tool.ShareTools;
import com.pcs.ztqtj.control.tool.ZtqImageTool;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.view.AnimatedGifEncoder;
import com.pcs.ztqtj.view.activity.FragmentActivityWithShare;
import com.pcs.ztqtj.view.myview.ImageTouchView;
import com.pcs.ztqtj.view.myview.MySeekBar;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 气象雷达
 *
 * @author chenjh
 */
public class ActivityWeatherRadar extends FragmentActivityWithShare implements
        View.OnClickListener, AnimationListener {
    private MyReceiver receiver = new MyReceiver();
    private PackRadarListUp packRadarListUp = new PackRadarListUp();
    private PackRadarUp packRadarUp = new PackRadarUp();
    private List<StationInfo> stationList = new ArrayList<StationInfo>(); // 气象雷达列表
    /***
     * 拼图列表
     */
    public List<StationInfo> puzzleList = new ArrayList<StationInfo>();
    private List<RadarImgInfo> radarImgList = new ArrayList<RadarImgInfo>();// 雷达图片列表

    private MyListBaseAdapter myBaseAdapter;
    private Bitmap[] comm_imgs;
    private int imgCount = 8;
    private int index;
    private LinearLayout tabLayout;
    public MySeekBar myPopSeekBar;
    public TextView tvPopProgress;
    public SeekBar mSeekBar;
    private TextView tvProgress;
    private int status;
    private View mBottomBar;
    private static final int STATUS_PAUSE = 1;
    private static final int STATUS_START = 2;
    private final int mAnimDelay = 1500;

    private PopupWindow window;
    private ImageTouchView mImage;
    public ImageView btnStart;
    private PopupWindow mPopWindow;
    private int currTab = 0;
    private TextureMapView mapView;
    protected AMap aMap;
    private static final LatLng INIT_LATLNG = new LatLng(39.0851000000,117.1993700000);
    // 地图缩放层级
    private static final float MAP_ZOOM = 8.6f;
    private RecyclerView hor_scr;

    private PackRadarNewUp packRadarNewUp = new PackRadarNewUp();
    private AdapterRadar adapterRadar;
    private List<String> list_level = new ArrayList<>();
    private List<String> levelList = new ArrayList<>();
    private SeekBar seekBar_maps;

    private List<ControlDistributionBase> controlList = new ArrayList<>();
    private RelativeLayout radarView;
    private View layoutScale;
    private ImageView iv_radar_example;
    /**
     * 记录下载第几张图片
     */
    private int count = 0;

    private boolean showLastImage = true;

    private RadarMapControl mapControl;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    showNextImageView();
                    break;
                case 1:
//                    hideButton();
                    break;
            }
        }
    };

    private TextView img_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 禁止休眠
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams
                .FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setTitleText("雷达回波");
        setContentView(R.layout.weather_radar_main);
        createImageFetcher();
        //图像加载监听
        getImageFetcher().addListener(loadImageListener);
        initMap(savedInstanceState);
        initView();
        initEvent();
        initData();
    }

    private void initMap(Bundle savedInstanceState) {
        mapView = (TextureMapView) findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.getUiSettings().setRotateGesturesEnabled(false);
        aMap.getUiSettings().setTiltGesturesEnabled(false);
        //aMap.setMapTextZIndex(5);
    }


    @Override
    protected void onResume() {
        super.onResume();
        getImageFetcher().addListener(loadImageListener);
        mapView.onResume();
    }

    private void initView() {
        iv_radar_example= (ImageView) findViewById(R.id.iv_radar_example);
        radarView = (RelativeLayout) findViewById(R.id.radarView);
        tabLayout = (LinearLayout) findViewById(R.id.tab_layout);
        mBottomBar = findViewById(R.id.bottom_bar);
        mImage = (ImageTouchView) findViewById(R.id.img);
        mImage.setHightFillScale(true);
        tvProgress = (TextView) findViewById(R.id.txt_progress);
        img_time = (TextView) findViewById(R.id.img_time);
        mSeekBar = (SeekBar) findViewById(R.id.seekBar);
        mSeekBar.setMax(imgCount - 1);
        mSeekBar.setEnabled(false);
        btnStart = (ImageView) findViewById(R.id.btn_start);
        tab_name = (TextView) findViewById(R.id.tab_name_puzzle);
        hor_scr = (RecyclerView) findViewById(R.id.hor_scr);
        hor_scr.setNestedScrollingEnabled(false);

        seekBar_maps = (SeekBar) findViewById(R.id.seekBar_maps);
//        seekBar_maps.setEnabled(false);
        adapterRadar = new AdapterRadar(list_level);
        hor_scr.setAdapter(adapterRadar);
        layoutScale = findViewById(R.id.layout_scale);
    }

    private void initEvent() {
        findViewById(R.id.spinner_station).setOnClickListener(this);
        findViewById(R.id.spinner_puzzle).setOnClickListener(this);
//        setBtnRight(R.drawable.icon_share_new, this);
        setBtnRightListener(this);
        setBtnRight2(R.drawable.icon_save_gif, this);
        btnStart.setOnClickListener(this);
        mImage.setTouchListener(imageTouchViewListener);
        mSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
        seekBar_maps.setOnSeekBarChangeListener(seekBarMapChangeListener);
    }


    private void initData() {
        addLocationMarkerToMap();
        showProgressDialog();
        // 注册广播接收
        PcsDataBrocastReceiver.registerReceiver(this, receiver);
        comm_imgs = new Bitmap[imgCount];
        getStationList();
        if (!isOpenNet()) {
            showToast(getString(R.string.open_netword));
        } else {
            if (!isWiFiNewWord()) {
                showToast(getString(R.string.un_wifi_desc));
            }
        }

        ControlMapBound controlMapBound = new ControlMapBound(ActivityWeatherRadar.this, aMap, Color.BLACK);
        controlMapBound.setLineWidth(4);
        controlMapBound.start();
        initControls();
        mapControl = new RadarMapControl(this, aMap);
        mapControl.init();
    }

    /**
     * 初始化适配器
     */
    private void initControls() {
        createControls();
        for (ControlDistributionBase bean : controlList) {
            bean.init();
        }
    }

    /**
     * @return
     */
    public AMap getMap() {
        return aMap;
    }


    private void createControls() {
    }


    /**
     * 执行控制器中清除
     */
    private void clearControls() {
        for (ControlDistributionBase bean : controlList) {
            bean.clear();
        }
    }

    /**
     * 回收控制器
     */
    private void destroyControls() {
        for (ControlDistributionBase bean : controlList) {
            bean.destroy();
        }
        if(mapControl != null) {
            mapControl.recycle();
        }
    }


    /**
     * 获取雷达站点列表
     **/
    private void getStationList() {

//        if (!isOpenNet()) {
//            showToast(getString(R.string.net_err));
//            return;
//        }
        packRadarListUp.type = "1";
        PackRadarListDown down =
                (PackRadarListDown) PcsDataManager.getInstance().getLocalPack(packRadarListUp.getName());
        dealListDown(down, false);
        PcsDataDownload.addDownload(packRadarListUp);
    }

    /**
     * 添加定位点
     */
    private void addLocationMarkerToMap() {
        aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(INIT_LATLNG, MAP_ZOOM));
    }


    private void dealListDown(PackRadarListDown packRadarListDown, boolean isnet) {
        if (packRadarListDown == null) {
            if (isnet) {
                PackRadarListDown down =
                        (PackRadarListDown) PcsDataManager.getInstance().getLocalPack(packRadarListUp.getName());
                if (down==null){
                    return;
                }
                stationList.clear();
                puzzleList.clear();
                stationList.addAll(down.stationList);
                puzzleList.addAll(down.stationList);
                updateStationList();
//        showProgressDialog();
                itemName = puzzleList.get(0).station_name;
                tab_name.setText(itemName);
                switchTab(0, puzzleList.get(0).station_id);
            }

        } else {
            stationList.clear();
            puzzleList.clear();
            stationList.addAll(packRadarListDown.stationList);
            puzzleList.addAll(packRadarListDown.stationList);
            updateStationList();
//        showProgressDialog();
            itemName = puzzleList.get(0).station_name;
            tab_name.setText(itemName);
            switchTab(0, puzzleList.get(0).station_id);
        }


    }

    private void dealRadarDown(PackRadarDown packRadarDown, boolean isnet) {
        if (packRadarDown == null) {
            if (isnet) {
                PackRadarDown down =
                        (PackRadarDown) PcsDataManager.getInstance().getLocalPack(packRadarUp.getName());
                if (down==null){
                    return;
                }
                mImage.setVisibility(View.VISIBLE);
                radarImgList = down.radarImgList;
                updateRadarData();
            }
        } else {
            mImage.setVisibility(View.VISIBLE);
            radarImgList = packRadarDown.radarImgList;
            updateRadarData();
        }

    }
    /**
     * 数据更新广播接收
     */
    private class MyReceiver extends PcsDataBrocastReceiver {
        @Override
        public void onReceive(String name, String error) {
            if (packRadarListUp != null && packRadarListUp.getName().equals(name)) {
                dismissProgressDialog();

                PackRadarListDown packRadarListDown =
                        (PackRadarListDown) PcsDataManager.getInstance().getNetPack(name);
                dealListDown(packRadarListDown, true);

//                PackRadarListDown packRadarListDown = (PackRadarListDown) PcsDataManager.getInstance().getNetPack(name);
//                if (packRadarListDown == null) {
//                    return;
//                }
//                stationList.clear();
//                puzzleList.clear();
//                stationList.addAll(packRadarListDown.stationList);
//                puzzleList.addAll(packRadarListDown.stationList);
//                updateStationList();
//
//                showProgressDialog();
//                itemName = puzzleList.get(0).station_name;
//                tab_name.setText(itemName);
//                switchTab(0, puzzleList.get(0).station_id);

            } else if (packRadarUp != null && packRadarUp.getName().equals(name)) {
                dismissProgressDialog();
                PackRadarDown packRadarDown =
                        (PackRadarDown) PcsDataManager.getInstance().getNetPack(name);
                dealRadarDown(packRadarDown, true);
//                PackRadarDown packRadarDown = (PackRadarDown) PcsDataManager.getInstance().getNetPack(name);
//                radarImgList.clear();
//                if (packRadarDown == null||packRadarDown.radarImgList.size()==0) {
////                    updateRadarData();
//                    mImage.setVisibility(View.GONE);
//                    return;
//                }
//                mImage.setVisibility(View.VISIBLE);
//                radarImgList.addAll(packRadarDown.radarImgList);
//                updateRadarData();
            } else if (packRadarNewUp.getName().equals(name)) {
                dismissProgressDialog();
                PackRadarNewDown down = (PackRadarNewDown) PcsDataManager.getInstance().getNetPack(name);
                if (down == null) {
                    return;
                }

                list_level.clear();
                list_level.add("");
                list_level.addAll(down.leve);
                list_level.add("");
                hor_scr.setLayoutManager(new GridLayoutManager(ActivityWeatherRadar.this, list_level.size()));
                adapterRadar.notifyDataSetChanged();
                seekBar_maps.setMax(list_level.size() - 1);
                levelList.clear();
                levelList.add("0");
                levelList.addAll(down.leve);
                levelList.add(down.leve.get(down.leve.size()-1));
                if(!TextUtils.isEmpty(packRadarNewUp.leve) &&!TextUtils.isEmpty(down.lat1) && !TextUtils.isEmpty(down.lat2) &&
                        !TextUtils.isEmpty(down.lon1) && !TextUtils.isEmpty(down.lon1)) {
                    LatLng latLng1 = new LatLng(Double.parseDouble(down.lat1), Double.parseDouble(down.lon1));
                    LatLng latLng2 = new LatLng(Double.parseDouble(down.lat2), Double.parseDouble(down.lon2));
                    radarImgList.clear();
                    radarImgList.addAll(down.radarImgList);
                    Collections.reverse(radarImgList);
                    mapControl.select(latLng1, latLng2, radarImgList);
                    if(radarImgList != null && radarImgList.size() > 0) {
                        RadarImgInfo info = radarImgList.get(radarImgList.size()-1);
                        reflushTime(info);
                    } else {
                        img_time.setText("");
                    }
                }
                if(TextUtils.isEmpty(packRadarNewUp.leve) && levelList.size() >= 2) {
                    reqMap(levelList.get(1));
                    seekBar_maps.setProgress(1);
                }
            }
        }
    }

    /**
     * 默认雷达雷达站点
     */
    private void updateStationList() {
        //下载数据
        PackLocalCityMain cityMain = ZtqCityDB.getInstance().getCityMain();
        if (!cityMain.isFjCity) {
            setPuzzleDef();
            return;
        }
//        PackLocalCity city2 = ZtqCityDB.getInstance().getCityInfo2_ID(cityMain.PARENT_ID);
//        PackLocalCity city1 = ZtqCityDB.getInstance().getCityInfo1_ID(city2.PARENT_ID);
//        if (cityMain != null && (cityMain.NAME.equals("省会福州"))) {
////            省会福州则用全省拼图；
//            setPuzzleDef();
//        } else if (city1.NAME.equals("莆田") || city1.NAME.equals("平潭")) {
//            setPuzzleDef();
//        } else {
        for (int i = 0; i < stationList.size(); i++) {
            StationInfo info = stationList.get(i);
            String station = info.station_name;
            if (station.startsWith(cityMain.NAME)) {
                TextView tab_def = (TextView) findViewById(R.id.tab_name_station);
                tab_def.setText(info.station_name);
                switchTab(0, info.station_id);
                break;
            }
        }
//        }

    }

    private void setPuzzleDef() {
        if (puzzleList.size() > 0) {
            StationInfo info = puzzleList.get(0);
            TextView tab_def = (TextView) findViewById(R.id.tab_name_puzzle);
            tab_def.setText(info.station_name);
            switchTab(0, info.station_id);
        }
    }

    /**
     * 切换雷达站点
     **/
    private void switchTab(int position, String station_id) {
        showLastImage = true;
        showButton();
        mSeekBar.setEnabled(false);
        status = STATUS_PAUSE;
        btnStart.setImageResource(R.drawable.btn_play);
        btnStart.setEnabled(true);
        index = 1;
        count = 0;
        currTab = position;

//        if (!isOpenNet()) {
//            showToast(getString(R.string.net_err));
//            return;
//        }
//        showProgressDialog();
        mSeekBar.setProgress(imgCount - 1);
        packRadarUp.station_id = station_id;
        packRadarUp.count = "8";
        PackRadarDown down =
                (PackRadarDown) PcsDataManager.getInstance().getLocalPack(packRadarUp.getName());
        dealRadarDown(down, false);
        PcsDataDownload.addDownload(packRadarUp);
    }

    /**
     * 更新雷达站点图片数据
     */
    private void updateRadarData() {
        //显示最后一张图片。更新雷达图片列表说明栏目已经切换，则显示默认的最后一张图片。
        int dataSize = radarImgList.size();
        imgCount = dataSize;
        if (dataSize > 0) {
            comm_imgs = new Bitmap[dataSize];
            showLastImageView();
        } else {
            img_time.setText("");
            mImage.setVisibility(View.GONE);
        }

    }

    private void showLastImageView() {
        if (radarImgList == null) {
            return;
        }
        if (radarImgList.size() > 0) {
            comm_imgs = new Bitmap[radarImgList.size()];
            // 显示最后一张图片
            RadarImgInfo info = radarImgList.get(radarImgList.size() - 1);
            String url = getString(R.string.file_download_url) + info.img;
            getImageFetcher().loadImage(url, null, ImageConstant.ImageShowType.NONE);
            initSeekBar(radarImgList.size());
            if (TextUtils.isEmpty(info.actiontime)) {
                img_time.setText("");
            } else {
                reflushTime(info);
            }
        } else {
            img_time.setText("");
            mImage.setVisibility(View.GONE);
        }
    }

    private ListenerImageLoad loadImageListener = new ListenerImageLoad() {
        @Override
        public void done(String key, boolean isSucc) {
            if (showLastImage) {
                if (key.equals(key)) {
                    dismissProgressDialog();
                    if (isSucc && getImageFetcher().getImageCache() != null) {
                        Bitmap bm = getImageFetcher().getImageCache().getBitmapFromAllCache(key).getBitmap();
                        mImage.setMyImageBitmap(bm);
                    } else {
                        // 最后一张图片显示取值失败
                    }
                }

                changeItemTitle();

            } else {
                if (isSucc && getImageFetcher().getImageCache() != null) {
                    for (int i = 0; i < radarImgList.size(); i++) {
                        RadarImgInfo info = radarImgList.get(i);
                        String url = getString(R.string.file_download_url) + info.img;
                        if (key.equals(url)) {
                            comm_imgs[i] = getImageFetcher().getImageCache()
                                    .getBitmapFromAllCache(key).getBitmap();
                            downloadImageAll();
                            break;
                        }
                    }
                } else {
                    comm_imgs[count] = BitmapFactory.decodeResource(
                            getResources(), R.drawable.alph100png);
                }
                if (count < imgCount) {
                    count++;
                }
            }
        }
    };

    /**
     * 下载所有图片
     */
    private void downloadImageAll() {
        showPopup();
        myPopSeekBar.setProgress(count);
        tvPopProgress.setText(count + 1 + "/" + radarImgList.size());
        if (count < radarImgList.size()) {
            // 下载第count张图片
            RadarImgInfo info = radarImgList.get(count);
            String url = getString(R.string.file_download_url) + info.img;
            getImageFetcher().addListener(loadImageListener);
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
     * 暂停播放图片
     */
    private void pause() {
        status = STATUS_PAUSE;
        btnStart.setImageResource(R.drawable.btn_play);
        // 允许拖动
        mSeekBar.setEnabled(true);
    }

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

    private void postDelayHideBar() {
        mHandler.removeMessages(1);
        mHandler.sendEmptyMessageDelayed(1, 3000);
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        //mBottomBar.setVisibility(View.GONE);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

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
        mPopWindow.showAtLocation(findViewById(R.id.radarView),
                Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
    }

    public void dismessPopup() {
        if(mPopWindow != null && mPopWindow.isShowing()) {
            mPopWindow.dismiss();
        }
    }
    /**
     * 列表
     *
     * @param v
     */
    private void loadListContent(View v, List<StationInfo> dataList) {
        int screenWidth = CommUtils.getScreenWidth(getApplicationContext());
        View view = View.inflate(this, R.layout.typhoon_list_dlg, null);
        final ListView listView = (ListView) view.findViewById(R.id.typhoon_list);
        myBaseAdapter = new MyListBaseAdapter(dataList);
        listView.setAdapter(myBaseAdapter);
        listView.setCacheColorHint(Color.parseColor("#00000000"));
        window = new PopupWindow(view, screenWidth / 2, LayoutParams.WRAP_CONTENT, true);
//        window.setAnimationStyle(R.style.Popup_right);
        window.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_drag_content));
        listView.setOnItemClickListener(new MyOnItemClickListener());
//        int[] coords = new int[2];
//        v.getLocationOnScreen(coords);
//        int y = v.getHeight() + coords[1];
//        int x = coords[0];
//        window.showAtLocation(getWindow().getDecorView(), Gravity.TOP| Gravity.LEFT, x - 8, y);
        window.showAsDropDown(v);
    }


    private class MyOnItemClickListener implements OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
            showProgressDialog();
            StationInfo info = (StationInfo) myBaseAdapter.getItem(position);
            itemName = info.station_name;
            stationId = info.station_id;
            if (stationId.equals("20001")) {
                iv_radar_example.setVisibility(View.VISIBLE);
                tab_name.setText(itemName);
                dismissProgressDialog();
                //mBottomBar.setVisibility(View.GONE);
                mapView.setVisibility(View.VISIBLE);
                reqMap("");
                layoutScale.setVisibility(View.VISIBLE);
                btnStart.setEnabled(true);
            } else {
                iv_radar_example.setVisibility(View.GONE);
                mBottomBar.setVisibility(View.VISIBLE);
                mapView.setVisibility(View.GONE);
                switchTab(position, info.station_id);
                layoutScale.setVisibility(View.GONE);
            }

            window.dismiss();
        }
    }

    private void reqMap(String level) {
        showProgressDialog();
        packRadarNewUp.leve = level;
        PcsDataDownload.addDownload(packRadarNewUp);
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

    private OnSeekBarChangeListener seekBarChangeListener = new OnSeekBarChangeListener() {

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
                RadarImgInfo info = radarImgList.get(progress);
                reflushTime(info);
//                if (TextUtils.isEmpty(radarImgList.get(progress).actiontime)) {
//                    tabDateText.setText("");
//                } else {
//                    RadarImgInfo info = radarImgList.get(progress);
//                    String tabDate = info.actiontime.substring(
//                            info.actiontime.lastIndexOf("年") + 1,
//                            info.actiontime.length());
//                    tabDateText.setText(tabDate);
//                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private OnSeekBarChangeListener seekBarMapChangeListener = new OnSeekBarChangeListener() {

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if(mapControl != null) {
                mapControl.stopDraw();
            }
            int progress = seekBar.getProgress();
            if(progress < levelList.size()) {
                reqMap(levelList.get(progress));
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {

        }
    };

    private void reflushTime(RadarImgInfo info) {
        try {
            String tabDate = info.actiontime.substring(
                    info.actiontime.lastIndexOf("年") + 1,
                    info.actiontime.length());
            img_time.setText(tabDate);
        } catch (Exception e) {
        }
    }

    /**
     * 显示下一张图片
     */
    private void showNextImageView() {
        try {
            if (comm_imgs != null && status == STATUS_START) {
                if (index > comm_imgs.length) {
                    index = 1;
                }
                initSeekBar(index - 1);
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

    private void initSeekBar(int progress) {
        if (comm_imgs.length > 1) {
            mSeekBar.setMax(comm_imgs.length - 1);
            mSeekBar.setProgress(progress);
        } else {
            mSeekBar.setProgress(1);
            mSeekBar.setMax(1);
        }
    }

    public void selectSeekBar(int progress, int max) {
        mSeekBar.setMax(max-1);
        mSeekBar.setProgress(progress);
        tvProgress.setText(progress + 1 + "/" + max);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_right:
                View layout = findViewById(R.id.all_view);
                Bitmap shareBitmap = BitmapUtil.takeScreenShot(this);
//                Bitmap shareBitmap = ZtqImageTool.getInstance().getScreenBitmap(layout);
                shareBitmap = ZtqImageTool.getInstance().stitchQR(ActivityWeatherRadar.this, shareBitmap);
                ShareTools.getInstance(ActivityWeatherRadar.this).setShareContent(getTitleText(), mShare,
                        shareBitmap, "0").showWindow(layout);
                break;
            case R.id.btn_right2:
                clickSaveGif();
                break;
            case R.id.spinner_puzzle:
                loadListContent(v, puzzleList);
                break;
            case R.id.spinner_station:
//                tab_name = (TextView) findViewById(R.id.tab_name_station);
//                loadListContent(v, stationList);
                break;
            case R.id.btn_start:
                if(stationId.equals("20001")) {
                    mapControl.play();
                } else {
                    mHandler.removeMessages(0);
                    showLastImage = false;
                    startEvent();
                }
                break;
            default:
                break;
        }
    }

    private void clickSaveGif() {
        showProgressDialog("保存中");
        Observable.fromIterable(radarImgList).observeOn(Schedulers.io()).flatMap(new Function<RadarImgInfo,
                ObservableSource<Bitmap>>() {
            @Override
            public ObservableSource<Bitmap> apply(RadarImgInfo radarImgInfo) throws Exception {
                return Observable.just(radarImgInfo).map(new Function<RadarImgInfo, Bitmap>() {
                    @Override
                    public Bitmap apply(RadarImgInfo info) throws Exception {
                        String path = getString(R.string.file_download_url) + info.img;
                        return getBitmapFromURL(path);
                    }
                });
            }
        }).toList().subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).map(new Function<List<Bitmap>, Boolean>() {
            @Override
            public Boolean apply(List<Bitmap> bitmapList) throws Exception {
                FileOutputStream outStream;
                try{
                    String path = PcsGetPathValue.getInstance().getImagePath();
                    File file = new File(PcsGetPathValue.getInstance().getImagePath());
                    if(!file.exists()) {
                        file.mkdirs();
                    }
                    String title = String.valueOf(System.currentTimeMillis());
                    String gifPath = path + title + ".gif";
                    outStream = new FileOutputStream(gifPath);
                    outStream.write(generateGIF(bitmapList));
                    outStream.close();
                    addImageToGallery(gifPath, title, title);

                }catch(Exception e){
                    e.printStackTrace();
                }
                return true;
            }
        }).onErrorReturn(new Function<Throwable, Boolean>() {
            @Override
            public Boolean apply(Throwable throwable) throws Exception {
                return false;
            }
        }).observeOn(AndroidSchedulers.mainThread()).doOnSuccess(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean b) throws Exception {
                if(b) {
                    showToast("保存成功");
                } else {
                    showToast("保存失败");
                }
                dismissProgressDialog();
            }
        }).subscribe();
    }

    private byte[] generateGIF(List<Bitmap> bitmapList) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        AnimatedGifEncoder encoder = new AnimatedGifEncoder();
        encoder.setDelay(mAnimDelay);
        encoder.start(bos);
        for (Bitmap bitmap : bitmapList) {
            encoder.addFrame(bitmap);
        }
        encoder.finish();
        return bos.toByteArray();
    }

    public Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
            //return resizeBitmap(myBitmap, 100);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void addImageToGallery(final String filePath, final String title, final String description) {
        try {
            String str = Images.Media.insertImage(getContentResolver(), filePath, title, description);
            // 通知图库更新
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                MediaScannerConnection.scanFile(this, new String[]{filePath}, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            public void onScanCompleted(String path, Uri uri) {
                                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                                mediaScanIntent.setData(uri);
                                sendBroadcast(mediaScanIntent);
                            }
                        });
            } else {
                File file = new File(filePath);
                File file1 = file.getParentFile();
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.fromFile(file1.getAbsoluteFile())));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void changeItemTitle() {
        if (tab_name != null) {
            if (tab_name.getId() == R.id.tab_name_puzzle) {
                tab_name.setText(itemName);
//                TextView anther = (TextView) findViewById(R.id.tab_name_station);
//                anther.setText(getString(R.string.please_station));
            } else if (tab_name.getId() == R.id.tab_name_station) {
                tab_name.setText(itemName);
                TextView anther = (TextView) findViewById(R.id.tab_name_puzzle);
                anther.setText(getString(R.string.please_puzzle));
            }
        }
    }


    private TextView tab_name;
    private String itemName = "";
    private String stationId = "";


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
     * 方法必须重写
     */
    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //图像加载监听
        getImageFetcher().removeListener(loadImageListener);
        this.unregisterReceiver(receiver);
        for (int i = 0; i < comm_imgs.length; i++) {
            comm_imgs[i] = null;
        }
        comm_imgs = null;
        mapView.onDestroy();

        destroyControls();
    }


}