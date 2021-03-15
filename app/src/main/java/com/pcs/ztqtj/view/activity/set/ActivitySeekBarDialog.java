package com.pcs.ztqtj.view.activity.set;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.tool.CommUtils;
import com.pcs.ztqtj.view.myview.MySeekBarThumb;

/**
 * seekbar弹窗
 * 
 * @author chenjh
 */
public class ActivitySeekBarDialog extends Activity {

	private Button cancelBtn;
	private Button submitBtn;
	private MySeekBarThumb mySeekBar;
	private TextView tvTitle;
	private TextView tvWeight;
	private TextView tvMinValue;
	private TextView tvMaxValue;
	private int leftmargin;
	private boolean hasMeasured = false;// 只计算一次
	private int textLeftPosition = 0; // 进度数值的距离左边的宽度，需计算
	private String type;//
	private String title;//
	private String unit;
	private String mValue; 
	private int minValue; 
	private int maxValue;
	private boolean isNegative;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_seekbar_ayout);

		ActivitySeekBarDialog.this.setFinishOnTouchOutside(false);

		initView();
		initData();
	}

	public void initView() {
		cancelBtn = (Button) findViewById(R.id.cancel_btn);
		submitBtn = (Button) findViewById(R.id.submit_btn);
		
		mySeekBar = (MySeekBarThumb) findViewById(R.id.mySeekBar);
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvWeight = (TextView) findViewById(R.id.tvWeight);
		tvMinValue = (TextView) findViewById(R.id.tvMinValue);
		tvMaxValue = (TextView) findViewById(R.id.tvMaxValue);

		mySeekBar.setThumb(getResources().getDrawable(R.drawable.common_seekbar_thumb));
		submitBtn.setOnClickListener(new MClick());
		cancelBtn.setOnClickListener(new MClick());
		mySeekBar.setOnSeekBarChangeListener(seekbarChangeListener);
	}

	public void initData() {
		try {
			type = getIntent().getStringExtra("type");//类型
			title = getIntent().getStringExtra("title");//标题
			unit = getIntent().getStringExtra("unit");//单位
			mValue = getIntent().getStringExtra("mValue");//默认值
			minValue = getIntent().getIntExtra("minValue", 0);//最小值
			maxValue = getIntent().getIntExtra("maxValue", 0);//最大值
			isNegative = getIntent().getBooleanExtra("isNegative", false);//是否负数

			tvTitle.setText(title + mValue + unit);
			int max = maxValue - minValue;
			int value = Integer.valueOf(mValue);
			if(isNegative){
				value = value + maxValue;
			}
			if("visibility".equals(type)){
				max=max/100;
				value =value/100;
			}
			mySeekBar.setMax(max);
			mySeekBar.setProgress(value);
			tvMinValue.setText(minValue + "");
			tvMaxValue.setText(maxValue + "");
			tvWeight.setText(mValue);
//			getLeftPosition();//设置seekBar上跟随thumb移动的数值textview初始位置
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	}
	
	private OnSeekBarChangeListener seekbarChangeListener = new OnSeekBarChangeListener() {

		// 停止拖动时执行

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {

			// System.out.println("停止拖动了！");

		}

		// 在进度开始改变时执行

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {

			// System.out.println("进度开始改变");

		}

		// 当进度发生改变时执行

		@Override
		public void onProgressChanged(SeekBar seekBar, int mProgress,

		boolean fromUser) {

			// System.out.println("正在进行拖动操作，还没有停下来一直再拖动");

			int pro = seekBar.getProgress();
			
			if(isNegative){
				pro = pro - maxValue;
			}
			if("visibility".equals(type)){
				pro=pro*100;
			}

			RelativeLayout.LayoutParams paramsStrength = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.WRAP_CONTENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT);

			leftmargin = ((MySeekBarThumb) seekBar).getSeekBarThumb()
					.getBounds().centerX()
					- (tvWeight.getWidth() / 2) + CommUtils.Dip2Px(getApplicationContext(), 10);
			if (leftmargin < 0)
				leftmargin = 0;

			paramsStrength.leftMargin =  leftmargin+textLeftPosition;

			tvWeight.setLayoutParams(paramsStrength);
			tvWeight.setText(pro + "");
			tvTitle.setText(title + pro + unit);
		}

	};
	
	/**
	 * 设置seekBar上跟随thumb移动的数值textview初始位置
	 */
	private void getLeftPosition() {
		ViewTreeObserver vto = mySeekBar.getViewTreeObserver();
		vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
			public boolean onPreDraw() {
				if (!hasMeasured) {
					textLeftPosition = mySeekBar.getLeft();
					// 获取到宽度和高度后，可用于计算

					RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
							RelativeLayout.LayoutParams.WRAP_CONTENT,
							RelativeLayout.LayoutParams.WRAP_CONTENT);
					leftmargin = ((MySeekBarThumb) mySeekBar).getSeekBarThumb()
							.getBounds().centerX()
							- (tvWeight.getWidth()/ 2) + CommUtils.Dip2Px(getApplicationContext(), 10);
					params.leftMargin = leftmargin + textLeftPosition;
					tvWeight.setLayoutParams(params);
					hasMeasured = true;// 只计算一次
				}
				return true;
			}
		});
	}
	
	@Override
	public void onBackPressed() {
		finish();
	}

	private class MClick implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.submit_btn:
				Intent intent = new Intent();
				intent.putExtra("type", type);
				intent.putExtra("value", tvWeight.getText().toString());
				intent.putExtra("flag", true);
				
				setResult(RESULT_OK, intent);
				finish();
				break;
			case R.id.cancel_btn:
				finish();
				break;
			default:
				break;
			}
		}
	}
}
