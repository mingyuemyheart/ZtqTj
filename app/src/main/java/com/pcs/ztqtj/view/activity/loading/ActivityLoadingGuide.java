package com.pcs.ztqtj.view.activity.loading;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.loading.CommandLoadingGuide;
import com.pcs.ztqtj.view.activity.FragmentActivityBase;
import com.pcs.lib.lib_pcs_v3.control.log.Log;

public class ActivityLoadingGuide extends FragmentActivityBase {
    @Override
    protected void onCreate(@Nullable Bundle arg0) {
        super.onCreate(arg0);
        Log.e("jzy", "引导页onCreate");
        setContentView(R.layout.activity_loading_guide);

        CommandLoadingGuide command = new CommandLoadingGuide(this);
        command.execute();
    }
}
