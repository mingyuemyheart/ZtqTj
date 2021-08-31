package com.pcs.ztqtj.view.activity.product.media;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.Gravity;
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
import android.widget.TextView;

import com.pcs.lib.lib_pcs_v3.model.image.ImageConstant;
import com.pcs.lib_ztqfj_v2.model.pack.net.media.MediaInfo;
import com.pcs.lib_ztqfj_v2.model.pack.net.media.PackMediaListDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.media.PackMediaListDown.ParentMedia;
import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.media.AdapterMediaGrid;
import com.pcs.ztqtj.control.adapter.media.AdapterMediaList;
import com.pcs.ztqtj.control.tool.Utils;
import com.pcs.ztqtj.control.tool.utils.TextUtil;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.util.OkHttpUtil;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
import com.pcs.ztqtj.view.myview.MyGridView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 生活气象-气象影视
 */
public class ActivityMediaList extends FragmentActivityZtqBase {

    // 栏目切换
    private RadioGroup radioGroup;
    private TextView null_data;

    public ArrayList<ParentMedia> mediaParentList;
    private ArrayList<MediaInfo> gridData;

    private ListView lv_media;
    private AdapterMediaList adapterMediaList;

    private ConstraintLayout item_top_layout;
    private MyGridView myGridView;
    private AdapterMediaGrid myGridViewAdapter;

    /**
     * 第一条数据
     */
    private MediaInfo topItemInfo;
    private ImageView item_image;
    private TextView item_text;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_media_list);
        String title = getIntent().getStringExtra("title");
        if (title != null) {
            setTitleText(title);
        }
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
        radioGroup = (RadioGroup) findViewById(R.id.tab_content);
        item_top_layout = findViewById(R.id.item_top_layout);
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
    }

    private void initData() {
        // 注册广播接收
        mediaParentList = new ArrayList<>();
        gridData = new ArrayList<>();
        myGridViewAdapter = new AdapterMediaGrid(this, gridData, getImageFetcher());
        myGridView.setAdapter(myGridViewAdapter);
        adapterMediaList = new AdapterMediaList(this, gridData, getImageFetcher());
        lv_media.setAdapter(adapterMediaList);

        okHttpList();
    }

    private void reflushUI(int item) {
        ParentMedia parent = mediaParentList.get(item);
        if (parent.column_name.equals("品牌视频")) {
            isBrandMovice(true, parent, item);
        } else {
            isBrandMovice(false, parent, item);
        }
    }

    /*选中的栏目是否为品牌栏目 是为true*/
    private void isBrandMovice(boolean isBrand, ParentMedia parent, int item) {
        if (isBrand) {
            item_top_layout.setVisibility(View.VISIBLE);
            myGridView.setVisibility(View.VISIBLE);
            lv_media.setVisibility(View.GONE);
            if (parent.video_list.size() > 0) {
                isDataNull(false);
                gridData.clear();
                gridData.addAll(parent.video_list);
                gridData.remove(0);
                topItemInfo = parent.video_list.get(0);
                myGridViewAdapter.notifyDataSetChanged();
                String imgUrl = getString(R.string.msyb) + topItemInfo.imageurl;
                item_text.setText(topItemInfo.title);
                getImageFetcher().loadImage(imgUrl, item_image, ImageConstant.ImageShowType.SRC);
            } else {
                isDataNull(true);
            }

        } else {
            myGridView.setVisibility(View.GONE);
            lv_media.setVisibility(View.VISIBLE);
            item_top_layout.setVisibility(View.GONE);
            if (parent.video_list.size() > 0) {
                isDataNull(false);
                gridData.clear();
                gridData.addAll(parent.video_list);
                adapterMediaList.notifyDataSetChanged();
            } else {
                isDataNull(true);
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
        Intent intent = new Intent(ActivityMediaList.this, ActivityMediaPlay.class);
        intent.putExtra("mediaInfo", mediaInfo);
        startActivity(intent);
    }

    /**
     * 获取气象影视列表数据
     */
    private void okHttpList() {
        showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    String json = param.toString();
                    final String url = CONST.BASE_URL+"qxmedia";
                    Log.e("qxmedia", url);
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
                                    dismissProgressDialog();
                                    if (!TextUtil.isEmpty(result)) {
                                        try {
                                            JSONObject obj = new JSONObject(result);
                                            if (!obj.isNull("b")) {
                                                JSONObject bobj = obj.getJSONObject("b");
                                                if (!bobj.isNull("wt_video")) {
                                                    JSONObject wt_video = bobj.getJSONObject("wt_video");
                                                    mediaParentList.clear();
                                                    PackMediaListDown packMediaListDown = new PackMediaListDown();
                                                    packMediaListDown.fillData(wt_video.toString());
                                                    if (packMediaListDown != null) {
                                                        mediaParentList.addAll(packMediaListDown.mediaParentList);
                                                    }
                                                    if (mediaParentList.size() > 0) {
                                                        int width = radioGroup.getWidth() / mediaParentList.size();
                                                        radioGroup.removeAllViews();
                                                        for (int i = 0; i < mediaParentList.size(); i++) {
                                                            RadioButton rb = new RadioButton(ActivityMediaList.this);
                                                            rb.setText(mediaParentList.get(i).column_name);
                                                            rb.setBackgroundResource(R.drawable.btn_warn_radiobutton_select);
                                                            rb.setTextColor(getResources().getColor(R.color.text_black));
                                                            rb.setButtonDrawable(getResources().getDrawable(android.R.color.transparent));
                                                            rb.setPadding(0, 0, 0, 0);
                                                            rb.setGravity(Gravity.CENTER);
                                                            rb.setSingleLine();
                                                            rb.setId(i + 10);
                                                            LayoutParams params = new LayoutParams(width, LayoutParams.MATCH_PARENT);
                                                            if ("1".equals(mediaParentList.get(i).is_first)) {
                                                                rb.setChecked(true);
                                                            }
                                                            radioGroup.addView(rb, params);
                                                        }
                                                    }

                                                    if (adapterMediaList != null) {
                                                        adapterMediaList.notifyDataSetChanged();
                                                    }
                                                    if (myGridViewAdapter != null) {
                                                        myGridViewAdapter.notifyDataSetChanged();
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
