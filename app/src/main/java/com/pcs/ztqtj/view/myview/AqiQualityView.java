package com.pcs.ztqtj.view.myview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.util.CommonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 绘制aqi平滑曲线
 */
public class AqiQualityView extends View{
	
	private Context mContext;
	private List<AqiDto> tempList = new ArrayList<>();
	private int maxTemp = 0;//最高温度
	private int minTemp = 1000;//最低温度
	private Paint lineP = null;//画线画笔
	private Paint textP = null;//写字画笔
	private Paint roundP = null;

	private String[] yDivider;//y轴坐标
	private int[] colors;//图例色块
	private String[] texts;//文字
	private List<Integer> indexList = new ArrayList<>();//用于存放符合条件的下标

	public AqiQualityView(Context context) {
		super(context);
		mContext = context;
		init();
	}
	
	public AqiQualityView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}
	
	public AqiQualityView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mContext = context;
		init();
	}
	
	private void init() {
		lineP = new Paint();
		lineP.setStyle(Style.STROKE);
		lineP.setStrokeCap(Paint.Cap.ROUND);
		lineP.setAntiAlias(true);
		
		textP = new Paint();
		textP.setAntiAlias(true);
		
		roundP = new Paint();
		roundP.setStyle(Style.FILL);
		roundP.setStrokeCap(Paint.Cap.ROUND);
		roundP.setAntiAlias(true);
	}
	
	/**
	 * 对cubicView进行赋值
	 */
	public void setData(List<AqiDto> dataList, String[] yDivider, int[] colors, String[] texts) {
		this.yDivider = yDivider;
		this.colors = colors;
		this.texts = texts;
		if (!dataList.isEmpty()) {
			tempList.clear();
			tempList.addAll(dataList);
			
			for (int i = 0; i < tempList.size(); i++) {
				String value = tempList.get(i).aqi;
				if (TextUtils.isEmpty(value)) {
					continue;
				}
				if (maxTemp <= Integer.valueOf(value)) {
					maxTemp = Integer.valueOf(value);
				}
				if (minTemp >= Integer.valueOf(value)) {
					minTemp = Integer.valueOf(value);
				}
			}

			for (int i = 0; i < yDivider.length-1; i++) {
				int val1 = Integer.valueOf(yDivider[i].trim());
				int val2 = Integer.valueOf(yDivider[i+1].trim());
				if (minTemp >= val1 && minTemp < val2) {
					minTemp = val1;
					if (!indexList.contains(i)) {
						indexList.add(i);
					}
					if (!indexList.contains(i+1)) {
						indexList.add(i+1);
					}
				}
				if (maxTemp > val1 && maxTemp <= val2) {
					maxTemp = val2;
					if (!indexList.contains(i)) {
						indexList.add(i);
					}
					if (!indexList.contains(i+1)) {
						indexList.add(i+1);
					}
				}
			}
			Log.e("ttttt", "minTemp="+minTemp+",maxTemp="+maxTemp);
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (tempList.isEmpty()) {
			return;
		}
		
		float w = canvas.getWidth();
		float h = canvas.getHeight();
		float chartW = w-CommonUtil.dip2px(mContext, 140);
		float chartH = h-CommonUtil.dip2px(mContext, 50);
		float leftMargin = CommonUtil.dip2px(mContext, 70);
		float rightMargin = CommonUtil.dip2px(mContext, 70);
		float topMargin = CommonUtil.dip2px(mContext, 20);
		float bottomMargin = CommonUtil.dip2px(mContext, 30);

		int size = tempList.size();
		float itemWidth = chartW/(size-1);
		//获取曲线上每个温度点的坐标
		for (int i = 0; i < size; i++) {
			AqiDto dto = tempList.get(i);
			dto.x = itemWidth*i + leftMargin;

			if (TextUtils.isEmpty(dto.aqi)) {
				dto.y = -1;
			} else {
				float value = Float.valueOf(dto.aqi);
				dto.y = chartH - chartH*(value-minTemp)/(maxTemp-minTemp) + topMargin;
			}
			tempList.set(i, dto);

			if (i % 2 == 1) {
				//绘制纵向分割线
				lineP.setStyle(Style.FILL_AND_STROKE);
				lineP.setColor(0x20ffffff);
				lineP.setStrokeWidth(CommonUtil.dip2px(mContext, 1));
				canvas.drawRect(new RectF(dto.x-itemWidth/2, topMargin, dto.x+itemWidth/2, chartH+topMargin), lineP);
			}
		}
		
		for (int i = 0; i < indexList.size(); i++) {
			int index = indexList.get(i);
			int value = Integer.valueOf(yDivider[index].trim());
			int color = colors[index];
			float dividerY = chartH - chartH*(value-minTemp)/(maxTemp-minTemp) + topMargin;

			//绘制左侧图例色块
			lineP.setStyle(Style.FILL_AND_STROKE);
			lineP.setColor(color);
			lineP.setStrokeWidth(CommonUtil.dip2px(mContext, 1));
			canvas.drawRect(new RectF(CommonUtil.dip2px(mContext, 26), topMargin, CommonUtil.dip2px(mContext, 30), dividerY), lineP);
			
			//绘制横向刻度线对应的值
			textP.setColor(Color.WHITE);
			textP.setTextSize(CommonUtil.dip2px(mContext, 12));
			canvas.drawText(String.valueOf(value), CommonUtil.dip2px(mContext, 5), dividerY+CommonUtil.dip2px(mContext, 6), textP);
			
			//绘制横向刻度线
			lineP.setColor(0x80dfdfdf);
			lineP.setStrokeWidth(CommonUtil.dip2px(mContext, 1));
			canvas.drawLine(CommonUtil.dip2px(mContext, 26), dividerY, w-CommonUtil.dip2px(mContext, 5), dividerY, lineP);
		}

		for (int i = 0; i < indexList.size()-1; i++) {
			int index = indexList.get(i);
			int index2 = indexList.get(i+1);
			int value = Integer.valueOf(yDivider[index].trim());
			int value2 = Integer.valueOf(yDivider[index2].trim());
			int color = colors[index];
			String text = texts[index];
			float dividerY = chartH - chartH*(value-minTemp)/(maxTemp-minTemp) + topMargin;
			float dividerY2 = chartH - chartH*(value2-minTemp)/(maxTemp-minTemp) + topMargin;

			//绘制等级文字
			textP.setColor(color);
			textP.setTextSize(CommonUtil.dip2px(mContext, 12));
			float textHeight = CommonUtil.dip2px(mContext, 6);
			if (text.length() < 4) {
				canvas.drawText(text, chartW+CommonUtil.dip2px(mContext, 120), (dividerY-dividerY2)/2+dividerY2+textHeight, textP);
			} else {
				canvas.drawText(text, chartW+CommonUtil.dip2px(mContext, 85), (dividerY-dividerY2)/2+dividerY2+textHeight, textP);
			}
		}
		
		//绘制贝塞尔曲线
		for (int i = 0; i < size-1; i++) {
			AqiDto dto = tempList.get(i);
			AqiDto dto2 = tempList.get(i+1);
			float x1 = dto.x;
			float y1 = dto.y;
			float x2 = dto2.x;
			float y2 = dto2.y;

			if (y1 == -1) {
				y1 = findNear(i);
			}
			if (y2 == -1) {
				y2 = findNear(i+1);
			}
			
			float wt = (x1 + x2) / 2;
			
			float x3 = wt;
			float y3 = y1;
			float x4 = wt;
			float y4 = y2;

			lineP.setColor(Color.WHITE);
			lineP.setStyle(Style.STROKE);
			lineP.setStrokeWidth(CommonUtil.dip2px(mContext, 1.5f));
			Path pathLow = new Path();
			pathLow.moveTo(x1, y1);
			pathLow.cubicTo(x3, y3, x4, y4, x2, y2);
			canvas.drawPath(pathLow, lineP);
		}
		
		for (int i = 0; i < size; i++) {
			AqiDto dto = tempList.get(i);
			if (!TextUtils.isEmpty(dto.aqi)) {
				int aqiValue = Integer.valueOf(dto.aqi);

				//绘制曲线上每个时间点marker
				lineP.setColor(Color.WHITE);
				lineP.setStrokeWidth(CommonUtil.dip2px(mContext, 10));
				canvas.drawPoint(dto.x, dto.y, lineP);

				lineP.setStrokeWidth(CommonUtil.dip2px(mContext, 7));
				//绘制曲线上每个时间点的温度值
				for (int m = 0; m < yDivider.length-1; m++) {
					int val1 = Integer.valueOf(yDivider[m].trim());
					int val2 = Integer.valueOf(yDivider[m+1].trim());
					if (aqiValue >= val1 && aqiValue < val2) {
						roundP.setColor(colors[m]);
						lineP.setColor(colors[m]);
						break;
					}
				}
				canvas.drawPoint(dto.x, dto.y, lineP);

				if (aqiValue > maxTemp-10) {
					RectF rectF = new RectF(dto.x-CommonUtil.dip2px(mContext, 10f), dto.y+CommonUtil.dip2px(mContext, 8), dto.x+CommonUtil.dip2px(mContext, 10f), dto.y+CommonUtil.dip2px(mContext, 23));
					canvas.drawRoundRect(rectF, CommonUtil.dip2px(mContext, 5), CommonUtil.dip2px(mContext, 5), roundP);
					textP.setColor(getResources().getColor(R.color.black));
					textP.setTextSize(CommonUtil.dip2px(mContext, 10));
					float tempWidth = textP.measureText(dto.aqi);
					canvas.drawText(dto.aqi, dto.x-tempWidth/2, dto.y+CommonUtil.dip2px(mContext, 20f), textP);
				}else {
					RectF rectF = new RectF(dto.x-CommonUtil.dip2px(mContext, 10f), dto.y-CommonUtil.dip2px(mContext, 23), dto.x+CommonUtil.dip2px(mContext, 10f), dto.y-CommonUtil.dip2px(mContext, 8));
					canvas.drawRoundRect(rectF, CommonUtil.dip2px(mContext, 5), CommonUtil.dip2px(mContext, 5), roundP);
					textP.setColor(getResources().getColor(R.color.black));
					textP.setTextSize(CommonUtil.dip2px(mContext, 10));
					float tempWidth = textP.measureText(dto.aqi);
					canvas.drawText(dto.aqi, dto.x-tempWidth/2, dto.y-CommonUtil.dip2px(mContext, 12f), textP);
				}
			}

			//绘制每个时间点上的时间值
			textP.setColor(Color.WHITE);
			textP.setTextSize(CommonUtil.dip2px(mContext, 12));
			float timeWidth = textP.measureText(dto.date);
			canvas.drawText(dto.date, dto.x-timeWidth/2, h-bottomMargin+CommonUtil.dip2px(mContext, 13), textP);
			float hourWidth = textP.measureText("时");
			canvas.drawText("时", dto.x-hourWidth/2, h-bottomMargin+CommonUtil.dip2px(mContext, 26), textP);

		}
		
	}

	/**
	 * 寻找最近的有效值
	 * @param index
	 */
	private float findNear(int index) {
		float y = -1;
		for (int i = index; i < tempList.size(); i++) {//向右找
			AqiDto dto = tempList.get(i);
			if (dto.y == -1) {
				continue;
			}
			y = dto.y;
			break;
		}
		for (int i = index; i >= 0; i--) {//向左找
			AqiDto dto = tempList.get(i);
			if (dto.y == -1) {
				continue;
			}
			y = dto.y;
			break;
		}
		return y;
	}
	
}
