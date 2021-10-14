package com.pcs.ztqtj.view.fragment.warning.disaster_reporting;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.services.geocoder.RegeocodeAddress;
import com.pcs.lib.lib_pcs_v3.control.file.PcsGetPathValue;
import com.pcs.lib.lib_pcs_v3.control.tool.Util;
import com.pcs.lib.lib_pcs_v3.model.image.ImageResizer;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCity;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityMain;
import com.pcs.lib_ztqfj_v2.model.pack.net.column.PackColumnDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.sh_warn.PackYjZqReleaseDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.sh_warn.PackYjfileDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.sh_warn.PackYjzqtjDown;
import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.livequery.AdapterData;
import com.pcs.ztqtj.control.tool.CommUtils;
import com.pcs.ztqtj.control.tool.MyConfigure;
import com.pcs.ztqtj.control.tool.PermissionsTools;
import com.pcs.ztqtj.control.tool.ZtqLocationTool;
import com.pcs.ztqtj.control.tool.utils.TextUtil;
import com.pcs.ztqtj.control.tool.videocompressor.MediaController;
import com.pcs.ztqtj.control.tool.videocompressor.VideoFileUtils;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.util.OkHttpUtil;
import com.pcs.ztqtj.view.activity.photoshow.ActivityLogin;
import com.pcs.ztqtj.view.fragment.warning.FragmentDisasterReporting;
import com.pcs.ztqtj.view.fragment.warning.picture.ActivityPhotoFull;
import com.pcs.ztqtj.view.fragment.warning.video.ActivityVideoPlay;
import com.pcs.ztqtj.view.fragment.warning.video.Code;
import com.pcs.ztqtj.view.fragment.warning.video.VideoListActivity;
import com.pcs.ztqtj.view.myview.MultiEditInputView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 首页-预警中心-灾害直报-灾情上传
 */
public class FragmentDisasterUp extends FragmentReportBase implements View.OnClickListener {

    private LinearLayout  lay_disaster_pic, lay_disaster_voice, lay_dsiaster_video;
    private List<String> mlist = new ArrayList<>();
    private List<String> mlist_id = new ArrayList<>();
    private List<String>  list_street, list_street_id;
    private TextView tv_disaster_type, tv_disater_pic, tv_disater_voice, tv_disater_video, tv_voice_time, bt_del,
            bt_paly, tv_voice_again, tv_voice_up;
    private TextView  disaster_up_street, tv_disaster_tg, tv_disaster_bh,
            tv_disaster_sh,
            tv_disaster_up_time, tv_disaster_address, tv_disaster;
    private PopupWindow mPopupWindow, pop_video, pop_voice;
    private ProgressDialog mProgress;
    private ImageView iv_disaster_pic, iv_disaster_pic_show, iv_disaster_video_show, iv_disaster_voice_show,
            iv_disaster_video, iv_voice, iv_disaster_voice, iv_paly_video_up;
    private Button bt_manage_info, bt_manage_release;
    private String user_id;
    private LinearLayout lay_disaster_manager_up, lay_disaster_bottom, lay_disaster_tg, lay_disaster_sh,
            lay_disaster_bh;
    private String zq_id, zq_time, zq_addr, zq_desc, pic_id, voi_id, vid_id, tub_id,
            zq_town_id;
    private MultiEditInputView id_meiv;
    private String file_type;
    private int num_shs = 0;
    private boolean is_voiceFirst = true, is_voiceEnd = true, is_phonoUp = false, is_voiceUp = false;
    private static FragmentDisasterUp instance;
    private String voice_time = "0";
    public static FragmentDisasterUp getInstance() {
        if (instance == null) {
            instance = new FragmentDisasterUp();
        }
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(getActivity()).inflate(R.layout.fragment_disaster_up, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initPopupWindow();
    }

    private List<PackLocalCity> list;

    /**
     * 初始化控件
     */
    private void initView() {
        user_id = MyApplication.UID;
        tv_disaster_type = (TextView) getView().findViewById(R.id.tv_disaster_type);
        tv_disaster_type.setOnClickListener(this);
        lay_disaster_pic = (LinearLayout) getView().findViewById(R.id.lay_disaster_pic);
        lay_disaster_pic.setOnClickListener(this);
        lay_disaster_voice = (LinearLayout) getView().findViewById(R.id.lay_disaster_voice);
        lay_disaster_voice.setOnClickListener(this);
        iv_disaster_pic = (ImageView) getView().findViewById(R.id.iv_disaster_pic);
        iv_disaster_pic_show = (ImageView) getView().findViewById(R.id.iv_disaster_pic_show);
        tv_disater_pic = (TextView) getView().findViewById(R.id.tv_disater_pic);
        lay_dsiaster_video = (LinearLayout) getView().findViewById(R.id.lay_disaster_video);
        lay_dsiaster_video.setOnClickListener(this);
        iv_disaster_video = (ImageView) getView().findViewById(R.id.iv_disaster_video);
        iv_disaster_voice = (ImageView) getView().findViewById(R.id.iv_disaster_voice);
        iv_disaster_voice_show = (ImageView) getView().findViewById(R.id.iv_disaster_voice_show);
        iv_disaster_video_show = (ImageView) getView().findViewById(R.id.iv_disaster_video_show);
        tv_disater_video = (TextView) getView().findViewById(R.id.tv_disaster_video);
        tv_disater_voice = (TextView) getView().findViewById(R.id.tv_disaster_voice);
        disaster_up_street = (TextView) getView().findViewById(R.id.disaster_up_street);
        disaster_up_street.setOnClickListener(this);
        lay_disaster_manager_up = (LinearLayout) getView().findViewById(R.id.lay_disaster_manager_up);
        lay_disaster_manager_up.setOnClickListener(this);
        lay_disaster_bottom = (LinearLayout) getView().findViewById(R.id.lay_disaster_bottom);
        tv_disaster_tg = (TextView) getView().findViewById(R.id.tv_disaster_tg);
        lay_disaster_tg = (LinearLayout) getView().findViewById(R.id.lay_disaster_tg);
        lay_disaster_tg.setOnClickListener(this);
        tv_disaster_bh = (TextView) getView().findViewById(R.id.tv_disaster_bh);
        lay_disaster_bh = (LinearLayout) getView().findViewById(R.id.lay_disaster_bh);
        lay_disaster_bh.setOnClickListener(this);
        tv_disaster_sh = (TextView) getView().findViewById(R.id.tv_disaster_sh);
        lay_disaster_sh = (LinearLayout) getView().findViewById(R.id.lay_disaster_sh);
        lay_disaster_sh.setOnClickListener(this);
        bt_manage_info = (Button) getView().findViewById(R.id.bt_manage_info);
        bt_manage_info.setOnClickListener(this);
        bt_manage_release = (Button) getView().findViewById(R.id.bt_manage_release);
        bt_manage_release.setOnClickListener(this);
        id_meiv = (MultiEditInputView) getView().findViewById(R.id.id_meiv);
        tv_disaster_up_time = (TextView) getView().findViewById(R.id.tv_disaster_up_time);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");//设置日期格式
        tv_disaster_up_time.setText(df.format(new Date()));
        tv_disaster_address = (TextView) getView().findViewById(R.id.tv_disaster_address);
        tv_disaster = (TextView) getView().findViewById(R.id.tv_disaster);
        iv_paly_video_up = (ImageView) getView().findViewById(R.id.iv_paly_video_up);
        //获取定位信息
        RegeocodeAddress mRegeocode = ZtqLocationTool.getInstance().getSearchAddress();
        PackLocalCityMain cityMain = ZtqCityDB.getInstance().getCityMain();
        //判断是否关闭定位信息
        boolean checked = ZtqLocationTool.getInstance().getIsAutoLocation();

        if (mRegeocode != null && checked) {
            if (TextUtils.isEmpty(mRegeocode.getStreetNumber().getStreet()) && TextUtils.isEmpty(mRegeocode
                    .getStreetNumber().getNumber())) {
                tv_disaster_address.setText(mRegeocode.getCity() + mRegeocode.getDistrict() + mRegeocode.getTownship
                        () + mRegeocode.getFormatAddress());
            } else {
                tv_disaster_address.setText(mRegeocode.getCity() + mRegeocode.getDistrict() + mRegeocode.getTownship() +
                        mRegeocode.getStreetNumber().getStreet() + mRegeocode.getStreetNumber().getNumber());
            }
            tv_disaster_address.setVisibility(View.VISIBLE);
            disaster_up_street.setVisibility(View.GONE);
            //获取定位2级城市
//            PackLocalCity cityMainParent2 = ZtqCityDB.getInstance().getCityInfo2_ID(cityMain.PARENT_ID);
//            zq_countyid = cityMain.ID;
            zq_town_id = cityMain.ID;

//            zq_cityid = cityMainParent2.PARENT_ID;
            zq_addr = tv_disaster_address.getText().toString();

            int spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            tv_disaster.measure(spec, spec);

            WindowManager wm = getActivity().getWindowManager();
            int screen_width = wm.getDefaultDisplay().getWidth();

            int w0 = tv_disaster.getMeasuredWidth();//控件宽度
            // double w1=tv_disaster_address.getPaint().measureText(tv_disaster_address.getText().toString());//文本宽度
            Rect bounds = new Rect();
            TextPaint paint;
            String text = tv_disaster_address.getText().toString();
            paint = tv_disaster_address.getPaint();
            paint.getTextBounds(text, 0, text.length(), bounds);
            int width = bounds.width();
            if (width > screen_width - w0 - 100) {
                tv_disaster.setGravity(Gravity.TOP);
            }
        } else {
            tv_disaster_address.setVisibility(View.GONE);
            disaster_up_street.setVisibility(View.VISIBLE);
            list_street = new ArrayList<>();
            list_street_id = new ArrayList<>();
            list = new ArrayList<>();
            list.addAll(ZtqCityDB.getInstance().getCityLv1());
            for (int i = 0; i < list.size(); i++) {
                list_street.add(list.get(i).NAME);
                list_street_id.add(list.get(i).ID);
            }
        }
        setvisibity(user_id);
    }

    /**
     * 判断user_id是否为空
     *
     * @param userid
     */
    public void setvisibity(String userid) {
        user_id = userid;
        okHttpAlarmType();
        if (TextUtils.isEmpty(userid)) {
            lay_disaster_bottom.setVisibility(View.INVISIBLE);
        } else {
            lay_disaster_bottom.setVisibility(View.VISIBLE);
            okHttpStatic();
        }
    }

    private Intent intent;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_disaster_type:
                createTimePopupWindow(tv_disaster_type, mlist, 0.25, "1").showAsDropDown(tv_disaster_type);
//                    (tv_disaster_type, Gravity.BOTTOM,
//                            0, 0);
                break;

            case R.id.disaster_up_street:
                createTimePopupWindow(disaster_up_street, list_street, 0.17, "4").showAsDropDown
                        (disaster_up_street);
//                            (disaster_up_street, Gravity.BOTTOM,
//                                    0, 0);
                break;
            case R.id.lay_disaster_pic:
                CommUtils.closeKeyboard(getActivity());
                if (TextUtils.isEmpty(user_id)) {
                    toLoginActivity();
                } else if (!is_phonoUp) {
                    pop_pic();
                } else {
                    //showProgressDialog();
                    intent = new Intent(getActivity(), ActivityPhotoFull.class);
                    intent.putExtra("path", mFilePhoto.getPath());
                    startActivityForResult(intent, Code.REQUEST_CODE_DSIASTER);
                }
                break;
            case R.id.lay_disaster_video:
                CommUtils.closeKeyboard(getActivity());
                if (TextUtils.isEmpty(user_id)) {
                    toLoginActivity();
                } else if (TextUtils.isEmpty(filPaths)) {
                    pop_video.showAtLocation(lay_disaster_pic, Gravity.BOTTOM, 0, 0);
                    WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
                    lp.alpha = 0.7f;
                    getActivity().getWindow().setAttributes(lp);
                } else {
                    intent = new Intent(getActivity(), ActivityVideoPlay.class);
                    String bpath = "file://" + filPaths;
                    intent.putExtra("path", filPaths);
                    startActivityForResult(intent, Code.LOCAL_VIDEO_PLAY);
                }
                break;

            case R.id.btnCamera:
                // 点击相机
                clickCamera();
                break;
            case R.id.btnAlbum:
                // 点击相册
                clickAlbum();
                break;
            case R.id.btnCancel:
                dismissPopupWindow();
                break;
            case R.id.lay_disaster_voice:
                CommUtils.closeKeyboard(getActivity());
                if (!TextUtils.isEmpty(voi_id)) {
                    tv_voice_time.setText(voice_time);
                    tv_voice_up.setVisibility(View.GONE);
                    bt_paly.setVisibility(View.VISIBLE);
                    tv_voice_again.setVisibility(View.VISIBLE);
                    bt_del.setVisibility(View.VISIBLE);
                }
                if (TextUtils.isEmpty(user_id)) {
                    toLoginActivity();
                } else {
                    if (checkAudioPermissions()) {
                        pop_voice.showAtLocation(lay_disaster_pic, Gravity.BOTTOM, 0, 0);
                        WindowManager.LayoutParams lps = getActivity().getWindow().getAttributes();
                        lps.alpha = 0.7f;
                        getActivity().getWindow().setAttributes(lps);
                    }
                }
                break;
            case R.id.btnvideo:
                dismissPopupWindow();
                clickVideoCamera();
                // 方法中接收结果
                break;
            case R.id.btnvideo_local:
                dismissPopupWindow();
                intent = new Intent(getActivity(), VideoListActivity.class);
                startActivityForResult(intent, Code.LOCAL_VIDEO_REQUEST);
                break;
            case R.id.btnvideo_cancel:
                dismissPopupWindow();
                break;
            case R.id.bt_voice_paly:
                if (!TextUtils.isEmpty(mFileName)) {
                    startPlaying();
                } else {
                    Toast.makeText(getActivity(), "您还未进行录制", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.tv_voice_up:
                is_voiceUp = true;
                file_type = "2";
                voice_time = tv_voice_time.getText().toString();
                reqChangeInfo(file_type);
                break;
            case R.id.tv_voice_again:
                is_voiceUp = false;
                tv_voice_up.setVisibility(View.GONE);
                bt_paly.setVisibility(View.GONE);
                tv_voice_again.setVisibility(View.GONE);
                bt_del.setVisibility(View.GONE);
                is_voiceEnd = true;
                is_voiceFirst = true;
                iv_disaster_voice.setVisibility(View.VISIBLE);
                iv_disaster_voice.setImageResource(R.drawable
                        .btn_disaster_voice);
                iv_disaster_voice_show.setVisibility(View.GONE);
                tv_disater_voice.setVisibility(View.VISIBLE);
                if (mFileName != null) {
                    tv_voice_time.setText("00:00");
                    mFileName = null;
                    voi_id = "";
                } else {
                    Toast.makeText(getActivity(), "您还未进行录制", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.bt_voice_del:
                if (!is_voiceUp) {
                    tv_voice_time.setText("00:00");
                    tv_voice_up.setVisibility(View.GONE);
                    bt_paly.setVisibility(View.GONE);
                    tv_voice_again.setVisibility(View.GONE);
                    bt_del.setVisibility(View.GONE);
                    mFileName = null;
                    voi_id = null;
                    is_voiceEnd = true;
                    is_voiceFirst = true;
                    iv_disaster_voice.setVisibility(View.VISIBLE);
                    iv_disaster_voice.setImageResource(R.drawable
                            .btn_disaster_voice);
                    iv_disaster_voice_show.setVisibility(View.GONE);
                    tv_disater_voice.setVisibility(View.VISIBLE);
                }
                dismissPopupWindow();

                break;
            case R.id.lay_disaster_manager_up:
                if (TextUtils.isEmpty(user_id)) {
                    toLoginActivity();
                }
                break;
            case R.id.lay_disaster_sh:
                //点击待审核
                FragmentDisasterReporting fragment = (FragmentDisasterReporting) getParentFragment();
                fragment.clickButton(R.id.btn_disaster_report);
                //fragment.changeFragment(1);
                fragment.updateFragment(1, "0");
                break;
            case R.id.lay_disaster_tg:
                //点击已通过
                FragmentDisasterReporting fragment1 = (FragmentDisasterReporting) getParentFragment();
                fragment1.clickButton(R.id.btn_disaster_report);
//                fragment1.changeFragment(1);
                fragment1.updateFragment(1, "1");
                break;
            case R.id.lay_disaster_bh:
                //点击被驳回
                FragmentDisasterReporting fragment2 = (FragmentDisasterReporting) getParentFragment();
                fragment2.clickButton(R.id.btn_disaster_report);
//                fragment2.changeFragment(1);
                fragment2.updateFragment(1, "2");
                break;
            case R.id.bt_manage_info:
                //点击发布管理
                FragmentDisasterReporting fragment3 = (FragmentDisasterReporting) getParentFragment();
                fragment3.clickButton(R.id.btn_disaster_report);
                fragment3.updateFragment(2, "");
//                fragment3.changeFragment(1);
                break;
            case R.id.bt_manage_release:
                //点击提交
                zq_desc = id_meiv.getContentText();
                zq_time = tv_disaster_up_time.getText().toString() + ":00";

                if (TextUtils.isEmpty(user_id)) {
                    toLoginActivity();
                } else if (TextUtils.isEmpty(zq_desc)) {
                    Toast.makeText(getActivity(), "您还未填入灾情描述", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(zq_id)) {
                    Toast.makeText(getActivity(), "您还未填选择灾情类型", Toast.LENGTH_SHORT).show();
                } else {
                    okHttpReport();
                }
                break;
        }
    }

    String[] nessaryPermissions = {
            Manifest.permission.RECORD_AUDIO
    };

    private boolean checkAudioPermissions() {
        return PermissionsTools.checkPermissions(this, nessaryPermissions, MyConfigure.REQUEST_PERMISSION_AUDIO);
    }

    /**
     * 显示音频录制
     */
    private void showAudioRecord() {
        if (pop_video != null) {
            pop_voice.showAtLocation(lay_disaster_pic, Gravity.BOTTOM, 0, 0);
            WindowManager.LayoutParams lps = getActivity().getWindow().getAttributes();
            lps.alpha = 0.7f;
            getActivity().getWindow().setAttributes(lps);
        }
    }

    private File mFilePhoto;
    // 图片最大长度
    private final int PHOTO_MAX_PENGTH = 1920;
    // 相册
    private final int REQUEST_ALBUM = 101;
    // 拍照
    private final int REQUEST_CAMERA = 102;

    private Toast toast;

    public void showToast(String str) {
        if (toast == null) {
            toast = Toast.makeText(getActivity(), str,
                    Toast.LENGTH_SHORT);
        } else {
            toast.setText(str);
        }
        toast.show();
    }


    /* 判断是否是有网络*/
    public boolean isOpenNet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context
                .CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null) {
            return true;
        } else {
            return false;
        }
    }

    public void pop_pic() {
        mPopupWindow.showAtLocation(lay_disaster_pic, Gravity.BOTTOM, 0, 0);
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = 0.7f;
        getActivity().getWindow().setAttributes(lp);
    }

    /**
     * 点击照相
     */
    private void clickCamera() {
        dismissPopupWindow();
        String tempStr = String.valueOf(System.currentTimeMillis());
        mFilePhoto = new File(PcsGetPathValue.getInstance().getMyPhotoPath() + tempStr + ".jpg");
        mFilePhoto.getParentFile().mkdirs();
        // 启动相机
        CommUtils.openCamera(this, mFilePhoto, REQUEST_CAMERA);
    }

    private void clickVideoCamera() {
        CommUtils.openVideoCamera(this, Code.VIDEO_RECORD_REQUEST, 60);
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionsTools.onRequestPermissionsResult(getActivity(), permissions, grantResults, new PermissionsTools
                .RequestPermissionResultCallback() {

            @Override
            public void onSuccess() {

                if (requestCode == REQUEST_CAMERA) {
                    clickCamera();
                } else {
                    clickVideoCamera();
                }
                switch (requestCode) {
                    case REQUEST_CAMERA:
                        clickCamera();
                        break;
                    case Code.VIDEO_RECORD_REQUEST:
                        clickVideoCamera();
                        break;
                    case MyConfigure.REQUEST_PERMISSION_AUDIO:
                        showAudioRecord();
                        break;
                }
            }

            @Override
            public void onDeny() {

            }

            @Override
            public void onDenyNeverAsk() {

            }
        });
    }


    /**
     * 点击相册
     */
    private void clickAlbum() {
        dismissPopupWindow();
        Intent it = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(it, REQUEST_ALBUM);
    }

    private String filPaths, result;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_ALBUM:
                // 相册
                resultAlbum(data);
                break;
            case REQUEST_CAMERA:
                // 相机
//                if (data.getExtras()!=null){
                resultCamera(data);
//                }else{
//                    mFilePhoto.delete();
//                    mFilePhoto=null;
//                }
                break;
            case Code.REQUEST_CODE_DSIASTER:
                dismissProgressDialog();
                result = data.getStringExtra("type");
                if (result.equals("0")) {
                    mFilePhoto.delete();
                    mFilePhoto = null;
                    pic_id = "";
                    Bitmap bd = BitmapFactory.decodeResource(getResources(), R.drawable.btn_disaster_pic);
                    Drawable drawable = new BitmapDrawable(bd);
                    iv_disaster_pic.setVisibility(View.VISIBLE);
                    iv_disaster_pic.setBackgroundDrawable(drawable);
                    iv_disaster_pic_show.setVisibility(View.GONE);
                    tv_disater_pic.setVisibility(View.VISIBLE);
                    is_phonoUp = false;
                } else if (result.equals("1")) {
                    mFilePhoto.delete();
                    mFilePhoto = null;
                    pic_id = "";
                    Bitmap bd = BitmapFactory.decodeResource(getResources(), R.drawable.btn_disaster_pic);
                    Drawable drawable = new BitmapDrawable(bd);
                    iv_disaster_pic.setVisibility(View.VISIBLE);
                    iv_disaster_pic.setBackgroundDrawable(drawable);
                    iv_disaster_pic_show.setVisibility(View.GONE);
                    tv_disater_pic.setVisibility(View.VISIBLE);
                    pop_pic();
                    is_phonoUp = false;
                }
                break;
            case Code.LOCAL_VIDEO_PLAY:
                dismissProgressDialog();
                result = data.getStringExtra("type");
                if (result.equals("0")) {
                    filPaths = null;
                    vid_id = "";
                    tub_id = "";
                    Bitmap bd = BitmapFactory.decodeResource(getResources(), R.drawable.btn_disaster_video);
                    Drawable drawable = new BitmapDrawable(bd);
                    iv_disaster_video.setVisibility(View.VISIBLE);
                    iv_disaster_video.setBackgroundDrawable(drawable);
                    iv_disaster_video_show.setVisibility(View.GONE);
                    iv_paly_video_up.setVisibility(View.GONE);
                    tv_disater_video.setVisibility(View.VISIBLE);
                } else if (result.equals("1")) {
                    filPaths = null;
                    vid_id = "";
                    tub_id = "";
                    Bitmap bd = BitmapFactory.decodeResource(getResources(), R.drawable.btn_disaster_video);
                    Drawable drawable = new BitmapDrawable(bd);
                    iv_disaster_video.setVisibility(View.VISIBLE);
                    iv_disaster_video.setBackgroundDrawable(drawable);
                    iv_disaster_video_show.setVisibility(View.GONE);
                    iv_paly_video_up.setVisibility(View.GONE);
                    tv_disater_video.setVisibility(View.VISIBLE);
                    pop_video.showAtLocation(lay_disaster_pic, Gravity.BOTTOM, 0, 0);
                    WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
                    lp.alpha = 0.7f;
                    getActivity().getWindow().setAttributes(lp);
                }
                break;
            case Code.VIDEO_RECORD_REQUEST:
                if (null != data) {
                    Uri uri = data.getData();
                    if (uri == null) {
                        return;
                    } else {
                        Cursor c = getActivity().getContentResolver().query(uri,
                                new String[]{MediaStore.MediaColumns.DATA},
                                null, null, null);
                        if (c != null && c.moveToFirst()) {
                            filPaths = c.getString(0);
                            tv_disater_video.setVisibility(View.GONE);
                            //处理上传视频的方法
                            showProgressDialog("压缩中...");
                            File tempFile = VideoFileUtils.saveTempFile(PcsGetPathValue.getInstance().getVideoPath(),
                                    getActivity(), filPaths);
                            VideoCompressor task = new VideoCompressor();
                            task.execute(tempFile);
                        }
                    }
                }
                break;
            case Code.LOCAL_VIDEO_REQUEST:
                filPaths = data.getStringExtra("path");
                if (!TextUtils.isEmpty(filPaths)) {
                    tv_disater_video.setVisibility(View.GONE);
                    //处理上传视频的方法
                    showProgressDialog("压缩中...");
                    File tempFile = VideoFileUtils.saveTempFile(PcsGetPathValue.getInstance().getVideoPath(),
                            getActivity
                                    (), filPaths);
                    VideoCompressor task = new VideoCompressor();
                    task.execute(tempFile);
                }

                break;
            case Code.REQUEST_CODE_LOGIN:
                user_id = data.getStringExtra("user_id");
                FragmentDisasterReporting fragmenta = (FragmentDisasterReporting) getParentFragment();
                fragmenta.updateFragment(2, "1");
                setvisibity(user_id);
                break;
        }
    }

    private Bitmap bt;

    //视频压缩类
    class VideoCompressor extends AsyncTask<File, Void, File> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected File doInBackground(File... params) {
            return MediaController.getInstance().convertVideo(params[0].getPath());
        }

        @Override
        protected void onPostExecute(File compressedFile) {
            super.onPostExecute(compressedFile);
            if (compressedFile != null) {
                filPaths = compressedFile.getPath();
                WindowManager wm = getActivity().getWindowManager();
                int width = wm.getDefaultDisplay().getWidth();
                int height = wm.getDefaultDisplay().getHeight();
                bt = ThumbnailUtils.createVideoThumbnail(filPaths, MediaStore.Video.Thumbnails.MINI_KIND);
                bt = ThumbnailUtils.extractThumbnail(bt, 300, 300);
                Drawable drawable = new BitmapDrawable(bt);
                iv_disaster_video_show.setVisibility(View.VISIBLE);
                iv_paly_video_up.setVisibility(View.VISIBLE);
                iv_disaster_video_show.setImageDrawable(drawable);
                iv_disaster_video.setVisibility(View.GONE);
                saveBitmap(bt);
                dismissProgressDialog();
                file_type = "3";
                reqChangeInfo(file_type);
            }
        }
    }


    /**
     * 从相机返回
     */

    private void resultCamera(Intent fromIntent) {
        if (mFilePhoto == null || !mFilePhoto.exists()) {
            Toast.makeText(getActivity(), R.string.photo_error, Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        showProgressDialog();
        // 调整图片大小
        resizeImage();
    }

    /**
     * 从相册返回
     *
     * @param fromIntent
     */
    private void resultAlbum(Intent fromIntent) {
        showProgressDialog();
        Uri uri = fromIntent.getData();
        String[] filePathColumns = {MediaStore.Images.Media.DATA};
        Cursor c = getActivity().getContentResolver().query(uri, filePathColumns, null,
                null, null);
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
        mFilePhoto = new File(PcsGetPathValue.getInstance().getMyPhotoPath()
                + oldFile.getName());
        if (mFilePhoto.exists()) {
            mFilePhoto.delete();
        }
        mFilePhoto.getParentFile().mkdirs();
        if (!copyFile(oldFile, mFilePhoto)) {
            Toast.makeText(getActivity(), R.string.photo_error, Toast.LENGTH_SHORT)
                    .show();
            dismissProgressDialog();
            return;
        }
        // 调整图片大小
        resizeImage();
    }

    /**
     * 拷贝文件
     *
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
     * 调整图片大小
     */
    private void resizeImage() {
        Point point = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(point);
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

    private ImageResizer.ImageResizerListener mImageResizerListener = new ImageResizer.ImageResizerListener() {

        @Override
        public void doneSD(String path, boolean isSucc) {
            showProgressDialog();
            // 前往提交页面
            file_type = "1";
            reqChangeInfo(file_type);
        }
    };

    public void showProgressDialog() {
        showProgressDialog(getActivity().getResources().getString(R.string.please_wait));
    }

    /**
     * 取消等待对话框
     */
    public void dismissProgressDialog() {
        if (mProgress != null && mProgress.isShowing()) {
            mProgress.dismiss();
        }
    }

    /**
     * 进度框OnCancel
     */
    private DialogInterface.OnCancelListener mProgressOnCancel = new DialogInterface.OnCancelListener() {
        @Override
        public void onCancel(DialogInterface dialog) {
            getActivity().finish();
        }
    };

    /**
     * 显示等待对话框
     */
    public void showProgressDialog(String keyWord) {
        if (mProgress == null) {
            mProgress = new ProgressDialog(getActivity());
            mProgress.setCancelable(true);
            mProgress.setCanceledOnTouchOutside(false);
            mProgress.setOnCancelListener(mProgressOnCancel);
        }
        if (mProgress.isShowing()) {
            mProgress.setMessage(keyWord);
        } else {
            mProgress.show();
            mProgress.setMessage(keyWord);
        }
    }

    private int screenHight = 0;

    /**
     * 创建下拉选择列表
     */
    public PopupWindow createTimePopupWindow(final TextView dropDownView, final List<String> dataeaum, double size,
                                             final String flag) {
        AdapterData dataAdapter = new AdapterData(getActivity(), dataeaum);
        dataAdapter.setTextViewSize(16);
        View popcontent = LayoutInflater.from(getActivity()).inflate(R.layout.pop_list_layout, null);
        ListView lv = (ListView) popcontent.findViewById(R.id.mylistviw);
        lv.setAdapter(dataAdapter);
        final PopupWindow pop = new PopupWindow(getActivity());
        pop.setContentView(popcontent);
        pop.setOutsideTouchable(false);
        pop.setWidth((dropDownView.getWidth()));
        // 调整下拉框长度
        screenHight = Util.getScreenHeight(getActivity());
//        pop.setHeight((int) (screenHight * size));
        if (dataeaum.size() > 4) {
            pop.setHeight((int) (screenHight * size));
        } else {
            pop.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        }

        pop.setFocusable(true);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pop.dismiss();
                String showTimeStr = dataeaum.get(position);
                dropDownView.setText(showTimeStr);
                if (flag.equals("1")) {
                    zq_id = mlist_id.get(position);
                }
                if (flag.equals("4")) {
                    zq_town_id = list_street_id.get(position);
                    zq_addr = disaster_up_street.getText().toString();
                }
            }
        });
        return pop;
    }


    /**
     * 初始化弹出框
     */
    private void initPopupWindow() {
        //上传照片
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.pop_photograph,
                null);
        mPopupWindow = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                // 设置背景颜色变暗
                WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
                lp.alpha = 1.0f;
                getActivity().getWindow().setAttributes(lp);
            }
        });
        // 设置可以获得焦点
        mPopupWindow.setFocusable(true);
        // 设置弹窗内可点击
        mPopupWindow.setTouchable(true);
        // 设置弹窗外可点击
        mPopupWindow.setOutsideTouchable(true);

        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());

        view.setOnKeyListener(new View.OnKeyListener() {
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
        btn.setOnClickListener(this);
        // 相册按钮
        btn = view.findViewById(R.id.btnAlbum);
        btn.setOnClickListener(this);
        // 取消按钮
        btn = view.findViewById(R.id.btnCancel);
        btn.setOnClickListener(this);

        View views = LayoutInflater.from(getActivity()).inflate(R.layout.pop_video, null);

        pop_video = new PopupWindow(views, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);
        pop_video.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                // 设置背景颜色变暗
                WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
                lp.alpha = 1.0f;
                getActivity().getWindow().setAttributes(lp);
            }
        });
        // 设置可以获得焦点
        pop_video.setFocusable(true);
        // 设置弹窗内可点击
        pop_video.setTouchable(true);
        // 设置弹窗外可点击
        pop_video.setOutsideTouchable(true);

        pop_video.setBackgroundDrawable(new BitmapDrawable());

        views.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dismissPopupWindow();
                    return true;
                }
                return false;
            }
        });
        // 本地按钮
        btn = views.findViewById(R.id.btnvideo_local);
        btn.setOnClickListener(this);
        // 录像按钮
        btn = views.findViewById(R.id.btnvideo);
        btn.setOnClickListener(this);
        // 取消按钮
        btn = views.findViewById(R.id.btnvideo_cancel);
        btn.setOnClickListener(this);

        View view_voice = LayoutInflater.from(getActivity()).inflate(R.layout.pop_voice, null);

        pop_voice = new PopupWindow(view_voice, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);
        pop_voice.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                // 设置背景颜色变暗
                WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
                lp.alpha = 1.0f;
                getActivity().getWindow().setAttributes(lp);

                tv_voice_up.setVisibility(View.GONE);
                bt_paly.setVisibility(View.GONE);
                tv_voice_again.setVisibility(View.GONE);
                bt_del.setVisibility(View.GONE);
                if (!is_voiceUp) {
                    tv_voice_time.setText("00:00");
                    mFileName = null;
                    voi_id = null;
                    iv_disaster_voice.setVisibility(View.VISIBLE);
                    iv_disaster_voice.setImageResource(R.drawable
                            .btn_disaster_voice);
                    iv_disaster_voice_show.setVisibility(View.GONE);
                    tv_disater_voice.setVisibility(View.VISIBLE);
                }
                is_voiceEnd = true;
                is_voiceFirst = true;

            }
        });
        // 设置可以获得焦点
        pop_voice.setFocusable(true);
        // 设置弹窗内可点击
        pop_voice.setTouchable(true);
        // 设置弹窗外可点击
        pop_voice.setOutsideTouchable(true);

        pop_voice.setBackgroundDrawable(new BitmapDrawable());

        view_voice.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dismissPopupWindow();
                    return true;
                }
                return false;
            }
        });

        bt_paly = (TextView) view_voice.findViewById(R.id.bt_voice_paly);
        bt_paly.setOnClickListener(this);
        tv_voice_up = (TextView) view_voice.findViewById(R.id.tv_voice_up);
        tv_voice_up.setOnClickListener(this);
        tv_voice_again = (TextView) view_voice.findViewById(R.id.tv_voice_again);
        tv_voice_again.setOnClickListener(this);
        tv_voice_time = (TextView) view_voice.findViewById(R.id.tv_voice_time);
        // 录音
        iv_voice = (ImageView) view_voice.findViewById(R.id.iv_voice);
        iv_voice.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //按下
                        if (is_voiceFirst) {
                            try {
                                onRecord(true);
                            } catch (IllegalStateException e) {
                                e.printStackTrace();
                                Toast.makeText(getActivity(), "录音时间太短", Toast.LENGTH_SHORT).show();
                            }
                            is_voiceFirst = false;
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //移动
                        break;
                    case MotionEvent.ACTION_UP:
                        //抬起
                        if (is_voiceEnd) {
                            tv_voice_up.setVisibility(View.VISIBLE);
                            bt_paly.setVisibility(View.VISIBLE);
                            tv_voice_again.setVisibility(View.VISIBLE);
                            bt_del.setVisibility(View.VISIBLE);
                            onRecord(false);
                            handler.removeMessages(0);
                            is_voiceEnd = false;
                        }
                        break;
                }
                return true;
            }
        });
        // 确定
        bt_del = (TextView) view_voice.findViewById(R.id.bt_voice_del);
        bt_del.setOnClickListener(this);

    }

    public void toLoginActivity() {
        Intent intent = new Intent(getActivity(), ActivityLogin.class);
        startActivityForResult(intent, Code.REQUEST_CODE_LOGIN);
    }

    /**
     * 关闭弹出框
     */
    private void dismissPopupWindow() {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
        if (pop_video != null && pop_video.isShowing()) {
            pop_video.dismiss();
        }
        if (pop_voice != null && pop_voice.isShowing()) {
            pop_voice.dismiss();
        }
    }

    //录音部分功能
    private static final String LOG_TAG = "AudioRecordTest";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static String mFileName = null;

    //private RecordButton mRecordButton = null;
    private MediaRecorder mRecorder = null;

    //private PlayButton   mPlayButton = null;
    private MediaPlayer mPlayer = null;

    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};
    boolean mStartRecording = true;
    boolean mStartPlaying = true, isStop = false;
    private int Count;

    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

//    private void onPlay(boolean start) {
//        if (start) {
//            startPlaying(start);
//        } else {
//            stopPlaying();
//        }
//    }

    private void startPlaying() {
//        if (mPlayer.isLooping()){
//            bt_paly.setText("播放");
//        }
        if (mPlayer == null) {
            mPlayer = new MediaPlayer();
            try {
                mPlayer.setDataSource(mFileName);
                mPlayer.prepare();
            } catch (IOException e) {
                Log.e(LOG_TAG, "prepare() failed");
            }
        }
        if (mPlayer.isPlaying()) {
            bt_paly.setText("播放");
            mPlayer.pause();
        } else {
            bt_paly.setText("暂停");
            mPlayer.start();
        }
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                bt_paly.setText("播放");
            }
        });

    }


    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }

    final Handler handler = new Handler();

    private void startRecording() {
        mFileName = getActivity().getExternalCacheDir().getAbsolutePath();
        mFileName += "/audiorecords.aac";
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        if (mRecorder != null) {
            mRecorder.start(); //开始录音
            Count = 0;

            final Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if (Count == 120) {//限时2分钟
                        mRecorder.stop();
                        mRecorder.release();
                        mRecorder = null;
                        return;
                    }
                    Count++;
                    String str = secToTime(Count);
                    tv_voice_time.setText(str);
                    handler.postDelayed(this, 1000);//每一秒刷新一次 }
                }
            };
            runnable.run();
        }
    }

    //将秒数转换成时间显示格式
    public String secToTime(int time) {
        String timeStr = null;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (time <= 0)
            return "00:00";
        else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                timeStr = unitFormat(minute) + ":" + unitFormat(second);
            } else {
                hour = minute / 60;
                if (hour > 99)
                    return "99:59:59";
                minute = minute % 60;
                second = time - hour * 3600 - minute * 60;
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
            }
        }
        return timeStr;
    }

    public String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + Integer.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }

    public void refresh() {
        id_meiv.setContentText("");
        if (mFileName != null) {
            mFileName = null;
        }
        tv_voice_time.setText("00:00");
        iv_disaster_voice_show.setVisibility(View.GONE);
        iv_disaster_voice.setVisibility(View.VISIBLE);
        iv_disaster_voice.setImageResource(R.drawable
                .btn_disaster_voice);
        tv_disater_voice.setVisibility(View.VISIBLE);
        voi_id = "";
        if (mFilePhoto != null) {
            mFilePhoto.delete();
            mFilePhoto = null;
            Bitmap bd = BitmapFactory.decodeResource(getResources(), R.drawable.btn_disaster_pic);
            Drawable drawable = new BitmapDrawable(bd);
            iv_disaster_pic_show.setVisibility(View.GONE);
            iv_disaster_pic.setVisibility(View.VISIBLE);
            iv_disaster_pic.setBackgroundDrawable(drawable);
            tv_disater_pic.setVisibility(View.VISIBLE);
            pic_id = "";
        }
        if (filPaths != null) {
            filPaths = null;
            Bitmap bd = BitmapFactory.decodeResource(getResources(), R.drawable.btn_disaster_video);
            Drawable drawable = new BitmapDrawable(bd);
            iv_disaster_video_show.setVisibility(View.GONE);
            iv_paly_video_up.setVisibility(View.GONE);
            iv_disaster_video.setVisibility(View.VISIBLE);
            iv_disaster_video.setBackgroundDrawable(drawable);
            tv_disater_video.setVisibility(View.VISIBLE);
            vid_id = "";
        }
        tv_disaster_type.setText(mlist.get(0));
        zq_id = mlist_id.get(0);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");//设置日期格式
        tv_disaster_up_time.setText(df.format(new Date()));
        is_phonoUp = false;
        num_shs = num_shs + 1;
        tv_disaster_sh.setText("待审核: " + num_shs + "条  >>");
    }

    private void stopRecording() {
        if (mRecorder != null) {
            mRecorder.setOnErrorListener(null);
            mRecorder.setPreviewDisplay(null);
            try {
                mRecorder.stop();
                mRecorder.release();
                mRecorder = null;
            } catch (IllegalStateException e) {
                Log.w("Yixia", "stopRecord", e);
            } catch (RuntimeException e) {
                Log.w("Yixia", "stopRecord", e);
            } catch (Exception e) {
                Log.w("Yixia", "stopRecord", e);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

    private File file;

    public void saveBitmap(Bitmap bitmap) {
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), "zxing_image");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = "suolue" + ".jpg";
        file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取预警分类
     */
    private void okHttpAlarmType() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param  = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    String json = param.toString();
                    final String url = CONST.BASE_URL+"alarm/getAlarmTypeList";
                    Log.e("getAlarmTypeList", url);
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
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.e("getAlarmTypeList", result);
                                    if (!TextUtil.isEmpty(result)) {
                                        try {
                                            JSONObject obj = new JSONObject(result);
                                            if (!obj.isNull("b")) {
                                                JSONObject bobj = obj.getJSONObject("b");
                                                if (!bobj.isNull("column")) {
                                                    JSONObject columnObj = bobj.getJSONObject("column");
                                                    if (!TextUtil.isEmpty(columnObj.toString())) {
                                                        PackColumnDown packDowns = new PackColumnDown();
                                                        packDowns.fillData(columnObj.toString());
                                                        if (packDowns.arrcolumnInfo != null) {
                                                            mlist.clear();
                                                            mlist_id.clear();
                                                            for (int i = 0; i < packDowns.arrcolumnInfo.size(); i++) {
                                                                mlist.add(packDowns.arrcolumnInfo.get(i).name);
                                                                mlist_id.add(packDowns.arrcolumnInfo.get(i).type);
                                                            }
                                                            tv_disaster_type.setText(mlist.get(0));
                                                            zq_id = mlist_id.get(0);
                                                        }
                                                    }
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

    /**
     * 获取灾害直报用户通过、驳回、待审核统计
     */
    private void okHttpStatic() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param  = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    JSONObject info = new JSONObject();
                    info.put("user_id", user_id);
                    param.put("paramInfo", info);
                    String json = param.toString();
                    final String url = CONST.BASE_URL+"alarm/userDisasterStatistics";
                    Log.e("userDisasterStatistics", url);
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
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.e("userDisasterStatistics", result);
                                    if (!TextUtil.isEmpty(result)) {
                                        try {
                                            JSONObject obj = new JSONObject(result);
                                            if (!obj.isNull("b")) {
                                                JSONObject bobj = obj.getJSONObject("b");
                                                if (!bobj.isNull("yj_my_zq_tj")) {
                                                    JSONObject columnObj = bobj.getJSONObject("yj_my_zq_tj");
                                                    if (!TextUtil.isEmpty(columnObj.toString())) {
                                                        dismissProgressDialog();
                                                        PackYjzqtjDown packDown = new PackYjzqtjDown();
                                                        packDown.fillData(columnObj.toString());
                                                        if (TextUtils.isEmpty(packDown.tg_num)) {
                                                            tv_disaster_tg.setText("已通过: " + "暂无  >>");
                                                        } else {
                                                            tv_disaster_tg.setText("已通过: " + packDown.tg_num + "条  >>");
                                                        }
                                                        if (TextUtils.isEmpty(packDown.ds_num)) {
                                                            tv_disaster_sh.setText("待审核: " + "暂无  >>");
                                                        } else {
                                                            num_shs = Integer.valueOf(packDown.ds_num);
                                                            tv_disaster_sh.setText("待审核: " + packDown.ds_num + "条  >>");
                                                        }
                                                        if (TextUtils.isEmpty(packDown.bh_num)) {
                                                            tv_disaster_bh.setText("被驳回: " + "暂无  >>");
                                                        } else {
                                                            tv_disaster_bh.setText("被驳回: " + packDown.bh_num + "条  >>");
                                                        }
                                                    }
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

    /**
     * 灾害直报发布
     */
    private void okHttpReport() {
        showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param  = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    JSONObject info = new JSONObject();
                    info.put("user_id", user_id);
                    info.put("town_id", zq_town_id);
                    info.put("pic_id", pic_id);
                    info.put("tub_id", tub_id);
                    info.put("vid_id", vid_id);
                    info.put("voi_id", voi_id);
                    info.put("zq_addr", zq_addr);
                    info.put("zq_desc", zq_desc);
                    info.put("zq_id", zq_id);
                    info.put("zq_time", zq_time);
                    param.put("paramInfo", info);
                    String json = param.toString();
                    Log.e("addDisasterReport", json);
                    final String url = CONST.BASE_URL+"alarm/addDisasterReport";
                    Log.e("addDisasterReport", url);
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
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.e("addDisasterReport", result);
                                    if (!TextUtil.isEmpty(result)) {
                                        try {
                                            JSONObject obj = new JSONObject(result);
                                            if (!obj.isNull("b")) {
                                                JSONObject bobj = obj.getJSONObject("b");
                                                if (!bobj.isNull("yj_sh_zq_tj")) {
                                                    JSONObject itemObj = bobj.getJSONObject("yj_sh_zq_tj");
                                                    if (!TextUtil.isEmpty(itemObj.toString())) {
                                                        dismissProgressDialog();
                                                        PackYjZqReleaseDown packDowns = new PackYjZqReleaseDown();
                                                        packDowns.fillData(itemObj.toString());
                                                        if (packDowns != null) {
                                                            if (packDowns.result.equals("1")) {
                                                                Toast.makeText(getActivity(), "发布成功", Toast.LENGTH_SHORT).show();
                                                                is_voiceEnd = true;
                                                                is_voiceFirst = true;
                                                                refresh();
                                                                FragmentDisasterReporting fragment2 = (FragmentDisasterReporting) getParentFragment();
                                                                fragment2.updateFragment(2, "1");
                                                            } else {
                                                                Toast.makeText(getActivity(), "发布失败,请重新提交", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    }
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

    /**
     * 上传图片、视频、音频
     */
    private void reqChangeInfo(final String type) {
        if (!isOpenNet()) {
            showToast(getString(R.string.net_err));
            return;
        }
        showProgressDialog("上传中...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                MultipartBody.Builder builder = new MultipartBody.Builder();
                builder.setType(MultipartBody.FORM);
                builder.addFormDataPart("token", MyApplication.TOKEN);
                builder.addFormDataPart("file_type", type);
                builder.addFormDataPart("user_id", user_id);
                if (mFilePhoto != null) {
                    if (type.equals("1")) {
                        File file = new File(mFilePhoto.getPath());
                        builder.addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));
                    }
                }
                if (mFileName != null) {
                    if (type.equals("2")) {
                        File file = new File(mFileName);
                        builder.addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dismissPopupWindow();
                        }
                    });
                }
                if (filPaths != null) {
                    if (type.equals("3")) {
                        File file = new File(filPaths);
                        builder.addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));
                    }
                }
                RequestBody body = builder.build();
                final String url = CONST.BASE_URL+"alarm/upload";
                Log.e("upload", url);
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
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.e("upload", result);
                                if (!TextUtil.isEmpty(result)) {
                                    try {
                                        JSONObject obj = new JSONObject(result);
                                        if (!obj.isNull("b")) {
                                            JSONObject bobj = obj.getJSONObject("b");
                                            if (!bobj.isNull("yj_sh_file_tj")) {
                                                JSONObject itemObj = bobj.getJSONObject("yj_sh_file_tj");
                                                if (!TextUtil.isEmpty(itemObj.toString())) {
                                                    dismissProgressDialog();
                                                    PackYjfileDown packDowns = new PackYjfileDown();
                                                    packDowns.fillData(itemObj.toString());
                                                    if (packDowns != null) {
                                                        if (file_type == "1") {
                                                            pic_id = packDowns.file_id;
                                                            BitmapDrawable drawable = new BitmapDrawable(mFilePhoto.getPath());
                                                            iv_disaster_pic_show.setVisibility(View.VISIBLE);
                                                            iv_disaster_pic_show.setBackgroundDrawable(drawable);
                                                            iv_disaster_pic.setVisibility(View.GONE);
                                                            tv_disater_pic.setVisibility(View.GONE);
                                                            is_phonoUp = true;
                                                        } else if (file_type == "2") {
                                                            is_voiceUp = true;
                                                            voi_id = packDowns.file_id;
                                                            iv_disaster_voice_show.setVisibility(View.VISIBLE);
                                                            iv_disaster_voice_show.setImageResource(R.drawable
                                                                    .recordresource);
                                                            iv_disaster_voice.setVisibility(View.GONE);
                                                            tv_disater_voice.setVisibility(View.GONE);
                                                        } else {
                                                            vid_id = packDowns.file_id;
                                                            tub_id = packDowns.tub_id;
                                                        }
                                                        Toast.makeText(getActivity(), "上传成功", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
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
            }
        }).start();
    }

}
