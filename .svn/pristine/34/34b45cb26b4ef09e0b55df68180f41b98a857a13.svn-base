package com.pcs.ztqtj.view.activity.set;

import android.os.Bundle;
import android.widget.ListView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.AdapterSms;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackSMSDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackSMSDown.WeatherSMSBean;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackSMSUp;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Z 气象短信
 */
public class ActivitySms extends FragmentActivityZtqBase {

	private ListView smsLisview;
	private List<WeatherSMSBean> dataList;
	private AdapterSms adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PcsDataBrocastReceiver.registerReceiver(ActivitySms.this, receiver);
		Bundle bundle = getIntent().getExtras();
		setTitleText(bundle.getString("title"));
		setContentView(R.layout.activity_sms_listview);
		initView();
		initData();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);

	}

	private MyReceiver receiver = new MyReceiver();

	private void initData() {
		reqNet();
		// Map<String, Object> mapa = new HashMap<String, Object>();
		// mapa.put("color", R.color.mblue);
		// mapa.put("t", "[移动电信]");
		// mapa.put("c", "按x键做啥？\n\r按y键做啥，\n\r最后做啥自己想");
		// Map<String, Object> mapb = new HashMap<String, Object>();
		// mapb.put("color", R.color.red);
		// mapb.put("t", "[铁通]");
		// mapb.put("c", "按x键做啥？\n\r按y键做啥，\n\r最后做啥自己想");
		// Map<String, Object> mapc = new HashMap<String, Object>();
		// mapc.put("color", R.color.Black);
		// mapc.put("t", "[网络]");
		// mapc.put("c", "按x键做啥？\n\r按y键做啥，\n\r最后做啥自己想");
		// dataList.add(mapa);
		// dataList.add(mapb);
		// dataList.add(mapc);

	}

	private void initView() {
		smsLisview = (ListView) findViewById(R.id.mylistviw);
		dataList = new ArrayList<WeatherSMSBean>();
		adapter = new AdapterSms(ActivitySms.this, dataList);
		smsLisview.setAdapter(adapter);
	}

	private PackSMSUp uppack;

	private void reqNet() {

		if(!isOpenNet()){
			showToast(getString(R.string.net_err));
			return ;
		}
		// 上传包
		uppack = new PackSMSUp();
		// 请求网络
		PcsDataDownload.addDownload(uppack);
	}

	private PackSMSDown pack = new PackSMSDown();

	private class MyReceiver extends PcsDataBrocastReceiver {
		@Override
		public void onReceive(String name, String errorStr) {
			if (PackSMSUp.NAME.equals(name)) {
				pack = (PackSMSDown) PcsDataManager.getInstance().getNetPack(name);
				if (pack == null) {
					return;
				}

				dataList.clear();
				for (int i = 0; i < pack.smsDataList.size(); i++) {
					dataList.add(pack.smsDataList.get(i));
				}
				adapter.notifyDataSetChanged();
			}
		}
	}

}
