package com.pcs.ztqtj.view.activity.life;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.pcs.lib.lib_pcs_v3.control.tool.Util;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackKnowWarnDetailDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackKnowWarnDetailDown.KnowWarnDetailBean;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackKnowWarnDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackKnowWarnDown.KnowWarnBean;
import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.AdapterImageDisaster;
import com.pcs.ztqtj.control.adapter.livequery.AdapterData;
import com.pcs.ztqtj.control.inter.DrowListClick;
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
 * 生活气象-气象科普-识图防灾
 */
public class ActivityImageDisaster extends FragmentActivityZtqBase {

	private ListView contextlistview;
	private AdapterImageDisaster adatper;
	private List<KnowWarnDetailBean> knowWarnList;
	private TextView warn_label;
	private TextView warn_title;
	private TextView warn_desc;
	private PackKnowWarnDown warnTitleList;
	private PackKnowWarnDetailDown warnDetailList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_iamgedisaster);
		setTitleText(getIntent().getStringExtra("title"));

		setBtnRight(R.drawable.product_top_right_button, new OnClickListener() {

			@Override
			public void onClick(View v) {
				createPopupWindow(dataeaum, 0, listener).showAsDropDown(
						btnRight,0, Util.dip2px(ActivityImageDisaster.this, 10));
			}
		});

		intiView();
		initData();
	}

	private void intiView() {
		contextlistview = (ListView) findViewById(R.id.contextlistview);
		warn_label = (TextView) findViewById(R.id.warn_label);
		warn_title = (TextView) findViewById(R.id.warn_title);
		warn_desc = (TextView) findViewById(R.id.warn_desc);
	}

	private void initData() {
		dataeaum = new ArrayList<>();
		knowWarnList = new ArrayList<>();
		adatper = new AdapterImageDisaster(ActivityImageDisaster.this, knowWarnList);
		contextlistview.setAdapter(adatper);
		okHttpWarningType();
	}

	/**
	 * 改变预警类型值时更改标题，描述
	 * @param titleBean
	 */
	public void changeValue(KnowWarnBean titleBean) {
		warn_title.setText(titleBean.title);
		warn_desc.setText(titleBean.des);
	}

	/**
	 * 获取预警详情列表
	 */
	private void reqDetailList(KnowWarnBean titleBean) {
		if(!isOpenNet()){
			showToast(getString(R.string.net_err));
			return ;
		}
		changeValue(titleBean);
		okHttpWarningDetail(titleBean.id);
	}

	/**
	 * 处理标题返回
	 */
	private void detailWarnTitleList(PackKnowWarnDown warnTitleList) {
		dataeaum.clear();
		for (int i = 0; i < warnTitleList.titleList.size(); i++) {
			dataeaum.add(warnTitleList.titleList.get(i).title);
		}
		showProgressDialog();
		if(warnTitleList.titleList.size() > 0) {
            reqDetailList(warnTitleList.titleList.get(0));
        }
	}

	/**
	 * 处理 详细列表
	 */
	private void detailWarnDetailList(PackKnowWarnDetailDown warnDetailList) {
		knowWarnList.clear();
		knowWarnList.addAll(warnDetailList.dataList);
		adatper.notifyDataSetChanged();
		contextlistview.setAdapter(adatper);
	}

	private List<String> dataeaum;

	/** 创建下拉选择列表 */
	public PopupWindow createPopupWindow(final List<String> dataeaum, final int floag, final DrowListClick listener) {
		AdapterData dataAdapter = new AdapterData(ActivityImageDisaster.this,dataeaum);
		View popcontent = LayoutInflater.from(ActivityImageDisaster.this).inflate(R.layout.pop_list_layout, null);
		ListView lv = (ListView) popcontent.findViewById(R.id.mylistviw);
		lv.setAdapter(dataAdapter);
		final PopupWindow pop = new PopupWindow(ActivityImageDisaster.this);
		pop.setContentView(popcontent);
		pop.setOutsideTouchable(false);
		int screenWidth = Util.getScreenWidth(ActivityImageDisaster.this);
		pop.setWidth((int)(screenWidth*0.5));
		
		// 调整下拉框长度
		if (lv.getCount() < 7) {
			pop.setHeight(LayoutParams.WRAP_CONTENT);
		} else {
			int screenHight = Util.getScreenHeight(ActivityImageDisaster.this);
			pop.setHeight((int) (screenHight * 0.7));
		}
		pop.setFocusable(true);
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				pop.dismiss();
				listener.itemClick(floag, position);
			}
		});
		return pop;
	}

	private final DrowListClick listener = new DrowListClick() {
		@Override
		public void itemClick(int floag, int item) {
			showProgressDialog();
			reqDetailList(warnTitleList.titleList.get(item));
		}
	};

	/**
	 * 进入栏目获取数据 获取预警种类
	 */
	private void okHttpWarningType() {
		showProgressDialog();
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					JSONObject param = new JSONObject();
					param.put("token", MyApplication.TOKEN);
					String json = param.toString();
					String url = CONST.BASE_URL+"stfz_type";
					Log.e("stfz_type", url);
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
									dismissProgressDialog();
									Log.e("stfz_type", result);
									if (!TextUtil.isEmpty(result)) {
										try {
											JSONObject obj = new JSONObject(result);
											if (!obj.isNull("b")) {
												JSONObject bobj = obj.getJSONObject("b");
												if (!bobj.isNull("stfz_type")) {
													JSONObject fltj_hour = bobj.getJSONObject("stfz_type");
													if (!TextUtil.isEmpty(fltj_hour.toString())) {
														warnTitleList = new PackKnowWarnDown();
														warnTitleList.fillData(fltj_hour.toString());
														if (warnTitleList == null) {
															return;
														}
														detailWarnTitleList(warnTitleList);
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

	/**
	 * 进入栏目获取数据 获取预警种类
	 */
	private void okHttpWarningDetail(final String warningType) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					JSONObject param = new JSONObject();
					param.put("token", MyApplication.TOKEN);
					JSONObject info = new JSONObject();
					info.put("type", warningType);
					param.put("paramInfo", info);
					String json = param.toString();
					String url = CONST.BASE_URL+"stfz";
					Log.e("stfz", url);
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
									Log.e("stfz", result);
									if (!TextUtil.isEmpty(result)) {
										try {
											JSONObject obj = new JSONObject(result);
											if (!obj.isNull("b")) {
												JSONObject bobj = obj.getJSONObject("b");
												if (!bobj.isNull("stfz_info")) {
													JSONObject fltj_hour = bobj.getJSONObject("stfz_info");
													if (!TextUtil.isEmpty(fltj_hour.toString())) {
														dismissProgressDialog();
														warnDetailList = new PackKnowWarnDetailDown();
														warnDetailList.fillData(fltj_hour.toString());
														detailWarnDetailList(warnDetailList);
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
