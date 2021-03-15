package com.pcs.ztqtj.view.activity.product.typhoon;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;

/**
 * 图例
 * 
 * @author chenjh
 * 
 */
public class ActivityTyphoonExample extends FragmentActivityZtqBase implements OnClickListener{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_typooninfo);
		setTitleText("图例说明");
		initEvent();
	} 

	/** 初始化监听 **/
	private void initEvent(){
		findViewById(R.id.btn_back).setOnClickListener(this);
	}

	@Override
	public void onBackPressed() {
		setResult(RESULT_OK);
		finish();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back:
			setResult(RESULT_OK);
			finish();
			break;

		default:
			break;
		}
		
	}

}
