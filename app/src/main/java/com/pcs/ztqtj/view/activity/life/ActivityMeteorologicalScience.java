package com.pcs.ztqtj.view.activity.life;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.AdapterLifeGridView;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
import com.pcs.ztqtj.view.myview.MyGridView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 生活气象-气象科普
 */
public class ActivityMeteorologicalScience extends FragmentActivityZtqBase {

	private MyGridView channel_gridView;
	private ImageView head_iv, bottom_iv;
	private List<Map<String, Object>> data = new ArrayList<>();

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.info_layout);
		setTitleText("气象科普");
		initView();
		initEvent();
		initData();
	}

	private void initView() {
		channel_gridView = (MyGridView) findViewById(R.id.channel_gridView);
		head_iv = (ImageView) findViewById(R.id.head_iv);
		bottom_iv = (ImageView) findViewById(R.id.bottom_iv);
	}

	private void initEvent() {
		head_iv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(ActivityMeteorologicalScience.this, ActivityChannelList.class);
				intent.putExtra("title", "气候解读");
				intent.putExtra("channel_id", "1");
				intent.putExtra("interfaceUrl", "qxkp");
				startActivity(intent);
			}
		});
		bottom_iv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(ActivityMeteorologicalScience.this, ActivityImageDisaster.class);
				intent.putExtra("title", "识图防灾");
				intent.putExtra("channel_id", "6");
				intent.putExtra("interfaceUrl", "qxkp");
				startActivity(intent);
			}
		});
		channel_gridView.setOnItemClickListener(itemListener);
	}

	private void initData() {
		Map<String, Object> a = new HashMap<>();
		a.put("t", "气象美图");
		a.put("i", R.drawable.channel_100008);
		a.put("id", "3");
		Map<String, Object> b = new HashMap<>();
		b.put("t", "节气养生");
		b.put("i", R.drawable.channel_100006);
		b.put("id", "2");
		Map<String, Object> c = new HashMap<>();
		c.put("t", "天文地理");
		c.put("i", R.drawable.channel_100007);
		c.put("id", "5");
		Map<String, Object> d = new HashMap<>();
		d.put("t", "气象百科");
		d.put("i", R.drawable.channel_100009);
		d.put("id", "4");

		data.add(a);// 气象美图
		data.add(b);// 节气养生
		data.add(c);// 天文地理
		data.add(d);// 气象百科

		channel_gridView.setAdapter(new AdapterLifeGridView(ActivityMeteorologicalScience.this, data));
	}

	private AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Map<String, Object> map = data.get(position);
			Intent intent = new Intent(ActivityMeteorologicalScience.this, ActivityChannelList.class);
			intent.putExtra("title", map.get("t").toString());
			intent.putExtra("channel_id", map.get("id").toString());
			intent.putExtra("interfaceUrl", "qxkp");
			startActivity(intent);
		}
	};

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

}
