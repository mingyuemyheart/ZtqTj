package com.pcs.ztqtj.control.adapter.adapter_set;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.lib.lib_pcs_v3.control.tool.Util;

import java.util.List;
import java.util.Map;

public class AdapterFragmentSetManager extends BaseAdapter {
	protected final List<Map<String, String>> listdata;
	protected final Context context;

    /**
     *
     * @param context
     * @param listdata
     * @param rebootIndex 开机自启动显示位置
     */
	public AdapterFragmentSetManager(Context context,
			List<Map<String, String>> listdata, int rebootIndex) {
		this.context = context;
		this.listdata = listdata;
	}

    public AdapterFragmentSetManager(Context context,
                                     List<Map<String, String>> listdata, int rebootIndex, int versionIndex) {
        this.context = context;
        this.listdata = listdata;
    }

	@Override
	public int getCount() {
		return listdata.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		Holder holder;
		if (view == null) {
			holder = new Holder();
			view = LayoutInflater.from(context).inflate(
					R.layout.item_fragmentset, null);
			holder.layout = (RelativeLayout) view
					.findViewById(R.id.item_fragmentset_id);
			holder.itemTest = (TextView) view.findViewById(R.id.explain_text);
			holder.icon = (ImageView) view.findViewById(R.id.setimage_icon);
			holder.choosebutton = (CheckBox) view.findViewById(R.id.open_icon);
			holder.subText = (TextView) view.findViewById(R.id.sub_text);
            holder.rightText = (TextView) view.findViewById(R.id.tv_right);
			view.setTag(holder);
		} else {
			holder = (Holder) view.getTag();
		}

		try {
			holder.choosebutton.setChecked(Util.getPreferencesBooleanValue(
					context, "root", "start"));

		} catch (Exception e) {
			e.printStackTrace();
		}
		holder.itemTest.setText(listdata.get(position).get("t"));
		if (position == getCount()) {
			holder.icon.setVisibility(View.GONE);
		} else {
			try {
				holder.icon.setImageResource(Integer.parseInt((listdata
						.get(position).get("i"))));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return view;
	}

	protected class Holder {
		public RelativeLayout layout;
		public TextView itemTest;
		public ImageView icon;
		public CheckBox choosebutton;
		public TextView subText;
        public TextView rightText;
	}
}
