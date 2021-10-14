package com.pcs.ztqtj.view.activity.product.waterflood;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.product_numerical.AdapterColumn;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.net.waterflood.PackWeatherColumnDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.waterflood.PackWeatherColumnUp;

/**
 * 气象产品-水利汛情
 * 
 * @author tya
 *
 */
public class ActivityWaterFlood extends FragmentActivityZtqBase {

	// UI
	/**
	 * 首页产品列表
	 */
    private GridView gridView;

	// 数据
	/**
	 * 产品适配器
	 */
    private AdapterColumn adapter;

    private PackWeatherColumnUp packUp = new PackWeatherColumnUp();
    private PackWeatherColumnDown packDown = new PackWeatherColumnDown();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_water_flood);
		setTitleText(R.string.title_water_flood);
		initView();
		initEvent();
		initData();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
        if(receiver != null) {
            PcsDataBrocastReceiver.unregisterReceiver(this, receiver);
            receiver = null;
        }
	}

	/**
	 * 初始化UI
	 */
	private void initView() {
        gridView = (GridView) findViewById(R.id.gridview);
	}

	/**
	 * 初始化事件
	 */
	private void initEvent() {
        gridView.setOnItemClickListener(onItemClick);
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
        PcsDataBrocastReceiver.registerReceiver(this, receiver);
		req();
	}

    private void req() {

        if(!isOpenNet()){
            showToast(getString(R.string.net_err));
            return ;
        }
        packUp = new PackWeatherColumnUp();
        packUp.type = "102";
        PcsDataDownload.addDownload(packUp);
    }

	/**
	 * 列表点击事件
	 */
	private OnItemClickListener onItemClick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if(packDown != null && packDown.lm_list != null && packDown.lm_list.size() > position) {
                PackWeatherColumnDown.WeatherColumnInfo info = packDown.lm_list.get(position);
                int type = -1;
                try {
                    type = Integer.valueOf(info.key_type);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(type != -1) {
                    Intent intent = new Intent();
                    switch (type) {
                        // 汛情摘要
                        case 10202:
                            intent.setClass(ActivityWaterFlood.this, ActivityFloodSummary.class);
                            break;
                        // 雨晴信息
                        case 10203:
                            intent.setClass(ActivityWaterFlood.this, ActivityRainInfo.class);
                            break;
                        // 水位信息
                        case 10204:
                            intent.setClass(ActivityWaterFlood.this, ActivityWaterLevelInfo.class);
                            break;
                        // 风情信息
                        case 10205:
                            intent.setClass(ActivityWaterFlood.this, ActivityWindInfo.class);
                            break;
                        // 积水监测
                        case 10201:
                            intent.setClass(ActivityWaterFlood.this, ActivityWaterMonitor.class);
                            break;
                    }
                    startActivity(intent);
                }
            }
		}
	};

    private PcsDataBrocastReceiver receiver = new PcsDataBrocastReceiver() {
        @Override
        public void onReceive(String nameStr, String errorStr) {
            if(nameStr.equals(packUp.getName())) {
                packDown = (PackWeatherColumnDown) PcsDataManager.getInstance().getNetPack(nameStr);
                if(packDown == null || packDown.lm_list == null || packDown.lm_list.size() == 0) {
                    return ;
                }
                adapter = new AdapterColumn(ActivityWaterFlood.this, packDown.lm_list);
                gridView.setAdapter(adapter);
            }
        }
    };

}
