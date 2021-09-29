package com.pcs.ztqtj.view.activity.lifenumber;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityMain;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackLifeNumberDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackLifeNumberDown.LifeNumber;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackLifeNumberUp;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.ShareTools;
import com.pcs.ztqtj.control.tool.ZtqImageTool;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * 生活指数详情
 * @author JiangZy
 */
public class ActivityLifeNumberDetail extends FragmentActivityZtqBase {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 向右退出
        setBackDirection(BackDirection.BACK_RIGHT);
        String id = getIntent().getStringExtra("key");
        PackLocalCityMain cityMain = ZtqCityDB.getInstance().getCityMain();
        String area = cityMain.ID;
        PackLifeNumberDown mDownPack = (PackLifeNumberDown) PcsDataManager.getInstance().getNetPack(PackLifeNumberUp.NAME + "#" + area);

        if (mDownPack == null) {
            return;
        }
        LifeNumber pack = mDownPack.dataMap.get(id);
        if (pack == null) {
            return;
        }
        // 标题
        setTitleText(pack.index_name + "指数");

        if (TextUtils.isEmpty(pack.create_time)) {
            shareC = cityMain.NAME + pack.des;
        } else {
            SimpleDateFormat format = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss.SSS");
            try {
                // 2015-05-05 16:08:47.0
                Date dDate = format.parse(pack.create_time);
                Calendar cc = Calendar.getInstance();
                cc.setTimeInMillis(dDate.getTime());

                shareC = pack.des;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        setContentView(R.layout.activity_life_number_detail);
        // 展示数据
        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setTextZoom(100);
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        webView.loadUrl(pack.shzs_url);

        setBtnRight(R.drawable.icon_share_new, new OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = shareC + CONST.SHARE_URL;
                LinearLayout all_view = (LinearLayout) findViewById(R.id.all_view);
                Bitmap shareBitmap = ZtqImageTool.getInstance().getScreenBitmap(all_view);
                shareBitmap = ZtqImageTool.getInstance().stitchQR(ActivityLifeNumberDetail.this, shareBitmap);
                ShareTools.getInstance(ActivityLifeNumberDetail.this).setShareContent(getTitleText(), content,
                        shareBitmap,"0").showWindow(all_view);
//              Bitmap shareBitmap = ZtqImageTool.getInstance().getScreenBitmap(all_view);
//                ShareUtil.share(ActivityLifeNumberDetail.this, shareC, shareBitmap);
            }
        });
    }

    private WebView webView;
    private String shareC = "";

    public String getWeek(Calendar c) {
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        String mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
        if ("1".equals(mWay)) {
            mWay = "天";
        } else if ("2".equals(mWay)) {
            mWay = "一";
        } else if ("3".equals(mWay)) {
            mWay = "二";
        } else if ("4".equals(mWay)) {
            mWay = "三";
        } else if ("5".equals(mWay)) {
            mWay = "四";
        } else if ("6".equals(mWay)) {
            mWay = "五";
        } else if ("7".equals(mWay)) {
            mWay = "六";
        }
        return " 星期" + mWay;
    }

}
