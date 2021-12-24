package com.pcs.ztqtj.view.activity.citylist;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.AdapteTraveSelectCityExpandableListView;
import com.pcs.ztqtj.control.adapter.AdapterSelectTravelViewExpandList;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
import com.pcs.ztqtj.view.myview.mExpandableListView;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * 生活气象-旅游气象-选择景点（点击搜索）
 */
public class ActivitySelectTravelViewList extends FragmentActivityZtqBase {
	private EditText edit;
	private List<PackLocalCity> data;
	private mExpandableListView exList;
	private AdapterSelectTravelViewExpandList adapter;
	private ExpandableListView selectCityListview;
	private AdapteTraveSelectCityExpandableListView adapterSearch;
	private List<PackLocalCity> province;
	private HashMap<String, List<PackLocalCity>> datasMap = new HashMap<String, List<PackLocalCity>>();
	private List<PackLocalCity> viewsList;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitleText("选择景点");
		setContentView(R.layout.layout_select_travel);
		initView();
		initEvent();
		initData();
	}

    private void initView() {
		selectCityListview = (ExpandableListView) findViewById(R.id.ss_alertgridview);
		exList = (mExpandableListView) findViewById(R.id.cityListView);
		exList.setHeaderView(getLayoutInflater().inflate(
				R.layout.layout_citygroupitem, exList, false));
		edit = (EditText) findViewById(R.id.ss_alertedittext);
	}

	private void initEvent() {
		selectCityListview.setOnChildClickListener(new listChildOnClick());
		edit.addTextChangedListener(new TextChangrListener());
		exList.setOnChildClickListener(new ExpendListener());
		exList.setOnGroupClickListener(new ExpendChildListener());
	}

	private void initData() {
		viewsList = new ArrayList<PackLocalCity>();
		province = new ArrayList<PackLocalCity>();
		ZtqCityDB.getInstance().initTravels(getApplicationContext());
		List<PackLocalCity> provinceResource = ZtqCityDB.getInstance().getProvincesList();
		for (PackLocalCity city : provinceResource) {
			if (!city.NAME.equals("钓鱼岛")) {
                province.add(city);
                if(city.NAME.equals("天津")) {
                    int index = province.indexOf(city)+1;
                    PackLocalCity temp = new PackLocalCity();
                    temp.NAME = "A-Z";
                    province.add(index, temp);
                }
			}

		}
		viewsList = ZtqCityDB.getInstance().getAllViewsInfo();
		data = viewsList;
		
		adapterSearch = new AdapteTraveSelectCityExpandableListView(this);
		selectCityListview.setAdapter(adapterSearch);
		for (int i = 0; i < province.size(); i++) {
			List<PackLocalCity> infoList = ZtqCityDB.getInstance().getViewsByProcinceID(province.get(i).ID);
			datasMap.put(province.get(i).ID, infoList);
		}
		adapter = new AdapterSelectTravelViewExpandList(this, exList, province, datasMap);
		exList.setAdapter(adapter);
		exList.setSelectedGroup(0);
	}

	private class ExpendChildListener implements OnGroupClickListener {
		@Override
		public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
			if (adapter.getGroupClickStatus(groupPosition) == 0) {
				adapter.setGroupClickStatus(groupPosition, 1);
				parent.expandGroup(groupPosition);
				parent.setSelectedGroup(groupPosition);
			} else if (adapter.getGroupClickStatus(groupPosition) == 1) {
				adapter.setGroupClickStatus(groupPosition, 0);
				parent.collapseGroup(groupPosition);
			}
			if (groupPosition == 0 && adapter.isHasloaction()) {
				// onChoiceCity(locationCity, locationProvince);
			}
			return true;
		}
	}

	/**
	 * @author Z 扩展listview子选项点击事件
	 */
	private class ExpendListener implements OnChildClickListener {
		@Override
		public boolean onChildClick(ExpandableListView parent, View v,
				int groupPosition, int childPosition, long id) {
			PackLocalCity cityinfo = datasMap.get(
					province.get(groupPosition).ID).get(childPosition);
			// 城市选择后的事件处理
			Intent intent = new Intent();
			intent.putExtra("cityinfo", cityinfo);
			setResult(RESULT_OK, intent);
			finish();
			return false;
		}
	}

	/**
	 * @author Z 文本输入框改变事件监听
	 */
	private class TextChangrListener implements TextWatcher {
		public void afterTextChanged(Editable arg0) {
		}

		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
		}

		public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
			if (null != cs && cs.length() != 0) {
				exList.setVisibility(View.GONE);
				selectCityListview.setVisibility(View.VISIBLE);
				//arrange(cs);
				adapterSearch.setSearchStr(cs.toString());
				// 控制展开
				for (int i = 0; i < adapterSearch.getGroupCount(); i++) {
					if (i == adapterSearch.getGroupCount() - 1) {
						// 展开最后一个
						selectCityListview.expandGroup(i);
					} else {
						selectCityListview.collapseGroup(i);
					}
				}
			} else {
				exList.setVisibility(View.VISIBLE);
				if (selectCityListview != null)
					selectCityListview.setVisibility(View.GONE);
				adapterSearch.notifyDataSetChanged();
			}
		}
	}
	
	private class listChildOnClick implements OnChildClickListener {

		@Override
		public boolean onChildClick(ExpandableListView parent, View v,
				int groupPosition, int childPosition, long id) {
			PackLocalCity cityinfo = (PackLocalCity) adapterSearch.getChild(groupPosition, childPosition);
			// 城市选择后的事件处理
			Intent intent = new Intent();
			intent.putExtra("cityinfo", cityinfo);
			setResult(RESULT_OK, intent);
			finish();
			return false;
		}
		
	}

	Comparator<PackLocalCity> comparator = new Comparator<PackLocalCity>() {
		public int compare(PackLocalCity s1, PackLocalCity s2) {
			return s1.PINGYIN.compareTo(s2.PINGYIN);
		}
	};

}
