package com.pcs.ztqtj.view.activity.life;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.pcs.lib_ztqfj_v2.model.pack.net.art.ArtTitleInfo;
import com.pcs.lib_ztqfj_v2.model.pack.net.art.PackArtTitleDown;
import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.AdapterChannelList;
import com.pcs.ztqtj.control.tool.utils.TextUtil;
import com.pcs.ztqtj.util.CONST;
import com.pcs.ztqtj.util.OkHttpUtil;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 生活气象-天气新闻
 */
public class ActivityChannelList extends FragmentActivityZtqBase {

	private TextView tip_title_tv;
	private ListView myListView;
	private View lineview;
	private AdapterChannelList mAdapter;

	private List<ArtTitleInfo> airTitleList = new ArrayList<ArtTitleInfo>();

	private String title = "";
	private String channel_id;

	/** 每页要加载的数量 **/
	public static final int PAGE_SIZE = 10;
	/** 记录当前已加载到第几页 **/
	private int page = 1;

	@Override
	protected void onCreate(Bundle arg0) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(arg0);
		setContentView(R.layout.channel_list_main);
		initView();
		initData();
		initEvent();
	}

	private void initView() {
		tip_title_tv = (TextView) findViewById(R.id.tip_title_tv);
		myListView = (ListView) findViewById(R.id.mylistviw);
		lineview = findViewById(R.id.lineview);
	}

	private void initEvent() {
		myListView.setOnItemClickListener(itemListener);
		myListView.setOnScrollListener(myOnScrollListener);
	}

	private void initData() {
		airTitleList.clear();
		mAdapter = new AdapterChannelList(getApplicationContext(), airTitleList, getImageFetcher());
		myListView.setAdapter(mAdapter);
		showProgressDialog();
		title = getIntent().getStringExtra("title");
		channel_id = getIntent().getStringExtra("channel_id");
		setTitleText(title);
		getTitleList();
	}

	private void getTitleList() {
		if (page == -1) {
			return;
		}

		if(!isOpenNet()){
			showToast(getString(R.string.net_err));
			return ;
		}

		okHttpArtTitle();
	}

	/**
	 * listview填充数据
	 */
	private void reflashData() {
		if (airTitleList.size() > 0) {
			mAdapter.notifyDataSetChanged();
			lineview.setVisibility(View.GONE);
			tip_title_tv.setVisibility(View.GONE);
			myListView.setVisibility(View.VISIBLE);
		} else {
			myListView.setVisibility(View.GONE);
			tip_title_tv.setVisibility(View.VISIBLE);
			lineview.setVisibility(View.VISIBLE);
		}
	}

	private AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			ArtTitleInfo info = airTitleList.get(position);
			Intent intent = new Intent(ActivityChannelList.this, ActivityChannelInfo.class);
			intent.putExtra("info", info);
			intent.putExtra("title", title);
			startActivity(intent);
		}
	};

	private OnScrollListener myOnScrollListener = new OnScrollListener() {
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			// 当不滚动时
			if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
				// 判断是否滚动到底部
				if (view.getLastVisiblePosition() == view.getCount() - 1) {
					// 加载更多功能的代码
					System.out.println("到了底部，加载更多");
					getTitleList();
				}
			}
		}

		@Override
		public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {

		}
	};

	/**
	 * 获取天气新闻
	 */
	private void okHttpArtTitle() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					JSONObject param  = new JSONObject();
					param.put("token", MyApplication.TOKEN);
					String json = param.toString();
					final String url = CONST.BASE_URL+"art_title";
					Log.e("art_title", url);
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
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									if (!TextUtil.isEmpty(result)) {
										Log.e("art_title", result);
										try {
											JSONObject obj = new JSONObject(result);
											if (!obj.isNull("b")) {
												JSONObject bobj = obj.getJSONObject("b");
												if (!bobj.isNull("art_title")) {
													JSONObject art_title = bobj.getJSONObject("art_title");
													if (art_title.isNull("page")) {
														art_title.put("page", "1");
													}
													if (!TextUtil.isEmpty(art_title.toString())) {
														PackArtTitleDown pack = new PackArtTitleDown();
														pack.fillData(art_title.toString());
														airTitleList.addAll(pack.artTitleList);
														reflashData();
														dismissProgressDialog();
														if (pack.artTitleList.size() > 0) {
															page++;
															System.out.println("有更多数据");
														} else {
															System.out.println("无更多数据");
															page = -1;
														}
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
