package com.pcs.ztqtj.control.adapter.media;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pcs.lib_ztqfj_v2.model.pack.net.media.MediaInfo;
import com.pcs.ztqtj.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * 生活气象-气象影视
 */
public class AdapterMediaGrid extends BaseAdapter {

	private Context mContext;
	private List<MediaInfo> dataList;

	public AdapterMediaGrid(Context context, List<MediaInfo> data) {
		this.mContext = context;
		this.dataList = data;
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
			view = LayoutInflater.from(mContext).inflate(R.layout.adapter_media_grid, null);
			holder.itemImage = (ImageView) view.findViewById(R.id.item_image);
			holder.itemName = (TextView) view.findViewById(R.id.item_text);
			view.setTag(holder);
		} else {
			holder = (Holder) view.getTag();
		}

		MediaInfo info = dataList.get(position);

		if (info.title != null) {
			holder.itemName.setText(info.title);
		}

		String url = mContext.getString(R.string.msyb) + info.imageurl;
		Picasso.get().load(url).into(holder.itemImage);
		holder.itemName.setVisibility(View.VISIBLE);
		return view;
	}

	private class Holder {
		public ImageView itemImage;
		public TextView itemName;
	}
}
