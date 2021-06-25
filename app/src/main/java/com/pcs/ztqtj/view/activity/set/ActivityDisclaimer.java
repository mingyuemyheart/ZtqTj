package com.pcs.ztqtj.view.activity.set;

import android.os.Bundle;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;
import com.pcs.lib_ztqfj_v2.model.pack.local.PackLocalCity;

/**
 * 设置-免责声明
 */
public class ActivityDisclaimer extends FragmentActivityZtqBase {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        setTitleText(bundle.getString("title"));
        setContentView(R.layout.activity_disclaimer);
    }

}
