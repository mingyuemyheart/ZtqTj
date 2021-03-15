package com.pcs.ztqtj.view.fragment.weatherflood;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.waterflood.AdapterWaterFloodList;
import com.pcs.ztqtj.view.activity.product.waterflood.ActivityWaterLevelInfo;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.net.waterflood.PackReservoirWaterInfoUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.waterflood.PackRiverWaterInfoUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.waterflood.PackWaterInfoDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.waterflood.PackWaterInfoDown.ItemTimeInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Z 列表展示
 */
public class FragmentWeatherList extends Fragment {
	private ListView weather_flood_list;
	private ActivityWaterLevelInfo activity;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = (ActivityWaterLevelInfo) activity;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fra_weather_flood_list, null);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		weather_flood_list = (ListView) getActivity().findViewById(R.id.weather_flood_list);
		initData();
	}

	private List<ItemTimeInfo> listdata;
	private ItemTimeInfo itemTitle;
	private PackWaterInfoDown mPackDown;

	@SuppressWarnings("unused")
	private void initData() {
		mPackDown = new PackWaterInfoDown();
		itemTitle = new PackWaterInfoDown().new ItemTimeInfo();
		itemTitle.detail_hour = "时间";
		itemTitle.water = "水位m";
		listdata = new ArrayList<ItemTimeInfo>();
		listdata.add(itemTitle);
		AdapterWaterFloodList adapter = new AdapterWaterFloodList(listdata);
		weather_flood_list.setAdapter(adapter);
		// 取哪个数据？
		String json;
		if (activity.getButtonSTATUS() == 1) {
			// 水道水位
            mPackDown = (PackWaterInfoDown) PcsDataManager.getInstance().getNetPack(PackRiverWaterInfoUp.NAME +  "#" + activity.station_id);
		} else {
//			水库
            mPackDown = (PackWaterInfoDown) PcsDataManager.getInstance().getNetPack(PackReservoirWaterInfoUp.NAME + "#" + activity.station_id);
		}
		if (mPackDown == null) {
		} else {
			// 加载数据
			listdata.addAll(mPackDown.riverList);
			adapter.notifyDataSetChanged();
		}
	}

}
