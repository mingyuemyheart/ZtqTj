package com.pcs.ztqtj.view.activity.life.travel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.AdapterTravelBookmarks;
import com.pcs.ztqtj.control.tool.MyConfigure;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.view.activity.citylist.ActivitySelectTravelViewList;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCity;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalTravelViewInfo;
import com.pcs.lib_ztqfj_v2.model.pack.net.week.PackTravelWeekDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.week.PackTravelWeekUp;

import java.util.ArrayList;
import java.util.List;

/**
 * 新版旅游气象
 * 
 * @author tya
 *
 */
public class ActivityTravelViewNew extends FragmentActivityZtqBase implements
		OnItemClickListener, OnClickListener {

	// UI
	/**
	 * 顶部图片导航
	 */
	private ViewPager vp = null;

	/**
	 * 收藏景点列表
	 */
	private GridView gridview = null;

	/**
	 * 景点搜索按钮
	 */
	private Button btnSearch = null;

	// 数据
	/**
	 * 保存的城市信息
	 */
	private List<PackTravelWeekDown> listCityInfo = new ArrayList<PackTravelWeekDown>();

	/**
	 * 一周天气上传包
	 */
	private PackTravelWeekUp packTravelWeekUp;

	/**
	 * 收藏的城市适配器
	 */
	private AdapterTravelBookmarks adapterBookmarks = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_travel_view_new);
		setTitleText("旅游气象");
		initView();
		initEvent();
		initData();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		vp = (ViewPager) findViewById(R.id.viewpager);
		gridview = (GridView) findViewById(R.id.gridview);
		btnSearch = (Button) findViewById(R.id.btn_search);
	}

	/**
	 * 初始化事件
	 */
	private void initEvent() {
		gridview.setOnItemClickListener(this);
		btnSearch.setOnClickListener(this);
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
	}

	/**
	 * 向列表中添加城市并刷新
	 * 
	 * @param cityInfo
	 */
	private void addcityinfo(PackLocalCity cityInfo) {
		try {
			for (int i = 0; i < listCityInfo.size(); i++) {
				if (listCityInfo.get(i).cityId.equals(cityInfo.ID)) {
					// 列表中已经存在值
					return;
				}
			}
			// 不存在则保存，并添加
			PackTravelWeekDown packDown = new PackTravelWeekDown();
			packDown.cityName = cityInfo.NAME;
			packDown.cityId = cityInfo.ID;
			listCityInfo.add(packDown);
			saveLocalTravelViewInfo(cityInfo);
			reqNet(cityInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 网络请求数据
	 * 
	 * @param cityInfo
	 */
	private void reqNet(PackLocalCity cityInfo) {

		if(!isOpenNet()){
			showToast(getString(R.string.net_err));
			return ;
		}
		showProgressDialog();
		packTravelWeekUp = new PackTravelWeekUp();
		packTravelWeekUp.setCity(cityInfo);
        PackTravelWeekDown down = (PackTravelWeekDown) PcsDataManager.getInstance().getNetPack(packTravelWeekUp.getName());
		if (down == null) {
			// 判断是缓存数据中是否已经存在，不存在则网络请求取数据
			PcsDataDownload.addDownload(packTravelWeekUp);
		} else {
			// 取城市信息后解析数据
            down.cityId = cityInfo.ID;
            down.cityName = cityInfo.NAME;
			addCityInfoToListView(down);

		}
	}

	/**
	 * 添加城市信息
	 * 
	 * @param pack
	 */
	private void addCityInfoToListView(PackTravelWeekDown pack) {
		for (int i = 0; i < listCityInfo.size(); i++) {
			if (listCityInfo.get(i).cityId.equals(pack.cityId)) {
				listCityInfo.set(i, pack);
				adapterBookmarks.notifyDataSetChanged();
				break;
			}
		}
		dismissProgressDialog();
	}

	/**
	 * 保存城市
	 * 
	 * @param cityinfo
	 */
	private void saveLocalTravelViewInfo(PackLocalCity cityinfo) {
		try {
			PackLocalTravelViewInfo localcitylist = ZtqCityDB.getInstance()
					.getCurrentTravelViewInfo();
			for (int i = 0; i < localcitylist.localViewList.size(); i++) {
				if (localcitylist.localViewList.get(i).ID.equals(cityinfo.ID)) {
					return;
				}
			}
			localcitylist.localViewList.add(cityinfo);
			ZtqCityDB.getInstance().setCurrentTravelViewInfo(localcitylist);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case MyConfigure.RESULT_SELECT_TRAVELVIEW:
			if (resultCode == Activity.RESULT_OK) {
				PackLocalCity cityinfo = (PackLocalCity) data
						.getSerializableExtra("cityinfo");
				addcityinfo(cityinfo);
			} else {
				// finish();
			}
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_search:
			Intent intent = new Intent(this, ActivitySelectTravelViewList.class);
			startActivityForResult(intent, MyConfigure.RESULT_SELECT_TRAVELVIEW);
			break;
		}
	}

}
