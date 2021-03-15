package com.pcs.ztqtj.view.myview;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
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

public class MyDialog extends Dialog {
	private Context context;
	private Button positiveButton;
	private Button negativeButton;
	private Button neutralbutton;
	private String posistr = "";
	private String negastr = "";
	private String neutralstr = "";
	private DialogListener dialogListener;
	private TextView titleTextView;
	private View btn_line_1, btn_line_2;

	public static final String DIMSSDIALOG = "key_back_click";

	/** 3个按钮的对话框 */
	public MyDialog(Context context, View view, String posistr, String negastr, String nugelstr, DialogListener dialogListener) {
		super(context, R.style.MyDialog);
		this.context = context;
		this.posistr = posistr;
		this.negastr = negastr;
		this.neutralstr = nugelstr;
		this.dialogListener = dialogListener;
		this.setContentView(view);
	}

	/** 2个按钮的对话框 */
	public MyDialog(Context context, View view, String posistr, String negastr, DialogListener dialogListener) {
		super(context, R.style.MyDialog);
		this.context = context;
		this.posistr = posistr;
		this.negastr = negastr;
		this.dialogListener = dialogListener;
		this.setContentView(view);
	}

	/** 1个按钮的对话框 */
	public MyDialog(Context context, View view, String posistr, DialogListener dialogListener) {
		super(context, R.style.MyDialog);
		this.context = context;
		this.posistr = posistr;
		this.dialogListener = dialogListener;
		this.setContentView(view);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	public void setTitle(String str) {
		titleTextView.setText(str);
	}

	@Override
	public void setContentView(View chilview) {
		View viewparent = LayoutInflater.from(context).inflate(R.layout.mydialog_parent, null);
		LinearLayout contentLayout = (LinearLayout) viewparent.findViewById(R.id.content_layout);
		initView(viewparent);
		initData();
		initEvent();
		setTitle("天津气象提示");
		LinearLayout.LayoutParams pareams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
		contentLayout.addView(chilview, pareams);
		super.setContentView(viewparent);
		setdialogwidth(context);
	}

	private void initEvent() {
		neutralbutton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (dialogListener != null) {
					dialogListener.click(neutralstr);
				}
			}
		});
		negativeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (dialogListener != null) {
					dialogListener.click(negastr);
				}
			}
		});
		positiveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (dialogListener != null) {
					dialogListener.click(posistr);
				}
			}
		});
	}

	private void initData() {
		positiveButton.setText(posistr);
		negativeButton.setText(negastr);
		neutralbutton.setText(neutralstr);
		if (TextUtils.isEmpty(posistr)) {
			positiveButton.setVisibility(View.GONE);
		} else {
			positiveButton.setVisibility(View.VISIBLE);
		}
		if (TextUtils.isEmpty(negastr)) {
			btn_line_1.setVisibility(View.GONE);
			negativeButton.setVisibility(View.GONE);
		} else {
			btn_line_1.setVisibility(View.VISIBLE);
			negativeButton.setVisibility(View.VISIBLE);
		}
		if (TextUtils.isEmpty(neutralstr)) {
			btn_line_2.setVisibility(View.GONE);
			neutralbutton.setVisibility(View.GONE);
		} else {
			btn_line_1.setVisibility(View.VISIBLE);
			neutralbutton.setVisibility(View.VISIBLE);
		}
	}

	private void initView(View chilview) {
		titleTextView = (TextView) chilview.findViewById(R.id.title);

		positiveButton = (Button) chilview.findViewById(R.id.positivebutton);
		negativeButton = (Button) chilview.findViewById(R.id.negativebutton);
		neutralbutton = (Button) chilview.findViewById(R.id.neutralbutton);
		btn_line_1 = chilview.findViewById(R.id.btn_line_1);
		btn_line_2 = chilview.findViewById(R.id.btn_line_2);
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
		lp.width = (int) ((display.getWidth() / 9) * 8); // 设置宽度
		lp.gravity = Gravity.CENTER;
		this.getWindow().setAttributes(lp);
		this.setCancelable(false);
		this.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					if (dialogListener != null) {
						dialogListener.click(DIMSSDIALOG);
					} else {
						dismiss();
					}
					return true;
				}
				return false;
			}
		});
	}

	public interface DialogListener {
		public void click(String str);
	}
}