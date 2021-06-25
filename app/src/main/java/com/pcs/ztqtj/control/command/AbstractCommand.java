package com.pcs.ztqtj.control.command;

import com.pcs.lib.lib_pcs_v3.control.log.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 命令接口
 */
public abstract class AbstractCommand implements InterCommand {

    private Status mStatus = Status.CREATE;
    private List<InterCommandListener> mListListener = new ArrayList<>();

    /**
     * 添加监听
     * @param listener
     */
    public void addListener(InterCommandListener listener) {
        if (listener == null) {
            throw new RuntimeException(Log.getClassMethodName() + "监听器为null");
        }
        mListListener.add(listener);
    }

    /**
     * 删除监听
     * @param listener
     */
    public void removeListener(InterCommandListener listener) {
        if (listener == null) {
            throw new RuntimeException(Log.getClassMethodName() + "监听器为null");
        }
        mListListener.remove(listener);
    }

    /**
     * 通知所有监听
     */
    protected void notifyAllListener() {
        Iterator<InterCommandListener> it = mListListener.iterator();
        while (it.hasNext()) {
            InterCommandListener listener = it.next();
            // 删除null
            if (listener == null) {
                mListListener.remove(listener);
                continue;
            }
            try {
                listener.done(mStatus);
            } catch (Exception ex) {
                mListListener.remove(listener);
                continue;
            }
        }
    }

    /**
     * 执行
     */
    @Override
    public void execute() {
        mStatus = Status.RUNNING;
    }

    @Override
    public void setStatus(Status status) {
        mStatus = status;
        notifyAllListener();
    }

    @Override
    public Status getStatus() {
        return mStatus;
    }
}
