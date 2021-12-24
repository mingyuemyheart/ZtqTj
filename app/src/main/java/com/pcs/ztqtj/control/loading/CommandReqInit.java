package com.pcs.ztqtj.control.loading;

import android.content.Context;

import com.pcs.ztqtj.control.command.AbstractCommand;

/**
 * 闪屏页面，请求初始化
 */
public class CommandReqInit extends AbstractCommand {

    public CommandReqInit(Context context) {
    }

    @Override
    public void execute() {
        super.execute();
        setStatus(Status.SUCC);
    }

}
