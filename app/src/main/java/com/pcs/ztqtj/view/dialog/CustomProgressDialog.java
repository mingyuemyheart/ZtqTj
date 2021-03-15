package com.pcs.ztqtj.view.dialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.pcs.ztqtj.R;

/**
 * @author Z 数据加载等待dialog
 */
public class CustomProgressDialog extends ProgressDialog {
	public CustomProgressDialog(Context context) {
		super(context);
	}

	public CustomProgressDialog(Context context, int theme) {
		super(context, theme);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.progressdialoglayout);
		setOnShowListener(mOnShowListener);
	}

	/**
	 * 提示内容
	 * 
	 * @param strMessage
	 * @return
	 * 
	 */
	public CustomProgressDialog setMessage(String strMessage) {
		TextView tvMsg = (TextView) findViewById(R.id.id_tv_loadingmsg);
		if (tvMsg != null) {
			tvMsg.setText(strMessage);
		}
		return this;
	}

	private OnShowListener mOnShowListener = new OnShowListener() {
		@Override
		public void onShow(DialogInterface dialog) {
			ImageView imageView = (ImageView) findViewById(R.id.loadingImageView);
			AnimationDrawable animationDrawable = (AnimationDrawable) imageView
					.getBackground();
			animationDrawable.start();
		}
	};
}