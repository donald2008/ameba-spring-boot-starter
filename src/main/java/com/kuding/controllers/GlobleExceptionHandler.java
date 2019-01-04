package com.kuding.controllers;

import java.net.SocketTimeoutException;
import java.nio.file.AccessDeniedException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.kuding.enums.ResponseStatusEnum;
import com.kuding.exceptions.HaveReasonException;
import com.kuding.models.StatusResultModel;

public class GlobleExceptionHandler {

	@ExceptionHandler
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public StatusResultModel paramErro(ValidationException e) {
		return ResponseStatusEnum.PARAMERRO.createStatusModel();
	}

	@ExceptionHandler
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public StatusResultModel sqlError(HibernateException e) {
		e.printStackTrace();
		return ResponseStatusEnum.SQLERROR.createStatusModel();
	}

	@ExceptionHandler
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public StatusResultModel nullError(NullPointerException e, HttpServletRequest req) {
		return ResponseStatusEnum.NULLERROR
				.createStatusModel(StringUtils.isNotBlank(e.getMessage()) ? e.getMessage() : "空指针错误");
	}

	@ExceptionHandler
	@ResponseStatus(value = HttpStatus.EXPECTATION_FAILED)
	public StatusResultModel reasonErro(HaveReasonException e) {
		return ResponseStatusEnum.REASONERRO.createStatusModel(e.getMessage());
	}

	@ExceptionHandler
	@ResponseStatus(value = HttpStatus.EXPECTATION_FAILED)
	public StatusResultModel accessDenia(AccessDeniedException e) {
		e.printStackTrace();
		return ResponseStatusEnum.USERVALIDATEERRO.createStatusModel();
	}

	@ExceptionHandler(value = { SocketTimeoutException.class })
	@ResponseStatus(value = HttpStatus.REQUEST_TIMEOUT)
	public StatusResultModel timeoueErro(Exception e) {
		return ResponseStatusEnum.TIMEOUTERROR.createStatusModel("链接超时，请重试");
	}

	@ExceptionHandler(RuntimeException.class)
	@ResponseStatus(value = HttpStatus.I_AM_A_TEAPOT)
	public StatusResultModel uncaughtError(RuntimeException exception, HttpServletRequest request) {
		return ResponseStatusEnum.FAIL.createStatusModel("系统出错");
	}
}
