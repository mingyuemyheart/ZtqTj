package com.pcs.ztqtj.view.activity.push;

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

import com.pcs.lib.lib_pcs_v3.control.tool.BitmapUtil;
import com.pcs.lib.lib_pcs_v3.model.pack.PcsPackDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.PackWarnPubDetailDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.PackWarnPubDetailUp;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.NetTask;
import com.pcs.ztqtj.control.tool.ShareTools;
import com.pcs.ztqtj.control.tool.ZtqImageTool;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Z 推送预警详情 更多
 */
public class ActivityPushWarnDetails extends FragmentActivityZtqBase {
	private ImageButton shareButton;
	private TextView content;
	private TextView title_content;
	private TextView title_date;
	private TextView defense_guidelines;
	private String titlecontent;
	private String ptime = "";

	private String contentText;
	private String shareContent;
	private String icon;
	private String id;
	private ImageView icon_title;

	private String author;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pull_warn_details);
		Intent intent = getIntent();
		setTitleText("预警详情");
		try {
			author = intent.getStringExtra("AUTHOR");
			titlecontent = intent.getStringExtra("TITLE");
			icon = intent.getStringExtra("ICO");
			ptime = intent.getStringExtra("PTIME");
			contentText = intent.getStringExtra("CONTENT");
			shareContent = titlecontent + "：" + contentText + "(" + ptime + ")";
			id = intent.getStringExtra("ID");
		} catch (Exception e) {
			e.printStackTrace();
		}
		initView();
		initData();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
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
		req();
	}

	private PackWarnPubDetailUp packup;

	public void req() {

		if(!isOpenNet()){
			showToast(getString(R.string.net_err));
			return ;
		}
		showProgressDialog();
        packup = new PackWarnPubDetailUp();
        packup.id = id;
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
                if (!titlecontent.contains("解除")) {
                    defense_guidelines.setText(packDown.defend);
                }
            }
        });
        task.execute(packup);
	}

    private void reqNet() {
        View layout = findViewById(R.id.layout_main);
        View rootView = layout.getRootView();
        Bitmap bitmap = ZtqImageTool.getInstance().getScreenBitmap(layout);
        bitmap = ZtqImageTool.getInstance().stitchQR(ActivityPushWarnDetails.this, bitmap);
        ShareTools.getInstance(ActivityPushWarnDetails.this).setShareContent(shareContent+ CONST.SHARE_URL, bitmap,"0").showWindow(rootView);
    }

	public void initView() {
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
			    reqNet();
			}
		});
	}
}
