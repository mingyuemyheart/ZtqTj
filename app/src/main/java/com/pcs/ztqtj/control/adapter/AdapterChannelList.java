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
import com.pcs.lib.lib_pcs_v3.model.image.ImageConstant;
import com.pcs.lib.lib_pcs_v3.model.image.ImageFetcher;
import com.pcs.lib_ztqfj_v2.model.pack.net.art.ArtTitleInfo;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * 
 * @author chenjh
 * 
 */
public class AdapterChannelList extends BaseAdapter {

	private List<ArtTitleInfo> mItems;
	private SimpleDateFormat mDateFormat;
	private boolean mBusy = false;
	private Context context;
    private ImageFetcher imageFetcher;
	public AdapterChannelList(Context context, List<ArtTitleInfo> items, ImageFetcher imageFetcher) {
		this.mItems = items;
		this.context = context;
		mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.imageFetcher = imageFetcher;
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
		ViewHodler viewHodler = null;
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
		ArtTitleInfo info = mItems.get(position);
		String imageDownloadUrl = "";
		// String tid= map.get("tid").toString();
		String title = info.title;
		String description = info.desc;
		// String ico=map.get("ico").toString();
		String formatDate = info.pubt;
		String small_ico = context.getString(R.string.file_download_url) + info.small_ico;
		String big_ico = context.getString(R.string.file_download_url) + info.big_ico;
		if (formatDate == null) {
			formatDate = "时间未知";
		}
		viewHodler.itemTitle.setText(title);
		viewHodler.itemDes.setText(description);

        if (!TextUtils.isEmpty(info.small_ico) || "null".equals(info.small_ico)) {
            imageFetcher.loadImage(small_ico, viewHodler.itemImg, ImageConstant.ImageShowType.SRC);
        }

		return convertView;
	}

	static class ViewHodler {
		View itemContextItem;
		ImageView itemImg;
		TextView itemTitle;
		TextView itemDes;
		TextView itemSource;
		TextView itemPubDateTime;
	}

	public void setFlagBusy(boolean busy) {
		this.mBusy = busy;
	}

	public void destory() {
		mDateFormat = null;
		// mImageLoader = null;
		mItems.clear();
		mItems = null;
	}

}
