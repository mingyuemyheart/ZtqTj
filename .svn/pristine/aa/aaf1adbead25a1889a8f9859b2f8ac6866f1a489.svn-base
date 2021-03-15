package com.pcs.ztqtj.view.activity.set;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.widget.ListView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.adapter_set.AdapterCauseExplain;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackSstqDown;

/**
 * @author Z
 *业务说明
 */
public class ActivityCauseExplan extends FragmentActivityZtqBase {
	private AdapterCauseExplain adapter;
	private ListView lv;
	private List<Map<String,String>> listData;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle=getIntent().getExtras();
		PackSstqDown ssta=(PackSstqDown) bundle.getSerializable("city");
		setTitleText(bundle.getString("title"));
		setContentView(R.layout.activity_cause_explan);
		init();
		initData();
	}

	private void initData() {
		listData=new ArrayList<Map<String,String>>();
		for (int i = 0; i < 6; i++) {
			Map<String,String> map=new HashMap<String, String>();
			map.put("t", "移动短信产品信息介绍");
			map.put("c", "201.长途优惠2011.12593长途新干线，长途定向优惠2013，闲时长途优惠，三元国内长途包月，五元国内长途包，十元国内长途包，漫游优惠，谁内漫游包5元宝月，周末轻松聊，亲情家园，添加亲情号码。漫游优惠，谁内漫游包5元宝月，周末轻松聊，亲情家园，添加亲情号码");
			listData.add(map);
		}
		adapter=new AdapterCauseExplain(getApplication(), listData);
		lv.setAdapter(adapter);
	}

	private void init() {
		lv = (ListView) findViewById(R.id.listview);
	}

}
