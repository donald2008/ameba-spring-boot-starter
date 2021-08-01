package com.kuding.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kuding.controllers.FeignClientExceptionHandler;
import com.kuding.feign.FeignErrorDecoder;

import feign.FeignException;
import feign.codec.ErrorDecoder;

@Configuration
@ConditionalOnProperty(name = "ameba.enable-error-advice", havingValue = "true")
@ConditionalOnWebApplication(type = Type.SERVLET)
@ConditionalOnClass(FeignException.class)
public class FeignClientExceptionHandlerConfig {

	@Autowired
	private ObjectMapper objectMapper;

	@Bean
	@ConditionalOnMissingBean
	public FeignClientExceptionHandler feignClientExceptionHandler() {
		return new FeignClientExceptionHandler();
	}

	@Bean
	@ConditionalOnMissingBean
	public ErrorDecoder feignErrorDecoder() {
		return new FeignErrorDecoder(objectMapper);
	}
}
