package com.pcs.ztqtj.control.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.view.activity.life.MyArtTitleInfo;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * 生活气象-气象科普-列表
 */
public class AdapterChannelList extends BaseAdapter {

	private List<MyArtTitleInfo> mItems;
	private Context context;

	public AdapterChannelList(Context context, List<MyArtTitleInfo> items) {
		this.mItems = items;
		this.context = context;
	}

	@Override
	public int getCount() {
		return mItems.size();
	}

	@Override
	public Object getItem(int position) {
		return mItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHodler viewHodler;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.channel_list_item, null);
			viewHodler = new ViewHodler();
			viewHodler.itemContextItem = convertView.findViewById(R.id.item_context_layout);
			viewHodler.itemImg = (ImageView) convertView.findViewById(R.id.item_img);
			viewHodler.itemTitle = (TextView) convertView.findViewById(R.id.item_title);
			viewHodler.itemDes = (TextView) convertView.findViewById(R.id.item_des);
			convertView.setTag(viewHodler);
		} else {
			viewHodler = (ViewHodler) convertView.getTag();
		}

		MyArtTitleInfo info = mItems.get(position);
		String title = info.title;
		String description = info.desc;
		String small_ico = context.getString(R.string.msyb)+info.small_ico;
		if (info.small_ico.startsWith("http")) {
			small_ico = info.small_ico;
		}
		viewHodler.itemTitle.setText(title);
		viewHodler.itemDes.setText(description);
        if (!TextUtils.isEmpty(info.small_ico) || "null".equals(info.small_ico)) {
			Picasso.get().load(small_ico).into(viewHodler.itemImg);
        } else {
			viewHodler.itemImg.setImageResource(R.drawable.no_pic);
		}

		return convertView;
	}

	static class ViewHodler {
		View itemContextItem;
		ImageView itemImg;
		TextView itemTitle;
		TextView itemDes;
	}

	public void setFlagBusy(boolean busy) {
	}

	public void destory() {
		mItems.clear();
		mItems = null;
	}

}
