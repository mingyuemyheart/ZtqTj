package com.pcs.ztqtj.control.adapter.air_quality;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.lib_ztqfj_v2.model.pack.net.airinfopack.PackAirInfoDown;

/**
 * 适配器：空气质量详情
 * 
 * @author JiangZy
 * 
 */
public class AdapterAirQualityDetail extends BaseAdapter {
	private Context mContext;
	// 数据
	private PackAirInfoDown mPack = null;
	// 中文
	private String mArrCN[] = {};
	// 英文
	private String mArrEN[] = {};
	// 单位
	private String mArrUnit[] = {};

	public AdapterAirQualityDetail(Context context) {
		mContext = context;
		mArrCN = context.getResources().getStringArray(
				R.array.AirQualityDetailCN);
		mArrEN = context.getResources().getStringArray(
				R.array.AirQualityDetailEN);
		mArrUnit = context.getResources().getStringArray(
				R.array.AirQualityDetailUnit);
	}

	/**
	 * 设置数据包
	 * 
	 * @param pack
	 */
	public void setDataPack(PackAirInfoDown pack) {
		mPack = pack;
	}

	@Override
	public int getCount() {
		return 7;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.item_airquality_detail, null);
		}
		TextView textView = null;
		// 英文
		textView = (TextView) convertView.findViewById(R.id.text_en);
		textView.setText(mArrEN[position]);
		// 中文
		textView = (TextView) convertView.findViewById(R.id.text_cn);
		textView.setText(mArrCN[position]);
		// 单位
		textView = (TextView) convertView.findViewById(R.id.text_unit);
		textView.setText(mArrUnit[position]);
		// 数据
		if (mPack != null) {
			textView = (TextView) convertView.findViewById(R.id.text_number);
			textView.setText(getDataByPosition(mPack, position));
		} else {
			textView.setText("");
		}
		return convertView;
	}

	/**
	 * 根据行号获取显示数据
	 * 
	 * @param pack
	 * @param position
	 * @return
	 */
	private String getDataByPosition(PackAirInfoDown pack, int position) {
		switch (position) {
		case 0:
			/** 细微颗粒物 **/
			return pack.pm2_5;
		case 1:
			/** 可吸入颗粒物 **/
			return pack.pm10;
		case 2:
			/** 一氧化碳 **/
			return pack.co;
		case 3:
			/** 二氧化碳 **/
			return pack.no2;
		case 4:
			/** 臭氧1小时平均值 **/
			return pack.o3;
		case 5:
			/** 臭氧8小时平均值 **/
			return pack.o3_8h;
		case 6:
			/** 二氧化硫 **/
			return pack.so2;
		}

		return "";
	}
}
