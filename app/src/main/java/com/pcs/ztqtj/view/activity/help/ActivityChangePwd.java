package com.pcs.ztqtj.view.activity.help;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.net.service.PackServiceChangePwdDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.service.PackServiceChangePwdUp;
import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;

/**
 * 修改密码
 * 
 * @author chenjh
 * 
 */
public class ActivityChangePwd extends FragmentActivityZtqBase implements
		View.OnClickListener {

	private EditText currPwdEt;
	private EditText newPwdEt;
	private EditText rePwdEt;

	private ImageView currPwdDel; // 删除旧密码
	private ImageView newPwdDel; // 删除新密码
	private ImageView reNewPwdDel; // 删除确认新密码

	private Button saveBtn;
	/** 新密码 **/
	public String newpwd = "";
	/** 旧密码 **/
	public String oldpwd = "";
	/** 确认密码 **/
	public String okpwd = "";
	/** 手机号 **/
	public String mobile = "";

	private MyReceiver receiver = new MyReceiver();
	private PackServiceChangePwdUp packServiceChangePwdUp = new PackServiceChangePwdUp();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitleText("修改密码");
		setContentView(R.layout.help_change_pwd);
		initView();
		initEvent();
		PcsDataBrocastReceiver.registerReceiver(this, receiver);
	}

	private void initEvent() {
		currPwdDel.setOnClickListener(this);
		newPwdDel.setOnClickListener(this);
		reNewPwdDel.setOnClickListener(this);
		saveBtn.setOnClickListener(this);
	}

	private void initView() {
		currPwdEt = (EditText) findViewById(R.id.curr_pwd_et);
		newPwdEt = (EditText) findViewById(R.id.new_pwd_et);
		rePwdEt = (EditText) findViewById(R.id.repwd_et);

		currPwdDel = (ImageView) findViewById(R.id.del_curr_pwd_iv);
		newPwdDel = (ImageView) findViewById(R.id.del_new_pwd_iv);
		reNewPwdDel = (ImageView) findViewById(R.id.del_repwd_iv);

		saveBtn = (Button) findViewById(R.id.save_btn);

	}

	protected void updatePwd() {
		oldpwd = currPwdEt.getText().toString().trim();
		newpwd = newPwdEt.getText().toString().trim();
		okpwd = rePwdEt.getText().toString().trim();

		if (TextUtils.isEmpty(oldpwd)) {
			Toast.makeText(ActivityChangePwd.this, "密码不能为空", Toast.LENGTH_SHORT)
					.show();
			return;
		} else if (TextUtils.isEmpty(newpwd)) {
			Toast.makeText(ActivityChangePwd.this, "新密码不能为空 ",
					Toast.LENGTH_SHORT).show();
			return;
		} else if (TextUtils.isEmpty(okpwd)) {
			Toast.makeText(ActivityChangePwd.this, "确认密码不能为空 ",
					Toast.LENGTH_SHORT).show();
			return;
		} else if (!okpwd.equals(newpwd)) {
			Toast.makeText(ActivityChangePwd.this, "新密码和确认密码不一致 ",
					Toast.LENGTH_SHORT).show();
			return;
		}
		if(!isOpenNet()){
			showToast(getString(R.string.net_err));
			return ;
		}
		if (packServiceChangePwdUp == null) {
			packServiceChangePwdUp = new PackServiceChangePwdUp();
		}
		packServiceChangePwdUp.mobile = MyApplication.MOBILE;
		packServiceChangePwdUp.oldpwd = oldpwd;
		packServiceChangePwdUp.newpwd = newpwd;
		packServiceChangePwdUp.okpwd = okpwd;
        PcsDataManager.getInstance().removeLocalData(packServiceChangePwdUp.getName());
		PcsDataDownload.addDownload(packServiceChangePwdUp);
		saveBtn.setClickable(false);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.del_curr_pwd_iv:
			currPwdEt.setText("");
			break;
		case R.id.del_new_pwd_iv:
			newPwdEt.setText("");
			break;
		case R.id.del_repwd_iv:
			rePwdEt.setText("");
			break;
		case R.id.save_btn:
			updatePwd();
			break;
		}
	}

	/**
	 * 数据更新广播接收
	 * 
	 */
	private class MyReceiver extends PcsDataBrocastReceiver {

		@Override
		public void onReceive(String name, String error) {

			if (packServiceChangePwdUp.getName().equals(name)) {
                PackServiceChangePwdDown packServiceChangePwdDown = (PackServiceChangePwdDown) PcsDataManager.getInstance().getNetPack(name);
				if (packServiceChangePwdDown == null) {
					return;
				}

				int key = Integer.valueOf(packServiceChangePwdDown.type);
				switch (key) {
				case 1:
					Toast.makeText(ActivityChangePwd.this,
							packServiceChangePwdDown.msg, Toast.LENGTH_SHORT)
							.show();
					currPwdEt.setText("");
					newPwdEt.setText("");
					rePwdEt.setText("");
				default:
					newPwdEt.setText("");
					rePwdEt.setText("");
					Toast.makeText(ActivityChangePwd.this,
							packServiceChangePwdDown.msg, Toast.LENGTH_SHORT)
							.show();
					break;
				}
				saveBtn.setClickable(true);
			}
		}
	}

	private void finishView() {
		setResult(RESULT_OK);
		finish();
	}

	@Override
	public void onBackPressed() {
		finishView();
		super.onBackPressed();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		this.unregisterReceiver(receiver);
	}
}
