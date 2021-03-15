package com.pcs.ztqtj.view.myview.chart;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;

/**
 * 图标数据
 * @author E.Sun
 * 2015年12月4日
 */
public class XYSeries {

	/** 折线默认宽度 */
	public static final int DEFAULT_LINE_WIDTH = 5;
	/** 折线圆点默认半径 */
	public static final int DEFAULT_CIRCLE_RADIUS = 10;
	
	private int seriesColor = Color.BLACK;
	private int valueColor = Color.BLACK;
	private float lineWidth = DEFAULT_LINE_WIDTH;
	private float circleRadius = DEFAULT_CIRCLE_RADIUS;
	
	private List<Double> values = new ArrayList<Double>();
	
	public void addValue(double value) {
		values.add(value);
	}
	
	public List<Double> getValues() {
		return values;
	}
	
	public double getMinValue() {
		double min = 0;
		for(double value : values) {
			min = value < min ? value : min;
		}
		return min;
	}
	
	public double getMaxValue() {
		double max = 0;
		for(double value : values) {
			max = value > max ? value : max;
		}
		return max;
	}
	
	public void setColor(int color) {
		seriesColor = color;
	}
	
	public int getSeriesColor() {
		return seriesColor;
	}

	public int getValueColor() {
		return valueColor;
	}

	public void setValueColor(int valueColor) {
		this.valueColor = valueColor;
	}

	public float getLineWidth() {
		return lineWidth;
	}

	public void setLineWidth(float lineWidth) {
		this.lineWidth = lineWidth;
	}

	public float getCircleRadius() {
		return circleRadius;
	}

	public void setCircleRadius(float circleRadius) {
		this.circleRadius = circleRadius;
	}
	
}
