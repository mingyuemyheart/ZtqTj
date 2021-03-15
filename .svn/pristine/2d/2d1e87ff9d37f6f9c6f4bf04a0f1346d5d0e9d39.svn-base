package com.pcs.ztqtj.view.activity.help;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.AdapterHelpList;
import com.pcs.ztqtj.control.tool.MyConfigure;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
import com.pcs.ztqtj.view.activity.service.AcitvityServeLogin;
import com.pcs.ztqtj.view.dialog.DialogFactory.DialogListener;
import com.pcs.ztqtj.view.dialog.DialogTwoButton;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.net.service.PackServiceOrgSearchDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.service.PackServiceOrgSearchUp;

import java.util.ArrayList;
import java.util.List;

/**
 * 帮助
 * @author chenjh
 */
public class ActivityHelp extends FragmentActivityZtqBase {
	private MyReceiver receiver = new MyReceiver();
	private PackServiceOrgSearchUp packServiceOrgSearchUp = new PackServiceOrgSearchUp();
	private ListView listview;
	private AdapterHelpList adapter;
	private List<String> listData;
	

	private DialogTwoButton myDialog;
	private TextView messageTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.helplayout);
		setTitleText(getString(R.string.helpacitvity));
		init();
		initListener();
	}

	public void init() {
		listview = (ListView) findViewById(R.id.mylistviw);
		listData = new ArrayList<String>();
		listData.add("关于专项服务栏目");
		listData.add("专项服务产品使用权限");
		listData.add("修改密码");
//		listData.add("气象服务单位联系方式");
		adapter = new AdapterHelpList(getApplication(), listData);
		listview.setAdapter(adapter);
		// 注册广播接收
		PcsDataBrocastReceiver.registerReceiver(this, receiver);
		getServiceOrgSearchList("");
	}

	public void initListener() {
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				case 0:
					gotoAcitvity(ActivityAboutWeatherServe.class,
							listData.get(position), position);
					break;
				case 1:
					gotoAcitvity(ActivityAboutWeatherServe.class,
							listData.get(position), position);
					break;
				case 2:
					if (TextUtils.isEmpty(ZtqCityDB.getInstance().getMyInfo().user_id)) {
						showLoginTipsDialog();
						return;
					}
					gotoAcitvity(ActivityChangePwd.class,
							listData.get(position), position);
					break;
				default:
//					gotoAcitvity(ActivityAboutWeatherServe.class,
//							listData.get(position), position);
					break;
				}
			}
		});
	}

	/**
	 * 跳转到下一级页面
	 * 
	 * @param goactivity
	 *            页面
	 * @param title
	 *            页面标题
	 */
	private void gotoAcitvity(Class goactivity, String title, int position) {
		Intent intent = new Intent();
		intent.setClass(getApplication(), goactivity);
		intent.putExtra("title", title);
		intent.putExtra("position", position);
		startActivity(intent);
	}

	/**
	 * 手动搜索气象服务单位
	 * 
	 * @param sel_org
	 */
	private void getServiceOrgSearchList(String sel_org) {
		if(!isOpenNet()){
			showToast(getString(R.string.net_err));
			return ;
		}
		if (packServiceOrgSearchUp == null) {
			packServiceOrgSearchUp = new PackServiceOrgSearchUp();
		}
		packServiceOrgSearchUp.sel_org = sel_org;

		PcsDataDownload.addDownload(packServiceOrgSearchUp);
	}

	/**
	 * 数据更新广播接收
	 * 
	 */
	private class MyReceiver extends PcsDataBrocastReceiver {

		@Override
		public void onReceive(String name, String error) {
			if (packServiceOrgSearchUp.getName().equals(name)) {
				PackServiceOrgSearchDown packServiceOrgSearchDown= (PackServiceOrgSearchDown) PcsDataManager.getInstance().getNetPack(name);
			}
		}

	}
	
	private void showLoginTipsDialog() {

		if (myDialog == null) {
			View view = LayoutInflater.from(ActivityHelp.this).inflate(
					R.layout.dialog_message, null);
			messageTextView = (TextView) view.findViewById(R.id.dialogmessage);

			messageTextView.setText(R.string.text_change_pwd_tips);
			myDialog = new DialogTwoButton(ActivityHelp.this, view, "登录",
					"返回", new DialogListener() {
						@Override
						public void click(String str) {
							myDialog.dismiss();
							if (str.equals("登录")) {
								Intent intent = null;
								intent = new Intent(ActivityHelp.this, AcitvityServeLogin.class);
								startActivityForResult(intent, MyConfigure.RESULT_HELP_VIEW);
							}
						}
					});
			myDialog.setTitle("天津气象提示");
			messageTextView.setTextColor(getResources().getColor(R.color.text_color));
			myDialog.showCloseBtn();
		}

		myDialog.show();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		this.unregisterReceiver(receiver);
	}

}
