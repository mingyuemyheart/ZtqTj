package com.pcs.ztqtj.view.activity.livequery;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.livequery.AdapterData;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
import com.pcs.ztqtj.view.myview.ImageTouchView;
import com.pcs.lib.lib_pcs_v3.control.file.PcsGetPathValue;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib.lib_pcs_v3.model.image.ImageConstant;
import com.pcs.lib.lib_pcs_v3.model.image.ListenerImageLoad;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackYltjImgDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.livequery.PackYltjImgUp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Z 降雨量分布图
 */
public class RainFallImage extends FragmentActivityZtqBase implements
        OnClickListener {
    private ImageTouchView imageview_show;
    private TextView like_spinner;
    private PackYltjImgDown imageDown;
    private String sdFilePath;
    private List<String> dataeaum;
    private ImageButton image_left;
    private ImageButton image_right;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitleText("福建省雨量实况分布图");
        setContentView(R.layout.activity_rainfall_image);
        createImageFetcher();
        initView();
        initEvent();
        initData();
    }

    private void initEvent() {
        like_spinner.setOnClickListener(this);
        image_right.setOnClickListener(this);
        image_left.setOnClickListener(this);
    }

    private String imageKey = "";

    /**
     * 下载图片并显示
     */
    private void getImageView(final int floag) {
        showProgressDialog();
        if (!TextUtils.isEmpty(imageDown.datalist.get(floag).img)) {
            SimpleDateFormat sf = new SimpleDateFormat("hh:MM:SS");

            imageKey = getString(R.string.file_download_url)
                    + imageDown.datalist.get(floag).img;
            getImageFetcher().addListener(mImageListener);
            getImageFetcher().loadImage(imageKey, null, ImageConstant.ImageShowType.NONE);
        } else {
            showToast("服务器不存在这张图标");
        }
    }

    private void initData() {
        sdFilePath = PcsGetPathValue.getInstance().getImagePath()
                + "livequeryrainfallimage/";
        dataeaum = new ArrayList<String>();
        imageDown = new PackYltjImgDown();

        PackYltjImgUp up = new PackYltjImgUp();

        imageDown = (PackYltjImgDown) PcsDataManager.getInstance().getNetPack(PackYltjImgUp.NAME);
        if (imageDown == null) {
            // 数据不存在
        } else {

            dataeaum.clear();
            for (int i = 0; i < imageDown.datalist.size(); i++) {
                dataeaum.add(imageDown.datalist.get(i).channel_name);
            }
            if (imageDown.datalist == null) {

            } else {
                if (imageDown.datalist.size() > 0) {
                    like_spinner.setText(dataeaum.get(0));
                    new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            super.handleMessage(msg);
                            showProgressDialog();
                            getImageView(0);
                        }
                    }.sendMessageDelayed(new Message(), 150);
                }
            }
        }
    }

    /**
     * 初始化视图
     */
    private void initView() {
        imageview_show = (ImageTouchView) findViewById(R.id.imageview_show);
        imageview_show.setHightFillScale(true);
        like_spinner = (TextView) findViewById(R.id.like_spinner);
        image_left = (ImageButton) findViewById(R.id.image_left);
        image_right = (ImageButton) findViewById(R.id.image_right);
    }

    /**
     * 创建下拉选择列表
     */
    public PopupWindow createPopupWindow(final TextView dropDownView,
                                         final List<String> dataeaum) {
        AdapterData dataAdapter = new AdapterData(RainFallImage.this, dataeaum);
        View popcontent = LayoutInflater.from(RainFallImage.this).inflate(
                R.layout.pop_list_layout, null);
        ListView lv = (ListView) popcontent.findViewById(R.id.mylistviw);
        lv.setAdapter(dataAdapter);
        final PopupWindow pop = new PopupWindow(RainFallImage.this);
        pop.setContentView(popcontent);
        pop.setOutsideTouchable(false);
        pop.setWidth((int) (dropDownView.getWidth()));
        pop.setHeight(LayoutParams.WRAP_CONTENT);
        pop.setFocusable(true);
        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                pop.dismiss();
                showposition = position;
                dropDownView.setText(dataeaum.get(position));
                getImageView(position);
            }
        });
        return pop;
    }

    private int showposition = 0;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.like_spinner:
                createPopupWindow(like_spinner, dataeaum).showAsDropDown(
                        like_spinner);
                break;
            case R.id.image_left:
                if (dataeaum.size() > 0) {
                    if (showposition == 0) {
                        showposition = dataeaum.size() - 1;
                    } else {
                        showposition--;
                    }
                    changeValue();
                }

                break;
            case R.id.image_right:
                if (dataeaum.size() > 0) {
                    if (showposition == dataeaum.size() - 1) {
                        showposition = 0;
                    } else {
                        showposition++;
                    }
                    changeValue();
                }
                break;
        }
    }

    private void changeValue() {
        like_spinner.setText(dataeaum.get(showposition));
        getImageView(showposition);
    }

    private ListenerImageLoad mImageListener = new ListenerImageLoad() {

        @Override
        public void done(String key, boolean isSucc) {
            if (imageKey.equals(key)) {
                dismissProgressDialog();
                SimpleDateFormat sf = new SimpleDateFormat("hh:MM:SS");
                if (isSucc) {
                    dismissProgressDialog();
                    if (getImageFetcher().getImageCache() == null) {
                        showToast("图片为空");
                        return;
                    }
                    Bitmap bm = getImageFetcher().getImageCache()
                            .getBitmapFromAllCache(key).getBitmap();
                    imageview_show.setMyImageBitmap(bm);
                } else {
                    showToast("图片为空");
                }
            }
        }
    };
}
