package com.pcs.ztqtj.view.activity.push;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.Contacts;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.NetTask;
import com.pcs.ztqtj.control.tool.ShareTools;
import com.pcs.ztqtj.control.tool.ZtqImageTool;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib.lib_pcs_v3.model.pack.PcsPackDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackShareAboutDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackShareAboutUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.PackWarnPubDetailDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.PackWarnPubDetailUp;

import java.util.HashMap;

/**
 * Created by tyaathome on 2017/7/26.
 */

public class ActivityPushEmergencyDetails extends FragmentActivityZtqBase {
    private ImageButton shareButton;
    private TextView content;
    private TextView title_content;
    private TextView title_date;
    private String titlecontent;
    private String ptime = "";

    private String contentText;
    private String shareContent;
    private MyReceiver receiver = new MyReceiver();

    private String author;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pull_emergency_details);
        PcsDataBrocastReceiver.registerReceiver(this, receiver);
        Intent intent = getIntent();
        setTitleText("预警详情");
        author = intent.getStringExtra("AUTHOR");
        titlecontent = intent.getStringExtra("TITLE");
        ptime = intent.getStringExtra("PTIME");
        contentText = intent.getStringExtra("CONTENT");
        id = intent.getStringExtra("ID");
        shareContent = titlecontent + "：" + contentText + "(" + ptime + ")";
        initView();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private void initData() {
        req();
    }

    private PackWarnPubDetailUp packup;

    public void req() {
        if (!isOpenNet()) {
            showToast(getString(R.string.net_err));
            return;
        }
        showProgressDialog();
        packup = new PackWarnPubDetailUp();
        packup.id = id;
//        packup.type = "1";
        NetTask task = new NetTask(this, new NetTask.NetListener() {
            @Override
            public void onComplete(PcsPackDown down) {
                dismissProgressDialog();
                if (down == null)
                    return;
                PackWarnPubDetailDown packDown = (PackWarnPubDetailDown) down;
                contentText = packDown.content;
                shareContent = titlecontent + "：" + contentText + "(" + ptime + ")";
                title_date.setText(author + ptime);
                title_content.setText(titlecontent);
                content.setText(packDown.content);
            }
        });
        task.execute(packup);
        //PcsDataDownload.addDownload(packup);
    }

    private class MyReceiver extends PcsDataBrocastReceiver {
        @Override
        public void onReceive(String name, String errorStr) {
            // 取出内容替换
            if (name.equals(packup.getName())) {
                dismissProgressDialog();
                PackWarnPubDetailDown packDown = (PackWarnPubDetailDown) PcsDataManager.getInstance().getNetPack(name);
                if (packDown == null)
                    return;
                contentText = packDown.content;
                shareContent = titlecontent + "：" + contentText + "(" + ptime + ")";
                title_date.setText(author + ptime);
                title_content.setText(titlecontent);
                content.setText(packDown.content);
            }
        }
    }

    final String[] level = {"bb_O", "bb_R", "by_B", "by_O", "by_R", "by_Y", "df_O", "df_R", "df_Y", "dljb_O",
            "dljb_R", "dljb_Y", "dw_O", "dw_R", "dw_Y", "gh_O", "gh_R", "gw_O",
            "gw_R", "jw_B", "jw_O", "jw_R", "jw_Y", "ld_O", "ld_R", "ld_Y", "m_O", "m_Y", "sd_B", "sd_O", "sd_Y",
            "tf_B", "tf_O", "tf_R", "tf_Y", "xz_O", "xz_R", "xz_Y"};

    private int getDefenseMsg(String icon) {

        HashMap<String, Integer> map = new HashMap<String, Integer>();
        for (int i = 0; i < level.length; i++) {
            map.put(level[i], i);
        }
        if (map.get(icon) == null) {
            return R.string.defense_38;
        }
        try {
            return R.string.defense_00 + map.get(icon);
        } catch (Exception e) {
            return 0;
        }
    }

    private PackShareAboutUp shareAboutUp;
    private void reqNet() {

        PackShareAboutDown shareDown= (PackShareAboutDown) PcsDataManager.getInstance().getNetPack(PackShareAboutUp.getNameCom());
        View layout = findViewById(R.id.layout_main);
        View rootView = layout.getRootView();
        Bitmap bitmap = ZtqImageTool.getInstance().getScreenBitmap(layout);
        bitmap = ZtqImageTool.getInstance().stitchQR(ActivityPushEmergencyDetails.this, bitmap);
        ShareTools.getInstance(ActivityPushEmergencyDetails.this).setShareContent(shareContent+shareDown.share_content, bitmap,"0").showWindow(rootView);
    }



    public void initView() {
        shareButton = (ImageButton) findViewById(R.id.warn_share);
        title_content = (TextView) findViewById(R.id.title_content);
        title_date = (TextView) findViewById(R.id.title_data);
        content = (TextView) findViewById(R.id.warn_content);

        setBtnRight(R.drawable.maillist_button, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 通讯录
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Contacts.People.CONTENT_URI);
                startActivity(intent);
            }
        });
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reqNet();

//                Bitmap bitmap = BitmapUtil.takeScreenShot(ActivityPushEmergencyDetails.this);
//                ShareUtil.share(ActivityPushEmergencyDetails.this, shareContent, bitmap);

//                View layout = findViewById(R.id.layout_main);
//                View rootView = layout.getRootView();
//                Bitmap bitmap = ZtqImageTool.getInstance().getScreenBitmap(layout);
//                bitmap = ZtqImageTool.getInstance().stitchQR(ActivityPushEmergencyDetails.this, bitmap);
//                ShareTools.getInstance(ActivityPushEmergencyDetails.this).setShareContent(shareContent, bitmap,"0").showWindow(rootView);
            }
        });
    }
}
