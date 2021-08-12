package com.pcs.ztqtj.control.adapter.media;

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
import com.pcs.lib_ztqfj_v2.model.pack.net.media.MediaInfo;

import java.util.List;

/**
 * 气象影视 适配器
 */
public class AdapterMediaGridView extends BaseAdapter {

	private Context mContext;
	private List<MediaInfo> dataList;
	private ImageFetcher mImageFetcher = null;

	public AdapterMediaGridView(Context context, List<MediaInfo> data,ImageFetcher mImageFetcher) {
		this.mContext = context;
		this.dataList = data;
		this.mImageFetcher=mImageFetcher;
	}

	@Override
	public int getCount() {
		return dataList.size();
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
			view = LayoutInflater.from(mContext).inflate(R.layout.media_gridview_item, null);
			holder.itemImage = (ImageView) view.findViewById(R.id.item_image);
			holder.itemName = (TextView) view.findViewById(R.id.item_text);
			view.setTag(holder);
		} else {
			holder = (Holder) view.getTag();
		}
		MediaInfo info = dataList.get(position);
		String title = info.title;
		String url = mContext.getString(R.string.msyb) + info.imageurl;
		mImageFetcher.loadImage(url, holder.itemImage, ImageConstant.ImageShowType.SRC);
		holder.itemName.setText(title);
		holder.itemName.setVisibility(View.VISIBLE);
		return view;
	}

	private class Holder {
		public ImageView itemImage;
		public TextView itemName;
	}
}
