package com.pcs.ztqtj.view.activity.life.travel;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pcs.lib.lib_pcs_v3.model.image.ImageConstant;
import com.pcs.lib.lib_pcs_v3.model.image.ImageFetcher;
import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.utils.TextUtil;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.util.OkHttpUtil;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 生活气象-旅游气象-第二页
 */
public class TravelFragmentTwo extends Fragment {

	private String cityId;
	private TextView travel_pagetwo_text;
	private ImageView travelTwoBanner;

	ImageFetcher mImageFetcher;

	public TravelFragmentTwo() {
	}

	@SuppressLint("ValidFragment")
    public TravelFragmentTwo(String cityId, ImageFetcher imageFetcher) {
		this.cityId = cityId;
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

		okHttpLyHjjc(cityId);
	}

	/**
	 * 获取景点信息
	 */
	private void okHttpLyHjjc(final String stationId) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					JSONObject param  = new JSONObject();
					param.put("token", MyApplication.TOKEN);
					JSONObject info = new JSONObject();
					info.put("stationId", stationId);
					param.put("paramInfo", info);
					String json = param.toString();
					final String url = CONST.BASE_URL+"ly_hjjc";
					Log.e("ly_hjjc", url);
					RequestBody body = FormBody.create(MediaType.parse("application/json; charset=utf-8"), json);
					OkHttpUtil.enqueue(new Request.Builder().post(body).url(url).build(), new Callback() {
						@Override
						public void onFailure(@NotNull Call call, @NotNull IOException e) {
						}
						@Override
						public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
							if (!response.isSuccessful()) {
								return;
							}
							final String result = response.body().string();
							getActivity().runOnUiThread(new Runnable() {
								@Override
								public void run() {
									Log.e("ly_hjjc", result);
									if (!TextUtil.isEmpty(result)) {
										try {
											JSONObject obj = new JSONObject(result);
											if (!obj.isNull("b")) {
												JSONObject bobj = obj.getJSONObject("b");
												if (!bobj.isNull("lyqx")) {
													JSONObject lyqx = bobj.getJSONObject("lyqx");
													if (!lyqx.isNull("des")) {
														travel_pagetwo_text.setText(lyqx.getString("des"));
													}
													if (!lyqx.isNull("img")) {
														String url = getString(R.string.file_download_url) + lyqx.getString("img");
														mImageFetcher.loadImage(url, travelTwoBanner, ImageConstant.ImageShowType.SRC);
													}
												}
											}
										} catch (JSONException e) {
											e.printStackTrace();
										}
									}
								}
							});
						}
					});
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

}
