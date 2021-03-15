package com.pcs.ztqtj.control.listener;

import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.pcs.ztqtj.control.tool.ZtqLocationTool;

/**
 * 地理编码搜索监听基类
 * 
 * @author JiangZY
 * 
 */
public abstract class PcsOnGeocodeSearchListener implements
		OnGeocodeSearchListener {

	@Override
	public void onGeocodeSearched(GeocodeResult result, int rCode) {

	}

	@Override
	public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
		if (rCode != ZtqLocationTool.RCODE) {
			return;
		}

		if (result == null || result.getRegeocodeAddress() == null
				|| result.getRegeocodeAddress().getFormatAddress() == null) {
			return;
		}

		// 格式化地址
		String formatAddress = result.getRegeocodeAddress().getFormatAddress();
		int sub = -1;
		sub = formatAddress.indexOf("省");
		if (sub > 0) {
			sub += 1;
		}
		if (sub == -1) {
			sub = formatAddress.indexOf("自治区");
			if (sub > 0) {
				sub += 3;
			}
		}
		if (sub > 0) {
			formatAddress = formatAddress.substring(sub);
		}

		if (formatAddress.equals("石家庄市正定县正定镇正定县")) {
			formatAddress = "石家庄市正定县正定镇";
		}
		// 设置地址
		result.getRegeocodeAddress().setFormatAddress(formatAddress);
	}

}
