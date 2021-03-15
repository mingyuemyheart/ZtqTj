package com.pcs.ztqtj.view.activity.life.travel;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.lib.lib_pcs_v3.model.image.ImageConstant;
import com.pcs.lib.lib_pcs_v3.model.image.ImageFetcher;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackTravelWeatherDown;

/**
 * 旅游气象第二页
 * 
 * @author WeiXJ
 * 
 */
public class TravelFragmentTwo extends Fragment {

	private TextView travel_pagetwo_text;
	private ImageView travelTwoBanner;

	ImageFetcher mImageFetcher;

	public TravelFragmentTwo() {
	}

	@SuppressLint("ValidFragment")
    public TravelFragmentTwo(ImageFetcher imageFetcher) {
		mImageFetcher = imageFetcher;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.travel_pagetwo_layout, container,
				false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		travel_pagetwo_text = (TextView) getView().findViewById(
				R.id.travel_pagetwo_text);
		travelTwoBanner = (ImageView) getView().findViewById(
				R.id.travel_two_banner);
	}

	/**
	 * 更新界面
	 * 
	 * @param pack
	 */
	public void reflashUI(PackTravelWeatherDown pack) {
		travel_pagetwo_text.setText(pack.des);

		String url = getString(R.string.file_download_url) + pack.img;
		mImageFetcher.loadImage(url, travelTwoBanner, ImageConstant.ImageShowType.SRC);
	}
}
