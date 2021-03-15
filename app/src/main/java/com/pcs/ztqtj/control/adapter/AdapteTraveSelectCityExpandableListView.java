package com.pcs.ztqtj.control.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.model.ZtqCityDB;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCity;

import java.util.ArrayList;
import java.util.List;

public class AdapteTraveSelectCityExpandableListView extends
		BaseExpandableListAdapter {

	// 临时ID
	protected final String ID_TEMP = "ID_TEMP";

	private List<PackLocalCity> mListProvice = new ArrayList<PackLocalCity>();
	private List<PackLocalCity> mListCity = new ArrayList<PackLocalCity>();

	private Context context = null;

	public AdapteTraveSelectCityExpandableListView(Context context) {
		this.context = context;
	}

	@Override
	public int getGroupCount() {
		return mListProvice.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		PackLocalCity pack = mListProvice.get(groupPosition);
		if (pack.ID.equals(ID_TEMP)) {
			return mListCity.size();
		}

		List<PackLocalCity> listCity = ZtqCityDB.getInstance()
		.getViewsByProcinceID(pack.ID);
		if (listCity == null) {
			return 0;
		}
		return listCity.size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return mListProvice.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		PackLocalCity pack = mListProvice.get(groupPosition);
		if (pack.ID.equals(ID_TEMP)) {
			return mListCity.get(childPosition);
		}

		List<PackLocalCity> listCity = ZtqCityDB.getInstance()
				.getViewsByProcinceID(pack.ID);
		if (listCity == null) {
			return null;
		}
		return listCity.get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return groupPosition * 10000 + childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		GroupHolder holder = null;
		if (convertView == null) {
			holder = new GroupHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.layout_citygroupitem, null);
			holder.tv = (TextView) convertView.findViewById(R.id.title_name);
			holder.image = (ImageView) convertView.findViewById(R.id.expandimage);
			convertView.setTag(holder);
		} else {
			holder = (GroupHolder) convertView.getTag();
		}
		holder.tv.setText(mListProvice.get(groupPosition).NAME);
		if (groupPosition != 0) {
			if (!isExpanded) {
				holder.image.setImageResource(R.drawable.expand_close);
			} else
				holder.image.setImageResource(R.drawable.expand_open);
		} else {
			holder.image.setImageResource(R.drawable.expand_close);
		}
		holder.image.setImageResource(R.drawable.expand_close);
//		holder.image.setVisibility(View.VISIBLE);
		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		ChildHolder holder = null;
		if (convertView == null) {
			holder = new ChildHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.layout_citychilditem, null);
			holder.tv = (TextView) convertView.findViewById(R.id.name);
			convertView.setTag(holder);
		} else {
			holder = (ChildHolder) convertView.getTag();
		}
		PackLocalCity pack = (PackLocalCity) getChild(groupPosition,
				childPosition);
		PackLocalCity packProvince = ZtqCityDB.getInstance()
				.getProvinceById(pack.PARENT_ID);

		holder.tv.setText(packProvince.NAME + " - " + pack.NAME);

		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	/**
	 * 设置搜索字符串
	 *
	 * @param str
	 */
	public void setSearchStr(String str) {
		// 省份
		mListProvice = ZtqCityDB.getInstance().searchProvince(str);
		// 城市
		mListCity = ZtqCityDB.getInstance().searchTrave(str);
		if(mListCity.size() > 0) {
			// 插入搜索城市
			PackLocalCity pack = new PackLocalCity();
			pack.ID = ID_TEMP;
			pack.NAME = "景点";
			mListProvice.add(pack);
		}
		notifyDataSetChanged();
	}

	private class GroupHolder {
		TextView tv;
		ImageView image;
	}

	private class ChildHolder {
		TextView tv;
	}

}
