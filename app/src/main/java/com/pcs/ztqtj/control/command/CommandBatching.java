package com.pcs.ztqtj.control.command;

import android.os.Handler;

import com.pcs.lib.lib_pcs_v3.control.log.Log;
import com.pcs.ztqtj.control.main_weather.CommandMainBase;

import java.util.ArrayList;
import java.util.List;


/**
 * 命令批量处理
 *
 * @author JiangZY
 */
public class CommandBatching extends AbstractCommand {
    /**
     * 执行检查间隔
     */
    private final long CHECK_INTERVAL = 200;
    /**
     * 命令列表
     */
    private List<InterCommand> mListCommand = new ArrayList<InterCommand>();

    /**
     * 是否队列执行
     */
    private boolean mIsQueueExecute = false;

    /**
     * 开始时间
     */
    private long mStartTime = -1;

    /**
     * 超时时间(默认30秒，-1无超时)
     */
    private long mOverTime = 30 * 1000;

    /**
     * 最小时间
     */
    private long mMinTime = 0;

    /**
     * 处理顺序执行
     */
    private Handler mSeqHandler = new Handler() {
        public void dispatchMessage(android.os.Message msg) {
            super.dispatchMessage(msg);
            int index = msg.what;
            checkExecOne(index);
        }
    };

    /**
     * 处理执行所有
     */
    private Handler mAllHandler = new Handler() {
        public void dispatchMessage(android.os.Message msg) {
            super.dispatchMessage(msg);
            checkAll();
        }
    };

    /**
     * 检查执行一条命令
     *
     * @param index
     */
    private void checkExecOne(int index) {
        InterCommand command = mListCommand.get(index);
        long currTime = System.currentTimeMillis();
        if (command.getStatus() == Status.CREATE) {
            // 刚创建则执行
            command.execute();
            // 检查
            checkExecOne(index);
        } else if (command.getStatus() == Status.SUCC) {
            // 成功
            if (index < mListCommand.size() - 1) {
                // 执行下一个
                checkExecOne(index + 1);
            } else {
                if (currTime - mStartTime > mMinTime) {
                    // 提示成功
                    setStatus(Status.SUCC);
                } else {
                    // 延迟检查
                    mSeqHandler.sendEmptyMessageDelayed(index, CHECK_INTERVAL);
                }
            }
        } else if (command.getStatus() == Status.RUNNING) {
            // 运行中
            if (mOverTime > 0 && mStartTime > 0
                    && mStartTime + mOverTime < currTime) {
                // 超时
                setStatus(Status.FAIL);
                return;
            }
            // 延迟检查
            mSeqHandler.sendEmptyMessageDelayed(index, CHECK_INTERVAL);
        } else if (command.getStatus() == Status.FAIL) {
            // 失败
            setStatus(Status.FAIL);
        }
    }

    /**
     * 执行所有
     */
    private void executeAll() {
        for (int i = 0; i < mListCommand.size(); i++) {
            InterCommand command = mListCommand.get(i);
            command.execute();
        }
        checkAll();
    }

    private void checkAll() {
        Status tempStatus = Status.SUCC;
        for (int i = 0; i < mListCommand.size(); i++) {
            InterCommand command = mListCommand.get(i);
            if (command.getStatus() == Status.FAIL) {
                tempStatus = Status.FAIL;
                notifyAllListener();
                break;
            } else if (command.getStatus() == Status.RUNNING) {
                tempStatus = Status.RUNNING;
                break;
            }
        }

        long currTime = System.currentTimeMillis();
        if (tempStatus == Status.SUCC) {
            if (currTime - mStartTime > mMinTime) {
                // 提示成功
                setStatus(Status.SUCC);
                notifyAllListener();
            } else {
                // 延迟检查
                mAllHandler.sendEmptyMessageDelayed(0, CHECK_INTERVAL);
            }
        } else if (tempStatus == Status.RUNNING) {
            // 运行中
            if (mOverTime > 0 && mStartTime > 0
                    && mStartTime + mOverTime < currTime) {
                // 超时
                setStatus(Status.FAIL);
                notifyAllListener();
            } else {
                // 延迟检查
                mAllHandler.sendEmptyMessageDelayed(0, CHECK_INTERVAL);
            }
        }
    }

    /**
     * 添加命令
     *
     * @param command
     */
    public void addCommand(InterCommand command) {
        if (command == null) {
            throw new RuntimeException(Log.getClassMethodName()
                    + "command为null");
        }

        mListCommand.add(command);
    }

    @Override
    public void execute() {
        super.execute();
        if (mListCommand.size() == 0) {
            setStatus(Status.SUCC);
            return;
        }
        mStartTime = System.currentTimeMillis();
        // 重置状态
        for (int i = 0; i < mListCommand.size(); i++) {
            mListCommand.get(i).setStatus(Status.CREATE);
        }

        if (mIsQueueExecute) {
            // 一条条执行
            checkExecOne(0);
        } else {
            // 执行所有
            executeAll();
        }

    }

    public void checkPermission(String[] permissions, int[] grantResults) {
        for(InterCommand command : mListCommand) {
            if(command instanceof CommandMainBase) {
                CommandMainBase main = (CommandMainBase) command;
                main.checkPermission(permissions, grantResults);
            }
        }
    }

    /**
     * 设置是否队列执行
     */
    public void setQueueExecute(boolean isQueueExecute) {
        mIsQueueExecute = isQueueExecute;
    }

    public void setOverTime(long time) {
        mOverTime = time;
    }

    /**
     * 设置最小时间
     *
     * @param time
     */
    public void setMinTime(long time) {
        mMinTime = time;
    }
}
