package com.pcs.ztqtj.view.activity.life;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.ShareTools;
import com.pcs.ztqtj.control.tool.ZtqImageTool;
import com.pcs.ztqtj.control.tool.image.ImageLoader;
import com.pcs.ztqtj.view.activity.FragmentActivitySZYBBase;
import com.squareup.picasso.Picasso;

/**
 * 生活气象-气象科普-列表-详情
 */
public class ActivityChannelInfo extends FragmentActivitySZYBBase {

	private ImageLoader mImageLoader;
	private ImageView bigImageView;// 图片获取类
	private TextView TextView1;
	private TextView textTitle;
	private TextView textTime;
	private String title = "";
	private MyArtTitleInfo info;
	private String url = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.channel_info_layout);
		info = (MyArtTitleInfo) getIntent().getSerializableExtra("info");
		title = getIntent().getStringExtra("title");
		mImageLoader = new ImageLoader(this);
		setTitleText(title);
		initView();
		initData();
		initEvent();
	}

    private void initEvent() {
        setBtnRight(R.drawable.icon_share_new, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String share_content = "";
                if (!TextUtils.isEmpty(textTitle.getText().toString())) {
                    share_content =  " 《" + textTitle.getText().toString() + "》 ";
                }
                View layout = findViewById(R.id.scrollview);
                Bitmap headBitmap = ZtqImageTool.getInstance().getScreenBitmap(headLayout);
                Bitmap shareBitmap = ZtqImageTool.getInstance().getScreenBitmap(layout);
                shareBitmap = ZtqImageTool.getInstance().stitch(headBitmap, shareBitmap);
                shareBitmap = ZtqImageTool.getInstance().stitchQR(ActivityChannelInfo.this, shareBitmap);
                ShareTools.getInstance(ActivityChannelInfo.this).setShareContent(getTitleText(),share_content, url, shareBitmap).showWindow(layout);
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
		url = info.url;
		if (TextUtils.isEmpty(info.big_ico) || "null".equals(info.big_ico)) {
			Log.d("info.big_ico", "大图为空");
			bigImageView.setImageResource(R.drawable.no_pic);
		} else {
			String big_ico = getString(R.string.msyb) + info.big_ico;
			if (info.big_ico.startsWith("http")) {
				big_ico = info.big_ico;
			}
			Bitmap bitmap = null;
			bitmap = mImageLoader.getBitmapFromCache(big_ico);
			if (bitmap != null) {
				bigImageView.setImageBitmap(bitmap);
			} else {
				Picasso.get().load(big_ico).into(bigImageView);
			}
		}

		String txt = "暂无数据";
		if (!TextUtils.isEmpty(info.desc)) {
			txt = info.desc;
		}
		if (!TextUtils.isEmpty(info.content)) {
			txt = info.content;
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
