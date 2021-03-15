package com.pcs.ztqtj.view.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.pcs.lib.lib_pcs_v3.control.tool.BitmapUtil;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackCompetitionEntryDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackCompetitionEntryUp;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.CommUtils;
import com.pcs.ztqtj.control.tool.youmeng.ShareUtil;

/**
 * 主播比赛报名
 * 
 * @author JiangZy
 * 
 */
public class ActivityCompetitionEntry extends FragmentActivityZtqBase {
	// 对话框
	private AlertDialog.Builder mDialogBuilder;

	// 上传包
	private PackCompetitionEntryUp mPackUp = new PackCompetitionEntryUp();
	// 下载包
	private PackCompetitionEntryDown mPackDown = new PackCompetitionEntryDown();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_competition_entry);
		setTitleText("气象主播选拔赛");
		setBtnRight(R.drawable.btn_share, mOnClick);
		// 初始化重置对话框
		initResetDialog();

		EditText edit;
		Button btn;
		// 电话
		edit = (EditText) findViewById(R.id.edit_phone);
		edit.setOnEditorActionListener(mEditorListener);
		// 重置
		btn = (Button) findViewById(R.id.btn_reset);
		btn.setOnClickListener(mOnClick);
		// 提交
		btn = (Button) findViewById(R.id.btn_commit);
		btn.setOnClickListener(mOnClick);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 注册广播接收
		PcsDataBrocastReceiver.registerReceiver(this, mReceiver);
	}

	@Override
	protected void onPause() {
		super.onPause();
		// 注销广播接收
		PcsDataBrocastReceiver.unregisterReceiver(this, mReceiver);
	}

	/**
	 * 初始化重置对话框
	 */
	private void initResetDialog() {
		mDialogBuilder = new AlertDialog.Builder(this);
		mDialogBuilder.setCancelable(true);
		mDialogBuilder.setTitle(R.string.competition_reset_confirm);
		mDialogBuilder.setPositiveButton(R.string.reset, mDialogOnClick);
		mDialogBuilder.setNegativeButton(R.string.cancel, mDialogOnClick);
	}

	/**
	 * 清空输入框
	 */
	private void clearEdit() {
		EditText edit;
		View tip;
		// 姓名
		edit = (EditText) findViewById(R.id.edit_name);
		edit.setText("");
		tip = findViewById(R.id.text_tip1);
		tip.setVisibility(View.GONE);
		// 性别
		edit = (EditText) findViewById(R.id.edit_gender);
		edit.setText("");
		tip = findViewById(R.id.text_tip2);
		tip.setVisibility(View.GONE);
		// 年龄
		edit = (EditText) findViewById(R.id.edit_age);
		edit.setText("");
		tip = findViewById(R.id.text_tip3);
		tip.setVisibility(View.GONE);
		// 学历
		edit = (EditText) findViewById(R.id.edit_education);
		edit.setText("");
		tip = findViewById(R.id.text_tip4);
		tip.setVisibility(View.GONE);
		// 电话
		edit = (EditText) findViewById(R.id.edit_phone);
		edit.setText("");
		tip = findViewById(R.id.text_tip5);
		tip.setVisibility(View.GONE);
	}

	/**
	 * 禁止所有
	 */
	private void disableAll() {
		EditText edit;
		// 姓名
		edit = (EditText) findViewById(R.id.edit_name);
		edit.setEnabled(false);
		// 性别
		edit = (EditText) findViewById(R.id.edit_gender);
		edit.setEnabled(false);
		// 年龄
		edit = (EditText) findViewById(R.id.edit_age);
		edit.setEnabled(false);
		// 学历
		edit = (EditText) findViewById(R.id.edit_education);
		edit.setEnabled(false);
		// 电话
		edit = (EditText) findViewById(R.id.edit_phone);
		edit.setEnabled(false);

		Button btn;
		// 重置
		btn = (Button) findViewById(R.id.btn_reset);
		btn.setTextColor(getResources().getColor(R.color.text_gray_light));
		btn.setEnabled(false);
		// 提交
		btn = (Button) findViewById(R.id.btn_commit);
		btn.setTextColor(getResources().getColor(R.color.text_gray_light));
		btn.setEnabled(false);
	}

	/**
	 * 点击分享
	 */
	private void clickShare() {
		Bitmap bitmap = BitmapUtil.takeScreenShot(this);
		ShareUtil.share(this, getString(R.string.competition_share_content),bitmap);
	}

	/**
	 * 点击重置
	 */
	private void clickReset() {
        CommUtils.closeKeyboard(this);
		mDialogBuilder.show();
	}

	/**
	 * 点击提交
	 */
	private void clickCommit() {
        CommUtils.closeKeyboard(this);
		boolean isValid = true;
		// 姓名
		EditText editName = (EditText) findViewById(R.id.edit_name);
		View tip1 = findViewById(R.id.text_tip1);
		tip1.setVisibility(View.GONE);
		if (TextUtils.isEmpty(editName.getText().toString())) {
			tip1.setVisibility(View.VISIBLE);
			isValid = false;
		}
		// 性别
		EditText editGender = (EditText) findViewById(R.id.edit_gender);
		View tip2 = findViewById(R.id.text_tip2);
		tip2.setVisibility(View.GONE);
		if (TextUtils.isEmpty(editGender.getText().toString())) {
			tip2.setVisibility(View.VISIBLE);
			isValid = false;
		}
		// 年龄
		EditText editAge = (EditText) findViewById(R.id.edit_age);
		View tip3 = findViewById(R.id.text_tip3);
		tip3.setVisibility(View.GONE);
		if (TextUtils.isEmpty(editAge.getText().toString())) {
			tip3.setVisibility(View.VISIBLE);
			isValid = false;
		}
		// 学历
		EditText editEducation = (EditText) findViewById(R.id.edit_education);
		View tip4 = findViewById(R.id.text_tip4);
		tip4.setVisibility(View.GONE);
		if (TextUtils.isEmpty(editEducation.getText().toString())) {
			tip4.setVisibility(View.VISIBLE);
			isValid = false;
		}
		// 电话
		EditText editPhone = (EditText) findViewById(R.id.edit_phone);
		View tip5 = findViewById(R.id.text_tip5);
		tip5.setVisibility(View.GONE);
		if (TextUtils.isEmpty(editPhone.getText().toString())) {
			tip5.setVisibility(View.VISIBLE);
			isValid = false;
		}
		// --------提交网络
		if (!isValid) {
			return;
		}
		if(!isOpenNet()){
			showToast(getString(R.string.net_err));
			return ;
		}
		showProgressDialog();
		mPackUp.name = editName.getText().toString();
		mPackUp.sex = editGender.getText().toString();
		mPackUp.age = editAge.getText().toString();
		mPackUp.edu = editEducation.getText().toString();
		mPackUp.phone = editPhone.getText().toString();
		PcsDataDownload.addDownload(mPackUp);
	}

	/**
	 * 对话框点击事件
	 */
	private DialogInterface.OnClickListener mDialogOnClick = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case DialogInterface.BUTTON_POSITIVE:
				// 清空输入框
				clearEdit();
				break;
			case DialogInterface.BUTTON_NEGATIVE:
				break;
			}
		}
	};

	private OnClickListener mOnClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_reset:
				// 点击重置
				clickReset();
				break;
			case R.id.btn_commit:
				// 点击提交
				clickCommit();
				break;
			case R.id.btn_right:
				// 点击分享
				clickShare();
			}
		}
	};

	/**
	 * 输入框Action键监听
	 */
	private OnEditorActionListener mEditorListener = new OnEditorActionListener() {
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			// 点击提交
			clickCommit();
			return false;
		}
	};

	/**
	 * 广播接收
	 */
	private PcsDataBrocastReceiver mReceiver = new PcsDataBrocastReceiver() {
		@Override
		public void onReceive(String nameStr, String errorStr) {
			if (!mPackUp.getName().equals(nameStr)) {
				return;
			}
            mPackDown = (PackCompetitionEntryDown) PcsDataManager.getInstance().getNetPack(nameStr);
            if(mPackDown == null) {
                return ;
            }
			if (!"1".equals(mPackDown.type) && !"2".equals(mPackDown.type)) {
				// 提示失败
				Toast.makeText(ActivityCompetitionEntry.this,
						R.string.competition_error, Toast.LENGTH_SHORT).show();
				// 取消等待框
				ActivityCompetitionEntry.this.dismissProgressDialog();
				return;
			}
			// 禁止所有控件
			disableAll();
			if ("1".equals(mPackDown.type)) {
				// 提示报名成功
				Toast.makeText(ActivityCompetitionEntry.this,
						R.string.competition_succ, Toast.LENGTH_SHORT).show();
			} else if ("2".equals(mPackDown.type)) {
				// 提示报名信息修改
				Toast.makeText(ActivityCompetitionEntry.this,
						R.string.competition_alter, Toast.LENGTH_SHORT).show();
			}
			// 取消等待框
			ActivityCompetitionEntry.this.dismissProgressDialog();
		}
	};
}
