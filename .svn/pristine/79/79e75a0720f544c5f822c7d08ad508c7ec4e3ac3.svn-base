package com.pcs.ztqtj.view.fragment.warning.emergency_responsibility;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.ShareTools;
import com.pcs.ztqtj.control.tool.ZtqImageTool;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqWithPhoneListAndHelp;
import com.pcs.ztqtj.view.activity.photoshow.ActivityPhotoFullDetail;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib.lib_pcs_v3.model.image.ImageCache;
import com.pcs.lib.lib_pcs_v3.model.image.ImageConstant;
import com.pcs.lib.lib_pcs_v3.model.image.ImageFetcher;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackShareAboutDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackShareAboutUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.sh_warn.PackYjZqInfoDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.sh_warn.PackYjZqInfoUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.sh_warn.YjZqInfo;

/**
 * Created by Administrator on 2017/11/10 0010.
 * chen_jx
 */

public class ActivityOtherDetail extends FragmentActivityZtqWithPhoneListAndHelp implements View.OnClickListener {

    private TextView tv_detail_name, tv_detail_number, tv_detail_title, tv_detail_content, tv_detail_type,
            tv_detail_time, tv_detail_address, tv_close,tv_share;
    private LinearLayout lay_fujian, lay_fujian01, lay_fujian02, lay_fujian03, lay_fujian_content;
    private ImageView iv_detail_pic, iv_detail_voice, iv_detail_video, iv_paly_video;
    private String vid_url, pic_url, voi_url, id = "",tub_url,name,phone;
    private ProgressDialog mProgress;
    protected ImageButton btnHelp = null;
    private ScrollView scroll;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otherself_detail);
        setTitleText("应急责任人名单");
        initView();
        initEvent();
        initData();
    }

    public void initView() {
        iv_detail_video = (ImageView) findViewById(R.id.iv_detail_video);
        iv_detail_pic = (ImageView) findViewById(R.id.iv_detail_pic);
        iv_detail_voice = (ImageView) findViewById(R.id.iv_detail_voice);
        iv_paly_video = (ImageView) findViewById(R.id.iv_paly_video);
        lay_fujian01 = (LinearLayout) findViewById(R.id.lay_fujian01);
        lay_fujian02 = (LinearLayout) findViewById(R.id.lay_fujian02);
        lay_fujian03 = (LinearLayout) findViewById(R.id.lay_fujian03);
        lay_fujian = (LinearLayout) findViewById(R.id.lay_fujian);
        lay_fujian_content = (LinearLayout) findViewById(R.id.lay_fujian_content);
        tv_detail_address = (TextView) findViewById(R.id.tv_detail_address);
        tv_detail_name = (TextView) findViewById(R.id.tv_detail_name);
        tv_detail_number = (TextView) findViewById(R.id.tv_detail_number);
        tv_detail_title = (TextView) findViewById(R.id.tv_detail_title);
        tv_detail_content = (TextView) findViewById(R.id.tv_detail_content);
        tv_detail_type= (TextView) findViewById(R.id.tv_detail_type);
        tv_detail_time= (TextView) findViewById(R.id.tv_detail_time);
        tv_close= (TextView) findViewById(R.id.tv_close);
        btnHelp = (ImageButton) findViewById(R.id.btn_right);
        btnHelp.setVisibility(View.GONE);
        tv_share = (TextView) findViewById(R.id.tv_share);
        scroll = (ScrollView) findViewById(R.id.scroll);
    }

    public void initData() {
        Intent intent=getIntent();
        id=intent.getStringExtra("id");
        name=intent.getStringExtra("name");
        phone=intent.getStringExtra("phone");
        tv_detail_name.setText("责任人: "+name);
        tv_detail_number.setText(phone);
        showProgressDialog();
        Check_Info();
    }

    public void initEvent() {
        iv_detail_video.setOnClickListener(this);
        iv_detail_pic.setOnClickListener(this);
        iv_detail_voice.setOnClickListener(this);
        tv_close.setOnClickListener(this);
        tv_share.setOnClickListener(this);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private PackYjZqInfoUp packYjZqInfoUp = new PackYjZqInfoUp();

    public void Check_Info() {
        packYjZqInfoUp.id = id;
        PcsDataDownload.addDownload(packYjZqInfoUp);
        PcsDataBrocastReceiver.registerReceiver(ActivityOtherDetail.this, mReceiver);
        //下载
        PcsDataDownload.addDownload(packYjZqInfoUp);
    }
    private PcsDataBrocastReceiver mReceiver = new PcsDataBrocastReceiver() {
        @Override
        public void onReceive(String nameStr, String errorStr) {
            if (TextUtils.isEmpty(nameStr)) {
                return;
            }
            if (nameStr.equals(packYjZqInfoUp.getName())) {
                //等待框
                dismissProgressDialog();
                PackYjZqInfoDown packDown = (PackYjZqInfoDown) PcsDataManager.getInstance().getNetPack(nameStr);
                if (packDown == null) {
                    PcsDataDownload.addDownload(packYjZqInfoUp);
                    return;
                }
                PcsDataBrocastReceiver.unregisterReceiver(ActivityOtherDetail.this, mReceiver);
                if (packDown.yjZqInfo != null) {
                    initView_Refresh(packDown.yjZqInfo);
                }
            }

        }
    };

    public void initView_Refresh(YjZqInfo yjZqInfo) {
        tv_detail_address.setText(yjZqInfo.zq_addr);
        tv_detail_content.setText(yjZqInfo.zq_desc);
        tv_detail_title.setText(yjZqInfo.zq_title);
        tv_detail_time.setText(yjZqInfo.zq_time);
        tv_detail_type.setText(yjZqInfo.zq_name);
        pic_url = yjZqInfo.pic_url;
        vid_url = yjZqInfo.vid_url;
        voi_url = yjZqInfo.voi_url;
        tub_url = yjZqInfo.tub_url;

        if (TextUtils.isEmpty(pic_url)&&TextUtils.isEmpty(voi_url)&&TextUtils.isEmpty(vid_url)){
            lay_fujian_content.setVisibility(View.GONE);
            lay_fujian.setVisibility(View.GONE);
        }else{
            lay_fujian_content.setVisibility(View.VISIBLE);
            lay_fujian.setVisibility(View.VISIBLE);
        }

        if (!TextUtils.isEmpty(pic_url)) {
            lay_fujian01.setVisibility(View.VISIBLE);
            getImageFetcher().loadImage(pic_url, iv_detail_pic, ImageConstant.ImageShowType.SRC);
        } else {
            lay_fujian01.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(voi_url)) {
            lay_fujian02.setVisibility(View.VISIBLE);
            iv_detail_voice.setBackgroundDrawable(ActivityOtherDetail.this.getResources().getDrawable(R.drawable
                    .recordresource));
        } else {
            lay_fujian02.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(vid_url)) {
            lay_fujian03.setVisibility(View.VISIBLE);
            getImageFetcher().loadImage(tub_url, iv_detail_video, ImageConstant.ImageShowType.SRC);
            iv_paly_video.setVisibility(View.VISIBLE);
        } else {
            lay_fujian03.setVisibility(View.GONE);
        }
    }
    
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_detail_video:
                if (!TextUtils.isEmpty(vid_url)) {
                    Uri uri = Uri.parse(vid_url);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(uri, "video/mp4");
                    startActivity(intent);
                } else {
                    Toast.makeText(ActivityOtherDetail.this, "暂无现场视频", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.iv_detail_pic:
                if (!TextUtils.isEmpty(pic_url)) {
                    Intent intent = new Intent(ActivityOtherDetail.this, ActivityPhotoFullDetail.class);
                    intent.putExtra("url", pic_url);
                    startActivity(intent);
                } else {
                    Toast.makeText(ActivityOtherDetail.this, "暂无现场图片", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.iv_detail_voice:
                if (!TextUtils.isEmpty(voi_url)) {
                    Intent it = new Intent(Intent.ACTION_VIEW);
                    it.setDataAndType(Uri.parse(voi_url), "audio/MP3");
                    startActivity(it);
                } else {
                    Toast.makeText(ActivityOtherDetail.this, "暂无现场录音", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.tv_close:
                ActivityOtherDetail.this.finish();
                break;
            case R.id.tv_share:
                clickShare();
        }
    }

    private View view;
    //点击分享
    private void clickShare() {
//        scroll.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                scroll.post(new Runnable() {
//                    public void run() {
//                        scroll.fullScroll(View.FOCUS_DOWN);
//                    }
//                });
//            }
//        });
//        scroll.scrollTo(0,1280);
        Bitmap shareBitmap = ZtqImageTool.getInstance().getScreenBitmapNew(ActivityOtherDetail.this);
        shareBitmap = ZtqImageTool.getInstance().stitchQR(ActivityOtherDetail.this, shareBitmap);
        PackShareAboutDown shareDown = (PackShareAboutDown) PcsDataManager.getInstance().getNetPack(PackShareAboutUp
                .getNameCom());
        ShareTools.getInstance(ActivityOtherDetail.this).setShareContent(getTitleText(),shareDown.share_content, shareBitmap,"1").showWindow(scroll);
    }

    private ImageFetcher mImageFetcher;

    public ImageFetcher getImageFetcher() {
        if (mImageFetcher == null) {
            createImageFetcher();
        }

        return mImageFetcher;
    }

    /**
     * 创建图片获取类
     */
    protected void createImageFetcher() {
        ImageCache.ImageCacheParams cacheParams = new ImageCache.ImageCacheParams(
                ActivityOtherDetail.this);
        cacheParams.setMemCacheSizePercent(0.25f);
        mImageFetcher = new ImageFetcher(ActivityOtherDetail.this);
        mImageFetcher.addImageCache(ActivityOtherDetail.this.getSupportFragmentManager(),
                cacheParams);
        mImageFetcher.setLoadingImage(R.drawable.no_pic);
    }

    public void showProgressDialog() {
        showProgressDialog("请等待...");
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
     * 显示等待对话框
     */
    public void showProgressDialog(String keyWord) {
        if (mProgress == null) {
            mProgress = new ProgressDialog(ActivityOtherDetail.this);
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

    /**
     * 进度框OnCancel
     */
    private DialogInterface.OnCancelListener mProgressOnCancel = new DialogInterface.OnCancelListener() {
        @Override
        public void onCancel(DialogInterface dialog) {
            ActivityOtherDetail.this.finish();
        }
    };

}
