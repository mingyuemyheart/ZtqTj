package com.pcs.ztqtj.control.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCity;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.view.myview.mExpandableListView;
import com.pcs.ztqtj.view.myview.mExpandableListView.HeaderAdapter;

import java.util.HashMap;
import java.util.List;

/**
 * @author 亲情城市列表适配器
 */
public class AdapterSelectAreaExpandList extends BaseExpandableListAdapter implements HeaderAdapter {
	private mExpandableListView listView;
	private Context context;
	private ListViewChildHolder lvchildholder;
	private ListViewGroupHolder gpholder;
	private HashMap<String, List<PackLocalCity>> datasMap;
	private List<PackLocalCity> areainfo;
	private boolean hasloaction = false;

	public boolean isHasloaction() {
		return hasloaction;
	}

	public AdapterSelectAreaExpandList(Context context, mExpandableListView listView, List<PackLocalCity> groupData,
			HashMap<String, List<PackLocalCity>> datasMap) {
		this.context = context;
		this.listView = listView;
		this.areainfo = groupData;
		this.datasMap = datasMap;
	}

	public class ListViewChildHolder {
		public View convertView;
		public TextView name;
	}

	public class ListViewGroupHolder {
		public View convertView;
		public TextView title_name;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {

		return childPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		if (null == convertView) {
			lvchildholder = new ListViewChildHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.layout_citychilditem, null);
			lvchildholder.convertView = convertView;
			lvchildholder.name = (TextView) convertView.findViewById(R.id.name);
			convertView.setTag(lvchildholder);
		} else {
			lvchildholder = (ListViewChildHolder) convertView.getTag();
		}
		if (datasMap.size() > 0) {
			// if (name.contains("郯城")) {
			// name = "临沂-郯城";
			// } else if (name.contains("崇武")) {
			// name = "泉州-崇武";
			// }

			try {
				String cityName = datasMap.get(areainfo.get(groupPosition).ID).get(childPosition).SHOW_NAME;
				lvchildholder.name.setText(cityName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		try {
			if (areainfo != null && areainfo.get(groupPosition) != null && datasMap.get(areainfo.get(groupPosition).ID) != null)
				return datasMap.get(areainfo.get(groupPosition).ID).size();
			else
				return 0;
		} catch (Exception e) {
			return 0;
		}

	}

	@Override
	public Object getGroup(int groupPosition) {
		return areainfo.get(groupPosition).NAME;
	}

	@Override
	public int getGroupCount() {
		if (areainfo != null && areainfo.size() > 0) {
			return areainfo.size();
		} else {
			return 0;
		}
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		if (null == convertView) {
			gpholder = new ListViewGroupHolder();
			convertView = View.inflate(context, R.layout.layout_citygroupitem, null);
			gpholder.convertView = convertView;
			gpholder.title_name = (TextView) convertView.findViewById(R.id.title_name);
			convertView.setTag(gpholder);
		} else {
			gpholder = (ListViewGroupHolder) convertView.getTag();
		}
		if (areainfo.size() > 0) {
            gpholder.title_name.setText(areainfo.get(groupPosition).NAME);
            ImageView mgroupimage = (ImageView) convertView.findViewById(R.id.expandimage);
            if (groupPosition != 0) {
                if (!isExpanded) {
                    mgroupimage.setImageResource(R.drawable.expand_close);
                } else
                    mgroupimage.setImageResource(R.drawable.expand_open);
            } else {
                mgroupimage.setImageResource(R.drawable.expand_close);
            }
            mgroupimage.setVisibility(View.VISIBLE);
        }
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	@Override
	public int getHeaderState(int groupPosition, int childPosition) {
		final int childCount = getChildrenCount(groupPosition);
		if (childPosition == -1 && listView.getChildAt(0).getTop() >= 0 || !listView.isGroupExpanded(groupPosition)) {
			return PINNED_HEADER_GONE;
		} else if (childPosition == childCount - 1) {
			return PINNED_HEADER_PUSHED_UP;
		} else {
			return PINNED_HEADER_VISIBLE;
		}
	}

	@Override
	public void configureHeader(View header, int groupPosition, int childPosition, int alpha) {
		((TextView) header.findViewById(R.id.title_name)).setText(areainfo.get(groupPosition).NAME);
	}

	private HashMap<Integer, Integer> groupStatusMap = new HashMap<Integer, Integer>();

	@Override
	public void setGroupClickStatus(int groupPosition, int status) {
		groupStatusMap.put(groupPosition, status);
	}

	@Override
	public int getGroupClickStatus(int groupPosition) {
		if (groupStatusMap.containsKey(groupPosition)) {
			return groupStatusMap.get(groupPosition);
		} else {
			return 0;
		}
	}
}