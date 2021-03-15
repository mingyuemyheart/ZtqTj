package com.pcs.ztqtj.control.command;

public interface InterCommand {
	/**
	 * 状态
	 *
	 * @author JiangZY
	 *
	 */
	public enum Status {
		CREATE, RUNNING, FAIL, SUCC
	}

	/**
	 * 命令执行监听
	 *
	 * @author JiangZY
	 *
	 */
	public interface InterCommandListener {
		public void done(Status status);
	}

	/**
	 * 执行
	 */
	public void execute();

	/**
	 * 设置状态
	 * @param status
	 */
	public void setStatus(Status status);

	/**
	 * 获取状态
	 *
	 * @return
	 */
	public Status getStatus();
}
