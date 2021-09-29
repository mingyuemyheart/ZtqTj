package com.pcs.ztqtj.view.activity.warn;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.Contacts;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.pcs.lib.lib_pcs_v3.control.tool.BitmapUtil;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.PackWarnPubDetailDown;
import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.MyConfigure;
import com.pcs.ztqtj.control.tool.ShareTools;
import com.pcs.ztqtj.control.tool.SharedPreferencesUtil;
import com.pcs.ztqtj.control.tool.ZtqImageTool;
import com.pcs.ztqtj.control.tool.utils.TextUtil;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.util.OkHttpUtil;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 预警详情
 */
public class ActivityWarnDetails extends FragmentActivityZtqBase {

    private ImageView contentInfo;
    private ImageButton shareButton;
    private TextView content;
    private TextView title_content;
    private TextView title_date;
    private TextView defense_guidelines;
    private String titlecontent;
    private String titledata = "";
    private String id = "";
    private String type="";
    private String contentText;
    private String shareContent;
    private String defend;
    private String icon;
    private String contentImageview;
    private ImageView icon_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warn_details);
        showProgressDialog();
        setTitleText("预警详情");
        Bundle bundle = getIntent().getBundleExtra(MyConfigure.EXTRA_BUNDLE);
        if(bundle != null) {
            icon = bundle.getString("i");
            id = bundle.getString("id", "");
            type=bundle.getString("type", "");
        }
        SharedPreferencesUtil.putData(id,id);

        initView();
        okHttpWarningDetail(id);
    }

    /**
     * 预警详情
     */
    private void okHttpWarningDetail(final String id) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param  = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    JSONObject info = new JSONObject();
                    info.put("stationId", id);
                    param.put("paramInfo", info);
                    String json = param.toString();
                    final String url = CONST.BASE_URL+"yjxx_info_query";
                    Log.e("yjxx_info_query", url);
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
                                        Log.e("yjxx_info_query", result);
                                    }
                                    try {
                                        JSONObject obj = new JSONObject(result);
                                        if (!obj.isNull("b")) {
                                            JSONObject bobj = obj.getJSONObject("b");
                                            if (!bobj.isNull("yjxx_info_query")) {
                                                JSONObject yjxx_info_query = bobj.getJSONObject("yjxx_info_query");
                                                if (!TextUtil.isEmpty(yjxx_info_query.toString())) {
                                                    dismissProgressDialog();
                                                    PackWarnPubDetailDown warnDown = new PackWarnPubDetailDown();
                                                    warnDown.fillData(yjxx_info_query.toString());
                                                    contentImageview = "";
                                                    titledata = warnDown.put_str;
                                                    titlecontent = warnDown.desc;
                                                    contentText = warnDown.content;
                                                    shareContent = titlecontent + "：" + contentText + "(" + titledata + ")" + CONST.SHARE_URL;
                                                    defend = warnDown.defend;
                                                    initData();
                                                }
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
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

    private void initData() {
        if (icon == null || icon.equals("")) {
            icon_title.setVisibility(View.GONE);
        } else {
            InputStream is = null;
            try {
                is = getResources().getAssets().open("img_warn/" + icon + ".png");
                Bitmap bm = BitmapFactory.decodeStream(is);
                bm = BitmapUtil.scaleBitmip(bm, 0.8f, 0.8f);
                is.close();
                icon_title.setImageBitmap(bm);
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException e1) {
                }
            }
        }
        try {
            title_date.setText(titledata);
            title_content.setText(titlecontent);
            content.setText(contentText);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (contentImageview.equals("")) {
            contentInfo.setVisibility(View.GONE);
        } else {
            contentInfo.setVisibility(View.VISIBLE);
        }

        try {
            // 预警指南
            if (titlecontent.contains("解除")) {

            } else {
                defense_guidelines.setText(defend);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    final String[] level = {"bb_O", "bb_R", "by_B", "by_O", "by_R", "by_Y", "df_O", "df_R", "df_Y", "dljb_O", "dljb_R", "dljb_Y", "dw_O", "dw_R", "dw_Y", "gh_O", "gh_R", "gw_O",
            "gw_R", "jw_B", "jw_O", "jw_R", "jw_Y", "ld_O", "ld_R", "ld_Y", "m_O", "m_Y", "sd_B", "sd_O", "sd_Y", "tf_B", "tf_O", "tf_R", "tf_Y", "xz_O", "xz_R", "xz_Y"};

    public void initView() {
        contentInfo = (ImageView) findViewById(R.id.image_info);
        icon_title = (ImageView) findViewById(R.id.c_icon);

        shareButton = (ImageButton) findViewById(R.id.warn_share);
        title_content = (TextView) findViewById(R.id.title_content);
        title_date = (TextView) findViewById(R.id.title_data);
        content = (TextView) findViewById(R.id.warn_content);
        defense_guidelines = (TextView) findViewById(R.id.defense_guidelines);

        setBtnRight(R.drawable.maillist_button, new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 通讯录
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Contacts.People.CONTENT_URI);
                startActivity(intent);
            }
        });
        shareButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                View layout = findViewById(R.id.layout);
                Bitmap bitmap = ZtqImageTool.getInstance().getScreenBitmap(layout);
                bitmap = ZtqImageTool.getInstance().stitchQR(ActivityWarnDetails.this, bitmap);
                ShareTools.getInstance(ActivityWarnDetails.this).setShareContent(getTitleText(),shareContent, bitmap, "1").showWindow(layout);
            }
        });
    }

}
