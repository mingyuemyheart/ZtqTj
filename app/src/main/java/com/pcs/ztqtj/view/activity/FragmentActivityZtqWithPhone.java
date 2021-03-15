package com.pcs.ztqtj.view.activity;

import android.os.Bundle;
import android.view.View;

/**
 * Created by tyaathome on 2016/8/9.
 */
public class FragmentActivityZtqWithPhone extends FragmentActivityZtqWithPhoneListAndHelp {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.btnHelp.setVisibility(View.GONE);
        super.btnPhoneList.setVisibility(View.VISIBLE);
    }
}
