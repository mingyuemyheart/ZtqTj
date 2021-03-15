package com.pcs.ztqtj.control.loading;

import android.app.Activity;

import com.pcs.ztqtj.control.command.AbstractCommand;
import com.pcs.ztqtj.control.tool.ZtqLocationTool;
import com.pcs.lib.lib_pcs_v3.control.log.Log;

/**
 *
 *
 * @author JiangZY
 */
public class CommandLoadingCity extends AbstractCommand {

    private Activity mActivity;

    public CommandLoadingCity(Activity activity) {
        mActivity = activity;
    }

    @Override
    public void execute() {
        super.execute();
        Log.e("jzy", "CommandLoadingCity");
        setStatus(Status.SUCC);
        ZtqLocationTool.getInstance().refreshLocationCity(true);
    }
}
