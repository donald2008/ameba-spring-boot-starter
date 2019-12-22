package com.kuding.controllers;

import javax.persistence.PersistenceException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.kuding.enums.ResponseStatusEnum;
import com.kuding.models.StatusResultModel;

public class HibernateGlobalException {

	@ExceptionHandler({ PersistenceException.class })
	@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
	public StatusResultModel jpaException() {
		return ResponseStatusEnum.SQLERROR.createStatusModel("数据持久化异常");
	}
}
