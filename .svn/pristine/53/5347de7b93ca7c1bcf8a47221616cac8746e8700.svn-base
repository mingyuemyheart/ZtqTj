package com.pcs.ztqtj.control.loading;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.pcs.ztqtj.control.ControlAppInit;
import com.pcs.ztqtj.control.command.AbstractCommand;

/**
 * JiangZy on 2016/6/21.
 */
public class CommandLoadingCheck extends AbstractCommand {

    private final long CHECK_INTERVAL = 200;
    private Context mAppContext;
    private boolean mIsInited = false;

    public CommandLoadingCheck(Context appContext) {
        mAppContext = appContext;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            if (ControlAppInit.getInstance().getStatus() == Status.SUCC) {
                setStatus(Status.SUCC);
            } else if (ControlAppInit.getInstance().getStatus() == Status.FAIL) {
                if (!mIsInited) {
                    ControlAppInit.getInstance().init(mAppContext);
                    mHandler.sendEmptyMessageDelayed(0, CHECK_INTERVAL);
                    mIsInited = true;
                } else {
                    setStatus(Status.FAIL);
                }
            } else {
                mHandler.sendEmptyMessageDelayed(0, CHECK_INTERVAL);
            }
        }
    };

    @Override
    public void execute() {
        super.execute();
        mHandler.sendEmptyMessageDelayed(0, CHECK_INTERVAL);
    }
}
