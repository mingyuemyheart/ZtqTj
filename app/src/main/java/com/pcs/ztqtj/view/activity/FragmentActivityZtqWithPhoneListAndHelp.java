package com.pcs.ztqtj.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Contacts;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.view.activity.help.ActivityHelp;

/**
 * 右上角显示联系人和帮助的activity类
 * @author tya
 *
 */
public class FragmentActivityZtqWithPhoneListAndHelp extends FragmentActivityZtqBase{

	protected ImageButton btnPhoneList = null;
    protected ImageButton btnHelp = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		initEvent();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	private void initView() {
		btnPhoneList = (ImageButton) findViewById(R.id.phonelist);
        btnHelp = (ImageButton) findViewById(R.id.btn_right);
        btnHelp.setBackgroundResource(R.drawable.serve_help);
		if(btnPhoneList.getVisibility() != View.VISIBLE) {
			btnPhoneList.setVisibility(View.VISIBLE);
		}
        if(btnHelp.getVisibility() != View.VISIBLE) {
            btnHelp.setVisibility(View.VISIBLE);
        }
	}
	
	private void initEvent() {
		btnPhoneList.setOnClickListener(clickListener);
        btnHelp.setOnClickListener(clickListener);
	}
	
	/**
	 * 跳转联系人
	 */
	private void gotoPhoneList() {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		intent.setData(Contacts.People.CONTENT_URI);
		startActivity(intent);
	}

    /**
     * 跳转帮助
     */
    private void gotoHelp() {
        Intent intent = new Intent(this, ActivityHelp.class);
        startActivity(intent);
    }

    private OnClickListener clickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.phonelist:
                    gotoPhoneList();
                    break;
                case R.id.btn_right:
                    gotoHelp();
                    break;
            }
        }
    };
}
