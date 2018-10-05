package com.kuding.models;

public class StatusResultModel {

	protected int status = 0;

	protected String message = "操作成功";

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "StatusResultModel [status=" + status + ", message=" + message + "]";
	}

}
