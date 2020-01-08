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
	public void setStatus(String status) {
		this.status = status;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	String status;
	String msg;
	double spendTime;
	public double getSpendTime() {
		return spendTime;
	}
	public void setSpendTime(double d) {
		this.spendTime = d;
	}
}
