package com.pcs.ztqtj.control.adapter.hour_forecast;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.listener.ObserverScrollSync;
import com.pcs.ztqtj.view.myview.MyHScrollView;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackHourForecastDown.HourForecast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

/**
 * 适配器：逐小时预报
 * 
 * @author JiangZy
 * 
 */
@SuppressLint({ "RtlHardcoded" })
public class AdapterHourForecast extends BaseAdapter {
	private Context mContext;

	/**
	 * 数据列表
	 */
	private List<HourForecast> mList = new ArrayList<HourForecast>();
	// 滚动条观察者
	private ObserverScrollSync mObserver;

	public AdapterHourForecast(Context context) {
		mContext = context;
	}

	public void setObserver(ObserverScrollSync observer) {
		mObserver = observer;
	}

	/**
	 * 添加数据列表
	 * 
	 * @param list
	 */
	public void addDataList(List<HourForecast> list) {
		mList.addAll(list);
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	private String getTime(String str){
		String time="";
		if(!TextUtils.isEmpty(str)){
			if(str.length()==10){
				int year=Integer.parseInt(str.substring(0, 4));
				int mouth=Integer.parseInt(str.substring(4, 6));
				int day=Integer.parseInt(str.substring(6, 8));
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.YEAR, year);
				calendar.set(Calendar.MONTH, mouth - 1);
				calendar.set(Calendar.DAY_OF_MONTH, day);
				time=mouth+"月"+day+"日"+getWeek(calendar);
			}
		}
		return time;
	}
	
	public String getWeek(Calendar c) {
		c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
		String  mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
		if ("1".equals(mWay)) {
			mWay = "天";
		} else if ("2".equals(mWay)) {
			mWay = "一";
		} else if ("3".equals(mWay)) {
			mWay = "二";
		} else if ("4".equals(mWay)) {
			mWay = "三";
		} else if ("5".equals(mWay)) {
			mWay = "四";
		} else if ("6".equals(mWay)) {
			mWay = "五";
		} else if ("7".equals(mWay)) {
			mWay = "六";
		}
		return " 星期" + mWay;
	}
	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.item_hour_forecast_content, null);
			// 滚动条
			MyHScrollView scrollView = (MyHScrollView) convertView
					.findViewById(R.id.scrollViewContent);
			mObserver.addScrollView(scrollView);
			scrollView.setObserver(mObserver);
		}

		if (mList.size() < position + 1) {
			return convertView;
		}

		TextView time_text = null;
		TextView textView = null;
		ImageView imageView = null;
		
		
		time_text=(TextView)convertView.findViewById(R.id.time_text);
		
		// 分割线
		imageView = (ImageView) convertView.findViewById(R.id.image_line_h);
		if ((position + 1) % 6 == 0) {
			imageView.setBackgroundResource(R.color.white_alpha70);
		} else {
			imageView.setBackgroundResource(R.color.white_alpha20);
		}

		HourForecast pack = mList.get(position);
		
		// 时间
		textView = (TextView) convertView.findViewById(R.id.text_time);
		if (pack.time != null && !"".equals(pack.time)) {
			textView.setText(pack.time + "时");
//			if ("0".equals(pack.time)) {
//				// 零时，文字黄色
//				textView.setTextColor(Color.rgb(252, 238, 9));
//			} else {
//				textView.setTextColor(Color.rgb(255, 255, 255));
//			}
		}
		if(position==0){
			time_text.setVisibility(View.VISIBLE);
			time_text.setText(getTime(pack.w_datetime));
			time_text.setTextColor(Color.rgb(252, 238, 9));
		}else if(!TextUtils.isEmpty(pack.time)){
			if("0".equals(pack.time)){
				time_text.setText(getTime(pack.w_datetime));
				time_text.setTextColor(Color.rgb(252, 238, 9));
				time_text.setVisibility(View.VISIBLE);
			}else{
				time_text.setVisibility(View.GONE);
			}
		}else{
			time_text.setVisibility(View.GONE);
		}
		
		// 天气现象
		TextView weatherDescView = (TextView) convertView
				.findViewById(R.id.image_weather_situation);
		weatherDescView.setText(pack.desc);
//		imageView.setVisibility(View.VISIBLE);
//		if (pack.ico != null && !"".equals(pack.ico)) {
//			try {
//				Bitmap bitmap = ZtqImageTool.getInstance().getWeatherIcon(
//						mContext, pack.isDayTime, pack.ico);
//				imageView.setImageBitmap(bitmap);
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		} else {
//			imageView.setVisibility(View.GONE);
//		}

		// 气温
		textView = (TextView) convertView
				.findViewById(R.id.text_temperature);
		if (pack.temperature != null && !"".equals(pack.temperature)) {
			textView.setText(pack.temperature);
		} else {
			textView.setText("");
		}
		// 降水量
		textView = (TextView) convertView.findViewById(R.id.text_rainfall);
		if (pack.rainfall != null && !"".equals(pack.rainfall)) {
			textView.setText(pack.rainfall);
		} else {
			textView.setText("");
		}
		// 能见度
//		if (pack.visibility != null && !"".equals(pack.visibility)) {
//			textView = (TextView) convertView
//					.findViewById(R.id.text_visibility);
//			textView.setText(pack.visibility);
//		}
		// 风速
		textView = (TextView) convertView.findViewById(R.id.text_windspeed);
		if (pack.windspeed != null && !"".equals(pack.windspeed)) {
			textView.setText(pack.windspeed);
		} else {
			textView.setText("");
		}
		// 风向
		textView = (TextView) convertView
				.findViewById(R.id.text_wind_direction);
		if (pack.winddir != null && !"".equals(pack.winddir)) {
			textView.setText(pack.winddir);
		} else {
			textView.setText("");
		}
		// 相对湿度
		textView = (TextView) convertView
				.findViewById(R.id.text_humidity_relatively);
		if (pack.rh != null && !"".equals(pack.rh)) {
			textView.setText(pack.rh);
		} else {
			textView.setText("");
		}
		// 气压
		textView = (TextView) convertView
				.findViewById(R.id.text_air_presure);
		if (pack.airpressure != null && !"".equals(pack.airpressure)) {
			textView.setText(pack.airpressure);
		} else {
			textView.setText("");
		}

		return convertView;
	}
}
