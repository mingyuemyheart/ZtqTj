package com.pcs.ztqtj.control.adapter.adapter_citymanager;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.adapter_citymanager.AdapterTreeView.TreeNode;
import com.pcs.lib.lib_pcs_v3.control.tool.Util;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCity;

import java.util.ArrayList;
import java.util.List;

import static com.pcs.ztqtj.control.adapter.adapter_citymanager.AdapterTreeView.ItemHeight;

public class AdapterSuperTreeView extends BaseExpandableListAdapter {

    public interface Threeclick {
        void item(int parent, int child, int threeparent, int threechild);
    }

    public interface Twoclick {
        void item(int groupPosition, int childPosition);
    }

    public static class SuperTreeNode {
        public PackLocalCity parent;
        // 二级树形菜单的结构体
        public List<AdapterTreeView.TreeNode> childs = new ArrayList<AdapterTreeView.TreeNode>();
    }

    protected Threeclick listener;
    private List<SuperTreeNode> superTreeNodes = new ArrayList<SuperTreeNode>();
    protected Context parentContext;

    public AdapterSuperTreeView(Context context, Threeclick listener) {
        parentContext = context;
        this.listener = listener;

    }

    public List<SuperTreeNode> GetTreeNode() {
        return superTreeNodes;
    }

    public void UpdateTreeNode(List<SuperTreeNode> node) {
        superTreeNodes = node;
    }

    public void RemoveAll() {
        superTreeNodes.clear();
    }

    public Object getChild(int groupPosition, int childPosition) {
        return superTreeNodes.get(groupPosition).childs.get(childPosition);
    }

    public int getChildrenCount(int groupPosition) {
        return superTreeNodes.get(groupPosition).childs.size();
    }

    public ExpandableListView getExpandableListView() {
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) ItemHeight);
        ExpandableListView superTreeView = new ExpandableListView(parentContext);
        superTreeView.setGroupIndicator(null);
        superTreeView.setLayoutParams(lp);
        return superTreeView;
    }

    /**
     * 三层树结构中的第二层是一个ExpandableListView
     */
    public View getChildView(final int secondGroupPossition, final int secondChildPossition, boolean isLastChild, View convertView, ViewGroup parent) {
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) ItemHeight);
        final ExpandableListView treeView = new ExpandableListView(parentContext);
        treeView.setGroupIndicator(null);
        treeView.setDivider(parentContext.getResources().getDrawable(R.drawable.line_common));
        treeView.setChildDivider(parentContext.getResources().getDrawable(R.drawable.line_common));
        treeView.setFooterDividersEnabled(false);
        treeView.setLayoutParams(lp);
        final AdapterTreeView treeViewAdapter = new AdapterTreeView(this.parentContext);
        List<TreeNode> tmp = treeViewAdapter.getTreeNode();
        final TreeNode treeNode = (TreeNode) getChild(secondGroupPossition, secondChildPossition);
        tmp.add(treeNode);
        treeViewAdapter.updateTreeNode(tmp);
        treeView.setAdapter(treeViewAdapter);
        treeView.setOnChildClickListener(new OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                listener.item(secondGroupPossition, secondChildPossition, groupPosition, childPosition);
                return true;
            }
        });

        /**
         * 关键点：第二级菜单展开时通过取得节点数来设置第三级菜单的大小
         */
        treeView.setOnGroupExpandListener(new OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
//				if (secondGroupPossition == 0 && secondChildPossition == 0) {
//					AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//							(int) ((treeNode.childs.size()+2)* AdapterTreeView.ItemHeight));
//					treeView.setLayoutParams(lp);
//				} else {

                float itemHight = (treeNode.childs.size() + 1) * AdapterTreeView.ItemHeight
                        + (treeNode.childs.size() + 1) * AdapterTreeView.ItemAdd * 0.6f;

                AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) itemHight);
                treeView.setLayoutParams(lp);
//				}
            }
        });

        /**
         * 第二级菜单回收时设置为标准Item大小
         */
        treeView.setOnGroupCollapseListener(new OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) ItemHeight);
                treeView.setLayoutParams(lp);
            }
        });
        treeView.setPadding(Util.dip2px(parentContext, AdapterTreeView.PaddingLeft * 2), 0, 0, 0);
        return treeView;
    }

    /**
     * 三级树结构中的首层是TextView,用于作为title
     */
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        TextView textView;
        if (convertView == null) {
            convertView = View.inflate(parentContext, R.layout.item_select_parent_group, null);
            textView = (TextView) convertView.findViewById(R.id.select_preent_item_group_text);
            convertView.setTag(textView);
        } else {
            textView = (TextView) convertView.getTag();
        }
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) ItemHeight);
        convertView.setLayoutParams(lp);
        convertView.setPadding(Util.dip2px(parentContext, AdapterTreeView.PaddingLeft), 0, 0, 0);
        textView.setText(getGroup(groupPosition).toString());
        return convertView;
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    public Object getGroup(int groupPosition) {
        return superTreeNodes.get(groupPosition).parent.NAME;
    }

    public int getGroupCount() {
        return superTreeNodes.size();
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public boolean hasStableIds() {
        return true;
    }
}
