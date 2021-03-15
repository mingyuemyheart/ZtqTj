package com.pcs.ztqtj.view.activity.life;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.CommUtils;
import com.pcs.ztqtj.control.tool.ShareTools;
import com.pcs.ztqtj.control.tool.ZtqImageTool;
import com.pcs.ztqtj.control.tool.image.ImageLoader;
import com.pcs.ztqtj.control.tool.image.ImageUtils;
import com.pcs.ztqtj.view.activity.FragmentActivitySZYBBase;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackShareAboutDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.art.ArtTitleInfo;

/**
 * 资讯详细信息
 * 
 * @author chenjh
 * 
 */
public class ActivityChannelInfo extends FragmentActivitySZYBBase {

	private ImageView bigImageView;
	private ImageLoader mImageLoader;
	private TextView TextView1;
	private TextView textTitle;
	private TextView textTime;
	private String title = "";
	private ArtTitleInfo info;
	private int screenWidth = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.channel_info_layout);
		info = (ArtTitleInfo) getIntent().getSerializableExtra("info");
		title = getIntent().getStringExtra("title");
		mImageLoader = new ImageLoader(ActivityChannelInfo.this);
		setTitleText(title);
		initView();
		initData();
		initEvent();
	}

    private void initEvent() {
        setBtnRight(R.drawable.icon_share_new, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PackShareAboutDown down = (PackShareAboutDown) PcsDataManager.getInstance().getNetPack("wt_share#ABOUT_QXCP_DXFW");
                if (down == null) {
                    return;
                }
                String share_content = "";
                if (!TextUtils.isEmpty(textTitle.getText().toString())) {
                    share_content =  " 《" + textTitle.getText().toString() + "》 " + down.share_content;
                } else {
                    share_content =  down.share_content;
                }
                View layout = findViewById(R.id.scrollview);
                Bitmap headBitmap = ZtqImageTool.getInstance().getScreenBitmap(headLayout);
                Bitmap shareBitmap = ZtqImageTool.getInstance().getScreenBitmap(layout);
                shareBitmap = ZtqImageTool.getInstance().stitch(headBitmap, shareBitmap);
                shareBitmap = ZtqImageTool.getInstance().stitchQR(ActivityChannelInfo.this, shareBitmap);
                ShareTools.getInstance(ActivityChannelInfo.this).setShareContent(getTitleText(),share_content, shareBitmap,"0").showWindow(layout);
            }
        });
    }

    private void initView() {
		TextView1 = (TextView) findViewById(R.id.TextView1);
		textTitle = (TextView) findViewById(R.id.text_content_title);
		textTime = (TextView) findViewById(R.id.text_time);
		bigImageView = (ImageView) findViewById(R.id.item_image);
	}

	private void initData() {
		screenWidth = CommUtils.getScreenWidth(ActivityChannelInfo.this);
		if (TextUtils.isEmpty(info.big_ico) || "null".equals(info.big_ico)) {
			Log.d("info.big_ico", "大图为空");
			ImageUtils.getInstance().setBgImage(ActivityChannelInfo.this, bigImageView, screenWidth, R.drawable.no_pic, true);
		} else {
			String big_ico = getString(R.string.file_download_url) + info.big_ico;
			Bitmap bitmap = null;
			bitmap = mImageLoader.getBitmapFromCache(big_ico);
			if (bitmap != null) {
				bigImageView.setImageBitmap(bitmap);
			} else {
				mImageLoader.loadImage(big_ico, ActivityChannelInfo.this, bigImageView, R.drawable.no_pic, screenWidth, true);
			}
		}
		String txt = "暂无数据";
		if (!TextUtils.isEmpty(info.desc)) {
			txt = info.desc;
		}
		String str=txt.replace("\r", "\n\r");
		TextView1.setText(str);

		// 标题
		txt = "";
		if (!TextUtils.isEmpty(info.title)) {
			txt = info.title;
		}
		textTitle.setText(txt);
		// 时间
		txt = "";
		if (!TextUtils.isEmpty(info.pubt)) {
			txt = info.pubt;
		}
		textTime.setText(txt);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// if (mWebView != null) {
		// mParent.removeView(mWebView);
		// WebView w = mWebView;
		// w.removeAllViews();
		// w.destroy();
		// w = null;
		// mWebView = null;
		// }
	}

}
