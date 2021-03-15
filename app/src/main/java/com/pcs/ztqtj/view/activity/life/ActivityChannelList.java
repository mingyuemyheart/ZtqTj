package com.pcs.ztqtj.view.activity.life;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.AdapterChannelList;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.net.art.ArtTitleInfo;
import com.pcs.lib_ztqfj_v2.model.pack.net.art.PackArtTitleDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.art.PackArtTitleUp;

import java.util.ArrayList;
import java.util.List;

/**
 * 资讯列表
 * 
 * @author chenjh
 */
public class ActivityChannelList extends FragmentActivityZtqBase {

	private TextView tip_title_tv;
	private ListView myListView;
	private View lineview;
	private AdapterChannelList mAdapter;

	private PackArtTitleUp packArtTitle = new PackArtTitleUp();

	private List<ArtTitleInfo> airTitleList = new ArrayList<ArtTitleInfo>();
	private MyReceiver receiver = new MyReceiver();

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
		// 注册广播接收
		PcsDataBrocastReceiver.registerReceiver(this, receiver);
		title = getIntent().getStringExtra("title");
		channel_id = getIntent().getStringExtra("channel_id");
		setTitleText(title);
		getTitleList();
	}

	private void getTitleList() {
		if (page == -1) {
			return;
		}
		if (null == packArtTitle) {
			packArtTitle = new PackArtTitleUp();
		}
		if(!isOpenNet()){
			showToast(getString(R.string.net_err));
			return ;
		}

		packArtTitle.page = page + "";
		packArtTitle.count = PAGE_SIZE + "";
		packArtTitle.channel = channel_id;
		PcsDataDownload.addDownload(packArtTitle);
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

	/**
	 * 数据更新广播接收
	 * 
	 * @author JiangZy
	 * 
	 */
	private class MyReceiver extends PcsDataBrocastReceiver {
		@Override
		public void onReceive(String name, String error) {

			if (packArtTitle != null && packArtTitle.getName().equals(name)) {
                PackArtTitleDown pack = (PackArtTitleDown) PcsDataManager.getInstance().getNetPack(name);
				if (pack == null) {
					return;
				}
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

	@Override
	public void onDestroy() {
		super.onDestroy();
		this.unregisterReceiver(receiver);
	}

}
