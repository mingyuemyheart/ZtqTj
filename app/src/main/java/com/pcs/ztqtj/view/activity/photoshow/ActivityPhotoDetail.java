package com.pcs.ztqtj.view.activity.photoshow;

import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib.lib_pcs_v3.model.image.ImageConstant;
import com.pcs.lib.lib_pcs_v3.model.image.ListenerImageLoad;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalPhotoUser;
import com.pcs.lib_ztqfj_v2.model.pack.net.photowall.PackPhotoCommentDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.photowall.PackPhotoCommentListUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.photowall.PackPhotoCommentUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.photowall.PackPhotoSingle;
import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.photo.AdapterPhotoComment;
import com.pcs.ztqtj.control.tool.CommUtils;
import com.pcs.ztqtj.control.tool.image.GetImageView;
import com.pcs.ztqtj.control.tool.utils.TextUtil;
import com.pcs.ztqtj.model.PhotoShowDB;
import com.pcs.ztqtj.model.PhotoShowDB.PhotoShowType;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.util.OkHttpUtil;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

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
 * 实景开拍详情页面
 */
public class ActivityPhotoDetail extends FragmentActivityZtqBase {

    /**
     * 详情信息
     */
    private PackPhotoSingle mInfo;
    /**
     * 适配器
     */
    private AdapterPhotoComment mAdapter;
    /**
     * 上传包：评论列表
     */
    private PackPhotoCommentListUp mPackCommentListUp = new PackPhotoCommentListUp();
    /**
     * 上传包：评论
     */
    private PackPhotoCommentUp mPackCommentUp = new PackPhotoCommentUp();
    private PackPhotoCommentDown mPackCommentDown = new PackPhotoCommentDown();

    /**
     * 当前显示的是第几张
     */
    private int positionImage = 0;
    /**
     * 提片列表
     */
    private List<PackPhotoSingle> imageList = new ArrayList<>();

    // 个人头像
    private ImageView ivHead;

    private GetImageView getImageView = new GetImageView();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);
        setTitleText(R.string.photo_title_detail);
        createImageFetcher();
        initDataPro();
        initView();
        // 初始化数据
        initData();
        // 初始化点击事件
        initOnClick();
    }

    /**
     * 点赞
     */
    private ImageView image_praise;
    /**
     * 点赞
     */
    private TextView text_praise;
    /**
     * 提交
     */
    private Button btn_submit;
    /**
     * 点击照片
     */
    private ImageView image_photo;

    // 昵称
    private TextView text_name;
    // 位置
    private TextView text_address;
    // 时间
    private TextView text_time;
    // 实况
    private TextView text_condition;
    // 描述
    private TextView text_desc;
    /**
     * 浏览数
     */
    private TextView text_browse;
    /**
     * 输入信息
     */
    private EditText edit_message;

    /**
     * 滚动条
     */
    private ScrollView scroll_view;

    /**
     * 关联视图
     */
    private void initView() {
        image_praise = (ImageView) findViewById(R.id.image_praise);
        text_praise = (TextView) findViewById(R.id.text_praise);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        image_photo = (ImageView) findViewById(R.id.image_photo);

        text_name = (TextView) findViewById(R.id.text_name);
        text_address = (TextView) findViewById(R.id.text_address);
        text_time = (TextView) findViewById(R.id.text_time);
        text_condition = (TextView) findViewById(R.id.text_condition);
        text_desc = (TextView) findViewById(R.id.text_desc);
        text_browse = (TextView) findViewById(R.id.text_browse);
        edit_message = (EditText) findViewById(R.id.edit_message);
        scroll_view = (ScrollView) findViewById(R.id.scroll_view);
        ivHead = (ImageView) findViewById(R.id.iv_head);
    }

    @Override
    protected void onResume() {
        super.onResume();
        PcsDataBrocastReceiver.registerReceiver(this, mReceiver);
    }

    @Override
    protected void onPause() {
        super.onPause();
        PcsDataBrocastReceiver.unregisterReceiver(this, mReceiver);
    }

    /**
     * 初始化点击事件
     */

    private void initOnClick() {
        image_praise.setOnClickListener(mOnClick);
        text_praise.setOnClickListener(mOnClick);
        btn_submit.setOnClickListener(mOnClick);
        image_photo.setOnClickListener(mOnClick);
//        image_photo.setOnTouchListener(touchListener);
        scroll_view.setOnTouchListener(touchListener);
    }

    private OnTouchListener touchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                    int count = (int) event.getX() - pointDown.x;
                    int countX = Math.abs(count);
                    int countY = (int) Math.abs(event.getY() - pointDown.y);
                    if (count > 0) {
                        // x轴移动大于y轴的两倍,并且移动距离至少大于100
                        if (countX > 2 * countY && countX > 100) {
                            touchDirectionListener(TOUCHDIRECTION.Left);
                        } else if (v.getId() == R.id.image_photo) {
                            clickPhoto();
                        }
                    } else {
                        // x轴移动大于y轴的两倍,并且移动距离至少大于100
                        if (countX > 2 * countY && countX > 100) {
                            touchDirectionListener(TOUCHDIRECTION.Right);
                        } else if (v.getId() == R.id.image_photo) {
                            clickPhoto();
                        }
                    }
                    break;
                case MotionEvent.ACTION_DOWN:
                    pointDown.x = (int) event.getX();
                    pointDown.y = (int) event.getY();
                    break;
            }
            return false;
        }
    };

    private enum TOUCHDIRECTION {
        Left, Right, NULLCHANGE;
    }

    private void touchDirectionListener(TOUCHDIRECTION direction) {
        if (direction == TOUCHDIRECTION.Left) {
            positionImage--;
            if (positionImage < 0) {
                positionImage++;
            } else {
                initData();
            }
        } else if (direction == TOUCHDIRECTION.Right) {
            positionImage++;
            if (positionImage >= imageList.size()) {
                positionImage--;
            } else {
                initData();
            }

        }
    }

    private Point pointDown = new Point();

    /**
     * 初始化数据
     *
     * @return
     */
    private void initData() {
        // 等待框
        showProgressDialog();
        mInfo = imageList.get(positionImage);
        // 显示详情
        showDetail();
        // 刷新点赞
        refreshPraise();
        reqestData();
    }

    /**
     * 是否是精品
     */
    private boolean isSpecial = false;

    /**
     * 是否是通过个人中心进入该页面的
     */
    private boolean fromCenterList = false;

    private void initDataPro() {
        PhotoShowDB db = PhotoShowDB.getInstance();
        positionImage = getIntent().getIntExtra("position", 0);
        fromCenterList = getIntent().getBooleanExtra("ActivityPhotoUserCenter", false);
        isSpecial = getIntent().getBooleanExtra("isSpecial", false);
        if (fromCenterList) {
            imageList = db.getPhotoListCenter();
            // mInfo = db.getPhotoListCenter().get(position);
        } else {
            if (isSpecial) {
                imageList = db.getPhotoList(PhotoShowType.SPECIAL);
            } else {
                imageList = db.getPhotoList(PhotoShowType.ORDINARY);
            }
            // mInfo = db.getPhotoList().get(position);
        }
    }

    /**
     * 请求本张图片的其他数据
     */
    private void reqestData() {
        // 初始化评论列表
        initCommentList();
        // 请求增加浏览数
        reqBrowseAdd();
        // 请求评论列表
        reqCommentList();
    }

    /**
     * 显示详情
     */
    private void showDetail() {
        text_address.setText(mInfo.address);
        text_time.setText(mInfo.date_time);
        text_condition.setText(mInfo.weather);
        text_desc.setText(mInfo.des);

        //如果是通过个人中心进入该页面的则显示当前登陆用户的头像
        if (fromCenterList) {
            getImageView.setImageView(this, MyApplication.UID, ivHead);
            text_name.setText(MyApplication.NAME);
        } else { //否则显示默认头像
            if(!TextUtils.isEmpty(mInfo.head_url)) {
                getImageView.setImageView(this, mInfo.head_url, ivHead);
            } else {
                ivHead.setImageResource(R.drawable.icon_stranger);
            }
            text_name.setText(mInfo.nickName);
        }
        String url_prev = getString(R.string.sjkp);
        getImageFetcher().addListener(mListenerImageLoad);
        getImageFetcher().loadImage(url_prev + mInfo.thumbnailUrl, image_photo, ImageConstant.ImageShowType.SRC);
    }

    /**
     * 刷新点赞
     */
    private void refreshPraise() {
        // 图标
        if (mInfo.isPraised) {
            image_praise.setImageResource(R.drawable.img_praise_red);
        }
        // 点赞数
        text_praise.setText(mInfo.praise);
    }

    /**
     * 初始化评论列表
     *
     * @return
     */
    private void initCommentList() {
        mAdapter = new AdapterPhotoComment(this);
        ListView listView = (ListView) findViewById(R.id.listview);
        listView.setAdapter(mAdapter);
    }

    /**
     * 请求增加浏览数
     */
    private void reqBrowseAdd() {
        if (TextUtils.isEmpty(mInfo.browsenum)) {
            mInfo.browsenum = "1";
        } else {
            int num = Integer.valueOf(mInfo.browsenum) + 1;
            mInfo.browsenum = String.valueOf(num);
        }

        text_browse.setText(mInfo.browsenum);
        // 设置首页刷新
        PhotoShowDB.getInstance().setRefreshType(PhotoShowDB.PhotoRefreshType.VIEW);
        okHttpScane();
    }

    /**
     * 请求评论列表
     */
    private void reqCommentList() {
        mPackCommentListUp.page = "1";
        mPackCommentListUp.itemId = mInfo.itemId;
        PcsDataDownload.addDownload(mPackCommentListUp);
    }

    /**
     * 点赞
     */
    private void clickPraise() {
        if (mInfo.isPraised) {
            return;
        }
        if(!isOpenNet()){
            showToast(getString(R.string.net_err));
            return ;
        }
        showProgressDialog();
        okHttpPraise();
    }

    /**
     * 提交
     */
    private void clickSubmit() {
        EditText edit = (EditText) findViewById(R.id.edit_message);
        String str = edit.getText().toString().trim();
        if (TextUtils.isEmpty(str)) {
            // 评论为空
            return;
        }
        if(!isOpenNet()){
            showToast(getString(R.string.net_err));
            return ;
        }
        showProgressDialog();
        PackLocalPhotoUser packUser = PhotoShowDB.getInstance().getUserPack();
        mPackCommentUp.commentsUserId = packUser.userId;
        mPackCommentUp.itemId = mInfo.itemId;
        mPackCommentUp.desc = str;
        PcsDataDownload.addDownload(mPackCommentUp);
    }

    /**
     * 点击照片
     */
    private void clickPhoto() {
        String url = getString(R.string.sjkp) + mInfo.thumbnailUrl;
        Intent intent = new Intent();
        intent.setClass(this, ActivityPhotoFullDetail.class);
        intent.putExtra("url", url);
        startActivity(intent);
    }

    /**
     * 浏览数返回
     */
    private void receiveBrowseAdd(String str) {
        Log.i("z", "浏览成功" + str);
    }

    /**
     * 点赞返回
     */
    private void receivePraise(boolean isSucc) {
        dismissProgressDialog();
        if (!isSucc) {
            // 不成功
            Toast.makeText(this, R.string.praise_err, Toast.LENGTH_SHORT).show();
            return;
        }
        // 成功
        mInfo.isPraised = true;
        int num = Integer.valueOf(mInfo.praise);
        mInfo.praise = String.valueOf(num + 1);
        refreshPraise();
    }

    /**
     * 评论返回
     */
    private void receiveComment() {
        mPackCommentDown = (PackPhotoCommentDown) PcsDataManager.getInstance().getNetPack(mPackCommentUp.getName());
        if (!mPackCommentDown.isSucc) {
            // 不成功
            Toast.makeText(this, R.string.comment_err, Toast.LENGTH_SHORT).show();
            return;
        }
        // 成功
        reqCommentList();
        // 清空输入框
        edit_message.setText("");
        CommUtils.closeKeyboard(this, edit_message);
        showToast(getString(R.string.comment_succ));
        dismissProgressDialog();
    }

    /**
     * 评论列表返回
     */
    private void receiveCommentList() {
        mAdapter.setJsonData(mPackCommentListUp.getName());
        mAdapter.notifyDataSetChanged();
        dismissProgressDialog();
    }

    /**
     * 点击事件
     */
    private OnClickListener mOnClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.image_praise:
                case R.id.text_praise:
                    // 点赞
                    clickPraise();
                    break;
                case R.id.btn_submit:
                    // 提交
                    clickSubmit();
                    break;
                case R.id.image_photo:
                    // 点击照片
                     clickPhoto();
                    break;
            }
        }
    };

    /**
     * 广播接收
     */
    private PcsDataBrocastReceiver mReceiver = new PcsDataBrocastReceiver() {
        @Override
        public void onReceive(String nameStr, String errorStr) {
            if (mPackCommentUp.getName().equals(nameStr)) {
                // 评论返回
                receiveComment();
            } else if (mPackCommentListUp.getName().equals(nameStr)) {
                // 评论列表返回
                receiveCommentList();
            }
        }
    };

    /**
     * 图片加载监听
     */
    private ListenerImageLoad mListenerImageLoad = new ListenerImageLoad() {

        @Override
        public void done(String key, boolean isSucc) {
            if (TextUtils.isEmpty(key) || !isSucc) {
                return;
            }
            // 屏幕宽度
            int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
            if (getImageFetcher().getImageCache() == null) {
                return;
            }
            // 高宽比例
            BitmapDrawable drawable = getImageFetcher().getImageCache()
                    .getBitmapFromAllCache(key);
            float scale = ((float) drawable.getIntrinsicHeight())
                    / ((float) drawable.getIntrinsicWidth());
            // 设置高度
            FrameLayout layoutPhoto = (FrameLayout) findViewById(R.id.layout_photo);
            LayoutParams params = layoutPhoto.getLayoutParams();
            params.height = (int) (((float) screenWidth) * scale);
            layoutPhoto.setLayoutParams(params);
        }

    };

    /**
     * 点赞
     * "status": 1,状态，1代表访问量新增+1；2触发点赞，点赞数量将新增+1
     */
    private void okHttpPraise() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param  = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    JSONObject info = new JSONObject();
                    info.put("userId", MyApplication.UID);
                    info.put("id", mInfo.itemId);
                    info.put("status", "2");
                    param.put("paramInfo", info);
                    String json = param.toString();
                    Log.e("livePhotoView", json);
                    final String url = CONST.BASE_URL+"live_photo/livePhotoView";
                    Log.e("livePhotoView", url);
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
                                        try {
                                            JSONObject obj = new JSONObject(result);
                                            if (!obj.isNull("status")) {
                                                String status = obj.getString("status");
                                                if (TextUtils.equals(status, "success")) {
                                                    receivePraise(true);
                                                } else {
                                                    receivePraise(false);
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
     * 浏览量、点赞
     * "status": 1,状态，1代表访问量新增+1；2触发点赞，点赞数量将新增+1
     */
    private void okHttpScane() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param  = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    JSONObject info = new JSONObject();
                    info.put("userId", MyApplication.UID);
                    info.put("id", mInfo.itemId);
                    info.put("status", "1");
                    param.put("paramInfo", info);
                    String json = param.toString();
                    Log.e("livePhotoView", json);
                    final String url = CONST.BASE_URL+"live_photo/livePhotoView";
                    Log.e("livePhotoView", url);
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
                                        try {
                                            JSONObject obj = new JSONObject(result);
                                            if (!obj.isNull("result")) {
                                                JSONObject itemObj = obj.getJSONObject("result");
                                                if (!itemObj.isNull("id")) {
                                                    mInfo.itemId = itemObj.getString("id");
                                                }
                                                if (!itemObj.isNull("imageUrl")) {
                                                    mInfo.thumbnailUrl = itemObj.getString("imageUrl");
                                                }
                                                if (!itemObj.isNull("browseNum")) {
                                                    mInfo.browsenum = itemObj.getString("browseNum");
                                                }
                                                if (!itemObj.isNull("address")) {
                                                    mInfo.address = itemObj.getString("address");
                                                }
                                                if (!itemObj.isNull("nickName")) {
                                                    mInfo.nickName = itemObj.getString("nickName");
                                                }
                                                if (!itemObj.isNull("des")) {
                                                    mInfo.des = itemObj.getString("des");
                                                }
                                                if (!itemObj.isNull("likeNum")) {
                                                    mInfo.praise = itemObj.getString("likeNum");
                                                }
                                                if (!itemObj.isNull("shootTime")) {
                                                    mInfo.date_time = itemObj.getString("shootTime");
                                                }
                                                if (!itemObj.isNull("weather")) {
                                                    mInfo.weather = itemObj.getString("weather");
                                                }
                                            }
                                            showDetail();
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
