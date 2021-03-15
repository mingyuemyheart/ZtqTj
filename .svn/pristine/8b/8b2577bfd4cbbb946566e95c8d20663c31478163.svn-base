package com.pcs.ztqtj.view.myview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.PathEffect;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import com.pcs.lib.lib_pcs_v3.control.tool.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * @author Z 水位站信息
 */
public class WeatherView extends View {
	/**
	 * 趋势图的高度
	 */
	private float viewHeight;

	/**
	 * 趋势图的宽度
	 */
	private float viewWidth;

	/** 高度点 */
	private Paint h_point;
	/** 连线 */
	private Paint lineMax;
	/** 公共线 */
	private Paint lineCom;
	/** 公共线偏灰 */
	private Paint lineCom2;
	/** 文字 */
	private Paint mTextPaint;
	/** 柱形线 */
	private Paint line1;
	private float hight_value;

	/** 红色线 */
	private Paint lineRed;
	/** 黄色先 */
	private Paint lineYellow;

	/** 当前值 */
	private List<Float> tempValue = new ArrayList<Float>();
	/** 当前值对应的高度 */
	private List<Float> viewTempValueHight = new ArrayList<Float>();
	/** 横轴时间点 */
	private List<String> xValue = new ArrayList<String>();
	/** Y轴的值 */
	private List<String> yValue = new ArrayList<String>();

	/** 区间值 */
	private float section = 5;
	/** 圆点半径 */
	private float CircleR = 4;
	/** 顶部留高 */
	private float margTopH = 40;
	/** 顶部留高 */
	private float margBottonH = 40;
	/** 右边距离边 */
	private float margRightW = 20;

	/** 左边距离边：实时变化，在draw中改变值 */
	private float margLeftValue = 10;

	/** Y周区间个数,(修改此值必须同时修改h_val默认个数) */
	private int countSectionY = 8;
	/** 横轴区间个数 */
	private int countSecitonX = 25;

	/** 文字大小 */
	private float textSzie = 15;
	/** 线宽度 */
	private float lineWidth = 2;
	private float rectangleWidth = 10;

	public WeatherView(Context context) {
		super(context);

	}

	/** 单位 */
	public String company_v = "水位m";
	private Context context;

	public WeatherView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void chagnValue(Context context) {
		CircleR = Util.dip2px(context, 4);
		/** 顶部留高 */
		margTopH = Util.dip2px(context, 40);
		/** 顶部留高 */
		margBottonH = Util.dip2px(context, 40);
		/** 右边距离边 */
		margRightW = Util.dip2px(context, 5);
		/** 左边距离边：实时变化，在draw中改变值 */
		margLeftValue = Util.dip2px(context, 5);

		textSzie = Util.dip2px(context, 13);
		lineWidth = Util.dip2px(context, 2);
		rectangleWidth = Util.dip2px(context, 10);
	}

	private void init(Context context) {
		chagnValue(context);
		// 最高点
		h_point = new Paint();
		h_point.setAntiAlias(true);
		h_point.setColor(Color.argb(255, 56, 145, 167));

		// 公共线
		lineCom = new Paint();
		lineCom.setAntiAlias(true);
		lineCom.setStrokeWidth(1);
		lineCom.setColor(Color.argb(255, 80, 80, 80));

		// 红色线
		lineRed = new Paint();
		lineRed.setAntiAlias(true);
		lineRed.setStrokeWidth(2);
		lineRed.setColor(Color.argb(255, 175, 13, 13));
		// 黄色线
		lineYellow = new Paint();
		lineYellow.setAntiAlias(true);
		lineYellow.setStrokeWidth(2);
		lineYellow.setColor(Color.argb(255, 196, 171, 0));
		// 公共线
		lineCom2 = new Paint();
		lineCom2.setAntiAlias(true);
		lineCom2.setStrokeWidth(1);
		lineCom2.setStyle(Paint.Style.STROKE);
		lineCom2.setColor(Color.argb(255, 95, 95, 95));
		PathEffect effects = new DashPathEffect(new float[] { 1, 2, 4, 8 }, 1);
		lineCom2.setPathEffect(effects);

		// 最高点线
		lineMax = new Paint();
		lineMax.setAntiAlias(true);
		lineMax.setStrokeWidth(lineWidth);
		lineMax.setColor(Color.argb(255, 56, 145, 167));

		// 文字颜色
		mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);
		mTextPaint.setColor(Color.argb(255, 98, 98, 98));

		mTextPaint.setTypeface(Typeface.create("宋体", Typeface.NORMAL));
		mTextPaint.setTextAlign(Align.CENTER);
		mTextPaint.setTextSize(textSzie);

		line1 = new Paint();
		line1.setAntiAlias(true);
		line1.setStrokeWidth(rectangleWidth);
		line1.setColor(Color.argb(255, 72, 117, 165));

	}

	/**
	 * @param tempValue
	 *            值
	 * @param timeValue
	 *            横轴文字
	 */
	public void setValue(List<Float> tempValue, List<String> timeValue) {
		if (tempValue.size() > 0) {
			for (int i = 0; i < tempValue.size(); i++) {
				if (i >= countSecitonX) {
				} else {
					this.tempValue.add(tempValue.get(i));
					this.xValue.add(timeValue.get(i));
				}
			}
		}
	}

	/**
	 * 使用前先清空数据，
	 */
	public void cleanData() {
		this.tempValue.clear();
		this.viewTempValueHight.clear();
		this.xValue.clear();
		this.yValue.clear();
		this.showWarnLine = false;
		this.showProtectLine = false;
		this.nowDrowPoint = 1;
	}

	public void setCompany(String company_v) {
		this.company_v = company_v;
	}

	public void startDrawView() {
		getSectionValue();
		getYValue();
		this.invalidate();
		handler.removeMessages(0);
		handler.sendEmptyMessageDelayed(0, 100);
	}

	private boolean showWarnLine = false;
	private boolean showProtectLine = false;
	private float warnLineValue;
	private float protectLineValue;

	/**
	 * 设置预警水位
	 * 
	 * @param warnLine
	 */
	public void setWranLine(float warnLine) {
		showWarnLine = true;
		warnLineValue = warnLine;
	}

	/**
	 * 设置保证水位
	 * 
	 * @param protectLine
	 */
	public void setProtectLine(float protectLine) {
		showProtectLine = true;
		protectLineValue = protectLine;
	}

	private float getDrawLineHight(float value) {
		float max = Collections.max(tempValue);
		float min = Collections.min(tempValue);

		if (showWarnLine) {
			if (Float.compare(warnLineValue, max) > 0) {
				max = warnLineValue;
			} else if (Float.compare(warnLineValue, min) < 0) {
				min = warnLineValue;
			}
		}

		if (showProtectLine) {
			if (Float.compare(protectLineValue, max) > 0) {
				max = protectLineValue;
			} else if (Float.compare(protectLineValue, min) < 0) {
				min = protectLineValue;
			}
		}

		float valueCount = max - min;
		if (Float.compare(valueCount, 0) == 0) {
			return 0f;
		} else {
			float section = value - min;
			float sacle = section / valueCount;
			float viewH = viewHeight;
			float resultH = viewH * sacle;
			return resultH;
		}
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			nowDrowPoint++;
			if (nowDrowPoint > tempValue.size()) {
				handler.removeMessages(0);
			} else {
				handler.removeMessages(0);
				handler.sendEmptyMessageDelayed(0, 100);
				invalidate();
			}
		}
	};
	private int nowDrowPoint = 0;

	/**
	 * 换算高度值
	 * 
	 * @param value
	 *            总的高度值
	 */
	private void getHight(float value) {
		float max = Collections.max(tempValue);
		float min = Collections.min(tempValue);

		if (showWarnLine) {
			if (Float.compare(warnLineValue, max) > 0) {
				max = warnLineValue;
			} else if (Float.compare(warnLineValue, min) < 0) {
				min = warnLineValue;
			}
		}

		if (showProtectLine) {
			if (Float.compare(protectLineValue, max) > 0) {
				max = protectLineValue;
			} else if (Float.compare(protectLineValue, min) < 0) {
				min = protectLineValue;
			}
		}

		float valueCount = max - min;
		if (Float.compare(valueCount, 0) == 0) {
			for (int i = 0; i < tempValue.size(); i++) {
				// 当前值减少最低值
				viewTempValueHight.add(0f);
			}
		} else {
			for (int i = 0; i < tempValue.size(); i++) {
				// 当前值减少最低值
				float section = tempValue.get(i) - min;
				viewTempValueHight.add(value * (section / valueCount));
			}
		}
	}

	// 获取Y轴区间值
	private void getYValue() {
		if (this.tempValue.size() != 0) {
			float max = Collections.max(tempValue);
			float min = Collections.min(tempValue);

			if (showWarnLine) {
				if (Float.compare(warnLineValue, max) > 0) {
					max = warnLineValue;
				} else if (Float.compare(warnLineValue, min) < 0) {
					min = warnLineValue;
				}
			}

			if (showProtectLine) {
				if (Float.compare(protectLineValue, max) > 0) {
					max = protectLineValue;
				} else if (Float.compare(protectLineValue, min) < 0) {
					min = protectLineValue;
				}
			}

			if (Float.compare(min, max) == 0) {
				for (int i = 0; i < countSectionY + 1; i++) {
					// 如果所有值为同一个值则Y轴最低值为max
					float value = (max + (countSectionY - i) * section);
					yValue.add(String.format("%.2f", value));
				}
			} else {
				for (int i = 0; i < countSectionY + 1; i++) {
					float value = (max - i * section);
					yValue.add(String.format("%.2f", value));
				}
			}
		}

	}

	/**
	 * 计算区间值
	 * 
	 * @return
	 */
	private void getSectionValue() {
		if (this.tempValue.size() != 0) {
			float max = Collections.max(tempValue);
			float min = Collections.min(tempValue);

			if (showWarnLine) {
				if (Float.compare(warnLineValue, max) > 0) {
					max = warnLineValue;
				} else if (Float.compare(warnLineValue, min) < 0) {
					min = warnLineValue;
				}
			}

			if (showProtectLine) {
				if (Float.compare(protectLineValue, max) > 0) {
					max = protectLineValue;
				} else if (Float.compare(protectLineValue, min) < 0) {
					min = protectLineValue;
				}
			}

			float value = max - min;
			if (Float.compare(min, max) == 0) {
				section = 1;
			} else {
				section = value / countSectionY;
			}
		} else {
			section = 1;
		}

	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);

		// 控件高度-距头，和距尾的值即为实际使用高度
		viewHeight = getHeight() - margTopH - margBottonH;
		viewWidth = getWidth();
		// 计算文字高度
		FontMetrics fontMetrics = mTextPaint.getFontMetrics();
		float fontHeight = fontMetrics.bottom - fontMetrics.top;
		canvas.drawText(company_v, margTopH * 2, fontHeight, mTextPaint);
		// canvas.drawText("h", viewWidth - fontHeight / 3, margTopH +
		// viewHeight + fontHeight, mTextPaint);
		// 区间高度
		float max;
		float sectionHight = viewHeight / countSectionY;
		if (tempValue.size() == 0) {
			max = 0f;
		} else {
			max = Collections.max(tempValue);
		}
		String maxStr = String.valueOf(max);
		// 根据Y轴值的长度缩进值
		margLeftValue = fontHeight * 3;

		viewWidth = viewWidth - margLeftValue - margRightW;
		// 宽度区间
		float sectionWidth = viewWidth / countSecitonX;
		for (int i = 0; i < countSectionY + 1; i++) {
			// 横线，
			if (i == countSectionY) {
				canvas.drawLine(margLeftValue, margTopH + sectionHight * i, viewWidth + margLeftValue, margTopH + sectionHight * i, lineCom);
				canvas.drawLine(viewWidth + margLeftValue, margTopH + sectionHight * i, viewWidth + margLeftValue - margRightW, margTopH + sectionHight * i - margRightW, lineCom);
			} else {
				canvas.drawLine(margLeftValue, margTopH + sectionHight * i, viewWidth + margLeftValue - sectionWidth, margTopH + sectionHight * i, lineCom);
			}
		}
		// 画y轴坐标值
		for (int i = 0; i < yValue.size(); i++) {
			canvas.drawText(yValue.get(i), margLeftValue / 2, margTopH + sectionHight * i + fontHeight / 3, mTextPaint);
		}
		for (int i = 0; i < countSecitonX; i++) {
			// 竖线
			if (i % 4 == 0 || i == countSecitonX - 1) {
				if (i == 0) {
					canvas.drawLine(margLeftValue + sectionWidth * i, 0, margLeftValue + sectionWidth * i, margTopH + viewHeight, lineCom);

					canvas.drawLine(margLeftValue + sectionWidth * i, 0, margLeftValue + sectionWidth * i + margRightW, margRightW, lineCom);
				} else {
					canvas.drawLine(margLeftValue + sectionWidth * i, margTopH, margLeftValue + sectionWidth * i, margTopH + viewHeight, lineCom);
				}
				// 底部文字
				if (i < xValue.size()) {
					canvas.drawText(xValue.get(i).toString(), margLeftValue + sectionWidth * i, margTopH + viewHeight + fontHeight, mTextPaint);
				}
			}
		}
		if (tempValue.size() == 0) {
			return;
		}

		getHight(viewHeight);
		// for (int i = 0; i < tempValue.size(); i++) {
		for (int i = 0; i < nowDrowPoint; i++) {
			float x = margLeftValue + sectionWidth * i;
			float y = margTopH + viewHeight - viewTempValueHight.get(i);
			// if (i % 2 != 0) {
			// canvas.drawLine(x, y, x, margTopH + viewHeight, line1);
			// }
			if (i == 0) {
			} else {
				float x_pro = margLeftValue + sectionWidth * (i - 1);
				float y_pro = margTopH + viewHeight - viewTempValueHight.get(i - 1);
				canvas.drawLine(x_pro, y_pro, x, y, lineMax);
			}
			canvas.drawCircle(x, y, CircleR, h_point);
		}
		if (showWarnLine) {
			canvas.drawLine(margLeftValue, margTopH + viewHeight - getDrawLineHight(warnLineValue), viewWidth + margLeftValue - sectionWidth, margTopH + viewHeight
					- getDrawLineHight(warnLineValue), lineRed);
		}
		if (showProtectLine) {
			canvas.drawLine(margLeftValue, margTopH + viewHeight - getDrawLineHight(protectLineValue), viewWidth + margLeftValue - sectionWidth, margTopH + viewHeight
					- getDrawLineHight(protectLineValue), lineYellow);
		}

	}
}
