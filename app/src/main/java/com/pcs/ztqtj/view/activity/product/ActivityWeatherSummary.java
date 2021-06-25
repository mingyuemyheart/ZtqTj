package com.pcs.ztqtj.view.activity.product;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackShareAboutDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackShareAboutUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackWeatherSummaryDown;
import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.ShareTools;
import com.pcs.ztqtj.control.tool.ZtqImageTool;
import com.pcs.ztqtj.control.tool.utils.TextUtil;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.util.OkHttpUtil;
import com.pcs.ztqtj.view.activity.FragmentActivitySZYBBase;

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
 * 监测预报-气象报告
 */
public class ActivityWeatherSummary extends FragmentActivitySZYBBase {

    private TextView content;
    private TextView null_context, tv_time;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.weather_summary);
        setTitleText("气象报告");
        initView();
        initEvent();
    }

    private void initView() {
        content = findViewById(R.id.tv_qxb_content);
        null_context = findViewById(R.id.null_context);
        tv_time = findViewById(R.id.tv_qxbg_time);

        okHttpContent();
    }

    private void initEvent() {
        setBtnRight(R.drawable.icon_share_new, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View layout = findViewById(R.id.layout);
                layout.measure(View.MeasureSpec.makeMeasureSpec(layout.getWidth(), View.MeasureSpec.AT_MOST),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                // layout高度
                int height = layout.getMeasuredHeight();

                Bitmap shareBitmap;
                DisplayMetrics metrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metrics);
                // 屏幕高度
                int screenHeight = metrics.heightPixels;

                if (height < screenHeight) {
                    // 截取全屏
                    shareBitmap = ZtqImageTool.getInstance().getScreenBitmapNew(ActivityWeatherSummary.this);
                } else {
                    // 截图整个layout
                    Bitmap headBitmap = ZtqImageTool.getInstance().getScreenBitmap(headLayout);
                    shareBitmap = ZtqImageTool.getInstance().getScreenBitmap(layout);
                    shareBitmap = ZtqImageTool.getInstance().stitch(headBitmap, shareBitmap);

                }
                shareBitmap = ZtqImageTool.getInstance().stitchQR(ActivityWeatherSummary.this, shareBitmap);
                PackShareAboutDown shareDown = (PackShareAboutDown) PcsDataManager.getInstance().getNetPack
                        (PackShareAboutUp.getNameCom());
                String shareContent = "";
                if (shareDown != null) {
                    shareContent = shareDown.share_content;
                }
                ShareTools.getInstance(ActivityWeatherSummary.this).setShareContent(getTitleText(),
                        shareContent, shareBitmap, "0").showWindow(layout);
            }
        });
    }

    /**
     * 获取气象报告数据
     */
    private void okHttpContent() {
        showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject param  = new JSONObject();
                    param.put("token", MyApplication.TOKEN);
                    String json = param.toString();
                    final String url = CONST.BASE_URL+"qxbg";
                    Log.e("qxbg", url);
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
                                    Log.e("qxbg", result);
                                    if (!TextUtil.isEmpty(result)) {
                                        try {
                                            JSONObject obj = new JSONObject(result);
                                            if (!obj.isNull("b")) {
                                                JSONObject bobj = obj.getJSONObject("b");
                                                if (!bobj.isNull("qxbg")) {
                                                    JSONObject listobj = bobj.getJSONObject("qxbg");
                                                    if (!TextUtil.isEmpty(listobj.toString())) {
                                                        dismissProgressDialog();
                                                        PackWeatherSummaryDown weatherDown = new PackWeatherSummaryDown();
                                                        weatherDown.fillData(listobj.toString());
                                                        if (TextUtils.isEmpty(weatherDown.pub_time) && TextUtils.isEmpty(weatherDown.txt)) {
                                                            tv_time.setVisibility(View.GONE);
                                                            content.setVisibility(View.GONE);
                                                            null_context.setVisibility(View.VISIBLE);
                                                        } else {
                                                            tv_time.setVisibility(View.VISIBLE);
                                                            content.setVisibility(View.VISIBLE);
                                                            null_context.setVisibility(View.GONE);
                                                            tv_time.setText(weatherDown.pub_time);
                                                            content.setText(weatherDown.txt);
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
