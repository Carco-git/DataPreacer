package top.preacer.pojo;

import java.io.Serializable;

public class Result implements Serializable{
	public static final String SUCCESS ="000";
	public static final String FAILED ="001";
	/**
	 * 
	 */
	private static final long serialVersionUID = 1538626553429097044L;
	public String getStatus() {
		return status;
	}
	public Result setStatus(String status) {
		this.status = status;
		return this;
	}
	public String getMsg() {
		return msg;
	}
	public Result setMsg(String msg) {
		this.msg = msg;
		return this;
	}
	String status;
	String msg;
	double spendTime;
	public double getSpendTime() {
		return spendTime;
	}
	public Result setSpendTime(double d) {
		this.spendTime = d;
		return this;
	}
	public Result lackOfAuthority() {
		this.setMsg("权限不足");
		this.setStatus(Result.FAILED);
		return this;
	}
	public Result wellDone() {
		this.setMsg("OK");
		this.setStatus(Result.SUCCESS);
		return this;
	}
}
