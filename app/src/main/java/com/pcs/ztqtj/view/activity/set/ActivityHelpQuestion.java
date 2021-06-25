package com.pcs.ztqtj.view.activity.set;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.net.GuideBean;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackUseGuideDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackUseGuideUp;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.AdapterUseGuide;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;

/**
 * 设置-功能导航
 */
public class ActivityHelpQuestion extends FragmentActivityZtqBase{

	private ListView listview;
	private PackUseGuideDown down;
	private MyReceiver receiver = new MyReceiver();
	private TextView null_data;
	private AdapterUseGuide adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_useguide);
		PcsDataBrocastReceiver.registerReceiver(this, receiver);
		initView();
		initEvent();
		initData();
	}

	private void initView() {
		listview = (ListView) findViewById(R.id.listview);
		null_data=(TextView) findViewById(R.id.null_data);
	}

	private void initEvent() {
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				GuideBean bean=(GuideBean) adapter.getItem(position);
				IntentNextActivity(bean.html_url,bean.title);
			}
		});
	}

	private void IntentNextActivity(String url,String title) {
		Intent intent = new Intent(ActivityHelpQuestion.this, ActivityGuideDetail.class);
		intent.putExtra("url", url);
		intent.putExtra("title", "功能导航");
        intent.putExtra("is_show_share", "0");
		startActivity(intent);
	}
	private void initData() {
		setTitleText("功能导航");
		adapter=new AdapterUseGuide(ActivityHelpQuestion.this);
		listview.setAdapter(adapter);
		req();
	}

	private void req() {
		if(!isOpenNet()){
			showToast(getString(R.string.net_err));
			return ;
		}
		down = new PackUseGuideDown();
		showProgressDialog();
		PackUseGuideUp up=new PackUseGuideUp();
		PcsDataDownload.addDownload(up);
		down = (PackUseGuideDown) PcsDataManager.getInstance().getNetPack(up.getName());
		if (down!=null) {
			dealWithData();
		}
	}

	/**
	 * 数据更新广播接收
	 */
	private class MyReceiver extends PcsDataBrocastReceiver {
		@Override
		public void onReceive(String name, String error) {
				if (name.equals(PackUseGuideUp.NAME)) {
					down = (PackUseGuideDown) PcsDataManager.getInstance().getNetPack(name);
					if (down!=null) {
						dealWithData();
					}
				}
		}
	}

	private void dealWithData() {
		dismissProgressDialog();
		adapter.setData(down.guideList);
		if(down.guideList==null||down.guideList.size()==0){
			null_data.setVisibility(View.VISIBLE);
		}else{
			null_data.setVisibility(View.GONE);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		this.unregisterReceiver(receiver);
	}
}
