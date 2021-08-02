package com.pcs.ztqtj.control.adapter.photo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.pcs.lib.lib_pcs_v3.model.image.ImageConstant;
import com.pcs.lib.lib_pcs_v3.model.image.ImageFetcher;
import com.pcs.lib_ztqfj_v2.model.pack.net.photowall.PackPhotoSingle;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.utils.TextUtil;
import com.pcs.ztqtj.util.CommonUtil;

import java.util.List;

/**
 * 实景开拍
 */
public class AdapterPhotoShow extends BaseAdapter {

	private Context mContext;
	private ImageFetcher mImageFetcher;
	private List<PackPhotoSingle> photoList;

	public AdapterPhotoShow(Context context, ImageFetcher imageFetcher, List<PackPhotoSingle> photoList) {
		mContext = context;
		mImageFetcher = imageFetcher;
		this.photoList = photoList;
	}

	@Override
	public int getCount() {
		return photoList.size();
	}

	@Override
	public Object getItem(int position) {
		return photoList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Hodler holder;
		if (convertView == null) {
			holder = new Hodler();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_photo_show, null);
			holder.imagePhoto = (ImageView) convertView.findViewById(R.id.image_photo);
			holder.textNum = (TextView) convertView.findViewById(R.id.text_num);
			holder.textAddr = (TextView) convertView.findViewById(R.id.text_addr);
			convertView.setTag(holder);
		} else {
			holder = (Hodler) convertView.getTag();
		}

		PackPhotoSingle pack = photoList.get(position);
		// 图片
		if (!TextUtil.isEmpty(pack.thumbnailUrl)) {
			mImageFetcher.loadImage(mContext.getString(R.string.sjkp) + pack.thumbnailUrl, holder.imagePhoto, ImageConstant.ImageShowType.SRC);
		} else {
			holder.imagePhoto.setImageResource(R.drawable.no_pic);
		}
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(CommonUtil.widthPixels(mContext)/2, CommonUtil.widthPixels(mContext)/2);
		holder.imagePhoto.setLayoutParams(params);

		if (pack.browsenum != null) {
			holder.textNum.setText(pack.browsenum);
		}
		if (pack.address != null) {
			holder.textAddr.setText(pack.address);
		}
		return convertView;
	}

	private class Hodler {
		private ImageView imagePhoto;
		private TextView textNum;
		private TextView textAddr;
	}

}
