package com.pcs.ztqtj.control.tool;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.PathEffect;
import android.graphics.Typeface;

import java.math.BigDecimal;

public class DrawViewTool {
	private static DrawViewTool instance;
	public static DrawViewTool getInstance() {
		if (instance == null) {
			instance = new DrawViewTool();
		}
		return instance;
	}
	private DrawViewTool() {
	}

	/**园的半径*/
	public static final float circleR=3;
	/**线的宽度*/
	public static final float lineWidth=1;
	/**文字大小*/
	public static final float textSize=14;

	/**
	 * 获取线
	 * @param color
	 * 颜色值：Color.argb(200, 255, 255, 255)
	 * @param widtha
	 * 		宽度
	 * @return
	 */
	public Paint getMyLine(int color,float widtha){
		Paint p = new Paint();
		p.setColor(color);
		p.setAntiAlias(true);
		p.setStrokeWidth(widtha);
		return p;
	}
	/**
	 * 获取线
	 * @param color
	 * 颜色值：Color.argb(200, 255, 255, 255)
	 * @return
	 */
	public Paint getMyLine(int color){
		Paint p = new Paint();
		p.setColor(color);
		p.setAntiAlias(true);
		return p;
	}
	
	
	/**
	 * 获取边现
	 * @return
	 */
	public Paint getLine(){
		// 边线
		Paint p = new Paint();
		p.setColor(Color.argb(255, 170, 170, 170));
		p.setAntiAlias(true);
		p.setStrokeWidth(2);
		return p;
	}
	
	/**
	 * 获取划虚线逼
	 * @param widthSize
	 * @return
	 */
	public Paint getDottedLine(int color,float widthSize) {
		Paint p = new Paint();
		p.setStyle(Style.STROKE);
		p.setColor(color);
		p.setStrokeWidth(widthSize);
		PathEffect effects = new DashPathEffect(new float[] { 8, 8, 8, 8 }, 1);
		p.setPathEffect(effects);
		p.setAntiAlias(true);
		return p;
	}

	/**
	 * 返回白色文字，宋体
	 * 
	 * @param
	 * @return
	 */
	public Paint getTextPaint(int color ) {
		// °文字
		Paint p = new Paint();
		p.setAntiAlias(true);
		p.setColor(color);
		p.setTypeface(Typeface.create("宋体", Typeface.NORMAL));
		p.setTextAlign(Align.CENTER);
		return p;
	}
	/**
	 * 返回白色文字，宋体
	 *
	 * @param textSize
	 * @return
	 */
	public Paint getTextPaintWhite(float textSize) {
		// °文字
		Paint p = new Paint();
		p.setAntiAlias(true);
		p.setColor(Color.argb(255, 255, 255, 255));
		p.setTextSize(textSize);
		p.setTypeface(Typeface.create("宋体", Typeface.NORMAL));
		p.setTextAlign(Align.CENTER);
		return p;
	}
	/**
	 * 返回白色文字，宋体
	 * 
	 * @param textSize
	 * @return
	 */
	public Paint getTextPaintWhite60(float textSize) {
		// °文字
		Paint p = new Paint();
		p.setAntiAlias(true);
		p.setColor(Color.argb(150, 255, 255, 255));
		p.setTextSize(textSize);
		p.setTypeface(Typeface.create("宋体", Typeface.NORMAL));
		p.setTextAlign(Align.CENTER);
		return p;
	}
	
	/**
	 * 两个单精度相减
	 * @param m
	 * @param c
	 * @return
	 */
	public static float compleFloat(float m, float c) {
		BigDecimal b1 = new BigDecimal(Float.toString(m));
		BigDecimal b2 = new BigDecimal(Float.toString(c));
		Float less = b1.subtract(b2).floatValue();
		return less;
	}

	/**
	 * 两个单精度相加
	 * 
	 * @param m
	 * @param c
	 * @return
	 */
	public static float addFloat(float m, float c) {
		BigDecimal b1 = new BigDecimal(Float.toString(m));
		BigDecimal b2 = new BigDecimal(Float.toString(c));
		Float addFloat = b1.add(b2).floatValue();
		return addFloat;
	}
	
}
