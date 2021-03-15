package com.pcs.ztqtj.control.adapter.adapter_citymanager;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.lib.lib_pcs_v3.control.tool.Util;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCity;

import java.util.ArrayList;
import java.util.List;

public class AdapterTreeView extends BaseExpandableListAdapter {

	public static  float ItemHeight = 0;
	public static  float ItemAdd = 1;
	public static final int PaddingLeft = 15;
	private int myPaddingLeft =20;
	static public class TreeNode {
		public PackLocalCity parent;
		public List<PackLocalCity> childs = new ArrayList<PackLocalCity>();
	}
	List<TreeNode> treeNodes = new ArrayList<TreeNode>();
	Context parentContext;
	public AdapterTreeView(Context context) {
		parentContext = context;
		ItemHeight=context.getResources().getDimension(R.dimen.threeCityHight);
		ItemAdd=context.getResources().getDimension(R.dimen.dimen1);
	}

	public List<TreeNode> getTreeNode() {
		return treeNodes;
	}

	public void updateTreeNode(List<TreeNode> nodes) {
		treeNodes = nodes;
	}

	public void removeAll() {
		treeNodes.clear();
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return treeNodes.get(groupPosition).childs.get(childPosition).NAME;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int)ItemHeight);
		TextView textView = new TextView(this.parentContext);
		textView.setLayoutParams(lp);
		textView.setTextSize(16);
		textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
		textView.setText(getChild(groupPosition, childPosition).toString());
		textView.setPadding(Util.dip2px(this.parentContext, myPaddingLeft*2 + PaddingLeft), 0, 0, 0);
		textView.setTextColor(parentContext.getResources().getColor(R.color.citylist_textcolor));
		return textView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return treeNodes.get(groupPosition).childs.size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return treeNodes.get(groupPosition).parent.NAME;
	}

	@Override
	public int getGroupCount() {
		return treeNodes.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		TextView textView;
		if(convertView==null){
			convertView=View.inflate(parentContext, R.layout.item_select_parent_group, null);
			textView=(TextView) convertView.findViewById(R.id.select_preent_item_group_text);
			convertView.setTag(textView);
		}else{
			textView=(TextView) convertView.getTag();
		}
		AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,(int)ItemHeight);
		convertView.setLayoutParams(lp);
		convertView.setPadding(Util.dip2px(parentContext,myPaddingLeft), 0, 0,0);
		textView.setText(getGroup(groupPosition).toString());
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

}
