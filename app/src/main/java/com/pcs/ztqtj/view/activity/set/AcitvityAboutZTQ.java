package com.pcs.ztqtj.view.activity.set;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.pcs.lib.lib_pcs_v3.control.tool.BitmapUtil;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCityMain;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.ShareTools;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;

/**
 * 设置-二维码
 */
public class AcitvityAboutZTQ extends FragmentActivityZtqBase {

    private TextView versionTextView;
    private ImageView qr_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBackDirection(BackDirection.BACK_RIGHT);
        Bundle bundle = getIntent().getExtras();
        setTitleText(bundle.getString("title"));
        setContentView(R.layout.activity_about_ztq);
        initView();
        // 设置关于
        setAbout();
    }

    private void initView() {
        versionTextView = (TextView) findViewById(R.id.versionname);
        qr_code = (ImageView) findViewById(R.id.qr_code);

        PackageManager mg = this.getPackageManager();
        PackageInfo information;
        try {
            information = mg.getPackageInfo(this.getPackageName(), 0);
            String version = "";
            version += information.versionName;
            versionTextView.setText(version);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        setBtnRight(R.drawable.btn_share, new OnClickListener() {
            @Override
            public void onClick(View v) {
                share();
            }
        });

        qr_code.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    private void share() {
        Bitmap bitmap = BitmapUtil.takeScreenShot(this);
        View layout = findViewById(R.id.layout);
        ShareTools.getInstance(this).setShareContent(getTitleText(), CONST.SHARE_URL, bitmap, "0").showWindow(layout);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 设置关于
     */
    private void setAbout() {
        TextView textView = (TextView) findViewById(R.id.text_view_about);
        // 当前城市
        PackLocalCityMain cityMain = ZtqCityDB.getInstance().getCityMain();
        if (cityMain != null && cityMain.isFjCity) {
            if (cityMain.NAME.equals("天津市区")) {
                String str = getResources().getString(R.string.careaboutasdescribed_1)
                        + "天津市公共气象服务中心"
                        + getResources().getString(R.string.careaboutasdescribed_2);
                textView.setText(str);
            } else {
                String str = getResources().getString(R.string.careaboutasdescribed_1)
                        + cityMain.NAME + "气象局"
                        + getResources().getString(R.string.careaboutasdescribed_2);
                textView.setText(str);
            }
        } else {
            String str = getResources().getString(R.string.careaboutasdescribed_1)
                    + "天津市公共气象服务中心"
                    + getResources().getString(R.string.careaboutasdescribed_2);
            textView.setText(str);
        }
    }
}
