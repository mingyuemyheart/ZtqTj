package com.pcs.ztqtj.view.activity.warn;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.Contacts;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.MyConfigure;
import com.pcs.ztqtj.control.tool.NetTask;
import com.pcs.ztqtj.control.tool.ShareTools;
import com.pcs.ztqtj.control.tool.SharedPreferencesUtil;
import com.pcs.ztqtj.control.tool.ZtqImageTool;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
import com.pcs.lib.lib_pcs_v3.control.tool.BitmapUtil;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib.lib_pcs_v3.model.pack.PcsPackDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackShareAboutDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackShareAboutUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.PackWarnPubDetailDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.PackWarnPubDetailUp;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;


/**
 * @author Z 预警详情
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
        Intent intent = getIntent();
        Bundle bundle = getIntent().getBundleExtra(MyConfigure.EXTRA_BUNDLE);
        if(bundle != null) {
//            setTitleText(bundle.getString("t"));
            icon = bundle.getString("i");
            id = bundle.getString("id", "");
            type=bundle.getString("type", "");
        }
        SharedPreferencesUtil.putData(id,id);

        initView();

        PackWarnPubDetailUp packDetailUp = new PackWarnPubDetailUp();
        packDetailUp.id = id;
        if (type.equals("1") || type.equals("2")){
            packDetailUp.type = type;
        }
        NetTask task = new NetTask(this, new NetTask.NetListener() {
            @Override
            public void onComplete(PcsPackDown down) {
                dismissProgressDialog();
                if(down != null) {
                    PackWarnPubDetailDown warnDown =(PackWarnPubDetailDown) down;
                    PackShareAboutDown shareDown= (PackShareAboutDown) PcsDataManager.getInstance().getNetPack(PackShareAboutUp.getNameCom());
                    String shareAdd="";
                    if (shareDown!=null) {
                        shareAdd=shareDown.share_content;
                    }
                    contentImageview = "";
                    titledata = warnDown.put_str;
                    titlecontent = warnDown.desc;
                    contentText = warnDown.content;
                    shareContent = titlecontent + "：" + contentText + "(" + titledata + ")" + shareAdd;
                    defend = warnDown.defend;
                    initData();
                }
            }
        });
        task.execute(packDetailUp);

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
//				String warn = getResources().getString(getDefenseMsg(icon));
//				defense_guidelines.setText(warn);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    final String[] level = {"bb_O", "bb_R", "by_B", "by_O", "by_R", "by_Y", "df_O", "df_R", "df_Y", "dljb_O", "dljb_R", "dljb_Y", "dw_O", "dw_R", "dw_Y", "gh_O", "gh_R", "gw_O",
            "gw_R", "jw_B", "jw_O", "jw_R", "jw_Y", "ld_O", "ld_R", "ld_Y", "m_O", "m_Y", "sd_B", "sd_O", "sd_Y", "tf_B", "tf_O", "tf_R", "tf_Y", "xz_O", "xz_R", "xz_Y"};

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
                //ShareUtil.share(ActivityWarnDetails.this, shareContent, bitmap);
                ShareTools.getInstance(ActivityWarnDetails.this).setShareContent(getTitleText(),shareContent, bitmap, "1").showWindow(layout);
            }
        });
    }

    /**
     * 获取详情失败
     */
    private void showDetilError() {
        Toast.makeText(this, R.string.get_detail_error, Toast.LENGTH_SHORT).show();
    }

}
