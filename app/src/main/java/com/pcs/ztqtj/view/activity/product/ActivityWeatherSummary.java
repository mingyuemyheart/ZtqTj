package com.pcs.ztqtj.view.activity.product;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackShareAboutDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackShareAboutUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackWeatherSummaryDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackWeatherSummaryUp;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.ShareTools;
import com.pcs.ztqtj.control.tool.ZtqImageTool;
import com.pcs.ztqtj.view.activity.FragmentActivitySZYBBase;

/**
 * 天气综述
 *
 * @author Z
 */
public class ActivityWeatherSummary extends FragmentActivitySZYBBase {
    private MyReceiver receiver = new MyReceiver();
    private TextView content;
    private PackWeatherSummaryUp weatherUp;
    private TextView null_context, tv_time;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        // 注册广播接收
        PcsDataBrocastReceiver.registerReceiver(ActivityWeatherSummary.this, receiver);
        setContentView(R.layout.weather_summary);
        setTitleText("气象报告");
        initView();
        initEvent();
        initData();
    }

    private void initView() {
        content = findViewById(R.id.tv_qxb_content);
        null_context = findViewById(R.id.null_context);
        tv_time = findViewById(R.id.tv_qxbg_time);
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

    private void initData() {
        if (!isOpenNet()) {
            showToast(getString(R.string.net_err));
            return;
        }
        showProgressDialog();
        weatherUp = new PackWeatherSummaryUp();
        weatherUp.type = "";
        PcsDataDownload.addDownload(weatherUp);
    }

    private class MyReceiver extends PcsDataBrocastReceiver {
        @Override
        public void onReceive(String name, String error) {
            if (weatherUp == null) {
                return;
            }
            if (name.equals(weatherUp.getName())) {
                dismissProgressDialog();
                PackWeatherSummaryDown weatherDown = (PackWeatherSummaryDown) PcsDataManager.getInstance().getNetPack
                        (name);
                if (weatherDown == null) {
                    return;
                }
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


//    /**
//     * 添加标题的单选按钮 动态添加radiobutton
//     *
//     * @param number_radio_group
//     */
//    @SuppressWarnings("ResourceType")
//    private void addRadioButton(RadioGroup number_radio_group, int countRadio) {
//        int width = Util.getScreenWidth(ActivityWeatherSummary.this);
//        int radioWidth = width / countRadio;
//        int pad = Util.dip2px(ActivityWeatherSummary.this, 10);
//        for (int i = 0; i < countRadio; i++) {
//            RadioButton radioButton = new RadioButton(
//                    ActivityWeatherSummary.this);
//            radioButton.setId(10 + i);
//            radioButton.setGravity(Gravity.CENTER);
//            radioButton.setTextColor(getResources()
//                    .getColor(R.color.text_black));
//            radioButton
//                    .setBackgroundResource(R.drawable.btn_warn_radiobutton_select);
//            radioButton.setPadding(0, pad, 0, pad);
//            radioButton.setButtonDrawable(getResources().getDrawable(
//                    android.R.color.transparent));
//            radioButton.setText(weatherList.get(i).title);
//            radioButton.setSingleLine(true);
//
//            if (i == 0) {
//                radioButton.setChecked(true);
//            }
//
//            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
//                    radioWidth, LayoutParams.WRAP_CONTENT);
//            number_radio_group.addView(radioButton, lp);
//        }
//        if (countRadio > 0) {
//            number_radio_group.check(10);
//        }
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(receiver);
    }


}
