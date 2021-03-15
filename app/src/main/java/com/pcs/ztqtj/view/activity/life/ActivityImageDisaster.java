package com.pcs.ztqtj.view.activity.life;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.AdapterImageDisaster;
import com.pcs.ztqtj.control.adapter.livequery.AdapterData;
import com.pcs.ztqtj.control.inter.DrowListClick;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
import com.pcs.lib.lib_pcs_v3.control.tool.Util;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackKnowWarnDetailDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackKnowWarnDetailDown.KnowWarnDetailBean;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackKnowWarnDetailUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackKnowWarnDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackKnowWarnDown.KnowWarnBean;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackKnowWarnUp;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Z 识图防灾
 */
public class ActivityImageDisaster extends FragmentActivityZtqBase implements
		OnClickListener {
	private ListView contextlistview;
	private AdapterImageDisaster adatper;
	private List<KnowWarnDetailBean> knowWarnList;
	/**
	 * 屏幕高度
	 */
	private int screenHight = 0;
	/**
	 * 屏幕宽度
	 */
	private int screenWidth = 0;
	private TextView warn_label;
	private TextView warn_title;
	private TextView warn_desc;
	private final MyReceiver receiver = new MyReceiver();

	private PackKnowWarnDown warnTitleList;
	private PackKnowWarnDetailDown warnDetailList;
    private PackKnowWarnDetailUp packKnowWarnDetailUp = new PackKnowWarnDetailUp();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_iamgedisaster);
		setTitleText(getIntent().getStringExtra("title"));
		PcsDataBrocastReceiver.registerReceiver(this, receiver);

		setBtnRight(R.drawable.product_top_right_button, new OnClickListener() {

			@Override
			public void onClick(View v) {
				createPopupWindow(dataeaum, 0, listener).showAsDropDown(
						btnRight,0, Util.dip2px(ActivityImageDisaster.this, 10));
			}
		});

		intiView();
		initData();
		initEvent();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
	}

	private void intiView() {
		contextlistview = (ListView) findViewById(R.id.contextlistview);
		warn_label = (TextView) findViewById(R.id.warn_label);
		warn_title = (TextView) findViewById(R.id.warn_title);
		warn_desc = (TextView) findViewById(R.id.warn_desc);
	}

	private void initData() {
		dataeaum = new ArrayList<String>();
		knowWarnList = new ArrayList<KnowWarnDetailBean>();
		adatper = new AdapterImageDisaster(ActivityImageDisaster.this,
				knowWarnList, getImageFetcher());
		contextlistview.setAdapter(adatper);
		reqList();
	}

	private void initEvent() {
		warn_label.setOnClickListener(this);
	}

	/**
	 * 改变预警类型值时更改标题，描述
	 * 
	 * @param titleBean
	 */
	public void changeValue(KnowWarnBean titleBean) {
		warn_title.setText(titleBean.title);
		warn_desc.setText(titleBean.des);
	}

	/**
	 * 进入栏目获取数据 获取预警种类
	 */
	private void reqList() {
		if(!isOpenNet()){
			showToast(getString(R.string.net_err));
			return ;
		}
		showProgressDialog();
		warnTitleList = new PackKnowWarnDown();
		PackKnowWarnUp uppack = new PackKnowWarnUp();
		PcsDataDownload.addDownload(uppack);
	}

	/**
	 * 获取预警详情列表
	 */
	private void reqDetailList(KnowWarnBean titleBean) {
		if(!isOpenNet()){
			showToast(getString(R.string.net_err));
			return ;
		}
		warnDetailList = new PackKnowWarnDetailDown();
		warnDetailList.id = titleBean.id;
        packKnowWarnDetailUp = new PackKnowWarnDetailUp();
        packKnowWarnDetailUp.id = titleBean.id;
		PcsDataDownload.addDownload(packKnowWarnDetailUp);
		changeValue(titleBean);
	}

	private class MyReceiver extends PcsDataBrocastReceiver {
		@Override
        public void onReceive(String name, String errorStr) {
            // 如果为定位出来的城市则另作处理
            dismissProgressDialog();
            if (name.equals(PackKnowWarnUp.NAME)) {
                warnTitleList = (PackKnowWarnDown) PcsDataManager.getInstance().getNetPack(name);
                if (warnTitleList == null) {
                    return;
                }
                detailWarnTitleList(warnTitleList);
            }
            if (name.equals(packKnowWarnDetailUp.getName())) {
                warnDetailList = (PackKnowWarnDetailDown) PcsDataManager.getInstance().getNetPack(name);
                if (warnTitleList == null) {
                    return;
                }
                detailWarnDetailList(warnDetailList);
            }
        }
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.warn_label:

			break;

		default:
			break;
		}
	}

	private List<String> dataeaum;

	/** 创建下拉选择列表 */
	public PopupWindow createPopupWindow(final List<String> dataeaum,
			final int floag, final DrowListClick listener) {
		AdapterData dataAdapter = new AdapterData(ActivityImageDisaster.this,dataeaum);

		View popcontent = LayoutInflater.from(ActivityImageDisaster.this).inflate(R.layout.pop_list_layout, null);
		ListView lv = (ListView) popcontent.findViewById(R.id.mylistviw);
		lv.setAdapter(dataAdapter);
		final PopupWindow pop = new PopupWindow(ActivityImageDisaster.this);
		pop.setContentView(popcontent);
		pop.setOutsideTouchable(false);
		screenWidth = Util.getScreenWidth(ActivityImageDisaster.this);
		pop.setWidth((int)(screenWidth*0.5));
		
		// 调整下拉框长度
		if (lv.getCount() < 7) {
			pop.setHeight(LayoutParams.WRAP_CONTENT);
		} else {
			screenHight = Util.getScreenHeight(ActivityImageDisaster.this);
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
}
