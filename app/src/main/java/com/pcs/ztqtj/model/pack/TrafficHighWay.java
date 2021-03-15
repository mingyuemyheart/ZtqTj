package com.pcs.ztqtj.model.pack;

import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalTrafficPoints;

/**
 * 交通：公路信息
 * 
 * @author JiangZy
 * 
 */
public class TrafficHighWay {
	public String ID = "";
	public String NAME = "";
	public String SEARCH_NAME = "";
	public double SHOW_LATITUDE = 0;
	public double SHOW_LONGITUDE = 0;
	public String IMAGE_PATH = "";
	public String DETAIL_IMAGE = "";

	public PackLocalTrafficPoints points = new PackLocalTrafficPoints();
}
