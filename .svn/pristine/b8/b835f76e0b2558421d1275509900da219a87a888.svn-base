package com.pcs.ztqtj.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.pcs.ztqtj.R;

public class DialogWaiting extends Dialog {
	private Context context;
	private String message = "";

	public DialogWaiting(Context context, String message) {
		super(context, R.style.MyDialog);
		this.context = context;
		this.message = message;
		View viewparent = LayoutInflater.from(context).inflate(
				R.layout.dialog_waiting_layout, null);

		this.setContentView(viewparent);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	public void setContentView(View viewparent) {
		TextView messagetv = (TextView) viewparent
				.findViewById(R.id.dialog_info);
		messagetv.setText(message);
		super.setContentView(viewparent);
		setdialogwidth(context);
	}

	/**
	 * 设置dialog的宽
	 * 
	 * @param context
	 */
	private void setdialogwidth(Context context) {
		WindowManager windowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = windowManager.getDefaultDisplay();
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.width = (int) ((display.getWidth() / 11) * 10); // 设置宽度
		lp.gravity = Gravity.CENTER;
		this.getWindow().setAttributes(lp);

		this.setCancelable(false);
		this.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					dialog.dismiss();
					return true;
				}
				return false;
			}
		});
		
	}
}