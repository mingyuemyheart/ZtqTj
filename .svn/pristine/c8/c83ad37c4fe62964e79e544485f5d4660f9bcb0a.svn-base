package com.pcs.ztqtj.control.loading;

import android.os.Handler;
import android.os.Message;

import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.control.command.AbstractCommand;

/**
 * JiangZy on 2016/6/21.
 */
public class CommandLoadingDelay extends AbstractCommand {

    private Handler mHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            setStatus(Status.SUCC);
        }
    };

    @Override
    public void execute() {
        super.execute();
        mHandler.sendEmptyMessageDelayed(0, MyApplication.START_INTERVAL + 100);
    }
}
