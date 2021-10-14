package com.pcs.ztqtj.view.myview;

import java.util.ArrayList;
import java.util.List;

public class AqiDto {

	public float x = 0;//x轴坐标点
	public float y = 0;//y轴坐标点
	public String date = null;//时间
	public String aqi = null;//空气质量
	public String pm2_5 = null;
	public String pm10 = null;
	public String NO2 = null;
	public String SO2 = null;
	public String O3 = null;
	public String CO = null;
	public List<AqiDto> aqiList = new ArrayList<>();

}
