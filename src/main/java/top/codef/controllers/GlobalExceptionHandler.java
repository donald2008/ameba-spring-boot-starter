package top.codef.controllers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import top.codef.enums.ResponseStatusEnum;
import top.codef.exceptions.HaveReasonException;
import top.codef.models.StatusResultModel;

@RestControllerAdvice
public class GlobalExceptionHandler {

	protected final Log logger = LogFactory.getLog(getClass());

	@ExceptionHandler
	@ResponseStatus(value = HttpStatus.EXPECTATION_FAILED)
	public StatusResultModel reasonErro(HaveReasonException e) {
		logger.info("业务异常" + e.getMessage(), e);
		return ResponseStatusEnum.REASONERRO.createStatusModel(e.getMessage());
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(value = HttpStatus.I_AM_A_TEAPOT)
	public StatusResultModel uncaughtError(Exception exception, HttpServletRequest request) {
		logger.error("未知错误", exception);
		return ResponseStatusEnum.FAIL.createStatusModel("系统出错");
	}
}
