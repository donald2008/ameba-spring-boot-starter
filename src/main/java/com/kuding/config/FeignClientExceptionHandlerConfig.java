package com.kuding.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.gson.Gson;
import com.kuding.controllers.FeignClientExceptionHandler;
import com.kuding.feign.FeignErrorDecoder;

import feign.FeignException;
import feign.codec.ErrorDecoder;

@Configuration
@ConditionalOnProperty(name = "ameba.enable-error-advice", havingValue = "true")
@ConditionalOnWebApplication
@ConditionalOnClass(FeignException.class)
public class FeignClientExceptionHandlerConfig {

	@Autowired
	private Gson gson;

	@Bean
	@ConditionalOnMissingBean
	public FeignClientExceptionHandler feignClientExceptionHandler() {
		return new FeignClientExceptionHandler();
	}

	@Bean
	@ConditionalOnMissingBean
	public ErrorDecoder feignErrorDecoder() {
		return new FeignErrorDecoder(gson);
	}
}
