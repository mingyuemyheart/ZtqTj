package com.pcs.ztqtj.view.fragment.warning.disaster_reporting;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib.lib_pcs_v3.model.image.ImageCache;
import com.pcs.lib.lib_pcs_v3.model.image.ImageConstant;
import com.pcs.lib.lib_pcs_v3.model.image.ImageFetcher;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackShareAboutDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackShareAboutUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.sh_warn.PackYjZqInfoDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.sh_warn.YjZqInfo;
import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.ShareTools;
import com.pcs.ztqtj.control.tool.ZtqImageTool;
import com.pcs.ztqtj.control.tool.utils.TextUtil;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.util.OkHttpUtil;
import com.pcs.ztqtj.view.activity.photoshow.ActivityPhotoFullDetail;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 首页-预警中心-灾害直报-我的预报-详情
 */
public class FragmentDisaterMyreportDetail extends Fragment implements View.OnClickListener {

    private TextView tv_close, tv_share;
    private ImageView iv_detail_video, iv_detail_pic, iv_detail_voice,iv_paly_video;
    private TextView tv_detail_voice, tv_detail_pic, tv_detail_video;
    private TextView tv_detail_address, tv_detail_time, tv_detail_title, tv_detail_type, tv_detail_content;
    private ProgressDialog mProgress;
    private String id = "", user_id, type = "";
    private String pic_url, voi_url, vid_url, tub_url;
    private View view;
    private ScrollView scroll;
    private LinearLayout lay_main_detail;
    private static FragmentDisaterMyreportDetail instance;
    private LinearLayout lay_fujian01,lay_fujian02,lay_fujian03,lay_fujian,lay_fujian_content;

    public static FragmentDisaterMyreportDetail getInstance() {
        if (instance == null) {
            instance = new FragmentDisaterMyreportDetail();
        }
        return instance;
    }

    private Fragment mCurrentFragment;

    public void setCurrentFragment(Fragment fragment) {
        mCurrentFragment = fragment;
    }

    public void updateFragment(String id) {
        this.id = id;
        user_id = MyApplication.UID;
        okHttpReportDetail();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState) {
        view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_report_detail, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        okHttpReportDetail();
    }

    private void initView() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            id = bundle.getString("id", "");
        }
        tv_close = (TextView) getView().findViewById(R.id.tv_close);
        tv_close.setOnClickListener(this);
        tv_share = (TextView) getView().findViewById(R.id.tv_share);
        tv_share.setOnClickListener(this);
        iv_detail_video = (ImageView) getView().findViewById(R.id.iv_detail_video);
        iv_detail_video.setOnClickListener(this);
        iv_detail_pic = (ImageView) getView().findViewById(R.id.iv_detail_pic);
        iv_detail_pic.setOnClickListener(this);
        iv_detail_voice = (ImageView) getView().findViewById(R.id.iv_detail_voice);
        iv_detail_voice.setOnClickListener(this);
        tv_detail_voice = (TextView) getView().findViewById(R.id.tv_detail_voice);
        tv_detail_pic = (TextView) getView().findViewById(R.id.tv_detail_pic);
        tv_detail_video = (TextView) getView().findViewById(R.id.tv_detail_video);
        tv_detail_address = (TextView) getView().findViewById(R.id.tv_detail_address);
        tv_detail_time = (TextView) getView().findViewById(R.id.tv_detail_time);
        tv_detail_title = (TextView) getView().findViewById(R.id.tv_detail_title);
        tv_detail_type = (TextView) getView().findViewById(R.id.tv_detail_type);
        tv_detail_content = (TextView) getView().findViewById(R.id.tv_detail_content);
        iv_paly_video= (ImageView) getView().findViewById(R.id.iv_paly_video);
        scroll = (ScrollView) getView().findViewById(R.id.scroll);
        lay_main_detail = (LinearLayout) getView().findViewById(R.id.lay_main_detail);

        lay_fujian01= (LinearLayout) getView().findViewById(R.id.lay_fujian01);
        lay_fujian02= (LinearLayout) getView().findViewById(R.id.lay_fujian02);
        lay_fujian03= (LinearLayout) getView().findViewById(R.id.lay_fujian03);
        lay_fujian= (LinearLayout) getView().findViewById(R.id.lay_fujian);
        lay_fujian_content= (LinearLayout) getView().findViewById(R.id.lay_fujian_content);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_close:
                changeFragment();
                break;
            case R.id.tv_share:
                clickShare();
                break;
            case R.id.iv_detail_video:
                if (!TextUtils.isEmpty(vid_url) && !TextUtils.equals(vid_url, "null")) {
                    Uri uri = Uri.parse(getString(R.string.msyb)+"/"+vid_url);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(uri, "video/mp4");
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), "暂无现场视频", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.iv_detail_pic:
                if (!TextUtils.isEmpty(pic_url) && !TextUtils.equals(pic_url, "null")) {
                    Intent intent = new Intent(getActivity(), ActivityPhotoFullDetail.class);
                    intent.putExtra("url", getString(R.string.msyb)+"/"+pic_url);
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), "暂无现场图片", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.iv_detail_voice:
                if (!TextUtils.isEmpty(voi_url) && !TextUtils.equals(voi_url, "null")) {
                    Intent it = new Intent(Intent.ACTION_VIEW);
                    it.setDataAndType(Uri.parse(getString(R.string.msyb)+"/"+voi_url), "audio/MP3");
                    startActivity(it);
                } else {
                    Toast.makeText(getActivity(), "暂无现场录音", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

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
        SetBottom();
//        scroll.scrollTo(0,1280);
        Bitmap shareBitmap = ZtqImageTool.getInstance().getScreenBitmap(view);
        shareBitmap = ZtqImageTool.getInstance().stitchQR(getActivity(), shareBitmap);
        PackShareAboutDown shareDown = (PackShareAboutDown) PcsDataManager.getInstance().getNetPack(PackShareAboutUp
                .getNameCom());
        ShareTools.getInstance(getActivity()).setShareContent(shareDown.share_content, shareBitmap,"1").showWindow(scroll);
    }

    public void SetBottom() {
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                scroll.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    public void SetTop() {
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                scroll.fullScroll(ScrollView.FOCUS_UP);
            }
        });
    }

    public void initView_Refresh(YjZqInfo yjZqInfo) {
        SetTop();
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

        if (!TextUtils.isEmpty(pic_url) && !TextUtils.equals(pic_url, "null")) {
            lay_fujian01.setVisibility(View.VISIBLE);
            getImageFetcher().loadImage(getString(R.string.msyb)+"/"+pic_url, iv_detail_pic, ImageConstant.ImageShowType.SRC);
        } else {
            lay_fujian01.setVisibility(View.GONE);
//            iv_detail_pic.setImageResource(R.drawable
//                    .btn_disaster_pic);
        }

        if (!TextUtils.isEmpty(voi_url)  && !TextUtils.equals(voi_url, "null")) {
            lay_fujian02.setVisibility(View.VISIBLE);
            iv_detail_voice.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable
                    .recordresource));
        } else {
            lay_fujian02.setVisibility(View.GONE);
//            iv_detail_voice.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable
//                    .btn_disaster_voice));
        }

        if (!TextUtils.isEmpty(vid_url)  && !TextUtils.equals(vid_url, "null")) {
            lay_fujian03.setVisibility(View.VISIBLE);
            getImageFetcher().loadImage(getString(R.string.msyb)+"/"+tub_url, iv_detail_video, ImageConstant.ImageShowType.SRC);
            iv_paly_video.setVisibility(View.VISIBLE);
        } else {
            lay_fujian03.setVisibility(View.GONE);
//            iv_detail_video.setImageResource(R.drawable
//                    .btn_disaster_video);
//            iv_paly_video.setVisibility(View.GONE);
        }
    }


    private ImageFetcher mImageFetcher;

    protected ImageFetcher getImageFetcher() {
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
                getActivity());
        cacheParams.setMemCacheSizePercent(0.25f);
        mImageFetcher = new ImageFetcher(getActivity());
        mImageFetcher.addImageCache(getActivity().getSupportFragmentManager(),
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

    /**
     * 进度框OnCancel
     */
    private DialogInterface.OnCancelListener mProgressOnCancel = new DialogInterface.OnCancelListener() {
        @Override
        public void onCancel(DialogInterface dialog) {
            getActivity().finish();
        }
    };


    public void changeFragment() {
        if (mCurrentFragment != null) {
            getFragmentManager().beginTransaction()
                    .hide(FragmentDisaterMyreportDetail.getInstance())
                    .show(mCurrentFragment)
                    .commit();
        } else {
            getFragmentManager().beginTransaction()
                    .hide(FragmentDisaterMyreportDetail.getInstance())
                    .commit();
        }
    }

    /**
     * 获取我的报告详情
     */
    private void okHttpReportDetail() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param  = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    JSONObject info = new JSONObject();
                    info.put("user_id", user_id);
                    info.put("reportId", id);
                    param.put("paramInfo", info);
                    String json = param.toString();
                    Log.e("disasterDetailById", json);
                    final String url = CONST.BASE_URL+"alarm/disasterDetailById";
                    Log.e("disasterDetailById", url);
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
                                    Log.e("disasterDetailById", result);
                                    if (!TextUtil.isEmpty(result)) {
                                        try {
                                            JSONObject obj = new JSONObject(result);
                                            if (!obj.isNull("b")) {
                                                JSONObject bobj = obj.getJSONObject("b");
                                                if (!bobj.isNull("yj_zq_info")) {
                                                    JSONObject itemObj = bobj.getJSONObject("yj_zq_info");
                                                    if (!TextUtil.isEmpty(itemObj.toString())) {
                                                        dismissProgressDialog();
                                                        PackYjZqInfoDown packDown = new PackYjZqInfoDown();
                                                        packDown.fillData(itemObj.toString());
                                                        if (packDown.yjZqInfo != null) {
                                                            initView_Refresh(packDown.yjZqInfo);
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

}
