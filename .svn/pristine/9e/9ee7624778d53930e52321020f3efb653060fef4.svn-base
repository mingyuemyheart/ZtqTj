package com.pcs.ztqtj.view.myview.chart;

import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

/**
 * 自定义图表
 * @author E.Sun
 * 2015年12月4日
 */
public class ChartView extends View {

	private Paint mPaint;
	private ChartType chartType = ChartType.NONE_CHART;
	private XYMultipleSeries xyMultipleSeries;
	
	private final String NO_DATA = "暂无图表数据";
	
	/** X轴默认显示刻度数量 */
	private final int DEFAULT_XLABEL_COUNT = 7;
	/** Y轴默认显示刻度数量 */
	private final int DEFAULT_YLABEL_COUNT = 7;
	
	/** 标题 */
	private String chartTitle = "标题", xAxisTitle = "X轴", yAxisTitle = "单位";
	/** 标题字号 */
	private int chartTitleSize = 60, axisTitleSize = 30;
	/** 数值字号 */
	private int valueSize = 30;
	/** 坐标轴显示刻度数量*/
	private int xLabelCount = DEFAULT_XLABEL_COUNT, yLabelCount = DEFAULT_YLABEL_COUNT;
	
	/** 坐标轴位置 */
	private float startX, stopX, startY, stopY;
	/** 图标内边距 */
	private int[] paddings = new int[]{0, 0, 0, 0};
	
	/** 柱状图的图形间隔（百分比数值，默认值：1.0，即间隔大小与图形宽度相等） */
	private float barSpacing = 1;
	/** 坐标轴刻度间隔 */
	private double xLabelSpacing, yLabelSpacing;
	/** 坐标刻度值间隔 */
	private int yValueSpecing;
	
	/** 坐标轴刻度与坐标轴的间距 */
	private float xLabelMargin = 5, yLabelMargin = 5;
	/** 数值与图形的间距 */
	private float valueMargin = 10;
	
	/** 柱状图图形宽度 */
	private double barWidth;
	
	/** Y轴极值 */
	private double yMinValue, yMaxValue;
	
	/** 是否显示数值 */
	private boolean displayValue = true;
	/** 是否显示水平网格 */
	private boolean displayHorizontalGrid = true;
	/** 是否显示虚线网格 */
	private boolean displayDottedGrid = true;
	
	/**
	 * 图表类型
	 * @author E.Sun
	 * 2015年12月2日
	 */
	public enum ChartType {
		
		/**
		 * 折线图
		 */
		LINE_CHART,
		
		/**
		 * 柱状图
		 */
		HISTOGRAM,
		
		NONE_CHART
	}
	
	public enum PaintType {
		/** 图表标题画笔 */
		CHART_TITLE_PAINT,
		/** X轴标题画笔 */
		X_AXIS_TITLE_PAINT,
		/** Y轴标题画笔 */
		Y_AXIS_TITLE_PAINT,
		/** X轴刻度画笔 */
		X_AXIS_LABEL_PAINT,
		/** Y轴刻度画笔 */
		Y_AXIS_LABEL_PAINT,
		/** 网格画笔 */
		GRID_PAINT,
		/** 柱状图画笔 */
		BAR_PAINT,
		/** 折线图画笔 */
		LINE_PAINT,
		/** 数值画笔 */
		VALUE_PAINT
	}
	
	public ChartView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setBackgroundColor(Color.WHITE);
		initPaint();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		float titleHeight = drawChartTitle(canvas);
		if(xyMultipleSeries == null || xyMultipleSeries.getSeriesList().size() == 0) {
			drawNoData(canvas);
			return;
		}
		
		switch (chartType) {
		case LINE_CHART:
			drawLineChart(canvas, titleHeight);
			break;
		case HISTOGRAM:
			drawHistogram(canvas, titleHeight);
			break;
		default:
			break;
		}
	}
	
	/**
	 * 设置图表类型
	 * @param type
	 */
	public void setChartType(ChartType type) {
		chartType = type;
	}
	
	/**
	 * 设置图表数据
	 * @param series
	 */
	public void setChartData(XYMultipleSeries series) {
		xyMultipleSeries = series;
	}
	
	/**
	 * 设置图表内边距
	 * @param paddings
	 */
	public void setPaddings(int[] paddings) {
		this.paddings = paddings;
	}
	
	/**
	 * 设置图表标题
	 * @param title
	 */
	public void setChartTitle(String title) {
		chartTitle = title;
	}
	
	/**
	 * 设置图表标题字体大小
	 * @param size
	 */
	public void setChartTitleSize(int size) {
		chartTitleSize = size;
	}
	
	/**
	 * 设置X轴标题
	 * @param title
	 */
	public void setXAxisTitle(String title) {
		xAxisTitle = title;
	}
	
	/**
	 * 设置Y轴标题
	 * @param title
	 */
	public void setYAxisTitle(String title) {
		yAxisTitle = title;
	}
	
	/**
	 * 设置坐标轴标题字体大小
	 * @param size
	 */
	public void setAxisTitleSize(int size) {
		axisTitleSize = size;
	}
	
	/**
	 * 设置X轴显示刻度数量
	 * @param count
	 */
	public void setXLabelCount(int count) {
		if(count <= 0) {
			xLabelCount = DEFAULT_XLABEL_COUNT;
		} else {
			xLabelCount = count;
		}
	}
	
	/**
	 * 设置Y轴显示刻度数量
	 * @param count
	 */
	public void setYLabelCount(int count) {
		if(count <= 0) {
			yLabelCount = DEFAULT_YLABEL_COUNT;
		} else {
			yLabelCount = count;
		}
	}
	
	/**
	 * 设置Y轴最大值
	 * @param value
	 */
	public void setYMaxValue(double value) {
		yMaxValue = value;
	}
	
	/**
	 * 设置Y轴最小值
	 * @param value
	 */
	public void setYMinValue(double value) {
		yMinValue = value;
	}
	
	/**
	 * 设置是否显示数值，默认显示
	 * @param display
	 */
	public void setDisplayValue(boolean display) {
		displayValue = display;
	}
	
	/**
	 * 设置是否显示垂直网格，默认显示
	 * @param display
	 */
	public void setDisplayHorizontalGrid(boolean display) {
		displayHorizontalGrid = display;
	}
	
	/**
	 * 设置是否显示虚线网格；默认为true，画虚线网格，仅在显示网格时有效
	 * @param display
	 */
	public void setDisplayDottedGrid(boolean display) {
		displayDottedGrid = display;
	}

	/**
	 * 初始化画笔
	 */
	private void initPaint() {
		mPaint = new Paint();
		mPaint.setAntiAlias(true);//去锯齿
	}
	
	/**
	 * 绘制折线图
	 * @param canvas
	 */
	private void drawLineChart(Canvas canvas, float titleHeight) {
		drawLineChartXAxis(canvas);
		drawLineChartAxis(canvas, titleHeight);
		drawLineChartData(canvas);
	}
	
	/**
	 * 绘制柱状图
	 * @param canvas
	 */
	private void drawHistogram(Canvas canvas, float titleHeight) {
		drawHistogramXAxis(canvas);
		drawHistogramAxis(canvas, titleHeight);
		drawHistogramData(canvas);
	}
	
	/**
	 * 绘制图表标题
	 * @param canvas
	 * @return 标题高度
	 */
	private float drawChartTitle(Canvas canvas) {
		mPaint = convertsPaint(mPaint, PaintType.CHART_TITLE_PAINT);
		FontMetrics metrics = mPaint.getFontMetrics();
		float x = canvas.getWidth() / 2;
		float y = Math.abs(metrics.ascent) + paddings[1];
		canvas.drawText(chartTitle, x, y, mPaint);
		return metrics.descent + y;
	}
	
	/**
	 * 绘制暂无数据提示语
	 * @param canvas
	 */
	private void drawNoData(Canvas canvas) {
		mPaint = convertsPaint(mPaint, PaintType.CHART_TITLE_PAINT);
		FontMetrics metrics = mPaint.getFontMetrics();
		float x = canvas.getWidth() / 2;
		float y = canvas.getHeight() / 2 - Math.abs(metrics.ascent);
		canvas.drawText(NO_DATA, x, y , mPaint);
	}
	
	/**
	 * 绘制折线图X轴
	 * @param canvas
	 */
	private void drawLineChartXAxis(Canvas canvas) {
		mPaint = convertsPaint(mPaint, PaintType.X_AXIS_TITLE_PAINT);
		
		FontMetrics metrics = mPaint.getFontMetrics();
		float textHeight = metrics.descent - metrics.ascent;
		
		startX = TextUtils.isEmpty(yAxisTitle) ? 50 : mPaint.measureText(yAxisTitle);
		startX += paddings[0];
		stopX = TextUtils.isEmpty(xAxisTitle) ? canvas.getWidth() - 50 : canvas.getWidth() - mPaint.measureText(xAxisTitle);
		stopX -= paddings[2];
		startY = canvas.getHeight() - textHeight - yLabelMargin;
		startY -= paddings[3];
		
		// X轴
		canvas.drawLine(startX, startY, stopX, startY, mPaint);
		canvas.drawText(xAxisTitle, stopX, startY, mPaint);
		
		// X轴刻度
		mPaint = convertsPaint(mPaint, PaintType.X_AXIS_LABEL_PAINT);
		xLabelSpacing = ((stopX - startX) / xLabelCount);
		barWidth = xLabelSpacing * (1 / (1 + barSpacing));
		
		List<String> xLabels = xyMultipleSeries.getXLabels();
		for(int i = 0; i < xLabels.size() && i < xLabelCount; i++) {
			canvas.drawText(xLabels.get(i), (float) (startX + xLabelSpacing / 2 + xLabelSpacing * i), startY + Math.abs(metrics.ascent), mPaint);
		}
	}
	
	/**
	 * 绘制柱状图X轴
	 * @param canvas
	 */
	private void drawHistogramXAxis(Canvas canvas) {
		mPaint = convertsPaint(mPaint, PaintType.X_AXIS_TITLE_PAINT);
		
		FontMetrics metrics = mPaint.getFontMetrics();
		float textHeight = metrics.descent - metrics.ascent;
		
		startX = TextUtils.isEmpty(yAxisTitle) ? 50 : mPaint.measureText(yAxisTitle);
		startX += paddings[0];
		stopX = TextUtils.isEmpty(xAxisTitle) ? canvas.getWidth() - 50 : canvas.getWidth() - mPaint.measureText(xAxisTitle);
		stopX -= paddings[2];
		startY = canvas.getHeight() - textHeight - yLabelMargin;
		startY -= paddings[3];
		
		// X轴
		canvas.drawLine(startX, startY, stopX, startY, mPaint);
		canvas.drawText(xAxisTitle, stopX, startY, mPaint);
		
		// X轴刻度
		mPaint = convertsPaint(mPaint, PaintType.X_AXIS_LABEL_PAINT);
		xLabelSpacing = ((stopX - startX) / xLabelCount);
		barWidth = xLabelSpacing * (1 / (1 + barSpacing));
		
		List<String> xLabels = xyMultipleSeries.getXLabels();
		for(int i = 0; i < xLabels.size() && i < xLabelCount; i++) {
			canvas.drawText(xLabels.get(i), (float) (startX + barWidth / 2 + (xLabelSpacing - barWidth) / 2 + xLabelSpacing * i), startY + Math.abs(metrics.ascent), mPaint);
		}
	}
	
	/**
	 * 绘制坐标轴
	 * @param canvas
	 * @param top 坐标轴与顶部的距离
	 */
	private void drawLineChartAxis(Canvas canvas, float top) {
		// Y轴
		mPaint = convertsPaint(mPaint, PaintType.Y_AXIS_TITLE_PAINT);
		FontMetrics metrics = mPaint.getFontMetrics();
		float yAxisTitleHeight = metrics.descent - metrics.ascent;
		stopY = top + yAxisTitleHeight * 2;
		canvas.drawLine(startX, startY, startX, stopY, mPaint);
		canvas.drawText(yAxisTitle, startX, stopY - yAxisTitleHeight, mPaint);
		
		// Y轴刻度
		mPaint = convertsPaint(mPaint, PaintType.Y_AXIS_LABEL_PAINT);
		yLabelSpacing = (startY - stopY) / yLabelCount;
		double minValue = 0;
		double maxValue = 0;
		List<XYSeries> seriesList = xyMultipleSeries.getSeriesList();
		for (XYSeries series : seriesList) {
			minValue = series.getMinValue();
			maxValue = series.getMaxValue();
			yMaxValue = yMaxValue < maxValue ? maxValue : yMaxValue;
			yMinValue = yMinValue < minValue ? yMinValue : minValue;
		}
		yValueSpecing = (int) Math.ceil((yMaxValue - yMinValue) / yLabelCount);
		yValueSpecing = yValueSpecing == 0 ? 1 : yValueSpecing;
		double value;
		for(int i = 0; i <= yLabelCount; i++) {
			value = yMinValue + yValueSpecing * i;
			canvas.drawText(String.format("%.0f", value), startX - xLabelMargin, (float) (startY + metrics.descent - yLabelSpacing * i), mPaint);
		}
		
		// 水平网格
		if(displayHorizontalGrid) {
			drawHorizontalGrid(canvas);
		}
	}
	
	/**
	 * 绘制坐标轴
	 * @param canvas
	 * @param top 坐标轴与顶部的距离
	 */
	private void drawHistogramAxis(Canvas canvas, float top) {
		// Y轴
		mPaint = convertsPaint(mPaint, PaintType.Y_AXIS_TITLE_PAINT);
		FontMetrics metrics = mPaint.getFontMetrics();
		float yAxisTitleHeight = metrics.descent - metrics.ascent;
		stopY = top + yAxisTitleHeight * 2;
		canvas.drawLine(startX, startY, startX, stopY, mPaint);
		canvas.drawText(yAxisTitle, startX, stopY - yAxisTitleHeight, mPaint);
		
		// Y轴刻度
		mPaint = convertsPaint(mPaint, PaintType.Y_AXIS_LABEL_PAINT);
		yLabelSpacing = (startY - stopY) / yLabelCount;
		double minValue = 0;
		double maxValue = 0;
		XYSeries series = xyMultipleSeries.getSeriesList().get(0);
		minValue = series.getMinValue();
		maxValue = series.getMaxValue();
		yMaxValue = yMaxValue < maxValue ? maxValue : yMaxValue;
		yMinValue = yMinValue < minValue ? yMinValue : minValue;
		yValueSpecing = (int) Math.ceil((yMaxValue - yMinValue) / yLabelCount);
		yValueSpecing = yValueSpecing == 0 ? 1 : yValueSpecing;
		double value;
		for(int i = 0; i <= yLabelCount; i++) {
			value = yMinValue + yValueSpecing * i;
			canvas.drawText(String.format("%.0f", value), startX - xLabelMargin, (float) (startY + metrics.descent - yLabelSpacing * i), mPaint);
		}
		
		// 水平网格
		if(displayHorizontalGrid) {
			drawHorizontalGrid(canvas);
		}
	}
	
	/**
	 * 绘制水平网格
	 * @param canvas
	 */
	private void drawHorizontalGrid(Canvas canvas) {
		mPaint = convertsPaint(mPaint, PaintType.GRID_PAINT);
		float gridY;
		float dottedStopX;
		for(int i = 0; i <= yLabelCount; i++) {
			gridY = (float) (startY - yLabelSpacing * i);
			if(displayDottedGrid) {
				for(float j = startX; j < stopX; j+=40) {
					dottedStopX = j < stopX - 20 ? j+20 : stopX;
					canvas.drawLine(j, gridY, dottedStopX, gridY, mPaint);
				}
			} else {
				canvas.drawLine(startX, gridY, stopX, gridY, mPaint);
			}
		}
	}
	
	/**
	 * 绘制折线图数据
	 * @param canvas
	 */
	private void drawLineChartData(Canvas canvas) {
		List<XYSeries> seriesList = xyMultipleSeries.getSeriesList();
		double value;
		float valueX = 0, lastX;
		float valueY = 0, lastY;
		float radius;
		double yMinus = yMaxValue - yMinValue;
		float yLength = startY - stopY;
		for(XYSeries series : seriesList) {
			mPaint = convertsPaint(mPaint, PaintType.LINE_PAINT, series.getSeriesColor(), series.getLineWidth());
			List<Double> values = series.getValues();
			radius = series.getCircleRadius();
			for(int i = 0; i < values.size() && i < xLabelCount; i++) {
				value = values.get(i);
				lastX = valueX;
				lastY = valueY;
				valueX = (float) (startX + xLabelSpacing / 2 + xLabelSpacing * i);
				valueY = (float) (startY - ((value - yMinValue) / yMinus * yLength));
				if(i > 0) {
					canvas.drawLine(lastX, lastY, valueX, valueY, mPaint);
				}
				canvas.drawCircle(valueX, valueY, radius, mPaint);
			}
			
			if(displayValue) {
				mPaint = convertsPaint(mPaint, PaintType.VALUE_PAINT, series.getValueColor());
				for(int i = 0; i < values.size() && i < xLabelCount; i++) {
					value = values.get(i);
					valueX = (float) (startX + xLabelSpacing / 2 + xLabelSpacing * i);
					valueY = (float) (startY - ((value - yMinValue) / yMinus * yLength) - radius / 2 - valueMargin);
					canvas.drawText(String.format("%.1f", value), valueX, valueY, mPaint);
				}
			}
		}
	}
	
	/**
	 * 绘制柱状图数据
	 * @param canvas
	 */
	private void drawHistogramData(Canvas canvas) {
		XYSeries series = xyMultipleSeries.getSeriesList().get(0);
		mPaint = convertsPaint(mPaint, PaintType.BAR_PAINT, series.getSeriesColor());
		List<Double> values = series.getValues();
		double value;
		float valueX, valueY;
		for(int i = 0; i < values.size() && i < xLabelCount; i++) {
			value = values.get(i);
			valueX = (float) (startX + barWidth / 2 + xLabelSpacing * i);
			valueY = (float) (startY - ((value - yMinValue) / yValueSpecing * yLabelSpacing));
			canvas.drawRect(valueX, valueY, (float) (valueX + barWidth), startY, mPaint);
		}
		
		if(displayValue) {
			mPaint = convertsPaint(mPaint, PaintType.VALUE_PAINT, series.getValueColor());
			for(int i = 0; i < values.size() && i < xLabelCount; i++) {
				value = values.get(i);
				valueX = (float) (startX + barWidth / 2 + xLabelSpacing * i);
				valueY = (float) (startY - ((value - yMinValue) / yValueSpecing * yLabelSpacing) - valueMargin);
				canvas.drawText(String.format("%.1f", value), (float) (valueX + barWidth / 2), valueY, mPaint);
			}
		}
	}
	
	/**
	 * 转换画笔属性
	 * @param paint
	 * @param type
	 * @return
	 */
	private Paint convertsPaint(Paint paint, PaintType type) {
		switch (type) {
		case CHART_TITLE_PAINT:
			paint.setColor(Color.BLACK);
			paint.setTextAlign(Align.CENTER);
			paint.setTextSize(chartTitleSize);
			break;
		case X_AXIS_TITLE_PAINT:
			paint.setColor(Color.BLACK);
			paint.setTextAlign(Align.LEFT);
			paint.setTextSize(axisTitleSize);
			break;
		case X_AXIS_LABEL_PAINT:
			paint.setColor(Color.BLACK);
			paint.setTextAlign(Align.CENTER);
			paint.setTextSize(axisTitleSize);
			break;
		case Y_AXIS_TITLE_PAINT:
			paint.setColor(Color.BLACK);
			paint.setTextAlign(Align.RIGHT);
			paint.setTextSize(axisTitleSize);
			break;
		case Y_AXIS_LABEL_PAINT:
			paint.setColor(Color.BLACK);
			paint.setTextAlign(Align.RIGHT);
			paint.setTextSize(axisTitleSize);
			break;
		case GRID_PAINT:
			paint.setColor(Color.GRAY);
			break;
		}
		return paint;
	}
	
	/**
	 * 转换画笔属性
	 * @param paint
	 * @param type
	 * @param color
	 * @return
	 */
	private Paint convertsPaint(Paint paint, PaintType type, int color) {
		switch (type) {
		case BAR_PAINT:
			paint.setStyle(Style.FILL);
			paint.setColor(color);
			break;
		case VALUE_PAINT:
			paint.setTextAlign(Align.CENTER);
			paint.setTextSize(valueSize);
			paint.setColor(color);
			break;
		}
		return paint;
	}
	
	/**
	 * 转换画笔属性
	 * @param paint
	 * @param type
	 * @param color
	 * @param width
	 * @return
	 */
	private Paint convertsPaint(Paint paint, PaintType type, int color, float width) {
		switch (type) {
		case LINE_PAINT:
			paint.setStyle(Style.FILL);
			paint.setColor(color);
			paint.setStrokeWidth(width);
			break;
		}
		return paint;
	}
	
}