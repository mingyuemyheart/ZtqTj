package com.pcs.ztqtj.control.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.pcs.lib_ztqfj_v2.model.pack.net.TravelSubjectInfo;
import com.pcs.ztqtj.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 景点适配器
 * 
 * @author tya
 *
 */
public class AdapterTravelTourSpots extends BaseAdapter {

	private Context context = null;
	private List<TravelSubjectInfo> dataList = new ArrayList<TravelSubjectInfo>();

	public AdapterTravelTourSpots(Context context, List<TravelSubjectInfo> dataList) {
		this.context = context;
		this.dataList = dataList;
	}

	@Override
	public int getCount() {
		return dataList.size();
	}

	@Override
	public Object getItem(int position) {
		return dataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_travel_tour_spots, null);
			holder.image = (ImageView) convertView.findViewById(R.id.image);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		 Bitmap bm = dataList.get(position).bm;
		 if(bm != null) {
//			 float w = bm.getWidth();
//			 float h = bm.getHeight();
//			 int width = Util.dip2px(context, 50);
//			 int height = (int) (width / w * h);
//			 LayoutParams params = new LayoutParams(width, height);
//			 holder.image.setLayoutParams(params);
			 holder.image.setImageBitmap(bm);
		 }
		return convertView;
	}

	public void setData(List<TravelSubjectInfo> dataList) {
		this.dataList = dataList;
		notifyDataSetChanged();
	}

	private static class ViewHolder {
		ImageView image;
	}

}
