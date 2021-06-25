package com.pcs.ztqtj.control.main_weather;

import com.pcs.ztqtj.control.command.AbstractCommand;

/**
 * 首页-base
 */
public abstract class CommandMainBase extends AbstractCommand {

    private boolean isFirstRun = true;

    @Override
    public void execute() {
        super.execute();
        if (isFirstRun) {
            init();
            isFirstRun = false;
        } else {
            refresh();
        }
        setStatus(Status.SUCC);
    }

    /**
     * 初始化
     */
    protected abstract void init();

    /**
     * 刷新
     */
    protected abstract void refresh();

    public void checkPermission(String[] permissions, int[] grantResults) {

    }
}
