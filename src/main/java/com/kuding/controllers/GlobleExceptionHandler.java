package com.kuding.controllers;

import java.net.SocketTimeoutException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.kuding.enums.ResponseStatusEnum;
import com.kuding.exceptions.HaveReasonException;
import com.kuding.models.StatusResultModel;

public class GlobleExceptionHandler {

	protected final Log logger = LogFactory.getLog(getClass());

	@ExceptionHandler
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public StatusResultModel paramErro(ValidationException e) {
		logger.error("参数异常", e);
		return ResponseStatusEnum.PARAMERRO.createStatusModel();
	}

	@ExceptionHandler
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public StatusResultModel nullError(NullPointerException e, HttpServletRequest req) {
		logger.error("空指针异常", e);
		return ResponseStatusEnum.NULLERROR
				.createStatusModel(StringUtils.isNotBlank(e.getMessage()) ? e.getMessage() : "空指针错误");
	}

	@ExceptionHandler
	@ResponseStatus(value = HttpStatus.EXPECTATION_FAILED)
	public StatusResultModel reasonErro(HaveReasonException e) {
		logger.info("业务异常" + e.getMessage());
		return ResponseStatusEnum.REASONERRO.createStatusModel(e.getMessage());
	}

	@ExceptionHandler(value = { SocketTimeoutException.class, })
	@ResponseStatus(value = HttpStatus.REQUEST_TIMEOUT)
	public StatusResultModel timeoueErro(Exception e) {
		return ResponseStatusEnum.TIMEOUTERROR.createStatusModel("链接超时，请重试");
	}

	@ExceptionHandler(RuntimeException.class)
	@ResponseStatus(value = HttpStatus.I_AM_A_TEAPOT)
	public StatusResultModel uncaughtError(RuntimeException exception, HttpServletRequest request) {
		logger.error("未知错误", exception);
		return ResponseStatusEnum.FAIL.createStatusModel("系统出错");
	}
}
