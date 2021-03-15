package com.pcs.ztqtj.control.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.view.myview.MyListView;
import com.pcs.lib_ztqfj_v2.model.pack.net.lightning.PackThunderQuireDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.lightning.PackThunderQuireDown.CityInfo;

public class AdapterDataQuire extends BaseAdapter {

	private Context context;
	private PackThunderQuireDown packThunderQuireDown;
	private LightningMoreBtn btn_more_list;

	public AdapterDataQuire(Context context,
			PackThunderQuireDown packThunderQuireDown,
			LightningMoreBtn btn_more_list) {
		this.context = context;
		this.packThunderQuireDown = packThunderQuireDown;
		this.btn_more_list = btn_more_list;
	}

	public void setData(PackThunderQuireDown packThunderQuireDown) {
		this.packThunderQuireDown = packThunderQuireDown;
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		if (packThunderQuireDown != null
				&& packThunderQuireDown.area_list.size() > 0) {
			return packThunderQuireDown.area_list.size();
		} else {
			return 5;
		}

	}

	@Override
	public Object getItem(int position) {
		if (packThunderQuireDown != null
				&& packThunderQuireDown.area_list.size() > 0) {
			return packThunderQuireDown.area_list.get(position).area_id;
		}
		return "";
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	private void initData(Handler handler, int i) {

		handler.tv_data_time
				.setText(packThunderQuireDown.area_list1.get(i).time);
		handler.tv_data_longitude.setText(packThunderQuireDown.area_list1
				.get(i).longitude);
		handler.tv_data_latitude
				.setText(packThunderQuireDown.area_list1.get(i).latitude);
		handler.tv_data_type
				.setText(packThunderQuireDown.area_list1.get(i).type);
		handler.tv_data_strength
				.setText(packThunderQuireDown.area_list1.get(i).intens);
	}

	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		Handler handler = null;
		if (view == null) {
			handler = new Handler();
			view = LayoutInflater.from(context).inflate(
					R.layout.item_data_quire, null);
			handler.tv_data_time = (TextView) view
					.findViewById(R.id.tv_data_time);
			handler.tv_data_longitude = (TextView) view
					.findViewById(R.id.tv_data_longitude);
			handler.tv_data_latitude = (TextView) view
					.findViewById(R.id.tv_data_latitude);
			handler.tv_data_type = (TextView) view
					.findViewById(R.id.tv_data_type);
			handler.tv_data_strength = (TextView) view
					.findViewById(R.id.tv_data_strength);

			handler.linear_city = (LinearLayout) view
					.findViewById(R.id.linear_city);

			handler.tv_title = (TextView) view.findViewById(R.id.tv_title);
			handler.data_more_btn = (Button) view
					.findViewById(R.id.data_more_btn);
			handler.data_lightning_list2 = (MyListView) view
					.findViewById(R.id.data_lightning_list2);

			handler.linear_area = (LinearLayout) view
					.findViewById(R.id.linear_area);
			view.setTag(handler);
		} else {
			handler = (Handler) view.getTag();
		}
		if (packThunderQuireDown != null
				&& packThunderQuireDown.area_list1.size() > 0) {
			handler.linear_city.setVisibility(View.VISIBLE);
			handler.linear_area.setVisibility(View.GONE);
			// 每次查询先初始化表格
			handler.tv_data_time.setText("");
			handler.tv_data_longitude.setText("");
			handler.tv_data_latitude.setText("");
			handler.tv_data_type.setText("");
			handler.tv_data_strength.setText("");
			if (packThunderQuireDown.area_list1.size() > 5) {
				for (int i = 0; i < 5; i++) {
					if (position == i) {
						initData(handler, position);
					}
				}
			} else {
				for (int i = 0; i < packThunderQuireDown.area_list1.size(); i++) {
					if (position == i) {
						initData(handler, position);
					}
				}
			}

		} else if (packThunderQuireDown != null
				&& packThunderQuireDown.area_list.size() > 0) {
			handler.linear_city.setVisibility(View.GONE);
			handler.linear_area.setVisibility(View.VISIBLE);
			CityInfo cityinfo = packThunderQuireDown.area_list.get(position);
			if (Integer.parseInt(cityinfo.count) > 5) {
				handler.data_more_btn.setVisibility(View.VISIBLE);
			} else {
				handler.data_more_btn.setVisibility(View.GONE);
			}
			handler.tv_title.setText(cityinfo.area_name + "：" + cityinfo.count);
			AdapterDateQuireTow mAdapter = new AdapterDateQuireTow(context,
					cityinfo);
			handler.data_lightning_list2.setAdapter(mAdapter);
		}
		handler.data_more_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				btn_more_list.itemOnclick(position);
			}
		});
		return view;
	}

	private class Handler {
		public TextView tv_data_time;// 时间
		public TextView tv_data_longitude;// 经度
		public TextView tv_data_latitude;// 纬度
		public TextView tv_data_type;// 类型
		public TextView tv_data_strength;// 强度
		public LinearLayout linear_city;// 市布局

		public TextView tv_title;// 城市
		public Button data_more_btn;// 更多按钮
		public MyListView data_lightning_list2;// 区列表
		public LinearLayout linear_area;// 区布局

	}

	public interface LightningMoreBtn {
		public void itemOnclick(int item);
	}

}
