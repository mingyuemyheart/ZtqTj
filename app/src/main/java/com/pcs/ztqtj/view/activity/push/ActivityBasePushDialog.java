package com.pcs.ztqtj.view.activity.push;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.pcs.ztqtj.control.tool.CommUtils;
import com.pcs.ztqtj.control.tool.LocalDataHelper;

/**
 * Created by tyaathome on 2018/1/30.
 */

public class ActivityBasePushDialog extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(LocalDataHelper.getRingtone(this)) {
            CommUtils.ringtone(this);
        }
    }
}
