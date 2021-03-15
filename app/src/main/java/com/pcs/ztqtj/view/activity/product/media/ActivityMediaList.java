package com.pcs.ztqtj.view.activity.product.media;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib.lib_pcs_v3.model.image.ImageConstant;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityMain;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackBannerDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackBannerUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.media.MediaInfo;
import com.pcs.lib_ztqfj_v2.model.pack.net.media.PackMediaListDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.media.PackMediaListDown.ParentMedia;
import com.pcs.lib_ztqfj_v2.model.pack.net.media.PackMediaListUp;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.AdapterControlMainRow8;
import com.pcs.ztqtj.control.adapter.media.AdapterMediaGridView;
import com.pcs.ztqtj.control.adapter.media.AdapterMediaList;
import com.pcs.ztqtj.control.inter.ImageClick;
import com.pcs.ztqtj.control.tool.Utils;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
import com.pcs.ztqtj.view.activity.web.webview.ActivityWebView;
import com.pcs.ztqtj.view.dialog.DialogTwoButton;
import com.pcs.ztqtj.view.myview.LeadPoint;
import com.pcs.ztqtj.view.myview.MyGridView;

import java.util.ArrayList;
import java.util.List;

/**
 * 气象影视
 *
 * @author chenjh
 */
public class ActivityMediaList extends FragmentActivityZtqBase {
    private PackMediaListUp upPack = new PackMediaListUp();
    private MyReceiver receiver = new MyReceiver();
    private MyGridView myGridView;
    private AdapterMediaGridView myGridViewAdapter;
    /**
     * 服务器下载的源数据
     */
    public List<ParentMedia> mediaParentList;
    private ArrayList<MediaInfo> gridData;

    /**
     * 第一条数据
     */
    private MediaInfo topItemInfo;

    // 第一条数据
    private ImageView item_image;
    private TextView item_text;

    // 栏目切换
    private RadioGroup radioGroup;

    private RelativeLayout banner_layout;
    private TextView null_data;

    //广告
    private RelativeLayout item_top_layout;
    private AdapterControlMainRow8 adapter;
    private ViewPager vp = null;
    private int pagerCurrentPosition = 0;
    private LeadPoint pointlayout;
    private List<String> listBanner = new ArrayList<String>();

    private ListView lv_media;
    private AdapterMediaList adapterMediaList;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.medialist);
        setTitleText("气象影视");
        createImageFetcher();
        initView();
        initData();
        initEvent();
    }

    private void initView() {
        myGridView = (MyGridView) findViewById(R.id.my_gridview);
        item_image = (ImageView) findViewById(R.id.item_image);
        item_text = (TextView) findViewById(R.id.item_text);
        null_data = (TextView) findViewById(R.id.null_data);
        // 栏目切换
        radioGroup = (RadioGroup) findViewById(R.id.tab_content);
        item_top_layout = (RelativeLayout) findViewById(R.id.item_top_layout);
        banner_layout = (RelativeLayout) findViewById(R.id.banner_layout);

        vp = (ViewPager) findViewById(R.id.viewpager);
        pointlayout = (LeadPoint) findViewById(R.id.pointlayout);

        lv_media = (ListView) findViewById(R.id.lv_media);

    }

    private void initEvent() {
        myGridView.setOnItemClickListener(myGridItemClickListener);
        lv_media.setOnItemClickListener(myListItemClickListener);
        item_image.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (topItemInfo != null) {
                    if(Utils.isNotFastClick()){
                        //你的代码
                        toMedioDetail(topItemInfo);
                    }
                }
            }
        });
        radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                reflushUI(checkedId - 10);
            }
        });

        vp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
                pagerCurrentPosition = arg0;
                if (listBanner.size() > 1) {
                    pointlayout.setPointSelect(pagerCurrentPosition
                            % listBanner.size());
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
                        if (brannerHandler != null) {
                            brannerHandler.removeMessages(0);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (listBanner != null && listBanner.size() > 1) {
                            // 如果广告小于等于一个的话这不跳转播放
                            moveToNextPager();
                        }
                        break;
                }
                return false;
            }
        });

    }

    private Handler brannerHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    brannerHandler.removeMessages(0);
                    if (vp.getVisibility() == View.VISIBLE) {
                        vp.setCurrentItem(pagerCurrentPosition + 1);
                        moveToNextPager();
                    }
                    break;
            }
            return false;
        }
    });

    private void moveToNextPager() {
        brannerHandler.removeMessages(0);
        brannerHandler.sendEmptyMessageDelayed(0, 3000);
    }

    private PackBannerUp mPackBannerUp = new PackBannerUp();
    private PackBannerDown mPackBannerDown;

    private void initData() {
//        初始化广告栏目
        mPackBannerUp.position_id = "26";
        mPackBannerDown = (PackBannerDown) PcsDataManager.getInstance().getNetPack(mPackBannerUp.getName());
        if (mPackBannerDown != null) {
            // 当数据不为空时
            for (int i = 0; i < mPackBannerDown.arrBannerInfo.size(); i++) {
                listBanner.add(getString(R.string.file_download_url)
                        + mPackBannerDown.arrBannerInfo.get(i).img_path);
            }
        }
        // 注册广播接收
        PcsDataBrocastReceiver.registerReceiver(this, receiver);
        mediaParentList = new ArrayList<ParentMedia>();
        gridData = new ArrayList<MediaInfo>();
        myGridViewAdapter = new AdapterMediaGridView(this, gridData, getImageFetcher());
        myGridView.setAdapter(myGridViewAdapter);
        adapterMediaList = new AdapterMediaList(this, gridData, getImageFetcher());
        lv_media.setAdapter(adapterMediaList);
        reqData();
    }

    private ImageClick imageClick = new ImageClick() {

        @Override
        public void itemClick(Object path) {
            // 图片的点击事件 path为点击的图片地址
            int position = 0;
            for (int i = 0; i < listBanner.size(); i++) {
                if (path.toString().equals(listBanner.get(i))) {
                    position = i;
                    break;
                }
            }
            String url = mPackBannerDown.arrBannerInfo.get(position).url;
            if (TextUtils.isEmpty(url)) {
                return;
            }
            Intent intent = new Intent(ActivityMediaList.this, ActivityWebView.class);
            intent.putExtra("title", mPackBannerDown.arrBannerInfo.get(position).title);
            intent.putExtra("shareContent", mPackBannerDown.arrBannerInfo.get(position).fx_content);
            intent.putExtra("url", url);
            startActivity(intent);
        }
    };

    /**
     * 请求气象影视数据
     */
    private void reqData() {
        if (!isOpenNet()) {
            showToast(getString(R.string.net_err));
            return;
        }
        showProgressDialog();
        PackLocalCityMain cityMain = ZtqCityDB.getInstance().getCityMain();
        if (cityMain == null) {
            return;
        }
        if (cityMain.isFjCity) {
            upPack.area_id = cityMain.ID;
        } else {
            upPack.area_id = "1279";
        }
        PcsDataDownload.addDownload(upPack);
    }

    /**
     * 数据更新广播接收
     *
     * @author JiangZy
     */
    private class MyReceiver extends PcsDataBrocastReceiver {
        @Override
        public void onReceive(String name, String error) {
            if (name.startsWith(upPack.getName())) {
                dismissProgressDialog();
                reqSuccess(name);
            }
        }
    }

    /**
     * 请求数据返回成功
     */
    private void reqSuccess(String name) {
        mediaParentList.clear();
        PackMediaListDown packMediaListDown = (PackMediaListDown) PcsDataManager.getInstance().getNetPack(name);
        if (packMediaListDown != null) {
            mediaParentList.addAll(packMediaListDown.mediaParentList);
        }
        reflashData();
    }

    /**
     * listview填充数据
     */
    private void reflashData() {
        if (mediaParentList.size() > 0) {
            reflushColumn();
        } else {
            myGridViewAdapter.notifyDataSetChanged();

        }
    }

    /**
     * 刷新栏目信息
     */
    private void reflushColumn() {
        int width = radioGroup.getWidth() / mediaParentList.size();
        int position = 0;
        radioGroup.removeAllViews();
        for (int i = 0; i < mediaParentList.size(); i++) {
            RadioButton rb = new RadioButton(this);
            rb.setText(mediaParentList.get(i).column_name);
            rb.setBackgroundResource(R.drawable.btn_warn_radiobutton_select);
            rb.setTextColor(getResources().getColor(R.color.text_black));
            rb.setButtonDrawable(getResources().getDrawable(
                    android.R.color.transparent));
            rb.setPadding(0, 0, 0, 0);
            rb.setGravity(Gravity.CENTER);
            rb.setSingleLine();
            rb.setId(i + 10);
            LayoutParams params = new LayoutParams(width,
                    LayoutParams.MATCH_PARENT);
            if ("1".equals(mediaParentList.get(i).is_first)) {
                rb.setChecked(true);
            }
            radioGroup.addView(rb, params);
        }
//        if(radioGroup.getChildCount() > 1) {
//            View button = radioGroup.getChildAt(1);
//            if(button != null && button.getId() != -1) {
//                radioGroup.check(button.getId());
//            }
//        }
    }

    private void reflushUI(int item) {
        ParentMedia parent = mediaParentList.get(item);
        if (parent.column_name.equals("品牌传播视频")) {
            isBrandMovice(true, parent, item);
        } else {
            isBrandMovice(false, parent, item);
        }
    }

    /*选中的栏目是否为品牌栏目 是为true*/
    private void isBrandMovice(boolean isBrand, ParentMedia parent, int item) {
        if (isBrand) {

            //第一行大图显示的为广告栏目，
            if (parent.video_list.size() > 0) {
                isDataNull(false);
                gridData.clear();
                gridData.addAll(parent.video_list);
                myGridViewAdapter.notifyDataSetChanged();
//                item_image.setImageResource(R.drawable.ic_launcher);
//                String imgUrl = getString(R.string.file_download_url)+ topItemInfo.imageurl;
                item_text.setText("");
//                getImageFetcher().loadImage(imgUrl, item_image, ImageConstant.ImageShowType.SRC);
            } else {
                isDataNull(true);
            }
            //出事话广告栏目
            if (listBanner.size() > 0) {
                banner_layout.setVisibility(View.VISIBLE);
                adapter = new AdapterControlMainRow8(listBanner, imageClick, getImageFetcher());
                vp.setAdapter(adapter);
            } else {
                banner_layout.setVisibility(View.GONE);
            }
            item_top_layout.setVisibility(View.GONE);

        } else {
            banner_layout.setVisibility(View.GONE);
            if (item == 0) {
                item_top_layout.setVisibility(View.GONE);
                myGridView.setVisibility(View.GONE);
                lv_media.setVisibility(View.VISIBLE);
                if (parent.video_list.size() > 0) {
                    isDataNull(false);
                    gridData.clear();
                    gridData.addAll(parent.video_list);
                    adapterMediaList.notifyDataSetChanged();
                } else {
                    isDataNull(true);
                }
            } else {
                item_top_layout.setVisibility(View.VISIBLE);
                myGridView.setVisibility(View.VISIBLE);
                lv_media.setVisibility(View.GONE);
//            所有的都是影视信息
                if (parent.video_list.size() > 0) {
                    isDataNull(false);
                    gridData.clear();
                    gridData.addAll(parent.video_list);
                    gridData.remove(0);
                    topItemInfo = parent.video_list.get(0);
                    myGridViewAdapter.notifyDataSetChanged();
                    String imgUrl = getString(R.string.file_download_url) + topItemInfo.imageurl;
                    item_text.setText(topItemInfo.title);
                    getImageFetcher().loadImage(imgUrl, item_image, ImageConstant.ImageShowType.SRC);
                } else {
                    isDataNull(true);
                }
            }


        }
    }


    /*输入是否为空。是为true*/
    private void isDataNull(boolean isNull) {
        if (isNull) {
            gridData.clear();
            myGridViewAdapter.notifyDataSetChanged();
            null_data.setVisibility(View.VISIBLE);
        } else {
            null_data.setVisibility(View.GONE);
        }
    }


    private OnItemClickListener myGridItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view,
                                int position, long arg3) {
            if(Utils.isNotFastClick()){
                //你的代码
                toMedioDetail(gridData.get(position));
            }
        }

    };

    private OnItemClickListener myListItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view,
                                int position, long arg3) {
            if(Utils.isNotFastClick()){
                //你的代码
                toMedioDetail(gridData.get(position));
            }
        }

    };



    private void toMedioDetail(MediaInfo mediaInfo) {
        this.mediaInfo = mediaInfo;
        toDetail();
    }


    private MediaInfo mediaInfo;

    private void toDetail() {
        Intent intent = new Intent(ActivityMediaList.this,
                ActivityMediaPlay.class);
        intent.putExtra("mediaInfo", mediaInfo);
        startActivity(intent);
    }

    private DialogTwoButton dialogRemain;


    @Override
    public void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(receiver);
    }

}
