package com.pcs.ztqtj.control.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.view.activity.web.MyWebView;
import com.pcs.lib.lib_pcs_v3.model.image.ImageConstant;
import com.pcs.lib.lib_pcs_v3.model.image.ImageFetcher;
import com.pcs.lib_ztqfj_v2.model.pack.net.TravelWeatherColumn;

import java.util.List;

public class AdapterTravelViewPager extends PagerAdapter {

	private Context context = null;
	private List<TravelWeatherColumn> dataList = null;
	private ImageFetcher imageFetcher = null;

	public AdapterTravelViewPager(Context context,
			List<TravelWeatherColumn> dataList, ImageFetcher imageFetcher) {
		this.context = context;
		this.dataList = dataList;
		this.imageFetcher = imageFetcher;
	}

	@Override
	public int getCount() {
		return dataList.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, final int position) {
		View view = LayoutInflater.from(context).inflate(
				R.layout.banner_travel, null);
		ImageView iv = (ImageView) view.findViewById(R.id.iv);
		String url = context.getResources().getString(
				R.string.file_download_url)
				+ dataList.get(position).img_url;
		imageFetcher.loadImage(url, iv, ImageConstant.ImageShowType.SRC);
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				TravelWeatherColumn column = dataList.get(position);
				if (column != null && !TextUtils.isEmpty(column.link_url)) {
					Intent intent = new Intent(context, MyWebView.class);
					intent.putExtra("title", "旅游专题");
					intent.putExtra("url", column.link_url);
					context.startActivity(intent);
				}
			}
		});
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		container.addView(view, lp);

		return view;
	}

}
