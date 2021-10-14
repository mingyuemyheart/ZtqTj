package com.pcs.ztqtj.view.activity.life.expert_interpretation;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackShareAboutDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.expert.PackExpertDetailTalkCommitDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.expert.PackExpertDetailTalkCommitUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.expert.PackExpertDetailTalkDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.expert.PackExpertDetailTalkUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.expert.PackExpertDetailUp;
import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.AdapterExpertTalk;
import com.pcs.ztqtj.control.tool.ShareTools;
import com.pcs.ztqtj.control.tool.ZtqImageTool;
import com.pcs.ztqtj.control.tool.image.ImageLoader;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.view.activity.FragmentActivitySZYBBase;
import com.pcs.ztqtj.view.activity.photoshow.ActivityLogin;
import com.pcs.ztqtj.view.myview.MyListView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * 生活气象-专家解读-详情
 */
public class ActivityExpertDetail extends FragmentActivitySZYBBase {
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    private ImageView bigImageView;
    private ImageLoader mImageLoader;
    private TextView TextView1;
    private TextView textTitle;
    private TextView textTime;
    private String id = "";
    private MyListView user_talk;
    private List<PackExpertDetailTalkDown.ItemTalk> talkListData;

    private ScrollView scrollview;

    private Button btn_commit;
    private EditText my_talk;

    private int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expert_detail);
        PcsDataBrocastReceiver.registerReceiver(this, mReceiver);
        initView();
        initData();
        initEvent();
    }

    private void initView() {
        scrollview = (ScrollView) findViewById(R.id.scrollview);
        TextView1 = (TextView) findViewById(R.id.TextView1);

        my_talk = (EditText) findViewById(R.id.my_talk);
        btn_commit = (Button) findViewById(R.id.btn_commit);

        textTitle = (TextView) findViewById(R.id.text_content_title);
        textTime = (TextView) findViewById(R.id.text_time);
        bigImageView = (ImageView) findViewById(R.id.item_image);
        user_talk = (MyListView) findViewById(R.id.user_talk);
    }

    private void initData() {
        id = getIntent().getStringExtra("id");
        mImageLoader = new ImageLoader(ActivityExpertDetail.this);
        setTitleText("专家解读");
        talkListData = new ArrayList<>();
        adatperTalk = new AdapterExpertTalk(talkListData);
        user_talk.setAdapter(adatperTalk);
        commitUp = new PackExpertDetailTalkCommitUp();
        initUser();
        reqData();
    }

    private void initUser() {
        if (ZtqCityDB.getInstance().isLoginService()) {
            logged();
        } else {
            notLogged();
        }
    }

    /**
     * 已登录处理
     */
    private void logged() {
        btn_commit.setText("提交");
    }

    /**
     * 未登录处理
     */
    private void notLogged() {
        btn_commit.setText("登录");
    }


    private PackExpertDetailUp expertDetailUp;
    private PackExpertDetailTalkUp expertTalkUp;
    private AdapterExpertTalk adatperTalk;
    private PackExpertDetailTalkCommitUp commitUp;

    private void reqData() {
//        showProgressDialog();
//        expertTalkUp = new PackExpertDetailTalkUp();
//        expertTalkUp.id = id;
//        PcsDataDownload.addDownload(expertTalkUp);
//        expertDetailUp = new PackExpertDetailUp();
//        expertDetailUp.id = id;
//        PcsDataDownload.addDownload(expertDetailUp);

        reflushView();
    }

    private void commitInfo() {
        if (ZtqCityDB.getInstance().isLoginService()) {
            String content = my_talk.getText().toString().trim();
            if (TextUtils.isEmpty(content)) {
                showToast("评论内容不能为空。");
            } else {
                commitUp.zx_id = id;
                commitUp.user_id = MyApplication.UID;
                commitUp.content = content;
                showProgressDialog();
                PcsDataDownload.addDownload(commitUp);
            }
        } else {
//            跳转到登录界面
            Intent intent = new Intent(ActivityExpertDetail.this, ActivityLogin.class);
            startActivityForResult(intent, GOTOLOGIN);
        }
    }

    /**
     * 登录跳转request code
     */
    private static final int GOTOLOGIN = 1001;

    private boolean isBottom = false;
    private boolean isMoveFirstPoint = true;
    private float firstPoint = 0;

    private void initEvent() {
        setBtnRight(R.drawable.icon_share_new, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PackShareAboutDown down = (PackShareAboutDown) PcsDataManager.getInstance().getNetPack("wt_share#ABOUT_QXCP_DXFW");
                if (down == null) {
                    return;
                }
                String share_content = "";
                if (!TextUtils.isEmpty(textTitle.getText().toString())) {
                    share_content =  " 《" + textTitle.getText().toString() + "》 " + down.share_content;
                } else {
                    share_content =  down.share_content;
                }
                View layout = findViewById(R.id.scrollview);
                Bitmap headBitmap = ZtqImageTool.getInstance().getScreenBitmap(headLayout);
                Bitmap shareBitmap = ZtqImageTool.getInstance().getScreenBitmap(layout);
                shareBitmap = ZtqImageTool.getInstance().stitch(headBitmap, shareBitmap);
                shareBitmap = ZtqImageTool.getInstance().stitchQR(ActivityExpertDetail.this, shareBitmap);
                ShareTools.getInstance(ActivityExpertDetail.this).setShareContent(getTitleText(),share_content, shareBitmap,"0").showWindow(layout);
            }
        });
        btn_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commitInfo();
            }
        });
//        scrollview.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                View childView = scrollview.getChildAt(0);
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_MOVE:
//                        if (isMoveFirstPoint) {
//                            isMoveFirstPoint = false;
//                            firstPoint = event.getY();
//                        }
//                        if (scrollview.getScrollY() == (childView.getHeight() - scrollview.getHeight())) {
//                            //滑动到底部了
//                            isBottom = true;
//                            if (firstPoint - event.getY() > 10) {
//                                getMoreTalk();
//                            }
////                            showToast("滑动到底部");
//                        } else {
//                            isBottom = false;
//                        }
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        isMoveFirstPoint = true;
//                        break;
//                }
//                return false;
//            }
//        });
    }

    private Boolean inGetData = false;

    private void getMoreTalk() {
        if (page != -1 && !inGetData) {
            inGetData = true;
//            showToast("加载更多");
            expertTalkUp.page = page;
            PcsDataDownload.addDownload(expertTalkUp);
            if (page == 1) {
                talkListData.clear();
                adatperTalk.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case GOTOLOGIN:
                    initUser();
                    break;
            }
        }
    }

    private void reflushView() {
        String title = getIntent().getStringExtra("title");
        String big_img = getIntent().getStringExtra("big_img");
        String desc = getIntent().getStringExtra("desc");
        String release_time = getIntent().getStringExtra("release_time");

        if (!TextUtils.isEmpty(title)) {
            textTitle.setText(title);
        }

        String txt = "暂无数据";
        if (!TextUtils.isEmpty(desc)) {
            txt = desc;
        }
        String str = txt.replace("\r", "\n\r");
        TextView1.setText(str);

        if (!TextUtils.isEmpty(release_time)) {
            textTime.setText(release_time);
        }

        if (TextUtils.isEmpty(big_img) || "null".equals(big_img)) {
            Log.d("info.big_ico", "大图为空");
            bigImageView.setImageResource(R.drawable.no_pic);
        } else {
            String big_ico = getString(R.string.msyb) + big_img;
            Bitmap bitmap = null;
            bitmap = mImageLoader.getBitmapFromCache(big_ico);
            if (bitmap != null) {
                bigImageView.setImageBitmap(bitmap);
            } else {
                Picasso.get().load(big_ico).into(bigImageView);
            }
        }

    }

    private PcsDataBrocastReceiver mReceiver = new PcsDataBrocastReceiver() {
        @Override
        public void onReceive(String nameStr, String errorStr) {
            if (TextUtils.isEmpty(nameStr) || !TextUtils.isEmpty(errorStr)) {
                return;
            }
            if (expertDetailUp != null && expertDetailUp.getName().equals(nameStr)) {
//                详情
                reflushView();
            } else if (expertTalkUp != null && expertTalkUp.getName().equals(nameStr)) {
                //评论列表
                reflushList();
            } else if (commitUp != null && commitUp.getName().equals(nameStr)) {
//                评论提交
                dismissProgressDialog();
                PackExpertDetailTalkCommitDown commitDown = (PackExpertDetailTalkCommitDown) PcsDataManager.getInstance().getNetPack(commitUp.getName());
                if (commitDown == null) {
                    return;
                }
                if (commitDown.result.equals("1")) {
                    my_talk.setText("");
                    page = 1;
                }
                showToast(commitDown.result_msg + "");
            }
        }

    };

    private void reflushList() {
//        刷新评论列表
        inGetData = false;
        PackExpertDetailTalkDown down = (PackExpertDetailTalkDown) PcsDataManager.getInstance().getNetPack(expertTalkUp.getName());
        if (down != null) {
            talkListData.addAll(down.dataList);
            if (down.dataList.size() != expertTalkUp.count) {
                page = -1;
            } else {
                page++;
            }
            adatperTalk.notifyDataSetChanged();
        }
    }
}
