package com.pcs.ztqtj.control.adapter.adapter_set;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pcs.ztqtj.R;

/**
 * @author Z 城市列表适配器
 */
public class AdapterSetAnther extends BaseAdapter {
	private Context context;
	private List<Map<String, String>> data;

	public AdapterSetAnther(Context context, List<Map<String, String>> data) {
		this.context = context;
		this.data = data;
	}
	@Override
	public int getCount() {
		return data.size();
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
	public View getView(final int position, View view, ViewGroup parent) {
		Holder holder;
		if (view == null) {
			holder = new Holder();
			view = LayoutInflater.from(context).inflate(R.layout.item_setanthergridview, null);

			holder.image = (ImageView) view.findViewById(R.id.gridviewitem_iamgebutton);
			holder.text = (TextView) view.findViewById(R.id.gridviewitem_text);
			view.setTag(holder);
		} else {
			holder = (Holder) view.getTag();
		}

		try {
			holder.image.setImageResource(Integer.parseInt(data.get(position).get("i")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		holder.text.setText(data.get(position).get("t"));
		return view;
	}

	private class Holder {
		public ImageView image;
		public TextView text;

	}
}
