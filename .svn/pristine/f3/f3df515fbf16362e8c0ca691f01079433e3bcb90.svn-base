package com.pcs.ztqtj.control.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.lib.lib_pcs_v3.model.image.ImageConstant;
import com.pcs.lib.lib_pcs_v3.model.image.ImageFetcher;
import com.pcs.lib_ztqfj_v2.model.pack.net.hot_tourist_spot.HotTouristSpot;

import java.util.ArrayList;

public class AdapterTravelFragement extends BaseAdapter {
	private Context context;
	private ArrayList<HotTouristSpot> touristSoptList;
	private ImageFetcher mImageFetcher;
	private String mUrlPrev = "";

	public AdapterTravelFragement(Context context,
			ArrayList<HotTouristSpot> touristSoptList, ImageFetcher imageFetcher) {
		this.context = context;
		this.touristSoptList = touristSoptList;
		mImageFetcher = imageFetcher;

		mUrlPrev = context.getResources().getString(R.string.file_download_url);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder;
		if (convertView == null) {
			holder = new Holder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.live_gridview_item_travel, null);
			holder.itemImage = (ImageView) convertView
					.findViewById(R.id.item_image);
			holder.itemText = (TextView) convertView
					.findViewById(R.id.item_text);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		String name = touristSoptList.get(position).getName();
		String[] names = touristSoptList.get(position).getName().split("\\.");
		if(names != null && names.length == 2) {
			name = names[0];
		}
		holder.itemText.setText(name);

		String url = mUrlPrev + touristSoptList.get(position).getImageUrl();
		mImageFetcher.loadImage(url, holder.itemImage, ImageConstant.ImageShowType.SRC);

		return convertView;
	}

	class Holder {
		public TextView itemText;
		public ImageView itemImage;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public int getCount() {
		return touristSoptList.size();
	}
}
