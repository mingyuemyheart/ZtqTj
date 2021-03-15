package com.pcs.ztqtj.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

public class DialogFactory {
	private DialogFactory() {
	}

	public interface DialogListener {
		public void click(String str);
	}

	/**
	 * 创建一个按钮的对话框
	 * @param context
	 * @param view
	 * 			中间view
	 * @param str
	 * 			按钮文字
	 * @param dialogListener
	 * 			按钮事件
	 * @return
	 * 		Dialog
	 * 
	 */
	public static Dialog getOneButton(Context context, View view, String str,
			DialogListener dialogListener){
		
		Dialog dialog=new DialogOneButton(context, view, str, dialogListener);
		
		return dialog;
	}

	/**
	 * 创建两个按钮的对话框
	 * @param context
	 * @param view
	 * 			中间view
	 * @param posistr
	 * 			左边按钮文字
	 * @param negastr
	 * 			右边按钮文字
	 * @param dialogListener
	 * 			事件监听，public void click(String str)、str为按钮的文字
	 * @return
	 * 
	 * 		Dialog
	 */
	public static Dialog getTwoDialog(Context context, View view, String posistr, String negastr,
			DialogListener dialogListener) {
		Dialog dialog=new DialogTwoButton(context, view, posistr, negastr, dialogListener);
		
		return dialog;
	}

	/**
	 * 创建两个按钮的对话框
	 * @param context
	 * @param view
	 * 			中间view
	 * @param posistr
	 * @param negastr
	 * @param nugelstr
	 * @param dialogListener
	 * 			事件监听，public void click(String str)、str为按钮的文字
	 * @return
	 * 		Dialog
	 */
	
	public static Dialog getThreeDialog(Context context, View view, String posistr,String negastr,String nugelstr,
			DialogListener dialogListener) {
		
		Dialog dialog=new DialogThreeButton(context, view, posistr, negastr, nugelstr, dialogListener);
		
		return dialog;
	}

	/**
	 * 创建一个等待框，
	 * 
	 * @param context
	 * @param message
	 *            等待框信息
	 * @return
	 * 
	 * 		Dialog
	 */
	public static Dialog getWaitDialog(Context context, String message) {
		Dialog dialog = new DialogWaiting(context, message);
		return dialog;
	}

}