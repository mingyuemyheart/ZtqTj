package com.pcs.ztqtj.view.activity.agricuture;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.util.CommonUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * 温度、湿度曲线
 */
public class AgriDetailChartView extends View {

	private Context mContext;
	private ArrayList<AgriDto> dataList = new ArrayList<>();
	private float maxValue = 100, minValue = 0;//最大、最小湿度
	private float maxTemp = 0, minTemp = 0;//最高温、最低温
	private Paint lineP,textP;
	private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
	private SimpleDateFormat sdf2 = new SimpleDateFormat("HH", Locale.CHINA);

	public AgriDetailChartView(Context context) {
		super(context);
		mContext = context;
		init();
	}

	public AgriDetailChartView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}

	public AgriDetailChartView(Context context, AttributeSet attrs, int defStyleAttr) {
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
	}
	
	/**
	 * 对cubicView进行赋值
	 */
	public void setData(ArrayList<AgriDto> dataList) {
		if (!dataList.isEmpty()) {
			this.dataList.addAll(dataList);

			//获取最高温度、最低温度
			for (int i = 0; i < dataList.size(); i++) {
				AgriDto dto = dataList.get(i);
				if (!TextUtils.isEmpty(dto.ttt150cm)) {
					float temp = Float.valueOf(dto.ttt150cm);
					if (i == 0) {
						maxTemp = temp;
						minTemp = temp;
					}
					if (maxTemp < temp) {
						maxTemp = temp;
					}
					if (minTemp > temp) {
						minTemp = temp;
					}
				}
			}
			Log.e("maxTempminTemp", maxTemp+"---"+minTemp);
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (dataList.isEmpty()) {
			return;
		}
		
		canvas.drawColor(Color.TRANSPARENT);
		float w = canvas.getWidth();
		float h = canvas.getHeight();
		float chartW = w-CommonUtil.dip2px(mContext, 60);
		float chartH = h-CommonUtil.dip2px(mContext, 160);
		float leftMargin = CommonUtil.dip2px(mContext, 40);
		float rightMargin = CommonUtil.dip2px(mContext, 20);
		float topMargin = CommonUtil.dip2px(mContext, 140);
		float tempTopMargin = CommonUtil.dip2px(mContext, 20);
		float bottomMargin = CommonUtil.dip2px(mContext, 20);
		float chartMaxH = chartH * maxValue / (Math.abs(maxValue)+Math.abs(minValue));//同时存在正负值时，正值高度
		
		int size = dataList.size();
		float columnWidth = chartW/(size-1);
		//获取曲线上每个温度点的坐标
		for (int i = 0; i < size; i++) {
			AgriDto dto = dataList.get(i);
			dto.x = columnWidth*i+leftMargin;
			dto.y = 0;
			if (!TextUtils.isEmpty(dto.rh150cm)) {
				float humidity = Float.valueOf(dto.rh150cm);
				if (humidity >= 0) {
					dto.y = chartMaxH - chartH*Math.abs(humidity)/(Math.abs(maxValue)+Math.abs(minValue)) + topMargin;
				}else {
					dto.y = chartMaxH + chartH*Math.abs(humidity)/(Math.abs(maxValue)+Math.abs(minValue)) + topMargin;
				}
			}

			dto.xTemp = columnWidth*i+leftMargin;
			dto.yTemp = tempTopMargin;
			if (!TextUtils.isEmpty(dto.ttt150cm)) {
				float temp = Float.valueOf(dto.ttt150cm);
				if (temp >= 0) {
					dto.yTemp = chartMaxH - chartH*Math.abs(temp)/(Math.abs(maxTemp)+Math.abs(minTemp)) + tempTopMargin;
				}else {
					dto.yTemp = chartMaxH + chartH*Math.abs(temp)/(Math.abs(maxTemp)+Math.abs(minTemp)) + tempTopMargin;
				}
			}

			dataList.set(i, dto);
		}

//		//绘制区域
//		for (int i = 0; i < size-1; i++) {
//			AgriDto dto = dataList.get(i);
//			Path rectPath = new Path();
//			rectPath.moveTo(dto.x, topMargin);
//			rectPath.lineTo(dto.x+columnWidth, topMargin);
//			rectPath.lineTo(dto.x+columnWidth, h-bottomMargin);
//			rectPath.lineTo(dto.x, h-bottomMargin);
//			rectPath.close();
//			if (i == 0 || i == 1 || i == 2 || i == 3 || i == 8 || i == 9 || i == 10 || i == 11
//					 || i == 16 || i == 17 || i == 18 || i == 19) {
//				lineP.setColor(Color.WHITE);
//			}else {
//				lineP.setColor(0xfff9f9f9);
//			}
//			lineP.setStyle(Style.FILL);
//			canvas.drawPath(rectPath, lineP);
//		}

//		//绘制分割线
//		for (int i = 0; i < size; i++) {
//			AgriDto dto = dataList.get(i);
//			Path linePath = new Path();
//			linePath.moveTo(dto.x, topMargin);
//			linePath.lineTo(dto.x, h-bottomMargin);
//			linePath.close();
//			lineP.setColor(0xfff1f1f1);
//			lineP.setStyle(Style.STROKE);
//			canvas.drawPath(linePath, lineP);
//		}
		
//		//绘制刻度线，每间隔为20
//		for (int i = (int) minTemp; i <= maxTemp; i+=100) {
//			float dividerY = chartMaxH - chartH*Math.abs(i)/(Math.abs(maxTemp)+Math.abs(minTemp)) + tempTopMargin;
//			lineP.setColor(0xfff1f1f1);
//			canvas.drawLine(leftMargin, dividerY, w-rightMargin, dividerY, lineP);
//			textP.setColor(0xff999999);
//			textP.setTextSize(CommonUtil.dip2px(mContext, 10));
//			canvas.drawText(String.valueOf(i), 0, dividerY, textP);
//		}

		//绘制曲线
		for (int i = 0; i < size-1; i++) {
			float x1 = dataList.get(i).xTemp;
			float y1 = dataList.get(i).yTemp;
			float x2 = dataList.get(i+1).xTemp;
			float y2 = dataList.get(i+1).yTemp;

			float wt = (x1 + x2) / 2;

			float x3 = wt;
			float y3 = y1;
			float x4 = wt;
			float y4 = y2;

			if (y2 != 0 && y3 != 0 && y4 != 0) {
				Path linePath = new Path();
				linePath.moveTo(x1, y1);
				linePath.lineTo(x2, y2);
//				linePath.cubicTo(x3, y3, x4, y4, x2, y2);
				lineP.setColor(getResources().getColor(R.color.bg_title));
				lineP.setStyle(Style.STROKE);
				lineP.setStrokeWidth(CommonUtil.dip2px(mContext, 2));
				canvas.drawPath(linePath, lineP);
			}
		}

//		//绘制刻度线，每间隔为20
//		int itemDivider = 20;
//		for (int i = (int) minValue; i <= maxValue; i+=itemDivider) {
//			float dividerY = chartMaxH - chartH*Math.abs(i)/(Math.abs(maxValue)+Math.abs(minValue)) + topMargin;
//			lineP.setColor(0xfff1f1f1);
//			canvas.drawLine(leftMargin, dividerY, w-rightMargin, dividerY, lineP);
//			textP.setColor(0xff999999);
//			textP.setTextSize(CommonUtil.dip2px(mContext, 10));
//			canvas.drawText(String.valueOf(i), 0, dividerY, textP);
//		}

		textP.setColor(0xff999999);
		textP.setTextSize(CommonUtil.dip2px(mContext, 10));
		canvas.drawText("湿度%", 0, h-bottomMargin, textP);
		
		for (int i = 0; i < size; i++) {
			AgriDto dto = dataList.get(i);

			//绘制柱形图
			float half = columnWidth/2-CommonUtil.dip2px(mContext, 2f);
			Path rectPath = new Path();
			rectPath.moveTo(dto.x-half, dto.y);
			rectPath.lineTo(dto.x+half, dto.y);
			rectPath.lineTo(dto.x+half, h-bottomMargin);
			rectPath.lineTo(dto.x-half, h-bottomMargin);
			rectPath.close();
			lineP.setColor(getResources().getColor(R.color.bg_title));
			lineP.setStyle(Style.FILL);
			canvas.drawPath(rectPath, lineP);

			//绘制柱形图每个点的数据值
			if (!TextUtils.isEmpty(dto.rh150cm)) {
				textP.setColor(Color.BLACK);
				textP.setTextSize(CommonUtil.dip2px(mContext, 10));
				float tempWidth = textP.measureText(dto.rh150cm);
				if (Float.valueOf(dto.rh150cm) < 80) {
					textP.setColor(Color.BLACK);
					canvas.drawText(dto.rh150cm, dto.x-tempWidth/2, dto.y-CommonUtil.dip2px(mContext, 5f), textP);
				}else {
					textP.setColor(Color.WHITE);
					canvas.drawText(dto.rh150cm, dto.x-tempWidth/2, dto.y+CommonUtil.dip2px(mContext, 12f), textP);
				}
			}

			//绘制曲线图每个点的数据值
			if (!TextUtils.isEmpty(dto.ttt150cm)) {
				textP.setColor(Color.BLACK);
				textP.setTextSize(CommonUtil.dip2px(mContext, 10));
				float tempWidth = textP.measureText(dto.ttt150cm);
				canvas.drawText(dto.ttt150cm, dto.xTemp-tempWidth/2, dto.yTemp-CommonUtil.dip2px(mContext, 5f), textP);
			}
			
			//绘制24小时
			try {
				if (!TextUtils.isEmpty(dto.datatime)) {
					String time = sdf2.format(sdf1.parse(dto.datatime));
					if (!TextUtils.isEmpty(time)) {
						textP.setColor(0xff999999);
						textP.setTextSize(CommonUtil.dip2px(mContext, 10));
						float text = textP.measureText(time);
						canvas.drawText(time, dto.x-text/2, h-CommonUtil.dip2px(mContext, 5f), textP);
					}
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		lineP.reset();
		textP.reset();
	}
	
}
