package com.pcs.ztqtj.control.radar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.GroundOverlay;
import com.amap.api.maps.model.GroundOverlayOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.pcs.lib_ztqfj_v2.model.pack.net.radar.RadarImgInfo;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.ZtqLocationTool;
import com.pcs.ztqtj.control.tool.image.ImageMultipleLoadFromUrl;
import com.pcs.ztqtj.view.activity.product.ActivityWeatherRadar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tyaathome on 2018/10/08.
 */
public class RadarMapControl {

    private Context context;
    private AMap aMap;
    private List<RadarImgInfo> radarImgList = new ArrayList<>();
    private LatLng latLng1, latLng2;
    private List<GroundOverlayOptions> mRadarGroundoverlayList = new ArrayList<>();
    private static final int WHAT_PLAY = 0;
    private static final int WHAT_PAUSE = 1;
    private int currentPlayPosition = -1;
    private GroundOverlay mRadarGroundoverlay;
    private ActivityWeatherRadar activity;
    private boolean isPlaying = false;
    private Marker marker;

    public RadarMapControl(ActivityWeatherRadar activity, AMap aMap) {
        this.activity = activity;
        context = activity;
        this.aMap = aMap;
    }

    public void init() {
        setLocation();
    }

    /**
     * 设置定位点，显示标识
     */
    private void setLocation() {
        LatLng location = ZtqLocationTool.getInstance().getLatLng();
        if(location == null) {
            return;
        }
        // 地图位置
        if(marker != null && !marker.isRemoved()) {
            marker.remove();
        }
        // 标记
        marker = aMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_location))
                .position(location));
    }

    public void select(LatLng latLng1, LatLng latLng2, List<RadarImgInfo> radarImgList) {
        stopDraw();
        this.latLng1 = latLng1;
        this.latLng2 = latLng2;
        this.radarImgList = radarImgList;
        mRadarGroundoverlayList.clear();
        if(!isPlaying) {
            if (mRadarGroundoverlayList.size() == 0) {
                activity.btnStart.setEnabled(false);
                List<String> urlList = new ArrayList<>();
                for (RadarImgInfo data : radarImgList) {
                    if(!TextUtils.isEmpty(data.img)) {
                        urlList.add(context.getResources().getString(R.string.file_download_url) + data.img);
                    }
                }
                ImageMultipleLoadFromUrl.getInstance()
                        .setParams(urlList, onMultipleCompleteListener)
                        .setObject(latLng1, latLng2)
                        .startMultiple();
            }
        }
    }

    public void play() {
        if(isPlaying) {
            Message message = Message.obtain();
            message.what = WHAT_PAUSE;
            message.arg1 = currentPlayPosition;
            mHandler.sendMessage(message);
        } else {
            startDraw(mRadarGroundoverlayList.size());
        }
    }

    private void addPolyToMap(int position) {
        if(mRadarGroundoverlayList.size() > position) {
            if(mRadarGroundoverlay != null) {
                mRadarGroundoverlay.remove();
            }
            GroundOverlayOptions options = mRadarGroundoverlayList.get(position);
            mRadarGroundoverlay = aMap.addGroundOverlay(options);
        }
    }

    public void stopDraw() {
        isPlaying = false;
        currentPlayPosition = -1;
        activity.selectSeekBar(7, 8);
        activity.btnStart.setImageResource(R.drawable.btn_play);
        mHandler.removeMessages(WHAT_PLAY);
        if(mRadarGroundoverlay != null) {
            mRadarGroundoverlay.remove();
        }
    }

    private void startDraw(int size) {
        Message message = Message.obtain();
        message.what = WHAT_PLAY;
        message.arg1 = currentPlayPosition;
        message.arg2 = size;
        mHandler.sendMessage(message);
    }

    public void recycle() {
        stopDraw();
        if(marker != null) {
            marker.remove();
        }
        if(mRadarGroundoverlay != null) {
            mRadarGroundoverlay.remove();
        }
        if(mRadarGroundoverlayList != null) {
            mRadarGroundoverlayList.size();
        }
        currentPlayPosition = 0;
    }

    private ImageMultipleLoadFromUrl.OnMultipleCompleteListener onMultipleCompleteListener = new ImageMultipleLoadFromUrl.OnMultipleCompleteListener() {

        @Override
        public void onComplete(List<Bitmap> bitmapList, Object... object) {
            if(bitmapList != null) {
                int size = bitmapList.size();
                activity.btnStart.setEnabled(true);
                activity.dismessPopup();
                if (object.length >= 2) {
                    mRadarGroundoverlayList.clear();
                    LatLng latLng1 = (LatLng) object[0];
                    LatLng latLng2 = (LatLng) object[1];
                    for (Bitmap bitmap : bitmapList) {
                        BitmapDescriptor descriptor = BitmapDescriptorFactory.fromBitmap(bitmap);
                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        builder.include(latLng1);
                        builder.include(latLng2);
                        mRadarGroundoverlayList.add(new GroundOverlayOptions()
                                .image(descriptor)
                                .positionFromBounds(builder.build())
                                .zIndex(2));
                    }
                    //startDraw(mRadarGroundoverlayList.size());
                    addPolyToMap(size - 1);
                    activity.selectSeekBar(size-1, size);
                }
            }
        }

        @Override
        public void onProgress(int progress, int max) {
            activity.showPopup();
            activity.myPopSeekBar.setMax(radarImgList.size()-1);
            activity.myPopSeekBar.setProgress(progress);
            activity.tvPopProgress.setText(progress + 1 + "/" + max);
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what) {
                case WHAT_PLAY:
                    isPlaying = true;
                    activity.btnStart.setImageResource(R.drawable.btn_pause);
                    int size = msg.arg2;
                    if(size > currentPlayPosition+1) {
                        currentPlayPosition++;
                    } else {
                        currentPlayPosition = 0;
                    }
                    activity.selectSeekBar(currentPlayPosition, size);
                    addPolyToMap(currentPlayPosition);
                    Message message = Message.obtain();
                    message.what = WHAT_PLAY;
                    message.arg1 = currentPlayPosition;
                    message.arg2 = size;
                    mHandler.sendMessageDelayed(message, 1000);
                    break;
                case WHAT_PAUSE:
                    isPlaying = false;
                    currentPlayPosition = msg.arg1;
                    mHandler.removeMessages(WHAT_PLAY);
                    activity.btnStart.setImageResource(R.drawable.btn_play);
                    break;
            }
        }
    };

}
