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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.view.dialog.DialogFactory.DialogListener;

public class DialogOneButton extends Dialog {
	private Context context;
	private Button positiveButton;
	private Button closeBtn;
	private String str = "";
	private DialogListener dialogListener;
	private TextView titleTextView;
	public static ImageButton btnBack;
	

	public DialogOneButton(Context context, View view, String str, DialogListener dialogListener) {
		super(context, R.style.MyDialog);
		this.context = context;
		this.str = str;
		this.dialogListener = dialogListener;
		this.setContentView(view);
		setTitle(context.getString(R.string.dialog_default_title));
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	public void setTitle(String str) {
		titleTextView.setText(str);
	}

	/** 显示顶部的关闭按钮 **/
	public void showCloseBtn() {
		closeBtn.setVisibility(View.VISIBLE);
	}
	/** 显示顶部的返回按钮 **/
	public void showBtnBack() {
		btnBack.setVisibility(View.VISIBLE);
	}
	/** 隐藏顶部的返回按钮 **/
	public void goneBtnBack() {
		btnBack.setVisibility(View.GONE);
	}
	@Override
	public void setContentView(View chilview) {
		View viewparent = LayoutInflater.from(context).inflate(R.layout.dialog_parentlayoutthreebutton, null);

		titleTextView = (TextView) viewparent.findViewById(R.id.title);

		LinearLayout contentLayout = (LinearLayout) viewparent.findViewById(R.id.content_layout);
		positiveButton = (Button) viewparent.findViewById(R.id.positivebutton);
		positiveButton.setText(str);
		positiveButton.setVisibility(View.VISIBLE);
		positiveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialogListener.click(str);
			}
		});

		closeBtn = (Button) viewparent.findViewById(R.id.close_btn);
		closeBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialogListener.click("close");
			}
		});
		btnBack=(ImageButton) viewparent.findViewById(R.id.btn_back);
		LinearLayout.LayoutParams pareams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
		contentLayout.addView(chilview, pareams);
		super.setContentView(viewparent);
		setdialogwidth(context);
	}

	/**
	 * 设置dialog的宽
	 * 
	 * @param context
	 */
	private void setdialogwidth(Context context) {
		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = windowManager.getDefaultDisplay();
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.width = (int) ((display.getWidth() / 11) * 10); // 设置宽度
		lp.gravity = Gravity.CENTER;
		this.getWindow().setAttributes(lp);
		this.setCancelable(false);
		this.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					dialog.dismiss();
					return true;
				}
				return false;
			}
		});

	}
}