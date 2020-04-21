package com.kuding.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.kuding.controllers.FeignClientExceptionHandler;

import feign.FeignException;

@Configuration
@ConditionalOnProperty(name = "ameba.enable-error-advice", havingValue = "true")
@ConditionalOnWebApplication
@ConditionalOnClass(FeignException.class)
public class FeignClientExceptionHandlerConfig {

	@Bean
	@ConditionalOnMissingBean
	public FeignClientExceptionHandler feignClientExceptionHandler() {
		return new FeignClientExceptionHandler();
	}
}
