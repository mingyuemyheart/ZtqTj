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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.view.dialog.DialogFactory.DialogListener;

public class DialogVoiceButton extends Dialog {
	private Context context;
	private Button closeBtn;
	private Button positiveButton;
	private Button negativeButton;
	private String posistr = "";
	private DialogListener dialogListener;

	private TextView titleTextView;

	public DialogVoiceButton(Context context, View view, String posistr,
                              DialogListener dialogListener) {
		super(context, R.style.MyDialog);
		this.context = context;
		this.posistr = posistr;
		this.dialogListener = dialogListener;
		this.setContentView(view);
		setTitle(context.getString(R.string.dialog_default_title));
	}

	public void setTitle(String str) {
		titleTextView.setText(str);
	}

	/** 显示顶部的关闭按钮 **/
	public void showCloseBtn() {
		closeBtn.setVisibility(View.VISIBLE);
	}

    public void setCloseBtnListener(View.OnClickListener listener) {
        if(listener != null) {
            closeBtn.setOnClickListener(listener);
        }
    }

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	private View view_line;
	@Override
	public void setContentView(View chilview) {
		View viewparent = LayoutInflater.from(context).inflate(
				R.layout.dialog_parentlayout, null);
		LinearLayout contentLayout = (LinearLayout) viewparent
				.findViewById(R.id.content_layout);
		titleTextView = (TextView) viewparent.findViewById(R.id.title);
		positiveButton = (Button) viewparent.findViewById(R.id.positivebutton);
		positiveButton.setText(posistr);
		negativeButton = (Button) viewparent.findViewById(R.id.negativebutton);
        negativeButton.setVisibility(View.GONE);
//        view_line=findViewById(R.id.view_line);
//        view_line.setVisibility(View.GONE);
		positiveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialogListener.click(posistr);
			}
		});
		closeBtn = (Button) viewparent.findViewById(R.id.close_btn);
		closeBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialogListener.click("close");
			}
		});

		LinearLayout.LayoutParams pareams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
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
		WindowManager windowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = windowManager.getDefaultDisplay();
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.width = (display.getWidth() / 11) * 10; // 设置宽度
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