package com.pcs.ztqtj.view.activity.set;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.ListView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.adapter_set.AdapterProjectManagemer;
import com.pcs.ztqtj.control.tool.LocalDataHelper;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;

import java.util.ArrayList;
import java.util.List;

/**
 * 监测预报-栏目管理
 */
public class ActivityProgramerManager extends FragmentActivityZtqBase {
	private ListView listview;
	private List<String> listdata;
	private AdapterProjectManagemer adapter;
	private String[] product_list;
    private String[] product_analysis;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitleText("产品管理");
		setContentView(R.layout.activity_programermanager);
		initView();
		initData();
	}

	private void initView() {
		listview = (ListView) findViewById(R.id.mylistviw);
	}

	private void initData() {
		product_list = getResources().getStringArray(R.array.product_list);
        product_analysis=getResources().getStringArray(R.array.product_analysis);
		listdata = new ArrayList<>();
		listdata.clear();
		for (int i = 0; i < product_list.length; i++) {
			String[] item = product_list[i].split(",");
			listdata.add(item[0]);
		}
		for (int j=0;j<product_analysis.length;j++){
			String[] item = product_analysis[j].split(",");
            listdata.add(item[0]);
        }
		adapter = new AdapterProjectManagemer(ActivityProgramerManager.this, listdata);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(onItemClickListener);
	}

	OnItemClickListener onItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			final String itemName = listdata.get(position);
			if("整点实况".equals(itemName)||"网格预报".equals(itemName)){
				return;
			}
			boolean boolean1 = !LocalDataHelper.getProductValue(ActivityProgramerManager.this, itemName);
			LocalDataHelper.saveProductValue(ActivityProgramerManager.this, itemName, boolean1);
			CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
			checkBox.setChecked(boolean1);
		}
	};

}
