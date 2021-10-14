package com.pcs.ztqtj.view.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackSubscriptionAccountDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackSubscriptionAccountUp;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.ShareTools;
import com.pcs.ztqtj.control.tool.ZtqImageTool;
import com.squareup.picasso.Picasso;

public class ActivityShWeather extends FragmentActivityZtqBase {

    private TextView tvTitle, tvAccount, tvDesc, tvUnit;
    private ImageView ivIcon, ivQr;
    private MyReceiver receiver = new MyReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitleText("天津天气");
        setContentView(R.layout.activity_subscription_account);
        initView();
        initEvent();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(receiver != null) {
            PcsDataBrocastReceiver.unregisterReceiver(this, receiver);
            receiver = null;
        }
    }

    private void initView() {
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvAccount = (TextView) findViewById(R.id.tv_account);
        tvDesc = (TextView) findViewById(R.id.tv_desc);
        tvUnit = (TextView) findViewById(R.id.tv_unit);
        ivIcon = (ImageView) findViewById(R.id.iv_icon);
        ivQr = (ImageView) findViewById(R.id.iv_qr);
    }

    private void initEvent() {
        setBtnRight(R.drawable.btn_share, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = ZtqImageTool.getInstance().getScreenBitmapNew(ActivityShWeather.this);
                View view = findViewById(R.id.layout);
                ShareTools.getInstance(ActivityShWeather.this)
                        .setShareContent(getTitleText(),"1", bitmap, "1")
                        .showWindow(view);
            }
        });
    }

    private void initData() {
        PcsDataBrocastReceiver.registerReceiver(this, receiver);
        req();
    }

    private void req() {
        PackSubscriptionAccountUp up =new PackSubscriptionAccountUp();
        up.type="2";
        PcsDataDownload.addDownload(up);
    }

    private class MyReceiver extends PcsDataBrocastReceiver {

        @Override
        public void onReceive(String nameStr, String errorStr) {
            if(nameStr.equals(PackSubscriptionAccountUp.NAME)) {
                PackSubscriptionAccountDown down = (PackSubscriptionAccountDown) PcsDataManager.getInstance().getNetPack(nameStr);
                if(down == null) {
                    return;
                }
                tvTitle.setText(down.title);
                tvAccount.setText(down.wxh);
                tvDesc.setText(down.gnjs);
                tvUnit.setText(down.zt);
                String iconPath = getString(R.string.file_download_url) + down.Img1;
                String qrPath = getString(R.string.file_download_url) + down.Img2;
                Picasso.get().load(iconPath).into(ivIcon);
                Picasso.get().load(qrPath).into(ivQr);
            }
        }
    }

}
