package com.pcs.ztqtj.view.activity.web;

import android.os.Bundle;
import android.view.View;

import com.pcs.ztqtj.view.activity.FragmentActivityZtqWithPhoneListAndHelp;

/**
 * 带帮助按钮的activity
 * Created by tyaathome on 2016/8/10.
 */
public class FragmentActivityZtqWithHelp extends FragmentActivityZtqWithPhoneListAndHelp {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.btnHelp.setVisibility(View.VISIBLE);
        super.btnPhoneList.setVisibility(View.GONE);
    }
}
