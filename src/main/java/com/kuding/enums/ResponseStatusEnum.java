package com.kuding.enums;

import com.kuding.models.ResultModel;
import com.kuding.models.StatusResultModel;

public enum ResponseStatusEnum {

	SUCCESS(0, "操作成功", "操作成功，默认返回操作"),
	FAIL(-1, "操作失败", "操作失败"),
	PARAMERRO(1, "参数错误,请检查参数是否正确", "参数验证模块处理"),
	SQLERROR(2,"数据存取错误", "hibernate错误"),
	NULLERROR(2, "空指针错误", "空指针异常"),
	USERVALIDATEERRO(302, "用户验证异常，请重新登录", ""),
	REASONERRO(10, "操作起因问题，请联系管理员15129072758", "此方式可以重写message"),
	TIMEOUTERROR(408 , "操作超时" , "");

	private final int status;
	private final String message;
	private final String explain;

	private ResponseStatusEnum(int status, String message, String explain) {
		this.status = status;
		this.message = message;
		this.explain = explain;
	}

	public StatusResultModel createStatusModel() {
		StatusResultModel model = new StatusResultModel();
		model.setMessage(message);
		model.setStatus(status);
		return model;
	}

	public StatusResultModel createStatusModel(String message) {
		StatusResultModel model = new StatusResultModel();
		model.setMessage(message);
		model.setStatus(this.status);
		return model;
	}

	public <T> ResultModel<T> createResultModel(T result) {
		ResultModel<T> re = new ResultModel<>();
		re.setMessage(message);
		re.setStatus(status);
		re.setResult(result);
		return re;
	}

	public int getStatus() {
		return status;
	}

	public String getMessage() {
		return message;
	}

	public String getExplain() {
		return explain;
	}

}
