package com.pcs.ztqtj.view.activity.lifenumber;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.life_number.AdapterLifeNumberEdit;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalLife;

/**
 * 生活指数编辑
 * 
 * @author JiangZy
 * 
 */
public class ActivityLifeNumberEdit extends FragmentActivityZtqBase {
	private AdapterLifeNumberEdit mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 向右退出
		setBackDirection(BackDirection.BACK_RIGHT);
		// 标题
		setTitleText(R.string.more);
		setContentView(R.layout.activity_life_number_edit);
		createImageFetcher();
		// 列表
		ListView listView = (ListView) findViewById(R.id.list);
		mAdapter = new AdapterLifeNumberEdit(ActivityLifeNumberEdit.this, getImageFetcher());
		listView.setAdapter(mAdapter);
		listView.setOnItemClickListener(new MyItemClickListener());
	}

	private class MyItemClickListener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			mAdapter.checkItem(position);
			PackLocalLife.getInstance().setDefault(ActivityLifeNumberEdit.this,
					true);
		}
	}
}