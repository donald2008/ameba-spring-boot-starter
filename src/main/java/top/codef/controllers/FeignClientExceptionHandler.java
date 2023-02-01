package top.codef.controllers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import feign.FeignException;
import top.codef.enums.ResponseStatusEnum;
import top.codef.models.StatusResultModel;

@RestControllerAdvice
public class FeignClientExceptionHandler {

	private final Log logger = LogFactory.getLog(FeignClientExceptionHandler.class);

	@ExceptionHandler({ FeignException.class })
	@ResponseStatus(HttpStatus.FAILED_DEPENDENCY)
	public StatusResultModel feignClientException(FeignException e) {
		logger.error("feign调用错误", e);
		return ResponseStatusEnum.FAIL.createStatusModel("服务出错!");
	}
}